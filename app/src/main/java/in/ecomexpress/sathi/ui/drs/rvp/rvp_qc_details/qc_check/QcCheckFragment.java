package in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.qc_check;


import static android.app.Activity.RESULT_OK;
import static in.ecomexpress.sathi.ui.drs.forward.details.ForwardDetailViewModel.TAG;
import static in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.RvpQcDataDetailsActivity.rvpQcDataDetailsActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.FragmentQcCheckBinding;
import in.ecomexpress.sathi.repo.local.data.rvp.RvpCommit;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.RvpQualityCheck;
import in.ecomexpress.sathi.repo.remote.model.masterdata.SampleQuestion;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.base.BaseFragment;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.IQcData;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.RvpQcDataDetailsActivity;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.cameraView.CameraActivity;

@AndroidEntryPoint
public class QcCheckFragment extends BaseFragment<FragmentQcCheckBinding, QcCheckViewModel> implements IQcCheckNavigation, IQcData {
    FragmentQcCheckBinding fragmentQcCheckBinding;
    @Inject
    QcCheckViewModel mQcCheckViewModel;
    RvpCommit.QcWizard qcWizard = new RvpCommit.QcWizard();
    SampleQuestion sampleQuestion;
    RvpQualityCheck rvpQualityCheck;
    private boolean isImageCapture = false;
    private static final int CAMERA_REQUEST_CODE = 100;

    public static QcCheckFragment newInstance() {
        return new QcCheckFragment();
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_qc_check;
    }

    @Override
    public QcCheckViewModel getViewModel() {
        return mQcCheckViewModel;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void inItListener() {
        try {
            fragmentQcCheckBinding.rgQualityCheckFragment.setOnCheckedChangeListener((radioGroup, i) -> {
                int radioButtonID = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = radioGroup.findViewById(radioButtonID);
                String checkValue = (String) radioButton.getText();
                mQcCheckViewModel.setCheckStatus(checkValue);
            });

        } catch (Exception e) {
            Logger.e(QcCheckFragment.class.getName(), e.getMessage());
            getBaseActivity().showSnackbar(e.getMessage());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            mQcCheckViewModel.setNavigator(this);
            fragmentQcCheckBinding = getViewDataBinding();
            if (getArguments() != null) {
                this.sampleQuestion = getArguments().getParcelable(Constants.QC_SAMPLE_QC);
                this.rvpQualityCheck = getArguments().getParcelable(Constants.QC_CHECK_LIST);
                mQcCheckViewModel.setQCData(rvpQualityCheck, sampleQuestion);
            }
            if ((sampleQuestion.getImageCaptureSettings().equalsIgnoreCase("O") || sampleQuestion.getImageCaptureSettings().equalsIgnoreCase("N")) && mQcCheckViewModel.getDataManager().getHideCamera()) {
                fragmentQcCheckBinding.imagelayout.setVisibility(View.GONE);
            }
            try {
                fragmentQcCheckBinding.remarks.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
                fragmentQcCheckBinding.remarks.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
            } catch (Exception e) {
                Logger.e(QcCheckFragment.class.getName(), e.getMessage());
            }
            inItListener();
        } catch (Exception e) {
            Logger.e(QcCheckFragment.class.getName(), e.getMessage());
            getBaseActivity().showSnackbar(e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void showMessage(String s) {
        Log.d(TAG, "showMessage: " + s);
    }

    //  getData function for send data from this fragment to activity for collect
    @Override
    public void getData(BaseFragment fragment) {
        try {
            boolean qcStatus = false;
            qcWizard.setQcName(mQcCheckViewModel.getName().get());
            qcWizard.setQcValue("NONE");
            qcWizard.setQccheckcode(mQcCheckViewModel.getQcName().get());
            qcWizard.setRemarks(mQcCheckViewModel.remark.get());
            qcWizard.setMatch("0");
            if (Objects.requireNonNull(mQcCheckViewModel.inputData.get()).trim().equalsIgnoreCase("Yes")) {
                qcStatus = true;
                qcWizard.setMatch("1");
            }
            qcWizard.setQccheckcode(Objects.requireNonNull(mQcCheckViewModel.sampleQuestions.get()).getCode());
            rvpQcDataDetailsActivity.getFragmentData(qcStatus, qcWizard, fragment);
        } catch (Exception e) {
            Logger.e(QcCheckFragment.class.getName(), e.getMessage());
            getBaseActivity().showSnackbar(e.getMessage());
        }
    }

    public boolean validateData() {
        try {
            if (mQcCheckViewModel.inputData.get() != null && !Objects.requireNonNull(mQcCheckViewModel.inputData.get()).trim().isEmpty()) {
                if (mQcCheckViewModel.sampleQuestions.get() != null && Objects.requireNonNull(mQcCheckViewModel.sampleQuestions.get()).getImageCaptureSettings().equalsIgnoreCase("M")) {
                    if (isImageCapture) {
                        return true;
                    } else {
                        getBaseActivity().showSnackbar("Please Capture Image");
                        return false;
                    }
                }
                return true;
            } else {
                getBaseActivity().showSnackbar("Please select Yes or No");
                return false;
            }
        } catch (Exception ex) {
            Logger.e(QcCheckFragment.class.getName(), ex.getMessage());
            getBaseActivity().showSnackbar(ex.getMessage());
        }
        return false;
    }

    @Override
    public boolean getPreviewImageClicked() {
        return false;
    }

    @Override
    public void validate(boolean flag) {
        isImageCapture = flag;
    }

    @Override
    public void captureImage(String sync_status) {
        startCameraActivity("QC_" + Objects.requireNonNull(mQcCheckViewModel.sampleQuestions.get()).getCode(), Objects.requireNonNull(mQcCheckViewModel.rvpQualityCheck.get()).getAwbNo() + "_" + Constants.TEMP_DRSID + "_" + "QC_" + Objects.requireNonNull(mQcCheckViewModel.sampleQuestions.get()).getCode() + ".png");
    }

    private void startCameraActivity(String ImageCode, String imageName) {
        try {
            Intent intent = new Intent(requireActivity(), CameraActivity.class);
            intent.putExtra("EmpCode", mQcCheckViewModel.getDataManager().getEmp_code());
            intent.putExtra("Latitude", mQcCheckViewModel.getDataManager().getCurrentLatitude());
            intent.putExtra("Longitude", mQcCheckViewModel.getDataManager().getCurrentLongitude());
            intent.putExtra("ImageCode", ImageCode);
            intent.putExtra("imageName", imageName);
            intent.putExtra("awbNumber", "" + mQcCheckViewModel.rvpQualityCheck.get().getAwbNo());
            intent.putExtra("drs_id", String.valueOf(Constants.TEMP_DRSID));
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                if (data != null) {
                    String imagePathWithWaterMark = data.getStringExtra("imagePathWithWaterMark");
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePathWithWaterMark);
                    String imageUri = data.getStringExtra("imageUri");
                    String imageCode = data.getStringExtra("imageCode");
                    String imageName = data.getStringExtra("imageName");

                    ((RvpQcDataDetailsActivity) requireActivity()).uploadImage(bitmap, imageUri, fragmentQcCheckBinding.imageViewCaptureImageCheck, imageName, imageCode);


                } else {
                    ((RvpQcDataDetailsActivity) requireActivity()).showSnackbar("Captured Image Data Is Empty");
                }
            } catch (Exception e) {
                ((RvpQcDataDetailsActivity) requireActivity()).showSnackbar("Captured Image Data Is Empty");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void showError(String e) {
        getBaseActivity().showSnackbar(e);
    }
}
