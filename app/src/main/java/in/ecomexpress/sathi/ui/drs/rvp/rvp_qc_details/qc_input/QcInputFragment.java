package in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.qc_input;

import static android.app.Activity.RESULT_OK;
import static in.ecomexpress.sathi.ui.drs.forward.details.ForwardDetailViewModel.TAG;
import static in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.RvpQcDataDetailsActivity.rvpQcDataDetailsActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.barcodelistner.BarcodeHandler;
import in.ecomexpress.barcodelistner.BarcodeResult;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.FragmentQcInputBinding;
import in.ecomexpress.sathi.repo.local.data.rvp.RvpCommit;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.RvpQualityCheck;
import in.ecomexpress.sathi.repo.remote.model.masterdata.SampleQuestion;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.base.BaseFragment;
import in.ecomexpress.sathi.ui.drs.rvp.awbscan.RQCScannerActivity;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.IQcData;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.RvpQcDataDetailsActivity;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.cameraView.CameraActivity;

@AndroidEntryPoint
public class QcInputFragment extends BaseFragment<FragmentQcInputBinding, QcInputViewModel> implements IQcInputNavigation, IQcData, BarcodeResult {
    FragmentQcInputBinding fragmentQcInputBinding;
    public String device = (Build.MANUFACTURER + ":" + Build.MODEL).toUpperCase(Locale.US);
    private final int REQUEST_CODE_SCAN = 1101;
    BarcodeHandler barcodeHandler;
    @Inject
    QcInputViewModel mQcInputViewModel;
    RvpCommit.QcWizard qcWizard = new RvpCommit.QcWizard();
    SampleQuestion sampleQuestion;
    RvpQualityCheck rvpQualityCheck;
    private boolean isImageCapture = false;
    private static final int CAMERA_REQUEST_CODE = 100;

