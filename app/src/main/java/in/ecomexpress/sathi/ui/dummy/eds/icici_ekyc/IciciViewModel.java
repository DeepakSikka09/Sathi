package in.ecomexpress.sathi.ui.dummy.eds.icici_ekyc;

import android.app.Application;
import android.util.Log;

import androidx.databinding.ObservableField;

import java.util.Calendar;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.IciciResponse.IciciResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.functions.Consumer;
import okhttp3.RequestBody;

@HiltViewModel
public class IciciViewModel extends BaseViewModel<IIciciFragmentNavigator> {
    public ObservableField<MasterActivityData> masterActivityData = new ObservableField<>();
    public ObservableField<EDSActivityWizard> edsActivityWizard = new ObservableField<>();
    public ObservableField<String> activityName = new ObservableField<>("ActivityName");
    public ObservableField<String> instructions = new ObservableField<>("Instruction");
    public ObservableField<String> activityQuestion = new ObservableField<>("");

    @Inject
    public IciciViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication){
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

    public void login(String token, String url, RequestBody packet){
        setIsLoading(true);
        try{
            final long timeStamp = System.currentTimeMillis();
            writeRestAPIRequst(timeStamp, packet);
            getCompositeDisposable().add(getDataManager().doICICIApiCall(token,getDataManager().getEcomRegion(), url, packet).doOnSuccess(new Consumer<IciciResponse>() {
                @Override
                public void accept(IciciResponse iciciResponse){
                    writeRestAPIResponse(timeStamp, iciciResponse);
                    Log.d("ICICI", iciciResponse.toString());
                    getNavigator().sendICICIResponse(iciciResponse);
                }
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<IciciResponse>() {
                @Override
                public void accept(IciciResponse iciciResponse){
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception{
                    IciciViewModel.this.setIsLoading(false);
                    String error, myerror;
                }
            }));
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void checkStatus(String token, String url, RequestBody urn){
        // showLoader();
        // setIsLoading(true);
        try{
            final long timeStamp = Calendar.getInstance().getTimeInMillis();
            // IciciRequest iciciRequest = new IciciRequest(packet);
            //  Gson gson = new Gson();
            // IciciRequest iciciRequest = gson.fromJson(packet ,IciciRequest.class);
            // writeRestAPIRequst(timeStamp, packet);
            getCompositeDisposable().add(getDataManager().doICICICheckcStatusCall(token,getDataManager().getEcomRegion(), url, urn).doOnSuccess(new Consumer<IciciResponse>() {
                @Override
                public void accept(IciciResponse iciciResponse){

                }
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<IciciResponse>() {
                @Override
                public void accept(IciciResponse iciciResponse){
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
