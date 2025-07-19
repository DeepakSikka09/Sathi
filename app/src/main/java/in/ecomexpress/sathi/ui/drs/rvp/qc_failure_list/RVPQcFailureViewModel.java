package in.ecomexpress.sathi.ui.drs.rvp.qc_failure_list;

import android.app.Application;

import androidx.databinding.ObservableField;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.data.rvp.RvpCommit;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

/**
 * Created by parikshittomar on 20-08-2018.
 */
@HiltViewModel
public class RVPQcFailureViewModel extends BaseViewModel<IRVPQcFailureNavigator> {


    private final ObservableField<String> failedQC = new ObservableField<>("");
    private final ObservableField<String> consignee = new ObservableField<>("");
    private final ObservableField<String> address = new ObservableField<>("");
    private final ObservableField<String> item = new ObservableField<>("");
    public final ObservableField<String> awbNo = new ObservableField<>("");

    @Inject
    public RVPQcFailureViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);
    }

    public void onBackClick() {
        getNavigator().onBack();
    }

    public ObservableField<String> getConsigneeName() {
        return consignee;
    }

    public ObservableField<String> getFailedQC() {
        return failedQC;
    }

    public ObservableField<String> getAwbNo() {
        return awbNo;
    }

    public ObservableField<String> getAddress() {
        return address;
    }

    public ObservableField<String> getItem() {
        return item;
    }

    public void getAllQcFailList(List<RvpCommit.QcWizard> qcWizards) {
        getNavigator().onSetAdapter(qcWizards);
    }

    public void setData(DRSReverseQCTypeResponse data) {
        try {
            awbNo.set(data.getAwbNo().toString());
            consignee.set(data.getConsigneeDetails().getName());
            address.set(data.getConsigneeDetails().getAddress().getLine1() + " "
                    + data.getConsigneeDetails().getAddress().getLine2() + " " +
                    data.getConsigneeDetails().getAddress().getLine3() + " " +
                    data.getConsigneeDetails().getAddress().getLine4() + " " +
                    data.getConsigneeDetails().getAddress().getCity()
            );
            item.set(data.getShipmentDetails().getItem());

        } catch (Exception e) {
             Logger.e(RVPQcFailureViewModel.class.getName(), e.getMessage());
        }
    }

    public void onNextClick() {
        getNavigator().onNext();
    }
}
