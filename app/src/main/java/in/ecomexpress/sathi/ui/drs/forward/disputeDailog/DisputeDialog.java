package in.ecomexpress.sathi.ui.drs.forward.disputeDailog;

import static android.app.Activity.RESULT_OK;
import static in.ecomexpress.sathi.utils.CommonUtils.logButtonEventInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.Constants.CAMERA_SCANNER_CODE;
import static in.ecomexpress.sathi.utils.Constants.IMAGE_SCANNER_CODE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import com.intsig.imageprocessdemo.ImageScannerActivity;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.DisputeDialogBinding;
import in.ecomexpress.sathi.ui.base.BaseDialog;
import in.ecomexpress.sathi.ui.drs.forward.details.ForwardDetailViewModel;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.CryptoUtils;
import in.ecomexpress.sathi.utils.DigitalCropImageHandler;
import in.ecomexpress.sathi.utils.ImageHandler;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class DisputeDialog extends BaseDialog implements IDisputeDialogNavigator {

    private final String TAG = DisputeDialog.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    static Activity context;
    static ForwardDetailViewModel mforwardDetailViewModel;
    public ImageHandler imageHandler;
    public DigitalCropImageHandler digitalCropImageHandler;
    DisputeDialogBinding disputeDialogBinding;
    String mImageUri = "";
    String awb_no, drs_id;
    @Inject
    DisputeDialogViewModel disputeDialogViewModel;

    public static DisputeDialog newInstance(Activity getContext, String awb, String drs_id, ForwardDetailViewModel forwardDetailViewModel) {
        DisputeDialog fragment = new DisputeDialog();
        context = getContext;
        mforwardDetailViewModel = forwardDetailViewModel;
        Bundle bundle = new Bundle();
        bundle.putString("awb_no", awb);
        bundle.putString("drs_id", drs_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, "");
    }

    @Override
    public void checkValidation() {
        if (mImageUri.equalsIgnoreCase("")) {
            getBaseActivity().showSnackbar("Please capture image.");
        } else {
            disputeDialogViewModel.uploadDisputeImage(awb_no, drs_id, mImageUri);
        }
    }

    @Override
    public void dismissDialog() {
        dismissDialog("");
    }

    @Override
    public void showerrorMessage(String error) {
        try {
            if (error != null) {
                DisputeDialog.context.runOnUiThread(() -> {
                    DisputeDialog.context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    Toast.makeText(DisputeDialog.context, error, Toast.LENGTH_SHORT).show();
                });
            }
        } catch (Exception e) {
            Logger.e(DisputeDialog.class.getName(), e.getMessage());

            getBaseActivity().showSnackbar(e.getMessage());
        }
    }

    /*on capture image*/
    @Override
    public void captureImage() {
        logButtonEventInGoogleAnalytics(TAG, "DisputedPaymentCaptureImage", "", context);
        if (!CommonUtils.isAllPermissionAllow(getBaseActivity())) {
            openSettingActivity();
            return;
        }
        try {
            imageHandler.captureImage("dispute_" + disputeDialogViewModel.getDataManager().getEmp_code() + "_" + disputeDialogViewModel.getDataManager().getLocationCode() + "_" + System.currentTimeMillis() + ".png", disputeDialogBinding.imgDispute, "open1");
        } catch (Exception e) {
            Logger.e(DisputeDialog.class.getName(), e.getMessage());

        }
    }

    @Override
    public void onClickOtherNumber() {
        disputeDialogBinding.llOthernumber.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClickRegisteredNumber() {
        disputeDialogBinding.llOthernumber.setVisibility(View.GONE);
        disputeDialogViewModel.raiseDispute("", awb_no, drs_id);
    }

    @Override
    public void onOtpSubmitClick() {
        if (!disputeDialogBinding.edtNum.getText().toString().equalsIgnoreCase("")) {
            disputeDialogViewModel.raiseDispute(disputeDialogBinding.edtNum.getText().toString(), awb_no, drs_id);
        } else {
            getBaseActivity().showSnackbar("Please enter mobile number.");
        }
    }

    @Override
    public void onHandleError(String description) {
        getBaseActivity().showSnackbar(description);
    }

    @Override
    public void showSucess(String description) {
        getBaseActivity().showSnackbar(description);
    }

    @Override
    public void setSucessData() {
        disputeDialogBinding.llVerify.setVisibility(View.VISIBLE);
    }

    @Override
    public void onVerifyClick() {
        if (!disputeDialogBinding.edtOtp.getText().toString().equalsIgnoreCase("")) {
            disputeDialogViewModel.verifyDisputeOTP(disputeDialogBinding.edtOtp.getText().toString(), awb_no, drs_id);
        } else {
            getBaseActivity().showSnackbar("Please enter otp.");
        }
    }

    @Override
    public void closeDispute() {
        dismissDialog();
    }

    @Override
    public void openSignatureActivity() {
        mforwardDetailViewModel.openSignatureActivityDispute();
    }

    @Override
    public void showImageSuccess() {
        disputeDialogBinding.btnDispute.setVisibility(View.GONE);
        disputeDialogBinding.llDisputeOtp.setVisibility(View.GONE);
        disputeDialogViewModel.raiseDispute("", awb_no, drs_id);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                imageHandler.onActivityResult(requestCode, resultCode, data);
            }
            if (requestCode == IMAGE_SCANNER_CODE) {
                final String outputPath = data.getStringExtra(ImageScannerActivity.EXTRA_KEY_RESULT_DATA_PATH);
                CryptoUtils.encryptFile(outputPath, outputPath, Constants.ENC_DEC_KEY);
            } else if (requestCode == CAMERA_SCANNER_CODE) {
                String path = data.getStringExtra("croped_path");
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                CryptoUtils.encryptFile(path, path, Constants.ENC_DEC_KEY);
                digitalCropImageHandler.sendImage(bitmap, path);
            }
        } catch (Exception e) {
            Logger.e(DisputeDialog.class.getName(), e.getMessage());

            Toast.makeText(getBaseActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        disputeDialogBinding = DataBindingUtil.inflate(inflater, R.layout.dispute_dialog, container, false);
        View view = disputeDialogBinding.getRoot();
        try {

            disputeDialogBinding.setViewModel(disputeDialogViewModel);
            disputeDialogViewModel.setNavigator(this);
            logScreenNameInGoogleAnalytics(TAG, context);
            awb_no = getArguments().getString("awb_no");
            drs_id = getArguments().getString("drs_id");
            if (disputeDialogViewModel.getDataManager().getEDispute().equalsIgnoreCase("true")) {
                disputeDialogBinding.llPaymentImage.setVisibility(View.VISIBLE);
                disputeDialogBinding.btnDispute.setVisibility(View.VISIBLE);
            } else {
                disputeDialogBinding.llPaymentImage.setVisibility(View.GONE);
                disputeDialogBinding.btnDispute.setVisibility(View.GONE);
                disputeDialogViewModel.raiseDispute("", awb_no, drs_id);
            }
            disputeDialogBinding.radiogroup.setOnCheckedChangeListener((radioGroup, i) -> {

            });
            imageHandler = new ImageHandler(getBaseActivity()) {
                @Override
                public void onBitmapReceived(Bitmap bitmap, String imageUri, ImageView imgView, String imageName, String imageCode, int pos, boolean verifyImage) {
                    if (imgView != null) {
                        mImageUri = imageUri;
                        imgView.setImageBitmap(bitmap);
                    }
                }
            };
            digitalCropImageHandler = new DigitalCropImageHandler(getBaseActivity()) {
                @Override
                public void onBitmapReceived(Bitmap bitmap, ImageView imgView, String imageName, String imageCode, String imageUri, boolean verifyImage) {
                    if (imgView != null) {
                        mImageUri = imageUri;
                        imgView.setImageBitmap(bitmap);
                    }
                }
            };
        } catch (Exception e) {
            Logger.e(DisputeDialog.class.getName(), e.getMessage());
        }
        return view;
    }
}
