package in.ecomexpress.sathi.ui.side_drawer.drawer_main;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logButtonEventInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.activity.OnBackPressedCallback;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.BuildConfig;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.backgroundServices.SyncServicesV2;
import in.ecomexpress.sathi.databinding.ActivitySideDrawerBinding;
import in.ecomexpress.sathi.ui.auth.changepassword.ChangePasswordActivity;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.side_drawer.adm.ADMActivity;
import in.ecomexpress.sathi.ui.side_drawer.dc_location_updation.DCLocationActivity;
import in.ecomexpress.sathi.ui.side_drawer.pendingHistory.PendingHistoryActivity;
import in.ecomexpress.sathi.ui.side_drawer.profile.ProfileActivity;
import in.ecomexpress.sathi.ui.dashboard.switchnumber.SwitchNumberDialog;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.PreferenceUtils;

@AndroidEntryPoint
public class SideDrawerActivity extends BaseActivity<ActivitySideDrawerBinding, SideDrawerViewModel> implements SideDrawerNavigator  {

    private final String TAG = SideDrawerActivity.class.getSimpleName();
    private ActivitySideDrawerBinding activitySideDrawerBinding;
    @Inject
    SideDrawerViewModel sideDrawerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySideDrawerBinding = ActivitySideDrawerBinding.inflate(getLayoutInflater());
        setContentView(activitySideDrawerBinding.getRoot());
        sideDrawerViewModel.setNavigator(this);
        logScreenNameInGoogleAnalytics(TAG, this);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backPress();
            }
        });
        setupClickListeners();
        setEmpDetailsDataAndVisibility();
    }

    @SuppressLint("SetTextI18n")
    private void setEmpDetailsDataAndVisibility(){
        activitySideDrawerBinding.sathiAppVersion.setText("Version " + BuildConfig.VERSION_NAME);
        activitySideDrawerBinding.empName.setText(sideDrawerViewModel.getDataManager().getName());
        activitySideDrawerBinding.empShortName.setText(CommonUtils.getShortName(sideDrawerViewModel.getDataManager().getName()));
        activitySideDrawerBinding.empId.setText(getString(R.string.emp_id) + sideDrawerViewModel.getDataManager().getCode());
        setVisibility(activitySideDrawerBinding.dpAvailabilityContainer, sideDrawerViewModel.getDataManager().getIsAdmEmp());
        setVisibility(activitySideDrawerBinding.updateScLocationContainer, sideDrawerViewModel.getDataManager().isDCLocationUpdateAllowed());
        //setVisibility(activitySideDrawerBinding.callBridgeContainer, "false".equalsIgnoreCase(sideDrawerViewModel.getDataManager().getENABLEDIRECTDIAL()));
    }

    private void setVisibility(View view, boolean isVisible) {
        view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @SuppressLint("NonConstantResourceId")
    private void setupClickListeners() {
        View.OnClickListener clickListener = view -> {
            switch (view.getId()) {
                case R.id.backButton:
                    backPress();
                    break;
                case R.id.profileContainer:
                    openProfileActivity();
                    break;
                case R.id.callBridgeContainer:
                    openSwitchCallBridgeNumberDialog();
                    break;
                case R.id.changePasswordContainer:
                    openChangePasswordActivity();
                    break;
                case R.id.updateScLocationContainer:
                    openDCLocationActivity();
                    break;
                case R.id.pendingForSyncContainer:
                    openPendingForSyncActivity();
                    break;
                case R.id.dpAvailabilityContainer:
                    openDpAvailability();
                    break;
                case R.id.syncConfigurationContainer:
                    callMasterDataApi();
                    break;
                case R.id.resetContainer:
                    resetApplication();
                    break;
                case R.id.logoutContainer:
                    logoutFromApplication();
                    break;
            }
        };

        activitySideDrawerBinding.backButton.setOnClickListener(clickListener);
        activitySideDrawerBinding.profileContainer.setOnClickListener(clickListener);
        activitySideDrawerBinding.callBridgeContainer.setOnClickListener(clickListener);
        activitySideDrawerBinding.changePasswordContainer.setOnClickListener(clickListener);
        activitySideDrawerBinding.updateScLocationContainer.setOnClickListener(clickListener);
        activitySideDrawerBinding.pendingForSyncContainer.setOnClickListener(clickListener);
        activitySideDrawerBinding.dpAvailabilityContainer.setOnClickListener(clickListener);
        activitySideDrawerBinding.syncConfigurationContainer.setOnClickListener(clickListener);
        activitySideDrawerBinding.resetContainer.setOnClickListener(clickListener);
        activitySideDrawerBinding.logoutContainer.setOnClickListener(clickListener);
    }

    private void backPress() {
        finish();
        applyTransitionToBackFromActivity(this);
    }

    private void openProfileActivity() {
        startActivity(ProfileActivity.getStartIntent(this));
        applyTransitionToOpenActivity(this);
    }

    private void openSwitchCallBridgeNumberDialog() {
        if (sideDrawerViewModel.getDataManager().getPstnFormat() == null) {
            showMessageOnUI(getString(R.string.pstn_format_value_is_null), true);
        } else {
            SwitchNumberDialog switchNumberDialog = SwitchNumberDialog.newInstance(this);
            switchNumberDialog.show(getSupportFragmentManager());
        }
    }

    private void openChangePasswordActivity() {
        ChangePasswordActivity changePasswordActivity = ChangePasswordActivity.newInstance();
        changePasswordActivity.show(getSupportFragmentManager());
        changePasswordActivity.setChangePasswordListener(this::finish);
    }

    private void openDCLocationActivity() {
        startActivity(DCLocationActivity.getStartIntent(this));
        applyTransitionToOpenActivity(this);
    }

    private void openPendingForSyncActivity(){
        startActivity(PendingHistoryActivity.getStartIntent(this));
        applyTransitionToOpenActivity(this);
    }

    private void openDpAvailability() {
        Intent intent = new Intent(this, ADMActivity.class);
        intent.putExtra("from", "none");
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    private void callMasterDataApi(){
        if(isNetworkConnected()){
            logButtonEventInGoogleAnalytics(TAG, getString(R.string.callmasterdataapi), "Emp Code " + sideDrawerViewModel.getDataManager().getCode(), this);
            sideDrawerViewModel.doMasterDataApiCall(this);
        } else{
            showMessageOnUI(getResources().getString(R.string.no_network_error), true);
        }
    }

    private void resetApplication() {
        logButtonEventInGoogleAnalytics(TAG, getString(R.string.manuallyresetapplication), "Emp Code " + sideDrawerViewModel.getDataManager().getCode(), this);
        sideDrawerViewModel.clearAppData(this);
    }

    private void logoutFromApplication() {
        if (sideDrawerViewModel.getDataManager().getTripId().equalsIgnoreCase("0")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
            builder.setCancelable(false);
            builder.setMessage(R.string.are_you_sure_you_want_to_logout);
            builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                if (!isNetworkConnected()) {
                    showMessageOnUI(getResources().getString(R.string.no_network_error), true);
                    return;
                }
                try {
                    Intent intent = new Intent(this, SyncServicesV2.class);
                    stopService(intent);
                    logButtonEventInGoogleAnalytics(TAG, getString(R.string.manuallylogoutfromapplication), "Emp Code " + sideDrawerViewModel.getDataManager().getCode(), this);
                    sideDrawerViewModel.doLogoutApiCall(this);
                } catch (Exception e) {
                    showMessageOnUI(e.getMessage(), true);
                }
            });
            builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            showMessageOnUI(getString(R.string.your_trip_is_active_stop_trip_and_logout_from_app), true);
        }
    }

    @Override
    public void clearStackAndMoveToLoginActivity(boolean clearPreferences) {
        if(clearPreferences){
            PreferenceUtils.clearPref(this);
        }
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    @Override
    public void showMessageOnUI(String message, boolean isError){
        if(isError){
            showSnackbar(message);
        } else{
            showSuccessInfo(message);
        }
    }

    @Override
    public SideDrawerViewModel getViewModel() {
        return sideDrawerViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_side_drawer;
    }

}