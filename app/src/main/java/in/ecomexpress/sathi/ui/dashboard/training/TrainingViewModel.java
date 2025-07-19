package in.ecomexpress.sathi.ui.dashboard.training;

import android.app.Application;
import android.app.ProgressDialog;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.chola.CholaRequest;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class TrainingViewModel extends BaseViewModel<ITrainingNavigator> {

    @Inject
    public TrainingViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication){
        super(dataManager, schedulerProvider, sathiApplication);
    }

    public void onBackClick(){
        getNavigator().onBackClick();
    }

    public void getCholaURLAPI(){
        ProgressDialog dialog;
        dialog = new ProgressDialog(getNavigator().getActivityContext(),android.R.style.Theme_Material_Light_Dialog);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading View....");
        dialog.show();
        String empCode = getDataManager().getEmp_code();
        CholaRequest request = new CholaRequest(empCode);
        getCompositeDisposable().add(getDataManager().doCholaURLAPI(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), request).doOnSuccess(cholaResponse -> setIsLoading(false)).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            try{
                if(response.isStatus()){
                    if(!response.getUrl().trim().equalsIgnoreCase("")){
                        getNavigator().startTrainingWebView(response.getUrl());
                    }
                    else{
                        getNavigator().showError("Try After Some Time");
                    }
                } else{
                    if(response.getResponse().getCode() == 107){
                        getNavigator().doLogout(response.getResponse().getDescription());
                    } else{
                        getNavigator().showError(response.getDescription());
                    }
                }
            }
            catch(Exception e){
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                getNavigator().showError(e.getMessage());
                e.printStackTrace();
            }
        }, throwable -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }}));
    }

    public void logoutLocal() {
        try {
            getDataManager().setTripId("");
            getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT);
            clearAppData();
        }catch (Exception e){
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
    }

    private void clearAppData() {
        try {
            getCompositeDisposable().add(getDataManager().deleteAllTables().subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
                try {
                    getDataManager().clearPrefrence();
                    getDataManager().setUserAsLoggedOut();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                getNavigator().clearStack();

            }));
        }catch (Exception e){
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
    }
}