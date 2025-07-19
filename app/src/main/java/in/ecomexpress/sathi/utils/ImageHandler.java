package in.ecomexpress.sathi.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import in.ecomexpress.sathi.repo.remote.model.masterdata.WaterMark;
import static android.app.Activity.RESULT_CANCELED;

public abstract class ImageHandler {
    private final String TAG = ImageHandler.class.getSimpleName();
    int pos;
    private String imageName;
    private ImageView imgView;
    private final ImageProcessor imageProcessor;
    private String imageCode;
    private boolean verifyImage;

    public ImageHandler(Activity activity) {
        imageProcessor = new ImageProcessor(activity);
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
        if (waterMark == null) {
            Logger.e(TAG, "water mark is not available");
        }
        imageProcessor.captureImage(imageName, waterMark);
    }

    public void captureImage(String imageName, ImageView imgView, String imageCode, WaterMark waterMark, boolean verifyImage) {
        this.imageName = imageName;
        this.imgView = imgView;
        this.imageCode = imageCode;
        this.verifyImage = verifyImage;
        if (waterMark == null) {
            Logger.e(TAG, "water mark is not available");
        }
        imageProcessor.captureImage(imageName, waterMark);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        if (resultCode != RESULT_CANCELED) {
            if (resultCode == Activity.RESULT_OK) {
                String imageLocation = imageProcessor.getSavedFilePath(requestCode, resultCode);
                Bitmap bitmap = BitmapFactory.decodeFile(imageLocation);
                if(Constants.Image_Click_Pos<Constants.Image_Array_Size) {
                    Constants.Image_Check=1;
                } else {
                    Constants.CHECKFIRSTUPLOAD=1;
                    Constants.Image_Check=0;
                }
                CryptoUtils.encryptFile(imageLocation, imageLocation, Constants.ENC_DEC_KEY);
                onBitmapReceived(bitmap, imageLocation, imgView, imageName, imageCode, pos,verifyImage);
            }
        }
    }

    public abstract void onBitmapReceived(Bitmap bitmap, String imageUri, ImageView imgView, String imageName, String imageCode, int pos,boolean verifyImage);
}