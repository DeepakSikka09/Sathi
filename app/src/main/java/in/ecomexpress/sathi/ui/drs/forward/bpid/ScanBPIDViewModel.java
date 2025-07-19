package in.ecomexpress.sathi.ui.drs.forward.bpid;


import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.ObservableField;
import com.nlscan.android.scan.ScanManager;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class ScanBPIDViewModel extends BaseViewModel<IScanBPNavigator> {
    Dialog ScanAlertdialog;

    private BroadcastReceiver mReceiver;
    private final ObservableField<Boolean> image = new ObservableField<>();
    public ObservableField<Boolean> getImage() {
        return image;
    }

    public void setImage(Boolean image) {
        this.image.set(image);
    }

    @Inject
    public ScanBPIDViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication){
        super(dataManager, schedulerProvider, sathiApplication);
    }

    BroadcastReceiver mResultReceiver(){
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent){
                String action = intent.getAction();
                if(ScanManager.ACTION_SEND_SCAN_RESULT.equals(action)){
                    byte[] bvalue = intent.getByteArrayExtra(ScanManager.EXTRA_SCAN_RESULT_ONE_BYTES);
                    String sValue = intent.getStringExtra("SCAN_BARCODE1");
                    getNavigator().mResultReceiver1(sValue);
                    try{
                        if(sValue == null && bvalue != null)
                            sValue = new String(bvalue, "GBK");
                        sValue = sValue == null ? "" : sValue;
                    } catch(Exception e){
                        Logger.e(ScanBPIDViewModel.class.getName(),e.getMessage());

                    }
                }
            }
        };
        return mReceiver;
    }


    public void doScanAgainAlert(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context,android.R.style.Theme_Material_Light_Dialog);
        builder.setMessage(R.string.please_scan_valid_bar_code_of_this_manifest);
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            dialogInterface.cancel();
            getNavigator().showScanAlert();
        });
        ScanAlertdialog = builder.create();
        ScanAlertdialog.show();
    }
    /*public void onBackClick(){
        getNavigator().onBackClick();
    }*/
}
