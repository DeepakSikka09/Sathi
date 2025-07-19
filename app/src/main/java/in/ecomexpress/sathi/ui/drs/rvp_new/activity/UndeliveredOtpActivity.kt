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
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import `in`.ecomexpress.sathi.BuildConfig
import `in`.ecomexpress.sathi.R
import `in`.ecomexpress.sathi.databinding.ActivityUndeliveredOtpBinding
import `in`.ecomexpress.sathi.databinding.RvpQcOtpBottomSheetBinding
import `in`.ecomexpress.sathi.repo.remote.model.drs_list.forward.SecureDelivery
import `in`.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse
import `in`.ecomexpress.sathi.repo.remote.model.masterdata.RVPReasonCodeMaster
import `in`.ecomexpress.sathi.ui.auth.login.LoginActivity
import `in`.ecomexpress.sathi.ui.base.BaseActivity
import `in`.ecomexpress.sathi.ui.drs.rvp_new.navigator.RvpQcNavigator
import `in`.ecomexpress.sathi.ui.drs.rvp_new.viewmodel.UndeliveredOtpViewModel
import `in`.ecomexpress.sathi.utils.CommonUtils
import `in`.ecomexpress.sathi.utils.Constants
import `in`.ecomexpress.sathi.utils.PreferenceUtils

