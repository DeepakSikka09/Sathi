package in.ecomexpress.sathi.ui.drs.sms;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import java.util.ArrayList;
import java.util.Calendar;

import javax.inject.Inject;

import cz.msebera.android.httpclient.HttpConnection;
import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.sms.Additional_info;
import in.ecomexpress.sathi.repo.remote.model.sms.SmsRequest;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class SMSViewModel extends BaseViewModel<SMSCallBack> {

    ProgressDialog dialog;
    String location, duration, remarks;
    public static final String TAG = HttpConnection.class.getSimpleName();

    public void onTime(AdapterView<?> parent, View view, int pos, long id) {
        getNavigator().time(parent.getSelectedItem().toString());
        duration = parent.getSelectedItem().toString();
    }

    public void onLocation(AdapterView<?> parent, View view, int pos, long id) {
        getNavigator().location(parent.getSelectedItem().toString());
        location = parent.getSelectedItem().toString();
    }

    @Inject
    public SMSViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }

    public void onCancelClick() {
        getNavigator().dismissDialog();
    }

    public void onSendClick() {
        getNavigator().onSendClick();
    }

    public void callSmsApi(Context context, ArrayList<String> getAwbList) {
        dialog = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
        dialog.show();
        dialog.setCancelable(false);
        dialog.setMessage("Fetching Data...");
        dialog.setIndeterminate(true);
        SmsRequest smsRequest = new SmsRequest();
        Additional_info addInfo = new Additional_info();
        smsRequest.setAwb(getAwbList);
        smsRequest.setSms_type(Constants.TECH_PARK);
        smsRequest.setLat(getDataManager().getCurrentLatitude());
        smsRequest.setLng(getDataManager().getCurrentLongitude());
        smsRequest.setDate(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        addInfo.setLocation(location);
        if (remarks.isEmpty()) {
            addInfo.setRemarks("");
        } else {
            addInfo.setRemarks(remarks);
        }
        addInfo.setDuration(duration);
        smsRequest.setAdditional_info(addInfo);

        try {
            getCompositeDisposable().add(getDataManager()
                .doSMSApiCall(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), smsRequest)
                .doOnSuccess(awbResponse -> Logger.i(TAG+"Response", awbResponse.toString()))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {
                    dialog.dismiss();
                    if (!response.getStatus()) {
                        getNavigator().onHandleError(response.getTodoResponse().getDescription());
                    }
                    getNavigator().dismissDialog();
                }, throwable -> {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    String error;
                    try {
                        error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                        getNavigator().showErrorMessage(error.contains("HTTP 500 "));
                    } catch (NullPointerException e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                }));
        } catch (Exception e) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void getFeRemarks(String feRemarks) {
        remarks = feRemarks;

    }
}