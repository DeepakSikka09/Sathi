package in.ecomexpress.sathi.ui.dashboard.attendance.custom_dialog;

import android.app.Application;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class CustumDialogViewModel extends BaseViewModel<CustomDialogCallBack> {

    @Inject
    public CustumDialogViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }

    public void onYearDecrease() {
        getNavigator().yearDecrease();
    }

    public void onYearIncrease() {
        getNavigator().yearIncrease();
    }

    public void onBackClick() {
        getNavigator().onBackClick();
    }

    public void onCancelClick() {
        getNavigator().cancelClick();
    }

    public void onOkClick() {
        getNavigator().okClick();
    }
}