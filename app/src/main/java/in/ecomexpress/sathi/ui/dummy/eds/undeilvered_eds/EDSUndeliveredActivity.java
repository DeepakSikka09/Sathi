package in.ecomexpress.sathi.ui.dummy.eds.undeilvered_eds;

import static in.ecomexpress.sathi.utils.Constants.ConsigneeDirectAlternateMobileNo;
import static in.ecomexpress.sathi.utils.Constants.ConsigneeDirectMobileNo;
import static in.ecomexpress.sathi.utils.Constants.ContactNO;
import static in.ecomexpress.sathi.utils.Constants.eds_call_count;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityEdsUndeliveredBinding;
import in.ecomexpress.sathi.repo.local.data.eds.EDSActivityResponseWizard;
import in.ecomexpress.sathi.repo.local.data.eds.EdsCommit;
import in.ecomexpress.sathi.repo.local.data.fwd.FeRescheduleInfo;
import in.ecomexpress.sathi.repo.local.db.model.Remark;
import in.ecomexpress.sathi.repo.remote.model.ErrorResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;
import in.ecomexpress.sathi.repo.remote.model.masterdata.EDSReasonCodeMaster;
import in.ecomexpress.sathi.repo.remote.model.reschedule.ReshceduleDetailsResponse;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.dummy.eds.eds_success_fail.EDSSuccessFailActivity;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.Helper;
import in.ecomexpress.sathi.utils.ImageHandler;
import in.ecomexpress.sathi.utils.NetworkUtils;
import in.ecomexpress.sathi.utils.TimeUtils;

@AndroidEntryPoint
public class EDSUndeliveredActivity extends BaseActivity<ActivityEdsUndeliveredBinding, UndeliveredViewModel> implements IUndeliveredNavigator {

    String TAG = EDSUndeliveredActivity.class.getCanonicalName();
    Dialog dialog;
    @Inject
    UndeliveredViewModel undeliveredViewModel;
    @Inject
    EdsCommit edsCommit;
    HashSet<String> getAllTrueEdsAttribures = new HashSet<>();
    String groupName = Constants.SELECT;
    ActivityEdsUndeliveredBinding activityEdsUndeliveredBinding;
    long awb;
    ImageHandler imageHandler;
    EDSResponse edsResponseCommit;
    Boolean isImageCaptured = false;
    String navigator, composite_key = null;
    List<EDSActivityResponseWizard> edsActivityResponseWizards;
    boolean reschedule_enable;
    private int mYear;
    private int mMonth;
    private int mDay;
    // Blur Image Recognition Work:-
    public static int imageCaptureCount = 0;
    public static boolean isrescheduleFlag = false;
    private boolean consigneeProfiling = false, is_call_mandatory;
    boolean dateFlag = false, slotFlag = false;
    String slotId = "", dateSet = "";
    private EDSReasonCodeMaster edsReasonCodeMaster;
    String getDrsApiKey = null, getDrsPstnKey = null, getDrsPin = null;
    private EDSResponse edsResponse;
    private int meterRange;
    CountDownTimer mCountDownTimer = null;
    Long awbNo = null;
    ProgressDialog progress;
    ImageView imageView;
    Bitmap bitmap_server;
    int check_call_count;
    ReshceduleDetailsResponse reshceduleDetailsResponse;
    boolean call_allowed;
    boolean is_already_kyced_paytm = false;
    boolean uD_OTP = false;
    String OFD_OTP;
    Gson gson = new Gson();

    public static Intent getStartIntent(Context context){
        return new Intent(context, EDSUndeliveredActivity.class);
    }

