package in.ecomexpress.sathi.ui.dummy.eds.ac_document_list_collection;


import android.content.Context;
import android.os.Bundle;
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
import in.ecomexpress.sathi.databinding.FragmentAcDocumentListCollectionBinding;
import in.ecomexpress.sathi.repo.local.data.eds.EDSActivityResponseWizard;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.ImageSetting;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.ActivityData;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.DocumentData;
import in.ecomexpress.sathi.utils.Constants;

import static in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.EDSDetailActivity.edsDetailActivity;
import static in.ecomexpress.sathi.ui.drs.forward.details.ForwardDetailViewModel.TAG;

/**
 * Created by dhananjayk on 01-05-2019.
 */
@AndroidEntryPoint
public class AcDocumentListCollectionFragment extends BaseFragment<FragmentAcDocumentListCollectionBinding, AcDocumentListViewModel> implements IAcDocumentListNavigation, ActivityData, DocumentData {

    public String codes = "";
    FragmentAcDocumentListCollectionBinding fragmentAcDocumentListCollectionBinding;
    EdsWithActivityList edsWithActivityList;
    @Inject
    AcDocumentListViewModel acDocumentListViewModel;
    MasterActivityData masterActivityData;
    EDSActivityWizard edsActivityWizard;
    EDSActivityResponseWizard edsActivityResponseWizard = new EDSActivityResponseWizard();
    @Inject
    AcDocumentListCollectionAdapter acDocumentListCollectionAdapter;
    private boolean isImageCapture = false;
    private List<Boolean> max;
    private List<Boolean> min;
    private List<Boolean> imageValidation;
    int position;
    String image_status;

    public static AcDocumentListCollectionFragment newInstance() {

        AcDocumentListCollectionFragment fragment = new AcDocumentListCollectionFragment();
        return fragment;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_ac_document_list_collection;
    }

