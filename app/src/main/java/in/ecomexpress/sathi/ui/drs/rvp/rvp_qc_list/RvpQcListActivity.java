package in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_list;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.Constants.INTENT_KEY_RVP_SAMPLE_QUESTIONS;
import static in.ecomexpress.sathi.utils.Constants.INTENT_KEY_RVP_WITH_QC;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityRvpQcListBinding;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.drs.rvp.awbscan.CaptureScanActivity;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.RvpQcDataDetailsActivity;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.qc_check_image.PreviewImageAdapterList;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_secure_activity.RVPSecureDeliveryActivity;
import in.ecomexpress.sathi.ui.drs.rvp.undelivered.RVPUndeliveredActivity;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class RvpQcListActivity extends BaseActivity<ActivityRvpQcListBinding, RvpQcListViewModel> implements IRvpQcListNavigator {

    private final String TAG = RvpQcListActivity.class.getSimpleName();
    @Inject
    RvpQcListViewModel rvpQcListViewModel;
    ActivityRvpQcListBinding activityRvpQcListBinding;
    Long awbNo = null;
    String getDrsApiKey = null;
    String getDrsPstKey = null;
    String getDrsPin = null;
    String composite_key = null;
    int drs_id;
    boolean call_allowed;
    String OFD_OTP;
    String CONSIGNEE_ALTERNATE_MOBILE;
    String CONSIGNEE_MOBILE = "";
    boolean is_secure_delivery = false;
    String otp_required_for_delivery = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        awbNo = getIntent().getLongExtra(Constants.INTENT_KEY, 0);
        logScreenNameInGoogleAnalytics(TAG, this);
        try {
            rvpQcListViewModel.setNavigator(this);
            activityRvpQcListBinding = getViewDataBinding();
            composite_key = getIntent().getStringExtra(Constants.COMPOSITE_KEY);
            getDrsPin = Objects.requireNonNull(getIntent().getExtras()).getString(Constants.DRS_PIN);
            drs_id = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra(Constants.DRS_ID_NUM)));
            CONSIGNEE_ALTERNATE_MOBILE = getIntent().getStringExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE);
            CONSIGNEE_MOBILE = getIntent().getStringExtra(Constants.CONSIGNEE_MOBILE);
            otp_required_for_delivery = getIntent().getStringExtra(Constants.otp_required_for_delivery);
            OFD_OTP = getIntent().getStringExtra(Constants.OFD_OTP);
            call_allowed = getIntent().getBooleanExtra("call_allowed", false);
            rvpQcListViewModel.getRvpDataWithQc(composite_key.trim());
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.rvp));
            activityRvpQcListBinding.consigneeName.setMovementMethod(new ScrollingMovementMethod());
            activityRvpQcListBinding.consigneeAddress.setMovementMethod(new ScrollingMovementMethod());
            getDrsApiKey = getIntent().getExtras().getString(Constants.DRS_API_KEY);
            getDrsPstKey = getIntent().getExtras().getString(Constants.DRS_PSTN_KEY);
            is_secure_delivery = getIntent().getBooleanExtra(Constants.IS_SECURE_DELIVERY, false);

        } catch (Exception e) {
            Logger.e(RvpQcListActivity.class.getName(), e.getMessage());
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public RvpQcListViewModel getViewModel() {
        return rvpQcListViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_rvp_qc_list;
    }

    public static Intent getStartIntent(Context context) {
        return new Intent(context, RvpQcListActivity.class);
    }

    @Override
    public void onUnsuccessful() {
        try {
            Intent intent = RVPUndeliveredActivity.getStartIntent(this);
            intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstKey);
            intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
            intent.putExtra(Constants.DRS_PIN, getDrsPin);
            intent.putExtra(Constants.DRS_ID, drs_id);
            intent.putExtra("awb", awbNo);
            intent.putExtra("call_allowed", call_allowed);
            intent.putExtra(Constants.INTENT_KEY, Long.parseLong(Objects.requireNonNull(rvpQcListViewModel.getAwbNo().get())));
            intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
            intent.putExtra(INTENT_KEY_RVP_WITH_QC, rvpQcListViewModel.getRvpWithQc());
            intent.putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, CONSIGNEE_ALTERNATE_MOBILE);
            intent.putExtra(Constants.SHIPMENT_TYPE, rvpQcListViewModel.getRvpWithQc().drsReverseQCTypeResponse.getShipmentDetails().getType());
            startActivity(intent);
            applyTransitionToOpenActivity(this);
        } catch (Exception e) {
            Logger.e(RvpQcListActivity.class.getName(), e.getMessage());
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void onProceed() {
        try {
            if (CommonUtils.turnGPSOn(this)) {
                Constants.RVPCOMMIT = Objects.requireNonNull(rvpQcListViewModel.rvpWithQC.get()).drsReverseQCTypeResponse.getShipmentDetails().getType();
                if ((rvpQcListViewModel.getSampleQuestions() == null || rvpQcListViewModel.getSampleQuestions().isEmpty()) && Constants.RVPCOMMIT.equalsIgnoreCase("RQC")) {
                    showError("Sync master data from side drawer");
                    return;
                }
                if (rvpQcListViewModel.getSampleQuestions() == null || rvpQcListViewModel.getSampleQuestions().isEmpty()) {
                    Intent intent = CaptureScanActivity.getStartIntent(this);
                    intent.putExtra(Constants.INTENT_KEY, awbNo);
                    intent.putExtra("awb", awbNo);
                    intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstKey);
                    intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
                    intent.putExtra(Constants.DRS_PIN, getDrsPin);
                    intent.putExtra("rvp_qc", "NO");
                    intent.putExtra("flag", false);
                    intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                    intent.putParcelableArrayListExtra(getString(R.string.data), null);
                    intent.putExtra("rvp", rvpQcListViewModel.getRvpWithQc().drsReverseQCTypeResponse);
                    startActivity(intent);
                    applyTransitionToOpenActivity(this);
                } else {
                    Intent intent;
                    rvpQcListViewModel.getDataManager().setStartQCLat(rvpQcListViewModel.getDataManager().getCurrentLatitude());
                    rvpQcListViewModel.getDataManager().setStartQCLng(rvpQcListViewModel.getDataManager().getCurrentLongitude());
                    if (is_secure_delivery) {
                        intent = RVPSecureDeliveryActivity.getStartIntent(this);
                    } else {
                        intent = RvpQcDataDetailsActivity.getStartIntent(this);
                    }
                    intent.putExtra(INTENT_KEY_RVP_WITH_QC, rvpQcListViewModel.getRvpWithQc());
                    intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstKey);
                    intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
                    intent.putExtra(Constants.DRS_PIN, getDrsPin);
                    intent.putExtra(Constants.DRS_ID, drs_id);
                    intent.putExtra(Constants.otp_required_for_delivery, otp_required_for_delivery);
                    intent.putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, CONSIGNEE_ALTERNATE_MOBILE);
                    intent.putExtra(Constants.CONSIGNEE_MOBILE, CONSIGNEE_MOBILE);
                    intent.putExtra("call_allowed", call_allowed);
                    intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                    intent.putExtra(Constants.INTENT_KEY, Long.parseLong(Objects.requireNonNull(rvpQcListViewModel.getAwbNo().get())));
                    intent.putParcelableArrayListExtra(INTENT_KEY_RVP_SAMPLE_QUESTIONS, rvpQcListViewModel.getSampleQuestions());
                    startActivity(intent);
                    applyTransitionToOpenActivity(this);
                }
            }
        } catch (Exception e) {
            Logger.e(RvpQcListActivity.class.getName(), e.getMessage());
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void OnBack() {
        super.onBackPressed();
        applyTransitionToBackFromActivity(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        applyTransitionToBackFromActivity(this);
    }

    @Override
    public void setHeaderVisibility(boolean flag) {
        activityRvpQcListBinding.qcHeader.setVisibility(flag ? View.VISIBLE : View.GONE);
        if (flag) {
            try {
                activityRvpQcListBinding.popupElement.setVisibility(View.GONE);
                activityRvpQcListBinding.frameLayoutQc.setVisibility(View.VISIBLE);
                activityRvpQcListBinding.itemNameCV.setVisibility(View.VISIBLE);
                activityRvpQcListBinding.cnclbtn.setVisibility(View.VISIBLE);
                activityRvpQcListBinding.instText.setVisibility(View.GONE);

                // image banner list handle
                StringBuilder qcUrls = new StringBuilder();
                for (int i = 0; i < rvpQcListViewModel.getRvpWithQc().rvpQualityCheckList.size(); i++) {
                    String qcValue = rvpQcListViewModel.getRvpWithQc().rvpQualityCheckList.get(i).getQcValue().replaceAll("\\s+", "");
                    if (qcValue.contains("https") || qcValue.contains("http")) {
                        qcUrls.append(qcValue);
                    }
                }
                if (qcUrls.length() == 0) {
                    activityRvpQcListBinding.linearLayoutPager.setVisibility(View.GONE);
                    activityRvpQcListBinding.noBannerPlaceholder.setImageDrawable(AppCompatResources.getDrawable(this, R.mipmap.ic_launcher_foreground));
                } else {
                    setPreviewImage(qcUrls.toString());
                }
            } catch (Exception e) {
                Logger.e(RvpQcListActivity.class.getName(), e.getMessage());
                activityRvpQcListBinding.linearLayoutPager.setVisibility(View.GONE);
                activityRvpQcListBinding.noBannerPlaceholder.setImageDrawable(AppCompatResources.getDrawable(this, R.mipmap.ic_launcher_foreground));
            }
        } else {
            activityRvpQcListBinding.popupElement.setVisibility(View.VISIBLE);
            activityRvpQcListBinding.frameLayoutQc.setVisibility(View.GONE);
            activityRvpQcListBinding.itemNameCV.setVisibility(View.GONE);
            activityRvpQcListBinding.cnclbtn.setVisibility(View.GONE);
            activityRvpQcListBinding.instText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showError(String e) {
        showSnackbar(e);
    }


    /**
     * @param qc_string == combination of the set of images
     */
    public void setPreviewImage(String qc_string) {
        // Banner images
        String[] split_images = qc_string.split(",");
        activityRvpQcListBinding.viewpager.setAdapter(new PreviewImageAdapterList(this, split_images, "internet"));
        activityRvpQcListBinding.indicator.setupWithViewPager(activityRvpQcListBinding.viewpager, true);
    }

    @Override
    public void setLayoutChild2Visibility(boolean isVisible) {
        activityRvpQcListBinding.layoutChild2.setVisibility(View.VISIBLE);
        activityRvpQcListBinding.txtQcList.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}