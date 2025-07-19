package in.ecomexpress.sathi.ui.drs.rts.rts_signature;

import android.content.Context;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.Details;

public interface IRTSSignatureNavigator {

    void saveSignature();

    void showErrorSnackbar(String message);

    void openSuccessActivity();

    void onRTSItemFetched(Details details);

    void setConsigneeDistance(int meter);

    void getAlert();

    void showOTPLayout(boolean b);

    String getOTP();

    void showMessage(String description);

    void hideKeyboard();

    void showCounter();

    Context getActivity();

    void clearStack();

    void setPODImageVisibility(Details details);

    void showRTSWidgets(String description);

    void setOTPVerify(boolean b, String description);

    void onOtpResendSuccess(String description);

    void doLogout(String description);

    void onOtpVerifySuccess(String s);
}