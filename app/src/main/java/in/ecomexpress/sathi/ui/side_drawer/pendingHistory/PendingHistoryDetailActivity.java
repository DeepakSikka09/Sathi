package in.ecomexpress.sathi.ui.side_drawer.pendingHistory;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityPendingHistoryDetailBinding;
import in.ecomexpress.sathi.repo.local.data.commit.PushApi;
import in.ecomexpress.sathi.repo.local.db.model.ImageModel;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class PendingHistoryDetailActivity extends BaseActivity<ActivityPendingHistoryDetailBinding, PendingHistoryDetailViewModel> implements IPendingHistoryDetailNavigator {

    private static final String TAG = PendingHistoryDetailActivity.class.getSimpleName();
    @Inject
    PendingHistoryDetailViewModel pendingHistoryDetailViewModel;
    private ActivityPendingHistoryDetailBinding activityPendingHistoryDetailBinding;
    @Inject
    PendingHistoryDetailAdapter pendingHistoryDetailAdapter;
    String shipment_type;
    String vendor_name;
    long awb_no = 0;
    String composite_key ="";
    PushApi pushApis_data_clicked;
    boolean shouldAllowBack = true;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        pendingHistoryDetailViewModel.setNavigator(this);
        pendingHistoryDetailViewModel.context = this;
        logScreenNameInGoogleAnalytics(TAG, this);
        this.activityPendingHistoryDetailBinding = getViewDataBinding();
        activityPendingHistoryDetailBinding.header.headingName.setText(R.string.pending_history_detail);
        try{
            awb_no = getIntent().getLongExtra("awb_no", 0);
            composite_key = getIntent().getStringExtra("composite_key");
            shipment_type = getIntent().getStringExtra("shipment_type");
            if(shipment_type == null){
                shipment_type = "";
            }
            if(shipment_type.equalsIgnoreCase("RTS")) {
                vendor_name = getIntent().getStringExtra("vendor_name");
                activityPendingHistoryDetailBinding.awbNumberName.setText("Seller Name:");
                activityPendingHistoryDetailBinding.awbNumber.setText(vendor_name);
            } else {
                activityPendingHistoryDetailBinding.awbNumberName.setText("AWB Number:");
                activityPendingHistoryDetailBinding.awbNumber.setText(awb_no + "");
            }
            activityPendingHistoryDetailBinding.shipmentType.setText(shipment_type);
            activityPendingHistoryDetailBinding.header.backArrow.setOnClickListener(v -> onBackPressed());
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.dashboardtool));
        } catch(Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
        setRecyclerView();
        activityPendingHistoryDetailBinding.commitShipment.setOnClickListener(v -> {
            try{
                int itemsInRecycler = (activityPendingHistoryDetailBinding.pendingHistoryDetailRecyclerView.getAdapter() == null) ? 0 : activityPendingHistoryDetailBinding.pendingHistoryDetailRecyclerView.getAdapter().getItemCount();
                if(!isNetworkConnected()){
                    showSnackbar(getString(R.string.check_internet));
                    return;
                }
                if(pendingHistoryDetailViewModel.isAllImageSynced || itemsInRecycler == 0){
                    if(pushApis_data_clicked == null){
                        showSnackbar("Commit Data Not Found, Please Retry");
                    } else{
                        pendingHistoryDetailViewModel.onShipmentCommitClick(pushApis_data_clicked);
                        showCounter();
                    }
                } else{
                    showSnackbar("Upload All Images First.");
                }
            } catch(Exception e){
                Logger.e(TAG, String.valueOf(e));
            }
        });
    }

    private void setRecyclerView(){
        activityPendingHistoryDetailBinding.pendingHistoryDetailRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        activityPendingHistoryDetailBinding.pendingHistoryDetailRecyclerView.setItemAnimator(new DefaultItemAnimator());
        activityPendingHistoryDetailBinding.pendingHistoryDetailRecyclerView.setAdapter(pendingHistoryDetailAdapter);
    }

    @Override
    public PendingHistoryDetailViewModel getViewModel(){
        return pendingHistoryDetailViewModel;
    }

    @Override
    public int getBindingVariable(){
        return BR.viewModel;
    }

    @Override
    public int getLayoutId(){
        return R.layout.activity_pending_history_detail;
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    public void setSuccessName(){
        runOnUiThread(() -> {
            showToast("Shipment Committed Successfully");
            finish();
        });
    }

    @Override
    public void showError(String message){
        showSnackbar(message);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setDetails(PushApi pushApis_data){
        this.pushApis_data_clicked = pushApis_data;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setDRSStatus(int shipmentSyncStatus){
        if(pushApis_data_clicked.getShipmentStatus() == 2 && shipmentSyncStatus == 2){
            activityPendingHistoryDetailBinding.commitShipment.setVisibility(View.GONE);
        } else{
            activityPendingHistoryDetailBinding.commitShipment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setImagesData(List<ImageModel> imageModels){
        pendingHistoryDetailAdapter.setData(pendingHistoryDetailViewModel, imageModels);
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onResume(){
        super.onResume();
        pendingHistoryDetailViewModel.getPushApiStatus(awb_no, composite_key);
    }

    public void onBackPressed(){
        if(shouldAllowBack){
            super.onBackPressed();
            applyTransitionToBackFromActivity(this);
        } else{
            showSnackbar("BackButton Is Disabled Until The Timer Is off.");
        }
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    public void showCounter(){
        new CountDownTimer(pendingHistoryDetailViewModel.getDataManager().getDisableResendOtpButtonDuration() * 250, 1000) {
            @Override
            public void onTick(long millisUntilFinished){
                shouldAllowBack = false;
                activityPendingHistoryDetailBinding.commitShipment.setEnabled(false);
                activityPendingHistoryDetailBinding.commitShipment.setBackground(getDrawable(R.drawable.pending_timer_background));
                @SuppressLint("DefaultLocale") String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                activityPendingHistoryDetailBinding.commitShipment.setText(hms);
            }
            @Override
            public void onFinish(){
                activityPendingHistoryDetailBinding.commitShipment.setEnabled(true);
                activityPendingHistoryDetailBinding.commitShipment.setBackground(getDrawable(R.drawable.fwd_button));
                activityPendingHistoryDetailBinding.commitShipment.setText(getString(R.string.commit_shipment));
                shouldAllowBack = true;
            }
        }.start();
    }
}