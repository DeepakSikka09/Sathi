package `in`.ecomexpress.sathi.ui.drs.mps.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
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
import `in`.ecomexpress.sathi.databinding.ActivityMpslistBinding
import `in`.ecomexpress.sathi.repo.local.data.rvp.RvpCommit.QcWizard
import `in`.ecomexpress.sathi.repo.local.db.model.RVPMPSWithQC
import `in`.ecomexpress.sathi.repo.remote.model.masterdata.SampleQuestion
import `in`.ecomexpress.sathi.repo.remote.model.mps.RvpMpsQualityCheck
import `in`.ecomexpress.sathi.ui.base.BaseActivity
import `in`.ecomexpress.sathi.ui.drs.mps.adapter.RvpMPSQCDetailAdapter
import `in`.ecomexpress.sathi.ui.drs.mps.navigator.AdapterRvpMPSQcClick
import `in`.ecomexpress.sathi.ui.drs.mps.viewmodel.MPSListViewModel
import `in`.ecomexpress.sathi.ui.drs.rvp.activity.RQCScannerActivity
import `in`.ecomexpress.sathi.ui.drs.rvp.adapter.ImageSliderAdapter
import `in`.ecomexpress.sathi.utils.CommonUtils
import `in`.ecomexpress.sathi.utils.Constants
import `in`.ecomexpress.sathi.utils.Logger
import `in`.ecomexpress.sathi.utils.cameraView.CameraActivity
import `in`.ecomexpress.sathi.utils.setupWithViewPager2


