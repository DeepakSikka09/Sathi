package in.ecomexpress.sathi.ui.dashboard.mapview;

public interface MapNavigator {
    void showError(String error);

    void onLogoutClick();

    void showStringError(String message);

    void clearStack();

    void showErrorMessage(boolean status);
}
