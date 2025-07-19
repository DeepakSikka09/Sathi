package in.ecomexpress.sathi.utils.fonts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by dhananjayk on 07-02-2017.
 */

@SuppressLint("AppCompatCustomView")
public class CustomEditRegular extends EditText {
    public CustomEditRegular(Context context) {
        super(context);
        setFont();
    }

    public CustomEditRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }

    public CustomEditRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    private void setFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/cabin_r.otf");
        setTypeface(font, Typeface.NORMAL);
    }
}