package in.ecomexpress.sathi.utils;

public interface GlobalConstant {

    interface ImageSyncStatus {
        int IMAGE_SYNC_STATUS_NO = 0;
        int IMAGE_SYNC_STATUS_COMPLETE = 2;
        int IMAGE_SYNC_STATUS_FAILED = 4;
    }

    interface DynamicAppUrl {
        String GET_IMAGE_BY_ID_URL = "get_image_by_id";
        String UPLOAD_IMAGE_URL = "uploadImage";
        String GET_SERVER_CERT = "get_server_cert";
        String FE_AADHAR_CONSENT = "fe_aadhaar_consent";
        String DRS_LIST_OTP_VERIFY_URL = "drs_list_otp_verify";
        String requestVoiceOtp = "voice_otp_request";
        String AMAZON_RESEND = "amz_encrypted_otp";
        String KYC_DEVICE_INFO = "kyc_deviceinfo";
        String SATHI_CALL_BRIDGE_STATUS = "sathi_callbridge_status";
        String CHANGE_PASSWORD_URL = "change_password";
        String RTS_DRS_COMMIT_URL = "rts_drs_commit";
        String REVERSE_DRS_COMMIT_URL = "reverse_drs_commit";
        String LOGOUT_URL = "logout";
        String FORWARD_DRS_COMMIT_URL = "forward_drs_commit";
        String SOS_URL = "sos";
        String START_TRIP_URL = "start_trip";
        String DRS_LIST_PROCESSING_URL = "drs_list_processing";
        String LOGIN_VERIFY_OTP_URL = "loginVerifyOTP";
        String MARK_ATTENDANCE = "mark_attendance";
        String CHECK_ATTENDANCE = "check_attendance";
        String AADHAR_MASKING_STATUS = "aadhar_masking_status";
        String MASTER_DATA_CONFIGURATION_URL = "master_data_configuration";
        String TRIP_REIMBURSEMENT_URL = "trip_reimbursement";
        String DRS_LIST_OTP_RESEND_URL = "drs_list_otp_resend";
        String GENERATE_UD_OTP_URL = "ud_otp_send";
        String EDS_DRS_COMMIT_URL = "eds_drs_commit";
        String FORGOT_PASSWORD_WITH_OTP_URL = "forgot_password_with_otp";
        String REGISTER_AWB = "register_awb";
        String STOP_TRIP_URL = "stop_trip";
        String FORWARD_DRS_COMMIT_MPS_URL = "forward_drs_commit_mps";
        String PERFORMANCE_URL = "performance";
        String SMS_TECHPARK_UNABLE_TO_REACH_URL = "sms_techpark_unabletoreach";
        String DRS_OTP_RESEND_OTHER_PHONE_URL = "drs_list_otp_resend_other_phone";
        String LOGIN_RESEND_OTP = "loginResendOTP";
        String LIVE_TRACKING_ID = "get_live_tracking_id";
        String sathi_logs = "sathi_logs_v2";
        String ATTENDANCE_URL = "attendance";
        String RE_ASSIGN_AWB = "re_assign_awb";
        String UD_FWD_REATTEMPT = "ud_reattempt";
        String CHECK_PAYMENT_STATUS_URL = "check_payment_status";
        String FORGOT_PASSWORD_URL = "forgot_password";
        String DoDrsListCheck = "check_drs_list";
        String Call_Bridge_URL = "call_bridge";
        String ROUTING_API_WAY = "routing_api_way_points";
        String CONSIGNEE_PROFILE_URL = "consigneeprofile_get-profile";
        String LOG_FILE_UPLOAD_URL = "logFileUpload";
        String START_STOP_POST_TRIP = "postTripImage";
        String SELFIE_IMAGE_UPLOAD = "selfie_post_image";
        String POST_IMAGE_URL = "postImage";
        String RVP_IMAGE_URL = "rvp_postimage";
        String RVP_MPS_IMAGE_URL = "rvp_mps_postimage";
        String POST_IMAGE_BYTE_URL = "rvp_postimage_byte";
        String AADHAR_MASKIMG_API = "aadhar_masking_api";
        String RESCHEDULE_STATUS = "get_reschedule_status";
        String IMAGE_EDS_URL = "eds_postImage";
        String SEND_INSTANT_OTP = "send_instant_otp";
        String RTS_OTP_SEND_GENERATE = "rts_otp_send";
        String VERIFY_INSTANT_OTP = "verify_instant_otp";
        String DC_UPDATE_LOCATION_URL = "dc_update_location";
        String AMAZON_RESECHEDULE_API = "amazon_reschedule_api";
        String DUPLICATE_CASH_RECEIPT = "duplicate_cash_receipt";
        String BANK_RECEIPT_DETAIL = "fetch_bankreceipt_codd_details";
        String BANK_RECEIPT_IMAGE = "bank_receipt_image";
        String GET_CONSIGNEE_NUMBER = "get_consignee_number";
        String GET_IDFC_TOKEN = "get_idfc_token";
        String PAYPHI_PAYMENT = "upi_dispute_image";
        String EMP_COVID_CONSENT = "emp_covid_consent";
        String SENT_PAYHI_PAYMENT_SMS = "payphi_payment_sms";
        String VERIFY_UPI_DISPUTE_OTP = "verify_dispute_otp";
        String RAISE_UPI_DISPUTE = "raise_upi_dispute";
        String ADM_DAILY_EARNING = "adm_daily_earning";
        String ADM_EMP_PERFORMANCE = "adm_emp_performance";
        String VERIFY_REFERNCE_CODE = "verify_reference_code";
        String rvpflyer_duplicate_check = "rvpflyer_duplicate_check";
        String update_dp_avalibility_details = "update_dp_avalibility_details";
        String shipment_weight_update = "shipment_weight_update";
        String unify_apps = "unify_apps";
        String rts_resend_otp = "rts_resend_otp";
        String rts_resend_Other_mobile_otp = "rts_resend_otp_alternate";
        String rts_verify_otp = "rts_verify_otp";
        String shipment_reschedule_details = "shipment_reschedule_details";
        String unify_apps_lms = "unify_apps_lms";
        String unify_apps_campaign = "unify_apps_campaign";
        String modular_shipment = "unify_apps_modular_shipment";
    }

