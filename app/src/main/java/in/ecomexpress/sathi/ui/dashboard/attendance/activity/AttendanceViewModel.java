package in.ecomexpress.sathi.ui.dashboard.attendance.activity;

import android.app.Application;
import java.util.HashMap;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.attendance.AttendanceDetails;
import in.ecomexpress.sathi.repo.remote.model.attendance.AttendanceRequest;
import in.ecomexpress.sathi.repo.remote.model.attendance.Response;
import in.ecomexpress.sathi.repo.remote.model.login.LogoutRequest;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class AttendanceViewModel extends BaseViewModel<IAttendanceNavigator> {

    @Inject
    public AttendanceViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);
    }

    public void onSpinnerClick() {
        getNavigator().spinnerClick();
    }

    public void callApi(String month, String year) {
        AttendanceViewModel.this.setIsLoading(true);
        try {
            AttendanceRequest request = new AttendanceRequest(getDataManager().getCode(), month, year);
            final long timeStamp = System.currentTimeMillis();
            writeRestAPIRequst(timeStamp, request);
            getCompositeDisposable().add(getDataManager()
                    .doAttendanceApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request)
                    .observeOn(getSchedulerProvider().ui())
                    .subscribeOn(getSchedulerProvider().io()).
                    subscribe(attendanceResponse -> {
                        if (attendanceResponse.getResponse() == null) {
                            attendanceResponse.setResponse(new Response());
                        }
                        if (attendanceResponse.getResponse().getAttendance_details() == null) {
                            attendanceResponse.getResponse().setAttendance_details(new AttendanceDetails());
                        }
                        AttendanceViewModel.this.setIsLoading(false);
                        try {
                            if (attendanceResponse.getStatus()) {
                                getNavigator().sendData(attendanceResponse.getResponse().getAttendance_details().getAvg_working_hours(), attendanceResponse.getResponse().getAttendance_details().getStart_day_of_month(), attendanceResponse.getResponse().getAttendance_details().getAttendance_response_list());
                            } else{
                                if (attendanceResponse.getResponse().getCode() == 107)
                                    logout();
                            }
                        } catch (Exception e) {
                            AttendanceViewModel.this.setIsLoading(false);
                            e.printStackTrace();
                        }
                    }, throwable -> {
                        try {
                            setIsLoading(false);
                            getNavigator().showErrorMessage(throwable.getMessage().contains("HTTP 500"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
            AttendanceViewModel.this.setIsLoading(false);
            e.printStackTrace();
        }
    }

    public void logout() {
        final long timeStamp = System.currentTimeMillis();
        try {
            LogoutRequest logoutRequest = new LogoutRequest();
            logoutRequest.setUsername(getDataManager().getCode());
            logoutRequest.setLogoutLat(getDataManager().getCurrentLatitude());
            logoutRequest.setLogoutLng(getDataManager().getCurrentLongitude());
            writeRestAPIRequst(timeStamp, logoutRequest);
            getCompositeDisposable().add(getDataManager()
                    .doLogoutApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), logoutRequest)
                    .doOnSuccess(response -> writeRestAPIResponse(timeStamp, response))
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(response -> {
                        if (response.isStatus()) {
                            logoutLocal();
                        } else {
                            String error;
                            error = response.getResponse().getDescription();
                            if (error.contains("HTTP 500 ")) {
                                getNavigator().showErrorMessage(true);
                            } else {
                                if (error.equalsIgnoreCase("Invalid Authentication Token.")) {
                                    logoutLocal();
                                } else {
                                    getNavigator().showStringError(response.getResponse().getDescription());
                                }
                            }
                        }
                    }, throwable -> {
                        setIsLoading(false);
                        logoutLocal();
                        RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(throwable.getCause());
                        restApiErrorHandler.writeErrorLogs(timeStamp, throwable.getMessage());
                    }));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
            e.printStackTrace();
            logoutLocal();
        }
    }

    public void logoutLocal() {
        getDataManager().setTripId("");
        getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT);
        clearAppData();
    }

    private void clearAppData() {
        try {
            getCompositeDisposable().add(getDataManager()
                .deleteAllTables().subscribeOn(getSchedulerProvider().io()).
                observeOn(getSchedulerProvider().ui())
                .subscribe(aBoolean -> {
                    try {
                        getDataManager().clearPrefrence();
                        getDataManager().setUserAsLoggedOut();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    getNavigator().clearStack();
                }));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
        }
    }

    public void getSingleDayData(String sdate, String sintime, String souttime, String sworkingHours, String sstatus) {
        try {
            HashMap<String, String> date = new HashMap<>();
            date.put("date", sdate);
            date.put("intime", sintime);
            date.put("outTime", souttime);
            date.put("wHours", sworkingHours);
            date.put("status", sstatus);
            getNavigator().getDayData(date);
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
        }
    }
}