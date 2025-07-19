package in.ecomexpress.sathi.ui.drs.forward.mps;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.nlscan.android.scan.ScanManager;
import com.nlscan.android.scan.ScanSettings;
import java.util.ArrayList;
import java.util.Locale;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityFwdScanAwbBinding;
import in.ecomexpress.sathi.repo.local.data.activitiesdata.FWDActivitiesData;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.SecureDelivery;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.drs.forward.details.ForwardDetailActivity;
import in.ecomexpress.sathi.ui.drs.forward.undelivered_fwd.UndeliveredActivity;
import in.ecomexpress.sathi.ui.drs.todolist.ToDoListViewModel;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class MPSScanActivity extends BaseActivity<ActivityFwdScanAwbBinding, MPSScanViewModel> implements MPSScanNavigator {

    private final String TAG = MPSScanActivity.class.getSimpleName();
    @Inject
    MPSScanViewModel mpsScanViewModel;
    private ActivityFwdScanAwbBinding activityFwdScanAwbBinding;
    private String getDrsApiKey = "";
    private String getDrsPstnKey = "";
    private String getDrsPin = "";
    private String shipmentType = "";
    private String composite_key = "";
    private long awbNo;
    ArrayList<MPSShipment> mpsShipments = new ArrayList<>();
    @Inject
    ScannerItemAdapter scannerItemAdapter;
    @Inject
    ForwardCommit forwardCommit;
    private SecureDelivery secureDelivery;
    private boolean is_amazon_schedule_enable;
    private int counter;
    private boolean isDigital;
    String order_id = "";
    boolean call_allowed;
    String  consignee_mobile, consignee_alternate_mobile = "";

    boolean sign_image_required = false;
    String fwd_del_image = "";

    public String device = (Build.MANUFACTURER + ":" + Build.MODEL).toUpperCase(Locale.US);
    private ScanManager mScanMgr;
    private BroadcastReceiver mReceiver;
    int drs_id_num = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mpsScanViewModel.setNavigator(this);
            this.activityFwdScanAwbBinding = getViewDataBinding();
            activityFwdScanAwbBinding.btnProceed.setText(getString(R.string.scan));
            logScreenNameInGoogleAnalytics(TAG, this);

            secureDelivery = getIntent().getParcelableExtra(Constants.SECURE_DELIVERY);
            awbNo = getIntent().getLongExtra(Constants.INTENT_KEY, 0);
            drs_id_num = getIntent().getIntExtra(Constants.DRS_ID_NUM, 0);
            sign_image_required = getIntent().getBooleanExtra(Constants.sign_image_required, false);

            if (getIntent().getStringExtra(Constants.FWD_DEL_IMAGE) != null)
            {
                fwd_del_image = getIntent().getStringExtra(Constants.FWD_DEL_IMAGE);
            }

            isDigital = getIntent().getExtras().getBoolean(Constants.IS_CARD);
            order_id = getIntent().getStringExtra(Constants.ORDER_ID);
            getDrsPin = getIntent().getExtras().getString(Constants.DRS_PIN);
            consignee_mobile = getIntent().getExtras().getString(Constants.CONSIGNEE_MOBILE);
            consignee_alternate_mobile = getIntent().getExtras().getString(Constants.CONSIGNEE_ALTERNATE_MOBILE);
            call_allowed = getIntent().getBooleanExtra("call_allowed", false);
            is_amazon_schedule_enable = getIntent().getBooleanExtra(Constants.IS_AMAZON_RESHEDUCLE_ENABLE, false);
            shipmentType = getIntent().getExtras().getString(Constants.SHIPMENT_TYPE);
            composite_key = String.valueOf(getIntent().getStringExtra(Constants.COMPOSITE_KEY));
            String awbsNos = getIntent().getExtras().getString(Constants.MPS_AWB_NOS);
            awbsNos = awbsNos.substring(1);
            awbsNos = awbsNos.substring(0, awbsNos.length() - 1);
            String[] awbArray = awbsNos.split(",");
            for (String awbNo : awbArray) {
                MPSShipment mpsShipment = new MPSShipment();
                mpsShipment.awbNo = awbNo;
                mpsShipments.add(mpsShipment);
            }
            initView();
            activityFwdScanAwbBinding.awb.setText(String.valueOf(awbNo));
            mpsScanViewModel.getShipmentData(forwardCommit, composite_key.trim(), awbNo);
            if (getIntent().getExtras().getString(Constants.DRS_API_KEY) != null) {
                getDrsApiKey = getIntent().getExtras().getString(Constants.DRS_API_KEY);
            }
            if (getIntent().getExtras().getString(Constants.DRS_PSTN_KEY) != null) {
                getDrsPstnKey = getIntent().getExtras().getString(Constants.DRS_PSTN_KEY);
            }
        } catch (Exception e) {
            Logger.e(MPSScanActivity.class.getName(), e.getMessage());

            showSnackbar(e.getMessage());
        }

        activityFwdScanAwbBinding.header.awb.setText(R.string.scan_shipments);
        activityFwdScanAwbBinding.header.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBack();
            }
        });

    }

    private void initView() {
        activityFwdScanAwbBinding.scannerRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        activityFwdScanAwbBinding.scannerRecyclerView.setItemAnimator(new DefaultItemAnimator());
        activityFwdScanAwbBinding.scannerRecyclerView.setAdapter(scannerItemAdapter);
        scannerItemAdapter.setData(mpsShipments);
        scannerItemAdapter.notifyDataSetChanged();
    }

    public static Intent getStartIntent(Context context) {
        return new Intent(context, MPSScanActivity.class);
    }

    @Override
    public MPSScanViewModel getViewModel() {
        return mpsScanViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_fwd_scan_awb;
    }

    /*on undelivered click*/
    @Override
    public void unDelivered(ForwardCommit forwardCommit) {
        FWDActivitiesData fwdActivitiesData = new FWDActivitiesData();
        fwdActivitiesData.setMps_undelivered("true");
        fwdActivitiesData.setDrsPin(getDrsPin);
        fwdActivitiesData.setDrsApiKey(getDrsApiKey);
        fwdActivitiesData.setDrsPstnKey(getDrsPstnKey);
        fwdActivitiesData.setOrderId(order_id);
        fwdActivitiesData.setCallAllowed(call_allowed);
        fwdActivitiesData.setConsignee_mobile(consignee_mobile);
        fwdActivitiesData.setConsignee_alternate_number(consignee_alternate_mobile);
        fwdActivitiesData.setDrsId(drs_id_num);
        fwdActivitiesData.setAwbNo(awbNo);
        fwdActivitiesData.setCompositeKey(composite_key);
        fwdActivitiesData.setIs_amazon_reschedule_enabled(is_amazon_schedule_enable);
        fwdActivitiesData.setShipment_type(shipmentType);
        fwdActivitiesData.setSecure_undelivered(String.valueOf(secureDelivery.getOTP()));
        fwdActivitiesData.setCollected_value("0");

        Intent intent = UndeliveredActivity.getStartIntent(MPSScanActivity.this);
        intent.putExtra(getString(R.string.data), forwardCommit);
        intent.putExtra("fwdActivitiesData", fwdActivitiesData);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        applyTransitionToBackFromActivity(this);
    }


    public void onBack() {
        super.onBackPressed();
        applyTransitionToBackFromActivity(this);
    }

    //proceed to next screen
    @Override
    public void onProceed() {
        if (activityFwdScanAwbBinding.btnProceed.getText().toString().equalsIgnoreCase(getString(R.string.proceed))) {

            Intent intent = null;
            if (secureDelivery != null) {
                if (secureDelivery.getPinb() == false && secureDelivery.getOTP() == false && secureDelivery.getSecure_pin() == false) {
                    intent = ForwardDetailActivity.getStartIntent(MPSScanActivity.this);
                } else {
                    intent = ForwardDetailActivity.getStartIntent(MPSScanActivity.this);
                }
            }

            FWDActivitiesData fwdActivitiesData = new FWDActivitiesData();
            fwdActivitiesData.setSecure_delivery(secureDelivery);
            fwdActivitiesData.setShipment_type(Constants.FWD);
            fwdActivitiesData.setDrsPstnKey(getDrsPstnKey);
            fwdActivitiesData.setDrsApiKey(getDrsApiKey);
            fwdActivitiesData.setDrsPin(getDrsPin);
            fwdActivitiesData.setCard(isDigital);
            fwdActivitiesData.setAwbNo(awbNo);
            fwdActivitiesData.setCallAllowed(call_allowed);
            fwdActivitiesData.setOrderId(order_id);
            fwdActivitiesData.setSign_image_required(sign_image_required);
            fwdActivitiesData.setResend_otp_enable(false);
            fwdActivitiesData.setCompositeKey(composite_key);
            fwdActivitiesData.setDrsId(drs_id_num);
            fwdActivitiesData.setShow_fwd_undl_btn("No");
            fwdActivitiesData.setConsignee_alternate_number(consignee_alternate_mobile);
            fwdActivitiesData.setReturn_package_barcode("");
            fwdActivitiesData.setScanValue("");
            fwdActivitiesData.setIs_amazon_reschedule_enabled(is_amazon_schedule_enable);
            fwdActivitiesData.setFwd_del_image(fwd_del_image);


            intent.putExtra("fwdActivitiesData", fwdActivitiesData);
            startActivity(intent);
            applyTransitionToOpenActivity(this);

        } else {
            if (!CommonUtils.isAllPermissionAllow(this)) {
                openSettingActivity();
                return;
            }
            if (device.equalsIgnoreCase(Constants.NEWLAND) || device.equalsIgnoreCase(Constants.NEWLAND_90) || device.equalsIgnoreCase(Constants.NEWLAND_DROI)) {
                mScanMgr = ScanManager.getInstance();
                mScanMgr.startScan();
                mScanMgr.disableBeep();
                mScanMgr.setScanEnable(true);
                mScanMgr.setOutpuMode(ScanSettings.Global.VALUE_OUT_PUT_MODE_BROADCAST);
                IntentFilter intFilter = new IntentFilter(ScanManager.ACTION_SEND_SCAN_RESULT);
                registerReceiver(mResultReceiver(), intFilter);

            } else {
                ScanOptions options = new ScanOptions();
                options.setPrompt(getString(R.string.scan_a_barcode_or_qr_code));
                options.setBeepEnabled(true);
                options.setTorchEnabled(true);
                options.setOrientationLocked(true);
                options.setCaptureActivity(CaptureActivity.class);
                barLauncher.launch(options);
            }
        }
    }

    private BroadcastReceiver mResultReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ScanManager.ACTION_SEND_SCAN_RESULT.equals(action)) {
                    String scannedValue = intent.getStringExtra("SCAN_BARCODE1");
                    if (scannedValue != null && !scannedValue.isEmpty()) {
                        handleScanResult(scannedValue);
                    }
                }
            }
        };
        return mReceiver;
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            handleScanResult(result.getContents());
        }
    });


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (device.equalsIgnoreCase(Constants.NEWLAND) || device.equalsIgnoreCase(Constants.NEWLAND_90) || device.equalsIgnoreCase(Constants.NEWLAND_DROI)) {
                mScanMgr.stopScan();
                unregisterReceiver(mReceiver);
            }
        } catch (Exception e) {
            Logger.e(MPSScanActivity.class.getName(), e.getMessage());
        }
    }


    @Override
    public void onErrorMsg(String e) {
        showSnackbar(e);
    }

    private void handleScanResult(String data) {
        boolean isValidAwb = true;
        if (data != null) {
            String awbNumber = data;
            boolean rightItemScanned = false;
            for (int i = 0; i < mpsShipments.size(); i++) {
                if (mpsShipments.get(i).awbNo.contains(awbNumber)) {
                    isValidAwb = false;
                }
            }
            if (isValidAwb) {
                Toast.makeText(getApplicationContext(), getString(R.string.wrong_awb), Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i = 0; i < mpsShipments.size(); i++) {
                if (mpsShipments.get(i).awbNo.contains(awbNumber) && !mpsShipments.get(i).scanned) {
                    mpsShipments.get(i).scanned = true;
                    counter++;
                    rightItemScanned = true;
                    break;
                }
            }
            if (!rightItemScanned) {
                showSnackbar(getString(R.string.scanmismatch));
            }
            if (mpsShipments.size() == counter) {
                activityFwdScanAwbBinding.btnProceed.setBackgroundResource(R.drawable.fwd_button);
                activityFwdScanAwbBinding.btnProceed.setText(getString(R.string.proceed));
                activityFwdScanAwbBinding.btnProceed.setEnabled(true);
            } else {
                activityFwdScanAwbBinding.btnProceed.setText(getString(R.string.scan));
            }
            scannerItemAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ToDoListViewModel.device.equals(Constants.NEWLAND)) {
            IntentFilter intFilter = new IntentFilter(ScanManager.ACTION_SEND_SCAN_RESULT);
            registerReceiver(mResultReceiver(), intFilter);
        }
        if (secureDelivery != null) {
            if (secureDelivery.getPinb() == false && secureDelivery.getOTP() == false && secureDelivery.getSecure_pin() == false) {
                activityFwdScanAwbBinding.mpsundelivered.setVisibility(View.VISIBLE);
            } else {
                activityFwdScanAwbBinding.mpsundelivered.setVisibility(View.GONE);
            }
        }
    }
}