package in.ecomexpress.sathi.repo.local.pref;

import static in.ecomexpress.sathi.repo.IDataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import in.ecomexpress.sathi.di.PreferenceInfo;
import in.ecomexpress.sathi.repo.DataManager;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.login.LoginResponse;

@Singleton
public class PreferenceHelper implements IPreferenceHelper {
    private static final String COUNTER_DELIVERY = "counter_delivery";
    private static final String SELECTED_DRS_VIEW = "selected_drs_view";
    private static final String MULTISPACE_APPS = "MULTISPACE_APPS";
    private static final String IMAGE_MANUAL_FLYER = "IMAGE_MANUAL_FLYER";
    private static final String address_quality_score = "address_quality_score";
    private static final String FEEDBACK_MESSAGE = "FEEDBACK_MESSAGE";
    private static final String BLUR_IMAGE_TYPE = "BLUR_IMAGE_TYPE";
    private static final String SATHI_ATTENDANCE_FEATURE_ENABLE = "SATHI_ATTENDANCE_FEATURE_ENABLE";
    private static final String CHECK_ATTENDANCE_LOGIN_STATUS = "CHECK_ATTENDANCE_LOGIN_STATUS";

    private static final String SCREEN_STATUS = "SCREEN_STATUS";
    private static final String BP_MISMATCH = "BP_MISMATCH";
    private static final String UD_BP = "UD_BP";
    private static final String OBD_REFUSED = "OBD_REFUSED";
    private static final String OBD_QC_FAIL = "OBD_QC_FAIL";
    private static final String HIDE_CAMERA = "HIDE_CAMERA";
    private static final String ROOT_DISABLED = "root_disabled";
    private final String CONSIGNEE_PROFILING_ENABLE = "consignee_profiling";
    private static final String SELECTED_SORTING = "selected_sorting";
    private static final String IS_CALL_DONE = "IS_CALL_DONE";
    private static final String IAMGE_URI = "IAMGE_URI";
    private static final String FORWARD_REASSIGN = "FORWARD_REASSIGN";
    private static final String SOS_NUMBERS = "sos_numbers";
    private static final String RTS_UNDELIVER_REASON_CODE = "RTS_UNDELIVER_REASON_CODE";
    private static final String V_CALL_POPUP = "V_CALL_POPUP";
    private static final String START_QC_LAT = "START_QC_LAT";
    private static final String START_QC_LNG = "START_QC_LNG";
    private static final String OFD_VERIFIED_STATUS = "OFD_VERIFIED_STATUS";
    private static final String SMS_THROUGH_WHATSAPP = "SMS_THROUGH_WHATSAPP";
    private static final String TECHPAK_WHATSAPP = "TECHPAK_WHATSAPP";
    private static final String TRIEDREACHYOU_WHATSAPP = "TRIEDREACHYOU_WHATSAPP";
    private static final String IS_MASTER_AVAILABLE = "IS_MASTER_AVAILABLE";
    private static final String FAKE_APPLICATIONS = "FAKE_APPLICATIONS";
    private static final String LOCATION_ACCURACY = "LOCATION_ACCURACY";
    private static final String PSTN_TYPE = "PSTN_TYPE";
    private static final String START_TRIP_TIME = "START_TRIP_TIME";
    private static final String RTS_RESEND_OTP_STATUS = "RTS_RESEND_OTP_STATUS";
    private static final String SOS_TEMPLATE = "sos_template";
    private static final String FILTER_COUNT = "FILTER_COUNT";
    private static final String SYNC_BLOCK_STATUS = "SYNC_BLOCK_STATUS";
    private static final String DISABLE_RESEND_OTP_BUTTON_DURATION = "DISABLE_RESEND_OTP_BUTTON_DURATION";
    private static final String CONSENT_AGREE = "CONSENT_AGREE";
    private static final String CONSENT_DISAGREE = "CONSENT_DISAGREE";
    private static final String ADM_UPDATED = "ADM_UPDATED";
    private static final String OFFLINE_FWD = "OFFLINE_FWD";
    private static final String LIVE_TRACKING_COUNT = "LIVE_TRACKING_COUNT";
    private static final String TOTAL_DISTANCE_WITH_SPEED = "TOTAL_DISTANCE_WITH_SPEED";
    private static final String PAYMENT_TYPE = "PAYMENT_TYPE";
    private static final String Login_PERMISSION = "Login_PERMISSION";
    private static final String RVP_RQC_BARCODE_SCAN = "RVP_RQC_BARCODE_SCAN";
    private static final String DRS_ID = "drs_id";
    private static final String SATHI_LOG_API_CALL_INTERVAL = "SATHI_LOG_API_CALL_INTERVAL";
    private static final String RESCHEDULE_MAX_ATTEMPTS = "RESCHEDULE_MAX_ATTEMPTS";
    private static final String RESCHEDULE_MAX_ALLOWED_DAYS = "RESCHEDULE_MAX_ALLOWED_DAYS";
    private static final String COD_STATUS_INTERVAL = "COD_STATUS_INTERVAL";
    private static final String COD_STATUS_INTERVAL_FRACTION = "COD_STATUS_INTERVAL_FRACTION";
    private static final String LOCATION_COUNT = "location_count";
    private static final String ENABLE_DP_EMPLOYEE = "ENABLE_DP_EMPLOYEE";
    private static final String ADM_EMP_FLAG = "adm_emp_flag";
    private static final String FWD_PRICE = "fwd_price";
    private static final String PPD_PRICE = "ppd_price";
    private static final String COD_PRICE = "cod_price";
    private static final String RQC_PRICE = "rqc_price";
    private static final String EDS_PRICE = "eds_price";
    private static final String RVP_PRICE = "rvp_price";
    private static final String RTS_PRICE = "rts_price";
    private static final String DIRECT_DELIVER = "DIRECT_DELIVER";
    private static final String CALL_STATUS_API_INTERVAL = "CALL_STATUS_API_INTERVAL";
    private static final String CURRENT_TRIP_AMOUNT = "current_trip_amount";
    private static final String IS_USE_CAMSCANNER_PRINT = "IS_USE_CAMSCANNER_PRINT";
    private static final String IS_USE_CAMSCANNER_DISPUTE = "IS_USE_CAMSCANNER_DISPUTE";
    private static final String IS_USE_CAMSCANNER_TRIP = "IS_USE_CAMSCANNER_TRIP";
    private static final String LIVE_TRACKING_DEVICE_MOVEMENT_MIN_SPEED = "LIVE_TRACKING_DEVICE_MOVEMENT_MIN_SPEED";
    private static final String LIVE_TRACKING_LAT_LNG_ACCURACY = "LIVE_TRACKING_LAT_LNG_ACCURACY";
    private static final String LIVE_TRACKING_LAT_LNG_MIN_DISPLACEMENT = "LIVE_TRACKING_LAT_LNG_MIN_DISPLACEMENT";
    private static final String LIVE_TRACKING_LAT_LNG_TIME_INTERVAL = "LIVE_TRACKING_LAT_LNG_TIME_INTERVAL";
    private static final String REQUEST_RESPONSE_TIME_INTERVAL = "REQUEST_RESPONSE_TIME_INTERVAL";
    private static final String covid_image_url = "covid_image_url";
    private static final String ecom_dlv_region = "ecom_dlv_region";
    private static final String is_covid_consent_captured = "is_covid_consent_captured";
    private static final String IS_EDISPUTE_IMAGE_MANDATORY = "IS_EDISPUTE_IMAGE_MANDATORY";
    private static final String IS_CALL_BRIDGE_CHECK_STATUS = "IS_CALL_BRIDGE_CHECK_STATUS";
    private static final String FE_REACH_DC = "fe_reached_dc";
    private static final String UNDELIVER_CONSIGNEE_RANGE = "UNDELIVER_CONSIGNEE_RANGE";
    private final String LAT_LNG_LIMIT = "LAT_LNG_LIMIT";
    private final String IS_SIGNATURE_IMAGE_MANDATORY = "IS_SIGNATURE_IMAGE_MANDATORY";
    private static final String APK_DOWNLOAD_IS_IN_PROCESS = "apk_download_is_in_process";
    private final String PREF_KEY_AUTH_TOKEN = "auth_token";
    private final String CALL_STATUS_API_RECURSION = "CALL_STATUS_API_RECURSION";
    private final String UNDELIVER_COUNT = "UNDELIVER_COUNT";
    private final String PREF_KEY_SERVICE_CENTER = "service_center";
    private static final String QUALITY_IMAGE_ID = "quality_image_id";
    private final String PREF_KEY_NAME = "name";
    private final String STOP_TRACKING_ALERT = "STOP_TRACKING_ALERT";
    private final String PREF_KEY_PSTN = "pstn_format";
    private final String PREF_KEY_DESIGNATION = "designation";
    private final String RVP_SECURE_IS_OTP_VERIFIED = "RVP_SECURE_IS_OTP_VERIFIED";
    private final String PREF_KEY_MOBILE = "mobile";
    private final String MAX_EDS_EKYC_FAIL_ATTEMPT = "MAX_EDS_EKYC_FAIL_ATTEMPT";
    private final String PREF_KEY_VODA_ORDER = "order_no";
    private final String PREF_KEY_LOCTAION_TYPE = "location_type";
    private final String PREF_KEY_LOCATION_CODE = "location_code";
    private final String PREF_KEY_CODE = "code";
    private final String PREF_KEY_IS_USER_VALIDATED = "is_user_validated";
    private final String PREF_KEY_PHOTO_URL = "photo_url";
    private final String PREF_KEY_USER_LOGGED_IN_MODE = "logged_in_mode";
    private final String PREF_KEY_USER_PINCODE = "user_pincode";
    private final String PREF_KEY_TRIP_ID = "trip_id";
    private final String PREF_KEY_Actual_Meter = "actual_meter";
    private final String PREF_VEHICLETYPE = "vehicle_type";
    private final String PREF_TYPEOFVEHICLE = "type_of_vehicle";
    private final String PREF_ROUTENAME = "route_name";
    private final String SKIP_OTP_REV_RQC = "SKIP_OTP_REV_RQC";
    private final String SKIP_CANC_OTP_RVP = "SKIP_CANC_OTP_RVP";
    private final String AADHAR_STATUS = "AADHAR_STATUS";
    private final String METER_READING_START_TRIP = "start_trip_meter_reading";
    private final String METER_READING_STOP_TRIP = "stop_trip_meter_reading";
    private final String CALL_IT_EXECUTIVE = "call_it_executive";
    private final String NOTIFICATION_COUNTER = "notificaiton_counter";
    private final String LIVE_TRACKING_DEVICE_MOVEMENT_SHORTEST_SPEED = "LIVE_TRACKING_DEVICE_MOVEMENT_SHORTEST_SPEED";
    private final String SYNC_DELAY = "sync_delay";
    private final String CURRENT_LATITUDE = "current_latitude";
    private final String CURRENT_LONGITUDE = "current_longitude";
    private final String Direction_Distacne = "direction_distance";
    private final String Direction_TotalDistance = "direction_total_distance";
    private final String DC_LATITUDE = "dc_latitude";
    private final String DC_LONGITUDE = "dc_longitude";
    private final String IS_RECENT_REMOVED = "IS_RECENT_REMOVED";
    private final String LIVE_TRACKING_ID_FOR_API = "LIVE_TRACKING_ID_FOR_API";
    private final String SET_END_TRIP_TIME = "end_trip_time";
    private final String SET_END_TRIP_KM = "end_trip_km";
    private final String EDS_ACTIVITY_CODES = "EDS_ACTIVITY_CODES";
    private final String ESP_REFER_SCHEME_TERM_AND_CONDITIONS = "ESP_REFER_SCHEME_TERM_AND_CONDITIONS";
    private final String ESP_REFERRAL_CODE = "ESP_REFERRAL_CODE";
    private final String MAX_DAILY_DIFF_FOR_START_TRIP = "MAX_DAILY_DIFF_FOR_START_TRIP";
    private final String MAX_TRIP_RUN_FOR_STOP_TRIP = "MAX_TRIP_RUN_FOR_STOP_TRIP";
    private static final String DC_LINE1 = "dc_line1";
    private static final String DC_LINE2 = "dc_line2";
    private static final String DC_LINE3 = "dc_line3";
    private static final String DC_LINE4 = "dc_line4";
    private static final String DC_CITY = "dc_city";
    private static final String DC_STATE = "dc_state";
    private static final String DC_PINCODE = "dc_pincode";
    private static final String PRIVATE_KEY = "private_key";
    private static final String IS_START_TRIP = "IS_START_TRIP";
    private static final String IS_KIRANA_USER = "IS_KIRANA_USER";
    private static final String FORWARD_REASON_CODE_FLAG = "forward_flag";
    private static final String RTS_REASON_CODE_FLAG = "rts_flag";
    private static final String RTS_INPUT_OTP_RESEND_FLAG = "rts_input_flag";
    private static final String RVP_REASON_CODE_FLAG = "rvp_flag";
    private static final String EDS_REASON_CODE_FLAG = "eds_flag";
    private static final String BOTTOM_TEXT = "bottom_text";
    private static final String DELAY_TIME = "delay_time";
    private static final String TYPEID = "type_id";
    private static final String ProgressiveTimer = "timer";
    private static final String ReassignTimer = "timer";
    private static final String VEHICLETYPEID = "vehicle_type_id";
    private static final String WEBLINK_URL = "weblink_url";
    private static final String LiveTrackingLatLngAccuracy = "livetrackinglatlngaccuracy";
    private static final String START_STOP_KM_DIFF = "start_stop_km_diff";
    private static final String ACTIVITY_CODE = "activity_code";
    private static final String TRIP_GEOFENCING = "trip_geofencing";
    private static final String FWD_UNATTEMPTED_CODE = "fwd_unattempted_code";
    private static final String RVP_UNATTEMPTED_CODE = "rvp_unattempted_code";
    private static final String RTS_UNATTEMPTED_CODE = "rts_unattempted_code";
    private static final String EDS_UNATTEMPTED_CODE = "eds_unattempted_code";
    private static final String EDS_REAL_TIME_SYNC = "eds_real_time_sync";
    private static final String RVP_REAL_TIME_SYNC = "rvp_real_time_sync";
    private static final String EMP_CODE = "emp_code";
    private static final String AADHAR_FRONT_IMAGE = "aadhar_front_image";
    private static final String AADHAR_REAR_IMAGE = "aadhar_rear_image";
    private static final String AADHAR_FRONT_IMAGE_NAME = "aadhar_rear_image_name";
    private static final String AADHAR_REAR_IMAGE_NAME = "aadhar_rear_image_name";
    private static final String CONSIGNEE_PROFILE = "consignee_profile_value";
    private static final String STOP_TRIP_DATE = "stop_trip_date";
    private static final String DC_RANGE = "dc_range";
    private static final String REQUEST_RESPONSE_TIME = "REQUEST_RESPONSE_TIME";
    private static final String REQUEST_RESPONSE_COUNT = "REQUEST_RESPONSE_COUNT";
    private static final String AADHAR_STATUS_CODE = "AADHAR_STATUS_CODE";
    private static final String PREVIOUS_TRIP_STATUS = "previous_trip_status";
    private static final String DC_LOCATION_UPDATE = "dc_location_update";
    private static final String RESCHEDULE_DATE = "eds_reschedule_date";
    private static final String ALLWO_DUPLICATE_CASH_RECEIPT = "allow_duplicate_cash_receipt";
    private static final String AMAZON_OTP_VALUE = "amazon_otp_value";
    private static final String AMAZON_PINB_VALUE = "amazon_pinb_value";
    private static final String SHIPPER_ID = "shipper_id";
    private static final String AADHAR_STATUS_INTERVL = "AADHAR_STATUS_INTERVL";
    private static final String AMAON_OTP_STATUS = "AMAON_OTP_STATUS";
    private static final String AMAZAON_OTP_TIMMING = "AMAZAON_OTP_TIMMING";
    private static final String PINB_OTP_STATUS = "PINB_OTP_STATUS";
    private static final String PINB_OTP_TIMMING = "PINB_OTP_TIMMING";
    private static final String AMAZON_LIST = "amazon_list";
    private static final String ADHAR_CONSENT = "adhar_consent";
    private static final String MAP_DRIVING_MODE = "mapDrivingMode";
    private static final String ADHAR_MESSAGE = "adhar_message";
    private static final String LOGIN_MONTH = "login_month";
    private static final String IS_QRCODE_SCANNED = "IS_QRCODE_SCANNED";
    private static final String LIVE_TRACKING_TRIP_ID = "live_tracking_trip_id";
    private static final String LOGIN_DATE = "LOGIN_DATE";
    private static final String RVP_AWB_WORDS = "RVP_AWB_WORDS";
    private static final String RVP_UD_FLYER = "RVP_UD_FLYER";
    private static final String LIVE_TRACKING_MAX_FILE_SIZE = "LIVE_TRACKING_MAX_FILE_SIZE";
    private static final String DLIGHT_ENCRPTED_SUCCESS_OTP = "dlight_encrpted_success_otp";
    private static final String DLIGHT_ENCRPTED_SUCCESS_OTP_TYEP = "dlight_encrpted_success_otp_type";
    private static final String API_AVAILABLE_FLAG = "api_available_flag";
    private static final String ENABLE_DIRECT_DIAL = "ENABLE_DIRECT_DIAL";
    private static final String ENABLE_RTS_EMAIL = "ENABLE_RTS_EMAIL";
    private static final String SAME_DAY_RESCHEDULE = "SAME_DAY_RESCHEDULE";
    private static final String ENABLE_RTS_IMAGE = "RTS_IMAGE";
    private static final String DEFAULT_STATISTICS = "DEFAULT_STATISTICS";
    private static final String IS_SCAN_AWB = "is_scan_awb";
    private static final String DC_UNDELIVER_STATUS = "dc_undeliver_status";
    private static final String DRS_TIME_STAMP = "DRS_TIME_STAMP";
    private static final String LIVE_TRACKING_CALCULATION_DISTANCE = "LIVE_TRACKING_CALCULATION_DISTANCE";
    private static final String GAP_DISTANCE = "GAP_DISTANCE";
    private static final String ESP_SCANNER = "ESP_SCANNER";
    private static final String MASTER_DATA_SYNC = "MASTER_DATA_SYNC";
    private static final String ESP_EARNING_VISIBILITY = "ESP_EARNING_VISIBILITY";
    private static final String ODH_VISIBILITY = "ODH_VISIBILITY";
    private static final String LOGIN_SERVER_TIMESTAMP = "LOGIN_SERVER_TIMESTAMP";
    private SharedPreferences mPrefs;
    private static final String CAMPAIGN_VISIBILITY = "CAMPAIGN_VISIBILITY";

