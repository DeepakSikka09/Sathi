package in.ecomexpress.sathi.ui.side_drawer.pendingHistory;

import static com.paytmmoneyagent.core.utils.CoreUtility.getString;
import static in.ecomexpress.sathi.utils.CommonUtils.logButtonEventInGoogleAnalytics;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.repo.remote.model.rts.ImageResponse;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.inject.Inject;

import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.data.eds.EDSActivityImageRequest;
import in.ecomexpress.sathi.repo.local.data.eds.EDSCommitResponse;
import in.ecomexpress.sathi.repo.local.data.eds.EdsCommit;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.repo.local.data.commit.PushApi;
import in.ecomexpress.sathi.repo.local.data.rts.RTSCommit;
import in.ecomexpress.sathi.repo.local.data.rvp.RvpCommit;
import in.ecomexpress.sathi.repo.local.db.model.ImageModel;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.CryptoUtils;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.NetworkUtils;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@HiltViewModel
public class PendingHistoryDetailViewModel extends BaseViewModel<IPendingHistoryDetailNavigator> {

    @Inject
    public PendingHistoryDetailViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication){
        super(dataManager, schedulerProvider, sathiApplication);
    }

    private static final String TAG = PendingHistoryDetailViewModel.class.getSimpleName();
    ForwardCommit.Image_response imageDataForward = new ForwardCommit.Image_response();
    EDSActivityImageRequest edsActivityImageRequest = new EDSActivityImageRequest();
    RvpCommit.ImageData imageDataRVP = new RvpCommit.ImageData();
    RTSCommit.ImageData imageDataRTS = new RTSCommit.ImageData();
    RvpCommit rvpCommit = null;
    EdsCommit edsCommit = null;
    RTSCommit rtsCommit = null;
    int status = 0;
    @SuppressLint("StaticFieldLeak")
    public Context context;
    public boolean isAllImageSynced = false;
    private Dialog dialog;

    public void getPushApiStatus(long awb_no, String composite_key) {
        try {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(getDataManager().getPushApiDetail(awb_no).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(pushApis -> {
                getNavigator().setDetails(pushApis);
                if (pushApis.getShipmentCaterogy().equalsIgnoreCase("FWD")) {
                    fetchForwardShipment(awb_no);
                } else if (pushApis.getShipmentCaterogy().equalsIgnoreCase("RVP") || pushApis.getShipmentCaterogy().equalsIgnoreCase("RQC")) {
                    fetchRVPShipment(composite_key, awb_no);
                } else if (pushApis.getShipmentCaterogy().equalsIgnoreCase("EDS")) {
                    fetchEDSShipment(composite_key, awb_no);
                } else if (pushApis.getShipmentCaterogy().equalsIgnoreCase("RTS")) {
                    fetchRTSShipment(awb_no);
                }
            }));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void fetchForwardShipment(long awb_no) {
        try {
            getCompositeDisposable().add(getDataManager().getForwardDRSCompositeKey(awb_no).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(drsForwardTypeResponse -> {
                getNavigator().setDRSStatus(drsForwardTypeResponse.getShipmentSyncStatus());
                getShipmentImageStatus(awb_no, false);
            }, throwable -> {
                writeErrors(System.currentTimeMillis(), new Exception(throwable));
                Logger.e(TAG, String.valueOf(throwable));
            }));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void fetchRVPShipment(String composite_key, long awb_no) {
        try {
            getCompositeDisposable().add(getDataManager().getRVPDRS(composite_key).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(drsReverseQCTypeResponse -> {
                getNavigator().setDRSStatus(drsReverseQCTypeResponse.getShipmentSyncStatus());
                getShipmentImageStatus(awb_no, false);
            }, throwable -> {
                writeErrors(Calendar.getInstance().getTimeInMillis(), new Exception(throwable));
                Logger.e(TAG, String.valueOf(throwable));
            }));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void fetchEDSShipment(String compositeKey, long awb_no) {
        getCompositeDisposable().add(getDataManager().getEDS(compositeKey).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(edsResponseNew -> {
            getNavigator().setDRSStatus(edsResponseNew.getShipmentSyncStatus());
            getShipmentImageStatus(awb_no, false);
        }, throwable -> {
            writeErrors(Calendar.getInstance().getTimeInMillis(), new Exception(throwable));
            Logger.e(TAG, String.valueOf(throwable));
        }));
    }

    public void fetchRTSShipment(long awb_no) {
        getCompositeDisposable().add(getDataManager().getVWDetails(awb_no).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(drsReturnToShipperTypeResponse -> {
            getNavigator().setDRSStatus(drsReturnToShipperTypeResponse.getShipmentStatus());
            getShipmentImageStatus(awb_no, false);
        }, throwable -> {
            writeErrors(Calendar.getInstance().getTimeInMillis(), new Exception(throwable));
            Logger.e(TAG, String.valueOf(throwable));
        }));
    }

    private void getShipmentImageStatus(long awb_no, boolean isFromUploadImage) {
        try {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(getDataManager().getShipmentImageStatus(awb_no).subscribeOn(Schedulers.io()).observeOn(getSchedulerProvider().ui()).subscribe(imageModels -> {
                if (imageModels != null && !imageModels.isEmpty()) {
                    for (ImageModel imageModel : imageModels) {
                        if (imageModel.getStatus() == 0 && imageModel.getImageId() < 0) {
                            isAllImageSynced = false;
                            break;
                        } else {
                            isAllImageSynced = true;
                        }
                    }
                } else {
                    isAllImageSynced = true;
                }
                if (isFromUploadImage) {
                    dismissProgressDialog();
                }
                getNavigator().setImagesData(imageModels);
            }, throwable -> dismissProgressDialog()));
        } catch (Exception e) {
            if (isFromUploadImage) {
                dismissProgressDialog();
            }
        }
    }

    @SuppressLint("CheckResult")
    public void uploadDRSImageToServer(ImageModel imageModel) {
        if (!isNetworkConnected()) {
            getNavigator().showError(getString(R.string.check_internet));
            return;
        }
        showProgressDialog(getString(R.string.image_uploading));
        try {
            File file = new File(imageModel.getImage());
            byte[] bytes = CryptoUtils.decryptFile1(file.toString(), Constants.ENC_DEC_KEY);
            if (bytes == null) {
                getNavigator().showError(context.getString(R.string.failed_to_decrypt_file));
            }
            RequestBody mFile = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), mFile);
            RequestBody awb_no;
            if (imageModel.getImageType().equalsIgnoreCase(GlobalConstant.ImageTypeConstants.UD_RTS_IMAGE)) {
                awb_no = RequestBody.create(MediaType.parse("text/plain"), imageModel.getDraNo());
            } else {
                awb_no = RequestBody.create(MediaType.parse("text/plain"), imageModel.getAwbNo());
            }
            RequestBody drs_no = RequestBody.create(MediaType.parse("text/plain"), imageModel.getDraNo());
            RequestBody image_code = RequestBody.create(MediaType.parse("text/plain"), imageModel.getImageCode());
            RequestBody image_name = RequestBody.create(MediaType.parse("text/plain"), file.getName());
            RequestBody image_type = RequestBody.create(MediaType.parse("text/plain"), imageModel.getImageType());
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
                getDataManager().doImageUploadApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), imageModel.getImageType(), headers, map, fileToUpload)
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(imageUploadResponse -> {
                            if (imageUploadResponse == null) {
                                dismissProgressDialog();
                                getNavigator().showError("Image Upload API Response Null");
                            } else {
                                String errorResponse = imageUploadResponse.getDescription() == null ? "Image Upload API Response False" : imageUploadResponse.getDescription();
                                if (imageUploadResponse.getStatus().equalsIgnoreCase("Success")) {
                                    if (imageUploadResponse.getImageId() <= 0) {
                                        dismissProgressDialog();
                                        getNavigator().showError("Getting Image ID Value : " + imageUploadResponse.getImageId());
                                    } else {
                                        saveImageDB(imageModel.getAwbNo(), imageModel.getImageName(), GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_COMPLETE, imageUploadResponse.getImageId());
                                    }
                                } else {
                                    dismissProgressDialog();
                                    getNavigator().showError(errorResponse);
                                }
                            }
                        }, throwable -> {
                            dismissProgressDialog();
                            getNavigator().showError("Image Upload API Failed: " + throwable.getMessage());
                        });
            } catch (Exception e) {
                dismissProgressDialog();
                getNavigator().showError("Image Upload API Failed " + e.getMessage());
            }
        } catch (Exception e) {
            dismissProgressDialog();
            getNavigator().showError("Image Upload API Failed " + e.getMessage());
        }
    }

    public boolean isNetworkConnected() {
        return NetworkUtils.isNetworkConnected(context);
    }

    public void saveImageDB(String awb_no, String image_name, int image_sync_status, int image_id) {
        getCompositeDisposable().add(getDataManager().updateImageID(image_name, image_id).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
            try {
                updateImageStatus(awb_no, image_name, image_sync_status);
            } catch (Exception e) {
                dismissProgressDialog();
            }
        }, throwable -> dismissProgressDialog()));
    }

    public void updateImageStatus(String awb_no, String image_name, int image_sync_status) {
        getCompositeDisposable().add(getDataManager().updateImageStatus(image_name, image_sync_status).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
            try {
                getShipmentImageStatus(Long.parseLong(awb_no), true);
            } catch (Exception e) {
                dismissProgressDialog();
            }
        }, throwable -> dismissProgressDialog()));
    }

    public void onShipmentCommitClick(PushApi pushApi) {
        showProgressDialog(context.getString(R.string.uploading_commit_data));
        logButtonEventInGoogleAnalytics(TAG, context.getString(R.string.pendingshipmentcommitclick), "Emp Code " + getDataManager().getCode() + " Awb No " + pushApi.getAwbNo(), this.getApplication());
        Constants.UPLOADEDS_CALLED = true;
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        try {
            compositeDisposable.add(getDataManager().getUnsynced(pushApi.getAwbNo()).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(imageModel -> {
                Constants.UPLOADEDS_CALLED = false;
                StringBuilder builder = new StringBuilder();
                switch (pushApi.getShipmentCaterogy()) {
                    case GlobalConstant.ShipmentTypeConstants.FWD:
                        List<ForwardCommit.Image_response> image_responses = new ArrayList<>();
                        if (pushApi.getShipmentStatus() == 0 || pushApi.getShipmentStatus() == 3 || pushApi.getShipmentStatus() == 4) {
                            for (ImageModel imageModel1 : imageModel) {
                                imageDataForward = new ForwardCommit.Image_response();
                                imageDataForward.setImage_id(String.valueOf(imageModel1.getImageId()));
                                imageDataForward.setImage_key(trimString(imageModel1.getImageName()));
                                builder.append("{");
                                builder.append(imageModel1.getImageName());
                                builder.append("}");
                                image_responses.add(imageDataForward);
                            }
                            if (!Constants.UPLOADEDS_CALLED) {
                                uploadForwardShipment(pushApi, image_responses);
                            }
                        } else {
                            getNavigator().showError("Shipment Already Committed");
                        }
                        break;
                    case GlobalConstant.ShipmentTypeConstants.RTS:
                        List<RTSCommit.ImageData> imageDataRTSList = new ArrayList<>();
                        if (pushApi.getShipmentStatus() == 0 || pushApi.getShipmentStatus() == 3 || pushApi.getShipmentStatus() == 4) {
                            for (ImageModel imageModel1 : imageModel) {
                                imageDataRTS = new RTSCommit.ImageData();
                                imageDataRTS.setImageId(String.valueOf(imageModel1.getImageId()));
                                imageDataRTS.setImageKey(trimString(imageModel1.getImageName()));
                                imageDataRTS.setImageType(imageModel1.getImageType());
                                builder.append("{");
                                builder.append(imageModel1.getImageName());
                                builder.append("}");
                                imageDataRTSList.add(imageDataRTS);
                            }
                            if (!Constants.UPLOADEDS_CALLED) {
                                UploadRTSShipment(pushApi, imageDataRTSList, imageModel);
                            }
                        } else {
                            getNavigator().showError("Shipment Already Committed");
                        }
                        break;
                    case GlobalConstant.ShipmentTypeConstants.RQC:
                    case GlobalConstant.ShipmentTypeConstants.RVP:
                        List<RvpCommit.ImageData> imageData = new ArrayList<>();
                        if (pushApi.getShipmentStatus() == 0 || pushApi.getShipmentStatus() == 3 || pushApi.getShipmentStatus() == 4) {
                            for (ImageModel imageModel1 : imageModel) {
                                imageDataRVP = new RvpCommit.ImageData();
                                imageDataRVP.setImageId(String.valueOf(imageModel1.getImageId()));
                                imageDataRVP.setImageKey(trimString(imageModel1.getImageName()));
                                builder.append("{");
                                builder.append(imageModel1.getImageName());
                                builder.append("}");
                                imageData.add(imageDataRVP);
                            }
                            if (!Constants.UPLOADEDS_CALLED) {
                                uploadRvpShipment(pushApi, imageData);
                            }
                        } else {
                            getNavigator().showError("Shipment Already Committed");
                        }
                        break;
                    case GlobalConstant.ShipmentTypeConstants.EDS:
                        try {
                            List<EDSActivityImageRequest> edsActivityImageRequests = new ArrayList<>();
                            if (pushApi.getShipmentStatus() == 0 || pushApi.getShipmentStatus() == 3 || pushApi.getShipmentStatus() == 4) {
                                for (ImageModel imageModel1 : imageModel) {
                                    edsActivityImageRequest = new EDSActivityImageRequest();
                                    edsActivityImageRequest.setImageId(String.valueOf(imageModel1.getImageId()));
                                    edsActivityImageRequest.setImageKey(trimString(imageModel1.getImageName()));
                                    builder.append("{");
                                    builder.append(imageModel1.getImageName());
                                    builder.append("}");
                                    edsActivityImageRequests.add(edsActivityImageRequest);
                                }
                                if (!Constants.UPLOADEDS_CALLED) {
                                    UploadEdsShipment(pushApi, edsActivityImageRequests);
                                }
                            } else {
                                getNavigator().showError(context.getString(R.string.shipment_already_committed));
                            }
                        } catch (Exception e) {
                            Logger.e(TAG, String.valueOf(e));
                        }
                        break;
                }
            }));
        } catch (Exception e) {
            Constants.UPLOADEDS_CALLED = false;
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @SuppressLint("CheckResult")
    private void uploadForwardShipment(PushApi pushApi, List<ForwardCommit.Image_response> image_response) {
        Constants.UPLOADEDS_CALLED = true;
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        HashMap<String, String> tokens = new HashMap<>();
        tokens.put(Constants.TOKEN, getDataManager().getAuthToken());
        tokens.put(Constants.EMP_CODE, getDataManager().getCode());
        ForwardCommit[] forwardCommit;
        List<ForwardCommit> fwdCommit = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            forwardCommit = mapper.readValue(pushApi.getRequestData(), ForwardCommit[].class);
            for (ForwardCommit commit : forwardCommit) {
                commit.setImage_response(image_response);
                commit.setCall_attempt_count(getDataManager().getForwardCallCount(pushApi.getAwbNo() + "FWD"));
                commit.setMap_activity_count(getDataManager().getForwardMapCount(pushApi.getAwbNo()));
                if (commit.getStatus().equalsIgnoreCase("2")) {
                    status = Constants.SHIPMENT_DELIVERED_STATUS;
                    commit.setStatus(Constants.UNDELIVERED);
                } else {
                    status = Constants.SHIPMENT_UNDELIVERED_STATUS;
                    commit.setStatus(Constants.DELIVERED);
                }
                try {
                    if (Constants.CURRENT_LATITUDE != null && Constants.CURRENT_LONGITUDE != null) {
                        commit.setLocation_lat(Constants.CURRENT_LATITUDE);
                        commit.setLocation_long(Constants.CURRENT_LONGITUDE);
                    } else {
                        commit.setLocation_lat(String.valueOf(in.ecomexpress.geolocations.Constants.latitude));
                        commit.setLocation_long(String.valueOf(in.ecomexpress.geolocations.Constants.longitude));
                    }
                } catch (Exception e) {
                    Logger.e(TAG + "uploadForwardShipment", String.valueOf(e));
                }
                fwdCommit.add(commit);
            }
            getUDORRCHEDShipmentStatus(forwardCommit);
        } catch (Exception e) {
            Logger.e(TAG + "uploadForwardShipment", String.valueOf(e));
        }
        try {
            compositeDisposable.add(getDataManager().doFWDCommitApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), tokens, fwdCommit).subscribeOn(getSchedulerProvider().io()).subscribe(forwardCommitResponse -> {
                Constants.UPLOADEDS_CALLED = false;
                if (forwardCommitResponse == null) {
                    getNavigator().showError(context.getString(R.string.fwd_commit_api_response_null));
                } else if (forwardCommitResponse.getStatus()) {
                    int shipment_status = forwardCommitResponse.getResponse().getShipment_status().equalsIgnoreCase(Constants.UNDELIVERED) ? Constants.SHIPMENT_UNDELIVERED_STATUS : Constants.SHIPMENT_DELIVERED_STATUS;
                    String compositeKey;
                    try {
                        compositeKey = forwardCommitResponse.getResponse().getDrs_no() + forwardCommitResponse.getResponse().getAwb_no().trim();
                    } catch (Exception e) {
                        compositeKey = pushApi.CompositeKey;
                    }
                    String finalCompositeKey = compositeKey;
                    getDataManager().updateForwardStatus(compositeKey, shipment_status).subscribe(aBoolean -> {
                        compositeDisposable.add(getDataManager().updateSyncStatusFWD(finalCompositeKey, GlobalConstant.CommitStatus.CommitSynced).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(responseBoolean -> {
                        }));
                        compositeDisposable.add(getDataManager().deleteSyncedImage(forwardCommitResponse.getResponse().getAwb_no()).subscribe(responseBoolean -> {
                        }));
                        compositeDisposable.add(getDataManager().deleteSyncedFWD(Long.parseLong(forwardCommitResponse.getResponse().getAwb_no())).subscribe(responseBoolean -> {
                        }));
                        getDataManager().setCallClicked(forwardCommitResponse.getResponse().getAwb_no() + "ForwardCall", true);
                        dismissProgressDialog();
                        getNavigator().setSuccessName();
                    }, throwable -> {
                        dismissProgressDialog();
                        getNavigator().showError("Re-Upload The Committed Shipment");
                        saveCommit(fwdCommit, pushApi.getAwbNo(), String.valueOf(pushApi.CompositeKey));
                    });
                } else if ((forwardCommitResponse.getResponse().getCode().equalsIgnoreCase("E107")) || (forwardCommitResponse.getResponse().getCode().equalsIgnoreCase("107"))) {
                    dismissProgressDialog();
                    LocalLogout();
                } else {
                    dismissProgressDialog();
                    getNavigator().showError(forwardCommitResponse.getResponse().getDescription() == null ? "FWD Commit Api Response False" : forwardCommitResponse.getResponse().getDescription());
                }
            }, throwable -> {
                dismissProgressDialog();
                getNavigator().showError(throwable.getMessage());
                saveCommit(fwdCommit, pushApi.getAwbNo(), String.valueOf(pushApi.CompositeKey));
            }));
        } catch (Exception e) {
            dismissProgressDialog();
            Constants.UPLOADEDS_CALLED = false;
            getNavigator().showError(e.getMessage());
            saveCommit(fwdCommit, pushApi.getAwbNo(), String.valueOf(pushApi.CompositeKey));
        }
    }

    private void getUDORRCHEDShipmentStatus(ForwardCommit[] forwardCommit) {
        if (forwardCommit[0].getUd_otp().equalsIgnoreCase("VERIFIED") || forwardCommit[0].getRd_otp().equalsIgnoreCase("VERIFIED")) {
            getDataManager().setFWD_UD_RD_OTPVerfied(forwardCommit[0].getAwb() + "Forward", true);
        }
    }

    public String trimString(String fileName) {
        if (fileName.indexOf(".") > 0) {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;
    }

    private void saveCommit(List<ForwardCommit> forwardCommit, long parentAWB, String composite_key) {
        PushApi pushApi = new PushApi();
        pushApi.setAwbNo(parentAWB);
        pushApi.setCompositeKey(composite_key);
        pushApi.setAuthtoken(getDataManager().getAuthToken());
        try {
            ObjectMapper mapper = new ObjectMapper();
            pushApi.setRequestData(mapper.writeValueAsString(forwardCommit));
            pushApi.setShipmentStatus(Constants.SHIPMENT_ASSIGNED_STATUS);
            pushApi.setShipmentDeliveryStatus("2");
            pushApi.setShipmentCaterogy(GlobalConstant.ShipmentTypeConstants.FWD);
            getCompositeDisposable().add(getDataManager().saveCommitPacket(pushApi).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
            }));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @SuppressLint("CheckResult")
    private void uploadRvpShipment(PushApi pushApi, List<RvpCommit.ImageData> imageData) {
        Constants.UPLOADEDS_CALLED = true;
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        HashMap<String, String> tokens = new HashMap<>();
        tokens.put(Constants.TOKEN, getDataManager().getAuthToken());
        tokens.put(Constants.EMP_CODE, getDataManager().getCode());
        try {
            rvpCommit = new ObjectMapper().readValue(pushApi.getRequestData(), RvpCommit.class);
            rvpCommit.setImageData(imageData);
            rvpCommit.setAttemptType(Constants.RVPCOMMIT);
            rvpCommit.setCall_attempt_count(getDataManager().getRVPCallCount(pushApi.getAwbNo() + "RVP"));
            rvpCommit.setMap_activity_count(getDataManager().getRVPMapCount(pushApi.getAwbNo()));
            if (rvpCommit.getStatus().equalsIgnoreCase(Constants.RVPUNDELIVERED) || rvpCommit.getStatus().equalsIgnoreCase("2")) {
                rvpCommit.setStatus(Constants.RVPUNDELIVERED);
                status = Constants.SHIPMENT_UNDELIVERED_STATUS;
            } else {
                rvpCommit.setStatus(Constants.RVPDELIVERED);
                status = Constants.SHIPMENT_DELIVERED_STATUS;
            }
            try {
                if (Constants.CURRENT_LATITUDE != null && Constants.CURRENT_LONGITUDE != null) {
                    rvpCommit.setLocationLat(Constants.CURRENT_LATITUDE);
                    rvpCommit.setLocationLong(Constants.CURRENT_LONGITUDE);
                } else {
                    rvpCommit.setLocationLat(String.valueOf(in.ecomexpress.geolocations.Constants.latitude));
                    rvpCommit.setLocationLong(String.valueOf(in.ecomexpress.geolocations.Constants.longitude));
                }
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        compositeDisposable.add(getDataManager().doRVPUndeliveredCommitApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), tokens, rvpCommit).subscribeOn(getSchedulerProvider().io()).subscribe(rvpCommitResponse -> {
            try {
                Constants.UPLOADEDS_CALLED = false;
                if (rvpCommitResponse == null) {
                    getNavigator().showError("RVP Commit Api Response Null");
                } else if (rvpCommitResponse.getStatus()) {
                    if (pushApi.getShipmentCaterogy().equalsIgnoreCase("RQC") && rvpCommit.getDrsId() != null) {
                        deleteRVPQCData(Integer.parseInt(rvpCommit.getDrsId()), Long.parseLong(rvpCommit.getAwb()));
                    }
                    int shipment_status;
                    String compositeKey;
                    if (rvpCommitResponse.getResponse().getShipment_status().equalsIgnoreCase(Constants.RVPUNDELIVERED)) {
                        shipment_status = Constants.SHIPMENT_UNDELIVERED_STATUS;
                    } else {
                        shipment_status = Constants.SHIPMENT_DELIVERED_STATUS;
                    }
                    try {
                        compositeKey = rvpCommitResponse.getResponse().getDrs_no() + rvpCommitResponse.getResponse().getAwb_no();
                    } catch (Exception e) {
                        compositeKey = pushApi.getCompositeKey();
                    }
                    String finalCompositeKey = compositeKey;
                    getDataManager().updateRvpStatus(compositeKey, shipment_status).subscribe(aBoolean -> {
                        pushApi.setShipmentStatus(GlobalConstant.CommitStatus.CommitSynced);
                        updateSyncStatusInDRSRVpTable(finalCompositeKey);
                        compositeDisposable.add(getDataManager().deleteSyncedImage(rvpCommitResponse.getResponse().getAwb_no()).subscribe(aBoolean1 -> {
                        }));
                        compositeDisposable.add(getDataManager().deleteSyncedFWD(Long.parseLong(rvpCommitResponse.getResponse().getAwb_no())).subscribe(aBoolean2 -> {
                        }));
                        compositeDisposable.add(getDataManager().saveCommitPacket(pushApi).subscribe(aBoolean13 -> {
                        }));
                        getDataManager().setCallClicked(rvpCommitResponse.getResponse().getAwb_no() + "RVPCall", true);
                        getNavigator().setSuccessName();
                        dismissProgressDialog();
                    }, throwable -> {
                        dismissProgressDialog();
                        getNavigator().showError("Re-Upload The Committed Shipment");
                        saveCommit(rvpCommit, String.valueOf(pushApi.CompositeKey));
                    });
                } else if ((rvpCommitResponse.getResponse().getCode().equalsIgnoreCase("E107")) || (rvpCommitResponse.getResponse().getCode().equalsIgnoreCase("107"))) {
                    dismissProgressDialog();
                    LocalLogout();
                } else {
                    dismissProgressDialog();
                    getNavigator().showError(rvpCommitResponse.getResponse().getDescription() == null ? "RVP Commit Api Response False" : rvpCommitResponse.getResponse().getDescription());
                }
            } catch (Exception e) {
                dismissProgressDialog();
                getNavigator().showError(e.getMessage());
                saveCommit(rvpCommit, String.valueOf(pushApi.CompositeKey));
            }
        }, throwable -> {
            Constants.UPLOADEDS_CALLED = false;
            dismissProgressDialog();
            getNavigator().showError(throwable.getMessage());
            saveCommit(rvpCommit, String.valueOf(pushApi.CompositeKey));
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
        compositeDisposable.add(getDataManager().updateSyncStatusRVP(composite_key, 2).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {
        }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
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
        } catch (JsonProcessingException e) {
            Logger.e(TAG, String.valueOf(e));
        }
        getCompositeDisposable().add(getDataManager().saveCommitPacket(pushApi).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
        }));
    }

    @SuppressLint("CheckResult")
    private void UploadEdsShipment(PushApi pushApi, List<EDSActivityImageRequest> edsActivityImageRequests) {
        Constants.UPLOADEDS_CALLED = true;
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        HashMap<String, String> tokens = new HashMap<>();
        tokens.put(Constants.TOKEN, getDataManager().getAuthToken());
        tokens.put(Constants.EMP_CODE, getDataManager().getCode());
        try {
            edsCommit = new ObjectMapper().readValue(pushApi.getRequestData(), EdsCommit.class);
            edsCommit.setEdsActivityImageRequests(edsActivityImageRequests);
            edsCommit.setCall_attempt_count(getDataManager().getEDSCallCount(pushApi.getAwbNo() + "EDS"));
            edsCommit.setMap_activity_count(getDataManager().getEDSMapCount(pushApi.getAwbNo()));
            if (edsCommit.getStatus().equalsIgnoreCase(Constants.EDSUNDELIVERED) || edsCommit.getStatus().equalsIgnoreCase("2")) {
                edsCommit.setStatus(Constants.EDSUNDELIVERED);
                status = 3;
            } else {
                edsCommit.setStatus(Constants.EDSDELIVERED);
                status = 2;
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        try {
            if (Constants.CURRENT_LATITUDE != null && Constants.CURRENT_LONGITUDE != null) {
                edsCommit.setLocationLat(Constants.CURRENT_LATITUDE);
                edsCommit.setLocationLong(Constants.CURRENT_LONGITUDE);
            } else {
                edsCommit.setLocationLat(String.valueOf(in.ecomexpress.geolocations.Constants.latitude));
                edsCommit.setLocationLong(String.valueOf(in.ecomexpress.geolocations.Constants.longitude));
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        try {
            compositeDisposable.add(getDataManager().doEDSCommitApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), tokens, edsCommit).subscribeOn(getSchedulerProvider().io()).subscribe((EDSCommitResponse edsCommitResponse) -> {
                Constants.UPLOADEDS_CALLED = false;
                if (edsCommitResponse == null) {
                    getNavigator().showError("EDS Commit Api Response Null");
                } else if (edsCommitResponse.getStatus()) {
                    int shipment_status;
                    String compositeKey;
                    if (edsCommitResponse.getResponse().getShipment_status().equalsIgnoreCase(Constants.EDSUNDELIVERED)) {
                        shipment_status = 3;
                    } else {
                        shipment_status = 2;
                    }
                    try {
                        compositeKey = edsCommitResponse.getResponse().getDrs_no() + edsCommitResponse.getResponse().getAwb_no();
                    } catch (Exception e) {
                        compositeKey = pushApi.getCompositeKey();
                    }
                    getDataManager().updateEdsStatus(compositeKey, shipment_status).subscribe(aBoolean -> {
                        if (aBoolean) {
                            pushApi.setShipmentStatus(GlobalConstant.CommitStatus.CommitSynced);
                            updateSyncStatusInDRSEDSTable(edsCommitResponse.getResponse().getDrs_no() + edsCommitResponse.getResponse().getAwb_no());
                            compositeDisposable.add(getDataManager().deleteSyncedImage((edsCommitResponse.getResponse().getAwb_no())).subscribe(aBoolean1 -> {
                            }));
                            compositeDisposable.add(getDataManager().deleteSyncedFWD(Long.parseLong(edsCommitResponse.getResponse().getAwb_no())).subscribe(aBoolean2 -> {
                            }));
                            compositeDisposable.add(getDataManager().saveCommitPacket(pushApi).subscribe(aBoolean3 -> {
                            }));
                            getDataManager().setCallClicked(edsCommitResponse.getResponse().getAwb_no() + "EDSCall", true);
                            dismissProgressDialog();
                            getNavigator().setSuccessName();
                        }
                    }, throwable -> {
                        dismissProgressDialog();
                        getNavigator().showError("Re-Upload The Committed Shipment");
                        saveCommit(edsCommit);
                    });
                } else if ((edsCommitResponse.getResponse().getCode().equalsIgnoreCase("E107")) || (edsCommitResponse.getResponse().getCode().equalsIgnoreCase("107"))) {
                    dismissProgressDialog();
                    LocalLogout();
                } else {
                    dismissProgressDialog();
                    getNavigator().showError(edsCommitResponse.getResponse().getDescription() == null ? "RTS Commit Api Response False" : edsCommitResponse.getResponse().getDescription());
                }
            }, throwable -> {
                dismissProgressDialog();
                saveCommit(edsCommit);
                getNavigator().showError(throwable.getMessage());
            }));
        } catch (Exception e) {
            dismissProgressDialog();
            Constants.UPLOADEDS_CALLED = false;
            saveCommit(edsCommit);
            getNavigator().showError(e.getMessage());
        }
    }

    private void updateSyncStatusInDRSEDSTable(String compositeKey) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager().updateSyncStatusEDS(compositeKey, 2).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {
        }, throwable -> {
        }));
    }

    private void saveCommit(EdsCommit edsCommit) {
        PushApi pushApi = new PushApi();
        pushApi.setCompositeKey(pushApi.getCompositeKey());
        pushApi.setAwbNo(Long.parseLong(edsCommit.getAwb()));
        pushApi.setAuthtoken(getDataManager().getAuthToken());
        try {
            pushApi.setRequestData(new ObjectMapper().writeValueAsString(edsCommit));
            pushApi.setShipmentStatus(0);
            pushApi.setShipmentCaterogy(GlobalConstant.ShipmentTypeConstants.EDS);
        } catch (JsonProcessingException e) {
            Logger.e(TAG, String.valueOf(e));
        }
        try {
            getCompositeDisposable().add(getDataManager().saveCommitPacket(pushApi).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
            }));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @SuppressLint("CheckResult")
    private void UploadRTSShipment(PushApi pushApi, List<RTSCommit.ImageData> imageData, List<ImageModel> imageModel) {
        Constants.UPLOADEDS_CALLED = true;
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        HashMap<String, String> tokens = new HashMap<>();
        tokens.put(Constants.TOKEN, getDataManager().getAuthToken());
        tokens.put(Constants.EMP_CODE, getDataManager().getCode());
        try {
            rtsCommit = new ObjectMapper().readValue(pushApi.getRequestData(), RTSCommit.class);
            for (int i = 0; i < rtsCommit.getShipments().size(); i++) {
                ArrayList<ImageResponse> imageResponseList = new ArrayList<>();
                for (int j = 0; j < imageModel.size(); j++) {
                    if (imageData.get(j).getImageType().equalsIgnoreCase(GlobalConstant.ImageTypeConstants.UD_RTS_IMAGE)) {
                        if (rtsCommit.getShipments().get(i).getAwbNo().equals(imageModel.get(j).getDraNo())) {
                            ImageResponse imageResponse = new ImageResponse();
                            imageResponse.setImageId(Integer.parseInt(imageData.get(j).getImageId()));
                            imageResponse.setImageKey(imageModel.get(j).getImageName());
                            imageResponseList.add(imageResponse);
                            rtsCommit.getShipments().get(i).setRts_shipment_images(imageResponseList);
                        }
                    }
                }
            }
            List<RTSCommit.ImageData> imageDataPOD = getImageData(imageData);
            rtsCommit.setImageData(imageDataPOD);
            rtsCommit.setCall_attempt_count(getDataManager().getRTSCallCount(rtsCommit.getConsigneeId() + "RTS"));
            rtsCommit.setMap_activity_count(getDataManager().getRTSMapCount(Long.parseLong(rtsCommit.getConsigneeId())));
            try {
                if (Constants.CURRENT_LATITUDE != null && Constants.CURRENT_LONGITUDE != null) {
                    rtsCommit.setLatitude(Constants.CURRENT_LATITUDE);
                    rtsCommit.setLongitude(Constants.CURRENT_LONGITUDE);
                } else {
                    rtsCommit.setLatitude(String.valueOf(in.ecomexpress.geolocations.Constants.latitude));
                    rtsCommit.setLongitude(String.valueOf(in.ecomexpress.geolocations.Constants.longitude));
                }
            } catch (Exception e) {
                Logger.e(TAG + "uploadRTSShipment", String.valueOf(e));
            }
        } catch (Exception e) {
            Logger.e(TAG + "uploadRTSShipment", String.valueOf(e));
        }
        try {
            compositeDisposable.add(getDataManager().doRTSCommitApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), tokens, rtsCommit).subscribeOn(getSchedulerProvider().io()).subscribe(rtsCommitResponse -> {
                Constants.UPLOADEDS_CALLED = false;
                try {
                    if (rtsCommitResponse == null) {
                        getNavigator().showError("RTS Commit Api Response Null");
                    } else {
                        if (rtsCommitResponse.getStatus()) {
                            int shipmentStatus;
                            if (pushApi.getShipmentDeliveryStatus().equalsIgnoreCase("3")) {
                                shipmentStatus = Constants.SHIPMENT_UNDELIVERED_STATUS;
                            } else {
                                shipmentStatus = Constants.SHIPMENT_DELIVERED_STATUS;
                            }
                            SathiApplication.shipmentImageMap.clear();
                            SathiApplication.rtsCapturedImage1.clear();
                            SathiApplication.rtsCapturedImage2.clear();
                            getDataManager().updateRtsStatus(Long.valueOf(rtsCommitResponse.getResponse().getId()), shipmentStatus).subscribe(aBoolean -> {
                                pushApi.setShipmentStatus(GlobalConstant.CommitStatus.CommitSynced);
                                updateSyncStatusInDRSRTSTable(Long.parseLong(rtsCommitResponse.getResponse().getId()));
                                compositeDisposable.add(getDataManager().deleteSyncedImage(String.valueOf(pushApi.getAwbNo())).subscribe(aBoolean1 -> {
                                }));
                                compositeDisposable.add(getDataManager().deleteSyncedFWD(pushApi.getAwbNo()).subscribe(aBoolean12 -> {
                                }));
                                compositeDisposable.add(getDataManager().saveCommitPacket(pushApi).subscribe(aBoolean13 -> {
                                }));
                                compositeDisposable.add(getDataManager().deleteShipmentData(Math.toIntExact(pushApi.getAwbNo())).subscribe(aBoolean14 -> {
                                }));
                                dismissProgressDialog();
                                getNavigator().setSuccessName();
                            }, throwable -> {
                                dismissProgressDialog();
                                getNavigator().showError("Re-Upload The Committed Shipment");
                                saveCommit(rtsCommit);
                            });
                        } else if ((rtsCommitResponse.getResponse().getCode().equalsIgnoreCase("E107")) || (rtsCommitResponse.getResponse().getCode().equalsIgnoreCase("107"))) {
                            dismissProgressDialog();
                            LocalLogout();
                        } else {
                            dismissProgressDialog();
                            getNavigator().showError(rtsCommitResponse.getResponse().getDescription() == null ? "RTS Commit Api Response False" : rtsCommitResponse.getResponse().getDescription());
                        }
                    }
                } catch (Exception e) {
                    dismissProgressDialog();
                    getNavigator().showError(e.getMessage());
                    saveCommit(rtsCommit);
                }
            }, throwable -> {
                dismissProgressDialog();
                getNavigator().showError(throwable.getMessage());
                saveCommit(rtsCommit);
            }));
        } catch (Exception e) {
            dismissProgressDialog();
            Constants.UPLOADEDS_CALLED = false;
            getNavigator().showError(e.getMessage());
            saveCommit(rtsCommit);
        }
    }

    private static List<RTSCommit.ImageData> getImageData(List<RTSCommit.ImageData> imageData) {
        List<RTSCommit.ImageData> imageDataPOD = new ArrayList<>();
        for (int i = 0; i < imageData.size(); i++) {
            if (imageData.get(i).getImageType().equalsIgnoreCase(GlobalConstant.ImageTypeConstants.OTHERS)) {
                RTSCommit.ImageData imageData1 = new RTSCommit.ImageData();
                imageData1.setImageId(imageData.get(i).getImageId());
                imageData1.setImageKey(imageData.get(i).getImageKey());
                imageDataPOD.add(imageData1);
            }
        }
        return imageDataPOD;
    }

    private void saveCommit(RTSCommit rtsCommit) {
        PushApi pushApi = new PushApi();
        pushApi.setAwbNo(Long.parseLong(rtsCommit.getConsigneeId()));
        pushApi.setAuthtoken(getDataManager().getAuthToken());
        try {
            pushApi.setRequestData(new ObjectMapper().writeValueAsString(rtsCommit));
            pushApi.setShipmentStatus(0);
            pushApi.setShipmentCaterogy(GlobalConstant.ShipmentTypeConstants.RTS);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        try {
            getCompositeDisposable().add(getDataManager().saveCommitPacket(pushApi).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
            }));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void updateSyncStatusInDRSRTSTable(long vendorID) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager().updateSyncStatusRTS(vendorID, 2).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {
        }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
    }

    private void LocalLogout() {
        getDataManager().setTripId("");
        getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT);
        clearAppData();
    }

    private void clearAppData() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager().deleteAllTables().subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
            try {
                getDataManager().clearPrefrence();
                getDataManager().setUserAsLoggedOut();
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
            clearStack();
        }));
    }

    private void clearStack() {
        Toast.makeText(context, "Your Session Hss Expired. Please Login", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    private void showProgressDialog(String message){
        dialog = new Dialog(context,android.R.style.Theme_Material_Light_Dialog);
        dialog.setContentView(R.layout.custom_progress_dialog);
        dialog.setCancelable(false);
        TextView loadingText = dialog.findViewById(R.id.dialog_loading_text);
        loadingText.setText(message);
        dialog.show();
    }

    private void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}