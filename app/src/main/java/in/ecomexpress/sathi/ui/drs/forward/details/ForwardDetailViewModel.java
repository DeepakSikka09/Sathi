package in.ecomexpress.sathi.ui.drs.forward.details;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;

import androidx.databinding.ObservableField;

import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.payphi.logisticsdk.PayPhiSdk;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import cz.msebera.android.httpclient.HttpConnection;
import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.repo.local.db.model.MsgLinkData;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.otp.resendotp.ResendOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.otp.verifyotp.VerifyOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.payphi.awb_register.AwbRequest;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.EncryptionDecription;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class ForwardDetailViewModel extends BaseViewModel<IForwardDetailNavigator> {
    private DRSForwardTypeResponse mDrsForwardTypeResponse;
    Boolean stIsOtpRequired = false;
    String stAmt = "";
    Integer stAddressPincode;

    Map<String, String> payphiData;
    String  merchant_id, payphi_appid;
    String stType, stAwb, stName, stAddressLine1, stAddressLine2, stAddressLine3, stAddressLine4, stAddressCity, stAddressState, stfinalAddress, stAddress, stMobile;
    public static String device = (Build.MANUFACTURER + ":" + Build.MODEL).toUpperCase(Locale.US);
    boolean misDigialPaymentAllowed;
    private ForwardCommit forwardCommit;
    ProgressDialog dialog;
    private final ObservableField<String> edtOtp = new ObservableField<>("");
    private final ObservableField<String> edtAmount = new ObservableField<>("");

    boolean cod_status_handler_called = true;
    int cod_handler_counter = 0;
    Handler codhandler;
    Runnable codrunnable;

    public ObservableField<String> getEdtOtp(){
        return edtOtp;
    }

    public ObservableField<String> getEdtAmount(){
        return edtAmount;
    }


    public static final String TAG = HttpConnection.class.getSimpleName();

    /*Awb*/
    private final ObservableField<String> itemAwb = new ObservableField<>();
    public final ObservableField<Boolean> countDownStatus = new ObservableField<>();

    public ObservableField<String> getItemAwb(){
        return itemAwb;
    }

    /*Name*/
    private final ObservableField<String> itemName = new ObservableField<>();

    public ObservableField<String> getItemName(){
        return itemName;
    }

    /*Address*/
    private final ObservableField<String> itemAddress = new ObservableField<>();

    public ObservableField<String> getItemAddress(){
        return itemAddress;
    }

    /*Amount*/
    private final ObservableField<String> itemAmt = new ObservableField<>();

    public ObservableField<String> getItemAmt(){
        return itemAmt;
    }





    /*Otp Mobile number*/
    private final ObservableField<String> itemMobileforOtp = new ObservableField<>();

    public ObservableField<String> getItemMobileforOtp(){
        return itemMobileforOtp;
    }

    /*Type*/
    private final ObservableField<String> itemType = new ObservableField<>();

    public ObservableField<String> getItemType(){
        return itemType;
    }

    public boolean send_link_clicked = false;
    public ObservableField<Boolean> clicked_status = new ObservableField<>();

    @Inject
    public ForwardDetailViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication){
        super(dataManager, schedulerProvider, sathiApplication);
    }

    public void onUndeliveredClick(){
        try{
            forwardCommit.setAttempt_reason_code(Constants.FORWARD_COMMIT_REASON_CODE);
            forwardCommit.setDeclared_value(mDrsForwardTypeResponse.getShipmentDetails().getDeclaredValue().toString());
            getNavigator().onUndelivered(forwardCommit);
        } catch(Exception e){
            Logger.e(ForwardDetailViewModel.class.getName(), e.getMessage());
            getNavigator().onerrorMsg(e.getMessage());
        }
    }

    public void onNextClick(){
        try{
            forwardCommit.setAttempt_reason_code(Constants.FORWARD_COMMIT_REASON_CODE);
            forwardCommit.setDeclared_value(mDrsForwardTypeResponse.getShipmentDetails().getDeclaredValue().toString());
            getNavigator().onNext(forwardCommit);
        } catch(Exception e){
            Logger.e(ForwardDetailViewModel.class.getName(), e.getMessage());


            getNavigator().onerrorMsg(e.getMessage());
        }
    }



    private void setForwardCommitValue(){
        try{
            forwardCommit.setDrs_date(String.valueOf(mDrsForwardTypeResponse.getAssignedDate()));
            forwardCommit.setAwb(getItemAwb().get());
            forwardCommit.setDeclared_value(String.valueOf(mDrsForwardTypeResponse.getShipmentDetails().getDeclaredValue()));
            forwardCommit.setDrs_id(mDrsForwardTypeResponse.getDrsId() + "");
            forwardCommit.setLocation_lat(String.valueOf(getDataManager().getCurrentLatitude()));
            forwardCommit.setLocation_long(String.valueOf(getDataManager().getCurrentLongitude()));
            forwardCommit.setConsignee_name(mDrsForwardTypeResponse.getConsigneeDetails().getName());
            forwardCommit.setShipment_type(Constants.SHIPMENT_TYPE_FORWARD);
            forwardCommit.setAttempt_reason_code(Constants.FORWARD_COMMIT_REASON_CODE);
            forwardCommit.setDeclared_value(mDrsForwardTypeResponse.getShipmentDetails().getDeclaredValue().toString());
        } catch(Exception e){
            Logger.e(ForwardDetailViewModel.class.getName(), e.getMessage());


            getNavigator().onerrorMsg(e.getMessage());
        }
    }

   /* public void onBackClick(){
        getNavigator().onBack();
    }*/

    public void onBackOption(){
        getNavigator().onPayeCODLayoutVisibile();
    }

    public void onOtpVerifyClick(){
        getNavigator().onOtpVerifyButton();
    }

    public void onOtpResendClick(){
        getNavigator().onOtpResendButton();
    }

    public void onPaybyCashClick(){
        getNavigator().onCashPaymentSelected();
    }

    public void onMessageLinkClick(){
        forwardCommit.setPayment_mode("link");
        Constants.PAYMENT_MODE = "link";
        getNavigator().onMsgLinkSelected();
    }

    public void onPaybyCardClick(){
        forwardCommit.setPayment_mode("QR");
        Constants.PAYMENT_MODE = "QR";
        getNavigator().onCardPaymentSelected();
    }

    public void onEcodClick(){
        if(getNavigator().validateDigitalClick()){
            showEcodStatusDelayDialog("Ecod status updating please wait...");
        } else{
            getNavigator().onHandleError("Please make digital payment first");
        }
    }

    public void showEcodStatusDelayDialog(String message){
        getNavigator().getContextProvider().runOnUiThread(new Runnable() {
            @Override
            public void run(){
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getNavigator().getContextProvider(),R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert).setMessage(message).setPositiveButton("Wait", null);
                Dialog dialog = alertDialog.create();
                dialog.setCancelable(false);
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    private final long AUTO_DISMISS_MILLIS = getDataManager().getCodStatusInterval();

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
                                if(cod_status_handler_called){
                                    cod_status_handler_called = false;
                                    codhandler = new Handler();
                                    codrunnable = new Runnable() {
                                        @Override
                                        public void run(){
                                            if(cod_handler_counter > getDataManager().getCodeStatusIntervalFraction()){
                                                codhandler.removeCallbacks(this);

                                            } else{
                                                cod_handler_counter++;
                                                getNavigator().onEcodClick();
                                                codhandler.postDelayed(this, (AUTO_DISMISS_MILLIS / getDataManager().getCodeStatusIntervalFraction()));
                                            }
                                        }
                                    };
                                    codhandler.postDelayed(codrunnable, 1000);
                                }
                            }

                            @Override
                            public void onFinish(){
                                try{
                                    if(((AlertDialog) dialog).isShowing()){
                                        dialog.dismiss();
                                    }
                                    codhandler.removeCallbacks(codrunnable);
                                    cod_status_handler_called = true;
                                    cod_handler_counter = 0;
                                } catch(Exception e){
                                    Logger.e(ForwardDetailViewModel.class.getName(), e.getMessage());


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

    public void onDisputeClick(){
        getNavigator().raiseDispute();
    }

    public void onNextAmountClick(){
        try{
            forwardCommit.setAttempt_reason_code(Constants.FORWARD_COMMIT_REASON_CODE);
            forwardCommit.setDeclared_value(mDrsForwardTypeResponse.getShipmentDetails().getDeclaredValue().toString());
            forwardCommit.setShipment_type(mDrsForwardTypeResponse.getShipmentType());
            getNavigator().onNextAmountClick(forwardCommit);
        } catch(Exception e){
            Logger.e(ForwardDetailViewModel.class.getName(), e.getMessage());


            getNavigator().onerrorMsg(e.getMessage());
        }
    }

    //FETCHING DATA FROM LIST ITEM
    public void getShipmentData(ForwardCommit nForwardCommit, String composite_key){
        this.forwardCommit = nForwardCommit;
        try{
            getCompositeDisposable().add(getDataManager().getForwardDRS(composite_key).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(drsForwardTypeResponse -> {
                Log.d(TAG, "accept: " + drsForwardTypeResponse.getFlags().toString());
                if(drsForwardTypeResponse != null){
                    mDrsForwardTypeResponse = drsForwardTypeResponse;
                    getDrsResponse(drsForwardTypeResponse);
                }
            }));
        } catch(Exception e){
            Logger.e(ForwardDetailViewModel.class.getName(), e.getMessage());


            getNavigator().onerrorMsg(e.getMessage());
        }
    }

    @SuppressLint("DefaultLocale")
    public void getDrsResponse(DRSForwardTypeResponse drsForwardTypeResponse){
        try{
            stType = drsForwardTypeResponse.getShipmentDetails().getType();
            stIsOtpRequired = drsForwardTypeResponse.getFlags().getOtpRequired();
            stAwb = drsForwardTypeResponse.getAwbNo().toString();
            stName = drsForwardTypeResponse.getConsigneeDetails().getName();
            stAddressLine1 = drsForwardTypeResponse.getConsigneeDetails().getAddress().getLine1();
            stAddressLine2 = drsForwardTypeResponse.getConsigneeDetails().getAddress().getLine2();
            stAddressLine3 = drsForwardTypeResponse.getConsigneeDetails().getAddress().getLine3();
            stAddressLine4 = drsForwardTypeResponse.getConsigneeDetails().getAddress().getLine4();
            stAddressCity = drsForwardTypeResponse.getConsigneeDetails().getAddress().getCity();
            stAddressState = drsForwardTypeResponse.getConsigneeDetails().getAddress().getState();
            stAddressPincode = drsForwardTypeResponse.getConsigneeDetails().getAddress().getPincode();
            try{
                forwardCommit.setDeclared_value(String.valueOf(drsForwardTypeResponse.getShipmentDetails().getDeclaredValue()));
            } catch(Exception e){
                getNavigator().onHandleError(e.getLocalizedMessage());
            }
            getNavigator().ontextchange(stType);
            stAddress = stAddressLine1 + "\n" + stAddressLine2 + "\n" + stAddressLine3 + "," + stAddressLine4 + "\n" + stAddressCity + "," + stAddressState + "," + stAddressPincode;
            stfinalAddress = stAddress.replaceAll(",null", "");
            if(stType.equalsIgnoreCase("PPD")){
                stAmt = String.format("%.2f", Double.parseDouble(drsForwardTypeResponse.getShipmentDetails().getDeclaredValue().toString()));
            } else if(stType.equalsIgnoreCase("COD")){
                stAmt = String.format("%.2f", Double.parseDouble(drsForwardTypeResponse.getShipmentDetails().getCollectableValue().toString()));
            }
            setInfo(stAwb, stAmt, stType, stName, stfinalAddress, stMobile, stIsOtpRequired);
        } catch(Exception e){
            Logger.e(ForwardDetailViewModel.class.getName(), e.getMessage());


            getNavigator().onerrorMsg(e.getMessage());
            getNavigator().setvisibility();
        }
    }

    //CHECK VISIBILITY ACCORDING TO OTP REQUIRED

    /**
     *
     * @param stIsOtpRequired-- to check for otp is required or not either true or false
     * @param mobile-- mobile input field
     */
    public void checkLayoutVisibility(Boolean stIsOtpRequired, String mobile){
        stIsOtpRequired = false;
        try{
            if(stIsOtpRequired == true){
                itemMobileforOtp.set("Please Enter the Otp sent on your registered mobile no.");
                getItemMobileforOtp().set(itemMobileforOtp.get());
                getNavigator().onOTPLayoutVisibile();
            } else{
                if(stType.equalsIgnoreCase("COD")){
                    //show amount layout_scan_popup
                    getNavigator().onPayeCODLayoutVisibile();
                }
                if(stType.equalsIgnoreCase("PPD")){
                    //open signature screen
                    getNavigator().showUndeliveredOption();
                }
            }
        } catch(Exception e){
            Logger.e(ForwardDetailViewModel.class.getName(), e.getMessage());


            getNavigator().onerrorMsg(e.getMessage());
        }
    }

    //SETTING DATA FROM LIST ITEM TO VIEW
    public void setInfo(String awb, String amt, String type, String name, String address, String mobile, Boolean otpRequired){
        itemAwb.set(awb);
        itemAmt.set(amt);
        itemType.set(type);
        itemName.set(name);
        itemAddress.set(address);
        itemMobileforOtp.set("Please Enter the Otp sent on your Registered mobile no. ");
        setForwardCommitValue();
        checkLayoutVisibility(otpRequired, mobile);
    }

    // ON CARD CLICK
    // METHOD 1
    //INITIALIZE PAYPHI SDK
    public void InitialisePayphiSdk(Activity context, String awb, String drs_id_num){
        try{
            dialog = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
            dialog.show();
            dialog.setMessage("Initializing......");
            dialog.setCancelable(false);
            dialog.setIndeterminate(false);
            //if prod then use Prod otherwise only QA
            String apiKey = Constants.PAYPHI_APPID_PROD;
            String mID = Constants.PAYPHI_MERCHANT_ID_PROD;
            PayPhiSdk.setEnv(context, PayPhiSdk.PROD);
            PayPhiSdk.setPosPref(context, PayPhiSdk.MSWIPE);
            try{
                merchant_id = new EncryptionDecription().decrypt(Constants.PAYPHI_MERCHANT_ID_PROD, "payphi");
                payphi_appid = new EncryptionDecription().decrypt(Constants.PAYPHI_APPID_PROD, "payphi");
            } catch(Exception e){
                Logger.e(ForwardDetailViewModel.class.getName(), e.getMessage());


            }
            PayPhiSdk.setAppInfo(mID, apiKey, context, new PayPhiSdk.IAppInitializationListener() {
                @Override
                public void onSuccess(String s){
                    Log.d("Success", s);
                    if(s.equalsIgnoreCase("0000")){
                        dialog.dismiss();
                        getNavigator().setCardClicked();
                        //CALLING API FOR REGISTERING AWB NO. IN METHOD 2
                        ServerCallToRegisterAwb(awb, context, drs_id_num);
                    }
                }

                @Override
                public void onFailure(String s){
                    Log.d("failure", s);
                    if(dialog.isShowing())
                        dialog.dismiss();
                    if(s.equalsIgnoreCase("101")){
                        getPopup("FAIL", "PayPhi Internal error", "", context);
                    } else if(s.equalsIgnoreCase("504")){
                        getPopup("FAIL", "PayPhi Connection error", "", context);
                    } else if(s.equalsIgnoreCase("201")){
                        getPopup("FAIL", "Invalid app credentials", "", context);
                    } else if(s.equalsIgnoreCase("205")){
                        getPopup("FAIL", "Payments not enabled", "", context);
                    } else{
                        getNavigator().onHandleError("Please Check your Internet Connection...");
                    }
                }
            });
        } catch(Exception e){
            Logger.e(ForwardDetailViewModel.class.getName(), e.getMessage());


            getNavigator().onHandleError(e.getMessage());
        }
    }

    // ON CARD CLICK
    // METHOD 2
    //making api call on card button click
    public void ServerCallToRegisterAwb(String awb, Activity context, String drs_id_num){
        setIsLoading(true);
        try{
            final long timeStamp = System.currentTimeMillis();
            AwbRequest request = new AwbRequest(awb, getDataManager().getEmp_code(), drs_id_num);
            writeRestAPIRequst(timeStamp, request);
            getCompositeDisposable().add(getDataManager().doAwbRegisterApiCallForSms(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), request).doOnSuccess(awbResponse -> {
                setIsLoading(false);
                writeRestAPIResponse(timeStamp, awbResponse);
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                setIsLoading(false);
                if(response.getStatus().equalsIgnoreCase("true")){
                    PayPiPayment(awb, context, drs_id_num);
                } else{
                    getNavigator().onHandleError(response.getResponse().getDescription());
                }
            }, throwable -> {
                setIsLoading(false);
                String error;
                try{
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                    getNavigator().showerrorMessage(error);
                } catch(NullPointerException e){
                    Logger.e(ForwardDetailViewModel.class.getName(), e.getMessage());


                }
            }));
        } catch(Exception e){
            setIsLoading(false);
            getNavigator().onHandleError(e.getMessage());
            Logger.e(ForwardDetailViewModel.class.getName(), e.getMessage());


        }
    }

    /**
     *
     * @param awb--awb
     * @param context activity context
     * @param drs_id-- drs id
     */
    public void registerAwbForMsgLink(String awb, Activity context, String drs_id){
        setIsLoading(true);
        try{
            final long timeStamp = System.currentTimeMillis();
            AwbRequest request = new AwbRequest(awb, getDataManager().getEmp_code(), drs_id);
            writeRestAPIRequst(timeStamp, request);
            getCompositeDisposable().add(getDataManager().doAwbRegisterApiCallForSms(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), request).doOnSuccess(awbResponse -> {
                setIsLoading(false);
                writeRestAPIResponse(timeStamp, awbResponse);
                if(awbResponse.getStatus().equalsIgnoreCase("true")){
                    processAfterApiCall();
                    getNavigator().showNumberPopup();
                } else
                    getNavigator().showerrorMessage(awbResponse.getResponse().getDescription());
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                setIsLoading(false);
                if(response.getStatus().equalsIgnoreCase("true")){

                } else{
                    getNavigator().onHandleError(response.getResponse().getDescription());
                }
            }, throwable -> {
                setIsLoading(false);
                String error;
                try{
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();

                    getNavigator().showerrorMessage(error);
                } catch(NullPointerException e){
                    Logger.e(ForwardDetailViewModel.class.getName(), e.getMessage());


                }
            }));
        } catch(Exception e){
            setIsLoading(false);
            getNavigator().onHandleError(e.getMessage());
            Logger.e(ForwardDetailViewModel.class.getName(), e.getMessage());


        }
    }

    public boolean getIsDigitalPaymentFlag(boolean isDigialPaymentAllowed){
        this.misDigialPaymentAllowed = isDigialPaymentAllowed;
        return misDigialPaymentAllowed;
    }

    // ON CARD CLICK
    // METHOD 3
    // SHOWING RESULT POPUP

    /**
     *
     * @param awb-- awb no.
     * @param context-- activity context
     * @param drs_id_num-- drs id
     */
    public void PayPiPayment(String awb, Activity context, String drs_id_num){
        dialog.setMessage("Make Payment......");
        dialog.setCancelable(false);
        payphiData = new HashMap<>();
        payphiData.put("MerchantID", Constants.PAYPHI_MERCHANT_ID_PROD);
        payphiData.put("deviceID", "");
        payphiData.put("awbNumber", awb);
        if(misDigialPaymentAllowed){
            payphiData.put("PaymentType", "");
        } else{
            payphiData.put("PaymentType", "upi");
        }
        PayPhiSdk.makePayment(context, payphiData, PayPhiSdk.DIALOG, (resultCode, map) -> {
            dialog.dismiss();
            try{
                switch(resultCode){
                    case -1:
                        try{
                            String key = "returnCode";
                            String value = "";
                            String description = "", info = "";
                            dialog.dismiss();
                            for(Map.Entry entry : map.entrySet()){
                                if(key.equals(entry.getKey())){
                                    value = entry.getValue().toString();
                                    if(value.equalsIgnoreCase("0") || value.equalsIgnoreCase("000") || value.equalsIgnoreCase("0000")){
                                        getNavigator().onHandleError("Success");
                                    }
                                } else if("paymentID".equals(entry.getKey())){
                                    forwardCommit.setPayment_id(entry.getValue().toString());

                                } else if("description".equals(entry.getKey())){
                                    description = entry.getValue().toString();
                                } else if("additionalInfo".equals(entry.getKey())){
                                    if(!entry.getValue().toString().isEmpty()){
                                        info = entry.getValue().toString();
                                        dialog.dismiss();
                                        getNavigator().onHandleError(info);
                                    }
                                }
                            }
                            if(forwardCommit.getPayment_id().isEmpty()){
                                getPopup("FAIL", description, "", context);
                            } else{

                                dialog.dismiss();
                                ServerCallToCheckStatus(awb, context, "payphi", drs_id_num);
                            }
                        } catch(Exception j){
                            if(dialog.isShowing())
                                dialog.dismiss();
                            getNavigator().onHandleError(j.getMessage());
                            j.printStackTrace();
                        }
                        break;
                    case 0:
                        getPopup("FAIL", "PayPhi Transcation CANCELED", "", context);
                        break;
                    case 2:
                        getPopup("FAIL", "PayPhi Application Error", "", context);
                        break;
                    case 3:
                        getPopup("FAIL", "PayPhi Network Error", "", context);
                        break;
                    case 4:
                        getPopup("FAIL", "PayPhi Configuration Error", "", context);
                        break;
                    case 5:
                        getPopup("FAIL", "PayPhi Close Request from App", "", context);
                        break;
                }
            } catch(Exception e){
                dialog.dismiss();
                Logger.e(ForwardDetailViewModel.class.getName(), e.getMessage());


                getNavigator().onHandleError(e.getMessage());
            }
        });
    }

    public void apiCallForSendMsg(){

    }

    public void processAfterApiCall(){
        MsgLinkData msgLinkData = new MsgLinkData();
        msgLinkData.setAwb_number(stAwb);
        msgLinkData.setWas_clicked(true);
        saveApiUrl(msgLinkData);
    }

    public void saveApiUrl(MsgLinkData msgLinkData){
        try{
            getCompositeDisposable().add(getDataManager().insertSmsLink(msgLinkData).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
                Log.e("isDataSaved?", "saved");

                getAllApiUrl(stAwb);
            }, Throwable::printStackTrace));
        } catch(Exception e){
            Logger.e(ForwardDetailViewModel.class.getName(), e.getMessage());


        }
    }

    /**
     *
     * @param stAwb--awb no. in string
     */
    public void getAllApiUrl(String stAwb){
        try{
            getCompositeDisposable().add(getDataManager().getSmsLink(stAwb).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(msgLinkData -> {
                try{
                    if(msgLinkData != null){
                        if(msgLinkData.getWas_clicked()){
                            send_link_clicked = (msgLinkData.getWas_clicked());
                            clicked_status.set(send_link_clicked);
                            getNavigator().changeLinkText();
                        } else{
                            send_link_clicked = false;
                            clicked_status.set(send_link_clicked);
                        }
                    }
                } catch(Exception e){
                    Logger.e(ForwardDetailViewModel.class.getName(), e.getMessage());


                }
            }, throwable -> {
            }));
        } catch(Exception e){
            Logger.e(ForwardDetailViewModel.class.getName(), e.getMessage());


        }
    }

    /**
     *
     * @param awb-- awb no.
     * @param context--activity context
     * @param info--is status/paypi
     * @param drs_id_num-- drsid
     */
    public void ServerCallToCheckStatus(String awb, Activity context, String info, String drs_id_num){
        dialog = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
        dialog.show();
        dialog.setMessage("Checking Status...");
        try{
            getCompositeDisposable().add(getDataManager().doCheckStatusApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(),new AwbRequest(awb, getDataManager().getCode(), drs_id_num)).doOnSuccess(awbResponse -> {
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                setIsLoading(false);
                if(response.getStatus().equals("true")){
                    if(dialog.isShowing())
                        dialog.dismiss();
                    if(response.getResponse().getPayment_id() != null){
                        forwardCommit.setPayment_id(response.getResponse().getPayment_id());
                      //  getPopup("Successful!", response.getResponse().getDescription(), "", context);

                        getNavigator().openSignatureActivity("ecod", itemAmt.get());

                    } else{
                        if(info.equalsIgnoreCase("cash")){
                            getNavigator().onProceedtoCash();
                        } else{
                            if(getDataManager().getMessageClicked(awb)){
                                if(cod_handler_counter == getDataManager().getCodeStatusIntervalFraction()){
                                    getNavigator().showDisputeButton();
                                }
                            } else if(getDataManager().getCardClicked(awb)){
                                if(cod_handler_counter == getDataManager().getCodeStatusIntervalFraction()){
                                    getNavigator().showDisputeButton();
                                }
                            }
                        }
                    }
                } else{
                    try{
                        if(dialog.isShowing())
                            dialog.dismiss();
                        if(info.equalsIgnoreCase("cash")){
                            if(response.getResponse().getDescription().equalsIgnoreCase("COD amount not received yet. eCOD transaction got interrupted. Please try again.")){
                                getNavigator().onProceedtoCash();
                            }
                        } else{
                            if(getDataManager().getMessageClicked(awb)){
                                if(cod_handler_counter == getDataManager().getCodeStatusIntervalFraction()){
                                    getNavigator().showDisputeButton();
                                }
                            } else if(getDataManager().getCardClicked(awb)){
                                if(cod_handler_counter == getDataManager().getCodeStatusIntervalFraction()){
                                    getNavigator().showDisputeButton();
                                }
                            } else{
                                getNavigator().onHandleError(response.getResponse().getDescription());
                            }
                        }
                    } catch(Exception e){
                        Logger.e(ForwardDetailViewModel.class.getName(), e.getMessage());


                    }
                }
            }, throwable -> {
                if(dialog.isShowing())
                    dialog.dismiss();
                String error;
                try{
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                    getNavigator().showerrorMessage(error);
                } catch(NullPointerException e){
                    Logger.e(ForwardDetailViewModel.class.getName(), e.getMessage());


                }
            }));
        } catch(Exception e){
            if(dialog.isShowing())
                dialog.dismiss();
            Logger.e(ForwardDetailViewModel.class.getName(), e.getMessage());


            getNavigator().onHandleError(e.getMessage());
        }
    }

    //ON VERIFY BUTTON CLICK
    //making api call on verify button click
    public void OnVerifyApiCall(Activity context, String awb, String otp, String type){

        dialog = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
        dialog.setMessage("Verifying....");
        dialog.show();
        dialog.setIndeterminate(false);
        try{
            VerifyOtpRequest request = new VerifyOtpRequest(awb, otp, "FWD", type, "");
            final long timeStamp = System.currentTimeMillis();
            writeRestAPIRequst(timeStamp, request);
            getCompositeDisposable().add(getDataManager().doVerifyOtpApiCall(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), request).doOnSuccess(verifyOtpResponse -> {
                writeRestAPIResponse(timeStamp, verifyOtpResponse);
                Log.d(ContentValues.TAG, verifyOtpResponse.toString());
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                setIsLoading(false);
                if(dialog.isShowing())
                    dialog.dismiss();
                if(response.getStatus()){
                    getNavigator().onOtpVerifySuccess(true);
                } else{
                    if(response.getResponse().getCode().equalsIgnoreCase("E137")){
                        getNavigator().getAlert();

                    } else
                        getNavigator().onHandleError(response.getResponse().getDescription());
                }
            }, throwable -> {
                if(dialog.isShowing())
                    dialog.dismiss();
                String error;
                try{
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();

                    getNavigator().showerrorMessage(error);
                } catch(Exception e){
                    Logger.e(ForwardDetailViewModel.class.getName(), e.getMessage());


                    getNavigator().onHandleError(e.getMessage());
                }
            }));
        } catch(Exception e){
            Logger.e(ForwardDetailViewModel.class.getName(), e.getMessage());


            if(dialog.isShowing())
                dialog.dismiss();
            getNavigator().onHandleError(e.getMessage());
        }
    }

    //ON RESEND BUTTON CLICK
    //making api call on resend button click
    public void OnResendApiCall(Activity context, String awb, String drsid){

        dialog = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
        dialog.setMessage("Sending OTP....");
        dialog.show();
        dialog.setIndeterminate(false);
        ResendOtpRequest request = new ResendOtpRequest(awb, "OTP",drsid,false);
        final long timeStamp = System.currentTimeMillis();
        writeRestAPIRequst(timeStamp, request);
        try{
            getCompositeDisposable().add(getDataManager().doResendOtpApiCall(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), request).doOnSuccess(resendOtpResponse -> {
                writeRestAPIResponse(timeStamp, resendOtpResponse);
                Log.d(ContentValues.TAG, resendOtpResponse.toString());
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                if(dialog.isShowing())
                    dialog.dismiss();
                if(response.getStatus().equals("true")){
                    String newMobileNo = response.getResponse().getMobile();
                    itemMobileforOtp.set("Please Enter the Otp sent on " + CommonUtils.imeiFullStars(newMobileNo));
                    getNavigator().onOtpResendSuccess();
                } else{
                    getNavigator().onHandleError(response.getResponse().getDescription());
                }
            }, throwable -> {
                if(dialog.isShowing())
                    dialog.dismiss();
                String error;
                try{
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();

                    getNavigator().showerrorMessage(error);
                } catch(Exception e){
                    if(dialog.isShowing())
                        dialog.dismiss();
                    Logger.e(ForwardDetailViewModel.class.getName(), e.getMessage());


                    getNavigator().onHandleError(e.getMessage());
                }
            }));
        } catch(Exception e){
            if(dialog.isShowing())
                dialog.dismiss();
            Logger.e(ForwardDetailViewModel.class.getName(), e.getMessage());


            getNavigator().onerrorMsg(e.getMessage());
        }

    }

    //SHOWING CUSTOM ALERT DIALOG
    @SuppressLint("SetTextI18n")
    public void getPopup(final String title, String value, String customer_number, Activity context){
        if (context.isFinishing()) {
            return;
        }
        Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.payphi_popupwindow);
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        TextView title_tv = dialog.findViewById(R.id.title_tv);
        TextView detail_tv = dialog.findViewById(R.id.detail_tv);
        ImageView cross = dialog.findViewById(R.id.cross);
        Button okPopup = dialog.findViewById(R.id.ok_popup);
        title_tv.setText(title);
        if(!value.isEmpty()){
            detail_tv.setText(value);
        } else{
            detail_tv.setVisibility(View.GONE);
        }
        if(title.equalsIgnoreCase("Pending") || title.equalsIgnoreCase("FAIL")){
            title_tv.setBackgroundResource(R.drawable.circle_red);
        } else{
            if(title.contains("Successful")){
                title_tv.setText("Successful!");
                detail_tv.setText("Payment completed!! Please press OKAY to move forward");
                title_tv.setBackgroundResource(R.drawable.circular_green);
            }
        }
        okPopup.setOnClickListener(v -> {
            if(dialog != null && dialog.isShowing()){
                if(title.contains("Successful")){
                    getNavigator().openSignatureActivity("ecod", itemAmt.get());
                } else{
                    dialog.dismiss();
                }
                dialog.dismiss();
            }
        });
        cross.setOnClickListener(v -> {
            if(title.contains("Successful")){
                getNavigator().onHandleError("Cannot go back. Please deliver this shipment.");
            } else{
                dialog.dismiss();
            }
        });
        if(!context.isFinishing())
        {
           dialog.show();
        }
        dialog.setCancelable(true);
    }

    public void logoutLocal(){

        getDataManager().setTripId("");
        getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT);
        clearAppData();
    }

    private void clearAppData(){
        try{
            getCompositeDisposable().add(getDataManager().deleteAllTables().subscribeOn(getSchedulerProvider().io()).
                    observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
                        try{
                            getDataManager().clearPrefrence();
                            getDataManager().setUserAsLoggedOut();
                        } catch(Exception ex){
                            ex.printStackTrace();
                        }
                        getNavigator().clearStack();
                    }));
        } catch(Exception e){
            Logger.e(ForwardDetailViewModel.class.getName(), e.getMessage());


            getNavigator().onerrorMsg(e.getMessage());
        }
    }





    public void onSubmitClick(){
        getNavigator().checkValidation();
    }

    public void onCancelClick(){
        getNavigator().dismissDialog();
    }


    public void openSignatureActivityDispute(){
        getNavigator().openSignatureActivityDispute("ecod", itemAmt.get());
    }

    public void getDisputedAwb(String awb){
        try{
            getCompositeDisposable().add(getDataManager().
                    getdisputed(awb).
                    observeOn(getSchedulerProvider().ui()).
                    subscribeOn(getSchedulerProvider().io()).
                    subscribe(awb_result -> {
                        if(awb_result.equalsIgnoreCase(awb)){
                            getNavigator().checkDisputedStatus(true);
                        } else{
                            getNavigator().checkDisputedStatus(false);
                        }
                    }));
        } catch(Exception e){
            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
            restApiErrorHandler.writeErrorLogs(0, e.getMessage());
            Logger.e(ForwardDetailViewModel.class.getName(), e.getMessage());


        }
    }

    public void onDisputeNext(){
        getNavigator().openSignatureActivityDispute("ecod", itemAmt.get());
    }
}



