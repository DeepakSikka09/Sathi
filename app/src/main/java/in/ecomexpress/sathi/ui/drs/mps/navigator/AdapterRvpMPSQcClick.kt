package `in`.ecomexpress.sathi.ui.drs.mps.navigator

import `in`.ecomexpress.sathi.repo.remote.model.masterdata.SampleQuestion
import `in`.ecomexpress.sathi.repo.remote.model.mps.RvpMpsQualityCheck

interface AdapterRvpMPSQcClick {

    fun onItemClick(imageName: String?, position: Int, sampleQuestion: SampleQuestion, rvpQualityCheck: RvpMpsQualityCheck)

    fun onScanClick(position: Int, sampleQuestion: SampleQuestion, rvpQualityCheck: RvpMpsQualityCheck)

    fun deleteImage(position: Int, sampleQuestion: SampleQuestion, rvpQualityCheck: RvpMpsQualityCheck)
}