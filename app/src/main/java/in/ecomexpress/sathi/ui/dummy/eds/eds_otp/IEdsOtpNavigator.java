package in.ecomexpress.sathi.ui.dummy.eds.eds_otp;

/**
 * Created by dhananjayk on 05-12-2018.
 */

public interface IEdsOtpNavigator {
     void onVerifyOtp();
     void onOtherMobile(int i);

    void onCancelActivity();

    void onNext(String description);

    void onResendOtp();

    void onResendNext(String description);

    void onMobileNoChange();

    void sendMsg(String description);

    void onMaxAttempt();

    void onShowMsg(String description);

    void backClick();

    void doLogout(String description);

    void clearStack();

    void showErrorMessage(boolean status);

    void showError(String e);
}
