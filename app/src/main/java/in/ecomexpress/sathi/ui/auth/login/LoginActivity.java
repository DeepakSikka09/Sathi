package in.ecomexpress.sathi.ui.auth.login;

import static in.ecomexpress.sathi.SathiApplication.esperDeviceSDK;
import static in.ecomexpress.sathi.SathiApplication.esperSDKActivated;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.exotel.verification.ConfigBuilder;
import com.exotel.verification.ExotelVerification;
import com.exotel.verification.Timer;
import com.exotel.verification.VerificationListener;
import com.exotel.verification.contracts.Config;
import com.exotel.verification.contracts.VerificationFailed;
import com.exotel.verification.contracts.VerificationStart;
import com.exotel.verification.contracts.VerificationSuccess;
import com.exotel.verification.exceptions.ConfigBuilderException;
import com.exotel.verification.exceptions.PermissionNotGrantedException;
import com.exotel.verification.exceptions.VerificationAlreadyInProgressException;
import com.judemanutd.autostarter.AutoStartPermissionHelper;
import com.shield.android.Shield;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.backgroundServices.SathiLocationService;
import in.ecomexpress.sathi.backgroundServices.SyncServicesV2;
import in.ecomexpress.sathi.databinding.ActivityLoginBinding;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.login.LoginResponse;
import in.ecomexpress.sathi.ui.auth.changepassword.ChangePasswordActivity;
import in.ecomexpress.sathi.ui.auth.forget.ForgetActivity;
import in.ecomexpress.sathi.ui.auth.verifyOtpLoginScreen.LoginVerifyOtpActivity;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.dashboard.landing.DashboardActivity;
import in.ecomexpress.sathi.ui.dashboard.mapview.MapActivity;
import in.ecomexpress.sathi.ui.dashboard.performance.PerformanceActivity;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.DefaultExceptionHandler;
import in.ecomexpress.sathi.utils.Logger;
import io.esper.devicesdk.EsperDeviceSDK;

@AndroidEntryPoint
public class LoginActivity extends BaseActivity<ActivityLoginBinding, LoginViewModel> implements ILoginNavigator {

