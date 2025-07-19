package in.ecomexpress.sathi.ui.dashboard.mapview;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.backgroundServices.SyncServicesV2;
import in.ecomexpress.sathi.databinding.ActivityDashBoardMapBinding;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.utils.ImageHandler;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class MapActivity extends BaseActivity<ActivityDashBoardMapBinding, MapViewmodel> implements MapNavigator {

    private final String TAG = MapActivity.class.getSimpleName();
    private ActivityDashBoardMapBinding binding;
    private MapViewmodel mdashBoardMapViewmodel;
    public ImageHandler imageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewDataBinding();
        binding = ActivityDashBoardMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        logScreenNameInGoogleAnalytics(TAG, this);
        MapFragment rvpSecureDeliveryMapFragment = new MapFragment(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, rvpSecureDeliveryMapFragment).commit();
        binding.setLifecycleOwner(this);
        mdashBoardMapViewmodel.setNavigator(this);
    }

    @Override
    public MapViewmodel getViewModel() {
        return mdashBoardMapViewmodel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_dash_board_map;
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
    public void onLogoutClick() {
        if (!isNetworkConnected()) {
            showSnackbar(getResources().getString(R.string.no_network_error));
            return;
        }
        try {
            stopService(SyncServicesV2.getStopIntent(MapActivity.this));
            mdashBoardMapViewmodel.logout(MapActivity.this);
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
        Intent intent = new Intent(MapActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
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
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        applyTransitionToBackFromActivity(this);
    }
}
