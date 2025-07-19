package in.ecomexpress.sathi.ui.dummy.eds.ac_document_list_collection;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by dhananjayk on 01-05-2019.
 */

public interface IAcDocumentListNavigation {
    void onChooseReasonSpinner(String code);

    void captureImage(ImageView imgKycActivityCapture, int position);

    void error(String message);
    void captureFrontImage();
    void captureRearImage();
    void uploadAAdharImage();
    void getHDFCMaskingStatus();
    Context getContextProvider();
    void showError(String e);
}
