package in.ecomexpress.sathi.repo.remote;

import androidx.lifecycle.LiveData;

import java.io.IOException;
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
import in.ecomexpress.sathi.repo.remote.model.eds.EdsRescheduleRequest;
import in.ecomexpress.sathi.repo.remote.model.eds.EdsRescheduleResponse;
import in.ecomexpress.sathi.repo.remote.model.eds.Get_Status_Masking;
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
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public interface IRestApiHelper {
    Single<LoginResponse> doLoginApiCall(LoginRequest request) throws IOException;


    Single<ConsigneeProfileResponse> doConsigneeProfileApiCall(String token, String ecomregion, ConsigneeProfileRequest consigneeProfileRequest) throws IOException;

    Single<LogoutResponse> doLogoutApiCall(String token, String ecomregion, LogoutRequest request) throws IOException;

    Single<AttendanceResponse> doAttendanceApiCall(String authToken, String ecomregion, AttendanceRequest request);

    Single<FwdReattemptResponse> doUdFwdReattemptApiCall(String authToken, String ecomregion, FwdReattemptRequest request);

    Single<PerformanceResponse> doPerformanceApiCall(String authToken, String ecomregion, PerformanceRequest request);

    Single<ForgotPasswordResponse> doForgetPasswordApiCall(String ecomregion, ForgetPasswordUserRequest forgetPasswordUserRequest);

    Single<ForgotPasswordResponse> doOTPVerifyWithPasswordApiCall(String ecomregion, OTPVerifyWithPasswordRequest otpVerifyWithPasswordRequest);

    Single<ForgotPasswordResponse> doResetPasswordApiCall(String authToken, String ecomregion, ChangePasswordRequest changePasswordRequest);

    Single<LastMileDRSListResponse> doDRSListApiCall(String authToken, String ecomregion, CommonUserIdRequest commonUserIdRequest);

    Single<DRSResponse> doDRSListApiNewCall(String authToken, String ecomregion, CommonUserIdRequest commonUserIdRequest);

    Single<ErrorResponse> doCallBridgeApiCall(String authToken, String ecomregion, CallApiRequest callApiRequest);

    Single<DRSResponse> doSMSApiCall(String authToken, String ecomregion, SmsRequest smsRequest);

    Single<MasterDataReasonCodeResponse> doMasterApiCall(String authToken, String ecomregion, CommonUserIdRequest commonUserIdRequest);

    //for Login VerifyOtp
    Single<LoginVerifyOtpResponse> doLoginVerifyOtpApiCall(String authToken, String ecomregion, LoginVerifyOtpRequest loginVerifyOtpRequest);

    Single<DisputeApiResponse> doRaiseDisputeApi(String authToken, String ecomregion, DisputeRequest disputeRequest);

    Single<VerifyDisputeApiResponse> doVerifyDisputeApi(String authToken, String ecomregion, VerifyDisputeRequest verifyDisputeRequest);

    Single<AadharStatusResponse> doGetStatusAadharMasking(String authToken, String ecomregion, Get_Status_Masking get_status_masking);

    //for verify otp
    Single<VerifyOtpResponse> doVerifyOtpApiCall(String authToken, String ecomregion, VerifyOtpRequest verifyOtpRequest);


    //for verify ud otp
    Single<VerifyOTPResponse> doVerifyUDOtpDRSApiCall(String authToken, String ecomregion, VerifyUDOtpRequest verifyUDOtpRequest);


    Single<VoiceOTPResponse> doVoiceOtpApiCall(String authToken, String ecomregion, VoiceOTP voiceOTP);

    // for resend Amazon OTP
    Single<AmazonOtpResponse> doAmazonOtpApiCall(String authToken, String ecomregion, AmazonOtpRequest amazonOtpRequest);

    Single<OtherNoResponse> doResendOtpToOtherNoApiCall(String authToken, String ecomregion, OtherNoRequest otherNoRequest);

    //for resend otp
    Single<ResendOtpResponse> doResendOtpApiCall(String authToken, String ecomregion, ResendOtpRequest resendOtpRequest);

    //for generate ud otp
    Single<ResendOtpResponse> doGenerateUDOtpApiCall(String authToken, String ecomregion, GenerateUDOtpRequest generateUDOtpRequest);

    Single<ResendOtpResponse> doResendUdOtpApiCall(String authToken, String ecomregion, ResendOtpRequest resendOtpRequest);

    //reg awb payphi
    Single<AwbResponse> doAwbRegisterApiCall(String token, String ecomregion, AwbRequest awbRequest);

    Single<AwbResponse> doAwbRegisterApiCallForSms(String token, String ecomregion, AwbRequest awbRequest);

    Single<SmsLinkResponse> doSentPayphiPaymentLinkSms(String token, String ecomregion, PaymentSmsLinkRequset awbRequest);

    //reg awb payphi
    Single<AwbResponse> doCheckStatusApiCall(String authToken, String ecomregion, AwbRequest awbRequest);

    //for start Trip
    Single<StartTripResponse> doStartTripApiCall(String token, String ecomregion, StartTripRequest startTripRequest);

    //for stop Trip
    Single<StopTripResponse> doStopTripApiCall(String token, String ecomregion, StopTripRequest stopTripRequest);

    //for forward Commit
    Single<ForwardCommitResponse> doFWDCommitApiCall(String authToken, String ecomregion, HashMap hashMap, List<ForwardCommit> forwardCommit);

    //for forward Undelivered Commit
    Single<ForwardCommitResponse> doFWDUndeliveredCommitApiCall(String authToken, String ecomregion, HashMap hashMap, ForwardCommit forwardCommit);

    Single<RVPCommitResponse> doRVPUndeliveredCommitApiCall(String authToken, String ecomregion, HashMap hashMap, RvpCommit rvpCommit);
    //Single<EDSCommitResponse> doEDSCommitApiCall(String authToken,HashMap hashMap,EdsCommit edsCommit);

    Single<RTSCommitResponse> doRTSCommitApiCall(String authToken, String ecomregion, HashMap hashMap, RTSCommit rvpCommit);

    Single<EDSCommitResponse> doEDSCommitApiCall(String authToken, String ecomregion, HashMap hashMap, EdsCommit edsCommit);

    Single<MasterDataConfig> doMasterReasonApiCall(String token, String ecomregion, masterRequest masterrequest);

    //For Fuel
    Single<FuelReimbursementResponse> doFuelListApiCall(String token, String ecomregion, FuelReimbursementRequest fuelReimbursementRequest);

    Single<ImageUploadResponse> doImageUploadApiCall(String authToken, String ecomregion, String imageType, Map headers, Map multipartBody, MultipartBody.Part file);

    Single<AadharMaskingResponse> doAadharMaskigImageUploadApiCall(String authToken, String ecomregion, String imageType, Map headers, Map multipartBody, MultipartBody.Part front_file, MultipartBody.Part rear_file);

    Single<ImageUploadResponse> doImageUploadApiCallStartStop(String authToken, String ecomregion, Map headers, Map multipartBody, MultipartBody.Part file);

    Single<ImageUploadResponse> doImageUploadApiCallTest(String authToken, String ecomregion, Map multipartBody);

    Single<Call<ResponseBody>> getImage(String authToken, String ecomregion, String url);

    Single<GMapResponse> getGMap(String authToken, String ecomregion, GMapRequest gMapRequest);

    Observable<Response<ResponseBody>> getFile(String authToken, String ecomregion, String url);

    Single<LoginVerifyOtpResponse> doLoginResendOtpApiCall(String authToken, String ecomregion, LoginVerifyOtpRequest loginVerifyOtpRequest);

    Single<LiveTrackingResponse> dogetLiveTrackingIDApiCall(String authToken, String ecomregion, LiveTrackingRequest liveTrackingRequest);

    Single<LiveTrackingLogRespone> dosendLiveTrackingLog(String authToken, String ecomregion, List<LiveTrackingLogTable> liveTrackingLog);

    Single<SOSResponse> doSOSApiCall(String authToken, String ecomregion, SOSRequest sosRequest);

    Single<LogFileUploadResponse> doAppLogsUploadApiCall(String ecomregion, Map headers, MultipartBody.Part file);

    Single<UploadImageResponse> doUploadImageApiCall(String ecomregion, Map request, MultipartBody.Part file) throws IOException;

    Single<DisputeImageApiResponse> doUploadDisputeImageApiCall(String ecomregion, Map request, MultipartBody.Part file) throws IOException;

    Single<ImageQualityResponse> getImageQuality(String ecomregion, int image_id) throws IOException;

    Single<ForwardCallResponse> doForwardCallStatusApiCall(String auth_token, String ecomregion, int counter, String emp_code, String awb_number, String drs_id, int shipper_id) throws IOException;


    Single<IncentiveResponse> doFEIncentiveApiCall(String url, String emp_code) throws IOException;


    Single<ekycXMLResponse> doEkycXMLApiCall(String ecomregion, String jobj, HashMap<String, String> webheader);

    //    Single<IciciResponse> doICICIApiCall( long awb,String lead_id, String shipmenttype,String parameter,String token,String pid,String url);
    Single<IciciResponse> doICICIApiCall(String token, String ecomregion, String url, RequestBody jobj);

    Single<IciciResponse> doICICICheckcStatusCall(String token, String ecomregion, String url, RequestBody urn);

    Single<IciciResponse_standard> doICICICheckcStatusCallStandard(String apikey, String ecomregion, String url, RequestBody urn);

    Single<ImageUploadResponse> doImageByteUploadApiCall(String authToken, String ecomregion, String imageType, Map headers, ByteImageRequest data);

    ImageUploadResponse doImageUploadApiCallInSingleThread(String authToken, String ecomregion, String imageType, Map<String, String> headers, Map<String, RequestBody> map, MultipartBody.Part fileToUpload);

    Single<DrsCheckListResponse> doDrsListCheck(String authToken, String ecomregion, DrsCheckListRequest drsCheckListRequest);

    Single<AdharApiResponse> doAdharApi(String authToken, String ecomregion, AdharRequest adharRequest);

    Single<CovidApiResponse> doCovidApi(String authToken, String ecomregion, CovidRequest covidRequest);

    Single<Biometric_response> dobiometricApiCall(String Authtoken, String ecomregion, Biometric_requestdata biometric_requestdata);

    Single<DCLocationUpdateResponse> doDCLocationUpdateApiCall(String authToken, String ecomregion, DCLocationUpdate dcLocationUpdate);

    Single<Amazon_reschedule_list> doScheduleDates(String authToken, String ecomregion, AmazonScheduleRequest strings);

    Single<CashReceipt_Response> doCashReceiptCall(String authToken, String ecomregion, CashReceipt_Request request);

    Single<EdsRescheduleResponse> doEdsRescheduleCall(String authToken, String ecomregion, EdsRescheduleRequest request);

    Single<VerifyOTPResponse> doVerifyOTPApiCall(String authToken, String ecomregion, VerifyOTPRequest verifyOTPRequest);

    Single<VerifyOTPResponse> doVerifyUdOTPApiCall(String authToken, String ecomregion, VerifyOTPRequest verifyOTPRequest);

    Single<PrintReceiptResponse> doPrintReceiptDataCall(String authToken, String ecomregion, PrintReceiptRequest printReceiptRequest);

    Single<PrintReceiptUploadResponse> doPrintReceiptUploadImage(String authToken, String ecomregion, Map<String, String> headers, Map<String, RequestBody> multipartBody, MultipartBody.Part file);
    //    ,String codd_id, String origin_id,String image_name, String image_code, String deposit_date,String cod_from_date,String cod_to_date,String total_amount,String deposit_reason

    Single<GenerateOTPResponse> doGenerateOTPApiCall(String authToken, String ecomregion, GenerateOTPRequest generateOTPRequest);

    Single<GenerateRTSOTPResponse> doGenerateRTSOTPApiCall(String authToken, String ecomregion, GenerateRTSOTPRequest generateOTPRequest);

    Single<Hospital> doHospitalList(String authToken, String ecomregion, String emp_code);


    //  Single<ContatDecryption> doconsigneeEncryptionCall(String token);
    Single<GenerateTokenNiyo> dogenerateniyotoken();

    Single<GenerateToken> dogeneratetoken(String token, String ecomregion);

    Single<IDFCToken_Response> dogeneratetokenIDFC(String url, String ecomregion, String grant_type, String client_assertion_type, String client_id, String scope, String client_assertion);

    Single<EncryptContactResponse> doencryptcontact(String token, String ecomregion, long awb);

    Single<WadhResponse> doGetWadhValueAntWork(String basic, String ecomregion, String token_url, WathRequest wathRequest);

    Single<DPDailyEarnedAmount> doDPDailyEarningApiCall(String token, String ecomregion, String emp_code);

    Single<VerifyOTPResponse> doDPVerifyOTPApiCall(String authToken, String ecomregion, VerifyOTPRequest verifyOTPRequest);

    Single<DailyEarningResponse> doDailyEarnigCalander(String authToken, String ecomregion, DailyEarningRequest dailyEarningRequest);

    Single<PayoutResponse> doPayoutApi(String authToken, String ecomregion, PayoutRequest payoutRequest);

    Single<DPReferenceCodeResponse> doDPReferecenApiCall(String authToken, String ecomregion, DPReferenceCodeRequest dpReferenceCodeRequest);

    Single<ReshceduleDetailsResponse> doReshceduleDetails(String authToken, String ecomregion, ResheduleDetailsRequest resheduleDetailsRequest);

    Single<RvpFlyerDuplicateCheckResponse> doRvpflyerDuplicateCheck(String authToken, String ecomregion, RvpFlyerDuplicateCheckRequest resheduleDetailsRequest);

    Single<List<ADMDATA>> getAdmData(String authToken, String ecomregion, String emp_code);

    Single<ADMUpdateResponse> doUpdateADMData(String authToken, String ecomregion, List<ADMUpdateRequest> admUpdateRequests);

    Single<ShipmentWeightResponse> doShipmentWeightApiCall(String authToken, String ecomregion, Map<String, String> headers, Map<String, RequestBody> multipartBody, MultipartBody.Part file);

    Single<CholaResponse> doCholaURLAPI(String authToken, String ecomregion, CholaRequest cholaRequest, boolean isTraining);

    Single<RTSResendOTPResponse> doResendOtpApiCall(String authToken, String ecomregion, RTSResendOTPRequest resendOtpRequest);

    Single<RTSResendOTPResponse> doResendOtpApiOtherMobileRTSCall(String authToken, String ecomregion, RTSResendOTPRequest resendOtpRequest);

    Single<RTSVerifyOTPResponse> doVerifyOTPApiCall(String authToken, String ecomregion, RTSVerifyOTPRequest rtsVerifyOTPRequest);

    Single<CardDetectionResponse> doCardDetectionResponse(String authToken, String ecomregion, Map<String, String> headers, Map<String, RequestBody> multipartBody, MultipartBody.Part file);

    Single<BioMatricResponse> doGetBioMatricData(String basic, String ecomregion, String token_url, BiomatricRequest biomatricRequest);

    Single<FwdReassignReponse> doFwdReassingApiCall(String authToken, FWDReassignRequest request);

    LiveData<EarningApiResponse> doTrainingAPICall(String authToken, EarningApiRequest earningApiRequest);

    Single<MarkAttendanceResponse> doMarkAttendanceApiCall(String authToken, MarkAttendanceRequest request);

    Single<CheckAttendanceResponse> doCheckAttendanceApiCall(String authToken, String empCode);


    Single<SelfieImageResponse> doSelfieImageUploadApiCall(String authToken, Map headers, Map multipartBody, MultipartBody.Part file);

    Single<CholaResponse> doWebViewAPI(String authToken, String ecomregion, CholaRequest cholaRequest);

    Single<CholaResponse> doCampaignAPI(String authToken, String ecomregion, CholaRequest cholaRequest);

}
