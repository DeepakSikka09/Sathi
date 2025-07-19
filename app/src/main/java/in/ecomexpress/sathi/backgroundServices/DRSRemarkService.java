package in.ecomexpress.sathi.backgroundServices;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.DeadSystemException;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.TimeUtils;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

@AndroidEntryPoint
public class DRSRemarkService extends Service {
    private static final String TAG = "DRSRemarkService";
    private static final int MAX_RETRY_COUNT = 3;
    private int retryCount = 0;
    @Inject
    IDataManager iDataManager;
    @Inject
    ISchedulerProvider iSchedulerProvider;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            refreshRemarkTable(iDataManager,iSchedulerProvider);
        } catch (RuntimeException e) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (e.getCause() instanceof DeadSystemException) {
                    if (retryCount < MAX_RETRY_COUNT) {
                        retryCount++;
                        new Handler().postDelayed(() -> startService(new Intent(this, DRSRemarkService.class)), 2000);
                    } else {
                        Logger.e(TAG, "Max retry attempts reached. Service will not be restarted.");
                    }
                } else {
                    throw e; // Re-throw if it's not the DeadSystemException
                }
            }
        }
        return START_STICKY;
    }

    public static void startService(Context context) {
        Intent intent = new Intent(context, DRSRemarkService.class);
        context.startService(intent);
    }

    public static void refreshRemarkTable(IDataManager iDataManager,ISchedulerProvider iSchedulerProvider) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        long currentTime = TimeUtils.getDateYearMonthMillies();
        compositeDisposable.add(iDataManager.deleteOldRemarks(currentTime)
                .subscribeOn(iSchedulerProvider.io())
                .observeOn(iSchedulerProvider.ui())
                .subscribe(aBoolean -> Logger.e(TAG, "Successfully delete all remarks before today's time. " + currentTime)));
    }
}
