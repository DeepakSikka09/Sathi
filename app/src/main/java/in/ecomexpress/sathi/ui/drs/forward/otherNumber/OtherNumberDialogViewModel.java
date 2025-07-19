package in.ecomexpress.sathi.ui.drs.forward.otherNumber;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import androidx.databinding.ObservableField;
import android.util.Log;

import java.util.ArrayList;


import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.payphi.payment_link_sms.PaymentSmsLinkRequset;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;



@HiltViewModel
public class OtherNumberDialogViewModel extends BaseViewModel<IOtherDialogNavigator> {
    ProgressDialog dialog;

    ObservableField<ArrayList<String>> mob_list= new ObservableField<>();

    @Inject
    public OtherNumberDialogViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider , Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }
    public void onSubmitClick(){
        getNavigator().checkValidation();
    }
    public void onCancelClick() {
        getNavigator().dismissDialog();
    }

    public void saveApiUrl(ArrayList<String> mob_list,String awb) {
        try {
            getCompositeDisposable().add(getDataManager().updateMobileList(mob_list,awb)
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
                        Log.e("isDataSaved?", "saved");

                        getAllApiUrl(awb);
                    }, throwable -> throwable.printStackTrace()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getAllApiUrl(String stAwb) {

        try {
            getCompositeDisposable().add(getDataManager().getSmsLink(stAwb)
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui()).subscribe(msgLinkData -> {
                        try {
                            if (msgLinkData != null) {
                                if (msgLinkData.getMobile_number_list()!=null) {
                                    mob_list.set(msgLinkData.getMobile_number_list());
                                    getNavigator().setMobListOnView();

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }, throwable -> {

                    }));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @param awb-- awb no.
     * @param context-- activity context
     * @param drs_id_num-- drs id
     * @param number-- mobile no.
     */
    public void sendSmsLinkPayphi(String awb, Activity context, String drs_id_num, String number) {
        if (!context.isFinishing()) {
            dialog = new ProgressDialog(context,android.R.style.Theme_Material_Light_Dialog);
            dialog.show();
            dialog.setMessage("Sending payment link...");
            dialog.setIndeterminate(false);
        }
        try {
            final long timeStamp = System.currentTimeMillis();
            PaymentSmsLinkRequset request = new PaymentSmsLinkRequset(getDataManager().getEmp_code() ,awb, number,drs_id_num);
            writeRestAPIRequst(timeStamp, request);
            getCompositeDisposable().add(getDataManager()
                    .doSentPayphiPaymentLinkSms(getDataManager().getAuthToken(), getDataManager().getEcomRegion(),request)
                    .doOnSuccess(awbResponse -> {
                        if (awbResponse.getStatus().equalsIgnoreCase("true")){
                            if (awbResponse.getDescription().equalsIgnoreCase("success")){
                                getDataManager().setCardCount(awb ,0);
                                getDataManager().setMessageClicked(awb ,true);
                                getDataManager().setCardClicked(awb ,false);
                                writeRestAPIResponse(timeStamp, awbResponse);
                                callCounter();
                                getNavigator().dismissDialog();
                                dialog.dismiss();
                            }

                        }
                        else {
                            if (awbResponse.getDescription()!=null) {
                                getNavigator().showerrorMessage(awbResponse.getDescription());
                                dialog.dismiss();
                                getNavigator().dismissDialog();
                            }
                        }
                    })
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(response -> {
                        setIsLoading(false);
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }, throwable -> {
                        if (dialog.isShowing())
                            dialog.dismiss();

                    }));

        } catch (Exception e) {
            if (dialog.isShowing())
                dialog.dismiss();
            e.printStackTrace();
        }
    }
    public void onClickOtherNum(){
        getNavigator().onClickOtherNumber();
    }
    public void onRegisterNumClick(){
        getNavigator().onRegisterNumClick();
    }
    public void callCounter(){
        getNavigator().showCounter();
    }
}
