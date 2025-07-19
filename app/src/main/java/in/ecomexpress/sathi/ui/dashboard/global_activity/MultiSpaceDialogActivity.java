package in.ecomexpress.sathi.ui.dashboard.global_activity;

import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class MultiSpaceDialogActivity extends AppCompatActivity {

    private final String TAG = MultiSpaceDialogActivity.class.getSimpleName();
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
            String type = getIntent().getStringExtra("FlagType");
            if(type == null){
                type = "";
            }
            if(type.equalsIgnoreCase("MultiSpace")){
                tv_msg.setText(R.string.multi_space);
                tv_ok.setVisibility(View.GONE);
                setFinishOnTouchOutside(false);
                finishActivityFlag = this;
            }
            if(type.equalsIgnoreCase("DeveloperModeEnable")){
                tv_msg.setText(R.string.developer_mode_enable);
                tv_ok.setVisibility(View.GONE);
                setFinishOnTouchOutside(false);
                finishActivityFlag = this;
            }
        } catch(Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
        tv_ok.setOnClickListener(v -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)));
    }
}