package in.ecomexpress.sathi.ui.drs.forward.undelivered_fwd;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.Constants.ConsigneeDirectAlternateMobileNo;
import static in.ecomexpress.sathi.utils.Constants.eds_call_count;
import static in.ecomexpress.sathi.utils.Constants.forward_call_count;
import static in.ecomexpress.sathi.utils.Constants.rts_call_count;
import static in.ecomexpress.sathi.utils.Constants.rvp_call_count;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.budiyev.android.codescanner.CodeScanner;
import com.nlscan.android.scan.ScanManager;
import com.nlscan.android.scan.ScanSettings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.barcodelistner.BarcodeHandler;
import in.ecomexpress.barcodelistner.BarcodeResult;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityUndeliveredBinding;
import in.ecomexpress.sathi.repo.local.data.activitiesdata.FWDActivitiesData;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.repo.local.db.model.ForwardUndeliveredReasonCodeList;
import in.ecomexpress.sathi.repo.local.db.model.Remark;
import in.ecomexpress.sathi.repo.remote.model.ErrorResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.masterdata.CallbridgeConfiguration;
import in.ecomexpress.sathi.repo.remote.model.masterdata.ForwardReasonCodeMaster;
import in.ecomexpress.sathi.repo.remote.model.masterdata.GlobalConfigurationMaster;
import in.ecomexpress.sathi.repo.remote.model.reschedule.ReshceduleDetailsResponse;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.drs.forward.fill_awb.AwbPopupDialog;
import in.ecomexpress.sathi.ui.drs.forward.fill_awb.MyDialogCloseListener;
import in.ecomexpress.sathi.ui.drs.forward.signature.SignatureViewModel;
import in.ecomexpress.sathi.ui.drs.forward.success.FWDSuccessActivity;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.FlashlightProvider;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.Helper;
import in.ecomexpress.sathi.utils.ImageHandler;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.NetworkUtils;
import in.ecomexpress.sathi.utils.TimeUtils;
import io.reactivex.disposables.CompositeDisposable;

@AndroidEntryPoint
public class UndeliveredActivity extends BaseActivity<ActivityUndeliveredBinding, UndeliveredViewModel> implements IUndeliveredNavigator, BarcodeResult, MyDialogCloseListener {

