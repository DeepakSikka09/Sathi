package `in`.ecomexpress.sathi.ui.drs.rvp_new.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fasterxml.jackson.databind.ObjectMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.ecomexpress.sathi.repo.IDataManager
import `in`.ecomexpress.sathi.repo.local.data.commit.PushApi
import `in`.ecomexpress.sathi.repo.local.data.rvp.RvpCommit
import `in`.ecomexpress.sathi.repo.local.db.model.RVPUndeliveredReasonCodeList
import `in`.ecomexpress.sathi.repo.remote.model.masterdata.RVPReasonCodeMaster
import `in`.ecomexpress.sathi.ui.base.BaseViewModel
import `in`.ecomexpress.sathi.ui.drs.rvp.undelivered.IRVPUndeliveredNavigator
import `in`.ecomexpress.sathi.ui.drs.rvp_new.navigator.RvpQcNavigator
import `in`.ecomexpress.sathi.utils.Constants
import `in`.ecomexpress.sathi.utils.Logger
import `in`.ecomexpress.sathi.utils.rx.ISchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class CancelPickupRemarksViewModel @Inject constructor(dataManager: IDataManager, schedulerProvider: ISchedulerProvider, application: Application) : BaseViewModel<IRVPUndeliveredNavigator>(dataManager, schedulerProvider, application) {


    //var rvpUndeliveredReasonCodeLists: List<RVPUndeliveredReasonCodeList> = ArrayList()
    private val compositeDisposable = CompositeDisposable()
    private val showErrorMessage = MutableLiveData<Pair<String, Boolean>>()
    val showErrorMessageLiveData: LiveData<Pair<String, Boolean>> get() = showErrorMessage




    private var awb: Long = 0  // Declare AWB variable

    // Method to set the AWB number
    fun setAwbNumber(awbNumber: Long) {
        awb = awbNumber
    }


    @SuppressLint("CheckResult")
    private fun uploadRvpShipment(rvpCommit: RvpCommit, compositeKey: String) {
        val compositeDisposable = CompositeDisposable()
        val tokens = hashMapOf<String, String>().apply {
            put(Constants.TOKEN, getDataManager().getAuthToken())
            put(Constants.EMP_CODE, getDataManager().getCode())
        }

        compositeDisposable.add(
            getDataManager().doRVPUndeliveredCommitApiCall(
                getDataManager().getAuthToken(),
                getDataManager().getEcomRegion(),
                tokens,
                rvpCommit
            )
                .subscribeOn(getSchedulerProvider().io())
                .subscribe({ rvpCommitResponse ->
                    if (rvpCommitResponse.status) {
                        val shipmentStatus = if (rvpCommitResponse.response.shipment_status.equals(Constants.RVPUNDELIVERED, ignoreCase = true)) {
                            Constants.SHIPMENT_UNDELIVERED_STATUS
                        } else {
                            Constants.SHIPMENT_DELIVERED_STATUS
                        }

                        val responseCompositeKey = try {
                            rvpCommitResponse.response.drs_no + rvpCommitResponse.response.awb_no
                        } catch (e: Exception) {

                        }

                        getDataManager().updateRvpStatus(responseCompositeKey as String?, shipmentStatus)
                            .subscribe({
                                updateSyncStatusInDRSRVpTable(rvpCommitResponse.response.drs_no + rvpCommitResponse.response.awb_no)
                                // Setting call preference after sync
                                getDataManager().setCallClicked(rvpCommitResponse.response.awb_no + "RVPCall", true)
                                compositeDisposable.add(
                                    getDataManager().deleteSyncedImage(rvpCommitResponse.response.awb_no)
                                        .subscribe()
                                )
                            }, { throwable ->
                                saveCommit(rvpCommit, compositeKey)
                                setIsLoading(false)
                            })
                    }
                }, { throwable ->

                    saveCommit(rvpCommit, compositeKey)
                    setIsLoading(false)
                })
        )
    }

    private fun updateSyncStatusInDRSRVpTable(compositeKey: String) {
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(
            getDataManager().updateSyncStatusRVP(compositeKey, 2)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().io())
                .subscribe({
                    getNavigator().onSubmitSuccess()
                }, { throwable ->
                    // Handle the error if necessary
                })
        )
    }

    private fun saveCommit(rvpCommit: RvpCommit, compositeKey: String) {
        val pushApi = PushApi().apply {
            awbNo = rvpCommit.awb.toLongOrNull() ?: 0L
            this.compositeKey = compositeKey
            authtoken = getDataManager().getAuthToken()

            try {
                requestData = ObjectMapper().writeValueAsString(rvpCommit)
                shipmentStatus = 0
                shipmentCaterogy = Constants.RVPCOMMIT
            } catch (e: Exception) {
                getNavigator().showError(e.message.orEmpty())
            }
        }

        try {
            getCompositeDisposable().add(
                getDataManager().saveCommitPacket(pushApi)
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe({
                        updateShipmentStatus(pushApi.compositeKey.toString())
                        updateRVPCallAttemptedWithZero(pushApi.awbNo.toString())
                        if (getIsCallAttempted(rvpCommit.awb) == 0) {
                            Constants.shipment_undelivered_count++
                        }
                    }, { ex ->

                         getNavigator().showError(ex.message.orEmpty())
                    })
            )
        } catch (ex: Exception) {

            getNavigator().showError(ex.message.orEmpty())
        }
    }

    private fun updateShipmentStatus(compositeKey: String) {
        try {
            getCompositeDisposable().add(
                getDataManager().updateRvpStatus(compositeKey, 3)
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe({
                        getNavigator().onSubmitSuccess()
                    }, { ex ->
                        getNavigator().showError(ex.message.orEmpty())
                    })
            )
        } catch (ex: Exception) {

            getNavigator().showError(ex.message.orEmpty())
        }
    }

    fun updateRVPCallAttemptedWithZero(awb: String) {
        try {
            getCompositeDisposable().add(
                getDataManager().updateRVPCallAttempted(awb.toLongOrNull() ?: 0L, 0)
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe({}, { ex ->

                        getNavigator().showError(ex.message.orEmpty())
                    })
            )
        } catch (ex: Exception) {

            getNavigator().showError(ex.message.orEmpty())
        }
    }


    fun getIsCallAttempted(awb: String): Int {
        var isCall = 0
        try {
            getCompositeDisposable().add(
                getDataManager().getisRVPCallattempted(awb.toLongOrNull() ?: 0L)
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe({ isCallAttempted ->
                        try {
                            isCall = isCallAttempted.toString().toInt()
                            getNavigator().isCallAttempted(isCall)
                        } catch (e: Exception) {

                        }
                    }, { throwable ->

                    })
            )
        } catch (ex: Exception) {

            getNavigator().showError(ex.message.orEmpty())
        }
        return isCall
    }




    fun showMessage(message: String, isError: Boolean) {
        showErrorMessage.value = Pair(message, isError)
    }



    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}