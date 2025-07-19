package in.ecomexpress.sathi.ui.drs.forward.obd.navigator;

import android.graphics.Bitmap;

public interface IFwdObdQcNavigator {

    void showError(String error);

    void setCapturedImageBitmap(Bitmap imageBitmap);
}