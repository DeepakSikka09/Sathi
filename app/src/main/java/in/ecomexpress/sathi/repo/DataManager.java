package in.ecomexpress.sathi.repo;

import android.content.Context;
import android.net.Uri;
import androidx.lifecycle.LiveData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import in.ecomexpress.sathi.repo.local.data.callbridge.CallApiRequest;
import in.ecomexpress.sathi.repo.local.data.eds.EDSCommitResponse;
import in.ecomexpress.sathi.repo.local.data.eds.EdsCommit;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommitResponse;
import in.ecomexpress.sathi.repo.local.data.commit.PushApi;
import in.ecomexpress.sathi.repo.local.data.rts.RTSCommit;
import in.ecomexpress.sathi.repo.local.data.rts.RTSCommitResponse;
import in.ecomexpress.sathi.repo.local.data.rvp.RVPCommitResponse;
import in.ecomexpress.sathi.repo.local.data.rvp.RvpCommit;
import in.ecomexpress.sathi.repo.local.db.IDBHelper;
import in.ecomexpress.sathi.repo.local.db.model.ApiUrlData;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.local.db.model.ImageModel;
import in.ecomexpress.sathi.repo.local.db.model.LiveTrackingLogTable;
import in.ecomexpress.sathi.repo.local.db.model.MsgLinkData;
import in.ecomexpress.sathi.repo.local.db.model.RVPQCImageTable;
import in.ecomexpress.sathi.repo.local.db.model.Remark;
import in.ecomexpress.sathi.repo.local.db.model.RescheduleEdsD;
import in.ecomexpress.sathi.repo.local.db.model.RvpWithQC;
import in.ecomexpress.sathi.repo.local.pref.IPreferenceHelper;
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
import in.ecomexpress.sathi.repo.remote.model.consignee_profile.ProfileFound;
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
import in.ecomexpress.sathi.repo.remote.model.drs_list.DRSSequence;
import in.ecomexpress.sathi.repo.remote.model.drs_list.LastMileDRSListResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.common.AmazonOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.drs_list.common.AmazonOtpResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.eds.ImageQualityResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.eds.UploadImageResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.ForwardCallResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.gmap.GMapRequest;
import in.ecomexpress.sathi.repo.remote.model.drs_list.gmap.GMapResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.DRSReturnToShipperTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.DRSReturnToShipperTypeNewResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.Details;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.ShipmentsDetail;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.RvpQualityCheck;
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
import in.ecomexpress.sathi.repo.remote.model.masterdata.CallbridgeConfiguration;
import in.ecomexpress.sathi.repo.remote.model.masterdata.CbPstnOptions;
import in.ecomexpress.sathi.repo.remote.model.masterdata.DashboardBanner;
import in.ecomexpress.sathi.repo.remote.model.masterdata.EDSReasonCodeMaster;
import in.ecomexpress.sathi.repo.remote.model.masterdata.Forward;
import in.ecomexpress.sathi.repo.remote.model.masterdata.ForwardReasonCodeMaster;
import in.ecomexpress.sathi.repo.remote.model.masterdata.GeneralQuestion;
import in.ecomexpress.sathi.repo.remote.model.masterdata.GlobalConfigurationMaster;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterDataConfig;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterDataReasonCodeResponse;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterDocumentList;
import in.ecomexpress.sathi.repo.remote.model.masterdata.RTSReasonCodeMaster;
import in.ecomexpress.sathi.repo.remote.model.masterdata.RVPReasonCodeMaster;
import in.ecomexpress.sathi.repo.remote.model.masterdata.Reverse;
import in.ecomexpress.sathi.repo.remote.model.masterdata.SampleQuestion;
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
import in.ecomexpress.sathi.repo.remote.model.payphi.raise_dispute.PaymentDisputedAwb;
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

@Singleton
public class DataManager implements IDataManager {

    private final IDBHelper mDbHelper;
    private final IPreferenceHelper mPreferencesHelper;
    private final IRestApiHelper mApiHelper;

    @Inject
    public DataManager(Context mContext, IDBHelper mDbHelper, IPreferenceHelper mPreferencesHelper, IRestApiHelper mApiHelper) {
        this.mDbHelper = mDbHelper;
        this.mPreferencesHelper = mPreferencesHelper;
        this.mApiHelper = mApiHelper;
    }

    @Override
    public int getCurrentUserLoggedInMode() {
        return mPreferencesHelper.getCurrentUserLoggedInMode();
    }

    @Override
    public void setCurrentUserLoggedInMode(LoggedInMode currentUserLoggedInMode) {
        mPreferencesHelper.setCurrentUserLoggedInMode(currentUserLoggedInMode);
    }

    @Override
    public String getAuthToken() {
        return mPreferencesHelper.getAuthToken();
    }

    @Override
    public void setAuthToken(String authToken) {
        mPreferencesHelper.setAuthToken(authToken);
    }

    @Override
    public void setCurrentTimeForDelay(long time) {
        mPreferencesHelper.setCurrentTimeForDelay(time);
    }

    @Override
    public long getCurrentTimeForDelay() {
        return mPreferencesHelper.getCurrentTimeForDelay();
    }

    @Override
    public int getTypeId() {
        return mPreferencesHelper.getTypeId();
    }

    @Override
    public void setTypeId(int pos) {
        mPreferencesHelper.setTypeId(pos);
    }

    @Override
    public int getVehicleTypeId() {
        return mPreferencesHelper.getVehicleTypeId();
    }

    @Override
    public void setVehicleTypeId(int pos) {
        mPreferencesHelper.setVehicleTypeId(pos);
    }

    @Override
    public int getProgressiveTimer() {
        return mPreferencesHelper.getProgressiveTimer();
    }

    @Override
    public void setProgressiveTimer(int timer) {
        mPreferencesHelper.setProgressiveTimer(timer);
    }

    @Override
    public int getSameDayReassignTimer() {
        return mPreferencesHelper.getSameDayReassignTimer();
    }

    @Override
    public void setSameDayReassignTimer(int count) {
        mPreferencesHelper.setSameDayReassignTimer(count);
    }

    @Override
    public String getServiceCenter() {
        return mPreferencesHelper.getServiceCenter();
    }

    @Override
    public void setServiceCenter(String serviceCenter) {
        mPreferencesHelper.setServiceCenter(serviceCenter);
    }

    @Override
    public void setDownloadAPKIsInProcess(long b) {
        mPreferencesHelper.setDownloadAPKIsInProcess(b);
    }

    @Override
    public String getName() {
        return mPreferencesHelper.getName();
    }

    @Override
    public void setName(String name) {
        mPreferencesHelper.setName(name);
    }

    @Override
    public String getDesignation() {
        return mPreferencesHelper.getDesignation();
    }

    @Override
    public void setDesignation(String designation) {
        mPreferencesHelper.setDesignation(designation);
    }

    @Override
    public String getMobile() {
        return mPreferencesHelper.getMobile();
    }

    @Override
    public void setMobile(String mobile) {
        mPreferencesHelper.setMobile(mobile);
    }

    @Override
    public String getVodaOrderNo() {
        return mPreferencesHelper.getVodaOrderNo();
    }

    @Override
    public void setVodaOrderNo(String orderNo) {
        mPreferencesHelper.setVodaOrderNo(orderNo);
    }

    @Override
    public Observable<Set<RVPReasonCodeMaster>> getSubgroupFromRvpReasonCode(String isSecure) {
        return null;
    }


    @Override
    public Long getLocationType() {
        return mPreferencesHelper.getLocationType();
    }

    @Override
    public void setLocationType(Long locationType) {
        mPreferencesHelper.setLocationType(locationType);
    }

    @Override
    public String getLocationCode() {
        return mPreferencesHelper.getLocationCode();
    }

    @Override
    public void setLocationCode(String locationCode) {
        mPreferencesHelper.setLocationCode(locationCode);
    }

    @Override
    public String getCode() {
        return mPreferencesHelper.getCode();
    }

    @Override
    public void setCode(String code) {
        mPreferencesHelper.setCode(code);
    }

    @Override
    public String getAuthPinCode() {
        return mPreferencesHelper.getAuthPinCode();
    }

    @Override
    public void setAuthPinCode(String authPinCode) {
        mPreferencesHelper.setAuthPinCode(authPinCode);
    }

    @Override
    public boolean clearPrefrence() {
        return mPreferencesHelper.clearPrefrence();
    }

    @Override
    public boolean getIsUserValided() {
        return mPreferencesHelper.getIsUserValided();
    }

    @Override
    public void setIsUserValided(Boolean isUserValid) {
        mPreferencesHelper.setIsUserValided(isUserValid);
    }

    @Override
    public String getPhotoUrl() {
        return mPreferencesHelper.getPhotoUrl();
    }

    @Override
    public void setPhotoUrl(String photoUrl) {
        mPreferencesHelper.setPhotoUrl(photoUrl);
    }

    @Override
    public String getForwardReasonCodeFlag() {
        return mPreferencesHelper.getForwardReasonCodeFlag();
    }

    @Override
    public void setForwardReasonCodeFlag(String flag) {
        mPreferencesHelper.setForwardReasonCodeFlag(flag);
    }

    @Override
    public String getRTSReasonCodeFlag() {
        return mPreferencesHelper.getRTSReasonCodeFlag();
    }

    @Override
    public void setRTSReasonCodeFlag(String flag) {
        mPreferencesHelper.setRTSReasonCodeFlag(flag);
    }

    @Override
    public void setRtsInputResendFlag(String flag) {
        mPreferencesHelper.setRtsInputResendFlag(flag);
    }

    @Override
    public String getRtsInputResendFlag() {
        return mPreferencesHelper.getRtsInputResendFlag();
    }

    @Override
    public String getRVPReasonCodeFlag() {
        return mPreferencesHelper.getRVPReasonCodeFlag();
    }

    @Override
    public void setRVPReasonCodeFlag(String flag) {
        mPreferencesHelper.setRVPReasonCodeFlag(flag);
    }

    @Override
    public String getEDSReasonCodeFlag() {
        return mPreferencesHelper.getEDSReasonCodeFlag();
    }

    @Override
    public void setEDSReasonCodeFlag(String flag) {
        mPreferencesHelper.setEDSReasonCodeFlag(flag);
    }

    @Override
    public String getTripId() {
        return mPreferencesHelper.getTripId();
    }

    @Override
    public void setTripId(String tripId) {
        mPreferencesHelper.setTripId(tripId);
    }

    @Override
    public String getVehicleType() {
        return mPreferencesHelper.getVehicleType();
    }

    @Override
    public void setVehicleType(String vehicleType) {
        mPreferencesHelper.setVehicleType(vehicleType);
    }

    @Override
    public String getTypeOfVehicle() {
        return mPreferencesHelper.getTypeOfVehicle();
    }

    @Override
    public void setTypeOfVehicle(String typeOfVehicle) {
        mPreferencesHelper.setTypeOfVehicle(typeOfVehicle);
    }

    @Override
    public String getRouteName() {
        return mPreferencesHelper.getRouteName();
    }

    @Override
    public void setRouteName(String routeName) {
        mPreferencesHelper.setRouteName(routeName);
    }

    @Override
    public long getStartTripMeterReading() {
        return mPreferencesHelper.getStartTripMeterReading();
    }

    @Override
    public void setStartTripMeterReading(long reading) {
        mPreferencesHelper.setStartTripMeterReading(reading);
    }

    @Override
    public long getStopTripMeterReading() {
        return mPreferencesHelper.getStopTripMeterReading();
    }

    @Override
    public void setStopTripMeterReading(long reading) {
        mPreferencesHelper.setStopTripMeterReading(reading);
    }

    @Override
    public long getActualMeterReading() {
        return mPreferencesHelper.getActualMeterReading();
    }

    @Override
    public void setActualMeterReading(Long actualMeterReading) {
        mPreferencesHelper.setActualMeterReading(actualMeterReading);
    }

    @Override
    public String getCallITExecutiveNo() {
        return mPreferencesHelper.getCallITExecutiveNo();
    }

    @Override
    public void setCallITExecutiveNo(String number) {
        mPreferencesHelper.setCallITExecutiveNo(number);
    }

    @Override
    public void increaseNotificationCounter() {
        mPreferencesHelper.increaseNotificationCounter();
    }

    @Override
    public int getNotificationCounter() {
        return mPreferencesHelper.getNotificationCounter();
    }

    @Override
    public String getPstnFormat() {
        return mPreferencesHelper.getPstnFormat();
    }

    @Override
    public void setPstnFormat(String format) {
        mPreferencesHelper.setPstnFormat(format);
    }

    @Override
    public double getCurrentLatitude() {
        return mPreferencesHelper.getCurrentLatitude();
    }

    @Override
    public void setCurrentLatitude(double latitude) {
        mPreferencesHelper.setCurrentLatitude(latitude);
    }

    @Override
    public double getCurrentLongitude() {
        return mPreferencesHelper.getCurrentLongitude();
    }

    @Override
    public void setCurrentLongitude(double longitude) {
        mPreferencesHelper.setCurrentLongitude(longitude);
    }

    @Override
    public double getDCLatitude() {
        return mPreferencesHelper.getDCLatitude();
    }

    @Override
    public double getDCLongitude() {
        return mPreferencesHelper.getDCLongitude();
    }

    @Override
    public int getRescheduleAttemptTimes() {
        return mPreferencesHelper.getRescheduleAttemptTimes();
    }

    @Override
    public void setDlightSuccessEncrptedOTP(String dlightSuccessEncrptedOTP) {
        mPreferencesHelper.setDlightSuccessEncrptedOTP(dlightSuccessEncrptedOTP);
    }

    @Override
    public String getDlightSuccessEncrptedOTP() {
        return mPreferencesHelper.getDlightSuccessEncrptedOTP();
    }

    @Override
    public void setDlightSuccessEncrptedOTPType(String otpType) {
        mPreferencesHelper.setDlightSuccessEncrptedOTPType(otpType);
    }

    @Override
    public String getDlightSuccessEncrptedOTPType() {
        return mPreferencesHelper.getDlightSuccessEncrptedOTPType();
    }

    @Override
    public void setRescheduleAttemptTimes(int rescheduleAttemptTimes) {

    }

    @Override
    public long isDownloadAPKIsInProcess() {
        return mPreferencesHelper.isDownloadAPKIsInProcess();
    }

    @Override
    public void updateDCDetails(LoginResponse.DcLocationAddress dcLocationAddress) {
        mPreferencesHelper.updateDCDetails(dcLocationAddress);
    }

    @Override
    public void updateUserLoggedInState(LoggedInMode loggedInMode) {
        mPreferencesHelper.updateUserLoggedInState(loggedInMode);
    }

    @Override
    public void saveBottomText(String text) {
        mPreferencesHelper.saveBottomText(text);
    }

    @Override
    public String getBottomText() {
        return mPreferencesHelper.getBottomText();
    }

    @Override
    public void setUserAsLoggedOut() {
        mPreferencesHelper.setCurrentUserLoggedInMode(LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT);
    }

