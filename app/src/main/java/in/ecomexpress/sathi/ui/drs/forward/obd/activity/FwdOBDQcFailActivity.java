package in.ecomexpress.sathi.ui.drs.forward.obd.activity;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.FwdObdNonqcActivityBinding;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.drs.forward.obd.adapter.OBDQcFailAdapter;
import in.ecomexpress.sathi.ui.drs.forward.obd.navigator.IFwdObdQCFailNavigator;
import in.ecomexpress.sathi.ui.drs.forward.obd.viewmodel.FwdOBDQcFailViewModel;
import in.ecomexpress.sathi.utils.Constants;

@AndroidEntryPoint
public class FwdOBDQcFailActivity extends BaseActivity<FwdObdNonqcActivityBinding, FwdOBDQcFailViewModel> implements IFwdObdQCFailNavigator {

    private final String TAG = FwdOBDQcFailActivity.class.getSimpleName();
    @Inject
    FwdOBDQcFailViewModel fwdOBDQcFailViewModel;
    public List<String> qcFailedData;
    public List<String> qcFailedDataWithPass = new ArrayList<>();
    public String awbNumber;
    private String consigneeName;
    private String consigneeAddress;
    private String vendorName;
    private String itemName;
    private String addressProfiled;
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
    public String drsIdNum;
    private boolean signImageRequired;
    private String returnPackageBarcode;
    private boolean fwdDelImage;
    private boolean callAllowed;
    private String DRS_DATE = "";
    private String SHIPMENT_DECLARED_VALUE = "";
    ForwardCommit forwardCommit;
    public List<String> qcCode;
    public List<String> qualityCheckName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FwdObdNonqcActivityBinding nonQcActivityBinding = FwdObdNonqcActivityBinding.inflate(getLayoutInflater());
        setContentView(nonQcActivityBinding.getRoot());
        fwdOBDQcFailViewModel.setNavigator(FwdOBDQcFailActivity.this);
        logScreenNameInGoogleAnalytics(TAG, this);
        getAllDataFromIntent(nonQcActivityBinding);
        nonQcActivityBinding.header.backArrow.setOnClickListener(view -> onBackPressed());
        nonQcActivityBinding.btnUndeliver.setOnClickListener(v -> {
            Intent intent = new Intent(FwdOBDQcFailActivity.this, FwdOBDScannerActivity.class);
            sendDataForOther(intent);
        });
    }

    private void setQCFailedAdapter(FwdObdNonqcActivityBinding nonQcActivityBinding) {
        OBDQcFailAdapter adapter = new OBDQcFailAdapter(qcFailedData);
        nonQcActivityBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        nonQcActivityBinding.recyclerView.setAdapter(adapter);
    }

    @Override
    public FwdOBDQcFailViewModel getViewModel() {
        return fwdOBDQcFailViewModel;
    }

    private void getAllDataFromIntent(FwdObdNonqcActivityBinding nonQcActivityBinding) {
        Intent intent = getIntent();
        qcCode = intent.getStringArrayListExtra("QC_CODE");
        qualityCheckName = intent.getStringArrayListExtra("QC_NAME");
        qcFailedData = intent.getStringArrayListExtra("QC_FAILED_DATA");
        qcFailedDataWithPass = intent.getStringArrayListExtra("QC_FAILED_DATA_WITH_PASS");
        consigneeName = intent.getStringExtra(Constants.OBD_CONSIGNEE_NAME);
        consigneeAddress = intent.getStringExtra(Constants.OBD_CONSIGNEE_ADDRESS);
        vendorName = intent.getStringExtra(Constants.OBD_VENDOR_NAME);
        itemName = intent.getStringExtra(Constants.OBD_ITEM_NAME);
        addressProfiled = intent.getStringExtra(Constants.OBD_ADDRESS_PROFILED);
        awbNumber = intent.getStringExtra(Constants.OBD_AWB_NUMBER);
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
        DRS_DATE = getIntent().getExtras().getString(Constants.DRS_DATE);
        SHIPMENT_DECLARED_VALUE = getIntent().getExtras().getString(Constants.SHIPMENT_DECLARED_VALUE);
        forwardCommit = getIntent().getParcelableExtra("data");
        setQCFailedAdapter(nonQcActivityBinding);
        nonQcActivityBinding.header.awb.setText(awbNumber);
    }

    public void sendDataForOther(Intent intent) {
        intent.putExtra("isUndelivered", true);
        intent.putExtra("flag", true);
        intent.putExtra("isNonQc", true);
        intent.putExtra(Constants.OBD_CONSIGNEE_NAME, consigneeName);
        intent.putExtra(Constants.OBD_CONSIGNEE_ADDRESS, consigneeAddress);
        intent.putExtra(Constants.OBD_VENDOR_NAME, vendorName);
        intent.putExtra(Constants.OBD_ITEM_NAME, itemName);
        intent.putExtra(Constants.OBD_ADDRESS_PROFILED, addressProfiled);
        intent.putExtra(Constants.OBD_AWB_NUMBER, awbNumber);
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
        // Sending QC_FAILED_DATA_WITH_PASS as QC_FAILED_DATA.
        Log.e("Ganpati_Data", qcFailedDataWithPass + "");
        Log.e("Ganpati_Size", qcFailedDataWithPass.size() + "");
        intent.putStringArrayListExtra("QC_FAILED_DATA", (ArrayList<String>) qcFailedDataWithPass);
        intent.putStringArrayListExtra("QC_CODE", (ArrayList<String>) qcCode);
        intent.putStringArrayListExtra("QC_NAME", (ArrayList<String>) qualityCheckName);
        intent.putExtra("call_allowed", callAllowed);
        intent.putExtra(Constants.SHIPMENT_DECLARED_VALUE, SHIPMENT_DECLARED_VALUE);
        intent.putExtra(Constants.DRS_DATE, DRS_DATE);
        intent.putExtra(getString(R.string.data), forwardCommit);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fwd_obd_nonqc_activity;
    }


    @Override
    public void showError(String error) {
        showSnackbar(error);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showSnackbar("You Can Not Move Back");
    }
}