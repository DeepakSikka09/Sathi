package in.ecomexpress.sathi.ui.dashboard.starttrip;

import static android.content.Context.ALARM_SERVICE;
import static in.ecomexpress.sathi.utils.Constants.DISTANCE_API_KEY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.geolocations.LocationService;
import in.ecomexpress.geolocations.LocationTracker;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.backgroundServices.SyncServicesV2;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.db.model.CommonDRSListItem;
import in.ecomexpress.sathi.repo.local.db.model.Remark;
import in.ecomexpress.sathi.repo.local.db.model.RescheduleEdsD;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.commonrequest.CommonUserIdRequest;
import in.ecomexpress.sathi.repo.remote.model.consignee_profile.Consignee_profile;
import in.ecomexpress.sathi.repo.remote.model.consignee_profile.ProfileFound;
import in.ecomexpress.sathi.repo.remote.model.dp_daily_earned.DPReferenceCodeRequest;
import in.ecomexpress.sathi.repo.remote.model.drs_list.DRSResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.common.Location;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.DRSReturnToShipperTypeNewResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.IRTSBaseInterface;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.RvpQualityCheck;
import in.ecomexpress.sathi.repo.remote.model.eds.EdsRescheduleRequest;
import in.ecomexpress.sathi.repo.remote.model.eds.Reschedule_info_awb_list;
import in.ecomexpress.sathi.repo.remote.model.masterdata.Forward;
import in.ecomexpress.sathi.repo.remote.model.masterdata.GlobalConfigurationMaster;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterDataConfig;
import in.ecomexpress.sathi.repo.remote.model.masterdata.Reverse;
import in.ecomexpress.sathi.repo.remote.model.masterdata.masterRequest;
import in.ecomexpress.sathi.repo.remote.model.trip.ImageResponse;
import in.ecomexpress.sathi.repo.remote.model.trip.StartTripRequest;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.ui.dashboard.global_activity.GlobalDialogActivity;
import in.ecomexpress.sathi.ui.dashboard.landing.DashboardActivity;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.CryptoUtils;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.SathiContentProvider;
import in.ecomexpress.sathi.utils.ScreenUtils;
import in.ecomexpress.sathi.utils.TimeUtils;
import in.ecomexpress.sathi.utils.receivers.NotificationUpdate;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function5;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@HiltViewModel
public class StartTripViewModel extends BaseViewModel<StartTripCallBack> {

    private static final String TAG = StartTripViewModel.class.getSimpleName();
    Consignee_profile consigneeProfile = new Consignee_profile();
    List<ProfileFound> profileFoundList;
    public boolean is_start_clicked = true;
    JSONArray array;
    Reschedule_info_awb_list rescheduleData;
    EdsRescheduleRequest edsRescheduleRequest;
    ArrayList<RescheduleEdsD> rescheduleEdsDS = new ArrayList<>();

    @SuppressLint("StaticFieldLeak")
    Context context;
    boolean isDcContains = false;
    boolean isDVContains = false;
    boolean isEKYCContains = false;
    boolean isACContains = false;
    int DC_COUNT = 0, EKYC_COUNT = 0, AC_COUNT = 0, DV_COUNT = 0;

