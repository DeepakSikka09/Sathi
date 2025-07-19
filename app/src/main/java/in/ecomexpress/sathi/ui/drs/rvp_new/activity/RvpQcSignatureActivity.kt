package `in`.ecomexpress.sathi.ui.drs.rvp_new.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.widget.AdapterView
import android.widget.RadioButton
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import `in`.ecomexpress.sathi.BR
import `in`.ecomexpress.sathi.BuildConfig
import `in`.ecomexpress.sathi.R
import `in`.ecomexpress.sathi.databinding.ActivityRvpQcSignatureBinding
import `in`.ecomexpress.sathi.repo.local.data.rvp.RvpCommit
import `in`.ecomexpress.sathi.ui.base.BaseActivity
import `in`.ecomexpress.sathi.ui.drs.rvp_new.navigator.RvpQcNavigator
import `in`.ecomexpress.sathi.ui.drs.rvp_new.viewmodel.RvpCommonViewModel
import `in`.ecomexpress.sathi.utils.BitmapUtils
import `in`.ecomexpress.sathi.utils.CommonUtils
import `in`.ecomexpress.sathi.utils.Constants
import `in`.ecomexpress.sathi.utils.cameraView.CameraActivity
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class RvpQcSignatureActivity : BaseActivity<ActivityRvpQcSignatureBinding, RvpCommonViewModel>(), RvpQcNavigator {

    private val activityRvpQcSignatureBinding: ActivityRvpQcSignatureBinding by lazy {
        ActivityRvpQcSignatureBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var rvpCommit: RvpCommit
    private val rvpCommonViewModel: RvpCommonViewModel by viewModels()
    private val cameraRequestCode = 100
    private var isSignatureMandatory = false
    private var imageCaptured = false
    private var imageCapturedSuccessfully = false
    private var capturedImageView: MaterialTextView? = null
    private var capturedImageBitmap: Bitmap? = null
    private var listBlue: ColorStateList? = null
    private var listGray: ColorStateList? = null
    private var drsId: String = ""
    private var shipmentType: String = ""
    private var awbNumber: String = ""
    private var isOfdOtpVerified: String = ""
    private var scannedFlyerValue: String = ""
    private var consigneeName: String = ""
    private var shipmentReceivedByName: String = ""
    private var shipmentReceivedByRelation: String = ""
    private var isFromSignatureImageCaptured: Boolean = false
    private var qcWizards: ArrayList<RvpCommit.QcWizard> = ArrayList()
    private var isFailed = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityRvpQcSignatureBinding.root)
        getIntentData()
        setupBackNavigation()
        interactWithViewModel()
        setupUI()
    }

    private fun setupUI() {
        setupHeader()
        setupAWBHeader()
        setupSubmitButton()
        setupDeliveryPlace()
        setupSignaturePadWork()
        setupCaptureImageWork()
        setupPickFromAddressWork()
    }

    private fun setupPickFromAddressWork() {
        val binding = activityRvpQcSignatureBinding

        binding.pickupPersonNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                shipmentReceivedByName = parent?.selectedItem.toString()
                when (shipmentReceivedByName) {
                    getString(R.string.self) -> {
                        showPickupPersonLayout()
                        binding.pickupPersonName.apply {
                            isEnabled = false
                            setText(consigneeName)
                        }
                    }
                    getString(R.string.select) -> {
                        hidePickupPersonLayout()
                    }
                    else -> {
                        showPickupPersonLayout()
                        binding.pickupPersonName.apply {
                            setText("")
                            isEnabled = true
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Nothing to do.
            }
        }
    }

    private fun showPickupPersonLayout() {
        activityRvpQcSignatureBinding.pickupPersonNameTitle.visibility = View.VISIBLE
        activityRvpQcSignatureBinding.pickupPersonName.visibility = View.VISIBLE
    }

    private fun hidePickupPersonLayout() {
        activityRvpQcSignatureBinding.pickupPersonNameTitle.visibility = View.GONE
        activityRvpQcSignatureBinding.pickupPersonName.visibility = View.GONE
    }

    private fun setupDeliveryPlace() {
        activityRvpQcSignatureBinding.pickupAddressInformation.apply {
            setOnCheckedChangeListener { radioGroup, _ ->
                val radioButtonID: Int = radioGroup.checkedRadioButtonId
                val radioButton: RadioButton = radioGroup.findViewById(radioButtonID)
                shipmentReceivedByRelation = radioButton.text as String
            }
        }
    }

    private fun setupCaptureImageWork() {
        activityRvpQcSignatureBinding.captureImageButtonOne.captureImageName.apply {
            text = getString(R.string.capture_image)
            setOnClickListener {
                showAlertDialogBox(getString(R.string.alert), true)
            }
        }

        rvpCommonViewModel.getImageSaveStatus.observe(this) { isImageUploaded ->
            if (isImageUploaded && !isFromSignatureImageCaptured) {
                imageCapturedSuccessfully = true
                val drawable = BitmapDrawable(resources, capturedImageBitmap)
                val desiredSize = resources.getDimensionPixelSize(R.dimen._40DP)
                drawable.setBounds(0, 0, desiredSize, desiredSize)
                activityRvpQcSignatureBinding.captureImageButtonOne.captureImageName.setCompoundDrawables(null, null, drawable, null)
            } else {
                showSnackbar(getString(R.string.image_not_uploaded_recapture_again))
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun startCameraActivity(imageCode: String, imageName: String, activitySource: String) {
        try {
            if (!isNetworkConnected) {
                showMessageOnUI(getString(R.string.check_internet), true)
                return
            }
            Intent(this, CameraActivity::class.java).apply {
                putExtra("EmpCode", rvpCommonViewModel.dataManager.getEmp_code())
                putExtra("Latitude", rvpCommonViewModel.dataManager.getCurrentLatitude())
                putExtra("Longitude", rvpCommonViewModel.dataManager.getCurrentLongitude())
                putExtra("ImageCode", imageCode)
                putExtra("imageName", imageName)
                putExtra("awbNumber", awbNumber)
                putExtra("drs_id", drsId)
                putExtra("activitySource", activitySource)
            }.also { intent ->
                startActivityForResult(intent, cameraRequestCode)
            }
        } catch (e: Exception) {
            showMessageOnUI("Exception: ${e.localizedMessage}", isError = true)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == cameraRequestCode && resultCode == Activity.RESULT_OK) {
            if (!isNetworkConnected) {
                showSnackbar(getString(R.string.check_internet))
                return
            }
            try {
                if (data != null) {
                    if (imageCaptured) {
                        imageCaptured = false
                        val imagePathWithWaterMark = data.getStringExtra("imagePathWithWaterMark")
                        val bitmap = BitmapFactory.decodeFile(imagePathWithWaterMark)
                        val imageUri = data.getStringExtra("imageUri")
                        val imageCode = data.getStringExtra("imageCode")
                        val imageName = data.getStringExtra("imageName")
                        isFromSignatureImageCaptured = false
                        rvpCommonViewModel.uploadImageToServer(this, imageName.toString(), imageUri.toString(), imageCode.toString(), awbNumber, drsId)
                        capturedImageBitmap = bitmap
                    }
                } else {
                    showMessageOnUI(getString(R.string.captured_image_data_is_empty), true)
                }
            } catch (e: Exception) {
                showMessageOnUI(getString(R.string.captured_image_data_is_empty), true)
            }
        } else {
            showMessageOnUI(getString(R.string.captured_image_data_is_empty), true)
        }
    }

    private fun setupSignaturePadWork() {
        isSignatureMandatory = rvpCommonViewModel.dataManager.getIsSignatureImageMandatory().toBoolean()
        activityRvpQcSignatureBinding.rvpSignatureContainer.visibility = if (isSignatureMandatory) View.VISIBLE else View.GONE
        if (isSignatureMandatory) {
            activityRvpQcSignatureBinding.clearSignaturePad.setOnClickListener {
                activityRvpQcSignatureBinding.signaturePadView.clear()
            }
        }
    }

    private fun setupSubmitButton() {
        listBlue = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.button_enable_color))
        listGray = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.button_disable_color))
        with(activityRvpQcSignatureBinding.bottomSubmitButton.submitButton) {
            isClickable = true
            backgroundTintList = listBlue
            text = getString(R.string.mark_complete)
            setOnClickListener {
                checkShipmentCommitValidations()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupHeader() {
        activityRvpQcSignatureBinding.header.headingName.text = getString(R.string.signature)
        activityRvpQcSignatureBinding.header.versionName.text = "v" + BuildConfig.VERSION_NAME
    }

    @SuppressLint("SetTextI18n")
    private fun setupAWBHeader() {
        val iconResource = if (shipmentType.equals(Constants.RVP, ignoreCase = true)) R.drawable.rvp_tag_icon else R.drawable.rqc_tag_icon
        activityRvpQcSignatureBinding.rqcAwbHeader.awbNumber.apply {
            setCompoundDrawablesWithIntrinsicBounds(0, 0, iconResource, 0)
            text = getString(R.string.awb) + " $awbNumber"
        }
    }

    private fun interactWithViewModel() {
        rvpCommonViewModel.showErrorMessageLiveData.observe(this) { (message, isError) ->
            showMessageOnUI(message, isError)
        }
    }

    private fun showMessageOnUI(message: String?, isError: Boolean) {
        message?.let {
            if (isError) showSnackbar(it) else showSuccessInfo(it)
        }
    }

    private fun setupBackNavigation() {
        activityRvpQcSignatureBinding.header.backArrow.setOnClickListener { backPress() }
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

    private fun getIntentData() {
        try {
            intent?.let {
                drsId = it.getStringExtra(Constants.DRS_ID) ?: ""
                shipmentType = it.getStringExtra(Constants.SHIPMENT_TYPE) ?: ""
                isOfdOtpVerified = it.getStringExtra(Constants.OFD_OTP) ?: ""
                awbNumber = it.getStringExtra(Constants.AWB_NUMBER) ?: ""
                scannedFlyerValue = it.getStringExtra(Constants.RVP_FLYER_SCANNED_VALUE) ?: ""
                consigneeName = it.getStringExtra(Constants.CONSIGNEE_NAME) ?: ""
                isFailed = it.getIntExtra(Constants.IS_FAILED, 3)
                qcWizards = it.getParcelableArrayListExtra(Constants.QC_WIZARDS) ?: ArrayList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createCommitPacketAndUploadShipmentData() {
        try {
            rvpCommit.apply {
                drsId = this@RvpQcSignatureActivity.drsId
                awb = awbNumber
                attemptType = shipmentType
                status = when (isFailed) {
                    0 -> {
                        Constants.RVPUNDELIVERED
                    }
                    1 -> {
                        Constants.RVPDELIVERED
                    }
                    else -> {
                        Constants.RVPDELIVERED
                    }
                }
                attemptReasonCode = Constants.RVP_DELIVERED_REASON_CODE
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
                consigneeName = this@RvpQcSignatureActivity.consigneeName
                received_by = shipmentReceivedByName
                received_by_relation = shipmentReceivedByRelation
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
            rvpCommonViewModel.addImageListAndDoCommitApiCall(this@RvpQcSignatureActivity, rvpCommit, isNetworkConnected)
            rvpCommonViewModel.commitApiVerifyStatus.observe(this) { isShipmentCommitted ->
                if (isShipmentCommitted) {
                    navigateToNextActivityWithData(RvpQcSuccessFailActivity::class.java)
                } else {
                    showMessageOnUI(getString(R.string.shipment_commit_failed), true)
                }
            }
        } catch (e: Exception) {
            showMessageOnUI("Exception: ${e.localizedMessage}", true)
        }
    }

    private fun checkShipmentCommitValidations() {
        if (shipmentReceivedByRelation.trim().isEmpty()) {
            showMessageOnUI(getString(R.string.select_pickup_location), true)
            return
        }
        val trimmedShipmentReceivedByName = shipmentReceivedByName.trim()
        if (trimmedShipmentReceivedByName == getString(R.string.select)) {
            showMessageOnUI(getString(R.string.select_pick_from_options), true)
            return
        }
        val pickupPersonName = activityRvpQcSignatureBinding.pickupPersonName.text.toString().trim()
        if (pickupPersonName.isEmpty()) {
            showMessageOnUI(getString(R.string.enter_receiver_s_name), true)
            return
        }
        if (!pickupPersonName.matches(Regex("[a-zA-Z.? ]*"))) {
            showMessageOnUI(getString(R.string.enter_valid_receiver_s_name), true)
            return
        }
        if (!isNetworkConnected) {
            if(rvpCommonViewModel.dataManager.getRVPRealTimeSync().equals("true", ignoreCase = true)){
                showSnackbar(getString(R.string.check_internet))
                return
            }
        }
        if (!imageCapturedSuccessfully && Constants.RVP_Sign_Image_Required) {
            showMessageOnUI(getString(R.string.capture_image), true)
            return
        }
        if (rvpCommonViewModel.isFeWithinTheDcRange()) {
            showMessageOnUI(getString(R.string.shipment_cannot_be_mark_delivered_within_dc), true)
            return
        }
        if (!checkDataAndTimeAreCorrectOrNot()) {
            showAlertDialogBox(getString(R.string.commit_restriction_msg), false)
            return
        }
        if (isSignatureMandatory) {
            uploadSignatureImage()
        } else {
            createCommitPacketAndUploadShipmentData()
        }
    }

    @Suppress("DEPRECATION")
    private fun showAlertDialogBox(attentionMessage: String, isFromCaptureImage: Boolean) {
        val builder = AlertDialog.Builder(this, R.style.Theme_Material3_Light_Dialog_Alert)
        builder.setMessage(getString(R.string.attention) + attentionMessage)
            .setCancelable(false)
            .setPositiveButton("Ok") { _: DialogInterface, _: Int ->
                if(isFromCaptureImage){
                    startCameraActivity("Image", "${awbNumber}_${drsId}_Image.png", "Capture")
                    imageCaptured = !imageCaptured
                    capturedImageView =
                        activityRvpQcSignatureBinding.captureImageButtonOne.captureImageName
                } else {
                    startActivityForResult(Intent(Settings.ACTION_DATE_SETTINGS), 0)
                }
            }
        val alert = builder.create()
        alert.show()
    }

    private fun checkDataAndTimeAreCorrectOrNot(): Boolean {
        val calendar = Calendar.getInstance()
        val mDay = calendar[Calendar.DAY_OF_MONTH]
        val mMonth = calendar[Calendar.MONTH] + 1
        return rvpCommonViewModel.dataManager.getLoginDate().equals(mDay.toString(), ignoreCase = true) && rvpCommonViewModel.dataManager.getLoginMonth() == mMonth
    }

    private fun uploadSignatureImage() {
        try {
            val signatureImageBitmap: Bitmap
            val emptySignatureImageBitmap: Bitmap
            val fileDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/" + Constants.EcomExpress)
            if (!fileDir.exists()) fileDir.mkdirs()
            val file = File(fileDir, awbNumber + "_" + drsId + "_signature.png")
            if (!file.exists()) file.createNewFile()
            FileOutputStream(file).use {
                signatureImageBitmap = activityRvpQcSignatureBinding.signaturePadView.getSignatureBitmap()
                emptySignatureImageBitmap = activityRvpQcSignatureBinding.signaturePadView.getWhiteBackground(signatureImageBitmap)
                if (signatureImageBitmap.sameAs(emptySignatureImageBitmap)) {
                    showMessageOnUI(getString(R.string.signature_can_not_be_blank), true)
                } else {
                    BitmapUtils.saveBitmap(file, signatureImageBitmap)
                    isFromSignatureImageCaptured = true
                    if (!isNetworkConnected) {
                        rvpCommonViewModel.apply {
                            if (rvpCommonViewModel.dataManager.rvpRealTimeSync.equals("true", ignoreCase = true)) {
                                showMessageOnUI(getString(R.string.check_internet), true)
                            } else {
                                saveImageInLocalDB(this@RvpQcSignatureActivity, awbNumber, drsId, file.absolutePath, "signature", "${awbNumber}_${drsId}_signature.png", 0, 0)
                            }
                        }
                    } else {
                        rvpCommonViewModel.uploadImageToServer(this, "${awbNumber}_${drsId}_signature.png", file.absolutePath, "signature", awbNumber, drsId)
                    }
                }
            }
            rvpCommonViewModel.getImageSaveStatus.observe(this) { isImageUploaded ->
                if (isImageUploaded && isFromSignatureImageCaptured) {
                    createCommitPacketAndUploadShipmentData()
                }
            }
        } catch (e: Exception) {
            showMessageOnUI("Exception: ${e.localizedMessage}", true)
        }
    }

    private fun navigateToNextActivityWithData(destinationActivity: Class<out Activity>) {
        Intent(this, destinationActivity).apply {
            putExtra(Constants.SHIPMENT_TYPE, shipmentType)
            putExtra(Constants.AWB_NUMBER, awbNumber)
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
        return R.layout.activity_rvp_qc_signature
    }
}