package in.ecomexpress.sathi.ui.dummy.eds.eds_success_fail;


import androidx.databinding.ObservableField;

import android.app.Application;
import android.util.Log;

import java.util.Calendar;
import java.util.Random;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;
import in.ecomexpress.sathi.repo.remote.model.eds.CashReceipt_Request;
import in.ecomexpress.sathi.repo.remote.model.eds.CashReceipt_Response;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.functions.Consumer;

import static android.content.ContentValues.TAG;

import javax.inject.Inject;

@HiltViewModel
public class EDSSuccessFailViewModel extends BaseViewModel<IEDSSuccessFailNavigator> {

    private final ObservableField<Boolean> textColor = new ObservableField<>();
    private final ObservableField<Boolean> image = new ObservableField<>();
    private final ObservableField<String> name = new ObservableField<>("");
    private final ObservableField<String> address = new ObservableField<>("");
    private final ObservableField<String> awb = new ObservableField<>("");
    private final ObservableField<String> item = new ObservableField<>("");
    private String awb_number;
    int cashReceipt_counter =0;

    public void setContent(EDSResponse edsResponse) {

        awb.set(String.valueOf(edsResponse.getAwbNo()));
        // name.set(edsResponse.getConsigneeDetail().getName());
        //  address.set(edsResponse.getConsigneeDetail().getAddress());
        item.set(edsResponse.getShipmentDetail().getItemDescription());

    }

    public ObservableField<Boolean> getImage() {
        return image;
    }

    public void setImage(Boolean image) {
        this.image.set(image);
    }

    public void setTextColor(Boolean image) {
        this.textColor.set(image);
    }


    public ObservableField<Boolean> getTextColor() {
        return textColor;
    }

    @Inject
    public EDSSuccessFailViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider , Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }

    public void OnHomeClick() {
        getNavigator().onhomeclick();
    }


    public void onCashReceiptClick() {
        if(cashReceipt_counter>2)
        {
            getNavigator().onHandleError("Max limit reached for cash receipt.");
            return;
        }
        setIsLoading(true);
        try {
            String empCode = getDataManager().getCode();
            String awbNo = awb.get();
            CashReceipt_Request request = new CashReceipt_Request(awbNo, empCode);
            final long timeStamp = Calendar.getInstance().getTimeInMillis();
            writeRestAPIRequst(timeStamp, request);
            getCompositeDisposable().add(getDataManager()
                    .doCashReceiptCall(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), request)
                    .doOnSuccess(new Consumer<CashReceipt_Response>() {
                        @Override
                        public void accept(CashReceipt_Response cashReceipt_response) {
                            writeRestAPIResponse(timeStamp, cashReceipt_response);

                        }
                    })
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<CashReceipt_Response>() {
                        @Override
                        public void accept(CashReceipt_Response response) {
                            Log.d(TAG, "login: " + response.toString());
                            setIsLoading(false);
                            if (response.receipt_status) {
                                cashReceipt_counter++;
                                getNavigator().showError(true);
                            } else {
                                getNavigator().showError(false);
                            }

                        }
                    }, throwable -> {
                        setIsLoading(false);
                        String error;
                        try {
                            error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                            if (error.contains("HTTP 500 ")) {
                                getNavigator().showError(false);
                            } else {
                                getNavigator().showError(false);
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            getNavigator().showError(false);
            setIsLoading(false);
            if (e instanceof Throwable) {
                // getNavigator().onHandleError(new RestApiErrorHandler(e.fillInStackTrace()).getErrorDetails().getEResponse().getDescription());
            }
        }
    }


    public ObservableField<String> getName() {
        return name;
    }

    public ObservableField<String> getAddress() {
        return address;
    }

    public ObservableField<String> getAwb() {
        return awb;
    }

    public ObservableField<String> getItem() {
        return item;
    }
}