    @Override
    public void updateUserInfo(LoggedInMode loggedInMode, String authToken, String serviceCenter, String name, String designation, String mobile, long locationType, String locationCode, String code, boolean isUserValidateted, String photoUrl, String ITExecutiveNo) {
        setCurrentUserLoggedInMode(loggedInMode);
        setAuthToken(authToken);
        setServiceCenter(serviceCenter);
        setName(name);
        setDesignation(designation);
        setMobile(mobile);
        setLocationType(locationType);
        setLocationCode(locationCode);
        setCode(code);
        setIsUserValided(isUserValidateted);
        setPhotoUrl(photoUrl);
        setCallITExecutiveNo(ITExecutiveNo);
    }

    @Override
    public void startTripInfo(Long actualMeterReading, String tripId, String vehicleType, String vehicleOwnerType, String routeName) {
        setTripId(tripId);
        setVehicleType(vehicleType);
        setTypeOfVehicle(vehicleOwnerType);
        setRouteName(routeName);
        setStartTripMeterReading(actualMeterReading);
    }

    @Override
    public Single<LoginResponse> doLoginApiCall(LoginRequest request) throws IOException {
        return mApiHelper.doLoginApiCall(request);
    }


    @Override
    public Single<ConsigneeProfileResponse> doConsigneeProfileApiCall(String token, String ecomregion, ConsigneeProfileRequest consigneeProfileRequest) throws IOException {
        return mApiHelper.doConsigneeProfileApiCall(token, ecomregion, consigneeProfileRequest);
    }

    @Override
    public Single<LogoutResponse> doLogoutApiCall(String token, String ecomregion, LogoutRequest request) throws IOException {
        return mApiHelper.doLogoutApiCall(token, ecomregion, request);
    }

    @Override
    public Single<AttendanceResponse> doAttendanceApiCall(String authToken, String ecomregion, AttendanceRequest request) {
        return mApiHelper.doAttendanceApiCall(authToken, ecomregion, request);
    }

    @Override
    public Single<FwdReattemptResponse> doUdFwdReattemptApiCall(String authToken, String ecomregion, FwdReattemptRequest request) {
        return mApiHelper.doUdFwdReattemptApiCall(authToken, ecomregion, request);
    }

    @Override
    public Single<PerformanceResponse> doPerformanceApiCall(String authToken, String ecomregion, PerformanceRequest request) {
        return mApiHelper.doPerformanceApiCall(authToken, ecomregion, request);
    }

    @Override
    public Single<StartTripResponse> doStartTripApiCall(String token, String ecomregion, StartTripRequest startTripRequest) {
        return mApiHelper.doStartTripApiCall(token, ecomregion, startTripRequest);
    }

    @Override
    public Single<StopTripResponse> doStopTripApiCall(String token, String ecomregion, StopTripRequest stopTripRequest) {
        return mApiHelper.doStopTripApiCall(token, ecomregion, stopTripRequest);
    }

    @Override
    public Single<ForgotPasswordResponse> doForgetPasswordApiCall(String ecomregion, ForgetPasswordUserRequest forgetPasswordUserRequest) {
        return mApiHelper.doForgetPasswordApiCall(ecomregion, forgetPasswordUserRequest);
    }

    @Override
    public Single<ForgotPasswordResponse> doOTPVerifyWithPasswordApiCall(String ecomregion, OTPVerifyWithPasswordRequest otpVerifyWithPasswordRequest) {
        return mApiHelper.doOTPVerifyWithPasswordApiCall(ecomregion, otpVerifyWithPasswordRequest);
    }

    @Override
    public Single<ForgotPasswordResponse> doResetPasswordApiCall(String token, String ecomregion, ChangePasswordRequest changePasswordRequest) {
        return mApiHelper.doResetPasswordApiCall(token, ecomregion, changePasswordRequest);
    }

    @Override
    public Single<LastMileDRSListResponse> doDRSListApiCall(String authToken, String ecomregion, CommonUserIdRequest commonUserIdRequest) {
        return mApiHelper.doDRSListApiCall(authToken, ecomregion, commonUserIdRequest);
    }

    @Override
    public Single<DRSResponse> doDRSListApiNewCall(String authToken, String ecomregion, CommonUserIdRequest commonUserIdRequest) {
        return mApiHelper.doDRSListApiNewCall(authToken, ecomregion, commonUserIdRequest);
    }

    @Override
    public Single<ErrorResponse> doCallBridgeApiCall(String authToken, String ecomregion, CallApiRequest callApiRequest) {
        return mApiHelper.doCallBridgeApiCall(authToken, ecomregion, callApiRequest);
    }

    @Override
    public Single<DRSResponse> doSMSApiCall(String authToken, String ecomregion, SmsRequest smsRequest) {
        return mApiHelper.doSMSApiCall(authToken, ecomregion, smsRequest);
    }

    @Override
    public Single<MasterDataReasonCodeResponse> doMasterApiCall(String authToken, String ecomregion, CommonUserIdRequest commonUserIdRequest) {
        return null;

    }

    @Override
    public Single<LoginVerifyOtpResponse> doLoginVerifyOtpApiCall(String authToken, String ecomregion, LoginVerifyOtpRequest loginVerifyOtpRequest) {
        return mApiHelper.doLoginVerifyOtpApiCall(authToken, ecomregion, loginVerifyOtpRequest);
    }

    @Override
    public Single<DisputeApiResponse> doRaiseDisputeApi(String authToken, String ecomregion, DisputeRequest disputeRequest) {
        return mApiHelper.doRaiseDisputeApi(authToken, ecomregion, disputeRequest);
    }

    @Override
    public Single<VerifyDisputeApiResponse> doVerifyDisputeApi(String authToken, String ecomregion, VerifyDisputeRequest verifyDisputeRequest) {
        return mApiHelper.doVerifyDisputeApi(authToken, ecomregion, verifyDisputeRequest);
    }

    @Override
    public Single<AadharStatusResponse> doGetStatusAadharMasking(String authToken, String ecomregion, Get_Status_Masking get_status_masking) {
        return mApiHelper.doGetStatusAadharMasking(authToken, ecomregion, get_status_masking);
    }

    @Override
    public Single<VerifyOtpResponse> doVerifyOtpApiCall(String authToken, String ecomregion, VerifyOtpRequest verifyOtpRequest) {
        return mApiHelper.doVerifyOtpApiCall(authToken, ecomregion, verifyOtpRequest);
    }

    @Override
    public Single<VerifyOTPResponse> doVerifyUDOtpDRSApiCall(String authToken, String ecomregion, VerifyUDOtpRequest verifyUDOtpRequest) {
        return mApiHelper.doVerifyUDOtpDRSApiCall(authToken, ecomregion, verifyUDOtpRequest);
    }

    @Override
    public Single<VoiceOTPResponse> doVoiceOtpApiCall(String authToken, String ecomregion, VoiceOTP voiceOTP) {
        return mApiHelper.doVoiceOtpApiCall(authToken, ecomregion, voiceOTP);
    }

    @Override
    public Single<AmazonOtpResponse> doAmazonOtpApiCall(String authToken, String ecomregion, AmazonOtpRequest amazonOtpRequest) {
        return mApiHelper.doAmazonOtpApiCall(authToken, ecomregion, amazonOtpRequest);
    }

    @Override
    public Single<OtherNoResponse> doResendOtpToOtherNoApiCall(String authToken, String ecomregion, OtherNoRequest otherNoRequest) {
        return mApiHelper.doResendOtpToOtherNoApiCall(authToken, ecomregion, otherNoRequest);
    }

    //resend otp
    @Override
    public Single<ResendOtpResponse> doResendOtpApiCall(String authToken, String ecomregion, ResendOtpRequest resendOtpRequest) {
        return mApiHelper.doResendOtpApiCall(authToken, ecomregion, resendOtpRequest);
    }

    //generate ud otp
    @Override
    public Single<ResendOtpResponse> doGenerateUDOtpApiCall(String authToken, String ecomregion, GenerateUDOtpRequest generateUDOtpRequest) {
        return mApiHelper.doGenerateUDOtpApiCall(authToken, ecomregion, generateUDOtpRequest);
    }
    @Override
    public Single<ResendOtpResponse> doResendUdOtpApiCall(String authToken, String ecomregion, ResendOtpRequest resendOtpRequest) {
        return mApiHelper.doResendUdOtpApiCall(authToken, ecomregion, resendOtpRequest);
    }

    @Override
    public Single<AwbResponse> doAwbRegisterApiCall(String authtoken, String ecomregion, AwbRequest awbRequest) {
        return mApiHelper.doAwbRegisterApiCall(authtoken, ecomregion, awbRequest);
    }

    @Override
    public Single<AwbResponse> doAwbRegisterApiCallForSms(String token, String ecomregion, AwbRequest awbRequest) {
        return mApiHelper.doAwbRegisterApiCallForSms(token, ecomregion, awbRequest);
    }

    @Override
    public Single<SmsLinkResponse> doSentPayphiPaymentLinkSms(String token, String ecomregion, PaymentSmsLinkRequset awbRequest) {
        return mApiHelper.doSentPayphiPaymentLinkSms(token, ecomregion, awbRequest);
    }

    @Override
    public Single<AwbResponse> doCheckStatusApiCall(String authToken, String ecomregion, AwbRequest awbRequest) {
        return mApiHelper.doCheckStatusApiCall(authToken, ecomregion, awbRequest);
    }

    @Override
    public Single<ForwardCommitResponse> doFWDCommitApiCall(String token, String ecomregion, HashMap hashMap, List<ForwardCommit> forwardCommit) {
        return mApiHelper.doFWDCommitApiCall(token, ecomregion, hashMap, forwardCommit);
    }

    //Undelivered Forward Commit
    @Override
    public Single<ForwardCommitResponse> doFWDUndeliveredCommitApiCall(String token, String ecomregion, HashMap hashMap, ForwardCommit forwardCommit) {
        return mApiHelper.doFWDUndeliveredCommitApiCall(token, ecomregion, hashMap, forwardCommit);
    }

    @Override
    public Single<RVPCommitResponse> doRVPUndeliveredCommitApiCall(String token, String ecomregion, HashMap hashMap, RvpCommit rvpCommit) {
        return mApiHelper.doRVPUndeliveredCommitApiCall(token, ecomregion, hashMap, rvpCommit);
    }

    @Override
    public Single<RTSCommitResponse> doRTSCommitApiCall(String token, String ecomregion, HashMap hashMap, RTSCommit rtsCommit) {
        return mApiHelper.doRTSCommitApiCall(token, ecomregion, hashMap, rtsCommit);
    }

    @Override
    public Single<EDSCommitResponse> doEDSCommitApiCall(String authToken, String ecomregion, HashMap hashMap, EdsCommit edsCommit) {
        return mApiHelper.doEDSCommitApiCall(authToken, ecomregion, hashMap, edsCommit);
    }

    @Override
    public Single<MasterDataConfig> doMasterReasonApiCall(String token, String ecomregion, masterRequest user_name) {
        return mApiHelper.doMasterReasonApiCall(token, ecomregion, user_name);
    }

    //fuel
    @Override
    public Single<FuelReimbursementResponse> doFuelListApiCall(String token, String ecomregion, FuelReimbursementRequest fuelReimbursementRequest) {
        return mApiHelper.doFuelListApiCall(token, ecomregion, fuelReimbursementRequest);
    }

    @Override
    public Single<ImageUploadResponse> doImageUploadApiCall(String token, String ecomregion, String imageType, Map headers, Map multipartBody, MultipartBody.Part file) {
        return mApiHelper.doImageUploadApiCall(token, ecomregion, imageType, headers, multipartBody, file);
    }

    @Override
    public Single<AadharMaskingResponse> doAadharMaskigImageUploadApiCall(String authToken, String ecomregion, String imageType, Map headers, Map multipartBody, MultipartBody.Part front_file, MultipartBody.Part rear_file) {
        return mApiHelper.doAadharMaskigImageUploadApiCall(authToken, ecomregion, imageType, headers, multipartBody, front_file, rear_file);
    }

    @Override
    public ImageUploadResponse doImageUploadApiCallInSingleThread(String authToken, String ecomregion, String imageType, Map headers, Map multipartBody, MultipartBody.Part file) {
        return mApiHelper.doImageUploadApiCallInSingleThread(authToken, ecomregion, imageType, headers, multipartBody, file);
    }

    @Override
    public Single<ImageUploadResponse> doImageUploadApiCallStartStop(String authToken, String ecomregion, Map headers, Map multipartBody, MultipartBody.Part file) {
        return mApiHelper.doImageUploadApiCallStartStop(authToken, ecomregion, headers, multipartBody, file);
    }

    @Override
    public Observable<Boolean> saveDRSForwardList(List<DRSForwardTypeResponse> drsForwardTypeResponseList) {
        return mDbHelper.saveDRSForwardList(drsForwardTypeResponseList);
    }

    @Override
    public Observable<Boolean> saveProfileFoundList(List<ProfileFound> profileFoundList) {
        return mDbHelper.saveProfileFoundList(profileFoundList);
    }

    @Override
    public Observable<Boolean> saveToDRSSequenceTable(List<DRSSequence> drsSequenceList) {
        return mDbHelper.saveToDRSSequenceTable(drsSequenceList);
    }

    @Override
    public Observable<Boolean> updateDRSSequenceTable(List<DRSSequence> drsSequenceList) {
        return mDbHelper.updateDRSSequenceTable(drsSequenceList);
    }

    @Override
    public Observable<List<DRSSequence>> getAllDRSSequenceData() {
        return mDbHelper.getAllDRSSequenceData();
    }

    @Override
    public Observable<Boolean> deleteAllDataDRSSequence() {
        return mDbHelper.deleteAllDataDRSSequence();
    }

    @Override
    public Observable<Boolean> saveDRSRTSList(List<DRSReturnToShipperTypeResponse> drsReturnToShipperTypeResponses) {
        return mDbHelper.saveDRSRTSList(drsReturnToShipperTypeResponses);

    }

    @Override
    public Observable<Boolean> saveDRSNewRTSList(DRSReturnToShipperTypeNewResponse drsReturnToShipperTypeResponses) {
        return mDbHelper.saveDRSNewRTSList(drsReturnToShipperTypeResponses);
    }

    @Override
    public Observable<Boolean> saveDRSRVP(DRSReverseQCTypeResponse drsReverseQCTypeResponses) {
        return mDbHelper.saveDRSRVP(drsReverseQCTypeResponses);
    }

    @Override
    public Observable<Boolean> saveNewDrsEDS(EDSResponse edsResponse) {
        return mDbHelper.saveNewDrsEDS(edsResponse);
    }

    @Override
    public Observable<Boolean> saveCallbridgeConfiguration(CallbridgeConfiguration callbridgeConfiguration) {
        return mDbHelper.saveCallbridgeConfiguration(callbridgeConfiguration);
    }

    @Override
    public Observable<Boolean> saveDashboardBanner(List<DashboardBanner> dashboardBanner) {
        return mDbHelper.saveDashboardBanner(dashboardBanner);
    }

    @Override
    public Observable<List<DashboardBanner>> getAllDashboardBanner() {
        return mDbHelper.getAllDashboardBanner();
    }

    @Override
    public Observable<Boolean> updatePushSyncStatusToZero(List<Long> awbNo) {
        return mDbHelper.updatePushSyncStatusToZero(awbNo);
    }

