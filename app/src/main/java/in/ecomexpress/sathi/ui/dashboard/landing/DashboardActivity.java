package in.ecomexpress.sathi.ui.dashboard.landing;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.Constants.Open_To_Do;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.scottyab.rootbeer.RootBeer;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.geolocations.LocationTracker;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.backgroundServices.DRSRemarkService;
import in.ecomexpress.sathi.backgroundServices.SathiLocationService;
import in.ecomexpress.sathi.backgroundServices.SyncServicesV2;
import in.ecomexpress.sathi.databinding.ActivityDashboardBinding;
import in.ecomexpress.sathi.repo.remote.model.ErrorResponse;
import in.ecomexpress.sathi.repo.remote.model.masterdata.DashboardBanner;
import in.ecomexpress.sathi.ui.auth.changepassword.ChangePasswordActivity;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.dashboard.attendance.activity.AttendanceActivity;
import in.ecomexpress.sathi.ui.dashboard.campaign.CampaignActivity;
import in.ecomexpress.sathi.ui.dashboard.fe_earned.Earned_Activity;
import in.ecomexpress.sathi.ui.dashboard.fuel.FuelReimburseActivity;
import in.ecomexpress.sathi.ui.dashboard.global_activity.GlobalDialogActivity;
import in.ecomexpress.sathi.ui.dashboard.performance.PerformanceActivity;
import in.ecomexpress.sathi.ui.dashboard.starttrip.MyDialogCloseListener;
import in.ecomexpress.sathi.ui.dashboard.starttrip.StartTripActivity;
import in.ecomexpress.sathi.ui.dashboard.stoptrip.StopTripActivity;
import in.ecomexpress.sathi.ui.dashboard.training.TrainingActivity;
import in.ecomexpress.sathi.ui.dashboard.unattempted_shipments.DashboardActivityInstance;
import in.ecomexpress.sathi.ui.dashboard.unattempted_shipments.UnattemptedShipmentActivity;
import in.ecomexpress.sathi.ui.drs.todolist.ToDoListActivity;
import in.ecomexpress.sathi.ui.side_drawer.drawer_main.SideDrawerActivity;
import in.ecomexpress.sathi.ui.side_drawer.profile.ProfileActivity;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.custom_view.LocationCapture;

@AndroidEntryPoint
public class DashboardActivity extends BaseActivity<ActivityDashboardBinding, DashboardViewModel> implements IDashboardNavigator, MyDialogCloseListener {

    private final String TAG = DashboardActivity.class.getSimpleName();
    public static final String SHOW_POPUP_STOP_TRIP = "stop_trip_popup";
    public static final String MESSAGE = "message";
    public static int imageCaptureCount = 0;
    @SuppressLint("StaticFieldLeak")
    public static LocationTracker lt;
    SliderAdapter sliderAdapter;
    @Inject
    DashboardViewModel dashboardViewModel;
    Timer timer;
    ChangePasswordActivity changePasswordActivity;
    List<DashboardBanner> getDashboardBanner;
    Activity activity;
    private LocationCapture mLocation;
    private ActivityDashboardBinding activityDashboardBinding;
    private IntentIntegrator qrScan;

