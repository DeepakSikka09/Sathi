package in.ecomexpress.sathi.ui.side_drawer.profile;

import static in.ecomexpress.geolocations.LocationTracker.isMyServiceRunning;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.backgroundServices.SyncServicesV2;
import in.ecomexpress.sathi.databinding.ActivityProfileBinding;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class ProfileActivity extends BaseActivity<ActivityProfileBinding, ProfileViewModel> implements IProfileNavigator {

    private final String TAG = ProfileActivity.class.getSimpleName();
    @Inject
    ProfileViewModel mProfileViewModel;
    ActivityProfileBinding mActivityProfileBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        try{
            mProfileViewModel.setNavigator(this);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.dashboarddark));
            this.mActivityProfileBinding = getViewDataBinding();
            logScreenNameInGoogleAnalytics(TAG, this);
            mProfileViewModel.setProfileData();
        } catch(Exception e){
            showSnackbar(e.getMessage());
            Logger.e("ProfileActivity", String.valueOf(e));
        }
        mActivityProfileBinding.callSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> Constants.IS_CALL_BRIDGE_FLAG_ON = isChecked);
        mActivityProfileBinding.header.backArrow.setOnClickListener(v -> {
            onBackPressed();
            applyTransitionToBackFromActivity(this);
        });
        mActivityProfileBinding.header.headingName.setText(R.string.profile);
    }

    public static Intent getStartIntent(Context context){
        return new Intent(context, ProfileActivity.class);
    }

    @Override
    public ProfileViewModel getViewModel(){
        return mProfileViewModel;
    }

    @Override
    public int getBindingVariable(){
        return BR.viewModel;
    }

    @Override
    public int getLayoutId(){
        return R.layout.activity_profile;
    }

    @Override
    public void onBackClick(){
        finish();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mProfileViewModel.getDataManager().setSycningBlokingStatus(true);
        try{
            if(!isMyServiceRunning(SyncServicesV2.class)){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    ContextCompat.startForegroundService(this, SyncServicesV2.getStartIntent(this));
                } else{
                    SyncServicesV2.start(this);
                }
            }
        } catch(Exception e){
            Logger.e("RestartBroadcastReceiver", String.valueOf(e));
        }
    }

    public boolean isMyServiceRunning(Class<?> serviceClass){
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }
}