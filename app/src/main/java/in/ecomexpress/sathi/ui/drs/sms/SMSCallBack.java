package in.ecomexpress.sathi.ui.drs.sms;

public interface SMSCallBack {

    void dismissDialog();

    void cancel();

    void onHandleError(String errorResponse);

    void doLogout(String message);

    void onSendClick();

    void time(String s);

    void location(String s);

    void showErrorMessage(boolean status);

    void showError(String e);
}