    @Inject
    public PreferenceHelper(Context context, @PreferenceInfo String prefFileName){
        mPrefs = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
    }

    public PreferenceHelper(){
        super();
    }

    @Override
    public int getCurrentUserLoggedInMode(){
        return mPrefs.getInt(PREF_KEY_USER_LOGGED_IN_MODE, 0);
    }

    @Override
    public String getAuthToken(){
        return mPrefs.getString(PREF_KEY_AUTH_TOKEN, null);
    }

    @Override
    public int getTypeId(){
        return mPrefs.getInt(TYPEID, 0);
    }

    @Override
    public int getVehicleTypeId(){
        return mPrefs.getInt(VEHICLETYPEID, 0);
    }

    @Override
    public String getServiceCenter(){
        return mPrefs.getString(PREF_KEY_SERVICE_CENTER, null);
    }

    @Override
    public String getCallITExecutiveNo(){
        return mPrefs.getString(CALL_IT_EXECUTIVE, "02262820481");
    }

    @Override
    public void setCallITExecutiveNo(String number){
        mPrefs.edit().putString(CALL_IT_EXECUTIVE, number).apply();
    }

    @Override
    public void increaseNotificationCounter(){
        int counter = mPrefs.getInt(NOTIFICATION_COUNTER, 0);
        mPrefs.edit().putInt(NOTIFICATION_COUNTER, counter++);
    }

    @Override
    public int getNotificationCounter(){
        return mPrefs.getInt(NOTIFICATION_COUNTER, 0);
    }

    @Override
    public void setPstnFormat(String format){
        mPrefs.edit().putString(PREF_KEY_PSTN, format).apply();
    }

    @Override
    public String getPstnFormat(){
        return mPrefs.getString(PREF_KEY_PSTN, "");
    }

    @Override
    public double getCurrentLatitude(){
        return Double.parseDouble(mPrefs.getString(CURRENT_LATITUDE, "0.0"));
    }

    @Override
    public double getCurrentLongitude(){
        return Double.parseDouble(mPrefs.getString(CURRENT_LONGITUDE, "0.0"));
    }

    @Override
    public void setCurrentLatitude(double latitude){
        mPrefs.edit().putString(CURRENT_LATITUDE, String.valueOf(latitude)).apply();
    }

    @Override
    public void setCurrentLongitude(double longitude){
        mPrefs.edit().putString(CURRENT_LONGITUDE, String.valueOf(longitude)).apply();
    }

    @Override
    public double getDCLatitude(){
        return Double.parseDouble(mPrefs.getString(DC_LATITUDE, "0.0"));
    }

    @Override
    public double getDCLongitude(){
        return Double.parseDouble(mPrefs.getString(DC_LONGITUDE, "0.0"));
    }

    @Override
    public long isDownloadAPKIsInProcess(){
        return mPrefs.getLong(APK_DOWNLOAD_IS_IN_PROCESS, -1);
    }

    @Override
    public void updateDCDetails(LoginResponse.DcLocationAddress dcLocationAddress){
        mPrefs.edit().putString(DC_LINE1, dcLocationAddress.getLine1()).apply();
        mPrefs.edit().putString(DC_LINE2, dcLocationAddress.getLine2()).apply();
        mPrefs.edit().putString(DC_LINE3, dcLocationAddress.getLine3()).apply();
        mPrefs.edit().putString(DC_LINE4, dcLocationAddress.getLine4()).apply();
        mPrefs.edit().putString(DC_PINCODE, dcLocationAddress.getPincode()).apply();
        mPrefs.edit().putString(DC_CITY, dcLocationAddress.getCity()).apply();
        mPrefs.edit().putString(DC_STATE, dcLocationAddress.getState()).apply();
        mPrefs.edit().putString(DC_LATITUDE, String.valueOf(dcLocationAddress.getLocationLat())).apply();
        mPrefs.edit().putString(DC_LONGITUDE, String.valueOf(dcLocationAddress.getLocationLong())).apply();
    }

