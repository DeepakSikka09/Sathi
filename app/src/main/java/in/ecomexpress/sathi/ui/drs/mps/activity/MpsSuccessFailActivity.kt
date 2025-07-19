package `in`.ecomexpress.sathi.ui.drs.mps.activity

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
import `in`.ecomexpress.sathi.databinding.ActivityMpsSuccessFailBinding
import `in`.ecomexpress.sathi.repo.local.data.rvp.RvpCommit
import `in`.ecomexpress.sathi.ui.base.BaseActivity
import `in`.ecomexpress.sathi.ui.drs.mps.viewmodel.MpsPickupScanViewModel
import `in`.ecomexpress.sathi.ui.drs.rvp.navigator.RvpQcNavigator
import `in`.ecomexpress.sathi.ui.drs.todolist.ToDoListActivity
import `in`.ecomexpress.sathi.utils.CommonUtils
import `in`.ecomexpress.sathi.utils.Constants
import `in`.ecomexpress.sathi.utils.Constants.RQC
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class MpsSuccessFailActivity : BaseActivity<ActivityMpsSuccessFailBinding, MpsPickupScanViewModel>(), RvpQcNavigator {

    private val activityMpsSuccessFailBinding: ActivityMpsSuccessFailBinding by lazy {
        ActivityMpsSuccessFailBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var rvpCommit: RvpCommit
    private var qcItemForCommit: ArrayList<RvpCommit.QcItem> = ArrayList()
    private val mpsPickupScanViewModel: MpsPickupScanViewModel by viewModels()
    private var awbNumber: String = ""
    private var shipmentType: String = ""
    private var listBlue: ColorStateList? = null
    private var listGray: ColorStateList? = null
    private var drsId: String = ""
    private var itemDescription: String = ""
    private var isOfdOtpVerified: String = ""
    private var scannedFlyerValue: String = ""
    private var consigneeAlternateMobile: String = ""
    private var consigneeName: String = ""
    private var isFailed = 3
    private var isFrom = ""
    private var isImageMissing = false
    private var isFromCustomerNotSharingOtp = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMpsSuccessFailBinding.root)
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
                qcItemForCommit = it.getParcelableArrayListExtra(Constants.QC_WIZARDS) ?: ArrayList()
                isImageMissing = it.getBooleanExtra(Constants.IS_IMAGE_MISSING, false)
                isFromCustomerNotSharingOtp = it.getBooleanExtra(Constants.IS_FROM_CUSTOMER_NOT_SHARING_OTP, false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupAWBHeader() {
        val iconResource = R.drawable.mps_tag_icon
        activityMpsSuccessFailBinding.rqcAwbHeader.awbNumber.apply {
            setCompoundDrawablesWithIntrinsicBounds(0, 0, iconResource, 0)
            text = getString(R.string.awb) + " $awbNumber"
        }
    }

    private fun setupSubmitButton() {
        listBlue = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.button_enable_color))
        listGray = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.button_disable_color))
        if (isFrom.equals("RQC_MARK", true)) {
            with(activityMpsSuccessFailBinding.bottomSubmitButton.submitButton) {
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
            with(activityMpsSuccessFailBinding.bottomSubmitButton.submitButton) {
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
                drsId = this@MpsSuccessFailActivity.drsId
                awb = awbNumber
                attemptType = shipmentType
                isRvp_mps = true
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
                trip_id = mpsPickupScanViewModel.dataManager.getTripId()
                call_attempt_count = mpsPickupScanViewModel.dataManager.getRVPCallCount(awbNumber + Constants.RVP)
                map_activity_count = mpsPickupScanViewModel.dataManager.getRVPMapCount(awbNumber.toLong())
                trying_reach = mpsPickupScanViewModel.dataManager.getTryReachingCount(awbNumber + Constants.TRY_RECHING_COUNT).toString()
                techpark = mpsPickupScanViewModel.dataManager.getSendSmsCount(awbNumber + Constants.TECH_PARK_COUNT).toString()
                locationLat = mpsPickupScanViewModel.dataManager.getCurrentLatitude().toString()
                locationLong = mpsPickupScanViewModel.dataManager.getCurrentLongitude().toString()
                feEmpCode = mpsPickupScanViewModel.dataManager.getEmp_code()
                ofd_otp_verified = isOfdOtpVerified == getString(R.string.verified)
                consigneeName = this@MpsSuccessFailActivity.consigneeName
                ofd_otp_verify_status = if (isOfdOtpVerified == getString(R.string.verified)) {
                    isOfdOtpVerified
                } else {
                    getString(R.string.none)
                }
                ud_otp_verify_status = getString(R.string.none)
                rd_otp_verify_status = getString(R.string.none)
                start_qc_lat = mpsPickupScanViewModel.dataManager.currentLatitude.toString()
                start_qc_lng = mpsPickupScanViewModel.dataManager.currentLongitude.toString()
                qcItem = qcItemForCommit
                total_item_number = qcItemForCommit.size
            }
            mpsPickupScanViewModel.addImageListAndDoCommitApiCall(this@MpsSuccessFailActivity, rvpCommit, isNetworkConnected)
            mpsPickupScanViewModel.commitApiVerifyStatus.observe(this) { isShipmentCommitted ->
                if (isShipmentCommitted) {
                    if(isFromCustomerNotSharingOtp){
                        mpsPickupScanViewModel.deleteRVPQCData(drsId.toInt(), awbNumber.toLong())
                    }
                    openNextActivity()
                } else {
                    activityMpsSuccessFailBinding.bottomSubmitButton.submitButton.isClickable = true
                    activityMpsSuccessFailBinding.bottomSubmitButton.submitButton.backgroundTintList = listBlue
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
            activityMpsSuccessFailBinding.shipmentStatusMessage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.cross, 0, 0)
            activityMpsSuccessFailBinding.shipmentStatusMessage.apply {
                text = context.getString(R.string.awb_npickup_cancelled, awbNumber)
            }
        } else if (shipmentType.equals(RQC, true)) {
            when (isFailed) {
                1 -> {
                    activityMpsSuccessFailBinding.shipmentStatusMessage.apply {
                        text = getString(R.string.qc_passed_pick_the_product)
                    }
                }

                0 -> {
                    activityMpsSuccessFailBinding.shipmentStatusMessage.apply {
                        text = getString(R.string.qc_failed)
                    }
                    activityMpsSuccessFailBinding.shipmentStatusMessage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.cross, 0, 0)
                }

                3 -> {
                    activityMpsSuccessFailBinding.shipmentStatusMessage.apply {
                        text = getString(R.string.order_completed_returned_the_shipment_back_to_dc)
                    }
                }

                else -> {
                    activityMpsSuccessFailBinding.shipmentStatusMessage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.cross, 0, 0)
                    activityMpsSuccessFailBinding.shipmentStatusMessage.apply {
                        text = getString(R.string.qc_failed)
                    }
                }
            }
        } else {
            activityMpsSuccessFailBinding.shipmentStatusMessage.apply {
                text = context.getString(R.string.please_complete_the_order_and_handover_the_shipment_to_dc)
            }
        }
    }

    private fun setupBackNavigation() {
        activityMpsSuccessFailBinding.header.backArrow.setOnClickListener { backPress() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPress()
            }
        })
    }

    private fun backPress() {
        showSnackbar(getString(R.string.can_t_move_back))
    }

    @SuppressLint("SetTextI18n")
    private fun setupHeader() {
        activityMpsSuccessFailBinding.header.headingName.text = getString(R.string.mark_complete)
        activityMpsSuccessFailBinding.header.versionName.text = "v" + BuildConfig.VERSION_NAME
    }

    private fun openNextActivity() {
        try {
            when (isFailed) {
                0,3 -> {
                    createCommitPacketAndUploadShipmentData()
                    activityMpsSuccessFailBinding.bottomSubmitButton.submitButton.isClickable = false
                    activityMpsSuccessFailBinding.bottomSubmitButton.submitButton.backgroundTintList = listGray
                    isFailed = 4
                }

                1 -> {
                    navigateToNextActivityWithData(MpsCaptureScanActivity::class.java)
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
            putParcelableArrayListExtra(Constants.QC_WIZARDS, qcItemForCommit)
            putExtra(Constants.IS_FAILED, isFailed)
            putExtra(Constants.IS_IMAGE_MISSING, isImageMissing)
        }.also { intent ->
            startActivity(intent)
            CommonUtils.applyTransitionToOpenActivity(this)
        }
    }

    private fun interactWithViewModel() {
        mpsPickupScanViewModel.showErrorMessageLiveData.observe(this) { (message, isError) ->
            showMessageOnUI(message, isError)
        }
    }

    override fun getViewModel(): MpsPickupScanViewModel {
        return mpsPickupScanViewModel
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_mps_success_fail
    }
}