package in.ecomexpress.sathi.ui.drs.rts.rts_scan_and_deliver;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.db.model.ImageModel;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.ShipmentsDetail;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.CryptoUtils;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@HiltViewModel
public class RTSScanActivityViewModel extends BaseViewModel<IRTSScanActivityNavigator> {

    ObservableField<List<ShipmentsDetail>> listShipmentObserver = new ObservableField<>();
    ObservableField<String> imageRequiredObservable = new ObservableField<>("");
    ObservableField<String> latchingRequiredObservable = new ObservableField<>("");
    private Dialog dialog;
    private final MutableLiveData<Boolean> imageUploadSuccessLiveData = new MutableLiveData<>();

    public LiveData<Boolean> getImageUploadSuccessLiveData() {
        return imageUploadSuccessLiveData;
    }

    @Inject
    public RTSScanActivityViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application application) {
        super(dataManager, schedulerProvider, application);
    }

    public void updateManualScannedMark(List<ShipmentsDetail> shipmentsDetailList) {
        try {
            for (ShipmentsDetail shipmentsDetail : shipmentsDetailList) {
                if (shipmentsDetail.isIS_FLYER_WRONG_CAPTURED()) {
                    try {
                        shipmentsDetail.setStatus(Constants.RTSUNDELIVERED);
                        shipmentsDetail.setReasonCode(Integer.parseInt(getDataManager().getRVP_UD_FLYER()));
                        shipmentsDetail.setChecked(false);
                    } catch (Exception e) {
                        getNavigator().onErrorMessage(String.valueOf(e));
                    }
                } else {
                    shipmentsDetail.setStatus(Constants.RTSDELIVERED);
                    shipmentsDetail.setChecked(false);
                    shipmentsDetail.setReasonCode(999);
                }
                updateShipment(shipmentsDetail);
            }
        } catch (Exception e) {
            getNavigator().onErrorMessage(String.valueOf(e));
        }
    }

    public void getVWDetails(long id) {
        try {
            getCompositeDisposable().add(getDataManager().getVWDetails(id).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(details -> {
                latchingRequiredObservable.set(details.getLatching_required());
                imageRequiredObservable.set(details.getImage_required());
            }));
        } catch (Exception e) {
            getNavigator().onErrorMessage(String.valueOf(e));
        }
    }

    public void updateShipment(ShipmentsDetail shipmentsDetail) {
        try {
            getCompositeDisposable().add(getDataManager().updateRTSShipmentDetail(shipmentsDetail).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).
                subscribe(aBoolean -> {
                    getNavigator().notifyAdapter();
                    getShipments1(Constants.rtsVWDetailID);
            }));
        } catch (Exception e) {
            getNavigator().onErrorMessage(String.valueOf(e));
        }
    }

    public void getShipments1(long vwID) {
        try {
            getCompositeDisposable().add(getDataManager().getVWShipmentList(vwID).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(shipmentsDetails -> {
                listShipmentObserver.set(shipmentsDetails);
                getNavigator().OnSetAdapter(shipmentsDetails);
            }));
        } catch (Exception e) {
            getNavigator().onErrorMessage(String.valueOf(e));
        }
    }

    public void uploadImageOnServer(String imageName, String imageUri, String imageCode, long awbNo, int drsNo, long vendorId, String ud_rts_image, Context context) {
        showProgressDialog(context, context.getString(R.string.image_uploading));
        try {
            File image_file = new File(imageUri);
            byte[] bytes = CryptoUtils.decryptFile1(image_file.toString(), Constants.ENC_DEC_KEY);
            RequestBody mFile = RequestBody.create(MediaType.parse("application/octet-stream"), Objects.requireNonNull(bytes));
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", image_file.getName(), mFile);
            RequestBody awb_no = RequestBody.create(MultipartBody.FORM, String.valueOf(awbNo));
            RequestBody drs_no = RequestBody.create(MultipartBody.FORM, String.valueOf(drsNo));
            RequestBody image_code = RequestBody.create(MultipartBody.FORM, String.valueOf(imageCode));
            RequestBody image_name = RequestBody.create(MultipartBody.FORM, String.valueOf(imageName));
            RequestBody image_type = RequestBody.create(MultipartBody.FORM, GlobalConstant.ImageTypeConstants.OTHERS);
            Map<String, RequestBody> map = new HashMap<>();
            map.put("image", mFile);
            map.put("awb_no", awb_no);
            map.put("drs_no", drs_no);
            map.put("image_code", image_code);
            map.put("image_name", image_name);
            map.put("image_type", image_type);
            Map<String, String> headers = new HashMap<>();
            headers.put("token", getDataManager().getAuthToken());
            headers.put("Accept", "application/json");
            try {
                getCompositeDisposable().add(getDataManager().doImageUploadApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), GlobalConstant.ImageTypeConstants.OTHERS, headers, map, fileToUpload).doOnSuccess(imageQualityResponse -> {}).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(imageUploadResponse -> {
                    dismissProgressDialog();
                    String errorDescription;
                    try {
                        if (imageUploadResponse == null) {
                            getNavigator().onErrorMessage(getApplication().getString(R.string.api_response_null));
                            return;
                        }
                        errorDescription = (imageUploadResponse.getDescription() == null) ? getApplication().getString(R.string.image_upload_api_response_false) : imageUploadResponse.getDescription();
                        String status = imageUploadResponse.getStatus();
                        if (status == null || (!status.equalsIgnoreCase("true") && !status.equalsIgnoreCase("Success"))) {
                            getNavigator().onErrorMessage(errorDescription);
                        } else {
                            saveImageDB(imageUri, imageCode, imageName, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_COMPLETE, vendorId, awbNo, imageUploadResponse.getImageId(), ud_rts_image, 2);
                        }
                    } catch (Exception e) {
                        getNavigator().onErrorMessage(String.valueOf(e));
                    }
                }, throwable -> {
                    dismissProgressDialog();
                    getNavigator().onErrorMessage(String.valueOf(throwable));
                }));
            } catch (Exception e) {
                dismissProgressDialog();
                getNavigator().onErrorMessage(String.valueOf(e));
            }
        } catch (Exception e) {
            dismissProgressDialog();
            getNavigator().onErrorMessage(String.valueOf(e));
        }
    }

    public void saveImageDB(String imageUri, String imageCode, String name, int image_sync_status, long rtsVWDetailID, long shipment_awb_no, int image_id, String ud_rts_image, int status) {
        try {
            imageUploadSuccessLiveData.setValue(true);
            ImageModel imageModel = new ImageModel();
            imageModel.setDraNo(String.valueOf(shipment_awb_no));
            imageModel.setAwbNo(String.valueOf(rtsVWDetailID));
            imageModel.setImageName(name);
            imageModel.setImage(imageUri);
            imageModel.setImageCode(imageCode);
            imageModel.setStatus(status);
            imageModel.setImageCurrentSyncStatus(image_sync_status);
            imageModel.setImageFutureSyncTime(Calendar.getInstance().getTimeInMillis());
            imageModel.setImageId(image_id);
            imageModel.setDate(Calendar.getInstance().getTimeInMillis());
            imageModel.setShipmentType(GlobalConstant.ShipmentTypeConstants.RTS);
            imageModel.setImageType(ud_rts_image);
            getCompositeDisposable().add(getDataManager().saveImage(imageModel).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {}));
        } catch (Exception e) {
            getNavigator().onErrorMessage(String.valueOf(e));
        }
    }

    public void updateImageCapturedFlag(long shipment_awb_no, int is_image_captured) {
        try {
            getCompositeDisposable().add(getDataManager().updateRtsImageCapturedStatus(shipment_awb_no, is_image_captured).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> getShipments1(Constants.rtsVWDetailID)));
        } catch (Exception e) {
            getNavigator().onErrorMessage(String.valueOf(e));
        }
    }

    private void showProgressDialog(Context context, String loadingMessage) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_progress_dialog);
        dialog.setCancelable(false);
        TextView loadingText = dialog.findViewById(R.id.dialog_loading_text);
        loadingText.setText(loadingMessage);
        dialog.show();
    }

    private void dismissProgressDialog(){
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }
}