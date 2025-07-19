package in.ecomexpress.sathi.ui.drs.secure_delivery;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.RequiresApi;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import android.os.Build;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bfil.encryptionmodule.EncryptionModule;
import com.google.gson.Gson;
import com.nlscan.android.scan.ScanManager;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import cz.msebera.android.httpclient.HttpConnection;
import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.repo.IDataManager;

import in.ecomexpress.sathi.repo.local.data.eds.EdsCommit;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.repo.local.data.rvp.RvpCommit;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.local.db.model.RvpWithQC;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.drs_list.common.AmazonOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.drs_list.common.AmazonOtpResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.SecureDelivery;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.otp.otherNo.OtherNoRequest;
import in.ecomexpress.sathi.repo.remote.model.otp.otherNo.OtherNoResponse;
import in.ecomexpress.sathi.repo.remote.model.otp.resendotp.ResendOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.otp.resendotp.ResendOtpResponse;
import in.ecomexpress.sathi.repo.remote.model.otp.verifyotp.VerifyOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.otp.verifyotp.VerifyOtpResponse;
import in.ecomexpress.sathi.repo.remote.model.voice_otp.VoiceOTP;
import in.ecomexpress.sathi.repo.remote.model.voice_otp.VoiceOTPResponse;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.HMACHasherAndMatcher;
import in.ecomexpress.sathi.utils.PreferenceUtils;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.functions.Consumer;

@HiltViewModel
public class SecureDeliveryViewModel extends BaseViewModel<ISecureDeliveryNavigator> {

    SecureDelivery isSecureDelivery;

    private ForwardCommit forwardCommit;
    private RvpCommit rvpCommit;
    private EdsCommit edsCommit;
    String shipmentType;

    String productType;
    public DRSForwardTypeResponse mDrsForwardTypeResponse;
    DRSReverseQCTypeResponse drsReverseQCTypeResponse;
    EDSResponse edsResponse;
    private BroadcastReceiver mReceiver;
    public static String device = (Build.MANUFACTURER + ":" + Build.MODEL).toUpperCase(Locale.US);

    ArrayList<ForwardCommit.Amz_Scan> amz_scanArrayList = new ArrayList<>();
    Boolean stIsOtpRequired = false;

    String stAmt = "";

    Integer stAddressPincode;
    String manifestType = "abc";

    String stType, stAwb, stName, stAddressLine1, stAddressLine2, stAddressLine3, stAddressLine4, stAddressCity, stAddressState, stfinalAddress, stAddress, stMobile;
    private final ObservableField<RvpWithQC> rvpWithQC = new ObservableField<RvpWithQC>();

    ProgressDialog dialog;

    private final ObservableField<EdsWithActivityList> edsWithActivityList = new ObservableField<EdsWithActivityList>();

    public ObservableBoolean isSecureOtp = new ObservableBoolean(false);
    public ObservableBoolean isSecurePin = new ObservableBoolean(false);
    public ObservableBoolean isSecurePinB = new ObservableBoolean(false);
    public ObservableBoolean isSecurePinbOffline = new ObservableBoolean(false);
    public ObservableField<String> securePinbValue = new ObservableField<>("");


    public ObservableBoolean getIsSecurePin() {
        return isSecurePin;
    }

    public ObservableBoolean getIsSecurePinB() {
        return isSecurePinB;
    }

    public ObservableBoolean getIsSecurePinbOffline() {
        return isSecurePinbOffline;
    }

    public ObservableField<String> getSecurePinbValue() {
        return securePinbValue;
    }

    private final ObservableField<String> edtOtp = new ObservableField<>("");

    public ObservableField<String> getEdtPin() {
        return edtPin;
    }

    private final ObservableField<String> edtPin = new ObservableField<>("");

    public ObservableField<String> getEdtOtp() {
        return edtOtp;
    }

    public static final String TAG = HttpConnection.class.getSimpleName();

    /*Awb*/
    private final ObservableField<String> itemAwb = new ObservableField<>();

    public ObservableField<String> getItemAwb() {
        return itemAwb;
    }

    /*Name*/
    private final ObservableField<String> itemName = new ObservableField<>();

    public ObservableField<String> getItemName() {
        return itemName;
    }

    /*Address*/
    private final ObservableField<String> itemAddress = new ObservableField<>();

