package in.ecomexpress.sathi.ui.drs.rts.rts_main_list;

import static in.ecomexpress.sathi.SathiApplication.rtsCapturedImage1;
import static in.ecomexpress.sathi.SathiApplication.rtsCapturedImage2;
import static in.ecomexpress.sathi.SathiApplication.shipmentImageMap;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logButtonEventInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.barcodelistner.BarcodeHandler;
import in.ecomexpress.barcodelistner.BarcodeResult;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.databinding.ActivityRtsMainListBinding;
import in.ecomexpress.sathi.databinding.RtsTwoImagesBottomsheetBinding;
import in.ecomexpress.sathi.repo.local.data.activitiesdata.RTSActivitiesData;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.ShipmentsDetail;
import in.ecomexpress.sathi.repo.remote.model.masterdata.RTSReasonCodeMaster;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.drs.rts.rts_scan_and_deliver.RTSScanActivity;
import in.ecomexpress.sathi.ui.drs.rts.rts_signature.RTSSignatureActivity;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;
import in.ecomexpress.sathi.ui.drs.rts.interfaces.ClickListener;
import in.ecomexpress.sathi.ui.drs.rvp.awbscan.ScannerActivity;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.ImageHandler;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class RTSListActivity extends BaseActivity<ActivityRtsMainListBinding, RTSListActivityViewModel> implements BarcodeResult, IRTSListActivityNavigator, IRTSAdapterInterface, SearchView.OnQueryTextListener, ClickListener {

    @Inject
    RTSListActivityViewModel RTSListActivityViewModel;
    private final String TAG = RTSListActivity.class.getSimpleName();
    ActivityRtsMainListBinding activityRtsMainListBinding;
    String deliveredImagesPosition = "";
    String radiobutton;
    @Inject
    RTSShipmentListAdapter rtsShipmentListAdapter;
    public ImageHandler imageHandler;
    private long rtsVWDetailID;
    int delivered, undelivered;
    String address = null;
    public static int imageCaptureCount = 0;
    BarcodeHandler barcodeHandler;
    String mobile_number;
    private String select_reason_code_rts = "";
    List<String> checkListAWB = new ArrayList<>();
    private static final int PICK_FROM_CAMERA = 0x000010;
    long shipment_awb_no;
    int imagePosition = 0;
    private long previous_awb = 1;
    List<RTSReasonCodeMaster> rtsReasonCodeMasterList;
    private Bitmap capturedImageBitmap;
    private ImageView capturedImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RTSListActivityViewModel.setNavigator(this);
        activityRtsMainListBinding = getViewDataBinding();
        logScreenNameInGoogleAnalytics(TAG, this);
        try {
            getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    backPress();
                }
            });
            activityRtsMainListBinding.etSearchBox.setOnQueryTextListener(RTSListActivity.this);
            RTSActivitiesData rtsActivitiesData = getIntent().getParcelableExtra("rtsActivitiesData");
            if (rtsActivitiesData != null) {
                try {
                    rtsVWDetailID = rtsActivitiesData.getRtsVWDetailID();
                    address = rtsActivitiesData.getAddress();
                    mobile_number = rtsActivitiesData.getConsigneeMobile();
                } catch (Exception e) {
                    showMessage(e.getMessage());
                }
            }
            Constants.rtsVWDetailID = rtsVWDetailID;
            RTSListActivityViewModel.getVWDetails(rtsVWDetailID);
            RTSListActivityViewModel.getShipments(rtsVWDetailID);
            RTSListActivityViewModel.listRtsReasonCodeMaster();
            setupClickListeners();
            activityRtsMainListBinding.rgDeliveryPlace.setOnCheckedChangeListener((radioGroup, i) -> {
                int radioButtonID = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = radioGroup.findViewById(radioButtonID);
                radiobutton = String.valueOf(radioButton.getText());
                RTSListActivityViewModel.setMode(radiobutton);
                logButtonEventInGoogleAnalytics(TAG, radiobutton, "", this);
            });
            barcodeHandler = new BarcodeHandler(this, "ScannerLM", this);
            // Perform task after successfully image uploaded.
            getViewModel().getImageUploadSuccessLiveData().observe(this, isSuccess -> {
                try{
                    if (isSuccess) {
                        capturedImageView.setImageBitmap(capturedImageBitmap);
                        shipmentImageMap.put(shipment_awb_no, capturedImageBitmap);
                        rtsShipmentListAdapter.updateDRS(shipmentImageMap);
                        getViewModel().updateImageCapturedFlag(shipment_awb_no, 1);
                        getViewModel().damageFlyerImageCaptured.put(shipment_awb_no, true);
                        if(deliveredImagesPosition.equalsIgnoreCase("Image1") && shipment_awb_no > 0){
                            rtsCapturedImage1.put(shipment_awb_no, true);
                        }
                        if(deliveredImagesPosition.equalsIgnoreCase("Image2") && shipment_awb_no > 0){
                            rtsCapturedImage2.put(shipment_awb_no, true);
                        }
                    }
                } catch (Exception e){
                    showSnackbar(String.valueOf(e));
                }
            });

            imageHandler = new ImageHandler(RTSListActivity.this) {
                @Override
                public void onBitmapReceived(Bitmap bitmap, String imageUri, ImageView imgView, String imageName, String imageCode, int position, boolean verifyImage) {
                    try {
                        if (CommonUtils.checkImageIsBlurryOrNot(RTSListActivity.this, "RTS", bitmap, imageCaptureCount, RTSListActivityViewModel.getDataManager())) {
                            imageCaptureCount++;
                        } else {
                            capturedImageBitmap = bitmap;
                            capturedImageView = imgView;
                            if (isNetworkConnected()) {
                                getViewModel().uploadImageOnServer(imageName, imageUri, imageCode, shipment_awb_no, 0, rtsVWDetailID, getString(R.string.ud_rts_image), RTSListActivity.this);
                            } else {
                                getViewModel().saveImageDB(imageUri, imageCode, imageName, 0, rtsVWDetailID, shipment_awb_no, -1, getString(R.string.ud_rts_image), 0);
                            }
                        }
                    } catch (Exception e) {
                        showSnackbar(e.getMessage());
                    }
                }
            };
            rtsShipmentListAdapter.setRTSListActivityViewModelInstance(RTSListActivityViewModel);
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
    }

    private void setupClickListeners() {
        try {
            activityRtsMainListBinding.backButton.setOnClickListener(v -> backPress());
            activityRtsMainListBinding.startScanningWork.setOnClickListener(v -> onScannedSequenceClick());
            activityRtsMainListBinding.reassignShipments.setOnClickListener(v -> reassignAllShipments());
            activityRtsMainListBinding.markManuallyShipments.setOnClickListener(v -> onMarkManually());
            activityRtsMainListBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            activityRtsMainListBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
            activityRtsMainListBinding.recyclerView.setAdapter(rtsShipmentListAdapter);
            rtsShipmentListAdapter.setRTSReasonCodeMaster(RTSListActivityViewModel.mapRTSReasonCodeMaster);
            rtsShipmentListAdapter.setUpdateStaticListener(this);
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public RTSListActivityViewModel getViewModel() {
        return RTSListActivityViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_rts_main_list;
    }

    public static Intent getStartIntent(Context context) {
        return new Intent(context, RTSListActivity.class);
    }

    /*
    * Move RTSListActivity to RTSSignatureActivity with all required data.
    * */
    @Override
    public void onNextClick() {
        try {
            if (RTSListActivityViewModel.checkIfAtLeastOneScan()) {
                HashSet<String> hSet = new HashSet<>(checkListAWB);
                ArrayList<String> awbArray = new ArrayList<>(hSet);
                Intent intent = new Intent(RTSListActivity.this, RTSSignatureActivity.class);
                RTSActivitiesData rtsActivitiesData = new RTSActivitiesData();
                rtsActivitiesData.setAddress(address);
                rtsActivitiesData.setAwb(RTSListActivityViewModel.getAwb());
                rtsActivitiesData.setAwbArray(awbArray);
                rtsActivitiesData.setConsigneeMobile(mobile_number);
                rtsActivitiesData.setRtsVWDetailID(rtsVWDetailID);
                intent.putExtra("rtsActivitiesData", rtsActivitiesData);
                startActivity(intent);
                applyTransitionToOpenActivity(this);
            }
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
    }

    /*
     * Move back to ToDoListActivity.
     * */
    private void backPress() {
        finish();
        applyTransitionToBackFromActivity(this);
    }

    @Override
    public void onErrorMessage(String message) {
        showSnackbar(message);
    }

    /*
    * We have two type of shipments FWD and RVP in RTS.
    * Move next screen after scanning awb or mark manually (Delivered, Undelivered and Dispute).
    * Scanning mandatory is based on Flag comes under the DRS.
    * Value of scanDeliver = FWD(Return Packaging Barcode should not be empty), RVP or BOTH.
    * */
    public void onMarkManually() {
        try {
            logButtonEventInGoogleAnalytics(TAG, getString(R.string.onmarkmanuallybutton), "", this);
            hideKeyboard(RTSListActivity.this);
            int checkedCount = RTSListActivityViewModel.getCheckedCount();
            int checkLastIteration = 0;
            List<ShipmentsDetail> checkShipmentList = RTSListActivityViewModel.getCheckedAwbList();
            String scanDeliver = getViewModel().scanDeliverObservable.get();
            scanDeliver = (scanDeliver == null) ? "" : scanDeliver;
            if (checkedCount != 0) {
                for (ShipmentsDetail shipmentDetail : checkShipmentList) {
                    if (scanDeliver.equalsIgnoreCase("BOTH") && (shipmentDetail.getReturn_package_barcode() != null && !shipmentDetail.getReturn_package_barcode().equalsIgnoreCase(""))) {
                        RTSListActivityViewModel.showManualMandateDialog(getString(R.string.these_shipment_can_only_be_deliver_by_scanning), this);
                        return;
                    } else if ((scanDeliver.equalsIgnoreCase("FWD") || scanDeliver.equalsIgnoreCase("RVP")) && (shipmentDetail.getReturn_package_barcode() != null && !shipmentDetail.getReturn_package_barcode().equalsIgnoreCase(""))) {
                        checkLastIteration++;
                        if (shipmentDetail.getParentAwbNo().equalsIgnoreCase("") && scanDeliver.equalsIgnoreCase("RVP")) {
                            RTSListActivityViewModel.showManualMandateDialog(getString(R.string.these_shipment_can_only_be_deliver_by_scanning), this);
                            return;
                        } else if (!shipmentDetail.getParentAwbNo().equalsIgnoreCase("") && scanDeliver.equalsIgnoreCase("FWD")) {
                            RTSListActivityViewModel.showManualMandateDialog(getString(R.string.these_shipment_can_only_be_deliver_by_scanning), this);
                            return;
                        } else {
                            if (checkLastIteration == checkShipmentList.size()) {
                                handleManualDelivery(checkShipmentList);
                            }
                        }
                    } else {
                        handleManualDelivery(checkShipmentList);
                        return;
                    }
                }
            } else {
                showSnackbar(getString(R.string.havent_mark));
            }
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void notifyAdapter() {
        rtsShipmentListAdapter.notifyDataSetChanged();
    }

    /*
    * Make all shipment in assigned mode after clicking the button.
    * All shipment behave like fresh shipment.
    * */
    public void reassignAllShipments() {
        try {
            AlertDialog.Builder alert = new AlertDialog.Builder(this,R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert).setTitle(R.string.alert_title).setMessage(R.string.reassign_alert).setPositiveButton("OK", (dialog, which) -> {
                logButtonEventInGoogleAnalytics(TAG, getString(R.string.reassignallshipments), "", this);
                RTSListActivityViewModel.isAllFWDChecked = false;
                RTSListActivityViewModel.isAllRVPChecked = false;
                activityRtsMainListBinding.cbFwdPackets.setChecked(false);
                activityRtsMainListBinding.cbRvpPackets.setChecked(false);
                checkListAWB.clear();
                shipmentImageMap.clear();
                rtsCapturedImage1.clear();
                rtsCapturedImage2.clear();
                RTSListActivityViewModel.reassign();
            }).setNegativeButton(R.string.cancel, null);
            alert.show();
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
    }

    /*
    * @RTSReasonCodeMaster:- This is collection of reason code while FE mark the shipment undelivered after selecting the valid reason code.
    * This method shows the list of reason code for undelivered shipment.
    * @DS_SL:- This is the key to identify which reason code belongs to Dispute Delivery.
    * */
    @Override
    public void showPopupWindowUndelivered(List<RTSReasonCodeMaster> rtsReasonCodeMasterList) {
        try {
            this.rtsReasonCodeMasterList = rtsReasonCodeMasterList;
            final ListPopupWindow mlWindow = new ListPopupWindow(this);
            mlWindow.setModal(false);
            mlWindow.setAnchorView(activityRtsMainListBinding.addCommentIv);
            mlWindow.setWidth(1000);
            List<String> rtsCodes = new ArrayList<>();
            for (RTSReasonCodeMaster rtsReasonCodeMaster : rtsReasonCodeMasterList) {
                rtsCodes.add(rtsReasonCodeMaster.getReasonMessage());
            }
            mlWindow.setAdapter(new ArrayAdapter<String>(this, R.layout.white_spinner_single_item, R.id.white_spinner_text_view, rtsCodes) {
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    if (convertView == null) {
                        convertView = LayoutInflater.from(RTSListActivity.this).inflate(R.layout.white_spinner_single_item, parent, false);
                    }
                    TextView childView = convertView.findViewById(R.id.white_spinner_text_view);
                    if (rtsReasonCodeMasterList.get(position).getMasterDataAttributeResponse().DS_SL) {
                        childView.setBackgroundColor(getResources().getColor(R.color.yellow));
                    } else {
                        childView.setBackgroundColor(getResources().getColor(R.color.white));
                    }
                    return super.getView(position, convertView, parent);
                }
            });

            mlWindow.setOnItemClickListener((adapterView, view, position, l) -> {
                getViewModel().isAttributeAvailable(rtsReasonCodeMasterList.get(position).getReasonCode(), rtsReasonCodeMasterList.get(position), rtsReasonCodeMasterList.get(position).getReason_id(), false);
                select_reason_code_rts = String.valueOf(rtsReasonCodeMasterList.get(position).getReasonCode());
                RTSListActivityViewModel.getDataManager().setUndeliverReasonCode(select_reason_code_rts);
                mlWindow.dismiss();
            });
            mlWindow.show();
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void getFinalCount(int delivered, int undelivered) {
        this.delivered = delivered;
        this.undelivered = undelivered;
    }

    @Override
    public void showToast() {
        showSnackbar(getString(R.string.no_shipment_selected));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void OnSetAdapter(List<ShipmentsDetail> shipmentsDetails) {
        rtsShipmentListAdapter.setData(shipmentsDetails, this);
        rtsShipmentListAdapter.notifyDataSetChanged();
    }

    /*
    * This method perform the beep sound when FE scan the awb.
    * */
    private void playSound(boolean result) {
        try {
            final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
            if (result) {
                tg.startTone(ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE);
            } else {
                tg.startTone(ToneGenerator.TONE_PROP_BEEP);
            }
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
    }

    /*
    * This method is used to gat back data of shipment which marked Delivered, Undelivered and Disputed.
    * */
    private void handleScanResult(Intent data) {
        try {
            if (data != null) {
                String awbNo = data.getStringExtra(ScannerActivity.SCANNED_CODE);
                if (RTSListActivityViewModel.getFilterOutput(awbNo)) {
                    RTSListActivityViewModel.getScannedShipment(Long.parseLong(Objects.requireNonNull(awbNo)));
                } else {
                    showSnackbar(getString(R.string.no_match_found));
                    playSound(false);
                }
            } else {
                showSnackbar(getString(R.string.no_data));
            }
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            int REQUEST_CODE_SCAN = 1101;
            if (requestCode == REQUEST_CODE_SCAN) {
                handleScanResult(data);
            }
            if (data != null) {
                barcodeHandler.onActivityResult(requestCode, resultCode, data);
            }
            if (requestCode == PICK_FROM_CAMERA) {
                imageHandler.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    /*
    * @ShipmentsDetail:- It is the collection of shipments.
    * This method accept only one shipment at a time and check whether this shipment is in assign mode or not.
    * */
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void getShipmentDetail(ShipmentsDetail shipmentsDetail) {
        RTSListActivityViewModel.updateScannedShipment(shipmentsDetail);
        rtsShipmentListAdapter.notifyDataSetChanged();
        playSound(true);
    }

    @Override
    public void showMessage(String description) {
        showSnackbar(description);
    }

    @Override
    public void clearStack() {
        Intent intent = new Intent(RTSListActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void setNewAwbList(ArrayList<String> airWayBills) {
        checkListAWB.addAll(airWayBills);
    }

    /*
    * This method is used to start scanning of the shipment and send useful data to ScanActivity.
    * */
    public void onScannedSequenceClick() {
        if (!CommonUtils.isAllPermissionAllow(this)) {
            openSettingActivity();
        } else {
            handleScannedSequence();
        }
    }

    /*
     * This method is used to start scanning of the shipment and send useful data to ScanActivity.
     * */
    private void handleScannedSequence() {
        try {
            SathiApplication.rtsVWDetailID = rtsVWDetailID;
            SathiApplication.shipmentsDetailsData = (ArrayList<ShipmentsDetail>) getViewModel().shipmentsDetails_data;
            Intent intent = new Intent(RTSListActivity.this, RTSScanActivity.class);
            startActivity(intent);
            applyTransitionToOpenActivity(this);
        } catch (Exception e) {
            onErrorMessage(e.getMessage());
        }
    }

    /*
    * This method is used to check and uncheck FWD shipment.
    * */
    @Override
    public void setFWDCheckBoxStatus(boolean isChecked) {
        activityRtsMainListBinding.cbFwdPackets.setChecked(isChecked);
        getViewModel().isAllFWDChecked = isChecked;
    }

    /*
     * This method is used to check and uncheck RVP shipment.
     * */
    @Override
    public void setRVPCheckBoxStatus(boolean isChecked) {
        activityRtsMainListBinding.cbRvpPackets.setChecked(isChecked);
        getViewModel().isAllRVPChecked = isChecked;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            RTSListActivityViewModel.getShipments(rtsVWDetailID);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void onResult(String value) {
        try {
            if (value != null) {
                if (RTSListActivityViewModel.getFilterOutput(value)) {
                    RTSListActivityViewModel.getScannedShipment(Long.parseLong(value));
                } else {
                    showSnackbar(getString(R.string.no_match_found));
                    playSound(false);
                }
            } else {
                showSnackbar(getString(R.string.no_data));
            }
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
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

    /*
    * This method is used to handle the shipment which is done via mark manually.
    * After clicked on Mark Manual button this method shows a dialog to take confirmation from the FE.
    * */
    @SuppressLint("NotifyDataSetChanged")
    private void handleManualDelivery(List<ShipmentsDetail> checkShipmentList) {
        new AlertDialog.Builder(this,R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert).setCancelable(false).setMessage(R.string.mark_manually_alert).setPositiveButton("OK", (dialog, which) -> {
            try {
                RTSListActivityViewModel.updateManualMark(checkShipmentList);
                rtsShipmentListAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                showSnackbar(getString(R.string.unable));
            }
        }).setNegativeButton(R.string.cancel, null).create().show();
    }

    /*
    * This is method is used to reassign the shipment after long press on shipment layout.
    * */
    @Override
    public void update(ShipmentsDetail shipmentsDetail) {
        try {
            RTSListActivityViewModel.updateShipment(shipmentsDetail);
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
    }

    /*
    * This override method is used for searching the AWB in RTS List Activity Search Bar.
    * */
    @Override
    public boolean onQueryTextSubmit(String query) {
        try {
            logButtonEventInGoogleAnalytics(TAG, getString(R.string.onquerytextsubmit), query, this);
            rtsShipmentListAdapter.getFilter().filter(query);
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
        return false;
    }

    /*
     * This override method is used for searching the AWB in RTS List Activity Search Bar.
     * */
    @Override
    public boolean onQueryTextChange(String newText) {
        try {
            rtsShipmentListAdapter.getFilter().filter(newText);
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
        return false;
    }

    /*
    * This method is used to capture the image and set the name with AWB and DRS_ID.
    * */
    @Override
    public void onCameraClick(ImageView cameraIcon, Long awbNo, int position, String shipmentStatus) {
        this.shipment_awb_no = awbNo;
        imagePosition = position;
        if(shipmentStatus.equals(Constants.RTSMANUALLYDELIVERED) || shipmentStatus.equals(Constants.RTSDELIVERED)){
            showBottomSheet(this, imageHandler, awbNo, rtsVWDetailID);
            return;
        }
        if (previous_awb != awbNo) {
            imageCaptureCount = 0;
            previous_awb = awbNo;
        }
        imageHandler.captureImage(awbNo + "_" + rtsVWDetailID + "_Image.png", cameraIcon, "Image.png");
    }
}