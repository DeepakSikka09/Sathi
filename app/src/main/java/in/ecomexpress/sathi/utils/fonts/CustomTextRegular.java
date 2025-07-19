package in.ecomexpress.sathi.utils.fonts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;


@SuppressLint("AppCompatCustomView")
public class CustomTextRegular extends TextView {
    public CustomTextRegular(Context context) {
        super(context);
        setFont();
    }

    public CustomTextRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }

    public CustomTextRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    private void setFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        setTypeface(font, Typeface.NORMAL);
    }
}