package in.ecomexpress.sathi.ui.drs.rts.rts_scan_and_deliver;

import static in.ecomexpress.sathi.SathiApplication.rtsCapturedImage1;
import static in.ecomexpress.sathi.SathiApplication.rtsCapturedImage2;
import static in.ecomexpress.sathi.SathiApplication.shipmentImageMap;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logButtonEventInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.activity.OnBackPressedCallback;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.nlscan.android.scan.ScanManager;
import com.nlscan.android.scan.ScanSettings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.databinding.ActivityRtsScanBinding;
import in.ecomexpress.sathi.databinding.CustomDialogMessageBinding;
import in.ecomexpress.sathi.databinding.RtsFlyercodeBinding;
import in.ecomexpress.sathi.databinding.RtsTwoImagesBottomsheetBinding;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.ShipmentsDetail;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.drs.rts.interfaces.AdapterCheckBoxCallBack;
import in.ecomexpress.sathi.ui.drs.rts.interfaces.ClickListener;
import in.ecomexpress.sathi.ui.drs.rts.rts_main_list.RTSListActivity;
import in.ecomexpress.sathi.ui.drs.rts.rts_main_list.RTSShipmentListAdapter;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.ImageHandler;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class RTSScanActivity extends BaseActivity<ActivityRtsScanBinding, RTSScanActivityViewModel> implements AdapterCheckBoxCallBack, IRTSScanActivityNavigator, View.OnClickListener, ClickListener {

    @Inject
    RTSScanActivityViewModel rtsScanActivityViewModel;
    private final String TAG = RTSScanActivity.class.getSimpleName();
    ActivityRtsScanBinding activityRtsScanBinding;
    private ScanManager mScanMgr;
    private BroadcastReceiver mReceiver;
    private CaptureManager capture;
    ArrayList<ShipmentsDetail> shipmentsDetails_set = new ArrayList<>();
    String manuallyEnteredFlyerCode;
    private int flyerScanValueCounter = 0;
    private long rtsVWDetailID;
    @Inject
    RTSShipmentListAdapter rtsShipmentListAdapter;
    long shipment_awb_no;
    long imageClickedAwbNo;
    String deliveredImagesPosition = "";
    public static boolean hideCheckBox = false;
    public Map<Long, Boolean> damageFlyerImageCaptured = new HashMap<>();
    private static final HashMap<String, ShipmentsDetail> shipmentMap = new LinkedHashMap<>();
    public static String device = (Build.MANUFACTURER + ":" + Build.MODEL).toUpperCase(Locale.US);
    public ImageHandler imageHandler;
    public static int imageCaptureCount = 0;
    AdapterCheckBoxCallBack adapterCheckBoxCallBack;
    public String lastText = "";
    private static final long DEBOUNCE_DELAY = 2000;
    private long lastScanTime = 0;
    private Bitmap capturedImageBitmap;
    private ImageView capturedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rtsScanActivityViewModel.setNavigator(this);
        activityRtsScanBinding = getViewDataBinding();
        logScreenNameInGoogleAnalytics(TAG, this);
        initScanManager(savedInstanceState);
        try {
            adapterCheckBoxCallBack = this;
            try {
                ArrayList<ShipmentsDetail> receivedData = SathiApplication.shipmentsDetailsData;
                rtsVWDetailID = SathiApplication.rtsVWDetailID;
                fillShipmentMap(receivedData);
            } catch (Exception e) {
                onErrorMessage(e.getMessage());
            }
            rtsScanActivityViewModel.getVWDetails(rtsVWDetailID);
            getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    backPress();
                }
            });
            rtsShipmentListAdapter = new RTSShipmentListAdapter(new ArrayList<>(), false);
            activityRtsScanBinding.recyclerDRS.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            activityRtsScanBinding.recyclerDRS.setAdapter(rtsShipmentListAdapter);
            activityRtsScanBinding.saveAction.setOnClickListener(this);
            activityRtsScanBinding.ivFlash.setOnClickListener(this);
            // Perform task after successfully image uploaded.
            getViewModel().getImageUploadSuccessLiveData().observe(this, isSuccess -> {
                try{
                    if (isSuccess) {
                        capturedImageView.setImageBitmap(capturedImageBitmap);
                        shipmentImageMap.put(shipment_awb_no, capturedImageBitmap);
                        rtsShipmentListAdapter.updateDRS(shipmentImageMap);
                        getViewModel().updateImageCapturedFlag(shipment_awb_no, 1);
                        if(shipment_awb_no > 0){
                            damageFlyerImageCaptured.put(shipment_awb_no, true);
                        }
                        if(deliveredImagesPosition.equalsIgnoreCase("Image1") && imageClickedAwbNo > 0){
                            shipment_awb_no = imageClickedAwbNo;
                            rtsCapturedImage1.put(imageClickedAwbNo, true);
                        }
                        if(deliveredImagesPosition.equalsIgnoreCase("Image2") && imageClickedAwbNo > 0){
                            shipment_awb_no = imageClickedAwbNo;
                            rtsCapturedImage2.put(imageClickedAwbNo, true);
                        }
                    }
                } catch (Exception e){
                    showSnackbar(String.valueOf(e));
                }
            });

            imageHandler = new ImageHandler(RTSScanActivity.this) {
                @Override
                public void onBitmapReceived(Bitmap bitmap, String imageUri, ImageView imgView, String imageName, String imageCode, int position, boolean verifyImage) {
                    try {
                        if (CommonUtils.checkImageIsBlurryOrNot(RTSScanActivity.this, "RTS", bitmap, imageCaptureCount, rtsScanActivityViewModel.getDataManager())) {
                            imageCaptureCount++;
                        } else {
                            capturedImageBitmap = bitmap;
                            capturedImageView = imgView;
                            if (isNetworkConnected()) {
                                getViewModel().uploadImageOnServer(imageName, imageUri, imageCode, imageClickedAwbNo, 0, rtsVWDetailID, getString(R.string.ud_rts_image), RTSScanActivity.this);
                            } else {
                                getViewModel().saveImageDB(imageUri, imageCode, imageName, 0, rtsVWDetailID, imageClickedAwbNo, -1, getString(R.string.ud_rts_image), 0);
                            }
                        }
                    } catch (Exception e) {
                        showSnackbar(String.valueOf(e));
                    }
                }
            };
            rtsShipmentListAdapter.setClickListenerInstance(this);
        } catch (Exception e) {
            showSnackbar(String.valueOf(e));
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void initScanManager(Bundle savedInstanceState) {
        if (device.equalsIgnoreCase(Constants.NEWLAND) || device.equalsIgnoreCase(Constants.NEWLAND_90) || device.equalsIgnoreCase(Constants.NEWLAND_DROI)) {
            activityRtsScanBinding.zxingBarcodeScanner.setVisibility(View.GONE);
            mScanMgr = ScanManager.getInstance();
            mScanMgr.startScan();
            mScanMgr.disableBeep();
            mScanMgr.setScanEnable(true);
            mScanMgr.setOutpuMode(ScanSettings.Global.VALUE_OUT_PUT_MODE_BROADCAST);
            IntentFilter intFilter = new IntentFilter(ScanManager.ACTION_SEND_SCAN_RESULT);
            registerReceiver(mResultReceiver(), intFilter);
        } else {
            DecoratedBarcodeView barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
            capture = new CaptureManager(this, barcodeScannerView);
            capture.initializeFromIntent(getIntent(), savedInstanceState);
            barcodeScannerView.decodeContinuous(callback);
        }
    }

    private BroadcastReceiver mResultReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ScanManager.ACTION_SEND_SCAN_RESULT.equals(action)) {
                    String scannedValue = intent.getStringExtra("SCAN_BARCODE1");
                    if (scannedValue != null && !scannedValue.isEmpty() && !scannedValue.equalsIgnoreCase(lastText)) {
                        startScannerWork(scannedValue);
                        lastText = scannedValue;
                    }
                }
            }
        };
        return mReceiver;
    }

    private final BarcodeCallback callback = barcodeResult -> {
        try {
            if (barcodeResult.getText() != null && !barcodeResult.getText().equalsIgnoreCase(lastText)) {
                if (System.currentTimeMillis() - lastScanTime > DEBOUNCE_DELAY) {
                    startScannerWork(barcodeResult.getText());
                    lastText = barcodeResult.getText();
                    lastScanTime = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    };

    private void fillShipmentMap(List<ShipmentsDetail> commonDRSListItems) {
        for (int i = 0; i < commonDRSListItems.size(); i++) {
            commonDRSListItems.get(i).setIs_flyer_scanned(false);
            commonDRSListItems.get(i).setFlyer_code_manually_input("");
            shipmentMap.put(String.valueOf(commonDRSListItems.get(i).getAwbNo()), commonDRSListItems.get(i));
            if (!String.valueOf(commonDRSListItems.get(i).getParentAwbNo()).trim().equalsIgnoreCase("")) {
                shipmentMap.put(String.valueOf(commonDRSListItems.get(i).getParentAwbNo()), commonDRSListItems.get(i));
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();
        if (!(device.equalsIgnoreCase(Constants.NEWLAND) || device.equalsIgnoreCase(Constants.NEWLAND_90) || device.equalsIgnoreCase(Constants.NEWLAND_DROI))) {
            capture.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (device.equalsIgnoreCase(Constants.NEWLAND) || device.equalsIgnoreCase(Constants.NEWLAND_90) || device.equalsIgnoreCase(Constants.NEWLAND_DROI)) {
            mScanMgr.stopScan();
        } else {
            activityRtsScanBinding.zxingBarcodeScanner.setTorchOff();
            capture.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (device.equalsIgnoreCase(Constants.NEWLAND) || device.equalsIgnoreCase(Constants.NEWLAND_90) || device.equalsIgnoreCase(Constants.NEWLAND_DROI)) {
                mScanMgr.stopScan();
                unregisterReceiver(mReceiver);
            } else {
                activityRtsScanBinding.zxingBarcodeScanner.setTorchOff();
                capture.onDestroy();
            }
            SathiApplication.rtsVWDetailID = 0L;
            SathiApplication.shipmentsDetailsData.clear();
        } catch (Exception e) {
            onErrorMessage(e.getMessage());
        }
    }

    @Override
    public RTSScanActivityViewModel getViewModel() {
        return rtsScanActivityViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_rts_scan;
    }

    private void startScannerWork(String result) {
        if (shipmentMap.containsKey(result)) {
            ShipmentsDetail shipment = shipmentMap.get(result);
            if (shipment == null || shipment.getStatus() == null || !shipment.getStatus().equals(Constants.RTSASSIGNED)) {
                setErrorSound(getString(R.string.shipment_is_not_in_assigned_mode), true);
                return;
            }
            if(!shipmentsDetails_set.isEmpty()){
                if(shipmentsDetails_set.get(0).getStatus().equalsIgnoreCase(Constants.RTSDELIVERED) && CommonUtils.getRtsDeliveredImagesValue(shipmentsDetails_set.get(0).getFlagsMap()) && CommonUtils.capturedImageCount(shipmentsDetails_set.get(0).getAwbNo()) <= 1){
                    setErrorSound(getString(R.string.capture_both_images_to_proceed_further), true);
                    return;
                }
            }
            if (!shipmentsDetails_set.isEmpty()) {
                if (shipmentsDetails_set.get(0).isIs_flyer_scanned()) {
                    if (shipmentsDetails_set.contains(shipmentMap.get(result))) {
                        setErrorSound(getString(R.string.awb_already_exist), true);
                    } else {
                        addShipmentDetailToSet(Objects.requireNonNull(shipmentMap.get(result)));
                    }
                } else if (String.valueOf(shipmentsDetails_set.get(0).getAwbNo()).equalsIgnoreCase(result)) {
                    setErrorSound(getString(R.string.awb_already_exist), true);
                } else {
                    if (shipmentsDetails_set.get(0).getParentAwbNo().trim().equalsIgnoreCase("")) {
                        errorShowAndCounterIncrease(getString(R.string.it_s_not_flyer_code_scan_flyer_code));
                    } else {
                        errorShowAndCounterIncrease(getString(R.string.shipment_can_t_be_deliver_due_to_bp_id_mismatch));
                    }
                }
            } else {
                addShipmentDetailToSet(Objects.requireNonNull(shipmentMap.get(result)));
            }
        } else {
            if (shipmentsDetails_set != null && !shipmentsDetails_set.isEmpty() && !shipmentsDetails_set.get(0).isIs_flyer_scanned()) {
                // Changes as per JIRA SCAD2-21234.
                String returnPackageBarcode = shipmentsDetails_set.get(0).getReturn_package_barcode();
                returnPackageBarcode = (returnPackageBarcode == null) ? "" : returnPackageBarcode;
                String parentAwbNo = shipmentsDetails_set.get(0).getParentAwbNo();
                String latchingRequired = rtsScanActivityViewModel.latchingRequiredObservable.get();
                if (latchingRequired == null) {
                    latchingRequired = "";
                }
                if ((Objects.requireNonNull(latchingRequired).equalsIgnoreCase("Both") ||
                        (latchingRequired.equalsIgnoreCase("Flyer") && parentAwbNo.trim().equalsIgnoreCase("")) ||
                        (latchingRequired.equalsIgnoreCase("BP") && !parentAwbNo.trim().equalsIgnoreCase("")))) {
                    if (returnPackageBarcode.trim().equalsIgnoreCase("")) {
                        setErrorSound(getString(R.string.awb_does_not_exist), true);
                        return;
                    }
                    if (returnPackageBarcode.equalsIgnoreCase(result)) {
                        setShipmentDataAndNotify(result);
                    } else {
                        if (returnPackageBarcode.trim().equalsIgnoreCase("")) {
                            setErrorSound(getString(R.string.awb_does_not_exist), true);
                            return;
                        }
                        if (flyerScanValueCounter >= 3) {
                            showManuallyFlyerCodeDialog();
                        } else {
                            if (!parentAwbNo.trim().equalsIgnoreCase("") && !returnPackageBarcode.trim().equalsIgnoreCase("")) {
                                errorShowAndCounterIncrease(getString(R.string.shipment_can_t_be_deliver_due_to_bp_id_mismatch));
                            } else {
                                if (CommonUtils.containsSpecialCharacterOrBlank(result)) {
                                    errorShowAndCounterIncrease(getString(R.string.invalid_flyer_code_try_again));
                                } else if (!checkManualFlyerCodeRegex(result)) {
                                    errorShowAndCounterIncrease(getString(R.string.it_s_not_flyer_code_try_again));
                                } else {
                                    errorShowAndCounterIncrease(getString(R.string.invalid_flyer_code_try_again));
                                }
                            }
                        }
                    }
                } else {
                    setShipmentDataAndNotify(result);
                }
            } else {
                setErrorSound(getString(R.string.awb_does_not_exist), true);
            }
        }
    }

    public void errorShowAndCounterIncrease(String errorMessage) {
        flyerScanValueCounter++;
        setErrorSound(errorMessage, true);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setShipmentDataAndNotify(String result) {
        shipmentsDetails_set.get(0).setIs_flyer_scanned(true);
        shipmentsDetails_set.get(0).setEntered_return_package_barcode(result.replaceAll("[^a-zA-Z0-9]", ""));
        shipmentsDetails_set.get(0).setFlyer_code_manually_input("scan");
        rtsCapturedImage1.put(shipmentsDetails_set.get(0).getAwbNo(), false);
        rtsCapturedImage2.put(shipmentsDetails_set.get(0).getAwbNo(), false);
        onScannerSeqSaveClicked(shipmentsDetails_set);
        flyerScanValueCounter = 0;
        rtsShipmentListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        try {
            int viewId = view.getId();
            if (viewId == R.id.save_action) {
                handleSaveActionClick();
            } else if (viewId == R.id.iv_flash) {
                switchFlashlight();
            }
        } catch (Exception e) {
            onErrorMessage(e.getMessage());
        }
    }

    private void handleSaveActionClick() {
        try {
            logButtonEventInGoogleAnalytics(TAG, getString(R.string.handlesaveactionclick), "", this);
            if (!shipmentsDetails_set.isEmpty()) {
                if(shipmentsDetails_set.get(0).getStatus().equalsIgnoreCase(Constants.RTSDELIVERED) && CommonUtils.getRtsDeliveredImagesValue(shipmentsDetails_set.get(0).getFlagsMap()) && CommonUtils.capturedImageCount(shipmentsDetails_set.get(0).getAwbNo()) <= 1){
                    setErrorSound(getString(R.string.capture_both_images_to_proceed_further), true);
                    return;
                }
                if (shipmentsDetails_set.get(0).isIs_flyer_scanned()) {
                    damageFlyerImageCaptured.clear();
                    Intent intent = new Intent(RTSScanActivity.this, RTSListActivity.class);
                    startActivity(intent);
                    applyTransitionToOpenActivity(this);
                } else {
                    if (shipmentsDetails_set.get(0).getParentAwbNo().trim().equalsIgnoreCase("")) {
                        setErrorSound(getString(R.string.scan_rvp_flyer_barcode), true);
                    } else {
                        setErrorSound(getString(R.string.scan_branded_packaging_barcode), true);
                    }
                }
            } else {
                setErrorSound(getString(R.string.scan_shipment_barcode), true);
            }
        } catch (Exception e) {
            onErrorMessage(e.getMessage());
        }
    }

    public void switchFlashlight() {
        logButtonEventInGoogleAnalytics(TAG, getString(R.string.switchflashlightrts), "", this);
        if (activityRtsScanBinding.ivFlash.isSelected()) {
            activityRtsScanBinding.zxingBarcodeScanner.setTorchOff();
            activityRtsScanBinding.ivFlash.setSelected(false);
            activityRtsScanBinding.ivFlash.setImageResource(R.drawable.flashoff);
        } else {
            activityRtsScanBinding.zxingBarcodeScanner.setTorchOn();
            activityRtsScanBinding.ivFlash.setSelected(true);
            activityRtsScanBinding.ivFlash.setImageResource(R.drawable.flashon);
        }
    }

    private void addShipmentDetailToSet(ShipmentsDetail commonDRSListItems) {
        hideCheckBox = true;
        try {
            if (commonDRSListItems.getReturn_package_barcode() == null) {
                commonDRSListItems.setReturn_package_barcode("");
            }
            if (!commonDRSListItems.getReturn_package_barcode().trim().equalsIgnoreCase("")) {
                commonDRSListItems.setStatus(Constants.RTSASSIGNED);
            } else {
                commonDRSListItems.setStatus(Constants.RTSDELIVERED);
                commonDRSListItems.setReasonCode(999);
                commonDRSListItems.setFlyer_code_manually_input("scan");
                rtsCapturedImage1.put(commonDRSListItems.getAwbNo(), false);
                rtsCapturedImage2.put(commonDRSListItems.getAwbNo(), false);
                commonDRSListItems.setChecked(false);
                getViewModel().updateShipment(commonDRSListItems);
            }
            shipmentsDetails_set.add(0, commonDRSListItems);
            setErrorSound("", false);
            rtsShipmentListAdapter.addItem(new LinkedHashSet<>(shipmentsDetails_set), true, commonDRSListItems, adapterCheckBoxCallBack);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void setErrorSound(String awb_does_not_exist, boolean isDialogShow) {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.bad_beep);
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.setVolume(100, 100);
            mediaPlayer.start();
        }
        Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(500);
        }
        if (isDialogShow) {
            showDialog(awb_does_not_exist);
        }
    }

    public void showDialog(String message) {
        final Dialog dialog = new Dialog(RTSScanActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        CustomDialogMessageBinding dialogMessageBinding = CustomDialogMessageBinding.inflate(LayoutInflater.from(RTSScanActivity.this));
        dialog.setContentView(dialogMessageBinding.getRoot());
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogMessageBinding.tvStatus.setText(message);
        dialogMessageBinding.btn.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void showManuallyFlyerCodeDialog() {
        Dialog dialog = new Dialog(RTSScanActivity.this);
        RtsFlyercodeBinding rtsFlyercodeBinding = RtsFlyercodeBinding.inflate(getLayoutInflater());
        dialog.setContentView(rtsFlyercodeBinding.getRoot());
        String parentAwbNo = shipmentsDetails_set.get(0).getParentAwbNo().trim();
        String returnPackageBarCode = shipmentsDetails_set.get(0).getReturn_package_barcode() == null ? "" : shipmentsDetails_set.get(0).getReturn_package_barcode();
        String latchingRequired = rtsScanActivityViewModel.latchingRequiredObservable.get() == null ? "" : rtsScanActivityViewModel.latchingRequiredObservable.get();
        String imageRequired = rtsScanActivityViewModel.imageRequiredObservable.get() == null ? "" : rtsScanActivityViewModel.imageRequiredObservable.get();
        if (parentAwbNo.isEmpty()) {
            rtsFlyercodeBinding.manualDialogHeading.setText(getResources().getString(R.string.provide_flyer_code_here));
            rtsFlyercodeBinding.edtNum.setHint(getResources().getString(R.string.enter_flyer_code));
        } else {
            rtsFlyercodeBinding.manualDialogHeading.setText(getResources().getString(R.string.provide_bpid_code_here));
            rtsFlyercodeBinding.edtNum.setHint(R.string.enter_branded_packaging_code);
        }

        if (imageRequired.equalsIgnoreCase("Both") && !returnPackageBarCode.equalsIgnoreCase("")) {
            rtsFlyercodeBinding.flyerImage.setVisibility(View.VISIBLE);
        } else if (parentAwbNo.isEmpty() && imageRequired.equalsIgnoreCase("Flyer")) {
            rtsFlyercodeBinding.flyerImage.setVisibility(View.VISIBLE);
        } else if (!parentAwbNo.isEmpty() && imageRequired.equalsIgnoreCase("BP") && !returnPackageBarCode.isEmpty()) {
            rtsFlyercodeBinding.flyerImage.setVisibility(View.VISIBLE);
        } else {
            rtsFlyercodeBinding.flyerImage.setVisibility(View.GONE);
        }

        rtsFlyercodeBinding.flyerImage.setOnClickListener(v -> {
            logButtonEventInGoogleAnalytics(TAG, getString(R.string.captureflyerbpidimageinmanualdialogrts), String.valueOf(shipmentsDetails_set.get(0).getAwbNo()), this);
            if (parentAwbNo.isEmpty()) {
                imageHandler.captureImage(shipmentsDetails_set.get(0).getAwbNo() + "_" + rtsVWDetailID + "_Mismatch_Flyer_Image.png", rtsFlyercodeBinding.flyerImage, "Image.png");
            } else {
                imageHandler.captureImage(shipmentsDetails_set.get(0).getAwbNo() + "_" + rtsVWDetailID + "_Mismatch_BP_ID_Image.png", rtsFlyercodeBinding.flyerImage, "Image.png");
            }
            setAwbNoForFlyer(shipmentsDetails_set.get(0).getAwbNo());
        });

        rtsFlyercodeBinding.btnSubmit.setOnClickListener(v -> {
            manuallyEnteredFlyerCode = rtsFlyercodeBinding.edtNum.getText().toString();
            logButtonEventInGoogleAnalytics(TAG, getString(R.string.manualinputflyerbpidrts), "AWB " + shipmentsDetails_set.get(0).getAwbNo() + getString( R.string.input_value ) + manuallyEnteredFlyerCode, this);
            if (rtsFlyercodeBinding.flyerImage.getVisibility() == View.VISIBLE) {
                if (!damageFlyerImageCaptured.containsKey(shipmentsDetails_set.get(0).getAwbNo())) {
                    if (parentAwbNo.isEmpty()) {
                        setErrorSound(getString(R.string.capture_image_of_flyer_barcode), true);
                    } else {
                        setErrorSound(getString(R.string.capture_image_of_branded_barcode), true);
                    }
                    return;
                }
            }
            if (parentAwbNo.isEmpty()) {
                if (manuallyEnteredFlyerCode.trim().equalsIgnoreCase("")) {
                    setErrorSound(getString(R.string.enter_flyer_barcode_value), true);
                    return;
                }
                if (!(checkManualFlyerCodeRegex(manuallyEnteredFlyerCode) && CommonUtils.isValidPatterForRVPFlyerScan(manuallyEnteredFlyerCode))) {
                    setErrorSound(getString(R.string.invalid_flyer_code_try_again), true);
                    return;
                }
            } else {
                if (manuallyEnteredFlyerCode.trim().equalsIgnoreCase("")) {
                    setErrorSound(getString(R.string.enter_branded_packaging_barcode_value), true);
                    return;
                }
            }
            if (Objects.requireNonNull(latchingRequired).equalsIgnoreCase("Both") && !returnPackageBarCode.isEmpty()) {
                shipmentsDetails_set.get(0).setIS_FLYER_WRONG_CAPTURED(!(shipmentsDetails_set.get(0).getReturn_package_barcode().equalsIgnoreCase(manuallyEnteredFlyerCode)));
            } else if (parentAwbNo.isEmpty() && latchingRequired.equalsIgnoreCase("Flyer")) {
                shipmentsDetails_set.get(0).setIS_FLYER_WRONG_CAPTURED(!(shipmentsDetails_set.get(0).getReturn_package_barcode().equalsIgnoreCase(manuallyEnteredFlyerCode)));
            } else if ((!parentAwbNo.isEmpty()) && latchingRequired.equalsIgnoreCase("BP") && !returnPackageBarCode.isEmpty()) {
                shipmentsDetails_set.get(0).setIS_FLYER_WRONG_CAPTURED(!(shipmentsDetails_set.get(0).getReturn_package_barcode().equalsIgnoreCase(manuallyEnteredFlyerCode)));
            } else {
                shipmentsDetails_set.get(0).setIS_FLYER_WRONG_CAPTURED(false);
            }
            shipmentsDetails_set.get(0).setEntered_return_package_barcode(manuallyEnteredFlyerCode.replaceAll("[^a-zA-Z0-9]", ""));
            shipmentsDetails_set.get(0).setIs_flyer_scanned(true);
            shipmentsDetails_set.get(0).setFlyer_code_manually_input("manual");
            if (parentAwbNo.isEmpty()) {
                rtsShipmentListAdapter.manuallyEnteredFlyerCode("Flyer Input : " + manuallyEnteredFlyerCode.replaceAll("[^a-zA-Z0-9]", ""), shipmentsDetails_set.get(0).getAwbNo());
            } else {
                rtsShipmentListAdapter.manuallyEnteredFlyerCode("BPID Input : " + manuallyEnteredFlyerCode.replaceAll("[^a-zA-Z0-9]", ""), shipmentsDetails_set.get(0).getAwbNo());
            }
            rtsCapturedImage1.put(shipmentsDetails_set.get(0).getAwbNo(), false);
            rtsCapturedImage2.put(shipmentsDetails_set.get(0).getAwbNo(), false);
            onScannerSeqSaveClicked(shipmentsDetails_set);
            rtsShipmentListAdapter.notifyDataSetChanged();
            flyerScanValueCounter = 0;
            dialog.dismiss();
        });
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);
        dialog.show();
    }

    public Boolean checkManualFlyerCodeRegex(String input) {
        String[] flyerCodeValues = rtsScanActivityViewModel.getDataManager().getRVPAWBWords().split(",");
        for (String code : flyerCodeValues) {
            if (input.startsWith(code)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onScannerSeqSaveClicked(List<ShipmentsDetail> newScannedDRSSequenceList) {
        getViewModel().updateManualScannedMark(newScannedDRSSequenceList);
    }

    @Override
    public void setAwbNoForFlyer(long awbNoForFlyer) {
        shipment_awb_no = awbNoForFlyer;
        imageClickedAwbNo = awbNoForFlyer;
    }

    @Override
    public void onErrorMessage(String message) {
        showSnackbar(message);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void notifyAdapter() {
        rtsShipmentListAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void OnSetAdapter(List<ShipmentsDetail> shipmentsDetails) {
        rtsShipmentListAdapter.setData(shipmentsDetails, this);
        rtsShipmentListAdapter.notifyDataSetChanged();
    }

    /*
     * Show bottom sheet for two images capture.
     * */
    public void showBottomSheet(Context context, ImageHandler imageHandler, long awbNo, long drsNo) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        RtsTwoImagesBottomsheetBinding rtsTwoImagesBottomsheetBinding = RtsTwoImagesBottomsheetBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(rtsTwoImagesBottomsheetBinding.getRoot());

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.getAttributes().windowAnimations = R.style.BottomSheetAnimation;
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(layoutParams);
        }

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        // Capture Image 1.
        rtsTwoImagesBottomsheetBinding.imageView1.setOnClickListener(v -> {
            imageHandler.captureImage(awbNo + "_" + drsNo + "_RTS_Delivered_Image_1.png", rtsTwoImagesBottomsheetBinding.imageView1, "Image.png");
            deliveredImagesPosition = "Image1";
        });

        // Capture Image 2.
        rtsTwoImagesBottomsheetBinding.imageView2.setOnClickListener(v -> {
            imageHandler.captureImage(awbNo + "_" + drsNo + "_RTS_Delivered_Image_2.png", rtsTwoImagesBottomsheetBinding.imageView2, "Image.png");
            deliveredImagesPosition = "Image2";
        });

        // When user clicked on confirm button, bottom sheet will be dismiss.
        rtsTwoImagesBottomsheetBinding.cancleBottomSheet.setOnClickListener(v -> {
            if (CommonUtils.capturedImageCount(awbNo) > 1) {
                dialog.dismiss();
            } else{
                CommonUtils.showCustomSnackbar(rtsTwoImagesBottomsheetBinding.getRoot(), getString(R.string.capture_both_images_to_proceed_further), this);
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            int PICK_FROM_CAMERA = 0x000010;
            if (requestCode == PICK_FROM_CAMERA) {
                imageHandler.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void onCheckBoxClick() {
        showManuallyFlyerCodeDialog();
    }

    private void backPress() {
        finish();
        activityRtsScanBinding.zxingBarcodeScanner.setTorchOff();
        applyTransitionToBackFromActivity(this);
    }

    @Override
    public void onCameraClick(ImageView cameraIcon, Long awbNo, int position, String shipmentStatus) {
        showBottomSheet(this, imageHandler, shipmentsDetails_set.get(0).getAwbNo(), rtsVWDetailID);
        imageClickedAwbNo = awbNo;
    }
}