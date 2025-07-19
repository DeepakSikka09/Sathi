package in.ecomexpress.sathi.ui.drs.rvp.success;

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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityRvpSuccessScreenBinding;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.drs.forward.shipmentearndialog.ShipmentEarnDialog;
import in.ecomexpress.sathi.ui.drs.todolist.ToDoListActivity;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class RVPSuccessActivity extends BaseActivity<ActivityRvpSuccessScreenBinding, RVPSuccessViewModel> implements IRVPSuccessNavigator {

    private final String TAG = RVPSuccessActivity.class.getSimpleName();
    @Inject
    RVPSuccessViewModel rvpSuccessViewModel;
    ActivityRvpSuccessScreenBinding activitySuccessScreenBinding;
    Long awb = null;
    private Handler handler;
    String ConsigneeName = null;
    String ConsigneeAddress = null;
    String ConsigneeItemName = null;
    int shipment_status = 0;
    String reason;
    private boolean rvp_success = false;
    String feedbackTextMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rvpSuccessViewModel.setNavigator(this);
        logScreenNameInGoogleAnalytics(TAG, this);
        activitySuccessScreenBinding = getViewDataBinding();
        String decideNext = Objects.requireNonNull(getIntent().getExtras()).getString(Constants.DECIDENEXT);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.rvp));
        awb = getIntent().getExtras().getLong("awb", 0);
        try {
            if (Objects.requireNonNull(decideNext).equals(Constants.UNDELIVERED)) {
                reason = getIntent().getStringExtra("Reason");
                activitySuccessScreenBinding.reasonText.setVisibility(View.VISIBLE);
                activitySuccessScreenBinding.reasonText.setText(reason);
                shipment_status = Constants.SHIPMENT_UNDELIVERED_STATUS;
                activitySuccessScreenBinding.decideText.setText(Constants.FAIL);
                rvpSuccessViewModel.setImage(false);
                rvpSuccessViewModel.setTextColor(false);
                activitySuccessScreenBinding.statusText.setText(R.string.pickup_failed);
                activitySuccessScreenBinding.comTv.setText("");
                getViewModel().writeEvent(Calendar.getInstance().getTimeInMillis(), " Shipment undelivered: AWB No-" + awb);
            } else {
                activitySuccessScreenBinding.reasonText.setVisibility(View.GONE);
                Dialog dialogForCsat = new Dialog(RVPSuccessActivity.this);
                dialogForCsat.setCancelable(false);
                Constants.shipment_undelivered_count = 0;
                shipment_status = Constants.SHIPMENT_DELIVERED_STATUS;
                activitySuccessScreenBinding.decideText.setText(Constants.SUCCESS);
                rvpSuccessViewModel.setImage(true);
                rvpSuccessViewModel.setTextColor(true);
                activitySuccessScreenBinding.comTv.setText("");
                activitySuccessScreenBinding.statusText.setText(R.string.shipment_picked);
                getViewModel().writeEvent(Calendar.getInstance().getTimeInMillis(), " Shipment undelivered: AWB No-" + awb);
                rvp_success = true;
                if (!rvpSuccessViewModel.getDataManager().getFeedbackMessage().equalsIgnoreCase("")) {
                    feedbackTextMessage = CommonUtils.randomStringSelector(rvpSuccessViewModel.getDataManager().getFeedbackMessage());
                    showFeedbackDialog(feedbackTextMessage);
                }
            }
            try {
                activitySuccessScreenBinding.awb.setText(String.format("AWB NO:- %s", awb));
            } catch (Exception e) {
                Logger.e(RVPSuccessActivity.class.getName(), e.getMessage());
            }
            ConsigneeName = getIntent().getExtras().getString("ConsigneeName");
            activitySuccessScreenBinding.consigneeName.setText(ConsigneeName);
            ConsigneeAddress = getIntent().getExtras().getString("ConsigneeAddress");
            activitySuccessScreenBinding.consigneeAddress.setText(ConsigneeAddress);
            ConsigneeItemName = getIntent().getExtras().getString("ConsigneeItemName");
            activitySuccessScreenBinding.type.setText(ConsigneeItemName);
            activitySuccessScreenBinding.consigneeAddress.setMovementMethod(new ScrollingMovementMethod());
            activitySuccessScreenBinding.consigneeName.setMovementMethod(new ScrollingMovementMethod());
            if ((rvpSuccessViewModel.getDataManager().getEnableDPEmployee() && rvp_success && rvpSuccessViewModel.getDataManager().getIsAdmEmp())) {
                rvpSuccessViewModel.fetchRVPShipment(String.valueOf(awb));
            }
        } catch (Exception e) {
            Logger.e(RVPSuccessActivity.class.getName(), e.getMessage());
            showSnackbar(e.getMessage());
        }
    }

    private void show_shipmentEarnDialog(String awb, String type) {
        ShipmentEarnDialog shipmentEarnDialog = ShipmentEarnDialog.newInstance(RVPSuccessActivity.this, awb, type);
        shipmentEarnDialog.show(getSupportFragmentManager());
        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                shipmentEarnDialog.dismiss();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    public static Intent getStartIntent(Context context) {
        return new Intent(context, RVPSuccessActivity.class);
    }

    @Override
    public RVPSuccessViewModel getViewModel() {
        return rvpSuccessViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_rvp_success_screen;
    }

    private void openTodoActivity() {
        try {
            Intent intent = ToDoListActivity.getStartIntent(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtra(ToDoListActivity.ITEM_MARKED, awb + "");
            intent.putExtra(ToDoListActivity.SHIPMENT_TYPE, GlobalConstant.ShipmentTypeConstants.RVP);
            intent.putExtra(ToDoListActivity.SHIPMENT_STATUS, shipment_status);
            finish();
            startActivity(intent);
            applyTransitionToOpenActivity(this);
        } catch (Exception e) {
            Logger.e(RVPSuccessActivity.class.getName(), e.getMessage());
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void onHomeClick() {
        openTodoActivity();
    }

    @Override
    public void onError(String e) {
        showSnackbar(e);
    }

    @Override
    public void showEarnedDialog(String shipmentType) {
        if ((rvpSuccessViewModel.getDataManager().getRQCPrice() > 0 && shipmentType.equalsIgnoreCase("rqc")) || (rvpSuccessViewModel.getDataManager().getRVPPrice() > 0 && shipmentType.equalsIgnoreCase("rvp"))) {
            show_shipmentEarnDialog(String.valueOf(awb), shipmentType);
        }
    }

    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showSnackbar(getString(R.string.cannot_go_back_Rvp));
    }

   
    public void showFeedbackDialog(String feedbackMessage) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.csat_img_layout);
        TextView feedbackView = dialog.findViewById(R.id.feedbackView);
        feedbackView.setTextColor(ContextCompat.getColor(this, R.color.rvp));
        feedbackView.setText(feedbackMessage);

        // Cancel the feedback dialog when the cross icon is pressed:-
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
            onHomeClick();
        }, 4000);
    }

    private void cancelHandler() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
