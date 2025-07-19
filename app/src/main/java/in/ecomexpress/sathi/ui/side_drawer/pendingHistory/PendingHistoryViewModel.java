package in.ecomexpress.sathi.ui.side_drawer.pendingHistory;

import android.app.Application;
import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

@HiltViewModel
public class PendingHistoryViewModel extends BaseViewModel<IPendingHistoryNavigator> {

    @Inject
    public PendingHistoryViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider , Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }

    private static final String TAG = PendingHistoryViewModel.class.getSimpleName();
    ArrayList<Long> list_of_awbs = new ArrayList<>();

    public void getAllDataPushApi() {
        try {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(getDataManager().getAllPushApiData().
                subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().io())
                .subscribe(pushApiList -> {
                    for(int i = 0; i <pushApiList.size() ; i++){
                        if(pushApiList.get(i).getShipmentStatus() == 2 || pushApiList.get(i).getShipmentStatus() == 3) {
                            deleteData(pushApiList.get(i).getAwbNo());
                            pushApiList.remove(i);
                        }
                    }
                    getNavigator().setPacketData(pushApiList);
            }));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void getAllForward() {
        try {
            list_of_awbs.clear();
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(getDataManager().getUnSyncForwardList().
                subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().io())
                .subscribe(drsForwardTypeResponseList -> {
                    ArrayList<Long> awbs = new ArrayList<>();
                    for(int i = 0; i <drsForwardTypeResponseList.size() ; i++){
                        awbs.add(drsForwardTypeResponseList.get(i).getAwbNo());
                    }
                    list_of_awbs.addAll(awbs);
                    getAllRVP(list_of_awbs);
            }));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void getAllEds(ArrayList<Long> list_of_awbs) {
        try {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(getDataManager().getUnSyncEdsList().
                subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().io())
                .subscribe(drsForwardTypeResponseList -> {
                    ArrayList<Long> awbs = new ArrayList<>();
                    for(int i = 0; i <drsForwardTypeResponseList.size() ; i++){
                        awbs.add(drsForwardTypeResponseList.get(i).awbNo);
                    }
                    list_of_awbs.addAll(awbs);
                    updatePushToZero(list_of_awbs);
                }));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void getAllRVP(ArrayList<Long> list_of_awbs) {
        try {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(getDataManager().getUnSyncRVPList().
                subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().io())
                .subscribe(drsForwardTypeResponseList -> {
                    ArrayList<Long> awbs = new ArrayList<>();
                    for(int i = 0; i <drsForwardTypeResponseList.size() ; i++){
                        awbs.add(drsForwardTypeResponseList.get(i).getAwbNo());
                    }
                    list_of_awbs.addAll(awbs);
                    getAllEds(list_of_awbs);
                }));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void updatePushToZero(ArrayList<Long> awbNo) {
        try {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(getDataManager().updatePushSyncStatusToZero(awbNo).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(aBoolean -> getAllDataPushApi()));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void deleteData(long awb_no){
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager().deleteSyncedFWD(awb_no).subscribe(aBoolean -> {}));
    }
}