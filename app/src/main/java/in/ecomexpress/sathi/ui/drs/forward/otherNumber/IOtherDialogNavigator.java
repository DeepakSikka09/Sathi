package in.ecomexpress.sathi.ui.drs.forward.otherNumber;



public  interface IOtherDialogNavigator {
    void checkValidation();

    void dismissDialog();
    void onClickOtherNumber();
    void onRegisterNumClick();
    void showCounter();
    void setMobListOnView();
    void showerrorMessage(String error);
}
