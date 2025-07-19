package in.ecomexpress.sathi.ui.dummy.eds.eds_signature;

import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;

/**
 * Created by shivangi sharma on 28-08-2018.
 */

public interface IEDSSignatureNavigator {
    void onCaptureImage();

    void saveSignature();

    // void showSuccessStatus(DRSReturnToShipperTypeResponse drsReturnToShipperTypeResponse);
    void submitErrorAlert(String s);

    void onbackclick();

    void onclear();

    void onBack();

    void commiterror(String s);

    void onHandleError(String description);

    void openSuccess();

    void setConsigneeDistance(int meter);

    void onEDSItemFetched(EDSResponse edsResponse);

    void setConsingeeProfiling(boolean enable);

    void callCommit(String image_id, String image_key);

    void setBitmap();

//    void onProgressFinishCall();
//    void showErrorMessage(boolean b);
}
