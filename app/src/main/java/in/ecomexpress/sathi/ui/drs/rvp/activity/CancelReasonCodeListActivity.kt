package `in`.ecomexpress.sathi.ui.drs.rvp.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import `in`.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import dagger.hilt.android.AndroidEntryPoint
import `in`.ecomexpress.sathi.BuildConfig
import `in`.ecomexpress.sathi.R
import `in`.ecomexpress.sathi.databinding.CancelReasonCodeListActivityBinding
import `in`.ecomexpress.sathi.repo.local.data.rvp.RadioButtonItem
import `in`.ecomexpress.sathi.repo.remote.model.drs_list.forward.SecureDelivery
import `in`.ecomexpress.sathi.ui.base.BaseActivity
import `in`.ecomexpress.sathi.ui.drs.rvp.adapter.CancelReasonCodeAdapter
import `in`.ecomexpress.sathi.ui.drs.rvp.navigator.RvpQcNavigator
import `in`.ecomexpress.sathi.ui.drs.rvp.viewmodel.CancelReasonCodeListViewModel
import `in`.ecomexpress.sathi.utils.CommonUtils
import `in`.ecomexpress.sathi.utils.Constants

@AndroidEntryPoint
class CancelReasonCodeListActivity : BaseActivity<CancelReasonCodeListActivityBinding, CancelReasonCodeListViewModel>(), RvpQcNavigator {

    private lateinit var adapter: CancelReasonCodeAdapter
    private lateinit var binding: CancelReasonCodeListActivityBinding
    private val cancelReasonCodeListViewModel: CancelReasonCodeListViewModel by viewModels()

    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    }

    private var drsPin: String = ""
    private var drsPstnKey: String = ""
    private var drsApiKey: String = ""
    private var drsId: String = ""
    private var secureDelivery: SecureDelivery? = null
    private var isSecureDelivery: Boolean = false
    private var isFromMps: Boolean = false
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

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.cancel_reason_code_list_activity)

        // Initialize adapter with lambda to handle selected reason
        adapter = CancelReasonCodeAdapter(mutableListOf()) { selectedReason ->
            saveSelectedReasonToSharedPreferences(selectedReason)
            goToNextActivity(selectedReason)
        }
        getIntentData()

        // Set up RecyclerView with adapter and layout manager
        binding.recyclerView1.layoutManager = LinearLayoutManager(this)
        binding.recyclerView1.adapter = adapter

        // Observe ViewModel's LiveData and update adapter when data changes
        cancelReasonCodeListViewModel.subGroupData.observe(this) { subGroupData ->
            if (subGroupData.isNotEmpty()) {
                val reasonList = subGroupData.map { item ->
                    RadioButtonItem(item) // Convert to RadioButtonItem
                }
                adapter.updateData(reasonList)
            } else {
                Toast.makeText(this, "No data received", Toast.LENGTH_SHORT).show()
            }
        }

        // Fetch data
        cancelReasonCodeListViewModel.getSubgroupFromRvpReasonCode()

        // Set up header details
        binding.header.headingName.setText(R.string.pickup_cancel)
        binding.header.backArrow.setOnClickListener { onBackClick() }
        binding.header.versionName.text = "v${BuildConfig.VERSION_NAME}"
    }

    // Save selected reason code to SharedPreferences
    private fun saveSelectedReasonToSharedPreferences(selectedReason: String) {
        sharedPreferences.edit().putString("SELECTED_REASON", selectedReason).apply()
    }

    override fun getViewModel(): CancelReasonCodeListViewModel {
        return cancelReasonCodeListViewModel
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.cancel_reason_code_list_activity
    }

    private fun goToNextActivity(selectedReason: String) {
        navigateToNextActivityWithData(CancelSubReasonCodeListActivity::class.java,awbNumber, selectedReason)
    }

    private fun navigateToNextActivityWithData(destinationActivity: Class<out Activity>, awbNumber: String,selectedReason: String) {
        val intent = Intent(this, destinationActivity).apply {
            putExtra(Constants.DRS_ID, drsId)
            putExtra(Constants.SECURE_DELIVERY, secureDelivery)
            putExtra(Constants.SECURE_DELIVERY_OTP, isSecureDelivery)
            putExtra(Constants.IS_FROM_MPS, isFromMps)
            putExtra(Constants.SHIPMENT_TYPE, shipmentType)
            putExtra(Constants.DRS_PIN, drsPin)
            putExtra(Constants.DRS_PSTN_KEY, drsPstnKey)
            putExtra(Constants.DRS_API_KEY, drsApiKey)
            putExtra(Constants.ITEM_DESCRIPTION, itemDescription)
            putExtra(Constants.OFD_OTP, ofdOtp)
            putExtra(Constants.CALL_ALLOWED, callAllowed)
            putExtra(Constants.CONSIGNEE_MOBILE, consigneeMobile)
            putExtra(Constants.AMAZON_ENCRYPTED_OTP, amazonEncryptedOtp)
            putExtra(Constants.AMAZON, amazon)
            putExtra(Constants.COMPOSITE_KEY, compositeKey)
            putExtra(Constants.AWB_NUMBER, awbNumber)
            putExtra(Constants.RESEND_SECURE_OTP, resendSecureOtp)
            putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, consigneeAlternateMobile)
            putExtra("SELECTED_REASON", selectedReason)
        }
        startActivity(intent)
        CommonUtils.applyTransitionToOpenActivity(this)
    }

    @Suppress("DEPRECATION")
    private fun getIntentData() {
        try {
            intent?.let {
                drsId = it.getStringExtra(Constants.DRS_ID) ?: ""
                secureDelivery = it.getParcelableExtra(Constants.SECURE_DELIVERY)
                isSecureDelivery = it.getBooleanExtra(Constants.SECURE_DELIVERY_OTP, false)
                isFromMps = it.getBooleanExtra(Constants.IS_FROM_MPS, false)
                shipmentType = it.getStringExtra(Constants.SHIPMENT_TYPE) ?: ""
                drsPin = it.getStringExtra(Constants.DRS_PIN) ?: ""
                drsPstnKey = it.getStringExtra(Constants.DRS_PSTN_KEY) ?: ""
                drsApiKey = it.getStringExtra(Constants.DRS_API_KEY) ?: ""
                itemDescription = it.getStringExtra(Constants.ITEM_DESCRIPTION) ?: ""
                ofdOtp = it.getStringExtra(Constants.OFD_OTP) ?: ""
                callAllowed = it.getBooleanExtra(Constants.CALL_ALLOWED, false)
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

    private fun onBackClick() {
        finish()
        applyTransitionToBackFromActivity(this)
    }
}