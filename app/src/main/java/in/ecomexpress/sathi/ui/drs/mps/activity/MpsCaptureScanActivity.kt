package `in`.ecomexpress.sathi.ui.drs.mps.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.imageview.ShapeableImageView
import dagger.hilt.android.AndroidEntryPoint
import `in`.ecomexpress.sathi.BR
import `in`.ecomexpress.sathi.BuildConfig
import `in`.ecomexpress.sathi.R
import `in`.ecomexpress.sathi.databinding.ActivityMpsCaptureScanBinding
import `in`.ecomexpress.sathi.repo.local.data.rvp.RvpCommit
import `in`.ecomexpress.sathi.ui.base.BaseActivity
import `in`.ecomexpress.sathi.ui.drs.mps.viewmodel.MpsPickupScanViewModel
import `in`.ecomexpress.sathi.ui.drs.rvp.navigator.RvpQcNavigator
import `in`.ecomexpress.sathi.utils.CommonUtils
import `in`.ecomexpress.sathi.utils.Constants
import `in`.ecomexpress.sathi.utils.cameraView.CameraActivity

@Suppress("DEPRECATION")
@AndroidEntryPoint
class MpsCaptureScanActivity :
    BaseActivity<ActivityMpsCaptureScanBinding, MpsPickupScanViewModel>(), RvpQcNavigator {

    private val activityMpsCaptureScanBinding: ActivityMpsCaptureScanBinding by lazy {
        ActivityMpsCaptureScanBinding.inflate(layoutInflater)
    }

    private var qcItemForCommit: ArrayList<RvpCommit.QcItem> = ArrayList()
    private val mpsPickupScanViewModel: MpsPickupScanViewModel by viewModels()
    private val cameraRequestCode = 100
    private var captureImageViewContainer: ConstraintLayout? = null
    private var capturedImageView: ShapeableImageView? = null
    private var capturedImageViewContainer: ConstraintLayout? = null
    private var imageCaptured = false
    private var capturedImageBitmap: Bitmap? = null
    private var listBlue: ColorStateList? = null
    private var listGray: ColorStateList? = null
    private var drsId: String = ""
    private var shipmentType: String = ""
    private var ofdOtp: String = ""
    private var awbNumber: String = ""
    private var consigneeAlternateMobile: String = ""
    private var itemDescription: String = ""
    private var consigneeName: String = ""
    private var isImageMissing = false
    private var isFailed = 3
    private var isFrom = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMpsCaptureScanBinding.root)
        getIntentData()
        setupBackNavigation()
        interactWithViewModel()
        setupUI()
    }

    private fun setupUI() {
        setupHeader()
        setupAWBHeader()
        setupSubmitButton()
        captureProductInsideFlyerImage()
        captureSealedFlyerImage()
        captureProductImage()
        setUpCaptureImageWork()
    }

    @SuppressLint("SetTextI18n")
    private fun setupHeader() {
        with(activityMpsCaptureScanBinding.header) {
            if (isFailed == 0) {
                headingName.text = getString(R.string.mark_qc_failed)
            } else {
                headingName.text = getString(R.string.start_packaging)
            }
            versionName.text = "v" + BuildConfig.VERSION_NAME
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupAWBHeader() {
        val iconResource = R.drawable.mps_tag_icon
        activityMpsCaptureScanBinding.rqcAwbHeader.awbNumber.apply {
            setCompoundDrawablesWithIntrinsicBounds(0, 0, iconResource, 0)
            text = getString(R.string.awb) + " $awbNumber"
        }
    }

    private fun setupSubmitButton() {
        mpsPickupScanViewModel.capturedImageCount.observe(this) { count ->
            updateSubmitButtonState(count)
        }
        listBlue = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.button_enable_color))
        listGray =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.button_disable_color))
        with(activityMpsCaptureScanBinding.bottomSubmitButton.submitButton) {
            if (isFailed == 0) {
                text = getString(R.string.submitButton)
                setOnClickListener {
                    navigateToNextActivityWithData(MpsSuccessFailActivity::class.java)
                }
            } else {
                text = getString(R.string.flyer_barcode_scan)
                setOnClickListener {
                    navigateToNextActivityWithData(MpsScanActivity::class.java)
                }
            }
        }
    }

    private fun updateSubmitButtonState(count: Int) {
        with(activityMpsCaptureScanBinding.bottomSubmitButton.submitButton) {
            if (isFailed == 0) {
                if (count >= 2) {
                    isClickable = true
                    backgroundTintList = listBlue
                } else {
                    isClickable = false
                    backgroundTintList = listGray
                }
            } else {
                if (count >= 3) {
                    isClickable = true
                    backgroundTintList = listBlue
                } else {
                    isClickable = false
                    backgroundTintList = listGray
                }
            }
        }
    }

    private fun captureProductImage() {
        with(activityMpsCaptureScanBinding) {
            if (isFailed == 0) {
                captureProductImage.root.visibility = View.GONE
                dividerOne.visibility = View.GONE
            } else {
                captureProductImage.root.visibility = View.VISIBLE
                dividerOne.visibility = View.VISIBLE
                captureProductImage.captureImageSample.setImageResource(R.drawable.rvp_qc_capture_product_image_icon)
                captureProductImage.captureImageButton.captureImageName.apply {
                    text = getString(R.string.capture_product_image)
                    setOnClickListener {
                        startCameraActivity("open1", "${awbNumber}_${drsId}_open1.png", "Product")
                        imageCaptured = !imageCaptured
                        captureImageViewContainer = captureProductImage.root
                        capturedImageViewContainer = capturedProductImage.root
                        capturedImageView = capturedProductImage.capturedFlyerImage
                    }
                }
                capturedProductImage.deleteCapturedImage.apply {
                    text = getString(R.string.capture_product_image)
                    setOnClickListener {
                        captureImageViewContainer = captureProductImage.root
                        capturedImageViewContainer = capturedProductImage.root
                        setCapturedImageBitmap(true)
                    }
                }
            }
        }
    }

    private fun captureProductInsideFlyerImage() {
        with(activityMpsCaptureScanBinding.captureProductInsideFlyerImage) {
            if (isFailed == 0) {
                captureImageSample.setImageResource(R.drawable.rvp_qc_capture_product_image_icon)
            } else {
                captureImageSample.setImageResource(R.drawable.rvp_qc_product_inside_flyer)
            }
            captureImageButton.captureImageName.apply {
                text = if (isFailed == 0) {
                    getString(R.string.capture_product_back_image)
                } else {
                    getString(R.string.capture_product_inside_flyer)
                }
                setOnClickListener {
                    if (isFailed == 0) {
                        startCameraActivity("Image", "${awbNumber}_${drsId}_Image.png", "Product")
                    } else {
                        startCameraActivity("open1_2", "${awbNumber}_${drsId}_open1_2.png", "Flyer")
                    }
                    imageCaptured = !imageCaptured
                    captureImageViewContainer = root
                    capturedImageViewContainer =
                        activityMpsCaptureScanBinding.capturedProductInsideFlyerImage.root
                    capturedImageView =
                        activityMpsCaptureScanBinding.capturedProductInsideFlyerImage.capturedFlyerImage
                }
            }
        }

        with(activityMpsCaptureScanBinding.capturedProductInsideFlyerImage) {
            deleteCapturedImage.text = if (isFailed == 0) {
                getString(R.string.captured_product_back_image)
            } else {
                getString(R.string.captured_product_inside_flyer)
            }
            deleteCapturedImage.setOnClickListener {
                captureImageViewContainer =
                    activityMpsCaptureScanBinding.captureProductInsideFlyerImage.root
                capturedImageViewContainer = root
                setCapturedImageBitmap(true)
            }
        }
    }

    private fun captureSealedFlyerImage() {
        with(activityMpsCaptureScanBinding.captureSealedFlyerImage) {
            if (isFailed == 0) {
                captureImageSample.setImageResource(R.drawable.rvp_qc_capture_product_image_icon)
            } else {
                captureImageSample.setImageResource(R.drawable.rvp_qc_sealed_flyer)
            }
            captureImageButton.captureImageName.apply {
                text = if (isFailed == 0) {
                    getString(R.string.capture_product_front_image)
                } else {
                    getString(R.string.capture_sealed_flyer)
                }
                setOnClickListener {
                    if (isFailed == 0) {
                        startCameraActivity("Image2", "${awbNumber}_${drsId}_Image2.png", "Product")
                    } else {
                        startCameraActivity("open2", "${awbNumber}_${drsId}_open2.png", "Flyer")
                    }
                    imageCaptured = !imageCaptured
                    captureImageViewContainer = root
                    capturedImageViewContainer =
                        activityMpsCaptureScanBinding.capturedSealedFlyerImage.root
                    capturedImageView =
                        activityMpsCaptureScanBinding.capturedSealedFlyerImage.capturedFlyerImage
                }
            }
        }

        with(activityMpsCaptureScanBinding.capturedSealedFlyerImage) {
            deleteCapturedImage.apply {
                text = if (isFailed == 0) {
                    getString(R.string.captured_product_front_image)
                } else {
                    getString(R.string.captured_sealed_flyer)
                }
                setOnClickListener {
                    captureImageViewContainer =
                        activityMpsCaptureScanBinding.captureSealedFlyerImage.root
                    capturedImageViewContainer = root
                    setCapturedImageBitmap(true)
                }
            }
        }
    }

    private fun setUpCaptureImageWork() {
        mpsPickupScanViewModel.getImageSaveStatus.observe(this) { isImageUploaded ->
            if (isImageUploaded) {
                setCapturedImageBitmap(!isImageUploaded)
            } else {
                showSnackbar(getString(R.string.image_not_uploaded_recapture_again))
            }
        }
    }

    private fun setCapturedImageBitmap(isFromImageDeletion: Boolean) {
        if (isFromImageDeletion) {
            mpsPickupScanViewModel.handleImageCaptureCount(true)
            captureImageViewContainer?.visibility = View.VISIBLE
            capturedImageViewContainer?.visibility = View.GONE
        } else {
            captureImageViewContainer?.visibility = View.GONE
            capturedImageViewContainer?.visibility = View.VISIBLE
            capturedImageView?.setImageBitmap(capturedImageBitmap)
        }
    }

    private fun getIntentData() {
        try {
            intent?.let {
                drsId = it.getStringExtra(Constants.DRS_ID) ?: ""
                shipmentType = it.getStringExtra(Constants.SHIPMENT_TYPE) ?: ""
                itemDescription = it.getStringExtra(Constants.ITEM_DESCRIPTION) ?: ""
                qcItemForCommit =
                    it.getParcelableArrayListExtra(Constants.QC_WIZARDS) ?: ArrayList()
                ofdOtp = it.getStringExtra(Constants.OFD_OTP) ?: ""
                awbNumber = it.getStringExtra(Constants.AWB_NUMBER) ?: ""
                consigneeAlternateMobile =
                    it.getStringExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE) ?: ""
                consigneeName = it.getStringExtra(Constants.CONSIGNEE_NAME) ?: ""
                isImageMissing = it.getBooleanExtra(Constants.IS_IMAGE_MISSING, false)
                isFailed = it.getIntExtra(Constants.IS_FAILED, 3)
                isFrom = it.getStringExtra(Constants.IS_FROM) ?: ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupBackNavigation() {
        activityMpsCaptureScanBinding.header.backArrow.setOnClickListener { backPress() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPress()
            }
        })
    }

    private fun navigateToNextActivityWithData(destinationActivity: Class<out Activity>) {
        Intent(this, destinationActivity).apply {
            putExtra(Constants.DRS_ID, drsId)
            putExtra(Constants.SHIPMENT_TYPE, shipmentType)
            putExtra(Constants.ITEM_DESCRIPTION, itemDescription)
            putParcelableArrayListExtra(Constants.QC_WIZARDS, qcItemForCommit)
            putExtra(Constants.OFD_OTP, ofdOtp)
            putExtra(Constants.AWB_NUMBER, awbNumber)
            putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, consigneeAlternateMobile)
            putExtra(Constants.CONSIGNEE_NAME, consigneeName)
            putExtra(Constants.IS_FAILED, isFailed)
            putExtra(Constants.IS_FROM, "RQC_MARK")
            putExtra(Constants.IS_IMAGE_MISSING, mpsPickupScanViewModel.isImageMissing)
        }.also { intent ->
            startActivity(intent)
            CommonUtils.applyTransitionToOpenActivity(this)
        }
    }

    private fun startCameraActivity(imageCode: String, imageName: String, activitySource: String) {
        try {
            if (!isNetworkConnected) {
                showMessageOnUI(getString(R.string.check_internet), true)
                return
            }
            Intent(this, CameraActivity::class.java).apply {
                putExtra("EmpCode", mpsPickupScanViewModel.dataManager.getEmp_code())
                putExtra("Latitude", mpsPickupScanViewModel.dataManager.getCurrentLatitude())
                putExtra("Longitude", mpsPickupScanViewModel.dataManager.getCurrentLongitude())
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

    private fun interactWithViewModel() {
        mpsPickupScanViewModel.showErrorMessageLiveData.observe(this) { (message, isError) ->
            showMessageOnUI(message, isError)
        }
    }

    private fun showMessageOnUI(message: String?, isError: Boolean) {
        message?.let {
            if (isError) showSnackbar(it) else showSuccessInfo(it)
        }
    }

    private fun backPress() {
        showSnackbar(getString(R.string.can_t_move_back))
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
                        mpsPickupScanViewModel.uploadImageToServer(
                            this,
                            imageName.toString(),
                            imageUri.toString(),
                            imageCode.toString(),
                            awbNumber,
                            drsId
                        )
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

    override fun getViewModel(): MpsPickupScanViewModel {
        return mpsPickupScanViewModel
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_mps_capture_scan
    }
}