package in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.qc_check_image;

import static android.app.Activity.RESULT_OK;
import static in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.RvpQcDataDetailsActivity.rvpQcDataDetailsActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.FragmentQcCheckImageBinding;
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
import me.relex.circleindicator.CircleIndicator;

@AndroidEntryPoint
public class QcCheckImageFragment extends BaseFragment<FragmentQcCheckImageBinding, QcCheckImageViewModel> implements IQcCheckImageNavigation, IQcData {

    FragmentQcCheckImageBinding fragmentQcCheckImageBinding;
    @Inject
    QcCheckImageViewModel mQcCheckImageViewModel;
    boolean isPreviewImageClicked = false;
    RvpCommit.QcWizard qcWizard = new RvpCommit.QcWizard();
    SampleQuestion sampleQuestion;
    RvpQualityCheck rvpQualityCheck;
    private boolean isImageCapture = false;
    private static final int CAMERA_REQUEST_CODE = 100;

    public static QcCheckImageFragment newInstance() {
        return new QcCheckImageFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_qc_check_image;
    }

    @Override
    public QcCheckImageViewModel getViewModel() {
        return mQcCheckImageViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void inItListener() {
        try {
            fragmentQcCheckImageBinding.rgQualityCheck.setOnCheckedChangeListener((radioGroup, i) -> {
                int radioButtonID = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = radioGroup.findViewById(radioButtonID);
                String checkValue = (String) radioButton.getText();
                mQcCheckImageViewModel.setCheckStatus(checkValue);
            });

        } catch (Exception e) {
            Logger.e(QcCheckImageFragment.class.getName(), e.getMessage());
            showError(e.getMessage());
        }
        if (rvpQualityCheck.getQcValue() != null) {
            String[] split_images = rvpQualityCheck.getQcValue().split(",");
            Glide.with(getActivity()).load(split_images[0])
                    .placeholder(R.drawable.sathi_placeholder)
                    .error(R.drawable.sathi_placeholder)
                    .into(fragmentQcCheckImageBinding.imageViewPreview);
        }
    }

    public boolean validateData() {
        try {
            if (mQcCheckImageViewModel.checkStatus.get() != null && !Objects.requireNonNull(mQcCheckImageViewModel.checkStatus.get()).trim().isEmpty()) {
                if (mQcCheckImageViewModel.sampleQuestions.get() != null &&
                        Objects.requireNonNull(mQcCheckImageViewModel.sampleQuestions.get()).getImageCaptureSettings().equalsIgnoreCase("M")) {
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
        } catch (Exception e) {
            Logger.e(QcCheckImageFragment.class.getName(), e.getMessage());
            showError(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean getPreviewImageClicked() {
        if (isPreviewImageClicked) {
            return true;
        } else {
            getBaseActivity().showSnackbar("Please View Sample Images Of Shipment.");
        }
        return false;
    }

    @Override
    public void validate(boolean flag) {
        isImageCapture = flag;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            mQcCheckImageViewModel.setNavigator(this);
            fragmentQcCheckImageBinding = getViewDataBinding();
            fragmentQcCheckImageBinding.remarks.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
            fragmentQcCheckImageBinding.remarks.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
            if (getArguments() != null) {
                this.sampleQuestion = getArguments().getParcelable(Constants.QC_SAMPLE_QC);
                this.rvpQualityCheck = getArguments().getParcelable(Constants.QC_CHECK_LIST);
                if (sampleQuestion != null && rvpQualityCheck != null) {
                    mQcCheckImageViewModel.setQCData(rvpQualityCheck, sampleQuestion);
                } else {
                    showError("Please Sync MDC & Try Again");
                }
            }
            inItListener();
            if ((sampleQuestion.getImageCaptureSettings().equalsIgnoreCase("O") || sampleQuestion.getImageCaptureSettings().equalsIgnoreCase("N")) && mQcCheckImageViewModel.getDataManager().getHideCamera()) {
                fragmentQcCheckImageBinding.imagelayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Logger.e(QcCheckImageFragment.class.getName(), e.getMessage());
            showError("Please Go Back And Try Again");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void getData(BaseFragment fragment) {
        try {
            boolean qcStatus = false;
            qcWizard.setQcName(mQcCheckImageViewModel.getQcName().get());
            qcWizard.setQcValue("NONE");
            qcWizard.setMatch("0");
            qcWizard.setRemarks(mQcCheckImageViewModel.inputData.get());
            if (Objects.requireNonNull(mQcCheckImageViewModel.checkStatus.get()).trim().equalsIgnoreCase("Yes")) {
                qcStatus = true;
                qcWizard.setMatch("1");
            }
            qcWizard.setQccheckcode(Objects.requireNonNull(mQcCheckImageViewModel.sampleQuestions.get()).getCode());
            rvpQcDataDetailsActivity.getFragmentData(qcStatus, qcWizard, fragment);

        } catch (Exception e) {
            Logger.e(QcCheckImageFragment.class.getName(), e.getMessage());
            showError(e.getMessage());
        }
    }

    @Override
    public void captureImage(String sync_status) {
        startCameraActivity("QC_" + Objects.requireNonNull(mQcCheckImageViewModel.sampleQuestions.get()).getCode(), Objects.requireNonNull(mQcCheckImageViewModel.rvpQualityCheck.get()).getAwbNo() + "_" + Constants.TEMP_DRSID + "_" + "QC_" + Objects.requireNonNull(mQcCheckImageViewModel.sampleQuestions.get()).getCode() + ".png");
    }

    private void startCameraActivity(String ImageCode, String imageName) {
        try {
            Intent intent = new Intent(requireActivity(), CameraActivity.class);
            intent.putExtra("EmpCode", mQcCheckImageViewModel.getDataManager().getEmp_code());
            intent.putExtra("Latitude", mQcCheckImageViewModel.getDataManager().getCurrentLatitude());
            intent.putExtra("Longitude", mQcCheckImageViewModel.getDataManager().getCurrentLongitude());
            intent.putExtra("ImageCode", ImageCode);
            intent.putExtra("imageName", imageName);
            intent.putExtra("awbNumber", "" + mQcCheckImageViewModel.rvpQualityCheck.get().getAwbNo());
            intent.putExtra("drs_id", String.valueOf(Constants.TEMP_DRSID));
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean imagePopupVisible = false;
    public static PopupWindow popupWindow;

    public void showImagePreview() {
        imagePopupVisible = true;
        isPreviewImageClicked = true;
        try {
            LayoutInflater layoutInflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View customView = layoutInflater.inflate(R.layout.image_preview_pager, null);
            ViewPager viewPager = customView.findViewById(R.id.viewpager);
            String urlData = rvpQualityCheck.getQcValue().replaceAll("\\s+", "");
            List<Integer> index = findWordUpgrade(urlData, "http");
            String qc_string = addCharacterIntoString(urlData, index);
            String[] split_images = qc_string.split(",#");
            viewPager.setAdapter(new PreviewImageAdapter(getActivity(), split_images));
            CircleIndicator indicator = customView.findViewById(R.id.indicator);
            indicator.setViewPager(viewPager);
            popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            popupWindow.showAtLocation(fragmentQcCheckImageBinding.imageViewPreview, Gravity.CENTER, 0, 0);
            customView.findViewById(R.id.closePopupBtn).setOnClickListener(v -> {
                imagePopupVisible = false;
                popupWindow.dismiss();
            });
        } catch (Exception e) {
            Logger.e(QcCheckImageFragment.class.getName(), e.getMessage());
            showError(e.getMessage());
        }
    }

    public List<Integer> findWordUpgrade(String textString, String word) {
        List<Integer> indexes = new ArrayList<>();
        String lowerCaseTextString = textString.toLowerCase();
        String lowerCaseWord = word.toLowerCase();
        int wordLength = 0;
        int index = 0;
        while (index != -1) {
            index = lowerCaseTextString.indexOf(lowerCaseWord, index + wordLength);
            if (index != -1) {
                indexes.add(index);
            }
            wordLength = word.length();
        }
        return indexes;
    }

    public String addCharacterIntoString(String original, List<Integer> indexes) {
        StringBuilder newString = new StringBuilder();
        for (int i = 0; i < original.length(); i++) {
            newString.append(original.charAt(i));
            for (int j = 1; j < indexes.size(); j++) {
                if (i == indexes.get(j) - 1) {
                    newString.append("#");
                }
            }
        }
        return newString.toString();
    }

    @Override
    public void showError(String e) {
        getBaseActivity().showSnackbar(e);
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


                    ((RvpQcDataDetailsActivity) requireActivity()).uploadImage(bitmap, imageUri, fragmentQcCheckImageBinding.imageViewCaptureImageCheckImage, imageName, imageCode);


                } else {
                    ((RvpQcDataDetailsActivity) requireActivity()).showSnackbar("Captured Image Data Is Empty");
                }
            } catch (Exception e) {
                ((RvpQcDataDetailsActivity) requireActivity()).showSnackbar("Captured Image Data Is Empty");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}