    interface APP_URL {
        String EKYC_XML_URL = "https://uat-esb.fincarebank.in/restgateway/services/ecom/ekyc";
    }

    interface ShipmentTypeConstants {
        String FWD = "FWD";
        String FWD_OBD = "FWD_OBD";
        String RTS = "RTS";
        String RVP = "RVP";
        String RVP_MPS = "RVP_MPS";
        String RQC = "RQC";
        String EDS = "EDS";
        String OTHER = "OTHER";
    }

    interface ImageTypeConstants {
        String FWD = "FWD";
        String FWD_OBD = "FWD_OBD";
        String RTS = "RTS";
        String RVP = "RVP";
        String RVP_QC = "RVP_QC";
        String RVP_MPS = "RVP_MPS";
        String EDS = "EDS";
        String OTHERS = "OTHERS";
        String UD_RTS_IMAGE = "UD_RTS_IMAGE";
    }

    interface ShipmentStatusConstants {
        String ASSIGNED = "Pending";
        String DELIVERED = "Successful";
        String SYNC = "SYNC";
        String UNDELIVERED = "Unsuccessful";
        int ASSIGNED_INT = 0;
    }

    interface RemarksTypeConstants {
        String CONSIGNEE_NOT_PICKING_CALL = "Tried Reaching You";
        String CONSIGNEE_REQUESTED_TO_CALL_LATER = "Consignee Requested To Call Later";
        String SAME_DAY_RESCHEDULE = "Same Day Reschedule";
        String CONSIGNEE_CALLED = "Consignee Called";
        String NO_REMARKS = "No Remark";
        String I_Am_On_The_Way = "I Am On The Way";
    }

    interface QcTypeConstants {
        String CHECK = "CHECK";
        String INPUT = "INPUT";
        String CHECK_IMAGE = "CHECK_IMAGE";
    }

    interface EDSActivityName {
        String DOCUMENT_COLLECTION = "Document Collection";
        String DOCUMENT_VERIFICATION = "Document Verification";
        String ACTIVITY = "Activities";
    }

    interface ShipmentTypeDetailConstants {
        String SHIPMENT_FWD = "Forward Shipment";
        String SHIPMENT_RTS = "Return To Shipper";
        String SHIPMENT_RVP = "Reverse Pickup";
        String SHIPMENT_EDS = "Ecom Digital Service";
    }

    interface EdsTaskList {
        String DOCUMENT_COLLECTION = "Documents to be collected";
        String DOCUMENT_VERIFICATION = "Documents to be verified ";
        String ACTIVITIES = "Activities";
    }

    interface CommitStatus {
        int CommitAssign = 0;
        int CommitSecondAttempt = 3;
        int CommitSynced = 2;
        int CommitSyncedFailed = 4;
    }

    interface ErrorCodes {
        int HTTP_EXCEPTION = 10101;
    }
}