    @Override
    public Observable<Boolean> updatePushSyncStatusToZeroOnClick(long awbNo) {
        return mDbHelper.updatePushSyncStatusToZeroOnClick(awbNo);
    }

    @Override
    public Observable<Boolean> updatePushShipmentStatus(long awbNo, int shipment_status) {
        return mDbHelper.updatePushShipmentStatus(awbNo, shipment_status);
    }

    @Override
    public Observable<Boolean> saveDRSRVPList(List<DRSReverseQCTypeResponse> drsReverseQCTypeResponses) {
        return mDbHelper.saveDRSRVPList(drsReverseQCTypeResponses);
    }

    @Override
    public Observable<Boolean> getRescheduleFlag(String awbNo) {
        return mDbHelper.getRescheduleFlag(awbNo);
    }

    @Override
    public Observable<Boolean> saveDRSRVPListQualityCheck(List<RvpQualityCheck> rvpQualityChecks) {
        return mDbHelper.saveDRSRVPListQualityCheck(rvpQualityChecks);
    }

    @Override
    public Observable<List<DRSForwardTypeResponse>> getAllForwardList() {
        return mDbHelper.getAllForwardList();
    }

    @Override
    public Observable<List<RvpQualityCheck>> getQcValuesForAwb() {
        return mDbHelper.getQcValuesForAwb();
    }

    @Override
    public Observable<List<DRSForwardTypeResponse>> getUnSyncForwardList() {
        return mDbHelper.getUnSyncForwardList();
    }

    @Override
    public Observable<List<EDSResponse>> getUnSyncEdsList() {
        return mDbHelper.getUnSyncEdsList();
    }

    @Override
    public Observable<List<DRSReverseQCTypeResponse>> getUnSyncRVPList() {
        return mDbHelper.getUnSyncRVPList();
    }

    @Override
    public Observable<List<CbPstnOptions>> getCbPstnOptions() {
        return mDbHelper.getCbPstnOptions();
    }

    @Override
    public Observable<List<GlobalConfigurationMaster>> getglobalConfigurationMasters() {
        return mDbHelper.getglobalConfigurationMasters();
    }

    @Override
    public Observable<Boolean> saveCallbridgecb_pstn_options(List<CbPstnOptions> cb_pstn_options) {
        return mDbHelper.saveCallbridgecb_pstn_options(cb_pstn_options);
    }

    @Override
    public Observable<List<DRSForwardTypeResponse>> getDRSListForward() {
        return mDbHelper.getDRSListForward();
    }

    @Override
    public Observable<List<DRSReturnToShipperTypeResponse>> getDRSListRTS() {

        return mDbHelper.getDRSListRTS();
    }

    @Override
    public Observable<DRSReturnToShipperTypeNewResponse> getDRSListNewRTS() {
        return mDbHelper.getDRSListNewRTS();
    }

    @Override
    public Observable<Details> getVWDetails(long id) {
        return mDbHelper.getVWDetails(id);
    }

    @Override
    public Observable<List<ShipmentsDetail>> getVWShipmentList(long vwID) {
        return mDbHelper.getVWShipmentList(vwID);
    }

    @Override
    public Observable<List<DRSReverseQCTypeResponse>> getDRSListRVP() {
        return mDbHelper.getDRSListRVP();
    }

    @Override
    public Observable<List<EDSResponse>> getDrsListNewEds() {
        return mDbHelper.getDrsListNewEds();
    }

    @Override
    public Observable<DRSForwardTypeResponse> getForwardDRS(String awbNo) {
        return mDbHelper.getForwardDRS(awbNo);
    }

    @Override
    public Observable<DRSForwardTypeResponse> getForwardDRSCompositeKey(Long awb) {
        return mDbHelper.getForwardDRSCompositeKey(awb);
    }

    @Override
    public Observable<DRSForwardTypeResponse> fetchObdQualityCheckData(Long awb) {
        return mDbHelper.fetchObdQualityCheckData(awb);
    }

    @Override
    public Observable<Integer> getDuplicateValueCount(String detailId) {
        return mDbHelper.getDuplicateValueCount(detailId);
    }

    @Override
    public Observable<DRSReturnToShipperTypeResponse> getRTSDRS(long awbNo) {
        return mDbHelper.getRTSDRS(awbNo);
    }

    @Override
    public Observable<DRSReverseQCTypeResponse> getRVPDRS(String awbNo) {
        return mDbHelper.getRVPDRS(awbNo);
    }

    @Override
    public Observable<Boolean> isRVPDRSExist(String awbNo) {
        return mDbHelper.isRVPDRSExist(awbNo);
    }

    @Override
    public Observable<Long> insertOrUpdateForward(DRSForwardTypeResponse drsForwardTypeResponse) {
        return mDbHelper.insertOrUpdateForward(drsForwardTypeResponse);
    }

    @Override
    public Observable<Long> getFWDStatusCount(int status) {
        return mDbHelper.getFWDStatusCount(status);
    }

    @Override
    public Observable<Long> getRTSStatusCount(int status) {
        return mDbHelper.getRTSStatusCount(status);
    }

    @Override
    public Observable<Long> getRVPStatusCount(int status) {
        return mDbHelper.getRVPStatusCount(status);
    }

    @Override
    public Observable<String> getTypeOfShipment(String awb) {
        return mDbHelper.getTypeOfShipment(awb);
    }
    @Override
    public Observable<String> getPhonePeShipmentType(String awb) {
        return mDbHelper.getPhonePeShipmentType(awb);
    }

    @Override
    public Observable<Long> getEDSStatusCount(int status) {
        return mDbHelper.getEDSStatusCount(status);
    }

    @Override
    public Observable<Double> getCodCount() {
        return mDbHelper.getCodCount();
    }

    @Override
    public Observable<Double> getEcodStatusCount() {
        return mDbHelper.getEcodStatusCount();
    }

    @Override
    public Observable<Boolean> isEDSDRSNewExist(String awbNo) {
        return mDbHelper.isEDSDRSNewExist(awbNo);
    }

    @Override
    public Observable<Boolean> saveEDSActivityNewWizardList(List<EDSActivityWizard> edsActivityWizards) {
        return mDbHelper.saveEDSActivityNewWizardList(edsActivityWizards);
    }

    @Override
    public Observable<Boolean> updateForwardStatus(String awbNo, int status) {
        return mDbHelper.updateForwardStatus(awbNo, status);
    }

    @Override
    public Observable<Boolean> insertcodCollected(Long awbNo, float amount) {
        return mDbHelper.insertcodCollected(awbNo, amount);
    }

    @Override
    public Observable<Boolean> insertEcodCollected(Long awbNo, float amount) {
        return mDbHelper.insertEcodCollected(awbNo, amount);
    }

    @Override
    public Observable<Boolean> updateForwardCallAttempted(Long awbNo, int isCallAttempted) {
        return mDbHelper.updateForwardCallAttempted(awbNo, isCallAttempted);
    }

    @Override
    public Observable<Boolean> updateRVPCallAttempted(Long awbNo, int isCallAttempted) {
        return mDbHelper.updateRVPCallAttempted(awbNo, isCallAttempted);
    }

    @Override
    public Observable<RTSReasonCodeMaster> isAttributeAvailable(String reasoncode) {
        return mDbHelper.isAttributeAvailable(reasoncode);
    }

    @Override
    public Observable<Boolean> updateRTSCallAttempted(Long id, int isCallAttempted) {
        return mDbHelper.updateRTSCallAttempted(id, isCallAttempted);
    }

    @Override
    public Observable<Boolean> updateEDSCallAttempted(Long awbNo, int isCallAttempted) {
        return mDbHelper.updateEDSCallAttempted(awbNo, isCallAttempted);
    }

    @Override
    public Observable<Boolean> updateRvpStatus(String awbNo, int status) {
        return mDbHelper.updateRvpStatus(awbNo, status);
    }

    @Override
    public Observable<Boolean> updateRtsStatus(Long id, int status) {
        return mDbHelper.updateRtsStatus(id, status);
    }

    @Override
    public Observable<Boolean> updateEdsStatus(String id, int status) {
        return mDbHelper.updateEdsStatus(id, status);
    }

    @Override
    public Observable<Boolean> updateOTPForward(long awbNo, String otp) {
        return mDbHelper.updateOTPForward(awbNo, otp);
    }

    @Override
    public Observable<Boolean> updateAssignDataForward(long awbNo, String assign_date) {
        return mDbHelper.updateAssignDataForward(awbNo, assign_date);
    }

    @Override
    public Observable<Boolean> updateTotalAttemptsForward(long awbNo, int attempts) {
        return mDbHelper.updateTotalAttemptsForward(awbNo, attempts);
    }

    @Override
    public Observable<Boolean> deleteShipment(String compositkey) {
        return mDbHelper.deleteShipment(compositkey);
    }

    @Override
    public Observable<Boolean> updateForwardShipment(String awb_no, int syncStatus, int status, int drs_id, String composite_key_new) {
        return mDbHelper.updateForwardShipment(awb_no, syncStatus, status, drs_id, composite_key_new);
    }

    @Override
    public Observable<Boolean> updateOTPEDS(long awbNo, String otp) {
        return mDbHelper.updateOTPEDS(awbNo, otp);
    }

    @Override
    public Observable<List<ImageModel>> getUDImage(String imagetype) {
        return mDbHelper.getUDImage(imagetype);
    }

    @Override
    public Observable<Boolean> updateRTSShipmentDetail(ShipmentsDetail shipmentsDetail) {
        return mDbHelper.updateRTSShipmentDetail(shipmentsDetail);
    }

    @Override
    public Observable<Boolean> updateRtsImageCapturedStatus(Long awb_no, int is_image_captured) {
        return mDbHelper.updateRtsImageCapturedStatus(awb_no, is_image_captured);
    }

    @Override
    public Observable<Boolean> deleteAllTables() {
        return mDbHelper.deleteAllTables();
    }

    @Override
    public Observable<Boolean> deleteAllTablesOnStopTrip() {
        return mDbHelper.deleteAllTablesOnStopTrip();
    }

    @Override
    public Observable<Boolean> nukeTable() {
        return mDbHelper.nukeTable();
    }

    @Override
    public Observable<Boolean> deleteDRSTables() {
        return mDbHelper.deleteDRSTables();
    }

    @Override
    public Observable<Boolean> saveMasterReason(MasterDataConfig masterDataReasonCodeResponse) {
        return mDbHelper.saveMasterReason(masterDataReasonCodeResponse);
    }

    @Override
    public Observable<RvpWithQC> getRvpWithQc(String awbNo) {
        return mDbHelper.getRvpWithQc(awbNo);
    }

    @Override
    public Observable<CallbridgeConfiguration> loadallcallbridge() {
        return mDbHelper.loadallcallbridge();
    }

    @Override
    public Observable<EdsWithActivityList> getEdsWithActivityList(String awbNo) {
        return mDbHelper.getEdsWithActivityList(awbNo);
    }

    @Override
    public Observable<List<ForwardReasonCodeMaster>> getAllForwardUndeliveredReasonCode(String shipmentType, String isSecure, String isMps) {
        return mDbHelper.getAllForwardUndeliveredReasonCode(shipmentType, isSecure, isMps);
    }

    @Override
    public Observable<List<RVPReasonCodeMaster>> getAllRVPUndeliveredReasonCode(String isSecure) {
        return mDbHelper.getAllRVPUndeliveredReasonCode(isSecure);
    }

    @Override
    public Observable<List<String>> getSubgroupFromRvpReasonCode() {
        return mDbHelper.getSubgroupFromRvpReasonCode();
    }

    @Override
    public Observable<List<RVPReasonCodeMaster>> getSubReasonCodeFromSubGroup(String selectedReason) {
        return mDbHelper.getSubReasonCodeFromSubGroup(selectedReason);
    }

    @Override
    public Observable<List<SampleQuestion>> getRvpMasterDescriptions(List<RvpQualityCheck> rvpQualityCheckList) {
        return mDbHelper.getRvpMasterDescriptions(rvpQualityCheckList);
    }

    @Override
    public Observable<List<MasterActivityData>> getEDSMasterDescriptions(List<EDSActivityWizard> edsActivityWizards) {
        return mDbHelper.getEDSMasterDescriptions(edsActivityWizards);
    }

    @Override
    public Observable<List<ImageModel>> getImages(String awbNo) {
        return mDbHelper.getImages(awbNo);
    }

    @Override
    public Observable<Boolean> saveImage(ImageModel imageModel) {
        return mDbHelper.saveImage(imageModel);
    }

    @Override
    public Observable<Boolean> insertSathiLog(LiveTrackingLogTable liveTrackingLogTable) {
        return mDbHelper.insertSathiLog(liveTrackingLogTable);
    }

    @Override
    public Observable<Boolean> deleteLogs() {
        return mDbHelper.deleteLogs();
    }

    @Override
    public Observable<List<LiveTrackingLogTable>> getDataFromLiveTrackingLog() {
        return mDbHelper.getDataFromLiveTrackingLog();
    }

    @Override
    public Observable<Integer> getCountFromLiveTrackingLog() {
        return mDbHelper.getCountFromLiveTrackingLog();
    }

    @Override
    public Observable<Integer> deleteFistRowTable() {
        return mDbHelper.deleteFistRowTable();
    }

    @Override
    public Observable<Boolean> updateImageStatus(String imageName, int status) {
        return mDbHelper.updateImageStatus(imageName, status);
    }

    @Override
    public Observable<Boolean> updateImageID(String imageName, int imageID) {
        return mDbHelper.updateImageID(imageName, imageID);
    }

    @Override
    public Observable<Long> getisForwardCallattempted(long awb) {
        return mDbHelper.getisForwardCallattempted(awb);
    }

    @Override
    public Observable<Long> getisRVPCallattempted(long awb) {
        return mDbHelper.getisRVPCallattempted(awb);
    }

    @Override
    public Observable<Long> getisEDSCallattempted(Long awb) {
        return mDbHelper.getisEDSCallattempted(awb);
    }

    @Override
    public Observable<Boolean> reassignAll(long vwID) {
        return mDbHelper.reassignAll(vwID);
    }

    @Override
    public Observable<Boolean> markUndelivered(ShipmentsDetail... shipmentDetails) {
        return mDbHelper.markUndelivered(shipmentDetails);
    }

    @Override
    public Observable<List<RTSReasonCodeMaster>> getRTSReasonCodes() {
        return mDbHelper.getRTSReasonCodes();
    }

    @Override
    public Observable<Boolean> saveCommitPacket(PushApi pushApi) {
        return mDbHelper.saveCommitPacket(pushApi);
    }

    @Override
    public Observable<List<PushApi>> getPushApiUnSyncList() {
        return mDbHelper.getPushApiUnSyncList();
    }

    @Override
    public Observable<List<PushApi>> getSizeOFPushApi() {
        return mDbHelper.getSizeOFPushApi();
    }

    @Override
    public Observable<List<PushApi>> getAllPushApiData() {
        return mDbHelper.getAllPushApiData();
    }

    @Override
    public Observable<List<PushApi>> getPushApiAllPendingUnSyncList() {
        return mDbHelper.getPushApiAllPendingUnSyncList();
    }