    public ObservableField<String> getItemAddress() {
        return itemAddress;
    }

    /*Amount*/
    private final ObservableField<String> itemAmt = new ObservableField<>();

    public ObservableField<String> getItemAmt() {
        return itemAmt;
    }

    /*Otp Mobile number*/
    private final ObservableField<String> itemMobileforOtp = new ObservableField<>();

    public ObservableField<String> getItemMobileforPin() {
        return itemMobileforPin;
    }

    private final ObservableField<String> itemMobileforPin = new ObservableField<>();

    public ObservableField<String> getItemMobileforOtp() {
        return itemMobileforOtp;
    }

    /*Type*/
    private final ObservableField<String> itemType = new ObservableField<>();

    public ObservableField<String> getItemType() {
        return itemType;
    }

    @Inject
    public SecureDeliveryViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider , Application sathiApplication) {
        super(dataManager, schedulerProvider , sathiApplication);
    }

    public void onUndeliveredClick() {
        try {
            if (shipmentType.equalsIgnoreCase(Constants.FWD)) {
                forwardCommit.setAttempt_reason_code(Constants.FORWARD_COMMIT_REASON_CODE);
                forwardCommit.setDeclared_value(mDrsForwardTypeResponse.getShipmentDetails().getDeclaredValue().toString());
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
                getNavigator().onUndelivered(forwardCommit);
            } else if (shipmentType.equalsIgnoreCase(Constants.RVP)) {

                getNavigator().onRVPUndelivered(rvpCommit);
            } else if (shipmentType.equalsIgnoreCase(Constants.EDS)) {
                getNavigator().onEDSUndelivered(edsCommit);
            }
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onHandleError(e.getMessage());
        }
    }

    public EDSResponse edsWithActivityList() {
        return edsResponse;
    }

    public void onNextClick() {
        try {
            forwardCommit.setAttempt_reason_code(Constants.FORWARD_COMMIT_REASON_CODE);
            forwardCommit.setDeclared_value(mDrsForwardTypeResponse.getShipmentDetails().getDeclaredValue().toString());
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onHandleError(e.getMessage());
        }
    }

    private void setForwardCommitValue() {
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
    }

  /*  public void onBackClick() {
        getNavigator().onBack();
    }*/


    public void onOtpVerifyClick() {
        getNavigator().onOtpVerifyButton();
    }

    public void onPinVerifyClick() {
        getNavigator().onPinVerifyButton();
    }

    public void onPinBVerifyClick() {
        getNavigator().onPinBVerifyClick();
    }

    public void onScanClick() {
        getNavigator().onScanClick();
    }

    public void onOtpResendClick() {

        getNavigator().onOtpResendButton();
    }

    public void showCallAndSmsDialog(String awb, String drs_id,String consigneealternatemobile)
    {
        Dialog dialog = new Dialog(getNavigator().getActivity(),R.style.RoundedCornersDialog);
        dialog.setContentView(R.layout.dialog_sms_call);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        Button sms = dialog.findViewById(R.id.bt_sms);
        Button call = dialog.findViewById(R.id.bt_call);
        ImageView crssdialog = dialog.findViewById(R.id.crssdialog);

        Button btsmsalternate = dialog.findViewById(R.id.bt_sms_alternate);

        if(!consigneealternatemobile.equals(""))
        {
            btsmsalternate.setVisibility(View.VISIBLE);
            btsmsalternate.setText("SMS on Alternate No. "+consigneealternatemobile);
        }

        btsmsalternate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                dialog.dismiss();
                getNavigator().resendSMS(true);
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
                getNavigator().resendSMS(false);
            }
        });


        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                dialog.dismiss();
               Constants.IS_CALL_CLICK=PreferenceUtils.getSharedPreferences(v.getContext()).getBoolean(awb, false);
               Constants.IS_CALL_CLICK_VERIFY=PreferenceUtils.getSharedPreferences(v.getContext()).getBoolean(awb+"secure", false);
               if(Constants.IS_CALL_CLICK && Constants.IS_CALL_CLICK_VERIFY)
               {
                   Toast.makeText(v.getContext(), "OTP is Already Verified by Call", Toast.LENGTH_SHORT).show();
               }
               else
                   {
                   getNavigator().resendVoiceCall();

                   if(shipmentType.equalsIgnoreCase(Constants.EDS))
                   {

                       doVoiceOTPApi(awb, edsResponse.getDrsNo()+"", productType);
                   }
                   else
                   {

                       doVoiceOTPApi(awb, drs_id, productType);
                   }

                   PreferenceUtils.writePreferenceValue(v.getContext(), awb, true);
               }

            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
    public void onDeliveredClick(){
        try{
            getNavigator().onDelivered();
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().onHandleError(e.getMessage());
        }
    }

    public void onBPIDClick(){
        try{
            forwardCommit.setAttempt_reason_code(Constants.FORWARD_COMMIT_REASON_CODE);
            forwardCommit.setDeclared_value(mDrsForwardTypeResponse.getShipmentDetails().getDeclaredValue().toString());
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

            getNavigator().onBPIDClick(forwardCommit);
        } catch(Exception e){
            e.printStackTrace();
            getNavigator().onHandleError(e.getMessage());
        }
    }

    public void doVoiceOTPApi(String awb, String drs_id, String productType)
    {
        try {
            VoiceOTP voiceOTP = new VoiceOTP();
            voiceOTP.awb = awb;
            voiceOTP.drs_id = drs_id;
            voiceOTP.message_type= "OTP";
            voiceOTP.employee_code = getDataManager().getEmp_code();

            voiceOTP.product_type = productType;
            getCompositeDisposable().add(getDataManager()
                    .doVoiceOtpApiCall(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), voiceOTP)
                    .doOnSuccess(new Consumer<VoiceOTPResponse>() {
                        @Override
                        public void accept(VoiceOTPResponse verifyOtpResponse) throws Exception {
                            Log.d(ContentValues.TAG, verifyOtpResponse.toString());
                        }
                    })
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(response -> {
                        setIsLoading(false);
                        try {
                            if (response.code == 0) {
                                getNavigator().showerrorMessage(response.description);
                            } else if(response.code == 1){
                                getNavigator().showerrorMessage(response.description);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            getNavigator().onHandleError(e.getMessage());
                        }
                    }, throwable -> {
                        String error;
                        try {
                            error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                            getNavigator().showerrorMessage(error);
                        } catch (Exception e) {
                            e.printStackTrace();
                            getNavigator().onHandleError(e.getMessage());
                        }
                    }));
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onHandleError(e.getMessage());
        }
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
                            SecureDeliveryViewModel.this.edsWithActivityList.set(edsWithActivityList);

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            getNavigator().onHandleError(throwable.getMessage());

                        }
                    }));
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onHandleError(e.getMessage());
        }
    }

    //FETCHING DATA FROM LIST ITEM
    public void getFWDShipmentData(ForwardCommit nForwardCommit, String composite_key, String consignee_mobile) {
        this.forwardCommit = nForwardCommit;
        try {

            getCompositeDisposable().add(getDataManager().getForwardDRS(composite_key).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<DRSForwardTypeResponse>() {
                @Override
                public void accept(DRSForwardTypeResponse drsForwardTypeResponse) throws Exception {

                    Log.d(TAG, "accept: " + drsForwardTypeResponse.getFlags().toString());
                    try {
                        if (drsForwardTypeResponse != null) {
                            mDrsForwardTypeResponse = drsForwardTypeResponse;
                            getFWDShipmentDetailsItem(drsForwardTypeResponse ,consignee_mobile);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        getNavigator().onHandleError(e.getMessage());
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    getNavigator().onHandleError(throwable.getMessage());

                }
            }));

        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onHandleError(e.getMessage());
        }
    }

    //FETCHING DATA FROM LIST ITEM

    public void getRvpShipmentData(RvpCommit mrvpCommit, String composite_key, String consignee_mobile) {
        this.rvpCommit = mrvpCommit;
        try {

            getCompositeDisposable().add(getDataManager().getRVPDRS(composite_key).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<DRSReverseQCTypeResponse>() {
                @Override
                public void accept(DRSReverseQCTypeResponse drsRVPTypeResponse) throws Exception {
                    try {
                        if (drsRVPTypeResponse != null) {
                            drsReverseQCTypeResponse = drsRVPTypeResponse;
                            getRVPShipmentDetailsItem(drsRVPTypeResponse ,consignee_mobile);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        getNavigator().onHandleError(e.getMessage());
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    getNavigator().onHandleError(throwable.getMessage());

                }
            }));

        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onHandleError(e.getMessage());
        }
    }  //FETCHING DATA FROM LIST ITEM

    public void getEDSShipmentData(EdsCommit medsCommit, String compositeKey, String consignee_mobile) {
        this.edsCommit = medsCommit;
        try {

            getCompositeDisposable().add(getDataManager().getEDS(compositeKey).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<EDSResponse>() {
                @Override
                public void accept(EDSResponse medsResponse) throws Exception {

                    try {
                        if (medsResponse != null) {
                            edsResponse = medsResponse;
                            getEDSShipmentDetailsItem(medsResponse, "EDS" ,consignee_mobile);
                        }
                    } catch (Exception e) {
                        getNavigator().onHandleError(e.getMessage());
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    getNavigator().onHandleError(throwable.getMessage());

                }
            }));

        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onHandleError(e.getMessage());
        }
    }

    public void getEDSShipmentDetailsItem(EDSResponse edsResponse, String manifest_Type, String consignee_mobile) {
        try {

            manifestType = manifest_Type;
            stAwb = edsResponse.getAwbNo().toString();
            stName = edsResponse.getConsigneeDetail().getName();
            stAddressLine1 = edsResponse.getConsigneeDetail().getAddress().getLine1();
            stAddressLine2 = edsResponse.getConsigneeDetail().getAddress().getLine2();
            stAddressLine3 = edsResponse.getConsigneeDetail().getAddress().getLine3();
            stAddressLine4 = edsResponse.getConsigneeDetail().getAddress().getLine4();
            stAddressCity = edsResponse.getConsigneeDetail().getAddress().getCity();
            stAddressState = edsResponse.getConsigneeDetail().getAddress().getState();
            stAddressPincode = edsResponse.getConsigneeDetail().getAddress().getPincode();
            stAddress = stAddressLine1 + "\n" + stAddressLine2 + "\n" + stAddressLine3 + "," + stAddressLine4 + "\n" + stAddressCity + "," + stAddressState + "," + stAddressPincode;
            stfinalAddress = stAddress.replaceAll(",null", "");
            setShipmentDetailsItem(stAwb, stAmt, stType, stName, stfinalAddress, stMobile, stIsOtpRequired ,consignee_mobile);
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onHandleError(e.getMessage());
        }
    }

    public void getRVPShipmentDetailsItem(DRSReverseQCTypeResponse drsReverseQCTypeResponse, String consignee_mobile) {
        try {
            stIsOtpRequired = Boolean.valueOf(drsReverseQCTypeResponse.getFlags().getOtpRequired().toString());
            stAwb = drsReverseQCTypeResponse.getAwbNo().toString();
            stName = drsReverseQCTypeResponse.getConsigneeDetails().getName();
            stAddressLine1 = drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getLine1();
            stAddressLine2 = drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getLine2();
            stAddressLine3 = drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getLine3();
            stAddressLine4 = drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getLine4();
            stAddressCity = drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getCity();
            stAddressState = drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getState();
            stAddressPincode = drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getPincode();
            stAddress = stAddressLine1 + "\n" + stAddressLine2 + "\n" + stAddressLine3 + "," + stAddressLine4 + "\n" + stAddressCity + "," + stAddressState + "," + stAddressPincode;
            stfinalAddress = stAddress.replaceAll(",null", "");
            stMobile = "";


            setShipmentDetailsItem(stAwb, stAmt, stType, stName, stfinalAddress, stMobile, stIsOtpRequired ,consignee_mobile);

        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onHandleError(e.getMessage());
        }
    }

    public void getFWDShipmentDetailsItem(DRSForwardTypeResponse drsForwardTypeResponse, String consignee_mobile) {
        try {
            stType = drsForwardTypeResponse.getShipmentDetails().getType();
            stIsOtpRequired = Boolean.valueOf(drsForwardTypeResponse.getFlags().getOtpRequired().toString());
            stAwb = drsForwardTypeResponse.getAwbNo().toString();
            stName = drsForwardTypeResponse.getConsigneeDetails().getName();
            stAddressLine1 = drsForwardTypeResponse.getConsigneeDetails().getAddress().getLine1();
            stAddressLine2 = drsForwardTypeResponse.getConsigneeDetails().getAddress().getLine2();
            stAddressLine3 = drsForwardTypeResponse.getConsigneeDetails().getAddress().getLine3();
            stAddressLine4 = drsForwardTypeResponse.getConsigneeDetails().getAddress().getLine4();
            stAddressCity = drsForwardTypeResponse.getConsigneeDetails().getAddress().getCity();
            stAddressState = drsForwardTypeResponse.getConsigneeDetails().getAddress().getState();
            stAddressPincode = drsForwardTypeResponse.getConsigneeDetails().getAddress().getPincode();
            forwardCommit.setDeclared_value(String.valueOf(drsForwardTypeResponse.getShipmentDetails().getDeclaredValue()));
            stAddress = stAddressLine1 + "\n" + stAddressLine2 + "\n" + stAddressLine3 + "," + stAddressLine4 + "\n" + stAddressCity + "," + stAddressState + "," + stAddressPincode;
            stfinalAddress = stAddress.replaceAll(",null", "");
            stMobile = drsForwardTypeResponse.getConsigneeDetails().getPhone() == null ? "Mobile no. not found" : drsForwardTypeResponse.getConsigneeDetails().getPhone();
            if (stType.equalsIgnoreCase("PPD")) {
                stAmt = String.format("%.2f", Double.parseDouble(drsForwardTypeResponse.getShipmentDetails().getDeclaredValue().toString()));

            } else if (stType.equalsIgnoreCase("COD")) {
                stAmt = String.format("%.2f",  Double.parseDouble(drsForwardTypeResponse.getShipmentDetails().getCollectableValue().toString()));

            }

            setShipmentDetailsItem(stAwb, stAmt, stType, stName, stfinalAddress, stMobile, stIsOtpRequired ,consignee_mobile);
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onHandleError(e.getMessage());
        }
    }

    public String getcollectablevalue ()
    {
        return mDrsForwardTypeResponse.getShipmentDetails().getCollectableValue().toString();
    }

    //SETTING DATA FROM LIST ITEM TO VIEW
    public void setShipmentDetailsItem(String awb, String amt, String type, String name, String address, String mobile, Boolean otpRequired, String consignee_mobile) {
        try {
            itemAwb.set(awb);
            itemAmt.set("\u20B9 " + amt);
            itemType.set(type);
            itemName.set(name);
            itemAddress.set(address);
            itemMobileforPin.set("Enter Pin");
            if (manifestType.equalsIgnoreCase("EDS")) {
                if (consignee_mobile != null && !consignee_mobile.isEmpty()) {
                    itemMobileforOtp.set("Please ask the consignee to share the Ref No. send on registered mobile number " +consignee_mobile);
                } else {
                    itemMobileforOtp.set("Please ask the consignee to share the Ref No. send on registered mobile number.");
                }
            } else {
                if (consignee_mobile != null && !consignee_mobile.isEmpty()) { // consignee_mobile.replaceAll("\\w(?=\\w{4})", "*")
                    itemMobileforOtp.set("Please ask the consignee to share the OTP send on registered mobile number "+consignee_mobile);
                } else {
                    itemMobileforOtp.set("Please ask the consignee to share the OTP send on registered mobile number");
                }
            }

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
                isSecurePin.set(isSecureDelivery.getSecure_pin());
                isSecurePinB.set(isSecureDelivery.getPinb());

                if (isSecureDelivery.getPinb_details() != null) {
                    securePinbValue.set(isSecureDelivery.getPinb_details().getPinb_value());
                    isSecurePinbOffline.set(isSecureDelivery.getPinb_details().getPinb_offline());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onHandleError(e.getMessage());
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean verifyEncryptedOTP(String amazon_encrypted_otp, String awb_no, String otp) {
        List<String> supplierNames = Arrays.asList(awb_no, "ECXECX" + getDataManager().getEmp_code());//+Helper.getEmpCode(mActivity)
        HMACHasherAndMatcher hmacHasherAndMatcher = new HMACHasherAndMatcher();
        try {
            String encrypted_otp = hmacHasherAndMatcher.computeHashedVerificationCode(otp, supplierNames);
            getDataManager().setAmazonOTPValue(otp);
            if (amazon_encrypted_otp.equalsIgnoreCase(encrypted_otp)) {
                getDataManager().setAmazonOTPStatus("true");
                getDataManager().setAmazonOTPTiming(Calendar.getInstance().getTimeInMillis());
                setAmazon();
                return true;
            } else {
                getDataManager().setAmazonOTPStatus("false");
                getDataManager().setAmazonOTPTiming(Calendar.getInstance().getTimeInMillis());
                setAmazon();
                return false;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setAmazon()
    {
        ForwardCommit.Amz_Scan amz_scan = new ForwardCommit.Amz_Scan();
        amz_scan.setAmz_otp_status(getDataManager().getAmazonOTPStatus());
        amz_scan.setAmz_otp_value(getDataManager().getAmazonOTPValue());
        amz_scan.setOtp_verified_time(getDataManager().getAmazonOTPTiming());

        amz_scanArrayList.add(amz_scan);
        Gson gson = new Gson();
        String amazon_lis = gson.toJson(amz_scanArrayList);
        getDataManager().setAmazonList(amazon_lis);
    }

    public boolean verifyDlightEncryptedOTP(String dlight_encrypted_otp, String otp) {
        int res = EncryptionModule.EncryptionToCompare(dlight_encrypted_otp, otp);
        if (res == 1) {
            getDataManager().setDlightSuccessEncrptedOTP(dlight_encrypted_otp);
            return true;
        } else {
            return false;
        }

    }

    //ON VERIFY BUTTON CLICK
    //making api call on verify button click
    public void onOtpOnVerifyApiCall(Activity context, String awb, String otp, String type, String shipmentType) {
        dialog = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
        dialog.setMessage("Verifying....");
        dialog.setCancelable(false);
        dialog.show();
        dialog.setIndeterminate(false);
        try {
            VerifyOtpRequest request = new VerifyOtpRequest(awb, otp, shipmentType, type ,"Secure");
            final long timeStamp = System.currentTimeMillis();
            writeRestAPIRequst(timeStamp, request);
            getCompositeDisposable().add(getDataManager()
                    .doVerifyOtpApiCall(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), request)
                    .doOnSuccess(new Consumer<VerifyOtpResponse>() {
                        @Override
                        public void accept(VerifyOtpResponse verifyOtpResponse) throws Exception {
                            writeRestAPIResponse(timeStamp, verifyOtpResponse);
                            Log.d(ContentValues.TAG, verifyOtpResponse.toString());
                        }
                    })
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(response -> {
                        setIsLoading(false);
                        try {
                            if (dialog.isShowing())
                                dialog.dismiss();
                            if (response.getStatus()) {
                                if(shipmentType.equalsIgnoreCase("RVP"))
                                {
                                    getDataManager().setRVPSecureOTPVerified("true");
                                }
                                getNavigator().onOtpVerifySuccess(type);
                            } else {
                                if (response.getResponse().getCode().equalsIgnoreCase("E137")) {
                                    getNavigator().getAlert();

                                } else
                                    getNavigator().onHandleError(response.getResponse().getDescription());

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            getNavigator().onHandleError(e.getMessage());
                        }
                    }, throwable -> {
                        if (dialog.isShowing())
                            dialog.dismiss();
                        String error;
                        try {
                            error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();

                            getNavigator().showerrorMessage(error);
                        } catch (Exception e) {
                            e.printStackTrace();
                            getNavigator().onHandleError(e.getMessage());
                        }
                    }));
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onHandleError(e.getMessage());
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }


    public void callAmazonEncyption() {
        getNavigator().callAmazon();
    }

    public void getAmazonEncryptedOTP(Activity context, String awb) {
        dialog = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
        dialog.setMessage("Sending OTP....");
        dialog.setCancelable(false);
        dialog.show();
        dialog.setIndeterminate(false);
        try {
            AmazonOtpRequest request = new AmazonOtpRequest(awb);
            final long timeStamp = System.currentTimeMillis();
            writeRestAPIRequst(timeStamp, request);
            getCompositeDisposable().add(getDataManager()
                    .doAmazonOtpApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(),request)
                    .doOnSuccess(new Consumer<AmazonOtpResponse>() {
                        @Override
                        public void accept(AmazonOtpResponse amazonOtpResponse) throws Exception {
                            try {
                                if (amazonOtpResponse.isStatus()) {
                                    try {
                                        getNavigator().sendEncryptedOtp(amazonOtpResponse.getOtp());
                                    }
                                    catch (Exception e)
                                    {e.printStackTrace();
                                        getNavigator().sendEncryptedOtp("");
                                    }
                                }
                                else {
                                    try {
                                        getNavigator().sendEncryptedOtp(amazonOtpResponse.getOtp());
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                        getNavigator().sendEncryptedOtp("");
                                    }
                                }
                            } catch (Exception e) {
                                getNavigator().sendEncryptedOtp("");
                                e.printStackTrace();
                            }


                        }
                    })
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(response -> {
                        setIsLoading(false);
                        try {
                            if (dialog.isShowing())
                                dialog.dismiss();

                        } catch (Exception e) {
                            e.printStackTrace();
                            getNavigator().onHandleError(e.getMessage());
                        }
                    }, throwable -> {
                        if (dialog.isShowing())
                            dialog.dismiss();
                        String error;
                        try {
                            error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();

                            getNavigator().sendEncryptedOtp("");
                        } catch (Exception e) {
                            e.printStackTrace();
                            getNavigator().onHandleError(e.getMessage());
                        }
                    }));
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onHandleError(e.getMessage());
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

    //ON RESEND BUTTON CLICK
    //making api call on resend button click
    public void OnResendApiCall(Activity context, String awb, boolean is_cash_collection, String mobile_number, String drs_id_num, Boolean alternateclick) {

        dialog = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
        if (manifestType.equalsIgnoreCase("EDS")) {
            dialog.setMessage("Sending Ref. No.....");
        } else {
            dialog.setMessage("Sending OTP....");
        }

        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();
        ResendOtpRequest request;
        if (is_cash_collection) {
            request = new ResendOtpRequest(awb, "EDS_CC",drs_id_num,alternateclick);
        } else {
            request = new ResendOtpRequest(awb, "OTP",drs_id_num,alternateclick);
        }

        final long timeStamp = System.currentTimeMillis();
        writeRestAPIRequst(timeStamp, request);
        try {

            getCompositeDisposable().add(getDataManager()
                    .doResendOtpApiCall(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), request)
                    .doOnSuccess(new Consumer<ResendOtpResponse>() {
                        @Override
                        public void accept(ResendOtpResponse resendOtpResponse) {
                            writeRestAPIResponse(timeStamp, resendOtpResponse);
                            Log.d(ContentValues.TAG, resendOtpResponse.toString());
                        }
                    })
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(response -> {
                        try {
                            if (dialog.isShowing())
                                dialog.dismiss();
                            if (response.getStatus().equals("true")) {
                                String newMobileNo = response.getResponse().getMobile();
                                if (manifestType.equalsIgnoreCase("EDS")) {
                                    itemMobileforOtp.set("Please Enter the Ref. No. resent on registered No. " + mobile_number);
                                } else {
                                    itemMobileforOtp.set("Please Enter the Otp resent on registered No. " + mobile_number);
                                }

                                getNavigator().onOtpResendSuccess();
                            } else {
                                getNavigator().onHandleError(response.getResponse().getDescription());

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            getNavigator().onHandleError(e.getMessage());
                        }
                    }, throwable -> {
                        if (dialog.isShowing())
                            dialog.dismiss();
                        String error;
                        try {
                            error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();

                            getNavigator().showerrorMessage(error);
                        } catch (Exception e) {
                            e.printStackTrace();
                            getNavigator().onHandleError(e.getMessage());
                        }
                    }));

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
            getCompositeDisposable().add(getDataManager()
                    .deleteAllTables().subscribeOn
                            (getSchedulerProvider().io()).
                            observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) {
                            try {
                                getDataManager().clearPrefrence();
                                getDataManager().setUserAsLoggedOut();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                getNavigator().onHandleError(ex.getMessage());
                            }
                            getNavigator().clearStack();

                        }
                    }));

        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onHandleError(e.getMessage());
        }
    }

    public void getShipmentType(String mshipmentType) {
        shipmentType = mshipmentType;
    }

    public RvpWithQC getRvpWithQc() {
        return rvpWithQC.get();
    }

    public void getRvpDataWithQc(String compositeKey) {
        try {

            getCompositeDisposable().add(getDataManager().
                    getRvpWithQc(compositeKey).
                    subscribeOn(getSchedulerProvider().io()).
                    observeOn(getSchedulerProvider().ui()).
                    subscribe(new Consumer<RvpWithQC>() {
                        @Override
                        public void accept(RvpWithQC rvpWithQC) throws Exception {
                            Log.e(TAG, "accept: " + rvpWithQC.toString());
                            SecureDeliveryViewModel.this.rvpWithQC.set(rvpWithQC);
                        }
                    }, throwable -> {
                        setIsLoading(false);
                        String error;
                        try {

                            error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                            Log.e("error", error);
                        } catch (Exception e) {
                            getNavigator().onHandleError(e.getMessage());


                        }
                    }));
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onHandleError(e.getMessage());
        }
    }

    BroadcastReceiver mResultReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ScanManager.ACTION_SEND_SCAN_RESULT.equals(action)) {
                    byte[] bvalue = intent.getByteArrayExtra(
                            ScanManager.EXTRA_SCAN_RESULT_ONE_BYTES);
                    String sValue = intent.getStringExtra("SCAN_BARCODE1");
                    getNavigator().mResultReceiver1(sValue);

                    try {
                        if (sValue == null && bvalue != null)
                            sValue = new String(bvalue, "GBK");
                        sValue = sValue == null ? "" : sValue;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        return mReceiver;
    }

    public void onsendOtpOnOtherNo(String phone, long awbNo) {
        setIsLoading(true);
        OtherNoRequest otherNoRequest = new OtherNoRequest(String.valueOf(awbNo), phone);
        final long timeStamp = Calendar.getInstance().getTimeInMillis();
        writeRestAPIRequst(timeStamp, otherNoRequest);
        try {
            getCompositeDisposable()
                    .add(getDataManager().doResendOtpToOtherNoApiCall(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), otherNoRequest)
                            .doOnSuccess(new Consumer<OtherNoResponse>() {
                                @Override
                                public void accept(OtherNoResponse otherNoResponse) {
                                    setIsLoading(false);
                                    writeRestAPIResponse(timeStamp, otherNoResponse);

                                }
                            }).subscribeOn(getSchedulerProvider().io())
                            .observeOn(getSchedulerProvider().ui())
                            .subscribe(new Consumer<OtherNoResponse>() {
                                @Override
                                public void accept(OtherNoResponse otherNoResponse) {
                                    try {
                                        if (otherNoResponse != null) {
                                            if (otherNoResponse.getStatus().equalsIgnoreCase("true")) {
                                                getNavigator().onOtherMobile(true);
                                                getNavigator().showerrorMessage(otherNoResponse.getResponse().getDescription());
                                            } else {
                                                getNavigator().showerrorMessage(otherNoResponse.getResponse().getDescription());
                                            }
                                        } else if (otherNoResponse.getResponse().getCode() == null) {
                                            getNavigator().showerrorMessage("Code value cannot be null.Contact Server Team");
                                        } else {
                                            if ((otherNoResponse.getResponse() != null) && (otherNoResponse.getResponse().getCode().equalsIgnoreCase("107"))) {

                                            }
                                        }
                                    } catch (Exception e) {
                                        setIsLoading(false);
                                        getNavigator().showerrorMessage(e.getMessage());
                                        e.printStackTrace();
                                    }
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    setIsLoading(false);
                                }
                            }));

        } catch (Exception e) {
            getNavigator().showerrorMessage(e.getMessage());
            e.printStackTrace();
        }
    }

    public void onCancel() {
        getNavigator().onOtherMobile(false);
    }

    public void onMobileNoChange() {
        getNavigator().onMobileNoChange();
    }

    public void clickOnOtherNumber() {
        getNavigator().clickOnOtherNumber();
    }

    public void getDisputedAwb(String awb) {
        try {
            getCompositeDisposable().add(getDataManager().
                    getdisputed(awb).
                    observeOn(getSchedulerProvider().ui()).
                    subscribeOn(getSchedulerProvider().io()).
                    subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String awb_result) {
                            if(awb_result.equalsIgnoreCase(awb)){
                                getNavigator().checkDisputedStatus(true);
                            } else{
                                getNavigator().checkDisputedStatus(false);
                            }
                        }
                    }));
        } catch (Exception e) {
            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
            restApiErrorHandler.writeErrorLogs(0, e.getMessage());
            e.printStackTrace();
        }
    }
}



