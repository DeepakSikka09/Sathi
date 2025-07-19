package in.ecomexpress.sathi.ui.drs.rvp.undelivered;

import static in.ecomexpress.sathi.utils.Constants.DISTANCE_API_KEY;
import static in.ecomexpress.sathi.utils.Constants.TECH_PARK_COUNT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.data.callbridge.CallApiRequest;
import in.ecomexpress.sathi.repo.local.data.commit.PushApi;
import in.ecomexpress.sathi.repo.local.data.rvp.RvpCommit;
import in.ecomexpress.sathi.repo.local.db.model.ImageModel;
import in.ecomexpress.sathi.repo.local.db.model.RVPUndeliveredReasonCodeList;
import in.ecomexpress.sathi.repo.local.db.model.Remark;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.call.Call;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterDataAttributeResponse;
import in.ecomexpress.sathi.repo.remote.model.masterdata.RVPReasonCodeMaster;
import in.ecomexpress.sathi.repo.remote.model.otp.resendotp.GenerateUDOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.otp.verifyotp.VerifyUDOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.voice_otp.VoiceOTP;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.CryptoUtils;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.LocationHelper;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@HiltViewModel
public class RVPUndeliveredViewModel extends BaseViewModel<IRVPUndeliveredNavigator> {

    int isCall = 0;
    List<RVPUndeliveredReasonCodeList> rvpUndeliveredReasonCodeList = new ArrayList<>();
    String stReasonMessage = null;
    Boolean flagIsRescheduled, flagIsCallBridge, flagIsOTPEnabled, flagIsImgEnabled;
    private RvpCommit rvpCommit;
    public ObservableArrayList<String> slotSpinner = new ObservableArrayList<>();
    public ObservableArrayList<String> slotCode = new ObservableArrayList<>();
    public final ObservableField<String> consigneeContactNumber = new ObservableField<>("");
    public final ObservableField<String> awbNo = new ObservableField<>();
    List<RVPUndeliveredReasonCodeList> rvpUndeliveredReasonCodeLists = new ArrayList<>();
    Integer stReasonCode;
    List<ImageModel> mImageModels = new ArrayList<>();
    Map<String, List<RVPReasonCodeMaster>> childGroup;
    public ObservableArrayList<RVPReasonCodeMaster> childRVPReasonCodeMaster = new ObservableArrayList<>();
    public final ObservableArrayList<String> spinnerReasonValues = new ObservableArrayList<>();
    List<String> parentGroupSpinnerValues = new ArrayList<>();
    List<String> reasonMessageSpinnerValues = new ArrayList<>();
    public int call_alert_number = 0;
    public boolean isCallRecursionDialogRunning = true;
    Dialog calldialog = null;
    boolean isStopRecursion = false;
    long request_time = 0L;
    long response_time = 0L;
    ProgressDialog progress;
    int meter;

