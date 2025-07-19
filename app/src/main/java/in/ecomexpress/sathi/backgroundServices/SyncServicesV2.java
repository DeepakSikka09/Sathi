package in.ecomexpress.sathi.backgroundServices;

import static in.ecomexpress.sathi.utils.Constants.DISTANCE_API_KEY;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import org.json.JSONObject;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.geolocations.LocationService;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.data.commit.PushApi;
import in.ecomexpress.sathi.repo.local.data.eds.EDSActivityImageRequest;
import in.ecomexpress.sathi.repo.local.data.eds.EDSCommitResponse;
import in.ecomexpress.sathi.repo.local.data.eds.EdsCommit;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.repo.local.data.rts.RTSCommit;
import in.ecomexpress.sathi.repo.local.data.rvp.RvpCommit;
import in.ecomexpress.sathi.repo.local.db.model.CommonDRSListItem;
import in.ecomexpress.sathi.repo.local.db.model.ImageModel;
import in.ecomexpress.sathi.repo.local.db.model.LiveTrackingLogTable;
import in.ecomexpress.sathi.repo.local.db.model.LiveTrackingRequestDataDB;
import in.ecomexpress.sathi.repo.local.db.model.Remark;
import in.ecomexpress.sathi.repo.remote.model.DeviceDetails;
import in.ecomexpress.sathi.repo.remote.model.LiveTrackingID.LiveTrackingRequest;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.DRSReturnToShipperTypeNewResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.IRTSBaseInterface;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.image.ImageUploadResponse;
import in.ecomexpress.sathi.repo.remote.model.rts.ImageResponse;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;
import in.ecomexpress.sathi.ui.dashboard.landing.DashboardActivity;
import in.ecomexpress.sathi.utils.AppLogJsonProcessor;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.CryptoUtils;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.NetworkUtils;
import in.ecomexpress.sathi.utils.TimeUtils;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function5;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@AndroidEntryPoint
public class SyncServicesV2 extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final List<ImageModel> imageList = new LinkedList<>();
    int status = 2;
    @Inject
    Context context;
    private GoogleApiClient googleApiClient;
    Timer notificationTimer;
    Timer LiveTrackingTimer;
    Timer sendLiveTrackingData;
    Timer sendLiveTrackingDataToServer;
    private static final String TAG = SyncServicesV2.class.getSimpleName();
    boolean valueData = false;
    @Inject
    IDataManager mDataManager;
    @Inject
    ISchedulerProvider iSchedulerProvider;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    @Inject
    DeviceDetails deviceDetails;
    EDSActivityImageRequest edsActivityImageRequest = new EDSActivityImageRequest();
    RvpCommit.ImageData imageDataRVP = new RvpCommit.ImageData();
    RTSCommit.ImageData imageDataRTS = new RTSCommit.ImageData();
    ForwardCommit.Image_response imageDataForward = new ForwardCommit.Image_response();
    public int level = 0;
    Notification.Builder builder;
    boolean isStopService = true;

    private final TimerTask notificationTask = new TimerTask() {
        @Override
        public void run() {
            long timeStampTag = System.currentTimeMillis();
            if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
                syncServiceLogging("Internet is available, to sync data", timeStampTag);
                valueData = false;
                connectGoogleClient();
                getUnSyncData();
                getUnSyncImageV2();
            } else {
                syncServiceLogging("Internet is not available, to sync data", timeStampTag);
            }
        }
    };

    private final TimerTask LiveTrackingTask = new TimerTask() {
        @Override
        public void run() {
            try {
                if (!mDataManager.getTripId().equalsIgnoreCase("null") || !mDataManager.getTripId().equalsIgnoreCase("0")) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (!mDataManager.getTripId().equalsIgnoreCase("0") && !mDataManager.getIsAdmEmp()) {
                            startLiveTracking();
                        }
                    } else {
                        ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
                    }
                }
            } catch (Exception e) {
                if (!mDataManager.getTripId().equalsIgnoreCase("0") && !mDataManager.getIsAdmEmp()) {
                    startLiveTracking();
                }
                Logger.e(TAG, String.valueOf(e));
            }
        }
    };

    private final TimerTask sendLiveTrackingDataTask = new TimerTask() {
        @Override
        public void run() {
            if (!mDataManager.getTripId().equalsIgnoreCase("null") || !mDataManager.getTripId().equalsIgnoreCase("0")) {
                if (!mDataManager.getIsAdmEmp()) {
                    serviceSendData();
                }
            }
        }
    };

    private final TimerTask sendLiveTrackingDataTaskToServer = new TimerTask() {
        @Override
        public void run() {
            if (!mDataManager.getTripId().equalsIgnoreCase("null") || !mDataManager.getTripId().equalsIgnoreCase("0")) {
                if (!mDataManager.getIsAdmEmp()) {
                    serviceSendDataToServer();
                }
            }
        }
    };

    private void serviceSendDataToServer() {
        try {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(mDataManager.getDataFromLiveTrackingLog().observeOn(iSchedulerProvider.io()).subscribeOn(iSchedulerProvider.io()).subscribe(this::sendLogToServer, throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void sendLogToServer(List<LiveTrackingLogTable> liveTrackingLogTables) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(mDataManager.dosendLiveTrackingLog(mDataManager.getAuthToken(), mDataManager.getEcomRegion(), liveTrackingLogTables).doOnSuccess(liveTrackingResponse -> deleteLogs()).subscribeOn(iSchedulerProvider.io()).observeOn(iSchedulerProvider.ui()).subscribe(liveTrackingResponse -> {
        }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
    }

    private void deleteLogs() {
        try {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(mDataManager.deleteLogs().observeOn(iSchedulerProvider.io()).subscribeOn(iSchedulerProvider.io()).subscribe(aBoolean -> {
            }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void connectGoogleClient() {
        googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        googleApiClient.connect();
    }

    private void syncServiceLogging(String message, long timeStamp) {
        AppLogJsonProcessor.appendSyncServiceLogs(AppLogJsonProcessor.LogType.EVENT, message, in.ecomexpress.geolocations.Constants.latitude, in.ecomexpress.geolocations.Constants.longitude, timeStamp, mDataManager.getCode());
    }

    private void startLiveTracking() {
        try {
            if (DashboardActivity.lt != null && mDataManager.getLiveTrackingTripId() != null && !mDataManager.getLiveTrackingTripId().equalsIgnoreCase("")) {
                DashboardActivity.lt.startTrackingWithParameters(context, Constants.APP_NAME, Constants.VERSION_NAME, "LastMile", mDataManager.getCode(), mDataManager.getLocationCode(), mDataManager.getVehicleType(), mDataManager.getAuthToken(), mDataManager.getLiveTrackingTripId(), "start", mDataManager.getLiveTrackingMaxFileSize(), Constants.LIVE_TRACKING_URL, mDataManager.getLiveTrackingAccuracy(), mDataManager.getLiveTrackingInterval(), Integer.parseInt(mDataManager.getLatLngLimit()), DISTANCE_API_KEY, mDataManager.getLiveTrackingDisplacement(), mDataManager.getDistance());
            } else {
                getLiveTrackingID();
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public static Intent getStartIntent(Context context) {
        return new Intent(context, SyncServicesV2.class).putExtra("stops", false);
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, SyncServicesV2.class);
        context.startService(starter);
    }

    public static Intent getStopIntent(Context context) {
        return new Intent(context, SyncServicesV2.class).putExtra("stops", true);
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, SyncServicesV2.class));
    }

    private void sendBoardCast() {
        Intent intent = new Intent(Constants.SYNC_SERVICE);
        intent.putExtra(Constants.MESSAGE, true);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (notificationTimer == null) {
            notificationTimer = new Timer();
        }
        notificationTimer.schedule(notificationTask, 5000L, Constants.SYNC_DELAY_TIME);

        if (LiveTrackingTimer == null) {
            LiveTrackingTimer = new Timer();
        }
        LiveTrackingTimer.schedule(LiveTrackingTask, 5000L, mDataManager.getSathiLogApiCallInterval());

        if (sendLiveTrackingData == null) {
            sendLiveTrackingData = new Timer();
        }
        sendLiveTrackingData.schedule(sendLiveTrackingDataTask, 5000L, 10 * 60 * 1000);

        if (sendLiveTrackingDataToServer == null) {
            sendLiveTrackingDataToServer = new Timer();
        }
        sendLiveTrackingDataToServer.schedule(sendLiveTrackingDataTaskToServer, 5000L, mDataManager.getSathiLogApiCallInterval());

        Handler handler = new Handler();
        int delay = 5000;
        handler.postDelayed(new Runnable() {
            public void run() {
                SathiLocationService.startLocationUpdates(context, mDataManager);
                handler.postDelayed(this, delay);
            }
        }, delay);
        try {
            this.registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent!=null) {
            isStopService = intent.getBooleanExtra("stops", true);
            if (isStopService) {
                stopSelf();
            } else {
                startForGroundService();
            }
        }
        getAllNewDRS();
        getAllApiUrl();
        return START_STICKY;
    }

    private void startForGroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!mDataManager.getTripId().equalsIgnoreCase("0")) {
                @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel("Android", "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(channel);
                builder = new Notification.Builder(this, "Android").setContentTitle(getString(R.string.app_name)).setContentText("Sync Service Running").setAutoCancel(false).setSmallIcon(R.drawable.ic_sathiappicon).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_sathiappicon)).setColor(context.getResources().getColor(R.color.transparent));
                Notification notification = builder.build();
                startForeground(1, notification);
            }
        }
    }

    public void getAllNewDRS() {
        try {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(Observable.zip(mDataManager.getDRSListForward(), mDataManager.getDRSListNewRTS(), mDataManager.getDRSListRVP(), mDataManager.getDrsListNewEds(), mDataManager.getAllRemarks(mDataManager.getCode(), TimeUtils.getDateYearMonthMillies()), new Function5<List<DRSForwardTypeResponse>, DRSReturnToShipperTypeNewResponse, List<DRSReverseQCTypeResponse>, List<EDSResponse>, List<Remark>, List<CommonDRSListItem>>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public List<CommonDRSListItem> apply(List<DRSForwardTypeResponse> drsForwardTypeResponses, DRSReturnToShipperTypeNewResponse drsReturnToShipperTypeNewResponses, List<DRSReverseQCTypeResponse> drsReverseQCTypeResponses, List<EDSResponse> edsResponses, List<Remark> remarks) {
                    List<CommonDRSListItem> commonDRSListItems = new ArrayList<>();
                    for (DRSForwardTypeResponse fwd : drsForwardTypeResponses) {
                        CommonDRSListItem item = new CommonDRSListItem(GlobalConstant.ShipmentTypeConstants.FWD, fwd);
                        commonDRSListItems.add(item);
                    }
                    for (IRTSBaseInterface rts : drsReturnToShipperTypeNewResponses.getCombinedList()) {
                        CommonDRSListItem item = new CommonDRSListItem(GlobalConstant.ShipmentTypeConstants.RTS, rts);
                        commonDRSListItems.add(item);
                    }
                    for (DRSReverseQCTypeResponse rvp : drsReverseQCTypeResponses) {
                        CommonDRSListItem item = new CommonDRSListItem(GlobalConstant.ShipmentTypeConstants.RVP, rvp);
                        commonDRSListItems.add(item);
                    }
                    for (EDSResponse eds : edsResponses) {
                        CommonDRSListItem item = new CommonDRSListItem(GlobalConstant.ShipmentTypeConstants.EDS, eds);
                        commonDRSListItems.add(item);
                    }
                    return commonDRSListItems;
                }
            }).subscribeOn(iSchedulerProvider.io()).observeOn(iSchedulerProvider.ui()).subscribe(commonDRSListItems -> {
            }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void onDestroy() {
        try {
            unregisterReceiver(mBatInfoReceiver);
            SathiLocationService.stopLocationUpdates();
            if (googleApiClient != null && googleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
                googleApiClient.disconnect();
            }
            if (isStopService) {
                Intent broadcastIntent = new Intent(this, RestartBroadcastReceiver.class);
                sendBroadcast(broadcastIntent);
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void getUnSyncData() {
        try {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(mDataManager.getPushApiUnSyncList().subscribeOn(iSchedulerProvider.io()).observeOn(iSchedulerProvider.io()).subscribe(pushApiList -> {
                if (pushApiList != null && !pushApiList.isEmpty()) {
                    Observable<List<PushApi>> obj = Observable.just(pushApiList);
                    getImageStatus(obj);
                } else if (pushApiList != null) {
                    getPendingSyncData();
                }
            }));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void getPendingSyncData() {
        try {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(mDataManager.getPushApiAllPendingUnSyncList().subscribeOn(iSchedulerProvider.io()).observeOn(iSchedulerProvider.io()).subscribe(drsForwardTypeResponses -> {
                if (drsForwardTypeResponses != null && !drsForwardTypeResponses.isEmpty()) {
                    Observable<List<PushApi>> obj = Observable.just(drsForwardTypeResponses);
                    getImageStatus(obj);
                }
            }));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void getImageStatus(Observable<List<PushApi>> pushApi) {
        try {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(pushApi.subscribeOn(Schedulers.single()).observeOn(iSchedulerProvider.io()).flatMap((Function<List<PushApi>, ObservableSource<PushApi>>) Observable::fromIterable).map(pushApi1 -> {
                Thread.sleep(10000);
                getShipmentImageStatus(pushApi1);
                return "";
            }).subscribe());
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void CheckImageStatus(PushApi pushApi, boolean isAllImageSynced) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(mDataManager.getImageStatus(pushApi.getAwbNo()).subscribeOn(Schedulers.io()).observeOn(iSchedulerProvider.io()).subscribe(flag -> {
            if (flag) {
                if (!valueData) {
                    valueData = true;
                    try {
                        if (!Constants.UPLOADEDS_CALLED) {
                            if (isAllImageSynced) {
                                uploadShipment(pushApi);
                            }
                        }
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                }
            } else if (pushApi.getShipmentCaterogy().equalsIgnoreCase(GlobalConstant.ShipmentTypeConstants.FWD) || pushApi.getShipmentCaterogy().equalsIgnoreCase(GlobalConstant.ShipmentTypeConstants.RTS)) {
                try {
                    if (!valueData) {
                        valueData = true;
                        try {
                            if (!Constants.UPLOADEDS_CALLED) {
                                if (isAllImageSynced) {
                                    uploadShipment(pushApi);
                                }
                            }
                        } catch (Exception e) {
                            Logger.e(TAG, String.valueOf(e));
                        }
                    }
                } catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                }
            }
        }));
    }

    private void getShipmentImageStatus(PushApi pushApi) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(mDataManager.getShipmentImageStatus(pushApi.getAwbNo()).subscribeOn(Schedulers.io()).observeOn(iSchedulerProvider.io()).subscribe(imageModels -> {
            boolean isAllImageSynced = true;
            if (imageModels != null && !imageModels.isEmpty()) {
                for (ImageModel imageModel : imageModels) {
                    if (imageModel.getStatus() == 0 && imageModel.getImageId() <= 0) {
                        isAllImageSynced = false;
                        break;
                    }
                }
            }
            CheckImageStatus(pushApi, isAllImageSynced);
        }, Throwable::printStackTrace));
    }

    public void uploadShipment(PushApi pushApi) {
        Constants.UPLOADEDS_CALLED = true;
        final long timeStamp = System.currentTimeMillis();
        writeAnalytics(timeStamp, "Start Uploading Shipment");
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        try {
            compositeDisposable.add(mDataManager.getUnsynced(pushApi.getAwbNo()).subscribeOn(iSchedulerProvider.io()).observeOn(iSchedulerProvider.io()).subscribe(imageModel -> {
                StringBuilder builder = new StringBuilder();
                Constants.UPLOADEDS_CALLED = false;
                switch (pushApi.getShipmentCaterogy()) {
                    case GlobalConstant.ShipmentTypeConstants.FWD:
                        List<ForwardCommit.Image_response> image_responses = new ArrayList<>();
                        if (pushApi.getShipmentStatus() == 0 || pushApi.getShipmentStatus() == 3 || pushApi.getShipmentStatus() == 4) {
                            for (ImageModel imageModel1 : imageModel) {
                                imageDataForward = new ForwardCommit.Image_response();
                                imageDataForward.setImage_id(String.valueOf(imageModel1.getImageId()));
                                imageDataForward.setImage_key(imageModel1.getImageName());
                                builder.append("{");
                                builder.append(imageModel1.getImageName());
                                builder.append("}");
                                image_responses.add(imageDataForward);
                            }
                            writeAnalytics(timeStamp, "Start Uploading FWD shipment [image_names:" + builder + "]");
                            if (!Constants.UPLOADEDS_CALLED) {
                                uploadForwardShipment(pushApi, image_responses);
                            }
                        }
                        break;
                    case GlobalConstant.ShipmentTypeConstants.RTS:
                        List<RTSCommit.ImageData> imageDataRTSList = new ArrayList<>();
                        if (pushApi.getShipmentStatus() == 0 || pushApi.getShipmentStatus() == 3 || pushApi.getShipmentStatus() == 4) {
                            for (ImageModel imageModel1 : imageModel) {
                                imageDataRTS = new RTSCommit.ImageData();
                                imageDataRTS.setImageId(String.valueOf(imageModel1.getImageId()));
                                imageDataRTS.setImageKey(trimString(imageModel1.getImageName()));
                                imageDataRTS.setImageType(imageModel1.getImageType());
                                builder.append("{");
                                builder.append(imageModel1.getImageName());
                                builder.append("}");
                                imageDataRTSList.add(imageDataRTS);
                            }
                            writeAnalytics(timeStamp, "Start Uploading RTS shipment [image_names:" + builder + "]");
                            if (!Constants.UPLOADEDS_CALLED) {
                                UploadRTSShipment(pushApi, imageDataRTSList, imageModel);
                            }
                        }
                        break;
                    case GlobalConstant.ShipmentTypeConstants.RQC:
                    case GlobalConstant.ShipmentTypeConstants.RVP:
                        List<RvpCommit.ImageData> imageDatas = new ArrayList<>();
                        if (pushApi.getShipmentStatus() == 0 || pushApi.getShipmentStatus() == 3 || pushApi.getShipmentStatus() == 4) {
                            for (ImageModel imageModel1 : imageModel) {
                                imageDataRVP = new RvpCommit.ImageData();
                                imageDataRVP.setImageId(String.valueOf(imageModel1.getImageId()));
                                imageDataRVP.setImageKey(trimString(imageModel1.getImageName()));
                                builder.append("{");
                                builder.append(imageModel1.getImageName());
                                builder.append("}");
                                imageDatas.add(imageDataRVP);
                            }
                            writeAnalytics(timeStamp, "Start Uploading RVP shipment [image_names:" + builder + "]");
                            if (!Constants.UPLOADEDS_CALLED) {
                                rvpShipment(pushApi, imageDatas);
                            }
                        }
                        break;
                    case GlobalConstant.ShipmentTypeConstants.EDS:
                        try {
                            List<EDSActivityImageRequest> edsActivityImageRequests = new ArrayList<>();
                            if (pushApi.getShipmentStatus() == 0 || pushApi.getShipmentStatus() == 3 || pushApi.getShipmentStatus() == 4) {
                                for (ImageModel imageModel1 : imageModel) {
                                    edsActivityImageRequest = new EDSActivityImageRequest();
                                    edsActivityImageRequest.setImageId(String.valueOf(imageModel1.getImageId()));
                                    edsActivityImageRequest.setImageKey(trimString(imageModel1.getImageName()));
                                    builder.append("{");
                                    builder.append(imageModel1.getImageName());
                                    builder.append("}");
                                    edsActivityImageRequests.add(edsActivityImageRequest);
                                }
                                writeAnalytics(timeStamp, "Start Uploading EDS shipment [image_names:" + builder + "]");
                                if (!Constants.UPLOADEDS_CALLED) {
                                    UploadEdsShipment(pushApi, edsActivityImageRequests);
                                }
                            }
                        } catch (Exception e) {
                            Constants.UPLOADEDS_CALLED = false;
                            Logger.e(TAG, String.valueOf(e));
                        }
                        break;
                }
            }));
        } catch (Exception e) {
            Constants.UPLOADEDS_CALLED = false;
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public String trimString(String fileName) {
        if (fileName.indexOf(".") > 0) fileName = fileName.substring(0, fileName.lastIndexOf("."));
        return fileName;
    }

    @SuppressLint("CheckResult")
    private void UploadEdsShipment(PushApi pushApi, List<EDSActivityImageRequest> edsActivityImageRequests) {
        Constants.UPLOADEDS_CALLED = true;
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        final long timeStamp = System.currentTimeMillis();
        writeAnalytics(timeStamp, "Start Uploading EDS shipment");
        HashMap<String, String> tokens = new HashMap<>();
        tokens.put(Constants.TOKEN, mDataManager.getAuthToken());
        tokens.put(Constants.EMP_CODE, mDataManager.getCode());
        EdsCommit edsCommit = null;
        try {
            edsCommit = new ObjectMapper().readValue(pushApi.getRequestData(), EdsCommit.class);
            edsCommit.setEdsActivityImageRequests(edsActivityImageRequests);
            if (edsCommit.getStatus().equalsIgnoreCase(Constants.EDSUNDELIVERED) || edsCommit.getStatus().equalsIgnoreCase("2")) {
                edsCommit.setStatus(Constants.EDSUNDELIVERED);
                status = 3;
            } else {
                edsCommit.setStatus(Constants.EDSDELIVERED);
                status = 2;
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        final EdsCommit edsCommitTemp = edsCommit;
        try {
            compositeDisposable.add(mDataManager.doEDSCommitApiCall(mDataManager.getAuthToken(), mDataManager.getEcomRegion(), tokens, edsCommit).subscribeOn(iSchedulerProvider.io()).subscribe((EDSCommitResponse edsCommitResponse) -> {
                writeAnalytics(timeStamp, "EDS Response: " + edsCommitResponse.getStatus());
                Constants.UPLOADEDS_CALLED = false;
                if (edsCommitResponse.getStatus()) {
                    int shipment_status;
                    String compositeKey;
                    if (edsCommitResponse.getResponse().getShipment_status().equalsIgnoreCase(Constants.EDSUNDELIVERED)) {
                        shipment_status = 3;
                    } else {
                        shipment_status = 2;
                    }
                    try {
                        compositeKey = edsCommitResponse.getResponse().getDrs_no() + edsCommitResponse.getResponse().getAwb_no().trim();
                    } catch (Exception e) {
                        compositeKey = pushApi.getCompositeKey();
                        Logger.e(TAG, String.valueOf(e));
                    }
                    mDataManager.updateEdsStatus(compositeKey, shipment_status).subscribe(aBoolean -> {
                        if (aBoolean) {
                            pushApi.setShipmentStatus(GlobalConstant.CommitStatus.CommitSynced);
                            updateSyncStatusInDRSEDSTable(edsCommitResponse.getResponse().getDrs_no() + edsCommitResponse.getResponse().getAwb_no());
                            compositeDisposable.add(mDataManager.deleteSyncedImage((edsCommitResponse.getResponse().getAwb_no())).subscribe(aBoolean1 -> {
                            }));
                            compositeDisposable.add(mDataManager.saveCommitPacket(pushApi).subscribe(aBoolean12 -> sendBoardCast()));
                            // Setting call preference after sync:-
                            mDataManager.setCallClicked(edsCommitResponse.getResponse().getAwb_no() + "EDSCall", true);
                            valueData = false;
                        }
                    }, throwable -> {
                        if (pushApi.getShipmentStatus() == GlobalConstant.CommitStatus.CommitAssign)
                            updatestatus(pushApi, GlobalConstant.CommitStatus.CommitSecondAttempt);
                        else if (pushApi.getShipmentStatus() == GlobalConstant.CommitStatus.CommitSecondAttempt)
                            updatestatus(pushApi, GlobalConstant.CommitStatus.CommitSyncedFailed);
                        writeCrashes(timeStamp, new Exception(throwable));
                        valueData = false;
                        Logger.e(TAG, String.valueOf(throwable));
                    });
                } else if ((edsCommitResponse.getResponse().getCode().equalsIgnoreCase("E107")) || (edsCommitResponse.getResponse().getCode().equalsIgnoreCase("107"))) {
                    valueData = false;
                    Constants.UPLOADEDS_CALLED = false;
                    LocalLogout();
                }
            }, throwable -> {
                Constants.UPLOADEDS_CALLED = false;
                if (pushApi.getShipmentStatus() == GlobalConstant.CommitStatus.CommitAssign) {
                    updatestatus(pushApi, GlobalConstant.CommitStatus.CommitSecondAttempt);
                } else if (pushApi.getShipmentStatus() == GlobalConstant.CommitStatus.CommitSecondAttempt) {
                    updatestatus(pushApi, GlobalConstant.CommitStatus.CommitSyncedFailed);
                }
                valueData = false;
            }));
        } catch (Exception e) {
            Constants.UPLOADEDS_CALLED = false;
            updatestatus(pushApi, 0);
            valueData = false;
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void updateSyncStatusInDRSEDSTable(String compositeKey) {
        final long TimeStampTag = System.currentTimeMillis();
        writeAnalytics(TimeStampTag, "Changing Status of EDS:" + compositeKey + " to synced.");
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(mDataManager.updateSyncStatusEDS(compositeKey, 2).subscribeOn(iSchedulerProvider.io()).observeOn(iSchedulerProvider.io()).subscribe(aBoolean -> writeAnalytics(TimeStampTag, "Successfully Changed Status of EDS:" + compositeKey + " to synced."), throwable -> {
        }));
    }

    private void updateSyncStatusInDRSFWDTable(String composite_key) {
        final long TimeStampTag = System.currentTimeMillis();
        writeAnalytics(TimeStampTag, "Changing Status of FWD:" + composite_key + " to synced.");
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(mDataManager.updateSyncStatusFWD(composite_key, GlobalConstant.CommitStatus.CommitSynced).subscribeOn(iSchedulerProvider.io()).observeOn(iSchedulerProvider.io()).subscribe(aBoolean -> {
        }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
    }

    private void updateSyncStatusInDRSRVpTable(boolean isFromMps, String composite_key) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        if(isFromMps){
            compositeDisposable.add(mDataManager.updateSyncStatusMps(composite_key, 2).subscribeOn(iSchedulerProvider.io()).observeOn(iSchedulerProvider.io()).subscribe(aBoolean -> {}, throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } else{
            compositeDisposable.add(mDataManager.updateSyncStatusRVP(composite_key, 2).subscribeOn(iSchedulerProvider.io()).observeOn(iSchedulerProvider.io()).subscribe(aBoolean -> {}, throwable -> Logger.e(TAG, String.valueOf(throwable))));
        }
    }

    public void updateSyncStatusInDRSRTSTable(long vendorID) {
        final long TimeStampTag = System.currentTimeMillis();
        writeAnalytics(TimeStampTag, "Changing Status of RTS:" + vendorID + " to synced.");
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(mDataManager.updateSyncStatusRTS(vendorID, 2).subscribeOn(iSchedulerProvider.io()).observeOn(iSchedulerProvider.io()).subscribe(aBoolean -> {
        }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
    }

    private void getUnSyncImageV2() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(mDataManager.getUnSyncImagesV2().subscribeOn(iSchedulerProvider.io()).observeOn(iSchedulerProvider.io()).subscribe(imageModels -> {
            imageList.clear();
            imageList.addAll(imageModels);
            if (!imageModels.isEmpty()) runSingleThread();
        }));
    }

    @SuppressLint("CheckResult")
    private void uploadForwardShipment(PushApi pushApi, List<ForwardCommit.Image_response> image_response) {
        Constants.UPLOADEDS_CALLED = true;
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        final long timeStamp = System.currentTimeMillis();
        writeAnalytics(timeStamp, "Start Uploading FWD Shipment");
        HashMap<String, String> tokens = new HashMap<>();
        tokens.put(Constants.TOKEN, mDataManager.getAuthToken());
        tokens.put(Constants.EMP_CODE, mDataManager.getCode());
        ForwardCommit[] forwardCommit;
        List<ForwardCommit> fwdCommit = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            forwardCommit = mapper.readValue(pushApi.getRequestData(), ForwardCommit[].class);
            for (ForwardCommit commit : forwardCommit) {
                commit.setImage_response(image_response);
                commit.setTrip_id(mDataManager.getTripId());
                if (commit.getStatus().equalsIgnoreCase(Constants.UNDELIVERED) || commit.getStatus().equalsIgnoreCase("2")) {
                    status = Constants.SHIPMENT_UNDELIVERED_STATUS;
                    commit.setStatus(Constants.UNDELIVERED);
                } else {
                    status = Constants.SHIPMENT_DELIVERED_STATUS;
                    commit.setStatus(Constants.DELIVERED);
                }
                fwdCommit.add(commit);
            }
            getUDORRCHEDShipmentStatus(forwardCommit);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        try {
            Log.e("sync", fwdCommit + "");
            compositeDisposable.add(mDataManager.doFWDCommitApiCall(mDataManager.getAuthToken(), mDataManager.getEcomRegion(), tokens, fwdCommit).subscribeOn(iSchedulerProvider.io()).subscribe(forwardCommitResponse -> {
                Constants.UPLOADEDS_CALLED = false;
                if (forwardCommitResponse.getStatus()) {
                    int shipement_status;
                    String compositeKey;
                    if (forwardCommitResponse.getResponse().getShipment_status().equalsIgnoreCase(Constants.UNDELIVERED)) {
                        shipement_status = Constants.SHIPMENT_UNDELIVERED_STATUS;
                    } else {
                        shipement_status = Constants.SHIPMENT_DELIVERED_STATUS;
                    }
                    try {
                        compositeKey = forwardCommitResponse.getResponse().getDrs_no() + forwardCommitResponse.getResponse().getAwb_no().trim();
                    } catch (Exception e) {
                        compositeKey = pushApi.getCompositeKey();
                        Logger.e(TAG, String.valueOf(e));
                    }
                    mDataManager.updateForwardStatus(compositeKey, shipement_status).subscribe(aBoolean -> {
                        Log.e("ForwardResponse", " Result:-forwardData");
                        pushApi.setShipmentStatus(GlobalConstant.CommitStatus.CommitSynced);
                        updateSyncStatusInDRSFWDTable(forwardCommitResponse.getResponse().getDrs_no() + forwardCommitResponse.getResponse().getAwb_no());
                        compositeDisposable.add(mDataManager.deleteSyncedImage(forwardCommitResponse.getResponse().getAwb_no()).subscribe(aBoolean1 -> {
                        }));

                        compositeDisposable.add(mDataManager.saveCommitPacket(pushApi).subscribe(aBoolean12 -> sendBoardCast()));
                        // Setting call preference after sync:-
                        mDataManager.setCallClicked(forwardCommitResponse.getResponse().getAwb_no() + "ForwardCall", true);
                        valueData = false;
                    }, throwable -> {
                        if (pushApi.getShipmentStatus() == GlobalConstant.CommitStatus.CommitAssign)
                            updatestatus(pushApi, GlobalConstant.CommitStatus.CommitSecondAttempt);
                        else if (pushApi.getShipmentStatus() == GlobalConstant.CommitStatus.CommitSecondAttempt)
                            updatestatus(pushApi, GlobalConstant.CommitStatus.CommitSyncedFailed);
                        writeCrashes(timeStamp, new Exception(throwable));
                        valueData = false;
                        Logger.e(TAG, String.valueOf(throwable));
                    });
                } else if ((forwardCommitResponse.getResponse().getCode().equalsIgnoreCase("E107")) || (forwardCommitResponse.getResponse().getCode().equalsIgnoreCase("107"))) {
                    Constants.UPLOADEDS_CALLED = false;
                    valueData = false;
                    LocalLogout();
                }
            }, throwable -> {
                Constants.UPLOADEDS_CALLED = false;
                if (pushApi.getShipmentStatus() == GlobalConstant.CommitStatus.CommitAssign) {
                    updatestatus(pushApi, GlobalConstant.CommitStatus.CommitSecondAttempt);
                }
                else if (pushApi.getShipmentStatus() == GlobalConstant.CommitStatus.CommitSecondAttempt) {
                    updatestatus(pushApi, GlobalConstant.CommitStatus.CommitSyncedFailed);
                }
                valueData = false;
            }));
        } catch (Exception e) {
            Constants.UPLOADEDS_CALLED = false;
            updatestatus(pushApi, 0);
            valueData = false;
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void getUDORRCHEDShipmentStatus(ForwardCommit[] forwardCommit) {
        if (forwardCommit[0].getUd_otp().equalsIgnoreCase("VERIFIED") || forwardCommit[0].getRd_otp().equalsIgnoreCase("VERIFIED")) {
            mDataManager.setFWD_UD_RD_OTPVerfied(forwardCommit[0].getAwb() + "Forward", true);
        }
    }

    private void updatestatus(PushApi pushApi, int i) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        pushApi.setShipmentStatus(i);
        compositeDisposable.add(mDataManager.saveCommitPacket(pushApi).subscribeOn(iSchedulerProvider.io()).observeOn(iSchedulerProvider.io()).subscribe(aBoolean -> {
        }));
    }

    private void LocalLogout() {
        mDataManager.setTripId("");
        mDataManager.setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT);
        clearAppData();
    }

    private void clearAppData() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(mDataManager.deleteAllTables().subscribeOn(iSchedulerProvider.io()).observeOn(iSchedulerProvider.ui()).subscribe(aBoolean -> {
            try {
                mDataManager.clearPrefrence();
                mDataManager.setUserAsLoggedOut();
            } catch (Exception ex) {
                Logger.e(TAG, String.valueOf(ex));
            }
            clearStack();
        }));
    }

    private void clearStack() {
        Toast.makeText(this, "Your session has expired. Please log in", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @SuppressLint("CheckResult")
    private void runSingleThread() {
        Observable.fromCallable(() -> {
            try {
                uploadImageServer();
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
            return false;
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe((result) -> {
        });
    }

    @SuppressLint("CheckResult")
    private void uploadImageServer() {
        final long timeStampUploadImageToServer = System.currentTimeMillis();
        for (ImageModel imageModel : imageList) {
            try {
                byte[] bytes;
                final long timeStamp = System.currentTimeMillis();
                writeCrashes(timeStamp, "Uploading Image to server imageName[" + imageModel.getImageName() + "]");
                File file = new File(imageModel.getImage());
                if (file == null || file.length() == 0) {
                    writeCrashes(timeStamp, "Error Uploading Image to server imageName[" + imageModel.getImageName() + "] file size Null//Zero");
                }
                bytes = CryptoUtils.decryptFile1(file.toString(), Constants.ENC_DEC_KEY);
                RequestBody mFile = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), mFile);
                RequestBody awb_no;
                if (imageModel.getImageType().equalsIgnoreCase(GlobalConstant.ImageTypeConstants.UD_RTS_IMAGE)) {
                    awb_no = RequestBody.create(MediaType.parse("text/plain"), imageModel.getDraNo());
                } else {
                    awb_no = RequestBody.create(MediaType.parse("text/plain"), imageModel.getAwbNo());
                }
                RequestBody drs_no = RequestBody.create(MediaType.parse("text/plain"), imageModel.getDraNo());
                RequestBody image_code = RequestBody.create(MediaType.parse("text/plain"), imageModel.getImageCode());
                RequestBody image_name = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                RequestBody image_type = RequestBody.create(MediaType.parse("text/plain"), imageModel.getImageType());
                Map<String, RequestBody> map = new HashMap<>();
                map.put("image", mFile);
                map.put("awb_no", awb_no);
                map.put("drs_no", drs_no);
                map.put("image_code", image_code);
                map.put("image_name", image_name);
                map.put("image_type", image_type);
                Map<String, String> headers = new HashMap<>();
                headers.put("token", mDataManager.getAuthToken());
                headers.put("Accept", "application/json");
                try {
                    ImageUploadResponse imageUploadResponse = mDataManager.doImageUploadApiCall(mDataManager.getAuthToken(), mDataManager.getEcomRegion(), imageModel.getImageType(), headers, map, fileToUpload).blockingGet();
                    if (imageUploadResponse != null) {
                        try {
                            if (imageUploadResponse.getStatus().toLowerCase().contains("Success".toLowerCase())) {
                                try {
                                    imageModel.setStatus(2);
                                    if (imageUploadResponse.getImageId() != null)
                                        imageModel.setImageId(imageUploadResponse.getImageId());
                                    boolean aBoolean = mDataManager.saveImage(imageModel).blockingSingle();
                                    if (aBoolean) {
                                        deleteImageFile(imageUploadResponse);
                                    }
                                } catch (Exception e) {
                                    Logger.e(TAG, String.valueOf(e));
                                }
                            }
                        } catch (Exception ex) {
                            try {
                                if (!imageModel.getImageType().contains("RVP") || !imageModel.getImageType().contains("EDS") || !imageModel.getImageType().contains("OTHER")) {
                                    imageModel.setStatus(GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_FAILED);
                                    imageModel.setImageId(-1);
                                    long nextSyncTime = imageModel.getImageFutureSyncTime() + 1000 * 60 * 10;//15 minute
                                    imageModel.setImageFutureSyncTime(nextSyncTime);
                                    mDataManager.saveImage(imageModel).blockingSingle();
                                }
                            } catch (Exception e) {
                                Logger.e(TAG, String.valueOf(e));
                            }
                        }
                    } else {
                        try {
                            if (!imageModel.getImageType().contains("RVP") || !imageModel.getImageType().contains("EDS") || !imageModel.getImageType().contains("OTHER")) {
                                imageModel.setStatus(GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_FAILED);
                                imageModel.setImageId(-1);
                                long nextSyncTime = imageModel.getImageFutureSyncTime() + 1000 * 60 * 10;//15 minute
                                imageModel.setImageFutureSyncTime(nextSyncTime);
                                mDataManager.saveImage(imageModel).blockingSingle();
                            }
                        } catch (Exception e) {
                            Logger.e(TAG, String.valueOf(e));
                        }
                    }
                } catch (Exception ex) {
                    try {
                        if (!imageModel.getImageType().contains("RVP") || !imageModel.getImageType().contains("EDS") || !imageModel.getImageType().contains("OTHER")) {
                            imageModel.setStatus(GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_FAILED);
                            imageModel.setImageId(-1);
                            long nextSyncTime = imageModel.getImageFutureSyncTime() + 1000 * 60 * 10;//15 minute
                            imageModel.setImageFutureSyncTime(nextSyncTime);
                            mDataManager.saveImage(imageModel).blockingSingle();
                        }
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                }
            } catch (Exception ex) {
                writeCrashes(timeStampUploadImageToServer, ex);
                Logger.e(TAG, String.valueOf(ex));
            }
        }
    }

    private void deleteImageFile(ImageUploadResponse imageUploadResponse) {
        File fileDelete = new File(imageUploadResponse.getFileName());
        if (fileDelete.exists()) {
            if (fileDelete.delete()) {
                System.out.println("file Deleted :" + imageUploadResponse.getFileName());
            } else {
                System.out.println("file not Deleted :" + imageUploadResponse.getFileName());
            }
        }
    }

    public static void close(Closeable c) {
        if (c == null) return;
        try {
            c.close();
        } catch (IOException e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @SuppressLint("CheckResult")
    private void UploadRTSShipment(PushApi pushApi, List<RTSCommit.ImageData> imageData, List<ImageModel> imageModel) {
        Constants.UPLOADEDS_CALLED = true;
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        final long timeStamp = System.currentTimeMillis();
        writeAnalytics(timeStamp, "Start Uploading RTS Shipment");
        HashMap<String, String> tokens = new HashMap<>();
        tokens.put(Constants.TOKEN, mDataManager.getAuthToken());
        tokens.put(Constants.EMP_CODE, mDataManager.getCode());
        RTSCommit rtsCommit = null;
        try {
            rtsCommit = new ObjectMapper().readValue(pushApi.getRequestData(), RTSCommit.class);

            for (int i = 0; i < rtsCommit.getShipments().size(); i++) {
                ArrayList<ImageResponse> imageResponseList = new ArrayList<>();
                for (int j = 0; j < imageModel.size(); j++) {
                    if (imageData.get(j).getImageType().equalsIgnoreCase(GlobalConstant.ImageTypeConstants.UD_RTS_IMAGE)) {
                        if (rtsCommit.getShipments().get(i).getAwbNo().equals(imageModel.get(j).getDraNo())) {
                            ImageResponse imageResponse = new ImageResponse();
                            imageResponse.setImageId(Integer.parseInt(imageData.get(j).getImageId()));
                            imageResponse.setImageKey(imageModel.get(j).getImageName());
                            imageResponseList.add(imageResponse);
                            rtsCommit.getShipments().get(i).setRts_shipment_images(imageResponseList);
                        }
                    }
                }
            }

            List<RTSCommit.ImageData> imageDataPOD = new ArrayList<>();
            for (int i = 0; i < imageData.size(); i++) {
                if (imageData.get(i).getImageType().equalsIgnoreCase(GlobalConstant.ImageTypeConstants.OTHERS)) {
                    RTSCommit.ImageData imageData1 = new RTSCommit.ImageData();
                    imageData1.setImageId(imageData.get(i).getImageId());
                    imageData1.setImageKey(imageData.get(i).getImageKey());
                    imageDataPOD.add(imageData1);
                }
            }
            rtsCommit.setImageData(imageDataPOD);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        final RTSCommit rtsCommitTemp = rtsCommit;
        try {
            compositeDisposable.add(mDataManager.doRTSCommitApiCall(mDataManager.getAuthToken(), mDataManager.getEcomRegion(), tokens, rtsCommit).subscribeOn(iSchedulerProvider.io()).subscribe(rtsCommitResponse -> {
                Constants.UPLOADEDS_CALLED = false;
                writeAnalytics(timeStamp, "RTS Commit Response: " + rtsCommitResponse.getStatus());
                try {
                    if (rtsCommitResponse.getStatus()) {
                        SathiApplication.shipmentImageMap.clear();
                        SathiApplication.rtsCapturedImage1.clear();
                        SathiApplication.rtsCapturedImage2.clear();
                        int shipmentStatus;
                        if (pushApi.getShipmentDeliveryStatus().equalsIgnoreCase("3")) {
                            shipmentStatus = Constants.SHIPMENT_UNDELIVERED_STATUS;
                        } else {
                            shipmentStatus = Constants.SHIPMENT_DELIVERED_STATUS;
                        }
                        mDataManager.updateRtsStatus(Long.valueOf(rtsCommitResponse.getResponse().getId()), shipmentStatus).subscribe(aBoolean -> {
                            if (aBoolean) {
                                valueData = false;
                                pushApi.setShipmentStatus(GlobalConstant.CommitStatus.CommitSynced);
                                updateSyncStatusInDRSRTSTable(Long.parseLong(rtsCommitResponse.getResponse().getId()));
                                compositeDisposable.add(mDataManager.deleteSyncedImage(String.valueOf(pushApi.getAwbNo())).subscribe(aBoolean1 -> {
                                }));
                                compositeDisposable.add(mDataManager.saveCommitPacket(pushApi).subscribe(aBoolean12 -> sendBoardCast()));

                                // Deleting the RTS shipment data after successfully committed:-
                                compositeDisposable.add(mDataManager.deleteShipmentData(Math.toIntExact(pushApi.getAwbNo())).subscribe(aBoolean13 -> sendBoardCast()));
                            }
                        }, throwable -> {
                            writeCrashes(timeStamp, new Exception(throwable));
                            valueData = false;
                            if (pushApi.getShipmentStatus() == GlobalConstant.CommitStatus.CommitAssign)
                                updatestatus(pushApi, GlobalConstant.CommitStatus.CommitSecondAttempt);
                            else if (pushApi.getShipmentStatus() == GlobalConstant.CommitStatus.CommitSecondAttempt)
                                updatestatus(pushApi, GlobalConstant.CommitStatus.CommitSyncedFailed);
                            Logger.e(TAG, String.valueOf(throwable));
                        });
                    } else if ((rtsCommitResponse.getResponse().getCode().equalsIgnoreCase("E107")) || (rtsCommitResponse.getResponse().getCode().equalsIgnoreCase("107"))) {
                        valueData = false;
                        LocalLogout();
                    }
                } catch (Exception e) {
                    valueData = false;
                    if (pushApi.getShipmentStatus() == GlobalConstant.CommitStatus.CommitAssign)
                        updatestatus(pushApi, GlobalConstant.CommitStatus.CommitSecondAttempt);
                    else if (pushApi.getShipmentStatus() == GlobalConstant.CommitStatus.CommitSecondAttempt)
                        updatestatus(pushApi, GlobalConstant.CommitStatus.CommitSyncedFailed);
                    writeCrashes(timeStamp, e);
                    Logger.e(TAG, String.valueOf(e));
                }
            }, throwable -> {
                valueData = false;
                if (pushApi.getShipmentStatus() == GlobalConstant.CommitStatus.CommitAssign) {
                    updatestatus(pushApi, GlobalConstant.CommitStatus.CommitSecondAttempt);
                }
                else if (pushApi.getShipmentStatus() == GlobalConstant.CommitStatus.CommitSecondAttempt) {
                    updatestatus(pushApi, GlobalConstant.CommitStatus.CommitSyncedFailed);
                }
                writeCrashes(timeStamp, new Exception(throwable));
                Logger.e(TAG, String.valueOf(throwable));
            }));
        } catch (Exception e) {
            valueData = false;
            updatestatus(pushApi, 0);
        }
    }

    @SuppressLint("CheckResult")
    private void rvpShipment(PushApi pushApi, List<RvpCommit.ImageData> imageData) {
        Constants.UPLOADEDS_CALLED = true;
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        final long timeStamp = System.currentTimeMillis();
        writeAnalytics(timeStamp, "Start Uploading RVP Shipments");
        HashMap<String, String> tokens = new HashMap<>();
        tokens.put(Constants.TOKEN, mDataManager.getAuthToken());
        tokens.put(Constants.EMP_CODE, mDataManager.getCode());
        RvpCommit rvpCommit = null;
        try {
            rvpCommit = new ObjectMapper().readValue(pushApi.getRequestData(), RvpCommit.class);
            rvpCommit.setImageData(imageData);
            if (rvpCommit.getStatus().equalsIgnoreCase(Constants.RVPUNDELIVERED) || rvpCommit.getStatus().equalsIgnoreCase("2")) {
                rvpCommit.setStatus(Constants.RVPUNDELIVERED);
                status = Constants.SHIPMENT_UNDELIVERED_STATUS;
            } else {
                rvpCommit.setStatus(Constants.RVPDELIVERED);
                status = Constants.SHIPMENT_DELIVERED_STATUS;
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        final RvpCommit rvpCommitTemp = rvpCommit;
        compositeDisposable.add(mDataManager.doRVPUndeliveredCommitApiCall(mDataManager.getAuthToken(), mDataManager.getEcomRegion(), tokens, rvpCommit).subscribeOn(iSchedulerProvider.io()).subscribe(rvpCommitResponse -> {
            Constants.UPLOADEDS_CALLED = false;
            writeAnalytics(timeStamp, "RVP Commit Response: " + rvpCommitResponse.getStatus());
            if (rvpCommitResponse.getStatus()) {
                if (pushApi.getShipmentCaterogy().equalsIgnoreCase("RQC") && rvpCommitTemp.getDrsId() != null) {
                    deleteRVPQCData(Integer.parseInt(rvpCommitTemp.getDrsId()), Long.parseLong(rvpCommitTemp.getAwb()));
                }
                int shipment_status;
                String compositeKey;
                if (rvpCommitResponse.getResponse().getShipment_status().equalsIgnoreCase(Constants.RVPUNDELIVERED)) {
                    shipment_status = Constants.SHIPMENT_UNDELIVERED_STATUS;
                } else {
                    shipment_status = Constants.SHIPMENT_DELIVERED_STATUS;
                }
                try {
                    compositeKey = rvpCommitResponse.getResponse().getDrs_no() + rvpCommitResponse.getResponse().getAwb_no().trim();
                } catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                    compositeKey = pushApi.getCompositeKey();
                }
                boolean isFromMps;
                isFromMps = rvpCommitTemp != null && rvpCommitTemp.isRvp_mps();
                if(isFromMps){
                    mDataManager.updateRvpMpsStatus(compositeKey, shipment_status).subscribe(aBoolean -> {
                        pushApi.setShipmentStatus(GlobalConstant.CommitStatus.CommitSynced);
                        updateSyncStatusInDRSRVpTable(true, rvpCommitResponse.getResponse().getDrs_no() + rvpCommitResponse.getResponse().getAwb_no());
                        compositeDisposable.add(mDataManager.deleteSyncedImage(rvpCommitResponse.getResponse().getAwb_no()).subscribe(aBoolean1 -> {}));
                        compositeDisposable.add(mDataManager.saveCommitPacket(pushApi).subscribe(aBoolean12 -> sendBoardCast()));
                        // Setting call preference after sync:-
                        mDataManager.setCallClicked(rvpCommitResponse.getResponse().getAwb_no() + "RVPCall", true);
                        valueData = false;
                    }, throwable -> {
                        if (pushApi.getShipmentStatus() == GlobalConstant.CommitStatus.CommitAssign) {
                            updatestatus(pushApi, GlobalConstant.CommitStatus.CommitSecondAttempt);
                        }
                        else if (pushApi.getShipmentStatus() == GlobalConstant.CommitStatus.CommitSecondAttempt) {
                            updatestatus(pushApi, GlobalConstant.CommitStatus.CommitSyncedFailed);
                        }
                        valueData = false;
                    });
                } else{
                    mDataManager.updateRvpStatus(compositeKey, shipment_status).subscribe(aBoolean -> {
                        pushApi.setShipmentStatus(GlobalConstant.CommitStatus.CommitSynced);
                        updateSyncStatusInDRSRVpTable(false, rvpCommitResponse.getResponse().getDrs_no() + rvpCommitResponse.getResponse().getAwb_no());
                        compositeDisposable.add(mDataManager.deleteSyncedImage(rvpCommitResponse.getResponse().getAwb_no()).subscribe(aBoolean1 -> {}));
                        compositeDisposable.add(mDataManager.saveCommitPacket(pushApi).subscribe(aBoolean12 -> sendBoardCast()));
                        // Setting call preference after sync:-
                        mDataManager.setCallClicked(rvpCommitResponse.getResponse().getAwb_no() + "RVPCall", true);
                        valueData = false;
                    }, throwable -> {
                        if (pushApi.getShipmentStatus() == GlobalConstant.CommitStatus.CommitAssign) {
                            updatestatus(pushApi, GlobalConstant.CommitStatus.CommitSecondAttempt);
                        }
                        else if (pushApi.getShipmentStatus() == GlobalConstant.CommitStatus.CommitSecondAttempt) {
                            updatestatus(pushApi, GlobalConstant.CommitStatus.CommitSyncedFailed);
                        }
                        valueData = false;
                    });
                }
            } else if ((rvpCommitResponse.getResponse().getCode().equalsIgnoreCase("E107")) || (rvpCommitResponse.getResponse().getCode().equalsIgnoreCase("107"))) {
                Constants.UPLOADEDS_CALLED = false;
                valueData = false;
                LocalLogout();
            }
        }, throwable -> {
            Constants.UPLOADEDS_CALLED = false;
            if (pushApi.getShipmentStatus() == GlobalConstant.CommitStatus.CommitAssign) {
                updatestatus(pushApi, GlobalConstant.CommitStatus.CommitSecondAttempt);
            }
            else if (pushApi.getShipmentStatus() == GlobalConstant.CommitStatus.CommitSecondAttempt) {
                updatestatus(pushApi, GlobalConstant.CommitStatus.CommitSyncedFailed);
            }
            valueData = false;
        }));
    }

    private void writeAnalytics(long timeStamp, String s) {
        writeLog(AppLogJsonProcessor.LogType.EVENT, timeStamp, s);
    }

    private void writeCrashes(long timeStamp, Exception e) {
        writeLog(AppLogJsonProcessor.LogType.ERROR, timeStamp, getExceptionAsString(e));
    }

    private void writeLog(AppLogJsonProcessor.LogType logType, long timeStamp, String e) {
        AppLogJsonProcessor.appendErrorJSONObject(logType, e, mDataManager.getCurrentLatitude(), mDataManager.getCurrentLongitude(), timeStamp, mDataManager.getCode());
    }

    private void writeCrashes(long timeStamp, String e) {
        writeLog(AppLogJsonProcessor.LogType.ERROR, timeStamp, e);
    }

    private String getExceptionAsString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startLocationUpdates();
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        Constants.CURRENT_LATITUDE = String.valueOf(location.getLatitude());
        Constants.CURRENT_LONGITUDE = String.valueOf(location.getLongitude());
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    private void deleteRVPQCData(int drs, long awbNo) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(mDataManager.deleteQCData(drs, awbNo).subscribeOn(iSchedulerProvider.io()).observeOn(iSchedulerProvider.ui()).subscribe(aBoolean -> {

        }, throwable -> {
        }));

    }

    public void getAllApiUrl() {
        try {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(mDataManager.getAllApiUrl().subscribeOn(iSchedulerProvider.io()).observeOn(iSchedulerProvider.ui()).subscribe(apiUrlData -> {
                try {
                    if (apiUrlData.isEmpty()) {
                        return;
                    }
                    for (int i = 0; i < apiUrlData.size(); i++) {
                        SathiApplication.hashMapAppUrl.put(apiUrlData.get(i).getApiUrlKey(), apiUrlData.get(i).getApiUrl());
                    }
                } catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                }
            }));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void getLiveTrackingID() {
        if (!mDataManager.getTripId().equalsIgnoreCase("")) {
            try {
                LiveTrackingRequest request = new LiveTrackingRequest(mDataManager.getEmp_code(), Integer.parseInt(mDataManager.getTripId()));
                CompositeDisposable compositeDisposable = new CompositeDisposable();
                compositeDisposable.add(mDataManager.dogetLiveTrackingIDApiCall(mDataManager.getAuthToken(), mDataManager.getEcomRegion(), request).doOnSuccess(liveTrackingResponse -> {
                }).subscribeOn(iSchedulerProvider.io()).observeOn(iSchedulerProvider.ui()).subscribe(liveTrackingResponse -> {
                    if (Boolean.parseBoolean(liveTrackingResponse.getStatus())) {
                        mDataManager.setLiveTrackingTripId(liveTrackingResponse.getLive_tracking_id());
                        mDataManager.setLiveTrackingTripIdForApi(liveTrackingResponse.getLive_tracking_id());
                    }
                }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
        }
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        mDataManager.isRecentRemoved(true);
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartServicePendingIntent);
        super.onTaskRemoved(rootIntent);
    }

    private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        }
    };

    private void serviceSendData() {
        sendLiveTrackingStatus();
    }

    public void sendLiveTrackingStatus() {
        try {
            boolean isGPSOnOff = true;
            boolean LocationServiceStatus = isMyServiceRunning(LocationService.class);
            boolean isLocationPermissionOn = false;
            if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    isLocationPermissionOn = true;
                }
            } else {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    isLocationPermissionOn = true;
                }
            }
            boolean doNotOptimizeBattery = optimizeBattery(getApplicationContext());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ts", getDate(System.currentTimeMillis(), "dd/MM/yyyy kk:mm:ss.SSS"));
            jsonObject.put("lat", mDataManager.getCurrentLatitude());
            jsonObject.put("lng", mDataManager.getCurrentLongitude());
            jsonObject.put("gps", isGPSOnOff);
            jsonObject.put("l_s_r", LocationServiceStatus);
            jsonObject.put("i_n_c", String.valueOf(NetworkUtils.isNetworkConnected(getApplicationContext())));
            jsonObject.put("b_t_l", String.valueOf(level));
            jsonObject.put("developer_mode_status", CommonUtils.isDeveloperModeEnabled(getApplicationContext()));
            jsonObject.put("is_recent", String.valueOf(mDataManager.getIsRecentRemoved()));
            jsonObject.put("login_permission", String.valueOf(mDataManager.getLoginPermission()));
            jsonObject.put("is_reboot", String.valueOf(Constants.IS_REBOOT));
            jsonObject.put("is_backgroud_location_permission", String.valueOf(isLocationPermissionOn));
            jsonObject.put("battery_optimization", String.valueOf(doNotOptimizeBattery));
            if (Constants.IS_USING_FAKE_GPS == 1) {
                for (int i = 0; i < mDataManager.getFakeApplications().split("\\|").length; i++) {
                    if (isAppInstalled(this, mDataManager.getFakeApplications().split("\\|")[i])) {
                        jsonObject.put("Fake_GPS_APP", mDataManager.getFakeApplications().split("\\|")[i]);
                        break;
                    }
                }
            } else {
                jsonObject.put("Fake_GPS_APP", "NA");
            }
            LiveTrackingRequestDataDB liveTrackingRequest = new LiveTrackingRequestDataDB();
            String logs = jsonObject.toString();
            LiveTrackingLogTable liveTrackingLog = new LiveTrackingLogTable(mDataManager.getEmp_code(), "Live_track", logs, liveTrackingRequest);
            try {
                CompositeDisposable compositeDisposable = new CompositeDisposable();
                compositeDisposable.add(mDataManager.insertSathiLog(liveTrackingLog).observeOn(iSchedulerProvider.io()).subscribeOn(iSchedulerProvider.io()).subscribe(aBoolean -> {
                }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public boolean optimizeBattery(Context context) {
        boolean isBatteryOptimizationOn = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);
            isBatteryOptimizationOn = pm.isIgnoringBatteryOptimizations(packageName);
        }
        return isBatteryOptimizationOn;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        unregisterReceiver(mBatInfoReceiver);
        return super.onUnbind(intent);
    }

    public boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}