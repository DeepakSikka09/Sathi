package in.ecomexpress.sathi.ui.drs.forward.signature;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.databinding.ObservableField;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nlscan.android.scan.ScanManager;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.repo.local.data.commit.PushApi;
import in.ecomexpress.sathi.repo.local.db.model.ImageModel;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.forward.generateOTP.GenerateOTPRequest;
import in.ecomexpress.sathi.repo.remote.model.forward.verifyOTP.VerifyOTPRequest;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
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
public class SignatureViewModel extends BaseViewModel<ISignatureNavigator> {
    public static String device = (Build.MANUFACTURER + ":" + Build.MODEL).toUpperCase(Locale.US);
    public final ObservableField<String> awbNo = new ObservableField<>("");
    public final ObservableField<String> receiverName = new ObservableField<>();
    public final ObservableField<String> deliveryAddress = new ObservableField<>("");
    public final ObservableField<String> isScanAwb = new ObservableField<>();
    private final ObservableField<String> receiver = new ObservableField<>();
    private final ObservableField<Boolean> isImageCaptured = new ObservableField<>(false);
    private final ObservableField<Boolean> isFWDDelImageCaptured = new ObservableField<>(false);
    boolean isSubmitClicked = false;
    boolean otpVerified = false;
    Dialog ScanAlertdialog;
    boolean sign_image_required;
    boolean fwd_del_image;
    List<ImageModel> mImageModels = new ArrayList<>();
    private ForwardCommit forwardCommit;
    private String parentAWBno;
    private long mLastClickTime = 0;
    private BroadcastReceiver mReceiver;

