package in.ecomexpress.sathi.ui.dashboard.performance;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import androidx.annotation.Nullable;
import java.util.HashMap;
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityPerformanceBinding;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.dashboard.landing.DashboardActivity;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;

@AndroidEntryPoint
public class PerformanceActivity extends BaseActivity<ActivityPerformanceBinding, PerformanceViewModel> implements IPerformanceNavigator {

    private final String TAG = PerformanceActivity.class.getSimpleName();
    @Inject
    PerformanceViewModel performanceViewModel;
    ProgressDialog dialog;
    ActivityPerformanceBinding activityPerformanceBinding;
    Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        performanceViewModel.setNavigator(this);
        this.activityPerformanceBinding = getViewDataBinding();
        activityPerformanceBinding.header.headingName.setText(R.string.performance);
        activityPerformanceBinding.header.backArrow.setOnClickListener(v -> onBackClick());
        logScreenNameInGoogleAnalytics(TAG, this);
        try {
            dialog = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
            dialog.show();
            dialog.setCancelable(false);
            dialog.setMessage("Loading...");
            dialog.setIndeterminate(true);
            if (isNetworkConnected()) {
                performanceViewModel.callApi(PerformanceActivity.this);
            } else {
                dialog.dismiss();
                showSnackbar(getResources().getString(R.string.no_network_error));
            }
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
    }
    public void onBackClick() {
        finish();
    }

    @Override
    public PerformanceViewModel getViewModel() {
        return performanceViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_performance;
    }

    public static Intent getStartIntent(Context context) {
        return new Intent(context, PerformanceActivity.class);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void startPerformanceWebView(String htmlString) {
        try {
            activityPerformanceBinding.performanceWebciew.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                }
            });
            activityPerformanceBinding.performanceWebciew.clearSslPreferences();//Authorization
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Android", getViewModel().getAuthToken());
            WebSettings webSettings = activityPerformanceBinding.performanceWebciew.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setUserAgentString("user-agent-string");
            activityPerformanceBinding.performanceWebciew.loadDataWithBaseURL("", htmlString, "text/html", "utf-8", null);
            activityPerformanceBinding.performanceWebciew.getSettings().getDomStorageEnabled();
            activityPerformanceBinding.performanceWebciew.getSettings().setLoadWithOverviewMode(true);
            activityPerformanceBinding.performanceWebciew.getSettings().setUseWideViewPort(true);
            dialog.dismiss();
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void errorHandler(String s) {
        showSnackbar(s);
        dialog.dismiss();
    }

    @Override
    public void showHandleError(boolean status) {
        dialog.dismiss();
        if (status)
            showSnackbar(getString(R.string.http_500_msg));
        else
            showSnackbar(getString(R.string.server_down_msg));
    }

    @Override
    public void doLogout(String message) {
        try {
            showToast(getString(R.string.session_expire));
            performanceViewModel.logoutLocal();
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void clearStack() {
        Intent intent = new Intent(PerformanceActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    @Override
    public void showError(String s) {
        showSnackbar(s);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(DashboardActivity.getStartIntent(PerformanceActivity.this));
        finish();
        applyTransitionToOpenActivity(this);
    }
}