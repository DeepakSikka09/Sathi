package `in`.ecomexpress.sathi.ui.drs.rvp_new.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
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
import `in`.ecomexpress.sathi.repo.local.data.rvp.RvpCommit.QcWizard
import `in`.ecomexpress.sathi.repo.local.db.model.ImageModel
import `in`.ecomexpress.sathi.repo.local.db.model.RvpWithQC
import `in`.ecomexpress.sathi.repo.remote.ByteImageRequest
import `in`.ecomexpress.sathi.repo.remote.model.drs_list.rvp.RvpFlyerDuplicateCheckRequest
import `in`.ecomexpress.sathi.repo.remote.model.drs_list.rvp.RvpFlyerDuplicateCheckResponse
import `in`.ecomexpress.sathi.repo.remote.model.drs_list.rvp.RvpQualityCheck
import `in`.ecomexpress.sathi.repo.remote.model.forward.verifyOTP.VerifyOTPResponse
import `in`.ecomexpress.sathi.repo.remote.model.image.ImageUploadResponse
import `in`.ecomexpress.sathi.repo.remote.model.masterdata.SampleQuestion
import `in`.ecomexpress.sathi.repo.remote.model.otp.resendotp.GenerateUDOtpRequest
import `in`.ecomexpress.sathi.repo.remote.model.otp.resendotp.ResendOtpRequest
import `in`.ecomexpress.sathi.repo.remote.model.otp.resendotp.ResendOtpResponse
import `in`.ecomexpress.sathi.repo.remote.model.otp.verifyotp.VerifyOtpRequest
import `in`.ecomexpress.sathi.repo.remote.model.otp.verifyotp.VerifyOtpResponse
import `in`.ecomexpress.sathi.repo.remote.model.otp.verifyotp.VerifyUDOtpRequest
import `in`.ecomexpress.sathi.repo.remote.model.voice_otp.VoiceOTP
import `in`.ecomexpress.sathi.repo.remote.model.voice_otp.VoiceOTPResponse
import `in`.ecomexpress.sathi.ui.base.BaseViewModel
import `in`.ecomexpress.sathi.ui.drs.rvp_new.navigator.RvpQcNavigator
import `in`.ecomexpress.sathi.utils.Base64
import `in`.ecomexpress.sathi.utils.CommonUtils.getHeadStarredString
import `in`.ecomexpress.sathi.utils.CommonUtils.getTrailStarredString
import `in`.ecomexpress.sathi.utils.CommonUtils.imeiFirstLastFourVisible
import `in`.ecomexpress.sathi.utils.CommonUtils.imeiFullStars
import `in`.ecomexpress.sathi.utils.CommonUtils.imeiHeadStars
import `in`.ecomexpress.sathi.utils.CommonUtils.imeiTailStars
import `in`.ecomexpress.sathi.utils.Constants
import `in`.ecomexpress.sathi.utils.Constants.RVP
import `in`.ecomexpress.sathi.utils.Constants.Secure
import `in`.ecomexpress.sathi.utils.Constants.VERIFY_OTP
import `in`.ecomexpress.sathi.utils.CryptoUtils
import `in`.ecomexpress.sathi.utils.GlobalConstant
import `in`.ecomexpress.sathi.utils.Helper
import `in`.ecomexpress.sathi.utils.LocationHelper
import `in`.ecomexpress.sathi.utils.Logger
import `in`.ecomexpress.sathi.utils.rx.ISchedulerProvider
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Calendar
import java.util.Collections
import java.util.Locale
import java.util.Objects
import javax.inject.Inject

