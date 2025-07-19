package in.ecomexpress.sathi.ui.dashboard.switchnumber;

import android.app.Application;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.masterdata.CbPstnOptions;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class SwitchNumberViewModel extends BaseViewModel<SwitchNumberCallBack> {

    List<CbPstnOptions> mycbPstnOptions;

    @Inject
    public SwitchNumberViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication){
        super(dataManager, schedulerProvider, sathiApplication);
    }

    public void onSubmitNumber(){
        getNavigator().onSubmitNumber();
    }

    public void onCancelClick(){
        getNavigator().dismissDialog();
    }

    public void getCbPstnOptions(){
        try{
            getCompositeDisposable().add(getDataManager().
                getCbPstnOptions().observeOn(getSchedulerProvider().ui()).
                subscribeOn(getSchedulerProvider().io()).
                subscribe(cbPstnOptions -> {
                    mycbPstnOptions = cbPstnOptions;
                    getNavigator().OnSetFuelAdapter(cbPstnOptions);
            }));
        } catch(Exception e){
            getNavigator().showError(e.getMessage());
        }
    }
}