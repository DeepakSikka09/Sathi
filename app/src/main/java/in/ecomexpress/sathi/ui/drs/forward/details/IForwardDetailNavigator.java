package in.ecomexpress.sathi.ui.drs.forward.details;

import android.app.Activity;

import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;

public interface IForwardDetailNavigator {
    void checkValidation();

    void dismissDialog();

    //next button
    void onNext(ForwardCommit forwardCommit);

    //back button


    void raiseDispute();

    void onNextAmountClick(ForwardCommit forwardCommit);

    //visibility handler for otp
    void onOTPLayoutVisibile();

    Activity getContextProvider();


    //visibility handler for ecod
    void onPayeCODLayoutVisibile();

    //card button
    void onCardPaymentSelected();

    //cash button
    void onCashPaymentSelected();

    //msg link button
    void onMsgLinkSelected();

    void changeLinkText();

    void showNumberPopup();


    void onProgressTimer(int seconds);

    void onProgressFinishCount();


    //ecod button
    void onEcodClick();

    //verify button
    void onOtpVerifyButton();

    //resend button
    void onOtpResendButton();

    //undelivered button
    void onUndelivered(ForwardCommit forwardCommit);

    //signature activity
    void openSignatureActivity(String type, String amount);

    //onOtpResendSuccess
    void onOtpResendSuccess();

    //onOtpVerifySuccess
    void onOtpVerifySuccess(Boolean flag);

    //otpverify handle error response
    void onHandleError(String errorResponse);

    void showUndeliveredOption();

    void getAlert();

    void clearStack();

    void onProceedtoCash();

    void showerrorMessage(String error);

    void onerrorMsg(String message);

    void setvisibility();

    void mResultReceiver1(String strScancode);


    void openSignatureActivityDispute(String ecod, String s);

    void showDisputeButton();

    void setCardClicked();

    boolean validateDigitalClick();

    void checkDisputedStatus(Boolean aBoolean);

    void ontextchange(String sttype);
}