    @Override
    public void updateUserLoggedInState(IDataManager.LoggedInMode loggedInMode){
        mPrefs.edit().putInt(PREF_KEY_USER_LOGGED_IN_MODE, loggedInMode.getType()).apply();
    }

    @Override
    public void saveBottomText(String text){
        mPrefs.edit().putString(BOTTOM_TEXT, text).apply();
    }

    @Override
    public String getBottomText(){
        return mPrefs.getString(BOTTOM_TEXT, null);
    }

    @Override
    public void setWebLinkUrl(String webLinkUrl){
        mPrefs.edit().putString(WEBLINK_URL, webLinkUrl).apply();
    }

    @Override
    public String getWebLinkUrl(){
        return mPrefs.getString(WEBLINK_URL, "");
    }

    @Override
    public float getLIVETRACKINGLATLNGACCURACYl(){
        return Float.parseFloat(mPrefs.getString(LiveTrackingLatLngAccuracy, "100"));
    }

    @Override
    public void setLIVETRACKINGLATLNGACCURACY(String livetrackinglatlngaccuracy){
        mPrefs.edit().putString(LiveTrackingLatLngAccuracy, livetrackinglatlngaccuracy).apply();
    }
    @Override
    public void setStartStopTripMeterReadingDiff(int readingDiff){
        mPrefs.edit().putInt(START_STOP_KM_DIFF, readingDiff).apply();
    }

    @Override
    public String getTripGeofencing(){
        return mPrefs.getString(TRIP_GEOFENCING, "");
    }

    @Override
    public void setTripGeofencing(String tripGeofencing){
        mPrefs.edit().putString(TRIP_GEOFENCING, tripGeofencing).apply();
    }

    @Override
    public int getStartStopMeterReadingDiff(){
        return mPrefs.getInt(START_STOP_KM_DIFF, 50);
    }

    @Override
    public void setSelectedDRSView(int index){
        mPrefs.edit().putInt(SELECTED_DRS_VIEW, index).apply();
    }

    @Override
    public int getSelectedDRSView(){
        return mPrefs.getInt(SELECTED_DRS_VIEW, 0);
    }

    @Override
    public void setSelectedSorting(int index){
        mPrefs.edit().putInt(SELECTED_SORTING, index).apply();
    }

    @Override
    public int getSelectedSorting(){
        return mPrefs.getInt(SELECTED_SORTING, 0);
    }

    @Override
    public void setSOSNumbers(String sosNumbers){
        mPrefs.edit().putString(SOS_NUMBERS, sosNumbers).apply();
    }

    @Override
    public String getSOSNumbers(){
        return mPrefs.getString(SOS_NUMBERS, "");
    }

    @Override
    public void setSOSSMSTemplate(String template){
        mPrefs.edit().putString(SOS_TEMPLATE, template).apply();
    }

    @Override
    public String getSOSSMSTemplate(){
        return mPrefs.getString(SOS_TEMPLATE, "");
    }

    @Override
    public void setFEReachedDC(boolean b){
        mPrefs.edit().putBoolean(FE_REACH_DC, b).apply();
    }

    @Override
    public boolean getFEReachedDC(){
        return mPrefs.getBoolean(FE_REACH_DC, false);
    }

    @Override
    public void setFWDUnattemptedReasonCode(int reasonCode){
        mPrefs.edit().putInt(FWD_UNATTEMPTED_CODE, reasonCode).apply();
    }

    @Override
    public void setRVPUnattemptedReasonCode(int reasonCode){
        mPrefs.edit().putInt(RVP_UNATTEMPTED_CODE, reasonCode).apply();
    }

    @Override
    public void setRTSUnattemptedReasonCode(int reasonCode){
        mPrefs.edit().putInt(RTS_UNATTEMPTED_CODE, reasonCode).apply();
    }

    @Override
    public void setEDSUnattemptedReasonCode(int reasonCode){
        mPrefs.edit().putInt(EDS_UNATTEMPTED_CODE, reasonCode).apply();
    }

    @Override
    public int getImageQualityId(){
        return mPrefs.getInt(QUALITY_IMAGE_ID, 0);
    }

    @Override
    public void setImageQualityId(int image_id){
        mPrefs.edit().putInt(QUALITY_IMAGE_ID, image_id).apply();
    }

    @Override
    public int getFWDUnattemptedReasonCode(){
        return mPrefs.getInt(FWD_UNATTEMPTED_CODE, 22703);
    }

    @Override
    public int getRVPUnattemptedReasonCode(){
        return mPrefs.getInt(RVP_UNATTEMPTED_CODE, 408);
    }

    @Override
    public int getRTSUnattemptedReasonCode(){
        return mPrefs.getInt(RTS_UNATTEMPTED_CODE, 228);
    }

    @Override
    public int getEDSUnattemptedReasonCode(){
        return mPrefs.getInt(EDS_UNATTEMPTED_CODE, 2407);
    }

    @Override
    public void setConsigneeProfiling(boolean enable){
        mPrefs.edit().putBoolean(CONSIGNEE_PROFILING_ENABLE, enable).apply();
    }

    @Override
    public boolean getConsigneeProfiling(){
        return mPrefs.getBoolean(CONSIGNEE_PROFILING_ENABLE, false);
    }

    @Override
    public boolean getRootDeviceDisabled(){
        return mPrefs.getBoolean(ROOT_DISABLED, false);
    }

    @Override
    public void setRoodDeviceDisabled(boolean disabled){
        mPrefs.edit().putBoolean(ROOT_DISABLED, disabled).apply();
    }

    @Override
    public void setActivityCode(String activityCode){
        mPrefs.edit().putString(ACTIVITY_CODE, activityCode).apply();
    }

    @Override
    public String getActivityCode(){
        return mPrefs.getString(ACTIVITY_CODE, "");
    }

    @Override
    public void saveEDSRealTimeSync(String status){
        mPrefs.edit().putString(EDS_REAL_TIME_SYNC, status).apply();
    }

    @Override
    public String getEDSRealTimeSync(){
        return mPrefs.getString(EDS_REAL_TIME_SYNC, "false");
    }

    @Override
    public void saveRVPRealTimeSync(String status){
        mPrefs.edit().putString(RVP_REAL_TIME_SYNC, status).apply();
    }

    @Override
    public String getRVPRealTimeSync(){
        return mPrefs.getString(RVP_REAL_TIME_SYNC, "false");
    }

    @Override
    public void setEmp_code(String emp_code){
        mPrefs.edit().putString(EMP_CODE, emp_code).apply();
    }

    @Override
    public String getEmp_code(){
        return mPrefs.getString(EMP_CODE, "");
    }

    @Override
    public void setDcLatitude(String latitude){
        mPrefs.edit().putString(DC_LATITUDE, latitude).apply();
    }

    @Override
    public String getDcLatitude(){
        return mPrefs.getString(DC_LATITUDE, "");
    }

    @Override
    public void setDcLongitude(String longitude){
        mPrefs.edit().putString(DC_LONGITUDE, longitude).apply();
    }

    @Override
    public String getDcLongitude(){
        return mPrefs.getString(DC_LONGITUDE, "");
    }

    @Override
    public void setConsigneeProfileValue(String value){
        mPrefs.edit().putString(CONSIGNEE_PROFILE, value).apply();
    }

    @Override
    public String getConsigneeProfileValue(){
        return mPrefs.getString(CONSIGNEE_PROFILE, "");
    }

    @Override
    public void setStopTripDate(long current_date){
        mPrefs.edit().putLong(STOP_TRIP_DATE, current_date).apply();
    }

    @Override
    public long getStopTripDate(){
        return mPrefs.getLong(STOP_TRIP_DATE, 0);
    }

    @Override
    public int getDCRANGE(){
        return mPrefs.getInt(DC_RANGE, 20);
    }

    @Override
    public void setDCRANGE(int range){
        mPrefs.edit().putInt(DC_RANGE, range).apply();
    }

    @Override
    public int getREQUEST_RESPONSE_TIME() {
        return mPrefs.getInt(REQUEST_RESPONSE_TIME, 0);
    }

    @Override
    public void setREQUEST_RESPONSE_TIME(int time) {
        mPrefs.edit().putInt(REQUEST_RESPONSE_TIME, time).apply();
    }

    @Override
    public int getREQUEST_RESPONSE_COUNT() {
        return mPrefs.getInt(REQUEST_RESPONSE_COUNT, 0);
    }

    @Override
    public void setREQUEST_RESPONSE_COUNT(int count) {
        mPrefs.edit().putInt(REQUEST_RESPONSE_COUNT, count).apply();
    }

    @Override
    public void savePrivateKey(String privateKey){
        mPrefs.edit().putString(PRIVATE_KEY, privateKey).apply();
    }

    @Override
    public String getPrivateKey(){
        return mPrefs.getString(PRIVATE_KEY, "");
    }

    @Override
    public long getEndTripTime(){
        return mPrefs.getLong(SET_END_TRIP_TIME, 0);
    }

    @Override
    public void setEndTripTime(long end_trip_time){
        mPrefs.edit().putLong(SET_END_TRIP_TIME, end_trip_time).apply();
    }

    @Override
    public long getEndTripKm(){
        return mPrefs.getLong(SET_END_TRIP_KM, 0);
    }

    @Override
    public void setEndTripKm(long end_trip_km){
        mPrefs.edit().putLong(SET_END_TRIP_KM, end_trip_km).apply();
    }

    @Override
    public int getMaxDailyDiffForStartTrip(){
        return mPrefs.getInt(MAX_DAILY_DIFF_FOR_START_TRIP, 250);
    }

    @Override
    public void setMaxDailyDiffForStartTrip(String configValue){
        mPrefs.edit().putInt(MAX_DAILY_DIFF_FOR_START_TRIP, Integer.parseInt(configValue)).apply();
    }

    @Override
    public int getMaxTripRunForStopTrip(){
        return mPrefs.getInt(MAX_TRIP_RUN_FOR_STOP_TRIP, 250);
    }

    @Override
    public void setMaxTripRunForStopTrip(String configValue){
        mPrefs.edit().putInt(MAX_TRIP_RUN_FOR_STOP_TRIP, Integer.parseInt(configValue)).apply();
    }

    @Override
    public void setInternetApiAvailable(String flag){
        mPrefs.edit().putString(API_AVAILABLE_FLAG, flag).apply();
    }

    @Override
    public String getInternetApiAvailable(){
        return mPrefs.getString(API_AVAILABLE_FLAG, "false");
    }


    @Override
    public void setENABLEDIRECTDIAL(String flag){
        mPrefs.edit().putString(ENABLE_DIRECT_DIAL, flag).apply();
    }

    @Override
    public String getENABLEDIRECTDIAL(){
        return mPrefs.getString(ENABLE_DIRECT_DIAL, "false");
    }

    @Override
    public void setENABLERTSEMAIL(String flag){
        mPrefs.edit().putString(ENABLE_RTS_EMAIL, flag).apply();
    }

    @Override
    public String getENABLERTSEMAIL(){
        return mPrefs.getString(ENABLE_RTS_EMAIL, "false");
    }

    @Override
    public void setSAMEDAYRESCHEDULE(String flag){
        mPrefs.edit().putString(SAME_DAY_RESCHEDULE, flag).apply();
    }

    @Override
    public String getSAMEDAYRESCHEDULE(){
        return mPrefs.getString(SAME_DAY_RESCHEDULE, "false");
    }

