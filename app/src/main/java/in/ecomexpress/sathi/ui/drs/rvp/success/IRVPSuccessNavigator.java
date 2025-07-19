package in.ecomexpress.sathi.ui.drs.rvp.success;

public interface IRVPSuccessNavigator {
    void onHomeClick();
    void onError(String e);
    void showEarnedDialog(String shipmentType);
}
