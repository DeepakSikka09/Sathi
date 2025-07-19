package in.ecomexpress.sathi.ui.drs.rts.rts_signature;

import static in.ecomexpress.sathi.utils.CommonUtils.logButtonEventInGoogleAnalytics;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.data.commit.PushApi;
import in.ecomexpress.sathi.repo.local.data.rts.RTSCommit;
import in.ecomexpress.sathi.repo.local.data.rts.Shipment;
import in.ecomexpress.sathi.repo.local.db.model.ImageModel;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.Details;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.ShipmentsDetail;
import in.ecomexpress.sathi.repo.remote.model.rts.RTSResendOTPRequest;
import in.ecomexpress.sathi.repo.remote.model.rts.RTSVerifyOTPRequest;
import in.ecomexpress.sathi.repo.remote.model.rts.generateOTP.AllOtpAWB;
import in.ecomexpress.sathi.repo.remote.model.rts.generateOTP.AwbList;
import in.ecomexpress.sathi.repo.remote.model.rts.generateOTP.GenerateRTSOTPRequest;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.CryptoUtils;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.LocationHelper;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@HiltViewModel
public class RTSSignatureViewModel extends BaseViewModel<IRTSSignatureNavigator> {

    private final String TAG = RTSSignatureViewModel.class.getSimpleName();
    public final ObservableField<Boolean> isImageCaptured = new ObservableField<>(false);
    public Details details;
    public long startAwbNo;
    public ObservableField<Boolean> ud_otp_verified_status = new ObservableField<>(false);
    public ObservableField<Boolean> ud_otp_skip_status = new ObservableField<>(false);
    public ObservableField<String> ofd_otp_verify_status = new ObservableField<>("NONE");
    public ObservableField<String> new_ud_otp_verify_status = new ObservableField<>("NONE");
    List<ShipmentsDetail> shipmentsDetails_data;
    boolean fromUDOtp;
    String is_otp_verified = null;
    boolean sign_image_required;
    ObservableField<Long> awbNo = new ObservableField<>();
    List<ImageModel> imageModels = new ArrayList<>();
    ObservableField<String> vendor_name_tv = new ObservableField<>();
    ObservableField<String> scannedDelivered = new ObservableField<>();
    ObservableField<String> manuallyDelivered = new ObservableField<>();
    ObservableField<List<ShipmentsDetail>> listShipmentObserver = new ObservableField<>();
    ObservableField<String> undeliveredShipments = new ObservableField<>();
    ObservableField<String> disputedDelivered = new ObservableField<>();
    ObservableField<String> totalShipmentsCount = new ObservableField<>();
    ObservableField<String> assignedDate = new ObservableField<>();
    ObservableField<String> et_usr = new ObservableField<>("");
    ObservableField<String> et_phone = new ObservableField<>("");
    List<ShipmentsDetail> mShipmentsDetails = new ArrayList<>();
    private final MutableLiveData<Boolean> imageUploadSuccessLiveData = new MutableLiveData<>();
    boolean isAllPacketUndelivered = false;
    boolean forDisputedDelivery = false;
    private RTSCommit rtsCommit;
    private Dialog dialog;


