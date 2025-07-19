package in.ecomexpress.sathi.ui.dummy.eds.eds_document_verification;


import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.FragmentDocVerificationBinding;
import in.ecomexpress.sathi.repo.local.data.eds.EDSActivityResponseWizard;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.ImageSetting;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.base.BaseFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.ActivityData;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.DocumentData;
import in.ecomexpress.sathi.utils.Constants;

import static in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.EDSDetailActivity.edsDetailActivity;
import static in.ecomexpress.sathi.ui.drs.forward.details.ForwardDetailViewModel.TAG;

/**
 * Created by dhananjayk on 07-11-2018.
 */
@AndroidEntryPoint
public class DocumentVerificationFragment extends BaseFragment<FragmentDocVerificationBinding, DocumentVerificationViewModel> implements IDocumentVerificationNavigation, ActivityData ,DocumentData{

    FragmentDocVerificationBinding fragmentDocVerificationBinding;

    @Inject
    DocumentVerificationViewModel documentVerificationViewModel;
    boolean sucess = false;

    MasterActivityData masterActivityData;
    EDSActivityWizard edsActivityWizard;
    EdsWithActivityList edsWithActivityList;
    private boolean isImageCapture = false;
    EDSActivityResponseWizard edsActivityResponseWizard = new EDSActivityResponseWizard();
    private List<Boolean> max;
    private List<Boolean> min;
    private List<Boolean> imageValidation;
    int position;
    String image_status;

    @Inject
    DocumentVerificationAdapter documentVerificationAdapter;

    public static DocumentVerificationFragment newInstance() {

        DocumentVerificationFragment fragment = new DocumentVerificationFragment();
        return fragment;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_doc_verification;
    }

    @Override
    public DocumentVerificationViewModel getViewModel() {
        return documentVerificationViewModel;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        documentVerificationViewModel.setNavigator(this);
        Log.d(TAG, "onCreate: " + this.toString());
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentDocVerificationBinding = getViewDataBinding();
        //fragmentDocVerificationBinding.etxtEnteredValue.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
        fragmentDocVerificationBinding.etRemarks.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
        fragmentDocVerificationBinding.etRemarks.setFilters(new InputFilter[] { new InputFilter.LengthFilter(50) });
        if (getArguments() != null) {
            this.masterActivityData = getArguments().getParcelable(Constants.EDS_MASTER_LIST);
            this.edsActivityWizard = getArguments().getParcelable(Constants.EDS_ACTIVITY_LIST);
            this.edsWithActivityList = getArguments().getParcelable(Constants.EDS_DATA);
            documentVerificationViewModel.setData(edsActivityWizard, masterActivityData);
            fragmentDocVerificationBinding.etxtEnteredValue.requestFocus();
            try {
                if (!masterActivityData.getInstructions().isEmpty()) {
                    fragmentDocVerificationBinding.txtActivityInstruction.setClickable(true);
                    fragmentDocVerificationBinding.txtActivityInstruction.setMovementMethod(LinkMovementMethod.getInstance());
                    setTextViewHTML(fragmentDocVerificationBinding.txtActivityInstruction, masterActivityData.getInstructions());
                }
                if (!edsActivityWizard.getCustomerRemarks().isEmpty()) {
                    fragmentDocVerificationBinding.txtActivityRemark.setText(edsActivityWizard.getCustomerRemarks());
                }
            } catch (Exception e) {
                getBaseActivity().showSnackbar(e.getMessage());
                e.printStackTrace();
            }

        }
        setUp();

    }


    @Override
    public void onResume() {
        super.onResume();
        if (edsActivityWizard.wizardFlag.isIs_adhar_masking_required()) {
            edsDetailActivity.setSpinnerCodeValue("AADHAR");
            fragmentDocVerificationBinding.recyclerView.setVisibility(View.GONE);
            fragmentDocVerificationBinding.hdfcMasking.hdfcMasking.setVisibility(View.VISIBLE);
        } else {
            fragmentDocVerificationBinding.recyclerView.setVisibility(View.VISIBLE);
            fragmentDocVerificationBinding.hdfcMasking.hdfcMasking.setVisibility(View.GONE);
        }


    }

    public void showMessage(String s) {
        Log.d(TAG, "showMessage: " + s);
    }

