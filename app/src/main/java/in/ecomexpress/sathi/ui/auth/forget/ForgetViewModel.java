package in.ecomexpress.sathi.ui.auth.forget;

import static in.ecomexpress.sathi.utils.CommonUtils.logButtonEventInGoogleAnalytics;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.login.ForgetPasswordUserRequest;
import in.ecomexpress.sathi.repo.remote.model.login.ForgotPasswordResponse;
import in.ecomexpress.sathi.repo.remote.model.login.OTPVerifyWithPasswordRequest;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;


@HiltViewModel
public class ForgetViewModel extends BaseViewModel<IForgetNavigator> {

    private final String TAG = ForgetViewModel.class.getSimpleName();
    private Dialog dialog;

    @Inject
    public ForgetViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider , Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }

    public void onServerLoginClick() {
        getNavigator().onServerLogin();
    }

    public void onBackClick() {
        getNavigator().onBackClicked();
    }

    public void forgetPassword(Context context, String email) {
        showProgressDialog(context, context.getString(R.string.verifying));
        ForgetPasswordUserRequest request = new ForgetPasswordUserRequest(email);
        try {
        getCompositeDisposable().add(getDataManager()
            .doForgetPasswordApiCall(getDataManager().getEcomRegion(),request).doOnSuccess(response -> {})
            .subscribeOn(getSchedulerProvider().io())
            .observeOn(getSchedulerProvider().ui())
            .subscribe((ForgotPasswordResponse response) -> {
                dismissProgressDialog();
                try{
                    if(response == null){
                        getNavigator().showError(context.getString(R.string.api_response_null));
                        return;
                    }
                    if (response.isStatus()) {
                        logButtonEventInGoogleAnalytics(TAG, context.getString(R.string.forgetpasswordbutton), "", context);
                        getNavigator().ViewFlag();
                    } else {
                        String errorDescription = (response.getResponse() == null || response.getResponse().getDescription() == null) ? context.getString(R.string.api_response_false) : response.getResponse().getDescription();
                        getNavigator().showError(errorDescription);
                    }
                } catch (Exception e){
                    getNavigator().showError(e.getMessage());
                }
            }, throwable -> {
                dismissProgressDialog();
                getNavigator().showError(throwable.getMessage());
            }));
        } catch (Exception e){
            dismissProgressDialog();
            getNavigator().showError(e.getMessage());
        }
    }

    public void otpVerifyWithPassword(Context context, String userName, String otp, String newPassword) {
        showProgressDialog(context, context.getString(R.string.verifying));
        OTPVerifyWithPasswordRequest request = new OTPVerifyWithPasswordRequest(userName, otp, newPassword);
        try {
        getCompositeDisposable().add(getDataManager()
            .doOTPVerifyWithPasswordApiCall(getDataManager().getEcomRegion(),request).doOnSuccess(response -> {})
            .subscribeOn(getSchedulerProvider().io())
            .observeOn(getSchedulerProvider().ui())
            .subscribe((ForgotPasswordResponse response) -> {
                dismissProgressDialog();
                try{
                    if(response == null){
                        getNavigator().showError(context.getString(R.string.api_response_null));
                        return;
                    }
                    if (response.isStatus()) {
                        logButtonEventInGoogleAnalytics(TAG, context.getString(R.string.otpverifywithpassword), "", context);
                        getNavigator().onSuccessFullyChangePassword(response.getResponse().getDescription());
                    } else {
                        String errorDescription = (response.getResponse() == null || response.getResponse().getDescription() == null) ? context.getString(R.string.api_response_false) : response.getResponse().getDescription();
                        getNavigator().showError(errorDescription);
                    }
                } catch (Exception e){
                    getNavigator().showError(e.getMessage());
                }
            }, throwable -> {
                dismissProgressDialog();
                getNavigator().showError(throwable.getMessage());
            }));
        } catch (Exception e){
            getNavigator().showError(e.getMessage());
        }
    }

    public boolean isUserIdValid(String userId) {
        return userId != null && !userId.isEmpty();
    }

    private void showProgressDialog(Context context, String loadingMessage) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_progress_dialog);
        dialog.setCancelable(false);
        TextView loadingText = dialog.findViewById(R.id.dialog_loading_text);
        loadingText.setText(loadingMessage);
        dialog.show();
    }

    private void dismissProgressDialog(){
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }
}