    @Override
    public AcDocumentListViewModel getViewModel() {
        return acDocumentListViewModel;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: " + this.toString());
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        acDocumentListViewModel.setNavigator(this);
        fragmentAcDocumentListCollectionBinding = getViewDataBinding();
        try {
            if (getArguments() != null) {
                this.masterActivityData = getArguments().getParcelable(Constants.EDS_MASTER_LIST);
                this.edsActivityWizard = getArguments().getParcelable(Constants.EDS_ACTIVITY_LIST);
                this.edsWithActivityList = getArguments().getParcelable(Constants.EDS_DATA);
                acDocumentListViewModel.setData(edsActivityWizard, masterActivityData);
                acDocumentListViewModel.getListDetail();
           /* try {
                if (!masterActivityData.getInstructions().isEmpty() || !edsActivityWizard.getCustomerRemarks().isEmpty()) {
                    fragmentDocumentListCollectionBinding.txtKycInstructions.setClickable(true);
                    fragmentDocumentListCollectionBinding.txtKycInstructions.setMovementMethod(LinkMovementMethod.getInstance());
                    setTextViewHTML(fragmentDocumentListCollectionBinding.txtKycInstructions, masterActivityData.getInstructions() + "\n" + edsActivityWizard.getCustomerRemarks());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
*/

            }
            setUp();
        } catch (Exception e) {
            e.printStackTrace();
            getBaseActivity().showSnackbar(e.getMessage());
        }

    }

    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();


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
            fragmentAcDocumentListCollectionBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            fragmentAcDocumentListCollectionBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
            fragmentAcDocumentListCollectionBinding.recyclerView.setAdapter(acDocumentListCollectionAdapter);
            acDocumentListCollectionAdapter.setData(min, max, acDocumentListViewModel);

        } catch (Exception e) {
            e.printStackTrace();
            getBaseActivity().showSnackbar(e.getMessage());
        }

    }

    public void showMessage(String s) {
        Log.d(TAG, "showMessage: " + s);
    }

    @Override
    public void setUserVisibleHint(boolean v) {
        super.setUserVisibleHint(v);
        if (v) {
            Log.d("visible", "true");
        } else {
            Log.d("visible", "Called");
        }
    }

    @Override
    public boolean validateData() {
        try {
            if (!codes.equals("1")) {
                if (acDocumentListViewModel.masterActivityData.get() != null) {
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
                getBaseActivity().showSnackbar("Please select the Document type");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            getBaseActivity().showSnackbar(e.getMessage());
        }
        return false;
    }

    @Override
    public void getData(BaseFragment fragment) {
        try {
            boolean activityStatus = false;

            edsActivityResponseWizard.setCode(edsActivityWizard.getCode());
            edsActivityResponseWizard.setInput_value("false");
            edsActivityResponseWizard.setInputRemark("");
            edsActivityResponseWizard.setIsDone("false");
            edsActivityResponseWizard.setActivityId("0");
            if (!codes.equalsIgnoreCase("1")) {
                activityStatus = true;
                edsActivityResponseWizard.setInput_value(codes);
                edsActivityResponseWizard.setInputRemark("NONE");
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
    public void validate(boolean flag) {
        isImageCapture = flag;
    }

    @Override
    public boolean validateCancelData() {
        try {
            String getSelectedValue = acDocumentListViewModel.getSpinnerValue();
//            Toast.makeText(getContext(), "value : " + getSelectedValue, Toast.LENGTH_SHORT).show();
            if (getSelectedValue.equals("1")) {
                return true;
            }
//            return documentListViewModel.spinnerCode.equals("1");

        } catch (Exception e) {
            e.printStackTrace();
            getBaseActivity().showSnackbar(e.getMessage());
        }
        return false;
    }

    @Override
    public EDSActivityWizard getActivityWizard() {
        return null;
    }

    @Override
    public void onChooseReasonSpinner(String code) {
        codes = code;
        edsDetailActivity.setSpinnerCodeValue(codes);
        if (codes.equalsIgnoreCase("AADHAR")) {
            fragmentAcDocumentListCollectionBinding.recyclerView.setVisibility(View.GONE);
            fragmentAcDocumentListCollectionBinding.hdfcMasking.hdfcMasking.setVisibility(View.VISIBLE);
        } else {
            edsDetailActivity.setCardType(codes);
            fragmentAcDocumentListCollectionBinding.recyclerView.setVisibility(View.VISIBLE);
            fragmentAcDocumentListCollectionBinding.hdfcMasking.hdfcMasking.setVisibility(View.GONE);
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

    public void captureImage(ImageView img, int pos) {
        position = pos;
        //imageValidation.set(pos, true);
        try {
            image_status = acDocumentListViewModel.getEdsImageStatuses().get(pos).getImage_status();
            ImageSetting imageSetting = new ImageSetting();
            imageSetting.setWaterMark(acDocumentListViewModel.setWaterMarkDetail(masterActivityData.getImageSettings().getWaterMark()));
            imageSetting.setMax(max.size());
            imageSetting.setMin(min.size());
            imageSetting.setcapture_scanned_image((masterActivityData.getImageSettings().capture_scanned_image));
            imageSetting.setVerify_image_on_server(masterActivityData.getImageSettings().verify_image_on_server);
            masterActivityData.setImageSettings(imageSetting);
            Log.d("DC waterMark", "" + masterActivityData.imageSettings.getWaterMark());
            if (!imageSetting.iscapture_scanned_image()) {
                edsDetailActivity.imageHandler.captureImage(acDocumentListViewModel.edsActivityWizard.get().getAwbNo()
                        + "_" + edsWithActivityList.edsResponse.getDrsNo()
                        + "_" + "EDS_" + acDocumentListViewModel.masterActivityData.get().getCode()
                        + "_" + pos + ".png", img, "EDS_" + acDocumentListViewModel.masterActivityData.get().getCode()
                        + "_" + pos, masterActivityData.imageSettings.getWaterMark());

            } else {
                edsDetailActivity.digitalCropImageHandler.DigitalCropImage(acDocumentListViewModel.edsActivityWizard.get().getAwbNo()
                        + "_" + edsWithActivityList.edsResponse.getDrsNo()
                        + "_" + "EDS_" + acDocumentListViewModel.masterActivityData.get().getCode()
                        + "_" + pos + ".png", img, "EDS_" + acDocumentListViewModel.masterActivityData.get().getCode()
                        + "_" + pos, masterActivityData.imageSettings.getWaterMark());
            }

        } catch (Exception e) {
            getBaseActivity().showSnackbar(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void error(String message) {
        getBaseActivity().showSnackbar(message);
    }

    @Override
    public void captureFrontImage() {
        if(!masterActivityData.getImageSettings().capture_scanned_image)
        {
            edsDetailActivity.imageHandler.captureImage(acDocumentListViewModel.edsActivityWizard.get().getAwbNo()
                            + "_" + edsWithActivityList.edsResponse.getDrsNo()
                            + "_" + "EDS_" + acDocumentListViewModel.masterActivityData.get().getCode()
                            + "_" + 0 + ".png", fragmentAcDocumentListCollectionBinding.hdfcMasking.imagecamFront, "AADHAR_FRONT_IMAGE"
                    , masterActivityData.imageSettings.getWaterMark());
        }
        else
        {
            edsDetailActivity.digitalCropImageHandler.DigitalCropImage(acDocumentListViewModel.edsActivityWizard.get().getAwbNo()
                            + "_" + edsWithActivityList.edsResponse.getDrsNo()
                            + "_" + "EDS_" + acDocumentListViewModel.masterActivityData.get().getCode()
                            + "_" + 0 + ".png", fragmentAcDocumentListCollectionBinding.hdfcMasking.imagecamFront, "AADHAR_FRONT_IMAGE"
                    , masterActivityData.imageSettings.getWaterMark());
        }

    }

    @Override
    public String getFrontImageCode()
    {
        return acDocumentListViewModel.masterActivityData.get().getCode()+"_"+0;
    }


    @Override
    public String getRearImageCode()
    {
        return acDocumentListViewModel.masterActivityData.get().getCode()+"_"+1;
    }


    @Override
    public void captureRearImage() {
        if(!masterActivityData.getImageSettings().capture_scanned_image)
        {
            edsDetailActivity.imageHandler.captureImage(acDocumentListViewModel.edsActivityWizard.get().getAwbNo()
                            + "_" + edsWithActivityList.edsResponse.getDrsNo()
                            + "_" + "EDS_" + acDocumentListViewModel.masterActivityData.get().getCode()
                            + "_" + 1 + ".png", fragmentAcDocumentListCollectionBinding.hdfcMasking.imagecamRear, "AADHAR_REAR_IMAGE",
                    masterActivityData.imageSettings.getWaterMark());
        }
        else
        {
            edsDetailActivity.digitalCropImageHandler.DigitalCropImage(acDocumentListViewModel.edsActivityWizard.get().getAwbNo()
                            + "_" + edsWithActivityList.edsResponse.getDrsNo()
                            + "_" + "EDS_" + acDocumentListViewModel.masterActivityData.get().getCode()
                            + "_" + 1 + ".png", fragmentAcDocumentListCollectionBinding.hdfcMasking.imagecamRear, "AADHAR_REAR_IMAGE",
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
    public void showError(String e) {
        getBaseActivity().showSnackbar(e);

    }

    @Override
    public void setDetail(int position, EDSActivityWizard edsActivityWizard) {
        Log.e("position", position + "");
        this.edsActivityWizard = edsActivityWizard;
        acDocumentListViewModel.setData(edsActivityWizard, masterActivityData);
    }

    @Override
    public void setAadharToCamera() {
        fragmentAcDocumentListCollectionBinding.hdfcMasking.imagecamFront.setImageResource(R.drawable.cam);
        fragmentAcDocumentListCollectionBinding.hdfcMasking.imagecamRear.setImageResource(R.drawable.cam);
    }

}
