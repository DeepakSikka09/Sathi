package in.ecomexpress.sathi.ui.side_drawer.pendingHistory;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityPendingHistoryBinding;
import in.ecomexpress.sathi.repo.local.data.commit.PushApi;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class PendingHistoryActivity extends BaseActivity<ActivityPendingHistoryBinding, PendingHistoryViewModel> implements IPendingHistoryNavigator{

    private final String TAG = PendingHistoryActivity.class.getSimpleName();
    @Inject
    PendingHistoryViewModel pendingHistoryViewModel;
    private ActivityPendingHistoryBinding activityPendingHistoryBinding;
    @Inject
    PendingHistoryAdapter pendingHistoryAdapter;
    List<PushApi> pushApis = new ArrayList<>();

    public static Intent getStartIntent(Context context) {
        return new Intent(context, PendingHistoryActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pendingHistoryViewModel.setNavigator(this);
        this.activityPendingHistoryBinding = getViewDataBinding();
        logScreenNameInGoogleAnalytics(TAG, this);
        setRecyclerView();
        Window window = getWindow();
        activityPendingHistoryBinding.header.headingName.setText(R.string.pending_for_sync);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.dashboardtool));
        activityPendingHistoryBinding.header.backArrow.setOnClickListener(v -> {
            onBackPressed();
            applyTransitionToBackFromActivity(this);
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessage, new IntentFilter(Constants.SYNC_SERVICE));
    }

    private void setRecyclerView() {
        activityPendingHistoryBinding.pendingHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        activityPendingHistoryBinding.pendingHistoryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        activityPendingHistoryBinding.pendingHistoryRecyclerView.setAdapter(pendingHistoryAdapter);
    }

    @Override
    public PendingHistoryViewModel getViewModel() {
        return pendingHistoryViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_pending_history;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        pendingHistoryViewModel.getAllForward();
    }

    @Override
    public void setPacketData(List<PushApi> pushApis) {
        this.pushApis = pushApis;
        runOnUiThread(() -> {
            if (pushApis.isEmpty()) {
                activityPendingHistoryBinding.noShipmentText.setVisibility(View.VISIBLE);
            } else {
                activityPendingHistoryBinding.noShipmentText.setVisibility(View.GONE);
            }
            pendingHistoryAdapter.setData(this, pushApis);
        });
    }

    @Override
    public void showError(String message) {
        showSnackbar(message);
    }

    private final BroadcastReceiver mMessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                pendingHistoryViewModel.getAllDataPushApi();
            } catch (Exception e) {
                Logger.e("PendingHistoryActivity", String.valueOf(e));
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessage);
    }
}