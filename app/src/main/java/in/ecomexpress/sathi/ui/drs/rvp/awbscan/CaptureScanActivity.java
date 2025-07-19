package in.ecomexpress.sathi.ui.drs.rvp.awbscan;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.barcodelistner.BarcodeResult;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityScanAwbBinding;
import in.ecomexpress.sathi.repo.local.data.rvp.RvpCommit;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.drs.rvp.signature.RVPSignatureActivity;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.cameraView.CameraActivity;

@AndroidEntryPoint
public class CaptureScanActivity extends BaseActivity<ActivityScanAwbBinding, CaptureScanViewModel> implements BarcodeResult, CaptureScanNavigator {

    private final String TAG = CaptureScanActivity.class.getSimpleName();
    public static int firstImageCaptureCount = 0;
    public static int secondImageCaptureCount = 0;
    public static int thirdImageCaptureCount = 0;
    private static final int CAMERA_REQUEST_CODE = 100;
    public String lastText = "";
    private static final long DEBOUNCE_DELAY = 2000;
    private long lastScanTime = 0;
    @Inject
    CaptureScanViewModel captureScanViewModel;
    @Inject
    RvpCommit rvpCommit;
    List<RvpCommit.QcWizard> qcWizards = null;
    static Boolean[] status = new Boolean[5];
    String awb = "";
    boolean t1 = false, t2 = false;
    DRSReverseQCTypeResponse drsReverseQCTypeResponse;
    String getDrsApiKey = null, getDrsPstKey = null, getDrsPin = null, composite_key = "";
    Long awbNo;
    boolean flag;
    private ActivityScanAwbBinding activityScanAwbBinding;
    private int layoutPosition = 0;
    private boolean imageCaptured = false;
    private ImageView captureImageView;
    Bitmap capturedImageBitmap;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, CaptureScanActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logScreenNameInGoogleAnalytics(TAG, this);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.rvp));
        try {
            captureScanViewModel.setNavigator(this);
            activityScanAwbBinding = getViewDataBinding();
            awbNo = getIntent().getLongExtra(Constants.INTENT_KEY, 0);
            flag = getIntent().getBooleanExtra("flag", false);
            getDrsApiKey = Objects.requireNonNull(getIntent().getExtras()).getString(Constants.DRS_API_KEY);
            getDrsPstKey = getIntent().getExtras().getString(Constants.DRS_PSTN_KEY);
            getDrsPin = getIntent().getExtras().getString(Constants.DRS_PIN);
            composite_key = getIntent().getExtras().getString(Constants.COMPOSITE_KEY);
            if (getIntent().getParcelableArrayListExtra("data") != null) {
                qcWizards = getIntent().getParcelableArrayListExtra("data");
            } else {
                qcWizards = null;
                activityScanAwbBinding.layoutafterimage.setVisibility(View.VISIBLE);
                activityScanAwbBinding.layoutaftersacan.setVisibility(View.VISIBLE);
            }
            awb = String.valueOf(getIntent().getExtras().getLong("awb", 0));
            drsReverseQCTypeResponse = getIntent().getParcelableExtra("rvp");
            rvpCommit.setDrsId(Objects.requireNonNull(drsReverseQCTypeResponse).getDrs().toString());
            rvpCommit.setAwb(drsReverseQCTypeResponse.getAwbNo().toString());
            activityScanAwbBinding.awb.setText(awb);
            activityScanAwbBinding.textViewBarcode1.setText(getString(R.string.scan_awb_bf_pkg));
            activityScanAwbBinding.textViewBarcode2.setText(getString(R.string.scan_awb_af_pkg));
            status[0] = false;
            status[1] = false;
            status[2] = false;
            status[3] = false;
            status[4] = false;

            //Show sample image in case of PhonePe
            captureScanViewModel.getPhonePeShipmentType(awb);
        } catch (Exception e) {
            printLogs(e.getMessage());
            showSnackbar(e.getMessage());
        }
        if (captureScanViewModel.getDataManager().getRVPRQCBarcodeScan().equalsIgnoreCase("True")) {
            activityScanAwbBinding.llBeforePkfScan.setVisibility(View.VISIBLE);
        } else {
            activityScanAwbBinding.llBeforePkfScan.setVisibility(View.GONE);
        }
        captureScanViewModel.initRvpCommitData(rvpCommit);
        activityScanAwbBinding.sampleImageAppCompatTextView.setOnClickListener(v -> openSampleScreen());
        activityScanAwbBinding.ivFlash.setOnClickListener(v -> switchFlashlight());
        activityScanAwbBinding.zxingBarcodeScanner.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory());
        activityScanAwbBinding.zxingBarcodeScanner.initializeFromIntent(getIntent());
        activityScanAwbBinding.zxingBarcodeScanner.decodeContinuous(callback);
        activityScanAwbBinding.imageViewBack.setOnClickListener(v -> showSnackbar("You Can Not Move Back"));
        captureScanViewModel.getImageSaveStatus().observe(this, isSaved -> {
            if (isSaved) {
                setOpenImages(captureImageView, capturedImageBitmap);
            } else {
                showSnackbar("Image Not Uploaded, Recapture Again");
            }
        });
    }

    private void openSampleScreen() {
        startActivity(new Intent(this, SamplePhonePeActivity.class));
        applyTransitionToOpenActivity(this);
    }

    private final BarcodeCallback callback = barcodeResult -> {
        try {
            if (barcodeResult.getText() != null && !barcodeResult.getText().equalsIgnoreCase(lastText)) {
                if (System.currentTimeMillis() - lastScanTime > DEBOUNCE_DELAY) {
                    String resultText = barcodeResult.getText();
                    if (layoutPosition == 1) {
                        if (resultText.equalsIgnoreCase(awb)) {
                            runOnUiThread(() -> {
                                activityScanAwbBinding.textViewBarcode1.setText(String.format("%s%s", getString(R.string.open_shipment_awb_is), resultText));
                                status[layoutPosition] = true;
                                t1 = false;
                                rvpCommit.setPackageBarcode(resultText);
                                captureScanViewModel.setScanCodeOpen(true);
                                activityScanAwbBinding.scannerFrame.setVisibility(View.GONE);
                                offFlashLight();
                            });
                        } else {
                            runOnUiThread(() -> {
                                showSnackbar(getString(R.string.awb_doesn_t_match));
                                status[layoutPosition] = false;
                                t2 = true;
                                captureScanViewModel.setScanCodeOpen(false);
                            });
                        }
                    }
                    if (layoutPosition == 2) {
                        if (resultText.equalsIgnoreCase(awb)) {
                            runOnUiThread(() -> {
                                activityScanAwbBinding.textViewBarcode1.setText(String.format("%s%s", getString(R.string.open_shipment_awb_is), resultText));
                                status[layoutPosition] = true;
                                t1 = false;
                                rvpCommit.setPackageBarcode(resultText);
                                captureScanViewModel.setScanCodeOpen(true);
                                activityScanAwbBinding.scannerFrame.setVisibility(View.GONE);
                                offFlashLight();
                            });
                        } else {
                            runOnUiThread(() -> {
                                showSnackbar("AWB Doesn't match");
                                status[layoutPosition] = false;
                                t2 = true;
                                captureScanViewModel.setScanCodeOpen(false);
                            });
                        }
                    } else if (layoutPosition == 4) {
                        runOnUiThread(() -> {
                            if (!CommonUtils.isValidPatterForRVPFlyerScan(resultText)) {
                                showSnackbar("Try Again With Another Flyer");
                                activityScanAwbBinding.scannerFrame.setVisibility(View.GONE);
                                offFlashLight();
                                return;
                            }
                            char firstCharacter = resultText.charAt(0);
                            if (Character.isLetter(firstCharacter)) {
                                String[] split = captureScanViewModel.getDataManager().getRVPAWBWords().split(",");
                                for (int i = 0; i < split.length; i++) {
                                    if (resultText.startsWith(split[i])) {
                                        activityScanAwbBinding.textViewBarcode2.setText(String.format("%s%s", getString(R.string.scanned_flyer_barcode_is), resultText));
                                        status[layoutPosition] = true;
                                        t2 = false;
                                        String cleanedString = resultText.replaceAll("[^a-zA-Z0-9]", "");
                                        rvpCommit.setRefPackageBarcode(cleanedString);
                                        captureScanViewModel.getRvpFlyerDuplicateCheck(rvpCommit.getRefPackageBarcode(), true);
                                        break;
                                    } else {
                                        if (i + 1 == split.length) {
                                            showSnackbar(getString(R.string.invalid_flyer_code_scan_again_or_another));
                                        }
                                    }
                                }
                            } else {
                                showSnackbar("This Is Not An Flyer Code");
                            }
                            activityScanAwbBinding.scannerFrame.setVisibility(View.GONE);
                            offFlashLight();
                        });
                    }
                    lastText = resultText;
                    lastScanTime = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            printLogs(e.getMessage());
            if (Objects.requireNonNull(e.getMessage()).contains(getString(R.string.only_the_original_thread)) || e.getMessage().contains(getString(R.string.can_touch_its_views))) {
                activityScanAwbBinding.textViewBarcode1.setText(String.format("%s%s", getString(R.string.open_shipment_awb_is), awb));
                status[layoutPosition] = true;
                t1 = false;
                rvpCommit.setPackageBarcode(awb);
                captureScanViewModel.setScanCodeOpen(true);
                activityScanAwbBinding.scannerFrame.setVisibility(View.GONE);
                offFlashLight();
            } else {
                showSnackbar(e.getMessage());
            }
        }
    };

    public void setOpenImages(ImageView imgView, Bitmap bitmap) {
        try {
            if (imgView != null) {
                imgView.setImageBitmap(bitmap);
                if (layoutPosition == 0) {
                    activityScanAwbBinding.txtbfpck.setText(R.string.imp);
                    captureScanViewModel.setImageOpen(true);
                }
                if (layoutPosition == 1) {
                    activityScanAwbBinding.txtbfpckTwo.setText(R.string.imp);
                    captureScanViewModel.setImageOpen_two(true);
                } else if (layoutPosition == 3) {
                    activityScanAwbBinding.txtafpck.setText(R.string.iap);
                    captureScanViewModel.setImageClose(true);
                }
                status[layoutPosition] = true;
            }
        } catch (Exception e) {
            showSnackbar("setOpenImages");
            Logger.e("setOpenImages", String.valueOf(e));
        }
    }

    @Override
    public CaptureScanViewModel getViewModel() {
        return captureScanViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_scan_awb;
    }

    @Override
    public void captureImageBeforePackaging() {
        if (!CommonUtils.isAllPermissionAllow(this)) {
            openSettingActivity();
            return;
        }
        if (!isNetworkConnected()) {
            showSnackbar(getString(R.string.no_network_error));
            return;
        }
        try {
            captureImageView = activityScanAwbBinding.imgCamBfPkg;
            startCameraActivity("open1", awb + "_" + drsReverseQCTypeResponse.getDrs() + "_open1.png");
            layoutPosition = 0;
            imageCaptured = !imageCaptured;
        } catch (Exception e) {
            printLogs(e.getMessage());
            Toast.makeText(CaptureScanActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void captureImageTwoBeforePackaging() {
        if (!CommonUtils.isAllPermissionAllow(this)) {
            openSettingActivity();
            return;
        }
        if (!isNetworkConnected()) {
            showSnackbar(getString(R.string.no_network_error));
            return;
        }
        try {
            layoutPosition = 1;
            imageCaptured = !imageCaptured;
            captureImageView = activityScanAwbBinding.imgCamBfPkgTwo;
            startCameraActivity("open1_2", awb + "_" + drsReverseQCTypeResponse.getDrs() + "_open1_2.png");
        } catch (Exception e) {
            printLogs(e.getMessage());
            Toast.makeText(CaptureScanActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void isPhonePayEnabled(String phonePeTag) {
        //Show sample image in case of PhonePe
        if (phonePeTag.equalsIgnoreCase("true")) {
            activityScanAwbBinding.sampleImageAppCompatTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void scanCodeBeforePackaging() {
        if (!CommonUtils.isAllPermissionAllow(this)) {
            openSettingActivity();
            return;
        }
        try {
            if (status[0]) {
                if (status[1]) {
                    layoutPosition = 2;
                    activityScanAwbBinding.zxingBarcodeScanner.decodeContinuous(callback);
                    activityScanAwbBinding.scannerFrame.setVisibility(View.VISIBLE);
                } else {
                    showSnackbar(getString(R.string.capture_second_image_bf_pkg));
                }
            } else {
                showSnackbar(getString(R.string.capture_image_bf_pkg));
            }
        } catch (Exception e) {
            Toast.makeText(CaptureScanActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void captureImageAfterPackaging() {
        if (!CommonUtils.isAllPermissionAllow(this)) {
            openSettingActivity();
            return;
        }
        if (!isNetworkConnected()) {
            showSnackbar(getString(R.string.no_network_error));
            return;
        }
        try {
            if (captureScanViewModel.getDataManager().getRVPRQCBarcodeScan().equalsIgnoreCase("True")) {
                if (status[1]) {
                    new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            printLogs(e.getMessage());
                        }
                    }).start();
                    try {
                        layoutPosition = 3;
                        imageCaptured = !imageCaptured;
                        captureImageView = activityScanAwbBinding.imgCamAfPkg;
                        startCameraActivity("open2", awb + "_" + rvpCommit.getDrsId() + "_open2.png");
                    } catch (Exception e) {
                        printLogs(e.getMessage());
                        showSnackbar(e.getMessage());
                    }
                } else {
                    showSnackbar(getString(R.string.scan_awb_af_pkg));
                }
            } else {
                try {
                    layoutPosition = 3;
                    imageCaptured = !imageCaptured;
                    captureImageView = activityScanAwbBinding.imgCamAfPkg;
                    startCameraActivity("open2", awb + "_" + rvpCommit.getDrsId() + "_open2.png");
                } catch (Exception e) {
                    printLogs(e.getMessage());
                    showSnackbar(e.getMessage());
                }
            }
        } catch (Exception e) {
            printLogs(e.getMessage());
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void scanCodeAfterPackaging() {
        if (!CommonUtils.isAllPermissionAllow(this)) {
            openSettingActivity();
            return;
        }
        try {
            if (status[3]) {
                layoutPosition = 4;
                activityScanAwbBinding.zxingBarcodeScanner.decodeContinuous(callback);
                activityScanAwbBinding.scannerFrame.setVisibility(View.VISIBLE);
            } else {
                showSnackbar(getString(R.string.capture_image_af_pkg));
            }
        } catch (Exception e) {
            printLogs(e.getMessage());
            Toast.makeText(CaptureScanActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSubmitSuccess() {
    }

    @Override
    public void onProceed() {
        if (qcWizards == null) {
            try {
                if (!status[0]) {
                    showSnackbar(getString(R.string.capture_first_image_bf_pkg));
                    return;
                }
                if (!status[1]) {
                    showSnackbar(getString(R.string.capture_second_image_bf_pkg));
                    return;
                }
                if (captureScanViewModel.getDataManager().getRVPRQCBarcodeScan().equalsIgnoreCase("True")) {
                    if (!status[2]) {
                        if (t1) {
                            showSnackbar(getString(R.string.scanmismatch));
                        } else {
                            showSnackbar(getString(R.string.scan_awb_af_pkg));
                        }
                    }
                }
                if (!status[3]) {
                    showSnackbar(getString(R.string.capture_image_af_pkg));
                    return;
                }
                if (!status[4]) {
                    if (t2) {
                        showSnackbar(getString(R.string.scanmismatch));
                    } else {
                        showSnackbar(getString(R.string.scan_awb_af_pkg));
                    }
                } else {
                    Intent intent = RVPSignatureActivity.getStartIntent(this);
                    if (qcWizards != null) {
                        intent.putParcelableArrayListExtra(getString(R.string.data), new ArrayList<>(qcWizards));
                    } else {
                        intent.putParcelableArrayListExtra(getString(R.string.data), null);
                    }
                    intent.putExtra("awb", awb);
                    intent.putExtra("rvpCommit", rvpCommit);
                    intent.putExtra("rvp", drsReverseQCTypeResponse);
                    intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                    intent.putExtra("navigation", "pass");
                    startActivity(intent);
                    applyTransitionToOpenActivity(this);
                }
            } catch (Exception e) {
                printLogs(e.getMessage());
                showSnackbar(e.getMessage());
            }
        } else {
            try {
                if (!status[0]) {
                    showSnackbar(getString(R.string.capture_first_image_bf_pkg));
                    return;
                }
                if (!status[1]) {
                    showSnackbar(getString(R.string.capture_second_image_bf_pkg));
                    return;
                }
                if (captureScanViewModel.getDataManager().getRVPRQCBarcodeScan().equalsIgnoreCase("True")) {
                    if (!status[2]) {
                        if (t1) {
                            showSnackbar(getString(R.string.scanmismatch));
                        } else {
                            showSnackbar(getString(R.string.scan_awb_af_pkg));
                        }
                    }
                }
                if (!status[3]) {
                    showSnackbar(getString(R.string.capture_image_af_pkg));
                    return;
                }
                if (!status[4]) {
                    if (t2) {
                        showSnackbar(getString(R.string.scanmismatch));
                    } else {
                        showSnackbar(getString(R.string.scan_awb_af_pkg));
                    }
                } else {
                    Intent intent = RVPSignatureActivity.getStartIntent(this);
                    if (qcWizards != null) {
                        intent.putParcelableArrayListExtra(getString(R.string.data), new ArrayList<>(qcWizards));
                    } else {
                        intent.putParcelableArrayListExtra(getString(R.string.data), null);
                    }
                    intent.putExtra("awb", awb);
                    intent.putExtra("rvpCommit", rvpCommit);
                    intent.putExtra("rvp", drsReverseQCTypeResponse);
                    intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                    intent.putExtra("navigation", "pass");
                    startActivity(intent);
                    applyTransitionToOpenActivity(this);
                }
            } catch (Exception e) {
                printLogs(e.getMessage());
                showSnackbar(e.getMessage());
            }
        }
    }

    public void switchFlashlight() {
        if (activityScanAwbBinding.ivFlash.isSelected()) {
            activityScanAwbBinding.zxingBarcodeScanner.setTorchOff();
            activityScanAwbBinding.ivFlash.setSelected(false);
            activityScanAwbBinding.ivFlash.setImageResource(R.drawable.flashoff);
        } else {
            activityScanAwbBinding.zxingBarcodeScanner.setTorchOn();
            activityScanAwbBinding.ivFlash.setSelected(true);
            activityScanAwbBinding.ivFlash.setImageResource(R.drawable.flashon);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showSnackbar("You Can Not Move Back");
    }

    @Override
    public void errorMsg(String message) {
        showSnackbar(message);
    }

    @Override
    public void mResultReceiver1(String strScancode) {
        if (strScancode != null) {
            try {
                if (status[0]) {
                    if (strScancode.equalsIgnoreCase(awb)) {
                        status[1] = true;
                        t1 = false;
                        captureScanViewModel.setScanCodeOpen(true);
                        rvpCommit.setPackageBarcode(strScancode);
                        activityScanAwbBinding.textViewBarcode1.setText(String.format("%s%s", getString(R.string.open_shipment_awb_is), strScancode));
                    } else {
                        if (status[2]) {
                            if (!strScancode.equalsIgnoreCase(awb)) {
                                String[] split = captureScanViewModel.getDataManager().getRVPAWBWords().split(",");
                                for (int i = 0; i < split.length; i++) {
                                    if (strScancode.contains(split[i])) {
                                        activityScanAwbBinding.textViewBarcode2.setText(String.format("%s%s", getString(R.string.scanned_flyer_barcode_is), strScancode));
                                        status[3] = true;
                                        t2 = false;
                                        rvpCommit.setRefPackageBarcode(strScancode);
                                        captureScanViewModel.getRvpFlyerDuplicateCheck(rvpCommit.getRefPackageBarcode(), true);
                                        break;
                                    } else {
                                        if (i + 1 == split.length) {
                                            showSnackbar("Invalid AWB");
                                        }
                                    }
                                }
                            } else {
                                showSnackbar("AWB Can't Be Same");
                                t2 = true;
                                status[3] = false;
                                captureScanViewModel.setScanCodeClose(false);
                            }
                        } else {
                            if (status[1]) {
                                if (activityScanAwbBinding.layoutafterimage.getVisibility() == View.VISIBLE) {
                                    showSnackbar(getString(R.string.capture_image_af_pkg));
                                    status[3] = false;
                                    t2 = true;
                                    captureScanViewModel.setScanCodeClose(false);
                                }
                            } else {
                                showSnackbar("AWB Doesn't Match");
                            }
                        }
                    }
                } else {
                    showSnackbar(getString(R.string.capture_image_bf_pkg));
                    status[1] = false;
                    t2 = true;
                    captureScanViewModel.setScanCodeOpen(false);
                }
            } catch (Exception e) {
                showSnackbar(e.getMessage());
            }
        } else {
            showSnackbar(getString(R.string.scan_awb_af_pkg));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            if (!isNetworkConnected()) {
                showSnackbar(getString(R.string.check_internet));
                return;
            }
            try {
                if (data != null) {
                    if (imageCaptured) {
                        imageCaptured = false;
                        String imagePathWithWaterMark = data.getStringExtra("imagePathWithWaterMark");
                        Bitmap bitmap = BitmapFactory.decodeFile(imagePathWithWaterMark);
                        String imageUri = data.getStringExtra("imageUri");
                        String imageCode = data.getStringExtra("imageCode");
                        String imageName = data.getStringExtra("imageName");
                        sendCapturedImageToServerAndSaveInDB(imageCode, imageUri, bitmap, imageName);
                        capturedImageBitmap = bitmap;
                    }
                } else {
                    showSnackbar("Captured Image Data Is Empty");
                }
            } catch (Exception e) {
                showSnackbar("Captured Image Data Is Empty");
            }
        }
    }

    private void sendCapturedImageToServerAndSaveInDB(String imageCode, String imageUri, Bitmap bitmap, String imageName) {
        try {
            if (imageCode.equalsIgnoreCase("open1")) {
                if (CommonUtils.checkImageIsBlurryOrNot(CaptureScanActivity.this, "RQC", bitmap, firstImageCaptureCount, captureScanViewModel.getDataManager()) || CommonUtils.checkImageIsBlurryOrNot(CaptureScanActivity.this, "RVP", bitmap, firstImageCaptureCount, captureScanViewModel.getDataManager())) {
                    firstImageCaptureCount++;
                } else {
                    captureScanViewModel.uploadImageServer(CaptureScanActivity.this, imageName, imageUri, imageCode, Long.parseLong(drsReverseQCTypeResponse.getAwbNo().toString()), Long.parseLong(drsReverseQCTypeResponse.getDrs().toString()));
                }
            } else if (imageCode.equalsIgnoreCase("open1_2")) {
                if (CommonUtils.checkImageIsBlurryOrNot(CaptureScanActivity.this, "RQC", bitmap, secondImageCaptureCount, captureScanViewModel.getDataManager()) || CommonUtils.checkImageIsBlurryOrNot(CaptureScanActivity.this, "RVP", bitmap, secondImageCaptureCount, captureScanViewModel.getDataManager())) {
                    secondImageCaptureCount++;
                } else {
                    captureScanViewModel.uploadImageServer(CaptureScanActivity.this, imageName, imageUri, imageCode, Long.parseLong(drsReverseQCTypeResponse.getAwbNo().toString()), Long.parseLong(drsReverseQCTypeResponse.getDrs().toString()));
                }
            } else if (imageCode.equalsIgnoreCase("open2")) {
                if (CommonUtils.checkImageIsBlurryOrNot(CaptureScanActivity.this, "RQC", bitmap, thirdImageCaptureCount, captureScanViewModel.getDataManager()) || CommonUtils.checkImageIsBlurryOrNot(CaptureScanActivity.this, "RVP", bitmap, thirdImageCaptureCount, captureScanViewModel.getDataManager())) {
                    thirdImageCaptureCount++;
                } else {
                    captureScanViewModel.uploadImageServer(CaptureScanActivity.this, imageName, imageUri, imageCode, Long.parseLong(drsReverseQCTypeResponse.getAwbNo().toString()), Long.parseLong(drsReverseQCTypeResponse.getDrs().toString()));
                }
            } else {
                captureScanViewModel.uploadImageServer(CaptureScanActivity.this, imageName, imageUri, imageCode, Long.parseLong(drsReverseQCTypeResponse.getAwbNo().toString()), Long.parseLong(drsReverseQCTypeResponse.getDrs().toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityScanAwbBinding.zxingBarcodeScanner.resume();
    }

    @Override
    public void onResult(String s) {
        try {
            if (status[0]) {
                if (s.equalsIgnoreCase(awb)) {
                    status[1] = true;
                    t1 = false;
                    captureScanViewModel.setScanCodeOpen(true);
                    rvpCommit.setPackageBarcode(s);
                    activityScanAwbBinding.textViewBarcode1.setText(String.format("%s%s", getString(R.string.scanned_flyer_barcode_is), s));
                } else {
                    if (status[2]) {
                        if (!s.equalsIgnoreCase(awb)) {
                            String[] split = captureScanViewModel.getDataManager().getRVPAWBWords().split(",");
                            for (int i = 0; i < split.length; i++) {
                                if (s.contains(split[i])) {
                                    activityScanAwbBinding.textViewBarcode2.setText(String.format("%s%s", getString(R.string.scanned_flyer_barcode_is), s));
                                    status[3] = true;
                                    t2 = false;
                                    rvpCommit.setRefPackageBarcode(s);
                                    captureScanViewModel.getRvpFlyerDuplicateCheck(rvpCommit.getRefPackageBarcode(), true);
                                    break;
                                } else {
                                    if (i + 1 == split.length) {
                                        showSnackbar("Invalid AWB");
                                    }
                                }
                            }
                        } else {
                            showSnackbar("AWB Can't Be Same");
                            t2 = true;
                            status[3] = false;
                            captureScanViewModel.setScanCodeClose(false);
                        }
                    } else {
                        if (status[1]) {
                            if (activityScanAwbBinding.layoutafterimage.getVisibility() == View.VISIBLE) {
                                showSnackbar(getString(R.string.capture_image_af_pkg));
                                status[3] = false;
                                t2 = true;
                                captureScanViewModel.setScanCodeClose(false);
                            }
                        } else {
                            showSnackbar("AWB Doesn't Match");
                        }
                    }
                }
            } else {
                showSnackbar(getString(R.string.capture_image_bf_pkg));
                status[1] = false;
                t2 = true;
                captureScanViewModel.setScanCodeOpen(false);
            }
        } catch (Exception e) {
            printLogs(e.getMessage());
            showSnackbar(e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            activityScanAwbBinding.zxingBarcodeScanner.pause();
            offFlashLight();
        } catch (Exception e) {
            printLogs(e.getMessage());
        }
    }

    private void offFlashLight() {
        activityScanAwbBinding.zxingBarcodeScanner.setTorchOff();
        activityScanAwbBinding.ivFlash.setSelected(false);
        activityScanAwbBinding.ivFlash.setImageResource(R.drawable.flashoff);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            offFlashLight();
        } catch (Exception e) {
            printLogs(e.getMessage());
        }
    }

    private void printLogs(String message) {
        Logger.e(CaptureScanActivity.class.getName(), message);
    }

    // Start Camera Activity And Send Data:-
    private void startCameraActivity(String ImageCode, String imageName) {
        try {
            Intent intent = new Intent(this, CameraActivity.class);
            intent.putExtra("EmpCode", captureScanViewModel.getDataManager().getEmp_code());
            intent.putExtra("Latitude", captureScanViewModel.getDataManager().getCurrentLatitude());
            intent.putExtra("Longitude", captureScanViewModel.getDataManager().getCurrentLongitude());
            intent.putExtra("ImageCode", ImageCode);
            intent.putExtra("imageName", imageName);
            intent.putExtra("awbNumber", awb);
            intent.putExtra("drs_id", drsReverseQCTypeResponse.getDrs().toString());
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}