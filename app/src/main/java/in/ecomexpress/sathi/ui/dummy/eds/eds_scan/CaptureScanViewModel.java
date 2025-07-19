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
import io.reactivex.functions.Consumer;
import in.ecomexpress.sathi.repo.local.data.eds.EdsCommit;

@HiltViewModel
public class CaptureScanViewModel extends BaseViewModel<CaptureScanNavigator> {
    EdsCommit edsCommit;
    private ScanManager mScanMgr;
    public static String device = (Build.MANUFACTURER + ":" + Build.MODEL).toUpperCase(Locale.US);

    int outputMode = -1;
    private BroadcastReceiver mReceiver;
    private final ObservableField<String> awbNo = new ObservableField<>();
    private final ObservableField<String> itemName = new ObservableField<>();
    private final ObservableField<String> codeOpen = new ObservableField<>();
    private final ObservableField<String> codeClose = new ObservableField<>();
    private final ObservableField<Boolean> scanCodeOpen = new ObservableField<>();
    private final ObservableField<Boolean> scanCodeClose = new ObservableField<>();
    private final ObservableField<Boolean> imageOpen = new ObservableField<>();
    private final ObservableField<Boolean> imageClose = new ObservableField<>();

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

    public ObservableField<Boolean> getImageClose() {
        return imageClose;
    }

    public void setImageClose(Boolean imageClose) {
        this.imageClose.set(imageClose);
    }

    public ObservableField<String> getAwbNo() {
        return awbNo;
    }

    public ObservableField<String> getItemName() {
        return itemName;
    }

    public ObservableField<String> getCodeOpen() {
        return codeOpen;
    }

    public void setCodeOpen(String code) {
        this.codeOpen.set(code);
    }

    public ObservableField<String> getCodeClose() {
        return codeClose;
    }

    public void setCodeClose(String code) {
        this.codeClose.set(code);
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

    public void captureImageAfterPackging() {
        getNavigator().captureImageAfterPackaging();
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
        getCompositeDisposable().add(getDataManager().saveImage(imageModel).
                subscribeOn(getSchedulerProvider().io()).
                observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {

            }
        }));

        }catch (Exception e){
            e.printStackTrace();
            getNavigator().showEror(e.getMessage());
        }
    }

    BroadcastReceiver mResultReceiver() {
        mReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ScanManager.ACTION_SEND_SCAN_RESULT.equals(action)) {
                    byte[] bvalue = intent.getByteArrayExtra(
                            ScanManager.EXTRA_SCAN_RESULT_ONE_BYTES);
                    String sValue = intent.getStringExtra("SCAN_BARCODE1");
                    getNavigator().mResultReceiverScan(sValue);

                    try {
                        if (sValue == null && bvalue != null)
                            sValue = new String(bvalue, "GBK");
                        sValue = sValue == null ? "" : sValue;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        return mReceiver;
    }
}
