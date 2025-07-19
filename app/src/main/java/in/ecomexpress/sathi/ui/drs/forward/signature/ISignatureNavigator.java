package in.ecomexpress.sathi.ui.drs.forward.signature;

import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;

public interface ISignatureNavigator {

    void showHandleError(boolean status);

    void onCaptureImage();
    void onCaptureImage2();

    void saveSignature();

    void showSuccessStatus(ForwardCommit forwardCommit);

    void submitErrorAlert();

    void showError(String msgs);



    void onClear();

    String getCompositeKey();

    void enableEditText(boolean b);

    void hideEdit(String select);

    String getReceiverName();

    void onDRSForwardItemFetch(DRSForwardTypeResponse drsForwardTypeResponse);

    void setConsigneeDistance(int meter);

    void setConsingeeProfiling(boolean enable);

    void saveCommit();

    void callCommit(String valueOf, String fileName, String compositeKey);

    void onHandleError(String error);

    void setBitmap();

    void setCommitOffline(String imageUri);

    void scanAwb();

    void mResultReceiver1(String strScancode);

    boolean getScanedResult();


    String getotp();

    void switchLayoutGone();

    void showScanAlert();

    void dismissDialog();
    void onSubmitBPClick();


    void showerrorMessage(String error);
}
