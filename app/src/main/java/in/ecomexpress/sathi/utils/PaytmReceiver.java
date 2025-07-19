 package in.ecomexpress.sathi.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.utils.receivers.SmsReceiver;

@AndroidEntryPoint
 public class PaytmReceiver extends BroadcastReceiver {
     private static final String TAG = SmsReceiver.class.getSimpleName();
     private Context context;

     @Override
     public void onReceive(Context context, Intent intent) {
         this.context = context;
         try {
             if (intent != null) {
                 String ORDERID = intent.getStringExtra("ORDERID");
                 String STATUS = intent.getStringExtra("STATUS");
                 String AWB_NUMBER = intent.getStringExtra("AWB_NUMBER");

                 // if the SMS is not from our gateway, ignore the message

                 storeValue(ORDERID, STATUS, AWB_NUMBER);
                 Log.d("Paytmrecievevale" , "ORDERID"+ORDERID+"STATUS"+STATUS+"AWB_NUMBER"+AWB_NUMBER);
             }
         } catch (Exception e) {
             //SathiLogger.e(e.getMessage());
             //Log.e(TAG, "Exception: " + e.getMessage());
         }
     }


     private void storeValue(String orderId, String status, String awb_number) {

         JSONObject jsonObject = new JSONObject();
         try {
             jsonObject.put("ORDERID", orderId).put("STATUS", status).put("AWB_NUMBER", awb_number);
             PreferenceUtils.writePreferenceValue(context, orderId, jsonObject.toString());
             Intent intent_local = new Intent("PAYTM_ACTION");
             sendLocationBroadcast(intent_local, orderId, status, awb_number);

         } catch (JSONException e) {
             e.printStackTrace();
         }
     }

     private void sendLocationBroadcast(Intent intent, String orderId, String status, String awb_number) {
         intent.putExtra("ORDERID", orderId);
         intent.putExtra("STATUS", status);
         intent.putExtra("AWB_NUMBER", awb_number);

         LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
         Intent i = new Intent();
         i.setClassName("in.ecomexpress.sathi", "in.ecomexpress.sathi.ui.eds.paytm.PaytmFragment");
         i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         context.startActivity(i);
     }
 }
