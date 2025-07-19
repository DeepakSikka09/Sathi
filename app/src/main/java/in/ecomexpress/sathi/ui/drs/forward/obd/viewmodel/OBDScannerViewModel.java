package in.ecomexpress.sathi.ui.drs.forward.obd.viewmodel;

import android.app.Application;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.RvpFlyerDuplicateCheckRequest;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.ui.drs.forward.obd.navigator.FlyerScanNavigator;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class OBDScannerViewModel extends BaseViewModel<FlyerScanNavigator> {


    @Inject
    public OBDScannerViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application application) {
        super(dataManager, schedulerProvider, application);
    }

    public void getRvpFlyerDuplicateCheck(String ref_packing_barcode,String awbNo, String drsID, Boolean scanCodeStatus) {
        setIsLoading(true);
        try {
            RvpFlyerDuplicateCheckRequest rvpflyerDuplicateCheckRequest = new RvpFlyerDuplicateCheckRequest();
            rvpflyerDuplicateCheckRequest.setAwb(awbNo);
            rvpflyerDuplicateCheckRequest.setDrs_id(drsID);
            rvpflyerDuplicateCheckRequest.setRef_packaging_barcode(ref_packing_barcode);
            getCompositeDisposable().add(getDataManager().doRvpflyerDuplicateCheck(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), rvpflyerDuplicateCheckRequest).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).
                    subscribe(rvpflyerDuplicateCheckResponse -> {
                        setIsLoading(false);
                        try {
                            if (rvpflyerDuplicateCheckResponse.getStatus().matches("true")) {

                                getNavigator().errorMsg(rvpflyerDuplicateCheckResponse.getDescription());

                            } else {
                                getNavigator().onProceed();
                            }
                        } catch (Exception e) {
                            Logger.e(OBDScannerViewModel.class.getName(), e.getMessage());
                        }
                    }, throwable -> {
                        try {
                            setIsLoading(false);
                        } catch (Exception e) {
                            Logger.e(OBDScannerViewModel.class.getName(), e.getMessage());
                        }
                    }));
        } catch (Exception e) {
            Logger.e(OBDScannerViewModel.class.getName(), e.getMessage());
            writeErrors(System.currentTimeMillis(), e);
            setIsLoading(false);
        }
    }


}
