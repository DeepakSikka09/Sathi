package in.ecomexpress.sathi.ui.dashboard.mapview;

import static in.ecomexpress.sathi.utils.Constants.DISTANCE_API_KEY;
import static in.ecomexpress.sathi.utils.Helper.TAG;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.geolocations.LocationService;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.backgroundServices.SyncServicesV2;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.login.LogoutRequest;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.ui.drs.forward.details.ForwardDetailViewModel;
import in.ecomexpress.sathi.ui.drs.forward.obd.viewmodel.FwdOBDCompleteViewModel;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class MapViewmodel extends BaseViewModel<MapNavigator> {

    @Inject
    public MapViewmodel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application application) {
        super(dataManager, schedulerProvider, application);
    }


    public double getDistanceBetweenLocations(com.google.maps.model.LatLng destination) {
        try {
            double distance;
            GeoApiContext context = new GeoApiContext().setApiKey(DISTANCE_API_KEY);
            DirectionsResult result = DirectionsApi.newRequest(context).mode(TravelMode.DRIVING).units(Unit.METRIC).origin(new com.google.maps.model.LatLng(getDataManager().getCurrentLatitude(), getDataManager().getCurrentLongitude())).optimizeWaypoints(true).destination(destination).awaitIgnoreError();
            String dis = (result.routes[0].legs[0].distance.humanReadable);
            if (dis.endsWith("km")) {
                distance = Double.parseDouble(dis.replaceAll("[^\\.0123456789]", "")) * 1000;
            } else {
                distance = Double.parseDouble(dis.replaceAll("[^\\.0123456789]", ""));
            }
            return distance;
        } catch (Exception e) {
            Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
        }
        return 0.0;
    }

    public void onLogoutClick() {
        try {
            getNavigator().onLogoutClick();
        } catch (Exception ex) {
            getNavigator().showStringError(ex.getMessage());
            Logger.e(TAG, String.valueOf(ex));
        }
    }

    public void logout(Context context) {
        final long timeStamp = System.currentTimeMillis();
        try {
            LogoutRequest logoutRequest = new LogoutRequest();
            logoutRequest.setUsername(getDataManager().getCode());
            logoutRequest.setLogoutLat(getDataManager().getCurrentLatitude());
            logoutRequest.setLogoutLng(getDataManager().getCurrentLongitude());
            writeRestAPIRequst(timeStamp, logoutRequest);
            getCompositeDisposable().add(getDataManager().doLogoutApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), logoutRequest).doOnSuccess(response -> writeRestAPIResponse(timeStamp, response)).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                if (response.isStatus()) {
                    logoutLocal(context);
                } else {
                    String error;
                    error = response.getResponse().getDescription();
                    if (error.contains("HTTP 500 ")) {
                        getNavigator().showErrorMessage(true);
                    } else {
                        if (error.equalsIgnoreCase("Invalid Authentication Token.")) {
                            logoutLocal(context);
                        } else {
                            getNavigator().showStringError(response.getResponse().getDescription());
                        }
                    }
                }
            }, throwable -> {
                setIsLoading(false);
                logoutLocal(context);
                RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(throwable.getCause());
                restApiErrorHandler.writeErrorLogs(timeStamp, throwable.getMessage());
            }));
        } catch (Exception e) {
            getNavigator().showStringError(e.getMessage());
            logoutLocal(context);
            Logger.e(ForwardDetailViewModel.TAG, String.valueOf(e));
        }
    }

    public void logoutLocal(Context context) {
        clearAppData(context);
    }

    private void clearAppData(Context context) {
        try {
            getCompositeDisposable().add(getDataManager().deleteAllTables().subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
                try {
                    getDataManager().clearPrefrence();
                    getDataManager().setUserAsLoggedOut();
                    getDataManager().setTripId("0");
                    getDataManager().setIsAdmEmp(false);
                    getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT);
                    try {
                        context.stopService(SyncServicesV2.getStopIntent(context));
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                    context.stopService(new Intent(context, LocationService.class));
                    SathiApplication.shipmentImageMap.clear();
                    SathiApplication.rtsCapturedImage1.clear();
                    SathiApplication.rtsCapturedImage2.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getNavigator().clearStack();
            }));
        } catch (Exception e) {
            getNavigator().showStringError(e.getMessage());
            Logger.e(ForwardDetailViewModel.TAG, String.valueOf(e));
        }
    }

}
