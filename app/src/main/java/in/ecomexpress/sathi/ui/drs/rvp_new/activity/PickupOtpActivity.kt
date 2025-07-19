package `in`.ecomexpress.sathi.ui.drs.rvp_new.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import `in`.ecomexpress.sathi.BR
import `in`.ecomexpress.sathi.BuildConfig
import `in`.ecomexpress.sathi.R
import `in`.ecomexpress.sathi.databinding.ActivityPickupOtpBinding
import `in`.ecomexpress.sathi.databinding.RvpQcOtpBottomSheetBinding
import `in`.ecomexpress.sathi.ui.auth.login.LoginActivity
import `in`.ecomexpress.sathi.ui.base.BaseActivity
import `in`.ecomexpress.sathi.ui.drs.rvp_new.navigator.RvpQcNavigator
import `in`.ecomexpress.sathi.ui.drs.rvp_new.viewmodel.RvpCommonViewModel
import `in`.ecomexpress.sathi.utils.CommonUtils
import `in`.ecomexpress.sathi.utils.Constants
import `in`.ecomexpress.sathi.utils.Logger
import `in`.ecomexpress.sathi.utils.PreferenceUtils

@AndroidEntryPoint
class PickupOtpActivity : BaseActivity<ActivityPickupOtpBinding, RvpCommonViewModel>(), RvpQcNavigator {

    private val activityPickupOtpBinding: ActivityPickupOtpBinding by lazy {
        ActivityPickupOtpBinding.inflate(layoutInflater)
    }

