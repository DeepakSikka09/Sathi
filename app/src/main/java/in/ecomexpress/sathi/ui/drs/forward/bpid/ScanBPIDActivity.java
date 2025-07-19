package in.ecomexpress.sathi.ui.drs.forward.bpid;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logButtonEventInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.Constants.PICK_FROM_CAMERA;
import static in.ecomexpress.sathi.utils.Constants.device;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.nlscan.android.scan.ScanManager;
import com.nlscan.android.scan.ScanSettings;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityScanBpidactivityBinding;
import in.ecomexpress.sathi.repo.local.data.activitiesdata.FWDActivitiesData;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.drs.forward.fill_awb.AwbPopupBPIDDialog;
import in.ecomexpress.sathi.ui.drs.forward.fill_awb.MyDialogCloseListener;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.FlashlightProvider;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class ScanBPIDActivity extends BaseActivity<ActivityScanBpidactivityBinding, ScanBPIDViewModel> implements IScanBPNavigator, MyDialogCloseListener {

    private final String TAG = ScanBPIDActivity.class.getSimpleName();
    @Inject
    ScanBPIDViewModel scanBPIDViewModel;
    ActivityScanBpidactivityBinding activityScanBpidactivityBinding;
    Boolean scannedStatus = false;
    String awb_no, composite_key, drs_id_num;
    String return_package_barcode = "";
    AwbPopupBPIDDialog awbPopupDialog;
    int counter_scan = 0;
    MyDialogCloseListener myDialogCloseListener;
    private boolean isScannerPaused = false;
    ForwardCommit forwardCommit;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, ScanBPIDActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scanBPIDViewModel.setNavigator(this);
        activityScanBpidactivityBinding = getViewDataBinding();
        activityScanBpidactivityBinding.ivFlash.setOnClickListener(view -> switchFlashlight());
        myDialogCloseListener = this;
        logScreenNameInGoogleAnalytics(TAG, this);

        //get the data from previous activity
        try {

            FWDActivitiesData fwdActivitiesData = getIntent().getParcelableExtra("fwdActivitiesData");
            if (fwdActivitiesData != null) {
                try {
                    return_package_barcode = fwdActivitiesData.getReturn_package_barcode();
                    awb_no = String.valueOf(fwdActivitiesData.getAwbNo());
                    composite_key = fwdActivitiesData.getCompositeKey();
                    drs_id_num = String.valueOf(fwdActivitiesData.getDrsId());
                } catch (Exception e) {
                    Logger.e("ScanBPIDActivity", e.getMessage());
                }
            }
            forwardCommit = getIntent().getParcelableExtra("data");
        } catch (Exception e) {
            Logger.e(ScanBPIDActivity.class.getName(), e.getMessage());

        }


        if (device.equals(Constants.NEWLAND_90) || device.equals(Constants.NEWLAND)) {
            activityScanBpidactivityBinding.ivBarcode.setVisibility(View.GONE);
            ScanManager mScanMgr = ScanManager.getInstance();
            mScanMgr.startScan();
            mScanMgr.enableBeep();
            mScanMgr.setOutpuMode(ScanSettings.Global.VALUE_OUT_PUT_MODE_BROADCAST);
            IntentFilter intFilter = new IntentFilter(ScanManager.ACTION_SEND_SCAN_RESULT);
            registerReceiver(scanBPIDViewModel.mResultReceiver(), intFilter);
        } else {
            try {
                activityScanBpidactivityBinding.ivBarcode.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory());
                activityScanBpidactivityBinding.ivBarcode.initializeFromIntent(getIntent());
                activityScanBpidactivityBinding.ivBarcode.decodeContinuous(callback);
            } catch (Exception e) {
                Logger.e(ScanBPIDActivity.class.getName(), e.getMessage());

            }
        }
        activityScanBpidactivityBinding.header.awb.setText(R.string.scan_bp_id);
        activityScanBpidactivityBinding.header.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackClick();
            }
        });
    }

    public void switchFlashlight() {
        if (activityScanBpidactivityBinding.ivFlash.isSelected()) {
            logButtonEventInGoogleAnalytics(TAG, "BPIDFlashClick", "FlashOff", this);
            activityScanBpidactivityBinding.ivBarcode.setTorchOff();
            activityScanBpidactivityBinding.ivFlash.setSelected(false);
            activityScanBpidactivityBinding.ivFlash.setImageResource(R.drawable.flashoff);
        } else {
            logButtonEventInGoogleAnalytics(TAG, "BPIDFlashClick", "FlashOn", this);
            activityScanBpidactivityBinding.ivBarcode.setTorchOn();
            activityScanBpidactivityBinding.ivFlash.setSelected(true);
            activityScanBpidactivityBinding.ivFlash.setImageResource(R.drawable.flashon);
        }
    }

    private final BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            try {
                try {
                    if (isScannerPaused) {
                        return;
                    }
                    if (counter_scan < 2) {
                        if (result != null) {
                            String resultText = result.getText();
                            if (resultText.equalsIgnoreCase(return_package_barcode)) {
                                scannedStatus = true;
                                String value = "Scan";
                                Intent intent = new Intent();
                                intent.putExtra("value", value);
                                intent.putExtra("manualBP", resultText);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else if (!resultText.equalsIgnoreCase(return_package_barcode) && scanBPIDViewModel.getDataManager().getBPMismatch()) {
                                scannedStatus = true;
                                String value = "Scan";
                                Intent intent = new Intent();
                                intent.putExtra("value", value);
                                intent.putExtra("manualBP", resultText);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                scannedStatus = false;
                                if (counter_scan >= 2) {
                                    awbPopupDialog = AwbPopupBPIDDialog.newInstance(ScanBPIDActivity.this, awb_no, return_package_barcode, composite_key, drs_id_num, forwardCommit);
                                    awbPopupDialog.show(getSupportFragmentManager());
                                    awbPopupDialog.setCancelable(false);
                                    awbPopupDialog.setListener(myDialogCloseListener);
                                    activityScanBpidactivityBinding.ivBarcode.setVisibility(View.GONE);
                                    counter_scan = 0;
                                } else {
                                    scanBPIDViewModel.doScanAgainAlert(ScanBPIDActivity.this);
                                    counter_scan++;
                                }
                            }
                        } else {
                            scannedStatus = false;
                            scanBPIDViewModel.doScanAgainAlert(ScanBPIDActivity.this);
                            counter_scan++;
                            if (counter_scan >= 2) {
                                awbPopupDialog = AwbPopupBPIDDialog.newInstance(ScanBPIDActivity.this, awb_no, return_package_barcode, composite_key, drs_id_num, forwardCommit);
                                awbPopupDialog.show(getSupportFragmentManager());
                                awbPopupDialog.setListener(myDialogCloseListener);
                                awbPopupDialog.setCancelable(false);
                                activityScanBpidactivityBinding.ivBarcode.setVisibility(View.GONE);
                                counter_scan = 0;
                            }
                        }
                    } else {
                        awbPopupDialog = AwbPopupBPIDDialog.newInstance(ScanBPIDActivity.this, awb_no, return_package_barcode, composite_key, drs_id_num, forwardCommit);
                        awbPopupDialog.show(getSupportFragmentManager());
                        awbPopupDialog.setListener(myDialogCloseListener);
                        awbPopupDialog.setCancelable(false);
                        activityScanBpidactivityBinding.ivBarcode.setVisibility(View.GONE);
                        counter_scan = 0;
                    }
                    // Pause the scanner for 2 second:-
                    pauseScanner();
                } catch (Exception e) {
                    Logger.e(ScanBPIDActivity.class.getName(), e.getMessage());

                    showSnackbar(e.getMessage());
                }
                activityScanBpidactivityBinding.ivBarcode.resume();
            } catch (Exception e) {
                Logger.e(ScanBPIDActivity.class.getName(), e.getMessage());

            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
            // No need to write code inside this method.
        }
    };

    @Override
    public ScanBPIDViewModel getViewModel() {
        return scanBPIDViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_scan_bpidactivity;
    }

    private void pauseScanner() {
        isScannerPaused = true;
        activityScanBpidactivityBinding.ivBarcode.pause();
        new Handler().postDelayed(this::resumeScanner, 2000);
    }

    /* Resume Scanner:-*/
    private void resumeScanner() {
        isScannerPaused = false;
        activityScanBpidactivityBinding.ivBarcode.resume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!CommonUtils.isAllPermissionAllow(this)) {
            openSettingActivity();
            return;
        }
        try {
            activityScanBpidactivityBinding.ivBarcode.resume();
        } catch (Exception e) {
            Logger.e(ScanBPIDActivity.class.getName(), e.getMessage());

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            activityScanBpidactivityBinding.ivBarcode.pause();
            FlashlightProvider flashlightProvider = new FlashlightProvider(this);
            flashlightProvider.turnFlashOff();
            if (device.equals(Constants.NEWLAND)) {
                if (scanBPIDViewModel.mResultReceiver() != null) {
                    unregisterReceiver(scanBPIDViewModel.mResultReceiver());
                }
            }
        } catch (Exception e) {
            Logger.e(ScanBPIDActivity.class.getName(), e.getMessage());

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityScanBpidactivityBinding.ivBarcode.setTorchOff();
    }


    public void onBackClick() {
        super.onBackPressed();
        activityScanBpidactivityBinding.ivBarcode.setTorchOff();
        applyTransitionToBackFromActivity(this);
    }

    @Override
    public void showScanAlert() {
        // No need to write code inside this method.
    }

    @Override
    public void mResultReceiver1(String strScancode) {
        // No need to write code inside this method.
    }


    @Override
    public void setStatusOfAwb(boolean isAwbMatch) {
        // No need to write code inside this method.
    }

    /*checking the status manually matched or not
     *
     * isAwbMatch is true/false
     * manualEnterBP is checked for bp id is matched manually or by scan
     * */
    @Override
    public void setStatusOfAwb(boolean isAwbMatch, String manualEnterBP) {
        if (isAwbMatch) {
            scannedStatus = true;
            showLoading();
            String value = "ManuallyMatched";
            Intent intent = new Intent();
            intent.putExtra("value", value);
            intent.putExtra("manualBP", manualEnterBP);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            scannedStatus = false;
            showLoading();
            String value = "ManuallyNotMatched";
            Intent intent = new Intent();
            intent.putExtra("value", value);
            intent.putExtra("manualBP", manualEnterBP);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == PICK_FROM_CAMERA) {
                awbPopupDialog.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            Logger.e(ScanBPIDActivity.class.getName(), e.getMessage());

            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
