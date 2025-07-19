package in.ecomexpress.sathi.ui.drs.rvp.qc_failure_list;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logButtonEventInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityRvpQcResultScreenBinding;
import in.ecomexpress.sathi.repo.local.data.rvp.RvpCommit;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.drs.rvp.signature.RVPSignatureActivity;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class RvpQcFailedActivity extends BaseActivity<ActivityRvpQcResultScreenBinding, RVPQcFailureViewModel> implements IRVPQcFailureNavigator {

    private final String TAG = RvpQcFailedActivity.class.getSimpleName();
    @Inject
    RVPQcFailureViewModel rvpQcFailureViewModel;
    String awb = "";
    ActivityRvpQcResultScreenBinding activityRvpQcResultScreenBinding;
    DRSReverseQCTypeResponse drsReverseQCTypeResponse;
    List<RvpCommit.QcWizard> qcWizards = null;
    List<RvpCommit.QcWizard> qcWizard = null;
    String composite_key = null;
    @Inject
    QcFailAdapter qcFailAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logScreenNameInGoogleAnalytics(TAG, this);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.rvp));
        try {

            qcWizards = new ArrayList<>();
            activityRvpQcResultScreenBinding = getViewDataBinding();
            rvpQcFailureViewModel.setNavigator(this);
            if (getIntent().getParcelableArrayListExtra("data") != null) {
                qcWizard = getIntent().getParcelableArrayListExtra("data");
                for (RvpCommit.QcWizard wizard : Objects.requireNonNull(qcWizard)) {
                    boolean isFound = false;
                    // check if the event name exists in noRepeat
                    for (RvpCommit.QcWizard e : qcWizards) {
                        if (e.getQccheckcode().equals(wizard.getQccheckcode()) || (e.equals(wizard))) {
                            isFound = true;
                            break;
                        }
                    }
                    if (!isFound) qcWizards.add(wizard);
                }
            } else {
                qcWizards = null;
            }

            rvpQcFailureViewModel.getAllQcFailList(qcWizards);
            awb = String.valueOf(Objects.requireNonNull(getIntent().getExtras()).getLong("awb", 0));
            composite_key = getIntent().getExtras().getString(Constants.COMPOSITE_KEY, "");

            drsReverseQCTypeResponse = getIntent().getParcelableExtra("rvp");
            rvpQcFailureViewModel.setData(drsReverseQCTypeResponse);
            logButtonEventInGoogleAnalytics(TAG, "RVPQcFailed", "Awb " + awb, this);
        } catch (Exception e) {
            Logger.e(RvpQcFailedActivity.class.getName(), e.getMessage());
            showSnackbar(e.getMessage());
        }
        setUp();
    }

    @Override
    public RVPQcFailureViewModel getViewModel() {
        return rvpQcFailureViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_rvp_qc_result_screen;
    }

    public static Intent getStartIntent(Context context) {
        return new Intent(context, RvpQcFailedActivity.class);
    }

    @Override
    public void onBack() {
        super.onBackPressed();
        applyTransitionToBackFromActivity(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        applyTransitionToBackFromActivity(this);
    }

    @Override
    public void onSetAdapter(List<RvpCommit.QcWizard> qcWizardList) {
        qcFailAdapter.setData(qcWizardList);
        qcFailAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNext() {
        openSignatureActivity();
    }

    private void openSignatureActivity() {
        try {
            Intent intent = RVPSignatureActivity.getStartIntent(this);
            if (qcWizards != null) {
                intent.putParcelableArrayListExtra(getString(R.string.data), new ArrayList<>(qcWizards));
            } else {
                intent.putParcelableArrayListExtra(getString(R.string.data), null);
            }
            intent.putExtra("awb", awb);
            intent.putExtra("rvp", drsReverseQCTypeResponse);
            intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
            intent.putExtra("navigation", "fail");
            startActivity(intent);
            finish();
            applyTransitionToOpenActivity(this);
        } catch (Exception ex) {
            showSnackbar(ex.getMessage());
            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(ex.getCause());
            restApiErrorHandler.writeErrorLogs(0, ex.getMessage());
            Logger.e(RvpQcFailedActivity.class.getName(), ex.getMessage());
        }
    }

    private void setUp() {
        activityRvpQcResultScreenBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        activityRvpQcResultScreenBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        activityRvpQcResultScreenBinding.recyclerView.setAdapter(qcFailAdapter);
    }

}
