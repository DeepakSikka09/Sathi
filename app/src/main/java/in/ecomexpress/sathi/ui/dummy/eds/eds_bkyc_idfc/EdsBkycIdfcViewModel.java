package in.ecomexpress.sathi.ui.dummy.eds.eds_bkyc_idfc;

import androidx.databinding.ObservableField;

import android.app.Application;

import java.util.Calendar;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.EncryptContactResponse;
import in.ecomexpress.sathi.repo.remote.model.GenerateToken;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.functions.Consumer;

@HiltViewModel
public class EdsBkycIdfcViewModel extends BaseViewModel<IEdsBkycIdfcFragmentNavigator> {
    public ObservableField<MasterActivityData> masterActivityData = new ObservableField<>();
    public ObservableField<EDSActivityWizard> edsActivityWizard = new ObservableField<>();

    @Inject
    public EdsBkycIdfcViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider , Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }
    public void ongetPid() {
        getNavigator().ongetPid();
    }
    public void setData(EDSActivityWizard edsActivityWizard, MasterActivityData masterActivityData) {
        this.edsActivityWizard.set(edsActivityWizard);
        this.masterActivityData.set(masterActivityData);
    }
    public void checkStatus( ) {
        setIsLoading(true);
        try {
            getCompositeDisposable().add(getDataManager()
                    .dogeneratetoken(getDataManager().getAuthToken(),getDataManager().getEcomRegion())
                    .doOnSuccess(new Consumer<GenerateToken>() {
                        @Override
                        public void accept(GenerateToken contatDecryption) {
                        }
                    })
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<GenerateToken>() {
                        @Override
                        public void accept(GenerateToken iciciResponse) {
                            setIsLoading(false);
                            getNavigator().getToken(iciciResponse);

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            setIsLoading(false);
                        }
                    }));
        } catch (Exception e) {
            setIsLoading(false);
            e.printStackTrace();
        }
    }
    public void getmobile_no(long awb ) {
        try {
            final long timeStamp = Calendar.getInstance().getTimeInMillis();
            getCompositeDisposable().add(getDataManager()
                    .doencryptcontact(getDataManager().getAuthToken(),getDataManager().getEcomRegion(),awb)
                    .doOnSuccess(new Consumer<EncryptContactResponse>() {
                        @Override
                        public void accept(EncryptContactResponse contatDecryption) {
                            writeRestAPIResponse(timeStamp, contatDecryption);
                        }
                    })
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<EncryptContactResponse>() {
                        @Override
                        public void accept(EncryptContactResponse encryptContactResponse) {
                            getNavigator().getMobile(encryptContactResponse);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            setIsLoading(false);

                        }
                    }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
