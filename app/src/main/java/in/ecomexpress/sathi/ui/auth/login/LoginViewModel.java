package in.ecomexpress.sathi.ui.auth.login;

import static in.ecomexpress.sathi.SathiApplication.shipmentImageMap;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logButtonEventInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.Constants.DISTANCE_API_KEY;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.geolocations.LocationTracker;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.backgroundServices.SathiLocationService;
import in.ecomexpress.sathi.repo.DataManager;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.db.model.ApiUrlData;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.DeviceDetails;
import in.ecomexpress.sathi.repo.remote.model.login.LoginRequest;
import in.ecomexpress.sathi.repo.remote.model.login.LoginResponse;
import in.ecomexpress.sathi.repo.remote.model.masterdata.Forward;
import in.ecomexpress.sathi.repo.remote.model.masterdata.GlobalConfigurationMaster;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterDataConfig;
import in.ecomexpress.sathi.repo.remote.model.masterdata.Reverse;
import in.ecomexpress.sathi.repo.remote.model.masterdata.masterRequest;
import in.ecomexpress.sathi.repo.remote.model.verifyOtp.LoginVerifyOtpRequest;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.cameraView.CameraSelfieActivity;
import in.ecomexpress.sathi.ui.dashboard.mapview.MapActivity;
import in.ecomexpress.sathi.ui.dashboard.landing.DashboardActivity;
import in.ecomexpress.sathi.ui.drs.forward.obd.viewmodel.FwdOBDCompleteViewModel;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.PreferenceUtils;
import in.ecomexpress.sathi.utils.UpdateAPKInstaller;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.esper.devicesdk.EsperDeviceSDK;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;

@HiltViewModel
public class LoginViewModel extends BaseViewModel<ILoginNavigator> {

    private final String TAG = LoginViewModel.class.getSimpleName();
    private boolean OTPRequiredTrue;
    private long mLastClickTime = 0;
    private int mProgressiveClickTimer = 0;
    private double  disanceformDestination;

