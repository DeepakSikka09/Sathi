package in.ecomexpress.sathi.ui.drs.forward.obd.activity;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.Constants.DRS_DATE;
import static in.ecomexpress.sathi.utils.Constants.SHIPMENT_DECLARED_VALUE;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import javax.inject.Inject;
import javax.inject.Named;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityObdProductDetailBinding;
import in.ecomexpress.sathi.repo.local.data.activitiesdata.FWDActivitiesData;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.SecureDelivery;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.drs.forward.obd.navigator.IOBDProductDetailNavigator;
import in.ecomexpress.sathi.ui.drs.forward.obd.viewmodel.FwdOBDProductDetailViewModel;
import in.ecomexpress.sathi.ui.drs.forward.undelivered_fwd.UndeliveredActivity;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class FwdOBDProductDetailActivity extends BaseActivity<ActivityObdProductDetailBinding, FwdOBDProductDetailViewModel> implements IOBDProductDetailNavigator {

    private final String TAG = FwdOBDProductDetailActivity.class.getSimpleName();
    ActivityObdProductDetailBinding binding;
    @Inject
    FwdOBDProductDetailViewModel fwdOBDProductDetailViewModel;
    @Inject
    ForwardCommit forwardCommit;
    private String consigneeName;
    private String consigneeAddress;
    private String vendorName;
    private String itemName;
    private String addressProfiled;
    private String awbNo;
    private Long longAwbNo;
    private boolean isCard;
    private String amazonEncryptedOtp;
    private String amazon;
    private String ofdOtp;
    private String dLightEncryptedOtp1;
    private String dLightEncryptedOtp2;
    private String consigneeMobile;
    private String consigneeAlternateMobile;
    private boolean resendSecureOtp;
    private boolean isDelightShipment;
    private String orderId;
    private String drsIdNum;
    private boolean signImageRequired;
    private boolean is_amazon_schedule_enable;
    private boolean fwdDelImage;
    private boolean callAllowed;
    private String composite_key;
    private String getDrsApiKey = null, getDrsPstnKey = null, getDrsPin = null, shipmentType = null;
    SecureDelivery isSecureDelivery;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, FwdOBDProductDetailActivity.class);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fwdOBDProductDetailViewModel.setNavigator(this);
        logScreenNameInGoogleAnalytics(TAG, this);
        binding = getViewDataBinding();
        isSecureDelivery = new SecureDelivery();
        getAllDataFromIntent();
        setBackPressed();

        fwdOBDProductDetailViewModel.getFWDShipmentData(forwardCommit, composite_key);
        fwdOBDProductDetailViewModel.getSecureDelivery(isSecureDelivery);

        consigneeName = (consigneeName == null) ? "" : consigneeName;
        vendorName = (vendorName == null) ? "" : vendorName;
        itemName = (itemName == null) ? "" : itemName;
        awbNo = (awbNo == null) ? "" : awbNo;
        addressProfiled = (addressProfiled == null) ? "" : addressProfiled;
        binding.txtAddress.setText(consigneeAddress);
        binding.txtDeliveredName.setText(consigneeName);
        binding.txtPrDescription.setText(itemName);
        binding.awbHeader.awb.setText("AWB: " + awbNo);
        if (addressProfiled.equalsIgnoreCase("Y")) {
            binding.txtVerifiedAddress.setVisibility(View.VISIBLE);
        } else {
            binding.txtVerifiedAddress.setVisibility(View.GONE);
        }
        binding.awbHeader.backArrow.setOnClickListener(view -> {
            onBackPressed();
            applyTransitionToBackFromActivity(this);
        });

        String finalConsigneeName = consigneeName;
        String finalVendorName = vendorName;
        String finalItemName = itemName;
        String finalAwbNo = awbNo;
        String finalAddressProfiled = addressProfiled;

        binding.btn.btnUndeliver.setOnClickListener(view -> fwdOBDProductDetailViewModel.onUndeliveredClick());

        binding.btn.btnDeliver.setOnClickListener(view -> {
            Intent intent1 = new Intent(FwdOBDProductDetailActivity.this, FwdOBDStartOTPActivity.class);
            intent1.putExtra(Constants.OBD_CONSIGNEE_NAME, finalConsigneeName);
            intent1.putExtra(Constants.OBD_CONSIGNEE_ADDRESS, consigneeAddress);
            intent1.putExtra(Constants.OBD_VENDOR_NAME, finalVendorName);
            intent1.putExtra(Constants.OBD_ITEM_NAME, finalItemName);
            intent1.putExtra(Constants.OBD_ADDRESS_PROFILED, finalAddressProfiled);
            intent1.putExtra(Constants.OBD_AWB_NUMBER, finalAwbNo);
            intent1.putExtra(Constants.IS_CARD, isCard);
            intent1.putExtra(Constants.AMAZON_ENCRYPTED_OTP, amazonEncryptedOtp);
            intent1.putExtra(Constants.AMAZON, amazon);
            intent1.putExtra(Constants.OFD_OTP, ofdOtp);
            intent1.putExtra(Constants.DLIGHT_ENCRYPTED_OTP1, dLightEncryptedOtp1);
            intent1.putExtra(Constants.DLIGHT_ENCRYPTED_OTP2, dLightEncryptedOtp2);
            intent1.putExtra(Constants.CONSIGNEE_MOBILE, consigneeMobile);
            intent1.putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, consigneeAlternateMobile);
            intent1.putExtra(Constants.RESEND_SECURE_OTP, resendSecureOtp);
            intent1.putExtra(Constants.ISDELIGHTSHIPMENT, isDelightShipment);
            intent1.putExtra(Constants.ORDER_ID, orderId);
            intent1.putExtra(Constants.DRS_ID_NUM, drsIdNum);
            intent1.putExtra(Constants.sign_image_required, signImageRequired);
            intent1.putExtra(Constants.FWD_DEL_IMAGE, fwdDelImage);
            intent1.putExtra(Constants.COMPOSITE_KEY, composite_key);
            intent1.putExtra(Constants.SHIPMENT_TYPE, shipmentType);
            intent1.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
            intent1.putExtra(Constants.DRS_PSTN_KEY, getDrsPstnKey);
            intent1.putExtra(Constants.DRS_PIN, getDrsPin);
            intent1.putExtra(Constants.SECURE_DELIVERY, isSecureDelivery);
            intent1.putExtra(Constants.INTENT_KEY, longAwbNo);
            intent1.putExtra(Constants.IS_AMAZON_RESHEDUCLE_ENABLE, is_amazon_schedule_enable);
            intent1.putExtra("call_allowed", callAllowed);
            intent1.putExtra(DRS_DATE, String.valueOf(fwdOBDProductDetailViewModel.mDrsForwardTypeResponse.getAssignedDate()));
            intent1.putExtra(SHIPMENT_DECLARED_VALUE, String.valueOf(fwdOBDProductDetailViewModel.mDrsForwardTypeResponse.getShipmentDetails().getDeclaredValue()));
            startActivity(intent1);
            applyTransitionToOpenActivity(this);
        });
    }

    private void getAllDataFromIntent() {
        Intent intent = getIntent();
        consigneeName = intent.getStringExtra(Constants.OBD_CONSIGNEE_NAME);
        consigneeAddress = intent.getStringExtra(Constants.OBD_CONSIGNEE_ADDRESS);
        vendorName = intent.getStringExtra(Constants.OBD_VENDOR_NAME);
        itemName = intent.getStringExtra(Constants.OBD_ITEM_NAME);
        addressProfiled = intent.getStringExtra(Constants.OBD_ADDRESS_PROFILED);
        awbNo = intent.getStringExtra(Constants.OBD_AWB_NUMBER);
        isCard = intent.getBooleanExtra(Constants.IS_CARD, false);
        amazonEncryptedOtp = intent.getStringExtra(Constants.AMAZON_ENCRYPTED_OTP);
        amazon = intent.getStringExtra(Constants.AMAZON);
        ofdOtp = intent.getStringExtra(Constants.OFD_OTP);
        dLightEncryptedOtp1 = intent.getStringExtra(Constants.DLIGHT_ENCRYPTED_OTP1);
        dLightEncryptedOtp2 = intent.getStringExtra(Constants.DLIGHT_ENCRYPTED_OTP2);
        consigneeMobile = intent.getStringExtra(Constants.CONSIGNEE_MOBILE);
        consigneeAlternateMobile = intent.getStringExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE);
        resendSecureOtp = intent.getBooleanExtra(Constants.RESEND_SECURE_OTP, false);
        isDelightShipment = intent.getBooleanExtra(Constants.ISDELIGHTSHIPMENT, false);
        orderId = intent.getStringExtra(Constants.ORDER_ID);
        drsIdNum = intent.getStringExtra(Constants.DRS_ID_NUM);
        signImageRequired = intent.getBooleanExtra(Constants.sign_image_required, false);
        fwdDelImage = intent.getBooleanExtra(Constants.FWD_DEL_IMAGE, false);
        callAllowed = intent.getBooleanExtra("call_allowed", false);
        composite_key = getIntent().getStringExtra(Constants.COMPOSITE_KEY);
        shipmentType = getIntent().getExtras().getString(Constants.SHIPMENT_TYPE);
        getDrsApiKey = getIntent().getExtras().getString(Constants.DRS_API_KEY);
        getDrsPstnKey = getIntent().getExtras().getString(Constants.DRS_PSTN_KEY);
        getDrsPin = getIntent().getExtras().getString(Constants.DRS_PIN);
        longAwbNo = getIntent().getLongExtra(Constants.INTENT_KEY, 0);
        is_amazon_schedule_enable = getIntent().getBooleanExtra(Constants.IS_AMAZON_RESHEDUCLE_ENABLE, false);
        isSecureDelivery = getIntent().getParcelableExtra(Constants.SECURE_DELIVERY);
    }

    @Override
    public FwdOBDProductDetailViewModel getViewModel() {
        return fwdOBDProductDetailViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_obd_product_detail;
    }

    private void setBackPressed() {
        binding.awbHeader.backArrow.setOnClickListener(view -> {
            FwdOBDProductDetailActivity.super.onBackPressed();
            applyTransitionToBackFromActivity(this);
        });
    }

    @Override
    public void onUndelivered(ForwardCommit forwardCommit) {
        try {
            Intent intent = new Intent();
            FWDActivitiesData fwdActivitiesData = new FWDActivitiesData();
            intent = UndeliveredActivity.getStartIntent(this);
            fwdActivitiesData.setDrsPin(getDrsPin);
            fwdActivitiesData.setDrsApiKey(getDrsApiKey);
            fwdActivitiesData.setDrsPstnKey(getDrsPstnKey);
            fwdActivitiesData.setOrderId(orderId);
            fwdActivitiesData.setCallAllowed(callAllowed);
            fwdActivitiesData.setConsignee_mobile(consigneeMobile);
            fwdActivitiesData.setConsignee_alternate_number(consigneeAlternateMobile);
            fwdActivitiesData.setDrsId(Integer.parseInt(drsIdNum));
            fwdActivitiesData.setAwbNo(longAwbNo);
            fwdActivitiesData.setCompositeKey(composite_key);
            fwdActivitiesData.setIs_amazon_reschedule_enabled(is_amazon_schedule_enable);
            fwdActivitiesData.setShipment_type("ppd");
            fwdActivitiesData.setSecure_undelivered(String.valueOf(fwdOBDProductDetailViewModel.isSecureOtp.get()));
            fwdActivitiesData.setCollected_value(fwdOBDProductDetailViewModel.getCollectableValue());
            fwdActivitiesData.setIs_obd(true);
            intent.putExtra(getString(R.string.data), forwardCommit);
            intent.putExtra("fwdActivitiesData", fwdActivitiesData);
            startActivity(intent);
            applyTransitionToOpenActivity(this);
        } catch (Exception e) {
            Logger.e("FwdOBDProductDetailActivity->onUndelivered", String.valueOf(e));
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void onHandleError(String errorResponse) {}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        applyTransitionToBackFromActivity(this);
    }
}