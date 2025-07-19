package in.ecomexpress.sathi.ui.drs.forward.obd.activity;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.lifecycle.ViewModelProvider;

import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityFwdObdScannerBinding;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.drs.forward.obd.navigator.FlyerScanNavigator;
import in.ecomexpress.sathi.ui.drs.forward.obd.viewmodel.OBDScannerViewModel;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class FwdOBDScannerActivity extends BaseActivity<ActivityFwdObdScannerBinding, OBDScannerViewModel> implements FlyerScanNavigator {

    private final String TAG = FwdOBDScannerActivity.class.getSimpleName();
    @Inject
    OBDScannerViewModel obdScannerViewModel;
    private ActivityFwdObdScannerBinding binding;
    private CaptureManager capture;
    private boolean flag;
    private boolean isUndelivered;
    private boolean isNonQc;
    private String lastText = "";
    private static final long DEBOUNCE_DELAY = 2000;
    private long lastScanTime = 0;
    private int scanAttempts = 0;
    private static final int MAX_SCAN_ATTEMPTS = 3;

    public List<String> qcCode;
    public List<String> qualityCheckName;
    // Data from previous activity:-
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
    boolean callAllowed;
    boolean isCheckedBox;
    ForwardCommit forwardCommit;
    public List<String> qcFailedData;
    String cleanedFlyerValue = "";
    private String DRS_DATE = "";
    private String SHIPMENT_DECLARED_VALUE = "";
    String returnPackageBarcode = "";
    private String flyerScan = "";
    private String awbScan = "";

    private boolean isFlyerImage = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        obdScannerViewModel.setNavigator(this);
        binding = getViewDataBinding();
        initScanManager(savedInstanceState);
        logScreenNameInGoogleAnalytics(TAG, this);
        // Get data:-
        getAllDataFromIntent();
        initialization();
        setBindings(flag);

        binding.awbHeader.awb.setText("AWB: " + awbNo);

        binding.btnSubmit.setOnClickListener(view -> {
            if (binding.txtAwbNo.getText().toString().equalsIgnoreCase(awbNo)) {
               /* if (flag) {
                    isFlyerImage=true;
                    sendIntent(FwdOBDFlyerSuccessActivity.class);
                } else*/
                {
                    sendIntent(FwdOBDQcPassActivity.class);
                }
            } else {
                showSnackbar("Enter Correct AWB");
            }
        });
        binding.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.txtShipmentAwbNo.setVisibility(View.VISIBLE);
                binding.constChild.setVisibility(View.VISIBLE);
                binding.btnCard.setVisibility(View.VISIBLE);
            } else {
                binding.txtShipmentAwbNo.setVisibility(View.GONE);
                binding.constChild.setVisibility(View.GONE);
                binding.btnCard.setVisibility(View.GONE);
            }
        });
        binding.icFlash.setOnClickListener(v -> switchFlashlight());
    }

    private void setBindings(boolean flag) {
        if (flag) {
            String text = "Scan Flyer Barcode";
            binding.txtCodeVeri.setText(text);
            binding.notCard.setVisibility(View.GONE);
            binding.flyerDetailCard.setVisibility(View.VISIBLE);
            binding.qcCard.setVisibility(View.GONE);
            binding.checkbox.setVisibility(View.GONE);
            binding.txtCheckbox.setVisibility(View.GONE);
            binding.btnCard.setVisibility(View.GONE);
            binding.txtShipmentAwbNo.setVisibility(View.GONE);
            binding.constChild.setVisibility(View.GONE);
        }
        if (isNonQc) {
            binding.notCard.setVisibility(View.GONE);
            binding.flyerDetailCard.setVisibility(View.GONE);
            binding.qcCard.setVisibility(View.VISIBLE);
            binding.checkbox.setVisibility(View.GONE);
            binding.txtCheckbox.setVisibility(View.GONE);
            binding.btnCard.setVisibility(View.GONE);
            binding.txtShipmentAwbNo.setVisibility(View.GONE);
            binding.constChild.setVisibility(View.GONE);
        }
    }


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void initScanManager(Bundle savedInstanceState) {
        DecoratedBarcodeView barcodeScannerView = binding.scanner;
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        barcodeScannerView.decodeContinuous(callback);
    }

    private final BarcodeCallback callback = barcodeResult -> {
        try {
            if (barcodeResult.getText() != null && !barcodeResult.getText().equalsIgnoreCase(lastText)) {
                if (System.currentTimeMillis() - lastScanTime > DEBOUNCE_DELAY) {
                    startScannerWork(barcodeResult.getText());
                    lastText = barcodeResult.getText();
                    lastScanTime = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    };

    private void startScannerWork(String result) {
        boolean scanSuccessful = checkScanResult(result);
        if (scanSuccessful) {
            scanAttempts = 0;
            if (flag) {
                isFlyerImage = true;
                flyerScan = cleanedFlyerValue;
                obdScannerViewModel.getRvpFlyerDuplicateCheck(flyerScan, awbNo, drsIdNum, true);

                //  sendIntent(FwdOBDFlyerSuccessActivity.class);
            } else {
                awbScan = "Scan";
                sendIntent(FwdOBDQcPassActivity.class);
            }
        } else {
            if (!flag) {
                scanAttempts++;
                int remainingAttempts = MAX_SCAN_ATTEMPTS - scanAttempts;
                if (scanAttempts == MAX_SCAN_ATTEMPTS) {
                    binding.checkbox.setEnabled(true);
                    binding.checkbox.setClickable(true);
                    binding.txtCheckbox.setTextColor(Color.parseColor("#1A1A1A"));
                    binding.checkbox.setVisibility(View.VISIBLE);
                    binding.txtCheckbox.setVisibility(View.VISIBLE);
                    binding.btnCard.setVisibility(View.VISIBLE);
                    binding.txtShipmentAwbNo.setVisibility(View.VISIBLE);
                    binding.constChild.setVisibility(View.VISIBLE);
                    awbScan = "Manual";
                }
                if (remainingAttempts >= 0) {
                    showSnackbar("Scan Attempt Left  " + remainingAttempts);
                }
            }
        }
    }

    private boolean checkScanResult(String result) {
        if (flag) {
            String[] split = getViewModel().getDataManager().getRVPAWBWords().split(",");
            for (int i = 0; i < split.length; i++) {
                if (result.startsWith(split[i])) {
                    cleanedFlyerValue = result.replaceAll("[^a-zA-Z0-9]", "");
                    return true;
                } else {
                    if (i + 1 == split.length) {
                        showSnackbar(getString(R.string.invalid_flyer_code_scan_again_or_another));
                        return false;
                    }
                }
            }
            return true;
        } else {
            return awbNo.equalsIgnoreCase(result);
        }
    }

    public void switchFlashlight() {
        if (binding.icFlash.isSelected()) {
            binding.scanner.setTorchOff();
            binding.icFlash.setSelected(false);
            binding.icFlash.setImageResource(R.drawable.flashoff);
        } else {
            binding.scanner.setTorchOn();
            binding.icFlash.setSelected(true);
            binding.icFlash.setImageResource(R.drawable.flashon);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (capture != null) {
            capture.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (capture != null) {
            capture.onPause();
            binding.scanner.setTorchOff();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (capture != null) {
            capture.onDestroy();
            binding.scanner.setTorchOff();
        }
    }

    @Override
    public OBDScannerViewModel getViewModel() {
        // return new ViewModelProvider(this, mViewModelFactory).get(OBDScannerViewModel.class);
        return obdScannerViewModel;
    }


    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_fwd_obd_scanner;
    }


    private void getAllDataFromIntent() {
        if (getIntent().getExtras() != null) {
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
            returnPackageBarcode = intent.getStringExtra(Constants.return_package_barcode);
            fwdDelImage = intent.getBooleanExtra(Constants.FWD_DEL_IMAGE, false);
            DRS_DATE = getIntent().getExtras().getString(Constants.DRS_DATE);
            SHIPMENT_DECLARED_VALUE = getIntent().getExtras().getString(Constants.SHIPMENT_DECLARED_VALUE);
            callAllowed = intent.getBooleanExtra("call_allowed", false);
            flag = intent.getBooleanExtra("flag", false);
            isUndelivered = intent.getBooleanExtra("isUndelivered", false);
            isNonQc = intent.getBooleanExtra("isNonQc", false);
            isCheckedBox = intent.getBooleanExtra("isCheckedbox", false);
            qcFailedData = intent.getStringArrayListExtra("QC_FAILED_DATA");
            forwardCommit = getIntent().getParcelableExtra("data");
            qcCode = intent.getStringArrayListExtra("QC_CODE");
            qualityCheckName = intent.getStringArrayListExtra("QC_NAME");
        }
    }

    private void sendIntent(Class<? extends Activity> targetActivity) {
        Intent intent = new Intent(FwdOBDScannerActivity.this, targetActivity);
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
        intent.putExtra(Constants.return_package_barcode, flyerScan);
        intent.putExtra(Constants.FWD_DEL_IMAGE, fwdDelImage);
        intent.putExtra("call_allowed", callAllowed);
        intent.putExtra("isUndelivered", isUndelivered);
        intent.putExtra("flag", flag);
        intent.putExtra("isCheckedbox", isCheckedBox);
        intent.putExtra("isNonQc", isNonQc);
        intent.putStringArrayListExtra("QC_FAILED_DATA", (ArrayList<String>) qcFailedData);
        intent.putExtra("cleanedFlyerValue", cleanedFlyerValue);
        intent.putExtra(Constants.SHIPMENT_DECLARED_VALUE, SHIPMENT_DECLARED_VALUE);
        intent.putExtra(Constants.DRS_DATE, DRS_DATE);
        intent.putStringArrayListExtra("QC_CODE", (ArrayList<String>) qcCode);
        intent.putStringArrayListExtra("QC_NAME", (ArrayList<String>) qualityCheckName);
        intent.putExtra(getString(R.string.data), forwardCommit);
        intent.putExtra(Constants.Flyer_SCAN, flyerScan);
        intent.putExtra(Constants.AWB_SCAN, awbScan);
        intent.putExtra(Constants.Flyer_Img_Check, isFlyerImage);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    private void initialization() {
        binding.awbHeader.backArrow.getDrawable().setTint(getResources().getColor(R.color.grey_F4));
        this.getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showSnackbar("You Can Not Move Back");
            }
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showSnackbar("You Can Not Move Back");
    }

    @Override
    public void errorMsg(String message) {
        showSnackbar(message);
    }

    @Override
    public void onProceed() {
        sendIntent(FwdOBDFlyerSuccessActivity.class);
    }
}