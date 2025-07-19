package in.ecomexpress.sathi.ui.drs.forward.obd.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.db.model.ImageModel;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.ui.drs.forward.obd.navigator.IFwdObdQcNavigator;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.CryptoUtils;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@HiltViewModel
public class FwdOBDcCPassViewModel extends BaseViewModel<IFwdObdQcNavigator> {

    private final String TAG = FwdOBDcCPassViewModel.class.getSimpleName();
    public int numberOfQualityChecks;
    public ArrayList<in.ecomexpress.sathi.repo.remote.model.drs_list.forward.quality_checks> quality_checks;
    public List<String> qualityCheckName = new ArrayList<>();
    public List<String> imageCaptureSettings = new ArrayList<>();
    public List<String> qcCode = new ArrayList<>();
    public List<String> isOptional = new ArrayList<>();
    public String itemName;
    private ProgressDialog dialog;

    @Inject
    public FwdOBDcCPassViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application application) {
        super(dataManager, schedulerProvider, application);
    }

    private final MutableLiveData<Boolean> isDataLoaded = new MutableLiveData<>();

    public LiveData<Boolean> getIsDataLoaded() {
        return isDataLoaded;
    }

    public void uploadImageToServer(Activity context, String imageName, String imageUri, String imageCode, long awbNo, int drsNo, Bitmap bitmap) {
        dialog = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
        dialog.setIndeterminate(true);
        dialog.setMessage("Image Uploading....");
        dialog.setCancelable(false);
        dialog.show();
        try {
            final long timeStamp = Calendar.getInstance().getTimeInMillis();
            File image_file = new File(imageUri);
            byte[] bytes = CryptoUtils.decryptFile1(image_file.toString(), Constants.ENC_DEC_KEY);
            RequestBody mFile = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", image_file.getName(), mFile);
            RequestBody awb_no = RequestBody.create(MultipartBody.FORM, String.valueOf(awbNo));
            RequestBody drs_no = RequestBody.create(MultipartBody.FORM, String.valueOf(drsNo));
            RequestBody image_code = RequestBody.create(MultipartBody.FORM, String.valueOf(imageCode));
            RequestBody image_name = RequestBody.create(MultipartBody.FORM, String.valueOf(imageName));
            RequestBody image_type = RequestBody.create(MultipartBody.FORM, GlobalConstant.ImageTypeConstants.RVP_QC);
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
                getCompositeDisposable().add(getDataManager().doImageUploadApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), GlobalConstant.ImageTypeConstants.RVP_QC, headers, map, fileToUpload).doOnSuccess(imageQualityResponse -> Log.d(ContentValues.TAG, imageQualityResponse.toString())).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(imageUploadResponse -> {
                    try {
                        if (imageUploadResponse.getStatus().equalsIgnoreCase("Success")) {
                            saveImageDB(imageUri, imageCode, imageName, imageUploadResponse.getImageId(), GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_COMPLETE, awbNo, drsNo, bitmap);
                        } else {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            String errorMessage = imageUploadResponse.getDescription() == null ? "" : imageUploadResponse.getDescription();
                            if (errorMessage.isEmpty()) {
                                getNavigator().showError("Server Response False, Recapture Image");
                            } else {
                                getNavigator().showError(errorMessage);
                            }
                        }
                    } catch (Exception e) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        getNavigator().showError("Server Response False, Recapture Image");
                        Logger.e(TAG+"uploadImageServer", String.valueOf(e));
                    }
                }, throwable -> {
                    try {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        writeErrors(timeStamp, new Exception(throwable));
                        getNavigator().showError("Server Response False, Recapture Image");
                    } catch (Exception e) {
                        Logger.e(TAG+"uploadImageServer", String.valueOf(e));
                    }
                }));
            } catch (Exception e) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                writeErrors(timeStamp, e);
                getNavigator().showError("Server Response False, Recapture Image");
            }
        } catch (Exception e) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            getNavigator().showError("Server Response False, Recapture Image");
        }
    }

    public void saveImageDB(String imageUri, String imageCode, String imageName, int imageId, int sync_status, long awb, int drsId, Bitmap bitmap) {
        try {
            ImageModel imageModel = new ImageModel();
            imageModel.setDraNo(String.valueOf(drsId));
            imageModel.setAwbNo(String.valueOf(awb));
            imageModel.setImageName(imageName);
            imageModel.setImage(imageUri);
            imageModel.setImageCode(imageCode);
            imageModel.setStatus(sync_status);
            imageModel.setImageCurrentSyncStatus(sync_status);
            imageModel.setImageFutureSyncTime(Calendar.getInstance().getTimeInMillis());
            imageModel.setImageId(imageId);
            imageModel.setImageType(GlobalConstant.ImageTypeConstants.FWD_OBD);
            imageModel.setDate(Calendar.getInstance().getTimeInMillis());
            imageModel.setShipmentType(GlobalConstant.ShipmentTypeConstants.FWD_OBD);
            getCompositeDisposable().add(getDataManager().saveImage(imageModel).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
                getNavigator().setCapturedImageBitmap(bitmap);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }));
        } catch (Exception e) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            getNavigator().showError(e.getMessage());
            Logger.e(TAG+"saveImageDB", String.valueOf(e));
        }
    }

    public void fetObdQualityChecksData(long awbNo){
        try{
            getCompositeDisposable().add(getDataManager().fetchObdQualityCheckData(awbNo).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(obdQualityCheckData -> {
                quality_checks = obdQualityCheckData.getShipmentDetails().qc_item.get(0).quality_checks;
                if(quality_checks == null){
                    numberOfQualityChecks = 0;
                } else{
                    itemName = obdQualityCheckData.getShipmentDetails().qc_item.get(0).getItem();
                    numberOfQualityChecks = quality_checks.size();
                    getQualityCheckNameList(quality_checks);
                }
            }, throwable -> {
                writeErrors(System.currentTimeMillis(), new Exception(throwable));
                Logger.e(TAG, String.valueOf(throwable));
            }));
        } catch(Exception e){
            getNavigator().showError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void getQualityCheckNameList(ArrayList<in.ecomexpress.sathi.repo.remote.model.drs_list.forward.quality_checks> quality_checks){
        for(int i=0; i<quality_checks.size(); i++){
            qualityCheckName.add(quality_checks.get(i).getQcName());
            imageCaptureSettings.add((quality_checks.get(i).getImageCaptureSettings()));
            qcCode.add(quality_checks.get(i).getQcCode());
            isOptional.add(quality_checks.get(i).getIs_optional());
        }
        isDataLoaded.setValue(true);
    }
}