package in.ecomexpress.sathi.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import in.ecomexpress.sathi.repo.remote.model.masterdata.WaterMark;
import in.ecomexpress.sathi.ui.base.BaseActivity;

public class ImageProcessor {
    private static final int PICK_FROM_CAMERA = 0x000010;
    private final String TAG = ImageProcessor.class.getName();
    int pos;
    private final Activity activity;
    private String fileName = "";
    private File cacheFile;
    private WaterMark waterMark;

    public ImageProcessor(Activity activity){
        this.activity = activity;
    }

    public String getFileName(){
        return fileName;
    }

    public void captureImage(String fileName){
        Logger.e(TAG, "Water mark not allowed.");
        if(!CommonUtils.isAllPermissionAllow((BaseActivity) activity)) {
            ((BaseActivity<?, ?>) activity).openSettingActivity();
            return;
        }
        captureImage(fileName, null);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void captureImage(String fileName, WaterMark waterMark){
        this.waterMark = waterMark;
        if(fileName.contains("stopTrip") || fileName.contains("startTrip")){
            Constants.Water_Mark_Awb = "";
        }
        this.fileName = fileName;
        File fileDir = new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "/" + Constants.EcomExpress);
        if(!fileDir.exists()) {
            fileDir.mkdirs();
        }
        cacheFile = new File(fileDir, fileName);
        if(cacheFile.exists())
            cacheFile.delete();
        Log.e("Raw Image:", cacheFile.getPath());
        Intent intent = null;
        if(activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            boolean isSdCardMounted = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
            if(isSdCardMounted){
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // INTENT_ACTION_STILL_IMAGE_CAMERA
                if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cacheFile));
                } else{
                    Uri fileProvider = FileProvider.getUriForFile(activity, "in.ecomexpress.sathi.fileprovider", cacheFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                }
            } else {
                Toast.makeText(activity, "SD Card not Mounted", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(activity, "Camera is not Working or not Exists", Toast.LENGTH_LONG).show();
        }
        if(intent != null){
            activity.startActivityForResult(intent, PICK_FROM_CAMERA);
        }
    }

    private Bitmap applyWaterMark(Bitmap bmp){
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
        if(bmp.getWidth() > bmp.getHeight()){
            int temp = point.x;
            point.x = point.y;
            point.y = temp;
        }
        if(bmp.getHeight() <= 200){
            startY = 180;
        }
        if(waterMark == null && !Constants.SHIPMENT_TYPE.equalsIgnoreCase(Constants.EDS)){
            point.x = 12;
            point.y = bmp.getHeight() - startY;
            canvas.drawText(Constants.Water_Mark_Awb, point.x, point.y, paint);
            startY = startY - subInterval;
            point.y = bmp.getHeight() - startY;
            canvas.drawText(Constants.water_mark_emp_code, point.x, point.y, paint);
            startY = startY - subInterval;
            point.y = bmp.getHeight() - startY;
            String latLong = in.ecomexpress.geolocations.Constants.latitude + " / " + in.ecomexpress.geolocations.Constants.longitude;
            canvas.drawText(latLong, point.x, point.y, paint);
            startY = startY - subInterval;
            point.y = bmp.getHeight() - startY;
            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
            canvas.drawText(date, point.x, point.y, paint);
            return result;
        }
        //1. Ecom Express
        if(waterMark != null && waterMark.getEcom_text() != null && waterMark.getEcom_text().length() > 0){
            String printStr = waterMark.getEcom_text();
            point.x = 12;
            point.y = bmp.getHeight() - startY;
            canvas.drawText(printStr, point.x, point.y, paint);
            startY = startY - subInterval;
        }
        //2. OSV
        if(waterMark != null && waterMark.getOsv_text() != null && !waterMark.getOsv_text().isEmpty()){
            String printStr = waterMark.getOsv_text();
            point.x = 12;
            point.y = bmp.getHeight() - startY;
            canvas.drawText(printStr, point.x, point.y, paint);
            startY = startY - subInterval;
        }
        //3. Employee Name
        if(waterMark != null && waterMark.getEmp_name() != null && !waterMark.getEmp_name().isEmpty()){
            String printStr = "Emp Name: " + waterMark.getEmp_name();
            point.x = 12;
            point.y = bmp.getHeight() - startY;
            canvas.drawText(printStr, point.x, point.y, paint);
            startY = startY - subInterval;
        }
        //4. Employee Code
        if(waterMark != null && waterMark.getEmp_code() != null && !waterMark.getEmp_code().isEmpty()){
            String printStr = "Emp Code: " + Constants.water_mark_emp_code;
            point.x = 12;
            point.y = bmp.getHeight() - startY;
            canvas.drawText(printStr, point.x, point.y, paint);
            startY = startY - subInterval;
        }
        //5. Date
        if(waterMark != null && waterMark.getDate() != null && !waterMark.getDate().isEmpty()){
            point.y = bmp.getHeight() - startY;
            String date = "Date: " + waterMark.getDate();
            canvas.drawText(date, point.x, point.y, paint);
            startY = startY - subInterval;
        }
        //6
        if(waterMark != null && waterMark.getLat() != null && !waterMark.getLat().isEmpty() && waterMark.getLng() != null && !waterMark.getLng().isEmpty()){
            String wLocation = "Lat: " + waterMark.getLat() + ", Lng: " + waterMark.getLng();
            point.y = bmp.getHeight() - startY;
            canvas.drawText(wLocation, point.x, point.y, paint);
        }
        return result;
    }

