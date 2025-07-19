package in.ecomexpress.sathi.ui.base;

import static android.content.Context.POWER_SERVICE;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.AndroidViewModel;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;

import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.DeviceDetails;
import in.ecomexpress.sathi.repo.remote.model.masterdata.WaterMark;
import in.ecomexpress.sathi.utils.AppLogJsonProcessor;
import in.ecomexpress.sathi.utils.TimeUtils;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
public abstract class BaseViewModel<N> extends AndroidViewModel {

    private WeakReference<N> mNavigator;
    private final IDataManager mDataManager;
    private final ISchedulerProvider mSchedulerProvider;
    private final ObservableBoolean mIsLoading = new ObservableBoolean(false);

    private final CompositeDisposable mCompositeDisposable;
    private DeviceDetails deviceDetails;

    public BaseViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application application) {
        super(application);
        this.mDataManager = dataManager;
        this.mSchedulerProvider = schedulerProvider;
        this.mCompositeDisposable = new CompositeDisposable();
    }

    public void setNavigator(N navigator) {
        if (navigator != null)
            this.mNavigator = new WeakReference<>(navigator);
    }

    public N getNavigator() {
        return mNavigator.get();
    }

    public IDataManager getDataManager() {
        return mDataManager;
    }

    public ISchedulerProvider getSchedulerProvider() {
        return mSchedulerProvider;
    }

    public CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    public ObservableBoolean getIsLoading() {
        return mIsLoading;
    }

    public void setIsLoading(boolean isLoading) {
        mIsLoading.set(isLoading);
    }

    @Override
    protected void onCleared() {
        mCompositeDisposable.dispose();
        super.onCleared();
    }

    public void writeRestAPIRequst(long timeStamp, Object request) {
        //AppLogs.Companion.writeRestApiRequest(timeStamp, request);
    }

    public void writeRestAPIResponse(long timeStamp, Object response) {
        // AppLogs.Companion.writeRestApiResponse(timeStamp, response);
    }


    public void writeEvent(long timeStamp, String string) {
        AppLogJsonProcessor.appendErrorJSONObject(AppLogJsonProcessor.LogType.EVENT, string, getDataManager().getCurrentLatitude(), getDataManager().getCurrentLongitude(), timeStamp, getDataManager().getCode());
    }

    public void writeErrors(long timeStamp, String string) {
//        AppLogs.Companion.writeCrashes(timeStamp, string);

        AppLogJsonProcessor.appendErrorJSONObject(AppLogJsonProcessor.LogType.ERROR, string, getDataManager().getCurrentLatitude(), getDataManager().getCurrentLongitude(), timeStamp, getDataManager().getCode());
    }

    public void writeErrors(long timeStamp, Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String exceptionAsString = sw.toString();
        writeErrors(timeStamp, exceptionAsString);
    }


    public double getlat() {
        return getDataManager().getCurrentLatitude();
    }

    public double getlng() {
        return getDataManager().getCurrentLongitude();
    }


    public WaterMark setWaterMarkDetail(WaterMark waterMark) {
        if (waterMark != null) {
            waterMark.setEmp_code(getDataManager().getCode());
            waterMark.setEmp_name(getDataManager().getName());
            waterMark.setLat(String.valueOf(getDataManager().getCurrentLatitude()));
            waterMark.setLng(String.valueOf(getDataManager().getCurrentLongitude()));

            if (waterMark.getDate() != null) {

                waterMark.setDate(TimeUtils.getDateTime());
            }

        }
        return waterMark;
    }

    public String getImeiNo(Activity context) {
        String imei = "123456789";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                imei = telephonyManager.getImei();
            } else {
                imei = telephonyManager.getDeviceId();
            }
            if (null == imei || 0 == imei.length()) {
                imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            imei = "123456789";
        }
        return imei;
    }

    public void optimizeBattery(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                context.startActivity(intent);
            }
        }
    }

}
