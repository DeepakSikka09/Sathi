package in.ecomexpress.sathi.ui.drs.forward.signature;

import static com.payphi.logisticsdk.PaymentOptionIntent.dialog;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.Constants.REQUEST_CODE_SCAN;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.nlscan.android.scan.ScanManager;
import com.nlscan.android.scan.ScanSettings;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivitySignatureBinding;
import in.ecomexpress.sathi.repo.local.data.activitiesdata.FWDActivitiesData;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.drs.forward.fill_awb.AwbPopupDialog;
import in.ecomexpress.sathi.ui.drs.forward.fill_awb.MyDialogCloseListener;
import in.ecomexpress.sathi.ui.drs.forward.success.FWDSuccessActivity;
import in.ecomexpress.sathi.utils.BitmapUtils;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.Helper;
import in.ecomexpress.sathi.utils.ImageHandler;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.NetworkUtils;

@AndroidEntryPoint
public class SignatureActivity extends BaseActivity<ActivitySignatureBinding, SignatureViewModel> implements ISignatureNavigator, MyDialogCloseListener {

    private final String TAG = SignatureActivity.class.getSimpleName();
    @Inject
    SignatureViewModel signatureViewModel;
    ActivitySignatureBinding mActivitySignatureBinding;
    Boolean scannedStatus = false;
    private boolean isScannerPaused = false;
    ImageHandler imageHandler;

    private boolean CAPTURE_IMAGE = false;
    ForwardCommit forwardCommit;
    Bitmap emptyBitmap, well;
    Boolean isImageCaptured = false;

    public static int imageCaptureCount = 0;
    public static int secondimageCaptureCount = 0;
    String change = "";
    String payType = null, composite_key = "";
    String getCollectedValue = null;
    ImageView mimageView;
    Bitmap mbitmap;
    String awb_no;
    String order_id = "";
    String imageFileName = "";
    private ScanManager mScanMgr;
    private DRSForwardTypeResponse drsForwardTypeResponse;
    private int meterRange = 0;
    private boolean consigneeProfiling;
    AwbPopupDialog awbPopupDialog;
    int counter_scan = 0;
    MyDialogCloseListener myDialogCloseListener;
    boolean sign_image_required;
    String fwd_del_image = "";
    String return_package_barcode = "";
    String ScanValue = "";