    @Override
    public Observable<Boolean> getImageStatus(long awb) {
        return mDbHelper.getImageStatus(awb);
    }

    @Override
    public Observable<List<ImageModel>> getShipmentImageStatus(long awb) {
        return mDbHelper.getShipmentImageStatus(awb);
    }

    @Override
    public Observable<List<ImageModel>> getUnSyncImagesV2() {
        return mDbHelper.getUnSyncImagesV2();
    }

    @Override
    public Observable<MasterDocumentList> doDocumentListMasterCall(String s, String abc) {
        return mDbHelper.doDocumentListMasterCall(s, abc);
    }

    @Override
    public Observable<List<GeneralQuestion>> doOpvMasterCall() {
        return mDbHelper.doOpvMasterCall();
    }

    @Override
    public Observable<List<EDSReasonCodeMaster>> doEDSReasonMasterCode(String fliter) {
        return mDbHelper.doEDSReasonMasterCode(fliter);
    }

    @Override
    public Observable<List<EDSReasonCodeMaster>> doEDSReasonMasterCodeALL(HashSet<String> fliter) {
        return mDbHelper.doEDSReasonMasterCodeALL(fliter);
    }

    @Override
    public Observable<List<ImageModel>> getUnsynced(long awbNo) {
        return mDbHelper.getUnsynced(awbNo);
    }
    @Override
    public Observable<Boolean> deleteSyncedImage(String awbNo) {
        return mDbHelper.deleteSyncedImage(awbNo);
    }

    @Override
    public Observable<Boolean> deleteSyncedFWD(long awbNo) {
        return mDbHelper.deleteSyncedFWD(awbNo);
    }

    @Override
    public Observable<Boolean> insertRemark(Remark remark) {
        return mDbHelper.insertRemark(remark);
    }

    @Override
    public Observable<Long> getRemarksoneCount(String remark) {
        return mDbHelper.getRemarksoneCount(remark);
    }

    @Override
    public Observable<Long> getRemarkstwoCount(String remark) {
        return mDbHelper.getRemarkstwoCount(remark);
    }

    @Override
    public Observable<Long> getRemarksthreeCount(String remark) {
        return mDbHelper.getRemarksthreeCount(remark);
    }

    @Override
    public Observable<Long> getRemarksfourCount(String remark) {
        return mDbHelper.getRemarksfourCount(remark);
    }

    @Override
    public Observable<Long> getNoRemarksCount() {
        return mDbHelper.getNoRemarksCount();
    }

    @Override
    public Observable<Remark> getRemarks(long awb) {
        return mDbHelper.getRemarks(awb);
    }

    @Override
    public Observable<Boolean> deleteOldRemarks(long currentDate) {
        return mDbHelper.deleteOldRemarks(currentDate);
    }

    @Override
    public Observable<Boolean> deleteBasisAWB(long awb) {
        return mDbHelper.deleteBasisAWB(awb);
    }

    @Override
    public Observable<Boolean> deleteSyncedRemarks(int status) {
        return mDbHelper.deleteSyncedRemarks(status);
    }

    @Override
    public Observable<List<Remark>> getAllRemarks(String code, long currentDate) {
        return mDbHelper.getAllRemarks(code, currentDate);
    }

    @Override
    public Observable<List<ProfileFound>> getProfileFound() {
        return mDbHelper.getProfileFound();
    }

    @Override
    public Observable<EDSResponse> getEDS(String awb) {
        return mDbHelper.getEDS(awb);
    }

    @Override
    public Observable<Boolean> saveRTSConsigneeDetails(Details details) {
        return mDbHelper.saveRTSConsigneeDetails(details);
    }

    @Override
    public Observable<Boolean> updateEDSpinCallAttempted(Long pin, int isCallAttempted) {
        return mDbHelper.updateEDSpinCallAttempted(pin, isCallAttempted);
    }

    @Override
    public Observable<Boolean> updateRVPpinCallAttempted(Long pin, int isCallAttempted) {
        return mDbHelper.updateRVPpinCallAttempted(pin, isCallAttempted);
    }

    @Override
    public Observable<Boolean> updateForwardpinCallAttempted(Long pin, int isCallAttempted) {
        return mDbHelper.updateForwardpinCallAttempted(pin, isCallAttempted);
    }

    @Override
    public Observable<Boolean> updateSyncStatusFWD(String awb, int syncStatus) {
        return mDbHelper.updateSyncStatusFWD(awb, syncStatus);
    }

    @Override
    public Observable<Boolean> updateSameDayReassignSyncStatusFWD(String awb, boolean syncStatus) {
        return mDbHelper.updateSameDayReassignSyncStatusFWD(awb, syncStatus);
    }

    @Override
    public Observable<Boolean> updateSyncStatusEDS(String awb, int syncStatus) {
        return mDbHelper.updateSyncStatusEDS(awb, syncStatus);
    }

    @Override
    public Observable<Boolean> updateSyncStatusRVP(String awb, int syncStatus) {
        return mDbHelper.updateSyncStatusRVP(awb, syncStatus);
    }

    @Override
    public Observable<Boolean> updateSyncStatusRTS(long vendorID, int syncStatus) {
        return mDbHelper.updateSyncStatusRTS(vendorID, syncStatus);
    }

    @Override
    public Observable<Boolean> updateForwardMPSShipmentStatus(String[] awbArr, int shipmentUndeliveredStatus) {
        return mDbHelper.updateForwardMPSShipmentStatus(awbArr, shipmentUndeliveredStatus);
    }

    @Override
    public Single<ImageUploadResponse> doImageUploadApiCallTest(String token, String ecomregion, Map multipartBody) {
        return mApiHelper.doImageUploadApiCallTest(token, ecomregion, multipartBody);
    }

    @Override
    public Single<Call<ResponseBody>> getImage(String authToken, String ecomregion, String url) {
        return mApiHelper.getImage(authToken, ecomregion, url);
    }

    @Override
    public Single<GMapResponse> getGMap(String authToken, String ecomregion, GMapRequest gMapRequest) {
        return mApiHelper.getGMap(authToken, ecomregion, gMapRequest);
    }

    @Override
    public Observable<Response<ResponseBody>> getFile(String authToken, String ecomregion, String url) {
        return mApiHelper.getFile(authToken, ecomregion, url);
    }

    @Override
    public Single<LoginVerifyOtpResponse> doLoginResendOtpApiCall(String authToken, String ecomregion, LoginVerifyOtpRequest loginVerifyOtpRequest) {
        return mApiHelper.doLoginResendOtpApiCall(authToken, ecomregion, loginVerifyOtpRequest);
    }

    @Override
    public Single<LiveTrackingResponse> dogetLiveTrackingIDApiCall(String authToken, String ecomregion, LiveTrackingRequest liveTrackingRequest) {
        return mApiHelper.dogetLiveTrackingIDApiCall(authToken, ecomregion, liveTrackingRequest);
    }

    @Override
    public Single<LiveTrackingLogRespone> dosendLiveTrackingLog(String authToken, String ecomregion, List<LiveTrackingLogTable> liveTrackingLog) {
        return mApiHelper.dosendLiveTrackingLog(authToken, ecomregion, liveTrackingLog);
    }

    @Override
    public Single<SOSResponse> doSOSApiCall(String authToken, String ecomregion, SOSRequest sosRequest) {
        return mApiHelper.doSOSApiCall(authToken, ecomregion, sosRequest);
    }

    @Override
    public Single<LogFileUploadResponse> doAppLogsUploadApiCall(String ecomregion, Map headers, MultipartBody.Part file) {
        return mApiHelper.doAppLogsUploadApiCall(ecomregion, headers, file);
    }

    @Override
    public Single<UploadImageResponse> doUploadImageApiCall(String ecomregion, Map request, MultipartBody.Part file) throws IOException {
        return mApiHelper.doUploadImageApiCall(ecomregion, request, file);
    }

    @Override
    public Single<DisputeImageApiResponse> doUploadDisputeImageApiCall(String ecomregion, Map request, MultipartBody.Part file) throws IOException {
        return mApiHelper.doUploadDisputeImageApiCall(ecomregion, request, file);
    }

    @Override
    public Single<ImageQualityResponse> getImageQuality(String ecomregion, int image_id) throws IOException {
        return mApiHelper.getImageQuality(ecomregion, image_id);
    }

    @Override
    public Single<ForwardCallResponse> doForwardCallStatusApiCall(String auth_token, String ecomregion, int counter, String emp_code, String awb_number, String drs_id, int shipper_id) throws IOException {
        return mApiHelper.doForwardCallStatusApiCall(auth_token, ecomregion, counter, emp_code, awb_number, drs_id, shipper_id);
    }

    @Override
    public Single<IncentiveResponse> doFEIncentiveApiCall(String url, String emp_code) throws IOException {
        return mApiHelper.doFEIncentiveApiCall(url, emp_code);
    }

    public String getWebLinkUrl() {
        return mPreferencesHelper.getWebLinkUrl();
    }

    public void setWebLinkUrl(String webLinkUrl) {
        mPreferencesHelper.setWebLinkUrl(webLinkUrl);
    }

    @Override
    public float getLIVETRACKINGLATLNGACCURACYl() {
        return mPreferencesHelper.getLIVETRACKINGLATLNGACCURACYl();
    }

    @Override
    public void setLIVETRACKINGLATLNGACCURACY(String livetrackinglatlngaccuracy) {
        mPreferencesHelper.setLIVETRACKINGLATLNGACCURACY(livetrackinglatlngaccuracy);
    }

    @Override
    public void setStartStopTripMeterReadingDiff(int readingDiff) {
        mPreferencesHelper.setStartStopTripMeterReadingDiff(readingDiff);
    }

    @Override
    public String getTripGeofencing() {
        return mPreferencesHelper.getTripGeofencing();
    }

    @Override
    public void setTripGeofencing(String tripGeofencing) {
        mPreferencesHelper.setTripGeofencing(tripGeofencing);
    }

    @Override
    public int getStartStopMeterReadingDiff() {
        return mPreferencesHelper.getStartStopMeterReadingDiff();
    }

    @Override
    public int getSelectedDRSView() {
        return mPreferencesHelper.getSelectedDRSView();
    }

    @Override
    public void setSelectedDRSView(int index) {
        mPreferencesHelper.setSelectedDRSView(index);
    }

    @Override
    public int getSelectedSorting() {
        return mPreferencesHelper.getSelectedSorting();
    }

    @Override
    public void setSelectedSorting(int index) {
        mPreferencesHelper.setSelectedSorting(index);
    }

    @Override
    public String getSOSNumbers() {
        return mPreferencesHelper.getSOSNumbers();
    }

    @Override
    public void setSOSNumbers(String sosNumbers) {
        mPreferencesHelper.setSOSNumbers(sosNumbers);
    }

    @Override
    public String getSOSSMSTemplate() {
        return mPreferencesHelper.getSOSSMSTemplate();
    }

    @Override
    public void setSOSSMSTemplate(String template) {
        mPreferencesHelper.setSOSSMSTemplate(template);
    }

    @Override
    public boolean getFEReachedDC() {
        return mPreferencesHelper.getFEReachedDC();
    }

    @Override
    public void setFEReachedDC(boolean b) {
        mPreferencesHelper.setFEReachedDC(b);
    }

    @Override
    public int getFWDUnattemptedReasonCode() {
        return mPreferencesHelper.getFWDUnattemptedReasonCode();
    }

    @Override
    public void setFWDUnattemptedReasonCode(int reasonCode) {
        mPreferencesHelper.setFWDUnattemptedReasonCode(reasonCode);
    }

    @Override
    public int getRVPUnattemptedReasonCode() {
        return mPreferencesHelper.getRVPUnattemptedReasonCode();
    }

    @Override
    public void setRVPUnattemptedReasonCode(int reasonCode) {
        mPreferencesHelper.setRVPUnattemptedReasonCode(reasonCode);
    }

    @Override
    public int getRTSUnattemptedReasonCode() {
        return mPreferencesHelper.getRTSUnattemptedReasonCode();
    }

    @Override
    public void setRTSUnattemptedReasonCode(int reasonCode) {
        mPreferencesHelper.setRTSUnattemptedReasonCode(reasonCode);
    }

    @Override
    public int getEDSUnattemptedReasonCode() {
        return mPreferencesHelper.getEDSUnattemptedReasonCode();
    }

    @Override
    public void setEDSUnattemptedReasonCode(int reasonCode) {
        mPreferencesHelper.setEDSUnattemptedReasonCode(reasonCode);
    }

    @Override
    public int getImageQualityId() {
        return mPreferencesHelper.getImageQualityId();
    }

    @Override
    public void setImageQualityId(int image_id) {
        mPreferencesHelper.setImageQualityId(image_id);
    }

    @Override
    public void setConsigneeProfiling(boolean enable) {
        mPreferencesHelper.setConsigneeProfiling(enable);
    }
    @Override
    public Single<IciciResponse> doICICIApiCall(String token, String ecomregion, String url, RequestBody pid) {
        return mApiHelper.doICICIApiCall(token, ecomregion, url, pid);
    }
    @Override
    public Single<IciciResponse> doICICICheckcStatusCall(String token, String ecomregion, String url, RequestBody urn) {
        return mApiHelper.doICICICheckcStatusCall(token, ecomregion, url, urn);
    }

    @Override
    public Single<IciciResponse_standard> doICICICheckcStatusCallStandard(String apikey, String ecomregion, String url, RequestBody urn) {
        return mApiHelper.doICICICheckcStatusCallStandard(apikey, ecomregion, url, urn);
    }

    @Override
    public Single<ImageUploadResponse> doImageByteUploadApiCall(String authToken, String ecomregion, String imageType, Map headers, ByteImageRequest data) {
        return mApiHelper.doImageByteUploadApiCall(authToken, ecomregion, imageType, headers, data);
    }

    @Override
    public boolean getConsigneeProfiling() {
        return mPreferencesHelper.getConsigneeProfiling();
    }

    @Override
    public boolean getRootDeviceDisabled() {
        return mPreferencesHelper.getRootDeviceDisabled();
    }

    @Override
    public void setRoodDeviceDisabled(boolean disabled) {
        mPreferencesHelper.setRoodDeviceDisabled(disabled);
    }

    @Override
    public void setActivityCode(String activityCode) {
        mPreferencesHelper.setActivityCode(activityCode);
    }

    @Override
    public String getActivityCode() {
        return mPreferencesHelper.getActivityCode();
    }

    @Override
    public void saveEDSRealTimeSync(String status) {
        mPreferencesHelper.saveEDSRealTimeSync(status);
    }

    @Override
    public String getEDSRealTimeSync() {
        return mPreferencesHelper.getEDSRealTimeSync();
    }

    @Override
    public void saveRVPRealTimeSync(String status) {
        mPreferencesHelper.saveRVPRealTimeSync(status);
    }

    @Override
    public String getRVPRealTimeSync() {
        return mPreferencesHelper.getRVPRealTimeSync();
    }

    @Override
    public void setEmp_code(String emp_code) {
        mPreferencesHelper.setEmp_code(emp_code);
    }

    @Override
    public String getEmp_code() {
        return mPreferencesHelper.getEmp_code();
    }

