package in.ecomexpress.sathi.ui.dummy.eds.eds_otp;

import android.app.Application;
import android.os.SystemClock;
import android.util.Log;

import androidx.databinding.ObservableField;

import java.util.Calendar;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.remote.model.otp.otherNo.OtherNoRequest;
import in.ecomexpress.sathi.repo.remote.model.otp.otherNo.OtherNoResponse;
import in.ecomexpress.sathi.repo.remote.model.otp.resendotp.ResendOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.otp.resendotp.ResendOtpResponse;
import in.ecomexpress.sathi.repo.remote.model.otp.verifyotp.VerifyOtpRequest;
import in.ecomexpress.sathi.repo.remote.model.otp.verifyotp.VerifyOtpResponse;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.functions.Consumer;

/**
 * Created by dhananjayk on 05-12-2018.
 */

@HiltViewModel
public class EdsOtpViewModel extends BaseViewModel<IEdsOtpNavigator> {
    private long mLastClickTime = 0;
    private final ObservableField<EdsWithActivityList> edsWithActivityList = new ObservableField<EdsWithActivityList>();


    @Inject
    public EdsOtpViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider , Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }

    public void onVerify() {
        getNavigator().onVerifyOtp();
    }

    public void onMobileNoChange() {
        getNavigator().onMobileNoChange();
    }

    public void onOtherNo() {
        getNavigator().onOtherMobile(1);
    }

    public void onResend() {
//        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
//            Log.e("system", "inside");
//            return;
//        }
//        Log.e("system", "outside");
//        mLastClickTime = SystemClock.elapsedRealtime();
        getNavigator().onResendOtp();
    }

    public void onCancel() {
        getNavigator().onOtherMobile(2);
    }

    public void onCancelActivity() {
        getNavigator().onCancelActivity();
    }

    public void onBackClick() {
        getNavigator().backClick();
    }

    public void getEdsListTask(String composite_key) {
        try {

            getCompositeDisposable().add(getDataManager().
                    getEdsWithActivityList(composite_key).
                    subscribeOn(getSchedulerProvider().io()).
                    observeOn(getSchedulerProvider().ui()).
                    subscribe(new Consumer<EdsWithActivityList>() {
                        @Override
                        public void accept(EdsWithActivityList edsWithActivityList) {
                            EdsOtpViewModel.this.edsWithActivityList.set(edsWithActivityList);

                        }


                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.getMessage();
                            EdsOtpViewModel.this.setIsLoading(false);
                            getNavigator().onShowMsg(throwable.getMessage());
                            // getNavigator().doLogout(throwable.getMessage());
                        }
                    }));

        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
            e.printStackTrace();
        }
    }

    public EdsWithActivityList edsWithActivityList() {
        return edsWithActivityList.get();
    }


    public void onVerifyOTP(String OTP, long awb) {
        EdsOtpViewModel.this.setIsLoading(true);
        VerifyOtpRequest request = new VerifyOtpRequest(String.valueOf(awb), OTP, "EDS", Constants.VERIFY_OTP ,"");
        final long timeStamp = Calendar.getInstance().getTimeInMillis();
        writeRestAPIRequst(timeStamp, request);
        try {
            getCompositeDisposable()
                    .add(getDataManager().doVerifyOtpApiCall(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), request)
                            .doOnSuccess(new Consumer<VerifyOtpResponse>() {
                                @Override
                                public void accept(VerifyOtpResponse verifyOtpResponse) {
                                    EdsOtpViewModel.this.setIsLoading(false);
                                    writeRestAPIResponse(timeStamp, verifyOtpResponse);
                                    if (verifyOtpResponse != null) {
                                        if (verifyOtpResponse.getStatus()) {
                                            getNavigator().onNext(verifyOtpResponse.getResponse().getDescription());
                                        } else {
                                            if (verifyOtpResponse.getResponse().getCode().equalsIgnoreCase("E137")) {
                                                getNavigator().onMaxAttempt();
                                            } else if (verifyOtpResponse.getResponse().getCode().equalsIgnoreCase("E107")) {
                                                getNavigator().doLogout(verifyOtpResponse.getResponse().getDescription());
                                            } else
                                                getNavigator().onShowMsg(verifyOtpResponse.getResponse().getDescription());
                                        }
                                    }

                                }
                            }).subscribeOn(getSchedulerProvider().io())
                            .observeOn(getSchedulerProvider().ui())
                            .subscribe(new Consumer<VerifyOtpResponse>() {
                                @Override
                                public void accept(VerifyOtpResponse response) {
                                    try {
                                        try {
                                            if (response.getResponse().getCode() == null) {
                                                // getNavigator().onShowMsg("Code value cannot be null.Contact Server Team");
                                            } else {
                                                if ((response.getResponse() != null) && (response.getResponse().getCode().equalsIgnoreCase("E107"))) {
                                                    getNavigator().doLogout(response.getResponse().getDescription());
                                                }
                                            }
                                        } catch (Exception e) {
                                            EdsOtpViewModel.this.setIsLoading(false);
                                            getNavigator().onShowMsg(e.getMessage());
                                            e.printStackTrace();
                                        }
                                    } catch (Exception e) {
                                        EdsOtpViewModel.this.setIsLoading(false);
                                        getNavigator().onShowMsg(e.getMessage());
                                        e.printStackTrace();
                                    }
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    EdsOtpViewModel.this.setIsLoading(false);
                                    getNavigator().showErrorMessage(throwable.getMessage().contains("HTTP 500 "));

                                    // getNavigator().doLogout(throwable.getMessage());
                                }
                            }));

        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
            e.printStackTrace();
        }

    }

    public void logoutLocal() {
        try {
            getDataManager().setTripId("");
            getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT);
            clearAppData();
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearAppData() {
        try {
            getCompositeDisposable().add(getDataManager()
                    .deleteAllTables().subscribeOn
                            (getSchedulerProvider().io()).
                            observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) {
                            try {
                                getDataManager().clearPrefrence();
                                getDataManager().setUserAsLoggedOut();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                getNavigator().showError(ex.getMessage());
                            }
                            getNavigator().clearStack();

                        }
                    }));

        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
            e.printStackTrace();
        }
    }

    public void onResendOTP(long awbNo, String drsid) {
        EdsOtpViewModel.this.setIsLoading(true);
        ResendOtpRequest resendOtpRequest = new ResendOtpRequest(String.valueOf(awbNo), "OTP",drsid,false);
        final long timeStamp = Calendar.getInstance().getTimeInMillis();
        writeRestAPIRequst(timeStamp, resendOtpRequest);
        try {

            getCompositeDisposable()
                    .add(getDataManager().doResendOtpApiCall(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), resendOtpRequest)
                            .doOnSuccess(new Consumer<ResendOtpResponse>() {
                                @Override
                                public void accept(ResendOtpResponse resendOtpResponse) {
                                    writeRestAPIResponse(timeStamp, resendOtpResponse);
                                    EdsOtpViewModel.this.setIsLoading(false);
                                    if (resendOtpRequest != null) {
                                        if (resendOtpResponse.getStatus().equalsIgnoreCase("true")) {
                                            getNavigator().onResendNext(resendOtpResponse.getResponse().getDescription());
                                        } else {
                                            getNavigator().onResendNext(resendOtpResponse.getResponse().getDescription());
                                        }
                                    }

                                }
                            }).subscribeOn(getSchedulerProvider().io())
                            .observeOn(getSchedulerProvider().ui())
                            .subscribe(new Consumer<ResendOtpResponse>() {
                                @Override
                                public void accept(ResendOtpResponse response) {
                                    try {
                                        try {
                                            if (response.getResponse().getCode() == null) {
                                                getNavigator().onShowMsg("Code value cannot be null.Contact Server Team");
                                            } else {
                                                if ((response.getResponse() != null) && (response.getResponse().getCode().equalsIgnoreCase("107"))) {
                                                    getNavigator().doLogout(response.getResponse().getDescription());
                                                }
                                            }
                                        } catch (Exception e) {
                                            EdsOtpViewModel.this.setIsLoading(false);
                                            getNavigator().onShowMsg(e.getMessage());
                                            e.printStackTrace();
                                        }
                                    } catch (Exception e) {
                                        EdsOtpViewModel.this.setIsLoading(false);
                                        getNavigator().onShowMsg(e.getMessage());
                                        e.printStackTrace();
                                    }
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    EdsOtpViewModel.this.setIsLoading(false);
                                    getNavigator().showErrorMessage(throwable.getMessage().contains("HTTP 500 "));


                                    // getNavigator().doLogout(throwable.getMessage());
                                }
                            }));

        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
            e.printStackTrace();
        }

    }

    public void onsendOtpOnOtherNo(String phone, long awbNo) {
        EdsOtpViewModel.this.setIsLoading(true);
        OtherNoRequest otherNoRequest = new OtherNoRequest(String.valueOf(awbNo), phone);
        final long timeStamp = Calendar.getInstance().getTimeInMillis();
        writeRestAPIRequst(timeStamp, otherNoRequest);
        try {

            getCompositeDisposable()
                    .add(getDataManager().doResendOtpToOtherNoApiCall(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), otherNoRequest)
                            .doOnSuccess(new Consumer<OtherNoResponse>() {
                                @Override
                                public void accept(OtherNoResponse otherNoResponse) {
                                    EdsOtpViewModel.this.setIsLoading(false);
                                    writeRestAPIResponse(timeStamp, otherNoResponse);
                                    if (otherNoResponse != null) {
                                        if (otherNoResponse.getStatus().equalsIgnoreCase("true")) {
                                            getNavigator().onOtherMobile(2);
                                            getNavigator().sendMsg(otherNoResponse.getResponse().getDescription());
                                        } else {
                                            getNavigator().onOtherMobile(1);
                                            getNavigator().sendMsg(otherNoResponse.getResponse().getDescription());
                                        }
                                    }

                                }
                            }).subscribeOn(getSchedulerProvider().io())
                            .observeOn(getSchedulerProvider().ui())
                            .subscribe(new Consumer<OtherNoResponse>() {
                                @Override
                                public void accept(OtherNoResponse otherNoRequest) {
                                    try {
                                        if (otherNoRequest.getResponse().getCode() == null) {
                                            getNavigator().onShowMsg("Code value cannot be null.Contact Server Team");
                                        } else {
                                            if ((otherNoRequest.getResponse() != null) && (otherNoRequest.getResponse().getCode().equalsIgnoreCase("107"))) {
                                                getNavigator().doLogout(otherNoRequest.getResponse().getDescription());
                                            }
                                        }
                                    } catch (Exception e) {
                                        EdsOtpViewModel.this.setIsLoading(false);
                                        getNavigator().onShowMsg(e.getMessage());
                                        e.printStackTrace();
                                    }
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    EdsOtpViewModel.this.setIsLoading(false);
                                    getNavigator().showErrorMessage(throwable.getMessage().contains("HTTP 500 "));


                                    // getNavigator().doLogout(throwable.getMessage());
                                }
                            }));

        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
            e.printStackTrace();
        }
    }
}
