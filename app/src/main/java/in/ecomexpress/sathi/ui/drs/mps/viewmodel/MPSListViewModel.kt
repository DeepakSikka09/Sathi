package `in`.ecomexpress.sathi.ui.drs.mps.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.ecomexpress.sathi.R
import `in`.ecomexpress.sathi.repo.IDataManager
import `in`.ecomexpress.sathi.repo.local.data.rvp.RvpCommit
import `in`.ecomexpress.sathi.repo.local.data.rvp.RvpCommit.QcWizard
import `in`.ecomexpress.sathi.repo.local.db.model.ImageModel
import `in`.ecomexpress.sathi.repo.local.db.model.RVPMPSWithQC
import `in`.ecomexpress.sathi.repo.remote.model.image.ImageUploadResponse
import `in`.ecomexpress.sathi.repo.remote.model.masterdata.SampleQuestion
import `in`.ecomexpress.sathi.repo.remote.model.mps.QcItem
import `in`.ecomexpress.sathi.repo.remote.model.mps.RvpMpsQualityCheck
import `in`.ecomexpress.sathi.ui.base.BaseViewModel
import `in`.ecomexpress.sathi.ui.drs.rvp.navigator.RvpQcNavigator
import `in`.ecomexpress.sathi.utils.CommonUtils.getHeadStarredString
import `in`.ecomexpress.sathi.utils.CommonUtils.getTrailStarredString
import `in`.ecomexpress.sathi.utils.CommonUtils.imeiFirstLastFourVisible
import `in`.ecomexpress.sathi.utils.CommonUtils.imeiFullStars
import `in`.ecomexpress.sathi.utils.CommonUtils.imeiHeadStars
import `in`.ecomexpress.sathi.utils.CommonUtils.imeiTailStars
import `in`.ecomexpress.sathi.utils.CryptoUtils
import `in`.ecomexpress.sathi.utils.GlobalConstant
import `in`.ecomexpress.sathi.utils.rx.ISchedulerProvider
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.Calendar
import java.util.Collections
import javax.inject.Inject


