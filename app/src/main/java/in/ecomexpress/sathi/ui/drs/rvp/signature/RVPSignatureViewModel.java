package in.ecomexpress.sathi.ui.drs.rvp.signature;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.SystemClock;
import android.widget.AdapterView;

import androidx.databinding.ObservableField;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.data.commit.PushApi;
import in.ecomexpress.sathi.repo.local.data.rvp.RvpCommit;
import in.ecomexpress.sathi.repo.local.db.model.ImageModel;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.CryptoUtils;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.LocationHelper;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.NetworkUtils;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@HiltViewModel
public class RVPSignatureViewModel extends BaseViewModel<IRVPSignatureNavigator> {

    public final ObservableField<String> receiverName = new ObservableField<>();
    private final ObservableField<Boolean> isImageCaptured = new ObservableField<>(false);
    private final ObservableField<Boolean> isImageCaptured_two = new ObservableField<>(false);
    boolean sign_image_required;
    boolean is_qc_fail;
    ObservableField<String> consigneeName = new ObservableField<>("");
    ObservableField<String> awbNo = new ObservableField<>();
    ObservableField<String> pickupAddress = new ObservableField<>();
    String spinnerSelectedItem = "";
    List<ImageModel> mImageModels = new ArrayList<>();
    List<RvpCommit.QcWizard> qcWizards;
    boolean isSubmitClicked = false;
    private long mLastClickTime = 0;
    private RvpCommit rvpCommit;
    ProgressDialog progress;

