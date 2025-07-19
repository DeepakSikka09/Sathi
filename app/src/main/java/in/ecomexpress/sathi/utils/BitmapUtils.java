package in.ecomexpress.sathi.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtils {

    public static void saveBitmap(File file, Bitmap well) {
        try {
            FileOutputStream ostream = new FileOutputStream(file);
            int width = 480;
            int height = (int) (((float) well.getHeight() / well.getWidth()) * width);
            Bitmap save = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Paint paint;
            paint = new Paint();
            paint.setColor(Color.WHITE);
            Canvas now = new Canvas(save);
            Rect fullRect = new Rect(0, 0, width, height);
            now.drawRect(fullRect, paint);
            now.drawBitmap(well, new Rect(0, 0, well.getWidth(), well.getHeight()), fullRect, null);
            save.compress(Bitmap.CompressFormat.PNG, 100, ostream);
            CryptoUtils.encryptFile(file.getAbsolutePath(),file.getAbsolutePath(), Constants.ENC_DEC_KEY);
            try {
                ostream.flush();
                ostream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}