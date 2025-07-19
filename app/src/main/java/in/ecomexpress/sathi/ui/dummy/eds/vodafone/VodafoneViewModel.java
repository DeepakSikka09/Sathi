package in.ecomexpress.sathi.ui.dummy.eds.vodafone;


import android.app.Application;

import androidx.databinding.ObservableField;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class VodafoneViewModel extends BaseViewModel<IVodafoneFragmentNavigator> {
    public final ObservableField<String> inputData = new ObservableField<>("");
    public ObservableField<MasterActivityData> masterActivityData = new ObservableField<>();
    public ObservableField<EDSActivityWizard> edsActivityWizard = new ObservableField<>();
    public ObservableField<String> activityName = new ObservableField<>("ActivityName");
    public ObservableField<String> activityQuestion = new ObservableField<>("");
    public ObservableField<String> imageCaptureSetting = new ObservableField<>("Image ( Optional )");
    public ObservableField<String> instructions = new ObservableField<>("Instruction");

    @Inject
    public VodafoneViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider , Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }

    public void onVodafoneConnect() {
        getNavigator().onVodaConnect();
    }

    public void setOrderNo(String orderNo) {
        getDataManager().setVodaOrderNo(orderNo);
    }

    public String getOrderNoFromSharedPreferences() {
        return getDataManager().getVodaOrderNo();
    }

    public ObservableField<String> getActivityName() {
        try {
            if (masterActivityData.get() != null && masterActivityData.get().getActivityName() != null)
                activityName.set(masterActivityData.get().getActivityName());

            return activityName;
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
        return activityName;
    }

    public void setData(EDSActivityWizard edsActivityWizard, MasterActivityData masterActivityData) {
        this.edsActivityWizard.set(edsActivityWizard);
        this.masterActivityData.set(masterActivityData);
        getActivityName();

        getInstruction();

    }

    public void onScan() {
        getNavigator().onscan();
    }

    public ObservableField<String> getInstruction() {
        try {
            if ((masterActivityData.get() != null)) {
                instructions.set(edsActivityWizard.get().getCustomerRemarks() + "\n" /*+ masterActivityData.get().getInstructions()==null ? "" :masterActivityData.get().getInstructions()*/);
            }
            return instructions;
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
        return instructions;
    }
}
