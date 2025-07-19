package in.ecomexpress.sathi.ui.dashboard.fuel;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import java.util.List;
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityFuelReimburseBinding;
import in.ecomexpress.sathi.repo.remote.model.ErrorResponse;
import in.ecomexpress.sathi.repo.remote.model.fuel.response.Reports;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class FuelReimburseActivity extends BaseActivity<ActivityFuelReimburseBinding, FuelReimburseViewModel> implements IFuelReimburseNavigator {

    @Inject
    FuelReimburseViewModel fuelReimburseViewModel;
    private final String TAG = FuelReimburseActivity.class.getSimpleName();
    ActivityFuelReimburseBinding activityFuelReimburseBinding;
    @Inject
    Context context;
    @Inject
    FuelReimbursementAdapter fuelReimbursementAdapter;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, FuelReimburseActivity.class);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fuelReimburseViewModel.setNavigator(this);
        activityFuelReimburseBinding = getViewDataBinding();
        logScreenNameInGoogleAnalytics(TAG, this);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.fwd));
        try {
            fuelReimburseViewModel.getAllFuelList(FuelReimburseActivity.this);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        setUp();
        //add toolbar
        activityFuelReimburseBinding.toolbarLayout.awb.setText(R.string.fuel_reimbursement);
        activityFuelReimburseBinding.toolbarLayout.backArrow.setOnClickListener(v -> onBackClick());
    }

    @Override
    public FuelReimburseViewModel getViewModel() {
        return fuelReimburseViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_fuel_reimburse;
    }

    public void onBackClick() {
        finish();
        applyTransitionToBackFromActivity(this);
    }

    @Override
    public void onHandleError(ErrorResponse errorDetails) {
        showSnackbar(errorDetails.getEResponse().getDescription());
    }

    @Override
    public void onShowDescription(String error) {
        try {
            showSnackbar(error);
            if (error.equalsIgnoreCase("Invalid Authentication Token.")) {
                fuelReimburseViewModel.logoutLocal();
            }
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @Override
    public void clearStack() {
        showToast(getString(R.string.session_expire));
        Intent intent = new Intent(FuelReimburseActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    @Override
    public void onShowLogout(String description) {
        fuelReimburseViewModel.logoutLocal();
    }

    @Override
    public void showErrorMessage(boolean status) {
        if (status) {
            showSnackbar(getResources().getString(R.string.http_500_msg));
        }
        else {
            showSnackbar(getResources().getString(R.string.server_down_msg));
        }

    }

    @Override
    public void showError(String error) {
        showSnackbar(error);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void OnSetFuelAdapter(List<Reports> reportsList) {
        try {
            if (!reportsList.isEmpty()) {
                activityFuelReimburseBinding.icons.setVisibility(View.VISIBLE);
                fuelReimbursementAdapter.setData(reportsList);
            } else {
                activityFuelReimburseBinding.icons.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            showError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void setUp() {
        activityFuelReimburseBinding.fuelRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        activityFuelReimburseBinding.fuelRecyclerView.setItemAnimator(new DefaultItemAnimator());
        activityFuelReimburseBinding.fuelRecyclerView.setAdapter(fuelReimbursementAdapter);
    }
}