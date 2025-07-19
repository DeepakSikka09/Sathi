package in.ecomexpress.sathi.utils;

import android.Manifest;
import android.os.Build;

import java.util.Locale;

import in.ecomexpress.sathi.BuildConfig;

public class Constants {
    public static final String DB_NAME = "sathi_database.db";
    public static final String DECRYPT_IDFC = "0id6Fc0Pe7M22";
    public static final String IS_SECURE_DELIVERY = "false";
    public static float IS_USING_FAKE_GPS = 0;
    public static boolean UPLOADEDS_CALLED = false;
    public static String Esper_key = "ZtXVTpNc6g5uqNXpbUKYbC8tr2z03E";
    public static String PAYMENT_MODE = "";
    public static String PLAIN_OTP = "";
    public static String WEB_TOKEN = "";
    public static boolean BACKTODOLIST = false;
    public static boolean CONSIGNEE_PROFILE = false;
    public static String CONSIGNEE_LOCATION_VERIFIED = "CONSIGNEE_LOCATION_VERIFIED";
    public static boolean IS_CASH_COLLECTION_ENABLE = false;
    public static final String DECRYPT = "9kVQF86X";
    public static boolean IS_CALL_BRIDGE_FLAG_ON = false;
    public static boolean IS_CALL_CLICK = false;
    public static boolean IS_CALL_CLICK_VERIFY = false;
    public static boolean IS_CALL_BRIDGE_FLAG_ON_STATUS = false;
    public static String TRY_RECHING_COUNT = "TRY_RECHING_COUNT";
    public static String TECH_PARK_COUNT = "TECH_PARK_COUNT";
    public static final String PREF_NAME = "sathi_preference";
    public static final String TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";
    public static long SYNC_DELAY_TIME = 3 * 60 * 1000;
    public static final String DISTANCE_API_KEY = "AIzaSyBDXuU3HgiGUZg8CQt5HIT-TtbEcfxytlQ";
    public static final String DB_PASS = BuildConfig.DB_KEY;
    public static boolean OFD_OTP_VERIFIED = false;
    public static final String VERSION_NAME = BuildConfig.VERSION_NAME;
    public static final Integer VERSION_CODE = BuildConfig.VERSION_CODE;
    public static final String ENCRYPTION_KEY = BuildConfig.ENCRYPTION_KEY;
    public static final String API_KEY = BuildConfig.API_KEY;
    public static final String SERVER_URL = BuildConfig.BASE_URL;
    public static final String IS_CASH_COLLECTION = "is_cash_collection";
    public static final String NEWLAND = "NEWLAND:MT65";
    public static final String NEWLAND_90 = "NEWLAND:NLS-MT90";
    public static final String NEWLAND_DROI = "DROI:NLS-MT90";
    public static final String sign_image_required = "sign_image_required";
    public static final String return_package_barcode = "return_package_barcode";
    public static final String FWD_DEL_IMAGE = "FWD_DEL_IMAGE";
    public static final String AMAZON_ENCRYPTED_OTP = "amazon_encrypted_otp";
    public static final String AMAZON = "amazon";
    public static String CURRENT_LATITUDE = "0.0";
    public static boolean IS_REBOOT = false;
    public static boolean RVP_Sign_Image_Required = false;
    public static String CURRENT_LONGITUDE = "0.0";
    public static final String DLIGHT_ENCRYPTED_OTP1 = "dlight_encrypted_otp1";
    public static final String ISDELIGHTSHIPMENT = "is_delight_shipment";
    public static final String DLIGHT_ENCRYPTED_OTP2 = "dlight_encrypted_otp2";
    public static final String CONSIGNEE_MOBILE = "CONSIGNEE_MOBILE";
    public static final String OBD_CONSIGNEE_NAME = "OBD_CONSIGNEE_NAME";
    public static final String OBD_REFUSED = "OBD_REFUSED";
    public static final String OBD_AWB_NUMBER = "OBD_AWB_NUMBER";
    public static final String OBD_CONSIGNEE_ADDRESS = "OBD_CONSIGNEE_ADDRESS";
    public static final String OBD_VENDOR_NAME = "OBD_VENDOR_NAME";
    public static final String OBD_ITEM_NAME = "OBD_ITEM_NAME";
    public static final String OBD_ADDRESS_PROFILED = "OBD_ADDRESS_PROFILED";
    public static String SUB_SHIPPER_EMAIL = "";
    public static final String CONSIGNEE_ALTERNATE_MOBILE = "CONSIGNEE_ALTERNATE_MOBILE";
    public static final String RVP_FLYER_SCANNED_VALUE = "RVP_FLYER_SCANNED_VALUE";
    public static final String RESEND_SECURE_OTP = "resend_secure_otp";
    public static boolean IS_CPV_ACTIVITY_EXITS = false;
    public static int FRAGMENT_LIST = 0;
    public static int FRAGMENT_MAP = 1;
    public static int FRAGMENT_VISIBLE = FRAGMENT_LIST;
    public static final String ZEBRA = "ZEBRA TECHNOLOGIES:MC36";
    public static long rtsVWDetailID = 0L;
    public static String RVPCOMMIT = "RVP";
    public static final String RTSCOMMIT = "RTS";
    public static final int SYNCED = 2;
    public static int CHECKFIRSTUPLOAD = 0;
    public static int Image_Click_Pos = 0;
    public static int Image_Array_Size = 0;
    public static int Image_Check = 0;
    public static int forward_call_count = 0;
    public static int eds_call_count = 0;
    public static int rvp_call_count = 0;
    public static int rts_call_count = 0;
    public static final String CAMSCANNER_APPKEY = "54420fb6455ac11e0e23311470-rpbzrkcerff";
    public static final int CONSIGNEE_PROFILING_METER_RANGE = 100;
    public static final String EMP_CODE = "employee_code";
    public static final String TOKEN = "auth_token";
    public static final String ERROR_404 = "Server Down. Please try again later.";
    public static final long INVALID_USER = 107;
    public static final String MESSAGE = "message";
    public static final String OTHERS = "OTHERS";
    public static final String SECURE_UNDELIVERED = "secure_undelivered";
    public static final String DRS_PSTN_KEY = "drs_pstn_key";
    public static final String DRS_API_KEY = "drs_api_key";
    public static final String SECURE_DELIVERY = "secure_delivery";
    public static final String DRS_PIN = "drs_pin";
    public static final String DRS_ID = "drs_id";
    public static final String IS_CARD = "is_card";
    public static final String SELECT = "--Select--";
    public static final String ENC_DEC_KEY = ".EcomExpress";
    public static final String EKYCXML = "AC_KYC_XML";
    public static final String EKYCRBL = "AC_RBL_BKYC";
    public static final String EKYCPAYTM = "AC_PAYTM_EKYC";
    public static final String EKYCPAYTMM = "AC_PAYTMM_EKYC";
    public static final String VODA_CONNECT_URL = "com.mobicule.vodafone.ekyc.client";
    public static final String VEHICLE_REGEX = "([A-Z]{2}\\d{2}[A-Z]{3}\\d{4})|([A-Z]{2}\\d{2}[A-Z]{2}\\d{4})|([A-Z]{2}\\d{2}[A-Z]{1}\\d{4})|([A-Z]{2}\\d{6})|([A-Z]{3}\\d{4})|([A-Z]{2}\\d{3}[A-Z]{1}\\d{4})|([A-Z]{2}\\d{1}[A-Z]{2}\\d{4})|[A-Z]{2}\\d{1}[A-Z]{3}\\d{4}";
    public static final String APP_NAME = "LastMile";
    public static final String VERIFY_OTP = "secure_otp";
    public static final String VERIFY_PIN = "secure_pin";
    public static final String Secure = "Secure";
    public static final String VERIFY_PINB = "secure_pinb";
    public static final String DELIVERED = "delivered";
    public static final String UNDELIVERED = "undelivered";
    public static final String ASSIGNED = "assigned";
    public static final String SUCCESS = "success";
    public static final String DECIDENEXT = "next";
    public static final String FWD = "fwd";
    public static final String RTS = "rts";
    public static final String RVP = "rvp";
    public static final String RQC = "RQC";
    public static final String EDS = "eds";
    public static final String PPD = "PPD";
    public static final String COD = "COD";
    public static final String OTP = "OTP";
    public static final String TECH_PARK = "TechPark";
    public static final String UNREACHABLE = "TriedReachingYou";
    public static final String RESCHEDULE_ENABLE = "reschedule_enable";
    public static final String IS_KYC_ACTIVE = "is_kyc_active";
    public static boolean IS_RUN_DIRECTION_API = false;
    public static boolean CancellationEnable = false;
    public static boolean RCHDEnable = false;
    public static long TEMP_DRSID = 0;
    public static String TEMP_CONSIGNEE_MOBILE = "";
    public static String TEMP_OFD_OTP = "";
    public static String TEMP_FYPE_NIYO = "0";
    public static String Open_To_Do = "";
    public static String Button = "Button";
    public static String Input = "Input";
    public static String OBD_START_TYPE = "OBD_START"; //OBD_start
    public static String OBD_STOP_TYPE = "OBD_STOP";  // Qc undelivered
    public static String OBD_PASS_TYPE = "OBD_PASS"; // OBD_QC_pass
    public static String OBD_FAIL_TYPE = "OBD_FAIL"; //QC_pass & undelivered
    public static String OBD_CENTRAL_CODE = "OBD_CTS";
    public static String OBD_Product_TYPE = "PPD";