@AndroidEntryPoint
class UndeliveredOtpActivity :
    BaseActivity<ActivityUndeliveredOtpBinding, UndeliveredOtpViewModel>(),
    RvpQcNavigator {

    private var meterRange= 0
    private var rcSelectedData: Long? = null
    private val undeliveredOtpViewModel: UndeliveredOtpViewModel by viewModels()
    private lateinit var activityUndeliveredOtpBinding: ActivityUndeliveredOtpBinding

    private lateinit var rvpQcOtpBottomSheetBinding: RvpQcOtpBottomSheetBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var lastClickTime: Long = 0
    private var countdownTimer: CountDownTimer? = null
    private var timerIsOn = false
    private var listBlue: ColorStateList? = null
    private var listGray: ColorStateList? = null
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
    private var otpMessageType: String = ""
    private var otpVerified: Boolean = false
    private var selectedData: RVPReasonCodeMaster? = null
    private var drsData: DRSReverseQCTypeResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_undelivered_otp)
        activityUndeliveredOtpBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_undelivered_otp)

        getIntentData()
        setupBackNavigation()
        interactWithViewModel()
        setupUI()
    }

    override fun getViewModel(): UndeliveredOtpViewModel {
        return undeliveredOtpViewModel
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_undelivered_otp
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
        val otpBoxes = listOf(
            activityUndeliveredOtpBinding.otpBoxOne,
            activityUndeliveredOtpBinding.otpBoxTwo,
            activityUndeliveredOtpBinding.otpBoxThree,
            activityUndeliveredOtpBinding.otpBoxFour
        )
        otpBoxes.forEachIndexed { index, currentBox ->
            val nextBox = otpBoxes.getOrNull(index + 1)
            val previousBox = otpBoxes.getOrNull(index - 1)
            currentBox.addTextChangedListener(OtpTextWatcher(nextBox, previousBox))
        }
    }

    private fun interactWithViewModel() {
        undeliveredOtpViewModel.showErrorMessageLiveData.observe(this) { (message, isError) ->
            showMessageOnUI(message, isError)
        }
        undeliveredOtpViewModel.clearStackEvent.observe(this) {
            clearStackAndMoveToLoginActivity()
        }
    }

    private fun setupCheckBox() {
        val submitButton = activityUndeliveredOtpBinding.bottomSubmitButton.submitButton
        val enabledColor = ContextCompat.getColor(this, R.color.button_enable_color)
        val otpFields = listOf(
            activityUndeliveredOtpBinding.otpBoxOne,
            activityUndeliveredOtpBinding.otpBoxTwo,
            activityUndeliveredOtpBinding.otpBoxThree,
            activityUndeliveredOtpBinding.otpBoxFour,
            activityUndeliveredOtpBinding.otpResentText,
            activityUndeliveredOtpBinding.consigneeCallButton,
            activityUndeliveredOtpBinding.otpTimerText

        )

        activityUndeliveredOtpBinding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            otpFields.forEach { field ->
                field.apply {
                    isClickable = !isChecked
                    isFocusable = !isChecked
                    isFocusableInTouchMode = !isChecked
                    isEnabled = !isChecked
                }
            }
            activityUndeliveredOtpBinding.checkbox.buttonTintList =
                if (isChecked) ColorStateList.valueOf(enabledColor) else null
            submitButton.apply {
                if (isChecked) {
                    backgroundTintList = listBlue
                    text = getString(R.string.undelivered)
                    isClickable = true
                    otpVerified= false
                    Constants.OFD_OTP_VERIFIED = false
                    setOnClickListener {
                        if (checkButtonRecentlyClicked()) return@setOnClickListener
                        navigateToNextActivityWithData(RvpSuccessFailActivity::class.java)
                    }
                } else {
                    checkOtpButtonCompletion()
                }
            }
        }
    }

    private fun setupOtpWork() {
        if (!isNetworkConnected) {
            showMessageOnUI(getString(R.string.check_internet), true)
        } else {

            activityUndeliveredOtpBinding.otpResentText.isClickable = false
            undeliveredOtpViewModel.onRqcGenerateOtpApiCall(
                this,
                awbNumber,
                drsId,
                alternateClick = false,
                generateOtp = true,
                otpMessageType
            )

        }
        activityUndeliveredOtpBinding.otpInstruction.text = getString(
            R.string.please_ask_the_consignee_to_share_the_otp_code_sent_on_mobile_number,
            formatMobileNumber(consigneeMobile)
        )
        activityUndeliveredOtpBinding.otpResentText.setOnClickListener {
            if (!isNetworkConnected) {
                showMessageOnUI(getString(R.string.check_internet), true)
                return@setOnClickListener
            }
            if (checkButtonRecentlyClicked()) {
                return@setOnClickListener
            }
            if (consigneeAlternateMobile.isEmpty()) {
                callGenerateOtpApi(false)
            } else {
                openSendOtpBottomSheet()
            }
        }
        activityUndeliveredOtpBinding.consigneeCallButton.setOnClickListener {
            if (!isNetworkConnected) {
                showMessageOnUI(getString(R.string.check_internet), true)
                return@setOnClickListener
            }
            if (checkButtonRecentlyClicked()) {
                return@setOnClickListener
            }
            activityUndeliveredOtpBinding.otpResentText.isClickable = false
            activityUndeliveredOtpBinding.consigneeCallButton.isClickable = false
            undeliveredOtpViewModel.doVoiceOTPApiCall(
                this,
                awbNumber,
                drsId,
                shipmentType,
                otpMessageType
            )
        }
        undeliveredOtpViewModel.otpSendStatus.observe(this) { isOtpSent ->
            if (isOtpSent) {
                startOtpCountdownTimer()
            } else {
                activityUndeliveredOtpBinding.otpResentText.isClickable = true
                activityUndeliveredOtpBinding.consigneeCallButton.isClickable = true
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
        val countdownTimeMillis =
            undeliveredOtpViewModel.dataManager.getDisableResendOtpButtonDuration() * 1000L
        with(activityUndeliveredOtpBinding) {
            otpResentText.visibility = View.GONE
            consigneeCallButton.visibility = View.GONE
            otpTimerText.visibility = View.VISIBLE
        }

        countdownTimer = object : CountDownTimer(countdownTimeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                activityUndeliveredOtpBinding.otpTimerText.text =
                    getString(R.string.resend_code_in_seconds, secondsRemaining)
            }

            override fun onFinish() {
                timerIsOn = false
                with(activityUndeliveredOtpBinding) {
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
        with(activityUndeliveredOtpBinding.header) {
            headingName.text = getString(R.string.pickup)
            versionName.text = "v" + BuildConfig.VERSION_NAME
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupAWBHeader() {
        val iconResource = if (shipmentType.equals(
                Constants.RVP,
                ignoreCase = true
            )
        ) R.drawable.rvp_tag_icon else R.drawable.rqc_tag_icon
        activityUndeliveredOtpBinding.rqcAwbHeader.awbNumber.apply {
            setCompoundDrawablesWithIntrinsicBounds(0, 0, iconResource, 0)
            text = getString(R.string.awb) + " $awbNumber"
        }
    }

    private fun setupSubmitButton() {
        listBlue = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.button_enable_color))
        listGray =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.button_disable_color))
        with(activityUndeliveredOtpBinding.bottomSubmitButton.submitButton) {
            text = getString(R.string.verify_code)
            isClickable = false
        }
    }

    private fun setupBackNavigation() {
        activityUndeliveredOtpBinding.header.backArrow.setOnClickListener { backPress() }
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
        activityUndeliveredOtpBinding.otpResentText.isClickable = false
        activityUndeliveredOtpBinding.consigneeCallButton.isClickable = false
        when (shipmentType.lowercase()) {
            Constants.RVP.lowercase() -> {
                undeliveredOtpViewModel.onRvpGenerateOtpApiCall(
                    this,
                    awbNumber,
                    drsId,
                    allowAlternate
                )
            }

            else -> {
                undeliveredOtpViewModel.onRqcGenerateOtpApiCall(
                    this,
                    awbNumber,
                    drsId,
                    allowAlternate,
                    false,
                    otpMessageType
                )
            }
        }
    }

    private fun openSendOtpBottomSheet() {
        try {
            rvpQcOtpBottomSheetBinding = RvpQcOtpBottomSheetBinding.inflate(layoutInflater)
            bottomSheetDialog = BottomSheetDialog(this, R.style.otpsheetDialogTheme)
            bottomSheetDialog.setContentView(rvpQcOtpBottomSheetBinding.root)
            rvpQcOtpBottomSheetBinding.primaryContactNumber.text =
                formatMobileNumber(consigneeMobile)
            rvpQcOtpBottomSheetBinding.alternateContactNumber.text =
                formatMobileNumber(consigneeAlternateMobile)
            rvpQcOtpBottomSheetBinding.primaryCallCard.setOnClickListener {
                callGenerateOtpApi(false)
                bottomSheetDialog.dismiss()
            }
            rvpQcOtpBottomSheetBinding.alternateCallCard.setOnClickListener {
                callGenerateOtpApi(true)
                bottomSheetDialog.dismiss()
            }

            bottomSheetDialog.setCanceledOnTouchOutside(true)

            val behavior =
                BottomSheetBehavior.from<View>(rvpQcOtpBottomSheetBinding.root.parent as View)
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
            activityUndeliveredOtpBinding.otpBoxOne.text,
            activityUndeliveredOtpBinding.otpBoxTwo.text,
            activityUndeliveredOtpBinding.otpBoxThree.text,
            activityUndeliveredOtpBinding.otpBoxFour.text
        ).joinToString("") { it.toString() }

        val submitButton = activityUndeliveredOtpBinding.bottomSubmitButton.submitButton

        if (otp.length == 4) {
            submitButton.apply {
                backgroundTintList = listBlue
                text = getString(R.string.verify_code)
                isClickable = true
                setOnClickListener {
                    if (!isNetworkConnected) {
                        showMessageOnUI(getString(R.string.check_internet), true)
                        return@setOnClickListener
                    }
                    if (checkButtonRecentlyClicked()) {
                        return@setOnClickListener
                    }

                    undeliveredOtpViewModel.onRqcOtpVerifyApiCall(
                        this@UndeliveredOtpActivity,
                        awbNumber,
                        otp,
                        drsId,
                        otpMessageType
                    )

                    undeliveredOtpViewModel.otpVerifyStatus.observe(this@UndeliveredOtpActivity) { isOtpVerified ->
                        if (isOtpVerified) {
                            otpVerified=true
                            Constants.OFD_OTP_VERIFIED = true
                            ofdOtp = getString(R.string.verified)
                            navigateToNextActivityWithData(RvpSuccessFailActivity::class.java)
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

    inner class OtpTextWatcher(
        private val nextBox: TextInputEditText?,
        private val previousBox: TextInputEditText? = null
    ) :
        TextWatcher {

        private var isBackspace = false

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            isBackspace = count > after
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (isBackspace && s.isNullOrEmpty()) {
                previousBox?.requestFocus()
            } else if (s?.length == 1 && !isBackspace) {
                nextBox?.requestFocus()
            }
            checkOtpButtonCompletion()
        }

        override fun afterTextChanged(s: Editable?) {}
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
                rcSelectedData = intent.getLongExtra("selectedDate", 0)
                meterRange = intent.getIntExtra("meterRange", 0)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun navigateToNextActivityWithData(destinationActivity: Class<out Activity>) {
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
            putExtra("drsData", drsData)
            putExtra("otpMessageType", otpMessageType)
            putExtra("masterData", selectedData)
            putExtra("selectedDate", rcSelectedData)
            putExtra("otpVerified", otpVerified)
            putExtra("meterRange", meterRange)
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
}