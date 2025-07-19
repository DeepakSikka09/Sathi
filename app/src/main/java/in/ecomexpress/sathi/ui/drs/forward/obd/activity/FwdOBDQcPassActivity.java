package in.ecomexpress.sathi.ui.drs.forward.obd.activity;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logButtonEventInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.FwdObdQcActivityBinding;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.drs.forward.obd.adapter.OBDPagerAdapter;
import in.ecomexpress.sathi.ui.drs.forward.obd.navigator.IFwdObdQcNavigator;
import in.ecomexpress.sathi.ui.drs.forward.obd.viewmodel.FwdOBDcCPassViewModel;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.ImageHandler;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class FwdOBDQcPassActivity extends BaseActivity<FwdObdQcActivityBinding, FwdOBDcCPassViewModel> implements IFwdObdQcNavigator {

    public ImageHandler imageHandler;
    public static FwdOBDQcPassActivity fwdOBDQcPassActivity;
    @Inject
    FwdOBDcCPassViewModel fwdOBDcCPassViewModel;

    OBDPagerAdapter obdPagerAdapter;
    private final String TAG = FwdOBDQcPassActivity.class.getSimpleName();
    private static int qcCompletedCount;
    static boolean qualityCheckStatus;
    private FwdObdQcActivityBinding fwdObdQcActivityBinding;
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
    private boolean fwdDelImage;
    private boolean callAllowed;
    public List<String> qcFailedData = new ArrayList<>();
    public List<String> qcFailedDataWithPass = new ArrayList<>();
    public ColorStateList listColor;
    public ColorStateList listWhite;
    public ColorStateList listBlue;
    public ColorStateList listGrey;
    private String DRS_DATE = "";
    private String SHIPMENT_DECLARED_VALUE = "";
    ForwardCommit forwardCommit;
    private String awbScanMethod;
    private final LinkedHashMap<String, String> qcStatusData = new LinkedHashMap<>();

    @Override
    public FwdOBDcCPassViewModel getViewModel() {
        return fwdOBDcCPassViewModel;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fwdOBDcCPassViewModel.setNavigator(FwdOBDQcPassActivity.this);
        fwdObdQcActivityBinding = getViewDataBinding();
        setContentView(fwdObdQcActivityBinding.getRoot());
        logScreenNameInGoogleAnalytics(TAG, this);

        fwdOBDQcPassActivity = this;
        getAllDataFromIntent();
        fwdOBDcCPassViewModel.fetObdQualityChecksData(Long.parseLong(awbNumber));
        setButtonColor();
        qcCompletedCount = 0;
        fwdObdQcActivityBinding.viewPagerQuestions.setUserInputEnabled(false);

        fwdOBDcCPassViewModel.getIsDataLoaded().observe(this, isLoaded -> {
            if (isLoaded) {
                obdPagerAdapter = new OBDPagerAdapter(qcCompletedCount, FwdOBDQcPassActivity.this, fwdOBDcCPassViewModel);
                fwdObdQcActivityBinding.viewPagerQuestions.setAdapter(obdPagerAdapter);
                fwdObdQcActivityBinding.awbHeader.backArrow.setOnClickListener(view -> showSnackbar("You Can Not Move Back"));
                fwdObdQcActivityBinding.awbHeader.awb.setText("AWB: " + awbNumber);

                fwdObdQcActivityBinding.txtInstruction.setText(fwdOBDcCPassViewModel.itemName);

                if (fwdOBDcCPassViewModel.qualityCheckName.isEmpty()) {
                    setQcNextButtonEnable();
                } else {
                    setQcNextButtonDisable();
                }

                fwdObdQcActivityBinding.layoutUndeliverNext.btnNext.setOnClickListener(view -> moveShipmentStatus(false));
                fwdObdQcActivityBinding.layoutUndeliverNext.btnUndeliver.setOnClickListener(view -> undeliveredShipmentAfterQCFailed());
                fwdObdQcActivityBinding.btnNextForUndelivered.setOnClickListener(view -> undeliveredShipmentWithQCFailed());
            }
        });

        imageHandler = new ImageHandler(this) {
            @Override
            public void onBitmapReceived(final Bitmap bitmap, String imageUri, ImageView imgView, String imageName, String imageCode, int pos, boolean verifyImage) {
                try {
                    if (isNetworkConnected()) {
                        fwdOBDcCPassViewModel.uploadImageToServer(FwdOBDQcPassActivity.this, imageName, imageUri, imageCode, Long.parseLong(awbNumber), Integer.parseInt(drsIdNum), bitmap);
                    } else {
                        showError("Image Not Uploaded, Check Your Internet Connection");
                    }
                } catch (Exception e) {
                    Logger.e(FwdOBDQcPassActivity.class.getName(), e.getMessage());
                }
            }
        };
    }

    private void undeliveredShipmentWithQCFailed() {
        if (qualityCheckStatus) {
            moveShipmentStatus(true);
        } else {
            undeliveredShipmentAfterQCFailed();
        }
    }

    private void undeliveredShipmentAfterQCFailed() {
        qcStatusData.put(fwdOBDcCPassViewModel.qcCode.get(qcCompletedCount), "Fail");
        qcFailedData.add(fwdOBDcCPassViewModel.qualityCheckName.get(qcCompletedCount));
        qcFailedDataWithPass.add(fwdOBDcCPassViewModel.qualityCheckName.get(qcCompletedCount));
        qualityCheckStatus = true;
        decideNextActivity();
    }

    private void moveShipmentStatus(boolean passOrOptional) {
        if (passOrOptional) {
            qcStatusData.put(fwdOBDcCPassViewModel.qcCode.get(qcCompletedCount), "Pass");
            qcFailedDataWithPass.add("Pass");
        } else {
            qcStatusData.put(fwdOBDcCPassViewModel.qcCode.get(qcCompletedCount), "Optional");
            qcFailedDataWithPass.add(fwdOBDcCPassViewModel.qualityCheckName.get(qcCompletedCount));
        }
        qualityCheckStatus = true;
        if (qcCompletedCount < fwdOBDcCPassViewModel.numberOfQualityChecks - 1) {
            logButtonEventInGoogleAnalytics(TAG, "CreatingOBDFragment", fwdOBDcCPassViewModel.qualityCheckName.get(qcCompletedCount), this);
            qcCompletedCount++;
            OBDPagerAdapter obdPagerAdapter = new OBDPagerAdapter(qcCompletedCount, FwdOBDQcPassActivity.this, fwdOBDcCPassViewModel);
            fwdObdQcActivityBinding.viewPagerQuestions.setAdapter(obdPagerAdapter);
        } else {
            decideNextActivity();
        }
    }

    private void decideNextActivity() {
        Intent intent;
        if (qcFailedData.isEmpty()) {
            intent = new Intent(FwdOBDQcPassActivity.this, FwdOBDStopOTPActivity.class);
        } else {
            intent = new Intent(FwdOBDQcPassActivity.this, FwdOBDQcFailActivity.class);
        }
        sendDataForOtherActivity(intent);
    }

    public void sendDataForOtherActivity(Intent intent) {
        Bundle qcStatusBundle = new Bundle();
        for (Map.Entry<String, String> entry : qcStatusData.entrySet()) {
            qcStatusBundle.putString(entry.getKey(), entry.getValue());
        }
        intent.putExtra("QC_STATUS_DATA", qcStatusBundle);
        intent.putStringArrayListExtra("QC_CODE", new ArrayList<>(fwdOBDcCPassViewModel.qcCode));
        intent.putStringArrayListExtra("QC_NAME", new ArrayList<>(fwdOBDcCPassViewModel.qualityCheckName));
        intent.putStringArrayListExtra("QC_FAILED_DATA_WITH_PASS", new ArrayList<>(qcFailedDataWithPass));
        intent.putStringArrayListExtra("QC_FAILED_DATA", new ArrayList<>(removeQCPassDataFromList(qcFailedDataWithPass)));
        intent.putExtra(Constants.AWB_SCAN, awbScanMethod);
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
        intent.putExtra(Constants.FWD_DEL_IMAGE, fwdDelImage);
        intent.putExtra("call_allowed", callAllowed);
        intent.putExtra(Constants.DRS_DATE, DRS_DATE);
        intent.putExtra(Constants.SHIPMENT_DECLARED_VALUE, SHIPMENT_DECLARED_VALUE);
        intent.putExtra(getString(R.string.data), forwardCommit);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    private List<String> removeQCPassDataFromList(List<String> qcFailedDataWithPass) {
        Iterator<String> iterator = qcFailedDataWithPass.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().contains("Pass")) {
                iterator.remove();
            }
        }
        return qcFailedDataWithPass;
    }

    public void setButtonColor() {
        listColor = ColorStateList.valueOf(getResources().getColor(R.color.green_FF));
        listWhite = ColorStateList.valueOf(getResources().getColor(R.color.white));
        listBlue = ColorStateList.valueOf(getResources().getColor(R.color.ecomBlue));
        listGrey = ColorStateList.valueOf(getResources().getColor(R.color.gray_ecom));
    }

    public void setQcNextButtonEnable() {
        fwdObdQcActivityBinding.layoutUndeliverNext.btnNext.setBackgroundTintList(listBlue);
        fwdObdQcActivityBinding.layoutUndeliverNext.btnNext.setClickable(true);
        fwdObdQcActivityBinding.layoutUndeliverNext.btnNext.setEnabled(true);

        fwdObdQcActivityBinding.layoutUndeliverNext.btnUndeliver.setBackgroundTintList(null);
        fwdObdQcActivityBinding.layoutUndeliverNext.btnUndeliver.setBackgroundResource(R.drawable.blue_border_button);
        fwdObdQcActivityBinding.layoutUndeliverNext.btnUndeliver.setTextColor(getResources().getColor(R.color.blue_8F));
        fwdObdQcActivityBinding.layoutUndeliverNext.btnUndeliver.setClickable(true);
        fwdObdQcActivityBinding.layoutUndeliverNext.btnUndeliver.setEnabled(true);

        fwdObdQcActivityBinding.btnNextForUndelivered.setBackgroundTintList(listBlue);
        fwdObdQcActivityBinding.btnNextForUndelivered.setClickable(true);
        fwdObdQcActivityBinding.btnNextForUndelivered.setEnabled(true);
    }

    public void setQcNextButtonDisable() {
        fwdObdQcActivityBinding.layoutUndeliverNext.btnNext.setBackgroundTintList(listGrey);
        fwdObdQcActivityBinding.layoutUndeliverNext.btnNext.setClickable(false);
        fwdObdQcActivityBinding.layoutUndeliverNext.btnNext.setEnabled(false);

        fwdObdQcActivityBinding.layoutUndeliverNext.btnUndeliver.setBackgroundTintList(listGrey);
        fwdObdQcActivityBinding.layoutUndeliverNext.btnUndeliver.setTextColor(listWhite);
        fwdObdQcActivityBinding.layoutUndeliverNext.btnUndeliver.setClickable(false);
        fwdObdQcActivityBinding.layoutUndeliverNext.btnUndeliver.setEnabled(false);

        fwdObdQcActivityBinding.btnNextForUndelivered.setBackgroundTintList(listGrey);
        fwdObdQcActivityBinding.btnNextForUndelivered.setClickable(false);
        fwdObdQcActivityBinding.btnNextForUndelivered.setEnabled(false);
    }

    public void showButtonNextOptionalQC() {
        qualityCheckStatus = true;
        fwdObdQcActivityBinding.layoutUndeliverNext.btnNext.setVisibility(View.VISIBLE);
        fwdObdQcActivityBinding.layoutUndeliverNext.btnUndeliver.setVisibility(View.VISIBLE);
        fwdObdQcActivityBinding.btnNextForUndelivered.setVisibility(View.GONE);
    }

    public void hideButtonForNextFragmentOrActivity() {
        fwdObdQcActivityBinding.layoutUndeliverNext.btnNext.setVisibility(View.GONE);
        fwdObdQcActivityBinding.layoutUndeliverNext.btnUndeliver.setVisibility(View.GONE);
        fwdObdQcActivityBinding.btnNextForUndelivered.setVisibility(View.GONE);
    }

    public void showButtonNextMandatoryQC() {
        fwdObdQcActivityBinding.btnNextForUndelivered.setVisibility(View.VISIBLE);
        fwdObdQcActivityBinding.layoutUndeliverNext.btnNext.setVisibility(View.GONE);
        fwdObdQcActivityBinding.layoutUndeliverNext.btnUndeliver.setVisibility(View.GONE);
    }

    private void getAllDataFromIntent() {
        Intent intent = getIntent();
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
        fwdDelImage = intent.getBooleanExtra(Constants.FWD_DEL_IMAGE, false);
        callAllowed = intent.getBooleanExtra("call_allowed", false);
        DRS_DATE = getIntent().getExtras().getString(Constants.DRS_DATE);
        SHIPMENT_DECLARED_VALUE = getIntent().getExtras().getString(Constants.SHIPMENT_DECLARED_VALUE);
        awbScanMethod = intent.getStringExtra(Constants.AWB_SCAN);
        forwardCommit = getIntent().getParcelableExtra("data");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            int PICK_FROM_CAMERA = 0x000010;
            if (requestCode == PICK_FROM_CAMERA) {
                imageHandler.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fwd_obd_qc_activity;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showSnackbar("You Can Not Move Back");
    }

    @Override
    public void showError(String error) {
        showSnackbar(error);
    }

    @Override
    public void setCapturedImageBitmap(Bitmap imageBitmap) {
        // No need to here, task already done OBDFragment.
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        qcFailedData.clear();
        qcFailedDataWithPass.clear();
        qcStatusData.clear();
    }
}