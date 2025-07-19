package in.ecomexpress.sathi.ui.auth.verifyOtpLoginScreen;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.util.Log;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.verifyOtp.LoginVerifyOtpRequest;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.cameraView.CameraSelfieActivity;
import in.ecomexpress.sathi.ui.dashboard.mapview.MapActivity;
import in.ecomexpress.sathi.ui.dashboard.landing.DashboardActivity;
import in.ecomexpress.sathi.ui.drs.forward.obd.viewmodel.FwdOBDCompleteViewModel;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import static android.content.ContentValues.TAG;
import static in.ecomexpress.sathi.utils.Constants.DISTANCE_API_KEY;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;

import javax.inject.Inject;

@HiltViewModel
public class LoginVerifyOtpViewModel extends BaseViewModel<ILoginVerifyOtpNavigator> {


    @Inject
    public LoginVerifyOtpViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider , Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }

    com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(getDataManager().getDCLatitude(), getDataManager().getDCLongitude());
    double distanceFormDestination = getDistanceBetweenLocations(destination);

    public void onResendOtpClick() {
        getNavigator().onResendOtp();
    }

    public void onNOtpClick() {
        getNavigator().onNOtp();
    }
    
    public String getmobile() {
        return "We have send the SMS with an activation code to " +  getDataManager().getMobile().replaceAll("\\w(?=\\w{4})", "*");
    }

    public void updateUserLoggedInState() {
        getDataManager().updateUserLoggedInState(IDataManager.LoggedInMode.LOGGED_IN_MODE_SERVER);
    }

    public void onOtpVerify() {
        getNavigator().onVerify();
    }

    public void verifyOtp(String otpDelimiter) {
        setIsLoading(true);
        try {
            String empCode = getDataManager().getEmp_code();
            LoginVerifyOtpRequest request = new LoginVerifyOtpRequest(empCode, otpDelimiter,false,"");
            getCompositeDisposable().add(getDataManager()
                    .doLoginVerifyOtpApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(),request)
                    .doOnSuccess(loginVerifyOtpResponse -> {})
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(response -> {
                        LoginVerifyOtpViewModel.this.setIsLoading(false);
                        if (response.isStatus()) {
                            getNavigator().onNext(otpDelimiter);
                        } else {
                            if (response.getsResponse().getCode() == 107) {
                                LocalLogout();
                            } else {
                                LoginVerifyOtpViewModel.this.getNavigator().onHandleError("Invalid OTP");
                            }
                        }
                    }, throwable -> {
                        setIsLoading(false);
                        String error;
                        try {
                            error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                            getNavigator().showErrorMessage(error.contains("HTTP 500 "));
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }));
        } catch (Exception e) {
            getNavigator().onHandleError(e.getMessage());
            setIsLoading(false);
            if (e instanceof Throwable) {
                getNavigator().onHandleError(new RestApiErrorHandler(e.fillInStackTrace()).getErrorDetails().getEResponse().getDescription());
            }
        }
    }

    private void LocalLogout() {
        try {
        getDataManager().setTripId("");
        getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT);
        clearAppData();
        }catch (Exception e){
            e.printStackTrace();
            getNavigator().onHandleError(e.getMessage());
        }
    }

    private void clearAppData() {
       try {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager()
            .deleteAllTables().subscribeOn(getSchedulerProvider().io()).
            observeOn(getSchedulerProvider().ui())
            .subscribe(aBoolean -> {
                try {
                    getDataManager().clearPrefrence();
                    getDataManager().setUserAsLoggedOut();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                clearStack();
            }));
       } catch (Exception e){
           getNavigator().onHandleError(e.getMessage());
       }
    }

    private void clearStack() {
        getNavigator().logout();
    }

    public void resendOtp(String otpDelimiter) {
        setIsLoading(true);
        try {
            String empCode = getDataManager().getCode();
            LoginVerifyOtpRequest request = new LoginVerifyOtpRequest(empCode, otpDelimiter,false,"");
            getCompositeDisposable().add(getDataManager()
                    .doLoginResendOtpApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(),request)
                    .doOnSuccess(loginVerifyOtpResponse -> {})
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(response -> {
                        Log.d(TAG, "login: " + response.toString());
                        LoginVerifyOtpViewModel.this.setIsLoading(false);
                        if (response.isStatus()) {
                            getNavigator().onNext(otpDelimiter);
                        } else {
                            if (response.getsResponse().getCode() == 107) {
                                LocalLogout();
                            }
                            LoginVerifyOtpViewModel.this.getNavigator().onHandleError(response.getsResponse().getDescription());
                        }
                    }, throwable -> {
                        setIsLoading(false);
                        String error;
                        try {
                            error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                            getNavigator().showErrorMessage(error.contains("HTTP 500 "));
                        } catch (Exception e) {
                            getNavigator().onHandleError(e.getMessage());
                            e.printStackTrace();
                        }
                    }));
        } catch (Exception e) {
            setIsLoading(false);
            getNavigator().onHandleError(e.getMessage());
        }
    }

    public void checkAttendance(Activity context, String empCode) {
        setIsLoading(true);
        try {
            final long timeStamp = System.currentTimeMillis();
            writeRestAPIRequst(timeStamp, empCode);
            SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.CHECK_ATTENDANCE);
            getCompositeDisposable().add(getDataManager()
                .doCheckAttendanceApiCall(getDataManager().getAuthToken(), empCode)
                .doOnSuccess(checkAttendanceResponse -> writeRestAPIResponse(timeStamp, checkAttendanceResponse))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {
                    LoginVerifyOtpViewModel.this.setIsLoading(false);
                    Intent intent;
                    if (response.isStatus()) {
                        intent = new Intent(context, DashboardActivity.class);
                    } else {
                        intent = (distanceFormDestination < getDataManager().getDCRANGE()) ?
                                new Intent(context, CameraSelfieActivity.class) :
                                new Intent(context, MapActivity.class);
                    }
                    context.startActivity(intent);
                    context.finish();
                }, throwable -> {
                    setIsLoading(false);
                    getNavigator().onHandleError("Check Attendance API Failed");
                }));
        } catch (Exception e) {
            setIsLoading(false);
            getNavigator().onHandleError("Check Attendance API Failed");
        }
    }

    public double getDistanceBetweenLocations(com.google.maps.model.LatLng destination) {
        try {
            double distance;
            GeoApiContext context = new GeoApiContext().setApiKey(DISTANCE_API_KEY);
            DirectionsResult result = DirectionsApi.newRequest(context).mode(TravelMode.DRIVING).units(Unit.METRIC).origin(new com.google.maps.model.LatLng(getDataManager().getCurrentLatitude(), getDataManager().getCurrentLongitude())).optimizeWaypoints(true).destination(destination).awaitIgnoreError();
            String dis = (result.routes[0].legs[0].distance.humanReadable);
            if (dis.endsWith("km")) {
                distance = Double.parseDouble(dis.replaceAll("[^.0123456789]", "")) * 1000;
            } else {
                distance = Double.parseDouble(dis.replaceAll("[^.0123456789]", ""));
            }
            return distance;
        } catch (Exception e) {
            Logger.e(FwdOBDCompleteViewModel.class.getName(), e.getMessage());
        }
        return 0.0;
    }
}