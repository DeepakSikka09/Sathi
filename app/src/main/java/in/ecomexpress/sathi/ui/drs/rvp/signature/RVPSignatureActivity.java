package in.ecomexpress.sathi.ui.drs.rvp.signature;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Named;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityRvpSignatureBinding;
import in.ecomexpress.sathi.repo.local.data.rvp.RvpCommit;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.drs.rvp.success.RVPSuccessActivity;
import in.ecomexpress.sathi.utils.BitmapUtils;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.Helper;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.cameraView.CameraActivity;

@AndroidEntryPoint
public class RVPSignatureActivity extends BaseActivity<ActivityRvpSignatureBinding, RVPSignatureViewModel> implements IRVPSignatureNavigator {

    private final String TAG = RVPSignatureActivity.class.getSimpleName();
    // Blur Image Recognition Work:-
    public static int imageCaptureCount = 0;
    @Inject
    RVPSignatureViewModel signatureViewModel;
    @Inject
    RvpCommit rvpCommit;
    ActivityRvpSignatureBinding activityRvpSignatureBinding;
    String awb = "";
    String composite_key = null;
    List<RvpCommit.QcWizard> qcWizards;
    DRSReverseQCTypeResponse drsReverseQCTypeResponse;
    Bitmap emptyBitmap, well;
    String navigation;
    Boolean isImageCaptured = false;
    boolean isImageStatus = false;
    ImageView imageView;
    Bitmap mBitmap;
    private int meterRange;
    private boolean consigneeProfiling = false;
    private static final int CAMERA_REQUEST_CODE = 100;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, RVPSignatureActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRvpSignatureBinding = getViewDataBinding();
        signatureViewModel.setNavigator(this);
        logScreenNameInGoogleAnalytics(TAG, this);
        Constants.LOCATION_ACCURACY = signatureViewModel.getDataManager().getUndeliverConsigneeRANGE();
        signatureViewModel.getDataManager().setLoginPermission(false);
        awb = getIntent().getStringExtra("awb");
        navigation = Objects.requireNonNull(getIntent().getExtras()).getString("navigation", "");
        composite_key = getIntent().getExtras().getString(Constants.COMPOSITE_KEY, "");
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.rvp));
        drsReverseQCTypeResponse = getIntent().getParcelableExtra("rvp");
        if (!navigation.equalsIgnoreCase("fail")) {
            rvpCommit = getIntent().getParcelableExtra("rvpCommit");
            activityRvpSignatureBinding.llCaptueImageTwo.setVisibility(View.GONE);
            signatureViewModel.setIsQcFail(false);
        } else {
            activityRvpSignatureBinding.llCaptueImageTwo.setVisibility(View.VISIBLE);
            signatureViewModel.setIsQcFail(true);
        }
        if (getIntent().getParcelableArrayListExtra("data") != null) {
            qcWizards = getIntent().getParcelableArrayListExtra("data");
        } else {
            qcWizards = null;
        }
        if (signatureViewModel.getDataManager().getIsSignatureImageMandatory().equalsIgnoreCase("true")) {
            activityRvpSignatureBinding.llSingatureText.setVisibility(View.VISIBLE);
            activityRvpSignatureBinding.llSingaturePad.setVisibility(View.VISIBLE);
            activityRvpSignatureBinding.tvDeclare.setVisibility(View.VISIBLE);
            activityRvpSignatureBinding.llWatemarkText.setVisibility(View.GONE);
        } else {
            activityRvpSignatureBinding.llWatemarkText.setVisibility(View.VISIBLE);
            activityRvpSignatureBinding.llSingatureText.setVisibility(View.GONE);
            activityRvpSignatureBinding.llSingaturePad.setVisibility(View.GONE);
            activityRvpSignatureBinding.tvDeclare.setVisibility(View.GONE);
            activityRvpSignatureBinding.tvDeclare.setTypeface(activityRvpSignatureBinding.tvDeclare.getTypeface(), Typeface.BOLD);
            activityRvpSignatureBinding.tvText.setText(R.string.no_need_to_take_signature_at_this_activity);
            activityRvpSignatureBinding.tvText.setTextColor(getResources().getColor(R.color.black));
            activityRvpSignatureBinding.tvText.setTextSize(15f);
        }
        activityRvpSignatureBinding.awb.setText(awb);

        // init RVP Commit packet
        signatureViewModel.setRvpCommit(qcWizards, drsReverseQCTypeResponse, rvpCommit);

        activityRvpSignatureBinding.rgDeliveryPlace.setOnCheckedChangeListener((radioGroup, i) -> {
            int radioButtonID = radioGroup.getCheckedRadioButtonId();
            RadioButton radioButton = radioGroup.findViewById(radioButtonID);
            String add = (String) radioButton.getText();
            signatureViewModel.setPickUpAddress(add);
        });
        signatureViewModel.fetchRVPShipment(rvpCommit.getAwb());
        signatureViewModel.getImageStatus(String.valueOf(awb));

        if (!CommonUtils.isEmpty(navigation))
            if (navigation.equalsIgnoreCase("fail")) {
                signatureViewModel.setImageRequired(true);
            } else {
                signatureViewModel.setImageRequired(Constants.RVP_Sign_Image_Required);
            }
        if (Constants.RVP_Sign_Image_Required || signatureViewModel.sign_image_required) {
            activityRvpSignatureBinding.txtMandatory.setText(R.string.capture_image_rts);
        } else {
            activityRvpSignatureBinding.txtMandatory.setText(R.string.capture_image);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread() {
            public void run() {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    Logger.e(RVPSignatureActivity.class.getName(), e.getMessage());
                }
            }
        }.start();
    }

    @Override
    public RVPSignatureViewModel getViewModel() {
        return signatureViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_rvp_signature;
    }

    @Override
    public void onCaptureImage(String image) {
        if (!CommonUtils.isAllPermissionAllow(this)) {
            openSettingActivity();
            return;
        }
        if (!isNetworkConnected()) {
            showSnackbar(getResources().getString(R.string.no_network_error));
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_MaterialComponents_Light_Dialog_Alert);
        String AlertText1 = "Attention : ";
        builder.setMessage(AlertText1 + getString(R.string.alert))
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> {
                    imageView = activityRvpSignatureBinding.image;
                    startCameraActivity(image, awb + "_" + rvpCommit.getDrsId() + "_Image.png");
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onCaptureImageTwo(String image) {
        if (!CommonUtils.isAllPermissionAllow(this)) {
            openSettingActivity();
            return;
        }
        if (!isNetworkConnected()) {
            showSnackbar(getResources().getString(R.string.no_network_error));
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_MaterialComponents_Light_Dialog_Alert);
        String AlertText1 = "Attention : ";
        builder.setMessage(AlertText1 + getString(R.string.alert))
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> {
                    imageView = activityRvpSignatureBinding.imagetwo;
                    startCameraActivity(image, awb + "_" + rvpCommit.getDrsId() + "_Image.png");
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                if (data != null) {
                    String imagePathWithWaterMark = data.getStringExtra("imagePathWithWaterMark");
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePathWithWaterMark);
                    String imageUri = data.getStringExtra("imageUri");
                    String imageCode = data.getStringExtra("imageCode");
                    sendCapturedImageToServerAndSaveInDB(imageCode, imageUri, bitmap);
                } else {
                    showSnackbar("Captured Image Data Is Empty");
                }
            } catch (Exception e) {
                showSnackbar("Captured Image Data Is Empty");
            }
        }
    }

    private void sendCapturedImageToServerAndSaveInDB(String imageCode, String imageUri, Bitmap bitmap) {
        if (CommonUtils.checkImageIsBlurryOrNot(RVPSignatureActivity.this, "RQC", bitmap, imageCaptureCount, signatureViewModel.getDataManager()) || CommonUtils.checkImageIsBlurryOrNot(RVPSignatureActivity.this, "RVP", bitmap, imageCaptureCount, signatureViewModel.getDataManager())) {
            imageCaptureCount++;
        } else {
            mBitmap = bitmap;
            if (imageView != null) {
                isImageCaptured = true;
            }
            if (!CommonUtils.isEmpty(imageCode))
                if (imageCode.equalsIgnoreCase("Image")) {
                    signatureViewModel.setImageCaptured(true);
                } else if (imageCode.equalsIgnoreCase("Image2")) {
                    signatureViewModel.setImageCapturedTwo(true);
                }
            if (isNetworkConnected()) {
                signatureViewModel.uploadImageServerImage(getContext(), awb + "_" + rvpCommit.getDrsId() + "_" + imageCode + ".png", imageUri, imageCode, Long.parseLong(awb), Integer.parseInt(rvpCommit.getDrsId()));
            } else {
                showSnackbar(getResources().getString(R.string.no_network_error));
            }
        }
    }

    @Override
    public void showServerError() {
        showSnackbar("Server Response False, Recapture Again");
    }

    @Override
    public void saveSignature() {
        try {
            Calendar calendar = Calendar.getInstance();
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
            int mMonth = calendar.get(Calendar.MONTH) + 1;
            if (signatureViewModel.loginDate().equalsIgnoreCase(String.valueOf(mDay)) && signatureViewModel.getDataManager().getLoginMonth() == mMonth) {
                rvpCommit.setCall_attempt_count(signatureViewModel.getDataManager().getRVPCallCount(awb + "RVP"));
                rvpCommit.setMap_activity_count(signatureViewModel.getDataManager().getRVPMapCount(Long.parseLong(awb)));
                rvpCommit.setOfd_otp_verify_status(signatureViewModel.getDataManager().getOFDOTPVerifiedStatus());
                rvpCommit.setStart_qc_lat(signatureViewModel.getDataManager().getStartQCLat());
                rvpCommit.setStart_qc_lng(signatureViewModel.getDataManager().getStartQCLng());
                saveBitmapInFile();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_MaterialComponents_Light_Dialog_Alert);
                String AlertText1 = "Attention : ";
                builder.setMessage(AlertText1 + getString(R.string.commit_restriction_msg))
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, id) -> {
                            dialog.cancel();
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        } catch (Exception e) {
            Logger.e(RVPSignatureActivity.class.getName(), e.getMessage());
        }
    }

    @Override
    public void showSuccessStatus() {
        if (navigation.equalsIgnoreCase("pass")) {
            openSuccessActivity();
        } else if (navigation.equalsIgnoreCase("fail")) {
            openFailActivity();
        }
        Helper.updateLocationWithData(RVPSignatureActivity.this, rvpCommit.getAwb(), rvpCommit.getStatus());
    }

    private void openFailActivity() {
        String address = drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getLine1()
                + " " + drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getLine2()
                + " " + drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getLine3()
                + " " + drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getLine4()
                + " " + drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getCity();
        String finalAddress = address.replaceAll("null", "");
        Intent intent = RVPSuccessActivity.getStartIntent(this);
        intent.putExtra(Constants.DECIDENEXT, Constants.UNDELIVERED);
        intent.putExtra("ConsigneeName", drsReverseQCTypeResponse.getConsigneeDetails().getName());
        intent.putExtra("ConsigneeAddress", finalAddress);
        intent.putExtra("ConsigneeItemName", drsReverseQCTypeResponse.getShipmentDetails().getItem());
        intent.putExtra("Date", drsReverseQCTypeResponse.getAssignedDate());
        intent.putExtra("awb", drsReverseQCTypeResponse.getAwbNo());
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    @Override
    public void submitErrorAlert() {
        showSnackbar(getString(R.string.select_picked_from));
    }

    @Override
    public void onHandleError(String errorResponse) {
        showSnackbar(errorResponse);
    }

    @Override
    public void onBack() {
        showSnackbar(getString(R.string.cannot_go_back));
    }

    @Override
    public void onpickaddressfalse() {
        showSnackbar(getString(R.string.pickupfrom));
    }

    @Override
    public void onClear() {
        activityRvpSignatureBinding.signature.clear();
    }

    @Override
    public void setConsigneeName(String selectedItem) {
        if (selectedItem.equalsIgnoreCase("self")) {
            activityRvpSignatureBinding.etEmail.setText(drsReverseQCTypeResponse.getConsigneeDetails().getName());
        } else {
            if (!activityRvpSignatureBinding.etEmail.getText().equals("")) {
                try {
                    if ((activityRvpSignatureBinding.etEmail.getText().toString()).equalsIgnoreCase(drsReverseQCTypeResponse.getConsigneeDetails().getName())) {
                        activityRvpSignatureBinding.etEmail.setText("");
                        activityRvpSignatureBinding.etEmail.setHint("Sender's Name");
                    }
                } catch (Exception e) {
                    Logger.e(RVPSignatureActivity.class.getName(), e.getMessage());
                }
            }
        }
    }

    @Override
    public void enableEditText(boolean b) {
        activityRvpSignatureBinding.etEmail.setEnabled(b);
    }

    @Override
    public void hideEdit(String self) {
        if (self.equalsIgnoreCase("Select"))
            activityRvpSignatureBinding.etEmail.setVisibility(View.GONE);
        else {
            activityRvpSignatureBinding.etEmail.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public String getReceiverName() {
        return activityRvpSignatureBinding.etEmail.getText().toString();
    }

    @Override
    public void onRVPItemFetched(DRSReverseQCTypeResponse drsReverseQCTypeResponse) {
        this.drsReverseQCTypeResponse = drsReverseQCTypeResponse;
        signatureViewModel.checkMeterRange(drsReverseQCTypeResponse);
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
        signatureViewModel.onCreateRVPCommit(navigation, composite_key);
    }

    @Override
    public void setImageStatus(Boolean aBoolean) {
        isImageStatus = aBoolean;
        Log.e("database image status", isImageStatus + "");
    }

    @Override
    public void setBitmap() {
        imageView.setImageBitmap(mBitmap);
    }

    @Override
    public Context getContext() {
        return this;
    }

    private void openSuccessActivity() {
        String address = drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getLine1()
                + " " + drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getLine2()
                + " " + drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getLine3()
                + " " + drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getLine4()
                + " " + drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getCity();
        String finalAddress = address.replaceAll("null", "");
        Intent intent = RVPSuccessActivity.getStartIntent(this);
        intent.putExtra(Constants.DECIDENEXT, Constants.SUCCESS);
        intent.putExtra("ConsigneeName", drsReverseQCTypeResponse.getConsigneeDetails().getName());
        intent.putExtra("ConsigneeAddress", finalAddress);
        intent.putExtra("ConsigneeItemName", drsReverseQCTypeResponse.getShipmentDetails().getItem());
        intent.putExtra("Date", drsReverseQCTypeResponse.getAssignedDate());
        intent.putExtra("awb", drsReverseQCTypeResponse.getAwbNo());
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        activityRvpSignatureBinding.signature.saveInstanceState();
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void saveBitmapInFile() {
        try {
            File fileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/" + Constants.EcomExpress);
            if (!fileDir.exists())
                fileDir.mkdirs();
            File file1 = new File(fileDir, awb + "_" + rvpCommit.getDrsId() + "_signature.png");
            if (!file1.exists())
                file1.createNewFile();
            try (FileOutputStream ostream = new FileOutputStream(file1)) {
                well = activityRvpSignatureBinding.signature.getSignatureBitmap();
                emptyBitmap = activityRvpSignatureBinding.signature.getWhiteBackground(well);
                if (well.sameAs(emptyBitmap)) {
                    showSnackbar("Please place signature.");
                } else {
                    if (!signatureViewModel.getDataManager().isCounterDelivery() && signatureViewModel.getCounterDeliveryRange() < signatureViewModel.getDataManager().getDCRANGE()) {
                        showSnackbar("Shipment cannot be marked delivered within the DC");
                        return;
                    }
                    if (signatureViewModel.getDataManager().isCounterDelivery() && signatureViewModel.getCounterDeliveryRange() < signatureViewModel.getDataManager().getDCRANGE()) {
                        BitmapUtils.saveBitmap(file1, well);
                        rvpCommit.setLocation_verified(false);
                        try {
                            if (isImageStatus) {
                                signatureViewModel.uploadAWSImage(file1.getAbsolutePath(), "signature", awb + "_" + rvpCommit.getDrsId() + "_signature.png");
                            } else {
                                signatureViewModel.uploadAWSImage(file1.getAbsolutePath(), "signature", awb + "_" + rvpCommit.getDrsId() + "_signature.png");
                            }
                        } catch (Exception e) {
                            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                            restApiErrorHandler.writeErrorLogs(0, e.getMessage());
                            Logger.e(RVPSignatureActivity.class.getName(), e.getMessage());
                        }
                    } else if (Constants.CONSIGNEE_PROFILE) {
                        String dialog_message = getString(R.string.commitdialog);
                        String positiveButtonText = getString(R.string.yes);
                        if (consigneeProfiling && meterRange > signatureViewModel.getDataManager().getUndeliverConsigneeRANGE()
                                && signatureViewModel.getDataManager().
                                getConsigneeProfileValue().equalsIgnoreCase("W")) {
                            dialog_message = "You are not attempting the shipment at Consignee’s location. Your current location = " + signatureViewModel.getDataManager().getCurrentLatitude() + ", " + signatureViewModel.getDataManager().getCurrentLongitude() + " You are " + meterRange + " meter away from consignee location. \nAre you sure you want to commit?";
                            positiveButtonText = getString(R.string.yes);
                        } else if ((!consigneeProfiling) && meterRange > signatureViewModel.getDataManager().getUndeliverConsigneeRANGE()
                                && signatureViewModel.getDataManager().
                                getConsigneeProfileValue().equalsIgnoreCase("R")) {
                            dialog_message = "You are not allowed to commit this shipment as you are not attempting at consignee location. your current location = " + signatureViewModel.getDataManager().getCurrentLatitude() + ", " + signatureViewModel.getDataManager().getCurrentLongitude() + " You are " + meterRange + " meter away from consignee location";
                            positiveButtonText = getString(R.string.ok);
                        } else {
                            rvpCommit.setLocation_verified(meterRange <= signatureViewModel.getDataManager().getUndeliverConsigneeRANGE());
                            BitmapUtils.saveBitmap(file1, well);
                            try {
                                if (isImageStatus) {
                                    signatureViewModel.uploadAWSImage(file1.getAbsolutePath(), "signature", awb + "_" + rvpCommit.getDrsId() + "_signature.png");
                                } else {
                                    signatureViewModel.uploadAWSImage(file1.getAbsolutePath(), "signature", awb + "_" + rvpCommit.getDrsId() + "_signature.png");
                                }
                            } catch (Exception e) {
                                RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                                restApiErrorHandler.writeErrorLogs(0, e.getMessage());
                                Logger.e(RVPSignatureActivity.class.getName(), e.getMessage());
                            }
                            return;
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_MaterialComponents_Light_Dialog_Alert);
                        builder.setCancelable(false);
                        builder.setMessage(dialog_message);
                        rvpCommit.setLocation_verified(meterRange <= signatureViewModel.getDataManager().getUndeliverConsigneeRANGE());
                        builder.setPositiveButton(positiveButtonText, (dialog, which) -> {
                            if (signatureViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("W")
                                    || signatureViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("N")) {
                                BitmapUtils.saveBitmap(file1, well);
                                if (isImageStatus) {
                                    signatureViewModel.uploadAWSImage(file1.getAbsolutePath(), "signature", awb + "_" + rvpCommit.getDrsId() + "_signature.png");
                                } else {
                                    signatureViewModel.uploadAWSImage(file1.getAbsolutePath(), "signature", awb + "_" + rvpCommit.getDrsId() + "_signature.png");
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
                        rvpCommit.setLocation_verified(meterRange <= signatureViewModel.getDataManager().getUndeliverConsigneeRANGE());
                        BitmapUtils.saveBitmap(file1, well);
                        try {
                            if (isImageStatus) {
                                signatureViewModel.uploadAWSImage(file1.getAbsolutePath(), "signature", awb + "_" + rvpCommit.getDrsId() + "_signature.png");
                            } else {
                                signatureViewModel.uploadAWSImage(file1.getAbsolutePath(), "signature", awb + "_" + rvpCommit.getDrsId() + "_signature.png");
                            }
                        } catch (Exception e) {
                            Logger.e(RVPSignatureActivity.class.getName(), e.getMessage());
                            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                            restApiErrorHandler.writeErrorLogs(0, e.getMessage());

                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.e(RVPSignatureActivity.class.getName(), e.getMessage());
        }
    }

    private void startCameraActivity(String ImageCode, String imageName) {
        try {
            Intent intent = new Intent(this, CameraActivity.class);
            intent.putExtra("EmpCode", signatureViewModel.getDataManager().getEmp_code());
            intent.putExtra("Latitude", signatureViewModel.getDataManager().getCurrentLatitude());
            intent.putExtra("Longitude", signatureViewModel.getDataManager().getCurrentLongitude());
            intent.putExtra("ImageCode", ImageCode);
            intent.putExtra("imageName", imageName);
            intent.putExtra("awbNumber", awb);
            intent.putExtra("drs_id", rvpCommit.getDrsId());
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        applyTransitionToBackFromActivity(this);
    }
}