package in.ecomexpress.sathi.ui.dummy.eds.dummy;


import android.app.Application;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

/**
 * Created by dhananjayk on 25-01-2019.
 */

@HiltViewModel
public class DummyViewModel extends BaseViewModel<IDummyNavigation> {


    @Inject
    public DummyViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider , Application sathiApplication) {
        super(dataManager, schedulerProvider , sathiApplication);
    }

}
