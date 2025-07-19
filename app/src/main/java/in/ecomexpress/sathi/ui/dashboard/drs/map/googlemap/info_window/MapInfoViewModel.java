package in.ecomexpress.sathi.ui.dashboard.drs.map.googlemap.info_window;

import android.app.Application;
import androidx.databinding.ObservableField;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class MapInfoViewModel extends BaseViewModel<IMapInfoNavigator> {

    private final ObservableField<String> consigneeName = new ObservableField<>();
    private final ObservableField<String> shipmentType = new ObservableField<>();
    private final ObservableField<String> address = new ObservableField<>();

    @Inject
    public MapInfoViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }

    public String getName() {
        return consigneeName.get();
    }

    public String getShipmentType() {
        return shipmentType.get();
    }

    public String getAddress() {
        return address.get();
    }
}