    @Override
    public void setRTSIMAGE(String flag){
        mPrefs.edit().putString(ENABLE_RTS_IMAGE, flag).apply();
    }

    @Override
    public String getRTSIMAGE(){
        return mPrefs.getString(ENABLE_RTS_IMAGE, "false");
    }

    @Override
    public void setDEFAULTSTATISTICS(String flag){
        mPrefs.edit().putString(DEFAULT_STATISTICS, flag).apply();
    }

    @Override
    public String getDEFAULTSTATISTICS(){
        return mPrefs.getString(DEFAULT_STATISTICS, "false");
    }

    @Override
    public void setDuplicateCashReceipt(String configValue){
        mPrefs.edit().putString(ALLWO_DUPLICATE_CASH_RECEIPT, configValue).apply();
    }

    @Override
    public String getDuplicateCashReceipt(){
        return mPrefs.getString(ALLWO_DUPLICATE_CASH_RECEIPT, "");
    }

    @Override
    public void setAmazonOTPStatus(String b){
        mPrefs.edit().putString(AMAON_OTP_STATUS, b).apply();
    }

    @Override
    public void setAmazonOTPTiming(long timeInMillis){
        mPrefs.edit().putLong(AMAZAON_OTP_TIMMING, timeInMillis).apply();
    }

    @Override
    public String getAmazonOTPStatus(){
        return mPrefs.getString(AMAON_OTP_STATUS, "");
    }

    @Override
    public long getAmazonOTPTiming(){
        return mPrefs.getLong(AMAZAON_OTP_TIMMING, 0L);
    }

    @Override
    public void setPinBOTPStatus(String b){
        mPrefs.edit().putString(PINB_OTP_STATUS, b).apply();
    }

    @Override
    public void setAmazonOTPValue(String otp_value){
        mPrefs.edit().putString(AMAZON_OTP_VALUE, otp_value).apply();
    }

    @Override
    public void setAmazonPinbValue(String pinb_value){
        mPrefs.edit().putString(AMAZON_PINB_VALUE, pinb_value).apply();
    }

    @Override
    public void setAmazonList(String amazonList){
        mPrefs.edit().putString(AMAZON_LIST, amazonList).apply();
    }

    @Override
    public String getAmazonList(){
        return mPrefs.getString(AMAZON_LIST, "");
    }

    @Override
    public String getAmazonOTPValue(){
        return mPrefs.getString(AMAZON_OTP_VALUE, "");
    }

    @Override
    public String getAmazonPinbValue(){
        return mPrefs.getString(AMAZON_PINB_VALUE, "");
    }

    @Override
    public void setPinBOTPTimming(long timeInMillis){
        mPrefs.edit().putLong(PINB_OTP_TIMMING, timeInMillis).apply();
    }

    @Override
    public String getPinBOTPStatus(){
        return mPrefs.getString(PINB_OTP_STATUS, "");
    }

    @Override
    public long getPinBOTPTimming(){
        return mPrefs.getLong(PINB_OTP_TIMMING, 0L);
    }

    @Override
    public void setSyncDelay(long configValue){
        mPrefs.edit().putLong(SYNC_DELAY, configValue).apply();
    }

    @Override
    public long getSyncDelay(){
        return mPrefs.getLong(SYNC_DELAY, 5000);
    }

    @Override
    public void setLoginDate(String date){
        mPrefs.edit().putString(LOGIN_DATE, date).apply();
    }

    @Override
    public String getLoginDate(){
        return mPrefs.getString(LOGIN_DATE, "");
    }

    @Override
    public void setIsScanAwb(boolean isScanAwb){
        mPrefs.edit().putBoolean(IS_SCAN_AWB, isScanAwb).apply();
    }

    @Override
    public void setDcUndeliverStatus(boolean dcUndeliverStatus){
        mPrefs.edit().putBoolean(DC_UNDELIVER_STATUS, dcUndeliverStatus).apply();
    }

    @Override
    public boolean getIsScanAwb(){
        return mPrefs.getBoolean(IS_SCAN_AWB, false);
    }

    @Override
    public boolean getDcUndeliverStatus(){
        return mPrefs.getBoolean(DC_UNDELIVER_STATUS, false);
    }

    @Override
    public void setAdharConsent(String aadhaar_consent){
        mPrefs.edit().putString(ADHAR_CONSENT, aadhaar_consent).apply();
    }

    @Override
    public String getAdharConsent(){
        return mPrefs.getString(ADHAR_CONSENT, "Y");
    }


    @Override
    public void setMAP_DRIVING_MODE(String mapDrivingMode){
        mPrefs.edit().putString(MAP_DRIVING_MODE, mapDrivingMode).apply();
    }

    @Override
    public String getMAP_DRIVING_MODE(){
        return mPrefs.getString(MAP_DRIVING_MODE, "bicycling");
    }


    @Override
    public void setLiveTrackingTripId(String live_tracking_trip_id){
        mPrefs.edit().putString(LIVE_TRACKING_TRIP_ID, live_tracking_trip_id).apply();
    }

    @Override
    public String getLiveTrackingTripId(){
        return mPrefs.getString(LIVE_TRACKING_TRIP_ID, "");
    }

    @Override
    public void setLiveTrackingMaxFileSize(int parseInt){
        mPrefs.edit().putInt(LIVE_TRACKING_MAX_FILE_SIZE, parseInt).apply();
    }

    @Override
    public int getLiveTrackingMaxFileSize(){
        return mPrefs.getInt(LIVE_TRACKING_MAX_FILE_SIZE, 0);
    }

    @Override
    public void setAdharMessage(String configValue){
        mPrefs.edit().putString(ADHAR_MESSAGE, configValue).apply();
    }

    @Override
    public String getAdharMessage(){
        return mPrefs.getString(ADHAR_MESSAGE, "");
    }

    @Override
    public void setRVPAWBWords(String configValue){
        mPrefs.edit().putString(RVP_AWB_WORDS, configValue).apply();
    }

    @Override
    public String getRVPAWBWords(){
        return mPrefs.getString(RVP_AWB_WORDS, "");
    }

    @Override
    public void setRVP_UD_FLYER(String flyerValue){
        mPrefs.edit().putString(RVP_UD_FLYER, flyerValue).apply();
    }

    @Override
    public String getRVP_UD_FLYER(){
        return mPrefs.getString(RVP_UD_FLYER, "");
    }

    @Override
    public void setAadharFrontImage(String front_image_id){
        mPrefs.edit().putString(AADHAR_FRONT_IMAGE, front_image_id).apply();
    }

    @Override
    public void setAadharRearImage(String rear_image_id){
        mPrefs.edit().putString(AADHAR_REAR_IMAGE, rear_image_id).apply();
    }

    @Override
    public String getAadharFrontImage(){
        return mPrefs.getString(AADHAR_FRONT_IMAGE, "");
    }

    @Override
    public String getAadharRearImage(){
        return mPrefs.getString(AADHAR_REAR_IMAGE, "");
    }

    @Override
    public void setAadharStatusInterval(String configValue){
        mPrefs.edit().putString(AADHAR_STATUS_INTERVL, configValue).apply();
    }

    @Override
    public String getAadharStatusInterval(){
        return mPrefs.getString(AADHAR_STATUS_INTERVL, "");
    }

    @Override
    public void setAadharStatus(boolean b){
        mPrefs.edit().putBoolean(AADHAR_STATUS, b).apply();
    }

    @Override
    public boolean getAadharStatus(){
        return mPrefs.getBoolean(AADHAR_STATUS, false);
    }

    @Override
    public void setAadharStatusCode(int i){
        mPrefs.edit().putInt(AADHAR_STATUS_CODE, i).apply();
    }

    @Override
    public int getAadharStatusCode(){
        return mPrefs.getInt(AADHAR_STATUS_CODE, -1);
    }

    @Override
    public void setStopTrackingAlertFlag(String s){
        mPrefs.edit().putString(STOP_TRACKING_ALERT, s).apply();
    }

    @Override
    public String getStopTrackingAlertFlag(){
        return mPrefs.getString(STOP_TRACKING_ALERT, "0");
    }

    @Override
    public void setEdsActivityCodes(Set<String> all_edsactivity_codes){
        mPrefs.edit().putStringSet(EDS_ACTIVITY_CODES, all_edsactivity_codes).apply();
    }

    @Override
    public Set<String> getEdsActivityCodes(){
        return mPrefs.getStringSet(EDS_ACTIVITY_CODES, new HashSet<>());
    }

    @Override
    public void setDRSTimeStap(long time_stamp){
        mPrefs.edit().putLong(DRS_TIME_STAMP, time_stamp).apply();
    }

    @Override
    public long getDRSTimeStamp(){
        return mPrefs.getLong(DRS_TIME_STAMP, 0L);
    }

    @Override
    public void setRescheduleAttemptTimes(int rescheduleAttemptTimes){
        mPrefs.edit().putInt(RESCHEDULE_DATE, rescheduleAttemptTimes).apply();
    }

    @Override
    public int getRescheduleAttemptTimes(){
        return mPrefs.getInt(RESCHEDULE_DATE, 10);
    }

    @Override
    public void setDlightSuccessEncrptedOTP(String encrptedOTP){
        mPrefs.edit().putString(DLIGHT_ENCRPTED_SUCCESS_OTP, encrptedOTP).apply();
    }

    @Override
    public String getDlightSuccessEncrptedOTP(){
        return mPrefs.getString(DLIGHT_ENCRPTED_SUCCESS_OTP, "");
    }

    @Override
    public void setDlightSuccessEncrptedOTPType(String otpType){
        mPrefs.edit().putString(DLIGHT_ENCRPTED_SUCCESS_OTP_TYEP, otpType).apply();
    }

    @Override
    public String getDlightSuccessEncrptedOTPType(){
        return mPrefs.getString(DLIGHT_ENCRPTED_SUCCESS_OTP_TYEP, "");
    }

    @Override
    public void setPreviousTripStatus(Boolean previousTripStatus){
        mPrefs.edit().putBoolean(PREVIOUS_TRIP_STATUS, previousTripStatus).apply();
    }

    @Override
    public Boolean getPreviosTripStatus(){
        return mPrefs.getBoolean(PREVIOUS_TRIP_STATUS, false);
    }

    @Override
    public boolean isDCLocationUpdateAllowed(){
        return mPrefs.getBoolean(DC_LOCATION_UPDATE, false);
    }

    @Override
    public void SetisDCLocationUpdateAllowed(boolean status){
        mPrefs.edit().putBoolean(DC_LOCATION_UPDATE, status).apply();
    }

    @Override
    public void setTypeId(int pos){
        mPrefs.edit().putInt(TYPEID, pos).apply();
    }

    @Override
    public void setVehicleTypeId(int pos){
        mPrefs.edit().putInt(VEHICLETYPEID, pos).apply();
    }

    @Override
    public int getProgressiveTimer(){
        return mPrefs.getInt(ProgressiveTimer, 0);
    }

    @Override
    public void setProgressiveTimer(int timer){
        mPrefs.edit().putInt(ProgressiveTimer, timer).apply();
    }

    @Override
    public int getSameDayReassignTimer(){
        return mPrefs.getInt(ReassignTimer, 0);
    }

    @Override
    public void setSameDayReassignTimer(int count){
        mPrefs.edit().putInt(ReassignTimer, count).apply();
    }

