package in.ecomexpress.sathi.ui.auth.forget;

public interface IForgetNavigator {

    void onServerLogin();

    void ViewFlag();

    void onSuccessFullyChangePassword(String statusMessage);

    void onBackClicked();

    void showError(String e);
}