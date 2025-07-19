package in.ecomexpress.sathi.ui.drs.forward.obd.viewmodel;

import android.app.Application;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.ui.drs.forward.obd.navigator.IFwdObdQCFailNavigator;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class FwdOBDQcFailViewModel extends BaseViewModel<IFwdObdQCFailNavigator> {

    @Inject
    public FwdOBDQcFailViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application application) {
        super(dataManager, schedulerProvider, application);
    }
}