package in.ecomexpress.sathi.ui.dashboard.campaign;

import android.app.Activity;

public interface CampaignInterface {
    void showError(String description);

    void startCampaignWebView(String url);

    Activity getActivityContext();

    void doLogout(String desc);

    void clearStack();
}