    @Inject
    public SignatureViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);
    }

    public void onReceiver(AdapterView<?> parent, View view, int pos, long id) {
        try {
            receiver.set(parent.getSelectedItem().toString());
            if (parent.getSelectedItem().toString().equalsIgnoreCase("self")) {
                receiverName.set(forwardCommit.getConsignee_name());
                getNavigator().enableEditText(false);
                getNavigator().hideEdit("self");
            } else if (parent.getSelectedItem().toString().equalsIgnoreCase("Select")) {
                getNavigator().enableEditText(false);
                getNavigator().hideEdit("Select");
                receiverName.set("");
            } else {
                if ((getNavigator().getReceiverName()).equalsIgnoreCase(forwardCommit.getConsignee_name())) {
                    getNavigator().enableEditText(true);
                    getNavigator().hideEdit("self");
                    receiverName.set("");
                } else {
                    getNavigator().enableEditText(true);
                    getNavigator().hideEdit("self");
                }
            }
        } catch (Exception e) {
            Logger.e(SignatureViewModel.class.getName(), e.getMessage());

            getNavigator().showError(e.getLocalizedMessage());
        }
    }


    public void onCaptureImageClick() {
        getNavigator().onCaptureImage();
    }

    public void onCaptureImageClick2() {
        getNavigator().onCaptureImage2();
    }
    public void onClear() {
        getNavigator().onClear();
    }

    public boolean getIsAwbScan() {
        boolean scanAwb = true;
        if (scanAwb) isScanAwb.set("M");
        else isScanAwb.set("O");
        return scanAwb;
    }

    public void onGenerateOTPClick() {

        SignatureViewModel.this.setIsLoading(true);
        try {
            GenerateOTPRequest request = new GenerateOTPRequest();
            request.setAwb_number(forwardCommit.getAwb());
            request.setDrs_id(forwardCommit.getDrs_id());
            final long timeStamp = System.currentTimeMillis();
            writeRestAPIRequst(timeStamp, request);
            getCompositeDisposable().add(getDataManager().doGenerateOTPApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(generateOTPResponse -> {
                SignatureViewModel.this.setIsLoading(false);
                try {
                    if (generateOTPResponse.getStatus().equalsIgnoreCase("false")) {
                        getNavigator().showError(generateOTPResponse.getDescription());
                    } else {
                        getNavigator().showError("Otp generated successfully.");
                    }
                } catch (Exception e) {
                    getNavigator().showError(e.getMessage());
                }
            }, throwable -> {
                try {
                    writeErrors(System.currentTimeMillis(), new Exception(throwable));
                    SignatureViewModel.this.setIsLoading(false);
                    getNavigator().showHandleError(throwable.getMessage().contains("HTTP 500 "));
                } catch (Exception e) {
                    getNavigator().showError(e.getMessage());
                }
            }));
        } catch (Exception e) {
            Logger.e(SignatureViewModel.class.getName(), e.getMessage());
            writeErrors(System.currentTimeMillis(), e);
            SignatureViewModel.this.setIsLoading(false);
            getNavigator().showError(e.getMessage());
        }
    }

    public void onVerifyOTPClick() {
        if (getNavigator().getotp().equalsIgnoreCase("") || getNavigator().getotp() == null || getNavigator().getotp().length() > 3) {
            getNavigator().onHandleError("please enter otp.");
            return;
        }
        SignatureViewModel.this.setIsLoading(true);
        try {
            VerifyOTPRequest request = new VerifyOTPRequest();
            request.setAwb(forwardCommit.getAwb());
            request.setOtp(getNavigator().getotp());
            request.setOtpType("D");
            request.setDrs_id(forwardCommit.getDrs_id());
            final long timeStamp = System.currentTimeMillis();
            writeRestAPIRequst(timeStamp, request);
            getCompositeDisposable().add(getDataManager().doVerifyOTPApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(verifyOTPResponse -> {
                SignatureViewModel.this.setIsLoading(false);
                try {
                    if (verifyOTPResponse.getStatus().equalsIgnoreCase("true")) {
                        getNavigator().onHandleError("Otp verified successfully.");
                        otpVerified = true;
                        getNavigator().switchLayoutGone();
                    } else {
                        getNavigator().onHandleError("Otp not verified.");
                    }
                } catch (Exception e) {
                    getNavigator().showError(e.getMessage());
                }
            }, throwable -> {
                try {
                    writeErrors(System.currentTimeMillis(), new Exception(throwable));
                    SignatureViewModel.this.setIsLoading(false);
                    getNavigator().showHandleError(throwable.getMessage().contains("HTTP 500 "));
                } catch (Exception e) {
                    getNavigator().showError(e.getMessage());
                }
            }));
        } catch (Exception e) {
            Logger.e(SignatureViewModel.class.getName(), e.getMessage());
            writeErrors(System.currentTimeMillis(), e);
            SignatureViewModel.this.setIsLoading(false);
            getNavigator().showError(e.getMessage());
        }
    }

    public void setImageRequired(boolean sign_image_required) {
        this.sign_image_required = sign_image_required;
    }
    public void setFwdDelImageRequired(boolean fwd_del_image) {
        this.fwd_del_image = fwd_del_image;
    }
    public void onSubmitClick() {
        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        try {
            if (deliveryAddress.get() == null || deliveryAddress.get().isEmpty() || deliveryAddress.get().equals("")) {
                getNavigator().showError("Please Choose Delivery Address");
                return;
            }
            if (receiver.get() == null || receiver.get().isEmpty() || receiver.get().equalsIgnoreCase("Select")) {
                getNavigator().submitErrorAlert();
                return;
            }
            if (receiverName.get().isEmpty() || receiverName.get().equalsIgnoreCase("null") || receiverName.get().equalsIgnoreCase("Select")) {
                getNavigator().showError("Please Fill Receiver's Name");
                return;
            }
            if (sign_image_required && !isImageCaptured.get()) {
                getNavigator().showError("Please capture image first");
                return;
            }
            if (fwd_del_image && !isFWDDelImageCaptured.get()) {
                getNavigator().showError("Please capture second image ");
                return;
            }
            if (getIsAwbScan()) {
                if (!getNavigator().getScanedResult()) {
                    getNavigator().showError("Please Scan bar code");
                    return;
                }
            }
        } catch (Exception e) {
            Logger.e(SignatureViewModel.class.getName(), e.getMessage());
        }
        if (getDCFEDistance()) {
            getRemarkCount(Long.parseLong(forwardCommit.getAwb()));
            if (getDataManager().getIsSignatureImageMandatory().equalsIgnoreCase("true")) {
                getNavigator().saveSignature();
            } else {
                isSubmitClicked = true;
                getNavigator().saveCommit();
            }
        }
    }

    public boolean switchGone() {
        return !otpVerified;
    }



    private void updateShipmentStatus(String composite_key) {
        try {
            getCompositeDisposable().add(getDataManager().updateForwardStatus(composite_key, Constants.SHIPMENT_DELIVERED_STATUS).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> getNavigator().showSuccessStatus(forwardCommit)));
        } catch (Exception e) {
            Logger.e(SignatureViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getLocalizedMessage());
            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
            restApiErrorHandler.writeErrorLogs(0, e.getMessage());
        }
    }

    private void isMPSShipment(ForwardCommit forwardCommit, String compositeKey, boolean iscommit) {
        try {
            getCompositeDisposable().add(getDataManager().getForwardDRS(compositeKey).subscribeOn(getSchedulerProvider().io()).subscribe(drsForwardTypeResponse -> {
                String mpsShipment = drsForwardTypeResponse.mpsShipment;
                List<ForwardCommit> fwd = new ArrayList<>();
                if (mpsShipment != null && mpsShipment.length() > 0) {
                    String awbsNos = drsForwardTypeResponse.mpsAWBs;
                    Logger.e("TAG", "MPS shipment awbNumbers: " + awbsNos);
                    awbsNos = awbsNos.substring(1).trim();
                    awbsNos = awbsNos.substring(0, awbsNos.length() - 1);
                    String[] awbArr = awbsNos.split(",");
                    for (String awb : awbArr) {
                        try {
                            ForwardCommit fwdCommit = (ForwardCommit) forwardCommit.clone();
                            if (!(Long.parseLong(awb.trim()) == drsForwardTypeResponse.getAwbNo())) {
                                fwdCommit.setOfd_customer_otp("");
                                fwdCommit.setOfd_otp_verified("false");
                            }
                            fwdCommit.setAwb(awb.trim());
                            fwdCommit.setTrip_id(getDataManager().getTripId());
                            fwdCommit.setParent_awb(String.valueOf(drsForwardTypeResponse.getAwbNo()));
                            if (getDataManager().getConsigneeProfileValue().equalsIgnoreCase("W")) {
                                fwdCommit.setFlag_of_warning("W");
                            } else {
                                fwdCommit.setFlag_of_warning("N");
                            }
                            fwd.add(fwdCommit);
                        } catch (Exception e) {
                            Logger.e(SignatureViewModel.class.getName(), e.getMessage());
                        }

                    }
                    if (iscommit) {
                        uploadForwardShipment(fwd, drsForwardTypeResponse.getAwbNo(), compositeKey);
                    }
                    updateMPSShipmentStatus(awbArr);
                } else {
                    Logger.e("TAG", "Not a mps shipment awbNo: " + compositeKey);
                    forwardCommit.setParent_awb(String.valueOf(drsForwardTypeResponse.getAwbNo()));
                    fwd.add(forwardCommit);
                    uploadForwardShipment(fwd, drsForwardTypeResponse.getAwbNo(), compositeKey);
                    updateShipmentStatus(compositeKey);
                }
            }, throwable -> {
            }));
        } catch (Exception e) {
            Logger.e(SignatureViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getLocalizedMessage());
            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
            restApiErrorHandler.writeErrorLogs(0, e.getMessage());
        }
    }

    private void updateMPSShipmentStatus(String[] awbArr) {
        try {
            getCompositeDisposable().add(getDataManager().updateForwardMPSShipmentStatus(awbArr, Constants.SHIPMENT_DELIVERED_STATUS).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {
                if (parentAWBno != null) forwardCommit.setAwb(parentAWBno);
                getNavigator().showSuccessStatus(forwardCommit);
            }, Throwable::printStackTrace));
        } catch (Exception e) {
            Logger.e(SignatureViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
            restApiErrorHandler.writeErrorLogs(0, e.getMessage());
        }
    }

    private void updateCodCollected(String awb, float amount) {
        try {
            getCompositeDisposable().add(getDataManager().insertcodCollected(Long.valueOf(awb), amount).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {
                if (isSubmitClicked && getDataManager().getIsSignatureImageMandatory().equalsIgnoreCase("false")) {
                    getNavigator().callCommit("", "", getNavigator().getCompositeKey());
                }
            }));
        } catch (Exception e) {
            Logger.e(SignatureViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
            restApiErrorHandler.writeErrorLogs(0, e.getMessage());
        }
    }

    private void updateEcodCollected(String awb, float amount) {
        try {
            getCompositeDisposable().add(getDataManager().insertEcodCollected(Long.valueOf(awb), amount).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {
                if (isSubmitClicked && getDataManager().getIsSignatureImageMandatory().equalsIgnoreCase("false")) {
                    getNavigator().callCommit("", "", getNavigator().getCompositeKey());
                }
            }));
        } catch (Exception e) {
            Logger.e(SignatureViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
            restApiErrorHandler.writeErrorLogs(0, e.getMessage());
        }
    }

    private void saveCommit(List<ForwardCommit> forwardCommit, long parentAWB, String composite_key) {
        Log.d("TAG", "saveCommit: forward" + forwardCommit.toString());
        PushApi pushApi = new PushApi();
        pushApi.setAwbNo(parentAWB);
        pushApi.setCompositeKey(composite_key);

        pushApi.setAuthtoken(getDataManager().getAuthToken());
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<ForwardCommit> forwardcommitList = forwardCommit;
            pushApi.setRequestData(mapper.writeValueAsString(forwardcommitList));
            pushApi.setShipmentStatus(Constants.SHIPMENT_ASSIGNED_STATUS);
            pushApi.setShipmentDeliveryStatus("2");
            pushApi.setShipmentCaterogy(GlobalConstant.ShipmentTypeConstants.FWD);
            getCompositeDisposable().add(getDataManager().saveCommitPacket(pushApi).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {

            }));
        } catch (Exception e) {
            Logger.e(SignatureViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    private void saveCommitUpload(List<ForwardCommit> forwardCommit, long parentAWB, String composite_key) {
        Log.d("TAG", "saveCommit: forward" + forwardCommit.toString());
        PushApi pushApi = new PushApi();
        pushApi.setAwbNo(parentAWB);
        pushApi.setCompositeKey(composite_key);

        pushApi.setAuthtoken(getDataManager().getAuthToken());
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<ForwardCommit> forwardcommitList = forwardCommit;
            pushApi.setRequestData(mapper.writeValueAsString(forwardcommitList));
            pushApi.setShipmentStatus(Constants.SHIPMENT_DELIVERED_STATUS);
            pushApi.setShipmentDeliveryStatus("2");
            pushApi.setShipmentCaterogy(GlobalConstant.ShipmentTypeConstants.FWD);
            getCompositeDisposable().add(getDataManager().saveCommitPacket(pushApi).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {

            }));
        } catch (Exception e) {
            Logger.e(SignatureViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void onForwardDRSCommit(ForwardCommit forwardCommit) {
        forwardCommit.setTrip_id(getDataManager().getTripId());
        this.forwardCommit = forwardCommit;
        awbNo.set(forwardCommit.getAwb());
    }

    public void setDeliveryAddress(String address) {
        deliveryAddress.set(address);
        forwardCommit.setAddress_type("" + address.charAt(0));
    }

    public void setImageCaptured(boolean isImageCaptured) {
        this.isImageCaptured.set(isImageCaptured);
    }

    public void setFwdDelImageCaptured(boolean isFWDDelImageCaptured) {
        this.isFWDDelImageCaptured.set(isFWDDelImageCaptured);
    }

    public void getInfo(String type, float amount) {
        if (type.equalsIgnoreCase("ppd")) {
            if (isSubmitClicked && getDataManager().getIsSignatureImageMandatory().equalsIgnoreCase("false")) {
                getNavigator().callCommit("", "", getNavigator().getCompositeKey());
            }
        } else {
            if (type != null && amount != 0) {
                if (type.equalsIgnoreCase("cod")) {
                    updateCodCollected(forwardCommit.getAwb(), amount);
                } else if (type.equalsIgnoreCase("ecod")) {
                    updateEcodCollected(forwardCommit.getAwb(), amount);
                }
            }
        }
    }

    public String loginDate() {
        return getDataManager().getLoginDate();
    }


    @SuppressLint("CheckResult")
    public void uploadAWSImage(String imageUri, String imageCode, String imageKey, int imageId, int imageStatus, Boolean isCommit, String compositeKey, boolean iscommit) {
        try {
            if (imageUri != null) {
                saveImageDB(imageUri, imageCode, imageKey, imageId, imageStatus);
            }
            if (isCommit) {
                forwardCommit.setReceived_by_name(receiverName.get());
                forwardCommit.setStatus(Constants.DELIVERED);
                forwardCommit.setShipment_id(getDataManager().getShipperId());
                forwardCommit.setReceived_by_relation(receiver.get());
                forwardCommit.setReschedule_date("");
                Gson gson = new Gson();
                Type type = new TypeToken<List<ForwardCommit.Amz_Scan>>() {}.getType();
                ArrayList<ForwardCommit.Amz_Scan> amz_scans = gson.fromJson(getDataManager().getAmazonList(), type);
                forwardCommit.setAmz_scan(amz_scans);
                if (getDataManager().getDlightSuccessEncrptedOTPType() != null && !getDataManager().getDlightSuccessEncrptedOTPType().equalsIgnoreCase("")) {
                    forwardCommit.setDlight_encrption_otp_type(getDataManager().getDlightSuccessEncrptedOTPType());
                }
                forwardCommit.setAttempt_reason_code("999");

                try {
                    if (!String.valueOf(in.ecomexpress.geolocations.Constants.latitude).equalsIgnoreCase("0.0") && !String.valueOf(in.ecomexpress.geolocations.Constants.longitude).equalsIgnoreCase("0.0")) {
                        forwardCommit.setLocation_lat(String.valueOf(in.ecomexpress.geolocations.Constants.latitude));
                        forwardCommit.setLocation_long(String.valueOf(in.ecomexpress.geolocations.Constants.longitude));
                    } else if (!Constants.CURRENT_LATITUDE.equalsIgnoreCase("0.0") && !Constants.CURRENT_LONGITUDE.equalsIgnoreCase("0.0")) {
                        forwardCommit.setLocation_lat(Constants.CURRENT_LATITUDE);
                        forwardCommit.setLocation_long(Constants.CURRENT_LONGITUDE);
                    } else {
                        forwardCommit.setLocation_lat(String.valueOf(getDataManager().getCurrentLatitude()));
                        forwardCommit.setLocation_long(String.valueOf(getDataManager().getCurrentLongitude()));
                    }
                } catch (Exception e) {
                    Logger.e(SignatureViewModel.class.getName(), e.getMessage());
                }
                forwardCommit.setAttempt_type("FWD");
                forwardCommit.setDrs_commit_date_time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                forwardCommit.setTrip_id(getDataManager().getTripId());
                forwardCommit.setFe_emp_code(getDataManager().getCode());
                parentAWBno = forwardCommit.getAwb();
                isMPSShipment(forwardCommit, compositeKey, iscommit);
            }
        } catch (Exception e) {
            Logger.e(SignatureViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void saveImageDB(String imageUri, String imageCode, String name, int imageId, int sync_staus) {
        try {
            ImageModel imageModel = new ImageModel();
            imageModel.setDraNo(forwardCommit.getDrs_id());
            imageModel.setAwbNo(forwardCommit.getAwb());
            imageModel.setImageName(name);
            imageModel.setImage(imageUri);
            imageModel.setImageCode(imageCode);
            imageModel.setImageId(imageId);
            imageModel.setImageCurrentSyncStatus(sync_staus);
            imageModel.setImageFutureSyncTime(System.currentTimeMillis());
            imageModel.setStatus(sync_staus);
            imageModel.setImageType(GlobalConstant.ImageTypeConstants.OTHERS);
            imageModel.setDate(System.currentTimeMillis());
            imageModel.setShipmentType(GlobalConstant.ShipmentTypeConstants.FWD);

            getCompositeDisposable().add(getDataManager().saveImage(imageModel).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
                if (getNavigator() != null) {
                    getNavigator().saveCommit();
                } else {
                    getNavigator().showError("Please Try Again");
                }
            },
            throwable -> getNavigator().showError("Please Try Again")));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
        }
    }

    /**
     *
     * @param drsid-- drs id
     * @param awbNo-- awb no.
     */
    public void fetchForwardShipment(String drsid, String awbNo) {
        try {
            getCompositeDisposable().add(getDataManager().getForwardDRSCompositeKey(Long.valueOf(awbNo)).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(drsForwardTypeResponse -> getNavigator().onDRSForwardItemFetch(drsForwardTypeResponse), throwable -> {
                writeErrors(System.currentTimeMillis(), new Exception(throwable));
                throwable.printStackTrace();
            }));
        } catch (Exception e) {
            Logger.e(SignatureViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void checkMeterRange(DRSForwardTypeResponse drsForwardTypeResponse) {
        try {
            getProfileLatLng(drsForwardTypeResponse);
        } catch (Exception e) {
            Logger.e(SignatureViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    private void getProfileLatLng(DRSForwardTypeResponse drsForwardTypeResponse) {
        try {
            getCompositeDisposable().add(getDataManager().getProfileLat(drsForwardTypeResponse.getAwbNo()).subscribeOn(getSchedulerProvider().io()).subscribe(profileFound -> {
                try {
                    if (profileFound != null && profileFound.getAwb_number() != 0) {
                        double consigneeLatitude = 0.0;
                        double consigneeLongitude = 0.0;
                        double currentLatitude = 0.0;
                        double currentLongitude = 0.0;
                        try {
                            currentLatitude = in.ecomexpress.geolocations.Constants.latitude;
                            currentLongitude = in.ecomexpress.geolocations.Constants.longitude;
                            consigneeLatitude = profileFound.getDelivery_latitude();
                            consigneeLongitude = profileFound.getDelivery_longitude();
                        } catch (Exception e) {
                            Logger.e(SignatureViewModel.class.getName(), e.getMessage());
                        }
                        if (consigneeLatitude == 0.0 || consigneeLongitude == 0.0) {
                            getNavigator().setConsigneeDistance(0);
                            return;
                        }
                        int meter = LocationHelper.getDistanceBetweenPoint(currentLatitude, currentLongitude, consigneeLatitude, consigneeLongitude);
                        getNavigator().setConsigneeDistance(meter);
                    }
                } catch (Exception e) {
                    Logger.e(SignatureViewModel.class.getName(), e.getMessage());
                }
            }, throwable -> Log.e("error", throwable.getMessage())));
        } catch (Exception e) {
            Logger.e(SignatureViewModel.class.getName(), e.getMessage());
        }
    }

    public void getConsigneeProfiling() {
        boolean enable = getDataManager().getConsigneeProfiling();
        getNavigator().setConsingeeProfiling(enable);
    }

    public void uploadImageServer(String imageName, String imageUri, String imageCode, long awbNo, int drsno, String activity_code, Bitmap bitmap, String compositeKey, boolean isSignature) {
        setIsLoading(true);
        try {
            File image_file = new File(imageUri);
            byte[] bytes = CryptoUtils.decryptFile1(image_file.toString(), Constants.ENC_DEC_KEY);
            RequestBody mFile = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", image_file.getName(), mFile);
            RequestBody awb_no = RequestBody.create(MultipartBody.FORM, String.valueOf(awbNo));
            RequestBody drs_no = RequestBody.create(MultipartBody.FORM, String.valueOf(drsno));
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
                getCompositeDisposable().add(getDataManager().doImageUploadApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), GlobalConstant.ImageTypeConstants.OTHERS, headers, map, fileToUpload).doOnSuccess(imageQualityResponse -> Log.d(ContentValues.TAG, imageQualityResponse.toString())).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(imageUploadResponse -> {
                    setIsLoading(false);
                    try {
                        if (imageUploadResponse.getStatus().equalsIgnoreCase("Success")) {
                            if (isSignature) {
                                saveImageDB(imageUri, imageCode, imageName, imageUploadResponse.getImageId(), GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_COMPLETE);
                                getNavigator().callCommit(String.valueOf(imageUploadResponse.getImageId()), imageName, compositeKey);
                            } else {
                                saveImageDB(imageUri, imageCode, imageName, imageUploadResponse.getImageId(), GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_COMPLETE);
                                getNavigator().setBitmap();
                            }
                        } else {
                            if (isSignature) {
                                getNavigator().setCommitOffline(imageUri);
                            } else {
                                getNavigator().showError("Image upload Api response failed.");
                            }
                        }
                    } catch (Exception e) {
                        if (isSignature) {
                            getNavigator().setCommitOffline(imageUri);
                        } else {
                            getNavigator().showError("Exception while uploading image : " + e.getLocalizedMessage());
                        }
                        Logger.e(SignatureViewModel.class.getName(), e.getMessage());
                        setIsLoading(false);
                    }
                }, throwable -> {
                    setIsLoading(false);
                    try {
                        if (isSignature) {
                            getNavigator().setCommitOffline(imageUri);
                        } else {
                            getNavigator().showError("Exception while uploading image : " + throwable.getLocalizedMessage());
                        }
                    } catch (Exception e) {
                        Logger.e(SignatureViewModel.class.getName(), e.getMessage());
                    }
                }));
            } catch (Exception e) {
                setIsLoading(false);
                if (isSignature) {
                    getNavigator().setCommitOffline(imageUri);
                } else {
                    getNavigator().showError("Exception while uploading image : " + e.getLocalizedMessage());
                }
            }
        } catch (Exception e) {
            if (isSignature) {
                getNavigator().setCommitOffline(imageUri);
            } else {
                getNavigator().showError("Exception while uploading image : " + e.getLocalizedMessage());
            }
            setIsLoading(false);
            Log.e("Image Sync exception", e.toString());
        }
    }

    public void createCommitPacket(ForwardCommit forwardCommits, String image_id, String image_key, String compositeKey) {
        try {
            forwardCommit = forwardCommits;
            mImageModels = getImagesList(forwardCommit.getAwb(), image_id, image_key, compositeKey);

            Log.d("TAG", "uploadAWSImage: " + forwardCommit);
        } catch (Exception e) {
            Logger.e(SignatureViewModel.class.getName(), e.getMessage());

        }
    }

    @SuppressLint("CheckResult")
    private void uploadForwardShipment(List<ForwardCommit> forwardCommits, long parentAWB, String composite_key) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        HashMap<String, String> tokens = new HashMap<>();
        tokens.put(Constants.TOKEN, getDataManager().getAuthToken());
        tokens.put(Constants.EMP_CODE, getDataManager().getCode());
        try {
            compositeDisposable.add(getDataManager().doFWDCommitApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), tokens, forwardCommits).subscribeOn(getSchedulerProvider().io()).subscribe(forwardCommitResponse -> {
                if (forwardCommitResponse.getStatus()) {
                    int shipement_status = 0;
                    String compositeKey = "";
                    if (forwardCommitResponse.getResponse().getShipment_status().equalsIgnoreCase(Constants.UNDELIVERED)) {
                        shipement_status = Constants.SHIPMENT_UNDELIVERED_STATUS;
                    } else {
                        shipement_status = Constants.SHIPMENT_DELIVERED_STATUS;
                    }
                    try {
                        compositeKey = forwardCommitResponse.getResponse().getDrs_no() + forwardCommitResponse.getResponse().getAwb_no();
                    } catch (Exception e) {
                        Logger.e(SignatureViewModel.class.getName(), e.getMessage());
                    }
                    getDataManager().updateForwardStatus(compositeKey, shipement_status).subscribe(aBoolean -> {
                        Log.e("ForwardResponse", " Result:-forwardData");
                        updateSyncStatusInDRSFWDTable(forwardCommitResponse.getResponse().getDrs_no() + "" + forwardCommitResponse.getResponse().getAwb_no(), GlobalConstant.CommitStatus.CommitSynced);
                        compositeDisposable.add(getDataManager().deleteSyncedImage(forwardCommitResponse.getResponse().getAwb_no()).subscribe(aBoolean1 -> {

                        }));
                        saveCommitUpload(forwardCommits, parentAWB, composite_key);
                    }, throwable -> {
                        throwable.printStackTrace();
                        saveCommit(forwardCommits, parentAWB, composite_key);
                    });
                } else if ((forwardCommitResponse.getResponse().getCode().equalsIgnoreCase("E107")) || (forwardCommitResponse.getResponse().getCode().equalsIgnoreCase("107"))) {
                    saveCommit(forwardCommits, parentAWB, composite_key);
                }
            }, throwable -> {
                throwable.printStackTrace();
                saveCommit(forwardCommits, parentAWB, composite_key);
            }));
        } catch (Exception e) {
            saveCommit(forwardCommits, parentAWB, composite_key);
            Logger.e(SignatureViewModel.class.getName(), e.getMessage());
        }
    }


    /**
     *
     * @param awbNo-- awb no
     * @param image_id-- image id
     * @param imageKey-- image key
     * @param compositeKey-- composite key
     */
    private List<ImageModel> getImagesList(String awbNo, String image_id, String imageKey, String compositeKey) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager().getImages(awbNo).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(imageModels -> {
            Log.e("getImages", imageModels.size() + "");
            mImageModels = imageModels;
            forwardCommit.setReceived_by_name(receiverName.get());
            forwardCommit.setShipment_id(getDataManager().getShipperId());
            forwardCommit.setStatus(Constants.DELIVERED);
            Gson gson = new Gson();
            Type type = new TypeToken<List<ForwardCommit.Amz_Scan>>() {
            }.getType();
            ArrayList<ForwardCommit.Amz_Scan> amz_scans = gson.fromJson(getDataManager().getAmazonList(), type);
            forwardCommit.setAmz_scan(amz_scans);
            if (getDataManager().getDlightSuccessEncrptedOTPType() != null && !getDataManager().getDlightSuccessEncrptedOTPType().equalsIgnoreCase("")) {
                forwardCommit.setDlight_encrption_otp_type(getDataManager().getDlightSuccessEncrptedOTPType());
            }
            forwardCommit.setReceived_by_relation(receiver.get());
            forwardCommit.setReschedule_date("");
            forwardCommit.setAttempt_reason_code("999");
            try {
                if (!String.valueOf(in.ecomexpress.geolocations.Constants.latitude).equalsIgnoreCase("0.0") && !String.valueOf(in.ecomexpress.geolocations.Constants.longitude).equalsIgnoreCase("0.0")) {
                    forwardCommit.setLocation_lat(String.valueOf(in.ecomexpress.geolocations.Constants.latitude));
                    forwardCommit.setLocation_long(String.valueOf(in.ecomexpress.geolocations.Constants.longitude));
                } else if (!Constants.CURRENT_LATITUDE.equalsIgnoreCase("0.0") && !Constants.CURRENT_LONGITUDE.equalsIgnoreCase("0.0")) {
                    forwardCommit.setLocation_lat(Constants.CURRENT_LATITUDE);
                    forwardCommit.setLocation_long(Constants.CURRENT_LONGITUDE);
                } else {
                    forwardCommit.setLocation_lat(String.valueOf(getDataManager().getCurrentLatitude()));
                    forwardCommit.setLocation_long(String.valueOf(getDataManager().getCurrentLongitude()));
                }
            } catch (Exception e) {
                Logger.e(SignatureViewModel.class.getName(), e.getMessage());
            }
            if (getDataManager().getConsigneeProfileValue().equalsIgnoreCase("W")) {
                forwardCommit.setFlag_of_warning("W");
            } else {
                forwardCommit.setFlag_of_warning("N");
            }
            forwardCommit.setAttempt_type("FWD");
            forwardCommit.setDrs_commit_date_time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            forwardCommit.setTrip_id(getDataManager().getTripId());
            forwardCommit.setFe_emp_code(getDataManager().getCode());
            parentAWBno = forwardCommit.getAwb();
            List<ForwardCommit.Image_response> list_image_responses = new ArrayList<>();
            if (mImageModels.size() > 0) {
                for (int i = 0; i < mImageModels.size(); i++) {
                    ForwardCommit.Image_response image_response = new ForwardCommit.Image_response();
                    image_response.setImage_id(String.valueOf(mImageModels.get(i).getImageId()));
                    image_response.setImage_key(String.valueOf(mImageModels.get(i).getImageName()));
                    list_image_responses.add(image_response);
                }
                forwardCommit.setImage_response(list_image_responses);
            } else {
                ForwardCommit.Image_response image_response = new ForwardCommit.Image_response();
                image_response.setImage_id(image_id);
                image_response.setImage_key(imageKey);
                list_image_responses.add(image_response);
                forwardCommit.setImage_response(list_image_responses);
            }
            isMPSShipment(forwardCommit, compositeKey, true);
        }));
        return mImageModels;
    }

    private void updateSyncStatusInDRSFWDTable(String composite_key, int i) {

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager().updateSyncStatusFWD(composite_key, i).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {
        }, throwable -> {
        }));
    }

    private void saveImageResponse(String image_name, int image_id) {
        getCompositeDisposable().add(getDataManager().updateImageStatus(image_name, 2).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
        }));
        getCompositeDisposable().add(getDataManager().updateImageID(image_name, image_id).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
        }));
    }

    public void scanAwb() {
        getNavigator().scanAwb();
    }

    BroadcastReceiver mResultReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ScanManager.ACTION_SEND_SCAN_RESULT.equals(action)) {
                    byte[] bvalue = intent.getByteArrayExtra(ScanManager.EXTRA_SCAN_RESULT_ONE_BYTES);
                    String sValue = intent.getStringExtra("SCAN_BARCODE1");
                    getNavigator().mResultReceiver1(sValue);

                    try {
                        if (sValue == null && bvalue != null) sValue = new String(bvalue, "GBK");
                        sValue = sValue == null ? "" : sValue;
                    } catch (Exception e) {
                        Logger.e(SignatureViewModel.class.getName(), e.getMessage());
                    }
                }
            }
        };
        return mReceiver;
    }

    public int getCounterDeliveryRange() {
        double currentLatitude = getDataManager().getCurrentLatitude();
        double currentLongitude = getDataManager().getCurrentLongitude();
        double DcLatitude = getDataManager().getDCLatitude();
        double DcLongitude = getDataManager().getDCLongitude();
        int meter = LocationHelper.getDistanceBetweenPoint(currentLatitude, currentLongitude, DcLatitude, DcLongitude);
        return meter;
    }

    public void doScanAgainAlert(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
        builder.setMessage("Please scan valid bar code of this manifest.");
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            dialogInterface.cancel();
            getNavigator().showScanAlert();
        });
        ScanAlertdialog = builder.create();
        ScanAlertdialog.show();
    }




    public boolean getDCFEDistance() {
        int meter = LocationHelper.getDistanceBetweenPoint(getDataManager().getCurrentLatitude(), getDataManager().getCurrentLongitude(), getDataManager().getDCLatitude(), getDataManager().getDCLongitude());
        if (meter < getDataManager().getDCRANGE()) {
            getNavigator().showError("Shipment cannot be mark delivered within DC");
            return false;
        } else {
            return true;
        }
    }

    public void getRemarkCount(long awb) {
        try {
            getCompositeDisposable().add(getDataManager().getRemarks(awb).observeOn(getSchedulerProvider().io()).subscribeOn(getSchedulerProvider().io()).subscribe(remark -> {
                forwardCommit.setTrying_reach(String.valueOf(getDataManager().getTryReachingCount(awb + Constants.TRY_RECHING_COUNT)));
                forwardCommit.setTechpark(String.valueOf(getDataManager().getSendSmsCount(awb + Constants.TECH_PARK_COUNT)));
            }, throwable -> throwable.printStackTrace()));
        } catch (Exception e) {
            Logger.e(SignatureViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void onCancelClick() {
        getNavigator().dismissDialog();
    }


    public void onSubmitBPClick() {
        getNavigator().onSubmitBPClick();
    }
}
