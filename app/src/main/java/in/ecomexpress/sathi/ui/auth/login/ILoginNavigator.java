package in.ecomexpress.sathi.ui.auth.login;

import android.app.Activity;
import in.ecomexpress.sathi.repo.remote.model.login.LoginResponse;

public interface ILoginNavigator {

    void onServerLogin();

    void onForgetPassword();

    void onSuccess();

    void onHandleError(String errorResponse);

    void onHandleProgressiveTimer(String response);

    void openDashboardActivity(boolean isAPICall);

    void onPinClick();

    void showAPKForceUpdate(LoginResponse.APKUpdateResponse apkUpdateResponse);

    void showAPKSoftUpdate(LoginResponse loginResponse, LoginResponse.APKUpdateResponse apkUpdateResponse);

    void showChangePassword();

    void showVerifyOtp(boolean flag, boolean manualOtpFlag);

    void showProgress();

    void doLogout(String message);

    void startLoginVerifyOTPActivity();

    void showErrorMessage(boolean status);

    void noAPKUpdate();

    Activity getActivityActivity();

    void showError(String message);

    void onNOTPNext(String message);

    void showAlertToUpdateDateTime();
}