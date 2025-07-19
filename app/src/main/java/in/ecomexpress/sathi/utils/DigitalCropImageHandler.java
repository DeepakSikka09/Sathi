package in.ecomexpress.sathi.utils;

import static android.app.Activity.RESULT_CANCELED;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Environment;
import android.widget.ImageView;

import com.intsig.imageprocessdemo.ImageScannerActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import in.ecomexpress.sathi.repo.remote.model.masterdata.WaterMark;
import in.ecomexpress.sathi.utils.cameraView.CameraActivity;
import preview.PreviewActivity;

public abstract class DigitalCropImageHandler {
    private final Activity activity;

    private String imageName;
    private ImageView imgView;
    private String imageCode;
    private static final int IMAGE_SCANNER_CODE = 1001;
    private static final int CAMERA_SCANNER_CODE = 1002;
    private static final String APPKEY = Constants.CAMSCANNER_APPKEY;//02LQ7AaA09hPdt5Ned7FByhP
    private final String TAG = ImageHandler.class.getSimpleName();
    String outPutFilePath2;
    private WaterMark waterMark;
    private boolean verifyImage;

    public DigitalCropImageHandler(Activity activity) {
        this.activity = activity;
    }

    public void DigitalCropImage(String imageName, ImageView imgView, String imageCode, WaterMark waterMark) {
        this.imageName = imageName;
        this.imgView = imgView;
        this.imageCode = imageCode;
        this.waterMark = waterMark;
        if (waterMark == null) {
            Logger.e(TAG, "water mark is not available");
        }
        Intent intent = new Intent(activity, CameraActivity.class);
        intent.putExtra("isCrop",true);
        activity.startActivityForResult(intent,CAMERA_SCANNER_CODE);
        //digitalImageProcessor.digitalCrop();
    }