    @Override
    public void setDownloadAPKIsInProcess(long b){
        mPrefs.edit().putLong(APK_DOWNLOAD_IS_IN_PROCESS, b).apply();
    }

    @Override
    public String getName(){
        return mPrefs.getString(PREF_KEY_NAME, null);
    }

    @Override
    public String getDesignation(){
        return mPrefs.getString(PREF_KEY_DESIGNATION, null);
    }

    @Override
    public String getMobile(){
        return mPrefs.getString(PREF_KEY_MOBILE, null);
    }

    @Override
    public String getVodaOrderNo(){
        return mPrefs.getString(PREF_KEY_VODA_ORDER, "");
    }

    @Override
    public Long getLocationType(){
        return mPrefs.getLong(PREF_KEY_LOCTAION_TYPE, 0);
    }

    @Override
    public String getLocationCode(){
        return mPrefs.getString(PREF_KEY_LOCATION_CODE, null);
    }

    @Override
    public String getCode(){
        return mPrefs.getString(PREF_KEY_CODE, null);
    }

    @Override
    public String getAuthPinCode(){
        return mPrefs.getString(PREF_KEY_USER_PINCODE, null);
    }

    @Override
    public boolean clearPrefrence(){
        setTripId("0");
        setCurrentUserLoggedInMode(LOGGED_IN_MODE_LOGGED_OUT);
        setAuthPinCode("");
        setAuthToken("");
        setCurrentUserLoggedInMode(LOGGED_IN_MODE_LOGGED_OUT);
        setDesignation("");
        setIsUserValided(false);
        setLocationCode("");
        setLocationType(0L);
        setMobile("");
        setVodaOrderNo("");
        setName("");
        setPhotoUrl("");
        setServiceCenter("");
        setAuthPinCode("");
        setStartTripMeterReading(0);
        setStopTripMeterReading(0);
        setForwardReasonCodeFlag("");
        setRTSReasonCodeFlag("");
        setRtsInputResendFlag("");
        setRVPReasonCodeFlag("");
        setEDSReasonCodeFlag("");
        setFEReachedDC(false);
        return mPrefs.edit().clear().commit();
    }

    @Override
    public boolean getIsUserValided(){
        return mPrefs.getBoolean(PREF_KEY_IS_USER_VALIDATED, false);
    }

    @Override
    public String getPhotoUrl(){
        return mPrefs.getString(PREF_KEY_PHOTO_URL, null);
    }

    @Override
    public void setForwardReasonCodeFlag(String flag){
        mPrefs.edit().putString(FORWARD_REASON_CODE_FLAG, flag).apply();
    }

    @Override
    public void setRTSReasonCodeFlag(String flag){
        mPrefs.edit().putString(RTS_REASON_CODE_FLAG, flag).apply();
    }

    @Override
    public void setRtsInputResendFlag(String flag){
        mPrefs.edit().putString(RTS_INPUT_OTP_RESEND_FLAG, flag).apply();
    }

    @Override
    public String getRtsInputResendFlag(){
        return mPrefs.getString(RTS_INPUT_OTP_RESEND_FLAG, "false");
    }

    @Override
    public void setRVPReasonCodeFlag(String flag){
        mPrefs.edit().putString(RVP_REASON_CODE_FLAG, flag).apply();
    }

    @Override
    public void setEDSReasonCodeFlag(String flag){
        mPrefs.edit().putString(EDS_REASON_CODE_FLAG, flag).apply();
    }

    @Override
    public void setVodaOrderNo(String orderNo){
        mPrefs.edit().putString(PREF_KEY_VODA_ORDER, orderNo).apply();
    }

    @Override
    public String getForwardReasonCodeFlag(){
        return mPrefs.getString(FORWARD_REASON_CODE_FLAG, "false");
    }

    @Override
    public String getRTSReasonCodeFlag(){
        return mPrefs.getString(RTS_REASON_CODE_FLAG, "false");
    }

    @Override
    public String getRVPReasonCodeFlag(){
        return mPrefs.getString(RVP_REASON_CODE_FLAG, "false");
    }

    @Override
    public String getEDSReasonCodeFlag(){
        return mPrefs.getString(EDS_REASON_CODE_FLAG, "false");
    }

    @Override
    public String getTripId(){
        return mPrefs.getString(PREF_KEY_TRIP_ID, "0");
    }

    @Override
    public String getVehicleType(){
        return mPrefs.getString(PREF_VEHICLETYPE, "0");
    }

    @Override
    public String getTypeOfVehicle(){
        return mPrefs.getString(PREF_TYPEOFVEHICLE, "0");
    }

    @Override
    public String getRouteName(){
        return mPrefs.getString(PREF_ROUTENAME, null);
    }

    @Override
    public void setCurrentUserLoggedInMode(DataManager.LoggedInMode mode){
        mPrefs.edit().putInt(PREF_KEY_USER_LOGGED_IN_MODE, mode.getType()).apply();
    }

    @Override
    public void setAuthToken(String authToken){
        mPrefs.edit().putString(PREF_KEY_AUTH_TOKEN, authToken).apply();
    }

    @Override
    public void setCurrentTimeForDelay(long time){
        mPrefs.edit().putLong(DELAY_TIME, time).apply();
    }

    @Override
    public long getCurrentTimeForDelay(){
        return mPrefs.getLong(DELAY_TIME, 0);
    }

    @Override
    public void setServiceCenter(String serviceCenter){
        mPrefs.edit().putString(PREF_KEY_SERVICE_CENTER, serviceCenter).apply();
    }

    @Override
    public void setName(String name){
        mPrefs.edit().putString(PREF_KEY_NAME, name).apply();
    }

    @Override
    public void setDesignation(String designation){
        mPrefs.edit().putString(PREF_KEY_DESIGNATION, designation).apply();
    }

    @Override
    public void setMobile(String mobile){
        mPrefs.edit().putString(PREF_KEY_MOBILE, mobile).apply();
    }

    @Override
    public void setLocationType(Long locationType){
        mPrefs.edit().putLong(PREF_KEY_LOCTAION_TYPE, locationType).apply();
    }

    @Override
    public void setLocationCode(String locationCode){
        mPrefs.edit().putString(PREF_KEY_LOCATION_CODE, locationCode).apply();
    }

    @Override
    public void setCode(String code){
        mPrefs.edit().putString(PREF_KEY_CODE, code).apply();
    }

    @Override
    public void setIsUserValided(Boolean isUserValided){
        mPrefs.edit().putBoolean(PREF_KEY_IS_USER_VALIDATED, isUserValided).apply();
    }

    @Override
    public void setPhotoUrl(String photoUrl){
        mPrefs.edit().putString(PREF_KEY_PHOTO_URL, photoUrl).apply();
    }

    @Override
    public void setAuthPinCode(String authPinCode){
        mPrefs.edit().putString(PREF_KEY_USER_PINCODE, authPinCode).apply();
    }

    @Override
    public void setTripId(String tripId){
        mPrefs.edit().putString(PREF_KEY_TRIP_ID, tripId).apply();
    }

    @Override
    public void setActualMeterReading(Long actualMeterReading){
        mPrefs.edit().putLong(PREF_KEY_Actual_Meter, actualMeterReading).apply();
    }

    @Override
    public void setVehicleType(String vehicleType){
        mPrefs.edit().putString(PREF_VEHICLETYPE, vehicleType).apply();
    }

    @Override
    public void setTypeOfVehicle(String typeOfVehicle){
        mPrefs.edit().putString(PREF_TYPEOFVEHICLE, typeOfVehicle).apply();
    }

    @Override
    public void setRouteName(String routeName){
        mPrefs.edit().putString(PREF_ROUTENAME, routeName).apply();
    }

    @Override
    public void setStartTripMeterReading(long reading){
        mPrefs.edit().putLong(METER_READING_START_TRIP, reading).apply();
    }

    @Override
    public void setStopTripMeterReading(long reading){
        mPrefs.edit().putLong(METER_READING_STOP_TRIP, reading).apply();
    }

    @Override
    public void setLiveTrackingTripIdForApi(String live_tracking_trip_id){
        mPrefs.edit().putString(LIVE_TRACKING_ID_FOR_API, live_tracking_trip_id).apply();
    }

    @Override
    public String getLiveTrackingTripIdForApi(){
        return mPrefs.getString(LIVE_TRACKING_ID_FOR_API, "");
    }

    @Override
    public long getStartTripMeterReading(){
        return mPrefs.getLong(METER_READING_START_TRIP, 0);
    }

    @Override
    public long getStopTripMeterReading(){
        return mPrefs.getLong(METER_READING_STOP_TRIP, 0);
    }

    @Override
    public long getActualMeterReading(){
        return mPrefs.getLong(PREF_KEY_Actual_Meter, 0);
    }

    @Override
    public void setLatLngLimit(String configValue){
        mPrefs.edit().putString(LAT_LNG_LIMIT, configValue).apply();
    }

    @Override
    public String getLatLngLimit(){
        return mPrefs.getString(LAT_LNG_LIMIT, "5");
    }

    @Override
    public void setLoginMonth(int mMonth){
        mPrefs.edit().putInt(LOGIN_MONTH, mMonth).apply();
    }

    @Override
    public int getLoginMonth(){
        return mPrefs.getInt(LOGIN_MONTH, 0);
    }

    @Override
    public void setAadharFrontImageName(String aadhar_front_image){
        mPrefs.edit().putString(AADHAR_FRONT_IMAGE_NAME, aadhar_front_image).apply();
    }

    @Override
    public void setAadharRearImageName(String aadhar_rear_image){
        mPrefs.edit().putString(AADHAR_REAR_IMAGE_NAME, aadhar_rear_image).apply();
    }

    @Override
    public void setUndeliverCount(int configValue){
        mPrefs.edit().putInt(UNDELIVER_COUNT, configValue).apply();
    }

    @Override
    public int getUndeliverCount(){
        return mPrefs.getInt(UNDELIVER_COUNT, 0);
    }

    @Override
    public void setShipperId(int shipper_id){
        mPrefs.edit().putInt(SHIPPER_ID, shipper_id).apply();
    }

    @Override
    public int getShipperId(){
        return mPrefs.getInt(SHIPPER_ID, 0);
    }

    @Override
    public void setDirectionDistance(double distance){
        mPrefs.edit().putString(Direction_Distacne, String.valueOf(distance)).apply();
    }

    @Override
    public void setDirectionTotalDistance(double distance){
        mPrefs.edit().putString(Direction_TotalDistance, String.valueOf(distance)).apply();
    }

    @Override
    public double getDirectionDistance(){
        return Double.parseDouble(mPrefs.getString(Direction_Distacne, "0.0"));
    }

    @Override
    public double getDirectionTotalDistance(){
        return Double.parseDouble(mPrefs.getString(Direction_TotalDistance, "0.0"));
    }

    @Override
    public void setUndeliverConsigneeRANGE(int parseInt){
        mPrefs.edit().putInt(UNDELIVER_CONSIGNEE_RANGE, parseInt).apply();
    }

    @Override
    public int getUndeliverConsigneeRANGE(){
        return mPrefs.getInt(UNDELIVER_CONSIGNEE_RANGE, 0);
    }

    @Override
    public void setIsSignatureImageMandatory(String configValue){
        mPrefs.edit().putString(IS_SIGNATURE_IMAGE_MANDATORY, configValue).apply();
    }

