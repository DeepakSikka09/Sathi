package in.ecomexpress.sathi.ui.dashboard.fe_earned;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityEarnedNewBinding;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.utils.WebViewClient;

@AndroidEntryPoint
public class Earned_Activity extends BaseActivity<ActivityEarnedNewBinding, EarnedViewModel> implements IEarnedNavigator {

    private final String TAG = Earned_Activity.class.getSimpleName();
    @Inject
    EarnedViewModel earnedViewModel;
    ActivityEarnedNewBinding activityDashboardBinding;
    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        earnedViewModel.setNavigator(this);
        this.activityDashboardBinding = getViewDataBinding();
        logScreenNameInGoogleAnalytics(TAG, this);
        if(isNetworkConnected()){
            earnedViewModel.doTrainingAPICall();
        } else{
            showSnackbar(getResources().getString(R.string.no_network_error));
        }
        earnedViewModel.getEarningApiResponseMutableLiveData().observe(this, earningApiResponse -> {
            String apiResponseUrl = (earningApiResponse.getRedirectUrl() == null) ? "" : earningApiResponse.getRedirectUrl();
            if (!apiResponseUrl.isEmpty()) {
                startTrainingWebView(apiResponseUrl);
            } else {
                showSnackbar(getString(R.string.unify_api_failed));
            }
        });
    }

    @Override
    public EarnedViewModel getViewModel() {
        return earnedViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_earned_new;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void showError(String description){
        showSnackbar(description);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void startTrainingWebView(String url) {
        try {
            webView = activityDashboardBinding.trainingWebview;
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setAllowFileAccess(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(webView, true);
            webView.setWebViewClient(new android.webkit.WebViewClient());
            webView.setWebChromeClient(new WebViewClient(Earned_Activity.this));
            activityDashboardBinding.trainingWebview.loadUrl(url);
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public Activity getActivityContext(){
        return this;
    }

    @Override
    public void onBackPressed() {
        if (webView!=null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
            applyTransitionToBackFromActivity(this);
        }
    }
}