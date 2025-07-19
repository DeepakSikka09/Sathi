package in.ecomexpress.sathi.ui.drs.rts.rts_signature;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logButtonEventInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.Constants.CHECKFIRSTUPLOAD;
import static in.ecomexpress.sathi.utils.Constants.Image_Check;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.text.InputFilter;
import android.view.View;
import android.widget.ImageView;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityRtsSignatureBinding;
import in.ecomexpress.sathi.repo.local.data.activitiesdata.RTSActivitiesData;
import in.ecomexpress.sathi.repo.local.data.rts.RTSCommit;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.Details;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;
import in.ecomexpress.sathi.ui.drs.rts.rts_success.RTSSuccessActivity;
import in.ecomexpress.sathi.utils.BitmapUtils;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.ImageHandler;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class RTSSignatureActivity extends BaseActivity<ActivityRtsSignatureBinding, RTSSignatureViewModel> implements IRTSSignatureNavigator, MultiplePodAdapter.OnPODListener {

    @Inject
    RTSSignatureViewModel signatureViewModel;
    private final String TAG = RTSSignatureActivity.class.getSimpleName();
    @Inject
    RTSCommit rtsCommit;
    ActivityRtsSignatureBinding activityRtsSignatureBinding;
    public ImageHandler imageHandler;
    Long rtsVWDetailID;
    String awb = "";
    public static int imageCaptureCount = 0;
    Bitmap emptyBitmap, well;
    String address = null;
    private int meterRange;
    String mobile_number;
    ArrayList<String> new_awb_list;
    boolean is_skip_OTP;
    private long mLastClickTime = 0;
    List<Integer> itemList = new ArrayList<>();
    MultiplePodAdapter rvAdapter;
    String idPress;
    private Bitmap capturedImageBitmap;
    private ImageView capturedImageView;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, RTSSignatureActivity.class);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            activityRtsSignatureBinding = getViewDataBinding();
            signatureViewModel.setNavigator(this);
            signatureViewModel.getDataManager().setLoginPermission(false);
            logScreenNameInGoogleAnalytics(TAG, this);
            activityRtsSignatureBinding.etPhone.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
            activityRtsSignatureBinding.etPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
            RTSActivitiesData rtsActivitiesData = getIntent().getParcelableExtra("rtsActivitiesData");
            if (rtsActivitiesData != null) {
                try {
                    rtsVWDetailID = rtsActivitiesData.getRtsVWDetailID();
                    awb = rtsActivitiesData.getAwb();
                    mobile_number = rtsActivitiesData.getConsigneeMobile();
                    new_awb_list = rtsActivitiesData.getAwbArray();
                    address = rtsActivitiesData.getAddress();
                } catch (Exception e){
                    showErrorSnackbar(e.getMessage());
                }
            }
            getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    backPress();
                }
            });
            setupClickListeners();

            getViewModel().getImageUploadSuccessLiveData().observe(this, isSuccess -> {
                try{
                    if (isSuccess) {
                        capturedImageView.setImageBitmap(capturedImageBitmap);
                        signatureViewModel.setImageCaptured(true);
                    }
                } catch (Exception e){
                    showSnackbar(String.valueOf(e));
                }
            });
            Constants.LOCATION_ACCURACY = signatureViewModel.getDataManager().getUndeliverConsigneeRANGE();
            activityRtsSignatureBinding.signature.setContext(RTSSignatureActivity.this);
            signatureViewModel.getVendorData(rtsCommit, rtsVWDetailID, false);
            activityRtsSignatureBinding.tvNumberStatement.setText(getString(R.string.otp_has_been_sent_to_the_registered_mobile_number) + mobile_number.replaceAll("\\w(?=\\w{4})", "*"));
            signatureViewModel.getRtsShipmentsData(rtsVWDetailID);
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }

        imageHandler = new ImageHandler(RTSSignatureActivity.this) {
            @Override
            public void onBitmapReceived(Bitmap bitmap, String imageUri, ImageView imgView, String imageName, String imageCode, int pos, boolean verifyImage) {
                try {
                    if (CommonUtils.checkImageIsBlurryOrNot(RTSSignatureActivity.this, "RTS", bitmap, imageCaptureCount, signatureViewModel.getDataManager())) {
                        imageCaptureCount++;
                    } else {
                        capturedImageBitmap = bitmap;
                        capturedImageView = imgView;
                        if (isNetworkConnected()) {
                            signatureViewModel.uploadImageOnServer(imageName, imageUri, imageCode, rtsVWDetailID, RTSSignatureActivity.this);
                        } else {
                            signatureViewModel.saveImageInDBAndUpdateShipment(imageUri, imageCode, imageName, false, 0);
                        }
                        imageCaptureCount = 0;
                    }
                } catch (Exception e) {
                    showSnackbar(e.getMessage());
                }
            }
        };
        try {
            signatureViewModel.fetchRTSShipment(rtsVWDetailID);
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }

        activityRtsSignatureBinding.chSkip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            logButtonEventInGoogleAnalytics(TAG, getString(R.string.skipotprts), String.valueOf(isChecked), this);
            if (Boolean.TRUE.equals(signatureViewModel.ud_otp_verified_status.get()) || Objects.requireNonNull(signatureViewModel.ofd_otp_verify_status.get()).equalsIgnoreCase("VERIFIED")) {
                showErrorSnackbar(getString(R.string.otp_already_verified));
                activityRtsSignatureBinding.chSkip.setChecked(false);
            } else if (activityRtsSignatureBinding.resendOtpTv.getVisibility() == View.GONE) {
                showErrorSnackbar(getString(R.string.resend_otp_at_least_once));
                activityRtsSignatureBinding.chSkip.setChecked(false);
            } else if (!activityRtsSignatureBinding.resendOtpTv.isEnabled()) {
                activityRtsSignatureBinding.chSkip.setChecked(false);
            } else {
                if (isChecked) {
                    is_skip_OTP = true;
                    signatureViewModel.is_otp_verified = "false";
                    signatureViewModel.ud_otp_skip_status.set(true);
                    if (signatureViewModel.fromUDOtp) {
                        signatureViewModel.new_ud_otp_verify_status.set("SKIPPED");
                    } else {
                        signatureViewModel.ofd_otp_verify_status.set("SKIPPED");
                    }
                } else {
                    is_skip_OTP = false;
                    signatureViewModel.ud_otp_skip_status.set(false);
                    signatureViewModel.ofd_otp_verify_status.set("NONE");
                }
            }
        });
        itemList.add(1);
        rvAdapter = new MultiplePodAdapter(this, itemList, this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 8);
        activityRtsSignatureBinding.podImageRecyclerview.setLayoutManager(layoutManager);
        activityRtsSignatureBinding.podImageRecyclerview.setAdapter(rvAdapter);
        activityRtsSignatureBinding.increasePodImageCount.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (CHECKFIRSTUPLOAD == 0) {
                showSnackbar(getString(R.string.upload_this_image));
            } else {
                if (itemList.size() <= 19) {
                    if (Integer.parseInt(idPress) < itemList.size()) {
                        if (CHECKFIRSTUPLOAD == 1) {
                            if (Image_Check == 1) {
                                onAddButtonClicked();
                            } else {
                                showSnackbar(getString(R.string.image_can_not_be_added));
                            }
                        } else {
                            onAddButtonClicked();
                        }
                    } else {
                        onAddButtonClicked();
                    }
                } else {
                    showSnackbar(getString(R.string.maximum_limit_reached));
                }
            }
        });
    }

    private void setupClickListeners() {
        activityRtsSignatureBinding.generateOtpTv.setOnClickListener(v -> signatureViewModel.generateOTPForAll(this));
        activityRtsSignatureBinding.resendOtpTv.setOnClickListener(v -> resendClick());
        activityRtsSignatureBinding.verifyTv.setOnClickListener(v -> onVerifyClick());
        activityRtsSignatureBinding.clearSignaturePad.setOnClickListener(v -> onClear());
        activityRtsSignatureBinding.back.setOnClickListener(v -> backPress());
    }

    public void onAddButtonClicked() {
        try {
            int IntegerFormat = Integer.parseInt("1");
            itemList.add(IntegerFormat);
            rvAdapter.notifyItemInserted(itemList.size() - 1);
        } catch (NumberFormatException e) {
            showSnackbar(getString(R.string.the_field_is_empty));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        activityRtsSignatureBinding.signature.saveInstanceState();
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public RTSSignatureViewModel getViewModel() {
        return signatureViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_rts_signature;
    }

    @Override
    public void saveSignature() {
        rtsCommit.setCall_attempt_count(signatureViewModel.getDataManager().getRTSCallCount(rtsVWDetailID + "RTS"));
        rtsCommit.setMap_activity_count(signatureViewModel.getDataManager().getRTSMapCount(rtsVWDetailID));
        if (signatureViewModel.isAllPacketUndelivered || !signatureViewModel.forDisputedDelivery) {
            if (activityRtsSignatureBinding.llUdOtp.getVisibility() == View.VISIBLE) {
                if (signatureViewModel.ofd_otp_verify_status != null && (Objects.requireNonNull(signatureViewModel.ofd_otp_verify_status.get()).equalsIgnoreCase("SKIPPED") || Objects.requireNonNull(signatureViewModel.new_ud_otp_verify_status.get()).equalsIgnoreCase("SKIPPED"))) {
                    if (!signatureViewModel.forDisputedDelivery) {
                        if (Boolean.TRUE.equals(signatureViewModel.isImageCaptured.get())) {
                            saveBitmapInFile();
                        } else {
                            showErrorSnackbar(getString(R.string.capture_at_least_1_image_as_its_mandatory_when_otp_is_skipped));
                        }
                    } else {
                        saveBitmapInFile();
                    }
                } else if (signatureViewModel.ofd_otp_verify_status != null && (Objects.requireNonNull(signatureViewModel.ofd_otp_verify_status.get()).equalsIgnoreCase("VERIFIED") || Objects.requireNonNull(signatureViewModel.new_ud_otp_verify_status.get()).equalsIgnoreCase("VERIFIED"))) {
                    saveBitmapInFile();
                } else {
                    showSnackbar(getString(R.string.verify_otp_first));
                }
            } else {
                saveBitmapInFile();
            }
        } else if (activityRtsSignatureBinding.llUdOtp.getVisibility() == View.VISIBLE) {
            if (signatureViewModel.ofd_otp_verify_status != null && (Objects.requireNonNull(signatureViewModel.ofd_otp_verify_status.get()).equalsIgnoreCase("SKIPPED") || Objects.requireNonNull(signatureViewModel.new_ud_otp_verify_status.get()).equalsIgnoreCase("SKIPPED"))) {
                if (Boolean.TRUE.equals(signatureViewModel.isImageCaptured.get())) {
                    saveBitmapInFile();
                } else {
                    showErrorSnackbar(getString(R.string.capture_at_least_1_image_as_its_mandatory_when_otp_is_skipped));
                }
            } else if (signatureViewModel.ofd_otp_verify_status != null && (Objects.requireNonNull(signatureViewModel.ofd_otp_verify_status.get()).equalsIgnoreCase("VERIFIED") || Objects.requireNonNull(signatureViewModel.new_ud_otp_verify_status.get()).equalsIgnoreCase("VERIFIED"))) {
                saveBitmapInFile();
            } else {
                showSnackbar(getString(R.string.verify_otp_first));
            }
        } else if (Boolean.TRUE.equals(signatureViewModel.isImageCaptured.get())) {
            saveBitmapInFile();
        }
        else {
            showErrorSnackbar(getString(R.string.capture_at_least_1_image_as_its_mandatory_when_otp_is_skipped));
        }
    }

    private void backPress() {
        if (shouldAllowBack()) {
            finish();
            applyTransitionToBackFromActivity(this);
        }
    }

    public void onClear() {
        logButtonEventInGoogleAnalytics(TAG, getString(R.string.onclearsignaturepadrts), "", this);
        activityRtsSignatureBinding.signature.clear();
    }


    @Override
    public void onRTSItemFetched(Details details) {
        try {
            signatureViewModel.checkMeterRange(details);
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void setConsigneeDistance(int meter) {
        this.meterRange = meter;
    }

    @Override
    public void getAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_MaterialComponents_Light_Dialog_Alert);
        String AlertText1 = "Attention : ";
        builder.setMessage(AlertText1 + getString(R.string.commit_restriction_msg)).setCancelable(false).setPositiveButton("OK", (dialog, id) -> {
            dialog.cancel();
            startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void showOTPLayout(boolean isLayoutVisible) {
        if (isLayoutVisible) {
            activityRtsSignatureBinding.llUdOtp.setVisibility(View.VISIBLE);
        } else {
            activityRtsSignatureBinding.llUdOtp.setVisibility(View.GONE);
        }
    }

    @Override
    public String getOTP() {
        return activityRtsSignatureBinding.edtUdOtp.getText().toString();
    }

    @Override
    public void showMessage(String description) {
        hideKeyboard(this);
        showSnackbar(description);
    }

    @Override
    public void showRTSWidgets(String description) {
        if (description.equalsIgnoreCase("Otp Already Generated.")) {
            activityRtsSignatureBinding.chSkip.setEnabled(true);
        } else {
            activityRtsSignatureBinding.chSkip.setEnabled(false);
            showCounter();
        }
        activityRtsSignatureBinding.generateOtpTv.setVisibility(View.GONE);
        activityRtsSignatureBinding.resendOtpTv.setVisibility(View.VISIBLE);
        activityRtsSignatureBinding.verifyTv.setVisibility(View.VISIBLE);
        activityRtsSignatureBinding.edtUdOtp.setEnabled(true);
    }

    @Override
    public void setOTPVerify(boolean isOTPVerified, String description) {
        runOnUiThread(() -> {
            if (isOTPVerified) {
                activityRtsSignatureBinding.chSkip.setChecked(false);
                activityRtsSignatureBinding.imgVerifiedTick.setVisibility(View.VISIBLE);
            } else {
                activityRtsSignatureBinding.imgVerifiedTick.setVisibility(View.GONE);
            }
            showSnackbar(description);
        });
    }

    @Override
    public void onOtpResendSuccess(String description) {
        showSnackbar(description);
        if (description.contains(getString(R.string.otp_already_generated))) {
            activityRtsSignatureBinding.chSkip.setEnabled(true);
        } else {
            activityRtsSignatureBinding.chSkip.setEnabled(false);
            showCounter();
        }
        activityRtsSignatureBinding.generateOtpTv.setVisibility(View.GONE);
        activityRtsSignatureBinding.edtUdOtp.setEnabled(true);
        activityRtsSignatureBinding.resendOtpTv.setVisibility(View.VISIBLE);
        activityRtsSignatureBinding.verifyTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void doLogout(String description) {
        showToast(getString(R.string.session_expire));
        signatureViewModel.logoutLocal();
    }

    @Override
    public void onOtpVerifySuccess(String isOTPVerified) {
        runOnUiThread(() -> {
            if (isOTPVerified.equalsIgnoreCase("Verified")) {
                activityRtsSignatureBinding.chSkip.setChecked(false);
                activityRtsSignatureBinding.imgVerifiedTick.setVisibility(View.VISIBLE);
            } else {
                activityRtsSignatureBinding.imgVerifiedTick.setVisibility(View.GONE);
            }
        });
    }

    public void resendClick() {
        logButtonEventInGoogleAnalytics(TAG, getString(R.string.resendotpclickrts), "", this);
        if (signatureViewModel.isAllPacketUndelivered) {
            signatureViewModel.resendOTPApiCall(false, this);
        } else {
            if (signatureViewModel.getDataManager().getRtsInputResendFlag().equalsIgnoreCase("true") && signatureViewModel.getDataManager().getENABLERTSEMAIL().equalsIgnoreCase("true")) {
                signatureViewModel.showCallAndSmsDialog(this);
            } else {
                signatureViewModel.resendOTPApiCall(false, this);
            }
        }
    }

    public void onVerifyClick() {
        hideKeyboard();
        if (!activityRtsSignatureBinding.edtUdOtp.getText().toString().equalsIgnoreCase("")) {
            logButtonEventInGoogleAnalytics(TAG, getString(R.string.onverifyclickotprts), "", this);
            signatureViewModel.verifyOTPApiCall(activityRtsSignatureBinding.edtUdOtp.getText().toString(), this);
        } else {
            showMessage(getString(R.string.please_enter_otp));
        }
    }

    @Override
    public void hideKeyboard() {
        hideKeyboard(this);
    }

    @Override
    public void showCounter() {
        new CountDownTimer(signatureViewModel.getDataManager().getDisableResendOtpButtonDuration() * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                activityRtsSignatureBinding.resendOtpTv.setEnabled(false);
                activityRtsSignatureBinding.chSkip.setEnabled(false);
                activityRtsSignatureBinding.resendOtpTv.setTextColor(getResources().getColor(R.color.black));
                @SuppressLint("DefaultLocale") String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                System.out.println(hms);
                activityRtsSignatureBinding.resendOtpTv.setText(hms);
                activityRtsSignatureBinding.resendOtpTv.setTextColor(getResources().getColor(R.color.white));
            }

            @Override
            public void onFinish() {
                activityRtsSignatureBinding.resendOtpTv.setEnabled(true);
                activityRtsSignatureBinding.resendOtpTv.setText(getResources().getString(R.string.resend));
                activityRtsSignatureBinding.chSkip.setEnabled(true);
                activityRtsSignatureBinding.resendOtpTv.setTextColor(getResources().getColor(R.color.White));
            }
        }.start();
    }

    @Override
    public Context getActivity() {
        return this;
    }

    @Override
    public void showErrorSnackbar(String message) {
        runOnUiThread(() -> showSnackbar(message));
    }

    @Override
    public void setPODImageVisibility(Details details) {
        if (signatureViewModel.getDataManager().getRTSIMAGE().equalsIgnoreCase("true")) {
            activityRtsSignatureBinding.captureImageMandatory.setText(R.string.capture_image_rts);
            signatureViewModel.setImageRequired(true);
        } else if (signatureViewModel.getDataManager().getRTSIMAGE().equalsIgnoreCase("false") && !details.isIs_otp_required()) {
            activityRtsSignatureBinding.captureImageMandatory.setText(R.string.capture_image_rts);
            signatureViewModel.setImageRequired(true);
        } else if (signatureViewModel.getDataManager().getRTSIMAGE().equalsIgnoreCase("false") && details.isIs_otp_required()) {
            activityRtsSignatureBinding.captureImageMandatory.setText(R.string.capture_image);
            signatureViewModel.setImageRequired(false);
        }
    }

    @Override
    public void clearStack() {
        Intent intent = new Intent(RTSSignatureActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread() {
            public void run() {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    showSnackbar(e.getMessage());
                }
            }
        }.start();
    }

    @Override
    public void openSuccessActivity() {
        try {
            Intent intent = RTSSuccessActivity.getStartIntent(this);
            RTSActivitiesData rtsActivitiesData = new RTSActivitiesData();
            rtsActivitiesData.setAwb(awb);
            rtsActivitiesData.setAddress(address);
            rtsActivitiesData.setConsigneeName(rtsCommit.getConsigneeName());
            rtsActivitiesData.setRtsVWDetailID(rtsVWDetailID);
            rtsActivitiesData.setDecideNext(signatureViewModel.isAllPacketUndelivered ? Constants.FAIL : Constants.SUCCESS);
            intent.putExtra("rtsActivitiesData", rtsActivitiesData);
            startActivity(intent);
            applyTransitionToOpenActivity(this);
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        imageHandler.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveBitmapInFile() {
        try {
            File fileDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "/" + Constants.EcomExpress);
            if (!fileDir.exists())
                fileDir.mkdirs();
            File file = new File(fileDir, rtsVWDetailID + "_" + rtsVWDetailID + "_signature.png");
            if (file.exists()){
                file.delete();
                file.createNewFile();
            } else{
                file.createNewFile();
            }

            FileOutputStream ostream = new FileOutputStream(file);
            well = activityRtsSignatureBinding.signature.getSignatureBitmap();
            emptyBitmap = activityRtsSignatureBinding.signature.getWhiteBackground(well);
            ostream.close();
            if (well.sameAs(emptyBitmap)) {
                showSnackbar(getString(R.string.please_place_signature));
            } else {
                if (signatureViewModel.getCounterDeliveryRange() < signatureViewModel.getDataManager().getDCRANGE()) {
                    showSnackbar(getString(R.string.shipment_cannot_be_marked_undelivered_within_the_dc));
                    return;
                }
                String dialog_message;
                String positiveButtonText = getString(R.string.yes);
                if (meterRange > signatureViewModel.getDataManager().getUndeliverConsigneeRANGE()) {
                    dialog_message = getString(R.string.commitdialog_meter_away_mandatory);
                    positiveButtonText = getString(R.string.cancel);
                }
                else if (meterRange > signatureViewModel.getDataManager().getUndeliverConsigneeRANGE())
                    dialog_message = getString(R.string.commitdialog_meter_away);
                else {
                    dialog_message = getString(R.string.commitdialog);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_MaterialComponents_Light_Dialog_Alert);
                builder.setCancelable(false);
                rtsCommit.setLocation_verified(meterRange <= signatureViewModel.getDataManager().getUndeliverConsigneeRANGE());
                builder.setMessage(dialog_message);
                builder.setPositiveButton(positiveButtonText, (dialog, which) -> {
                    if (meterRange > signatureViewModel.getDataManager().getUndeliverConsigneeRANGE()) {
                        dialog.dismiss();
                        return;
                    }
                    BitmapUtils.saveBitmap(file, well);
                    signatureViewModel.saveCommit(rtsCommit);
                    signatureViewModel.saveImageInDBAndUpdateShipment(file.getAbsolutePath(), "signature", rtsVWDetailID + "_" + rtsVWDetailID + "_signature.png", true, 0);
                });
                if (!(meterRange > signatureViewModel.getDataManager().getUndeliverConsigneeRANGE())) {
                    builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel());
                }
                AlertDialog alert = builder.create();
                alert.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    private boolean shouldAllowBack() {
        try{
            if (activityRtsSignatureBinding.resendOtpTv.getText().toString().equalsIgnoreCase("RESEND")) {
                if (Objects.requireNonNull(signatureViewModel.ofd_otp_verify_status.get()).equalsIgnoreCase("VERIFIED") || Objects.requireNonNull(signatureViewModel.new_ud_otp_verify_status.get()).equalsIgnoreCase("VERIFIED")) {
                    showSnackbar(getString(R.string.can_t_move_back));
                    return false;
                } else {
                    return true;
                }
            } else {
                showSnackbar(getString(R.string.backbutton_is_disabled_until_the_timer_is_off));
                return false;
            }
        } catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
            return false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        well = activityRtsSignatureBinding.signature.getSignatureBitmap();
    }

    @Override
    protected void onStop() {
        super.onStop();
        well = activityRtsSignatureBinding.signature.getSignatureBitmap();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPODClick(String idPress) {
        this.idPress = idPress;
        activityRtsSignatureBinding.increasePodImageCount.setVisibility(View.VISIBLE);
    }
}