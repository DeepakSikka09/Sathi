package in.ecomexpress.sathi.ui.dummy.eds.undeilvered_eds;

import static in.ecomexpress.sathi.utils.Constants.DISTANCE_API_KEY;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;

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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.data.callbridge.CallApiRequest;
import in.ecomexpress.sathi.repo.local.data.eds.EDSActivityImageRequest;
import in.ecomexpress.sathi.repo.local.data.eds.EDSActivityResponseWizard;
import in.ecomexpress.sathi.repo.local.data.eds.EDSCommitResponse;
import in.ecomexpress.sathi.repo.local.data.eds.EdsCommit;
import in.ecomexpress.sathi.repo.local.data.fwd.FeRescheduleInfo;
import in.ecomexpress.sathi.repo.local.data.commit.PushApi;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.local.db.model.ImageModel;
import in.ecomexpress.sathi.repo.local.db.model.Remark;
import in.ecomexpress.sathi.repo.local.db.model.RescheduleEdsD;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.ErrorResponse;
import in.ecomexpress.sathi.repo.remote.model.call.Call;
import in.ecomexpress.sathi.repo.remote.model.consignee_profile.ProfileFound;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.ForwardCallResponse;
import in.ecomexpress.sathi.repo.remote.model.forward.verifyOTP.VerifyOTPResponse;
import in.ecomexpress.sathi.repo.remote.model.image.ImageUploadResponse;
import in.ecomexpress.sathi.repo.remote.model.masterdata.CallbridgeConfiguration;
import in.ecomexpress.sathi.repo.remote.model.masterdata.EDSMasterDataAttributeResponse;
import in.ecomexpress.sathi.repo.remote.model.masterdata.EDSReasonCodeMaster;
import in.ecomexpress.sathi.repo.remote.model.otp.resendotp.GenerateUDOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.otp.resendotp.ResendOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.otp.resendotp.ResendOtpResponse;
import in.ecomexpress.sathi.repo.remote.model.otp.verifyotp.VerifyUDOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.reschedule.ReshceduleDetailsResponse;
import in.ecomexpress.sathi.repo.remote.model.reschedule.ResheduleDetailsRequest;
import in.ecomexpress.sathi.repo.remote.model.voice_otp.VoiceOTP;
import in.ecomexpress.sathi.repo.remote.model.voice_otp.VoiceOTPResponse;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.CryptoUtils;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.LocationHelper;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.exceptions.OnErrorNotImplementedException;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by dhananjayk on 22-11-2018.
 */
@HiltViewModel
public class UndeliveredViewModel extends BaseViewModel<IUndeliveredNavigator> {
    public ObservableArrayList<String> spinnerName = new ObservableArrayList<>();
    public ObservableArrayList<String> slotSpinner = new ObservableArrayList<>();
    public ObservableArrayList<String> slotCode = new ObservableArrayList<>();
    public ObservableArrayList<Integer> spinnerCode = new ObservableArrayList<>();
    public ObservableField<Long> awb = new ObservableField<>();
    private final ObservableField<EdsWithActivityList> edsWithActivityList = new ObservableField<EdsWithActivityList>();
    public final ObservableField<String> consigneeContactNumber = new ObservableField<>("");
    public ObservableArrayList<String> parentGroup = new ObservableArrayList<>();
    public ObservableArrayList<String> childGroupName = new ObservableArrayList<>();
    public ObservableArrayList<EDSReasonCodeMaster> childEDSReasonCodeMaster = new ObservableArrayList<>();
    public int call_alert_number = 0;
    public boolean isCallRecursionDailogRunning = true;
    CallbridgeConfiguration mymasterDataReasonCodeResponse = null;
    List<EDSReasonCodeMaster> edsReasonCode = new ArrayList<>();
    int isCall = 0;
    List<ImageModel> mimageModels = new ArrayList<>();
    // For parent child
    Map<String, List<EDSReasonCodeMaster>> childGroup;
    List<String> parentGroupSpinnerValues = new ArrayList<String>();
    List<String> childGroupSpinnerValues = new ArrayList<String>();
    List<String> vehicleTypeSpinnerValues = new ArrayList<String>();
    List<RescheduleEdsD> rescheduleEdsDS = new ArrayList<>();
    Dialog calldialog = null;
    boolean isStopRecursion = false;
    long request_time = 0l;
    long response_time = 0l;
    private final List<EDSActivityImageRequest> edsActivityImageRequestList = new ArrayList<EDSActivityImageRequest>();
    private EDSResponse edsResponse;
    private EdsCommit edsCommit;
    ProgressDialog dialog;
    int counter_skip = 0;
    String ud_otp_commit_status = "NONE";
    String rd_otp_commit_status = "NONE";
    public ObservableField<Boolean> ud_otp_verified_status = new ObservableField<>(false);
    public ObservableField<Boolean> rd_otp_verified_status = new ObservableField<>(false);
    public ObservableField<String> ud_otp_commit_status_field = new ObservableField<>("NONE");
    public ObservableField<String> rd_otp_commit_status_field = new ObservableField<>("NONE");

