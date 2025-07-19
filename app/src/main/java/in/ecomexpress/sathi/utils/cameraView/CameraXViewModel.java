package in.ecomexpress.sathi.utils.cameraView;

import static in.ecomexpress.sathi.ui.drs.forward.details.ForwardDetailViewModel.TAG;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.ui.dashboard.landing.DashboardActivity;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.CryptoUtils;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@HiltViewModel
public class CameraXViewModel extends BaseViewModel<CameraNavigator> {

    private ProgressDialog dialog;

    @Inject
    public CameraXViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);
    }

    public void uploadImageServer(Context context, String imageUrl, String imageCode, String timeStamp) {
        dialog = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
        dialog.setIndeterminate(true);
        dialog.setMessage("Image Uploading....");
        dialog.setCancelable(false);
        dialog.show();

        final long timeStampTag = System.currentTimeMillis();
        writeEvent(timeStampTag, "Event to upload image, create request body and map before upload image. ");
        try {
            File file = new File(imageUrl);
            byte[] bytes = CryptoUtils.decryptFile1(file.toString(), Constants.ENC_DEC_KEY);
            RequestBody mFile = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), mFile);
            RequestBody trip_emp_code = RequestBody.create(MediaType.parse("text/plain"), getDataManager().getCode());
            RequestBody trip_image_ts = RequestBody.create(MediaType.parse("text/plain"), timeStamp);
            RequestBody image_code = RequestBody.create(MediaType.parse("text/plain"), imageCode);
            RequestBody image_name = RequestBody.create(MediaType.parse("text/plain"), file.getName());
            RequestBody image_type = RequestBody.create(MediaType.parse("text/plain"), "Trip");
            Map<String, RequestBody> map = new HashMap<>();
            map.put("image", mFile);
            map.put("trip_emp_code", trip_emp_code);
            map.put("trip_image_ts", trip_image_ts);
            map.put("image_code", image_code);
            map.put("image_name", image_name);
            map.put("image_type", image_type);
            Map<String, String> headers = new HashMap<>();
            headers.put("token", getDataManager().getAuthToken());
            getCompositeDisposable().add(getDataManager().doImageUploadApiCallStartStop(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), headers, map, fileToUpload).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(imageUploadResponse -> {
                writeEvent(timeStampTag, "Successfully get image upload response: Image Name: " + imageUploadResponse.getFileName());
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if(imageUploadResponse == null){
                    getNavigator().onErrorMessage("API Response Null");
                    return;
                }
                if (imageUploadResponse.getStatus().equalsIgnoreCase("Success")) {
                    getNavigator().imageUploadNotify(""+imageUploadResponse.getImageId(),imageUploadResponse.getFileName(),imageUrl);
                } else {
                    if(imageUploadResponse.getDescription() == null){
                        getNavigator().onErrorMessage("API Response False");
                    } else{
                        getNavigator().onErrorMessage(imageUploadResponse.getDescription());
                    }
                }
            }, throwable -> {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                getNavigator().onErrorMessage(throwable.getMessage());
            }));
        } catch (Exception e) {
            getNavigator().onErrorMessage(e.getMessage());
        }
    }

    public void uploadSelfieImageServer(Activity context, String imageUrl) {
        dialog = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
        dialog.setIndeterminate(true);
        dialog.setMessage("Image Uploading....");
        dialog.setCancelable(false);
        dialog.show();

        final long timeStampTag = System.currentTimeMillis();
        writeEvent(timeStampTag, "Event to upload image, create request body and map before upload image. ");
        try {
            File file = new File(imageUrl);
            byte[] bytes = CryptoUtils.decryptFile1(file.toString(), Constants.ENC_DEC_KEY);
            RequestBody mFile = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("selfie_bitmap", file.getName(), mFile);
            RequestBody empCode = RequestBody.create(MediaType.parse("emp_code"), getDataManager().getEmp_code());
            RequestBody sourceCode = RequestBody.create(MediaType.parse("source_code"), "SATHI");
            RequestBody dcCode = RequestBody.create(MediaType.parse("dc_code"),getDataManager().getCode());

            Map<String, RequestBody> map = new HashMap<>();
            map.put("selfie_bitmap", mFile);
            map.put("emp_code", empCode);
            map.put("source_code", sourceCode);
            map.put("dc_code", dcCode);
            Map<String, String> headers = new HashMap<>();
            headers.put("token", getDataManager().getAuthToken());

            getCompositeDisposable().add(getDataManager().doSelfieImageUploadApiCall(getDataManager().getAuthToken(),headers,map, fileToUpload).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(selfieImageResponse -> {
                writeEvent(timeStampTag, "Successfully get image upload response: Image Name: " + selfieImageResponse);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if(selfieImageResponse == null){
                    getNavigator().onErrorMessage("API Response Null");
                    return;
                }
                if (selfieImageResponse.getStatus().equalsIgnoreCase("Success")) {
                    Intent intent = new Intent(context, DashboardActivity.class);
                    context.startActivity(intent);
                    context.finish();
                } else {
                    if(selfieImageResponse.getDescription() == null){
                        getNavigator().onErrorMessage("API Response False");
                    } else{
                        getNavigator().onErrorMessage(selfieImageResponse.getDescription());
                    }
                }
            }, throwable -> {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                getNavigator().onErrorMessage(throwable.getMessage());
            }));
        } catch (Exception e) {
            getNavigator().onErrorMessage(e.getMessage());
        }
    }

    public void getAllApiUrl(){
        try{
            getCompositeDisposable().add(getDataManager().getAllApiUrl().subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(apiUrlData -> {
                for(int i = 0; i < apiUrlData.size(); i++){
                    SathiApplication.hashMapAppUrl.put(apiUrlData.get(i).getApiUrlKey(), apiUrlData.get(i).getApiUrl());
                }
            }));
        } catch(Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
    }
}