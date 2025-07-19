package in.ecomexpress.sathi.ui.drs.rvp.rvp_secure_activity;

import android.content.Context;
import android.view.View;

public interface IRVPSecureDeliveryNavigator {

    void onBack();
    void onOtpResendSuccess(String description);
    void onOtpVerifySuccess(String type);
    void onHandleError(String errorResponse);
    void getAlert();
    void clearStack();
    void showErrorMessage(String error);
    void onScanClick();
    void mResultReceiver1(String strBarcodeScan);
    void onMobileNoChange();
    Context getActivity();
    void resendSMS(Boolean alternateClick);
    void onGenerateOtpClick();
    void showError(String error);
    void doLogout(String description);
    void onResendClick();
    Context getActivityContext();
    void resendSms(boolean b);
    void VoiceCallOtp();
    void voiceTimer();
    void onVerifyClick();
    void onSkipClick(View view);
    void setGreenTick();
}
