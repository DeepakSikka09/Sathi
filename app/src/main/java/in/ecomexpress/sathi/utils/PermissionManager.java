package in.ecomexpress.sathi.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;


public class PermissionManager {
    //A method that can be called from any Activity, to check for specific permission
    public static void check(Activity activity, String permission, int requestCode) {
        //If requested permission isn't Granted yet
        if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            //Request permission from user
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        }
    }
}