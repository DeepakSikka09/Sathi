package in.ecomexpress.sathi.ui.dashboard.unattempted_shipments;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.List;
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityUnattemptedShipmentBinding;
import in.ecomexpress.sathi.ui.base.BaseActivity;

@AndroidEntryPoint
public class UnattemptedShipmentActivity extends BaseActivity<ActivityUnattemptedShipmentBinding,UnattemptedShipmentViewmodel>  implements  IUnattemptedNavigator{

    private final String TAG = UnattemptedShipmentActivity.class.getSimpleName();
    @Inject
    UnattemptedShipmentAdapter unattemptedShipmentAdapter;
    @Inject
    UnattemptedShipmentViewmodel unattemptedShipmentViewmodel;
    ActivityUnattemptedShipmentBinding activityUnattemptedShipmentBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        unattemptedShipmentViewmodel.setNavigator(this);
        activityUnattemptedShipmentBinding = getViewDataBinding();
        setContentView(activityUnattemptedShipmentBinding.getRoot());
        activityUnattemptedShipmentBinding.header.headingName.setText(R.string.unattempted_shipment_title);
        logScreenNameInGoogleAnalytics(TAG, this);
        //test
        try {
            initRecyclerView();
            unattemptedShipmentViewmodel.getAllNewDRS();
            activityUnattemptedShipmentBinding.buttonOkay.setOnClickListener(view -> performTaskForButtonOkay(true));
            activityUnattemptedShipmentBinding.header.backArrow.setOnClickListener(view -> {
                finish();
                applyTransitionToBackFromActivity(this);
            });
        } catch (Exception e){
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public UnattemptedShipmentViewmodel getViewModel() {
        return unattemptedShipmentViewmodel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_unattempted_shipment;
    }

    private void initRecyclerView() {
        activityUnattemptedShipmentBinding.mRecyclerView.setLayoutManager(new LinearLayoutManager(UnattemptedShipmentActivity.this));
        activityUnattemptedShipmentBinding.mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        activityUnattemptedShipmentBinding.mRecyclerView.setAdapter(unattemptedShipmentAdapter);
    }

    public void performTaskForButtonOkay(boolean doUnattemptedShipmentCount) {
        try {
            DashboardActivityInstance.getInstance().navigateToStopTrip(doUnattemptedShipmentCount);
            finish();
            applyTransitionToOpenActivity(this);
        } catch (Exception e){
            showSnackbar(e.getMessage());
        }
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void updateList(List<UnattemptedShipments> unattemptedShipments) {
        unattemptedShipmentAdapter.setUnattemptedShipmentData(unattemptedShipments, UnattemptedShipmentActivity.this);
        activityUnattemptedShipmentBinding.textViewTotalUnAttemptedCount.setText("You have " + unattemptedShipments.size() + " unattempted shipment" + (unattemptedShipments.size() > 1 ? "s" : "") + ", are you sure that you are unable to attempt ?");
        unattemptedShipmentAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError(String e) {
        showSnackbar(e);
    }

    @Override
    public void navigateToNextActivity(boolean doUnattemptedShipmentCount){
        performTaskForButtonOkay(doUnattemptedShipmentCount);
    }
}