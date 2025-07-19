package in.ecomexpress.sathi.ui.dummy.eds.capture_image;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by dhananjayk on 30-03-2019.
 */

public interface ICaptureImageNavigation {
    void captureImage(ImageView img, int pos,boolean verifyImage);

    void showError(String e);
    void captureFrontImage();
    void captureRearImage();
    void uploadAAdharImage();
    void getHDFCMaskingStatus();
    Context getContextProvider();
}
