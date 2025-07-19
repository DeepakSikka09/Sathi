package in.ecomexpress.sathi.ui.auth.changepassword;

import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityChangePasswordBinding;
import in.ecomexpress.sathi.repo.remote.model.login.ForgotPasswordResponse;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.base.BaseDialog;
import in.ecomexpress.sathi.utils.CommonUtils;

@AndroidEntryPoint
public class ChangePasswordActivity extends BaseDialog implements ChangePasswordNavigator {

    @Inject
    ChangePasswordViewModel changePasswordViewModel;
    private static final String TAG = ChangePasswordActivity.class.getSimpleName();
    private ActivityChangePasswordBinding activityChangePasswordBinding;
    private OnPasswordChangeListener onPasswordChangeListener;

    public static ChangePasswordActivity newInstance() {
        ChangePasswordActivity fragment = new ChangePasswordActivity();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setChangePasswordListener(OnPasswordChangeListener onPasswordChangeListener) {
        this.onPasswordChangeListener = onPasswordChangeListener;
    }

    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, TAG);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activityChangePasswordBinding = DataBindingUtil.inflate(inflater, R.layout.activity_change_password, container, false);
        View view = activityChangePasswordBinding.getRoot();
        activityChangePasswordBinding.setViewModel(changePasswordViewModel);
        logScreenNameInGoogleAnalytics(TAG, getContext());
        try {
            changePasswordViewModel.setNavigator(this);
            changePasswordViewModel.setUserId();
        } catch (Exception e) {
            showError(e.getMessage());
        }
        activityChangePasswordBinding.etOldPassword.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
        activityChangePasswordBinding.etConfirmPassword.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
        activityChangePasswordBinding.etNewPassword.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
        activityChangePasswordBinding.etOldPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});
        activityChangePasswordBinding.etNewPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});
        activityChangePasswordBinding.etConfirmPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});
        return view;
    }

    public static Intent getStartIntent(Context context) {
        return new Intent(context, ChangePasswordActivity.class);
    }

    @Override
    public void onChangePassword() {
        try {
            if (activityChangePasswordBinding.etConfirmPassword.getText().toString().trim().isEmpty() || activityChangePasswordBinding.etNewPassword.getText().toString().trim().isEmpty() || activityChangePasswordBinding.etOldPassword.getText().toString().trim().isEmpty()) {
                showError(getString(R.string.all_fields_are_mandatory));
                return;
            }
            if (CommonUtils.isStringMatch(activityChangePasswordBinding.etNewPassword.getText().toString().trim(), activityChangePasswordBinding.etConfirmPassword.getText().toString().trim())) {
                if (!isNetworkConnected()) {
                    showError(getString(R.string.check_internet));
                    return;
                }
                changePasswordViewModel.changePasswordRequest(getContext(), activityChangePasswordBinding.etEmployeeCode.getText().toString().trim(), activityChangePasswordBinding.etOldPassword.getText().toString().trim(), activityChangePasswordBinding.etConfirmPassword.getText().toString().trim());
            } else {
                showError(getString(R.string.invalid_confirm_password));
            }
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @Override
    public void doLogout(String message) {
        try {
            getBaseActivity().showInfo(message);
            changePasswordViewModel.logoutLocal();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @Override
    public void clearStack() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        onPasswordChangeListener.onSuccess();
    }

    @Override
    public void showError(String message) {
        getBaseActivity().showSnackbar(message);
    }

    @Override
    public void onSuccess(ForgotPasswordResponse forgotPasswordResponse) {
        try {
            dismiss();
            getBaseActivity().showSuccessInfo(forgotPasswordResponse.getResponse().getDescription());
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @Override
    public void onBackClick() {
        activityChangePasswordBinding.popupElement.setVisibility(View.GONE);
        this.dismiss();
    }

    public interface OnPasswordChangeListener {
        void onSuccess();
    }
}