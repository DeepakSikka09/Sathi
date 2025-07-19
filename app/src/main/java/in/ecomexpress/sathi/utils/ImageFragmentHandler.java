package in.ecomexpress.sathi.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import in.ecomexpress.sathi.repo.remote.model.masterdata.WaterMark;
import in.ecomexpress.sathi.ui.base.BaseActivity;

import static android.app.Activity.RESULT_CANCELED;

public abstract class ImageFragmentHandler {
    private final Activity activity;
    private String imageName;
    private Bitmap bitmap;
    private ImageView imgView;
    private final ImageProcessor imageProcessor;
    private String imageCode;

    private final String TAG = ImageHandler.class.getSimpleName();

    public ImageFragmentHandler(Activity activity) {
        this.activity = activity;
        imageProcessor = new ImageProcessor(activity);
    }

    private boolean checkPermission() {
        boolean status = false;
        BaseActivity baseActivity = (BaseActivity) activity;
        baseActivity.requestPermissionsSafely(Constants.permissions, 100);
        return status;
    }

    public void captureImage(String imageName, ImageView imgView, String imageCode) {
        this.imageName = imageName;
        this.imgView = imgView;
        this.imageCode = imageCode;
        imageProcessor.captureImage(imageName);
    }

    public void captureImage(String imageName, ImageView imgView, String imageCode, WaterMark waterMark) {
        this.imageName = imageName;
        this.imgView = imgView;
        this.imageCode = imageCode;
        //remove below line when we are sending watermark from drs list item
        if (waterMark == null) {
            Logger.e(TAG, "water mark is not available");
        }
        imageProcessor.captureImage(imageName, waterMark);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        if (resultCode != RESULT_CANCELED) {
            if (resultCode == Activity.RESULT_OK) {
                // bitmap = imageProcessor.getThumbnail(100, 100);
                String imageLocation = imageProcessor.getSavedFilePath(requestCode, resultCode);
                Bitmap bitmap = BitmapFactory.decodeFile(imageLocation);

                CryptoUtils.encryptFile(imageLocation, imageLocation, Constants.ENC_DEC_KEY);
                //imageUriSaveInDB(imageLocation);WWW
                onBitmapReceived(bitmap, imageLocation, imgView, imageName, imageCode);
               /* bitmap = imageProcessor.getThumbnail(100, 100);
                String imageLocation = imageProcessor.getSavedFilePath(requestCode, resultCode);
                // imageUriSaveInDB(imageLocation);
                onBitmapReceived(bitmap, imageLocation, imgView, imageName, imageCode);
*/
            }
        }
    }
    private void uploadimageudaan(String path){


    }
    private void imageUriSaveInDB(String path) {

/*
        KYCImageSaveObject kycImageSaveObject =
                new KYCImageSaveObject(activityId, drsNo, imageType, imageName, path, awb, "0", "");
        DBHelper helper = new DBHelper(activity);
        helper.addKYCImage(kycImageSaveObject);
        helper.close();
        new BackendCallATImageUpload(activity, kycImageSaveObject).execute();*/
    }

    public abstract void onBitmapReceived(Bitmap bitmap, String imageUri, ImageView imgView, String imageName, String imageCode);

}