@AndroidEntryPoint
class MPSListActivity : BaseActivity<ActivityMpslistBinding, MPSListViewModel>(),
    AdapterRvpMPSQcClick {

    private val activityMpsListBinding: ActivityMpslistBinding by lazy {
        ActivityMpslistBinding.inflate(layoutInflater)
    }

    private val mpsListViewModel: MPSListViewModel by viewModels()
    private var drsId: String = ""
    private var shipmentType: String = ""
    private var amazonEncryptedOtp: String = ""
    private var amazon: String = ""
    private var compositeKey: String = ""
    private var awbNumber: String = ""
    private var consigneeAlternateMobile: String = ""
    private var consigneeName: String = ""
    private var ofdOtp: String = ""
    private lateinit var rvpMPSQCDetailAdapter: RvpMPSQCDetailAdapter
    private lateinit var sampleQuestion: SampleQuestion
    private lateinit var rvpQualityCheck: RvpMpsQualityCheck
    private var adapterPos = -1
    private var imageCaptured = false
    private val cameraRequestCode = 100
    private var capturedImageBitmap: Bitmap? = null
    private var imagePathWithWaterMark: String = ""
    private var imageCode: String = ""
    private val requestCodeScan = 1101
    private var verificationMode: String = ""
    private var codeQc: String = ""
    private var itemId: String = ""
    private lateinit var sampleQuestions: ArrayList<SampleQuestion>
    private lateinit var rvpmpsWithQC: RVPMPSWithQC
    private lateinit var qcImageUrls: List<List<String>>
    private var totalPos = 0
    private var currentPos = 0
    private var isFailed = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMpsListBinding.root)
        getIntentData()
        setupBackNavigation()
        setupAWBHeader()
        interactWithViewModel()
        setAdapterForQc()
        setupImageSlider(currentPos)
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
                ofdOtp = it.getStringExtra(Constants.OFD_OTP) ?: ""
                consigneeAlternateMobile =
                    it.getStringExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE) ?: ""
                consigneeName = it.getStringExtra(Constants.CONSIGNEE_NAME) ?: ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupBackNavigation() {
        activityMpsListBinding.header.backArrow.setOnClickListener { backPress() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPress()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setupAWBHeader() {
        activityMpsListBinding.rqcAwbHeader.awbNumber.apply {
            setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.mps_tag_icon, 0)
            text = getString(R.string.awb) + " $awbNumber"
        }
    }

    private fun interactWithViewModel() {
        mpsListViewModel.getRvpMPSDataWithQc(compositeKey)
        mpsListViewModel.getQcImageUrls(awbNumber.toLong(), Integer.parseInt(drsId))
        mpsListViewModel.showErrorMessageLiveData.observe(this) { (message, isError) ->
            showMessageOnUI(message, isError)
        }

        mpsListViewModel.imageCaptureStatus.observe(this) {
            if (!it) {
                showMessageOnUI("Please capture image", true)
            }
        }

        mpsListViewModel.nextScreenStatus.observe(this) { isNext ->
            if (mpsListViewModel.isCaptureDone) {
                isFailed = isNext
                if (isFailed == 0) {
                    navigateToNextActivityWithData(MpsCaptureScanActivity::class.java)
                } else {
                    currentPos += 1
                    if (totalPos != currentPos) {
                        setUpImageByPos(currentPos)
                        setAdapterForQc()
                    } else {
                        navigateToNextActivityWithData(MpsSuccessFailActivity::class.java)
                    }
                }
            }
        }
    }

    private fun navigateToNextActivityWithData(destinationActivity: Class<out Activity>) {
        Intent(this, destinationActivity).apply {
            putExtra(Constants.DRS_ID, drsId)
            putExtra(Constants.SHIPMENT_TYPE, shipmentType)
            putExtra(Constants.OFD_OTP, ofdOtp)
            putExtra(Constants.AWB_NUMBER, awbNumber)
            putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, consigneeAlternateMobile)
            putExtra(Constants.CONSIGNEE_NAME, consigneeName)
            putExtra(Constants.IS_FAILED, isFailed)
            putExtra(Constants.IS_IMAGE_MISSING, mpsListViewModel.isImageMissing)
            putParcelableArrayListExtra(
                Constants.QC_WIZARDS,
                mpsListViewModel.getQcItemListForCommit()
            )
        }.also { intent ->
            startActivity(intent)
            CommonUtils.applyTransitionToOpenActivity(this)
        }
    }

    private fun setupImageSlider(currentPos: Int) {
        activityMpsListBinding.apply {
            mpsListViewModel.qcImageUrls.observe(this@MPSListActivity) { urls ->
                qcImageUrls = urls
                setUpImageByPos(currentPos)
            }
        }
    }

    private fun setUpImageByPos(currentPos: Int) {
        if (qcImageUrls[currentPos].isEmpty()) {
            showNoQCImagePlaceHolder()
        } else {
            setupViewPager(currentPos)
        }

        //set header
        activityMpsListBinding.rqcProduct.text = "RQC Product " + (currentPos + 1) + "/" + totalPos
    }

    private fun setupViewPager(currentPos: Int) {
        if (qcImageUrls.isNotEmpty()) activityMpsListBinding.apply {
            qcImageView.adapter = ImageSliderAdapter(qcImageUrls[currentPos])
            qcImageIndicator.setupWithViewPager2(qcImageView)
        }
    }

    private fun showNoQCImagePlaceHolder() {
        activityMpsListBinding.apply {
            qcImageView.isGone = true
            qcImageIndicator.isGone = true
            noQCImageView.isVisible = true
        }
    }

    private fun setAdapterForQc() {
        rvpMPSQCDetailAdapter = RvpMPSQCDetailAdapter(this, mpsListViewModel)
        activityMpsListBinding.recycler.layoutManager = null
        activityMpsListBinding.recycler.layoutManager = LinearLayoutManager(this)
        activityMpsListBinding.recycler.isNestedScrollingEnabled = false
        activityMpsListBinding.recycler.adapter = rvpMPSQCDetailAdapter


        mpsListViewModel.sampleQuestions.observe(this@MPSListActivity) {
            sampleQuestions = it
            mpsListViewModel.rvpWithQC.observe(this@MPSListActivity) { it1 ->
                rvpmpsWithQC = it1
                totalPos = rvpmpsWithQC.qcItemList.size
                //set header
                activityMpsListBinding.rqcProduct.text =
                    "RQC Product " + (currentPos + 1) + "/" + totalPos
                rvpMPSQCDetailAdapter.setAdapterData(it1.qcItemList[currentPos].qualityChecks, it)
            }
        }
    }

    private fun setupBottomButton() {
        activityMpsListBinding.deliveredShipmentButton.submitButton.apply {
            text = getString(R.string.next_rts_scan)
            backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    this@MPSListActivity, R.color.button_enable_color
                )
            )
            setOnClickListener {
                if (totalPos != currentPos) {
                    val qcWizardList = ArrayList<QcWizard>()
                    val itemNum = rvpmpsWithQC.qcItemList[currentPos].qualityChecks[0].idNumber
                    mpsListViewModel.qcWizards.let {
                        val qcWizards = mpsListViewModel.qcWizards
                        for (qcwiz in qcWizards) {
                            if (qcwiz.item_number.equals(itemNum.toString(), false)) {
                                qcWizardList.add(qcwiz)
                            }
                        }
                    }
                    mpsListViewModel.qcDataVerifyForCommitPackets(
                        rvpmpsWithQC.qcItemList[currentPos].qualityChecks,
                        qcWizardList
                    )
                } else {
                    text = getString(R.string.submitButton)
                }
            }
        }
    }

    private fun backPress() {
        showSnackbar(getString(R.string.can_t_move_back))
    }

    @SuppressLint("SetTextI18n")
    private fun setupHeader() {
        activityMpsListBinding.header.headingName.text = getString(R.string.qc_details)
        activityMpsListBinding.header.versionName.text = "v" + BuildConfig.VERSION_NAME
    }

    private fun setUpCaptureImageWork() {
        mpsListViewModel.getImageSaveStatus.observe(this) { isImageUploaded ->
            if (isImageUploaded) {
                if (adapterPos != -1 && imagePathWithWaterMark.isNotBlank()) {
                    mpsListViewModel.createQcWizard(
                        checkValue = false,
                        remark = "",
                        scannedData = "",
                        sampleQuestion = sampleQuestion,
                        rvpQualityCheck = rvpQualityCheck,
                        isImageCheck = true,
                        imageBitmap = imagePathWithWaterMark
                    )
                    val qcWizard = getQcWizardAgainstQcCode(
                        mpsListViewModel.qcWizards, imageCode, itemId
                    )
                    rvpMPSQCDetailAdapter.let {
                        rvpMPSQCDetailAdapter.setAdapterData(adapterPos, qcWizard)
                    }
                }
            } else {
                showSnackbar(getString(R.string.image_not_uploaded_recapture_again))
            }
        }
    }

    private fun getQcWizardAgainstQcCode(
        qcWizards: ArrayList<QcWizard>, imageCode: String?, itemId: String
    ): QcWizard {
        var qcWizard = QcWizard()
        for (qcWiz in qcWizards) {
            if (qcWiz.qccheckcode.equals(imageCode, false) && qcWiz.item_number == itemId) {
                qcWizard = qcWiz
            }
        }
        return qcWizard
    }

    private fun showMessageOnUI(message: String?, isError: Boolean) {
        message?.let {
            if (isError) showSnackbar(it) else showSuccessInfo(it)
        }
    }

    override fun getViewModel(): MPSListViewModel {
        return mpsListViewModel
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_mpslist
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
                        mpsListViewModel.uploadImageServerRvpMPSQc(
                            this,
                            imageName.toString(),
                            imageUri.toString(),
                            imageCode.toString(),
                            awbNumber.toLongOrNull() ?: 0L,
                            drsId.toLongOrNull() ?: 0L,
                            bitmap,
                            mpsListViewModel.rvpWithQC.value?.getQcItemList()?.size
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
            Logger.e(MPSListViewModel::class.java.getName(), e.message)
        }
    }

    private fun setDataToAdapter(scannedData: String?) {
        if (adapterPos != -1) {
            scannedData?.let {
                mpsListViewModel.createQcWizard(
                    checkValue = false,
                    remark = "",
                    scannedData = it,
                    sampleQuestion = sampleQuestion,
                    rvpQualityCheck = rvpQualityCheck,
                    isImageCheck = false,
                    imageBitmap = ""
                )
                rvpMPSQCDetailAdapter.let {
                    val qcWizard: QcWizard =
                        getQcWizardAgainstQcCode(mpsListViewModel.qcWizards, codeQc, itemId)
                    rvpMPSQCDetailAdapter.setAdapterData(adapterPos, qcWizard)
                }
            }
        }
    }


    override fun onItemClick(
        imageName: String?,
        position: Int,
        sampleQuestion: SampleQuestion,
        rvpQualityCheck: RvpMpsQualityCheck
    ) {
        try {
            if (!isNetworkConnected) {
                showMessageOnUI(getString(R.string.check_internet), true)
                return
            }
            this.sampleQuestion = sampleQuestion
            this.rvpQualityCheck = rvpQualityCheck
            itemId = rvpQualityCheck.idNumber.toString()
            imageCaptured = true
            adapterPos = position
            Intent(this, CameraActivity::class.java).apply {
                putExtra("EmpCode", mpsListViewModel.dataManager.getEmp_code())
                putExtra("Latitude", mpsListViewModel.dataManager.getCurrentLatitude())
                putExtra("Longitude", mpsListViewModel.dataManager.getCurrentLongitude())
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

    override fun onScanClick(
        position: Int, sampleQuestion: SampleQuestion, rvpQualityCheck: RvpMpsQualityCheck
    ) {
        this.sampleQuestion = sampleQuestion
        this.rvpQualityCheck = rvpQualityCheck
        this.verificationMode = sampleQuestion.verificationMode
        itemId = rvpQualityCheck.idNumber.toString()
        codeQc = sampleQuestion.code
        adapterPos = position
        val intent = Intent(this, RQCScannerActivity::class.java)
        startActivityForResult(intent, requestCodeScan)
    }

    override fun deleteImage(
        position: Int, sampleQuestion: SampleQuestion, rvpQualityCheck: RvpMpsQualityCheck
    ) {
        if (position != -1) {
            itemId = rvpQualityCheck.idNumber.toString()
            mpsListViewModel.createQcWizard(
                checkValue = false,
                remark = "",
                scannedData = "",
                sampleQuestion = sampleQuestion,
                rvpQualityCheck = rvpQualityCheck,
                isImageCheck = true,
                imageBitmap = ""
            )
            rvpMPSQCDetailAdapter.let {
                val qcWizard: QcWizard = getQcWizardAgainstQcCode(
                    mpsListViewModel.qcWizards, sampleQuestion.code, itemId
                )
                rvpMPSQCDetailAdapter.setAdapterData(position, qcWizard)
            }
        }
    }
}