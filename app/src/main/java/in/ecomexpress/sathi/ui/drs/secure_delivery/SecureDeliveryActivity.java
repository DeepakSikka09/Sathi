package in.ecomexpress.sathi.ui.drs.secure_delivery;

import static in.ecomexpress.sathi.ui.drs.todolist.DRSListAdapter.commonDRSListItemFWDClick;
import static in.ecomexpress.sathi.ui.drs.todolist.DRSListAdapter.commonDRSListItemRVPClick;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.Constants.INTENT_KEY_RVP_WITH_QC;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.nlscan.android.scan.ScanManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.barcodelistner.BarcodeHandler;
import in.ecomexpress.barcodelistner.BarcodeResult;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityForwardSecureDeliveryBinding;
import in.ecomexpress.sathi.repo.local.data.activitiesdata.FWDActivitiesData;
import in.ecomexpress.sathi.repo.local.data.eds.EdsCommit;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.repo.local.data.rvp.RvpCommit;
import in.ecomexpress.sathi.repo.local.db.model.CommonDRSListItem;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.SecureDelivery;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.dummy.eds.eds_task_list.EdsTaskListActivity;
import in.ecomexpress.sathi.ui.dummy.eds.undeilvered_eds.EDSUndeliveredActivity;
import in.ecomexpress.sathi.ui.drs.forward.bpid.ScanBPIDActivity;
import in.ecomexpress.sathi.ui.drs.forward.details.ForwardDetailActivity;
import in.ecomexpress.sathi.ui.drs.forward.mps.MPSScanActivity;
import in.ecomexpress.sathi.ui.drs.forward.signature.SignatureActivity;
import in.ecomexpress.sathi.ui.drs.forward.undelivered_fwd.UndeliveredActivity;
import in.ecomexpress.sathi.ui.drs.forward.undelivered_fwd.UndeliveredBPIDActivity;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;
import in.ecomexpress.sathi.ui.drs.rvp.awbscan.ScannerActivity;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_list.RvpQcListActivity;
import in.ecomexpress.sathi.ui.drs.rvp.undelivered.RVPUndeliveredActivity;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.PreferenceUtils;

@AndroidEntryPoint
public class SecureDeliveryActivity extends BaseActivity<ActivityForwardSecureDeliveryBinding, SecureDeliveryViewModel> implements ISecureDeliveryNavigator, BarcodeResult {

