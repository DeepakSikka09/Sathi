package in.ecomexpress.sathi.ui.dummy.eds.eds_opv;

import android.app.Application;

import androidx.databinding.ObservableField;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.repo.IDataManager;

import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.GeneralQuestion;

import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.functions.Consumer;


@HiltViewModel
public class OpvFragmentViewModel extends BaseViewModel<IOpvFragmentNavigation> {
    public ObservableField<MasterActivityData> masterActivityData = new ObservableField<>();
    public ObservableField<EDSActivityWizard> edsActivityWizard = new ObservableField<>();

    @Inject
    public OpvFragmentViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider , Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }

    public void getOpvMasterData() {
        try {
        getCompositeDisposable().add(getDataManager()
                .doOpvMasterCall().subscribeOn
                        (getSchedulerProvider().io()).
                        observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<List<GeneralQuestion>>() {
                    @Override
                    public void accept(List<GeneralQuestion> generalQuestions) {
                        getNavigator().shareOPVMasterData(generalQuestions);
                    }
                }));

        }catch (Exception e){e.printStackTrace();
            getNavigator().showError(e.getMessage());}
    }
    public void setData(EDSActivityWizard edsActivityWizard, MasterActivityData masterActivityData) {
        this.edsActivityWizard.set(edsActivityWizard);
        this.masterActivityData.set(masterActivityData);


    }


}