    public String getSavedFilePath(int requestCode, int resultCode){
        try{
            String filePath = "";
            if(requestCode == PICK_FROM_CAMERA && resultCode == Activity.RESULT_OK){
                if(cacheFile != null){
                    // Wait for 1 second for maximum 3 time if not exits
                    int wait = 0;
                    while(!cacheFile.exists() && wait < 3){
                        Thread.sleep(1000);
                        wait++;
                    }
                    if(cacheFile.exists()){
                        filePath = compressImage(cacheFile);
                        Log.e("Image Saved to:", filePath);
                    }
                }
            }
            return filePath;
        } catch(Exception e){
            Toast.makeText(activity, "Exception " + e.toString(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    private String compressImage(File tempFile){
        String filePath = getRealPathFromURI(tempFile.getAbsolutePath());
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();

      /*by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
      you try the use the bitmap here, you will get null.*/
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        /*max Height and width values of the compressed image is taken as 1123x794*/
        float maxHeight = 1123.0f;
        float maxWidth = 794.0f;
        float imgRatio = (float) actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        /*width and height values are set maintaining the aspect ratio of the image*/
        if(actualHeight > maxHeight || actualWidth > maxWidth){
            if(imgRatio < maxRatio){
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if(imgRatio > maxRatio){
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else{
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        } else{
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            actualHeight = displayMetrics.heightPixels;
            actualWidth = displayMetrics.widthPixels;
        }

        /*setting inSampleSize value allows to load a scaled down version of the original image*/
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

        /*inJustDecodeBounds set to false to load the actual bitmap*/
        options.inJustDecodeBounds = false;

        /*this options allow android to claim the bitmap memory if it runs low on memory*/
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];
        try{
            /*load the bitmap from its path*/
            bmp = BitmapFactory.decodeFile(filePath, options);
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch(OutOfMemoryError exception){
            exception.printStackTrace();
        }
        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        assert scaledBitmap != null;
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - (float) bmp.getWidth() / 2, middleY - (float) bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        //apply water mark on scaled bitmap. This image will synced to server.
        //No way to find employee id and location when location and employee id is not null uncomment below line.
        //Water mark will show on image.
        //scaledBitmap = applyWaterMark(scaledBitmap, empCode);
        //apply water mark on scaled bitmap. This image will synced to server.
        /*check the rotation of the image and display it properly*/
        ExifInterface exif;
        try{
            exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if(orientation == 6){
                matrix.postRotate(90);
            } else if(orientation == 3){
                matrix.postRotate(180);
            } else if(orientation == 8){
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            scaledBitmap = applyWaterMark(scaledBitmap);
        } catch(IOException e){
            e.printStackTrace();
        }
        FileOutputStream out;
        String filename = getFileName(tempFile.getName());
        try{
            out = new FileOutputStream(filename);
            /*write the compressed bitmap at the destination specified by filename.*/
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 85, out);
        } catch(FileNotFoundException e){
            e.printStackTrace();
        }
        return filename;
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if(height > reqHeight || width > reqWidth){
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = Math.min(heightRatio, widthRatio);
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while(totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap){
            inSampleSize++;
        }
        return inSampleSize;
    }

    @SuppressLint("DefaultLocale")
    public String getFileName(String fileName){
        File file = new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "/" + Constants.EcomExpress);
        if(!file.exists()){
            file.mkdirs();
        }
        return file.getAbsolutePath() + "/" + fileName;
    }

    private String getRealPathFromURI(String contentURI){
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = activity.getContentResolver().query(contentUri, null, null, null, null);
        if(cursor == null){
            return contentUri.getPath();
        } else{
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }
}