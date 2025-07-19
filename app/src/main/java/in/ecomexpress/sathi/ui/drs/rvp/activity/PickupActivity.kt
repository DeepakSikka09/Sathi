package `in`.ecomexpress.sathi.ui.drs.rvp.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import `in`.ecomexpress.sathi.BR
import `in`.ecomexpress.sathi.BuildConfig
import `in`.ecomexpress.sathi.R
import `in`.ecomexpress.sathi.databinding.ActivityPickupBinding
import `in`.ecomexpress.sathi.repo.remote.model.drs_list.forward.SecureDelivery
import `in`.ecomexpress.sathi.ui.base.BaseActivity
import `in`.ecomexpress.sathi.ui.drs.rvp.adapter.ImageSliderAdapter
import `in`.ecomexpress.sathi.ui.drs.rvp.navigator.RvpQcNavigator
import `in`.ecomexpress.sathi.ui.drs.rvp.viewmodel.RvpCommonViewModel
import `in`.ecomexpress.sathi.utils.CommonUtils
import `in`.ecomexpress.sathi.utils.Constants
import `in`.ecomexpress.sathi.utils.setupWithViewPager2

@AndroidEntryPoint
class PickupActivity : BaseActivity<ActivityPickupBinding, RvpCommonViewModel>(), RvpQcNavigator {

    private val activityPickupBinding: ActivityPickupBinding by lazy {
        ActivityPickupBinding.inflate(layoutInflater)
    }