    public static String Flyer_Img_Check = "flyer_img_check";

    public static final String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.GET_ACCOUNTS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE};

    public static final String[] permissionsupperN = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.GET_ACCOUNTS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.CALL_PHONE, Manifest.permission.ANSWER_PHONE_CALLS, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE};

    public static final String[] permissionsbelowN = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.GET_ACCOUNTS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE};

    public static final String INTENT_KEY = "intent_key";
    public static final String otp_required_for_delivery = "otp_required_for_delivery";
    public static final String ORDER_ID = "order_id";
    public static final String OFD_OTP = "OFD_OTP";
    public static final String DRS_ID_NUM = "drs_id_num";
    public static final String SHOW_FWD_UNDL_BTN = "No";
    public static boolean uD_OTP_API_CHECK = false;
    public static boolean rD_OTP_API_CHECK = false;
    public static final String INTENT_KEY1 = "intent_key1";
    public static final String INTENT_KEY_RVP_WITH_QC = "rvp_with_qc";
    public static final String INTENT_KEY_RVP_SAMPLE_QUESTIONS = "rvp_sample_questions";
    public static final String INTENT_KEY_EDS_WITH_ACTIVITY = "eds_with_activity";
    public static final String INTENT_KEY_EDS_MASTER_LIST = "eds_master_list";
    public static final String FAIL = "FAIL";
    public static final String DELIVER = "DELIVERED";
    public static final String UNDELIVER = "UNDELIVERED";
    public static final int callAttempted = 1;
    public static int shipment_undelivered_count = 0;
    public static final String RELATION = "RELATION";
    //RVP QC Delivered and Undelivered constants
    public static final String RVPDELIVERED = "delivered";
    public static final String RVP_DELIVERED_REASON_CODE = "450";
    public static final String RVPUNDELIVERED = "undelivered";
    //EDS Delivered and Undelivered constants
    public static final String EDSDELIVERED = "delivered";
    public static final String EDSUNDELIVERED = "undelivered";
    //RTS  Delivered and Undelivered constants
    public static final String RTSDELIVERED = "Delivered";
    public static final String RTSDELIVEREDBbutDAMAGED = RTSDELIVERED + " But Packet Damaged";
    public static final String RTSUNDELIVERED = "Undelivered";
    public static final String RTSDISPUTED = "Disputed Delivered";
    public static int IS_ICICI_FINKARE = 0;
    public static final String RTSASSIGNED = "Assigned";
    public static final String RTSMANUALLYDELIVERED = "Manually Delivered";
    public static final String RTSMANUALLYDELIVEREDbutDAMAGED = RTSMANUALLYDELIVERED + " But Packet Damaged";
    public static final String RTSDeliveryMode = "Delivery Mode";
    public static final String RTSReAssignMode = "Re-Assign Mode";
    //for qc pager adapter
    public static final String QC_CHECK_LIST = "CheckList";
    public static final String QC_SAMPLE_QC = "SampleQc";
    //for EDS Activity List pager adapter
    public static final String EDS_ACTIVITY_LIST = "activityList";
    public static final String EDS_MASTER_LIST = "masterList";
    public static final String EDS_DATA = "edsData";
    //Filters
    public static String SHIPMENT_TYPE = "Shipment Type";
    public static String DRS_DATE = "DRS_DATE";
    public static String SHIPMENT_DECLARED_VALUE = "SHIPMENT_DECLARED_VALUE";
    public static final String IS_AMAZON_RESHEDUCLE_ENABLE = "is_amazon_reschedule_enabled";
    public static final String SHIPMENT_STATUS = "Shipment Status";
    public static final String SHIPMENT_REMARK = "Remark By";
    public static final String SHIPMENT_DELIVERED = "Delivered";
    public static final String SHIPMENT_UNDELIVERED = "Undelivered";
    public static final String COMPOSITE_KEY = "Composite_key";
    public static final String MORE = "more";
    public static final int SHIPMENT_ASSIGNED_STATUS = 0;
    public static final int SHIPMENT_DELIVERED_STATUS = 2;
    public static final int SHIPMENT_UNDELIVERED_STATUS = 3;
    public static final String SHIPMENT_TYPE_FORWARD = "forward";
    public static final String FORWARD_COMMIT_REASON_CODE = "999";
    public static final String SYNC_SERVICE = "sync_services";
    public static final String pstn_pin = "@@PIN@@";
    public static final String pstn_awb = "@@AWB@@";
    public static final String no_filter = "No Filters";
    public static final String with_filter = "Filter Applied";
    public static String OTP_DELIMITER = "OTP";
    public static String TYPE = "type";
    public static boolean SYNCFLAG = false;
    public static boolean is_dc_enabled = false;
    public static String SCANNED_DATA = "Not Found";
    public static String call_awb = "";
    public static String calling_format = "";
    public static String call_pin = "";
    public static String shipment_type = "";
    public static String broad_call_type = "";
    public static String broad_shipment_type = "";
    public static String call_intent_number = "";
    public static String EcomExpress = "EcomExpress.nomedia";
    public static String EcomExpress1 = "EcomExpress";
    public static boolean Wrong_Mobile_no = false;
    public static String ConsigneeDirectMobileNo = "0";
    public static String ContactNO = "0";
    public static String ConsigneeDirectAlternateMobileNo = "0";
    public static String awb = "";
    public static String Water_Mark_Awb = "";
    public static String SAME_DAY_REASSIGN_VERIFIED = "NOT";
    public static String IMAGE = "image";
    public static String CashCollection = "AC_CASH_COLLECTION";
    public static String Vodafone = "AC_NEO_EKYC";
    public static String Icici = "AC_ICICI_BKYC";
    public static boolean rCheduleFlag = false;
    public static String is_filter_applied = "No Filters";
    public static boolean is_fwd = false;
    public static boolean is_rvp = false;
    public static boolean is_rts = false;
    public static boolean is_eds = false;
    public static boolean is_pending = false;
    public static boolean is_success = false;
    public static boolean is_unsuccess = false;
    public static boolean is_remark1 = false;
    public static boolean is_remark2 = false;
    public static boolean is_remark3 = false;
    public static boolean is_remark4 = false;
    public static boolean is_remark5 = false;
    public static String water_mark_emp_code = "";
    public static final String PFX_PW = "S@tH!321";
    public static final String PFX_PW2 = "it@ecom";
    public static boolean START_LIVE_TRACKING = false;
    public static String MPS_AWB_NOS = "mps_awb_nos";
    public static int LOCATION_ACCURACY = 500;
    public static String MPS = "mps";
    public static double EDS_CASH_COLLECTION = 0.0;

    /*For Live Tracking URL:-
     * public static String LIVE_TRACKING_URL = "https://otc.ecomexpress.in/location/send_location_v2/";
     * public static String LIVE_TRACKING_URL = "https://test.ecomexpress.in:8030/location/send_location_v2/";
     * public static String LIVE_TRACKING_URL = "http://192.168.0.143:8000/location/send_location_v2/";
     * */

    public static String CASH_RECEIPT = "false";
    public static double START_TRIP_LAT = 0.0;
    public static double START_TRIP_LNG = 0.0;
    public static boolean IsStartTrip = false;
    public static String startTripTimestamp = "";
    public static int pendingShipmentAssignedStatus = 0;
    public static int failedShipmentStatus = 3;
    public static int successShipmentStatus = 2;

    public static int splitdecimal(double number) {
        int integer = (int) number;
        double decimal = (100 * number - 100 * integer);
        int i = (int) decimal;
        System.out.println(decimal);
        System.out.println(i);
        return i;
    }

    public static class NotificationState {
        public static final int UNREAD = 2;
        public static final boolean UNDELIVERED = false;
    }

    // All EDS reason code attributes
    public static boolean EDS_OTP = false;
    public static boolean SECURED = false;
    public static boolean EDS_CC = false;
    public static boolean EDS_CPV = false;
    public static boolean EDS_EKYC = false;
    public static boolean EDS_EKYC_FAIL = false;
    public static boolean EDS_UDAAN = false;
    public static boolean EDS_IMAGE = false;
    public static boolean EDS_PAYTM_IMAGE = false;
    public static boolean EDS_DC = false;
    public static boolean EDS_DV = false;
    public static final int DB_VERSION = 96;

    /*V2 prod configurations:-
     * public static final String PAYPHI_APPID_PROD = "79f5561ba4629c3b";
     * public static final String PAYPHI_MERCHANT_ID_PROD = "P_00092";
     * public static String LIVE_TRACKING_URL = "https://otc.ecomexpress.in/location/send_location_v2/";
     * */

    /*V2 staging configurations:-
     * public static final String PAYPHI_APPID_PROD = "80bc18249511f868";
     * public static final String PAYPHI_MERCHANT_ID_PROD = "T_00024";
     * public static String LIVE_TRACKING_URL = "https://test.ecomexpress.in:8030/location/send_location_v2/";
     * */

    // v3 prod configurations
    public static final String PAYPHI_APPID_PROD = "6c2dd59e55944825";
    public static final String PAYPHI_MERCHANT_ID_PROD = "P_30895";
    //public static String LIVE_TRACKING_URL = "https://otc.ecomexpress.in/location/saathi_v3_send_location/";
    public static String LIVE_TRACKING_URL = "https://sathi3.eexp.in/otc/location/saathi_v3_send_location/";  // Apn Sim provided by JAVA team.

    /*V3 staging configurations
     * public static final String PAYPHI_APPID_PROD = "6c2dd59e55944825";
     * public static final String PAYPHI_MERCHANT_ID_PROD = "P_30895";
     * public static String LIVE_TRACKING_URL = "https://test.ecomexpress.in:8030/location/saathi_v3_send_location/";
     * */

    public static final String BROADCAST_DETECTED_ACTIVITY = "activity_intent";
    public static long number_of_fwd_shipments = 0;
    public static String device = (Build.MANUFACTURER + ":" + Build.MODEL).toUpperCase(Locale.US);
    public static final int PICK_FROM_CAMERA = 0x000010;
    public static final int IMAGE_SCANNER_CODE = 1001;
    public static final int CAMERA_SCANNER_CODE = 1002;
    public static final int REQUEST_CODE_SCAN = 1101;

    public static final String Shield_Site_Id = BuildConfig.SHIELD_ID;
    public static final String Shield_Secret_Key = BuildConfig.SHIELD_KEY;

    public static final String Flyer_SCAN = "flyr_scan";
    public static final String AWB_SCAN = "awb_scan";
    public static final String CAMERA_TYPE = "camera_type";
    public static final String AWB_NUMBER = "AWB_NUMBER";
    public static final String CALL_ALLOWED = "CALL_ALLOWED";
    public static final String ITEM_DESCRIPTION = "ITEM_DESCRIPTION";
    public static final String CONSIGNEE_NAME = "CONSIGNEE_NAME";
    public static final String SECURE_DELIVERY_OTP = "SECURE_DELIVERY_OTP";
    public static final String QC_WIZARDS = "QC_WIZARDS";
    public static final String IS_FAILED = "IS_FAILED";
    public static final String IS_FROM = "IS_FROM";
}