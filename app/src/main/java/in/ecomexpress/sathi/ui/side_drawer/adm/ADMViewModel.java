package in.ecomexpress.sathi.ui.side_drawer.adm;

import android.app.Application;
import android.view.View;
import android.widget.AdapterView;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.adm.ADMUpdateRequest;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class ADMViewModel extends BaseViewModel<IADMNavigator> {

    private final String TAG = ADMViewModel.class.getSimpleName();

    @Inject
    public ADMViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication){
        super(dataManager, schedulerProvider, sathiApplication);
    }

    public void onChooseReasonSpinner1(AdapterView<?> parent, View view, int pos, long id){
        getNavigator().chooseSpinner1(parent, view, pos, id);
    }

    public void onChooseReasonSpinner2(AdapterView<?> parent, View view, int pos, long id){
        getNavigator().chooseSpinner2(parent, view, pos, id);
    }

    public void onChooseReasonSpinner3(AdapterView<?> parent, View view, int pos, long id){
        getNavigator().chooseSpinner3(parent, view, pos, id);
    }

    public void onChooseReasonSpinner4(AdapterView<?> parent, View view, int pos, long id){
        getNavigator().chooseSpinner4(parent, view, pos, id);
    }

    public void onChooseReasonSpinner5(AdapterView<?> parent, View view, int pos, long id){
        getNavigator().chooseSpinner5(parent, view, pos, id);
    }

    public void onChooseReasonSpinner6(AdapterView<?> parent, View view, int pos, long id){
        getNavigator().chooseSpinner6(parent, view, pos, id);
    }

    public void onChooseReasonSpinner7(AdapterView<?> parent, View view, int pos, long id){
        getNavigator().chooseSpinner7(parent, view, pos, id);
    }

    public void onClickTimer1(){
        getNavigator().setTimer1();
    }

    public void onClickTimer2(){
        getNavigator().setTimer2();
    }

    public void onClickTimer3(){
        getNavigator().setTimer3();
    }

    public void onClickTimer4(){
        getNavigator().setTimer4();
    }

    public void onClickTimer5(){
        getNavigator().setTimer5();
    }

    public void onClickTimer6(){
        getNavigator().setTimer6();
    }

    public void onClickTimer7(){
        getNavigator().setTimer7();
    }

    public void onUpdateClick(){
        getNavigator().setUpdatedData();
    }

    public void getADMData(){
        setIsLoading(true);
        try{
            String empCode = getDataManager().getEmp_code();
            getCompositeDisposable().add(getDataManager().getAdmData(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), empCode).doOnSuccess(response -> setIsLoading(false)).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                setIsLoading(false);
                Logger.i(TAG, response+"");
                getNavigator().sendADMResponse(response);
            }, throwable -> {
                setIsLoading(false);
                Logger.e(TAG, String.valueOf(throwable));
            }));
        } catch(Exception e){
            setIsLoading(false);
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void doUpdateADMData(List<ADMUpdateRequest> admUpdateRequests, String from){
        setIsLoading(true);
        try{
            getCompositeDisposable().add(getDataManager().doUpdateADMData(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), admUpdateRequests).doOnSuccess(response -> setIsLoading(false)).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                setIsLoading(false);
                getNavigator().showInfo(response);
                getDataManager().setADMUpdated(true);
                if(from.equalsIgnoreCase("dashboard")){
                    getNavigator().setFinish();
                }
                Logger.e(TAG, String.valueOf(response));
            }, throwable -> {
                setIsLoading(false);
                Logger.e(TAG, String.valueOf(throwable));
            }));
        } catch(Exception e){
            setIsLoading(false);
            Logger.e(TAG, String.valueOf(e));
        }
    }
}