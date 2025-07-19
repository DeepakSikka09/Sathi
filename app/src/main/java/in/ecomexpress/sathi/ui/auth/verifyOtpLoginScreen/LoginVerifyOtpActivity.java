package in.ecomexpress.sathi.ui.auth.verifyOtpLoginScreen;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityLoginVerifyOtpBinding;
import in.ecomexpress.sathi.utils.receivers.SmsBroadcastReceiver;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.dashboard.landing.DashboardActivity;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;
import in.ecomexpress.sathi.ui.dashboard.performance.PerformanceActivity;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.PermissionManager;

@AndroidEntryPoint
public class LoginVerifyOtpActivity extends BaseActivity<ActivityLoginVerifyOtpBinding, LoginVerifyOtpViewModel> implements ILoginVerifyOtpNavigator {

    private static final String TAG = LoginVerifyOtpActivity.class.getSimpleName();
    public static final int REQUEST_CODE_FOR_SMS = 1;
    CounterClass otpTimer;
    @Inject
    LoginVerifyOtpViewModel loginVerifyOtpViewModel;
    boolean flag = true;
    ActivityLoginVerifyOtpBinding activityLoginVerifyOtpBinding;
    boolean flagValue, manualOtpFlag;
    private static final int REQ_USER_CONSENT = 200;
    SmsBroadcastReceiver smsBroadcastReceiver;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, LoginVerifyOtpActivity.class);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginVerifyOtpViewModel.setNavigator(this);
        logScreenNameInGoogleAnalytics(TAG, this);
        try {
            flagValue = getIntent().getBooleanExtra("flag", false);
            manualOtpFlag = getIntent().getBooleanExtra("manualOtpFlag", false);
            this.activityLoginVerifyOtpBinding = getViewDataBinding();
            activityLoginVerifyOtpBinding.timerTv.setText("00:30");
            otpTimer = new CounterClass(30000, 1000);
            otpTimer.start();
            activityLoginVerifyOtpBinding.enterOtpLayoutChild3.setVisibility(View.GONE);
            activityLoginVerifyOtpBinding.enterOtpLayoutChild2.setVisibility(View.VISIBLE);
            startSmsUserConsent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
            applyTransitionToBackFromActivity(this);
        } else {
            Toast.makeText(this, getString(R.string.backbutton_is_disabled_until_the_timer_is_off), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean shouldAllowBack() {
        return activityLoginVerifyOtpBinding.timerTv.getText().toString().equalsIgnoreCase("00:00");
    }

    @Override
    protected void onStart() {
        super.onStart();
        PermissionManager.check(this, android.Manifest.permission.RECEIVE_SMS, REQUEST_CODE_FOR_SMS);
        registerBroadcastReceiver();
        onResendOtp();
    }

    @Override
    public LoginVerifyOtpViewModel getViewModel() {
        return loginVerifyOtpViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login_verify_otp;
    }

    @Override
    public void onResendOtp() {
        try {
            activityLoginVerifyOtpBinding.enterOtpLayoutChild3.setVisibility(View.VISIBLE);
            activityLoginVerifyOtpBinding.enterOtpLayoutChild2.setVisibility(View.GONE);
            loginVerifyOtpViewModel.resendOtp("none");
        } catch (Exception e) {
            e.printStackTrace();
            onHandleError(getString(R.string.http_500_msg));
        }
    }

    @Override
    public void onNOtp() {

    }

    @Override
    public void onHandleError(String description) {
        try {
            runOnUiThread(() -> showSnackbar(description));
        } catch (Exception e) {
            Logger.e("onHandleError", String.valueOf(e));
        }
    }

    @Override
    public void onNext(String otpDelimiter) {
        if (otpDelimiter.length() == 6) {
            Constants.OTP_DELIMITER = "OTP";
            loginVerifyOtpViewModel.updateUserLoggedInState();
            if(loginVerifyOtpViewModel.getDataManager().getSathiAttendanceFeatureEnable()){
                loginVerifyOtpViewModel.checkAttendance(LoginVerifyOtpActivity.this, loginVerifyOtpViewModel.getDataManager().getEmp_code());
            } else{
                if (loginVerifyOtpViewModel.getDataManager().getDEFAULTSTATISTICS().equalsIgnoreCase("true")) {
                    Intent intent = new Intent(LoginVerifyOtpActivity.this, PerformanceActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(LoginVerifyOtpActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                }
                applyTransitionToOpenActivity(this);
            }
        } else {
            runOnUiThread(() -> showSnackbar("OTP Sent Successfully"));
        }
    }

    @Override
    public void onVerify() {
        try {
            if (containsOnlyNumbers(activityLoginVerifyOtpBinding.otpEdt.getText().toString()) && activityLoginVerifyOtpBinding.otpEdt.getText().toString().length() == 6) {
                loginVerifyOtpViewModel.verifyOtp(activityLoginVerifyOtpBinding.otpEdt.getText().toString());
                flag = false;
            } else {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), getString(R.string.enter_valid_otp), Toast.LENGTH_LONG).show());
            }
        } catch (Exception e) {
            e.printStackTrace();
            onHandleError(getString(R.string.http_500_msg));
        }
    }

    @Override
    public void logout() {
        try {
            hideKeyboard(LoginVerifyOtpActivity.this);
            runOnUiThread(() -> Toast.makeText(LoginVerifyOtpActivity.this, R.string.your_session_has_expired_please_log_in, Toast.LENGTH_SHORT).show());
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            applyTransitionToOpenActivity(this);
        } catch (Exception e) {
            onHandleError(getString(R.string.http_500_msg));
        }
    }

    @Override
    public void showErrorMessage(boolean status) {
        if (status)
            runOnUiThread(() -> Toast.makeText(getApplicationContext(), getString(R.string.http_500_msg), Toast.LENGTH_LONG).show());
        else
            runOnUiThread(() -> Toast.makeText(getApplicationContext(), getString(R.string.server_down_msg), Toast.LENGTH_LONG).show());
    }

    public class CounterClass extends CountDownTimer {

        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            try {
                activityLoginVerifyOtpBinding.enterOtpLayoutChild3.setVisibility(View.VISIBLE);
                activityLoginVerifyOtpBinding.enterOtpLayoutChild2.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
                onHandleError(getString(R.string.http_500_msg));
            }
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onTick(long millisUntilFinished) {
            try {
                String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                activityLoginVerifyOtpBinding.timerTv.setTextColor(Color.parseColor("#27AD92"));
                activityLoginVerifyOtpBinding.timerTv.setText(hms);
                activityLoginVerifyOtpBinding.resendTv.setBackgroundColor(getResources().getColor(R.color.light_gray));
                if (Constants.OTP_DELIMITER.length() == 6) {
                    if (flag) {
                        if (containsOnlyNumbers(Constants.OTP_DELIMITER) && Constants.OTP_DELIMITER.length() == 6) {
                            loginVerifyOtpViewModel.verifyOtp(Constants.OTP_DELIMITER);
                            flag = false;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                onHandleError(getString(R.string.http_500_msg));
            }
        }
    }

    public static boolean containsOnlyNumbers(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i)))
                return false;
        }
        return true;
    }

    private void startSmsUserConsent() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        client.startSmsUserConsent(null).addOnSuccessListener(aVoid -> {}).addOnFailureListener(e -> {});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_USER_CONSENT) {
            if ((resultCode == RESULT_OK) && (data != null)) {
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                unregisterReceiver(smsBroadcastReceiver);
                getOtpFromMessage(message);
            }
        }
    }

    private void getOtpFromMessage(String message) {
        Pattern pattern = Pattern.compile("(|^)\\d{6}");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            activityLoginVerifyOtpBinding.otpEdt.setText(matcher.group(0));
            Constants.OTP_DELIMITER = matcher.group(0);
            if (containsOnlyNumbers(Constants.OTP_DELIMITER) && Constants.OTP_DELIMITER.length() == 6) {
                loginVerifyOtpViewModel.verifyOtp(Constants.OTP_DELIMITER);
                flag = false;
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void registerBroadcastReceiver() {
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        smsBroadcastReceiver.smsBroadcastReceiverListener = new SmsBroadcastReceiver.SmsBroadcastReceiverListener() {
            @Override
            public void onSuccess(Intent intent) {
                startActivityForResult(intent, REQ_USER_CONSENT);
            }

            @Override
            public void onFailure() {}
        };
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Nullable
    @Override
    public CharSequence onCreateDescription() {
        return super.onCreateDescription();
    }
}