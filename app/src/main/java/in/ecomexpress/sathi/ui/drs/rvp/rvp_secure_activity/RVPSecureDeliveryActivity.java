package in.ecomexpress.sathi.ui.drs.rvp.rvp_secure_activity;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.Constants.INTENT_KEY_RVP_SAMPLE_QUESTIONS;
import static in.ecomexpress.sathi.utils.Constants.INTENT_KEY_RVP_WITH_QC;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityRvpSecureDeliveryBinding;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.SecureDelivery;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.RvpQcDataDetailsActivity;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class RVPSecureDeliveryActivity extends BaseActivity<ActivityRvpSecureDeliveryBinding, RVPSecureDeliveryViewModel> implements IRVPSecureDeliveryNavigator {

    private final String TAG = RVPSecureDeliveryActivity.class.getSimpleName();
    @Inject
    RVPSecureDeliveryViewModel rvpSecureDeliveryViewModel;
    ActivityRvpSecureDeliveryBinding rvpSecureDeliveryBinding;
    Context context;
    String getDrsApiKey = null;
    String getDrsPstnKey = null;
    String getDrsPin = null;
    String composite_key = "";
    SecureDelivery isSecureDelivery;
    Long awbNo;
    boolean call_allowed;
    String OFD_OTP;
    int drs_id;
    String CONSIGNEE_ALTERNATE_MOBILE;
    String PHONE = "";
    CountDownTimer mCountDownTimer = null;
    String otp_required_for_delivery = "N";

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rvpSecureDeliveryViewModel.setNavigator(this);
        rvpSecureDeliveryBinding = getViewDataBinding();
        logScreenNameInGoogleAnalytics(TAG, this);
        try {
            awbNo = getIntent().getLongExtra(Constants.INTENT_KEY, 0);
            drs_id = getIntent().getIntExtra(Constants.DRS_ID, 0);
            OFD_OTP = getIntent().getStringExtra(Constants.OFD_OTP);
            CONSIGNEE_ALTERNATE_MOBILE = getIntent().getStringExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE);
            PHONE = getIntent().getStringExtra(Constants.CONSIGNEE_MOBILE);
            call_allowed = getIntent().getBooleanExtra("call_allowed", false);
            composite_key = getIntent().getStringExtra(Constants.COMPOSITE_KEY);
            isSecureDelivery = new SecureDelivery();
            otp_required_for_delivery = getIntent().getStringExtra(Constants.otp_required_for_delivery);
            getDrsApiKey = getIntent().getExtras().getString(Constants.DRS_API_KEY);
            getDrsPstnKey = getIntent().getExtras().getString(Constants.DRS_PSTN_KEY);
            call_allowed = getIntent().getBooleanExtra("call_allowed", false);

            // get shipment data from DB for RVP as per composite key
            rvpSecureDeliveryViewModel.getRVPShipmentData(composite_key);
            // get QC data from DB for RVP as per composite key
            rvpSecureDeliveryViewModel.getRvpDataWithQc(composite_key);

            rvpSecureDeliveryBinding.tvNumberStatement.setText(String.format("%s%s", getString(R.string.we_have_send_otp_on_registered_mobile_number), PHONE));
            RVPSecureDeliveryMapFragment rvpSecureDeliveryMapFragment = new RVPSecureDeliveryMapFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, rvpSecureDeliveryMapFragment).commit();
        } catch (Exception e) {
            Logger.e(RVPSecureDeliveryActivity.class.getName(), e.getMessage());
            showSnackbar(e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (otp_required_for_delivery.equalsIgnoreCase("Y")) {
                rvpSecureDeliveryBinding.otpSkip.setVisibility(View.GONE);
            } else {
                if (rvpSecureDeliveryViewModel.getDataManager().getSKIPOTPREVRQC().equalsIgnoreCase("True")) {
                    rvpSecureDeliveryBinding.otpSkip.setVisibility(View.VISIBLE);
                } else {
                    rvpSecureDeliveryBinding.otpSkip.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            if (rvpSecureDeliveryViewModel.getDataManager().getSKIPOTPREVRQC().equalsIgnoreCase("True")) {
                rvpSecureDeliveryBinding.otpSkip.setVisibility(View.VISIBLE);
            } else {
                rvpSecureDeliveryBinding.otpSkip.setVisibility(View.GONE);
            }
            Logger.e(RVPSecureDeliveryActivity.class.getName(), e.getMessage());
        }
    }

    public static Intent getStartIntent(Context context) {
        return new Intent(context, RVPSecureDeliveryActivity.class);
    }

    @Override
    public RVPSecureDeliveryViewModel getViewModel() {
        return rvpSecureDeliveryViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_rvp_secure_delivery;
    }

    public void onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
            applyTransitionToBackFromActivity(this);
        } else {
            showSnackbar("BackButton is disabled until the timer is off.");
        }
    }

    @Override
    public void onBack() {
        if (shouldAllowBack()) {
            super.onBackPressed();
            applyTransitionToBackFromActivity(this);
        } else {
            showSnackbar("BackButton is disabled until the timer is off.");
        }
    }

    @Override
    public void onOtpResendSuccess(String description) {
        showSnackbar(description);
        if (description.equalsIgnoreCase("Otp already generated!")) {
            rvpSecureDeliveryBinding.otpSkip.setEnabled(true);
        } else {
            rvpSecureDeliveryBinding.otpSkip.setEnabled(false);
            showCounter();
        }
        rvpSecureDeliveryBinding.edtUdOtp.setEnabled(true);
        rvpSecureDeliveryBinding.generateOtpTv.setVisibility(View.GONE);
        rvpSecureDeliveryBinding.resendOtpTv.setVisibility(View.VISIBLE);
        rvpSecureDeliveryBinding.verifyTv.setVisibility(View.VISIBLE);
    }

    public void showCounter() {
        new CountDownTimer(rvpSecureDeliveryViewModel.getDataManager().getDisableResendOtpButtonDuration() * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                rvpSecureDeliveryBinding.resendOtpTv.setEnabled(false);
                rvpSecureDeliveryBinding.resendOtpTv.setTextColor(getResources().getColor(R.color.black));
                rvpSecureDeliveryBinding.otpSkip.setEnabled(false);
                rvpSecureDeliveryBinding.otpSkip.setChecked(false);
                String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                rvpSecureDeliveryBinding.resendOtpTv.setText(hms);
                rvpSecureDeliveryBinding.resendOtpTv.setTextColor(getResources().getColor(R.color.white));
            }

            @Override
            public void onFinish() {
                rvpSecureDeliveryBinding.otpSkip.setEnabled(true);
                rvpSecureDeliveryBinding.resendOtpTv.setEnabled(true);
                rvpSecureDeliveryBinding.resendOtpTv.setText(getResources().getString(R.string.resend));
                rvpSecureDeliveryBinding.resendOtpTv.setTextColor(getResources().getColor(R.color.White));
            }
        }.start();
    }

    @Override
    public void onOtpVerifySuccess(String type) {
        Constants.RVPCOMMIT = type;
        Intent intent = RvpQcDataDetailsActivity.getStartIntent(this);
        intent.putExtra(INTENT_KEY_RVP_WITH_QC, rvpSecureDeliveryViewModel.rvpWithQCObservableField.get());
        intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstnKey);
        intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
        intent.putExtra(Constants.DRS_PIN, getDrsPin);
        intent.putExtra(Constants.DRS_ID, drs_id);
        intent.putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, CONSIGNEE_ALTERNATE_MOBILE);
        intent.putExtra("call_allowed", call_allowed);
        intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
        intent.putExtra(Constants.INTENT_KEY, awbNo);
        intent.putExtra("otp_verified_status", rvpSecureDeliveryViewModel.ofd_otp_verify_status.get());
        intent.putParcelableArrayListExtra(INTENT_KEY_RVP_SAMPLE_QUESTIONS, rvpSecureDeliveryViewModel.sampleQuestionList.get());
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    private boolean shouldAllowBack() {
        if (Objects.requireNonNull(rvpSecureDeliveryViewModel.ofd_otp_verify_status.get()).equalsIgnoreCase("VERIFIED")) {
            return false;
        } else
            return rvpSecureDeliveryBinding.resendOtpTv.getText().toString().equalsIgnoreCase("RESEND") || rvpSecureDeliveryBinding.resendOtpTv.getVisibility() == View.GONE;
    }

    @Override
    public Context getActivity() {
        return this;
    }

    @Override
    public void resendSMS(Boolean alternateClick) {
        hideKeyboard(this);
        rvpSecureDeliveryViewModel.onGenerateOtpApiCall(RVPSecureDeliveryActivity.this, String.valueOf(awbNo), String.valueOf(drs_id), alternateClick, "OTP", false);
    }

    @Override
    public void onGenerateOtpClick() {
        hideKeyboard(this);
        rvpSecureDeliveryViewModel.onGenerateOtpApiCall(RVPSecureDeliveryActivity.this, String.valueOf(awbNo), String.valueOf(drs_id), false, "OTP", true);
    }

    @Override
    public void showError(String error) {
        showSnackbar(error);
    }

    @Override
    public void doLogout(String description) {
        showToast(getString(R.string.session_expire));
        rvpSecureDeliveryViewModel.logoutLocal();
    }

    @Override
    public void onResendClick() {
        hideKeyboard(this);
        if (call_allowed && rvpSecureDeliveryViewModel.getDataManager().getVCallPopup()) {
            rvpSecureDeliveryViewModel.showCallAndSmsDialog(CONSIGNEE_ALTERNATE_MOBILE, "Call");
        } else if (!CONSIGNEE_ALTERNATE_MOBILE.equalsIgnoreCase("") || CONSIGNEE_ALTERNATE_MOBILE != null) {
            rvpSecureDeliveryViewModel.showCallAndSmsDialog(CONSIGNEE_ALTERNATE_MOBILE, "");
        } else {
            rvpSecureDeliveryViewModel.onGenerateOtpApiCall(RVPSecureDeliveryActivity.this, String.valueOf(awbNo), String.valueOf(drs_id), false, "OTP", false);
        }
    }

    @Override
    public Context getActivityContext() {
        return this;
    }

    @Override
    public void resendSms(boolean b) {
    }

    @Override
    public void VoiceCallOtp() {
        hideKeyboard(this);
        rvpSecureDeliveryViewModel.doVoiceOTPApi(String.valueOf(awbNo), String.valueOf(drs_id), "OTP");
    }

    @Override
    public void voiceTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = new CountDownTimer(rvpSecureDeliveryViewModel.getDataManager().getDisableResendOtpButtonDuration() * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    rvpSecureDeliveryBinding.resendOtpTv.setEnabled(false);
                    rvpSecureDeliveryBinding.edtUdOtp.setEnabled(true);
                    rvpSecureDeliveryBinding.resendOtpTv.setTextColor(getResources().getColor(R.color.light_gray));
                    String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                    System.out.println(hms);
                    rvpSecureDeliveryBinding.resendOtpTv.setText(hms);
                }

                @Override
                public void onFinish() {
                    rvpSecureDeliveryBinding.resendOtpTv.setEnabled(true);
                    rvpSecureDeliveryBinding.edtUdOtp.setEnabled(true);
                    rvpSecureDeliveryBinding.resendOtpTv.setText(getResources().getString(R.string.resend));
                }
            };
            mCountDownTimer.start();
        } else {
            mCountDownTimer = new CountDownTimer(rvpSecureDeliveryViewModel.getDataManager().getDisableResendOtpButtonDuration() * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    rvpSecureDeliveryBinding.resendOtpTv.setEnabled(false);
                    rvpSecureDeliveryBinding.edtUdOtp.setEnabled(true);
                    rvpSecureDeliveryBinding.resendOtpTv.setTextColor(getResources().getColor(R.color.light_gray));
                    String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                    System.out.println(hms);
                    rvpSecureDeliveryBinding.resendOtpTv.setText(hms);
                }

                @Override
                public void onFinish() {
                    rvpSecureDeliveryBinding.resendOtpTv.setEnabled(true);
                    rvpSecureDeliveryBinding.edtUdOtp.setEnabled(true);
                    rvpSecureDeliveryBinding.resendOtpTv.setText(getResources().getString(R.string.resend));
                }
            };
            mCountDownTimer.start();
        }
    }

    @Override
    public void onVerifyClick() {
        hideKeyboard(this);
        Constants.OFD_OTP_VERIFIED = true;
        if (rvpSecureDeliveryBinding.edtUdOtp.getText() == null || rvpSecureDeliveryBinding.edtUdOtp.getText().toString().equalsIgnoreCase("")) {
            showError("Please Enter OTP.");
        } else if (rvpSecureDeliveryBinding.edtUdOtp.getText().length() < 4) {
            showError("Enter valid OTP.");
        } else {
            if (OFD_OTP != null && !OFD_OTP.equalsIgnoreCase("")) {
                String encryptText = CommonUtils.decrypt(OFD_OTP, Constants.DECRYPT);
                Constants.PLAIN_OTP = encryptText;
                if (Objects.requireNonNull(encryptText).equalsIgnoreCase(rvpSecureDeliveryBinding.edtUdOtp.getText().toString())) {
                    Constants.OFD_OTP_VERIFIED = true;
                    rvpSecureDeliveryViewModel.setOFDVerified(true);
                    showSnackbar("Verified successfully");
                } else {
                    Constants.OFD_OTP_VERIFIED = true;
                    rvpSecureDeliveryViewModel.onVerifyApiCall(RVPSecureDeliveryActivity.this, String.valueOf(awbNo), rvpSecureDeliveryBinding.edtUdOtp.getText().toString(), String.valueOf(drs_id), "OTP");
                    showSnackbar("Verified.");
                    rvpSecureDeliveryBinding.otpSkip.setChecked(false);
                }
            } else {
                Constants.OFD_OTP_VERIFIED = true;
                rvpSecureDeliveryViewModel.onVerifyApiCall(RVPSecureDeliveryActivity.this, String.valueOf(awbNo), rvpSecureDeliveryBinding.edtUdOtp.getText().toString(), String.valueOf(drs_id), "OTP");
                showSnackbar("Verified.");
                rvpSecureDeliveryBinding.otpSkip.setChecked(false);
            }
        }
    }

    @Override
    public void onSkipClick(View view) {
        hideKeyboard(this);
        if (Objects.equals(rvpSecureDeliveryViewModel.ofd_otp_verify_status.get(), "VERIFIED")) {
            showErrorMessage("Already Verified");
            rvpSecureDeliveryBinding.otpSkip.setChecked(false);
        } else if (rvpSecureDeliveryBinding.resendOtpTv.getVisibility() == View.GONE) {
            showErrorMessage("Please resend OTP atleast once.");
            rvpSecureDeliveryBinding.otpSkip.setChecked(false);
        } else {
            if (rvpSecureDeliveryBinding.resendOtpTv.isEnabled()) {
                if (((CheckBox) view).isChecked()) {
                    rvpSecureDeliveryViewModel.ofd_otp_verify_status.set("SKIPPED");
                    rvpSecureDeliveryViewModel.ofd_otp_verified.set("false");
                } else {
                    rvpSecureDeliveryViewModel.ofd_otp_verify_status.set("NONE");
                    rvpSecureDeliveryViewModel.ofd_otp_verified.set("null");
                }
            } else {
                rvpSecureDeliveryBinding.otpSkip.setChecked(false);
            }
        }
    }

    @Override
    public void setGreenTick() {
        rvpSecureDeliveryBinding.imgVerifiedTick.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHandleError(String errorResponse) {
        showSnackbar(errorResponse);
        if (errorResponse.equalsIgnoreCase("Invalid Authentication Token.")) {
            rvpSecureDeliveryViewModel.logoutLocal();
        }
    }

    @Override
    public void getAlert() {
    }

    @Override
    public void clearStack() {
        Intent intent = new Intent(RVPSecureDeliveryActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    @Override
    public void showErrorMessage(String error) {
        showSnackbar(error);
    }

    @Override
    public void onScanClick() {
    }

    @Override
    public void mResultReceiver1(String strBarcodeScan) {
    }

    @Override
    public void onMobileNoChange() {
    }


}
