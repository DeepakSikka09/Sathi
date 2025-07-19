package in.ecomexpress.sathi.ui.drs.rvp.signature;

import android.content.Context;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse;

public interface IRVPSignatureNavigator {

    void onCaptureImage(String image);

    void onCaptureImageTwo(String image);

    void saveSignature();

    void showSuccessStatus();

    void submitErrorAlert();

    void onHandleError(String errorResponse);

    void onBack();

    void onpickaddressfalse();

    void onClear();

    void setConsigneeName(String item);

    void enableEditText(boolean b);

    void hideEdit(String self);

    String getReceiverName();

    void onRVPItemFetched(DRSReverseQCTypeResponse drsReverseQCTypeResponse);

    void setConsigneeDistance(int meter);

    void setConsingeeProfiling(boolean enable);

    void saveCommit();

    void setImageStatus(Boolean aBoolean);

    void setBitmap();

    Context getContext();

    void showServerError();
}