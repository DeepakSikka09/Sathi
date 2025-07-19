package in.ecomexpress.sathi.ui.drs.forward.disputeDailog;



public interface IDisputeDialogNavigator {
    void checkValidation();

    void dismissDialog();

    void showerrorMessage(String error);

    void captureImage();

    void onClickOtherNumber();

    void onClickRegisteredNumber();

    void onOtpSubmitClick();

    void onHandleError(String description);

    void showSucess(String description);

    void setSucessData();

    void onVerifyClick();

    void closeDispute();

    void openSignatureActivity();

    void showImageSuccess();
}
