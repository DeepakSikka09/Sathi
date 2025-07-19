package in.ecomexpress.sathi.ui.drs.rvp.undelivered;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logButtonEventInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.Constants.ConsigneeDirectAlternateMobileNo;
import static in.ecomexpress.sathi.utils.Constants.INTENT_KEY_RVP_WITH_QC;
import static in.ecomexpress.sathi.utils.Constants.forward_call_count;
import static in.ecomexpress.sathi.utils.Constants.rvp_call_count;
import static in.ecomexpress.sathi.utils.Constants.shipment_type;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.TextUtils;
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityRvpUndeliveredBinding;
import in.ecomexpress.sathi.repo.local.data.rvp.RvpCommit;
import in.ecomexpress.sathi.repo.local.db.model.RVPUndeliveredReasonCodeList;
import in.ecomexpress.sathi.repo.local.db.model.Remark;
import in.ecomexpress.sathi.repo.local.db.model.RvpWithQC;
import in.ecomexpress.sathi.repo.remote.model.ErrorResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.masterdata.RVPReasonCodeMaster;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;
import in.ecomexpress.sathi.ui.drs.rvp.success.RVPSuccessActivity;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.Helper;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.NetworkUtils;
import in.ecomexpress.sathi.utils.TimeUtils;
import in.ecomexpress.sathi.utils.cameraView.CameraActivity;

@AndroidEntryPoint
public class RVPUndeliveredActivity extends BaseActivity<ActivityRvpUndeliveredBinding, RVPUndeliveredViewModel> implements IRVPUndeliveredNavigator {

