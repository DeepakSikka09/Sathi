package in.ecomexpress.sathi.ui.dashboard.landing;

import static android.content.Context.LOCATION_SERVICE;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logButtonEventInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.Constants.DISTANCE_API_KEY;
import static in.ecomexpress.sathi.utils.Constants.failedShipmentStatus;
import static in.ecomexpress.sathi.utils.Constants.pendingShipmentAssignedStatus;
import static in.ecomexpress.sathi.utils.Constants.successShipmentStatus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.geolocations.LocationService;
import in.ecomexpress.geolocations.LocationTracker;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.backgroundServices.SathiLocationService;
import in.ecomexpress.sathi.backgroundServices.SyncServicesV2;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.db.model.CommonDRSListItem;
import in.ecomexpress.sathi.repo.local.db.model.RVPQCImageTable;
import in.ecomexpress.sathi.repo.local.db.model.RescheduleEdsD;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.commonrequest.CommonUserIdRequest;
import in.ecomexpress.sathi.repo.remote.model.consignee_profile.Consignee_profile;
import in.ecomexpress.sathi.repo.remote.model.consignee_profile.ProfileFound;
import in.ecomexpress.sathi.repo.remote.model.dp_daily_earned.DPReferenceCodeRequest;
import in.ecomexpress.sathi.repo.remote.model.drs_list.DRSResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.DRSReturnToShipperTypeNewResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.IRTSBaseInterface;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.RvpQualityCheck;
import in.ecomexpress.sathi.repo.remote.model.eds.EdsRescheduleRequest;
import in.ecomexpress.sathi.repo.remote.model.eds.Reschedule_info_awb_list;
import in.ecomexpress.sathi.repo.remote.model.login.LogoutRequest;
import in.ecomexpress.sathi.repo.remote.model.login.MarkAttendanceRequest;
import in.ecomexpress.sathi.repo.remote.model.masterdata.DashboardBanner;
import in.ecomexpress.sathi.repo.remote.model.masterdata.Forward;
import in.ecomexpress.sathi.repo.remote.model.masterdata.GlobalConfigurationMaster;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterDataConfig;
import in.ecomexpress.sathi.repo.remote.model.masterdata.Reverse;
import in.ecomexpress.sathi.repo.remote.model.masterdata.masterRequest;
import in.ecomexpress.sathi.repo.remote.model.trip.DrsCheckListRequest;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.ui.dashboard.refer.ReferFriendActivity;
import in.ecomexpress.sathi.ui.side_drawer.adm.ADMActivity;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.LocationHelper;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.NetworkUtils;
import in.ecomexpress.sathi.utils.TimeUtils;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;

@HiltViewModel
public class DashboardViewModel extends BaseViewModel<IDashboardNavigator> {

    private final String TAG = DashboardViewModel.class.getSimpleName();
    List<ProfileFound> profileFoundList;
    List<DashboardBanner> mydashboardBannerList = new ArrayList<>();
    private final MutableLiveData<Integer> forwardCount = new MutableLiveData<>();
    private final MutableLiveData<Integer> rtsCount = new MutableLiveData<>();
    private final MutableLiveData<Integer> rvpCount = new MutableLiveData<>();
    private final MediatorLiveData<Integer> totalAssignedCount = new MediatorLiveData<>();
    private final MutableLiveData<Integer> successForwardCount = new MutableLiveData<>();
    private final MutableLiveData<Integer> successRTSCount = new MutableLiveData<>();
    private final MutableLiveData<Integer> successRvpCount = new MutableLiveData<>();
    private final MediatorLiveData<Integer> totalSuccessCount = new MediatorLiveData<>();
    private final MutableLiveData<Integer> failedForwardCount = new MutableLiveData<>();
    private final MutableLiveData<Integer> failedRtsCount = new MutableLiveData<>();
    private final MediatorLiveData<Integer> totalFailedCount = new MediatorLiveData<>();
    ObservableField<Integer> unattemptedShipmentCount = new ObservableField<>();
    Consignee_profile consigneeProfile;
    Reschedule_info_awb_list rescheduleData;
    @SuppressLint("StaticFieldLeak")
    Context context;
    EdsRescheduleRequest edsRescheduleRequest;
    JSONArray array;
    ArrayList<RescheduleEdsD> rescheduleEdsDS = new ArrayList<>();
    ProgressDialog dialog;
    boolean isStopClicked = false;
    double distance=0.0;

