package in.ecomexpress.sathi.ui.dashboard.switchnumber;

import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivitySwitchNumberBinding;
import in.ecomexpress.sathi.repo.remote.model.masterdata.CbPstnOptions;
import in.ecomexpress.sathi.ui.base.BaseDialog;
import in.ecomexpress.sathi.utils.Constants;

@AndroidEntryPoint
public class SwitchNumberDialog extends BaseDialog implements SwitchNumberCallBack {

    private static final String TAG = SwitchNumberDialog.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    static Activity context;
    @Inject
    SwitchNumberViewModel switchNumberViewModel;
    ActivitySwitchNumberBinding activitySwitchNumberBinding;
    String currentFormat;
    @Inject
    SwitchNumberListAdapter switchNumberListAdapter;
    String setPstnFormat = null;

    public static SwitchNumberDialog newInstance(Activity getContext) {
        SwitchNumberDialog fragment = new SwitchNumberDialog();
        context = getContext;
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activitySwitchNumberBinding = DataBindingUtil.inflate(inflater, R.layout.activity_switch_number, container, false);
        View view = activitySwitchNumberBinding.getRoot();
        logScreenNameInGoogleAnalytics(TAG, context);
        activitySwitchNumberBinding.setViewModel(switchNumberViewModel);
        try {
            switchNumberViewModel.setNavigator(this);
            switchNumberViewModel.getCbPstnOptions();
        } catch (Exception e) {
            showError(e.getMessage());
        }
        try {
            currentFormat = switchNumberViewModel.getDataManager().getPstnFormat();
            if (!currentFormat.isEmpty()) {
                if (currentFormat.contains(Constants.pstn_pin)) {
                    setPstnFormat = currentFormat.replaceAll(",@@PIN@@#", "");
                } else if (currentFormat.contains(Constants.pstn_awb)) {
                    setPstnFormat = currentFormat.replaceAll(",@@AWB@@#", "");
                }
                activitySwitchNumberBinding.current.setText("Current Number : " + setPstnFormat);
            } else {
                activitySwitchNumberBinding.current.setText("Please Sync Data");
            }
        } catch (Exception e) {
            showError(e.getMessage());
        }
        setUp();
        return view;
    }

    private void setUp() {
        try {
            activitySwitchNumberBinding.fuelRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            activitySwitchNumberBinding.fuelRecyclerView.setItemAnimator(new DefaultItemAnimator());
            activitySwitchNumberBinding.fuelRecyclerView.setAdapter(switchNumberListAdapter);
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, TAG);
    }

    @Override
    public void dismissDialog() {
        dismissDialog(TAG);
        activitySwitchNumberBinding.popupElement.setVisibility(View.GONE);
    }

    @Override
    public void cancel() {
        dismissDialog(TAG);
        activitySwitchNumberBinding.popupElement.setVisibility(View.GONE);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void OnSetFuelAdapter(List<CbPstnOptions> cbPstnOptions) {
        switchNumberListAdapter.setData(cbPstnOptions, switchNumberViewModel);
        switchNumberListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSubmitNumber() {
        try {
            String getPstnFormat = switchNumberListAdapter.getPstnFormat();
            if (getPstnFormat != null) {
                switchNumberViewModel.getDataManager().setPstnFormat(getPstnFormat);
                Toast.makeText(context, "CallBridge Number Switched", Toast.LENGTH_SHORT).show();
                dismissDialog(TAG);
                activitySwitchNumberBinding.popupElement.setVisibility(View.GONE);
            } else {
                Toast.makeText(context, "Please Choose A Number.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @Override
    public void showError(String e) {
        getBaseActivity().showSnackbar(e);
    }
}