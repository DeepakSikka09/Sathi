package in.ecomexpress.sathi.ui.dummy.eds.eds_signature;

import android.annotation.SuppressLint;

import androidx.constraintlayout.widget.Constraints;
import androidx.databinding.ObservableField;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.Map;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.data.commit.PushApi;
import in.ecomexpress.sathi.repo.local.data.eds.EDSActivityImageRequest;
import in.ecomexpress.sathi.repo.local.data.eds.EDSActivityResponseWizard;
import in.ecomexpress.sathi.repo.local.data.eds.EDSCommitResponse;
import in.ecomexpress.sathi.repo.local.data.eds.EdsCommit;
import in.ecomexpress.sathi.repo.local.db.model.ImageModel;
import in.ecomexpress.sathi.repo.local.db.model.Remark;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.consignee_profile.ProfileFound;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;
import in.ecomexpress.sathi.repo.remote.model.image.ImageUploadResponse;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.CryptoUtils;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.LocationHelper;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.content.ContentValues.TAG;
import static in.ecomexpress.sathi.utils.Constants.DISTANCE_API_KEY;

import javax.inject.Inject;


@HiltViewModel
public class EDSSignatureViewModel extends BaseViewModel<IEDSSignatureNavigator> {
    private long mLastClickTime = 0;

    private EdsCommit edsCommit;
    List<ImageModel> mimageModels = new ArrayList<>();
    private EDSResponse edsResponse;
    private final List<EDSActivityImageRequest> edsActivityImageRequestList = new ArrayList<EDSActivityImageRequest>();
    ObservableField<String> consignee_name_tv = new ObservableField<>();
    ObservableField<String> consignee_address_tv = new ObservableField<>();

    public ObservableField<String> getConsignee_address_tv() {
        return consignee_address_tv;
    }

    public void setConsignee_address_tv(ObservableField<String> consignee_address_tv) {
        this.consignee_address_tv = consignee_address_tv;
    }


    public ObservableField<String> getConsignee_name_tv() {
        return consignee_name_tv;
    }

    public void setConsignee_name_tv(ObservableField<String> consignee_name_tv) {
        this.consignee_name_tv = consignee_name_tv;
    }

