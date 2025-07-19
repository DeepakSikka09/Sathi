package in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logButtonEventInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityRvpQcDataDetailsBinding;
import in.ecomexpress.sathi.repo.local.data.rvp.RvpCommit;
import in.ecomexpress.sathi.repo.local.data.rvp.RvpCommit.QcWizard;
import in.ecomexpress.sathi.repo.local.db.model.RvpWithQC;
import in.ecomexpress.sathi.repo.remote.model.masterdata.SampleQuestion;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.base.BaseFragment;
import in.ecomexpress.sathi.ui.drs.rvp.awbscan.CaptureScanActivity;
import in.ecomexpress.sathi.ui.drs.rvp.qc_failure_list.RvpQcFailedActivity;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.qc_check.QcCheckFragment;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.qc_check_image.QcCheckImageFragment;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.qc_input.QcInputFragment;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_list.RvpQcListActivity;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.ImageHandler;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class RvpQcDataDetailsActivity extends BaseActivity<ActivityRvpQcDataDetailsBinding, RvpQcDataDetailsViewModel> implements IRvpQcDataDetailsNavigator {

    private final String TAG = RvpQcDataDetailsActivity.class.getSimpleName();
    public static RvpQcDataDetailsActivity rvpQcDataDetailsActivity;
    public ImageHandler imageHandler;
    @Inject
    RvpQcDataDetailsViewModel rvpQcDataDetailsViewModel;
    RvpQcDataPagerAdapter rvpQcDataPagerAdapter;
    Fragment fragment;
    IQcData iQcData;
    RvpWithQC rvpWithQC;
    List<SampleQuestion> sampleQuestions;
    Long awbNo, getDNo;
    long drs_id;
    String getDrsApiKey = null;
    String getDrsPstnKey = null;
    String getDrsPin = null;
    String composite_key = null;
    private ActivityRvpQcDataDetailsBinding activityRvpQcDataDetailsBinding;
    private int count = 0;
    public static int imageCaptureCount = 0;
    private final LinkedHashMap<BaseFragment, Boolean> passFragment = new LinkedHashMap<>();
    private final LinkedList<QcWizard> qcWizards = new LinkedList<>();
    ProgressDialog progress;
    ImageView imageView;
    Bitmap bitmap_server;
    String otp_verified_status = "";

    public static Intent getStartIntent(Context context) {
        return new Intent(context, RvpQcDataDetailsActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logScreenNameInGoogleAnalytics(TAG, this);
        try {
            rvpQcDataDetailsActivity = this;
            rvpQcDataDetailsViewModel.setNavigator(RvpQcDataDetailsActivity.this);
            activityRvpQcDataDetailsBinding = getViewDataBinding();
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.rvp));
            awbNo = getIntent().getLongExtra(Constants.INTENT_KEY, 0);
            getDNo = getIntent().getLongExtra(Constants.INTENT_KEY, 0);
            getDrsApiKey = Objects.requireNonNull(getIntent().getExtras()).getString(Constants.DRS_API_KEY);
            getDrsPstnKey = getIntent().getExtras().getString(Constants.DRS_PSTN_KEY);
            composite_key = getIntent().getExtras().getString(Constants.COMPOSITE_KEY, "");
            getDrsPin = getIntent().getExtras().getString(Constants.DRS_PIN);
            drs_id = Constants.TEMP_DRSID;
            rvpWithQC = getIntent().getParcelableExtra(Constants.INTENT_KEY_RVP_WITH_QC);
            sampleQuestions = getIntent().getParcelableArrayListExtra(Constants.INTENT_KEY_RVP_SAMPLE_QUESTIONS);
            rvpQcDataDetailsViewModel.setQcData(rvpWithQC, sampleQuestions);
            otp_verified_status = getIntent().getStringExtra("otp_verified_status");
            updateViewPager(rvpWithQC, sampleQuestions);
            activityRvpQcDataDetailsBinding.consigneeName.setMovementMethod(new ScrollingMovementMethod());
            setUp();
        } catch (Exception e) {
            Logger.e(RvpQcDataDetailsActivity.class.getName(), e.getMessage());
            showSnackbar(e.getMessage());
        }
    }

    private void setUp() {
        imageHandler = new ImageHandler(this) {
            @Override
            public void onBitmapReceived(Bitmap bitmap, String imageUri, ImageView imgView, String imageName, String imageCode, int pos, boolean verifyImage) {
                runOnUiThread(() -> {
                    // Blur Image Recognition Using Laplacian Variance:-
                    try {
                        if (CommonUtils.checkImageIsBlurryOrNot(RvpQcDataDetailsActivity.this, "RQC", bitmap, imageCaptureCount, rvpQcDataDetailsViewModel.getDataManager())) {
                            imageCaptureCount++;
                        } else {
                            imageView = imgView;
                            bitmap_server = bitmap;
                            fragment = rvpQcDataPagerAdapter.getItem(activityRvpQcDataDetailsBinding.qcViewPager.getCurrentItem());
                            logButtonEventInGoogleAnalytics(TAG, "CreatingRunTimeQCFragment", "Awb " + awbNo + " Fragment Name" + fragment, RvpQcDataDetailsActivity.this);
                            if (fragment instanceof QcCheckFragment) {
                                iQcData = (QcCheckFragment) rvpQcDataPagerAdapter.getRegisteredFragment(activityRvpQcDataDetailsBinding.qcViewPager.getCurrentItem());
                            } else if (fragment instanceof QcInputFragment) {
                                QcInputFragment qcInputFragment = (QcInputFragment) rvpQcDataPagerAdapter.getRegisteredFragment(activityRvpQcDataDetailsBinding.qcViewPager.getCurrentItem());
                                qcInputFragment.showMessage(QcInputFragment.class.getSimpleName());
                                iQcData = qcInputFragment;
                            } else if (fragment instanceof QcCheckImageFragment) {
                                iQcData = (QcCheckImageFragment) rvpQcDataPagerAdapter.getRegisteredFragment(activityRvpQcDataDetailsBinding.qcViewPager.getCurrentItem());
                            }
                            if (!imageUri.isEmpty()) {
                                if (isNetworkConnected()) {
                                    rvpQcDataDetailsViewModel.uploadImageServer(imageName, imageUri, imageCode, awbNo, drs_id, bitmap);
                                } else {
                                    showSnackbar(getString(R.string.no_network_error));
                                }
                            }
                        }
                    } catch (Exception e) {
                        Logger.e(RvpQcDataDetailsActivity.class.getName(), e.getMessage());
                        showSnackbar(e.getMessage());
                    }
                });
            }
        };
        activityRvpQcDataDetailsBinding.qcViewPager.setAdapter(rvpQcDataPagerAdapter);
    }


    public void uploadImage(Bitmap bitmap, String imageUri, ImageView imgView, String imageName, String imageCode) {
        // Blur Image Recognition Using Laplacian Variance:-
        try {
            if (CommonUtils.checkImageIsBlurryOrNot(RvpQcDataDetailsActivity.this, "RQC", bitmap, imageCaptureCount, rvpQcDataDetailsViewModel.getDataManager())) {
                imageCaptureCount++;
            } else {
                imageView = imgView;
                bitmap_server = bitmap;
                fragment = rvpQcDataPagerAdapter.getItem(activityRvpQcDataDetailsBinding.qcViewPager.getCurrentItem());
                if (fragment instanceof QcCheckFragment) {
                    iQcData = (QcCheckFragment) rvpQcDataPagerAdapter.getRegisteredFragment(activityRvpQcDataDetailsBinding.qcViewPager.getCurrentItem());
                } else if (fragment instanceof QcInputFragment) {
                    QcInputFragment qcInputFragment = (QcInputFragment) rvpQcDataPagerAdapter.getRegisteredFragment(activityRvpQcDataDetailsBinding.qcViewPager.getCurrentItem());
                    qcInputFragment.showMessage(QcInputFragment.class.getSimpleName());
                    iQcData = qcInputFragment;
                } else if (fragment instanceof QcCheckImageFragment) {
                    iQcData = (QcCheckImageFragment) rvpQcDataPagerAdapter.getRegisteredFragment(activityRvpQcDataDetailsBinding.qcViewPager.getCurrentItem());
                }
                if (!imageUri.isEmpty()) {
                    if (isNetworkConnected()) {
                        rvpQcDataDetailsViewModel.uploadImageServer(imageName, imageUri, imageCode, awbNo, drs_id, bitmap);
                    } else {
                        showSnackbar(getString(R.string.no_network_error));
                    }
                }
            }
        } catch (Exception e) {
            Logger.e(RvpQcDataDetailsActivity.class.getName(), e.getMessage());
            showSnackbar(e.getMessage());
        }
    }

    private void updateViewPager(RvpWithQC rvpWithQC, List<SampleQuestion> sampleQuestions) {
        rvpQcDataPagerAdapter = new RvpQcDataPagerAdapter(this.getSupportFragmentManager(), rvpWithQC, sampleQuestions);
        rvpQcDataPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public RvpQcDataDetailsViewModel getViewModel() {
        return rvpQcDataDetailsViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    public void onBackPressed() {
        try {
            if (QcCheckImageFragment.imagePopupVisible) {
                if (QcCheckImageFragment.popupWindow != null) {
                    QcCheckImageFragment.imagePopupVisible = false;
                    QcCheckImageFragment.popupWindow.dismiss();
                }
                return;
            }
        } catch (Exception e) {
            Logger.e(RvpQcDataDetailsActivity.class.getName(), e.getMessage());
            showSnackbar(e.getMessage());
        }
        try {
            BaseFragment baseFragment = null;
            for (Map.Entry<BaseFragment, Boolean> entry : passFragment.entrySet()) {
                baseFragment = entry.getKey();
            }
            if (baseFragment != null) {
                passFragment.remove(baseFragment);
                if (qcWizards.getLast() != null)
                    qcWizards.remove(qcWizards.getLast());
            }
            int pos = showFragmentPosition(false);
            if (pos >= 0) {
                activityRvpQcDataDetailsBinding.qcViewPager.setCurrentItem(pos);
                setCount();
            } else {
                if (otp_verified_status.equalsIgnoreCase("Verified") || otp_verified_status.equalsIgnoreCase("Skipped")) {
                    showError("Cannot move back!!");
                } else {
                    super.onBackPressed();
                    Intent intent = new Intent(RvpQcDataDetailsActivity.this, RvpQcListActivity.class);
                    intent.putExtra(Constants.INTENT_KEY, awbNo);
                    intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstnKey);
                    intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
                    intent.putExtra(Constants.DRS_PIN, getDrsPin);
                    intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                    startActivity(intent);
                    applyTransitionToBackFromActivity(this);
                }
            }
        } catch (Exception e) {
            Logger.e(RvpQcDataDetailsActivity.class.getName(), e.getMessage());
            super.onBackPressed();
            applyTransitionToBackFromActivity(this);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_rvp_qc_data_details;
    }

    @Override
    public void onBack() {
        try {
            BaseFragment baseFragment = null;
            for (Map.Entry<BaseFragment, Boolean> entry : passFragment.entrySet()) {
                baseFragment = entry.getKey();
            }
            if (baseFragment != null) {
                passFragment.remove(baseFragment);
                if (qcWizards.getLast() != null)
                    qcWizards.remove(qcWizards.getLast());
            }
            int pos = showFragmentPosition(false);
            if (pos >= 0) {
                activityRvpQcDataDetailsBinding.qcViewPager.setCurrentItem(pos);
                setCount();
            } else {
                if (otp_verified_status.equalsIgnoreCase("Verified") || otp_verified_status.equalsIgnoreCase("Skipped")) {
                    showError("Cannot move back!!");
                } else {
                    super.onBackPressed();
                    Intent intent = new Intent(RvpQcDataDetailsActivity.this, RvpQcListActivity.class);
                    intent.putExtra(Constants.INTENT_KEY, awbNo);
                    intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstnKey);
                    intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
                    intent.putExtra(Constants.DRS_PIN, getDrsPin);
                    intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                    startActivity(intent);
                    applyTransitionToBackFromActivity(this);
                }
            }
        } catch (Exception e) {
            Logger.e(RvpQcDataDetailsActivity.class.getName(), e.getMessage());
            super.onBackPressed();
            applyTransitionToBackFromActivity(this);
        }
    }

    @Override
    public void onNext(RvpCommit rvpCommit) {
        fragment = rvpQcDataPagerAdapter.getItem(activityRvpQcDataDetailsBinding.qcViewPager.getCurrentItem());
        try {
            if (fragment instanceof QcCheckFragment) {
                imageCaptureCount = 0;
                QcCheckFragment qcCheckFragment = (QcCheckFragment) rvpQcDataPagerAdapter.getRegisteredFragment(activityRvpQcDataDetailsBinding.qcViewPager.getCurrentItem());
                iQcData = qcCheckFragment;
                qcCheckFragment.showMessage(QcCheckFragment.class.getSimpleName());
                if (!iQcData.validateData()) {
                    return;
                }
                iQcData.getData(qcCheckFragment);
            } else if (fragment instanceof QcInputFragment) {
                imageCaptureCount = 0;
                QcInputFragment qcInputFragment = (QcInputFragment) rvpQcDataPagerAdapter.getRegisteredFragment(activityRvpQcDataDetailsBinding.qcViewPager.getCurrentItem());
                qcInputFragment.showMessage(QcInputFragment.class.getSimpleName());
                iQcData = qcInputFragment;
                if (!iQcData.validateData()) {
                    return;
                }
                iQcData.getData(qcInputFragment);
            } else if (fragment instanceof QcCheckImageFragment) {
                imageCaptureCount = 0;
                QcCheckImageFragment qcCheckImageFragment = (QcCheckImageFragment) rvpQcDataPagerAdapter.getRegisteredFragment(activityRvpQcDataDetailsBinding.qcViewPager.getCurrentItem());
                iQcData = qcCheckImageFragment;
                if (!iQcData.validateData()) {
                    return;
                }
                if (!iQcData.getPreviewImageClicked()) {
                    return;
                }
                iQcData.getData(qcCheckImageFragment);
            }
        } catch (Exception e) {
            Logger.e(RvpQcDataDetailsActivity.class.getName(), e.getMessage());
            showSnackbar(e.getMessage());
        }
        try {
            if (sampleQuestions != null && !sampleQuestions.isEmpty() && sampleQuestions.size() > count) {
                int pos = showFragmentPosition(true);
                if (sampleQuestions.size() - 1 > activityRvpQcDataDetailsBinding.qcViewPager.getCurrentItem()) {
                    activityRvpQcDataDetailsBinding.qcViewPager.setCurrentItem(pos);
                    setCount();
                } else {
                    proceedToNextActivity();
                }
            } else {
                proceedToNextActivity();
            }
        } catch (Exception e) {
            Logger.e(RvpQcDataDetailsActivity.class.getName(), e.getMessage());
            showSnackbar(e.getMessage());
        }
    }

    private void proceedToNextActivity() {
        Intent intent;
        if (Collections.frequency(new ArrayList<>(passFragment.values()), false) == 0) {
            intent = CaptureScanActivity.getStartIntent(this);
        } else {
            intent = RvpQcFailedActivity.getStartIntent(this);
        }
        try {
            intent.putParcelableArrayListExtra(getString(R.string.data), new ArrayList<>(qcWizards));
            intent.putExtra("rvp", Objects.requireNonNull(rvpQcDataDetailsViewModel.rvpWithQC.get()).drsReverseQCTypeResponse);
            intent.putExtra("awb", awbNo);
            intent.putExtra(Constants.INTENT_KEY, awbNo);
            intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
            intent.putExtra("flag", true);
        } catch (Exception e) {
            Logger.e(RvpQcDataDetailsActivity.class.getName(), e.getMessage());
            showSnackbar(e.getMessage());
        }
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    // set fail and pass numbers of qc on header
    private void setCount() {
        try {
            if (sampleQuestions != null && !sampleQuestions.isEmpty()) {
                rvpQcDataDetailsViewModel.setQualityCheckValue(activityRvpQcDataDetailsBinding.qcViewPager.getCurrentItem() + 1, sampleQuestions.size());
                rvpQcDataDetailsViewModel.setTotalPass(Collections.frequency(new ArrayList<>(passFragment.values()), true));
                rvpQcDataDetailsViewModel.setTotalFailed(Collections.frequency(new ArrayList<>(passFragment.values()), false));
            }
        } catch (Exception e) {
            Logger.e(RvpQcDataDetailsActivity.class.getName(), e.getMessage());
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void onProceed() {
    }

    @Override
    public void upDateCount() {
        setCount();
    }

    @Override
    public void showErrorMessage(boolean status) {
        try {
            if (status)
                showSnackbar(getString(R.string.http_500_msg));
            else
                showSnackbar(getString(R.string.server_down_msg));
        } catch (Exception e) {
            showSnackbar(getString(R.string.http_500_msg));
        }
    }

    @Override
    public void showError(String e) {
        try {
            showSnackbar(e);
        } catch (Exception e1) {
            showSnackbar(getString(R.string.http_500_msg));
        }
    }

    @Override
    public void onProgressFinishCall() {
        if (progress.isShowing()) {
            progress.dismiss();
        }
    }

    @Override
    public void onHandleError(String errorResponse) {
        try {
            showSnackbar(errorResponse);
        } catch (Exception e) {
            showSnackbar(getString(R.string.http_500_msg));
        }
    }

    @Override
    public void showProgress() {
        progress = new ProgressDialog(this, android.R.style.Theme_Material_Light_Dialog);
        progress.setMessage("Uploading The Image. Please Wait...");
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    public void setBitmap() {
        try {
            iQcData.validate(true);
            imageView.setImageBitmap(bitmap_server);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Image Corrupted try again..", Toast.LENGTH_LONG).show();
        }
    }

    private int showFragmentPosition(boolean isNext) {
        if (isNext) {
            if (rvpQcDataDetailsViewModel.rvpWithQC.get() != null && Objects.requireNonNull(rvpQcDataDetailsViewModel.rvpWithQC.get()).rvpQualityCheckList != null && Objects.requireNonNull(rvpQcDataDetailsViewModel.rvpWithQC.get()).rvpQualityCheckList.size() > count) {
                return ++count;
            } else {
                return -1;
            }
        } else {
            if (count > 0) {
                return --count;
            } else {
                return count = -1;
            }
        }
    }

    public void getFragmentData(boolean status, QcWizard qcWizard, BaseFragment fragment) {
        // add qc data from all segments in to qcList
        qcWizards.add(qcWizard);
        passFragment.put(fragment, status || qcWizard.getQccheckcode().startsWith("OP"));
    }
}