    @Override
    public void setConsigneeProfileValue(String value) {
        mPreferencesHelper.setConsigneeProfileValue(value);
    }

    @Override
    public String getConsigneeProfileValue() {
        return mPreferencesHelper.getConsigneeProfileValue();
    }

    @Override
    public void setStopTripDate(long current_date) {
        mPreferencesHelper.setStopTripDate(current_date);
    }

    @Override
    public long getStopTripDate() {
        return mPreferencesHelper.getStopTripDate();
    }

    @Override
    public long getEndTripTime() {
        return mPreferencesHelper.getEndTripTime();
    }

    @Override
    public void setEndTripTime(long end_trip_time) {
        mPreferencesHelper.setEndTripTime(end_trip_time);
    }

    @Override
    public long getEndTripKm() {
        return mPreferencesHelper.getEndTripKm();
    }

    @Override
    public void setEndTripKm(long end_trip_km) {
        mPreferencesHelper.setEndTripKm(end_trip_km);
    }

    @Override
    public void setDcLatitude(String latitude) {
        mPreferencesHelper.setDcLatitude(latitude);
    }

    @Override
    public String getDcLatitude() {
        return mPreferencesHelper.getDcLatitude();
    }

    @Override
    public void setDcLongitude(String longitude) {
        mPreferencesHelper.setDcLongitude(longitude);
    }

    @Override
    public String getDcLongitude() {
        return mPreferencesHelper.getDcLongitude();
    }

    @Override
    public Observable<Boolean> insertAllApiUrls(List<ApiUrlData> apiUrlData) {
        return mDbHelper.insertAllApiUrls(apiUrlData);
    }

    @Override
    public Observable<List<ApiUrlData>> getAllApiUrl() {
        return mDbHelper.getAllApiUrl();
    }

    @Override
    public Observable<Boolean> insertFwdShipperId(Forward forward) {
        return mDbHelper.insertFwdShipperId(forward);
    }

    @Override
    public Observable<Forward> getFwdShipperId() {
        return mDbHelper.getFwdShipperId();
    }

    @Override
    public Observable<Boolean> insertSmsLink(MsgLinkData msgLinkData) {
        return mDbHelper.insertSmsLink(msgLinkData);
    }

    @Override
    public Observable<Boolean> updateMobileList(ArrayList<String> mobile_number, String awbNo) {
        return mDbHelper.updateMobileList(mobile_number, awbNo);
    }

    @Override
    public Observable<MsgLinkData> getSmsLink(String awb) {
        return mDbHelper.getSmsLink(awb);
    }

    @Override
    public boolean getImageStatusSingleThread(long awbNo) {
        return mDbHelper.getImageStatusSingleThread(awbNo);
    }

    @Override
    public boolean saveImageSingleThread(ImageModel imageModel) {
        return mDbHelper.saveImageSingleThread(imageModel);
    }

    @Override
    public Observable<PushApi> getUnSyncPushAPIPacket(long awbNo) {
        return mDbHelper.getUnSyncPushAPIPacket(awbNo);
    }

    @Override
    public Observable<ProfileFound> getProfileLat(long awbno) {
        return mDbHelper.getProfileLat(awbno);
    }
    @Override
    public Observable<ShipmentsDetail> getShipmentData(long scannedAwbNo) {
        return mDbHelper.getShipmentData(scannedAwbNo);
    }

    @Override
    public Observable<List<RescheduleEdsD>> getEdsRescheduleFlag() {
        return mDbHelper.getEdsRescheduleFlag();
    }

    @Override
    public Observable<Boolean> insertEdsRescheduleFlag(ArrayList<RescheduleEdsD> edsRescheduleResponse) {
        return mDbHelper.insertEdsRescheduleFlag(edsRescheduleResponse);
    }

    @Override
    public Observable<Boolean> deleteEdsRescheduleFlag() {
        return mDbHelper.deleteEdsRescheduleFlag();
    }

    @Override
    public Observable<List<PushApi>> insetedOrNotinTable(long awbno) {
        return mDbHelper.insetedOrNotinTable(awbno);
    }

    @Override
    public Observable<PushApi> getPushApiDetail(long awbno) {
        return mDbHelper.getPushApiDetail(awbno);
    }

    @Override
    public int getDCRANGE() {
        return mPreferencesHelper.getDCRANGE();
    }

    @Override
    public void setDCRANGE(int range) {
        mPreferencesHelper.setDCRANGE(range);
    }

    @Override
    public int getREQUEST_RESPONSE_TIME() {
        return mPreferencesHelper.getREQUEST_RESPONSE_TIME();
    }

    @Override
    public void setREQUEST_RESPONSE_TIME(int time) {
        mPreferencesHelper.setREQUEST_RESPONSE_TIME(time);
    }

    @Override
    public int getREQUEST_RESPONSE_COUNT() {
        return mPreferencesHelper.getREQUEST_RESPONSE_COUNT();
    }

    @Override
    public void setREQUEST_RESPONSE_COUNT(int count) {
        mPreferencesHelper.setREQUEST_RESPONSE_COUNT(count);
    }

    @Override
    public boolean isDCLocationUpdateAllowed() {
        return mPreferencesHelper.isDCLocationUpdateAllowed();
    }

    @Override
    public void SetisDCLocationUpdateAllowed(boolean status) {
        mPreferencesHelper.SetisDCLocationUpdateAllowed(status);
    }

    @Override
    public void savePrivateKey(String privateKey) {
        mPreferencesHelper.savePrivateKey(privateKey);
    }

    @Override
    public String getPrivateKey() {
        return mPreferencesHelper.getPrivateKey();
    }

    @Override
    public int getMaxDailyDiffForStartTrip() {
        return mPreferencesHelper.getMaxDailyDiffForStartTrip();
    }

    @Override
    public void setMaxDailyDiffForStartTrip(String configValue) {
        mPreferencesHelper.setMaxDailyDiffForStartTrip(configValue);
    }

    @Override
    public int getMaxTripRunForStopTrip() {
        return mPreferencesHelper.getMaxTripRunForStopTrip();
    }

    @Override
    public void setMaxTripRunForStopTrip(String configValue) {
        mPreferencesHelper.setMaxTripRunForStopTrip(configValue);
    }

    @Override
    public void setInternetApiAvailable(String flag) {
        mPreferencesHelper.setInternetApiAvailable(flag);
    }

    @Override
    public String getInternetApiAvailable() {
        return mPreferencesHelper.getInternetApiAvailable();
    }

    @Override
    public void setENABLEDIRECTDIAL(String flag) {
        mPreferencesHelper.setENABLEDIRECTDIAL(flag);
    }

    @Override
    public String getENABLEDIRECTDIAL() {
        return mPreferencesHelper.getENABLEDIRECTDIAL();
    }


    @Override
    public void setENABLERTSEMAIL(String flag) {
        mPreferencesHelper.setENABLERTSEMAIL(flag);
    }

    @Override
    public String getENABLERTSEMAIL() {
        return mPreferencesHelper.getENABLERTSEMAIL();
    }

    @Override
    public void setSAMEDAYRESCHEDULE(String flag) {
        mPreferencesHelper.setSAMEDAYRESCHEDULE(flag);
    }

    @Override
    public String getSAMEDAYRESCHEDULE() {
        return mPreferencesHelper.getSAMEDAYRESCHEDULE();
    }

    @Override
    public void setRTSIMAGE(String flag) {
        mPreferencesHelper.setRTSIMAGE(flag);
    }

    @Override
    public String getRTSIMAGE() {
        return mPreferencesHelper.getRTSIMAGE();
    }

    @Override
    public void setDEFAULTSTATISTICS(String flag) {
        mPreferencesHelper.setDEFAULTSTATISTICS(flag);
    }

    @Override
    public String getDEFAULTSTATISTICS() {
        return mPreferencesHelper.getDEFAULTSTATISTICS();
    }

    @Override
    public void setDuplicateCashReceipt(String configValue) {
        mPreferencesHelper.setDuplicateCashReceipt(configValue);
    }

    @Override
    public String getDuplicateCashReceipt() {
        return mPreferencesHelper.getDuplicateCashReceipt();
    }

    @Override
    public void setAmazonOTPStatus(String b) {
        mPreferencesHelper.setAmazonOTPStatus(b);
    }

    @Override
    public void setAmazonOTPTiming(long timeInMillis) {
        mPreferencesHelper.setAmazonOTPTiming(timeInMillis);
    }

    @Override
    public String getAmazonOTPStatus() {
        return mPreferencesHelper.getAmazonOTPStatus();
    }

    @Override
    public long getAmazonOTPTiming() {
        return mPreferencesHelper.getAmazonOTPTiming();
    }

    @Override
    public void setPinBOTPStatus(String b) {
        mPreferencesHelper.setPinBOTPStatus(b);
    }

    @Override
    public void setAmazonOTPValue(String otp_value) {
        mPreferencesHelper.setAmazonOTPValue(otp_value);
    }

    @Override
    public void setAmazonPinbValue(String pinb_value) {
        mPreferencesHelper.setAmazonPinbValue(pinb_value);
    }

    @Override
    public void setAmazonList(String amazonList) {
        mPreferencesHelper.setAmazonList(amazonList);
    }

    @Override
    public String getAmazonList() {
        return mPreferencesHelper.getAmazonList();
    }

    @Override
    public String getAmazonOTPValue() {
        return mPreferencesHelper.getAmazonOTPValue();
    }

    @Override
    public String getAmazonPinbValue() {
        return mPreferencesHelper.getAmazonPinbValue();
    }

    @Override
    public void setPinBOTPTimming(long timeInMillis) {
        mPreferencesHelper.setPinBOTPTimming(timeInMillis);
    }

    @Override
    public String getPinBOTPStatus() {
        return mPreferencesHelper.getPinBOTPStatus();
    }

    @Override
    public long getPinBOTPTimming() {
        return mPreferencesHelper.getPinBOTPTimming();
    }

    @Override
    public void setSyncDelay(long configValue) {
        mPreferencesHelper.setSyncDelay(configValue);
    }

    @Override
    public long getSyncDelay() {
        return mPreferencesHelper.getSyncDelay();
    }

    @Override
    public void setLoginDate(String date) {
        mPreferencesHelper.setLoginDate(date);
    }

    @Override
    public String getLoginDate() {
        return mPreferencesHelper.getLoginDate();
    }

    @Override
    public void setIsScanAwb(boolean isScanAwb) {
        mPreferencesHelper.setIsScanAwb(isScanAwb);
    }

    @Override
    public void setDcUndeliverStatus(boolean isDcUndeliver) {
        mPreferencesHelper.setDcUndeliverStatus(isDcUndeliver);
    }

    @Override
    public boolean getIsScanAwb() {
        return mPreferencesHelper.getIsScanAwb();
    }

    @Override
    public boolean getDcUndeliverStatus() {
        return mPreferencesHelper.getDcUndeliverStatus();
    }

    @Override
    public void setAdharConsent(String aadhaar_consent) {
        mPreferencesHelper.setAdharConsent(aadhaar_consent);
    }

    @Override
    public void setLiveTrackingTripIdForApi(String live_tracking_trip_id) {
        mPreferencesHelper.setLiveTrackingTripIdForApi(live_tracking_trip_id);
    }

    @Override
    public String getLiveTrackingTripIdForApi() {
        return mPreferencesHelper.getLiveTrackingTripIdForApi();
    }

    @Override
    public String getAdharConsent() {
        return mPreferencesHelper.getAdharConsent();
    }

    @Override
    public void setMAP_DRIVING_MODE(String mapDrivingMode) {
        mPreferencesHelper.setMAP_DRIVING_MODE(mapDrivingMode);
    }

    @Override
    public String getMAP_DRIVING_MODE() {
        return mPreferencesHelper.getMAP_DRIVING_MODE();
    }

    @Override
    public void setLiveTrackingTripId(String live_tracking_trip_id) {
        mPreferencesHelper.setLiveTrackingTripId(live_tracking_trip_id);
    }

    @Override
    public String getLiveTrackingTripId() {
        return mPreferencesHelper.getLiveTrackingTripId();
    }

    @Override
    public void setLiveTrackingMaxFileSize(int parseInt) {
        mPreferencesHelper.setLiveTrackingMaxFileSize(parseInt);
    }

    @Override
    public int getLiveTrackingMaxFileSize() {
        return mPreferencesHelper.getLiveTrackingMaxFileSize();
    }

    @Override
    public void setAdharMessage(String configValue) {
        mPreferencesHelper.setAdharMessage(configValue);
    }

    @Override
    public String getAdharMessage() {
        return mPreferencesHelper.getAdharMessage();
    }

    @Override
    public void setRVPAWBWords(String configValue) {
        mPreferencesHelper.setRVPAWBWords(configValue);
    }

    @Override
    public String getRVPAWBWords() {
        return mPreferencesHelper.getRVPAWBWords();
    }

    @Override
    public void setRVP_UD_FLYER(String flyerValue) {
        mPreferencesHelper.setRVP_UD_FLYER(flyerValue);
    }

    @Override
    public String getRVP_UD_FLYER() {
        return mPreferencesHelper.getRVP_UD_FLYER();
    }

    @Override
    public void setAadharFrontImage(String front_image_id) {
        mPreferencesHelper.setAadharFrontImage(front_image_id);
    }

    @Override
    public void setAadharRearImage(String rear_image_id) {
        mPreferencesHelper.setAadharRearImage(rear_image_id);
    }

    @Override
    public String getAadharFrontImage() {
        return mPreferencesHelper.getAadharFrontImage();
    }

    @Override
    public String getAadharRearImage() {
        return mPreferencesHelper.getAadharRearImage();
    }

    @Override
    public void setAadharStatusInterval(String configValue) {
        mPreferencesHelper.setAadharStatusInterval(configValue);
    }

    @Override
    public String getAadharStatusInterval() {
        return mPreferencesHelper.getAadharStatusInterval();
    }

    @Override
    public void setAadharStatus(boolean b) {
        mPreferencesHelper.setAadharStatus(b);
    }

    @Override
    public boolean getAadharStatus() {
        return mPreferencesHelper.getAadharStatus();
    }

    @Override
    public void setAadharStatusCode(int i) {
        mPreferencesHelper.setAadharStatusCode(i);
    }

    @Override
    public int getAadharStatusCode() {
        return mPreferencesHelper.getAadharStatusCode();
    }

    @Override
    public void setStopTrackingAlertFlag(String s) {
        mPreferencesHelper.setStopTrackingAlertFlag(s);
    }

    @Override
    public String getStopTrackingAlertFlag() {
        return mPreferencesHelper.getStopTrackingAlertFlag();
    }

    @Override
    public void setEdsActivityCodes(Set<String> all_edsactivity_codes) {
        mPreferencesHelper.setEdsActivityCodes(all_edsactivity_codes);
    }

    @Override
    public Set<String> getEdsActivityCodes() {
        return mPreferencesHelper.getEdsActivityCodes();
    }

    @Override
    public void setDRSTimeStap(long time_stamp) {
        mPreferencesHelper.setDRSTimeStap(time_stamp);
    }

    @Override
    public long getDRSTimeStamp() {
        return mPreferencesHelper.getDRSTimeStamp();
    }

