package `in`.ecomexpress.sathi.ui.drs.rvp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.ecomexpress.sathi.repo.IDataManager
import `in`.ecomexpress.sathi.repo.local.db.model.RVPUndeliveredReasonCodeList
import `in`.ecomexpress.sathi.ui.base.BaseViewModel
import `in`.ecomexpress.sathi.ui.drs.rvp.navigator.RvpQcNavigator
import `in`.ecomexpress.sathi.utils.rx.ISchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class CancelReasonCodeListViewModel @Inject constructor(
    dataManager: IDataManager,
    schedulerProvider: ISchedulerProvider,
    application: Application
) : BaseViewModel<RvpQcNavigator>(dataManager, schedulerProvider, application) {


    var rvpUndeliveredReasonCodeLists: List<RVPUndeliveredReasonCodeList> = ArrayList()
    private val compositeDisposable = CompositeDisposable()
    private val showErrorMessage = MutableLiveData<Pair<String, Boolean>>()
    val showErrorMessageLiveData: LiveData<Pair<String, Boolean>> get() = showErrorMessage

    private val _subGroupData = MutableLiveData<List<String>>()
    val subGroupData: LiveData<List<String>> get() = _subGroupData


    fun showMessage(message: String, isError: Boolean) {
        showErrorMessage.value = Pair(message, isError)
    }

    fun getSubgroupFromRvpReasonCode() {
        try {
            getCompositeDisposable().add(
                dataManager.subgroupFromRvpReasonCode
                    .observeOn(schedulerProvider.ui())
                    .subscribeOn(schedulerProvider.io())
                    .subscribe({ result ->
                        _subGroupData.postValue(result) // Update LiveData
                    }, { error ->
                        // Handle error if necessary
                        Log.e("Error", "Failed to load data", error)
                    })
            )
        } catch (e: Exception) {
            Log.e("Error", "Exception occurred", e)
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}