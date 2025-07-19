package in.ecomexpress.sathi.ui.dummy.eds.eds_otp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.CountDownTimer;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityEdsOtpBinding;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.dummy.eds.eds_task_list.EdsTaskListActivity;
import in.ecomexpress.sathi.ui.dummy.eds.undeilvered_eds.EDSUndeliveredActivity;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;
import in.ecomexpress.sathi.utils.Constants;

/**
 * Created by dhananjayk on 05-12-2018.
 */

@AndroidEntryPoint
public class EdsOtpActivity extends BaseActivity<ActivityEdsOtpBinding, EdsOtpViewModel> implements IEdsOtpNavigator {
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    String TAG = EdsOtpActivity.class.getCanonicalName();
    @Inject
    EdsOtpViewModel edsOtpViewModel;
    ActivityEdsOtpBinding activityEdsOtpBinding;
    long awbNo;
    int drs_no;
    boolean reschedule_enable;
   private String compoisite_key=null;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, EdsOtpActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEdsOtpBinding = getViewDataBinding();
        edsOtpViewModel.setNavigator(this);
        awbNo = getIntent().getLongExtra(Constants.INTENT_KEY, 0);
        drs_no = getIntent().getIntExtra(Constants.DRS_ID, 0);
        reschedule_enable=getIntent().getBooleanExtra(Constants.RESCHEDULE_ENABLE,false);
        compoisite_key=getIntent().getExtras().getString(Constants.COMPOSITE_KEY,"");
        edsOtpViewModel.getEdsListTask(compoisite_key);
        if (checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of  permissions  granted.
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.eds));
        }

        activityEdsOtpBinding.mobileEdt.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
        activityEdsOtpBinding.otpEtKyc.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});

    }

    @Override
    public EdsOtpViewModel getViewModel() {
        return edsOtpViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_eds_otp;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // imageHandler.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    private boolean checkAndRequestPermissions() {
        try {

        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);

        int receiveSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);

        int readSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_MMS);
        }
        if (readSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }

        }catch (Exception e){
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
        return true;
    }

    @Override
    public void onVerifyOtp() {
        try {
        if (activityEdsOtpBinding.otpEtKyc.getText().toString().trim().isEmpty()) {
            onUiThread("OTP field cannot be blank");
        } else if (activityEdsOtpBinding.otpEtKyc.getText().toString().length() == 6)
            edsOtpViewModel.onVerifyOTP(activityEdsOtpBinding.otpEtKyc.getText().toString(), awbNo);
        else {
            onUiThread("Please Enter Valid OTP");

        }
    }catch (Exception e){
        e.printStackTrace();
        showSnackbar(e.getMessage());
    }
    }

    @Override
    public void onOtherMobile(int count) {
        EdsOtpActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                switch (count) {
                    case 1:
                        activityEdsOtpBinding.otpLayout.setVisibility(View.GONE);
                        activityEdsOtpBinding.changeNoLayout.setVisibility(View.VISIBLE);
                        activityEdsOtpBinding.mobileEdt.setText("");
                        activityEdsOtpBinding.otpEtKyc.setText("");
                        break;
                    case 2:
                        activityEdsOtpBinding.changeNoLayout.setVisibility(View.GONE);
                        activityEdsOtpBinding.otpLayout.setVisibility(View.VISIBLE);
                        activityEdsOtpBinding.mobileEdt.setText("");
                        activityEdsOtpBinding.otpEtKyc.setText("");
                        break;
                }

            }
        });

    }

    @Override
    public void onCancelActivity() {
        Intent intent;
        intent = new Intent(this, EDSUndeliveredActivity.class);
        intent.putParcelableArrayListExtra(getString(R.string.data), null);
        intent.putExtra("edsResponse", edsOtpViewModel.edsWithActivityList().edsResponse);
        intent.putExtra(Constants.RESCHEDULE_ENABLE,reschedule_enable);
        intent.putExtra(Constants.COMPOSITE_KEY,compoisite_key);
        intent.putExtra("awb", awbNo);
        intent.putExtra("navigator", "otp");
        startActivity(intent);
    }

    @Override
    public void onNext(String description) {
        onUiThread(description);
        Intent intent = new Intent(this, EdsTaskListActivity.class);
        intent.putExtra(Constants.RESCHEDULE_ENABLE,reschedule_enable);
        intent.putExtra(Constants.INTENT_KEY, awbNo);
        intent.putExtra(Constants.COMPOSITE_KEY,compoisite_key);
        intent.putExtra(Constants.DRS_ID, drs_no);
        startActivity(intent);
        this.finish();

    }

    @Override
    public void onResendOtp() {
        new CountDownTimer(edsOtpViewModel.getDataManager().getDisableResendOtpButtonDuration() * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished){
                activityEdsOtpBinding.resend.setEnabled(false);
                activityEdsOtpBinding.resend.setTextColor(getResources().getColor(R.color.light_gray));
                String hms = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                                .toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                                .toMinutes(millisUntilFinished)));
                System.out.println(hms);
                activityEdsOtpBinding.resend.setText(hms);
            }

            @Override
            public void onFinish(){
                activityEdsOtpBinding.resend.setEnabled(true);
                activityEdsOtpBinding.resend.setText(getResources().getString(R.string.resend));
                edsOtpViewModel.onResendOTP(awbNo,String.valueOf(drs_no));
            }
        }.start();

    }

    @Override
    public void onResendNext(String description) {
        if (!isNetworkConnected()) {
            onUiThread("Please Check Internet Connection");
        } else
            onUiThread(description);

    }

    public void onUiThread(String description) {
        try {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(EdsOtpActivity.this, description, Toast.LENGTH_LONG).show();
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onMobileNoChange() {
        if (activityEdsOtpBinding.mobileEdt.getText().toString().length() == 10)
            edsOtpViewModel.onsendOtpOnOtherNo(activityEdsOtpBinding.mobileEdt.getText().toString(), awbNo);
        else {
            onUiThread("Invalid Mobile No.");
            // Toast.makeText(EdsOtpActivity.this, "Invalid Mobile No.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void sendMsg(String description) {
        if (!isNetworkConnected()) {
            onUiThread("Please Check Internet Connection");
        } else
            onUiThread(description);


    }

    @Override
    public void onMaxAttempt() {
        EdsOtpActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                if (isNetworkConnected()) {
                    try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EdsOtpActivity.this,R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
                    builder.setCancelable(false);
                    builder.setMessage("Max Attempt Failed.You are being navigated to Undelivered Page.");
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent;
                            intent = new Intent(EdsOtpActivity.this, EDSUndeliveredActivity.class);
                            intent.putParcelableArrayListExtra(getString(R.string.data), null);
                            intent.putExtra("edsResponse", edsOtpViewModel.edsWithActivityList().edsResponse);
                            intent.putExtra(Constants.RESCHEDULE_ENABLE,reschedule_enable);
                            intent.putExtra(Constants.COMPOSITE_KEY,compoisite_key);
                            intent.putExtra("awb", awbNo);
                            intent.putExtra("navigator", "otp");
                            startActivity(intent);
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }catch (Exception e){
                    e.printStackTrace();
                    showSnackbar(e.getMessage());
                }
                } else {
                    onUiThread("Please Check Internet Connection");
                }

            }

        });

    }

    @Override
    public void onShowMsg(String description) {
        if (!isNetworkConnected()) {
            onUiThread("Please Check Internet Connection");
        } else
            onUiThread(description);

    }

    @Override
    public void backClick() {
        super.onBackPressed();

    }

    @Override
    public void clearStack() {
        Intent intent = new Intent(EdsOtpActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    @Override
    public void showErrorMessage(boolean status) {
        if (status) {
            if (!isNetworkConnected()) {
                onUiThread("Please Check Internet Connection");
            } else
                onUiThread(getString(R.string.http_500_msg));
        } else {
            if (!isNetworkConnected()) {
                onUiThread("Please Check Internet Connection");
            } else
                onUiThread(getString(R.string.server_down_msg));
        }
    }

    @Override
    public void showError(String e) {
        showSnackbar(e);
    }

    @Override
    public void doLogout(String message) {

        showToast(getString(R.string.session_expire));
        edsOtpViewModel.logoutLocal();
        /*Intent intent = new Intent(DashboardActivity.this, SyncLiveTrackingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);*/
    }


}
