package in.ecomexpress.sathi.ui.drs.forward.obd.viewmodel;

import android.app.Activity;
import android.annotation.SuppressLint;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.util.Log;
import java.io.File;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.local.db.model.ImageModel;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.ui.drs.forward.obd.navigator.IObdOTPNavigator;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.otp.resendotp.GenerateUDOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.otp.verifyotp.VerifyUDOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.voice_otp.VoiceOTP;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.CryptoUtils;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.LocationHelper;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

@HiltViewModel
public class OBDStopOTPViewModel extends BaseViewModel<IObdOTPNavigator> {

    private ProgressDialog dialog;
    private final String TAG = OBDStopOTPViewModel.class.getSimpleName();

    @Inject
    public OBDStopOTPViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application application) {
        super(dataManager, schedulerProvider, application);
    }

    public void generatePassOtp() {
        getNavigator().onGenerateOtpClick();
    }

    public void generatePassOtpResend() {
        getNavigator().onGenerateOtpResendClick();
    }

    public void generateStopOtp(boolean isGenerate){
        getNavigator().onGenerateStopOtp(isGenerate);
    }

    public void generateFailedOtp(boolean isGenerate){
        getNavigator().onGenerateQcFailOtp(isGenerate);
    }

    public void generateCtsOtp(boolean isGenerate){
        getNavigator().onGenerateCtsOtp(isGenerate);
    }

    public void generateVoiceOtp(String otpType){
        getNavigator().VoiceCallOtp(otpType);
    }

    public void getOtpOnAlternate(boolean isAlternate) {
        getNavigator().resendSms(isAlternate);
    }

    public void getOtpVerifyCall() {
        getNavigator().onVerifyClick();
    }

    public void onGenerateOtpApiCall(Activity context, String awb, String drsId, Boolean alternateClick, String messageType, Boolean generateOtp) {
        dialog = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
        dialog.setMessage("Sending OTP....");
        dialog.show();
        dialog.setIndeterminate(false);
        GenerateUDOtpRequest request = new GenerateUDOtpRequest(awb, messageType, drsId, alternateClick, getDataManager().getCode(), generateOtp, Constants.OBD_Product_TYPE);
        try {
            getCompositeDisposable().add(getDataManager().doGenerateUDOtpApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request).doOnSuccess(resendOtpResponse -> {
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (response.getStatus().equals("true")) {
                    getNavigator().onOtpResendSuccess(response.getDescription());
                    if (!response.getDescription().equalsIgnoreCase("Otp already generated!")) {
                        getNavigator().voiceTimer();
                    }
                } else {
                    if (response.getResponse() != null) {
                        if (response.getResponse().getStatusCode().equalsIgnoreCase("107")) {
                            getNavigator().doLogout(response.getResponse().getDescription());
                        }
                    } else if (response.getDescription().matches("Max Attempt Reached")) {
                        getNavigator().onOtpResendSuccess(response.getDescription());
                    } else {
                        getNavigator().showError(response.getDescription());
                    }
                }
            }, throwable -> {
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
                String error;
                try {
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                    getNavigator().showError(error);
                } catch (Exception e) {
                    if (dialog.isShowing())
                        dialog.dismiss();
                    Logger.e(OBDStopOTPViewModel.class.getName(), e.getMessage());
                    getNavigator().showError(e.getMessage());
                }
            }));
        } catch (Exception e) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Logger.e(OBDStopOTPViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void onVerifyApiCall(Activity context, String awb, String otp, String drsId, String messageType) {
        dialog = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
        dialog.setIndeterminate(true);
        dialog.setMessage("Verifying....");
        dialog.show();
        try {
            VerifyUDOtpRequest request = new VerifyUDOtpRequest(awb, drsId, messageType, otp);
            getCompositeDisposable().add(getDataManager().doVerifyUDOtpDRSApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request).doOnSuccess(verifyOtpResponse -> {
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                setIsLoading(false);
                if (dialog.isShowing())
                    dialog.dismiss();
                if (response.getStatus().equalsIgnoreCase("true")) {
                    getNavigator().onOtpVerifySuccess(response.getDescription(),messageType);
                } else {
                    if (response.getStatus().equalsIgnoreCase("false")) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        getNavigator().showError("Invalid Otp, Try Again");
                    } else {
                        if (response.getResponse().getStatusCode().equalsIgnoreCase("107")) {
                            getNavigator().doLogout(response.getResponse().getDescription());
                        } else {
                            getNavigator().showError(response.getDescription());
                        }
                    }
                }}, throwable -> {
                if (dialog.isShowing())
                    dialog.dismiss();
                String error;
                try {
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                    getNavigator().showError(error);
                } catch (Exception e) {
                    Logger.e(OBDStopOTPViewModel.class.getName(), e.getMessage());
                    getNavigator().showError(e.getMessage());
                }
            }));
        } catch (Exception e) {
            Logger.e(OBDStopOTPViewModel.class.getName(), e.getMessage());
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            getNavigator().showError(e.getMessage());
        }
    }

    public void doVoiceOTPApi(String awb, String drs_id, String message_type) {
        try {
            VoiceOTP voiceOTP = new VoiceOTP();
            voiceOTP.awb = awb;
            voiceOTP.drs_id = drs_id;
            voiceOTP.product_type = Constants.OBD_Product_TYPE;
            voiceOTP.employee_code = getDataManager().getEmp_code();
            voiceOTP.message_type = message_type;
            getCompositeDisposable().add(getDataManager().doVoiceOtpApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), voiceOTP).doOnSuccess(verifyOtpResponse -> Log.d(ContentValues.TAG, verifyOtpResponse.toString())).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                setIsLoading(false);
                try {
                    if (response.code == 0) {
                        getNavigator().showError(response.description);
                    } else if (response.code == 1) {
                        getNavigator().showError(response.description);
                    }
                } catch (Exception e) {
                    Logger.e(OBDStartOTPViewModel.class.getName(), e.getMessage());
                }
            }, throwable -> {
                String error;
                try {
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                    getNavigator().showError(error);
                } catch (Exception e) {
                    Logger.e(OBDStopOTPViewModel.class.getName(), e.getMessage());
                    getNavigator().showError(e.getMessage());
                }
            }));
        } catch (Exception e) {
            Logger.e(OBDStopOTPViewModel.class.getName(), e.getMessage());
        }
    }

    public void setQcImageDataForCommit(Activity context, ForwardCommit forwardCommit, String awbNo, String compositeKey, List<String> qcCode, LinkedHashMap<String, String> qcStatusData) {
        dialog = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Commit Is In Progress....");
        dialog.show();
        try{
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(getDataManager().getImages(awbNo).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(imageModels -> {
                List<ForwardCommit.QCItem> qcItemListResponse =  new ArrayList<>();
                ForwardCommit.QCItem qcItem =  new ForwardCommit.QCItem();
                qcItem.setItem_number("1");
                qcItem.setStatus("Pass");

                List<ForwardCommit.Image_response> list_image_responses = new ArrayList<>();

                if (!imageModels.isEmpty()) {
                    for (int i = 0; i < imageModels.size(); i++) {
                        ForwardCommit.Image_response image_response = new ForwardCommit.Image_response();
                        image_response.setImage_id(String.valueOf(imageModels.get(i).getImageId()));
                        image_response.setImage_key(imageModels.get(i).getImageName());
                        list_image_responses.add(image_response);
                    }
                } else {
                    ForwardCommit.Image_response image_response = new ForwardCommit.Image_response();
                    image_response.setImage_id("");
                    image_response.setImage_key("");
                    list_image_responses.add(image_response);
                }
                qcItem.setImage_response(list_image_responses);
                forwardCommit.setImage_response(list_image_responses);

                List<ForwardCommit.QcWizard> qcWizardListResponse = new ArrayList<>();

                if(!qcCode.isEmpty()){
                    for (int i = 0; i < qcCode.size(); i++) {
                        ForwardCommit.QcWizard qcWizardList = new ForwardCommit.QcWizard();
                        qcWizardList.setRvp_qc_code(qcCode.get(i));
                        if(qcStatusData.get(qcCode.get(i)).equalsIgnoreCase("Pass")){
                            qcWizardList.setQc_check_status("1");
                        } else{
                            qcWizardList.setQc_check_status("0");
                        }
                        qcWizardList.setQc_result_value("NONE");
                        qcWizardList.setFe_remarks("");
                        qcWizardListResponse.add(qcWizardList);
                    }
                    qcItem.setQc_wizard(qcWizardListResponse);
                } else{
                    ForwardCommit.QcWizard qcWizardList = new ForwardCommit.QcWizard();
                    qcWizardList.setRvp_qc_code("");
                    qcWizardList.setQc_check_status("");
                    qcWizardList.setQc_result_value("");
                    qcWizardList.setFe_remarks("");
                    qcWizardListResponse.add(qcWizardList);
                    qcItem.setQc_wizard(qcWizardListResponse);
                }

                qcItemListResponse.add(qcItem);
                forwardCommit.setQc_item(qcItemListResponse);
                List<ForwardCommit> forwardCommitList = new ArrayList<>();
                forwardCommitList.add(forwardCommit);
                uploadOBDShipment(awbNo, forwardCommitList, compositeKey);
            }));
        } catch (Exception e){
            dismissProgressDDialog();
            getNavigator().showError("Shipment Not Committed, Try Again");
        }
    }

    public void getQcItemData(ForwardCommit forwardCommit, String awbNo, List<String> qcCode, List<String> qcFailedData, List<String> qualityCheckName) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager().getImages(awbNo).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(imageModels -> {

            List<ForwardCommit.QCItem> qcItemListResponse = new ArrayList<>();
            ForwardCommit.QCItem qcItem = new ForwardCommit.QCItem();
            qcItem.setItem_number("1");
            qcItem.setStatus((qcFailedData == null || qcFailedData.isEmpty()) ? "Pass" : "Fail");
            List<ForwardCommit.Image_response> list_image_responses = new ArrayList<>();

            if (!imageModels.isEmpty()) {
                for (int i = 0; i < imageModels.size(); i++) {
                    ForwardCommit.Image_response image_response = new ForwardCommit.Image_response();
                    image_response.setImage_id(String.valueOf(imageModels.get(i).getImageId()));
                    image_response.setImage_key(String.valueOf(imageModels.get(i).getImageName()));
                    list_image_responses.add(image_response);
                }
            } else {
                ForwardCommit.Image_response image_response = new ForwardCommit.Image_response();
                image_response.setImage_id("");
                image_response.setImage_key("");
                list_image_responses.add(image_response);
            }
            qcItem.setImage_response(list_image_responses);
            forwardCommit.setImage_response(list_image_responses);
            List<ForwardCommit.QcWizard> qcWizardListResponse = new ArrayList<>();

            Log.e("Ganpati_Data", qcFailedData+"");
            Log.e("Ganpati_Size", qcFailedData.size()+"");
            if(!qcCode.isEmpty()){
                for (int i = 0; i < qcCode.size(); i++) {
                    ForwardCommit.QcWizard qcWizardList = new ForwardCommit.QcWizard();
                    if(qcFailedData == null || qcFailedData.isEmpty()){
                        qcWizardList.setRvp_qc_code(qcCode.get(i));
                        qcWizardList.setQc_check_status("1");
                    } else {
                        if(i==qcFailedData.size()){
                            break;
                        }
                        if(qcFailedData.get(i).equalsIgnoreCase("Pass")){
                            qcWizardList.setRvp_qc_code(qcCode.get(i));
                            qcWizardList.setQc_check_status("1");
                            qcWizardList.setQc_result_value("NONE");
                            qcWizardList.setFe_remarks("");
                        } else if (qcFailedData.contains(qualityCheckName.get(i))) {
                            qcWizardList.setRvp_qc_code(qcCode.get(i));
                            qcWizardList.setQc_check_status("0");
                            qcWizardList.setQc_result_value("NONE");
                            qcWizardList.setFe_remarks("");
                        }
                    }
                    qcWizardListResponse.add(qcWizardList);
                }
                qcItem.setQc_wizard(qcWizardListResponse);
            } else{
                ForwardCommit.QcWizard qcWizardList = new ForwardCommit.QcWizard();
                qcWizardList.setRvp_qc_code("");
                qcWizardList.setQc_check_status("");
                qcWizardList.setQc_result_value("");
                qcWizardList.setFe_remarks("");
                qcWizardListResponse.add(qcWizardList);
                qcItem.setQc_wizard(qcWizardListResponse);
            }

            qcItemListResponse.add(qcItem);
            forwardCommit.setQc_item(qcItemListResponse);
            getNavigator().onUndelivered(forwardCommit);
        }));
    }

    public boolean getDCFEDistance() {
        int meter = LocationHelper.getDistanceBetweenPoint(getDataManager().getCurrentLatitude(), getDataManager().getCurrentLongitude(), getDataManager().getDCLatitude(), getDataManager().getDCLongitude());
        if (meter < getDataManager().getDCRANGE()) {
            getNavigator().showError("Shipment Cannot Be Mark Delivered Within DC");
            return false;
        } else {
            return true;
        }
    }

    public String loginDate() {
        return getDataManager().getLoginDate();
    }

    public int getCounterDeliveryRange() {
        double currentLatitude = getDataManager().getCurrentLatitude();
        double currentLongitude = getDataManager().getCurrentLongitude();
        double DcLatitude = getDataManager().getDCLatitude();
        double DcLongitude = getDataManager().getDCLongitude();
        return LocationHelper.getDistanceBetweenPoint(currentLatitude, currentLongitude, DcLatitude, DcLongitude);
    }

    @SuppressLint("CheckResult")
    private void uploadOBDShipment(String awbNo, List<ForwardCommit> forwardCommits, String compositeKey) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        HashMap<String, String> tokens = new HashMap<>();
        tokens.put(Constants.TOKEN, getDataManager().getAuthToken());
        tokens.put(Constants.EMP_CODE, getDataManager().getCode());
        try {
            compositeDisposable.add(getDataManager().doFWDCommitApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), tokens, forwardCommits).subscribeOn(getSchedulerProvider().io()).subscribe(forwardCommitResponse -> {
                if (forwardCommitResponse.getStatus()) {
                    int shipmentStatus;
                    shipmentStatus = Constants.SHIPMENT_DELIVERED_STATUS;
                    getDataManager().updateForwardStatus(compositeKey, shipmentStatus).subscribe(aBoolean -> compositeDisposable.add(getDataManager().deleteSyncedImage(awbNo).subscribe(aBoolean1 ->
                        updateSyncStatusInDRSFWDTable(compositeKey)
                    )));
                }
            }, throwable -> {
                dismissProgressDDialog();
                getNavigator().showError("Shipment Not Delivered Due To Server Issue, Try Again");
            }));
        } catch (Exception e) {
            dismissProgressDDialog();
            getNavigator().showError("Shipment Not Delivered Due To Server Issue, Try Again");
        }
    }

    public void logoutLocal() {
        getDataManager().setTripId("");
        getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT);
        clearAppData();
    }

    private void clearAppData() {
        try {
            getCompositeDisposable().add(getDataManager().deleteAllTables().subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
                try {
                    getDataManager().clearPrefrence();
                    getDataManager().setUserAsLoggedOut();
                } catch (Exception e) {
                    Logger.e(OBDStopOTPViewModel.class.getName(), e.getMessage());
                }
                getNavigator().clearStack();
            }));
        } catch (Exception e) {
            Logger.e(OBDStopOTPViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    private void updateSyncStatusInDRSFWDTable(String composite_key) {
        try{
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(getDataManager().updateSyncStatusFWD(composite_key, GlobalConstant.CommitStatus.CommitSynced).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(aBoolean ->{
                dismissProgressDDialog();
                getNavigator().navigateToSuccessActivity();
            }));
        } catch (Exception e){
            dismissProgressDDialog();
            getNavigator().showError("Shipment Not Delivered Due To Server Issue, Try Again");
        }
    }

    private void dismissProgressDDialog(){
        if (dialog.isShowing()){
            dialog.dismiss();
        }
    }

    public void uploadImageToServer(String imageName, String imageUri, String imageCode, long awbNo, int drsNo) {
        setIsLoading(true);
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
                getCompositeDisposable().add(getDataManager().doImageUploadApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), GlobalConstant.ImageTypeConstants.FWD, headers, map, fileToUpload).doOnSuccess(imageQualityResponse -> Log.d(ContentValues.TAG, imageQualityResponse.toString())).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(imageUploadResponse -> {
                    setIsLoading(false);
                    try {
                        if (imageUploadResponse.getStatus().equalsIgnoreCase("Success")) {
                            saveImageDB(imageUri, imageCode, imageName, imageUploadResponse.getImageId(), GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_COMPLETE, awbNo, drsNo);
                        } else {
                            String errorMessage = imageUploadResponse.getDescription() == null ? "" : imageUploadResponse.getDescription();
                            if (errorMessage.isEmpty()) {
                                getNavigator().showError("Server Response False, Recapture Image");
                            } else {
                                getNavigator().showError(errorMessage);
                            }
                        }
                    } catch (Exception e) {
                        setIsLoading(false);
                        getNavigator().showError("Server Response False, Recapture Image");
                        Logger.e(TAG+"uploadImageServer", String.valueOf(e));
                    }
                }, throwable -> {
                    setIsLoading(false);
                    try {
                        writeErrors(timeStamp, new Exception(throwable));
                        getNavigator().showError("Server Response False, Recapture Image");
                    } catch (Exception e) {
                        Logger.e(TAG+"uploadImageServer", String.valueOf(e));
                    }
                }));
            } catch (Exception e) {
                setIsLoading(false);
                writeErrors(timeStamp, e);
                getNavigator().showError("Server Response False, Recapture Image");
                Logger.e(TAG+"uploadImageServer", String.valueOf(e));
            }
        } catch (Exception e) {
            setIsLoading(false);
            getNavigator().showError("Server Response False, Recapture Image");
            Logger.e(TAG+"uploadImageServer", String.valueOf(e));
        }
    }

    public void saveImageDB(String imageUri, String imageCode, String imageName, int imageId, int sync_status, long awb, int drsId) {
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
            getCompositeDisposable().add(getDataManager().saveImage(imageModel).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean ->{}));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
            Logger.e(TAG+"saveImageDB", String.valueOf(e));
        }
    }
}