@HiltViewModel
class RvpCommonViewModel @Inject constructor(
    dataManager: IDataManager,
    schedulerProvider: ISchedulerProvider,
    application: Application
) : BaseViewModel<RvpQcNavigator>(dataManager, schedulerProvider, application) {

    private val _qcImageUrls = MutableLiveData<List<String>>()
    val qcImageUrls: LiveData<List<String>> get() = _qcImageUrls
    private val showErrorMessage = MutableLiveData<Pair<String, Boolean>>()
    val showErrorMessageLiveData: LiveData<Pair<String, Boolean>> get() = showErrorMessage
    private lateinit var dialog: Dialog
    private val setOtpSendStatus = MutableLiveData<Boolean>()
    val otpSendStatus: LiveData<Boolean> get() = setOtpSendStatus
    private val _otpVerifyStatus = SingleLiveEvent<Boolean>()
    val otpVerifyStatus: LiveData<Boolean> get() = _otpVerifyStatus
    private fun setOtpVerifyStatus(status: Boolean) {
        _otpVerifyStatus.postValue(status)
    }

    private val _commitApiVerifyStatus = SingleLiveEvent<Boolean>()
    val commitApiVerifyStatus: LiveData<Boolean> get() = _commitApiVerifyStatus
    private fun setCommitApiVerifyStatus(status: Boolean) {
        _commitApiVerifyStatus.postValue(status)
    }

    private val _clearStackEvent = MutableLiveData<Unit>()
    val clearStackEvent: LiveData<Unit> = _clearStackEvent
    val flyerDuplicateCheckStatus: LiveData<Boolean> get() = setFlyerDuplicateCheckStatus
    private val setFlyerDuplicateCheckStatus = MutableLiveData<Boolean>()
    val getImageSaveStatus: LiveData<Boolean> get() = imageSaveStatus
    private val imageSaveStatus = MutableLiveData<Boolean>()
    private val setCapturedImageCount = MutableLiveData(0)
    val capturedImageCount: LiveData<Int> get() = setCapturedImageCount

    private val _rvpWithQC = MutableLiveData<RvpWithQC>()
    val rvpWithQC: LiveData<RvpWithQC> get() = _rvpWithQC
    private val _sampleQuestions = MutableLiveData<ArrayList<SampleQuestion>>()
    val sampleQuestions: LiveData<ArrayList<SampleQuestion>> get() = _sampleQuestions
    var qcWizards = ArrayList<QcWizard>()
    private val setNextScreenStatus = MutableLiveData<Int>()
    val nextScreenStatus: LiveData<Int> get() = setNextScreenStatus
    private val isImageCapture = MutableLiveData<Boolean>()
    val imageCaptureStatus: LiveData<Boolean> get() = isImageCapture
    private val setPhonePayEnabled = MutableLiveData<Boolean>()
    val isPhonePayEnabled: LiveData<Boolean> get() = setPhonePayEnabled
    var isCaptureDone = true

    fun getQcImageUrls(context: Context, awbNumber: Long) {
        val disposable: Disposable = dataManager.qcValuesForAwb
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe(
                { qcValues: List<RvpQualityCheck> ->
                    val imageUrls = qcValues.filter { qc ->
                        qc.awbNo == awbNumber && (qc.qcValue.contains("https") || qc.qcValue.contains(
                            "http"
                        ))
                    }.map { it.qcValue }
                    _qcImageUrls.value = imageUrls
                }, { error ->
                    showMessage(
                        error.message ?: context.getString(R.string.unable_to_read_data_from_local),
                        true
                    )
                }
            )
        compositeDisposable.add(disposable)
    }

    fun onRvpGenerateOtpApiCall(
        context: Context,
        awb: String?,
        drsId: String?,
        allowAlternateNumber: Boolean?
    ) {
        showProgressDialog(context, context.getString(R.string.sending_otp))
        val request = ResendOtpRequest(awb, "OTP", drsId, allowAlternateNumber)
        try {
            compositeDisposable.add(
                dataManager.doResendOtpApiCall(
                    dataManager.authToken,
                    dataManager.ecomRegion,
                    request
                ).doOnSuccess {}.subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui()).subscribe({ response: ResendOtpResponse ->
                    dismissProgressDialog()
                    val error: String = response.description ?: response.response?.description
                    ?: context.getString(
                        R.string.api_response_is_false
                    )
                    if (response.status.equals("true", ignoreCase = true)) {
                        setOtpSendStatus.postValue(true)
                        showMessage(context.getString(R.string.otp_send_successfully), false)
                    } else {
                        setOtpSendStatus.postValue(false)
                        showMessage(error, true)
                        if (error.equals(
                                context.getString(R.string.invalid_authentication_token),
                                ignoreCase = true
                            )
                        ) {
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

    fun onRqcGenerateOtpApiCall(
        context: Activity,
        awb: String?,
        drsId: String?,
        alternateClick: Boolean?,
        generateOtp: Boolean?
    ) {
        showProgressDialog(context, context.getString(R.string.sending_otp))
        try {
            val request = GenerateUDOtpRequest(
                awb,
                Constants.OTP,
                drsId,
                alternateClick,
                dataManager.code,
                generateOtp,
                Constants.RQC
            )
            compositeDisposable.add(dataManager.doGenerateUDOtpApiCall(
                dataManager.authToken,
                dataManager.ecomRegion,
                request
            ).doOnSuccess {}.subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui())
                .subscribe({ response: ResendOtpResponse ->
                    dismissProgressDialog()
                    val error: String =
                        response.description ?: response.response?.description ?: context.getString(
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
                        if (error.equals(
                                context.getString(R.string.invalid_authentication_token),
                                ignoreCase = true
                            )
                        ) {
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

    fun onRvpOtpVerifyApiCall(context: Context, otp: String?, awbNumber: String) {
        try {
            showProgressDialog(context, context.getString(R.string.verifying))
            val request = VerifyOtpRequest(awbNumber, otp, RVP, VERIFY_OTP, Secure)
            compositeDisposable.add(
                dataManager.doVerifyOtpApiCall(
                    dataManager.authToken,
                    dataManager.ecomRegion,
                    request
                ).doOnSuccess {}.subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui()).subscribe({ response: VerifyOtpResponse ->
                    dismissProgressDialog()
                    try {
                        val error =
                            if ((response.response == null || response.response.description == null)) context.getString(
                                R.string.api_response_is_false
                            ) else response.response.description
                        if (response.status) {
                            setOtpVerifyStatus(true)
                        } else {
                            setOtpVerifyStatus(false)
                            showMessage(error, true)
                            if (error.equals(
                                    context.getString(R.string.invalid_authentication_token),
                                    ignoreCase = true
                                )
                            ) {
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
            compositeDisposable.add(dataManager.doVerifyUDOtpDRSApiCall(
                dataManager.authToken,
                dataManager.ecomRegion,
                request
            ).doOnSuccess {}.subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui())
                .subscribe({ response: VerifyOTPResponse ->
                    dismissProgressDialog()
                    val error: String =
                        response.description ?: response.response?.description ?: context.getString(
                            R.string.api_response_is_false
                        )
                    if (response.status.equals("true", ignoreCase = true)) {
                        setOtpVerifyStatus(true)
                    } else {
                        setOtpVerifyStatus(false)
                        showMessage(error, true)
                        if (error.equals(
                                context.getString(R.string.invalid_authentication_token),
                                ignoreCase = true
                            )
                        ) {
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

    fun doVoiceOTPApiCall(context: Context, awb: String, drsId: String, shipmentType: String) {
        try {
            showProgressDialog(context, context.getString(R.string.sending_otp))
            val voiceOtpRequest = VoiceOTP()
            voiceOtpRequest.awb = awb
            voiceOtpRequest.drs_id = drsId
            voiceOtpRequest.product_type = shipmentType
            voiceOtpRequest.employee_code = dataManager.emp_code
            voiceOtpRequest.message_type = Constants.OTP
            compositeDisposable.add(dataManager.doVoiceOtpApiCall(
                dataManager.authToken,
                dataManager.ecomRegion,
                voiceOtpRequest
            ).doOnSuccess {}.subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui())
                .subscribe({ response: VoiceOTPResponse ->
                    dismissProgressDialog()
                    try {
                        val error = response.description
                            ?: context.getString(R.string.api_response_is_false)
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

    fun doRvpFlyerDuplicateCheckApiCall(
        context: Context,
        scannedValue: String?,
        drsId: String?,
        awbNumber: String
    ) {
        try {
            showProgressDialog(context, context.getString(R.string.verifying))
            val rvpFlyerDuplicateCheckRequest = RvpFlyerDuplicateCheckRequest().apply {
                this.awb = awbNumber
                this.drs_id = drsId
                this.ref_packaging_barcode = scannedValue
            }
            compositeDisposable.add(
                dataManager.doRvpflyerDuplicateCheck(
                    dataManager.authToken,
                    dataManager.ecomRegion,
                    rvpFlyerDuplicateCheckRequest
                ).observeOn(schedulerProvider.ui()).subscribeOn(schedulerProvider.io())
                    .subscribe({ rvpFlyerDuplicateCheckResponse: RvpFlyerDuplicateCheckResponse ->
                        dismissProgressDialog()
                        try {
                            if (rvpFlyerDuplicateCheckResponse.status.equals(
                                    "true",
                                    ignoreCase = true
                                )
                            ) {
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

    fun uploadImageToServer(
        context: Context,
        imageName: String,
        imageUri: String,
        imageCode: String,
        awbNo: String,
        drsId: String
    ) {
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
            val finalImageType: RequestBody =
                RequestBody.create(MultipartBody.FORM, GlobalConstant.ImageTypeConstants.OTHERS)
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
            compositeDisposable.add(dataManager.doImageUploadApiCall(
                dataManager.authToken,
                dataManager.ecomRegion,
                Constants.OTHERS,
                headers,
                map,
                fileToUpload
            ).doOnSuccess { }.subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui())
                .subscribe({ imageUploadResponse: ImageUploadResponse ->
                    dismissProgressDialog()
                    try {
                        if (imageUploadResponse.status.equals("Success", ignoreCase = true)) {
                            saveImageInLocalDB(
                                context,
                                awbNo,
                                drsId,
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

    fun saveImageInLocalDB(
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
            imageModel.imageType = GlobalConstant.ImageTypeConstants.OTHERS
            imageModel.date = Calendar.getInstance().timeInMillis
            imageModel.shipmentType = GlobalConstant.ShipmentTypeConstants.RVP
            compositeDisposable.add(dataManager.saveImage(imageModel)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui()).subscribe({
                    dismissProgressDialog()
                    imageSaveStatus.value = true
                    handleImageCaptureCount(false)
                }, { throwable: Throwable? ->
                    dismissProgressDialog()
                    imageSaveStatus.value = false
                    showMessage("Error: ${throwable?.localizedMessage}", true)
                }
                ))
        } catch (e: Exception) {
            e.printStackTrace()
            dismissProgressDialog()
            showMessage("Exception: ${e.localizedMessage}", true)
        }
    }

    fun handleImageCaptureCount(isImageDeleted: Boolean) {
        setCapturedImageCount.value =
            (setCapturedImageCount.value ?: 0) + if (isImageDeleted) -1 else 1
    }

    fun addImageListAndDoCommitApiCall(
        context: Context,
        rvpCommit: RvpCommit,
        isInternetConnected: Boolean
    ) {
        val compositeDisposable = CompositeDisposable()
        try {
            compositeDisposable.add(
                dataManager.getImages(rvpCommit.awb).subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.io()).subscribe({ imageModels ->
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

    @SuppressLint("CheckResult")
    private fun startCommitApi(context: Context, rvpCommit: RvpCommit) {
        showProgressDialog(context, context.getString(R.string.uploading_commit_data))
        val compositeDisposable = CompositeDisposable()
        val tokens = hashMapOf<String, String>()
        tokens[Constants.TOKEN] = dataManager.authToken
        tokens[Constants.EMP_CODE] = dataManager.code
        try {
            compositeDisposable.add(dataManager.doRVPUndeliveredCommitApiCall(
                dataManager.authToken,
                dataManager.ecomRegion,
                tokens,
                rvpCommit
            ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .doFinally { dismissProgressDialog() }.subscribe({ rvpCommitResponse ->
                if (rvpCommitResponse.status) {
                    val shipmentStatus = if (rvpCommitResponse.response.shipment_status.equals(
                            Constants.RVPUNDELIVERED,
                            ignoreCase = true
                        )
                    ) {
                        Constants.SHIPMENT_UNDELIVERED_STATUS
                    } else {
                        Constants.SHIPMENT_DELIVERED_STATUS
                    }
                    compositeDisposable.add(
                        dataManager.updateRvpStatus(
                            rvpCommit.drsId + rvpCommit.awb,
                            shipmentStatus
                        ).subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                            .flatMap { dataManager.deleteSyncedImage(rvpCommitResponse.response.awb_no) }
                            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                            updateSyncStatusInDRSRVpTable(
                                rvpCommitResponse.response.drs_no + rvpCommitResponse.response.awb_no,
                                rvpCommit
                            )
                        }, { throwable ->
                            throwable.printStackTrace()
                            setCommitApiVerifyStatus(false)
                            showMessage("Error: ${throwable.localizedMessage}", true)
                        })
                    )
                } else {
                    setCommitApiVerifyStatus(false)
                    val errorMessage = rvpCommitResponse.response.description
                        ?: context.getString(R.string.api_response_is_false)
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
    private fun updateSyncStatusInDRSRVpTable(compositeKey: String, rvpCommit: RvpCommit) {
        dataManager.updateSyncStatusRVP(compositeKey, 2).subscribeOn(schedulerProvider.io())
            .observeOn(AndroidSchedulers.mainThread()).doFinally {}.subscribe({
            saveCommitApi(rvpCommit, false)
        }, { throwable: Throwable ->
            throwable.printStackTrace()
            setCommitApiVerifyStatus(false)
            showMessage("Error: ${throwable.localizedMessage}", true)
        })
    }

    private fun saveCommitApi(rvpCommit: RvpCommit, isFromOffline: Boolean) {
        val pushApi = PushApi().apply {
            awbNo = rvpCommit.awb.toLong()
            compositeKey = rvpCommit.drsId + rvpCommit.awb
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

        compositeDisposable.add(
            dataManager.saveCommitPacket(pushApi).subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui()).subscribe({
                if (isFromOffline) {
                    compositeDisposable.add(
                        dataManager.updateRvpStatus(
                            rvpCommit.drsId + rvpCommit.awb,
                            Constants.SHIPMENT_DELIVERED_STATUS
                        ).subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui())
                            .subscribe({
                                setCommitApiVerifyStatus(true)
                            }, { throwable ->
                                setCommitApiVerifyStatus(false)
                                throwable.printStackTrace()
                                showMessage(
                                    "Error updating RVP status: ${throwable.localizedMessage}",
                                    true
                                )
                            })
                    )
                } else {
                    setCommitApiVerifyStatus(true)
                }
            }, { throwable ->
                throwable.printStackTrace()
                setCommitApiVerifyStatus(false)
                showMessage("Error: ${throwable.localizedMessage}", true)
            })
        )
    }


    fun clearAppData(context: Context) {
        try {
            compositeDisposable.add(
                dataManager.deleteAllTables().subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui()).subscribe({
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
                        activityContext.stopService(
                            Intent(context, LocationService::class.java)
                        )

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
                })
            )
        } catch (e: Exception) {
            e.printStackTrace()
            showMessage(e.message.toString(), true)
        }
    }

    fun isFeWithinTheDcRange(): Boolean {
        val meter: Int = LocationHelper.getDistanceBetweenPoint(
            dataManager.currentLatitude,
            dataManager.currentLongitude,
            dataManager.getDCLatitude(),
            dataManager.getDCLongitude()
        )
        return (!dataManager.isCounterDelivery && meter < dataManager.dcrange)
    }

    fun showMessage(message: String, isError: Boolean) {
        showErrorMessage.value = Pair(message, isError)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
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

    //------------------------------------------------------  RVP-QC --------------------------------------------------------------------

    fun createQcWizard(
        checkValue: String,
        remark: String,
        scannedData: String,
        sampleQuestion: SampleQuestion,
        rvpQualityCheck: RvpQualityCheck,
        isImageCheck: Boolean
    ) {
        if (qcWizards.size == 0) {
            val qcWizard = QcWizard()
            qcWizard.qcName = sampleQuestion.name
            qcWizard.remarks = remark
            qcWizard.qccheckcode = sampleQuestion.code
            qcWizard.match = "0"
            if (rvpQualityCheck.qcType == GlobalConstant.QcTypeConstants.CHECK || rvpQualityCheck.qcType == GlobalConstant.QcTypeConstants.CHECK_IMAGE) {
                qcWizard.qcValue = "NONE"
                if (checkValue.equals("Yes", true)) {
                    qcWizard.match = "1"
                }
            } else if (rvpQualityCheck.qcType.contains(
                    GlobalConstant.QcTypeConstants.INPUT,
                    true
                )
            ) {
                qcWizard.qcValue = scannedData
                if (getDetailStarredString(rvpQualityCheck, sampleQuestion).equals(
                        scannedData,
                        true
                    )
                ) {
                    qcWizard.match = "1"
                }
            }
            if (isImageCheck) {
                qcWizard.isQcImageFlag = true
            }
            qcWizards.add(qcWizard)
        } else {
            var needUpdate = true
            qcWizards.forEachIndexed { index, _ ->
                if (qcWizards[index].qccheckcode == sampleQuestion.code) {
                    needUpdate = false
                    qcWizards[index].qcName = sampleQuestion.name
                    qcWizards[index].remarks = remark
                    qcWizards[index].qccheckcode = sampleQuestion.code
                    qcWizards[index].match = "0"
                    if (rvpQualityCheck.qcType.equals(
                            GlobalConstant.QcTypeConstants.CHECK,
                            true
                        ) || rvpQualityCheck.qcType.equals(
                            GlobalConstant.QcTypeConstants.CHECK_IMAGE,
                            true
                        )
                    ) {
                        qcWizards[index].qcValue = "NONE"
                        if (checkValue.equals("Yes", true)) {
                            qcWizards[index].match = "1"
                        }
                    } else if (rvpQualityCheck.qcType.contains(
                            GlobalConstant.QcTypeConstants.INPUT,
                            true
                        )
                    ) {
                        qcWizards[index].qcValue = scannedData
                        if (getDetailStarredString(rvpQualityCheck, sampleQuestion).equals(
                                scannedData,
                                true
                            )
                        ) {
                            qcWizards[index].match = "1"
                        }
                    }
                    if (isImageCheck) {
                        qcWizards[index].isQcImageFlag = true
                    }
                }
            }
            if (needUpdate) {
                val qcWizard = QcWizard()
                qcWizard.qcName = sampleQuestion.name
                qcWizard.remarks = remark
                qcWizard.qccheckcode = sampleQuestion.code
                qcWizard.match = "0"
                if (rvpQualityCheck.qcType.equals(
                        GlobalConstant.QcTypeConstants.CHECK,
                        true
                    ) || rvpQualityCheck.qcType.equals(
                        GlobalConstant.QcTypeConstants.CHECK_IMAGE,
                        true
                    )
                ) {
                    qcWizard.qcValue = "NONE"
                    if (checkValue.equals("Yes", true)) {
                        qcWizard.match = "1"
                    }
                } else if (rvpQualityCheck.qcType.contains(
                        GlobalConstant.QcTypeConstants.INPUT,
                        true
                    )
                ) {
                    qcWizard.qcValue = scannedData
                    if (getDetailStarredString(rvpQualityCheck, sampleQuestion).equals(
                            scannedData,
                            true
                        )
                    ) {
                        qcWizard.match = "1"
                    }
                }
                if (isImageCheck) {
                    qcWizard.isQcImageFlag = true
                }
                qcWizards.add(qcWizard)
            }
        }
    }

    fun getRvpDataWithQc(compositeKey: String?) {
        try {
            compositeDisposable.add(
                dataManager.getRvpWithQc(compositeKey).subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui()).subscribe { rvpWithQC: RvpWithQC ->
                    _rvpWithQC.value = rvpWithQC
                    if (rvpWithQC.drsReverseQCTypeResponse.flags.flagMap.is_mdc_rvp_qc_disabled.equals(
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
        val sampleQuestions = ArrayList<SampleQuestion>()
        try {
            for (i in rvpWithQC.value!!.rvpQualityCheckList.indices) {
                val sampleQuestion = SampleQuestion()
                sampleQuestion.code = rvpWithQC.value!!.rvpQualityCheckList[i].qcCode
                sampleQuestion.name = rvpWithQC.value!!.rvpQualityCheckList[i].qcName
                sampleQuestion.imageCaptureSettings =
                    rvpWithQC.value!!.rvpQualityCheckList[i].imageCaptureSettings
                sampleQuestion.instructions = rvpWithQC.value!!.rvpQualityCheckList[i].instructions
                sampleQuestion.verificationMode = rvpWithQC.value!!.rvpQualityCheckList[i].qcType
                sampleQuestions.add(sampleQuestion)
            }
            _sampleQuestions.value = sampleQuestions
        } catch (e: Exception) {
            showMessage("Exception: ${e.localizedMessage}", true)
        }
    }

    private fun getRvpMasterData() {
        try {
            compositeDisposable.add(
                dataManager.getRvpMasterDescriptions(rvpWithQC.value?.rvpQualityCheckList)
                    .subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui())
                    .subscribe { sampleQuestions: List<SampleQuestion> ->
                        try {
                            for (i in rvpWithQC.value!!.rvpQualityCheckList.indices) {
                                for (j in sampleQuestions.indices) {
                                    val code = rvpWithQC.value!!.rvpQualityCheckList[i].qcCode
                                    if (code.equals(sampleQuestions[j].code, ignoreCase = true)) {
                                        Collections.swap(sampleQuestions, i, j)
                                    }
                                    if (sampleQuestions[j].code.startsWith("GEN_ITEM_BRAND_CHECK")) {
                                        if (rvpWithQC.value!!.rvpQualityCheckList[i].qcCode.equals(
                                                "GEN_ITEM_BRAND_CHECK",
                                                ignoreCase = true
                                            )
                                        ) {
                                            val s = sampleQuestions[j].name.replace(
                                                "#COLOR#",
                                                rvpWithQC.value!!.rvpQualityCheckList[i].qcValue
                                            )
                                            sampleQuestions[j].name = s
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

    fun getSampleQuestions(): ArrayList<SampleQuestion>? {
        return sampleQuestions.value
    }

    // phone-pe shipment type find out
    fun getPhonePeShipmentType(awbNo: String?) {
        try {
            compositeDisposable.add(
                dataManager.getPhonePeShipmentType(awbNo).subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui()).subscribe({ value: String? ->
                        if (value.equals("true", ignoreCase = true)) {
                            setPhonePayEnabled.postValue(true)
                        } else {
                            setPhonePayEnabled.postValue(false)
                        }
                    }, {})
            )
        } catch (e: Exception) {
            showMessage("Exception: ${e.localizedMessage}", true)
        }
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

    //----------------------------------------------------------QC-CHECK----------------------------------------------------------------
    fun qcNameQcCheck(sampleQuestion: SampleQuestion, rvpQualityCheck: RvpQualityCheck): String {
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

    fun imageSettingQcCheck(sampleQuestion: SampleQuestion): String {
        var imageCaptureSetting = "Capture Image"
        try {
            if (sampleQuestion.imageCaptureSettings.equals("M", ignoreCase = true)) {
                imageCaptureSetting = "Capture Image*"
            }
        } catch (e: Exception) {
            showMessage("Exception: ${e.localizedMessage}", true)
        }
        return imageCaptureSetting
    }

    //----------------------------------------------------------QC-INPUT----------------------------------------------------------------
    private fun getDetailStarredString(
        rvpQualityCheck: RvpQualityCheck,
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

    fun getDetail(rvpQualityCheck: RvpQualityCheck, sampleQuestions: SampleQuestion): String {
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

    @SuppressLint("CheckResult")
    fun uploadImageServerRvpQc(
        context: Context,
        imageName: String?,
        imageUri: String?,
        imageCode: String?,
        awbNo: Long,
        drsNo: Long,
        bitmap: Bitmap?
    ) {
        try {
            showProgressDialog(context, context.getString(R.string.upload_image))
            val file = imageUri?.let { File(it) }
            val bytes = CryptoUtils.convertImageToBase64(bitmap)
            val mFile = RequestBody.create(MediaType.parse("application/octet-stream"), bytes)
            val fileToUpload = MultipartBody.Part.createFormData("image", file!!.name, mFile)
            val awb_no = RequestBody.create(MediaType.parse("text/plain"), awbNo.toString())
            val drs_no = RequestBody.create(MediaType.parse("text/plain"), drsNo.toString())
            val image_code = RequestBody.create(MediaType.parse("text/plain"), "QC_$imageCode")
            val image_name = RequestBody.create(MediaType.parse("text/plain"), file.name)
            val image_type: RequestBody = RequestBody.create(
                MediaType.parse("text/plain"),
                GlobalConstant.ImageTypeConstants.RVP_QC
            )
            val map: MutableMap<String?, RequestBody?> = HashMap()
            map["image"] = mFile
            map["awb_no"] = awb_no
            map["drs_no"] = drs_no
            map["image_code"] = image_code
            map["image_name"] = image_name
            map["image_type"] = image_type
            val headers: MutableMap<String?, String?> = HashMap()
            headers["token"] = dataManager.authToken
            headers["Accept"] = "application/json"
            try {
                compositeDisposable.add(
                    dataManager.doImageUploadApiCall(
                        dataManager.authToken,
                        dataManager.ecomRegion,
                        GlobalConstant.ImageTypeConstants.RVP_QC,
                        headers,
                        map,
                        fileToUpload
                    ).subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui())
                        .subscribe({ imageUploadResponse: ImageUploadResponse? ->
                            if (imageUploadResponse != null) {
                                try {
                                    saveImageDB(imageName, imageUri, imageCode)
                                    dismissProgressDialog()
                                    if (imageUploadResponse.status.lowercase(Locale.getDefault())
                                            .contains("Success".lowercase(Locale.getDefault()))
                                    ) {
                                        Observable.fromCallable {
                                            dismissProgressDialog()
                                            if (imageUploadResponse.imageId == null) {
                                                uploadImageByteToServer(
                                                    context,
                                                    imageName,
                                                    imageCode,
                                                    awbNo,
                                                    drsNo,
                                                    bitmap
                                                )
                                                showMessage(
                                                    "Image Corrupted While Uploading. Try Again",
                                                    true
                                                )
                                            } else {
                                                imageName?.let {
                                                    saveImageResponse(
                                                        it,
                                                        imageUploadResponse.imageId
                                                    )
                                                }
                                            }
                                            false
                                        }.subscribeOn(schedulerProvider.io())
                                            .observeOn(schedulerProvider.ui()).subscribe {
                                            if (imageUploadResponse.imageId == null) {
                                                uploadImageByteToServer(
                                                    context,
                                                    imageName,
                                                    imageCode,
                                                    awbNo,
                                                    drsNo,
                                                    bitmap
                                                )
                                            }
                                        }
                                    } else {
                                        uploadImageByteToServer(
                                            context,
                                            imageName,
                                            imageCode,
                                            awbNo,
                                            drsNo,
                                            bitmap
                                        )
                                        dismissProgressDialog()
                                    }
                                } catch (e: Exception) {
                                    dismissProgressDialog()
                                    showMessage("Exception: ${e.localizedMessage}", true)
                                }
                            } else {
                                uploadImageByteToServer(
                                    context,
                                    imageName,
                                    imageCode,
                                    awbNo,
                                    drsNo,
                                    bitmap
                                )
                                dismissProgressDialog()
                            }
                        }, { throwable: Throwable ->
                            dismissProgressDialog()
                            showMessage("Throwable: ${throwable.localizedMessage}", true)
                        }
                        )
                )
            } catch (e: Exception) {
                uploadImageByteToServer(context, imageName, imageCode, awbNo, drsNo, bitmap)
                dismissProgressDialog()
                showMessage("Exception: ${e.localizedMessage}", true)
            }
        } catch (e: Exception) {
            uploadImageByteToServer(context, imageName, imageCode, awbNo, drsNo, bitmap)
            dismissProgressDialog()
            showMessage("Exception: ${e.localizedMessage}", true)
        }
    }

    private fun uploadImageByteToServer(
        context: Context,
        imageName: String?,
        imageCode: String?,
        awbNo: Long,
        drsNo: Long,
        bitmap: Bitmap?
    ) {
        try {
            showProgressDialog(context, context.getString(R.string.upload_image))
            val bytearray: String = convertImageToBase64(bitmap)
            val headers: MutableMap<String?, String?> = java.util.HashMap()
            headers["token"] = dataManager.authToken
            headers["Accept"] = "application/json"
            try {
                val byteImageRequest = ByteImageRequest()
                byteImageRequest.image = bytearray
                byteImageRequest.awb_no = awbNo
                byteImageRequest.drs_no = drsNo
                byteImageRequest.image_code = imageCode
                byteImageRequest.image_name = imageName
                byteImageRequest.image_type = "RVP_QC"
                compositeDisposable.add(dataManager.doImageByteUploadApiCall(
                    dataManager.authToken,
                    dataManager.ecomRegion,
                    "RVP_BYTE",
                    headers,
                    byteImageRequest
                ).doOnSuccess {}.subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe({ imageUploadResponse: ImageUploadResponse ->
                        try {
                            dismissProgressDialog()
                            if (imageUploadResponse.imageId == null) {
                                showMessage(
                                    "Image Corrupted while uploading.Please try Again",
                                    true
                                )
                            } else {
                                imageName?.let {
                                    saveImageResponse(
                                        it,
                                        imageUploadResponse.imageId
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            showMessage("Exception: ${e.localizedMessage}", true)
                            dismissProgressDialog()
                        }
                    }, { throwable: Throwable? ->
                        dismissProgressDialog()
                        showMessage("Throwable: ${throwable?.localizedMessage}", true)
                    })
                )
            } catch (e: Exception) {
                dismissProgressDialog()
                showMessage("Exception: ${e.localizedMessage}", true)
            }
        } catch (e: Exception) {
            showMessage("Exception: ${e.localizedMessage}", true)
            dismissProgressDialog()
        }
    }

    fun saveImageDB(name: String?, imageUri: String?, imageCode: String?) {
        try {
            val imageModel = ImageModel()
            imageModel.draNo =
                Objects.requireNonNull<RvpWithQC>(rvpWithQC.value).drsReverseQCTypeResponse.drs.toString()
            imageModel.awbNo =
                Objects.requireNonNull<RvpWithQC>(rvpWithQC.value).drsReverseQCTypeResponse.awbNo.toString()
            imageModel.imageName = name
            imageModel.image = imageUri
            imageModel.imageCode = imageCode
            imageModel.status = 0
            imageModel.imageCurrentSyncStatus = GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO
            imageModel.imageFutureSyncTime = Calendar.getInstance().timeInMillis
            imageModel.imageId = -1
            imageModel.date = Calendar.getInstance().timeInMillis
            imageModel.shipmentType = GlobalConstant.ShipmentTypeConstants.RVP
            imageModel.imageType = GlobalConstant.ImageTypeConstants.RVP_QC
            compositeDisposable.add(
                dataManager.saveImage(imageModel).subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui()).subscribe { })
        } catch (e: Exception) {
            showMessage("Exception: ${e.localizedMessage}", true)
        }
    }

    @SuppressLint("CheckResult")
    private fun saveImageResponse(imageName: String, imageId: Int) {
        try {
            dataManager.updateImageStatus(imageName, 2).blockingSingle()
            dataManager.updateImageID(imageName, imageId).blockingSingle()
        } catch (e: Exception) {
            showMessage("Exception: ${e.localizedMessage}", true)
        }
    }

    private fun convertImageToBase64(bitmap: Bitmap?): String {
        val bytArray = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 0, bytArray) //bm is the bitmap object
        val byteArrayImage = bytArray.toByteArray()
        return Base64.getEncoder().encodeToString(byteArrayImage)
    }

    fun qcDataVerifyForCommitPackets() {
        var isFailed = false
        if (sampleQuestions.value?.size == qcWizards.size) {
            for (qcWizard in qcWizards) {
                if (!qcWizard.isQcImageFlag) {
                    sampleQuestions.value?.forEach { value ->
                        if (qcWizard.qccheckcode.equals(value.code, true)) {
                            isCaptureDone = true
                            isImageCapture.value = true
                            if (value.imageCaptureSettings == "M") {
                                isImageCapture.value = false
                            }
                        }
                    }
                }
                if (qcWizard.match.equals("0", true)) {
                    isFailed = true
                }
            }
            if (isFailed) {
                //failed qc screen
                setNextScreenStatus.postValue(0)
            } else {
                //pass qc screen
                setNextScreenStatus.postValue(1)
            }
        } else {
            showMessage("Please complete all quality check...", true)
        }
    }
}