    private void setUp() {
        try {
            max = new ArrayList<>();
            min = new ArrayList<>();
            imageValidation = new ArrayList<>();

            for (int i = 0; i < masterActivityData.getImageSettings().getMax(); i++) {
                max.add(true);
                imageValidation.add(false);
            }
            for (int j = 0; j < masterActivityData.getImageSettings().getMin(); j++) {
                min.add(true);

            }
            fragmentDocVerificationBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            fragmentDocVerificationBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
            fragmentDocVerificationBinding.recyclerView.setAdapter(documentVerificationAdapter);
            documentVerificationAdapter.setData(min, max, documentVerificationViewModel);
        } catch (Exception e) {
            getBaseActivity().showSnackbar(e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void setImageValidation()
    {
        if(image_status.equalsIgnoreCase("M"))
        {
            imageValidation.set(position, true);
        }

    }

    @Override
    public void captureImage(ImageView img, int pos) {

        if (isNetworkConnected()) {
            try {
                position = pos;
                image_status = documentVerificationViewModel.getEdsImageStatuses().get(pos).getImage_status();
                ImageSetting imageSetting = new ImageSetting();
                imageSetting.setWaterMark(documentVerificationViewModel.setWaterMarkDetail(masterActivityData.getImageSettings().getWaterMark()));
                imageSetting.setMax(max.size());
                imageSetting.setMin(min.size());
                imageSetting.setcapture_scanned_image(masterActivityData.getImageSettings().capture_scanned_image);
                imageSetting.setVerify_image_on_server(masterActivityData.getImageSettings().verify_image_on_server);

                masterActivityData.setImageSettings(imageSetting);


                Log.d("DV waterMark", "" + masterActivityData.imageSettings.getWaterMark());
                //  edsDetailActivity.imageHandler.captureImage(documentVerificationViewModel.edsActivityWizard.get().getAwbNo() + "_" + edsWithActivityList.edsResponse.getDrsNo() + "_" + "EDS_" + documentVerificationViewModel.masterActivityData.get().getCode() + "_" + pos + ".png", img, "EDS_" + documentVerificationViewModel.masterActivityData.get().getCode() + "_" + pos, masterActivityData.imageSettings.getWaterMark());
                if (!masterActivityData.imageSettings.iscapture_scanned_image()) {
                    edsDetailActivity.imageHandler.captureImage(documentVerificationViewModel.edsActivityWizard.get().getAwbNo()
                            + "_" + edsWithActivityList.edsResponse.getDrsNo()
                            + "_" + "EDS_" + documentVerificationViewModel.masterActivityData.get().getCode()
                            + "_" + pos + ".png", img, "EDS_" + documentVerificationViewModel.masterActivityData.get().getCode()
                            + "_" + pos, masterActivityData.imageSettings.getWaterMark());
                } else {
                    edsDetailActivity.digitalCropImageHandler.DigitalCropImage(documentVerificationViewModel.edsActivityWizard.get().getAwbNo()
                            + "_" + edsWithActivityList.edsResponse.getDrsNo()
                            + "_" + "EDS_" + documentVerificationViewModel.masterActivityData.get().getCode()
                            + "_" + pos + ".png", img, "EDS_" + documentVerificationViewModel.masterActivityData.get().getCode()
                            + "_" + pos, masterActivityData.getImageSettings().getWaterMark());
                }
            } catch (Exception e) {
                getBaseActivity().showSnackbar(e.getMessage());
                e.printStackTrace();
            }
        } else {
            getBaseActivity().showSnackbar("Please check your internet connection.");
        }

    }

    @Override
    public void showError(String e) {
        getBaseActivity().showSnackbar(e);
    }

    @Override
    public void onVerify(String starredString) {
        try {
            if (fragmentDocVerificationBinding.etxtEnteredValue.getText().toString().trim().isEmpty()) {
                fragmentDocVerificationBinding.rlVerfictionResult.setBackgroundColor(getResources().getColor(R.color.red_dark2));
                // fragmentDocVerificationBinding.etxtEnteredValue.setHint(getString(R.string.verification_code));
                fragmentDocVerificationBinding.btnVerify.setTextColor(getResources().getColor(R.color.black));
                fragmentDocVerificationBinding.txtVerificationStatus.setTextColor(getResources().getColor(R.color.white));
                getBaseActivity().showSnackbar(getString(R.string.verification_code));
                sucess = false;
            } else if (!fragmentDocVerificationBinding.etxtEnteredValue.getText().toString().isEmpty() && starredString.equals(fragmentDocVerificationBinding.etxtEnteredValue.getText().toString())) {
                fragmentDocVerificationBinding.rlVerfictionResult.setBackgroundColor(getResources().getColor(R.color.green));
                fragmentDocVerificationBinding.txtVerificationStatus.setText("Verified");
                fragmentDocVerificationBinding.etxtEnteredValue.setEnabled(false);
                fragmentDocVerificationBinding.etxtEnteredValue.setFocusable(false);
                fragmentDocVerificationBinding.txtVerificationStatus.setTextColor(getResources().getColor(R.color.white));
                fragmentDocVerificationBinding.btnVerify.setTextColor(getResources().getColor(R.color.green));
                sucess = true;

            } else {
                fragmentDocVerificationBinding.rlVerfictionResult.setBackgroundColor(getResources().getColor(R.color.red_dark2));
                fragmentDocVerificationBinding.txtVerificationStatus.setText(getString(R.string.notverified));
                fragmentDocVerificationBinding.btnVerify.setTextColor(getResources().getColor(R.color.black));
                fragmentDocVerificationBinding.txtVerificationStatus.setTextColor(getResources().getColor(R.color.white));
                getBaseActivity().showSnackbar(getString(R.string.notverified));
                sucess = false;
            }
            getBaseActivity().hideKeyboard(getActivity());
        } catch (Exception e) {
            getBaseActivity().showSnackbar(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void getData(BaseFragment fragment) {
        boolean activityStatus = false;
        try {
            edsActivityResponseWizard.setCode(edsActivityWizard.getCode());
            edsActivityResponseWizard.setInput_value("false");
            edsActivityResponseWizard.setInputRemark("");
            edsActivityResponseWizard.setIsDone("false");
            edsActivityResponseWizard.setActivityId("0");
            if (sucess) {
                activityStatus = true;
                edsActivityResponseWizard.setInput_value("true");
                edsActivityResponseWizard.setInputRemark(fragmentDocVerificationBinding.etRemarks.getText().toString());
                edsActivityResponseWizard.setIsDone("true");
                edsActivityResponseWizard.setActivityId(edsActivityWizard.getActivityId());
                edsActivityResponseWizard.setAdditionalInfos(new ArrayList<>());

            }
            edsDetailActivity.getFragmentData(activityStatus, edsActivityResponseWizard, fragment);
        } catch (Exception e) {
            getBaseActivity().showSnackbar(e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public boolean validateData() {
        try {
            if (!fragmentDocVerificationBinding.etxtEnteredValue.getText().toString().isEmpty() && sucess) {
                if (edsActivityWizard.wizardFlag.isIs_adhar_masking_required()) {
                    if (!documentVerificationViewModel.getDataManager().getAadharStatus()) {
                        showError("Aadhar Images are not uploaded or Aadhar status is pending.");
                        return false;
                    }
                }
                else
                if (documentVerificationViewModel.masterActivityData.get() != null) {
                    int imagecount = 0;
                    if (min.size() > 0) {
                        for (Boolean value : imageValidation) {
                            if (value)
                                imagecount++;
                        }
                        if (imagecount >= min.size()) {
                            return true;
                        } else {
                            getBaseActivity().showSnackbar("Please Capture Image");

                            return false;
                        }
                    } else {
                        return true;
                    }
                }
                return true;
            } else {
                getBaseActivity().showSnackbar("Please Enter Correct Detail..");
                return false;
            }
        } catch (Exception ex) {
            getBaseActivity().showSnackbar(ex.getMessage());
            ex.printStackTrace();
        }
        return false;

    }

    @Override
    public void validate(boolean flag) {
        isImageCapture = flag;
    }

    @Override
    public boolean validateCancelData() {
        return true;
    }

    @Override
    public EDSActivityWizard getActivityWizard() {
        return edsActivityWizard;
    }
/*

    @Override
    public void getData(BaseFragment fragment) {
        boolean qcStatus = false;

        qcWizard.setQcValue("NONE");
        qcWizard.setQcName(mQcCheckViewModel.getQcName().get());
        qcWizard.setRemarks(mQcCheckViewModel.remark.get());
        qcWizard.setMatch("0");
        if (mQcCheckViewModel.inputData.get().trim().equalsIgnoreCase("Yes")) {
            qcStatus = true;
            qcWizard.setMatch("1");

        }
        qcWizard.setQccheckcode(mQcCheckViewModel.sampleQuestions.get().getCode());
        rvpQcDataDetailsActivity.getFragmentData(qcStatus, qcWizard, fragment);
    }
*/

/*
    public boolean validateData() {
        try {
            if (mQcCheckViewModel.inputData.get() != null && !mQcCheckViewModel.inputData.get().trim().isEmpty()) {

                //showMessage("Please enter ..");
                if (mQcCheckViewModel.sampleQuestions.get() != null &&
                        mQcCheckViewModel.sampleQuestions.get().getImageCaptureSettings().equalsIgnoreCase("M")) {
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
        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public void validate(boolean flag) {
        isImageCapture = flag;
    }

    @Override
    public void captureImage() {
        rvpQcDataDetailsActivity.imageHandler.captureImage(mQcCheckViewModel.rvpQualityCheck.get().getAwbNo() + "_" + mQcCheckViewModel.rvpQualityCheck.get().getDrs() + "_QC_CHECK_" + mQcCheckViewModel.sampleQuestions.get().getCode() + ".png", fragmentQcCheckBinding.imageViewCaptureImageCheck, "QC_CHECK_" + mQcCheckViewModel.sampleQuestions.get().getCode());
        // isImageCapture = true;
    }*/

    @Override
    public void captureFrontImage() {
        if(!masterActivityData.getImageSettings().capture_scanned_image)
        {
            edsDetailActivity.imageHandler.captureImage(documentVerificationViewModel.edsActivityWizard.get().getAwbNo()
                            + "_" + edsWithActivityList.edsResponse.getDrsNo()
                            + "_" + "EDS_" + documentVerificationViewModel.masterActivityData.get().getCode()
                            + "_" + 0 + ".png", fragmentDocVerificationBinding.hdfcMasking.imagecamFront, "AADHAR_FRONT_IMAGE"
                    , masterActivityData.imageSettings.getWaterMark());
        }
        else
        {
            edsDetailActivity.digitalCropImageHandler.DigitalCropImage(documentVerificationViewModel.edsActivityWizard.get().getAwbNo()
                            + "_" + edsWithActivityList.edsResponse.getDrsNo()
                            + "_" + "EDS_" + documentVerificationViewModel.masterActivityData.get().getCode()
                            + "_" + 0 + ".png", fragmentDocVerificationBinding.hdfcMasking.imagecamFront, "AADHAR_FRONT_IMAGE"
                    , masterActivityData.imageSettings.getWaterMark());
        }

    }

    @Override
    public String getFrontImageCode()
    {
        return documentVerificationViewModel.masterActivityData.get().getCode()+"_"+0;
    }


    @Override
    public String getRearImageCode()
    {
        return documentVerificationViewModel.masterActivityData.get().getCode()+"_"+1;
    }


    @Override
    public void captureRearImage() {
        if(!masterActivityData.getImageSettings().capture_scanned_image)
        {
            edsDetailActivity.imageHandler.captureImage(documentVerificationViewModel.edsActivityWizard.get().getAwbNo()
                            + "_" + edsWithActivityList.edsResponse.getDrsNo()
                            + "_" + "EDS_" + documentVerificationViewModel.masterActivityData.get().getCode()
                            + "_" + 1 + ".png", fragmentDocVerificationBinding.hdfcMasking.imagecamRear, "AADHAR_REAR_IMAGE",
                    masterActivityData.imageSettings.getWaterMark());
        }
        else
        {
            edsDetailActivity.digitalCropImageHandler.DigitalCropImage(documentVerificationViewModel.edsActivityWizard.get().getAwbNo()
                            + "_" + edsWithActivityList.edsResponse.getDrsNo()
                            + "_" + "EDS_" + documentVerificationViewModel.masterActivityData.get().getCode()
                            + "_" + 1 + ".png", fragmentDocVerificationBinding.hdfcMasking.imagecamRear, "AADHAR_REAR_IMAGE",
                    masterActivityData.imageSettings.getWaterMark());
        }

    }

    @Override
    public void uploadAAdharImage() {
        edsDetailActivity.uploadAAdharImage();
    }

    @Override
    public void getHDFCMaskingStatus() {
        edsDetailActivity.getHDFCMaskingStatus();
    }

    @Override
    public Context getContextProvider() {
        return getContext();
    }

    @Override
    public void setDetail(int position, EDSActivityWizard edsActivityWizard) {
        Log.e("position", position + "");
        this.edsActivityWizard = edsActivityWizard;
        documentVerificationViewModel.setData(edsActivityWizard, masterActivityData);
    }

    @Override
    public void setAadharToCamera() {
        fragmentDocVerificationBinding.hdfcMasking.imagecamFront.setImageResource(R.drawable.cam);
        fragmentDocVerificationBinding.hdfcMasking.imagecamRear.setImageResource(R.drawable.cam);
    }
}
