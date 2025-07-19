package in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail;

import android.app.Activity;
import android.graphics.Bitmap;

import in.ecomexpress.sathi.repo.local.data.eds.EdsCommit;

/**
 * Created by dhananjayk on 05-11-2018.
 */

public interface IEDSDetailNavigator {
    void onBack() ;
    void onNext(EdsCommit edsCommit) ;

    void onCancel(EdsCommit edsCommit);

    void showErrorMessage(boolean status);

    void sendScanResult(String scannedData);

    void onHandleError(String errorResponse);
    void errorMgs(String eMsg);

    void showProgressDelay();

    void onProgressTimer(int seconds);

    void onProgressFinish();
    void onProgressFinishCount();

    void setBitmap();

    void removeBitmap();

    void onProgressFinishCall();

    void setBitmapOnImageId();
    void showProgress();

    Activity getContextProvider();

    void setAadharToCameraImages();
    void showSuccessMessage(String msg);

    void setAadharURIToBlank();

    void uploadEdsImages(String imageName, String imageUri, String imageCode, long awbNo, int drs_no, String activity_code, Bitmap bitmap, boolean uddan_flag);
}
