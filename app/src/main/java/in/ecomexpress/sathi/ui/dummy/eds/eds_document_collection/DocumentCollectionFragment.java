package in.ecomexpress.sathi.ui.dummy.eds.eds_document_collection;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.FragmentDocCollectionBinding;
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

//import preview.PreviewActivity;


/**
 * Created by dhananjayk on 06-11-2018.
 */
@AndroidEntryPoint
public class DocumentCollectionFragment extends BaseFragment<FragmentDocCollectionBinding, DocumentCollectionViewModel> implements IDocumentCollectionNavigation, ActivityData, DocumentData {

    FragmentDocCollectionBinding fragmentDocCollectionBinding;
    EdsWithActivityList edsWithActivityList;
    @Inject
    DocumentCollectionViewModel documentCollectionViewModel;
    @Inject
    DocumentCollectionAdapter documentCollectionAdapter;
    MasterActivityData masterActivityData;
    EDSActivityWizard edsActivityWizard;
    private boolean isImageCapture = false;
    EDSActivityResponseWizard edsActivityResponseWizard = new EDSActivityResponseWizard();
    private List<Boolean> max;
    private List<Boolean> min;
    private List<Boolean> imageValidation;
    private static final int IMAGE_SCANNER_CODE = 1001;
    private static final int CAMERA_SCANNER_CODE = 1002;
    int position;
    String image_status;
    private static final String APPKEY = "02LQ7AaA09hPdt5Ned7FByhP";//02LQ7AaA09hPdt5Ned7FByhP
    private static final String mRootPath = Environment.getExternalStorageDirectory()
            .getAbsolutePath();
    private static final String outPutFilePath = mRootPath + File.separator + "intsigCsPdf";

    public static DocumentCollectionFragment newInstance() {
        DocumentCollectionFragment fragment = new DocumentCollectionFragment();
        return fragment;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_doc_collection;
    }

