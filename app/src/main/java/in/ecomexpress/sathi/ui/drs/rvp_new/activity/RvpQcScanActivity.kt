package `in`.ecomexpress.sathi.ui.drs.rvp_new.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import dagger.hilt.android.AndroidEntryPoint
import `in`.ecomexpress.sathi.BR
import `in`.ecomexpress.sathi.BuildConfig
import `in`.ecomexpress.sathi.R
import `in`.ecomexpress.sathi.databinding.ActivityRvpQcScanBinding
import `in`.ecomexpress.sathi.repo.local.data.rvp.RvpCommit
import `in`.ecomexpress.sathi.ui.base.BaseActivity
import `in`.ecomexpress.sathi.ui.drs.rvp_new.navigator.RvpQcNavigator
import `in`.ecomexpress.sathi.ui.drs.rvp_new.viewmodel.RvpCommonViewModel
import `in`.ecomexpress.sathi.utils.CommonUtils
import `in`.ecomexpress.sathi.utils.Constants

@AndroidEntryPoint
class RvpQcScanActivity : BaseActivity<ActivityRvpQcScanBinding, RvpCommonViewModel>(),
    RvpQcNavigator {

    private val activityRvpQcScanBinding: ActivityRvpQcScanBinding by lazy {
        ActivityRvpQcScanBinding.inflate(layoutInflater)
    }

    private val rvpCommonViewModel: RvpCommonViewModel by viewModels()
    private var lastText = ""
    private val debounceDelay: Long = 2000
    private var lastScanTime: Long = 0
    private var listBlue: ColorStateList? = null
    private var listGray: ColorStateList? = null
    private var drsId: String = ""
    private var shipmentType: String = ""
    private var awbNumber: String = ""
    private var isOfdOtpVerified: String = ""
    private var scannedFlyerValue: String = ""
    private var consigneeName: String = ""
    private var isConsigneeLocationVerified = false
    private var qcWizards: ArrayList<RvpCommit.QcWizard> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityRvpQcScanBinding.root)
        getIntentData()
        setupBackNavigation()
        interactWithViewModel()
        setupUI()
    }

    private fun setupUI() {
        setupHeader()
        setupSubmitButton()
        startScanningWork()
    }

    private fun interactWithViewModel() {
        rvpCommonViewModel.showErrorMessageLiveData.observe(this) { (message, isError) ->
            showMessageOnUI(message, isError)
        }
    }

    private fun startScanningWork() {
        with(activityRvpQcScanBinding.scannerView) {
            initializeFromIntent(intent)
            decodeContinuous(barcodeCallback)
        }
        activityRvpQcScanBinding.scannerFlash.setOnClickListener {
            toggleFlash()
        }
    }

    private val barcodeCallback = BarcodeCallback { barcodeResult: BarcodeResult ->
        if (barcodeResult.text != null && !barcodeResult.text.equals(lastText, ignoreCase = true)) {
            if (System.currentTimeMillis() - lastScanTime > debounceDelay) {
                startScanningWork(barcodeResult.text)
                lastText = barcodeResult.text
                lastScanTime = System.currentTimeMillis()
            }
        }
    }

    private fun startScanningWork(scannedValue: String) {
        if (!isNetworkConnected) {
            showMessageOnUI(getString(R.string.check_internet), true)
            return
        }
        if (!CommonUtils.isValidPatterForRVPFlyerScan(scannedValue)) {
            showMessageOnUI(getString(R.string.try_again_with_another_flyer), true)
            return
        }
        val firstCharacter: Char = scannedValue[0]
        if (Character.isLetter(firstCharacter)) {
            val binding = activityRvpQcScanBinding
            val submitButton = binding.bottomSubmitButton.submitButton
            if (checkFlyerStatus(scannedValue)) {
                rvpCommonViewModel.doRvpFlyerDuplicateCheckApiCall(
                    this@RvpQcScanActivity,
                    scannedValue,
                    drsId,
                    awbNumber
                )
            } else {
                showMessageOnUI(getString(R.string.invalid_flyer_code_scan_again_or_another), true)
            }
            rvpCommonViewModel.flyerDuplicateCheckStatus.observe(this) { isFlyerValid ->
                if (isFlyerValid) {
                    pauseScannerAndTorch()
                }
                binding.scannedSuccessfullyMessage.apply {
                    visibility = if (isFlyerValid) View.VISIBLE else View.GONE
                    text = if (isFlyerValid) context.getString(
                        R.string.awb_scanned_successfully,
                        scannedValue
                    ) else ""
                }

                submitButton.apply {
                    isClickable = isFlyerValid
                    backgroundTintList = if (isFlyerValid) listBlue else listGray
                    setOnClickListener {
                        if (isFlyerValid) {
                            scannedFlyerValue = scannedValue.replace("[^a-zA-Z0-9]".toRegex(), "")
                            navigateToNextActivityWithData(RvpQcSignatureActivity::class.java)
                        }
                    }
                }
            }
        } else {
            showMessageOnUI(getString(R.string.this_is_not_an_flyer_code), true)
        }
    }

    private fun checkFlyerStatus(scannedFlyerValue: String): Boolean {
        val flyerWords = rvpCommonViewModel.dataManager.getRVPAWBWords()
        for (word in flyerWords.split(",")) {
            if (scannedFlyerValue.startsWith(word)) {
                return true
            }
        }
        return false
    }

    private fun setupSubmitButton() {
        listBlue = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.button_enable_color))
        listGray =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.button_disable_color))
        with(activityRvpQcScanBinding.bottomSubmitButton.submitButton) {
            text = getString(R.string.mark_picked_up)
            isClickable = false
        }
    }

    private fun toggleFlash() {
        activityRvpQcScanBinding.scannerFlash.isSelected =
            !activityRvpQcScanBinding.scannerFlash.isSelected
        activityRvpQcScanBinding.scannerView.barcodeView.setTorch(activityRvpQcScanBinding.scannerFlash.isSelected)
        updateFlashIcon()
    }

    private fun updateFlashIcon() {
        val flashIcon = if (activityRvpQcScanBinding.scannerFlash.isSelected) {
            R.drawable.flash_on_icon
        } else {
            R.drawable.flash_off_icon
        }
        activityRvpQcScanBinding.scannerFlash.setImageResource(flashIcon)
    }

    private fun showMessageOnUI(message: String?, isError: Boolean) {
        message?.let {
            if (isError) showSnackbar(it) else showSuccessInfo(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupHeader() {
        with(activityRvpQcScanBinding.header) {
            headingName.text = getString(R.string.scan_shipment)
            versionName.text = "v" + BuildConfig.VERSION_NAME
        }
    }

    private fun setupBackNavigation() {
        activityRvpQcScanBinding.header.backArrow.setOnClickListener { backPress() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPress()
            }
        })
    }

    private fun backPress() {
        finish()
        CommonUtils.applyTransitionToBackFromActivity(this)
    }

    private fun getIntentData() {
        try {
            intent?.let {
                drsId = it.getStringExtra(Constants.DRS_ID) ?: ""
                shipmentType = it.getStringExtra(Constants.SHIPMENT_TYPE) ?: ""
                isOfdOtpVerified = it.getStringExtra(Constants.OFD_OTP) ?: ""
                awbNumber = it.getStringExtra(Constants.AWB_NUMBER) ?: ""
                isConsigneeLocationVerified =
                    it.getBooleanExtra(Constants.CONSIGNEE_LOCATION_VERIFIED, false)
                consigneeName = it.getStringExtra(Constants.CONSIGNEE_NAME) ?: ""
                qcWizards = it.getParcelableArrayListExtra(Constants.QC_WIZARDS) ?: ArrayList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun navigateToNextActivityWithData(destinationActivity: Class<out Activity>) {
        val intent = Intent(this, destinationActivity).apply {
            putExtra(Constants.DRS_ID, drsId)
            putExtra(Constants.SHIPMENT_TYPE, shipmentType)
            putExtra(Constants.OFD_OTP, isOfdOtpVerified)
            putExtra(Constants.AWB_NUMBER, awbNumber)
            putExtra(Constants.CONSIGNEE_LOCATION_VERIFIED, isConsigneeLocationVerified)
            putExtra(Constants.RVP_FLYER_SCANNED_VALUE, scannedFlyerValue)
            putExtra(Constants.CONSIGNEE_NAME, consigneeName)
            putParcelableArrayListExtra(Constants.QC_WIZARDS, qcWizards)
        }
        startActivity(intent)
        CommonUtils.applyTransitionToOpenActivity(this)
    }

    override fun onResume() {
        super.onResume()
        activityRvpQcScanBinding.scannerView.resume()
        activityRvpQcScanBinding.scannerView.barcodeView.setTorch(activityRvpQcScanBinding.scannerFlash.isSelected)
    }

    override fun onPause() {
        super.onPause()
        pauseScannerAndTorch()
    }

    private fun pauseScannerAndTorch() {
        activityRvpQcScanBinding.scannerView.pause()
        activityRvpQcScanBinding.scannerView.barcodeView.setTorch(false)
    }

    override fun getViewModel(): RvpCommonViewModel {
        return rvpCommonViewModel
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_rvp_qc_scan
    }
}