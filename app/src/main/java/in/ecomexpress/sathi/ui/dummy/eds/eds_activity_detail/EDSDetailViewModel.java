package in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.DialogInterface;
import androidx.databinding.ObservableField;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.data.eds.EdsCommit;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.local.db.model.ImageModel;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.drs_list.eds.ImageQualityResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.eds.UploadImageRequest;
import in.ecomexpress.sathi.repo.remote.model.drs_list.eds.UploadImageResponse;
import in.ecomexpress.sathi.repo.remote.model.eds.AadharMaskingResponse;
import in.ecomexpress.sathi.repo.remote.model.eds.AadharStatusResponse;
import in.ecomexpress.sathi.repo.remote.model.eds.Get_Status_Masking;
import in.ecomexpress.sathi.repo.remote.model.image.ImageUploadResponse;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.CryptoUtils;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.content.ContentValues.TAG;

import javax.inject.Inject;

@HiltViewModel
public class EDSDetailViewModel extends BaseViewModel<IEDSDetailNavigator> {
    public static ObservableField<String> activityNameCount = new ObservableField<>("Not Defined");
    public static ObservableField<String> activityRunCount = new ObservableField<>("0");
    public static ObservableField<String> activityTotalCount = new ObservableField<>("0");
    public static ObservableField<String> stageActivityCount = new ObservableField<>("0");
    public static ObservableField<String> totalStageCount = new ObservableField<>("0");
    private final ObservableField<List<MasterActivityData>> masterActivityData = new ObservableField<>();
    public ObservableField<EdsWithActivityList> edsWithActivityList = new ObservableField<EdsWithActivityList>();
    public ObservableField<String> awbNo = new ObservableField<>("N/A");
    public ObservableField<String> itemDescription = new ObservableField<>("N/A");
    public ObservableField<String> consigneeName = new ObservableField<>("N/A");
    public ObservableField<String> consigneeAddress = new ObservableField<>("N/A");
    EdsCommit edsCommit;
    LinkedHashMap<Integer, String> activity_code = new LinkedHashMap<>();
    String imageName;
    String imageUri;
    String imageCode;
    String front_image_id;
    String rear_image_id;
    Long awb_no;
    int drs_no;
    String activity;
    Bitmap bitmap;
    int imagePosition;
    int continues_bad_image_count = 0;
    boolean is_adhar_required = false;

