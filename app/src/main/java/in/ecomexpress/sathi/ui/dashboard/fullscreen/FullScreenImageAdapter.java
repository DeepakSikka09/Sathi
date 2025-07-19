package in.ecomexpress.sathi.ui.dashboard.fullscreen;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import in.ecomexpress.sathi.R;

public class FullScreenImageAdapter extends PagerAdapter {

	private final Activity activity;
	private final ArrayList<String> imagePaths;

    public FullScreenImageAdapter(Activity activity,ArrayList<String> imagePaths) {
		this.activity = activity;
		this.imagePaths = imagePaths;
	}

	@Override
	public int getCount() {
		return this.imagePaths.size();
	}

	@Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
	
	@NonNull
	@Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        TouchImageView imgDisplay;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container, false);
        imgDisplay = viewLayout.findViewById(R.id.imgDisplay);
		Glide.with(activity).load(imagePaths.get(position)).into(imgDisplay);
        container.addView(viewLayout);
        return viewLayout;
	}
	
	@Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }
}