    public void DigitalCropImage(String imageName, ImageView imgView, String imageCode, WaterMark waterMark, boolean verifyImage) {
        this.imageName = imageName;
        this.imgView = imgView;
        this.imageCode = imageCode;
        this.waterMark = waterMark;
        this.verifyImage = verifyImage;
        if (waterMark == null) {
            Logger.e(TAG, "water mark is not available");
        }
        Intent intent = new Intent(activity, CameraActivity.class);
        intent.putExtra("isCrop",true);
        activity.startActivityForResult(intent,CAMERA_SCANNER_CODE);
        //digitalImageProcessor.digitalCrop();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == IMAGE_SCANNER_CODE || requestCode == CAMERA_SCANNER_CODE) {
                    final String result = data.getStringExtra(PreviewActivity.EXTRA_KEY_RESULT);
                    clickCsFromCameraFunc(result, imageName);

                }
            }
        }
    }

    private Bitmap applyWaterMark(Bitmap bmp) {
        if (waterMark == null) {
            Logger.e(TAG, "No Water Mark");
            return bmp;
        }
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, bmp.getConfig());

        //Write image on canvas only once.
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bmp, 0, 0, null);

        //Now write all items on this canvas

        int subInterval = 40;
        int startY = 220;

        Paint paint = new Paint();
        paint.setTextSize(18);
        paint.setColor(Color.YELLOW);
        paint.setAntiAlias(true);
        paint.setUnderlineText(true);

        Point point = new Point();

        Logger.e(TAG, "bmp w: " + bmp.getWidth() + ", h:" + bmp.getHeight());
        if (bmp.getWidth() > bmp.getHeight()) {
            int temp = point.x;
            point.x = point.y;
            point.y = temp;
        }

        if (bmp.getHeight() <= 200) {
            startY = 180;
        }
        //1. Ecom Express
        if (waterMark.getEcom_text() != null && waterMark.getEcom_text().length() > 0) {
            String printStr = waterMark.getEcom_text();
            point.x = 12;
            point.y = bmp.getHeight() - startY;
            canvas.drawText(printStr, point.x, point.y, paint);
            startY = startY - subInterval;
        }
        //2. OSV
        if (waterMark.getOsv_text() != null && waterMark.getOsv_text().length() > 0) {
            String printStr = waterMark.getOsv_text();
            point.x = 12;
            point.y = bmp.getHeight() - startY;
            canvas.drawText(printStr, point.x, point.y, paint);
            startY = startY - subInterval;
        }
        //3. Employee Name
        if (waterMark.getEmp_name() != null && waterMark.getEmp_name().length() > 0) {
            String printStr = "Emp Name: " + waterMark.getEmp_name();
            point.x = 12;
            point.y = bmp.getHeight() - startY;
            canvas.drawText(printStr, point.x, point.y, paint);
            startY = startY - subInterval;
        }
        //4. Employee Code
        if (waterMark.getEmp_code() != null && waterMark.getEmp_code().length() > 0) {
            String printStr = "Emp Code: " + waterMark.getEmp_code();
            point.x = 12;
            point.y = bmp.getHeight() - startY;
            canvas.drawText(printStr, point.x, point.y, paint);
            startY = startY - subInterval;
        }
        //5. Date
        if (waterMark.getDate() != null && waterMark.getDate().length() > 0) {
            point.y = bmp.getHeight() - startY;
            String date = "Date: " + waterMark.getDate();
            canvas.drawText(date, point.x, point.y, paint);
            startY = startY - subInterval;
        }
        //6
        if (waterMark.getLat() != null && waterMark.getLat().length() > 0 && waterMark.getLng() != null && waterMark.getLng().length() > 0) {
            String wLocation = "Lat: " + waterMark.getLat() + ", Lng: " + waterMark.getLng();
            point.y = bmp.getHeight() - startY;
            canvas.drawText(wLocation, point.x, point.y, paint);
        }
        return result;
    }

    @SuppressLint("DefaultLocale")
    public String getFileName(String fileName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/" + Constants.EcomExpress);
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        }
        return file.getAbsolutePath() + "/" + fileName;
    }

    public void clickCsFromCameraFunc(String result, String imageName) {
        File fileDir = new File(Environment.getExternalStorageDirectory(), Constants.EcomExpress);
        Intent intent = new Intent();
        intent.setClass(activity, ImageScannerActivity.class);
        intent.putExtra(ImageScannerActivity.EXTRA_KEY_APP_KEY, APPKEY);
        intent.putExtra(ImageScannerActivity.EXTRA_IMPORT_IMG_TYPE, ImageScannerActivity.IMPORT_FROM_FILEDIR);
        intent.putExtra(ImageScannerActivity.EXTRA_KEY_JUDGE_GRAYORCOLOR, false);
        intent.putExtra(ImageScannerActivity.EXTRA_TRIM_IMAGE_MAXSIDE, 1599);
        outPutFilePath2 = fileDir + File.separator + imageName;
        intent.putExtra(ImageScannerActivity.EXTRA_KEY_INPUTFILE_DATA_PATH, result);
        intent.putExtra(ImageScannerActivity.EXTRA_KEY_RESULT_DATA_PATH, outPutFilePath2);
        activity.startActivityForResult(intent, IMAGE_SCANNER_CODE);
    }

    public abstract void onBitmapReceived(Bitmap bitmap, ImageView imgView, String imageName, String imageCode, String outPutFilePath2, boolean verifyImage);

    public void sendImage(Bitmap b, String uri) {
        outPutFilePath2 = uri;
        if (waterMark != null) {
            Bitmap bit = applyWaterMark(b);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                savebitmap(bit, imageName, outPutFilePath2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            onBitmapReceived(bit, imgView, imageName, imageCode, outPutFilePath2, verifyImage);
        } else {
            onBitmapReceived(b, imgView, imageName, imageCode, outPutFilePath2, verifyImage);
        }
    }

    public void savebitmap(Bitmap bmp, String imageName, String path) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File f = new File(path);
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        CryptoUtils.encryptFile(path, path, Constants.ENC_DEC_KEY);
        fo.close();
        String root = Environment.getExternalStorageDirectory().toString();
        File file = new File(root + "/oriTrimcamera.jpg");
        File file1 = new File(root + "/oriTrimResult.jpg");
        if (file.exists()) {
            file.delete();
        }
        if (file1.exists()) {
            file.delete();
        }
    }
}