    @Inject
    public RTSSignatureViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);
    }

    public LiveData<Boolean> getImageUploadSuccessLiveData() {
        return imageUploadSuccessLiveData;
    }

    public ObservableField<String> getEt_usr() {
        return et_usr;
    }

    public void setEt_usr(ObservableField<String> et_usr) {
        this.et_usr = et_usr;
    }

    public ObservableField<String> getEt_phone() {
        return et_phone;
    }

    public void setEt_phone(ObservableField<String> et_phone) {
        this.et_phone = et_phone;
    }

    public ObservableField<String> getScannedDelivered() {
        return scannedDelivered;
    }

    public void setScannedDelivered(ObservableField<String> scannedDelivered) {
        this.scannedDelivered = scannedDelivered;
    }

    public ObservableField<String> getTotalShipmentsCount() {
        return totalShipmentsCount;
    }

    public void setTotalShipmentsCount(ObservableField<String> totalShipmentsCount) {
        this.totalShipmentsCount = totalShipmentsCount;
    }

    public ObservableField<String> getManuallyDelivered() {
        return manuallyDelivered;
    }

    public void setManuallyDelivered(ObservableField<String> manuallyDelivered) {
        this.manuallyDelivered = manuallyDelivered;
    }

    public ObservableField<String> getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(ObservableField<String> assignedDate) {
        this.assignedDate = assignedDate;
    }

    public ObservableField<String> getUndeliveredShipments() {
        return undeliveredShipments;
    }

    public void setUndeliveredShipments(ObservableField<String> undeliveredShipments) {
        this.undeliveredShipments = undeliveredShipments;
    }

    public ObservableField<String> getDisputedDelivered() {
        return disputedDelivered;
    }

    public void setDisputedDelivered(ObservableField<String> disputedDelivered) {
        this.disputedDelivered = disputedDelivered;
    }

    public ObservableField<String> getVendor_name_tv() {
        return vendor_name_tv;
    }

    public void setVendor_name_tv(ObservableField<String> vendor_name_tv) {
        this.vendor_name_tv = vendor_name_tv;
    }

    public Long getAwbNo() {
        return awbNo.get();
    }

    public void setAwbNo(Long awb) {
        awbNo.set(awb);
    }

    public void generateOTPForAll(Context context) {
        try{
            logButtonEventInGoogleAnalytics(TAG, context.getString(R.string.generateotpclickrts), "", context);
            showProgressDialog(context.getString(R.string.generating_otp), context);
            GenerateRTSOTPRequest generateOTPWithDispute = new GenerateRTSOTPRequest();
            ArrayList<AllOtpAWB> deliveredAwb = new ArrayList<>();
            ArrayList<AllOtpAWB> undeliveredAwb = new ArrayList<>();
            ArrayList<AllOtpAWB> disputedAwb = new ArrayList<>();
            for (ShipmentsDetail rtsShipmentDetails : Objects.requireNonNull(listShipmentObserver.get())) {
                AllOtpAWB allOtpAWB = new AllOtpAWB();
                allOtpAWB.awb = String.valueOf(rtsShipmentDetails.getAwbNo());
                allOtpAWB.reason = String.valueOf(rtsShipmentDetails.getReasonCode());
                allOtpAWB.reason_id = rtsShipmentDetails.getReason_id();
                allOtpAWB.drs_id = rtsShipmentDetails.getDrsNo();
                if (rtsShipmentDetails.getStatus().equalsIgnoreCase(Constants.RTSDISPUTED)) {
                    disputedAwb.add(allOtpAWB);
                } else if (rtsShipmentDetails.getStatus().equalsIgnoreCase(Constants.RTSUNDELIVERED)) {
                    undeliveredAwb.add(allOtpAWB);
                } else {
                    deliveredAwb.add(allOtpAWB);
                }
            }
            AwbList awbList = new AwbList();
            awbList.setDelivered_awb(deliveredAwb);
            awbList.setUndelivered_awb(undeliveredAwb);
            awbList.setDisputed_awb(disputedAwb);
            generateOTPWithDispute.setAwb_lists(awbList);
            generateOTPWithDispute.setEmp_code(getDataManager().getEmp_code());
            generateOTPWithDispute.setSub_shipper_id(String.valueOf(details.getId()));
            generateOTPWithDispute.setSub_shipper_mobile(details.getSub_shipper_mobile());
            if (fromUDOtp) {
                generateOTPWithDispute.setMessage_type("UD_OTP");
            }
            getCompositeDisposable().add(getDataManager().doGenerateRTSOTPApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), generateOTPWithDispute).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(generateOTPResponse -> {
                dismissProgressDialog();
                String errorDescription;
                try {
                    if (generateOTPResponse == null) {
                        getNavigator().showMessage(context.getString(R.string.api_response_null));
                        return;
                    }
                    errorDescription = (generateOTPResponse.getResponse().description == null) ? context.getString(R.string.generate_otp_status_false_try_again) : generateOTPResponse.getResponse().description;
                    if (generateOTPResponse.getStatus().equalsIgnoreCase("true")) {
                        getNavigator().showMessage(errorDescription);
                        getNavigator().showRTSWidgets(errorDescription);
                    } else if (generateOTPResponse.getResponse() != null) {
                        if (generateOTPResponse.getResponse().status_code.equalsIgnoreCase("107")) {
                            getNavigator().showErrorSnackbar(errorDescription);
                            clearAppData();
                        } else{
                            getNavigator().showMessage(errorDescription);
                        }
                    } else {
                        getNavigator().showMessage(errorDescription);
                    }
                } catch (Exception e) {
                    getNavigator().showMessage(String.valueOf(e));
                }
            }, throwable -> {
                dismissProgressDialog();
                getNavigator().showMessage(String.valueOf(throwable));
            }));
        } catch (Exception e){
            dismissProgressDialog();
            getNavigator().showMessage(String.valueOf(e));
        }
    }

    @SuppressLint("SetTextI18n")
    public void showCallAndSmsDialog(Context context) {
        Dialog dialog = new Dialog(getNavigator().getActivity(), R.style.RoundedCornersDialog);
        dialog.setContentView(R.layout.dialog_other_resend_otp);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Button sms = dialog.findViewById(R.id.bt_sms);
        Button call = dialog.findViewById(R.id.bt_call);
        Button btEmail = dialog.findViewById(R.id.bt_email);
        EditText mobile_edt = dialog.findViewById(R.id.mobile_edt);
        Button bt_send = dialog.findViewById(R.id.bt_send);
        ImageView crossDialog = dialog.findViewById(R.id.crssdialog);
        if (getDataManager().getENABLERTSEMAIL().equalsIgnoreCase("true") && !TextUtils.isEmpty(Constants.SUB_SHIPPER_EMAIL)) {
            btEmail.setVisibility(View.VISIBLE);
            btEmail.setText("Send OTP on E-Mail " + Constants.SUB_SHIPPER_EMAIL.replaceFirst(".{5}", "****"));
        } else {
            btEmail.setVisibility(View.GONE);
        }
        if (getDataManager().getRtsInputResendFlag().equalsIgnoreCase("true")) {
            sms.setVisibility(View.VISIBLE);
        } else {
            sms.setVisibility(View.GONE);
        }
        bt_send.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mobile_edt.getWindowToken(), 0);
            if (mobile_edt.getText().toString().length() == 10) {
                if (mobile_edt.getText().toString().startsWith("0") || mobile_edt.getText().toString().startsWith("1") || mobile_edt.getText().toString().startsWith("2") || mobile_edt.getText().toString().startsWith("3") || mobile_edt.getText().toString().startsWith("4")) {
                    getNavigator().showMessage(context.getString(R.string.enter_valid_mobile_number));
                } else {
                    resendOTPOtherMobileApiCall(mobile_edt.getText().toString(), context);
                    dialog.dismiss();
                }
            } else {
                getNavigator().showMessage(context.getString(R.string.enter_valid_mobile_number));
            }
        });
        crossDialog.setOnClickListener(v -> dialog.dismiss());
        sms.setOnClickListener(v -> {
            mobile_edt.setVisibility(View.VISIBLE);
            bt_send.setVisibility(View.VISIBLE);
        });
        call.setOnClickListener(v -> {
            resendOTPApiCall(false, context);
            dialog.dismiss();
        });
        btEmail.setOnClickListener(v -> {
            resendOTPApiCall(true, context);
            dialog.dismiss();
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public void setImageRequired(boolean sign_image_required) {
        this.sign_image_required = sign_image_required;
    }

    public void setImageCaptured(boolean isImageCaptured) {
        this.isImageCaptured.set(isImageCaptured);
    }

    public void onSubmitClick() {
        try {
            if (Objects.requireNonNull(et_usr.get()).isEmpty()) {
                getNavigator().showErrorSnackbar("Enter receiver's name");
                return;
            }
            if (!Objects.requireNonNull(et_usr.get()).matches("[a-zA-Z.? ]*")) {
                getNavigator().showErrorSnackbar("Enter valid receiver's name");
                return;
            }
            if (Objects.requireNonNull(et_phone.get()).isEmpty()) {
                getNavigator().showErrorSnackbar("Enter phone number");
                return;
            }
            if (Objects.requireNonNull(et_phone.get()).length() != 10) {
                getNavigator().showErrorSnackbar("Enter valid phone number");
                return;
            }
            if (getDCFEDistance()) {
                getNavigator().showErrorSnackbar("Shipment cannot be mark delivered within DC");
                return;
            }
            String receiverName = et_usr.get();
            String phoneNo = et_phone.get();
            rtsCommit.setReceivedBy(receiverName);
            rtsCommit.setReceiverPhoneNo(phoneNo);
            try {
                List<Shipment> shipmentList = new ArrayList<>();
                for (ShipmentsDetail shipmentsDetail : mShipmentsDetails) {
                    Shipment shipment = new Shipment();
                    shipment.setAssignDate(String.valueOf(java.util.Calendar.getInstance().getTimeInMillis()));
                    shipment.setDateTime(String.valueOf(java.util.Calendar.getInstance().getTimeInMillis()));
                    shipment.setAwbNo(String.valueOf(shipmentsDetail.getAwbNo()));
                    shipment.setDrsNo(shipmentsDetail.getDrsNo());
                    shipment.setOrderNo(shipmentsDetail.getOrderNo());
                    shipment.setParentAwbNo(shipmentsDetail.getParentAwbNo());
                    shipment.setReason(String.valueOf(shipmentsDetail.getReasonCode()));
                    shipment.setRemarks("YES");
                    shipment.setReason_id(shipmentsDetail.getReason_id());
                    shipment.setStatus(shipmentsDetail.getStatus());
                    if (isAllPacketUndelivered) {
                        if (shipmentsDetail.getStatus().equalsIgnoreCase(Constants.RTSDISPUTED)) {
                            shipment.setDS_SL("true");
                            shipment.setStatus(Constants.RTSMANUALLYDELIVERED);
                        }
                        shipment.setIs_otp_verified(is_otp_verified);
                        if (fromUDOtp) {
                            shipment.setUd_otp_verify_status(new_ud_otp_verify_status.get());
                        } else {
                            shipment.setRchd_otp_verify_status(ofd_otp_verify_status.get());
                        }
                        shipment.setReturn_package_barcode(shipmentsDetail.getEntered_return_package_barcode());
                        shipment.setScanned_method(shipmentsDetail.getFlyer_code_manually_input());
                    } else if (shipmentsDetail.getStatus().equalsIgnoreCase(Constants.RTSMANUALLYDELIVERED)) {
                        shipment.setIs_otp_verified(is_otp_verified);
                        shipment.SetOfd_otp_verify_status(ofd_otp_verify_status.get());
                    } else if (shipmentsDetail.getStatus().equalsIgnoreCase(Constants.SHIPMENT_DELIVERED)) {
                        shipment.setReturn_package_barcode(shipmentsDetail.getEntered_return_package_barcode());
                        shipment.setScanned_method(shipmentsDetail.getFlyer_code_manually_input());
                        shipment.setIs_otp_verified(is_otp_verified);
                        shipment.SetOfd_otp_verify_status(ofd_otp_verify_status.get());
                    } else if (shipmentsDetail.getStatus().equalsIgnoreCase(Constants.RTSDISPUTED)) {
                        shipment.setIs_otp_verified(is_otp_verified);
                        shipment.SetOfd_otp_verify_status(ofd_otp_verify_status.get());
                        shipment.setStatus(Constants.RTSMANUALLYDELIVERED);
                        shipment.setDS_SL("true");
                    } else {
                        shipment.setReturn_package_barcode(shipmentsDetail.getEntered_return_package_barcode());
                        shipment.setScanned_method(shipmentsDetail.getFlyer_code_manually_input());
                        shipment.setIs_otp_verified(null);
                        shipment.SetOfd_otp_verify_status("NONE");
                        shipment.setDS_SL("false");
                    }
                    shipmentList.add(shipment);
                }
                rtsCommit.setShipments(shipmentList);
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }

            try {
                if (!String.valueOf(in.ecomexpress.geolocations.Constants.latitude).equalsIgnoreCase("0.0") && !String.valueOf(in.ecomexpress.geolocations.Constants.longitude).equalsIgnoreCase("0.0")) {
                    rtsCommit.setLatitude(String.valueOf(in.ecomexpress.geolocations.Constants.latitude));
                    rtsCommit.setLongitude(String.valueOf(in.ecomexpress.geolocations.Constants.longitude));
                } else if (!Constants.CURRENT_LATITUDE.equalsIgnoreCase("0.0") && !Constants.CURRENT_LONGITUDE.equalsIgnoreCase("0.0")) {
                    rtsCommit.setLatitude(Constants.CURRENT_LATITUDE);
                    rtsCommit.setLongitude(Constants.CURRENT_LONGITUDE);
                } else {
                    rtsCommit.setLatitude(String.valueOf(getDataManager().getCurrentLatitude()));
                    rtsCommit.setLongitude(String.valueOf(getDataManager().getCurrentLongitude()));
                }
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
            Calendar calendar = Calendar.getInstance();
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
            int mMonth = calendar.get(Calendar.MONTH) + 1;
            if (getDataManager().getLoginDate().equalsIgnoreCase(String.valueOf(mDay)) && getDataManager().getLoginMonth() == mMonth) {
                getNavigator().saveSignature();
            } else {
                getNavigator().getAlert();
            }
        } catch (Exception e) {
            getNavigator().showErrorSnackbar(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @SuppressLint("CheckResult")
    public void saveImageInDBAndUpdateShipment(String imageUri, String imageCode, String imageKey, Boolean isCommit, int image_sync_status) {
        saveImageDB(imageUri, imageCode, imageKey, image_sync_status, -1, 0);
        if (isCommit) {
            updateShipmentStatus(rtsCommit.getConsigneeId());
        }
    }

    public void saveImageDB(String imageUri, String imageCode, String name, int image_sync_status, int image_id, int status) {
        try {
            imageUploadSuccessLiveData.setValue(true);
            ImageModel imageModel = new ImageModel();
            imageModel.setDraNo(rtsCommit.getConsigneeId());
            imageModel.setAwbNo(rtsCommit.getConsigneeId());
            imageModel.setImageName(name);
            imageModel.setImage(imageUri);
            imageModel.setImageCode(imageCode);
            imageModel.setStatus(status);
            imageModel.setImageCurrentSyncStatus(image_sync_status);
            imageModel.setImageFutureSyncTime(Calendar.getInstance().getTimeInMillis());
            imageModel.setImageId(image_id);
            imageModel.setDate(Calendar.getInstance().getTimeInMillis());
            imageModel.setShipmentType(GlobalConstant.ShipmentTypeConstants.RTS);
            imageModel.setImageType(GlobalConstant.ImageTypeConstants.OTHERS);
            getCompositeDisposable().add(getDataManager().saveImage(imageModel).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {}));
        } catch (Exception e) {
            getNavigator().showErrorSnackbar(e.getMessage());
        }
    }

    public void setRtsCommit(Details details) {
        vendor_name_tv.set(details.getName());
        try {
            undeliveredShipments.set("Undelivered : " + details.getUndelivered());
            scannedDelivered.set("Scanned Delivered : " + details.getDelivered());
            manuallyDelivered.set("Manually Delivered : " + details.getMnnuallyDelivered());
            disputedDelivered.set("Disputed Delivered : " + details.getDispute_delivery());
            totalShipmentsCount.set("Total AWBs Count : " + details.getTotalShipmentCount());
            assignedDate.set("Assigned Date : " + CommonUtils.getDate(details.getAssignedDate(), "dd/MM/yyyy"));
        } catch (Exception e) {
            getNavigator().showErrorSnackbar(e.getMessage());
        }
    }

    public void getVendorData(RTSCommit rtsCommit, long id, boolean commitFlag) {
        this.rtsCommit = rtsCommit;
        try {
            getCompositeDisposable().add(getDataManager().getVWDetails(id).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(details_response -> {
                if (details_response != null) {
                    details = details_response;
                    getNavigator().setPODImageVisibility(details);
                    setRtsCommit(details_response);
                    getShipmentData(details_response, commitFlag, details_response.isRCHD_enabled());
                }
            }));
        } catch (Exception e) {
            getNavigator().showErrorSnackbar(e.getMessage());
        }
    }

    public void findDuplicateReasonCode(boolean rchd_enabled, long id) {
        try {
            getCompositeDisposable().add(getDataManager().getDuplicateValueCount(String.valueOf(id)).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(count -> {
                if (isAllPacketUndelivered) {
                    isAttributeAvailable(count, rchd_enabled);
                }
            }));
        } catch (Exception e) {
            getNavigator().showErrorSnackbar(e.getMessage());
        }
    }

    public void isAttributeAvailable(Integer count, boolean rchd_enabled) {
        try {
            getCompositeDisposable().add(getDataManager().isAttributeAvailable(getDataManager().getUndeliverReasonCode()).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(master_rchd_enabled -> {
                fromUDOtp = false;
                if (count > 1) {
                    getNavigator().showOTPLayout(false, false);
                } else if (master_rchd_enabled.getMasterDataAttributeResponse().RCHD_OTP && rchd_enabled) {
                    getNavigator().showOTPLayout(true, false);
                } else if (master_rchd_enabled.getMasterDataAttributeResponse().UD_OTP) {
                    fromUDOtp = true;
                    getNavigator().showOTPLayout(true, false);
                }
            }));
        } catch (Exception e) {
            getNavigator().showErrorSnackbar(e.getMessage());
        }
    }

    private void getShipmentData(Details details, boolean isCommit, boolean rchd_enabled) {
        try {
            getCompositeDisposable().add(getDataManager().getVWShipmentList(details.getId()).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(shipmentsDetails -> {
                mShipmentsDetails = shipmentsDetails;
                createCommitPacket(details, shipmentsDetails, isCommit);
                getStartAwbNo(shipmentsDetails);
                shipmentsDetails_data = shipmentsDetails;
                for (int i = 0; i < shipmentsDetails.size(); i++) {
                    if (shipmentsDetails.get(i).getStatus().endsWith("Delivered") || shipmentsDetails.get(i).getStatus().endsWith("Disputed")) {
                        if (details.isIs_otp_required()) {
                            getNavigator().showOTPLayout(true, !details.isRts_skip_otp() && shipmentsDetails.get(i).getStatus().endsWith("Delivered"));
                        }
                        isAllPacketUndelivered = false;
                        forDisputedDelivery = true;
                        break;
                    } else if (shipmentsDetails.get(i).getStatus().endsWith("Undelivered")) {
                        isAllPacketUndelivered = true;
                        forDisputedDelivery = true;
                    }
                }
                if (!forDisputedDelivery) {
                    getNavigator().showOTPLayout(true, false);
                }
                if (isAllPacketUndelivered) {
                    findDuplicateReasonCode(rchd_enabled, details.getId());
                }
            }));
        } catch (Exception e) {
            getNavigator().showErrorSnackbar(e.getMessage());
        }
    }

    public void getStartAwbNo(List<ShipmentsDetail> shipmentsDetails) {
        startAwbNo = shipmentsDetails.get(0).getAwbNo();
    }

    private void createCommitPacket(Details details, List<ShipmentsDetail> shipmentsDetails, boolean isCommit) {
        try {
            startAwbNo = shipmentsDetails.get(0).getAwbNo();
            rtsCommit.setAssignDate(String.valueOf(details.getAssignedDate()));
            rtsCommit.setVendor_name(details.getName());
            rtsCommit.setConsigneeId(String.valueOf(details.getId()));
            List<RTSCommit.ImageData> list_image_responses = new ArrayList<>();
            imageModels = getImagesList(rtsCommit.getConsigneeId());
            for (int i = 0; i < imageModels.size(); i++) {
                RTSCommit.ImageData image_response = new RTSCommit.ImageData();
                image_response.setImageId(String.valueOf(imageModels.get(i).getImageId()));
                image_response.setImageKey(String.valueOf(imageModels.get(i).getImageName()));
                list_image_responses.add(image_response);
            }
            rtsCommit.setImageData(list_image_responses);
            rtsCommit.setConsigneeName(details.getName());
            rtsCommit.setReceivedBy(scannedDelivered.get());
            rtsCommit.setDrsType(Constants.RTSCOMMIT);
            rtsCommit.setTrip_id(getDataManager().getTripId());
            try {
                if (!Constants.CURRENT_LATITUDE.equalsIgnoreCase("0.0") && !Constants.CURRENT_LONGITUDE.equalsIgnoreCase("0.0")) {
                    rtsCommit.setLatitude(Constants.CURRENT_LATITUDE);
                    rtsCommit.setLongitude(Constants.CURRENT_LONGITUDE);
                } else if (!String.valueOf(in.ecomexpress.geolocations.Constants.latitude).equalsIgnoreCase("0.0") && !String.valueOf(in.ecomexpress.geolocations.Constants.longitude).equalsIgnoreCase("0.0")) {
                    rtsCommit.setLatitude(String.valueOf(in.ecomexpress.geolocations.Constants.latitude));
                    rtsCommit.setLongitude(String.valueOf(in.ecomexpress.geolocations.Constants.longitude));
                } else {
                    rtsCommit.setLatitude(String.valueOf(getDataManager().getCurrentLatitude()));
                    rtsCommit.setLongitude(String.valueOf(getDataManager().getCurrentLongitude()));
                }
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
            rtsCommit.setAddressType(details.isVendor() ? "V" : "W");
            rtsCommit.setFe_emp_code(getDataManager().getCode());
            rtsCommit.setTrip_id(getDataManager().getTripId());
            rtsCommit.setShipmentType(GlobalConstant.ShipmentTypeConstants.RTS);
            rtsCommit.setDeliveryType(GlobalConstant.ShipmentTypeConstants.RTS);
        } catch (Exception e) {
            getNavigator().showErrorSnackbar(e.getMessage());
        }
        List<Shipment> shipmentList = new ArrayList<>();
        for (ShipmentsDetail shipmentsDetail : shipmentsDetails) {
            Shipment shipment = new Shipment();
            shipment.setAssignDate(String.valueOf(java.util.Calendar.getInstance().getTimeInMillis()));
            shipment.setDateTime(String.valueOf(java.util.Calendar.getInstance().getTimeInMillis()));
            shipment.setAwbNo(String.valueOf(shipmentsDetail.getAwbNo()));
            shipment.setDrsNo(shipmentsDetail.getDrsNo());
            shipment.setOrderNo(shipmentsDetail.getOrderNo());
            shipment.setParentAwbNo(shipmentsDetail.getParentAwbNo());
            shipment.setReason(String.valueOf(shipmentsDetail.getReasonCode()));
            shipment.setRemarks("YES");
            shipment.setStatus(shipmentsDetail.getStatus());
            if (isAllPacketUndelivered) {
                shipment.setIs_otp_verified(is_otp_verified);
                shipment.setRchd_otp_verify_status(ofd_otp_verify_status.get());
            } else if (shipmentsDetail.getStatus().equalsIgnoreCase(Constants.RTSMANUALLYDELIVERED)) {
                shipment.setIs_otp_verified(is_otp_verified);
                shipment.SetOfd_otp_verify_status(ofd_otp_verify_status.get());
            } else {
                shipment.setIs_otp_verified(null);
                shipment.SetOfd_otp_verify_status("NONE");
            }
            shipmentList.add(shipment);
        }
        rtsCommit.setShipments(shipmentList);
        if (isCommit) {
            saveCommit(rtsCommit);
        }
    }

    public void saveCommit(RTSCommit rtsCommit) {
        PushApi pushApi = new PushApi();
        pushApi.setAwbNo(Long.parseLong(rtsCommit.getConsigneeId()));
        pushApi.setAuthtoken(getDataManager().getAuthToken());
        try {
            pushApi.setRequestData(new ObjectMapper().writeValueAsString(rtsCommit));
            pushApi.setShipmentStatus(0);
            pushApi.vendor_name = rtsCommit.getVendor_name();
            pushApi.setShipmentCaterogy(GlobalConstant.ShipmentTypeConstants.RTS);
        } catch (Exception e) {
            getNavigator().showErrorSnackbar(e.getMessage());
        }
        try {
            getCompositeDisposable().add(getDataManager().saveCommitPacket(pushApi).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {}));
        } catch (Exception e) {
            getNavigator().showErrorSnackbar(e.getMessage());
        }
    }

    private void updateShipmentStatus(String id) {
        try {
            getCompositeDisposable().add(getDataManager().updateRtsStatus(Long.valueOf(id), 2).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> getNavigator().openSuccessActivity()));
        } catch (Exception e) {
            getNavigator().showErrorSnackbar(e.getMessage());
        }
    }

    public void fetchRTSShipment(long id) {
        try {
            getCompositeDisposable().add(getDataManager().getVWDetails(id).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(details -> getNavigator().onRTSItemFetched(details), throwable -> getNavigator().showErrorSnackbar(String.valueOf(throwable))));
        } catch (Exception e) {
            getNavigator().showErrorSnackbar(e.getMessage());
        }
    }

    public void checkMeterRange(Details details) {
        try {
            double consigneeLatitude = 0.0;
            double consigneeLongitude = 0.0;
            double currentLatitude = getDataManager().getCurrentLatitude();
            double currentLongitude = getDataManager().getCurrentLongitude();
            try {
                consigneeLatitude = details.getLocation().getLat();
                consigneeLongitude = details.getLocation().getLong();
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
            if (consigneeLatitude == 0.0 || consigneeLongitude == 0.0) {
                getNavigator().setConsigneeDistance(0);
                return;
            }
            int meter = LocationHelper.getDistanceBetweenPoint(currentLatitude, currentLongitude, consigneeLatitude, consigneeLongitude);
            getNavigator().setConsigneeDistance(meter);
        } catch (Exception e) {
            getNavigator().showErrorSnackbar(e.getMessage());
        }
    }

    public void uploadImageOnServer(String imageName, String imageUri, String imageCode, long drsNo, Context context) {
        showProgressDialog(context.getString(R.string.image_uploading), context);
        try {
            File image_file = new File(imageUri);
            byte[] bytes = CryptoUtils.decryptFile1(image_file.toString(), Constants.ENC_DEC_KEY);
            RequestBody mFile = RequestBody.create(MediaType.parse("application/octet-stream"), Objects.requireNonNull(bytes));
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", image_file.getName(), mFile);
            RequestBody awb_no = RequestBody.create(MultipartBody.FORM, String.valueOf(drsNo));
            RequestBody drs_no = RequestBody.create(MultipartBody.FORM, String.valueOf(drsNo));
            RequestBody image_code = RequestBody.create(MultipartBody.FORM, String.valueOf(imageCode));
            RequestBody image_name = RequestBody.create(MultipartBody.FORM, String.valueOf(imageName));
            RequestBody image_type = RequestBody.create(MultipartBody.FORM, GlobalConstant.ImageTypeConstants.OTHERS);
            Map<String, RequestBody> map = new HashMap<>();
            map.put("image", mFile);
            map.put("awb_no", awb_no);
            map.put("drs_no", drs_no);
            map.put("image_code", image_code);
            map.put("image_name", image_name);
            map.put("image_type", image_type);
            Map<String, String> headers = new HashMap<>();
            headers.put("token", getDataManager().getAuthToken());
            headers.put("Accept", "application/json");
            try {
                getCompositeDisposable().add(getDataManager().doImageUploadApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), GlobalConstant.ImageTypeConstants.OTHERS, headers, map, fileToUpload).doOnSuccess(imageQualityResponse -> {}).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(imageUploadResponse -> {
                    dismissProgressDialog();
                    String errorDescription;
                    try {
                        if (imageUploadResponse == null) {
                            getNavigator().showErrorSnackbar("API Response Null");
                            return;
                        }
                        errorDescription = (imageUploadResponse.getDescription() == null) ? "Image Upload API Response False" : imageUploadResponse.getDescription();
                        String status = imageUploadResponse.getStatus();
                        if (status == null || (!status.equalsIgnoreCase("true") && !status.equalsIgnoreCase("Success"))) {
                            getNavigator().showErrorSnackbar(errorDescription);
                        } else {
                            saveImageDB(imageUri, imageCode, imageName, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_COMPLETE, imageUploadResponse.getImageId(), 2);
                        }
                    } catch (Exception e) {
                        getNavigator().showErrorSnackbar(String.valueOf(e));
                    }
                }, throwable -> {
                    dismissProgressDialog();
                    getNavigator().showErrorSnackbar(String.valueOf(throwable));
                }));
            } catch (Exception e) {
                dismissProgressDialog();
                getNavigator().showErrorSnackbar(String.valueOf(e));
            }
        } catch (Exception e) {
            dismissProgressDialog();
            getNavigator().showErrorSnackbar(String.valueOf(e));
        }
    }

    private List<ImageModel> getImagesList(String awbNo) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager().getImages(awbNo).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(imageModels -> this.imageModels = imageModels));
        return imageModels;
    }

    public int getCounterDeliveryRange() {
        double currentLatitude = getDataManager().getCurrentLatitude();
        double currentLongitude = getDataManager().getCurrentLongitude();
        double DcLatitude = getDataManager().getDCLatitude();
        double DcLongitude = getDataManager().getDCLongitude();
        return LocationHelper.getDistanceBetweenPoint(currentLatitude, currentLongitude, DcLatitude, DcLongitude);
    }

    public void resendOTPApiCall(boolean otp_resend_email, Context context) {
        try {
            showProgressDialog(context.getString(R.string.resending_otp), context);
            RTSResendOTPRequest rtsResendOTPRequest = new RTSResendOTPRequest();
            ArrayList<String> drs_nos = new ArrayList<>();
            for (int i = 0; i < shipmentsDetails_data.size(); i++) {
                drs_nos.add(shipmentsDetails_data.get(i).getDrsNo());
            }
            rtsResendOTPRequest.setDrs_ids(drs_nos);
            rtsResendOTPRequest.setShipper_id(details.getShipper_id());
            rtsResendOTPRequest.setEmployee_code(getDataManager().getEmp_code());
            rtsResendOTPRequest.setSub_shipper_id(String.valueOf(details.getId()));
            rtsResendOTPRequest.setSub_shipper_mobile(details.getSub_shipper_mobile());
            rtsResendOTPRequest.setOtp_resend_email(otp_resend_email);
            if (fromUDOtp) {
                rtsResendOTPRequest.setMessage_type("UD_OTP");
            }
            getCompositeDisposable().add(getDataManager().doResendOtpApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), rtsResendOTPRequest).doOnSuccess(resendOtpResponse -> {}).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(resendOtpResponse -> {
                dismissProgressDialog();
                try{
                    if (resendOtpResponse == null) {
                        getNavigator().showMessage(context.getString(R.string.api_response_null));
                        return;
                    }
                    if (resendOtpResponse.status) {
                        getNavigator().showCounter();
                        getNavigator().showMessage(resendOtpResponse.description);
                    } else if (resendOtpResponse.response != null) {
                        if (resendOtpResponse.response.status_code.equalsIgnoreCase("107")) {
                            getNavigator().showErrorSnackbar(resendOtpResponse.response.description);
                            clearAppData();
                        } else{
                            getNavigator().showMessage(resendOtpResponse.response.description);
                        }
                    } else {
                        getNavigator().showMessage(resendOtpResponse.description);
                    }
                } catch (Exception e){
                    dismissProgressDialog();
                    getNavigator().showMessage(String.valueOf(e));
                }
            }, throwable -> {
                dismissProgressDialog();
                getNavigator().showMessage(String.valueOf(throwable));
            }));
        } catch (Exception e) {
            dismissProgressDialog();
            getNavigator().showMessage(String.valueOf(e));
        }
    }

    public void resendOTPOtherMobileApiCall(String phone, Context context) {
        try {
            showProgressDialog(context.getString(R.string.generating_otp), context);
            RTSResendOTPRequest rtsResendOTPRequest = new RTSResendOTPRequest();
            ArrayList<String> drs_nos = new ArrayList<>();
            for (int i = 0; i < shipmentsDetails_data.size(); i++) {
                drs_nos.add(shipmentsDetails_data.get(i).getDrsNo());
            }
            rtsResendOTPRequest.setDrs_ids(drs_nos);
            rtsResendOTPRequest.setShipper_id(details.getShipper_id());
            rtsResendOTPRequest.setEmployee_code(getDataManager().getEmp_code());
            rtsResendOTPRequest.setSub_shipper_id(String.valueOf(details.getId()));
            rtsResendOTPRequest.setSub_shipper_mobile(details.getSub_shipper_mobile());
            rtsResendOTPRequest.setPhone(phone);
            if(fromUDOtp) {
                rtsResendOTPRequest.setMessage_type("UD_OTP");
            }
            getCompositeDisposable().add(getDataManager().doResendOtpApiOtherMobileRTSCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), rtsResendOTPRequest).doOnSuccess(resendOtpResponse -> {}).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(resendOtpResponse -> {
                dismissProgressDialog();
                try{
                    if (resendOtpResponse.status) {
                        getNavigator().showCounter();
                        getNavigator().showMessage(resendOtpResponse.description);
                    } else {
                        if (resendOtpResponse.response != null) {
                            if (resendOtpResponse.response.status_code.equalsIgnoreCase("107")) {
                                getNavigator().showErrorSnackbar(resendOtpResponse.response.description);
                                clearAppData();
                            } else {
                                getNavigator().showMessage(resendOtpResponse.description);
                            }
                        } else {
                            getNavigator().showMessage(resendOtpResponse.description);
                        }
                    }
                } catch (Exception e){
                    dismissProgressDialog();
                    getNavigator().showMessage(String.valueOf(e));
                }
            }, throwable -> {
                dismissProgressDialog();
                getNavigator().showMessage(String.valueOf(throwable));
            }));
        } catch (Exception e) {
            dismissProgressDialog();
            getNavigator().showMessage(String.valueOf(e));
        }
    }

    public void verifyOTPApiCall(String otp, Context context) {
        try {
            showProgressDialog(context.getString(R.string.verifying_otp), context);
            RTSVerifyOTPRequest rtsVerifyOTPRequest = new RTSVerifyOTPRequest();
            ArrayList<String> drs_nos = new ArrayList<>();
            for (int i = 0; i < shipmentsDetails_data.size(); i++) {
                drs_nos.add(shipmentsDetails_data.get(i).getDrsNo());
            }
            rtsVerifyOTPRequest.setDrs_ids(drs_nos);
            rtsVerifyOTPRequest.setShipper_id(details.getShipper_id());
            rtsVerifyOTPRequest.setOtp(otp);
            rtsVerifyOTPRequest.setEmployee_code(getDataManager().getEmp_code());
            rtsVerifyOTPRequest.setSub_shipper_id(String.valueOf(details.getId()));
            rtsVerifyOTPRequest.setSub_shipper_mobile(details.getSub_shipper_mobile());
            getCompositeDisposable().add(getDataManager().doVerifyOTPApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), rtsVerifyOTPRequest).doOnSuccess(rtsVerifyOTPResponse -> {}).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(rtsVerifyOTPResponse -> {
                dismissProgressDialog();
                try{
                    if (rtsVerifyOTPResponse.isStatus()) {
                        is_otp_verified = "true";
                        ud_otp_verified_status.set(true);
                        if (fromUDOtp) {
                            new_ud_otp_verify_status.set("VERIFIED");
                        } else {
                            ofd_otp_verify_status.set("VERIFIED");
                        }
                        getNavigator().setOTPVerify(true, rtsVerifyOTPResponse.getDescription());
                    } else {
                        is_otp_verified = "false";
                        getNavigator().setOTPVerify(false, rtsVerifyOTPResponse.getDescription() == null ? "Verify OTP API Response False" : rtsVerifyOTPResponse.getDescription());
                    }
                } catch (Exception e){
                    dismissProgressDialog();
                    getNavigator().showMessage(String.valueOf(e));
                }
            }, throwable -> {
                dismissProgressDialog();
                getNavigator().showMessage(String.valueOf(throwable));
            }));
        } catch (Exception e) {
            dismissProgressDialog();
            getNavigator().showMessage(String.valueOf(e));
        }
    }

    private void clearAppData() {
        try {
            getCompositeDisposable().add(getDataManager().deleteAllTables().subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
                try {
                    getDataManager().clearPrefrence();
                    getDataManager().setUserAsLoggedOut();
                    getDataManager().setTripId("0");
                    getDataManager().setIsAdmEmp(false);
                    getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT);
                } catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                }
                getNavigator().clearStack();
            }));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void logoutLocal() {
        getDataManager().setTripId("");
        getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT);
        clearAppData();
    }

    public void getRtsShipmentsData(long vwID) {
        try {
            getCompositeDisposable().add(getDataManager().getVWShipmentList(vwID).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(shipmentsDetails -> {
                shipmentsDetails_data = shipmentsDetails;
                listShipmentObserver.set(shipmentsDetails);
            }));
        } catch (Exception e) {
            getNavigator().showErrorSnackbar(e.getMessage());
        }
    }

    public boolean getDCFEDistance() {
        double distance = LocationHelper.getDistanceBetweenPoint(
            getDataManager().getCurrentLatitude(),
            getDataManager().getCurrentLongitude(),
            getDataManager().getDCLatitude(),
            getDataManager().getDCLongitude()
        );
        return distance < getDataManager().getDCRANGE();
    }

    private void showProgressDialog(String loadingMessage, Context context) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_progress_dialog);
        dialog.setCancelable(false);
        TextView loadingText = dialog.findViewById(R.id.dialog_loading_text);
        loadingText.setText(loadingMessage);
        dialog.show();
    }

    private void dismissProgressDialog(){
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }
}