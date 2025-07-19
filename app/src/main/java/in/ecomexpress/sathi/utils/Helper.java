package in.ecomexpress.sathi.utils;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import in.ecomexpress.geolocations.DatabaseHandler;

@SuppressWarnings("resource")
public class Helper {
    public static final String TAG = Helper.class.getSimpleName();

    public static void updateLocationWithData(Context mContext, String awb, String status){
        JSONObject jsonObject = new JSONObject();
        String markerType = "UnDelivered";
        if(status.equalsIgnoreCase(Constants.DELIVERED) || status.equalsIgnoreCase(Constants.RTSMANUALLYDELIVERED) || status.equalsIgnoreCase("1")){
            markerType = "Delivered";
        }
        try{
            jsonObject.put("awb", awb);
            jsonObject.put("status", status);
        } catch(JSONException ex){
            ex.printStackTrace();
        }
        try{
            if(!Constants.CURRENT_LATITUDE.equalsIgnoreCase("0.0") && !Constants.CURRENT_LONGITUDE.equalsIgnoreCase("0.0")){
                DatabaseHandler.getInstance(mContext).updateMarkerTypeAndMetadata(markerType, jsonObject.toString(), Double.parseDouble(Constants.CURRENT_LATITUDE), Double.parseDouble(Constants.CURRENT_LONGITUDE));
            } else if(!String.valueOf(in.ecomexpress.geolocations.Constants.latitude).equalsIgnoreCase("0.0") && !String.valueOf(in.ecomexpress.geolocations.Constants.longitude).equalsIgnoreCase("0.0")){
                DatabaseHandler.getInstance(mContext).updateMarkerTypeAndMetadata(markerType, jsonObject.toString(), in.ecomexpress.geolocations.Constants.latitude, in.ecomexpress.geolocations.Constants.longitude);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}