    @Override
    public void setPreviousTripStatus(Boolean previousTripStatus) {
        mPreferencesHelper.setPreviousTripStatus(previousTripStatus);
    }

    @Override
    public Boolean getPreviosTripStatus() {
        return mPreferencesHelper.getPreviosTripStatus();
    }

    @Override
    public Single<ekycXMLResponse> doEkycXMLApiCall(String ecomregion, String jobj, HashMap<String, String> webheader) {
        return mApiHelper.doEkycXMLApiCall(ecomregion, jobj, webheader);
    }

    @Override
    public Single<Biometric_response> dobiometricApiCall(String token, String ecomregion, Biometric_requestdata biometric_requestdata) {
        return mApiHelper.dobiometricApiCall(token, ecomregion, biometric_requestdata);
    }

    @Override
    public Single<DrsCheckListResponse> doDrsListCheck(String authToken, String ecomregion, DrsCheckListRequest drsCheckListRequest) {
        return mApiHelper.doDrsListCheck(authToken, ecomregion, drsCheckListRequest);
    }

    @Override
    public Single<AdharApiResponse> doAdharApi(String authToken, String ecomregion, AdharRequest adharRequest) {
        return mApiHelper.doAdharApi(authToken, ecomregion, adharRequest);
    }

    @Override
    public Single<CovidApiResponse> doCovidApi(String authToken, String ecomregion, CovidRequest covidRequest) {
        return mApiHelper.doCovidApi(authToken, ecomregion, covidRequest);
    }

    @Override
    public Single<DCLocationUpdateResponse> doDCLocationUpdateApiCall(String authToken, String ecomregion, DCLocationUpdate dcLocationUpdate) {
        return mApiHelper.doDCLocationUpdateApiCall(authToken, ecomregion, dcLocationUpdate);
    }

    @Override
    public Single<Amazon_reschedule_list> doScheduleDates(String authToken, String ecomregion, AmazonScheduleRequest strings) {
        return mApiHelper.doScheduleDates(authToken, ecomregion, strings);
    }


    @Override
    public Single<CashReceipt_Response> doCashReceiptCall(String authToken, String ecomregion, CashReceipt_Request request) {
        return mApiHelper.doCashReceiptCall(authToken, ecomregion, request);
    }

    @Override
    public Single<EdsRescheduleResponse> doEdsRescheduleCall(String authToken, String ecomregion, EdsRescheduleRequest request) {
        return mApiHelper.doEdsRescheduleCall(authToken, ecomregion, request);
    }

    @Override
    public void setLatLngLimit(String configValue) {
        mPreferencesHelper.setLatLngLimit(configValue);
    }

    @Override
    public String getLatLngLimit() {
        return mPreferencesHelper.getLatLngLimit();
    }

    @Override
    public void setLoginMonth(int mMonth) {
        mPreferencesHelper.setLoginMonth(mMonth);
    }

    @Override
    public int getLoginMonth() {
        return mPreferencesHelper.getLoginMonth();
    }

    @Override
    public void setAadharFrontImageName(String aadhar_front_image) {
        mPreferencesHelper.setAadharFrontImageName(aadhar_front_image);
    }

    @Override
    public void setAadharRearImageName(String aadhar_rear_image) {
        mPreferencesHelper.setAadharRearImageName(aadhar_rear_image);
    }

    @Override
    public void setUndeliverCount(int configValue) {
        mPreferencesHelper.setUndeliverCount(configValue);
    }

    @Override
    public int getUndeliverCount() {
        return mPreferencesHelper.getUndeliverCount();
    }

    @Override
    public void setShipperId(int shipper_id) {
        mPreferencesHelper.setShipperId(shipper_id);
    }

    @Override
    public int getShipperId() {
        return mPreferencesHelper.getShipperId();
    }

    @Override
    public void setDirectionDistance(double distance) {
        mPreferencesHelper.setDirectionDistance(distance);
    }

    @Override
    public void setDirectionTotalDistance(double distance) {
        mPreferencesHelper.setDirectionTotalDistance(distance);
    }

    @Override
    public double getDirectionDistance() {
        return mPreferencesHelper.getDirectionDistance();
    }

    @Override
    public double getDirectionTotalDistance() {
        return mPreferencesHelper.getDirectionTotalDistance();
    }

    @Override
    public void setUndeliverConsigneeRANGE(int parseInt) {
        mPreferencesHelper.setUndeliverConsigneeRANGE(parseInt);
    }

    @Override
    public int getUndeliverConsigneeRANGE() {
        return mPreferencesHelper.getUndeliverConsigneeRANGE();
    }

    @Override
    public void setIsSignatureImageMandatory(String configValue) {
        mPreferencesHelper.setIsSignatureImageMandatory(configValue);
    }

    @Override
    public String getIsSignatureImageMandatory() {
        return mPreferencesHelper.getIsSignatureImageMandatory();
    }

    @Override
    public void setEDISPUTE(String configValue) {
        mPreferencesHelper.setEDISPUTE(configValue);
    }

    @Override
    public String getEDispute() {
        return mPreferencesHelper.getEDispute();
    }

    @Override
    public void setIsCallBridgeCheckStatus(String configValue) {
        mPreferencesHelper.setEDISPUTE(configValue);
    }

    @Override
    public String getIsCallBridgeCheckStatus() {
        return mPreferencesHelper.getEDispute();
    }


    @Override
    public void setCovidConset(boolean configValue) {
        mPreferencesHelper.setCovidConset(configValue);
    }

    @Override
    public boolean getCovidConset() {
        return mPreferencesHelper.getCovidConset();
    }

    @Override
    public void setMaxEDSFailAttempt(int configValue) {
        mPreferencesHelper.setMaxEDSFailAttempt(configValue);
    }

    @Override
    public int getMaxEDSFailAttempt() {
        return mPreferencesHelper.getMaxEDSFailAttempt();
    }

    @Override
    public void setLocationCount(int count) {
        mPreferencesHelper.setLocationCount(count);
    }

    @Override
    public int getLocationCount() {
        return mPreferencesHelper.getLocationCount();
    }

    @Override
    public void setLiveTrackingSpeed(String configValue) {
        mPreferencesHelper.setLiveTrackingSpeed(configValue);
    }

    @Override
    public void setIsAdmEmp(boolean flag) {
        mPreferencesHelper.setIsAdmEmp(flag);
    }

    @Override
    public boolean getIsAdmEmp() {
        return mPreferencesHelper.getIsAdmEmp();
    }

    @Override
    public void setFWDPrice(float price) {
        mPreferencesHelper.setFWDPrice(price);
    }

    @Override
    public float getFWDPrice() {
        return mPreferencesHelper.getFWDPrice();
    }

    @Override
    public void setPPDPrice(float price) {
        mPreferencesHelper.setPPDPrice(price);
    }

    @Override
    public float getPPDPrice() {
        return mPreferencesHelper.getPPDPrice();
    }

    @Override
    public void setCODPrice(float price) {
        mPreferencesHelper.setCODPrice(price);
    }

    @Override
    public float getCODPrice() {
        return mPreferencesHelper.getCODPrice();
    }

    @Override
    public void setRQCPrice(float price) {
        mPreferencesHelper.setRQCPrice(price);
    }

    @Override
    public float getRQCPrice() {
        return mPreferencesHelper.getRQCPrice();
    }

    @Override
    public void setEDSPrice(float price) {

        mPreferencesHelper.setEDSPrice(price);
    }

    @Override
    public float getEDSPrice() {
        return mPreferencesHelper.getEDSPrice();
    }

    @Override
    public void setRVPPrice(float price) {
        mPreferencesHelper.setRVPPrice(price);
    }

    @Override
    public float getRVPPrice() {
        return mPreferencesHelper.getRVPPrice();
    }

    @Override
    public void setRTSPrice(float price) {

        mPreferencesHelper.setRTSPrice(price);
    }

    @Override
    public float getRTSPrice() {
        return mPreferencesHelper.getRTSPrice();
    }

    @Override
    public void setCurrentTripAmount(String amount) {

        mPreferencesHelper.setCurrentTripAmount(amount);
    }

    @Override
    public String getCurrentTripAmount() {
        return mPreferencesHelper.getCurrentTripAmount();
    }

    @Override
    public void setDRSId(long id) {
        mPreferencesHelper.setDRSId(id);
    }

    @Override
    public long getDRSId() {
        return mPreferencesHelper.getDRSId();
    }

    @Override
    public void setIsQRCodeScanned(boolean b) {
        mPreferencesHelper.setIsQRCodeScanned(b);
    }

    @Override
    public boolean getIsQRCodeScanned() {
        return mPreferencesHelper.getIsQRCodeScanned();
    }

    @Override
    public String getCallStatusApiInterval() {
        return mPreferencesHelper.getCallStatusApiInterval();
    }

    @Override
    public void setCallStatusApiInterval(String interval) {
        mPreferencesHelper.setCallStatusApiInterval(interval);
    }

    @Override
    public boolean getDirectUndeliver() {
        return mPreferencesHelper.getDirectUndeliver();
    }

    @Override
    public void setDirectUndeliver(boolean b) {
        mPreferencesHelper.setDirectUndeliver(b);
    }

    @Override
    public boolean isCallStatusAPIRecursion() {
        return mPreferencesHelper.isCallStatusAPIRecursion();
    }

    @Override
    public void setCallAPIRecursion(boolean b) {
        mPreferencesHelper.setCallAPIRecursion(b);
    }

    @Override
    public long getRequestRsponseTime() {
        return mPreferencesHelper.getRequestRsponseTime();
    }

    @Override
    public void setRequestResponseTime(long time) {
        mPreferencesHelper.setRequestResponseTime(time);
    }

    @Override
    public void setEnableDPEmployee(boolean parseLong) {
        mPreferencesHelper.setEnableDPEmployee(parseLong);
    }

    @Override
    public boolean getEnableDPEmployee() {
        return mPreferencesHelper.getEnableDPEmployee();
    }

    @Override
    public void setCounterDelivery(boolean b) {
        mPreferencesHelper.setCounterDelivery(b);
    }

    @Override
    public boolean isCounterDelivery() {
        return mPreferencesHelper.isCounterDelivery();
    }

    @Override
    public void setCodStatusInterval(long parseBoolean) {
        mPreferencesHelper.setCodStatusInterval(parseBoolean);
    }

    @Override
    public long getCodStatusInterval() {
        return mPreferencesHelper.getCodStatusInterval();
    }

    @Override
    public void setCodStatusIntervalStatusFraction(int parseBoolean) {
        mPreferencesHelper.setCodStatusIntervalStatusFraction(parseBoolean);
    }

    @Override
    public int getCodeStatusIntervalFraction() {
        return mPreferencesHelper.getCodeStatusIntervalFraction();
    }

    @Override
    public void setRescheduleMaxAttempts(int parseInt) {
        mPreferencesHelper.setRescheduleMaxAttempts(parseInt);
    }

    @Override
    public int getRescheduleMaxAttempts() {
        return mPreferencesHelper.getRescheduleMaxAttempts();
    }

    @Override
    public void setRescheduleMaxDaysAllow(int parseInt) {
        mPreferencesHelper.setRescheduleMaxDaysAllow(parseInt);
    }

    @Override
    public int getRescheduleMaxDaysAllow() {
        return mPreferencesHelper.getRescheduleMaxDaysAllow();
    }

    @Override
    public boolean isUseCamscannerPrintReceipt() {
        return mPreferencesHelper.isUseCamscannerPrintReceipt();
    }

    @Override
    public void setIsUseCamscannerPrintReceipt(boolean val) {
        mPreferencesHelper.setIsUseCamscannerPrintReceipt(val);
    }

    @Override
    public boolean isUseCamscannerDispute() {
        return mPreferencesHelper.isUseCamscannerDispute();
    }

    @Override
    public void setIsUseCamscannerDispute(boolean val) {
        mPreferencesHelper.setIsUseCamscannerDispute(val);
    }

    @Override
    public boolean isUseCamscannerTrip() {
        return mPreferencesHelper.isUseCamscannerTrip();
    }

    @Override
    public void setIsUseCamscannerTrip(boolean val) {
        mPreferencesHelper.setIsUseCamscannerTrip(val);
    }

    @Override
    public void setSathiLogApiCallInterval(long parseLong) {
        mPreferencesHelper.setSathiLogApiCallInterval(parseLong);
    }

    @Override
    public long getSathiLogApiCallInterval() {
        return mPreferencesHelper.getSathiLogApiCallInterval();
    }

    @Override
    public double getLiveTrackingSpeed() {
        return mPreferencesHelper.getLiveTrackingSpeed();
    }

    @Override
    public void setLiveTrackingAccuracy(String configValue) {
        mPreferencesHelper.setLiveTrackingAccuracy(configValue);
    }

    @Override
    public int getLiveTrackingAccuracy() {
        return mPreferencesHelper.getLiveTrackingAccuracy();
    }

    @Override
    public void setLiveTrackingDisplacement(String configValue) {
        mPreferencesHelper.setLiveTrackingDisplacement(configValue);
    }

    @Override
    public float getLiveTrackingDisplacement() {
        return mPreferencesHelper.getLiveTrackingDisplacement();
    }

    @Override
    public void setLiveTrackingInterval(String configValue) {
        mPreferencesHelper.setLiveTrackingInterval(configValue);
    }

    @Override
    public long getLiveTrackingInterval() {
        return mPreferencesHelper.getLiveTrackingInterval();
    }

    @Override
    public void setLiveTrackingMINSpeed(String configValue) {
        mPreferencesHelper.setLiveTrackingMINSpeed(configValue);
    }

    @Override
    public double getLiveTrackingMINSpeed() {
        return mPreferencesHelper.getLiveTrackingMINSpeed();
    }

    @Override
    public void setCovidUrl(String covid_image_url) {
        mPreferencesHelper.setCovidUrl(covid_image_url);
    }

    @Override
    public String getCovidUrl() {
        return mPreferencesHelper.getCovidUrl();
    }

    @Override
    public void setEcomRegion(String ecom_dlv_region) {
        mPreferencesHelper.setEcomRegion(ecom_dlv_region);
    }

    @Override
    public String getEcomRegion() {
        return mPreferencesHelper.getEcomRegion();
    }

    @Override
    public void setMessageClicked(String awb, boolean val) {
        mPreferencesHelper.setMessageClicked(awb, val);
    }

    @Override
    public void setCardClicked(String awb, boolean val) {
        mPreferencesHelper.setCardClicked(awb, val);
    }

    @Override
    public boolean getMessageClicked(String awb) {
        return mPreferencesHelper.getMessageClicked(awb);
    }

    @Override
    public boolean getCardClicked(String awb) {
        return mPreferencesHelper.getCardClicked(awb);
    }

    @Override
    public void setMessageCount(String awb, int ecode_status_clicked_times_message_link) {
        mPreferencesHelper.setMessageCount(awb, ecode_status_clicked_times_message_link);
    }

    @Override
    public int getMessageCount(String awb) {
        return mPreferencesHelper.getMessageCount(awb);
    }

    @Override
    public int getCardCount(String awb) {
        return mPreferencesHelper.getCardCount(awb);
    }

