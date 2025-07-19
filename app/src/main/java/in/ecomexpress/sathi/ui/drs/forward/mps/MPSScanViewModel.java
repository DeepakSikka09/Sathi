package in.ecomexpress.sathi.ui.drs.forward.mps;

import android.app.Application;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;



@HiltViewModel
public class MPSScanViewModel extends BaseViewModel<MPSScanNavigator> {

    private DRSForwardTypeResponse mDrsForwardTypeResponse;
    private ForwardCommit forwardCommit;

    @Inject
    public MPSScanViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider , Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }


    public void onProceed() {
        getNavigator().onProceed();
    }

    public void onCancel() {
        try {
        forwardCommit.setAttempt_reason_code(Constants.FORWARD_COMMIT_REASON_CODE);
        forwardCommit.setDeclared_value(mDrsForwardTypeResponse.getShipmentDetails().getDeclaredValue().toString());
        getNavigator().unDelivered(forwardCommit);

        }catch (Exception e){
            e.printStackTrace();
            getNavigator().onErrorMsg(e.getMessage());
        }
    }

    //FETCHING DATA FROM LIST ITEM

    /**
     *
     * @param nForwardCommit-- fwd commit data
     * @param composite_key-- composite key
     * @param awbNo-- awb no.
     */
    public void getShipmentData(ForwardCommit nForwardCommit, String composite_key,long awbNo) {
        this.forwardCommit = nForwardCommit;
        try {
        getCompositeDisposable().add(getDataManager().getForwardDRS(composite_key).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(drsForwardTypeResponse -> {
            if (drsForwardTypeResponse != null) {
                mDrsForwardTypeResponse = drsForwardTypeResponse;
                setForwardCommitValue(awbNo);
            }
        }));
    }catch (Exception e){
        e.printStackTrace();
        getNavigator().onErrorMsg(e.getMessage());
    }
    }

   /* public void onBackClick() {
        getNavigator().onBack();
    }*/

    private void setForwardCommitValue(long awbNo) {
        try {
        forwardCommit.setDrs_date(String.valueOf(mDrsForwardTypeResponse.getAssignedDate()));
        forwardCommit.setAwb(String.valueOf(awbNo));
        forwardCommit.setDeclared_value(String.valueOf(mDrsForwardTypeResponse.getShipmentDetails().getDeclaredValue()));
        forwardCommit.setDrs_id(mDrsForwardTypeResponse.getDrsId() + "");
        forwardCommit.setLocation_lat(String.valueOf(getDataManager().getCurrentLatitude()));
        forwardCommit.setLocation_long(String.valueOf(getDataManager().getCurrentLongitude()));
        forwardCommit.setConsignee_name(mDrsForwardTypeResponse.getConsigneeDetails().getName());
        forwardCommit.setShipment_type(Constants.SHIPMENT_TYPE_FORWARD);
        forwardCommit.setAttempt_reason_code(Constants.FORWARD_COMMIT_REASON_CODE);
        forwardCommit.setDeclared_value(mDrsForwardTypeResponse.getShipmentDetails().getDeclaredValue().toString());
    }catch (Exception e){
        e.printStackTrace();
        getNavigator().onErrorMsg(e.getMessage());
    }
    }

}
