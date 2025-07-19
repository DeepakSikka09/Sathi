package in.ecomexpress.sathi.ui.dashboard.training;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import androidx.annotation.Nullable;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityTrainingBinding;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;

@AndroidEntryPoint
public class TrainingActivity extends BaseActivity<ActivityTrainingBinding, TrainingViewModel> implements ITrainingNavigator {

    private final String TAG = TrainingActivity.class.getSimpleName();
    @Inject
    TrainingViewModel trainingViewModel;
    ActivityTrainingBinding activityTrainingBinding;
    private WebView webView;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        trainingViewModel.setNavigator(this);
        activityTrainingBinding = getViewDataBinding();
        String sourceOfOrigin = getIntent().getStringExtra(Constants.SOURCE_OF_ORIGIN);
        boolean isTraining = sourceOfOrigin == null || !sourceOfOrigin.equalsIgnoreCase(Constants.ODH);
        if(isTraining){
            activityTrainingBinding.header.headingName.setText(R.string.pathshaala);
        } else {
            activityTrainingBinding.header.getRoot().setVisibility(View.GONE);
        }
        logScreenNameInGoogleAnalytics(TAG, this);
        if(isNetworkConnected()){
            trainingViewModel.getCholaURLAPI(isTraining, this);
        } else{
            showSnackbar(getResources().getString(R.string.no_network_error));
        }
        activityTrainingBinding.header.backArrow.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public TrainingViewModel getViewModel(){
        return trainingViewModel;
    }

    @Override
    public int getBindingVariable(){
        return BR.viewModel;
    }

    @Override
    public int getLayoutId(){
        return R.layout.activity_training;
    }

    @Override
    public void showError(String description){
        showSnackbar(description);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void startTrainingWebView(String url) {
        try {
            webView = activityTrainingBinding.trainingWebview;
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setAllowFileAccess(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setMediaPlaybackRequiresUserGesture(false);

            // Enable cookies
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(webView, true);
            webView.setWebViewClient(new android.webkit.WebViewClient());
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onPermissionRequest(final PermissionRequest request) {
                    runOnUiThread(() -> request.grant(request.getResources()));
                }

                @Override
                public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                    return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
                }
            });
            activityTrainingBinding.trainingWebview.loadUrl(url);
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public Activity getActivityContext(){
        return this;
    }

    @Override
    public void onBackClick(){
        try{
            if(activityTrainingBinding.trainingWebview.canGoBack()){
                webView.goBack();
            } else{
                super.onBackPressed();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void doLogout(String desc){
        try {
            showToast(getString(R.string.session_expire));
            trainingViewModel.logoutLocal();
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void clearStack() {
        Intent intent = new Intent(TrainingActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
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