package in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.qc_check_image;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.ui.dashboard.fullscreen.FullScreenViewActivity;
import in.ecomexpress.sathi.utils.Constants;

public class PreviewImageAdapterList extends PagerAdapter {

    private final Context mContext;
    String[] split_images;
    ArrayList<String> urlstr;
    String from;

    public PreviewImageAdapterList(Context context, String[] split_images, String local) {
        mContext = context;
        this.from = local;
        this.split_images = split_images;
        urlstr = new ArrayList<>();
        Collections.addAll(urlstr, split_images);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.popuplist, collection, false);
        ImageView imageView = layout.findViewById(R.id.image);
        TextView textView = layout.findViewById(R.id.textView);

        if (from.equals("local")) {
            File fileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/" + Constants.EcomExpress);
            if (!fileDir.exists())
                fileDir.mkdirs();
            File cacheFile = new File(fileDir, split_images[position]);
            Glide.with(mContext).load(cacheFile.getAbsolutePath())
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .error(R.drawable.ic_broken_image_black_24dp)
                    .into(imageView);
            collection.addView(layout);
        } else {
            Glide.with(mContext)
                    .load(split_images[position].trim())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .error(R.drawable.ic_broken_image_black_24dp)
                    .into(imageView);
            collection.addView(layout);
        }
        textView.setOnClickListener(view -> {
            Intent i = new Intent(mContext, FullScreenViewActivity.class);
            i.putStringArrayListExtra("urlstr", urlstr);
            i.putExtra("clickposition", position);
            mContext.startActivity(i);
        });
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