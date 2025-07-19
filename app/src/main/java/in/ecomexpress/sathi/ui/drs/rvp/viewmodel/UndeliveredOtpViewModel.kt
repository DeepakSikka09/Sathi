package `in`.ecomexpress.sathi.ui.drs.rvp.viewmodel

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.paytmmoneyagent.manchlib.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.ecomexpress.geolocations.LocationService
import `in`.ecomexpress.sathi.R
import `in`.ecomexpress.sathi.SathiApplication
import `in`.ecomexpress.sathi.backgroundServices.SyncServicesV2
import `in`.ecomexpress.sathi.repo.IDataManager
import `in`.ecomexpress.sathi.repo.remote.model.forward.verifyOTP.VerifyOTPResponse
import `in`.ecomexpress.sathi.repo.remote.model.otp.resendotp.GenerateUDOtpRequest
import `in`.ecomexpress.sathi.repo.remote.model.otp.resendotp.ResendOtpRequest
import `in`.ecomexpress.sathi.repo.remote.model.otp.resendotp.ResendOtpResponse
import `in`.ecomexpress.sathi.repo.remote.model.otp.verifyotp.VerifyUDOtpRequest
import `in`.ecomexpress.sathi.repo.remote.model.voice_otp.VoiceOTP
import `in`.ecomexpress.sathi.repo.remote.model.voice_otp.VoiceOTPResponse
import `in`.ecomexpress.sathi.ui.base.BaseViewModel
import `in`.ecomexpress.sathi.ui.drs.rvp.navigator.RvpQcNavigator
import `in`.ecomexpress.sathi.utils.Constants
import `in`.ecomexpress.sathi.utils.Helper
import `in`.ecomexpress.sathi.utils.Logger
import `in`.ecomexpress.sathi.utils.rx.ISchedulerProvider
import javax.inject.Inject

@HiltViewModel
class UndeliveredOtpViewModel @Inject constructor(dataManager: IDataManager, schedulerProvider: ISchedulerProvider, application: Application) : BaseViewModel<RvpQcNavigator>(dataManager, schedulerProvider, application) {

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

    private val _clearStackEvent = MutableLiveData<Unit>()
    val clearStackEvent: LiveData<Unit> = _clearStackEvent
    private val setCapturedImageCount = MutableLiveData(0)
    val capturedImageCount: LiveData<Int> get() = setCapturedImageCount

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
                    val error: String =
                        response.description ?: response.response?.description ?: context.getString(
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
                }
                ))
        } catch (e: Exception) {
            setOtpSendStatus.postValue(false)
            dismissProgressDialog()
            showMessage("Exception: ${e.localizedMessage}", true)
        }
    }

    fun onRqcGenerateOtpApiCall(
        context: Activity,
        awb: String?,
        drsId: String?,
        alternateClick: Boolean?,
        generateOtp: Boolean?,
        otpMessageType: String
    ) {
        showProgressDialog(context, context.getString(R.string.sending_otp))
        try {
            val request = GenerateUDOtpRequest(
                awb,
                otpMessageType,
                drsId,
                alternateClick,
                dataManager.code,
                generateOtp,
                Constants.RQC
            )
            compositeDisposable.add(
                dataManager.doGenerateUDOtpApiCall(
                    dataManager.authToken,
                    dataManager.ecomRegion,
                    request
                ).doOnSuccess {}.subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui()).subscribe({ response: ResendOtpResponse ->
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
                }
                ))
        } catch (e: Exception) {
            setOtpSendStatus.postValue(false)
            dismissProgressDialog()
            showMessage("Exception: ${e.localizedMessage}", true)
        }
    }

    fun onRqcOtpVerifyApiCall(
        context: Activity,
        awb: String?,
        otp: String?,
        drsId: String?,
        otpMessageType: String
    ) {
        try {
            showProgressDialog(context, context.getString(R.string.verifying))
            val request = VerifyUDOtpRequest(awb, drsId, otpMessageType, otp)
            compositeDisposable.add(
                dataManager.doVerifyUDOtpDRSApiCall(
                    dataManager.authToken,
                    dataManager.ecomRegion,
                    request
                ).doOnSuccess {}.subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui()).subscribe({ response: VerifyOTPResponse ->
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
                }
                ))
        } catch (e: Exception) {
            setOtpVerifyStatus(false)
            dismissProgressDialog()
            showMessage("Exception: ${e.localizedMessage}", true)
        }
    }

    fun doVoiceOTPApiCall(
        context: Context,
        awb: String,
        drsId: String,
        shipmentType: String,
        otpMessageType: String
    ) {
        try {
            showProgressDialog(context, context.getString(R.string.sending_otp))
            val voiceOtpRequest = VoiceOTP()
            voiceOtpRequest.awb = awb
            voiceOtpRequest.drs_id = drsId
            voiceOtpRequest.product_type = shipmentType
            voiceOtpRequest.employee_code = dataManager.emp_code
            voiceOtpRequest.message_type = otpMessageType
            compositeDisposable.add(
                dataManager.doVoiceOtpApiCall(
                    dataManager.authToken,
                    dataManager.ecomRegion,
                    voiceOtpRequest
                ).doOnSuccess {}.subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui()).subscribe({ response: VoiceOTPResponse ->
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
                }
                ))
        } catch (e: Exception) {
            setOtpSendStatus.postValue(false)
            dismissProgressDialog()
            showMessage("Exception: ${e.localizedMessage}", true)
        }
    }

    fun clearAppData(context: Context) {
        try {
            compositeDisposable.add(dataManager.deleteAllTables()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe({
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
                            Intent(
                                context,
                                LocationService::class.java
                            )
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
            showMessage(e.message.toString(), true)
        }
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
}