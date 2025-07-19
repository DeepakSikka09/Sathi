package in.ecomexpress.sathi.ui.dashboard.performance;

public interface IPerformanceNavigator {


    void startPerformanceWebView(String s);

    void errorHandler(String s);

    void showHandleError(boolean status);

    void doLogout(String message);

    void clearStack();

    void showError(String s);
}
