package in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_idfc;

import androidx.databinding.ObservableField;
import android.app.Application;
import android.util.Log;

import java.util.Calendar;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.EncryptContactResponse;
import in.ecomexpress.sathi.repo.remote.model.GenerateToken;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.eds.IDFCToken_Response;
import in.ecomexpress.sathi.repo.remote.model.eds.Idfc_token_request;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.functions.Consumer;


@HiltViewModel
public class EdsEkycIdfcViewModel  extends BaseViewModel<IEdsEkycIdfcFragmentNavigator> {
    public ObservableField<MasterActivityData> masterActivityData = new ObservableField<>();
    public ObservableField<EDSActivityWizard> edsActivityWizard = new ObservableField<>();

    @Inject
    public EdsEkycIdfcViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider , Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }
    public void onActivateScan() {
        getNavigator().onActivateScan();
    }
    public void setData(EDSActivityWizard edsActivityWizard, MasterActivityData masterActivityData) {
        this.edsActivityWizard.set(edsActivityWizard);
        this.masterActivityData.set(masterActivityData);
    }
    public void generateToken(String url , String scope , String grant_type , String client_id , String client_assertion_type , String token) {
        setIsLoading(true);
        try {
            getCompositeDisposable().add(getDataManager()
                    .dogeneratetokenIDFC(url ,getDataManager().getEcomRegion(),grant_type ,client_assertion_type ,client_id ,scope ,token)
                    .doOnSuccess(new Consumer<IDFCToken_Response>() {
                        @Override
                        public void accept(IDFCToken_Response contatDecryption) {

                        }
                    })
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<IDFCToken_Response>() {
                        @Override
                        public void accept(IDFCToken_Response idfcToken_response) {
                            setIsLoading(false);
                            Log.e("idfc token response" , idfcToken_response+"");
                            getNavigator().getToken(idfcToken_response);

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            setIsLoading(false);
                            Log.e("excption" ,throwable.getLocalizedMessage());
                        }
                    }));
        } catch (Exception e) {
            setIsLoading(false);
            e.printStackTrace();
        }
    }
    public String getAuthToken()
    {
        return getDataManager().getAuthToken();
    }
    public void getConsigneemobile_no(long awb ) {
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
