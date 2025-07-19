package in.ecomexpress.sathi.ui.dashboard.training;

import android.app.Activity;

public interface ITrainingNavigator {
    void showError(String description);

    void startTrainingWebView(String url);

    Activity getActivityContext();

    void onBackClick();

    void doLogout(String desc);

    void clearStack();
}
