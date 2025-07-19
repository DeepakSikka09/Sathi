package in.ecomexpress.sathi.ui.auth.verifyOtpLoginScreen;

public interface ILoginVerifyOtpNavigator {

    void onResendOtp();

    void onNOtp();

    void onHandleError(String description);

    void onNext(String otpDelimiter);

    void onVerify();

    void logout();

    void showErrorMessage(boolean status);
}