    @Inject
    public DashboardViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);
        // Update total assigned count:-
        totalAssignedCount.addSource(forwardCount, value -> updateTotalAssignedCount());
        totalAssignedCount.addSource(rtsCount, value -> updateTotalAssignedCount());
        totalAssignedCount.addSource(rvpCount, value -> updateTotalAssignedCount());
        // Update total success count:-
        totalSuccessCount.addSource(successForwardCount, value -> updateSuccessCount());
        totalSuccessCount.addSource(successRTSCount, value -> updateSuccessCount());
        totalSuccessCount.addSource(successRvpCount, value -> updateSuccessCount());
        // Update total failed count:-
        totalFailedCount.addSource(failedForwardCount, value -> updateTotalFailedCount());
        totalFailedCount.addSource(failedRtsCount, value -> updateTotalFailedCount());
        totalFailedCount.addSource(successRvpCount, value -> updateTotalFailedCount());
    }

    public LiveData<Integer> getForwardCount() {
        return forwardCount;
    }

    public LiveData<Integer> getRtsCount() {
        return rtsCount;
    }

    public LiveData<Integer> getRvpCount() {
        return rvpCount;
    }

    public LiveData<Integer> getTotalAssignedCount() {
        return totalAssignedCount;
    }

    public LiveData<Integer> getTotalSuccessCount() {
        return totalSuccessCount;
    }

    public LiveData<Integer> getFailedForwardCount() {
        return failedForwardCount;
    }

    public LiveData<Integer> getSuccessForwardCount() {
        return successForwardCount;
    }

    public LiveData<Integer> getSuccessRTSCount() {
        return successRTSCount;
    }

    public LiveData<Integer> getFailedRtsCount() {
        return failedRtsCount;
    }

    public LiveData<Integer> getSuccessRvpCount() {
        return successRvpCount;
    }

    public LiveData<Integer> getTotalFailedCount() {
        return totalFailedCount;
    }

    public void onReportClick() {
        getNavigator().openFuelReimburse();
    }

    public void onServerClickstatistics() {
        getNavigator().openStatisticsActivity();
    }

    public void onToDoClick() {
        try {
            if (CommonUtils.checkMultiSpace(getNavigator().getActivityContext(), getDataManager()) && !CommonUtils.checkDeveloperMode(getNavigator().getActivityContext())) {
                if (getDataManager().getTripId().equalsIgnoreCase("0")) {
                    getNavigator().noToDo();
                } else {
                    if (!getPreviousTripStatus()) {
                        getNavigator().openTodo();
                    } else {
                        getNavigator().showStringError("Your previous trip is still started. Stop your on going trip.");
                    }
                }
            }
        } catch (Exception e) {
            getNavigator().showStringError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    Boolean getPreviousTripStatus() {
        return getDataManager().getPreviosTripStatus();
    }

    public void onStartTripClick() {
        try {
            if (!CommonUtils.checkMultiSpace(getNavigator().getActivityContext(), getDataManager())) {
                return;
            }
            if (CommonUtils.checkDeveloperMode(getNavigator().getActivityContext())) {
                return;
            }
            if (ContextCompat.checkSelfPermission(getNavigator().getActivityActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getNavigator().getActivityActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 345);
            } else {
                Calendar calendar = Calendar.getInstance();
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                int mMonth = calendar.get(Calendar.MONTH) + 1;
                if (getDataManager().getLoginDate().equalsIgnoreCase(String.valueOf(mDay)) && getDataManager().getLoginMonth() == mMonth) {
                    optimizeBattery(getNavigator().getActivityActivity());
                    if (!(Build.MANUFACTURER + ":" + Build.MODEL).toUpperCase(Locale.US).equals(Constants.ZEBRA)) {
                        Constants.LOCATION_ACCURACY = getDataManager().getDCRANGE();
                        if (!NetworkUtils.isNetworkConnected(getNavigator().getActivityContext())) {
                            getNavigator().showStringError("No Network Connectivity");
                        } else {
                            if (!getDataManager().getTripId().equalsIgnoreCase("0")) {
                                if (getDataManager().getIsAdmEmp()) {
                                    if (getDataManager().getKiranaUser().equalsIgnoreCase("true")) {
                                        getSyncList(getNavigator().getActivityContext(), true);
                                    } else {
                                        if (!getDataManager().getDPUserBarcodeFlag()) {
                                            getSyncList(getNavigator().getActivityContext(), true);
                                        } else {
                                            if (getDataManager().getIsQRCodeScanned()) {
                                                getSyncList(getNavigator().getActivityContext(), true);
                                            } else {
                                                if (getDataManager().isADMUpdated()) {
                                                    getNavigator().scanQRCode();
                                                } else {
                                                    openAlertForADM();
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    if (getDataManager().getEmp_code().startsWith("SF")) {
                                        if (getDataManager().getTripId().equalsIgnoreCase("0")) {
                                            getNavigator().openStartTrip();
                                        } else {
                                            getSyncList(getNavigator().getActivityContext(), true);
                                        }
                                    } else {
//                                        checkRange();
                                        getDistanceBetweenLocations(true);
                                    }
                                }
                            } else {
//                                doDrsCheckStatus(getNavigator().setContext());
                                getDistanceBetweenLocations(false);
                            }
                        }
                    }
                } else {
                    showDayAlert();
                }
            }
        } catch (Exception e) {
            getNavigator().showStringError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void openAlertForADM() {
        AlertDialog.Builder builder = getBuilder();
        AlertDialog dialog_adm = builder.create();
        dialog_adm.setOnShowListener(dialogInterface -> {
            dialog_adm.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getNavigator().getActivityContext().getResources().getColor(R.color.red_ecom));
            dialog_adm.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getNavigator().getActivityContext().getResources().getColor(R.color.green));
        });
        dialog_adm.show();
    }

    @NonNull
    private AlertDialog.Builder getBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getNavigator().getActivityContext(), android.R.style.Theme_Material_Light_Dialog);
        builder.setMessage("Please Update Your Availability For Tomorrow To Stop The Trip.");
        builder.setPositiveButton("I Want To Update", (dialog, which) -> {
            Intent adm = new Intent(getNavigator().getActivityContext(), ADMActivity.class);
            adm.putExtra("from", "dashboard");
            getNavigator().getActivityContext().startActivity(adm);
            applyTransitionToOpenActivity((Activity) getNavigator().getActivityContext());

        });
        builder.setNegativeButton("Already Updated", (dialog, which) -> getDataManager().setADMUpdated(true));
        return builder;
    }

    private void checkRange() {
        if (getDataManager().getTripGeofencing().equalsIgnoreCase("R")) {
//            double calculated_distance = getDistanceBetweenLocations(new LatLng(getDataManager().getDCLatitude(), getDataManager().getDCLongitude()));
            double calculated_distance = distance;
            double calculated_distance_point = LocationHelper.getDistanceBetweenPoint(getDataManager().getDCLatitude(), getDataManager().getDCLongitude(), getDataManager().getCurrentLatitude(), getDataManager().getCurrentLongitude());
            if (calculated_distance < getDataManager().getDCRANGE() || calculated_distance_point < getDataManager().getDCRANGE()) {
                getSyncList(getNavigator().getActivityContext(), true);
            } else if (calculated_distance < 2000 || calculated_distance_point < 2000) {
                getSyncList(getNavigator().getActivityContext(), true);
            } else {
                getNavigator().showAlertWarning("Please Perform This Functionality After Reaching DC. Distance " + calculated_distance / 1000 + " Km away from DC " + "\n\nCurrent Location " + getDataManager().getCurrentLatitude() + " , " + getDataManager().getCurrentLongitude() + "\n\nDC Location " + getDataManager().getDCLatitude() + " , " + getDataManager().getDCLongitude(), "R");
            }
        } else if (getDataManager().getTripGeofencing().equalsIgnoreCase("W")) {
            double calculated_distance = distance;
//            double calculated_distance = getDistanceBetweenLocations(new LatLng(getDataManager().getDCLatitude(), getDataManager().getDCLongitude()));
            double calculated_distance_point = LocationHelper.getDistanceBetweenPoint(getDataManager().getDCLatitude(), getDataManager().getDCLongitude(), getDataManager().getCurrentLatitude(), getDataManager().getCurrentLongitude());
            if (calculated_distance < getDataManager().getDCRANGE() || calculated_distance_point < getDataManager().getDCRANGE()) {
                getSyncList(getNavigator().getActivityContext(), true);
            } else {
                getNavigator().showAlertWarning("You Are Not In DC Location. You Are " + calculated_distance / 1000 + " Km Away From DC " + "\n\nCurrent Location " + getDataManager().getCurrentLatitude() + " , " + getDataManager().getCurrentLongitude() + "\n\nDC Location " + getDataManager().getDCLatitude() + " , " + getDataManager().getDCLongitude(), "W");
            }
        } else {
            getSyncList(getNavigator().getActivityContext(), true);
        }
    }

    public void onSyncClick() {
        try {
            Handler handler = new Handler();
            int delay = 1000;
            handler.postDelayed(new Runnable() {
                public void run() {
                    SathiLocationService.startLocationUpdates(getNavigator().getActivityContext(), getDataManager());
                    handler.postDelayed(this, delay);
                }
            }, delay);
            if (getDataManager().getTripId().equalsIgnoreCase("0")) {
                getNavigator().sendMsg();
            } else {
                if (!getDataManager().getPreviosTripStatus()) {
                    deleteEdsRescheduleData();
                } else {
                    getNavigator().showStringError("Your Previous Trip Is Still Started. Stop Your On Going Trip.");
                }
            }
        } catch (Exception e) {
            getNavigator().showStringError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void deleteEdsRescheduleData() {
        try {
            getCompositeDisposable().add(getDataManager().deleteEdsRescheduleFlag().subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
                try {
                    if (rescheduleEdsDS != null) {
                        insertEdsRescheduleData(rescheduleEdsDS);
                        getNavigator().onSyncClick();
                    }
                } catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                }
            }, Throwable::printStackTrace));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void insertEdsRescheduleData(ArrayList<RescheduleEdsD> response) {
        try {
            getCompositeDisposable().add(getDataManager().insertEdsRescheduleFlag(response).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> getEdsReschdData(), Throwable::printStackTrace));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void getEdsReschdData() {
        try {
            getCompositeDisposable().add(getDataManager().getEdsRescheduleFlag().subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(rescheduleEdsDS -> {
            }));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void onAttendanceClick() {
        getNavigator().onAttendanceClick();
    }

    public void onCampaignClick() {
        getNavigator().onCampaignClick();
    }


    public void onMoreAppClick() {
        if (SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.unify_apps_lms) == null) {
            getNavigator().showStringError("This Feature Is Under Development");
        } else {
            Intent intent = new Intent(getNavigator().getActivityContext(), ReferFriendActivity.class);
            getNavigator().getActivityContext().startActivity(intent);
            applyTransitionToOpenActivity((Activity) getNavigator().getActivityContext());
        }
    }

    public void onTrainingClick() {
        getNavigator().onTrainingClick();
    }

    public void setSyncUnfocused() {
        try {
            getDataManager().setCurrentTimeForDelay(System.currentTimeMillis() + 3 * 60 * 1000);
            getNavigator().disableSyncButton();
        } catch (Exception e) {
            getNavigator().showStringError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void showDelayAlert(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog);
        if (getDataManager().getCurrentTimeForDelay() - System.currentTimeMillis() > 0) {
            builder.setMessage("Please wait  " + (getDataManager().getCurrentTimeForDelay() - System.currentTimeMillis()) / 1000 + " seconds for next syncing...");
            builder.setPositiveButton("Ok", (dialogInterface, i) -> dialogInterface.dismiss());
            Dialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    public void calculateUnAttemptedAssignedShipments() {
        try {
            getCompositeDisposable().add(Observable.zip(getDataManager().getFWDStatusCount(GlobalConstant.ShipmentStatusConstants.ASSIGNED_INT), getDataManager().getRVPStatusCount(GlobalConstant.ShipmentStatusConstants.ASSIGNED_INT), getDataManager().getRTSStatusCount(GlobalConstant.ShipmentStatusConstants.ASSIGNED_INT), getDataManager().getEDSStatusCount(GlobalConstant.ShipmentStatusConstants.ASSIGNED_INT), (aLong1, aLong2, aLong3, aLong4) -> aLong1 + aLong2 + aLong3 + aLong4).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aLong -> {
                if (aLong != null) {
                    unattemptedShipmentCount.set(aLong.intValue());
                }
                if (isStopClicked) {
                    getNavigator().openStopTrip();
                }
            }, throwable -> {
                writeErrors(System.currentTimeMillis(), new Exception(throwable));
                Logger.e(TAG, String.valueOf(throwable));
            }));
        } catch (Exception e) {
            getNavigator().showStringError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public int getUnAttemptedShipmentCount() {
        try {
            return unattemptedShipmentCount.get();
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return 0;
    }

    public void logout() {
        final long timeStamp = System.currentTimeMillis();
        try {
            LogoutRequest logoutRequest = new LogoutRequest();
            logoutRequest.setUsername(getDataManager().getCode());
            logoutRequest.setLogoutLat(getDataManager().getCurrentLatitude());
            logoutRequest.setLogoutLng(getDataManager().getCurrentLongitude());
            writeRestAPIRequst(timeStamp, logoutRequest);
            getCompositeDisposable().add(getDataManager().doLogoutApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), logoutRequest).doOnSuccess(response -> writeRestAPIResponse(timeStamp, response)).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                if (response.isStatus()) {
                    logoutLocal();
                } else {
                    String error;
                    error = response.getResponse().getDescription();
                    if (error.contains("HTTP 500 ")) {
                        getNavigator().showErrorMessage(true);
                    } else {
                        if (error.equalsIgnoreCase("Invalid Authentication Token.")) {
                            logoutLocal();
                        } else {
                            getNavigator().showStringError(response.getResponse().getDescription());
                        }
                    }
                }
            }, throwable -> {
                setIsLoading(false);
                logoutLocal();
                RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(throwable.getCause());
                restApiErrorHandler.writeErrorLogs(timeStamp, throwable.getMessage());
            }));
        } catch (Exception e) {
            getNavigator().showStringError(e.getMessage());
            logoutLocal();
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void logoutLocal() {
        clearAppData();
    }

    private void clearAppData() {
        try {
            getCompositeDisposable().add(getDataManager().deleteAllTables().subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
                try {
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
                } catch (Exception ex) {
                    Logger.e(TAG, String.valueOf(ex));
                }
                getNavigator().clearStack();
            }));
        } catch (Exception e) {
            getNavigator().showStringError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void getAllForwardReasonCodeMasterList() {
        try {
            getCompositeDisposable().add(getDataManager().getAllForwardReasonCodeMasterList().observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(forwardReasonCodeMasters -> getDataManager().setIsMasterDataAvailable(!forwardReasonCodeMasters.isEmpty())));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @SuppressLint("CheckResult")
    public void getMasterData(Context context) {
        showProgressDialog(context);
        try {
            masterRequest request = new masterRequest();
            request.setUsername(getDataManager().getCode());
            getCompositeDisposable().add(getDataManager().doMasterReasonApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request).flatMap((Function<MasterDataConfig, SingleSource<?>>) masterDataReasonCodeResponse -> {
                dismissProgressDialog();
                if (masterDataReasonCodeResponse == null) {
                    getNavigator().showError(context.getString(R.string.api_response_is_null));
                } else {
                    String errorDescription = (masterDataReasonCodeResponse.getResponse() == null || masterDataReasonCodeResponse.getResponse().getDescription() == null) ? context.getString(R.string.api_response_is_false) : masterDataReasonCodeResponse.getResponse().getDescription();
                    getDataManager().saveMasterReason(masterDataReasonCodeResponse).subscribe(aBoolean -> {
                        if (masterDataReasonCodeResponse.getStatus()) {
                            getDataManager().setMasterDataSync(true);
                            try {
                                getNavigator().showSuccess(context.getString(R.string.master_data_synced_successfully));
                                if (masterDataReasonCodeResponse.getResponse().getMaster_data_configurations() != null) {
                                    List<GlobalConfigurationMaster> globalConfigurationMasterList = masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getGlobalConfigurationResponse();
                                    try {
                                        try {
                                            if (masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCustomer_attributes().getForward() != null) {
                                                saveShipperId(masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCustomer_attributes().getForward());
                                            }
                                            if (masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCustomer_attributes().getRVP() != null) {
                                                saveRVPShipperId(masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCustomer_attributes().getRVP());
                                            } else {
                                                Reverse tempReverse = new Reverse(new ArrayList<>(), new ArrayList<>(), 1);
                                                saveRVPShipperId(tempReverse);
                                            }
                                        } catch (Exception e) {
                                            Logger.e(TAG, String.valueOf(e));
                                        }
                                        for (GlobalConfigurationMaster globalConfigurationMaster : globalConfigurationMasterList) {
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("APP_FOOTER")) {
                                                getDataManager().saveBottomText(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("is_internet_api_available")) {
                                                getDataManager().setInternetApiAvailable(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ENABLE_DIRECT_DIAL")) {
                                                getDataManager().setENABLEDIRECTDIAL(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ENABLE_RTS_EMAIL")) {
                                                getDataManager().setENABLERTSEMAIL(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("SAME_DAY_RESCHEDULE")) {
                                                getDataManager().setSAMEDAYRESCHEDULE(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RTS_IMAGE")) {
                                                getDataManager().setRTSIMAGE(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("LIVE_TRACKING_LAT_LNG_ACCURACY")) {
                                                getDataManager().setLIVETRACKINGLATLNGACCURACY(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("SCAN_PACKET_ON_DELIVERY")) {
                                                getDataManager().setIsScanAwb(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("DC_UNDELIVER_STATUS")) {
                                                getDataManager().setDcUndeliverStatus(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("PERFORMANCE_URL")) {
                                                getDataManager().setWebLinkUrl(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("IS_SIGNATURE_IMAGE_MANDATORY")) {
                                                getDataManager().setIsSignatureImageMandatory(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("IS_EDISPUTE_IMAGE_MANDATORY")) {
                                                getDataManager().setEDISPUTE(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("IS_CALL_BRIDGE_CHECK_STATUS")) {
                                                getDataManager().setIsCallBridgeCheckStatus(globalConfigurationMaster.getConfigValue());
                                            }
                                            // Un-attempted Reason Codes.
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("EDS_UNATTEMPTED_CODE")) {
                                                getDataManager().setEDSUnattemptedReasonCode(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("FWD_UNATTEMPTED_CODE")) {
                                                getDataManager().setFWDUnattemptedReasonCode(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RTS_UNATTEMPTED_CODE")) {
                                                getDataManager().setRTSUnattemptedReasonCode(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RVP_UNATTEMPTED_CODE")) {
                                                getDataManager().setRVPUnattemptedReasonCode(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("CONSIGNEE_PROFILING_ENABLE")) {
                                                getDataManager().setConsigneeProfiling(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ENABLE_VOICE_CALL_OTP")) {
                                                getDataManager().setVCallpopup(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("MULTISPACE_APPS")) {
                                                getDataManager().setMultiSpaceApps(globalConfigurationMaster.getConfigValue());
                                            }
                                            // Feedback Popup work:-
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("FEEDBACK_MESSAGE")) {
                                                getDataManager().setFeedbackMessage(globalConfigurationMaster.getConfigValue());
                                            }
                                            // Blur Image Work:-
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("BLUR_IMAGE_TYPE")) {
                                                getDataManager().setBlurImageType(globalConfigurationMaster.getConfigValue());
                                            }
                                            // For Attendance Feature Enable or Disable:-
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("SATHI_ATTENDANCE_FEATURE_ENABLE")) {
                                                getDataManager().setSathiAttendanceFeatureEnable(Boolean.valueOf(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("IMAGE_MANUAL_FLYER")) {
                                                getDataManager().setImageManualFlyer(Boolean.valueOf(globalConfigurationMaster.getConfigValue()));
                                            }
                                            // BP mismatch:-
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("BP_MISMATCH")) {
                                                getDataManager().setBPMismatch(Boolean.valueOf(globalConfigurationMaster.getConfigValue()));
                                            }
                                            // UD BP Reason Code:-
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("UD_BP")) {
                                                getDataManager().setUDBPCode(globalConfigurationMaster.getConfigValue());
                                            }
                                            // Hide Camera in RQC:-
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("HIDE_CAMERA")) {
                                                getDataManager().setHideCamera(Boolean.valueOf(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ESP_EARNING_VISIBILITY")) {
                                                getDataManager().setESP_EARNING_VISIBILITY(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("SMS_THROUGH_WHATSAPP")) {
                                                getDataManager().setSMSThroughWhatsapp(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("TECHPAK_WHATSAPP")) {
                                                getDataManager().setTechparkWhatsapp(String.valueOf(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("TRIEDREACHYOU_WHATSAPP")) {
                                                getDataManager().setTriedReachyouWhatsapp(String.valueOf(globalConfigurationMaster.getConfigValue()));
                                            }
                                            // DP_Users_Barcode_Scan_Work:-
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ESP_SCANNER")) {
                                                getDataManager().setDPUserBarcodeFlag(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("APP_FOOTER")) {
                                                getDataManager().saveBottomText(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("Forward")) {
                                                getDataManager().setForwardReasonCodeFlag(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("Rts")) {
                                                getDataManager().setRTSReasonCodeFlag(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RTS_INPUT_OTP_RESEND")) {
                                                getDataManager().setRtsInputResendFlag(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RVP")) {
                                                getDataManager().setRVPReasonCodeFlag(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("EDS")) {
                                                getDataManager().setEDSReasonCodeFlag(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("PERFORMANCE_URL")) {
                                                getDataManager().setWebLinkUrl(globalConfigurationMaster.getConfigValue());
                                            }
                                            // Start stop trip:-
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("MAX_DAILY_DIFF_FOR_START_TRIP")) {
                                                getDataManager().setMaxDailyDiffForStartTrip(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("MAX_TRIP_RUN_FOR_STOP_TRIP")) {
                                                getDataManager().setMaxTripRunForStopTrip(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("STOP_TRIP_ALERT_KM")) {
                                                getDataManager().setStartStopTripMeterReadingDiff(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("IT_HELP_CALL_BRIDGE")) {
                                                getDataManager().setCallITExecutiveNo(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("SOS_NUMBERS")) {
                                                getDataManager().setSOSNumbers(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("SOS_SMS_TEMPLATE")) {
                                                getDataManager().setSOSSMSTemplate(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("EDS_REAL_TIME_IMAGE_SYNC")) {
                                                getDataManager().saveEDSRealTimeSync(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RVP_REAL_TIME_IMAGE_SYNC")) {
                                                getDataManager().saveRVPRealTimeSync(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("CONSIGNEE_PROFILING")) {
                                                getDataManager().setConsigneeProfileValue(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("DC_RANGE")) {
                                                getDataManager().setDCRANGE(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("CAMPAIGN_VISIBILITY")) {
                                                getDataManager().setCampaignStatus(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("REQUEST_RESPONSE_TIME")) {
                                                getDataManager().setREQUEST_RESPONSE_TIME(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("REQUEST_RESPONSE_COUNT")) {
                                                getDataManager().setREQUEST_RESPONSE_COUNT(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("IS_KIRANA_USER")) {
                                                getDataManager().setKiranaUser(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("UNDELIVERED_CONSIGNEE_RANGE")) {
                                                getDataManager().setUndeliverConsigneeRANGE(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("TRIP_GEOFENCING")) {
                                                getDataManager().setTripGeofencing(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("MAX_DAILY_DIFF_FOR_START_TRIP")) {
                                                getDataManager().setMaxDailyDiffForStartTrip(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("MAX_TRIP_RUN_FOR_STOP_TRIP")) {
                                                getDataManager().setMaxTripRunForStopTrip(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ALLOW_DUPLICATE_CASH_RECEIPT")) {
                                                getDataManager().setDuplicateCashReceipt(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("SYNC_SERVICE_INTERVAL")) {
                                                getDataManager().setSyncDelay(Long.parseLong(globalConfigurationMaster.getConfigValue()));
                                                Constants.SYNC_DELAY_TIME = Long.parseLong(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("LIVE_TRACKING_MAX_FILE_SIZE")) {
                                                getDataManager().setLiveTrackingMaxFileSize(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("AADHAR_CONSENT_DISCLAIMER")) {
                                                getDataManager().setAdharMessage(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RVP_BARCODE_FLYER")) {
                                                getDataManager().setRVPAWBWords(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RVP_UD_FLYER")) {
                                                getDataManager().setRVP_UD_FLYER(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("AADHAR_MASKING_STATUS_CHECK_INTERVAL")) {
                                                getDataManager().setAadharStatusInterval(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("UNDELIVERED_COUNT")) {
                                                getDataManager().setUndeliverCount(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("MAX_EDS_EKYC_FAIL_ATTEMPT")) {
                                                getDataManager().setMaxEDSFailAttempt(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("LAT_LNG_BATCH_COUNT")) {
                                                getDataManager().setLatLngLimit(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("LIVE_TRACKING_DEVICE_MOVEMENT_MAX_SPEED")) {
                                                getDataManager().setLiveTrackingSpeed(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("LIVE_TRACKING_LAT_LNG_ACCURACY")) {
                                                getDataManager().setLiveTrackingAccuracy(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("LIVE_TRACKING_LAT_LNG_MIN_DISPLACEMENT")) {
                                                getDataManager().setLiveTrackingDisplacement(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("LIVE_TRACKING_LAT_LNG_TIME_INTERVAL")) {
                                                getDataManager().setLiveTrackingInterval(String.valueOf(Long.parseLong(globalConfigurationMaster.getConfigValue()) * 1000));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("LIVE_TRACKING_DEVICE_MOVEMENT_MIN_SPEED")) {
                                                getDataManager().setLiveTrackingMINSpeed(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("CALL_STATUS_API_INTERVAL")) {
                                                getDataManager().setCallStatusApiInterval(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ENABLE_GET_CALL_STATUS_API")) {
                                                getDataManager().setDirectUndeliver(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ENABLE_CALL_API_RECURSION")) {
                                                getDataManager().setCallAPIRecursion(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("CALL_STATUS_API_RECURSION_INTERVAL")) {
                                                getDataManager().setRequestResponseTime(Long.parseLong(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ENABLE_DP_EMPLOYEE")) {
                                                getDataManager().setEnableDPEmployee(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("COD_STATUS_INTERVAL")) {
                                                getDataManager().setCodStatusInterval(Long.parseLong(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RVP_RQC_BARCODE_SCAN")) {
                                                getDataManager().setRVPRQCBarcodeScan(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("COD_STATUS_INTERVAL_FRACTION")) {
                                                getDataManager().setCodStatusIntervalStatusFraction(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RESCHEDULE_MAX_ATTEMPTS")) {
                                                getDataManager().setRescheduleMaxAttempts(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RESCHEDULE_MAX_ATTEPMTS_ALLOWED_DAYS")) {
                                                getDataManager().setRescheduleMaxDaysAllow(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("IS_USE_CAMSCANNER_PRINT_RECEIPT")) {
                                                getDataManager().setIsUseCamscannerPrintReceipt(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("IS_USE_CAMSCANNER_DISPUTE")) {
                                                getDataManager().setIsUseCamscannerDispute(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("IS_USE_CAMSCANNER_TRIP")) {
                                                getDataManager().setIsUseCamscannerTrip(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("SATHI_LOG_API_CALL_INTERVAL")) {
                                                getDataManager().setSathiLogApiCallInterval(Long.parseLong(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("DISTANCE_GAP_FOR_DIRECTION_CAL")) {
                                                getDataManager().setDistance(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("offline_fwd")) {
                                                getDataManager().setOfflineFwd(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("CONSENT_AGREE_BUTTON_LABEL")) {
                                                getDataManager().setAdharPositiveButton(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("CONSENT_DISAGREE_BUTTON_LABEL")) {
                                                getDataManager().setAdharNegativeButton(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("DISABLE_RESEND_OTP_BUTTON_DURATION")) {
                                                getDataManager().setDisableResendOtpButtonDuration(Long.parseLong(globalConfigurationMaster.getConfigValue()));
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ESP_REFER_SCHEME_TERM_AND_CONDITIONS")) {
                                                getDataManager().setESPSchemeTerms(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ESP_REFERRAL_CODE")) {
                                                getDataManager().setESPReferCode(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("address_quality_score")) {
                                                getDataManager().setAddressQualityScore(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("SKIP_OTP_REV_RQC")) {
                                                getDataManager().setSKIPOTPREVRQC(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("SKIP_CANC_OTP_RVP")) {
                                                getDataManager().setSKIP_CANC_OTP_RVP(globalConfigurationMaster.getConfigValue());
                                            }
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("FAKE_APPLICATIONS")) {
                                                if (!globalConfigurationMaster.getConfigValue().equalsIgnoreCase("")) {
                                                    getDataManager().setFakeApplicatons(globalConfigurationMaster.getConfigValue());
                                                    LocationTracker.setFakeApplications(getDataManager().getFakeApplications());
                                                }
                                            }
                                            // Distance Api Enable:-
                                            if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("IS_DISTANCE_API_ENABLE")) {
                                                getDataManager().setDistanceAPIEnabled(Boolean.valueOf(globalConfigurationMaster.getConfigValue()));
                                            }
                                        }
                                        boolean exists = false;
                                        if (masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCallbridgeConfiguration().getCb_pstn_options() != null && !masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCallbridgeConfiguration().getCb_pstn_options().isEmpty()) {
                                            getDataManager().setPSTNType(masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCallbridgeConfiguration().getCb_calling_type());
                                            if (getDataManager().getPstnFormat().equalsIgnoreCase("")) {
                                                getDataManager().setPstnFormat(masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCallbridgeConfiguration().getCb_pstn_options().get(0).getPstn_format());
                                            } else {
                                                for (int i = 0; i < masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCallbridgeConfiguration().getCb_pstn_options().size(); i++) {
                                                    if (getDataManager().getPstnFormat().equalsIgnoreCase(masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCallbridgeConfiguration().getCb_pstn_options().get(i).pstn_format)) {
                                                        getDataManager().setPstnFormat(masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCallbridgeConfiguration().getCb_pstn_options().get(i).getPstn_format());
                                                        exists = true;
                                                        break;
                                                    }
                                                }
                                            }
                                            if (!exists) {
                                                getDataManager().setPstnFormat(masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCallbridgeConfiguration().getCb_pstn_options().get(0).getPstn_format());
                                            }
                                        }
                                    } catch (Exception e) {
                                        getNavigator().showError(e.getMessage());
                                    }
                                }
                            } catch (Exception e) {
                                getNavigator().showError(e.getMessage());
                            }
                        } else {
                            if (errorDescription.equalsIgnoreCase(context.getString(R.string.invalid_authentication_token))) {
                                getNavigator().doLogout(errorDescription);
                            } else {
                                getNavigator().showError(errorDescription);
                            }
                        }
                    });
                }
                return Single.just(true);
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(o -> {
            }, throwable -> {
                dismissProgressDialog();
                getNavigator().showError(throwable.getMessage());
            }));
        } catch (Exception e) {
            dismissProgressDialog();
            getNavigator().showError(e.getMessage());
        }
    }

    public void saveShipperId(Forward response) {
        try {
            getCompositeDisposable().add(getDataManager().insertFwdShipperId(response).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
            }, Throwable::printStackTrace));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void saveRVPShipperId(Reverse reverse) {
        try {
            getCompositeDisposable().add(getDataManager().insertRvpShipperId(reverse).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
            }, Throwable::printStackTrace));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void getAllDashboardBanner() {
        try {
            getCompositeDisposable().add(getDataManager().getAllDashboardBanner().observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(dashboardBannerList -> {
                if (dashboardBannerList != null) {
                    mydashboardBannerList = dashboardBannerList;
                    getNavigator().dashboardBannerList(mydashboardBannerList);
                }
            }));
        } catch (Exception e) {
            getNavigator().showStringError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void setConsigneeName(TextView consigneeName) {
        consigneeName.setText(getDataManager().getName());
    }

    public String getEmpCode() {
        return getDataManager().getCode();
    }

    public String getVehicleType() {
        return getDataManager().getVehicleType();
    }

    public String getLocationCode() {
        return getDataManager().getLocationCode();
    }

    public boolean getRootDeviceDisabled() {
        return getDataManager().getRootDeviceDisabled();
    }

    public void getAllApiUrl(String type) {
        try {
            getCompositeDisposable().add(getDataManager().getAllApiUrl().subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(apiUrlData -> {
                for (int i = 0; i < apiUrlData.size(); i++) {
                    SathiApplication.hashMapAppUrl.put(apiUrlData.get(i).getApiUrlKey(), apiUrlData.get(i).getApiUrl());
                }
                if (type.equalsIgnoreCase("Sync")) {
                    getNavigator().callSync();
                }
                if (type.equalsIgnoreCase("onCreate")) {
                    getNavigator().callCheckAttendanceApi();
                }
            }));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void doDrsCheckStatus(Context context) {
        setIsLoading(true);
        try {
            DrsCheckListRequest drsCheckListRequest = new DrsCheckListRequest();
            drsCheckListRequest.setEmployee_code(getDataManager().getEmp_code());
            final long timeStamp = System.currentTimeMillis();
            writeRestAPIRequst(timeStamp, drsCheckListRequest);
            getCompositeDisposable().add(getDataManager().doDrsListCheck(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), drsCheckListRequest).doOnSuccess(drsCheckListResponse -> {
                setIsLoading(false);
                writeRestAPIResponse(timeStamp, drsCheckListRequest);
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(drsCheckListResponse -> {
                double dc_latitude = 0.0;
                double dc_longitude = 0.0;
                double current_latitude = 0.0;
                double current_longitude = 0.0;
                String description = (drsCheckListResponse.getResponse() == null || drsCheckListResponse.getResponse().getDescription() == null) ? "" : drsCheckListResponse.getResponse().getDescription();
                try {
                    if (drsCheckListResponse.getStatus().equalsIgnoreCase("true")) {
                        try {
                            dc_latitude = getDataManager().getDCLatitude();
                            dc_longitude = getDataManager().getDCLongitude();
                        } catch (Exception e) {
                            getNavigator().showStringError("Unable To Get DC Location...");
                            Logger.e(TAG, String.valueOf(e));
                        }
                        if (getDataManager().getCurrentLatitude() != 0.0 && getDataManager().getCurrentLongitude() != 0.0) {
                            current_latitude = getDataManager().getCurrentLatitude();
                            current_longitude = getDataManager().getCurrentLongitude();
                            in.ecomexpress.geolocations.Constants.latitude = current_latitude;
                            in.ecomexpress.geolocations.Constants.longitude = current_longitude;
                        }
                        LocationManager service = (LocationManager) getNavigator().getActivityContext().getSystemService(LOCATION_SERVICE);
                        boolean gpsIsEnabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
                        if (getDataManager().getTripGeofencing().equalsIgnoreCase("R")) {
                            if (getDataManager().getEmp_code().startsWith("SF")) {
                                if (getDataManager().getTripId().equalsIgnoreCase("0")) {
                                    getNavigator().openStartTrip();
                                } else {
                                    getNavigator().openStopTrip();
                                }
                            } else {
                                if (gpsIsEnabled) {
                                    double calculated_distance = distance;
//                                    double calculated_distance = getDistanceBetweenLocations(new LatLng(getDataManager().getDCLatitude(), getDataManager().getDCLongitude()));
                                    double calculated_distance_point = LocationHelper.getDistanceBetweenPoint(dc_latitude, dc_longitude, current_latitude, current_longitude);
                                    if (calculated_distance < getDataManager().getDCRANGE() || calculated_distance_point < getDataManager().getDCRANGE()) {
                                        if (getDataManager().getTripId().equalsIgnoreCase("0")) {
                                            getNavigator().openStartTrip();
                                        } else {
                                            getNavigator().openStopTrip();
                                        }
                                    } else if (current_latitude == 0.0 && current_longitude == 0.0) {
                                        if (getDataManager().getTripId().equalsIgnoreCase("0")) {
                                            getNavigator().openStartTrip();
                                        } else {
                                            getNavigator().openStopTrip();
                                        }
                                    } else if (calculated_distance < 2000 || calculated_distance_point < 2000) {
                                        if (getDataManager().getTripId().equalsIgnoreCase("0")) {
                                            getNavigator().openStartTrip();
                                        } else {
                                            getNavigator().openStopTrip();
                                        }
                                    } else if (calculated_distance > getDataManager().getDCRANGE() || calculated_distance_point > getDataManager().getDCRANGE()) {
                                        getNavigator().showAlertWarning("Please perform this functionality after reaching DC. You are " + calculated_distance / 1000 + " Km away from DC " + "\n\nCurrent Location " + getDataManager().getCurrentLatitude() + " , " + getDataManager().getCurrentLongitude() + "\n\nDC Location " + getDataManager().getDCLatitude() + " , " + getDataManager().getDCLongitude(), "R");
                                    }
                                } else {
                                    try {
                                        if (context != null) {
                                            if (!getDataManager().getTripId().equalsIgnoreCase("0") && !getDataManager().getIsAdmEmp()) {
                                                LocationTracker.getInstance(context, getNavigator().getActivityActivity(), true, false).runLocationServices();
                                            }
                                            getNavigator().showError("Unable To Get Your Location. Reset Your GPS Setting And Try Again.");
                                        } else {
                                            getNavigator().showError("Unable To Get Your Location. Reset Your GPS Setting And Try Again.");
                                        }
                                    } catch (Exception e) {
                                        getNavigator().showError("Unable To Get Your Location. Reset Your GPS Setting And Try Again.");
                                        Logger.e(TAG, String.valueOf(e));
                                    }
                                }
                            }
                        } else if (getDataManager().getTripGeofencing().equalsIgnoreCase("W")) {
                            if (getDataManager().getEmp_code().startsWith("SF")) {
                                if (getDataManager().getTripId().equalsIgnoreCase("0")) {
                                    getNavigator().openStartTrip();
                                } else {
                                    getNavigator().openStopTrip();
                                }
                            } else {
                                if (gpsIsEnabled) {
                                    double calculated_distance_point = LocationHelper.getDistanceBetweenPoint(dc_latitude, dc_longitude, current_latitude, current_longitude);
                                    double calculated_distance = distance;
//                                    double calculated_distance = getDistanceBetweenLocations(new LatLng(getDataManager().getDCLatitude(), getDataManager().getDCLongitude()));
                                    if (calculated_distance < getDataManager().getDCRANGE() || calculated_distance_point < getDataManager().getDCRANGE()) {
                                        if (getDataManager().getTripId().equalsIgnoreCase("0")) {
                                            getNavigator().openStartTrip();
                                        } else {
                                            getNavigator().openStopTrip();
                                        }
                                    } else if (current_latitude == 0.0 && current_longitude == 0.0) {
                                        if (getDataManager().getTripId().equalsIgnoreCase("0")) {
                                            getNavigator().openStartTrip();
                                        } else {
                                            getNavigator().openStopTrip();
                                        }
                                    } else if (calculated_distance > getDataManager().getDCRANGE() || calculated_distance_point > getDataManager().getDCRANGE()) {
                                        getNavigator().showAlertWarning("You Are Not In DC Location. You Are " + calculated_distance / 1000 + " Km Away From DC. " + "\n\nCurrent Location " + getDataManager().getCurrentLatitude() + " , " + getDataManager().getCurrentLongitude() + "\n\nDC Location " + getDataManager().getDCLatitude() + " , " + getDataManager().getDCLongitude(), "W");
                                    }
                                } else {
                                    try {
                                        if (context != null) {
                                            if (!getDataManager().getTripId().equalsIgnoreCase("0") && !getDataManager().getIsAdmEmp()) {
                                                LocationTracker.getInstance(context, getNavigator().getActivityActivity(), true, false).runLocationServices();
                                            }
                                            getNavigator().showError("Unable To Get Your Location. Reset Your GPS Setting And Try Again.");
                                        } else {
                                            getNavigator().showError("Unable To Get Your Location. Reset Your GPS Setting And Try Again.");
                                        }
                                    } catch (Exception e) {
                                        getNavigator().showError("Unable To Get Your Location. Reset Your GPS Setting And Try Again.");
                                    }
                                }
                            }
                        } else {
                            if (getDataManager().getTripId().equalsIgnoreCase("0")) {
                                getNavigator().openStartTrip();
                            } else {
                                getNavigator().openStopTrip();
                            }
                        }
                    } else if (description.equalsIgnoreCase("Invalid Authentication Token.")) {
                        getNavigator().doLogout(drsCheckListResponse.getResponse().getDescription());
                    } else if (!description.isEmpty()) {
                        getNavigator().showStringError(drsCheckListResponse.getResponse().getDescription());
                    } else {
                        getNavigator().showStringError("Can't Start Trip Because DRS Is Not Assigned");
                    }
                } catch (Exception e) {
                    getNavigator().showStringError("Check DRS API Failed.");
                }
            }, throwable -> {
                setIsLoading(false);
                getNavigator().showStringError("Check DRS API Failed.");
            }));
        } catch (Exception e) {
            setIsLoading(false);
            getNavigator().showStringError("Check DRS API Failed.");
        }
    }

//    public double getDistanceBetweenLocations(LatLng destination) {
//        try {
//            double distance;
//            GeoApiContext context = new GeoApiContext().setApiKey(DISTANCE_API_KEY);
//            DirectionsResult result = DirectionsApi.newRequest(context).mode(TravelMode.DRIVING).units(Unit.METRIC).origin(new LatLng(getDataManager().getCurrentLatitude(), getDataManager().getCurrentLongitude())).optimizeWaypoints(true).destination(destination).awaitIgnoreError();
//            String dis = (result.routes[0].legs[0].distance.humanReadable);
//            if (dis.endsWith("km")) {
//                distance = Double.parseDouble(dis.replaceAll("[^\\d.]", "")) * 1000;
//            } else {
//                distance = Double.parseDouble(dis.replaceAll("[^\\d.]", ""));
//            }
//            return distance;
//        } catch (Exception e) {
//            Logger.e(TAG, String.valueOf(e));
//        }
//        return 0.0;
//    }
    public  void getDistanceBetweenLocations(Boolean isCheckRange) {
            final long timeStamp = System.currentTimeMillis();
            try {
                final StringBuilder[] builder = {new StringBuilder()};
                builder[0].append(getDataManager().getCurrentLongitude());
                builder[0].append(",");
                builder[0].append(getDataManager().getCurrentLatitude());
                builder[0].append(";");
                builder[0].append(getDataManager().getDCLongitude());
                builder[0].append(",");
                builder[0].append(getDataManager().getDCLatitude());
                getCompositeDisposable().add(getDataManager().distanceCalculationApis(builder[0].toString(), "distance").doOnSuccess(response -> writeRestAPIResponse(timeStamp, response)
                ).subscribeOn(getSchedulerProvider().ui()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                    distance = Math.round(response.getDistances().get(0).get(1));
                    if (isCheckRange) {
                        checkRange();
                    } else {
                        doDrsCheckStatus(getNavigator().setContext());
                    }
                }, throwable -> {
                }));
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
        }
    }

    public void DPScanReferenceCode(String ref_code) {
        setIsLoading(true);
        try {
            DPReferenceCodeRequest request = new DPReferenceCodeRequest();
            request.setDrs_id(getDataManager().getDRSId());
            request.setRef_code(ref_code);
            getCompositeDisposable().add(getDataManager().doDPReferecenApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request).doOnSuccess(dpReferenceCodeResponse -> setIsLoading(false)).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                setIsLoading(false);
                if (response.getStatus()) {
                    getNavigator().showSuccess("QR Code scanned.");
                    getDataManager().setIsQRCodeScanned(true);
                } else {
                    getNavigator().showError(response.getDescription());
                }
            }, throwable -> {
                setIsLoading(false);
                String error;
                try {
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                    getNavigator().showErrorMessage(error.contains("HTTP 500 "));
                } catch (NullPointerException e) {
                    Logger.e(TAG, String.valueOf(e));
                }
            }));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
            setIsLoading(false);
            getNavigator().showError(new RestApiErrorHandler(e.fillInStackTrace()).getErrorDetails().getEResponse().getDescription());
        }
    }

    public void drsData(Context context) {
        onDRSListApiCall(context, false);
    }

    public void onDRSListApiCall(final Context context, Boolean flag) {
        this.context = context;
        getSyncList(context, false);
    }

    public void getSyncList(Context context, boolean isFromStopTrip) {
        try {
            logButtonEventInGoogleAnalytics(TAG, context.getString(R.string.manuallydrssyncclick), getDataManager().getCode(), context);
            ProgressDialog dialog;
            dialog = new ProgressDialog(context, android.R.style.Theme_Material_Light_Dialog);
            dialog.show();
            dialog.setCancelable(false);
            dialog.setMessage(context.getString(R.string.synchronizing_wait));
            dialog.setIndeterminate(true);
            CommonUserIdRequest request = new CommonUserIdRequest(String.valueOf(getDataManager().getCode()));
            final long timeStamp = System.currentTimeMillis();
            writeRestAPIRequst(timeStamp, request);
            try {
                getCompositeDisposable().add(getDataManager().doDRSListApiNewCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request).doOnSuccess(drsResponse -> {
                    writeRestAPIResponse(timeStamp, drsResponse);
                    try {
                        if (drsResponse.getStatus()) {
                            if (drsResponse.getTodoResponse().getDrs_list_response() == null) {
                                getNavigator().showError(context.getString(R.string.drs_list_processing_api_response_null));
                            } else {
                                if (drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList() != null && !drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().isEmpty()) {
                                    for (int i = 0; i < drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().size(); i++) {
                                        updateOTPForward(drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().get(i).getAwbNo(), drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().get(i).getShipmentDetails().getOfd_otp());
                                    }
                                    for (int i = 0; i < drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().size(); i++) {
                                        updateTotalAttemptsForward(drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().get(i).getAwbNo(), drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().get(i).getTotal_attempts());
                                    }
                                    saveForwardList(drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList());
                                    if (!drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().isEmpty()) {
                                        getDataManager().setDRSId((long) drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().get(0).getDrsId());
                                    }
                                }
                                // Saving Data to Room DataBase for RTS
                                if (drsResponse.getTodoResponse().getDrs_list_response().getDrsReturnToShipperTypeNewResponse() != null) {
                                    if (drsResponse.getTodoResponse().getDrs_list_response().getDrsReturnToShipperTypeNewResponse().getVendorResponse() != null || drsResponse.getTodoResponse().getDrs_list_response().getDrsReturnToShipperTypeNewResponse().getWarehouseResponse() != null) {
                                        saveNewRTSList(drsResponse.getTodoResponse().getDrs_list_response().getDrsReturnToShipperTypeNewResponse());
                                    }
                                }
                                // Saving Data to Room DataBase for RVP
                                if (drsResponse.getTodoResponse().getDrs_list_response().getRevDrsList() != null && !drsResponse.getTodoResponse().getDrs_list_response().getRevDrsList().isEmpty()) {
                                    saveRVPList(drsResponse.getTodoResponse().getDrs_list_response().getRevDrsList());
                                    getDataManager().setDRSId((long) drsResponse.getTodoResponse().getDrs_list_response().getRevDrsList().get(0).getDrs());
                                }
                                // Saving data to room for EDS
                                if (drsResponse.getTodoResponse().getDrs_list_response().getEdsList() != null && !drsResponse.getTodoResponse().getDrs_list_response().getEdsList().isEmpty()) {
                                    for (int i = 0; i < drsResponse.getTodoResponse().getDrs_list_response().getEdsList().size(); i++) {
                                        updateOTPEDS(drsResponse.getTodoResponse().getDrs_list_response().getEdsList().get(i).getAwbNo(), drsResponse.getTodoResponse().getDrs_list_response().getEdsList().get(i).getShipmentDetail().getOfd_otp());
                                    }
                                    getDataManager().setDRSId(drsResponse.getTodoResponse().getDrs_list_response().getEdsList().get(0).getDrsNo());
                                    saveEDSNewList(drsResponse.getTodoResponse().getDrs_list_response().getEdsList());
                                }
                                getAllNewDRS(drsResponse, dialog);
                            }
                        } else {
                            getNavigator().showError("DRS List Processing API Response False");
                        }
                    } catch (Exception ex) {
                        String error;
                        try {
                            error = new RestApiErrorHandler(ex).getErrorDetails().getEResponse().getDescription();
                            if (error.contains("HTTP 500 ")) {
                                getNavigator().showErrorMessage(true);
                            } else {
                                getNavigator().showError(error);
                            }
                        } catch (Exception e) {
                            Logger.e(TAG, String.valueOf(e));
                            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                            restApiErrorHandler.writeErrorLogs(timeStamp, e.getMessage());
                        }
                    }
                }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(drsResponse -> {
                    try {
                        isStopClicked = isFromStopTrip;
                        new Handler().postDelayed(() -> getNavigator().enableSyncButton(), 1000);
                        setIsLoading(false);
                        if ((drsResponse.getTodoResponse() != null) && (drsResponse.getTodoResponse().getStatusCode() == 107)) {
                            getNavigator().doLogout(drsResponse.getTodoResponse().getDescription());
                        }
                    } catch (Exception e) {
                        new Handler().postDelayed(() -> getNavigator().enableSyncButton(), 1000);
                        Logger.e(TAG, String.valueOf(e));
                        RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                        restApiErrorHandler.writeErrorLogs(timeStamp, e.getMessage());
                    }
                }, throwable -> {
                    setIsLoading(false);
                    String error;
                    try {
                        dialog.dismiss();
                        new Handler().postDelayed(() -> getNavigator().enableSyncButton(), 1000);
                        error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                        getNavigator().showErrorMessage(error.contains("HTTP 500 "));
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                        RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                        restApiErrorHandler.writeErrorLogs(timeStamp, e.getMessage());
                    }
                }));
            } catch (Exception e) {
                setIsLoading(false);
                Logger.e(TAG, String.valueOf(e));
                getNavigator().showStringError(e.getMessage());
                RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                restApiErrorHandler.writeErrorLogs(timeStamp, e.getMessage());
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void updateOTPForward(long awbNo, String ofd_otp) {
        try {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(getDataManager().updateOTPForward(awbNo, ofd_otp).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {
            }));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void updateTotalAttemptsForward(long awb, int attempts) {
        try {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(getDataManager().updateTotalAttemptsForward(awb, attempts).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {
            }));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @SuppressLint("CheckResult")
    private void saveForwardList(List<DRSForwardTypeResponse> drsForwardTypeResponses) {
        Constants.number_of_fwd_shipments = drsForwardTypeResponses.size();
        Observable.fromCallable(() -> {
            for (DRSForwardTypeResponse response : drsForwardTypeResponses) {
                try {
                    // Create a composite key before save to db.
                    if (response.mpsAWBNo != null) {
                        response.mpsAWBs = response.mpsAWBNo.toString();
                    }
                    response.setCompositeKey((response.getDrsId() + "" + response.getAwbNo()).trim());
                } catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                    getNavigator().showStringError(e.getMessage());
                    RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                    restApiErrorHandler.writeErrorLogs(0, e.getMessage());
                }
            }
            return drsForwardTypeResponses;
        }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(this::saveFWDAfterGeoCoding);
    }

    @SuppressLint("CheckResult")
    private void saveNewRTSList(DRSReturnToShipperTypeNewResponse drsReturnToShipperTypeNewResponses) {
        // Before saving first loop and check that all items have latitude and longitude. If don't have then apply reverse geocoding and fetch address:-
        Observable.fromCallable(() -> drsReturnToShipperTypeNewResponses).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(drsForwardTypeResponseList -> saveNewRTSListAfterGeoCoding(drsReturnToShipperTypeNewResponses));
    }

    @SuppressLint("CheckResult")
    private void saveRVPList(List<DRSReverseQCTypeResponse> drsReverseQCTypeResponses) {
        try {
            Observable.fromCallable(() -> {
                for (DRSReverseQCTypeResponse response : drsReverseQCTypeResponses) {
                    try {
                        response.setCompositeKey(response.getDrs() + "" + response.getAwbNo());
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                        RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                        restApiErrorHandler.writeErrorLogs(0, e.getMessage());
                    }
                }
                return drsReverseQCTypeResponses;
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(drsReverseQCTypeResponsesList -> saveRVPAfterGeoCoding(drsReverseQCTypeResponsesList));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
            getNavigator().showStringError(e.getMessage());
        }
    }

    public void updateOTPEDS(long awbNo, String OTP) {
        try {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(getDataManager().updateOTPEDS(awbNo, OTP).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {
            }));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @SuppressLint("CheckResult")
    private void saveEDSNewList(List<EDSResponse> edsResponses) {
        // Before saving first loop and check that all items have latitude and longitude. If don't have the apply reverse geocoding and fetch address:-
        Observable.fromCallable(() -> {
            for (EDSResponse response : edsResponses) {
                try {
                    response.setCompositeKey(response.getDrsNo() + "" + response.getAwbNo());
                } catch (Exception e) {
                    RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                    restApiErrorHandler.writeErrorLogs(0, e.getMessage());
                    Logger.e(TAG, String.valueOf(e));
                }
            }
            return edsResponses;
        }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(this::saveEDSAfterGeoCoding);
    }

    public void getAllNewDRS(DRSResponse drsResponse, ProgressDialog dialog) {
        try {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(Observable.zip(getDataManager().getDRSListForward(), getDataManager().getDRSListNewRTS(), getDataManager().getDRSListRVP(), getDataManager().getDrsListNewEds(), getDataManager().getEdsRescheduleFlag(), getDataManager().getAllRemarks(getDataManager().getCode(), TimeUtils.getDateYearMonthMillies()), (drsForwardTypeResponses, drsReturnToShipperTypeNewResponses, drsReverseQCTypeResponses, edsResponses, rescheduleEdsDS, remarks) -> {
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
                getAllConsigneeProfile(drsResponse, dialog);
                return commonDRSListItems;
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(commonDRSListItems -> calculateUnAttemptedAssignedShipments(), Throwable::printStackTrace));
        } catch (Exception e) {
            getNavigator().showStringError(e.getMessage());
            dialog.dismiss();
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void saveFWDAfterGeoCoding(List<DRSForwardTypeResponse> drsForwardTypeResponses) {
        try {
            getCompositeDisposable().add(getDataManager().saveDRSForwardList(drsForwardTypeResponses).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {
            }, throwable -> {
            }));
        } catch (Exception e) {
            getNavigator().showStringError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void saveNewRTSListAfterGeoCoding(DRSReturnToShipperTypeNewResponse drsReturnToShipperTypeNewResponses) {
        try {
            getCompositeDisposable().add(getDataManager().saveDRSNewRTSList(drsReturnToShipperTypeNewResponses).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {
            }, throwable -> {
            }));
        } catch (Exception e) {
            getNavigator().showStringError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void saveRVPAfterGeoCoding(List<DRSReverseQCTypeResponse> drsReverseQCTypeResponses) {
        HashSet<DRSReverseQCTypeResponse> drsReverseQCTypeResponses_set = new HashSet<>();
        for (int i = 0; i < drsReverseQCTypeResponses.size(); i++) {
            if (!String.valueOf(drsReverseQCTypeResponses.get(i).getCompositeKey()).startsWith("null") && !drsReverseQCTypeResponses.get(i).getCompositeKey().equalsIgnoreCase("")) {
                drsReverseQCTypeResponses_set.add(drsReverseQCTypeResponses.get(i));
            }
            RVPQCImageTable rvpqcImageTable = new RVPQCImageTable();
            rvpqcImageTable.awb_number = drsReverseQCTypeResponses.get(i).getAwbNo();
            getCompositeDisposable().add(getDataManager().insert(rvpqcImageTable).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
            }, throwable -> {
                Logger.e(TAG, String.valueOf(throwable));
                RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(throwable.getCause());
                restApiErrorHandler.writeErrorLogs(0, throwable.getMessage());
            }));
        }
        for (DRSReverseQCTypeResponse drsReverseQCTypeResponse : drsReverseQCTypeResponses_set) {
            try {
                getCompositeDisposable().add(getDataManager().isRVPDRSExist(drsReverseQCTypeResponse.getCompositeKey()).flatMap(Observable::just).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {
                    if (!aBoolean) {
                        addRVPWithQC(drsReverseQCTypeResponse);
                    }
                }, throwable -> {
                    RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(throwable.getCause());
                    restApiErrorHandler.writeErrorLogs(0, throwable.getMessage());
                }));
            } catch (Exception e) {
                getNavigator().showStringError(e.getMessage());
                Logger.e(TAG, String.valueOf(e));
            }
        }
    }

    private void saveEDSAfterGeoCoding(List<EDSResponse> edsResponses) {
        for (EDSResponse edsResponse : edsResponses) {
            try {
                getCompositeDisposable().add(getDataManager().isEDSDRSNewExist(edsResponse.getCompositeKey()).flatMap(Observable::just).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(aBoolean -> addEDSNewActivityWizard(edsResponse), throwable -> {
                }));
            } catch (Exception e) {
                getNavigator().showStringError(e.getMessage());
                Logger.e(TAG, String.valueOf(e));
            }
        }
    }

    private void getAllConsigneeProfile(DRSResponse drsResponse, ProgressDialog dialog) {
        try {
            ArrayList<Consignee_profile> awbList = new ArrayList<>();
            profileFoundList = new ArrayList<>();
            ArrayList<Reschedule_info_awb_list> edsRescheduleList = new ArrayList<>();
            if (drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList() != null && !drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().isEmpty()) {
                for (int i = 0; i < drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().size(); i++) {
                    // Creating profile found object by own now:-
                    ProfileFound profileFound = new ProfileFound();
                    profileFound.setAwb_number(drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().get(i).getAwbNo());
                    profileFound.setDelivery_latitude(drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().get(i).getConsigneeDetails().getAddress().getLocation().getLat());
                    profileFound.setDelivery_longitude(drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().get(i).getConsigneeDetails().getAddress().getLocation().getLng());
                    if (drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().get(i).getAddress_profiled() != null && drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().get(i).getAddress_profiled().equalsIgnoreCase("Y")) {
                        profileFoundList.add(profileFound);
                    }
                    consigneeProfile = new Consignee_profile();
                    consigneeProfile.setAwb(drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().get(i).getAwbNo() + "");
                    consigneeProfile.setShipper_id(drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().get(i).getShipmentDetails().getShipper_id() + "");
                    awbList.add(consigneeProfile);
                }
            }
            // Saving data into Room DB for RVP:-
            if (drsResponse.getTodoResponse().getDrs_list_response().getRevDrsList() != null && !drsResponse.getTodoResponse().getDrs_list_response().getRevDrsList().isEmpty()) {
                for (int i = 0; i < drsResponse.getTodoResponse().getDrs_list_response().getRevDrsList().size(); i++) {
                    // Creating profile found object by own now:-
                    ProfileFound profileFound = new ProfileFound();
                    profileFound.setAwb_number(drsResponse.getTodoResponse().getDrs_list_response().getRevDrsList().get(i).getAwbNo());
                    profileFound.setDelivery_latitude(drsResponse.getTodoResponse().getDrs_list_response().getRevDrsList().get(i).getConsigneeDetails().getAddress().getLocation().getLat());
                    profileFound.setDelivery_longitude(drsResponse.getTodoResponse().getDrs_list_response().getRevDrsList().get(i).getConsigneeDetails().getAddress().getLocation().getLng());
                    if (drsResponse.getTodoResponse().getDrs_list_response().getRevDrsList().get(i).getAddress_profiled() != null && drsResponse.getTodoResponse().getDrs_list_response().getRevDrsList().get(i).getAddress_profiled().equalsIgnoreCase("Y")) {
                        profileFoundList.add(profileFound);
                    }
                    consigneeProfile = new Consignee_profile();
                    consigneeProfile.setAwb(drsResponse.getTodoResponse().getDrs_list_response().getRevDrsList().get(i).getAwbNo() + "");
                    consigneeProfile.setShipper_id(drsResponse.getTodoResponse().getDrs_list_response().getRevDrsList().get(i).getShipmentDetails().getShipper_id() + "");
                    awbList.add(consigneeProfile);
                }
            }
            // Saving data into Room DB for EDS:-
            if (drsResponse.getTodoResponse().getDrs_list_response().getEdsList() != null && !drsResponse.getTodoResponse().getDrs_list_response().getEdsList().isEmpty()) {
                for (int i = 0; i < drsResponse.getTodoResponse().getDrs_list_response().getEdsList().size(); i++) {
                    // Creating profile found object by own now
                    ProfileFound profileFound = new ProfileFound();
                    profileFound.setAwb_number(drsResponse.getTodoResponse().getDrs_list_response().getEdsList().get(i).getAwbNo());
                    profileFound.setDelivery_latitude(drsResponse.getTodoResponse().getDrs_list_response().getEdsList().get(i).getConsigneeDetail().getAddress().getLocation().getLat());
                    profileFound.setDelivery_longitude(drsResponse.getTodoResponse().getDrs_list_response().getEdsList().get(i).getConsigneeDetail().getAddress().getLocation().getLng());
                    if (drsResponse.getTodoResponse().getDrs_list_response().getEdsList().get(i).getAddress_profiled() != null && drsResponse.getTodoResponse().getDrs_list_response().getEdsList().get(i).getAddress_profiled().equalsIgnoreCase("Y")) {
                        profileFoundList.add(profileFound);
                    }
                    consigneeProfile = new Consignee_profile();
                    consigneeProfile.setAwb(drsResponse.getTodoResponse().getDrs_list_response().getEdsList().get(i).getAwbNo() + "");
                    consigneeProfile.setShipper_id(drsResponse.getTodoResponse().getDrs_list_response().getEdsList().get(i).getShipmentDetail().getShipper_id() + "");
                    awbList.add(consigneeProfile);
                }
            }
            if (drsResponse.getTodoResponse().getDrs_list_response().getEdsList() != null && !drsResponse.getTodoResponse().getDrs_list_response().getEdsList().isEmpty()) {
                array = new JSONArray();
                for (int i = 0; i < drsResponse.getTodoResponse().getDrs_list_response().getEdsList().size(); i++) {
                    rescheduleData = new Reschedule_info_awb_list();
                    rescheduleData.setAwb_number(String.valueOf(drsResponse.getTodoResponse().getDrs_list_response().getEdsList().get(i).getAwbNo()));
                    rescheduleData.setShipper_id(String.valueOf(drsResponse.getTodoResponse().getDrs_list_response().getEdsList().get(i).getShipmentDetail().getShipper_id()));
                    edsRescheduleList.add(rescheduleData);
                    edsRescheduleRequest = new EdsRescheduleRequest();
                    edsRescheduleRequest.setReschedule_info_awb_list(edsRescheduleList);
                    array.put(rescheduleData);
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("reschedule_info_awb_list", array);
                getRescheduleFlag();
            }
            saveProfileFoundList(profileFoundList, dialog);
        } catch (Exception e) {
            dialog.dismiss();
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void addRVPWithQC(DRSReverseQCTypeResponse drsReverseQCTypeResponse) {
        try {
            List<RvpQualityCheck> rvpQualityCheck = new ArrayList<>();
            List<RvpQualityCheck> rvpQualities = drsReverseQCTypeResponse.getShipmentDetails().getQualityChecks();
            if (rvpQualities != null) {
                rvpQualityCheck = rvpQualities;
            }
            try {
                int indexOf = 0;
                for (int i = 0; i < Objects.requireNonNull(rvpQualities).size(); i++) {
                    if (rvpQualities.get(i).getQcCode().equalsIgnoreCase("GEN_CHECK_ITEM_IMAGE")) {
                        indexOf = i;
                    }
                }
                Collections.swap(rvpQualities, 0, indexOf);
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
            for (RvpQualityCheck rvpQuality : rvpQualityCheck) {
                rvpQuality.setAwbNo(drsReverseQCTypeResponse.getAwbNo());
                rvpQuality.setDrs(drsReverseQCTypeResponse.getDrs());
            }
            getCompositeDisposable().add(Observable.merge(getDataManager().saveDRSRVPListQualityCheck(rvpQualityCheck), getDataManager().saveDRSRVP(drsReverseQCTypeResponse)).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {

            }, throwable -> {
                Logger.e(TAG, String.valueOf(throwable));
                RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(throwable.getCause());
                restApiErrorHandler.writeErrorLogs(0, throwable.getMessage());
            }));
        } catch (Exception e) {
            getNavigator().showStringError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void addEDSNewActivityWizard(EDSResponse edsResponse) {
        List<EDSActivityWizard> edsActivityWizards = edsResponse.getShipmentDetail().getEdsActivityWizards();
        for (EDSActivityWizard edsActivityWizard : edsActivityWizards) {
            edsActivityWizard.setAwbNo(edsResponse.getAwbNo());
            edsActivityWizard.setQuestion_form_dummy(new JSONObject(edsActivityWizard.getQuestionForm()).toString());
        }
        try {
            getCompositeDisposable().add(Observable.merge(getDataManager().saveEDSActivityNewWizardList(edsActivityWizards), getDataManager().saveNewDrsEDS(edsResponse)).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {

            }, throwable -> {
                RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(throwable.getCause());
                restApiErrorHandler.writeErrorLogs(0, throwable.getMessage());
            }));
        } catch (Exception e) {
            getNavigator().showStringError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void getRescheduleFlag() {
        try {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(getDataManager().doEdsRescheduleCall(getAuthToken(), getDataManager().getEcomRegion(), edsRescheduleRequest).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(edsRescheduleResponse -> {
                try {
                    edsRescheduleResponse.setAwb_reschedule_info(edsRescheduleResponse.getAwb_reschedule_info());
                    edsRescheduleResponse.getAwb_reschedule_info();
                    rescheduleEdsDS.clear();
                    for (int i = 0; i < edsRescheduleResponse.getAwb_reschedule_info().size(); i++) {
                        RescheduleEdsD rescheduleEdsD = new RescheduleEdsD();
                        rescheduleEdsD.setAwb_number(edsRescheduleResponse.getAwb_reschedule_info().get(i).getAwb_number());
                        rescheduleEdsD.setReschedule_status(edsRescheduleResponse.getAwb_reschedule_info().get(i).getReschedule_status());
                        rescheduleEdsDS.add(rescheduleEdsD);
                    }
                    insertEdsRescheduleData(rescheduleEdsDS);
                } catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                }
            }, Throwable::printStackTrace));
        } catch (Exception ex) {
            Logger.e(TAG, String.valueOf(ex));
        }
    }

    public String getAuthToken() {
        return getDataManager().getAuthToken();
    }

    private void saveProfileFoundList(List<ProfileFound> profileFounds, ProgressDialog dialog) {
        try {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(getDataManager().saveProfileFoundList(profileFounds).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> dialog.dismiss(), throwable -> dialog.dismiss()));
        } catch (Exception e) {
            dialog.dismiss();
            getNavigator().showStringError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void showLocationAlert(Activity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
        builder.setMessage("This app needs background location. \n\n To run this application in background you need to give location permission all the time. Please permit the permission through location Settings screen. \n\n" + "Select App Permissions-> Location Permission-> Select Allow all the time.");
        builder.setPositiveButton("Settings", (dialog, which) -> {
            dialog.dismiss();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getNavigator().getActivityActivity().getPackageName(), null);
            intent.setData(uri);
            getNavigator().getActivityActivity().startActivity(intent);
        });
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    public void showDayAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getNavigator().getActivityActivity(), R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
        String AlertText1 = "Attention : ";
        builder.setMessage(AlertText1 + " Your need to login today for updated day time").setCancelable(false).setPositiveButton("OK", (dialog, id) -> {
            dialog.cancel();
            logoutLocal();
        });
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    public void getAllCategoryAssignedCount() {
        // Fetch Assigned Shipments:-
        fetchForwardShipment(pendingShipmentAssignedStatus, forwardCount);
        fetchRtsShipments(pendingShipmentAssignedStatus, rtsCount);
        fetchRvpShipments(pendingShipmentAssignedStatus, rvpCount);
        // Fetch Success Shipments:-
        fetchForwardShipment(successShipmentStatus, successForwardCount);
        fetchRtsShipments(successShipmentStatus, successRTSCount);
        fetchRvpShipments(successShipmentStatus, successRvpCount);
        // Fetch Failed Shipments:-
        fetchForwardShipment(failedShipmentStatus, failedForwardCount);
        fetchRtsShipments(failedShipmentStatus, failedRtsCount);
        fetchRvpShipments(successShipmentStatus, successRvpCount);
    }

    private void fetchForwardShipment(int status, MutableLiveData<Integer> setShipmentCount) {
        getCompositeDisposable().add(
                getDataManager().getFWDStatusCount(status)
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(forward -> setShipmentCount.postValue(forward.intValue()), throwable -> Logger.e(TAG, String.valueOf(throwable))
                        )
        );
    }

    private void fetchRtsShipments(int status, MutableLiveData<Integer> setShipmentCount) {
        getCompositeDisposable().add(
                getDataManager().getRTSStatusCount(status)
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(rts -> setShipmentCount.postValue(rts.intValue()), throwable -> Logger.e(TAG, String.valueOf(throwable))
                        )
        );
    }

    private void fetchRvpShipments(int status, MutableLiveData<Integer> setShipmentCount) {
        getCompositeDisposable().add(
                getDataManager().getRVPStatusCount(status)
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(rvp -> setShipmentCount.postValue(rvp.intValue()), throwable -> Logger.e(TAG, String.valueOf(throwable))
                        )
        );
    }

    private void updateTotalAssignedCount() {
        int forward = forwardCount.getValue() != null ? forwardCount.getValue() : 0;
        int rts = rtsCount.getValue() != null ? rtsCount.getValue() : 0;
        int rvp = rvpCount.getValue() != null ? rvpCount.getValue() : 0;
        totalAssignedCount.postValue(forward + rts + rvp);
    }

    private void updateSuccessCount() {
        int successForward = successForwardCount.getValue() != null ? successForwardCount.getValue() : 0;
        int successRts = successRTSCount.getValue() != null ? successRTSCount.getValue() : 0;
        int successRvp = successRvpCount.getValue() != null ? successRvpCount.getValue() : 0;
        totalSuccessCount.postValue(successForward + successRts + successRvp);
    }

    private void updateTotalFailedCount() {
        int failedForward = failedForwardCount.getValue() != null ? failedForwardCount.getValue() : 0;
        int failedRts = failedRtsCount.getValue() != null ? failedRtsCount.getValue() : 0;
        int failedRvp = successRvpCount.getValue() != null ? successRvpCount.getValue() : 0;
        totalFailedCount.setValue(failedForward + failedRts + failedRvp);
    }

    //LogIn New MarkAttendance
    public void markCheckAttendance(String empCode, Activity context, String location_code) {
        setIsLoading(true);
        try {
            String imei_number = CommonUtils.getImei(context);
//            String empCode = getDataManager().getEmp_code();
            MarkAttendanceRequest request = new MarkAttendanceRequest(empCode, imei_number, location_code);
            final long timeStamp = System.currentTimeMillis();
            writeRestAPIRequst(timeStamp, request);
            SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.MARK_ATTENDANCE);
            getCompositeDisposable().add(getDataManager()
                    .doMarkAttendanceApiCall(getDataManager().getAuthToken(), request)
                    .doOnSuccess(loginVerifyOtpResponse -> writeRestAPIResponse(timeStamp, loginVerifyOtpResponse))
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(response -> {
                        DashboardViewModel.this.setIsLoading(false);
                        if (response.isStatus()) {
                            Log.d("markAttendace", "attendance");
                            // Need to code.
                        } else {
                            // Need to code
                        }
                    }, throwable -> {
                        setIsLoading(false);
                        String error;
                        try {
                            error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                            getNavigator().showErrorMessage(error.contains("HTTP 500 "));
                        } catch (NullPointerException e) {
                            Logger.e(TAG, String.valueOf(e));
                        }
                    }));
        } catch (Exception e) {
            //getNavigator().onHandleError(e.getMessage());
            setIsLoading(false);
            //getNavigator().onHandleError(new RestApiErrorHandler(e.fillInStackTrace()).getErrorDetails().getEResponse().getDescription());
        }
    }

    //LogIn New CheckAttendnace
    public void checkAttendance(Activity context, String empCode) {
        setIsLoading(true);
        try {
            final long timeStamp = System.currentTimeMillis();
            writeRestAPIRequst(timeStamp, empCode);
            SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.CHECK_ATTENDANCE);
            getCompositeDisposable().add(getDataManager()
                    .doCheckAttendanceApiCall(getDataManager().getAuthToken(), empCode)
                    .doOnSuccess(checkAttendanceResponse -> writeRestAPIResponse(timeStamp, checkAttendanceResponse))
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(response -> {
                        DashboardViewModel.this.setIsLoading(false);
                        if (!response.isStatus()) {
                            markCheckAttendance(getDataManager().getEmp_code(), context, getDataManager().getLocationCode());
                            getDataManager().setCheckAttendanceLoginStatus(false);
                        }
                    }, throwable -> {
                        setIsLoading(false);
                        String error;
                        try {
                            error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                            getNavigator().showErrorMessage(error.contains("HTTP 500"));
                        } catch (NullPointerException e) {
                            Logger.e(TAG, String.valueOf(e));
                        }
                    }));
        } catch (Exception e) {
            //getNavigator().onHandleError(e);
            setIsLoading(false);
            Logger.e(TAG, String.valueOf(e));
            //getNavigator().onHandleError(new RestApiErrorHandler(e.fillInStackTrace()).getErrorDetails().getEResponse().getDescription());
        }
    }

    private void showProgressDialog(Context context) {
        dialog = new ProgressDialog(context, android.R.style.Theme_Material_Light_Dialog);
        dialog.setIndeterminate(true);
        dialog.setMessage(getApplication().getString(R.string.syncing_master_data));
        dialog.setCancelable(false);
        dialog.show();
    }

    private void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}