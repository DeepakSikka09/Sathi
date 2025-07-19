package in.ecomexpress.sathi.ui.drs.forward.success;


import android.app.Application;
import android.util.Log;

import androidx.databinding.ObservableField;

import java.util.Calendar;

import cz.msebera.android.httpclient.HttpConnection;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

public class FWDSuccessViewModel extends BaseViewModel<IFWDSuccessNavigator> {
    public static final String TAG = HttpConnection.class.getSimpleName();
    private final ObservableField<Boolean> textColor = new ObservableField<>();
    private final ObservableField<Boolean> image = new ObservableField<>();
    ForwardCommit forwardCommit;
    ObservableField<String> name = new ObservableField<>();
    ObservableField<String> address = new ObservableField<>();
    ObservableField<String> item_name = new ObservableField<>();
    private DRSForwardTypeResponse mDrsForwardTypeResponse;


    public FWDSuccessViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);
    }

    public ObservableField<Boolean> getImage() {
        return image;
    }

    public void setImage(Boolean image) {
        this.image.set(image);
    }

    public ObservableField<Boolean> getTextColor() {
        return textColor;
    }

    public void setTextColor(Boolean image) {
        this.textColor.set(image);
    }

    public ObservableField<String> getName() {
        return name;
    }

    public void setName(ObservableField<String> name) {
        this.name = name;
    }

    public ObservableField<String> getAddress() {
        return address;
    }

    public void setAddress(ObservableField<String> address) {
        this.address = address;
    }

    public ObservableField<String> getItem_name() {
        return item_name;
    }

    public void setItem_name(ObservableField<String> item_name) {
        this.item_name = item_name;
    }

    public void OnHomeClick() {

        home_click();

    }

    public void setSuccessData(DRSForwardTypeResponse drsForwardTypeResponse) {
        try {

            String address1 = drsForwardTypeResponse.getConsigneeDetails().getAddress().getLine1()
                    + " " + drsForwardTypeResponse.getConsigneeDetails().getAddress().getLine2()
                    + " " + drsForwardTypeResponse.getConsigneeDetails().getAddress().getLine3()
                    + " " + drsForwardTypeResponse.getConsigneeDetails().getAddress().getLine4()
                    + " " + drsForwardTypeResponse.getConsigneeDetails().getAddress().getCity();
            String finaladdress = address1.replaceAll("null", "");
            name.set(drsForwardTypeResponse.getConsigneeDetails().getName());
            item_name.set(drsForwardTypeResponse.getShipmentDetails().getItem());
            address.set(finaladdress);

        } catch (Exception e) {
            Logger.e(FWDSuccessViewModel.class.getName(), e.getMessage());
            getNavigator().onError(e.getMessage());
        }
    }

    //FETCHING DATA FROM LIST ITEM

    /**
     *
     * @param nForwardCommit-- forward commit data
     * @param composite_key--composite key
     */
    public void getShipmentData(ForwardCommit nForwardCommit, String composite_key) {
        try {
            this.forwardCommit = nForwardCommit;
            getCompositeDisposable().add(getDataManager().getForwardDRS(composite_key).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(drsForwardTypeResponse -> {
                Log.d(TAG, "accept: " + drsForwardTypeResponse.getFlags().toString());
                if (drsForwardTypeResponse != null) {
                    mDrsForwardTypeResponse = drsForwardTypeResponse;
                    setSuccessData(drsForwardTypeResponse);

                }
            }));
        } catch (Exception e) {
            Logger.e(FWDSuccessViewModel.class.getName(), e.getMessage());
            getNavigator().onError(e.getMessage());
        }
    }

    public void home_click() {
        getNavigator().onhomeclick();
    }


    public void fetchForwardShipment(String awbNo) {
        try {
            getCompositeDisposable().add(getDataManager().getForwardDRSCompositeKey(Long.valueOf(awbNo)).
                    subscribeOn(getSchedulerProvider().io()).
                    observeOn(getSchedulerProvider().ui()).subscribe(drsForwardTypeResponse -> getNavigator().showEarnedDialog(drsForwardTypeResponse), throwable -> {
                        writeErrors(Calendar.getInstance().getTimeInMillis(), new Exception(throwable));
                        throwable.printStackTrace();
                    }));
        } catch (Exception e) {
            Logger.e(FWDSuccessViewModel.class.getName(), e.getMessage());
            getNavigator().onError(e.getMessage());
        }
    }
}
