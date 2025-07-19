package `in`.ecomexpress.sathi.ui.drs.rvp.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import `in`.ecomexpress.sathi.BuildConfig
import `in`.ecomexpress.sathi.R
import `in`.ecomexpress.sathi.databinding.ActivityCancelPickupRemarksBinding
import `in`.ecomexpress.sathi.databinding.DialogDirectCallNewBinding
import `in`.ecomexpress.sathi.repo.local.data.rvp.RvpCommit
import `in`.ecomexpress.sathi.repo.local.db.model.Remark
import `in`.ecomexpress.sathi.repo.remote.model.ErrorResponse
import `in`.ecomexpress.sathi.repo.remote.model.drs_list.forward.FlagMap
import `in`.ecomexpress.sathi.repo.remote.model.drs_list.forward.SecureDelivery
import `in`.ecomexpress.sathi.repo.remote.model.drs_list.forward.callbridge_details
import `in`.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse
import `in`.ecomexpress.sathi.repo.remote.model.masterdata.RVPReasonCodeMaster
import `in`.ecomexpress.sathi.repo.remote.model.mps.DRSRvpQcMpsResponse
import `in`.ecomexpress.sathi.ui.base.BaseActivity
import `in`.ecomexpress.sathi.ui.drs.rvp.adapter.UndeliveredCalenderRVPAdapter
import `in`.ecomexpress.sathi.ui.drs.rvp.navigator.IRVPUndeliveredNavigator
import `in`.ecomexpress.sathi.ui.drs.rvp.viewmodel.RVPUndeliveredCancelViewModel
import `in`.ecomexpress.sathi.utils.CommonUtils
import `in`.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity
import `in`.ecomexpress.sathi.utils.Constants
import `in`.ecomexpress.sathi.utils.Constants.ConsigneeDirectAlternateMobileNo
import `in`.ecomexpress.sathi.utils.Constants.ConsigneeDirectMobileNo
import `in`.ecomexpress.sathi.utils.Constants.rvp_call_count
import `in`.ecomexpress.sathi.utils.GlobalConstant
import `in`.ecomexpress.sathi.utils.Logger
import `in`.ecomexpress.sathi.utils.TimeUtils
import `in`.ecomexpress.sathi.utils.cameraView.CameraActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class CancelPickupRemarksActivity : BaseActivity<ActivityCancelPickupRemarksBinding, RVPUndeliveredCancelViewModel>(), IRVPUndeliveredNavigator, OnClickListener {

    private var rcSelectedData: String = ""
    private var rcSelectedDataMilliSecond: Long? = null
    private var isCallMandatory: Boolean = false
    private val tag = CancelPickupRemarksActivity::class.java.simpleName
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
    private var resendSecureOtp: Boolean = false
    private var consigneeAlternateMobile: String = ""
    private var itemDescription: String = ""
    private lateinit var activityCancelPickupRemarksBinding: ActivityCancelPickupRemarksBinding
    private lateinit var undeliveredCalenderRVPAdapter: UndeliveredCalenderRVPAdapter
    private val cancelPickupRemarksViewModel: RVPUndeliveredCancelViewModel by viewModels()
    private var shipmentType: String = ""
    private var compositeKey: String = ""
    private var awbNumber: String = ""
    private var selectedData: RVPReasonCodeMaster? = null
    private var callBridgeDetails: ArrayList<callbridge_details>? = null
    private var flagMap: FlagMap? = null
    private var shipperName: String? = null
    private var consigneeName: String? = null
    private val rvpCommit = RvpCommit()
    private var isWhatsappMessaged = false
    private var meterRange = 0
    private var checkCallAttempted = 0
    private val cameraRequestCode = 100
    private var capturedImageBitmap: Bitmap? = null
    private var bitmapServer: Bitmap? = null
    private var isImageCaptured: Boolean = false
    var imageCaptureCount: Int = 0
    private var grpName: String? = null
    private var drsAssignDate: String? = null
    private var isFromMps: Boolean = false

    data class DateItem(val date: String, val dayName: String, val fullDate: String)

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityCancelPickupRemarksBinding = DataBindingUtil.setContentView(this, R.layout.activity_cancel_pickup_remarks)

        getIntentData()
        cancelPickupRemarksViewModel.navigator = this@CancelPickupRemarksActivity
        activityCancelPickupRemarksBinding.tvSubmit.setOnClickListener(this)
        activityCancelPickupRemarksBinding.startImg.setOnClickListener(this)
        activityCancelPickupRemarksBinding.imageDelete.setOnClickListener(this)
        grpName = selectedData!!.subGroup
        activityCancelPickupRemarksBinding.parentReasonCode.text = grpName
        activityCancelPickupRemarksBinding.shipmentDamage.text = selectedData?.reasonMessage

        val dateList = generateDates()
        activityCancelPickupRemarksBinding.calender.calenderRecycler.layoutManager = GridLayoutManager(this, 3)
        undeliveredCalenderRVPAdapter = UndeliveredCalenderRVPAdapter(dateList) { selectedDate ->
            // Handle the selected date
            handleDateSelection(selectedDate)
        }
        activityCancelPickupRemarksBinding.calender.calenderRecycler.adapter = undeliveredCalenderRVPAdapter

        activityCancelPickupRemarksBinding.header.headingName.setText(R.string.pickup)
        activityCancelPickupRemarksBinding.header.backArrow.setOnClickListener { onBackClick() }
        activityCancelPickupRemarksBinding.header.versionName.text = "v${BuildConfig.VERSION_NAME}"
        setupAWBHeader()
    }

    @SuppressLint("SimpleDateFormat")
    private fun handleDateSelection(selectedDate: DateItem) {
        rcSelectedData = selectedDate.date
        val sdf = SimpleDateFormat("dd MMM yyyy")
        val parsedDate = sdf.parse(selectedDate.fullDate)
        rcSelectedDataMilliSecond = parsedDate?.time
    }

    private fun checkUndeliveredType() {
        val template: String = CommonUtils.getWhatsAppRemarkTemplate(cancelPickupRemarksViewModel.dataManager.name, cancelPickupRemarksViewModel.dataManager.mobile, awbNumber, shipperName)
        if (cancelPickupRemarksViewModel.dataManager.smsThroughWhatsapp && selectedData!!.masterDataAttributeResponse.isWHATSAPP_MAND && cancelPickupRemarksViewModel.dataManager.getTryReachingCount(awbNumber + Constants.TRY_RECHING_COUNT) == 0) {
            showWhatsAppDialog(template)
        } else {
            executeOnChooseChildSpinner(selectedData!!)
        }
    }

    private fun showWhatsAppDialog(template: String) {
        CommonUtils.showDialogBoxForWhatsApp(activityContext).setPositiveButton("OK") { _: DialogInterface?, _: Int ->
            try {
                isWhatsappMessaged = true
                CommonUtils.sendSMSViaWhatsApp(this, this, consigneeMobile, template)
                cancelPickupRemarksViewModel.dataManager.setTryReachingCount(awbNumber + Constants.TRY_RECHING_COUNT, cancelPickupRemarksViewModel.dataManager.getTryReachingCount(awbNumber + Constants.TRY_RECHING_COUNT) + 1)
                val remarks = GlobalConstant.RemarksTypeConstants.CONSIGNEE_NOT_PICKING_CALL
                val remark = Remark()
                remark.remark = remarks
                remark.empCode = cancelPickupRemarksViewModel.dataManager.emp_code
                remark.awbNo = awbNumber.toLong()
                remark.count = cancelPickupRemarksViewModel.dataManager.getTryReachingCount(awbNumber + Constants.TRY_RECHING_COUNT)
                remark.date = TimeUtils.getDateYearMonthMillies()
                cancelPickupRemarksViewModel.addRemarks(remark)
                executeOnChooseChildSpinner(selectedData!!)
            } catch (e: Exception) {
                Logger.e(tag, e.message)
            }
        }.setOnCancelListener {
            executeOnChooseChildSpinner(selectedData!!)
        }.show()
    }

    private fun executeOnChooseChildSpinner(rvpReasonCodeMaster: RVPReasonCodeMaster) {
        setVisibility(rvpReasonCodeMaster)
        cancelPickupRemarksViewModel.udOtpCommitStatus = "NONE"
        cancelPickupRemarksViewModel.udOtpCommitStatusField.set("NONE")
        cancelPickupRemarksViewModel.rdOtpCommitStatus = "NONE"
        cancelPickupRemarksViewModel.rdOtpCommitStatusField.set("NONE")
    }

    @SuppressLint("SetTextI18n")
    private fun setVisibility(rvpReasonCodeMaster: RVPReasonCodeMaster) {
        try {
            if (rvpReasonCodeMaster.masterDataAttributeResponse.isUD_OTP) {
                Constants.uD_OTP_API_CHECK = true
                Constants.rD_OTP_API_CHECK = false
            } else if (rvpReasonCodeMaster.masterDataAttributeResponse.isRCHD_OTP) {
                Constants.rD_OTP_API_CHECK = true
                Constants.uD_OTP_API_CHECK = false
            } else {
                Constants.rD_OTP_API_CHECK = false
                Constants.uD_OTP_API_CHECK = false
            }
            isCallMandatory = if (rvpReasonCodeMaster.masterDataAttributeResponse.iscALLM()) {
                Constants.CONSIGNEE_PROFILE || meterRange >= cancelPickupRemarksViewModel.dataManager.undeliverConsigneeRANGE
            } else {
                false
            }
            if (rvpReasonCodeMaster.masterDataAttributeResponse.isiMG()) {
                activityCancelPickupRemarksBinding.textViewLabel.text = getString(R.string.capture_image) + "*"
            } else {
                activityCancelPickupRemarksBinding.textViewLabel.text = getString(R.string.capture_image)
            }

            if (!rvpReasonCodeMaster.masterDataAttributeResponse.isrCHD()) {
                activityCancelPickupRemarksBinding.calender.root.visibility = View.GONE
            } else {
                activityCancelPickupRemarksBinding.calender.root.visibility = View.VISIBLE
                activityCancelPickupRemarksBinding.pickupRemark.root.visibility = View.VISIBLE
            }
            if (rvpReasonCodeMaster.masterDataAttributeResponse.iscALLM()) {
                cancelPickupRemarksViewModel.getIsCallAttempted(awbNumber, isFromMps)
            }

            if (!rvpReasonCodeMaster.masterDataAttributeResponse.isrCHD() && !rvpReasonCodeMaster.masterDataAttributeResponse.iscALLM()) {
                activityCancelPickupRemarksBinding.calender.root.visibility = View.GONE
            }
        } catch (e: java.lang.Exception) {
            showSnackbar("Exception : " + e.message)
        }
        rvpCommit.attemptReasonCode = rvpReasonCodeMaster.reasonCode.toString()
    }

    @SuppressLint("SetTextI18n")
    private fun setupAWBHeader() {
        val iconResource = when {
            isFromMps -> R.drawable.mps_tag_icon
            shipmentType.equals(Constants.RVP, ignoreCase = true) -> R.drawable.rvp_tag_icon
            else -> R.drawable.rqc_tag_icon
        }

        activityCancelPickupRemarksBinding.rqcAwbHeader.awbNumber.apply {
            setCompoundDrawablesWithIntrinsicBounds(0, 0, iconResource, 0)
            text = getString(R.string.awb) + " $awbNumber"
        }
    }

    override fun getViewModel(): RVPUndeliveredCancelViewModel {
        return cancelPickupRemarksViewModel
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_cancel_pickup_remarks
    }

    override fun onBackClick() {
        finish()
        applyTransitionToBackFromActivity(this)
    }

    override fun onSubmitSuccess() {}

    override fun OnSubmitClick() {}

    override fun onHandleError(callApiResponse: ErrorResponse?) {}

    override fun onChooseGroupSpinner(s: String?) {}

    override fun onChooseChildSpinner(rvpReasonCodeMaster: RVPReasonCodeMaster?) {}

    override fun visible(b: Boolean) {}

    override fun showError(error: String?) {
        showSnackbar(error)
    }

    override fun showServerError(error: String) {
        showSnackbar(error)
    }

    override fun setReasonMessageSpinnerValues(reasonMessageSpinnerValues: MutableList<String>?) {}

    override fun showErrorMessage(status: Boolean) {}

    override fun doLogout(description: String?) {}

    override fun clearStack() {}

    override fun onRVPItemFetched(drsReverseQCTypeResponse: DRSReverseQCTypeResponse?, drsMpsTypeResponse: DRSRvpQcMpsResponse?) {
        try {
            if(isFromMps){
                callBridgeDetails = drsMpsTypeResponse?.getCallbridge_details()
                flagMap = drsMpsTypeResponse!!.flags.getFlagMap()
                shipperName = drsMpsTypeResponse.shipmentDetails.shipper
                consigneeName = drsMpsTypeResponse.consigneeDetails.name
                drsAssignDate = drsMpsTypeResponse.assignedDate.toString()
                checkUndeliveredType()
            } else{
                callBridgeDetails = drsReverseQCTypeResponse?.getCallbridge_details()
                flagMap = drsReverseQCTypeResponse!!.flags.getFlagMap()
                shipperName = drsReverseQCTypeResponse.shipmentDetails.shipper
                consigneeName = drsReverseQCTypeResponse.consigneeDetails.name
                drsAssignDate = drsReverseQCTypeResponse.assignedDate.toString()
                checkUndeliveredType()
            }
            cancelPickupRemarksViewModel.checkMeterRange(awbNumber.toLong())
        } catch (e: java.lang.Exception) {
            showSnackbar(e.message)
        }
    }

    override fun setConsigneeDistance(meter: Int) {
        meterRange = meter
    }

    override fun setConsingeeProfiling(enable: Boolean) {}

    override fun setBitmap() {
        activityCancelPickupRemarksBinding.startImg.setImageBitmap(bitmapServer)
    }

    override fun isCallAttempted(isCall: Int) {
        checkCallAttempted = isCall
        if (isCall == 0 && cancelPickupRemarksViewModel.dataManager.getCallClicked(awbNumber + "RVPCall")) {
            callActivate()
        } else {
            if (!selectedData!!.masterDataAttributeResponse.isrCHD()) {
                activityCancelPickupRemarksBinding.calender.root.visibility = View.GONE
            } else {
                activityCancelPickupRemarksBinding.calender.root.visibility = View.VISIBLE
            }
        }
    }

    private fun makeCallOnClick() {
        try {
            if (flagMap?.is_callbridge_enabled.equals("true", true) && callBridgeDetails != null) {
                val callingFormat = (callBridgeDetails!![0].callbridge_number + "," + callBridgeDetails!![0].pin) + "#"
                Constants.call_pin = (callBridgeDetails!![0].pin).toString()
                Constants.calling_format = callingFormat
                Constants.shipment_type = Constants.RVP
                cancelPickupRemarksViewModel.saveCallStatus(awbNumber.toLong(), drsId.toInt())
                Constants.IS_CALL_BRIDGE_FLAG_ON_STATUS = cancelPickupRemarksViewModel.dataManager.isCallBridgeCheckStatus.equals("true", true)
                if (flagMap?.is_callbridge_enabled.equals("true", true) && callBridgeDetails != null) {
                    if (callBridgeDetails!!.size > 1) {
                        showCallBridgeDialog()
                    } else {
                        cancelPickupRemarksViewModel.dataManager.setCallClicked(awbNumber + "RVPCall", false)
                        cancelPickupRemarksViewModel.consigneeContactNumber.set(callingFormat)
                        CommonUtils.startCallIntent(callingFormat, this, this@CancelPickupRemarksActivity)
                    }
                } else {
                    if (!TextUtils.isEmpty(ConsigneeDirectAlternateMobileNo) && ConsigneeDirectAlternateMobileNo != null && ConsigneeDirectAlternateMobileNo != "0") {
                        showDirectCallDialog()
                    } else {
                        cancelPickupRemarksViewModel.dataManager.setCallClicked(awbNumber + "RVPCall", false)
                        cancelPickupRemarksViewModel.consigneeContactNumber.set(ConsigneeDirectMobileNo)
                        rvp_call_count += 1
                        cancelPickupRemarksViewModel.dataManager.setRVPCallCount(awbNumber + "RVP", rvp_call_count)
                        val intent1 = Intent(Intent.ACTION_CALL, Uri.parse("tel:$ConsigneeDirectMobileNo"))
                        intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent1)
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            showSnackbar(e.message)
        }
    }

    private fun showCallBridgeDialog() {
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.dialog_callbridge_new)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom((dialog.window)!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        val call: MaterialTextView? = dialog.findViewById(R.id.primaryText)
        call!!.text = callBridgeDetails!![0].callbridge_number
        val altCall: MaterialTextView? = dialog.findViewById(R.id.alternateText)
        altCall!!.text = callBridgeDetails!![1].callbridge_number

        val btnCall: ConstraintLayout? = dialog.findViewById(R.id.bt_call)
        val btnAltCall: ConstraintLayout? = dialog.findViewById(R.id.bt_sms)

        btnCall!!.setOnClickListener {
            cancelPickupRemarksViewModel.dataManager.setCallClicked(awbNumber + "RVPCall", false)
            cancelPickupRemarksViewModel.consigneeContactNumber.set((callBridgeDetails!![0].callbridge_number + "," + callBridgeDetails!![0].pin) + "#")
            rvp_call_count += 1
            cancelPickupRemarksViewModel.dataManager.setRVPCallCount(awbNumber + "RVP", rvp_call_count)
            val intent1 = Intent(Intent.ACTION_CALL, Uri.parse((("tel:" + callBridgeDetails!![0].callbridge_number) + "," + callBridgeDetails!![0].pin) + "#"))
            intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent1)
            dialog.dismiss()
        }
        btnAltCall!!.setOnClickListener {
            cancelPickupRemarksViewModel.dataManager.setCallClicked(awbNumber + "RVPCall", false)
            cancelPickupRemarksViewModel.consigneeContactNumber.set((callBridgeDetails!![1].callbridge_number + "," + callBridgeDetails!![1].pin) + "#")
            rvp_call_count += 1
            cancelPickupRemarksViewModel.dataManager.setRVPCallCount(awbNumber + "RVP", rvp_call_count)
            val intent1 = Intent(Intent.ACTION_CALL, Uri.parse((("tel:" + callBridgeDetails!![1].callbridge_number) + "," + callBridgeDetails!![1].pin) + "#"))
            intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent1)
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.attributes = lp
    }

    private fun formatMobileNumber(mobileNumber: String): String {
        val sanitizedNumber = mobileNumber.takeLast(10)
        return if (sanitizedNumber.length == 10) {
            "XXXXXX${sanitizedNumber.takeLast(4)}"
        } else {
            sanitizedNumber
        }
    }

    fun showDirectCallDialog() {
        val dialogDirectCallNewBinding: DialogDirectCallNewBinding = DialogDirectCallNewBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogDirectCallNewBinding.root)
        val lp = WindowManager.LayoutParams().apply {
            copyFrom(dialog.window?.attributes)
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }
        dialogDirectCallNewBinding.primaryText.text = formatMobileNumber(ConsigneeDirectMobileNo)
        dialogDirectCallNewBinding.alternateText.text = formatMobileNumber(ConsigneeDirectAlternateMobileNo)

        val callButton: ConstraintLayout? = dialog.findViewById(R.id.bt_call)
        val altCallButton: ConstraintLayout? = dialog.findViewById(R.id.bt_sms)

        callButton!!.setOnClickListener {
            cancelPickupRemarksViewModel.dataManager.setCallClicked(awbNumber + "RVPCall", false)
            cancelPickupRemarksViewModel.consigneeContactNumber.set(ConsigneeDirectMobileNo)
            rvp_call_count += 1
            cancelPickupRemarksViewModel.dataManager.setRVPCallCount(awbNumber + "RVP", rvp_call_count)
            val intent1 = Intent(Intent.ACTION_CALL, Uri.parse("tel:$ConsigneeDirectMobileNo"))
            intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent1)
            dialog.dismiss()
        }

        altCallButton!!.setOnClickListener {
            cancelPickupRemarksViewModel.dataManager.setCallClicked(awbNumber + "RVPCall", false)
            cancelPickupRemarksViewModel.consigneeContactNumber.set(ConsigneeDirectAlternateMobileNo)
            rvp_call_count += 1
            cancelPickupRemarksViewModel.dataManager.setRVPCallCount(awbNumber + "RVP", rvp_call_count)
            val intent1 = Intent(Intent.ACTION_CALL, Uri.parse("tel:${ConsigneeDirectAlternateMobileNo}"))
            intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent1)
            dialog.dismiss()
        }
        dialog.show()
        dialog.window?.attributes = lp
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this@CancelPickupRemarksActivity, R.style.Theme_MaterialComponents_Light_Dialog_Alert)
        val alertText1 = "Attention : "
        builder.setMessage(alertText1 + getString(R.string.undelivered_shipment_msg)).setCancelable(false).setPositiveButton("Call") { _: DialogInterface?, _: Int ->
            try {
                callBridgeClick()
            } catch (e: java.lang.Exception) {
                Logger.e(tag, e.message)
            }
        }
        builder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    override fun undeliverShipment(failFlag: Boolean, b: Boolean) {
        if (b) {
            submitUndelivered()
        } else {
            showDialog()
        }
    }

    override fun getActivityContext(): Activity {
        return this
    }

    override fun onResendClick() {}

    override fun onVerifyClick() {}

    override fun resendSms(alternateclick: Boolean?) {}

    override fun voiceTimer() {}

    override fun onOtpResendSuccess(str: String?, description: String?) {}

    override fun onGenerateOtpClick() {}

    override fun VoiceCallOtp() {}

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.tv_submit -> {
                markDeliverOrFail(false)
            }

            R.id.start_img -> {
                onCaptureImageNew()
            }

            R.id.imageDelete -> {
                activityCancelPickupRemarksBinding.imageDelete.visibility = View.GONE
                isImageCaptured = false
                activityCancelPickupRemarksBinding.startImg.setImageResource(R.drawable.cam_new)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun markDeliverOrFail(failFlag: Boolean) {
        val currentTime = SimpleDateFormat("HH").format(Date()).toInt()
        if (Constants.CONSIGNEE_PROFILE && meterRange < cancelPickupRemarksViewModel.dataManager.undeliverConsigneeRANGE) {
            submitUndelivered()
        } else if (cancelPickupRemarksViewModel.checkIsFeIsDcRange(cancelPickupRemarksViewModel.dataManager.currentLatitude, cancelPickupRemarksViewModel.dataManager.currentLongitude, cancelPickupRemarksViewModel.dataManager.getDCLatitude(), cancelPickupRemarksViewModel.dataManager.getDCLongitude())) {
            if (!cancelPickupRemarksViewModel.dataManager.directUndeliver) {
                submitUndelivered()
            } else {
                if (callAllowed) {
                    flagMap?.let { cancelPickupRemarksViewModel.callApi(it.is_callbridge_enabled, failFlag, awbNumber, drsId) }
                } else {
                    submitUndelivered()
                }
            }
        } else if (currentTime >= 21 || currentTime <= 6) {
            if (!cancelPickupRemarksViewModel.dataManager.directUndeliver) {
                submitUndelivered()
            } else {
                if (callAllowed) {
                    flagMap?.let { cancelPickupRemarksViewModel.callApi(it.is_callbridge_enabled, failFlag, awbNumber, drsId) }
                } else {
                    submitUndelivered()
                }
            }
        } else if ((TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis()) - TimeUnit.MILLISECONDS.toMinutes(cancelPickupRemarksViewModel.dataManager.drsTimeStamp)) <= 7) {
            if (!cancelPickupRemarksViewModel.dataManager.directUndeliver) {
                submitUndelivered()
            } else {
                if (callAllowed) {
                    flagMap?.let { cancelPickupRemarksViewModel.callApi(it.is_callbridge_enabled, failFlag, awbNumber, drsId) }
                } else {
                    submitUndelivered()
                }
            }
        } else {
            if (isCallMandatory) {
                if (!cancelPickupRemarksViewModel.dataManager.directUndeliver) {
                    submitUndelivered()
                } else {
                    if (callAllowed) {
                        flagMap?.let { cancelPickupRemarksViewModel.callApi(it.is_callbridge_enabled, failFlag, awbNumber, drsId) }
                    } else {
                        submitUndelivered()
                    }
                }
            } else {
                submitUndelivered()
            }
        }
    }

    private fun submitUndelivered() {
        if (cancelPickupRemarksViewModel.dataManager.smsThroughWhatsapp && selectedData!!.masterDataAttributeResponse.isWHATSAPP_MAND && cancelPickupRemarksViewModel.dataManager.getTryReachingCount(awbNumber + Constants.TRY_RECHING_COUNT) == 0) {
            val template = CommonUtils.getWhatsAppRemarkTemplate(cancelPickupRemarksViewModel.dataManager.name, cancelPickupRemarksViewModel.dataManager.mobile, awbNumber, shipperName)
            showWhatsAppDialog(template)
        } else if (selectedData!!.masterDataAttributeResponse.cALLM && cancelPickupRemarksViewModel.dataManager.getCallClicked(awbNumber + "RVPCall")) {
            callActivate()
        } else if (selectedData!!.masterDataAttributeResponse.isiMG() && !isImageCaptured) {
            Toast.makeText(applicationContext, getString(R.string.capture_image), Toast.LENGTH_SHORT).show()
        } else if (rcSelectedData.isEmpty() && selectedData!!.masterDataAttributeResponse.isrCHD()) {
            showSnackbar(getString(R.string.reschedule_statment))
        } else if (Constants.uD_OTP_API_CHECK) {
            navigateToNextActivityWithData(UndeliveredOtpActivity::class.java, awbNumber, "UD_OTP")
        } else if (Constants.rD_OTP_API_CHECK) {
            navigateToNextActivityWithData(UndeliveredOtpActivity::class.java, awbNumber, "RCHD_OTP")
        } else {
            navigateToNextActivityWithData(RvpSuccessFailActivity::class.java, awbNumber, "")
        }
    }

    private fun generateDates(): List<DateItem> {
        val dateList = mutableListOf<DateItem>()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        val dateFormatWithYear = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())

        for (i in 0..5) {
            val date = dateFormat.format(calendar.time)
            val date1 = dateFormatWithYear.format(calendar.time)
            val dayName = when (i) {
                0 -> "Today"
                1->"Tomorrow"
                else -> dayFormat.format(calendar.time)
            }
            dateList.add(DateItem(date, dayName, date1))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return dateList
    }

    @Suppress("DEPRECATION")
    private fun getIntentData() {
        try {
            intent?.let {
                drsId = it.getStringExtra(Constants.DRS_ID) ?: ""
                secureDelivery = it.getParcelableExtra(Constants.SECURE_DELIVERY)
                isFromMps = it.getBooleanExtra(Constants.IS_FROM_MPS, false)
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
                resendSecureOtp = it.getBooleanExtra(Constants.RESEND_SECURE_OTP, false)
                consigneeAlternateMobile = it.getStringExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE) ?: ""
                selectedData = intent.getParcelableExtra("SELECTED_REASON") as? RVPReasonCodeMaster

                // Fetch shipment details:-
                cancelPickupRemarksViewModel.loadRvpQcShipmentDetails(compositeKey, isFromMps)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun navigateToNextActivityWithData(destinationActivity: Class<out Activity>, awbNumber: String, otpMessageType: String) {
        val intent = Intent(this, destinationActivity).apply {
            putExtra(Constants.DRS_ID, drsId)
            putExtra(Constants.SECURE_DELIVERY, secureDelivery)
            putExtra(Constants.IS_FROM_MPS, isFromMps)
            putExtra(Constants.DRS_ASSIGN_DATE, drsAssignDate)
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
            putExtra(Constants.RESEND_SECURE_OTP, resendSecureOtp)
            putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, consigneeAlternateMobile)
            putExtra(Constants.CONSIGNEE_NAME, consigneeName)
            putExtra("otpMessageType", otpMessageType)
            putExtra("masterData", selectedData)
            putExtra("selectedDate", rcSelectedDataMilliSecond)
            putExtra("meterRange", meterRange)
            putExtra("remarks", activityCancelPickupRemarksBinding.pickupRemark.etRemarks.getText().toString().trim())
        }
        startActivity(intent)
        CommonUtils.applyTransitionToOpenActivity(this)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == cameraRequestCode && resultCode == RESULT_OK) {
            try {
                if (data != null) {
                    if (!isNetworkConnected) {
                        showSnackbar(getString(R.string.no_network_error))
                        return
                    }
                    val imagePathWithWaterMark = data.getStringExtra("imagePathWithWaterMark")
                    val bitmap = BitmapFactory.decodeFile(imagePathWithWaterMark)
                    val imageUri = data.getStringExtra("imageUri")
                    val imageCode = data.getStringExtra("imageCode")
                    sendCapturedImageToServerAndSaveInDB(imageCode, imageUri, bitmap)
                    capturedImageBitmap = bitmap
                } else {
                    showSnackbar(getString(R.string.captured_image_data_is_empty))
                }
            } catch (e: java.lang.Exception) {
                showSnackbar(getString(R.string.captured_image_data_is_empty))
            }
        }
    }

    private fun sendCapturedImageToServerAndSaveInDB(imageCode: String?, imageUri: String?, bitmap: Bitmap) {
        if (CommonUtils.checkImageIsBlurryOrNot(this@CancelPickupRemarksActivity, "RVP", bitmap, imageCaptureCount, cancelPickupRemarksViewModel.dataManager) || CommonUtils.checkImageIsBlurryOrNot(this@CancelPickupRemarksActivity, "RQC", bitmap, imageCaptureCount, cancelPickupRemarksViewModel.dataManager)) {
            imageCaptureCount++
        } else {
            bitmapServer = bitmap
            activityCancelPickupRemarksBinding.startImg.setImageBitmap(bitmapServer)
            isImageCaptured = true
            activityCancelPickupRemarksBinding.imageDelete.visibility = View.GONE
            if (!isNetworkConnected) {
                showSnackbar(getString(R.string.no_network_error))
                return
            }
            cancelPickupRemarksViewModel.uploadImageServer(activityContext, imageUri, (awbNumber + "_" + drsId) + "_UndeliveredImage.png", imageCode!!, awbNumber.toLong(), drsId.toInt())
        }
    }

    private fun onCaptureImageNew() {
        if (!CommonUtils.isAllPermissionAllow(this)) {
            openSettingActivity()
            return
        }
        if (!isNetworkConnected) {
            showSnackbar(getString(R.string.no_network_error))
            return
        }
        val builder = AlertDialog.Builder(this, R.style.Theme_MaterialComponents_Light_Dialog_Alert)
        val alertText = "Attention : "
        builder.setMessage(alertText + getString(R.string.alert))
            .setCancelable(false)
            .setPositiveButton("OK") { _: DialogInterface?, _: Int ->
                startCameraActivity()
            }
        val alert = builder.create()
        alert.show()
    }

    @Suppress("DEPRECATION")
    private fun startCameraActivity() {
        try {
            val intent = Intent(this, CameraActivity::class.java)
            intent.putExtra("EmpCode", cancelPickupRemarksViewModel.dataManager.emp_code)
            intent.putExtra("Latitude", cancelPickupRemarksViewModel.dataManager.currentLatitude)
            intent.putExtra("Longitude", cancelPickupRemarksViewModel.dataManager.currentLongitude)
            intent.putExtra("ImageCode", "UndeliveredImage")
            intent.putExtra("imageName", "UndeliveredImage")
            intent.putExtra("awbNumber", "" + awbNumber)
            intent.putExtra("drs_id", drsId)
            startActivityForResult(intent, cameraRequestCode)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun callActivate() {
        activityCancelPickupRemarksBinding.btnCallConsignee.visibility = View.VISIBLE
        activityCancelPickupRemarksBinding.conCall.visibility = View.VISIBLE
        activityCancelPickupRemarksBinding.tvSubmit.visibility = View.GONE
        activityCancelPickupRemarksBinding.btnCallConsignee.setOnClickListener {
            callBridgeClick()
        }
    }

    private fun callBridgeClick() {
        if (flagMap?.is_callbridge_enabled.equals("true", true) && callBridgeDetails != null) {
            makeCallOnClick()
        } else {
            if (!TextUtils.isEmpty(ConsigneeDirectAlternateMobileNo) && ConsigneeDirectAlternateMobileNo != null && !ConsigneeDirectAlternateMobileNo.equals("0")) {
                showDirectCallDialog()
            } else {
                cancelPickupRemarksViewModel.dataManager.setCallClicked(awbNumber + "RVPCall", false)
                cancelPickupRemarksViewModel.consigneeContactNumber.set(consigneeMobile)
                rvp_call_count += 1
                cancelPickupRemarksViewModel.dataManager.setRVPCallCount(awbNumber + "RVP", rvp_call_count)
                CommonUtils.startCallIntent(consigneeMobile, activityContext, this@CancelPickupRemarksActivity)
            }
        }
        submitActivate()
    }

    private fun submitActivate() {
        activityCancelPickupRemarksBinding.btnCallConsignee.visibility = View.GONE
        activityCancelPickupRemarksBinding.conCall.visibility = View.GONE
        activityCancelPickupRemarksBinding.tvSubmit.visibility = View.VISIBLE
        activityCancelPickupRemarksBinding.tvSubmit.setOnClickListener {
            markDeliverOrFail(false)
        }
    }
}