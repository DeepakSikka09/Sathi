package in.ecomexpress.sathi.ui.drs.forward.obd.activity;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityObdOtpVerificationBinding;
import in.ecomexpress.sathi.databinding.BottomsheetMobileNumberBinding;
import in.ecomexpress.sathi.repo.local.data.activitiesdata.FWDActivitiesData;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.SecureDelivery;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.drs.forward.obd.navigator.IObdOTPNavigator;
import in.ecomexpress.sathi.ui.drs.forward.obd.viewmodel.OBDStartOTPViewModel;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class FwdOBDStartOTPActivity extends BaseActivity<ActivityObdOtpVerificationBinding, OBDStartOTPViewModel> implements IObdOTPNavigator {

    private final String TAG = FwdOBDStartOTPActivity.class.getSimpleName();
    BottomsheetMobileNumberBinding bottomsheetMobileNumberBinding;
    BottomSheetDialog bottomSheetDialog;
    ActivityObdOtpVerificationBinding binding;
    ColorStateList listBlue;
    ColorStateList listGray;
    ColorStateList listWhite;
    @Inject
    OBDStartOTPViewModel obdOtpViewModel;
    @Inject
    ForwardCommit forwardCommit;
    CountDownTimer mCountDownTimer = null;
    private String otpText = "";
    private String composite_key;
    String getDrsApiKey = null, getDrsPstnKey = null, getDrsPin = null, shipmentType = null;
    SecureDelivery isSecureDelivery;
    private Long longAwbNo;
    boolean is_amazon_schedule_enable;
    private String consigneeName;
    private String consigneeAddress;
    private String vendorName;
    private String itemName;
    private String addressProfiled;
    private String awbNo;
    private boolean isCard;
    private String amazonEncryptedOtp;
    private String amazon;
    private String ofdOtp;
    private String dLightEncryptedOtp1;
    private String dLightEncryptedOtp2;
    private String consigneeMobile;
    private String consigneeAlternateMobile;
    private boolean resendSecureOtp;
    private boolean isDelightShipment;
    private String orderId;
    private String drsIdNum;
    private boolean signImageRequired;
    private boolean fwdDelImage;
    private boolean callAllowed;
    private boolean shouldAllowBack = true;
    private String DRS_DATE = "";
    private String SHIPMENT_DECLARED_VALUE = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        obdOtpViewModel.setNavigator(this);
        binding = getViewDataBinding();
        logScreenNameInGoogleAnalytics(TAG, this);

        getAllDataFromIntent();
        // selection of button color
        listBlue = ColorStateList.valueOf(getResources().getColor(R.color.ecomBlue));
        listGray = ColorStateList.valueOf(getResources().getColor(R.color.grey_78));
        listWhite = ColorStateList.valueOf(getResources().getColor(R.color.white));
        binding.awbHeader.awb.setText("AWB:" + awbNo);

        obdOtpViewModel.getClick();
        setNextButton(listGray, "Verify Code", false);

        binding.ed1.addTextChangedListener(new GenericTextWatcher(binding.ed1, binding.ed2));
        binding.ed2.addTextChangedListener(new GenericTextWatcher(binding.ed2, binding.ed3));
        binding.ed3.addTextChangedListener(new GenericTextWatcher(binding.ed3, binding.ed4));
        binding.ed4.addTextChangedListener(new GenericTextWatcher(binding.ed4, null));

        binding.ed1.setOnKeyListener(new OTPKeyListener(null, binding.ed1));
        binding.ed2.setOnKeyListener(new OTPKeyListener(binding.ed1, binding.ed2));
        binding.ed3.setOnKeyListener(new OTPKeyListener(binding.ed2, binding.ed3));
        binding.ed4.setOnKeyListener(new OTPKeyListener(binding.ed3, binding.ed4));

        binding.icCallIcon.setOnClickListener(view -> obdOtpViewModel.getVoiceCallClick());
        binding.btnOtpVerify.setOnClickListener(view -> {
            if (binding.checkbox.isChecked()) {
                obdOtpViewModel.onUndeliveredClick();
            } else {
                obdOtpViewModel.getOtpVerifyCall();
            }
        });
        binding.txtPleaseAsk.setText(getResources().getString(R.string.please_ask_the_consignee_to_share_the_otp_code_sent_on_mobile_number_789xxxx999) + consigneeMobile);
        binding.checkbox.setOnClickListener(view -> {
            if (binding.checkbox.isChecked()) {
                setNextButton(listBlue, "Mark Undeliver", true);
            } else {
                if (otpText.length() == 4) {
                    setNextButton(listBlue, "Verify Code", true);
                } else {
                    setNextButton(listGray, "Verify Code", false);
                }
            }
        });
        binding.tvResendOtp.setOnClickListener(view -> {
            if (consigneeAlternateMobile == null || consigneeAlternateMobile.isEmpty()) {
                obdOtpViewModel.getResendOtpClick();
            } else {
                openBottomSheet();
            }
        });

        setBackPressed(true);
    }

    @Override
    public void onUndelivered(ForwardCommit forwardCommit) {
        try {
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_MaterialComponents_Light_Dialog_Alert);
                String AlertText1 = "Shipment will be mark as Undelivered";
                builder.setMessage(AlertText1);
                builder.setCancelable(false);
                builder.setPositiveButton("OK", (dialog, id) -> {
                    Intent intent = new Intent();
                    FWDActivitiesData fwdActivitiesData = new FWDActivitiesData();
                    intent = new Intent(FwdOBDStartOTPActivity.this, FwdOBDCompleteActivity.class);
                    fwdActivitiesData.setDrsPin(getDrsPin);
                    fwdActivitiesData.setDrsApiKey(getDrsApiKey);
                    fwdActivitiesData.setDrsPstnKey(getDrsPstnKey);
                    fwdActivitiesData.setOrderId(orderId);
                    fwdActivitiesData.setCallAllowed(callAllowed);
                    fwdActivitiesData.setConsignee_mobile(consigneeMobile);
                    fwdActivitiesData.setConsignee_alternate_number(consigneeAlternateMobile);
                    fwdActivitiesData.setDrsId(Integer.parseInt(drsIdNum));
                    fwdActivitiesData.setAwbNo(longAwbNo);
                    fwdActivitiesData.setCompositeKey(composite_key);
                    fwdActivitiesData.setIs_amazon_reschedule_enabled(is_amazon_schedule_enable);
                    fwdActivitiesData.setShipment_type(shipmentType);
                    fwdActivitiesData.setSecure_undelivered(String.valueOf(obdOtpViewModel.isSecureOtp.get()));
                    fwdActivitiesData.setCollected_value(obdOtpViewModel.getCollectableValue());
                    intent.putExtra(getString(R.string.data), forwardCommit);
                    intent.putExtra("fwdActivitiesData", fwdActivitiesData);
                    intent.putExtra(Constants.OBD_AWB_NUMBER, awbNo);
                    intent.putExtra(Constants.OBD_REFUSED, "OtpNotAvailable");
                    startActivity(intent);
                    applyTransitionToOpenActivity(this);
                    dialog.dismiss();
                });
                builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dialog.cancel();
                });
                AlertDialog alert = builder.create();
                alert.show();
            } catch (Exception e) {
                Logger.e(FwdOBDStartOTPActivity.class.getSimpleName() + "alertShowForUndelivered", e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public OBDStartOTPViewModel getViewModel() {
        return obdOtpViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_obd_otp_verification;
    }

    private void openBottomSheet() {
        try {
            bottomsheetMobileNumberBinding = BottomsheetMobileNumberBinding.inflate(getLayoutInflater());
            bottomSheetDialog = new BottomSheetDialog(this, R.style.otpsheetDialogTheme);
            bottomSheetDialog.setContentView(bottomsheetMobileNumberBinding.getRoot());
            bottomsheetMobileNumberBinding.txtMobilePrimary.setText(consigneeMobile);
            bottomsheetMobileNumberBinding.txtMobileAlternate.setText(consigneeAlternateMobile);
            bottomsheetMobileNumberBinding.mobilePrimaryCard.setOnClickListener(view -> obdOtpViewModel.getOtpOnAlternate(false));
            bottomsheetMobileNumberBinding.mobileSeconadryCard.setOnClickListener(view -> obdOtpViewModel.getOtpOnAlternate(true));
            bottomSheetDialog.setCanceledOnTouchOutside(true);

            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) bottomsheetMobileNumberBinding.getRoot().getParent());
            behavior.setHideable(true);
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            if (bottomSheetDialog != null && !bottomSheetDialog.isShowing()) {
                bottomSheetDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBackPressed(boolean shouldAllowBack) {
        binding.awbHeader.backArrow.setOnClickListener(view -> {
            if (shouldAllowBack) {
                FwdOBDStartOTPActivity.super.onBackPressed();
                applyTransitionToBackFromActivity(this);
            } else {
                showSnackbar("You Cannot Go Back If The Timer Is Tickling");
            }
        });
    }

    private void setNextButton(ColorStateList colorStateList, String text, boolean toEnable) {
        binding.btnOtpVerify.setBackgroundTintList(colorStateList);
        binding.btnOtpVerify.setEnabled(toEnable);
        binding.btnOtpVerify.setText(text);
    }

    @Override
    public String getCompositeKey() {
        return "";
    }

    @Override
    public void navigateToSuccessActivity() {
    }

    @Override
    public void onBack() {
    }

    @Override
    public void onOtpResendSuccess(String description) {
        showSuccessInfo(description);
    }

    @Override
    public void onOtpVerifySuccess(String description, String messageType) {
        if (description.equalsIgnoreCase("Verified")) {
            sendIntent();
        }
    }

    private void sendIntent() {
        Intent intent = new Intent(FwdOBDStartOTPActivity.this, FwdOBDScannerActivity.class);
        intent.putExtra(Constants.OBD_CONSIGNEE_NAME, consigneeName);
        intent.putExtra(Constants.OBD_CONSIGNEE_ADDRESS, consigneeAddress);
        intent.putExtra(Constants.OBD_VENDOR_NAME, vendorName);
        intent.putExtra(Constants.OBD_ITEM_NAME, itemName);
        intent.putExtra(Constants.OBD_ADDRESS_PROFILED, addressProfiled);
        intent.putExtra(Constants.OBD_AWB_NUMBER, awbNo);
        intent.putExtra(Constants.IS_CARD, isCard);
        intent.putExtra(Constants.AMAZON_ENCRYPTED_OTP, amazonEncryptedOtp);
        intent.putExtra(Constants.AMAZON, amazon);
        intent.putExtra(Constants.OFD_OTP, ofdOtp);
        intent.putExtra(Constants.DLIGHT_ENCRYPTED_OTP1, dLightEncryptedOtp1);
        intent.putExtra(Constants.DLIGHT_ENCRYPTED_OTP2, dLightEncryptedOtp2);
        intent.putExtra(Constants.CONSIGNEE_MOBILE, consigneeMobile);
        intent.putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, consigneeAlternateMobile);
        intent.putExtra(Constants.RESEND_SECURE_OTP, resendSecureOtp);
        intent.putExtra(Constants.ISDELIGHTSHIPMENT, isDelightShipment);
        intent.putExtra(Constants.ORDER_ID, orderId);
        intent.putExtra(Constants.DRS_ID_NUM, drsIdNum);
        intent.putExtra(Constants.sign_image_required, signImageRequired);
        intent.putExtra(Constants.FWD_DEL_IMAGE, fwdDelImage);
        intent.putExtra(Constants.DRS_DATE, DRS_DATE);
        intent.putExtra(Constants.SHIPMENT_DECLARED_VALUE, SHIPMENT_DECLARED_VALUE);
        intent.putExtra("call_allowed", callAllowed);
        intent.putExtra("isUndelivered", false);
        intent.putExtra("flag", false);
        intent.putExtra("isCheckedbox", false);
        intent.putExtra(getString(R.string.data), forwardCommit);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    @Override
    public void onHandleError(String errorResponse) {
    }

    @Override
    public void showErrorMessage(String error) {
    }

    @Override
    public void resendSms(boolean alternateClick) {
        bottomSheetDialog.dismiss();
        obdOtpViewModel.onGenerateOtpApiCall(FwdOBDStartOTPActivity.this, String.valueOf(awbNo), String.valueOf(drsIdNum), alternateClick, Constants.OBD_START_TYPE, false);
    }

    @Override
    public void onGenerateOtpClick() {
        hideKeyboard(this);
        obdOtpViewModel.onGenerateOtpApiCall(FwdOBDStartOTPActivity.this, String.valueOf(awbNo), String.valueOf(drsIdNum), false, Constants.OBD_START_TYPE, true);
    }

    @Override
    public void onGenerateOtpResendClick() {
    }

    @Override
    public void onGenerateStopOtp(boolean isGenerate) {
    }

    @Override
    public void onGenerateQcFailOtp(boolean isGenerate) {
    }

    @Override
    public void showError(String error) {
        showSnackbar(error);
    }

    @Override
    public void doLogout(String description) {
        showToast(getString(R.string.session_expire));
        obdOtpViewModel.logoutLocal();
    }

    @Override
    public void clearStack() {
        Intent intent = new Intent(FwdOBDStartOTPActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    @Override
    public void onGenerateCtsOtp(boolean isGenerate) {
    }

    @Override
    public void onResendClick() {
        hideKeyboard(this);
        obdOtpViewModel.onGenerateOtpApiCall(FwdOBDStartOTPActivity.this, String.valueOf(awbNo), String.valueOf(drsIdNum), false, Constants.OBD_START_TYPE, false);
    }

    @Override
    public void VoiceCallOtp(String otpType) {
        hideKeyboard(this);
        obdOtpViewModel.doVoiceOTPApi(String.valueOf(awbNo), String.valueOf(drsIdNum), Constants.OBD_START_TYPE);
    }

    @Override
    public Context getActivityContext() {
        return this;
    }

    @Override
    public void onVerifyClick() {
        hideKeyboard(this);
        setOtpText();
        obdOtpViewModel.onVerifyApiCall(FwdOBDStartOTPActivity.this, String.valueOf(awbNo), otpText, String.valueOf(drsIdNum), Constants.OBD_START_TYPE);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void voiceTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = new CountDownTimer(obdOtpViewModel.getDataManager().getDisableResendOtpButtonDuration() * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    setBackPressed(false);
                    shouldAllowBack = false;
                    binding.tvResendOtp.setVisibility(View.GONE);
                    String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                    System.out.println(hms);
                    binding.tvResendTimer.setVisibility(View.VISIBLE);
                    binding.tvResendTimer.setText("Didn’t receive a code (" + hms + ")");
                }

                @Override
                public void onFinish() {
                    binding.tvResendOtp.setVisibility(View.VISIBLE);
                    binding.tvResendTimer.setVisibility(View.GONE);
                    setBackPressed(true);
                    shouldAllowBack = true;
                }
            };
            mCountDownTimer.start();
        } else {
            mCountDownTimer = new CountDownTimer(obdOtpViewModel.getDataManager().getDisableResendOtpButtonDuration() * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    setBackPressed(false);
                    shouldAllowBack = false;
                    binding.tvResendOtp.setVisibility(View.GONE);
                    String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                    System.out.println(hms);
                    binding.tvResendTimer.setVisibility(View.VISIBLE);
                    binding.tvResendTimer.setText("Didn’t receive a code (" + hms + ")");
                }

                @Override
                public void onFinish() {
                    binding.tvResendOtp.setVisibility(View.VISIBLE);
                    binding.tvResendTimer.setVisibility(View.GONE);
                    setBackPressed(true);
                    shouldAllowBack = true;
                }
            };
            mCountDownTimer.start();
        }
    }


    private void setOtpText() {
        otpText = binding.ed1.getText().toString() + binding.ed2.getText() + binding.ed3.getText() + binding.ed4.getText();
    }


    class GenericTextWatcher implements TextWatcher {

        private final EditText currentEditText;
        private final EditText nextEditText;

        public GenericTextWatcher(EditText currentEditText, EditText nextEditText) {
            this.currentEditText = currentEditText;
            this.nextEditText = nextEditText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (currentEditText.getText().toString().length() == 1 && nextEditText != null) {
                nextEditText.requestFocus();
            }
            if (otpText.isEmpty()) {
                binding.checkbox.setEnabled(true);
                binding.checkbox.setChecked(false);
                binding.btnOtpVerify.setText("Verify Code");
            } else {
                binding.checkbox.setEnabled(false);
                binding.checkbox.setChecked(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            setOtpText();

            if (otpText.isEmpty() && binding.checkbox.isChecked()) {
                binding.checkbox.setEnabled(true);
                binding.checkbox.setChecked(false);
                setNextButton(listGray, "Mark Undeliver", false);

            } else if (otpText.isEmpty() && !binding.checkbox.isChecked()) {
                binding.checkbox.setEnabled(true);
                binding.checkbox.setChecked(false);
                setNextButton(listGray, "Verify Code", false);

            } else if (otpText.length() == 4) {
                binding.checkbox.setEnabled(false);
                binding.checkbox.setChecked(false);
                setNextButton(listBlue, "Verify Code", true);

            } else {
                binding.checkbox.setEnabled(false);
                binding.checkbox.setChecked(false);
                setNextButton(listGray, "Verify Code", false);
            }
        }
    }

    static class OTPKeyListener implements View.OnKeyListener {

        private final EditText previousView;
        private final EditText currentView;

        public OTPKeyListener(EditText previousView, EditText currentView) {
            this.previousView = previousView;
            this.currentView = currentView;
        }

        @Override
        public boolean onKey(View view, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (currentView.getText().length() == 0 && previousView != null) {
                    previousView.requestFocus();
                    previousView.setText("");
                }
            }
            return false;
        }
    }

    private void getAllDataFromIntent() {
        Intent intent = getIntent();
        consigneeName = intent.getStringExtra(Constants.OBD_CONSIGNEE_NAME);
        consigneeAddress = intent.getStringExtra(Constants.OBD_CONSIGNEE_ADDRESS);
        vendorName = intent.getStringExtra(Constants.OBD_VENDOR_NAME);
        itemName = intent.getStringExtra(Constants.OBD_ITEM_NAME);
        addressProfiled = intent.getStringExtra(Constants.OBD_ADDRESS_PROFILED);
        awbNo = intent.getStringExtra(Constants.OBD_AWB_NUMBER);
        isCard = intent.getBooleanExtra(Constants.IS_CARD, false);
        amazonEncryptedOtp = intent.getStringExtra(Constants.AMAZON_ENCRYPTED_OTP);
        amazon = intent.getStringExtra(Constants.AMAZON);
        ofdOtp = intent.getStringExtra(Constants.OFD_OTP);
        dLightEncryptedOtp1 = intent.getStringExtra(Constants.DLIGHT_ENCRYPTED_OTP1);
        dLightEncryptedOtp2 = intent.getStringExtra(Constants.DLIGHT_ENCRYPTED_OTP2);
        consigneeMobile = intent.getStringExtra(Constants.CONSIGNEE_MOBILE);
        consigneeAlternateMobile = intent.getStringExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE);
        resendSecureOtp = intent.getBooleanExtra(Constants.RESEND_SECURE_OTP, false);
        isDelightShipment = intent.getBooleanExtra(Constants.ISDELIGHTSHIPMENT, false);
        orderId = intent.getStringExtra(Constants.ORDER_ID);
        drsIdNum = intent.getStringExtra(Constants.DRS_ID_NUM);
        signImageRequired = intent.getBooleanExtra(Constants.sign_image_required, false);
        fwdDelImage = intent.getBooleanExtra(Constants.FWD_DEL_IMAGE, false);
        callAllowed = intent.getBooleanExtra("call_allowed", false);
        composite_key = getIntent().getStringExtra(Constants.COMPOSITE_KEY);
        shipmentType = getIntent().getExtras().getString(Constants.SHIPMENT_TYPE);
        getDrsApiKey = getIntent().getExtras().getString(Constants.DRS_API_KEY);
        getDrsPstnKey = getIntent().getExtras().getString(Constants.DRS_PSTN_KEY);
        getDrsPin = getIntent().getExtras().getString(Constants.DRS_PIN);
        DRS_DATE = getIntent().getExtras().getString(Constants.DRS_DATE);
        SHIPMENT_DECLARED_VALUE = getIntent().getExtras().getString(Constants.SHIPMENT_DECLARED_VALUE);
        longAwbNo = getIntent().getLongExtra(Constants.INTENT_KEY, 0);
        is_amazon_schedule_enable = getIntent().getBooleanExtra(Constants.IS_AMAZON_RESHEDUCLE_ENABLE, false);
        isSecureDelivery = getIntent().getParcelableExtra(Constants.SECURE_DELIVERY);
        obdOtpViewModel.getFWDShipmentData(forwardCommit, composite_key, consigneeMobile);
        obdOtpViewModel.getSecureDelivery(isSecureDelivery);
    }

    @Override
    public void onBackPressed() {
        if (shouldAllowBack) {
            super.onBackPressed();
            applyTransitionToBackFromActivity(this);
        } else {
            showSnackbar("You Cannot Go Back If The Timer Is Tickling");
        }
    }
}

