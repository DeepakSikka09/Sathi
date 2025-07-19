package in.ecomexpress.sathi.ui.drs.secure_delivery;

import android.content.Context;

import in.ecomexpress.sathi.repo.local.data.eds.EdsCommit;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.repo.local.data.rvp.RvpCommit;

public interface ISecureDeliveryNavigator {


    void onOTPLayoutVisibile();
    void onOtpVerifyButton();
    void onOtpResendButton();
    void onUndelivered(ForwardCommit forwardCommit);
    void onDelivered();
    void onBPIDClick(ForwardCommit forwardCommit);
    void onOtpResendSuccess();
    void onOtpVerifySuccess(String type);
    void onHandleError(String errorResponse);
    void getAlert();
    void clearStack();
    void showerrorMessage(String error);
    void onScanClick();
    void checkDisputedStatus(Boolean aBoolean);
    void onPinVerifyButton();

    void onPinBVerifyClick();

    void onRVPUndelivered(RvpCommit rvpCommit);

    void onEDSUndelivered(EdsCommit edsCommit);

    void mResultReceiver1(String strBarcodeScan);

    void sendEncryptedOtp(String otp);

    void callAmazon();

    void onOtherMobile(boolean i);

    void onMobileNoChange();

    void clickOnOtherNumber();

    Context getActivity();

    void resendSMS(Boolean alternateclick);

    void resendVoiceCall();
}
