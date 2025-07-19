package in.ecomexpress.sathi.ui.drs.rvp.awbscan;

public interface CaptureScanNavigator {

    void captureImageBeforePackaging();

    void scanCodeBeforePackaging();

    void captureImageAfterPackaging();

    void scanCodeAfterPackaging();

    void onSubmitSuccess();

    void onProceed();

    void errorMsg(String message);

    void mResultReceiver1(String strScancode);

    void captureImageTwoBeforePackaging();

    void isPhonePayEnabled(String phonePeTag);
}