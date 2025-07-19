package in.ecomexpress.sathi.ui.dashboard.fe_earned;

import android.app.Application;
import android.app.ProgressDialog;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.EarningApiRequest;
import in.ecomexpress.sathi.repo.remote.model.EarningApiResponse;
import in.ecomexpress.sathi.repo.remote.model.FormData;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class EarnedViewModel extends BaseViewModel<IEarnedNavigator> {

    @Inject
    public EarnedViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider , Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }

    private final MediatorLiveData<EarningApiResponse> earningApiResponseMutableLiveData = new MediatorLiveData<>();

    public MediatorLiveData<EarningApiResponse> getEarningApiResponseMutableLiveData() {
        return earningApiResponseMutableLiveData;
    }

    public void doTrainingAPICall() {
        ProgressDialog dialog;
        dialog = new ProgressDialog(getNavigator().getActivityContext(),android.R.style.Theme_Material_Light_Dialog);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading View....");
        dialog.show();
        try{
            String authToken = "Bearer PE9369Pf5Y3fRayUfgdFj1bbImaomyGbO4P4bmCdpclpdLxJ8rphl1OhhJDDtH7dYGwapBKI1eH67TDdMdDuAymm5DEmRKlLQaMizWU0Zala71m3j0MhKZvSi7Hoxaux";
            EarningApiRequest earningApiRequest = getEarningApiRequest();
            LiveData<EarningApiResponse> obj = getDataManager().doTrainingAPICall(authToken, earningApiRequest);
            earningApiResponseMutableLiveData.addSource(obj, selfDropResponse -> {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                earningApiResponseMutableLiveData.removeSource(obj);
                earningApiResponseMutableLiveData.setValue(selfDropResponse);
            });
        } catch (Exception e){
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    @NonNull
    private EarningApiRequest getEarningApiRequest() {
        FormData.CustomProperties customProperties=new FormData.CustomProperties();
        customProperties.setToken(getDataManager().getAuthToken());
        customProperties.setEmployee_id(getDataManager().getEmp_code());

        FormData formData=new FormData();
        formData.setUsername(getDataManager().getEmp_code());
        formData.setName(getDataManager().getName());
        formData.setCustomProperties(customProperties);

        EarningApiRequest earningApiRequest= new EarningApiRequest();
        earningApiRequest.setIdentityProviderId("66a231770f0285b266856ec7");
        earningApiRequest.setFormData(formData);
        return earningApiRequest;
    }
}