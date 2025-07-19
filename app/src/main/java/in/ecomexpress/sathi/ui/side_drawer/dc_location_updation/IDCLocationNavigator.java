package in.ecomexpress.sathi.ui.side_drawer.dc_location_updation;

public interface IDCLocationNavigator {

    void onBackPress();

    void showSnackBar(String msg, boolean isSuccessMessage);

    boolean latLongStatus();

    void showToastMsg(String s);

    void setCurrentDCAddress(String addresses);

    void onLocationClick();
}