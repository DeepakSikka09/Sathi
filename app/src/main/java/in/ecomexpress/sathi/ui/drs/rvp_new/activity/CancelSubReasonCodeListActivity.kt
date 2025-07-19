package `in`.ecomexpress.sathi.ui.drs.rvp_new.activity

import android.app.Activity
import android.content.Intent
import `in`.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.library.baseAdapters.BR
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import `in`.ecomexpress.sathi.BuildConfig
import `in`.ecomexpress.sathi.R
import `in`.ecomexpress.sathi.databinding.ActivityCancelSubReasonCodeListBinding
import `in`.ecomexpress.sathi.databinding.CancelReasonCodeListActivityBinding
import `in`.ecomexpress.sathi.repo.local.data.rvp.RadioButtonItem
import `in`.ecomexpress.sathi.repo.remote.model.drs_list.forward.SecureDelivery
import `in`.ecomexpress.sathi.repo.remote.model.masterdata.RVPReasonCodeMaster
import `in`.ecomexpress.sathi.ui.base.BaseActivity
import `in`.ecomexpress.sathi.ui.drs.rvp_new.adapter.CancelReasonCodeAdapter
import `in`.ecomexpress.sathi.ui.drs.rvp_new.adapter.CancelSubReasonCodeAdapter
import `in`.ecomexpress.sathi.ui.drs.rvp_new.navigator.RvpQcNavigator
import `in`.ecomexpress.sathi.ui.drs.rvp_new.viewmodel.CancelReasonCodeListViewModel
import `in`.ecomexpress.sathi.ui.drs.rvp_new.viewmodel.CancelSubReasonCodeListViewModel
import `in`.ecomexpress.sathi.utils.CommonUtils
import `in`.ecomexpress.sathi.utils.Constants

@AndroidEntryPoint
class CancelSubReasonCodeListActivity :  BaseActivity<ActivityCancelSubReasonCodeListBinding, CancelSubReasonCodeListViewModel>(),
    RvpQcNavigator {

    private lateinit var binding: ActivityCancelSubReasonCodeListBinding
    private lateinit var adapter: CancelSubReasonCodeAdapter
    private lateinit var reasonCodeList: List<RVPReasonCodeMaster>
    private val cancelSubReasonCodeListViewModel: CancelSubReasonCodeListViewModel by viewModels()
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
    private var otpRequiredForDelivery: String = ""
    private var resendSecureOtp: Boolean = false
    private var consigneeAlternateMobile: String = ""
    private var itemDescription: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cancel_sub_reason_code_list)
        getIntentData()
        // Initialize adapter with lambda to handle selected reason
        adapter = CancelSubReasonCodeAdapter(mutableListOf()) { position ->
            goToNextActivity(position)
        }
        binding.recyclerView2.layoutManager = LinearLayoutManager(this)
        binding.recyclerView2.adapter = adapter
        val selectedReason = intent.getStringExtra("SELECTED_REASON")
        val reasonTextView: MaterialTextView = findViewById(R.id.accept_refuge)
        reasonTextView.text = selectedReason

        // Observe ViewModel's LiveData and update adapter when data changes
        cancelSubReasonCodeListViewModel.subReasonList.observe(this) { subGroupData ->
            if (subGroupData.isNotEmpty()) {
                reasonCodeList=subGroupData
                val reasonList = subGroupData.map { item ->
                    RadioButtonItem(item.reasonMessage) // Convert to RadioButtonItem
                }
                adapter.updateData(reasonList)
            } else {
                Toast.makeText(this, "No data received", Toast.LENGTH_SHORT).show()
            }
        }
        if (selectedReason != null) {
            cancelSubReasonCodeListViewModel.getSubReasonCodeFromSubGroup(selectedReason)
        }

        binding.header.headingName.setText(R.string.pickup_cancel)
        binding.header.backArrow.setOnClickListener { onBackClick() }
        binding.header.versionName.text = "v${BuildConfig.VERSION_NAME}"




    }

    override fun getViewModel(): CancelSubReasonCodeListViewModel {
        return  cancelSubReasonCodeListViewModel
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_cancel_sub_reason_code_list
    }

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
                callAllowed = it.getBooleanExtra(Constants.CALL_ALLOWED, false)
                consigneeMobile = it.getStringExtra(Constants.CONSIGNEE_MOBILE) ?: ""
                amazonEncryptedOtp = it.getStringExtra(Constants.AMAZON_ENCRYPTED_OTP) ?: ""
                amazon = it.getStringExtra(Constants.AMAZON) ?: ""
                compositeKey = it.getStringExtra(Constants.COMPOSITE_KEY) ?: ""
                awbNumber = it.getStringExtra(Constants.AWB_NUMBER) ?: ""
                otpRequiredForDelivery = it.getStringExtra(Constants.otp_required_for_delivery) ?: ""
                resendSecureOtp = it.getBooleanExtra(Constants.RESEND_SECURE_OTP, false)
                consigneeAlternateMobile = it.getStringExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE) ?: ""

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun goToNextActivity(selectedReason: Int) {
        navigateToNextActivityWithData(CancelPickupRemarksActivity::class.java,awbNumber,selectedReason)

    }

    private fun navigateToNextActivityWithData(destinationActivity: Class<out Activity>, awbNumber: String,selectedReason: Int) {
        val intent = Intent(this, destinationActivity).apply {
            putExtra(Constants.DRS_ID, drsId)
            putExtra(Constants.SECURE_DELIVERY, secureDelivery)
            putExtra(Constants.SECURE_DELIVERY_OTP, isSecureDelivery)
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
            putExtra(Constants.otp_required_for_delivery, otpRequiredForDelivery)
            putExtra(Constants.RESEND_SECURE_OTP, resendSecureOtp)
            putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, consigneeAlternateMobile)
            putExtra("SELECTED_REASON", reasonCodeList[selectedReason])
        }
        startActivity(intent)
        CommonUtils.applyTransitionToOpenActivity(this)
    }



    private fun onBackClick() {
        finish()
        applyTransitionToBackFromActivity(this)
    }



}
