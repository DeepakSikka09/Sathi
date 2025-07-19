package in.ecomexpress.sathi.ui.drs.forward.obd.navigator;

import android.content.Context;

import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;

public interface IObdOTPNavigator {

    String getCompositeKey();

    void navigateToSuccessActivity();

    void onBack();

    void onOtpResendSuccess(String description);

    void onOtpVerifySuccess(String type,String messageType);

    void onHandleError(String errorResponse);

    void showErrorMessage(String error);

    void onGenerateOtpClick();
    void onGenerateOtpResendClick();

    void onGenerateStopOtp(boolean isGenerate);

    void onGenerateQcFailOtp(boolean isGenerate);

    void showError(String error);

    void doLogout(String description);

    void onResendClick();

    void resendSms(boolean b);

    void VoiceCallOtp(String otpType);

    void voiceTimer();

    void onVerifyClick();

    Context getActivityContext();

    void onUndelivered(ForwardCommit forwardCommit);

    void clearStack();

   void onGenerateCtsOtp(boolean isGenerate);
}