    @Override
    public String getIsSignatureImageMandatory(){
        return mPrefs.getString(IS_SIGNATURE_IMAGE_MANDATORY, "false");
    }

    @Override
    public void setIsCallBridgeCheckStatus(String configValue){
        mPrefs.edit().putString(IS_CALL_BRIDGE_CHECK_STATUS, configValue).apply();
    }

    @Override
    public String getIsCallBridgeCheckStatus(){
        return mPrefs.getString(IS_CALL_BRIDGE_CHECK_STATUS, "false");
    }

    @Override
    public void setEDISPUTE(String configValue){
        mPrefs.edit().putString(IS_EDISPUTE_IMAGE_MANDATORY, configValue).apply();
    }

    @Override
    public String getEDispute(){
        return mPrefs.getString(IS_EDISPUTE_IMAGE_MANDATORY, "false");
    }

    @Override
    public void setCovidConset(boolean configValue){
        mPrefs.edit().putBoolean(is_covid_consent_captured, configValue).apply();
    }

    @Override
    public boolean getCovidConset(){
        return mPrefs.getBoolean(is_covid_consent_captured, false);
    }

    @Override
    public void setCovidUrl(String mcovid_image_url){
        mPrefs.edit().putString(covid_image_url, mcovid_image_url).apply();
    }

    @Override
    public String getCovidUrl(){
        return mPrefs.getString(covid_image_url, "");
    }

    @Override
    public void setEcomRegion(String ecom_region){
        mPrefs.edit().putString(ecom_dlv_region, ecom_region).apply();
    }

    @Override
    public String getEcomRegion(){
        return mPrefs.getString(ecom_dlv_region, "");
    }

    @Override
    public void setMessageClicked(String awb, boolean val){
        mPrefs.edit().putBoolean("Set_Message_" + awb, val).apply();
    }

    @Override
    public void setCardClicked(String awb, boolean val){
        mPrefs.edit().putBoolean("Set_Card_" + awb, val).apply();
    }

    @Override
    public boolean getMessageClicked(String awb){
        return mPrefs.getBoolean("Set_Message_" + awb, false);
    }

    @Override
    public boolean getCardClicked(String awb){
        return mPrefs.getBoolean("Set_Card_" + awb, false);
    }

    @Override
    public void setMessageCount(String awb, int ecode_status_clicked_times_message_link){
        mPrefs.edit().putInt("Message_Count_" + awb, ecode_status_clicked_times_message_link).apply();
    }

    @Override
    public int getMessageCount(String awb){
        return mPrefs.getInt("Message_Count_" + awb, 0);
    }

    @Override
    public int getCardCount(String awb){
        return mPrefs.getInt("Card_Count" + awb, 0);
    }

    @Override
    public void setCardCount(String awb, int i){
        mPrefs.edit().putInt("Card_Count" + awb, i).apply();
    }

    @Override
    public void setMaxEDSFailAttempt(int configValue){
        mPrefs.edit().putInt(MAX_EDS_EKYC_FAIL_ATTEMPT, configValue).apply();
    }

    @Override
    public int getMaxEDSFailAttempt(){
        return mPrefs.getInt(MAX_EDS_EKYC_FAIL_ATTEMPT, 4);
    }

    @Override
    public void setLocationCount(int count){
        mPrefs.edit().putInt(LOCATION_COUNT, count).apply();
    }

    @Override
    public int getLocationCount(){
        return mPrefs.getInt(LOCATION_COUNT, 0);
    }

    @Override
    public void setLiveTrackingSpeed(String configValue){
        mPrefs.edit().putString(LIVE_TRACKING_DEVICE_MOVEMENT_MIN_SPEED, configValue).apply();
    }

    @Override
    public double getLiveTrackingSpeed(){
        return Double.parseDouble(mPrefs.getString(LIVE_TRACKING_DEVICE_MOVEMENT_MIN_SPEED, "0.0"));
    }

    @Override
    public void setLiveTrackingAccuracy(String configValue){
        mPrefs.edit().putString(LIVE_TRACKING_LAT_LNG_ACCURACY, configValue).apply();
    }

    @Override
    public int getLiveTrackingAccuracy(){
        return Integer.parseInt(mPrefs.getString(LIVE_TRACKING_LAT_LNG_ACCURACY, "50"));
    }

    @Override
    public void setLiveTrackingDisplacement(String configValue){
        mPrefs.edit().putString(LIVE_TRACKING_LAT_LNG_MIN_DISPLACEMENT, configValue).apply();
    }

    @Override
    public float getLiveTrackingDisplacement(){
        return Float.parseFloat(mPrefs.getString(LIVE_TRACKING_LAT_LNG_MIN_DISPLACEMENT, "250"));
    }

    @Override
    public void setLiveTrackingInterval(String configValue){
        mPrefs.edit().putString(LIVE_TRACKING_LAT_LNG_TIME_INTERVAL, configValue).apply();
    }

    @Override
    public long getLiveTrackingInterval(){
        return Long.parseLong(mPrefs.getString(LIVE_TRACKING_LAT_LNG_TIME_INTERVAL, "18000"));
    }

    @Override
    public void setLiveTrackingMINSpeed(String configValue){
        mPrefs.edit().putString(LIVE_TRACKING_DEVICE_MOVEMENT_SHORTEST_SPEED, configValue).apply();
    }

    @Override
    public double getLiveTrackingMINSpeed(){
        return Double.parseDouble(mPrefs.getString(LIVE_TRACKING_DEVICE_MOVEMENT_SHORTEST_SPEED, "15"));
    }

    @Override
    public void setIsAdmEmp(boolean flag){
        mPrefs.edit().putBoolean(ADM_EMP_FLAG, flag).apply();
    }

    @Override
    public boolean getIsAdmEmp(){
        return mPrefs.getBoolean(ADM_EMP_FLAG, false);
    }

    @Override
    public void setFWDPrice(float price){
        mPrefs.edit().putFloat(FWD_PRICE, price).apply();
    }

    @Override
    public float getFWDPrice(){
        return mPrefs.getFloat(FWD_PRICE, 0);
    }

    @Override
    public void setPPDPrice(float price){
        mPrefs.edit().putFloat(PPD_PRICE, price).apply();
    }

    @Override
    public float getPPDPrice(){
        return mPrefs.getFloat(PPD_PRICE, 0);
    }

    @Override
    public void setCODPrice(float price){
        mPrefs.edit().putFloat(COD_PRICE, price).apply();
    }

    @Override
    public float getCODPrice(){
        return mPrefs.getFloat(COD_PRICE, 0);
    }

    @Override
    public void setRQCPrice(float price){
        mPrefs.edit().putFloat(RQC_PRICE, price).apply();
    }

    @Override
    public float getRQCPrice(){
        return mPrefs.getFloat(RQC_PRICE, 0);
    }

    @Override
    public void setEDSPrice(float price){
        mPrefs.edit().putFloat(EDS_PRICE, price).apply();
    }

    @Override
    public float getEDSPrice(){
        return mPrefs.getFloat(EDS_PRICE, 0);
    }

    @Override
    public void setRVPPrice(float price){
        mPrefs.edit().putFloat(RVP_PRICE, price).apply();
    }

    @Override
    public float getRVPPrice(){
        return mPrefs.getFloat(RVP_PRICE, 0);
    }

    @Override
    public void setRTSPrice(float price){
        mPrefs.edit().putFloat(RTS_PRICE, price).apply();
    }

    @Override
    public float getRTSPrice(){
        return mPrefs.getFloat(RTS_PRICE, 0);
    }

    @Override
    public void setCurrentTripAmount(String amount){
        mPrefs.edit().putString(CURRENT_TRIP_AMOUNT, amount).apply();
    }

    @Override
    public String getCurrentTripAmount(){
        return mPrefs.getString(CURRENT_TRIP_AMOUNT, "0.0");
    }

    @Override
    public void setDRSId(long drs_id){
        mPrefs.edit().putLong(DRS_ID, drs_id).apply();
    }

    @Override
    public long getDRSId(){
        return mPrefs.getLong(DRS_ID, 0L);
    }

    @Override
    public void setIsQRCodeScanned(boolean b){
        mPrefs.edit().putBoolean(IS_QRCODE_SCANNED, b).apply();
    }

    @Override
    public boolean getIsQRCodeScanned(){
        return mPrefs.getBoolean(IS_QRCODE_SCANNED, false);
    }

    @Override
    public String getCallStatusApiInterval(){
        return mPrefs.getString(CALL_STATUS_API_INTERVAL, "10");
    }

    @Override
    public void setCallStatusApiInterval(String interval){
        mPrefs.edit().putString(CALL_STATUS_API_INTERVAL, interval).apply();
    }

    @Override
    public boolean getDirectUndeliver(){
        return mPrefs.getBoolean(DIRECT_DELIVER, false);
    }

    @Override
    public void setDirectUndeliver(boolean b){
        mPrefs.edit().putBoolean(DIRECT_DELIVER, b).apply();
    }

    @Override
    public boolean isCallStatusAPIRecursion(){
        return mPrefs.getBoolean(CALL_STATUS_API_RECURSION, false);
    }

    @Override
    public void setCallAPIRecursion(boolean b){
        mPrefs.edit().putBoolean(CALL_STATUS_API_RECURSION, b).apply();
    }

    @Override
    public long getRequestRsponseTime(){
        return mPrefs.getLong(REQUEST_RESPONSE_TIME_INTERVAL, 2000);
    }

    @Override
    public void setRequestResponseTime(long time){
        mPrefs.edit().putLong(REQUEST_RESPONSE_TIME_INTERVAL, time).apply();
    }

    @Override
    public void setEnableDPEmployee(boolean parseLong){
        mPrefs.edit().putBoolean(ENABLE_DP_EMPLOYEE, parseLong).apply();
    }

    @Override
    public boolean getEnableDPEmployee(){
        return mPrefs.getBoolean(ENABLE_DP_EMPLOYEE, false);
    }

    @Override
    public void setCounterDelivery(boolean b){
        mPrefs.edit().putBoolean(COUNTER_DELIVERY, b).apply();
    }

    @Override
    public boolean isCounterDelivery(){
        return mPrefs.getBoolean(COUNTER_DELIVERY, false);
    }

    @Override
    public void setCodStatusInterval(long interval){
        mPrefs.edit().putLong(COD_STATUS_INTERVAL, interval).apply();
    }

    @Override
    public long getCodStatusInterval(){
        return mPrefs.getLong(COD_STATUS_INTERVAL, 0L);
    }

    @Override
    public void setCodStatusIntervalStatusFraction(int fraction){
        mPrefs.edit().putInt(COD_STATUS_INTERVAL_FRACTION, fraction).apply();
    }

    @Override
    public int getCodeStatusIntervalFraction(){
        return mPrefs.getInt(COD_STATUS_INTERVAL_FRACTION, 0);
    }

    @Override
    public void setRescheduleMaxAttempts(int parseInt){
        mPrefs.edit().putInt(RESCHEDULE_MAX_ATTEMPTS, parseInt).apply();
    }

    @Override
    public int getRescheduleMaxAttempts(){
        return mPrefs.getInt(RESCHEDULE_MAX_ATTEMPTS, 0);
    }

    @Override
    public void setRescheduleMaxDaysAllow(int parseInt){
        mPrefs.edit().putInt(RESCHEDULE_MAX_ALLOWED_DAYS, parseInt).apply();
    }

