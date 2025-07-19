package in.ecomexpress.sathi.ui.drs.forward.obd.activity;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.databinding.FwdObdCommitActivityBinding;
import in.ecomexpress.sathi.ui.drs.todolist.ToDoListActivity;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.GlobalConstant;

@AndroidEntryPoint
public class FwdOBDSucessActivity extends AppCompatActivity {

    private final String TAG = FwdOBDSucessActivity.class.getSimpleName();
    private FwdObdCommitActivityBinding commitActivityBinding;
    String awbNo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        commitActivityBinding = FwdObdCommitActivityBinding.inflate(getLayoutInflater());
        setContentView(commitActivityBinding.getRoot());
        logScreenNameInGoogleAnalytics(TAG, this);

        awbNo = getIntent().getStringExtra(Constants.OBD_AWB_NUMBER);
        commitActivityBinding.awb.awb.setText("AWB: "+awbNo);
        commitActivityBinding.btnVerify.setOnClickListener(view -> {
            Intent intent = new Intent(FwdOBDSucessActivity.this, ToDoListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtra(ToDoListActivity.ITEM_MARKED, awbNo);
            intent.putExtra(ToDoListActivity.SHIPMENT_TYPE, GlobalConstant.ShipmentTypeConstants.FWD);
            intent.putExtra(ToDoListActivity.SHIPMENT_STATUS, Constants.SHIPMENT_DELIVERED_STATUS);
            startActivity(intent);
            finish();
            applyTransitionToOpenActivity(this);
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        CommonUtils.showCustomSnackbar(commitActivityBinding.getRoot(),"You Can Not Move Back", this);
    }
}