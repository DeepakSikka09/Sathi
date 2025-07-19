package in.ecomexpress.sathi.ui.dummy.eds.ekyc_paytm;

import android.app.Application;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class EdsEkycPaytmViewModel extends BaseViewModel<IEdsEkycPaytmFragmentNavigator> {

    @Inject
    public EdsEkycPaytmViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider , Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }
}