    private final BroadcastReceiver mDistance = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };

    private final BroadcastReceiver newLocation = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };

    private final BroadcastReceiver mMessage = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(MESSAGE);
            if (Objects.requireNonNull(message).equalsIgnoreCase("start")) {
                initLocation();
            } else if (message.equalsIgnoreCase("stop")) {
                stopLiveTracking();
            }
        }
    };

    public static Intent getStartIntent(Context context) {
        return new Intent(context, DashboardActivity.class);
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashboardViewModel.setNavigator(this);
        dashboardViewModel.getAllApiUrl("onCreate");
        this.activityDashboardBinding = getViewDataBinding();
        logScreenNameInGoogleAnalytics(TAG, this);
        dashboardViewModel.getDataManager().setScreenStatus(false);
        activity = DashboardActivity.this;
        SathiLocationService.startLocationUpdates(this, dashboardViewModel.getDataManager());

        // Calling MDC with condition (If during login MDC is not call):-
        if (!dashboardViewModel.getDataManager().getMasterDataSync()) {
            dashboardViewModel.getMasterData(this);
        }

        if (!dashboardViewModel.getDataManager().getTripId().equalsIgnoreCase("0") && !dashboardViewModel.getDataManager().getIsAdmEmp()) {
            LocationTracker.getInstance(getApplicationContext(), DashboardActivity.this, true, false).runLocationServices();
        }
        try {
            dashboardViewModel.getAllForwardReasonCodeMasterList();
            dashboardViewModel.getAllDashboardBanner();
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }

        startDRSRemarkService();
        mLocation = new LocationCapture(this);
        mLocation.setBlurRadius(5000);
        if (!mLocation.hasLocationEnabled()) {
            if (!dashboardViewModel.getDataManager().getTripId().equalsIgnoreCase("0") && !dashboardViewModel.getDataManager().getIsAdmEmp()) {
                in.ecomexpress.geolocations.LocationTracker.getInstance(DashboardActivity.this, DashboardActivity.this, true, false).runLocationServices();
            }
        }
        registerReceiver(gpsReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        LocalBroadcastManager.getInstance(DashboardActivity.this).registerReceiver(gpsReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        LocalBroadcastManager.getInstance(DashboardActivity.this).registerReceiver(mMessage, new IntentFilter("StartTrip"));
        LocalBroadcastManager.getInstance(DashboardActivity.this).registerReceiver(mMessage, new IntentFilter("StopTrip"));
        LocalBroadcastManager.getInstance(DashboardActivity.this).registerReceiver(mDistance, new IntentFilter("calculate_distance")); // Distance matrix

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (dashboardViewModel.getPreviousTripStatus()) {
                dashboardViewModel.getSyncList(DashboardActivity.this, true);
            }
        }, 1000);
        qrScan = new IntentIntegrator(this);
        activityDashboardBinding.empShortName.setText(CommonUtils.getShortName(dashboardViewModel.getDataManager().getName()));
        activityDashboardBinding.sideDrawerView.setOnClickListener(v -> openSideDrawerActivity());
        activityDashboardBinding.tripIcon.setOnClickListener(v -> {
            activityDashboardBinding.tripIcon.setEnabled(false);
            dashboardViewModel.onStartTripClick();
            new Handler(Looper.getMainLooper()).postDelayed(() -> activityDashboardBinding.tripIcon.setEnabled(true), 3000);
        });
    }

    private void startDRSRemarkService() {
        try {
            DRSRemarkService.startService(DashboardActivity.this);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void openSideDrawerActivity() {
        Intent intent = new Intent(this, SideDrawerActivity.class);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    private void openUnattemptedShipmentActivity() {
        try {
            DashboardActivityInstance.getInstance().setDashboardActivity(this);
            Intent intent = new Intent(DashboardActivity.this, UnattemptedShipmentActivity.class);
            startActivity(intent);
            applyTransitionToOpenActivity(this);
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
    }


    @Override
    public void openChangePasswordActivity() {
        changePasswordActivity = ChangePasswordActivity.newInstance();
        changePasswordActivity.show(getSupportFragmentManager());
        changePasswordActivity.setChangePasswordListener(this::finish);
    }

    @Override
    public void openProfileActivity() {
        startActivity(ProfileActivity.getStartIntent(DashboardActivity.this));
        applyTransitionToOpenActivity(this);
    }

    @Override
    public void openStatisticsActivity() {
        if (!isNetworkConnected()) {
            showSnackbar(getResources().getString(R.string.no_network_error));
            return;
        }
        startActivity(PerformanceActivity.getStartIntent(DashboardActivity.this));
        applyTransitionToOpenActivity(this);
    }

    @Override
    public void openTodo() {
        startActivity(ToDoListActivity.getStartIntent(DashboardActivity.this));
        applyTransitionToOpenActivity(this);
    }

    @SuppressLint("NewApi")
    @Override
    public void openStartTrip() {
        if (isDeviceRooted()) {
            blockAppAccess();
            return;
        }
        if (!CommonUtils.isAllPermissionAllow(DashboardActivity.this)) {
            openSettingActivity();
            return;
        }
        openStartTripActivity();
    }

    @Override
    public void openStopTrip() {
        if (!CommonUtils.isAllPermissionAllow(DashboardActivity.this)) {
            openSettingActivity();
            return;
        }
        try {
            if (dashboardViewModel.getUnAttemptedShipmentCount() > 0) {
                openUnattemptedShipmentActivity();
                return;
            }
            openStopTripActivity();
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void noToDo() {
        showSnackbar(getString(R.string.start_your_trip));
    }


    @Override
    public void onHandleError(ErrorResponse errorDetails) {
        showSnackbar(errorDetails.getEResponse().getDescription());
    }

    @Override
    public void doLogout(String message) {
        try {
            runOnUiThread(() -> {
                showToast(getString(R.string.session_expire));
                dashboardViewModel.logoutLocal();
            });
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void openFuelReimburse() {
        if (!isNetworkConnected()) {
            showSnackbar(getResources().getString(R.string.no_network_error));
            return;
        }
        if ((dashboardViewModel.getDataManager().getIsAdmEmp())) {
            try {
                if (dashboardViewModel.getDataManager().getESP_EARNING_VISIBILITY()) {
                    activityDashboardBinding.fuelTxtView.setText("Earning");
                    activityDashboardBinding.imgFuel.setImageResource(R.drawable.dp_earning_icon);
                    Intent intent = new Intent(DashboardActivity.this, Earned_Activity.class);
                    startActivity(intent);
                    applyTransitionToOpenActivity(this);
                } else {
                    showStringError("This Feature Is Under Development");
                }
            } catch (Exception e) {
                showStringError("This Feature Is Under Development");
            }
        } else {
            activityDashboardBinding.fuelTxtView.setText("Fuel");
            activityDashboardBinding.imgFuel.setImageResource(R.drawable.petrol);
            startActivity(FuelReimburseActivity.getStartIntent(this));
            applyTransitionToOpenActivity(this);
        }
    }

    @Override
    public void onSyncClick() {
        try {
            if (CommonUtils.checkMultiSpace(this, dashboardViewModel.getDataManager()) && !CommonUtils.checkDeveloperMode(this)) {
                dashboardViewModel.drsData(this);
                if (activityDashboardBinding.imgSync.isClickable()) {
                    dashboardViewModel.setSyncUnfocused();
                    if (!isNetworkConnected()) {
                        showSnackbar(getResources().getString(R.string.no_network_error));
                        return;
                    }
                    dashboardViewModel.getAllApiUrl("Sync");
                } else {
                    dashboardViewModel.showDelayAlert(DashboardActivity.this);
                }
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onAttendanceClick() {
        if (!isNetworkConnected()) {
            showSnackbar(getResources().getString(R.string.no_network_error));
            return;
        }
        startActivity();
    }

    private void startActivity() {
        Intent intent = new Intent(DashboardActivity.this, AttendanceActivity.class);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }


    @Override
    public void onLogoutClick(boolean flag) {
        if (flag) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this, R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
            builder.setCancelable(false);
            builder.setMessage("Are You Sure You Want To Logout?");
            builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                // If user pressed "yes", then he is allowed to exit from application
                if (!isNetworkConnected()) {
                    showSnackbar(getResources().getString(R.string.no_network_error));
                    return;
                }
                try {
                    stopService(SyncServicesV2.getStopIntent(DashboardActivity.this));
                    dashboardViewModel.logout();
                } catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                }
            });
            builder.setNegativeButton(R.string.no, (dialog, which) -> {
                // If user select "No", just startTripSyncDrs this dialog and continue with app
                dialog.cancel();
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            showSnackbar("Your Trip Is Active. Stop Trip And Logout From App");
        }
    }

    @Override
    public void onTrainingClick() {
        try {
            if (!isNetworkConnected()) {
                showSnackbar(getResources().getString(R.string.no_network_error));
            } else if (eventValidator(GlobalConstant.DynamicAppUrl.unify_apps)) {
                showSnackbar("This Feature Is Under Development");
            } else {
                Intent intent = new Intent(DashboardActivity.this, TrainingActivity.class);
                startActivity(intent);
                applyTransitionToOpenActivity(this);
            }
        } catch (Exception e) {
            showSnackbar("This Feature Is Under Development");
        }
    }


    @SuppressLint("NewApi")
    @Override
    public void onCampaignClick() {
        try {
            if (!isNetworkConnected()) {
                showSnackbar(getResources().getString(R.string.no_network_error));
            } else if (dashboardViewModel.getDataManager().getCampaignStatus()) {
                if (eventValidator(GlobalConstant.DynamicAppUrl.unify_apps_campaign)) {
                    showSnackbar("This Feature Is Under Development");
                } else {
                    Intent intent = new Intent(DashboardActivity.this, CampaignActivity.class);
                    startActivity(intent);
                    applyTransitionToOpenActivity(this);
                }
            } else {
                showSnackbar("This Feature Is Under Development");
            }
        } catch (Exception e) {
            showSnackbar("This Feature Is Under Development");
        }
    }

    @Override
    public void showError(String error) {
        try {
            showSnackbar(error);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void showStringError(String message) {
        showSnackbar(message);
    }

    @Override
    public void clearStack() {
        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    @Override
    public Context getActivityContext() {
        return DashboardActivity.this;
    }

    @Override
    public Activity getActivityActivity() {
        return activity;
    }

    @Override
    public void dashboardBannerList(List<DashboardBanner> mydashboardBannerList) {
        getDashboardBanner = mydashboardBannerList;
        if (mydashboardBannerList.isEmpty()) {
            activityDashboardBinding.viewPager.setVisibility(View.GONE);
            activityDashboardBinding.noBannerPlaceholder.setVisibility(View.VISIBLE);
            activityDashboardBinding.noBannerPlaceholder.setImageDrawable(AppCompatResources.getDrawable(this, R.mipmap.ic_launcher_foreground));
        } else {
            activityDashboardBinding.noBannerPlaceholder.setVisibility(View.GONE);
            activityDashboardBinding.viewPager.setVisibility(View.VISIBLE);
            sliderAdapter = new SliderAdapter(this, mydashboardBannerList);
            activityDashboardBinding.viewPager.setAdapter(sliderAdapter);
            activityDashboardBinding.indicator.setupWithViewPager(activityDashboardBinding.viewPager, true);
            timer = new Timer();
            timer.scheduleAtFixedRate(new SliderTimer(), 4000, 6000);
        }
    }

    @Override
    public void sendMsg() {
        showSnackbar("Start The Trip Before DRS Sync");
    }

    @Override
    public void showErrorMessage(boolean status) {
        if (status) {
            showSnackbar(getResources().getString(R.string.http_500_msg));
        } else {
            showSnackbar(getResources().getString(R.string.server_down_msg));
        }
    }

    @Override
    public void enableSyncButton() {
        activityDashboardBinding.imgSync.setClickable(true);
    }

    @Override
    public void disableSyncButton() {
        activityDashboardBinding.imgSync.setClickable(false);
    }

    @Override
    public void showAlertWarning(String msg, String mode) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this, R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
        builder.setMessage(msg);
        builder.setPositiveButton(getResources().getString(R.string.ok), (dialogInterface, i) -> {
            try {
                if (mode.equalsIgnoreCase("W")) {
                    dialogInterface.dismiss();
                    if (dashboardViewModel.getDataManager().getTripId().equalsIgnoreCase("0")) {
                        openStartTrip();
                    } else {
                        dashboardViewModel.getSyncList(DashboardActivity.this, true);
                    }
                } else if (mode.equalsIgnoreCase("R")) {
                    dialogInterface.dismiss();
                }
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void callSync() {
        Constants.SYNCFLAG = true;
        if (!isMyServiceRunning(SyncServicesV2.class)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(this, SyncServicesV2.getStartIntent(getApplicationContext()));
            } else {
                SyncServicesV2.start(getApplicationContext());
            }
        }
    }

    @Override
    public Context setContext() {
        return DashboardActivity.this;
    }

    @Override
    public void showSuccess(String msg) {
        showSuccessInfo(msg);
    }

    @Override
    public void scanQRCode() {
        qrScan.setOrientationLocked(false);
        qrScan.initiateScan();
    }

    @Override
    public void callCheckAttendanceApi() {
        // check attendance api for current date
        Calendar calendar = Calendar.getInstance();
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int mMonth = calendar.get(Calendar.MONTH) + 1;


        if (dashboardViewModel.getDataManager().getSathiAttendanceFeatureEnable() && dashboardViewModel.getDataManager().getCheckAttendanceLoginStatus()) {
            if (dashboardViewModel.getDataManager().getLoginDate().equalsIgnoreCase(String.valueOf(mDay)) && dashboardViewModel.getDataManager().getLoginMonth() == mMonth) {
                dashboardViewModel.checkAttendance(this, dashboardViewModel.getDataManager().getEmp_code());
            }
        }
    }

    private boolean isDeviceRooted() {
        if (!dashboardViewModel.getRootDeviceDisabled()) {
            return false;
        }
        RootBeer rootBeer = new RootBeer(DashboardActivity.this);
        return rootBeer.isRooted();
    }

    private void blockAppAccess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this, R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
        builder.setCancelable(false);
        builder.setMessage(R.string.device_rooted);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> System.exit(0));
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            initLocation();
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onResume() {
        super.onResume();
        if (dashboardViewModel.getDataManager().getTripId().equalsIgnoreCase("0") && !dashboardViewModel.getPreviousTripStatus()) {
            activityDashboardBinding.tripIcon.setImageResource(R.drawable.start_trip_icon);
        } else {
            activityDashboardBinding.tripIcon.setImageResource(R.drawable.stop_trip_icon);
        }
        // Show Pending Data:-
        showPendingAndHandoverTask();
        Constants.WEB_TOKEN = dashboardViewModel.getDataManager().getAuthToken();
        Constants.water_mark_emp_code = dashboardViewModel.getDataManager().getEmp_code();
        try {
            dashboardViewModel.getDataManager().isRecentRemoved(false);
            if (!(Build.MANUFACTURER + ":" + Build.MODEL).toUpperCase(Locale.US).equals(Constants.ZEBRA)) {
                if (!mLocation.hasLocationEnabled()) {
                    if (!dashboardViewModel.getDataManager().getTripId().equalsIgnoreCase("0") && !dashboardViewModel.getDataManager().getIsAdmEmp()) {
                        in.ecomexpress.geolocations.LocationTracker.getInstance(this, this, true, false).runLocationServices();
                    }
                }
                inItLocationTracker();
                dashboardViewModel.isStopClicked = false;
            }
            if (dashboardViewModel.getDataManager().getIsAdmEmp()) {
                activityDashboardBinding.fuelTxtView.setText("Earning");
                activityDashboardBinding.imgFuel.setImageResource(R.drawable.dp_earning_icon);
            } else {
                activityDashboardBinding.fuelTxtView.setText("Fuel");
                activityDashboardBinding.imgFuel.setImageResource(R.drawable.petrol);
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        if (!TextUtils.isEmpty(Open_To_Do)) {
            Open_To_Do = "";
            activityDashboardBinding.imgSync.performClick();
        }
    }

    @SuppressLint("SetTextI18n")
    public void showPendingAndHandoverTask() {
        // Fetch all shipment status data from local room DB.
        dashboardViewModel.getAllCategoryAssignedCount();
        // Set Pending Shipment Count.
        dashboardViewModel.getForwardCount().observe(this, count -> activityDashboardBinding.numberFwd.setText(String.valueOf(count)));
        dashboardViewModel.getRtsCount().observe(this, count -> activityDashboardBinding.pendingRtsShipmentCount.setText(String.valueOf(count)));
        dashboardViewModel.getRvpCount().observe(this, count -> activityDashboardBinding.numberRvp.setText(String.valueOf(count)));
        dashboardViewModel.getTotalAssignedCount().observe(this, count -> activityDashboardBinding.pendingTaskTextView.setText("Pending Task’s: " + count));
        // Set Successfully Shipment Count.
        dashboardViewModel.getSuccessForwardCount().observe(this, count -> activityDashboardBinding.successNumberFwd1.setText(String.valueOf(count)));
        dashboardViewModel.getSuccessRTSCount().observe(this, count -> activityDashboardBinding.successNumberrts1.setText(String.valueOf(count)));
        dashboardViewModel.getSuccessRvpCount().observe(this, count -> activityDashboardBinding.successNumberRvp1.setText(String.valueOf(count)));
        dashboardViewModel.getTotalSuccessCount().observe(this, count -> activityDashboardBinding.successTextView.setText("Successfully Completed: " + count));
        // Set Failed Shipment Count.
        dashboardViewModel.getFailedForwardCount().observe(this, count -> activityDashboardBinding.numberFwd1.setText(String.valueOf(count)));
        dashboardViewModel.getFailedRtsCount().observe(this, count -> activityDashboardBinding.numberrts1.setText(String.valueOf(count)));
        dashboardViewModel.getSuccessRvpCount().observe(this, count -> activityDashboardBinding.numberRvp1.setText(String.valueOf(count)));
        dashboardViewModel.getTotalFailedCount().observe(this, count -> activityDashboardBinding.handoverTextView.setText("To Handover: " + count));
    }

    public void openStopTripActivity() {
        Intent intent = new Intent(DashboardActivity.this, StopTripActivity.class);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    public void openStartTripActivity() {
        Intent intent = new Intent(DashboardActivity.this, StartTripActivity.class);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    private void inItLocationTracker() {
        boolean isPlayStore = true;
        lt = LocationTracker.getInstance(this, DashboardActivity.this, isPlayStore, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void initLocation() {
        try {
            if (checkPermission()) {
                inItLocationTracker();
            } else {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    dashboardViewModel.showLocationAlert(this);
                }
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        if (!checkPermission(Constants.permissions)) {
            requestPermission();
        }
    }

    private boolean checkPermission() {
        String[] permission = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_BACKGROUND_LOCATION};
        for (String per : permission) {
            if ((ContextCompat.checkSelfPermission(DashboardActivity.this, per) != PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkPermission(String[] permissions) {
        for (String permission : permissions) {
            if (!hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    private void requestPermission() {
        requestPermissionsSafely(Constants.permissions, 100);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(newLocation);
    }

    @Override
    public boolean hasPermission(String permission) {
        return super.hasPermission(permission);
    }

    @Override
    public DashboardViewModel getViewModel() {
        return dashboardViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_dashboard;
    }


    @SuppressWarnings("MissingSuperCall")
    @Override
    public void onBackPressed() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this, R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
            builder.setCancelable(false);
            builder.setMessage("Do You Want To Exit?");
            builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                // If user pressed "yes", then he is allowed to exit from application
                finishAffinity();
            });
            builder.setNegativeButton(R.string.no, (dialog, which) -> {
                // If user select "No", just startTripSyncDrs this dialog and continue with app
                dialog.cancel();
            });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void stopLiveTracking() {
        LocationTracker.stopTracking(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    Logger.e(TAG, TAG);
                } else {
                    try {
                        dashboardViewModel.DPScanReferenceCode(result.getContents());
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                }
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @SuppressLint("InlinedApi")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0, len = permissions.length; i < len; i++) {
            String permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                if (permission.equalsIgnoreCase("android.permission.ACCESS_BACKGROUND_LOCATION")) {
                    boolean showRationale = shouldShowRequestPermissionRationale(permission);
                    if (!showRationale) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 456);
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(DashboardActivity.this).unregisterReceiver(mMessage);
        LocalBroadcastManager.getInstance(DashboardActivity.this).unregisterReceiver(mDistance);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(gpsReceiver);
        DashboardActivityInstance.getInstance().setDashboardActivity(null);
        super.onDestroy();
    }

    @Override
    public void handleDialogClose() {
        startActivity(ToDoListActivity.getStartIntent(DashboardActivity.this).putExtra("From", "startTrip"));
        applyTransitionToOpenActivity(this);
    }

    public void navigateToStopTrip(boolean doUnattemptedShipmentCount) {
        if (doUnattemptedShipmentCount) {
            dashboardViewModel.unattemptedShipmentCount.set(0);
        }
        openStopTripActivity();
    }

    private class SliderTimer extends TimerTask {
        @Override
        public void run() {
            DashboardActivity.this.runOnUiThread(() -> {
                if (activityDashboardBinding.viewPager.getCurrentItem() < getDashboardBanner.size() - 1) {
                    activityDashboardBinding.viewPager.setCurrentItem(activityDashboardBinding.viewPager.getCurrentItem() + 1);
                } else {
                    activityDashboardBinding.viewPager.setCurrentItem(0);
                }
            });
        }
    }

    private final BroadcastReceiver gpsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (Objects.requireNonNull(intent.getAction()).matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                    String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                    boolean isGPSOnOff = provider.contains("gps");
                    if (!isGPSOnOff) {
                        Intent i = new Intent(context, GlobalDialogActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra("FlagType", "GPS");
                        context.startActivity(i);
                    } else {
                        if (GlobalDialogActivity.finishActivityFlag != null) {
                            GlobalDialogActivity.finishActivityFlag.finish();
                        }
                    }
                }
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
        }
    };

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.clear();
        outState.putString("IMAGE_URI", dashboardViewModel.getDataManager().getImageUri());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Constants.IsStartTrip = dashboardViewModel.getDataManager().getStatTrip();
        dashboardViewModel.getDataManager().setImageUri(Uri.parse(savedInstanceState.getString("IMAGE_URI")));
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}