package in.ecomexpress.sathi.ui.drs.rvp.awbscan;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.databinding.ActivitySamplePhonePeBinding;


@AndroidEntryPoint
public class SamplePhonePeActivity extends AppCompatActivity {

    private final String TAG = SamplePhonePeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySamplePhonePeBinding activitySamplePhonePeBinding = ActivitySamplePhonePeBinding.inflate(getLayoutInflater());
        View view = activitySamplePhonePeBinding.getRoot();
        setContentView(view);
        logScreenNameInGoogleAnalytics(TAG, this);
        activitySamplePhonePeBinding.imageViewBack.setOnClickListener(v -> {
            finish();
            applyTransitionToBackFromActivity(this);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        applyTransitionToBackFromActivity(this);
    }
}