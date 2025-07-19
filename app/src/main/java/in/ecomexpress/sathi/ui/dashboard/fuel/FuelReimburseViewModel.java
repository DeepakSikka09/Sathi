package in.ecomexpress.sathi.ui.dashboard.fuel;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.fuel.FuelReimbursementRequest;
import in.ecomexpress.sathi.repo.remote.model.fuel.response.Reports;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class FuelReimburseViewModel extends BaseViewModel<IFuelReimburseNavigator> {
    private static final String TAG = FuelReimburseViewModel.class.getSimpleName();
    ProgressDialog dialog;
    List<Reports> getReports;

    @Inject
    public FuelReimburseViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);

    }

    public void getAllFuelList(Context context) {
        dialog = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
        dialog.show();
        dialog.setCancelable(false);
        dialog.setMessage("Fetching Data...");
        dialog.setIndeterminate(true);
        final long timeStamp = Calendar.getInstance().getTimeInMillis();
        FuelReimbursementRequest request = new FuelReimbursementRequest(getDataManager().getCode(), "", "");
        writeRestAPIRequst(timeStamp, request);
        try {
            getCompositeDisposable()
                    .add(getDataManager()
                            .doFuelListApiCall
                                    (getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request)
                            .doOnSuccess(fuelReimbursementResponse -> {
                                writeRestAPIResponse(timeStamp, fuelReimbursementResponse);
                                Logger.i(TAG, "accept: " + fuelReimbursementResponse);
                            })
                            .subscribeOn(getSchedulerProvider().io())
                            .observeOn(getSchedulerProvider().ui())
                            .subscribe(fuelReimbursementResponse -> {
                                try {
                                    try {
                                        dialog.dismiss();
                                        if (fuelReimbursementResponse.getResponse() == null) {
                                            getNavigator().onShowDescription("Nothing to show");
                                        } else {
                                            Log.i(TAG, fuelReimbursementResponse.toString());
                                            if (fuelReimbursementResponse.getStatus()) {
                                                getReports = fuelReimbursementResponse.getResponse().getReports();
                                                if (!getReports.isEmpty()) {
                                                    getNavigator().OnSetFuelAdapter(fuelReimbursementResponse.getResponse().getReports());
                                                } else {
                                                    getNavigator().onShowDescription("No data found");
                                                }
                                            } else {
                                                getNavigator().onShowDescription(fuelReimbursementResponse.getResponse().getDescription());
                                            }
                                        }
                                    } catch (Exception ex) {
                                        dialog.dismiss();
                                        String error;
                                        error = new RestApiErrorHandler(ex).getErrorDetails().getEResponse().getDescription();
                                        if (error.contains("HTTP 500 ")) {
                                            getNavigator().showErrorMessage(true);
                                        }
                                        Logger.i(TAG, "fuelReimbursementResponse: " + fuelReimbursementResponse);
                                        if (fuelReimbursementResponse.getResponse().getCode().equalsIgnoreCase("107")) {
                                            getNavigator().onShowLogout(fuelReimbursementResponse.getResponse().getDescription());
                                        } else {
                                            Logger.e(TAG, error);
                                        }
                                    }
                                } catch (Exception e) {
                                    dialog.dismiss();
                                    getNavigator().showError(e.getMessage());
                                    Logger.e(TAG, String.valueOf(e));
                                }
                            }, throwable -> {
                                String error;
                                dialog.dismiss();
                                try {
                                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                                    getNavigator().showErrorMessage(error.contains("HTTP 500 "));
                                } catch (Exception e) {
                                    dialog.dismiss();
                                    getNavigator().showError(e.getMessage());
                                    Logger.e(TAG, String.valueOf(e));
                                }
                            }));
        } catch (Exception e) {
            dialog.dismiss();
            getNavigator().showError(e.getMessage());
            dialog.dismiss();
        }
    }

    public void logoutLocal() {
        try {
            getDataManager().setTripId("");
            getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT);
            clearAppData();
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void clearAppData() {
        try {
            getCompositeDisposable().add(getDataManager()
                    .deleteAllTables().subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
                        try {
                            getDataManager().clearPrefrence();
                            getDataManager().setUserAsLoggedOut();
                        } catch (Exception ex) {
                            Logger.e(TAG, String.valueOf(ex));
                        }
                        getNavigator().clearStack();
                    }));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }
}