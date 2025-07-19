package `in`.ecomexpress.sathi.ui.drs.rvp_new.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import `in`.ecomexpress.sathi.BR
import `in`.ecomexpress.sathi.BuildConfig
import `in`.ecomexpress.sathi.R
import `in`.ecomexpress.sathi.databinding.ActivityRvpqcSuccessFailBinding
import `in`.ecomexpress.sathi.repo.local.data.rvp.RvpCommit
import `in`.ecomexpress.sathi.ui.base.BaseActivity
import `in`.ecomexpress.sathi.ui.drs.rvp_new.navigator.RvpQcNavigator
import `in`.ecomexpress.sathi.ui.drs.rvp_new.viewmodel.RvpCommonViewModel
import `in`.ecomexpress.sathi.ui.drs.todolist.ToDoListActivity
import `in`.ecomexpress.sathi.utils.CommonUtils
import `in`.ecomexpress.sathi.utils.Constants
import `in`.ecomexpress.sathi.utils.Constants.RQC

@AndroidEntryPoint
class RvpQcSuccessFailActivity : BaseActivity<ActivityRvpqcSuccessFailBinding, RvpCommonViewModel>(), RvpQcNavigator {

    private val activityRvpQcSuccessFailBinding: ActivityRvpqcSuccessFailBinding by lazy {
        ActivityRvpqcSuccessFailBinding.inflate(layoutInflater)
    }

