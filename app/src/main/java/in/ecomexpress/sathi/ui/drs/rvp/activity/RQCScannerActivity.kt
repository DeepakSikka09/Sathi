package `in`.ecomexpress.sathi.ui.drs.rvp.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import dagger.hilt.android.AndroidEntryPoint
import `in`.ecomexpress.sathi.R
import `in`.ecomexpress.sathi.databinding.ActivityRqcscannerBinding
import `in`.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity
import `in`.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics
import `in`.ecomexpress.sathi.utils.Logger

@AndroidEntryPoint
class RQCScannerActivity : AppCompatActivity() {

    private val activityName = RQCScannerActivity::class.java.simpleName
    private lateinit var binding: ActivityRqcscannerBinding
    private var lastText: String = ""
    private var lastScanTime: Long = 0

    companion object {
        const val SCANNED_CODE = "scanned_code"
        private const val DEBOUNCE_DELAY = 2000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rqcscanner)
        logScreenNameInGoogleAnalytics(activityName, this)
        setupBackNavigation()
        binding.ivFlash.setOnClickListener { switchFlashlight() }
        binding.zxingBarcodeScanner.barcodeView.decoderFactory = DefaultDecoderFactory()
        binding.zxingBarcodeScanner.initializeFromIntent(intent)
        binding.zxingBarcodeScanner.decodeContinuous(callback)
    }

    override fun onResume() {
        super.onResume()
        binding.zxingBarcodeScanner.resume()
    }

    override fun onPause() {
        super.onPause()
        try {
            binding.zxingBarcodeScanner.pause()
            offFlashLight()
        } catch (e: Exception) {
            Logger.e(activityName, e.message ?: "Unknown error")
        }
    }

    private val callback = BarcodeCallback { barcodeResult ->
        try {
            barcodeResult.text?.let { resultText ->
                if (resultText != lastText && System.currentTimeMillis() - lastScanTime > DEBOUNCE_DELAY) {
                    val returnIntent = Intent().apply {
                        putExtra(SCANNED_CODE, resultText)
                    }
                    setResult(RESULT_OK, returnIntent)
                    lastText = resultText
                    lastScanTime = System.currentTimeMillis()
                    offFlashLight()
                    finish()
                }
            }
        } catch (e: Exception) {
            Logger.e(activityName, e.message ?: "Unknown error")
        }
    }

    private fun switchFlashlight() {
        with(binding.ivFlash) {
            if (isSelected) {
                binding.zxingBarcodeScanner.setTorchOff()
                isSelected = false
                setImageResource(R.drawable.flashoff)
            } else {
                binding.zxingBarcodeScanner.setTorchOn()
                isSelected = true
                setImageResource(R.drawable.flashon)
            }
        }
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPress()
            }
        })
    }

    private fun backPress() {
        binding.zxingBarcodeScanner.setTorchOff()
        finish()
        applyTransitionToBackFromActivity(this)
    }

    private fun offFlashLight() {
        binding.zxingBarcodeScanner.setTorchOff()
        binding.ivFlash.isSelected = false
        binding.ivFlash.setImageResource(R.drawable.flashoff)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            offFlashLight()
        } catch (e: Exception) {
            Logger.e(activityName, e.message ?: "Unknown error")
        }
    }
}