    @Override
    public int getRescheduleMaxDaysAllow(){
        return mPrefs.getInt(RESCHEDULE_MAX_ALLOWED_DAYS, 0);
    }

    @Override
    public boolean isUseCamscannerPrintReceipt(){
        return mPrefs.getBoolean(IS_USE_CAMSCANNER_PRINT, false);
    }

    @Override
    public void setIsUseCamscannerPrintReceipt(boolean val){
        mPrefs.edit().putBoolean(IS_USE_CAMSCANNER_PRINT, val).apply();
    }

    @Override
    public boolean isUseCamscannerDispute(){
        return mPrefs.getBoolean(IS_USE_CAMSCANNER_DISPUTE, false);
    }

    @Override
    public void setIsUseCamscannerDispute(boolean val){
        mPrefs.edit().putBoolean(IS_USE_CAMSCANNER_DISPUTE, val).apply();
    }

    @Override
    public boolean isUseCamscannerTrip(){
        return mPrefs.getBoolean(IS_USE_CAMSCANNER_TRIP, false);
    }

    @Override
    public void setIsUseCamscannerTrip(boolean val){
        mPrefs.edit().putBoolean(IS_USE_CAMSCANNER_TRIP, val).apply();
    }

    @Override
    public void setSathiLogApiCallInterval(long parseLong){
        mPrefs.edit().putLong(SATHI_LOG_API_CALL_INTERVAL, parseLong).apply();
    }

    @Override
    public long getSathiLogApiCallInterval(){
        return mPrefs.getLong(SATHI_LOG_API_CALL_INTERVAL, 60 * 1000);
    }

    @Override
    public void setDistance(int parseInt){
        mPrefs.edit().putInt(GAP_DISTANCE, parseInt).apply();
    }

    @Override
    public int getDistance(){
        return mPrefs.getInt(GAP_DISTANCE, 5000);
    }

    @Override
    public void setLiveTrackingCalculatedDistance(float distance){
        mPrefs.edit().putFloat(LIVE_TRACKING_CALCULATION_DISTANCE, distance).apply();
    }

    @Override
    public float getLiveTrackingCalculatedDistance(){
        return mPrefs.getFloat(LIVE_TRACKING_CALCULATION_DISTANCE, 0);
    }

    @Override
    public void setLiveTrackingCalculatedDistanceWithSpeed(float distance){
        mPrefs.edit().putFloat(TOTAL_DISTANCE_WITH_SPEED, distance).apply();
    }

    @Override
    public float getLiveTrackingCalculatedDistanceWithSpeed(){
        return mPrefs.getFloat(TOTAL_DISTANCE_WITH_SPEED, 0f);
    }

    @Override
    public void isRecentRemoved(boolean b){
        mPrefs.edit().putBoolean(IS_RECENT_REMOVED, b).apply();
    }

    @Override
    public boolean getIsRecentRemoved(){
        return mPrefs.getBoolean(IS_RECENT_REMOVED, false);
    }

    @Override
    public void setLoginPermission(boolean b){
        mPrefs.edit().putBoolean(Login_PERMISSION, b).apply();
    }

    @Override
    public boolean getLoginPermission(){
        return mPrefs.getBoolean(Login_PERMISSION, false);
    }

    @Override
    public void setPaymentType(String paymentType){
        mPrefs.edit().putString(PAYMENT_TYPE, paymentType).apply();
    }

    @Override
    public String getPaymentType(){
        return mPrefs.getString(PAYMENT_TYPE, "");
    }

    @Override
    public void setOfflineFwd(boolean parseBoolean){
        mPrefs.edit().putBoolean(OFFLINE_FWD, parseBoolean).apply();
    }

    @Override
    public boolean getofflineFwd(){
        return mPrefs.getBoolean(OFFLINE_FWD, false);
    }

    @Override
    public void setLiveTrackingCount(int size){
        mPrefs.edit().putInt(LIVE_TRACKING_COUNT, size).apply();
    }

    @Override
    public int getLiveTrackingCount(){
        return mPrefs.getInt(LIVE_TRACKING_COUNT, 0);
    }

    @Override
    public void setADMUpdated(boolean b){
        mPrefs.edit().putBoolean(ADM_UPDATED, b).apply();
    }

    @Override
    public boolean isADMUpdated(){
        return mPrefs.getBoolean(ADM_UPDATED, false);
    }

    @Override
    public void setAdharPositiveButton(String configValue){
        mPrefs.edit().putString(CONSENT_AGREE, configValue).apply();
    }

    @Override
    public void setAdharNegativeButton(String configValue){
        mPrefs.edit().putString(CONSENT_DISAGREE, configValue).apply();
    }

    @Override
    public String getAdharPositiveButton(){
        return mPrefs.getString(CONSENT_AGREE, "YES");
    }

    @Override
    public String getAdharNegativeButton(){
        return mPrefs.getString(CONSENT_DISAGREE, "NO");
    }

    @Override
    public void setDisableResendOtpButtonDuration(long configValue){
        mPrefs.edit().putLong(DISABLE_RESEND_OTP_BUTTON_DURATION, configValue).apply();
    }

    @Override
    public long getDisableResendOtpButtonDuration(){
        return mPrefs.getLong(DISABLE_RESEND_OTP_BUTTON_DURATION, 60);
    }

    @Override
    public void setStartTripTime(long currentTimeMillis){
        mPrefs.edit().putLong(START_TRIP_TIME, currentTimeMillis).apply();
    }

    @Override
    public long getStartTripTime(){
        return mPrefs.getLong(START_TRIP_TIME, 0L);
    }

    @Override
    public void setSycningBlokingStatus(boolean b){
        mPrefs.edit().putBoolean(SYNC_BLOCK_STATUS, b).apply();
    }

    @Override
    public boolean getSynckingBlockingStatus(){
        return mPrefs.getBoolean(SYNC_BLOCK_STATUS, true);
    }

    @Override
    public void setFilterCount(int filterCount){
        mPrefs.edit().putInt(FILTER_COUNT, filterCount).apply();
    }

    @Override
    public int getFilterCount(){
        return mPrefs.getInt(FILTER_COUNT, 0);
    }

    @Override
    public void setESPSchemeTerms(String terms){
        mPrefs.edit().putString(ESP_REFER_SCHEME_TERM_AND_CONDITIONS, terms).apply();
    }

    @Override
    public void setESPReferCode(String refer_code){
        mPrefs.edit().putString(ESP_REFERRAL_CODE, refer_code).apply();
    }

    @Override
    public String getESPSchemeTerms(){
        return mPrefs.getString(ESP_REFER_SCHEME_TERM_AND_CONDITIONS, "");
    }

    @Override
    public String getESPReferCode(){
        return mPrefs.getString(ESP_REFERRAL_CODE, "");
    }

    @Override
    public void setLocationAccuracy(float accuracy){
        mPrefs.edit().putFloat(LOCATION_ACCURACY, accuracy).apply();
    }

    @Override
    public float getLocationAccuracy(){
        return mPrefs.getFloat(LOCATION_ACCURACY, 0.0f);
    }

    @Override
    public void setPSTNType(String cb_calling_type){
        mPrefs.edit().putString(PSTN_TYPE, cb_calling_type).apply();
    }

    @Override
    public String getPSTNType(){
        return mPrefs.getString(PSTN_TYPE, "");
    }

    @Override
    public void setIsMasterDataAvailable(boolean b){
        mPrefs.edit().putBoolean(IS_MASTER_AVAILABLE , b).apply();
    }

    @Override
    public boolean isMasterDataAvailable(){
        return mPrefs.getBoolean(IS_MASTER_AVAILABLE ,false);
    }

    @Override
    public void setFakeApplicatons(String configValue){
        mPrefs.edit().putString(FAKE_APPLICATIONS , configValue).apply();
    }

    @Override
    public String getFakeApplications(){
        return mPrefs.getString(FAKE_APPLICATIONS , "com.location.test|com.lexa.fakegps|com.incorporateapps.fakegps.fre|com.blogspot.newapphorizons.fakegps|com.rosteam.gpsemulator|com.blogspot.newapphorizons.fakegps");
    }

    @Override
    public void setIsCallAlreadyDone(Boolean aBoolean){
        mPrefs.edit().putBoolean(IS_CALL_DONE ,aBoolean).apply();
    }

    @Override
    public boolean getIsCallAlreadyDone(){
        return mPrefs.getBoolean(IS_CALL_DONE , false);
    }

    @Override
    public void setKiranaUser(String configValue){
        mPrefs.edit().putString(IS_KIRANA_USER, configValue).apply();

    }

    @Override
    public String getKiranaUser(){
        return mPrefs.getString(IS_KIRANA_USER, "false");
    }

    @Override
    public void setVCallpopup(boolean parseBoolean){
        mPrefs.edit().putBoolean(V_CALL_POPUP ,parseBoolean).apply();

    }

    @Override
    public boolean getVCallPopup(){
        return mPrefs.getBoolean(V_CALL_POPUP, false);
    }

    @Override
    public void setForwardCallCount(String awb, int forward_call_count){
        mPrefs.edit().putInt(String.valueOf(awb) ,forward_call_count).apply();
    }

    @Override
    public void setRVPCallCount(String awb, int rvp_call_count){
        mPrefs.edit().putInt(String.valueOf(awb) ,rvp_call_count).apply();
    }

    @Override
    public void setEDSCallCount(String awb, int eds_call_count){
        mPrefs.edit().putInt(String.valueOf(awb) ,eds_call_count).apply();
    }

    @Override
    public void setRTSCallCount(String awb, int rts_call_count){
        mPrefs.edit().putInt(String.valueOf(awb) ,rts_call_count).apply();
    }

    @Override
    public boolean getCallClicked(String awb) {
        return mPrefs.getBoolean(awb, true);
    }

    @Override
    public void setCallClicked(String awb, boolean isCallClicked) {
        mPrefs.edit().putBoolean(awb, isCallClicked).apply();
    }

    @Override
    public int getForwardCallCount(String awb){
        return mPrefs.getInt(String.valueOf(awb), 0);
    }

    @Override
    public int getRVPCallCount(String awb){
        return mPrefs.getInt(String.valueOf(awb), 0);
    }

    @Override
    public int getEDSCallCount(String awb){
        return mPrefs.getInt(String.valueOf(awb), 0);
    }

    @Override
    public int getRTSCallCount(String awb){
        return mPrefs.getInt(String.valueOf(awb), 0);
    }

    @Override
    public void setForwardMapCount(long awb, int forward_map_count){
        mPrefs.edit().putInt(String.valueOf(awb) ,forward_map_count).apply();
    }

    @Override
    public void setRTSMapCount(long awb, int rts_map_count){
        mPrefs.edit().putInt(String.valueOf(awb) ,rts_map_count).apply();
    }

    @Override
    public void setRVPMapCount(long awb, int rvp_map_count){
        mPrefs.edit().putInt(String.valueOf(awb) ,rvp_map_count).apply();
    }

    @Override
    public void setEDSMapCount(long awb, int eds_map_count){
        mPrefs.edit().putInt(String.valueOf(awb) ,eds_map_count).apply();
    }

    @Override
    public int getForwardMapCount(long awb){
        return mPrefs.getInt(String.valueOf(awb) , 0);
    }

    @Override
    public int getRVPMapCount(long awb){
        return mPrefs.getInt(String.valueOf(awb) , 0);
    }