    @Inject
    public EDSSignatureViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);
    }


    public void onBackclick() {
        getNavigator().onbackclick();
    }


    public void onCaptureImageClick() {
        getNavigator().onCaptureImage();
    }

    public void onSubmitClick() {
        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        if (getDCFEDistance()) {
            getNavigator().saveSignature();
        }
    }


    public String loginDate() {
        return getDataManager().getLoginDate();
    }

    @SuppressLint("CheckResult")
    public void uploadAWSImage(String imageUri, String imageType, String imageKey, Boolean isCommit, int image_sync_status) {

        saveImageDB(imageUri, imageType, imageKey, 0, image_sync_status);
        if (isCommit) {
            getNavigator().callCommit("", "");
            updateShipmentStatus(String.valueOf(edsResponse.getCompositeKey()));
        }
    }

    public void saveImageDB(String imageUri, String imageCode, String name, int imageId, int image_sync_staus) {
        try {

            Log.e("saveImageDB", "called");
            ImageModel imageModel = new ImageModel();
            imageModel.setDraNo(Integer.toString(edsResponse.getDrsNo()));
            imageModel.setAwbNo(Long.toString(edsResponse.getAwbNo()));
            imageModel.setImageName(name);
            imageModel.setImage(imageUri);
            imageModel.setImageCode(imageCode);
            imageModel.setStatus(image_sync_staus);
            imageModel.setImageCurrentSyncStatus(image_sync_staus);
            imageModel.setImageFutureSyncTime(Calendar.getInstance().getTimeInMillis());
            imageModel.setImageId(imageId);
            imageModel.setDate(Calendar.getInstance().getTimeInMillis());
            imageModel.setShipmentType(GlobalConstant.ShipmentTypeConstants.EDS);
            imageModel.setImageType(GlobalConstant.ShipmentTypeConstants.OTHER);

            getCompositeDisposable().add(getDataManager().saveImage(imageModel).
                    subscribeOn(getSchedulerProvider().io()).
                    observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {

                        }
                    }));

        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().submitErrorAlert(e.getMessage());
        }
    }

    public void setedsCommit(EDSResponse edsResponse) {
        try {

            this.edsResponse = edsResponse;
            // edsResponse.set(edsResponses);
            consignee_name_tv.set(edsResponse.getConsigneeDetail().getName());
            consignee_address_tv.set(CommonUtils.nullToEmpty(edsResponse.getConsigneeDetail().getAddress().getLine1()) + " "
                    + CommonUtils.nullToEmpty(edsResponse.getConsigneeDetail().getAddress().getLine2()) + " "
                    + CommonUtils.nullToEmpty(edsResponse.getConsigneeDetail().getAddress().getLine3()) + " "
                    + CommonUtils.nullToEmpty(edsResponse.getConsigneeDetail().getAddress().getLine4()) + " "
                    + CommonUtils.nullToEmpty(edsResponse.getConsigneeDetail().getAddress().getCity()) + " "
                    + edsResponse.getConsigneeDetail().getAddress().getPincode());

        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().submitErrorAlert(e.getMessage());
        }
    }
    public void createCommitPacketCashCollection(String image_id, String image_key, EdsCommit edsCommits, EDSResponse edsResponse, List<EDSActivityResponseWizard> edsActivityResponseWizards) {
        try {
            edsCommit = edsCommits;

            edsCommit.setFeEmpCode(getDataManager().getCode());
            edsCommit.setDrsId(Integer.toString(edsResponse.getDrsNo()));
            try {
                if (!Constants.CURRENT_LATITUDE.equalsIgnoreCase("0.0") && !Constants.CURRENT_LONGITUDE.equalsIgnoreCase("0.0")) {
                    edsCommit.setLocationLat(Constants.CURRENT_LATITUDE);
                    edsCommit.setLocationLong(Constants.CURRENT_LONGITUDE);
                } else if (!String.valueOf(in.ecomexpress.geolocations.Constants.latitude).equalsIgnoreCase("0.0") && !String.valueOf(in.ecomexpress.geolocations.Constants.longitude).equalsIgnoreCase("0.0")) {
                    edsCommit.setLocationLat(String.valueOf(in.ecomexpress.geolocations.Constants.latitude));
                    edsCommit.setLocationLong(String.valueOf(in.ecomexpress.geolocations.Constants.longitude));
                } else {
                    edsCommit.setLocationLat(String.valueOf(getDataManager().getCurrentLatitude()));
                    edsCommit.setLocationLong(String.valueOf(getDataManager().getCurrentLongitude()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            edsCommit.setConsigneeName(edsResponse.getConsigneeDetail().getName());
            edsCommit.setAttemptType("EDS");
            edsCommit.setAwb(Long.toString(edsResponse.getAwbNo()));
            edsCommit.setFeComments("NONE");
            edsCommit.setStatus(Constants.EDSDELIVERED);
            edsCommit.setAssignDate(edsResponse.getAssignDate());
            edsCommit.setAttemptReasonCode("2601");
            edsCommit.setOrderNo(edsResponse.getShipmentDetail().getOrderNo());
            edsCommit.setDrsCommitDateTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            if (edsResponse.getShipmentDetail().getFlag().isIdDcEnabled())
                edsCommit.setPackageBarcode(Long.toString(edsResponse.getAwbNo()));
            else
                edsCommit.setPackageBarcode("");
            if (getDataManager().getConsigneeProfileValue().equalsIgnoreCase("W")) {
                edsCommit.setFlag_of_warning("W");
            } else {
                edsCommit.setFlag_of_warning("N");
            }

            edsCommit.setTripId(getDataManager().getTripId());
            edsCommit.setEdsActivityResponseWizard(edsActivityResponseWizards);
            if (Constants.IS_CASH_COLLECTION_ENABLE) {
                if (!image_id.equalsIgnoreCase("")) {
                    EDSActivityImageRequest edsActivityImageRequest = new EDSActivityImageRequest();
                    edsActivityImageRequest.setImageId(image_id);
                    edsActivityImageRequest.setImageKey(trimString(image_key));
                    edsActivityImageRequestList.add(edsActivityImageRequest);

                    edsCommit.setEdsActivityImageRequests(edsActivityImageRequestList);
                    UploadEdsShipment(edsCommit);
                } else {
                    saveCommit(edsCommit);
                }
            } else {
                saveCommit(edsCommit);
            }


        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().submitErrorAlert(e.getMessage());
        }

    }

    public String trimString(String fileName) {
        if (fileName.indexOf(".") > 0)
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        return fileName;
    }

    private void UploadEdsShipment(EdsCommit edsCommit) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        final long timeStamp = Calendar.getInstance().getTimeInMillis();
        setIsLoading(true);
        HashMap<String, String> tokens = new HashMap<>();
        tokens.put(Constants.TOKEN, getDataManager().getAuthToken());
        tokens.put(Constants.EMP_CODE, getDataManager().getCode());
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
        // AppLogs.Companion.writeRestApiRequest(timeStamp, edsCommit);
        try {
            compositeDisposable.add(getDataManager()
                    .doEDSCommitApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), tokens, edsCommit).subscribeOn(getSchedulerProvider().io()).
                    subscribe((EDSCommitResponse edsCommitResponse) -> {
                        // AppLogs.Companion.writeRestApiResponse(timeStamp, edsCommitResponse);
                        if (edsCommitResponse.getStatus()) {
                            int shipment_status = 0;
                            String compositeKey = "";
                            if (edsCommitResponse.getResponse().getShipment_status().equalsIgnoreCase(Constants.EDSUNDELIVERED)) {
                                shipment_status = 3;
                            } else {
                                shipment_status = 2;
                            }
                            try {
                                compositeKey = edsCommitResponse.getResponse().getDrs_no() + "" + edsCommitResponse.getResponse().getAwb_no();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            getDataManager().
                                    updateEdsStatus(compositeKey, shipment_status).
                                    subscribe(new Consumer<Boolean>() {
                                                  @Override
                                                  public void accept(Boolean aBoolean) throws Exception {
                                                      if (aBoolean) {
                                                          setIsLoading(false);
                                                          updateSyncStatusInDRSEDSTable(edsCommitResponse.getResponse().getDrs_no() + "" + edsCommitResponse.getResponse().getAwb_no(), GlobalConstant.CommitStatus.CommitSynced);
                                                          compositeDisposable.add(getDataManager().deleteSyncedImage((edsCommitResponse.getResponse().getAwb_no())).subscribe(new Consumer<Boolean>() {
                                                              @Override
                                                              public void accept(Boolean aBoolean) throws Exception {
                                                                  //sendBoardCast();

                                                              }
                                                          }));

                                                      }
                                                  }
                                              }
                                            , new Consumer<Throwable>() {
                                                @Override
                                                public void accept(Throwable throwable) throws Exception {

                                                    throwable.printStackTrace();
                                                    getNavigator().openSuccess();
                                                    saveCommit(edsCommit);
                                                    setIsLoading(false);

                                                }
                                            });
                        } else {
                            saveCommit(edsCommit);
                            getNavigator().openSuccess();
                            setIsLoading(false);
                        }

                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            saveCommit(edsCommit);
                            setIsLoading(false);
                            getNavigator().openSuccess();
                            throwable.printStackTrace();
                        }
                    }));
        } catch (Exception e) {
            e.printStackTrace();
            setIsLoading(false);
            saveCommit(edsCommit);
            getNavigator().openSuccess();

        }
    }


    private void updateSyncStatusInDRSEDSTable(String compositeKey, int i) {
        final long TimeStampTag = Calendar.getInstance().getTimeInMillis();
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager().updateSyncStatusEDS(compositeKey, 2)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        getNavigator().openSuccess();
                    }
                }, throwable -> {

                }));
    }


    private void saveCommit(EdsCommit edsCommit) {
        PushApi pushApi = new PushApi();
        pushApi.setCompositeKey(edsResponse.getCompositeKey());
        pushApi.setAwbNo(Long.valueOf(edsCommit.getAwb()));
        pushApi.setAuthtoken(getDataManager().getAuthToken());
        try {
            pushApi.setRequestData(new ObjectMapper().writeValueAsString(edsCommit));
            pushApi.setShipmentStatus(0);
            pushApi.setShipmentCaterogy(GlobalConstant.ShipmentTypeConstants.EDS);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            getNavigator().submitErrorAlert(e.getMessage());
        }
        try {

            getCompositeDisposable().add(getDataManager().saveCommitPacket(pushApi).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {
                    updateShipmentStatus(String.valueOf(edsResponse.getCompositeKey()));
                }
            }));

        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().submitErrorAlert(e.getMessage());
        }
    }

    public void onClear() {
        getNavigator().onclear();
    }

    private void updateShipmentStatus(String id) {

        try {
            getCompositeDisposable().add(getDataManager().
                    updateEdsStatus(id, 2).observeOn(getSchedulerProvider().ui()).
                    subscribeOn(getSchedulerProvider().io()).
                    subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) {
                            getNavigator().openSuccess();
                        }
                    }));
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().submitErrorAlert(e.getMessage());
        }

    }

    public void getConsigneeProfiling() {
        boolean enable = getDataManager().getConsigneeProfiling();
        getNavigator().setConsingeeProfiling(enable);
    }

    public void fetchEDSShipment(String compositeKey) {
//        Long awb=Long.parseLong(awbNo);
        getCompositeDisposable().add(getDataManager().getEDS(compositeKey).
                subscribeOn(getSchedulerProvider().io()).
                observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<EDSResponse>() {
                    @Override
                    public void accept(EDSResponse edsResponsenew) throws Exception {
                        edsResponse.setCompositeKey(edsResponsenew.getCompositeKey());
                        getNavigator().onEDSItemFetched(edsResponsenew);
                    }
                }, throwable -> {
                    writeErrors(Calendar.getInstance().getTimeInMillis(), new Exception(throwable));
                    throwable.printStackTrace();
                }));


    }
    public void checkMeterRange(EDSResponse edsResponse) {
        try {
            getProfileLatLng(edsResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getProfileLatLng(EDSResponse edsResponse) {
        try {
            getCompositeDisposable().add(getDataManager().
                    getProfileLat(edsResponse.getAwbNo()).
                    subscribeOn(getSchedulerProvider().io()).
                    subscribe(new Consumer<ProfileFound>() {
                        @Override
                        public void accept(ProfileFound profileFound) {
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
                                        e.printStackTrace();
                                    }
                                    if (consigneeLatitude == 0.0 || consigneeLongitude == 0.0) {
                                        getNavigator().setConsigneeDistance(0);
                                        return;
                                    }

                                    int meter = LocationHelper.getDistanceBetweenPoint(currentLatitude, currentLongitude, consigneeLatitude, consigneeLongitude);
                                    getNavigator().setConsigneeDistance(meter);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.e("error", throwable.getMessage());
                        }
                    }));
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    public void uploadImageServer(String imageName, String imageUri, String imageCode, long awbNo, int drsno, String activity_code, Bitmap bitmap) {
        final long timeStampUploadImageToServer = Calendar.getInstance().getTimeInMillis();
        setIsLoading(true);
        HashMap<String, Long> timeStampTagging = new HashMap<>();
        try {
            final long timeStamp = Calendar.getInstance().getTimeInMillis();
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
            Log.e("EDS map", map + "");
            //AppLogs.Companion.writeRestApiRequest(timeStamp, map);
            try {

                getCompositeDisposable().add(getDataManager()
                        .doImageUploadApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), GlobalConstant.ImageTypeConstants.OTHERS, headers, map, fileToUpload)
                        .doOnSuccess(new Consumer<ImageUploadResponse>() {
                            @Override
                            public void accept(ImageUploadResponse imageQualityResponse) {
                                Log.d(TAG, imageQualityResponse.toString());

                            }
                        })
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(new Consumer<ImageUploadResponse>() {
                            @Override
                            public void accept(ImageUploadResponse imageUploadResponse) {
                                EDSSignatureViewModel.this.setIsLoading(false);
                                try {

                                    //setIsLoading(false);
                                    // saveImageId(imageUploadResponse,activity_code);
                                    if (imageUploadResponse.getStatus().equalsIgnoreCase("Success")) {
                                        saveImageDB(imageUri, imageCode, imageName, imageUploadResponse.getImageId(), GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_COMPLETE);
                                        //saveImageResponse(imageUploadResponse.getFileName(), imageUploadResponse.getImageId());
                                        getNavigator().callCommit(String.valueOf(imageUploadResponse.getImageId()), imageName);
                                    } else {
                                        saveImageDB(imageUri, imageCode, imageName, 0, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO);
                                        getNavigator().callCommit("", imageName);
                                    }

                                } catch (Exception e) {
                                    saveImageDB(imageUri, imageCode, imageName, 0, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO);
                                    getNavigator().callCommit("", imageName);
                                    e.printStackTrace();
                                    setIsLoading(false);
                                }
                            }

                        }, throwable -> {
                            // setIsLoading(false);

                            String error;
                            setIsLoading(false);
                            try {
                                saveImageDB(imageUri, imageCode, imageName, 0, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO);
                                getNavigator().callCommit("", imageName);
                                writeErrors(timeStamp, new Exception(throwable));
                                error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                                getNavigator().onHandleError(error);
                            } catch (Exception e) {
                                e.printStackTrace();

                            }
                        }));
            } catch (Exception e) {
                setIsLoading(false);
                saveImageDB(imageUri, imageCode, imageName, 0, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO);
                getNavigator().callCommit("", imageName);
                writeErrors(timeStamp, e);
                e.printStackTrace();
//            Log.e(TAG, e.getCause().getStackTrace().toString());
                //setIsLoading(false);
                if (e instanceof Throwable) {
                    getNavigator().onHandleError(new RestApiErrorHandler(e.fillInStackTrace()).getErrorDetails().getEResponse().getDescription());
                }
            }

        } catch (Exception ex) {
            saveImageDB(imageUri, imageCode, imageName, 0, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO);
            getNavigator().callCommit("", imageName);
            ex.printStackTrace();
            // setIsLoading(false);
            Log.e("Image Sync exception", ex.toString());
        }
    }


    public void uploadImageServerImage(String imageName, String imageUri, String imageCode, long awbNo, int drsno, Bitmap bitmap) {
        final long timeStampUploadImageToServer = Calendar.getInstance().getTimeInMillis();
        setIsLoading(true);
        HashMap<String, Long> timeStampTagging = new HashMap<>();
        try {
            final long timeStamp = Calendar.getInstance().getTimeInMillis();
//            File image_file = createFileFromBitmap(imageUri ,bitmap);
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
            // AppLogs.Companion.writeRestApiRequest(timeStamp, map);
            try {
                getCompositeDisposable().add(getDataManager().doImageUploadApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), GlobalConstant.ImageTypeConstants.OTHERS, headers, map, fileToUpload).
                        subscribeOn(getSchedulerProvider().io()).
                        observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<ImageUploadResponse>() {
                            @Override
                            public void accept(ImageUploadResponse imageUploadResponse) throws Exception {

                                if (imageUploadResponse != null) {
                                    try {
                                        setIsLoading(false);
                                        if (imageUploadResponse.getStatus().toLowerCase().contains("Success".toLowerCase())) {
                                            getNavigator().setBitmap();
                                            saveImageDB(imageUri, imageCode, imageName, imageUploadResponse.getImageId(), GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_COMPLETE);
                                            Observable.fromCallable(() -> {
                                                        saveImageResponse(imageName, imageUploadResponse.getImageId());
                                                        return false;
                                                    }).subscribeOn(Schedulers.io())
                                                    .observeOn(Schedulers.io())
                                                    .subscribe((result) -> {

                                                    });

                                            Log.e("image upload", "success");
                                        } else {
                                            getNavigator().setBitmap();
                                            saveImageDB(imageUri, imageCode, imageName, 0, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO);
                                            setIsLoading(false);
//                                    getNavigator().onHandleError(imageUploadResponse.getDescription());
                                        }
                                    } catch (Exception ex) {
                                        try {
                                            setIsLoading(false);
                                            getNavigator().setBitmap();
                                            saveImageDB(imageUri, imageCode, imageName, 0, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO);
//                                    getNavigator().onHandleError("Please try again.");
                                            ex.printStackTrace();
                                        } catch (Exception e) {
                                            getNavigator().setBitmap();
                                            saveImageDB(imageUri, imageCode, imageName, 0, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO);
                                            setIsLoading(false);
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    getNavigator().setBitmap();
                                    saveImageDB(imageUri, imageCode, imageName, 0, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO);
                                    setIsLoading(false);
//                            getNavigator().onHandleError("server error");
                                    //getNavigator().showError(imageUploadResponse.getDescription());
                                }
//
                            }
                        }, throwable -> {
                            setIsLoading(false);
                            throwable.printStackTrace();
                            getNavigator().setBitmap();
                            saveImageDB(imageUri, imageCode, imageName, 0, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO);
//                    getNavigator().onHandleError(throwable.getLocalizedMessage());
                            // writeCrashes(timeStamp, new Exception(throwable));
                            Log.d(Constraints.TAG, "accept: error");

                        }));


            } catch (Exception ex) {
                getNavigator().setBitmap();
                saveImageDB(imageUri, imageCode, imageName, 0, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO);
                setIsLoading(false);
                ex.printStackTrace();
            }

        } catch (Exception ex) {
            getNavigator().setBitmap();
            saveImageDB(imageUri, imageCode, imageName, 0, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO);
            setIsLoading(false);
            ex.printStackTrace();
            Log.e("Image Sync exception", ex.toString());
        }
    }

    private void saveImageResponse(String image_name, int image_id) {
        getDataManager().updateImageStatus(image_name, 2).blockingSingle();
        getDataManager().updateImageID(image_name, image_id).blockingSingle();
    }


    private List<ImageModel> getImagesList(String awbNo) {

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager().getImages(awbNo).subscribeOn
                        (getSchedulerProvider().io()).
                observeOn(getSchedulerProvider().io()).subscribe(new Consumer<List<ImageModel>>() {
                    @Override
                    public void accept(List<ImageModel> imageModels) throws Exception {
                        Log.e("getImages", imageModels.size() + "");
                        mimageModels = imageModels;

                    }
                }));
        return mimageModels;
    }

    public double getDistaneBetweenLocations(LatLng destination) {
        try {
            double distance = 0.0;
            GeoApiContext context = new GeoApiContext().setApiKey(DISTANCE_API_KEY);
            DirectionsResult result = DirectionsApi.newRequest(context).mode(TravelMode.DRIVING).units(Unit.METRIC).origin(new LatLng(getDataManager().getCurrentLatitude(), getDataManager().getCurrentLongitude())).optimizeWaypoints(true).destination(destination).awaitIgnoreError();
            String dis = (result.routes[0].legs[0].distance.humanReadable);
            if (dis.endsWith("km")) {
                distance = Double.parseDouble(dis.replaceAll("[^\\.0123456789]", "")) * 1000;
            } else {
                distance = Double.parseDouble(dis.replaceAll("[^\\.0123456789]", ""));
            }
            return distance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public int getCounterDeliveryRange() {
        double currentLatitude = getDataManager().getCurrentLatitude();
        double currentLongitude = getDataManager().getCurrentLongitude();
        double DcLatitude = getDataManager().getDCLatitude();
        double DcLongitude = getDataManager().getDCLongitude();
        int meter = LocationHelper.getDistanceBetweenPoint(currentLatitude, currentLongitude, DcLatitude, DcLongitude);
        return meter;
    }

    public boolean getDCFEDistance() {
        int meter = LocationHelper.getDistanceBetweenPoint(getDataManager().getCurrentLatitude(),
                getDataManager().getCurrentLongitude(), getDataManager().getDCLatitude(), getDataManager().getDCLongitude());
        if (meter < getDataManager().getDCRANGE()) {
            getNavigator().onHandleError("Shipment cannot be mark delivered within DC");
            return false;
        } else {
            return true;
        }
    }

    public void getRemarkCount(long awb) {
        try {
            getCompositeDisposable().add(getDataManager().getRemarks(awb).observeOn(getSchedulerProvider().io()).subscribeOn(getSchedulerProvider().io()).subscribe(new Consumer<Remark>() {
                @Override
                public void accept(Remark remark) throws Exception {

                }
            }, throwable -> {
                throwable.printStackTrace();
            }));
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onHandleError(e.getMessage());
        }
    }
}

