package in.ecomexpress.sathi.ui.drs.rvp.rvp_secure_activity;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.ObservableField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.db.model.RvpWithQC;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.masterdata.SampleQuestion;
import in.ecomexpress.sathi.repo.remote.model.otp.resendotp.GenerateUDOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.otp.verifyotp.VerifyUDOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.voice_otp.VoiceOTP;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class RVPSecureDeliveryViewModel extends BaseViewModel<IRVPSecureDeliveryNavigator> {
    private ProgressDialog dialog;
    public ObservableField<String> itemAddress = new ObservableField<>("");
    public ObservableField<String> itemName = new ObservableField<>("");
    public ObservableField<String> itemAwb = new ObservableField<>("");
    public ObservableField<String> feCurentLat = new ObservableField<>("");
    public ObservableField<String> feCurentLng = new ObservableField<>("");
    public ObservableField<String> ofd_otp_verify_status = new ObservableField<>("NONE");
    public ObservableField<String> ofd_otp_verified = new ObservableField<>("null");
    public ObservableField<RvpWithQC> rvpWithQCObservableField = new ObservableField<>();
    public ObservableField<ArrayList<SampleQuestion>> sampleQuestionList = new ObservableField<>();

    @Inject
    public RVPSecureDeliveryViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application application) {
        super(dataManager, schedulerProvider, application);
    }

    public void onGenerateOTPClick() {
        getNavigator().onGenerateOtpClick();
    }

    public void onGenerateOtpApiCall(Activity context, String awb, String drsId, Boolean alternateClick, String messageType, Boolean generateOtp) {
        dialog = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
        dialog.setMessage("Sending OTP....");
        dialog.show();
        dialog.setIndeterminate(false);
        GenerateUDOtpRequest request = new GenerateUDOtpRequest(awb, messageType, drsId, alternateClick, getDataManager().getCode(), generateOtp, "RQC");
        final long timeStamp = System.currentTimeMillis();
        writeRestAPIRequst(timeStamp, request);
        try {
            getCompositeDisposable().add(getDataManager().doGenerateUDOtpApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request).doOnSuccess(resendOtpResponse -> {
                writeRestAPIResponse(timeStamp, resendOtpResponse);
                Log.d(ContentValues.TAG, resendOtpResponse.toString());
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                if (dialog.isShowing())
                    dialog.dismiss();
                if (response.getStatus().equals("true")) {
                    getNavigator().onOtpResendSuccess(response.getDescription());
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
                    Logger.e(RVPSecureDeliveryViewModel.class.getName(), e.getMessage());
                    getNavigator().showError(e.getMessage());
                }
            }));
        } catch (Exception e) {
            if (dialog.isShowing())
                dialog.dismiss();
            Logger.e(RVPSecureDeliveryViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void onResendClick() {
        getNavigator().onResendClick();
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
                    Logger.e(RVPSecureDeliveryViewModel.class.getName(), e.getMessage());
                }
                getNavigator().clearStack();
            }));
        } catch (Exception e) {
            Logger.e(RVPSecureDeliveryViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void showCallAndSmsDialog(String consigneeAlternateMobile, String checkCall) {
        Dialog dialog = new Dialog(getNavigator().getActivityContext(), R.style.RoundedCornersDialog);
        dialog.setContentView(R.layout.dialog_sms_call);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Button sms = dialog.findViewById(R.id.bt_sms);
        Button call = dialog.findViewById(R.id.bt_call);
        ImageView crossDialog = dialog.findViewById(R.id.crssdialog);
        TextView textCall = dialog.findViewById(R.id.txtcall);
        Button btSmsAlternate = dialog.findViewById(R.id.bt_sms_alternate);
        if (!consigneeAlternateMobile.isEmpty()) {
            btSmsAlternate.setVisibility(View.VISIBLE);
            btSmsAlternate.setText(String.format("SMS on Alternate No. %s", consigneeAlternateMobile));
        }
        if (checkCall.isEmpty()) {
            call.setVisibility(View.GONE);
            textCall.setVisibility(View.INVISIBLE);
        }
        btSmsAlternate.setOnClickListener(v -> {
            dialog.dismiss();
            getNavigator().resendSMS(true);
        });
        crossDialog.setOnClickListener(v -> dialog.dismiss());
        sms.setOnClickListener(v -> {
            dialog.dismiss();
            getNavigator().resendSMS(false);
        });
        call.setOnClickListener(v -> {
            dialog.dismiss();
            getNavigator().VoiceCallOtp();
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public void doVoiceOTPApi(String awb, String drs_id, String message_type) {
        try {
            VoiceOTP voiceOTP = new VoiceOTP();
            voiceOTP.awb = awb;
            voiceOTP.drs_id = drs_id;
            voiceOTP.product_type = "RQC";
            voiceOTP.employee_code = getDataManager().getEmp_code();
            voiceOTP.message_type = message_type;
            getCompositeDisposable().add(getDataManager().doVoiceOtpApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), voiceOTP).doOnSuccess(verifyOtpResponse -> Log.d(ContentValues.TAG, verifyOtpResponse.toString())).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                setIsLoading(false);
                try {
                    if (response.code == 0) {
                        getNavigator().showError(response.description);
                        getNavigator().voiceTimer();
                    } else if (response.code == 1) {
                        getNavigator().showError(response.description);
                    }
                } catch (Exception e) {
                    Logger.e(RVPSecureDeliveryViewModel.class.getName(), e.getMessage());
                }
            }, throwable -> {
                String error;
                try {
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                    getNavigator().showError(error);
                } catch (Exception e) {
                    Logger.e(RVPSecureDeliveryViewModel.class.getName(), e.getMessage());
                    getNavigator().showError(e.getMessage());
                }
            }));
        } catch (Exception e) {
            Logger.e(RVPSecureDeliveryViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public void onVerifyClick() {
        getNavigator().onVerifyClick();
    }

    public void setOFDVerified(boolean isVerified) {
        if (isVerified) {
            ofd_otp_verify_status.set("VERIFIED");
            ofd_otp_verified.set("true");
        }
    }

    public void onVerifyApiCall(Activity context, String awb, String otp, String drsId, String messageType) {
        dialog = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
        dialog.setMessage("Verifying....");
        dialog.show();
        dialog.setIndeterminate(false);
        try {
            VerifyUDOtpRequest request = new VerifyUDOtpRequest(awb, drsId, messageType, otp);
            final long timeStamp = System.currentTimeMillis();
            writeRestAPIRequst(timeStamp, request);
            getCompositeDisposable().add(getDataManager().doVerifyUDOtpDRSApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request).doOnSuccess(verifyOtpResponse -> {
                if (verifyOtpResponse.getDescription().equalsIgnoreCase("Verified")) {
                    ofd_otp_verify_status.set("VERIFIED");
                    ofd_otp_verified.set("true");
                } else {
                    ofd_otp_verify_status.set("NONE");
                    ofd_otp_verified.set("false");
                }
                Constants.OFD_OTP_VERIFIED = true;
                getDataManager().setOFDOTPVerifiedStatus(ofd_otp_verify_status.get());
                getDataManager().setRVPSecureOTPVerified(ofd_otp_verified.get());
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                setIsLoading(false);
                if (dialog.isShowing())
                    dialog.dismiss();
                if (response.getStatus().equalsIgnoreCase("true")) {
                    ofd_otp_verify_status.set("VERIFIED");
                    ofd_otp_verified.set("true");
                    getNavigator().setGreenTick();

                } else {
                    getNavigator().showError(response.getDescription());
                    ofd_otp_verify_status.set("NONE");
                    ofd_otp_verified.set("false");
                }
            }, throwable -> {
                if (dialog.isShowing())
                    dialog.dismiss();
                String error;
                try {
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                    getNavigator().showError(error);
                } catch (Exception e) {
                    Logger.e(RVPSecureDeliveryViewModel.class.getName(), e.getMessage());
                    getNavigator().showError(e.getMessage());
                }
            }));
        } catch (Exception e) {
            Logger.e(RVPSecureDeliveryViewModel.class.getName(), e.getMessage());
            if (dialog.isShowing())
                dialog.dismiss();
            getNavigator().showError(e.getMessage());
        }
    }

    public void onSkip(View view) {
        getNavigator().onSkipClick(view);
    }

    public void getRVPShipmentData(String composite_key) {
        try {
            getCompositeDisposable().add(getDataManager().getRVPDRS(composite_key).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(drsReverseQCTypeResponse -> {
                try {
                    itemAddress.set(drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getLine1() + "\n" + drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getLine2() + "\n" + drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getLine3() + "\n" + drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getCity() + "," + drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getState() + "," + drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getPincode());
                    itemName.set(drsReverseQCTypeResponse.getConsigneeDetails().getName());
                    itemAwb.set(drsReverseQCTypeResponse.getAwbNo().toString());
                    feCurentLat.set("Current Latitude :- " + getDataManager().getStartQCLat());
                    feCurentLng.set("Current Longitude :- " + getDataManager().getStartQCLng());
                } catch (Exception e) {
                    Logger.e(RVPSecureDeliveryViewModel.class.getName(), e.getMessage());
                    getNavigator().onHandleError(e.getMessage());
                }
            }, throwable -> getNavigator().onHandleError(throwable.getMessage())));
        } catch (Exception e) {
            Logger.e(RVPSecureDeliveryViewModel.class.getName(), e.getMessage());
            getNavigator().onHandleError(e.getMessage());
        }
    }

    public void onBackClick() {
        getNavigator().onBack();
    }

    public void onSubmitClick() {
        if (Objects.requireNonNull(ofd_otp_verify_status.get()).equalsIgnoreCase("VERIFIED") || Objects.requireNonNull(ofd_otp_verify_status.get()).equalsIgnoreCase("SKIPPED")) {
            Constants.OFD_OTP_VERIFIED = Objects.requireNonNull(ofd_otp_verify_status.get()).equalsIgnoreCase("VERIFIED");
            getDataManager().setOFDOTPVerifiedStatus(ofd_otp_verify_status.get());
            getDataManager().setRVPSecureOTPVerified(ofd_otp_verified.get());
            getNavigator().onOtpVerifySuccess("RQC");
        } else {
            getNavigator().showError("Please verify OTP");
        }
    }

    public void getRvpDataWithQc(String compositeKey) {
        try {
            getCompositeDisposable().add(getDataManager().
                    getRvpWithQc(compositeKey).
                    subscribeOn(getSchedulerProvider().io()).
                    observeOn(getSchedulerProvider().ui()).
                    subscribe(rvpWithQC -> {
                        rvpWithQCObservableField.set(rvpWithQC);
                        if (rvpWithQC.drsReverseQCTypeResponse.getFlags().flagMap.getIs_mdc_rvp_qc_disabled().equalsIgnoreCase("true")) {
                            getMDCDisbaledCase();
                        } else {
                            getRvpMasterData();
                        }
                    }, throwable -> {
                        setIsLoading(false);
                        try {
                            new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                        } catch (Exception e) {
                            getNavigator().onHandleError(e.getMessage());
                        }
                    }));
        } catch (Exception e) {
            Logger.e(RVPSecureDeliveryViewModel.class.getName(), e.getMessage());
            getNavigator().onHandleError(e.getMessage());
        }
    }

    // change
    private void getMDCDisbaledCase() {
        ArrayList<SampleQuestion> sampleQuestions = new ArrayList<>();
        try {
            for (int i = 0; i < Objects.requireNonNull(rvpWithQCObservableField.get()).rvpQualityCheckList.size(); i++) {
                SampleQuestion sampleQuestion = new SampleQuestion();
                sampleQuestion.setCode(Objects.requireNonNull(rvpWithQCObservableField.get()).rvpQualityCheckList.get(i).getQcCode());
                sampleQuestion.setName(Objects.requireNonNull(rvpWithQCObservableField.get()).rvpQualityCheckList.get(i).getQcName());
                sampleQuestion.setImageCaptureSettings(Objects.requireNonNull(rvpWithQCObservableField.get()).rvpQualityCheckList.get(i).getImageCaptureSettings());
                sampleQuestion.setInstructions(Objects.requireNonNull(rvpWithQCObservableField.get()).rvpQualityCheckList.get(i).getInstructions());
                sampleQuestion.setVerificationMode(Objects.requireNonNull(rvpWithQCObservableField.get()).rvpQualityCheckList.get(i).getQcType());
                sampleQuestions.add(sampleQuestion);
            }
        } catch (Exception e) {
            Logger.e(RVPSecureDeliveryViewModel.class.getSimpleName(), e.getMessage());
        }
        sampleQuestionList.set(new ArrayList<>(sampleQuestions));
    }

    private void getRvpMasterData() {
        try {
            getCompositeDisposable().add(getDataManager().getRvpMasterDescriptions(Objects.requireNonNull(rvpWithQCObservableField.get()).rvpQualityCheckList).subscribeOn(getSchedulerProvider().io()).
                    observeOn(getSchedulerProvider().ui()).
                    subscribe(sampleQuestions -> {
                        try {
                            for (int i = 0; i < Objects.requireNonNull(rvpWithQCObservableField.get()).rvpQualityCheckList.size(); i++) {
                                for (int j = 0; j < sampleQuestions.size(); j++) {
                                    String code = Objects.requireNonNull(rvpWithQCObservableField.get()).rvpQualityCheckList.get(i).getQcCode();
                                    if (code.equalsIgnoreCase(sampleQuestions.get(j).getCode())) {
                                        Collections.swap(sampleQuestions, i, j);
                                    }
                                    if (sampleQuestions.get(j).getCode().startsWith("GEN_ITEM_BRAND_CHECK")) {
                                        if (Objects.requireNonNull(rvpWithQCObservableField.get()).rvpQualityCheckList.get(i).getQcCode().equalsIgnoreCase("GEN_ITEM_BRAND_CHECK")) {
                                            String s = sampleQuestions.get(j).getName().replace("#COLOR#", Objects.requireNonNull(rvpWithQCObservableField.get()).rvpQualityCheckList.get(i).getQcValue());
                                            sampleQuestions.get(j).setName(s);
                                        }
                                    }
                                }
                            }

                        } catch (Exception e) {
                            Logger.e(RVPSecureDeliveryViewModel.class.getName(), e.getMessage());
                        }
                        sampleQuestionList.set(new ArrayList<>(sampleQuestions));
                    }));
        } catch (Exception e) {
            Logger.e(RVPSecureDeliveryViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }
}



