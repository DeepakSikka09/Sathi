package `in`.ecomexpress.sathi.ui.drs.rvp_new.viewmodel

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
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.LatLng
import com.google.maps.model.TravelMode
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.ecomexpress.sathi.R
import `in`.ecomexpress.sathi.repo.IDataManager
import `in`.ecomexpress.sathi.repo.local.data.callbridge.CallApiRequest
import `in`.ecomexpress.sathi.repo.local.data.commit.PushApi
import `in`.ecomexpress.sathi.repo.local.data.rvp.RVPCommitResponse
import `in`.ecomexpress.sathi.repo.local.data.rvp.RvpCommit
import `in`.ecomexpress.sathi.repo.local.db.model.ImageModel
import `in`.ecomexpress.sathi.repo.local.db.model.RVPUndeliveredReasonCodeList
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
import `in`.ecomexpress.sathi.repo.remote.model.otp.resendotp.GenerateUDOtpRequest
import `in`.ecomexpress.sathi.repo.remote.model.otp.resendotp.ResendOtpResponse
import `in`.ecomexpress.sathi.ui.base.BaseViewModel
import `in`.ecomexpress.sathi.ui.drs.rvp.undelivered.IRVPUndeliveredNavigator
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
class RVPUndeliveredCancelViewModel @Inject constructor(
    dataManager: IDataManager,
    schedulerProvider: ISchedulerProvider,
    sathiApplication: Application
) : BaseViewModel<IRVPUndeliveredNavigator>(dataManager, schedulerProvider, sathiApplication) {
    var isCall = 0
    private var rvpUndeliveredReasonCodeList: List<RVPUndeliveredReasonCodeList> = ArrayList()
    private var stReasonMessage: String? = null
    var flagIsRescheduled: Boolean? = null
    private var flagIsCallBridge: Boolean? = null
    private var flagIsOTPEnabled: Boolean? = null
    private var flagIsImgEnabled: Boolean? = null
    private var rvpCommit: RvpCommit? = null
    val consigneeContactNumber = ObservableField("")
    val awbNo = ObservableField<String>()
    private var rvpUndeliveredReasonCodeLists: MutableList<RVPUndeliveredReasonCodeList> = ArrayList()
    private var stReasonCode: Int? = null
    private var mImageModels: List<ImageModel> = ArrayList()
    private var childGroup: Map<String, MutableList<RVPReasonCodeMaster>>? = null
    private var childRVPReasonCodeMaster: ObservableArrayList<RVPReasonCodeMaster> =
        ObservableArrayList<RVPReasonCodeMaster>()
    private val spinnerReasonValues = ObservableArrayList<String?>()
    private var parentGroupSpinnerValues: MutableList<String> = ArrayList()
    private var reasonMessageSpinnerValues: MutableList<String> = ArrayList()
    private var call_alert_number = 0
    private var isCallRecursionDialogRunning = true
    var calldialog: Dialog? = null
    var isStopRecursion = false
    private var request_time = 0L
    private var response_time = 0L
    var progress: ProgressDialog? = null
    fun onChooseReasonSpinner(pos: Int) {
        navigator.onChooseReasonSpinner(rvpUndeliveredReasonCodeList[pos])
    }


    val date: Unit
        get() {
            navigator.captureDate()
        }

    fun onDatePickerClick() {
        navigator.onDatePicker()
    }

    fun onCaptureImageClick() {
        navigator.onCaptureImage()
    }

    fun onChooseSlotSpinner(pos: Int) {
        // navigator.onChooseSlotSpinner(slotCode[pos])
    }

    fun loginDate(): String {
        return dataManager.loginDate
    }

    fun onRescheduleClick() {
        navigator.onRescheduleClick()
    }

    // Upload image on server and save to DB:-
    @SuppressLint("CheckResult")
    fun uploadImageServer(
        context: Context?,
        imageUri: String?,
        imageName: String,
        imageCode: String,
        awbNo: Long,
        drsNo: Int
    ) {
        showProgress(context)
        try {
            val image_file = File(imageUri)
            val bytes = CryptoUtils.decryptFile1(image_file.toString(), Constants.ENC_DEC_KEY)
            val mFile = RequestBody.create(
                MediaType.parse("application/octet-stream"),
                Objects.requireNonNull(bytes)
            )
            val fileToUpload = MultipartBody.Part.createFormData("image", image_file.name, mFile)
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
                compositeDisposable.add(dataManager.doImageUploadApiCall(
                    dataManager.authToken,
                    dataManager.ecomRegion,
                    "RVP",
                    headers,
                    map,
                    fileToUpload
                ).subscribeOn(
                    schedulerProvider.io()
                ).observeOn(schedulerProvider.ui()).subscribe(
                    { imageUploadResponse: ImageUploadResponse? ->
                        dismissDialog()
                        if (imageUploadResponse != null) {
                            try {
                                if (imageUploadResponse.status.equals(
                                        "Success",
                                        ignoreCase = true
                                    )
                                ) {
                                    saveImageDB(
                                        awbNo.toString(),
                                        drsNo.toString(),
                                        imageUri,
                                        imageCode,
                                        imageName,
                                        GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_COMPLETE
                                    )
                                    Observable.fromCallable {
                                        saveImageResponse(
                                            imageName,
                                            imageUploadResponse.imageId
                                        )
                                        false
                                    }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                                        .subscribe { _: Boolean? -> }
                                } else {
                                    navigator.showServerError()
                                }
                            } catch (ex: Exception) {
                                navigator.showServerError()
                            }
                        } else {
                            navigator.showServerError()
                        }
                    }, { _: Throwable? ->
                        dismissDialog()
                        navigator.showServerError()
                    })
                )
            } catch (ex: Exception) {
                dismissDialog()
                navigator.showServerError()
            }
        } catch (ex: Exception) {
            dismissDialog()
            navigator.showServerError()
        }
    }

    @SuppressLint("CheckResult")
    private fun saveImageResponse(image_name: String, image_id: Int) {
        dataManager.updateImageStatus(image_name, 2)
        dataManager.updateImageID(image_name, image_id)
    }

    fun callApi(nyka: String, failFlag: Boolean, awb_number: String?, drs_id: String?) {
        if (nyka.equals("true", ignoreCase = true)) {
            try {
                request_time = System.currentTimeMillis()
                setIsLoading(true)
                compositeDisposable.add(dataManager.doForwardCallStatusApiCall(
                    dataManager.authToken,
                    dataManager.ecomRegion,
                    call_alert_number,
                    dataManager.emp_code,
                    awb_number,
                    drs_id,
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
                            response_time = System.currentTimeMillis()
                            if (forwardCallResponse.isCall_again_required) {
                                navigator.undeliverShipment(failFlag, false)
                            } else {
                                navigator.showError(forwardCallResponse.getResponse())
                                if (dataManager.isCallStatusAPIRecursion) {
                                    call_alert_number += 1
                                    val time_difference = response_time - request_time
                                    if (time_difference < dataManager.requestRsponseTime) {
                                        val difference =
                                            dataManager.requestRsponseTime - time_difference
                                        val handler = Handler()
                                        handler.postDelayed({
                                            callApi(nyka, failFlag, awb_number, drs_id)
                                            if (isCallRecursionDialogRunning) {
                                                navigator.getActivityContext()
                                                    .runOnUiThread(Runnable {
                                                        showCallAPIDelayDialog(
                                                            nyka,
                                                            failFlag,
                                                            awb_number,
                                                            drs_id
                                                        )
                                                    })
                                            }
                                        }, difference)
                                    } else {
                                        callApi(nyka, failFlag, awb_number, drs_id)
                                        if (isCallRecursionDialogRunning) {
                                            showCallAPIDelayDialog(
                                                nyka,
                                                failFlag,
                                                awb_number,
                                                drs_id
                                            )
                                        }
                                    }
                                } else {
                                    if (isStopRecursion) {
                                        return@subscribe
                                    }
                                    call_alert_number += 1
                                    showCallAPIDelayDialog(nyka, failFlag, awb_number, drs_id)
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

    fun onUndeliveredApiCall(rvpCommit: RvpCommit, composite_key: String) {
        try {
            rvpCommit.status = Constants.RVPUNDELIVERED
            rvpCommit.trip_id = dataManager.tripId
            rvpCommit.feEmpCode = dataManager.code
            if (!`in`.ecomexpress.geolocations.Constants.latitude.toString().equals(
                    "0.0",
                    ignoreCase = true
                ) && !`in`.ecomexpress.geolocations.Constants.longitude.toString()
                    .equals("0.0", ignoreCase = true)
            ) {
                rvpCommit.locationLat = `in`.ecomexpress.geolocations.Constants.latitude.toString()
                rvpCommit.locationLong =
                    `in`.ecomexpress.geolocations.Constants.longitude.toString()
            } else if (!Constants.CURRENT_LATITUDE.equals(
                    "0.0",
                    ignoreCase = true
                ) && !Constants.CURRENT_LONGITUDE.equals("0.0", ignoreCase = true)
            ) {
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
            saveCommit(rvpCommit, composite_key)
        } catch (ex: Exception) {
            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, ex.message)
            navigator.showError(ex.localizedMessage)
        }
    }

    fun onUndeliveredApiCallRealTime(rvpCommit: RvpCommit, composite_key: String) {
        try {
            compositeDisposable.add(dataManager.getImages(rvpCommit.awb).subscribeOn(
                schedulerProvider.io()
            ).observeOn(schedulerProvider.io()).subscribe(
                { imageModels: List<ImageModel> ->
                    mImageModels = imageModels
                    var isAllImageSynced = true
                    for (i in mImageModels.indices) {
                        if (mImageModels[i].imageId <= 0) {
                            isAllImageSynced = false
                            break
                        }
                    }
                    if (isAllImageSynced) {
                        val list_image_responses: MutableList<RvpCommit.ImageData> = ArrayList()
                        for (i in mImageModels.indices) {
                            val image_response = RvpCommit.ImageData()
                            image_response.imageId = mImageModels[i].imageId.toString()
                            image_response.imageKey = mImageModels[i].imageName.toString()
                            list_image_responses.add(image_response)
                        }
                        rvpCommit.imageData = list_image_responses
                        rvpCommit.status = Constants.RVPUNDELIVERED
                        rvpCommit.trip_id = dataManager.tripId
                        if (!`in`.ecomexpress.geolocations.Constants.latitude.toString().equals(
                                "0.0",
                                ignoreCase = true
                            ) && !`in`.ecomexpress.geolocations.Constants.longitude.toString()
                                .equals("0.0", ignoreCase = true)
                        ) {
                            rvpCommit.locationLat =
                                `in`.ecomexpress.geolocations.Constants.latitude.toString()
                            rvpCommit.locationLong =
                                `in`.ecomexpress.geolocations.Constants.longitude.toString()
                        } else if (!Constants.CURRENT_LATITUDE.equals(
                                "0.0",
                                ignoreCase = true
                            ) && !Constants.CURRENT_LONGITUDE.equals("0.0", ignoreCase = true)
                        ) {
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
                        uploadRvpShipment(rvpCommit, composite_key)
                    } else {
                        onUndeliveredApiCall(rvpCommit, composite_key)
                    }
                }) { throwable: Throwable ->
                Logger.e(
                    RVPUndeliveredCancelViewModel::class.java.name, throwable.message
                )
                navigator.showError(throwable.localizedMessage)
            })
        } catch (ex: Exception) {
            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, ex.message)
            navigator.showError(ex.localizedMessage)
        }
    }

    @SuppressLint("CheckResult")
    private fun uploadRvpShipment(rvpCommit: RvpCommit, composite_key: String) {
        val tokens = HashMap<String, String>()
        tokens[Constants.TOKEN] = dataManager.authToken
        tokens[Constants.EMP_CODE] = dataManager.code
        compositeDisposable.add(dataManager.doRVPUndeliveredCommitApiCall(
            dataManager.authToken,
            dataManager.ecomRegion,
            tokens,
            rvpCommit
        ).subscribeOn(
            schedulerProvider.io()
        ).subscribe(
            { rvpCommitResponse: RVPCommitResponse ->
                if (rvpCommitResponse.status) {
                    val shipment_status: Int
                    var compositeKey = ""
                    shipment_status =
                        if (rvpCommitResponse.getResponse().getShipment_status().equals(
                                Constants.RVPUNDELIVERED, ignoreCase = true
                            )
                        ) {
                            Constants.SHIPMENT_UNDELIVERED_STATUS
                        } else {
                            Constants.SHIPMENT_DELIVERED_STATUS
                        }
                    try {
                        compositeKey = rvpCommitResponse.response
                            .drs_no + rvpCommitResponse.response.awb_no
                    } catch (e: Exception) {
                        Logger.e(RVPUndeliveredCancelViewModel::class.java.name, e.message)
                    }
                    dataManager.updateRvpStatus(compositeKey, shipment_status)
                        .subscribe({ _: Boolean? ->
                            updateSyncStatusInDRSRVpTable(
                                rvpCommitResponse.response
                                    .drs_no + rvpCommitResponse.response.awb_no
                            )
                            // Setting call preference after sync:-
                            dataManager.setCallClicked(
                                rvpCommitResponse.response.awb_no + "RVPCall", true
                            )
                            compositeDisposable.add(
                                dataManager.deleteSyncedImage(
                                    rvpCommitResponse.response.awb_no
                                ).subscribe { _: Boolean? -> })
                        }) { _: Throwable? ->
                            saveCommit(rvpCommit, composite_key)
                            setIsLoading(false)
                        }
                }
            }, { throwable: Throwable ->
                Logger.e(RVPUndeliveredCancelViewModel::class.java.name, throwable.message)
                saveCommit(rvpCommit, composite_key)
                setIsLoading(false)
                navigator.showError(throwable.message)
            })
        )
    }

    private fun updateSyncStatusInDRSRVpTable(composite_key: String) {
        compositeDisposable.add(dataManager.updateSyncStatusRVP(composite_key, 2).subscribeOn(
            schedulerProvider.io()
        ).observeOn(
            schedulerProvider.io()
        ).subscribe(
            { _: Boolean? -> navigator.onSubmitSuccess() }) { _: Throwable? ->
        })
    }

    private fun updateShipmentStatus(composite_key: String) {
        try {
            compositeDisposable.add(
                dataManager.updateRvpStatus(composite_key, 3).observeOn(
                    schedulerProvider.ui()
                ).subscribeOn(schedulerProvider.io())
                    .subscribe { _: Boolean? -> navigator.onSubmitSuccess() })
        } catch (ex: Exception) {
            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, ex.message)
        }
    }

    fun onRvpDRSCommit(rvpCommit: RvpCommit) {
        this.rvpCommit = rvpCommit
        awbNo.set(rvpCommit.awb)
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

    fun saveImageDB(
        awb: String?,
        drsid: String?,
        imageUri: String?,
        imageCode: String?,
        name: String?,
        image_sync_status: Int
    ) {
        try {
            val imageModel = ImageModel()
            imageModel.draNo = drsid
            imageModel.awbNo = awb
            imageModel.imageName = name
            imageModel.image = imageUri
            imageModel.imageCode = imageCode
            imageModel.status = 0
            imageModel.imageCurrentSyncStatus = image_sync_status
            imageModel.imageFutureSyncTime = Calendar.getInstance().timeInMillis
            imageModel.imageId = -1
            imageModel.imageType = GlobalConstant.ImageTypeConstants.OTHERS
            imageModel.date = Calendar.getInstance().timeInMillis
            imageModel.shipmentType = GlobalConstant.ShipmentTypeConstants.RVP
            compositeDisposable.add(dataManager.saveImage(imageModel)
                .subscribeOn(schedulerProvider.io()).observeOn(
                schedulerProvider.ui()
            ).subscribe { _: Boolean? -> navigator.setBitmap() })
        } catch (ex: Exception) {
            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, ex.message)
            navigator.showError(ex.localizedMessage)
        }
    }



    private fun saveCommit(rvpCommit: RvpCommit, composite_key: String) {
        val pushApi = PushApi()
        pushApi.awbNo = rvpCommit.awb.toLong()
        pushApi.compositeKey = composite_key
        pushApi.authtoken = dataManager.authToken
        try {
            pushApi.requestData = ObjectMapper().writeValueAsString(rvpCommit)
            pushApi.shipmentStatus = 0
            pushApi.shipmentCaterogy = Constants.RVPCOMMIT
        } catch (e: Exception) {
            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, e.message)
            navigator.showError(e.message)
        }
        try {
            compositeDisposable.add(
                dataManager.saveCommitPacket(pushApi).subscribeOn(
                    schedulerProvider.io()
                ).observeOn(schedulerProvider.ui()).subscribe { _: Boolean? ->
                    updateShipmentStatus(pushApi.compositeKey.toString())
                    updateRVPCallAttemptedWithZero(pushApi.awbNo.toString())
                    val isCallattempted = getIsCallAttempted(rvpCommit.awb)
                    if (isCallattempted == 0) {
                        Constants.shipment_undelivered_count++
                    }
                })
        } catch (ex: Exception) {
            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, ex.message)
            navigator.showError(ex.message)
        }
    }

    val lat: String
        get() = `in`.ecomexpress.geolocations.Constants.latitude.toString()
    val lng: String
        get() = `in`.ecomexpress.geolocations.Constants.longitude.toString()

    fun getIsCallAttempted(awb: String): Int {
        try {
            compositeDisposable.add(
                dataManager.getisRVPCallattempted(awb.toLong()).observeOn(
                    schedulerProvider.ui()
                ).subscribeOn(schedulerProvider.io()).subscribe { isCallattempted: Long ->
                    try {
                        isCall = isCallattempted.toString().toInt()
                        navigator.isCallAttempted(isCall)
                    } catch (e: Exception) {
                        Logger.e(RVPUndeliveredCancelViewModel::class.java.name, e.message)
                    }
                })
        } catch (ex: Exception) {
            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, ex.message)
            navigator.showError(ex.message)
        }
        return isCall
    }

    fun updateRVPCallAttempted(awb: String) {
        try {
            compositeDisposable.add(
                dataManager.updateRVPCallAttempted(
                    java.lang.Long.valueOf(awb),
                    Constants.callAttempted
                ).observeOn(
                    schedulerProvider.ui()
                ).subscribeOn(schedulerProvider.io()).subscribe { _: Boolean? -> })
        } catch (ex: Exception) {
            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, ex.message)
            navigator.showError(ex.message)
        }
    }

    fun updateRVPCallAttemptedWithZero(awb: String) {
        try {
            compositeDisposable.add(
                dataManager.updateRVPCallAttempted(java.lang.Long.valueOf(awb), 0).observeOn(
                    schedulerProvider.ui()
                ).subscribeOn(schedulerProvider.io()).subscribe { _: Boolean? -> })
        } catch (ex: Exception) {
            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, ex.message)
            navigator.showError(ex.message)
        }
    }

    fun makeCallBridgeApiCall(callBridgeKey: String?, awb: String, drsid: String, type: String) {
        val request = CallApiRequest()
        request.awb = awb
        request.cb_api_key = callBridgeKey
        request.drs_id = drsid
        val timeStamp = Calendar.getInstance().timeInMillis
        writeRestAPIRequst(timeStamp, request)
        try {
            compositeDisposable.add(dataManager.doCallBridgeApiCall(
                dataManager.authToken,
                dataManager.ecomRegion,
                request
            ).observeOn(
                schedulerProvider.ui()
            ).subscribeOn(schedulerProvider.io()).subscribe(
                { callApiResponse: ErrorResponse? ->
                    writeRestAPIResponse(timeStamp, callApiResponse)
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
                    navigator.showErrorMessage(
                        Objects.requireNonNull(throwable.message)!!.contains("HTTP 500 ")
                    )
                    Logger.e(RVPUndeliveredCancelViewModel::class.java.name, throwable.message)
                })
            )
        } catch (ex: Exception) {
            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, ex.message)
            navigator.showError(ex.message)
        }
    }

    fun logoutLocal() {
        try {
            dataManager.tripId = ""
            dataManager.setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT)
            clearAppData()
        } catch (ex: Exception) {
            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, ex.message)
            navigator.showError(ex.message)
        }
    }

    private fun clearAppData() {
        try {
            compositeDisposable.add(
                dataManager.deleteAllTables().subscribeOn(schedulerProvider.io()).observeOn(
                    schedulerProvider.ui()
                ).subscribe { _: Boolean? ->
                    try {
                        dataManager.clearPrefrence()
                        dataManager.setUserAsLoggedOut()
                    } catch (ex: Exception) {
                        Logger.e(RVPUndeliveredCancelViewModel::class.java.name, ex.message)
                    }
                    navigator.clearStack()
                })
        } catch (ex: Exception) {
            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, ex.message)
            navigator.showError(ex.message)
        }
    }

    fun fetchRVPShipment(composite_key: String?) {
        try {
            compositeDisposable.add(dataManager.getRVPDRS(composite_key).subscribeOn(
                schedulerProvider.io()
            ).observeOn(
                schedulerProvider.ui()
            ).subscribe(
                { drsReverseQCTypeResponse: DRSReverseQCTypeResponse ->
                    navigator.onRVPItemFetched(
                        drsReverseQCTypeResponse
                    )
                }, { throwable: Throwable ->
                    writeErrors(Calendar.getInstance().timeInMillis, Exception(throwable))
                    Logger.e(RVPUndeliveredCancelViewModel::class.java.name, throwable.message)
                })
            )
        } catch (ex: Exception) {
            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, ex.message)
            navigator.showError("awb   is " + ex.message)
        }
    }

    fun checkMeterRange(drsReverseQCTypeResponse: DRSReverseQCTypeResponse) {
        try {
            getProfileLatLng(drsReverseQCTypeResponse)
        } catch (e: Exception) {
            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, e.message)
            navigator.showError(e.message)
        }
    }

    private fun getProfileLatLng(drsForwardTypeResponse: DRSReverseQCTypeResponse) {
        try {
            compositeDisposable.add(dataManager.getProfileLat(drsForwardTypeResponse.getAwbNo())
                .subscribeOn(
                    schedulerProvider.io()
                ).subscribe(
                    Consumer<ProfileFound> { profileFound: ProfileFound? ->
                        try {
                            if (profileFound != null && profileFound.awb_number != 0L) {
                                var consigneeLatitude = 0.0
                                var consigneeLongitude = 0.0
                                try {
                                    consigneeLatitude = profileFound.delivery_latitude
                                    consigneeLongitude = profileFound.delivery_longitude
                                } catch (e: Exception) {
                                    Logger.e(
                                        RVPUndeliveredCancelViewModel::class.java.name,
                                        e.message
                                    )
                                }
                                if (consigneeLatitude == 0.0 || consigneeLongitude == 0.0) {
                                    navigator.setConsigneeDistance(0)
                                    return@Consumer
                                }
                                // int meter = (int) getDistanceBetweenLocations(new LatLng(consigneeLatitude, consigneeLongitude));
                                val meter: Int = LocationHelper.getDistanceBetweenPoint(
                                    consigneeLatitude,
                                    consigneeLongitude,
                                    dataManager.currentLatitude,
                                    dataManager.currentLongitude
                                )
                                navigator.setConsigneeDistance(meter)
                            }
                        } catch (e: Exception) {
                            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, e.message)
                        }
                    }, Consumer<Throwable> { obj: Throwable -> obj.printStackTrace() })
            )
        } catch (e: Exception) {
            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, e.message)
        }
    }

    val consigneeProfiling: Unit
        get() {
            val enable = dataManager.consigneeProfiling
            navigator.setConsingeeProfiling(enable)
        }

    fun FEInDCZone(
        latitude: Double,
        longitude: Double,
        dcLatitude: Double,
        dcLongitude: Double
    ): Boolean {
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

    fun getDistanceBetweenLocations(destination: LatLng?): Double {
        try {
            val distance: Double
            val context = GeoApiContext().setApiKey(Constants.DISTANCE_API_KEY)
            val result = DirectionsApi.newRequest(context).mode(TravelMode.DRIVING)
                .units(com.google.maps.model.Unit.METRIC).origin(
                    LatLng(
                        dataManager.currentLatitude, dataManager.currentLongitude
                    )
                ).optimizeWaypoints(true).destination(destination).awaitIgnoreError()
            val dis = result.routes[0].legs[0].distance.humanReadable
            distance = if (dis.endsWith("km")) {
                dis.replace("[^\\.0123456789]".toRegex(), "").toDouble() * 1000
            } else {
                dis.replace("[^\\.0123456789]".toRegex(), "").toDouble()
            }
            return distance
        } catch (e: Exception) {
            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, e.message)
        }
        return 0.0
    }

    fun showCallAPIDelayDialog(
        nyka: String,
        failFlag: Boolean,
        awb_number: String?,
        drs_id: String?
    ) {
        isCallRecursionDialogRunning = false
        navigator.getActivityContext().runOnUiThread(Runnable {
            val callalertDialog = AlertDialog.Builder(navigator.getActivityContext())
                .setMessage("Getting call status...").setPositiveButton("Wait", null)
            calldialog = callalertDialog.create()
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
                                if (call_alert_number > 1) {
                                    navigator.undeliverShipment(failFlag, false)
                                } else {
                                    callApi(nyka, failFlag, awb_number, drs_id)
                                }
                            }
                        }
                    }.start()
                }
            })
            calldialog?.setCancelable(false)
            calldialog?.show()
        })
    }

    val counterDeliveryRange: Int
        get() {
            val currentLatitude = dataManager.currentLatitude
            val currentLongitude = dataManager.currentLongitude
            val DcLatitude = dataManager.getDCLatitude()
            val DcLongitude = dataManager.getDCLongitude()
            return LocationHelper.getDistanceBetweenPoint(
                currentLatitude,
                currentLongitude,
                DcLatitude,
                DcLongitude
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
                { aBoolean: Boolean? -> dataManager.setIsCallAlreadyDone(aBoolean) }) { throwable: Throwable? -> })
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
            compositeDisposable.add(dataManager.saveCallStatus(call)
                .subscribeOn(schedulerProvider.io()).observeOn(
                    schedulerProvider.ui()
                ).subscribe(
                    { aBoolean: Boolean? -> }) { throwable: Throwable? -> })
        } catch (e: Exception) {
            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, e.message)
        }
    }

    var ud_otp_commit_status = "NONE"
    var ud_otp_verified_status = ObservableField(false)
    var ud_otp_commit_status_field = ObservableField("NONE")
    var rd_otp_commit_status = "NONE"
    var rd_otp_commit_status_field = ObservableField("NONE")
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
        if (!consigneealternatemobile.isEmpty()) {
            btSmsAlternate.visibility = View.VISIBLE
            btSmsAlternate.text = "SMS on Alternate No. $consigneealternatemobile"
        }
        if (checkcall.isEmpty()) {
            call.visibility = View.GONE
            txtCall.visibility = View.INVISIBLE
        }
        btSmsAlternate.setOnClickListener { v: View? ->
            dialog.dismiss()
            navigator.resendSms(true)
        }
        crossDialog.setOnClickListener { v: View? -> dialog.dismiss() }
        sms.setOnClickListener { v: View? ->
            dialog.dismiss()
            navigator.resendSms(false)
        }
        call.setOnClickListener { v: View? ->
            dialog.dismiss()
            navigator.VoiceCallOtp()
        }
        dialog.show()
        dialog.window!!.attributes = lp
    }

    var dialog: ProgressDialog? = null
    var counter_skip = 0
    fun onGenerateOtpApiCall(
        context: Activity?,
        awb: String?,
        drsid: String?,
        alternateclick: Boolean?,
        messagetype: String?,
        generateotp: Boolean?,
        shipment_type: String?
    ) {
        dialog = ProgressDialog(context)
        dialog!!.setMessage("Sending OTP....")
        dialog!!.show()
        dialog!!.isIndeterminate = false
        val request = GenerateUDOtpRequest(
            awb,
            messagetype,
            drsid,
            alternateclick,
            dataManager.code,
            generateotp,
            shipment_type
        )
        val timeStamp = System.currentTimeMillis()
        writeRestAPIRequst(timeStamp, request)
        try {
            compositeDisposable.add(dataManager.doGenerateUDOtpApiCall(
                dataManager.authToken,
                dataManager.ecomRegion,
                request
            ).doOnSuccess(
                Consumer<ResendOtpResponse> { resendOtpResponse: ResendOtpResponse ->
                    writeRestAPIResponse(timeStamp, resendOtpResponse)
                    Log.d(ContentValues.TAG, resendOtpResponse.toString())
                }).subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui()).subscribe(
                { response: ResendOtpResponse ->
                    if (dialog!!.isShowing) dialog!!.dismiss()
                    if (response.getStatus() == "true") {
                        counter_skip++
                        navigator.onOtpResendSuccess(
                            response.getStatus(),
                            response.getDescription()
                        )
                    } else {
                        if (response.getResponse() != null) {
                            if (response.getResponse().getStatusCode()
                                    .equals("107", ignoreCase = true)
                            ) {
                                navigator.doLogout(response.getResponse().getDescription())
                            }
                        } else if (response.getDescription().equals("Max Attempt Reached")) {
                            counter_skip++
                            navigator.onOtpResendSuccess(
                                response.getStatus(),
                                response.getDescription()
                            )
                        }
                        navigator.showError(response.getDescription())
                    }
                }, { throwable: Throwable? ->
                    if (dialog!!.isShowing) dialog!!.dismiss()
                    val error: String
                    try {
                        error = RestApiErrorHandler(throwable).getErrorDetails().getEResponse()
                            .getDescription()
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


    fun onSkip(view: View?) {
        navigator.onSkipClick(view)
    }


    fun getRemarkCount(awb: Long) {
        try {
            compositeDisposable.add(dataManager.getRemarks(awb).observeOn(schedulerProvider.io())
                .subscribeOn(
                    schedulerProvider.io()
                ).subscribe(
                    { remark: Remark? ->
                        rvpCommit!!.trying_reach =
                            dataManager.getTryReachingCount(awb.toString() + Constants.TRY_RECHING_COUNT)
                                .toString()
                        rvpCommit!!.techpark =
                            dataManager.getSendSmsCount(awb.toString() + Constants.TECH_PARK_COUNT)
                                .toString()
                    }, { obj: Throwable -> obj.printStackTrace() })
            )
        } catch (e: Exception) {
            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, e.message)
            navigator.showError(e.message)
        }
    }

    fun addRemarks(remark: Remark?) {
        try {
            compositeDisposable.add(dataManager.insertRemark(remark)
                .observeOn(schedulerProvider.io()).subscribeOn(
                    schedulerProvider.io()
                ).subscribe(
                    { aBoolean: Boolean? -> }) { obj: Throwable -> obj.printStackTrace() })
        } catch (e: Exception) {
            Logger.e(RVPUndeliveredCancelViewModel::class.java.name, e.message)
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
        val selectTemplate: RVPReasonCodeMaster
            get() {
                val reasonSelectReason = RVPReasonCodeMaster()
                reasonSelectReason.setReasonCode(-1)
                reasonSelectReason.setReasonMessage(Constants.SELECT)
                val masterDataAttributeResponse = MasterDataAttributeResponse()
                masterDataAttributeResponse.setcALLM(false)
                masterDataAttributeResponse.setiMG(false)
                masterDataAttributeResponse.setoTP(false)
                masterDataAttributeResponse.setrCHD(false)
                masterDataAttributeResponse.setSECURED(false)
                reasonSelectReason.setMasterDataAttributeResponse(masterDataAttributeResponse)
                return reasonSelectReason
            }
    }
}