    @Override
    public void setCardCount(String awb, int i) {
        mPreferencesHelper.setCardCount(awb, i);
    }

    @Override
    public Single<GenerateToken> dogeneratetoken(String token, String ecomregion) {
        return mApiHelper.dogeneratetoken(token, ecomregion);
    }

    @Override
    public Single<IDFCToken_Response> dogeneratetokenIDFC(String url, String ecomregion, String grant_type, String client_assertion_type, String client_id, String scope, String client_assertion) {
        return mApiHelper.dogeneratetokenIDFC(url, ecomregion, grant_type, client_assertion_type, client_id, scope, client_assertion);
    }

    @Override
    public Single<GenerateOTPResponse> doGenerateOTPApiCall(String authToken, String ecomregion, GenerateOTPRequest generateOTPRequest) {
        return mApiHelper.doGenerateOTPApiCall(authToken, ecomregion, generateOTPRequest);
    }

    @Override
    public Single<GenerateRTSOTPResponse> doGenerateRTSOTPApiCall(String authToken, String ecomregion, GenerateRTSOTPRequest generateOTPRequest) {
        return mApiHelper.doGenerateRTSOTPApiCall(authToken, ecomregion, generateOTPRequest);
    }

    @Override
    public Single<Hospital> doHospitalList(String authToken, String ecomregion, String emp_code) {
        return mApiHelper.doHospitalList(authToken, ecomregion, emp_code);
    }

    @Override
    public Single<VerifyOTPResponse> doVerifyOTPApiCall(String authToken, String ecomregion, VerifyOTPRequest verifyOTPRequest) {
        return mApiHelper.doVerifyOTPApiCall(authToken, ecomregion, verifyOTPRequest);
    }

    @Override
    public Single<VerifyOTPResponse> doVerifyUdOTPApiCall(String authToken, String ecomregion, VerifyOTPRequest verifyOTPRequest) {
        return mApiHelper.doVerifyUdOTPApiCall(authToken, ecomregion, verifyOTPRequest);
    }

    @Override
    public Single<PrintReceiptResponse> doPrintReceiptDataCall(String authToken, String ecomregion, PrintReceiptRequest printReceiptRequest) {
        return mApiHelper.doPrintReceiptDataCall(authToken, ecomregion, printReceiptRequest);
    }

    @Override
    public Single<PrintReceiptUploadResponse> doPrintReceiptUploadImage(String authToken, String ecomregion, Map<String, String> headers, Map<String, RequestBody> multipartBody, MultipartBody.Part file) {
        return mApiHelper.doPrintReceiptUploadImage(authToken, ecomregion, headers, multipartBody, file);
    }
    @Override
    public Single<EncryptContactResponse> doencryptcontact(String token, String ecomregion, long awb) {
        return mApiHelper.doencryptcontact(token, ecomregion, awb);
    }

    @Override
    public Single<WadhResponse> doGetWadhValueAntWork(String basic, String ecomregion, String token_url, WathRequest wathRequest) {
        return mApiHelper.doGetWadhValueAntWork(basic, ecomregion, token_url, wathRequest);
    }

    @Override
    public Single<DPDailyEarnedAmount> doDPDailyEarningApiCall(String token, String ecomregion, String emp_code) {
        return mApiHelper.doDPDailyEarningApiCall(token, ecomregion, emp_code);
    }

    @Override
    public Single<VerifyOTPResponse> doDPVerifyOTPApiCall(String authToken, String ecomregion, VerifyOTPRequest verifyOTPRequest) {
        return mApiHelper.doDPVerifyOTPApiCall(authToken, ecomregion, verifyOTPRequest);
    }

    @Override
    public Single<DailyEarningResponse> doDailyEarnigCalander(String authToken, String ecomregion, DailyEarningRequest dailyEarningRequest) {
        return mApiHelper.doDailyEarnigCalander(authToken, ecomregion, dailyEarningRequest);
    }

    @Override
    public Single<PayoutResponse> doPayoutApi(String authToken, String ecomregion, PayoutRequest payoutRequest) {
        return mApiHelper.doPayoutApi(authToken, ecomregion, payoutRequest);
    }

    @Override
    public Single<DPReferenceCodeResponse> doDPReferecenApiCall(String authToken, String ecomregion, DPReferenceCodeRequest dpReferenceCodeRequest) {
        return mApiHelper.doDPReferecenApiCall(authToken, ecomregion, dpReferenceCodeRequest);
    }

    @Override
    public Single<ReshceduleDetailsResponse> doReshceduleDetails(String authToken, String ecomregion, ResheduleDetailsRequest resheduleDetailsRequest) {
        return mApiHelper.doReshceduleDetails(authToken, ecomregion, resheduleDetailsRequest);
    }
    @Override
    public Single<GenerateTokenNiyo> dogenerateniyotoken() {
        return mApiHelper.dogenerateniyotoken();
    }

    @Override
    public Single<RvpFlyerDuplicateCheckResponse> doRvpflyerDuplicateCheck(String authToken, String ecomregion, RvpFlyerDuplicateCheckRequest rvpflyerDuplicateCheckRequest) {
        return mApiHelper.doRvpflyerDuplicateCheck(authToken, ecomregion, rvpflyerDuplicateCheckRequest);
    }

    @Override
    public Single<List<ADMDATA>> getAdmData(String authToken, String ecomregion, String emp_code) {
        return mApiHelper.getAdmData(authToken, ecomregion, emp_code);
    }

    @Override
    public Single<ADMUpdateResponse> doUpdateADMData(String authToken, String ecomregion, List<ADMUpdateRequest> admUpdateRequests) {
        return mApiHelper.doUpdateADMData(authToken, ecomregion, admUpdateRequests);
    }

    @Override
    public Single<ShipmentWeightResponse> doShipmentWeightApiCall(String authToken, String ecomregion, Map<String, String> headers, Map<String, RequestBody> multipartBody, MultipartBody.Part file) {
        return mApiHelper.doShipmentWeightApiCall(authToken, ecomregion, headers, multipartBody, file);
    }

    @Override
    public Single<CholaResponse> doCholaURLAPI(String authToken, String ecomregion, CholaRequest cholaRequest) {
        return mApiHelper.doCholaURLAPI(authToken, ecomregion, cholaRequest);
    }

    @Override
    public Single<RTSResendOTPResponse> doResendOtpApiCall(String authToken, String ecomregion, RTSResendOTPRequest resendOtpRequest) {
        return mApiHelper.doResendOtpApiCall(authToken, ecomregion, resendOtpRequest);
    }
    @Override
    public Single<RTSResendOTPResponse> doResendOtpApiOtherMobileRTSCall(String authToken, String ecomregion, RTSResendOTPRequest resendOtpRequest) {
        return mApiHelper.doResendOtpApiOtherMobileRTSCall(authToken, ecomregion, resendOtpRequest);
    }
    @Override
    public Single<RTSVerifyOTPResponse> doVerifyOTPApiCall(String authToken, String ecomregion, RTSVerifyOTPRequest rtsVerifyOTPRequest) {
        return mApiHelper.doVerifyOTPApiCall(authToken, ecomregion, rtsVerifyOTPRequest);
    }

    @Override
    public Single<CardDetectionResponse> doCardDetectionResponse(String authToken, String ecomregion, Map<String, String> headers, Map<String, RequestBody> multipartBody, MultipartBody.Part file) {
        return mApiHelper.doCardDetectionResponse(authToken, ecomregion, headers, multipartBody, file);
    }

    @Override
    public Single<BioMatricResponse> doGetBioMatricData(String basic, String ecomregion, String token_url, BiomatricRequest biomatricRequest) {
        return mApiHelper.doGetBioMatricData(basic, ecomregion, token_url, biomatricRequest);
    }

    @Override
    public Single<FwdReassignReponse> doFwdReassingApiCall(String authToken, FWDReassignRequest request) {
        return mApiHelper.doFwdReassingApiCall(authToken, request);
    }

    @Override
    public void setDistance(int parseInt) {
        mPreferencesHelper.setDistance(parseInt);
    }

    @Override
    public int getDistance() {
        return mPreferencesHelper.getDistance();
    }

    @Override
    public void setLiveTrackingCalculatedDistance(float distance) {
        mPreferencesHelper.setLiveTrackingCalculatedDistance(distance);
    }

    @Override
    public float getLiveTrackingCalculatedDistance() {
        return mPreferencesHelper.getLiveTrackingCalculatedDistance();
    }

    @Override
    public void setLiveTrackingCalculatedDistanceWithSpeed(float distance) {
        mPreferencesHelper.setLiveTrackingCalculatedDistanceWithSpeed(distance);
    }

    @Override
    public float getLiveTrackingCalculatedDistanceWithSpeed() {
        return mPreferencesHelper.getLiveTrackingCalculatedDistanceWithSpeed();
    }

    @Override
    public void isRecentRemoved(boolean b) {
        mPreferencesHelper.isRecentRemoved(b);
    }


    @Override
    public boolean getIsRecentRemoved() {
        return mPreferencesHelper.getIsRecentRemoved();
    }

    @Override
    public void setLoginPermission(boolean b) {
        mPreferencesHelper.setLoginPermission(b);
    }

    @Override
    public boolean getLoginPermission() {
        return mPreferencesHelper.getLoginPermission();
    }

    @Override
    public void setPaymentType(String paymentType) {
        mPreferencesHelper.setPaymentType(paymentType);
    }

    @Override
    public String getPaymentType() {
        return mPreferencesHelper.getPaymentType();
    }

    @Override
    public void setOfflineFwd(boolean parseBoolean) {
        mPreferencesHelper.setOfflineFwd(parseBoolean);
    }

    @Override
    public boolean getofflineFwd() {
        return mPreferencesHelper.getofflineFwd();
    }

    @Override
    public void setLiveTrackingCount(int size) {
        mPreferencesHelper.setLiveTrackingCount(size);
    }

    @Override
    public int getLiveTrackingCount() {
        return mPreferencesHelper.getLiveTrackingCount();
    }

    @Override
    public void setADMUpdated(boolean b) {
        mPreferencesHelper.setADMUpdated(b);
    }

    @Override
    public boolean isADMUpdated() {
        return mPreferencesHelper.isADMUpdated();
    }

    @Override
    public void setAdharPositiveButton(String configValue) {
        mPreferencesHelper.setAdharPositiveButton(configValue);
    }

    @Override
    public void setAdharNegativeButton(String configValue) {
        mPreferencesHelper.setAdharNegativeButton(configValue);
    }

    @Override
    public String getAdharPositiveButton() {
        return mPreferencesHelper.getAdharPositiveButton();
    }

    @Override
    public String getAdharNegativeButton() {
        return mPreferencesHelper.getAdharNegativeButton();
    }

    @Override
    public void setDisableResendOtpButtonDuration(long configValue) {
        mPreferencesHelper.setDisableResendOtpButtonDuration(configValue);
    }

    @Override
    public long getDisableResendOtpButtonDuration() {
        return mPreferencesHelper.getDisableResendOtpButtonDuration();
    }

    @Override
    public void setStartTripTime(long currentTimeMillis) {
        mPreferencesHelper.setStartTripTime(currentTimeMillis);
    }

    @Override
    public long getStartTripTime() {
        return mPreferencesHelper.getStartTripTime();
    }

    @Override
    public void setSycningBlokingStatus(boolean b) {
        mPreferencesHelper.setSycningBlokingStatus(b);
    }

    @Override
    public boolean getSynckingBlockingStatus() {
        return mPreferencesHelper.getSynckingBlockingStatus();
    }

    @Override
    public void setFilterCount(int filterCount) {
        mPreferencesHelper.setFilterCount(filterCount);
    }

    @Override
    public int getFilterCount() {
        return mPreferencesHelper.getFilterCount();
    }

    @Override
    public void setESPSchemeTerms(String terms) {
        mPreferencesHelper.setESPSchemeTerms(terms);
    }

    @Override
    public void setESPReferCode(String refer_code) {
        mPreferencesHelper.setESPReferCode(refer_code);
    }

    @Override
    public String getESPSchemeTerms() {
        return mPreferencesHelper.getESPSchemeTerms();
    }

    @Override
    public String getESPReferCode() {
        return mPreferencesHelper.getESPReferCode();
    }

    @Override
    public void setLocationAccuracy(float accuracy) {
        mPreferencesHelper.setLocationAccuracy(accuracy);
    }

    @Override
    public float getLocationAccuracy() {
        return mPreferencesHelper.getLocationAccuracy();
    }

    @Override
    public void setPSTNType(String cb_calling_type) {
        mPreferencesHelper.setPSTNType(cb_calling_type);
    }

    @Override
    public String getPSTNType() {
        return mPreferencesHelper.getPSTNType();
    }

    @Override
    public void setIsMasterDataAvailable(boolean b) {
        mPreferencesHelper.setIsMasterDataAvailable(b);
    }

    @Override
    public boolean isMasterDataAvailable() {
        return mPreferencesHelper.isMasterDataAvailable();
    }

    @Override
    public void setFakeApplicatons(String configValue) {
        mPreferencesHelper.setFakeApplicatons(configValue);
    }

    @Override
    public String getFakeApplications() {
        return mPreferencesHelper.getFakeApplications();
    }

    @Override
    public void setIsCallAlreadyDone(Boolean aBoolean) {
        mPreferencesHelper.setIsCallAlreadyDone(aBoolean);
    }

    @Override
    public boolean getIsCallAlreadyDone() {
        return mPreferencesHelper.getIsCallAlreadyDone();
    }

    @Override
    public void setKiranaUser(String configValue) {
        mPreferencesHelper.setKiranaUser(configValue);
    }

    @Override
    public String getKiranaUser() {
        return mPreferencesHelper.getKiranaUser();
    }

    @Override
    public void setVCallpopup(boolean parseBoolean) {
        mPreferencesHelper.setVCallpopup(parseBoolean);
    }

    @Override
    public boolean getVCallPopup() {
        return mPreferencesHelper.getVCallPopup();
    }

    @Override
    public void setForwardCallCount(String awb, int forward_call_count) {
        mPreferencesHelper.setForwardCallCount(awb, forward_call_count);
    }

    @Override
    public void setRVPCallCount(String awb, int rvp_call_count) {
        mPreferencesHelper.setRVPCallCount(awb, rvp_call_count);
    }

    @Override
    public void setEDSCallCount(String awb, int eds_call_count) {
        mPreferencesHelper.setEDSCallCount(awb, eds_call_count);
    }

    @Override
    public void setRTSCallCount(String awb, int rts_call_count) {
        mPreferencesHelper.setRTSCallCount(awb, rts_call_count);
    }

    @Override
    public boolean getCallClicked(String awb) {
        return mPreferencesHelper.getCallClicked(awb);
    }

    @Override
    public void setCallClicked(String awb, boolean isCallClicked) {
        mPreferencesHelper.setCallClicked(awb, isCallClicked);
    }

    @Override
    public int getForwardCallCount(String awb) {
        return mPreferencesHelper.getForwardCallCount(awb);
    }

    @Override
    public int getRVPCallCount(String awb) {
        return mPreferencesHelper.getRVPCallCount(awb);
    }

