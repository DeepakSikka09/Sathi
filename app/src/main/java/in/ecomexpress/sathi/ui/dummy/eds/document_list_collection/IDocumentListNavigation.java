package in.ecomexpress.sathi.ui.dummy.eds.document_list_collection;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by dhananjayk on 19-11-2018.
 */

public interface IDocumentListNavigation {
    void onChooseReasonSpinner(String code);


    void captureImage(ImageView imgKycActivityCapture, int position);

    void showError(String e);

    void captureFrontImage();
    void captureRearImage();
    void uploadAAdharImage();
    void getHDFCMaskingStatus();
    Context getContextProvider();
}
