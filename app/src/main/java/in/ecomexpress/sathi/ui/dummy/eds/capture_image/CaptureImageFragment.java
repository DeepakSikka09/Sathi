package in.ecomexpress.sathi.ui.dummy.eds.capture_image;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.InputFilter;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.FragmentImageCaptureBinding;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.data.eds.EDSActivityResponseWizard;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.local.db.model.ImageModel;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.image.ImageUploadResponse;
import in.ecomexpress.sathi.repo.remote.model.masterdata.ImageSetting;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.base.BaseFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.ActivityData;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.DocumentData;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.EDSDetailActivity;
//import in.ecomexpress.sathi.utils.AppLogs;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.CryptoUtils;
import in.ecomexpress.sathi.utils.ImageFragmentHandler;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static in.ecomexpress.sathi.ui.drs.forward.details.ForwardDetailViewModel.TAG;

/**
 * Created by dhananjayk on 30-03-2019.
 */

@AndroidEntryPoint
public class CaptureImageFragment extends BaseFragment<FragmentImageCaptureBinding, CaptureImageViewModel> implements ICaptureImageNavigation, ActivityData, DocumentData {

    FragmentImageCaptureBinding fragmentImageCaptureBinding;
    EdsWithActivityList edsWithActivityList;
    @Inject
    IDataManager mDataManager;
    @Inject
    ISchedulerProvider iSchedulerProvider;
    @Inject
    CaptureImageViewModel captureImageViewModel;
    @Inject
    CaptureImageAdapter captureImageAdapter;
    ImageFragmentHandler imageFragmentHandler;
    MasterActivityData masterActivityData;
    EDSActivityWizard edsActivityWizard;
    EDSActivityResponseWizard edsActivityResponseWizard = new EDSActivityResponseWizard();
    ArrayList<Integer> arrayList_image_validation = new ArrayList<>();
    private boolean isImageCapture = false;
    private List<Boolean> max;
    private List<Boolean> min;
    private List<Boolean> imageValidation;
    String image_status;
    ImageView globalImageView;
    int position = 0;

