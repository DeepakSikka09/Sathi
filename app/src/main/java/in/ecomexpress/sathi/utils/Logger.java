package in.ecomexpress.sathi.utils;

import android.os.Environment;
import android.util.Log;
import java.io.File;
import in.ecomexpress.sathi.BuildConfig;

public class Logger {

    public static boolean log = BuildConfig.BUILD_TYPE.equals("debug") || BuildConfig.BUILD_TYPE.equals("demo");

    public static void e(String TAG, String message) {
        if (log) {
            Log.e(TAG, message);
        }
    }

    public static void i(String TAG, String message) {
        if (log) {
            Log.i(TAG, message);
        }
    }

    public static void d(String TAG, String message) {
        if (log) {
            Log.d(TAG, message);
        }
    }

    public static String getHiddenDir(String filename) {
        try {
            File fileDir = new File(Environment.getExternalStorageDirectory(), "/." + Constants.EcomExpress);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            File file = new File(fileDir, filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            return file.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}