    @Inject
    public StartTripViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);
    }

    public void onCancelClick() {
        if (!StartTripViewModel.this.getIsLoading().get()) {
            new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(() -> getNavigator().dismissDialog(), 2000);
        }
    }

    public void onVehicleType(AdapterView<?> parent, View view, int pos, long id) {
        getNavigator().VehicleType(parent.getSelectedItem().toString());
    }

    public void onTypeOfVehicle(AdapterView<?> parent, View view, int pos, long id) {
        getNavigator().TypeOfVehicle(parent.getSelectedItem().toString());
    }

    public void getClickStart() {
        Log.d("is viewmodel connect", "viewmodel connect");
    }

    public void onVehicleNumber() {
        getNavigator().sendVehicleNumber(getDataManager().getRouteName());
    }

    public void onDRSListApiCall() {
        final long timeStamp = System.currentTimeMillis();
        try {
            writeEvent(timeStamp, "Syncing DRS list after start trip: ");
            StartTripViewModel.this.setIsLoading(true);
            CommonUserIdRequest request = new CommonUserIdRequest(String.valueOf(getDataManager().getCode()));
            writeRestAPIRequst(timeStamp, request);
            getCompositeDisposable().add(getDataManager().doDRSListApiNewCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request).doOnSuccess(lastMileDRSListNewResponse -> {
                writeEvent(timeStamp, "DRS List successfully fetched. size ");
                StartTripViewModel.this.setIsLoading(false);
                writeRestAPIResponse(timeStamp, lastMileDRSListNewResponse);
                try {
                    if (lastMileDRSListNewResponse.getStatus()) {
                        //Saving Data to Room DataBase for Forward
                        if (lastMileDRSListNewResponse.getTodoResponse().getDrs_list_response() != null) {
                            if (lastMileDRSListNewResponse.getTodoResponse().getDrs_list_response().getForwardDrsList() != null) {
                                saveForwardList(lastMileDRSListNewResponse.getTodoResponse().getDrs_list_response().getForwardDrsList());
                                if (!lastMileDRSListNewResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().isEmpty()) {
                                    getDataManager().setDRSId((long) lastMileDRSListNewResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().get(0).getDrsId());
                                }
                            }
                            if (lastMileDRSListNewResponse.getTodoResponse().getDrs_list_response().getDrsReturnToShipperTypeNewResponse() != null) {
                                saveNewRTSList(lastMileDRSListNewResponse.getTodoResponse().getDrs_list_response().getDrsReturnToShipperTypeNewResponse());
                            }
                            //Saving Data to Room DataBase for RVP
                            if (lastMileDRSListNewResponse.getTodoResponse().getDrs_list_response().getRevDrsList() != null) {
                                saveRVPList(lastMileDRSListNewResponse.getTodoResponse().getDrs_list_response().getRevDrsList());
                                if (!lastMileDRSListNewResponse.getTodoResponse().getDrs_list_response().getRevDrsList().isEmpty()) {
                                    getDataManager().setDRSId((long) lastMileDRSListNewResponse.getTodoResponse().getDrs_list_response().getRevDrsList().get(0).getDrs());
                                }
                            }
                            if (lastMileDRSListNewResponse.getTodoResponse().getDrs_list_response().getEdsList() != null) {
                                saveEDSNewList(lastMileDRSListNewResponse.getTodoResponse().getDrs_list_response().getEdsList());
                                if (!lastMileDRSListNewResponse.getTodoResponse().getDrs_list_response().getEdsList().isEmpty()) {
                                    getDataManager().setDRSId(lastMileDRSListNewResponse.getTodoResponse().getDrs_list_response().getEdsList().get(0).getDrsNo());
                                }
                            }
                            // EDS number of kyc
                            try {
                                if (lastMileDRSListNewResponse.getStatus()) {
                                    for (int i = 0; i < lastMileDRSListNewResponse.getTodoResponse().getDrs_list_response().getEdsList().size(); i++) {
                                        for (int j = 0; j < lastMileDRSListNewResponse.getTodoResponse().getDrs_list_response().getEdsList().get(i).getShipmentDetail().getEdsActivityWizards().size(); j++) {
                                            if (lastMileDRSListNewResponse.getTodoResponse().getDrs_list_response().getEdsList().get(i).getShipmentDetail().getEdsActivityWizards().get(j).code.startsWith("DC")) {
                                                isDcContains = true;
                                            }
                                            if (lastMileDRSListNewResponse.getTodoResponse().getDrs_list_response().getEdsList().get(i).getShipmentDetail().getEdsActivityWizards().get(j).code.startsWith("DV")) {
                                                isDVContains = true;
                                            }
                                            if (lastMileDRSListNewResponse.getTodoResponse().getDrs_list_response().getEdsList().get(i).getShipmentDetail().getEdsActivityWizards().get(j).code.endsWith("EKYC")) {
                                                isEKYCContains = true;
                                            }
                                            if (lastMileDRSListNewResponse.getTodoResponse().getDrs_list_response().getEdsList().get(i).getShipmentDetail().getEdsActivityWizards().get(j).code.startsWith("AC")) {
                                                isACContains = true;
                                            }
                                        }
                                        if (isDcContains) {
                                            DC_COUNT = DC_COUNT + 1;
                                        }
                                        if (isEKYCContains) {
                                            EKYC_COUNT = EKYC_COUNT + 1;
                                        }
                                        if (isACContains) {
                                            AC_COUNT = AC_COUNT + 1;
                                        }
                                        if (isDVContains) {
                                            DV_COUNT = DV_COUNT + 1;
                                        }
                                        isDcContains = false;
                                        isEKYCContains = false;
                                        isACContains = false;
                                        isDVContains = false;
                                    }
                                }
                                for (EDSResponse eds : lastMileDRSListNewResponse.getTodoResponse().getDrs_list_response().getEdsList()) {
                                    if (!getDataManager().getTripId().equalsIgnoreCase("0") && eds.getShipmentSyncStatus() == 0) {
                                        if (eds.getShipmentDetail().getSlot_details().getStart_time() < getDataManager().getStartTripTime() && getDataManager().getStartTripTime() < eds.getShipmentDetail().getSlot_details().getEnd_time()) {
                                            if (DC_COUNT > 0 && AC_COUNT > 0 && DV_COUNT > 0) {
                                                generateNotification(getDataManager().getStartTripTime() + 2 * 60 * 1000, "Don’t forget  - " + DC_COUNT + " Document Collection ," + AC_COUNT + " Activity, " + DV_COUNT + " Document verification ," + " EDS cases to attempt for slot " + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getStart_time()) + "-" + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getEnd_time()));
                                            } else if (DC_COUNT > 0 && AC_COUNT > 0) {
                                                generateNotification(getDataManager().getStartTripTime() + 2 * 60 * 1000, "Don’t forget  - " + DC_COUNT + " Document Collection ," + AC_COUNT + " Activity EDS cases to attempt for slot " + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getStart_time()) + "-" + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getEnd_time()));
                                            } else if (DC_COUNT > 0 && DV_COUNT > 0) {
                                                generateNotification(getDataManager().getStartTripTime() + 2 * 60 * 1000, "Don’t forget  - " + DC_COUNT + " Document Collection , " + DV_COUNT + " Document verification ," + " EDS cases to attempt for slot " + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getStart_time()) + "-" + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getEnd_time()));
                                            } else if (DV_COUNT > 0 && AC_COUNT > 0) {
                                                generateNotification(getDataManager().getStartTripTime() + 2 * 60 * 1000, "Don’t forget  - " + DV_COUNT + " Document verification ," + AC_COUNT + "Activity,  EDS cases to attempt for slot " + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getStart_time()) + "-" + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getEnd_time()));
                                            } else if (DC_COUNT > 0) {
                                                generateNotification(getDataManager().getStartTripTime() + 2 * 60 * 1000, "Don’t forget  - " + DC_COUNT + " Document Collection ," + " EDS cases to attempt for slot " + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getStart_time()) + "-" + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getEnd_time()));
                                            } else if (DV_COUNT > 0) {
                                                generateNotification(getDataManager().getStartTripTime() + 2 * 60 * 1000, "Don’t forget  - " + DV_COUNT + "Docuemnt verification ," + " EDS cases to attempt for slot " + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getStart_time()) + "-" + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getEnd_time()));
                                            } else if (AC_COUNT > 0) {
                                                generateNotification(getDataManager().getStartTripTime() + 2 * 60 * 1000, "Don’t forget  - " + AC_COUNT + "Activity," + " EDS cases to attempt for slot " + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getStart_time()) + "-" + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getEnd_time()));
                                            }
                                        } else if (eds.getShipmentDetail().getSlot_details().getStart_time() > getDataManager().getStartTripTime()) {
                                            if (DC_COUNT > 0 && AC_COUNT > 0 && DV_COUNT > 0) {
                                                generateNotification(eds.getShipmentDetail().getSlot_details().getStart_time(), "Don’t forget  - " + DC_COUNT + " Document Collection ," + AC_COUNT + " Activity, " + DV_COUNT + " Document verification ," + " EDS cases to attempt for slot " + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getStart_time()) + "-" + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getEnd_time()));
                                            } else if (DC_COUNT > 0 && AC_COUNT > 0) {
                                                generateNotification(eds.getShipmentDetail().getSlot_details().getStart_time(), "Don’t forget  - " + DC_COUNT + " Document Collection ," + AC_COUNT + " Activity EDS cases to attempt for slot " + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getStart_time()) + "-" + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getEnd_time()));
                                            } else if (DC_COUNT > 0 && DV_COUNT > 0) {
                                                generateNotification(eds.getShipmentDetail().getSlot_details().getStart_time(), "Don’t forget  - " + DC_COUNT + " Document Collection , " + DV_COUNT + " Document verification ," + " EDS cases to attempt for slot " + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getStart_time()) + "-" + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getEnd_time()));
                                            } else if (DV_COUNT > 0 && AC_COUNT > 0) {
                                                generateNotification(eds.getShipmentDetail().getSlot_details().getStart_time(), "Don’t forget  - " + DV_COUNT + " Document verification ," + AC_COUNT + "Activity,  EDS cases to attempt for slot " + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getStart_time()) + "-" + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getEnd_time()));
                                            } else if (DC_COUNT > 0) {
                                                generateNotification(eds.getShipmentDetail().getSlot_details().getStart_time(), "Don’t forget  - " + DC_COUNT + " Document Collection ," + " EDS cases to attempt for slot " + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getStart_time()) + "-" + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getEnd_time()));
                                            } else if (DV_COUNT > 0) {
                                                generateNotification(eds.getShipmentDetail().getSlot_details().getStart_time(), "Don’t forget  - " + DV_COUNT + "Document verification ," + " EDS cases to attempt for slot " + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getStart_time()) + "-" + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getEnd_time()));
                                            } else if (AC_COUNT > 0) {
                                                generateNotification(eds.getShipmentDetail().getSlot_details().getStart_time(), "Don’t forget  - " + AC_COUNT + "Activity," + " EDS cases to attempt for slot " + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getStart_time()) + "-" + changeMillisToTime(eds.getShipmentDetail().getSlot_details().getEnd_time()));
                                            }
                                        }
                                    }
                                }
                            } catch (Exception ex) {
                                RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(ex.getCause());
                                restApiErrorHandler.writeErrorLogs(timeStamp, ex.getMessage());
                                Logger.e(TAG, String.valueOf(ex));
                            }
                            getMasterData();
                            getAllNewDRS(lastMileDRSListNewResponse);
                            getNavigator().closeDialogopenDrs();
                        } else {
                            getNavigator().showDescription("no list");
                        }
                    } else {
                        getNavigator().showDescription(lastMileDRSListNewResponse.getTodoResponse().getDescription());
                    }
                } catch (Exception ex) {
                    RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(ex.getCause());
                    restApiErrorHandler.writeErrorLogs(timeStamp, ex.getMessage());
                    StartTripViewModel.this.setIsLoading(false);
                    getNavigator().showDescription(ex.getMessage());
                }
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                try {
                    StartTripViewModel.this.setIsLoading(false);
                    Logger.i(TAG, "LastMileDRSListNewResponse: " + response.toString());
                    if ((response.getTodoResponse() != null) && (response.getTodoResponse().getCode() == 107)) {
                        getNavigator().doLogout(response.getTodoResponse().getDescription());
                    }
                } catch (Exception e) {
                    getNavigator().showDescription(e.getMessage());
                }
            }, throwable -> {
                StartTripViewModel.this.setIsLoading(false);
                String error, myError;
                try {
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                    if (error.contains("HTTP 500 ")) {
                        myError = "Internal Server Error! Please Contact System Admin.";
                        getNavigator().doLogout(myError);
                    } else {
                        RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(throwable);
                        restApiErrorHandler.writeErrorLogs(timeStamp, throwable.getMessage());
                    }
                } catch (Exception e) {
                    RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                    restApiErrorHandler.writeErrorLogs(timeStamp, e.getMessage());
                    getNavigator().showDescription(e.getMessage());
                    Logger.e(TAG, String.valueOf(e));
                }
            }));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
            restApiErrorHandler.writeErrorLogs(timeStamp, e.getMessage());
            getNavigator().showDescription(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void getAllNewDRS(DRSResponse drsResponse) {
        try {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(Observable.zip(getDataManager().getDRSListForward(), getDataManager().getDRSListNewRTS(), getDataManager().getDRSListRVP(), getDataManager().getDrsListNewEds(), getDataManager().getAllRemarks(getDataManager().getCode(), TimeUtils.getDateYearMonthMillies()), new Function5<List<DRSForwardTypeResponse>, DRSReturnToShipperTypeNewResponse, List<DRSReverseQCTypeResponse>, List<EDSResponse>, List<Remark>, List<CommonDRSListItem>>() {
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
                            if (commonDRSListItems != null) {
                                getAllConsigneeProfile(drsResponse);
                            }
                            return commonDRSListItems;
                        }
                    }).subscribeOn(getSchedulerProvider().io()).
                    observeOn(getSchedulerProvider().ui()).
                    subscribe(commonDRSListItems -> {
                    }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void getAllConsigneeProfile(DRSResponse drsResponse) {
        try {
            profileFoundList = new ArrayList<>();
            ArrayList<Reschedule_info_awb_list> edsRescheduleList = new ArrayList<>();
            if (drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList() != null && !drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().isEmpty()) {
                for (int i = 0; i < drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().size(); i++) {
                    // Creating profile Found object by own now
                    ProfileFound profileFound = new ProfileFound();
                    profileFound.setAwb_number(drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().get(i).getAwbNo());
                    profileFound.setDelivery_latitude(drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().get(i).getConsigneeDetails().getAddress().getLocation().getLat());
                    profileFound.setDelivery_longitude(drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().get(i).getConsigneeDetails().getAddress().getLocation().getLat());
                    if (drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().get(i).getAddress_profiled() != null && drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().get(i).getAddress_profiled().equalsIgnoreCase("Y")) {
                        profileFoundList.add(profileFound);
                    }
                    consigneeProfile = new Consignee_profile();
                    consigneeProfile.setAwb(drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().get(i).getAwbNo() + "");
                    consigneeProfile.setShipper_id(drsResponse.getTodoResponse().getDrs_list_response().getForwardDrsList().get(i).getShipmentDetails().getShipper_id() + "");
                }
            }
            // Saving Data to Room DataBase for RVP
            if (drsResponse.getTodoResponse().getDrs_list_response().getRevDrsList() != null && !drsResponse.getTodoResponse().getDrs_list_response().getRevDrsList().isEmpty()) {
                for (int i = 0; i < drsResponse.getTodoResponse().getDrs_list_response().getRevDrsList().size(); i++) {
                    // Creating profile Found object by own now
                    ProfileFound profileFound = new ProfileFound();
                    profileFound.setAwb_number(drsResponse.getTodoResponse().getDrs_list_response().getRevDrsList().get(i).getAwbNo());
                    profileFound.setDelivery_latitude(drsResponse.getTodoResponse().getDrs_list_response().getRevDrsList().get(i).getConsigneeDetails().getAddress().getLocation().getLat());
                    profileFound.setDelivery_longitude(drsResponse.getTodoResponse().getDrs_list_response().getRevDrsList().get(i).getConsigneeDetails().getAddress().getLocation().getLat());
                    if (drsResponse.getTodoResponse().getDrs_list_response().getRevDrsList().get(i).getAddress_profiled() != null && drsResponse.getTodoResponse().getDrs_list_response().getRevDrsList().get(i).getAddress_profiled().equalsIgnoreCase("Y")) {
                        profileFoundList.add(profileFound);
                    }
                    consigneeProfile = new Consignee_profile();
                    consigneeProfile.setAwb(drsResponse.getTodoResponse().getDrs_list_response().getRevDrsList().get(i).getAwbNo() + "");
                    consigneeProfile.setShipper_id(drsResponse.getTodoResponse().getDrs_list_response().getRevDrsList().get(i).getShipmentDetails().getShipper_id() + "");
                }
            }
            // Saving data to room for EDS
            if (drsResponse.getTodoResponse().getDrs_list_response().getEdsList() != null && !drsResponse.getTodoResponse().getDrs_list_response().getEdsList().isEmpty()) {
                for (int i = 0; i < drsResponse.getTodoResponse().getDrs_list_response().getEdsList().size(); i++) {
                    // Creating profile Found object by own now
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
            saveProfileFoundList(profileFoundList);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void saveProfileFoundList(List<ProfileFound> profileFounds) {
        try {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(getDataManager().
                    saveProfileFoundList(profileFounds).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).
                    subscribe(aBoolean -> Logger.e(TAG, String.valueOf(aBoolean)), throwable -> {
                    }));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void uploadData(Activity context, String imageUrl, String imageKey, int imageId, String vehicleNumber, String vehicleType, String typeOfVehicle, String routeName, long actualMeterReading, List<ImageResponse> imageResponseList, String device_name) {
        this.context = context;
        imageKey = getFileName(imageKey);
        ImageResponse imageResponse = new ImageResponse(imageKey, imageId);
        StartTripViewModel.this.setIsLoading(false);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        String imei = CommonUtils.getImei(context);
        final long timeStamp = System.currentTimeMillis();
        StartTripRequest request;
        if (getDataManager().getIsAdmEmp()) {
            request = new StartTripRequest(vehicleNumber, typeOfVehicle, vehicleType, StartTripViewModel.this.getDataManager().getCode(), routeName, Calendar.getInstance().getTimeInMillis(), 0, getDataManager().getCurrentLatitude(), getDataManager().getCurrentLongitude(), imei, getDataManager().getLocationCode(), String.valueOf(getDataManager().getIsAdmEmp()), imageResponseList, device_name);
        } else {
            request = new StartTripRequest(vehicleNumber, typeOfVehicle, vehicleType, StartTripViewModel.this.getDataManager().getCode(), routeName, Calendar.getInstance().getTimeInMillis(), actualMeterReading, getDataManager().getCurrentLatitude(), getDataManager().getCurrentLongitude(), imei, getDataManager().getLocationCode(), String.valueOf(getDataManager().getIsAdmEmp()), imageResponseList, device_name);
        }
        writeRestAPIRequst(timeStamp, request);
        writeEvent(timeStamp, "After image upload: Uploading data to server.");
        try {
            StartTripViewModel.this.getCompositeDisposable().add(StartTripViewModel.this.getDataManager().doStartTripApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request)
                    .subscribeOn(StartTripViewModel.this.getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                        try {
                            is_start_clicked = true;
                            getNavigator().enableSubmitButton();
                            StartTripViewModel.this.setIsLoading(true);
                            if (response.getStatus()) {
                                writeRestAPIResponse(timeStamp, response);
                                ContentValues values = new ContentValues();
                                Constants.IS_USING_FAKE_GPS = 0;
                                values.put(SathiContentProvider.name, "Sathi Trip Started");
                                getNavigator().getActivityContext().getContentResolver().insert(SathiContentProvider.CONTENT_URI, values);
                                setAppSettingAlarm(getNavigator().getActivityContext());
                                getDataManager().setLiveTrackingTripId(response.getResponse().getLive_tracking_trip_id());
                                getDataManager().setLiveTrackingTripIdForApi(response.getResponse().getLive_tracking_trip_id());
                                getDataManager().setStartTripTime(System.currentTimeMillis());
                                writeEvent(timeStamp, "Successfully trip created.Trip ID: " + response.getResponse().getTripId());
                                deleteFile(imageUrl);
                                Constants.START_TRIP_LAT = getDataManager().getCurrentLatitude();
                                Constants.START_TRIP_LNG = getDataManager().getCurrentLongitude();
                                getDataManager().startTripInfo(actualMeterReading, Integer.toString(response.getResponse().getTripId()), vehicleType, typeOfVehicle, routeName);
                                getNavigator().startTripSyncDrs();
                                LocationTracker.setBothDistanceToZero(getNavigator().getActivityContext());
                                LocationTracker.deleteLatLng();
                                if (!getDataManager().getIsAdmEmp()) {
                                    startLiveTracking();
                                }
                                getDataManager().setADMUpdated(false);
                                getDataManager().setIsQRCodeScanned(false);
                                Constants.IS_RUN_DIRECTION_API = true;
                                if (!isMyServiceRunning(SyncServicesV2.class)) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        ContextCompat.startForegroundService(getNavigator().getActivityContext(), SyncServicesV2.getStartIntent(getNavigator().getActivityContext()));
                                    } else {
                                        SyncServicesV2.start(getNavigator().getActivityContext());
                                    }
                                }
                                LocationTracker.isStopWriting = false;
                                Constants.START_LIVE_TRACKING = true;
                                LocationTracker.deletetable();
                                LocationTracker.setBothDistanceToZero(getNavigator().getActivityContext());
                            } else {
                                StartTripViewModel.this.setIsLoading(false);
                                writeRestAPIResponse(timeStamp, response);
                                if (response.getResponse().getDescription().equalsIgnoreCase("Invalid Authentication Token.")) {
                                    getNavigator().doLogout(response.getResponse().getDescription());
                                } else {
                                    StartTripViewModel.this.setIsLoading(false);
                                    StartTripViewModel.this.getNavigator().onHandleError(response.getResponse().getDescription());
                                }
                            }
                        } catch (Exception e) {
                            is_start_clicked = true;
                            getNavigator().enableSubmitButton();
                            StartTripViewModel.this.setIsLoading(false);
                            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                            restApiErrorHandler.writeErrorLogs(timeStamp, e.getMessage());
                            getNavigator().showDescription(e.getMessage());
                            Logger.e(TAG, String.valueOf(e));
                        }
                    }, throwable -> {
                        is_start_clicked = true;
                        getNavigator().enableSubmitButton();
                        RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(throwable);
                        restApiErrorHandler.writeErrorLogs(timeStamp, throwable.getMessage());
                        StartTripViewModel.this.setIsLoading(false);
                        String error;
                        try {
                            error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                            getNavigator().showErrorMessage(error.contains("HTTP 500 "));
                        } catch (Exception e) {
                            restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                            restApiErrorHandler.writeErrorLogs(timeStamp, e.getMessage());
                            getNavigator().showDescription(e.getMessage());
                            Logger.e(TAG, String.valueOf(e));
                        }
                    }));
        } catch (Exception e) {
            is_start_clicked = true;
            getNavigator().enableSubmitButton();
            getNavigator().showError(e.getMessage());
            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
            restApiErrorHandler.writeErrorLogs(timeStamp, e.getMessage());
            StartTripViewModel.this.setIsLoading(false);
            getNavigator().showDescription(e.getMessage());
            getNavigator().onHandleError(new RestApiErrorHandler(e.fillInStackTrace()).getErrorDetails().getEResponse().getDescription());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getNavigator().getActivityContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void startLiveTracking() {
        getDataManager().setDirectionDistance(0);
        getDataManager().setDirectionTotalDistance(0);
        try {
            try {
                if (DashboardActivity.lt != null && getDataManager().getLiveTrackingTripId() != null && !getDataManager().getLiveTrackingTripId().equalsIgnoreCase("")) {
                    DashboardActivity.lt.startTrackingWithParameters(getNavigator().getActivityContext(), Constants.APP_NAME, Constants.VERSION_NAME, "LastMile", getDataManager().getCode(), getDataManager().getLocationCode(), getDataManager().getVehicleType(), getDataManager().getAuthToken(), getDataManager().getLiveTrackingTripId(), "start", getDataManager().getLiveTrackingMaxFileSize(), Constants.LIVE_TRACKING_URL, getDataManager().getLiveTrackingAccuracy(), getDataManager().getLiveTrackingInterval(), Integer.parseInt(getDataManager().getLatLngLimit()), DISTANCE_API_KEY, getDataManager().getLiveTrackingDisplacement(), getDataManager().getDistance());
                }
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void deleteFile(String imageUrl) {
        try {
            File fileDelete = new File(imageUrl);
            if (fileDelete.exists()) {
                if (fileDelete.delete()) {
                    System.out.println("file Deleted :" + imageUrl);
                } else {
                    System.out.println("file not Deleted :" + imageUrl);
                }
            }
        } catch (Exception ex) {
            getNavigator().showError(ex.getMessage());
            RestApiErrorHandler errorHandler = new RestApiErrorHandler(ex.getCause());
            errorHandler.writeErrorLogs(0, ex.getMessage());
            StartTripViewModel.this.setIsLoading(false);
            getNavigator().showDescription(ex.toString());
            Logger.e(TAG, String.valueOf(ex));
        }
    }

    private String getFileName(String imageKey) {
        if (imageKey.indexOf(".") > 0) {
            imageKey = imageKey.substring(0, imageKey.lastIndexOf("."));
        }
        return imageKey;
    }

    @SuppressLint("CheckResult")
    public void uploadAWSImage(Activity context, String timeStamp, String vehicalNumber, String vehicleType, String typeOfVehicle, String imageUri, String routeName, Long meterReading, String imageCode) {
        try {
            setIsLoading(true);
            uploadImageServer(context, imageUri, imageCode, timeStamp, vehicalNumber, vehicleType, typeOfVehicle, routeName, meterReading);
        } catch (Exception e) {
            getNavigator().showDescription(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void uploadImageServer(Activity context, String imageUrl, String imageCode, String timeStamp, String vehicleNumber, String vehicleType, String typeOfVehicle, String routeName, Long meterReading) {
        StartTripViewModel.this.setIsLoading(true);
        getNavigator().disableSubmitButton();
        final long timeStampTag = System.currentTimeMillis();
        writeEvent(timeStampTag, "Event to upload image, create request body and map before upload image. ");
        try {
            is_start_clicked = false;
            File file = new File(imageUrl);
            byte[] bytes = CryptoUtils.decryptFile1(file.toString(), Constants.ENC_DEC_KEY);
            RequestBody mFile = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), mFile);
            RequestBody trip_emp_code = RequestBody.create(MediaType.parse("text/plain"), getDataManager().getCode());
            RequestBody trip_image_ts = RequestBody.create(MediaType.parse("text/plain"), timeStamp);
            RequestBody image_code = RequestBody.create(MediaType.parse("text/plain"), imageCode);
            RequestBody image_name = RequestBody.create(MediaType.parse("text/plain"), file.getName());
            RequestBody image_type = RequestBody.create(MediaType.parse("text/plain"), "Trip");
            Map<String, RequestBody> map = new HashMap<>();
            map.put("image", mFile);
            map.put("trip_emp_code", trip_emp_code);
            map.put("trip_image_ts", trip_image_ts);
            map.put("image_code", image_code);
            map.put("image_name", image_name);
            map.put("image_type", image_type);
            Map<String, String> headers = new HashMap<>();
            headers.put("token", getDataManager().getAuthToken());
            getCompositeDisposable().add(getDataManager().doImageUploadApiCallStartStop(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), headers, map, fileToUpload).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(imageUploadResponse -> {
                writeEvent(timeStampTag, "Successfully get image upload response: Image Name: " + imageUploadResponse.getFileName());
                if (imageUploadResponse != null) {
                    if (imageUploadResponse.getStatus().equalsIgnoreCase("Success")) {
                        //uploadData(context, imageUrl, imageUploadResponse.getFileName(), imageUploadResponse.getImageId(), vehicleNumber, vehicleType, typeOfVehicle, routeName, meterReading);
                    } else {
                        StartTripViewModel.this.setIsLoading(false);
                        //uploadData(context, imageUrl, "", -1, vehicleNumber, vehicleType, typeOfVehicle, routeName, meterReading);
                    }
                }
            }, throwable -> {
                getNavigator().enableSubmitButton();
                RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(throwable);
                restApiErrorHandler.writeErrorLogs(timeStampTag, throwable.getMessage());
                setIsLoading(false);
                Logger.e(TAG, String.valueOf(throwable));
                //uploadData(context, imageUrl, "", -1, vehicleNumber, vehicleType, typeOfVehicle, routeName, meterReading);
            }));
        } catch (Exception ex) {
            getNavigator().enableSubmitButton();
            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(ex.getCause());
            restApiErrorHandler.writeErrorLogs(timeStampTag, ex.getMessage());
            //uploadData(context, imageUrl, "", -1, vehicleNumber, vehicleType, typeOfVehicle, routeName, meterReading);
            Logger.e(TAG, String.valueOf(ex));
        }
    }

    public void onStartTrip() {
        if (is_start_clicked) {
            is_start_clicked = false;
            getNavigator().StartTrip();
        }
    }

    public void onCameraLaunch() {
        getNavigator().CameraLaunch();
    }

    public void onFrontCameraLaunch() {
        getNavigator().FrontCameraLaunch();
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

    @SuppressLint("CheckResult")
    private void saveForwardList(List<DRSForwardTypeResponse> drsForwardTypeResponses) {
        Constants.number_of_fwd_shipments = drsForwardTypeResponses.size();
        Observable.fromCallable(() -> {
            for (DRSForwardTypeResponse response : drsForwardTypeResponses) {
                try {
                    if (response.mpsAWBNo != null) {
                        response.mpsAWBs = response.mpsAWBNo.toString();
                    }
                    response.setCompositeKey(response.getDrsId() + "" + response.getAwbNo());
                } catch (Exception e) {
                    RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                    restApiErrorHandler.writeErrorLogs(0, e.getMessage());
                    getNavigator().showDescription(e.getMessage());
                    Logger.e(TAG, String.valueOf(e));
                }
            }
            return drsForwardTypeResponses;
        }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(drsForwardTypeResponseList -> {
            try {
                saveFWDAfterGeoCoding(drsForwardTypeResponseList);
            } catch (Exception e) {
                getNavigator().showDescription(e.getMessage());
            }
        });
    }

    private void saveFWDAfterGeoCoding(List<DRSForwardTypeResponse> drsForwardTypeResponses) {
        try {
            getCompositeDisposable().add(getDataManager().
                    saveDRSForwardList(drsForwardTypeResponses).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).
                    subscribe(aBoolean -> {
                    }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void saveNewRTSList(DRSReturnToShipperTypeNewResponse drsReturnToShipperTypeNewResponses) {
        try {
            getCompositeDisposable().add(getDataManager().saveDRSNewRTSList(drsReturnToShipperTypeNewResponses).observeOn(getSchedulerProvider().io()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {
            }, throwable -> {
            }));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @SuppressLint("CheckResult")
    private void saveRVPList(List<DRSReverseQCTypeResponse> drsReverseQCTypeResponses) {
        try {
            Observable.fromCallable(() -> {
                for (DRSReverseQCTypeResponse response : drsReverseQCTypeResponses) {
                    try {
                        response.setCompositeKey(response.getDrs() + "" + response.getAwbNo());
                    } catch (Exception e) {
                        RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                        restApiErrorHandler.writeErrorLogs(0, e.getMessage());
                        Logger.e(TAG, String.valueOf(e));
                    }
                }
                return drsReverseQCTypeResponses;
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(this::saveRVPAfterGeoCoding);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void saveRVPAfterGeoCoding(List<DRSReverseQCTypeResponse> drsReverseQCTypeResponses) {
        HashSet<DRSReverseQCTypeResponse> drsReverseQCTypeResponses_set = new HashSet<>();
        for (int i = 0; i < drsReverseQCTypeResponses.size(); i++) {
            if (!String.valueOf(drsReverseQCTypeResponses.get(i).getCompositeKey()).startsWith("null") && !drsReverseQCTypeResponses.get(i).getCompositeKey().equalsIgnoreCase("")) {
                drsReverseQCTypeResponses_set.add(drsReverseQCTypeResponses.get(i));
            }
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
                    Logger.e(TAG, String.valueOf(throwable));
                }));
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
        }
    }

    private void addRVPWithQC(DRSReverseQCTypeResponse drsReverseQCTypeResponse) {
        List<RvpQualityCheck> rvpQualityCheck = new ArrayList<>();
        List<RvpQualityCheck> rvpQualities = drsReverseQCTypeResponse.getShipmentDetails().getQualityChecks();
        if (rvpQualities != null) {
            rvpQualityCheck = rvpQualities;
        }
        for (RvpQualityCheck rvpQuality : rvpQualityCheck) {
            rvpQuality.setAwbNo(drsReverseQCTypeResponse.getAwbNo());
            rvpQuality.setDrs(drsReverseQCTypeResponse.getDrs());
        }
        try {
            getCompositeDisposable().add(Observable.merge(getDataManager().saveDRSRVPListQualityCheck(rvpQualityCheck), getDataManager().saveDRSRVP(drsReverseQCTypeResponse)).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {
            }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void addEDSNewActivityWizard(EDSResponse edsResponse) {
        try {
            List<EDSActivityWizard> edsActivityWizards = edsResponse.getShipmentDetail().getEdsActivityWizards();
            for (EDSActivityWizard edsActivityWizard : edsActivityWizards) {
                edsActivityWizard.setAwbNo(edsResponse.getAwbNo());
                edsActivityWizard.setQuestion_form_dummy(new JSONObject(edsActivityWizard.getQuestionForm()).toString());
            }
            getCompositeDisposable().add(Observable.merge(getDataManager().saveEDSActivityNewWizardList(edsActivityWizards), getDataManager().saveNewDrsEDS(edsResponse)).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {
            }, throwable -> {
            }));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @SuppressLint("CheckResult")
    private void saveEDSNewList(List<EDSResponse> edsResponses) {
        Observable.fromCallable(() -> {
            for (EDSResponse response : edsResponses) {
                try {
                    response.setCompositeKey(response.getDrsNo() + "" + response.getAwbNo());
                    Location location = response.getConsigneeDetail().getAddress().getLocation();
                    if (location != null && location.getLat() > 0 && location.getLng() > 0) {
                        Logger.e(TAG, location.toString());
                    }
                } catch (Exception e) {
                    RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                    restApiErrorHandler.writeErrorLogs(0, e.getMessage());
                    Logger.e(TAG, String.valueOf(e));
                }
            }
            return edsResponses;
        }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(this::saveEDSAfterGeoCoding);
    }

    private void saveEDSAfterGeoCoding(List<EDSResponse> edsResponses) {
        for (EDSResponse edsResponse : edsResponses) {
            try {
                getCompositeDisposable().add(getDataManager().isEDSDRSNewExist(edsResponse.getCompositeKey()).flatMap(Observable::just).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {
                    if (!aBoolean) {
                        addEDSNewActivityWizard(edsResponse);
                    }
                }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
            } catch (Exception e) {
                getNavigator().showError(e.getMessage());
                Logger.e(TAG, String.valueOf(e));
            }
        }
    }

    public String getEmpCode() {
        return getDataManager().getCode();
    }

    public long startMeterReadingShouldBe() {
        long start_meter_reading = 0;
        try {
            Date current_date = Calendar.getInstance().getTime();
            long stop_meter_reading = getDataManager().getEndTripKm();
            long last_stop_trip_date = getDataManager().getEndTripTime();
            long diff = current_date.getTime() - last_stop_trip_date;
            long days = TimeUnit.MILLISECONDS.toDays(diff);
            if (days == 0) {
                start_meter_reading = stop_meter_reading + getDataManager().getMaxDailyDiffForStartTrip();
            } else {
                start_meter_reading = stop_meter_reading + (days * getDataManager().getMaxDailyDiffForStartTrip());
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return start_meter_reading;
    }

    public void showAlertDialog(Context context, long reading) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
        builder.setMessage(context.getResources().getString(R.string.start_alert_msg) + " " + reading);
        builder.setPositiveButton(R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss());
        Dialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("WrongConstant")
    public void generateNotification(long interval, String description) {
        AlarmManager mAlarmManager;
        NotificationChannel notificationChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel("default", "primary", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = (NotificationManager) getNavigator().getActivityContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(notificationChannel);
            }
            mAlarmManager = (AlarmManager) getNavigator().getActivityContext().getSystemService(ALARM_SERVICE);
            PendingIntent intent = PendingIntent.getBroadcast(getNavigator().getActivityContext(), (int) interval, new Intent(getNavigator().getActivityContext(), NotificationUpdate.class).putExtra("description", description), 0);
            mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, interval, intent);
        }
    }

    public String changeMillisToTime(long millis) {
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(new Date(millis));
    }

    public void getMasterData() {
        final long timeStamp = System.currentTimeMillis();
        try {
            masterRequest request = new masterRequest();
            request.setUsername(getDataManager().getCode());
            writeRestAPIRequst(timeStamp, request);
            getCompositeDisposable().add(getDataManager().doMasterReasonApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request).flatMap(new Function<MasterDataConfig, SingleSource<?>>() {
                @SuppressLint("CheckResult")
                @Override
                public SingleSource<?> apply(MasterDataConfig masterDataReasonCodeResponse) {
                    writeRestAPIResponse(timeStamp, masterDataReasonCodeResponse);
                    setIsLoading(false);
                    if (masterDataReasonCodeResponse != null) {
                        getDataManager().saveMasterReason(masterDataReasonCodeResponse).subscribe(aBoolean -> {
                            if (masterDataReasonCodeResponse.getStatus()) {
                                try {
                                    getDataManager().setSycningBlokingStatus(true);
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
                                                // BP mismatch:-
                                                if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("BP_MISMATCH")) {
                                                    getDataManager().setBPMismatch(Boolean.valueOf(globalConfigurationMaster.getConfigValue()));
                                                }
                                                // UD BP Reason Code:-
                                                if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("UD_BP")) {
                                                    getDataManager().setUDBPCode(globalConfigurationMaster.getConfigValue());
                                                }
                                                //
                                                // OBD_QC_FAIL:-
                                                if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("OBD_QC_FAIL")) {
                                                    getDataManager().setOBDQCFAIL(globalConfigurationMaster.getConfigValue());
                                                }
                                                //
                                                // OBD_REFUSED:-
                                                if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("OBD_REFUSED")) {
                                                    getDataManager().setOBDREFUSED(globalConfigurationMaster.getConfigValue());
                                                }
                                                // Hide Camera in RQC:-
                                                if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("HIDE_CAMERA")) {
                                                    getDataManager().setHideCamera(Boolean.valueOf(globalConfigurationMaster.getConfigValue()));
                                                }
                                                if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ESP_EARNING_VISIBILITY")) {
                                                    getDataManager().setESP_EARNING_VISIBILITY(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                                }
                                                if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("CAMPAIGN_VISIBILITY")) {
                                                    getDataManager().setCampaignStatus(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                                }
                                                if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("SCAN_PACKET_ON_DELIVERY")) {
                                                    getDataManager().setIsScanAwb(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                                }
                                                if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("IMAGE_MANUAL_FLYER")) {
                                                    getDataManager().setImageManualFlyer(Boolean.valueOf(globalConfigurationMaster.getConfigValue()));
                                                }
                                                // For Attendance Feature Enable or Disable:-
                                                if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("SATHI_ATTENDANCE_FEATURE_ENABLE")) {
                                                    getDataManager().setSathiAttendanceFeatureEnable(Boolean.valueOf(globalConfigurationMaster.getConfigValue()));
                                                }
                                                if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("DC_UNDELIVER_STATUS")) {
                                                    getDataManager().setDcUndeliverStatus(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                                }
                                                if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("PERFORMANCE_URL")) {
                                                    getDataManager().setWebLinkUrl(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("IS_SIGNATURE_IMAGE_MANDATORY")) {
                                                    getDataManager().setIsSignatureImageMandatory(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("IS_EDISPUTE_IMAGE_MANDATORY")) {
                                                    getDataManager().setEDISPUTE(globalConfigurationMaster.getConfigValue());
                                                }
                                                // Distance Api Enable:-
                                                if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("IS_DISTANCE_API_ENABLE")) {
                                                    getDataManager().setDistanceAPIEnabled(Boolean.valueOf(globalConfigurationMaster.getConfigValue()));
                                                }
                                            }
                                            for (GlobalConfigurationMaster globalConfigurationMaster : globalConfigurationMasterList) {
                                                // UnAttempted Reason Codes
                                                if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("EDS_UNATTEMPTED_CODE")) {
                                                    getDataManager().setEDSUnattemptedReasonCode(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                                }
                                                if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("FWD_UNATTEMPTED_CODE")) {
                                                    getDataManager().setFWDUnattemptedReasonCode(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                                }
                                                if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("IS_KIRANA_USER")) {
                                                    getDataManager().setKiranaUser(globalConfigurationMaster.getConfigValue());
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
                                                if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("SMS_THROUGH_WHATSAPP")) {
                                                    getDataManager().setSMSThroughWhatsapp(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("address_quality_score")) {
                                                    getDataManager().setAddressQualityScore(globalConfigurationMaster.getConfigValue());
                                                }
                                                if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("TECHPAK_WHATSAPP")) {
                                                    getDataManager().setTechparkWhatsapp(String.valueOf(globalConfigurationMaster.getConfigValue()));
                                                }
                                                // DP_Users_Barcode_Scan_Work:-
                                                if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ESP_SCANNER")) {
                                                    getDataManager().setDPUserBarcodeFlag(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                                }
                                                if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("TRIEDREACHYOU_WHATSAPP")) {
                                                    getDataManager().setTriedReachyouWhatsapp(String.valueOf(globalConfigurationMaster.getConfigValue()));
                                                }
                                                if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("APP_FOOTER")) {
                                                    getDataManager().saveBottomText(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("Forward")) {
                                                    getDataManager().setForwardReasonCodeFlag(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("Rts")) {
                                                    getDataManager().setRTSReasonCodeFlag(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RVP_RQC_BARCODE_SCAN")) {
                                                    getDataManager().setRVPRQCBarcodeScan(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RTS_INPUT_OTP_RESEND")) {
                                                    getDataManager().setRtsInputResendFlag(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RVP")) {
                                                    getDataManager().setRVPReasonCodeFlag(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("EDS")) {
                                                    getDataManager().setEDSReasonCodeFlag(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("PERFORMANCE_URL")) {
                                                    getDataManager().setWebLinkUrl(globalConfigurationMaster.getConfigValue());
                                                }
                                                // start stop trip
                                                else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("MAX_DAILY_DIFF_FOR_START_TRIP")) {
                                                    getDataManager().setMaxDailyDiffForStartTrip(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("MAX_TRIP_RUN_FOR_STOP_TRIP")) {
                                                    getDataManager().setMaxTripRunForStopTrip(globalConfigurationMaster.getConfigValue());
                                                }
                                                if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ENABLE_VOICE_CALL_OTP")) {
                                                    getDataManager().setVCallpopup(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
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
                                                //
                                                if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("REQUEST_RESPONSE_TIME")) {
                                                    getDataManager().setREQUEST_RESPONSE_TIME(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                                }
                                                //
                                                if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("REQUEST_RESPONSE_COUNT")) {
                                                    getDataManager().setREQUEST_RESPONSE_COUNT(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RVP_RQC_BARCODE_SCAN")) {
                                                    getDataManager().setRVPRQCBarcodeScan(globalConfigurationMaster.getConfigValue());
                                                }
                                                if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("UNDELIVERED_CONSIGNEE_RANGE")) {
                                                    getDataManager().setUndeliverConsigneeRANGE(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                                }
                                                if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("TRIP_GEOFENCING")) {
                                                    getDataManager().setTripGeofencing(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("MAX_DAILY_DIFF_FOR_START_TRIP")) {
                                                    getDataManager().setMaxDailyDiffForStartTrip(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("MAX_TRIP_RUN_FOR_STOP_TRIP")) {
                                                    getDataManager().setMaxTripRunForStopTrip(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ALLOW_DUPLICATE_CASH_RECEIPT")) {
                                                    getDataManager().setDuplicateCashReceipt(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("SYNC_SERVICE_INTERVAL")) {
                                                    getDataManager().setSyncDelay(Long.parseLong(globalConfigurationMaster.getConfigValue()));
                                                    Constants.SYNC_DELAY_TIME = Long.parseLong(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("SKIP_OTP_REV_RQC")) {
                                                    getDataManager().setSKIPOTPREVRQC(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("SKIP_CANC_OTP_RVP")) {
                                                    getDataManager().setSKIP_CANC_OTP_RVP(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("FORWARD_REASSIGN")) {
                                                    getDataManager().setFWDRessign(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("LIVE_TRACKING_MAX_FILE_SIZE")) {
                                                    getDataManager().setLiveTrackingMaxFileSize(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("AADHAR_CONSENT_DISCLAIMER")) {
                                                    getDataManager().setAdharMessage(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RVP_BARCODE_FLYER")) {
                                                    getDataManager().setRVPAWBWords(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RVP_UD_FLYER")) {
                                                    getDataManager().setRVP_UD_FLYER(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("AADHAR_MASKING_STATUS_CHECK_INTERVAL")) {
                                                    getDataManager().setAadharStatusInterval(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("UNDELIVERED_COUNT")) {
                                                    getDataManager().setUndeliverCount(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("MAX_EDS_EKYC_FAIL_ATTEMPT")) {
                                                    getDataManager().setMaxEDSFailAttempt(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("LAT_LNG_BATCH_COUNT")) {
                                                    getDataManager().setLatLngLimit(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("LIVE_TRACKING_DEVICE_MOVEMENT_MAX_SPEED")) {
                                                    getDataManager().setLiveTrackingSpeed(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("LIVE_TRACKING_LAT_LNG_ACCURACY")) {
                                                    getDataManager().setLiveTrackingAccuracy(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("LIVE_TRACKING_LAT_LNG_MIN_DISPLACEMENT")) {
                                                    getDataManager().setLiveTrackingDisplacement(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("LIVE_TRACKING_LAT_LNG_TIME_INTERVAL")) {
                                                    getDataManager().setLiveTrackingInterval(String.valueOf(Long.parseLong(globalConfigurationMaster.getConfigValue()) * 1000));
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("LIVE_TRACKING_DEVICE_MOVEMENT_MIN_SPEED")) {
                                                    getDataManager().setLiveTrackingMINSpeed(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("CALL_STATUS_API_INTERVAL")) {
                                                    getDataManager().setCallStatusApiInterval(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ENABLE_GET_CALL_STATUS_API")) {
                                                    getDataManager().setDirectUndeliver(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ENABLE_CALL_API_RECURSION")) {
                                                    getDataManager().setCallAPIRecursion(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("CALL_STATUS_API_RECURSION_INTERVAL")) {
                                                    getDataManager().setRequestResponseTime(Long.parseLong(globalConfigurationMaster.getConfigValue()));
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ENABLE_DP_EMPLOYEE")) {
                                                    getDataManager().setEnableDPEmployee(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("COD_STATUS_INTERVAL")) {
                                                    getDataManager().setCodStatusInterval(Long.parseLong(globalConfigurationMaster.getConfigValue()));
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("COD_STATUS_INTERVAL_FRACTION")) {
                                                    getDataManager().setCodStatusIntervalStatusFraction(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RESCHEDULE_MAX_ATTEMPTS")) {
                                                    getDataManager().setRescheduleMaxAttempts(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("RESCHEDULE_MAX_ATTEPMTS_ALLOWED_DAYS")) {
                                                    getDataManager().setRescheduleMaxDaysAllow(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("IS_USE_CAMSCANNER_PRINT_RECEIPT")) {
                                                    getDataManager().setIsUseCamscannerPrintReceipt(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("IS_USE_CAMSCANNER_DISPUTE")) {
                                                    getDataManager().setIsUseCamscannerDispute(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("IS_USE_CAMSCANNER_TRIP")) {
                                                    getDataManager().setIsUseCamscannerTrip(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("SATHI_LOG_API_CALL_INTERVAL")) {
                                                    getDataManager().setSathiLogApiCallInterval(Long.parseLong(globalConfigurationMaster.getConfigValue()));
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("DISTANCE_GAP_FOR_DIRECTION_CAL")) {
                                                    getDataManager().setDistance(Integer.parseInt(globalConfigurationMaster.getConfigValue()));
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("offline_fwd")) {
                                                    getDataManager().setOfflineFwd(Boolean.parseBoolean(globalConfigurationMaster.getConfigValue()));
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("CONSENT_AGREE_BUTTON_LABEL")) {
                                                    getDataManager().setAdharPositiveButton(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("CONSENT_DISAGREE_BUTTON_LABEL")) {
                                                    getDataManager().setAdharNegativeButton(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("DISABLE_RESEND_OTP_BUTTON_DURATION")) {
                                                    getDataManager().setDisableResendOtpButtonDuration(Long.parseLong(globalConfigurationMaster.getConfigValue()));
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ESP_REFER_SCHEME_TERM_AND_CONDITIONS")) {
                                                    getDataManager().setESPSchemeTerms(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("ESP_REFERRAL_CODE")) {
                                                    getDataManager().setESPReferCode(globalConfigurationMaster.getConfigValue());
                                                } else if (globalConfigurationMaster.getConfigGroup().equalsIgnoreCase("FAKE_APPLICATIONS")) {
                                                    if (!globalConfigurationMaster.getConfigValue().equalsIgnoreCase("")) {
                                                        getDataManager().setFakeApplicatons(globalConfigurationMaster.getConfigValue());
                                                        LocationTracker.setFakeApplications(getDataManager().getFakeApplications());
                                                    }
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
                                            setIsLoading(false);
                                            if (masterDataReasonCodeResponse.getResponse().getCode() == 107) {
                                                Log.d("code107--", "107");
                                                getNavigator().doLogout(masterDataReasonCodeResponse.getResponse().getDescription());
                                            }
                                            Logger.e(TAG, String.valueOf(e));
                                        }
                                    }
                                } catch (Exception ex) {
                                    setIsLoading(false);
                                    String error;
                                    error = new RestApiErrorHandler(ex).getErrorDetails().getEResponse().getDescription();
                                    try {
                                        if (error.equalsIgnoreCase("HTTP 500 ")) {
                                            getDataManager().setSycningBlokingStatus(false);
                                            getNavigator().showErrorMessage(true);
                                        } else {
                                            getNavigator().showError(error);
                                            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(ex.getCause());
                                            restApiErrorHandler.writeErrorLogs(timeStamp, ex.getMessage());
                                            Logger.e(TAG, String.valueOf(ex));
                                        }
                                    } catch (Exception e) {
                                        RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                                        restApiErrorHandler.writeErrorLogs(timeStamp, e.getMessage());
                                        Logger.e(TAG, String.valueOf(e));
                                    }
                                }
                            } else {
                                getNavigator().showError("Master Data Failed Please Try Again..");
                                if (masterDataReasonCodeResponse.getResponse().getCode() == 107) {
                                    getNavigator().doLogout(masterDataReasonCodeResponse.getResponse().getDescription());
                                }
                            }
                        });
                    }
                    return Single.just(true);
                }
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(o -> {
            }, throwable -> {
                try {
                    getNavigator().showError("Master Data Failed Please Try Again.");
                    setIsLoading(false);
                    getDataManager().setSycningBlokingStatus(false);
                    getNavigator().showErrorMessage(Objects.requireNonNull(throwable.getMessage()).contains("HTTP 500"));
                    RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(throwable.getCause());
                    restApiErrorHandler.writeErrorLogs(timeStamp, throwable.getMessage());
                } catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                }
            }));
        } catch (Exception e) {
            getNavigator().showError("Master Data Failed Please Try Again.");
            getNavigator().showError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
            restApiErrorHandler.writeErrorLogs(timeStamp, e.getMessage());
        }
    }

    private void saveRVPShipperId(Reverse reverse) {
        try {
            getCompositeDisposable().add(getDataManager().insertRvpShipperId(reverse).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
            }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void saveShipperId(Forward response) {
        try {
            getCompositeDisposable().add(getDataManager().insertFwdShipperId(response).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
            }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void setAppSettingAlarm(Context context) {
        String emp_code = getDataManager().getEmp_code();
        String last_character = emp_code.substring(emp_code.length() - 1);
        if (Integer.parseInt(last_character) == 8) {
            last_character = "1";
        } else if (Integer.parseInt(last_character) == 9) {
            last_character = "2";
        } else if (Integer.parseInt(last_character) == 0) {
            last_character = "3";
        }
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == Integer.parseInt(last_character)) {
            try {
                if (!ScreenUtils.isAppIsInBackground(context)) {
                    Intent i = new Intent(context, GlobalDialogActivity.class);
                    i.putExtra("AppSettingAlert", true);
                    context.startActivity(i);
                }
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
        }
    }

    private void getRescheduleFlag() {
        try {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(getDataManager().doEdsRescheduleCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), edsRescheduleRequest).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(edsRescheduleResponse -> {
                Logger.i(TAG, "accept: " + edsRescheduleResponse);
                try {
                    edsRescheduleResponse.setAwb_reschedule_info(edsRescheduleResponse.getAwb_reschedule_info());
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
            }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception ex) {
            Logger.e(TAG, String.valueOf(ex));
        }
    }

    public void insertEdsRescheduleData(ArrayList<RescheduleEdsD> response) {
        try {
            getCompositeDisposable().add(getDataManager().insertEdsRescheduleFlag(response).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> getEdsReschdData(), throwable -> Logger.e(TAG, String.valueOf(throwable))));
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

    public void clearAppData() {
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
            getNavigator().onHandleError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }
}