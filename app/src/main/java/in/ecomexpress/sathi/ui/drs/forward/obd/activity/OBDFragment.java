package in.ecomexpress.sathi.ui.drs.forward.obd.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.FragmentOBDBinding;
import in.ecomexpress.sathi.ui.base.BaseFragment;
import in.ecomexpress.sathi.ui.drs.forward.obd.navigator.IFwdObdQcNavigator;
import in.ecomexpress.sathi.ui.drs.forward.obd.viewmodel.FwdOBDcCPassViewModel;

@AndroidEntryPoint
public class OBDFragment extends BaseFragment<FragmentOBDBinding, FwdOBDcCPassViewModel> implements IFwdObdQcNavigator, View.OnClickListener {

    FwdOBDcCPassViewModel fwdOBDcCPassViewModel;
    private FragmentOBDBinding obdBinding;
    private final String qcItemName;
    private final FwdOBDQcPassActivity fwdOBDQcPassActivity;
    private final int qcItemCheckPlace;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fwdOBDcCPassViewModel.setNavigator(this);
        obdBinding = getViewDataBinding();
        obdBinding.radioOBDPass.setOnClickListener(this);
        obdBinding.radioOBDFail.setOnClickListener(this);
        obdBinding.imageCamera.setOnClickListener(this);
        obdBinding.imageDelete.setOnClickListener(this);
        obdBinding.textViewQuestion.setText(qcItemName);
    }

    public OBDFragment(String qcItemName, int qcItemCheckPlace, FwdOBDQcPassActivity qcActivity, FwdOBDcCPassViewModel fwdOBDcCPassViewModel) {
        this.qcItemName = qcItemName;
        this.qcItemCheckPlace = qcItemCheckPlace;
        this.fwdOBDQcPassActivity = qcActivity;
        this.fwdOBDcCPassViewModel = fwdOBDcCPassViewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fwdOBDQcPassActivity.setQcNextButtonDisable();
        fwdOBDQcPassActivity.hideButtonForNextFragmentOrActivity();
    }

    public void setDataAfterImageCaptured(Bitmap qcCapturedImageBitmap){
        if (qcCapturedImageBitmap != null) {
            obdBinding.imageViewStart.setVisibility(View.VISIBLE);
            obdBinding.imageViewStart.setImageBitmap(qcCapturedImageBitmap);
            obdBinding.imageCamera.setVisibility(View.GONE);
            obdBinding.imageDelete.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public FwdOBDcCPassViewModel getViewModel() {
        return fwdOBDcCPassViewModel;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.radioOBDFail:
                obdBinding.imageCaptureCard.setVisibility(View.VISIBLE);
                obdBinding.radioOBDFail.setBackgroundTintList(fwdOBDQcPassActivity.listColor);
                obdBinding.radioOBDPass.setBackgroundTintList(fwdOBDQcPassActivity.listWhite);
                FwdOBDQcPassActivity.qualityCheckStatus = false;
                manageQcNextButton();
                if(fwdOBDcCPassViewModel.imageCaptureSettings.get(qcItemCheckPlace).equalsIgnoreCase("O")){
                    fwdOBDQcPassActivity.setQcNextButtonEnable();
                }
                break;

            case R.id.radioOBDPass:
                obdBinding.imageCaptureCard.setVisibility(View.VISIBLE);
                obdBinding.radioOBDFail.setBackgroundTintList(fwdOBDQcPassActivity.listWhite);
                obdBinding.radioOBDPass.setBackgroundTintList(fwdOBDQcPassActivity.listColor);
                FwdOBDQcPassActivity.qualityCheckStatus = true;
                fwdOBDQcPassActivity.showButtonNextMandatoryQC();
                if(fwdOBDcCPassViewModel.imageCaptureSettings.get(qcItemCheckPlace).equalsIgnoreCase("O")){
                    fwdOBDQcPassActivity.setQcNextButtonEnable();
                }
                break;

            case R.id.imageCamera:
                openCamera();
                break;

            case R.id.imageDelete:
                showError("Image Deleted");
                setDataAfterImageDelete();
                break;
        }
    }

    private void setDataAfterImageDelete() {
        obdBinding.imageDelete.setVisibility(View.GONE);
        obdBinding.imageCamera.setVisibility(View.VISIBLE);
        obdBinding.imageViewStart.setVisibility(View.GONE);
        if(fwdOBDcCPassViewModel.imageCaptureSettings.get(qcItemCheckPlace).equalsIgnoreCase("O")) {
            fwdOBDQcPassActivity.setQcNextButtonEnable();
        } else{
            fwdOBDQcPassActivity.setQcNextButtonDisable();
        }
    }

    private void openCamera() {
        if (isNetworkConnected()) {
            fwdOBDQcPassActivity.imageHandler.captureImage(fwdOBDQcPassActivity.awbNumber + "_" + fwdOBDQcPassActivity.drsIdNum + "_" + "OBD_QC_" + fwdOBDcCPassViewModel.qcCode.get(qcItemCheckPlace) + ".png", obdBinding.imageCamera, fwdOBDcCPassViewModel.qcCode.get(qcItemCheckPlace));
        } else {
            showError("Check Your Internet Connection");
        }
    }

    private void manageQcNextButton() {
        try {
            if (fwdOBDcCPassViewModel.isOptional == null || fwdOBDcCPassViewModel.isOptional.isEmpty()) {
                fwdOBDQcPassActivity.showButtonNextMandatoryQC();
                return;
            }

            String isOptionalValue = fwdOBDcCPassViewModel.isOptional.get(qcItemCheckPlace);

            if (isOptionalValue == null) {
                fwdOBDQcPassActivity.showButtonNextMandatoryQC();
            } else if (isOptionalValue.equalsIgnoreCase("Y")) {
                fwdOBDQcPassActivity.showButtonNextOptionalQC();
            } else {
                fwdOBDQcPassActivity.showButtonNextMandatoryQC();
            }
        } catch (Exception e) {
            showError("Error : isOptional"+ e.getMessage());
        }
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_o_b_d;
    }

    @Override
    public void showError(String error) {
        fwdOBDQcPassActivity.showError(error);
    }

    @Override
    public void setCapturedImageBitmap(Bitmap imageBitmap) {
        setDataAfterImageCaptured(imageBitmap);
        fwdOBDQcPassActivity.setQcNextButtonEnable();
    }
}