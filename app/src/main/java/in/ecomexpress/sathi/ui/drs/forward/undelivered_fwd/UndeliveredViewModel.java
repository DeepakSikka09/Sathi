package in.ecomexpress.sathi.ui.drs.forward.undelivered_fwd;

import static in.ecomexpress.sathi.utils.Constants.DISTANCE_API_KEY;

import android.annotation.SuppressLint;
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
import android.telephony.PhoneStateListener;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;

import java.io.File;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.data.callbridge.CallApiRequest;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.repo.local.data.commit.PushApi;
import in.ecomexpress.sathi.repo.local.db.model.ForwardUndeliveredReasonCodeList;
import in.ecomexpress.sathi.repo.local.db.model.ImageModel;
import in.ecomexpress.sathi.repo.local.db.model.Remark;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.call.Call;
import in.ecomexpress.sathi.repo.remote.model.drs_list.AmazonReschedule.AmazonScheduleRequest;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.masterdata.CallbridgeConfiguration;
import in.ecomexpress.sathi.repo.remote.model.masterdata.ForwardReasonCodeMaster;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterDataAttributeResponse;
import in.ecomexpress.sathi.repo.remote.model.otp.resendotp.GenerateUDOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.otp.resendotp.ResendOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.otp.verifyotp.VerifyUDOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.reschedule.ResheduleDetailsRequest;
import in.ecomexpress.sathi.repo.remote.model.voice_otp.VoiceOTP;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.CryptoUtils;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.LocationHelper;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.exceptions.OnErrorNotImplementedException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.adapter.rxjava2.HttpException;

@HiltViewModel
public class UndeliveredViewModel extends BaseViewModel<IUndeliveredNavigator> {
    static IDataManager iDataManager;
    static ISchedulerProvider iSchedulerProvider;
    public final ObservableField<String> consigneeContactNumber = new ObservableField<>("");
    public final ObservableField<String> isScanAwb = new ObservableField<>();
    public final ObservableField<String> awbNo = new ObservableField<>();
    public ObservableArrayList<ForwardReasonCodeMaster> childForwardCodeMaster = new ObservableArrayList<>();
    public int call_alert_number = 0;
    public boolean isCallRecursionDailogRunning = true;
    public ObservableField<Boolean> ud_otp_verified_status = new ObservableField<>(false);
    public ObservableField<String> ud_otp_commit_status_field = new ObservableField<>("NONE");
    public ObservableField<String> rd_otp_commit_status_field = new ObservableField<>("NONE");
    CallbridgeConfiguration mymasterDataReasonCodeResponse = null;
    ProgressDialog dialog;
    int isCall = 0;
    long inscan_date;
    int total_attempts;
    String parentAWBNo;
    List<ImageModel> mimageModels = new ArrayList<>();
    List<ForwardUndeliveredReasonCodeList> forwardUndeliveredReasonCodeLists = new ArrayList<>();
    Map<String, List<ForwardReasonCodeMaster>> childGroup;
    List<String> spinnerReasonValues = new ArrayList<>();
    List<String> ChildSpinnerValues = new ArrayList<>();
    List<String> parentGroupSpinnerValues = new ArrayList<>();
    ArrayList<String> schedule_dates = new ArrayList<>();
    Dialog calldialog = null;
    boolean isStopRecursion = false;
    long request_time = 0L;
    long response_time = 0L;
    int counter_skip = 0;
    String ud_otp_commit_status = "NONE";
    String rd_otp_commit_status = "NONE";
    private ForwardCommit forwardCommit;

