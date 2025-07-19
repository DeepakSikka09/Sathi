package in.ecomexpress.sathi.ui.dashboard.landing;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import in.ecomexpress.sathi.ui.dashboard.fullscreen.FullScreenViewActivity;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.repo.remote.model.masterdata.DashboardBanner;
import in.ecomexpress.sathi.utils.Logger;

public class SliderAdapter extends PagerAdapter {

    ArrayList<String> urls;
    private final Context context;
    List<DashboardBanner> getDashboardBanner;

    public SliderAdapter(Context context, List<DashboardBanner> getDashboardBanner) {
        this.context = context;
        this.getDashboardBanner = getDashboardBanner;
        urls = new ArrayList<>();
        for (int i=0; i<getDashboardBanner.size(); i++) {
            urls.add(getDashboardBanner.get(i).getLong_url());
        }
    }

    @Override
    public int getCount() {
        return getDashboardBanner.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_slider, null);

        TextView textView = view.findViewById(R.id.textView);
        ProgressBar progress = view.findViewById(R.id.progress1);
        ImageView web = view.findViewById(R.id.image);
        WebView webView = view.findViewById(R.id.image1);
        try {
            Glide.with(context).load(getDashboardBanner.get(position).getShort_url()).into(web);
            startWebView(webView, progress, getDashboardBanner.get(position).getShort_url());
            ViewPager viewPager = (ViewPager) container;
            viewPager.addView(view, 0);
        } catch (Exception e) {
            Logger.e("SliderAdapterDashboard", String.valueOf(e));
        }
         textView.setOnClickListener(view1 -> {
             Intent i = new Intent(context, FullScreenViewActivity.class);
             i.putStringArrayListExtra("urlstr", urls);
             i.putExtra("clickposition", position);
             context.startActivity(i);
         });
        return view;
    }

    public void startWebView(final WebView webview, final ProgressBar progressBar, String url) {
        webview.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);

            }
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                webview.loadUrl("http://arunimmanuel.blogspot.in");
                progressBar.setVisibility(View.GONE);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });
        webview.loadUrl(url);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}