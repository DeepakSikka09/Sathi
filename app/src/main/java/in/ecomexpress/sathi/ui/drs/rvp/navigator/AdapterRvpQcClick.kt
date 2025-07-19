package `in`.ecomexpress.sathi.ui.drs.rvp.navigator

import `in`.ecomexpress.sathi.repo.remote.model.drs_list.rvp.RvpQualityCheck
import `in`.ecomexpress.sathi.repo.remote.model.masterdata.SampleQuestion

interface AdapterRvpQcClick {
    fun onItemClick(imageName: String?, position: Int, sampleQuestion: SampleQuestion, rvpQualityCheck: RvpQualityCheck)

    fun onScanClick(position: Int, sampleQuestion: SampleQuestion, rvpQualityCheck: RvpQualityCheck)

    fun deleteImage(position: Int, sampleQuestion: SampleQuestion, rvpQualityCheck: RvpQualityCheck)
}