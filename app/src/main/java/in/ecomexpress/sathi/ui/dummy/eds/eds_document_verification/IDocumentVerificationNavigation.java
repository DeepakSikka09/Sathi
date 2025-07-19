package in.ecomexpress.sathi.ui.dummy.eds.eds_document_verification;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by dhananjayk on 07-11-2018.
 */

public interface IDocumentVerificationNavigation {
     

    void onVerify(String starredString);

    void captureImage(ImageView imgKycActivityCapture, int position);

    void showError(String e);

    void captureFrontImage();
    void captureRearImage();
    void uploadAAdharImage();
    void getHDFCMaskingStatus();
    Context getContextProvider();
}
