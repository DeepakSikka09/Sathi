package `in`.ecomexpress.sathi.ui.drs.mps.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import `in`.ecomexpress.sathi.BR
import `in`.ecomexpress.sathi.BuildConfig
import `in`.ecomexpress.sathi.R
import `in`.ecomexpress.sathi.databinding.ActivityMpsPickupBinding
import `in`.ecomexpress.sathi.repo.remote.model.drs_list.forward.SecureDelivery
import `in`.ecomexpress.sathi.ui.base.BaseActivity
import `in`.ecomexpress.sathi.ui.drs.mps.adapter.MpsPickupAdapter
import `in`.ecomexpress.sathi.ui.drs.mps.viewmodel.MpsPickupScanViewModel
import `in`.ecomexpress.sathi.ui.drs.rvp.activity.CancelReasonCodeListActivity
import `in`.ecomexpress.sathi.ui.drs.rvp.navigator.RvpQcNavigator
import `in`.ecomexpress.sathi.utils.CommonUtils
import `in`.ecomexpress.sathi.utils.Constants
import `in`.ecomexpress.sathi.utils.setupWithViewPager2

@AndroidEntryPoint
class MpsPickupActivity : BaseActivity<ActivityMpsPickupBinding, MpsPickupScanViewModel>(), RvpQcNavigator {

    private val activityMpsPickupBinding: ActivityMpsPickupBinding by lazy {
        ActivityMpsPickupBinding.inflate(layoutInflater)
    }

