package in.ecomexpress.sathi.ui.drs.forward.otherNumber;

import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.OtherNumDialogBinding;
import in.ecomexpress.sathi.ui.base.BaseDialog;
import in.ecomexpress.sathi.ui.drs.forward.details.ForwardDetailActivity;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class OtherNumberDialog extends BaseDialog implements IOtherDialogNavigator {

    private final String TAG = OtherNumberDialog.class.getSimpleName();
    MyDialogCloseListener myDialogCloseListener;
    public Activity c;
    @SuppressLint("StaticFieldLeak")
    static Activity context;
    OtherNumDialogBinding otherNumDialogBinding;
    @Inject
    OtherNumberDialogViewModel otherNumberDialogViewModel;
    static String awb_no = "", drs_id_no = "";
    static boolean resend_flag;
    ArrayList<String> mob_list = new ArrayList<>();


    public void setMyDialogCloseListener(MyDialogCloseListener listener) {
        this.myDialogCloseListener = listener;
    }

    public static OtherNumberDialog newInstance(Activity getcontext, String awb, String drs_id, boolean flag) {
        OtherNumberDialog fragment = new OtherNumberDialog();
        awb_no = awb;
        drs_id_no = drs_id;
        context = getcontext;
        resend_flag = flag;
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, "");
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        otherNumDialogBinding = DataBindingUtil.inflate(inflater, R.layout.other_num_dialog, container, false);
        View view = otherNumDialogBinding.getRoot();
        try {
            otherNumDialogBinding.setViewModel(otherNumberDialogViewModel);
            otherNumberDialogViewModel.setNavigator(this);
            logScreenNameInGoogleAnalytics(TAG, context);
            otherNumberDialogViewModel.getAllApiUrl(awb_no);
            otherNumDialogBinding.llChooseBtn.setVisibility(View.VISIBLE);
            otherNumDialogBinding.edtNum.setVisibility(View.GONE);
            otherNumDialogBinding.btnSubmit.setVisibility(View.GONE);
            otherNumDialogBinding.txtOther.setVisibility(View.GONE);
        } catch (Exception e) {
            Logger.e(OtherNumberDialog.class.getName(), e.getMessage());

        }


        return view;
    }


    @Override
    public void dismissDialog() {
        dismissDialog("");
        otherNumDialogBinding.popupElement.setVisibility(View.GONE);
    }

    @Override
    public void onClickOtherNumber() {
        if (resend_flag) {
            otherNumDialogBinding.edtNum.setVisibility(View.VISIBLE);
            otherNumDialogBinding.btnSubmit.setVisibility(View.VISIBLE);
            otherNumDialogBinding.llChooseBtn.setVisibility(View.GONE);
            otherNumDialogBinding.txtOther.setVisibility(View.GONE);
        } else {
            otherNumDialogBinding.edtNum.setVisibility(View.VISIBLE);
            otherNumDialogBinding.btnSubmit.setVisibility(View.VISIBLE);
            otherNumDialogBinding.llChooseBtn.setVisibility(View.GONE);
            otherNumDialogBinding.txtOther.setVisibility(View.VISIBLE);

        }
        if (otherNumDialogBinding.edtNum.getText().toString().trim().length() == 10) {
            getBaseActivity().showToast("Click on submit");

        } else {
            getBaseActivity().showToast("Please enter number.");
        }

    }

    @Override
    public void onRegisterNumClick() {
        otherNumDialogBinding.edtNum.setVisibility(View.GONE);
        otherNumDialogBinding.btnSubmit.setVisibility(View.GONE);
        otherNumDialogBinding.txtOther.setVisibility(View.GONE);
        otherNumberDialogViewModel.sendSmsLinkPayphi(awb_no, context, drs_id_no, "");
    }

    @Override
    public void showCounter() {

        OtherNumberDialog.context.runOnUiThread(() -> {
            OtherNumberDialog.context.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            );
            ((ForwardDetailActivity) context).countDown();

        });

    }

    @Override
    public void setMobListOnView() {
        mob_list.add(otherNumberDialogViewModel.mob_list.get().get(0));
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.select_dialog_item, mob_list);
        otherNumDialogBinding.edtNum.setThreshold(1);
        otherNumDialogBinding.edtNum.setAdapter(adapter);

    }

    @Override
    public void checkValidation() {
        if (otherNumDialogBinding.edtNum.getText().toString().trim().length() == 10) {
            ArrayList<String> mob_list = new ArrayList<>();
            mob_list.add(otherNumDialogBinding.edtNum.getText().toString());
            otherNumberDialogViewModel.saveApiUrl(mob_list, awb_no);
            otherNumberDialogViewModel.sendSmsLinkPayphi(awb_no, context, drs_id_no, otherNumDialogBinding.edtNum.getText().toString());
            dismissDialog();

        } else if (otherNumDialogBinding.edtNum.getText().toString().trim().length() == 0 || otherNumDialogBinding.edtNum.getText().toString() == null || otherNumDialogBinding.edtNum.getText().toString().isEmpty()) {
            getBaseActivity().showToast("Please enter mobile number.");

        } else {
            getBaseActivity().showToast("Please enter correct mobile number.");

        }
    }

    @Override
    public void showerrorMessage(String error) {
        try {
            if (error != null) {
                OtherNumberDialog.context.runOnUiThread(() -> {
                    OtherNumberDialog.context.getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                    );
                    Toast.makeText(OtherNumberDialog.context, error, Toast.LENGTH_SHORT).show();

                });
            }
        } catch (Exception e) {
            Logger.e(OtherNumberDialog.class.getName(), e.getMessage());

            getBaseActivity().showSnackbar(e.getMessage());
        }

    }

}
