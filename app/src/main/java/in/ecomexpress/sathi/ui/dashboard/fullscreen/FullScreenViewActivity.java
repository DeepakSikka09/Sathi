package in.ecomexpress.sathi.ui.dashboard.fullscreen;

import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.MotionEventCompat;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.R;

public class FullScreenViewActivity extends AppCompatActivity {

    private final String TAG = FullScreenViewActivity.class.getSimpleName();
    int clickPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_view);
        logScreenNameInGoogleAnalytics(TAG, this);
        ViewPager viewPager = findViewById(R.id.pager);
        viewPager.setBackgroundColor(Color.BLACK);
        ImageView imvClose = findViewById(R.id.btnClose);
        imvClose.setOnClickListener(v -> finish());
        Intent i = getIntent();
        ArrayList<String> urlstr;
        urlstr = i.getStringArrayListExtra("urlstr");
        clickPosition = i.getIntExtra("clickposition", 0);
        FullScreenImageAdapter adapter = new FullScreenImageAdapter(FullScreenViewActivity.this, urlstr);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(clickPosition);
        // Using ContextCompat to get color
        int color = ContextCompat.getColor(this, R.color.black);
        // Apply the color to a view, for example
        viewPager.setBackgroundColor(color);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int action = MotionEventCompat.getActionMasked(event);
        switch(action){
            case (MotionEvent.ACTION_DOWN):
            case (MotionEvent.ACTION_MOVE):
            case (MotionEvent.ACTION_UP):
            case (MotionEvent.ACTION_CANCEL):
            case (MotionEvent.ACTION_OUTSIDE):
                finish();
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }
}