package `in`.ecomexpress.sathi.ui.drs.rvp_new.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import `in`.ecomexpress.sathi.databinding.QcCheckImageSingleItemLayoutBinding
import `in`.ecomexpress.sathi.databinding.QcCheckSingleItemLayoutBinding
import `in`.ecomexpress.sathi.databinding.ScannerViewLayoutBinding
import `in`.ecomexpress.sathi.repo.remote.model.drs_list.rvp.RvpQualityCheck
import `in`.ecomexpress.sathi.repo.remote.model.masterdata.SampleQuestion
import `in`.ecomexpress.sathi.ui.base.BaseActivity
import `in`.ecomexpress.sathi.ui.drs.rvp_new.navigator.AdapterRvpQcClick
import `in`.ecomexpress.sathi.ui.drs.rvp_new.viewmodel.RvpCommonViewModel
import `in`.ecomexpress.sathi.utils.Constants
import `in`.ecomexpress.sathi.utils.GlobalConstant
import `in`.ecomexpress.sathi.utils.Logger

class RvpQCDetailAdapter(
    private val cameraClickListener: AdapterRvpQcClick,
    private var rvpQcCommonViewModel: RvpCommonViewModel,
    private var onItemImageCapture: (
        checkValue: String,
        remark: String,
        scannedData: String,
        sampleQuestion: SampleQuestion,
        rvpQualityCheck: RvpQualityCheck,
        isImageCheck: Boolean
    ) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var imageBitmap: Bitmap? = null
    private var adapterPos: Int = -1
    private var scannedData: String = ""

    private  var rvpQualityCheck = ArrayList<RvpQualityCheck>()
    private  var sampleQuestions = ArrayList<SampleQuestion>()
    private  var isPhonePay: Boolean?=null

    fun setImage(imageUri: Bitmap?, adapterPos: Int) {
        this.imageBitmap = imageUri
        this.adapterPos = adapterPos
        notifyItemChanged(adapterPos)
    }

    fun setScannedData(scannedData: String, adapterPos: Int) {
        this.scannedData = scannedData
        this.adapterPos = adapterPos
        notifyItemChanged(adapterPos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            QC_CHECK_VIEW -> {
                val viewBinding = QcCheckSingleItemLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return PassFailTypeViewHolder(viewBinding)
            }

            QC_CHECK_IMAGE_VIEW -> {
                val viewBinding = QcCheckImageSingleItemLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return PassFailImageTypeViewHolder(viewBinding)
            }

            INPUT_SCANNER_VIEW -> {
                val viewBinding = ScannerViewLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return ScannerTypeViewHolder(viewBinding)
            }

            else -> throw IllegalArgumentException("Invalid view type")

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            val sampleQuestion = sampleQuestions[position]
            when (holder.itemViewType) {
                QC_CHECK_VIEW -> {
                    (holder as PassFailTypeViewHolder).bind(
                        sampleQuestion,
                        rvpQualityCheck[position],
                        cameraClickListener,
                        rvpQcCommonViewModel,
                        adapterPos,
                        imageBitmap,
                        onItemImageCapture
                    )
                }

                QC_CHECK_IMAGE_VIEW -> {
                    (holder as PassFailImageTypeViewHolder).bind(
                        sampleQuestion,
                        rvpQualityCheck[position],
                        cameraClickListener,
                        rvpQcCommonViewModel,
                        adapterPos,
                        imageBitmap,
                        onItemImageCapture
                    )
                }

                INPUT_SCANNER_VIEW -> {
                    (holder as ScannerTypeViewHolder).bind(
                        sampleQuestion,
                        rvpQualityCheck[position],
                        cameraClickListener,
                        rvpQcCommonViewModel,
                        adapterPos,
                        imageBitmap,
                        scannedData,
                        isPhonePay,
                        onItemImageCapture
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return if (rvpQualityCheck.isNotEmpty()) {
            rvpQualityCheck.size
        } else {
            0
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (sampleQuestions[position].verificationMode.equals(
                GlobalConstant.QcTypeConstants.CHECK, true
            )
        ) {
            return QC_CHECK_VIEW
        } else if (sampleQuestions[position].verificationMode.equals(
                GlobalConstant.QcTypeConstants.CHECK_IMAGE, true
            )
        ) {
            return QC_CHECK_IMAGE_VIEW
        } else if (sampleQuestions[position].verificationMode.contains(
                GlobalConstant.QcTypeConstants.INPUT, true
            )
        ) {
            return INPUT_SCANNER_VIEW
        }
        return -1
    }

    class ScannerTypeViewHolder(private val binding: ScannerViewLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            sampleQuestion: SampleQuestion,
            rvpQualityCheck: RvpQualityCheck,
            cameraClickListener: AdapterRvpQcClick,
            rvpQcCommonViewModel: RvpCommonViewModel,
            adapterPos: Int,
            imageBitmap: Bitmap?,
            scannedData: String,
            isPhonePay: Boolean?,
            onItemImageCapture: (checkValue: String, remark: String, scannedData: String, sampleQuestion: SampleQuestion, rvpQualityCheck: RvpQualityCheck, isImageCheck: Boolean) -> Unit
        ) {
            //For phonePay Shipment
            if (isPhonePay == true) {
                binding.tvRemark.visibility = View.GONE
                binding.scannedText.isEnabled = false
            }

            if ((sampleQuestion.imageCaptureSettings.equals(
                    "O", ignoreCase = true
                ) || sampleQuestion.imageCaptureSettings.equals(
                    "N", ignoreCase = true
                )) && rvpQcCommonViewModel.dataManager.getHideCamera()
            ) {
                binding.captureImg.imageCapture.visibility = View.GONE
            }

            try {
                binding.etRemarks.filters = arrayOf<InputFilter>(BaseActivity.EMOJI_FILTER)
                binding.etRemarks.filters = arrayOf<InputFilter>(
                    InputFilter.LengthFilter(
                        50
                    )
                )
            } catch (e: Exception) {
                Logger.e(RvpQCDetailAdapter::class.java.getName(), e.message)
            }

            binding.captureImg.captureImageMaterialTextView.text =
                rvpQcCommonViewModel.imageSettingQcCheck(sampleQuestion)

            binding.questionContainer.questionNameMaterialTextView.text = buildString {
                append(sampleQuestion.name)
                append("( ")
                append(rvpQcCommonViewModel.getDetail(rvpQualityCheck, sampleQuestion))
                append(" )")
            }

            binding.questionContainer.description.text = sampleQuestion.instructions

            if (adapterPosition == adapterPos) {
                imageBitmap.let {
                    binding.captureImg.ivImgShw.setImageBitmap(imageBitmap)
                    onItemImageCapture(
                        "",
                        binding.etRemarks.text?.trim().toString(),
                        binding.scannedText.text?.trim().toString(),
                        sampleQuestion,
                        rvpQualityCheck,
                        true
                    )
                }
                scannedData.let {
                    binding.scannedText.setText(it)
                    binding.scannedText.text?.let { it1 ->
                        rvpQcCommonViewModel.createQcWizard(
                            checkValue = "",
                            remark = binding.etRemarks.text?.trim().toString(),
                            scannedData = it1.trim().toString(),
                            sampleQuestion = sampleQuestion,
                            rvpQualityCheck = rvpQualityCheck,
                            isImageCheck = false
                        )
                    }
                }
            }

            binding.scannedText.addTextChangedListener(afterTextChanged = {
                rvpQcCommonViewModel.createQcWizard(
                    checkValue = "",
                    remark = binding.etRemarks.text.toString().trim(),
                    scannedData = it.toString().trim(),
                    sampleQuestion = sampleQuestion,
                    rvpQualityCheck = rvpQualityCheck,
                    isImageCheck = false
                )
            })
            binding.ivScan.setOnClickListener {
                cameraClickListener.onScanClick(sampleQuestion.verificationMode, adapterPosition)
            }
            binding.captureImg.cameraClick.setOnClickListener {
                cameraClickListener.onItemClick(
                    sampleQuestion.code,
                    rvpQualityCheck.awbNo.toString() + "_" + Constants.TEMP_DRSID + "_" + "QC_" + sampleQuestion.code + ".png",
                    adapterPosition
                )
            }

            binding.etRemarks.addTextChangedListener(afterTextChanged = {
                rvpQcCommonViewModel.createQcWizard(
                    checkValue = "",
                    remark = it.toString().trim(),
                    scannedData = "",
                    sampleQuestion = sampleQuestion,
                    rvpQualityCheck = rvpQualityCheck,
                    isImageCheck = false
                )
            })
        }
    }

    class PassFailTypeViewHolder(private val binding: QcCheckSingleItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            sampleQuestion: SampleQuestion,
            rvpQualityCheck: RvpQualityCheck,
            cameraClickListener: AdapterRvpQcClick,
            rvpQcCommonViewModel: RvpCommonViewModel,
            adapterPos: Int,
            imageBitmap: Bitmap?,
            onItemImageCapture: (
                checkValue: String,
                remark: String,
                scannedData: String,
                sampleQuestion: SampleQuestion,
                rvpQualityCheck: RvpQualityCheck,
                isImageCheck: Boolean
            ) -> Unit
        ) {
            if ((sampleQuestion.imageCaptureSettings.equals(
                    "O", ignoreCase = true
                ) || sampleQuestion.imageCaptureSettings.equals(
                    "N", ignoreCase = true
                )) && rvpQcCommonViewModel.dataManager.getHideCamera()
            ) {
                binding.captureImg.imageCapture.visibility = View.GONE
            }

            try {
                binding.etRemarks.filters = arrayOf<InputFilter>(BaseActivity.EMOJI_FILTER)
                binding.etRemarks.filters = arrayOf<InputFilter>(
                    InputFilter.LengthFilter(
                        50
                    )
                )
            } catch (e: Exception) {
                Logger.e(RvpQCDetailAdapter::class.java.getName(), e.message)
            }

            binding.questionContainer.questionNameMaterialTextView.text =
                rvpQcCommonViewModel.qcNameQcCheck(sampleQuestion, rvpQualityCheck)
            binding.questionContainer.description.text = sampleQuestion.instructions
            binding.captureImg.cameraClick.setOnClickListener {
                cameraClickListener.onItemClick(
                    sampleQuestion.code,
                    rvpQualityCheck.awbNo.toString() + "_" + Constants.TEMP_DRSID + "_" + "QC_" + sampleQuestion.code + ".png",
                    adapterPosition
                )
            }
            if (adapterPosition == adapterPos) {
                imageBitmap.let {
                    binding.captureImg.ivImgShw.setImageBitmap(imageBitmap)
                    onItemImageCapture(
                        getCheckValue(binding.radioContainer.rgQualityCheck),
                        binding.etRemarks.text?.trim().toString(),
                        "",
                        sampleQuestion,
                        rvpQualityCheck,
                        true
                    )
                }
            }
            binding.radioContainer.radioNo.setOnClickListener {
                sampleQuestion.isSelected = false
                rvpQcCommonViewModel.createQcWizard(
                    checkValue = "No",
                    remark = binding.etRemarks.text.toString().trim(),
                    scannedData = "",
                    sampleQuestion = sampleQuestion,
                    rvpQualityCheck = rvpQualityCheck,
                    isImageCheck = false
                )
            }
            binding.radioContainer.radioYes.setOnClickListener {
                sampleQuestion.isSelected = true
                rvpQcCommonViewModel.createQcWizard(
                    checkValue = "Yes",
                    remark = binding.etRemarks.text.toString().trim(),
                    scannedData = "",
                    sampleQuestion = sampleQuestion,
                    rvpQualityCheck = rvpQualityCheck,
                    isImageCheck = false
                )
            }
            if (sampleQuestion.isSelected){
                binding.radioContainer.radioYes.isChecked=true
                binding.radioContainer.radioNo.isChecked=false
            }else{
                binding.radioContainer.radioNo.isChecked=true
                binding.radioContainer.radioYes.isChecked=false
            }
            /*binding.etRemarks.addTextChangedListener(afterTextChanged = {
                rvpQcCommonViewModel.createQcWizard(
                    checkValue = getCheckValue(binding.radioContainer.rgQualityCheck),
                    remark = it.toString().trim(),
                    scannedData = "",
                    sampleQuestion = sampleQuestion,
                    rvpQualityCheck = rvpQualityCheck,
                    isImageCheck = false
                )
            })*/
        }

        private fun getCheckValue(radioGroup: RadioGroup): String {
            val radioButtonID = radioGroup.checkedRadioButtonId
            val radioButton: RadioButton = radioGroup.findViewById(radioButtonID)
            val checkValue = radioButton.text as String
            return checkValue
        }
    }

    class PassFailImageTypeViewHolder(private val binding: QcCheckImageSingleItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            sampleQuestion: SampleQuestion,
            rvpQualityCheck: RvpQualityCheck,
            cameraClickListener: AdapterRvpQcClick,
            rvpQcCommonViewModel: RvpCommonViewModel,
            adapterPos: Int,
            imageBitmap: Bitmap?,
            onItemImageCapture: (checkValue: String, remark: String, scannedData: String, sampleQuestion: SampleQuestion, rvpQualityCheck: RvpQualityCheck, isImageCheck: Boolean) -> Unit
        ) {

            if ((sampleQuestion.imageCaptureSettings.equals(
                    "O", ignoreCase = true
                ) || sampleQuestion.imageCaptureSettings.equals(
                    "N", ignoreCase = true
                )) && rvpQcCommonViewModel.dataManager.getHideCamera()
            ) {
                binding.captureImg.imageCapture.visibility = View.GONE
            }

            try {
                binding.etRemarks.filters = arrayOf<InputFilter>(BaseActivity.EMOJI_FILTER)
                binding.etRemarks.filters = arrayOf<InputFilter>(
                    InputFilter.LengthFilter(
                        50
                    )
                )
            } catch (e: Exception) {
                Logger.e(RvpQCDetailAdapter::class.java.getName(), e.message)
            }

            binding.questionContainer.questionNameMaterialTextView.text =
                rvpQcCommonViewModel.getQcNameCheckImage(sampleQuestion)
            binding.questionContainer.description.text = sampleQuestion.instructions
            binding.captureImg.cameraClick.setOnClickListener {
                cameraClickListener.onItemClick(
                    sampleQuestion.code,
                    rvpQualityCheck.awbNo.toString() + "_" + Constants.TEMP_DRSID + "_" + "QC_" + sampleQuestion.code + ".png",
                    adapterPosition
                )
            }
            if (adapterPosition == adapterPos) {
                imageBitmap.let {
                    binding.captureImg.ivImgShw.setImageBitmap(imageBitmap)

                    onItemImageCapture(
                        "",
                        binding.etRemarks.text?.trim().toString(),
                        "",
                        sampleQuestion,
                        rvpQualityCheck,
                        true
                    )
                }
            }

            binding.radioContainer.radioNo.setOnClickListener{
                sampleQuestion.isSelected = false
                rvpQcCommonViewModel.createQcWizard(
                    checkValue = "No",
                    remark = binding.etRemarks.text.toString().trim(),
                    scannedData = "",
                    sampleQuestion = sampleQuestion,
                    rvpQualityCheck = rvpQualityCheck,
                    isImageCheck = false
                )
            }

            binding.radioContainer.radioYes.setOnClickListener{
                sampleQuestion.isSelected = true
                rvpQcCommonViewModel.createQcWizard(
                    checkValue = "Yes",
                    remark = binding.etRemarks.text.toString().trim(),
                    scannedData = "",
                    sampleQuestion = sampleQuestion,
                    rvpQualityCheck = rvpQualityCheck,
                    isImageCheck = false
                )
            }
            if (sampleQuestion.isSelected){
                binding.radioContainer.radioYes.isChecked=true
                binding.radioContainer.radioNo.isChecked=false
            }else{
                binding.radioContainer.radioNo.isChecked=true
                binding.radioContainer.radioYes.isChecked=false
            }
        }

    }
    @SuppressLint("NotifyDataSetChanged")
    fun setAdapterData(rvpQualityCheck: List<RvpQualityCheck>, sampleQuestions: List<SampleQuestion>, isPhonePay: Boolean){
        this.rvpQualityCheck.clear()
        this.rvpQualityCheck.addAll(rvpQualityCheck)
        this.sampleQuestions.clear()
        this.sampleQuestions.addAll(sampleQuestions)
        this.isPhonePay=isPhonePay
        notifyDataSetChanged()
    }

    companion object {
        const val QC_CHECK_VIEW: Int = 0
        const val QC_CHECK_IMAGE_VIEW: Int = 1
        const val INPUT_SCANNER_VIEW: Int = 2
    }
}