    private final BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            arrayList_image_validation = intent.getIntegerArrayListExtra("image_validation");
//            Log.e("size", captureImageViewModel.getEdsImageStatuses().size()+"");
//            for (int i = 0; i < captureImageViewModel.getEdsImageStatuses().size(); i++) {
//                if (image_status.equalsIgnoreCase("M")) {
//                   arrayList_image_validation.add(1);
//                } else {
//                    arrayList_image_validation.add(0);
//                }
//            }
        }
   };

    public static CaptureImageFragment newInstance() {
        CaptureImageFragment fragment = new CaptureImageFragment();
        return fragment;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_image_capture;
    }

    @Override
    public CaptureImageViewModel getViewModel() {
        return captureImageViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            captureImageViewModel.setNavigator(this);
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(listener, new IntentFilter("SEND_IMAGE_VALIDATION"));
            Log.d(TAG, "onCreate: " + this.toString());

        } catch (Exception e) {
            e.printStackTrace();
            getBaseActivity().showSnackbar(e.getMessage());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentImageCaptureBinding = getViewDataBinding();

        fragmentImageCaptureBinding.etRemarks.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
        if (getArguments() != null) {
            this.masterActivityData = getArguments().getParcelable(Constants.EDS_MASTER_LIST);
            this.edsActivityWizard = getArguments().getParcelable(Constants.EDS_ACTIVITY_LIST);
            this.edsWithActivityList = getArguments().getParcelable(Constants.EDS_DATA);

            try {
                captureImageViewModel.setData(edsActivityWizard, masterActivityData);
                if (!masterActivityData.getInstructions().isEmpty()) {
                    fragmentImageCaptureBinding.txtKycInstructions.setClickable(true);
                    fragmentImageCaptureBinding.txtKycInstructions.setMovementMethod(LinkMovementMethod.getInstance());
                    setTextViewHTML(fragmentImageCaptureBinding.txtKycInstructions, masterActivityData.getInstructions());
                }
                if (!edsActivityWizard.getCustomerRemarks().isEmpty()) {
                    fragmentImageCaptureBinding.txtKycRemark.setText(edsActivityWizard.getCustomerRemarks());
                }

            } catch (Exception e) {
                e.printStackTrace();
                getBaseActivity().showSnackbar(e.getMessage());
            }
        }
        imageFragmentHandler = new ImageFragmentHandler(getActivity()) {
            @Override
            public void onBitmapReceived(Bitmap bitmap, String imageUri, ImageView imgView, String imageName, String imageCode) {
                Log.d("Bitmap", "Image");
            }
        };
        setUp();
        inItListener();
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (edsActivityWizard.wizardFlag.isIs_adhar_masking_required()) {
            EDSDetailActivity.edsDetailActivity.setSpinnerCodeValue("AADHAR");
            fragmentImageCaptureBinding.recyclerView.setVisibility(View.GONE);
            fragmentImageCaptureBinding.hdfcMasking.hdfcMasking.setVisibility(View.VISIBLE);
        } else {
            fragmentImageCaptureBinding.recyclerView.setVisibility(View.VISIBLE);
            fragmentImageCaptureBinding.hdfcMasking.hdfcMasking.setVisibility(View.GONE);
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
            fragmentImageCaptureBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            fragmentImageCaptureBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
            fragmentImageCaptureBinding.recyclerView.setAdapter(captureImageAdapter);
            captureImageAdapter.setData(min, max, captureImageViewModel);

//            Intent intent = new Intent("SET_SIZE");
//            intent.putExtra("size", imageValidation.size());
//            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

        } catch (Exception e) {
            e.printStackTrace();
            getBaseActivity().showSnackbar(e.getMessage());
        }

    }

    public int getMax() {
        return max.size();
    }

    private void inItListener() {
        try {
            fragmentImageCaptureBinding.radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    int radioButtonID = radioGroup.getCheckedRadioButtonId();
                    RadioButton radioButton = radioGroup.findViewById(radioButtonID);
                    String checkValue = (String) radioButton.getText();
                    captureImageViewModel.setCheckStatus(checkValue);
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
            if (captureImageViewModel.inputData.get() != null && !captureImageViewModel.inputData.get().trim().isEmpty()) {
                if (captureImageViewModel.inputData.get().equalsIgnoreCase("NO")) {
                    getBaseActivity().showSnackbar("Cannot Select NO to Proceed next");
                    return false;
                }
                if (edsActivityWizard.wizardFlag.isIs_adhar_masking_required()) {
                    if (!captureImageViewModel.getDataManager().getAadharStatus()) {
                        showError("Aadhar Images are not uploaded or Aadhar status is pending.");
                        return false;
                    }
                }
                else
                if (captureImageViewModel.masterActivityData.get() != null) {
                    int imagecount = 0;
                    Log.e("arar", imageValidation + "  max size---" + max.size());

                    if (imageValidation != null && imageValidation.size() >= min.size()) {
                        for (Boolean value : imageValidation) {
                            if (value) {
                                imagecount++;
                            } else {
                                // bad quality image
                                // getBaseActivity().showSnackbar("Please Capture Image");
                                break;
                            }
                        }
                    } else {
                        getBaseActivity().showSnackbar("Please Capture Image");
                    }
                    if (imagecount >= min.size()) {
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
            ex.printStackTrace();
            getBaseActivity().showSnackbar(ex.getMessage());
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
            if (captureImageViewModel.inputData.get() != null && !captureImageViewModel.inputData.get().trim().isEmpty()) {
                if (captureImageViewModel.inputData.get().equalsIgnoreCase("NO"))
                    return true;
                else if (captureImageViewModel.inputData.get().equalsIgnoreCase("Yes")) {
                    return false;
                }

            } else {
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            getBaseActivity().showSnackbar(ex.getMessage());
        }
        return false;
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
    public EDSActivityWizard getActivityWizard() {
        return edsActivityWizard;
    }

    @Override
    public void captureImage(ImageView img, int pos, boolean verifyImage) {
        if (isNetworkConnected()) {
            try {
                Log.e("captured image postiion", pos + "");
                globalImageView = img;
                position = pos;
                image_status = captureImageViewModel.getEdsImageStatuses().get(pos).getImage_status();
                Log.e("image_status", image_status+"");
                // imageValidation.set(pos, true);
                ImageSetting imageSetting = new ImageSetting();
                imageSetting.setWaterMark(captureImageViewModel.setWaterMarkDetail(masterActivityData.getImageSettings().getWaterMark()));
                imageSetting.setMax(max.size());
                imageSetting.setMin(min.size());
                imageSetting.setcapture_scanned_image((masterActivityData.getImageSettings().capture_scanned_image));
                imageSetting.setVerify_image_on_server(masterActivityData.getImageSettings().verify_image_on_server);
                //    masterActivityData.setImageSettings(imageSetting);
                Log.d("DC waterMark", "" + masterActivityData.imageSettings.getWaterMark());

                if (!imageSetting.iscapture_scanned_image()) {
                    EDSDetailActivity.edsDetailActivity.imageHandler.captureImage(captureImageViewModel.edsActivityWizard.get().getAwbNo()
                            + "_" + edsWithActivityList.edsResponse.getDrsNo()
                            + "_" + "EDS_" + captureImageViewModel.masterActivityData.get().getCode()
                            + "_" + pos + ".png", img, "EDS_" + captureImageViewModel.masterActivityData.get().getCode()
                            + "_" + pos, masterActivityData.imageSettings.getWaterMark(), verifyImage);
                } else {
                    EDSDetailActivity.edsDetailActivity.digitalCropImageHandler.DigitalCropImage(captureImageViewModel.edsActivityWizard.get().getAwbNo()
                            + "_" + edsWithActivityList.edsResponse.getDrsNo()
                            + "_" + "EDS_" + captureImageViewModel.masterActivityData.get().getCode()
                            + "_" + pos + ".png", img, "EDS_" + captureImageViewModel.masterActivityData.get().getCode()
                            + "_" + pos, masterActivityData.getImageSettings().getWaterMark(), verifyImage);
                }
//                edsDetailActivity.imageHandler.captureImage(captureImageViewModel.edsActivityWizard.get().getAwbNo()
//                        + "_" + edsWithActivityList.edsResponse.getDrsNo()
//                        + "_" + "EDS_" + captureImageViewModel.masterActivityData.get().getCode()
//                        + "_" + pos + ".png", img, "EDS_" + captureImageViewModel.masterActivityData.get().getCode()
//                        + "_" + pos, masterActivityData.imageSettings.getWaterMark(), verifyImage);
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

    @Override
    public void getData(BaseFragment fragment) {
        boolean activityStatus = false;
        try {

            edsActivityResponseWizard.setCode(edsActivityWizard.getCode());
            edsActivityResponseWizard.setInput_value("false");
            edsActivityResponseWizard.setInputRemark("");
            edsActivityResponseWizard.setIsDone("false");
            edsActivityResponseWizard.setActivityId("0");
            if (captureImageViewModel.inputData.get().trim().equalsIgnoreCase("Yes")) {
                activityStatus = true;
                edsActivityResponseWizard.setInput_value("true");
                edsActivityResponseWizard.setInputRemark(fragmentImageCaptureBinding.etRemarks.getText().toString());
                edsActivityResponseWizard.setIsDone("true");
                edsActivityResponseWizard.setActivityId(edsActivityWizard.getActivityId());
                edsActivityResponseWizard.setAdditionalInfos(new ArrayList<>());

            }
            EDSDetailActivity.edsDetailActivity.getFragmentData(activityStatus, edsActivityResponseWizard, fragment);
        } catch (Exception e) {
            e.printStackTrace();
            getBaseActivity().showSnackbar(e.getMessage());
        }
    }

    private void uploadImageServer() {
        final long timeStampUploadImageToServer = Calendar.getInstance().getTimeInMillis();
        HashMap<String, Long> timeStampTagging = new HashMap<>();
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        ImageModel imageModel = null;
        //for (ImageModel imageModel : imageList) {
        try {
            final long timeStamp = Calendar.getInstance().getTimeInMillis();
            timeStampTagging.put(imageModel.getImageName(), timeStamp);
            //  writeAnalytics(timeStamp, "Uploading Image to server imageName[" + imageModel.getImageName() + "]");
            File file = new File(imageModel.getImage());
            byte[] bytes = CryptoUtils.decryptFile1(file.toString(), Constants.ENC_DEC_KEY);
            RequestBody mFile = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), mFile);
            RequestBody awb_no = RequestBody.create(MediaType.parse("text/plain"), imageModel.getAwbNo());
            RequestBody drs_no = RequestBody.create(MediaType.parse("text/plain"), imageModel.getDraNo());
            RequestBody image_code = RequestBody.create(MediaType.parse("text/plain"), imageModel.getImageCode());
            RequestBody image_name = RequestBody.create(MediaType.parse("text/plain"), file.getName());
            RequestBody image_type = RequestBody.create(MediaType.parse("text/plain"), imageModel.getImageType());

            Map<String, RequestBody> map = new HashMap<>();
            map.put("image", mFile);
            map.put("awb_no", awb_no);
            map.put("drs_no", drs_no);
            map.put("image_code", image_code);
            map.put("image_name", image_name);
            map.put("image_type", image_type);
            Map<String, String> headers = new HashMap<>();
            headers.put("token", mDataManager.getAuthToken());
            headers.put("Accept", "application/json");
            Log.d(TAG, "uploadImageServer: abc" + imageModel.toString());
            //AppLogs.Companion.writeRestApiRequest(timeStamp, map);
            compositeDisposable.add(mDataManager.
                    doImageUploadApiCall(mDataManager.getAuthToken(),mDataManager.getEcomRegion(), imageModel.getImageType(), headers, map, fileToUpload).subscribeOn(iSchedulerProvider.io())
                    .observeOn(iSchedulerProvider.io()).subscribe(new Consumer<ImageUploadResponse>() {
                        @Override
                        public void accept(ImageUploadResponse imageUploadResponse) throws Exception {
//                                writeAnalytics(timeStampTagging.get(imageUploadResponse.getFileName()), "Uploading Image to server imageName[" + imageUploadResponse.getFileName() + "], status[" + imageUploadResponse.getStatus() + "]");
                           // AppLogs.Companion.writeRestApiResponse(timeStamp, imageUploadResponse);
                            if (imageUploadResponse != null) {
                                if (imageUploadResponse.getStatus().equalsIgnoreCase("Success")) {
                                    try {
                                        Log.d(TAG, "accept:111 " + imageUploadResponse.toString());
                                        imageModel.setStatus(2);
                                        if (imageUploadResponse.getImageId() != null)
                                            imageModel.setImageId(imageUploadResponse.getImageId());
                                        mDataManager.saveImage(imageModel).
                                                subscribe(new Consumer<Boolean>() {
                                                    @Override
                                                    public void accept(Boolean aBoolean) {
                                                        if (aBoolean) {
                                                            // deleteImageFile(imageUploadResponse);
                                                        }
                                                    }
                                                });
                                    } catch (Exception ex) {
                                        Log.e("Image Sync exception", ex.toString());
                                        //   writeCrashes(timeStampTagging.get(imageUploadResponse.getFileName()), "Error in file name[" + imageUploadResponse.getFileName() + "], " + " crash [" + ex.getMessage() + "]");
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    }, throwable -> {
                        throwable.printStackTrace();
                        // writeCrashes(timeStamp, new Exception(throwable));
                        Log.d(TAG, "accept: error");
                    }));

        } catch (Exception ex) {
            ex.printStackTrace();
            getBaseActivity().showSnackbar(ex.getMessage());
            //     writeCrashes(timeStampUploadImageToServer, ex);
        }
        // }
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(listener);
        super.onDestroy();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        try {

            captureImageAdapter.myViewHolder.doBlankImageView();
//            if (globalImageView.getDrawable().getConstantState() != null && globalImageView.getDrawable().getConstantState()
//                    != getResources().getDrawable(R.drawable.cam).getConstantState()) {
//                for (int i = 0; i < max.size(); i++) {
//                    globalImageView.setImageResource(R.drawable.cam);
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void captureFrontImage() {
        if(!masterActivityData.getImageSettings().capture_scanned_image)
        {
            EDSDetailActivity.edsDetailActivity.imageHandler.captureImage(captureImageViewModel.edsActivityWizard.get().getAwbNo()
                            + "_" + edsWithActivityList.edsResponse.getDrsNo()
                            + "_" + "EDS_" + captureImageViewModel.masterActivityData.get().getCode()
                            + "_" + 0 + ".png", fragmentImageCaptureBinding.hdfcMasking.imagecamFront, "AADHAR_FRONT_IMAGE"
                    , masterActivityData.imageSettings.getWaterMark());
        }
        else
        {
            EDSDetailActivity.edsDetailActivity.digitalCropImageHandler.DigitalCropImage(captureImageViewModel.edsActivityWizard.get().getAwbNo()
                            + "_" + edsWithActivityList.edsResponse.getDrsNo()
                            + "_" + "EDS_" + captureImageViewModel.masterActivityData.get().getCode()
                            + "_" + 0 + ".png", fragmentImageCaptureBinding.hdfcMasking.imagecamFront, "AADHAR_FRONT_IMAGE"
                    , masterActivityData.imageSettings.getWaterMark());
        }

    }

    @Override
    public String getFrontImageCode()
    {
        return captureImageViewModel.masterActivityData.get().getCode()+"_"+0;
    }


    @Override
    public String getRearImageCode()
    {
        return captureImageViewModel.masterActivityData.get().getCode()+"_"+1;
    }


    @Override
    public void captureRearImage() {
        if(!masterActivityData.getImageSettings().capture_scanned_image)
        {
            EDSDetailActivity.edsDetailActivity.imageHandler.captureImage(captureImageViewModel.edsActivityWizard.get().getAwbNo()
                            + "_" + edsWithActivityList.edsResponse.getDrsNo()
                            + "_" + "EDS_" + captureImageViewModel.masterActivityData.get().getCode()
                            + "_" + 1 + ".png", fragmentImageCaptureBinding.hdfcMasking.imagecamRear, "AADHAR_REAR_IMAGE",
                    masterActivityData.imageSettings.getWaterMark());
        }
        else
        {
            EDSDetailActivity.edsDetailActivity.digitalCropImageHandler.DigitalCropImage(captureImageViewModel.edsActivityWizard.get().getAwbNo()
                            + "_" + edsWithActivityList.edsResponse.getDrsNo()
                            + "_" + "EDS_" + captureImageViewModel.masterActivityData.get().getCode()
                            + "_" + 1 + ".png", fragmentImageCaptureBinding.hdfcMasking.imagecamRear, "AADHAR_REAR_IMAGE",
                    masterActivityData.imageSettings.getWaterMark());
        }

    }

    @Override
    public void uploadAAdharImage() {
        EDSDetailActivity.edsDetailActivity.uploadAAdharImage();
    }

    @Override
    public void getHDFCMaskingStatus() {
        EDSDetailActivity.edsDetailActivity.getHDFCMaskingStatus();
    }

    @Override
    public Context getContextProvider() {
        return getContext();
    }

    @Override
    public void setDetail(int position, EDSActivityWizard edsActivityWizard) {
        Log.e("position", position + "");
        this.edsActivityWizard = edsActivityWizard;
        captureImageViewModel.setData(edsActivityWizard, masterActivityData);
    }

    @Override
    public void setAadharToCamera() {
        fragmentImageCaptureBinding.hdfcMasking.imagecamFront.setImageResource(R.drawable.cam);
        fragmentImageCaptureBinding.hdfcMasking.imagecamRear.setImageResource(R.drawable.cam);
    }


}
