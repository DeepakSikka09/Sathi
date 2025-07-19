package in.ecomexpress.sathi.ui.drs.forward.obd.viewmodel;

import static in.ecomexpress.sathi.utils.Constants.DISTANCE_API_KEY;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.repo.local.data.commit.PushApi;
import in.ecomexpress.sathi.repo.local.db.model.ImageModel;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.ui.drs.forward.obd.navigator.IFwdOBDCompleteNavigator;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.LocationHelper;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

@HiltViewModel
public class FwdOBDCompleteViewModel extends BaseViewModel<IFwdOBDCompleteNavigator> {

    static IDataManager iDataManager;
    static ISchedulerProvider iSchedulerProvider;
    public final ObservableField<String> consigneeContactNumber = new ObservableField<>("");
    public final ObservableField<String> awbNo = new ObservableField<>();
    public int call_alert_number = 0;
    public boolean isCallRecursionDialogRunning = true;
    public ObservableField<Boolean> ud_otp_verified_status = new ObservableField<>(false);
    public ObservableField<String> ud_otp_commit_status_field = new ObservableField<>("NONE");
    public ObservableField<String> rd_otp_commit_status_field = new ObservableField<>("NONE");
    public boolean isStopRecursion = false;
    public String ud_otp_commit_status = "NONE";
    int isCall = 0;
    String parentAWBNo;
    List<ImageModel> mImageModels = new ArrayList<>();
    Dialog calldialog = null;
    long request_time = 0L;
    long response_time = 0L;
    private ForwardCommit forwardCommit;
    int meter=0;

