package in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_rbl;

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

/**
 * Created by santosh on 17/1/20.
 */

@HiltViewModel
public class EdsRblViewModel extends BaseViewModel<IEdsRblFragmentNavigator> {
//    public ObservableField<MasterActivityData> masterActivityData = new ObservableField<>();
//    public ObservableField<EDSActivityWizard> edsActivityWizard = new ObservableField<>();
//    public final ObservableField<String> inputData = new ObservableField<>("");

    public ObservableField<MasterActivityData> masterActivityData = new ObservableField<>();
    public ObservableField<EDSActivityWizard> edsActivityWizard = new ObservableField<>();
    public ObservableField<String> activityName = new ObservableField<>("ActivityName");
    public ObservableField<String> instructions = new ObservableField<>("Instruction");
    public ObservableField<String> activityQuestion = new ObservableField<>("");

    @Inject
    public EdsRblViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider , Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }
//    public void setData(EDSActivityWizard edsActivityWizard, MasterActivityData masterActivityData) {
//        this.edsActivityWizard.set(edsActivityWizard);
//        this.masterActivityData.set(masterActivityData);
//    }

//    public void validateurn() {
//        getNavigator().validateurn();
//    }
//
//    public void validateAdhaar() {
//        getNavigator().validateAdhaar();
//    }
    public void ongetPid() {
        getNavigator().ongetPid();
    }

//}

    public ObservableField<String> getActivityName() {
        if (masterActivityData.get() != null && masterActivityData.get().getActivityName() != null)
            activityName.set(masterActivityData.get().getActivityName());

        return activityName;
    }

    public void setData(EDSActivityWizard edsActivityWizard, MasterActivityData masterActivityData) {
        this.edsActivityWizard.set(edsActivityWizard);
        this.masterActivityData.set(masterActivityData);
        getActivityName();

        getInstruction();

    }

    public ObservableField<String> getActivityQuestion() {
        if (masterActivityData.get() != null && masterActivityData.get().getActivityName() != null)
            activityQuestion.set(masterActivityData.get().getActivityQuestion());

        return activityQuestion;
    }

    public ObservableField<String> getInstruction() {
        if ((masterActivityData.get() != null)) {
            instructions.set(edsActivityWizard.get().getCustomerRemarks() + "\n" /*+ masterActivityData.get().getInstructions()==null ? "" :masterActivityData.get().getInstructions()*/);
        }
        return instructions;
    }
}