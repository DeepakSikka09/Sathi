package in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.databinding.ObservableField;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.data.rvp.RvpCommit;
import in.ecomexpress.sathi.repo.local.db.model.ImageModel;
import in.ecomexpress.sathi.repo.local.db.model.RvpWithQC;
import in.ecomexpress.sathi.repo.remote.ByteImageRequest;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.masterdata.SampleQuestion;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.ui.drs.rvp.signature.RVPSignatureViewModel;
import in.ecomexpress.sathi.utils.Base64;
import in.ecomexpress.sathi.utils.CryptoUtils;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@HiltViewModel
public class RvpQcDataDetailsViewModel extends BaseViewModel<IRvpQcDataDetailsNavigator> {
    RvpCommit rvpCommit;
    public ObservableField<RvpWithQC> rvpWithQC = new ObservableField<>();
    private final ObservableField<List<SampleQuestion>> sampleQuestionList = new ObservableField<>();
    public ObservableField<String> awbNo = new ObservableField<>("Awb");
    public ObservableField<String> itemName = new ObservableField<>("");
    public ObservableField<String> qualityCheck = new ObservableField<>("");
    public ObservableField<String> totalPass = new ObservableField<>("Total Pass");
    public ObservableField<String> totalFailed = new ObservableField<>("Total Failed");
    public ObservableField<String> qualityCheckDetails = new ObservableField<>("Quality Check Details");