    private lateinit var rvpQcOtpBottomSheetBinding: RvpQcOtpBottomSheetBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var lastClickTime: Long = 0
    private var countdownTimer: CountDownTimer? = null
    private var timerIsOn = false
    private var listBlue: ColorStateList? = null
    private var listGray: ColorStateList? = null
    private val rvpCommonViewModel : RvpCommonViewModel by viewModels()
    private var drsId: String = ""
    private var shipmentType: String = ""
    private var ofdOtp: String = ""
    private var callAllowed: Boolean = false
    private var consigneeMobile: String = ""
    private var awbNumber: String = ""
    private var otpRequiredForDelivery: String = ""
    private var resendSecureOtp: Boolean = false
    private var consigneeAlternateMobile: String = ""
    private var consigneeName: String = ""
    private var compositeKey: String = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityPickupOtpBinding.root)
        getIntentData()
        setupBackNavigation()
        interactWithViewModel()
        setupUI()
    }

    private fun setupUI() {
        setupHeader()
        setupAWBHeader()
        setupSubmitButton()
        setupOtpWork()
        setupOtpInput()
        setupCheckBox()
    }

    private fun setupOtpInput() {
        val otpBoxes = listOf(activityPickupOtpBinding.otpBoxOne, activityPickupOtpBinding.otpBoxTwo, activityPickupOtpBinding.otpBoxThree, activityPickupOtpBinding.otpBoxFour)
        otpBoxes.forEachIndexed { index, currentBox ->
            val nextBox = otpBoxes.getOrNull(index + 1)
            val previousBox = otpBoxes.getOrNull(index - 1)
            currentBox.addTextChangedListener(OtpTextWatcher(nextBox, previousBox))
            currentBox.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (currentBox.text.isNullOrEmpty()) {
                        previousBox?.requestFocus()
                    }
                }
                false
            }
        }
    }

    private fun interactWithViewModel(){
        rvpCommonViewModel.showErrorMessageLiveData.observe(this) { (message, isError) ->
            showMessageOnUI(message, isError)
        }
        rvpCommonViewModel.clearStackEvent.observe(this) {
            clearStackAndMoveToLoginActivity()
        }
    }

    private fun setupCheckBox() {
        val submitButton = activityPickupOtpBinding.bottomSubmitButton.submitButton
        val enabledColor = ContextCompat.getColor(this, R.color.button_enable_color)
        val otpFields = listOf(
            activityPickupOtpBinding.otpBoxOne,
            activityPickupOtpBinding.otpBoxTwo,
            activityPickupOtpBinding.otpBoxThree,
            activityPickupOtpBinding.otpBoxFour,
            activityPickupOtpBinding.otpResentText,
            activityPickupOtpBinding.consigneeCallButton,
            activityPickupOtpBinding.otpTimerText
        )

        activityPickupOtpBinding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            otpFields.forEach { field ->
                field.apply {
                    isClickable = !isChecked
                    isFocusable = !isChecked
                    isFocusableInTouchMode = !isChecked
                    isEnabled = !isChecked
                }
            }
            activityPickupOtpBinding.checkbox.buttonTintList = if (isChecked) ColorStateList.valueOf(enabledColor) else null
            submitButton.apply {
                if(isChecked){
                    backgroundTintList =  listBlue
                    text = getString(R.string.cancel_pickup)
                    isClickable = true
                    setOnClickListener {
                        if (checkButtonRecentlyClicked()) return@setOnClickListener
                        navigateToNextActivityWithData(CancelReasonCodeListActivity::class.java)
                    }
                } else{
                    checkOtpButtonCompletion()
                }
            }
        }
    }

    private fun setupOtpWork() {
        if (shipmentType.equals(Constants.RQC, ignoreCase = true) && isNetworkConnected) {
            activityPickupOtpBinding.otpResentText.isClickable = false
            rvpCommonViewModel.onRqcGenerateOtpApiCall(this, awbNumber, drsId, alternateClick = false, generateOtp = true)
        }
        activityPickupOtpBinding.otpInstruction.text = getString(
            R.string.please_ask_the_consignee_to_share_the_otp_code_sent_on_mobile_number,
            formatMobileNumber(consigneeMobile)
        )
        activityPickupOtpBinding.otpResentText.setOnClickListener {
            if (!isNetworkConnected) {
                showMessageOnUI(getString(R.string.check_internet), true)
                return@setOnClickListener
            }
            if (checkButtonRecentlyClicked()) {
                return@setOnClickListener
            }
            if (consigneeAlternateMobile.isEmpty() || formatMobileNumber(consigneeMobile) == formatMobileNumber(consigneeAlternateMobile)) {
                callGenerateOtpApi(false)
            } else {
                openSendOtpBottomSheet()
            }
        }
        activityPickupOtpBinding.consigneeCallButton.setOnClickListener {
            if (!isNetworkConnected) {
                showMessageOnUI(getString(R.string.check_internet), true)
                return@setOnClickListener
            }
            if (checkButtonRecentlyClicked()) {
                return@setOnClickListener
            }
            activityPickupOtpBinding.otpResentText.isClickable = false
            activityPickupOtpBinding.consigneeCallButton.isClickable = false
            rvpCommonViewModel.doVoiceOTPApiCall(this, awbNumber, drsId, shipmentType)
        }
        rvpCommonViewModel.otpSendStatus.observe(this) { isOtpSent ->
            if (isOtpSent) {
                startOtpCountdownTimer()
            } else {
                activityPickupOtpBinding.otpResentText.isClickable = true
                activityPickupOtpBinding.consigneeCallButton.isClickable = true
            }
        }
    }

    private fun formatMobileNumber(mobileNumber: String): String {
        val sanitizedNumber = mobileNumber.takeLast(10)
        return if (sanitizedNumber.length == 10) {
            "XXXXXX${sanitizedNumber.takeLast(4)}"
        } else {
            sanitizedNumber
        }
    }

    private fun startOtpCountdownTimer() {
        timerIsOn = true
        val countdownTimeMillis = rvpCommonViewModel.dataManager.getDisableResendOtpButtonDuration() * 1000L
        with(activityPickupOtpBinding) {
            otpResentText.visibility = View.GONE
            consigneeCallButton.visibility = View.GONE
            otpTimerText.visibility = View.VISIBLE
        }

        countdownTimer = object : CountDownTimer(countdownTimeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                activityPickupOtpBinding.otpTimerText.text = getString(R.string.resend_code_in_seconds, secondsRemaining)
            }

            override fun onFinish() {
                timerIsOn = false
                with(activityPickupOtpBinding) {
                    otpTimerText.visibility = View.GONE
                    otpResentText.visibility = View.VISIBLE
                    consigneeCallButton.visibility = View.VISIBLE
                    otpResentText.isClickable = true
                    consigneeCallButton.isClickable = true
                }
            }
        }.start()
    }

    @SuppressLint("SetTextI18n")
    private fun setupHeader() {
        with(activityPickupOtpBinding.header) {
            headingName.text = getString(R.string.pickup)
            versionName.text = "v" + BuildConfig.VERSION_NAME
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupAWBHeader() {
        val iconResource = if (shipmentType.equals(Constants.RVP, ignoreCase = true)) R.drawable.rvp_tag_icon else R.drawable.rqc_tag_icon
        activityPickupOtpBinding.rqcAwbHeader.awbNumber.apply {
            setCompoundDrawablesWithIntrinsicBounds(0, 0, iconResource, 0)
            text = getString(R.string.awb) + " $awbNumber"
        }
    }

    private fun setupSubmitButton() {
        listBlue = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.button_enable_color))
        listGray = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.button_disable_color))
        with(activityPickupOtpBinding.bottomSubmitButton.submitButton) {
            text = getString(R.string.verify_code)
            isClickable = false
        }
    }

    private fun setupBackNavigation() {
        activityPickupOtpBinding.header.backArrow.setOnClickListener { backPress() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPress()
            }
        })
    }

    private fun callGenerateOtpApi(allowAlternate: Boolean) {
        if (!isNetworkConnected) {
            showMessageOnUI(getString(R.string.check_internet), true)
            return
        }
        activityPickupOtpBinding.otpResentText.isClickable = false
        activityPickupOtpBinding.consigneeCallButton.isClickable = false
        when (shipmentType.lowercase()) {
            Constants.RVP.lowercase() -> {
                rvpCommonViewModel.onRvpGenerateOtpApiCall(this, awbNumber, drsId, allowAlternate)
            }
            else -> {
                rvpCommonViewModel.onRqcGenerateOtpApiCall(this, awbNumber, drsId, allowAlternate, false)
            }
        }
    }

    private fun openSendOtpBottomSheet() {
        try {
            rvpQcOtpBottomSheetBinding = RvpQcOtpBottomSheetBinding.inflate(layoutInflater)
            bottomSheetDialog = BottomSheetDialog(this, R.style.otpsheetDialogTheme)
            bottomSheetDialog.setContentView(rvpQcOtpBottomSheetBinding.root)
            rvpQcOtpBottomSheetBinding.primaryContactNumber.text = formatMobileNumber(consigneeMobile)
            rvpQcOtpBottomSheetBinding.alternateContactNumber.text = formatMobileNumber(consigneeAlternateMobile)
            rvpQcOtpBottomSheetBinding.primaryCallCard.setOnClickListener {
                callGenerateOtpApi(false)
                bottomSheetDialog.dismiss()
            }
            rvpQcOtpBottomSheetBinding.alternateCallCard.setOnClickListener {
                callGenerateOtpApi(true)
                bottomSheetDialog.dismiss()
            }

            bottomSheetDialog.setCanceledOnTouchOutside(true)

            val behavior = BottomSheetBehavior.from<View>(rvpQcOtpBottomSheetBinding.root.parent as View)
            behavior.isHideable = true
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED

            if (!bottomSheetDialog.isShowing) {
                bottomSheetDialog.show()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun backPress() {
        if (timerIsOn) {
            showMessageOnUI(getString(R.string.backbutton_is_disabled_until_the_timer_is_off), true)
        } else {
            finish()
            CommonUtils.applyTransitionToBackFromActivity(this)
        }
    }

    private fun checkOtpButtonCompletion() {
        val otp = listOf(
            activityPickupOtpBinding.otpBoxOne.text,
            activityPickupOtpBinding.otpBoxTwo.text,
            activityPickupOtpBinding.otpBoxThree.text,
            activityPickupOtpBinding.otpBoxFour.text
        ).joinToString("") { it.toString() }

        val submitButton = activityPickupOtpBinding.bottomSubmitButton.submitButton

        if (otp.length == 4) {
            hideKeyboard(this@PickupOtpActivity)
            submitButton.apply {
                backgroundTintList = listBlue
                text = getString(R.string.verify_code)
                isClickable = true
                setOnClickListener {
                    if (!isNetworkConnected) {
                        showMessageOnUI(getString(R.string.check_internet), true)
                        return@setOnClickListener
                    }
                    if(checkButtonRecentlyClicked()){
                        return@setOnClickListener
                    }
                    if(shipmentType.equals(Constants.RVP, ignoreCase = true)){
                        rvpCommonViewModel.onRvpOtpVerifyApiCall(this@PickupOtpActivity, otp, awbNumber)
                    } else{
                        rvpCommonViewModel.onRqcOtpVerifyApiCall(this@PickupOtpActivity, awbNumber, otp, drsId)
                    }
                    rvpCommonViewModel.otpVerifyStatus.observe(this@PickupOtpActivity) { isOtpVerified ->
                        if (isOtpVerified) {
                            ofdOtp = getString(R.string.verified)
                            if (shipmentType.equals(Constants.RVP, ignoreCase = true)) {
                                navigateToNextActivityWithData(CaptureScanActivity::class.java)
                            } else {
                                navigateToNextActivityWithData(RvpQCDetailActivity::class.java)
                            }
                        }
                    }
                }
            }
        } else {
            submitButton.apply {
                backgroundTintList = listGray
                text = getString(R.string.verify_code)
                isClickable = false
            }
        }
    }

    private fun checkButtonRecentlyClicked(): Boolean {
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
            return true
        } else {
            lastClickTime = SystemClock.elapsedRealtime()
            return false
        }
    }

    inner class OtpTextWatcher(private val nextBox: TextInputEditText?, private val previousBox: TextInputEditText?) : TextWatcher {

        private var isBackspace = false

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            isBackspace = count > after
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (isBackspace && s.isNullOrEmpty()) {
                previousBox?.requestFocus()
            } else if (!isBackspace && s?.length == 1) {
                nextBox?.requestFocus()
            }
            checkOtpButtonCompletion()
        }

        override fun afterTextChanged(s: Editable?) {
            // Nothing to do.
        }
    }

    private fun getIntentData() {
        try {
            intent?.let {
                drsId = it.getStringExtra(Constants.DRS_ID) ?: ""
                shipmentType = it.getStringExtra(Constants.SHIPMENT_TYPE) ?: ""
                ofdOtp = it.getStringExtra(Constants.OFD_OTP) ?: ""
                callAllowed = it.getBooleanExtra(Constants.CALL_ALLOWED, false)
                consigneeMobile = it.getStringExtra(Constants.CONSIGNEE_MOBILE) ?: ""
                awbNumber = it.getStringExtra(Constants.AWB_NUMBER) ?: ""
                otpRequiredForDelivery = it.getStringExtra(Constants.otp_required_for_delivery) ?: ""
                resendSecureOtp = it.getBooleanExtra(Constants.RESEND_SECURE_OTP, false)
                consigneeAlternateMobile = it.getStringExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE) ?: ""
                consigneeName = it.getStringExtra(Constants.CONSIGNEE_NAME) ?: ""
                compositeKey = it.getStringExtra(Constants.COMPOSITE_KEY) ?: ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun navigateToNextActivityWithData(destinationActivity: Class<out Activity>) {
        val intent = Intent(this, destinationActivity).apply {
            putExtra(Constants.DRS_ID, drsId)
            putExtra(Constants.SHIPMENT_TYPE, shipmentType)
            putExtra(Constants.OFD_OTP, ofdOtp)
            putExtra(Constants.CALL_ALLOWED, callAllowed)
            putExtra(Constants.CONSIGNEE_MOBILE, consigneeMobile)
            putExtra(Constants.AWB_NUMBER, awbNumber)
            putExtra(Constants.otp_required_for_delivery, otpRequiredForDelivery)
            putExtra(Constants.RESEND_SECURE_OTP, resendSecureOtp)
            putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, consigneeAlternateMobile)
            putExtra(Constants.CONSIGNEE_NAME, consigneeName)
            putExtra(Constants.COMPOSITE_KEY, compositeKey)
        }
        startActivity(intent)
        CommonUtils.applyTransitionToOpenActivity(this)
    }

    private fun clearStackAndMoveToLoginActivity() {
        PreferenceUtils.clearPref(this)
        val intent = Intent(this, LoginActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        CommonUtils.applyTransitionToOpenActivity(this)
    }

    private fun showMessageOnUI(message: String?, isError: Boolean) {
        message?.let {
            if (isError) showSnackbar(it) else showSuccessInfo(it)
        }
    }

    override fun onDestroy() {
        countdownTimer?.cancel()
        countdownTimer = null
        super.onDestroy()
    }

    override fun getViewModel(): RvpCommonViewModel {
        return rvpCommonViewModel
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_pickup_otp
    }
}