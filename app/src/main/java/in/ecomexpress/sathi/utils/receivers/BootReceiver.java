package in.ecomexpress.sathi.utils.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.ui.dashboard.landing.DashboardActivity;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class BootReceiver extends BroadcastReceiver {
    private final static String TAG=BootReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.e(TAG,"boot device receiver");
        Intent i = new Intent(context, DashboardActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

}
