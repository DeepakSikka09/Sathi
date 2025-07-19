package in.ecomexpress.sathi.utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;

public class WebViewClient extends WebChromeClient {
    private View mCustomView;
    private CustomViewCallback mCustomViewCallback;
    private int mOriginalOrientation;
    private int mOriginalSystemUiVisibility;

    private final Activity activity;

    public WebViewClient(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onHideCustomView() {
        ((FrameLayout) activity.getWindow().getDecorView()).removeView(mCustomView);
        mCustomView = null;
        activity.getWindow().getDecorView().setSystemUiVisibility(mOriginalSystemUiVisibility);
        activity.setRequestedOrientation(mOriginalOrientation);
        mCustomViewCallback.onCustomViewHidden();
        mCustomViewCallback = null;
    }

    @Override
    public void onShowCustomView(View paramView, CustomViewCallback paramCustomViewCallback) {
        if (mCustomView != null) {
            onHideCustomView();
            return;
        }
        mCustomView = paramView;
        mOriginalSystemUiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
        mOriginalOrientation = activity.getRequestedOrientation();
        mCustomViewCallback = paramCustomViewCallback;

        // Add the custom view to the decor view
        ((FrameLayout) activity.getWindow().getDecorView()).addView(
                mCustomView,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )
        );

        // Hide the system UI and make the activity full-screen
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
}
