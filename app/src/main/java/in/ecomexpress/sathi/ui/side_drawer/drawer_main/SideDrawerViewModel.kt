package `in`.ecomexpress.sathi.ui.side_drawer.drawer_main

import android.annotation.SuppressLint
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.widget.TextView
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.ecomexpress.geolocations.LocationService
import `in`.ecomexpress.geolocations.LocationTracker
import `in`.ecomexpress.sathi.R
import `in`.ecomexpress.sathi.SathiApplication
import `in`.ecomexpress.sathi.backgroundServices.SyncServicesV2
import `in`.ecomexpress.sathi.repo.IDataManager
import `in`.ecomexpress.sathi.repo.remote.model.login.LogoutRequest
import `in`.ecomexpress.sathi.repo.remote.model.login.LogoutResponse
import `in`.ecomexpress.sathi.repo.remote.model.masterdata.Forward
import `in`.ecomexpress.sathi.repo.remote.model.masterdata.Reverse
import `in`.ecomexpress.sathi.repo.remote.model.masterdata.masterRequest
import `in`.ecomexpress.sathi.ui.base.BaseViewModel
import `in`.ecomexpress.sathi.utils.Constants
import `in`.ecomexpress.sathi.utils.Helper
import `in`.ecomexpress.sathi.utils.Logger
import `in`.ecomexpress.sathi.utils.rx.ISchedulerProvider
import io.reactivex.Single
import javax.inject.Inject

@HiltViewModel
class SideDrawerViewModel @Inject constructor(dataManager: IDataManager, schedulerProvider: ISchedulerProvider, application: Application) : BaseViewModel<SideDrawerNavigator>(dataManager, schedulerProvider, application) {

