package in.ecomexpress.sathi.ui.drs.forward.details;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logButtonEventInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.Constants.CAMERA_SCANNER_CODE;
import static in.ecomexpress.sathi.utils.Constants.IMAGE_SCANNER_CODE;
import static in.ecomexpress.sathi.utils.Constants.PICK_FROM_CAMERA;
import static in.ecomexpress.sathi.utils.Constants.REQUEST_CODE_SCAN;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.barcodelistner.BarcodeResult;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityForwardDetailBinding;
import in.ecomexpress.sathi.repo.local.data.activitiesdata.FWDActivitiesData;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.drs.forward.disputeDailog.DisputeDialog;
import in.ecomexpress.sathi.ui.drs.forward.otherNumber.OtherNumberDialog;
import in.ecomexpress.sathi.ui.drs.forward.signature.SignatureActivity;
import in.ecomexpress.sathi.ui.drs.forward.undelivered_fwd.UndeliveredActivity;
import in.ecomexpress.sathi.ui.drs.rvp.awbscan.ScannerActivity;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.utils.PreferenceUtils;

@AndroidEntryPoint
public class ForwardDetailActivity extends BaseActivity<ActivityForwardDetailBinding, ForwardDetailViewModel> implements IForwardDetailNavigator, BarcodeResult {

    private final String TAG = ForwardDetailActivity.class.getSimpleName();
    @Inject
    ForwardDetailViewModel forwardDetailViewModel;
    OtherNumberDialog otherNumberDialog;
    @Inject
    ForwardCommit forwardCommit;
    ActivityForwardDetailBinding mActivityForwardDetailBinding;
    Double returnAmt, collectableAmount, collectableAmountWithDecimal;
    Double collectedAmount = 0.0;
    int roundOffAmount;

    boolean verifyFlag = false;
    String getDrsApiKey = "", getDrsPstnKey = "", getOrder_id = "", getDrsPin = "";
    String payType = "", compositeKey = "";
    String getCollectedValue = null;
    Boolean isDigitalPaymentAllowed = false;
    long awbNo;
    Boolean scannedStatus = false;
    String order_id = "";
    int drs_id_num = 0;
    boolean resend_secure_otp = false;
    ProgressDialog progress1;
    boolean counter_status = false;
    DisputeDialog disputeDialog;
    boolean call_allowed;
    String mobile_number;
    String shw_fwd_undelivered_btn = "No";
    boolean sign_image_required = false;
    String fwd_del_image;
    String consignee_alternate_mobile = "";
    String return_package_barcode = "";
    String ScanValue = "";
    private boolean is_amazon_schedule_enable = false;

    //for payphi
    private Handler handler = new Handler();
    private int timerSeconds = 5;
    private int clickCount = 0;
    private int maxAttempts = 10;

