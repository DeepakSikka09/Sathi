package in.ecomexpress.sathi.ui.dashboard.refer;

import android.app.Activity;

public interface ReferNavigator {

    void showError(String description);

    void startTrainingWebView(String url);

    Activity getActivityContext();

    void doLogout(String desc);

    void clearStack();
}