    @Override
    protected void onStart(){
        super.onStart();
        try{
            if(!Constants.broad_call_type.isEmpty() && !Constants.broad_shipment_type.isEmpty()){
                String getCallType = Constants.broad_call_type;
                String getShipmentType = Constants.broad_shipment_type;
                if(getCallType.equalsIgnoreCase(Constants.call_awb)){
                    if(getShipmentType == Constants.EDS){
                        undeliveredViewModel.updateEDSCallAttempted(getCallType);
                    }
                } else if(getCallType.equalsIgnoreCase(Constants.call_pin)){
                    if(getShipmentType == Constants.EDS){
                        undeliveredViewModel.updateEDSpinCallAttempted(getCallType);
                    }
                }
                Constants.call_awb = "";
                Constants.call_pin = "";
                Constants.shipment_type = "";
                Constants.broad_call_type = "";
                Constants.broad_shipment_type = "";
                Constants.call_intent_number = "";
            }
        } catch(Exception e){
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        activityEdsUndeliveredBinding = getViewDataBinding();
        undeliveredViewModel.setNavigator(this);
        undeliveredViewModel.getDataManager().setLoginPermission(false);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        getDrsApiKey = getIntent().getExtras().getString(Constants.DRS_API_KEY);
        getDrsPstnKey = getIntent().getExtras().getString(Constants.DRS_PSTN_KEY);
        getDrsPin = getIntent().getExtras().getString(Constants.DRS_PIN);
        composite_key = getIntent().getExtras().getString(Constants.COMPOSITE_KEY);
        undeliveredViewModel.getEdsListTask(composite_key);
        if(getIntent() != null){
            is_already_kyced_paytm = getIntent().getBooleanExtra("is_already_kyced", false);
        }
        call_allowed = getIntent().getBooleanExtra("call_allowed", false);
        reschedule_enable = getIntent().getBooleanExtra(Constants.RESCHEDULE_ENABLE, false);
        Constants.LOCATION_ACCURACY = undeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.eds));
        }

        edsActivityResponseWizards = getIntent().getParcelableArrayListExtra("data");
        navigator = getIntent().getStringExtra("navigator");
        awb = getIntent().getExtras().getLong("awb", 0);
        edsResponseCommit = gson.fromJson(getIntent().getStringExtra("edsResponse"), EDSResponse.class);
        edsResponseCommit.setCompositeKey(composite_key);
        try{
            undeliveredViewModel.setedsCommit(edsResponseCommit);
            getAllTrueEdsAttribures = undeliveredViewModel.getEdsTrueAttributes(is_already_kyced_paytm);
            undeliveredViewModel.getListDetailNew(getAllTrueEdsAttribures);
            undeliveredViewModel.setAwb(awb);
            activityEdsUndeliveredBinding.awbTv.setText(String.valueOf(awb));
            undeliveredViewModel.fetchEDSShipment(edsResponseCommit.getCompositeKey());
            undeliveredViewModel.getCallStatus(edsResponseCommit.getAwbNo(), edsResponseCommit.getDrsNo());
            activityEdsUndeliveredBinding.tvNumberStatement.setText("We have send OTP on registered mobile number: " + ContactNO);
            imageHandler = new ImageHandler(this) {
                @Override
                public void onBitmapReceived(final Bitmap bitmap, final String imageUri, ImageView imageview, String imageName, String imageCode, int pos, boolean verifyImage){
                    // Blur Image Recognition Using Laplacian Variance:-
                    runOnUiThread(() -> {
                        if(CommonUtils.checkImageIsBlurryOrNot(EDSUndeliveredActivity.this, "EDS", bitmap, imageCaptureCount, undeliveredViewModel.getDataManager())){
                            imageCaptureCount++;
                        }
                        else{
                            if(imageview != null)
                                imageView = imageview;
                            bitmap_server = bitmap;
                            isImageCaptured = true;
                            if(undeliveredViewModel.getDataManager().getEDSRealTimeSync().equalsIgnoreCase("true") && NetworkUtils.isNetworkConnected(EDSUndeliveredActivity.this)){
                                undeliveredViewModel.uploadAWSImage(imageUri, GlobalConstant.ShipmentTypeConstants.OTHER, awb + "_" + edsResponseCommit.getDrsNo() + "_UndeliveredImage.png", imageCode, awb, edsResponseCommit.getDrsNo(), bitmap, false);
                            } else{
                                undeliveredViewModel.saveImageDB(imageUri, imageCode, imageName);
                                imageView.setImageBitmap(bitmap);
                            }
                        }
                    });
                }
            };
            undeliveredViewModel.getConsigneeProfiling();
            activityEdsUndeliveredBinding.remarksEdt.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
            activityEdsUndeliveredBinding.remarks.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
        } catch(Exception e){
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
        activityEdsUndeliveredBinding.scrollView.setFillViewport(true);
    }

    @Override
    public UndeliveredViewModel getViewModel(){
        return undeliveredViewModel;
    }

    @Override
    public int getBindingVariable(){
        return BR.viewModel;
    }

    @Override
    public int getLayoutId(){
        return R.layout.activity_eds_undelivered;
    }

    @Override
    protected void onResume(){
        super.onResume();
        dateFlag = false;
        slotFlag = false;
        slotId = "";
        activityEdsUndeliveredBinding.remarksEdt.setText("");

        if(undeliveredViewModel.consigneeContactNumber.get() == null){
            undeliveredViewModel.consigneeContactNumber.set("");
        }
        if(!Objects.requireNonNull(undeliveredViewModel.consigneeContactNumber.get()).equalsIgnoreCase("")){
            CommonUtils.deleteNumberFromCallLogsAsync(undeliveredViewModel.consigneeContactNumber.get(), EDSUndeliveredActivity.this);
        }

        try{
            int isCall = undeliveredViewModel.getisCallattempted(edsResponseCommit.getAwbNo().toString());
            if((isCall != 0)){
                dateSet = "";
                if(isrescheduleFlag && reschedule_enable){
                    activityEdsUndeliveredBinding.rescheduleLayoutLayout.setVisibility(View.VISIBLE);
                    //undeliveredViewModel.getShipmentRescheduleDetails(String.valueOf(awb));
                } else{
                    activityEdsUndeliveredBinding.rescheduleLayoutLayout.setVisibility(View.GONE);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
        if(undeliveredViewModel.ud_otp_verified_status.get()){
            activityEdsUndeliveredBinding.imgVerifiedTick.setVisibility(View.VISIBLE);
        } else{
            activityEdsUndeliveredBinding.imgVerifiedTick.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        imageHandler.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed(){
        if(shouldAllowBack()){
            super.onBackPressed();
        } else{
            showSnackbar("BackButton is disabled until the timer is off");
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    public void showStatusDialog(String textstatus){
        Dialog dialog = new Dialog(this, R.style.RoundedCornersDialog);
        dialog.setContentView(R.layout.dialog_show_status);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Button closeok = dialog.findViewById(R.id.closedok);
        TextView txtstatus = dialog.findViewById(R.id.txtstatus);
        txtstatus.setText(textstatus);
        closeok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void onChooseReasonSpinner(EDSReasonCodeMaster edsReasonCodeMaster, boolean rescheduleFlag, boolean callFlag){
        try{
            if(is_call_mandatory){
                is_call_mandatory = Constants.CONSIGNEE_PROFILE || meterRange >= undeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE();
            } else{
                is_call_mandatory = false;
            }
            activityEdsUndeliveredBinding.otpSkip.setChecked(false);
            undeliveredViewModel.ud_otp_commit_status = "NONE";
            undeliveredViewModel.ud_otp_commit_status_field.set("NONE");
            undeliveredViewModel.rd_otp_commit_status = "NONE";
            undeliveredViewModel.rd_otp_commit_status_field.set("NONE");
            this.edsReasonCodeMaster = edsReasonCodeMaster;
            groupName = edsReasonCodeMaster.getReasonMessage();
            isrescheduleFlag = rescheduleFlag;
            dateSet = "";
            slotId = "";
            activityEdsUndeliveredBinding.remarksEdt.setText("");
            if(isrescheduleFlag && reschedule_enable){
                activityEdsUndeliveredBinding.rescheduleLayoutLayout.setVisibility(View.VISIBLE);
                return;
                // undeliveredViewModel.getShipmentRescheduleDetails(String.valueOf(awb));
            } else{
                activityEdsUndeliveredBinding.rescheduleLayoutLayout.setVisibility(View.GONE);
            }
            if(edsReasonCodeMaster.getReasonCode().equals(-1)){
                activityEdsUndeliveredBinding.rescheduleLayoutLayout.setVisibility(View.GONE);
            } else if(undeliveredViewModel.getDataManager().isCounterDelivery() && undeliveredViewModel.getCounterDeliveryRange() < undeliveredViewModel.getDataManager().getDCRANGE()){
            } else if(callFlag){
                int isCall = undeliveredViewModel.getisCallattempted(edsResponseCommit.getAwbNo().toString());
                if(isCall == 0 && undeliveredViewModel.getDataManager().getCallClicked(awbNo+"EDSCall")){
                    makeCallDialog();
                } else{
                }
            } else{
                activityEdsUndeliveredBinding.rescheduleLayoutLayout.setVisibility(View.GONE);
            }
        } catch(Exception e){
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    public void makeCallDialog(){
        if(!call_allowed){
            // undeliver();
        } else{
            dialog = new BottomSheetDialog(EDSUndeliveredActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.activity_undelivered_call_dialog);
            TextView name = dialog.findViewById(R.id.name);
            name.setText("Name : " + edsResponseCommit.getShipmentDetail().getCustomerName());
            TextView awb = dialog.findViewById(R.id.awb);
            awb.setText("AWB : " + edsResponseCommit.getAwbNo());
            ImageView dialogButton = dialog.findViewById(R.id.call);
            // if button is clicked, close the custom dialog
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    undeliveredViewModel.getDataManager().setCallClicked(edsCommit.getAwb()+"EDSCall", false);
                    if(edsResponse.getShipmentDetail().getFlag().getFlagMap().getIs_callbridge_enabled().equalsIgnoreCase("true")
                            && edsResponse.getCallbridge_details()!=null) {

                        makeCallonClick();
                    }
                    else
                    {
                        if(!TextUtils.isEmpty(ConsigneeDirectAlternateMobileNo) && ConsigneeDirectAlternateMobileNo != null && ConsigneeDirectAlternateMobileNo != "0"){
                            showDirectCallDialog();
                        } else {
                            undeliveredViewModel.consigneeContactNumber.set(edsResponse.getConsigneeDetail().getMobile());
                            eds_call_count = eds_call_count + 1;
                            undeliveredViewModel.getDataManager().setEDSCallCount(awb + "EDS", eds_call_count);
                            CommonUtils.startCallIntent(edsResponse.getConsigneeDetail().getMobile(), getActivityContext(), EDSUndeliveredActivity.this);
                        }
                    }

                }
            });
            dialog.show();
            //undeliveredViewModel.callBridgeCheckStatusApi(edsResponse.getShipmentDetail().getFlag().getFlagMap().getIs_callbridge_enabled() , String.valueOf(awb), composite_key.replaceAll(String.valueOf(awb), ""));
        }
    }

    @Override
    public void doLogout(String description){
        showToast(getString(R.string.session_expire));
        undeliveredViewModel.logoutLocal();
    }

    @Override
    public void clearStack(){
        Intent intent = new Intent(EDSUndeliveredActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onEDSItemFetched(EDSResponse edsResponse){
        this.edsResponse = edsResponse;
        undeliveredViewModel.checkMeterRange(edsResponse);
    }

    @Override
    public void setConsigneeDistance(int meter){
        this.meterRange = meter;
    }

    @Override
    public void setConsingeeProfiling(boolean enable){
        this.consigneeProfiling = enable;
    }

    @Override
    public void showProgress(){
        progress = new ProgressDialog(this,android.R.style.Theme_Material_Light_Dialog);
        progress.setMessage("We are Sending this image to server. Please wait for moment...");
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    public void setBitmap(){
        imageView.setImageBitmap(bitmap_server);
    }

    @Override
    public void onProgressFinishCall(){
        progress.dismiss();
    }

    @Override
    public void callNotAttemptedDialog(){
        showDialog_CallNotAttempted();
    }

    private void showDialog_CallNotAttempted(){
        AlertDialog.Builder builder = new AlertDialog.Builder(EDSUndeliveredActivity.this,R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
        String AlertText1 = "Attention : ";
        builder.setMessage(AlertText1 + getString(R.string.undelivered_shipment_msg)).setCancelable(false).setPositiveButton("Call", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                makeCallDialog();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void makeCallonClick(){
        /*Check 1.1 (Check for PSTN)*/
        /*if(getDrsPstnKey != null){
            Constants.call_awb = String.valueOf(edsResponseCommit.getAwbNo());
            Constants.shipment_type = Constants.EDS;
            undeliveredViewModel.saveCallStatus(edsResponseCommit.getAwbNo(), edsResponseCommit.getDrsNo());
            if(undeliveredViewModel.getDataManager().getIsCallBridgeCheckStatus().equalsIgnoreCase("true")){
                Constants.IS_CALL_BRIDGE_FLAG_ON_STATUS = true;
            } else{
                Constants.IS_CALL_BRIDGE_FLAG_ON_STATUS = false;
            }

            if(edsResponse.getShipmentDetail().getFlag().getFlagMap().getIs_callbridge_enabled().equalsIgnoreCase("true") && !undeliveredViewModel.getDataManager().getENABLEDIRECTDIAL().equalsIgnoreCase("true"))

            {
                undeliveredViewModel.consigneeContactNumber.set(getDrsPstnKey);
                CommonUtils.startCallIntent(getDrsPstnKey, this, EDSUndeliveredActivity.this);
            }
             else
            {
                if(!TextUtils.isEmpty(ConsigneeDirectAlternateMobileNo) && ConsigneeDirectAlternateMobileNo != null && ConsigneeDirectAlternateMobileNo != "0"){
                    showDirectCallDialog();
                } else{
                    undeliveredViewModel.consigneeContactNumber.set(ConsigneeDirectMobileNo);
                    undeliveredViewModel.getDataManager().setEDSCallCount(awb+"EDS" ,Constants.eds_call_count);
                    Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ConsigneeDirectMobileNo));
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                }
            }

        }

        *//*Check 1.2 (Check for API)*//*
        else if(getDrsApiKey != null){
            undeliveredViewModel.makeCallBridgeApiCall(getDrsApiKey, edsResponseCommit.getAwbNo().toString(), String.valueOf(edsResponseCommit.getDrsNo()), Constants.EDS);
        }*/

        /*Check 2 (Check for Calling method in Master Data)*/
       // else if(getDrsApiKey == null && getDrsPstnKey == null){
            try{
              /*  if(undeliveredViewModel.getDataManager().getPSTNType().equalsIgnoreCase("")){
                    getCbConfigCallType = "PSTN";
                } else{
                    getCbConfigCallType = undeliveredViewModel.getDataManager().getPSTNType();
                }*/
             //   if(getCbConfigCallType != null){
                if(edsResponse.getShipmentDetail().getFlag().getFlagMap().getIs_callbridge_enabled().equalsIgnoreCase("true")
                        && edsResponse.getCallbridge_details()!=null)
                {

                       // Masterpstnformat = undeliveredViewModel.getDataManager().getPstnFormat();
                        String callingformat = null;
                        //01241234567,@@AWB@@#
                      /*  if(Masterpstnformat.contains(this.getString(R.string.patn_awb))){
                            callingformat = Masterpstnformat.replaceAll(this.getString(R.string.patn_awb), edsResponseCommit.getAwbNo().toString());
                            Constants.call_awb = edsResponseCommit.getAwbNo().toString();
                            Constants.shipment_type = Constants.EDS;
                        } else if(Masterpstnformat.contains(this.getString(R.string.pstn_pin))){*/
                            //callingformat = Masterpstnformat.replaceAll(this.getString(R.string.pstn_pin), getDrsPin);
                            //Constants.call_pin = getDrsPin;
                            callingformat = edsResponse.getCallbridge_details().get(0).getCallbridge_number()+","+edsResponse.getCallbridge_details().get(0).getPin().substring(0, 4)+","+ edsResponse.getCallbridge_details().get(0).getPin().substring(4)+"#";
                            Constants.call_pin = String.valueOf(edsResponse.getCallbridge_details().get(0).getPin());
                            Constants.calling_format = callingformat;
                            Constants.shipment_type = Constants.EDS;
                               // }
                        if(callingformat != null){
                            undeliveredViewModel.saveCallStatus(edsResponseCommit.getAwbNo(), edsResponseCommit.getDrsNo());
                            if(undeliveredViewModel.getDataManager().getIsCallBridgeCheckStatus().equalsIgnoreCase("true")){
                                Constants.IS_CALL_BRIDGE_FLAG_ON_STATUS = true;
                            } else{
                                Constants.IS_CALL_BRIDGE_FLAG_ON_STATUS = false;
                            }
                            if(edsResponse.getShipmentDetail().getFlag().getFlagMap().getIs_callbridge_enabled().equalsIgnoreCase("true")
                                    && edsResponse.getCallbridge_details()!=null)
                            {
                                if (edsResponse.getCallbridge_details().size()>1)
                                {
                                    showCallBridgeDialog();
                                }
                                else
                                {
                                    undeliveredViewModel.consigneeContactNumber.set(callingformat);
                                    CommonUtils.startCallIntent(callingformat, this, EDSUndeliveredActivity.this);
                                }

                            }
                            else
                            {
                                if(!TextUtils.isEmpty(ConsigneeDirectAlternateMobileNo) && ConsigneeDirectAlternateMobileNo != null && ConsigneeDirectAlternateMobileNo != "0"){
                                    showDirectCallDialog();
                                } else{
                                    undeliveredViewModel.consigneeContactNumber.set(ConsigneeDirectMobileNo);
                                    eds_call_count = eds_call_count + 1;
                                    undeliveredViewModel.getDataManager().setEDSCallCount(awb+"EDS" ,eds_call_count);
                                    Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ConsigneeDirectMobileNo));
                                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent1);
                                }
                            }
                            //                            undeliveredViewModel.updateEDSCallAttempted(edsCommit.getAwb());
                        }
                    }

                    /*Check 2.2 (Check for API)*/
                   /* if(getCbConfigCallType.equalsIgnoreCase("API")){
                        //                    Toast.makeText(this, "Fwd3 : Call MASTER API ", Toast.LENGTH_SHORT).show();
                        //call getDrsApiKey
                        if(isNetworkConnected())
                            undeliveredViewModel.makeCallBridgeApiCall("API", edsResponseCommit.getAwbNo().toString(), String.valueOf(edsResponseCommit.getDrsNo()), Constants.EDS);
                        else{
                            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                        }
                    }*/
             //   }
            } catch(Exception e){
                e.printStackTrace();
                showSnackbar(e.getMessage());
            }
       // }
        if(dialog.isShowing()){
            dialog.dismiss();
        }
    }

    @Override
    public void onChooseSlotSpinner(String slotid){
        if(!slotid.equalsIgnoreCase("-1")){
            slotFlag = true;
            slotId = slotid;
        }
    }

    @Override
    public void onCaptureImage(){
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
            String AlertText1 = "Attention : ";
            builder.setMessage(AlertText1 + getString(R.string.alert)).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id){
                    //do things
                    imageHandler.captureImage(awb + "_" + edsResponseCommit.getDrsNo() + "_UndeliveredImage.png", activityEdsUndeliveredBinding.imagecam, "UndeliveredImage");
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } catch(Exception e){
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void OnSubmitClick(){
        boolean failFlag = false;
        eds_call_count=0;
        undeliveredViewModel.isCallRecursionDailogRunning = true;
        undeliveredViewModel.isStopRecursion = false;
        undeliveredViewModel.call_alert_number = 0;
        edsCommit.setCall_attempt_count(undeliveredViewModel.getDataManager().getEDSCallCount(awb+"EDS"));
        edsCommit.setMap_activity_count(undeliveredViewModel.getDataManager().getEDSMapCount(awb));
        edsCommit.setTrying_reach(String.valueOf(undeliveredViewModel.getDataManager().getTryReachingCount(String.valueOf(awb+Constants.TRY_RECHING_COUNT))));
        edsCommit.setTechpark(String.valueOf(undeliveredViewModel.getDataManager().getSendSmsCount(String.valueOf(awb+Constants.TECH_PARK_COUNT))));
        try{
            if(this.edsReasonCodeMaster == null || this.edsReasonCodeMaster.getReasonCode() == -1 || groupName.equalsIgnoreCase(Constants.SELECT)){
                showSnackbar(getString(R.string.select_reason_code));
                failFlag = true;
                return;
            }
            if(undeliveredViewModel.ud_otp_commit_status.equalsIgnoreCase("none") && uD_OTP){
                showSnackbar("Please Verify OTP");
                return;
            }
            if(!undeliveredViewModel.ud_otp_commit_status.equalsIgnoreCase("VERIFIED") && edsReasonCodeMaster.getEdsMasterDataAttributeResponse().cALLM && undeliveredViewModel.getDataManager().getCallClicked(edsCommit.getAwb()+"EDSCall") && Boolean.FALSE.equals(undeliveredViewModel.ud_otp_verified_status.get())) {
                makeCallDialog();
                return;
            }
            if(undeliveredViewModel.getDataManager().getSMSThroughWhatsapp() && edsReasonCodeMaster.getEdsMasterDataAttributeResponse().isWHATSAPP_MAND() && undeliveredViewModel.getDataManager().getTryReachingCount(edsCommit.getAwb()+Constants.TRY_RECHING_COUNT) == 0){
                String template = CommonUtils.getWhatsAppRemarkTemplate(undeliveredViewModel.getDataManager().getName(), undeliveredViewModel.getDataManager().getMobile(), String.valueOf(awb), edsResponse.getShipmentDetail().getCustomerName());
                showWhatsAppDialog(template, edsReasonCodeMaster.getEdsMasterDataAttributeResponse().isrCHD(), edsReasonCodeMaster.getEdsMasterDataAttributeResponse().iscALLM());
                return;
            }

           // if(Constants.IS_CPV_ACTIVITY_EXITS){
                if(edsReasonCodeMaster.getEdsMasterDataAttributeResponse().isEDS_IMAGE() && !isImageCaptured){
                    Toast.makeText(getApplicationContext(), getString(R.string.capture_image), Toast.LENGTH_SHORT).show();
                    failFlag = true;
                    //  showSnackbar(getString(R.string.capture_image));
                    return;
                }
           // }
            if(activityEdsUndeliveredBinding.rescheduleLayoutLayout.getVisibility() == View.VISIBLE){
                showSnackbar(getString(R.string.reschedule_statment));
                failFlag = true;
                return;
            } else if(Constants.shipment_undelivered_count >= undeliveredViewModel.getDataManager().getUndeliverCount()){
                if(undeliveredViewModel.getDataManager().getCallClicked(edsCommit.getAwb()+"EDSCall") && Boolean.FALSE.equals(undeliveredViewModel.ud_otp_verified_status.get())){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
                    builder.setMessage("Three or more consecutive shipments cannot be marked as UD without a call. Please call consignee.");
                    builder.setPositiveButton("CALL", (dialog, which) -> {
                        makeCallDialog();
                        dialog.dismiss();
                    });
                    Dialog dialog = builder.create();
                    dialog.show();
                }
                else{
                    if(undeliveredViewModel.getDataManager().getDcUndeliverStatus()){
                        if(undeliveredViewModel.getCounterDeliveryRange() < undeliveredViewModel.getDataManager().getDCRANGE()){
                            showError("Shipment cannot be marked undelivered within the DC");
                        } else{
                            markDeliveredOrFail(failFlag);
                        }
                    } else{
                        markDeliveredOrFail(failFlag);
                    }
                }
            } else{
                if(undeliveredViewModel.getDataManager().getDcUndeliverStatus()){
                    if(undeliveredViewModel.getCounterDeliveryRange() < undeliveredViewModel.getDataManager().getDCRANGE()){
                        showError("Shipment cannot be marked undelivered within the DC");
                    } else{
                        markDeliveredOrFail(failFlag);
                    }
                } else{
                    markDeliveredOrFail(failFlag);
                }
            }

        } catch(Exception e){
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    void markDeliveredOrFail(boolean failflag){
        int current_time = Integer.parseInt(new SimpleDateFormat("HH").format(new Date()));
        if(Constants.CONSIGNEE_PROFILE && meterRange < undeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE()){
            undeliver();
        } else if(undeliveredViewModel.FEInDCZone(undeliveredViewModel.getDataManager().getCurrentLatitude(), undeliveredViewModel.getDataManager().getCurrentLongitude(), undeliveredViewModel.getDataManager().getDCLatitude(), undeliveredViewModel.getDataManager().getDCLongitude())){
            if(!undeliveredViewModel.getDataManager().getDirectUndeliver()){
                undeliver();
            } else{
                if(call_allowed && !undeliveredViewModel.ud_otp_commit_status.equalsIgnoreCase("VERIFIED")){
                    undeliveredViewModel.callApi(edsResponse.getShipmentDetail().getFlag().getFlagMap().getIs_callbridge_enabled() , String.valueOf(awb), composite_key.replaceAll(String.valueOf(awb), ""));
                } else{
                    undeliver();
                }
            }
        } else if(current_time >= 21 || current_time <= 6){
            if(!undeliveredViewModel.getDataManager().getDirectUndeliver()){
                undeliver();
            } else{
                if(call_allowed && !undeliveredViewModel.ud_otp_commit_status.equalsIgnoreCase("VERIFIED")){
                    undeliveredViewModel.callApi(edsResponse.getShipmentDetail().getFlag().getFlagMap().getIs_callbridge_enabled() ,String.valueOf(awb), composite_key.replaceAll(String.valueOf(awb), ""));
                } else{
                    undeliver();
                }
            }
        } else if((TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis()) - TimeUnit.MILLISECONDS.toMinutes(undeliveredViewModel.getDataManager().getDRSTimeStamp())) <= 7){
            if(!undeliveredViewModel.getDataManager().getDirectUndeliver()){
                undeliver();
            } else{
                if(call_allowed && !undeliveredViewModel.ud_otp_commit_status.equalsIgnoreCase("VERIFIED")){
                    undeliveredViewModel.callApi(edsResponse.getShipmentDetail().getFlag().getFlagMap().getIs_callbridge_enabled() ,String.valueOf(awb), composite_key.replaceAll(String.valueOf(awb), ""));
                } else{
                    undeliver();
                }
            }
        } else{
            if(is_call_mandatory){
                if(!undeliveredViewModel.getDataManager().getDirectUndeliver()){
                    undeliver();
                } else{
                    if(call_allowed && !undeliveredViewModel.ud_otp_commit_status.equalsIgnoreCase("VERIFIED")){
                        undeliveredViewModel.callApi(edsResponse.getShipmentDetail().getFlag().getFlagMap().getIs_callbridge_enabled() , String.valueOf(awb), composite_key.replaceAll(String.valueOf(awb), ""));
                    } else{
                        undeliver();
                    }
                }
            } else{
                undeliver();
            }
        }
    }

    private void undeliver(){
        try{
            if(Constants.uD_OTP_API_CHECK && undeliveredViewModel.ud_otp_commit_status_field.get().equalsIgnoreCase("VERIFIED")){
                edsCommit.setUd_otp(undeliveredViewModel.ud_otp_commit_status_field.get());
                edsCommit.setRd_otp("NONE");
            } else if(Constants.rD_OTP_API_CHECK && undeliveredViewModel.ud_otp_commit_status_field.get().equalsIgnoreCase("VERIFIED")){
                edsCommit.setRd_otp("VERIFIED");
                edsCommit.setUd_otp("NONE");
            } else{
                edsCommit.setUd_otp(undeliveredViewModel.ud_otp_commit_status_field.get());
                edsCommit.setRd_otp(undeliveredViewModel.rd_otp_commit_status_field.get());
            }
            Calendar calendar = Calendar.getInstance();
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
            int mMonth = calendar.get(Calendar.MONTH) + 1;
            if(undeliveredViewModel.loginDate().equalsIgnoreCase(String.valueOf(mDay)) && undeliveredViewModel.getDataManager().getLoginMonth() == mMonth){
                try{
                    // start
                    String dialog_message = getString(R.string.commitdialog);
                    String positiveButtonText = getString(R.string.yes);
                    if(Constants.CONSIGNEE_PROFILE && meterRange > undeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE() && undeliveredViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("W")){
                        dialog_message = "You are not attempting the shipment at Consignee’s location. Your current location = " + undeliveredViewModel.getDataManager().getCurrentLatitude() + ", " + undeliveredViewModel.getDataManager().getCurrentLongitude() + " You are " + meterRange + " meter away from consignee location. \nAre you sure you want to commit?";
                        //                        dialog_message = getString(R.string.commitdialog_meter_away);
                        positiveButtonText = getString(R.string.yes);
                    } else if(Constants.CONSIGNEE_PROFILE && meterRange > undeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE() && undeliveredViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("R")){
                        dialog_message = "You are not allowed to commit this shipment as you are not attempting at consignee location. your current location = " + undeliveredViewModel.getDataManager().getCurrentLatitude() + ", " + undeliveredViewModel.getDataManager().getCurrentLongitude() + "You are " + meterRange + " meter away from consignee location";
                        //dialog_message = getString(R.string.commitdialog_meter_away_mandatory);
                        positiveButtonText = getString(R.string.ok);
                    } else if(Constants.CONSIGNEE_PROFILE && meterRange > undeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE()){
                        FeRescheduleInfo feRescheduleInfo = new FeRescheduleInfo();
                        feRescheduleInfo.setRescheduleDate(dateSet);
                        feRescheduleInfo.setRescheduleSlot(slotId);
                        feRescheduleInfo.setRescheduleFeComments(activityEdsUndeliveredBinding.remarksEdt.getText().toString());
                        if(undeliveredViewModel.getDataManager().getEDSRealTimeSync().equalsIgnoreCase("true") && NetworkUtils.isNetworkConnected(EDSUndeliveredActivity.this)){
                            undeliveredViewModel.createCommitPacket(edsReasonCodeMaster.getReasonCode().toString(), feRescheduleInfo, edsCommit, edsResponseCommit, edsActivityResponseWizards, false);
                        } else{
                            undeliveredViewModel.createCommitPacket(edsReasonCodeMaster.getReasonCode().toString(), feRescheduleInfo, edsCommit, edsResponseCommit, edsActivityResponseWizards, false);
                        }
                        return;
                    } else if(Constants.CONSIGNEE_PROFILE){
                        FeRescheduleInfo feRescheduleInfo = new FeRescheduleInfo();
                        feRescheduleInfo.setRescheduleDate(dateSet);
                        feRescheduleInfo.setRescheduleSlot(slotId);
                        feRescheduleInfo.setRescheduleFeComments(activityEdsUndeliveredBinding.remarksEdt.getText().toString());
                        if(undeliveredViewModel.getDataManager().getEDSRealTimeSync().equalsIgnoreCase("true") && NetworkUtils.isNetworkConnected(EDSUndeliveredActivity.this)){
                            undeliveredViewModel.createCommitPacket(edsReasonCodeMaster.getReasonCode().toString(), feRescheduleInfo, edsCommit, edsResponseCommit, edsActivityResponseWizards, false);
                        } else{
                            undeliveredViewModel.createCommitPacket(edsReasonCodeMaster.getReasonCode().toString(), feRescheduleInfo, edsCommit, edsResponseCommit, edsActivityResponseWizards, false);
                        }
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
                    builder.setCancelable(true);
                    builder.setMessage(dialog_message);
                    if(meterRange > undeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE()){
                        edsCommit.setLocation_verified(false);
                    } else{
                        edsCommit.setLocation_verified(true);
                    }
                    builder.setPositiveButton(positiveButtonText, (dialog, which) -> {
                        //if user pressed "yes", then he is allowed to exit from application
                        if(undeliveredViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("R") && meterRange > undeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE()){
                            //showError(getString(R.string.commitdialog_meter_away));
                            dialog.dismiss();
                            return;
                        } else if(consigneeProfiling && meterRange > undeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE() && undeliveredViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("W")){
                            dialog.dismiss();
                           // return;
                        }
                        FeRescheduleInfo feRescheduleInfo = new FeRescheduleInfo();
                        feRescheduleInfo.setRescheduleDate(dateSet);
                        feRescheduleInfo.setRescheduleSlot(slotId);
                        feRescheduleInfo.setRescheduleFeComments(activityEdsUndeliveredBinding.remarksEdt.getText().toString());
                        if(undeliveredViewModel.getDataManager().getEDSRealTimeSync().equalsIgnoreCase("true") && NetworkUtils.isNetworkConnected(EDSUndeliveredActivity.this)){
                            undeliveredViewModel.createCommitPacket(edsReasonCodeMaster.getReasonCode().toString(), feRescheduleInfo, edsCommit, edsResponseCommit, edsActivityResponseWizards, false);
                        } else{
                            undeliveredViewModel.createCommitPacket(edsReasonCodeMaster.getReasonCode().toString(), feRescheduleInfo, edsCommit, edsResponseCommit, edsActivityResponseWizards, false);
                        }
                        if(!(consigneeProfiling && meterRange > undeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE())){
                            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    dialog.cancel();
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    return;
                } catch(Exception e){
                    e.printStackTrace();
                }
            } else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
                String AlertText1 = "Attention : ";
                builder.setMessage(AlertText1 + getString(R.string.commit_restriction_msg)).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void captureDate(){
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        try{
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,R.style.Theme_AppCompat_Light_Dialog, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
                    Calendar calendar = Calendar.getInstance();
                    int dayOfWeek = calendar.get(Calendar.DATE);
                    if(dayOfWeek == dayOfMonth){
                        showInfo("Cannot Select Today Date for Re-schedule");
                    } else{
                        activityEdsUndeliveredBinding.dateBtn.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        dateFlag = true;
                        if(monthOfYear > 0 && monthOfYear < 10){
                            dateSet = dayOfMonth + "-" + "0" + (monthOfYear + 1) + "-" + year;
                        } else
                            dateSet = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        Date date = null;
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        try{
                            date = sdf.parse(dateSet);
                            dateSet = String.valueOf(date.getTime());
                        } catch(ParseException e){
                            e.printStackTrace();
                        }
                    }
                }
            }, mYear, mMonth, mDay);
            //            int attemptTimes = (edsResponseCommit.getShipmentDetail().getRescheduleAttemptTimes());
            //            if (attemptTimes == 0) {
            //                attemptTimes = undeliveredViewModel.getAttemptTimes();
            //            }
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            // datePickerDialog.getDatePicker().setMaxDate(reshceduleDetailsResponse.getInscan_date() + (1000 * 60 * 60 * 24 * undeliveredViewModel.getDataManager().getRescheduleMaxDaysAllow()));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000 + (1000 * 60 * 60 * 24 * 10));
            datePickerDialog.show();
        } catch(Exception e){
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void openFail(){
        Helper.updateLocationWithData(EDSUndeliveredActivity.this, edsCommit.getAwb(), edsCommit.getStatus());
        Intent intent = EDSSuccessFailActivity.getStartIntent(this);
        intent.putExtra(Constants.INTENT_KEY, awb);
        intent.putExtra("edsResponseCommit", gson.toJson(edsResponseCommit));
        intent.putExtra(Constants.DECIDENEXT, Constants.UNDELIVERED);
        intent.putExtra("Reason", edsReasonCodeMaster.getReasonMessage());
       /* intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);*/
        startActivity(intent);
        // finish();
    }

    @Override
    public void onRescheduleClick(){
        if(!dateFlag){
            showSnackbar("Please Choose Date");
        } else if(!slotFlag){
            showSnackbar("Please Choose Slot");
        } else{
            activityEdsUndeliveredBinding.rescheduleLayoutLayout.setVisibility(View.GONE);
            isrescheduleFlag = false;
        }
        //            activityEdsUndeliveredBinding.flagIsRescheduled.setVisibility(View.GONE);
    }

    @Override
    public void onHandleError(ErrorResponse callApiResponse){
        showInfo(callApiResponse.getEResponse().getDescription());
    }

    @Override
    public void onBackClick(){
        if(shouldAllowBack()){
            super.onBackPressed();
        } else{
            showSnackbar("BackButton is disabled until the timer is off.");
        }
    }

    private boolean shouldAllowBack(){
        if(activityEdsUndeliveredBinding.resendOtpTv.getText().toString().equalsIgnoreCase("RESEND") || activityEdsUndeliveredBinding.resendOtpTv.getVisibility() == View.GONE){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void onChooseGroupSpinner(String groupName){
        this.groupName = groupName;
        try{
            activityEdsUndeliveredBinding.otpSkip.setChecked(false);
            undeliveredViewModel.ud_otp_commit_status = "NONE";
            undeliveredViewModel.ud_otp_commit_status_field.set("NONE");
            undeliveredViewModel.rd_otp_commit_status = "NONE";
            undeliveredViewModel.rd_otp_commit_status_field.set("NONE");
            if(groupName.equalsIgnoreCase(Constants.SELECT)){
                if(activityEdsUndeliveredBinding.rescheduleLayoutLayout.getVisibility() == View.VISIBLE){
                    activityEdsUndeliveredBinding.rescheduleLayoutLayout.setVisibility(View.GONE);
                }
                activityEdsUndeliveredBinding.mandateTv.setText("");
                if(isImageCaptured){
                    isImageCaptured = false;
                    activityEdsUndeliveredBinding.imagecam.setImageBitmap(null);
                    activityEdsUndeliveredBinding.imagecam.setImageResource(R.drawable.cam);
                }
                activityEdsUndeliveredBinding.childGroupLayout.setVisibility(View.GONE);
                //activityEdsUndeliveredBinding.edtUdOtp.setVisibility(View.GONE);
                activityEdsUndeliveredBinding.llUdOtp.setVisibility(View.GONE);
            } else
                activityEdsUndeliveredBinding.childGroupLayout.setVisibility(View.VISIBLE);
            activityEdsUndeliveredBinding.spinnerChildType.performClick();
        } catch(Exception e){
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void onChooseChildSpinner(EDSReasonCodeMaster edsReasonCodeMaster, boolean rescheduleFlag, boolean callFlag){
        this.edsReasonCodeMaster = edsReasonCodeMaster;
        String template = CommonUtils.getWhatsAppRemarkTemplate(undeliveredViewModel.getDataManager().getName(), undeliveredViewModel.getDataManager().getMobile(), String.valueOf(awb), edsResponse.getShipmentDetail().getCustomerName());
        if(undeliveredViewModel.getDataManager().getSMSThroughWhatsapp() && edsReasonCodeMaster.getEdsMasterDataAttributeResponse().isWHATSAPP_MAND() && undeliveredViewModel.getDataManager().getTryReachingCount(awb+Constants.TRY_RECHING_COUNT) == 0){
            showWhatsAppDialog(template, rescheduleFlag, callFlag);
        }
        else{
            executeOnChooseChildSpinner(edsReasonCodeMaster, rescheduleFlag, callFlag);
        }
    }

    private void showWhatsAppDialog(String template, boolean rescheduleFlag, boolean callFlag){
        CommonUtils.showDialogBoxForWhatsApp(getActivityContext())
                .setPositiveButton("OK", (dialog, id) -> {
                    try {
                        CommonUtils.sendSMSViaWhatsApp(this, this, edsResponse.getConsigneeDetail().getMobile(), template);
                        undeliveredViewModel.getDataManager().setTryReachingCount(edsResponseCommit.getAwbNo()+Constants.TRY_RECHING_COUNT,undeliveredViewModel.getDataManager().getTryReachingCount(edsResponseCommit.getAwbNo()+Constants.TRY_RECHING_COUNT)+1);
                        String  remarks = GlobalConstant.RemarksTypeConstants.CONSIGNEE_NOT_PICKING_CALL;
                        Remark remark = new Remark();
                        remark.remark = remarks;
                        remark.empCode = undeliveredViewModel.getDataManager().getEmp_code();
                        remark.awbNo = edsResponseCommit.getAwbNo();
                        remark.count = undeliveredViewModel.getDataManager().getTryReachingCount(edsResponseCommit.getAwbNo()+Constants.TRY_RECHING_COUNT);
                        remark.date = TimeUtils.getDateYearMonthMillies();
                        undeliveredViewModel.addRemarks(remark);

                        // Moving next after whatsapp click:-
                        executeOnChooseChildSpinner(edsReasonCodeMaster, rescheduleFlag, callFlag);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .setOnCancelListener(dialog -> executeOnChooseChildSpinner(edsReasonCodeMaster, rescheduleFlag, callFlag)).show();
    }

    public void executeOnChooseChildSpinner(EDSReasonCodeMaster edsReasonCodeMaster, boolean rescheduleFlag, boolean callFlag){
        this.edsReasonCodeMaster = edsReasonCodeMaster;
        isrescheduleFlag = rescheduleFlag;
        dateSet = "";
        slotId = "";
        activityEdsUndeliveredBinding.remarksEdt.setText("");
        activityEdsUndeliveredBinding.rescheduleLayoutLayout.setVisibility(View.GONE);
        activityEdsUndeliveredBinding.otpSkip.setChecked(false);
        undeliveredViewModel.ud_otp_commit_status = "NONE";
        undeliveredViewModel.ud_otp_commit_status_field.set("NONE");
        undeliveredViewModel.rd_otp_commit_status = "NONE";
        undeliveredViewModel.rd_otp_commit_status_field.set("NONE");
        if(edsReasonCodeMaster != null){
            try{
                if(!edsReasonCodeMaster.getReasonMessage().equalsIgnoreCase(Constants.SELECT)){
                    if(edsReasonCodeMaster.getEdsMasterDataAttributeResponse().isUD_OTP()){
                        otpLayout(true, "UD_OTP");
                        Constants.uD_OTP_API_CHECK = true;
                        Constants.rD_OTP_API_CHECK = false;
                    } else if(edsReasonCodeMaster.getEdsMasterDataAttributeResponse().isRCHD_OTP()){
                        otpLayout(true, "RCHD_OTP");
                        Constants.rD_OTP_API_CHECK = true;
                        Constants.uD_OTP_API_CHECK = false;
                    } else{
                        otpLayout(false, "");
                        Constants.rD_OTP_API_CHECK = false;
                        Constants.uD_OTP_API_CHECK = false;
                    }
                }
            } catch(Exception e){
                e.printStackTrace();
                showError(e.getMessage());
            }
            if(edsReasonCodeMaster.getEdsMasterDataAttributeResponse().cALLM){
                //  is_call_mandatory = true;
                is_call_mandatory = Constants.CONSIGNEE_PROFILE || meterRange >= undeliveredViewModel.getDataManager().getUndeliverConsigneeRANGE();
            } else{
                is_call_mandatory = false;
            }
            // if(Constants.IS_CPV_ACTIVITY_EXITS){
            if(edsReasonCodeMaster.getEdsMasterDataAttributeResponse().isEDS_IMAGE()){
                activityEdsUndeliveredBinding.mandateTv.setText("MDT");
                if(isImageCaptured){
                    isImageCaptured = false;
                    activityEdsUndeliveredBinding.imagecam.setImageBitmap(null);
                    activityEdsUndeliveredBinding.imagecam.setImageResource(R.drawable.cam);
                }
            } else{
                activityEdsUndeliveredBinding.mandateTv.setText("");
                if(isImageCaptured){
                    isImageCaptured = false;
                    activityEdsUndeliveredBinding.imagecam.setImageBitmap(null);
                    activityEdsUndeliveredBinding.imagecam.setImageResource(R.drawable.cam);
                }
            }
//            } else{
//                activityEdsUndeliveredBinding.mandateTv.setText("");
//                if(isImageCaptured){
//                    isImageCaptured = false;
//                    activityEdsUndeliveredBinding.imagecam.setImageBitmap(null);
//                    activityEdsUndeliveredBinding.imagecam.setImageResource(R.drawable.cam);
//                }
//            }
        }
        if(edsReasonCodeMaster.getReasonCode().equals(-1)){
            activityEdsUndeliveredBinding.rescheduleLayoutLayout.setVisibility(View.GONE);
            activityEdsUndeliveredBinding.mandateTv.setText("");
            if(isImageCaptured){
                isImageCaptured = false;
                activityEdsUndeliveredBinding.imagecam.setImageBitmap(null);
                activityEdsUndeliveredBinding.imagecam.setImageResource(R.drawable.cam);
            }
        }
        if(isrescheduleFlag && reschedule_enable){
            activityEdsUndeliveredBinding.rescheduleLayoutLayout.setVisibility(View.VISIBLE);
            return;
            // undeliveredViewModel.getShipmentRescheduleDetails(String.valueOf(awb));
        } else{
            activityEdsUndeliveredBinding.rescheduleLayoutLayout.setVisibility(View.GONE);
        }
        if(undeliveredViewModel.getDataManager().isCounterDelivery() && undeliveredViewModel.getCounterDeliveryRange() < undeliveredViewModel.getDataManager().getDCRANGE()){
        } else if(callFlag){
            int isCall = undeliveredViewModel.getisCallattempted(edsResponseCommit.getAwbNo().toString());
            check_call_count = isCall;
            if(isCall == 0 && undeliveredViewModel.getDataManager().getCallClicked(awbNo+"EDSCall")){
                makeCallDialog();
            }
        } else{
            activityEdsUndeliveredBinding.rescheduleLayoutLayout.setVisibility(View.GONE);
        }
        if(edsReasonCodeMaster.getReasonMessage().equalsIgnoreCase("Residence / office closed")){
            showStatusDialog("Attention : Take the clear full image of the residence doorstep closely");
        }
        if(edsReasonCodeMaster.getReasonMessage().equalsIgnoreCase("Addressee shifted, phone not reachable")){
            showStatusDialog("Attention : Take the clear full image of the residence door step closely");
        }
        if(edsReasonCodeMaster.getReasonMessage().equalsIgnoreCase("Entry not allowed")){
            showStatusDialog("Attention : Take the clear full image of the Building Gate /Location closely from where\n" + "the entry is restricted");
        }
        if(edsReasonCodeMaster.getReasonMessage().equalsIgnoreCase("Addressee refused activity on field, hence cancelled by Ecom")){
            showStatusDialog("Attention : Take the clear full image of the residence door step closely");
        }
        if(edsReasonCodeMaster.getReasonMessage().equalsIgnoreCase("Address not locatable and Addressee not contactable")){
            showStatusDialog("Attention : Take the clear full image of the landmark from where we are unable to locate\n" + "customer address (Residence/Shop/Building/Plot) closely");
        }
        if(edsReasonCodeMaster.getReasonMessage().equalsIgnoreCase("No such addressee in the given address")){
            showStatusDialog("Attention : Take the clear full image of the residence door step closely");
        }
        if(edsReasonCodeMaster.getReasonMessage().equalsIgnoreCase("Field Data Validation Failed")){
            showStatusDialog("Attention : Take the clear full image of the residence door step closely");
        }
    }

    @Override
    public void visible(boolean flag){
        if(flag){
            activityEdsUndeliveredBinding.groupLayout.setVisibility(View.VISIBLE);
            activityEdsUndeliveredBinding.layout.setVisibility(View.GONE);
        } else{
            activityEdsUndeliveredBinding.groupLayout.setVisibility(View.GONE);
            activityEdsUndeliveredBinding.layout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showError(String error){
        showSnackbar(error);
    }

    @Override
    public void setParentGroupSpinnerValues(List<String> parentGroupSpinnerValues){
        activityEdsUndeliveredBinding.spinnerGroupType.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_single_item, R.id.spinner_text_view, parentGroupSpinnerValues));
    }

    @Override
    public void setChildSpinnerValues(List<String> childGroupSpinnerValues){
        activityEdsUndeliveredBinding.spinnerChildType.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_single_item, R.id.spinner_text_view, childGroupSpinnerValues));
    }

    @Override
    public void setVehicleTypeSpinnerValues(List<String> vehicleTypeSpinnerValues){
        activityEdsUndeliveredBinding.spinnerVehicleType.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_single_item, R.id.spinner_text_view, vehicleTypeSpinnerValues));
    }

    @Override
    public void showErrorMessage(boolean status){
        if(status)
            showSnackbar(getString(R.string.http_500_msg));
        else
            showSnackbar(getString(R.string.server_down_msg));
    }

    @Override
    public void undeliverShipment(boolean callFlag){
        if(callFlag){
            undeliver();
        } else{
            showDialog();
        }
    }

    @Override
    public void getEDSRescheduleFlag(Boolean aBoolean){
        reschedule_enable = aBoolean;
    }

    @Override
    public Activity getActivityContext(){
        return this;
    }

    @Override
    public void setRescheduleResponse(ReshceduleDetailsResponse reshceduleDetailsResponse){
        this.reshceduleDetailsResponse = reshceduleDetailsResponse;
        activityEdsUndeliveredBinding.rescheduleLayoutLayout.setVisibility(View.VISIBLE);
    }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(EDSUndeliveredActivity.this,R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
        String AlertText1 = "Attention : ";
        builder.setMessage(AlertText1 + getString(R.string.undelivered_shipment_msg)).setCancelable(false).setPositiveButton("Call", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                try{
                    makeCallDialog();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public void showCallBridgeDialog() {
        Dialog dialog = new Dialog(this, R.style.RoundedCornersDialog);
        dialog.setContentView(R.layout.dialog_callbridge);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Button call = dialog.findViewById(R.id.bt_call);
        call.setText(edsResponse.getCallbridge_details().get(0).getCallbridge_number());
        Button altCall = dialog.findViewById(R.id.bt_sms);
        altCall.setText(edsResponse.getCallbridge_details().get(1).getCallbridge_number());
        ImageView crossDialog = dialog.findViewById(R.id.crssdialog);
        crossDialog.setOnClickListener(v -> dialog.dismiss());
        call.setOnClickListener(v -> {
            undeliveredViewModel.consigneeContactNumber.set(edsResponse.getCallbridge_details().get(0).getCallbridge_number()+","+edsResponse.getCallbridge_details().get(0).getPin().substring(0, 4)+","+ edsResponse.getCallbridge_details().get(0).getPin().substring(4)+"#");
            eds_call_count = eds_call_count + 1;
            undeliveredViewModel.getDataManager().setEDSCallCount(awb+"EDS" ,eds_call_count);
            Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + edsResponse.getCallbridge_details().get(0).getCallbridge_number()+","+edsResponse.getCallbridge_details().get(0).getPin().substring(0, 4)+","+ edsResponse.getCallbridge_details().get(0).getPin().substring(4)+"#"));
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
            dialog.dismiss();
        });
        altCall.setOnClickListener(v -> {
            undeliveredViewModel.consigneeContactNumber.set(edsResponse.getCallbridge_details().get(1).getCallbridge_number()+","+edsResponse.getCallbridge_details().get(1).getPin()+"#");
            eds_call_count = eds_call_count + 1;
            undeliveredViewModel.getDataManager().setEDSCallCount(awb+"EDS" ,eds_call_count);
            Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + edsResponse.getCallbridge_details().get(1).getCallbridge_number()+","+edsResponse.getCallbridge_details().get(1).getPin()+"#"));
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
            dialog.dismiss();
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
    public void showDirectCallDialog(){
        Dialog dialog = new Dialog(this, R.style.RoundedCornersDialog);
        dialog.setContentView(R.layout.dialog_direct_call);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Button call = dialog.findViewById(R.id.bt_call);
        Button altcall = dialog.findViewById(R.id.bt_sms);
        ImageView crssdialog = dialog.findViewById(R.id.crssdialog);
        crssdialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                dialog.dismiss();
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                undeliveredViewModel.consigneeContactNumber.set(ConsigneeDirectMobileNo);
                eds_call_count = eds_call_count + 1;
                undeliveredViewModel.getDataManager().setEDSCallCount(awb+"EDS" ,eds_call_count);
                Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ConsigneeDirectMobileNo));
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
                dialog.dismiss();
            }
        });
        altcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                undeliveredViewModel.consigneeContactNumber.set(ConsigneeDirectAlternateMobileNo);
                eds_call_count = eds_call_count + 1;
                undeliveredViewModel.getDataManager().setEDSCallCount(awb+"EDS" ,eds_call_count);
                Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ConsigneeDirectAlternateMobileNo));
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void otpLayout(boolean uD_OTP_staus, String otp_key){
        try{
            if(otp_key.equalsIgnoreCase("UD_OTP")){
                if(Constants.CancellationEnable){
                    if(uD_OTP_staus){
                        activityEdsUndeliveredBinding.llUdOtp.setVisibility(View.VISIBLE);
                        activityEdsUndeliveredBinding.edtUdOtp.setEnabled(false);
                        activityEdsUndeliveredBinding.edtUdOtp.setText("");
                        activityEdsUndeliveredBinding.imgVerifiedTick.setVisibility(View.GONE);
                       // undeliveredViewModel.ud_otp_commit_status = "NONE";
                        undeliveredViewModel.ud_otp_verified_status.set(false);
                        activityEdsUndeliveredBinding.generateOtpTv.setVisibility(View.VISIBLE);
                        activityEdsUndeliveredBinding.resendOtpTv.setVisibility(View.GONE);
                        activityEdsUndeliveredBinding.verifyTv.setVisibility(View.GONE);
                        uD_OTP = uD_OTP_staus;
                    } else{
                        uD_OTP = false;
                        activityEdsUndeliveredBinding.llUdOtp.setVisibility(View.GONE);
                    }
                } else{
                    uD_OTP = false;
                    activityEdsUndeliveredBinding.llUdOtp.setVisibility(View.GONE);
                }
            } else if(otp_key.equalsIgnoreCase("RCHD_OTP")){
                if(Constants.RCHDEnable){
                    if(uD_OTP_staus){
                        activityEdsUndeliveredBinding.llUdOtp.setVisibility(View.VISIBLE);
                        activityEdsUndeliveredBinding.edtUdOtp.setEnabled(false);
                        activityEdsUndeliveredBinding.edtUdOtp.setText("");
                        activityEdsUndeliveredBinding.imgVerifiedTick.setVisibility(View.GONE);
                        //undeliveredViewModel.ud_otp_commit_status = "NONE";
                        undeliveredViewModel.ud_otp_verified_status.set(false);
                        activityEdsUndeliveredBinding.generateOtpTv.setVisibility(View.VISIBLE);
                        activityEdsUndeliveredBinding.resendOtpTv.setVisibility(View.GONE);
                        activityEdsUndeliveredBinding.verifyTv.setVisibility(View.GONE);
                        uD_OTP = uD_OTP_staus;
                    } else{
                        uD_OTP = false;
                        activityEdsUndeliveredBinding.llUdOtp.setVisibility(View.GONE);
                    }
                } else{
                    uD_OTP = false;
                    activityEdsUndeliveredBinding.llUdOtp.setVisibility(View.GONE);
                }
            } else{
                uD_OTP = false;
                activityEdsUndeliveredBinding.llUdOtp.setVisibility(View.GONE);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResendClick(){
        hideKeyboard(this);
        if(call_allowed && undeliveredViewModel.getDataManager().getVCallPopup()){
            undeliveredViewModel.showCallAndSmsDialog(this , String.valueOf(awb), String.valueOf(edsResponseCommit.getDrsNo()), String.valueOf(ConsigneeDirectAlternateMobileNo), "Call");
        } else if(!String.valueOf(ConsigneeDirectMobileNo).equalsIgnoreCase("") || String.valueOf(ConsigneeDirectMobileNo) != null){
            undeliveredViewModel.showCallAndSmsDialog(this ,String.valueOf(awb), String.valueOf(edsResponseCommit.getDrsNo()), String.valueOf(ConsigneeDirectAlternateMobileNo), "");
        } else{
            if(edsReasonCodeMaster.getEdsMasterDataAttributeResponse().isUD_OTP()){
                undeliveredViewModel.onGenerateOtpApiCall(EDSUndeliveredActivity.this, awb + "", edsResponseCommit.getDrsNo() + "", false, "UD_OTP", false);
            }
            if(edsReasonCodeMaster.getEdsMasterDataAttributeResponse().isRCHD_OTP()){
                undeliveredViewModel.onGenerateOtpApiCall(EDSUndeliveredActivity.this, awb + "", edsResponseCommit.getDrsNo() + "", false, "RCHD_OTP", false);
            }
        }
    }

    @Override
    public void onGenerateOtpClick(){
        hideKeyboard(this);
        if(edsReasonCodeMaster.getEdsMasterDataAttributeResponse().isUD_OTP()){
            undeliveredViewModel.onGenerateOtpApiCall(EDSUndeliveredActivity.this, awb + "", edsResponseCommit.getDrsNo() + "", false, "UD_OTP", true);
        }
        if(edsReasonCodeMaster.getEdsMasterDataAttributeResponse().isRCHD_OTP()){
            undeliveredViewModel.onGenerateOtpApiCall(EDSUndeliveredActivity.this, awb + "", edsResponseCommit.getDrsNo() + "", false, "RCHD_OTP", true);
        }
    }

    @Override
    public void onVerifyClick(){
        hideKeyboard(this);
        if(activityEdsUndeliveredBinding.edtUdOtp.getText() == null || activityEdsUndeliveredBinding.edtUdOtp.getText().toString().equalsIgnoreCase("")){
            showError("Please Enter OTP.");
        } else if(activityEdsUndeliveredBinding.edtUdOtp.getText().length() < 4){
            showError("Please Enter OTP.");
        } else{
            if(OFD_OTP != null && !OFD_OTP.equalsIgnoreCase("")){
                String encryptText = CommonUtils.decrypt(String.valueOf(OFD_OTP), Constants.DECRYPT);
                Constants.PLAIN_OTP = encryptText;
                if(encryptText.equalsIgnoreCase(activityEdsUndeliveredBinding.edtUdOtp.getText().toString())){
                    Constants.OFD_OTP_VERIFIED = true;
                    undeliveredViewModel.setOFDVerfied(true);
                    Toast.makeText(EDSUndeliveredActivity.this, "Verified successfully", Toast.LENGTH_SHORT).show();
                } else{
                    Constants.OFD_OTP_VERIFIED = false;
                    if(edsReasonCodeMaster.getEdsMasterDataAttributeResponse().isUD_OTP()){
                        undeliveredViewModel.onVerifyApiCall(EDSUndeliveredActivity.this, activityEdsUndeliveredBinding.awbTv.getText().toString(), activityEdsUndeliveredBinding.edtUdOtp.getText().toString(), edsResponseCommit.getDrsNo() + "", "UD_OTP");
                        showSnackbar("Verified.");
                        activityEdsUndeliveredBinding.otpSkip.setChecked(false);
                    }
                    if(edsReasonCodeMaster.getEdsMasterDataAttributeResponse().isRCHD_OTP()){
                        undeliveredViewModel.onVerifyApiCall(EDSUndeliveredActivity.this, activityEdsUndeliveredBinding.awbTv.getText().toString(), activityEdsUndeliveredBinding.edtUdOtp.getText().toString(), edsResponseCommit.getDrsNo() + "", "RCHD_OTP");
                        showSnackbar("Verified.");
                        activityEdsUndeliveredBinding.otpSkip.setChecked(false);
                    }
                }
            } else{
                Constants.OFD_OTP_VERIFIED = false;
                if(edsReasonCodeMaster.getEdsMasterDataAttributeResponse().isUD_OTP()){
                    undeliveredViewModel.onVerifyApiCall(EDSUndeliveredActivity.this, activityEdsUndeliveredBinding.awbTv.getText().toString(), activityEdsUndeliveredBinding.edtUdOtp.getText().toString(), edsResponseCommit.getDrsNo() + "", "UD_OTP");
                    showSnackbar("Verified.");
                    activityEdsUndeliveredBinding.otpSkip.setChecked(false);
                }
                if(edsReasonCodeMaster.getEdsMasterDataAttributeResponse().isRCHD_OTP()){
                    undeliveredViewModel.onVerifyApiCall(EDSUndeliveredActivity.this, activityEdsUndeliveredBinding.awbTv.getText().toString(), activityEdsUndeliveredBinding.edtUdOtp.getText().toString(), edsResponseCommit.getDrsNo() + "", "RCHD_OTP");
                    showSnackbar("Verified.");
                    activityEdsUndeliveredBinding.otpSkip.setChecked(false);
                }
            }
        }
    }

    @Override
    public void onSkipClick(View view){
        hideKeyboard(this);
        if(undeliveredViewModel.ud_otp_verified_status.get()){
            showError("Already Verified");
            activityEdsUndeliveredBinding.otpSkip.setChecked(false);
            return;
        }
        if(undeliveredViewModel.counter_skip == 0){
            activityEdsUndeliveredBinding.otpSkip.setChecked(false);
            showError("Please resend OTP atleast once.");
            return;
        } else{
            if (((CheckBox)view).isChecked()) {
                if(edsReasonCodeMaster.getEdsMasterDataAttributeResponse().isUD_OTP()){
                    undeliveredViewModel.ud_otp_commit_status = "SKIPPED";
                    undeliveredViewModel.ud_otp_commit_status_field.set("SKIPPED");
                    undeliveredViewModel.rd_otp_commit_status = "";
                    undeliveredViewModel.rd_otp_commit_status_field.set("");
                }
                if(edsReasonCodeMaster.getEdsMasterDataAttributeResponse().isRCHD_OTP()){
                    undeliveredViewModel.rd_otp_commit_status = "SKIPPED";
                    undeliveredViewModel.rd_otp_commit_status_field.set("SKIPPED");
                    undeliveredViewModel.ud_otp_commit_status = "";
                    undeliveredViewModel.ud_otp_commit_status_field.set("");
                }
            }
            else
            {
                undeliveredViewModel.ud_otp_commit_status = "NONE";
                undeliveredViewModel.ud_otp_commit_status_field.set("NONE");
                undeliveredViewModel.rd_otp_commit_status = "NONE";
                undeliveredViewModel.rd_otp_commit_status_field.set("NONE");
            }
            //activityEdsUndeliveredBinding.otpSkip.setChecked(true);
        }
    }

    @Override
    public void onOtpResendSuccess(String str, String description){
        showSnackbar(description);
        activityEdsUndeliveredBinding.generateOtpTv.setVisibility(View.GONE);
        activityEdsUndeliveredBinding.edtUdOtp.setEnabled(true);
        activityEdsUndeliveredBinding.resendOtpTv.setVisibility(View.VISIBLE);
        activityEdsUndeliveredBinding.verifyTv.setVisibility(View.VISIBLE);
        if(str.equalsIgnoreCase("true") && description.contains("success")){
            if(mCountDownTimer != null){
                mCountDownTimer.cancel();
                mCountDownTimer = new CountDownTimer(undeliveredViewModel.getDataManager().getDisableResendOtpButtonDuration() * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished){
                        activityEdsUndeliveredBinding.resendOtpTv.setEnabled(false);
                        activityEdsUndeliveredBinding.resendOtpTv.setTextColor(getResources().getColor(R.color.light_gray));
                        activityEdsUndeliveredBinding.otpSkip.setEnabled(false);
                        String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                        System.out.println(hms);
                        activityEdsUndeliveredBinding.resendOtpTv.setText(hms);
                    }

                    @Override
                    public void onFinish(){
                        activityEdsUndeliveredBinding.resendOtpTv.setEnabled(true);
                        activityEdsUndeliveredBinding.resendOtpTv.setText(getResources().getString(R.string.resend));
                        activityEdsUndeliveredBinding.otpSkip.setEnabled(true);
                    }
                };
                mCountDownTimer.start();
            } else{
                mCountDownTimer = new CountDownTimer(undeliveredViewModel.getDataManager().getDisableResendOtpButtonDuration() * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished){
                        activityEdsUndeliveredBinding.resendOtpTv.setEnabled(false);
                        activityEdsUndeliveredBinding.resendOtpTv.setTextColor(getResources().getColor(R.color.light_gray));
                        activityEdsUndeliveredBinding.otpSkip.setEnabled(false);
                        String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                        System.out.println(hms);
                        activityEdsUndeliveredBinding.resendOtpTv.setText(hms);
                    }

                    @Override
                    public void onFinish(){
                        activityEdsUndeliveredBinding.resendOtpTv.setEnabled(true);
                        activityEdsUndeliveredBinding.resendOtpTv.setText(getResources().getString(R.string.resend));
                        activityEdsUndeliveredBinding.otpSkip.setEnabled(true);
                    }
                };
                mCountDownTimer.start();
            }
        } else{
            if(mCountDownTimer != null){
                activityEdsUndeliveredBinding.resendOtpTv.setEnabled(true);
                activityEdsUndeliveredBinding.resendOtpTv.setText(getResources().getString(R.string.resend));
                activityEdsUndeliveredBinding.otpSkip.setEnabled(true);
                mCountDownTimer.cancel();
            }
            else {
                activityEdsUndeliveredBinding.otpSkip.setEnabled(true);
            }
        }
    }

    @Override
    public void resendSms(Boolean alternateclick){
        hideKeyboard(this);
        if(edsReasonCodeMaster.getEdsMasterDataAttributeResponse().isUD_OTP()){
            undeliveredViewModel.onGenerateOtpApiCall(EDSUndeliveredActivity.this, awb + "", edsResponseCommit.getDrsNo() + "", alternateclick, "UD_OTP", false);
        }
        if(edsReasonCodeMaster.getEdsMasterDataAttributeResponse().isRCHD_OTP()){
            undeliveredViewModel.onGenerateOtpApiCall(EDSUndeliveredActivity.this, awb + "", edsResponseCommit.getDrsNo() + "", alternateclick, "RCHD_OTP", false);
        }
    }

    @Override
    public void voiceTimer(){
        if(mCountDownTimer != null){
            mCountDownTimer.cancel();
            mCountDownTimer = new CountDownTimer(undeliveredViewModel.getDataManager().getDisableResendOtpButtonDuration() * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished){
                    activityEdsUndeliveredBinding.resendOtpTv.setEnabled(false);
                    activityEdsUndeliveredBinding.resendOtpTv.setTextColor(getResources().getColor(R.color.light_gray));
                    activityEdsUndeliveredBinding.otpSkip.setEnabled(false);
                    String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                    System.out.println(hms);
                    activityEdsUndeliveredBinding.resendOtpTv.setText(hms);
                }

                @Override
                public void onFinish(){
                    activityEdsUndeliveredBinding.resendOtpTv.setEnabled(true);
                    activityEdsUndeliveredBinding.resendOtpTv.setText(getResources().getString(R.string.resend));
                    activityEdsUndeliveredBinding.otpSkip.setEnabled(true);
                }
            };
            mCountDownTimer.start();
        } else{
            mCountDownTimer = new CountDownTimer(undeliveredViewModel.getDataManager().getDisableResendOtpButtonDuration() * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished){
                    activityEdsUndeliveredBinding.resendOtpTv.setEnabled(false);
                    activityEdsUndeliveredBinding.resendOtpTv.setTextColor(getResources().getColor(R.color.light_gray));
                    activityEdsUndeliveredBinding.otpSkip.setEnabled(false);
                    String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                    System.out.println(hms);
                    activityEdsUndeliveredBinding.resendOtpTv.setText(hms);
                }

                @Override
                public void onFinish(){
                    activityEdsUndeliveredBinding.resendOtpTv.setEnabled(true);
                    activityEdsUndeliveredBinding.resendOtpTv.setText(getResources().getString(R.string.resend));
                    activityEdsUndeliveredBinding.otpSkip.setEnabled(true);
                }
            };
            mCountDownTimer.start();
        }
    }

    @Override
    public void VoiceCallOtp(){
        hideKeyboard(this);
        if(edsReasonCodeMaster.getEdsMasterDataAttributeResponse().isUD_OTP()){
            undeliveredViewModel.doVoiceOTPApi(awb + "", edsResponseCommit.getDrsNo() + "", "UD_OTP");
        } else if(edsReasonCodeMaster.getEdsMasterDataAttributeResponse().isRCHD_OTP()){
            undeliveredViewModel.doVoiceOTPApi(awb + "", edsResponseCommit.getDrsNo() + "", "RCHD_OTP");
        } else{
            undeliveredViewModel.doVoiceOTPApi(awb + "", edsResponseCommit.getDrsNo() + "", "OTP");
        }
    }

    @Override
    public void onCallBridgeCheckStatus() {
        dialog = new BottomSheetDialog(EDSUndeliveredActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.activity_undelivered_call_dialog);
        TextView name = dialog.findViewById(R.id.name);
        name.setText("Name : " + edsResponseCommit.getShipmentDetail().getCustomerName());
        TextView awb = dialog.findViewById(R.id.awb);
        awb.setText("AWB : " + edsResponseCommit.getAwbNo());
        ImageView dialogButton = dialog.findViewById(R.id.call);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                undeliveredViewModel.getDataManager().setCallClicked(edsCommit.getAwb()+"EDSCall", false);
                if(edsResponse.getShipmentDetail().getFlag().getFlagMap().getIs_callbridge_enabled().equalsIgnoreCase("true")
                        && edsResponse.getCallbridge_details()!=null) {

                    makeCallonClick();
                }
                else
                {
                    if(!TextUtils.isEmpty(ConsigneeDirectAlternateMobileNo) && ConsigneeDirectAlternateMobileNo != null && ConsigneeDirectAlternateMobileNo != "0"){
                        showDirectCallDialog();
                    } else {
                        undeliveredViewModel.consigneeContactNumber.set(edsResponse.getConsigneeDetail().getMobile());
                        eds_call_count = eds_call_count + 1;
                        undeliveredViewModel.getDataManager().setEDSCallCount(awb + "EDS", eds_call_count);
                        CommonUtils.startCallIntent(edsResponse.getConsigneeDetail().getMobile(), getActivityContext(), EDSUndeliveredActivity.this);
                    }
                }

            }
        });
        dialog.show();
    }
}
