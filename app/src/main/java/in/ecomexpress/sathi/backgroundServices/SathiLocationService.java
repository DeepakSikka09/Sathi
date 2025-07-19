package in.ecomexpress.sathi.backgroundServices;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import java.io.PrintWriter;
import java.io.StringWriter;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.ui.dashboard.landing.DashboardActivity;
import in.ecomexpress.sathi.utils.AppLogJsonProcessor;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.Logger;

public class SathiLocationService {

    private static final String TAG = SathiLocationService.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    private static FusedLocationProviderClient mFusedLocationClient;
    private static final long SECONDS_15 = 1000 * 15;
    private static final long SECONDS_10 = 1000 * 10;
    private static IDataManager mDataManager;

    public static void startLocationUpdates(Context context, IDataManager iDataManager) {
        mDataManager = iDataManager;
        final long timeStamp = System.currentTimeMillis();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            writeCrashes(timeStamp);
            return;
        }
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(SECONDS_15);
        mLocationRequest.setFastestInterval(SECONDS_10);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null && location.getLatitude() > 0 && location.getLongitude() > 0) {
                            mDataManager.setCurrentLatitude(location.getLatitude());
                            mDataManager.setCurrentLongitude(location.getLongitude());
                            Constants.CURRENT_LATITUDE = String.valueOf(location.getLatitude());
                            Constants.CURRENT_LONGITUDE = String.valueOf(location.getLongitude());
                            if (FEInDCZone(location.getLatitude(), location.getLongitude(), mDataManager.getDCLatitude(), mDataManager.getDCLongitude())) {
                                showPopupInDashBoard(context);
                            }
                        }
                    });
        } catch (Exception e) {
            writeCrashes(timeStamp, e);
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private static void showPopupInDashBoard(Context context) {
        if (!mDataManager.getFEReachedDC()) {
            Intent intent = new Intent(DashboardActivity.SHOW_POPUP_STOP_TRIP);
            intent.putExtra(DashboardActivity.MESSAGE, DashboardActivity.SHOW_POPUP_STOP_TRIP);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }

    private static boolean FEInDCZone(double latitude, double longitude, double dcLatitude, double dcLongitude) {
        if (mDataManager.getTripId().equalsIgnoreCase("0")) {
            return false;
        }
        Location locationA = new Location("point A");
        locationA.setLatitude(latitude);
        locationA.setLongitude(longitude);
        Location locationB = new Location("point B");
        locationB.setLatitude(dcLatitude);
        locationB.setLongitude(dcLongitude);
        float distance = locationA.distanceTo(locationB);
        return distance <= Constants.LOCATION_ACCURACY;
    }

    private static void writeCrashes(long timeStamp) {
        AppLogJsonProcessor.appendErrorJSONObject(AppLogJsonProcessor.LogType.ERROR, "Location Permission disabled.", mDataManager.getCurrentLatitude(), mDataManager.getCurrentLongitude(), timeStamp, mDataManager.getCode());
    }

    private static void writeCrashes(long timeStamp, Exception e) {
        AppLogJsonProcessor.appendErrorJSONObject(AppLogJsonProcessor.LogType.ERROR,
                getExceptionAsString(e),
                mDataManager.getCurrentLatitude(),
                mDataManager.getCurrentLongitude(), timeStamp,
                mDataManager.getCode());
    }

    private static String getExceptionAsString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private static final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (location.getAccuracy() < Constants.LOCATION_ACCURACY) {
                    if (location.getLatitude() > 0 && location.getLongitude() > 0) {
                        SathiApplication.setGlobalFELat(location.getLatitude());
                        SathiApplication.setGlobalFELng(location.getLongitude());
                        mDataManager.setCurrentLatitude(location.getLatitude());
                        mDataManager.setCurrentLongitude(location.getLongitude());
                        Constants.CURRENT_LATITUDE = String.valueOf(location.getLatitude());
                        Constants.CURRENT_LONGITUDE = String.valueOf(location.getLongitude());
                    }
                }
            }
        }
    };

    public static void stopLocationUpdates() {
        try {
            if (mFusedLocationClient != null) {
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

//e    @Override
//    public void onLocationChanged(@NonNull Location location) {
//        if (location!=null) {
//            Constants.CURRENT_LATITUDE = String.valueOf(location.getLatitude());
//            Constants.CURRENT_LONGITUDE = String.valueOf(location.getLongitude());
//        }
//    }
}