    @Override
    public int getEDSCallCount(String awb) {
        return mPreferencesHelper.getEDSCallCount(awb);
    }

    @Override
    public int getRTSCallCount(String awb) {
        return mPreferencesHelper.getRTSCallCount(awb);
    }

    @Override
    public void setForwardMapCount(long awb, int forward_map_count) {
        mPreferencesHelper.setForwardMapCount(awb, forward_map_count);
    }

    @Override
    public void setRTSMapCount(long awb, int rts_map_count) {
        mPreferencesHelper.setRTSMapCount(awb, rts_map_count);
    }

    @Override
    public void setRVPMapCount(long awb, int rvp_map_count) {
        mPreferencesHelper.setRVPMapCount(awb, rvp_map_count);
    }

    @Override
    public void setEDSMapCount(long awb, int eds_map_count) {
        mPreferencesHelper.setEDSMapCount(awb, eds_map_count);
    }

    @Override
    public int getForwardMapCount(long awb) {
        return mPreferencesHelper.getForwardMapCount(awb);
    }

    @Override
    public int getRVPMapCount(long awb) {
        return mPreferencesHelper.getRVPMapCount(awb);
    }

    @Override
    public int getEDSMapCount(long awb) {
        return mPreferencesHelper.getEDSMapCount(awb);
    }

    @Override
    public int getRTSMapCount(long awb) {
        return mPreferencesHelper.getRTSMapCount(awb);
    }

    @Override
    public void setStartQCLat(double currentLatitude) {
        mPreferencesHelper.setStartQCLat(currentLatitude);
    }

    @Override
    public void setStartQCLng(double currentLongitude) {
        mPreferencesHelper.setStartQCLng(currentLongitude);
    }

    @Override
    public String getStartQCLat() {
        return mPreferencesHelper.getStartQCLat();
    }

    @Override
    public String getStartQCLng() {
        return mPreferencesHelper.getStartQCLng();
    }

    @Override
    public void setOFDOTPVerifiedStatus(String s) {
        mPreferencesHelper.setOFDOTPVerifiedStatus(s);
    }

    @Override
    public String getOFDOTPVerifiedStatus() {
        return mPreferencesHelper.getOFDOTPVerifiedStatus();
    }

    @Override
    public void setSKIPOTPREVRQC(String configValue) {
        mPreferencesHelper.setSKIPOTPREVRQC(configValue);
    }

    @Override
    public String getSKIPOTPREVRQC() {
        return mPreferencesHelper.getSKIPOTPREVRQC();
    }

    @Override
    public void setSKIP_CANC_OTP_RVP(String configValue) {
        mPreferencesHelper.setSKIP_CANC_OTP_RVP(configValue);
    }

    @Override
    public String getSKIP_CANC_OTP_RVP() {
        return mPreferencesHelper.getSKIP_CANC_OTP_RVP();
    }

    @Override
    public void setRVPRQCBarcodeScan(String configValue) {
        mPreferencesHelper.setRVPRQCBarcodeScan(configValue);
    }

    @Override
    public String getRVPRQCBarcodeScan() {
        return mPreferencesHelper.getRVPRQCBarcodeScan();
    }

    @Override
    public void setSMSThroughWhatsapp(boolean parseBoolean) {
        mPreferencesHelper.setSMSThroughWhatsapp(parseBoolean);
    }

    @Override
    public void setTechparkWhatsapp(String parseBoolean) {
        mPreferencesHelper.setTechparkWhatsapp(parseBoolean);
    }

    @Override
    public void setTriedReachyouWhatsapp(String parseBoolean) {
        mPreferencesHelper.setTriedReachyouWhatsapp(parseBoolean);
    }

    @Override
    public boolean getSMSThroughWhatsapp() {
        return mPreferencesHelper.getSMSThroughWhatsapp();
    }

    @Override
    public String getTechparkWhatsapp() {
        return mPreferencesHelper.getTechparkWhatsapp();
    }

    @Override
    public String getTriedReachyouWhatsapp() {
        return mPreferencesHelper.getTriedReachyouWhatsapp();
    }

    @Override
    public void setTryReachingCount(String awb, int count) {
        mPreferencesHelper.setTryReachingCount(awb, count);
    }

    @Override
    public int getTryReachingCount(String awb) {
        return mPreferencesHelper.getTryReachingCount(awb);
    }

    @Override
    public void setSendSmsCount(String awb, int count) {
        mPreferencesHelper.setSendSmsCount(awb, count);
    }

    @Override
    public int getSendSmsCount(String awb) {
        return mPreferencesHelper.getSendSmsCount(awb);
    }

    @Override
    public void setRVPSecureOTPVerified(String s) {
        mPreferencesHelper.setRVPSecureOTPVerified(s);
    }

    @Override
    public String getRVPSecureOTPVerified() {
        return mPreferencesHelper.getRVPSecureOTPVerified();
    }

    @Override
    public void setUndeliverReasonCode(String select_reason_code_rts) {
        mPreferencesHelper.setUndeliverReasonCode(select_reason_code_rts);
    }

    @Override
    public String getUndeliverReasonCode() {
        return mPreferencesHelper.getUndeliverReasonCode();
    }

    @Override
    public void setImageUri(Uri selectedPhotoPath) {
        mPreferencesHelper.setImageUri(selectedPhotoPath);
    }

    @Override
    public String getImageUri() {
        return mPreferencesHelper.getImageUri();
    }

    @Override
    public void setStartTrip(boolean b) {
        mPreferencesHelper.setStartTrip(b);
    }

    @Override
    public boolean getStatTrip() {
        return mPreferencesHelper.getStatTrip();
    }

    @Override
    public Observable<Boolean> saveDisputedAwb(PaymentDisputedAwb awb) {
        return mDbHelper.saveDisputedAwb(awb);
    }

    @Override
    public Observable<List<ForwardReasonCodeMaster>> getAllForwardReasonCodeMasterList() {
        return mDbHelper.getAllForwardReasonCodeMasterList();
    }

    @Override
    public Observable<Boolean> saveCallStatus(in.ecomexpress.sathi.repo.remote.model.call.Call call) {
        return mDbHelper.saveCallStatus(call);
    }

    @Override
    public Observable<Boolean> deleteQCData(int drs, long awbNo) {
        return mDbHelper.deleteQCData(drs, awbNo);
    }

    @Override
    public Observable<Boolean> getCallStatus(long awb, int drs) {
        return mDbHelper.getCallStatus(awb, drs);
    }

    @Override
    public Observable<Boolean> insertRvpShipperId(Reverse reverse) {
        return mDbHelper.insertRvpShipperId(reverse);
    }

    @Override
    public Observable<Reverse> getRvpShipperId() {
        return mDbHelper.getRvpShipperId();
    }

    @Override
    public Observable<Long> insert(RVPQCImageTable rvpqcImageTable) {
        return mDbHelper.insert(rvpqcImageTable);
    }

    @Override
    public Observable<List<RVPQCImageTable>> getImageForAwb(String awb) {
        return mDbHelper.getImageForAwb(awb);
    }

    @Override
    public Observable<Integer> deleteRVPQCImageTable() {
        return mDbHelper.deleteRVPQCImageTable();
    }

    @Override
    public Observable<String> getdisputed(String awb) {
        return mDbHelper.getdisputed(awb);
    }

    @Override
    public boolean getDPUserBarcodeFlag() {
        return mPreferencesHelper.getDPUserBarcodeFlag();
    }

    @Override
    public void setDPUserBarcodeFlag(boolean dpUserFlag) {
        mPreferencesHelper.setDPUserBarcodeFlag(dpUserFlag);
    }

    @Override
    public void setMultiSpaceApps(String configValue) {
        mPreferencesHelper.setMultiSpaceApps(configValue);
    }

    @Override
    public String getMultiSpaceApps() {
        return mPreferencesHelper.getMultiSpaceApps();
    }

    @Override
    public void setFeedbackMessage(String feedbackMessage) {
        mPreferencesHelper.setFeedbackMessage(feedbackMessage);
    }

    @Override
    public String getFeedbackMessage() {
        return mPreferencesHelper.getFeedbackMessage();
    }

    // Blur Image Work:-
    @Override
    public void setBlurImageType(String blurImageType) {
        mPreferencesHelper.setBlurImageType(blurImageType);
    }

    @Override
    public String getBlurImageType() {
        return mPreferencesHelper.getBlurImageType();
    }

    @Override
    public void setSathiAttendanceFeatureEnable(Boolean sathiAttendanceFeatureEnable) {
        mPreferencesHelper.setSathiAttendanceFeatureEnable(sathiAttendanceFeatureEnable);
    }

    @Override
    public Boolean getSathiAttendanceFeatureEnable() {
        return mPreferencesHelper.getSathiAttendanceFeatureEnable();
    }

    @Override
    public void setBPMismatch(Boolean bpMismatch) {
        mPreferencesHelper.setBPMismatch(bpMismatch);
    }

    @Override
    public Boolean getBPMismatch() {
        return mPreferencesHelper.getBPMismatch();
    }

    @Override
    public void setUDBPCode(String udbpCode) {
        mPreferencesHelper.setUDBPCode(udbpCode);

    }

    @Override
    public String getUDBPCode() {
        return mPreferencesHelper.getUDBPCode();
    }

    @Override
    public void setOBDREFUSED(String obdRefused) {
        mPreferencesHelper.setOBDREFUSED(obdRefused);
    }

    @Override
    public String getOBDREFUSED() {
        return mPreferencesHelper.getOBDREFUSED();
    }

    @Override
    public void setOBDQCFAIL(String obdQcFail) {
        mPreferencesHelper.setOBDQCFAIL(obdQcFail);
    }

    @Override
    public String getOBDQCFAIL() {
        return mPreferencesHelper.getOBDQCFAIL();
    }

    @Override
    public void setHideCamera(Boolean hideCamera) {
        mPreferencesHelper.setHideCamera(hideCamera);
    }

    @Override
    public Boolean getHideCamera() {
        return mPreferencesHelper.getHideCamera();
    }

    @Override
    public void setFWD_UD_RD_OTPVerfied(String awb, boolean b) {
        mPreferencesHelper.setFWD_UD_RD_OTPVerfied(awb, b);
    }

    @Override
    public boolean getFWD_UD_RD_OTPVerfied(String awb) {
        return mPreferencesHelper.getFWD_UD_RD_OTPVerfied(awb);
    }

    @Override
    public void setFWDRessign(boolean parseBoolean) {
        mPreferencesHelper.setFWDRessign(parseBoolean);
    }

    @Override
    public boolean getFWDRessign() {
        return mPreferencesHelper.getFWDRessign();
    }

    @Override
    public void setAddressQualityScore(String configValue) {
        mPreferencesHelper.setAddressQualityScore(configValue);
    }

    @Override
    public String getAddressQualityScore() {
        return mPreferencesHelper.getAddressQualityScore();
    }

    @Override
    public void setLoginServerTimeStamp(String timeStamp) {
        mPreferencesHelper.setLoginServerTimeStamp(timeStamp);
    }

    @Override
    public String getLoginServerTimeStamp() {
        return mPreferencesHelper.getLoginServerTimeStamp();
    }


    @Override
    public void setImageManualFlyer(Boolean aBoolean) {
        mPreferencesHelper.setImageManualFlyer(aBoolean);
    }

    @Override
    public boolean getImageManulaFlyer() {
        return mPreferencesHelper.getImageManulaFlyer();
    }

    @Override
    public void setMasterDataSync(boolean masterDataSync) {
        mPreferencesHelper.setMasterDataSync(masterDataSync);
    }

    @Override
    public boolean getMasterDataSync() {
        return mPreferencesHelper.getMasterDataSync();
    }

    @Override
    public void setESP_EARNING_VISIBILITY(boolean espEarningVisibility) {
        mPreferencesHelper.setESP_EARNING_VISIBILITY(espEarningVisibility);
    }

    @Override
    public boolean getESP_EARNING_VISIBILITY() {
        return mPreferencesHelper.getESP_EARNING_VISIBILITY();
    }

    // Deleting RTS Shipment after successfully synced:-
    @Override
    public Observable<Boolean> deleteShipmentData(int detailId) {
        return mDbHelper.deleteShipmentData(detailId);
    }

    @Override
    public Observable<Boolean> updateShipmentDetailData(int shipmentStatus, String status, int detailID) {
        return mDbHelper.updateShipmentDetailData(shipmentStatus, status, detailID);
    }

    @Override
    public LiveData<EarningApiResponse> doTrainingAPICall(String authToken, EarningApiRequest earningApiRequest) {
        return mApiHelper.doTrainingAPICall(authToken, earningApiRequest);
    }

    @Override
    public Single<MarkAttendanceResponse> doMarkAttendanceApiCall(String authToken, MarkAttendanceRequest request) {
        return mApiHelper.doMarkAttendanceApiCall(authToken, request);
    }

    @Override
    public Single<CheckAttendanceResponse> doCheckAttendanceApiCall(String authToken, String empCode) {
        return mApiHelper.doCheckAttendanceApiCall(authToken, empCode);
    }

    @Override
    public Single<SelfieImageResponse> doSelfieImageUploadApiCall(String authToken, Map headers, Map multipartBody, MultipartBody.Part file) {
        return mApiHelper.doSelfieImageUploadApiCall(authToken, headers, multipartBody, file);
    }


    @Override
    public void setCheckAttendanceLoginStatus(boolean status) {
        mPreferencesHelper.setCheckAttendanceLoginStatus(status);
    }

    @Override
    public boolean getCheckAttendanceLoginStatus() {
        return mPreferencesHelper.getCheckAttendanceLoginStatus();
    }

    @Override
    public void setScreenStatus(Boolean status) {
        mPreferencesHelper.setScreenStatus(status);

    }

    @Override
    public boolean getScreenStatus() {
        return mPreferencesHelper.getScreenStatus();
    }


    @Override
    public Single<CholaResponse> doWebViewAPI(String authToken, String ecomregion, CholaRequest cholaRequest) {
        return mApiHelper.doWebViewAPI(authToken, ecomregion, cholaRequest);
    }

    @Override
    public Single<CholaResponse> doCampaignAPI(String authToken, String ecomregion, CholaRequest cholaRequest) {
        return mApiHelper.doCampaignAPI(authToken, ecomregion, cholaRequest);
    }

    @Override
    public LiveData<DistanceApiResponse> distanceCalculationApi(String locationRequest, String annotations) {
        return mApiHelper.distanceCalculationApi(locationRequest, annotations);
    }

    @Override
    public boolean getCampaignStatus() {
        return mPreferencesHelper.getCampaignStatus();
    }

    @Override
    public void setDistanceAPIEnabled(Boolean isDistanceAPIEnabled) {
        mPreferencesHelper.setDistanceAPIEnabled(isDistanceAPIEnabled);
    }

    @Override
    public Boolean getDistanceAPIEnabled() {
        return mPreferencesHelper.getDistanceAPIEnabled();
    }

    @Override
    public void setCampaignStatus(Boolean status) {
        mPreferencesHelper.setCampaignStatus(status);
    }

    @Override
    public Single<DistanceApiResponse> distanceCalculationApis(String location, String annotations) {
        return mApiHelper.distanceCalculationApis(location,annotations);
    }
}