    private final String TAG = RVPUndeliveredActivity.class.getSimpleName();
    private final RvpCommit rvpCommit = new RvpCommit();
    @Inject
    RVPUndeliveredViewModel rvpUndeliveredViewModel;
    boolean dateFlag = false, slotFlag = false, is_call_mandatory;
    public static int imageCaptureCount = 0;
    String slotId = "", dateSet = "";
    Dialog dialog;
    String groupName = Constants.SELECT;
    String getDrsApiKey = null, getDrsPstKey = null, getCbConfigCallType = null, MasterPstFormat = null, getDrsPin = null, composite_key = null;
    String isSecureUndelivered = null;
    String drs_id;
    String OFD_OTP = "";
    Long awbNo = null;
    ImageView imageView;
    Bitmap bitmap_server;
    int check_call_attempted;
    boolean call_allowed;
    String consignee_mobile, consignee_alternate_mobile = "";
    boolean uD_OTP = false;
    private ActivityRvpUndeliveredBinding mActivityUndeliveredBinding;
    private Boolean isImageCaptured = false;
    private RvpWithQC rvpWithQC;
    private RVPReasonCodeMaster rvpReasonCodeMaster;
    private int mYear;
    private int mMonth;
    private int mDay;
    private DRSReverseQCTypeResponse rvpResponse;
    private int meterRange;
    private boolean consigneeProfiling = false;
    CountDownTimer mCountDownTimer = null;
    boolean isWhatsappMessaged = false;
    private static final int CAMERA_REQUEST_CODE = 100;
    Bitmap capturedImageBitmap;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, RVPUndeliveredActivity.class);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rvpUndeliveredViewModel.setNavigator(this);
        logScreenNameInGoogleAnalytics(TAG, this);
        try {
            getDrsApiKey = Objects.requireNonNull(getIntent().getExtras()).getString(Constants.DRS_API_KEY);
            getDrsPstKey = getIntent().getExtras().getString(Constants.DRS_PSTN_KEY);
            composite_key = getIntent().getExtras().getString(Constants.COMPOSITE_KEY, "");
            getDrsPin = getIntent().getExtras().getString(Constants.DRS_PIN);
            drs_id = String.valueOf(Constants.TEMP_DRSID);
            awbNo = getIntent().getLongExtra("awb", 0);
            consignee_mobile = getIntent().getExtras().getString(Constants.CONSIGNEE_MOBILE);
            if (getIntent().getStringExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE) != null) {
                consignee_alternate_mobile = getIntent().getStringExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE);
            }
            consignee_mobile = Constants.TEMP_CONSIGNEE_MOBILE;
            call_allowed = getIntent().getBooleanExtra("call_allowed", false);
            rvpUndeliveredViewModel.getDataManager().setLoginPermission(false);
            mActivityUndeliveredBinding = getViewDataBinding();
            isSecureUndelivered = getIntent().getExtras().getString(Constants.SECURE_UNDELIVERED);
            Constants.LOCATION_ACCURACY = rvpUndeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE();
            OFD_OTP = Constants.TEMP_OFD_OTP;
            rvpWithQC = getIntent().getParcelableExtra(INTENT_KEY_RVP_WITH_QC);
            assert rvpWithQC != null;
            String shipmentType = getIntent().getStringExtra(Constants.SHIPMENT_TYPE);
            Constants.RVPCOMMIT = shipmentType;
            shipment_type = shipmentType;
            String isSecure;
            if (isSecureUndelivered != null) {
                isSecure = isSecureUndelivered;
            } else {
                isSecure = "false";
            }
            rvpUndeliveredViewModel.getAllUndeliveredReasonCode(isSecure);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.rvp));

            // set commit packet to viewmodel commit packet
            rvpUndeliveredViewModel.onRvpDRSCommit(rvpCommit);
            mActivityUndeliveredBinding.awb.setText(awbNo.toString());

            //init the commit packet with needed data to set
            initRVPCommitPacket(rvpWithQC);
            rvpUndeliveredViewModel.setRVPCommitPacket(rvpCommit);
            //get the call status from the preference class
            rvpUndeliveredViewModel.getCallStatus(Long.parseLong(rvpCommit.getAwb()), Integer.parseInt(rvpCommit.getDrsId()));
            mActivityUndeliveredBinding.tvNumberStatement.setText(String.format("We Have Send OTP On Registered Mobile Number: %s", consignee_mobile != null ? consignee_mobile : ""));
            //fetch the RVP shipment from the DB as per composite key
            rvpUndeliveredViewModel.fetchRVPShipment(composite_key.trim());
            mActivityUndeliveredBinding.remarksEdt.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
            mActivityUndeliveredBinding.remarks.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
            mActivityUndeliveredBinding.remarksEdt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
            mActivityUndeliveredBinding.remarks.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
            mActivityUndeliveredBinding.scrollView.setFillViewport(true);
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage());
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public RVPUndeliveredViewModel getViewModel() {
        return rvpUndeliveredViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_rvp_undelivered;
    }

    private void initRVPCommitPacket(RvpWithQC rvpWithQC) {
        try {
            rvpCommit.setAddressType("");
            rvpCommit.setAttemptType(Constants.RVPCOMMIT);
            rvpCommit.setDrsCommitDateTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            rvpCommit.setAwb(Long.toString(rvpWithQC.drsReverseQCTypeResponse.getAwbNo()));
            rvpCommit.setDrsId(Long.toString(rvpWithQC.drsReverseQCTypeResponse.getDrs()));
            rvpCommit.setConsigneeName(rvpWithQC.drsReverseQCTypeResponse.getConsigneeDetails().getName());
            rvpCommit.setFeComment("NONE");
            rvpCommit.setImageData(new ArrayList<>());
            try {
                if (Constants.CURRENT_LATITUDE != null && Constants.CURRENT_LONGITUDE != null) {
                    rvpCommit.setLocationLat(Constants.CURRENT_LATITUDE);
                    rvpCommit.setLocationLong(Constants.CURRENT_LONGITUDE);
                } else {
                    rvpCommit.setLocationLat(String.valueOf(in.ecomexpress.geolocations.Constants.latitude));
                    rvpCommit.setLocationLong(String.valueOf(in.ecomexpress.geolocations.Constants.longitude));
                }
            } catch (Exception e) {
                Logger.e(TAG, e.getMessage());
            }
            rvpCommit.setPackageBarcode("");
            rvpCommit.setRefPackageBarcode("");
            rvpCommit.setQcWizard(new ArrayList<>());
            rvpCommit.setReceived_by("ECOM");
            rvpCommit.setDrsDate(rvpWithQC.drsReverseQCTypeResponse.getAssignedDate() + "");
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage());
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void onChooseReasonSpinner(RVPUndeliveredReasonCodeList rvpUndeliveredReasonCodeList) {
        if (rvpUndeliveredReasonCodeList.getRvpReasonCodeMaster().getMasterDataAttributeResponse().iscALLM()) {
            is_call_mandatory = Constants.CONSIGNEE_PROFILE || meterRange >= rvpUndeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE();
        } else {
            is_call_mandatory = false;
        }
        mActivityUndeliveredBinding.otpSkip.setChecked(false);
        rvpUndeliveredViewModel.ud_otp_commit_status = "NONE";
        rvpUndeliveredViewModel.ud_otp_commit_status_field.set("NONE");
        rvpUndeliveredViewModel.rd_otp_commit_status = "NONE";
        rvpUndeliveredViewModel.rd_otp_commit_status_field.set("NONE");
        this.rvpReasonCodeMaster = rvpUndeliveredReasonCodeList.getRvpReasonCodeMaster();
        groupName = rvpUndeliveredReasonCodeList.getRvpReasonCodeMaster().getReasonMessage();
        SetViewsVisibility(rvpUndeliveredReasonCodeList);
    }

    //set the visibility of the views as per reasonCode of RVP
    public void SetViewsVisibility(RVPUndeliveredReasonCodeList rvpReasonCodeMaster) {
        try {
            if (rvpReasonCodeMaster != null) {
                mActivityUndeliveredBinding.tvMandate.setText("");
                if (isImageCaptured) {
                    isImageCaptured = false;
                    mActivityUndeliveredBinding.image.setImageBitmap(null);
                    mActivityUndeliveredBinding.image.setImageResource(R.drawable.cam);
                }
                if (rvpReasonCodeMaster.getRvpReasonCodeMaster().getMasterDataAttributeResponse().iscALLM()) {
                    callMandatory();
                } else if (rvpReasonCodeMaster.getRvpReasonCodeMaster().getMasterDataAttributeResponse().isrCHD())
                    rescheduleMandatory();
                else {
                    mActivityUndeliveredBinding.flagIsRescheduled.setVisibility(View.GONE);
                }
                rvpCommit.setAttemptReasonCode(rvpReasonCodeMaster.getRvpReasonCodeMaster().getReasonCode().toString());
            }
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage());
            showSnackbar(e.getMessage());
        }
    }

    private void callMandatory() {
        try {
            rvpUndeliveredViewModel.getIsCallAttempted(rvpCommit.getAwb());
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage());
            showSnackbar(e.getMessage());
        }
    }

    private void rescheduleMandatory() {
        mActivityUndeliveredBinding.flagIsRescheduled.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCaptureImage() {
        if (!CommonUtils.isAllPermissionAllow(this)) {
            openSettingActivity();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_MaterialComponents_Light_Dialog_Alert);
        String AlertText1 = "Attention : ";
        builder.setMessage(AlertText1 + getString(R.string.alert))
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> {
                    imageView = mActivityUndeliveredBinding.image;
                    startCameraActivity();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onDatePicker() {
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
                } else
                    dateSet = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                Date date;
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    date = sdf.parse(dateSet);
                    dateSet = String.valueOf(Objects.requireNonNull(date).getTime());
                } catch (ParseException e) {
                    Logger.e(TAG, e.getMessage());
                }
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000 + (1000 * 60 * 60 * 24 * 10));
        datePickerDialog.show();
    }

    @Override
    public void onSubmitSuccess() {
        OpenToDoList();
    }

    @Override
    public void OpenToDoList() {
        try {
            Helper.updateLocationWithData(RVPUndeliveredActivity.this, rvpCommit.getAwb(), rvpCommit.getStatus());
            String address = rvpWithQC.drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getLine1() + " " + rvpWithQC.drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getLine2() + " " + rvpWithQC.drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getLine3() + " " + rvpWithQC.drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getLine4() + " " + rvpWithQC.drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getCity();
            String finalAddress = address.replaceAll("null", "");
            Intent intent = RVPSuccessActivity.getStartIntent(this);
            intent.putExtra(Constants.DECIDENEXT, Constants.UNDELIVERED);
            intent.putExtra("ConsigneeName", rvpWithQC.drsReverseQCTypeResponse.getConsigneeDetails().getName());
            intent.putExtra("ConsigneeAddress", finalAddress);
            intent.putExtra("ConsigneeItemName", rvpWithQC.drsReverseQCTypeResponse.getShipmentDetails().getItem());
            intent.putExtra("Date", rvpWithQC.drsReverseQCTypeResponse.getAssignedDate());
            intent.putExtra("awb", rvpWithQC.drsReverseQCTypeResponse.getAwbNo());
            intent.putExtra("Reason", rvpReasonCodeMaster.getReasonMessage());
            startActivity(intent);
            applyTransitionToOpenActivity(this);
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage());
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void OnSubmitClick() {
        boolean failFlag = false;
        rvp_call_count = 0;
        rvpUndeliveredViewModel.isCallRecursionDialogRunning = true;
        rvpUndeliveredViewModel.isStopRecursion = false;
        rvpUndeliveredViewModel.call_alert_number = 0;
        rvpCommit.setCall_attempt_count(rvpUndeliveredViewModel.getDataManager().getRVPCallCount(awbNo + "RVP"));
        rvpCommit.setMap_activity_count(rvpUndeliveredViewModel.getDataManager().getRVPMapCount(awbNo));
        rvpCommit.setOfd_otp_verified(Constants.OFD_OTP_VERIFIED);
        rvpCommit.setOfd_customer_otp(Constants.PLAIN_OTP);
        rvpUndeliveredViewModel.getRemarkCount(Long.parseLong(rvpCommit.getAwb()));
        logButtonEventInGoogleAnalytics(TAG, "RVPQCUndeliveredOnSubmitClick", "Awb " + awbNo, this);
        try {
            if (rvpUndeliveredViewModel.ud_otp_commit_status.equalsIgnoreCase("none") && uD_OTP) {
                showSnackbar("Please Verify OTP");
                return;
            }
            if (this.rvpReasonCodeMaster == null || this.rvpReasonCodeMaster.getReasonCode() == -1 || groupName.equalsIgnoreCase(Constants.SELECT)) {
                showSnackbar(getString(R.string.select_reason_code));
            } else if (mActivityUndeliveredBinding.flagIsCameraEnabled.getVisibility() == View.VISIBLE) {
                if (rvpReasonCodeMaster.getMasterDataAttributeResponse().cALLM && rvpUndeliveredViewModel.getDataManager().getCallClicked(rvpCommit.getAwb() + "RVPCall")) {
                    makeCallDialog();
                    return;
                }
                if (rvpUndeliveredViewModel.getDataManager().getSMSThroughWhatsapp() && rvpReasonCodeMaster.getMasterDataAttributeResponse().isWHATSAPP_MAND() && rvpUndeliveredViewModel.getDataManager().getTryReachingCount(awbNo + Constants.TRY_RECHING_COUNT) == 0) {
                    String template = CommonUtils.getWhatsAppRemarkTemplate(rvpUndeliveredViewModel.getDataManager().getName(), rvpUndeliveredViewModel.getDataManager().getMobile(), String.valueOf(awbNo), rvpResponse.getShipmentDetails().getShipper());
                    showWhatsAppDialog(template);
                    return;
                }
                if (rvpReasonCodeMaster.getMasterDataAttributeResponse().isiMG() && !isImageCaptured) {
                    Toast.makeText(getApplicationContext(), getString(R.string.capture_image), Toast.LENGTH_SHORT).show();
                } else if (mActivityUndeliveredBinding.flagIsRescheduled.getVisibility() == View.VISIBLE) {
                    showSnackbar(getString(R.string.reschedule_statment));
                } else if (Constants.shipment_undelivered_count >= rvpUndeliveredViewModel.getDataManager().getUndeliverCount()) {
                    if (rvpUndeliveredViewModel.getDataManager().getCallClicked(rvpCommit.getAwb() + "RVPCall") && Boolean.FALSE.equals(rvpUndeliveredViewModel.ud_otp_verified_status.get())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_MaterialComponents_Light_Dialog_Alert);
                        builder.setMessage("Three or more consecutive shipments cannot be marked as UD without a call. Please call consignee.");
                        builder.setPositiveButton("CALL", (dialog, which) -> {
                            makeCallDialog();
                            dialog.dismiss();
                        });
                        Dialog dialog = builder.create();
                        dialog.show();
                    } else if (rvpUndeliveredViewModel.getDataManager().isCounterDelivery() && rvpUndeliveredViewModel.getCounterDeliveryRange() < rvpUndeliveredViewModel.getDataManager().getDCRANGE()) {
                        undelivered(failFlag);
                    } else {
                        if (rvpUndeliveredViewModel.getDataManager().getDcUndeliverStatus()) {
                            if (rvpUndeliveredViewModel.getCounterDeliveryRange() < rvpUndeliveredViewModel.getDataManager().getDCRANGE()) {
                                showError("Shipment cannot be marked undelivered within the DC");
                            } else {
                                if (is_call_mandatory) {
                                    if (check_call_attempted == 0) {
                                        markDeliverOrFail(failFlag);
                                    } else {
                                        markDeliverOrFail(failFlag);
                                    }
                                } else {
                                    markDeliverOrFail(failFlag);
                                }
                            }
                        } else {
                            if (is_call_mandatory) {
                                if (check_call_attempted == 0) {
                                    markDeliverOrFail(failFlag);
                                } else {
                                    markDeliverOrFail(failFlag);
                                }
                            } else {
                                markDeliverOrFail(failFlag);
                            }
                        }
                    }
                } else if (rvpUndeliveredViewModel.getDataManager().isCounterDelivery() && rvpUndeliveredViewModel.getCounterDeliveryRange() < rvpUndeliveredViewModel.getDataManager().getDCRANGE()) {
                    undelivered(failFlag);
                } else {
                    if (rvpUndeliveredViewModel.getDataManager().getDcUndeliverStatus()) {
                        if (rvpUndeliveredViewModel.getCounterDeliveryRange() < rvpUndeliveredViewModel.getDataManager().getDCRANGE()) {
                            showError("Shipment cannot be marked undelivered within the DC");
                        } else {
                            if (is_call_mandatory) {
                                if (check_call_attempted == 0) {
                                    markDeliverOrFail(failFlag);
                                } else {
                                    markDeliverOrFail(failFlag);
                                }
                            } else {
                                markDeliverOrFail(failFlag);
                            }
                        }
                    } else {
                        if (is_call_mandatory) {
                            if (check_call_attempted == 0) {
                                markDeliverOrFail(failFlag);
                            } else {
                                markDeliverOrFail(failFlag);
                            }
                        } else {
                            markDeliverOrFail(failFlag);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage());
            showSnackbar(e.getMessage());
        }
    }

    // for calling purpose from dialogueBox
    private void makeCallDialog() {
        if (!call_allowed) {
            undelivered(false);
        } else {
            dialog = new Dialog(this);
            dialog.setCancelable(true);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.activity_undelivered_call_dialog);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            TextView name = dialog.findViewById(R.id.name);
            name.setText(String.format("Name : %s", rvpCommit.getConsigneeName()));
            TextView awb = dialog.findViewById(R.id.awb);
            awb.setText(String.format("AWB : %s", rvpCommit.getAwb()));
            ImageView dialogButton = dialog.findViewById(R.id.call);
            dialogButton.setOnClickListener(v -> {
                rvpUndeliveredViewModel.getDataManager().setCallClicked(rvpCommit.getAwb() + "RVPCall", false);
                if (rvpResponse.getFlags().getFlagMap().getIs_callbridge_enabled().equalsIgnoreCase("true") &&
                        rvpResponse.getCallbridge_details()!=null) {
                    makeCallOnClick();
                } else {
                    if (!TextUtils.isEmpty(ConsigneeDirectAlternateMobileNo) && ConsigneeDirectAlternateMobileNo != null && !ConsigneeDirectAlternateMobileNo.equals("0")) {
                        showDirectCallDialog();
                    } else {
                        rvpUndeliveredViewModel.consigneeContactNumber.set(rvpResponse.getConsigneeDetails().getMobile());
                        rvp_call_count = rvp_call_count + 1;
                        rvpUndeliveredViewModel.getDataManager().setRVPCallCount(awbNo + "RVP", rvp_call_count);
                        CommonUtils.startCallIntent(rvpResponse.getConsigneeDetails().getMobile(), getActivityContext(), RVPUndeliveredActivity.this);
                    }
                }

            });
            dialog.show();
            dialog.getWindow().setAttributes(lp);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rvpUndeliveredViewModel.consigneeContactNumber.get() == null) {
            rvpUndeliveredViewModel.consigneeContactNumber.set("");
        }
        if (!Objects.requireNonNull(rvpUndeliveredViewModel.consigneeContactNumber.get()).equalsIgnoreCase("")) {
            CommonUtils.deleteNumberFromCallLogsAsync(rvpUndeliveredViewModel.consigneeContactNumber.get(), RVPUndeliveredActivity.this);
        }
    }

    // Undelivered the packet
    @SuppressLint("SimpleDateFormat")
    private void undelivered(boolean failFlag) {
        try {
            if (Constants.uD_OTP_API_CHECK && Objects.requireNonNull(rvpUndeliveredViewModel.ud_otp_commit_status_field.get()).equalsIgnoreCase("VERIFIED")) {
                rvpCommit.setUd_otp_verify_status(rvpUndeliveredViewModel.ud_otp_commit_status_field.get());
                rvpCommit.setRd_otp_verify_status("NONE");
            } else if (Constants.rD_OTP_API_CHECK && Objects.requireNonNull(rvpUndeliveredViewModel.ud_otp_commit_status_field.get()).equalsIgnoreCase("VERIFIED")) {
                rvpCommit.setRd_otp_verify_status("VERIFIED");
                rvpCommit.setUd_otp_verify_status("NONE");
            } else {
                rvpCommit.setUd_otp_verify_status(rvpUndeliveredViewModel.ud_otp_commit_status_field.get());
                rvpCommit.setRd_otp_verify_status(rvpUndeliveredViewModel.rd_otp_commit_status_field.get());
            }
            Calendar calendar = Calendar.getInstance();
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
            int mMonth = calendar.get(Calendar.MONTH) + 1;
            if (rvpUndeliveredViewModel.loginDate().equalsIgnoreCase(String.valueOf(mDay)) && rvpUndeliveredViewModel.getDataManager().getLoginMonth() == mMonth) {
                if (!failFlag) {
                    String dialog_message = getString(R.string.commitdialog);
                    String positiveButtonText = getString(R.string.yes);
                    if (Constants.CONSIGNEE_PROFILE && meterRange > rvpUndeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE() && rvpUndeliveredViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("W")) {
                        dialog_message = "You are not attempting the shipment at Consignee’s location. Your current location is " + rvpUndeliveredViewModel.getDataManager().getCurrentLatitude() + ", " + rvpUndeliveredViewModel.getDataManager().getCurrentLongitude() + " You are " + meterRange + " meter away from consignee location, \nAre you sure you want to commit?";
                        positiveButtonText = getString(R.string.yes);
                    } else if (Constants.CONSIGNEE_PROFILE && meterRange > rvpUndeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE() && rvpUndeliveredViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("R")) {
                        dialog_message = "You are not allowed to commit this shipment as you are not attempting at consignee location. your current location = " + rvpUndeliveredViewModel.getDataManager().getCurrentLatitude() + ", " + rvpUndeliveredViewModel.getDataManager().getCurrentLongitude() + " You are " + meterRange + " meter away from consignee location";
                        positiveButtonText = getString(R.string.ok);
                    } else if (!rvpUndeliveredViewModel.getDataManager().isCounterDelivery()) {
                        if (Constants.CONSIGNEE_PROFILE && meterRange > rvpUndeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE()) {
                            RvpCommit.RescheduleInfo feRescheduleInfo = new RvpCommit.RescheduleInfo();
                            Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(dateSet);
                            dateSet = String.valueOf(Objects.requireNonNull(date1).getTime());
                            feRescheduleInfo.setRescheduleDate(dateSet);
                            feRescheduleInfo.setRescheduleSlot(slotId);
                            feRescheduleInfo.setRescheduleFeComments(mActivityUndeliveredBinding.remarksEdt.getText().toString());
                            rvpCommit.setDrsCommitDateTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                            rvpCommit.setRescheduleInfo(feRescheduleInfo);
                            if (NetworkUtils.isNetworkConnected(RVPUndeliveredActivity.this)) {
                                rvpUndeliveredViewModel.onUndeliveredApiCallRealTime(rvpCommit, composite_key);
                            } else {
                                rvpUndeliveredViewModel.onUndeliveredApiCall(rvpCommit, composite_key);
                            }
                            return;
                        } else if (Constants.CONSIGNEE_PROFILE) {
                            RvpCommit.RescheduleInfo feRescheduleInfo = new RvpCommit.RescheduleInfo();
                            if (!dateSet.equalsIgnoreCase("")) {
                                Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(dateSet);
                                dateSet = String.valueOf(Objects.requireNonNull(date1).getTime());
                                feRescheduleInfo.setRescheduleDate(dateSet);
                                feRescheduleInfo.setRescheduleSlot(slotId);
                            }
                            feRescheduleInfo.setRescheduleFeComments(mActivityUndeliveredBinding.remarksEdt.getText().toString());
                            rvpCommit.setDrsCommitDateTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                            rvpCommit.setRescheduleInfo(feRescheduleInfo);
                            if (NetworkUtils.isNetworkConnected(RVPUndeliveredActivity.this)) {
                                rvpUndeliveredViewModel.onUndeliveredApiCallRealTime(rvpCommit, composite_key);
                            } else {
                                rvpUndeliveredViewModel.onUndeliveredApiCall(rvpCommit, composite_key);
                            }
                            return;
                        }
                    }
                    rvpCommit.setDrsCommitDateTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                    AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_MaterialComponents_Light_Dialog_Alert);
                    builder.setCancelable(false);
                    builder.setMessage(dialog_message);
                    rvpCommit.setLocation_verified(meterRange <= rvpUndeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE());
                    builder.setPositiveButton(positiveButtonText, (dialog, which) -> {
                        if (rvpUndeliveredViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("R") && meterRange > rvpUndeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE()) {
                            dialog.dismiss();
                            return;
                        } else if (consigneeProfiling && meterRange > rvpUndeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE()) {
                            dialog.dismiss();
                        }
                        RvpCommit.RescheduleInfo feRescheduleInfo = new RvpCommit.RescheduleInfo();
                        Date date1;
                        try {
                            date1 = new SimpleDateFormat("dd-MM-yyyy").parse(dateSet);
                            dateSet = String.valueOf(Objects.requireNonNull(date1).getTime());
                            feRescheduleInfo.setRescheduleDate(dateSet);
                            feRescheduleInfo.setRescheduleSlot(slotId);
                        } catch (ParseException e) {
                            Logger.e(TAG, e.getMessage());
                        }
                        feRescheduleInfo.setRescheduleFeComments(mActivityUndeliveredBinding.remarksEdt.getText().toString());
                        rvpCommit.setDrsCommitDateTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                        rvpCommit.setRescheduleInfo(feRescheduleInfo);
                        // Apis calling got undelivered the packet
                        if (NetworkUtils.isNetworkConnected(RVPUndeliveredActivity.this)) {
                            rvpUndeliveredViewModel.onUndeliveredApiCallRealTime(rvpCommit, composite_key);
                        } else {
                            rvpUndeliveredViewModel.onUndeliveredApiCall(rvpCommit, composite_key);
                        }
                    });
                    builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
                    if (!(consigneeProfiling && meterRange > rvpUndeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE())) {
                        builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel());
                    }
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_MaterialComponents_Light_Dialog_Alert);
                String AlertText1 = "Attention : ";
                builder.setMessage(AlertText1 + getString(R.string.commit_restriction_msg)).setCancelable(false).setPositiveButton("OK", (dialog, id) -> {
                    dialog.cancel();
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    @SuppressLint("SimpleDateFormat")
    void markDeliverOrFail(boolean failFlag) {
        int current_time = Integer.parseInt(new SimpleDateFormat("HH").format(new Date()));
        if (Constants.CONSIGNEE_PROFILE && meterRange < rvpUndeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE()) {
            undelivered(failFlag);
        } else if (rvpUndeliveredViewModel.FEInDCZone(rvpUndeliveredViewModel.getDataManager().getCurrentLatitude(), rvpUndeliveredViewModel.getDataManager().getCurrentLongitude(), rvpUndeliveredViewModel.getDataManager().getDCLatitude(), rvpUndeliveredViewModel.getDataManager().getDCLongitude())) {
            if (!rvpUndeliveredViewModel.getDataManager().getDirectUndeliver()) {
                undelivered(failFlag);
            } else {
                if (call_allowed) {
                    rvpUndeliveredViewModel.callApi(rvpResponse.getFlags().getFlagMap().getIs_callbridge_enabled(), failFlag, rvpCommit.getAwb(), rvpCommit.getDrsId());
                } else {
                    undelivered(failFlag);
                }
            }
        } else if (current_time >= 21 || current_time <= 6) {
            if (!rvpUndeliveredViewModel.getDataManager().getDirectUndeliver()) {
                undelivered(failFlag);
            } else {
                if (call_allowed) {
                    rvpUndeliveredViewModel.callApi(rvpResponse.getFlags().getFlagMap().getIs_callbridge_enabled(), failFlag, rvpCommit.getAwb(), rvpCommit.getDrsId());
                } else {
                    undelivered(failFlag);
                }
            }
        } else if ((TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis()) - TimeUnit.MILLISECONDS.toMinutes(rvpUndeliveredViewModel.getDataManager().getDRSTimeStamp())) <= 7) {
            if (!rvpUndeliveredViewModel.getDataManager().getDirectUndeliver()) {
                undelivered(failFlag);
            } else {
                if (call_allowed) {
                    rvpUndeliveredViewModel.callApi(rvpResponse.getFlags().getFlagMap().getIs_callbridge_enabled(), failFlag, rvpCommit.getAwb(), rvpCommit.getDrsId());
                } else {
                    undelivered(failFlag);
                }
            }
        } else {
            if (is_call_mandatory) {
                if (!rvpUndeliveredViewModel.getDataManager().getDirectUndeliver()) {
                    undelivered(failFlag);
                } else {
                    if (call_allowed) {
                        rvpUndeliveredViewModel.callApi(rvpResponse.getFlags().getFlagMap().getIs_callbridge_enabled(), failFlag, rvpCommit.getAwb(), rvpCommit.getDrsId());
                    } else {
                        undelivered(failFlag);
                    }
                }
            } else {
                undelivered(failFlag);
            }
        }
    }

    private void makeCallOnClick() {
        /*if (getDrsPstKey != null) {
            Constants.call_awb = rvpCommit.getAwb();
            Constants.shipment_type = Constants.RVP;
            rvpUndeliveredViewModel.saveCallStatus(Long.parseLong(rvpCommit.getAwb()), Integer.parseInt(rvpCommit.getDrsId()));
            Constants.IS_CALL_BRIDGE_FLAG_ON_STATUS = rvpUndeliveredViewModel.getDataManager().getIsCallBridgeCheckStatus().equalsIgnoreCase("true");
            if (rvpResponse.getFlags().getFlagMap().getIs_callbridge_enabled().equalsIgnoreCase("true") && !rvpUndeliveredViewModel.getDataManager().getENABLEDIRECTDIAL().equalsIgnoreCase("true")) {
                rvpUndeliveredViewModel.consigneeContactNumber.set(getDrsPstKey);
                CommonUtils.startCallIntent(getDrsPstKey, this, RVPUndeliveredActivity.this);
            } else {
                if (!TextUtils.isEmpty(ConsigneeDirectAlternateMobileNo) && ConsigneeDirectAlternateMobileNo != null && !ConsigneeDirectAlternateMobileNo.equals("0")) {
                    showDirectCallDialog();
                } else {
                    rvpUndeliveredViewModel.consigneeContactNumber.set(Constants.ConsigneeDirectMobileNo);
                    rvp_call_count = rvp_call_count + 1;
                    rvpUndeliveredViewModel.getDataManager().setRVPCallCount(awbNo + "RVP", rvp_call_count);
                    Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Constants.ConsigneeDirectMobileNo));
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                }
            }
        } else if (getDrsApiKey != null) {
            try {
                if (isNetworkConnected())
                    rvpUndeliveredViewModel.makeCallBridgeApiCall(getDrsApiKey, rvpCommit.getAwb(), rvpCommit.getDrsId(), Constants.RVP);
                else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Logger.e(TAG, e.getMessage());
                showSnackbar(e.getMessage());
            }
        }*/
        //else {
            try {
                /*try {
                    if (rvpUndeliveredViewModel.getDataManager().getPSTNType().equalsIgnoreCase("")) {
                        getCbConfigCallType = "PSTN";
                    } else {
                        getCbConfigCallType = rvpUndeliveredViewModel.getDataManager().getPSTNType();
                    }
                } catch (Exception e) {
                    Logger.e(TAG, e.getMessage());
                }*/
               // if (getCbConfigCallType != null) {
                if (rvpResponse.getFlags().getFlagMap().getIs_callbridge_enabled().equalsIgnoreCase("true")
                        && rvpResponse.getCallbridge_details()!=null)  {
                       // MasterPstFormat = rvpUndeliveredViewModel.getDataManager().getPstnFormat();
                        String callingFormat = null;
                       /* if (MasterPstFormat.contains(this.getString(R.string.patn_awb))) {
                            callingFormat = MasterPstFormat.replaceAll(this.getString(R.string.patn_awb), rvpCommit.getAwb());
                            Constants.call_awb = rvpCommit.getAwb();
                            Constants.shipment_type = Constants.RVP;
                        } else if (MasterPstFormat.contains(this.getString(R.string.pstn_pin))) {*/
                        //    callingFormat = MasterPstFormat.replaceAll(this.getString(R.string.pstn_pin), getDrsPin);
                        //    Constants.call_pin = getDrsPin;
                            callingFormat = rvpResponse.getCallbridge_details().get(0).getCallbridge_number()+","+rvpResponse.getCallbridge_details().get(0).getPin()+"#";
                            Constants.call_pin = String.valueOf(rvpResponse.getCallbridge_details().get(0).getPin());
                            Constants.calling_format = callingFormat;
                            Constants.shipment_type = Constants.RVP;
                       // }
                        if (callingFormat != null) {
                            rvpUndeliveredViewModel.saveCallStatus(Long.parseLong(rvpCommit.getAwb()), Integer.parseInt(rvpCommit.getDrsId()));
                            Constants.IS_CALL_BRIDGE_FLAG_ON_STATUS = rvpUndeliveredViewModel.getDataManager().getIsCallBridgeCheckStatus().equalsIgnoreCase("true");
                            if (rvpResponse.getFlags().getFlagMap().getIs_callbridge_enabled().equalsIgnoreCase("true")
                                    && rvpResponse.getCallbridge_details()!=null)
                            {
                                if (rvpResponse.getCallbridge_details().size()>1)
                                {
                                    showCallBridgeDialog();
                                }
                                else
                                {
                                    rvpUndeliveredViewModel.consigneeContactNumber.set(callingFormat);
                                    CommonUtils.startCallIntent(callingFormat, this, RVPUndeliveredActivity.this);
                                }
                            } else {
                                if (!TextUtils.isEmpty(ConsigneeDirectAlternateMobileNo) && ConsigneeDirectAlternateMobileNo != null && !ConsigneeDirectAlternateMobileNo.equals("0")) {
                                    showDirectCallDialog();
                                } else {
                                    rvpUndeliveredViewModel.consigneeContactNumber.set(Constants.ConsigneeDirectMobileNo);
                                    rvp_call_count = rvp_call_count + 1;
                                    rvpUndeliveredViewModel.getDataManager().setRVPCallCount(awbNo + "RVP", rvp_call_count);
                                    Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Constants.ConsigneeDirectMobileNo));
                                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent1);
                                }
                            }

                        }
                    }
                   /* if (getCbConfigCallType.equalsIgnoreCase("API")) {
                        if (isNetworkConnected())
                            rvpUndeliveredViewModel.makeCallBridgeApiCall("API", rvpCommit.getAwb(), rvpCommit.getDrsId(), Constants.RVP);
                        else {
                            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                        }
                    }*/
              //  }
            } catch (Exception e) {
                Logger.e(TAG, e.getMessage());
                showSnackbar(e.getMessage());
            }
     //   }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void captureDate() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(this,R.style.Theme_AppCompat_Light_Dialog, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DATE);
            if (dayOfWeek == dayOfMonth) {
                showInfo("Cannot Select Today Date For Re-schedule");
            } else {
                mActivityUndeliveredBinding.dateBtn.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                dateFlag = true;
                if (monthOfYear > 0 && monthOfYear < 10) {
                    dateSet = dayOfMonth + "-" + "0" + (monthOfYear + 1) + "-" + year;
                } else
                    dateSet = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000 + (1000 * 60 * 60 * 24 * 10));
        datePickerDialog.show();
    }

    @Override
    public void onChooseSlotSpinner(String slotId) {
        slotFlag = true;
        this.slotId = slotId;
    }

    @Override
    public void onRescheduleClick() {
        if (!dateFlag) {
            showSnackbar("Please Choose Date");
        } else if (!slotFlag) {
            showSnackbar("Please Choose Slot");
        } else
            mActivityUndeliveredBinding.flagIsRescheduled.setVisibility(View.GONE);
    }

    @Override
    public void onHandleError(ErrorResponse callApiResponse) {
        showSnackbar(callApiResponse.getEResponse().getDescription());
    }

    @Override
    public void onChooseGroupSpinner(String groupName) {
        try {
            this.groupName = groupName;
            mActivityUndeliveredBinding.otpSkip.setChecked(false);
            rvpUndeliveredViewModel.ud_otp_commit_status = "NONE";
            rvpUndeliveredViewModel.ud_otp_commit_status_field.set("NONE");
            rvpUndeliveredViewModel.rd_otp_commit_status = "NONE";
            rvpUndeliveredViewModel.rd_otp_commit_status_field.set("NONE");
            if (groupName.equalsIgnoreCase(Constants.SELECT)) {
                mActivityUndeliveredBinding.tvMandate.setText("");
                if (isImageCaptured) {
                    isImageCaptured = false;
                    mActivityUndeliveredBinding.image.setImageBitmap(null);
                    mActivityUndeliveredBinding.image.setImageResource(R.drawable.cam);
                }
                mActivityUndeliveredBinding.childGroupLayout.setVisibility(View.GONE);
                mActivityUndeliveredBinding.flagIsRescheduled.setVisibility(View.GONE);
                mActivityUndeliveredBinding.llUdOtp.setVisibility(View.GONE);
            } else
                mActivityUndeliveredBinding.childGroupLayout.setVisibility(View.VISIBLE);
            mActivityUndeliveredBinding.spinnerChildType.performClick();
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage());
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void onChooseChildSpinner(RVPReasonCodeMaster rvpReasonCodeMaster) {
        this.rvpReasonCodeMaster = rvpReasonCodeMaster;
        String template = CommonUtils.getWhatsAppRemarkTemplate(rvpUndeliveredViewModel.getDataManager().getName(), rvpUndeliveredViewModel.getDataManager().getMobile(), String.valueOf(awbNo), rvpResponse.getShipmentDetails().getShipper());
        if (rvpUndeliveredViewModel.getDataManager().getSMSThroughWhatsapp() && rvpReasonCodeMaster.getMasterDataAttributeResponse().isWHATSAPP_MAND() && rvpUndeliveredViewModel.getDataManager().getTryReachingCount(awbNo + Constants.TRY_RECHING_COUNT) == 0) {
            showWhatsAppDialog(template);
        } else {
            executeOnChooseChildSpinner(rvpReasonCodeMaster);
        }
    }

    private void showWhatsAppDialog(String template) {
        CommonUtils.showDialogBoxForWhatsApp(getActivityContext()).setPositiveButton("OK", (dialog, id) -> {
            try {
                isWhatsappMessaged = true;
                CommonUtils.sendSMSViaWhatsApp(this, this, rvpResponse.getConsigneeDetails().getMobile(), template);
                rvpUndeliveredViewModel.getDataManager().setTryReachingCount(awbNo + Constants.TRY_RECHING_COUNT, rvpUndeliveredViewModel.getDataManager().getTryReachingCount(awbNo + Constants.TRY_RECHING_COUNT) + 1);
                String remarks = GlobalConstant.RemarksTypeConstants.CONSIGNEE_NOT_PICKING_CALL;
                Remark remark = new Remark();
                remark.remark = remarks;
                remark.empCode = rvpUndeliveredViewModel.getDataManager().getEmp_code();
                remark.awbNo = awbNo;
                remark.count = rvpUndeliveredViewModel.getDataManager().getTryReachingCount(awbNo + Constants.TRY_RECHING_COUNT);
                remark.date = TimeUtils.getDateYearMonthMillies();
                rvpUndeliveredViewModel.addRemarks(remark);
                executeOnChooseChildSpinner(rvpReasonCodeMaster);
            } catch (Exception e) {
                Logger.e(TAG, e.getMessage());
            }
        }).setOnCancelListener(dialog -> executeOnChooseChildSpinner(rvpReasonCodeMaster)).show();
    }

    public void executeOnChooseChildSpinner(RVPReasonCodeMaster rvpReasonCodeMaster) {
        this.rvpReasonCodeMaster = rvpReasonCodeMaster;
        SetVisibility(rvpReasonCodeMaster);
        mActivityUndeliveredBinding.otpSkip.setChecked(false);
        rvpUndeliveredViewModel.ud_otp_commit_status = "NONE";
        rvpUndeliveredViewModel.ud_otp_commit_status_field.set("NONE");
        rvpUndeliveredViewModel.rd_otp_commit_status = "NONE";
        rvpUndeliveredViewModel.rd_otp_commit_status_field.set("NONE");
    }

    public void SetVisibility(RVPReasonCodeMaster rvpReasonCodeMaster) {
        if (rvpReasonCodeMaster.getReasonMessage().equalsIgnoreCase(Constants.SELECT)) {
            try {
                if (mActivityUndeliveredBinding.flagIsRescheduled.getVisibility() == View.VISIBLE) {
                    mActivityUndeliveredBinding.flagIsRescheduled.setVisibility(View.GONE);
                }
                mActivityUndeliveredBinding.tvMandate.setText("");
                if (isImageCaptured) {
                    isImageCaptured = false;
                    mActivityUndeliveredBinding.image.setImageBitmap(null);
                    mActivityUndeliveredBinding.image.setImageResource(R.drawable.cam);
                }
            } catch (Exception e) {
                Logger.e(TAG, e.getMessage());
                showSnackbar(e.getMessage());
            }
        }
        if (!rvpReasonCodeMaster.getReasonMessage().equalsIgnoreCase(Constants.SELECT)) {
            try {
                if (rvpReasonCodeMaster.getMasterDataAttributeResponse().isUD_OTP()) {
                    otpLayout(true, "UD_OTP");
                    Constants.uD_OTP_API_CHECK = true;
                    Constants.rD_OTP_API_CHECK = false;
                } else if (rvpReasonCodeMaster.getMasterDataAttributeResponse().isRCHD_OTP()) {
                    otpLayout(true, "RCHD_OTP");
                    Constants.rD_OTP_API_CHECK = true;
                    Constants.uD_OTP_API_CHECK = false;
                } else {
                    otpLayout(false, "");
                    Constants.rD_OTP_API_CHECK = false;
                    Constants.uD_OTP_API_CHECK = false;
                }
                if (rvpReasonCodeMaster.getMasterDataAttributeResponse().iscALLM()) {
                    is_call_mandatory = Constants.CONSIGNEE_PROFILE || meterRange >= rvpUndeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE();
                } else {
                    is_call_mandatory = false;
                }
                if (rvpReasonCodeMaster.getMasterDataAttributeResponse().isiMG()) {
                    mActivityUndeliveredBinding.tvMandate.setText(R.string.mdt);
                } else {
                    mActivityUndeliveredBinding.tvMandate.setText("");
                }
                if (isImageCaptured) {
                    isImageCaptured = false;
                    mActivityUndeliveredBinding.image.setImageBitmap(null);
                    mActivityUndeliveredBinding.image.setImageResource(R.drawable.cam);
                }
                if (!rvpReasonCodeMaster.getMasterDataAttributeResponse().isrCHD()) {
                    mActivityUndeliveredBinding.flagIsRescheduled.setVisibility(View.GONE);
                } else {
                    mActivityUndeliveredBinding.flagIsRescheduled.setVisibility(View.VISIBLE);
                }

                if (rvpReasonCodeMaster.getMasterDataAttributeResponse().iscALLM()) {
                    rvpUndeliveredViewModel.getIsCallAttempted(rvpCommit.getAwb());
                }
            } catch (Exception e) {
                Logger.e(TAG, e.getMessage());
                showSnackbar(e.getMessage());
            }
            rvpCommit.setAttemptReasonCode(rvpReasonCodeMaster.getReasonCode().toString());
        }
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
    public void showServerError() {
        showSnackbar("Server Response False, Recapture Again");
    }

    @Override
    public void setParentGroupSpinnerValues(List<String> parentGroupSpinnerValues) {
        mActivityUndeliveredBinding.spinnerGroupType.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_single_item, R.id.spinner_text_view, parentGroupSpinnerValues));
    }

    @Override
    public void setReasonMessageSpinnerValues(List<String> reasonMessageSpinnerValues) {
        mActivityUndeliveredBinding.spinnerChildType.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_single_item, R.id.spinner_text_view, reasonMessageSpinnerValues));
    }

    @Override
    public void showErrorMessage(boolean status) {
        if (status) {
            showSnackbar(getString(R.string.http_500_msg));
        } else {
            showSnackbar(getString(R.string.server_down_msg));
        }
    }

    @Override
    public void doLogout(String description) {
        try {
            showToast(getString(R.string.session_expire));
            rvpUndeliveredViewModel.logoutLocal();
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage());
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void clearStack() {
        Intent intent = new Intent(RVPUndeliveredActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    @Override
    public void onRVPItemFetched(DRSReverseQCTypeResponse drsReverseQCTypeResponse) {
        try {
            this.rvpResponse = drsReverseQCTypeResponse;
            rvpUndeliveredViewModel.checkMeterRange(rvpResponse);
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage());
            showSnackbar(e.getMessage());
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
        imageView.setImageBitmap(bitmap_server);
    }

    @Override
    public void isCallAttempted(int isCall) {
        check_call_attempted = isCall;
        if (isCall == 0 && rvpUndeliveredViewModel.getDataManager().getCallClicked(awbNo + "RVPCall")) {
            makeCallDialog();
        } else {
            if (!rvpReasonCodeMaster.getMasterDataAttributeResponse().isrCHD()) {
                mActivityUndeliveredBinding.flagIsRescheduled.setVisibility(View.GONE);
            } else {
                mActivityUndeliveredBinding.flagIsRescheduled.setVisibility(View.VISIBLE);
            }
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

    @Override
    public void otpLayout(boolean UD_OTP_Status, String otp_key) {
        try {
            if (otp_key.equalsIgnoreCase("UD_OTP")) {
                if (Constants.CancellationEnable) {
                    if (UD_OTP_Status) {
                        mActivityUndeliveredBinding.llUdOtp.setVisibility(View.VISIBLE);
                        mActivityUndeliveredBinding.edtUdOtp.setEnabled(false);
                        mActivityUndeliveredBinding.edtUdOtp.setText("");
                        mActivityUndeliveredBinding.imgVerifiedTick.setVisibility(View.GONE);
                        rvpUndeliveredViewModel.ud_otp_verified_status.set(false);
                        mActivityUndeliveredBinding.generateOtpTv.setVisibility(View.VISIBLE);
                        mActivityUndeliveredBinding.resendOtpTv.setVisibility(View.GONE);
                        mActivityUndeliveredBinding.verifyTv.setVisibility(View.GONE);
                        if (rvpUndeliveredViewModel.getDataManager().getSKIP_CANC_OTP_RVP().equalsIgnoreCase("True")) {
                            mActivityUndeliveredBinding.otpSkip.setVisibility(View.VISIBLE);
                        } else {
                            mActivityUndeliveredBinding.otpSkip.setVisibility(View.GONE);
                        }
                        uD_OTP = true;
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
                    if (UD_OTP_Status) {
                        mActivityUndeliveredBinding.llUdOtp.setVisibility(View.VISIBLE);
                        mActivityUndeliveredBinding.edtUdOtp.setEnabled(false);
                        mActivityUndeliveredBinding.edtUdOtp.setText("");
                        mActivityUndeliveredBinding.imgVerifiedTick.setVisibility(View.GONE);
                        rvpUndeliveredViewModel.ud_otp_verified_status.set(false);
                        mActivityUndeliveredBinding.generateOtpTv.setVisibility(View.VISIBLE);
                        mActivityUndeliveredBinding.otpSkip.setVisibility(View.VISIBLE);
                        mActivityUndeliveredBinding.resendOtpTv.setVisibility(View.GONE);
                        mActivityUndeliveredBinding.verifyTv.setVisibility(View.GONE);
                        uD_OTP = true;
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
            Logger.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onResendClick() {
        hideKeyboard(this);
        if (call_allowed && rvpUndeliveredViewModel.getDataManager().getVCallPopup()) {
            rvpUndeliveredViewModel.showCallAndSmsDialog(consignee_alternate_mobile, "Call");
        } else if (!consignee_alternate_mobile.equalsIgnoreCase("") || consignee_alternate_mobile != null) {
            rvpUndeliveredViewModel.showCallAndSmsDialog(consignee_alternate_mobile, "");
        } else {
            if (rvpReasonCodeMaster.getMasterDataAttributeResponse().isUD_OTP()) {
                rvpUndeliveredViewModel.onGenerateOtpApiCall(RVPUndeliveredActivity.this, String.valueOf(awbNo), drs_id, false, "UD_OTP", false, shipment_type);
            }
            if (rvpReasonCodeMaster.getMasterDataAttributeResponse().isRCHD_OTP()) {
                rvpUndeliveredViewModel.onGenerateOtpApiCall(RVPUndeliveredActivity.this, String.valueOf(awbNo), drs_id, false, "RCHD_OTP", false, shipment_type);
            }
        }
    }

    @Override
    public void onGenerateOtpClick() {
        hideKeyboard(this);
        if (rvpReasonCodeMaster.getMasterDataAttributeResponse().isUD_OTP()) {
            rvpUndeliveredViewModel.onGenerateOtpApiCall(RVPUndeliveredActivity.this, String.valueOf(awbNo), drs_id, false, "UD_OTP", true, shipment_type);
        }
        if (rvpReasonCodeMaster.getMasterDataAttributeResponse().isRCHD_OTP()) {
            rvpUndeliveredViewModel.onGenerateOtpApiCall(RVPUndeliveredActivity.this, String.valueOf(awbNo), drs_id, false, "RCHD_OTP", true, shipment_type);
        }
    }

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
                if (Objects.requireNonNull(encryptText).equalsIgnoreCase(mActivityUndeliveredBinding.edtUdOtp.getText().toString())) {
                    Constants.OFD_OTP_VERIFIED = true;
                    rvpUndeliveredViewModel.setOFDVerified(true);
                    Toast.makeText(RVPUndeliveredActivity.this, "Verified Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Constants.OFD_OTP_VERIFIED = false;
                    if (rvpReasonCodeMaster.getMasterDataAttributeResponse().isUD_OTP()) {
                        rvpUndeliveredViewModel.onVerifyApiCall(RVPUndeliveredActivity.this, mActivityUndeliveredBinding.awb.getText().toString(), mActivityUndeliveredBinding.edtUdOtp.getText().toString(), drs_id, "UD_OTP");
                        mActivityUndeliveredBinding.otpSkip.setChecked(false);
                        showSnackbar("Verified Successfully");
                    }
                    if (rvpReasonCodeMaster.getMasterDataAttributeResponse().isRCHD_OTP()) {
                        rvpUndeliveredViewModel.onVerifyApiCall(RVPUndeliveredActivity.this, mActivityUndeliveredBinding.awb.getText().toString(), mActivityUndeliveredBinding.edtUdOtp.getText().toString(), drs_id, "RCHD_OTP");
                        mActivityUndeliveredBinding.otpSkip.setChecked(false);
                        showSnackbar("Verified Successfully");
                    }
                }
            } else {
                Constants.OFD_OTP_VERIFIED = false;
                if (rvpReasonCodeMaster.getMasterDataAttributeResponse().isUD_OTP()) {
                    rvpUndeliveredViewModel.onVerifyApiCall(RVPUndeliveredActivity.this, mActivityUndeliveredBinding.awb.getText().toString(), mActivityUndeliveredBinding.edtUdOtp.getText().toString(), drs_id, "UD_OTP");
                    mActivityUndeliveredBinding.otpSkip.setChecked(false);
                    showSnackbar("Verified Successfully");
                }
                if (rvpReasonCodeMaster.getMasterDataAttributeResponse().isRCHD_OTP()) {
                    rvpUndeliveredViewModel.onVerifyApiCall(RVPUndeliveredActivity.this, mActivityUndeliveredBinding.awb.getText().toString(), mActivityUndeliveredBinding.edtUdOtp.getText().toString(), drs_id, "RCHD_OTP");
                    mActivityUndeliveredBinding.otpSkip.setChecked(false);
                    showSnackbar("Verified Successfully");
                }
            }
        }
    }

    @Override
    public void onSkipClick(View view) {
        hideKeyboard(this);
        if (Boolean.TRUE.equals(rvpUndeliveredViewModel.ud_otp_verified_status.get())) {
            showError("Already Verified");
            mActivityUndeliveredBinding.otpSkip.setChecked(false);
            return;
        }
        if (rvpUndeliveredViewModel.counter_skip == 0) {
            mActivityUndeliveredBinding.otpSkip.setChecked(false);
            showError("Please Resend OTP At-least Once.");
        } else if (!mActivityUndeliveredBinding.resendOtpTv.isEnabled()) {
            mActivityUndeliveredBinding.otpSkip.setChecked(false);
        } else {
            if (((CheckBox) view).isChecked()) {
                if (rvpReasonCodeMaster.getMasterDataAttributeResponse().isUD_OTP()) {
                    rvpUndeliveredViewModel.ud_otp_commit_status = "SKIPPED";
                    rvpUndeliveredViewModel.ud_otp_commit_status_field.set("SKIPPED");
                    rvpUndeliveredViewModel.rd_otp_commit_status = "";
                    rvpUndeliveredViewModel.rd_otp_commit_status_field.set("");
                }
                if (rvpReasonCodeMaster.getMasterDataAttributeResponse().isRCHD_OTP()) {
                    rvpUndeliveredViewModel.rd_otp_commit_status = "SKIPPED";
                    rvpUndeliveredViewModel.rd_otp_commit_status_field.set("SKIPPED");
                    rvpUndeliveredViewModel.ud_otp_commit_status = "";
                    rvpUndeliveredViewModel.ud_otp_commit_status_field.set("");
                }
            } else {
                rvpUndeliveredViewModel.ud_otp_commit_status = "NONE";
                rvpUndeliveredViewModel.ud_otp_commit_status_field.set("NONE");
                rvpUndeliveredViewModel.rd_otp_commit_status = "NONE";
                rvpUndeliveredViewModel.rd_otp_commit_status_field.set("NONE");
            }
        }
    }

    @Override
    public void VoiceCallOtp() {
        hideKeyboard(this);
        if (rvpReasonCodeMaster.getMasterDataAttributeResponse().isUD_OTP()) {
            rvpUndeliveredViewModel.doVoiceOTPApi(String.valueOf(awbNo), drs_id, "UD_OTP", shipment_type);
        } else if (rvpReasonCodeMaster.getMasterDataAttributeResponse().isRCHD_OTP()) {
            rvpUndeliveredViewModel.doVoiceOTPApi(String.valueOf(awbNo), drs_id, "RCHD_OTP", shipment_type);
        } else {
            rvpUndeliveredViewModel.doVoiceOTPApi(String.valueOf(awbNo), drs_id, "OTP", shipment_type);
        }
    }

    @Override
    public void resendSms(Boolean alternateClick) {
        hideKeyboard(this);
        if (rvpReasonCodeMaster.getMasterDataAttributeResponse().isUD_OTP()) {
            rvpUndeliveredViewModel.onGenerateOtpApiCall(RVPUndeliveredActivity.this, String.valueOf(awbNo), drs_id, alternateClick, "UD_OTP", false, shipment_type);
        }
        if (rvpReasonCodeMaster.getMasterDataAttributeResponse().isRCHD_OTP()) {
            rvpUndeliveredViewModel.onGenerateOtpApiCall(RVPUndeliveredActivity.this, String.valueOf(awbNo), drs_id, alternateClick, "RCHD_OTP", false, shipment_type);
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void voiceTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = new CountDownTimer(rvpUndeliveredViewModel.getDataManager().getDisableResendOtpButtonDuration() * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mActivityUndeliveredBinding.resendOtpTv.setEnabled(false);
                    mActivityUndeliveredBinding.resendOtpTv.setTextColor(getResources().getColor(R.color.light_gray));
                    mActivityUndeliveredBinding.otpSkip.setEnabled(false);
                    mActivityUndeliveredBinding.otpSkip.setChecked(false);
                    String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
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
            mCountDownTimer = new CountDownTimer(rvpUndeliveredViewModel.getDataManager().getDisableResendOtpButtonDuration() * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mActivityUndeliveredBinding.resendOtpTv.setEnabled(false);
                    mActivityUndeliveredBinding.resendOtpTv.setTextColor(getResources().getColor(R.color.light_gray));
                    mActivityUndeliveredBinding.otpSkip.setEnabled(false);
                    mActivityUndeliveredBinding.otpSkip.setChecked(false);
                    String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
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

    @SuppressLint("DefaultLocale")
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
                mCountDownTimer = new CountDownTimer(rvpUndeliveredViewModel.getDataManager().getDisableResendOtpButtonDuration() * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        mActivityUndeliveredBinding.resendOtpTv.setEnabled(false);
                        mActivityUndeliveredBinding.resendOtpTv.setTextColor(getResources().getColor(R.color.light_gray));
                        mActivityUndeliveredBinding.otpSkip.setEnabled(false);
                        mActivityUndeliveredBinding.otpSkip.setChecked(false);
                        String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
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
                mCountDownTimer = new CountDownTimer(rvpUndeliveredViewModel.getDataManager().getDisableResendOtpButtonDuration() * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        mActivityUndeliveredBinding.otpSkip.setChecked(false);
                        mActivityUndeliveredBinding.resendOtpTv.setEnabled(false);
                        mActivityUndeliveredBinding.resendOtpTv.setTextColor(getResources().getColor(R.color.light_gray));
                        mActivityUndeliveredBinding.otpSkip.setEnabled(false);
                        mActivityUndeliveredBinding.otpSkip.setChecked(false);
                        String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
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

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_MaterialComponents_Light_Dialog_Alert);
        String AlertText1 = "Attention : ";
        builder.setMessage(AlertText1 + getString(R.string.undelivered_shipment_msg)).setCancelable(false).setPositiveButton("Call", (dialog, id) -> {
            try {
                makeCallDialog();
            } catch (Exception e) {
                Logger.e(TAG, e.getMessage());
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
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
                    capturedImageBitmap = bitmap;
                } else {
                    showSnackbar("Captured Image Data Is Empty");
                }
            } catch (Exception e) {
                showSnackbar("Captured Image Data Is Empty");
            }
        }
    }

    private void sendCapturedImageToServerAndSaveInDB(String imageCode, String imageUri, Bitmap bitmap) {
        if (CommonUtils.checkImageIsBlurryOrNot(RVPUndeliveredActivity.this, "RVP", bitmap, imageCaptureCount, rvpUndeliveredViewModel.getDataManager()) || CommonUtils.checkImageIsBlurryOrNot(RVPUndeliveredActivity.this, "RQC", bitmap, imageCaptureCount, rvpUndeliveredViewModel.getDataManager())) {
            imageCaptureCount++;
        } else {
            bitmap_server = bitmap;
            isImageCaptured = true;
            if (rvpUndeliveredViewModel.getDataManager().getRVPRealTimeSync().equalsIgnoreCase("true")) {
                if (!isNetworkConnected()) {
                    showSnackbar(getString(R.string.no_network_error));
                    return;
                }
                rvpUndeliveredViewModel.uploadImageServer(getActivityContext(), imageUri, rvpCommit.getAwb() + "_" + rvpCommit.getDrsId() + "_UndeliveredImage.png", imageCode, awbNo, Integer.parseInt(rvpCommit.getDrsId()));
            } else {
                rvpUndeliveredViewModel.saveImageDB(imageUri, imageCode, rvpCommit.getAwb() + "_" + rvpCommit.getDrsId() + "_UndeliveredImage.png", 0);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
            applyTransitionToBackFromActivity(this);
        } else {
            showSnackbar("BackButton Is Disabled Until The Timer Is Off.");
        }
    }

    @Override
    public void onBackClick() {
        if (shouldAllowBack()) {
            super.onBackPressed();
            applyTransitionToBackFromActivity(this);
        } else {
            showSnackbar("BackButton Is Disabled Until The Timer Is Off.");
        }
    }

    private boolean shouldAllowBack() {
        return mActivityUndeliveredBinding.resendOtpTv.getText().toString().equalsIgnoreCase("RESEND") || mActivityUndeliveredBinding.resendOtpTv.getVisibility() == View.GONE;
    }

    public void showCallBridgeDialog() {
        Dialog dialog = new Dialog(this, R.style.RoundedCornersDialog);
        dialog.setContentView(R.layout.dialog_callbridge);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Button call = dialog.findViewById(R.id.bt_call);
        call.setText(rvpResponse.getCallbridge_details().get(0).getCallbridge_number());
        Button altCall = dialog.findViewById(R.id.bt_sms);
        altCall.setText(rvpResponse.getCallbridge_details().get(1).getCallbridge_number());
        ImageView crossDialog = dialog.findViewById(R.id.crssdialog);
        crossDialog.setOnClickListener(v -> dialog.dismiss());
        call.setOnClickListener(v -> {
            rvpUndeliveredViewModel.consigneeContactNumber.set(rvpResponse.getCallbridge_details().get(0).getCallbridge_number()+","+rvpResponse.getCallbridge_details().get(0).getPin()+"#");
            rvp_call_count = rvp_call_count + 1;
            rvpUndeliveredViewModel.getDataManager().setRVPCallCount(awbNo + "RVP", rvp_call_count);
            Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + rvpResponse.getCallbridge_details().get(0).getCallbridge_number()+","+rvpResponse.getCallbridge_details().get(0).getPin()+"#"));
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
            dialog.dismiss();
        });
        altCall.setOnClickListener(v -> {
            rvpUndeliveredViewModel.consigneeContactNumber.set(rvpResponse.getCallbridge_details().get(1).getCallbridge_number()+","+rvpResponse.getCallbridge_details().get(1).getPin()+"#");
            rvp_call_count = rvp_call_count + 1;
            rvpUndeliveredViewModel.getDataManager().setRVPCallCount(awbNo + "RVP", rvp_call_count);
            Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + rvpResponse.getCallbridge_details().get(1).getCallbridge_number()+","+rvpResponse.getCallbridge_details().get(1).getPin()+"#"));
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
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Button call = dialog.findViewById(R.id.bt_call);
        Button altCall = dialog.findViewById(R.id.bt_sms);
        ImageView crossDialog = dialog.findViewById(R.id.crssdialog);
        crossDialog.setOnClickListener(v -> dialog.dismiss());
        call.setOnClickListener(v -> {
            rvpUndeliveredViewModel.consigneeContactNumber.set(Constants.ConsigneeDirectMobileNo);
            rvp_call_count = rvp_call_count + 1;
            rvpUndeliveredViewModel.getDataManager().setRVPCallCount(awbNo + "RVP", rvp_call_count);
            Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Constants.ConsigneeDirectMobileNo));
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
            dialog.dismiss();
        });
        altCall.setOnClickListener(v -> {
            rvpUndeliveredViewModel.consigneeContactNumber.set(ConsigneeDirectAlternateMobileNo);
            rvp_call_count = rvp_call_count + 1;
            rvpUndeliveredViewModel.getDataManager().setRVPCallCount(awbNo + "RVP", rvp_call_count);
            Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ConsigneeDirectAlternateMobileNo));
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
            dialog.dismiss();
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    // Start Camera Activity And Send Data:-
    private void startCameraActivity() {
        try {
            Intent intent = new Intent(this, CameraActivity.class);
            intent.putExtra("EmpCode", rvpUndeliveredViewModel.getDataManager().getEmp_code());
            intent.putExtra("Latitude", rvpUndeliveredViewModel.getDataManager().getCurrentLatitude());
            intent.putExtra("Longitude", rvpUndeliveredViewModel.getDataManager().getCurrentLongitude());
            intent.putExtra("ImageCode", "UndeliveredImage");
            intent.putExtra("imageName", "UndeliveredImage");
            intent.putExtra("awbNumber", "" + awbNo);
            intent.putExtra("drs_id", rvpCommit.getDrsId());
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}