    private lateinit var dialog: Dialog

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
                } catch (e: Exception) {
                    showMessageOnUI(e.message.toString(), true)
                }
                navigator.clearStackAndMoveToLoginActivity(true)
            }, { e ->
                showMessageOnUI(e.message.toString(), true)
            }))
        } catch (e: Exception) {
            showMessageOnUI(e.message.toString(), true)
        }
    }

    fun doLogoutApiCall(context: Context) {
        showProgressDialog(context, context.getString(R.string.loading))
        try {
            val logoutRequest = LogoutRequest()
            logoutRequest.username = dataManager.code
            logoutRequest.logoutLat = dataManager.currentLatitude
            logoutRequest.logoutLng = dataManager.currentLongitude
            compositeDisposable.add(dataManager.doLogoutApiCall(dataManager.authToken, dataManager.ecomRegion, logoutRequest).doOnSuccess {}.subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui()).subscribe({
                response: LogoutResponse? ->
                    dismissProgressDialog()
                    if (response == null) { showMessageOnUI(context.getString(R.string.api_response_is_null), true)
                        return@subscribe
                    }
                    val error = if ((response.response == null || response.response.description == null)) context.getString(
                            R.string.api_response_is_false
                        ) else response.response.description
                    if (response.isStatus) {
                        showMessageOnUI(response.getResponse().description, false)
                        clearAppData(context)
                    } else {
                        if (error.equals(context.getString(R.string.invalid_authentication_token), ignoreCase = true)) {
                            clearAppData(context)
                        } else {
                            showMessageOnUI(response.getResponse().description, true)
                        }
                    }
                }, { throwable: Throwable ->
                    run {
                        dismissProgressDialog()
                        showMessageOnUI(throwable.message.toString(), true)
                    }
                })
            )
        } catch (e: java.lang.Exception) {
            dismissProgressDialog()
            showMessageOnUI(e.message.toString(), true)
        }
    }

    @SuppressLint("CheckResult")
    fun doMasterDataApiCall(context: Context) {
        showProgressDialog(context, context.getString(R.string.syncing_master_data))
        try {
            val request = masterRequest().apply {
                username = dataManager.code
            }
            compositeDisposable.add(dataManager.doMasterReasonApiCall(dataManager.authToken, dataManager.ecomRegion, request).flatMap {
                masterDataReasonCodeResponse ->
                dismissProgressDialog()
                val errorDescription = masterDataReasonCodeResponse.response?.description ?: context.getString(R.string.api_response_is_false)
                dataManager.saveMasterReason(masterDataReasonCodeResponse).subscribe {
                    if (masterDataReasonCodeResponse.status) {
                        try {
                            masterDataReasonCodeResponse.response?.master_data_configurations?.let { config ->
                                val globalConfigurationMasterList = config.globalConfigurationResponse
                                try {
                                    config.customer_attributes?.let { attributes ->
                                        attributes.forward?.let { saveShipperId(it) }
                                        attributes.rvp?.let { saveRVPShipperId(it) } ?: run {
                                            val tempReverse = Reverse(ArrayList(), ArrayList(), 1)
                                            saveRVPShipperId(tempReverse)
                                        }
                                    }
                                    globalConfigurationMasterList?.forEach { globalConfigurationMaster ->
                                        when (globalConfigurationMaster.configGroup) {
                                            "is_internet_api_available" -> dataManager.setInternetApiAvailable(globalConfigurationMaster.configValue)

                                            "ENABLE_DIRECT_DIAL" -> dataManager.setENABLEDIRECTDIAL(globalConfigurationMaster.configValue)

                                            "ENABLE_RTS_EMAIL" -> dataManager.setENABLERTSEMAIL(globalConfigurationMaster.configValue)

                                            "SAME_DAY_RESCHEDULE" -> dataManager.setSAMEDAYRESCHEDULE(globalConfigurationMaster.configValue)

                                            "RTS_IMAGE" -> dataManager.setRTSIMAGE(globalConfigurationMaster.configValue)

                                            "SCAN_PACKET_ON_DELIVERY" -> dataManager.setIsScanAwb(globalConfigurationMaster.configValue.toBoolean())

                                            "DC_UNDELIVER_STATUS" -> dataManager.setDcUndeliverStatus(globalConfigurationMaster.configValue.toBoolean())

                                            "PERFORMANCE_URL" -> dataManager.setWebLinkUrl(globalConfigurationMaster.configValue)

                                            "IS_SIGNATURE_IMAGE_MANDATORY" -> dataManager.setIsSignatureImageMandatory(globalConfigurationMaster.configValue)

                                            "IS_EDISPUTE_IMAGE_MANDATORY" -> dataManager.setEDISPUTE(globalConfigurationMaster.configValue)

                                            "IS_CALL_BRIDGE_CHECK_STATUS" -> dataManager.setIsCallBridgeCheckStatus(globalConfigurationMaster.configValue)

                                            "EDS_UNATTEMPTED_CODE" -> dataManager.setEDSUnattemptedReasonCode(globalConfigurationMaster.configValue.toInt())

                                            "FWD_UNATTEMPTED_CODE" -> dataManager.setFWDUnattemptedReasonCode(globalConfigurationMaster.configValue.toInt())

                                            "RTS_UNATTEMPTED_CODE" -> dataManager.setRTSUnattemptedReasonCode(globalConfigurationMaster.configValue.toInt())

                                            "RVP_UNATTEMPTED_CODE" -> dataManager.setRVPUnattemptedReasonCode(globalConfigurationMaster.configValue.toInt())

                                            "CONSIGNEE_PROFILING_ENABLE" -> dataManager.setConsigneeProfiling(globalConfigurationMaster.configValue.toBoolean())

                                            "ENABLE_VOICE_CALL_OTP" -> dataManager.setVCallpopup(globalConfigurationMaster.configValue.toBoolean())

                                            "MULTISPACE_APPS" -> dataManager.setMultiSpaceApps(globalConfigurationMaster.configValue)

                                            "FEEDBACK_MESSAGE" -> dataManager.setFeedbackMessage(globalConfigurationMaster.configValue)

                                            "BLUR_IMAGE_TYPE" -> dataManager.setBlurImageType(globalConfigurationMaster.configValue)

                                            "SATHI_ATTENDANCE_FEATURE_ENABLE" -> dataManager.setSathiAttendanceFeatureEnable(globalConfigurationMaster.configValue.toBoolean())

                                            "IMAGE_MANUAL_FLYER" -> dataManager.setImageManualFlyer(globalConfigurationMaster.configValue.toBoolean())

                                            "BP_MISMATCH" -> dataManager.setBPMismatch(globalConfigurationMaster.configValue.toBoolean())

                                            "UD_BP" -> dataManager.setUDBPCode(globalConfigurationMaster.configValue)

                                            "HIDE_CAMERA" -> dataManager.setHideCamera(globalConfigurationMaster.configValue.toBoolean())

                                            "ESP_EARNING_VISIBILITY" -> dataManager.setESP_EARNING_VISIBILITY(globalConfigurationMaster.configValue.toBoolean())

                                            "ODH_VISIBILITY" -> dataManager.setODH_VISIBILITY(globalConfigurationMaster.configValue.toBoolean())

                                            "SMS_THROUGH_WHATSAPP" -> dataManager.setSMSThroughWhatsapp(globalConfigurationMaster.configValue.toBoolean())

                                            "TECHPAK_WHATSAPP" -> dataManager.setTechparkWhatsapp(globalConfigurationMaster.configValue)

                                            "TRIEDREACHYOU_WHATSAPP" -> dataManager.setTriedReachyouWhatsapp(globalConfigurationMaster.configValue)

                                            "ESP_SCANNER" -> dataManager.setDPUserBarcodeFlag(globalConfigurationMaster.configValue.toBoolean())

                                            "APP_FOOTER" -> dataManager.saveBottomText(globalConfigurationMaster.configValue)

                                            "Forward" -> dataManager.setForwardReasonCodeFlag(globalConfigurationMaster.configValue)

                                            "Rts" -> dataManager.setRTSReasonCodeFlag(globalConfigurationMaster.configValue)

                                            "RTS_INPUT_OTP_RESEND" -> dataManager.setRtsInputResendFlag(globalConfigurationMaster.configValue)

                                            "RVP" -> dataManager.setRVPReasonCodeFlag(globalConfigurationMaster.configValue)

                                            "EDS" -> dataManager.setEDSReasonCodeFlag(globalConfigurationMaster.configValue)

                                            "MAX_TRIP_RUN_FOR_STOP_TRIP" -> dataManager.setMaxTripRunForStopTrip(globalConfigurationMaster.configValue)

                                            "STOP_TRIP_ALERT_KM" -> dataManager.setStartStopTripMeterReadingDiff(globalConfigurationMaster.configValue.toInt())

                                            "IT_HELP_CALL_BRIDGE" -> dataManager.setCallITExecutiveNo(globalConfigurationMaster.configValue)

                                            "SOS_NUMBERS" -> dataManager.setSOSNumbers(globalConfigurationMaster.configValue)

                                            "SOS_SMS_TEMPLATE" -> dataManager.setSOSSMSTemplate(globalConfigurationMaster.configValue)

                                            "EDS_REAL_TIME_IMAGE_SYNC" -> dataManager.saveEDSRealTimeSync(globalConfigurationMaster.configValue)

                                            "RVP_REAL_TIME_IMAGE_SYNC" -> dataManager.saveRVPRealTimeSync(globalConfigurationMaster.configValue)

                                            "CONSIGNEE_PROFILING" -> dataManager.setConsigneeProfileValue(globalConfigurationMaster.configValue)

                                            "DC_RANGE" -> dataManager.setDCRANGE(globalConfigurationMaster.configValue.toInt())

                                            "CAMPAIGN_VISIBILITY" -> dataManager.setCampaignStatus(globalConfigurationMaster.configValue.toBoolean())

                                            "REQUEST_RESPONSE_TIME" -> dataManager.setREQUEST_RESPONSE_TIME(globalConfigurationMaster.configValue.toInt())

                                            "REQUEST_RESPONSE_COUNT" -> dataManager.setREQUEST_RESPONSE_COUNT(globalConfigurationMaster.configValue.toInt())

                                            "IS_KIRANA_USER" -> dataManager.setKiranaUser(globalConfigurationMaster.configValue)

                                            "UNDELIVERED_CONSIGNEE_RANGE" -> dataManager.setUndeliverConsigneeRANGE(globalConfigurationMaster.configValue.toInt())

                                            "TRIP_GEOFENCING" -> dataManager.setTripGeofencing(globalConfigurationMaster.configValue)

                                            "MAX_DAILY_DIFF_FOR_START_TRIP" -> dataManager.setMaxDailyDiffForStartTrip(globalConfigurationMaster.configValue)

                                            "ALLOW_DUPLICATE_CASH_RECEIPT" -> dataManager.setDuplicateCashReceipt(globalConfigurationMaster.configValue)

                                            "SYNC_SERVICE_INTERVAL" -> {
                                                val syncDelay = globalConfigurationMaster.configValue.toLong()
                                                dataManager.setSyncDelay(syncDelay)
                                                Constants.SYNC_DELAY_TIME = syncDelay
                                            }

                                            "LIVE_TRACKING_MAX_FILE_SIZE" -> dataManager.setLiveTrackingMaxFileSize(globalConfigurationMaster.configValue.toInt())

                                            "AADHAR_CONSENT_DISCLAIMER" -> dataManager.setAdharMessage(globalConfigurationMaster.configValue)

                                            "RVP_BARCODE_FLYER" -> dataManager.setRVPAWBWords(globalConfigurationMaster.configValue)

                                            "RVP_UD_FLYER" -> dataManager.setRVP_UD_FLYER(globalConfigurationMaster.configValue)

                                            "AADHAR_MASKING_STATUS_CHECK_INTERVAL" -> dataManager.setAadharStatusInterval(globalConfigurationMaster.configValue)

                                            "UNDELIVERED_COUNT" -> dataManager.setUndeliverCount(globalConfigurationMaster.configValue.toInt())

                                            "MAX_EDS_EKYC_FAIL_ATTEMPT" -> dataManager.setMaxEDSFailAttempt(globalConfigurationMaster.configValue.toInt())

                                            "LAT_LNG_BATCH_COUNT" -> dataManager.setLatLngLimit(globalConfigurationMaster.configValue)

                                            "LIVE_TRACKING_DEVICE_MOVEMENT_MAX_SPEED" -> dataManager.setLiveTrackingSpeed(globalConfigurationMaster.configValue)

                                            "LIVE_TRACKING_LAT_LNG_ACCURACY" -> dataManager.setLiveTrackingAccuracy(globalConfigurationMaster.configValue)

                                            "LIVE_TRACKING_LAT_LNG_MIN_DISPLACEMENT" -> dataManager.setLiveTrackingDisplacement(globalConfigurationMaster.configValue)

                                            "LIVE_TRACKING_LAT_LNG_TIME_INTERVAL" -> dataManager.setLiveTrackingInterval((globalConfigurationMaster.configValue.toLong() * 1000).toString())

                                            "LIVE_TRACKING_DEVICE_MOVEMENT_MIN_SPEED" -> dataManager.setLiveTrackingMINSpeed(globalConfigurationMaster.configValue)

                                            "CALL_STATUS_API_INTERVAL" -> dataManager.setCallStatusApiInterval(globalConfigurationMaster.configValue)

                                            "ENABLE_GET_CALL_STATUS_API" -> dataManager.setDirectUndeliver(globalConfigurationMaster.configValue.toBoolean())

                                            "ENABLE_CALL_API_RECURSION" -> dataManager.setCallAPIRecursion(globalConfigurationMaster.configValue.toBoolean())

                                            "CALL_STATUS_API_RECURSION_INTERVAL" -> dataManager.setRequestResponseTime(globalConfigurationMaster.configValue.toLong())

                                            "ENABLE_DP_EMPLOYEE" -> dataManager.setEnableDPEmployee(globalConfigurationMaster.configValue.toBoolean())

                                            "COD_STATUS_INTERVAL" -> dataManager.setCodStatusInterval(globalConfigurationMaster.configValue.toLong())

                                            "RVP_RQC_BARCODE_SCAN" -> dataManager.setRVPRQCBarcodeScan(globalConfigurationMaster.configValue)

                                            "COD_STATUS_INTERVAL_FRACTION" -> dataManager.setCodStatusIntervalStatusFraction(globalConfigurationMaster.configValue.toInt())

                                            "RESCHEDULE_MAX_ATTEMPTS" -> dataManager.setRescheduleMaxAttempts(globalConfigurationMaster.configValue.toInt())

                                            "RESCHEDULE_MAX_ATTEPMTS_ALLOWED_DAYS" -> dataManager.setRescheduleMaxDaysAllow(globalConfigurationMaster.configValue.toInt())

                                            "IS_USE_CAMSCANNER_PRINT_RECEIPT" -> dataManager.setIsUseCamscannerPrintReceipt(globalConfigurationMaster.configValue.toBoolean())

                                            "IS_USE_CAMSCANNER_DISPUTE" -> dataManager.setIsUseCamscannerDispute(globalConfigurationMaster.configValue.toBoolean())

                                            "IS_USE_CAMSCANNER_TRIP" -> dataManager.setIsUseCamscannerTrip(globalConfigurationMaster.configValue.toBoolean())

                                            "SATHI_LOG_API_CALL_INTERVAL" -> dataManager.setSathiLogApiCallInterval(globalConfigurationMaster.configValue.toLong())

                                            "DISTANCE_GAP_FOR_DIRECTION_CAL" -> dataManager.setDistance(globalConfigurationMaster.configValue.toInt())

                                            "offline_fwd" -> dataManager.setOfflineFwd(globalConfigurationMaster.configValue.toBoolean())

                                            "CONSENT_AGREE_BUTTON_LABEL" -> dataManager.setAdharPositiveButton(globalConfigurationMaster.configValue)

                                            "CONSENT_DISAGREE_BUTTON_LABEL" -> dataManager.setAdharNegativeButton(globalConfigurationMaster.configValue)

                                            "DISABLE_RESEND_OTP_BUTTON_DURATION" -> dataManager.setDisableResendOtpButtonDuration(globalConfigurationMaster.configValue.toLong())

                                            "ESP_REFER_SCHEME_TERM_AND_CONDITIONS" -> dataManager.setESPSchemeTerms(globalConfigurationMaster.configValue)

                                            "ESP_REFERRAL_CODE" -> dataManager.setESPReferCode(globalConfigurationMaster.configValue)

                                            "address_quality_score" -> dataManager.setAddressQualityScore(globalConfigurationMaster.configValue)

                                            "SKIP_OTP_REV_RQC" -> dataManager.setSKIPOTPREVRQC(globalConfigurationMaster.configValue)

                                            "SKIP_CANC_OTP_RVP" -> dataManager.setSKIP_CANC_OTP_RVP(globalConfigurationMaster.configValue)

                                            "FAKE_APPLICATIONS" -> {
                                                if (globalConfigurationMaster.configValue.isNotBlank()) { dataManager.setFakeApplicatons(globalConfigurationMaster.configValue)
                                                    LocationTracker.setFakeApplications(dataManager.getFakeApplications())
                                                }
                                            }
                                        }
                                    }

                                    dataManager.masterDataSync = true
                                    showMessageOnUI(context.getString(R.string.master_data_synced_successfully), false)
                                    var exists = false
                                    config.callbridgeConfiguration?.cb_pstn_options?.let { pstnOptions ->
                                        if (pstnOptions.isNotEmpty()) {
                                            dataManager.setPSTNType(config.callbridgeConfiguration.cb_calling_type)
                                            if (dataManager.pstnFormat.isEmpty()) {
                                                dataManager.pstnFormat = pstnOptions[0].pstn_format
                                            } else {
                                                pstnOptions.forEach { option ->
                                                    if (dataManager.pstnFormat == option.pstn_format) {
                                                        dataManager.pstnFormat = option.pstn_format
                                                        exists = true
                                                        return@forEach
                                                    }
                                                }
                                            }
                                            if (!exists) {
                                                dataManager.pstnFormat = pstnOptions[0].pstn_format
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    showMessageOnUI(e.message.toString(), true)
                                }
                            }
                        } catch (e: Exception) {
                            showMessageOnUI(e.message.toString(), true)
                        }
                    } else {
                        if (errorDescription.equals(context.getString(R.string.invalid_authentication_token), ignoreCase = true)) {
                            showMessageOnUI(errorDescription, true)
                            clearAppData(context)
                        } else {
                            showMessageOnUI(errorDescription, true)
                        }
                    }
                }
                Single.just(true)
            }
            .subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui()).subscribe({ }, {
                throwable ->
                    dismissProgressDialog()
                    showMessageOnUI(throwable.message.toString(), true)
                }
            ))
        } catch (e: Exception) {
            dismissProgressDialog()
            showMessageOnUI(e.message.toString(), true)
        }
    }

    private fun saveShipperId(response: Forward) {
        try {
            compositeDisposable.add(dataManager.insertFwdShipperId(response).subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui()).subscribe({}, { throwable ->
                throwable.printStackTrace()
            }))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveRVPShipperId(reverse: Reverse) {
        try {
            compositeDisposable.add(dataManager.insertRvpShipperId(reverse).subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.ui()).subscribe({}, { throwable ->
                throwable.printStackTrace()
            }))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showProgressDialog(context: Context, loadingMessage: String) {
        dialog = Dialog(context, android.R.style.Theme_Material_Light_Dialog).apply {
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

    private fun showMessageOnUI(message: String, isError: Boolean) {
        navigator.showMessageOnUI(message, isError)
    }
}