    @Inject
    public LoginViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);
    }

    public void decideNextActivity() {
        try {
            if (getDataManager().getCurrentUserLoggedInMode() != DataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT.getType()) {
                getNavigator().openDashboardActivity(false);
            } else {
                // Setting master data sync value false in preference initially:-
                getDataManager().setMasterDataSync(false);
            }
        } catch (Exception e) {
            getNavigator().onHandleError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public String getCopyRightText() {
        return getDataManager().getBottomText();
    }

    public void onServerLoginClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        if (!CommonUtils.checkMultiSpace(getNavigator().getActivityActivity(), getDataManager())) {
            return;
        }
        if (CommonUtils.checkDeveloperMode(getNavigator().getActivityActivity())) {
            return;
        }
        getNavigator().onServerLogin();
    }

    public void onForgetPasswordClick() {
        getNavigator().onForgetPassword();
    }

    /**
     * Need to check all code with ganpati
     *
     * @param context
     * @param email
     * @param password
     * @param deviceDetails
     */
    public void login(Activity context, String email, String password, DeviceDetails deviceDetails) {
        setIsLoading(true);
        final long timeStamp = System.currentTimeMillis();
        try {
            if (getDataManager().isDownloadAPKIsInProcess() > 0) {
                getNavigator().onHandleError("An updated apk is in download. please wait for some time. ");
                return;
            }
            LoginRequest loginRequest = new LoginRequest(email, password, deviceDetails);
            deviceDetails.setLatitude(getDataManager().getCurrentLatitude());
            deviceDetails.setLongitude(getDataManager().getCurrentLongitude());
            deviceDetails.setDeviceTime(Calendar.getInstance().getTimeInMillis());
            writeRestAPIRequst(timeStamp, loginRequest);
            getCompositeDisposable().add(getDataManager().doLoginApiCall(loginRequest).doOnSuccess(loginResponse -> {
                try {
                    if (loginResponse.isStatus()) {
                        logButtonEventInGoogleAnalytics(TAG, context.getString(R.string.loginbuttonclick), context.getString(R.string.employee_code) + email, context);
                        setIsLoading(false);
                        if(loginResponse.getSResponse().getServer_timestamp() == null){
                            getNavigator().showError("Timestamp Is Null In Login Response");
                            getDataManager().setLoginServerTimeStamp(String.valueOf(System.currentTimeMillis()));
                        } else{
                            getDataManager().setLoginServerTimeStamp(loginResponse.getSResponse().getServer_timestamp());
                        }
                        shipmentImageMap.clear();
                        SathiApplication.rtsCapturedImage1.clear();
                        SathiApplication.rtsCapturedImage2.clear();
                        getDataManager().setProgressiveTimer(0);
                        // Setting Login Server Time in Preference :-
                        if (getDataManager().getCode() != null && loginResponse.getSResponse().getCode() != null) {
                            if (!getDataManager().getCode().equalsIgnoreCase(loginResponse.getSResponse().getCode())) {
                                clearAppData();
                            }
                        }
                        if (loginResponse.getSResponse().getStartTripBackup().getPreviousTripStatus() != null) {
                            getDataManager().setPreviousTripStatus(loginResponse.getSResponse().getStartTripBackup().getPreviousTripStatus());
                        }
                        getDataManager().setStopTrackingAlertFlag("0");
                        LocationTracker.isStopWriting = true;
                        if (loginResponse.getSResponse().getStartTripBackup().getPreviousTripStatus() != null && loginResponse.getSResponse().getStartTripBackup().getTripId() != 0) {
                            LocationTracker.isStopWriting = false;
                            Constants.IS_RUN_DIRECTION_API = true;
                            if (!loginResponse.getSResponse().getIs_adm_emp()) {
                                startLiveTracking();
                            }
                            getDataManager().setLiveTrackingTripIdForApi(loginResponse.getSResponse().getStartTripBackup().getLive_tracking_trip_id());
                            getDataManager().setLiveTrackingTripId(loginResponse.getSResponse().getStartTripBackup().getLive_tracking_trip_id());
                        }
                        try {
                            getDataManager().SetisDCLocationUpdateAllowed(loginResponse.getSResponse().isIs_dc_update_allowed_for_dept());
                            getDataManager().setConsigneeProfiling(true);
                            getDataManager().setCovidConset(loginResponse.getSResponse().isIs_covid_consent_captured());
                            getDataManager().setCovidUrl(loginResponse.getSResponse().getCovid_image_url());
                            PreferenceUtils.writePreferenceValue(context, "ecom_dlv_region", loginResponse.getSResponse().getEcom_dlv_region());
                            getDataManager().setEcomRegion(loginResponse.getSResponse().getEcom_dlv_region());
                            getDataManager().setName(loginResponse.getSResponse().getName());
                            getDataManager().setAdharConsent(loginResponse.getSResponse().getAadhaar_consent());
                            getDataManager().setEndTripTime(loginResponse.getSResponse().getStartTripBackup().getEnd_trip_time());
                            getDataManager().setEndTripKm(loginResponse.getSResponse().getStartTripBackup().getEnd_trip_km());
                            getDataManager().setLocationCode(loginResponse.getSResponse().getLocationCode());
                            getDataManager().setCounterDelivery(loginResponse.getSResponse().getStartTripBackup().getTypeOfVehicle().equalsIgnoreCase("Counter"));
                            getDataManager().setDcLatitude(String.valueOf(loginResponse.getSResponse().getDcLocationAddress().getLocationLat()));
                            getDataManager().setDcLongitude(String.valueOf(loginResponse.getSResponse().getDcLocationAddress().getLocationLong()));
                        } catch (Exception e) {
                            Logger.e(TAG, String.valueOf(e));
                        }
                        try {
                            if (loginResponse.getSResponse().getIs_adm_emp()) {
                                getDataManager().setIsAdmEmp(loginResponse.getSResponse().getIs_adm_emp());
                                getDataManager().setPPDPrice(loginResponse.getSResponse().getAdm_price_data().getPPDPrice());
                                getDataManager().setCODPrice(loginResponse.getSResponse().getAdm_price_data().getCODPrice());
                                getDataManager().setRQCPrice(loginResponse.getSResponse().getAdm_price_data().getRQCPrice());
                                getDataManager().setEDSPrice(loginResponse.getSResponse().getAdm_price_data().getEDSPrice());
                                getDataManager().setRVPPrice(loginResponse.getSResponse().getAdm_price_data().getRVPPrice());
                                getDataManager().setRTSPrice(loginResponse.getSResponse().getAdm_price_data().getRTSPrice());
                                getDataManager().setCurrentTripAmount(String.valueOf(loginResponse.getSResponse().getStartTripBackup().getCurrent_trip_amount()));
                            } else {
                                getDataManager().setIsAdmEmp(false);
                            }
                        } catch (Exception e) {
                            Logger.e(TAG, String.valueOf(e));
                        }
                        getDataManager().setTripId(String.valueOf(loginResponse.getSResponse().getStartTripBackup().getTripId()));
                        boolean isPasswordResetRequired = false;
                        boolean isOtpRequiredFlag = loginResponse.getSResponse().isOtpRequired();
                        OTPRequiredTrue = isOtpRequiredFlag;
                        if (loginResponse.getSResponse().isPasswordResetRequired()) {
                            isPasswordResetRequired = true;
                        }
                        // If user get force apk update then application will not save any login information.
                        if (loginResponse.getSResponse().getApkUpdateResponse().getVersion_status() == 2) {
                            return;
                        }
                        LoginResponse.SResponse response = loginResponse.getSResponse();
                        IDataManager.LoggedInMode loggedInMode = DataManager.LoggedInMode.LOGGED_IN_MODE_SERVER;
                        if (isPasswordResetRequired) {
                            loggedInMode = DataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT;
                        }
                        if (isOtpRequiredFlag) {
                            loggedInMode = DataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT;
                        }
                        LoginViewModel.this.getDataManager().updateUserInfo(loggedInMode, response.getAuthToken(), response.getServiceCenter(), response.getName(), response.getDesignation(), response.getMobile(), response.getLocationType(), response.getLocationCode(), response.getCode(), response.isIsUserValidated(), response.getPhotoUrl(), ""/*, response.getApiUrls().getLiveAPKUrls().getPerformanceUrl()*/);
                        getDataManager().setLoginDate(String.valueOf(CommonUtils.convertMillisToDate(getDataManager().getLoginServerTimeStamp())));
                        getDataManager().setLoginMonth(CommonUtils.convertMillisToMonth(getDataManager().getLoginServerTimeStamp()));
                        if (response.getStartTripBackup().getTripId() != Integer.parseInt("0")) {
                            getDataManager().startTripInfo(response.getStartTripBackup().getMeterReading(), Integer.toString(response.getStartTripBackup().getTripId()), response.getStartTripBackup().getVehicleType(), response.getStartTripBackup().getTypeOfVehicle(), response.getStartTripBackup().getRouteName());
                        }
                        LoginResponse.DcLocationAddress dcLocationAddress = loginResponse.getSResponse().getDcLocationAddress();
                        if (dcLocationAddress != null) {
                            getDataManager().updateDCDetails(dcLocationAddress);
                        }
                    }
                } catch (Exception e) {
                    setIsLoading(false);
                    getNavigator().onHandleError(e.getMessage());
                    Logger.e(TAG, String.valueOf(e));
                }
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                setIsLoading(false);
                // Apk update functionality working fine already check dummy apk download and install
                try {
                    if (response.isStatus()) {

                        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(getDataManager().getDCLatitude(), getDataManager().getDCLongitude());
                        disanceformDestination = getDistanceBetweenLocations(destination);

                        try {
                            SathiLocationService.startLocationUpdates(context, getDataManager());
                            getDataManager().setProgressiveTimer(0);
                            getDataManager().setCode(response.getSResponse().getCode());
                            getDataManager().setEmp_code(response.getSResponse().getCode());
                            ArrayList<ApiUrlData> list = new ArrayList<>();
                            HashMap<String, String> hm;
                            hm = response.getSResponse().getApiUrls().getLive_api_url();
                            if (hm != null) {
                                JSONObject jsonObject = new JSONObject(hm);
                                Iterator<?> keys = jsonObject.keys();
                                while (keys.hasNext()) {
                                    String key = (String) keys.next();
                                    String Value = jsonObject.get(key).toString();
                                    ApiUrlData apiUrlData = new ApiUrlData();
                                    apiUrlData.setApiUrlKey(key);
                                    apiUrlData.setApiUrl(Value);
                                    list.add(apiUrlData);
                                }
                            }
                            if (!list.isEmpty()) {
                                saveApiUrl(list);
                            }
                        } catch (Exception e) {
                            Logger.e(TAG, String.valueOf(e));
                        }
                        if (response.getSResponse().getApkUpdateResponse().getVersion_status() == 2) {
                            getNavigator().showAPKForceUpdate(response.getSResponse().getApkUpdateResponse());
                        } else {
                            if (!response.getSResponse().isOtpRequired() && !response.getSResponse().isPasswordResetRequired()) {
                                getMasterData(response, context);
                            } else {
                                try {
                                    if (response.getSResponse().isPasswordResetRequired()) {
                                        getNavigator().showChangePassword();
                                        return;
                                    }
                                    if (response.getSResponse().isOtpRequired()) {
                                        getMasterData(response, context);
                                        getNavigator().showVerifyOtp(response.getSResponse().isPasswordResetRequired(), response.getSResponse().isManualOtpRequired());
                                        return;
                                    }
                                    if (response.getSResponse().getApkUpdateResponse().getVersion_status() == 1) {
                                        if (!response.getSResponse().isOtpRequired()) {
                                            getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_SERVER);
                                        }
                                        getMasterData(response, context);
                                        getNavigator().showAPKSoftUpdate(response, response.getSResponse().getApkUpdateResponse());
                                    } else {
                                        getMasterData(response, context);
                                    }
                                } catch (Exception e) {
                                    Logger.e(TAG, String.valueOf(e));
                                }
                            }
                        }
                    } else {
                        if (response.getSResponse().getDescription().contains("Password Mismatch")) {
                            mProgressiveClickTimer = getDataManager().getProgressiveTimer() + 1;
                            getDataManager().setProgressiveTimer(mProgressiveClickTimer);
                            getNavigator().onHandleProgressiveTimer(response.getSResponse().getDescription());
                        } else if (response.getSResponse().getDescription().equalsIgnoreCase("Your phone date/time is inaccurate! Please correct it and try again.")) {
                            context.runOnUiThread(() -> getNavigator().showAlertToUpdateDateTime());
                        } else {
                            getNavigator().onHandleError(response.getSResponse().getDescription());
                        }
                    }
                   getDataManager().setCheckAttendanceLoginStatus(true);
                } catch (Exception e) {
                    getNavigator().onHandleError(e.getMessage());
                    Logger.e(TAG, String.valueOf(e));
                }
            }, throwable -> {
                setIsLoading(false);
                String error;
                try {
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                    getNavigator().showErrorMessage(error.contains("HTTP 500 "));
                } catch (Exception e) {
                    getNavigator().onHandleError(e.getMessage());
                    LoginViewModel.this.getNavigator().onHandleError(e.getMessage());
                }
            }));
        } catch (Exception e) {
            getNavigator().onHandleError(e.getMessage());
            writeErrors(timeStamp, e);
            setIsLoading(false);
            getNavigator().onHandleError(new RestApiErrorHandler(e.fillInStackTrace()).getErrorDetails().getEResponse().getDescription());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void clearAppData() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager().deleteAllTablesOnStopTrip().subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {}));
    }

    private void startLiveTracking() {
        getDataManager().setDirectionDistance(0);
        getDataManager().setDirectionTotalDistance(0);
        try {
            if (DashboardActivity.lt != null && getDataManager().getLiveTrackingTripId() != null && !getDataManager().getLiveTrackingTripId().equalsIgnoreCase("")) {
                DashboardActivity.lt.startTrackingWithParameters(getApplication(), Constants.APP_NAME, Constants.VERSION_NAME, "LastMile", getDataManager().getCode(), getDataManager().getLocationCode(), getDataManager().getVehicleType(), getDataManager().getAuthToken(), getDataManager().getLiveTrackingTripId(), "start", getDataManager().getLiveTrackingMaxFileSize(), Constants.LIVE_TRACKING_URL, getDataManager().getLiveTrackingAccuracy(), getDataManager().getLiveTrackingInterval(), Integer.parseInt(getDataManager().getLatLngLimit()), DISTANCE_API_KEY, getDataManager().getLiveTrackingDisplacement(), getDataManager().getDistance());
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void downloadAPK(String url, Context context, EsperDeviceSDK esperDeviceSDK, Boolean esperSDKActivated) {
        try {
            if (url != null) {
                ProgressDialog pd = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
                UpdateAPKInstaller downloadAndInstall = new UpdateAPKInstaller();
                pd.setCancelable(false);
                pd.setMessage("Downloading APK File");
                pd.setMax(100);
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                downloadAndInstall.setContext(context, pd, esperDeviceSDK, esperSDKActivated);
                downloadAndInstall.execute(url.trim());
            } else {
                getNavigator().noAPKUpdate();
            }
        } catch (Exception e) {
            getNavigator().onHandleError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    /**
     * Need to check this discuss with ganpati
     *
     * @param loginResponse getMasterData
     */
    @SuppressLint("CheckResult")
    public void getMasterData(LoginResponse loginResponse, Activity context) {
        setIsLoading(true);
        final long timeStamp = System.currentTimeMillis();
        try {
            masterRequest request = new masterRequest();
            request.setUsername(getDataManager().getCode());
            writeRestAPIRequst(timeStamp, request);
            getCompositeDisposable().add(getDataManager().doMasterReasonApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request).flatMap((Function<MasterDataConfig, SingleSource<?>>) masterDataReasonCodeResponse -> {
                writeRestAPIResponse(timeStamp, masterDataReasonCodeResponse);
                setIsLoading(false);
                if (masterDataReasonCodeResponse != null) {
                    getDataManager().saveMasterReason(masterDataReasonCodeResponse).subscribe(aBoolean -> {
                        setIsLoading(false);
                        if (masterDataReasonCodeResponse.getStatus()) {
                            // Setting master data sync value true in preference:-
                            getDataManager().setMasterDataSync(true);
                            try {
                                getDataManager().setSycningBlokingStatus(true);
                                if (masterDataReasonCodeResponse.getResponse().getMaster_data_configurations() != null) {
                                    List<GlobalConfigurationMaster> globalConfigurationMasterList = masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getGlobalConfigurationResponse();
                                    try {
                                        try {
                                            if (masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCustomer_attributes().getForward() != null) {
                                                saveShipperId(masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCustomer_attributes().getForward());
                                            }
                                            if (masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCustomer_attributes().getRVP() != null) {
                                                saveRVPShipperId(masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCustomer_attributes().getRVP());
                                            } else {
                                                Reverse tempReverse = new Reverse(new ArrayList<>(), new ArrayList<>(), 1);
                                                saveRVPShipperId(tempReverse);
                                            }
                                        } catch (Exception e) {
                                            Logger.e(TAG, String.valueOf(e));
                                        }
                                        for (GlobalConfigurationMaster globalConfigurationMaster : globalConfigurationMasterList) {
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("APP_FOOTER")) {
                                                getDataManager().saveBottomText(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("is_internet_api_available")) {
                                                getDataManager().setInternetApiAvailable(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ENABLE_DIRECT_DIAL")) {
                                                getDataManager().setENABLEDIRECTDIAL(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ENABLE_RTS_EMAIL")) {
                                                getDataManager().setENABLERTSEMAIL(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("IMAGE_MANUAL_FLYER")) {
                                                getDataManager().setImageManualFlyer(Boolean.valueOf(globalConfigurationMaster.getConfigValue()));
                                            }
                                            // For Attendance Feature Enable or Disable:-
                                            if(globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("SATHI_ATTENDANCE_FEATURE_ENABLE")){
                                                getDataManager().setSathiAttendanceFeatureEnable(Boolean.valueOf(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("SAME_DAY_RESCHEDULE")) {
                                                getDataManager().setSAMEDAYRESCHEDULE(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RTS_IMAGE")) {
                                                getDataManager().setRTSIMAGE(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("DEFAULT_STATISTICS")) {
                                                getDataManager().setDEFAULTSTATISTICS(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("LIVE_TRACKING_LAT_LNG_ACCURACY")) {
                                                getDataManager().setLIVETRACKINGLATLNGACCURACY(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("MULTISPACE_APPS")) {
                                                getDataManager().setMultiSpaceApps(globalConfigurationMaster.getConfigValue());
                                            }
                                            // Feedback Popup work:-
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("FEEDBACK_MESSAGE")) {
                                                getDataManager().setFeedbackMessage(globalConfigurationMaster.getConfigValue());
                                            }
                                            // Blur Image Work:-
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("BLUR_IMAGE_TYPE")) {
                                                getDataManager().setBlurImageType(globalConfigurationMaster.getConfigValue());
                                            }
                                            // BP mismatch:-
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("BP_MISMATCH")) {
                                                getDataManager().setBPMismatch(Boolean.valueOf(globalConfigurationMaster.getConfigValue()));
                                            }
                                            // UD BP Reason Code:-
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("UD_BP")) {
                                                getDataManager().setUDBPCode(globalConfigurationMaster.getConfigValue());
                                            }
                                            //
                                            // OBD_QC_FAIL:-
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("OBD_QC_FAIL")) {
                                                getDataManager().setOBDQCFAIL(globalConfigurationMaster.getConfigValue());
                                            }
                                            //
                                            // OBD_REFUSED:-
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("OBD_REFUSED")) {
                                                getDataManager().setOBDREFUSED(globalConfigurationMaster.getConfigValue());
                                            }
                                            // Hide Camera in RQC:-
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("HIDE_CAMERA")) {
                                                getDataManager().setHideCamera(Boolean.valueOf(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if(globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ESP_EARNING_VISIBILITY")){
                                                getDataManager().setESP_EARNING_VISIBILITY(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if(globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ODH_VISIBILITY")){
                                                getDataManager().setODH_VISIBILITY(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            }
                                            else if(globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("address_quality_score")){
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("address_quality_score")) {
                                                getDataManager().setAddressQualityScore(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("SCAN_PACKET_ON_DELIVERY")) {
                                                getDataManager().setIsScanAwb(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("DC_UNDELIVER_STATUS")) {
                                                getDataManager().setDcUndeliverStatus(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("PERFORMANCE_URL")) {
                                                getDataManager().setWebLinkUrl(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("IS_SIGNATURE_IMAGE_MANDATORY")) {
                                                getDataManager().setIsSignatureImageMandatory(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("IS_EDISPUTE_IMAGE_MANDATORY")) {
                                                getDataManager().setEDISPUTE(globalConfigurationMaster.getConfigValue());
                                            }
                                        }
                                        for (GlobalConfigurationMaster globalConfigurationMaster : globalConfigurationMasterList) {
                                            // UnAttempted Reason Codes
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("EDS_UNATTEMPTED_CODE")) {
                                                getDataManager().setEDSUnattemptedReasonCode(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("FWD_UNATTEMPTED_CODE")) {
                                                getDataManager().setFWDUnattemptedReasonCode(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RTS_UNATTEMPTED_CODE")) {
                                                getDataManager().setRTSUnattemptedReasonCode(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("SMS_THROUGH_WHATSAPP")) {
                                                getDataManager().setSMSThroughWhatsapp(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("TECHPAK_WHATSAPP")) {
                                                getDataManager().setTechparkWhatsapp(String.valueOf(globalConfigurationMaster.getConfigValue()));
                                            }
                                            // DP_Users_Barcode_Scan_Work:-
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ESP_SCANNER")) {
                                                getDataManager().setDPUserBarcodeFlag(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("TRIEDREACHYOU_WHATSAPP")) {
                                                getDataManager().setTriedReachyouWhatsapp(String.valueOf(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RVP_UNATTEMPTED_CODE")) {
                                                getDataManager().setRVPUnattemptedReasonCode(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("CONSIGNEE_PROFILING_ENABLE")) {
                                                getDataManager().setConsigneeProfiling(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ENABLE_VOICE_CALL_OTP")) {
                                                getDataManager().setVCallpopup(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("APP_FOOTER")) {
                                                getDataManager().saveBottomText(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("Forward")) {
                                                getDataManager().setForwardReasonCodeFlag(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("Rts")) {
                                                getDataManager().setRTSReasonCodeFlag(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RTS_INPUT_OTP_RESEND")) {
                                                getDataManager().setRtsInputResendFlag(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RVP")) {
                                                getDataManager().setRVPReasonCodeFlag(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("EDS")) {
                                                getDataManager().setEDSReasonCodeFlag(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("PERFORMANCE_URL")) {
                                                getDataManager().setWebLinkUrl(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RVP_RQC_BARCODE_SCAN")) {
                                                getDataManager().setRVPRQCBarcodeScan(globalConfigurationMaster.getConfigValue());
                                            }
                                            // Start stop trip
                                            else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("MAX_DAILY_DIFF_FOR_START_TRIP")) {
                                                getDataManager().setMaxDailyDiffForStartTrip(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("MAX_TRIP_RUN_FOR_STOP_TRIP")) {
                                                getDataManager().setMaxTripRunForStopTrip(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("STOP_TRIP_ALERT_KM")) {
                                                getDataManager().setStartStopTripMeterReadingDiff(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("IT_HELP_CALL_BRIDGE")) {
                                                getDataManager().setCallITExecutiveNo(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("SOS_NUMBERS")) {
                                                getDataManager().setSOSNumbers(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("SOS_SMS_TEMPLATE")) {
                                                getDataManager().setSOSSMSTemplate(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("EDS_REAL_TIME_IMAGE_SYNC")) {
                                                getDataManager().saveEDSRealTimeSync(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RVP_REAL_TIME_IMAGE_SYNC")) {
                                                getDataManager().saveRVPRealTimeSync(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("CONSIGNEE_PROFILING")) {
                                                getDataManager().setConsigneeProfileValue(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("IS_KIRANA_USER")) {
                                                getDataManager().setKiranaUser(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("DC_RANGE")) {
                                                getDataManager().setDCRANGE(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            }
                                            //
                                            if(globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("CAMPAIGN_VISIBILITY")){
                                                getDataManager().setCampaignStatus(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("REQUEST_RESPONSE_TIME")) {
                                                getDataManager().setREQUEST_RESPONSE_TIME(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            }
                                            //
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("REQUEST_RESPONSE_COUNT")) {
                                                getDataManager().setREQUEST_RESPONSE_COUNT(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("UNDELIVERED_CONSIGNEE_RANGE")) {
                                                getDataManager().setUndeliverConsigneeRANGE(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RVP_RQC_BARCODE_SCAN")) {
                                                getDataManager().setRVPRQCBarcodeScan(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("TRIP_GEOFENCING")) {
                                                getDataManager().setTripGeofencing(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("MAX_DAILY_DIFF_FOR_START_TRIP")) {
                                                getDataManager().setMaxDailyDiffForStartTrip(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("MAX_TRIP_RUN_FOR_STOP_TRIP")) {
                                                getDataManager().setMaxTripRunForStopTrip(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ALLOW_DUPLICATE_CASH_RECEIPT")) {
                                                getDataManager().setDuplicateCashReceipt(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("SYNC_SERVICE_INTERVAL")) {
                                                getDataManager().setSyncDelay(Long.parseLong(globalConfigurationMaster.getConfigValue()));
                                                Constants.SYNC_DELAY_TIME = Long.parseLong(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("LIVE_TRACKING_MAX_FILE_SIZE")) {
                                                getDataManager().setLiveTrackingMaxFileSize(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("AADHAR_CONSENT_DISCLAIMER")) {
                                                getDataManager().setAdharMessage(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RVP_BARCODE_FLYER")) {
                                                getDataManager().setRVPAWBWords(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RVP_UD_FLYER")) {
                                                getDataManager().setRVP_UD_FLYER(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("AADHAR_MASKING_STATUS_CHECK_INTERVAL")) {
                                                getDataManager().setAadharStatusInterval(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("UNDELIVERED_COUNT")) {
                                                getDataManager().setUndeliverCount(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("MAX_EDS_EKYC_FAIL_ATTEMPT")) {
                                                getDataManager().setMaxEDSFailAttempt(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("LAT_LNG_BATCH_COUNT")) {
                                                getDataManager().setLatLngLimit(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("LIVE_TRACKING_DEVICE_MOVEMENT_MAX_SPEED")) {
                                                getDataManager().setLiveTrackingSpeed(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("LIVE_TRACKING_LAT_LNG_ACCURACY")) {
                                                getDataManager().setLiveTrackingAccuracy(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("LIVE_TRACKING_LAT_LNG_MIN_DISPLACEMENT")) {
                                                getDataManager().setLiveTrackingDisplacement(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("LIVE_TRACKING_LAT_LNG_TIME_INTERVAL")) {
                                                getDataManager().setLiveTrackingInterval(String.valueOf(Long.parseLong(globalConfigurationMaster.getConfigValue()) * 1000));
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("LIVE_TRACKING_DEVICE_MOVEMENT_MIN_SPEED")) {
                                                getDataManager().setLiveTrackingMINSpeed(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("CALL_STATUS_API_INTERVAL")) {
                                                getDataManager().setCallStatusApiInterval(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ENABLE_GET_CALL_STATUS_API")) {
                                                getDataManager().setDirectUndeliver(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ENABLE_CALL_API_RECURSION")) {
                                                getDataManager().setCallAPIRecursion(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("CALL_STATUS_API_RECURSION_INTERVAL")) {
                                                getDataManager().setRequestResponseTime(Long.parseLong(globalConfigurationMaster.getConfigValue()));
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ENABLE_DP_EMPLOYEE")) {
                                                getDataManager().setEnableDPEmployee(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("COD_STATUS_INTERVAL")) {
                                                getDataManager().setCodStatusInterval(Long.parseLong(globalConfigurationMaster.getConfigValue()));
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("COD_STATUS_INTERVAL_FRACTION")) {
                                                getDataManager().setCodStatusIntervalStatusFraction(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RESCHEDULE_MAX_ATTEMPTS")) {
                                                getDataManager().setRescheduleMaxAttempts(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RESCHEDULE_MAX_ATTEPMTS_ALLOWED_DAYS")) {
                                                getDataManager().setRescheduleMaxDaysAllow(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("IS_USE_CAMSCANNER_PRINT_RECEIPT")) {
                                                getDataManager().setIsUseCamscannerPrintReceipt(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("IS_USE_CAMSCANNER_DISPUTE")) {
                                                getDataManager().setIsUseCamscannerDispute(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("IS_USE_CAMSCANNER_TRIP")) {
                                                getDataManager().setIsUseCamscannerTrip(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("SATHI_LOG_API_CALL_INTERVAL")) {
                                                getDataManager().setSathiLogApiCallInterval(Long.parseLong(globalConfigurationMaster.getConfigValue()));
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("SKIP_OTP_REV_RQC")) {
                                                getDataManager().setSKIPOTPREVRQC(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("SKIP_CANC_OTP_RVP")) {
                                                getDataManager().setSKIP_CANC_OTP_RVP(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("FORWARD_REASSIGN")) {
                                                getDataManager().setFWDRessign(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("DISTANCE_GAP_FOR_DIRECTION_CAL")) {
                                                getDataManager().setDistance(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("offline_fwd")) {
                                                getDataManager().setOfflineFwd(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("CONSENT_AGREE_BUTTON_LABEL")) {
                                                getDataManager().setAdharPositiveButton(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("CONSENT_DISAGREE_BUTTON_LABEL")) {
                                                getDataManager().setAdharNegativeButton(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("DISABLE_RESEND_OTP_BUTTON_DURATION")) {
                                                getDataManager().setDisableResendOtpButtonDuration(Long.parseLong(globalConfigurationMaster.getConfigValue()));
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ESP_REFER_SCHEME_TERM_AND_CONDITIONS")) {
                                                getDataManager().setESPSchemeTerms(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ESP_REFERRAL_CODE")) {
                                                getDataManager().setESPReferCode(globalConfigurationMaster.getConfigValue());
                                            } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("FAKE_APPLICATIONS")) {
                                                if (!globalConfigurationMaster.getConfigValue().equalsIgnoreCase("")) {
                                                    getDataManager().setFakeApplicatons(globalConfigurationMaster.getConfigValue());
                                                    LocationTracker.setFakeApplications(getDataManager().getFakeApplications());
                                                }
                                            }
                                        }
                                        try {
                                            getAllDashboardBanner();
                                        } catch (Exception e) {
                                            Logger.e(TAG, String.valueOf(e));
                                        }
                                        boolean exists = false;
                                        if (masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCallbridgeConfiguration().getCb_pstn_options() != null && !masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCallbridgeConfiguration().getCb_pstn_options().isEmpty()) {
                                            getDataManager().setPSTNType(masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCallbridgeConfiguration().getCb_calling_type());
                                            if (getDataManager().getPstnFormat().equalsIgnoreCase("")) {
                                                getDataManager().setPstnFormat(masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCallbridgeConfiguration().getCb_pstn_options().get(0).getPstn_format());
                                            } else {
                                                for (int i = 0; i < masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCallbridgeConfiguration().getCb_pstn_options().size(); i++) {
                                                    if (getDataManager().getPstnFormat().equalsIgnoreCase(masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCallbridgeConfiguration().getCb_pstn_options().get(i).pstn_format)) {
                                                        getDataManager().setPstnFormat(masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCallbridgeConfiguration().getCb_pstn_options().get(i).getPstn_format());
                                                        exists = true;
                                                        break;
                                                    }
                                                }
                                            }
                                            if (!exists) {
                                                getDataManager().setPstnFormat(masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCallbridgeConfiguration().getCb_pstn_options().get(0).getPstn_format());
                                            }
                                        }
                                    } catch (Exception e) {
                                        setIsLoading(false);
                                        if (masterDataReasonCodeResponse.getResponse().getCode() == 107) {
                                            getNavigator().doLogout(masterDataReasonCodeResponse.getResponse().getDescription());
                                        }
                                        Logger.e(TAG, String.valueOf(e));
                                    }
                                }
                            } catch (Exception ex) {
                                setIsLoading(false);
                                String error;
                                error = new RestApiErrorHandler(ex).getErrorDetails().getEResponse().getDescription();
                                try {
                                    if (error.equalsIgnoreCase("HTTP 500 ")) {
                                        getDataManager().setSycningBlokingStatus(false);
                                        getNavigator().showErrorMessage(true);
                                    } else {
                                        Logger.e(TAG, String.valueOf(ex));
                                        RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(ex.getCause());
                                        restApiErrorHandler.writeErrorLogs(timeStamp, ex.getMessage());
                                    }
                                } catch (Exception e) {
                                    RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                                    restApiErrorHandler.writeErrorLogs(timeStamp, e.getMessage());
                                    Logger.e(TAG, String.valueOf(e));
                                }
                            }
                        } else {
                            if (masterDataReasonCodeResponse.getResponse().getCode() == 107) {
                                getNavigator().doLogout(masterDataReasonCodeResponse.getResponse().getDescription());
                            }
                        }
                    });
                }
                return Single.just(true);
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(o -> {
                try {
                    setIsLoading(false);
                    if (loginResponse.getSResponse().isPasswordResetRequired()) {
                        getNavigator().showChangePassword();
                        return;
                    }
                    if (loginResponse.getSResponse().isOtpRequired()) {
                        OTPRequiredTrue = true;
                        return;
                    }
                    if (loginResponse.getSResponse().getApkUpdateResponse().getVersion_status() == 2) {
                        getNavigator().showAPKForceUpdate(loginResponse.getSResponse().getApkUpdateResponse());
                    } else if (loginResponse.getSResponse().getApkUpdateResponse().getVersion_status() == 1) {
                        if (!loginResponse.getSResponse().isOtpRequired()) {
                            getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_SERVER);
                        }
                        getNavigator().showAPKSoftUpdate(loginResponse, loginResponse.getSResponse().getApkUpdateResponse());
                    } else if (loginResponse.getSResponse().isOtpRequired()) {
                        getNavigator().startLoginVerifyOTPActivity();
                    } else {
                        getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_SERVER);
                        LoginViewModel.this.getNavigator().onSuccess();
                    }
                } catch (Exception e) {
                    getNavigator().onHandleError(e.getMessage());
                    Logger.e(TAG, String.valueOf(e));
                }
            }, throwable -> {
                try {
                    setIsLoading(false);
                    getDataManager().setSycningBlokingStatus(false);
                    getNavigator().showErrorMessage(Objects.requireNonNull(throwable.getMessage()).contains("HTTP 500"));
                    RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(throwable.getCause());
                    restApiErrorHandler.writeErrorLogs(timeStamp, throwable.getMessage());
                } catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                }
            }));
        } catch (Exception e) {
            setIsLoading(false);
            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
            restApiErrorHandler.writeErrorLogs(timeStamp, e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void saveRVPShipperId(Reverse reverse) {
        try {
            getCompositeDisposable().add(getDataManager().insertRvpShipperId(reverse).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
            }, Throwable::printStackTrace));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void getAllDashboardBanner() {
        try {
            getCompositeDisposable().add(getDataManager().getAllDashboardBanner().observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(dashboardBannerList -> {
            }));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void saveShipperId(Forward response) {
        try {
            getCompositeDisposable().add(getDataManager().insertFwdShipperId(response).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {

            }, Throwable::printStackTrace));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public boolean isEmailAndPasswordValid(String email, String password) {
        // Validate email and password
        if (email == null || email.isEmpty()) {
            return false;
        }
        return !(password == null || password.isEmpty());
    }

    public void onPinclick() {
        getNavigator().onPinClick();
    }

    public void onTouchFingerPrintClick() {
    }

    public boolean isOTPRequiredTrue() {
        return OTPRequiredTrue;
    }

    public void saveApiUrl(List<ApiUrlData> response) {
        try {
            getCompositeDisposable().add(getDataManager().insertAllApiUrls(response).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
                getAllApiUrl();
            }, Throwable::printStackTrace));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void getAllApiUrl() {
        try {
            getCompositeDisposable().add(getDataManager().getAllApiUrl().subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(apiUrlData -> {
                for (int i = 0; i < apiUrlData.size(); i++) {
                    SathiApplication.hashMapAppUrl.put(apiUrlData.get(i).getApiUrlKey(), apiUrlData.get(i).getApiUrl());
                }
            }));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void verifyOtp(String status) {
        setIsLoading(true);
        try {
            String empCode = getDataManager().getEmp_code();
            LoginVerifyOtpRequest request = new LoginVerifyOtpRequest(empCode, "", true, status);
            final long timeStamp = System.currentTimeMillis();
            writeRestAPIRequst(timeStamp, request);
            getCompositeDisposable().add(getDataManager().doLoginVerifyOtpApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request).doOnSuccess(loginVerifyOtpResponse -> writeRestAPIResponse(timeStamp, loginVerifyOtpResponse)).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                LoginViewModel.this.setIsLoading(false);
                if (response.isStatus()) {
                    getNavigator().onNOTPNext(status);
                } else {
                    if (response.getsResponse().getCode() != 107) {
                        getNavigator().onNOTPNext(status);
                    }
                }
            }, throwable -> {
                setIsLoading(false);
                String error;
                try {
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                    getNavigator().showErrorMessage(error.contains("HTTP 500 "));
                } catch (NullPointerException e) {
                    Logger.e(TAG, String.valueOf(e));
                }
            }));
        } catch (Exception e) {
            getNavigator().onHandleError(e.getMessage());
            setIsLoading(false);
            getNavigator().onHandleError(new RestApiErrorHandler(e.fillInStackTrace()).getErrorDetails().getEResponse().getDescription());
        }
    }

    //LogIn New CheckAttendnace
    public void checkAttendance(Activity context, String empCode) {
        setIsLoading(true);
        try {
            final long timeStamp = System.currentTimeMillis();
            writeRestAPIRequst(timeStamp, empCode);
            SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.CHECK_ATTENDANCE);
            getCompositeDisposable().add(getDataManager()
                    .doCheckAttendanceApiCall(getDataManager().getAuthToken(), empCode)
                    .doOnSuccess(checkAttendanceResponse -> writeRestAPIResponse(timeStamp, checkAttendanceResponse))
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(response -> {
                        LoginViewModel.this.setIsLoading(false);
                        Intent intent;
                        if (response.isStatus()) {
                            intent = new Intent(context, DashboardActivity.class);
                        }
                        else {
                            intent = (disanceformDestination < getDataManager().getDCRANGE()) ?
                                    new Intent(context, CameraSelfieActivity.class) :
                                    new Intent(context, MapActivity.class);
                        }
                        context.startActivity(intent);
                        context.finish();
                        applyTransitionToOpenActivity(context);
                    }, throwable -> {
                        setIsLoading(false);
                        getNavigator().showError("Check Attendance API Failed");
                    }));
        } catch (Exception e) {
            getNavigator().showError("Check Attendance API Failed");
            setIsLoading(false);
        }
    }

    public double getDistanceBetweenLocations(com.google.maps.model.LatLng destination) {
        try {
            double distance;
            GeoApiContext context = new GeoApiContext().setApiKey(DISTANCE_API_KEY);
            DirectionsResult result = DirectionsApi.newRequest(context).mode(TravelMode.DRIVING).units(Unit.METRIC).origin(new com.google.maps.model.LatLng(getDataManager().getCurrentLatitude(), getDataManager().getCurrentLongitude())).optimizeWaypoints(true).destination(destination).awaitIgnoreError();
            String dis = (result.routes[0].legs[0].distance.humanReadable);
            if (dis.endsWith("km")) {
                distance = Double.parseDouble(dis.replaceAll("[^\\.0123456789]", "")) * 1000;
            } else {
                distance = Double.parseDouble(dis.replaceAll("[^\\.0123456789]", ""));
            }
            return distance;
        } catch (Exception e) {
            Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
        }
        return 0.0;
    }
}