    @Override
    public int getEDSMapCount(long awb){
        return mPrefs.getInt(String.valueOf(awb) , 0);
    }

    @Override
    public int getRTSMapCount(long awb){
        return mPrefs.getInt(String.valueOf(awb) , 0);
    }

    @Override
    public void setStartQCLat(double currentLatitude){
        mPrefs.edit().putString(START_QC_LAT , String.valueOf(currentLatitude)).apply();
    }

    @Override
    public void setStartQCLng(double currentLongitude){
        mPrefs.edit().putString(START_QC_LNG , String.valueOf(currentLongitude)).apply();
    }

    @Override
    public String getStartQCLat(){
        return mPrefs.getString(START_QC_LAT , "");
    }

    @Override
    public String getStartQCLng(){
        return mPrefs.getString(START_QC_LNG , "");
    }

    @Override
    public void setOFDOTPVerifiedStatus(String s){
        mPrefs.edit().putString(OFD_VERIFIED_STATUS , s).apply();
    }

    @Override
    public String getOFDOTPVerifiedStatus(){
        return mPrefs.getString(OFD_VERIFIED_STATUS, "NONE");
    }

    @Override
    public void setSKIPOTPREVRQC(String configValue){
        mPrefs.edit().putString(SKIP_OTP_REV_RQC ,configValue).apply();
    }

    @Override
    public String getSKIPOTPREVRQC(){
        return mPrefs.getString(SKIP_OTP_REV_RQC, "False");
    }

    @Override
    public void setSKIP_CANC_OTP_RVP(String configValue) {
        mPrefs.edit().putString(SKIP_CANC_OTP_RVP ,configValue).apply();
    }

    @Override
    public String getSKIP_CANC_OTP_RVP() {
        return mPrefs.getString(SKIP_CANC_OTP_RVP, "False");
    }

    @Override
    public void setRVPSecureOTPVerified(String s){
        mPrefs.edit().putString(RVP_SECURE_IS_OTP_VERIFIED, s).apply();
    }

    @Override
    public String getRVPSecureOTPVerified(){
        return mPrefs.getString(RVP_SECURE_IS_OTP_VERIFIED, "false");
    }

    @Override
    public void setUndeliverReasonCode(String select_reason_code_rts){
        mPrefs.edit().putString(RTS_UNDELIVER_REASON_CODE, select_reason_code_rts).apply();
    }

    @Override
    public String getUndeliverReasonCode(){
        return mPrefs.getString(RTS_UNDELIVER_REASON_CODE, "");
    }

    @Override
    public void setRVPRQCBarcodeScan(String configValue){
        mPrefs.edit().putString(RVP_RQC_BARCODE_SCAN, configValue).apply();
    }

    @Override
    public String getRVPRQCBarcodeScan(){
        return mPrefs.getString(RVP_RQC_BARCODE_SCAN, "false");
    }

    @Override
    public void setSMSThroughWhatsapp(boolean parseBoolean){
        mPrefs.edit().putBoolean(SMS_THROUGH_WHATSAPP, parseBoolean).apply();
    }

    @Override
    public void setTechparkWhatsapp(String value){
        mPrefs.edit().putString(TECHPAK_WHATSAPP, value).apply();
    }

    @Override
    public void setTriedReachyouWhatsapp(String value){
        mPrefs.edit().putString(TRIEDREACHYOU_WHATSAPP, value).apply();
    }

    @Override
    public boolean getSMSThroughWhatsapp(){
        return mPrefs.getBoolean(SMS_THROUGH_WHATSAPP, false);
    }

    @Override
    public String getTechparkWhatsapp(){
        return mPrefs.getString(TECHPAK_WHATSAPP, "");
    }

    @Override
    public String getTriedReachyouWhatsapp(){
        return mPrefs.getString(TRIEDREACHYOU_WHATSAPP, "");
    }

    @Override
    public void setTryReachingCount(String awb ,int count){
        mPrefs.edit().putInt(awb, count).apply();
    }

    @Override
    public int getTryReachingCount(String awb){
        return mPrefs.getInt(awb, 0);
    }

    @Override
    public void setSendSmsCount(String awb, int count){
        mPrefs.edit().putInt(awb, count).apply();
    }

    @Override
    public int getSendSmsCount(String awb){
        return mPrefs.getInt(awb, 0);
    }

    @Override
    public void setImageUri(Uri selectedPhotoPath){
        mPrefs.edit().putString(IAMGE_URI ,selectedPhotoPath.toString()).apply();
    }

    @Override
    public String getImageUri(){
        return mPrefs.getString(IAMGE_URI, "");
    }

    @Override
    public void setStartTrip(boolean b){
        mPrefs.edit().putBoolean(IS_START_TRIP ,b).apply();
    }

    @Override
    public boolean getStatTrip(){
        return mPrefs.getBoolean(IS_START_TRIP ,true);
    }

    @Override
    public boolean getDPUserBarcodeFlag(){
        return mPrefs.getBoolean(ESP_SCANNER, false);
    }

    @Override
    public void setDPUserBarcodeFlag(boolean dpUserFlag){
        mPrefs.edit().putBoolean(ESP_SCANNER, dpUserFlag).apply();
    }

    @Override
    public void setMultiSpaceApps(String configValue){
        mPrefs.edit().putString(MULTISPACE_APPS, configValue).apply();
    }

    @Override
    public String getMultiSpaceApps(){
        return mPrefs.getString(MULTISPACE_APPS, "");
    }

    @Override
    public void setFeedbackMessage(String feedbackMessage) {
        mPrefs.edit().putString(FEEDBACK_MESSAGE, feedbackMessage).apply();
    }

    @Override
    public String getFeedbackMessage() {
        return mPrefs.getString(FEEDBACK_MESSAGE, "");
    }

    // Blur Image Work:-
    @Override
    public void setBlurImageType(String blurImageType){
        mPrefs.edit().putString(BLUR_IMAGE_TYPE, blurImageType).apply();
    }

    @Override
    public String getBlurImageType(){
        return mPrefs.getString(BLUR_IMAGE_TYPE, "");
    }

    @Override
    public void setSathiAttendanceFeatureEnable(Boolean sathiAttendanceFeatureEnable) {
        mPrefs.edit().putBoolean(SATHI_ATTENDANCE_FEATURE_ENABLE, sathiAttendanceFeatureEnable).apply();
    }

    @Override
    public Boolean getSathiAttendanceFeatureEnable() {
        return mPrefs.getBoolean(SATHI_ATTENDANCE_FEATURE_ENABLE, false);
    }

    @Override
    public void setBPMismatch(Boolean bpMismatch) {
        mPrefs.edit().putBoolean(BP_MISMATCH, bpMismatch).apply();
    }

    @Override
    public Boolean getBPMismatch() {
        return mPrefs.getBoolean(BP_MISMATCH, false);
    }

    @Override
    public void setUDBPCode(String udbpCode) {
        mPrefs.edit().putString(UD_BP, udbpCode).apply();
    }

    @Override
    public String getUDBPCode() {
        return mPrefs.getString(UD_BP, "0");
    }

    @Override
    public void setOBDREFUSED(String obdrefused) {
        mPrefs.edit().putString(OBD_REFUSED, obdrefused).apply();
    }

    @Override
    public String getOBDREFUSED() {
        return mPrefs.getString(OBD_REFUSED, "0");
    }

    @Override
    public void setOBDQCFAIL(String obdQcFail) {
        mPrefs.edit().putString(OBD_QC_FAIL, obdQcFail).apply();
    }

    @Override
    public String getOBDQCFAIL() {
        return mPrefs.getString(OBD_QC_FAIL, "0");
    }

    @Override
    public void setHideCamera(Boolean hideCamera) {
        mPrefs.edit().putBoolean(HIDE_CAMERA, hideCamera).apply();
    }

    @Override
    public Boolean getHideCamera() {
        return mPrefs.getBoolean(HIDE_CAMERA, false);
    }

    @Override
    public void setFWD_UD_RD_OTPVerfied(String awb, boolean b){
        mPrefs.edit().putBoolean(awb, b).apply();
    }

    @Override
    public boolean getFWD_UD_RD_OTPVerfied(String awb){
        return mPrefs.getBoolean(awb, false);
    }

    @Override
    public void setFWDRessign(boolean parseBoolean){
        mPrefs.edit().putBoolean(FORWARD_REASSIGN, parseBoolean).apply();
    }

    @Override
    public boolean getFWDRessign(){
        return mPrefs.getBoolean(FORWARD_REASSIGN, true);
    }

    @Override
    public void setAddressQualityScore(String configValue) {
        mPrefs.edit().putString(address_quality_score, configValue).apply();
    }

    @Override
    public String getAddressQualityScore() {
        return mPrefs.getString(address_quality_score, "0");
    }

    @Override
    public void setLoginServerTimeStamp(String timeStamp) {
        mPrefs.edit().putString(LOGIN_SERVER_TIMESTAMP, timeStamp).apply();
    }

    @Override
    public String getLoginServerTimeStamp() {
        return mPrefs.getString(LOGIN_SERVER_TIMESTAMP, "");
    }



    @Override
    public void setImageManualFlyer(Boolean aBoolean) {
        mPrefs.edit().putBoolean(IMAGE_MANUAL_FLYER, aBoolean).apply();
    }

    @Override
    public boolean getImageManulaFlyer() {
        return mPrefs.getBoolean(IMAGE_MANUAL_FLYER, false);
    }

    @Override
    public void setMasterDataSync(boolean masterDataSync) {
        mPrefs.edit().putBoolean(MASTER_DATA_SYNC, masterDataSync).apply();
    }

    @Override
    public boolean getMasterDataSync() {
        return mPrefs.getBoolean(MASTER_DATA_SYNC, false);
    }

    @Override
    public void setESP_EARNING_VISIBILITY(boolean espEarningVisibility) {
        mPrefs.edit().putBoolean(ESP_EARNING_VISIBILITY, espEarningVisibility).apply();
    }

    @Override
    public boolean getESP_EARNING_VISIBILITY() {
        return mPrefs.getBoolean(ESP_EARNING_VISIBILITY, false);
    }

    @Override
    public void setODH_VISIBILITY(boolean odhVisibility) {
        mPrefs.edit().putBoolean(ODH_VISIBILITY, odhVisibility).apply();
    }

    @Override
    public boolean getODH_VISIBILITY() {
        return mPrefs.getBoolean(ODH_VISIBILITY, false);
    }

    @Override
    public void setCheckAttendanceLoginStatus(boolean status) {
        mPrefs.edit().putBoolean(CHECK_ATTENDANCE_LOGIN_STATUS, status).apply();
    }

    @Override
    public boolean getCheckAttendanceLoginStatus() {
        return mPrefs.getBoolean(CHECK_ATTENDANCE_LOGIN_STATUS, false);
    }

    @Override
    public void setScreenStatus(Boolean status) {
        mPrefs.edit().putBoolean(SCREEN_STATUS, status).apply();
    }

    @Override
    public boolean getScreenStatus() {
        return mPrefs.getBoolean(SCREEN_STATUS, false);
    }

    @Override
    public boolean getCampaignStatus() {
        return mPrefs.getBoolean(CAMPAIGN_VISIBILITY, false);}


    @Override
    public void setCampaignStatus(Boolean status) {
        mPrefs.edit().putBoolean(CAMPAIGN_VISIBILITY, status).apply();
    }
}