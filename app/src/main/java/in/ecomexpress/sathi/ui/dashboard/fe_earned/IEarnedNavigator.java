package in.ecomexpress.sathi.ui.dashboard.fe_earned;

import android.app.Activity;

public interface IEarnedNavigator {

    void showError(String description);

    Activity getActivityContext();
}