    private final String TAG = SecureDeliveryActivity.class.getSimpleName();
    private final static int MY_REQUEST_CODE = 1;
    private final int REQUEST_CODE_SCAN = 1101;
    BarcodeHandler barcodeHandler;
    @Inject
    SecureDeliveryViewModel secureDeliveryViewModel;
    @Inject
    ForwardCommit forwardCommit;
    @Inject
    RvpCommit mRvpCommit;
    @Inject
    EdsCommit medsCommit;
    ActivityForwardSecureDeliveryBinding activityForwardSecureDeliveryBinding;
    @Inject
    Context context;
    String getDrsApiKey = null, getDrsPstnKey = null, getDrsPin = null, shipmentType = null, composite_key = "", getOrderId = "";
    String productType = null;
    Boolean isDigitalPaymentAllowed = false;
    boolean is_kyc_active;
    SecureDelivery isSecureDelivery;
    Long awbNo, new_Awb;
    String amazon_encrypted_otp = "";
    String amazon;
    String order_id = "";
    String delight_encrypted_otp1;
    String delight_encrypted_otp2;
    boolean isDelightShipment;
    boolean rescudeEnable;
    boolean sign_image_required;
    String fwd_del_image = "";
    String drs_id_num, consignee_mobile, consignee_alternate_mobile = "";
    boolean call_allowed;
    String OFD_OTP;
    String return_package_barcode = "";
    String ScanValue = "";
    String manualEnterBP = "";
    String shw_fwd_undl_btn = "No";
    private boolean is_amazon_schedule_enable;
    private String mpsShipment, mpsAWBs;
    private boolean is_cash_collection;
    private boolean resend_secure_otp;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, SecureDeliveryActivity.class);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        secureDeliveryViewModel.setNavigator(this);
        activityForwardSecureDeliveryBinding = getViewDataBinding();
        logScreenNameInGoogleAnalytics(TAG, this);
        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                activityForwardSecureDeliveryBinding.callBridge.setImageResource(R.drawable.barcode);
            } else {
                activityForwardSecureDeliveryBinding.callBridge.setImageResource(R.drawable.ic_scan_barcode);
            }
            awbNo = getIntent().getLongExtra(Constants.INTENT_KEY, 0);
            sign_image_required = getIntent().getBooleanExtra(Constants.sign_image_required, false);
            if (getIntent().getStringExtra(Constants.FWD_DEL_IMAGE) != null)
            {
                fwd_del_image = getIntent().getStringExtra(Constants.FWD_DEL_IMAGE);
            }
            resend_secure_otp = getIntent().getBooleanExtra(Constants.RESEND_SECURE_OTP, false);
            OFD_OTP = getIntent().getStringExtra(Constants.OFD_OTP);
            // Check if return_package_barcode contains null then convert it into blank string:-
            if (getIntent().getStringExtra(Constants.return_package_barcode) == null) {
                return_package_barcode = "";
            } else {
                return_package_barcode = getIntent().getStringExtra(Constants.return_package_barcode);
            }
            secureDeliveryViewModel.amz_scanArrayList.clear();
            //Fetching data for Clicked list item from awb number
            isSecureDelivery = new SecureDelivery();
            shipmentType = getIntent().getExtras().getString(Constants.SHIPMENT_TYPE);
            secureDeliveryViewModel.shipmentType = shipmentType;
            if (shipmentType.equalsIgnoreCase("EDS")) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

                    productType = Constants.EDS;
                }
            }
            if (shipmentType.equalsIgnoreCase("rvp")) {
                activityForwardSecureDeliveryBinding.btDeliver.setText("Picked Up");
                activityForwardSecureDeliveryBinding.btUndeliver.setText("Failed");
                activityForwardSecureDeliveryBinding.textchng.setText("Note : If consignee wants to handover the shipment select Picked Up option else Failed option");
                productType = Constants.RVP;
            }
            if (shipmentType.equalsIgnoreCase("fwd")) {
                if (commonDRSListItemFWDClick != null) {
                    if (commonDRSListItemFWDClick.getDrsForwardTypeResponse() != null) {

                        if (commonDRSListItemFWDClick.getDrsForwardTypeResponse().getShipmentDetails().getType().equalsIgnoreCase("PPD")) {

                            productType = Constants.PPD;
                        } else {

                            productType = Constants.COD;
                        }
                    }
                }
            }
            secureDeliveryViewModel.productType = productType;
            drs_id_num = getIntent().getStringExtra(Constants.DRS_ID_NUM);
            getDrsApiKey = getIntent().getExtras().getString(Constants.DRS_API_KEY);
            getDrsPstnKey = getIntent().getExtras().getString(Constants.DRS_PSTN_KEY);
            consignee_mobile = getIntent().getStringExtra(Constants.CONSIGNEE_MOBILE);
            if (getIntent().getStringExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE) != null) {
                consignee_alternate_mobile = getIntent().getStringExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE);
            }
            if (getIntent().getStringExtra(Constants.SHOW_FWD_UNDL_BTN) != null) {
                shw_fwd_undl_btn = getIntent().getStringExtra(Constants.SHOW_FWD_UNDL_BTN);

            }
            call_allowed = getIntent().getBooleanExtra("call_allowed", false);
            is_amazon_schedule_enable = getIntent().getBooleanExtra(Constants.IS_AMAZON_RESHEDUCLE_ENABLE, false);
            if (getIntent().getStringExtra(Constants.ORDER_ID) != null) {
                order_id = getIntent().getStringExtra(Constants.ORDER_ID);
            }
            amazon_encrypted_otp = getIntent().getExtras().getString(Constants.AMAZON_ENCRYPTED_OTP);
            try {
                if (amazon_encrypted_otp == null)
                    amazon_encrypted_otp = "";
            } catch (Exception e) {
                e.printStackTrace();
            }
            amazon = getIntent().getExtras().getString(Constants.AMAZON);
            delight_encrypted_otp1 = getIntent().getExtras().getString(Constants.DLIGHT_ENCRYPTED_OTP1);
            delight_encrypted_otp2 = getIntent().getExtras().getString(Constants.DLIGHT_ENCRYPTED_OTP2);
            isDelightShipment = getIntent().getExtras().getBoolean(Constants.ISDELIGHTSHIPMENT);
            is_kyc_active = getIntent().getBooleanExtra(Constants.IS_KYC_ACTIVE, false);
            getDrsPin = getIntent().getExtras().getString(Constants.DRS_PIN);
            getOrderId = getIntent().getExtras().getString(Constants.ORDER_ID);
            is_cash_collection = getIntent().getBooleanExtra(Constants.IS_CASH_COLLECTION, false);
            isDigitalPaymentAllowed = getIntent().getBooleanExtra(Constants.IS_CARD, false);
            isSecureDelivery = getIntent().getParcelableExtra(Constants.SECURE_DELIVERY);
            mpsShipment = getIntent().getExtras().getString(Constants.MPS);
            mpsAWBs = getIntent().getExtras().getString(Constants.MPS_AWB_NOS);
            composite_key = getIntent().getStringExtra(Constants.COMPOSITE_KEY);
            if (shipmentType.equalsIgnoreCase(Constants.FWD)) {
                secureDeliveryViewModel.getFWDShipmentData(forwardCommit, composite_key, consignee_mobile);
            } else if (shipmentType.equalsIgnoreCase(Constants.RVP)) {
                activityForwardSecureDeliveryBinding.amountlayout.setVisibility(View.GONE);
                secureDeliveryViewModel.getRvpShipmentData(mRvpCommit, composite_key, consignee_mobile);
                secureDeliveryViewModel.getRvpDataWithQc(composite_key);
            } else if (shipmentType.equalsIgnoreCase(Constants.EDS)) {
                try {
                    activityForwardSecureDeliveryBinding.enterOtp.setHint(getResources().getString(R.string.eds_refno));
                    rescudeEnable = getIntent().getBooleanExtra(Constants.RESCHEDULE_ENABLE, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                activityForwardSecureDeliveryBinding.amountlayout.setVisibility(View.GONE);
                secureDeliveryViewModel.getEDSShipmentData(medsCommit, composite_key, consignee_mobile);
                secureDeliveryViewModel.getEdsListTask(composite_key);
            }
            secureDeliveryViewModel.getShipmentType(shipmentType);
            secureDeliveryViewModel.getSecureDelivery(isSecureDelivery);
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
        activityForwardSecureDeliveryBinding.consigneeAddress.setMovementMethod(new ScrollingMovementMethod());
        activityForwardSecureDeliveryBinding.consigneeName.setMovementMethod(new ScrollingMovementMethod());

        barcodeHandler = new BarcodeHandler(this, "ScannerLM", this);
        barcodeHandler.enableScanner();
        if (resend_secure_otp) {
            activityForwardSecureDeliveryBinding.resend.setVisibility(View.VISIBLE);
        } else {
            activityForwardSecureDeliveryBinding.resend.setVisibility(View.INVISIBLE);
        }
        activityForwardSecureDeliveryBinding.crssdialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(SecureDeliveryActivity.this);
                activityForwardSecureDeliveryBinding.enterOtp.setText("");
                activityForwardSecureDeliveryBinding.isotpLayout.setVisibility(View.GONE);
                //activityForwardSecureDeliveryBinding.btUndeliver.setBackgroundTintList(getResources().getColorStateList(R.color.red_dark2));
                activityForwardSecureDeliveryBinding.btUndeliver.setEnabled(true);
                activityForwardSecureDeliveryBinding.btUndeliver.setVisibility(View.VISIBLE);
                activityForwardSecureDeliveryBinding.textchng.setVisibility(View.VISIBLE);
            }
        });


        if (shipmentType.equalsIgnoreCase("fwd")) {


            if (shw_fwd_undl_btn.equalsIgnoreCase("Yes")) {
                if (!return_package_barcode.equals("")) {
                    activityForwardSecureDeliveryBinding.btDeliver.setVisibility(View.GONE);
                    activityForwardSecureDeliveryBinding.btBpID.setVisibility(View.VISIBLE);
                    activityForwardSecureDeliveryBinding.btBpID.setAlpha(1f);
                    activityForwardSecureDeliveryBinding.btBpID.setEnabled(true);


                } else {
                    activityForwardSecureDeliveryBinding.btBpID.setVisibility(View.GONE);
                    activityForwardSecureDeliveryBinding.btDeliver.setVisibility(View.VISIBLE);


                }

            } else {
                activityForwardSecureDeliveryBinding.btDeliver.setVisibility(View.VISIBLE);
                if (!return_package_barcode.equals("")) {
                    activityForwardSecureDeliveryBinding.btBpID.setVisibility(View.VISIBLE);
                    activityForwardSecureDeliveryBinding.btBpID.setAlpha(0.5f);
                    activityForwardSecureDeliveryBinding.btBpID.setEnabled(false);


                } else {
                    activityForwardSecureDeliveryBinding.btBpID.setVisibility(View.GONE);

                }

            }
        }
             activityForwardSecureDeliveryBinding.header.awb.setText(R.string.secure_activity);
             activityForwardSecureDeliveryBinding.header.backArrow.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     onBack();
                 }
             });

    }

    @Override
    protected void onResume() {
        super.onResume();
        activityForwardSecureDeliveryBinding.enterOtp.setText("");
        if (shipmentType.equalsIgnoreCase(Constants.FWD)) {
            secureDeliveryViewModel.getDisputedAwb(String.valueOf(awbNo));
            try {
                if (commonDRSListItemFWDClick != null) {
                    if (commonDRSListItemFWDClick.getDrsForwardTypeResponse() != null) {
                        if (commonDRSListItemFWDClick.getDrsForwardTypeResponse().getShipmentDetails().getType().equalsIgnoreCase("PPD")) {
                            activityForwardSecureDeliveryBinding.amountlayout.setVisibility(View.GONE);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (barcodeHandler != null) {
            barcodeHandler.disableScanner();
            barcodeHandler.onDestroy();
            barcodeHandler = null;
        }
        barcodeHandler = new BarcodeHandler(this, "ScannerLM", this);
        barcodeHandler.enableScanner();
    }

    @Override
    public SecureDeliveryViewModel getViewModel() {
        return secureDeliveryViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_forward_secure_delivery;
    }

    //on click of undelivered button
    @Override
    public void onUndelivered(ForwardCommit forwardCommit) {
        try {
            Intent intent = new Intent();
            FWDActivitiesData fwdActivitiesData = new FWDActivitiesData();

            if (ScanValue.equals("ManuallyNotMatched")) {


                intent = UndeliveredBPIDActivity.getStartIntent(this);
                fwdActivitiesData.setReturn_package_barcode(manualEnterBP);


            } else {
                intent = UndeliveredActivity.getStartIntent(this);

            }
            fwdActivitiesData.setDrsPin(getDrsPin);
            fwdActivitiesData.setDrsApiKey(getDrsApiKey);
            fwdActivitiesData.setDrsPstnKey(getDrsPstnKey);
            fwdActivitiesData.setOrderId(getOrderId);
            fwdActivitiesData.setCallAllowed(call_allowed);
            fwdActivitiesData.setConsignee_mobile(consignee_mobile);
            fwdActivitiesData.setConsignee_alternate_number(consignee_alternate_mobile);
            fwdActivitiesData.setDrsId(Integer.parseInt(drs_id_num));
            fwdActivitiesData.setAwbNo(awbNo);
            fwdActivitiesData.setCompositeKey(composite_key);
            fwdActivitiesData.setIs_amazon_reschedule_enabled(is_amazon_schedule_enable);
            fwdActivitiesData.setShipment_type(activityForwardSecureDeliveryBinding.type.getText().toString());
            fwdActivitiesData.setSecure_undelivered(String.valueOf(secureDeliveryViewModel.isSecureOtp.get()));
            fwdActivitiesData.setCollected_value(secureDeliveryViewModel.getcollectablevalue());
            intent.putExtra(getString(R.string.data), forwardCommit);
            intent.putExtra("fwdActivitiesData", fwdActivitiesData);
            startActivity(intent);
            applyTransitionToOpenActivity(this);
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void onDelivered() {
        if (shipmentType.equalsIgnoreCase(Constants.RVP)) {
            rvpClick(commonDRSListItemRVPClick);
        } else {

            if (shw_fwd_undl_btn.equalsIgnoreCase("Yes") && return_package_barcode.equals("")) {
                openDetailActivity();
            } else {

                handleLayoutVisibility();
                activityForwardSecureDeliveryBinding.btUndeliver.setVisibility(View.GONE);
                activityForwardSecureDeliveryBinding.textchng.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onBPIDClick(ForwardCommit forwardCommit) {
        if (secureDeliveryViewModel.isSecureOtp.get() || amazon != null && amazon.equalsIgnoreCase("true")) {

            if (secureDeliveryViewModel.getDataManager().getBPMismatch()) {
                if (Constants.OFD_OTP_VERIFIED && ScanValue == "") {
                    Intent intent = new Intent();
                    FWDActivitiesData fwdActivitiesData = new FWDActivitiesData();
                    fwdActivitiesData.setReturn_package_barcode(return_package_barcode);
                    fwdActivitiesData.setAwbNo(awbNo);
                    fwdActivitiesData.setCompositeKey(composite_key);
                    fwdActivitiesData.setDrsId(Integer.parseInt(drs_id_num));

                    intent = ScanBPIDActivity.getStartIntent(context);
                    intent.putExtra("fwdActivitiesData", fwdActivitiesData);
                    intent.putExtra("data", forwardCommit);
                    startActivityForResult(intent, MY_REQUEST_CODE);
                } else if (Constants.OFD_OTP_VERIFIED && !ScanValue.equals("")) {

                    activityForwardSecureDeliveryBinding.enterOtp.setText(Constants.PLAIN_OTP);
                    onOtpVerifyButton();

                } else {

                }
            } else {
                if (Constants.OFD_OTP_VERIFIED && ScanValue == "") {
                    Intent intent = new Intent();
                    FWDActivitiesData fwdActivitiesData = new FWDActivitiesData();
                    fwdActivitiesData.setReturn_package_barcode(return_package_barcode);
                    fwdActivitiesData.setAwbNo(awbNo);
                    fwdActivitiesData.setCompositeKey(composite_key);
                    fwdActivitiesData.setDrsId(Integer.parseInt(drs_id_num));

                    intent = ScanBPIDActivity.getStartIntent(context);
                    intent.putExtra("fwdActivitiesData", fwdActivitiesData);
                    intent.putExtra("data", forwardCommit);
                    startActivityForResult(intent, MY_REQUEST_CODE);
                } else if (Constants.OFD_OTP_VERIFIED && !ScanValue.equals("ManuallyNotMatched")) {

                    activityForwardSecureDeliveryBinding.enterOtp.setText(Constants.PLAIN_OTP);

                    onOtpVerifyButton();

                } else if (Constants.OFD_OTP_VERIFIED && ScanValue.equals("ManuallyNotMatched")) {

                    secureDeliveryViewModel.onUndeliveredClick();

                } else {

                }
            }

        } else {
            if (secureDeliveryViewModel.getDataManager().getBPMismatch()) {
                if (ScanValue == "") {
                    Intent intent = new Intent();

                    FWDActivitiesData fwdActivitiesData = new FWDActivitiesData();
                    fwdActivitiesData.setReturn_package_barcode(return_package_barcode);
                    fwdActivitiesData.setAwbNo(awbNo);
                    fwdActivitiesData.setCompositeKey(composite_key);
                    fwdActivitiesData.setDrsId(Integer.parseInt(drs_id_num));

                    intent = ScanBPIDActivity.getStartIntent(context);
                    intent.putExtra("fwdActivitiesData", fwdActivitiesData);
                    intent.putExtra("data", forwardCommit);
                    startActivityForResult(intent, MY_REQUEST_CODE);
                } else if (!ScanValue.equals("")) {


                    openDetailActivity();


                } else {


                }
            } else {
                if (ScanValue == "") {
                    Intent intent = new Intent();

                    FWDActivitiesData fwdActivitiesData = new FWDActivitiesData();
                    fwdActivitiesData.setReturn_package_barcode(return_package_barcode);
                    fwdActivitiesData.setAwbNo(awbNo);
                    fwdActivitiesData.setCompositeKey(composite_key);
                    fwdActivitiesData.setDrsId(Integer.parseInt(drs_id_num));

                    intent = ScanBPIDActivity.getStartIntent(context);
                    intent.putExtra("fwdActivitiesData", fwdActivitiesData);
                    intent.putExtra("data", forwardCommit);
                    startActivityForResult(intent, MY_REQUEST_CODE);
                } else if (ScanValue.equals("Scan") || ScanValue.equals("ManuallyMatched")) {


                    openDetailActivity();

                } else if (ScanValue.equals("ManuallyNotMatched")) {

                    secureDeliveryViewModel.onUndeliveredClick();

                } else {


                }
            }

        }


    }

    private void rvpClick(CommonDRSListItem commonDRSListItem) {
        if (commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getSecure_delivery() != null) {
            if (!commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getPinb() && !commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getOTP() && !commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getSecure_pin()) {
                Intent intent = RvpQcListActivity.getStartIntent(this);
                intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstnKey);
                intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
                intent.putExtra(Constants.DRS_PIN, getDrsPin);
                intent.putExtra(Constants.DRS_ID_NUM, String.valueOf(commonDRSListItem.getDrsReverseQCTypeResponse().getDrs()));
                intent.putExtra(Constants.OFD_OTP, commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentDetails().getOfd_otp());
                intent.putExtra("call_allowed", commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getCallAllowed());
                intent.putExtra(Constants.COMPOSITE_KEY, commonDRSListItem.getDrsReverseQCTypeResponse().getCompositeKey());
                intent.putExtra(Constants.INTENT_KEY, commonDRSListItem.getDrsReverseQCTypeResponse().getAwbNo());
                startActivity(intent);
                applyTransitionToOpenActivity(this);
            } else {
                handleLayoutVisibility();
                activityForwardSecureDeliveryBinding.btUndeliver.setVisibility(View.GONE);
                activityForwardSecureDeliveryBinding.textchng.setVisibility(View.GONE);
            }
        } else {
            Intent intent = RvpQcListActivity.getStartIntent(this);
            intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstnKey);
            intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
            intent.putExtra(Constants.DRS_PIN, getDrsPin);
            intent.putExtra(Constants.OFD_OTP, commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentDetails().getOfd_otp());
            intent.putExtra("call_allowed", commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getCallAllowed());
            intent.putExtra(Constants.COMPOSITE_KEY, commonDRSListItem.getDrsReverseQCTypeResponse().getCompositeKey());
            intent.putExtra(Constants.DRS_ID, commonDRSListItem.getDrsReverseQCTypeResponse().getDrs());
            intent.putExtra(Constants.DRS_ID_NUM, String.valueOf(commonDRSListItem.getDrsReverseQCTypeResponse().getDrs()));
            intent.putExtra(Constants.INTENT_KEY, commonDRSListItem.getDrsReverseQCTypeResponse().getAwbNo());
            startActivity(intent);
            applyTransitionToOpenActivity(this);
        }
    }

    public void onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
            applyTransitionToBackFromActivity(this);
        } else {
            showSnackbar("BackButton is disabled until the timer is off.");
        }
    }

    //On click of back button

    public void onBack() {
        if (shouldAllowBack()) {
            super.onBackPressed();
            applyTransitionToBackFromActivity(this);
        } else {
            showSnackbar("BackButton is disabled until the timer is off.");
        }
    }

    private boolean shouldAllowBack() {
        return activityForwardSecureDeliveryBinding.resend.getText().toString().equalsIgnoreCase("RESEND");
    }

    //Setting Otp layout Visible
    @Override
    public void onOTPLayoutVisibile() {
    }

    @Override
    public void showerrorMessage(String error) {
        if (error.contains("HTTP 500"))
            showSnackbar(getString(R.string.http_500_msg));
        else if (error.equalsIgnoreCase("Invalid Authentication Token.")) {
            showSnackbar(error);
            secureDeliveryViewModel.logoutLocal();
        } else
            showSnackbar(error);
    }

    @Override
    public void onPinBVerifyClick() {
        hideKeyboard(SecureDeliveryActivity.this);
        try {
            if (!activityForwardSecureDeliveryBinding.enterPinB.getText().toString().equalsIgnoreCase("Click here to scan")) {
                if (isNetworkConnected()) {
                    secureDeliveryViewModel.onOtpOnVerifyApiCall(SecureDeliveryActivity.this, activityForwardSecureDeliveryBinding.awb.getText().toString(), activityForwardSecureDeliveryBinding.enterPinB.getText().toString(), Constants.VERIFY_PINB, shipmentType);
                } else {
                    if (secureDeliveryViewModel.isSecurePinbOffline.get()) {
                        if (secureDeliveryViewModel.securePinbValue.get() != null) {
                            if (activityForwardSecureDeliveryBinding.enterPinB.getText().toString().equalsIgnoreCase(secureDeliveryViewModel.securePinbValue.get())) {
                                openDetailActivity();
                            } else {
                                showSnackbar("Incorrect Value");
                            }
                        }
                    } else {
                        showSnackbar("No Internet Connection. Offline support not available");
                    }
                }
            } else {
                showSnackbar("Please Scan to Verify");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void onRVPUndelivered(RvpCommit rvpCommit) {
        try {
            Intent intent;
            intent = new Intent(this, RVPUndeliveredActivity.class);
            intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstnKey);
            intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
            intent.putExtra(Constants.DRS_PIN, getDrsPin);
            intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
            intent.putExtra("awb", awbNo);
            intent.putExtra("call_allowed", call_allowed);
            intent.putExtra(Constants.DRS_ID_NUM, drs_id_num);
            intent.putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, consignee_alternate_mobile);
            intent.putExtra(INTENT_KEY_RVP_WITH_QC, secureDeliveryViewModel.getRvpWithQc());
            intent.putExtra(Constants.SHIPMENT_TYPE, secureDeliveryViewModel.getRvpWithQc().drsReverseQCTypeResponse.getShipmentDetails().getType());
            startActivity(intent);
            applyTransitionToOpenActivity(this);
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void onEDSUndelivered(EdsCommit edsCommit) {
        try {
            Intent intent;
            intent = new Intent(this, EDSUndeliveredActivity.class);
            intent.putParcelableArrayListExtra(getString(R.string.data), null);
            intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstnKey);
            intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
            intent.putExtra(Constants.DRS_PIN, getDrsPin);
            intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
            intent.putExtra(Constants.RESCHEDULE_ENABLE, rescudeEnable);
            intent.putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, consignee_alternate_mobile);
            intent.putExtra("edsResponse", secureDeliveryViewModel.edsWithActivityList());
            intent.putExtra("awb", awbNo);
            intent.putExtra("call_allowed", call_allowed);
            intent.putExtra("navigator", "OTP");
            startActivity(intent);
            applyTransitionToOpenActivity(this);
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void mResultReceiver1(String strBarcodeScan) {
    }

    @Override
    public void sendEncryptedOtp(String otp) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!otp.equalsIgnoreCase("")) {
                    amazon_encrypted_otp = otp;
                    activityForwardSecureDeliveryBinding.isotpLayout.setVisibility(View.VISIBLE);
                    activityForwardSecureDeliveryBinding.isPinLayout.setVisibility(View.GONE);
                    activityForwardSecureDeliveryBinding.isPinBLayout.setVisibility(View.GONE);
                    activityForwardSecureDeliveryBinding.isGenerateOtpLayout.setVisibility(View.GONE);
                } else {
                    openDetailActivity();
                }
            }
        });
    }

    @Override
    public void callAmazon() {
        secureDeliveryViewModel.getAmazonEncryptedOTP(SecureDeliveryActivity.this, activityForwardSecureDeliveryBinding.awb.getText().toString());
    }

    @Override
    public void onOtherMobile(boolean isSuccess) {
        if (amazon != null && amazon.equalsIgnoreCase("true")) {
            activityForwardSecureDeliveryBinding.resend.setVisibility(View.INVISIBLE);
        } else if (isDelightShipment) {
            activityForwardSecureDeliveryBinding.resend.setVisibility(View.INVISIBLE);
        } else {
            activityForwardSecureDeliveryBinding.resend.setVisibility(View.VISIBLE);
        }
        activityForwardSecureDeliveryBinding.changeNoLayout.setVisibility(View.GONE);
        activityForwardSecureDeliveryBinding.isGenerateOtpLayout.setVisibility(View.GONE);
        activityForwardSecureDeliveryBinding.otherNumber.setVisibility(View.VISIBLE);
        activityForwardSecureDeliveryBinding.isotpLayout.setVisibility(View.VISIBLE);
        activityForwardSecureDeliveryBinding.isPinLayout.setVisibility(View.GONE);
        activityForwardSecureDeliveryBinding.isPinBLayout.setVisibility(View.GONE);
    }

    @Override
    public void onMobileNoChange() {
        if (activityForwardSecureDeliveryBinding.mobileEdt.getText().toString().length() == 10)
            secureDeliveryViewModel.onsendOtpOnOtherNo(activityForwardSecureDeliveryBinding.mobileEdt.getText().toString(), awbNo);
        else {
            onUiThread("Invalid Mobile No.");
        }
    }

    @Override
    public void clickOnOtherNumber() {
        activityForwardSecureDeliveryBinding.changeNoLayout.setVisibility(View.VISIBLE);
        activityForwardSecureDeliveryBinding.isGenerateOtpLayout.setVisibility(View.GONE);
        activityForwardSecureDeliveryBinding.resend.setVisibility(View.INVISIBLE);
        activityForwardSecureDeliveryBinding.otherNumber.setVisibility(View.GONE);
        activityForwardSecureDeliveryBinding.isotpLayout.setVisibility(View.GONE);
        activityForwardSecureDeliveryBinding.isPinLayout.setVisibility(View.GONE);
        activityForwardSecureDeliveryBinding.isPinBLayout.setVisibility(View.GONE);
    }

    @Override
    public Context getActivity() {
        return this;
    }

    @Override
    public void resendSMS(Boolean alternateclick) {
        hideKeyboard(SecureDeliveryActivity.this);
        try {
            if (isNetworkConnected()) {
                if (amazon != null && amazon.equalsIgnoreCase("true")) {
                    if (amazon_encrypted_otp != null && (!amazon_encrypted_otp.equalsIgnoreCase("") || amazon_encrypted_otp != null)) {
                        secureDeliveryViewModel.OnResendApiCall(SecureDeliveryActivity.this, activityForwardSecureDeliveryBinding.awb.getText().toString(), is_cash_collection, consignee_mobile, drs_id_num, alternateclick);
                    } else {
                        secureDeliveryViewModel.getAmazonEncryptedOTP(SecureDeliveryActivity.this, activityForwardSecureDeliveryBinding.awb.getText().toString());
                    }
                } else {
                    secureDeliveryViewModel.OnResendApiCall(SecureDeliveryActivity.this, activityForwardSecureDeliveryBinding.awb.getText().toString(), is_cash_collection, consignee_mobile, drs_id_num, alternateclick);
                }
            } else {
                showSnackbar(getString(R.string.check_internet));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
        new CountDownTimer(secureDeliveryViewModel.getDataManager().getDisableResendOtpButtonDuration() * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                activityForwardSecureDeliveryBinding.resend.setEnabled(false);
                activityForwardSecureDeliveryBinding.resend.setTextColor(getResources().getColor(R.color.ecomBlue));
                String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                System.out.println(hms);
                activityForwardSecureDeliveryBinding.resend.setText(hms);
            }

            @Override
            public void onFinish() {
                activityForwardSecureDeliveryBinding.resend.setEnabled(true);
                activityForwardSecureDeliveryBinding.resend.setText(getResources().getString(R.string.resend));
            }
        }.start();
    }

    @Override
    public void resendVoiceCall() {
        new CountDownTimer(secureDeliveryViewModel.getDataManager().getDisableResendOtpButtonDuration() * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                activityForwardSecureDeliveryBinding.resend.setEnabled(false);
                activityForwardSecureDeliveryBinding.resend.setTextColor(getResources().getColor(R.color.ecomBlue));
                String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                System.out.println(hms);
                activityForwardSecureDeliveryBinding.resend.setText(hms);
            }

            @Override
            public void onFinish() {
                activityForwardSecureDeliveryBinding.resend.setEnabled(true);
                activityForwardSecureDeliveryBinding.resend.setText(getResources().getString(R.string.resend));
            }
        }.start();
    }

    @Override
    public void checkDisputedStatus(Boolean aBoolean) {

    }

    public void onUiThread(String description) {
        try {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(SecureDeliveryActivity.this, description, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onScanClick() {
        Intent intent = new Intent(SecureDeliveryActivity.this, ScannerActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
        if (SecureDeliveryViewModel.device.equals(Constants.NEWLAND)) {
            IntentFilter intFilter = new IntentFilter(ScanManager.ACTION_SEND_SCAN_RESULT);
            registerReceiver(secureDeliveryViewModel.mResultReceiver(), intFilter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);


            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == MY_REQUEST_CODE) {
                    if (data != null) {

                        ScanValue = data.getStringExtra("value");
                        manualEnterBP = data.getStringExtra("manualBP");

                        activityForwardSecureDeliveryBinding.btBpID.setText("Proceed");
                        secureDeliveryViewModel.onBPIDClick();

                    }

                }
            }

            if (requestCode == REQUEST_CODE_SCAN) {
                handleScanResult(data);
            } else {
                barcodeHandler.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    private void handleScanResult(Intent data) {
        if (data != null) {
            String getScannedText = data.getStringExtra(ScannerActivity.SCANNED_CODE);
            if (getScannedText.isEmpty()) {
                showSnackbar("No data found");
            } else {
                activityForwardSecureDeliveryBinding.enterPinB.setText(getScannedText);
                if (!activityForwardSecureDeliveryBinding.enterPinB.getText().toString().equalsIgnoreCase("Click here to scan")) {
                    if (secureDeliveryViewModel.isSecurePinbOffline.get()) {
                        if (secureDeliveryViewModel.securePinbValue.get() != null) {
                            secureDeliveryViewModel.getDataManager().setAmazonPinbValue(activityForwardSecureDeliveryBinding.enterPinB.getText().toString());
                            if (activityForwardSecureDeliveryBinding.enterPinB.getText().toString().equalsIgnoreCase(secureDeliveryViewModel.securePinbValue.get())) {
                                secureDeliveryViewModel.getDataManager().setPinBOTPStatus("true");
                                secureDeliveryViewModel.getDataManager().setPinBOTPTimming(Calendar.getInstance().getTimeInMillis());
                                openDetailActivity();
                            } else {
                                secureDeliveryViewModel.getDataManager().setPinBOTPStatus("false");
                                secureDeliveryViewModel.getDataManager().setPinBOTPTimming(Calendar.getInstance().getTimeInMillis());
                                showSnackbar("Incorrect Value");
                            }
                        } else {
                            secureDeliveryViewModel.getDataManager().setPinBOTPStatus("false");
                            secureDeliveryViewModel.getDataManager().setPinBOTPTimming(Calendar.getInstance().getTimeInMillis());
                            showSnackbar("Value Mismatched.");
                        }
                        ForwardCommit.Amz_Scan amz_scan = new ForwardCommit.Amz_Scan();
                        amz_scan.setAmz_pinb_status(secureDeliveryViewModel.getDataManager().getPinBOTPStatus());
                        amz_scan.setAmz_pinb_value(secureDeliveryViewModel.getDataManager().getAmazonPinbValue());
                        amz_scan.setPinb_verified_time(secureDeliveryViewModel.getDataManager().getPinBOTPTimming());
                        secureDeliveryViewModel.amz_scanArrayList.add(amz_scan);
                        Gson gson = new Gson();
                        String amazon_lis = gson.toJson(secureDeliveryViewModel.amz_scanArrayList);
                        secureDeliveryViewModel.getDataManager().setAmazonList(amazon_lis);
                    } else {
                        openDetailActivity();
                    }
                } else {
                    showSnackbar("Please Scan to Verify");
                }
            }
        } else {
            showSnackbar("No data found");
        }
    }

    @Override
    public void onPinVerifyButton() {
        hideKeyboard(SecureDeliveryActivity.this);
        try {
            if (isNetworkConnected()) {
                if (!activityForwardSecureDeliveryBinding.enterPin.getText().toString().isEmpty() && activityForwardSecureDeliveryBinding.enterPin.getText().toString().length() < 20) {
                    secureDeliveryViewModel.onOtpOnVerifyApiCall(SecureDeliveryActivity.this, activityForwardSecureDeliveryBinding.awb.getText().toString(), activityForwardSecureDeliveryBinding.enterPin.getText().toString(), Constants.VERIFY_PIN, shipmentType);
                } else if (activityForwardSecureDeliveryBinding.enterPin.getText().toString().isEmpty()) {
                    showSnackbar(getString(R.string.Invalid_pin_entered));
                } else if (activityForwardSecureDeliveryBinding.enterPin.getText().toString().length() > 20) {
                    showSnackbar(getString(R.string.Invalid_pin_entered));
                }
            } else {
                showSnackbar(getString(R.string.check_internet));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    public void handleLayoutVisibility() {
        try {
            if (amazon != null && amazon.equalsIgnoreCase("true") && (amazon_encrypted_otp.equalsIgnoreCase("") || amazon_encrypted_otp == null)) {
                activityForwardSecureDeliveryBinding.isGenerateOtpLayout.setVisibility(View.VISIBLE);
                activityForwardSecureDeliveryBinding.resend.setVisibility(View.INVISIBLE);
                activityForwardSecureDeliveryBinding.changeNoLayout.setVisibility(View.GONE);
                activityForwardSecureDeliveryBinding.otherNumber.setVisibility(View.GONE);
                activityForwardSecureDeliveryBinding.isotpLayout.setVisibility(View.GONE);
                activityForwardSecureDeliveryBinding.isPinLayout.setVisibility(View.GONE);
                activityForwardSecureDeliveryBinding.isPinBLayout.setVisibility(View.GONE);
            } else if (secureDeliveryViewModel.isSecureOtp.get() || amazon != null && amazon.equalsIgnoreCase("true")) {

                activityForwardSecureDeliveryBinding.isotpLayout.setVisibility(View.VISIBLE);
                if (is_cash_collection) {
                    activityForwardSecureDeliveryBinding.otherNumber.setVisibility(View.VISIBLE);
                } else {
                    activityForwardSecureDeliveryBinding.otherNumber.setVisibility(View.GONE);
                }
                if (amazon != null && amazon.equalsIgnoreCase("true") && (!amazon_encrypted_otp.equalsIgnoreCase("") || amazon_encrypted_otp != null)) {
                    activityForwardSecureDeliveryBinding.resend.setVisibility(View.INVISIBLE);
                } else if (isDelightShipment) {
                    activityForwardSecureDeliveryBinding.enterOtp.setHint(getResources().getString(R.string._dlight_enter_the_otp));
                    activityForwardSecureDeliveryBinding.resend.setVisibility(View.INVISIBLE);
                } else {
                    activityForwardSecureDeliveryBinding.resend.setVisibility(View.VISIBLE);
                }
                activityForwardSecureDeliveryBinding.isPinLayout.setVisibility(View.GONE);
                activityForwardSecureDeliveryBinding.isPinBLayout.setVisibility(View.GONE);
                activityForwardSecureDeliveryBinding.changeNoLayout.setVisibility(View.GONE);
                activityForwardSecureDeliveryBinding.isGenerateOtpLayout.setVisibility(View.GONE);
            } else if (secureDeliveryViewModel.isSecurePin.get()) {
                activityForwardSecureDeliveryBinding.isotpLayout.setVisibility(View.GONE);
                activityForwardSecureDeliveryBinding.isPinLayout.setVisibility(View.VISIBLE);
                activityForwardSecureDeliveryBinding.resend.setVisibility(View.VISIBLE);
                activityForwardSecureDeliveryBinding.otherNumber.setVisibility(View.GONE);
                activityForwardSecureDeliveryBinding.changeNoLayout.setVisibility(View.GONE);
                activityForwardSecureDeliveryBinding.isPinBLayout.setVisibility(View.GONE);
                activityForwardSecureDeliveryBinding.isGenerateOtpLayout.setVisibility(View.GONE);
            } else if (secureDeliveryViewModel.isSecurePinB.get()) {
                activityForwardSecureDeliveryBinding.isotpLayout.setVisibility(View.GONE);
                activityForwardSecureDeliveryBinding.resend.setVisibility(View.VISIBLE);
                activityForwardSecureDeliveryBinding.otherNumber.setVisibility(View.GONE);
                activityForwardSecureDeliveryBinding.changeNoLayout.setVisibility(View.GONE);
                activityForwardSecureDeliveryBinding.isPinLayout.setVisibility(View.GONE);
                activityForwardSecureDeliveryBinding.isGenerateOtpLayout.setVisibility(View.GONE);
                activityForwardSecureDeliveryBinding.isPinBLayout.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void onOtpResendSuccess() {
        if (shipmentType.equalsIgnoreCase("EDS")) {
            showSnackbar(getString(R.string.ref_resend_successfully));
        } else {
            showSnackbar(getString(R.string.otp_resend_successfully));
        }
    }

    @Override
    public void onOtpVerifySuccess(String type) {
        try {

            Constants.OFD_OTP_VERIFIED = true;
            Constants.PLAIN_OTP = activityForwardSecureDeliveryBinding.enterOtp.getText().toString();
            if (type.equalsIgnoreCase(Constants.VERIFY_OTP)) {
                if (secureDeliveryViewModel.isSecurePin.get()) {
                    activityForwardSecureDeliveryBinding.isotpLayout.setVisibility(View.GONE);
                    activityForwardSecureDeliveryBinding.isPinLayout.setVisibility(View.VISIBLE);
                    activityForwardSecureDeliveryBinding.resend.setVisibility(View.VISIBLE);
                    activityForwardSecureDeliveryBinding.isPinBLayout.setVisibility(View.GONE);
                    activityForwardSecureDeliveryBinding.isGenerateOtpLayout.setVisibility(View.GONE);
                    Constants.TYPE = Constants.VERIFY_PIN;
                } else if (secureDeliveryViewModel.isSecurePinB.get()) {
                    activityForwardSecureDeliveryBinding.isotpLayout.setVisibility(View.GONE);
                    activityForwardSecureDeliveryBinding.isPinLayout.setVisibility(View.GONE);
                    activityForwardSecureDeliveryBinding.resend.setVisibility(View.VISIBLE);
                    activityForwardSecureDeliveryBinding.isPinBLayout.setVisibility(View.VISIBLE);
                    activityForwardSecureDeliveryBinding.isGenerateOtpLayout.setVisibility(View.GONE);
                    Constants.TYPE = Constants.VERIFY_PINB;
                } else {

                    if (mpsShipment != null) {
                        if (getIntent().getExtras().getString(Constants.MPS).equalsIgnoreCase("P")) {
                            openDetailActivity();
                        }
                    } else {
                        if (ScanValue != "" || shipmentType.equalsIgnoreCase(Constants.RVP) || shipmentType.equalsIgnoreCase(Constants.EDS)) {
                            openDetailActivity();
                        } else {
                            if (return_package_barcode.equals("")) {
                                openDetailActivity();
                            } else {
                                showSnackbar("OTP Verified");
                                activityForwardSecureDeliveryBinding.isotpLayout.setVisibility(View.GONE);
                                activityForwardSecureDeliveryBinding.btBpID.setEnabled(true);
                                activityForwardSecureDeliveryBinding.btBpID.setAlpha(1f);
                            }
                        }
                    }

                }
            } else if (type.equalsIgnoreCase(Constants.VERIFY_PIN)) {
                if (secureDeliveryViewModel.isSecurePinB.get()) {
                    activityForwardSecureDeliveryBinding.isotpLayout.setVisibility(View.GONE);
                    activityForwardSecureDeliveryBinding.isPinLayout.setVisibility(View.GONE);
                    activityForwardSecureDeliveryBinding.resend.setVisibility(View.VISIBLE);
                    activityForwardSecureDeliveryBinding.isGenerateOtpLayout.setVisibility(View.GONE);
                    activityForwardSecureDeliveryBinding.isPinBLayout.setVisibility(View.VISIBLE);
                    Constants.TYPE = Constants.VERIFY_PINB;
                } else {
                    openDetailActivity();
                }
            } else if (type.equalsIgnoreCase(Constants.VERIFY_PINB)) {
                openDetailActivity();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    private void openDetailActivity() {
        try {
            PreferenceUtils.writePreferenceValue(context, awbNo + "secure", true);
            Intent intent = new Intent();
            if (shipmentType.equalsIgnoreCase(Constants.FWD)) {
                if (mpsShipment != null) {
                    if (getIntent().getExtras().getString(Constants.MPS).equalsIgnoreCase("P")) {
                        intent = MPSScanActivity.getStartIntent(this);
                        intent.putExtra(Constants.MPS, getIntent().getExtras().getString(Constants.MPS));
                        intent.putExtra(Constants.MPS_AWB_NOS, getIntent().getExtras().getString(Constants.MPS_AWB_NOS));
                        intent.putExtra(Constants.ORDER_ID, order_id);
                        intent.putExtra(Constants.DRS_ID_NUM, Integer.parseInt(drs_id_num));
                        intent.putExtra(Constants.OFD_OTP, getIntent().getExtras().getString(Constants.OFD_OTP));
                        intent.putExtra(Constants.CONSIGNEE_MOBILE, getIntent().getExtras().getString(Constants.CONSIGNEE_MOBILE));
                        intent.putExtra("call_allowed", getIntent().getExtras().getString("call_allowed"));
                        intent.putExtra(Constants.sign_image_required, sign_image_required);
                        intent.putExtra(Constants.FWD_DEL_IMAGE, fwd_del_image);
                        intent.putExtra(Constants.SECURE_DELIVERY, isSecureDelivery);
                        intent.putExtra(Constants.SHIPMENT_TYPE, Constants.FWD);
                        intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstnKey);
                        intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
                        intent.putExtra(Constants.DRS_PIN, getDrsPin);
                        intent.putExtra(Constants.IS_AMAZON_RESHEDUCLE_ENABLE, is_amazon_schedule_enable);
                        intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                        intent.putExtra(Constants.IS_CARD, isDigitalPaymentAllowed);
                        intent.putExtra(Constants.INTENT_KEY, awbNo);
                        startActivity(intent);
                        applyTransitionToOpenActivity(this);
                    }
                } else if (activityForwardSecureDeliveryBinding.type.getText().toString().equalsIgnoreCase("ppd")) {

                    FWDActivitiesData fwdActivitiesData = new FWDActivitiesData();
                    fwdActivitiesData.setType("ppd");
                    fwdActivitiesData.setAmount(secureDeliveryViewModel.mDrsForwardTypeResponse.getShipmentDetails().getDeclaredValue().toString());
                    fwdActivitiesData.setChange("0.0");
                    fwdActivitiesData.setSign_image_required(sign_image_required);
                    fwdActivitiesData.setAwbNo(awbNo);
                    fwdActivitiesData.setCompositeKey(composite_key);
                    fwdActivitiesData.setOrderId(order_id);
                    fwdActivitiesData.setReturn_package_barcode(manualEnterBP);
                    fwdActivitiesData.setScanValue(ScanValue);
                    fwdActivitiesData.setFwd_del_image(fwd_del_image);
                    intent = SignatureActivity.getStartIntent(this);

                    forwardCommit.setShipment_type(secureDeliveryViewModel.mDrsForwardTypeResponse.getShipmentType());
                    forwardCommit.setPayment_mode("PPD");
                    forwardCommit.setDrs_date(String.valueOf(secureDeliveryViewModel.mDrsForwardTypeResponse.getAssignedDate()));
                    forwardCommit.setAwb(secureDeliveryViewModel.getItemAwb().get());
                    forwardCommit.setDrs_id(secureDeliveryViewModel.mDrsForwardTypeResponse.getDrsId() + "");
                    forwardCommit.setLocation_lat(String.valueOf(secureDeliveryViewModel.getDataManager().getCurrentLatitude()));
                    forwardCommit.setLocation_long(String.valueOf(secureDeliveryViewModel.getDataManager().getCurrentLongitude()));
                    forwardCommit.setConsignee_name(secureDeliveryViewModel.mDrsForwardTypeResponse.getConsigneeDetails().getName());
                    forwardCommit.setAttempt_reason_code(Constants.FORWARD_COMMIT_REASON_CODE);
                    forwardCommit.setDeclared_value(secureDeliveryViewModel.mDrsForwardTypeResponse.getShipmentDetails().getDeclaredValue().toString());
                    intent.putExtra("fwdActivitiesData", fwdActivitiesData);
                    intent.putExtra("data", forwardCommit);
                    startActivity(intent);
                    applyTransitionToOpenActivity(this);
                } else {

                    FWDActivitiesData fwdActivitiesData = new FWDActivitiesData();
                    fwdActivitiesData.setDrsPstnKey(getDrsPstnKey);
                    fwdActivitiesData.setDrsApiKey(getDrsApiKey);
                    fwdActivitiesData.setDrsPin(getDrsPin);
                    fwdActivitiesData.setCard(isDigitalPaymentAllowed);
                    fwdActivitiesData.setAwbNo(awbNo);
                    fwdActivitiesData.setCallAllowed(call_allowed);
                    fwdActivitiesData.setOrderId(order_id);
                    fwdActivitiesData.setSign_image_required(sign_image_required);
                    fwdActivitiesData.setResend_otp_enable(resend_secure_otp);
                    fwdActivitiesData.setCompositeKey(composite_key);
                    fwdActivitiesData.setDrsId(Integer.parseInt(drs_id_num));
                    fwdActivitiesData.setShow_fwd_undl_btn(shw_fwd_undl_btn);
                    fwdActivitiesData.setConsignee_alternate_number(consignee_alternate_mobile);
                    fwdActivitiesData.setReturn_package_barcode(manualEnterBP);
                    fwdActivitiesData.setScanValue(ScanValue);
                    fwdActivitiesData.setIs_amazon_reschedule_enabled(is_amazon_schedule_enable);
                    fwdActivitiesData.setFwd_del_image(fwd_del_image);

                    intent = ForwardDetailActivity.getStartIntent(context);
                    intent.putExtra("fwdActivitiesData", fwdActivitiesData);
                    startActivity(intent);
                    applyTransitionToOpenActivity(this);


                }
            } else if (shipmentType.equalsIgnoreCase(Constants.RVP)) {
                intent = RvpQcListActivity.getStartIntent(context);
                intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstnKey);
                intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
                intent.putExtra("call_allowed", call_allowed);
                intent.putExtra(Constants.DRS_PIN, getDrsPin);
                intent.putExtra(Constants.DRS_ID_NUM, drs_id_num);
                intent.putExtra(Constants.sign_image_required, sign_image_required);
                intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                intent.putExtra(Constants.INTENT_KEY, awbNo);
                startActivity(intent);
                applyTransitionToOpenActivity(this);
            } else if (shipmentType.equalsIgnoreCase(Constants.EDS)) {
                intent = EdsTaskListActivity.getStartIntent(context);
                intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstnKey);
                intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
                intent.putExtra(Constants.DRS_PIN, getDrsPin);
                intent.putExtra(Constants.sign_image_required, sign_image_required);
                intent.putExtra("call_allowed", call_allowed);
                intent.putExtra(Constants.INTENT_KEY, awbNo);
                intent.putExtra(Constants.ORDER_ID, order_id);
                intent.putExtra(Constants.IS_KYC_ACTIVE, is_kyc_active);
                intent.putExtra(Constants.RESCHEDULE_ENABLE, rescudeEnable);
                intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                intent.putExtra(Constants.INTENT_KEY1, new_Awb);
                startActivity(intent);
                applyTransitionToOpenActivity(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void onHandleError(String errorResponse) {
        showSnackbar(errorResponse);
        Log.d("errorResponse", errorResponse);
        if (errorResponse.equalsIgnoreCase("Invalid Authentication Token.")) {
            secureDeliveryViewModel.logoutLocal();
        }
    }

    @Override
    public void getAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_Material3_Light_Dialog_Alert);
        builder.setCancelable(false);
        builder.setMessage("Max Attempt Failed.You are being navigated to Undelivered Page.");
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    if (shipmentType.equalsIgnoreCase(Constants.FWD)) {
                        forwardCommit.setPayment_mode("");
                        forwardCommit.setAttempt_reason_code(Constants.FORWARD_COMMIT_REASON_CODE);
                        forwardCommit.setDeclared_value(secureDeliveryViewModel.mDrsForwardTypeResponse.getShipmentDetails().getDeclaredValue().toString());
                        forwardCommit.setDrs_date(String.valueOf(secureDeliveryViewModel.mDrsForwardTypeResponse.getAssignedDate()));
                        forwardCommit.setAwb(secureDeliveryViewModel.getItemAwb().get());
                        forwardCommit.setDeclared_value(String.valueOf(secureDeliveryViewModel.mDrsForwardTypeResponse.getShipmentDetails().getDeclaredValue()));
                        forwardCommit.setDrs_id(secureDeliveryViewModel.mDrsForwardTypeResponse.getDrsId() + "");
                        forwardCommit.setLocation_lat(String.valueOf(secureDeliveryViewModel.getDataManager().getCurrentLatitude()));
                        forwardCommit.setLocation_long(String.valueOf(secureDeliveryViewModel.getDataManager().getCurrentLongitude()));
                        forwardCommit.setConsignee_name(secureDeliveryViewModel.mDrsForwardTypeResponse.getConsigneeDetails().getName());
                        forwardCommit.setShipment_type(Constants.SHIPMENT_TYPE_FORWARD);
                        forwardCommit.setAttempt_reason_code(Constants.FORWARD_COMMIT_REASON_CODE);
                        forwardCommit.setDeclared_value(secureDeliveryViewModel.mDrsForwardTypeResponse.getShipmentDetails().getDeclaredValue().toString());

                        FWDActivitiesData fwdActivitiesData = new FWDActivitiesData();
                        fwdActivitiesData.setDrsPin(getDrsPin);
                        fwdActivitiesData.setDrsApiKey(getDrsApiKey);
                        fwdActivitiesData.setDrsPstnKey(getDrsPstnKey);
                        fwdActivitiesData.setOrderId(getOrderId);
                        fwdActivitiesData.setCallAllowed(call_allowed);
                        fwdActivitiesData.setConsignee_mobile(consignee_mobile);
                        fwdActivitiesData.setConsignee_alternate_number(consignee_alternate_mobile);
                        fwdActivitiesData.setDrsId(Integer.parseInt(drs_id_num));
                        fwdActivitiesData.setAwbNo(Long.parseLong(forwardCommit.getAwb()));
                        fwdActivitiesData.setCompositeKey(composite_key);
                        fwdActivitiesData.setIs_amazon_reschedule_enabled(is_amazon_schedule_enable);
                        fwdActivitiesData.setShipment_type(activityForwardSecureDeliveryBinding.type.getText().toString());
                        fwdActivitiesData.setSecure_undelivered(String.valueOf(secureDeliveryViewModel.isSecureOtp.get()));
                        fwdActivitiesData.setCollected_value(secureDeliveryViewModel.getcollectablevalue());

                        Intent intent = UndeliveredActivity.getStartIntent(getApplicationContext());
                        intent.putExtra("data", forwardCommit);
                        intent.putExtra("fwdActivitiesData", fwdActivitiesData);
                        startActivity(intent);
                        applyTransitionToOpenActivity(SecureDeliveryActivity.this);
                    }
                    if (shipmentType.equalsIgnoreCase(Constants.RVP)) {
                        getundeliverRvp();

                    }
                    if (shipmentType.equalsIgnoreCase(Constants.EDS)) {
                        getundeliver();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showSnackbar(e.getMessage());
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void getundeliverRvp() {
        Intent intent;
        intent = new Intent(this, RVPUndeliveredActivity.class);
        intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstnKey);
        intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
        intent.putExtra(Constants.DRS_PIN, getDrsPin);
        intent.putExtra("call_allowed", call_allowed);
        intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
        intent.putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, consignee_alternate_mobile);
        intent.putExtra("awb", awbNo);
        intent.putExtra(INTENT_KEY_RVP_WITH_QC, secureDeliveryViewModel.getRvpWithQc());
        intent.putExtra(Constants.SHIPMENT_TYPE, secureDeliveryViewModel.getRvpWithQc().drsReverseQCTypeResponse.getShipmentDetails().getType());
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    public void getundeliver() {
        Intent intent;
        intent = new Intent(this, EDSUndeliveredActivity.class);
        intent.putParcelableArrayListExtra(getString(R.string.data), null);
        intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstnKey);
        intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
        intent.putExtra(Constants.DRS_PIN, getDrsPin);
        intent.putExtra("call_allowed", call_allowed);
        intent.putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, consignee_alternate_mobile);
        intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
        intent.putExtra("edsResponse", secureDeliveryViewModel.edsWithActivityList());
        intent.putExtra("awb", awbNo);
        intent.putExtra("navigator", "act_list");
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    @Override
    public void clearStack() {
        Intent intent = new Intent(SecureDeliveryActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    //Calling api on click of verify button of otp
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onOtpVerifyButton() {
        hideKeyboard(SecureDeliveryActivity.this);
        if (Constants.OFD_OTP_VERIFIED && !ScanValue.equals("")) {

            activityForwardSecureDeliveryBinding.enterOtp.setText(Constants.PLAIN_OTP);
        }
        try {
            if (activityForwardSecureDeliveryBinding.enterOtp.getText().toString().trim().length() < 4) {
                showSnackbar(getString(R.string.Invalid_otp_entered));
            } else if (amazon != null && amazon.equalsIgnoreCase("true")) {
                if (amazon_encrypted_otp != null) {
                    if (secureDeliveryViewModel.verifyEncryptedOTP(amazon_encrypted_otp, activityForwardSecureDeliveryBinding.awb.getText().toString(), activityForwardSecureDeliveryBinding.enterOtp.getText().toString())) {
                        onOtpVerifySuccess(Constants.VERIFY_OTP);
                    } else {
                        showSnackbar(getString(R.string.Invalid_otp_entered));
                    }
                } else {
                    showSnackbar(getResources().getString(R.string.please_generate_otp));
                }
            } else {
                if (OFD_OTP != null && !OFD_OTP.equalsIgnoreCase("")) {
                    String encryptText = CommonUtils.decrypt(OFD_OTP, Constants.DECRYPT);
                    Constants.PLAIN_OTP = encryptText;
                    if (encryptText.equalsIgnoreCase(activityForwardSecureDeliveryBinding.enterOtp.getText().toString())) {
                        onOtpVerifySuccess(Constants.VERIFY_OTP);
                        Constants.OFD_OTP_VERIFIED = true;
                    } else {
                        verifyOTP();
                    }
                } else {
                    verifyOTP();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    public void verifyOTP() {
        if (isNetworkConnected()) {
            if (!activityForwardSecureDeliveryBinding.enterOtp.getText().toString().isEmpty() && activityForwardSecureDeliveryBinding.enterOtp.getText().toString().length() > 0) {
                Constants.TYPE = Constants.VERIFY_OTP;
                if (amazon != null && amazon.equalsIgnoreCase("true")) {
                    if (amazon_encrypted_otp != null) {
                        if (secureDeliveryViewModel.verifyEncryptedOTP(amazon_encrypted_otp, activityForwardSecureDeliveryBinding.awb.getText().toString(), activityForwardSecureDeliveryBinding.enterOtp.getText().toString())) {
                            onOtpVerifySuccess(Constants.VERIFY_OTP);
                        } else {
                            showSnackbar(getString(R.string.Invalid_otp_entered));
                        }
                    } else {
                        showSnackbar(getResources().getString(R.string.please_generate_otp));
                    }
                } else if (isDelightShipment) {
                    if (delight_encrypted_otp1 != null && !delight_encrypted_otp1.equalsIgnoreCase("")) {
                        if (secureDeliveryViewModel.verifyDlightEncryptedOTP(delight_encrypted_otp1, activityForwardSecureDeliveryBinding.enterOtp.getText().toString())) {
                            secureDeliveryViewModel.getDataManager().setDlightSuccessEncrptedOTPType(activityForwardSecureDeliveryBinding.enterOtp.getText().toString());
                            onOtpVerifySuccess(Constants.VERIFY_OTP);
                        } else if (secureDeliveryViewModel.verifyDlightEncryptedOTP(delight_encrypted_otp2, activityForwardSecureDeliveryBinding.enterOtp.getText().toString())) {
                            secureDeliveryViewModel.getDataManager().setDlightSuccessEncrptedOTPType(activityForwardSecureDeliveryBinding.enterOtp.getText().toString());
                            onOtpVerifySuccess(Constants.VERIFY_OTP);
                        } else {
                            showSnackbar(getString(R.string.Invalid_otp_entered));
                        }
                    } else {
                        showSnackbar(getResources().getString(R.string.please_generate_otp));
                    }
                } else {
                    secureDeliveryViewModel.onOtpOnVerifyApiCall(SecureDeliveryActivity.this, activityForwardSecureDeliveryBinding.awb.getText().toString(), activityForwardSecureDeliveryBinding.enterOtp.getText().toString(), Constants.VERIFY_OTP, shipmentType);
                }
            } else if (activityForwardSecureDeliveryBinding.enterOtp.getText().toString().isEmpty()) {
                showSnackbar(getString(R.string.otp_blank));
            } else if (activityForwardSecureDeliveryBinding.enterOtp.getText().toString().length() > 6) {
                showSnackbar(getString(R.string.Invalid_otp_entered));
            } else {
                showSnackbar(getString(R.string.Invalid_otp_entered));
            }
        } else {
            showSnackbar(getString(R.string.check_internet));
        }

    }

    //Calling api on click of resend button of otp
    @Override
    public void onOtpResendButton() {
        if (amazon != null && amazon.equalsIgnoreCase("true")) {
            if (amazon_encrypted_otp != null && (!amazon_encrypted_otp.equalsIgnoreCase("") || amazon_encrypted_otp != null)) {
                secureDeliveryViewModel.OnResendApiCall(SecureDeliveryActivity.this, activityForwardSecureDeliveryBinding.awb.getText().toString(), is_cash_collection, consignee_mobile, drs_id_num, false);
            } else {
                secureDeliveryViewModel.getAmazonEncryptedOTP(SecureDeliveryActivity.this, activityForwardSecureDeliveryBinding.awb.getText().toString());
            }
        } else {
            if (call_allowed && secureDeliveryViewModel.getDataManager().getVCallPopup()) {
                secureDeliveryViewModel.showCallAndSmsDialog(activityForwardSecureDeliveryBinding.awb.getText().toString(), drs_id_num, consignee_alternate_mobile);
            } else {
                hideKeyboard(SecureDeliveryActivity.this);
                try {
                    if (isNetworkConnected()) {
                        if (amazon != null && amazon.equalsIgnoreCase("true")) {
                            if (amazon_encrypted_otp != null && (!amazon_encrypted_otp.equalsIgnoreCase("") || amazon_encrypted_otp != null)) {
                                secureDeliveryViewModel.OnResendApiCall(SecureDeliveryActivity.this, activityForwardSecureDeliveryBinding.awb.getText().toString(), is_cash_collection, consignee_mobile, drs_id_num, false);
                            } else {
                                secureDeliveryViewModel.getAmazonEncryptedOTP(SecureDeliveryActivity.this, activityForwardSecureDeliveryBinding.awb.getText().toString());
                            }
                        } else {
                            secureDeliveryViewModel.OnResendApiCall(SecureDeliveryActivity.this, activityForwardSecureDeliveryBinding.awb.getText().toString(), is_cash_collection, consignee_mobile, drs_id_num, false);
                        }
                    } else {
                        showSnackbar(getString(R.string.check_internet));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showSnackbar(e.getMessage());
                }
                new CountDownTimer(secureDeliveryViewModel.getDataManager().getDisableResendOtpButtonDuration() * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        activityForwardSecureDeliveryBinding.resend.setEnabled(false);
                        activityForwardSecureDeliveryBinding.resend.setTextColor(getResources().getColor(R.color.ecomBlue));
                        String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                        System.out.println(hms);
                        activityForwardSecureDeliveryBinding.resend.setText(hms);
                    }

                    @Override
                    public void onFinish() {
                        activityForwardSecureDeliveryBinding.resend.setEnabled(true);
                        activityForwardSecureDeliveryBinding.resend.setText(getResources().getString(R.string.resend));
                    }
                }.start();
            }
        }
    }

    @Override
    public void onResult(String s) {
    }
}
