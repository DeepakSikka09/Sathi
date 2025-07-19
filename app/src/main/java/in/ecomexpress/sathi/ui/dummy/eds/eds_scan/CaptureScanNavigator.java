package in.ecomexpress.sathi.ui.dummy.eds.eds_scan;
public interface CaptureScanNavigator {
    void captureImageBeforePackaging();
    void scanCodeBeforePackaging();
    void captureImageAfterPackaging();
    void scanCodeAfterPackaging();
    void onSubmitSuccess();
    void onProceed();
    void onBack();
    void mResultReceiverScan(String strBarcode);
    void showEror(String e);
}
