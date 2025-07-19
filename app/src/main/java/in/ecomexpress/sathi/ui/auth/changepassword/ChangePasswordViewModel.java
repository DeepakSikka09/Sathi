package in.ecomexpress.sathi.ui.auth.changepassword;

import static in.ecomexpress.sathi.utils.CommonUtils.logButtonEventInGoogleAnalytics;

import androidx.databinding.ObservableField;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.login.ChangePasswordRequest;
import in.ecomexpress.sathi.repo.remote.model.login.ForgotPasswordResponse;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class ChangePasswordViewModel extends BaseViewModel<ChangePasswordNavigator> {

    private static final String TAG = ChangePasswordViewModel.class.getSimpleName();
    private final ObservableField<String> empCode = new ObservableField<>();
    private Dialog dialog;

    public ObservableField<String> getCode() {
        return empCode;
    }

    @Inject
    public ChangePasswordViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }

    public void onChangePasswordClick() {
        getNavigator().onChangePassword();
    }

    public void changePasswordRequest(Context context, String username, String oldPassword, String newPassword) {
        showProgressDialog(context, context.getString(R.string.changing_password));
        ChangePasswordRequest request = new ChangePasswordRequest(username, oldPassword, newPassword);
        try {
            getCompositeDisposable().add(getDataManager()
                .doResetPasswordApiCall(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), request)
                .doOnSuccess(response -> Logger.e(TAG, response + ""))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe((ForgotPasswordResponse response) -> {
                    dismissProgressDialog();
                    try {
                        if(response == null){
                            getNavigator().showError(context.getString(R.string.api_response_null));
                            return;
                        }
                        if (response.isStatus()) {
                            logButtonEventInGoogleAnalytics(TAG, context.getString(R.string.changepasswordrequest), "", context);
                            getNavigator().onSuccess(response);
                        } else {
                            String errorDescription = (response.getResponse() == null || response.getResponse().getDescription() == null) ? context.getString(R.string.change_password_api_response_false) : response.getResponse().getDescription();
                            if (errorDescription.equalsIgnoreCase(context.getString(R.string.invalid_authentication_token))) {
                                getNavigator().doLogout(response.getResponse().getDescription());
                            } else {
                                getNavigator().showError(errorDescription);
                            }
                        }
                    } catch (Exception e) {
                        getNavigator().showError(e.getMessage());
                    }
                }, (Throwable throwable) -> {
                    dismissProgressDialog();
                    getNavigator().showError(throwable.getMessage());
                }));
        } catch (Exception e){
            dismissProgressDialog();
            getNavigator().showError(e.getMessage());
        }
    }

    public void onBackClick() {
        getNavigator().onBackClick();
    }

    public void setUserId() {
        final String code = getDataManager().getCode();
        if (!TextUtils.isEmpty(code)) {
            empCode.set(code);
        }
    }

    public void logoutLocal() {
        try {
            getDataManager().setTripId("");
            getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT);
            clearAppData();
        } catch (Exception e){
            getNavigator().showError(e.getMessage());
        }
    }

    private void clearAppData() {
        try {
        getCompositeDisposable().add(getDataManager()
            .deleteAllTables().subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui())
            .subscribe(aBoolean -> {
                try {
                    getDataManager().clearPrefrence();
                    getDataManager().setUserAsLoggedOut();
                } catch (Exception e) {
                    getNavigator().showError(e.getMessage());
                }
                getNavigator().clearStack();
        }));
        } catch (Exception e){
            getNavigator().showError(e.getMessage());
        }
    }

    private void showProgressDialog(Context context, String loadingMessage) {
        dialog = new Dialog(context,android.R.style.Theme_Material_Light_Dialog);
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