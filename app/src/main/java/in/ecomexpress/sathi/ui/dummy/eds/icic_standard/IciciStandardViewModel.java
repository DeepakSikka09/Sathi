package in.ecomexpress.sathi.ui.dummy.eds.icic_standard;

import android.app.Application;
import android.util.Log;

import androidx.databinding.ObservableField;

import java.util.Calendar;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.IciciResponse.IciciResponse;
import in.ecomexpress.sathi.repo.remote.model.IciciResponse.IciciResponse_standard;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.functions.Consumer;
import okhttp3.RequestBody;

@HiltViewModel
public class IciciStandardViewModel extends BaseViewModel<IIciciFragmentStandardNavigator> {
    public ObservableField<MasterActivityData> masterActivityData = new ObservableField<>();
    public ObservableField<EDSActivityWizard> edsActivityWizard = new ObservableField<>();
    public ObservableField<String> activityName = new ObservableField<>("ActivityName");
    public ObservableField<String> instructions = new ObservableField<>("Instruction");
    public ObservableField<String> activityQuestion = new ObservableField<>("");

    @Inject
    public IciciStandardViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication){
        super(dataManager, schedulerProvider, sathiApplication);
    }

    public void ongetPid(){
        getNavigator().ongetPid();
    }

    public void validateUrn(){
        getNavigator().validateUrn();
    }

    public ObservableField<String> getActivityName(){
        if(masterActivityData.get() != null && masterActivityData.get().getActivityName() != null)
            activityName.set(masterActivityData.get().getActivityName());
        return activityName;
    }

    public void setData(EDSActivityWizard edsActivityWizard, MasterActivityData masterActivityData){
        this.edsActivityWizard.set(edsActivityWizard);
        this.masterActivityData.set(masterActivityData);
        getActivityName();
        getInstruction();
    }

    public ObservableField<String> getActivityQuestion(){
        if(masterActivityData.get() != null && masterActivityData.get().getActivityName() != null)
            activityQuestion.set(masterActivityData.get().getActivityQuestion());
        return activityQuestion;
    }

    public ObservableField<String> getInstruction(){
        if((masterActivityData.get() != null)){
            instructions.set(edsActivityWizard.get().getCustomerRemarks() + "\n" /*+ masterActivityData.get().getInstructions()==null ? "" :masterActivityData.get().getInstructions()*/);
        }
        return instructions;
    }

    public void sendData(String name){
        getNavigator().sendPidDetail(name);
    }
   /* retStr = HttpConnection.doBkycCallICString ICIServer(icici_url, icici_token, kycObject.getAwbNo(), kycObject.getOrderNo(),
    args[0], getpacket(), Icici_Ekyc.this);*/

//    public void login(String token, String url, RequestBody packet){
//        setIsLoading(true);
//        try{
//            final long timeStamp = System.currentTimeMillis();
//            writeRestAPIRequst(timeStamp, packet);
//            getCompositeDisposable().add(getDataManager().doICICIApiCall(token, url, packet).doOnSuccess(new Consumer<IciciResponse_standard>() {
//                @Override
//                public void accept(IciciResponse_standard iciciResponse){
//                    getNavigator().sendICICIResponse(iciciResponse);
//                }
//            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<IciciResponse_standard>() {
//                @Override
//                public void accept(IciciResponse_standard iciciResponse){
//                }
//            }, new Consumer<Throwable>() {
//                @Override
//                public void accept(Throwable throwable) throws Exception{
//                    IciciStandardViewModel.this.setIsLoading(false);
//                    String error, myerror;
//                }
//            }));
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//    }

    public void checkStatus(String apikey, String url, RequestBody urn){
        try{
            final long timeStamp = Calendar.getInstance().getTimeInMillis();
            getCompositeDisposable().add(getDataManager().doICICICheckcStatusCallStandard(apikey, getDataManager().getEcomRegion(),url, urn).doOnSuccess(new Consumer<IciciResponse_standard>() {
                @Override
                public void accept(IciciResponse_standard iciciResponse){
                    writeRestAPIResponse(timeStamp, iciciResponse);
                }
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<IciciResponse_standard>() {
                @Override
                public void accept(IciciResponse_standard iciciResponse){
                    getNavigator().sendICICIResponse(iciciResponse);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception{
                    setIsLoading(false);
                    // hideLoader();
                    String error, myerror;
                }
            }));
        } catch(Exception e){
            // hideLoader();
            e.printStackTrace();
        }
    }
}