    public static Intent getStartIntent(Context context) {
        return new Intent(context, SignatureActivity.class);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivitySignatureBinding = getViewDataBinding();
        logScreenNameInGoogleAnalytics(TAG, this);
        mActivitySignatureBinding.signature.setContext(SignatureActivity.this);
        mActivitySignatureBinding.etEmail.setEnabled(false);
        signatureViewModel.getDataManager().setLoginPermission(false);
        mActivitySignatureBinding.imageScan.setImageResource(R.drawable.scan);
        signatureViewModel.setNavigator(this);
        myDialogCloseListener = this;
        Constants.LOCATION_ACCURACY = signatureViewModel.getDataManager().getUndeliverConsigneeRANGE();
        if (signatureViewModel.getIsAwbScan()) {
            signatureViewModel.getIsAwbScan();
        }
        if (signatureViewModel.getDataManager().getIsSignatureImageMandatory().equalsIgnoreCase("true")) {
            mActivitySignatureBinding.llSignatureText.setVisibility(View.VISIBLE);
            mActivitySignatureBinding.llSignaturePad.setVisibility(View.VISIBLE);
            mActivitySignatureBinding.tvDeclare.setVisibility(View.VISIBLE);
            mActivitySignatureBinding.llWatemarkText.setVisibility(View.GONE);
        } else {
            mActivitySignatureBinding.llWatemarkText.setVisibility(View.VISIBLE);
            mActivitySignatureBinding.llSignatureText.setVisibility(View.GONE);
            mActivitySignatureBinding.llSignaturePad.setVisibility(View.GONE);
            mActivitySignatureBinding.tvDeclare.setVisibility(View.GONE);
            mActivitySignatureBinding.tvText.setTextColor(getResources().getColor(R.color.black));
            mActivitySignatureBinding.tvDeclare.setTypeface(mActivitySignatureBinding.tvDeclare.getTypeface(), Typeface.BOLD);
            mActivitySignatureBinding.tvText.setText("No need to take signature at this activity.");
            mActivitySignatureBinding.tvText.setTextSize(15f);
        }
        try {

            FWDActivitiesData fwdActivitiesData = getIntent().getParcelableExtra("fwdActivitiesData");
            if (fwdActivitiesData != null) {
                try {
                    awb_no = String.valueOf(fwdActivitiesData.getAwbNo());
                    if (fwdActivitiesData.getScanValue() == null) {
                        ScanValue = "";
                    } else {
                        ScanValue = fwdActivitiesData.getScanValue();
                    }
                    if (fwdActivitiesData.getReturn_package_barcode() == null) {
                        return_package_barcode = "";
                    } else {
                        return_package_barcode = fwdActivitiesData.getReturn_package_barcode();
                    }
                    order_id = fwdActivitiesData.getOrderId();
                    change = fwdActivitiesData.getChange();
                    payType = fwdActivitiesData.getType();
                    sign_image_required = fwdActivitiesData.isSign_image_required();
                    if (fwdActivitiesData.getFwd_del_image() == null) {
                        fwd_del_image = "";
                    } else {
                        fwd_del_image = fwdActivitiesData.getFwd_del_image();
                    }

                    composite_key = fwdActivitiesData.getCompositeKey();
                    getCollectedValue = fwdActivitiesData.getAmount();
                } catch (Exception e) {
                    Logger.e("SignatureActivity", e.getMessage());

                }
            }

            forwardCommit = getIntent().getParcelableExtra("data");
            forwardCommit.setAwb(awb_no);

            if (!change.equalsIgnoreCase("0.0") && payType.equalsIgnoreCase("COD")) {
                mActivitySignatureBinding.changeText.setText("Did you received your change? \u20B9 " + change);
                mActivitySignatureBinding.changeLayout.setVisibility(View.GONE);
            } else {
                mActivitySignatureBinding.changeLayout.setVisibility(View.GONE);
            }


            if (fwd_del_image.equalsIgnoreCase("true")) {
                mActivitySignatureBinding.capimage2.setVisibility(View.VISIBLE);
                mActivitySignatureBinding.txtMandatory.setText(" 1*");
                signatureViewModel.setFwdDelImageRequired(true);
                signatureViewModel.setImageRequired(true);

            } else {
                mActivitySignatureBinding.capimage2.setVisibility(View.GONE);
                signatureViewModel.setFwdDelImageRequired(false);
                signatureViewModel.setImageRequired(sign_image_required);
                if (sign_image_required) {
                    mActivitySignatureBinding.txtMandatory.setVisibility(View.VISIBLE);
                    mActivitySignatureBinding.txtMandatory.setText("*");
                } else {
                    mActivitySignatureBinding.txtMandatory.setVisibility(View.GONE);
                }
            }

            signatureViewModel.onForwardDRSCommit(forwardCommit);
            imageHandler = new ImageHandler(this) {
                @Override
                public void onBitmapReceived(final Bitmap bitmap, final String imageUri, final ImageView imageView, String imageName, String imageCode, int pos, boolean verifyImage) {
                    // Blur Image Recognition Using Laplacian Variance:-
                    runOnUiThread(() -> {
                        try {
                            if (imageCode.equalsIgnoreCase("FWD_Delivered_Image_1")) {
                                if (!NetworkUtils.isNetworkConnected(SignatureActivity.this)) {
                                    showError(getString(R.string.check_internet));
                                    return;
                                }
                                if (CommonUtils.checkImageIsBlurryOrNot(SignatureActivity.this, "FWD", bitmap, imageCaptureCount, signatureViewModel.getDataManager())) {
                                    imageCaptureCount++;
                                } else {
                                    if (imageView != null) {
                                        isImageCaptured = true;
                                        signatureViewModel.setImageCaptured(true);
                                        mimageView = imageView;
                                        mbitmap = bitmap;
                                        imageFileName = imageUri;
                                        signatureViewModel.uploadImageServer(forwardCommit.getAwb() + "_" + forwardCommit.getDrs_id() + "_FWD_Delivered_Image_1.png", imageUri, imageCode, Long.parseLong(forwardCommit.getAwb()), Integer.parseInt(forwardCommit.getDrs_id()), "", bitmap, composite_key, false);
                                    }
                                }
                            } else if (imageCode.equalsIgnoreCase("FWD_Delivered_Image_2")) {
                                if (!NetworkUtils.isNetworkConnected(SignatureActivity.this)) {
                                    showError(getString(R.string.check_internet));
                                }
                                if (CommonUtils.checkImageIsBlurryOrNot(SignatureActivity.this, "FWD", bitmap, secondimageCaptureCount, signatureViewModel.getDataManager())) {
                                    secondimageCaptureCount++;
                                } else {
                                    if (imageView != null) {
                                        isImageCaptured = true;
                                        signatureViewModel.setFwdDelImageCaptured(true);
                                        mimageView = imageView;
                                        mbitmap = bitmap;
                                        imageFileName = imageUri;
                                        signatureViewModel.uploadImageServer(forwardCommit.getAwb() + "_" + forwardCommit.getDrs_id() + "_FWD_Delivered_Image_2.png", imageUri, imageCode, Long.parseLong(forwardCommit.getAwb()), Integer.parseInt(forwardCommit.getDrs_id()), "", bitmap, composite_key, false);
                                    }
                                }
                            }

                        } catch (Exception e) {
                            Toast.makeText(SignatureActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            };

            mActivitySignatureBinding.rgDeliveryPlace.setOnCheckedChangeListener((radioGroup, i) -> {
                int radioButtonID = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = radioGroup.findViewById(radioButtonID);
                String add = (String) radioButton.getText();
                signatureViewModel.setDeliveryAddress(add);
            });
            signatureViewModel.fetchForwardShipment(forwardCommit.getDrs_id(), forwardCommit.getAwb());
            signatureViewModel.getConsigneeProfiling();
        } catch (Exception e) {
            Logger.e(SignatureActivity.class.getName(), e.getMessage());
        }
        mActivitySignatureBinding.scrollView.setFillViewport(true);
        mActivitySignatureBinding.ivFlash.setOnClickListener(view -> switchFlashlight());
        try {
            mActivitySignatureBinding.ivBarcode.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory());
            mActivitySignatureBinding.ivBarcode.initializeFromIntent(getIntent());
            mActivitySignatureBinding.ivBarcode.decodeContinuous(callback);
            final Handler handler = new Handler();
            handler.postDelayed(this::pauseScanner, 1000);
        } catch (Exception e) {
            Logger.e(SignatureActivity.class.getName(), e.getMessage());
        }

        mActivitySignatureBinding.header.awb.setText(R.string.fwd_signature);
        mActivitySignatureBinding.header.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackClick();
            }
        });
    }

    @Override
    public String getReceiverName() {
        return mActivitySignatureBinding.etEmail.getText().toString();
    }

    @Override
    public void onDRSForwardItemFetch(DRSForwardTypeResponse drsForwardTypeResponse) {
        this.drsForwardTypeResponse = drsForwardTypeResponse;
        signatureViewModel.checkMeterRange(drsForwardTypeResponse);
    }

    @Override
    public void setConsigneeDistance(int meter) {
        this.meterRange = meter;
    }

    @Override
    public void setConsingeeProfiling(boolean enable) {
        this.consigneeProfiling = enable;
    }

    @Override
    public void saveCommit() {
        forwardCommit.setChange_received_confirmation("Y");
        forwardCommit.setOfd_customer_otp(Constants.PLAIN_OTP);
        forwardCommit.setOfd_otp_verified(String.valueOf(Constants.OFD_OTP_VERIFIED));
        forwardCommit.setScanable_by(ScanValue);
        if (return_package_barcode == null || return_package_barcode.equals("")) {
            forwardCommit.setReturn_packaging_barcode("");
        } else {
            forwardCommit.setReturn_packaging_barcode(return_package_barcode.replaceAll("[^a-zA-Z0-9]", ""));
        }
        forwardCommit.setCall_attempt_count(signatureViewModel.getDataManager().getForwardCallCount(awb_no + "FWD"));
        forwardCommit.setMap_activity_count(signatureViewModel.getDataManager().getForwardMapCount(Long.parseLong(awb_no)));

        if (mActivitySignatureBinding.changeLayout.getVisibility() == View.VISIBLE) {
            if (!mActivitySignatureBinding.change.isChecked()) {
                showError("Have you received your change? Please verify...");
                return;
            }
            forwardCommit.setChange_received_confirmation("Y");
        }
        signatureViewModel.getInfo(payType, Float.parseFloat(getCollectedValue));
    }

    @Override
    public void callCommit(String image_id, String image_key, String composite_key) {
        signatureViewModel.createCommitPacket(forwardCommit, image_id, image_key, composite_key);
    }

    @Override
    public void onHandleError(String error) {
        try {
            showSnackbar(error);
        } catch (Exception e) {
            Logger.e(SignatureActivity.class.getName(), e.getMessage());
        }
    }

    @Override
    public void setBitmap() {
        mimageView.setImageBitmap(mbitmap);
    }

    @Override
    public void setCommitOffline(String imageUri) {
        try {
            if (imageUri != null)
                signatureViewModel.uploadAWSImage(imageUri, "signature", forwardCommit.getAwb() + "_" + forwardCommit.getDrs_id() + "_signature.png", -1, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO, true, composite_key, true);
            else {
                signatureViewModel.uploadAWSImage(imageFileName, "signature", forwardCommit.getAwb() + "_" + forwardCommit.getDrs_id() + "_signature.png", -1, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO, true, composite_key, true);
            }
        } catch (Exception e) {
            Logger.e(SignatureActivity.class.getName(), e.getMessage());
            signatureViewModel.uploadAWSImage(null, "signature", forwardCommit.getAwb() + "_" + forwardCommit.getDrs_id() + "_signature.png", -1, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO, true, composite_key, true);
        }
    }

    @Override
    public void scanAwb() {
        if (SignatureViewModel.device.equals(Constants.NEWLAND) || SignatureViewModel.device.equals(Constants.NEWLAND_90) || SignatureViewModel.device.equalsIgnoreCase(Constants.NEWLAND_DROI)) {
            mScanMgr = ScanManager.getInstance();
            mScanMgr.startScan();
            mScanMgr.enableBeep();
            mScanMgr.setOutpuMode(ScanSettings.Global.VALUE_OUT_PUT_MODE_BROADCAST);
            IntentFilter intFilter = new IntentFilter(ScanManager.ACTION_SEND_SCAN_RESULT);
            registerReceiver(signatureViewModel.mResultReceiver(), intFilter);
        }
    }

    @Override
    public void mResultReceiver1(String strScancode) {
    }

    @Override
    public boolean getScanedResult() {
        return scannedStatus;
    }


    @Override
    public String getotp() {
        return mActivitySignatureBinding.otpEt.getText().toString();
    }

    @Override
    public void switchLayoutGone() {
        mActivitySignatureBinding.otpLayout.setVisibility(View.GONE);
    }

    @Override
    public void showScanAlert() {}

    @Override
    public void dismissDialog() {}

    @Override
    public void onSubmitBPClick() {

    }

    @Override
    public void showerrorMessage(String error) {

    }

    @Override
    public SignatureViewModel getViewModel() {
        return signatureViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_signature;
    }

    @Override
    public void enableEditText(boolean b) {
        mActivitySignatureBinding.etEmail.setEnabled(b);
    }

    @Override
    public void hideEdit(String select) {
        if (select.equalsIgnoreCase("Select"))
            mActivitySignatureBinding.etEmail.setVisibility(View.GONE);
        else {
            mActivitySignatureBinding.etEmail.setVisibility(View.VISIBLE);
        }
    }

    /**
     * for capturing the image
     */
    @Override
    public void onCaptureImage() {
        if (scannedStatus) {
            try {
                if (!isNetworkConnected()) {
                    showError(getString(R.string.check_internet));
                    return;
                }
                CAPTURE_IMAGE = !CAPTURE_IMAGE;
                AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_Material3_Light_Dialog_Alert);
                String AlertText1 = "Attention : ";
                builder.setMessage(AlertText1 + getString(R.string.alert)).setCancelable(false).setPositiveButton("OK", (dialog, id) -> imageHandler.captureImage(forwardCommit.getAwb() + "_" + forwardCommit.getDrs_id() + "_FWD_Delivered_Image_1.png", mActivitySignatureBinding.image, "FWD_Delivered_Image_1"));
                AlertDialog alert = builder.create();
                alert.show();
            } catch (Exception e) {
                Logger.e(SignatureActivity.class.getName(), e.getMessage());
                showError(e.getMessage());
            }
        } else {
            showSnackbar("Please scan bar code first.");
        }
    }

