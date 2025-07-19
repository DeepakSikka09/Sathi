package in.ecomexpress.sathi.ui.dashboard.stoptrip;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.geolocations.LocationService;
import in.ecomexpress.geolocations.LocationTracker;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.backgroundServices.SyncServicesV2;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.data.commit.PushApi;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.distancecalculations.DistanceApiResponse;
import in.ecomexpress.sathi.repo.remote.model.trip.ImageResponse;
import in.ecomexpress.sathi.repo.remote.model.trip.StopTripRequest;
import in.ecomexpress.sathi.repo.remote.model.trip.StopTripResponse;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.ui.side_drawer.pendingHistory.PendingHistoryActivity;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import static in.ecomexpress.sathi.SathiApplication.shipmentImageMap;

import javax.inject.Inject;

@HiltViewModel
public class StopTripViewModel extends BaseViewModel<StopTripCallBack> {

    private static final String TAG = StopTripViewModel.class.getSimpleName();
    boolean is_start_clicked = true;
    int pushApiSize = 0;
    private final MediatorLiveData<DistanceApiResponse> distanceCalculationApiResponseMutableLiveData = new MediatorLiveData<>();

    public MediatorLiveData<DistanceApiResponse> getDistanceCalculationApiResponseMutableLiveData() {
        return distanceCalculationApiResponseMutableLiveData;
    }
    private final MediatorLiveData<DistanceApiResponse> distanceCalculationWithSpeedApiResponseMutableLiveData = new MediatorLiveData<>();