    private val rvpCommonViewModel: RvpCommonViewModel by viewModels()
    private var drsPin: String = ""
    private var drsPstnKey: String = ""
    private var drsApiKey: String = ""
    private var drsId: String = ""
    private var secureDelivery: SecureDelivery? = null
    private var isSecureDelivery: Boolean = false
    private var shipmentType: String = ""
    private var ofdOtp: String = ""
    private var callAllowed: Boolean = false
    private var consigneeMobile: String = ""
    private var amazonEncryptedOtp: String = ""
    private var amazon: String = ""
    private var compositeKey: String = ""
    private var awbNumber: String = ""
    private var resendSecureOtp: Boolean = false
    private var consigneeAlternateMobile: String = ""
    private var itemDescription: String = ""
    private var consigneeName: String = ""
    private var isConsigneeLocationVerified = false
    private var smartQc = "false"
    private var isRvpPhonepeShipment = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityPickupBinding.root)
        getIntentData()
        setupBackNavigation()
        interactWithViewModel()
        setupUI()
    }

    private fun setupUI() {
        getQcImageUrls()
        setupHeader()
        setupAWBHeader()
        setupBottomButton()
        setupImageSlider()
    }

    private fun interactWithViewModel() {
        rvpCommonViewModel.showErrorMessageLiveData.observe(this) { (message, isError) ->
            showMessageOnUI(message, isError)
        }
    }

    private fun getQcImageUrls() {
        if (shipmentType.equals(Constants.RQC, ignoreCase = true)) {
            rvpCommonViewModel.getQcImageUrls(this, awbNumber.toLongOrNull() ?: 0L)
        }
    }

    private fun navigateToNextActivityWithData(
        destinationActivity: Class<out Activity>, awbNumber: String) {
        val intent = Intent(this, destinationActivity).apply {
            putExtra(Constants.DRS_ID, drsId)
            putExtra(Constants.SECURE_DELIVERY, secureDelivery)
            putExtra(Constants.SECURE_DELIVERY_OTP, isSecureDelivery)
            putExtra(Constants.SHIPMENT_TYPE, shipmentType)
            putExtra(Constants.DRS_PIN, drsPin)
            putExtra(Constants.DRS_PSTN_KEY, drsPstnKey)
            putExtra(Constants.DRS_API_KEY, drsApiKey)
            putExtra(Constants.ITEM_DESCRIPTION, itemDescription)
            putExtra(Constants.CONSIGNEE_NAME, consigneeName)
            putExtra(Constants.OFD_OTP, ofdOtp)
            putExtra(Constants.CALL_ALLOWED, callAllowed)
            putExtra(Constants.CONSIGNEE_LOCATION_VERIFIED, isConsigneeLocationVerified)
            putExtra(Constants.CONSIGNEE_MOBILE, consigneeMobile)
            putExtra(Constants.AMAZON_ENCRYPTED_OTP, amazonEncryptedOtp)
            putExtra(Constants.AMAZON, amazon)
            putExtra(Constants.COMPOSITE_KEY, compositeKey)
            putExtra(Constants.AWB_NUMBER, awbNumber)
            putExtra(Constants.RESEND_SECURE_OTP, resendSecureOtp)
            putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, consigneeAlternateMobile)
            putExtra(Constants.SMART_QC_ENABLED, smartQc)
            putExtra(Constants.IS_FROM_MPS, false)
        }
        startActivity(intent)
        CommonUtils.applyTransitionToOpenActivity(this)
    }

    @SuppressLint("SetTextI18n")
    private fun setupHeader() {
        activityPickupBinding.header.headingName.text = getString(R.string.pickup)
        activityPickupBinding.header.versionName.text = "v" + BuildConfig.VERSION_NAME
    }

    private fun setupBottomButton() {
        with(activityPickupBinding.actionButton) {
            undeliveredShipmentButton.text = getString(R.string.rvp_cancel)
            deliveredShipmentButton.text = when {
                shipmentType.equals(Constants.RVP, ignoreCase = true) -> getString(R.string.pickup)
                else -> getString(R.string.start_qc)
            }
            deliveredShipmentButton.setOnClickListener {
                val targetActivity = when {
                    secureDelivery == null ||  secureDelivery?.otp == false-> {
                        if (shipmentType.equals(Constants.RQC, ignoreCase = true)) {
                            RvpQCDetailActivity::class.java
                        } else {
                            CaptureScanActivity::class.java
                        }
                    }
                    secureDelivery?.otp == true -> {
                        PickupOtpActivity::class.java
                    }
                    else -> CaptureScanActivity::class.java
                }
                navigateToNextActivityWithData(targetActivity, awbNumber)
            }
            undeliveredShipmentButton.setOnClickListener {
                navigateToNextActivityWithData(CancelReasonCodeListActivity::class.java, awbNumber)
            }
        }
    }

    private fun setupImageSlider() {
        activityPickupBinding.apply {
            itemDescription.text = this@PickupActivity.itemDescription
            if (shipmentType.equals(Constants.RVP, ignoreCase = true) || smartQc.equals("true", true) || isRvpPhonepeShipment.equals("true", true)) {
                showNoQCImagePlaceHolder()
                return
            }
            rvpCommonViewModel.qcImageUrls.observe(this@PickupActivity) { urls ->
                if (urls.isNullOrEmpty()) {
                    showNoQCImagePlaceHolder()
                } else {
                    setupViewPager(urls)
                }
            }
        }
    }

    private fun showNoQCImagePlaceHolder() {
        activityPickupBinding.apply {
            qcImageView.isGone = true
            qcImageIndicator.isGone = true
            noQCImageView.isVisible = true
            if (isRvpPhonepeShipment.equals("true", true)) {
                noQCImageView.setImageResource(R.drawable.phonepe_placeholder)
            }
        }
    }

    private fun setupViewPager(imageUrls: List<String>) {
        activityPickupBinding.apply {
            qcImageView.adapter = ImageSliderAdapter(imageUrls)
            qcImageIndicator.setupWithViewPager2(qcImageView)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupAWBHeader() {
        if (smartQc.equals("true", true)) {
            activityPickupBinding.rqcAwbHeader.awbNumber.apply {
                setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.smart_qc, 0)
                text = getString(R.string.awb) + " $awbNumber"
            }
        } else {
            val iconResource = if (shipmentType.equals(Constants.RVP, ignoreCase = true)) R.drawable.rvp_tag_icon else R.drawable.rqc_tag_icon
            activityPickupBinding.rqcAwbHeader.awbNumber.apply {
                setCompoundDrawablesWithIntrinsicBounds(0, 0, iconResource, 0)
                text = getString(R.string.awb) + " $awbNumber"
            }
        }
    }

    private fun setupBackNavigation() {
        activityPickupBinding.header.backArrow.setOnClickListener { backPress() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPress()
            }
        })
    }

    @Suppress("DEPRECATION")
    private fun getIntentData() {
        try {
            intent?.let {
                drsId = it.getStringExtra(Constants.DRS_ID) ?: ""
                secureDelivery = it.getParcelableExtra(Constants.SECURE_DELIVERY)
                isSecureDelivery = it.getBooleanExtra(Constants.SECURE_DELIVERY_OTP, false)
                shipmentType = it.getStringExtra(Constants.SHIPMENT_TYPE) ?: ""
                drsPin = it.getStringExtra(Constants.DRS_PIN) ?: ""
                drsPstnKey = it.getStringExtra(Constants.DRS_PSTN_KEY) ?: ""
                drsApiKey = it.getStringExtra(Constants.DRS_API_KEY) ?: ""
                itemDescription = it.getStringExtra(Constants.ITEM_DESCRIPTION) ?: ""
                ofdOtp = it.getStringExtra(Constants.OFD_OTP) ?: ""
                consigneeName = it.getStringExtra(Constants.CONSIGNEE_NAME) ?: ""
                callAllowed = it.getBooleanExtra(Constants.CALL_ALLOWED, false)
                isConsigneeLocationVerified = it.getBooleanExtra(Constants.CONSIGNEE_LOCATION_VERIFIED, false)
                consigneeMobile = it.getStringExtra(Constants.CONSIGNEE_MOBILE) ?: ""
                amazonEncryptedOtp = it.getStringExtra(Constants.AMAZON_ENCRYPTED_OTP) ?: ""
                amazon = it.getStringExtra(Constants.AMAZON) ?: ""
                compositeKey = it.getStringExtra(Constants.COMPOSITE_KEY) ?: ""
                awbNumber = it.getStringExtra(Constants.AWB_NUMBER) ?: ""
                resendSecureOtp = it.getBooleanExtra(Constants.RESEND_SECURE_OTP, false)
                consigneeAlternateMobile = it.getStringExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE) ?: ""
                smartQc = it.getStringExtra(Constants.SMART_QC_ENABLED) ?: ""
                isRvpPhonepeShipment = it.getStringExtra(Constants.IS_RVP_PHONEPE_SHIPMENT) ?: ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun backPress() {
        finish()
        CommonUtils.applyTransitionToBackFromActivity(this)
    }

    private fun showMessageOnUI(message: String?, isError: Boolean) {
        message?.let {
            if (isError) showSnackbar(it) else showSuccessInfo(it)
        }
    }

    override fun getViewModel(): RvpCommonViewModel {
        return rvpCommonViewModel
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_pickup
    }
}