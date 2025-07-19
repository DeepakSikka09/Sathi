package `in`.ecomexpress.sathi.ui.drs.rvp_new.navigator

interface AdapterRvpQcClick {
    fun onItemClick(
        imageCode: String?,
        imageName: String?,
        position: Int
    )

    fun onScanClick(verificationMode: String, position: Int)
}
