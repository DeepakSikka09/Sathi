package in.ecomexpress.sathi.ui.dummy.eds.eds_scan;

public interface CaptureScanNavigator {

    void captureImageBeforePackaging();

    void scanCodeBeforePackaging();

    void scanCodeAfterPackaging();

    void onProceed();

    void onBack();

    void mResultReceiverScan(String strBarcode);

    void showError(String e);
}