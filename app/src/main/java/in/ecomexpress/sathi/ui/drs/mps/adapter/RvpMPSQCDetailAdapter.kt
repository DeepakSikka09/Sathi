package `in`.ecomexpress.sathi.ui.drs.mps.adapter

import android.annotation.SuppressLint
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import `in`.ecomexpress.sathi.R
import `in`.ecomexpress.sathi.databinding.QcCheckImageSingleItemLayoutBinding
import `in`.ecomexpress.sathi.databinding.QcCheckSingleItemLayoutBinding
import `in`.ecomexpress.sathi.databinding.ScannerViewLayoutBinding
import `in`.ecomexpress.sathi.repo.local.data.rvp.RvpCommit.QcWizard
import `in`.ecomexpress.sathi.repo.remote.model.masterdata.SampleQuestion
import `in`.ecomexpress.sathi.repo.remote.model.mps.RvpMpsQualityCheck
import `in`.ecomexpress.sathi.ui.base.BaseActivity
import `in`.ecomexpress.sathi.ui.drs.mps.navigator.AdapterRvpMPSQcClick
import `in`.ecomexpress.sathi.ui.drs.mps.viewmodel.MPSListViewModel
import `in`.ecomexpress.sathi.utils.CommonUtils
import `in`.ecomexpress.sathi.utils.Constants
import `in`.ecomexpress.sathi.utils.GlobalConstant
import `in`.ecomexpress.sathi.utils.Logger

