package in.ecomexpress.sathi.utils.receivers;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.R;

@AndroidEntryPoint
public class NotificationUpdate extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        updateNotification(context, intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateNotification(Context context, Intent intent) {
        Notification notification = new Notification.Builder(context.getApplicationContext(), "default").setContentTitle("Eds slot reminder").setContentText(intent.getStringExtra("description")).setSmallIcon(R.mipmap.ic_launcher).setAutoCancel(true).build();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) manager.notify(123, notification);
    }
}