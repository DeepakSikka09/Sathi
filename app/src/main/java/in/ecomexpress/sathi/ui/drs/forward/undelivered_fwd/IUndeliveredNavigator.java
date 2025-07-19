package in.ecomexpress.sathi.ui.drs.forward.undelivered_fwd;

import android.app.Activity;
import android.view.View;

import java.util.List;

import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.repo.local.db.model.ForwardUndeliveredReasonCodeList;
import in.ecomexpress.sathi.repo.remote.model.ErrorResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.masterdata.CallbridgeConfiguration;
import in.ecomexpress.sathi.repo.remote.model.masterdata.ForwardReasonCodeMaster;
import in.ecomexpress.sathi.repo.remote.model.masterdata.GlobalConfigurationMaster;
import in.ecomexpress.sathi.repo.remote.model.reschedule.ReshceduleDetailsResponse;

public interface IUndeliveredNavigator {

    void onChooseReasonSpinner(ForwardUndeliveredReasonCodeList forwardReasonCodeMaster);

    void onCaptureImage();

    void onDatePicker();

    void onSubmitSuccess(ForwardCommit forwardCommit);

    void OpenFailScreen(ForwardCommit forwardCommit);

    void OnSubmitClick();

    void onHandleError(ErrorResponse errorResponse);

    void onChooseGroupSpinner(String groupName);

    void onChooseChildSpinner(ForwardReasonCodeMaster forwardReasonCodeMaster);

    void visible(boolean b);

    void showError(String error);

    void dismissCallDialog();



    void setChildSpinnerValues(List<String> childSpinnerValues);

    void setParentGroupSpinnerValues(List<String> parentSpinnerValues);

    void setSpinner(List<String> spinnerReasonValues);

    void getCallConfig(CallbridgeConfiguration mymasterDataReasonCodeResponse);

    void getGlobal(List<GlobalConfigurationMaster> globalConfigurationMasterList);

    void isCall(int isCall);

    void showErrorMessage(boolean status);

    void onDRSForwardItemFetch(DRSForwardTypeResponse drsForwardTypeResponse);

    void setConsigneeDistance(int meter);

    void setConsingeeProfiling(boolean enable);

    void setBitmap();

    void onChooseDateSpinner(String validDate);

    void setDateSpinner(List<String> validDays);

    void pickDateVisibility();

    void onCallBridgeCheckStatus();

    void undeliverShipment(boolean failFlag, boolean b);

    Activity getActivityContext();
    void otpLayout(boolean uD_OTP, String otp_key);
    void onResendClick();
    void onGenerateOtpClick();
    void onVerifyClick();
    void onSkipClick(View view);
    void onOtpResendSuccess(String str, String description);

    void showScanAlert();

    void setRescheduleResponse(ReshceduleDetailsResponse reshceduleDetailsResponse);

    void resendSms(Boolean alternateclick);

    void voiceTimer();

    void VoiceCallOtp();

    void clearStack();
    void doLogout(String description);
}