    private final String TAG = LoginActivity.class.getSimpleName();
    @Inject
    LoginViewModel mLoginViewModel;
    @Inject
    Context context;
    ActivityLoginBinding mActivityLoginBinding;
    ChangePasswordActivity changePasswordActivity;
    private ProgressDialog progressDialog;
    Dialog dialog = null;
    boolean flag;
    boolean manualOtpFlag;
    @SuppressLint("StaticFieldLeak")
    public static LoginActivity globalActivityContext;
    CounterClass otpTimer;
    ExotelVerification eVerification;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    class VerifyListener implements VerificationListener {
        public void onVerificationStarted(VerificationStart verificationStart) {
            showNOTPDialog();
            Toast.makeText(LoginActivity.this, "Verification Started", Toast.LENGTH_SHORT).show();
        }

        public void onVerificationSuccess(VerificationSuccess verificationSuccess) {
            if (dialog != null) {
                dialog.dismiss();
            }
            mLoginViewModel.verifyOtp("Success");
            Toast.makeText(LoginActivity.this, "Verification Success", Toast.LENGTH_SHORT).show();
        }

        public void onVerificationFailed(VerificationFailed verificationFailed) {
            showSimCardDialog("Failed");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globalActivityContext = LoginActivity.this;
        mActivityLoginBinding = getViewDataBinding();
        try {
            mLoginViewModel.setNavigator(this);
            activity = LoginActivity.this;
            logScreenNameInGoogleAnalytics(TAG, this);

            // Check Esper SDK Activated or Not:-
            esperDeviceSDK.isActivated(new EsperDeviceSDK.Callback<Boolean>() {
                @Override
                public void onResponse(Boolean active) {

                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    Logger.e("LoginActivity", String.valueOf(t));
                }
            });

            setBottomText();
            if (AutoStartPermissionHelper.getInstance().isAutoStartPermissionAvailable(this)) {
                AutoStartPermissionHelper.getInstance().getAutoStartPermission(this);
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }

        mLoginViewModel.getDataManager().setCheckAttendanceLoginStatus(false);
    }

    @SuppressLint("SetTextI18n")
    public void showNOTPDialog() {
        dialog = new Dialog(this, R.style.RoundedCornersDialog);
        dialog.setContentView(R.layout.dialog_n_otp);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        TextView txtnotp = dialog.findViewById(R.id.txtnotp);
        txtnotp.setVisibility(View.VISIBLE);

        ImageView crossDialog = dialog.findViewById(R.id.crssdialog);
        Glide.with(getApplicationContext()).load(R.drawable.caller).asGif().fitCenter().override(300, 300).into(crossDialog);

        Timer customTimer = new Timer();
        customTimer.setTimerListener(time -> txtnotp.setText("Please expect the verification call in " + time / 1000 + " seconds."));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void setBottomText() {
        try {
            String text = mLoginViewModel.getCopyRightText();
            if (text != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mActivityLoginBinding.tvCopyright.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    mActivityLoginBinding.tvCopyright.setText(Html.fromHtml(text));
                }
            } else
                mActivityLoginBinding.tvCopyright.setText(Html.fromHtml(getString(R.string.copyright_txt)));
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dialog != null) {
            dialog.dismiss();
        }
        initializeVerification();
        try {
            stopService(SyncServicesV2.getStopIntent(context));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                mLoginViewModel.decideNextActivity();
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (!checkPermission(Constants.permissionsupperN)) {
                        requestPermission();
                    } else {
                        deviceDetails.setDeviceId(CommonUtils.getImei(getApplicationContext()));
                        SathiLocationService.startLocationUpdates(context, mLoginViewModel.getDataManager());
                    }
                } else {
                    if (!checkPermission(Constants.permissionsbelowN)) {
                        requestPermissionBelowN();
                    } else {
                        deviceDetails.setDeviceId(CommonUtils.getImei(getApplicationContext()));
                        SathiLocationService.startLocationUpdates(context, mLoginViewModel.getDataManager());
                    }
                }
            } else {
                deviceDetails.setDeviceId(CommonUtils.getImei(getApplicationContext()));
                mLoginViewModel.decideNextActivity();
            }
        } catch (Exception e) {
            showSnackbar(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void requestPermission() {
        requestPermissionsSafely(Constants.permissionsupperN, 100);
        mLoginViewModel.getDataManager().setLoginPermission(true);
    }

    private void requestPermissionBelowN() {
        requestPermissionsSafely(Constants.permissionsbelowN, 100);
        mLoginViewModel.getDataManager().setLoginPermission(true);
    }

    @Override
    public boolean hasPermission(String permission) {
        return super.hasPermission(permission);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0, len = permissions.length; i < len; i++) {
            String permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                // User Rejected The Permission
                boolean showRationale = shouldShowRequestPermissionRationale(permission);
                if (!showRationale) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, 1);
                }
            } else if (checkPermission(permissions)) {
                deviceDetails.setDeviceId(CommonUtils.getImei(getApplicationContext()));
                mLoginViewModel.decideNextActivity();
            } else {
                showSnackbar(getString(R.string.permission_required));
            }
        }
    }

    private boolean checkPermission(String[] permissions) {
        for (String permission : permissions) {
            if (!hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public LoginViewModel getViewModel() {
        return mLoginViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void showErrorMessage(boolean status) {
        if (status) {
            showSnackbar(getString(R.string.http_500_msg));
        } else {
            showSnackbar(getString(R.string.server_down_msg));
        }
    }

    @Override
    public void noAPKUpdate() {
        showSnackbar("Download URL cannot be blank. Please contact server Admin");
    }

    @Override
    public Activity getActivityActivity() {
        return this;
    }

    @Override
    public void showError(String message) {
        showSnackbar(message);
    }

    @Override
    public void onNOTPNext(String status) {
        if (status.equalsIgnoreCase("Success")) {
            mLoginViewModel.getDataManager().updateUserLoggedInState(IDataManager.LoggedInMode.LOGGED_IN_MODE_SERVER);
            openDashboardActivity(true);
        } else {
            Intent intent = new Intent(LoginActivity.this, LoginVerifyOtpActivity.class);
            intent.putExtra("flag", flag);
            intent.putExtra("manualOtpFlag", manualOtpFlag);
            startActivity(intent);
        }
    }

    @Override
    public void onServerLogin() {
        hideKeyboard(LoginActivity.this);
        try {
            if (mLoginViewModel.isEmailAndPasswordValid(mActivityLoginBinding.etEmail.getText().toString(), mActivityLoginBinding.etPassword.getText().toString())) {
                if (isNetworkConnected()) {
                    HashMap<String, String> attributes = new HashMap<>();
                    attributes.put("user_id", mActivityLoginBinding.etEmail.getText().toString());
                    Shield.getInstance().setDeviceResultStateListener(() -> Shield.getInstance().sendAttributes("Login", attributes));
                    mLoginViewModel.login(LoginActivity.this, mActivityLoginBinding.etEmail.getText().toString().toUpperCase(), mActivityLoginBinding.etPassword.getText().toString(), deviceDetails);
                } else {
                    showSnackbar(getString(R.string.check_internet));
                }
            } else {
                if (mActivityLoginBinding.etEmail.getText().toString().isEmpty() || mActivityLoginBinding.etPassword.getText().toString().isEmpty()) {
                    showSnackbar(getString(R.string.login_error));
                }
            }
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void onForgetPassword() {
        if (isNetworkConnected()) {
            ForgetActivity forgetActivity = ForgetActivity.newInstance(LoginActivity.this);
            forgetActivity.show(getSupportFragmentManager());
        } else {
            showSnackbar(getString(R.string.check_internet));
        }
    }

    @Override
    public void openDashboardActivity(boolean isAPICall) {
        if (mLoginViewModel.getDataManager().getSathiAttendanceFeatureEnable() && isAPICall) {
            mLoginViewModel.checkAttendance(LoginActivity.this, mLoginViewModel.getDataManager().getEmp_code());
        } else {
            if (mLoginViewModel.getDataManager().getDEFAULTSTATISTICS().equalsIgnoreCase("true")) {
                startActivity(PerformanceActivity.getStartIntent(this));
                applyTransitionToOpenActivity(this);
            } else {
                if (mLoginViewModel.getDataManager().getScreenStatus()) {
                    Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                    startActivity(intent);
                } else {
                    startActivity(DashboardActivity.getStartIntent(this));
                }
                applyTransitionToOpenActivity(this);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onHandleError(String errorResponse) {
        showSnackbar(errorResponse);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onHandleProgressiveTimer(String response) {
        if (mLoginViewModel.getDataManager().getProgressiveTimer() == 1 || mLoginViewModel.getDataManager().getProgressiveTimer() == 2) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                onHandleError(response);
            }
        } else if (mLoginViewModel.getDataManager().getProgressiveTimer() == 3) {
            mActivityLoginBinding.btnServerLogin.setText("00:10");
            otpTimer = new CounterClass(10000, 1000);
            mActivityLoginBinding.btnServerLogin.setEnabled(false);
            otpTimer.start();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                onHandleError(response);
            }
        } else if (mLoginViewModel.getDataManager().getProgressiveTimer() == 4) {
            mActivityLoginBinding.btnServerLogin.setText("00:20");
            otpTimer = new CounterClass(20000, 1000);
            mActivityLoginBinding.btnServerLogin.setEnabled(false);
            otpTimer.start();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                onHandleError(response);
            }
        } else if (mLoginViewModel.getDataManager().getProgressiveTimer() == 5) {
            mActivityLoginBinding.btnServerLogin.setText("00:60");
            otpTimer = new CounterClass(60000, 1000);
            mActivityLoginBinding.btnServerLogin.setEnabled(false);
            otpTimer.start();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                onHandleError(response);
            }
        } else if (mLoginViewModel.getDataManager().getProgressiveTimer() >= 6) {
            mActivityLoginBinding.btnServerLogin.setText("05:00");
            otpTimer = new CounterClass(300000, 1000);
            mActivityLoginBinding.btnServerLogin.setEnabled(false);
            otpTimer.start();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                onHandleError(response);
            }
        }
    }

    @Override
    public void onSuccess() {
        try {
            if (!mLoginViewModel.isOTPRequiredTrue()) {
                openDashboardActivity(true);
            }
        } catch (Exception e) {
            showSnackbar(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void onPinClick() {
    }

    private Activity activity;

    @Override
    public void showAPKForceUpdate(LoginResponse.APKUpdateResponse apkUpdateResponse) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
        String message = apkUpdateResponse.getApk_version_message() != null ? apkUpdateResponse.getApk_version_message() : "Please update your current application.";
        AlertDialog dialog = alertDialog.setMessage(message).setTitle("App Update").setPositiveButton("Update", (dialogInterface, i) -> startDownload(apkUpdateResponse)).create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void startDownload(LoginResponse.APKUpdateResponse apkUpdateResponse) {
        try {
            mLoginViewModel.downloadAPK(apkUpdateResponse.getApk_url(), LoginActivity.this, esperDeviceSDK, esperSDKActivated);
        } catch (Exception e) {
            showSnackbar(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void showAPKSoftUpdate(LoginResponse loginResponse, LoginResponse.APKUpdateResponse apkUpdateResponse) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
        String message = apkUpdateResponse.getApk_version_message() != null ? apkUpdateResponse.getApk_version_message() : "Please update your current application.";
        AlertDialog dialog = alertDialog.setMessage(message).setTitle("App Update").setPositiveButton("Update", (dialogInterface, i) -> startDownload(apkUpdateResponse)).setNegativeButton("Later", ((dialogInterface, i) -> {
            if (loginResponse.getSResponse().isOtpRequired()) {
                startLoginVerifyOTPActivity();
                return;
            }
            openDashboardActivity(true);
        })).create();
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    public void showChangePassword() {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Light_Dialog);
            String message = getString(R.string.message_pass_expired);
            AlertDialog dialog = alertDialog.setMessage(message).setTitle(getString(R.string.title_pass_expired)).setPositiveButton(getString(R.string.Continue), (dialog1, which) -> {
                changePasswordActivity = ChangePasswordActivity.newInstance();
                changePasswordActivity.show(getSupportFragmentManager());
                changePasswordActivity.setChangePasswordListener(() -> {
                    clearStack();
                    finish();
                });
            }).create();
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception e) {
            showSnackbar(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void clearStack() {
        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void showVerifyOtp(boolean flag, boolean manualOtpFlag) {
        this.flag = flag;
        this.manualOtpFlag = manualOtpFlag;
        TelephonyManager telMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
            case TelephonyManager.SIM_STATE_UNKNOWN:
                showSimCardDialog("Sim");
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                break;
            case TelephonyManager.SIM_STATE_READY:
                try {
                    eVerification.startVerification(new LoginActivity.VerifyListener(), "+91" + mLoginViewModel.getDataManager().getMobile(), 10);
                } catch (VerificationAlreadyInProgressException e) {
                    Logger.e(TAG, String.valueOf(e));
                }
                break;
        }
    }

    @Override
    public void showProgress() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(LoginActivity.this, "", "Please wait...");
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    @Override
    public void doLogout(String message) {
        try {
            runOnUiThread(() -> {
                try {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                }
            });
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void startLoginVerifyOTPActivity() {
        startActivity(LoginVerifyOtpActivity.getStartIntent(LoginActivity.this));
        applyTransitionToOpenActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
        builder.setCancelable(false);
        builder.setMessage("Do you want to exit?");
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            // If user pressed "yes", then he is allowed to exit from application
            finishAffinity();
        });
        builder.setNegativeButton(R.string.no, (dialog, which) -> {
            // If user select "No", just startTripSyncDrs this dialog and continue with app
            dialog.cancel();
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void initializeVerification() {
        try {
            String accountSid = "ecomexpress2";
            String notpAppId = "c72c739449994d1490941e6fda37af6c";
            String appSecret = "royamogameca";
            Config config = new ConfigBuilder(notpAppId, appSecret, accountSid, getApplicationContext()).Build();
            eVerification = new ExotelVerification(config);
        } catch (PermissionNotGrantedException | ConfigBuilderException e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @SuppressLint("SetTextI18n")
    public void showSimCardDialog(String checkStatus) {
        if (dialog != null) {
            dialog.dismiss();
        }
        Dialog dialog = new Dialog(this, R.style.RoundedCornersDialog);
        dialog.setContentView(R.layout.dialog_notp_message);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        Button sms = dialog.findViewById(R.id.bt_sms);
        Button call = dialog.findViewById(R.id.bt_call);
        TextView txtStat = dialog.findViewById(R.id.txtstat);
        if (checkStatus.equalsIgnoreCase("Failed")) {
            txtStat.setText("Your auto verification is Failed!\n" + "Do you want to verify through secure code?");
        }

        sms.setOnClickListener(v -> {
            dialog.dismiss();
            if (checkStatus.equalsIgnoreCase("Failed")) {
                mLoginViewModel.verifyOtp("Failed");
            } else {
                Intent intent = new Intent(LoginActivity.this, LoginVerifyOtpActivity.class);
                intent.putExtra("flag", flag);
                intent.putExtra("manualOtpFlag", manualOtpFlag);
                startActivity(intent);
                applyTransitionToOpenActivity(this);
            }
        });

        call.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public class CounterClass extends CountDownTimer {
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            try {
                mActivityLoginBinding.btnServerLogin.setText(R.string.text_login);
                mActivityLoginBinding.btnServerLogin.setEnabled(true);
                mActivityLoginBinding.btnServerLogin.setTextColor(Color.parseColor("#ffffff"));

            } catch (Exception e) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    onHandleError(getString(R.string.http_500_msg));
                }
                Logger.e(TAG, String.valueOf(e));
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            try {
                @SuppressLint("DefaultLocale") String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                System.out.println(hms);
                mActivityLoginBinding.btnServerLogin.setTextColor(Color.parseColor("#ffffff"));
                mActivityLoginBinding.btnServerLogin.setText(hms);
            } catch (Exception e) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    onHandleError(getString(R.string.http_500_msg));
                }
                Logger.e(TAG, String.valueOf(e));
            }
        }
    }

    @Override
    public void showAlertToUpdateDateTime() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog);
        String AlertText1 = "Attention : ";
        builder.setMessage(AlertText1 + getString(R.string.commit_restriction_msg)).setCancelable(false).setPositiveButton("OK", (dialog, id) -> {
            dialog.cancel();
            startActivityForResult(new Intent(Settings.ACTION_DATE_SETTINGS), 0);
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}