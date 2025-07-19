package `in`.ecomexpress.sathi.ui.drs.rvp.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.location.Location
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import com.fasterxml.jackson.databind.ObjectMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.ecomexpress.sathi.R
import `in`.ecomexpress.sathi.repo.IDataManager
import `in`.ecomexpress.sathi.repo.local.data.callbridge.CallApiRequest
import `in`.ecomexpress.sathi.repo.local.data.commit.PushApi
import `in`.ecomexpress.sathi.repo.local.data.rvp.RVPCommitResponse
import `in`.ecomexpress.sathi.repo.local.data.rvp.RvpCommit
import `in`.ecomexpress.sathi.repo.local.db.model.ImageModel
import `in`.ecomexpress.sathi.repo.local.db.model.Remark
import `in`.ecomexpress.sathi.repo.remote.RestApiErrorHandler
import `in`.ecomexpress.sathi.repo.remote.model.ErrorResponse
import `in`.ecomexpress.sathi.repo.remote.model.call.Call
import `in`.ecomexpress.sathi.repo.remote.model.consignee_profile.ProfileFound
import `in`.ecomexpress.sathi.repo.remote.model.drs_list.forward.ForwardCallResponse
import `in`.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse
import `in`.ecomexpress.sathi.repo.remote.model.image.ImageUploadResponse
import `in`.ecomexpress.sathi.repo.remote.model.masterdata.MasterDataAttributeResponse
import `in`.ecomexpress.sathi.repo.remote.model.masterdata.RVPReasonCodeMaster
import `in`.ecomexpress.sathi.repo.remote.model.mps.DRSRvpQcMpsResponse
import `in`.ecomexpress.sathi.repo.remote.model.otp.resendotp.GenerateUDOtpRequest
import `in`.ecomexpress.sathi.repo.remote.model.otp.resendotp.ResendOtpResponse
import `in`.ecomexpress.sathi.ui.base.BaseViewModel
import `in`.ecomexpress.sathi.ui.drs.rvp.navigator.IRVPUndeliveredNavigator
import `in`.ecomexpress.sathi.utils.Constants
import `in`.ecomexpress.sathi.utils.CryptoUtils
import `in`.ecomexpress.sathi.utils.GlobalConstant
import `in`.ecomexpress.sathi.utils.LocationHelper
import `in`.ecomexpress.sathi.utils.Logger
import `in`.ecomexpress.sathi.utils.rx.ISchedulerProvider
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.Calendar
import java.util.Locale
import java.util.Objects
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class RVPUndeliveredCancelViewModel @Inject constructor(dataManager: IDataManager, schedulerProvider: ISchedulerProvider, sathiApplication: Application) : BaseViewModel<IRVPUndeliveredNavigator>(dataManager, schedulerProvider, sathiApplication) {

    var isCall = 0
    var flagIsRescheduled: Boolean? = null
    val consigneeContactNumber = ObservableField("")
    val awbNo = ObservableField<String>()
    private var mImageModels: List<ImageModel> = ArrayList()
    private var childGroup: Map<String, MutableList<RVPReasonCodeMaster>>? = null
    private var childRVPReasonCodeMaster: ObservableArrayList<RVPReasonCodeMaster> = ObservableArrayList<RVPReasonCodeMaster>()
    private var parentGroupSpinnerValues: MutableList<String> = ArrayList()
    private var reasonMessageSpinnerValues: MutableList<String> = ArrayList()
    private var callAlertNumber = 0
    private var isCallRecursionDialogRunning = true
    var calldialog: Dialog? = null
    var isStopRecursion = false
    private var requestTime = 0L
    private var responseTime = 0L
    var progress: ProgressDialog? = null

    // Upload image on server and save to DB:-
    @SuppressLint("CheckResult")
    fun uploadImageServer(context: Context?, imageUri: String?, imageName: String, imageCode: String, awbNo: Long, drsNo: Int) {
        showProgress(context)
        try {
            val imageFile = File(imageUri)
            val bytes = CryptoUtils.decryptFile1(imageFile.toString(), Constants.ENC_DEC_KEY)
            val mFile = RequestBody.create(
                MediaType.parse("application/octet-stream"),
                Objects.requireNonNull(bytes)
            )
            val fileToUpload = MultipartBody.Part.createFormData("image", imageFile.name, mFile)
            val awb_no = RequestBody.create(MultipartBody.FORM, awbNo.toString())
            val drs_no = RequestBody.create(MultipartBody.FORM, drsNo.toString())
            val image_code = RequestBody.create(MultipartBody.FORM, imageCode)
            val image_name = RequestBody.create(MultipartBody.FORM, imageName)
            val image_type = RequestBody.create(MultipartBody.FORM, "RVP")
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
                compositeDisposable.add(dataManager.doImageUploadApiCall(dataManager.authToken, dataManager.ecomRegion, "RVP", headers, map, fileToUpload).subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui()).subscribe({
                    imageUploadResponse: ImageUploadResponse? ->
                        dismissDialog()
                        if (imageUploadResponse != null) {
                            try {
                                if (imageUploadResponse.status.equals("Success", ignoreCase = true)) {
                                    saveImageDB(awbNo.toString(), drsNo.toString(), imageUri, imageCode, imageName, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_COMPLETE, imageUploadResponse.imageId)
                                    Observable.fromCallable {
                                        saveImageResponse(imageName, imageUploadResponse.imageId)
                                        false
                                    }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                                        .subscribe { _: Boolean? -> }
                                } else {
                                    val message = imageUploadResponse.description ?: context!!.getString(R.string.image_api_response_false)
                                    navigator.showServerError(message)
                                }
                            } catch (e: Exception) {
                                navigator.showServerError("Exception: ${e.localizedMessage}")
                            }
                        } else {
                            navigator.showServerError("Api response null")
                        }
                    }, { throwable: Throwable? ->
                        dismissDialog()
                        navigator.showServerError("Exception: ${throwable?.localizedMessage}")
                    })
                )
            } catch (e: Exception) {
                dismissDialog()
                navigator.showServerError("Exception: ${e.localizedMessage}")
            }
        } catch (e: Exception) {
            dismissDialog()
            navigator.showServerError("Exception: ${e.localizedMessage}")
        }
    }

    @SuppressLint("CheckResult")
    private fun saveImageResponse(imageName: String, imageId: Int) {
        dataManager.updateImageStatus(imageName, 2)
        dataManager.updateImageID(imageName, imageId)
    }

    fun callApi(nyka: String, failFlag: Boolean, awbNumber: String?, drsId: String?) {
        if (nyka.equals("true", ignoreCase = true)) {
            try {
                requestTime = System.currentTimeMillis()
                setIsLoading(true)
                compositeDisposable.add(dataManager.doForwardCallStatusApiCall(
                    dataManager.authToken,
                    dataManager.ecomRegion,
                    callAlertNumber,
                    dataManager.emp_code,
                    awbNumber,
                    drsId,
                    dataManager.shipperId
                ).observeOn(
                    schedulerProvider.ui()
                ).subscribeOn(schedulerProvider.io()).subscribe(
                    { forwardCallResponse: ForwardCallResponse ->
                        setIsLoading(false)
                        if (forwardCallResponse.status.equals("true", ignoreCase = true)) {
                            navigator.undeliverShipment(failFlag, true)
                            if (calldialog != null) {
                                calldialog!!.dismiss()
                            }
                            navigator.showError(forwardCallResponse.response)
                        } else {
                            responseTime = System.currentTimeMillis()
                            if (forwardCallResponse.isCall_again_required) {
                                navigator.undeliverShipment(failFlag, false)
                            } else {
                                navigator.showError(forwardCallResponse.response)
                                if (dataManager.isCallStatusAPIRecursion) {
                                    callAlertNumber += 1
                                    val timeDifference = responseTime - requestTime
                                    if (timeDifference < dataManager.requestRsponseTime) {
                                        val difference =
                                            dataManager.requestRsponseTime - timeDifference
                                        val handler = Handler()
                                        handler.postDelayed({
                                            callApi(nyka, failFlag, awbNumber, drsId)
                                            if (isCallRecursionDialogRunning) {
                                                navigator.getActivityContext()
                                                    .runOnUiThread {
                                                        showCallAPIDelayDialog(
                                                            nyka,
                                                            failFlag,
                                                            awbNumber,
                                                            drsId
                                                        )
                                                    }
                                            }
                                        }, difference)
                                    } else {
                                        callApi(nyka, failFlag, awbNumber, drsId)
                                        if (isCallRecursionDialogRunning) {
                                            showCallAPIDelayDialog(
                                                nyka,
                                                failFlag,
                                                awbNumber,
                                                drsId
                                            )
                                        }
                                    }
                                } else {
                                    if (isStopRecursion) {
                                        return@subscribe
                                    }
                                    callAlertNumber += 1
                                    showCallAPIDelayDialog(nyka, failFlag, awbNumber, drsId)
                                }
                            }
                        }
                    }, { _: Throwable? -> setIsLoading(false) })
                )
            } catch (e: Exception) {
                navigator.showError(e.localizedMessage)
                setIsLoading(false)
                Logger.e(RVPUndeliveredCancelViewModel::class.java.name, e.message)
            }
        } else {
            navigator.undeliverShipment(failFlag, true)
        }
    }

    // For Group Chooser
    fun onChooseGroupSpinner(pos: Int) {
        try {
            val rvpReasonCodeMasters: List<RVPReasonCodeMaster>? =
                childGroup!![parentGroupSpinnerValues[pos]]
            reasonMessageSpinnerValues.clear()
            childRVPReasonCodeMaster.clear()
            if (rvpReasonCodeMasters != null) {
                childRVPReasonCodeMaster.add(0, selectTemplate)
                reasonMessageSpinnerValues.add(Constants.SELECT)
                for (rvpReasonCodeMaster in rvpReasonCodeMasters) {
                    childRVPReasonCodeMaster.add(rvpReasonCodeMaster)
                    reasonMessageSpinnerValues.add(rvpReasonCodeMaster.reasonMessage)
                }
            }
            navigator.onChooseGroupSpinner(parentGroupSpinnerValues[pos])
            navigator.setReasonMessageSpinnerValues(reasonMessageSpinnerValues)
        } catch (e: Exception) {
            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, e.message)
            navigator.showError(e.message)
        }
    }

    // For Child Chooser
    fun onChooseChildSpinner(pos: Int) {
        navigator.onChooseChildSpinner(childRVPReasonCodeMaster[pos])
    }

    fun createCommitDataAndSaveToDB(rvpCommit: RvpCommit, compositeKey: String, isFromMps: Boolean) {
        try {
            rvpCommit.status = Constants.RVPUNDELIVERED
            rvpCommit.trip_id = dataManager.tripId
            rvpCommit.feEmpCode = dataManager.code
            if (!`in`.ecomexpress.geolocations.Constants.latitude.toString().equals("0.0", ignoreCase = true) && !`in`.ecomexpress.geolocations.Constants.longitude.toString().equals("0.0", ignoreCase = true)) {
                rvpCommit.locationLat = `in`.ecomexpress.geolocations.Constants.latitude.toString()
                rvpCommit.locationLong = `in`.ecomexpress.geolocations.Constants.longitude.toString()
            } else if (!Constants.CURRENT_LATITUDE.equals("0.0", ignoreCase = true) && !Constants.CURRENT_LONGITUDE.equals("0.0", ignoreCase = true)) {
                rvpCommit.locationLat = Constants.CURRENT_LATITUDE
                rvpCommit.locationLong = Constants.CURRENT_LONGITUDE
            } else {
                rvpCommit.locationLat = dataManager.currentLatitude.toString()
                rvpCommit.locationLong = dataManager.currentLongitude.toString()
            }
            if (dataManager.consigneeProfileValue.equals("W", ignoreCase = true)) {
                rvpCommit.flag_of_warning = "W"
            } else {
                rvpCommit.flag_of_warning = "N"
            }
            saveCommitToDatabase(rvpCommit, compositeKey, isFromMps)
        } catch (e: Exception) {
            navigator.showError(e.localizedMessage)
        }
    }

    fun onUndeliveredApiCallRealTime(rvpCommit: RvpCommit, compositeKey: String, isFromMps: Boolean) {
        try {
            compositeDisposable.add(dataManager.getImages(rvpCommit.awb).subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.io()).subscribe({
                imageModels: List<ImageModel> ->
                    mImageModels = imageModels
                    var isAllImageSynced = true
                    for (i in mImageModels.indices) {
                        if (mImageModels[i].imageId <= 0) {
                            isAllImageSynced = false
                            break
                        }
                    }
                    if (isAllImageSynced) {
                        val listImageResponses: MutableList<RvpCommit.ImageData> = ArrayList()
                        for (i in mImageModels.indices) {
                            val imageResponse = RvpCommit.ImageData()
                            imageResponse.imageId = mImageModels[i].imageId.toString()
                            imageResponse.imageKey = mImageModels[i].imageName.toString()
                            listImageResponses.add(imageResponse)
                        }
                        rvpCommit.imageData = listImageResponses
                        rvpCommit.status = Constants.RVPUNDELIVERED
                        rvpCommit.trip_id = dataManager.tripId
                        if (!`in`.ecomexpress.geolocations.Constants.latitude.toString().equals("0.0", ignoreCase = true) && !`in`.ecomexpress.geolocations.Constants.longitude.toString().equals("0.0", ignoreCase = true)) {
                            rvpCommit.locationLat = `in`.ecomexpress.geolocations.Constants.latitude.toString()
                            rvpCommit.locationLong = `in`.ecomexpress.geolocations.Constants.longitude.toString()
                        } else if (!Constants.CURRENT_LATITUDE.equals("0.0", ignoreCase = true) && !Constants.CURRENT_LONGITUDE.equals("0.0", ignoreCase = true)) {
                            rvpCommit.locationLat = Constants.CURRENT_LATITUDE
                            rvpCommit.locationLong = Constants.CURRENT_LONGITUDE
                        } else {
                            rvpCommit.locationLat = dataManager.currentLatitude.toString()
                            rvpCommit.locationLong = dataManager.currentLongitude.toString()
                        }
                        if (dataManager.consigneeProfileValue.equals("W", ignoreCase = true)) {
                            rvpCommit.flag_of_warning = "W"
                        } else {
                            rvpCommit.flag_of_warning = "N"
                        }
                        rvpCommit.feEmpCode = dataManager.code
                        uploadRvpShipment(rvpCommit, compositeKey, isFromMps)
                    } else {
                        createCommitDataAndSaveToDB(rvpCommit, compositeKey, isFromMps)
                    }
                }) { throwable: Throwable ->
                navigator.showError(throwable.localizedMessage)
            })
        } catch (ex: Exception) {
            navigator.showError(ex.localizedMessage)
        }
    }

    @SuppressLint("CheckResult")
    private fun uploadRvpShipment(rvpCommit: RvpCommit, compositeKey: String, isFromMps: Boolean) {
        val tokens = HashMap<String, String>()
        tokens[Constants.TOKEN] = dataManager.authToken
        tokens[Constants.EMP_CODE] = dataManager.code
        compositeDisposable.add(dataManager.doRVPUndeliveredCommitApiCall(dataManager.authToken, dataManager.ecomRegion, tokens, rvpCommit).subscribeOn(schedulerProvider.io()).subscribe({
            rvpCommitResponse: RVPCommitResponse ->
                if (rvpCommitResponse.status) {
                    var finalCompositeKey = ""
                    val shipmentStatus: Int =
                        if (rvpCommitResponse.response.shipment_status.equals(Constants.RVPUNDELIVERED, ignoreCase = true)) {
                            Constants.SHIPMENT_UNDELIVERED_STATUS
                        } else {
                            Constants.SHIPMENT_DELIVERED_STATUS
                        }
                    try {
                        rvpCommit.drsId?.toInt()?.let { drsId ->
                            if (rvpCommit.attemptType.equals("RQC", ignoreCase = true) || isFromMps) {
                                deleteQCDataAfterCommitShipment(drsId, rvpCommit.awb.toLong(), isFromMps)
                            }
                        }
                        finalCompositeKey = rvpCommitResponse.response.drs_no + rvpCommitResponse.response.awb_no
                    } catch (e: Exception) {
                        Logger.e(RVPUndeliveredCancelViewModel::class.java.name, e.message)
                    }
                    if(isFromMps){
                        dataManager.updateRvpMpsStatus(finalCompositeKey, shipmentStatus).subscribe({ _: Boolean? ->
                            updateSyncStatusInDB(finalCompositeKey, true)
                            // Setting call preference after sync:-
                            dataManager.setCallClicked(rvpCommitResponse.response.awb_no + "RVPCall", true)
                            compositeDisposable.add(dataManager.deleteSyncedImage(rvpCommitResponse.response.awb_no).subscribe { _: Boolean? -> })
                        }) { _: Throwable? ->
                            saveCommitToDatabase(rvpCommit, finalCompositeKey, true)
                            setIsLoading(false)
                        }
                    } else{
                        dataManager.updateRvpStatus(finalCompositeKey, shipmentStatus).subscribe({ _: Boolean? ->
                            updateSyncStatusInDB(finalCompositeKey, false)
                            // Setting call preference after sync:-
                            dataManager.setCallClicked(rvpCommitResponse.response.awb_no + "RVPCall", true)
                            compositeDisposable.add(dataManager.deleteSyncedImage(rvpCommitResponse.response.awb_no).subscribe { _: Boolean? -> })
                        }) { _: Throwable? ->
                            saveCommitToDatabase(rvpCommit, finalCompositeKey, false)
                            setIsLoading(false)
                        }
                    }
                } else{
                    navigator.showError("Undelivered Api response false")
                }
            }, { throwable: Throwable ->
                setIsLoading(false)
                saveCommitToDatabase(rvpCommit, compositeKey, isFromMps)
                    navigator.showError(throwable.message)
                }
            )
        )
    }

    private fun deleteQCDataAfterCommitShipment(drs: Int, awbNo: Long, isFromMps: Boolean) {
        if(isFromMps){
            compositeDisposable.add(dataManager.deleteMpsQcDataFromQcItemTable(drs, awbNo).subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.io()).subscribe({ }, { }))
        } else{
            compositeDisposable.add(dataManager.deleteQCData(drs, awbNo).subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.io()).subscribe({ }, { }))
        }
    }

    private fun updateSyncStatusInDB(compositeKey: String, isFromMps: Boolean) {
        if(isFromMps){
            compositeDisposable.add(dataManager.updateSyncStatusMps(compositeKey, 2).subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.io()).subscribe({ _: Boolean? -> navigator.onSubmitSuccess() }) { _: Throwable? -> })
        } else{
            compositeDisposable.add(dataManager.updateSyncStatusRVP(compositeKey, 2).subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.io()).subscribe({ _: Boolean? -> navigator.onSubmitSuccess() }) { _: Throwable? -> })
        }
    }

    private fun updateShipmentStatus(compositeKey: String, isFromMps: Boolean) {
        try {
            if(isFromMps){
                compositeDisposable.add(dataManager.updateRvpMpsStatus(compositeKey, 3).observeOn(schedulerProvider.ui()).subscribeOn(schedulerProvider.io()).subscribe { _: Boolean? -> navigator.onSubmitSuccess() })
            } else{
                compositeDisposable.add(dataManager.updateRvpStatus(compositeKey, 3).observeOn(schedulerProvider.ui()).subscribeOn(schedulerProvider.io()).subscribe { _: Boolean? -> navigator.onSubmitSuccess() })
            }
        } catch (ex: Exception) {
            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, ex.message)
        }
    }

    fun onSubmitClick() {
        navigator.OnSubmitClick()
    }

    fun onGenerateOTPClick() {
        navigator.onGenerateOtpClick()
    }

    fun onBackClick() {
        navigator.onBackClick()
    }

    fun saveImageDB(awb: String?, drsId: String?, imageUri: String?, imageCode: String?, name: String?, imageSyncStatus: Int, imageId: Int) {
        try {
            val imageModel = ImageModel()
            imageModel.draNo = drsId
            imageModel.awbNo = awb
            imageModel.imageName = name
            imageModel.image = imageUri
            imageModel.imageCode = imageCode
            imageModel.status = Constants.SYNCED
            imageModel.imageCurrentSyncStatus = imageSyncStatus
            imageModel.imageFutureSyncTime = Calendar.getInstance().timeInMillis
            imageModel.imageId = imageId
            imageModel.imageType = GlobalConstant.ImageTypeConstants.OTHERS
            imageModel.date = Calendar.getInstance().timeInMillis
            imageModel.shipmentType = GlobalConstant.ShipmentTypeConstants.RVP
            compositeDisposable.add(dataManager.saveImage(imageModel).subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui()).subscribe { _: Boolean? -> navigator.setBitmap() })
        } catch (e: Exception) {
            navigator.showError(e.localizedMessage)
        }
    }

    private fun saveCommitToDatabase(rvpCommit: RvpCommit, compositeKey: String, isFromMps: Boolean) {
        val pushApi = PushApi()
        pushApi.awbNo = rvpCommit.awb.toLong()
        pushApi.compositeKey = compositeKey
        pushApi.authtoken = dataManager.authToken
        try {
            pushApi.requestData = ObjectMapper().writeValueAsString(rvpCommit)
            pushApi.shipmentStatus = 0
            pushApi.shipmentCaterogy = Constants.RVPCOMMIT
            pushApi.isRvp_mps = isFromMps
        } catch (e: Exception) {
            navigator.showError(e.message)
        }
        try {
            compositeDisposable.add(dataManager.saveCommitPacket(pushApi).subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui()).subscribe { _: Boolean? ->
                updateShipmentStatus(pushApi.compositeKey.toString(), isFromMps)
                updateRVPCallAttemptedWithZero(pushApi.awbNo.toString())
                val isCallAttempted = getIsCallAttempted(rvpCommit.awb, isFromMps)
                if (isCallAttempted == 0) {
                    Constants.shipment_undelivered_count++
                }
            })
        } catch (e: Exception) {
            navigator.showError(e.message)
        }
    }

    val lat: String get() = `in`.ecomexpress.geolocations.Constants.latitude.toString()

    val lng: String get() = `in`.ecomexpress.geolocations.Constants.longitude.toString()

    fun getIsCallAttempted(awb: String, isFromMps: Boolean): Int {
        try {
            if(isFromMps){
                compositeDisposable.add(dataManager.getIsMpsCallAttempted(awb.toLong()).observeOn(schedulerProvider.ui()).subscribeOn(schedulerProvider.io()).subscribe { isCallAttempted: Long ->
                    try {
                        isCall = isCallAttempted.toString().toInt()
                        navigator.isCallAttempted(isCall)
                    } catch (e: Exception) {
                        Logger.e(RVPUndeliveredCancelViewModel::class.java.name, e.message)
                    }
                })
            } else{
                compositeDisposable.add(dataManager.getisRVPCallattempted(awb.toLong()).observeOn(schedulerProvider.ui()).subscribeOn(schedulerProvider.io()).subscribe { isCallAttempted: Long ->
                    try {
                        isCall = isCallAttempted.toString().toInt()
                        navigator.isCallAttempted(isCall)
                    } catch (e: Exception) {
                        Logger.e(RVPUndeliveredCancelViewModel::class.java.name, e.message)
                    }
                })
            }
        } catch (e: Exception) {
            navigator.showError(e.message)
        }
        return isCall
    }

    fun updateRVPCallAttempted(awb: String) {
        try {
            compositeDisposable.add(dataManager.updateRVPCallAttempted(java.lang.Long.valueOf(awb), Constants.callAttempted).observeOn(schedulerProvider.ui()).subscribeOn(schedulerProvider.io()).subscribe { _: Boolean? -> })
        } catch (e: Exception) {
            navigator.showError(e.message)
        }
    }

    private fun updateRVPCallAttemptedWithZero(awb: String) {
        try {
            compositeDisposable.add(dataManager.updateRVPCallAttempted(java.lang.Long.valueOf(awb), 0).observeOn(schedulerProvider.ui()).subscribeOn(schedulerProvider.io()).subscribe { _: Boolean? -> })
        } catch (e: Exception) {
            navigator.showError(e.message)
        }
    }

    fun makeCallBridgeApiCall(callBridgeKey: String?, awb: String, drsid: String, type: String) {
        val request = CallApiRequest()
        request.awb = awb
        request.cb_api_key = callBridgeKey
        request.drs_id = drsid
        try {
            compositeDisposable.add(dataManager.doCallBridgeApiCall(dataManager.authToken, dataManager.ecomRegion, request).observeOn(schedulerProvider.ui()).subscribeOn(schedulerProvider.io()).subscribe({
                callApiResponse: ErrorResponse? ->
                    if (callApiResponse != null) {
                        try {
                            if (callApiResponse.isStatus) {
                                if (type.equals(Constants.RVP, ignoreCase = true)) {
                                    updateRVPCallAttempted(awb)
                                }
                            }
                            navigator.onHandleError(callApiResponse)
                        } catch (e: Exception) {
                            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, e.message)
                        }
                    }
                }, { throwable: Throwable ->
                    navigator.showErrorMessage(Objects.requireNonNull(throwable.message)!!.contains("HTTP 500 "))
                })
            )
        } catch (e: Exception) {
            navigator.showError(e.message)
        }
    }

    fun logoutLocal() {
        try {
            dataManager.tripId = ""
            dataManager.setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT)
            clearAppData()
        } catch (e: Exception) {
            navigator.showError(e.message)
        }
    }

    private fun clearAppData() {
        try {
            compositeDisposable.add(dataManager.deleteAllTables().subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui()).subscribe { _: Boolean? ->
                try {
                    dataManager.clearPrefrence()
                    dataManager.setUserAsLoggedOut()
                } catch (e: Exception) {
                    Logger.e(RVPUndeliveredCancelViewModel::class.java.name, e.message)
                }
                navigator.clearStack()
            })
        } catch (e: Exception) {
            navigator.showError(e.message)
        }
    }

    fun loadRvpQcShipmentDetails(compositeKey: String?, isFromMps: Boolean) {
        try {
            if(isFromMps){
                compositeDisposable.add(dataManager.loadMpsShipmentDetailsFromDB(compositeKey).subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui()).subscribe({
                    drsMpsTypeResponse: DRSRvpQcMpsResponse ->
                        navigator.onRVPItemFetched(null, drsMpsTypeResponse)
                    }, { throwable: Throwable ->
                        Logger.e(RVPUndeliveredCancelViewModel::class.java.name, throwable.message)
                    })
                )
            } else{
                compositeDisposable.add(dataManager.getRVPDRS(compositeKey).subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui()).subscribe({
                    drsReverseQCTypeResponse: DRSReverseQCTypeResponse ->
                        navigator.onRVPItemFetched(drsReverseQCTypeResponse, null)
                    }, { throwable: Throwable ->
                        Logger.e(RVPUndeliveredCancelViewModel::class.java.name, throwable.message)
                    })
                )
            }
        } catch (e: Exception) {
            navigator.showError("awb   is " + e.message)
        }
    }

    fun checkMeterRange(awbNumber: Long) {
        try {
            getProfileLatLng(awbNumber)
        } catch (e: Exception) {
            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, e.message)
            navigator.showError(e.message)
        }
    }

    private fun getProfileLatLng(awbNumber: Long) {
        try {
            compositeDisposable.add(dataManager.getProfileLat(awbNumber).subscribeOn(schedulerProvider.io()).subscribe(Consumer {
                profileFound: ProfileFound? ->
                    try {
                        if (profileFound != null && profileFound.awb_number != 0L) {
                            var consigneeLatitude = 0.0
                            var consigneeLongitude = 0.0
                            try {
                                consigneeLatitude = profileFound.delivery_latitude
                                consigneeLongitude = profileFound.delivery_longitude
                            } catch (e: Exception) {
                                Logger.e(RVPUndeliveredCancelViewModel::class.java.name, e.message)
                            }
                            if (consigneeLatitude == 0.0 || consigneeLongitude == 0.0) {
                                navigator.setConsigneeDistance(0)
                                return@Consumer
                            }
                            val meter: Int = LocationHelper.getDistanceBetweenPoint(consigneeLatitude, consigneeLongitude, dataManager.currentLatitude, dataManager.currentLongitude)
                            navigator.setConsigneeDistance(meter)
                        }
                    } catch (e: Exception) {
                        Logger.e(RVPUndeliveredCancelViewModel::class.java.name, e.message)
                    }
                }) { obj: Throwable -> obj.printStackTrace() }
            )
        } catch (e: Exception) {
            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, e.message)
        }
    }

    val consigneeProfiling: Unit get() {
        val enable = dataManager.consigneeProfiling
        navigator.setConsingeeProfiling(enable)
    }

    fun checkIsFeIsDcRange(latitude: Double, longitude: Double, dcLatitude: Double, dcLongitude: Double): Boolean {
        try {
            if (dataManager.tripId.equals("0", ignoreCase = true)) {
                return false
            }
            val locationA = Location("point A")
            locationA.latitude = latitude
            locationA.longitude = longitude
            val locationB = Location("point B")
            locationB.latitude = dcLatitude
            locationB.longitude = dcLongitude
            val distance = locationA.distanceTo(locationB)
            return distance <= dataManager.undeliverConsigneeRANGE
        } catch (e: Exception) {
            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, e.message)
        }
        return false
    }

    private fun showCallAPIDelayDialog(
        nyka: String,
        failFlag: Boolean,
        awbNumber: String?,
        drsId: String?
    ) {
        isCallRecursionDialogRunning = false
        navigator.getActivityContext().runOnUiThread {
            val callAlertDialog = AlertDialog.Builder(navigator.getActivityContext()).setMessage("Getting call status...").setPositiveButton("Wait", null)
            calldialog = callAlertDialog.create()
            calldialog?.setOnShowListener(object : DialogInterface.OnShowListener {
                private val AUTO_DISMISS_MILLIS = dataManager.callStatusApiInterval.toLong() * 1000
                override fun onShow(dialog: DialogInterface) {
                    val defaultButton =
                        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                    defaultButton.isEnabled = false
                    val negativeButtonText = defaultButton.text
                    object : CountDownTimer(AUTO_DISMISS_MILLIS, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            isCallRecursionDialogRunning = false
                            defaultButton.text = String.format(
                                Locale.getDefault(),
                                "%s (%d)",
                                negativeButtonText,
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1
                            )
                        }

                        override fun onFinish() {
                            if ((calldialog as AlertDialog?)!!.isShowing) {
                                calldialog?.dismiss()
                            }
                            if (dataManager.isCallStatusAPIRecursion) {
                                dataManager.setCallAPIRecursion(false)
                                isStopRecursion = true
                                navigator.undeliverShipment(failFlag, false)
                            } else {
                                if (callAlertNumber > 1) {
                                    navigator.undeliverShipment(failFlag, false)
                                } else {
                                    callApi(nyka, failFlag, awbNumber, drsId)
                                }
                            }
                        }
                    }.start()
                }
            })
            calldialog?.setCancelable(false)
            calldialog?.show()
        }
    }

    val counterDeliveryRange: Int
        get() {
            val currentLatitude = dataManager.currentLatitude
            val currentLongitude = dataManager.currentLongitude
            val dcLatitude = dataManager.getDCLatitude()
            val dcLongitude = dataManager.getDCLongitude()
            return LocationHelper.getDistanceBetweenPoint(
                currentLatitude,
                currentLongitude,
                dcLatitude,
                dcLongitude
            )
        }

    fun getCallStatus(awb: Long, drs: Int) {
        try {
            val compositeDisposable = CompositeDisposable()
            compositeDisposable.add(dataManager.getCallStatus(awb, drs).subscribeOn(
                schedulerProvider.io()
            ).observeOn(
                schedulerProvider.ui()
            ).subscribe(
                { aBoolean: Boolean? -> dataManager.setIsCallAlreadyDone(aBoolean) }) { })
        } catch (e: Exception) {
            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, e.message)
        }
    }

    fun saveCallStatus(awb: Long, drs: Int) {
        try {
            val call = Call()
            call.drs = drs
            call.awb = awb
            call.isStatus = true
            val compositeDisposable = CompositeDisposable()
            compositeDisposable.add(dataManager.saveCallStatus(call).subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui()).subscribe({ }) { })
        } catch (e: Exception) {
            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, e.message)
        }
    }

    var udOtpCommitStatus = "NONE"
    var udOtpCommitStatusField = ObservableField("NONE")
    var rdOtpCommitStatus = "NONE"
    var rdOtpCommitStatusField = ObservableField("NONE")
    fun onVerifyClick() {
        navigator.onVerifyClick()
    }

    fun onResendClick() {
        navigator.onResendClick()
    }

    @SuppressLint("SetTextI18n")
    fun showCallAndSmsDialog(consigneealternatemobile: String, checkcall: String) {
        val dialog = Dialog(navigator.getActivityContext(), R.style.RoundedCornersDialog)
        dialog.setContentView(R.layout.dialog_sms_call)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(Objects.requireNonNull(dialog.window)?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        val sms = dialog.findViewById<Button>(R.id.bt_sms)
        val call = dialog.findViewById<Button>(R.id.bt_call)
        val crossDialog = dialog.findViewById<ImageView>(R.id.crssdialog)
        val txtCall = dialog.findViewById<TextView>(R.id.txtcall)
        val btSmsAlternate = dialog.findViewById<Button>(R.id.bt_sms_alternate)
        if (consigneealternatemobile.isNotEmpty()) {
            btSmsAlternate.visibility = View.VISIBLE
            btSmsAlternate.text = "SMS on Alternate No. $consigneealternatemobile"
        }
        if (checkcall.isEmpty()) {
            call.visibility = View.GONE
            txtCall.visibility = View.INVISIBLE
        }
        btSmsAlternate.setOnClickListener {
            dialog.dismiss()
            navigator.resendSms(true)
        }
        crossDialog.setOnClickListener { dialog.dismiss() }
        sms.setOnClickListener {
            dialog.dismiss()
            navigator.resendSms(false)
        }
        call.setOnClickListener {
            dialog.dismiss()
            navigator.VoiceCallOtp()
        }
        dialog.show()
        dialog.window!!.attributes = lp
    }

    var dialog: ProgressDialog? = null
    private var counterSkip = 0
    fun onGenerateOtpApiCall(
        context: Activity?,
        awb: String?,
        drsId: String?,
        alternateClick: Boolean?,
        messageType: String?,
        generateOtp: Boolean?,
        shipmentType: String?
    ) {
        dialog = ProgressDialog(context)
        dialog!!.setMessage("Sending OTP....")
        dialog!!.show()
        dialog!!.isIndeterminate = false
        val request = GenerateUDOtpRequest(
            awb,
            messageType,
            drsId,
            alternateClick,
            dataManager.code,
            generateOtp,
            shipmentType
        )
        val timeStamp = System.currentTimeMillis()
        writeRestAPIRequst(timeStamp, request)
        try {
            compositeDisposable.add(dataManager.doGenerateUDOtpApiCall(
                dataManager.authToken,
                dataManager.ecomRegion,
                request
            ).doOnSuccess { resendOtpResponse: ResendOtpResponse ->
                writeRestAPIResponse(timeStamp, resendOtpResponse)
                Log.d(ContentValues.TAG, resendOtpResponse.toString())
            }.subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui()).subscribe(
                { response: ResendOtpResponse ->
                    if (dialog!!.isShowing) dialog!!.dismiss()
                    if (response.status == "true") {
                        counterSkip++
                        navigator.onOtpResendSuccess(
                            response.status,
                            response.description
                        )
                    } else {
                        if (response.response != null) {
                            if (response.response.statusCode
                                    .equals("107", ignoreCase = true)
                            ) {
                                navigator.doLogout(response.response.description)
                            }
                        } else if (response.description.equals("Max Attempt Reached")) {
                            counterSkip++
                            navigator.onOtpResendSuccess(
                                response.status,
                                response.description
                            )
                        }
                        navigator.showError(response.description)
                    }
                }, { throwable: Throwable? ->
                    if (dialog!!.isShowing) dialog!!.dismiss()
                    val error: String
                    try {
                        error = RestApiErrorHandler(throwable).errorDetails.getEResponse()
                            .description
                        navigator.showError(error)
                    } catch (e: Exception) {
                        if (dialog!!.isShowing) dialog!!.dismiss()
                        Logger.e(RVPUndeliveredCancelViewModel::class.java.name, e.message)
                        navigator.showError(e.message)
                    }
                })
            )
        } catch (e: Exception) {
            if (dialog!!.isShowing) dialog!!.dismiss()
            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, e.message)
            navigator.showError(e.message)
        }
    }

    fun addRemarks(remark: Remark?) {
        try {
            compositeDisposable.add(dataManager.insertRemark(remark).observeOn(schedulerProvider.io()).subscribeOn(schedulerProvider.io()).subscribe({ }) { obj: Throwable -> obj.printStackTrace() })
        } catch (e: Exception) {
            navigator.showError(e.message)
        }
    }

    fun showProgress(context: Context?) {
        progress = ProgressDialog(context)
        progress!!.isIndeterminate = true
        progress!!.setMessage("Uploading Image...")
        progress!!.setCancelable(false)
        progress!!.show()
    }

    fun dismissDialog() {
        try {
            if (progress != null) {
                progress!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        val selectTemplate: RVPReasonCodeMaster get() {
            val reasonSelectReason = RVPReasonCodeMaster()
            reasonSelectReason.reasonCode = -1
            reasonSelectReason.reasonMessage = Constants.SELECT
            val masterDataAttributeResponse = MasterDataAttributeResponse()
            masterDataAttributeResponse.setcALLM(false)
            masterDataAttributeResponse.setiMG(false)
            masterDataAttributeResponse.setoTP(false)
            masterDataAttributeResponse.setrCHD(false)
            masterDataAttributeResponse.isSECURED = false
            reasonSelectReason.masterDataAttributeResponse = masterDataAttributeResponse
            return reasonSelectReason
        }
    }
}