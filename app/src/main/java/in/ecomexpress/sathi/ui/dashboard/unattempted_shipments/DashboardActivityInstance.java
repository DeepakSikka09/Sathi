package in.ecomexpress.sathi.ui.dashboard.unattempted_shipments;

import in.ecomexpress.sathi.ui.dashboard.landing.DashboardActivity;

public class DashboardActivityInstance {

    private static DashboardActivityInstance instance;
    private DashboardActivity dashboardActivity;

    private DashboardActivityInstance() {}

    public static synchronized DashboardActivityInstance getInstance() {
        if (instance == null) {
            instance = new DashboardActivityInstance();
        }
        return instance;
    }

    public void setDashboardActivity(DashboardActivity activity) {
        this.dashboardActivity = activity;
    }

    public void navigateToStopTrip(boolean doUnattemptedShipmentCount) {
        if (dashboardActivity != null) {
            dashboardActivity.navigateToStopTrip(doUnattemptedShipmentCount);
        }
    }
}