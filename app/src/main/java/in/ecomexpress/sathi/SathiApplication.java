package in.ecomexpress.sathi;

import static in.ecomexpress.sathi.utils.Constants.Shield_Secret_Key;
import static in.ecomexpress.sathi.utils.Constants.Shield_Site_Id;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.StrictMode;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleObserver;
import androidx.multidex.MultiDex;
import com.paytmmoneyagent.core.MoneyQrScannerConfig;
import com.shield.android.Shield;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import dagger.hilt.android.HiltAndroidApp;
import in.ecomexpress.sathi.di.component.DaggerAppComponent;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.ShipmentsDetail;
import in.ecomexpress.sathi.utils.AppLogJsonProcessor;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.UncaughtExceptionHandler;
import io.esper.devicesdk.EsperDeviceSDK;

@HiltAndroidApp
public class SathiApplication extends Application implements Application.ActivityLifecycleCallbacks, LifecycleObserver {

    public static String APP_NAME = "SATHiV2";
    public static HashMap<String, String> hashMapAppUrl = new HashMap<>();
    private static double globalFELat;
    @SuppressLint("StaticFieldLeak")
    public static EsperDeviceSDK esperDeviceSDK;
    public static Boolean esperSDKActivated;
    private static double globalFELng;
    public static String EMP_CODE;
    public static SathiApplication instance;
    public static HashMap<Long, Bitmap> shipmentImageMap = new LinkedHashMap<>();
    public static HashMap<Long, Boolean> rtsCapturedImage1 = new LinkedHashMap<>();
    public static HashMap<Long, Boolean> rtsCapturedImage2 = new LinkedHashMap<>();
    public static long rtsVWDetailID;
    public static ArrayList<ShipmentsDetail> shipmentsDetailsData = new ArrayList<>();

    public static void setGlobalFELat(double globalFELat) {
        SathiApplication.globalFELat = globalFELat;
    }

    public static void setGlobalFELng(double globalFELng) {
        SathiApplication.globalFELng = globalFELng;
    }

    public static double getGlobalFELat() {
        return SathiApplication.globalFELat;
    }

    public static double getGlobalFELng() {
        return SathiApplication.globalFELng;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        AppLogJsonProcessor.init(this);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MoneyQrScannerConfig.INSTANCE.initQrScanner(getApplicationContext());
        // Initializing the EsperDeviceSDK:-
        esperDeviceSDK = EsperDeviceSDK.getInstance(getApplicationContext());
        esperDeviceSDK.activateSDK(Constants.Esper_key, new EsperDeviceSDK.Callback<Void>() {
            @Override
            public void onResponse(Void response) {
                esperSDKActivated = true;
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                esperSDKActivated = false;
                t.printStackTrace();
            }
        });
        DaggerAppComponent.builder().application(this).build().inject(this);
        UncaughtExceptionHandler.init(getApplicationContext());
        UncaughtExceptionHandler.startLogging();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        registerActivityLifecycleCallbacks(this);
        instance = this;
        APP_NAME = getResources().getString(R.string.app_name);
        Shield shield = new Shield.Builder(this, Shield_Site_Id, Shield_Secret_Key).build();
        Shield.setSingletonInstance(shield);
    }

    public static SathiApplication getInstance() {
        return instance;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(@NonNull Activity activity) {}

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        //  optimizeBattery();
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {}

    @Override
    public void onActivityStopped(@NonNull Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {}

    public void optimizeBattery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }
    }
}