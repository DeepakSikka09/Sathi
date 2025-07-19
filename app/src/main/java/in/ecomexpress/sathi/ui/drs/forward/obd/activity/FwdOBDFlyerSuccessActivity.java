package in.ecomexpress.sathi.ui.drs.forward.obd.activity;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityFwdObdSuccessShipBinding;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.utils.Constants;

@AndroidEntryPoint
public class FwdOBDFlyerSuccessActivity extends AppCompatActivity {

    private final String TAG = FwdOBDFlyerSuccessActivity.class.getSimpleName();
    private ActivityFwdObdSuccessShipBinding binding;
    // Data from previous activity:-
    private String consigneeName;
    private String consigneeAddress;
    private String vendorName;
    private String itemName;
    private String addressProfiled;
    private String awbNo;
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
    private String returnPackageBarcode;
    private boolean fwdDelImage;
    private boolean callAllowed;
    private boolean flag;
    private boolean isUndelivered;
    private boolean isNonQc;
    private boolean isCheckedbox;
    private String cleanedFlyerValue;
    private String DRS_DATE = "";
    private String SHIPMENT_DECLARED_VALUE = "";
    public List<String> qcFailedData;
    ForwardCommit forwardCommit;
    public List<String> qcCode;
    public List<String> qualityCheckName;

    private boolean isFlyerImage=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFwdObdSuccessShipBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        logScreenNameInGoogleAnalytics(TAG, this);
        setBackPressed();
        getAllDataFromIntent();
        binding.btnCapture.setOnClickListener(v -> sendIntent(FwdOBDStopOTPActivity.class));
    }

    private void setBackPressed() {
        binding.awb.backArrow.setOnClickListener(view -> {
            FwdOBDFlyerSuccessActivity.super.onBackPressed();
            applyTransitionToBackFromActivity(this);
        });
    }

    private void getAllDataFromIntent() {
        if (getIntent().getExtras() != null) {
            Intent intent = getIntent();
            qcCode = intent.getStringArrayListExtra("QC_CODE");
            qualityCheckName = intent.getStringArrayListExtra("QC_NAME");
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
            returnPackageBarcode = intent.getStringExtra(Constants.return_package_barcode);
            fwdDelImage = intent.getBooleanExtra(Constants.FWD_DEL_IMAGE, false);
            callAllowed = intent.getBooleanExtra("call_allowed", false);
            flag = intent.getBooleanExtra("flag", false);
            isUndelivered = intent.getBooleanExtra("isUndelivered", false);
            isNonQc = intent.getBooleanExtra("isNonQc", false);
            cleanedFlyerValue = intent.getStringExtra("cleanedFlyerValue");
            isCheckedbox=intent.getBooleanExtra("isCheckedbox",false);
            qcFailedData = intent.getStringArrayListExtra("QC_FAILED_DATA");
            DRS_DATE = getIntent().getExtras().getString(Constants.DRS_DATE);
            SHIPMENT_DECLARED_VALUE = getIntent().getExtras().getString(Constants.SHIPMENT_DECLARED_VALUE);
            forwardCommit = getIntent().getParcelableExtra("data");
            isFlyerImage=getIntent().getBooleanExtra(Constants.Flyer_Img_Check,false);
            binding.awb.awb.setText("AWB: "+awbNo);
        }
    }

    private void sendIntent(Class<? extends Activity> targetActvity) {
        forwardCommit.setConsignee_name(consigneeName);
        Intent intent = new Intent(FwdOBDFlyerSuccessActivity.this, targetActvity);
        intent.putExtra(Constants.OBD_CONSIGNEE_NAME, consigneeName);
        intent.putExtra(Constants.OBD_CONSIGNEE_ADDRESS, consigneeAddress);
        intent.putExtra(Constants.OBD_VENDOR_NAME, vendorName);
        intent.putExtra(Constants.OBD_ITEM_NAME, itemName);
        intent.putExtra(Constants.OBD_ADDRESS_PROFILED, addressProfiled);
        intent.putExtra(Constants.OBD_AWB_NUMBER, awbNo);
        intent.putExtra(Constants.IS_CARD, isCard);
        intent.putExtra(Constants.AMAZON_ENCRYPTED_OTP, amazonEncryptedOtp);
        intent.putExtra(Constants.AMAZON, amazon);
        intent.putExtra(Constants.OFD_OTP, ofdOtp);
        intent.putExtra(Constants.DLIGHT_ENCRYPTED_OTP1, dLightEncryptedOtp1);
        intent.putExtra(Constants.DLIGHT_ENCRYPTED_OTP2, dLightEncryptedOtp2);
        intent.putExtra(Constants.CONSIGNEE_MOBILE, consigneeMobile);
        intent.putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, consigneeAlternateMobile);
        intent.putExtra(Constants.RESEND_SECURE_OTP, resendSecureOtp);
        intent.putExtra(Constants.ISDELIGHTSHIPMENT, isDelightShipment);
        intent.putExtra(Constants.ORDER_ID, orderId);
        intent.putExtra(Constants.DRS_ID_NUM, drsIdNum);
        intent.putExtra(Constants.sign_image_required, signImageRequired);
        intent.putExtra(Constants.return_package_barcode, returnPackageBarcode);
        intent.putExtra(Constants.FWD_DEL_IMAGE, fwdDelImage);
        intent.putExtra("call_allowed", callAllowed);
        intent.putExtra("isUndelivered", isUndelivered);
        intent.putExtra("flag", true);
        intent.putExtra("isCheckedbox", isCheckedbox);
        intent.putExtra("isNonQc", isNonQc);
        intent.putStringArrayListExtra("QC_FAILED_DATA", (ArrayList<String>) qcFailedData);
        intent.putExtra(Constants.SHIPMENT_DECLARED_VALUE,SHIPMENT_DECLARED_VALUE);
        intent.putExtra(Constants.DRS_DATE,DRS_DATE);
        intent.putStringArrayListExtra("QC_CODE", (ArrayList<String>) qcCode);
        intent.putStringArrayListExtra("QC_NAME", (ArrayList<String>) qualityCheckName);
        intent.putExtra(getString(R.string.data), forwardCommit);
        intent.putExtra(Constants.Flyer_Img_Check,isFlyerImage);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }
}