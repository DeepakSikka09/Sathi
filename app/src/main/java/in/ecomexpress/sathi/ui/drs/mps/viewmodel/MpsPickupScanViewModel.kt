package `in`.ecomexpress.sathi.ui.drs.mps.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.paytmmoneyagent.manchlib.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.ecomexpress.geolocations.LocationService
import `in`.ecomexpress.sathi.R
import `in`.ecomexpress.sathi.SathiApplication
import `in`.ecomexpress.sathi.backgroundServices.SyncServicesV2
import `in`.ecomexpress.sathi.repo.IDataManager
import `in`.ecomexpress.sathi.repo.local.data.commit.PushApi
import `in`.ecomexpress.sathi.repo.local.data.rvp.RvpCommit
import `in`.ecomexpress.sathi.repo.local.db.model.ImageModel
import `in`.ecomexpress.sathi.repo.remote.model.drs_list.rvp.RvpFlyerDuplicateCheckRequest
import `in`.ecomexpress.sathi.repo.remote.model.drs_list.rvp.RvpFlyerDuplicateCheckResponse
import `in`.ecomexpress.sathi.repo.remote.model.forward.verifyOTP.VerifyOTPResponse
import `in`.ecomexpress.sathi.repo.remote.model.image.ImageUploadResponse
import `in`.ecomexpress.sathi.repo.remote.model.mps.MpsPickupItem
import `in`.ecomexpress.sathi.repo.remote.model.mps.QcItem
import `in`.ecomexpress.sathi.repo.remote.model.otp.resendotp.GenerateUDOtpRequest
import `in`.ecomexpress.sathi.repo.remote.model.otp.resendotp.ResendOtpRequest
import `in`.ecomexpress.sathi.repo.remote.model.otp.resendotp.ResendOtpResponse
import `in`.ecomexpress.sathi.repo.remote.model.otp.verifyotp.VerifyOtpRequest
import `in`.ecomexpress.sathi.repo.remote.model.otp.verifyotp.VerifyOtpResponse
import `in`.ecomexpress.sathi.repo.remote.model.otp.verifyotp.VerifyUDOtpRequest
import `in`.ecomexpress.sathi.repo.remote.model.voice_otp.VoiceOTP
import `in`.ecomexpress.sathi.repo.remote.model.voice_otp.VoiceOTPResponse
import `in`.ecomexpress.sathi.ui.base.BaseViewModel
import `in`.ecomexpress.sathi.ui.drs.rvp.navigator.RvpQcNavigator
import `in`.ecomexpress.sathi.utils.Constants
import `in`.ecomexpress.sathi.utils.CryptoUtils
import `in`.ecomexpress.sathi.utils.GlobalConstant
import `in`.ecomexpress.sathi.utils.Helper
import `in`.ecomexpress.sathi.utils.Logger
import `in`.ecomexpress.sathi.utils.rx.ISchedulerProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class MpsPickupScanViewModel @Inject constructor(dataManager: IDataManager, schedulerProvider: ISchedulerProvider, application: Application) : BaseViewModel<RvpQcNavigator>(dataManager, schedulerProvider, application){

    var isImageMissing = true
    private val showErrorMessage = MutableLiveData<Pair<String, Boolean>>()
    val showErrorMessageLiveData: LiveData<Pair<String, Boolean>> get() = showErrorMessage

    private val _pickupItemsLiveData = MutableLiveData<List<MpsPickupItem>>()
    val pickupItemsLiveData: LiveData<List<MpsPickupItem>> get() = _pickupItemsLiveData

    private lateinit var dialog: Dialog

    private val _otpVerifyStatus = SingleLiveEvent<Boolean>()
    val otpVerifyStatus: LiveData<Boolean> get() = _otpVerifyStatus

    private val _clearStackEvent = MutableLiveData<Unit>()
    val clearStackEvent: LiveData<Unit> = _clearStackEvent

    private val setOtpSendStatus = MutableLiveData<Boolean>()
    val otpSendStatus: LiveData<Boolean> get() = setOtpSendStatus

    private val setCapturedImageCount = MutableLiveData(0)
    val capturedImageCount: LiveData<Int> get() = setCapturedImageCount

    val getImageSaveStatus: LiveData<Boolean> get() = imageSaveStatus
    private val imageSaveStatus = MutableLiveData<Boolean>()

    val flyerDuplicateCheckStatus: LiveData<Boolean> get() = setFlyerDuplicateCheckStatus
    internal val setFlyerDuplicateCheckStatus = MutableLiveData<Boolean>()

    private val _commitApiVerifyStatus = SingleLiveEvent<Boolean>()
    val commitApiVerifyStatus: LiveData<Boolean> get() = _commitApiVerifyStatus

    private fun setCommitApiVerifyStatus(status: Boolean) {
        _commitApiVerifyStatus.postValue(status)
    }

    private fun setOtpVerifyStatus(status: Boolean) {
        _otpVerifyStatus.postValue(status)
    }

    fun showMessage(message: String, isError: Boolean) {
        showErrorMessage.value = Pair(message, isError)
    }

    fun handleImageCaptureCount(isImageDeleted: Boolean) {
        setCapturedImageCount.value = (setCapturedImageCount.value ?: 0) + if (isImageDeleted) -1 else 1
    }

    fun fetchQCDetails(awbNo: Long, drs: Int) {
        dataManager.getQCDetailsForPickupActivity(awbNo, drs).subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui()).subscribe({ 
            qcItems ->
                val pickupItems = processQCItems(qcItems)
                _pickupItemsLiveData.postValue(pickupItems)
            }, { error ->
                error.printStackTrace()
                showMessage("Failed to fetch QC details: ${error.localizedMessage}", true)
            }
        ).let { compositeDisposable.add(it) } 
    }

    private fun processQCItems(qcItems: List<QcItem>): List<MpsPickupItem> {
        return qcItems.mapIndexed { index, qcItem ->
            val allValues = qcItem.qualityChecks
                ?.mapNotNull {
                    it.qcValue
                }?.flatMap {
                    it.split(",").map {
                        value -> value.trim()
                    }
                } ?: emptyList()

            val imageUrls = allValues.filter {
                it.startsWith("http", ignoreCase = true)
            }
            val finalImageUrls = imageUrls.ifEmpty {
                listOf("Without_Urls")
            }

            MpsPickupItem(subProductNumbering = "Product #${index + 1}", imageUrls = finalImageUrls, itemDescription = qcItem.item ?: "No description available")
        }
    }

    fun onRvpOtpVerifyApiCall(context: Context, otp: String?, awbNumber: String) {
        try {
            showProgressDialog(context, context.getString(R.string.verifying))
            val request = VerifyOtpRequest(awbNumber, otp, Constants.RVP, Constants.VERIFY_OTP, Constants.Secure)
            compositeDisposable.add(dataManager.doVerifyOtpApiCall(dataManager.authToken, dataManager.ecomRegion, request).doOnSuccess {}.subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe({ response: VerifyOtpResponse ->
                    dismissProgressDialog()
                    try {
                        val error = if ((response.response == null || response.response.description == null)) context.getString(
                                R.string.api_response_is_false
                            ) else response.response.description
                        if (response.status) {
                            setOtpVerifyStatus(true)
                        } else {
                            setOtpVerifyStatus(false)
                            showMessage(error, true)
                            if (error.equals(context.getString(R.string.invalid_authentication_token), ignoreCase = true)) {
                                clearAppData(context)
                            }
                        }
                    } catch (e: Exception) {
                        setOtpVerifyStatus(false)
                        showMessage("Exception: ${e.localizedMessage}", true)
                    }
                }, { throwable: Throwable? ->
                    setOtpVerifyStatus(false)
                    dismissProgressDialog()
                    showMessage("Error: ${throwable?.localizedMessage}", true)
                })
            )
        } catch (e: Exception) {
            setOtpVerifyStatus(false)
            dismissProgressDialog()
            showMessage("Exception: ${e.localizedMessage}", true)
            e.printStackTrace()
        }
    }

    fun onRqcOtpVerifyApiCall(context: Activity, awb: String?, otp: String?, drsId: String?) {
        try {
            showProgressDialog(context, context.getString(R.string.verifying))
            val request = VerifyUDOtpRequest(awb, drsId, Constants.OTP, otp)
            compositeDisposable.add(dataManager.doVerifyUDOtpDRSApiCall(dataManager.authToken, dataManager.ecomRegion, request).doOnSuccess {}.subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe({ response: VerifyOTPResponse ->
                    dismissProgressDialog()
                    val error: String = response.description ?: response.response?.description ?: context.getString(
                            R.string.api_response_is_false
                        )
                    if (response.status.equals("true", ignoreCase = true)) {
                        setOtpVerifyStatus(true)
                    } else {
                        setOtpVerifyStatus(false)
                        showMessage(error, true)
                        if (error.equals(context.getString(R.string.invalid_authentication_token), ignoreCase = true)) {
                            clearAppData(context)
                        }
                    }
                }, { throwable: Throwable? ->
                    setOtpVerifyStatus(false)
                    dismissProgressDialog()
                    showMessage("Error: ${throwable?.localizedMessage}", true)
                })
            )
        } catch (e: Exception) {
            setOtpVerifyStatus(false)
            dismissProgressDialog()
            e.printStackTrace()
            showMessage("Exception: ${e.localizedMessage}", true)
        }
    }

    fun onRqcGenerateOtpApiCall(context: Activity, awb: String?, drsId: String?, alternateClick: Boolean?, generateOtp: Boolean?) {
        showProgressDialog(context, context.getString(R.string.sending_otp))
        try {
            val request = GenerateUDOtpRequest(awb, Constants.OTP, drsId, alternateClick, dataManager.code, generateOtp, Constants.RQC)
            compositeDisposable.add(dataManager.doGenerateUDOtpApiCall(dataManager.authToken, dataManager.ecomRegion, request).doOnSuccess {}.subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui())
                .subscribe({ response: ResendOtpResponse ->
                    dismissProgressDialog()
                    val error: String = response.description ?: response.response?.description ?: context.getString(
                            R.string.api_response_is_false
                        )
                    if (response.status.equals("true", ignoreCase = true)) {
                        val message =
                            if (response.description.contains(context.getString(R.string.otp_already_generated))) {
                                setOtpSendStatus.postValue(false)
                                context.getString(R.string.otp_already_generated)
                            } else {
                                setOtpSendStatus.postValue(true)
                                context.getString(R.string.otp_send_successfully)
                            }
                        showMessage(message, false)
                    } else {
                        setOtpSendStatus.postValue(false)
                        showMessage(error, true)
                        if (error.equals(context.getString(R.string.invalid_authentication_token), ignoreCase = true)) {
                            clearAppData(context)
                        }
                    }
                }, { throwable: Throwable? ->
                    setOtpSendStatus.postValue(false)
                    dismissProgressDialog()
                    showMessage("Error: ${throwable?.localizedMessage}", true)
                })
            )
        } catch (e: Exception) {
            setOtpSendStatus.postValue(false)
            dismissProgressDialog()
            showMessage("Exception: ${e.localizedMessage}", true)
            e.printStackTrace()
        }
    }

    fun onRvpGenerateOtpApiCall(context: Context, awb: String?, drsId: String?, allowAlternateNumber: Boolean?) {
        showProgressDialog(context, context.getString(R.string.sending_otp))
        val request = ResendOtpRequest(awb, "OTP", drsId, allowAlternateNumber)
        try {
            compositeDisposable.add(dataManager.doResendOtpApiCall(dataManager.authToken, dataManager.ecomRegion, request).doOnSuccess {}.subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui())
                .subscribe({ response: ResendOtpResponse ->
                    dismissProgressDialog()
                    val error: String = response.description ?: response.response?.description ?: context.getString(
                            R.string.api_response_is_false
                        )
                    if (response.status.equals("true", ignoreCase = true)) {
                        setOtpSendStatus.postValue(true)
                        showMessage(context.getString(R.string.otp_send_successfully), false)
                    } else {
                        setOtpSendStatus.postValue(false)
                        showMessage(error, true)
                        if (error.equals(context.getString(R.string.invalid_authentication_token), ignoreCase = true)) {
                            clearAppData(context)
                        }
                    }
                }, { throwable: Throwable? ->
                    setOtpSendStatus.postValue(false)
                    dismissProgressDialog()
                    showMessage("Error: ${throwable?.localizedMessage}", true)
                })
            )
        } catch (e: Exception) {
            setOtpSendStatus.postValue(false)
            dismissProgressDialog()
            showMessage("Exception: ${e.localizedMessage}", true)
            e.printStackTrace()
        }
    }

    fun doVoiceOTPApiCall(context: Context, awb: String, drsId: String, shipmentType: String) {
        try {
            showProgressDialog(context, context.getString(R.string.sending_otp))
            val voiceOtpRequest = VoiceOTP()
            voiceOtpRequest.awb = awb
            voiceOtpRequest.drs_id = drsId
            voiceOtpRequest.product_type = shipmentType
            voiceOtpRequest.employee_code = dataManager.emp_code
            voiceOtpRequest.message_type = Constants.OTP
            compositeDisposable.add(dataManager.doVoiceOtpApiCall(dataManager.authToken, dataManager.ecomRegion, voiceOtpRequest).doOnSuccess {}.subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui())
                .subscribe({ response: VoiceOTPResponse ->
                    dismissProgressDialog()
                    try {
                        val error = response.description ?: context.getString(R.string.api_response_is_false)
                        if (response.code == 0) {
                            setOtpSendStatus.postValue(true)
                            showMessage(error, false)
                        } else {
                            setOtpSendStatus.postValue(false)
                            showMessage(error, true)
                        }
                    } catch (e: Exception) {
                        setOtpSendStatus.postValue(false)
                        showMessage("Exception: ${e.localizedMessage}", true)
                    }
                }, { throwable: Throwable? ->
                    setOtpSendStatus.postValue(false)
                    dismissProgressDialog()
                    showMessage("Error: ${throwable?.localizedMessage}", true)
                })
            )
        } catch (e: Exception) {
            setOtpSendStatus.postValue(false)
            dismissProgressDialog()
            showMessage("Exception: ${e.localizedMessage}", true)
            e.printStackTrace()
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

    fun clearAppData(context: Context) {
        try {
            compositeDisposable.add(dataManager.deleteAllTables().subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui()).subscribe({
                try {
                    dataManager.clearPrefrence()
                    dataManager.setUserAsLoggedOut()
                    dataManager.setTripId("0")
                    dataManager.setIsAdmEmp(false)
                    dataManager.setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT)

                    // Stop services
                    val activityContext = context
                    try {
                        activityContext.stopService(SyncServicesV2.getStopIntent(context))
                    } catch (e: Exception) {
                        Logger.e(Helper.TAG, e.toString())
                    }
                    activityContext.stopService(Intent(context, LocationService::class.java))

                    // Clear application data
                    SathiApplication.shipmentImageMap.clear()
                    SathiApplication.rtsCapturedImage1.clear()
                    SathiApplication.rtsCapturedImage2.clear()
                    _clearStackEvent.postValue(Unit)
                } catch (e: Exception) {
                    showMessage(e.message.toString(), true)
                }
            }, { e ->
                showMessage(e.message.toString(), true)
            }))
        } catch (e: Exception) {
            e.printStackTrace()
            showMessage(e.message.toString(), true)
        }
    }

    fun uploadImageToServer(context: Context, imageName: String, imageUri: String, imageCode: String, awbNo: String, drsId: String) {
        showProgressDialog(context, context.getString(R.string.image_uploading))
        try {
            val imageFile = File(imageUri)
            val bytes = CryptoUtils.decryptFile1(imageFile.toString(), Constants.ENC_DEC_KEY)
            val mFile = RequestBody.create(MediaType.parse("application/octet-stream"), bytes)
            val fileToUpload = MultipartBody.Part.createFormData("image", imageFile.name, mFile)
            val awbNumber = RequestBody.create(MultipartBody.FORM, awbNo)
            val drsNo = RequestBody.create(MultipartBody.FORM, drsId)
            val finalImageCode = RequestBody.create(MultipartBody.FORM, imageCode)
            val finalImageName = RequestBody.create(MultipartBody.FORM, imageName)
            val finalImageType: RequestBody = RequestBody.create(MultipartBody.FORM, GlobalConstant.ImageTypeConstants.OTHERS)
            val map: MutableMap<String?, RequestBody?> = HashMap()
            map["image"] = mFile
            map["awb_no"] = awbNumber
            map["drs_no"] = drsNo
            map["image_code"] = finalImageCode
            map["image_name"] = finalImageName
            map["image_type"] = finalImageType
            val headers: MutableMap<String?, String?> = HashMap()
            headers["token"] = dataManager.authToken
            headers["Accept"] = "application/json"
            compositeDisposable.add(dataManager.doImageUploadApiCall(dataManager.authToken, dataManager.ecomRegion, Constants.OTHERS, headers, map, fileToUpload).doOnSuccess { }.subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui()).subscribe({
                imageUploadResponse: ImageUploadResponse ->
                    dismissProgressDialog()
                    try {
                        if (imageUploadResponse.status.equals("Success", ignoreCase = true)) {
                            saveImageInLocalDB(context, awbNo, drsId, imageUri, imageCode, imageName, imageUploadResponse.imageId)
                        } else {
                            val message = imageUploadResponse.description ?: context.getString(R.string.image_api_response_false)
                            showMessage(message, true)
                        }
                    } catch (e: Exception) {
                        showMessage("Exception: ${e.localizedMessage}", true)
                    }
                }, { throwable: Throwable? ->
                    dismissProgressDialog()
                    throwable?.printStackTrace()
                    showMessage("Error: ${throwable?.localizedMessage}", true)
                })
            )
        } catch (e: Exception) {
            e.printStackTrace()
            dismissProgressDialog()
            showMessage("Exception: ${e.localizedMessage}", true)
        }
    }

    private fun saveImageInLocalDB(context: Context, awbNo: String, drsId: String?, imageUri: String?, imageCode: String?, imageName: String?, imageId: Int) {
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
            imageModel.status = Constants.SYNCED
            imageModel.imageCurrentSyncStatus = GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO
            imageModel.imageFutureSyncTime = Calendar.getInstance().timeInMillis
            imageModel.imageType = GlobalConstant.ImageTypeConstants.OTHERS
            imageModel.date = Calendar.getInstance().timeInMillis
            imageModel.shipmentType = GlobalConstant.ShipmentTypeConstants.RVP
            compositeDisposable.add(dataManager.saveImage(imageModel).subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui()).subscribe({
                dismissProgressDialog()
                imageSaveStatus.value = true
                handleImageCaptureCount(false)
            }, { throwable: Throwable? ->
                dismissProgressDialog()
                imageSaveStatus.value = false
                showMessage("Error: ${throwable?.localizedMessage}", true)
            }))
        } catch (e: Exception) {
            e.printStackTrace()
            dismissProgressDialog()
            showMessage("Exception: ${e.localizedMessage}", true)
        }
    }

    fun doRvpFlyerDuplicateCheckApiCall(context: Context, scannedValue: String?, drsId: String?, awbNumber: String) {
        try {
            showProgressDialog(context, context.getString(R.string.verifying))
            val rvpFlyerDuplicateCheckRequest = RvpFlyerDuplicateCheckRequest().apply {
                this.awb = awbNumber
                this.drs_id = drsId
                this.ref_packaging_barcode = scannedValue
            }
            compositeDisposable.add(dataManager.doRvpflyerDuplicateCheck(dataManager.authToken, dataManager.ecomRegion, rvpFlyerDuplicateCheckRequest).observeOn(schedulerProvider.ui()).subscribeOn(schedulerProvider.io()).subscribe({
                rvpFlyerDuplicateCheckResponse: RvpFlyerDuplicateCheckResponse ->
                    dismissProgressDialog()
                    try {
                        if (rvpFlyerDuplicateCheckResponse.status.equals("true", ignoreCase = true)) {
                            setFlyerDuplicateCheckStatus.postValue(false)
                            showMessage(rvpFlyerDuplicateCheckResponse.description, true)
                        } else {
                            setFlyerDuplicateCheckStatus.postValue(true)
                        }
                    } catch (e: Exception) {
                        setFlyerDuplicateCheckStatus.postValue(false)
                        showMessage("Exception: ${e.localizedMessage}", true)
                    }
                }, { throwable: Throwable? ->
                    dismissProgressDialog()
                    setFlyerDuplicateCheckStatus.postValue(false)
                    showMessage("Error: ${throwable?.localizedMessage}", true)
                })
            )
        } catch (e: Exception) {
            dismissProgressDialog()
            setFlyerDuplicateCheckStatus.postValue(false)
            showMessage("Exception: ${e.localizedMessage}", true)
        }
    }

    fun addImageListAndDoCommitApiCall(context: Context, rvpCommit: RvpCommit, isInternetConnected: Boolean) {
        val compositeDisposable = CompositeDisposable()
        try {
            compositeDisposable.add(dataManager.getImages(rvpCommit.awb).subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.io()).subscribe({
                imageModels ->
                    val listImageResponses = imageModels.mapNotNull { imageModel ->
                        imageModel?.let {
                            RvpCommit.ImageData().apply {
                                imageId = it.imageId.toString()
                                imageKey = it.imageName.toString()
                            }
                        }
                    }
                    rvpCommit.imageData = listImageResponses
                    val handler = Handler(Looper.getMainLooper())
                    handler.post {
                        if (!isInternetConnected) {
                            if (dataManager.rvpRealTimeSync.equals("true", ignoreCase = true)) {
                                showMessage(context.getString(R.string.check_internet), true)
                            } else {
                                saveCommitApi(rvpCommit, true)
                            }
                        } else {
                            startCommitApi(context, rvpCommit)
                        }
                    }
                }, { error ->
                    showMessage("Error ${error.localizedMessage}", true)
                })
            )
        } catch (e: Exception) {
            e.printStackTrace()
            showMessage("Exception: ${e.localizedMessage}", true)
        }
    }

    private fun saveCommitApi(rvpCommit: RvpCommit, isFromOffline: Boolean) {
        val pushApi = PushApi().apply {
            awbNo = rvpCommit.awb.toLong()
            compositeKey = rvpCommit.drsId + rvpCommit.awb
            isRvp_mps = true
            shipmentStatus = if (isFromOffline) {
                GlobalConstant.CommitStatus.CommitAssign
            } else {
                GlobalConstant.CommitStatus.CommitSynced
            }
            if (!isFromOffline) {
                shipmentDeliveryStatus = GlobalConstant.CommitStatus.CommitSynced.toString()
            }
            authtoken = dataManager.authToken
            shipmentCaterogy = rvpCommit.attemptType
        }

        try {
            pushApi.requestData = ObjectMapper().writeValueAsString(rvpCommit)
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
            setCommitApiVerifyStatus(false)
            showMessage("Error: ${e.localizedMessage}", true)
            return
        }

        compositeDisposable.add(dataManager.saveCommitPacket(pushApi).subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui()).subscribe({
            if (isFromOffline) {
                compositeDisposable.add(dataManager.updateRvpMpsStatus(rvpCommit.drsId + rvpCommit.awb, Constants.SHIPMENT_DELIVERED_STATUS).subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui()).subscribe({
                    setCommitApiVerifyStatus(true)
                }, { throwable ->
                    setCommitApiVerifyStatus(false)
                    throwable.printStackTrace()
                    showMessage("Error updating RVP status: ${throwable.localizedMessage}", true)
                }))
            } else {
                setCommitApiVerifyStatus(true)
            }
        }, { throwable ->
            throwable.printStackTrace()
            setCommitApiVerifyStatus(false)
            showMessage("Error: ${throwable.localizedMessage}", true)
        }))
    }

    @SuppressLint("CheckResult")
    private fun startCommitApi(context: Context, rvpCommit: RvpCommit) {
        showProgressDialog(context, context.getString(R.string.uploading_commit_data))
        val compositeDisposable = CompositeDisposable()
        val tokens = hashMapOf<String, String>()
        tokens[Constants.TOKEN] = dataManager.authToken
        tokens[Constants.EMP_CODE] = dataManager.code
        try {
            compositeDisposable.add(dataManager.doRVPUndeliveredCommitApiCall(dataManager.authToken, dataManager.ecomRegion, tokens, rvpCommit).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doFinally { dismissProgressDialog() }.subscribe({
                rvpCommitResponse ->
                    if (rvpCommitResponse.status) {
                        val shipmentStatus = if (rvpCommitResponse.response.shipment_status.equals(Constants.RVPUNDELIVERED, ignoreCase = true)) {
                            Constants.SHIPMENT_UNDELIVERED_STATUS
                        } else {
                            Constants.SHIPMENT_DELIVERED_STATUS
                        }
                        compositeDisposable.add(dataManager.updateRvpMpsStatus(rvpCommit.drsId + rvpCommit.awb, shipmentStatus).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).flatMap { dataManager.deleteSyncedImage(rvpCommitResponse.response.awb_no) }.observeOn(AndroidSchedulers.mainThread()).subscribe({
                            updateSyncStatusInDRSRvpMpsTable(rvpCommitResponse.response.drs_no + rvpCommitResponse.response.awb_no, rvpCommit)
                        }, { throwable ->
                            throwable.printStackTrace()
                            setCommitApiVerifyStatus(false)
                            showMessage("Error: ${throwable.localizedMessage}", true)
                        }))
                    } else {
                        setCommitApiVerifyStatus(false)
                        val errorMessage = rvpCommitResponse.response.description ?: context.getString(R.string.api_response_is_false)
                        showMessage(errorMessage, true)
                    }
                }, { throwable ->
                    throwable.printStackTrace()
                    setCommitApiVerifyStatus(false)
                    showMessage("Error: ${throwable.localizedMessage}", true)
                })
            )
        } catch (e: Exception) {
            e.printStackTrace()
            setCommitApiVerifyStatus(false)
            dismissProgressDialog()
            showMessage("Exception: ${e.localizedMessage}", true)
        }
    }

    @SuppressLint("CheckResult")
    private fun updateSyncStatusInDRSRvpMpsTable(compositeKey: String, rvpCommit: RvpCommit) {
        dataManager.updateSyncStatusMps(compositeKey, Constants.SYNCED).subscribeOn(schedulerProvider.io()).observeOn(AndroidSchedulers.mainThread()).doFinally {}.subscribe({
            saveCommitApi(rvpCommit, false)
        }, { throwable: Throwable ->
            throwable.printStackTrace()
            setCommitApiVerifyStatus(false)
            showMessage("Error: ${throwable.localizedMessage}", true)
        })
    }

    fun deleteRVPQCData(drs: Int, awbNo: Long) {
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(dataManager.deleteMpsQcDataFromQcItemTable(drs, awbNo).subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.io()).subscribe({
            // Nothing to do after success.
        }, { throwable ->
            showMessage("Error: ${throwable?.localizedMessage}", true)
        }))
    }
}