    public MediatorLiveData<DistanceApiResponse> getDistanceCalculationWithSpeedApiResponseMutableLiveData() {
        return distanceCalculationWithSpeedApiResponseMutableLiveData;
    }

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Inject
    public StopTripViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication){
        super(dataManager, schedulerProvider, sathiApplication);
    }

    public void onCancelClick(){
        getNavigator().dismissDialog();
    }

    public void uploadAWSImage(Activity context, String fileName, String fileKey, long meterReading, String imageId, float distanceTracking){
        setIsLoading(true);
        LocationTracker.setStropLiveTrackingRunInfo("stop");
        uploadData(context, fileKey, Integer.parseInt(imageId), meterReading, distanceTracking);
    }
    
    @SuppressLint("MissingPermission")
    public void uploadData(Activity context, String imageKey, int imageId, long km, float lib_actual_gps_calculated){
        final long timeStamp = System.currentTimeMillis();
        getNavigator().disableSubmitButton();
        writeEvent(timeStamp, "uploading data for stop trip [image key: " + imageKey + "]");
        try{
            Constants.START_LIVE_TRACKING = false;
            LocationTracker.setStropLiveTrackingRunInfo("stop");
            String imei = CommonUtils.getImei(context);
            imageKey = getFileName(imageKey);
            ImageResponse imageResponse = new ImageResponse(imageKey, imageId);
            float final_calculated_km = 0;
            float randomNumber;
            try{
                float odometer_diff = getNavigator().getStopMeterReadingXML() - getDataManager().getStartTripMeterReading();
                if(lib_actual_gps_calculated == 0){
                    final_calculated_km = (getDataManager().getLiveTrackingCalculatedDistance()) / 1000;
                } else{
                    final_calculated_km = lib_actual_gps_calculated;
                }
                if(final_calculated_km < 0){
                    randomNumber = getRandom(0, 1);
                    final_calculated_km = odometer_diff + randomNumber;
                }
                if(odometer_diff < getDataManager().getLiveTrackingCalculatedDistanceWithSpeed() / 1000){
                    if(getDataManager().getLiveTrackingCalculatedDistanceWithSpeed() / 1000 > 100){
                        final_calculated_km = odometer_diff - getRandom(0, 8);
                    } else{
                        final_calculated_km = getDataManager().getLiveTrackingCalculatedDistance() / 1000;
                    }
                }
                if(final_calculated_km == 0 && getDataManager().getLiveTrackingCount() > 20){
                    randomNumber = getRandom(0, 1);
                    final_calculated_km = odometer_diff + randomNumber;
                }
                if(final_calculated_km == 0 && getDataManager().getLiveTrackingCount() == 0){
                    randomNumber = getRandom(0, 2);
                    final_calculated_km = odometer_diff + randomNumber;
                }
            } catch(Exception e){
                if(getNavigator().getStopVehicleType().equalsIgnoreCase("Others") || getNavigator().getStopVehicleType().equalsIgnoreCase("COUNTER")){
                    final_calculated_km = getDataManager().getLiveTrackingCalculatedDistanceWithSpeed();
                } else{
                    try{
                        final_calculated_km = getNavigator().getStopMeterReadingXML() - getDataManager().getStartTripMeterReading();
                    } catch(Exception e1){
                        Logger.e(TAG, String.valueOf(e1));
                    }
                }
                Logger.e(TAG, String.valueOf(e));
            }
            if(Constants.IS_USING_FAKE_GPS == 1) {
                final_calculated_km = 0;
            }
            StopTripRequest request;
            if(getDataManager().getIsAdmEmp()){
                request = new StopTripRequest(Integer.parseInt(getDataManager().getTripId()), Calendar.getInstance().getTimeInMillis(), 0, getDataManager().getCurrentLatitude(), getDataManager().getCurrentLongitude(), 0, null, getDataManager().getLiveTrackingTripIdForApi(), 0, String.valueOf(getDataManager().getIsAdmEmp()), imei);
            } else{
                request = new StopTripRequest(Integer.parseInt(getDataManager().getTripId()), Calendar.getInstance().getTimeInMillis(), km, getDataManager().getCurrentLatitude(), getDataManager().getCurrentLongitude(), Constants.IS_USING_FAKE_GPS, imageResponse, getDataManager().getLiveTrackingTripIdForApi(), final_calculated_km, String.valueOf(getDataManager().getIsAdmEmp()), imei);
            }
            writeRestAPIRequst(timeStamp, request);
            executeStopTrip(request, context);
        } catch(Exception e){
            is_start_clicked = true;
            getNavigator().enableSubmitButton();
            getNavigator().onHandleError(e.getMessage());
            writeErrors(timeStamp, e);
            StopTripViewModel.this.setIsLoading(false);
            getNavigator().onHandleError(new RestApiErrorHandler(e.fillInStackTrace()).getErrorDetails().getEResponse().getDescription());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public static float getRandom(int min, int max){
        Random rand = new Random();
        return rand.nextFloat() * (max - min) + min;
    }

    private void resetData(){
        try{
            getDataManager().setTripId("0");
            LocationTracker.deleteLatLng();
            LocationTracker.deletetable();
            getDataManager().setLiveTrackingCount(0);
            getDataManager().setLiveTrackingCalculatedDistance(0);
            getDataManager().setLiveTrackingCalculatedDistanceWithSpeed(0);
            LocationTracker.setBothDistanceToZero(getNavigator().getActivityContext());
            getDataManager().setLiveTrackingTripId("");
            getNavigator().cancel();
        } catch(Exception ex){
            StopTripViewModel.this.setIsLoading(false);
            Logger.e(TAG, String.valueOf(ex));
        }
    }

    private String getFileName(String imageKey){
        if(imageKey.indexOf(".") > 0) {
            imageKey = imageKey.substring(0, imageKey.lastIndexOf("."));
        }
        return imageKey;
    }

    public void onStopTrip(){
        getNavigator().StopTrip();
    }

    public void onCameraLaunch(){
        getNavigator().CameraLaunch();
    }

    public String getEmpCode(){
        return getDataManager().getCode();
    }

    public long getStopMeterDiff(){
        return getDataManager().getMaxTripRunForStopTrip();
    }

    public void setFEReachedDC(boolean FEReachedDC){
        getDataManager().setFEReachedDC(FEReachedDC);
    }

    @SuppressLint("CheckResult")
    public void getUnSyncData(){
        try{
            Observable.fromCallable(() -> {
                List<PushApi> pushData;
                pushData = getDataManager().getPushApiUnSyncList().blockingSingle();
                return pushData.size();
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe((result) -> getNavigator().getSize(result));
        } catch(Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void getSizeOfPushApi(){
        try{
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(getDataManager().getSizeOFPushApi().subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(pushApis -> {
                pushApiSize = pushApis.size();
                getNavigator().setPushApiSize(pushApiSize);
            }));
        } catch(Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void getLatLongForStopTrip(){
        try{
            double current_latitude;
            double current_longitude;
            if(getDataManager().getCurrentLatitude() != 0.0 && getDataManager().getCurrentLongitude() != 0.0){
                current_latitude = getDataManager().getCurrentLatitude();
                current_longitude = getDataManager().getCurrentLongitude();
                in.ecomexpress.geolocations.Constants.latitude = current_latitude;
                in.ecomexpress.geolocations.Constants.longitude = current_longitude;
            }
        } catch(Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void saveStopTripDate(){
        getDataManager().setStopTripDate(System.currentTimeMillis());
    }

    public void executeStopTrip(StopTripRequest request, Context context){
        final long timeStamp = System.currentTimeMillis();
        getCompositeDisposable().add(getDataManager().doStopTripApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request).doOnSuccess(new Consumer<StopTripResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void accept(StopTripResponse stopTripResponse){
                try{
                    shipmentImageMap.clear();
                    SathiApplication.rtsCapturedImage1.clear();
                    SathiApplication.rtsCapturedImage2.clear();
                    is_start_clicked = true;
                    writeEvent(timeStamp, "uploading data for stop trip");
                    writeRestAPIResponse(timeStamp, stopTripResponse);
                    getDataManager().setPreviousTripStatus(false);
                    getDataManager().setEndTripTime(Calendar.getInstance().getTimeInMillis());
                    LocationTracker.isStopWriting = true;
                } catch(Exception e){
                    Logger.e(TAG, String.valueOf(e));
                }
            }
        }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
            getNavigator().enableSubmitButton();
            writeEvent(timeStamp, "uploading data for stop trip");
            is_start_clicked = true;
            StopTripViewModel.this.setIsLoading(false);
            if(response.getStatus()){
                getNavigator().dismissDialog();
                resetData();
                getNavigator().setStopMeterReading();
                ContentResolver cr = getNavigator().getActivityContext().getContentResolver();
                cr.delete(Uri.parse("content://in.ecomexpress.sathi.provider/users"), null, null);
                deleteAllTablesOnStopTrip();
                // Deleting images repo where all images are stored:-
                deleteImageDirectory();
            } else if(response.getResponse().getDescription().equalsIgnoreCase("Invalid Authentication Token.")){
                getNavigator().doLogout(response.getResponse().getDescription());
            } else if(!response.getStatus() && String.valueOf(response.getResponse().getCode()).contains(String.valueOf(Constants.INVALID_USER))){
                getNavigator().doLogout(response.getResponse().getDescription());
            } else if(!response.getStatus() && response.getResponse().getStatusCode() == Constants.INVALID_USER){
                getNavigator().doLogout(response.getResponse().getDescription());
            } else if(response.getResponse().getCode() == 301){
                StopTripViewModel.this.getNavigator().onHandleError(response.getResponse().getDescription());
            } else if(response.getResponse().getCode() == 107){
                getNavigator().doLogout(response.getResponse().getDescription());
            } else{
                StopTripViewModel.this.getNavigator().onHandleError(response.getResponse().getDescription());
                getNavigator().dismissDialog();
            }
        }, throwable -> {
            is_start_clicked = true;
            getNavigator().enableSubmitButton();
            writeErrors(timeStamp, new Exception(throwable));
            StopTripViewModel.this.setIsLoading(false);
            String error;
            error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
            try{
                getNavigator().showErrorMessage(error.contains("HTTP 500"));
            } catch(Exception e){
                is_start_clicked = true;
                writeErrors(timeStamp, e);
                getNavigator().onHandleError(e.getMessage());
                Logger.e(TAG, String.valueOf(e));
            }
            getNavigator().dismissDialog();
        }));
    }

    private void deleteAllTablesOnStopTrip(){
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager().deleteAllTablesOnStopTrip().subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {}));
    }

    public void showAlertForPendingSync(){
        if (getNavigator().getActivityContext() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getNavigator().getActivityContext(), R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
        builder.setMessage("Your data is not yet Synced.Press Ok to Sync.");
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            Intent i = new Intent(getNavigator().getActivityContext(), PendingHistoryActivity.class);
            getNavigator().getActivityContext().startActivity(i);
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    public void clearAppData(){
        try{
            getCompositeDisposable().add(getDataManager().deleteAllTables().subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
                try{
                    getDataManager().clearPrefrence();
                    getDataManager().setUserAsLoggedOut();
                    getDataManager().setTripId("0");
                    getDataManager().setIsAdmEmp(false);
                    getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT);
                    getNavigator().getActivityContext().stopService(SyncServicesV2.getStopIntent(getNavigator().getActivityContext()));
                    getNavigator().getActivityContext().stopService(new Intent(getNavigator().getActivityContext(), LocationService.class));
                    SathiApplication.shipmentImageMap.clear();
                    SathiApplication.rtsCapturedImage1.clear();
                    SathiApplication.rtsCapturedImage2.clear();
                } catch(Exception ex){
                    Logger.e(TAG, String.valueOf(ex));
                }
                getNavigator().clearStack();
            }));
        } catch(Exception e){
            getNavigator().onHandleError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void deleteImageDirectory() {
        try{
            File primaryDir = getDirectory(Constants.EcomExpress, true);
            File secondaryDir = getDirectory(Constants.EcomExpress1, false);
            if (primaryDir.exists()) {
                deleteRecursiveAsync(primaryDir);
            }
            if (secondaryDir.exists()) {
                deleteRecursiveAsync(secondaryDir);
            }
        } catch (Exception e){
            Logger.e("StopTripViewModel", String.valueOf(e));
        }
    }

    public void deleteRecursiveAsync(File fileOrDirectory) {
        executor.execute(() -> deleteRecursive(fileOrDirectory));
    }

    private void deleteRecursive(File fileOrDirectory) {
        try{
            if (fileOrDirectory != null && fileOrDirectory.isDirectory()) {
                File[] children = fileOrDirectory.listFiles();
                if (children != null) {
                    for (File child : children) {
                        deleteRecursive(child);
                    }
                }
            }
            if (fileOrDirectory != null) {
                fileOrDirectory.delete();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private File getDirectory(String directoryName, boolean isExternal) {
        File dir;
        if (isExternal) {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), directoryName);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), directoryName);
            } else {
                dir = new File(Environment.getExternalStorageDirectory(), directoryName);
            }
        }
        return dir;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
    public void distanceCalculationApi(String location, Boolean isDistanceCalculationWithSpeed) {
        try{
            LiveData<DistanceApiResponse> obj = getDataManager().distanceCalculationApi(location,"distance");
            if (isDistanceCalculationWithSpeed){
                    distanceCalculationWithSpeedApiResponseMutableLiveData.addSource(obj, selfDropResponse -> {
                    distanceCalculationWithSpeedApiResponseMutableLiveData.removeSource(obj);
                    distanceCalculationWithSpeedApiResponseMutableLiveData.setValue(selfDropResponse);
                });
            }else {
                    distanceCalculationApiResponseMutableLiveData.addSource(obj, selfDropResponse -> {
                    distanceCalculationApiResponseMutableLiveData.removeSource(obj);
                    distanceCalculationApiResponseMutableLiveData.setValue(selfDropResponse);
                });
            }
        } catch (Exception e){
            Log.d(TAG, "distanceCalculationApi: FAILED" + e.getLocalizedMessage());
            }
        }
    }