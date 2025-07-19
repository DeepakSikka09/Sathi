package in.ecomexpress.sathi.ui.dummy.eds.eds_hdfc_masking;


import android.app.Application;

import androidx.databinding.ObservableField;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;


@HiltViewModel
public class HDFCMaskingFragmentViewModel extends BaseViewModel<HDFCMaskingFragmentNavigation> {

    public ObservableField<EDSActivityWizard> edsActivityWizard = new ObservableField<>();
    public ObservableField<MasterActivityData> masterActivityData = new ObservableField<>();
    public ObservableField<String> activityName = new ObservableField<>("ActivityName Not Defined");

    @Inject
    public HDFCMaskingFragmentViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);
    }


    public ObservableField<String> getActivityName() {
        try {
            if (edsActivityWizard.get() != null && masterActivityData.get() != null) {
                activityName.set(masterActivityData.get().getActivityName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activityName;
    }


    public void captureFrontImage() {
        getNavigator().captureFrontImage();
    }

    public void captureRearImage() {
        getNavigator().captureRearImage();
    }

}
