package in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.qc_check_image;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import in.ecomexpress.sathi.R;

public class PreviewImageAdapter extends PagerAdapter {

    private final Context mContext;
    String[] split_images;


    public PreviewImageAdapter(Context context , String[] split_images) {
        mContext = context;
        this.split_images = split_images;

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.popup, collection, false);
        ImageView imageView = layout.findViewById(R.id.imageView);

        Glide.with(mContext).load(split_images[position].trim())
                .placeholder(R.drawable.ic_image_black_24dp)
                .error(R.drawable.ic_broken_image_black_24dp)
                .into(imageView);
        collection.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, @NonNull Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return split_images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
       return "";
    }

}