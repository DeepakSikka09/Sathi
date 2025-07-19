package in.ecomexpress.sathi.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import in.ecomexpress.sathi.repo.remote.model.masterdata.WaterMark;

public class WatermarkUtils {

    public static WaterMark waterMark;

    public static String compressImage(Activity activity, File tempFile) {
        String filePath = getRealPathFromURI(activity, tempFile.getAbsolutePath());
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        int actualHeight;
        int actualWidth;

        /*width and height values are set maintaining the aspect ratio of the image*/
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        actualHeight = displayMetrics.heightPixels;
        actualWidth = displayMetrics.widthPixels;

        /*setting inSampleSize value allows to load a scaled down version of the original image*/
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

        /*inJustDecodeBounds set to false to load the actual bitmap*/
        options.inJustDecodeBounds = false;

        /*this options allow android to claim the bitmap memory if it runs low on memory*/
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];
        try {
            /*load the bitmap from its path*/
            bmp = BitmapFactory.decodeFile(filePath, options);
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
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
        try {
            exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            //  Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                //    Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                //    Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                //    Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            scaledBitmap = applyWaterMark(scaledBitmap, "", null);
        } catch (IOException e) {
            //SathiLogger.e(e.getMessage());
            e.printStackTrace();
        }
        FileOutputStream out;
        String filename = getFileName(tempFile.getName());
        try {
            out = new FileOutputStream(filename);
            /*write the compressed bitmap at the destination specified by filename.*/
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
        } catch (FileNotFoundException e) {
            // SathiLogger.e(e.getMessage());
            e.printStackTrace();
        }
        return filename;
    }

    @SuppressLint("DefaultLocale")
    public static String getFileName(String fileName) {
        // Done by Sudhir and need to improve.
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/" + Constants.EcomExpress);
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        }
        return file.getAbsolutePath() + "/" + fileName;
    }

    public static String getRealPathFromURI(Activity activity, String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = activity.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = Math.min(heightRatio, widthRatio);
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    public static Bitmap applyWaterMark(Bitmap bmp, String employeeCode, WaterMark waterMark) {
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
        if (bmp.getWidth() > bmp.getHeight()) {
            int temp = point.x;
            point.x = point.y;
            point.y = temp;
        }
        if (bmp.getHeight() <= 200) {
            startY = 180;
        }
        if (waterMark == null) {
            point.x = 12;
            point.y = bmp.getHeight() - startY;
            canvas.drawText(Constants.Water_Mark_Awb, point.x, point.y, paint);
            startY = startY - subInterval;
            point.y = bmp.getHeight() - startY;
            canvas.drawText(Constants.water_mark_emp_code, point.x, point.y, paint);
            startY = startY - subInterval;
            point.y = bmp.getHeight() - startY;
            String latlng = in.ecomexpress.geolocations.Constants.latitude + " / " + in.ecomexpress.geolocations.Constants.longitude;
            canvas.drawText(latlng, point.x, point.y, paint);
            startY = startY - subInterval;
            point.y = bmp.getHeight() - startY;
            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
            canvas.drawText(date, point.x, point.y, paint);
            return result;
        }
        //1. Ecom Express
        if (waterMark.getEcom_text() != null && !waterMark.getEcom_text().isEmpty()) {
            String printStr = waterMark.getEcom_text();
            point.x = 12;
            point.y = bmp.getHeight() - startY;
            canvas.drawText(printStr, point.x, point.y, paint);
            startY = startY - subInterval;
        }
        //2. OSV
        if (waterMark.getOsv_text() != null && !waterMark.getOsv_text().isEmpty()) {
            String printStr = waterMark.getOsv_text();
            point.x = 12;
            point.y = bmp.getHeight() - startY;
            canvas.drawText(printStr, point.x, point.y, paint);
            startY = startY - subInterval;
        }
        //3. Employee Name
        if (waterMark.getEmp_name() != null && !waterMark.getEmp_name().isEmpty()) {
            String printStr = "Emp Name: " + waterMark.getEmp_name();
            point.x = 12;
            point.y = bmp.getHeight() - startY;
            canvas.drawText(printStr, point.x, point.y, paint);
            startY = startY - subInterval;
        }
        //4. Employee Code
        if (waterMark.getEmp_code() != null && !waterMark.getEmp_code().isEmpty()) {
            String printStr = "Emp Code: " + Constants.water_mark_emp_code;
            point.x = 12;
            point.y = bmp.getHeight() - startY;
            canvas.drawText(printStr, point.x, point.y, paint);
            startY = startY - subInterval;
        }
        //5. Date
        if (waterMark.getDate() != null && !waterMark.getDate().isEmpty()) {
            point.y = bmp.getHeight() - startY;
            String date = "Date: " + waterMark.getDate();
            canvas.drawText(date, point.x, point.y, paint);
            startY = startY - subInterval;
        }
        //6
        if (waterMark.getLat() != null && !waterMark.getLat().isEmpty() && waterMark.getLng() != null && !waterMark.getLng().isEmpty()) {
            String wLocation = "Lat: " + waterMark.getLat() + ", Lng: " + waterMark.getLng();
            point.y = bmp.getHeight() - startY;
            canvas.drawText(wLocation, point.x, point.y, paint);
        }
        return result;
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}