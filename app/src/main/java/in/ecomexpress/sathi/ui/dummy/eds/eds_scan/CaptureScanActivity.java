package in.ecomexpress.sathi.ui.dummy.eds.eds_scan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityEdsScanBinding;
import in.ecomexpress.sathi.repo.local.data.eds.EDSActivityResponseWizard;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.dummy.eds.eds_signature.EDSSignatureActivity;
import in.ecomexpress.sathi.ui.drs.forward.details.ScannerActivity;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.FlashlightProvider;
import in.ecomexpress.sathi.utils.ImageHandler;

@AndroidEntryPoint
public class CaptureScanActivity extends BaseActivity<ActivityEdsScanBinding, CaptureScanViewModel> implements CaptureScanNavigator {

    private final int REQUEST_CODE_SCAN = 1101;
    public List<EDSActivityResponseWizard> edsActivityResponseWizards;
    @Inject
    CaptureScanViewModel captureScanViewModel;
    Boolean[] status = new Boolean[3];
    // Blur Image Recognition Work:-
    public static int imageCaptureCount = 0;
    String awb = "";
    EDSResponse edsResponseCommit;
    private ActivityEdsScanBinding activityScanAwbBinding;
    private int layoutPosition = 0;
    private ImageHandler imageHandler;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, CaptureScanActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        captureScanViewModel.setNavigator(this);
        activityScanAwbBinding = getViewDataBinding();
        awb = getIntent().getExtras().getString("awb");
        edsResponseCommit = getIntent().getParcelableExtra("edsResponse");
        if (getIntent().getParcelableArrayListExtra("data") != null) {
            edsActivityResponseWizards = getIntent().getParcelableArrayListExtra("data");
        } else {
            edsActivityResponseWizards = null;
        }
        edsActivityResponseWizards = getIntent().getParcelableArrayListExtra("data");

        activityScanAwbBinding.awb.setText(String.valueOf(edsResponseCommit.getAwbNo()));
        activityScanAwbBinding.textViewBarcode1.setText(getString(R.string.eds_scan_awb_bf_pkg));
        activityScanAwbBinding.textViewBarcode2.setText(getString(R.string.eds_scan_awb_af_pkg));
        status[0] = true;
        status[1] = false;
        status[2] = false;