    @Override
    public void onCaptureImage2() {
        if (scannedStatus) {
            try {
                if (!isNetworkConnected()) {
                    showError(getString(R.string.check_internet));
                    return;
                }
                CAPTURE_IMAGE = !CAPTURE_IMAGE;
                AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_Material3_Light_Dialog_Alert);
                String AlertText1 = "Attention : ";
                builder.setMessage(AlertText1 + getString(R.string.alert))
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, id) -> imageHandler.captureImage(forwardCommit.getAwb() + "_" + forwardCommit.getDrs_id() + "_FWD_Delivered_Image_2.png", mActivitySignatureBinding.imagecap2, "FWD_Delivered_Image_2"));
                AlertDialog alert = builder.create();
                alert.show();
            } catch (Exception e) {
                Logger.e(SignatureActivity.class.getName(), e.getMessage());
                showError(e.getMessage());
            }
        } else {
            showSnackbar("Please scan bar code first.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check permission granted:-
        if (!CommonUtils.isAllPermissionAllow(this)) {
            openSettingActivity();
            return;
        }
        try {
            mActivitySignatureBinding.ivBarcode.resume();
        } catch (Exception e) {
            Logger.e(SignatureActivity.class.getName(), e.getMessage());
        }
        if (signatureViewModel.getIsAwbScan()) {
            if (!scannedStatus && signatureViewModel.switchGone()) {
                try {
                    mActivitySignatureBinding.ivBarcode.resume();
                    mActivitySignatureBinding.ivBarcode.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    Logger.e(SignatureActivity.class.getName(), e.getMessage());
                }
            }
            signatureViewModel.getIsAwbScan();
            if (SignatureViewModel.device.equals(Constants.NEWLAND)) {
                mScanMgr = ScanManager.getInstance();
                mScanMgr.startScan();
                mScanMgr.enableBeep();
                mScanMgr.setOutpuMode(ScanSettings.Global.VALUE_OUT_PUT_MODE_BROADCAST);
                IntentFilter intFilter = new IntentFilter(ScanManager.ACTION_SEND_SCAN_RESULT);
                registerReceiver(signatureViewModel.mResultReceiver(), intFilter);
            } else {
                try {
                    mActivitySignatureBinding.ivBarcode.resume();
                } catch (Exception e) {
                    Logger.e(SignatureActivity.class.getName(), e.getMessage());
                }
            }
        } else {
            mActivitySignatureBinding.ivBarcode.setVisibility(View.GONE);
        }
    }

    @Override
    public void saveSignature() {
        try {
            if (mActivitySignatureBinding.changeLayout.getVisibility() == View.VISIBLE) {
                if (!mActivitySignatureBinding.change.isChecked()) {
                    showError("Have you received your change? Please verify...");
                    return;
                }
            }
            forwardCommit.setChange_received_confirmation("Y");
            forwardCommit.setOfd_customer_otp(Constants.PLAIN_OTP);
            forwardCommit.setCall_attempt_count(signatureViewModel.getDataManager().getForwardCallCount(awb_no + "FWD"));
            forwardCommit.setMap_activity_count(signatureViewModel.getDataManager().getForwardMapCount(Long.parseLong(awb_no)));
            forwardCommit.setOfd_otp_verified(String.valueOf(Constants.OFD_OTP_VERIFIED));
            forwardCommit.setScanable_by(ScanValue);
            if (return_package_barcode == null || return_package_barcode.equals("")) {
                forwardCommit.setReturn_packaging_barcode("");
            } else {
                forwardCommit.setReturn_packaging_barcode(return_package_barcode.replaceAll("[^a-zA-Z0-9]", ""));
            }
            Calendar calendar = Calendar.getInstance();
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
            int mMonth = calendar.get(Calendar.MONTH) + 1;
            if (signatureViewModel.loginDate().equalsIgnoreCase(String.valueOf(mDay)) && signatureViewModel.getDataManager().getLoginMonth() == mMonth) {
                saveBitmapInFile();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_Material3_Light_Dialog_Alert);
                String AlertText1 = "Attention : ";
                builder.setMessage(AlertText1 + getString(R.string.commit_restriction_msg)).setCancelable(false).setPositiveButton("OK", (dialog, id) -> {
                    dialog.cancel();
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        } catch (Exception e) {
            Logger.e(SignatureActivity.class.getName(), e.getMessage());
            showError(e.getMessage());
        }
    }

    @Override
    public void showSuccessStatus(ForwardCommit forwardCommit) {
        openFWDSuccessActivity(forwardCommit);
    }

    @Override
    public void submitErrorAlert() {
        try {
            showSnackbar(getString(R.string.please_select_deliverd_to));
        } catch (Exception e) {
            Logger.e(SignatureActivity.class.getName(), e.getMessage());
            showError(e.getMessage());
        }
    }

    @Override
    public void showError(String msgStr) {
        try {
            showSnackbar(msgStr);
        } catch (Exception e) {
            Logger.e(SignatureActivity.class.getName(), e.getMessage());
            showError(e.getMessage());
        }
    }


    public void onBackClick() {
        super.onBackPressed();
        mActivitySignatureBinding.ivBarcode.setTorchOff();
        applyTransitionToBackFromActivity(this);
    }

    @Override
    public void onClear() {
        mActivitySignatureBinding.signature.clear();
    }

    @Override
    public String getCompositeKey() {
        if (composite_key != null && !composite_key.equalsIgnoreCase("")) {
            return composite_key;
        } else {
            return "";
        }
    }

    //on click of submit
    private void openFWDSuccessActivity(ForwardCommit forwardCommit) {
        try {
            FWDActivitiesData fwdActivitiesData = new FWDActivitiesData();
            fwdActivitiesData.setAwbNo(Long.parseLong(forwardCommit.getAwb()));
            fwdActivitiesData.setCompositeKey(composite_key);
            fwdActivitiesData.setDecideNext(Constants.SUCCESS);
            fwdActivitiesData.setReason("");
            Intent intent = FWDSuccessActivity.getStartIntent(this);
            intent.putExtra("fwdActivitiesData", fwdActivitiesData);
            Helper.updateLocationWithData(SignatureActivity.this, forwardCommit.getAwb(), forwardCommit.getStatus());
            startActivity(intent);
            applyTransitionToOpenActivity(this);
        } catch (Exception e) {
            Logger.e(SignatureActivity.class.getName(), e.getMessage());
            showError(e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (CAPTURE_IMAGE) {
                CAPTURE_IMAGE = false;
                imageHandler.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            Logger.e(SignatureActivity.class.getName(), e.getMessage());
            showSnackbar(e.getMessage());
        }
    }

    public void saveBitmapInFile() {
        try {
            File fileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/" + Constants.EcomExpress);
            if (!fileDir.exists()) fileDir.mkdirs();
            File file = new File(fileDir, forwardCommit.getAwb() + "_" + forwardCommit.getDrs_id() + "_signature.png");
            if (!file.exists()) file.createNewFile();
            FileOutputStream ostream = new FileOutputStream(file);
            well = mActivitySignatureBinding.signature.getSignatureBitmap();
            emptyBitmap = mActivitySignatureBinding.signature.getWhiteBackground(well);
            emptyBitmap = mActivitySignatureBinding.signature.getWhiteBackground(well);
            if (well.sameAs(emptyBitmap)) {
                showSnackbar("Please place signature.");
                ostream.close();
            } else {
                if (!signatureViewModel.getDataManager().isCounterDelivery() && signatureViewModel.getCounterDeliveryRange() < signatureViewModel.getDataManager().getDCRANGE()) {
                    showError("Shipment cannot be marked delivered within the DC");
                }
                if (Constants.CONSIGNEE_PROFILE) {
                    String dialog_message = getString(R.string.commitdialog);
                    String positiveButtonText = getString(R.string.yes);
                    if (consigneeProfiling && meterRange > signatureViewModel.getDataManager().getUndeliverConsigneeRANGE() && signatureViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("W")) {
                        dialog_message = "⚠️ You are not attempting the shipment at the consignee's location.\n\n"
                                + "📍 **Your Current Location:** " + signatureViewModel.getDataManager().getCurrentLatitude()
                                + ", " + signatureViewModel.getDataManager().getCurrentLongitude() + "\n"
                                + "**Distance from Consignee:** " + meterRange + " meters away.\n\n"
                                + "❓ Are you sure you want to commit?";
                        positiveButtonText = getString(R.string.yes);
                    } else if (Constants.CONSIGNEE_PROFILE && meterRange > signatureViewModel.getDataManager().getUndeliverConsigneeRANGE() && signatureViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("R")) {
                        dialog_message = "🚫 You are not allowed to commit this shipment as you are not at the consignee's location.\n\n"
                                + "📍 **Your Current Location:** " + signatureViewModel.getDataManager().getCurrentLatitude()
                                + ", " + signatureViewModel.getDataManager().getCurrentLongitude() + "\n"
                                + "**Distance from Consignee:** " + meterRange + " meters away.";
                        positiveButtonText = getString(R.string.ok);
                    } else {
                        forwardCommit.setLocation_verified(meterRange <= signatureViewModel.getDataManager().getUndeliverConsigneeRANGE());
                        BitmapUtils.saveBitmap(file, well);
                        try {
                            imageFileName = file.getAbsolutePath();
                            if (NetworkUtils.isNetworkConnected(SignatureActivity.this)) {
                                signatureViewModel.uploadImageServer(forwardCommit.getAwb() + "_" + forwardCommit.getDrs_id() + "_signature.png", file.getAbsolutePath(), "signature", Long.parseLong(forwardCommit.getAwb()), Integer.parseInt(forwardCommit.getDrs_id()), "", mActivitySignatureBinding.signature.getSignatureBitmap(), composite_key, true);
                            } else {
                                signatureViewModel.uploadAWSImage(file.getAbsolutePath(), "signature", forwardCommit.getAwb() + "_" + forwardCommit.getDrs_id() + "_signature.png", -1, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO, true, composite_key, true);
                            }
                        } catch (Exception e) {
                            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                            restApiErrorHandler.writeErrorLogs(0, e.getMessage());
                            Logger.e(SignatureActivity.class.getName(), e.getMessage());
                        }
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_Material3_Light_Dialog_Alert);
                    builder.setCancelable(false);
                    builder.setMessage(dialog_message);
                    forwardCommit.setLocation_verified(meterRange <= signatureViewModel.getDataManager().getUndeliverConsigneeRANGE());
                    builder.setPositiveButton(positiveButtonText, (dialog, which) -> {
                        if (signatureViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("W") || signatureViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("N")) {
                            BitmapUtils.saveBitmap(file, well);
                            try {
                                imageFileName = file.getAbsolutePath();
                                if (NetworkUtils.isNetworkConnected(SignatureActivity.this)) {
                                    signatureViewModel.uploadImageServer(forwardCommit.getAwb() + "_" + forwardCommit.getDrs_id() + "_signature.png", file.getAbsolutePath(), "signature", Long.parseLong(forwardCommit.getAwb()), Integer.parseInt(forwardCommit.getDrs_id()), "", mActivitySignatureBinding.signature.getSignatureBitmap(), composite_key, true);
                                } else {
                                    signatureViewModel.uploadAWSImage(file.getAbsolutePath(), "signature", forwardCommit.getAwb() + "_" + forwardCommit.getDrs_id() + "_signature.png", -1, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO, true, composite_key, false);
                                }
                            } catch (Exception e) {
                                RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                                restApiErrorHandler.writeErrorLogs(0, e.getMessage());
                                Logger.e(SignatureActivity.class.getName(), e.getMessage());
                            }
                        } else {
                            dialog.cancel();
                        }
                    });
                    if (!(consigneeProfiling && meterRange > signatureViewModel.getDataManager().getUndeliverConsigneeRANGE())) {
                        builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel());
                    }
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    if (signatureViewModel.getDataManager().isCounterDelivery() && signatureViewModel.getCounterDeliveryRange() < signatureViewModel.getDataManager().getDCRANGE()) {
                        forwardCommit.setLocation_verified(false);
                    } else {
                        forwardCommit.setLocation_verified(meterRange <= signatureViewModel.getDataManager().getUndeliverConsigneeRANGE());
                    }
                    BitmapUtils.saveBitmap(file, well);
                    try {
                        imageFileName = file.getAbsolutePath();
                        if (signatureViewModel.getDataManager().getofflineFwd()) {
                            signatureViewModel.uploadAWSImage(file.getAbsolutePath(), "signature", forwardCommit.getAwb() + "_" + forwardCommit.getDrs_id() + "_signature.png", -1, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO, true, composite_key, false);
                        } else if (NetworkUtils.isNetworkConnected(SignatureActivity.this)) {
                            signatureViewModel.uploadImageServer(forwardCommit.getAwb() + "_" + forwardCommit.getDrs_id() + "_signature.png", file.getAbsolutePath(), "signature", Long.parseLong(forwardCommit.getAwb()), Integer.parseInt(forwardCommit.getDrs_id()), "", mActivitySignatureBinding.signature.getSignatureBitmap(), composite_key, true);
                        } else {
                            signatureViewModel.uploadAWSImage(file.getAbsolutePath(), "signature", forwardCommit.getAwb() + "_" + forwardCommit.getDrs_id() + "_signature.png", -1, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO, true, composite_key, false);
                        }
                    } catch (Exception e) {
                        RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                        restApiErrorHandler.writeErrorLogs(0, e.getMessage());
                        Logger.e(SignatureActivity.class.getName(), e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            Logger.e(SignatureActivity.class.getName(), e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mActivitySignatureBinding.ivBarcode.setTorchOff();
        applyTransitionToBackFromActivity(this);
    }

    //to check flashlight is on or off
    public void switchFlashlight() {
        if (mActivitySignatureBinding.ivFlash.isSelected()) {
            mActivitySignatureBinding.ivBarcode.setTorchOff();
            mActivitySignatureBinding.ivFlash.setSelected(false);
            mActivitySignatureBinding.ivFlash.setImageResource(R.drawable.flashoff);
        } else {
            mActivitySignatureBinding.ivBarcode.setTorchOn();
            mActivitySignatureBinding.ivFlash.setSelected(true);
            mActivitySignatureBinding.ivFlash.setImageResource(R.drawable.flashon);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mActivitySignatureBinding.ivBarcode.pause();
            mActivitySignatureBinding.ivBarcode.setTorchOff();

        } catch (Exception e) {
            Logger.e(SignatureActivity.class.getName(), e.getMessage());
        }
    }

    @Override
    public void showHandleError(boolean status) {
        dialog.dismiss();
        if (status) showSnackbar(getString(R.string.http_500_msg));
        else showSnackbar(getString(R.string.server_down_msg));
    }

    /**
     * @param isAwbMatch-- this true/false value is used for checking the awb match or not
     */
    @Override
    public void setStatusOfAwb(boolean isAwbMatch) {
        if (isAwbMatch) {
            scannedStatus = true;
            showToast("AWB matched");
            mActivitySignatureBinding.imageScan.setImageResource(R.drawable.scan_tick);
        } else {
            scannedStatus = false;
        }
    }

    @Override
    public void setStatusOfAwb(boolean isAwbMatch, String manualBP) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mActivitySignatureBinding.ivBarcode.setTorchOff();
        } catch (Exception e) {
            Logger.e(SignatureActivity.class.getName(), e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //for getting awb scanned result
    private final BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(com.journeyapps.barcodescanner.BarcodeResult result) {
            try {
                runOnUiThread(() -> {
                    try {
                        if (isScannerPaused) {
                            return;
                        }
                        if (counter_scan < 4) {
                            if (result != null) {
                                if (result.getText().equalsIgnoreCase(awb_no) || result.getText().equalsIgnoreCase(order_id)) {
                                    scannedStatus = true;
                                    showToast("bar code matched");
                                    mActivitySignatureBinding.ivBarcode.setVisibility(View.GONE);
                                } else {
                                    scannedStatus = false;
                                    if (counter_scan >= 4) {
                                        awbPopupDialog = AwbPopupDialog.newInstance(SignatureActivity.this, awb_no);
                                        awbPopupDialog.show(getSupportFragmentManager());
                                        awbPopupDialog.setCancelable(false);
                                        awbPopupDialog.setListener(myDialogCloseListener);
                                        mActivitySignatureBinding.ivBarcode.setVisibility(View.GONE);
                                        counter_scan = 0;
                                    } else {
                                        signatureViewModel.doScanAgainAlert(SignatureActivity.this);
                                        counter_scan++;
                                    }
                                }
                            } else {
                                scannedStatus = false;
                                signatureViewModel.doScanAgainAlert(SignatureActivity.this);
                                counter_scan++;
                                if (counter_scan >= 4) {
                                    awbPopupDialog = AwbPopupDialog.newInstance(SignatureActivity.this, awb_no);
                                    awbPopupDialog.show(getSupportFragmentManager());
                                    awbPopupDialog.setListener(myDialogCloseListener);
                                    awbPopupDialog.setCancelable(false);
                                    mActivitySignatureBinding.ivBarcode.setVisibility(View.GONE);
                                    counter_scan = 0;
                                }
                            }
                        } else {
                            awbPopupDialog = AwbPopupDialog.newInstance(SignatureActivity.this, awb_no);
                            awbPopupDialog.show(getSupportFragmentManager());
                            awbPopupDialog.setListener(myDialogCloseListener);
                            awbPopupDialog.setCancelable(false);
                            mActivitySignatureBinding.ivBarcode.setVisibility(View.GONE);
                            counter_scan = 0;
                        }
                        pauseScanner();
                    } catch (Exception e) {
                        Logger.e(SignatureActivity.class.getName(), e.getMessage());
                        showSnackbar(e.getMessage());
                    }
                    mActivitySignatureBinding.ivBarcode.resume();

                });


            } catch (Exception e) {
                Logger.e(SignatureActivity.class.getName(), e.getMessage());
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    private void pauseScanner() {
        isScannerPaused = true;
        mActivitySignatureBinding.ivBarcode.pause();
        new Handler().postDelayed(this::resumeScanner, 1000);

    }

    // Resume Scanner:-
    private void resumeScanner() {
        isScannerPaused = false;
        mActivitySignatureBinding.ivBarcode.resume();
    }


}
