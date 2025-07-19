package in.ecomexpress.sathi.ui.dummy.eds.paytm;

import android.app.Application;
import android.util.Log;

import androidx.databinding.ObservableField;

import java.util.Calendar;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class PaytmViewModel extends BaseViewModel<IPaytmFragmentNavigator> {
    public final ObservableField<String> inputData = new ObservableField<>("");
    public ObservableField<MasterActivityData> masterActivityData = new ObservableField<>();
    public ObservableField<EDSActivityWizard> edsActivityWizard = new ObservableField<>();
    public ObservableField<String> activityName = new ObservableField<>("ActivityName");
    public ObservableField<String> activityQuestion = new ObservableField<>("");
    public ObservableField<String> imageCaptureSetting = new ObservableField<>("Image ( Optional )");
    public ObservableField<String> instructions = new ObservableField<>("Instruction");

    @Inject
    public PaytmViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);
    }

    public void onVodafoneConnect() {
        getNavigator().onVodaConnect();
    }

    public void setOrderNo(String orderNo) {
        getDataManager().setVodaOrderNo(orderNo);
    }

    public ObservableField<String> getActivityName() {
        try {
            if (masterActivityData.get() != null && masterActivityData.get().getActivityName() != null)
                activityName.set(masterActivityData.get().getActivityName());

            return activityName;
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
        return activityName;
    }

    public void setData(EDSActivityWizard edsActivityWizard, MasterActivityData masterActivityData) {
        this.edsActivityWizard.set(edsActivityWizard);
        this.masterActivityData.set(masterActivityData);
        getActivityName();

        getInstruction();

    }

    public void getmobile_no(long awb) {
        try {
            final long timeStamp = Calendar.getInstance().getTimeInMillis();
            getCompositeDisposable().add(getDataManager()
                    .doencryptcontact(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), awb)
                    .doOnSuccess(contatDecryption -> {
                        writeRestAPIResponse(timeStamp, contatDecryption);
                        Log.d("ICICIM", String.valueOf(contatDecryption.isStatus()));
                        // hideLoader();
                    })
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(encryptContactResponse -> {
                        Log.d("ICICIM", String.valueOf(encryptContactResponse.isStatus()));
                        getNavigator().getMobile(encryptContactResponse);

                    }, throwable -> {
                        setIsLoading(false);
                        Log.d("ICICI", "ERRRRRRRR");
                        String error, myerror;


                    }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public ObservableField<String> getInstruction() {
        try {
            if ((masterActivityData.get() != null)) {
                instructions.set(edsActivityWizard.get().getCustomerRemarks() + "\n");
            }
            return instructions;
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
        return instructions;
    }
}


