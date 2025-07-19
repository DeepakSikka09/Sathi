package `in`.ecomexpress.sathi.ui.drs.rvp_new.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
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
import `in`.ecomexpress.sathi.databinding.ActivityCaptureScanBinding
import `in`.ecomexpress.sathi.repo.local.data.rvp.RvpCommit
import `in`.ecomexpress.sathi.ui.base.BaseActivity
import `in`.ecomexpress.sathi.ui.drs.rvp_new.navigator.RvpQcNavigator
import `in`.ecomexpress.sathi.ui.drs.rvp_new.viewmodel.RvpCommonViewModel
import `in`.ecomexpress.sathi.utils.CommonUtils
import `in`.ecomexpress.sathi.utils.Constants
import `in`.ecomexpress.sathi.utils.cameraView.CameraActivity

@Suppress("DEPRECATION")
@AndroidEntryPoint
class CaptureScanActivity : BaseActivity<ActivityCaptureScanBinding, RvpCommonViewModel>(),
    RvpQcNavigator {

    private val activityCaptureScanBinding: ActivityCaptureScanBinding by lazy {
        ActivityCaptureScanBinding.inflate(layoutInflater)
    }

    private val rvpCommonViewModel: RvpCommonViewModel by viewModels()
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
    private var qcWizards: ArrayList<RvpCommit.QcWizard> = ArrayList()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityCaptureScanBinding.root)
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
        if (shipmentType.equals(Constants.RVP, ignoreCase = true)) {
            activityCaptureScanBinding.dividerOne.visibility = View.VISIBLE
            activityCaptureScanBinding.captureProductImageContainer.visibility = View.VISIBLE
            captureProductImage()
        }
        setUpCaptureImageWork()
    }

    @SuppressLint("SetTextI18n")
    private fun setupHeader() {
        with(activityCaptureScanBinding.header) {
            headingName.text = getString(R.string.start_packaging)
            versionName.text = "v" + BuildConfig.VERSION_NAME
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupAWBHeader() {
        val iconResource = if (shipmentType.equals(
                Constants.RVP, ignoreCase = true
            )
        ) R.drawable.rvp_tag_icon else R.drawable.rqc_tag_icon
        activityCaptureScanBinding.rqcAwbHeader.awbNumber.apply {
            setCompoundDrawablesWithIntrinsicBounds(0, 0, iconResource, 0)
            text = getString(R.string.awb) + " $awbNumber"
        }
    }

    private fun setupSubmitButton() {
        rvpCommonViewModel.capturedImageCount.observe(this) { count ->
            updateSubmitButtonState(count)
        }
        listBlue = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.button_enable_color))
        listGray =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.button_disable_color))
        with(activityCaptureScanBinding.bottomSubmitButton.submitButton) {
            text = getString(R.string.flyer_barcode_scan)
            setOnClickListener {
                navigateToNextActivityWithData(RvpQcScanActivity::class.java)
            }
        }
    }

    private fun updateSubmitButtonState(count: Int) {
        with(activityCaptureScanBinding.bottomSubmitButton.submitButton) {
            if (shipmentType.equals(Constants.RVP, ignoreCase = true)) {
                if (count >= 3) {
                    isClickable = true
                    backgroundTintList = listBlue
                } else {
                    isClickable = false
                    backgroundTintList = listGray
                }
            } else {
                if (count >= 2) {
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
        with(activityCaptureScanBinding) {
            if (shipmentType.equals(Constants.RQC, ignoreCase = true)) {
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
                    text = getString(R.string.captured_product_image)
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
        with(activityCaptureScanBinding.captureProductInsideFlyerImage) {
            captureImageSample.setImageResource(R.drawable.rvp_qc_product_inside_flyer)
            captureImageButton.captureImageName.apply {
                text = getString(R.string.capture_product_inside_flyer)
                setOnClickListener {
                    startCameraActivity("open1_2", "${awbNumber}_${drsId}_open1_2.png", "Flyer")
                    imageCaptured = !imageCaptured
                    captureImageViewContainer = root
                    capturedImageViewContainer =
                        activityCaptureScanBinding.capturedProductInsideFlyerImage.root
                    capturedImageView =
                        activityCaptureScanBinding.capturedProductInsideFlyerImage.capturedFlyerImage
                }
            }
        }

        with(activityCaptureScanBinding.capturedProductInsideFlyerImage) {
            deleteCapturedImage.text = getString(R.string.captured_product_inside_flyer)
            deleteCapturedImage.setOnClickListener {
                captureImageViewContainer =
                    activityCaptureScanBinding.captureProductInsideFlyerImage.root
                capturedImageViewContainer = root
                setCapturedImageBitmap(true)
            }
        }
    }

    private fun captureSealedFlyerImage() {
        with(activityCaptureScanBinding.captureSealedFlyerImage) {
            captureImageSample.setImageResource(R.drawable.rvp_qc_sealed_flyer)
            captureImageButton.captureImageName.apply {
                text = getString(R.string.capture_sealed_flyer)
                setOnClickListener {
                    startCameraActivity("open2", "${awbNumber}_${drsId}_open2.png", "Flyer")
                    imageCaptured = !imageCaptured
                    captureImageViewContainer = root
                    capturedImageViewContainer =
                        activityCaptureScanBinding.capturedSealedFlyerImage.root
                    capturedImageView =
                        activityCaptureScanBinding.capturedSealedFlyerImage.capturedFlyerImage
                }
            }
        }

        with(activityCaptureScanBinding.capturedSealedFlyerImage) {
            deleteCapturedImage.apply {
                text = getString(R.string.captured_sealed_flyer)
                setOnClickListener {
                    captureImageViewContainer =
                        activityCaptureScanBinding.captureSealedFlyerImage.root
                    capturedImageViewContainer = root
                    setCapturedImageBitmap(true)
                }
            }
        }
    }

    private fun setUpCaptureImageWork() {
        rvpCommonViewModel.getImageSaveStatus.observe(this) { isImageUploaded ->
            if (isImageUploaded) {
                setCapturedImageBitmap(!isImageUploaded)
            } else {
                showSnackbar(getString(R.string.image_not_uploaded_recapture_again))
            }
        }
    }

    private fun setCapturedImageBitmap(isFromImageDeletion: Boolean) {
        if (isFromImageDeletion) {
            rvpCommonViewModel.handleImageCaptureCount(true)
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
                ofdOtp = it.getStringExtra(Constants.OFD_OTP) ?: ""
                awbNumber = it.getStringExtra(Constants.AWB_NUMBER) ?: ""
                consigneeAlternateMobile =
                    it.getStringExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE) ?: ""
                consigneeName = it.getStringExtra(Constants.CONSIGNEE_NAME) ?: ""
                qcWizards = it.getParcelableArrayListExtra(Constants.QC_WIZARDS) ?: ArrayList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupBackNavigation() {
        activityCaptureScanBinding.header.backArrow.setOnClickListener { backPress() }
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
            putExtra(Constants.OFD_OTP, ofdOtp)
            putExtra(Constants.AWB_NUMBER, awbNumber)
            putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, consigneeAlternateMobile)
            putExtra(Constants.CONSIGNEE_NAME, consigneeName)
            putParcelableArrayListExtra(Constants.QC_WIZARDS, qcWizards)
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
                putExtra("EmpCode", rvpCommonViewModel.dataManager.getEmp_code())
                putExtra("Latitude", rvpCommonViewModel.dataManager.getCurrentLatitude())
                putExtra("Longitude", rvpCommonViewModel.dataManager.getCurrentLongitude())
                putExtra("ImageCode", imageCode)
                putExtra("imageName", imageName)
                putExtra("awbNumber", awbNumber)
                putExtra("drs_id", drsId)
                putExtra("activitySource", activitySource)
                putParcelableArrayListExtra(Constants.QC_WIZARDS, qcWizards)
            }.also { intent ->
                startActivityForResult(intent, cameraRequestCode)
            }
        } catch (e: Exception) {
            showMessageOnUI("Exception: ${e.localizedMessage}", isError = true)
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

    private fun backPress() {
        finish()
        CommonUtils.applyTransitionToBackFromActivity(this)
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
                        rvpCommonViewModel.uploadImageToServer(this, imageName.toString(), imageUri.toString(), imageCode.toString(), awbNumber, drsId)
                        capturedImageBitmap = bitmap
                    }
                } else {
                    showMessageOnUI(getString(R.string.captured_image_data_is_empty), true)
                }
            } catch (e: Exception) {
                showMessageOnUI(getString(R.string.captured_image_data_is_empty), true)
            }
        } else{
            showMessageOnUI(getString(R.string.captured_image_data_is_empty), true)
        }
    }

    override fun getViewModel(): RvpCommonViewModel {
        return rvpCommonViewModel
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_capture_scan
    }
}