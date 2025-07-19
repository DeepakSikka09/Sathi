package in.ecomexpress.sathi.ui.drs.forward.disputeDailog;

import static android.content.ContentValues.TAG;

import android.app.Application;
import android.util.Log;

import androidx.databinding.ObservableField;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.forward.dispute_dialog.DisputeRequest;
import in.ecomexpress.sathi.repo.remote.model.forward.verify_dispute_otp.VerifyDisputeRequest;
import in.ecomexpress.sathi.repo.remote.model.payphi.raise_dispute.PaymentDisputedAwb;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.CryptoUtils;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@HiltViewModel
public class DisputeDialogViewModel extends BaseViewModel<IDisputeDialogNavigator> {

    public final ObservableField<String> inputData = new ObservableField<>("");
    String image_key = "";

    @Inject
    public DisputeDialogViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);
    }

    public void onSubmitClick() {
        getNavigator().checkValidation();
    }

    public void onVerifyClick() {
        getNavigator().onVerifyClick();
    }

    public void setCheckStatus(String checkStatus) {
        inputData.set(checkStatus);
    }

    public void captureImage() {
        getNavigator().captureImage();
    }


    public void onCancelClick() {
        getNavigator().dismissDialog();
    }


    public void onRegisterNumClick() {
        getNavigator().onClickRegisteredNumber();
    }

    public void onClickOtherNum() {
        getNavigator().onClickOtherNumber();
    }

    public void onOtpSubmitClick() {
        getNavigator().onOtpSubmitClick();
    }


    /**
     *
     * @param awbNo-- awb no.
     * @param drs_no-- drs id
     * @param imageUri-- capture image uri
     */
    public void uploadDisputeImage(String awbNo, String drs_no, String imageUri) {
        try {
            setIsLoading(true);
            File image_file = new File(imageUri);
            byte[] bytes = CryptoUtils.decryptFile1(image_file.toString(), Constants.ENC_DEC_KEY);
            RequestBody mFile = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", image_file.getName(), mFile);
            RequestBody awb = RequestBody.create(MultipartBody.FORM, awbNo);
            RequestBody drdid = RequestBody.create(MultipartBody.FORM, drs_no);
            Map<String, RequestBody> map = new HashMap<>();
            map.put("airway_number", awb);
            map.put("drs_id", drdid);
            getCompositeDisposable().add(getDataManager()
                    .doUploadDisputeImageApiCall(getDataManager().getEcomRegion(), map, fileToUpload)
                    .doOnSuccess(disputeImageApiResponse -> {})
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(disputeImageApiResponse -> {
                        setIsLoading(false);
                        if (disputeImageApiResponse.getStatus().equalsIgnoreCase("Success")) {
                            image_key = disputeImageApiResponse.getFile_name();
                            getNavigator().showImageSuccess();
                        } else {
                            getNavigator().onHandleError(disputeImageApiResponse.getDescription());
                        }
                    }, throwable -> {
                        setIsLoading(false);
                        String error;
                        try {
                            error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                            getNavigator().onHandleError(error);
                        } catch (Exception e) {
                            Logger.e(DisputeDialogViewModel.class.getName(), e.getMessage());
                        }
                    }));
        } catch (Exception e) {
            setIsLoading(false);
            getNavigator().onHandleError(e.getLocalizedMessage());
        }
    }


    /**
     *
     * @param mobile_number-- mobile number
     * @param awb_no--awb no.
     * @param drs_id-- drsid
     */
    public void raiseDispute(String mobile_number, String awb_no, String drs_id) {
        setIsLoading(true);
        try {
            DisputeRequest request = new DisputeRequest();
            request.setAlternate_mobile_number(mobile_number);
            request.setAwb_number(awb_no);
            request.setDrs_id(drs_id);
            getCompositeDisposable().add(getDataManager()
                    .doRaiseDisputeApi(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request)
                    .doOnSuccess(disputeApiResponse -> setIsLoading(false))
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(disputeApiResponse -> {
                        setIsLoading(false);
                        if (disputeApiResponse.isStatus()) {
                            getNavigator().showSucess(disputeApiResponse.getDescription());
                            getNavigator().setSucessData();
                            //saveDisputedAwb(awb_no);
                        } else {
                            getNavigator().onHandleError(disputeApiResponse.getDescription());
                        }
                    }, throwable -> {
                        setIsLoading(false);
                        String error;
                        try {
                            error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                            getNavigator().onHandleError(error);
                        } catch (NullPointerException e) {
                            Logger.e(DisputeDialogViewModel.class.getName(), e.getMessage());
                        }
                    }));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            setIsLoading(false);

        }
    }


    /**
     *
     * @param otp--- otp to verify
     * @param awb_no-- awb no.
     * @param drs_id-- drs id
     */
    public void verifyDisputeOTP(String otp, String awb_no, String drs_id) {
        setIsLoading(true);
        try {
            VerifyDisputeRequest request = new VerifyDisputeRequest();
            request.setOtp_value(otp);
            request.setAwb_number(awb_no);
            request.setDrs_id(drs_id);
            request.setPayment_ref_number("");
            request.setSms_image_key(image_key);
            getCompositeDisposable().add(getDataManager()
                    .doVerifyDisputeApi(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request)
                    .doOnSuccess(disputeApiResponse -> {
                        setIsLoading(false);
                        if (disputeApiResponse.isStatus()) {
                            saveDisputedAwb(awb_no);
                            getNavigator().showSucess(disputeApiResponse.getDescription());
                            getNavigator().openSignatureActivity();
                            getNavigator().closeDispute();
                        } else {
                            getNavigator().closeDispute();
                            getNavigator().onHandleError(disputeApiResponse.getDescription());
                        }

                    })
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(response -> setIsLoading(false), throwable -> {
                        setIsLoading(false);
                        String error;
                        try {
                            error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                            getNavigator().onHandleError(error);
                        } catch (NullPointerException e) {
                            Logger.e(DisputeDialogViewModel.class.getName(), e.getMessage());
                        }
                    }));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            setIsLoading(false);

        }
    }

    public void saveDisputedAwb(String disputedAwb) {
        try {
            PaymentDisputedAwb raiseDispute = new PaymentDisputedAwb();
            raiseDispute.setDisputed_awb(disputedAwb);
            getCompositeDisposable().add(getDataManager().saveDisputedAwb(raiseDispute).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> Log.e("isDataSaved?", "saved"), Throwable::printStackTrace));
        } catch (Exception e) {
            Logger.e(DisputeDialogViewModel.class.getName(), e.getMessage());
        }
    }

}
