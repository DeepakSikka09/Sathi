package in.ecomexpress.sathi.ui.drs.forward.fill_awb;

import static in.ecomexpress.sathi.utils.CommonUtils.logButtonEventInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.CustomDialogMessageFwdBinding;
import in.ecomexpress.sathi.databinding.LayoutFillAwbBpidBinding;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.ui.base.BaseDialog;
import in.ecomexpress.sathi.ui.drs.forward.signature.ISignatureNavigator;
import in.ecomexpress.sathi.ui.drs.forward.signature.SignatureViewModel;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.ImageHandler;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.NetworkUtils;

@AndroidEntryPoint
public class AwbPopupBPIDDialog extends BaseDialog implements ISignatureNavigator {

    private final String TAG = AwbPopupBPIDDialog.class.getSimpleName();
    public static int imageCaptureCount = 0;
    static String awb_no = "";
    static String composite_key = "", drs_id_num = "";
    static String return_package_barcode = "";
    @SuppressLint("StaticFieldLeak")
    static Activity context;
    static ForwardCommit forwardCommit;
    LayoutFillAwbBpidBinding layoutFillAwbBinding;
    @Inject
    SignatureViewModel awbPopupDialogViewModel;
    MyDialogCloseListener myDialogCloseListener;
    ImageHandler imageHandler;
    Boolean isImageCaptured = false;
    ImageView mimageView;
    Bitmap mbitmap;
    String imageFileName = "";