    private val rvpCommonViewModel: RvpCommonViewModel by viewModels()
    private var awbNumber: String = ""
    private var shipmentType: String = ""
    private var listBlue: ColorStateList? = null
    private var qcWizards: ArrayList<RvpCommit.QcWizard> = ArrayList()
    private var drsId: String = ""
    private var itemDescription: String = ""
    private var ofdOtp: String = ""
    private var consigneeAlternateMobile: String = ""
    private var consigneeName: String = ""
    private var isFailed = 3
    private var isFrom = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityRvpQcSuccessFailBinding.root)
        getIntentData()
        setupSubmitButton()
        setupHeader()
        setupBackNavigation()
        setupShipmentStatusMessage()
    }

    private fun getIntentData() {
        try {
            intent?.let {
                drsId = it.getStringExtra(Constants.DRS_ID) ?: ""
                itemDescription = it.getStringExtra(Constants.ITEM_DESCRIPTION) ?: ""
                ofdOtp = it.getStringExtra(Constants.OFD_OTP) ?: ""
                consigneeAlternateMobile = it.getStringExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE) ?: ""
                consigneeName = it.getStringExtra(Constants.CONSIGNEE_NAME) ?: ""
                isFailed = it.getIntExtra(Constants.IS_FAILED, 3)
                isFrom = it.getStringExtra(Constants.IS_FROM) ?: ""
                shipmentType = it.getStringExtra(Constants.SHIPMENT_TYPE) ?: ""
                awbNumber = it.getStringExtra(Constants.AWB_NUMBER) ?: ""
                qcWizards = it.getParcelableArrayListExtra(Constants.QC_WIZARDS) ?: ArrayList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupSubmitButton() {
        listBlue = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.button_enable_color))
        if (shipmentType.equals(Constants.RVP, ignoreCase = true)) {
            with(activityRvpQcSuccessFailBinding.bottomSubmitButton.submitButton) {
                isClickable = true
                backgroundTintList = listBlue
                text = getString(R.string.go_to_next_shipment)
                setOnClickListener {
                    openNextActivity()
                }
            }
        } else if (isFrom.equals("RQC_MARK", true)) {
            with(activityRvpQcSuccessFailBinding.bottomSubmitButton.submitButton) {
                isClickable = true
                backgroundTintList = listBlue
                text = if (isFailed == 1) {
                    getString(R.string.start_packaging)
                } else {
                    getString(R.string.mark_qc_failed)
                }
                setOnClickListener {
                    openNextActivity()
                }
            }
        } else {
            with(activityRvpQcSuccessFailBinding.bottomSubmitButton.submitButton) {
                isClickable = true
                backgroundTintList = listBlue
                text = getString(R.string.go_to_next_shipment)
                setOnClickListener {
                    openNextActivity()
                }
            }
        }
    }

    private fun setupShipmentStatusMessage() {
        if (shipmentType.equals(Constants.RVP, ignoreCase = true)) {
            activityRvpQcSuccessFailBinding.shipmentStatusMessage.apply {
                text = getString(R.string.order_completed_returned_the_shipment_back_to_dc)
            }
        } else if (shipmentType.equals(RQC, true)) {
            when (isFailed) {
                1 -> {
                    activityRvpQcSuccessFailBinding.shipmentStatusMessage.apply {
                        text = getString(R.string.qc_passed_pick_the_product)
                    }
                }
                0 -> {
                    activityRvpQcSuccessFailBinding.shipmentStatusMessage.apply {
                        text = getString(R.string.qc_failed)
                    }
                    activityRvpQcSuccessFailBinding.shipmentStatusMessage.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        R.drawable.cross,
                        0,
                        0
                    )
                }
                else -> {
                    activityRvpQcSuccessFailBinding.shipmentStatusMessage.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        R.drawable.cross,
                        0,
                        0
                    )
                    activityRvpQcSuccessFailBinding.shipmentStatusMessage.apply {
                        text = getString(R.string.qc_failed)
                    }
                }
            }
        } else {
            activityRvpQcSuccessFailBinding.shipmentStatusMessage.apply {
                text = getString(R.string.order_completed_returned_the_shipment_back_to_dc)
            }
        }
    }

    private fun setupBackNavigation() {
        activityRvpQcSuccessFailBinding.header.backArrow.setOnClickListener { backPress() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPress()
            }
        })
    }

    private fun backPress() {
        if (shipmentType.equals(Constants.RVP, ignoreCase = true)) {
            showSnackbar(getString(R.string.can_t_move_back))
        } else {
            finish()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun setupHeader() {
        activityRvpQcSuccessFailBinding.header.headingName.text = awbNumber
        activityRvpQcSuccessFailBinding.header.versionName.text = "v" + BuildConfig.VERSION_NAME
    }

    private fun openNextActivity() {
        try {
            if (shipmentType.equals(Constants.RVP, ignoreCase = true)) {
                val intent = ToDoListActivity.getStartIntent(this).apply {
                    setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    putExtra(ToDoListActivity.ITEM_MARKED, awbNumber)
                    putExtra(ToDoListActivity.SHIPMENT_TYPE, shipmentType)
                    putExtra(ToDoListActivity.SHIPMENT_STATUS, Constants.SHIPMENT_DELIVERED_STATUS)
                }
                finish()
                startActivity(intent)
                CommonUtils.applyTransitionToOpenActivity(this)
            } else {
                when (isFailed) {
                    1 -> {
                        navigateToNextActivityWithData(CaptureScanActivity::class.java)
                    }
                    0 -> {
                        navigateToNextActivityWithData(RvpQcSignatureActivity::class.java)
                    }
                    else -> {
                        val intent = ToDoListActivity.getStartIntent(this).apply {
                            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                            putExtra(ToDoListActivity.ITEM_MARKED, awbNumber)
                            putExtra(ToDoListActivity.SHIPMENT_TYPE, shipmentType)
                            putExtra(ToDoListActivity.SHIPMENT_STATUS, Constants.SHIPMENT_DELIVERED_STATUS)
                        }
                        finish()
                        startActivity(intent)
                        CommonUtils.applyTransitionToOpenActivity(this)
                    }
                }
            }
        } catch (e: Exception) {
            showSnackbar("Exception: ${e.localizedMessage}")
        }
    }

    private fun navigateToNextActivityWithData(destinationActivity: Class<out Activity>) {
        Intent(this, destinationActivity).apply {
            putExtra(Constants.DRS_ID, drsId)
            putExtra(Constants.SHIPMENT_TYPE, shipmentType)
            putExtra(Constants.ITEM_DESCRIPTION, itemDescription)
            putExtra(Constants.OFD_OTP, ofdOtp)
            putExtra(Constants.AWB_NUMBER, awbNumber)
            putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, consigneeAlternateMobile)
            putExtra(Constants.CONSIGNEE_NAME, consigneeName)
            putParcelableArrayListExtra(Constants.QC_WIZARDS, qcWizards)
            putExtra(Constants.IS_FAILED, isFailed)
        }.also { intent ->
            startActivity(intent)
            CommonUtils.applyTransitionToOpenActivity(this)
        }
    }

    override fun getViewModel(): RvpCommonViewModel {
        return rvpCommonViewModel
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_rvpqc_success_fail
    }
}