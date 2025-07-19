package in.ecomexpress.sathi.ui.drs.forward.shipmentearndialog;

import android.app.Application;

import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;




public class ShipmentEarnDialogViewModel extends BaseViewModel<ShipmentEarnDialogCallBack> {


    public ShipmentEarnDialogViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider , Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }


    public void onOkClick() {

        getNavigator().onDismiss();
    }





}