    @Inject
    public EDSDetailViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication){
        super(dataManager, schedulerProvider, sathiApplication);
    }

    public void onBackClick(){
        getNavigator().onBack();
    }

    public void onNextClick(){
        getNavigator().onNext(edsCommit);
    }

    public void onCancelClick(){
        getNavigator().onCancel(edsCommit);
    }

    public void setData(EdsWithActivityList edsWithActivityList, List<MasterActivityData> masterActivityData){
        try{
            this.edsWithActivityList.set(edsWithActivityList);
            this.masterActivityData.set(masterActivityData);
            getConsigneeAddress();
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().errorMgs(e.getMessage());
        }
    }

    public ObservableField<String> getConsigneeName(){
        try{
            if(edsWithActivityList.get() != null && edsWithActivityList.get().edsResponse != null)
                consigneeName.set(edsWithActivityList.get().edsResponse.getConsigneeDetail().getName());
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().errorMgs(e.getMessage());
        }
        return consigneeName;
    }

    public ObservableField<String> getItemDescription(){
        try{
            if(edsWithActivityList.get() != null && edsWithActivityList.get().edsResponse != null)
                itemDescription.set(edsWithActivityList.get().edsResponse.getShipmentDetail().getItemDescription());
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().errorMgs(e.getMessage());
        }
        return itemDescription;
    }

    public ObservableField<String> getConsigneeAddress(){
        try{
            if(edsWithActivityList.get() != null && edsWithActivityList.get().edsResponse != null)
                consigneeAddress.set(CommonUtils.nullToEmpty(edsWithActivityList.get().edsResponse.getConsigneeDetail().getAddress().getLine1()) + " " + CommonUtils.nullToEmpty(edsWithActivityList.get().edsResponse.getConsigneeDetail().getAddress().getLine2()) + " " + CommonUtils.nullToEmpty(edsWithActivityList.get().edsResponse.getConsigneeDetail().getAddress().getLine3()) + " " + CommonUtils.nullToEmpty(edsWithActivityList.get().edsResponse.getConsigneeDetail().getAddress().getLine4()) + " " + CommonUtils.nullToEmpty(edsWithActivityList.get().edsResponse.getConsigneeDetail().getAddress().getCity()) + " " + edsWithActivityList.get().edsResponse.getConsigneeDetail().getAddress().getPincode());
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().errorMgs(e.getMessage());
        }
        return consigneeAddress;
    }

    public ObservableField<String> getAwbNo(){
        try{
            if(edsWithActivityList.get() != null && edsWithActivityList.get().edsResponse != null)
                awbNo.set(Long.toString(edsWithActivityList.get().edsResponse.getAwbNo()));
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().errorMgs(e.getMessage());
        }
        return awbNo;
    }

    public void setActivityName(String name){
        try{
            activityNameCount.set(name);
            getActivityNameCount();
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().errorMgs(e.getMessage());
        }
    }

    public void setActivityCode(int pos, String actCode){
        activity_code.put(pos, actCode);
    }

    public ObservableField<String> getActivityNameCount(){
        return activityNameCount;
    }

    public void setActivityNameCount(int count){
        activityRunCount.set(String.valueOf(count));
    }

    public ObservableField<String> getActivityRunCount(){
        return activityRunCount;
    }

    public ObservableField<String> getActivityTotalCount(){
        return activityTotalCount;
    }

    public void setActivityTotalCount(int count){
        activityTotalCount.set(String.valueOf(count));
    }

    public ObservableField<String> getStageActivityCount(){
        return stageActivityCount;
    }

    public void setStageActivityCount(int count){
        try{
            stageActivityCount.set(String.valueOf(count));
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().errorMgs(e.getMessage());
        }
    }

    public ObservableField<String> getTotalStageCount(){
        return totalStageCount;
    }

    public void setTotalStageCount(int mcount){
        try{
            totalStageCount.set(String.valueOf(mcount));
            getTotalStageCount();
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().errorMgs(e.getMessage());
        }
    }

    @SuppressLint("CheckResult")
    public void upLoadAwsImage(String imageName, String imageUri, String imageCode, long awbNo, int drs_no, String activity_code, Bitmap bitmap){
        try{
            if(getDataManager().getEDSRealTimeSync().equalsIgnoreCase("true")){
                getNavigator().showProgress();
                this.imageName = imageName;
                uploadEdsImage(awbNo, drs_no, activity_code, bitmap, imageUri);
            } else{
                uploadEdsImage(awbNo, drs_no, activity_code, bitmap, imageUri);
            }
            this.imageUri = imageUri;
            this.imageCode = imageCode;
            this.awb_no = awbNo;
            this.drs_no = drs_no;
            this.activity = activity_code;
            this.bitmap = bitmap;
            getDataManager().setActivityCode(activity_code);
            // saveImageDB(imageName, imageUri, imageCode);
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().errorMgs(e.getMessage());
        }
    }

    public void saveImage(String imageName, String imageUri, String imageCode, int image_id){
        saveImageDB(imageName, imageUri, imageCode, image_id);
    }

    File createFileFromBitmap(Bitmap old_bitmap) throws Exception{
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + Calendar.getInstance().getTimeInMillis() + "_sathi.png");
        f.createNewFile();
        //Convert bitmap to byte array
        Bitmap bitmap = old_bitmap;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();
        //write the bytes in file
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
        return f;
    }

    public void uploadEdsImage(long awbNo, int drs_no, String activity_code, Bitmap bitmap, String imageUri){
        if(getDataManager().isDownloadAPKIsInProcess() > 0){
            getNavigator().onHandleError("An updated apk is in download. please wait for some time. ");
            return;
        }
        // setIsLoading(true);
        //getNavigator().showProgress();
        final long timeStamp = Calendar.getInstance().getTimeInMillis();
        try{
            // File image_file = createFileFromBitmap(bitmap);
            File image_file = new File(imageUri);
            File newFrontFile = new File(getNavigator().getContextProvider().getFilesDir() ,imageName);
            newFrontFile.createNewFile();
            CommonUtils.copy(image_file, newFrontFile);
            byte[] bytes = CryptoUtils.decryptFile1(image_file.toString(), Constants.ENC_DEC_KEY);
            RequestBody mFile = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", image_file.getName(), mFile);
            //RequestBody mFile = RequestBody.create(MediaType.parse("multipart/form-data"), image_file);
            //MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", image_file.getName(), mFile);
            RequestBody awb = RequestBody.create(MultipartBody.FORM, String.valueOf(awbNo));
            RequestBody drdid = RequestBody.create(MultipartBody.FORM, String.valueOf(drs_no));
            //RequestBody drdid = RequestBody.create(MultipartBody.FORM, String.valueOf("44"));
            RequestBody activitycode = RequestBody.create(MultipartBody.FORM, activity_code);
            RequestBody remark = RequestBody.create(MultipartBody.FORM, "");
            Map<String, RequestBody> map = new HashMap<>();
            map.put("awb", awb);
            map.put("drs_id", drdid);
            map.put("activity_code", activitycode);
            map.put("text", remark);
            Log.e("map", map.toString());
            UploadImageRequest uploadImageRequest = new UploadImageRequest(awbNo, drs_no, activity_code);
            writeRestAPIRequst(timeStamp, uploadImageRequest);
            getCompositeDisposable().add(getDataManager().doUploadImageApiCall(getDataManager().getEcomRegion(),map, fileToUpload).doOnSuccess(new Consumer<UploadImageResponse>() {
                @Override
                public void accept(UploadImageResponse uploadImageResponse){
                    writeRestAPIResponse(timeStamp, uploadImageResponse);
                    Log.e(TAG, uploadImageResponse.toString());
                    //APK update working fine.
                }
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<UploadImageResponse>() {
                @Override
                public void accept(UploadImageResponse response){
                    Log.e(TAG, "UploadImageResponse: " + response.toString());
                    // EDSDetailViewModel.this.setIsLoading(false);
                    try{
                        if(response.getResponseCode() == 200){
                            getNavigator().onProgressFinish();
                            // getNavigator().setBitmapOnImageId();
                            getDataManager().setImageQualityId(response.getImageResponse().getImage_id());
                            saveImageId(response, activity_code);
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }, throwable -> {
                // setIsLoading(false);
                getNavigator().onProgressFinish();
                String error;
                try{
                    writeErrors(timeStamp, new Exception(throwable));
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                    getNavigator().showErrorMessage(error.contains("HTTP 500"));
                } catch(Exception e){
                    e.printStackTrace();
                }
            }));
        } catch(Exception e){
            e.printStackTrace();
            writeErrors(timeStamp, e);
            // Log.e(TAG, e.getCause().getStackTrace().toString());
            // setIsLoading(false);
            getNavigator().onProgressFinish();
            if(e instanceof Throwable){
                //getNavigator().onProgressFinishCall();
                getNavigator().onHandleError(new RestApiErrorHandler(e.fillInStackTrace()).getErrorDetails().getEResponse().getDescription());
            }
        }
    }

    private void saveImageId(UploadImageResponse response, String activity_code){
        saveImageResponse(response, activity_code);
        //        Observable.fromCallable(() -> {
        //
        //            return false;
        //        }).subscribeOn(Schedulers.io())
        //                .observeOn(Schedulers.io())
        //                .subscribe((result) -> {
        //
        //                });
    }

    private void saveImageResponse(UploadImageResponse response, String activity_code){
        if(activity_code.startsWith("AC") && activity_code.endsWith("IMAGE")){
            getNavigator().showProgressDelay();
        }
    }

    private void saveImageResponseDirect(String imageName, int image_id){
        updateRecord(image_id, imageName);
    }

    private void updateRecord(int image_id, String imageName){
        getCompositeDisposable().add(getDataManager().updateImageStatus(imageName, 2).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception{
                //
            }
        }));
        getCompositeDisposable().add(getDataManager().updateImageID(imageName, image_id).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception{
            }
        }));
    }

    public void saveImageDB(String name, String imageUri, String imageCode, int image_id){
        try{
            ImageModel imageModel = new ImageModel();
            imageModel.setDraNo("" + edsWithActivityList.get().edsResponse.getDrsNo());
            imageModel.setAwbNo("" + edsWithActivityList.get().edsResponse.getAwbNo());
            imageModel.setImageName(name);
            imageModel.setImage(imageUri);
            imageModel.setImageCode(imageCode);
            imageModel.setStatus(2);
            imageModel.setImageId(image_id);
            imageModel.setDate(Calendar.getInstance().getTimeInMillis());
            imageModel.setShipmentType(GlobalConstant.ShipmentTypeConstants.EDS);
            imageModel.setImageType(GlobalConstant.ImageTypeConstants.EDS);
            getCompositeDisposable().add(getDataManager().saveImage(imageModel).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception{
                    saveImageResponseDirect(imageName, image_id);
                    getNavigator().setBitmap();
                    //
                }
            }));
        } catch(Exception e){
            getNavigator().errorMgs(e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveAadharImageDB(int status, String name, String imageUri, String imageCode, int image_id){
        try{
            ImageModel imageModel = new ImageModel();
            imageModel.setDraNo("" + edsWithActivityList.get().edsResponse.getDrsNo());
            imageModel.setAwbNo("" + edsWithActivityList.get().edsResponse.getAwbNo());
            imageModel.setImageName(name);
            imageModel.setImage(imageUri);
            imageModel.setImageCode(imageCode);
            imageModel.setStatus(status);
            imageModel.setImageId(image_id);
            imageModel.setDate(Calendar.getInstance().getTimeInMillis());
            imageModel.setShipmentType(GlobalConstant.ShipmentTypeConstants.EDS);
            imageModel.setImageType(GlobalConstant.ImageTypeConstants.EDS);
            getCompositeDisposable().add(getDataManager().saveImage(imageModel).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception{
                    saveImageResponseDirect(imageName, image_id);
                    getNavigator().setBitmap();
                    //
                }
            }));
        } catch(Exception e){
            getNavigator().errorMgs(e.getMessage());
            e.printStackTrace();
        }
    }

    void countDown(){
        new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished){
                getNavigator().onProgressTimer((int) millisUntilFinished / 1000);
            }

            public void onFinish(){
                getNavigator().onProgressFinishCount();
            }
        }.start();
    }

    public void sendScanResult(String scannedData){
        getNavigator().sendScanResult(scannedData);
    }

    public void getImageResult(){
        // setIsLoading(true);
        getNavigator().showProgress();
        final long timeStamp = Calendar.getInstance().getTimeInMillis();
        try{
            getCompositeDisposable().add(getDataManager().getImageQuality(getDataManager().getEcomRegion(),getDataManager().getImageQualityId()).doOnSuccess(new Consumer<ImageQualityResponse>() {
                @Override
                public void accept(ImageQualityResponse imageQualityResponse){
                    Log.d(TAG, imageQualityResponse.toString());
                }
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<ImageQualityResponse>() {
                @Override
                public void accept(ImageQualityResponse imageQualityResponse){
                    //  EDSDetailViewModel.this.setIsLoading(false);
                    getNavigator().onProgressFinish();
                    try{
                        if(imageQualityResponse.getImageResponse().getImageQuality().equalsIgnoreCase("BAD")){
                            continues_bad_image_count = continues_bad_image_count + 1;
                            if(continues_bad_image_count == 2){
                                if(getDataManager().getEDSRealTimeSync().equalsIgnoreCase("true")){
                                    uploadImageServer(imageQualityResponse.getImageResponse().getImageKey(), imageUri, imageCode, awb_no, drs_no, activity, bitmap, "NONE");
                                } else{
                                    getNavigator().setBitmap();
                                    saveImageDB(imageQualityResponse.getImageResponse().getImageKey(), imageUri, imageCode, 0);
                                }
                            } else{
                                getNavigator().onHandleError("Image Quality is BAD. Please capture image again.");
                                getNavigator().removeBitmap();
                            }
                        } else{
                            continues_bad_image_count = 0;
                            if(getDataManager().getEDSRealTimeSync().equalsIgnoreCase("true")){
                                uploadImageServer(imageQualityResponse.getImageResponse().getImageKey(), imageUri, imageCode, awb_no, drs_no, activity, bitmap, "NONE");
                            } else{
                                getNavigator().setBitmap();
                                saveImageDB(imageQualityResponse.getImageResponse().getImageKey(), imageUri, imageCode, 0);
                            }
                        }
                    } catch(Exception e){
                        getNavigator().onHandleError("Image Quality is BAD(Exception).Try again..");
                        getNavigator().removeBitmap();
                        //getNavigator().onProgressFinish();
                    }
                }
            }, throwable -> {
                //setIsLoading(false);
                getNavigator().onProgressFinish();
                String error;
                try{
                    writeErrors(timeStamp, new Exception(throwable));
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                    getNavigator().onHandleError(error);
                } catch(Exception e){
                    e.printStackTrace();
                }
            }));
        } catch(Exception e){
            getNavigator().errorMgs(e.getMessage());
            getNavigator().onProgressFinish();
            writeErrors(timeStamp, e);
            e.printStackTrace();
            //            Log.e(TAG, e.getCause().getStackTrace().toString());
            //  setIsLoading(false);
            if(e instanceof Throwable){
                getNavigator().onHandleError(new RestApiErrorHandler(e.fillInStackTrace()).getErrorDetails().getEResponse().getDescription());
            }
        }
    }

    public void UploadImage(String imageName, String imageUri, String imageCode, long awbNo, int drsno, String activity_code, Bitmap bitmap, String card_type){
        if(getDataManager().getEDSRealTimeSync().equalsIgnoreCase("true")){
            getNavigator().showProgress();
            uploadImageServer(imageName, imageUri, imageCode, awbNo, drsno, activity_code, bitmap, card_type);
        } else{
            saveImage(imageName, imageUri, imageCode, 0);
            getNavigator().setBitmapOnImageId();
        }
    }

    public void uploadImageServer(String imageName, String imageUri, String imageCode, long awbNo, int drsno, String activity_code, Bitmap bitmap, String card_type){
        //  final long timeStampUploadImageToServer = System.currentTimeMillis();
        HashMap<String, Long> timeStampTagging = new HashMap<>();
        try{
            final long timeStamp = Calendar.getInstance().getTimeInMillis();
            File image_file = new File(imageUri);

            byte[] bytes = CryptoUtils.decryptFile1(image_file.toString(), Constants.ENC_DEC_KEY);
            //            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            //            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            //            byte[] byteArray = stream.toByteArray();
            RequestBody mFile = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", image_file.getName(), mFile);
            RequestBody awb_no = RequestBody.create(MultipartBody.FORM, String.valueOf(awbNo));
            RequestBody drs_no = RequestBody.create(MultipartBody.FORM, String.valueOf(drsno));
            RequestBody image_code = RequestBody.create(MultipartBody.FORM, String.valueOf(imageCode));
            RequestBody image_name = RequestBody.create(MultipartBody.FORM, String.valueOf(imageName));
            RequestBody image_type = RequestBody.create(MultipartBody.FORM, "EDS");
            RequestBody cardtype = RequestBody.create(MultipartBody.FORM, String.valueOf(card_type));
            RequestBody isclassificationrequired;
            if(card_type.equalsIgnoreCase("NONE")){
                isclassificationrequired = RequestBody.create(MultipartBody.FORM, String.valueOf(false));
            } else{
                isclassificationrequired = RequestBody.create(MultipartBody.FORM, String.valueOf(true));
            }
            Map<String, RequestBody> map = new HashMap<>();
            map.put("image", mFile);
            map.put("awb_no", awb_no);
            map.put("drs_no", drs_no);
            map.put("image_code", image_code);
            map.put("image_name", image_name);
            map.put("image_type", image_type);
            map.put("card_type",  cardtype);
            map.put("is_classification_required", isclassificationrequired);
            Map<String, String> headers = new HashMap<>();
            headers.put("token", getDataManager().getAuthToken());
            headers.put("Accept", "application/json");
            Log.e("EDS map", map + "");
            //AppLogs.Companion.writeRestApiRequest(timeStamp, map);
            try{
                getCompositeDisposable().add(getDataManager().doImageUploadApiCall(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), "EDS", headers, map, fileToUpload).doOnSuccess(new Consumer<ImageUploadResponse>() {
                    @Override
                    public void accept(ImageUploadResponse imageQualityResponse){
                        Log.d(TAG, imageQualityResponse.toString());
                        if(!String.valueOf(card_type).equals("NONE")){
                            if(imageQualityResponse.getCard_status().equalsIgnoreCase("0")){
                                getNavigator().onHandleError("This is not " + card_type);
                            } else if(imageQualityResponse.getCard_status().equalsIgnoreCase("1")){
                                getNavigator().onHandleError(card_type + " is matched successfully");
                            }
                        }
                    }
                }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<ImageUploadResponse>() {
                    @Override
                    public void accept(ImageUploadResponse imageUploadResponse){
                        //  EDSDetailViewModel.this.setIsLoading(false);
                        try{
                            getNavigator().onProgressFinishCall();
                            //setIsLoading(false);
                            // saveImageId(imageUploadResponse,activity_code);
                            if(imageUploadResponse.getImageId() == null){
                                getNavigator().onHandleError("Image Corrupted while uploading.Please try Again");
                            } else{
                                saveImage(imageName, imageUri, imageCode, imageUploadResponse.getImageId());
                            }
                        } catch(Exception e){
                            getNavigator().onProgressFinishCall();
                            e.printStackTrace();
                        }
                    }
                }, throwable -> {
                    // setIsLoading(false);
                    getNavigator().onProgressFinishCall();
                    String error;
                    try{
                        writeErrors(timeStamp, new Exception(throwable));
                        error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                        getNavigator().onHandleError(error);
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }));
            } catch(Exception e){
                getNavigator().errorMgs(e.getMessage());
                writeErrors(timeStamp, e);
                e.printStackTrace();
                //            Log.e(TAG, e.getCause().getStackTrace().toString());
                //setIsLoading(false);
                getNavigator().onProgressFinishCall();
                if(e instanceof Throwable){
                    getNavigator().onHandleError(new RestApiErrorHandler(e.fillInStackTrace()).getErrorDetails().getEResponse().getDescription());
                }
            }
        } catch(Exception ex){
            ex.printStackTrace();
            getNavigator().onProgressFinishCall();
            // setIsLoading(false);
            Log.e("Image Sync exception", ex.toString());
        }
    }

    public String convertImageToBase64(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
        byte[] byteArrayImage = baos.toByteArray();
        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        return encodedImage;
    }

    public void setImagePosition(int position){
        this.imagePosition = position;
    }

    int getImagePosition(){
        return this.imagePosition;
    }
    // upload Aadhar images

    public void uploadAadharImageServer(String front_image_uri, String rear_image_uri, String front_image_code, String rear_image_code, String front_image_name, String rear_image_name){
        //  final long timeStampUploadImageToServer = System.currentTimeMillis();
        setIsLoading(true);
        HashMap<String, Long> timeStampTagging = new HashMap<>();
        try{
            final long timeStamp = Calendar.getInstance().getTimeInMillis();

            File front_image = new File(front_image_uri);
            File rear_image = new File(rear_image_uri);

            File newFrontFile = new File(getNavigator().getContextProvider().getFilesDir() ,front_image_name);
            newFrontFile.createNewFile();
            CommonUtils.copy(front_image, newFrontFile);


            File newRearFile = new File(getNavigator().getContextProvider().getFilesDir(),rear_image_name);
            newRearFile.createNewFile();
            CommonUtils.copy(rear_image, newRearFile);


            byte[] front_bytes = CryptoUtils.decryptFile1(newFrontFile.toString(), Constants.ENC_DEC_KEY);
            byte[] rear_bytes = CryptoUtils.decryptFile1(newRearFile.toString(), Constants.ENC_DEC_KEY);
            RequestBody front_mFile = RequestBody.create(MediaType.parse("application/octet-stream"), front_bytes);
            RequestBody rear_mFile = RequestBody.create(MediaType.parse("application/octet-stream"), rear_bytes);
            MultipartBody.Part front_fileToUpload = MultipartBody.Part.createFormData("upload_front", newFrontFile.getName(), front_mFile);
            MultipartBody.Part rear_fileToUpload = MultipartBody.Part.createFormData("upload_back", newRearFile.getName(), rear_mFile);
            RequestBody front_image_type = RequestBody.create(MultipartBody.FORM, String.valueOf(front_image_code));
            RequestBody rear_image_type = RequestBody.create(MultipartBody.FORM, String.valueOf(rear_image_code));
            RequestBody new_aadhar = RequestBody.create(MultipartBody.FORM, "true");
            Map<String, RequestBody> map = new HashMap<>();
            map.put("upload_front", front_mFile);
            map.put("upload_back", rear_mFile);
            map.put("front_image_type", front_image_type);
            map.put("back_image_type", rear_image_type);
            map.put("new_aadhar", new_aadhar);
            Map<String, String> headers = new HashMap<>();
            headers.put("token", getDataManager().getAuthToken());
            headers.put("Accept", "application/json");
            Log.e("EDS map", map + "");
            // AppLogs.Companion.writeRestApiRequest(timeStamp, map);
            try{
                getCompositeDisposable().add(getDataManager().doAadharMaskigImageUploadApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(),"EDS", headers, map, front_fileToUpload, rear_fileToUpload).doOnSuccess(new Consumer<AadharMaskingResponse>() {
                    @Override
                    public void accept(AadharMaskingResponse aadharMaskingResponse){
                        Log.d(TAG, aadharMaskingResponse.toString());
                    }
                }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<AadharMaskingResponse>() {
                    @Override
                    public void accept(AadharMaskingResponse aadharMaskingResponse){
                        //  EDSDetailViewModel.this.setIsLoading(false);
                        try{
                            setIsLoading(false);
                            if(aadharMaskingResponse.getStatus().equalsIgnoreCase("true")){
                                front_image_id = aadharMaskingResponse.getResponse_data()[0].getImage_id();
                                rear_image_id = aadharMaskingResponse.getResponse_data()[1].getImage_id();
                                if(front_image_id.equalsIgnoreCase("") || rear_image_id.equalsIgnoreCase("")){
                                    getNavigator().onHandleError("Some technical issue faced. Please upload images again!");
                                }
                                getDataManager().setAadharFrontImage(front_image_id);
                                getDataManager().setAadharRearImage(rear_image_id);
                                getDataManager().setAadharFrontImageName("AADHAR_FRONT_IMAGE");
                                getDataManager().setAadharRearImageName("AADHAR_REAR_IMAGE");
                                getDataManager().setAadharStatusCode(0);
                                saveAadharImageDB(2, front_image_name, front_image_uri, front_image_code, Integer.parseInt(aadharMaskingResponse.getResponse_data()[0].getImage_id()));
                                saveAadharImageDB(2, rear_image_name, rear_image_uri, rear_image_code, Integer.parseInt(aadharMaskingResponse.getResponse_data()[1].getImage_id()));
                                // saveImage(front_image_name, front_image_uri, front_image_code, Integer.parseInt(aadharMaskingResponse.getResponse_data()[0].getImage_id()));
                                //saveImage(rear_image_name, rear_image_uri, rear_image_code, Integer.parseInt(aadharMaskingResponse.getResponse_data()[1].getImage_id()));
                                getNavigator().onHandleError(aadharMaskingResponse.getDescription());
                            } else{
                                getNavigator().onHandleError(aadharMaskingResponse.getDescription());
                            }
                        } catch(Exception e){
                            //getNavigator().onProgressFinishCall();
                            getNavigator().onHandleError(e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                    }
                }, throwable -> {
                    setIsLoading(false);
                    //getNavigator().onProgressFinishCall();
                    String error;
                    try{
                        //    setIsLoading(false);
                        writeErrors(timeStamp, new Exception(throwable));
                        error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                        getNavigator().onHandleError(error);
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }));
            } catch(Exception e){
                getNavigator().errorMgs(e.getMessage());
                writeErrors(timeStamp, e);
                e.printStackTrace();
                //            Log.e(TAG, e.getCause().getStackTrace().toString());
                setIsLoading(false);
                //getNavigator().onProgressFinishCall();
                if(e instanceof Throwable){
                    getNavigator().onHandleError(new RestApiErrorHandler(e.fillInStackTrace()).getErrorDetails().getEResponse().getDescription());
                }
            }
        } catch(Exception ex){
            ex.printStackTrace();
            //getNavigator().onProgressFinishCall();
            setIsLoading(false);
            Log.e("Image Sync exception", ex.toString());
        }
    }

    public void getStatusAadharMasking(){
        setIsLoading(true);
        try{
            Get_Status_Masking request = new Get_Status_Masking(front_image_id, rear_image_id);
            final long timeStamp = System.currentTimeMillis();
            writeRestAPIRequst(timeStamp, request);
            getCompositeDisposable().add(getDataManager().doGetStatusAadharMasking(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), request).doOnSuccess(new Consumer<AadharStatusResponse>() {
                @Override
                public void accept(AadharStatusResponse aadharStatusResponse){
                    Log.d(TAG, aadharStatusResponse.toString());
                    writeRestAPIResponse(timeStamp, aadharStatusResponse);
                }
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<AadharStatusResponse>() {
                @Override
                public void accept(AadharStatusResponse response){
                    setIsLoading(false);
                    try{
                        if(response.getStatus_code().equalsIgnoreCase("0")){ // under process
                            getDataManager().setAadharStatusCode(0);
                            showAadharStatusDelayDialog(response.getDescription());
                        } else if(response.getStatus_code().equalsIgnoreCase("1")){ // success
                            getDataManager().setAadharStatusCode(0); // don't change this. its correct.
                            getDataManager().setAadharStatus(true);
                            getNavigator().onHandleError(response.getDescription());
                        } else{ // fail
                            getDataManager().setAadharStatusCode(2);
                            getNavigator().setAadharToCameraImages();
                            getDataManager().setAadharFrontImage("");
                            getDataManager().setAadharRearImage("");
                            getNavigator().setAadharURIToBlank();
                            getNavigator().onHandleError(response.getDescription());
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }, throwable -> {
                setIsLoading(false);
                String error;
                try{
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                    getNavigator().showErrorMessage(error.contains("HTTP 500 "));
                } catch(NullPointerException e){
                    e.printStackTrace();
                }
            }));
        } catch(Exception e){
            Log.e(TAG, e.getMessage());
            getNavigator().onHandleError(e.getMessage());
            setIsLoading(false);
            if(e instanceof Throwable){
                getNavigator().onHandleError(new RestApiErrorHandler(e.fillInStackTrace()).getErrorDetails().getEResponse().getDescription());
            }
        }
    }

    public void showAadharStatusDelayDialog(String message){
        getNavigator().getContextProvider().runOnUiThread(new Runnable() {
            @Override
            public void run(){
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getNavigator().getContextProvider()).setMessage(message).setPositiveButton("Wait", null);
                Dialog dialog = alertDialog.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    private final long AUTO_DISMISS_MILLIS = Long.parseLong(getDataManager().getAadharStatusInterval()) * 1000;

                    @Override
                    public void onShow(final DialogInterface dialog){
                        final Button defaultButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        defaultButton.setEnabled(false);
                        final CharSequence negativeButtonText = defaultButton.getText();
                        new CountDownTimer(AUTO_DISMISS_MILLIS, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished){
                                defaultButton.setText(String.format(Locale.getDefault(), "%s (%d)", negativeButtonText, TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1 //add one so it never displays zero
                                ));
                            }

                            @Override
                            public void onFinish(){
                                if(((AlertDialog) dialog).isShowing()){
                                    dialog.dismiss();
                                }
                            }
                        }.start();
                    }
                });
                dialog.setCancelable(false);
                dialog.show();
            }
        });
    }
    //    public void uploadCardDetectionImageToServer(String imageName, String imageUri, String imageCode, long awbNo, int drs_no, String activity_code, Bitmap bitmap, boolean uddan_flag) {
    //        setIsLoading(true);
    //        MultipartBody.Part fileToUpload = null;
    //        RequestBody mFile = null;
    //        if(imageUri != null)
    //        {
    //            File image_file = new File(imageUri);
    //            byte[] bytes = CryptoUtils.decryptFile1(image_file.toString(), Constants.ENC_DEC_KEY);
    //            mFile = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
    //            fileToUpload = MultipartBody.Part.createFormData("file", image_file.getName(), mFile);
    //        }
    //
    //        Map<String, RequestBody> map = new HashMap<>();
    //        if(mFile != null)
    //        {
    //            map.put("file",    mFile);
    //        }
    //
    //        Map<String, String> headers = new HashMap<>();
    //        headers.put("token", getDataManager().getAuthToken());
    //        headers.put("Accept", "application/json");
    //        try {
    //            getCompositeDisposable()
    //                    .add(getDataManager()
    //                            .doCardDetectionResponse(getDataManager().getAuthToken(), headers, map, fileToUpload)
    //                            .doOnSuccess(new Consumer<CardDetectionResponse>() {
    //                                @Override
    //                                public void accept(CardDetectionResponse cardDetectionResponse) {
    //                                    setIsLoading(false);
    //                                }
    //                            })
    //                            .subscribeOn(getSchedulerProvider().io())
    //                            .observeOn(getSchedulerProvider().ui())
    //                            .subscribe(new Consumer<CardDetectionResponse>() {
    //                                @Override
    //                                public void accept(CardDetectionResponse cardDetectionResponse) {
    //                                    setIsLoading(false);
    //                                    getNavigator().uploadEdsImages(imageName, imageUri, imageCode, awbNo, drs_no, activity_code, bitmap, uddan_flag);
    //                                }
    //
    //                            }, throwable -> {
    //                                getNavigator().errorMgs(throwable.getLocalizedMessage());
    //                                setIsLoading(false);
    //                                try {
    //
    //                                } catch (Exception e) {
    //                                    e.printStackTrace();
    //
    //                                }
    //                            }));
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //    }
}