    public static AwbPopupBPIDDialog newInstance(Activity getContext, String awb, String returnPackageBarcode, String compositeKey, String drsIdNum, ForwardCommit fwdCommit) {
        AwbPopupBPIDDialog fragment = new AwbPopupBPIDDialog();
        awb_no = awb;
        return_package_barcode = returnPackageBarcode;
        composite_key = compositeKey;
        drs_id_num = drsIdNum;
        forwardCommit = fwdCommit;
        context = getContext;
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void showerrorMessage(String error) {

    }

    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, "");
    }

    @Override
    public void dismissDialog() {
        dismissDialog("");
        layoutFillAwbBinding.popupElement.setVisibility(View.GONE);

    }

    @Override
    public void onSubmitBPClick() {
        if (layoutFillAwbBinding.edtNum.getText().length() == 0) {
            showDialog("Please Enter BP ID");
        } else if (!isImageCaptured && awbPopupDialogViewModel.getDataManager().getImageManulaFlyer()) {
            showDialog("Please capture the image");
        } else {
            logButtonEventInGoogleAnalytics(TAG, "ManualBPIDInputFwdOnSubmitBPClick", "ManuallyEnteredBPID " + layoutFillAwbBinding.edtNum.getText().toString(), context);
            if (layoutFillAwbBinding.edtNum.getText().toString().equalsIgnoreCase(return_package_barcode)) {
                myDialogCloseListener.setStatusOfAwb(true, layoutFillAwbBinding.edtNum.getText().toString());

                dismissDialog();
            } else {

                myDialogCloseListener.setStatusOfAwb(false, layoutFillAwbBinding.edtNum.getText().toString());

                dismissDialog();
            }
        }

    }

    @Override
    public void showHandleError(boolean status) {

    }

    @Override
    public void onCaptureImage() {

        try {

            AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.Theme_MaterialComponents_Light_Dialog_Alert);
            String AlertText1 = "Attention : ";
            builder.setMessage(AlertText1 + getString(R.string.alert)).setCancelable(false).setPositiveButton("OK", (dialog, id) -> imageHandler.captureImage(awb_no + "_" + drs_id_num + "_Mismatch_BP_Image.png", layoutFillAwbBinding.image, "Mismatch_BP_Image"));
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {

            Logger.e(AwbPopupBPIDDialog.class.getName(), e.getMessage());

        }

    }

    @Override
    public void onCaptureImage2() {

    }

    @Override
    public void saveSignature() {

    }

    @Override
    public void showSuccessStatus(ForwardCommit forwardCommit) {

    }

    @Override
    public void submitErrorAlert() {

    }

    @Override
    public void showError(String msgStr) {
        try {
            getBaseActivity().showSnackbar(msgStr);
        } catch (Exception e) {
            Logger.e(AwbPopupBPIDDialog.class.getName(), e.getMessage());
            showError(e.getMessage());
        }
    }


    @Override
    public void onClear() {

    }

    @Override
    public String getCompositeKey() {
        return null;
    }

    @Override
    public void enableEditText(boolean b) {

    }

    @Override
    public void hideEdit(String select) {

    }

    @Override
    public String getReceiverName() {
        return null;
    }

    @Override
    public void onDRSForwardItemFetch(DRSForwardTypeResponse drsForwardTypeResponse) {

    }

    @Override
    public void setConsigneeDistance(int meter) {

    }

    @Override
    public void setConsingeeProfiling(boolean enable) {

    }

    @Override
    public void saveCommit() {

    }

    @Override
    public void callCommit(String valueOf, String fileName, String compositeKey) {

    }

    @Override
    public void onHandleError(String error) {

    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutFillAwbBinding = DataBindingUtil.inflate(inflater, R.layout.layout_fill_awb_bpid, container, false);
        View view = layoutFillAwbBinding.getRoot();
        try {
            layoutFillAwbBinding.setViewModel(awbPopupDialogViewModel);
            awbPopupDialogViewModel.setNavigator(this);
            logScreenNameInGoogleAnalytics(TAG, context);


            Constants.LOCATION_ACCURACY = awbPopupDialogViewModel.getDataManager().getUndeliverConsigneeRANGE();
            if (awbPopupDialogViewModel.getIsAwbScan()) {
                awbPopupDialogViewModel.getIsAwbScan();
            }


            forwardCommit.setAwb(awb_no);
            awbPopupDialogViewModel.setImageRequired(false);
            awbPopupDialogViewModel.onForwardDRSCommit(forwardCommit);
            awbPopupDialogViewModel.fetchForwardShipment(drs_id_num, awb_no);
            awbPopupDialogViewModel.getConsigneeProfiling();
            awbPopupDialogViewModel.setDeliveryAddress("Office");


            imageHandler = new ImageHandler(getBaseActivity()) {
                @Override
                public void onBitmapReceived(final Bitmap bitmap, final String imageUri, final ImageView imageView, String imageName, String imageCode, int pos, boolean verifyImage) {
                    // Blur Image Recognition Using Laplacian Variance:-

                    context.runOnUiThread(() -> {

                        if (CommonUtils.checkImageIsBlurryOrNot(context, "FWD", bitmap, imageCaptureCount, awbPopupDialogViewModel.getDataManager())) {
                            imageCaptureCount++;
                        } else {
                            if (imageView != null) {
                                isImageCaptured = true;
                                awbPopupDialogViewModel.setImageCaptured(true);
                                mimageView = imageView;
                                mbitmap = bitmap;
                                imageFileName = imageUri;
                                if (NetworkUtils.isNetworkConnected(context)) {
                                    awbPopupDialogViewModel.uploadImageServer(awb_no + "_" + drs_id_num + "_Mismatch_BP_Image.png", imageUri, imageCode, Long.parseLong(awb_no), Integer.parseInt(drs_id_num), "", bitmap, composite_key, false);
                                } else {
                                    imageView.setImageBitmap(bitmap);
                                    awbPopupDialogViewModel.uploadAWSImage(imageUri, "Mismatch_BP_Image", awb_no + "_" + drs_id_num + "_Mismatch_BP_Image.png", -1, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO, false, composite_key, false);
                                }
                            }
                        }
                    });
                }
            };

        } catch (Exception e) {
            Logger.e(AwbPopupBPIDDialog.class.getName(), e.getMessage());
        }


        return view;
    }

    @Override
    public void setBitmap() {
        mimageView.setImageBitmap(mbitmap);
    }

    @Override
    public void setCommitOffline(String imageUri) {

    }

    @Override
    public void scanAwb() {

    }

    @Override
    public void mResultReceiver1(String strScancode) {

    }

    @Override
    public boolean getScanedResult() {
        return false;
    }


    @Override
    public String getotp() {
        return null;
    }

    @Override
    public void switchLayoutGone() {

    }

    @Override
    public void showScanAlert() {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (awbPopupDialogViewModel.getDataManager().getImageManulaFlyer()) {

            layoutFillAwbBinding.image.setVisibility(View.VISIBLE);
        } else {
            layoutFillAwbBinding.image.setVisibility(View.GONE);

        }
    }

    public void setListener(MyDialogCloseListener myDialogCloseListener) {
        this.myDialogCloseListener = myDialogCloseListener;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        try {
            {
                imageHandler.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            Logger.e(AwbPopupBPIDDialog.class.getName(), e.getMessage());

        }
    }

    public void showDialog(String message) {
        if (getContext() != null) {
            final Dialog dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            CustomDialogMessageFwdBinding dialogMessageBinding = CustomDialogMessageFwdBinding.inflate(LayoutInflater.from(getContext()));
            dialog.setContentView(dialogMessageBinding.getRoot());
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialogMessageBinding.tvStatus.setText(message);
            dialogMessageBinding.btn.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }
}
