package `in`.ecomexpress.sathi.ui.drs.rvp.activity

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
import `in`.ecomexpress.sathi.ui.drs.rvp.navigator.RvpQcNavigator
import `in`.ecomexpress.sathi.ui.drs.rvp.viewmodel.RvpCommonViewModel
import `in`.ecomexpress.sathi.ui.drs.todolist.ToDoListActivity
import `in`.ecomexpress.sathi.utils.CommonUtils
import `in`.ecomexpress.sathi.utils.Constants
import `in`.ecomexpress.sathi.utils.Constants.RQC
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class RvpQcSuccessFailActivity : BaseActivity<ActivityRvpqcSuccessFailBinding, RvpCommonViewModel>(), RvpQcNavigator {

    private val activityRvpQcSuccessFailBinding: ActivityRvpqcSuccessFailBinding by lazy {
        ActivityRvpqcSuccessFailBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var rvpCommit: RvpCommit
    private val rvpCommonViewModel: RvpCommonViewModel by viewModels()
    private var awbNumber: String = ""
    private var shipmentType: String = ""
    private var listBlue: ColorStateList? = null
    private var listGray: ColorStateList? = null
    private var qcWizards: ArrayList<RvpCommit.QcWizard> = ArrayList()
    private var drsId: String = ""
    private var itemDescription: String = ""
    private var isOfdOtpVerified: String = ""
    private var scannedFlyerValue: String = ""
    private var consigneeAlternateMobile: String = ""
    private var consigneeName: String = ""
    private var isFailed = 3
    private var isFrom = ""
    private var isImageMissing = false
    private var smartQc = "false"
    private var isFromCustomerNotSharingOtp = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityRvpQcSuccessFailBinding.root)
        getIntentData()
        setupSubmitButton()
        setupHeader()
        setupAWBHeader()
        setupBackNavigation()
        interactWithViewModel()
        setupShipmentStatusMessage()
    }

    @Suppress("DEPRECATION")
    private fun getIntentData() {
        try {
            intent?.let {
                drsId = it.getStringExtra(Constants.DRS_ID) ?: ""
                itemDescription = it.getStringExtra(Constants.ITEM_DESCRIPTION) ?: ""
                isOfdOtpVerified = it.getStringExtra(Constants.OFD_OTP) ?: ""
                consigneeAlternateMobile = it.getStringExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE) ?: ""
                consigneeName = it.getStringExtra(Constants.CONSIGNEE_NAME) ?: ""
                scannedFlyerValue = it.getStringExtra(Constants.RVP_FLYER_SCANNED_VALUE) ?: ""
                isFailed = it.getIntExtra(Constants.IS_FAILED, 3)
                isFrom = it.getStringExtra(Constants.IS_FROM) ?: ""
                shipmentType = it.getStringExtra(Constants.SHIPMENT_TYPE) ?: ""
                awbNumber = it.getStringExtra(Constants.AWB_NUMBER) ?: ""
                qcWizards = it.getParcelableArrayListExtra(Constants.QC_WIZARDS) ?: ArrayList()
                isImageMissing = it.getBooleanExtra(Constants.IS_IMAGE_MISSING, false)
                smartQc = it.getStringExtra(Constants.SMART_QC_ENABLED) ?: ""
                isFromCustomerNotSharingOtp = it.getBooleanExtra(Constants.IS_FROM_CUSTOMER_NOT_SHARING_OTP, false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupAWBHeader() {
        if (smartQc.equals("true", true)) {
            activityRvpQcSuccessFailBinding.rqcAwbHeader.awbNumber.apply {
                setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.smart_qc, 0)
                text = getString(R.string.awb) + " $awbNumber"
            }
        } else {
            val iconResource = if (shipmentType.equals(Constants.RVP, ignoreCase = true)) R.drawable.rvp_tag_icon else R.drawable.rqc_tag_icon
            activityRvpQcSuccessFailBinding.rqcAwbHeader.awbNumber.apply {
                setCompoundDrawablesWithIntrinsicBounds(0, 0, iconResource, 0)
                text = getString(R.string.awb) + " $awbNumber"
            }
        }
    }

    private fun setupSubmitButton() {
        listBlue = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.button_enable_color))
        listGray = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.button_disable_color))
        if (shipmentType.equals(Constants.RVP, ignoreCase = true)) {
            with(activityRvpQcSuccessFailBinding.bottomSubmitButton.submitButton) {
                isClickable = true
                backgroundTintList = listBlue
                text = getString(R.string.submitButton)
                setOnClickListener {
                    createCommitPacketAndUploadShipmentData()
                    isClickable = false
                    backgroundTintList = listGray
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
                text = getString(R.string.submitButton)
                setOnClickListener {
                    openNextActivity()
                }
            }
        }
    }

    private fun createCommitPacketAndUploadShipmentData() {
        try {
            rvpCommit.apply {
                drsId = this@RvpQcSuccessFailActivity.drsId
                awb = awbNumber
                attemptType = shipmentType
                status = if (isFailed == 0 || isFromCustomerNotSharingOtp) {
                    Constants.RVPUNDELIVERED
                } else {
                    Constants.RVPDELIVERED
                }
                attemptReasonCode = if (isFailed == 0) {
                    Constants.RQC_FAILED_REASON_CODE
                } else if(isFromCustomerNotSharingOtp) {
                    Constants.RVP_QC_CUSTOMER_NOT_SHARING_OTP_REASON_CODE
                } else{
                    Constants.RVP_DELIVERED_REASON_CODE
                }
                refPackageBarcode = scannedFlyerValue
                drsCommitDateTime = Calendar.getInstance().timeInMillis.toString()
                trip_id = rvpCommonViewModel.dataManager.getTripId()
                call_attempt_count = rvpCommonViewModel.dataManager.getRVPCallCount(awbNumber + Constants.RVP)
                map_activity_count = rvpCommonViewModel.dataManager.getRVPMapCount(awbNumber.toLong())
                trying_reach = rvpCommonViewModel.dataManager.getTryReachingCount(awbNumber + Constants.TRY_RECHING_COUNT).toString()
                techpark = rvpCommonViewModel.dataManager.getSendSmsCount(awbNumber + Constants.TECH_PARK_COUNT).toString()
                locationLat = rvpCommonViewModel.dataManager.getCurrentLatitude().toString()
                locationLong = rvpCommonViewModel.dataManager.getCurrentLongitude().toString()
                feEmpCode = rvpCommonViewModel.dataManager.getEmp_code()
                ofd_otp_verified = isOfdOtpVerified == getString(R.string.verified)
                consigneeName = this@RvpQcSuccessFailActivity.consigneeName
                ofd_otp_verify_status = if (isOfdOtpVerified == getString(R.string.verified)) {
                    isOfdOtpVerified
                } else {
                    getString(R.string.none)
                }
                ud_otp_verify_status = getString(R.string.none)
                rd_otp_verify_status = getString(R.string.none)
                start_qc_lat = rvpCommonViewModel.dataManager.currentLatitude.toString()
                start_qc_lng = rvpCommonViewModel.dataManager.currentLongitude.toString()
                qcWizard = qcWizards
            }
            rvpCommonViewModel.addImageListAndDoCommitApiCall(this@RvpQcSuccessFailActivity, rvpCommit, isNetworkConnected)
            rvpCommonViewModel.commitApiVerifyStatus.observe(this) { isShipmentCommitted ->
                if (isShipmentCommitted) {
                    if(isFromCustomerNotSharingOtp){
                        rvpCommonViewModel.deleteRVPQCData(drsId.toInt(), awbNumber.toLong())
                    }
                    openNextActivity()
                } else {
                    activityRvpQcSuccessFailBinding.bottomSubmitButton.submitButton.isClickable = true
                    activityRvpQcSuccessFailBinding.bottomSubmitButton.submitButton.backgroundTintList = listBlue
                }
            }
        } catch (e: Exception) {
            showMessageOnUI("Exception: ${e.localizedMessage}", true)
        }
    }

    private fun showMessageOnUI(message: String?, isError: Boolean) {
        message?.let {
            if (isError) showSnackbar(it) else showSuccessInfo(it)
        }
    }

    private fun setupShipmentStatusMessage() {
        if (isFromCustomerNotSharingOtp) {
            activityRvpQcSuccessFailBinding.shipmentStatusMessage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.cross, 0, 0)
            activityRvpQcSuccessFailBinding.shipmentStatusMessage.apply {
                text = context.getString(R.string.awb_npickup_cancelled, awbNumber)
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
                    activityRvpQcSuccessFailBinding.shipmentStatusMessage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.cross, 0, 0)
                }

                3 -> {
                    activityRvpQcSuccessFailBinding.shipmentStatusMessage.apply {
                        text = getString(R.string.order_completed_returned_the_shipment_back_to_dc)
                    }
                }

                else -> {
                    activityRvpQcSuccessFailBinding.shipmentStatusMessage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.cross, 0, 0)
                    activityRvpQcSuccessFailBinding.shipmentStatusMessage.apply {
                        text = getString(R.string.qc_failed)
                    }
                }
            }
        } else {
            activityRvpQcSuccessFailBinding.shipmentStatusMessage.apply {
                text = context.getString(R.string.please_complete_the_order_and_handover_the_shipment_to_dc)
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
        finish()
    }

    @SuppressLint("SetTextI18n")
    private fun setupHeader() {
        activityRvpQcSuccessFailBinding.header.headingName.text = getString(R.string.mark_complete)
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
                    0 -> {
                        createCommitPacketAndUploadShipmentData()
                        activityRvpQcSuccessFailBinding.bottomSubmitButton.submitButton.isClickable = false
                        activityRvpQcSuccessFailBinding.bottomSubmitButton.submitButton.backgroundTintList = listGray
                        isFailed = 4
                    }

                    1 -> {
                        navigateToNextActivityWithData(CaptureScanActivity::class.java)
                    }

                    3 -> {
                        createCommitPacketAndUploadShipmentData()
                        activityRvpQcSuccessFailBinding.bottomSubmitButton.submitButton.isClickable = false
                        activityRvpQcSuccessFailBinding.bottomSubmitButton.submitButton.backgroundTintList = listGray
                        isFailed = 4
                    }

                    4 -> {
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
            putExtra(Constants.OFD_OTP, isOfdOtpVerified)
            putExtra(Constants.AWB_NUMBER, awbNumber)
            putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, consigneeAlternateMobile)
            putExtra(Constants.CONSIGNEE_NAME, consigneeName)
            putParcelableArrayListExtra(Constants.QC_WIZARDS, qcWizards)
            putExtra(Constants.IS_FAILED, isFailed)
            putExtra(Constants.IS_IMAGE_MISSING, isImageMissing)
            putExtra(Constants.SMART_QC_ENABLED, smartQc)
        }.also { intent ->
            startActivity(intent)
            CommonUtils.applyTransitionToOpenActivity(this)
        }
    }

    private fun interactWithViewModel() {
        rvpCommonViewModel.showErrorMessageLiveData.observe(this) { (message, isError) ->
            showMessageOnUI(message, isError)
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