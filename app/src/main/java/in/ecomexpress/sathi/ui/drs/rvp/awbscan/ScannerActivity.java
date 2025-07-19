package in.ecomexpress.sathi.ui.drs.rvp.awbscan;

import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.R;

@AndroidEntryPoint
// Need to find the use
public class ScannerActivity extends AppCompatActivity {

    private final String TAG = ScannerActivity.class.getSimpleName();
    private CodeScanner mCodeScanner;
    public static final String SCANNED_CODE = "scanned_code";
    private CodeScannerView mCodeScannerView;
    ViewGroup contentFrame;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_scanner);
        logScreenNameInGoogleAnalytics(TAG, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        contentFrame = findViewById(R.id.content_frame);
        mCodeScannerView = new CodeScannerView(this);
        contentFrame.addView(mCodeScannerView);
        mCodeScanner = new CodeScanner(ScannerActivity.this, mCodeScannerView);
        mCodeScanner.setDecodeCallback(callback);
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        super.onPause();
        contentFrame.removeView(mCodeScannerView);
        mCodeScanner.releaseResources();
    }

    private final DecodeCallback callback = result -> {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(SCANNED_CODE, result.getText());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    };
}
