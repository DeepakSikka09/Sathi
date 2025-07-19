package in.ecomexpress.sathi.utils.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.ui.drs.forward.undelivered_fwd.UndeliveredViewModel;

@AndroidEntryPoint
public class CallCompleteReceiver extends BroadcastReceiver {

    @Inject
    UndeliveredViewModel undeliveredViewModel;
    Context call_context;

    @Override
    public void onReceive(Context context, Intent intent) {
        call_context = context;

        try {

            TelephonyManager tmgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);


            UndeliveredViewModel.MyPhoneStateListener PhoneListener = new UndeliveredViewModel.MyPhoneStateListener();

            tmgr.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        } catch (Exception e) {
            Log.e("Phone Receive Error", " " + e);
        }


    }

}