        imageHandler = new ImageHandler(CaptureScanActivity.this) {
            @Override
            public void onBitmapReceived(Bitmap bitmap, String imageUri, ImageView imgView, String imageName, String imageCode, int pos,boolean verifyImage) {
                // Blur Image Recognition Using Laplacian Variance:-
                if(CommonUtils.checkImageIsBlurryOrNot(CaptureScanActivity.this, "EDS", bitmap, imageCaptureCount, captureScanViewModel.getDataManager())){
                    imageCaptureCount++;
                }
                else{
                    if (imgView != null) {
                        imgView.setImageBitmap(bitmap);
                        if (layoutPosition == 0) {
                            activityScanAwbBinding.txtbfpck.setText(R.string.imp);
                            captureScanViewModel.setImageOpen(true);
                        }
                        status[layoutPosition] = true;
                    }
                    captureScanViewModel.saveImageDB(imageUri, imageCode, imageName, edsResponseCommit);
                }
            }
        };
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
        return R.layout.activity_eds_scan;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!CommonUtils.isAllPermissionAllow(this)) {
            openSettingActivity();
            return;
        }
    }

    @Override
    public void captureImageBeforePackaging() {
        try {
           /* if (!CommonUtils.isAllPermissionAllow(this)) {
                openSettingActivity();
                return;
            }*/

            layoutPosition = 0;
            imageHandler.captureImage(edsResponseCommit.getAwbNo() + "_" + edsResponseCommit.getDrsNo() + "_open1.png", activityScanAwbBinding.imgCamBfPkg, "open1");
        } catch (Exception e){
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void scanCodeBeforePackaging() {
        try {
            // Check permission granted:-
           /* if(!CommonUtils.isAllPermissionAllow(this)){
                openSettingActivity();
                return;
            }*/
            if (status[0]) {
                layoutPosition = 1;
                Intent intent = new Intent(CaptureScanActivity.this, ScannerActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
            } else {
                showSnackbar(getString(R.string.capture_image_bf_pkg));
            }
        }catch (Exception e){
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void scanCodeAfterPackaging() {
        try {
            // Check permission granted:-
           /* if(!CommonUtils.isAllPermissionAllow(this)){
                openSettingActivity();
                return;
            }*/
            if (status[1]) {
                layoutPosition = 2;
                Intent intent = new Intent(CaptureScanActivity.this, ScannerActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
            } else {
                showSnackbar(getString(R.string.scan_awb_af_pkg));
            }
        }catch (Exception e){
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void onProceed() {
         try {
             if (!status[0]) {
                 showSnackbar(getString(R.string.capture_image_bf_pkg));
                 return;
             }
             if (!status[1]) {
                 if (activityScanAwbBinding.textViewBarcode1.getText().equals(getString(R.string.tryagain))) {
                     showSnackbar(getString(R.string.scanmismatch));
                 } else {
                     showSnackbar(getString(R.string.scan_awb_af_pkg));
                 }
                 return;
             }
             if (!status[2]) {
                 if (activityScanAwbBinding.textViewBarcode2.getText().equals(getString(R.string.tryagain))) {
                     showSnackbar(getString(R.string.scanmismatch));
                 } else {
                     showSnackbar(getString(R.string.scan_awb_af_pkg));
                 }
             } else {
                 Intent intent = EDSSignatureActivity.getStartIntent(this);
                 intent.putParcelableArrayListExtra(getString(R.string.data), new ArrayList<>(edsActivityResponseWizards));
                 intent.putExtra("awb", edsResponseCommit.getAwbNo());
                 intent.putExtra("edsResponse", edsResponseCommit);
                 finish();
                 startActivity(intent);
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
    }

    @Override
    public void onBack() {
        finish();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void mResultReceiverScan(String strBarcode) {
        if (!status[1]) {
            if (strBarcode.equalsIgnoreCase(String.valueOf(edsResponseCommit.getAwbNo()))) {
                activityScanAwbBinding.textViewBarcode1.setText("Open shipment AWB is " + strBarcode);
                status[1] = true;
                captureScanViewModel.setScanCodeOpen(true);
            } else {
                showSnackbar("AWB doesn't match");
                activityScanAwbBinding.textViewBarcode1.setText(R.string.tryagain);
                status[1] = false;
                captureScanViewModel.setScanCodeOpen(false);
            }
        } else if (!status[2]) {
            if (strBarcode.equalsIgnoreCase(String.valueOf(edsResponseCommit.getAwbNo()))) {
                activityScanAwbBinding.textViewBarcode2.setText("Packed shipment AWB is " + strBarcode);
                status[2] = true;
                captureScanViewModel.setScanCodeClose(true);
            } else {
                showSnackbar("AWB doesn't match");
                activityScanAwbBinding.textViewBarcode2.setText(getString(R.string.tryagain));
                status[2] = false;
                captureScanViewModel.setScanCodeClose(false);
            }
        }
    }

    @Override
    public void showError(String e) {
        showSnackbar(e);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQUEST_CODE_SCAN) {
                handleScanResult(data);
            } else {
                imageHandler.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e){
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    @SuppressLint("SetTextI18n")
    private void handleScanResult(Intent data) {
        try {
            if (layoutPosition == 1) {
                if (data != null) {
                    if (data.getStringExtra(ScannerActivity.SCANNED_CODE).equalsIgnoreCase(String.valueOf(edsResponseCommit.getAwbNo()))) {
                        activityScanAwbBinding.textViewBarcode1.setText("Open shipment AWB is " + data.getStringExtra(ScannerActivity.SCANNED_CODE));
                        status[layoutPosition] = true;
                        captureScanViewModel.setScanCodeOpen(true);
                    } else {
                        showSnackbar("AWB doesn't match");
                        activityScanAwbBinding.textViewBarcode1.setText(R.string.tryagain);
                        status[layoutPosition] = false;
                        captureScanViewModel.setScanCodeOpen(false);
                    }
                } else {
                    status[layoutPosition] = false;
                    showSnackbar("AWB doesn't match");
                }
            } else if (layoutPosition == 2) {
                if (data != null) {
                    activityScanAwbBinding.textViewBarcode2.setText("Packed shipment AWB is " + data.getStringExtra(ScannerActivity.SCANNED_CODE));
                    status[layoutPosition] = true;
                    captureScanViewModel.setScanCodeClose(true);
                } else {
                    status[layoutPosition] = false;
                    showSnackbar("AWB doesn't match");
                }
            }
        } catch (Exception e){
            e.printStackTrace();
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
            FlashlightProvider flashlightProvider=new FlashlightProvider(this);
            flashlightProvider.turnFlashOff();
            if (CaptureScanViewModel.device.equals(Constants.NEWLAND)) {
                if (captureScanViewModel.mResultReceiver()!=null) {
                    unregisterReceiver(captureScanViewModel.mResultReceiver());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}