    private final TextWatcher enteredAmountWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @SuppressLint("DefaultLocale")
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                //Returning Amount Calculation
                if (!mActivityForwardDetailBinding.amount.getText().toString().equalsIgnoreCase("") && !mActivityForwardDetailBinding.enterAmt.getText().toString().equalsIgnoreCase("")) {
                    collectableAmount = Double.parseDouble(mActivityForwardDetailBinding.amount.getText().toString());
                    collectedAmount = Double.parseDouble(mActivityForwardDetailBinding.enterAmt.getText().toString());
                    if (collectedAmount >= collectableAmount && collectedAmount != null) {
                        returnAmt = collectedAmount - collectableAmount;

                        mActivityForwardDetailBinding.returningAmount.setText(String.format("%.2f", returnAmt));
                    }
                    if (collectedAmount > 200000) {
                        showSnackbar(getString(R.string.more_amount_entered));
                    }
                    if (mActivityForwardDetailBinding.enterAmt.getText().equals("")) {
                        mActivityForwardDetailBinding.returningAmount.setText("0.0");
                    }
                    if (collectedAmount <= collectableAmount && collectedAmount != null) {
                        mActivityForwardDetailBinding.returningAmount.setText("0.0");
                    }
                }
            } catch (Exception e) {
                Logger.e(ForwardDetailActivity.class.getName(), e.getMessage());

                showSnackbar(e.getMessage());
                collectedAmount = 0.0;
            }
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        public void afterTextChanged(Editable s) {
            //RoundOff Amount Calculation
            try {
                collectableAmountWithDecimal = Double.parseDouble("0" + collectableAmount);
                roundOffAmount = (CommonUtils.splitDecimal(collectableAmountWithDecimal) < 49) ? (0 - CommonUtils.splitDecimal(collectableAmountWithDecimal)) : (100 - CommonUtils.splitDecimal(collectableAmountWithDecimal));
                double collectableAmount, doubleFormOfIntCollectableAmount, valueAfterDecimal, fractionAmount;
                collectableAmount = collectableAmountWithDecimal;
                int integerPartOfCollectableAmount = (int) collectableAmount;
                doubleFormOfIntCollectableAmount = (double) integerPartOfCollectableAmount;
                valueAfterDecimal = collectableAmount - doubleFormOfIntCollectableAmount;
                fractionAmount = 1 - valueAfterDecimal;
                if (valueAfterDecimal > .50)
                    mActivityForwardDetailBinding.roundOffAmount.setText(String.format("%.2f", collectableAmountWithDecimal + fractionAmount));
                else if (valueAfterDecimal <= .50)
                    mActivityForwardDetailBinding.roundOffAmount.setText(String.format("%.2f", integerPartOfCollectableAmount));
                if (mActivityForwardDetailBinding.enterAmt.getText().equals("")) {
                    mActivityForwardDetailBinding.roundOffAmount.setText("0.00");
                }
            } catch (Exception e) {
                Logger.e(ForwardDetailActivity.class.getName(), e.getMessage());

            }
        }
    };

    public static Intent getStartIntent(Context context) {
        return new Intent(context, ForwardDetailActivity.class);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        forwardDetailViewModel.setNavigator(this);
        mActivityForwardDetailBinding = getViewDataBinding();
        mActivityForwardDetailBinding.nex.setVisibility(View.GONE);
        logScreenNameInGoogleAnalytics(TAG, this);
        try {

            FWDActivitiesData fwdActivitiesData = getIntent().getParcelableExtra("fwdActivitiesData");
            if (fwdActivitiesData != null) {
                try {
                    awbNo = fwdActivitiesData.getAwbNo();
                    order_id = fwdActivitiesData.getOrderId();
                    getOrder_id = fwdActivitiesData.getOrderId();
                    drs_id_num = fwdActivitiesData.getDrsId();
                    resend_secure_otp = fwdActivitiesData.isResend_otp_enable();
                    sign_image_required = fwdActivitiesData.isSign_image_required();
                    fwd_del_image = fwdActivitiesData.getFwd_del_image();
                    getDrsApiKey = fwdActivitiesData.getDrsApiKey();
                    getDrsPstnKey = fwdActivitiesData.getDrsPstnKey();
                    getDrsPin = fwdActivitiesData.getDrsPin();
                    mobile_number = fwdActivitiesData.getConsignee_mobile();
                    call_allowed = fwdActivitiesData.isCallAllowed();
                    is_amazon_schedule_enable = fwdActivitiesData.isIs_amazon_reschedule_enabled();
                    isDigitalPaymentAllowed = fwdActivitiesData.isCard();
                    compositeKey = fwdActivitiesData.getCompositeKey();
                    consignee_alternate_mobile = fwdActivitiesData.getConsignee_alternate_number();
                    if (fwdActivitiesData.getScanValue() == null) {
                        ScanValue = "";
                    } else {
                        ScanValue = fwdActivitiesData.getScanValue();
                    }
                    if (fwdActivitiesData.getReturn_package_barcode() == null) {
                        return_package_barcode = "";
                    } else {
                        return_package_barcode = fwdActivitiesData.getReturn_package_barcode();
                    }
                    if (fwdActivitiesData.getShow_fwd_undl_btn() != null) {
                        shw_fwd_undelivered_btn = fwdActivitiesData.getShow_fwd_undl_btn();
                        if (shw_fwd_undelivered_btn.equalsIgnoreCase("Yes")) {
                            mActivityForwardDetailBinding.btUndeliver.setVisibility(View.VISIBLE);
                        } else {
                            mActivityForwardDetailBinding.btUndeliver.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    Logger.e(ForwardDetailActivity.class.getName(), e.getMessage());
                }
            }
            forwardDetailViewModel.getIsDigitalPaymentFlag(isDigitalPaymentAllowed);
            forwardCommit.setAwb(String.valueOf(awbNo));

            //Fetching data for Clicked list item from awb number
            forwardDetailViewModel.getShipmentData(forwardCommit, compositeKey);
            mActivityForwardDetailBinding.consigneeAddress.setMovementMethod(new ScrollingMovementMethod());
            mActivityForwardDetailBinding.consigneeName.setMovementMethod(new ScrollingMovementMethod());
            mActivityForwardDetailBinding.enterAmt.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
            mActivityForwardDetailBinding.enterOtp.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
            mActivityForwardDetailBinding.enterAmt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});
            mActivityForwardDetailBinding.enterOtp.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});

            forwardDetailViewModel.getAllApiUrl(String.valueOf(awbNo));
            forwardDetailViewModel.getAllApiUrl(String.valueOf(awbNo));
            if (isDigitalPaymentAllowed) {
                //mActivityForwardDetailBinding.llPaymentOption.setWeightSum(.15f);
                mActivityForwardDetailBinding.llLinkpayment.setVisibility(View.VISIBLE);
            } else {
                //mActivityForwardDetailBinding.llPaymentOption.setWeightSum(.10f);
                mActivityForwardDetailBinding.llLinkpayment.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Logger.e(ForwardDetailActivity.class.getName(), e.getMessage());

            showSnackbar(e.getMessage());
        }

        mActivityForwardDetailBinding.header.awb.setText(R.string.fwd_detail);
        mActivityForwardDetailBinding.header.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBack();
            }
        });
    }

    @Override
    public ForwardDetailViewModel getViewModel() {
        return forwardDetailViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_forward_detail;
    }

    /*on click of undelivered button*/
    @Override
    public void onUndelivered(ForwardCommit forwardCommit) {
        try {

            FWDActivitiesData fwdActivitiesData = new FWDActivitiesData();
            fwdActivitiesData.setDrsPin(getDrsPin);
            fwdActivitiesData.setDrsApiKey(getDrsApiKey);
            fwdActivitiesData.setDrsPstnKey(getDrsPstnKey);
            fwdActivitiesData.setOrderId(order_id);
            fwdActivitiesData.setCallAllowed(call_allowed);
            fwdActivitiesData.setConsignee_mobile(mobile_number);
            fwdActivitiesData.setConsignee_alternate_number(consignee_alternate_mobile);
            fwdActivitiesData.setDrsId(drs_id_num);
            fwdActivitiesData.setAwbNo(Long.parseLong(forwardCommit.getAwb()));
            fwdActivitiesData.setCompositeKey(compositeKey);
            fwdActivitiesData.setIs_amazon_reschedule_enabled(is_amazon_schedule_enable);
            fwdActivitiesData.setShipment_type(mActivityForwardDetailBinding.type.getText().toString());
            fwdActivitiesData.setSecure_undelivered("false");
            fwdActivitiesData.setCollected_value(mActivityForwardDetailBinding.amount.getText().toString());

            Intent intent = UndeliveredActivity.getStartIntent(this);
            intent.putExtra("data", forwardCommit);
            intent.putExtra("fwdActivitiesData", fwdActivitiesData);
            startActivity(intent);
            applyTransitionToOpenActivity(this);

        } catch (Exception e) {
            Logger.e(ForwardDetailActivity.class.getName(), e.getMessage());

        }
    }

    @Override
    public void checkValidation() {
    }


    @Override
    public void dismissDialog() {
        otherNumberDialog.dismiss();
    }

    /*on click of next button*/
    @Override
    public void onNext(ForwardCommit forwardCommit1) {
        forwardCommit = forwardCommit1;
        if (mActivityForwardDetailBinding.layoutChild2Amount.getVisibility() == View.VISIBLE) {
            if (!mActivityForwardDetailBinding.enterAmt.getText().toString().trim().isEmpty()) {
                if (Double.parseDouble(mActivityForwardDetailBinding.enterAmt.getText().toString()) < Double.parseDouble(mActivityForwardDetailBinding.amount.getText().toString())) {
                    showSnackbar(getString(R.string.less_amount_entered));
                } else {
                    collectedAmount = Double.parseDouble(mActivityForwardDetailBinding.enterAmt.getText().toString());
                    if (collectedAmount > 200000) {
                        showSnackbar(getString(R.string.more_amount_entered));
                    } else {
                        forwardCommit.setCod_amount(mActivityForwardDetailBinding.amount.getText().toString());
                        payType = "cod";
                        forwardCommit.setPayment_mode("cash");
                        getCollectedValue = mActivityForwardDetailBinding.amount.getText().toString();
                        openSignatureActivity(payType, getCollectedValue);
                    }
                }
            } else {
                showSnackbar(getString(R.string.enter_input));
            }
        } else if (mActivityForwardDetailBinding.layoutChild2Otp.getVisibility() == View.VISIBLE && mActivityForwardDetailBinding.enterOtp.getText().toString() != null && mActivityForwardDetailBinding.type.getText().toString().equalsIgnoreCase(getString(R.string.ppd))) {
            if (verifyFlag) {
                payType = "ppd";
                forwardCommit.setPayment_mode("PPD");
                getCollectedValue = mActivityForwardDetailBinding.amount.getText().toString();
                openSignatureActivity(payType, getCollectedValue);
            } else {
                showSnackbar(getString(R.string.verify_otp));
            }
        } else if (mActivityForwardDetailBinding.nex.getVisibility() == View.VISIBLE) {
            payType = "ppd";
            forwardCommit.setPayment_mode("PPD");
            getCollectedValue = mActivityForwardDetailBinding.amount.getText().toString();
            openSignatureActivity(payType, getCollectedValue);
        }
    }

    public void onBackPressed() {
        if (!counter_status) {
            super.onBackPressed();
            applyTransitionToBackFromActivity(this);
        }
    }

    /*On click of back button*/

    public void onBack() {
        super.onBackPressed();
        applyTransitionToBackFromActivity(this);
    }

    /*on raise dispute*/
    @Override
    public void raiseDispute() {
        disputeDialog = DisputeDialog.newInstance(ForwardDetailActivity.this, String.valueOf(awbNo), String.valueOf(drs_id_num), forwardDetailViewModel);
        Window window = getWindow();
        disputeDialog.setCancelable(false);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        disputeDialog.show(getSupportFragmentManager());
    }


    @Override
    public void onNextAmountClick(ForwardCommit forwardCommit1) {
        forwardCommit = forwardCommit1;
        try {
            logButtonEventInGoogleAnalytics(TAG, getString(R.string.onnextamountclick), "Awb " + mActivityForwardDetailBinding.awb.getText().toString() + "Cod Amount" + mActivityForwardDetailBinding.amount.getText().toString(), this);
            forwardCommit.setCod_amount(mActivityForwardDetailBinding.amount.getText().toString());
            payType = "cod";
            forwardCommit.setPayment_mode("cash");
            getCollectedValue = mActivityForwardDetailBinding.amount.getText().toString();
            openSignatureActivity(payType, getCollectedValue);
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
    }

    /*Setting Otp layout_scan_popup Visible*/
    @Override
    public void onOTPLayoutVisibile() {
        mActivityForwardDetailBinding.nex.setVisibility(View.GONE);
        mActivityForwardDetailBinding.layoutChild2Amount.setVisibility(View.GONE);
        mActivityForwardDetailBinding.layoutChild2Otp.setVisibility(View.VISIBLE);
        mActivityForwardDetailBinding.layoutPayEcod.setVisibility(View.GONE);
    }

    /*Setting eCod layout_scan_popup visible*/
    @Override
    public void onPayeCODLayoutVisibile() {
        mActivityForwardDetailBinding.nex.setVisibility(View.GONE);
        mActivityForwardDetailBinding.layoutChild2Amount.setVisibility(View.GONE);
        mActivityForwardDetailBinding.layoutChild2Otp.setVisibility(View.GONE);
        mActivityForwardDetailBinding.layoutPayEcod.setVisibility(View.VISIBLE);
        mActivityForwardDetailBinding.layoutEcod.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCashPaymentSelected() {
        try {

            forwardDetailViewModel.onNextAmountClick();
        } catch (Exception e) {
            Logger.e(ForwardDetailActivity.class.getName(), e.getMessage());

            showSnackbar(e.getMessage());
        }
    }

    private void disableEnableControls(boolean enable, ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            child.setEnabled(enable);
            if (child instanceof ViewGroup) {
                disableEnableControls(enable, (ViewGroup) child);
            }
        }
    }

    @Override
    public void onMsgLinkSelected() {
        try {
            logButtonEventInGoogleAnalytics(TAG, getString(R.string.onmsglinkselectedfwd), "Awb " + awbNo, this);
            if (isNetworkConnected()) {
                if (forwardDetailViewModel.clicked_status.get() != null) {
                    if (!forwardDetailViewModel.clicked_status.get().booleanValue()) {
                        forwardDetailViewModel.registerAwbForMsgLink(String.valueOf(awbNo), this, String.valueOf(drs_id_num));
                    } else {
                        otherNumberDialog = OtherNumberDialog.newInstance(ForwardDetailActivity.this, String.valueOf(awbNo), String.valueOf(drs_id_num), resend_secure_otp);
                        otherNumberDialog.show(getSupportFragmentManager());
                    }
                } else {
                    forwardDetailViewModel.registerAwbForMsgLink(String.valueOf(awbNo), this, String.valueOf(drs_id_num));
                }
            } else {
                showSnackbar(getString(R.string.check_internet));
            }
        } catch (Exception e) {
            Logger.e(ForwardDetailActivity.class.getName(), e.getMessage());

            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void showNumberPopup() {
        try {
            otherNumberDialog = OtherNumberDialog.newInstance(ForwardDetailActivity.this, String.valueOf(awbNo), String.valueOf(drs_id_num), resend_secure_otp);
            otherNumberDialog.show(getSupportFragmentManager());
        } catch (Exception e) {
            Logger.e(ForwardDetailActivity.class.getName(), e.getMessage());

        }
    }

    @Override
    public void changeLinkText() {
        try {
            if (forwardDetailViewModel.clicked_status.get().booleanValue()) {
                mActivityForwardDetailBinding.txtvwMsgLink.setText(R.string.rsnd_link);
            } else {
                mActivityForwardDetailBinding.txtvwMsgLink.setText(R.string.msg_link);
            }
        } catch (Exception e) {
            Logger.e(ForwardDetailActivity.class.getName(), e.getMessage());

        }
    }

    @Override
    public Activity getContextProvider() {
        return this;
    }


    @Override
    public void onProceedtoCash() {
        mActivityForwardDetailBinding.layoutChild2Amount.setVisibility(View.VISIBLE);
        mActivityForwardDetailBinding.layoutChild2Otp.setVisibility(View.GONE);
        mActivityForwardDetailBinding.layoutPayEcod.setVisibility(View.GONE);
        mActivityForwardDetailBinding.enterAmt.addTextChangedListener(enteredAmountWatcher);
    }

    @Override
    public void showerrorMessage(String error) {
        try {
            if (error.contains("HTTP 500 "))
                showSnackbar(getString(R.string.http_500_msg));
            else if (error.equalsIgnoreCase("Invalid Authentication Token.")) {
                showSnackbar(error);
                forwardDetailViewModel.logoutLocal();
            } else
                showSnackbar(getString(R.string.server_down_msg));
        } catch (Exception e) {
            Logger.e(ForwardDetailActivity.class.getName(), e.getMessage());

            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void onerrorMsg(String message) {
        showSnackbar(message);
    }

    @Override
    public void setvisibility() {
        mActivityForwardDetailBinding.layoutChild2.setVisibility(View.INVISIBLE);
        showSnackbar("Improper Data found.Please contact server admin... ");
    }

    @Override
    public void mResultReceiver1(String strScancode) {
        if (strScancode != null) {
            strScancode = strScancode.replaceAll("\\s+", "");
            if (strScancode.equalsIgnoreCase(String.valueOf(awbNo)) || strScancode.equalsIgnoreCase(String.valueOf(order_id))) {
                scannedStatus = true;
                showToast("bar code matched");
                openSignatureActivity(payType, getCollectedValue);

            } else {
                scannedStatus = false;
                showToast("Please scan valid bar code of this manifest");
            }
        } else {
            showSnackbar(getString(R.string.scan_bar_code));
        }
    }

    /*
    @type-- type is payment type
    @amount-- the amount which have to pay
    */
    @Override
    public void openSignatureActivityDispute(String type, String amount) {
        payType = type;
        getCollectedValue = amount;

        FWDActivitiesData fwdActivitiesData = new FWDActivitiesData();
        fwdActivitiesData.setType(type);
        fwdActivitiesData.setAmount(amount);
        fwdActivitiesData.setChange(mActivityForwardDetailBinding.returningAmount.getText().toString());
        fwdActivitiesData.setSign_image_required(sign_image_required);
        fwdActivitiesData.setAwbNo(awbNo);
        fwdActivitiesData.setCompositeKey(compositeKey);
        fwdActivitiesData.setOrderId(order_id);
        fwdActivitiesData.setReturn_package_barcode(return_package_barcode);
        fwdActivitiesData.setScanValue(ScanValue);
        fwdActivitiesData.setFwd_del_image(fwd_del_image);
        Intent intent = SignatureActivity.getStartIntent(this);
        intent.putExtra("fwdActivitiesData", fwdActivitiesData);
        intent.putExtra("data", forwardCommit);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    @Override
    public void showDisputeButton() {
        raiseDispute();
    }

    @Override
    public void setCardClicked() {
        forwardDetailViewModel.getDataManager().setCardClicked(mActivityForwardDetailBinding.awb.getText().toString(), true);
    }

    @Override
    public boolean validateDigitalClick() {
        if (forwardDetailViewModel.getDataManager().getMessageClicked(mActivityForwardDetailBinding.awb.getText().toString())) {
            forwardCommit.setPayment_mode("link");
            Constants.PAYMENT_MODE = "link";
            return true;
        } else if (forwardDetailViewModel.getDataManager().getCardClicked(mActivityForwardDetailBinding.awb.getText().toString())) {
            forwardCommit.setPayment_mode("QR");
            Constants.PAYMENT_MODE = "QR";
            return true;
        } else {
            return false;
        }

    }


    @Override
    public void onEcodClick() {
        try {
            if (isNetworkConnected()) {
                //Calling api for check status
                forwardDetailViewModel.ServerCallToCheckStatus(mActivityForwardDetailBinding.awb.getText().toString(), ForwardDetailActivity.this, "status", String.valueOf(drs_id_num));
            } else {
                showSnackbar(getString(R.string.check_internet));
            }
        } catch (Exception e) {
            Logger.e(ForwardDetailActivity.class.getName(), e.getMessage());

            showSnackbar(e.getMessage());
        }
    }

    /*open signature activity method*/
    @Override
    public void openSignatureActivity(String type, String amount) {

        FWDActivitiesData fwdActivitiesData = new FWDActivitiesData();
        fwdActivitiesData.setType(type);
        fwdActivitiesData.setAmount(amount);
        fwdActivitiesData.setChange(mActivityForwardDetailBinding.returningAmount.getText().toString());
        fwdActivitiesData.setSign_image_required(sign_image_required);
        fwdActivitiesData.setAwbNo(awbNo);
        fwdActivitiesData.setCompositeKey(compositeKey);
        fwdActivitiesData.setOrderId(order_id);
        fwdActivitiesData.setReturn_package_barcode(return_package_barcode);
        fwdActivitiesData.setScanValue(ScanValue);
        fwdActivitiesData.setFwd_del_image(fwd_del_image);

        Intent intent = SignatureActivity.getStartIntent(this);
        intent.putExtra("fwdActivitiesData", fwdActivitiesData);
        intent.putExtra("data", forwardCommit);
        startActivity(intent);
        applyTransitionToOpenActivity(this);

    }

    @Override
    public void onOtpResendSuccess() {
        showSnackbar(getString(R.string.otp_resend_successfully));
    }

    @Override
    public void onOtpVerifySuccess(Boolean flag) {
        verifyFlag = flag;
        if (mActivityForwardDetailBinding.type.getText().toString().equalsIgnoreCase(getString(R.string.ppd))) {
            if (flag)
                payType = "ppd";
            getCollectedValue = mActivityForwardDetailBinding.amount.getText().toString();
            openSignatureActivity(payType, getCollectedValue);
        } else if (mActivityForwardDetailBinding.type.getText().toString().equalsIgnoreCase(getString(R.string.cod))) {
            onCashPaymentSelected();
        }
    }

    @Override
    public void onHandleError(String errorResponse) {
        showSnackbar(errorResponse);
        Log.d("errorResponse", errorResponse);
        try {
            if (errorResponse.equalsIgnoreCase("Invalid Authentication Token.")) {
                forwardDetailViewModel.logoutLocal();
            }
        } catch (Exception e) {
            Logger.e(ForwardDetailActivity.class.getName(), e.getMessage());

            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void showUndeliveredOption() {
        mActivityForwardDetailBinding.layoutChild2Amount.setVisibility(View.GONE);
        mActivityForwardDetailBinding.layoutChild2Otp.setVisibility(View.GONE);
        mActivityForwardDetailBinding.layoutPayEcod.setVisibility(View.GONE);
        mActivityForwardDetailBinding.nex.setVisibility(View.VISIBLE);
    }

    @Override
    public void getAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_Material3_Light_Dialog_Alert);
        builder.setCancelable(false);
        builder.setMessage("Max Attempt Failed.You are being navigated to Undelivered Page.");
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            forwardCommit.setPayment_mode("");
            FWDActivitiesData fwdActivitiesData = new FWDActivitiesData();
            fwdActivitiesData.setDrsPin(getDrsPin);
            fwdActivitiesData.setDrsApiKey(getDrsApiKey);
            fwdActivitiesData.setDrsPstnKey(getDrsPstnKey);
            fwdActivitiesData.setOrderId(order_id);
            fwdActivitiesData.setCallAllowed(call_allowed);
            fwdActivitiesData.setConsignee_mobile(mobile_number);
            fwdActivitiesData.setConsignee_alternate_number(consignee_alternate_mobile);
            fwdActivitiesData.setDrsId(drs_id_num);
            fwdActivitiesData.setAwbNo(Long.parseLong(forwardCommit.getAwb()));
            fwdActivitiesData.setCompositeKey(compositeKey);
            fwdActivitiesData.setIs_amazon_reschedule_enabled(is_amazon_schedule_enable);
            fwdActivitiesData.setShipment_type(mActivityForwardDetailBinding.type.getText().toString());
            fwdActivitiesData.setSecure_undelivered("false");
            fwdActivitiesData.setCollected_value(mActivityForwardDetailBinding.amount.getText().toString());

            Intent intent = UndeliveredActivity.getStartIntent(getApplicationContext());
            intent.putExtra(getString(R.string.data), forwardCommit);
            intent.putExtra("fwdActivitiesData", fwdActivitiesData);
            startActivity(intent);
            applyTransitionToOpenActivity(this);


        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void clearStack() {
        Intent intent = new Intent(ForwardDetailActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    /*Calling api on click of verify button of otp*/
    @Override
    public void onOtpVerifyButton() {
        hideKeyboard(ForwardDetailActivity.this);
        try {
            if (isNetworkConnected()) {
                if (!mActivityForwardDetailBinding.enterOtp.getText().toString().isEmpty() && mActivityForwardDetailBinding.enterOtp.getText().toString().length() > 3) {
                    forwardDetailViewModel.OnVerifyApiCall(ForwardDetailActivity.this, mActivityForwardDetailBinding.awb.getText().toString(), mActivityForwardDetailBinding.enterOtp.getText().toString(), Constants.VERIFY_OTP);
                } else if (mActivityForwardDetailBinding.enterOtp.getText().toString().isEmpty()) {
                    showSnackbar(getString(R.string.otp_blank));
                } else if (mActivityForwardDetailBinding.enterOtp.getText().toString().length() > 6) {
                    showSnackbar(getString(R.string.Invalid_otp_entered));
                } else {
                    showSnackbar(getString(R.string.Invalid_otp_entered));
                }
            } else {
                showSnackbar(getString(R.string.check_internet));
            }
        } catch (Exception e) {
            Logger.e(ForwardDetailActivity.class.getName(), e.getMessage());

            showSnackbar(e.getMessage());
        }
    }

    /*Calling api on click of resend button of otp*/
    @Override
    public void onOtpResendButton() {
        hideKeyboard(ForwardDetailActivity.this);
        new CountDownTimer(forwardDetailViewModel.getDataManager().getDisableResendOtpButtonDuration() * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mActivityForwardDetailBinding.resend.setEnabled(false);
                mActivityForwardDetailBinding.resend.setTextColor(getResources().getColor(R.color.light_gray));
                @SuppressLint("DefaultLocale") String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                System.out.println(hms);
                mActivityForwardDetailBinding.resend.setText(hms);
            }

            @Override
            public void onFinish() {
                mActivityForwardDetailBinding.resend.setEnabled(true);
                mActivityForwardDetailBinding.resend.setText(getResources().getString(R.string.resend));
                mActivityForwardDetailBinding.resend.setTextColor(getResources().getColor(R.color.colorPrimary));
                try {
                    if (isNetworkConnected()) {
                        forwardDetailViewModel.OnResendApiCall(ForwardDetailActivity.this, mActivityForwardDetailBinding.awb.getText().toString(), String.valueOf(drs_id_num));
                    } else {
                        showSnackbar(getString(R.string.check_internet));
                    }
                } catch (Exception e) {
                    Logger.e(ForwardDetailActivity.class.getName(), e.getMessage());

                    showSnackbar(e.getMessage());
                }
            }
        }.start();
    }

    /*calling payPhi sdk for card payment*/
    @Override
    public void onCardPaymentSelected() {
        maxAttempts = forwardDetailViewModel.getDataManager().getREQUEST_RESPONSE_COUNT();
        timerSeconds = forwardDetailViewModel.getDataManager().getREQUEST_RESPONSE_TIME() * 1000;

//        System.out.println("-----max"+forwardDetailViewModel.getDataManager().getREQUEST_RESPONSE_COUNT());
//        System.out.println("-----timer"+timerSeconds);
//        System.out.println("-----count"+clickCount);
//        System.out.println("-----perf"+PreferenceUtils.getSharedPreferences(ForwardDetailActivity.this).getInt(mActivityForwardDetailBinding.awb.getText().toString() + "count",0));

        if (PreferenceUtils.getSharedPreferences(ForwardDetailActivity.this).getInt(mActivityForwardDetailBinding.awb.getText().toString() + "count", 0) < maxAttempts) {
            clickCount++;

            mActivityForwardDetailBinding.payphi.setEnabled(false);
            mActivityForwardDetailBinding.card.setEnabled(false);
            mActivityForwardDetailBinding.paytext.setEnabled(false);

            PreferenceUtils.writePreferenceValue(ForwardDetailActivity.this, mActivityForwardDetailBinding.awb.getText().toString() + "count", clickCount);

            Toast.makeText(ForwardDetailActivity.this, "Request in Progress ", Toast.LENGTH_LONG).show();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mActivityForwardDetailBinding.payphi.setEnabled(true);
                    mActivityForwardDetailBinding.card.setEnabled(true);
                    mActivityForwardDetailBinding.paytext.setEnabled(true);
                }
            }, timerSeconds);

            try {
                if (isNetworkConnected()) {
                    logButtonEventInGoogleAnalytics(TAG, getString(R.string.oncardpaymentselectedfwd), "Awb " + mActivityForwardDetailBinding.awb.getText().toString(), this);
                    forwardDetailViewModel.getDataManager().setMessageClicked(mActivityForwardDetailBinding.awb.getText().toString(), false);
                    forwardDetailViewModel.getDataManager().setMessageCount(mActivityForwardDetailBinding.awb.getText().toString(), 0);
                    forwardDetailViewModel.InitialisePayphiSdk(ForwardDetailActivity.this, mActivityForwardDetailBinding.awb.getText().toString(), String.valueOf(drs_id_num));
                } else {
                    showSnackbar(getString(R.string.check_internet));
                }
            } catch (Exception e) {
                Logger.e(ForwardDetailActivity.class.getName(), e.getMessage());
                showSnackbar(e.getMessage());
            }

        } else {
            Toast.makeText(ForwardDetailActivity.this, "Maximum attempts reached. The button is now disabled.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        forwardDetailViewModel.getDisputedAwb(String.valueOf(awbNo));
    }

    @Override
    public void onResult(String strScancode) {
        Log.e("AWB no IN RTS", "Scan result is" + strScancode);
        if (strScancode != null) {
            strScancode = strScancode.replaceAll("\\s+", "");
            if (strScancode.equalsIgnoreCase(String.valueOf(awbNo)) || strScancode.equalsIgnoreCase(order_id)) {
                scannedStatus = true;
                showToast("AWB matched");
            } else {
                scannedStatus = false;
                showToast("AWB not matched");
            }
        } else {
            scannedStatus = false;
            showToast("Please scan Awb");
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onProgressTimer(int seconds) {
        mActivityForwardDetailBinding.llCounter.setVisibility(View.VISIBLE);
        disableEnableControls(false, mActivityForwardDetailBinding.parentView);
        otherNumberDialog.dismissDialog();
        if (seconds < 10) {
            mActivityForwardDetailBinding.tvProgressTime.setText("00 : 0" + seconds + "");
        } else {
            mActivityForwardDetailBinding.tvProgressTime.setText("00 : " + seconds + "");
        }
    }

    public void countDown() {
        new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                counter_status = true;
                onProgressTimer((int) millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                counter_status = false;
                onProgressFinishCount();
                forwardDetailViewModel.countDownStatus.set(true);
            }
        }.start();
    }

    @Override
    public void onProgressFinishCount() {
        try {
            disableEnableControls(true, mActivityForwardDetailBinding.parentView);
            mActivityForwardDetailBinding.txtvwMsgLink.setText(R.string.rsnd_link);
            changeLinkText();
            mActivityForwardDetailBinding.llCounter.setVisibility(View.GONE);
            if (progress1 != null && progress1.isShowing()) {
                progress1.dismiss();
                progress1 = null;
            }
        } catch (Exception e) {
            disableEnableControls(true, mActivityForwardDetailBinding.parentView);
            mActivityForwardDetailBinding.llCounter.setVisibility(View.GONE);
            if (progress1 != null && progress1.isShowing()) {
                progress1.dismiss();
                progress1 = null;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQUEST_CODE_SCAN) {
                handleScanResult(data);
            } else if (requestCode == IMAGE_SCANNER_CODE) {
                disputeDialog.onActivityResult(requestCode, resultCode, data);
            } else if (requestCode == CAMERA_SCANNER_CODE) {
                disputeDialog.onActivityResult(requestCode, resultCode, data);
            } else if (requestCode == PICK_FROM_CAMERA) {
                disputeDialog.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            Logger.e(ForwardDetailActivity.class.getName(), e.getMessage());

            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleScanResult(Intent data) {
        try {
            if (data != null) {
                if (data.getStringExtra(ScannerActivity.SCANNED_CODE).equalsIgnoreCase(String.valueOf(awbNo)) || data.getStringExtra(ScannerActivity.SCANNED_CODE).equalsIgnoreCase(order_id)) {
                    scannedStatus = true;
                    showToast("AWB matched");
                    openSignatureActivity(payType, getCollectedValue);
                } else {
                    scannedStatus = false;
                    showToast("AWB not matched");
                }
            } else {
                scannedStatus = false;
                showToast("Please scan Awb");
            }
        } catch (Exception e) {
            Logger.e(ForwardDetailActivity.class.getName(), e.getMessage());

            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void checkDisputedStatus(Boolean aBoolean) {
        if (aBoolean) {
            if (shw_fwd_undelivered_btn.equalsIgnoreCase("Yes")) {
                mActivityForwardDetailBinding.btUndeliver.setVisibility(View.VISIBLE);
            } else {
                mActivityForwardDetailBinding.btUndeliver.setVisibility(View.GONE);
            }
            mActivityForwardDetailBinding.btDisputeNext.setVisibility(View.VISIBLE);
        } else {
            if (shw_fwd_undelivered_btn.equalsIgnoreCase("Yes")) {
                mActivityForwardDetailBinding.btUndeliver.setVisibility(View.VISIBLE);
            } else {
                mActivityForwardDetailBinding.btUndeliver.setVisibility(View.GONE);
            }
            mActivityForwardDetailBinding.btDisputeNext.setVisibility(View.GONE);
        }
    }

    @Override
    public void ontextchange(String stType) {
        if (stType.equalsIgnoreCase(getString(R.string.ppd))) {
            mActivityForwardDetailBinding.amountll.setVisibility(View.GONE);
        } else {
            mActivityForwardDetailBinding.amountll.setVisibility(View.VISIBLE);
        }
    }

}
