package `in`.ecomexpress.sathi.ui.drs.rvp_new.activity
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import `in`.ecomexpress.sathi.BR
import `in`.ecomexpress.sathi.BuildConfig
import `in`.ecomexpress.sathi.R
import `in`.ecomexpress.sathi.databinding.ActivityRvpSuccessFailBinding
import `in`.ecomexpress.sathi.repo.local.data.rvp.RvpCommit
import `in`.ecomexpress.sathi.repo.local.db.model.RVPUndeliveredReasonCodeList
import `in`.ecomexpress.sathi.repo.remote.model.ErrorResponse
import `in`.ecomexpress.sathi.repo.remote.model.drs_list.forward.SecureDelivery
import `in`.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse
import `in`.ecomexpress.sathi.repo.remote.model.masterdata.RVPReasonCodeMaster
import `in`.ecomexpress.sathi.ui.base.BaseActivity
import `in`.ecomexpress.sathi.ui.drs.rvp.undelivered.IRVPUndeliveredNavigator
import `in`.ecomexpress.sathi.ui.drs.rvp_new.viewmodel.RVPUndeliveredCancelViewModel
import `in`.ecomexpress.sathi.ui.drs.todolist.ToDoListActivity
import `in`.ecomexpress.sathi.utils.CommonUtils
import `in`.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity
import `in`.ecomexpress.sathi.utils.Constants
import `in`.ecomexpress.sathi.utils.Constants.rvp_call_count
import `in`.ecomexpress.sathi.utils.GlobalConstant
import `in`.ecomexpress.sathi.utils.Logger
import `in`.ecomexpress.sathi.utils.NetworkUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class RvpSuccessFailActivity :
    BaseActivity<ActivityRvpSuccessFailBinding, RVPUndeliveredCancelViewModel>(),
    IRVPUndeliveredNavigator {

    private var meterRange: Int = 0
    private lateinit var binding: ActivityRvpSuccessFailBinding
    private var remarks = ""
    private val cancelPickupRemarksViewModel: RVPUndeliveredCancelViewModel by viewModels()
    private var shipmentType: String = ""
    private var compositeKey: String = ""
    private var awbNumber: String = ""
    private var selectedData: RVPReasonCodeMaster? = null
    private var drsData: DRSReverseQCTypeResponse? = null
    private val rvpCommit = RvpCommit()
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
    private var otpRequiredForDelivery: String = ""
    private var resendSecureOtp: Boolean = false
    private var consigneeAlternateMobile: String = ""
    private var itemDescription: String = ""
    private var rcSelectedDataMili: Long? = null
    private var otpMessageType: String = ""
    private var OFD_OTP: String = ""
    private var otpVerified: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rvp_success_fail)

        getIntentData()

        cancelPickupRemarksViewModel.navigator = this@RvpSuccessFailActivity
        awbNumber = drsData?.awbNo.toString()
        initRVPCommitPacket()
        onSubmit()
        binding.header.headingName.setText(R.string.pickup)
        binding.header.backArrow.setOnClickListener { showSnackbar(getString(R.string.cannot_go_back_Rvp)) }
        binding.header.versionName.text = "v${BuildConfig.VERSION_NAME}"

        binding.tvSubmit.setOnClickListener {
            val intent = Intent(this, ToDoListActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.putExtra(ToDoListActivity.ITEM_MARKED, awbNumber.toString() + "")
            intent.putExtra(
                ToDoListActivity.SHIPMENT_TYPE,
                GlobalConstant.ShipmentTypeConstants.RVP
            )
            intent.putExtra(ToDoListActivity.SHIPMENT_STATUS, shipmentType)
            finish()
            startActivity(intent)
            CommonUtils.applyTransitionToOpenActivity(this)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showSnackbar(getString(R.string.cannot_go_back_Rvp))
            }
        })
    }

    override fun getViewModel(): RVPUndeliveredCancelViewModel {
        return cancelPickupRemarksViewModel
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_rvp_success_fail
    }

    override fun onChooseReasonSpinner(rvpReasonCodeMaster: RVPUndeliveredReasonCodeList?) {

    }

    override fun onCaptureImage() {

    }

    override fun onDatePicker() {

    }

    override fun onBackClick() {
        finish()
        applyTransitionToBackFromActivity(this)
    }

    override fun onSubmitSuccess() {

    }

    override fun OpenToDoList() {

    }

    override fun OnSubmitClick() {

    }

    override fun captureDate() {

    }

    override fun onChooseSlotSpinner(s: String?) {

    }

    override fun onRescheduleClick() {

    }

    override fun onHandleError(callApiResponse: ErrorResponse?) {

    }

    override fun onChooseGroupSpinner(s: String?) {

    }

    override fun onChooseChildSpinner(rvpReasonCodeMaster: RVPReasonCodeMaster?) {

    }

    override fun visible(b: Boolean) {

    }

    override fun showError(error: String?) {
        showSnackbar(error)

    }

    override fun showServerError() {

    }

    override fun setParentGroupSpinnerValues(parentGroupSpinnerValues: MutableList<String>?) {

    }

    override fun setReasonMessageSpinnerValues(reasonMessageSpinnerValues: MutableList<String>?) {

    }

    override fun showErrorMessage(status: Boolean) {

    }

    override fun doLogout(description: String?) {

    }

    override fun clearStack() {

    }

    override fun onRVPItemFetched(drsReverseQCTypeResponse: DRSReverseQCTypeResponse?) {

    }

    override fun setConsigneeDistance(meter: Int) {

    }

    override fun setConsingeeProfiling(enable: Boolean) {

    }

    override fun setBitmap() {

    }

    override fun isCallAttempted(isCall: Int) {

    }

    override fun undeliverShipment(failFlag: Boolean, b: Boolean) {

    }

    override fun getActivityContext(): Activity {
        return this@RvpSuccessFailActivity
    }

    override fun otpLayout(uD_OTP: Boolean, otp_key: String?) {

    }

    override fun onResendClick() {

    }

    override fun onVerifyClick() {

    }

    override fun onSkipClick(view: View?) {

    }

    override fun resendSms(alternateclick: Boolean?) {

    }

    override fun voiceTimer() {

    }

    override fun onOtpResendSuccess(str: String?, description: String?) {

    }

    override fun onGenerateOtpClick() {

    }

    override fun VoiceCallOtp() {

    }


    fun onSubmit() {
        OFD_OTP = Constants.TEMP_OFD_OTP
        val encryptText = CommonUtils.decrypt(OFD_OTP, Constants.DECRYPT)
        Constants.PLAIN_OTP = encryptText

        rvpCommit.isLocation_verified =
            meterRange <= cancelPickupRemarksViewModel.dataManager.getUndeliverConsigneeRANGE()

        rvp_call_count = 0
        rvpCommit.call_attempt_count =
            cancelPickupRemarksViewModel.dataManager.getRVPCallCount("${awbNumber}RVP")
        rvpCommit.map_activity_count =
            cancelPickupRemarksViewModel.dataManager.getRVPMapCount(drsData!!.awbNo)
        rvpCommit.ofd_otp_verified = otpVerified
        rvpCommit.ofd_customer_otp = Constants.PLAIN_OTP
        cancelPickupRemarksViewModel.getRemarkCount(awbNumber.toLong() ?: 0L)

        undelivered()
    }

    @SuppressLint("SimpleDateFormat")
    private fun undelivered() {
        try {
            if (NetworkUtils.isNetworkConnected(this@RvpSuccessFailActivity)) {
                cancelPickupRemarksViewModel.onUndeliveredApiCallRealTime(
                    rvpCommit,
                    compositeKey
                )
            } else {
                cancelPickupRemarksViewModel.onUndeliveredApiCall(
                    rvpCommit,
                    compositeKey
                )
            }
        } catch (e: Exception) {
            showSnackbar(e.message)
            Logger.e("TAG", e.message ?: "Error")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initRVPCommitPacket() {
        try {
            rvpCommit.addressType = ""
            rvpCommit.attemptType = Constants.RVPCOMMIT
            rvpCommit.drsCommitDateTime = Calendar.getInstance().timeInMillis.toString()
            rvpCommit.awb = (drsData!!.awbNo.toString())
            rvpCommit.drsId = drsData!!.drs.toString()
            rvpCommit.consigneeName = drsData!!.consigneeDetails.name
            rvpCommit.feComment = remarks
            rvpCommit.imageData = ArrayList()
            rvpCommit.attemptReasonCode = selectedData!!.reasonCode.toString()


            if (Constants.CURRENT_LATITUDE != null && Constants.CURRENT_LONGITUDE != null) {
                rvpCommit.locationLat = Constants.CURRENT_LATITUDE
                rvpCommit.locationLong = Constants.CURRENT_LONGITUDE
            }

            val rcRescheduleInfo = RvpCommit.RescheduleInfo()
            if (selectedData!!.masterDataAttributeResponse.isrCHD()) {
                rcRescheduleInfo.rescheduleDate = rcSelectedDataMili.toString()
            }
            rvpCommit.rescheduleInfo = rcRescheduleInfo

            if (otpVerified && Constants.uD_OTP_API_CHECK) {
                rvpCommit.ud_otp_verify_status = "VERIFIED"
                rvpCommit.rd_otp_verify_status = "NONE"
            } else if (otpVerified && Constants.rD_OTP_API_CHECK) {
                rvpCommit.rd_otp_verify_status = "VERIFIED"
                rvpCommit.ud_otp_verify_status = "NONE"
            } else {
                rvpCommit.rd_otp_verify_status = "NONE"
                rvpCommit.ud_otp_verify_status = "NONE"
            }

            rvpCommit.attemptType = shipmentType
            rvpCommit.feEmpCode = cancelPickupRemarksViewModel.dataManager.emp_code
            rvpCommit.drsDate = drsData!!.assignedDate.toString()
            rvpCommit.packageBarcode = ""
            rvpCommit.refPackageBarcode = ""
            rvpCommit.qcWizard = ArrayList()
            rvpCommit.received_by = "ECOM"

            if (selectedData!!.masterDataAttributeResponse.isrCHD()) {
                val date = Date(rcSelectedDataMili!!)
                val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
                val formattedDate: String = sdf.format(date)
                binding.header3.awbNumber.text = "Shipment to be Re-Attempted \non $formattedDate"
                binding.header3.failImage.setImageResource((R.drawable.tick))
            } else {
                binding.header3.awbNumber.text =
                    "AWB : $awbNumber \n${getString(R.string.pickup_cancel)}"
                binding.header3.failImage.setImageResource((R.drawable.cross))
            }


        } catch (e: java.lang.Exception) {
            Logger.e("TAG", e.message)
            showSnackbar(e.message)
        }
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
                otpRequiredForDelivery =
                    it.getStringExtra(Constants.otp_required_for_delivery) ?: ""
                resendSecureOtp = it.getBooleanExtra(Constants.RESEND_SECURE_OTP, false)
                consigneeAlternateMobile =
                    it.getStringExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE) ?: ""
                otpMessageType = it.getStringExtra("otpMessageType") ?: ""
                selectedData = intent.getParcelableExtra("masterData") as? RVPReasonCodeMaster
                drsData = intent.getParcelableExtra("drsData") as? DRSReverseQCTypeResponse
                rcSelectedDataMili = intent.getLongExtra("selectedDate", 0)
                remarks = intent.extras!!.getString("remarks", "")
                otpVerified = intent.getBooleanExtra("otpVerified", false)
                meterRange = intent.getIntExtra("meterRange", 0)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
