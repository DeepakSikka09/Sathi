package in.ecomexpress.sathi.repo.remote;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.ecomexpress.sathi.BuildConfig;

import javax.inject.Inject;
import javax.inject.Singleton;

import in.ecomexpress.sathi.SathiApplication;
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
import in.ecomexpress.sathi.repo.remote.model.distancecalculations.DistanceApiResponse;
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
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterDataReasonCodeResponse;
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

import in.ecomexpress.sathi.utils.GlobalConstant;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class RestApiHelper implements IRestApiHelper {

    private static final Logger log = LoggerFactory.getLogger(RestApiHelper.class);
    @Inject
    Context context;
    private final RetrofitService retrofitService;

    @Inject
    public RestApiHelper(RetrofitService retrofitService) {
        this.retrofitService = retrofitService;
    }

    @Override
    public Single<LoginResponse> doLoginApiCall(LoginRequest request) {
        return retrofitService.doLoginApiCall(request);
    }

    @Override
    public Single<ConsigneeProfileResponse> doConsigneeProfileApiCall(String token, String ecomregion, ConsigneeProfileRequest consigneeProfileRequest) {
        return retrofitService.doConsigneeProfileApiCall(token, ecomregion, SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.CONSIGNEE_PROFILE_URL), consigneeProfileRequest);
    }

    @Override
    public Single<LogoutResponse> doLogoutApiCall(String token, String ecomregion, LogoutRequest request) throws IOException {
        return retrofitService.doLogoutApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.LOGOUT_URL), token, ecomregion, request);
    }

    @Override
    public Single<AttendanceResponse> doAttendanceApiCall(String token, String ecomregion, AttendanceRequest request) {
        return retrofitService.doAttendanceApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.ATTENDANCE_URL), token, ecomregion, request);
    }

    @Override
    public Single<FwdReattemptResponse> doUdFwdReattemptApiCall(String token, String ecomregion, FwdReattemptRequest request) {
        return retrofitService.doUdFwdReattemptApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.UD_FWD_REATTEMPT), token, ecomregion, request);
    }

    @Override
    public Single<PerformanceResponse> doPerformanceApiCall(String authToken, String ecomregion, PerformanceRequest request) {
        return retrofitService.doPerformanceApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.PERFORMANCE_URL), authToken, ecomregion, request);
    }

    @Override
    public Single<ekycXMLResponse> doEkycXMLApiCall(String ecomregion, String jobj, HashMap<String, String> webheader) {
        return retrofitService.doEkycXMLApiCall(GlobalConstant.APP_URL.EKYC_XML_URL, ecomregion, jobj, webheader);
    }

    @Override
    public Single<ForgotPasswordResponse> doForgetPasswordApiCall(String ecomregion, ForgetPasswordUserRequest forgetPasswordUserRequest) {
        String url = SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.FORGOT_PASSWORD_URL);
        if (url == null || url.isEmpty()) {
            url = BuildConfig.BASE_URL + "forgot-password/";
        }
        return retrofitService.doForgetPasswordApiCall(url, ecomregion, forgetPasswordUserRequest);
    }

    @Override
    public Single<ForgotPasswordResponse> doOTPVerifyWithPasswordApiCall(String ecomregion, OTPVerifyWithPasswordRequest otpVerifyWithPasswordRequest) {
        String url = SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.FORGOT_PASSWORD_WITH_OTP_URL);
        if (url == null) {
            url = BuildConfig.BASE_URL + "forgot-password-with-otp/";
        }
        return retrofitService.doOTPVerifyWithPasswordApiCall(url, ecomregion, otpVerifyWithPasswordRequest);
    }

    @Override
    public Single<ForgotPasswordResponse> doResetPasswordApiCall(String token, String ecomregion, ChangePasswordRequest changePasswordRequest) {
        return retrofitService.doResetPasswordApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.CHANGE_PASSWORD_URL), token, ecomregion, changePasswordRequest);
    }

    @Override
    public Single<LastMileDRSListResponse> doDRSListApiCall(String authToken, String ecomregion, CommonUserIdRequest commonUserIdRequest) {
        return retrofitService.doDRSListApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.DRS_LIST_PROCESSING_URL), authToken, ecomregion, commonUserIdRequest);
    }

    @Override
    public Single<DRSResponse> doDRSListApiNewCall(String authToken, String ecomregion, CommonUserIdRequest commonUserIdRequest) {
        return retrofitService.doDRSListApiNewCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.DRS_LIST_PROCESSING_URL), authToken, ecomregion, commonUserIdRequest);
    }

    @Override
    public Single<ErrorResponse> doCallBridgeApiCall(String authToken, String ecomregion, CallApiRequest callApiRequest) {
        return retrofitService.doCallBridgeApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.Call_Bridge_URL), authToken, ecomregion, callApiRequest);
    }

    @Override
    public Single<DRSResponse> doSMSApiCall(String authToken, String ecomregion, SmsRequest smsRequest) {
        return retrofitService.doSMSApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.SMS_TECHPARK_UNABLE_TO_REACH_URL), authToken, ecomregion, smsRequest);
    }

    @Override
    public Single<MasterDataReasonCodeResponse> doMasterApiCall(String authToken, String ecomregion, CommonUserIdRequest commonUserIdRequest) {
        return null;
    }

    @Override
    public Single<LoginVerifyOtpResponse> doLoginVerifyOtpApiCall(String authToken, String ecomregion, LoginVerifyOtpRequest loginVerifyOtpRequest) {
        return retrofitService.doLoginVerifyOtpApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.LOGIN_VERIFY_OTP_URL), authToken, ecomregion, loginVerifyOtpRequest);
    }

    @Override
    public Single<DisputeApiResponse> doRaiseDisputeApi(String authToken, String ecomregion, DisputeRequest disputeRequest) {
        return retrofitService.doRaiseDisputeApi(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.RAISE_UPI_DISPUTE), authToken, ecomregion, disputeRequest);
    }

    @Override
    public Single<VerifyDisputeApiResponse> doVerifyDisputeApi(String authToken, String ecomregion, VerifyDisputeRequest verifyDisputeRequest) {
        return retrofitService.doVerifyDisputeApi(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.VERIFY_UPI_DISPUTE_OTP), authToken, ecomregion, verifyDisputeRequest);
    }

    @Override
    public Single<AadharStatusResponse> doGetStatusAadharMasking(String authToken, String ecomregion, Get_Status_Masking get_status_masking) {
        return retrofitService.doGetStatusAadharMasking(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.AADHAR_MASKING_STATUS), authToken, ecomregion, get_status_masking);
    }

    @Override
    public Single<VerifyOtpResponse> doVerifyOtpApiCall(String authToken, String ecomregion, VerifyOtpRequest verifyOtpRequest) {
        return retrofitService.doVerifyOtpApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.DRS_LIST_OTP_VERIFY_URL), authToken, ecomregion, verifyOtpRequest);
    }

    @Override
    public Single<VoiceOTPResponse> doVoiceOtpApiCall(String authToken, String ecomregion, VoiceOTP voiceOTP) {
        return retrofitService.doVoiceOtpApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.requestVoiceOtp), authToken, ecomregion, voiceOTP);
    }

    @Override
    public Single<AmazonOtpResponse> doAmazonOtpApiCall(String authToken, String ecomregion, AmazonOtpRequest amazonOtpRequest) {
        return retrofitService.doAmazonOtpApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.AMAZON_RESEND), authToken, ecomregion, amazonOtpRequest);
    }

    @Override
    public Single<OtherNoResponse> doResendOtpToOtherNoApiCall(String authToken, String ecomregion, OtherNoRequest otherNoRequest) {
        return retrofitService.doResendOtpToOtherNoApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.DRS_OTP_RESEND_OTHER_PHONE_URL), authToken, ecomregion, otherNoRequest);
    }

    @Override
    public Single<ResendOtpResponse> doResendOtpApiCall(String authToken, String ecomregion, ResendOtpRequest resendOtpRequest) {
        return retrofitService.doResendOtpApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.DRS_LIST_OTP_RESEND_URL), authToken, ecomregion, resendOtpRequest);
    }

    @Override
    public Single<ResendOtpResponse> doGenerateUDOtpApiCall(String authToken, String ecomregion, GenerateUDOtpRequest generateUDOtpRequest) {
        return retrofitService.doGenerateUDOtpApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.GENERATE_UD_OTP_URL), authToken, ecomregion, generateUDOtpRequest);
    }

    @Override
    public Single<ResendOtpResponse> doResendUdOtpApiCall(String authToken, String ecomregion, ResendOtpRequest resendOtpRequest) {
        return retrofitService.doResendUdOtpApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.DRS_LIST_OTP_RESEND_URL), authToken, ecomregion, resendOtpRequest);
    }

    @Override
    public Single<StartTripResponse> doStartTripApiCall(String token, String ecomregion, StartTripRequest startTripRequest) {
        return retrofitService.doStartTripApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.START_TRIP_URL), token, ecomregion, startTripRequest);
    }

    @Override
    public Single<StopTripResponse> doStopTripApiCall(String token, String ecomregion, StopTripRequest stopTripRequest) {
        return retrofitService.doStopTripApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.STOP_TRIP_URL), token, ecomregion, stopTripRequest);
    }

    @Override
    public Single<AwbResponse> doAwbRegisterApiCall(String authToken, String ecomregion, AwbRequest awbRequest) {
        return retrofitService.regAwb(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.REGISTER_AWB), authToken, ecomregion, awbRequest);
    }

    @Override
    public Single<AwbResponse> doAwbRegisterApiCallForSms(String token, String ecomregion, AwbRequest awbRequest) {
        return retrofitService.regAwbNew(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.REGISTER_AWB), token, ecomregion, awbRequest);
    }

    @Override
    public Single<SmsLinkResponse> doSentPayphiPaymentLinkSms(String token, String ecomregion, PaymentSmsLinkRequset awbRequest) {
        return retrofitService.doSentPayphiPaymentLinkSms(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.SENT_PAYHI_PAYMENT_SMS), token, ecomregion, awbRequest);
    }

    @Override
    public Single<AwbResponse> doCheckStatusApiCall(String authToken, String ecomregion, AwbRequest awbRequest) {
        return retrofitService.checkStatus(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.CHECK_PAYMENT_STATUS_URL), authToken, ecomregion, awbRequest);
    }

    @Override
    public Single<ForwardCommitResponse> doFWDCommitApiCall(String authToken, String ecomregion, HashMap hashMap, List<ForwardCommit> forwardCommit) {
        return retrofitService.doFWDCommitApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.FORWARD_DRS_COMMIT_MPS_URL), authToken, ecomregion, hashMap, forwardCommit);
    }

    /*Forward Undelivered */
    @Override
    public Single<ForwardCommitResponse> doFWDUndeliveredCommitApiCall(String authToken, String ecomregion, HashMap hashMap, ForwardCommit forwardCommit) {
        return retrofitService.doFWDUndeliveredCommitApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.FORWARD_DRS_COMMIT_URL), authToken, ecomregion, hashMap, forwardCommit);
    }

    @Override
    public Single<RVPCommitResponse> doRVPUndeliveredCommitApiCall(String authToken, String ecomregion, HashMap hashMap, RvpCommit rvpCommit) {
        return retrofitService.doRVPUndeliveredCommitApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.REVERSE_DRS_COMMIT_URL), authToken, ecomregion, hashMap, rvpCommit);
    }

    @Override
    public Single<RTSCommitResponse> doRTSCommitApiCall(String authToken, String ecomregion, HashMap hashMap, RTSCommit rtsCommit) {
        return retrofitService.doRTSCommitApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.RTS_DRS_COMMIT_URL), authToken, ecomregion, hashMap, rtsCommit);
    }

    @Override
    public Single<EDSCommitResponse> doEDSCommitApiCall(String authToken, String ecomregion, HashMap hashMap, EdsCommit edsCommit) {
        return retrofitService.doEDSCommitApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.EDS_DRS_COMMIT_URL), authToken, ecomregion, hashMap, edsCommit);
    }

    @Override
    public Single<MasterDataConfig> doMasterReasonApiCall(String token, String ecomregion, masterRequest user_name) {
        String url = SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.MASTER_DATA_CONFIGURATION_URL);
        if (url == null || url.isEmpty()) {
            url = BuildConfig.BASE_URL + "master_data_configuration/";
        }
        return retrofitService.doMasterReasonApiCall(url, token, ecomregion, user_name);
    }

    @Override
    public Single<FuelReimbursementResponse> doFuelListApiCall(String token, String ecomregion, FuelReimbursementRequest fuelReimbursementRequest) {
        return retrofitService.doFuelListApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.TRIP_REIMBURSEMENT_URL), token, ecomregion, fuelReimbursementRequest);
    }


    @Override
    public Single<ImageUploadResponse> doImageUploadApiCall(String authToken, String ecomregion, String imageType, Map headers, Map multipartBody, MultipartBody.Part file) {
        RequestBody awb_no = (RequestBody) multipartBody.get("awb_no");
        RequestBody drs_no = (RequestBody) multipartBody.get("drs_no");
        RequestBody image_code = (RequestBody) multipartBody.get("image_code");
        RequestBody image_name = (RequestBody) multipartBody.get("image_name");
        RequestBody image_type = (RequestBody) multipartBody.get("image_type");
        RequestBody card_type = (RequestBody) multipartBody.get("card_type");
        RequestBody is_classification_required = (RequestBody) multipartBody.get("is_classification_required");
        if (imageType.equalsIgnoreCase("EDS")) {
            return retrofitService.doImageUploadApiCall(authToken, ecomregion, SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.IMAGE_EDS_URL), headers, file, awb_no, drs_no, image_name, image_code, image_type, card_type, is_classification_required);
        } else if (imageType.equalsIgnoreCase("rvp_qc")) {
            return retrofitService.doImageUploadApiCall(authToken, ecomregion, SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.RVP_IMAGE_URL), headers, file, awb_no, drs_no, image_name, image_code, image_type, card_type, is_classification_required);
        } else {
            return retrofitService.doImageUploadApiCall(authToken, ecomregion, SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.POST_IMAGE_URL), headers, file, awb_no, drs_no, image_name, image_code, image_type, card_type, is_classification_required);
        }
    }

    @Override
    public Single<AadharMaskingResponse> doAadharMaskigImageUploadApiCall(String authToken, String ecomregion, String imageType, Map headers, Map multipartBody, MultipartBody.Part front_file, MultipartBody.Part rear_file) {
        RequestBody front_image_type = (RequestBody) multipartBody.get("front_image_type");
        RequestBody back_image_type = (RequestBody) multipartBody.get("back_image_type");
        RequestBody new_aadhar = (RequestBody) multipartBody.get("new_aadhar");
        return retrofitService.doAadharImageUploadApiCall(authToken, ecomregion, SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.AADHAR_MASKIMG_API), headers, front_image_type, back_image_type, new_aadhar, front_file, rear_file);
    }

    @Override
    public ImageUploadResponse doImageUploadApiCallInSingleThread(String authToken, String ecomregion, String imageType, Map headers, Map multipartBody, MultipartBody.Part file) {
        RequestBody awb_no = (RequestBody) multipartBody.get("awb_no");
        RequestBody drs_no = (RequestBody) multipartBody.get("drs_no");
        RequestBody image_code = (RequestBody) multipartBody.get("image_code");
        RequestBody image_name = (RequestBody) multipartBody.get("image_name");
        RequestBody image_type = (RequestBody) multipartBody.get("image_type");
        RequestBody card_type = (RequestBody) multipartBody.get("card_type");
        RequestBody is_classification_required = (RequestBody) multipartBody.get("is_classification_required");
        Single<ImageUploadResponse> single = null;
        if (imageType.equalsIgnoreCase("EDS")) {
            single = retrofitService.doImageUploadApiCall(authToken, ecomregion, SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.IMAGE_EDS_URL), headers, file, awb_no, drs_no, image_name, image_code, image_type, card_type, is_classification_required);
        } else {
            single = retrofitService.doImageUploadApiCall(authToken, ecomregion, SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.POST_IMAGE_URL), headers, file, awb_no, drs_no, image_name, image_code, image_type, card_type, is_classification_required);
        }
        return single.blockingGet();
    }

    @Override
    public Single<ImageUploadResponse> doImageUploadApiCallStartStop(String authToken, String ecomregion, Map headers, Map multipartBody, MultipartBody.Part file) {
        RequestBody trip_emp_code = (RequestBody) multipartBody.get("trip_emp_code");
        RequestBody image_time = (RequestBody) multipartBody.get("trip_image_ts");
        RequestBody image_code = (RequestBody) multipartBody.get("image_code");
        RequestBody image_name = (RequestBody) multipartBody.get("image_name");
        RequestBody image_type = (RequestBody) multipartBody.get("image_type");
        return retrofitService.doImageUploadApiCallStartStop(authToken, ecomregion, SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.START_STOP_POST_TRIP), headers, file, trip_emp_code, image_time, image_code, image_name, image_type);
    }

    public Single<ImageUploadResponse> doImageUploadApiCallTest(String authToken, String ecomregion, Map multipartBody) {
        return retrofitService.doImageUploadApiCallTest(authToken, ecomregion, "http://192.168.1.176:8080/image/", multipartBody);
    }

    @Override
    public Single<Call<ResponseBody>> getImage(String authToken, String ecomregion, String url) {
        return retrofitService.downloadImageFromURL(authToken, ecomregion, url);
    }

    @Override
    public Single<GMapResponse> getGMap(String authToken, String ecomregion, GMapRequest gMapRequest) {
        return retrofitService.doGMapApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.ROUTING_API_WAY), authToken, ecomregion, gMapRequest);
    }


    @Override
    public Observable<Response<ResponseBody>> getFile(String authToken, String ecomregion, String url) {
        return retrofitService.downloadFileFromUrl(authToken, ecomregion, url);
    }

    @Override
    public Single<LoginVerifyOtpResponse> doLoginResendOtpApiCall(String authToken, String ecomregion, LoginVerifyOtpRequest loginVerifyOtpRequest) {
        return retrofitService.doLoginResendOtpApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.LOGIN_RESEND_OTP), authToken, ecomregion, loginVerifyOtpRequest);
    }

    @Override
    public Single<LiveTrackingResponse> dogetLiveTrackingIDApiCall(String authToken, String ecomregion, LiveTrackingRequest liveTrackingRequest) {
        return retrofitService.doLiveTrackingApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.LIVE_TRACKING_ID), authToken, ecomregion, liveTrackingRequest);
    }

    @Override
    public Single<LiveTrackingLogRespone> dosendLiveTrackingLog(String authToken, String ecomregion, List<LiveTrackingLogTable> liveTrackingLog) {
        return retrofitService.dosendLiveTrackingLog(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.sathi_logs), authToken, ecomregion, liveTrackingLog);
    }

    @Override
    public Single<SOSResponse> doSOSApiCall(String authToken, String ecomregion, SOSRequest sosRequest) {
        return retrofitService.doSOSApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.SOS_URL), authToken, ecomregion, sosRequest);
    }

    @Override
    public Single<IciciResponse> doICICIApiCall(String ss, String ecomregion, String url, RequestBody jobj) {
        return retrofitService.doIciciApiCall(ss, ecomregion, url, jobj);
    }

    @Override
    public Single<IciciResponse> doICICICheckcStatusCall(String ss, String ecomregion, String url, RequestBody jobj) {
        return retrofitService.doICICICheckcStatusCall(ss, ecomregion, url, jobj);
    }

    @Override
    public Single<IciciResponse_standard> doICICICheckcStatusCallStandard(String apikey, String ecomregion, String url, RequestBody urn) {
        return retrofitService.doICICICheckcStatusCallStandard(apikey, ecomregion, url, urn);
    }

    @Override
    public Single<ImageUploadResponse> doImageByteUploadApiCall(String authToken, String ecomregion, String imageType, Map headers, ByteImageRequest data) {
        if (imageType.equalsIgnoreCase("eds")) {
            return retrofitService.doImageByteUploadApiCall(authToken, ecomregion, SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.IMAGE_EDS_URL), headers, data);
        } else if (imageType.contains("RVP_BYTE")) {
            return retrofitService.doImageByteUploadApiCall(authToken, ecomregion, SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.POST_IMAGE_BYTE_URL), headers, data);
        } else if (imageType.contains("RVP")) {
            return retrofitService.doImageByteUploadApiCall(authToken, ecomregion, SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.RVP_IMAGE_URL), headers, data);
        } else {
            return retrofitService.doImageByteUploadApiCall(authToken, ecomregion, SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.POST_IMAGE_URL), headers, data);
        }
    }

    @Override
    public Single<DrsCheckListResponse> doDrsListCheck(String authToken, String ecomregion, DrsCheckListRequest drsCheckListRequest) {
        String url = SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.DoDrsListCheck);
        if (url == null || url.isEmpty()) {
            url = BuildConfig.BASE_URL + "check_drs_list/";
        }
        return retrofitService.doDrsListCheck(url, authToken, ecomregion, drsCheckListRequest);
    }

    @Override
    public Single<AdharApiResponse> doAdharApi(String authToken, String ecomregion, AdharRequest adharRequest) {
        return retrofitService.DoAdharApiRequest(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.FE_AADHAR_CONSENT), authToken, ecomregion, adharRequest);
    }

    @Override
    public Single<CovidApiResponse> doCovidApi(String authToken, String ecomregion, CovidRequest covidRequest) {
        return retrofitService.DoCovidApiRequest(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.EMP_COVID_CONSENT), authToken, ecomregion, covidRequest);
    }

    @Override
    public Single<DCLocationUpdateResponse> doDCLocationUpdateApiCall(String authToken, String ecomregion, DCLocationUpdate dcLocationUpdate) {
        return retrofitService.doDCLocationUpdateApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.DC_UPDATE_LOCATION_URL)/*SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.DC_UPDATE_LOCATION_URL)*/, authToken, ecomregion, dcLocationUpdate);
    }

    @Override
    public Single<Amazon_reschedule_list> doScheduleDates(String authToken, String ecomregion, AmazonScheduleRequest strings) {
        return retrofitService.doScheduleDates(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.AMAZON_RESECHEDULE_API), authToken, ecomregion, strings);
    }


    @Override
    public Single<CashReceipt_Response> doCashReceiptCall(String authToken, String ecomregion, CashReceipt_Request request) {
        return retrofitService.doCashReceipt(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.DUPLICATE_CASH_RECEIPT), authToken, ecomregion, request);
    }

    @Override
    public Single<EdsRescheduleResponse> doEdsRescheduleCall(String authToken, String ecomregion, EdsRescheduleRequest request) {
        return retrofitService.doEdsReschedule(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.RESCHEDULE_STATUS), authToken, ecomregion, request);
    }

    @Override
    public Single<LogFileUploadResponse> doAppLogsUploadApiCall(String ecomregion, Map headers, MultipartBody.Part file) {
        return retrofitService.doAppLogsUploadApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.LOG_FILE_UPLOAD_URL), ecomregion, headers, file);
    }

    @Override
    public Single<UploadImageResponse> doUploadImageApiCall(String ecomregion, Map request, MultipartBody.Part file) {
        return retrofitService.doImageUploadApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.UPLOAD_IMAGE_URL), ecomregion, request, file);
    }

    @Override
    public Single<DisputeImageApiResponse> doUploadDisputeImageApiCall(String ecomregion, Map request, MultipartBody.Part file) {
        return retrofitService.doDisputeImageUploadApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.PAYPHI_PAYMENT), ecomregion, request, file);
    }

    @Override
    public Single<ImageQualityResponse> getImageQuality(String ecomregion, int image_id) {
        return retrofitService.doImageQuality(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.GET_IMAGE_BY_ID_URL), ecomregion, image_id);
    }

    @Override
    public Single<ForwardCallResponse> doForwardCallStatusApiCall(String authToken, String ecomregion, int counter, String emp_code, String awb_number, String drs_id, int shipper_id) throws IOException {
        return retrofitService.doForwardCallStatusApiCall(authToken, ecomregion, SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.SATHI_CALL_BRIDGE_STATUS), counter, emp_code, awb_number, drs_id, shipper_id);
    }

    @Override
    public Single<IncentiveResponse> doFEIncentiveApiCall(String url, String emp_code) throws IOException {
        return retrofitService.doFEIncentiveApiCall(url, emp_code);
    }

    @Override
    public Single<Biometric_response> dobiometricApiCall(String authtoken, String ecomregion, Biometric_requestdata biometric_requestdata) {
        return retrofitService.dobiometricApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.KYC_DEVICE_INFO), authtoken, ecomregion, biometric_requestdata);
    }

    @Override
    public Single<GenerateToken> dogeneratetoken(String token, String ecomregion) {
        //https://sathi2.ecomexpress.in/services/last_mile/v1/get-idfc-token
        //http://13.126.182.187:8446/services/last_mile/v1/get-idfc-token
        return retrofitService.dogeneratetoken(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.GET_IDFC_TOKEN), token, ecomregion);
    }

    @Override
    public Single<IDFCToken_Response> dogeneratetokenIDFC(String url, String ecomregion, String grant_type, String client_assertion_type, String client_id, String scope, String client_assertion) {
        return retrofitService.dogeneratetokenIDFC(url, ecomregion, grant_type, client_assertion_type, client_id, scope, client_assertion);
    }

    public Single<GenerateOTPResponse> doGenerateOTPApiCall(String authToken, String ecomregion, GenerateOTPRequest generateOTPRequest) {
        return retrofitService.doGenerateOTPApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.SEND_INSTANT_OTP), authToken, ecomregion, generateOTPRequest);
    }

    public Single<GenerateRTSOTPResponse> doGenerateRTSOTPApiCall(String authToken, String ecomregion, GenerateRTSOTPRequest generateOTPRequest) {
        return retrofitService.doGenerateRTSOTPApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.RTS_OTP_SEND_GENERATE), authToken, ecomregion, generateOTPRequest);
    }

    @Override
    public Single<Hospital> doHospitalList(String authToken, String ecomregion, String emp_code) {
        return retrofitService.covidHospitalList(authToken, ecomregion, emp_code);
    }


    @Override
    public Single<VerifyOTPResponse> doVerifyOTPApiCall(String authToken, String ecomregion, VerifyOTPRequest verifyOTPRequest) {
        return retrofitService.doVerifyOTPApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.VERIFY_INSTANT_OTP), authToken, ecomregion, verifyOTPRequest);
    }

    @Override
    public Single<VerifyOTPResponse> doVerifyUDOtpDRSApiCall(String authToken, String ecomregion, VerifyUDOtpRequest verifyUDOtpRequest) {
        return retrofitService.doVerifyUDOtpDRSApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.VERIFY_INSTANT_OTP), authToken, ecomregion, verifyUDOtpRequest);
    }

    @Override
    public Single<VerifyOTPResponse> doVerifyUdOTPApiCall(String authToken, String ecomregion, VerifyOTPRequest verifyOTPRequest) {
        return retrofitService.doVerifyOTPApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.VERIFY_INSTANT_OTP), authToken, ecomregion, verifyOTPRequest);
    }

    @Override
    public Single<PrintReceiptResponse> doPrintReceiptDataCall(String authToken, String ecomregion, PrintReceiptRequest printReceiptRequest) {
        return retrofitService.doPrintReceiptDataCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.BANK_RECEIPT_DETAIL), authToken, ecomregion, printReceiptRequest);
    }

    @Override
    public Single<PrintReceiptUploadResponse> doPrintReceiptUploadImage(String authToken, String ecomregion, Map<String, String> headers, Map<String, RequestBody> multipartBody, MultipartBody.Part file) {
        return retrofitService.doPrintReceiptUploadImage(authToken, ecomregion, SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.BANK_RECEIPT_IMAGE), headers, multipartBody, file);
    }

    @Override
    public Single<EncryptContactResponse> doencryptcontact(String token, String ecomregion, long awb) {
        return retrofitService.doencryptcontact(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.GET_CONSIGNEE_NUMBER) + "?awb_number=" + awb, token, ecomregion);
    }

    @Override
    public Single<WadhResponse> doGetWadhValueAntWork(String basic, String ecomregion, String token_url, WathRequest wathRequest) {
        return retrofitService.doGetWadhValueAntWork(basic, ecomregion, token_url, wathRequest);
    }

    @Override
    public Single<DPDailyEarnedAmount> doDPDailyEarningApiCall(String token, String ecomregion, String emp_code) {
        return retrofitService.doDPDailyEarningApiCall(token, ecomregion, emp_code);
    }

    @Override
    public Single<VerifyOTPResponse> doDPVerifyOTPApiCall(String authToken, String ecomregion, VerifyOTPRequest verifyOTPRequest) {
        return retrofitService.doDPVerifyApi(authToken, ecomregion, verifyOTPRequest);
    }

    @Override
    public Single<DailyEarningResponse> doDailyEarnigCalander(String authToken, String ecomregion, DailyEarningRequest dailyEarningRequest) {
        return retrofitService.doDailyEarnigCalender(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.ADM_DAILY_EARNING), authToken, ecomregion, dailyEarningRequest);
    }

    @Override
    public Single<PayoutResponse> doPayoutApi(String authToken, String ecomregion, PayoutRequest payoutRequest) {
        return retrofitService.doPayoutApi(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.ADM_EMP_PERFORMANCE), authToken, ecomregion, payoutRequest);
    }

    @Override
    public Single<DPReferenceCodeResponse> doDPReferecenApiCall(String authToken, String ecomregion, DPReferenceCodeRequest dpReferenceCodeRequest) {
        return retrofitService.doDPReferenceVerifyAPI(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.VERIFY_REFERNCE_CODE), authToken, ecomregion, dpReferenceCodeRequest);
    }

    @Override
    public Single<ReshceduleDetailsResponse> doReshceduleDetails(String authToken, String ecomregion, ResheduleDetailsRequest resheduleDetailsRequest) {
        return retrofitService.doReshceduleDetailsAPI(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.shipment_reschedule_details), authToken, ecomregion, resheduleDetailsRequest);
    }

    @Override
    public Single<GenerateTokenNiyo> dogenerateniyotoken() {
        //https://sathi2.ecomexpress.in/services/last_mile/v1/get-idfc-token
        //http://13.126.182.187:8446/services/last_mile/v1/get-idfc-token
        return retrofitService.dogenerateniyotoken("https://idfc-gateway.goniyo.com/v1/keys/idfc-gateway.goniyo.com");
        //"https://sathi2.ecomexpress.in/services/last_mile/v1/get-idfc-token"
    }

    @Override
    public Single<RvpFlyerDuplicateCheckResponse> doRvpflyerDuplicateCheck(String authToken, String ecomregion, RvpFlyerDuplicateCheckRequest resheduleDetailsRequest) {
        return retrofitService.doRvpflyerDuplicateCheck(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.rvpflyer_duplicate_check), authToken, ecomregion, resheduleDetailsRequest);
    }

    @Override
    public Single<List<ADMDATA>> getAdmData(String authToken, String ecomregion, String emp_code) {
        return retrofitService.getAdmData(authToken, ecomregion, emp_code);
    }

    @Override
    public Single<ADMUpdateResponse> doUpdateADMData(String authToken, String ecomregion, List<ADMUpdateRequest> admUpdateRequests) {
        return retrofitService.doUpdateADMData(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.update_dp_avalibility_details), authToken, ecomregion, admUpdateRequests);
    }

    @Override
    public Single<ShipmentWeightResponse> doShipmentWeightApiCall(String authToken, String ecomregion, Map<String, String> headers, Map<String, RequestBody> multipartBody, MultipartBody.Part file) {
        return retrofitService.doShipmentWeightApiCall(authToken, ecomregion, SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.shipment_weight_update), headers, multipartBody, file);
    }

    @Override
    public Single<CholaResponse> doCholaURLAPI(String authToken, String ecomregion, CholaRequest cholaRequest) {
        return retrofitService.doCholaURLAPI(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.unify_apps), authToken, ecomregion, cholaRequest);
    }

    @Override
    public Single<RTSResendOTPResponse> doResendOtpApiCall(String authToken, String ecomregion, RTSResendOTPRequest resendOtpRequest) {
        return retrofitService.doRTSResendApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.rts_resend_otp), authToken, ecomregion, resendOtpRequest);
    }

    @Override
    public Single<RTSResendOTPResponse> doResendOtpApiOtherMobileRTSCall(String authToken, String ecomregion, RTSResendOTPRequest resendOtpRequest) {
        return retrofitService.doRTSResendApiOtherMobileRTSCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.rts_resend_Other_mobile_otp), authToken, ecomregion, resendOtpRequest);
    }

    @Override
    public Single<RTSVerifyOTPResponse> doVerifyOTPApiCall(String authToken, String ecomregion, RTSVerifyOTPRequest rtsVerifyOTPRequest) {
        return retrofitService.doRTSVerifyApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.rts_verify_otp), authToken, ecomregion, rtsVerifyOTPRequest);
    }

    @Override
    public Single<CardDetectionResponse> doCardDetectionResponse(String authToken, String ecomregion, Map<String, String> headers, Map<String, RequestBody> multipartBody, MultipartBody.Part file) {
        return retrofitService.doCardDetectionApiCall(authToken, ecomregion, "https://test.ecomexpress.in:8060/upload/", headers, file);
    }

    @Override
    public Single<BioMatricResponse> doGetBioMatricData(String basic, String ecomregion, String token_url, BiomatricRequest biomatricRequest) {
        return retrofitService.doGetBioMatricAntWork(basic, ecomregion, token_url, biomatricRequest);
    }

    @Override
    public Single<FwdReassignReponse> doFwdReassingApiCall(String authToken, FWDReassignRequest request) {
        return retrofitService.doFwdReassingApiCall(authToken, SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.RE_ASSIGN_AWB), request);
    }


    @Override
    public Single<MarkAttendanceResponse> doMarkAttendanceApiCall(String authToken, MarkAttendanceRequest request) {
        return retrofitService.doMarkAttendanceApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.MARK_ATTENDANCE), authToken, request);
    }


    @Override
    public Single<CheckAttendanceResponse> doCheckAttendanceApiCall(String authToken, String empCode) {
        return retrofitService.doCheckAttendanceApiCall(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.CHECK_ATTENDANCE), authToken, empCode);

    }

    @Override
    public Single<SelfieImageResponse> doSelfieImageUploadApiCall(String authToken, Map headers, Map multipartBody, MultipartBody.Part file) {
        RequestBody selfie_bitmap = (RequestBody) multipartBody.get("selfie_bitmap");
        RequestBody emp_code = (RequestBody) multipartBody.get("emp_code");
        RequestBody source_code = (RequestBody) multipartBody.get("source_code");
        RequestBody dc_code = (RequestBody) multipartBody.get("dc_code");

        return retrofitService.doSelfieImageUploadApiCall(authToken, SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.SELFIE_IMAGE_UPLOAD), headers, file, emp_code, source_code, dc_code);
    }

    @Override
    public LiveData<EarningApiResponse> doTrainingAPICall(String authToken, EarningApiRequest earningApiRequest) {
        MutableLiveData<EarningApiResponse> earningApiResponseMutableLiveData = new MutableLiveData<>();
        retrofitService.doTrainingAPICall("https://ecom-esp.matrix-prod-aps1.unifyapps.com/auth/createUserExternalLoginSession", authToken, earningApiRequest).enqueue(new Callback<EarningApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<EarningApiResponse> call, @NonNull Response<EarningApiResponse> response) {
                if (response.code() == 200) {
                    earningApiResponseMutableLiveData.setValue(response.body());
                } else {
                    EarningApiResponse earningApiResponse = new EarningApiResponse();
                    earningApiResponseMutableLiveData.setValue(earningApiResponse);
                }
            }

            @Override
            public void onFailure(Call<EarningApiResponse> call, Throwable t) {
                EarningApiResponse ob = new EarningApiResponse();
                earningApiResponseMutableLiveData.setValue(ob);
            }
        });
        return earningApiResponseMutableLiveData;
    }

    @Override

    public Single<CholaResponse> doWebViewAPI(String authToken, String ecomregion, CholaRequest cholaRequest) {
        return retrofitService.doCholaURLAPI(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.unify_apps_lms), authToken, ecomregion, cholaRequest);
    }

    @Override
    public Single<CholaResponse> doCampaignAPI(String authToken, String ecomregion, CholaRequest cholaRequest) {
        return retrofitService.doCholaURLAPI(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.unify_apps_campaign), authToken, ecomregion, cholaRequest);

    }

    @Override
    public LiveData<DistanceApiResponse> distanceCalculationApi(String location, String annotations) {
        MutableLiveData<DistanceApiResponse> distanceCalculationApiResponseMutableLiveData = new MutableLiveData<>();
        retrofitService.distanceCalculationApi(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.OSRM_TRACKING)+location,annotations).enqueue(new Callback<DistanceApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<DistanceApiResponse> call, @NonNull Response<DistanceApiResponse> response) {
                Log.d( "onResponse: ",response.body().toString());
                if (response.code() == 200) {
                    Log.d( "onResponseSuccess: ",response.body().toString());
                    distanceCalculationApiResponseMutableLiveData.setValue(response.body());
                } else {
                    DistanceApiResponse distanceCalculationApiResponse = new DistanceApiResponse();
                    Log.d( "onResponseError1: ",response.body().toString());
                    distanceCalculationApiResponseMutableLiveData.setValue(distanceCalculationApiResponse);
                }
            }

            @Override
            public void onFailure(Call<DistanceApiResponse> call, Throwable t) {
                Log.d( "onResponseError: ",t.getLocalizedMessage().toString());
                DistanceApiResponse failureObject = new DistanceApiResponse();
                distanceCalculationApiResponseMutableLiveData.setValue(failureObject);
            }
        });
        return distanceCalculationApiResponseMutableLiveData;
    }

    @Override
    public Single<DistanceApiResponse> distanceCalculationApis(String location, String annotations) {
        return retrofitService.distanceCalculationApis(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.OSRM_TRACKING)+location,annotations);
    }
}