    @Inject
    public RVPUndeliveredViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);
    }

    public void onChooseReasonSpinner(int pos) {
        getNavigator().onChooseReasonSpinner(rvpUndeliveredReasonCodeList.get(pos));
    }

    public ObservableArrayList<String> getSlotSpinner() {
        slotSpinner.add("Select");
        slotCode.add("-1");
        slotSpinner.add("8 AM - 12 PM");
        slotCode.add("1");
        slotSpinner.add("12 PM - 4 PM");
        slotCode.add("2");
        slotSpinner.add("4 PM - 8 PM");
        slotCode.add("3");
        return slotSpinner;
    }

    public void getDate() {
        getNavigator().captureDate();
    }

    public void onDatePickerClick() {
        getNavigator().onDatePicker();
    }

    public void onCaptureImageClick() {
        getNavigator().onCaptureImage();
    }

    public void onChooseSlotSpinner(int pos) {
        getNavigator().onChooseSlotSpinner(slotCode.get(pos));
    }

    public String loginDate() {
        return getDataManager().getLoginDate();
    }

    public void onRescheduleClick() {
        getNavigator().onRescheduleClick();
    }

    // Upload image on server and save to DB:-
    @SuppressLint("CheckResult")
    public void uploadImageServer(Context context, String imageUri, String imageName, String imageCode, long awbNo, int drsNo) {
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
            RequestBody image_type = RequestBody.create(MultipartBody.FORM, "RVP");
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
                getCompositeDisposable().add(getDataManager().doImageUploadApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), "RVP", headers, map, fileToUpload).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(imageUploadResponse -> {
                    dismissDialog();
                    if (imageUploadResponse != null) {
                        try {
                            if (imageUploadResponse.getStatus().equalsIgnoreCase("Success")) {
                                saveImageDB(imageUri, imageCode, imageName, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_COMPLETE);
                                Observable.fromCallable(() -> {
                                    saveImageResponse(imageName, imageUploadResponse.getImageId());
                                    return false;
                                }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe((result) -> {
                                });
                            } else {
                                getNavigator().showServerError();
                            }
                        } catch (Exception ex) {
                            getNavigator().showServerError();
                        }
                    } else {
                        getNavigator().showServerError();
                    }
                }, throwable -> {
                    dismissDialog();
                    getNavigator().showServerError();
                }));
            } catch (Exception ex) {
                dismissDialog();
                getNavigator().showServerError();
            }
        } catch (Exception ex) {
            dismissDialog();
            getNavigator().showServerError();
        }
    }

    @SuppressLint("CheckResult")
    private void saveImageResponse(String image_name, int image_id) {
        getDataManager().updateImageStatus(image_name, 2);
        getDataManager().updateImageID(image_name, image_id);
    }

    public void callApi(String nyka, boolean failFlag, String awb_number, String drs_id) {
        if (nyka.equalsIgnoreCase("true")) {
            try {
                request_time = System.currentTimeMillis();
                RVPUndeliveredViewModel.this.setIsLoading(true);
                getCompositeDisposable().add(getDataManager().doForwardCallStatusApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), call_alert_number, getDataManager().getEmp_code(), awb_number, drs_id, getDataManager().getShipperId()).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(forwardCallResponse -> {
                    RVPUndeliveredViewModel.this.setIsLoading(false);
                    if (forwardCallResponse.getStatus().equalsIgnoreCase("true")) {
                        getNavigator().undeliverShipment(failFlag, true);
                        if (calldialog != null) {
                            calldialog.dismiss();
                        }
                        getNavigator().showError(forwardCallResponse.getResponse());
                    } else {
                        response_time = System.currentTimeMillis();
                        if (forwardCallResponse.isCall_again_required()) {
                            getNavigator().undeliverShipment(failFlag, false);
                        } else {
                            getNavigator().showError(forwardCallResponse.getResponse());
                            if (getDataManager().isCallStatusAPIRecursion()) {
                                call_alert_number = call_alert_number + 1;
                                long time_difference = response_time - request_time;
                                if (time_difference < getDataManager().getRequestRsponseTime()) {
                                    long difference = getDataManager().getRequestRsponseTime() - time_difference;
                                    final Handler handler = new Handler();
                                    handler.postDelayed(() -> {
                                        callApi(nyka, failFlag, awb_number, drs_id);
                                        if (isCallRecursionDialogRunning) {
                                            getNavigator().getActivityContext().runOnUiThread(() -> showCallAPIDelayDialog(nyka, failFlag, awb_number, drs_id));
                                        }
                                    }, difference);
                                } else {
                                    callApi(nyka, failFlag, awb_number, drs_id);
                                    if (isCallRecursionDialogRunning) {
                                        showCallAPIDelayDialog(nyka, failFlag, awb_number, drs_id);
                                    }
                                }
                            } else {
                                if (isStopRecursion) {
                                    return;
                                }
                                call_alert_number = call_alert_number + 1;
                                showCallAPIDelayDialog(nyka, failFlag, awb_number, drs_id);
                            }
                        }
                    }
                }, throwable -> RVPUndeliveredViewModel.this.setIsLoading(false)));
            } catch (Exception e) {
                getNavigator().showError(e.getLocalizedMessage());
                RVPUndeliveredViewModel.this.setIsLoading(false);
                Logger.e(RVPUndeliveredViewModel.class.getName(), e.getMessage());
            }
        } else {
            getNavigator().undeliverShipment(failFlag, true);
        }
    }

    // For Group Chooser
    public void onChooseGroupSpinner(int pos) {
        try {
            List<RVPReasonCodeMaster> rvpReasonCodeMasters = childGroup.get(parentGroupSpinnerValues.get(pos));
            reasonMessageSpinnerValues.clear();
            childRVPReasonCodeMaster.clear();
            if (rvpReasonCodeMasters != null) {
                childRVPReasonCodeMaster.add(0, getSelectTemplate());
                reasonMessageSpinnerValues.add(Constants.SELECT);
                for (RVPReasonCodeMaster rvpReasonCodeMaster : rvpReasonCodeMasters) {
                    childRVPReasonCodeMaster.add(rvpReasonCodeMaster);
                    reasonMessageSpinnerValues.add(rvpReasonCodeMaster.getReasonMessage());
                }
            }
            getNavigator().onChooseGroupSpinner(parentGroupSpinnerValues.get(pos));
            getNavigator().setReasonMessageSpinnerValues(reasonMessageSpinnerValues);
        } catch (Exception e) {
            Logger.e(RVPUndeliveredViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    // For Child Chooser
    public void onChooseChildSpinner(int pos) {
        getNavigator().onChooseChildSpinner(childRVPReasonCodeMaster.get(pos));
    }

    public void getAllUndeliveredReasonCode(String isSecure) {
        try {
            getCompositeDisposable().add(getDataManager().getAllRVPUndeliveredReasonCode(isSecure).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(rvpReasonCodeMasters -> {
                childGroup = new HashMap<>();
                if (rvpReasonCodeMasters != null) {
                    try {
                        getNavigator().visible(true);
                        childGroup = groupRecord(rvpReasonCodeMasters);
                    } catch (NullPointerException e) {
                        Logger.e(RVPUndeliveredViewModel.class.getName(), e.getMessage());
                        getNavigator().showError(e.getMessage());
                    }
                }
            }));
        } catch (Exception e) {
            Logger.e(RVPUndeliveredViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public Map<String, List<RVPReasonCodeMaster>> groupRecord(List<RVPReasonCodeMaster> rvpReasonCodeMasters) {
        Map<String, List<RVPReasonCodeMaster>> map = new HashMap<>();
        parentGroupSpinnerValues.add(Constants.SELECT);
        for (RVPReasonCodeMaster rvpReasonCodeMaster : rvpReasonCodeMasters) {
            String key = rvpReasonCodeMaster.getSubGroup();
            if (Constants.CONSIGNEE_PROFILE && rvpReasonCodeMaster.getMasterDataAttributeResponse().isPR_NA()) {
                continue;
            } else {
                if (map.containsKey(key)) {
                    List<RVPReasonCodeMaster> list = map.get(key);
                    Objects.requireNonNull(list).add(rvpReasonCodeMaster);
                } else {
                    List<RVPReasonCodeMaster> list = new ArrayList<>();
                    list.add(rvpReasonCodeMaster);
                    parentGroupSpinnerValues.add(key);
                    map.put(key, list);
                }
            }
        }
        getNavigator().setParentGroupSpinnerValues(parentGroupSpinnerValues);
        return map;
    }

    public static RVPReasonCodeMaster getSelectTemplate() {
        RVPReasonCodeMaster reasonSelectReason = new RVPReasonCodeMaster();
        reasonSelectReason.setReasonCode(-1);
        reasonSelectReason.setReasonMessage(Constants.SELECT);
        MasterDataAttributeResponse masterDataAttributeResponse = new MasterDataAttributeResponse();
        masterDataAttributeResponse.setcALLM(false);
        masterDataAttributeResponse.setiMG(false);
        masterDataAttributeResponse.setoTP(false);
        masterDataAttributeResponse.setrCHD(false);
        masterDataAttributeResponse.setSECURED(false);
        reasonSelectReason.setMasterDataAttributeResponse(masterDataAttributeResponse);
        return reasonSelectReason;
    }

    public void setSpinner(List<RVPReasonCodeMaster> rvpReasonCodeMasterList) {
        try {
            for (RVPReasonCodeMaster forwardReasonCodeMasterResponse : rvpReasonCodeMasterList) {
                rvpUndeliveredReasonCodeLists.add(new RVPUndeliveredReasonCodeList(forwardReasonCodeMasterResponse));
                stReasonMessage = forwardReasonCodeMasterResponse.getReasonMessage();
                stReasonCode = forwardReasonCodeMasterResponse.getReasonCode();
                flagIsRescheduled = forwardReasonCodeMasterResponse.getMasterDataAttributeResponse().isrCHD();
                flagIsCallBridge = forwardReasonCodeMasterResponse.getMasterDataAttributeResponse().iscALLM();
                flagIsOTPEnabled = forwardReasonCodeMasterResponse.getMasterDataAttributeResponse().isoTP();
                flagIsImgEnabled = forwardReasonCodeMasterResponse.getMasterDataAttributeResponse().isiMG();
                spinnerReasonValues.add(stReasonMessage);
            }
        } catch (Exception ex) {
            Logger.e(RVPUndeliveredViewModel.class.getName(), ex.getMessage());
            getNavigator().showError(ex.getMessage());
        }
    }

    public void onUndeliveredApiCall(RvpCommit rvpCommit, String composite_key) {
        try {
            rvpCommit.setStatus(Constants.RVPUNDELIVERED);
            rvpCommit.setTrip_id(getDataManager().getTripId());
            rvpCommit.setFeEmpCode(getDataManager().getCode());
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
            if (getDataManager().getConsigneeProfileValue().equalsIgnoreCase("W")) {
                rvpCommit.setFlag_of_warning("W");
            } else {
                rvpCommit.setFlag_of_warning("N");
            }
            saveCommit(rvpCommit, composite_key);
        } catch (Exception ex) {
            Logger.e(RVPUndeliveredViewModel.class.getName(), ex.getMessage());
            getNavigator().showError(ex.getLocalizedMessage());
        }
    }

    public void onUndeliveredApiCallRealTime(RvpCommit rvpCommit, String composite_key) {
        try {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(getDataManager().getImages(rvpCommit.getAwb()).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(imageModels -> {
                mImageModels = imageModels;
                boolean isAllImageSynced = true;
                for (int i = 0; i < mImageModels.size(); i++) {
                    if (mImageModels.get(i).getImageId() <= 0) {
                        isAllImageSynced = false;
                        break;
                    }
                }
                if (isAllImageSynced) {
                    List<RvpCommit.ImageData> list_image_responses = new ArrayList<>();
                    for (int i = 0; i < mImageModels.size(); i++) {
                        RvpCommit.ImageData image_response = new RvpCommit.ImageData();
                        image_response.setImageId(String.valueOf(mImageModels.get(i).getImageId()));
                        image_response.setImageKey(String.valueOf(mImageModels.get(i).getImageName()));
                        list_image_responses.add(image_response);
                    }
                    rvpCommit.setImageData(list_image_responses);
                    rvpCommit.setStatus(Constants.RVPUNDELIVERED);
                    rvpCommit.setTrip_id(getDataManager().getTripId());
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
                    if (getDataManager().getConsigneeProfileValue().equalsIgnoreCase("W")) {
                        rvpCommit.setFlag_of_warning("W");
                    } else {
                        rvpCommit.setFlag_of_warning("N");
                    }
                    rvpCommit.setFeEmpCode(getDataManager().getCode());
                    uploadRvpShipment(rvpCommit, composite_key);
                } else {
                    onUndeliveredApiCall(rvpCommit, composite_key);
                }
            }, throwable -> {
                Logger.e(RVPUndeliveredViewModel.class.getName(), throwable.getMessage());
                getNavigator().showError(throwable.getLocalizedMessage());
            }));
        } catch (Exception ex) {
            Logger.e(RVPUndeliveredViewModel.class.getName(), ex.getMessage());
            getNavigator().showError(ex.getLocalizedMessage());
        }
    }

    @SuppressLint("CheckResult")
    private void uploadRvpShipment(RvpCommit rvpCommit, String composite_key) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        HashMap<String, String> tokens = new HashMap<>();
        tokens.put(Constants.TOKEN, getDataManager().getAuthToken());
        tokens.put(Constants.EMP_CODE, getDataManager().getCode());
        compositeDisposable.add(getDataManager().doRVPUndeliveredCommitApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), tokens, rvpCommit).subscribeOn(getSchedulerProvider().io()).subscribe(rvpCommitResponse -> {
            if (rvpCommitResponse.getStatus()) {
                if (rvpCommit.getAttemptType().equalsIgnoreCase("RQC") && rvpCommit.getDrsId() != null) {
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
                    Logger.e(RVPUndeliveredViewModel.class.getName(), e.getMessage());
                }
                getDataManager().updateRvpStatus(compositeKey, shipment_status).subscribe(aBoolean -> {
                    updateSyncStatusInDRSRVpTable(rvpCommitResponse.getResponse().getDrs_no() + rvpCommitResponse.getResponse().getAwb_no());
                    // Setting call preference after sync:-
                    getDataManager().setCallClicked(rvpCommitResponse.getResponse().getAwb_no() + "RVPCall", true);
                    compositeDisposable.add(getDataManager().deleteSyncedImage(rvpCommitResponse.getResponse().getAwb_no()).subscribe(aBoolean1 -> {

                    }));
                }, throwable -> {
                    saveCommit(rvpCommit, composite_key);
                    setIsLoading(false);
                });
            }
        }, throwable -> {
            Logger.e(RVPUndeliveredViewModel.class.getName(), throwable.getMessage());
            saveCommit(rvpCommit, composite_key);
            setIsLoading(false);
        }));
    }

    private void deleteRVPQCData(int drs, long awbNo) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager().deleteQCData(drs, awbNo).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {

        }, throwable -> {
        }));

    }

    private void updateSyncStatusInDRSRVpTable(String composite_key) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager().updateSyncStatusRVP(composite_key, 2).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(aBoolean -> getNavigator().onSubmitSuccess(), throwable -> {
        }));
    }

    private void updateShipmentStatus(String composite_key) {
        try {
            getCompositeDisposable().add(getDataManager().updateRvpStatus(composite_key, 3).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> getNavigator().onSubmitSuccess()));
        } catch (Exception ex) {
            Logger.e(RVPUndeliveredViewModel.class.getName(), ex.getMessage());
            getNavigator().showError(ex.getMessage());
        }
    }

    public void onRvpDRSCommit(RvpCommit rvpCommit) {
        this.rvpCommit = rvpCommit;
        awbNo.set(rvpCommit.getAwb());
    }

    public void onSubmitClick() {
        getNavigator().OnSubmitClick();
    }

    public void onGenerateOTPClick() {
        getNavigator().onGenerateOtpClick();
    }

    public void onBackClick() {
        getNavigator().onBackClick();
    }

    public void saveImageDB(String imageUri, String imageCode, String name, int image_sync_status) {
        try {
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
            getCompositeDisposable().add(getDataManager().saveImage(imageModel).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> getNavigator().setBitmap()));
        } catch (Exception ex) {
            Logger.e(RVPUndeliveredViewModel.class.getName(), ex.getMessage());
            getNavigator().showError(ex.getLocalizedMessage());
        }
    }

    public void setRVPCommitPacket(RvpCommit rvpCommit) {
        this.rvpCommit = rvpCommit;
    }

    private void saveCommit(RvpCommit rvpCommit, String composite_key) {
        PushApi pushApi = new PushApi();
        pushApi.setAwbNo(Long.parseLong(rvpCommit.getAwb()));
        pushApi.setCompositeKey(composite_key);
        pushApi.setAuthtoken(getDataManager().getAuthToken());
        try {
            pushApi.setRequestData(new ObjectMapper().writeValueAsString(rvpCommit));
            pushApi.setShipmentStatus(0);
            pushApi.setShipmentCaterogy(Constants.RVPCOMMIT);
        } catch (Exception e) {
            Logger.e(RVPUndeliveredViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
        try {
            getCompositeDisposable().add(getDataManager().saveCommitPacket(pushApi).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
                updateShipmentStatus(String.valueOf(pushApi.getCompositeKey()));
                updateRVPCallAttemptedWithZero(String.valueOf(pushApi.getAwbNo()));
                int isCallattempted = getIsCallAttempted(rvpCommit.getAwb());
                if (isCallattempted == 0) {
                    Constants.shipment_undelivered_count++;
                }
            }));
        } catch (Exception ex) {
            Logger.e(RVPUndeliveredViewModel.class.getName(), ex.getMessage());
            getNavigator().showError(ex.getMessage());
        }
    }

    public String getLat() {
        return String.valueOf(in.ecomexpress.geolocations.Constants.latitude);
    }

    public String getLng() {
        return String.valueOf(in.ecomexpress.geolocations.Constants.longitude);
    }

    public int getIsCallAttempted(String awb) {
        try {
            getCompositeDisposable().add(getDataManager().getisRVPCallattempted(Long.parseLong(awb)).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(isCallattempted -> {
                try {
                    isCall = Integer.parseInt(String.valueOf(isCallattempted));
                    getNavigator().isCallAttempted(isCall);
                } catch (Exception e) {
                    Logger.e(RVPUndeliveredViewModel.class.getName(), e.getMessage());
                }
            }));
        } catch (Exception ex) {
            Logger.e(RVPUndeliveredViewModel.class.getName(), ex.getMessage());
            getNavigator().showError(ex.getMessage());
        }
        return isCall;
    }

    public void updateRVPCallAttempted(String awb) {
        try {
            getCompositeDisposable().add(getDataManager().updateRVPCallAttempted(Long.valueOf(awb), Constants.callAttempted).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {
            }));
        } catch (Exception ex) {
            Logger.e(RVPUndeliveredViewModel.class.getName(), ex.getMessage());
            getNavigator().showError(ex.getMessage());
        }
    }

    public void updateRVPCallAttemptedWithZero(String awb) {
        try {
            getCompositeDisposable().add(getDataManager().updateRVPCallAttempted(Long.valueOf(awb), 0).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {
            }));
        } catch (Exception ex) {
            Logger.e(RVPUndeliveredViewModel.class.getName(), ex.getMessage());
            getNavigator().showError(ex.getMessage());
        }
    }

    public void makeCallBridgeApiCall(String callBridgeKey, String awb, String drsid, String type) {
        CallApiRequest request = new CallApiRequest();
        request.setAwb(awb);
        request.setCb_api_key(callBridgeKey);
        request.setDrs_id(String.valueOf(drsid));
        final long timeStamp = Calendar.getInstance().getTimeInMillis();
        writeRestAPIRequst(timeStamp, request);
        try {
            getCompositeDisposable().add(getDataManager().doCallBridgeApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(callApiResponse -> {
                writeRestAPIResponse(timeStamp, callApiResponse);
                if (callApiResponse != null) {
                    try {
                        if (callApiResponse.isStatus()) {
                            if (type.equalsIgnoreCase(Constants.RVP)) {
                                updateRVPCallAttempted(awb);
                            }
                        }
                        getNavigator().onHandleError(callApiResponse);
                    } catch (Exception e) {
                        Logger.e(RVPUndeliveredViewModel.class.getName(), e.getMessage());
                    }
                }
            }, throwable -> {
                getNavigator().showErrorMessage(Objects.requireNonNull(throwable.getMessage()).contains("HTTP 500 "));
                Logger.e(RVPUndeliveredViewModel.class.getName(), throwable.getMessage());
            }));
        } catch (Exception ex) {
            Logger.e(RVPUndeliveredViewModel.class.getName(), ex.getMessage());
            getNavigator().showError(ex.getMessage());
        }
    }

    public void logoutLocal() {
        try {
            getDataManager().setTripId("");
            getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT);
            clearAppData();
        } catch (Exception ex) {
            Logger.e(RVPUndeliveredViewModel.class.getName(), ex.getMessage());
            getNavigator().showError(ex.getMessage());
        }
    }

    private void clearAppData() {
        try {
            getCompositeDisposable().add(getDataManager().deleteAllTables().subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
                try {
                    getDataManager().clearPrefrence();
                    getDataManager().setUserAsLoggedOut();
                } catch (Exception ex) {
                    Logger.e(RVPUndeliveredViewModel.class.getName(), ex.getMessage());
                }
                getNavigator().clearStack();
            }));
        } catch (Exception ex) {
            Logger.e(RVPUndeliveredViewModel.class.getName(), ex.getMessage());
            getNavigator().showError(ex.getMessage());
        }
    }

    public void fetchRVPShipment(String composite_key) {
        try {
            getCompositeDisposable().add(getDataManager().getRVPDRS(composite_key).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(drsReverseQCTypeResponse -> getNavigator().onRVPItemFetched(drsReverseQCTypeResponse), throwable -> {
                writeErrors(Calendar.getInstance().getTimeInMillis(), new Exception(throwable));
                Logger.e(RVPUndeliveredViewModel.class.getName(), throwable.getMessage());
            }));
        } catch (Exception ex) {
            Logger.e(RVPUndeliveredViewModel.class.getName(), ex.getMessage());
            getNavigator().showError(ex.getMessage());
        }
    }

    public void checkMeterRange(DRSReverseQCTypeResponse drsReverseQCTypeResponse) {
        try {
            getProfileLatLng(drsReverseQCTypeResponse);
        } catch (Exception e) {
            Logger.e(RVPUndeliveredViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    private void getProfileLatLng(DRSReverseQCTypeResponse drsForwardTypeResponse) {
        try {
            getCompositeDisposable().add(getDataManager().getProfileLat(drsForwardTypeResponse.getAwbNo()).subscribeOn(getSchedulerProvider().io()).subscribe(profileFound -> {
                try {
                    if (profileFound != null && profileFound.getAwb_number() != 0) {
                        double consigneeLatitude = 0.0;
                        double consigneeLongitude = 0.0;
                        try {
                            consigneeLatitude = profileFound.getDelivery_latitude();
                            consigneeLongitude = profileFound.getDelivery_longitude();
                        } catch (Exception e) {
                            Logger.e(RVPUndeliveredViewModel.class.getName(), e.getMessage());
                        }
                        if (consigneeLatitude == 0.0 || consigneeLongitude == 0.0) {
                            getNavigator().setConsigneeDistance(0);
                            return;
                        }
                             getDistanceBetweenPoint();
                    }
                } catch (Exception e) {
                    Logger.e(RVPUndeliveredViewModel.class.getName(), e.getMessage());
                }
            }, Throwable::printStackTrace));
        } catch (Exception e) {
            Logger.e(RVPUndeliveredViewModel.class.getName(), e.getMessage());
        }
    }

    public void getConsigneeProfiling() {
        boolean enable = getDataManager().getConsigneeProfiling();
        getNavigator().setConsingeeProfiling(enable);
    }

    public boolean FEInDCZone(double latitude, double longitude, double dcLatitude, double dcLongitude) {
        try {
            if (getDataManager().getTripId().equalsIgnoreCase("0")) {
                return false;
            }
            Location locationA = new Location("point A");
            locationA.setLatitude(latitude);
            locationA.setLongitude(longitude);
            Location locationB = new Location("point B");
            locationB.setLatitude(dcLatitude);
            locationB.setLongitude(dcLongitude);
            float distance = locationA.distanceTo(locationB);
            return distance <= getDataManager().getUndeliverConsigneeRANGE();
        } catch (Exception e) {
            Logger.e(RVPUndeliveredViewModel.class.getName(), e.getMessage());
        }
        return false;
    }

    public void getDistanceBetweenPoint() {
        if (getDataManager().getDistanceAPIEnabled()) {
           meter= getCounterDeliveryRange();
            getNavigator().setConsigneeDistance(meter);
        } else {
            final long timeStamp = System.currentTimeMillis();
            try {
                final StringBuilder[] builder = {new StringBuilder()};
                builder[0].append(getDataManager().getCurrentLongitude());
                builder[0].append(",");
                builder[0].append(getDataManager().getCurrentLatitude());
                builder[0].append(";");
                builder[0].append(getDataManager().getDCLongitude());
                builder[0].append(",");
                builder[0].append(getDataManager().getDCLatitude());
                getCompositeDisposable().add(getDataManager().distanceCalculationApis(builder[0].toString(), "distance").doOnSuccess(response -> writeRestAPIResponse(timeStamp, response)
                ).subscribeOn(getSchedulerProvider().ui()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                    meter = (int) Math.round(response.getDistances().get(0).get(1));
                    getNavigator().setConsigneeDistance(meter);
                }, throwable -> {
                }));
            } catch (Exception e) {
                Logger.e("RvpUndeliveredViewModel", String.valueOf(e));
            }
        }
    }


    public void showCallAPIDelayDialog(String nyka, boolean failFlag, String awb_number, String drs_id) {
        isCallRecursionDialogRunning = false;
        getNavigator().getActivityContext().runOnUiThread(() -> {
            AlertDialog.Builder callalertDialog = new AlertDialog.Builder(getNavigator().getActivityContext(), R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert).setMessage("Getting call status...").setPositiveButton("Wait", null);
            calldialog = callalertDialog.create();
            calldialog.setOnShowListener(new DialogInterface.OnShowListener() {
                private final long AUTO_DISMISS_MILLIS = Long.parseLong(getDataManager().getCallStatusApiInterval()) * 1000;

                @Override
                public void onShow(final DialogInterface dialog) {
                    final Button defaultButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    defaultButton.setEnabled(false);
                    final CharSequence negativeButtonText = defaultButton.getText();
                    new CountDownTimer(AUTO_DISMISS_MILLIS, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            isCallRecursionDialogRunning = false;
                            defaultButton.setText(String.format(Locale.getDefault(), "%s (%d)", negativeButtonText, TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1
                            ));
                        }

                        @Override
                        public void onFinish() {
                            if (((AlertDialog) calldialog).isShowing()) {
                                calldialog.dismiss();
                            }
                            if (getDataManager().isCallStatusAPIRecursion()) {
                                getDataManager().setCallAPIRecursion(false);
                                isStopRecursion = true;
                                getNavigator().undeliverShipment(failFlag, false);
                            } else {
                                if (call_alert_number > 1) {
                                    getNavigator().undeliverShipment(failFlag, false);
                                } else {
                                    callApi(nyka, failFlag, awb_number, drs_id);
                                }
                            }
                        }
                    }.start();
                }
            });
            calldialog.setCancelable(false);
            calldialog.show();
        });
    }

    public int getCounterDeliveryRange() {
        double currentLatitude = getDataManager().getCurrentLatitude();
        double currentLongitude = getDataManager().getCurrentLongitude();
        double DcLatitude = getDataManager().getDCLatitude();
        double DcLongitude = getDataManager().getDCLongitude();
        return LocationHelper.getDistanceBetweenPoint(currentLatitude, currentLongitude, DcLatitude, DcLongitude);
    }

    public void getCallStatus(long awb, int drs) {
        try {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(getDataManager().getCallStatus(awb, drs).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> getDataManager().setIsCallAlreadyDone(aBoolean), throwable -> {
            }));
        } catch (Exception e) {
            Logger.e(RVPUndeliveredViewModel.class.getName(), e.getMessage());
        }
    }

    public void saveCallStatus(long awb, int drs) {
        try {
            Call call = new Call();
            call.setDrs(drs);
            call.setAwb(awb);
            call.setStatus(true);
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(getDataManager().saveCallStatus(call).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
            }, throwable -> {
            }));
        } catch (Exception e) {
            Logger.e(RVPUndeliveredViewModel.class.getName(), e.getMessage());
        }
    }

    String ud_otp_commit_status = "NONE";
    public ObservableField<Boolean> ud_otp_verified_status = new ObservableField<>(false);
    public ObservableField<String> ud_otp_commit_status_field = new ObservableField<>("NONE");
    String rd_otp_commit_status = "NONE";
    public ObservableField<String> rd_otp_commit_status_field = new ObservableField<>("NONE");

    public void onVerifyClick() {
        getNavigator().onVerifyClick();
    }

    public void onResendClick() {
        getNavigator().onResendClick();
    }

    @SuppressLint("SetTextI18n")
    public void showCallAndSmsDialog(String consigneealternatemobile, String checkcall) {
        Dialog dialog = new Dialog(getNavigator().getActivityContext(), R.style.RoundedCornersDialog);
        dialog.setContentView(R.layout.dialog_sms_call);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Button sms = dialog.findViewById(R.id.bt_sms);
        Button call = dialog.findViewById(R.id.bt_call);
        ImageView crossDialog = dialog.findViewById(R.id.crssdialog);
        TextView txtCall = dialog.findViewById(R.id.txtcall);

        Button btSmsAlternate = dialog.findViewById(R.id.bt_sms_alternate);

        if (!consigneealternatemobile.isEmpty()) {
            btSmsAlternate.setVisibility(View.VISIBLE);
            btSmsAlternate.setText("SMS on Alternate No. " + consigneealternatemobile);
        }
        if (checkcall.isEmpty()) {
            call.setVisibility(View.GONE);
            txtCall.setVisibility(View.INVISIBLE);
        }
        btSmsAlternate.setOnClickListener(v -> {
            dialog.dismiss();
            getNavigator().resendSms(true);
        });
        crossDialog.setOnClickListener(v -> dialog.dismiss());
        sms.setOnClickListener(v -> {
            dialog.dismiss();
            getNavigator().resendSms(false);
        });
        call.setOnClickListener(v -> {
            dialog.dismiss();
            getNavigator().VoiceCallOtp();
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    ProgressDialog dialog;
    int counter_skip;

    public void onGenerateOtpApiCall(Activity context, String awb, String drsid, Boolean alternateclick, String messagetype, Boolean generateotp, String shipment_type) {
        dialog = new ProgressDialog(context, android.R.style.Theme_Material_Light_Dialog);
        dialog.setMessage("Sending OTP....");
        dialog.show();
        dialog.setIndeterminate(false);
        GenerateUDOtpRequest request = new GenerateUDOtpRequest(awb, messagetype, drsid, alternateclick, getDataManager().getCode(), generateotp, shipment_type);
        final long timeStamp = System.currentTimeMillis();
        writeRestAPIRequst(timeStamp, request);
        try {
            getCompositeDisposable().add(getDataManager().doGenerateUDOtpApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request).doOnSuccess(resendOtpResponse -> {
                writeRestAPIResponse(timeStamp, resendOtpResponse);
                Log.d(ContentValues.TAG, resendOtpResponse.toString());
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                if (dialog.isShowing())
                    dialog.dismiss();
                if (response.getStatus().equals("true")) {
                    counter_skip++;
                    getNavigator().onOtpResendSuccess(response.getStatus(), response.getDescription());
                } else {
                    if (response.getResponse() != null) {
                        if (response.getResponse().getStatusCode().equalsIgnoreCase("107")) {
                            getNavigator().doLogout(response.getResponse().getDescription());
                        }
                    } else if (response.getDescription().matches("Max Attempt Reached")) {
                        counter_skip++;
                        getNavigator().onOtpResendSuccess(response.getStatus(), response.getDescription());
                    }
                    getNavigator().showError(response.getDescription());
                }
            }, throwable -> {
                if (dialog.isShowing())
                    dialog.dismiss();
                String error;
                try {
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                    getNavigator().showError(error);
                } catch (Exception e) {
                    if (dialog.isShowing())
                        dialog.dismiss();
                    Logger.e(RVPUndeliveredViewModel.class.getName(), e.getMessage());
                    getNavigator().showError(e.getMessage());
                }
            }));
        } catch (Exception e) {
            if (dialog.isShowing())
                dialog.dismiss();
            Logger.e(RVPUndeliveredViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void doVoiceOTPApi(String awb, String drs_id, String message_type, String shipment_type) {
        try {
            VoiceOTP voiceOTP = new VoiceOTP();
            voiceOTP.awb = awb;
            voiceOTP.drs_id = drs_id;
            voiceOTP.employee_code = getDataManager().getEmp_code();
            voiceOTP.message_type = message_type;
            voiceOTP.product_type = shipment_type;
            getCompositeDisposable().add(getDataManager().doVoiceOtpApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), voiceOTP).doOnSuccess(verifyOtpResponse -> Log.d(ContentValues.TAG, verifyOtpResponse.toString())).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                setIsLoading(false);
                try {
                    counter_skip++;
                    if (response.code == 0) {
                        getNavigator().showError(response.description);
                        getNavigator().voiceTimer();
                    } else if (response.code == 1) {
                        getNavigator().showError(response.description);
                    }
                } catch (Exception e) {
                    Logger.e(RVPUndeliveredViewModel.class.getName(), e.getMessage());
                }
            }, throwable -> {
                String error;
                try {
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                    getNavigator().showError(error);
                } catch (Exception e) {
                    Logger.e(RVPUndeliveredViewModel.class.getName(), e.getMessage());
                    getNavigator().showError(e.getMessage());
                }
            }));
        } catch (Exception e) {
            Logger.e(RVPUndeliveredViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void onSkip(View view) {
        getNavigator().onSkipClick(view);
    }

    public void setOFDVerified(boolean isVerified) {
        if (isVerified) {
            ud_otp_commit_status_field.set("VERIFIED");
            ud_otp_commit_status = "VERIFIED";
            ud_otp_verified_status.set(true);
        }
    }

    public void onVerifyApiCall(Activity context, String awb, String otp, String drsid, String messagetype) {
        dialog = new ProgressDialog(context, android.R.style.Theme_Material_Light_Dialog);
        dialog.setMessage("Verifying....");
        dialog.show();
        dialog.setIndeterminate(false);
        try {
            VerifyUDOtpRequest request = new VerifyUDOtpRequest(awb, drsid, messagetype, otp);
            final long timeStamp = System.currentTimeMillis();
            writeRestAPIRequst(timeStamp, request);
            getCompositeDisposable().add(getDataManager().doVerifyUDOtpDRSApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request).doOnSuccess(verifyOtpResponse -> {
                writeRestAPIResponse(timeStamp, verifyOtpResponse);
                Log.d(ContentValues.TAG, verifyOtpResponse.toString());
                if (verifyOtpResponse.getStatus().equalsIgnoreCase("true")) {
                    ud_otp_commit_status_field.set("VERIFIED");
                    ud_otp_commit_status = "VERIFIED";
                    ud_otp_verified_status.set(true);
                } else {
                    ud_otp_commit_status = "NONE";
                    ud_otp_commit_status_field.set("NONE");
                    ud_otp_verified_status.set(false);
                }
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                setIsLoading(false);
                if (dialog.isShowing())
                    dialog.dismiss();
                if (response.getStatus().equalsIgnoreCase("true")) {
                    ud_otp_commit_status = "VERIFIED";
                    ud_otp_commit_status_field.set("VERIFIED");
                } else {
                    getNavigator().showError(response.getDescription());
                    ud_otp_commit_status = "NONE";
                    ud_otp_commit_status_field.set("NONE");
                }
            }, throwable -> {
                if (dialog.isShowing())
                    dialog.dismiss();
                String error;
                try {
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                    getNavigator().showError(error);
                } catch (Exception e) {
                    Logger.e(RVPUndeliveredViewModel.class.getName(), e.getMessage());
                    getNavigator().showError(e.getMessage());
                }
            }));
        } catch (Exception e) {
            Logger.e(RVPUndeliveredViewModel.class.getName(), e.getMessage());
            if (dialog.isShowing())
                dialog.dismiss();
            getNavigator().showError(e.getMessage());
        }
    }

    public void getRemarkCount(long awb) {
        try {
            getCompositeDisposable().add(getDataManager().getRemarks(awb).observeOn(getSchedulerProvider().io()).subscribeOn(getSchedulerProvider().io()).subscribe(remark -> {
                rvpCommit.setTrying_reach(String.valueOf(getDataManager().getTryReachingCount(awb + Constants.TRY_RECHING_COUNT)));
                rvpCommit.setTechpark(String.valueOf(getDataManager().getSendSmsCount(awb + TECH_PARK_COUNT)));
            }, Throwable::printStackTrace));
        } catch (Exception e) {
            Logger.e(RVPUndeliveredViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void addRemarks(Remark remark) {
        try {
            getCompositeDisposable().add(getDataManager().insertRemark(remark).observeOn(getSchedulerProvider().io()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {

            }, Throwable::printStackTrace));
        } catch (Exception e) {
            Logger.e(RVPUndeliveredViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void showProgress(Context context) {
        progress = new ProgressDialog(context, android.R.style.Theme_Material_Light_Dialog);
        progress.setIndeterminate(true);
        progress.setMessage("Uploading Image...");
        progress.setCancelable(false);
        progress.show();
    }

    public void dismissDialog() {
        try {
            if (progress != null) {
                progress.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}