class RvpMPSQCDetailAdapter(
    private val cameraClickListener: AdapterRvpMPSQcClick,
    private var mpsListViewModel: MPSListViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var adapterPos: Int = -1
    private var rvpQualityCheck = ArrayList<RvpMpsQualityCheck>()
    private var sampleQuestions = ArrayList<SampleQuestion>()
    private var editText: EditText? = null
    private var qcWizard: QcWizard = QcWizard()

    fun setAdapterData(adapterPos: Int, qcWizard: QcWizard) {
        this.adapterPos = adapterPos
        this.qcWizard = qcWizard
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
            val sampleQuestion = getSampleQuestionAsPerQualityCheckCode(
                sampleQuestions,
                rvpQualityCheck[position]
            )
            when (holder.itemViewType) {
                QC_CHECK_VIEW -> {
                    (holder as PassFailTypeViewHolder).bind(
                        sampleQuestion,
                        rvpQualityCheck[position],
                        cameraClickListener,
                        mpsListViewModel,
                        adapterPos,
                        qcWizard
                    )
                }

                QC_CHECK_IMAGE_VIEW -> {
                    (holder as PassFailImageTypeViewHolder).bind(
                        sampleQuestion,
                        rvpQualityCheck[position],
                        cameraClickListener,
                        mpsListViewModel,
                        adapterPos,
                        qcWizard
                    )
                }

                INPUT_SCANNER_VIEW -> {
                    (holder as ScannerTypeViewHolder).bind(
                        sampleQuestion,
                        rvpQualityCheck[position],
                        cameraClickListener,
                        mpsListViewModel,
                        adapterPos,
                        qcWizard,
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getSampleQuestionAsPerQualityCheckCode(
        sampleQuestions: ArrayList<SampleQuestion>,
        rvpMpsQualityCheck: RvpMpsQualityCheck,
    ): SampleQuestion {
        var sampleQuestionSingle = SampleQuestion()
        for (sampleQuestion in sampleQuestions) {
            if (sampleQuestion.code.equals(rvpMpsQualityCheck.qcCode, false)) {
                sampleQuestionSingle = sampleQuestion
            }
        }
        return sampleQuestionSingle
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

    inner class ScannerTypeViewHolder(private val binding: ScannerViewLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            sampleQuestion: SampleQuestion,
            rvpQualityCheck: RvpMpsQualityCheck,
            cameraClickListener: AdapterRvpMPSQcClick,
            mpsListViewModel: MPSListViewModel,
            adapterPos: Int,
            qcWizard: QcWizard
        ) {

            binding.captureImg.captureImageMaterialTextView.setText(R.string.capture_product_image)
            binding.captureImg.ivCamera.setImageResource(R.drawable.camera)

            if ((sampleQuestion.imageCaptureSettings.equals(
                    "O", ignoreCase = true
                ) || sampleQuestion.imageCaptureSettings.equals(
                    "N", ignoreCase = true
                )) && mpsListViewModel.dataManager.getHideCamera()
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
                Logger.e(RvpMPSQCDetailAdapter::class.java.getName(), e.message)
            }

            binding.captureImg.captureImageMaterialTextView.text =
                mpsListViewModel.imageSettingQcCheck(sampleQuestion)

            binding.questionContainer.questionNameMaterialTextView.text = buildString {
                append(sampleQuestion.name)
                append("( ")
                append(mpsListViewModel.getDetail(rvpQualityCheck, sampleQuestion))
                append(" )")
            }

            binding.questionContainer.description.text = sampleQuestion.instructions

            if (adapterPosition == adapterPos) {
                qcWizard.let {
                    binding.scannedText.setText("")
                    qcWizard.qcValue?.let {
                        binding.scannedText.setText(qcWizard.qcValue)
                    }
                    qcWizard.remarks?.let {
                        binding.etRemarks.setText(qcWizard.remarks)
                    }
                    qcWizard.imagePathWithWaterMark?.let {
                        if (qcWizard.imagePathWithWaterMark.isNotBlank()) {
                            binding.captureImg.ivImgShw.setImageBitmap(
                                CommonUtils.convertPathtoBitmap(
                                    qcWizard.imagePathWithWaterMark
                                )
                            )
                        }

                        if (qcWizard.imagePathWithWaterMark.isNotBlank()) {
                            binding.captureImg.captureImageMaterialTextView.setText(R.string.captured_product_image)
                            binding.captureImg.ivCamera.setImageResource(R.drawable.rvp_qc_image_delete)
                        }
                    }
                }
            }

            binding.scannedText.setOnFocusChangeListener { _, _ ->
                editText = binding.scannedText
            }
            binding.ivScan.setOnClickListener {
                editText?.clearFocus()
                cameraClickListener.onScanClick(
                    adapterPosition, sampleQuestion, rvpQualityCheck
                )
            }
            binding.captureImg.cameraClick.setOnClickListener {
                editText?.clearFocus()
                if (qcWizard.imagePathWithWaterMark.isNullOrEmpty()) {
                    cameraClickListener.onItemClick(
                        rvpQualityCheck.awbNo.toString() + "_" + Constants.TEMP_DRSID + "_" + "MPS" + "_" + "QC_" + sampleQuestion.code + "_" + rvpQualityCheck.idNumber + ".png",
                        adapterPosition,
                        sampleQuestion,
                        rvpQualityCheck
                    )
                } else {
                    cameraClickListener.deleteImage(
                        adapterPosition, sampleQuestion, rvpQualityCheck
                    )
                }
            }

            binding.etRemarks.addTextChangedListener(afterTextChanged = {
                mpsListViewModel.createQcWizard(
                    checkValue = false,
                    remark = it.toString().trim(),
                    scannedData = binding.scannedText.text.toString().trim(),
                    sampleQuestion = sampleQuestion,
                    rvpQualityCheck = rvpQualityCheck,
                    isImageCheck = false,
                    imageBitmap = ""
                )
            })
        }
    }

    inner class PassFailTypeViewHolder(private val binding: QcCheckSingleItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            sampleQuestion: SampleQuestion,
            rvpQualityCheck: RvpMpsQualityCheck,
            cameraClickListener: AdapterRvpMPSQcClick,
            mpsListViewModel: MPSListViewModel,
            adapterPos: Int,
            qcWizard: QcWizard
        ) {
            if ((sampleQuestion.imageCaptureSettings.equals(
                    "O", ignoreCase = true
                ) || sampleQuestion.imageCaptureSettings.equals(
                    "N", ignoreCase = true
                )) && mpsListViewModel.dataManager.getHideCamera()
            ) {
                binding.captureImg.imageCapture.visibility = View.GONE
            }

            try {
                binding.etRemarks.filters = arrayOf<InputFilter>(BaseActivity.EMOJI_FILTER)
                binding.etRemarks.filters = arrayOf<InputFilter>(
                    InputFilter.LengthFilter(50)
                )
            } catch (e: Exception) {
                Logger.e(RvpMPSQCDetailAdapter::class.java.getName(), e.message)
            }

            binding.captureImg.captureImageMaterialTextView.setText(R.string.capture_product_image)
            binding.captureImg.ivCamera.setImageResource(R.drawable.camera)

            binding.questionContainer.questionNameMaterialTextView.text =
                mpsListViewModel.qcNameQcCheck(sampleQuestion, rvpQualityCheck)
            binding.questionContainer.description.text = sampleQuestion.instructions
            binding.captureImg.cameraClick.setOnClickListener {
                editText?.clearFocus()
                if (qcWizard.imagePathWithWaterMark.isNullOrEmpty()) {
                    cameraClickListener.onItemClick(
                        rvpQualityCheck.awbNo.toString() + "_" + Constants.TEMP_DRSID + "_" + "MPS" + "_" + "QC_" + sampleQuestion.code + "_" + rvpQualityCheck.idNumber + ".png",
                        adapterPosition,
                        sampleQuestion,
                        rvpQualityCheck
                    )
                } else {
                    cameraClickListener.deleteImage(
                        adapterPosition, sampleQuestion, rvpQualityCheck
                    )
                }
            }

            if (adapterPosition == adapterPos) {
                qcWizard.let {
                    qcWizard.match?.let {
                        if (qcWizard.match.equals("1", false)) {
                            binding.radioContainer.radioYes.isChecked = true
                            binding.radioContainer.radioYes.setBackgroundResource(R.drawable.bg_redio_button_selected)
                            binding.radioContainer.radioNo.isChecked = false
                            binding.radioContainer.radioNo.setBackgroundDrawable(null)
                        } else {
                            binding.radioContainer.radioNo.isChecked = true
                            binding.radioContainer.radioNo.setBackgroundResource(R.drawable.bg_redio_button_selected)
                            binding.radioContainer.radioYes.isChecked = false
                            binding.radioContainer.radioYes.setBackgroundDrawable(null)
                        }
                    }
                    qcWizard.remarks?.let {
                        binding.etRemarks.setText(qcWizard.remarks)
                    }
                    qcWizard.imagePathWithWaterMark?.let {
                        binding.captureImg.ivImgShw.setImageBitmap(
                            CommonUtils.convertPathtoBitmap(
                                qcWizard.imagePathWithWaterMark
                            )
                        )
                        if (qcWizard.imagePathWithWaterMark.isNotBlank()) {
                            binding.captureImg.captureImageMaterialTextView.setText(R.string.captured_product_image)
                            binding.captureImg.ivCamera.setImageResource(R.drawable.rvp_qc_image_delete)
                        }
                    }
                }
            }
            binding.radioContainer.radioNo.setOnClickListener {
                editText?.clearFocus()
                binding.radioContainer.radioNo.isChecked = true
                binding.radioContainer.radioNo.setBackgroundResource(R.drawable.bg_redio_button_selected)
                binding.radioContainer.radioYes.isChecked = false
                binding.radioContainer.radioYes.setBackgroundDrawable(null)
                sampleQuestion.isSelected = false
                mpsListViewModel.createQcWizard(
                    checkValue = false,
                    remark = binding.etRemarks.text.toString().trim(),
                    scannedData = "",
                    sampleQuestion = sampleQuestion,
                    rvpQualityCheck = rvpQualityCheck,
                    isImageCheck = false,
                    imageBitmap = ""
                )
            }
            binding.radioContainer.radioYes.setOnClickListener {
                editText?.clearFocus()
                binding.radioContainer.radioYes.isChecked = true
                binding.radioContainer.radioYes.setBackgroundResource(R.drawable.bg_redio_button_selected)
                binding.radioContainer.radioNo.isChecked = false
                binding.radioContainer.radioNo.setBackgroundDrawable(null)
                sampleQuestion.isSelected = true
                mpsListViewModel.createQcWizard(
                    checkValue = true,
                    remark = binding.etRemarks.text.toString().trim(),
                    scannedData = "",
                    sampleQuestion = sampleQuestion,
                    rvpQualityCheck = rvpQualityCheck,
                    isImageCheck = false,
                    imageBitmap = ""
                )
            }

//            sampleQuestion.isSelected?.let {
//                if (sampleQuestion.isSelected) {
//                    binding.radioContainer.radioYes.isChecked = true
//                    binding.radioContainer.radioYes.setBackgroundResource(R.drawable.bg_redio_button_selected)
//                    binding.radioContainer.radioNo.isChecked = false
//                    binding.radioContainer.radioNo.setBackgroundDrawable(null)
//                } else {
//                    binding.radioContainer.radioNo.isChecked = true
//                    binding.radioContainer.radioNo.setBackgroundResource(R.drawable.bg_redio_button_selected)
//                    binding.radioContainer.radioYes.isChecked = false
//                    binding.radioContainer.radioYes.setBackgroundDrawable(null)
//                }
//            }
        }
    }

    inner class PassFailImageTypeViewHolder(private val binding: QcCheckImageSingleItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            sampleQuestion: SampleQuestion,
            rvpQualityCheck: RvpMpsQualityCheck,
            cameraClickListener: AdapterRvpMPSQcClick,
            mpsListViewModel: MPSListViewModel,
            adapterPos: Int,
            qcWizard: QcWizard
        ) {

            if ((sampleQuestion.imageCaptureSettings.equals(
                    "O", ignoreCase = true
                ) || sampleQuestion.imageCaptureSettings.equals(
                    "N", ignoreCase = true
                )) && mpsListViewModel.dataManager.getHideCamera()
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
                Logger.e(RvpMPSQCDetailAdapter::class.java.getName(), e.message)
            }

            binding.captureImg.captureImageMaterialTextView.setText(R.string.capture_product_image)
            binding.captureImg.ivCamera.setImageResource(R.drawable.camera)

            binding.questionContainer.questionNameMaterialTextView.text =
                mpsListViewModel.getQcNameCheckImage(sampleQuestion)
            binding.questionContainer.description.text = sampleQuestion.instructions
            binding.captureImg.cameraClick.setOnClickListener {
                editText?.clearFocus()
                if (qcWizard.imagePathWithWaterMark.isNullOrEmpty()) {
                    cameraClickListener.onItemClick(
                        rvpQualityCheck.awbNo.toString() + "_" + Constants.TEMP_DRSID + "_" + "MPS" + "_" + "QC_" + sampleQuestion.code + "_" + rvpQualityCheck.idNumber + ".png",
                        adapterPosition,
                        sampleQuestion,
                        rvpQualityCheck
                    )
                } else {
                    cameraClickListener.deleteImage(
                        adapterPosition, sampleQuestion, rvpQualityCheck
                    )
                }
            }

            if (adapterPosition == adapterPos) {
                qcWizard.let {
                    qcWizard.match?.let {
                        if (qcWizard.match.equals("1", false)) {
                            binding.radioContainer.radioYes.isChecked = true
                            binding.radioContainer.radioYes.setBackgroundResource(R.drawable.bg_redio_button_selected)
                            binding.radioContainer.radioNo.isChecked = false
                            binding.radioContainer.radioNo.setBackgroundDrawable(null)
                        } else {
                            binding.radioContainer.radioNo.isChecked = true
                            binding.radioContainer.radioNo.setBackgroundResource(R.drawable.bg_redio_button_selected)
                            binding.radioContainer.radioYes.isChecked = false
                            binding.radioContainer.radioYes.setBackgroundDrawable(null)
                        }
                    }
                    qcWizard.remarks?.let {
                        binding.etRemarks.setText(qcWizard.remarks)
                    }
                    qcWizard.imagePathWithWaterMark?.let {
                        binding.captureImg.ivImgShw.setImageBitmap(
                            CommonUtils.convertPathtoBitmap(
                                qcWizard.imagePathWithWaterMark
                            )
                        )
                        if (qcWizard.imagePathWithWaterMark.isNotBlank()) {
                            binding.captureImg.captureImageMaterialTextView.setText(R.string.captured_product_image)
                            binding.captureImg.ivCamera.setImageResource(R.drawable.rvp_qc_image_delete)
                        }
                    }
                }
            }

            binding.radioContainer.radioNo.setOnClickListener {
                editText?.clearFocus()
                binding.radioContainer.radioNo.isChecked = true
                binding.radioContainer.radioNo.setBackgroundResource(R.drawable.bg_redio_button_selected)
                binding.radioContainer.radioYes.isChecked = false
                binding.radioContainer.radioYes.setBackgroundDrawable(null)
                sampleQuestion.isSelected = false
                mpsListViewModel.createQcWizard(
                    checkValue = false,
                    remark = binding.etRemarks.text.toString().trim(),
                    scannedData = "",
                    sampleQuestion = sampleQuestion,
                    rvpQualityCheck = rvpQualityCheck,
                    isImageCheck = false,
                    imageBitmap = ""
                )
            }

            binding.radioContainer.radioYes.setOnClickListener {
                editText?.clearFocus()
                binding.radioContainer.radioYes.isChecked = true
                binding.radioContainer.radioYes.setBackgroundResource(R.drawable.bg_redio_button_selected)
                binding.radioContainer.radioNo.isChecked = false
                binding.radioContainer.radioNo.setBackgroundDrawable(null)
                sampleQuestion.isSelected = true
                mpsListViewModel.createQcWizard(
                    checkValue = true,
                    remark = binding.etRemarks.text.toString().trim(),
                    scannedData = "",
                    sampleQuestion = sampleQuestion,
                    rvpQualityCheck = rvpQualityCheck,
                    isImageCheck = false,
                    imageBitmap = ""
                )
            }
//            sampleQuestion.isSelected?.let {
//                if (sampleQuestion.isSelected) {
//                    binding.radioContainer.radioYes.isChecked = true
//                    binding.radioContainer.radioYes.setBackgroundResource(R.drawable.bg_redio_button_selected)
//                    binding.radioContainer.radioNo.isChecked = false
//                    binding.radioContainer.radioNo.setBackgroundDrawable(null)
//                } else {
//                    binding.radioContainer.radioNo.isChecked = true
//                    binding.radioContainer.radioNo.setBackgroundResource(R.drawable.bg_redio_button_selected)
//                    binding.radioContainer.radioYes.isChecked = false
//                    binding.radioContainer.radioYes.setBackgroundDrawable(null)
//                }
//            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setAdapterData(
        rvpQualityCheck: List<RvpMpsQualityCheck>,
        sampleQuestions: List<SampleQuestion>,
    ) {
        this.qcWizard = QcWizard()
        this.rvpQualityCheck.clear()
        this.rvpQualityCheck.addAll(rvpQualityCheck)
        this.sampleQuestions.clear()
        this.sampleQuestions.addAll(sampleQuestions)
        notifyDataSetChanged()
    }

    companion object {
        const val QC_CHECK_VIEW: Int = 0
        const val QC_CHECK_IMAGE_VIEW: Int = 1
        const val INPUT_SCANNER_VIEW: Int = 2
    }
}