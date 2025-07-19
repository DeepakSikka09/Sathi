package in.ecomexpress.sathi.ui.dummy.eds.eds_success_fail;

import static in.ecomexpress.sathi.utils.Constants.IS_CASH_COLLECTION_ENABLE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityEdsSuccessFailBinding;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.drs.forward.shipmentearndialog.ShipmentEarnDialog;
import in.ecomexpress.sathi.ui.drs.todolist.ToDoListActivity;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.GlobalConstant;

@AndroidEntryPoint
public class EDSSuccessFailActivity extends BaseActivity<ActivityEdsSuccessFailBinding, EDSSuccessFailViewModel> implements IEDSSuccessFailNavigator {
    String TAG = EDSSuccessFailActivity.class.getCanonicalName();
    @Inject
    EDSSuccessFailViewModel EDSSuccessFailViewModel;
    ActivityEdsSuccessFailBinding activitySuccessScreenBinding;

    Long awb = null;
    private Handler handler;
    int shipment_status = 0;
    String reason;
    EDSResponse edsResponseCommit;
    private boolean eds_success = false;
    String feedbackTextMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EDSSuccessFailViewModel.setNavigator(this);
        activitySuccessScreenBinding = getViewDataBinding();
        String decideNext = getIntent().getExtras().getString(Constants.DECIDENEXT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.eds));
        }
        try {
            if (decideNext.equals(Constants.UNDELIVERED)) {
                // Constants.shipment_undelivered_count++;
                reason = getIntent().getStringExtra("Reason");
                activitySuccessScreenBinding.reasonText.setVisibility(View.VISIBLE);
                activitySuccessScreenBinding.reasonText.setText(reason);
                shipment_status = Constants.SHIPMENT_UNDELIVERED_STATUS;
                activitySuccessScreenBinding.decideText.setText(Constants.FAIL);
                EDSSuccessFailViewModel.setImage(false);
                EDSSuccessFailViewModel.setTextColor(false);
                activitySuccessScreenBinding.statusText.setText("Shipment Undelivered");
            } else {
                activitySuccessScreenBinding.reasonText.setVisibility(View.GONE);
                Constants.shipment_undelivered_count = 0;
                shipment_status = Constants.SHIPMENT_DELIVERED_STATUS;
                activitySuccessScreenBinding.decideText.setText(Constants.SUCCESS);
                EDSSuccessFailViewModel.setImage(true);
                EDSSuccessFailViewModel.setTextColor(true);
                activitySuccessScreenBinding.statusText.setText(getString(R.string.now_you_can_recieve_this_shipment_from_customer));
                eds_success = true;
                if (!EDSSuccessFailViewModel.getDataManager().getFeedbackMessage().equalsIgnoreCase("")) {
                    feedbackTextMessage = CommonUtils.randomStringSelector(EDSSuccessFailViewModel.getDataManager().getFeedbackMessage());
                    showFeedbackDialog(feedbackTextMessage);
                }
            }
            awb = getIntent().getExtras().getLong(Constants.INTENT_KEY, 0);
            activitySuccessScreenBinding.awb.setText("AWB NO:- " + String.valueOf(awb));

            edsResponseCommit = getIntent().getParcelableExtra("edsResponseCommit");
            EDSSuccessFailViewModel.setContent(edsResponseCommit);

            if (EDSSuccessFailViewModel.getDataManager().getDuplicateCashReceipt().equalsIgnoreCase("true") && IS_CASH_COLLECTION_ENABLE) {
                activitySuccessScreenBinding.lltFooter.setVisibility(View.GONE);
                activitySuccessScreenBinding.lltFooterCashReceipt.setVisibility(View.VISIBLE);

            } else {
                activitySuccessScreenBinding.lltFooter.setVisibility(View.VISIBLE);
                activitySuccessScreenBinding.lltFooterCashReceipt.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
        Log.d(TAG, "onCreate: ");
        try {

            activitySuccessScreenBinding.consigneeAddress.setMovementMethod(new ScrollingMovementMethod());
            activitySuccessScreenBinding.consigneeName.setMovementMethod(new ScrollingMovementMethod());
            activitySuccessScreenBinding.consigneeAddress.setText(CommonUtils.nullToEmpty(edsResponseCommit.getConsigneeDetail().getAddress().getLine1()) + " "
                    + CommonUtils.nullToEmpty(edsResponseCommit.getConsigneeDetail().getAddress().getLine2()) + " "
                    + CommonUtils.nullToEmpty(edsResponseCommit.getConsigneeDetail().getAddress().getLine3()) + " "
                    + CommonUtils.nullToEmpty(edsResponseCommit.getConsigneeDetail().getAddress().getLine4()) + " "
                    + CommonUtils.nullToEmpty(edsResponseCommit.getConsigneeDetail().getAddress().getCity()) + " "
                    + edsResponseCommit.getConsigneeDetail().getAddress().getPincode());
            activitySuccessScreenBinding.consigneeName.setText(edsResponseCommit.getConsigneeDetail().getName());
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        if (EDSSuccessFailViewModel.getDataManager().getEnableDPEmployee() && eds_success && EDSSuccessFailViewModel.getDataManager().getEDSPrice() > 0 && EDSSuccessFailViewModel.getDataManager().getIsAdmEmp()) {
            show_shipmentEarnDialog(String.valueOf(awb));
        }
    }

    private void show_shipmentEarnDialog(String awb) {
        ShipmentEarnDialog shipmentEarnDialog = ShipmentEarnDialog.newInstance(EDSSuccessFailActivity.this, awb, "EDS");
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

    public static Intent getStartIntent(Context context) {
        return new Intent(context, EDSSuccessFailActivity.class);
    }

    @Override
    public EDSSuccessFailViewModel getViewModel() {
        return EDSSuccessFailViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_eds_success_fail;
    }


    private void openTodoActivity() {
        try {
            Constants.BACKTODOLIST = true;
            Intent intent = ToDoListActivity.getStartIntent(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtra(ToDoListActivity.ITEM_MARKED, String.valueOf(edsResponseCommit.getAwbNo()));
            intent.putExtra(Constants.DRS_ID, String.valueOf(Constants.TEMP_DRSID));
            intent.putExtra(ToDoListActivity.SHIPMENT_TYPE, GlobalConstant.ShipmentTypeConstants.EDS);
            intent.putExtra(ToDoListActivity.SHIPMENT_STATUS, shipment_status);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }

    }

    @Override
    public void onhomeclick() {
        openTodoActivity();
    }

    @Override
    public void showError(boolean b) {
        if (b) {
            showSnackbar("Receipt send successfully");
        } else {
            showSnackbar("Server error, try again");
        }

    }

    @Override
    public void onHandleError(String msg) {
        showSnackbar(msg);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState: ");

    }

    @Override
    public void onBackPressed() {
        showSnackbar(getString(R.string.cannot_go_back_Rvp));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }


    public String getAwbNumber() {
        return String.valueOf(awb);
    }


    public void showFeedbackDialog(String feedbackMessage) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.csat_img_layout);
        TextView feedbackView = dialog.findViewById(R.id.feedbackView);
        feedbackView.setTextColor(ContextCompat.getColor(this, R.color.eds));
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
