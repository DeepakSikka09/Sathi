package in.ecomexpress.sathi.ui.side_drawer.dc_location_updation;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.TextView;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.DCLocationUpdate.DCLocationUpdate;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class DCLocationViewModel extends BaseViewModel<IDCLocationNavigator> {

    private final String TAG = DCLocationViewModel.class.getSimpleName();
    double current_lat;
    double current_lng;
    String address;
    @SuppressLint("StaticFieldLeak")
    Context context;
    private Dialog dialog;

    @Inject
    public DCLocationViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider , Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }

    public void onBackClick() {
        getNavigator().onBackPress();
    }

    public void onLocationClick() {
        getNavigator().onLocationClick();
    }

    public void onUploadClick() {
        if (getNavigator().latLongStatus()) {
            getNavigator().showSnackBar(context.getString(R.string.unable_to_get_location_get_location_first), false);
        } else {
            callDCLocationUpdateApiCall();
        }
    }

    public void callDCLocationUpdateApiCall() {
        try {
            showProgressDialog(context, context.getString(R.string.updating_dc_location));
            DCLocationUpdate request = new DCLocationUpdate(address, getDataManager().getCode(), String.valueOf(current_lat), String.valueOf(current_lng));
            getCompositeDisposable().add(getDataManager().doDCLocationUpdateApiCall(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), request).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).
                    subscribe(dcLocationUpdateResponse -> {
                        dismissProgressDialog();
                        try {
                            if(dcLocationUpdateResponse== null){
                                getNavigator().showSnackBar(context.getString(R.string.dc_location_api_response_null), false);
                                return;
                            }
                            String errorMessage = dcLocationUpdateResponse.getDescription() == null || dcLocationUpdateResponse.getDescription().trim().isEmpty() ? context.getString(R.string.dc_location_api_response_false) : dcLocationUpdateResponse.getDescription();
                            if (dcLocationUpdateResponse.getStatus()) {
                                getNavigator().showSnackBar(context.getString(R.string.dc_location_updated_successfully), true);
                                getDataManager().setDcLatitude(String.valueOf(current_lat));
                                getDataManager().setDcLongitude(String.valueOf(current_lng));
                            } else {
                                getNavigator().showSnackBar(errorMessage, false);
                            }
                        } catch (Exception e) {
                            getNavigator().showSnackBar(e.getLocalizedMessage(), false);
                        }
                    }, throwable -> {
                        dismissProgressDialog();
                        getNavigator().showSnackBar(throwable.getLocalizedMessage(), false);
                }));
        } catch (Exception e) {
            dismissProgressDialog();
            getNavigator().showSnackBar(e.getLocalizedMessage(), false);
        }
    }

    public void getAddress(Context context, double lat, double lng) {
        current_lng = lng;
        current_lat = lat;
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            assert addresses != null;
            address = addresses.get(0).getAddressLine(0);
            getNavigator().setCurrentDCAddress(address);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void showProgressDialog(Context context, String loadingMessage) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_progress_dialog);
        dialog.setCancelable(false);
        TextView loadingText = dialog.findViewById(R.id.dialog_loading_text);
        loadingText.setText(loadingMessage);
        dialog.show();
    }

    public void dismissProgressDialog(){
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }
}