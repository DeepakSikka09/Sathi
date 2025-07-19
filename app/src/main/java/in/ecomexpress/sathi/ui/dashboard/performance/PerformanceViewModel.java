package in.ecomexpress.sathi.ui.dashboard.performance;

import android.app.Application;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.performance.PerformanceRequest;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class PerformanceViewModel extends BaseViewModel<IPerformanceNavigator> {

    @Inject
    public PerformanceViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);
    }

    public String getAuthToken() {
        return getDataManager().getAuthToken();
    }

    public void callApi(PerformanceActivity context) {
        try {
            PerformanceRequest request = new PerformanceRequest(getDataManager().getCode());
            final long timeStamp = System.currentTimeMillis();
            writeRestAPIRequst(timeStamp, request);
            getCompositeDisposable().add(getDataManager().doPerformanceApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(performanceResponse -> {
                try {
                    context.dialog.dismiss();
                    getNavigator().startPerformanceWebView(performanceResponse.getPerformance_response());
                } catch (Exception e) {
                    e.printStackTrace();
                    writeErrors(System.currentTimeMillis(), e);
                    getNavigator().errorHandler("Unable to Load...");
                }
            }, throwable -> {
                try {
                    writeErrors(System.currentTimeMillis(), new Exception(throwable));
                    context.dialog.dismiss();
                    getNavigator().showHandleError(throwable.getMessage().contains("HTTP 500 "));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
            writeErrors(System.currentTimeMillis(), e);
            context.dialog.dismiss();
            getNavigator().errorHandler("Unable to Load...");
        }


    }

    public void logoutLocal() {
        try {
            getDataManager().setTripId("");
            getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT);
            clearAppData();

        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
    }

    private void clearAppData() {
        try {

            getCompositeDisposable().add(getDataManager().deleteAllTables().subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new io.reactivex.functions.Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) {
                    try {
                        getDataManager().clearPrefrence();
                        getDataManager().setUserAsLoggedOut();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    getNavigator().clearStack();

                }
            }));

        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
    }


}
