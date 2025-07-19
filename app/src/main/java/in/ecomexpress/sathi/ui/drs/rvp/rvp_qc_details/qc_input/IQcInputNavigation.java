package in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.qc_input;

public interface IQcInputNavigation {

    void captureImage();

    void showError(String e);

    void onScanClick();

    void isPhonePayEnabled(String string);
}