    @Inject
    public RvpQcDataDetailsViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);

    }

    public void onBackClick() {
        getNavigator().onBack();

    }

    public ObservableField<String> getAwbNo() {
        try {
            if (rvpWithQC.get() != null && Objects.requireNonNull(rvpWithQC.get()).drsReverseQCTypeResponse != null)
                awbNo.set(Objects.requireNonNull(rvpWithQC.get()).drsReverseQCTypeResponse.getAwbNo().toString());
        } catch (Exception e) {
            Logger.e(RvpQcDataDetailsViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
        return awbNo;
    }

    public ObservableField<String> getItemName() {
        try {
            if (rvpWithQC.get() != null && Objects.requireNonNull(rvpWithQC.get()).drsReverseQCTypeResponse != null)
                itemName.set(Objects.requireNonNull(rvpWithQC.get()).drsReverseQCTypeResponse.getShipmentDetails().getItem());
        } catch (Exception e) {
            Logger.e(RvpQcDataDetailsViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
        return itemName;
    }

    public void onNextClick() {
        getNavigator().onNext(rvpCommit);
    }

    public void setQcData(RvpWithQC rvpWithQC, List<SampleQuestion> sampleQuestions) {
        this.rvpWithQC.set(rvpWithQC);
        this.sampleQuestionList.set(sampleQuestions);
        getItemName();
        getAwbNo();
        getNavigator().upDateCount();
    }

    @SuppressLint("DefaultLocale")
    public void setQualityCheckValue(int current, int total) {
        try {
            qualityCheck.set(String.format("Quality Check - %d out of %d", current, total));
        } catch (Exception e) {
            Logger.e(RvpQcDataDetailsViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    @SuppressLint("DefaultLocale")
    public void setTotalPass(int pass) {
        try {
            totalPass.set(String.format("Total Pass - %d ", pass));
        } catch (Exception e) {
            Logger.e(RvpQcDataDetailsViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    @SuppressLint("DefaultLocale")
    public void setTotalFailed(int fail) {
        try {
            totalFailed.set(String.format("Total Failed - %d ", fail));
        } catch (Exception e) {
            Logger.e(RvpQcDataDetailsViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void saveImageDB(String name, String imageUri, String imageCode) {
        try {
            ImageModel imageModel = new ImageModel();
            imageModel.setDraNo(Objects.requireNonNull(rvpWithQC.get()).drsReverseQCTypeResponse.getDrs().toString());
            imageModel.setAwbNo(Objects.requireNonNull(rvpWithQC.get()).drsReverseQCTypeResponse.getAwbNo().toString());
            imageModel.setImageName(name);
            imageModel.setImage(imageUri);
            imageModel.setImageCode(imageCode);
            imageModel.setStatus(0);
            imageModel.setImageCurrentSyncStatus(GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO);
            imageModel.setImageFutureSyncTime(Calendar.getInstance().getTimeInMillis());
            imageModel.setImageId(-1);
            imageModel.setDate(Calendar.getInstance().getTimeInMillis());
            imageModel.setShipmentType(GlobalConstant.ShipmentTypeConstants.RVP);
            imageModel.setImageType(GlobalConstant.ImageTypeConstants.RVP_QC);
            getCompositeDisposable().add(getDataManager().saveImage(imageModel).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {}));
        } catch (Exception e) {
            Logger.e(RvpQcDataDetailsViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    @SuppressLint("CheckResult")
    public void uploadImageServer(String imageName, String imageUri, String imageCode, long awbNo, long drsNo, Bitmap bitmap)
    {
        try {
            getNavigator().showProgress();
            byte[] bytes;
            File file = new File(imageUri);
            bytes = CryptoUtils.convertImageToBase64(bitmap);
            RequestBody mFile = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), mFile);
            RequestBody awb_no = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(awbNo));
            RequestBody drs_no = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(drsNo));
            RequestBody image_code = RequestBody.create(MediaType.parse("text/plain"), imageCode);
            RequestBody image_name = RequestBody.create(MediaType.parse("text/plain"), file.getName());
            RequestBody image_type = RequestBody.create(MediaType.parse("text/plain"), GlobalConstant.ImageTypeConstants.RVP_QC);
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
                getCompositeDisposable().add(getDataManager().doImageUploadApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), GlobalConstant.ImageTypeConstants.RVP_QC, headers, map, fileToUpload).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(imageUploadResponse -> {
                    if (imageUploadResponse != null) {
                        try {
                            saveImageDB(imageName, imageUri, imageCode);
                            getNavigator().onProgressFinishCall();
                            if (imageUploadResponse.getStatus().toLowerCase().contains("Success".toLowerCase())) {
                                Observable.fromCallable(() -> {
                                    getNavigator().onProgressFinishCall();
                                    if (imageUploadResponse.getImageId() == null) {
                                        uploadImageByteToServer(imageName, imageUri, imageCode, awbNo, drsNo, bitmap);
                                        getNavigator().showError("Image Corrupted While Uploading. Try Again");
                                    } else {
                                        saveImageResponse(imageName, imageUploadResponse.getImageId());
                                    }
                                    return false;
                                }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe((result) -> {
                                    if (imageUploadResponse.getImageId() != null) {
                                        getNavigator().setBitmap();
                                    } else {
                                        uploadImageByteToServer(imageName, imageUri, imageCode, awbNo, drsNo, bitmap);
                                    }
                                });
                            } else {
                                uploadImageByteToServer(imageName, imageUri, imageCode, awbNo, drsNo, bitmap);
                                getNavigator().onProgressFinishCall();
                            }
                        } catch (Exception ex) {
                            try {
                                getNavigator().onProgressFinishCall();
                                getNavigator().showError("Try Again.");
                                Logger.e(RvpQcDataDetailsViewModel.class.getName(), ex.getMessage());
                            } catch (Exception e) {
                                Logger.e(RvpQcDataDetailsViewModel.class.getName(), e.getMessage());
                            }
                        }
                    } else {
                        uploadImageByteToServer(imageName, imageUri, imageCode, awbNo, drsNo, bitmap);
                        getNavigator().onProgressFinishCall();
                    }
                }, throwable -> {
                    // while at the time of online
                    getNavigator().onProgressFinishCall();
                    getNavigator().setBitmap();
                    Logger.e(RVPSignatureViewModel.class.getName(), throwable.getMessage());
                    getNavigator().onHandleError(throwable.getLocalizedMessage());
                }));

            } catch (Exception ex) {
                Logger.e(RvpQcDataDetailsViewModel.class.getName(), ex.getMessage());
                uploadImageByteToServer(imageName, imageUri, imageCode, awbNo, drsNo, bitmap);
                getNavigator().onProgressFinishCall();
            }

        } catch (Exception ex) {
            Logger.e(RvpQcDataDetailsViewModel.class.getName(), ex.getMessage());
            uploadImageByteToServer(imageName, imageUri, imageCode, awbNo, drsNo, bitmap);
            getNavigator().onProgressFinishCall();
        }
    }






    public String convertImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream bytArray = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bytArray); //bm is the bitmap object
        byte[] byteArrayImage = bytArray.toByteArray();
        return Base64.getEncoder().encodeToString(byteArrayImage);
    }


    public void uploadImageByteToServer(String imageName, String imageUri, String imageCode, long awbNo, long drsno, Bitmap bitmap) {
        try {
            getNavigator().showProgress();
            final long timeStamp = Calendar.getInstance().getTimeInMillis();
            String bytearray = convertImageToBase64(bitmap);
            Map<String, String> headers = new HashMap<>();
            headers.put("token", getDataManager().getAuthToken());
            headers.put("Accept", "application/json");
            try {
                ByteImageRequest byteImageRequest = new ByteImageRequest();
                byteImageRequest.setImage(bytearray);
                byteImageRequest.setAwb_no(awbNo);
                byteImageRequest.setDrs_no(drsno);
                byteImageRequest.setImage_code(imageCode);
                byteImageRequest.setImage_name(imageName);
                byteImageRequest.setImage_type("RVP_QC");

                getCompositeDisposable().add(getDataManager()
                        .doImageByteUploadApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), "RVP_BYTE", headers, byteImageRequest)
                        .doOnSuccess(imageQualityResponse -> Log.d(TAG, imageQualityResponse.toString()))
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(imageUploadResponse -> {
                            try {
                                getNavigator().onProgressFinishCall();
                                if (imageUploadResponse.getImageId() == null) {
                                    getNavigator().onHandleError("Image Corrupted while uploading.Please try Again");
                                } else {
                                    saveImageResponse(imageName, imageUploadResponse.getImageId());
                                    getNavigator().setBitmap();
                                }
                            } catch (Exception e) {
                                getNavigator().onProgressFinishCall();
                                Logger.e(RvpQcDataDetailsViewModel.class.getName(), e.getMessage());
                            }
                        }, throwable -> {
                            getNavigator().onProgressFinishCall();
                            String error;
                            try {
                                writeErrors(timeStamp, new Exception(throwable));
                                error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                                getNavigator().onHandleError(error);
                            } catch (Exception e) {
                                Logger.e(RvpQcDataDetailsViewModel.class.getName(), e.getMessage());
                            }
                        }));
            } catch (Exception e) {
                writeErrors(timeStamp, e);
                Logger.e(RvpQcDataDetailsViewModel.class.getName(), e.getMessage());
                getNavigator().onProgressFinishCall();
                getNavigator().onHandleError(new RestApiErrorHandler(e.fillInStackTrace()).getErrorDetails().getEResponse().getDescription());
            }
        } catch (Exception ex) {
            Logger.e(RvpQcDataDetailsViewModel.class.getName(), ex.getMessage());
            getNavigator().onProgressFinishCall();
            Log.e("Image Sync exception", ex.toString());
        }
    }

    @SuppressLint("CheckResult")
    private void saveImageResponse(String image_name, int image_id) {
        try {
            getDataManager().updateImageStatus(image_name, 2).blockingSingle();
            getDataManager().updateImageID(image_name, image_id).blockingSingle();
        } catch (Exception e) {
            Logger.e(RvpQcDataDetailsViewModel.class.getName(), e.getMessage());
        }
    }

}
