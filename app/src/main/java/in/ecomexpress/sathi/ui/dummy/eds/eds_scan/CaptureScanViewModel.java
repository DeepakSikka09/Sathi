package in.ecomexpress.sathi.ui.dummy.eds.eds_scan;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.ObservableField;
import android.os.Build;
import com.nlscan.android.scan.ScanManager;
import java.util.Calendar;
import java.util.Locale;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.db.model.ImageModel;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class CaptureScanViewModel extends BaseViewModel<CaptureScanNavigator> {

    public static String device = (Build.MANUFACTURER + ":" + Build.MODEL).toUpperCase(Locale.US);
    private final ObservableField<String> awbNo = new ObservableField<>();
    private final ObservableField<String> itemName = new ObservableField<>();
    private final ObservableField<Boolean> scanCodeOpen = new ObservableField<>();
    private final ObservableField<Boolean> scanCodeClose = new ObservableField<>();
    private final ObservableField<Boolean> imageOpen = new ObservableField<>();

    public ObservableField<Boolean> getScanCodeOpen() {
        return scanCodeOpen;
    }

    public void setScanCodeOpen(Boolean codeOpen) {
        this.scanCodeOpen.set(codeOpen);
    }

    public ObservableField<Boolean> getScanCodeClose() {
        return scanCodeClose;
    }

    public void setScanCodeClose(Boolean codeClose) {
        this.scanCodeClose.set(codeClose);
    }

    public ObservableField<Boolean> getImageOpen() {
        return imageOpen;
    }

    public void setImageOpen(Boolean imageOpen) {
        this.imageOpen.set(imageOpen);
    }

    public ObservableField<String> getAwbNo() {
        return awbNo;
    }

    public ObservableField<String> getItemName() {
        return itemName;
    }

    @Inject
    public CaptureScanViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider , Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }

    public void captureImageBeforePackging() {
        getNavigator().captureImageBeforePackaging();
    }

    public void scanCodeBeforePackging() {
        getNavigator().scanCodeBeforePackaging();
    }

    public void scanCodeAfterPackging() {
        getNavigator().scanCodeAfterPackaging();
    }

    public void OnProceed() {
        getNavigator().onProceed();
    }

    public void onBackClick() {
        getNavigator().onBack();
    }

    public void saveImageDB(String imageUri, String imageCode, String name, EDSResponse edsResponse) {
        try {
            ImageModel imageModel = new ImageModel();
            imageModel.setDraNo(String.valueOf(edsResponse.getDrsNo()));
            imageModel.setAwbNo(String.valueOf(edsResponse.getAwbNo()));
            imageModel.setImageName(name);
            imageModel.setImage(imageUri);
            imageModel.setImageCode(imageCode);
            imageModel.setStatus(0);
            imageModel.setImageId(-1);
            imageModel.setImageCurrentSyncStatus(GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO);
            imageModel.setImageFutureSyncTime(Calendar.getInstance().getTimeInMillis());
            imageModel.setDate(Calendar.getInstance().getTimeInMillis());
            imageModel.setShipmentType(GlobalConstant.ShipmentTypeConstants.EDS);
            imageModel.setImageType(GlobalConstant.ShipmentTypeConstants.OTHER);
            getCompositeDisposable().add(getDataManager().saveImage(imageModel).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {}));
        } catch (Exception e){
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
    }

    BroadcastReceiver mResultReceiver() {
        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ScanManager.ACTION_SEND_SCAN_RESULT.equals(action)) {
                    String sValue = intent.getStringExtra("SCAN_BARCODE1");
                    getNavigator().mResultReceiverScan(sValue);
                }
            }
        };
        return mReceiver;
    }
}