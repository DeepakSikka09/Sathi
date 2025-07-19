package in.ecomexpress.sathi.ui.drs.rvp.undelivered;

import android.app.Activity;
import android.view.View;
import java.util.List;
import in.ecomexpress.sathi.repo.local.db.model.RVPUndeliveredReasonCodeList;
import in.ecomexpress.sathi.repo.remote.model.ErrorResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.masterdata.RVPReasonCodeMaster;

public interface IRVPUndeliveredNavigator {

    void onChooseReasonSpinner(RVPUndeliveredReasonCodeList rvpReasonCodeMaster);

    void onCaptureImage();

    void onDatePicker();

    void onBackClick();

    void onSubmitSuccess();

    void OpenToDoList();

    void OnSubmitClick();

    void captureDate();

    void onChooseSlotSpinner(String s);

    void onRescheduleClick();

    void onHandleError(ErrorResponse callApiResponse);

    void onChooseGroupSpinner(String s);

    void onChooseChildSpinner(RVPReasonCodeMaster rvpReasonCodeMaster);

    void visible(boolean b);

    void showError(String error);

    void showServerError();

    void setParentGroupSpinnerValues(List<String> parentGroupSpinnerValues);

    void setReasonMessageSpinnerValues(List<String> reasonMessageSpinnerValues);

    void showErrorMessage(boolean status);

    void doLogout(String description);

    void clearStack();

    void onRVPItemFetched(DRSReverseQCTypeResponse drsReverseQCTypeResponse);

    void setConsigneeDistance(int meter);

    void setConsingeeProfiling(boolean enable);

    void setBitmap();

    void isCallAttempted(int isCall);

    void undeliverShipment(boolean failFlag, boolean b);

    Activity getActivityContext();

    void otpLayout(boolean uD_OTP, String otp_key);

    void onResendClick();

    void onVerifyClick();

    void onSkipClick(View view);

    void resendSms(Boolean alternateclick);

    void voiceTimer();

    void onOtpResendSuccess(String str, String description);

    void onGenerateOtpClick();

    void VoiceCallOtp();
}