package in.ecomexpress.sathi.ui.drs.forward.fill_awb;

import android.app.Application;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class AwbPopupDialogViewModel extends BaseViewModel<IAwbDialogNavigator> {

    @Inject
    public AwbPopupDialogViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider , Application sathiApplication) {
        super(dataManager, schedulerProvider , sathiApplication);
    }

    public void onCancelClick() {
        getNavigator().dismissDialog();
    }

    public void onSubmitClick() {
        getNavigator().onSubmitClick();
    }
}