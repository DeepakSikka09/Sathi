package in.ecomexpress.sathi.ui.drs.forward.obd.activity;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityObdOtpVerificationShipmentBinding;
import in.ecomexpress.sathi.databinding.BottomsheetMobileNumberBinding;
import in.ecomexpress.sathi.repo.local.data.activitiesdata.FWDActivitiesData;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.drs.forward.obd.navigator.IObdOTPNavigator;
import in.ecomexpress.sathi.ui.drs.forward.obd.viewmodel.OBDStopOTPViewModel;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.ImageHandler;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class FwdOBDStopOTPActivity extends BaseActivity<ActivityObdOtpVerificationShipmentBinding, OBDStopOTPViewModel> implements IObdOTPNavigator {

    private final String TAG = FwdOBDStopOTPActivity.class.getSimpleName();
    BottomsheetMobileNumberBinding bottomsheetMobileNumberBinding;
    BottomSheetDialog bottomSheetDialog;
    private ColorStateList colorWhite;
    private ColorStateList colorBlue;
    private ColorStateList listBlue;
    private ColorStateList listGray;
    ActivityObdOtpVerificationShipmentBinding binding;
    private boolean isUndelivered = false;
    private boolean isQcFailedFlag = false;
    @Inject
    OBDStopOTPViewModel obdStopOtpViewModel;
    ForwardCommit forwardCommit;
    private String consigneeName;
    private String compositeKey = "";
    private boolean isCheckedBox = false;
    CountDownTimer mCountDownTimer = null;
    public String awbNumber;
    private String consigneeMobile;
    private String consigneeAlternateMobile;
    public String drsIdNum;
    private String returnPackageBarcode;
    public List<String> qcCode;
    public List<String> qualityCheckName;
    private String otpText = "";
    private ImageHandler imageHandler;
    private String orderId;
    private Bitmap imgBitmap;
    private String DRS_DATE = "";
    private String SHIPMENT_DECLARED_VALUE = "";
    public List<String> qcFailedData;
    private String OBD_REFUSED = "No";
    private String awbScanMethod;
    private boolean isFlyerImage = false;
    private final LinkedHashMap<String, String> qcStatusData = new LinkedHashMap<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewDataBinding();
        obdStopOtpViewModel.setNavigator(this);
        getAllDataFromIntent();
        logScreenNameInGoogleAnalytics(TAG, this);

        compositeKey = drsIdNum + awbNumber;
        awbNumber = (awbNumber == null) ? "" : awbNumber;
        drsIdNum = (drsIdNum == null) ? "" : drsIdNum;
        binding.awbHeader.awb.setText("AWB: " + awbNumber);

        binding.ed1.addTextChangedListener(new GenericTextWatcher(binding.ed1, binding.ed2));
        binding.ed2.addTextChangedListener(new GenericTextWatcher(binding.ed2, binding.ed3));
        binding.ed3.addTextChangedListener(new GenericTextWatcher(binding.ed3, binding.ed4));
        binding.ed4.addTextChangedListener(new GenericTextWatcher(binding.ed4, null));

        binding.ed1.setOnKeyListener(new OTPKeyListener(null, binding.ed1));
        binding.ed2.setOnKeyListener(new OTPKeyListener(binding.ed1, binding.ed2));
        binding.ed3.setOnKeyListener(new OTPKeyListener(binding.ed2, binding.ed3));
        binding.ed4.setOnKeyListener(new OTPKeyListener(binding.ed3, binding.ed4));

        binding.btnDeliver.setSelected(false);
        binding.btnUndeliver.setSelected(false);

        colorWhite = ColorStateList.valueOf(getResources().getColor(R.color.white));
        colorBlue = ColorStateList.valueOf(getResources().getColor(R.color.green_FF));
        listBlue = ColorStateList.valueOf(getResources().getColor(R.color.ecomBlue));
        listGray = ColorStateList.valueOf(getResources().getColor(R.color.grey_78));

        setNextButton(listGray, getResources().getString(R.string.verify_code), false);

        initialization();
        if (isCheckedBox && isUndelivered) {
            obdStopOtpViewModel.generateCtsOtp(true);
            binding.txtPleaseAsk.setText("Please ask the Central Team  to share the OTP code on mobile number" + consigneeMobile);

        } else if (isUndelivered && isQcFailedFlag) {
            obdStopOtpViewModel.generateFailedOtp(true);


        } else if (isUndelivered) {
            obdStopOtpViewModel.generateStopOtp(true);
        }


        binding.checkbox.setOnClickListener(view -> {
            if (binding.checkbox.isChecked()) {
                if (isUndelivered || isQcFailedFlag) {
                    setNextButton(listBlue, getResources().getString(R.string.send_code_to_CT), true);
                } else {
                    setNextButton(listBlue, getResources().getString(R.string.mark_undeliver), true);
                }
                isCheckedBox = true;
            } else {
                if (otpText.length() == 4) {
                    setNextButton(listBlue, getResources().getString(R.string.verify_code), true);
                } else {
                    setNextButton(listGray, getResources().getString(R.string.verify_code), false);
                }
                isCheckedBox = false;
            }
        });

        binding.btnDeliver.setOnClickListener(v -> {
            binding.btnUndeliver.setBackgroundTintList(colorWhite);
            binding.btnDeliver.setBackgroundTintList(colorBlue);
            setVisibility(View.VISIBLE);
            binding.imageCard.setVisibility(View.GONE);
            setNextButton(listGray, "Verify Code", false);
            obdStopOtpViewModel.generatePassOtp();
            isUndelivered = false;
            binding.checkbox.setChecked(false);

        });

        binding.btnUndeliver.setOnClickListener(v -> {
            binding.btnUndeliver.setBackgroundTintList(colorBlue);
            binding.btnDeliver.setBackgroundTintList(colorWhite);
            setVisibility(View.GONE);
            setNextBtnWithUndelivered(listBlue, getResources().getString(R.string.mark_undeliver));
            isUndelivered = true;
        });

        binding.btnVerify.setOnClickListener(view -> {
            String buttonText = binding.btnVerify.getText().toString();
            if (buttonText.equals(getResources().getString(R.string.mark_undeliver))) {
                Intent intent = new Intent(FwdOBDStopOTPActivity.this, FwdOBDScannerActivity.class);
                isUndelivered = true;
                sendDataForOtherActivity(intent, true);
            } else if (buttonText.equals(getResources().getString(R.string.send_code_to_CT))) {
                updateViews();
            } else if (buttonText.equals(getResources().getString(R.string.verify_code))) {
                obdStopOtpViewModel.getOtpVerifyCall();
            }
        });

        binding.txtRecieveCode.setOnClickListener(view -> {
            if (consigneeAlternateMobile == null || consigneeAlternateMobile.isEmpty()) {
                if (isCheckedBox && isUndelivered) {
                    obdStopOtpViewModel.generateCtsOtp(false);
                } else if (isUndelivered && isQcFailedFlag) {
                    obdStopOtpViewModel.generateFailedOtp(false);

                } else if (isUndelivered) {
                    obdStopOtpViewModel.generateStopOtp(false);

                } else {
                    obdStopOtpViewModel.generatePassOtpResend();
                }
            } else {
                openBottomSheet();
            }
        });

        binding.icCallIcon.setOnClickListener(view -> {
            if (isUndelivered && isCheckedBox) {
                obdStopOtpViewModel.generateVoiceOtp(Constants.OBD_CENTRAL_CODE);
            } else if (isUndelivered) {
                obdStopOtpViewModel.generateVoiceOtp(Constants.OBD_STOP_TYPE);
            } else if (isQcFailedFlag) {
                obdStopOtpViewModel.generateVoiceOtp(Constants.OBD_FAIL_TYPE);
            } else {
                obdStopOtpViewModel.generateVoiceOtp(Constants.OBD_PASS_TYPE);
            }
        });

        imageHandler = new ImageHandler(this) {
            @Override
            public void onBitmapReceived(final Bitmap bitmap, String imageUri, ImageView imgView, String imageName, String imageCode, int pos, boolean verifyImage) {
                try {
                    if (isNetworkConnected()) {
                        imgBitmap = bitmap;
                        obdStopOtpViewModel.uploadImageToServer(imageName, imageUri, imageCode, Long.parseLong(awbNumber), Integer.parseInt(drsIdNum));
                    } else {
                        showError("Image Not Uploaded, Check Your Internet Connection");
                    }
                } catch (Exception e) {
                    Logger.e(FwdOBDQcPassActivity.class.getSimpleName(), e.getMessage());
                }
            }
        };
    }

    @SuppressLint("SetTextI18n")
    private void updateViews() {
        binding.constMarkShipment.setVisibility(View.VISIBLE);
        binding.otpSentCard.setVisibility(View.VISIBLE);
        binding.deliverCard.setVisibility(View.VISIBLE);
        binding.imageCard.setVisibility(View.VISIBLE);
        binding.view3.setVisibility(View.VISIBLE);
        binding.view4.setVisibility(View.GONE);
        binding.detailCard.setVisibility(View.GONE);
        binding.view2.setVisibility(View.GONE);
        binding.customerNotCard.setVisibility(View.GONE);
        binding.txtPleaseAsk.setText("Please Ask the Central Team to share the OTP code");
        setNextButton(listGray, getResources().getString(R.string.verify_code), false);
        obdStopOtpViewModel.generateCtsOtp(true);
    }

    @Override
    public OBDStopOTPViewModel getViewModel() {
        return obdStopOtpViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_obd_otp_verification_shipment;
    }

    @SuppressLint("SetTextI18n")
    private void initialization() {
        binding.awbHeader.backArrow.getDrawable().setTint(getResources().getColor(R.color.grey_F4));
        this.getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        });
        binding.txtPleaseAsk.setText(getResources().getString(R.string.please_ask_the_consignee_to_share_the_otp_code_sent_on_mobile_number_789xxxx999) + consigneeMobile);
    }

    private void setNextButton(ColorStateList colorStateList, String text, boolean toEnable) {
        binding.btnVerify.setBackgroundTintList(colorStateList);
        binding.btnVerify.setText(text);
        binding.btnVerify.setEnabled(toEnable);
    }

    private void setNextBtnWithUndelivered(ColorStateList colorStateList, String text) {
        binding.btnVerify.setBackgroundTintList(colorStateList);
        binding.btnVerify.setText(text);
        binding.btnVerify.setEnabled(true);
    }

    private void setVisibility(int visibility) {
        binding.view2.setVisibility(visibility);
        binding.deliverCard.setVisibility(visibility);
        binding.customerNotCard.setVisibility(visibility);
        binding.imageCard.setVisibility(visibility);
    }

    private void openBottomSheet() {
        try {
            bottomsheetMobileNumberBinding = BottomsheetMobileNumberBinding.inflate(getLayoutInflater());
            bottomSheetDialog = new BottomSheetDialog(this, R.style.otpsheetDialogTheme);
            bottomSheetDialog.setContentView(bottomsheetMobileNumberBinding.getRoot());
            bottomsheetMobileNumberBinding.mobilePrimaryCard.setOnClickListener(view -> obdStopOtpViewModel.getOtpOnAlternate(false));
            bottomsheetMobileNumberBinding.mobileSeconadryCard.setOnClickListener(view -> obdStopOtpViewModel.getOtpOnAlternate(true));
            bottomSheetDialog.setCanceledOnTouchOutside(true);
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) bottomsheetMobileNumberBinding.getRoot().getParent());
            behavior.setHideable(true);
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            if (bottomSheetDialog != null && !bottomSheetDialog.isShowing()) {
                bottomSheetDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onOtpResendSuccess(String description) {
        showSnackbar(description);
    }

    @Override
    public void onOtpVerifySuccess(String description, String messageType) {
        if (description.equalsIgnoreCase("Verified")) {
            if (messageType.equals(Constants.OBD_PASS_TYPE)) {
                doShipmentCommit(qcCode);
            } else if (isUndelivered || isQcFailedFlag || isCheckedBox) {
                doShipmentUnDelivered(qcCode, qcFailedData, qualityCheckName);
            }
        }
    }

    public void sendDataForOtherActivity(Intent intent, boolean flag) {
        intent.putExtra("flag", flag);
        intent.putExtra("isUndelivered", isUndelivered);
        intent.putExtra("isCheckedbox", isCheckedBox);
        intent.putExtra(Constants.OBD_AWB_NUMBER, awbNumber);
        intent.putExtra(Constants.return_package_barcode, returnPackageBarcode);
        intent.putExtra(Constants.COMPOSITE_KEY, compositeKey);
        intent.putExtra(Constants.OBD_AWB_NUMBER, awbNumber);
        intent.putExtra(Constants.CONSIGNEE_MOBILE, consigneeMobile);
        intent.putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, consigneeAlternateMobile);
        intent.putExtra(Constants.ORDER_ID, orderId);
        intent.putExtra(Constants.DRS_ID_NUM, drsIdNum);
        intent.putExtra(Constants.DRS_DATE, DRS_DATE);
        intent.putStringArrayListExtra("QC_FAILED_DATA", (ArrayList<String>) qcFailedData);
        intent.putStringArrayListExtra("QC_CODE", (ArrayList<String>) qcCode);
        intent.putExtra(Constants.SHIPMENT_DECLARED_VALUE, SHIPMENT_DECLARED_VALUE);
        intent.putExtra(getString(R.string.data), forwardCommit);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    @Override
    public void onHandleError(String errorResponse) {
    }

    @Override
    public void showErrorMessage(String error) {
    }

    @Override
    public void onGenerateOtpClick() {
        hideKeyboard(this);
        obdStopOtpViewModel.onGenerateOtpApiCall(FwdOBDStopOTPActivity.this, awbNumber, drsIdNum, false, Constants.OBD_PASS_TYPE, true);
    }

    @Override
    public void onGenerateOtpResendClick() {
        hideKeyboard(this);
        obdStopOtpViewModel.onGenerateOtpApiCall(FwdOBDStopOTPActivity.this, awbNumber, drsIdNum, false, Constants.OBD_PASS_TYPE, false);
    }

    @Override
    public void showError(String error) {
        showSnackbar(error);
    }

    @Override
    public void doLogout(String description) {
        showToast(getString(R.string.session_expire));
        obdStopOtpViewModel.logoutLocal();
    }

    @Override
    public void onResendClick() {
    }

    @Override
    public void resendSms(boolean alternateClick) {
        bottomSheetDialog.dismiss();
        if (isCheckedBox && isUndelivered) {
            obdStopOtpViewModel.onGenerateOtpApiCall(FwdOBDStopOTPActivity.this, String.valueOf(awbNumber), drsIdNum, alternateClick, Constants.OBD_CENTRAL_CODE, false);
        } else if (isUndelivered) {
            obdStopOtpViewModel.onGenerateOtpApiCall(FwdOBDStopOTPActivity.this, String.valueOf(awbNumber), drsIdNum, alternateClick, Constants.OBD_STOP_TYPE, false);
        } else if (isQcFailedFlag) {
            obdStopOtpViewModel.onGenerateOtpApiCall(FwdOBDStopOTPActivity.this, String.valueOf(awbNumber), drsIdNum, alternateClick, Constants.OBD_FAIL_TYPE, false);
        } else {
            obdStopOtpViewModel.onGenerateOtpApiCall(FwdOBDStopOTPActivity.this, String.valueOf(awbNumber), drsIdNum, alternateClick, Constants.OBD_PASS_TYPE, false);
        }
    }

    @Override
    public void VoiceCallOtp(String otpType) {
        hideKeyboard(this);
        obdStopOtpViewModel.doVoiceOTPApi(String.valueOf(awbNumber), drsIdNum, otpType);
    }

    @Override
    public void onGenerateStopOtp(boolean isGenerate) {
        hideKeyboard(this);
        obdStopOtpViewModel.onGenerateOtpApiCall(FwdOBDStopOTPActivity.this, awbNumber, drsIdNum, false, Constants.OBD_STOP_TYPE, isGenerate);
    }

    @Override
    public void onGenerateQcFailOtp(boolean isGenerate) {
        hideKeyboard(this);
        obdStopOtpViewModel.onGenerateOtpApiCall(FwdOBDStopOTPActivity.this, awbNumber, drsIdNum, false, Constants.OBD_FAIL_TYPE, isGenerate);
    }

    @Override
    public void onGenerateCtsOtp(boolean isGenerate) {
        hideKeyboard(this);
        obdStopOtpViewModel.onGenerateOtpApiCall(FwdOBDStopOTPActivity.this, awbNumber, drsIdNum, false, Constants.OBD_CENTRAL_CODE, isGenerate);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void voiceTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = new CountDownTimer(obdStopOtpViewModel.getDataManager().getDisableResendOtpButtonDuration() * 1000, 1000) {
                @SuppressLint("SetTextI18n")
                @Override
                public void onTick(long millisUntilFinished) {
                    binding.txtRecieveCode.setVisibility(View.GONE);
                    String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                    System.out.println(hms);
                    binding.tvResendTimer.setVisibility(View.VISIBLE);
                    binding.tvResendTimer.setText("Didn’t receive a code (" + hms + ")");
                }

                @Override
                public void onFinish() {
                    binding.txtRecieveCode.setVisibility(View.VISIBLE);
                    binding.tvResendTimer.setVisibility(View.GONE);
                }
            };
            mCountDownTimer.start();
        } else {
            mCountDownTimer = new CountDownTimer(obdStopOtpViewModel.getDataManager().getDisableResendOtpButtonDuration() * 1000, 1000) {
                @SuppressLint("SetTextI18n")
                @Override
                public void onTick(long millisUntilFinished) {
                    binding.txtRecieveCode.setVisibility(View.GONE);
                    @SuppressLint("DefaultLocale") String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                    System.out.println(hms);
                    binding.tvResendTimer.setVisibility(View.VISIBLE);
                    binding.tvResendTimer.setText("Didn’t receive a code (" + hms + ")");
                }

                @Override
                public void onFinish() {
                    binding.txtRecieveCode.setVisibility(View.VISIBLE);
                    binding.tvResendTimer.setVisibility(View.GONE);
                    binding.checkbox.setEnabled(true);
                }
            };
            mCountDownTimer.start();
        }
    }

    @Override
    public void onVerifyClick() {
        hideKeyboard(this);
        if (isCheckedBox && isUndelivered) {
            obdStopOtpViewModel.onVerifyApiCall(FwdOBDStopOTPActivity.this, String.valueOf(awbNumber), otpText, drsIdNum, Constants.OBD_CENTRAL_CODE);
        } else if (isUndelivered && isQcFailedFlag) {
            obdStopOtpViewModel.onVerifyApiCall(FwdOBDStopOTPActivity.this, String.valueOf(awbNumber), otpText, drsIdNum, Constants.OBD_FAIL_TYPE);

        } else if (isUndelivered) {
            obdStopOtpViewModel.onVerifyApiCall(FwdOBDStopOTPActivity.this, String.valueOf(awbNumber), otpText, drsIdNum, Constants.OBD_STOP_TYPE);
        } else {
            obdStopOtpViewModel.onVerifyApiCall(FwdOBDStopOTPActivity.this, String.valueOf(awbNumber), otpText, drsIdNum, Constants.OBD_PASS_TYPE);
        }
    }

    @Override
    public Context getActivityContext() {
        return FwdOBDStopOTPActivity.this;
    }

    @Override
    public void clearStack() {
        Intent intent = new Intent(FwdOBDStopOTPActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    public void doShipmentUnDelivered(List<String> qcCode, List<String> qcFailedData, List<String> qualityCheckName) {
        if (obdStopOtpViewModel.getDCFEDistance()) {
            forwardCommit.setChange_received_confirmation("Y");
            forwardCommit.setOfd_customer_otp(Constants.PLAIN_OTP);
            forwardCommit.setOfd_otp_verified(String.valueOf(Constants.OFD_OTP_VERIFIED));
            forwardCommit.setShipment_type(Constants.SHIPMENT_TYPE_FORWARD);
            forwardCommit.setScanable_by("Scan");
            forwardCommit.setReceived_by_name(consigneeName);
            forwardCommit.setConsignee_name(consigneeName);
            forwardCommit.setShipment_id(obdStopOtpViewModel.getDataManager().getShipperId());
            forwardCommit.setStatus(Constants.UNDELIVERED);
            forwardCommit.setCall_attempt_count(obdStopOtpViewModel.getDataManager().getForwardCallCount(awbNumber + "FWD"));
            forwardCommit.setMap_activity_count(obdStopOtpViewModel.getDataManager().getForwardMapCount(Long.parseLong(awbNumber)));
            forwardCommit.setTrying_reach(String.valueOf(obdStopOtpViewModel.getDataManager().getTryReachingCount(awbNumber + Constants.TRY_RECHING_COUNT)));
            forwardCommit.setTechpark(String.valueOf(obdStopOtpViewModel.getDataManager().getSendSmsCount(awbNumber + Constants.TECH_PARK_COUNT)));
            forwardCommit.setReceived_by_relation("Self");
            forwardCommit.setAttempt_reason_code("999");
            forwardCommit.setAttempt_type("FWD");
            forwardCommit.setDrs_commit_date_time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            forwardCommit.setTrip_id(obdStopOtpViewModel.getDataManager().getTripId());
            forwardCommit.setFe_emp_code(obdStopOtpViewModel.getDataManager().getCode());
            forwardCommit.setDrs_id(drsIdNum);
            forwardCommit.setAwb(awbNumber);
            forwardCommit.setFe_emp_code(obdStopOtpViewModel.getDataManager().getEmp_code());
            forwardCommit.setIs_obd(true);
            forwardCommit.setOfd_customer_otp("");
            forwardCommit.setOfd_otp_verified("True");
            if (SHIPMENT_DECLARED_VALUE == null) {
                forwardCommit.setDeclared_value("");
            } else {
                forwardCommit.setDeclared_value(SHIPMENT_DECLARED_VALUE);
            }
            if (DRS_DATE == null) {
                forwardCommit.setDrs_date(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            } else {
                forwardCommit.setDrs_date(DRS_DATE);
            }
            if (returnPackageBarcode == null || returnPackageBarcode.isEmpty()) {
                forwardCommit.setReturn_packaging_barcode("");
            } else {
                forwardCommit.setReturn_packaging_barcode(returnPackageBarcode.replaceAll("[^a-zA-Z0-9]", ""));
            }
            if (obdStopOtpViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("W")) {
                forwardCommit.setFlag_of_warning("W");
            } else {
                forwardCommit.setFlag_of_warning("N");
            }
            if (obdStopOtpViewModel.getDataManager().getDlightSuccessEncrptedOTPType() != null && !obdStopOtpViewModel.getDataManager().getDlightSuccessEncrptedOTPType().equalsIgnoreCase("")) {
                forwardCommit.setDlight_encrption_otp_type(obdStopOtpViewModel.getDataManager().getDlightSuccessEncrptedOTPType());
            }
            try {
                if (!String.valueOf(in.ecomexpress.geolocations.Constants.latitude).equalsIgnoreCase("0.0") && !String.valueOf(in.ecomexpress.geolocations.Constants.longitude).equalsIgnoreCase("0.0")) {
                    forwardCommit.setLocation_lat(String.valueOf(in.ecomexpress.geolocations.Constants.latitude));
                    forwardCommit.setLocation_long(String.valueOf(in.ecomexpress.geolocations.Constants.longitude));
                } else if (!Constants.CURRENT_LATITUDE.equalsIgnoreCase("0.0") && !Constants.CURRENT_LONGITUDE.equalsIgnoreCase("0.0")) {
                    forwardCommit.setLocation_lat(Constants.CURRENT_LATITUDE);
                    forwardCommit.setLocation_long(Constants.CURRENT_LONGITUDE);
                } else {
                    forwardCommit.setLocation_lat(String.valueOf(obdStopOtpViewModel.getDataManager().getCurrentLatitude()));
                    forwardCommit.setLocation_long(String.valueOf(obdStopOtpViewModel.getDataManager().getCurrentLongitude()));
                }
            } catch (Exception e) {
                Logger.e("OBD Commit->doShipmentUnCommit", String.valueOf(e));
            }
            obdStopOtpViewModel.getQcItemData(forwardCommit, awbNumber, qcCode, qcFailedData, qualityCheckName);
        }
    }

    public void doShipmentCommit(List<String> qcCode) {
        if (obdStopOtpViewModel.getDCFEDistance()) {
            forwardCommit.setChange_received_confirmation("Y");
            forwardCommit.setOfd_customer_otp(Constants.PLAIN_OTP);
            forwardCommit.setOfd_otp_verified(String.valueOf(Constants.OFD_OTP_VERIFIED));
            forwardCommit.setShipment_type(Constants.SHIPMENT_TYPE_FORWARD);
            forwardCommit.setScanable_by(awbScanMethod);
            forwardCommit.setConsignee_name(consigneeName);
            forwardCommit.setReceived_by_name(consigneeName);
            forwardCommit.setShipment_id(obdStopOtpViewModel.getDataManager().getShipperId());
            forwardCommit.setStatus(Constants.DELIVERED);
            forwardCommit.setCall_attempt_count(obdStopOtpViewModel.getDataManager().getForwardCallCount(awbNumber + "FWD"));
            forwardCommit.setMap_activity_count(obdStopOtpViewModel.getDataManager().getForwardMapCount(Long.parseLong(awbNumber)));
            forwardCommit.setTrying_reach(String.valueOf(obdStopOtpViewModel.getDataManager().getTryReachingCount(awbNumber + Constants.TRY_RECHING_COUNT)));
            forwardCommit.setTechpark(String.valueOf(obdStopOtpViewModel.getDataManager().getSendSmsCount(awbNumber + Constants.TECH_PARK_COUNT)));
            forwardCommit.setReceived_by_relation("Self");
            forwardCommit.setAttempt_reason_code("999");
            forwardCommit.setAttempt_type("FWD");
            forwardCommit.setDrs_commit_date_time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            forwardCommit.setTrip_id(obdStopOtpViewModel.getDataManager().getTripId());
            forwardCommit.setDrs_id(drsIdNum);
            forwardCommit.setAwb(awbNumber);
            forwardCommit.setFe_emp_code(obdStopOtpViewModel.getDataManager().getEmp_code());
            forwardCommit.setIs_obd(true);
            forwardCommit.setOfd_customer_otp("");
            forwardCommit.setOfd_otp_verified("True");
            if (SHIPMENT_DECLARED_VALUE == null) {
                forwardCommit.setDeclared_value("");
            } else {
                forwardCommit.setDeclared_value(SHIPMENT_DECLARED_VALUE);
            }
            if (DRS_DATE == null) {
                forwardCommit.setDrs_date(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            } else {
                forwardCommit.setDrs_date(DRS_DATE);
            }
            if (returnPackageBarcode == null || returnPackageBarcode.isEmpty()) {
                forwardCommit.setReturn_packaging_barcode("");
            } else {
                forwardCommit.setReturn_packaging_barcode(returnPackageBarcode.replaceAll("[^a-zA-Z0-9]", ""));
            }
            if (obdStopOtpViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("W")) {
                forwardCommit.setFlag_of_warning("W");
            } else {
                forwardCommit.setFlag_of_warning("N");
            }
            if (obdStopOtpViewModel.getDataManager().getDlightSuccessEncrptedOTPType() != null && !obdStopOtpViewModel.getDataManager().getDlightSuccessEncrptedOTPType().equalsIgnoreCase("")) {
                forwardCommit.setDlight_encrption_otp_type(obdStopOtpViewModel.getDataManager().getDlightSuccessEncrptedOTPType());
            }
            try {
                if (!String.valueOf(in.ecomexpress.geolocations.Constants.latitude).equalsIgnoreCase("0.0") && !String.valueOf(in.ecomexpress.geolocations.Constants.longitude).equalsIgnoreCase("0.0")) {
                    forwardCommit.setLocation_lat(String.valueOf(in.ecomexpress.geolocations.Constants.latitude));
                    forwardCommit.setLocation_long(String.valueOf(in.ecomexpress.geolocations.Constants.longitude));
                } else if (!Constants.CURRENT_LATITUDE.equalsIgnoreCase("0.0") && !Constants.CURRENT_LONGITUDE.equalsIgnoreCase("0.0")) {
                    forwardCommit.setLocation_lat(Constants.CURRENT_LATITUDE);
                    forwardCommit.setLocation_long(Constants.CURRENT_LONGITUDE);
                } else {
                    forwardCommit.setLocation_lat(String.valueOf(obdStopOtpViewModel.getDataManager().getCurrentLatitude()));
                    forwardCommit.setLocation_long(String.valueOf(obdStopOtpViewModel.getDataManager().getCurrentLongitude()));
                }
            } catch (Exception e) {
                Logger.e("OBD Commit->doShipmentCommit", String.valueOf(e));
            }
            obdStopOtpViewModel.setQcImageDataForCommit(FwdOBDStopOTPActivity.this, forwardCommit, awbNumber, compositeKey, qcCode, qcStatusData);
        }
    }

    @Override
    public String getCompositeKey() {
        if (compositeKey != null && !compositeKey.equalsIgnoreCase("")) {
            return compositeKey;
        } else {
            return "";
        }
    }

    @Override
    public void navigateToSuccessActivity() {
        Intent intent = new Intent(FwdOBDStopOTPActivity.this, FwdOBDSucessActivity.class);
        sendDataForOtherActivity(intent, false);
    }

    @Override
    public void onBack() {
    }

    @Override
    public void onUndelivered(ForwardCommit forwardCommit) {
        try {
            if (qcFailedData == null || qcFailedData.isEmpty()) {
                OBD_REFUSED = "Yes";
            }
            Intent intent = new Intent();
            FWDActivitiesData fwdActivitiesData = new FWDActivitiesData();
            intent = new Intent(FwdOBDStopOTPActivity.this, FwdOBDCompleteActivity.class);
            fwdActivitiesData.setReturn_package_barcode(returnPackageBarcode);
            fwdActivitiesData.setOrderId(orderId);
            fwdActivitiesData.setCallAllowed(false);
            fwdActivitiesData.setConsignee_mobile(consigneeMobile);
            fwdActivitiesData.setConsignee_alternate_number(consigneeAlternateMobile);
            fwdActivitiesData.setDrsId(Integer.parseInt(drsIdNum));
            fwdActivitiesData.setAwbNo(Long.parseLong(awbNumber));
            fwdActivitiesData.setCompositeKey(compositeKey);
            fwdActivitiesData.setIs_amazon_reschedule_enabled(false);
            fwdActivitiesData.setShipment_type("FWD");
            fwdActivitiesData.setCollected_value(SHIPMENT_DECLARED_VALUE);
            intent.putExtra(getString(R.string.data), forwardCommit);
            intent.putExtra(Constants.OBD_CONSIGNEE_NAME, consigneeName);
            intent.putExtra(Constants.OBD_REFUSED, OBD_REFUSED);
            intent.putExtra("fwdActivitiesData", fwdActivitiesData);
            startActivity(intent);
            applyTransitionToOpenActivity(this);
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    class GenericTextWatcher implements TextWatcher {

        private final EditText currentEditText;
        private final EditText nextEditText;

        public GenericTextWatcher(EditText currentEditText, EditText nextEditText) {
            this.currentEditText = currentEditText;
            this.nextEditText = nextEditText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (currentEditText.getText().toString().length() == 1 && nextEditText != null) {
                nextEditText.requestFocus();
            }
            if (otpText.isEmpty()) {
                binding.checkbox.setEnabled(true);
                binding.checkbox.setChecked(false);
            } else {
                binding.checkbox.setEnabled(false);
                binding.checkbox.setChecked(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            setOtpText();
            if (otpText.isEmpty()) {
                binding.checkbox.setEnabled(true);
                setNextButton(listGray, getResources().getString(R.string.mark_undeliver), false);
            } else if (binding.checkbox.isChecked()) {
                binding.checkbox.setEnabled(false);
                binding.checkbox.setChecked(false);
                setNextButton(listBlue, getResources().getString(R.string.verify_code), false);
            } else if (otpText.length() == 4) {
                binding.checkbox.setEnabled(false);
                binding.checkbox.setChecked(false);
                if (isFlyerImage && imgBitmap == null) {
                    setNextButton(listGray, getResources().getString(R.string.verify_code), false);
                } else {
                    setNextButton(listBlue, getResources().getString(R.string.verify_code), true);
                }
            }
        }
    }

    static class OTPKeyListener implements View.OnKeyListener {

        private final EditText previousView;
        private final EditText currentView;

        public OTPKeyListener(EditText previousView, EditText currentView) {
            this.previousView = previousView;
            this.currentView = currentView;
        }

        @Override
        public boolean onKey(View view, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (currentView.getText().length() == 0 && previousView != null) {
                    previousView.requestFocus();
                    previousView.setText("");
                }
            }
            return false;
        }
    }

    private void setOtpText() {
        otpText = binding.ed1.getText().toString() + binding.ed2.getText() + binding.ed3.getText() + binding.ed4.getText();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showSnackbar("You Can Not Move Back");
    }

    public void setViews() {
        if (isUndelivered && isCheckedBox) {
            binding.otpSentCard.setVisibility(View.VISIBLE);
            binding.deliverCard.setVisibility(View.VISIBLE);
            binding.imageCard.setVisibility(View.VISIBLE);
            binding.view3.setVisibility(View.VISIBLE);
            binding.view4.setVisibility(View.GONE);
            binding.detailCard.setVisibility(View.GONE);
            binding.view2.setVisibility(View.GONE);
        } else if (isUndelivered || isQcFailedFlag) {
            binding.deliverCard.setVisibility(View.VISIBLE);
            binding.imageCard.setVisibility(View.VISIBLE);
            binding.customerNotCard.setVisibility(View.VISIBLE);
            binding.otpSentCard.setVisibility(View.GONE);
            binding.constMarkShipment.setVisibility(View.GONE);
            binding.view3.setVisibility(View.VISIBLE);
            binding.view4.setVisibility(View.VISIBLE);
        } else {
            binding.constMarkShipment.setVisibility(View.VISIBLE);
            binding.deliverCard.setVisibility(View.GONE);
            binding.imageCard.setVisibility(View.GONE);
            binding.customerNotCard.setVisibility(View.GONE);
            binding.otpSentCard.setVisibility(View.GONE);
            binding.view3.setVisibility(View.GONE);
            binding.view4.setVisibility(View.GONE);
        }
        binding.icDelete.setOnClickListener(v -> {
            binding.icCamera.setVisibility(View.VISIBLE);
            binding.icDelete.setVisibility(View.GONE);
            binding.icFlyerImg.setVisibility(View.GONE);
            imgBitmap = null;
            if (isFlyerImage) {
                setNextButton(listGray, getResources().getString(R.string.verify_code), false);
            }
        });
        binding.icCamera.setOnClickListener(v -> openCamera());
    }

    private void getAllDataFromIntent() {
        Intent intent = getIntent();
        Bundle qcStatusBundle = intent.getBundleExtra("QC_STATUS_DATA");
        if (qcStatusBundle != null) {
            for (String key : qcStatusBundle.keySet()) {
                qcStatusData.put(key, qcStatusBundle.getString(key));
            }
        }
        qcCode = intent.getStringArrayListExtra("QC_CODE");
        qualityCheckName = intent.getStringArrayListExtra("QC_NAME");
        consigneeName = intent.getStringExtra(Constants.OBD_CONSIGNEE_NAME);
        awbNumber = intent.getStringExtra(Constants.OBD_AWB_NUMBER);
        consigneeMobile = intent.getStringExtra(Constants.CONSIGNEE_MOBILE);
        consigneeAlternateMobile = intent.getStringExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE);
        drsIdNum = intent.getStringExtra(Constants.DRS_ID_NUM);
        returnPackageBarcode = intent.getStringExtra(Constants.return_package_barcode);
        isUndelivered = intent.getBooleanExtra("isUndelivered", false);
        isQcFailedFlag = intent.getBooleanExtra("isNonQc", false);
        isCheckedBox = intent.getBooleanExtra("isCheckedbox", false);
        awbNumber = intent.getStringExtra(Constants.OBD_AWB_NUMBER);
        drsIdNum = intent.getStringExtra(Constants.DRS_ID_NUM);
        orderId = intent.getStringExtra(Constants.ORDER_ID);
        consigneeMobile = intent.getStringExtra(Constants.CONSIGNEE_MOBILE);
        consigneeAlternateMobile = intent.getStringExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE);
        DRS_DATE = getIntent().getExtras().getString(Constants.DRS_DATE);
        SHIPMENT_DECLARED_VALUE = getIntent().getExtras().getString(Constants.SHIPMENT_DECLARED_VALUE);
        qcFailedData = intent.getStringArrayListExtra("QC_FAILED_DATA");
        forwardCommit = getIntent().getParcelableExtra("data");
        awbScanMethod = intent.getStringExtra(Constants.AWB_SCAN);
        isFlyerImage = intent.getBooleanExtra(Constants.Flyer_Img_Check, false);
        setViews();
    }

    private void openCamera() {
        if (isNetworkConnected()) {
            FwdOBDStopOTPActivity.this.imageHandler.captureImage(awbNumber + "_" + drsIdNum + "_" + "OBD_Flyer_Image_" + ".png", binding.icCamera, "OBD_Flyer_Image");
        } else {
            showError("Check Your Internet Connection");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            int PICK_FROM_CAMERA = 0x000010;
            if (requestCode == PICK_FROM_CAMERA) {
                imageHandler.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            Logger.e("TAG", String.valueOf(e));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (imgBitmap != null) {
            setDataAfterImageCaptured(imgBitmap);
            if (isFlyerImage && otpText.length() == 4) {
                setNextButton(listBlue, getResources().getString(R.string.verify_code), true);
            }
        }
    }

    public void setDataAfterImageCaptured(Bitmap qcCapturedImageBitmap) {
        if (qcCapturedImageBitmap != null) {
            binding.icFlyerImg.setVisibility(View.VISIBLE);
            binding.icFlyerImg.setImageBitmap(qcCapturedImageBitmap);
            binding.icCamera.setVisibility(View.GONE);
            binding.icDelete.setVisibility(View.VISIBLE);
        }
    }
}