    private final String TAG = UndeliveredActivity.class.getSimpleName();
    public static int imageCaptureCount = 0;
    @Inject
    UndeliveredViewModel undeliveredViewModel;
    ActivityUndeliveredBinding mActivityUndeliveredBinding;
    ImageHandler imageHandler;
    ForwardCommit forwardCommit;
    String shipmentType;
    String stPickedSpinnerValue = null;
    Boolean isImageCaptured = false;
    Boolean scannedStatus = false;
    AwbPopupDialog awbPopupDialog;
    MyDialogCloseListener myDialogCloseListener;
    BarcodeHandler barcodeHandler;
    String getDrsApiKey = "", getDrsPstnKey = "", getCbConfigCallType = "", masterPstnFormat = "", getDrsPin = "", composite_key = "";
    Dialog dialog;
    CallbridgeConfiguration callbridgeConfiguration = null;
    String groupName = Constants.SELECT;
    int isCall;
    boolean dateFlag = false, call_flag;
    String dateSet = "";
    String isSecureUndelivered = "false";
    String isMPSUndelivered = "false";
    int counter_scan = 0;
    String order_id = "";
    ImageView mimageView;
    Bitmap mbitmap;
    String awb;
    boolean check_call_mandatory_flag = false;
    boolean uD_OTP = false;
    ReshceduleDetailsResponse reshceduleDetailsResponse;
    boolean call_allowed;
    String consignee_mobile, consignee_alternate_mobile = "";
    String OFD_OTP;
    String drs_id_num;
    String collectedValue = "0";
    CountDownTimer mCountDownTimer = null;
    private ForwardReasonCodeMaster forwardReasonCodeMaster;
    private int mYear;
    private int mMonth;
    private int mDay;
    private DRSForwardTypeResponse drsFWDResponse;
    private int meterRange;
    private boolean consigneeProfiling = false, is_call_mandatory;
    private boolean is_amazon_schedule_enable;
    private ScanManager mScanMgr;
    private CodeScanner mCodeScanner;
    private long mLastClickTime = 0;
    private boolean is_obd;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, UndeliveredActivity.class);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        undeliveredViewModel.setNavigator(this);
        mActivityUndeliveredBinding = getViewDataBinding();
        barcodeHandler = new BarcodeHandler(this, "ScannerLM", this);
        barcodeHandler.enableScanner();
        myDialogCloseListener = this;
        logScreenNameInGoogleAnalytics(TAG, this);
        undeliveredViewModel.getDataManager().setLoginPermission(false);
        try {
            mActivityUndeliveredBinding.scrollView.setFillViewport(true);
            undeliveredViewModel.getGlobalConfigurationMaster();
            undeliveredViewModel.getcallConfig();
            forwardCommit = getIntent().getParcelableExtra("data");


            // getting the data from previous activity
            FWDActivitiesData fwdActivitiesData = getIntent().getParcelableExtra("fwdActivitiesData");

            if (fwdActivitiesData != null) {
                try {
                    awb = String.valueOf(fwdActivitiesData.getAwbNo());
                    order_id = fwdActivitiesData.getOrderId();
                    drs_id_num = String.valueOf(fwdActivitiesData.getDrsId());
                    shipmentType = fwdActivitiesData.getShipment_type();
                    composite_key = fwdActivitiesData.getCompositeKey();
                    getDrsApiKey = fwdActivitiesData.getDrsApiKey();
                    getDrsPstnKey = fwdActivitiesData.getDrsPstnKey();
                    getDrsPin = fwdActivitiesData.getDrsPin();
                    collectedValue = fwdActivitiesData.getCollected_value();
                    call_allowed = fwdActivitiesData.isCallAllowed();
                    is_amazon_schedule_enable = fwdActivitiesData.isIs_amazon_reschedule_enabled();
                    consignee_mobile = fwdActivitiesData.getConsignee_mobile();
                    consignee_alternate_mobile = fwdActivitiesData.getConsignee_alternate_number();
                    isSecureUndelivered = fwdActivitiesData.getSecure_undelivered();
                    if (fwdActivitiesData.isIs_obd()) {
                        is_obd = fwdActivitiesData.isIs_obd();

                    } else {
                        is_obd = false;

                    }

                    Logger.e("SecureUnSecureUDOTPFlags", isSecureUndelivered);
                    if (fwdActivitiesData.getOfd_otp() == null) {
                        OFD_OTP = "";
                    } else {
                        OFD_OTP = fwdActivitiesData.getOfd_otp();
                    }
                    if (fwdActivitiesData.getMps_undelivered() == null) {
                        isMPSUndelivered = "";
                    } else {
                        isMPSUndelivered = fwdActivitiesData.getMps_undelivered();
                    }
                } catch (Exception e) {
                    Logger.e("UndeliveredActivity", e.getMessage());

                }
            }
            forwardCommit.setAwb(awb);
            Constants.LOCATION_ACCURACY = undeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE();
            String isSecure;
            if (isSecureUndelivered != null) {
                isSecure = isSecureUndelivered;
            } else {
                isSecure = "false";
            }
            String isMPS;
            if (isMPSUndelivered != null) {
                isMPS = isMPSUndelivered;
            } else {
                isMPS = "false";
            }
            undeliveredViewModel.getAllUndeliveredReasonCode(shipmentType, isSecure, isMPS);
            undeliveredViewModel.onForwardDRSCommit(forwardCommit);
            undeliveredViewModel.onForwardDRSCommit(forwardCommit);
            undeliveredViewModel.fetchForwardShipment(forwardCommit.getDrs_id(), forwardCommit.getAwb());
            mActivityUndeliveredBinding.flagIsRescheduled.setVisibility(View.GONE);
            undeliveredViewModel.getCallStatus(Long.parseLong(awb), Integer.parseInt(forwardCommit.getDrs_id()));
            mActivityUndeliveredBinding.tvNumberStatement.setText("We have send OTP on registered mobile number: " + consignee_mobile);
            imageHandler = new ImageHandler(this) {
                @Override
                public void onBitmapReceived(final Bitmap bitmap, final String imageUri, final ImageView imageView, String imageName, String imageCode, int pos, boolean verifyImage) {
                    runOnUiThread(() -> {
                        try {
                            if (CommonUtils.checkImageIsBlurryOrNot(UndeliveredActivity.this, "FWD", bitmap, imageCaptureCount, undeliveredViewModel.getDataManager())) {
                                imageCaptureCount++;
                            } else {
                                if (imageView != null) mimageView = imageView;
                                mbitmap = bitmap;
                                isImageCaptured = true;
                                if (NetworkUtils.isNetworkConnected(UndeliveredActivity.this)) {
                                    undeliveredViewModel.uploadImageServer(forwardCommit.getAwb() + "_" + forwardCommit.getDrs_id() + "_UndeliveredImage.png", imageUri, "UndeliveredImage", Long.parseLong(forwardCommit.getAwb()), Integer.parseInt(forwardCommit.getDrs_id()), "", bitmap, composite_key);
                                } else {
                                    undeliveredViewModel.uploadAWSImage(imageUri, "UndeliveredImage", forwardCommit.getAwb() + "_" + forwardCommit.getDrs_id() + "_UndeliveredImage.png", false);
                                }
                            }
                        } catch (Exception e) {
                            Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
                            showSnackbar(e.getMessage());
                        }
                    });
                }
            };
            mActivityUndeliveredBinding.remarks.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
            mActivityUndeliveredBinding.remarks.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
            undeliveredViewModel.getConsigneeProfiling();
        } catch (Exception e) {
            Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
            showError(e.getMessage());
        }

        try {
            mCodeScanner = new CodeScanner(this, mActivityUndeliveredBinding.scannerView);
            mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
                try {
                    if (counter_scan < 4) {
                        if (result != null) {
                            if (result.getText().equalsIgnoreCase(awb) || result.getText().equalsIgnoreCase(order_id)) {
                                scannedStatus = true;
                                mActivityUndeliveredBinding.scannerFrame.setVisibility(View.GONE);
                            } else {
                                scannedStatus = false;
                                if (counter_scan >= 4) {
                                    awbPopupDialog = AwbPopupDialog.newInstance(UndeliveredActivity.this, awb);
                                    awbPopupDialog.show(getSupportFragmentManager());
                                    awbPopupDialog.setCancelable(false);
                                    awbPopupDialog.setListener(myDialogCloseListener);
                                    mActivityUndeliveredBinding.scannerFrame.setVisibility(View.GONE);
                                    counter_scan = 0;
                                } else {
                                    undeliveredViewModel.doScanAgainAlert(UndeliveredActivity.this);
                                    counter_scan++;
                                }
                            }
                        } else {
                            scannedStatus = false;
                            undeliveredViewModel.doScanAgainAlert(UndeliveredActivity.this);
                            counter_scan++;
                            if (counter_scan >= 4) {
                                awbPopupDialog = AwbPopupDialog.newInstance(UndeliveredActivity.this, awb);
                                awbPopupDialog.show(getSupportFragmentManager());
                                awbPopupDialog.setListener(myDialogCloseListener);
                                awbPopupDialog.setCancelable(false);
                                mActivityUndeliveredBinding.scannerFrame.setVisibility(View.GONE);
                                counter_scan = 0;
                            }
                        }
                    } else {
                        awbPopupDialog = AwbPopupDialog.newInstance(UndeliveredActivity.this, awb);
                        awbPopupDialog.show(getSupportFragmentManager());
                        awbPopupDialog.setListener(myDialogCloseListener);
                        awbPopupDialog.setCancelable(false);
                        mActivityUndeliveredBinding.scannerFrame.setVisibility(View.GONE);
                        counter_scan = 0;
                    }
                } catch (Exception e) {
                    Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
                    showSnackbar(e.getMessage());
                }
                mCodeScanner.startPreview();
            }));
        } catch (Exception e) {
            Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
        }

        drs_id_num = forwardCommit.getDrs_id();

        mActivityUndeliveredBinding.header.awb.setText(R.string.fwd_undelivered);
        mActivityUndeliveredBinding.header.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackClick();
            }
        });

    }

    /**
     * @param uD_OTP_staus--- this true/false value for Undelived OTP
     * @param otp_key--       this is used for undelivered otp/reschedule otp value
     */

    @Override
    public void otpLayout(boolean uD_OTP_staus, String otp_key) {
        try {
            if (otp_key.equalsIgnoreCase("UD_OTP")) {
                if (Constants.CancellationEnable) {
                    if (uD_OTP_staus) {
                        mActivityUndeliveredBinding.llUdOtp.setVisibility(View.VISIBLE);
                        mActivityUndeliveredBinding.edtUdOtp.setEnabled(false);
                        mActivityUndeliveredBinding.edtUdOtp.setText("");
                        mActivityUndeliveredBinding.imgVerifiedTick.setVisibility(View.GONE);
                        undeliveredViewModel.ud_otp_verified_status.set(false);
                        mActivityUndeliveredBinding.generateOtpTv.setVisibility(View.VISIBLE);
                        mActivityUndeliveredBinding.resendOtpTv.setVisibility(View.GONE);
                        mActivityUndeliveredBinding.verifyTv.setVisibility(View.GONE);
                        uD_OTP = uD_OTP_staus;
                    } else {
                        uD_OTP = false;
                        mActivityUndeliveredBinding.llUdOtp.setVisibility(View.GONE);
                    }
                } else {
                    uD_OTP = false;
                    mActivityUndeliveredBinding.llUdOtp.setVisibility(View.GONE);
                }
            } else if (otp_key.equalsIgnoreCase("RCHD_OTP")) {
                if (Constants.RCHDEnable) {
                    if (uD_OTP_staus) {
                        mActivityUndeliveredBinding.llUdOtp.setVisibility(View.VISIBLE);
                        mActivityUndeliveredBinding.edtUdOtp.setEnabled(false);
                        mActivityUndeliveredBinding.edtUdOtp.setText("");
                        mActivityUndeliveredBinding.imgVerifiedTick.setVisibility(View.GONE);
                        undeliveredViewModel.ud_otp_verified_status.set(false);
                        mActivityUndeliveredBinding.generateOtpTv.setVisibility(View.VISIBLE);
                        mActivityUndeliveredBinding.resendOtpTv.setVisibility(View.GONE);
                        mActivityUndeliveredBinding.verifyTv.setVisibility(View.GONE);
                        uD_OTP = uD_OTP_staus;
                    } else {
                        uD_OTP = false;
                        mActivityUndeliveredBinding.llUdOtp.setVisibility(View.GONE);
                    }
                } else {
                    uD_OTP = false;
                    mActivityUndeliveredBinding.llUdOtp.setVisibility(View.GONE);
                }
            } else {
                uD_OTP = false;
                mActivityUndeliveredBinding.llUdOtp.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
        }
    }

    @Override
    public void onResendClick() {
        hideKeyboard(this);
        if (call_allowed && undeliveredViewModel.getDataManager().getVCallPopup()) {
            undeliveredViewModel.showCallAndSmsDialog(awb, drs_id_num, consignee_alternate_mobile, "Call");
        } else if (!consignee_alternate_mobile.equalsIgnoreCase("") || consignee_alternate_mobile != null) {
            undeliveredViewModel.showCallAndSmsDialog(awb, drs_id_num, consignee_alternate_mobile, "");
        } else {
            if (forwardReasonCodeMaster.getMasterDataAttributeResponse().isUD_OTP()) {
                undeliveredViewModel.onGenerateOtpApiCall(UndeliveredActivity.this, awb, drs_id_num, false, "UD_OTP", false, shipmentType);
            }
            if (forwardReasonCodeMaster.getMasterDataAttributeResponse().isRCHD_OTP()) {
                undeliveredViewModel.onGenerateOtpApiCall(UndeliveredActivity.this, awb, drs_id_num, false, "RCHD_OTP", false, shipmentType);
            }
        }
    }

    @Override
    public void onGenerateOtpClick() {
        hideKeyboard(this);
        if (forwardReasonCodeMaster.getMasterDataAttributeResponse().isUD_OTP()) {
            undeliveredViewModel.onGenerateOtpApiCall(UndeliveredActivity.this, awb, drs_id_num, false, "UD_OTP", true, shipmentType);
        }
        if (forwardReasonCodeMaster.getMasterDataAttributeResponse().isRCHD_OTP()) {
            undeliveredViewModel.onGenerateOtpApiCall(UndeliveredActivity.this, awb, drs_id_num, false, "RCHD_OTP", true, shipmentType);
        }
    }

    //on skip click button
    @Override
    public void onSkipClick(View view) {
        hideKeyboard(this);
        if (undeliveredViewModel.ud_otp_verified_status.get()) {
            showError("Already Verified");
            mActivityUndeliveredBinding.otpSkip.setChecked(false);
            return;
        }
        if (undeliveredViewModel.counter_skip == 0) {
            mActivityUndeliveredBinding.otpSkip.setChecked(false);
            showError("Please resend OTP atleast once.");
        } else {
            if (mActivityUndeliveredBinding.resendOtpTv.isEnabled()) {
                if (((CheckBox) view).isChecked()) {
                    if (forwardReasonCodeMaster.getMasterDataAttributeResponse().isUD_OTP()) {
                        undeliveredViewModel.ud_otp_commit_status = "SKIPPED";
                        undeliveredViewModel.ud_otp_commit_status_field.set("SKIPPED");
                        undeliveredViewModel.rd_otp_commit_status = "";
                        undeliveredViewModel.rd_otp_commit_status_field.set("");
                    }
                    if (forwardReasonCodeMaster.getMasterDataAttributeResponse().isRCHD_OTP()) {
                        undeliveredViewModel.rd_otp_commit_status = "SKIPPED";
                        undeliveredViewModel.rd_otp_commit_status_field.set("SKIPPED");
                        undeliveredViewModel.ud_otp_commit_status = "";
                        undeliveredViewModel.ud_otp_commit_status_field.set("");
                    }
                } else {
                    undeliveredViewModel.ud_otp_commit_status = "NONE";
                    undeliveredViewModel.ud_otp_commit_status_field.set("NONE");
                    undeliveredViewModel.rd_otp_commit_status = "NONE";
                    undeliveredViewModel.rd_otp_commit_status_field.set("NONE");
                }
            } else {
                mActivityUndeliveredBinding.otpSkip.setChecked(false);
            }
        }
    }

    /**
     * @param str---        str will be true/false
     * @param description-- description will be success/fail
     */
    @Override
    public void onOtpResendSuccess(String str, String description) {
        showSnackbar(description);
        mActivityUndeliveredBinding.generateOtpTv.setVisibility(View.GONE);
        mActivityUndeliveredBinding.edtUdOtp.setEnabled(true);
        mActivityUndeliveredBinding.resendOtpTv.setVisibility(View.VISIBLE);
        mActivityUndeliveredBinding.verifyTv.setVisibility(View.VISIBLE);
        if (str.equalsIgnoreCase("true") && description.contains("success")) {
            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
                mCountDownTimer = new CountDownTimer(undeliveredViewModel.getDataManager().getDisableResendOtpButtonDuration() * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        mActivityUndeliveredBinding.resendOtpTv.setEnabled(false);
                        mActivityUndeliveredBinding.resendOtpTv.setTextColor(getResources().getColor(R.color.light_gray));
                        mActivityUndeliveredBinding.otpSkip.setEnabled(false);
                        @SuppressLint("DefaultLocale") String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                        System.out.println(hms);
                        mActivityUndeliveredBinding.resendOtpTv.setText(hms);
                    }

                    @Override
                    public void onFinish() {
                        mActivityUndeliveredBinding.resendOtpTv.setEnabled(true);
                        mActivityUndeliveredBinding.resendOtpTv.setText(getResources().getString(R.string.resend));
                        mActivityUndeliveredBinding.otpSkip.setEnabled(true);
                    }
                };
                mCountDownTimer.start();
            } else {
                mCountDownTimer = new CountDownTimer(undeliveredViewModel.getDataManager().getDisableResendOtpButtonDuration() * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        mActivityUndeliveredBinding.resendOtpTv.setEnabled(false);
                        mActivityUndeliveredBinding.resendOtpTv.setTextColor(getResources().getColor(R.color.light_gray));
                        mActivityUndeliveredBinding.otpSkip.setEnabled(false);
                        @SuppressLint("DefaultLocale") String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                        System.out.println(hms);
                        mActivityUndeliveredBinding.resendOtpTv.setText(hms);
                    }

                    @Override
                    public void onFinish() {
                        mActivityUndeliveredBinding.resendOtpTv.setEnabled(true);
                        mActivityUndeliveredBinding.resendOtpTv.setText(getResources().getString(R.string.resend));
                        mActivityUndeliveredBinding.otpSkip.setEnabled(true);
                    }
                };
                mCountDownTimer.start();
            }
        } else {
            if (mCountDownTimer != null) {
                mActivityUndeliveredBinding.resendOtpTv.setEnabled(true);
                mActivityUndeliveredBinding.resendOtpTv.setText(getResources().getString(R.string.resend));
                mActivityUndeliveredBinding.otpSkip.setEnabled(true);
                mCountDownTimer.cancel();
            } else {
                mActivityUndeliveredBinding.otpSkip.setEnabled(true);
            }
        }
    }

    @Override
    public void showScanAlert() {
    }

    @Override
    public void setRescheduleResponse(ReshceduleDetailsResponse reshceduleDetailsResponse) {
        this.reshceduleDetailsResponse = reshceduleDetailsResponse;
        mActivityUndeliveredBinding.flagIsRescheduled.setVisibility(View.VISIBLE);
    }

    //for voice call click
    @Override
    public void VoiceCallOtp() {
        hideKeyboard(this);
        if (forwardReasonCodeMaster.getMasterDataAttributeResponse().isUD_OTP()) {
            undeliveredViewModel.doVoiceOTPApi(awb, drs_id_num, "UD_OTP", shipmentType);
        } else if (forwardReasonCodeMaster.getMasterDataAttributeResponse().isRCHD_OTP()) {
            undeliveredViewModel.doVoiceOTPApi(awb, drs_id_num, "RCHD_OTP", shipmentType);
        } else {
            undeliveredViewModel.doVoiceOTPApi(awb, drs_id_num, "OTP", shipmentType);
        }
    }

    @Override
    public void clearStack() {
        Intent intent = new Intent(UndeliveredActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    @Override
    public void doLogout(String description) {
        showToast(getString(R.string.session_expire));
        undeliveredViewModel.logoutLocal();
    }

    @Override
    public void resendSms(Boolean alternateclick) {
        hideKeyboard(this);
        if (forwardReasonCodeMaster.getMasterDataAttributeResponse().isUD_OTP()) {
            undeliveredViewModel.onGenerateOtpApiCall(UndeliveredActivity.this, awb, drs_id_num, alternateclick, "UD_OTP", false, shipmentType);
        }
        if (forwardReasonCodeMaster.getMasterDataAttributeResponse().isRCHD_OTP()) {
            undeliveredViewModel.onGenerateOtpApiCall(UndeliveredActivity.this, awb, drs_id_num, alternateclick, "RCHD_OTP", false, shipmentType);
        }
    }


    // for timer count down
    @Override
    public void voiceTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = new CountDownTimer(undeliveredViewModel.getDataManager().getDisableResendOtpButtonDuration() * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mActivityUndeliveredBinding.resendOtpTv.setEnabled(false);
                    mActivityUndeliveredBinding.resendOtpTv.setTextColor(getResources().getColor(R.color.light_gray));
                    mActivityUndeliveredBinding.otpSkip.setEnabled(false);
                    @SuppressLint("DefaultLocale") String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                    System.out.println(hms);
                    mActivityUndeliveredBinding.resendOtpTv.setText(hms);
                }

                @Override
                public void onFinish() {
                    mActivityUndeliveredBinding.resendOtpTv.setEnabled(true);
                    mActivityUndeliveredBinding.resendOtpTv.setText(getResources().getString(R.string.resend));
                    mActivityUndeliveredBinding.otpSkip.setEnabled(true);
                }
            };
            mCountDownTimer.start();
        } else {
            mCountDownTimer = new CountDownTimer(undeliveredViewModel.getDataManager().getDisableResendOtpButtonDuration() * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mActivityUndeliveredBinding.resendOtpTv.setEnabled(false);
                    mActivityUndeliveredBinding.resendOtpTv.setTextColor(getResources().getColor(R.color.light_gray));
                    mActivityUndeliveredBinding.otpSkip.setEnabled(false);
                    @SuppressLint("DefaultLocale") String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                    System.out.println(hms);
                    mActivityUndeliveredBinding.resendOtpTv.setText(hms);
                }

                @Override
                public void onFinish() {
                    mActivityUndeliveredBinding.resendOtpTv.setEnabled(true);
                    mActivityUndeliveredBinding.resendOtpTv.setText(getResources().getString(R.string.resend));
                    mActivityUndeliveredBinding.otpSkip.setEnabled(true);
                }
            };
            mCountDownTimer.start();
        }
    }

    // for verify otp click
    @Override
    public void onVerifyClick() {
        hideKeyboard(this);
        if (mActivityUndeliveredBinding.edtUdOtp.getText() == null || mActivityUndeliveredBinding.edtUdOtp.getText().toString().equalsIgnoreCase("")) {
            showError("Please Enter OTP.");
        } else if (mActivityUndeliveredBinding.edtUdOtp.getText().length() < 4) {
            showError("Please Enter OTP.");
        } else {
            if (OFD_OTP != null && !OFD_OTP.equalsIgnoreCase("")) {
                String encryptText = CommonUtils.decrypt(OFD_OTP, Constants.DECRYPT);
                Constants.PLAIN_OTP = encryptText;
                if (encryptText.equalsIgnoreCase(mActivityUndeliveredBinding.edtUdOtp.getText().toString())) {
                    Constants.OFD_OTP_VERIFIED = true;
                    undeliveredViewModel.setOFDVerfied(true);
                    Toast.makeText(UndeliveredActivity.this, "Verified successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Constants.OFD_OTP_VERIFIED = false;
                    if (forwardReasonCodeMaster.getMasterDataAttributeResponse().isUD_OTP()) {
                        undeliveredViewModel.onVerifyApiCall(UndeliveredActivity.this, mActivityUndeliveredBinding.awb.getText().toString(), mActivityUndeliveredBinding.edtUdOtp.getText().toString(), drs_id_num, "UD_OTP");
                        showSnackbar("Verified");
                        mActivityUndeliveredBinding.otpSkip.setChecked(false);
                    }
                    if (forwardReasonCodeMaster.getMasterDataAttributeResponse().isRCHD_OTP()) {
                        undeliveredViewModel.onVerifyApiCall(UndeliveredActivity.this, mActivityUndeliveredBinding.awb.getText().toString(), mActivityUndeliveredBinding.edtUdOtp.getText().toString(), drs_id_num, "RCHD_OTP");
                        showSnackbar("Verified");
                        mActivityUndeliveredBinding.otpSkip.setChecked(false);
                    }
                }
            } else {
                Constants.OFD_OTP_VERIFIED = false;
                if (forwardReasonCodeMaster.getMasterDataAttributeResponse().isUD_OTP()) {
                    undeliveredViewModel.onVerifyApiCall(UndeliveredActivity.this, mActivityUndeliveredBinding.awb.getText().toString(), mActivityUndeliveredBinding.edtUdOtp.getText().toString(), drs_id_num, "UD_OTP");
                    showSnackbar("Verified");
                    mActivityUndeliveredBinding.otpSkip.setChecked(false);
                }
                if (forwardReasonCodeMaster.getMasterDataAttributeResponse().isRCHD_OTP()) {
                    undeliveredViewModel.onVerifyApiCall(UndeliveredActivity.this, mActivityUndeliveredBinding.awb.getText().toString(), mActivityUndeliveredBinding.edtUdOtp.getText().toString(), drs_id_num, "RCHD_OTP");
                    showSnackbar("Verified");
                    mActivityUndeliveredBinding.otpSkip.setChecked(false);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (undeliveredViewModel.consigneeContactNumber.get() == null) {
            undeliveredViewModel.consigneeContactNumber.set("");
        }
        if (!Objects.requireNonNull(undeliveredViewModel.consigneeContactNumber.get()).equalsIgnoreCase("")) {
            CommonUtils.deleteNumberFromCallLogsAsync(undeliveredViewModel.consigneeContactNumber.get(), UndeliveredActivity.this);
        }

        if (undeliveredViewModel.getIsAwbScan()) {
            if (!scannedStatus) {
                mCodeScanner.startPreview();
            }
            barcodeHandler = new BarcodeHandler(this, "ScannerLM", this);
            barcodeHandler.enableScanner();
            undeliveredViewModel.getIsAwbScan();
            if (SignatureViewModel.device.equals(Constants.NEWLAND)) {
                mScanMgr = ScanManager.getInstance();
                mScanMgr.startScan();
                mScanMgr.enableBeep();
                mScanMgr.setOutpuMode(ScanSettings.Global.VALUE_OUT_PUT_MODE_BROADCAST);
            } else {
                barcodeHandler = new BarcodeHandler(this, "ScannerLM", this);
                barcodeHandler.enableScanner();
            }
        } else {
            mActivityUndeliveredBinding.scannerFrame.setVisibility(View.GONE);
        }
        try {
            undeliveredViewModel.getisCallattempted(forwardCommit.getAwb());
            if (Constants.rCheduleFlag) {
                if (!forwardReasonCodeMaster.getMasterDataAttributeResponse().isrCHD())
                    mActivityUndeliveredBinding.flagIsRescheduled.setVisibility(View.GONE);
                else undeliveredViewModel.getShipmentRescheduleDetails(String.valueOf(awb));
            }
            if (undeliveredViewModel.ud_otp_verified_status.get()) {
                mActivityUndeliveredBinding.imgVerifiedTick.setVisibility(View.VISIBLE);
            } else {
                mActivityUndeliveredBinding.imgVerifiedTick.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
        }
    }

    @Override
    public UndeliveredViewModel getViewModel() {
        return undeliveredViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_undelivered;
    }

    @Override
    public void onChooseReasonSpinner(ForwardUndeliveredReasonCodeList forwardReasonCodeMaster) {
        try {
            this.stPickedSpinnerValue = forwardReasonCodeMaster.getForwardReasonCodeMaster().getReasonMessage();
            this.forwardReasonCodeMaster = forwardReasonCodeMaster.getForwardReasonCodeMaster();
            groupName = forwardReasonCodeMaster.getForwardReasonCodeMaster().getReasonMessage();
            if (forwardReasonCodeMaster.getForwardReasonCodeMaster().getMasterDataAttributeResponse().iscALLM()) {
                is_call_mandatory = Constants.CONSIGNEE_PROFILE || meterRange >= undeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE();
            } else {
                is_call_mandatory = false;
            }
            mActivityUndeliveredBinding.otpSkip.setChecked(false);
            undeliveredViewModel.ud_otp_commit_status = "NONE";
            undeliveredViewModel.ud_otp_commit_status_field.set("NONE");
            undeliveredViewModel.rd_otp_commit_status = "NONE";
            undeliveredViewModel.rd_otp_commit_status_field.set("NONE");
            SetViewsVisibility(forwardReasonCodeMaster);
        } catch (Exception e) {
            Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
        }
    }

    public void SetViewsVisibility(ForwardUndeliveredReasonCodeList forwardReasonCodeMaster) {
        try {
            if (!forwardReasonCodeMaster.getForwardReasonCodeMaster().getReasonMessage().equalsIgnoreCase(Constants.SELECT)) {
                if (forwardReasonCodeMaster != null) {
                    if (undeliveredViewModel.getDataManager().isCounterDelivery() && undeliveredViewModel.getCounterDeliveryRange() < undeliveredViewModel.getDataManager().getDCRANGE()) {
                    } else if (!forwardReasonCodeMaster.getForwardReasonCodeMaster().getMasterDataAttributeResponse().iscALLM()) {
                    } else {
                        if (isCall == 0 && undeliveredViewModel.getDataManager().getCallClicked(awb + "ForwardCall")) {
                            makeCallDialog();
                            return;
                        }
                    }
                    if (!forwardReasonCodeMaster.getForwardReasonCodeMaster().getMasterDataAttributeResponse().isrCHD()) {
                        mActivityUndeliveredBinding.flagIsRescheduled.setVisibility(View.GONE);
                    } else {
                        undeliveredViewModel.getShipmentRescheduleDetails(String.valueOf(awb));
                        forwardCommit.setReschedule_date(mActivityUndeliveredBinding.date.getText().toString());
                    }
                }
                mActivityUndeliveredBinding.llUdOtp.setVisibility(View.GONE);
                forwardCommit.setAttempt_reason_code(forwardReasonCodeMaster.getForwardReasonCodeMaster().getReasonCode().toString());
            }
        } catch (Exception e) {
            Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
            showError(e.getMessage());
        }
    }

    @SuppressLint("SetTextI18n")
    public void makeCallDialog() {
        if (!call_allowed) {
            undelivered(false);
        } else try {
            dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.activity_undelivered_call_dialog);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
            TextView name = dialog.findViewById(R.id.name);
            name.setText("Name : " + forwardCommit.getConsignee_name());
            TextView awb = dialog.findViewById(R.id.awb);
            awb.setText("AWB : " + forwardCommit.getAwb());
            ImageView dialogButton = dialog.findViewById(R.id.call);
            // if button is clicked, close the custom dialog
            dialogButton.setOnClickListener(v -> {
                undeliveredViewModel.getDataManager().setCallClicked(forwardCommit.getAwb() + "ForwardCall", false);
                try {
                    if (drsFWDResponse.getFlags().getFlagMap().getIs_callbridge_enabled().equalsIgnoreCase("true") &&
                            drsFWDResponse.getCallbridge_details()!=null)
                    {
                        makeCallonClick();
                    }
                    else
                    {
                        if (!TextUtils.isEmpty(ConsigneeDirectAlternateMobileNo) && ConsigneeDirectAlternateMobileNo != null && !ConsigneeDirectAlternateMobileNo.equals("0")) {
                            showDirectCallDialog();
                        } else {
                            undeliveredViewModel.consigneeContactNumber.set(drsFWDResponse.getConsigneeDetails().getMobile());
                            forward_call_count = forward_call_count + 1;
                            undeliveredViewModel.getDataManager().setForwardCallCount(forwardCommit.getAwb() + "FWD", forward_call_count);
                            CommonUtils.startCallIntent(drsFWDResponse.getConsigneeDetails().getMobile(), getActivityContext(), UndeliveredActivity.this);
                        }

                    }

                    dialog.dismiss();
                } catch (Exception e) {
                    Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
                }
            });
            dialog.show();
        } catch (Exception e) {
            Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
        }
    }

    private void makeCallonClick() {
        /*Check 1.1 (Check for PSTN)*/
        /*if (getDrsPstnKey != null) {
            try {
                Constants.call_awb = forwardCommit.getAwb();
                Constants.shipment_type = Constants.FWD;
                undeliveredViewModel.saveCallStatus(Long.parseLong(forwardCommit.getAwb()), Integer.parseInt(forwardCommit.getDrs_id()));
                Constants.IS_CALL_BRIDGE_FLAG_ON_STATUS = undeliveredViewModel.getDataManager().getIsCallBridgeCheckStatus().equalsIgnoreCase("true");
                if (drsFWDResponse.getFlags().getFlagMap().getIs_callbridge_enabled().equalsIgnoreCase("true")
                        && drsFWDResponse.getCallbridge_details()!=null)
                {
                    undeliveredViewModel.consigneeContactNumber.set(getDrsPstnKey);
                    CommonUtils.startCallIntent(getDrsPstnKey, this, UndeliveredActivity.this);
                }
                else{
                    if (!TextUtils.isEmpty(ConsigneeDirectAlternateMobileNo) && ConsigneeDirectAlternateMobileNo != null && !ConsigneeDirectAlternateMobileNo.equals("0")) {
                        showDirectCallDialog();
                    } else {
                        undeliveredViewModel.consigneeContactNumber.set(Constants.ConsigneeDirectMobileNo);
                        forward_call_count = forward_call_count + 1;
                        undeliveredViewModel.getDataManager().setForwardCallCount(awb + "FWD", forward_call_count);
                        Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Constants.ConsigneeDirectMobileNo));
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent1);
                    }
                }

            } catch (Exception e) {
               Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
                showError(e.getMessage());
            }
        }*/

        /*Check 1.2 (Check for API)*/
        /*else if (getDrsApiKey != null) {
            try {
                if (isNetworkConnected()) {
                    undeliveredViewModel.makeCallBridgeApiCall(getDrsApiKey, forwardCommit.getAwb(), forwardCommit.getDrs_id(), Constants.FWD);
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
               Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
                showError(e.getMessage());
            }
        }*/

        /*Check 2 (Check for Calling method in Master Data)*/

            try {
                //undeliveredViewModel.getcallConfig();
              /*  if (callbridgeConfiguration != null) {
                    getCbConfigCallType = callbridgeConfiguration.getCb_calling_type();
                    Log.d("getCbConfigCallType: ", getCbConfigCallType);*/

                    /*Check 2.1 (Check for PSTN)*/
                   /* if (getCbConfigCallType != null) {*/
                        if (drsFWDResponse.getFlags().getFlagMap().getIs_callbridge_enabled().equalsIgnoreCase("true")
                                && drsFWDResponse.getCallbridge_details()!=null) {

                            //masterPstnFormat = undeliveredViewModel.getDataManager().getPstnFormat();
                            String callingformat = null;

                          /*  if (masterPstnFormat.contains(this.getString(R.string.patn_awb))) {
                                callingformat = masterPstnFormat.replaceAll(this.getString(R.string.patn_awb), forwardCommit.getAwb());
                                Constants.call_awb = String.valueOf(forwardCommit.getAwb());
                                Constants.shipment_type = Constants.FWD;
                            } else if (masterPstnFormat.contains(this.getString(R.string.pstn_pin))) {*/
                           //     callingformat = masterPstnFormat.replaceAll(this.getString(R.string.pstn_pin), getDrsPin);
                             //   Constants.call_pin = String.valueOf(getDrsPin);
                                callingformat = drsFWDResponse.getCallbridge_details().get(0).getCallbridge_number()+","+drsFWDResponse.getCallbridge_details().get(0).getPin()+"#";
                                Constants.call_pin = String.valueOf(drsFWDResponse.getCallbridge_details().get(0).getPin());
                                Constants.calling_format = callingformat;
                                Constants.shipment_type = Constants.FWD;
                           // }
                            if (callingformat != null) {
                                undeliveredViewModel.saveCallStatus(Long.parseLong(forwardCommit.getAwb()), Integer.parseInt(forwardCommit.getDrs_id()));
                                Constants.IS_CALL_BRIDGE_FLAG_ON_STATUS = undeliveredViewModel.getDataManager().getIsCallBridgeCheckStatus().equalsIgnoreCase("true");
                                if (drsFWDResponse.getFlags().getFlagMap().getIs_callbridge_enabled().equalsIgnoreCase("true")
                                        && drsFWDResponse.getCallbridge_details()!=null)
                                {
                                    if (drsFWDResponse.getCallbridge_details().size()>1)
                                    {
                                        showCallBridgeDialog();
                                    }
                                    else
                                    {
                                        undeliveredViewModel.consigneeContactNumber.set(callingformat);
                                        CommonUtils.startCallIntent(callingformat, this, UndeliveredActivity.this);
                                    }

                                }

                                else
                                {
                                    if (!TextUtils.isEmpty(ConsigneeDirectAlternateMobileNo) && ConsigneeDirectAlternateMobileNo != null && !ConsigneeDirectAlternateMobileNo.equals("0")) {
                                        showDirectCallDialog();
                                    } else {
                                        undeliveredViewModel.consigneeContactNumber.set(Constants.ConsigneeDirectMobileNo);
                                        forward_call_count = forward_call_count + 1;
                                        undeliveredViewModel.getDataManager().setForwardCallCount(awb + "FWD", forward_call_count);
                                        Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Constants.ConsigneeDirectMobileNo));
                                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent1);
                                    }
                                }
                            }
                        }

                        /*Check 2.2 (Check for API)*/
                       /* if (getCbConfigCallType.equalsIgnoreCase("API")) {
                            if (isNetworkConnected())
                                undeliveredViewModel.makeCallBridgeApiCall(callbridgeConfiguration.getCb_calling_api(), forwardCommit.getAwb(), forwardCommit.getDrs_id(), Constants.FWD);
                            else {
                                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                            }
                        }*/
                   /* } else {
                        Toast.makeText(this, "callbridgeConfiguration empty", Toast.LENGTH_SHORT).show();
                    }*/
                /*} else {
                    Toast.makeText(this, "Empty callbrige config", Toast.LENGTH_SHORT).show();
                }*/
            } catch (Exception e) {
                Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
                showError(e.getMessage());
            }

        try {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            if (!Constants.broad_call_type.isEmpty() && !Constants.broad_shipment_type.isEmpty()) {
                String getCallType = Constants.broad_call_type;
                String getShipmentType = Constants.broad_shipment_type;
                if (getCallType.equalsIgnoreCase(Constants.call_awb)) {
                    if (getShipmentType.equals(Constants.FWD)) {
                        undeliveredViewModel.updateForwardCallAttempted(getCallType);
                    }
                } else if (getCallType.equalsIgnoreCase(Constants.call_pin)) {
                    if (getShipmentType.equals(Constants.FWD)) {
                        undeliveredViewModel.updateForwardpinCallAttempted(getCallType);
                    }
                }
                Constants.call_awb = "";
                Constants.call_pin = "";
                Constants.shipment_type = "";
                Constants.broad_call_type = "";
                Constants.broad_shipment_type = "";
                Constants.call_intent_number = "";
            }

        } catch (Exception e) {
            Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
            showError(e.getMessage());
        }
    }

    // on capture image
    @Override
    public void onCaptureImage() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_Material3_Light_Dialog_Alert);
        String AlertText1 = "Attention : ";
        builder.setMessage(AlertText1 + getString(R.string.alert)).setCancelable(false).setPositiveButton("OK", (dialog, id) -> imageHandler.captureImage(forwardCommit.getAwb() + "_" + forwardCommit.getDrs_id() + "_UndeliveredImage.png", mActivityUndeliveredBinding.image, "Undelivered.png"));
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onDatePicker() {
        if (is_amazon_schedule_enable) {
            if (forwardCommit.getAwb() != null && !forwardCommit.getAwb().equalsIgnoreCase("")) {
                undeliveredViewModel.getReschuduleDates(forwardCommit.getAwb());
            } else {
                showError("Awb number is missing.");
            }
        } else {
            if (undeliveredViewModel.total_attempts < undeliveredViewModel.getDataManager().getRescheduleMaxAttempts()) {
                openDatePicker();
            } else {
                showError("You have reached your max attempt of reschedule.");
            }
        }
    }

    public void openDatePicker() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(this,R.style.Theme_AppCompat_Light_Dialog, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DATE);
            if (dayOfWeek == dayOfMonth) {
                showInfo("Cannot Select Today Date for Re-schedule");
            } else {
                mActivityUndeliveredBinding.date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                dateFlag = true;
                if (monthOfYear > 0 && monthOfYear < 10) {
                    dateSet = dayOfMonth + "-" + "0" + (monthOfYear + 1) + "-" + year;
                } else dateSet = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                Date date = null;
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    date = sdf.parse(dateSet);
                    dateSet = String.valueOf(date.getTime());
                } catch (ParseException e) {
                    Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
                }
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.getDatePicker().setMaxDate(undeliveredViewModel.inscan_date + (Long.parseLong(String.valueOf(undeliveredViewModel.getDataManager().getRescheduleMaxDaysAllow())) * 1000 * 24 * 60 * 60));
        datePickerDialog.show();
    }

    public void openSpinner() {
        mActivityUndeliveredBinding.spinnerDates.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        imageHandler.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void onBackClick() {
        if (shouldAllowBack()) {
            super.onBackPressed();
            applyTransitionToBackFromActivity(this);
        } else {
            Toast.makeText(this, "BackButton is disabled until the timer is off.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
            applyTransitionToBackFromActivity(this);
        } else {
            Toast.makeText(this, "BackButton is disabled until the timer is off.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean shouldAllowBack() {
        return mActivityUndeliveredBinding.resendOtpTv.getText().toString().equalsIgnoreCase("RESEND") || mActivityUndeliveredBinding.resendOtpTv.getVisibility() == View.GONE;
    }

    @Override
    public void onSubmitSuccess(ForwardCommit forwardCommit) {
        OpenFailScreen(forwardCommit);
    }

    // for undelivered success
    @Override
    public void OpenFailScreen(ForwardCommit forwardCommit) {
        Constants.rCheduleFlag = false;

        FWDActivitiesData fwdActivitiesData = new FWDActivitiesData();
        fwdActivitiesData.setAwbNo(Long.parseLong(forwardCommit.getAwb()));
        fwdActivitiesData.setCompositeKey(composite_key);
        fwdActivitiesData.setDecideNext(Constants.UNDELIVERED);
        fwdActivitiesData.setReason(forwardReasonCodeMaster.getReasonMessage());

        Helper.updateLocationWithData(UndeliveredActivity.this, forwardCommit.getAwb(), forwardCommit.getStatus());
        Intent intent = FWDSuccessActivity.getStartIntent(this);
        intent.putExtra("fwdActivitiesData", fwdActivitiesData);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    @SuppressLint("SetTextI18n")
    public void SetVisibility(ForwardReasonCodeMaster forwardReasonCodeMaster) {
        check_call_mandatory_flag = forwardReasonCodeMaster.getMasterDataAttributeResponse().iscALLM();
        mActivityUndeliveredBinding.date.setText("Pick Date");
        forwardCommit.setUd_otp("NONE");
        forwardCommit.setRd_otp("NONE");
        try {
            if (forwardReasonCodeMaster.getReasonMessage().equalsIgnoreCase(Constants.SELECT)) {
                mActivityUndeliveredBinding.imgMandatTv.setText("");
                if (isImageCaptured) {
                    isImageCaptured = false;
                    mActivityUndeliveredBinding.image.setImageBitmap(null);
                    mActivityUndeliveredBinding.image.setImageResource(R.drawable.cam);
                }
            }
        } catch (Exception e) {
            Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
            showError(e.getMessage());
        }
        if (!forwardReasonCodeMaster.getReasonMessage().equalsIgnoreCase(Constants.SELECT)) {
            call_flag = false;
            try {
                if (forwardReasonCodeMaster != null) {
                    if (forwardReasonCodeMaster.getMasterDataAttributeResponse().isUD_OTP()) {
                        otpLayout(true, "UD_OTP");
                        Constants.uD_OTP_API_CHECK = true;
                        Constants.rD_OTP_API_CHECK = false;
                    } else if (forwardReasonCodeMaster.getMasterDataAttributeResponse().isRCHD_OTP()) {
                        otpLayout(true, "RCHD_OTP");
                        Constants.rD_OTP_API_CHECK = true;
                        Constants.uD_OTP_API_CHECK = false;
                    } else {
                        otpLayout(false, "");
                        Constants.rD_OTP_API_CHECK = false;
                        Constants.uD_OTP_API_CHECK = false;
                    }
                    if (forwardReasonCodeMaster.getMasterDataAttributeResponse().iscALLM()) {
                        is_call_mandatory = !Constants.CONSIGNEE_PROFILE || meterRange >= undeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE();
                    } else {
                        is_call_mandatory = false;
                    }
                    if (forwardReasonCodeMaster.getMasterDataAttributeResponse().isiMG()) {
                        mActivityUndeliveredBinding.imgMandatTv.setText("MDT");
                        if (isImageCaptured) {
                            isImageCaptured = false;
                            mActivityUndeliveredBinding.image.setImageBitmap(null);
                            mActivityUndeliveredBinding.image.setImageResource(R.drawable.cam);
                        }
                    } else {
                        mActivityUndeliveredBinding.imgMandatTv.setText("");
                        if (isImageCaptured) {
                            isImageCaptured = false;
                            mActivityUndeliveredBinding.image.setImageBitmap(null);
                            mActivityUndeliveredBinding.image.setImageResource(R.drawable.cam);
                        }
                    }
                    forwardCommit.setAttempt_reason_code(forwardReasonCodeMaster.getReasonCode().toString());
                    if (undeliveredViewModel.getDataManager().isCounterDelivery() && undeliveredViewModel.getCounterDeliveryRange() < undeliveredViewModel.getDataManager().getDCRANGE()) {
                    } else if (forwardReasonCodeMaster.getMasterDataAttributeResponse().iscALLM()) {
                        if (call_allowed) {
                            callMandatory();
                        }
                        if (forwardReasonCodeMaster.getMasterDataAttributeResponse().isrCHD()) {
                            rescheduleMandatory();
                        } else {
                        }
                    } else if (forwardReasonCodeMaster.getMasterDataAttributeResponse().isrCHD()) {
                        rescheduleMandatory();
                    } else {
                        mActivityUndeliveredBinding.flagIsRescheduled.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
                showError(e.getMessage());
            }
        } else {
            mActivityUndeliveredBinding.llUdOtp.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void rescheduleMandatory() {
        undeliveredViewModel.getShipmentRescheduleDetails(String.valueOf(awb));
        mActivityUndeliveredBinding.date.setText("Pick Date");
    }

    private void callMandatory() {
        if (isCall == 0 && undeliveredViewModel.getDataManager().getCallClicked(awb + "ForwardCall")) {
            makeCallDialog();
        } else {
            try {
                if (!forwardReasonCodeMaster.getMasterDataAttributeResponse().isrCHD()) {
                    mActivityUndeliveredBinding.flagIsRescheduled.setVisibility(View.GONE);
                } else {
                    undeliveredViewModel.getShipmentRescheduleDetails(String.valueOf(awb));
                }
            } catch (Exception e) {
                Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
                showError(e.getMessage());
            }
        }
    }


    @Override
    public void OnSubmitClick() {

        forward_call_count = 0;
        undeliveredViewModel.call_alert_number = 0;
        undeliveredViewModel.isCallRecursionDailogRunning = true;
        undeliveredViewModel.isStopRecursion = false;
        forwardCommit.setCall_attempt_count(undeliveredViewModel.getDataManager().getForwardCallCount(awb + "FWD"));
        forwardCommit.setMap_activity_count(undeliveredViewModel.getDataManager().getForwardMapCount(Long.parseLong(awb)));
        forwardCommit.setChange_received_confirmation("Y");
        forwardCommit.setCollectable_value(collectedValue);
        forwardCommit.setOfd_customer_otp(Constants.PLAIN_OTP);
        forwardCommit.setOfd_otp_verified(String.valueOf(Constants.OFD_OTP_VERIFIED));
        undeliveredViewModel.getRemarkCount(Long.parseLong(forwardCommit.getAwb()));
        forwardCommit.setPayment_id(null);
        forwardCommit.setPayment_mode(null);
        forwardCommit.setIs_obd(is_obd);
        try {
            if (this.forwardReasonCodeMaster == null || this.forwardReasonCodeMaster.getReasonCode() == -1 || groupName.equalsIgnoreCase(Constants.SELECT)) {
                showSnackbar(getString(R.string.select_reason_code));
                return;
            }
            if (undeliveredViewModel.ud_otp_commit_status.equalsIgnoreCase("none") && uD_OTP) {
                showSnackbar("Please Verify OTP");
                return;
            }
            if (forwardReasonCodeMaster.getMasterDataAttributeResponse().cALLM && undeliveredViewModel.getDataManager().getCallClicked(forwardCommit.getAwb() + "ForwardCall")) {
                makeCallDialog();
                return;
            }
            if (undeliveredViewModel.getDataManager().getSMSThroughWhatsapp() && forwardReasonCodeMaster.getMasterDataAttributeResponse().isWHATSAPP_MAND() && undeliveredViewModel.getDataManager().getTryReachingCount(forwardCommit.getAwb() + Constants.TRY_RECHING_COUNT) == 0) {
                String template = CommonUtils.getWhatsAppRemarkTemplate(undeliveredViewModel.getDataManager().getName(), undeliveredViewModel.getDataManager().getMobile(), awb, drsFWDResponse.getShipmentDetails().getShipper());
                showWhatsAppDialog(template);
                return;
            }
            if (forwardReasonCodeMaster.getMasterDataAttributeResponse().isiMG()) {
                if (!isImageCaptured) {
                    Toast.makeText(getApplicationContext(), getString(R.string.capture_image), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } catch (Exception e) {
            Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
            showError(e.getMessage());
        }
        if (mActivityUndeliveredBinding.flagIsRescheduled.getVisibility() == View.VISIBLE) {
            if (mActivityUndeliveredBinding.date.getText().toString().equalsIgnoreCase(getString(R.string.pick_dates))) {
                showSnackbar(getString(R.string.pick_dates));
                return;
            }
        }
        try {
            if (Constants.shipment_undelivered_count >= undeliveredViewModel.getDataManager().getUndeliverCount()) {
                if (undeliveredViewModel.getDataManager().getCallClicked(forwardCommit.getAwb() + "ForwardCall") && Boolean.FALSE.equals(undeliveredViewModel.ud_otp_verified_status.get())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_Material3_Light_Dialog_Alert);
                    builder.setMessage("Three or more consecutive shipments cannot be marked as UD without a call. Please call consignee.");
                    builder.setPositiveButton("CALL", (dialog, which) -> {
                        makeCallDialog();
                        dialog.dismiss();
                    });
                    Dialog dialog = builder.create();
                    dialog.show();
                } else if (undeliveredViewModel.getDataManager().getofflineFwd()) {
                    undelivered(false);
                } else if (undeliveredViewModel.getDataManager().isCounterDelivery() && undeliveredViewModel.getCounterDeliveryRange() < undeliveredViewModel.getDataManager().getDCRANGE()) {
                    undelivered(false);
                } else if (undeliveredViewModel.getDataManager().getDcUndeliverStatus()) {
                    if (undeliveredViewModel.getCounterDeliveryRange() < undeliveredViewModel.getDataManager().getDCRANGE()) {
                        showError("Shipment cannot be marked undelivered within the DC");
                    } else {
                        markDeliverOrFail(false);
                    }
                } else {
                    markDeliverOrFail(false);
                }
            } else if (undeliveredViewModel.getDataManager().getofflineFwd()) {
                undelivered(false);
            } else if (undeliveredViewModel.getDataManager().isCounterDelivery() && undeliveredViewModel.getCounterDeliveryRange() < undeliveredViewModel.getDataManager().getDCRANGE()) {
                undelivered(false);
            } else if (undeliveredViewModel.getDataManager().getDcUndeliverStatus()) {
                if (undeliveredViewModel.getCounterDeliveryRange() < undeliveredViewModel.getDataManager().getDCRANGE()) {
                    showError("Shipment cannot be marked undelivered within the DC");
                } else {
                    markDeliverOrFail(false);
                }
            } else {
                markDeliverOrFail(false);
            }
        } catch (Exception e) {
            Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
        }
    }

    private void updateSyncStatusInDRSFWDTable(String composite_key, boolean i) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(undeliveredViewModel.getDataManager().updateSameDayReassignSyncStatusFWD(composite_key, i).subscribeOn(undeliveredViewModel.getSchedulerProvider().io()).observeOn(undeliveredViewModel.getSchedulerProvider().io()).subscribe(aBoolean -> {
            try {
                CompositeDisposable compositeDisposable1 = new CompositeDisposable();
                compositeDisposable1.add(undeliveredViewModel.getDataManager().updateForwardStatus(composite_key, 0).subscribeOn(undeliveredViewModel.getSchedulerProvider().io()).observeOn(undeliveredViewModel.getSchedulerProvider().io()).subscribe(aBoolean1 -> {

                }));
            } catch (Exception e) {
                Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
            }
        }, throwable -> {
        }));
    }

    void markDeliverOrFail(boolean failFlag) {
        if (Constants.SAME_DAY_REASSIGN_VERIFIED.equalsIgnoreCase("VERIFIED")) {
            updateSyncStatusInDRSFWDTable(composite_key, false);
        }
        @SuppressLint("SimpleDateFormat") int current_time = Integer.parseInt(new SimpleDateFormat("HH").format(new Date()));
        if (Constants.CONSIGNEE_PROFILE && meterRange < undeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE()) {
            undelivered(failFlag);
        } else if (undeliveredViewModel.FEInDCZone(undeliveredViewModel.getDataManager().getCurrentLatitude(), undeliveredViewModel.getDataManager().getCurrentLongitude(), undeliveredViewModel.getDataManager().getDCLatitude(), undeliveredViewModel.getDataManager().getDCLongitude())) {
            if (!undeliveredViewModel.getDataManager().getDirectUndeliver()) {
                undelivered(failFlag);
            } else {
                if (call_allowed) {
                    undeliveredViewModel.callApi(drsFWDResponse.getFlags().getFlagMap().getIs_callbridge_enabled(), failFlag, forwardCommit.getAwb(), forwardCommit.getDrs_id());
                } else {
                    undelivered(failFlag);
                }
            }
        } else if (current_time >= 21 || current_time <= 6) {
            if (!undeliveredViewModel.getDataManager().getDirectUndeliver()) {
                undelivered(failFlag);
            } else {
                if (!call_allowed) {
                    undeliveredViewModel.callApi(drsFWDResponse.getFlags().getFlagMap().getIs_callbridge_enabled(), failFlag, forwardCommit.getAwb(), forwardCommit.getDrs_id());
                } else {
                    undelivered(failFlag);
                }
            }
        } else if ((TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis()) - TimeUnit.MILLISECONDS.toMinutes(undeliveredViewModel.getDataManager().getDRSTimeStamp())) <= 7/*undeliveredViewModel.getDateCurrentTimeZone(undeliveredViewModel.getCurrentTime())-undeliveredViewModel.getDateCurrentTimeZone(undeliveredViewModel.getDataManager().getDRSTimeStamp())<=7*/) {
            if (!undeliveredViewModel.getDataManager().getDirectUndeliver()) {
                undelivered(failFlag);
            } else {
                if (call_allowed && is_call_mandatory) {
                    undeliveredViewModel.callApi(drsFWDResponse.getFlags().getFlagMap().getIs_callbridge_enabled(), failFlag, forwardCommit.getAwb(), forwardCommit.getDrs_id());
                } else {
                    undelivered(failFlag);
                }
            }
        } else {
            if (is_call_mandatory) {
                if (!undeliveredViewModel.getDataManager().getDirectUndeliver()) {
                    undelivered(failFlag);
                } else {
                    if (call_allowed) {
                        undeliveredViewModel.callApi(drsFWDResponse.getFlags().getFlagMap().getIs_callbridge_enabled(), failFlag, forwardCommit.getAwb(), forwardCommit.getDrs_id());
                    } else {
                        undelivered(failFlag);
                    }
                }
            } else {
                undelivered(failFlag);
            }
        }

    }


    @Override
    public void onHandleError(ErrorResponse errorResponse) {
        showSnackbar(errorResponse.getEResponse().getDescription());
    }

    @Override
    public void onChooseGroupSpinner(String groupName) {
        this.groupName = groupName;
        mActivityUndeliveredBinding.flagIsRescheduled.setVisibility(View.GONE);
        mActivityUndeliveredBinding.otpSkip.setChecked(false);
        undeliveredViewModel.ud_otp_commit_status = "NONE";
        undeliveredViewModel.ud_otp_commit_status_field.set("NONE");
        undeliveredViewModel.rd_otp_commit_status = "NONE";
        undeliveredViewModel.rd_otp_commit_status_field.set("NONE");
        if (groupName.equalsIgnoreCase(Constants.SELECT)) {
            mActivityUndeliveredBinding.imgMandatTv.setText("");
            if (isImageCaptured) {
                isImageCaptured = false;
                mActivityUndeliveredBinding.image.setImageBitmap(null);
                mActivityUndeliveredBinding.image.setImageResource(R.drawable.cam);
            }
            mActivityUndeliveredBinding.childGroupLayout.setVisibility(View.GONE);
            mActivityUndeliveredBinding.flagIsRescheduled.setVisibility(View.GONE);
            mActivityUndeliveredBinding.llUdOtp.setVisibility(View.GONE);
        } else mActivityUndeliveredBinding.childGroupLayout.setVisibility(View.VISIBLE);
        mActivityUndeliveredBinding.spinnerChildType.performClick();
        mActivityUndeliveredBinding.imgMandatTv.setText("");
        if (isImageCaptured) {
            isImageCaptured = false;
            mActivityUndeliveredBinding.image.setImageBitmap(null);
            mActivityUndeliveredBinding.image.setImageResource(R.drawable.cam);
        }
    }

    @Override
    public void onChooseChildSpinner(ForwardReasonCodeMaster forwardReasonCodeMaster) {
        this.forwardReasonCodeMaster = forwardReasonCodeMaster;
        String template = CommonUtils.getWhatsAppRemarkTemplate(undeliveredViewModel.getDataManager().getName(), undeliveredViewModel.getDataManager().getMobile(), awb, drsFWDResponse.getShipmentDetails().getShipper());
        if (undeliveredViewModel.getDataManager().getSMSThroughWhatsapp() && forwardReasonCodeMaster.getMasterDataAttributeResponse().isWHATSAPP_MAND() && undeliveredViewModel.getDataManager().getTryReachingCount(awb + Constants.TRY_RECHING_COUNT) == 0) {
            showWhatsAppDialog(template);
        } else {
            executeOnChooseChildSpinner(forwardReasonCodeMaster);
        }
    }

    private void showWhatsAppDialog(String template) {
        CommonUtils.showDialogBoxForWhatsApp(getActivityContext()).setPositiveButton("OK", (dialog, id) -> {
            try {
                CommonUtils.sendSMSViaWhatsApp(this, this, drsFWDResponse.getConsigneeDetails().getMobile(), template);
                undeliveredViewModel.getDataManager().setTryReachingCount(awb + Constants.TRY_RECHING_COUNT, undeliveredViewModel.getDataManager().getTryReachingCount(awb + Constants.TRY_RECHING_COUNT) + 1);
                String remarks = GlobalConstant.RemarksTypeConstants.CONSIGNEE_NOT_PICKING_CALL;
                Remark remark = new Remark();
                remark.remark = remarks;
                remark.empCode = undeliveredViewModel.getEmployeeCode();
                remark.awbNo = Long.parseLong(awb);
                remark.count = undeliveredViewModel.getDataManager().getTryReachingCount(awb + Constants.TRY_RECHING_COUNT);
                remark.date = TimeUtils.getDateYearMonthMillies();
                undeliveredViewModel.addRemarks(remark);

                // Moving next after whatsapp click:-
                executeOnChooseChildSpinner(forwardReasonCodeMaster);
            } catch (Exception e) {
                Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
            }
        }).setOnCancelListener(dialog -> executeOnChooseChildSpinner(forwardReasonCodeMaster)).show();
    }

    public void executeOnChooseChildSpinner(ForwardReasonCodeMaster forwardReasonCodeMaster) {
        this.forwardReasonCodeMaster = forwardReasonCodeMaster;
        mActivityUndeliveredBinding.otpSkip.setChecked(false);
        undeliveredViewModel.ud_otp_commit_status = "NONE";
        undeliveredViewModel.ud_otp_commit_status_field.set("NONE");
        undeliveredViewModel.rd_otp_commit_status = "NONE";
        undeliveredViewModel.rd_otp_commit_status_field.set("NONE");
        SetVisibility(forwardReasonCodeMaster);
    }

    @Override
    public void visible(boolean flag) {
        if (flag) {
            mActivityUndeliveredBinding.groupLayout.setVisibility(View.VISIBLE);
            mActivityUndeliveredBinding.layout.setVisibility(View.GONE);
        } else {
            mActivityUndeliveredBinding.groupLayout.setVisibility(View.GONE);
            mActivityUndeliveredBinding.layout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showError(String error) {
        showSnackbar(error);
    }

    @Override
    public void dismissCallDialog() {
        try {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
        }
    }


    @Override
    public void setChildSpinnerValues(List<String> childSpinnerValues) {
        mActivityUndeliveredBinding.spinnerChildType.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_single_item, R.id.spinner_text_view, childSpinnerValues));
    }

    @Override
    public void setParentGroupSpinnerValues(List<String> parentSpinnerValues) {
        mActivityUndeliveredBinding.spinnerGroupType.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_single_item, R.id.spinner_text_view, parentSpinnerValues));
    }

    @Override
    public void setSpinner(List<String> spinnerReasonValues) {
        mActivityUndeliveredBinding.spinnerVehicleType.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_single_item, R.id.spinner_text_view, spinnerReasonValues));
    }

    @Override
    public void getCallConfig(CallbridgeConfiguration mymasterDataReasonCodeResponse) {
        callbridgeConfiguration = mymasterDataReasonCodeResponse;
    }

    @Override
    public void getGlobal(List<GlobalConfigurationMaster> globalConfigurationMasterList) {
    }

    @Override
    public void isCall(int isCallmade) {
        isCall = isCallmade;
    }

    @Override
    public void showErrorMessage(boolean status) {
        if (status) showSnackbar(getString(R.string.http_500_msg));
        else showSnackbar(getString(R.string.server_down_msg));
    }

    @Override
    public void onDRSForwardItemFetch(DRSForwardTypeResponse drsForwardTypeResponse) {
        try {
            this.drsFWDResponse = drsForwardTypeResponse;
            undeliveredViewModel.checkMeterRange(drsFWDResponse);
            undeliveredViewModel.inscan_date = drsFWDResponse.getInscan_date();
            undeliveredViewModel.total_attempts = drsFWDResponse.getTotal_attempts();
        } catch (Exception e) {
            Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
            showError(e.getMessage());
        }
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
    public void setBitmap() {
        mimageView.setImageBitmap(mbitmap);
    }

    @Override
    public void onChooseDateSpinner(String valid_date) {
        try {
            if (valid_date != null && !valid_date.equalsIgnoreCase("select")) {
                mActivityUndeliveredBinding.date.setText(valid_date);
                Date d = undeliveredViewModel.convertStringtoDate(valid_date);
                dateSet = String.valueOf(d.getTime());
            }
        } catch (Exception e) {
            Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
        }
    }

    @Override
    public void setDateSpinner(List<String> validDays) {
        mActivityUndeliveredBinding.spinnerDates.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_single_item, R.id.spinner_text_view, validDays));
        openSpinner();
    }

    @Override
    public void pickDateVisibility() {
        mActivityUndeliveredBinding.flagIsRescheduled.setVisibility(View.GONE);
    }


    private void undelivered(boolean failFlag) {
        try {
            if (Constants.uD_OTP_API_CHECK && undeliveredViewModel.ud_otp_commit_status_field.get().equalsIgnoreCase("VERIFIED")) {
                forwardCommit.setUd_otp(undeliveredViewModel.ud_otp_commit_status_field.get());
                forwardCommit.setRd_otp("NONE");
            } else if (Constants.rD_OTP_API_CHECK && undeliveredViewModel.ud_otp_commit_status_field.get().equalsIgnoreCase("VERIFIED")) {
                forwardCommit.setRd_otp("VERIFIED");
                forwardCommit.setUd_otp("NONE");
            } else {
                forwardCommit.setUd_otp(undeliveredViewModel.ud_otp_commit_status_field.get());
                forwardCommit.setRd_otp(undeliveredViewModel.rd_otp_commit_status_field.get());
            }
            Calendar calendar = Calendar.getInstance();
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
            int mMonth = calendar.get(Calendar.MONTH) + 1;
            if (undeliveredViewModel.loginDate().equalsIgnoreCase(String.valueOf(mDay)) && undeliveredViewModel.getDataManager().getLoginMonth() == mMonth) {
                if (!failFlag) {
                    String dialog_message = getString(R.string.commitdialog);
                    String positiveButtonText = getString(R.string.yes);
                    if (Constants.CONSIGNEE_PROFILE && meterRange > undeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE() && undeliveredViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("W")) {
                        dialog_message = "You are not attempting the shipment at Consignee’s location. Your current location = " + undeliveredViewModel.getDataManager().getCurrentLatitude() + ", " + undeliveredViewModel.getDataManager().getCurrentLongitude() + " You are " + meterRange + " meter away from consignee location. \nAre you sure you want to commit?";
                        positiveButtonText = getString(R.string.yes);
                    } else if (Constants.CONSIGNEE_PROFILE && meterRange > undeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE() && undeliveredViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("R")) {
                        dialog_message = "You are not allowed to commit this shipment as you are not attempting at consignee location. your current location = " + undeliveredViewModel.getDataManager().getCurrentLatitude() + ", " + undeliveredViewModel.getDataManager().getCurrentLongitude() + " You are " + meterRange + " meter away from consignee location";
                        positiveButtonText = getString(R.string.ok);
                    } else if (Constants.CONSIGNEE_PROFILE && meterRange > undeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE()) {
                        if (NetworkUtils.isNetworkConnected(UndeliveredActivity.this)) {
                            undeliveredViewModel.createCommitPacketCashCollection(forwardCommit, forwardReasonCodeMaster.getReasonCode(), dateSet, composite_key);
                        } else {
                            undeliveredViewModel.onUndeliveredApiCall(UndeliveredActivity.this, forwardReasonCodeMaster.getReasonCode(), dateSet, composite_key);
                        }
                        return;
                    } else if (Constants.CONSIGNEE_PROFILE) {
                        if (NetworkUtils.isNetworkConnected(UndeliveredActivity.this)) {
                            undeliveredViewModel.createCommitPacketCashCollection(forwardCommit, forwardReasonCodeMaster.getReasonCode(), dateSet, composite_key);
                        } else {
                            undeliveredViewModel.onUndeliveredApiCall(UndeliveredActivity.this, forwardReasonCodeMaster.getReasonCode(), dateSet, composite_key);
                        }
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_Material3_Light_Dialog_Alert);
                    builder.setCancelable(false);
                    builder.setMessage(dialog_message);
                    forwardCommit.setLocation_verified(meterRange <= undeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE());
                    builder.setPositiveButton(positiveButtonText, (dialog, which) -> {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        if (undeliveredViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("R") && meterRange > undeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE()) {
                            dialog.dismiss();
                            return;
                        } else if (consigneeProfiling && meterRange > undeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE()) {
                            dialog.dismiss();
                        }
                        if (!mActivityUndeliveredBinding.remarks.getText().toString().isEmpty()) {
                            forwardCommit.setFe_comments(mActivityUndeliveredBinding.remarks.getText().toString());
                        }
                        if (NetworkUtils.isNetworkConnected(UndeliveredActivity.this)) {
                            undeliveredViewModel.createCommitPacketCashCollection(forwardCommit, forwardReasonCodeMaster.getReasonCode(), dateSet, composite_key);
                        } else {
                            undeliveredViewModel.onUndeliveredApiCall(UndeliveredActivity.this, forwardReasonCodeMaster.getReasonCode(), dateSet, composite_key);
                        }
                    });
                    if (!(consigneeProfiling && meterRange > undeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE())) {
                        builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel());
                    }
                    AlertDialog alert = builder.create();
                    alert.show();
                    return;
                }
                if (!mActivityUndeliveredBinding.remarks.getText().toString().isEmpty()) {
                    forwardCommit.setFe_comments(mActivityUndeliveredBinding.remarks.getText().toString());
                }
                if (NetworkUtils.isNetworkConnected(UndeliveredActivity.this)) {
                    undeliveredViewModel.createCommitPacketCashCollection(forwardCommit, forwardReasonCodeMaster.getReasonCode(), dateSet, composite_key);
                } else {
                    undeliveredViewModel.onUndeliveredApiCall(UndeliveredActivity.this, forwardReasonCodeMaster.getReasonCode(), dateSet, composite_key);
                }
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
            Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
        }
    }

    @Override
    public void undeliverShipment(boolean failFlag, boolean callFlag) {
        if (callFlag) {
            undelivered(failFlag);
        } else {
            showDialog();
        }
    }

    @Override
    public Activity getActivityContext() {
        return this;
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UndeliveredActivity.this,R.style.Theme_Material3_Light_Dialog_Alert);
        String AlertText1 = "Attention : ";
        builder.setMessage(AlertText1 + getString(R.string.undelivered_shipment_msg)).setCancelable(false).setPositiveButton("Call", (dialog, id) -> {
            try {
                makeCallDialog();
            } catch (Exception e) {
                Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onResult(String s) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            FlashlightProvider flashlightProvider = new FlashlightProvider(this);
            flashlightProvider.turnFlashOff();
        } catch (Exception e) {
            Logger.e(UndeliveredActivity.class.getName(), e.getMessage());
        }

    }

    /**
     * @param isAwbMatch-- this true/false value is used for checking the awb match or not
     */
    @Override
    public void setStatusOfAwb(boolean isAwbMatch) {
        if (isAwbMatch) {
            scannedStatus = true;
            showToast("AWB matched");
        } else {
            scannedStatus = false;
        }
    }

    @Override
    public void setStatusOfAwb(boolean isAwbMatch, String manualBP) {

    }


    public void showCallBridgeDialog() {
        Dialog dialog = new Dialog(this, R.style.RoundedCornersDialog);
        dialog.setContentView(R.layout.dialog_callbridge);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Button call = dialog.findViewById(R.id.bt_call);
        call.setText(drsFWDResponse.getCallbridge_details().get(0).getCallbridge_number());
        Button altCall = dialog.findViewById(R.id.bt_sms);
        altCall.setText(drsFWDResponse.getCallbridge_details().get(1).getCallbridge_number());
        ImageView crossDialog = dialog.findViewById(R.id.crssdialog);
        crossDialog.setOnClickListener(v -> dialog.dismiss());
        call.setOnClickListener(v -> {
            undeliveredViewModel.consigneeContactNumber.set(drsFWDResponse.getCallbridge_details().get(0).getCallbridge_number()+","+drsFWDResponse.getCallbridge_details().get(0).getPin()+"#");
            forward_call_count = forward_call_count + 1;
            undeliveredViewModel.getDataManager().setForwardCallCount(awb + "FWD", forward_call_count);
            Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + drsFWDResponse.getCallbridge_details().get(0).getCallbridge_number()+","+drsFWDResponse.getCallbridge_details().get(0).getPin()+"#"));
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
            dialog.dismiss();
        });
        altCall.setOnClickListener(v -> {
            undeliveredViewModel.consigneeContactNumber.set(drsFWDResponse.getCallbridge_details().get(1).getCallbridge_number()+","+drsFWDResponse.getCallbridge_details().get(1).getPin()+"#");
            forward_call_count = forward_call_count + 1;
            undeliveredViewModel.getDataManager().setForwardCallCount(awb + "FWD", forward_call_count);
            Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + drsFWDResponse.getCallbridge_details().get(1).getCallbridge_number()+","+drsFWDResponse.getCallbridge_details().get(1).getPin()+"#"));
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
            dialog.dismiss();
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
    public void showDirectCallDialog() {
        Dialog dialog = new Dialog(this, R.style.RoundedCornersDialog);
        dialog.setContentView(R.layout.dialog_direct_call);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Button call = dialog.findViewById(R.id.bt_call);
        Button altcall = dialog.findViewById(R.id.bt_sms);
        ImageView crssdialog = dialog.findViewById(R.id.crssdialog);
        crssdialog.setOnClickListener(v -> dialog.dismiss());
        call.setOnClickListener(v -> {
            undeliveredViewModel.consigneeContactNumber.set(Constants.ConsigneeDirectMobileNo);
            forward_call_count = forward_call_count + 1;
            undeliveredViewModel.getDataManager().setForwardCallCount(awb + "FWD", forward_call_count);
            Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Constants.ConsigneeDirectMobileNo));
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
            dialog.dismiss();
        });
        altcall.setOnClickListener(v -> {
            undeliveredViewModel.consigneeContactNumber.set(ConsigneeDirectAlternateMobileNo);
            forward_call_count = forward_call_count + 1;
            undeliveredViewModel.getDataManager().setForwardCallCount(awb + "FWD", forward_call_count);
            Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ConsigneeDirectAlternateMobileNo));
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
            dialog.dismiss();
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


}
