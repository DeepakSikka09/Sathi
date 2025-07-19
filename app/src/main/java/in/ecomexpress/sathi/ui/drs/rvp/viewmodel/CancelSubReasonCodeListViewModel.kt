package `in`.ecomexpress.sathi.ui.drs.rvp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.ecomexpress.sathi.repo.IDataManager
import `in`.ecomexpress.sathi.repo.local.db.model.RVPUndeliveredReasonCodeList
import `in`.ecomexpress.sathi.repo.remote.model.masterdata.RVPReasonCodeMaster
import `in`.ecomexpress.sathi.ui.base.BaseViewModel
import `in`.ecomexpress.sathi.ui.drs.rvp.navigator.RvpQcNavigator
import `in`.ecomexpress.sathi.utils.rx.ISchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class CancelSubReasonCodeListViewModel @Inject constructor(dataManager: IDataManager, schedulerProvider: ISchedulerProvider, application: Application) : BaseViewModel<RvpQcNavigator>(dataManager, schedulerProvider, application) {


    var rvpUndeliveredReasonCodeLists: List<RVPUndeliveredReasonCodeList> = ArrayList()
    private val compositeDisposable = CompositeDisposable()
    private val showErrorMessage = MutableLiveData<Pair<String, Boolean>>()
    val showErrorMessageLiveData: LiveData<Pair<String, Boolean>> get() = showErrorMessage



    private val _subReasonList = MutableLiveData<List<RVPReasonCodeMaster>>()
    val subReasonList: LiveData<List<RVPReasonCodeMaster>> get() = _subReasonList

    fun getSubReasonCodeFromSubGroup(selectedReason: String) {
        try {
            getCompositeDisposable().add(
                dataManager.getSubReasonCodeFromSubGroup(selectedReason)
                    .observeOn(schedulerProvider.ui())  // Switch to UI thread for post-processing
                    .subscribeOn(schedulerProvider.io()) // Perform the query on IO thread
                    .subscribe({ result ->
                        _subReasonList.postValue(result)  // Update LiveData with the result
                    }, { error ->
                        // Handle error, log the error if necessary
                        Log.e("Error", "Failed to load sub-reason codes", error)
                    })
            )
        } catch (e: Exception) {
            Log.e("Error", "Exception occurred while fetching sub-reason codes", e)
        }
    }





    fun showMessage(message: String, isError: Boolean) {
        showErrorMessage.value = Pair(message, isError)
    }



    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}