package in.ecomexpress.sathi.ui.dashboard.campaign;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.core.content.ContextCompat;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityCampaignBinding;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.utils.WebViewClient;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;

@AndroidEntryPoint
public class CampaignActivity extends BaseActivity<ActivityCampaignBinding, CampaignViewModel> implements CampaignInterface {

    private final String TAG = CampaignActivity.class.getSimpleName();
    @Inject
    CampaignViewModel campaignViewModel;
    ActivityCampaignBinding activityCampaignBinding;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        campaignViewModel.setNavigator(this);
        activityCampaignBinding = getViewDataBinding();
        logScreenNameInGoogleAnalytics(TAG, this);
        if (isNetworkConnected()) {
            campaignViewModel.getCampaignURLAPI();
        } else {
            showSnackbar(getResources().getString(R.string.no_network_error));
        }
    }

    @Override
    public CampaignViewModel getViewModel() {
        return campaignViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_campaign;
    }

    @Override
    public void showError(String description) {
        showSnackbar(description);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void startCampaignWebView(String url) {
        try {
            webView = activityCampaignBinding.trainingWebview;
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setAllowFileAccess(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            // Enable cookies
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(webView, true);

            webView.setWebViewClient(new android.webkit.WebViewClient());
            webView.setWebChromeClient(new WebViewClient(CampaignActivity.this));
            webView.setBackgroundColor(ContextCompat.getColor(this,R.color.transparant));
            webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
            activityCampaignBinding.trainingWebview.loadUrl(url);
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public Activity getActivityContext() {
        return this;
    }

    @Override
    public void doLogout(String desc) {
        try {
            showToast(getString(R.string.session_expire));
            campaignViewModel.logoutLocal();
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void clearStack() {
        Intent intent = new Intent(CampaignActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
            applyTransitionToBackFromActivity(this);
        }
    }
}