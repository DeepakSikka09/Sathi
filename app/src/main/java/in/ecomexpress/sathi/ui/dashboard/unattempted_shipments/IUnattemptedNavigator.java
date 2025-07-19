package in.ecomexpress.sathi.ui.dashboard.unattempted_shipments;

import java.util.List;

public interface IUnattemptedNavigator {

    void updateList(List<UnattemptedShipments> unattemptedShipments);

    void showError(String e);

    void navigateToNextActivity(boolean doUnattemptedShipmentCount);
}