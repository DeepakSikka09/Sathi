package in.ecomexpress.sathi.ui.drs.rvp.awbscan;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.data.rvp.RvpCommit;
import in.ecomexpress.sathi.repo.local.db.model.ImageModel;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.RvpFlyerDuplicateCheckRequest;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.qc_input.QcInputViewModel;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.CryptoUtils;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@HiltViewModel
public class CaptureScanViewModel extends BaseViewModel<CaptureScanNavigator> {

    public static String device = (Build.MANUFACTURER + ":" + Build.MODEL).toUpperCase(Locale.US);
    ProgressDialog progress;
    RvpCommit rvpCommit;
    private final ObservableField<String> awbNo = new ObservableField<>();
    private final ObservableField<String> itemName = new ObservableField<>();
    private final ObservableField<Boolean> scanCodeOpen = new ObservableField<>();
    private final ObservableField<Boolean> scanCodeClose = new ObservableField<>();
    private final ObservableField<Boolean> imageOpen = new ObservableField<>();
    private final ObservableField<Boolean> imageOpen_two = new ObservableField<>();
    private final ObservableField<Boolean> imageClose = new ObservableField<>();
    private final MutableLiveData<Boolean> imageSaveStatus = new MutableLiveData<>();

    public LiveData<Boolean> getImageSaveStatus() {
        return imageSaveStatus;
    }

    public ObservableField<Boolean> getScanCodeOpen() {
        return scanCodeOpen;
    }

    public void setScanCodeOpen(Boolean codeOpen) {
        this.scanCodeOpen.set(codeOpen);
    }

    public ObservableField<Boolean> getScanCodeClose() {
        return scanCodeClose;
    }

    public void setScanCodeClose(Boolean codeClose) {
        this.scanCodeClose.set(codeClose);
    }

    public ObservableField<Boolean> getImageOpen() {
        return imageOpen;
    }

    public void setImageOpen(Boolean imageOpen) {
        this.imageOpen.set(imageOpen);
    }

    public ObservableField<Boolean> getImageOpen_two() {
        return imageOpen_two;
    }

    public void setImageOpen_two(Boolean imageOpen) {
        this.imageOpen_two.set(imageOpen);
    }

    public ObservableField<Boolean> getImageClose() {
        return imageClose;
    }

    public void setImageClose(Boolean imageClose) {
        this.imageClose.set(imageClose);
    }

    public ObservableField<String> getAwbNo() {
        return awbNo;
    }

    public ObservableField<String> getItemName() {
        return itemName;
    }

