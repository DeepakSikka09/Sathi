package in.ecomexpress.sathi.ui.drs.rvp.awbscan;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityRqcScannerBinding;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class RQCScannerActivity extends AppCompatActivity {

    private final String TAG = RQCScannerActivity.class.getSimpleName();
    private ActivityRqcScannerBinding binding;
    public static final String SCANNED_CODE = "scanned_code";
    public String lastText = "";
    private static final long DEBOUNCE_DELAY = 2000;
    private long lastScanTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rqc_scanner);
        logScreenNameInGoogleAnalytics(TAG, this);
        binding.ivFlash.setOnClickListener(v -> switchFlashlight());
        binding.zxingBarcodeScanner.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory());
        binding.zxingBarcodeScanner.initializeFromIntent(getIntent());
        binding.zxingBarcodeScanner.decodeContinuous(callback);
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.zxingBarcodeScanner.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            binding.zxingBarcodeScanner.pause();
            offFlashLight();
        } catch (Exception e) {
             Logger.e(RQCScannerActivity.class.getName(), e.getMessage());
        }
    }

    private final BarcodeCallback callback = barcodeResult -> {
        try {
            if (barcodeResult.getText() != null && !barcodeResult.getText().equalsIgnoreCase(lastText)) {
                if (System.currentTimeMillis() - lastScanTime > DEBOUNCE_DELAY) {
                    Intent returnIntent = new Intent();
                    String resultText = barcodeResult.getText();
                    returnIntent.putExtra(SCANNED_CODE, resultText);
                    setResult(Activity.RESULT_OK, returnIntent);
                    lastText = resultText;
                    lastScanTime = System.currentTimeMillis();
                    offFlashLight();
                    finish();
                }
            }
        } catch (Exception e) {
             Logger.e(RQCScannerActivity.class.getName(), e.getMessage());
        }
    };

    public void switchFlashlight() {
        if (binding.ivFlash.isSelected()) {
            binding.zxingBarcodeScanner.setTorchOff();
            binding.ivFlash.setSelected(false);
            binding.ivFlash.setImageResource(R.drawable.flashoff);
        } else {
            binding.zxingBarcodeScanner.setTorchOn();
            binding.ivFlash.setSelected(true);
            binding.ivFlash.setImageResource(R.drawable.flashon);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        binding.zxingBarcodeScanner.setTorchOff();
        applyTransitionToBackFromActivity(this);
    }

    private void offFlashLight() {
        binding.zxingBarcodeScanner.setTorchOff();
        binding.ivFlash.setSelected(false);
        binding.ivFlash.setImageResource(R.drawable.flashoff);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            offFlashLight();
        } catch (Exception e) {
             Logger.e(RQCScannerActivity.class.getName(), e.getMessage());
        }
    }
}