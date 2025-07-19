package in.ecomexpress.sathi.ui.drs.forward.success;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityFwdSuccessScreenBinding;
import in.ecomexpress.sathi.repo.local.data.activitiesdata.FWDActivitiesData;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.drs.forward.shipmentearndialog.ShipmentEarnDialog;
import in.ecomexpress.sathi.ui.drs.todolist.ToDoListActivity;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class FWDSuccessActivity extends BaseActivity<ActivityFwdSuccessScreenBinding, FWDSuccessViewModel> implements IFWDSuccessNavigator {

    String TAG = FWDSuccessActivity.class.getSimpleName();
    @Inject
    FWDSuccessViewModel fwdSuccessViewModel;
    ActivityFwdSuccessScreenBinding activitySuccessScreenBinding;
    @Inject
    ForwardCommit forwardCommit;
    String awbNo, reason;
    String feedbackTextMessage;
    String decideNext;
    String composite_key;
    int shipment_status = 0;

    private Handler handler;
    private boolean fwd_success = false;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, FWDSuccessActivity.class);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fwdSuccessViewModel.setNavigator(this);
        activitySuccessScreenBinding = getViewDataBinding();
        logScreenNameInGoogleAnalytics(TAG, this);

        FWDActivitiesData fwdActivitiesData = getIntent().getParcelableExtra("fwdActivitiesData");
        if (fwdActivitiesData != null) {
            try {
                awbNo = String.valueOf(fwdActivitiesData.getAwbNo());
                decideNext = fwdActivitiesData.getDecideNext();
                composite_key = fwdActivitiesData.getCompositeKey();
                reason = fwdActivitiesData.getReason();
            } catch (Exception e) {
                Logger.e("FwdSuccessActivity", e.getMessage());

            }
        }

        try {
            if (decideNext.equals(Constants.UNDELIVERED)) {
                activitySuccessScreenBinding.reasonText.setVisibility(View.VISIBLE);
                activitySuccessScreenBinding.reasonText.setText(reason);
                shipment_status = Constants.SHIPMENT_UNDELIVERED_STATUS;
                activitySuccessScreenBinding.decideText.setText(Constants.UNDELIVER);
                fwdSuccessViewModel.setImage(false);
                fwdSuccessViewModel.setTextColor(false);
                activitySuccessScreenBinding.statusText.setText("Shipment Undelivered");
                getViewModel().writeEvent(System.currentTimeMillis(), " FWD Shipment undelivered: AWB No-" + awbNo);
            } else {
                activitySuccessScreenBinding.reasonText.setVisibility(View.GONE);
                Constants.shipment_undelivered_count = 0;
                shipment_status = Constants.SHIPMENT_DELIVERED_STATUS;
                activitySuccessScreenBinding.decideText.setText(Constants.DELIVER);
                fwdSuccessViewModel.setImage(true);
                fwdSuccessViewModel.setTextColor(true);
                getViewModel().writeEvent(System.currentTimeMillis(), " FWD Shipment delivered: AWB No-" + awbNo);
                activitySuccessScreenBinding.statusText.setText(getString(R.string.now_you_can_handover_this_shipment_from_customer));
                fwd_success = true;
                if (!fwdSuccessViewModel.getDataManager().getFeedbackMessage().equalsIgnoreCase("")) {
                    feedbackTextMessage = CommonUtils.randomStringSelector(fwdSuccessViewModel.getDataManager().getFeedbackMessage());
                    showFeedbackDialog(feedbackTextMessage);
                }
            }
            fwdSuccessViewModel.getShipmentData(forwardCommit, composite_key);
            Log.d(TAG, "onCreate: " + forwardCommit.getAwb());
            activitySuccessScreenBinding.awb.setText("AWB NO:- " + awbNo);
            activitySuccessScreenBinding.consigneeAddress.setMovementMethod(new ScrollingMovementMethod());
            activitySuccessScreenBinding.consigneeName.setMovementMethod(new ScrollingMovementMethod());
            if ((fwdSuccessViewModel.getDataManager().getEnableDPEmployee() && fwd_success && fwdSuccessViewModel.getDataManager().getIsAdmEmp())) {
                fwdSuccessViewModel.fetchForwardShipment(awbNo);
            }
        } catch (Exception e) {
            Logger.e(FWDSuccessActivity.class.getName(), e.getMessage());
            showSnackbar(e.getMessage());
        }

        activitySuccessScreenBinding.header.awb.setText(R.string.shipment_status_txt);
        activitySuccessScreenBinding.header.backArrow.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public FWDSuccessViewModel getViewModel() {
        return fwdSuccessViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_fwd_success_screen;
    }

    private void openTodoActivity() {
        try {
            Intent intent = ToDoListActivity.getStartIntent(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtra(ToDoListActivity.ITEM_MARKED, awbNo);
            intent.putExtra(ToDoListActivity.SHIPMENT_TYPE, GlobalConstant.ShipmentTypeConstants.FWD);
            intent.putExtra(ToDoListActivity.SHIPMENT_STATUS, shipment_status);
            startActivity(intent);
            finish();
            applyTransitionToOpenActivity(this);
        } catch (Exception e) {
            Logger.e(FWDSuccessActivity.class.getName(), e.getMessage());
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void onhomeclick() {
        openTodoActivity();
    }

    @Override
    public void onError(String e) {
        showSnackbar(e);
    }

    @Override
    public void showEarnedDialog(DRSForwardTypeResponse drsForwardTypeResponse) {
        if ((fwdSuccessViewModel.getDataManager().getPPDPrice() > 0 && drsForwardTypeResponse.getShipmentDetails().getType().equalsIgnoreCase("ppd")) || (fwdSuccessViewModel.getDataManager().getCODPrice() > 0 && drsForwardTypeResponse.getShipmentDetails().getType().equalsIgnoreCase("cod"))) {
            show_shipmentEarnDialog(String.valueOf(drsForwardTypeResponse.getAwbNo()), drsForwardTypeResponse.getShipmentDetails().getType());
        }
    }

    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState: ");
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showSnackbar(getString(R.string.cannot_go_back_Rvp));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    /**
     * @param awb--awb       no.
     * @param type--shipment type
     */
    private void show_shipmentEarnDialog(String awb, String type) {
        ShipmentEarnDialog shipmentEarnDialog = ShipmentEarnDialog.newInstance(FWDSuccessActivity.this, awb, type);
        shipmentEarnDialog.show(getSupportFragmentManager());
        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                try {
                    shipmentEarnDialog.dismiss();
                } catch (Exception e) {
                    Logger.e(FWDSuccessActivity.class.getName(), e.getMessage());
                }
            }
        }.start();
    }


    public void showFeedbackDialog(String feedbackMessage) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.csat_img_layout);
        TextView feedbackView = dialog.findViewById(R.id.feedbackView);
        feedbackView.setTextColor(ContextCompat.getColor(this, R.color.fwd));
        feedbackView.setText(feedbackMessage);

        // Cancel the feedback dialog when the cross icon is pressed:-
        ImageView imgCross = dialog.findViewById(R.id.img_cross);
        imgCross.setOnClickListener(view -> {
            dialog.dismiss();
            cancelHandler();
        });

        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.show();
        handler = new Handler();
        handler.postDelayed(() -> {
            dialog.dismiss();
            onhomeclick();
        }, 4000);
    }

    private void cancelHandler() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
