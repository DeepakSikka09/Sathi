package in.ecomexpress.sathi.utils.fonts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by shivangis on 10/24/2018.
 */

@SuppressLint("AppCompatCustomView")
public class CustomTextBold extends TextView {
    public CustomTextBold(Context context) {
        super(context);
        setFont();
    }

    public CustomTextBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }

    public CustomTextBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    private void setFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Medium.ttf");
        setTypeface(font, Typeface.NORMAL);
    }
}