package in.ecomexpress.sathi.ui.dashboard.unattempted_shipments;

import android.app.Application;
import androidx.databinding.ObservableField;
import org.joda.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.IRTSBaseInterface;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.Observable;

@HiltViewModel
public class UnattemptedShipmentViewmodel extends BaseViewModel<IUnattemptedNavigator> {

    @Inject
    public UnattemptedShipmentViewmodel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application application) {
        super(dataManager, schedulerProvider, application);
    }

    private final String TAG = UnattemptedShipmentViewmodel.class.getSimpleName();
    private final ObservableField<List<UnattemptedShipments>> unattemptedShipmentsObserver = new ObservableField<>();
    private final ObservableField<String> itemRTS = new ObservableField<>();
    public ObservableField<String> getItemRTS() {
        return itemRTS;
    }

    public void getAllNewDRS() {
        getCompositeDisposable().add(Observable.zip(getDataManager().getDRSListForward(), getDataManager().getDRSListNewRTS(),
            getDataManager().getDRSListRVP(), getDataManager().getDrsListNewEds(), (drsForwardTypeResponses, drsReturnToShipperTypeNewResponses, drsReverseQCTypeResponses, edsResponses) -> {
                List<UnattemptedShipments> unattemptedShipments = new ArrayList<>();
                for (DRSForwardTypeResponse fwd : drsForwardTypeResponses) {
                    if (fwd.getShipmentStatus() == GlobalConstant.ShipmentStatusConstants.ASSIGNED_INT) {
                        unattemptedShipments.add(new UnattemptedShipments(String.valueOf(fwd.getAwbNo()), fwd.getCompositeKey(), GlobalConstant.ShipmentTypeConstants.FWD, fwd.getAssignedDate()));
                    }
                }
                for (IRTSBaseInterface rts : drsReturnToShipperTypeNewResponses.getCombinedList()) {
                    if (rts.getDetails().getShipmentStatus() == GlobalConstant.ShipmentStatusConstants.ASSIGNED_INT) {
                        unattemptedShipments.add(new UnattemptedShipments(String.valueOf(rts.getShipmentsDetails().get(0).getAwbNo()),"" , GlobalConstant.ShipmentTypeConstants.RTS, rts.getShipmentsDetails(), rts.getDetails().getAssignedDate()) );
                        itemRTS.set("Note : For RTS all shipments of particular Shipper will be marked un-attempted.");
                    }
                }
                for (DRSReverseQCTypeResponse rvp : drsReverseQCTypeResponses) {
                    if (rvp.getShipmentStatus() == GlobalConstant.ShipmentStatusConstants.ASSIGNED_INT) {
                        unattemptedShipments.add(new UnattemptedShipments(String.valueOf(rvp.getAwbNo()), rvp.getShipmentDetails().getType(), rvp.getAssignedDate()));
                    }
                }
                for (EDSResponse eds : edsResponses) {
                    if (eds.getShipmentStatus() == GlobalConstant.ShipmentStatusConstants.ASSIGNED_INT) {
                        unattemptedShipments.add(new UnattemptedShipments(String.valueOf(eds.getAwbNo()), GlobalConstant.ShipmentTypeConstants.EDS, eds.getAssignDate()));
                    }
                }
                unattemptedShipmentsObserver.set(unattemptedShipments);
                return unattemptedShipments;
            }).subscribeOn(getSchedulerProvider().io()).
            observeOn(getSchedulerProvider().ui()).
            subscribe(this::filterUnattemptedShipments, throwable -> Logger.e(TAG, String.valueOf(throwable))));
    }

    private void filterUnattemptedShipments(List<UnattemptedShipments> unattemptedShipments){
        if(getDataManager().getPreviosTripStatus()) {
            ArrayList<UnattemptedShipments> filteredShipmentArray = new ArrayList<>();
            for(int i = 0; i <unattemptedShipments.size() ; i++){
                LocalDate previousDate = new LocalDate(unattemptedShipments.get(i).getAssigned_date());
                LocalDate currentDate = new LocalDate();
                if(!previousDate.equals(currentDate)) {
                    filteredShipmentArray.add(unattemptedShipments.get(i));
                }
            }
            if(!filteredShipmentArray.isEmpty()) {
                getNavigator().updateList(filteredShipmentArray);
            } else {
                getNavigator().navigateToNextActivity(false);
            }
        } else {
            getNavigator().updateList(unattemptedShipments);
        }
    }
}