    private val mpsPickupScanViewModel: MpsPickupScanViewModel by viewModels()
    private lateinit var mpsPickupAdapter: MpsPickupAdapter
    private var shipmentType: String = Constants.RQC
    private var drsPin: String = ""
    private var drsPstnKey: String = ""
    private var drsApiKey: String = ""
    private var drsId: String = ""
    private var secureDelivery: SecureDelivery? = null
    private var isSecureDelivery: Boolean = false
    private var ofdOtp: String = ""
    private var callAllowed: Boolean = false
    private var consigneeMobile: String = ""
    private var amazonEncryptedOtp: String = ""
    private var amazon: String = ""
    private var compositeKey: String = ""
    private var awbNumber: String = ""
    private var resendSecureOtp: Boolean = false
    private var consigneeAlternateMobile: String = ""
    private var consigneeName: String = ""
    private var isConsigneeLocationVerified = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMpsPickupBinding.root)
        getIntentData()
        setupBackNavigation()
        interactWithViewModel()
        setupUI()
    }

    private fun setupUI() {
        setupRecyclerView()
        setupHeader()
        setupAWBHeaderAndTotalProductsCount()
        setupBottomButton()
    }

    @SuppressLint("SetTextI18n")
    private fun setupHeader() {
        activityMpsPickupBinding.header.headingName.text = getString(R.string.pickup)
        activityMpsPickupBinding.header.versionName.text = "v" + BuildConfig.VERSION_NAME
    }

    @SuppressLint("SetTextI18n")
    private fun setupAWBHeaderAndTotalProductsCount() {
        val iconResource = R.drawable.mps_tag_icon
        activityMpsPickupBinding.rqcAwbHeader.awbNumber.apply {
            setCompoundDrawablesWithIntrinsicBounds(0, 0, iconResource, 0)
            text = getString(R.string.awb) + " $awbNumber"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupRecyclerView() {
        mpsPickupScanViewModel.fetchQCDetails(awbNumber.toLong(), Integer.parseInt(drsId))
        mpsPickupScanViewModel.pickupItemsLiveData.observe(this) { pickupItems ->
            activityMpsPickupBinding.totalMpsQcCount.text = activityMpsPickupBinding.root.context.getString(R.string.total_mps_qc_count, pickupItems.size)
            mpsPickupAdapter = MpsPickupAdapter(pickupItems) { viewPager, tabLayout ->
                tabLayout.setupWithViewPager2(viewPager)
            }

            activityMpsPickupBinding.mpsQcDetailsRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@MpsPickupActivity)
                adapter = mpsPickupAdapter
            }
        }
    }

    private fun setupBackNavigation() {
        activityMpsPickupBinding.header.backArrow.setOnClickListener { backPress() }
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

    @Suppress("DEPRECATION")
    private fun getIntentData() {
        try {
            intent?.let {
                drsId = it.getStringExtra(Constants.DRS_ID) ?: ""
                secureDelivery = it.getParcelableExtra(Constants.SECURE_DELIVERY)
                isSecureDelivery = it.getBooleanExtra(Constants.SECURE_DELIVERY_OTP, false)
                drsPin = it.getStringExtra(Constants.DRS_PIN) ?: ""
                drsPstnKey = it.getStringExtra(Constants.DRS_PSTN_KEY) ?: ""
                drsApiKey = it.getStringExtra(Constants.DRS_API_KEY) ?: ""
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
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupBottomButton() {
        with(activityMpsPickupBinding.actionButton) {
            undeliveredShipmentButton.text = getString(R.string.rvp_cancel)
            deliveredShipmentButton.text = getString(R.string.start_qc)

            deliveredShipmentButton.setOnClickListener {
                val targetActivity = when {
                    secureDelivery == null || secureDelivery?.otp == false -> {
                        MPSListActivity::class.java
                    }

                    secureDelivery?.otp == true -> {
                        MpsPickupOtpActivity::class.java
                    }

                    else -> MpsCaptureScanActivity::class.java
                }
                navigateToNextActivityWithData(targetActivity, awbNumber)
            }
            undeliveredShipmentButton.setOnClickListener {
                navigateToNextActivityWithData(CancelReasonCodeListActivity::class.java, awbNumber)
            }
        }
    }

    private fun interactWithViewModel() {
        mpsPickupScanViewModel.showErrorMessageLiveData.observe(this) { (message, isError) ->
            showMessageOnUI(message, isError)
        }
    }

    private fun showMessageOnUI(message: String?, isError: Boolean) {
        message?.let {
            if (isError) showSnackbar(it) else showSuccessInfo(it)
        }
    }

    private fun navigateToNextActivityWithData(destinationActivity: Class<out Activity>, awbNumber: String) {
        val intent = Intent(this, destinationActivity).apply {
            putExtra(Constants.DRS_ID, drsId)
            putExtra(Constants.SECURE_DELIVERY, secureDelivery)
            putExtra(Constants.SECURE_DELIVERY_OTP, isSecureDelivery)
            putExtra(Constants.DRS_PIN, drsPin)
            putExtra(Constants.SHIPMENT_TYPE, shipmentType)
            putExtra(Constants.DRS_PSTN_KEY, drsPstnKey)
            putExtra(Constants.DRS_API_KEY, drsApiKey)
            putExtra(Constants.CONSIGNEE_NAME, consigneeName)
            putExtra(Constants.OFD_OTP, ofdOtp)
            putExtra(Constants.CALL_ALLOWED, callAllowed)
            putExtra(Constants.CONSIGNEE_MOBILE, consigneeMobile)
            putExtra(Constants.AMAZON_ENCRYPTED_OTP, amazonEncryptedOtp)
            putExtra(Constants.AMAZON, amazon)
            putExtra(Constants.COMPOSITE_KEY, compositeKey)
            putExtra(Constants.AWB_NUMBER, awbNumber)
            putExtra(Constants.RESEND_SECURE_OTP, resendSecureOtp)
            putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, consigneeAlternateMobile)
            putExtra(Constants.IS_FROM_MPS, true)
        }
        startActivity(intent)
        CommonUtils.applyTransitionToOpenActivity(this)
    }

    override fun getViewModel(): MpsPickupScanViewModel {
        return mpsPickupScanViewModel
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_mps_pickup
    }
}