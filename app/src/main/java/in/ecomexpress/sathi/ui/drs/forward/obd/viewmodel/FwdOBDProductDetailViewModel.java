package in.ecomexpress.sathi.ui.drs.forward.obd.viewmodel;

import android.app.Application;
import androidx.databinding.ObservableBoolean;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.SecureDelivery;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.ui.drs.forward.obd.navigator.IOBDProductDetailNavigator;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class FwdOBDProductDetailViewModel extends BaseViewModel<IOBDProductDetailNavigator> {

    @Inject
    public FwdOBDProductDetailViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application application) {
        super(dataManager, schedulerProvider, application);
    }
    SecureDelivery isSecureDelivery;
    public ObservableBoolean isSecureOtp = new ObservableBoolean(false);
    private ForwardCommit forwardCommit;
    public DRSForwardTypeResponse mDrsForwardTypeResponse;

    //FETCHING DATA FROM LIST ITEM
    public void getFWDShipmentData(ForwardCommit nForwardCommit, String composite_key) {
        this.forwardCommit = nForwardCommit;
        try {
            getCompositeDisposable().add(getDataManager().getForwardDRS(composite_key).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(drsForwardTypeResponse -> {
                try {
                    if (drsForwardTypeResponse != null) {
                        mDrsForwardTypeResponse = drsForwardTypeResponse;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    getNavigator().onHandleError(e.getMessage());
                }
            }, throwable -> getNavigator().onHandleError(throwable.getMessage())));

        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onHandleError(e.getMessage());
        }
    }

    public void getSecureDelivery(SecureDelivery secureDelivery) {
        try {
            if (secureDelivery != null) {
                isSecureDelivery = secureDelivery;
                isSecureOtp.set(isSecureDelivery.getOTP());
            }
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onHandleError(e.getMessage());
        }
    }

    public String getCollectableValue() {
        return mDrsForwardTypeResponse.getShipmentDetails().getCollectableValue().toString();
    }

    public void onUndeliveredClick() {
        try {
            forwardCommit.setAttempt_reason_code(Constants.FORWARD_COMMIT_REASON_CODE);
            forwardCommit.setDrs_date(String.valueOf(mDrsForwardTypeResponse.getAssignedDate()));
            forwardCommit.setAwb(mDrsForwardTypeResponse.getAwbNo().toString());
            forwardCommit.setDeclared_value(String.valueOf(mDrsForwardTypeResponse.getShipmentDetails().getDeclaredValue()));
            forwardCommit.setDrs_id(mDrsForwardTypeResponse.getDrsId() + "");
            forwardCommit.setLocation_lat(String.valueOf(getDataManager().getCurrentLatitude()));
            forwardCommit.setLocation_long(String.valueOf(getDataManager().getCurrentLongitude()));
            forwardCommit.setConsignee_name(mDrsForwardTypeResponse.getConsigneeDetails().getName());
            forwardCommit.setShipment_type(Constants.SHIPMENT_TYPE_FORWARD);
            forwardCommit.setAttempt_reason_code(Constants.FORWARD_COMMIT_REASON_CODE);
            getNavigator().onUndelivered(forwardCommit);
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onHandleError(e.getMessage());
        }
    }
}