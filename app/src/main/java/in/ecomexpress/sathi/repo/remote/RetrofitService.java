package in.ecomexpress.sathi.repo.remote;

import com.google.firebase.encoders.annotations.Encodable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import in.ecomexpress.sathi.repo.local.data.callbridge.CallApiRequest;
import in.ecomexpress.sathi.repo.local.data.eds.EDSCommitResponse;
import in.ecomexpress.sathi.repo.local.data.eds.EdsCommit;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommitResponse;
import in.ecomexpress.sathi.repo.local.data.rts.RTSCommit;
import in.ecomexpress.sathi.repo.local.data.rts.RTSCommitResponse;
import in.ecomexpress.sathi.repo.local.data.rvp.RVPCommitResponse;
import in.ecomexpress.sathi.repo.local.data.rvp.RvpCommit;
import in.ecomexpress.sathi.repo.local.db.model.LiveTrackingLogTable;
import in.ecomexpress.sathi.repo.remote.model.DCLocationUpdate.DCLocationUpdate;
import in.ecomexpress.sathi.repo.remote.model.DCLocationUpdate.DCLocationUpdateResponse;
import in.ecomexpress.sathi.repo.remote.model.EarningApiRequest;
import in.ecomexpress.sathi.repo.remote.model.EarningApiResponse;
import in.ecomexpress.sathi.repo.remote.model.EncryptContactResponse;
import in.ecomexpress.sathi.repo.remote.model.ErrorResponse;
import in.ecomexpress.sathi.repo.remote.model.FwdReattemptRequest.FwdReattemptRequest;
import in.ecomexpress.sathi.repo.remote.model.FwdReattemptResponse.FwdReattemptResponse;
import in.ecomexpress.sathi.repo.remote.model.GenerateToken;
import in.ecomexpress.sathi.repo.remote.model.GenerateTokenNiyo;
import in.ecomexpress.sathi.repo.remote.model.IciciResponse.IciciResponse;
import in.ecomexpress.sathi.repo.remote.model.IciciResponse.IciciResponse_standard;
import in.ecomexpress.sathi.repo.remote.model.IncentiveResponse.IncentiveResponse;
import in.ecomexpress.sathi.repo.remote.model.LiveTrackingID.LiveTrackingRequest;
import in.ecomexpress.sathi.repo.remote.model.LiveTrackingID.LiveTrackingResponse;
import in.ecomexpress.sathi.repo.remote.model.LiveTrackingLogRespone;
import in.ecomexpress.sathi.repo.remote.model.adm.ADMDATA;
import in.ecomexpress.sathi.repo.remote.model.adm.ADMUpdateRequest;
import in.ecomexpress.sathi.repo.remote.model.adm.ADMUpdateResponse;
import in.ecomexpress.sathi.repo.remote.model.antwork.BioMatricResponse;
import in.ecomexpress.sathi.repo.remote.model.antwork.BiomatricRequest;
import in.ecomexpress.sathi.repo.remote.model.antwork.WadhResponse;
import in.ecomexpress.sathi.repo.remote.model.antwork.WathRequest;
import in.ecomexpress.sathi.repo.remote.model.attendance.AttendanceRequest;
import in.ecomexpress.sathi.repo.remote.model.attendance.AttendanceResponse;
import in.ecomexpress.sathi.repo.remote.model.carddetection.CardDetectionResponse;
import in.ecomexpress.sathi.repo.remote.model.chola.CholaRequest;
import in.ecomexpress.sathi.repo.remote.model.chola.CholaResponse;
import in.ecomexpress.sathi.repo.remote.model.commonrequest.CommonUserIdRequest;
import in.ecomexpress.sathi.repo.remote.model.commonrequest.LogFileUploadResponse;
import in.ecomexpress.sathi.repo.remote.model.consignee_profile.ConsigneeProfileRequest;
import in.ecomexpress.sathi.repo.remote.model.consignee_profile.ConsigneeProfileResponse;
import in.ecomexpress.sathi.repo.remote.model.covid.CovidApiResponse;
import in.ecomexpress.sathi.repo.remote.model.covid.CovidRequest;
import in.ecomexpress.sathi.repo.remote.model.device_upload.Biometric_requestdata;
import in.ecomexpress.sathi.repo.remote.model.device_upload.Biometric_response;
import in.ecomexpress.sathi.repo.remote.model.dp_daily_earned.DPDailyEarnedAmount;
import in.ecomexpress.sathi.repo.remote.model.dp_daily_earned.DPReferenceCodeRequest;
import in.ecomexpress.sathi.repo.remote.model.dp_daily_earned.DPReferenceCodeResponse;
import in.ecomexpress.sathi.repo.remote.model.dp_daily_earned.DailyEarningRequest;
import in.ecomexpress.sathi.repo.remote.model.dp_daily_earned.DailyEarningResponse;
import in.ecomexpress.sathi.repo.remote.model.dp_daily_earned.PayoutRequest;
import in.ecomexpress.sathi.repo.remote.model.dp_daily_earned.PayoutResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.AmazonReschedule.AmazonScheduleRequest;
import in.ecomexpress.sathi.repo.remote.model.drs_list.AmazonReschedule.Amazon_reschedule_list;
import in.ecomexpress.sathi.repo.remote.model.drs_list.DRSResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.LastMileDRSListResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.common.AmazonOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.drs_list.common.AmazonOtpResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.eds.ImageQualityResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.eds.UploadImageResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.ForwardCallResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.gmap.GMapRequest;
import in.ecomexpress.sathi.repo.remote.model.drs_list.gmap.GMapResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.RvpFlyerDuplicateCheckRequest;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.RvpFlyerDuplicateCheckResponse;
import in.ecomexpress.sathi.repo.remote.model.eds.AadharMaskingResponse;
import in.ecomexpress.sathi.repo.remote.model.eds.AadharStatusResponse;
import in.ecomexpress.sathi.repo.remote.model.eds.CashReceipt_Request;
import in.ecomexpress.sathi.repo.remote.model.eds.CashReceipt_Response;
import in.ecomexpress.sathi.repo.remote.model.eds.Get_Status_Masking;
import in.ecomexpress.sathi.repo.remote.model.eds.EdsRescheduleRequest;
import in.ecomexpress.sathi.repo.remote.model.eds.EdsRescheduleResponse;
import in.ecomexpress.sathi.repo.remote.model.eds.IDFCToken_Response;
import in.ecomexpress.sathi.repo.remote.model.ekyc_xml_response.ekycXMLResponse;
import in.ecomexpress.sathi.repo.remote.model.forward.dispute_dialog.DisputeApiResponse;
import in.ecomexpress.sathi.repo.remote.model.forward.dispute_dialog.DisputeRequest;
import in.ecomexpress.sathi.repo.remote.model.forward.dispute_image.DisputeImageApiResponse;
import in.ecomexpress.sathi.repo.remote.model.forward.generateOTP.GenerateOTPRequest;
import in.ecomexpress.sathi.repo.remote.model.forward.generateOTP.GenerateOTPResponse;
import in.ecomexpress.sathi.repo.remote.model.forward.reassign.FWDReassignRequest;
import in.ecomexpress.sathi.repo.remote.model.forward.reassign.FwdReassignReponse;
import in.ecomexpress.sathi.repo.remote.model.forward.verifyOTP.VerifyOTPRequest;
import in.ecomexpress.sathi.repo.remote.model.forward.verifyOTP.VerifyOTPResponse;
import in.ecomexpress.sathi.repo.remote.model.forward.verify_dispute_otp.VerifyDisputeApiResponse;
import in.ecomexpress.sathi.repo.remote.model.forward.verify_dispute_otp.VerifyDisputeRequest;
import in.ecomexpress.sathi.repo.remote.model.fuel.FuelReimbursementRequest;
import in.ecomexpress.sathi.repo.remote.model.fuel.response.FuelReimbursementResponse;
import in.ecomexpress.sathi.repo.remote.model.hospital.Hospital;
import in.ecomexpress.sathi.repo.remote.model.image.ImageUploadResponse;
import in.ecomexpress.sathi.repo.remote.model.login.ChangePasswordRequest;
import in.ecomexpress.sathi.repo.remote.model.login.CheckAttendanceResponse;
import in.ecomexpress.sathi.repo.remote.model.login.ForgetPasswordUserRequest;
import in.ecomexpress.sathi.repo.remote.model.login.ForgotPasswordResponse;
import in.ecomexpress.sathi.repo.remote.model.login.LoginRequest;
import in.ecomexpress.sathi.repo.remote.model.login.LoginResponse;
import in.ecomexpress.sathi.repo.remote.model.login.LogoutRequest;
import in.ecomexpress.sathi.repo.remote.model.login.LogoutResponse;
import in.ecomexpress.sathi.repo.remote.model.login.MarkAttendanceRequest;
import in.ecomexpress.sathi.repo.remote.model.login.MarkAttendanceResponse;
import in.ecomexpress.sathi.repo.remote.model.login.OTPVerifyWithPasswordRequest;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterDataConfig;
import in.ecomexpress.sathi.repo.remote.model.masterdata.masterRequest;
import in.ecomexpress.sathi.repo.remote.model.otp.otherNo.OtherNoRequest;
import in.ecomexpress.sathi.repo.remote.model.otp.otherNo.OtherNoResponse;
import in.ecomexpress.sathi.repo.remote.model.otp.resendotp.GenerateUDOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.otp.resendotp.ResendOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.otp.resendotp.ResendOtpResponse;
import in.ecomexpress.sathi.repo.remote.model.otp.verifyotp.VerifyOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.otp.verifyotp.VerifyOtpResponse;
import in.ecomexpress.sathi.repo.remote.model.otp.verifyotp.VerifyUDOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.payphi.awb_register.AwbRequest;
import in.ecomexpress.sathi.repo.remote.model.payphi.awb_register.AwbResponse;
import in.ecomexpress.sathi.repo.remote.model.payphi.payment_link_sms.PaymentSmsLinkRequset;
import in.ecomexpress.sathi.repo.remote.model.payphi.payment_link_sms.SmsLinkResponse;
import in.ecomexpress.sathi.repo.remote.model.performance.PerformanceRequest;
import in.ecomexpress.sathi.repo.remote.model.performance.PerformanceResponse;
import in.ecomexpress.sathi.repo.remote.model.print_receipt.PrintReceiptRequest;
import in.ecomexpress.sathi.repo.remote.model.print_receipt.PrintReceiptResponse;
import in.ecomexpress.sathi.repo.remote.model.print_receipt.PrintReceiptUploadResponse;
import in.ecomexpress.sathi.repo.remote.model.reschedule.ReshceduleDetailsResponse;
import in.ecomexpress.sathi.repo.remote.model.reschedule.ResheduleDetailsRequest;
import in.ecomexpress.sathi.repo.remote.model.rts.RTSResendOTPRequest;
import in.ecomexpress.sathi.repo.remote.model.rts.RTSResendOTPResponse;
import in.ecomexpress.sathi.repo.remote.model.rts.RTSVerifyOTPRequest;
import in.ecomexpress.sathi.repo.remote.model.rts.RTSVerifyOTPResponse;
import in.ecomexpress.sathi.repo.remote.model.rts.generateOTP.GenerateRTSOTPRequest;
import in.ecomexpress.sathi.repo.remote.model.rts.generateOTP.GenerateRTSOTPResponse;
import in.ecomexpress.sathi.repo.remote.model.shipment_weight.ShipmentWeightResponse;
import in.ecomexpress.sathi.repo.remote.model.sms.SmsRequest;
import in.ecomexpress.sathi.repo.remote.model.sos.SOSRequest;
import in.ecomexpress.sathi.repo.remote.model.sos.SOSResponse;
import in.ecomexpress.sathi.repo.remote.model.trip.AdharApiResponse;
import in.ecomexpress.sathi.repo.remote.model.trip.AdharRequest;
import in.ecomexpress.sathi.repo.remote.model.trip.DrsCheckListRequest;
import in.ecomexpress.sathi.repo.remote.model.trip.DrsCheckListResponse;
import in.ecomexpress.sathi.repo.remote.model.trip.SelfieImageResponse;
import in.ecomexpress.sathi.repo.remote.model.trip.StartTripRequest;
import in.ecomexpress.sathi.repo.remote.model.trip.StartTripResponse;
import in.ecomexpress.sathi.repo.remote.model.trip.StopTripRequest;
import in.ecomexpress.sathi.repo.remote.model.trip.StopTripResponse;
import in.ecomexpress.sathi.repo.remote.model.verifyOtp.LoginVerifyOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.verifyOtp.LoginVerifyOtpResponse;
import in.ecomexpress.sathi.repo.remote.model.voice_otp.VoiceOTP;
import in.ecomexpress.sathi.repo.remote.model.voice_otp.VoiceOTPResponse;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface RetrofitService {

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver: " + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST("login/")
    Single<LoginResponse> doLoginApiCall(@Body LoginRequest request);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver: " + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<ConsigneeProfileResponse> doConsigneeProfileApiCall(@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Url String baseurl,@Body ConsigneeProfileRequest request);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver: " + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<LogoutResponse> doLogoutApiCall(@Url String logout,@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body LogoutRequest logoutRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<ForgotPasswordResponse> doForgetPasswordApiCall(@Url String forgot_pass,@Header("ecom_dlv_region") String ecom_dlv_region,@Body ForgetPasswordUserRequest forgetPasswordUserRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<ForgotPasswordResponse> doOTPVerifyWithPasswordApiCall(@Url String forgot_pass_with_otp,@Header("ecom_dlv_region") String ecom_dlv_region,@Body OTPVerifyWithPasswordRequest otpVerifyWithPasswordRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver: " + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<AttendanceResponse> doAttendanceApiCall(@Url String attendance_url,@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body AttendanceRequest attendanceRequest);

    //Performance
    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver: " + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<PerformanceResponse> doPerformanceApiCall(@Url String performance_url,@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body PerformanceRequest performanceRequest);

    @GET
    Single<IncentiveResponse> doFEIncentiveApiCall(@Url String url,@Query("emp_code") String emp_code);

    //ud_fwd_reattempt
    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver: " + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<FwdReattemptResponse> doUdFwdReattemptApiCall(@Url String fwd_reattempt_url,@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body FwdReattemptRequest attendanceRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<ForgotPasswordResponse> doResetPasswordApiCall(@Url String change_password,@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body ChangePasswordRequest changePasswordRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<LastMileDRSListResponse> doDRSListApiCall(@Url String drs_list_url,@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body CommonUserIdRequest commonUserIdRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<DRSResponse> doDRSListApiNewCall(@Url String drs_list_url,@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body CommonUserIdRequest commonUserIdRequest);

    //Verify Otp
    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<VerifyOtpResponse> doVerifyOtpApiCall(@Url String drs_list_otp_url,@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body VerifyOtpRequest verifyOtpRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<VoiceOTPResponse> doVoiceOtpApiCall(@Url String drs_list_otp_url, @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body VoiceOTP verifyOtpRequest);

    //Resend Otp
    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<ResendOtpResponse> doResendOtpApiCall(@Url String drs_list_otp_url,@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body ResendOtpRequest resendOtpRequest);

    //Generate Otp
    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<ResendOtpResponse> doGenerateUDOtpApiCall(@Url String drs_list_otp_url,@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body GenerateUDOtpRequest generateUDOtpRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<ResendOtpResponse> doResendUdOtpApiCall(@Url String drs_list_otp_url,@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body ResendOtpRequest resendOtpRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<AwbResponse> regAwb(@Url String register_awb_url,@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body AwbRequest awbRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<AwbResponse> regAwbNew(@Url String register_awb_url, @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body AwbRequest awbRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<SmsLinkResponse> doSentPayphiPaymentLinkSms(@Url String sent_payphi_payment_link_sms, @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body PaymentSmsLinkRequset awbRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<DCLocationUpdateResponse> doDCLocationUpdateApiCall(@Url String dc_location_update_url, @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body DCLocationUpdate dcLocationUpdate);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<AwbResponse> checkStatus(@Url String payment_status_url,@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body AwbRequest awbRequest);

    @Multipart
    @Headers({
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER,})
    @POST
    Single<ImageUploadResponse> doImageUploadApiCall(@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Url String baseurl, @HeaderMap Map<String, String> headers, @Part MultipartBody.Part image, @Part("awb_no") RequestBody awb_no, @Part("drs_no") RequestBody drs_no, @Part("image_name") RequestBody image_name, @Part("image_code") RequestBody image_code, @Part("image_type") RequestBody image_type , @Part("card_type") RequestBody card_type ,@Part("is_classification_required") RequestBody is_classification_required);

    @Multipart
    @Headers({"api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY, "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME, "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER,})
    @POST
    Single<ImageUploadResponse> doImageUploadRVPMpsApiCall(@Header("auth_token") String token, @Header("ecom_dlv_region") String ecom_dlv_region, @Url String baseurl, @HeaderMap Map<String, String> headers, @Part MultipartBody.Part image, @Part("awb") RequestBody awb_no, @Part("drs_no") RequestBody drs_no, @Part("rvp_qc_image_key") RequestBody image_name, @Part("rvp_qc_code") RequestBody image_code, @Part("image_type") RequestBody image_type, @Part("no_of_item") RequestBody no_of_item, @Part("item_id") RequestBody item_id, @Part("is_classification_required") RequestBody is_classification_required);

    @Multipart
    @Headers({
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER,})
    @POST
    Single<AadharMaskingResponse> doAadharImageUploadApiCall(@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Url String baseurl, @HeaderMap Map<String, String> headers, @Part("front_image_type") RequestBody front_image_type, @Part("back_image_type") RequestBody back_image_type, @Part("new_aadhar") RequestBody new_aadhar , @Part MultipartBody.Part front_image, @Part MultipartBody.Part rear_image);

    @Headers({
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER,})
    @POST
    Single<ImageUploadResponse> doImageByteUploadApiCall(@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Url String baseurl, @HeaderMap Map<String, String> headers, @Body ByteImageRequest byteImageRequest);

    @Multipart
    @Headers({
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER,})
    @POST
    Single<ImageUploadResponse> doImageUploadApiCallStartStop(@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Url String baseurl, @HeaderMap Map<String, String> headers, @Part MultipartBody.Part image, @Part("trip_emp_code") RequestBody trip_emp_code, @Part("trip_image_ts") RequestBody image_time, @Part("image_code") RequestBody image_code, @Part("image_name") RequestBody image_name, @Part("image_type") RequestBody image_type);

    @Multipart
    @Headers({
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER,})
    @POST
    Single<ImageUploadResponse> doImageUploadApiCallTest(@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Url String baseurl, @PartMap Map<String, RequestBody> image);

    //Start Trip
    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<StartTripResponse> doStartTripApiCall(@Url String start_trip,@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body StartTripRequest startTripRequest);

    //Stop Trip
    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<StopTripResponse> doStopTripApiCall(@Url String stop_trip,@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body StopTripRequest stopTripRequest);

    //Fuel Reimbursement
    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER,
    })
    @POST
    Single<FuelReimbursementResponse> doFuelListApiCall(@Url String trip_reimburs_trip,@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body FuelReimbursementRequest fuelReimbursementRequest);

    //Forward Undelivered commit Api call
    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER,
            "via:" + "MOBILE",
            "requesttype:" + "drs_commit"})

    @POST
    Single<ForwardCommitResponse> doFWDUndeliveredCommitApiCall(@Url String forward_drs_commit,@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @HeaderMap Map<String, String> headers, @Body ForwardCommit forwardCommit);

    //Forward commit Api call
    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER,
            "via:" + "MOBILE",
            "requesttype:" + "drs_commit"})
    @POST
    Single<ForwardCommitResponse> doFWDCommitApiCall(@Url String forward_drs_commit_mps,@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @HeaderMap Map<String, String> headers, @Body List<ForwardCommit> forwardCommit);

    //Forward commit Api call
    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER,
    })
    @POST
    Single<MasterDataConfig> doMasterReasonApiCall(@Url String master_data_config,@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body masterRequest user_name);


    //RVP Commit Api Call
    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER,
            "via:" + "MOBILE",
            "requesttype:" + "drs_commit"})
    @POST
    Single<RVPCommitResponse> doRVPUndeliveredCommitApiCall(@Url String reverse_drs_commit,@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @HeaderMap Map<String, String> headers, @Body RvpCommit rvpCommit);

    /*RTS*/
    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER,
    })
    @POST
    Single<RTSCommitResponse> doRTSCommitApiCall(@Url String rts_drs_commit,@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @HeaderMap Map<String, String> headers, @Body RTSCommit rtsCommit);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER,
            "via:" + "MOBILE",
            "requesttype:" + "image_download"})
    @GET
    Single<Call<ResponseBody>> downloadImageFromURL(@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, String url);

    @GET
    Observable<Response<ResponseBody>> downloadFileFromUrl(@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, String url);

    /*EDS*/
    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER,
    })
    @POST
    Single<EDSCommitResponse> doEDSCommitApiCall(@Url String eds_drs_commit,@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @HeaderMap Map<String, String> headers, @Body EdsCommit edsCommit);

    /*EDS*/
    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER,
    })
    @POST
    Single<ErrorResponse> doCallBridgeApiCall(@Url String call_bridge,@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body CallApiRequest callApiRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER,
    })

    @POST
    Single<GMapResponse> doGMapApiCall(@Url String routing_api_way,@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body GMapRequest gMapRequest);

    //Verify Otp
    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<LoginVerifyOtpResponse> doLoginVerifyOtpApiCall(@Url String login_verify,@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body LoginVerifyOtpRequest loginVerifyOtpRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<AadharStatusResponse> doGetStatusAadharMasking(@Url String login_verify, @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body Get_Status_Masking get_status_masking);

    //Resend Otp
    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<LoginVerifyOtpResponse> doLoginResendOtpApiCall(@Url String login_resend_otp,@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body LoginVerifyOtpRequest loginVerifyOtpRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<LiveTrackingResponse> doLiveTrackingApiCall(@Url String live_tracking_url, @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body LiveTrackingRequest liveTrackingRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<OtherNoResponse> doResendOtpToOtherNoApiCall(@Url String otp_resend_other_phone,@Header("auth_token") String authToken, @Header("ecom_dlv_region") String ecom_dlv_region, @Body OtherNoRequest otherNoRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<DRSResponse> doSMSApiCall(@Url String sms_tech,@Header("auth_token") String authToken,@Header("ecom_dlv_region") String ecom_dlv_region, @Body SmsRequest smsRequest);


    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<SOSResponse> doSOSApiCall(@Url String sos,@Header("auth_token") String authToken,@Header("ecom_dlv_region") String ecom_dlv_region, @Body SOSRequest sosRequest);

    @Multipart
    @Headers({
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<LogFileUploadResponse> doAppLogsUploadApiCall(@Url String baseurl,@Header("ecom_dlv_region") String ecom_dlv_region, @HeaderMap Map<String, String> headers, @Part MultipartBody.Part image);

    @Multipart
    @Headers({
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})

    @POST
    Single<UploadImageResponse> doImageUploadApiCall(@Url String baseurl, @Header("ecom_dlv_region") String ecom_dlv_region, @PartMap Map<String, RequestBody> headers, @Part MultipartBody.Part image);

    @Multipart
    @Headers({
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<DisputeImageApiResponse> doDisputeImageUploadApiCall(@Url String baseurl, @Header("ecom_dlv_region") String ecom_dlv_region, @PartMap Map<String, RequestBody> headers, @Part MultipartBody.Part image);

    @GET
    Single<ImageQualityResponse> doImageQuality(@Url String baseurl,@Header("ecom_dlv_region") String ecom_dlv_region, @Query("image_id") int image_id);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @GET
    Single<ForwardCallResponse> doForwardCallStatusApiCall(@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Url String baseurl, @Query("counter") int counter ,@Query("emp_code") String emp_code, @Query("awb_number") String awb_number,@Query("drs_id") String drs_id , @Query("shipper_id") int shipper_id);

    // @FormUrlEncoded
    @Headers({"content-type: application/json"})
    @POST
    Single<IciciResponse> doIciciApiCall(@Header("token")String token, @Header("ecom_dlv_region") String ecom_dlv_region, @Url String uatStartStopUrl, @Body RequestBody jobj);

    @Headers({"content-type: application/json"})
    @POST
    Single<IciciResponse> doICICICheckcStatusCall(@Header("token")String token, @Header("ecom_dlv_region") String ecom_dlv_region, @Url String uatStartStopUrl, @Body RequestBody jobj);

    @Headers({"content-type: application/json"})
    @POST
    Single<IciciResponse_standard> doICICICheckcStatusCallStandard(@Header("APIKey")String token, @Header("ecom_dlv_region") String ecom_dlv_region, @Url String uatStartStopUrl, @Body RequestBody jobj);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<Biometric_response> dobiometricApiCall(@Url String deviceurl, @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body Biometric_requestdata biometric_requestdata);

    @Headers({"Content-Type: application/xml"})
    @POST
    Single<ekycXMLResponse> doEkycXMLApiCall(@Url String ekycUrl, @Header("ecom_dlv_region") String ecom_dlv_region, @Body String jobj, @HeaderMap HashMap<String,String> webheader);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<DrsCheckListResponse> doDrsListCheck(@Url String u, @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body DrsCheckListRequest drsCheckListRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<AmazonOtpResponse> doAmazonOtpApiCall(@Url String u, @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body AmazonOtpRequest amazonOtpRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<Amazon_reschedule_list> doScheduleDates(@Url String url, @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body AmazonScheduleRequest amazonScheduleRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<CashReceipt_Response> doCashReceipt(@Url String url, @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body CashReceipt_Request cashReceipt_request);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<AdharApiResponse> DoAdharApiRequest(@Url String u, @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body AdharRequest adharRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<EdsRescheduleResponse> doEdsReschedule(@Url String u, @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body EdsRescheduleRequest edsReschedule);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @GET
    Single<EncryptContactResponse> doencryptcontact(@Url String baseurl, @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER
    })
    @POST
    Single<GenerateOTPResponse> doGenerateOTPApiCall(@Url String url, @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body GenerateOTPRequest generateOTPRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER
    })
    @POST
    Single<GenerateRTSOTPResponse> doGenerateRTSOTPApiCall(@Url String url, @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body GenerateRTSOTPRequest generateOTPRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER
       })
    @POST
    Single<WadhResponse> doGetWadhValueAntWork(@Header("Authorization") String basic , @Header("ecom_dlv_region") String ecom_dlv_region,@Url String url, @Body WathRequest wathRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER
    })
    @POST
    Single<BioMatricResponse> doGetBioMatricAntWork(@Header("Authorization") String basic , @Header("ecom_dlv_region") String ecom_dlv_region,@Url String url, @Body BiomatricRequest biomatricRequest);


    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER
    })
    @POST
    Single<FwdReassignReponse> doFwdReassingApiCall(@Header("auth_token") String token , @Url String url, @Body FWDReassignRequest fwdReassignRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<VerifyOTPResponse> doVerifyOTPApiCall(@Url String url, @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body VerifyOTPRequest verifyOtpRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<VerifyOTPResponse> doVerifyUDOtpDRSApiCall(@Url String url, @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body VerifyUDOtpRequest verifyOtpRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<CovidApiResponse> DoCovidApiRequest(@Url String u, @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body CovidRequest covidRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<PrintReceiptResponse> doPrintReceiptDataCall(@Url String l, @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body PrintReceiptRequest printReceiptRequest);
//
    @Multipart
    @Headers({
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<PrintReceiptUploadResponse> doPrintReceiptUploadImage(@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Url String baseurl, @PartMap Map<String, String> headers, @PartMap Map<String, RequestBody> data,@Part MultipartBody.Part image);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<DisputeApiResponse> doRaiseDisputeApi(@Url String u, @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body DisputeRequest disputeRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<VerifyDisputeApiResponse> doVerifyDisputeApi(@Url String u, @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body VerifyDisputeRequest verifyDisputeRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @GET("get_covid_hospitals?")
    Single<Hospital> covidHospitalList(@Header("auth_token") String token , @Header("ecom_dlv_region") String ecom_dlv_region,@Query("emp_code") String emp_code);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @GET
    Single<GenerateToken> dogeneratetoken(@Url String baseurl, @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region);

    @FormUrlEncoded
    @Encodable.Ignore
    @Headers({"Content-Type: application/x-www-form-urlencoded",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "Accept:" + "application/json",
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<IDFCToken_Response> dogeneratetokenIDFC(@Url String baseurl,@Header("ecom_dlv_region") String ecom_dlv_region, @Field("grant_type") String grant_type , @Field("client_assertion_type") String client_assertion_type,
            @Field("client_id") String client_id, @Field("scope") String scope, @Field("client_assertion") String client_assertion);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @GET("adm_live_earning?")
    Single<DPDailyEarnedAmount> doDPDailyEarningApiCall(@Header("auth_token") String token ,@Header("ecom_dlv_region") String ecom_dlv_region, @Query("empCode") String emp_code);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<VerifyOTPResponse> doDPVerifyApi(@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body VerifyOTPRequest verifyOTPRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<DailyEarningResponse> doDailyEarnigCalender(@Url String url , @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body DailyEarningRequest dailyEarningRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<PayoutResponse> doPayoutApi(@Url String url , @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body PayoutRequest payoutRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<DPReferenceCodeResponse> doDPReferenceVerifyAPI(@Url String url , @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body DPReferenceCodeRequest dpReferenceCodeRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<ReshceduleDetailsResponse> doReshceduleDetailsAPI(@Url String url , @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body ResheduleDetailsRequest resheduleDetailsRequest);

    @GET
    Single<GenerateTokenNiyo> dogenerateniyotoken(@Url String baseurl);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<RvpFlyerDuplicateCheckResponse> doRvpflyerDuplicateCheck(@Url String url , @Header("auth_token") String token, @Header("ecom_dlv_region") String ecom_dlv_region, @Body RvpFlyerDuplicateCheckRequest resheduleDetailsRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<LiveTrackingLogRespone> dosendLiveTrackingLog(@Url String url , @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body List<LiveTrackingLogTable> liveTrackingLog);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @GET("fetch_dp_avalibility_details?")
    Single<List<ADMDATA>> getAdmData(@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Query("empCode") String emp_code);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<ADMUpdateResponse> doUpdateADMData(@Url String url , @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body List<ADMUpdateRequest> admUpdateRequests);

    @Multipart
    @Headers({
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<ShipmentWeightResponse> doShipmentWeightApiCall(@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Url String baseurl, @PartMap Map<String, String> headers, @PartMap Map<String, RequestBody> data, @Part MultipartBody.Part image);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<CholaResponse> doCholaURLAPI(@Url String url , @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body CholaRequest cholaRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<RTSResendOTPResponse> doRTSResendApiCall(@Url String url , @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body RTSResendOTPRequest rtsResendOTPRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<RTSResendOTPResponse> doRTSResendApiOtherMobileRTSCall(@Url String url , @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body RTSResendOTPRequest rtsResendOTPRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<RTSVerifyOTPResponse> doRTSVerifyApiCall(@Url String url , @Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Body RTSVerifyOTPRequest rtsVerifyOTPRequest);

    @Multipart
    @Headers({
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<CardDetectionResponse> doCardDetectionApiCall(@Header("auth_token") String token,@Header("ecom_dlv_region") String ecom_dlv_region, @Url String baseurl, @PartMap Map<String, String> headers , @Part MultipartBody.Part image);

    @POST()
    Call<EarningApiResponse> doTrainingAPICall(@Url String urlName, @Header("Authorization") String authToken, @Body EarningApiRequest earningApiRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @POST
    Single<MarkAttendanceResponse> doMarkAttendanceApiCall(@Url String mark_attendance_api_url, @Header("auth_token") String token, @Body MarkAttendanceRequest markAttendanceRequest);

    @Headers({"Content-Type: application/json",
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER})
    @GET
    Single<CheckAttendanceResponse> doCheckAttendanceApiCall(@Url String check_attendance_api_url, @Header("auth_token") String token, @Query("emp_code") String empCode);

    @Multipart
    @Headers({
            "api_key:" + in.ecomexpress.sathi.BuildConfig.API_KEY,
            "app_ver:" + in.ecomexpress.sathi.BuildConfig.VERSION_NAME,
            "api_ver: " + in.ecomexpress.sathi.BuildConfig.API_VER,})
    @POST
    Single<SelfieImageResponse> doSelfieImageUploadApiCall(@Header("auth_token") String token, @Url String baseURL, @HeaderMap Map<String, String> headers, @Part MultipartBody.Part image, @Part("emp_code") RequestBody emp_code, @Part("source_code") RequestBody source_code, @Part("dc_code") RequestBody dc_code);
}