@HiltViewModel
class MPSListViewModel @Inject constructor(
    dataManager: IDataManager,
    schedulerProvider: ISchedulerProvider,
    application: Application
) : BaseViewModel<RvpQcNavigator>(dataManager, schedulerProvider, application) {

    private val _qcImageUrls = MutableLiveData<List<List<String>>>()
    val qcImageUrls: LiveData<List<List<String>>> get() = _qcImageUrls
    private val showErrorMessage = MutableLiveData<Pair<String, Boolean>>()
    val showErrorMessageLiveData: LiveData<Pair<String, Boolean>> get() = showErrorMessage
    private val _rvpWithQC = MutableLiveData<RVPMPSWithQC>()
    val rvpWithQC: LiveData<RVPMPSWithQC> get() = _rvpWithQC
    private val _sampleQuestions = MutableLiveData<ArrayList<SampleQuestion>>()
    val sampleQuestions: LiveData<ArrayList<SampleQuestion>> get() = _sampleQuestions
    val getImageSaveStatus: LiveData<Boolean> get() = imageSaveStatus
    private val imageSaveStatus = MutableLiveData<Boolean>()
    private val isImageCapture = MutableLiveData<Boolean>()
    val imageCaptureStatus: LiveData<Boolean> get() = isImageCapture
    private val setNextScreenStatus = MutableLiveData<Int>()
    val nextScreenStatus: LiveData<Int> get() = setNextScreenStatus

    private lateinit var dialog: Dialog
    var qcWizards = ArrayList<QcWizard>()
    var isCaptureDone = true
    var isImageMissing = true

    fun getQcImageUrls(awbNo: Long, drs: Int) {
        dataManager.getQCDetailsForPickupActivity(awbNo, drs).subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui()).subscribe({ qcItems ->
                val pickupItems = processQCItems(qcItems)
                _qcImageUrls.postValue(pickupItems)
            }, { error ->
                error.printStackTrace()
                showMessage("Failed to fetch QC details: ${error.localizedMessage}", true)
            }
            ).let { compositeDisposable.add(it) }
    }

    private fun processQCItems(qcItems: List<QcItem>): List<List<String>> {
        return qcItems.mapIndexed { index, qcItem ->
            val allValues = qcItem.qualityChecks
                ?.mapNotNull {
                    it.qcValue
                }?.flatMap {
                    it.split(",").map { value ->
                        value.trim()
                    }
                } ?: emptyList()
            allValues.filter {
                it.startsWith("http", ignoreCase = true)
            }
        }
    }

    fun showMessage(message: String, isError: Boolean) {
        showErrorMessage.value = Pair(message, isError)
    }

    fun getRvpMPSDataWithQc(compositeKey: String?) {
        try {
            compositeDisposable.add(
                dataManager.getRvpMpsWithQc(compositeKey).subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui()).subscribe { rvpMPSWithQC: RVPMPSWithQC ->
                        val qualityCheckListUpdate: MutableList<RvpMpsQualityCheck> = arrayListOf()
                        for (i in 0 until rvpMPSWithQC.getQcItemList().size) {
                            val qualityCheckList: List<RvpMpsQualityCheck> =
                                rvpMPSWithQC.getQcItemList()[i].qualityChecks
                            for (j in qualityCheckList.indices) {
                                qualityCheckList[j].awbNo = rvpMPSWithQC.drsRvpQcMpsResponse.awbNo
                                qualityCheckList[j].drs = rvpMPSWithQC.drsRvpQcMpsResponse.drs
                                qualityCheckList[j].idNumber = rvpMPSWithQC.getQcItemList()[i].id
                                qualityCheckList[j].item = rvpMPSWithQC.getQcItemList()[i].item
                            }
                            rvpMPSWithQC.getQcItemList()[i].qualityChecks = qualityCheckList
                            qualityCheckListUpdate.addAll(rvpMPSWithQC.getQcItemList()[i].qualityChecks)
                        }
                        rvpMPSWithQC.qualityChecks = qualityCheckListUpdate
                        _rvpWithQC.value = rvpMPSWithQC

                        if (rvpWithQC.value?.drsRvpQcMpsResponse?.flags?.flagMap?.is_mdc_rvp_qc_disabled.equals(
                                "true",
                                ignoreCase = true
                            )
                        ) {
                            getMdcOffCase()
                        } else {
                            getRvpMasterData()
                        }
                    })
        } catch (e: Exception) {
            showMessage("Exception: ${e.localizedMessage}", true)
        }
    }

    private fun getMdcOffCase() {
        try {
            val sampleQuestions = ArrayList<SampleQuestion>()
            for (k in rvpWithQC.value!!.qcItemList.indices) {
                sampleQuestions.clear()
                for (i in rvpWithQC.value!!.qcItemList[k].qualityChecks.indices) {
                    val sampleQuestion = SampleQuestion()
                    sampleQuestion.code = rvpWithQC.value!!.qcItemList[k].qualityChecks[i].qcCode
                    sampleQuestion.name = rvpWithQC.value!!.qcItemList[k].qualityChecks[i].item
                    sampleQuestion.imageCaptureSettings =
                        rvpWithQC.value!!.qcItemList[k].qualityChecks[i].imageCaptureSettings
                    sampleQuestion.instructions =
                        rvpWithQC.value!!.qcItemList[k].qualityChecks[i].instructions
                    sampleQuestion.verificationMode =
                        rvpWithQC.value!!.qcItemList[k].qualityChecks[i].qcType
                    sampleQuestions.add(sampleQuestion)
                }
            }
            _sampleQuestions.value = sampleQuestions
        } catch (e: Exception) {
            showMessage("Exception: ${e.localizedMessage}", true)
        }
    }

    private fun getRvpMasterData() {
        try {
            compositeDisposable.add(
                dataManager.getRvpMpsMasterDescriptions(rvpWithQC.value?.qcItemList)
                    .subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui())
                    .subscribe { sampleQuestions: List<SampleQuestion> ->
                        try {
                            for (k in rvpWithQC.value!!.qcItemList.indices) {
                                for (i in rvpWithQC.value!!.qcItemList[k].qualityChecks.indices) {
                                    for (j in sampleQuestions.indices) {
                                        val code =
                                            rvpWithQC.value!!.qcItemList[k].qualityChecks[i].qcCode
                                        if (code.equals(
                                                sampleQuestions[j].code,
                                                ignoreCase = true
                                            )
                                        ) {
                                            Collections.swap(sampleQuestions, i, j)
                                        }
                                        if (sampleQuestions[j].code.startsWith("GEN_ITEM_BRAND_CHECK")) {
                                            if (rvpWithQC.value!!.qcItemList[k].qualityChecks[i].qcCode.equals(
                                                    "GEN_ITEM_BRAND_CHECK",
                                                    ignoreCase = true
                                                )
                                            ) {
                                                val s = sampleQuestions[j].name.replace(
                                                    "#COLOR#",
                                                    rvpWithQC.value!!.qcItemList[k].qualityChecks[i].qcValue
                                                )
                                                sampleQuestions[j].name = s
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            showMessage("Exception: ${e.localizedMessage}", true)
                        }
                        _sampleQuestions.value = ArrayList(sampleQuestions)
                    })
        } catch (e: Exception) {
            showMessage("Exception: ${e.localizedMessage}", true)
        }
    }

    fun imageSettingQcCheck(sampleQuestion: SampleQuestion): String {
        var imageCaptureSetting = "Capture Product Image"
        try {
            if (sampleQuestion.imageCaptureSettings.equals("M", ignoreCase = true)) {
                imageCaptureSetting = "Capture Product Image*"
            }
        } catch (e: Exception) {
            showMessage("Exception: ${e.localizedMessage}", true)
        }
        return imageCaptureSetting
    }

    fun getDetail(rvpQualityCheck: RvpMpsQualityCheck, sampleQuestions: SampleQuestion): String {
        var starredValue = ""
        try {
            starredValue = if (sampleQuestions.verificationMode.contains("TAIL")) {
                imeiTailStars(
                    rvpQualityCheck.qcValue,
                    sampleQuestions.verificationMode.substring(sampleQuestions.verificationMode.length - 1)
                        .toInt()
                )
            } else if (sampleQuestions.verificationMode.contains("HEAD")) {
                imeiHeadStars(
                    rvpQualityCheck.qcValue,
                    sampleQuestions.verificationMode.substring(sampleQuestions.verificationMode.length - 1)
                        .toInt()
                )
            } else if (sampleQuestions.verificationMode.contains("ALL")) {
                imeiFirstLastFourVisible(rvpQualityCheck.qcValue)
            } else {
                imeiFullStars(rvpQualityCheck.qcValue)
            }
        } catch (e: Exception) {
            showMessage("Exception: ${e.localizedMessage}", true)
        }
        return starredValue
    }

    //----------------------------------------------------------QC-CHECK----------------------------------------------------------------
    fun qcNameQcCheck(sampleQuestion: SampleQuestion, rvpQualityCheck: RvpMpsQualityCheck): String {
        var qcName = ""
        try {
            sampleQuestion.name?.let {
                qcName = sampleQuestion.name + "*"
                if (sampleQuestion.name.contains("#COLOR#")) {
                    val qcN = sampleQuestion.name.replace("#COLOR#", rvpQualityCheck.qcValue + " ")
                    qcName = "$qcN*"
                }
            }
        } catch (e: Exception) {
            showMessage("Exception: ${e.localizedMessage}", true)
        }
        return qcName
    }

    //----------------------------------------------------------QC-CHECK-IMAGE----------------------------------------------------------

    fun getQcNameCheckImage(sampleQuestion: SampleQuestion): String {
        var qcName = ""
        try {
            if (sampleQuestion.name != null) {
                qcName = sampleQuestion.name + "*"
            }
        } catch (e: Exception) {
            showMessage("Exception: ${e.localizedMessage}", true)
        }
        return qcName
    }

    @SuppressLint("CheckResult")
    fun uploadImageServerRvpMPSQc(
        context: Context,
        imageName: String?,
        imageUri: String?,
        imageCode: String?,
        awbNo: Long,
        drsNo: Long,
        bitmap: Bitmap?,
        itemSize: Int?
    ) {
        try {
            showProgressDialog(context, context.getString(R.string.upload_image))
            val itemId = imageName!!.substring(imageName.lastIndexOf("_") + 1).replace(".png", "")
            val file = imageUri?.let { File(it) }
            val bytes = CryptoUtils.convertImageToBase64(bitmap)
            val mFile = RequestBody.create(MediaType.parse("application/octet-stream"), bytes)
            val fileToUpload = MultipartBody.Part.createFormData("image", file!!.name, mFile)
            val finalAwbNo = RequestBody.create(MediaType.parse("text/plain"), awbNo.toString())
            val finalDrsNo = RequestBody.create(MediaType.parse("text/plain"), drsNo.toString())
            val finalImageCode =
                imageCode?.let { RequestBody.create(MediaType.parse("text/plain"), it) }
            val finalImageName =
                RequestBody.create(MediaType.parse("text/plain"), imageName.toString())
            val finalImageType: RequestBody = RequestBody.create(
                MediaType.parse("text/plain"),
                GlobalConstant.ImageTypeConstants.RVP_MPS
            )
            val item_id = RequestBody.create(MediaType.parse("text/plain"), itemId)
            val no_of_item: RequestBody = RequestBody.create(
                MediaType.parse("text/plain"),
                java.lang.String.valueOf(itemSize)
            )
            val map: MutableMap<String?, RequestBody?> = HashMap()
            map["image"] = mFile
            map["awb_no"] = finalAwbNo
            map["drs_no"] = finalDrsNo
            map["image_code"] = finalImageCode
            map["image_name"] = finalImageName
            map["image_type"] = finalImageType
            map["no_of_item"] = no_of_item
            map["item_id"] = item_id
            map["rvp_qc_image_key"] = finalImageName

            val headers: MutableMap<String?, String?> = HashMap()
            headers["token"] = dataManager.authToken
            headers["Accept"] = "application/json"
            try {
                compositeDisposable.add(
                    dataManager.doImageUploadApiCall(
                        dataManager.authToken,
                        dataManager.ecomRegion,
                        GlobalConstant.ImageTypeConstants.RVP_MPS,
                        headers,
                        map,
                        fileToUpload
                    ).subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui())
                        .subscribe({ imageUploadResponse: ImageUploadResponse ->
                            dismissProgressDialog()
                            try {
                                if (imageUploadResponse.status.equals(
                                        "Success",
                                        ignoreCase = true
                                    )
                                ) {
                                    saveImageDB(
                                        context,
                                        awbNo.toString(),
                                        drsNo.toString(),
                                        imageUri,
                                        imageCode,
                                        imageName,
                                        2,
                                        imageUploadResponse.imageId
                                    )
                                } else {
                                    val message = imageUploadResponse.description
                                        ?: context.getString(R.string.image_api_response_false)
                                    showMessage(message, true)
                                }
                            } catch (e: Exception) {
                                showMessage("Exception: ${e.localizedMessage}", true)
                            }
                        }, { throwable: Throwable ->
                            dismissProgressDialog()
                            showMessage("Throwable: ${throwable.localizedMessage}", true)
                        })
                )
            } catch (e: Exception) {
                dismissProgressDialog()
                showMessage("Exception: ${e.localizedMessage}", true)
            }
        } catch (e: Exception) {
            dismissProgressDialog()
            showMessage("Exception: ${e.localizedMessage}", true)
        }
    }

    fun saveImageDB(
        context: Context,
        awbNo: String,
        drsId: String?,
        imageUri: String?,
        imageCode: String?,
        imageName: String?,
        status: Int,
        imageId: Int
    ) {
        try {
            showProgressDialog(context, context.getString(R.string.saving_image_response))
            val imageModel = ImageModel()
            if (imageId > 0) {
                imageModel.imageId = imageId
            }
            imageModel.draNo = drsId
            imageModel.awbNo = awbNo
            imageModel.imageName = imageName
            imageModel.image = imageUri
            imageModel.imageCode = imageCode
            imageModel.status = status
            imageModel.imageCurrentSyncStatus = GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO
            imageModel.imageFutureSyncTime = Calendar.getInstance().timeInMillis
            imageModel.date = Calendar.getInstance().timeInMillis
            imageModel.shipmentType = GlobalConstant.ShipmentTypeConstants.RVP
            imageModel.imageType = GlobalConstant.ImageTypeConstants.RVP_MPS
            compositeDisposable.add(
                dataManager.saveImage(imageModel).subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui()).subscribe({
                        dismissProgressDialog()
                        imageSaveStatus.value = true
                    }, { throwable: Throwable? ->
                        dismissProgressDialog()
                        imageSaveStatus.value = false
                        showMessage("Error: ${throwable?.localizedMessage}", true)
                    })
            )
        } catch (e: Exception) {
            showMessage("Exception: ${e.localizedMessage}", true)
        }
    }

    private fun showProgressDialog(context: Context, loadingMessage: String) {
        dialog = Dialog(context).apply {
            setContentView(R.layout.custom_progress_dialog)
            setCancelable(false)
            findViewById<TextView>(R.id.dialog_loading_text).text = loadingMessage
            show()
        }
    }

    private fun dismissProgressDialog() {
        if (::dialog.isInitialized && dialog.isShowing) {
            dialog.dismiss()
        }
    }

    fun createQcWizard(
        checkValue: Boolean,
        remark: String,
        scannedData: String,
        sampleQuestion: SampleQuestion,
        rvpQualityCheck: RvpMpsQualityCheck,
        isImageCheck: Boolean,
        imageBitmap: String
    ) {
        if (qcWizards.size == 0) {
            val qcWizard = QcWizard()
            qcWizard.qcName = sampleQuestion.name
            qcWizard.qccheckcode = sampleQuestion.code
            qcWizard.item_number = rvpQualityCheck.idNumber.toString()
            if (isImageCheck) {
                if (imageBitmap.isEmpty()) {
                    qcWizard.isQcImageFlag = false
                    qcWizard.imagePathWithWaterMark = ""
                } else {
                    qcWizard.isQcImageFlag = true
                    qcWizard.imagePathWithWaterMark = imageBitmap
                }
            } else {
                if (remark.isNotEmpty()) {
                    qcWizard.remarks = remark
                }
                if (sampleQuestion.verificationMode == GlobalConstant.QcTypeConstants.CHECK || sampleQuestion.verificationMode == GlobalConstant.QcTypeConstants.CHECK_IMAGE) {
                    qcWizard.qcValue = "NONE"
                    if (checkValue || qcWizard.qccheckcode.startsWith("OP")) {
                        qcWizard.match = "1"
                    } else {
                        qcWizard.match = "0"
                    }
                } else if (sampleQuestion.verificationMode.contains(
                        GlobalConstant.QcTypeConstants.INPUT,
                        true
                    )
                ) {
                    if (scannedData.isNotBlank()) {
                        qcWizard.qcValue = scannedData
                        if (getDetailStarredString(rvpQualityCheck, sampleQuestion).equals(
                                scannedData,
                                true
                            ) || qcWizard.qccheckcode.startsWith("OP")
                        ) {
                            qcWizard.match = "1"
                        } else {
                            qcWizard.match = "0"
                        }
                    }
                }
            }
            qcWizards.add(qcWizard)
        } else {
            var needUpdate = true
            qcWizards.forEachIndexed { index, _ ->
                if (qcWizards[index].qccheckcode.equals(
                        sampleQuestion.code,
                        false
                    ) && qcWizards[index].item_number.equals(
                        rvpQualityCheck.idNumber.toString(),
                        false
                    )
                ) {
                    needUpdate = false
                    qcWizards[index].qcName = sampleQuestion.name
                    qcWizards[index].qccheckcode = sampleQuestion.code
                    if (isImageCheck) {
                        if (imageBitmap.isBlank()) {
                            qcWizards[index].isQcImageFlag = false
                            qcWizards[index].imagePathWithWaterMark = ""
                        } else {
                            qcWizards[index].isQcImageFlag = true
                            qcWizards[index].imagePathWithWaterMark = imageBitmap
                        }
                    } else {
                        qcWizards[index].remarks = remark

                        if (sampleQuestion.verificationMode.equals(
                                GlobalConstant.QcTypeConstants.CHECK,
                                true
                            ) || sampleQuestion.verificationMode.equals(
                                GlobalConstant.QcTypeConstants.CHECK_IMAGE,
                                true
                            )
                        ) {
                            qcWizards[index].qcValue = "NONE"
                            if (checkValue || qcWizards[index].qccheckcode.startsWith("OP")) {
                                qcWizards[index].match = "1"
                            } else {
                                qcWizards[index].match = "0"
                            }
                        } else if (sampleQuestion.verificationMode.contains(
                                GlobalConstant.QcTypeConstants.INPUT,
                                true
                            )
                        ) {
                            if (scannedData.isNotBlank()) {
                                qcWizards[index].qcValue = scannedData
                                if (getDetailStarredString(rvpQualityCheck, sampleQuestion).equals(
                                        scannedData,
                                        true
                                    ) || qcWizards[index].qccheckcode.startsWith("OP")
                                ) {
                                    qcWizards[index].match = "1"
                                } else {
                                    qcWizards[index].match = "0"
                                }
                            }
                        }
                    }
                }
            }
            if (needUpdate) {
                val qcWizard = QcWizard()
                qcWizard.qcName = sampleQuestion.name
                qcWizard.qccheckcode = sampleQuestion.code
                qcWizard.item_number = rvpQualityCheck.idNumber.toString()
                if (isImageCheck) {
                    if (imageBitmap.isEmpty()) {
                        qcWizard.isQcImageFlag = false
                        qcWizard.imagePathWithWaterMark = ""
                    } else {
                        qcWizard.isQcImageFlag = true
                        qcWizard.imagePathWithWaterMark = imageBitmap
                    }
                } else {
                    qcWizard.remarks = remark

                    if (sampleQuestion.verificationMode.equals(
                            GlobalConstant.QcTypeConstants.CHECK,
                            true
                        ) || sampleQuestion.verificationMode.equals(
                            GlobalConstant.QcTypeConstants.CHECK_IMAGE,
                            true
                        )
                    ) {
                        qcWizard.qcValue = "NONE"
                        if (checkValue || qcWizard.qccheckcode.startsWith("OP")) {
                            qcWizard.match = "1"
                        } else {
                            qcWizard.match = "0"
                        }
                    } else if (sampleQuestion.verificationMode.contains(
                            GlobalConstant.QcTypeConstants.INPUT,
                            true
                        )
                    ) {
                        if (scannedData.isNotBlank()) {
                            qcWizard.qcValue = scannedData
                            if (getDetailStarredString(rvpQualityCheck, sampleQuestion).equals(
                                    scannedData,
                                    true
                                ) || qcWizard.qccheckcode.startsWith("OP")
                            ) {
                                qcWizard.match = "1"
                            } else {
                                qcWizard.match = "0"
                            }
                        }
                    }
                }
                qcWizards.add(qcWizard)
            }
        }
    }

    private fun getDetailStarredString(
        rvpQualityCheck: RvpMpsQualityCheck,
        sampleQuestions: SampleQuestion
    ): String {
        var starredString = ""
        try {
            starredString = if (sampleQuestions.verificationMode.contains("TAIL")) {
                getTrailStarredString(
                    rvpQualityCheck.qcValue,
                    sampleQuestions.verificationMode.substring(sampleQuestions.verificationMode.length - 1)
                        .toInt()
                )
            } else if (sampleQuestions.verificationMode.contains("HEAD")) {
                getHeadStarredString(
                    rvpQualityCheck.qcValue,
                    sampleQuestions.verificationMode.substring(sampleQuestions.verificationMode.length - 1)
                        .toInt()
                )
            } else if (sampleQuestions.verificationMode.contains("ALL")) {
                rvpQualityCheck.qcValue
            } else {
                rvpQualityCheck.qcValue
            }
        } catch (e: Exception) {
            showMessage("Exception: ${e.localizedMessage}", true)
        }
        return starredString
    }

    fun getQcItemListForCommit(
    ): ArrayList<RvpCommit.QcItem> {
        //create QC Item packet with qcWizards as per new commit packet requirements
        val qcItemList: ArrayList<RvpCommit.QcItem> = ArrayList()
        var isEntry = false
        for (i in qcWizards.indices) {
            if (qcItemList.isEmpty()) {
                val qcItem = RvpCommit.QcItem()
                val wizardArrayList: MutableList<QcWizard> = ArrayList()
                if (qcWizards[i].match.equals("1", ignoreCase = true)) {
                    qcItem.status = "Pass"
                } else {
                    qcItem.status = "Fail"
                }
                wizardArrayList.add(qcWizards[i])
                qcItem.qcWizard = wizardArrayList
                qcItem.itemNumber = qcWizards[i].item_number.toString()
                qcItemList.add(qcItem)
            } else {
                for (j in qcItemList.indices) {
                    if (qcItemList[j].itemNumber == qcWizards[i].item_number.toString()) {
                        val wizardArrayList = qcItemList[j].qcWizard
                        wizardArrayList.add(qcWizards[i])
                        if (qcWizards[i].match.equals("1", ignoreCase = true)) {
                            if (qcItemList[j].status.equals(
                                    "NA",
                                    ignoreCase = true
                                ) || qcItemList[j].status == null
                            ) qcItemList[j].status =
                                "Pass"
                        } else {
                            qcItemList[j].status = "Fail"
                        }
                        qcItemList[j].qcWizard = wizardArrayList
                        isEntry = false
                    } else {
                        isEntry = true
                    }
                }
                if (isEntry) {
                    val qcItem = RvpCommit.QcItem()
                    val wizardArrayList: MutableList<QcWizard> = ArrayList()
                    if (qcWizards[i].match.equals("1", ignoreCase = true)) {
                        qcItem.status = "Pass"
                    } else {
                        qcItem.status = "Fail"
                    }
                    wizardArrayList.add(qcWizards[i])
                    qcItem.qcWizard = wizardArrayList
                    qcItem.itemNumber = qcWizards[i].item_number.toString()
                    qcItemList.add(qcItem)
                }
            }
        }
        return qcItemList
    }

    fun qcDataVerifyForCommitPackets(
        qualityChecks: MutableList<RvpMpsQualityCheck>,
        qcWizardList: ArrayList<QcWizard>
    ) {
        var isFailed = false
        isImageCapture.value = true
        isCaptureDone = true
        isImageMissing = true
        if (qualityChecks.size == qcWizardList.size) {
            for (qcWizard in qcWizardList) {
                sampleQuestions.value?.forEach { value ->
                    if (!qcWizard.isQcImageFlag) {
                        if (qcWizard.qccheckcode.equals(
                                value.code,
                                true
                            ) && value.imageCaptureSettings.equals(
                                "M",
                                false
                            ) && qcWizard.item_number.equals(
                                qualityChecks[0].idNumber.toString(),
                                false
                            )
                        ) {
                            isCaptureDone = false
                            isImageCapture.value = false
                        }
                    }
                    if (qcWizard.isQcImageFlag) {
                        isImageMissing = false
                    }
                }
                if (qcWizard.match == null) {
                    showMessage("Please mark complete all qc...", true)
                    return
                }
                if (qcWizard.match.equals("0", true)) {
                    isFailed = true
                }
            }
            if (isFailed) {
                setNextScreenStatus.postValue(0)
            } else {
                setNextScreenStatus.postValue(1)
            }
        } else {
            showMessage("Please complete all quality check...", true)
        }
    }
}