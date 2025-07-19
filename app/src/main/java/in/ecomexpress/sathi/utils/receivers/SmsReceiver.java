package in.ecomexpress.sathi.utils.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.utils.Constants;

@AndroidEntryPoint
public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (Object aPdusObj : pdusObj) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    String senderAddress = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();
                    Pattern pattern = Pattern.compile("\\w+([0-9]+)\\w+([0-9]+)");
                    Matcher matcher = pattern.matcher(message);
                    // String new_word = message.substring(message.length() - 6);
                    if (senderAddress.contains("-EcomEx"))
                        for (int i = 0; i < matcher.groupCount(); i++) {
                            matcher.find();
                            if (matcher.group().length() == 6)
                                Constants.OTP_DELIMITER = matcher.group();
                        }
                }
            }
        } catch (Exception e) {
        }
    }
}
