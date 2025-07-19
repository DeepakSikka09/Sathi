package `in`.ecomexpress.sathi.ui.drs.rvp.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import `in`.ecomexpress.sathi.BR
import `in`.ecomexpress.sathi.BuildConfig
import `in`.ecomexpress.sathi.R
import `in`.ecomexpress.sathi.databinding.ActivityRvpQcdetailBinding
import `in`.ecomexpress.sathi.repo.local.data.rvp.RvpCommit.QcWizard
import `in`.ecomexpress.sathi.repo.remote.model.drs_list.rvp.RvpQualityCheck
import `in`.ecomexpress.sathi.repo.remote.model.masterdata.SampleQuestion
import `in`.ecomexpress.sathi.ui.base.BaseActivity
import `in`.ecomexpress.sathi.ui.drs.rvp.adapter.ImageSliderAdapter
import `in`.ecomexpress.sathi.ui.drs.rvp.adapter.RvpQCDetailAdapter
import `in`.ecomexpress.sathi.ui.drs.rvp.navigator.AdapterRvpQcClick
import `in`.ecomexpress.sathi.ui.drs.rvp.viewmodel.RvpCommonViewModel
import `in`.ecomexpress.sathi.utils.CommonUtils
import `in`.ecomexpress.sathi.utils.Constants
import `in`.ecomexpress.sathi.utils.Logger
import `in`.ecomexpress.sathi.utils.cameraView.CameraActivity
import `in`.ecomexpress.sathi.utils.setupWithViewPager2

