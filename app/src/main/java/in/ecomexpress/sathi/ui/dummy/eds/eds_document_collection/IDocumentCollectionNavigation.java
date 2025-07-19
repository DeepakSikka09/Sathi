package in.ecomexpress.sathi.ui.dummy.eds.eds_document_collection;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by dhananjayk on 06-11-2018.
 */

public interface IDocumentCollectionNavigation {
    void captureImage(ImageView img,int pos);

    void showError(String e);
    void captureFrontImage();
    void captureRearImage();
    void uploadAAdharImage();
    void getHDFCMaskingStatus();
    Context getContextProvider();
   // void digitalCrop();
}
