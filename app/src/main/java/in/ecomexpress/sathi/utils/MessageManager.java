package in.ecomexpress.sathi.utils;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import in.ecomexpress.sathi.R;

public class MessageManager {
    private static final boolean logger = false;

    public static void showSampleToast(Context context, String message) {
        if (logger) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showToast(Context context, String message) {
        Toast toast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT);
        View view= toast.getView();
        if (view!=null){
            view.setBackgroundColor(ContextCompat.getColor(context,R.color.background_color));
        }
        toast.show();
    }
}

