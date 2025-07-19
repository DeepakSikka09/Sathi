package in.ecomexpress.sathi.ui.dashboard.global_activity;

import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class GlobalDialogActivity extends AppCompatActivity {

    private final String TAG = GlobalDialogActivity.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    public static Activity finishActivityFlag;
    TextView tv_msg, tv_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        logScreenNameInGoogleAnalytics(TAG, this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_global_dialog);
        tv_msg = findViewById(R.id.tv_msg);
        tv_ok = findViewById(R.id.tv_ok);
        try{
            boolean AppSettingAlert = getIntent().getBooleanExtra("AppSettingAlert", false);
            String type = getIntent().getStringExtra("FlagType");
            if(type == null){
                type = "";
            }
            if(AppSettingAlert){
               appSettingAlert(this);
            } else {
                if(type.equalsIgnoreCase("FakeLocation")){
                    tv_msg.setText(R.string.fakelocationmsg);
                    Constants.IS_USING_FAKE_GPS = 1;
                    setFinishOnTouchOutside(false);
                    tv_ok.setVisibility(View.GONE);
                    finishActivityFlag = this;
                }
            }
            if(type.equalsIgnoreCase("GPS")){
                tv_msg.setText(R.string.GPSONOFF);
                tv_ok.setVisibility(View.VISIBLE);
                setFinishOnTouchOutside(false);
                finishActivityFlag = this;
            }
            if(type.equalsIgnoreCase("MultiSpace")){
                tv_msg.setText(R.string.multi_space);
                tv_ok.setVisibility(View.GONE);
                setFinishOnTouchOutside(false);
                finishActivityFlag = this;
            }
        } catch(Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
        tv_ok.setOnClickListener(v -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)));
    }

    public void appSettingAlert(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
        builder.setMessage("You need to check your application settings.");
        builder.setPositiveButton("Ok", (dialog, which) -> {
            dialog.dismiss();
            finish();
        });
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }
}