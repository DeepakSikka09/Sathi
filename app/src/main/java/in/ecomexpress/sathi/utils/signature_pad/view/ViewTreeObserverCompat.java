package in.ecomexpress.sathi.utils.signature_pad.view;

import android.annotation.SuppressLint;
import android.view.ViewTreeObserver;

public class ViewTreeObserverCompat {

    @SuppressLint("NewApi")
    public static void removeOnGlobalLayoutListener(ViewTreeObserver observer, ViewTreeObserver.OnGlobalLayoutListener victim) {
        observer.removeOnGlobalLayoutListener(victim);
    }
}