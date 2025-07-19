package in.ecomexpress.sathi.repo.local.storage;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
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
import in.ecomexpress.sathi.repo.remote.ByteImageRequest;
import in.ecomexpress.sathi.repo.remote.IRestApiHelper;
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
import in.ecomexpress.sathi.utils.CommonUtils;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

@Singleton
public class StorageHelper implements IRestApiHelper {
    Context mContext;

    @Inject
    public StorageHelper(Context context) {
        this.mContext = context;
    }

    @Override
    public Single<LoginResponse> doLoginApiCall(LoginRequest request) throws IOException {

        String strbuffer = CommonUtils.loadJSONFromAsset(mContext, "login_response.json");
        LoginResponse loginResponse = new ObjectMapper().readValue(strbuffer, LoginResponse.class);
        return Single.just(loginResponse);
    }


    @Override
    public Single<ConsigneeProfileResponse> doConsigneeProfileApiCall(String token, String ecomregion, ConsigneeProfileRequest consigneeProfileRequest) throws IOException {
        try {
            ConsigneeProfileResponse attendanceResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "consignee_profile.json"), ConsigneeProfileResponse.class);
            return Single.just(attendanceResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<LogoutResponse> doLogoutApiCall(String token, String ecomregion, LogoutRequest request) throws IOException {
        return null;
    }

    @Override
    public Single<AttendanceResponse> doAttendanceApiCall(String authToken, String ecomregion, AttendanceRequest request) {
        try {
            AttendanceResponse attendanceResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "attendance_response.json"), AttendanceResponse.class);
            return Single.just(attendanceResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<FwdReattemptResponse> doUdFwdReattemptApiCall(String authToken, String ecomregion, FwdReattemptRequest request) {
        return null;
    }

    @Override
    public Single<PerformanceResponse> doPerformanceApiCall(String authToken, String ecomregion, PerformanceRequest request) {
        // String htmlFile="<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\"><title>Ecom Express</title><base href=\"/\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" /><link rel=\"stylesheet\"\thref=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\"><script\tsrc=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script><script\tsrc=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script></head><body>\t<div class=\"container\">\t\t<h4 style=\"color: red; text-decoration: underline;\">Performance</h4>\t\t<table class=\"table table-bordered table-striped\">\t\t\t<thead style=\"background-color: #FFC2B3\">\t\t\t\t\t\t<th style=\"font-size:13px;font-weight:700;vertical-align:middle;text-align: center;\">Product</th><th style=\"font-size:13px;font-weight:700;vertical-align:middle;text-align: center;\">Yesterday</th><th style=\"font-size:13px;font-weight:700;vertical-align:middle;text-align: center;\">Last_7Days</th><th style=\"font-size:13px;font-weight:700;vertical-align:middle;text-align: center;\">This_Season</th>\t\t\t</thead>\t\t\t<tbody>\t\t\t<tr><td style=\"font-weight:bold;font-size:11px;text-align: center;\">Outscanned</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td></tr><tr><td style=\"font-weight:bold;font-size:11px;text-align: center;\">Successful %</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td></tr><tr><td style=\"font-weight:bold;font-size:11px;text-align: center;\">DC Rank</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td></tr><tr><td style=\"font-weight:bold;font-size:11px;text-align: center;\">State Rank</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td></tr><tr><td style=\"font-weight:bold;font-size:11px;text-align: center;\">Country Rank</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td></tr>            </tbody>\t\t</table>\t\t<h4 style=\"color:red;text-decoration: underline;\">NPS</h4>\t\t<table class=\"table table-bordered table-striped\">\t\t<thead style=\"background-color: #FFC2B3\">\t\t\t<th style=\"font-size:13px;font-weight:700;vertical-align:middle;text-align: center;\">Product</th><th style=\"font-size:13px;font-weight:700;vertical-align:middle;text-align: center;\">Yesterday</th><th style=\"font-size:13px;font-weight:700;vertical-align:middle;text-align: center;\">Last_7Days</th><th style=\"font-size:13px;font-weight:700;vertical-align:middle;text-align: center;\">This_Season</th>\t\t\t</thead>\t\t\t<tbody>\t\t\t<tr><td style=\"font-weight:bold;font-size:11px;text-align: center;\">Outscanned</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td></tr><tr><td style=\"font-weight:bold;font-size:11px;text-align: center;\">Successful %</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td></tr><tr><td style=\"font-weight:bold;font-size:11px;text-align: center;\">DC Rank</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td></tr><tr><td style=\"font-weight:bold;font-size:11px;text-align: center;\">State Rank</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td></tr>\t\t\t</tbody>\t\t</table>\t</div></body></html>";
        return null;
    }

    @Override
    public Single<ForgotPasswordResponse> doForgetPasswordApiCall(String ecomregion, ForgetPasswordUserRequest forgetPasswordUserRequest) {
        try {
            ForgotPasswordResponse forgotPasswordResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "forgot_password_response.json"), ForgotPasswordResponse.class);
            return Single.just(forgotPasswordResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<ForgotPasswordResponse> doOTPVerifyWithPasswordApiCall(String ecomregion, OTPVerifyWithPasswordRequest otpVerifyWithPasswordRequest) {
        try {
            ForgotPasswordResponse forgotPasswordResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "forgot_password_response.json"), ForgotPasswordResponse.class);
            return Single.just(forgotPasswordResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<ForgotPasswordResponse> doResetPasswordApiCall(String token, String ecomregion, ChangePasswordRequest changePasswordRequest) {
        try {
            ForgotPasswordResponse loginResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "forgot_password_response.json"), ForgotPasswordResponse.class);
            return Single.just(loginResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<LastMileDRSListResponse> doDRSListApiCall(String authToken, String ecomregion, CommonUserIdRequest commonUserIdRequest) {
        try {
            LastMileDRSListResponse lastMileDRSListResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "lastMile_DRS_List_java_format.json"), LastMileDRSListResponse.class);
            return Single.just(lastMileDRSListResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<DRSResponse> doDRSListApiNewCall(String authToken, String ecomregion, CommonUserIdRequest commonUserIdRequest) {
        try {
            DRSResponse lastMileDRSListNewResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "lastMile_DRS_List_java_format.json"), DRSResponse.class);
            return Single.just(lastMileDRSListNewResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*Call Bridge Api*/
    @Override
    public Single<ErrorResponse> doCallBridgeApiCall(String authToken, String ecomregion, CallApiRequest callApiRequest) {
        try {
            ErrorResponse callApiResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "callbridge_response.json"), ErrorResponse.class);
            return Single.just(callApiResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<DRSResponse> doSMSApiCall(String authToken, String ecomregion, SmsRequest smsRequest) {
        try {
            DRSResponse callApiResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "lastMile_DRS_List_java_format.json"), DRSResponse.class);
            return Single.just(callApiResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<MasterDataReasonCodeResponse> doMasterApiCall(String authToken, String ecomregion, CommonUserIdRequest commonUserIdRequest) {
        try {
            MasterDataReasonCodeResponse masterDataReasonCodeResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "master_data_response.json"), MasterDataReasonCodeResponse.class);
            return Single.just(masterDataReasonCodeResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<LoginVerifyOtpResponse> doLoginVerifyOtpApiCall(String authToken, String ecomregion, LoginVerifyOtpRequest loginVerifyOtpRequest) {
        return null;
    }

    @Override
    public Single<DisputeApiResponse> doRaiseDisputeApi(String authToken, String ecomregion, DisputeRequest disputeRequest) {
        return null;
    }

    @Override
    public Single<VerifyDisputeApiResponse> doVerifyDisputeApi(String authToken, String ecomregion, VerifyDisputeRequest verifyDisputeRequest) {
        return null;
    }

    @Override
    public Single<AadharStatusResponse> doGetStatusAadharMasking(String authToken, String ecomregion, Get_Status_Masking get_status_masking) {
        return null;
    }

    @Override
    public Single<VerifyOtpResponse> doVerifyOtpApiCall(String authToken, String ecomregion, VerifyOtpRequest verifyOtpRequest) {
        try {
            VerifyOtpResponse verifyOtpResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "verifyOtpResponse.json"), VerifyOtpResponse.class);
            return Single.just(verifyOtpResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<VerifyOTPResponse> doVerifyUDOtpDRSApiCall(String authToken, String ecomregion, VerifyUDOtpRequest verifyUDOtpRequest) {

        return null;
    }

    @Override
    public Single<VoiceOTPResponse> doVoiceOtpApiCall(String authToken, String ecomregion, VoiceOTP voiceOTP) {
        return null;
    }

    @Override
    public Single<AmazonOtpResponse> doAmazonOtpApiCall(String authToken, String ecomregion, AmazonOtpRequest amazonOtpRequest) {
        try {
            AmazonOtpResponse verifyOtpResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "verifyOtpResponse.json"), AmazonOtpResponse.class);
            return Single.just(verifyOtpResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<OtherNoResponse> doResendOtpToOtherNoApiCall(String authToken, String ecomregion, OtherNoRequest otherNoRequest) {
        try {
            OtherNoResponse verifyOtpResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "verifyOtpResponse.json"), OtherNoResponse.class);
            return Single.just(verifyOtpResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<ResendOtpResponse> doResendOtpApiCall(String authToken, String ecomregion, ResendOtpRequest resendOtpRequest) {
        try {
            ResendOtpResponse resendOtpResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "resendotpresponse.json"), ResendOtpResponse.class);
            return Single.just(resendOtpResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<ResendOtpResponse> doGenerateUDOtpApiCall(String authToken, String ecomregion, GenerateUDOtpRequest generateUDOtpRequest) {
        try {
            ResendOtpResponse resendOtpResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "resendotpresponse.json"), ResendOtpResponse.class);
            return Single.just(resendOtpResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<ResendOtpResponse> doResendUdOtpApiCall(String authToken, String ecomregion, ResendOtpRequest resendOtpRequest) {
        try {
            ResendOtpResponse resendOtpResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "resendotpresponse.json"), ResendOtpResponse.class);
            return Single.just(resendOtpResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public Single<AwbResponse> doAwbRegisterApiCall(String authtoken, String ecomregion, AwbRequest awbRequest) {
        try {
            AwbResponse awbResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "payphi_awb_response.json"), AwbResponse.class);
            return Single.just(awbResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<AwbResponse> doAwbRegisterApiCallForSms(String token, String ecomregion, AwbRequest awbRequest) {
        return null;
    }

    @Override
    public Single<SmsLinkResponse> doSentPayphiPaymentLinkSms(String token, String ecomregion, PaymentSmsLinkRequset awbRequest) {

        return null;
    }

    @Override
    public Single<AwbResponse> doCheckStatusApiCall(String authtoken, String ecomregion, AwbRequest awbRequest) {
        try {
            AwbResponse checkStatusResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "payphi_awb_response.json"), AwbResponse.class);
            return Single.just(checkStatusResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*Forward Commit*/
    @Override
    public Single<ForwardCommitResponse> doFWDCommitApiCall(String auth_token, String ecomregion, HashMap hashMap, List<ForwardCommit> forwardCommit) {
        try {
            ForwardCommitResponse forwardCommitResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "forward_commit_response.json"), ForwardCommitResponse.class);
            forwardCommitResponse.getResponse().setAwb_no(forwardCommit.get(0).getAwb());
            return Single.just(forwardCommitResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*Forward Undelivered Commit*/
    @Override
    public Single<ForwardCommitResponse> doFWDUndeliveredCommitApiCall(String auth_token, String ecomregion, HashMap hashMap, ForwardCommit forwardCommit) {
        try {
            ForwardCommitResponse forwardCommitResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "forward_commit_response.json"), ForwardCommitResponse.class);
            forwardCommitResponse.getResponse().setAwb_no(forwardCommit.getAwb());
            return Single.just(forwardCommitResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<RVPCommitResponse> doRVPUndeliveredCommitApiCall(String auth_token, String ecomregion, HashMap hashMap, RvpCommit rvpCommit) {
        try {
            RVPCommitResponse rvpCommitResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "rvp_error_response.json"), RVPCommitResponse.class);
            rvpCommitResponse.getResponse().setAwb_no(rvpCommit.getAwb());
            return Single.just(rvpCommitResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<RTSCommitResponse> doRTSCommitApiCall(String auth_token, String ecomregion, HashMap hashMap, RTSCommit rvpCommit) {
        try {
            RTSCommitResponse rtsCommitResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "rvp_error_response.json"), RTSCommitResponse.class);
            rtsCommitResponse.getResponse().setAwb_no(rtsCommitResponse.getResponse().getAwb_no());
            return Single.just(rtsCommitResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<EDSCommitResponse> doEDSCommitApiCall(String authToken, String ecomregion, HashMap hashMap, EdsCommit edsCommit) {
        try {
            EDSCommitResponse edsCommitResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "rvp_error_response.json"), EDSCommitResponse.class);
            edsCommitResponse.getResponse().setAwb_no(edsCommitResponse.getResponse().getAwb_no());
            return Single.just(edsCommitResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<MasterDataConfig> doMasterReasonApiCall(String token, String ecomregion, masterRequest masterrequest) {
        try {
            MasterDataConfig masterDataReasonCodeResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "master_data_response.json"), MasterDataConfig.class);
            return Single.just(masterDataReasonCodeResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //fuel
    @Override
    public Single<FuelReimbursementResponse> doFuelListApiCall(String token, String ecomregion, FuelReimbursementRequest fuelReimbursementRequest) {
        try {
            FuelReimbursementResponse fuelReimbursementResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "fuel.json"), FuelReimbursementResponse.class);
            return Single.just(fuelReimbursementResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<ImageUploadResponse> doImageUploadApiCall(String auth_token, String ecomregion, String imageType, Map headers, Map multipartBody, MultipartBody.Part file) {
        try {
            ImageUploadResponse imageUploadResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "image_upload_response.json"), ImageUploadResponse.class);
            imageUploadResponse.setFileName(multipartBody.get("image_name").toString());

            return Single.just(imageUploadResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<AadharMaskingResponse> doAadharMaskigImageUploadApiCall(String authToken, String ecomregion, String imageType, Map headers, Map multipartBody, MultipartBody.Part front_file, MultipartBody.Part rear_file) {
        return null;
    }

    @Override
    public ImageUploadResponse doImageUploadApiCallInSingleThread(String authToken, String ecomregion, String imageType, Map headers, Map multipartBody, MultipartBody.Part file) {
        try {
            ImageUploadResponse imageUploadResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "image_upload_response.json"), ImageUploadResponse.class);
            imageUploadResponse.setFileName(multipartBody.get("image_name").toString());

            return imageUploadResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<ImageUploadResponse> doImageUploadApiCallStartStop(String authToken, String ecomregion, Map headers, Map multipartBody, MultipartBody.Part file) {
        try {
            ImageUploadResponse imageUploadResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "startTripImageResponse.json"), ImageUploadResponse.class);
            imageUploadResponse.setFileName(multipartBody.get("image_name").toString());

            return Single.just(imageUploadResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<ImageUploadResponse> doImageUploadApiCallTest(String auth_token, String ecomregion, Map multipartBody) {
        return null;
    }

    @Override
    public Single<Call<ResponseBody>> getImage(String authToken, String ecomregion, String url) {
        return null;
    }

    @Override
    public Single<GMapResponse> getGMap(String authToken, String ecomregion, GMapRequest gMapRequest) {
        try {
            GMapResponse gMapResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "gmapresponse.json"), GMapResponse.class);
            return Single.just(gMapResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Observable<Response<ResponseBody>> getFile(String authToken, String ecomregion, String url) {
        return null;
    }

    @Override
    public Single<LoginVerifyOtpResponse> doLoginResendOtpApiCall(String authToken, String ecomregion, LoginVerifyOtpRequest loginVerifyOtpRequest) {
        return null;
    }

    @Override
    public Single<LiveTrackingResponse> dogetLiveTrackingIDApiCall(String authToken, String ecomregion, LiveTrackingRequest liveTrackingRequest) {
        return null;
    }

    @Override
    public Single<LiveTrackingLogRespone> dosendLiveTrackingLog(String authToken, String ecomregion, List<LiveTrackingLogTable> liveTrackingLog) {
        return null;
    }

    @Override
    public Single<SOSResponse> doSOSApiCall(String authToken, String ecomregion, SOSRequest sosRequest) {
        try {
            SOSResponse sosResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "sosresponse.json"), SOSResponse.class);
            return Single.just(sosResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<LogFileUploadResponse> doAppLogsUploadApiCall(String ecomregion, Map headers, MultipartBody.Part file) {
        return null;
    }

    @Override
    public Single<UploadImageResponse> doUploadImageApiCall(String ecomregion, Map request, MultipartBody.Part file) throws IOException {
        try {
            UploadImageResponse uploadImageResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "sosresponse.json"), UploadImageResponse.class);
            return Single.just(uploadImageResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<DisputeImageApiResponse> doUploadDisputeImageApiCall(String ecomregion, Map request, MultipartBody.Part file) throws IOException {
        return null;
    }

    @Override
    public Single<ImageQualityResponse> getImageQuality(String ecomregion, int image_id) throws IOException {
        try {
            ImageQualityResponse imageQualityResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "sosresponse.json"), ImageQualityResponse.class);
            return Single.just(imageQualityResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<ForwardCallResponse> doForwardCallStatusApiCall(String auth_token, String ecomregion, int counter, String emp_code, String awb_number, String drs_id, int shipper_id) throws IOException {
        return null;
    }

    @Override
    public Single<IncentiveResponse> doFEIncentiveApiCall(String url, String emp_code) throws IOException {
        return null;
    }



   /* @Override
    public Single<ImageUploadResponse> doAppLogsUploadApiCall(String authToken, Map headers, MultipartBody.Part file) {
        return null;
    }*/


   /* @Override
    public Single<MasterDataReasonCodeResponse> doMasterReasonApiCall() {
        try {
            MasterDataReasonCodeResponse masterDataReasonCodeResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "master_data_response.json"), MasterDataReasonCodeResponse.class);
            return Single.just(masterDataReasonCodeResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
*/

    @Override
    public Single<StartTripResponse> doStartTripApiCall(String token, String ecomregion, StartTripRequest startTripRequest) {
        try {
            StartTripResponse startResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "startTripResponse.json"), StartTripResponse.class);
            return Single.just(startResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<StopTripResponse> doStopTripApiCall(String token, String ecomregion, StopTripRequest stopTripRequest) {
        try {
            StopTripResponse stopResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "stopTripResponse.json"), StopTripResponse.class);
            return Single.just(stopResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<IciciResponse> doICICIApiCall(String token, String ecomregion, String url, RequestBody pid) {
        try {
            IciciResponse iciciResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "icici.json"), IciciResponse.class);
            return Single.just(iciciResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<IciciResponse> doICICICheckcStatusCall(String token, String ecomregion, String url, RequestBody pid) {
        try {
            IciciResponse iciciResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "icici.json"), IciciResponse.class);
            return Single.just(iciciResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<IciciResponse_standard> doICICICheckcStatusCallStandard(String apikey, String ecomregion, String url, RequestBody urn) {
        return null;
    }

    @Override
    public Single<ImageUploadResponse> doImageByteUploadApiCall(String authToken, String ecomregion, String imageType, Map headers, ByteImageRequest data) {
        return null;
    }

    @Override
    public Single<Biometric_response> dobiometricApiCall(String token, String ecomregion, Biometric_requestdata biometric_requestdata) {
        return null;
    }

    @Override
    public Single<ekycXMLResponse> doEkycXMLApiCall(String ecomregion, String jobj, HashMap<String, String> webheader) {
        try {
            ekycXMLResponse ekycXMLResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "stopTripResponse.json"), ekycXMLResponse.class);
            return Single.just(ekycXMLResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<DrsCheckListResponse> doDrsListCheck(String authToken, String ecomregion, DrsCheckListRequest drsCheckListRequest) {
        try {
            DrsCheckListResponse drsCheckListResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "drs_check_list_response.json"), DrsCheckListResponse.class);
            return Single.just(drsCheckListResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<AdharApiResponse> doAdharApi(String authToken, String ecomregion, AdharRequest adharRequest) {
        return null;
    }

    @Override
    public Single<CovidApiResponse> doCovidApi(String authToken, String ecomregion, CovidRequest covidRequest) {
        return null;
    }


    @Override
    public Single<DCLocationUpdateResponse> doDCLocationUpdateApiCall(String authToken, String ecomregion, DCLocationUpdate dcLocationUpdate) {
        return null;
    }

    @Override
    public Single<Amazon_reschedule_list> doScheduleDates(String authToken, String ecomregion, AmazonScheduleRequest strings) {
        return null;
    }

    @Override
    public Single<CashReceipt_Response> doCashReceiptCall(String authToken, String ecomregion, CashReceipt_Request request) {
        return null;
    }

    @Override
    public Single<EdsRescheduleResponse> doEdsRescheduleCall(String authToken, String ecomregion, EdsRescheduleRequest request) {
        return null;
    }

    //    @Override
//    public Single<ContatDecryption> doconsigneeEncryptionCall(String token) {
//        try {
//            ContatDecryption contatDecryption = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "icici.json"), ContatDecryption.class);
//            return Single.just(contatDecryption);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    // return null;
    //}
    @Override
    public Single<EncryptContactResponse> doencryptcontact(String token, String ecomregion, long awb) {
        return null;
    }

    @Override
    public Single<WadhResponse> doGetWadhValueAntWork(String basic, String ecomregion, String token_url, WathRequest wathRequest) {
        return null;
    }

    @Override
    public Single<DPDailyEarnedAmount> doDPDailyEarningApiCall(String token, String ecomregion, String emp_code) {
        return null;
    }

    @Override
    public Single<VerifyOTPResponse> doDPVerifyOTPApiCall(String authToken, String ecomregion, VerifyOTPRequest verifyOTPRequest) {
        return null;
    }

    @Override
    public Single<DailyEarningResponse> doDailyEarnigCalander(String authToken, String ecomregion, DailyEarningRequest dailyEarningRequest) {
        return null;
    }

    @Override
    public Single<PayoutResponse> doPayoutApi(String authToken, String ecomregion, PayoutRequest payoutRequest) {
        return null;
    }

    @Override
    public Single<DPReferenceCodeResponse> doDPReferecenApiCall(String authToken, String ecomregion, DPReferenceCodeRequest dpReferenceCodeRequest) {
        return null;
    }

    @Override
    public Single<ReshceduleDetailsResponse> doReshceduleDetails(String authToken, String ecomregion, ResheduleDetailsRequest resheduleDetailsRequest) {
        return null;
    }

    @Override
    public Single<RvpFlyerDuplicateCheckResponse> doRvpflyerDuplicateCheck(String authToken, String ecomregion, RvpFlyerDuplicateCheckRequest resheduleDetailsRequest) {
        return null;
    }

    @Override
    public Single<List<ADMDATA>> getAdmData(String authToken, String ecomregion, String emp_code) {
        return null;
    }

    @Override
    public Single<ADMUpdateResponse> doUpdateADMData(String authToken, String ecomregion, List<ADMUpdateRequest> admUpdateRequests) {
        return null;
    }

    @Override
    public Single<ShipmentWeightResponse> doShipmentWeightApiCall(String authToken, String ecomregion, Map<String, String> headers, Map<String, RequestBody> multipartBody, MultipartBody.Part file) {
        return null;
    }

    @Override
    public Single<CholaResponse> doCholaURLAPI(String authToken, String ecomregion, CholaRequest cholaRequest) {
        return null;
    }

    @Override
    public Single<RTSResendOTPResponse> doResendOtpApiCall(String authToken, String ecomregion, RTSResendOTPRequest resendOtpRequest) {
        return null;
    }

    @Override
    public Single<RTSResendOTPResponse> doResendOtpApiOtherMobileRTSCall(String authToken, String ecomregion, RTSResendOTPRequest resendOtpRequest) {
        return null;
    }

    @Override
    public Single<RTSVerifyOTPResponse> doVerifyOTPApiCall(String authToken, String ecomregion, RTSVerifyOTPRequest rtsVerifyOTPRequest) {
        return null;
    }

    @Override
    public Single<CardDetectionResponse> doCardDetectionResponse(String authToken, String ecomregion, Map<String, String> headers, Map<String, RequestBody> multipartBody, MultipartBody.Part file) {
        return null;
    }

    @Override
    public Single<BioMatricResponse> doGetBioMatricData(String basic, String ecomregion, String token_url, BiomatricRequest biomatricRequest) {
        return null;
    }

    @Override
    public Single<FwdReassignReponse> doFwdReassingApiCall(String authToken, FWDReassignRequest fwdReassignRequest) {
        return null;
    }

    @Override
    public LiveData<EarningApiResponse> doTrainingAPICall(String authToken, EarningApiRequest earningApiRequest) {
        return null;
    }


    @Override
    public Single<GenerateToken> dogeneratetoken(String token, String ecomregion) {
        return null;
    }

    @Override
    public Single<IDFCToken_Response> dogeneratetokenIDFC(String url, String ecomregion, String grant_type, String client_assertion_type, String client_id, String scope, String client_assertion) {
        return null;
    }

    @Override
    public Single<PrintReceiptResponse> doPrintReceiptDataCall(String authToken, String ecomregion, PrintReceiptRequest printReceiptRequest) {
        return null;
    }

    @Override
    public Single<GenerateOTPResponse> doGenerateOTPApiCall(String authToken, String ecomregion, GenerateOTPRequest generateOTPRequest) {
        return null;
    }

    @Override
    public Single<GenerateRTSOTPResponse> doGenerateRTSOTPApiCall(String authToken, String ecomregion, GenerateRTSOTPRequest generateOTPRequest) {
        return null;
    }

    @Override
    public Single<Hospital> doHospitalList(String authToken, String ecomregion, String emp_code) {
        return null;
    }

    @Override
    public Single<GenerateTokenNiyo> dogenerateniyotoken() {
        return null;
    }

    @Override
    public Single<VerifyOTPResponse> doVerifyOTPApiCall(String authToken, String ecomregion, VerifyOTPRequest verifyOTPRequest) {
        return null;
    }

    @Override
    public Single<VerifyOTPResponse> doVerifyUdOTPApiCall(String authToken, String ecomregion, VerifyOTPRequest verifyOTPRequest) {
        return null;
    }

    @Override
    public Single<PrintReceiptUploadResponse> doPrintReceiptUploadImage(String authToken, String ecomregion, Map headers, Map multipartBody, MultipartBody.Part file) {
        return null;
    }

    @Override
    public Single<MarkAttendanceResponse> doMarkAttendanceApiCall(String authToken, MarkAttendanceRequest request) {
        return null;
    }

    @Override
    public Single<CheckAttendanceResponse> doCheckAttendanceApiCall(String authToken, String empCode) {
        return null;
    }

    @Override
    public Single<SelfieImageResponse> doSelfieImageUploadApiCall(String authToken, Map headers, Map multipartBody, MultipartBody.Part file) {
        return null;
    }

    @Override
    public Single<CholaResponse> doWebViewAPI(String authToken, String ecomregion, CholaRequest cholaRequest) {
        return null;
    }

    @Override
    public Single<CholaResponse> doCampaignAPI(String authToken, String ecomregion, CholaRequest cholaRequest) {
        return null;
    }

    @Override
    public LiveData<DistanceApiResponse> distanceCalculationApi(String locationRequest,String annotations) {
        return null;
    }

    @Override
    public Single<DistanceApiResponse> distanceCalculationApis(String location, String annotations) {
        return  null;
    }
}