    @Inject
    public FwdOBDCompleteViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);
        iDataManager = dataManager;
        iSchedulerProvider = schedulerProvider;
    }


    public void callApi(boolean failFlag, String awb_number, String drs_id) {
        if (getDataManager().getENABLEDIRECTDIAL().equalsIgnoreCase("true") || Constants.Wrong_Mobile_no) {
            getNavigator().undeliveredShipment(failFlag, true);
        } else {
            try {
                request_time = System.currentTimeMillis();
                FwdOBDCompleteViewModel.this.setIsLoading(true);
                getCompositeDisposable().add(getDataManager().doForwardCallStatusApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), call_alert_number, getDataManager().getEmp_code(), awb_number, drs_id, getDataManager().getShipperId()).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(forwardCallResponse -> {
                    FwdOBDCompleteViewModel.this.setIsLoading(false);

                    if (forwardCallResponse.getStatus().equalsIgnoreCase("true")) {
                        getNavigator().showError(forwardCallResponse.getResponse());
                        if (calldialog != null) {
                            calldialog.dismiss();
                        }
                        getNavigator().undeliveredShipment(failFlag, true);
                    } else {
                        response_time = System.currentTimeMillis();
                        if (forwardCallResponse.isCall_again_required()) {
                            getNavigator().undeliveredShipment(failFlag, false);
                        } else {

                            if (getDataManager().isCallStatusAPIRecursion()) {
                                call_alert_number = call_alert_number + 1;
                                long time_difference = response_time - request_time;
                                if (time_difference < getDataManager().getRequestRsponseTime()) {
                                    long diffrence = getDataManager().getRequestRsponseTime() - time_difference;
                                    final Handler handler = new Handler();
                                    handler.postDelayed(() -> {
                                        callApi(failFlag, forwardCommit.getAwb(), forwardCommit.getDrs_id());
                                        if (isCallRecursionDialogRunning) {
                                            getNavigator().getActivityContext().runOnUiThread(() -> showCallAPIDelayDialog(failFlag));
                                        }
                                    }, diffrence);
                                } else {
                                    callApi(failFlag, forwardCommit.getAwb(), forwardCommit.getDrs_id());
                                    if (isCallRecursionDialogRunning) {
                                        showCallAPIDelayDialog(failFlag);
                                    }
                                }
                            } else {
                                if (isStopRecursion) {
                                    return;
                                }
                                call_alert_number = call_alert_number + 1;
                                showCallAPIDelayDialog(failFlag);
                            }
                        }
                    }
                }, throwable -> FwdOBDCompleteViewModel.this.setIsLoading(false)));
            } catch (Exception e) {
                getNavigator().showError(e.getMessage());
                FwdOBDCompleteViewModel.this.setIsLoading(false);
                Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
            }
        }
    }

    public String loginDate() {
        return getDataManager().getLoginDate();
    }

    public void onUndeliveredApiCall(Context context, String reschedule, String compositeKey) {
        try {
            forwardCommit.setReceived_by_name("");
            forwardCommit.setStatus("2");
            forwardCommit.setReceived_by_relation("");
            forwardCommit.setReschedule_date(reschedule);
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
                Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
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

            parentAWBNo = forwardCommit.getAwb();
            isMPSShipment(forwardCommit, compositeKey);

        } catch (Exception e) {
            Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void createCommitPacketCashCollection(ForwardCommit forwardCommits, String reschedule, String compositeKey) {
        try {
            forwardCommit = forwardCommits;
            mImageModels = getImagesList(forwardCommit.getAwb(), reschedule, compositeKey);

        } catch (Exception e) {
            Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
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
                    Logger.e("forward.undelivered_fwd", "MPS shipment awbNumbers: " + awbsNos);
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
                            Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
                        }

                    }
                    uploadForwardShipment(fwd, drsForwardTypeResponse.getAwbNo(), compositeKey);
                    updateMPSShipmentStatus(awbArr);
                } else {
                    Logger.e("forward.undelivered_fwd", "Not a mps shipment awbNo: " + compositeKey);
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
            }, throwable -> {
            }));
        } catch (Exception e) {
            Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
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
                    int shipement_status;
                    String compositeKey = "";
                    if (forwardCommitResponse.getResponse().getShipment_status().equalsIgnoreCase(Constants.UNDELIVERED)) {
                        shipement_status = Constants.SHIPMENT_UNDELIVERED_STATUS;
                    } else {
                        shipement_status = Constants.SHIPMENT_DELIVERED_STATUS;
                    }
                    try {
                        compositeKey = forwardCommitResponse.getResponse().getDrs_no() + forwardCommitResponse.getResponse().getAwb_no();
                    } catch (Exception e) {
                        Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
                    }
                    getDataManager().updateForwardStatus(compositeKey, shipement_status).subscribe(aBoolean -> {
                        updateSyncStatusInDRSFWDTable(forwardCommitResponse.getResponse().getDrs_no() + forwardCommitResponse.getResponse().getAwb_no());
                        // Setting call preference after sync:-
                        getDataManager().setCallClicked(forwardCommitResponse.getResponse().getAwb_no() + "ForwardCall", true);
                        compositeDisposable.add(getDataManager().deleteSyncedImage(forwardCommitResponse.getResponse().getAwb_no()).subscribe(aBoolean1 -> {
                            //getNavigator().navigateToNextActivity();
                        }));

                        saveCommitUpload(forwardCommits, parentAWB, composite_key);
                    }, throwable -> {
                        /*getNavigator().commitShipmentAgain();
                        getNavigator().showError("Shipment Not Delivered Due To Server Issue, Try Again");
                       */ saveCommit(forwardCommits, parentAWB, composite_key);
                    });
                }/* else{
                    getNavigator().commitShipmentAgain();
                    getNavigator().showError("Shipment Not Delivered Due To Server Issue, Try Again");
                }*/
            }, throwable -> {
               /* getNavigator().commitShipmentAgain();
                getNavigator().showError("Shipment Not Delivered Due To Server Issue, Try Again");
               */ saveCommit(forwardCommits, parentAWB, composite_key);
                throwable.printStackTrace();
            }));
        } catch (Exception e) {
           /* getNavigator().commitShipmentAgain();
            getNavigator().showError("Shipment Not Delivered Due To Server Issue, Try Again");
           */ saveCommit(forwardCommits, parentAWB, composite_key);
           e.printStackTrace();

        }
    }

    private void updateSyncStatusInDRSFWDTable(String composite_key) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager().updateSyncStatusFWD(composite_key, GlobalConstant.CommitStatus.CommitSynced).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {
        }, throwable -> {
        }));
    }

    private void updateMPSShipmentStatus(String[] awbArr) {
        try {
            getCompositeDisposable().add(getDataManager().updateForwardMPSShipmentStatus(awbArr, Constants.SHIPMENT_UNDELIVERED_STATUS).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> onSubmitSuccess(forwardCommit), throwable -> throwable.printStackTrace()));
        } catch (Exception e) {
            Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
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
                            Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
                        }
                        if (consigneeLatitude == 0.0 || consigneeLongitude == 0.0) {
                            getNavigator().setConsigneeDistance(0);
                            return;
                        }
                         getDistanceBetweenLocations();

                    }
                } catch (Exception e) {
                    Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
                }
            }, throwable -> Log.e("error", throwable.getMessage())));
        } catch (Exception e) {
            Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
        }
    }

    private void updateShipmentStatus(String composite_key) {
        try {
            getCompositeDisposable().add(getDataManager().updateForwardStatus(composite_key, Constants.SHIPMENT_UNDELIVERED_STATUS).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> onSubmitSuccess(forwardCommit)));
        } catch (Exception e) {
            Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
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

    /**
     * @param imageUri---    imageuri
     * @param imageCode--    image code
     * @param name--         name of image
     * @param imageId--      image id
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
            getCompositeDisposable().add(getDataManager().saveImage(imageModel).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
            }));
        } catch (Exception e) {
            Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
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
            Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
        try {
            getCompositeDisposable().add(getDataManager().saveCommitPacket(pushApi).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> updateForwardCallAttemptedWithZero(String.valueOf(pushApi.getAwbNo()))));
        } catch (Exception e) {
            Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    private void saveCommitUpload(List<ForwardCommit> forwardCommit, long parentAWB, String compositeKey) {
        Log.d("forward.undelivered_fwd", "saveCommit: forward" + forwardCommit.toString());
        PushApi pushApi = new PushApi();
        pushApi.setAwbNo(parentAWB);
        pushApi.setCompositeKey(compositeKey);
        pushApi.setAuthtoken(getDataManager().getAuthToken());
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<ForwardCommit> forwardcommitList = forwardCommit;
            pushApi.setRequestData(mapper.writeValueAsString(forwardcommitList));
            pushApi.setShipmentStatus(2);
            pushApi.setShipmentDeliveryStatus("3");
            pushApi.setShipmentCaterogy(GlobalConstant.ShipmentTypeConstants.FWD);
        } catch (Exception e) {
            Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
        try {
            getCompositeDisposable().add(getDataManager().saveCommitPacket(pushApi).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
                int isCallAttempted = getIsCallAttemptedUndeliverCount(String.valueOf(parentAWB));
                if (isCallAttempted == 0) {
                    Constants.shipment_undelivered_count++;
                }
                updateForwardCallAttemptedWithZero(String.valueOf(pushApi.getAwbNo()));
                getUDORRCHEDShipmentStatus();
            }));
        } catch (Exception e) {
            Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    private void getUDORRCHEDShipmentStatus() {
        if (forwardCommit.getUd_otp().equalsIgnoreCase("VERIFIED") || forwardCommit.getRd_otp().equalsIgnoreCase("VERIFIED")) {
            getDataManager().setFWD_UD_RD_OTPVerfied(forwardCommit.getAwb() + "Forward", true);
        }
    }

    public int getIsCallAttemptedUndeliverCount(String awb) {
        try {
            getCompositeDisposable().add(getDataManager().getisForwardCallattempted(Long.parseLong(awb)).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(isCallattempted -> {
                try {
                    isCall = Integer.parseInt(String.valueOf(isCallattempted));
                    getNavigator().isCall(isCall);
                } catch (Exception e) {
                    RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                    restApiErrorHandler.writeErrorLogs(0, e.getMessage());
                    Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
                }
            }, throwable -> {
                RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(throwable.getCause());
                restApiErrorHandler.writeErrorLogs(0, throwable.getMessage());
            }));
        } catch (Exception e) {
            Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
            restApiErrorHandler.writeErrorLogs(0, e.getMessage());
        }
        return isCall;
    }


    public void updateForwardCallAttemptedWithZero(String awb) {
        try {
            getCompositeDisposable().add(getDataManager().updateForwardCallAttempted(Long.valueOf(awb), 0).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {

            }));
        } catch (Exception e) {
            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
            restApiErrorHandler.writeErrorLogs(0, e.getMessage());
            Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void checkMeterRange(DRSForwardTypeResponse drsForwardTypeResponse) {
        try {
            getProfileLatLng(drsForwardTypeResponse);
        } catch (Exception e) {
            Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void getConsigneeProfiling() {
        boolean enable = getDataManager().getConsigneeProfiling();
        getNavigator().setConsigneeProfiling(enable);
    }

    private List<ImageModel> getImagesList(String awbNo, String reschedule, String compositeKey) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager().getImages(awbNo).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(imageModels -> {
            try {
                mImageModels = imageModels;
                List<ForwardCommit.Image_response> list_image_responses = new ArrayList<>();
                if (!mImageModels.isEmpty()) {
                    for (int i = 0; i < mImageModels.size(); i++) {
                        ForwardCommit.Image_response image_response = new ForwardCommit.Image_response();
                        image_response.setImage_id(String.valueOf(mImageModels.get(i).getImageId()));
                        image_response.setImage_key(String.valueOf(mImageModels.get(i).getImageName()));
                        list_image_responses.add(image_response);
                    }
                    forwardCommit.setImage_response(list_image_responses);
                } else {
                    forwardCommit.setImage_response(list_image_responses);
                }
                Gson gson = new Gson();
                Type type = new TypeToken<List<ForwardCommit.Amz_Scan>>() {
                }.getType();
                ArrayList<ForwardCommit.Amz_Scan> amz_scans = gson.fromJson(getDataManager().getAmazonList(), type);
                forwardCommit.setAmz_scan(amz_scans);
                forwardCommit.setReceived_by_name("");
                forwardCommit.setStatus(Constants.UNDELIVERED);
                forwardCommit.setReceived_by_relation("");
                forwardCommit.setShipment_id(getDataManager().getShipperId());
                forwardCommit.setReschedule_date(reschedule);
                //forwardCommit.setAttempt_reason_code(String.valueOf(code));
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
                    Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
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
                parentAWBNo = forwardCommit.getAwb();
                isMPSShipment(forwardCommit, compositeKey);
            } catch (Exception e) {
                Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
            }
        }));
        return mImageModels;
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
            Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
        }
        return false;
    }


public  void getDistanceBetweenLocations() {
    if (getDataManager().getDistanceAPIEnabled()) {
        meter = getCounterDeliveryRange();
        getNavigator().setConsigneeDistance(meter);
    } else {
        final long timeStamp = System.currentTimeMillis();
        try {
            final StringBuilder[] builder = {new StringBuilder()};
            builder[0].append(getDataManager().getCurrentLongitude());
            builder[0].append(",");
            builder[0].append(getDataManager().getCurrentLatitude());
            builder[0].append(";");
            builder[0].append(getDataManager().getDCLongitude());
            builder[0].append(",");
            builder[0].append(getDataManager().getDCLatitude());
            getCompositeDisposable().add(getDataManager().distanceCalculationApis(builder[0].toString(), "distance").doOnSuccess(response -> writeRestAPIResponse(timeStamp, response)
            ).subscribeOn(getSchedulerProvider().ui()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {

                meter = (int) Math.round(response.getDistances().get(0).get(1));
                getNavigator().setConsigneeDistance(meter);
            }, throwable -> {
            }));
        } catch (Exception e) {
            Logger.e("FwdObdCompleteViewModel", String.valueOf(e));
        }
    }
}
    public void showCallAPIDelayDialog(boolean failFlag) {
        isCallRecursionDialogRunning = false;
        getNavigator().getActivityContext().runOnUiThread(() -> {
            AlertDialog.Builder callAlertDialog = new AlertDialog.Builder(getNavigator().getActivityContext()).setMessage("Getting call status...").setPositiveButton("Wait", null);
            calldialog = callAlertDialog.create();
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
                            isCallRecursionDialogRunning = false;
                            defaultButton.setText(String.format(Locale.getDefault(), "%s (%d)", negativeButtonText, TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1
                            ));
                        }

                        @Override
                        public void onFinish() {
                            if (calldialog.isShowing()) {
                                calldialog.dismiss();
                            }
                            if (getDataManager().isCallStatusAPIRecursion()) {
                                getDataManager().setCallAPIRecursion(false);
                                getNavigator().undeliveredShipment(failFlag, false);
                                isStopRecursion = true;
                            } else {
                                if (call_alert_number > 1) {
                                    getNavigator().undeliveredShipment(failFlag, false);
                                } else {
                                    callApi(failFlag, forwardCommit.getAwb(), forwardCommit.getDrs_id());
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

    public int getCounterDeliveryRange() {
        double currentLatitude = getDataManager().getCurrentLatitude();
        double currentLongitude = getDataManager().getCurrentLongitude();
        double DcLatitude = getDataManager().getDCLatitude();
        double DcLongitude = getDataManager().getDCLongitude();
        return LocationHelper.getDistanceBetweenPoint(currentLatitude, currentLongitude, DcLatitude, DcLongitude);
    }


    public void getCallStatus(long awb, int drs) {
        try {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(getDataManager().getCallStatus(awb, drs).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui())
                    .subscribe(aBoolean -> getDataManager().setIsCallAlreadyDone(aBoolean), throwable -> {
            }));
        } catch (Exception e) {
            Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
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
            Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
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
            Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }
}