    @Inject
    public RVPSignatureViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);
    }

    public void onReceiver(AdapterView<?> parent) {
        if (parent.getSelectedItem().toString().equalsIgnoreCase("self")) {
            getNavigator().enableEditText(false);
            receiverName.set(rvpCommit.getConsigneeName());
            getNavigator().hideEdit("self");
        } else if (parent.getSelectedItem().toString().equalsIgnoreCase("Select")) {
            getNavigator().enableEditText(false);
            getNavigator().hideEdit("Select");
            receiverName.set("");
        } else {
            if ((getNavigator().getReceiverName()).equalsIgnoreCase(rvpCommit.getConsigneeName())) {
                getNavigator().enableEditText(true);
                getNavigator().hideEdit("self");
                receiverName.set("");
            } else {
                getNavigator().enableEditText(true);
                getNavigator().hideEdit("self");
            }
        }
        spinnerSelectedItem = parent.getSelectedItem().toString();
        rvpCommit.setReceived_by_relation(spinnerSelectedItem);
        getNavigator().setConsigneeName(parent.getSelectedItem().toString());
    }

    public String getAwbNo() {
        return awbNo.get();
    }

    public void setAwbNo(String awb) {
        awbNo.set(awb);
    }

    public String getConsigneeName() {
        return consigneeName.get();
    }

    public void setConsigneeName(String name) {
        consigneeName.set(name);
    }

    public void onCaptureImageClick() {
        getNavigator().onCaptureImage("Image");
    }

    public void onCaptureImageClickTwo() {
        getNavigator().onCaptureImageTwo("Image2");
    }

    public void setImageCaptured(boolean isImageCaptured) {
        this.isImageCaptured.set(isImageCaptured);
    }

    public void setImageCapturedTwo(boolean isImageCaptured) {
        this.isImageCaptured_two.set(isImageCaptured);
    }

    public void setImageRequired(boolean sign_image_required) {
        this.sign_image_required = sign_image_required;
    }

    public void setIsQcFail(boolean is_qc_fail) {
        this.is_qc_fail = is_qc_fail;
    }

    public void onSubmitClick() {
        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        if (pickupAddress.get() == null) {
            getNavigator().onpickaddressfalse();
            return;
        }
        if (spinnerSelectedItem.equals("Select")) {
            getNavigator().onHandleError("Select Picked From");
            return;
        }
        if (Objects.requireNonNull(receiverName.get()).isEmpty() || Objects.requireNonNull(receiverName.get()).equalsIgnoreCase("null") || Objects.requireNonNull(receiverName.get()).equalsIgnoreCase("Select")) {
            getNavigator().onHandleError("Please Fill Receiver's Name");
            return;
        }
        if (!Objects.requireNonNull(consigneeName.get()).matches("[a-zA-Z.? ]*")) {
            getNavigator().onHandleError("Please Enter A Valid Receiver's Name");
            return;
        }

        if (is_qc_fail) {
            if (sign_image_required && (Boolean.FALSE.equals(isImageCaptured.get()) || Boolean.FALSE.equals(isImageCaptured_two.get()))) {
                getNavigator().onHandleError("Please capture images");
                return;
            }
        } else {
            if (sign_image_required && (Boolean.FALSE.equals(isImageCaptured.get()))) {
                getNavigator().onHandleError("Please capture images");
                return;
            }
        }

        rvpCommit.setOfd_otp_verified(Constants.OFD_OTP_VERIFIED);
        rvpCommit.setOfd_otp_verify_status(getDataManager().getOFDOTPVerifiedStatus());

        if (getDCFEDistance()) {
            getRemarkCount(Long.parseLong(rvpCommit.getAwb()));
            if (getDataManager().getIsSignatureImageMandatory().equalsIgnoreCase("true")) {
                getNavigator().saveSignature();
            } else {
                isSubmitClicked = true;
                getNavigator().saveCommit();
            }
        }
    }

    public String loginDate() {
        return getDataManager().getLoginDate();
    }

    public String getLat() {
        return String.valueOf(getDataManager().getCurrentLatitude());
    }

    public String getLng() {
        return String.valueOf(getDataManager().getCurrentLongitude());
    }

    public void setPickUpAddress(String address) {
        pickupAddress.set(address);
    }

    @SuppressLint("CheckResult")
    public void uploadAWSImage(String imageUri, String imageType, String imageKey) {
        saveImageDB(imageUri, imageType, imageKey, 0);
    }

    public void setRvpCommit(List<RvpCommit.QcWizard> qcWizards, DRSReverseQCTypeResponse drsReverseQCTypeResponse, RvpCommit rvpCommit) {
        this.qcWizards = qcWizards;
        this.rvpCommit = rvpCommit;
        awbNo.set(String.valueOf(drsReverseQCTypeResponse.getAwbNo()));
        this.rvpCommit.setQcWizard(qcWizards);
        this.rvpCommit.setAwb(drsReverseQCTypeResponse.getAwbNo().toString());
        this.rvpCommit.setDrsId(drsReverseQCTypeResponse.getDrs().toString());
        this.rvpCommit.setDrsDate(drsReverseQCTypeResponse.getAssignedDate().toString());
    }

    public void onCreateRVPCommit(String filter, String composite_key) {
        rvpCommit.setFeEmpCode(getDataManager().getCode());
        rvpCommit.setAddressType("" + Objects.requireNonNull(pickupAddress.get()).charAt(0));
        rvpCommit.setReceived_by(receiverName.get());
        rvpCommit.setAttemptType(Constants.RVPCOMMIT);
        rvpCommit.setStart_qc_lat(getDataManager().getStartQCLat());
        rvpCommit.setStart_qc_lng(getDataManager().getStartQCLng());
        rvpCommit.setConsigneeName(getDataManager().getName());
        rvpCommit.setOfd_otp_verify_status(getDataManager().getOFDOTPVerifiedStatus());
        rvpCommit.setCall_attempt_count(getDataManager().getRVPCallCount(rvpCommit.getAwb() + "RVP"));
        rvpCommit.setMap_activity_count(getDataManager().getRVPMapCount(Long.parseLong(rvpCommit.getAwb())));
        mImageModels = getImagesList(rvpCommit.getAwb(), filter, composite_key);
    }

    @SuppressLint("CheckResult")
    private void uploadRvpShipment(RvpCommit rvpCommit, String composite_key) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        HashMap<String, String> tokens = new HashMap<>();
        tokens.put(Constants.TOKEN, getDataManager().getAuthToken());
        tokens.put(Constants.EMP_CODE, getDataManager().getCode());
        compositeDisposable.add(getDataManager()
                .doRVPUndeliveredCommitApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), tokens, rvpCommit).subscribeOn(getSchedulerProvider().io()).
                subscribe(rvpCommitResponse -> {
                    if (rvpCommitResponse.getStatus()) {
                        if(rvpCommit.getAttemptType().equalsIgnoreCase("RQC") && rvpCommit.getDrsId() != null){
                            deleteRVPQCData(Integer.parseInt(rvpCommit.getDrsId()), Long.parseLong(rvpCommit.getAwb()));
                        }
                        int shipment_status;
                        String compositeKey = "";
                        if (rvpCommitResponse.getResponse().getShipment_status().equalsIgnoreCase(Constants.RVPUNDELIVERED)) {
                            shipment_status = Constants.SHIPMENT_UNDELIVERED_STATUS;
                        } else {
                            shipment_status = Constants.SHIPMENT_DELIVERED_STATUS;
                        }
                        try {
                            compositeKey = rvpCommitResponse.getResponse().getDrs_no() + rvpCommitResponse.getResponse().getAwb_no();
                        } catch (Exception e) {
                            Logger.e(RVPSignatureViewModel.class.getName(), e.getMessage());
                        }
                        getDataManager().
                                updateRvpStatus(compositeKey, shipment_status).subscribe(aBoolean -> {
                                    updateSyncStatusInDRSRVpTable(rvpCommitResponse.getResponse().getDrs_no() + rvpCommitResponse.getResponse().getAwb_no());
                                    compositeDisposable.add(getDataManager().deleteSyncedImage(rvpCommitResponse.getResponse().getAwb_no()).subscribe(aBoolean1 -> {
                                    }));
                                }, throwable -> setIsLoading(false));
                    }
                }, throwable -> {
                    setIsLoading(false);
                    Logger.e(RVPSignatureViewModel.class.getName(), throwable.getMessage());
                    saveCommitApi(rvpCommit, composite_key);
                }));
    }
    private void deleteRVPQCData(int drs, long awbNo) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager().deleteQCData(drs,awbNo).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {

        }, throwable -> {
        }));

    }
    private void updateSyncStatusInDRSRVpTable(String composite_key) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager().updateSyncStatusRVP(composite_key, 2)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().io())
                .subscribe(aBoolean -> saveCommitApi(rvpCommit, composite_key), throwable -> {

                }));
    }

    private void updateShipmentStatus(String composite_key, String filter) {
        int val;
        if (filter.equalsIgnoreCase("pass")) {
            val = 2;
        } else {
            val = 3;
        }
        try {
            getCompositeDisposable().add(getDataManager().
                    updateRvpStatus(composite_key.trim(), val).observeOn(getSchedulerProvider().ui()).
                    subscribeOn(getSchedulerProvider().io()).
                    subscribe(aBoolean -> {
                        if (getNavigator() != null)
                            getNavigator().showSuccessStatus();
                        setIsLoading(false);
                    }));
        } catch (Exception e) {
            Logger.e(RVPSignatureViewModel.class.getName(), e.getMessage());
            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
            restApiErrorHandler.writeErrorLogs(0, e.getMessage());
        }

    }

    private void saveCommit(RvpCommit rvpCommit, String filter, String composite_key) {
        PushApi pushApi = new PushApi();
        pushApi.setAwbNo(Long.parseLong(rvpCommit.getAwb()));
        pushApi.setCompositeKey(composite_key.trim());
        pushApi.setAuthtoken(getDataManager().getAuthToken());
        try {
            pushApi.setRequestData(new ObjectMapper().writeValueAsString(rvpCommit));
            pushApi.setShipmentStatus(0);
            pushApi.setShipmentCaterogy(Constants.RVPCOMMIT);
        } catch (JsonProcessingException e) {
            Logger.e(RVPSignatureViewModel.class.getName(), e.getMessage());
        }
        getCompositeDisposable().add(getDataManager().saveCommitPacket(pushApi).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> updateShipmentStatus(String.valueOf(pushApi.getCompositeKey()), filter)));
    }

    private void saveCommitApi(RvpCommit rvpCommit, String composite_key) {
        PushApi pushApi = new PushApi();
        pushApi.setAwbNo(Long.parseLong(rvpCommit.getAwb()));
        pushApi.setCompositeKey(composite_key.trim());
        pushApi.setShipmentStatus(GlobalConstant.CommitStatus.CommitSynced);
        pushApi.setAuthtoken(getDataManager().getAuthToken());
        try {
            pushApi.setRequestData(new ObjectMapper().writeValueAsString(rvpCommit));
            pushApi.setShipmentDeliveryStatus("2");
            pushApi.setShipmentCaterogy(Constants.RVPCOMMIT);
        } catch (JsonProcessingException e) {
            Logger.e(RVPSignatureViewModel.class.getName(), e.getMessage());
        }
        getCompositeDisposable().add(getDataManager().saveCommitPacket(pushApi).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
            if (getNavigator() != null)
                getNavigator().showSuccessStatus();
        }));
    }

    public void onBackClick() {
        getNavigator().onBack();
    }

    public void onClear() {
        getNavigator().onClear();
    }

    public void saveImageDBNew(String imageUri, String imageCode, String name, int image_sync_status, Integer imageId) {
        ImageModel imageModel = new ImageModel();
        imageModel.setDraNo(rvpCommit.getDrsId());
        imageModel.setAwbNo(rvpCommit.getAwb());
        imageModel.setImageName(name);
        imageModel.setImage(imageUri);
        imageModel.setImageCode(imageCode);
        imageModel.setStatus(image_sync_status);
        imageModel.setImageCurrentSyncStatus(image_sync_status);
        imageModel.setImageFutureSyncTime(Calendar.getInstance().getTimeInMillis());
        imageModel.setImageId(imageId);
        imageModel.setImageType(GlobalConstant.ImageTypeConstants.OTHERS);
        imageModel.setDate(Calendar.getInstance().getTimeInMillis());
        imageModel.setShipmentType(GlobalConstant.ShipmentTypeConstants.RVP);
        getCompositeDisposable().add(getDataManager().saveImage(imageModel).
                subscribeOn(getSchedulerProvider().io()).
                observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
                }));
    }

    public void saveImageDB(String imageUri, String imageCode, String name, int image_sync_status) {
        ImageModel imageModel = new ImageModel();
        imageModel.setDraNo(rvpCommit.getDrsId());
        imageModel.setAwbNo(rvpCommit.getAwb());
        imageModel.setImageName(name);
        imageModel.setImage(imageUri);
        imageModel.setImageCode(imageCode);
        imageModel.setStatus(0);
        imageModel.setImageCurrentSyncStatus(image_sync_status);
        imageModel.setImageFutureSyncTime(Calendar.getInstance().getTimeInMillis());
        imageModel.setImageId(-1);
        imageModel.setImageType(GlobalConstant.ImageTypeConstants.OTHERS);
        imageModel.setDate(Calendar.getInstance().getTimeInMillis());
        imageModel.setShipmentType(GlobalConstant.ShipmentTypeConstants.RVP);
        getCompositeDisposable().add(getDataManager().saveImage(imageModel).
                subscribeOn(getSchedulerProvider().io()).
                observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
                    if (NetworkUtils.isNetworkConnected(getNavigator().getContext())) {
                        uploadImageServer(name, imageUri, imageCode, Long.parseLong(rvpCommit.getAwb()), Integer.parseInt(rvpCommit.getDrsId()));
                    } else {
                        getNavigator().saveCommit();
                    }
                }));
    }

    public void fetchRVPShipment(String awbNo) {
        getCompositeDisposable().add(getDataManager().getRVPDRS(String.valueOf(awbNo)).
                subscribeOn(getSchedulerProvider().io()).
                observeOn(getSchedulerProvider().ui()).subscribe(drsReverseQCTypeResponse -> getNavigator().onRVPItemFetched(drsReverseQCTypeResponse), throwable -> {
                    writeErrors(Calendar.getInstance().getTimeInMillis(), new Exception(throwable));
                    Logger.e(RVPSignatureViewModel.class.getName(), throwable.getMessage());
                }));
    }


    public void getImageStatus(String awbNo) {
        long awb_no = Long.parseLong(awbNo);
        getCompositeDisposable().add(getDataManager().getImageStatus(awb_no).
                subscribeOn(getSchedulerProvider().io()).
                observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> getNavigator().setImageStatus(aBoolean), throwable -> {
                    writeErrors(Calendar.getInstance().getTimeInMillis(), new Exception(throwable));
                    Logger.e(RVPSignatureViewModel.class.getName(), throwable.getMessage());
                }));
    }

    public void checkMeterRange(DRSReverseQCTypeResponse drsReverseQCTypeResponse) {
        try {
            getProfileLatLng(drsReverseQCTypeResponse);
        } catch (Exception e) {
            Logger.e(RVPSignatureViewModel.class.getName(), e.getMessage());
        }
    }

    private void getProfileLatLng(DRSReverseQCTypeResponse drsForwardTypeResponse) {
        try {
            getCompositeDisposable().add(getDataManager().
                    getProfileLat(drsForwardTypeResponse.getAwbNo()).
                    subscribeOn(getSchedulerProvider().io()).
                    subscribe(profileFound -> {
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
                                    Logger.e(RVPSignatureViewModel.class.getName(), e.getMessage());
                                }
                                if (consigneeLatitude == 0.0 || consigneeLongitude == 0.0) {
                                    getNavigator().setConsigneeDistance(0);
                                    return;
                                }
                                int meter = LocationHelper.getDistanceBetweenPoint(currentLatitude, currentLongitude, consigneeLatitude, consigneeLongitude);
                                getNavigator().setConsigneeDistance(meter);
                            }
                        } catch (Exception e) {
                            Logger.e(RVPSignatureViewModel.class.getName(), e.getMessage());
                        }
                    }, Throwable::printStackTrace));
        } catch (Exception e) {
            Logger.e(RVPSignatureViewModel.class.getName(), e.getMessage());
        }
    }

    public void getConsigneeProfiling() {
        boolean enable = getDataManager().getConsigneeProfiling();
        getNavigator().setConsingeeProfiling(enable);
    }

    public void uploadImageServer(String imageName, String imageUri, String imageCode, long awbNo, int drsno) {
        setIsLoading(true);
        try {
            final long timeStamp = Calendar.getInstance().getTimeInMillis();
            File image_file = new File(imageUri);
            byte[] bytes = CryptoUtils.decryptFile1(image_file.toString(), Constants.ENC_DEC_KEY);
            RequestBody mFile = RequestBody.create(MediaType.parse("application/octet-stream"), Objects.requireNonNull(bytes));
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
                getCompositeDisposable().add(getDataManager()
                        .doImageUploadApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), "EDS", headers, map, fileToUpload)
                        .doOnSuccess(imageQualityResponse -> {

                        })
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(imageUploadResponse -> {
                            setIsLoading(false);
                            try {
                                if (imageUploadResponse.getStatus().equalsIgnoreCase("Success")) {
                                    saveImageDB(imageUri, imageCode, imageName, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_COMPLETE);
                                    saveImageResponse(imageUploadResponse.getFileName(), imageUploadResponse.getImageId());
                                } else {
                                    saveImageDB(imageUri, imageCode, imageName, 0);
                                }
                            } catch (Exception e) {
                                saveImageDB(imageUri, imageCode, imageName, 0);
                                Logger.e(RVPSignatureViewModel.class.getName(), e.getMessage());
                                setIsLoading(false);
                            }
                        }, throwable -> {
                            String error;
                            setIsLoading(false);
                            try {
                                saveImageDB(imageUri, imageCode, imageName, 0);
                                writeErrors(timeStamp, new Exception(throwable));
                                error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                                getNavigator().onHandleError(error);
                            } catch (Exception e) {
                                Logger.e(RVPSignatureViewModel.class.getName(), e.getMessage());
                            }
                        }));
            } catch (Exception e) {
                setIsLoading(false);
                saveImageDB(imageUri, imageCode, imageName, 0);
                writeErrors(timeStamp, e);
                Logger.e(RVPSignatureViewModel.class.getName(), e.getMessage());
                getNavigator().onHandleError(new RestApiErrorHandler(e.fillInStackTrace()).getErrorDetails().getEResponse().getDescription());
            }
        } catch (Exception e) {
            saveImageDB(imageUri, imageCode, imageName, 0);
            Logger.e(RVPSignatureViewModel.class.getName(), e.getMessage());
        }
    }


    @SuppressLint("CheckResult")
    public void uploadImageServerImage(Context context, String imageName, String imageUri, String imageCode, long awbNo, int drsNo) {
        showProgress(context);
        try {
            File image_file = new File(imageUri);
            byte[] bytes = CryptoUtils.decryptFile1(image_file.toString(), Constants.ENC_DEC_KEY);
            RequestBody mFile = RequestBody.create(MediaType.parse("application/octet-stream"), Objects.requireNonNull(bytes));
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", image_file.getName(), mFile);
            RequestBody awb_no = RequestBody.create(MultipartBody.FORM, String.valueOf(awbNo));
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
                getCompositeDisposable().add(getDataManager().doImageUploadApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), "RVP", headers, map, fileToUpload).
                    subscribeOn(getSchedulerProvider().io()).
                    observeOn(getSchedulerProvider().ui()).subscribe(imageUploadResponse -> {
                        dismissDialog();
                        if (imageUploadResponse != null) {
                            try {
                                if (imageUploadResponse.getStatus().toLowerCase().contains("Success".toLowerCase())) {
                                    getNavigator().setBitmap();
                                    saveImageDBNew(imageUri, imageCode, imageName, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_COMPLETE, imageUploadResponse.getImageId());
                                    Observable.fromCallable(() -> {
                                        saveImageResponse(imageName, imageUploadResponse.getImageId());
                                        return false;
                                    }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe((result) -> {});
                                } else {
                                    getNavigator().showServerError();
                                }
                            } catch (Exception e) {
                                getNavigator().showServerError();
                            }
                        } else {
                            getNavigator().showServerError();
                        }
                    }, throwable -> {
                        dismissDialog();
                        getNavigator().showServerError();
                }));
            } catch (Exception e) {
                dismissDialog();
                getNavigator().showServerError();
            }
        } catch (Exception e) {
            dismissDialog();
            getNavigator().showServerError();
        }
    }

    @SuppressLint("CheckResult")
    private void saveImageResponse(String image_name, int image_id) {
        getDataManager().updateImageStatus(image_name, 2).blockingSingle();
        getDataManager().updateImageID(image_name, image_id).blockingSingle();
    }

    private List<ImageModel> getImagesList(String awbNo, String filter, String composite_key) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager().getImages(awbNo).subscribeOn(getSchedulerProvider().io()).
                observeOn(getSchedulerProvider().io()).subscribe(imageModels -> {
                    boolean isAllImageSynced = true;
                    mImageModels = imageModels;
                    List<RvpCommit.ImageData> list_image_responses = new ArrayList<>();
                    for (int i = 0; i < mImageModels.size(); i++) {
                        if (mImageModels.get(i).getImageId() <= 0) {
                            isAllImageSynced = false;
                        }
                        RvpCommit.ImageData image_response = new RvpCommit.ImageData();
                        image_response.setImageId(String.valueOf(mImageModels.get(i).getImageId()));
                        image_response.setImageKey(String.valueOf(mImageModels.get(i).getImageName()));
                        list_image_responses.add(image_response);
                    }
                    rvpCommit.setImageData(list_image_responses);
                    if (filter.equalsIgnoreCase("pass")) {
                        rvpCommit.setAttemptReasonCode("450");
                        rvpCommit.setStatus(Constants.RVPDELIVERED);
                    } else {
                        rvpCommit.setAttemptReasonCode("426");
                        rvpCommit.setStatus(Constants.UNDELIVERED);
                    }
                    rvpCommit.setDrsCommitDateTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                    rvpCommit.setTrip_id(getDataManager().getTripId());
                    try {
                        if (!String.valueOf(in.ecomexpress.geolocations.Constants.latitude).equalsIgnoreCase("0.0") && !String.valueOf(in.ecomexpress.geolocations.Constants.longitude).equalsIgnoreCase("0.0")) {
                            rvpCommit.setLocationLat(String.valueOf(in.ecomexpress.geolocations.Constants.latitude));
                            rvpCommit.setLocationLong(String.valueOf(in.ecomexpress.geolocations.Constants.longitude));
                        } else if (!Constants.CURRENT_LATITUDE.equalsIgnoreCase("0.0") && !Constants.CURRENT_LONGITUDE.equalsIgnoreCase("0.0")) {
                            rvpCommit.setLocationLat(Constants.CURRENT_LATITUDE);
                            rvpCommit.setLocationLong(Constants.CURRENT_LONGITUDE);
                        } else {
                            rvpCommit.setLocationLat(String.valueOf(getDataManager().getCurrentLatitude()));
                            rvpCommit.setLocationLong(String.valueOf(getDataManager().getCurrentLongitude()));
                        }
                    } catch (Exception e) {
                        Logger.e(RVPSignatureViewModel.class.getName(), e.getMessage());
                    }
                    if (getDataManager().getConsigneeProfileValue().equalsIgnoreCase("W")) {
                        rvpCommit.setFlag_of_warning("W");
                    } else {
                        rvpCommit.setFlag_of_warning("N");
                    }
                    if (NetworkUtils.isNetworkConnected(getNavigator().getContext()) && isAllImageSynced) {
                        uploadRvpShipment(rvpCommit, composite_key);
                    } else {
                        saveCommit(rvpCommit, filter, composite_key);
                    }
                }));
        return mImageModels;
    }

    public int getCounterDeliveryRange() {
        double currentLatitude = getDataManager().getCurrentLatitude();
        double currentLongitude = getDataManager().getCurrentLongitude();
        double DcLatitude = getDataManager().getDCLatitude();
        double DcLongitude = getDataManager().getDCLongitude();
        return LocationHelper.getDistanceBetweenPoint(currentLatitude, currentLongitude, DcLatitude, DcLongitude);
    }


    public boolean getDCFEDistance() {
        int meter = LocationHelper.getDistanceBetweenPoint(getDataManager().getCurrentLatitude(), getDataManager().getCurrentLongitude(), getDataManager().getDCLatitude(), getDataManager().getDCLongitude());
        if (!getDataManager().isCounterDelivery() && meter < getDataManager().getDCRANGE()) {
            getNavigator().onHandleError("Shipment cannot be mark delivered within DC");
            return false;
        } else {
            return true;
        }
    }

    public void getRemarkCount(long awb) {
        try {
            getCompositeDisposable().add(getDataManager().getRemarks(awb).observeOn(getSchedulerProvider().io()).subscribeOn(getSchedulerProvider().io()).subscribe(remark -> {
                rvpCommit.setTrying_reach(String.valueOf(getDataManager().getTryReachingCount(awb + Constants.TRY_RECHING_COUNT)));
                rvpCommit.setTechpark(String.valueOf(getDataManager().getSendSmsCount(awb + Constants.TECH_PARK_COUNT)));
            }, Throwable::printStackTrace));
        } catch (Exception e) {
            Logger.e(RVPSignatureViewModel.class.getName(), e.getMessage());
            getNavigator().onHandleError(e.getMessage());
        }
    }

    public void showProgress(Context context) {
        progress = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
        progress.setIndeterminate(true);
        progress.setMessage("Uploading Image...");
        progress.setCancelable(false);
        progress.show();
    }

    public void dismissDialog(){
        try{
            if (progress != null) {
                progress.dismiss();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}