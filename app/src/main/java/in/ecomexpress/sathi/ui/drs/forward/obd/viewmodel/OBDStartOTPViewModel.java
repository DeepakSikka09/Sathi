package in.ecomexpress.sathi.ui.drs.forward.obd.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.util.Log;
import androidx.databinding.ObservableBoolean;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.SecureDelivery;
import in.ecomexpress.sathi.repo.remote.model.otp.resendotp.GenerateUDOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.otp.verifyotp.VerifyUDOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.voice_otp.VoiceOTP;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.ui.drs.forward.obd.navigator.IObdOTPNavigator;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.functions.Consumer;

@HiltViewModel
public class OBDStartOTPViewModel extends BaseViewModel<IObdOTPNavigator> {

    private ProgressDialog dialog;
    SecureDelivery isSecureDelivery;
    public ObservableBoolean isSecureOtp = new ObservableBoolean(false);

    private ForwardCommit forwardCommit;
    public DRSForwardTypeResponse mDrsForwardTypeResponse;

    @Inject
    public OBDStartOTPViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application application) {
        super(dataManager, schedulerProvider, application);
    }

    public void getClick() {
        getNavigator().onGenerateOtpClick();
    }

    public void getResendOtpClick() {
        getNavigator().onResendClick();
    }

    public void getOtpOnAlternate(boolean isAlternate) {
        getNavigator().resendSms(isAlternate);
    }

    public void getVoiceCallClick() {
        getNavigator().VoiceCallOtp("");
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
                if (dialog.isShowing())
                    dialog.dismiss();
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
                    } else {
                        if (response.getResponse() != null) {
                            if (response.getResponse().getStatusCode().equalsIgnoreCase("107")) {
                                getNavigator().doLogout(response.getResponse().getDescription());
                            }
                        } else if (response.getDescription().matches("Max Attempt Reached")) {
                            getNavigator().onOtpResendSuccess(response.getDescription());
                        }
                        getNavigator().showError(response.getDescription());
                    }

                }
            }, throwable -> {
                if (dialog.isShowing())
                    dialog.dismiss();
                String error;
                try {
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                    getNavigator().showError(error);
                } catch (Exception e) {
                    if (dialog.isShowing())
                        dialog.dismiss();
                    getNavigator().showError(e.getMessage());
                }
            }));
        } catch (Exception e) {
            if (dialog.isShowing())
                dialog.dismiss();
            getNavigator().showError(e.getMessage());
        }
    }

    public void onVerifyApiCall(Activity context, String awb, String otp, String drsId, String messageType) {
        dialog = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
        dialog.setMessage("Verifying....");
        dialog.show();
        dialog.setIndeterminate(false);
        try {
            VerifyUDOtpRequest request = new VerifyUDOtpRequest(awb, drsId, messageType, otp);
            getCompositeDisposable().add(getDataManager().doVerifyUDOtpDRSApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request).doOnSuccess(verifyOtpResponse -> {
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                setIsLoading(false);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (response.getStatus().equalsIgnoreCase("true")) {
                    getNavigator().onOtpVerifySuccess(response.getDescription(),"");
                } else {
                    if (response.getStatus().equalsIgnoreCase("false")){
                        getNavigator().showError("Invalid Otp, Try Again");

                    } else{
                        if (response.getResponse().getStatusCode().equalsIgnoreCase("107")){
                            getNavigator().doLogout(response.getResponse().getDescription());
                        } else {
                            getNavigator().showError(response.getDescription());
                        }
                    }
                }
            }, throwable -> {
                if (dialog.isShowing())
                    dialog.dismiss();
                String error;
                try {
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                    getNavigator().showError(error);
                } catch (Exception e) {
                    getNavigator().showError(e.getMessage());
                }
            }));
        } catch (Exception e) {
            if (dialog.isShowing())
                dialog.dismiss();
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
                    Logger.e(OBDStartOTPViewModel.class.getName(), e.getMessage());
                    getNavigator().showError(e.getMessage());
                }
            }));
        } catch (Exception e) {
            Logger.e(OBDStartOTPViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void getFWDShipmentData(ForwardCommit nForwardCommit, String composite_key, String consignee_mobile) {
        this.forwardCommit = nForwardCommit;
        try {
            getCompositeDisposable().add(getDataManager().getForwardDRS(composite_key).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<DRSForwardTypeResponse>() {
                @Override
                public void accept(DRSForwardTypeResponse drsForwardTypeResponse) throws Exception {
                    try {
                        if (drsForwardTypeResponse != null) {
                            mDrsForwardTypeResponse = drsForwardTypeResponse;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        getNavigator().onHandleError(e.getMessage());
                    }
                }
            }, throwable -> getNavigator().onHandleError(throwable.getMessage())));

        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onHandleError(e.getMessage());
        }
    }
    public void getSecureDelivery(SecureDelivery secureDelivery) {
        try {
            if (secureDelivery != null) {
                isSecureDelivery = secureDelivery;
                isSecureOtp.set(isSecureDelivery.getOTP());
            }
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onHandleError(e.getMessage());
        }
    }
    public String getCollectableValue() {
        return mDrsForwardTypeResponse.getShipmentDetails().getCollectableValue().toString();
    }

    public void onUndeliveredClick() {
        try {
            forwardCommit.setAttempt_reason_code(Constants.FORWARD_COMMIT_REASON_CODE);
            forwardCommit.setDeclared_value(mDrsForwardTypeResponse.getShipmentDetails().getDeclaredValue().toString());
            forwardCommit.setDrs_date(String.valueOf(mDrsForwardTypeResponse.getAssignedDate()));
            forwardCommit.setAwb(mDrsForwardTypeResponse.getAwbNo().toString());
            forwardCommit.setDeclared_value(String.valueOf(mDrsForwardTypeResponse.getShipmentDetails().getDeclaredValue()));
            forwardCommit.setDrs_id(mDrsForwardTypeResponse.getDrsId() + "");
            forwardCommit.setLocation_lat(String.valueOf(getDataManager().getCurrentLatitude()));
            forwardCommit.setLocation_long(String.valueOf(getDataManager().getCurrentLongitude()));
            forwardCommit.setConsignee_name(mDrsForwardTypeResponse.getConsigneeDetails().getName());
            forwardCommit.setShipment_type(Constants.SHIPMENT_TYPE_FORWARD);
            forwardCommit.setAttempt_reason_code(Constants.FORWARD_COMMIT_REASON_CODE);
            forwardCommit.setDeclared_value(mDrsForwardTypeResponse.getShipmentDetails().getDeclaredValue().toString());
            getNavigator().onUndelivered(forwardCommit);
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onHandleError(e.getMessage());
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
                    Logger.e(OBDStartOTPViewModel.class.getName(), e.getMessage());
                }
                getNavigator().clearStack();
            }));
        } catch (Exception e) {
            Logger.e(OBDStartOTPViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }
}