    @Inject
    public CaptureScanViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);
    }

    public void captureImageBeforePackaging() {
        getNavigator().captureImageBeforePackaging();
    }

    public void captureImageTwoBeforePackaging() {
        getNavigator().captureImageTwoBeforePackaging();
    }

    public void scanCodeBeforePackaging() {
        getNavigator().scanCodeBeforePackaging();
    }

    public void captureImageAfterPackaging() {
        getNavigator().captureImageAfterPackaging();
    }

    public void scanCodeAfterPackaging() {
        getNavigator().scanCodeAfterPackaging();
    }

    public void OnProceed() {
        getNavigator().onProceed();
    }

    public void initRvpCommitData(RvpCommit rvpCommit) {
        this.rvpCommit = rvpCommit;
    }

    public void getRvpFlyerDuplicateCheck(String ref_packing_barcode, Boolean scanCodeStatus) {
        setIsLoading(true);
        try {
            RvpFlyerDuplicateCheckRequest rvpflyerDuplicateCheckRequest = new RvpFlyerDuplicateCheckRequest();
            rvpflyerDuplicateCheckRequest.setAwb(rvpCommit.getAwb());
            rvpflyerDuplicateCheckRequest.setDrs_id(rvpCommit.getDrsId());
            rvpflyerDuplicateCheckRequest.setRef_packaging_barcode(ref_packing_barcode);
            getCompositeDisposable().add(getDataManager().doRvpflyerDuplicateCheck(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), rvpflyerDuplicateCheckRequest).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).
                subscribe(rvpflyerDuplicateCheckResponse -> {
                    setIsLoading(false);
                    try {
                        if (rvpflyerDuplicateCheckResponse.getStatus().matches("true")) {
                            scanCodeClose.set(false);
                            getNavigator().errorMsg(rvpflyerDuplicateCheckResponse.getDescription());
                            CaptureScanActivity.status[4] = false;
                        } else {
                            scanCodeClose.set(scanCodeStatus);
                        }
                    } catch (Exception e) {
                        Logger.e(CaptureScanViewModel.class.getName(), e.getMessage());
                    }
                }, throwable -> {
                    try {
                        setIsLoading(false);
                    } catch (Exception e) {
                        Logger.e(CaptureScanViewModel.class.getName(), e.getMessage());
                    }
            }));
        } catch (Exception e) {
            Logger.e(CaptureScanViewModel.class.getName(), e.getMessage());
            writeErrors(System.currentTimeMillis(), e);
            setIsLoading(false);
        }
    }

    public void uploadImageServer(Context context, String imageName, String imageUri, String imageCode, long awbNo, long drsNo) {
        showProgress(context);
        try {
            File image_file = new File(imageUri);
            byte[] bytes = CryptoUtils.decryptFile1(image_file.toString(), Constants.ENC_DEC_KEY);
            RequestBody mFile = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
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
                getCompositeDisposable().add(getDataManager()
                    .doImageUploadApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), "OTHERS", headers, map, fileToUpload)
                    .doOnSuccess(imageQualityResponse -> dismissDialog())
                    .subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(imageUploadResponse -> {
                        try {
                            dismissDialog();
                            if (imageUploadResponse.getStatus().equalsIgnoreCase("Success")) {
                                saveImageInLocalDB(imageUri, imageCode, imageName, 2, imageUploadResponse.getImageId());
                            } else {
                                saveImageInLocalDB(imageUri, imageCode, imageName, 0, 0);
                            }
                        } catch (Exception e) {
                            dismissDialog();
                            Logger.e(CaptureScanViewModel.class.getName(), e.getMessage());
                        }
                    }, throwable -> {
                        dismissDialog();
                        String error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                        getNavigator().errorMsg(error);
                }));
            } catch (Exception e) {
                dismissDialog();
                Logger.e(CaptureScanViewModel.class.getName(), e.getMessage());
                getNavigator().errorMsg(new RestApiErrorHandler(e.fillInStackTrace()).getErrorDetails().getEResponse().getDescription());
            }
        } catch (Exception ex) {
            dismissDialog();
            getNavigator().errorMsg(String.valueOf(ex));
        }
    }

    public void saveImageInLocalDB(String imageUri, String imageCode, String image_name, int status, int imageId) {
        try {
            ImageModel imageModel = new ImageModel();
            if(imageId > 0){
                imageModel.setImageId(imageId);
            }
            imageModel.setDraNo(rvpCommit.getDrsId());
            imageModel.setAwbNo(rvpCommit.getAwb());
            imageModel.setImageName(image_name);
            imageModel.setImage(imageUri);
            imageModel.setImageCode(imageCode);
            imageModel.setStatus(status);
            imageModel.setImageCurrentSyncStatus(GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO);
            imageModel.setImageFutureSyncTime(Calendar.getInstance().getTimeInMillis());
            imageModel.setImageType(GlobalConstant.ImageTypeConstants.OTHERS);
            imageModel.setDate(Calendar.getInstance().getTimeInMillis());
            imageModel.setShipmentType(GlobalConstant.ShipmentTypeConstants.RVP);
            getCompositeDisposable().add(getDataManager().saveImage(imageModel)
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(imageSaveStatus::setValue,
                    throwable -> imageSaveStatus.setValue(false))
            );
        } catch (Exception e) {
            getNavigator().errorMsg("Image Not Save On Local DB, Recapture Again");
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

    public void getPhonePeShipmentType(String awbNo) {
        try {
            getCompositeDisposable().add(getDataManager().getPhonePeShipmentType(awbNo).
                    subscribeOn(getSchedulerProvider().io()).
                    observeOn(getSchedulerProvider().ui()).subscribe(String -> getNavigator().isPhonePayEnabled(String), throwable -> {
                        writeErrors(Calendar.getInstance().getTimeInMillis(), new Exception(throwable));
                        Logger.e(QcInputViewModel.class.getName(), throwable.getMessage());
                    }));
        } catch (Exception e) {
            Logger.e(QcInputViewModel.class.getName(), e.getMessage());
            getNavigator().errorMsg(e.getMessage());
        }
    }
}