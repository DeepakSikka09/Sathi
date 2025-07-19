package in.ecomexpress.sathi.ui.dashboard.refer;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityReferBinding;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.utils.WebViewClient;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;

@AndroidEntryPoint
public class ReferFriendActivity extends BaseActivity<ActivityReferBinding, ReferFriendViewModel> implements ReferNavigator {

    private final String TAG = ReferFriendActivity.class.getSimpleName();
    @Inject
    ReferFriendViewModel referFriendViewModel;
    ActivityReferBinding activityWebViewReferFriendBinding;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        referFriendViewModel.setNavigator(this);
        activityWebViewReferFriendBinding = getViewDataBinding();
        logScreenNameInGoogleAnalytics(TAG, this);
        if (isNetworkConnected()) {
            referFriendViewModel.getReferURLAPI();
        } else {
            showSnackbar(getResources().getString(R.string.no_network_error));
        }
    }

    public static Intent getStartIntent(Context context) {
        return new Intent(context, ReferFriendActivity.class);
    }

    @Override
    public ReferFriendViewModel getViewModel() {
        return referFriendViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_refer;
    }

    @Override
    public void showError(String description) {
        showSnackbar(description);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void startTrainingWebView(String url) {
        try {
            webView = activityWebViewReferFriendBinding.trainingWebview;
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
            webView.setWebChromeClient(new WebViewClient(ReferFriendActivity.this));
            activityWebViewReferFriendBinding.trainingWebview.loadUrl(url);
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
            referFriendViewModel.logoutLocal();
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void clearStack() {
        Intent intent = new Intent(ReferFriendActivity.this, LoginActivity.class);
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