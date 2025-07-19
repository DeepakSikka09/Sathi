package in.ecomexpress.sathi.ui.drs.rts.rts_success;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Named;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityRtsSuccessScreenBinding;
import in.ecomexpress.sathi.repo.local.data.activitiesdata.RTSActivitiesData;
import in.ecomexpress.sathi.repo.local.data.rts.RTSCommit;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.drs.todolist.ToDoListActivity;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.GlobalConstant;

@AndroidEntryPoint
public class RTSSuccessActivity extends BaseActivity<ActivityRtsSuccessScreenBinding, RTSSuccessViewModel> implements IRTSSuccessNavigator {

    private final String TAG = RTSSuccessActivity.class.getSimpleName();
    String feedbackTextMessage;
    private Handler handler;
    @Inject
    RTSSuccessViewModel rtsSuccessViewModel;
    ActivityRtsSuccessScreenBinding activitySuccessScreenBinding;
    Long rtsVWDetailID = null;
    @Inject
    RTSCommit rtsCommit;
    int shipment_status=0;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rtsSuccessViewModel.setNavigator(this);
        activitySuccessScreenBinding = getViewDataBinding();
        logScreenNameInGoogleAnalytics(TAG, this);
        activitySuccessScreenBinding.goToHomeButton.setOnClickListener(v -> openTodoActivity());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showSnackbar(getString(R.string.cannot_go_back_Rvp));
            }
        });
        try {
            String decideNext = "";
            String name = "";
            String address = "";
            RTSActivitiesData rtsActivitiesData = getIntent().getParcelableExtra("rtsActivitiesData");
            if (rtsActivitiesData != null) {
                try{
                    decideNext = rtsActivitiesData.getDecideNext();
                    name = rtsActivitiesData.getConsigneeName();
                    address = rtsActivitiesData.getAddress();
                    rtsVWDetailID = rtsActivitiesData.getRtsVWDetailID();
                } catch (Exception e){
                    showError(e.getMessage());
                }
            }
            if (decideNext.equals(Constants.FAIL)) {
                shipment_status = Constants.SHIPMENT_DELIVERED_STATUS;
                activitySuccessScreenBinding.decideText.setText(Constants.FAIL);
                rtsSuccessViewModel.setImage(false);
                rtsSuccessViewModel.setTextColor(false);
                activitySuccessScreenBinding.statusText.setText(R.string.shipment_undelivered);
                activitySuccessScreenBinding.consigneeNameTv.setText(name);
                activitySuccessScreenBinding.consigneeAddressTv.setText(address);
            } else {
                shipment_status = Constants.SHIPMENT_DELIVERED_STATUS;
                activitySuccessScreenBinding.decideText.setText(Constants.SUCCESS);
                rtsSuccessViewModel.setImage(true);
                rtsSuccessViewModel.setTextColor(true);
                activitySuccessScreenBinding.statusText.setText(getString(R.string.now_you_can_handover_this_shipment_from_customer));
                activitySuccessScreenBinding.consigneeNameTv.setText(name);
                activitySuccessScreenBinding.consigneeAddressTv.setText(address);
                if(!rtsSuccessViewModel.getDataManager().getFeedbackMessage().equalsIgnoreCase("")) {
                    feedbackTextMessage = CommonUtils.randomStringSelector(rtsSuccessViewModel.getDataManager().getFeedbackMessage());
                    showFeedbackDialog(feedbackTextMessage);
                }
            }
            rtsSuccessViewModel.getShipmentData(rtsCommit, rtsVWDetailID);
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
    }

    public static Intent getStartIntent(Context context) {
        return new Intent(context, RTSSuccessActivity.class);
    }

    @Override
    public RTSSuccessViewModel getViewModel() {
        return rtsSuccessViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_rts_success_screen;
    }

    @Override
    public void showError(String e) {
        showSnackbar(e);
    }

    private void openTodoActivity() {
        try {
            Intent intent = ToDoListActivity.getStartIntent(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtra(ToDoListActivity.ITEM_MARKED,rtsVWDetailID + "");
            intent.putExtra(ToDoListActivity.SHIPMENT_TYPE, GlobalConstant.ShipmentTypeConstants.RTS);
            intent.putExtra(ToDoListActivity.SHIPMENT_STATUS, shipment_status);
            finish();
            startActivity(intent);
            applyTransitionToBackFromActivity(this);
        } catch (Exception e){
            showSnackbar(e.getMessage());
        }
    }

    public void showFeedbackDialog(String feedbackMessage) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.csat_img_layout);
        TextView feedbackView = dialog.findViewById(R.id.feedbackView);
        feedbackView.setTextColor(ContextCompat.getColor(this, R.color.rts));
        feedbackView.setText(feedbackMessage);
        ImageView imgCross = dialog.findViewById(R.id.img_cross);
        imgCross.setOnClickListener(view -> {
            dialog.dismiss();
            cancelHandler();
        });
        Objects.requireNonNull(dialog.getWindow()).setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.show();
        handler = new Handler();
        handler.postDelayed(() -> {
            dialog.dismiss();
            openTodoActivity();
        }, 4000);
    }

    private void cancelHandler() {
        if(handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}