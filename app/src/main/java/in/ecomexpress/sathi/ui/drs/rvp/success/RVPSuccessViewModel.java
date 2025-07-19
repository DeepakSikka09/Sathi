package in.ecomexpress.sathi.ui.drs.rvp.success;


import android.app.Application;

import androidx.databinding.ObservableField;

import java.util.Calendar;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class RVPSuccessViewModel extends BaseViewModel<IRVPSuccessNavigator> {

    private final ObservableField<Boolean> textColor = new ObservableField<>();
    private final ObservableField<Boolean> image = new ObservableField<>();

    public ObservableField<Boolean> getImage() {
        return image;
    }

    public void setImage(Boolean image) {
        this.image.set(image);
    }

    public void setTextColor(Boolean image) {
        this.textColor.set(image);
    }


    public ObservableField<Boolean> getTextColor() {
        return textColor;
    }

    @Inject
    public RVPSuccessViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);
    }

    public void OnHomeClick() {
        getNavigator().onHomeClick();
    }

    public void fetchRVPShipment(String awbNo) {
        try {
            getCompositeDisposable().add(getDataManager().getTypeOfShipment(awbNo).
                    subscribeOn(getSchedulerProvider().io()).
                    observeOn(getSchedulerProvider().ui()).subscribe(String -> getNavigator().showEarnedDialog(String), throwable -> {
                        writeErrors(Calendar.getInstance().getTimeInMillis(), new Exception(throwable));
                        Logger.e(RVPSuccessViewModel.class.getName(), throwable.getMessage());
                    }));
        } catch (Exception e) {
            Logger.e(RVPSuccessViewModel.class.getName(), e.getMessage());
            getNavigator().onError(e.getMessage());
        }
    }
}