    @Override
    public DocumentCollectionViewModel getViewModel() {
        return documentCollectionViewModel;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            documentCollectionViewModel.setNavigator(this);
            Log.d(TAG, "onCreate: " + this.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            fragmentDocCollectionBinding = getViewDataBinding();
            fragmentDocCollectionBinding.etRemarks.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
            if (getArguments() != null) {
                this.masterActivityData = getArguments().getParcelable(Constants.EDS_MASTER_LIST);
                this.edsActivityWizard = getArguments().getParcelable(Constants.EDS_ACTIVITY_LIST);
                this.edsWithActivityList = getArguments().getParcelable(Constants.EDS_DATA);
                documentCollectionViewModel.setData(edsActivityWizard, masterActivityData);
                try {
                    if (!masterActivityData.getInstructions().isEmpty()) {
                        fragmentDocCollectionBinding.txtKycInstructions.setClickable(true);
                        fragmentDocCollectionBinding.txtKycInstructions.setMovementMethod(LinkMovementMethod.getInstance());
                        setTextViewHTML(fragmentDocCollectionBinding.txtKycInstructions, masterActivityData.getInstructions());
                    }
                    if (!edsActivityWizard.getCustomerRemarks().isEmpty()) {
                        fragmentDocCollectionBinding.txtKycRemark.setText(edsActivityWizard.getCustomerRemarks());
                    }
                } catch (Exception e) {
                    getBaseActivity().showSnackbar(e.getMessage());
                    e.printStackTrace();
                }
            }

            setUp();
            inItListener();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (edsActivityWizard.wizardFlag.isIs_adhar_masking_required()) {
            edsDetailActivity.setSpinnerCodeValue("AADHAR");
            fragmentDocCollectionBinding.recyclerView.setVisibility(View.GONE);
            fragmentDocCollectionBinding.hdfcMasking.hdfcMasking.setVisibility(View.VISIBLE);
        } else {
            fragmentDocCollectionBinding.recyclerView.setVisibility(View.VISIBLE);
            fragmentDocCollectionBinding.hdfcMasking.hdfcMasking.setVisibility(View.GONE);
        }

    }


    public void showMessage(String s) {
        Log.d(TAG, "showMessage: " + s);
    }

    private void setUp() {
        try {
            //  Toast.makeText(getContext(), "onStart", Toast.LENGTH_LONG).show();
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
            fragmentDocCollectionBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            fragmentDocCollectionBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
            fragmentDocCollectionBinding.recyclerView.setAdapter(documentCollectionAdapter);
            documentCollectionAdapter.setData(min, max, documentCollectionViewModel);

        } catch (Exception e) {
            e.printStackTrace();
            getBaseActivity().showSnackbar(e.getMessage());
        }
    }

    private void inItListener() {
        try {
            fragmentDocCollectionBinding.radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    int radioButtonID = radioGroup.getCheckedRadioButtonId();
                    RadioButton radioButton = radioGroup.findViewById(radioButtonID);
                    String checkValue = (String) radioButton.getText();
                    documentCollectionViewModel.setCheckStatus(checkValue);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            getBaseActivity().showSnackbar(e.getMessage());
        }
    }

    @Override
    public boolean validateData() {
        try {
            if (documentCollectionViewModel.inputData.get() != null && !documentCollectionViewModel.inputData.get().trim().isEmpty()) {
                if (documentCollectionViewModel.inputData.get().equalsIgnoreCase("NO")) {
                    getBaseActivity().showSnackbar("Cannot Select NO to Proceed next");
                    return false;
                }
                if (edsActivityWizard.wizardFlag.isIs_adhar_masking_required()) {
                    if (!documentCollectionViewModel.getDataManager().getAadharStatus()) {
                        showError("Aadhar Images are not uploaded or Aadhar status is pending.");
                        return false;
                    }
                }
                else
                if (documentCollectionViewModel.masterActivityData.get() != null) {
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
                getBaseActivity().showSnackbar("Please select Yes or No");
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
        try {
            if (documentCollectionViewModel.inputData.get() != null && !documentCollectionViewModel.inputData.get().trim().isEmpty()) {
                if (documentCollectionViewModel.inputData.get().equalsIgnoreCase("NO"))
                    return true;
                else if (documentCollectionViewModel.inputData.get().equalsIgnoreCase("Yes")) {
                    return false;
                }

            } else {
                return false;
            }
        } catch (Exception ex) {
            getBaseActivity().showSnackbar(ex.getMessage());
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public EDSActivityWizard getActivityWizard() {
        return edsActivityWizard;
    }

    @Override
    public void setImageValidation() {
        if(image_status.equalsIgnoreCase("M"))
        {
            imageValidation.set(position, true);
        }
    }

    @Override
    public void captureImage(ImageView img, int pos) {
        if (isNetworkConnected()) {
            position = pos;
            image_status = documentCollectionViewModel.getEdsImageStatuses().get(pos).getImage_status();
            // imageValidation.set(pos, true);
            try {
                ImageSetting imageSetting = new ImageSetting();
                imageSetting.setWaterMark(documentCollectionViewModel.setWaterMarkDetail(masterActivityData.getImageSettings().getWaterMark()));
                imageSetting.setMax(max.size());
                imageSetting.setMin(min.size());
                imageSetting.setcapture_scanned_image((masterActivityData.getImageSettings().capture_scanned_image));
                imageSetting.setVerify_image_on_server(masterActivityData.getImageSettings().verify_image_on_server);
                masterActivityData.setImageSettings(imageSetting);
                Log.d("DC waterMark", "" + masterActivityData.imageSettings.getWaterMark());
                if (!imageSetting.iscapture_scanned_image()) {
                    edsDetailActivity.imageHandler.captureImage(documentCollectionViewModel.edsActivityWizard.get().getAwbNo()
                            + "_" + edsWithActivityList.edsResponse.getDrsNo()
                            + "_" + "EDS_" + documentCollectionViewModel.masterActivityData.get().getCode()
                            + "_" + pos + ".png", img, "EDS_" + documentCollectionViewModel.masterActivityData.get().getCode()
                            + "_" + pos, masterActivityData.imageSettings.getWaterMark());

                } else {
                    edsDetailActivity.digitalCropImageHandler.DigitalCropImage(documentCollectionViewModel.edsActivityWizard.get().getAwbNo()
                                    + "_" + edsWithActivityList.edsResponse.getDrsNo()
                                    + "_" + "EDS_" + documentCollectionViewModel.masterActivityData.get().getCode()
                                    + "_" + pos + ".png", img, "EDS_" + documentCollectionViewModel.masterActivityData.get().getCode() + "_" + pos
                            , masterActivityData.getImageSettings().getWaterMark());
                }

            } catch (Exception e) {
                e.printStackTrace();
                getBaseActivity().showSnackbar(e.getMessage());
            }
        } else {
            getBaseActivity().showSnackbar("Please check your internet connection.");
        }
    }

    @Override
    public void showError(String e) {
        getBaseActivity().showSnackbar(e);
    }

//    @Override
//    public void digitalCrop() {
//        edsDetailActivity.digitalCrop();
//    }


    @Override
    public void getData(BaseFragment fragment) {
        boolean activityStatus = false;
        try {
            edsActivityResponseWizard.setCode(edsActivityWizard.getCode());
            edsActivityResponseWizard.setInput_value("false");
            edsActivityResponseWizard.setInputRemark("");
            edsActivityResponseWizard.setIsDone("false");
            edsActivityResponseWizard.setActivityId("0");
            if (documentCollectionViewModel.inputData.get().trim().equalsIgnoreCase("Yes")) {
                activityStatus = true;
                edsActivityResponseWizard.setInput_value("true");
                edsActivityResponseWizard.setInputRemark(fragmentDocCollectionBinding.etRemarks.getText().toString());
                edsActivityResponseWizard.setIsDone("true");
                edsActivityResponseWizard.setActivityId(edsActivityWizard.getActivityId());
                edsActivityResponseWizard.setAdditionalInfos(new ArrayList<>());

            }
            edsDetailActivity.getFragmentData(activityStatus, edsActivityResponseWizard, fragment);

        } catch (Exception e) {
            e.printStackTrace();
            getBaseActivity().showSnackbar(e.getMessage());
        }
    }

    @Override
    public void captureFrontImage() {
        if(!masterActivityData.getImageSettings().capture_scanned_image)
        {
            edsDetailActivity.imageHandler.captureImage(documentCollectionViewModel.edsActivityWizard.get().getAwbNo()
                            + "_" + edsWithActivityList.edsResponse.getDrsNo()
                            + "_" + "EDS_" + documentCollectionViewModel.masterActivityData.get().getCode()
                            + "_" + 0 + ".png", fragmentDocCollectionBinding.hdfcMasking.imagecamFront, "AADHAR_FRONT_IMAGE"
                    , masterActivityData.imageSettings.getWaterMark());
        }
        else
        {
            edsDetailActivity.digitalCropImageHandler.DigitalCropImage(documentCollectionViewModel.edsActivityWizard.get().getAwbNo()
                            + "_" + edsWithActivityList.edsResponse.getDrsNo()
                            + "_" + "EDS_" + documentCollectionViewModel.masterActivityData.get().getCode()
                            + "_" + 0 + ".png", fragmentDocCollectionBinding.hdfcMasking.imagecamFront, "AADHAR_FRONT_IMAGE"
                    , masterActivityData.imageSettings.getWaterMark());
        }

    }

    @Override
    public String getFrontImageCode()
    {
        return documentCollectionViewModel.masterActivityData.get().getCode()+"_"+0;
    }


    @Override
    public String getRearImageCode()
    {
        return documentCollectionViewModel.masterActivityData.get().getCode()+"_"+1;
    }


    @Override
    public void captureRearImage() {
        if(!masterActivityData.getImageSettings().capture_scanned_image)
        {
            edsDetailActivity.imageHandler.captureImage(documentCollectionViewModel.edsActivityWizard.get().getAwbNo()
                            + "_" + edsWithActivityList.edsResponse.getDrsNo()
                            + "_" + "EDS_" + documentCollectionViewModel.masterActivityData.get().getCode()
                            + "_" + 1 + ".png", fragmentDocCollectionBinding.hdfcMasking.imagecamRear, "AADHAR_REAR_IMAGE",
                    masterActivityData.imageSettings.getWaterMark());
        }
        else
        {
            edsDetailActivity.digitalCropImageHandler.DigitalCropImage(documentCollectionViewModel.edsActivityWizard.get().getAwbNo()
                            + "_" + edsWithActivityList.edsResponse.getDrsNo()
                            + "_" + "EDS_" + documentCollectionViewModel.masterActivityData.get().getCode()
                            + "_" + 1 + ".png", fragmentDocCollectionBinding.hdfcMasking.imagecamRear, "AADHAR_REAR_IMAGE",
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
        documentCollectionViewModel.setData(edsActivityWizard, masterActivityData);
    }

    @Override
    public void setAadharToCamera() {
        fragmentDocCollectionBinding.hdfcMasking.imagecamFront.setImageResource(R.drawable.cam);
        fragmentDocCollectionBinding.hdfcMasking.imagecamRear.setImageResource(R.drawable.cam);
    }

}