    @Inject
    public UndeliveredViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication){
        super(dataManager, schedulerProvider, sathiApplication);
        rescheduleEdsDS.clear();
    }

    public void setedsCommit(EDSResponse edsResponse){
        this.edsResponse = edsResponse;
    }

    public String loginDate(){
        return getDataManager().getLoginDate();
    }

    public void onBackClick(){
        getNavigator().onBackClick();
    }

    public void getEdsListTask(String composite_key) {
        try {

            getCompositeDisposable().add(getDataManager().
                    getEdsWithActivityList(composite_key).
                    subscribeOn(getSchedulerProvider().io()).
                    observeOn(getSchedulerProvider().ui()).
                    subscribe(new Consumer<EdsWithActivityList>() {
                        @Override
                        public void accept(EdsWithActivityList edsWithActivityList) throws Exception {
                            UndeliveredViewModel.this.edsWithActivityList.set(edsWithActivityList);

                        }

                    }));

        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
    }
    public void getListDetailNew(HashSet<String> filter){
        try{
            getCompositeDisposable().add(getDataManager().doEDSReasonMasterCodeALL(filter).subscribeOn(getSchedulerProvider().io()).
                    observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<List<EDSReasonCodeMaster>>() {
                @Override
                public void accept(List<EDSReasonCodeMaster> edsReasonCodeMasters) throws Exception{
                    // edsReasonCode.add(0, getselectData());
                    if(getDataManager().getEDSReasonCodeFlag().equalsIgnoreCase("true")){
                        getNavigator().visible(true);
                        childGroup = new HashMap<String, List<EDSReasonCodeMaster>>();
                        childGroup = groupRecord(edsReasonCodeMasters);
                    } else{
                        getNavigator().visible(false);
                        edsReasonCode.add(0, getselectData());
                        for(EDSReasonCodeMaster edsReasonCodeMaster : edsReasonCodeMasters){
                            edsReasonCode.add(edsReasonCodeMaster);
                        }
                        for(EDSReasonCodeMaster edsReasonCodeMaster : edsReasonCode){
                            //  spinnerName.add(edsReasonCodeMaster.getReasonMessage());
                            vehicleTypeSpinnerValues.add(edsReasonCodeMaster.getReasonMessage());
                        }
                        getNavigator().setVehicleTypeSpinnerValues(vehicleTypeSpinnerValues);
                    }
                }
            }, throwable -> {
                setIsLoading(false);
                getNavigator().showError(throwable.getMessage());
            }));
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
    }

    public void callApi(String nyka,String awb_number, String drs_id){

        if (nyka.equalsIgnoreCase("true")){
        try{
            request_time = System.currentTimeMillis();
            UndeliveredViewModel.this.setIsLoading(true);
            getCompositeDisposable().add(getDataManager().doForwardCallStatusApiCall(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), call_alert_number, getDataManager().getEmp_code(), awb_number, drs_id, getDataManager().getShipperId()).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).
                    subscribe(new Consumer<ForwardCallResponse>() {
                        @Override
                        public void accept(ForwardCallResponse forwardCallResponse) throws Exception{
                            UndeliveredViewModel.this.setIsLoading(false);
                            if(forwardCallResponse.getStatus().equalsIgnoreCase("true")){
                                getNavigator().undeliverShipment(true);
                                if(calldialog != null){
                                    calldialog.dismiss();
                                }
                                getNavigator().showError(forwardCallResponse.getResponse());
                            } else{
                                if(forwardCallResponse.isCall_again_required()){
                                    getNavigator().undeliverShipment(false);
                                } else{
                                    response_time = System.currentTimeMillis();
                                    getNavigator().showError(forwardCallResponse.getResponse());
                                    if(getDataManager().isCallStatusAPIRecursion()){
                                        long time_difference = response_time - request_time;
                                        if(time_difference < getDataManager().getRequestRsponseTime()){
                                            long diffrence = getDataManager().getRequestRsponseTime() - time_difference;
                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run(){
                                                    callApi(nyka,awb_number, drs_id);
                                                    if(isCallRecursionDailogRunning){
                                                        getNavigator().getActivityContext().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run(){
                                                                showCallAPIDelayDialog(nyka,awb_number, drs_id);
                                                            }
                                                        });
                                                    }
                                                }
                                            }, diffrence);
                                        } else{
                                            call_alert_number = call_alert_number + 1;
                                            callApi(nyka,awb_number, drs_id);
                                            if(isCallRecursionDailogRunning){
                                                showCallAPIDelayDialog(nyka,awb_number, drs_id);
                                            }
                                        }
                                    } else{
                                        if(isStopRecursion){
                                            return;
                                        }
                                        call_alert_number = call_alert_number + 1;
                                        showCallAPIDelayDialog(nyka,awb_number, drs_id);
                                    }
                                }
                            }
                        }
                    }, throwable -> {
                        UndeliveredViewModel.this.setIsLoading(false);
                    }));
        } catch(Exception e){
            getNavigator().showError(e.getLocalizedMessage());
            UndeliveredViewModel.this.setIsLoading(false);
            e.printStackTrace();
        }
        }
        else
        {
            getNavigator().undeliverShipment(true);
        }
    }

    public void callBridgeCheckStatusApi(String nyka,String awb_number, String drs_id){

        if (nyka.equalsIgnoreCase("true")){
            try{
                request_time = System.currentTimeMillis();
                UndeliveredViewModel.this.setIsLoading(true);
                getCompositeDisposable().add(getDataManager().doForwardCallStatusApiCall(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), call_alert_number, getDataManager().getEmp_code(), awb_number, drs_id, getDataManager().getShipperId()).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).
                        subscribe(new Consumer<ForwardCallResponse>() {
                            @Override
                            public void accept(ForwardCallResponse forwardCallResponse) throws Exception{
                                UndeliveredViewModel.this.setIsLoading(false);
                                if(forwardCallResponse.getStatus().equalsIgnoreCase("true")){
                                    getNavigator().undeliverShipment(true);
                                    if(calldialog != null){
                                        calldialog.dismiss();
                                    }
                                    getNavigator().showError(forwardCallResponse.getResponse());
                                } else{
                                    getNavigator().onCallBridgeCheckStatus();
                                }
                            }
                        }, throwable -> {
                            UndeliveredViewModel.this.setIsLoading(false);
                        }));
            } catch(Exception e){
                getNavigator().showError(e.getLocalizedMessage());
                UndeliveredViewModel.this.setIsLoading(false);
                e.printStackTrace();
            }
        }
        else
        {
            getNavigator().undeliverShipment(true);
        }
    }

    public Map<String, List<EDSReasonCodeMaster>> groupRecord(List<EDSReasonCodeMaster> edsReasonCodeMasters){
        Map<String, List<EDSReasonCodeMaster>> map = new HashMap<String, List<EDSReasonCodeMaster>>();
        //  parentGroup.add(Constants.SELECT);
        parentGroupSpinnerValues.add(Constants.SELECT);
        try{
            for(EDSReasonCodeMaster edsReasonCodeMaster : edsReasonCodeMasters){
                String key = edsReasonCodeMaster.getSubGroup();
                if(Constants.CONSIGNEE_PROFILE && edsReasonCodeMaster.getEdsMasterDataAttributeResponse().isPR_NA()){
                    continue;
                } else{
                    if(map.containsKey(key)){
                        List<EDSReasonCodeMaster> list = map.get(key);
                        list.add(edsReasonCodeMaster);
                    } else{
                        List<EDSReasonCodeMaster> list = new ArrayList<EDSReasonCodeMaster>();
                        list.add(edsReasonCodeMaster);
                        // parentGroup.add(key);
                        parentGroupSpinnerValues.add(key);
                        map.put(key, list);
                    }
                }
            }
            getNavigator().setParentGroupSpinnerValues(parentGroupSpinnerValues);
            return map;
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
        return map;
    }

    public ObservableArrayList<String> getSlotSpinner(){
        slotSpinner.add("Select Slot");
        slotCode.add("-1");
        slotSpinner.add("8 AM - 12 PM");
        slotCode.add("1");
        slotSpinner.add("12 PM - 4 PM");
        slotCode.add("2");
        slotSpinner.add("4 PM - 8 PM");
        slotCode.add("3");
        return slotSpinner;
    }

    public void getDate(){
        getNavigator().captureDate();
    }

    private EDSReasonCodeMaster getselectData(){
        EDSReasonCodeMaster edsReasonCodeMaster = new EDSReasonCodeMaster();
        try{
            edsReasonCodeMaster.setReasonCode(-1);
            edsReasonCodeMaster.setReasonMessage(Constants.SELECT);
            EDSMasterDataAttributeResponse edsMasterDataAttributeResponse = new EDSMasterDataAttributeResponse();
            edsMasterDataAttributeResponse.setcALLM(false);
            edsMasterDataAttributeResponse.setiMG(false);
            edsMasterDataAttributeResponse.setoTP(false);
            edsMasterDataAttributeResponse.setrCHD(false);
            edsMasterDataAttributeResponse.seteDSActivity(false);
            edsMasterDataAttributeResponse.seteDSActivityList(false);
            edsMasterDataAttributeResponse.seteDSOTP(false);
            edsMasterDataAttributeResponse.setSECURED(false);
            edsMasterDataAttributeResponse.setEDS_CC(false);
            edsMasterDataAttributeResponse.setEDS_CPV(false);
            edsMasterDataAttributeResponse.setEDS_EKYC(false);
            edsMasterDataAttributeResponse.setEDS_UDAAN(false);
            edsMasterDataAttributeResponse.setEDS_IMAGE(false);
            edsMasterDataAttributeResponse.setEDS_PAYTM_IMAGE(false);
            edsMasterDataAttributeResponse.setEDS_DV(false);
            edsMasterDataAttributeResponse.setEDS_DC(false);
            edsReasonCodeMaster.setEdsMasterDataAttributeResponse(edsMasterDataAttributeResponse);
            return edsReasonCodeMaster;
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
        return edsReasonCodeMaster;
    }

    public void setAwb(long awbNo){
        awb.set(awbNo);
    }

    public void onCaptureImageClick(){
        getNavigator().onCaptureImage();
    }

    public void onChooseGroupSpinner(AdapterView<?> parent, View view, int pos, long id){
        try{
            List<EDSReasonCodeMaster> edsReasonCodeMasters = childGroup.get(parentGroupSpinnerValues.get(pos));
            childGroupSpinnerValues.clear();
            childEDSReasonCodeMaster.clear();
            if(edsReasonCodeMasters != null){
                childEDSReasonCodeMaster.add(0, getselectData());
                childGroupSpinnerValues.add(Constants.SELECT);
                for(EDSReasonCodeMaster edsReasonCodeMaster : edsReasonCodeMasters){
                    childEDSReasonCodeMaster.add(edsReasonCodeMaster);
                    childGroupSpinnerValues.add(edsReasonCodeMaster.getReasonMessage());
                }
            }
            getNavigator().onChooseGroupSpinner(parentGroupSpinnerValues.get(pos));
            getNavigator().setChildSpinnerValues(childGroupSpinnerValues);
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
    }

    //for Child Chooser
    public void onChooseChildSpinner(AdapterView<?> parent, View view, int pos, long id){
        getNavigator().onChooseChildSpinner(childEDSReasonCodeMaster.get(pos), childEDSReasonCodeMaster.get(pos).getEdsMasterDataAttributeResponse().isrCHD(), childEDSReasonCodeMaster.get(pos).getEdsMasterDataAttributeResponse().iscALLM());
    }

    public void onChooseReasonSpinner(AdapterView<?> parent, View view, int pos, long id){
        getNavigator().onChooseReasonSpinner(edsReasonCode.get(pos), edsReasonCode.get(pos).getEdsMasterDataAttributeResponse().isrCHD(), edsReasonCode.get(pos).getEdsMasterDataAttributeResponse().iscALLM());
    }

    public void onChooseSlotSpinner(AdapterView<?> parent, View view, int pos, long id){
        getNavigator().onChooseSlotSpinner(slotCode.get(pos));
    }

    public void onRescheduleClick(){
        getNavigator().onRescheduleClick();
    }

    public void onSubmitClick(){
        getNavigator().OnSubmitClick();
    }

    public void uploadAWSImage(String imageUri, String imageType, String imageKey, String image_code, long awbNo, int drs_no, Bitmap bitmap, Boolean isCommit){
        saveImageDB(imageUri, image_code, imageKey);
        if(getDataManager().getEDSRealTimeSync().equalsIgnoreCase("true")){
            getNavigator().showProgress();
            uploadImageServer(imageKey, imageUri, image_code, awbNo, drs_no, bitmap);
        }
    }

    public void uploadImageServer(String imageName, String imageUri, String imageCode, long awbNo, int drsno, Bitmap bitmap){
        final long timeStampUploadImageToServer = Calendar.getInstance().getTimeInMillis();
        HashMap<String, Long> timeStampTagging = new HashMap<>();
        try{
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
            try{
                getCompositeDisposable().add(getDataManager().doImageUploadApiCall(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), "OTHERS", headers, map, fileToUpload).
                        subscribeOn(getSchedulerProvider().io()).
                        observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<ImageUploadResponse>() {
                    @Override
                    public void accept(ImageUploadResponse imageUploadResponse) throws Exception{
                        if(imageUploadResponse != null){
                            try{
                                getNavigator().onProgressFinishCall();
                                if(imageUploadResponse.getStatus().toLowerCase().contains("Success".toLowerCase())){
                                    getNavigator().setBitmap();
                                    Observable.fromCallable(() -> {
                                        try{
                                            saveImageResponse(imageName, imageUploadResponse.getImageId());
                                        } catch(Exception e){
                                            e.printStackTrace();
                                        }
                                        return false;
                                    }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe((result) -> {
                                    });
                                    Log.e("image upload", "success");
                                } else{
                                    getNavigator().onProgressFinishCall();
                                    getNavigator().showError(imageUploadResponse.getDescription());
                                }
                            } catch(Exception ex){
                                try{
                                    getNavigator().onProgressFinishCall();
                                    getNavigator().showError("Please try again.");
                                    ex.printStackTrace();
                                } catch(Exception e){
                                    getNavigator().onProgressFinishCall();
                                    e.printStackTrace();
                                }
                            }
                        } else{
                            getNavigator().onProgressFinishCall();
                            getNavigator().showErrorMessage(false);
                        }
                        //
                    }
                }, throwable -> {
                    getNavigator().onProgressFinishCall();
                    throwable.printStackTrace();
                    getNavigator().showErrorMessage(false);
                    Log.d("TAG", "accept: error");
                }));
            } catch(Exception ex){
                getNavigator().onProgressFinishCall();
            }
        } catch(Exception ex){
            getNavigator().onProgressFinishCall();
            ex.printStackTrace();
            Log.e("Image Sync exception", ex.toString());
        }
    }

    private void saveImageResponse(String image_name, int image_id){
        try{
            getDataManager().updateImageStatus(image_name, 2).blockingSingle();
            getDataManager().updateImageID(image_name, image_id).blockingSingle();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void saveImageDB(String imageUri, String imageCode, String name){
        try{
            ImageModel imageModel = new ImageModel();
            imageModel.setDraNo(Integer.toString(edsResponse.getDrsNo()));
            imageModel.setAwbNo(Long.toString(edsResponse.getAwbNo()));
            imageModel.setImageName(name);
            imageModel.setImage(imageUri);
            imageModel.setImageCode(imageCode);
            imageModel.setStatus(0);
            imageModel.setImageCurrentSyncStatus(GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO);
            imageModel.setImageFutureSyncTime(Calendar.getInstance().getTimeInMillis());
            imageModel.setImageId(-1);
            imageModel.setDate(Calendar.getInstance().getTimeInMillis());
            imageModel.setShipmentType(GlobalConstant.ShipmentTypeConstants.EDS);
            imageModel.setImageType(GlobalConstant.ShipmentTypeConstants.OTHER);
            getCompositeDisposable().add(getDataManager().saveImage(imageModel).
                    subscribeOn(getSchedulerProvider().io()).
                    observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception{
                }
            }));
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
    }

    public void createCommitPacket(String reasonCode, FeRescheduleInfo feRescheduleInfo, EdsCommit edsCommits, EDSResponse edsResponse, List<EDSActivityResponseWizard> edsActivityResponseWizards, boolean isNRealTimeSync){
        edsCommit = edsCommits;
        mimageModels = getImagesList(edsCommits.getAwb());
        for(int i = 0; i < mimageModels.size(); i++){
            EDSActivityImageRequest edsActivityImageRequest = new EDSActivityImageRequest();
            edsActivityImageRequest.setImageId(String.valueOf(mimageModels.get(i).getImageId()));
            edsActivityImageRequest.setImageKey(String.valueOf(mimageModels.get(i).getImageName()));
            edsActivityImageRequestList.add(edsActivityImageRequest);
        }
        edsCommits.setEdsActivityImageRequests(edsActivityImageRequestList);
        edsCommit.setFeEmpCode(getDataManager().getCode());

        for (EDSActivityWizard edsActivityWizard : edsWithActivityList.get().getEdsActivityWizards()) {
            edsCommit.setMinimum_amount(String.valueOf(edsActivityWizard.getMinimumAmount()));
            edsCommit.setActual_value(String.valueOf(edsActivityWizard.getActualValue()));
        }
        edsCommit.setDrsId(Integer.toString(edsResponse.getDrsNo()));
        try{
            if(!String.valueOf(in.ecomexpress.geolocations.Constants.latitude).equalsIgnoreCase("0.0") && !String.valueOf(in.ecomexpress.geolocations.Constants.longitude).equalsIgnoreCase("0.0")){
                edsCommit.setLocationLat(String.valueOf(in.ecomexpress.geolocations.Constants.latitude));
                edsCommit.setLocationLong(String.valueOf(in.ecomexpress.geolocations.Constants.longitude));
            } else if(!Constants.CURRENT_LATITUDE.equalsIgnoreCase("0.0") && !Constants.CURRENT_LONGITUDE.equalsIgnoreCase("0.0")){
                edsCommit.setLocationLat(Constants.CURRENT_LATITUDE);
                edsCommit.setLocationLong(Constants.CURRENT_LONGITUDE);
            } else{
                edsCommit.setLocationLat(String.valueOf(getDataManager().getCurrentLatitude()));
                edsCommit.setLocationLong(String.valueOf(getDataManager().getCurrentLongitude()));
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        edsCommit.setConsigneeName(edsResponse.getConsigneeDetail().getName());
        edsCommit.setAttemptType("EDS");
        edsCommit.setAwb(Long.toString(edsResponse.getAwbNo()));
        edsCommit.setFeComments("NONE");
        edsCommit.setAssignDate(edsResponse.getAssignDate());
        edsCommit.setStatus(Constants.EDSUNDELIVERED);
        edsCommit.setAttemptReasonCode(reasonCode);
        edsCommit.setOrderNo(edsResponse.getShipmentDetail().getOrderNo());
        edsCommit.setDrsCommitDateTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        edsCommit.setFeRescheduleInfo(feRescheduleInfo);
        if(edsResponse.getShipmentDetail().getFlag().isIdDcEnabled())
            edsCommit.setPackageBarcode(Long.toString(edsResponse.getAwbNo()));
        else
            edsCommit.setPackageBarcode("");
        if(getDataManager().getConsigneeProfileValue().equalsIgnoreCase("W")){
            edsCommit.setFlag_of_warning("W");
        } else{
            edsCommit.setFlag_of_warning("N");
        }
        if(Constants.uD_OTP_API_CHECK && ud_otp_commit_status_field.get().equalsIgnoreCase("VERIFIED"))
        {
            edsCommit.setUd_otp(ud_otp_commit_status_field.get());
            edsCommit.setRd_otp("NONE");
        }
        else if(Constants.rD_OTP_API_CHECK && ud_otp_commit_status_field.get().equalsIgnoreCase("VERIFIED"))

        {
            edsCommit.setRd_otp("VERIFIED");
            edsCommit.setUd_otp("NONE");

        }
        else
        {
            edsCommit.setUd_otp(ud_otp_commit_status_field.get());
            edsCommit.setRd_otp(rd_otp_commit_status_field.get());
        }
        edsCommit.setTripId(getDataManager().getTripId());
        edsCommit.setEdsActivityResponseWizard(edsActivityResponseWizards);
        /*if(isNRealTimeSync){
            UploadEdsShipment(edsCommit, edsResponse.compositeKey);
        } else{*/
            updateShipmentStatus(String.valueOf(edsResponse.getCompositeKey()));
            saveCommit(edsCommit, edsResponse.getCompositeKey());
      //  }
    }

    private void updateShipmentStatus(String compositeKey){
        try{
            getCompositeDisposable().add(getDataManager().
                    updateEdsStatus(compositeKey, 3).observeOn(getSchedulerProvider().ui()).
                    subscribeOn(getSchedulerProvider().io()).
                    subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean){
                            getNavigator().openFail();
                        }
                    }));
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
    }

    private void saveCommit(EdsCommit edsCommit, String compositeKey){
        PushApi pushApi = new PushApi();
        pushApi.setCompositeKey(compositeKey);
        pushApi.setAwbNo(Long.valueOf(edsCommit.getAwb()));
        pushApi.setAuthtoken(getDataManager().getAuthToken());
        try{
            pushApi.setRequestData(new ObjectMapper().writeValueAsString(edsCommit));
            pushApi.setShipmentStatus(0);
            pushApi.setShipmentCaterogy(GlobalConstant.ShipmentTypeConstants.EDS);
        } catch(JsonProcessingException e){
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
        try{
            getCompositeDisposable().add(getDataManager().saveCommitPacket(pushApi).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception{
                    int isCallattempted = getisCallattempted(edsCommit.getAwb());
                    if(isCallattempted == 0){
                        Constants.shipment_undelivered_count++;
                    }
                    updateEDSCallAttemptedWithZero(String.valueOf(pushApi.getAwbNo()));
                    // updateShipmentStats(String.valueOf(pushApi.getAwbNo()));
                }
            }));
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
    }

    public int getisCallattempted(String awb){
        try{
            getCompositeDisposable().add(getDataManager().
                    getisEDSCallattempted(Long.valueOf(awb)).
                    observeOn(getSchedulerProvider().ui()).
                    subscribeOn(getSchedulerProvider().io()).
                    subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long isCallattempted) throws Exception{
                            try{
                                isCall = Integer.parseInt(String.valueOf(isCallattempted));
                                //                                Toast.makeText(context, "isCallattempted = " + isCallattempted, Toast.LENGTH_SHORT).show();
                                //                            getNavigator().showSuccessStatus(forwardCommit);
                            } catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }, throwable -> {
                        setIsLoading(false);
                        getNavigator().showError(throwable.getMessage());
                    }));
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
        return isCall;
    }

    public void updateEDSCallAttempted(String awb){
        try{
            getCompositeDisposable().add(getDataManager().
                    updateEDSCallAttempted(Long.valueOf(awb), Constants.callAttempted).
                    observeOn(getSchedulerProvider().ui()).
                    subscribeOn(getSchedulerProvider().io()).
                    subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean){
                        }
                    }));
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
    }

    public void updateEDSCallAttemptedWithZero(String awb){
        try{
            getCompositeDisposable().add(getDataManager().
                    updateEDSCallAttempted(Long.valueOf(awb), 0).
                    observeOn(getSchedulerProvider().ui()).
                    subscribeOn(getSchedulerProvider().io()).
                    subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean){
                        }
                    }));
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
    }

    public void updateEDSpinCallAttempted(String awb){
        try{
            getCompositeDisposable().add(getDataManager().
                    updateEDSpinCallAttempted(Long.valueOf(awb), Constants.callAttempted).
                    observeOn(getSchedulerProvider().ui()).
                    subscribeOn(getSchedulerProvider().io()).
                    subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean){
                        }
                    }));
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
    }

    public void makeCallBridgeApiCall(String callBridgeKey, String awb, String drsid, String type){
        try{
            CallApiRequest request = new CallApiRequest();
            request.setAwb(awb);
            request.setCb_api_key(callBridgeKey);
            request.setDrs_id(String.valueOf(drsid));
            Log.d("request123", request.toString());
            final long timeStamp = Calendar.getInstance().getTimeInMillis();
            writeRestAPIRequst(timeStamp, request);
            getCompositeDisposable().add(getDataManager().doCallBridgeApiCall(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), request).observeOn(getSchedulerProvider().ui()).
                    subscribeOn(getSchedulerProvider().io()).
                    subscribe(new Consumer<ErrorResponse>() {
                        @Override
                        public void accept(ErrorResponse callApiResponse){
                            Log.d("TAG", callApiResponse.toString());
                            writeRestAPIResponse(timeStamp, callApiResponse);
                            if(callApiResponse != null){
                                try{
                                    if(callApiResponse.isStatus() == true){
                                        if(type.equalsIgnoreCase(Constants.EDS)){
                                            updateEDSCallAttempted(awb);
                                        }
                                    }
                                    getNavigator().onHandleError(callApiResponse);
                                } catch(NullPointerException e){
                                    e.printStackTrace();
                                } catch(OnErrorNotImplementedException e){
                                    e.printStackTrace();
                                } catch(retrofit2.adapter.rxjava2.HttpException e){
                                    e.printStackTrace();
                                } catch(Exception ex){
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }, throwable -> {
                        setIsLoading(false);
                        getNavigator().showErrorMessage(throwable.getMessage().contains("HTTP 500 "));
                    }));
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
    }

    public void logoutLocal(){
        getDataManager().setTripId("");
        getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT);
        clearAppData();
    }

    private void clearAppData(){
        try{
            getCompositeDisposable().add(getDataManager().deleteAllTables().subscribeOn(getSchedulerProvider().io()).
                    observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean){
                    try{
                        getDataManager().clearPrefrence();
                        getDataManager().setUserAsLoggedOut();
                    } catch(Exception ex){
                        ex.printStackTrace();
                    }
                    getNavigator().clearStack();
                }
            }));
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
    }

    public void fetchEDSShipment(String awbNo){
        try{
            //        long compositeKey = Long.parseLong(awbNo);
            getCompositeDisposable().add(getDataManager().getEDS(awbNo).
                    subscribeOn(getSchedulerProvider().io()).
                    observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<EDSResponse>() {
                @Override
                public void accept(EDSResponse edsResponse) throws Exception{
                    getNavigator().onEDSItemFetched(edsResponse);
                }
            }, throwable -> {
                writeErrors(Calendar.getInstance().getTimeInMillis(), new Exception(throwable));
                throwable.printStackTrace();
            }));
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
    }

    public void getConsigneeProfiling(){
        boolean enable = getDataManager().getConsigneeProfiling();
        getNavigator().setConsingeeProfiling(enable);
    }
    //    public void checkMeterRange(EDSResponse edsResponse) {
    //        double currentLatitude = getDataManager().getCurrentLatitude();
    //        double currentLongitude = getDataManager().getCurrentLongitude();
    //        double consigneeLatitude = edsResponse.getConsigneeDetail().getAddress().getLocation().getLat();
    //        double consigneeLongitude = edsResponse.getConsigneeDetail().getAddress().getLocation().getLng();
    //
    ////        double consigneeLatitude = 28.9873942;
    ////        double consigneeLongitude = 77.6289194;
    //
    //
    //        if (consigneeLatitude == 0.0 || consigneeLongitude == 0.0) {
    //            getNavigator().setConsigneeDistance(0);
    //            return;
    //        }
    //
    //        int meter = LocationHelper.getDistanceBetweenPoint(currentLatitude, currentLongitude, consigneeLatitude, consigneeLongitude);
    //        getNavigator().setConsigneeDistance(meter);
    //
    //    }

    public void checkMeterRange(EDSResponse edsResponse){
        try{
            getProfileLatLng(edsResponse);
        } catch(Exception e){
            e.printStackTrace();
            // getNavigator().showError(e.getMessage());
        }
    }

    private void getProfileLatLng(EDSResponse edsResponse){
        try{
            getCompositeDisposable().add(getDataManager().
                    getProfileLat(edsResponse.getAwbNo()).
                    subscribeOn(getSchedulerProvider().io()).
                    subscribe(new Consumer<ProfileFound>() {
                        @Override
                        public void accept(ProfileFound profileFound){
                            try{
                                if(profileFound != null && profileFound.getAwb_number() != 0){
                                    double consigneeLatitude = 0.0;
                                    double consigneeLongitude = 0.0;
                                    double currentLatitude = 0.0;
                                    double currentLongitude = 0.0;
                                    try{
                                        currentLatitude = getDataManager().getCurrentLatitude();
                                        currentLongitude = getDataManager().getCurrentLongitude();
                                        consigneeLatitude = profileFound.getDelivery_latitude();
                                        consigneeLongitude = profileFound.getDelivery_longitude();
                                    } catch(Exception e){
                                        e.printStackTrace();
                                    }
                                    if(consigneeLatitude == 0.0 || consigneeLongitude == 0.0){
                                        getNavigator().setConsigneeDistance(0);
                                        return;
                                    }
                                    int meter = (int) getDistaneBetweenLocations(new LatLng(consigneeLatitude, consigneeLongitude));
                                    //int meter_point = LocationHelper.getDistanceBetweenPoint(currentLatitude, currentLongitude, consigneeLatitude, consigneeLongitude);
                                    getNavigator().setConsigneeDistance(meter);
                                }
                            } catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception{
                            Log.e("error", throwable.getMessage());
                        }
                    }));
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public double getDistaneBetweenLocations(LatLng destination){
        try{
            double distance = 0.0;
            GeoApiContext context = new GeoApiContext().setApiKey(DISTANCE_API_KEY);
            DirectionsResult result = DirectionsApi.newRequest(context).mode(TravelMode.DRIVING).units(Unit.METRIC).origin(new LatLng(getDataManager().getCurrentLatitude(), getDataManager().getCurrentLongitude())).optimizeWaypoints(true).destination(destination).awaitIgnoreError();
            String dis = (result.routes[0].legs[0].distance.humanReadable);
            if(dis.endsWith("km")){
                distance = Double.parseDouble(dis.replaceAll("[^\\.0123456789]", "")) * 1000;
            } else{
                distance = Double.parseDouble(dis.replaceAll("[^\\.0123456789]", ""));
            }
            return distance;
        } catch(Exception e){
            e.printStackTrace();
        }
        return 0.0;
    }

    public int getAttemptTimes(){
        getDataManager().setRescheduleAttemptTimes(10);
        return getDataManager().getRescheduleAttemptTimes();
    }

    private void UploadEdsShipment(EdsCommit edsCommit, String composite_key){
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        final long timeStamp = Calendar.getInstance().getTimeInMillis();
        setIsLoading(true);
        HashMap<String, String> tokens = new HashMap<>();
        tokens.put(Constants.TOKEN, getDataManager().getAuthToken());
        tokens.put(Constants.EMP_CODE, getDataManager().getCode());
        try{
        } catch(Exception e){
            e.printStackTrace();
        }
        // AppLogs.Companion.writeRestApiRequest(timeStamp, edsCommit);
        try{
            compositeDisposable.add(getDataManager().doEDSCommitApiCall(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), tokens, edsCommit).subscribeOn(getSchedulerProvider().io()).
                    subscribe((EDSCommitResponse edsCommitResponse) -> {
                        // AppLogs.Companion.writeRestApiResponse(timeStamp, edsCommitResponse);
                        if(edsCommitResponse.getStatus()){
                            int shipment_status = 0;
                            String compositeKey = "";
                            if(edsCommitResponse.getResponse().getShipment_status().equalsIgnoreCase(Constants.EDSUNDELIVERED)){
                                shipment_status = 3;
                            } else{
                                shipment_status = 2;
                            }
                            try{
                                compositeKey =edsCommitResponse.getResponse().getDrs_no() +""+ edsCommitResponse.getResponse().getAwb_no();
                            } catch(Exception e){
                                e.printStackTrace();
                            }
                            getDataManager().
                                    updateEdsStatus(compositeKey, shipment_status).
                                    subscribe(new Consumer<Boolean>() {
                                        @Override
                                        public void accept(Boolean aBoolean) throws Exception{
                                            if(aBoolean){
                                                setIsLoading(false);
                                                updateSyncStatusInDRSEDSTable(edsCommitResponse.getResponse().getDrs_no() + edsCommitResponse.getResponse().getAwb_no(), GlobalConstant.CommitStatus.CommitSynced);
                                                // Setting call preference after sync:-
                                                getDataManager().setCallClicked(edsCommitResponse.getResponse().getAwb_no()+"EDSCall", true);
                                                compositeDisposable.add(getDataManager().deleteSyncedImage((edsCommitResponse.getResponse().getAwb_no())).subscribe(new Consumer<Boolean>() {
                                                    @Override
                                                    public void accept(Boolean aBoolean) throws Exception{
                                                        //sendBoardCast();
                                                    }
                                                }));
                                            }
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception{
                                            throwable.printStackTrace();
                                            getNavigator().openFail();
                                            saveCommit(edsCommit, composite_key);
                                            setIsLoading(false);
                                        }
                                    });
                                   /* pushApi.setShipmentStatus(GlobalConstant.CommitStatus.CommitSynced);
                                    updateSyncStatusInDRSEDSTable(pushApi.getCompositeKey(), GlobalConstant.CommitStatus.CommitSynced);
                                    compositeDisposable.add(mDataManager.deleteSyncedImage(String.valueOf(pushApi.getAwbNo())).subscribe(new Consumer<Boolean>() {
                                        @Override
                                        public void accept(Boolean aBoolean) throws Exception {
                                            //sendBoardCast();
                                        }
                                    }));
                                    compositeDisposable.add(mDataManager.saveCommitPacket(pushApi).subscribe(new Consumer<Boolean>() {
                                        @Override
                                        public void accept(Boolean aBoolean) throws Exception {
                                            sendBoardCast();
                                        }
                                    }));*/
                        } else{
                            //                                    saveImageDB(imageUri, imageType, imageKey);
                            //                                    if (isCommit) {
                            //                                        updateShipmentStatus(String.valueOf(edsResponse.getCompositeKey()));
                            //                                    }
                            saveCommit(edsCommit, composite_key);
                            getNavigator().openFail();
                            setIsLoading(false);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception{
                            saveCommit(edsCommit, composite_key);
                            setIsLoading(false);
                            getNavigator().openFail();
                            throwable.printStackTrace();
                        }
                    }));
        } catch(Exception e){
            e.printStackTrace();
            setIsLoading(false);
            saveCommit(edsCommit, composite_key);
            getNavigator().openFail();
        }
    }

    private void updateSyncStatusInDRSEDSTable(String compositeKey, int i){
        final long TimeStampTag = Calendar.getInstance().getTimeInMillis();
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager().updateSyncStatusEDS(compositeKey, 2).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception{
                getNavigator().openFail();
            }
        }, throwable -> {
        }));
    }

    private List<ImageModel> getImagesList(String awbNo){
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager().getImages(awbNo).subscribeOn(getSchedulerProvider().io()).
                observeOn(getSchedulerProvider().io()).subscribe(new Consumer<List<ImageModel>>() {
            @Override
            public void accept(List<ImageModel> imageModels) throws Exception{
                Log.e("getImages", imageModels.size() + "");
                mimageModels = imageModels;
            }
        }));
        return mimageModels;
    }

    public HashSet<String> getEdsTrueAttributes(boolean is_already_kyced){
        HashSet<String> attribute_set = new HashSet<>();
        if(getDataManager().getEdsActivityCodes() != null && getDataManager().getEdsActivityCodes().size() > 0){
            for(String val : getDataManager().getEdsActivityCodes()){
                if(is_already_kyced)
                {
                    Constants.EDS_PAYTM_IMAGE = true;
                    attribute_set.add("EDS_PAYTM_IMAGE");
                    break;
                }
                if(val.startsWith("DC")){
                    Constants.EDS_DC = true;
                    attribute_set.add("EDS_DC");
                }
                if(val.startsWith("DV")){
                    Constants.EDS_DV = true;
                    attribute_set.add("EDS_DV");
                }
                if(val.contains("AC_CASH_COLLECTION")){
                    Constants.EDS_CC = true;
                    attribute_set.add("EDS_CC");
                }
                if(val.startsWith("AC") && val.endsWith("CPV")){
                    Constants.EDS_CPV = true;
                    attribute_set.add("EDS_CPV");
                }
                if(val.startsWith("AC") && val.endsWith("IMAGE")){
                    if(val.contains("PAYTM")){
                        Constants.EDS_PAYTM_IMAGE = true;
                        attribute_set.add("EDS_PAYTM_IMAGE");
                    } else if(val.contains("UD") || val.contains("UDL") || val.contains("UDR")){
                        Constants.EDS_UDAAN = true;
                        attribute_set.add("EDS_UDAAN");
                    } else{
                        Constants.EDS_IMAGE = true;
                        attribute_set.add("EDS_IMAGE");
                    }
                }
                if(val.startsWith("AC") && val.endsWith("EKYC") || val.endsWith("BKYC")){
                    if(val.contains("PAYTM")){
                        Constants.EDS_PAYTM_IMAGE = true;
                        attribute_set.add("EDS_PAYTM_IMAGE");
                    }
                    Constants.EDS_EKYC = true;
                    attribute_set.add("EDS_EKYC");
                }
                if(val.startsWith("AC") && val.endsWith("FAIL")){
                    Constants.EDS_EKYC_FAIL = true;
                    attribute_set.add("EDS_EKYC_FAIL");
                }
                if(val.startsWith("AC_LIST")){
                    Constants.EDS_DC = true;
                    attribute_set.add("EDS_DC");
                }

            }
        } else{
            Constants.EDS_OTP = true;
            attribute_set.add("EDS_OTP");
        }
        if(attribute_set.isEmpty()){
            Constants.EDS_DC = true;
            attribute_set.add("EDS_DC");
        }
        return attribute_set;
    }

    public boolean FEInDCZone(double latitude, double longitude, double dcLatitude, double dcLongitude){
        try{
            if(getDataManager().getTripId().equalsIgnoreCase("0")){
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
        } catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public List<RescheduleEdsD> getEdsReschdData(){
        try{
            getCompositeDisposable().add(getDataManager().getEdsRescheduleFlag().subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<List<RescheduleEdsD>>() {
                @Override
                public void accept(List<RescheduleEdsD> rescheduleEdsDSs) throws Exception{
                    rescheduleEdsDS = rescheduleEdsDSs;
                }
            }));
        } catch(Exception e){
            e.printStackTrace();
        }
        return rescheduleEdsDS;
    }

    public void showCallAPIDelayDialog(String nyka,String awb_number, String drs_id){
        isCallRecursionDailogRunning = false;
        getNavigator().getActivityContext().runOnUiThread(new Runnable() {
            @Override
            public void run(){
                AlertDialog.Builder callalertDialog = new AlertDialog.Builder(getNavigator().getActivityContext()).setMessage("Getting call status...").setPositiveButton("Wait", null);
                calldialog = callalertDialog.create();
                calldialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    private final long AUTO_DISMISS_MILLIS = Long.parseLong(getDataManager().getCallStatusApiInterval()) * 1000;

                    @Override
                    public void onShow(final DialogInterface dialog){
                        final Button defaultButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        defaultButton.setEnabled(false);
                        final CharSequence negativeButtonText = defaultButton.getText();
                        new CountDownTimer(AUTO_DISMISS_MILLIS, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished){
                                isCallRecursionDailogRunning = false;
                                defaultButton.setText(String.format(Locale.getDefault(), "%s (%d)", negativeButtonText, TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1 //add one so it never displays zero
                                ));
                            }

                            @Override
                            public void onFinish(){
                                if(((AlertDialog) calldialog).isShowing()){
                                    calldialog.dismiss();
                                }
                                if(getDataManager().isCallStatusAPIRecursion()){
                                    getNavigator().undeliverShipment(false);
                                    getDataManager().setCallAPIRecursion(false);
                                    isStopRecursion = true;
                                } else{
                                    if(call_alert_number > 1){
                                        getNavigator().undeliverShipment(false);
                                    } else{
                                        callApi(nyka,awb_number, drs_id);
                                    }
                                }
                            }
                        }.start();
                    }
                });
                calldialog.setCancelable(false);
                calldialog.show();
            }
        });
    }

    public void getShipmentRescheduleDetails(String awbno){
        setIsLoading(true);
        try{
            ResheduleDetailsRequest request = new ResheduleDetailsRequest();
            request.setAwb(awbno);
            final long timeStamp = System.currentTimeMillis();
            writeRestAPIRequst(timeStamp, request);
            getCompositeDisposable().add(getDataManager().doReshceduleDetails(getDataManager().getAuthToken(), getDataManager().getEcomRegion(),request).doOnSuccess(new Consumer<ReshceduleDetailsResponse>() {
                @Override
                public void accept(ReshceduleDetailsResponse reshceduleDetailsResponse){
                    if(reshceduleDetailsResponse.getTotal_attempts() < getDataManager().getRescheduleMaxAttempts()){
                        getNavigator().setRescheduleResponse(reshceduleDetailsResponse);
                    } else{
                        getNavigator().showError("You have reached your max attempt of reschedule.");
                    }
                }
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<ReshceduleDetailsResponse>() {
                @Override
                public void accept(ReshceduleDetailsResponse response){
                }
            }, throwable -> {
                setIsLoading(false);
                String error;
                try{
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                    //                            if (error.contains("HTTP 500 ")) {
                    //                                getNavigator().showErrorMessage(true);
                    //                            } else {
                    //                                getNavigator().showErrorMessage(false);
                    //                            }
                } catch(NullPointerException e){
                    e.printStackTrace();
                }
            }));
        } catch(Exception e){
            Log.e(ContentValues.TAG, e.getMessage());
            setIsLoading(false);
            if(e instanceof Throwable){
                getNavigator().showError(new RestApiErrorHandler(e.fillInStackTrace()).getErrorDetails().getEResponse().getDescription());
            }
        }
    }

    public int getCounterDeliveryRange(){
        double currentLatitude = getDataManager().getCurrentLatitude();
        double currentLongitude = getDataManager().getCurrentLongitude();
        double DcLatitude = getDataManager().getDCLatitude();
        double DcLongitude = getDataManager().getDCLongitude();
        int meter = LocationHelper.getDistanceBetweenPoint(currentLatitude, currentLongitude, DcLatitude, DcLongitude);
        return meter;
    }

    public void getCallStatus(long awb, int drs){
        try{
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(getDataManager().getCallStatus(awb ,drs).subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception{
                            getDataManager().setIsCallAlreadyDone(aBoolean);
                        }
                    }, throwable -> {

                    }));
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void saveCallStatus(long awb, int drs){
        try{
            Call call = new Call();
            call.setDrs(drs);
            call.setAwb(awb);
            call.setStatus(true);
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(getDataManager().saveCallStatus(call).subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception{
                            Log.e("isDataSaved?", "saved");
                        }
                    }, throwable -> {

                    }));
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    public void setOFDVerfied(boolean isVerfied){
        if(isVerfied)
        {
            ud_otp_commit_status_field.set("VERIFIED");
            ud_otp_commit_status = "VERIFIED";
            ud_otp_verified_status.set(true);
        }
    }
    public void onVerifyClick(){
        getNavigator().onVerifyClick();
    }

    public void onResendClick(){
        getNavigator().onResendClick();
    }

    public void onGenerateOTPClick(){
        getNavigator().onGenerateOtpClick();
    }

    public void onSkip(View view){
        getNavigator().onSkipClick(view);
    }

    public void onGenerateOtpApiCall(Activity context, String awb, String drsid, Boolean alternateclick,String messagetype,Boolean generateotp){
        // setIsLoading(true);
        dialog = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
        dialog.setMessage("Sending OTP....");
        dialog.show();
        dialog.setIndeterminate(false);
        GenerateUDOtpRequest request = new GenerateUDOtpRequest(awb, messagetype, drsid,alternateclick,getDataManager().getCode(),generateotp, "EDS");
        final long timeStamp = System.currentTimeMillis();
        writeRestAPIRequst(timeStamp, request);
        try{
            getCompositeDisposable().add(getDataManager().doGenerateUDOtpApiCall(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), request).doOnSuccess(new Consumer<ResendOtpResponse>() {
                @Override
                public void accept(ResendOtpResponse resendOtpResponse){
                    writeRestAPIResponse(timeStamp, resendOtpResponse);
                    Log.d(ContentValues.TAG, resendOtpResponse.toString());
                }
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                if(dialog.isShowing())
                    dialog.dismiss();
                if(response.getStatus().equals("true")){
                    counter_skip++;
                    getNavigator().onOtpResendSuccess(response.getStatus() ,response.getDescription());
                    getNavigator().onOtpResendSuccess(response.getStatus(), response.getDescription());
                } else{
                    if(response.getResponse() != null)
                    {
                        if(response.getResponse().getStatusCode().equalsIgnoreCase("107"))
                        {
                            getNavigator().doLogout(response.getResponse().getDescription());
                        }
                    }
                    else if(response.getDescription().matches("Max Attempt Reached")){
                        counter_skip++;
                        getNavigator().onOtpResendSuccess(response.getStatus() ,response.getDescription());
                    }
                    getNavigator().showError(response.getDescription());
                }
            }, throwable -> {
                if(dialog.isShowing())
                    dialog.dismiss();
                String error;
                try{
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                       /* if (error.contains("HTTP 500 ")) {
                            getNavigator().onHandleError(Resources.getSystem().getString(R.string.http_500_msg));
                        } else {
                            getNavigator().onHandleError(Resources.getSystem().getString(R.string.server_down_msg));
                        }*/
                    getNavigator().showError(error);
                } catch(Exception e){
                    if(dialog.isShowing())
                        dialog.dismiss();
                    e.printStackTrace();
                    getNavigator().showError(e.getMessage());
                }
                // getNavigator().onHandleError(new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription());
            }));
        } catch(Exception e){
            if(dialog.isShowing())
                dialog.dismiss();
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
        //        updateStatus(2);
    }
    //ON RESEND BUTTON CLICK
    //making api call on resend button click
    public void onResendApiCall(Activity context, String awb, String drsid, Boolean alternateclick){
        // setIsLoading(true);
        dialog = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
        dialog.setMessage("Sending OTP....");
        dialog.show();
        dialog.setIndeterminate(false);
        ResendOtpRequest request = new ResendOtpRequest(awb, "OTP", drsid,alternateclick);
        final long timeStamp = System.currentTimeMillis();
        writeRestAPIRequst(timeStamp, request);
        try{
            getCompositeDisposable().add(getDataManager().doResendOtpApiCall(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), request).doOnSuccess(new Consumer<ResendOtpResponse>() {
                @Override
                public void accept(ResendOtpResponse resendOtpResponse){
                    writeRestAPIResponse(timeStamp, resendOtpResponse);
                    Log.d(ContentValues.TAG, resendOtpResponse.toString());
                }
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                if(dialog.isShowing())
                    dialog.dismiss();
                if(response.getStatus().equals("true")){
                    counter_skip++;
                    String newMobileNo = response.getResponse().getMobile();
                    //                            itemMobileforOtp.set("Please Enter the Otp sent on " + CommonUtils.imeiFullStars(newMobileNo));
                    //     getNavigator().onOtpResendSuccess();
                } else{
                    if(response.getResponse().getDescription().matches("Max Attempt Reached")){
                        counter_skip++;
                    }
                    getNavigator().showError(response.getResponse().getDescription());
                }
            }, throwable -> {
                if(dialog.isShowing())
                    dialog.dismiss();
                String error;
                try{
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                       /* if (error.contains("HTTP 500 ")) {
                            getNavigator().onHandleError(Resources.getSystem().getString(R.string.http_500_msg));
                        } else {
                            getNavigator().onHandleError(Resources.getSystem().getString(R.string.server_down_msg));
                        }*/
                    getNavigator().showError(error);
                } catch(Exception e){
                    if(dialog.isShowing())
                        dialog.dismiss();
                    e.printStackTrace();
                    getNavigator().showError(e.getMessage());
                }
                // getNavigator().onHandleError(new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription());
            }));
        } catch(Exception e){
            if(dialog.isShowing())
                dialog.dismiss();
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
        //        updateStatus(2);
    }

    //ON VERIFY BUTTON CLICK
    //making api call on verify button click
    public void onVerifyApiCall(Activity context, String awb, String otp, String drsid,String messagetype){
        // setIsLoading(true);
        dialog = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
        dialog.setMessage("Verifying....");
        dialog.show();
        dialog.setIndeterminate(false);
        try{
            VerifyUDOtpRequest request = new VerifyUDOtpRequest(awb,drsid,messagetype, otp );
            final long timeStamp = System.currentTimeMillis();
            writeRestAPIRequst(timeStamp, request);
            getCompositeDisposable().add(getDataManager().doVerifyUDOtpDRSApiCall(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), request).doOnSuccess(new Consumer<VerifyOTPResponse>() {
                @Override
                public void accept(VerifyOTPResponse verifyOtpResponse){
                    writeRestAPIResponse(timeStamp, verifyOtpResponse);
                    Log.d(ContentValues.TAG, verifyOtpResponse.toString());
                    if(verifyOtpResponse.getStatus().equalsIgnoreCase("true")){
                        ud_otp_commit_status_field.set("VERIFIED");
                        ud_otp_commit_status = "VERIFIED";
                        ud_otp_verified_status.set(true);
                        Constants.SAME_DAY_REASSIGN_VERIFIED="VERIFIED";
                    } else{
                        ud_otp_commit_status = "NONE";
                        ud_otp_commit_status_field.set("NONE");
                        ud_otp_verified_status.set(false);
                        Constants.SAME_DAY_REASSIGN_VERIFIED="NONE";
                    }
                }
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                setIsLoading(false);
                if(dialog.isShowing())
                    dialog.dismiss();
                if(response.getStatus().equalsIgnoreCase("true")){
                    ud_otp_commit_status = "VERIFIED";
                    ud_otp_commit_status_field.set("VERIFIED");
                    Constants.SAME_DAY_REASSIGN_VERIFIED="VERIFIED";
                } else{
                    getNavigator().showError(response.getDescription());
                    ud_otp_commit_status = "NONE";
                    ud_otp_commit_status_field.set("NONE");
                    Constants.SAME_DAY_REASSIGN_VERIFIED="NONE";
                }
            }, throwable -> {
                if(dialog.isShowing())
                    dialog.dismiss();
                String error;
                try{
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                    getNavigator().showError(error);
                } catch(Exception e){
                    e.printStackTrace();
                    getNavigator().showError(e.getMessage());
                }
                //setIsLoading(false);
                //getNavigator().onHandleError(new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription());
            }));
        } catch(Exception e){
            e.printStackTrace();
            if(dialog.isShowing())
                dialog.dismiss();
            getNavigator().showError(e.getMessage());
        }
    }
    public void showCallAndSmsDialog(Context context , String awb, String drs_id, String consigneealternatemobile, String checkcall){
        Dialog dialog = new Dialog(getNavigator().getActivityContext(), R.style.RoundedCornersDialog);
        dialog.setContentView(R.layout.dialog_sms_call);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Button sms = dialog.findViewById(R.id.bt_sms);
        Button call = dialog.findViewById(R.id.bt_call);
        ImageView crssdialog = dialog.findViewById(R.id.crssdialog);
        TextView txtcall = dialog.findViewById(R.id.txtcall);
        Button btsmsalternate = dialog.findViewById(R.id.bt_sms_alternate);
        sms.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.eds_button));
        call.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.eds_button));
        btsmsalternate.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.eds_button));
        if(!consigneealternatemobile.equals("0") && !consigneealternatemobile.equals("null"))
        {
            btsmsalternate.setVisibility(View.VISIBLE);
            btsmsalternate.setText("SMS on Alternate No. "+consigneealternatemobile);
        }

        if(checkcall.equals(""))
        {
            call.setVisibility(View.GONE);
            txtcall.setVisibility(View.INVISIBLE);
        }

        btsmsalternate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                dialog.dismiss();
                getNavigator().resendSms(true);
            }
        });

        crssdialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                dialog.dismiss();

            }
        });
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                dialog.dismiss();
                getNavigator().resendSms(false);
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                dialog.dismiss();
                getNavigator().VoiceCallOtp();
                //doVoiceOTPApi(awb ,drs_id);

            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public void doVoiceOTPApi(String awb, String drs_id, String message_type){
        try{
            VoiceOTP voiceOTP = new VoiceOTP();
            voiceOTP.awb = awb;
            voiceOTP.drs_id = drs_id;
            voiceOTP.employee_code = getDataManager().getEmp_code();
            voiceOTP.message_type = message_type;
            // Change by Nitin
            voiceOTP.product_type = "EDS";
            getCompositeDisposable().add(getDataManager().doVoiceOtpApiCall(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), voiceOTP).doOnSuccess(new Consumer<VoiceOTPResponse>() {
                @Override
                public void accept(VoiceOTPResponse verifyOtpResponse) throws Exception{
                    Log.d(ContentValues.TAG, verifyOtpResponse.toString());
                }
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                setIsLoading(false);
                try{
                    counter_skip++;
                    if(response.code == 0){
                        getNavigator().showError(response.description);
                        getNavigator().voiceTimer();
                    } else if(response.code == 1){
                        getNavigator().showError(response.description);
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
            }, throwable -> {
                String error;
                try{
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                    getNavigator().showError(error);
                } catch(Exception e){
                    e.printStackTrace();
                    getNavigator().showError(e.getMessage());
                }
            }));
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
    }

    public void getRemarkCount(long awb){
        try{
            getCompositeDisposable().add(getDataManager().getRemarks(awb).observeOn(getSchedulerProvider().io()).subscribeOn(getSchedulerProvider().io()).subscribe(new Consumer<Remark>() {
                @Override
                public void accept(Remark remark) throws Exception{
                    edsCommit.setTrying_reach(String.valueOf(getDataManager().getTryReachingCount(String.valueOf(awb+Constants.TRY_RECHING_COUNT))));
                    edsCommit.setTechpark(String.valueOf(getDataManager().getSendSmsCount(String.valueOf(awb+Constants.TECH_PARK_COUNT))));
                }
            }, throwable -> {
                throwable.printStackTrace();
            }));
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
    }

    public void addRemarks(Remark remark){
        try{
            getCompositeDisposable().add(getDataManager().insertRemark(remark).observeOn(getSchedulerProvider().io()).subscribeOn(getSchedulerProvider().io()).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception{

                }
            }, throwable -> {
                throwable.printStackTrace();
            }));
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
    }
}
