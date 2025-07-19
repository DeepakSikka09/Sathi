package in.ecomexpress.sathi.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import java.util.Date;

import in.ecomexpress.sathi.ui.drs.todolist.DRSCallListener;
import in.ecomexpress.sathi.ui.drs.todolist.ToDoListViewModel;


public class CallReceiver extends BroadcastReceiver {

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;
    private DRSCallListener drsCallListener;

    ToDoListViewModel toDoListViewModel;
    private static String outgoingCallNumber = null;
    private static String outgoingCallPIN = null;
    private static String outgoingCallAWB = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
            } else {
                String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                int state = 0;
                if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    state = TelephonyManager.CALL_STATE_IDLE;
                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    state = TelephonyManager.CALL_STATE_OFFHOOK;
                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    state = TelephonyManager.CALL_STATE_RINGING;
                }
                onCallStateChanged(context, state, number);

            }
            // onCallStateChanged(context, state, number);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    public void onCallStateChanged(Context context, int state, String number) {
        if (lastState == state) {
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                // callStartTime = new Date();
                savedNumber = number;

                // Toast.makeText(context, "Incoming Call Ringing", Toast.LENGTH_SHORT).show();
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;

                    try {
                        Constants.rCheduleFlag=true;
                        outgoingCallNumber = Constants.call_intent_number;
                        outgoingCallAWB = Constants.call_awb;
                        outgoingCallPIN = Constants.call_pin;
                        if (!outgoingCallNumber.isEmpty()) {
                            if (!outgoingCallAWB.isEmpty()) {
                                if (outgoingCallNumber.contains(outgoingCallAWB)) {
                                    Constants.broad_call_type = Constants.call_awb;
                                    Constants.broad_shipment_type = Constants.shipment_type;
                                    //Constants.broad_shipment_type = Constants.shipment_type;

                                }
                            } else if (!outgoingCallPIN.isEmpty()) {
                                if (outgoingCallNumber.contains(outgoingCallPIN)) {
                                    Constants.broad_call_type = Constants.call_pin;
                                    Constants.broad_shipment_type = Constants.shipment_type;
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {

                    //Ring but no pickup-  a miss
                    // Toast.makeText(context, "Ringing but no pickup" + savedNumber + " Call time " + callStartTime + " Date " + new Date(), Toast.LENGTH_SHORT).show();
                } else if (isIncoming) {

                    // Toast.makeText(context, "Incoming " + savedNumber + " Call time " + callStartTime, Toast.LENGTH_SHORT).show();
                } else {

                    // Toast.makeText(context, "outgoing " + savedNumber + " Call time " + callStartTime + " Date " + new Date(), Toast.LENGTH_SHORT).show();

                }

                break;
        }
        lastState = state;
    }


}