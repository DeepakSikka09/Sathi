package in.ecomexpress.sathi.backgroundServices;

import static in.ecomexpress.geolocations.LocationTracker.isMyServiceRunning;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.content.ContextCompat;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class RestartBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        try{
            if(!isMyServiceRunning(SyncServicesV2.class)){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    ContextCompat.startForegroundService(context, SyncServicesV2.getStartIntent(context));
                } else{
                    SyncServicesV2.start(context);
                }
            }
        } catch(Exception e){
            Logger.e("RestartBroadcastReceiver", String.valueOf(e));
        }
    }
}