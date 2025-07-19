package in.ecomexpress.sathi.ui.drs.forward.fill_awb;

import static in.ecomexpress.sathi.utils.CommonUtils.logButtonEventInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.LayoutFillAwbBinding;
import in.ecomexpress.sathi.ui.base.BaseDialog;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class AwbPopupDialog extends BaseDialog implements IAwbDialogNavigator {

    private final String TAG = AwbPopupDialog.class.getSimpleName();
    static String awb_no = "";
    static Activity context;
    LayoutFillAwbBinding layoutFillAwbBinding;
    @Inject
    AwbPopupDialogViewModel awbPopupDialogViewModel;
    MyDialogCloseListener myDialogCloseListener;

    public static AwbPopupDialog newInstance(Activity getContext, String awb) {
        AwbPopupDialog fragment = new AwbPopupDialog();
        awb_no = awb;
        context = getContext;
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void showerrorMessage(String error) {

    }

    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, "");
    }

    @Override
    public void dismissDialog() {
        dismissDialog("");
        layoutFillAwbBinding.popupElement.setVisibility(View.GONE);

    }

    @Override
    public void onSubmitClick() {
        if (layoutFillAwbBinding.edtNum.getText().length() == 0) {
            Toast.makeText(context, "Please Enter AWB number", Toast.LENGTH_SHORT).show();
        } else {
            logButtonEventInGoogleAnalytics(TAG, "ManuallyEnteredAwbFwdOnSubmitClick", "Awb " + layoutFillAwbBinding.edtNum.getText().toString(), context);
            if (layoutFillAwbBinding.edtNum.getText().toString().equalsIgnoreCase(awb_no)) {
                myDialogCloseListener.setStatusOfAwb(true);
                dismissDialog();
            } else {
                Toast.makeText(context, "Please enter correct AWB number", Toast.LENGTH_SHORT).show();

            }
        }

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutFillAwbBinding = DataBindingUtil.inflate(inflater, R.layout.layout_fill_awb, container, false);
        View view = layoutFillAwbBinding.getRoot();
        logScreenNameInGoogleAnalytics(TAG, context);
        try {
            layoutFillAwbBinding.setViewModel(awbPopupDialogViewModel);
            awbPopupDialogViewModel.setNavigator(this);

        } catch (Exception e) {
            Logger.e(AwbPopupDialog.class.getName(), e.getMessage());
        }


        return view;
    }

    public void setListener(MyDialogCloseListener myDialogCloseListener) {
        this.myDialogCloseListener = myDialogCloseListener;
    }

}
