package in.ecomexpress.sathi.ui.dashboard.drs.map.googlemap;

import android.app.Application;
import androidx.databinding.ObservableField;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.db.model.CommonDRSListItem;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.remote.model.drs_list.common.Location;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.IRTSBaseInterface;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.Observable;

@HiltViewModel
public class GViewModel extends BaseViewModel<IGFragmentNavigator> {

    private static final String TAG = GBaseFragment.class.getSimpleName();
    public ObservableField<EdsWithActivityList> edsWithActivityList = new ObservableField<>();

    @Inject
    public GViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application app) {
        super(dataManager, schedulerProvider,app);
    }

    public double getCurrentLatitude() {
        try {
            return getDataManager().getCurrentLatitude();
        } catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
        return 00.00;
    }

    public double getCurrentLongitude() {
        try {
            return getDataManager().getCurrentLongitude();
        } catch (Exception e){
            getNavigator().showError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
        return 00.00;
    }

    public double getDCLongitude() {
        return getDataManager().getDCLongitude();
    }

    public double getDCLatitude() {
       return getDataManager().getDCLatitude();
    }

    public void createWayPoints(List<CommonDRSListItem> listItemList) {
        setIsLoading(true);
        try {
        Observable.fromCallable(() -> {
            setIsLoading(false);
            List<LatLng> shipmentPoints = new ArrayList<>();
            try {
                for (CommonDRSListItem commonDRSListItem : listItemList) {
                    switch (commonDRSListItem.getType()) {
                        case GlobalConstant.ShipmentTypeConstants.FWD:
                            try {
                                DRSForwardTypeResponse response = commonDRSListItem.getDrsForwardTypeResponse();
                                Location location = response.getConsigneeDetails().getAddress().getLocation();
                                if (location.getLat() != 0.0 && location.getLng() != 0.0) {
                                    shipmentPoints.add(new LatLng(location.getLat(), location.getLng()));
                                }
                            } catch (Exception e) {
                                Logger.e(TAG, String.valueOf(e));
                            }
                            break;
                        case GlobalConstant.ShipmentTypeConstants.RVP:
                            try {
                                DRSReverseQCTypeResponse response = commonDRSListItem.getDrsReverseQCTypeResponse();
                                Location location = response.getConsigneeDetails().getAddress().getLocation();
                                if (location.getLat() != 0.0 && location.getLng() != 0.0) {
                                    shipmentPoints.add(new LatLng(location.getLat(), location.getLng()));
                                }
                            } catch (Exception e) {
                                Logger.e(TAG, String.valueOf(e));
                            }
                            break;
                        case GlobalConstant.ShipmentTypeConstants.RTS:
                            try {
                                IRTSBaseInterface irtsBaseInterface = commonDRSListItem.getIRTSInterface();
                                in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.Location location = irtsBaseInterface.getDetails().getLocation();
                                double lat = location.getLat();
                                double lng = location.getLong();
                                if (lat != 0.0 && lng != 0.0) {
                                    shipmentPoints.add(new LatLng(lat, lng));
                                }
                            } catch (Exception e) {
                                Logger.e(TAG, String.valueOf(e));
                            }
                            break;
                        case GlobalConstant.ShipmentTypeConstants.EDS:
                            try {
                                EDSResponse edsResponse = commonDRSListItem.getEdsResponse();
                                double latitude = edsResponse.getConsigneeDetail().getAddress().getLocation().getLat();
                                double longitude = edsResponse.getConsigneeDetail().getAddress().getLocation().getLng();
                                if (latitude != 0.0 && longitude != 0.0) {
                                    shipmentPoints.add(new LatLng(latitude, longitude));
                                }
                            } catch (Exception e) {
                                Logger.e(TAG, String.valueOf(e));
                            }
                            break;
                    }
                }
            } catch (Exception e) {
                getNavigator().showError(e.getMessage());
                Logger.e(TAG, String.valueOf(e));
            }
            return shipmentPoints;
        }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(latLngs -> getNavigator().onWayPointReady(latLngs), throwable -> Logger.e(TAG, String.valueOf(throwable)));
        } catch (Exception e){
            getNavigator().showError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }

    }

    public void logout() {
        logoutLocal();
    }

    private void logoutLocal() {
        try {
            getDataManager().setTripId("");
            getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT);
            clearAppData();
        } catch (Exception e){
            getNavigator().showError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void clearAppData() {
        getCompositeDisposable().add(getDataManager()
            .deleteAllTables().subscribeOn
                    (getSchedulerProvider().io()).
                    observeOn(getSchedulerProvider().ui())
            .subscribe(aBoolean -> {
                try {
                    getDataManager().clearPrefrence();
                    getDataManager().setUserAsLoggedOut();
                } catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                }
                getNavigator().clearStack();
        }));
    }

    public void getEdsListTask(String composite_key) {
        try {
            getCompositeDisposable().add(getDataManager().
                getEdsWithActivityList(composite_key).
                subscribeOn(getSchedulerProvider().io()).
                observeOn(getSchedulerProvider().ui()).
                subscribe(edsWithActivityList -> {
                    GViewModel.this.edsWithActivityList.set(edsWithActivityList);
                    for (int i = 0; i < Objects.requireNonNull(GViewModel.this.edsWithActivityList.get()).getEdsActivityWizards().size(); i++) {
                        if (Objects.requireNonNull(GViewModel.this.edsWithActivityList.get()).getEdsActivityWizards().get(i).code.endsWith("CPV")) {
                            Constants.IS_CPV_ACTIVITY_EXITS = true;
                            break;
                        } else {
                            Constants.IS_CPV_ACTIVITY_EXITS = false;
                        }
                    }
            }));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }
}