    public static QcInputFragment newInstance() {
        return new QcInputFragment();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN) {
            handleScanResult(data);
            barcodeHandler.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                if (data != null) {
                    String imagePathWithWaterMark = data.getStringExtra("imagePathWithWaterMark");
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePathWithWaterMark);
                    String imageUri = data.getStringExtra("imageUri");
                    String imageCode = data.getStringExtra("imageCode");
                    String imageName = data.getStringExtra("imageName");
                    ((RvpQcDataDetailsActivity) requireActivity()).uploadImage(bitmap, imageUri, fragmentQcInputBinding.imageViewCaptureImageQcInput, imageName, imageCode);
                } else {
                    ((RvpQcDataDetailsActivity) requireActivity()).showSnackbar("Captured Image Data Is Empty");
                }
            } catch (Exception e) {
                ((RvpQcDataDetailsActivity) requireActivity()).showSnackbar("Captured Image Data Is Empty");
            }
        }
    }

    @Override
    public void onResult(String s) {
        try {
            if (s != null) {
                String scannedData = s;
                if (sampleQuestion.getVerificationMode().contains("TAIL")) {
                    String[] segments = sampleQuestion.getVerificationMode().split("_");
                    String trim_digit = segments[segments.length - 1];
                    scannedData = scannedData.substring(scannedData.length() - Integer.parseInt(trim_digit));
                    fragmentQcInputBinding.etValue.setText(scannedData);
                    fragmentQcInputBinding.phonePeAppCompatTextView.setText(scannedData);
                } else if (sampleQuestion.getVerificationMode().contains("HEAD")) {
                    String[] segments = sampleQuestion.getVerificationMode().split("_");
                    String trim_digit = segments[segments.length - 1];
                    scannedData = scannedData.substring(0, Integer.parseInt(trim_digit));
                    fragmentQcInputBinding.etValue.setText(scannedData);
                    fragmentQcInputBinding.phonePeAppCompatTextView.setText(scannedData);
                } else {
                    fragmentQcInputBinding.etValue.setText(scannedData);
                    fragmentQcInputBinding.phonePeAppCompatTextView.setText(scannedData);
                }
                fragmentQcInputBinding.etValue.setSelection(scannedData.length());
                fragmentQcInputBinding.phonePeAppCompatTextView.setSelection(scannedData.length());
            } else {
                Toast.makeText(getBaseActivity(), "Cropped bar code scanned", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Logger.e(QcInputFragment.class.getName(), e.getMessage());
        }
    }

    private void handleScanResult(Intent data) {
        try {
            if (data != null) {
                String scannedData = data.getStringExtra(RQCScannerActivity.SCANNED_CODE);
                if (sampleQuestion.getVerificationMode().contains("TAIL")) {
                    String[] segments = sampleQuestion.getVerificationMode().split("_");
                    String trim_digit = segments[segments.length - 1];
                    scannedData = Objects.requireNonNull(scannedData).substring(scannedData.length() - Integer.parseInt(trim_digit));
                    fragmentQcInputBinding.etValue.setText(scannedData);
                    fragmentQcInputBinding.phonePeAppCompatTextView.setText(scannedData);
                } else if (sampleQuestion.getVerificationMode().contains("HEAD")) {
                    String[] segments = sampleQuestion.getVerificationMode().split("_");
                    String trim_digit = segments[segments.length - 1];
                    scannedData = Objects.requireNonNull(scannedData).substring(0, Integer.parseInt(trim_digit));
                    fragmentQcInputBinding.etValue.setText(scannedData);
                    fragmentQcInputBinding.phonePeAppCompatTextView.setText(scannedData);
                } else {
                    fragmentQcInputBinding.etValue.setText(scannedData);
                    fragmentQcInputBinding.phonePeAppCompatTextView.setText(scannedData);
                }
                fragmentQcInputBinding.etValue.setSelection(Objects.requireNonNull(scannedData).length());
                fragmentQcInputBinding.phonePeAppCompatTextView.setSelection(Objects.requireNonNull(scannedData).length());
            } else {
                Toast.makeText(getBaseActivity(), "Cropped bar code scanned", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Logger.e(QcInputFragment.class.getName(), e.getMessage());
        }
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_qc_input;
    }

    @Override
    public QcInputViewModel getViewModel() {
        return mQcInputViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQcInputViewModel.setNavigator(this);
        barcodeHandler = new BarcodeHandler(getActivity(), "ScannerLM", this);
        barcodeHandler.enableScanner();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentQcInputBinding = getViewDataBinding();
        fragmentQcInputBinding.etValue.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
        fragmentQcInputBinding.phonePeAppCompatTextView.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
        fragmentQcInputBinding.remarks.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
        fragmentQcInputBinding.remarks.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
        if (getArguments() != null) {
            this.sampleQuestion = getArguments().getParcelable(Constants.QC_SAMPLE_QC);
            this.rvpQualityCheck = getArguments().getParcelable(Constants.QC_CHECK_LIST);
            if ((sampleQuestion.getImageCaptureSettings().equalsIgnoreCase("O") || sampleQuestion.getImageCaptureSettings().equalsIgnoreCase("N")) && mQcInputViewModel.getDataManager().getHideCamera()) {
                fragmentQcInputBinding.imagelayout.setVisibility(View.GONE);
            }
            try {
                mQcInputViewModel.setQCData(rvpQualityCheck, sampleQuestion);
                //for phonePe shipment hide manual entry for barcode
                mQcInputViewModel.getPhonePeShipmentType(String.valueOf(rvpQualityCheck.getAwbNo()));

            } catch (Exception e) {
                Logger.e(QcInputFragment.class.getName(), e.getMessage());
                showError(e.getMessage());
            }
        }
    }

    public void showMessage(String s) {
        Log.d(TAG, "showMessage: " + s);
    }

    @Override
    public void getData(BaseFragment fragment) {
        try {
            boolean qcStatus = false;
            qcWizard.setQcName(mQcInputViewModel.getName().get());
            qcWizard.setQccheckcode(Objects.requireNonNull(mQcInputViewModel.sampleQuestions.get()).getCode());
            qcWizard.setRemarks(mQcInputViewModel.inputDataRemark.get());
            qcWizard.setMatch("0");
            if (mQcInputViewModel.getValue().equalsIgnoreCase(Objects.requireNonNull(mQcInputViewModel.inputData.get()).trim())) {
                qcStatus = true;
                qcWizard.setMatch("1");
            }
            qcWizard.setQcValue(Objects.requireNonNull(mQcInputViewModel.inputData.get()).trim());
            rvpQcDataDetailsActivity.getFragmentData(qcStatus, qcWizard, fragment);
        } catch (Exception e) {
            Logger.e(QcInputFragment.class.getName(), e.getMessage());
            showError(e.getMessage());
        }
    }

    public boolean validateData() {
        try {
            if (mQcInputViewModel.inputData.get() == null || Objects.requireNonNull(mQcInputViewModel.inputData.get()).trim().isEmpty()) {
                getBaseActivity().showSnackbar("Please enter value");
                return false;
            }
            if (mQcInputViewModel.sampleQuestions.get() != null &&
                    Objects.requireNonNull(mQcInputViewModel.sampleQuestions.get()).getImageCaptureSettings().equalsIgnoreCase("M")) {
                if (isImageCapture) {
                    return true;
                } else {
                    getBaseActivity().showSnackbar("Please Capture Image");
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            Logger.e(QcInputFragment.class.getName(), e.getMessage());
            showError(e.getMessage());
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
    public void captureImage() {
        startCameraActivity("QC_" + Objects.requireNonNull(mQcInputViewModel.sampleQuestions.get()).getCode(), Objects.requireNonNull(mQcInputViewModel.rvpQualityCheck.get()).getAwbNo() + "_" + Constants.TEMP_DRSID + "_" + "QC_" + Objects.requireNonNull(mQcInputViewModel.sampleQuestions.get()).getCode() + ".png");
    }

    private void startCameraActivity(String ImageCode, String imageName) {
        try {
            Intent intent = new Intent(requireActivity(), CameraActivity.class);
            intent.putExtra("EmpCode", mQcInputViewModel.getDataManager().getEmp_code());
            intent.putExtra("Latitude", mQcInputViewModel.getDataManager().getCurrentLatitude());
            intent.putExtra("Longitude", mQcInputViewModel.getDataManager().getCurrentLongitude());
            intent.putExtra("ImageCode", ImageCode);
            intent.putExtra("imageName", imageName);
            intent.putExtra("awbNumber", "" + mQcInputViewModel.rvpQualityCheck.get().getAwbNo());
            intent.putExtra("drs_id", String.valueOf(Constants.TEMP_DRSID));
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showError(String e) {
        getBaseActivity().showSnackbar(e);
    }

    @Override
    public void onScanClick() {
        Intent intent = new Intent(getActivity(), RQCScannerActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    @Override
    public void isPhonePayEnabled(String string) {
        // For PhonePe shipment hide manual entry for barcode.
        if (Objects.requireNonNull(string).equalsIgnoreCase("true")) {
            fragmentQcInputBinding.qrCodeScanLinearLayout.setVisibility(View.GONE);
            fragmentQcInputBinding.qrCodeScanPhonePeLinearLayout.setVisibility(View.VISIBLE);
        }
    }
}