package in.ecomexpress.sathi.ui.auth.changepassword;

import in.ecomexpress.sathi.repo.remote.model.login.ForgotPasswordResponse;

public interface ChangePasswordNavigator {

    void onChangePassword();

    void onSuccess(ForgotPasswordResponse forgotPasswordResponse);

    void onBackClick();

    void doLogout(String description);

    void clearStack();

    void showError(String e);
}