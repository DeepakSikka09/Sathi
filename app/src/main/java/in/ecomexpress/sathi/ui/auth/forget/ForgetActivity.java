package in.ecomexpress.sathi.ui.auth.forget;

import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import in.ecomexpress.sathi.databinding.ForgetPasswordActivityBinding;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.base.BaseDialog;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.MessageManager;

@AndroidEntryPoint
public class ForgetActivity extends BaseDialog implements IForgetNavigator {

    private static final String TAG = ForgetActivity.class.getSimpleName();
    @Inject
    ForgetViewModel mForgetViewModel;
    @SuppressLint("StaticFieldLeak")
    static Context context;
    ForgetPasswordActivityBinding forgetBinding;

    public static ForgetActivity newInstance(Activity mContext) {
        ForgetActivity fragment = new ForgetActivity();
        context = mContext;
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        forgetBinding = DataBindingUtil.inflate(inflater, R.layout.forget_password_activity, container, false);
        View view = forgetBinding.getRoot();

        forgetBinding.setViewModel(mForgetViewModel);
        mForgetViewModel.setNavigator(this);
        logScreenNameInGoogleAnalytics(TAG, context);
        setCancelable(false);
        forgetBinding.etEmployeeCode.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
        forgetBinding.etNewPassword.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
        forgetBinding.etConfirmPassword.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
        forgetBinding.etEmployeeCode.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });
        forgetBinding.etNewPassword.setFilters(new InputFilter[] { new InputFilter.LengthFilter(25) });
        forgetBinding.etConfirmPassword.setFilters(new InputFilter[] { new InputFilter.LengthFilter(25) });
        return view;
    }

    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, TAG);
    }

    @Override
    public void onServerLogin() {
        try {
        if (mForgetViewModel.isUserIdValid(forgetBinding.etEmployeeCode.getText().toString().trim())) {
            if (isNetworkConnected()) {
                if (!forgetBinding.etOtp.isShown()) {
                    mForgetViewModel.forgetPassword(context, forgetBinding.etEmployeeCode.getText().toString().trim());
                    forgetBinding.btnForgetPassword.setEnabled(false);
                } else {
                    if (!(forgetBinding.etOtp.getText().toString().trim().length() == 6)) {
                        MessageManager.showToast(context, getString(R.string.enter_valid_otp));
                        return;
                    }
                    if (forgetBinding.etNewPassword.getText().toString().trim().isEmpty() || forgetBinding.etConfirmPassword.getText().toString().trim().isEmpty()) {
                        MessageManager.showToast(context, getString(R.string.please_enter_new_password_and_confirm_password));
                        return;
                    }
                    if (!CommonUtils.isStringMatch(forgetBinding.etNewPassword.getText().toString().trim(), forgetBinding.etConfirmPassword.getText().toString().trim())) {
                        MessageManager.showToast(context, getString(R.string.invalid_confirm_password));
                        return;
                    }
                    mForgetViewModel.otpVerifyWithPassword(context, forgetBinding.etEmployeeCode.getText().toString().trim(), forgetBinding.etOtp.getText().toString().trim(), forgetBinding.etConfirmPassword.getText().toString().trim());
                }
            } else {
                MessageManager.showToast(context, getString(R.string.check_internet));
            }
        } else {
            MessageManager.showToast(context, getString(R.string.please_enter_user_id));
        }
        } catch (Exception e){
            showError(e.getMessage());
        }
    }

    @Override
    public void ViewFlag() {
        forgetBinding.btnForgetPassword.setEnabled(true);
        forgetBinding.etEmployeeCode.setEnabled(false);
        forgetBinding.otpLayout.setVisibility(View.VISIBLE);
        forgetBinding.etNewpassLayout.setVisibility(View.VISIBLE);
        forgetBinding.etConfirmPassLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuccessFullyChangePassword(String statusMessage) {
        hideKeyboard(getActivity());
        MessageManager.showToast(context, statusMessage);
        dismiss();
        startActivity(LoginActivity.getStartIntent(context));
        forgetBinding.popupElement.setVisibility(View.GONE);
    }

    @Override
    public void onBackClicked() {
        dismissDialog(TAG);
        forgetBinding.popupElement.setVisibility(View.GONE);
    }

    @Override
    public void showError(String e) {
        getBaseActivity().showSnackbar(e);
        forgetBinding.btnForgetPassword.setEnabled(true);
    }
}