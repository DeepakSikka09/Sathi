package in.ecomexpress.sathi.ui.drs.forward.obd.activity;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.Constants.forward_call_count;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityFwdObdOdrCompBinding;
import in.ecomexpress.sathi.repo.local.data.activitiesdata.FWDActivitiesData;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.drs.forward.obd.navigator.IFwdOBDCompleteNavigator;
import in.ecomexpress.sathi.ui.drs.forward.obd.viewmodel.FwdOBDCompleteViewModel;
import in.ecomexpress.sathi.ui.drs.todolist.ToDoListActivity;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.Helper;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.NetworkUtils;
import io.reactivex.disposables.CompositeDisposable;

@AndroidEntryPoint
public class FwdOBDCompleteActivity extends BaseActivity<ActivityFwdObdOdrCompBinding, FwdOBDCompleteViewModel> implements IFwdOBDCompleteNavigator {

    private final String TAG = FwdOBDCompleteActivity.class.getSimpleName();
    private String consigneeName = "";
    private String OBD_REFUSED = "No";
    ForwardCommit forwardCommit;
    @Inject
    FwdOBDCompleteViewModel fwdOBDCompleteViewModel;
    String shipmentType;
    String composite_key = "";
    int isCall;
    String dateSet = "";
    String order_id = "";
    String awb;
    boolean call_allowed;
    String consignee_mobile, consignee_alternate_mobile = "";
    String OFD_OTP;
    String drs_id_num;
    String collectedValue = "0";
    String return_package_barcode = "";
    private ActivityFwdObdOdrCompBinding binding;
    private int meterRange;
    private boolean consigneeProfiling = false;
    private long mLastClickTime = 0;
    public ColorStateList listBlue;
    public ColorStateList listGrey;
    boolean shipmentCommitFailed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fwdOBDCompleteViewModel.setNavigator(this);
        logScreenNameInGoogleAnalytics(TAG, this);
        binding = getViewDataBinding();
        fwdOBDCompleteViewModel.getDataManager().setLoginPermission(false);
        setButtonColor();
        setQcNextButtonDisable();
        shipmentCommitFailed = false;
        try {
            forwardCommit = getIntent().getParcelableExtra("data");
            if (getIntent().getStringExtra(Constants.OBD_CONSIGNEE_NAME) != null) {
                consigneeName = getIntent().getStringExtra(Constants.OBD_CONSIGNEE_NAME);
            }
            if (getIntent().getStringExtra(Constants.OBD_REFUSED) != null) {
                OBD_REFUSED = getIntent().getStringExtra(Constants.OBD_REFUSED);
            }

            FWDActivitiesData fwdActivitiesData = getIntent().getParcelableExtra("fwdActivitiesData");

            if (fwdActivitiesData != null) {
                try {
                    awb = String.valueOf(fwdActivitiesData.getAwbNo());
                    order_id = fwdActivitiesData.getOrderId();
                    drs_id_num = String.valueOf(fwdActivitiesData.getDrsId());
                    shipmentType = fwdActivitiesData.getShipment_type();
                    composite_key = fwdActivitiesData.getCompositeKey();
                    collectedValue = fwdActivitiesData.getCollected_value();
                    call_allowed = fwdActivitiesData.isCallAllowed();
                    consignee_mobile = fwdActivitiesData.getConsignee_mobile();
                    consignee_alternate_mobile = fwdActivitiesData.getConsignee_alternate_number();
                    return_package_barcode = fwdActivitiesData.getReturn_package_barcode();
                    if (fwdActivitiesData.getOfd_otp() == null) {
                        OFD_OTP = "";
                    } else {
                        OFD_OTP = fwdActivitiesData.getOfd_otp();
                    }
                    binding.awb.awb.setText("AWB: " + awb);
                } catch (Exception e) {
                    Logger.e("UndeliveredActivity", e.getMessage());
                }
            }
            binding.awb.awb.setText("AWB: " + awb);
            forwardCommit.setAwb(awb);
            Constants.LOCATION_ACCURACY = fwdOBDCompleteViewModel.getDataManager().getUndeliverConsigneeRANGE();
            fwdOBDCompleteViewModel.onForwardDRSCommit(forwardCommit);
            fwdOBDCompleteViewModel.getCallStatus(Long.parseLong(awb), Integer.parseInt(drs_id_num));
            fwdOBDCompleteViewModel.getConsigneeProfiling();
        } catch (Exception e) {
            showError(e.getMessage() + "OnCreate");
            e.printStackTrace();
            Logger.e(FwdOBDCompleteActivity.class.getName(), e.getMessage());
        }
        setBackPressed();
    }

    public void setButtonColor() {
        listBlue = ColorStateList.valueOf(getResources().getColor(R.color.ecomBlue));
        listGrey = ColorStateList.valueOf(getResources().getColor(R.color.gray_ecom));
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            fwdOBDCompleteViewModel.onSubmitClick();
        } catch (Exception e) {
            Logger.e(FwdOBDCompleteActivity.class.getName(), e.getMessage());
        }
    }

    @Override
    public FwdOBDCompleteViewModel getViewModel() {
        return fwdOBDCompleteViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_fwd_obd_odr_comp;
    }

    private void setBackPressed() {
        binding.awb.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FwdOBDCompleteActivity.this, ToDoListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra(ToDoListActivity.ITEM_MARKED, awb);
                intent.putExtra(ToDoListActivity.SHIPMENT_TYPE, GlobalConstant.ShipmentTypeConstants.FWD);
                intent.putExtra(ToDoListActivity.SHIPMENT_STATUS, Constants.SHIPMENT_UNDELIVERED_STATUS);
                startActivity(intent);
                finish();
                applyTransitionToBackFromActivity(FwdOBDCompleteActivity.this);
            }
        });

        binding.btnVerify.setOnClickListener(v -> {
            if (shipmentCommitFailed) {
                fwdOBDCompleteViewModel.onSubmitClick();
            } else {
                Intent intent = new Intent(FwdOBDCompleteActivity.this, ToDoListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra(ToDoListActivity.ITEM_MARKED, awb);
                intent.putExtra(ToDoListActivity.SHIPMENT_TYPE, GlobalConstant.ShipmentTypeConstants.FWD);
                intent.putExtra(ToDoListActivity.SHIPMENT_STATUS, Constants.SHIPMENT_UNDELIVERED_STATUS);
                startActivity(intent);
                finish();
                applyTransitionToBackFromActivity(this);
            }
        });
    }


    @Override
    public void onSubmitSuccess(ForwardCommit forwardCommit) {
        OpenFailScreen(forwardCommit);
    }

    @Override
    public void OpenFailScreen(ForwardCommit forwardCommit) {
        Constants.rCheduleFlag = false;
        Helper.updateLocationWithData(FwdOBDCompleteActivity.this, forwardCommit.getAwb(), forwardCommit.getStatus());
        navigateToNextActivity();
    }

    @Override
    public void OnSubmitClick() {
        forward_call_count = 0;
        boolean failFlag = false;
        fwdOBDCompleteViewModel.call_alert_number = 0;
        fwdOBDCompleteViewModel.isCallRecursionDialogRunning = true;
        fwdOBDCompleteViewModel.isStopRecursion = false;
        forwardCommit.setUd_otp("NONE");
        forwardCommit.setRd_otp("NONE");
        if (OBD_REFUSED.equalsIgnoreCase("Yes")) {
            forwardCommit.setAttempt_reason_code(fwdOBDCompleteViewModel.getDataManager().getOBDREFUSED());
        } else if (OBD_REFUSED.equalsIgnoreCase("OtpNotAvailable")) {
            forwardCommit.setAttempt_reason_code("24204");
        } else {
            forwardCommit.setAttempt_reason_code(fwdOBDCompleteViewModel.getDataManager().getOBDQCFAIL());
        }
        forwardCommit.setIs_obd(true);
        forwardCommit.setQc_item(forwardCommit.getQc_item());
        forwardCommit.setCall_attempt_count(fwdOBDCompleteViewModel.getDataManager().getForwardCallCount(awb + "FWD"));
        forwardCommit.setMap_activity_count(fwdOBDCompleteViewModel.getDataManager().getForwardMapCount(Long.parseLong(awb)));
        forwardCommit.setChange_received_confirmation("Y");
        forwardCommit.setCollectable_value(collectedValue);
        forwardCommit.setOfd_customer_otp(Constants.PLAIN_OTP);
        forwardCommit.setOfd_otp_verified(String.valueOf(Constants.OFD_OTP_VERIFIED));
        forwardCommit.setScanable_by("");
        forwardCommit.setConsignee_name(consigneeName);
        forwardCommit.setReceived_by_name(consigneeName);
        forwardCommit.setReceived_by_relation("Self");
        if (return_package_barcode == null) {
            forwardCommit.setReturn_packaging_barcode("");
        } else {
            forwardCommit.setReturn_packaging_barcode(return_package_barcode);
        }
        fwdOBDCompleteViewModel.getRemarkCount(Long.parseLong(forwardCommit.getAwb()));
        forwardCommit.setPayment_id(null);
        forwardCommit.setPayment_mode(null);
        forwardCommit.setFe_comments("");
        if (fwdOBDCompleteViewModel.getDataManager().getofflineFwd()) {
            undelivered(failFlag);
        } else if (fwdOBDCompleteViewModel.getDataManager().isCounterDelivery() && fwdOBDCompleteViewModel.getCounterDeliveryRange() < fwdOBDCompleteViewModel.getDataManager().getDCRANGE()) {
            undelivered(failFlag);
        } else if (fwdOBDCompleteViewModel.getDataManager().getDcUndeliverStatus()) {
            if (fwdOBDCompleteViewModel.getCounterDeliveryRange() < fwdOBDCompleteViewModel.getDataManager().getDCRANGE()) {
                showError(getString(R.string.shipment_cannot_be_marked_undelivered_within_the_dc));
            } else {
                markDeliverOrFail(failFlag);
            }
        } else {
            markDeliverOrFail(failFlag);
        }
    }

    private void updateSyncStatusInDRSFWDTable(String composite_key, boolean i) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(fwdOBDCompleteViewModel.getDataManager().updateSameDayReassignSyncStatusFWD(composite_key, i).subscribeOn(fwdOBDCompleteViewModel.getSchedulerProvider().io()).observeOn(fwdOBDCompleteViewModel.getSchedulerProvider().io()).subscribe(aBoolean -> {
            try {
                CompositeDisposable compositeDisposable1 = new CompositeDisposable();
                compositeDisposable1.add(fwdOBDCompleteViewModel.getDataManager().updateForwardStatus(composite_key, 0).subscribeOn(fwdOBDCompleteViewModel.getSchedulerProvider().io()).observeOn(fwdOBDCompleteViewModel.getSchedulerProvider().io()).subscribe(aBoolean1 -> {
                }));
            } catch (Exception e) {
                Logger.e(FwdOBDCompleteActivity.class.getName(), e.getMessage());
            }
        }, throwable -> {
        }));
    }

    @SuppressLint("SimpleDateFormat")
    void markDeliverOrFail(boolean failFlag) {
        if (Constants.SAME_DAY_REASSIGN_VERIFIED.equalsIgnoreCase("VERIFIED")) {
            updateSyncStatusInDRSFWDTable(composite_key, false);
        }
        int current_time = Integer.parseInt(new SimpleDateFormat("HH").format(new Date()));
        if (Constants.CONSIGNEE_PROFILE && meterRange < fwdOBDCompleteViewModel.getDataManager().getUndeliverConsigneeRANGE()) {
            undelivered(failFlag);
        } else if (fwdOBDCompleteViewModel.FEInDCZone(fwdOBDCompleteViewModel.getDataManager().getCurrentLatitude(), fwdOBDCompleteViewModel.getDataManager().getCurrentLongitude(), fwdOBDCompleteViewModel.getDataManager().getDCLatitude(), fwdOBDCompleteViewModel.getDataManager().getDCLongitude())) {
            if (!fwdOBDCompleteViewModel.getDataManager().getDirectUndeliver()) {
                undelivered(failFlag);
            } else {
                if (call_allowed) {
                    fwdOBDCompleteViewModel.callApi(failFlag, forwardCommit.getAwb(), forwardCommit.getDrs_id());
                } else {
                    undelivered(failFlag);
                }
            }
        } else if (current_time >= 21 || current_time <= 6) {
            if (!fwdOBDCompleteViewModel.getDataManager().getDirectUndeliver()) {
                undelivered(failFlag);
            } else {
                if (!call_allowed) {
                    fwdOBDCompleteViewModel.callApi(failFlag, forwardCommit.getAwb(), forwardCommit.getDrs_id());
                } else {
                    undelivered(failFlag);
                }
            }
        } else if ((TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis()) - TimeUnit.MILLISECONDS.toMinutes(fwdOBDCompleteViewModel.getDataManager().getDRSTimeStamp())) <= 7/*fwdOBDCompleteViewModel.getDateCurrentTimeZone(fwdOBDCompleteViewModel.getCurrentTime())-fwdOBDCompleteViewModel.getDateCurrentTimeZone(fwdOBDCompleteViewModel.getDataManager().getDRSTimeStamp())<=7*/) {
            if (!fwdOBDCompleteViewModel.getDataManager().getDirectUndeliver()) {
                undelivered(failFlag);
            } else {
                if (call_allowed) {
                    fwdOBDCompleteViewModel.callApi(failFlag, forwardCommit.getAwb(), forwardCommit.getDrs_id());
                } else {
                    undelivered(failFlag);
                }
            }
        } else {
            undelivered(failFlag);
        }
    }

    private void undelivered(boolean failFlag) {
        try {
            Calendar calendar = Calendar.getInstance();
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
            int mMonth = calendar.get(Calendar.MONTH) + 1;
            if (fwdOBDCompleteViewModel.loginDate().equalsIgnoreCase(String.valueOf(mDay)) && fwdOBDCompleteViewModel.getDataManager().getLoginMonth() == mMonth) {
                if (!failFlag) {
                    String dialog_message = "Shipment will be mark as Undelivered";
                    String positiveButtonText = getString(R.string.ok);
                    if (Constants.CONSIGNEE_PROFILE && meterRange > fwdOBDCompleteViewModel.getDataManager().getUndeliverConsigneeRANGE() && fwdOBDCompleteViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("W")) {
                        dialog_message = "You are not attempting the shipment at Consignee’s location. Your current location = " + fwdOBDCompleteViewModel.getDataManager().getCurrentLatitude() + ", " + fwdOBDCompleteViewModel.getDataManager().getCurrentLongitude() + " You are " + meterRange + " meter away from consignee location. \nAre you sure you want to commit?";
                        positiveButtonText = getString(R.string.yes);
                    } else if (Constants.CONSIGNEE_PROFILE && meterRange > fwdOBDCompleteViewModel.getDataManager().getUndeliverConsigneeRANGE() && fwdOBDCompleteViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("R")) {
                        dialog_message = "You are not allowed to commit this shipment as you are not attempting at consignee location. your current location = " + fwdOBDCompleteViewModel.getDataManager().getCurrentLatitude() + ", " + fwdOBDCompleteViewModel.getDataManager().getCurrentLongitude() + " You are " + meterRange + " meter away from consignee location";
                        positiveButtonText = getString(R.string.ok);
                    } else if (Constants.CONSIGNEE_PROFILE && meterRange > fwdOBDCompleteViewModel.getDataManager().getUndeliverConsigneeRANGE()) {
                        if (NetworkUtils.isNetworkConnected(FwdOBDCompleteActivity.this)) {
                            fwdOBDCompleteViewModel.createCommitPacketCashCollection(forwardCommit, dateSet, composite_key);
                        } else {
                            fwdOBDCompleteViewModel.onUndeliveredApiCall(FwdOBDCompleteActivity.this, dateSet, composite_key);
                        }
                        return;
                    } else if (Constants.CONSIGNEE_PROFILE) {
                        if (NetworkUtils.isNetworkConnected(FwdOBDCompleteActivity.this)) {
                            fwdOBDCompleteViewModel.createCommitPacketCashCollection(forwardCommit, dateSet, composite_key);
                        } else {
                            fwdOBDCompleteViewModel.onUndeliveredApiCall(FwdOBDCompleteActivity.this, dateSet, composite_key);
                        }
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_MaterialComponents_Light_Dialog_Alert);
                    builder.setCancelable(false);
                    builder.setMessage(dialog_message);
                    forwardCommit.setLocation_verified(meterRange <= fwdOBDCompleteViewModel.getDataManager().getUndeliverConsigneeRANGE());
                    builder.setPositiveButton(positiveButtonText, (dialog, which) -> {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        if (fwdOBDCompleteViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("R") && meterRange > fwdOBDCompleteViewModel.getDataManager().getUndeliverConsigneeRANGE()) {
                            dialog.dismiss();
                        } else if (consigneeProfiling && meterRange > fwdOBDCompleteViewModel.getDataManager().getUndeliverConsigneeRANGE()) {
                            dialog.dismiss();
                        }

                        if (NetworkUtils.isNetworkConnected(FwdOBDCompleteActivity.this)) {
                            fwdOBDCompleteViewModel.createCommitPacketCashCollection(forwardCommit, dateSet, composite_key);
                        } else {
                            fwdOBDCompleteViewModel.onUndeliveredApiCall(FwdOBDCompleteActivity.this, dateSet, composite_key);
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    return;
                }

                if (NetworkUtils.isNetworkConnected(FwdOBDCompleteActivity.this)) {
                    fwdOBDCompleteViewModel.createCommitPacketCashCollection(forwardCommit, dateSet, composite_key);
                } else {
                    fwdOBDCompleteViewModel.onUndeliveredApiCall(FwdOBDCompleteActivity.this, dateSet, composite_key);
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this,android.R.style.Theme_Material_Light_Dialog);
                String AlertText1 = "Attention : ";
                builder.setMessage(AlertText1 + getString(R.string.commit_restriction_msg)).setCancelable(false).setPositiveButton("OK", (dialog, id) -> {
                    dialog.cancel();
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        } catch (Exception e) {
            Logger.e(FwdOBDCompleteActivity.class.getSimpleName() + "undelivered()", e.getMessage());
        }
    }

    @Override
    public void showError(String error) {
        showSnackbar(error);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void navigateToNextActivity() {
        setQcNextButtonEnable();
        binding.btnVerify.setText("Go To Next Shipment");
        binding.ivTick.setVisibility(View.VISIBLE);
        binding.tvOrderDelivered.setVisibility(View.VISIBLE);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void commitShipmentAgain() {
        binding.ivTick.setVisibility(View.INVISIBLE);
        binding.tvOrderDelivered.setVisibility(View.INVISIBLE);
        binding.btnVerify.setText("Try Again To Commit");
        setQcNextButtonEnable();
        shipmentCommitFailed = true;
    }

    public void setQcNextButtonEnable() {
        binding.btnVerify.setBackgroundTintList(listBlue);
        binding.btnVerify.setClickable(true);
    }

    public void setQcNextButtonDisable() {
        binding.btnVerify.setBackgroundTintList(listGrey);
        binding.btnVerify.setClickable(false);
    }

    @Override
    public void isCall(int isCallMade) {
        isCall = isCallMade;
    }

    @Override
    public void setConsigneeDistance(int meter) {
        this.meterRange = meter;
    }

    @Override
    public void setConsigneeProfiling(boolean enable) {
        this.consigneeProfiling = enable;
    }

    @Override
    public void undeliveredShipment(boolean failFlag, boolean callFlag) {
        undelivered(failFlag);
    }

    @Override
    public Activity getActivityContext() {
        return this;
    }

    @Override
    public void clearStack() {
        Intent intent = new Intent(FwdOBDCompleteActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        CommonUtils.showCustomSnackbar(binding.getRoot(), "You Can Not Move Back", this);
    }
}