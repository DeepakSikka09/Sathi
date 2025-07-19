package in.ecomexpress.sathi.ui.drs.rts.rts_success;

import android.app.Application;
import androidx.databinding.ObservableField;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.data.rts.RTSCommit;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.Details;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class RTSSuccessViewModel extends BaseViewModel<IRTSSuccessNavigator> {

    private RTSCommit rtsCommit;
    public Details details;
    private final ObservableField<Boolean> textColor = new ObservableField<>();
    private final ObservableField<Boolean> image = new ObservableField<>();
    ObservableField<String> vendor_name_tv = new ObservableField<>("");

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
    public RTSSuccessViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider , Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }

    public void getShipmentData(RTSCommit rtsCommit, long id) {
        this.rtsCommit = rtsCommit;
        try {
            getCompositeDisposable().add(getDataManager().getVWDetails(id).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(details -> {
                if (details != null) {
                    setRtsCommit(details);
                }
            }));
        } catch (Exception e){
            getNavigator().showError(e.getMessage());
        }
    }

    public void setRtsCommit(Details details) {
        try {
            vendor_name_tv.set(details.getName());
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
        }
    }

    public String getID() {
        return this.rtsCommit.getConsigneeId();
    }
}