@AndroidEntryPoint
class RvpQCDetailActivity : BaseActivity<ActivityRvpQcdetailBinding, RvpCommonViewModel>(),
    AdapterRvpQcClick {

    private val activityRvpQcDetailBinding: ActivityRvpQcdetailBinding by lazy {
        ActivityRvpQcdetailBinding.inflate(layoutInflater)
    }

    private val rvpCommonViewModel: RvpCommonViewModel by viewModels()
    private val requestCodeScan = 1101
    private var verificationMode: String = ""
    private var codeQc: String = ""
    private var capturedImageBitmap: Bitmap? = null
    private val cameraRequestCode = 100
    private var adapterPos = -1
    private var imageCaptured = false
    private var drsId: String = ""
    private var shipmentType: String = ""
    private var amazonEncryptedOtp: String = ""
    private var amazon: String = ""
    private var compositeKey: String = ""
    private var awbNumber: String = ""
    private var consigneeAlternateMobile: String = ""
    private var itemDescription: String = ""
    private var consigneeName: String = ""
    private var ofdOtp: String = ""
    private var isFailed = 0
    private lateinit var rvpQCDetailAdapter: RvpQCDetailAdapter
    private lateinit var sampleQuestion: SampleQuestion
    private lateinit var rvpQualityCheck: RvpQualityCheck
    private var smartQc = "false"
    private var imagePathWithWaterMark: String = ""
    private var imageCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityRvpQcDetailBinding.root)
        getIntentData()
        setupBackNavigation()
        checkPhonePayShipment()
        setupAWBHeader()
        interactWithViewModel()
        setupImageSlider()
        setAdapterForQc()
        setupBottomButton()
        setupHeader()
        setUpCaptureImageWork()
    }

    private fun getIntentData() {
        try {
            intent?.let {
                drsId = it.getStringExtra(Constants.DRS_ID) ?: ""
                shipmentType = it.getStringExtra(Constants.SHIPMENT_TYPE) ?: ""
                amazonEncryptedOtp = it.getStringExtra(Constants.AMAZON_ENCRYPTED_OTP) ?: ""
                amazon = it.getStringExtra(Constants.AMAZON) ?: ""
                compositeKey = it.getStringExtra(Constants.COMPOSITE_KEY) ?: ""
                awbNumber = it.getStringExtra(Constants.AWB_NUMBER) ?: ""
                itemDescription = it.getStringExtra(Constants.ITEM_DESCRIPTION) ?: ""
                ofdOtp = it.getStringExtra(Constants.OFD_OTP) ?: ""
                consigneeAlternateMobile =
                    it.getStringExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE) ?: ""
                consigneeName = it.getStringExtra(Constants.CONSIGNEE_NAME) ?: ""
                smartQc = it.getStringExtra(Constants.SMART_QC_ENABLED) ?: ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
            putExtra(Constants.IS_FAILED, isFailed)
            putExtra(Constants.IS_FROM, "RQC_MARK")
            putExtra(Constants.IS_IMAGE_MISSING, rvpCommonViewModel.isImageMissing)
            putParcelableArrayListExtra(
                Constants.QC_WIZARDS,
                ArrayList<QcWizard>(rvpCommonViewModel.qcWizards)
            )
            putExtra(Constants.SMART_QC_ENABLED, smartQc)
        }.also { intent ->
            startActivity(intent)
            CommonUtils.applyTransitionToOpenActivity(this)
        }
    }

    private fun setupBackNavigation() {
        activityRvpQcDetailBinding.header.backArrow.setOnClickListener { backPress() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPress()
            }
        })
    }

    private fun backPress() {
        showSnackbar(getString(R.string.can_t_move_back))
    }

    private fun checkPhonePayShipment() {
        rvpCommonViewModel.getPhonePeShipmentType(awbNumber)
    }

    @SuppressLint("SetTextI18n")
    private fun setupAWBHeader() {
        if (smartQc.equals("true", true)) {
            activityRvpQcDetailBinding.rqcAwbHeader.awbNumber.apply {
                setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.smart_qc, 0)
                text = getString(R.string.awb) + " $awbNumber"
            }
            activityRvpQcDetailBinding.viewPagerLayout.visibility = View.GONE
        } else {
            activityRvpQcDetailBinding.rqcAwbHeader.awbNumber.apply {
                setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.rqc_tag_icon, 0)
                text = getString(R.string.awb) + " $awbNumber"
            }
        }
    }

    private fun interactWithViewModel() {
        rvpCommonViewModel.getRvpDataWithQc(compositeKey)

        if (shipmentType.equals(Constants.RQC, ignoreCase = true)) {
            rvpCommonViewModel.getQcImageUrls(this, awbNumber.toLongOrNull() ?: 0L)
        }
        rvpCommonViewModel.showErrorMessageLiveData.observe(this) { (message, isError) ->
            showMessageOnUI(message, isError)
        }
        rvpCommonViewModel.imageCaptureStatus.observe(this) {
            if (!it) {
                showMessageOnUI("Please capture image", true)
            }
        }

        rvpCommonViewModel.nextScreenStatus.observe(this) { isNext ->
            if (rvpCommonViewModel.isCaptureDone) {
                isFailed = isNext
                if (isFailed == 0) {
                    navigateToNextActivityWithData(CaptureScanActivity::class.java)
                } else {
                    navigateToNextActivityWithData(RvpQcSuccessFailActivity::class.java)
                }
            }
        }
    }

    private fun setupImageSlider() {
        rvpCommonViewModel.isPhonePayEnabled.observe(this@RvpQCDetailActivity) { isTrue ->
            if (isTrue) {
                showNoQCImagePlaceHolder(true)
                return@observe
            }
        }
        activityRvpQcDetailBinding.apply {
            rvpCommonViewModel.qcImageUrls.observe(this@RvpQCDetailActivity) { urls ->
                if (urls.isNullOrEmpty()) {
                    showNoQCImagePlaceHolder(false)
                } else {
                    setupViewPager(urls)
                }
            }
        }
    }

    private fun showNoQCImagePlaceHolder(isPhonepeShipment: Boolean) {
        activityRvpQcDetailBinding.apply {
            qcImageView.isGone = true
            qcImageIndicator.isGone = true
            noQCImageView.isVisible = true
            if (isPhonepeShipment) {
                noQCImageView.setImageResource(R.drawable.phonepe_placeholder)
            }
        }
    }

    private fun setupViewPager(imageUrls: List<String>) {
        activityRvpQcDetailBinding.apply {
            qcImageView.adapter = ImageSliderAdapter(imageUrls)
            qcImageIndicator.setupWithViewPager2(qcImageView)
        }
    }

    private fun setAdapterForQc() {
        rvpQCDetailAdapter = RvpQCDetailAdapter(this, rvpCommonViewModel)
        activityRvpQcDetailBinding.recycler.layoutManager = LinearLayoutManager(this)
        activityRvpQcDetailBinding.recycler.isNestedScrollingEnabled = false
        activityRvpQcDetailBinding.recycler.adapter = rvpQCDetailAdapter
        rvpCommonViewModel.isPhonePayEnabled.observe(this) { isTrue ->
            rvpCommonViewModel.sampleQuestions.observe(this@RvpQCDetailActivity) {
                rvpCommonViewModel.rvpWithQC.observe(this@RvpQCDetailActivity) { it1 ->
                    rvpQCDetailAdapter.setAdapterData(it1.rvpQualityCheckList, it, isTrue)
                }
            }
        }
    }

    private fun setupBottomButton() {
        activityRvpQcDetailBinding.deliveredShipmentButton.submitButton.apply {
            text = getString(R.string.submitButton)
            backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    this@RvpQCDetailActivity,
                    R.color.button_enable_color
                )
            )
            setOnClickListener { rvpCommonViewModel.qcDataVerifyForCommitPackets() }
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
                        capturedImageBitmap = bitmap
                        this.imagePathWithWaterMark = imagePathWithWaterMark.toString()
                        this.imageCode = imageCode.toString()
                        rvpCommonViewModel.uploadImageServerRvpQc(
                            this,
                            imageName.toString(),
                            imageUri.toString(),
                            imageCode.toString(),
                            awbNumber.toLongOrNull() ?: 0L,
                            drsId.toLongOrNull() ?: 0L,
                            bitmap
                        )
                    }
                } else {
                    showMessageOnUI(getString(R.string.captured_image_data_is_empty), true)
                }
            } catch (e: Exception) {
                showMessageOnUI(getString(R.string.captured_image_data_is_empty), true)
            }
        } else if (requestCode == requestCodeScan && resultCode == Activity.RESULT_OK) {
            try {
                handleScanResult(data)
            } catch (e: Exception) {
                showMessageOnUI(e.message, true)
            }
        }
    }

    private fun setUpCaptureImageWork() {
        rvpCommonViewModel.getImageSaveStatus.observe(this) { isImageUploaded ->
            if (isImageUploaded) {
                if (adapterPos != -1 && imagePathWithWaterMark.isNotBlank()) {
                    rvpCommonViewModel.createQcWizard(
                        checkValue = false,
                        remark = "",
                        scannedData = "",
                        sampleQuestion = sampleQuestion,
                        rvpQualityCheck = rvpQualityCheck,
                        isImageCheck = true,
                        imageBitmap = imagePathWithWaterMark
                    )
                    val qcWizard = getQcWizardAgainstQcCode(rvpCommonViewModel.qcWizards, imageCode)
                    rvpQCDetailAdapter.let {
                        rvpQCDetailAdapter.setAdapterData(adapterPos, qcWizard)
                    }
                }
            } else {
                showSnackbar(getString(R.string.image_not_uploaded_recapture_again))
            }
        }
    }

    private fun getQcWizardAgainstQcCode(
        qcWizards: ArrayList<QcWizard>,
        imageCode: String?
    ): QcWizard {
        var qcWizard = QcWizard()
        for (qcWiz in qcWizards) {
            if (qcWiz.qccheckcode.equals(imageCode, false)) {
                qcWizard = qcWiz
            }
        }
        return qcWizard
    }

    private fun handleScanResult(data: Intent?) {
        try {
            if (data != null && verificationMode != "") {
                var scannedData = data.getStringExtra(RQCScannerActivity.SCANNED_CODE)
                if (verificationMode.contains("TAIL")) {
                    val segments: Array<String> =
                        verificationMode.split("_".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    val trimDigit = segments[segments.size - 1]
                    scannedData = scannedData?.substring(scannedData.length - trimDigit.toInt())
                    setDataToAdapter(scannedData)
                } else if (verificationMode.contains("HEAD")) {
                    val segments: Array<String> =
                        verificationMode.split("_".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    val trimDigit = segments[segments.size - 1]
                    scannedData = scannedData?.substring(0, trimDigit.toInt())
                    setDataToAdapter(scannedData)
                } else {
                    setDataToAdapter(scannedData)
                }
            } else {
                Toast.makeText(this, "Cropped bar code scanned", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Logger.e(RvpQCDetailActivity::class.java.getName(), e.message)
        }
    }

    private fun setDataToAdapter(scannedData: String?) {
        if (adapterPos != -1) {
            scannedData?.let {
                rvpCommonViewModel.createQcWizard(
                    checkValue = false,
                    remark = "",
                    scannedData = it,
                    sampleQuestion = sampleQuestion,
                    rvpQualityCheck = rvpQualityCheck,
                    isImageCheck = false,
                    imageBitmap = ""
                )
                rvpQCDetailAdapter.let {
                    val qcWizard: QcWizard =
                        getQcWizardAgainstQcCode(rvpCommonViewModel.qcWizards, codeQc)
                    rvpQCDetailAdapter.setAdapterData(adapterPos, qcWizard)
                }
            }
        }
    }


    @Suppress("DEPRECATION")
    override fun onItemClick(
        imageName: String?,
        position: Int,
        sampleQuestion: SampleQuestion,
        rvpQualityCheck: RvpQualityCheck
    ) {
        try {
            if (!isNetworkConnected) {
                showMessageOnUI(getString(R.string.check_internet), true)
                return
            }
            this.sampleQuestion = sampleQuestion
            this.rvpQualityCheck = rvpQualityCheck
            imageCaptured = true
            adapterPos = position
            Intent(this, CameraActivity::class.java).apply {
                putExtra("EmpCode", rvpCommonViewModel.dataManager.getEmp_code())
                putExtra("Latitude", rvpCommonViewModel.dataManager.getCurrentLatitude())
                putExtra("Longitude", rvpCommonViewModel.dataManager.getCurrentLongitude())
                putExtra("ImageCode", sampleQuestion.code)
                putExtra("imageName", imageName)
                putExtra("awbNumber", awbNumber)
                putExtra("drs_id", drsId)
                putExtra("activitySource", "Product")
            }.also { intent ->
                startActivityForResult(intent, cameraRequestCode)
            }
        } catch (e: Exception) {
            showMessageOnUI("Exception: ${e.localizedMessage}", isError = true)
        }
    }

    @Suppress("DEPRECATION")
    override fun onScanClick(
        position: Int,
        sampleQuestion: SampleQuestion,
        rvpQualityCheck: RvpQualityCheck
    ) {
        this.sampleQuestion = sampleQuestion
        this.rvpQualityCheck = rvpQualityCheck
        this.verificationMode = sampleQuestion.verificationMode
        codeQc = sampleQuestion.code
        adapterPos = position
        val intent = Intent(this, RQCScannerActivity::class.java)
        startActivityForResult(intent, requestCodeScan)
    }

    override fun deleteImage(
        position: Int,
        sampleQuestion: SampleQuestion,
        rvpQualityCheck: RvpQualityCheck
    ) {
        if (position != -1) {
            rvpCommonViewModel.createQcWizard(
                checkValue = false,
                remark = "",
                scannedData = "",
                sampleQuestion = sampleQuestion,
                rvpQualityCheck = rvpQualityCheck,
                isImageCheck = true,
                imageBitmap = ""
            )
            rvpQCDetailAdapter.let {
                val qcWizard: QcWizard =
                    getQcWizardAgainstQcCode(rvpCommonViewModel.qcWizards, sampleQuestion.code)
                rvpQCDetailAdapter.setAdapterData(position, qcWizard)
            }
        }
    }

    override fun getViewModel(): RvpCommonViewModel {
        return rvpCommonViewModel
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_rvp_qcdetail
    }

    private fun showMessageOnUI(message: String?, isError: Boolean) {
        message?.let {
            if (isError) showSnackbar(it) else showSuccessInfo(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupHeader() {
        activityRvpQcDetailBinding.header.headingName.text = getString(R.string.qc_details)
        activityRvpQcDetailBinding.header.versionName.text = "v" + BuildConfig.VERSION_NAME
    }
}