    @Inject
    public UndeliveredViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);
        iDataManager = dataManager;
        iSchedulerProvider = schedulerProvider;
    }

    public static ForwardReasonCodeMaster getSelectTemplet() {
        ForwardReasonCodeMaster reasonSelectReason = new ForwardReasonCodeMaster();
        reasonSelectReason.setReasonCode(-1);
        reasonSelectReason.setReasonMessage(Constants.SELECT);
        MasterDataAttributeResponse masterDataAttributeResponse = new MasterDataAttributeResponse();
        masterDataAttributeResponse.setcALLM(false);
        masterDataAttributeResponse.setiMG(false);
        masterDataAttributeResponse.setoTP(false);
        masterDataAttributeResponse.setrCHD(false);
        masterDataAttributeResponse.setUD_OTP(false);
        reasonSelectReason.setMasterDataAttributeResponse(masterDataAttributeResponse);
        return reasonSelectReason;
    }

    //for Group Chooser
    public void onChooseGroupSpinner(AdapterView<?> parent, View view, int pos, long id) {
        List<ForwardReasonCodeMaster> forwardReasonCodeMasters = childGroup.get(parentGroupSpinnerValues.get(pos));
        ChildSpinnerValues.clear();
        childForwardCodeMaster.clear();
        if (forwardReasonCodeMasters != null) {
            childForwardCodeMaster.add(0, getSelectTemplet());
            ChildSpinnerValues.add(Constants.SELECT);
            for (ForwardReasonCodeMaster forwardReasonCodeMaster : forwardReasonCodeMasters) {
                childForwardCodeMaster.add(forwardReasonCodeMaster);
                ChildSpinnerValues.add(forwardReasonCodeMaster.getReasonMessage());
            }
        }

        getNavigator().onChooseGroupSpinner(parentGroupSpinnerValues.get(pos));
        getNavigator().setChildSpinnerValues(ChildSpinnerValues);
    }

    //for Child Chooser
    public void onChooseChildSpinner(AdapterView<?> parent, View view, int pos, long id) {
        getNavigator().onChooseChildSpinner(childForwardCodeMaster.get(pos));
    }

    public void onChooseReasonSpinner(AdapterView<?> parent, View view, int pos, long id) {
        getNavigator().onChooseReasonSpinner(forwardUndeliveredReasonCodeLists.get(pos));
    }

    public void onChooseDateSpinner(AdapterView<?> parent, View view, int pos, long id) {
        getNavigator().onChooseDateSpinner(schedule_dates.get(pos));
    }

    public void onDatePickerClick() {
        getNavigator().onDatePicker();
    }

    public void callApi(String nyka,boolean failFlag, String awb_number, String drs_id) {

        if (nyka.equalsIgnoreCase("true")){
            try {
                //call bridge
                request_time = System.currentTimeMillis();
                UndeliveredViewModel.this.setIsLoading(true);
                getCompositeDisposable().add(getDataManager().doForwardCallStatusApiCall(
                        getDataManager().getAuthToken(),
                        getDataManager().getEcomRegion(),
                        call_alert_number,
                        getDataManager().getEmp_code(),
                        awb_number,
                        drs_id,
                        getDataManager().getShipperId())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribeOn(getSchedulerProvider().io())
                        .subscribe(forwardCallResponse -> {
                    UndeliveredViewModel.this.setIsLoading(false);
                    if (forwardCallResponse.getStatus().equalsIgnoreCase("true")) {
                        getNavigator().showError(forwardCallResponse.getResponse());
                        if (calldialog != null) {
                            calldialog.dismiss();
                        }
                        getNavigator().undeliverShipment(failFlag, true);
                    } else {
                        response_time = System.currentTimeMillis();
                        if (forwardCallResponse.isCall_again_required()) {
                            getNavigator().undeliverShipment(failFlag, false);
                        } else {
                            if (getDataManager().isCallStatusAPIRecursion()) {
                                call_alert_number = call_alert_number + 1;
                                long time_difference = response_time - request_time;
                                if (time_difference < getDataManager().getRequestRsponseTime()) {
                                    long diffrence = getDataManager().getRequestRsponseTime() - time_difference;
                                    final Handler handler = new Handler();
                                    handler.postDelayed(() -> {
                                        callApi(nyka,failFlag, forwardCommit.getAwb(), forwardCommit.getDrs_id());
                                        if (isCallRecursionDailogRunning) {
                                            getNavigator().getActivityContext().runOnUiThread(() -> showCallAPIDelayDialog(nyka,failFlag, awb_number, drs_id));
                                        }
                                    }, diffrence);
                                } else {
                                    callApi(nyka,failFlag, forwardCommit.getAwb(), forwardCommit.getDrs_id());
                                    if (isCallRecursionDailogRunning) {
                                        showCallAPIDelayDialog(nyka,failFlag, awb_number, drs_id);
                                    }
                                }
                            } else {
                                if (isStopRecursion) {
                                    return;
                                }
                                call_alert_number = call_alert_number + 1;
                                showCallAPIDelayDialog(nyka,failFlag, awb_number, drs_id);
                            }
                        }
                    }
                }, throwable ->{
                            UndeliveredViewModel.this.setIsLoading(false);
                            if (throwable instanceof HttpException) {
                                HttpException httpException = (HttpException) throwable;
                                int statusCode = httpException.code();
                                if (statusCode == 404) {
                                    // Handle 404 error
                                    getNavigator().showError("Resource not found (404)");
                                } else {
                                    // Handle other HTTP errors
                                    getNavigator().showError("HTTP error code: " + statusCode);
                                }
                            } else {
                                // Handle other errors (non-HTTP)
                                getNavigator().showError(throwable.getMessage());
                            }
                            Logger.e(UndeliveredViewModel.class.getName(), throwable.getMessage());
                        })
                );
            } catch (Exception e) {
                getNavigator().showError(e.getMessage());
                UndeliveredViewModel.this.setIsLoading(false);
                Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            }
        }

        else{
            getNavigator().undeliverShipment(failFlag, true);
        }
    }
    public void callBridgeCheckStatusApi(String nyka,boolean failFlag, String awb_number, String drs_id) {

        if (nyka.equalsIgnoreCase("true")){
            try {
                //call bridge
                request_time = System.currentTimeMillis();
                UndeliveredViewModel.this.setIsLoading(true);
                getCompositeDisposable().add(getDataManager().doForwardCallStatusApiCall(
                                getDataManager().getAuthToken(),
                                getDataManager().getEcomRegion(),
                                call_alert_number,
                                getDataManager().getEmp_code(),
                                awb_number,
                                drs_id,
                                getDataManager().getShipperId())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribeOn(getSchedulerProvider().io())
                        .subscribe(forwardCallResponse -> {
                            UndeliveredViewModel.this.setIsLoading(false);
                            if (forwardCallResponse.getStatus().equalsIgnoreCase("true")) {
                                getNavigator().showError(forwardCallResponse.getResponse());
                                if (calldialog != null) {
                                    calldialog.dismiss();
                                }
                                getNavigator().undeliverShipment(failFlag, true);
                            }
                            else {
                                getNavigator().onCallBridgeCheckStatus();
                            }

                        }, throwable ->{
                            UndeliveredViewModel.this.setIsLoading(false);
                            if (throwable instanceof HttpException) {
                                HttpException httpException = (HttpException) throwable;
                                int statusCode = httpException.code();
                                if (statusCode == 404) {
                                    // Handle 404 error
                                    getNavigator().showError("Resource not found (404)");
                                } else {
                                    // Handle other HTTP errors
                                    getNavigator().showError("HTTP error code: " + statusCode);
                                }
                            } else {
                                // Handle other errors (non-HTTP)
                                getNavigator().showError(throwable.getMessage());
                            }
                            Logger.e(UndeliveredViewModel.class.getName(), throwable.getMessage());
                        })
                );
            } catch (Exception e) {
                getNavigator().showError(e.getMessage());
                UndeliveredViewModel.this.setIsLoading(false);
                Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            }
        }

        else{
            getNavigator().undeliverShipment(failFlag, true);
        }
    }

    public String loginDate() {
        return getDataManager().getLoginDate();
    }

    public void onCaptureImageClick() {
        getNavigator().onCaptureImage();
    }

    @SuppressLint("CheckResult")
    public void uploadAWSImage(String imageUri, String imageCode, String imageKey, Boolean isCommit) {
        saveImageDB(imageUri, imageCode, imageKey, -1, 0);
        getNavigator().setBitmap();
    }

    public void onUndeliveredApiCall(Context context, int code, String reschedule, String compositeKey) {
        try {
            forwardCommit.setReceived_by_name("");
            forwardCommit.setStatus("2");
            forwardCommit.setReceived_by_relation("");
            forwardCommit.setReschedule_date(reschedule);
            forwardCommit.setAttempt_reason_code(String.valueOf(code));

            forwardCommit.setDeclared_value(forwardCommit.getDeclared_value());


            forwardCommit.setShipment_id(getDataManager().getShipperId());
            forwardCommit.setPayment_id("");
            Gson gson = new Gson();
            Type type = new TypeToken<List<ForwardCommit.Amz_Scan>>() {
            }.getType();
            ArrayList<ForwardCommit.Amz_Scan> amz_scans = gson.fromJson(getDataManager().getAmazonList(), type);
            forwardCommit.setAmz_scan(amz_scans);
            if (getDataManager().getDlightSuccessEncrptedOTPType() != null && !getDataManager().getDlightSuccessEncrptedOTPType().equalsIgnoreCase("")) {
                forwardCommit.setDlight_encrption_otp_type(getDataManager().getDlightSuccessEncrptedOTPType());
            }
            try {
                if (!String.valueOf(in.ecomexpress.geolocations.Constants.latitude).equalsIgnoreCase("0.0") && !String.valueOf(in.ecomexpress.geolocations.Constants.longitude).equalsIgnoreCase("0.0")) {
                    forwardCommit.setLocation_lat(String.valueOf(in.ecomexpress.geolocations.Constants.latitude));
                    forwardCommit.setLocation_long(String.valueOf(in.ecomexpress.geolocations.Constants.longitude));
                } else if (!Constants.CURRENT_LATITUDE.equalsIgnoreCase("0.0") && !Constants.CURRENT_LONGITUDE.equalsIgnoreCase("0.0")) {
                    forwardCommit.setLocation_lat(Constants.CURRENT_LATITUDE);
                    forwardCommit.setLocation_long(Constants.CURRENT_LONGITUDE);
                } else {
                    forwardCommit.setLocation_lat(String.valueOf(getDataManager().getCurrentLatitude()));
                    forwardCommit.setLocation_long(String.valueOf(getDataManager().getCurrentLongitude()));
                }
            } catch (Exception e) {
                Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            }
            forwardCommit.setAttempt_type("FWD");
            forwardCommit.setDrs_commit_date_time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            forwardCommit.setTrip_id(getDataManager().getTripId());
            forwardCommit.setFe_emp_code(getDataManager().getCode());
            if (getDataManager().getConsigneeProfileValue().equalsIgnoreCase("W")) {
                forwardCommit.setFlag_of_warning("W");
            } else {
                forwardCommit.setFlag_of_warning("N");
            }

            if (Constants.uD_OTP_API_CHECK && ud_otp_commit_status_field.get().equalsIgnoreCase("VERIFIED")) {
                forwardCommit.setUd_otp(ud_otp_commit_status_field.get());
                forwardCommit.setRd_otp("NONE");
            } else if (Constants.rD_OTP_API_CHECK && ud_otp_commit_status_field.get().equalsIgnoreCase("VERIFIED")) {
                forwardCommit.setRd_otp("VERIFIED");
                forwardCommit.setUd_otp("NONE");

            } else {
                forwardCommit.setUd_otp(ud_otp_commit_status_field.get());
                forwardCommit.setRd_otp(rd_otp_commit_status_field.get());
            }

            //check first that this is a mps shipment or not if its mps shipment than
            //save all forward items
            parentAWBNo = forwardCommit.getAwb();
            isMPSShipment(forwardCommit, compositeKey);

        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void createCommitPacketCashCollection(ForwardCommit forwardCommits, int code, String reschedule, String compositeKey) {
        try {
            forwardCommit = forwardCommits;
            mimageModels = getImagesList(forwardCommit.getAwb(), code, reschedule, compositeKey);

        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    private void isMPSShipment(ForwardCommit forwardCommit, String compositeKey) {
        try {
            getCompositeDisposable().add(getDataManager().getForwardDRS(compositeKey).subscribeOn(getSchedulerProvider().io()).subscribe(drsForwardTypeResponse -> {
                String mpsShipment = drsForwardTypeResponse.mpsShipment;
                List<ForwardCommit> fwd = new ArrayList<>();
                if (mpsShipment != null && !mpsShipment.isEmpty()) {
                    String awbsNos = drsForwardTypeResponse.mpsAWBs;
                    awbsNos = awbsNos.substring(1).trim();
                    awbsNos = awbsNos.substring(0, awbsNos.length() - 1);
                    String[] awbArr = awbsNos.split(",");
                    for (String awb : awbArr) {
                        try {
                            ForwardCommit fwdCommit = (ForwardCommit) forwardCommit.clone();
                            fwdCommit.setAwb(awb.trim());
                            fwdCommit.setParent_awb(String.valueOf(drsForwardTypeResponse.getAwbNo()));
                            if (getDataManager().getConsigneeProfileValue().equalsIgnoreCase("W")) {
                                fwdCommit.setFlag_of_warning("W");
                            } else {
                                fwdCommit.setFlag_of_warning("N");
                            }
                            if (Constants.uD_OTP_API_CHECK && ud_otp_commit_status_field.get().equalsIgnoreCase("VERIFIED")) {
                                forwardCommit.setUd_otp(ud_otp_commit_status_field.get());
                                forwardCommit.setRd_otp("NONE");
                            } else if (Constants.rD_OTP_API_CHECK && ud_otp_commit_status_field.get().equalsIgnoreCase("VERIFIED")) {
                                forwardCommit.setRd_otp("VERIFIED");
                                forwardCommit.setUd_otp("NONE");
                            } else {
                                forwardCommit.setUd_otp(ud_otp_commit_status_field.get());
                                forwardCommit.setRd_otp(rd_otp_commit_status_field.get());
                            }
                            fwd.add(fwdCommit);
                        } catch (CloneNotSupportedException e) {
                            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
                        }
                    }
                    uploadForwardShipment(fwd, drsForwardTypeResponse.getAwbNo(), compositeKey);
                    updateMPSShipmentStatus(awbArr);
                } else {
                    forwardCommit.setParent_awb(String.valueOf(drsForwardTypeResponse.getAwbNo()));
                    if (getDataManager().getConsigneeProfileValue().equalsIgnoreCase("W")) {
                        forwardCommit.setFlag_of_warning("W");
                    } else {
                        forwardCommit.setFlag_of_warning("N");
                    }
                    if (Constants.uD_OTP_API_CHECK && ud_otp_commit_status_field.get().equalsIgnoreCase("VERIFIED")) {
                        forwardCommit.setUd_otp(ud_otp_commit_status_field.get());
                        forwardCommit.setRd_otp("NONE");
                    } else if (Constants.rD_OTP_API_CHECK && ud_otp_commit_status_field.get().equalsIgnoreCase("VERIFIED")) {
                        forwardCommit.setRd_otp("VERIFIED");
                        forwardCommit.setUd_otp("NONE");
                    } else {
                        forwardCommit.setUd_otp(ud_otp_commit_status_field.get());
                        forwardCommit.setRd_otp(rd_otp_commit_status_field.get());
                    }
                    fwd.add(forwardCommit);
                    uploadForwardShipment(fwd, drsForwardTypeResponse.getAwbNo(), compositeKey);
                    updateShipmentStatus(compositeKey);
                }
            }));
        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
            restApiErrorHandler.writeErrorLogs(0, e.getMessage());
        }
    }

    @SuppressLint("CheckResult")
    private void uploadForwardShipment(List<ForwardCommit> forwardCommits, long parentAWB, String composite_key) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        HashMap<String, String> tokens = new HashMap<>();
        tokens.put(Constants.TOKEN, getDataManager().getAuthToken());
        tokens.put(Constants.EMP_CODE, getDataManager().getCode());

        try {
            compositeDisposable.add(getDataManager().doFWDCommitApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), tokens, forwardCommits).subscribeOn(getSchedulerProvider().io()).subscribe(forwardCommitResponse -> {
                if (forwardCommitResponse.getStatus()) {
                    int shipement_status = 0;
                    String compositeKey = "";
                    if (forwardCommitResponse.getResponse().getShipment_status().equalsIgnoreCase(Constants.UNDELIVERED)) {
                        shipement_status = Constants.SHIPMENT_UNDELIVERED_STATUS;
                    } else {
                        shipement_status = Constants.SHIPMENT_DELIVERED_STATUS;
                    }
                    try {
                        compositeKey = forwardCommitResponse.getResponse().getDrs_no() + forwardCommitResponse.getResponse().getAwb_no();
                    } catch (Exception e) {
                        Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
                    }
                    getDataManager().updateForwardStatus(compositeKey, shipement_status).subscribe(aBoolean -> {
                        updateSyncStatusInDRSFWDTable(forwardCommitResponse.getResponse().getDrs_no() + "" + forwardCommitResponse.getResponse().getAwb_no(), GlobalConstant.CommitStatus.CommitSynced);
                        // Setting call preference after sync:-
                        getDataManager().setCallClicked(forwardCommitResponse.getResponse().getAwb_no() + "ForwardCall", true);
                        compositeDisposable.add(getDataManager().deleteSyncedImage(forwardCommitResponse.getResponse().getAwb_no()).subscribe(aBoolean1 -> {}));
                        saveCommitUpload(forwardCommits, parentAWB, composite_key);
                    }, throwable -> {
                        throwable.printStackTrace();
                        saveCommit(forwardCommits, parentAWB, composite_key);
                    });
                }
            }, throwable -> {
                saveCommit(forwardCommits, parentAWB, composite_key);
                throwable.printStackTrace();
            }));
        } catch (Exception e) {
            saveCommit(forwardCommits, parentAWB, composite_key);
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());

        }
    }

    private void updateSyncStatusInDRSFWDTable(String composite_key, int i) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager().updateSyncStatusFWD(composite_key, i).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {
        }, throwable -> {
        }));
    }

    private void updateMPSShipmentStatus(String[] awbArr) {
        try {
            getCompositeDisposable().add(getDataManager().updateForwardMPSShipmentStatus(awbArr, Constants.SHIPMENT_UNDELIVERED_STATUS).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> onSubmitSuccess(forwardCommit), throwable -> throwable.printStackTrace()));
        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
            restApiErrorHandler.writeErrorLogs(0, e.getMessage());
        }
    }

    private void getProfileLatLng(DRSForwardTypeResponse drsForwardTypeResponse) {
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
                            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
                        }
                        if (consigneeLatitude == 0.0 || consigneeLongitude == 0.0) {
                            getNavigator().setConsigneeDistance(0);
                            return;
                        }
                        int meter = (int) getDistaneBetweenLocations(new LatLng(consigneeLatitude, consigneeLongitude));
                        getNavigator().setConsigneeDistance(meter);
                    }
                } catch (Exception e) {
                    Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
                }
            }, throwable -> Log.e("error", throwable.getMessage())));
        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
        }
    }

    private void updateShipmentStatus(String composite_key) {
        try {
            getCompositeDisposable().add(getDataManager().updateForwardStatus(composite_key, Constants.SHIPMENT_UNDELIVERED_STATUS).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> onSubmitSuccess(forwardCommit)));
        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
            restApiErrorHandler.writeErrorLogs(0, e.getMessage());
        }
    }

    public void onForwardDRSCommit(ForwardCommit forwardCommit) {
        forwardCommit.setTrip_id(getDataManager().getTripId());
        this.forwardCommit = forwardCommit;
        awbNo.set(forwardCommit.getAwb());
    }

    public void onSubmitSuccess(ForwardCommit forwardCommit) {
        if (parentAWBNo != null) forwardCommit.setAwb(parentAWBNo);
        getNavigator().onSubmitSuccess(forwardCommit);
    }

    public void onSubmitClick() {
        getNavigator().OnSubmitClick();
    }

  /*  public void onBackClick() {
        getNavigator().onBackClick();
    }*/

    public void getAllUndeliveredReasonCode(String shipmentType, String isSecure, String isMps) {
        try {
            getCompositeDisposable().add(getDataManager().getAllForwardUndeliveredReasonCode(shipmentType, isSecure, isMps).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(forwardReasonCodeMasters -> {
                Log.d("forward.undelivered_fwd", "forwardReasonCode: " + forwardReasonCodeMasters.toString());
                childGroup = new HashMap<>();
                if (forwardReasonCodeMasters != null) {
                    try {
                        getNavigator().visible(true);
                        childGroup = groupRecord(forwardReasonCodeMasters);
                        getNavigator().setSpinner(spinnerReasonValues);
                    } catch (Exception e) {
                        RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                        restApiErrorHandler.writeErrorLogs(0, e.getMessage());
                        Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
                    }
                }
            }));
        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public Map<String, List<ForwardReasonCodeMaster>> groupRecord(List<ForwardReasonCodeMaster> forwardReasonCodeMasters) {
        Map<String, List<ForwardReasonCodeMaster>> map = new HashMap<>();
        try {
            parentGroupSpinnerValues.clear();
            parentGroupSpinnerValues.add(Constants.SELECT);
            for (ForwardReasonCodeMaster forwardReasonCodeMaster : forwardReasonCodeMasters) {
                String key = forwardReasonCodeMaster.getSubGroup();
                if (Constants.CONSIGNEE_PROFILE && forwardReasonCodeMaster.getMasterDataAttributeResponse().isPR_NA()) {

                } else {
                    if (map.containsKey(key)) {
                        List<ForwardReasonCodeMaster> list = map.get(key);
                        list.add(forwardReasonCodeMaster);
                    } else {
                        List<ForwardReasonCodeMaster> list = new ArrayList<>();
                        list.add(forwardReasonCodeMaster);
                        parentGroupSpinnerValues.add(key);
                        map.put(key, list);
                    }
                }
            }
            getNavigator().setParentGroupSpinnerValues(parentGroupSpinnerValues);
        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            getNavigator().showError(e.getMessage());
        }
        return map;
    }

    /**
     *
     * @param imageUri--- imageuri
     * @param imageCode-- image code
     * @param name-- name of image
     * @param imageId-- image id
     * @param sync_status--- sync status value
     */
    public void saveImageDB(String imageUri, String imageCode, String name, int imageId, int sync_status) {
        try {
            ImageModel imageModel = new ImageModel();
            imageModel.setDraNo(forwardCommit.getDrs_id());
            imageModel.setAwbNo(forwardCommit.getAwb());
            imageModel.setImageName(name);
            imageModel.setImage(imageUri);
            imageModel.setImageCode(imageCode);
            imageModel.setStatus(sync_status);
            imageModel.setImageCurrentSyncStatus(sync_status);
            imageModel.setImageFutureSyncTime(Calendar.getInstance().getTimeInMillis());
            imageModel.setImageId(imageId);
            imageModel.setImageType(GlobalConstant.ImageTypeConstants.FWD);
            imageModel.setDate(Calendar.getInstance().getTimeInMillis());
            imageModel.setShipmentType(GlobalConstant.ShipmentTypeConstants.FWD);
            getCompositeDisposable().add(getDataManager().saveImage(imageModel).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {}));
        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    private void saveCommit(List<ForwardCommit> forwardCommit, long parentAWB, String compositeKey) {
        PushApi pushApi = new PushApi();
        pushApi.setAwbNo(parentAWB);
        pushApi.setCompositeKey(compositeKey);
        pushApi.setAuthtoken(getDataManager().getAuthToken());
        try {
            ObjectMapper mapper = new ObjectMapper();
            pushApi.setRequestData(mapper.writeValueAsString(forwardCommit));
            pushApi.setShipmentStatus(Constants.SHIPMENT_ASSIGNED_STATUS);
            pushApi.setShipmentDeliveryStatus("3");
            pushApi.setShipmentCaterogy(GlobalConstant.ShipmentTypeConstants.FWD);
        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            getNavigator().showError(e.getMessage());
        }
        try {
            getCompositeDisposable().add(getDataManager().saveCommitPacket(pushApi).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> updateForwardCallAttemptedWithZero(String.valueOf(pushApi.getAwbNo()))));
        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    private void saveCommitUpload(List<ForwardCommit> forwardCommit, long parentAWB, String compositeKey) {
        PushApi pushApi = new PushApi();
        pushApi.setAwbNo(parentAWB);
        pushApi.setCompositeKey(compositeKey);
        pushApi.setAuthtoken(getDataManager().getAuthToken());
        try {
            ObjectMapper mapper = new ObjectMapper();
            pushApi.setRequestData(mapper.writeValueAsString(forwardCommit));
            pushApi.setShipmentStatus(2);
            pushApi.setShipmentDeliveryStatus("3");
            pushApi.setShipmentCaterogy(GlobalConstant.ShipmentTypeConstants.FWD);
        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            getNavigator().showError(e.getMessage());
        }
        try {
            getCompositeDisposable().add(getDataManager().saveCommitPacket(pushApi).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {

                int isCallattempted = getisCallattemptedUndeliverCount(String.valueOf(parentAWB));
                if (isCallattempted == 0) {
                    Constants.shipment_undelivered_count++;
                }
                updateForwardCallAttemptedWithZero(String.valueOf(pushApi.getAwbNo()));
                getUDORRCHEDShipmentStatus();
            }));
        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    private void getUDORRCHEDShipmentStatus() {
        if (forwardCommit.getUd_otp().equalsIgnoreCase("VERIFIED") || forwardCommit.getRd_otp().equalsIgnoreCase("VERIFIED")) {
            getDataManager().setFWD_UD_RD_OTPVerfied(forwardCommit.getAwb() + "Forward", true);
        }
    }

    public void getisCallattempted(String awb) {
        try {
            getCompositeDisposable().add(getDataManager().getisForwardCallattempted(Long.parseLong(awb)).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(isCallattempted -> {
                try {
                    isCall = Integer.parseInt(String.valueOf(isCallattempted));
                    getNavigator().isCall(isCall);
                } catch (Exception e) {
                    RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                    restApiErrorHandler.writeErrorLogs(0, e.getMessage());
                    Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
                }
            }, throwable -> {
                RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(throwable.getCause());
                restApiErrorHandler.writeErrorLogs(0, throwable.getMessage());
            }));
        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            getNavigator().showError(e.getMessage());
            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
            restApiErrorHandler.writeErrorLogs(0, e.getMessage());
        }
    }

    public int getisCallattemptedUndeliverCount(String awb) {
        try {
            getCompositeDisposable().add(getDataManager().getisForwardCallattempted(Long.parseLong(awb)).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(isCallattempted -> {
                try {
                    isCall = Integer.parseInt(String.valueOf(isCallattempted));
                    getNavigator().isCall(isCall);
                } catch (Exception e) {
                    RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                    restApiErrorHandler.writeErrorLogs(0, e.getMessage());
                    Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
                }
            }, throwable -> {
                RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(throwable.getCause());
                restApiErrorHandler.writeErrorLogs(0, throwable.getMessage());
            }));
        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            getNavigator().showError(e.getMessage());
            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
            restApiErrorHandler.writeErrorLogs(0, e.getMessage());
        }
        return isCall;
    }

    public void updateForwardCallAttempted(String awb) {
        try {
            getCompositeDisposable().add(getDataManager().updateForwardCallAttempted(Long.valueOf(awb), Constants.callAttempted).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> getNavigator().dismissCallDialog()));
        } catch (Exception e) {
            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
            restApiErrorHandler.writeErrorLogs(0, e.getMessage());
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void updateForwardCallAttemptedWithZero(String awb) {
        try {
            getCompositeDisposable().add(getDataManager().updateForwardCallAttempted(Long.valueOf(awb), 0).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {
                try {
                    getNavigator().dismissCallDialog();
                } catch (Exception e) {
                    Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
                }

            }));
        } catch (Exception e) {
            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
            restApiErrorHandler.writeErrorLogs(0, e.getMessage());
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void updateForwardpinCallAttempted(String awb) {

        try {
            getCompositeDisposable().add(getDataManager().updateForwardpinCallAttempted(Long.valueOf(awb), Constants.callAttempted).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> getNavigator().dismissCallDialog()));
        } catch (Exception e) {
            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
            restApiErrorHandler.writeErrorLogs(0, e.getMessage());
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void makeCallBridgeApiCall(String callBridgeKey, String awb, String drsid, String type) {
        CallApiRequest request = new CallApiRequest();
        request.setAwb(awb);
        request.setCb_api_key(callBridgeKey);
        request.setDrs_id(String.valueOf(drsid));
        Log.d("request123", request.toString());
        final long timeStamp = Calendar.getInstance().getTimeInMillis();
        writeRestAPIRequst(timeStamp, request);
        try {
            getCompositeDisposable().add(getDataManager().doCallBridgeApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(callApiResponse -> {
                Log.d("forward.undelivered_fwd", callApiResponse.toString());
                Constants.rCheduleFlag = true;
                writeRestAPIResponse(timeStamp, callApiResponse);
                if (callApiResponse != null) {
                    try {
                        if (callApiResponse.isStatus()) {
                            if (type.equals(Constants.FWD)) {
                                updateForwardCallAttempted(awb);
                            }
                        }
                        getNavigator().onHandleError(callApiResponse);
                    } catch (NullPointerException | HttpException | OnErrorNotImplementedException e) {
                        Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }, throwable -> {
                getNavigator().showErrorMessage(throwable.getMessage().contains("HTTP 500 "));
                throwable.printStackTrace();
            }));
        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }


    public void getGlobalConfigurationMaster() {
        try {
            getCompositeDisposable().add(getDataManager().getglobalConfigurationMasters().observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(globalConfigurationMasterList -> {
                try {
                    if (globalConfigurationMasterList != null) {
                        if (globalConfigurationMasterList.size() > 0) {
                            for (int i = 0; i < globalConfigurationMasterList.size(); i++) {
                                if (globalConfigurationMasterList.get(i).getConfigGroup() != null) {
                                    if (globalConfigurationMasterList.get(i).getConfigGroup().equalsIgnoreCase(Constants.RELATION)) {
                                        String relation = globalConfigurationMasterList.get(i).getConfigValue();
                                    }
                                }
                            }
                        }
                        getNavigator().getGlobal(globalConfigurationMasterList);
                    }
                } catch (Exception e) {
                    Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
                    RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                    restApiErrorHandler.writeErrorLogs(0, e.getMessage());
                }
            }, throwable -> {
                RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(throwable.getCause());
                restApiErrorHandler.writeErrorLogs(0, throwable.getMessage());
            }));
        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void getcallConfig() {
        try {
            getCompositeDisposable().add(getDataManager().loadallcallbridge().subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(callbridgeConfiguration -> {
                if (callbridgeConfiguration != null) {
                    try {
                        mymasterDataReasonCodeResponse = callbridgeConfiguration;
                        getNavigator().getCallConfig(mymasterDataReasonCodeResponse);
                    } catch (NullPointerException e) {
                        Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
                    } catch (Exception ex) {
                        String error, myerror;
                        error = new RestApiErrorHandler(ex).getErrorDetails().getEResponse().getDescription();
                        if (error.equalsIgnoreCase("HTTP 500 ")) {
                            myerror = "Internal Server Error! Please contact system admin.";
                            getNavigator().showError(myerror);
                        } else {
                            ex.printStackTrace();

                        }
                    }
                }
            }, throwable -> throwable.printStackTrace()));
        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void fetchForwardShipment(String drsid, String awbNo) {
        try {

            String compositeKey = drsid + "" + Long.valueOf(awbNo);

            getCompositeDisposable().add(getDataManager().getForwardDRSCompositeKey(Long.valueOf(awbNo)).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(drsForwardTypeResponse -> getNavigator().onDRSForwardItemFetch(drsForwardTypeResponse), throwable -> {
                writeErrors(Calendar.getInstance().getTimeInMillis(), new Exception(throwable));
                throwable.printStackTrace();
            }));
        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void checkMeterRange(DRSForwardTypeResponse drsForwardTypeResponse) {
        try {
            getProfileLatLng(drsForwardTypeResponse);
        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void getConsigneeProfiling() {
        boolean enable = getDataManager().getConsigneeProfiling();
        getNavigator().setConsingeeProfiling(enable);
    }

    public void uploadImageServer(String imageName, String imageUri, String imageCode, long awbNo, int drsno, String activity_code, Bitmap bitmap, String compositeKey) {
        setIsLoading(true);
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
            Log.e("forward map", map + "");
            try {
                getCompositeDisposable().add(getDataManager().doImageUploadApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), GlobalConstant.ImageTypeConstants.FWD, headers, map, fileToUpload).doOnSuccess(imageQualityResponse -> Log.d(ContentValues.TAG, imageQualityResponse.toString())).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(imageUploadResponse -> {
                    setIsLoading(false);
                    try {
                        if (imageUploadResponse.getStatus().equalsIgnoreCase("Success")) {
                            getNavigator().setBitmap();
                            saveImageDB(imageUri, imageCode, imageName, imageUploadResponse.getImageId(), GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_COMPLETE);
                        } else {
                            getNavigator().showError("Image upload Api response failed.");
                        }
                    } catch (Exception e) {
                        getNavigator().showError("Exception while uploading image : " + e.getLocalizedMessage());
                        setIsLoading(false);
                    }
                }, throwable -> {
                    setIsLoading(false);
                    getNavigator().showError("Exception while uploading image : " + throwable.getLocalizedMessage());
                }));
            } catch (Exception e) {
                setIsLoading(false);
                getNavigator().showError("Exception while uploading image : " + e.getLocalizedMessage());
            }
        } catch (Exception e) {
            setIsLoading(false);
            getNavigator().showError("Exception while uploading image : " + e.getLocalizedMessage());
        }
    }

    private List<ImageModel> getImagesList(String awbNo, int code, String reschedule, String compositeKey) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager().getImages(awbNo).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(imageModels -> {
            try {
                mimageModels = imageModels;
                List<ForwardCommit.Image_response> list_image_responses = new ArrayList<>();
                if (!mimageModels.isEmpty()) {
                    for (int i = 0; i < mimageModels.size(); i++) {
                        ForwardCommit.Image_response image_response = new ForwardCommit.Image_response();
                        image_response.setImage_id(String.valueOf(mimageModels.get(i).getImageId()));
                        image_response.setImage_key(String.valueOf(mimageModels.get(i).getImageName()));
                        list_image_responses.add(image_response);
                    }
                    forwardCommit.setImage_response(list_image_responses);
                } else {
                    forwardCommit.setImage_response(list_image_responses);
                }
                Gson gson = new Gson();
                Type type = new TypeToken<List<ForwardCommit.Amz_Scan>>() {}.getType();
                ArrayList<ForwardCommit.Amz_Scan> amz_scans = gson.fromJson(getDataManager().getAmazonList(), type);
                forwardCommit.setAmz_scan(amz_scans);
                forwardCommit.setReceived_by_name("");
                forwardCommit.setStatus(Constants.UNDELIVERED);
                forwardCommit.setReceived_by_relation("");
                forwardCommit.setShipment_id(getDataManager().getShipperId());
                forwardCommit.setReschedule_date(reschedule);
                forwardCommit.setAttempt_reason_code(String.valueOf(code));
                forwardCommit.setDeclared_value(forwardCommit.getDeclared_value());

                forwardCommit.setPayment_id("");
                if (getDataManager().getConsigneeProfileValue().equalsIgnoreCase("W")) {
                    forwardCommit.setFlag_of_warning("W");
                } else {
                    forwardCommit.setFlag_of_warning("N");
                }
                try {
                    if (!String.valueOf(in.ecomexpress.geolocations.Constants.latitude).equalsIgnoreCase("0.0") && !String.valueOf(in.ecomexpress.geolocations.Constants.longitude).equalsIgnoreCase("0.0")) {
                        forwardCommit.setLocation_lat(String.valueOf(in.ecomexpress.geolocations.Constants.latitude));
                        forwardCommit.setLocation_long(String.valueOf(in.ecomexpress.geolocations.Constants.longitude));
                    } else if (!Constants.CURRENT_LATITUDE.equalsIgnoreCase("0.0") && !Constants.CURRENT_LONGITUDE.equalsIgnoreCase("0.0")) {
                        forwardCommit.setLocation_lat(Constants.CURRENT_LATITUDE);
                        forwardCommit.setLocation_long(Constants.CURRENT_LONGITUDE);
                    } else {
                        forwardCommit.setLocation_lat(String.valueOf(getDataManager().getCurrentLatitude()));
                        forwardCommit.setLocation_long(String.valueOf(getDataManager().getCurrentLongitude()));
                    }
                } catch (Exception e) {
                    Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
                }
                if (getDataManager().getDlightSuccessEncrptedOTPType() != null && !getDataManager().getDlightSuccessEncrptedOTPType().equalsIgnoreCase("")) {
                    forwardCommit.setDlight_encrption_otp_type(getDataManager().getDlightSuccessEncrptedOTPType());
                }
                forwardCommit.setAttempt_type("FWD");
                forwardCommit.setDrs_commit_date_time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                forwardCommit.setTrip_id(getDataManager().getTripId());
                forwardCommit.setFe_emp_code(getDataManager().getCode());
                if (Constants.uD_OTP_API_CHECK && ud_otp_commit_status_field.get().equalsIgnoreCase("VERIFIED")) {
                    forwardCommit.setUd_otp(ud_otp_commit_status_field.get());
                    forwardCommit.setRd_otp("NONE");
                } else if (Constants.rD_OTP_API_CHECK && ud_otp_commit_status_field.get().equalsIgnoreCase("VERIFIED")) {
                    forwardCommit.setRd_otp("VERIFIED");
                    forwardCommit.setUd_otp("NONE");

                } else {
                    forwardCommit.setUd_otp(ud_otp_commit_status_field.get());
                    forwardCommit.setRd_otp(rd_otp_commit_status_field.get());
                }
                //check first that this is a mps shipment or not if its mps shipment than
                //save all forward items
                parentAWBNo = forwardCommit.getAwb();
                isMPSShipment(forwardCommit, compositeKey);
            } catch (Exception e) {
                Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            }
        }));
        return mimageModels;
    }

    public void setOFDVerfied(boolean isVerfied) {
        if (isVerfied) {
            ud_otp_commit_status_field.set("VERIFIED");
            ud_otp_commit_status = "VERIFIED";
            ud_otp_verified_status.set(true);
        }
    }

    public void getReschuduleDates(String awbNo) {
        setIsLoading(true);
        final long timeStamp = Calendar.getInstance().getTimeInMillis();
        try {
            AmazonScheduleRequest amazonScheduleRequest = new AmazonScheduleRequest(awbNo);
            getCompositeDisposable().add(getDataManager().doScheduleDates(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), amazonScheduleRequest).doOnSuccess(amazon_reschedule_list -> {
                writeRestAPIResponse(timeStamp, amazon_reschedule_list);
                Log.d(ContentValues.TAG, amazon_reschedule_list.toString());


            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(amazon_reschedule_list -> {
                try {
                    Log.d(ContentValues.TAG, "login: " + amazon_reschedule_list.toString());
                    setIsLoading(false);
                    if (amazon_reschedule_list.getStatus().equalsIgnoreCase("true")) {
                        schedule_dates.clear();
                        schedule_dates.add("select");
                        try {
                            for (int i = 0; i < amazon_reschedule_list.getReschedule_info().getValidDays().length; i++) {
                                String date = convertDate(amazon_reschedule_list.getReschedule_info().getValidDays()[i].getStart());
                                schedule_dates.add(date);
                            }
                            getNavigator().setDateSpinner(schedule_dates);
                        } catch (Exception e) {
                            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
                            getNavigator().pickDateVisibility();
                            getNavigator().showError(amazon_reschedule_list.getDescription());
                        }
                    } else {
                        getNavigator().pickDateVisibility();
                        getNavigator().showError(amazon_reschedule_list.getDescription());
                    }
                } catch (Exception e) {
                    Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
                }
            }, throwable -> {
                setIsLoading(false);
                String error;
                try {
                    writeErrors(timeStamp, new Exception(throwable));
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                    getNavigator().showError(error);
                } catch (Exception e) {
                    Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
                }
            }));
        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            writeErrors(timeStamp, e);
            setIsLoading(false);
        }
    }

    public String convertDate(String valid_date) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        Date date = null;
        try {
            date = inputFormat.parse(valid_date);
        } catch (ParseException e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
        }
        String formattedDate = outputFormat.format(date);
        Log.e("valid date", formattedDate);
        return formattedDate;
    }

    public Date convertStringtoDate(String date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Date myDate = null;
        try {
            myDate = dateFormat.parse(date);
        } catch (ParseException e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
        }
        return myDate;
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
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
        }
        return false;
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
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
        }
        return 0.0;
    }

    public void showCallAPIDelayDialog(String nyka, boolean failFlag, String awb_number, String drs_id) {
        isCallRecursionDailogRunning = false;
        getNavigator().getActivityContext().runOnUiThread(() -> {
            AlertDialog.Builder callalertDialog = new AlertDialog.Builder(getNavigator().getActivityContext()).setMessage("Getting call status...").setPositiveButton("Wait", null);
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
                            isCallRecursionDailogRunning = false;
                            defaultButton.setText(String.format(Locale.getDefault(), "%s (%d)", negativeButtonText, TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1 //add one so it never displays zero
                            ));
                        }

                        @Override
                        public void onFinish() {
                            if (calldialog.isShowing()) {
                                calldialog.dismiss();
                            }
                            if (getDataManager().isCallStatusAPIRecursion()) {
                                getDataManager().setCallAPIRecursion(false);
                                getNavigator().undeliverShipment(failFlag, false);
                                isStopRecursion = true;
                            } else {
                                if (call_alert_number > 1) {
                                    getNavigator().undeliverShipment(failFlag, false);
                                } else {
                                    callApi(nyka,failFlag, forwardCommit.getAwb(), forwardCommit.getDrs_id());
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

    public void onVerifyClick() {
        getNavigator().onVerifyClick();
    }

    public void onResendClick() {
        getNavigator().onResendClick();
    }

    public void onGenerateOTPClick() {
        getNavigator().onGenerateOtpClick();
    }

    @SuppressLint("SetTextI18n")
    public void showCallAndSmsDialog(String awb, String drs_id, String consigneealternatemobile, String checkcall) {
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
        if (!consigneealternatemobile.equals("")) {
            btsmsalternate.setVisibility(View.VISIBLE);
            btsmsalternate.setText("SMS on Alternate No. " + consigneealternatemobile);
        }
        if (checkcall.equals("")) {
            call.setVisibility(View.GONE);
            txtcall.setVisibility(View.INVISIBLE);
        }
        btsmsalternate.setOnClickListener(v -> {
            dialog.dismiss();
            getNavigator().resendSms(true);
        });

        crssdialog.setOnClickListener(v -> dialog.dismiss());
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


    /**
     *
     * @param awb-- awb no
     * @param drs_id-- drs id
     * @param message_type-- this is value of UDOTP/RCHD OTP
     * @param shipment_type--this is shipement type
     */
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
                    Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
                }
            }, throwable -> {
                String error;
                try {
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                    getNavigator().showError(error);
                } catch (Exception e) {
                    Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
                    getNavigator().showError(e.getMessage());
                }
            }));
        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void onSkip(View view) {
        getNavigator().onSkipClick(view);
    }


    /**
     *
     * @param context-- activity context
     * @param awb-- awb no.
     * @param drsid-- drs id
     * @param alternateclick--true/false alternate click bydefault false
     * @param messagetype-- this is message of UDOTP or RCHD OTP
     * @param generateotp-- this is true/false value to generate otp or not
     * @param shipmentType-- this is shipment type
     */
    public void onGenerateOtpApiCall(Activity context, String awb, String drsid, Boolean alternateclick, String messagetype, Boolean generateotp, String shipmentType) {
        dialog = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
        dialog.setMessage("Sending OTP....");
        dialog.show();
        dialog.setIndeterminate(false);
        GenerateUDOtpRequest request = new GenerateUDOtpRequest(awb, messagetype, drsid, alternateclick, getDataManager().getCode(), generateotp, shipmentType);
        final long timeStamp = System.currentTimeMillis();
        writeRestAPIRequst(timeStamp, request);
        try {
            getCompositeDisposable().add(getDataManager().doGenerateUDOtpApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request).doOnSuccess(resendOtpResponse -> {
                writeRestAPIResponse(timeStamp, resendOtpResponse);
                Log.d(ContentValues.TAG, resendOtpResponse.toString());
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                if (dialog.isShowing()) dialog.dismiss();
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
                if (dialog.isShowing()) dialog.dismiss();
                String error;
                try {
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                    getNavigator().showError(error);
                } catch (Exception e) {
                    if (dialog.isShowing()) dialog.dismiss();
                    Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
                    getNavigator().showError(e.getMessage());
                }
            }));
        } catch (Exception e) {
            if (dialog.isShowing()) dialog.dismiss();
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    //ON RESEND BUTTON CLICK
    //making api call on resend button click
    public void onResendApiCall(Activity context, String awb, String drsid, Boolean alternateclick) {
        dialog = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
        dialog.setMessage("Sending OTP....");
        dialog.show();
        dialog.setIndeterminate(false);
        ResendOtpRequest request = new ResendOtpRequest(awb, "OTP", drsid, alternateclick);
        final long timeStamp = System.currentTimeMillis();
        writeRestAPIRequst(timeStamp, request);
        try {
            getCompositeDisposable().add(getDataManager().doResendOtpApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request).doOnSuccess(resendOtpResponse -> {
                writeRestAPIResponse(timeStamp, resendOtpResponse);
                Log.d(ContentValues.TAG, resendOtpResponse.toString());
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                if (dialog.isShowing()) dialog.dismiss();
                if (response.getStatus().equals("true")) {
                    counter_skip++;
                    String newMobileNo = response.getResponse().getMobile();

                } else {
                    if (response.getResponse().getDescription().matches("Max Attempt Reached")) {
                        counter_skip++;
                    }
                    getNavigator().showError(response.getResponse().getDescription());
                }
            }, throwable -> {
                if (dialog.isShowing()) dialog.dismiss();
                String error;
                try {
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();

                    getNavigator().showError(error);
                } catch (Exception e) {
                    if (dialog.isShowing()) dialog.dismiss();
                    Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
                    getNavigator().showError(e.getMessage());
                }
            }));
        } catch (Exception e) {
            if (dialog.isShowing()) dialog.dismiss();
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    //ON VERIFY BUTTON CLICK
    //making api call on verify button click
    public void onVerifyApiCall(Activity context, String awb, String otp, String drsid, String messagetype) {
        dialog = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
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
                    Constants.SAME_DAY_REASSIGN_VERIFIED = "VERIFIED";
                } else {
                    ud_otp_commit_status = "NONE";
                    ud_otp_commit_status_field.set("NONE");
                    ud_otp_verified_status.set(false);
                    Constants.SAME_DAY_REASSIGN_VERIFIED = "NONE";
                }
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                setIsLoading(false);
                if (dialog.isShowing()) dialog.dismiss();
                if (response.getStatus().equalsIgnoreCase("true")) {
                    ud_otp_commit_status = "VERIFIED";
                    ud_otp_commit_status_field.set("VERIFIED");
                    Constants.SAME_DAY_REASSIGN_VERIFIED = "VERIFIED";
                } else {
                    getNavigator().showError(response.getDescription());
                    ud_otp_commit_status = "NONE";
                    ud_otp_commit_status_field.set("NONE");
                    Constants.SAME_DAY_REASSIGN_VERIFIED = "NONE";
                }
            }, throwable -> {
                if (dialog.isShowing()) dialog.dismiss();
                String error;
                try {
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                    getNavigator().showError(error);
                } catch (Exception e) {
                    Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
                    getNavigator().showError(e.getMessage());
                }
            }));
        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            if (dialog.isShowing()) dialog.dismiss();
            getNavigator().showError(e.getMessage());
        }
    }

    public int getCounterDeliveryRange() {
        double currentLatitude = getDataManager().getCurrentLatitude();
        double currentLongitude = getDataManager().getCurrentLongitude();
        double DcLatitude = getDataManager().getDCLatitude();
        double DcLongitude = getDataManager().getDCLongitude();
        int meter = LocationHelper.getDistanceBetweenPoint(currentLatitude, currentLongitude, DcLatitude, DcLongitude);
        return meter;
    }

    public void getShipmentRescheduleDetails(String awbno) {
        setIsLoading(true);
        try {
            ResheduleDetailsRequest request = new ResheduleDetailsRequest();
            request.setAwb(awbno);
            final long timeStamp = System.currentTimeMillis();
            writeRestAPIRequst(timeStamp, request);
            getCompositeDisposable().add(getDataManager().doReshceduleDetails(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request).doOnSuccess(reshceduleDetailsResponse -> setIsLoading(false)).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(reshceduleDetailsResponse -> {
                setIsLoading(false);
                if (reshceduleDetailsResponse.getTotal_attempts() < getDataManager().getRescheduleMaxAttempts()) {
                    getNavigator().setRescheduleResponse(reshceduleDetailsResponse);
                } else {
                    getNavigator().showError("You have reached your max attempt of reschedule.");
                }
            }, throwable -> {
                setIsLoading(false);
                String error;
                try {
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();

                } catch (NullPointerException e) {
                    Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
                }
            }));
        } catch (Exception e) {
            Log.e(ContentValues.TAG, e.getMessage());
            setIsLoading(false);
            if (e instanceof Throwable) {
                getNavigator().showError(new RestApiErrorHandler(e.fillInStackTrace()).getErrorDetails().getEResponse().getDescription());
            }
        }
    }

    public boolean getIsAwbScan() {
        boolean scanAwb = true;
        if (scanAwb) isScanAwb.set("M");
        else isScanAwb.set("O");
        return scanAwb;
    }

    public void doScanAgainAlert(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
        builder.setMessage("Please scan valid bar code of this manifest.");
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            dialogInterface.cancel();
            getNavigator().showScanAlert();
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    public void showErrorDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context,android.R.style.Theme_Material_Light_Dialog);
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        Dialog dialog = builder.create();
        dialog.show();
    }

    /**
     *
     * @param awb---this is awb no.
     * @param drs--- this is drs id
     */
    public void getCallStatus(long awb, int drs) {
        try {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(getDataManager().getCallStatus(awb, drs).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> getDataManager().setIsCallAlreadyDone(aBoolean), throwable -> {
            }));
        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
        }
    }

    /**
     *
     * @param awb---this is awb no.
     * @param drs--- this is drs id
     */
    public void saveCallStatus(long awb, int drs) {
        try {
            Call call = new Call();
            call.setDrs(drs);
            call.setAwb(awb);
            call.setStatus(true);
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(getDataManager().saveCallStatus(call).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> Log.e("isDataSaved?", "saved"), throwable -> {
            }));
        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
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
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                getNavigator().clearStack();
            }));
        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void getRemarkCount(long awb) {
        try {
            getCompositeDisposable().add(getDataManager().getRemarks(awb).observeOn(getSchedulerProvider().io()).subscribeOn(getSchedulerProvider().io()).subscribe(remark -> {
                forwardCommit.setTrying_reach(String.valueOf(getDataManager().getTryReachingCount(awb + Constants.TRY_RECHING_COUNT)));
                forwardCommit.setTechpark(String.valueOf(getDataManager().getSendSmsCount(awb + Constants.TECH_PARK_COUNT)));
            }, Throwable::printStackTrace));
        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void addRemarks(Remark remark) {
        try {
            getCompositeDisposable().add(getDataManager().insertRemark(remark).observeOn(getSchedulerProvider().io()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {

            }, Throwable::printStackTrace));
        } catch (Exception e) {
            Logger.e(UndeliveredViewModel.class.getName(),e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public String getEmployeeCode() {
        return getDataManager().getCode();
    }

    public static class MyPhoneStateListener extends PhoneStateListener {
        public void onCallStateChanged(int state, String phoneNumber) {
            // zero state is CALL_STATE_IDLE
            if (state == 0) {
            }
        }
    }
}
