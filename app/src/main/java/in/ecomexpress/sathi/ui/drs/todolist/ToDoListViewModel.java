package in.ecomexpress.sathi.ui.drs.todolist;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.databinding.ItemEdsListViewBinding;
import in.ecomexpress.sathi.databinding.ItemForwardListViewBinding;
import in.ecomexpress.sathi.databinding.ItemRvpListViewBinding;
import in.ecomexpress.sathi.databinding.ItemRvpMpsListViewBinding;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.data.callbridge.CallApiRequest;
import in.ecomexpress.sathi.repo.local.db.model.CommonDRSListItem;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.local.db.model.LiveTrackingLogTable;
import in.ecomexpress.sathi.repo.local.db.model.RVPQCImageTable;
import in.ecomexpress.sathi.repo.local.db.model.Remark;
import in.ecomexpress.sathi.repo.local.db.model.RescheduleEdsD;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.call.Call;
import in.ecomexpress.sathi.repo.remote.model.consignee_profile.ProfileFound;
import in.ecomexpress.sathi.repo.remote.model.drs_list.DRSSequence;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.IRTSBaseInterface;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.forward.reassign.FWDReassignRequest;
import in.ecomexpress.sathi.repo.remote.model.masterdata.CallbridgeConfiguration;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.repo.remote.model.mps.DRSRvpQcMpsResponse;
import in.ecomexpress.sathi.repo.remote.model.sms.Additional_info;
import in.ecomexpress.sathi.repo.remote.model.sms.SmsRequest;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.DRSListSorter;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.PreferenceUtils;
import in.ecomexpress.sathi.utils.TimeUtils;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

@HiltViewModel
public class ToDoListViewModel extends BaseViewModel<IToDoListNavigator> {

    private static final String TAG = ToDoListViewModel.class.getSimpleName();
    public static String device = (Build.MANUFACTURER + ":" + Build.MODEL).toUpperCase(Locale.US);
    private final ArrayList<String> getRemarkAwb = new ArrayList<>();
    public final ObservableField<String> consigneeContactNumber = new ObservableField<>("");
    private final ObservableField<ArrayList<MasterActivityData>> masterActivityDataList = new ObservableField<>();
    private final List<String> header = new ArrayList<>();
    private final List<String> childListDC = new ArrayList<>();
    private final List<String> childListDV = new ArrayList<>();
    private final List<Boolean> option = new ArrayList<>();
    private final List<String> childListActivity = new ArrayList<>();
    private final List<String> listHeaderActivity = new ArrayList<>();
    private final List<String> listHeaderDV = new ArrayList<>();
    private final List<String> listHeaderDC = new ArrayList<>();
    private final LinkedHashMap<String, List<String>> childList = new LinkedHashMap<>();
    private final LinkedHashMap<String, Boolean> childList_optional_flag = new LinkedHashMap<>();
    private final LinkedHashMap<String, String> code_name = new LinkedHashMap<>();
    public ObservableField<EdsWithActivityList> edsWithActivityList = new ObservableField<>();
    public ObservableField<Long> forwardTotalCount = new ObservableField<>();
    public ObservableField<Long> rtsTotalCount = new ObservableField<>();
    public ObservableField<Long> rvpTotalCount = new ObservableField<>();
    public ObservableField<Long> edsTotalCount = new ObservableField<>();
    public ObservableField<String> totalAssignedCount = new ObservableField<>("");
    public ObservableField<String> totalDeliveredCount = new ObservableField<>("");
    public ObservableField<String> totalUndeliveredCount = new ObservableField<>("");
    public ObservableBoolean remark_one = new ObservableBoolean(false);
    public ObservableBoolean remark_two = new ObservableBoolean(false);
    public ObservableBoolean remark_three = new ObservableBoolean(false);
    public ObservableBoolean remark_four = new ObservableBoolean(false);
    public ObservableBoolean remark_none = new ObservableBoolean(false);
    public ObservableBoolean is_pending = new ObservableBoolean(false);
    public ObservableBoolean is_successful = new ObservableBoolean(false);
    public ObservableBoolean is_unsuccessful = new ObservableBoolean(false);
    public List<DRSSequence> drsSequenceListFromDB;
    CallbridgeConfiguration myMasterDataReasonCodeResponse = null;
    Set<String> awbhashset = new HashSet<>();
    ArrayList<String> myGetSelectedAwb = new ArrayList<>();
    SmsRequest smsRequest;
    Additional_info addInfo;
    List<Boolean> filterStatusList = new ArrayList<>();
    boolean setData = false;
    ObservableField<List<CommonDRSListItem>> listShipmentObserver = new ObservableField<>();
    boolean flag = true;
    List<Boolean> flagList;
    List<EDSResponse> edsResponseForDialog = new ArrayList<>();
    Dialog edsAcitivitesdialog;
    private List<CommonDRSListItem> commonDRSListItemList;
    private List<RescheduleEdsD> rescheduleEdsList;
    private boolean[] mItemSelectedDataStatus = new boolean[]{true, true, true, true};
    private boolean[] mItemSelectedDataRemark = new boolean[]{true, true, true, true, true};
    private ObservableField<String> codCashCount = new ObservableField<>("");
    private ObservableField<String> ecodCashCount = new ObservableField<>("");
    private final HashMap<Long, String> qc_value_map = new LinkedHashMap<>();

    @Inject
    public ToDoListViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application application) {
        super(dataManager, schedulerProvider, application);
    }

    public ObservableField<String> getCodCashCount() {
        return codCashCount;
    }

    public void setCodCashCount(ObservableField<String> codCashCount) {
        this.codCashCount = codCashCount;
    }

    public ObservableField<String> getEcodCashCount() {
        return ecodCashCount;
    }

    public void setEcodCashCount(ObservableField<String> ecodCashCount) {
        this.ecodCashCount = ecodCashCount;
    }

    public boolean mysetData() {
        return setData;
    }

    public void getForwardDRSList() {
        commonDRSListItemList.clear();
        try {
            getCompositeDisposable().add(Observable.zip(getDataManager().getDRSListForward(), getDataManager().getAllRemarks(getDataManager().getCode(), TimeUtils.getDateYearMonthMillies()), (drsForwardTypeResponses, remarks) -> {
                HashMap<Long, Remark> remarkMap = new HashMap<>();
                for (Remark remark : remarks) {
                    remarkMap.put(remark.awbNo, remark);
                }
                forwardTotalCount.set((long) drsForwardTypeResponses.size());
                List<CommonDRSListItem> commonDRSListItems = new ArrayList<>();
                for (DRSForwardTypeResponse fwd : drsForwardTypeResponses) {
                    CommonDRSListItem item = new CommonDRSListItem(GlobalConstant.ShipmentTypeConstants.FWD, fwd);
                    item.setRemark(remarkMap.get(fwd.getAwbNo()));
                    commonDRSListItems.add(item);
                }
                return commonDRSListItems;
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(commonDRSListItems -> {
                commonDRSListItemList.addAll(commonDRSListItems);
                if (commonDRSListItemList.isEmpty()) {
                    getNavigator().hideLayout(Constants.FWD);
                }
                filterOnStatus();
            }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
        }
    }

    public void getRTSDRSList() {
        commonDRSListItemList.clear();
        try {
            getCompositeDisposable().add(Observable.zip(getDataManager().getDRSListNewRTS(), getDataManager().getAllRemarks(getDataManager().getCode(), TimeUtils.getDateYearMonthMillies()), (drsReturnToShipperTypeNewResponses, remarks) -> {
                HashMap<Long, Remark> remarkMap = new HashMap<>();
                for (Remark remark : remarks) {
                    remarkMap.put(remark.awbNo, remark);
                }
                rtsTotalCount.set((long) drsReturnToShipperTypeNewResponses.getCombinedList().size());
                List<CommonDRSListItem> commonDRSListItems = new ArrayList<>();
                for (IRTSBaseInterface irtsBaseInterface : drsReturnToShipperTypeNewResponses.getCombinedList()) {
                    CommonDRSListItem item = new CommonDRSListItem(GlobalConstant.ShipmentTypeConstants.RTS, irtsBaseInterface);
                    commonDRSListItems.add(item);
                    item.setRemark(remarkMap.get(irtsBaseInterface.getDetails().getId()));
                    commonDRSListItems.add(item);
                }
                return commonDRSListItems;
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(commonDRSListItems -> {
                commonDRSListItemList.addAll(commonDRSListItems);
                filterOnStatus();
                if (commonDRSListItemList.isEmpty()) {
                    getNavigator().hideLayout(Constants.RTS);
                }
            }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
        }
    }

    public void getAllNewDRS() {
        try {
            getCompositeDisposable().add(Observable.zip(getDataManager().getDRSListForward(), getDataManager().getDRSListNewRTS(), getDataManager().getDRSListRVP(), getDataManager().getDRSListRVPMPS(), getDataManager().getDrsListNewEds(), getDataManager().getAllDRSSequenceData(), getDataManager().getAllRemarks(getDataManager().getCode(), TimeUtils.getDateYearMonthMillies()), getDataManager().getEdsRescheduleFlag(), getDataManager().getProfileFound(), (drsForwardTypeResponses, drsReturnToShipperTypeNewResponses, drsReverseQCTypeResponses, drsRvpQcMpsResponses, edsResponses, drsSequenceList, remarks, rescheduleEdsDS, profileFoundLists) -> {
                HashMap<Long, ProfileFound> profileMap = new HashMap<>();
                for (ProfileFound profileFound : profileFoundLists) {
                    profileMap.put(profileFound.getAwb_number(), profileFound);
                }
                rescheduleEdsList = rescheduleEdsDS;
                HashMap<Long, Remark> remarkMap = new HashMap<>();
                for (Remark remark : remarks) {
                    remarkMap.put(remark.awbNo, remark);
                    if (remark.remark.equalsIgnoreCase(GlobalConstant.RemarksTypeConstants.CONSIGNEE_NOT_PICKING_CALL)) {
                        remark_one.set(true);
                    } else if (remark.remark.equalsIgnoreCase(GlobalConstant.RemarksTypeConstants.CONSIGNEE_REQUESTED_TO_CALL_LATER)) {
                        remark_two.set(true);
                    } else if (remark.remark.contains(GlobalConstant.RemarksTypeConstants.SAME_DAY_RESCHEDULE)) {
                        remark_three.set(true);
                    } else if (remark.remark.equalsIgnoreCase(GlobalConstant.RemarksTypeConstants.CONSIGNEE_CALLED)) {
                        remark_four.set(true);
                    }
                }
                forwardTotalCount.set((long) drsForwardTypeResponses.size());
                rtsTotalCount.set((long) drsReturnToShipperTypeNewResponses.getCombinedList().size());
                rvpTotalCount.set((long) drsReverseQCTypeResponses.size() + drsRvpQcMpsResponses.size());
                edsTotalCount.set((long) edsResponses.size());
                List<CommonDRSListItem> commonDRSListItems = new ArrayList<>();
                for (DRSForwardTypeResponse fwd : drsForwardTypeResponses) {
                    CommonDRSListItem item = new CommonDRSListItem(GlobalConstant.ShipmentTypeConstants.FWD, fwd);
                    item.setRemark(remarkMap.get(fwd.getAwbNo()));
                    item.setProfileFound(profileMap.get(fwd.getAwbNo()));
                    commonDRSListItems.add(item);
                }
                for (IRTSBaseInterface rts : drsReturnToShipperTypeNewResponses.getCombinedList()) {
                    CommonDRSListItem item = new CommonDRSListItem(GlobalConstant.ShipmentTypeConstants.RTS, rts);
                    item.setRemark(remarkMap.get(rts.getDetails().getId()));
                    commonDRSListItems.add(item);
                }
                for (DRSReverseQCTypeResponse rvp : drsReverseQCTypeResponses) {
                    CommonDRSListItem item = new CommonDRSListItem(GlobalConstant.ShipmentTypeConstants.RVP, rvp);
                    item.setRemark(remarkMap.get(rvp.getAwbNo()));
                    item.setProfileFound(profileMap.get(rvp.getAwbNo()));
                    commonDRSListItems.add(item);
                }
                for (DRSRvpQcMpsResponse rvpMps : drsRvpQcMpsResponses) {
                    CommonDRSListItem item = new CommonDRSListItem(GlobalConstant.ShipmentTypeConstants.RVP_MPS, rvpMps);
                    item.setRemark(remarkMap.get(rvpMps.getAwbNo()));
                    item.setProfileFound(profileMap.get(rvpMps.getAwbNo()));
                    commonDRSListItems.add(item);
                }
                for (EDSResponse eds : edsResponses) {
                    edsResponseForDialog = edsResponses;
                    CommonDRSListItem item = new CommonDRSListItem(GlobalConstant.ShipmentTypeConstants.EDS, eds);
                    item.setRemark(remarkMap.get(eds.getAwbNo()));
                    item.setProfileFound(profileMap.get(eds.getAwbNo()));
                    item.setRescheduleFlagFound(rescheduleEdsList);
                    commonDRSListItems.add(item);
                }
                commonDRSListItemList = commonDRSListItems;
                drsSequenceListFromDB = drsSequenceList;
                if (drsSequenceListFromDB.isEmpty()) {
                    getNavigator().setResetSequenceVisible(false);
                    ToDoListActivity.SORTING_TECHNIQUE = ToDoListActivity.SORTING_DRS_NO;
                    applyDRSSequenceNoSorting(commonDRSListItems);
                    saveDRSSequenceToTable(commonDRSListItemList);
                    getNavigator().setDrsData(commonDRSListItemList);
                } else {
                    getNavigator().setResetSequenceVisible(true);
                    ToDoListActivity.SORTING_TECHNIQUE = ToDoListActivity.SORTING_SAVED_SEQUENCE;
                    setDRSListAsPerSavedSequence(commonDRSListItemList, drsSequenceListFromDB);
                }
                return commonDRSListItems;
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(commonDRSListItems -> {
                if (!commonDRSListItems.isEmpty()) {
                    listShipmentObserver.set(commonDRSListItems);
                    filterOnStatus();
                    getNavigator().toolbarVisibility(commonDRSListItems);
                    getNavigator().updateExpandableAdapter();
                    updateCountLayoutVisibility();
                    setNoRemarkStatus(commonDRSListItems);
                    setData = true;
                }
            }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
        }
    }

    private void applyDRSSequenceNoSorting(List<CommonDRSListItem> commonDRSListItems) {
        if (commonDRSListItems == null || commonDRSListItems.isEmpty()) {
            return;
        }
        List<CommonDRSListItem> list;
        if (ToDoListActivity.SORTING_TECHNIQUE == ToDoListActivity.SORTING_LOCATION) {
            list = DRSListSorter.sort(commonDRSListItems, DRSListSorter.TECHNIQUE.DISTANCE);
        } else {
            list = DRSListSorter.sort(commonDRSListItems, DRSListSorter.TECHNIQUE.DRS_NO);
        }
        commonDRSListItemList.clear();
        commonDRSListItemList.addAll(list);
    }

    @SuppressLint("CheckResult")
    public void saveDRSSequenceToTable(List<CommonDRSListItem> commonDRSListItemList) {
        List<DRSSequence> drsSequenceList = new ArrayList<>();
        Observable.fromCallable(() -> {
            for (int i = 0; i < commonDRSListItemList.size(); i++) {
                try {
                    DRSSequence drsSequenceObj = new DRSSequence();
                    CommonDRSListItem drsItem = commonDRSListItemList.get(i);
                    String drsType = drsItem.getType();
                    switch (drsType) {
                        case GlobalConstant.ShipmentTypeConstants.FWD:
                            drsSequenceObj.setSequence_no((long) i);
                            drsSequenceObj.setCompositeKey(drsItem.getDrsForwardTypeResponse().getCompositeKey());
                            drsSequenceObj.setAwbNo(drsItem.getDrsForwardTypeResponse().getAwbNo());
                            drsSequenceObj.setShipmentStatus(drsItem.getDrsForwardTypeResponse().getShipmentStatus());
                            drsSequenceObj.setShipmentSyncStatus(drsItem.getDrsForwardTypeResponse().getShipmentSyncStatus());
                            break;
                        case GlobalConstant.ShipmentTypeConstants.EDS:
                            drsSequenceObj.setSequence_no((long) i);
                            drsSequenceObj.setCompositeKey(drsItem.getEdsResponse().getCompositeKey());
                            drsSequenceObj.setAwbNo(drsItem.getEdsResponse().getAwbNo());
                            drsSequenceObj.setShipmentStatus(drsItem.getEdsResponse().getShipmentStatus());
                            drsSequenceObj.setShipmentSyncStatus(drsItem.getEdsResponse().getShipmentSyncStatus());
                            break;
                        case GlobalConstant.ShipmentTypeConstants.RTS:
                            drsSequenceObj.setSequence_no((long) i);
                            drsSequenceObj.setAwbNo(commonDRSListItemList.get(i).getIRTSInterface().getDetails().getId());
                            drsSequenceObj.setCompositeKey(String.valueOf(commonDRSListItemList.get(i).getIRTSInterface().getDetails().getId()));
                            drsSequenceObj.setShipmentStatus(commonDRSListItemList.get(i).getIRTSInterface().getDetails().getShipmentStatus());
                            drsSequenceObj.setShipmentSyncStatus(commonDRSListItemList.get(i).getIRTSInterface().getDetails().getShipmentSyncStatus());
                            break;
                        case GlobalConstant.ShipmentTypeConstants.RVP:
                            drsSequenceObj.setSequence_no((long) i);
                            drsSequenceObj.setCompositeKey(drsItem.getDrsReverseQCTypeResponse().getCompositeKey());
                            drsSequenceObj.setAwbNo(drsItem.getDrsReverseQCTypeResponse().getAwbNo());
                            drsSequenceObj.setShipmentStatus(drsItem.getDrsReverseQCTypeResponse().getShipmentStatus());
                            drsSequenceObj.setShipmentSyncStatus(drsItem.getDrsReverseQCTypeResponse().getShipmentSyncStatus());
                            break;
                        case GlobalConstant.ShipmentTypeConstants.RVP_MPS:
                            drsSequenceObj.setSequence_no((long) i);
                            drsSequenceObj.setCompositeKey(drsItem.getDrsRvpQcMpsResponse().getCompositeKey());
                            drsSequenceObj.setAwbNo(drsItem.getDrsRvpQcMpsResponse().getAwbNo());
                            drsSequenceObj.setShipmentStatus(drsItem.getDrsRvpQcMpsResponse().getShipmentStatus());
                            drsSequenceObj.setShipmentSyncStatus(drsItem.getDrsRvpQcMpsResponse().getShipmentSyncStatus());
                            break;
                        default:
                            break;
                    }
                    drsSequenceList.add(drsSequenceObj);
                } catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                }
            }
            return commonDRSListItemList;
        }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(edsResponsesList -> {
            try {
                getCompositeDisposable().add(getDataManager().deleteAllDataDRSSequence().observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> getCompositeDisposable().add(getDataManager().saveToDRSSequenceTable(drsSequenceList).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean1 -> getCompositeDisposable().add(getDataManager().getAllDRSSequenceData().observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(drsSequenceList1 -> {
                    drsSequenceListFromDB = drsSequenceList1;
                    getNavigator().setResetSequenceVisible(true);
                })), throwable -> {
                }))));
            } catch (Exception e) {
                getNavigator().onErrorMsg(e.getMessage());
            }
        });
    }

    public void setDRSListAsPerSavedSequence(List<CommonDRSListItem> commonDRSListItemList, List<DRSSequence> drsSequencesList) {
        try {
            if (drsSequencesList.size() <= commonDRSListItemList.size()) {
                if (!ifIsNewDRSInTable(drsSequencesList, commonDRSListItemList)) {
                    List<CommonDRSListItem> oldDRSItem = getSortedDRSAsPerSequence(drsSequencesList, commonDRSListItemList);
                    List<CommonDRSListItem> commitPackage = extractCommitPackage(oldDRSItem);
                    List<CommonDRSListItem> pendingDRSPackage = extractPendingPackage(oldDRSItem);
                    List<CommonDRSListItem> tempList = new ArrayList<>();
                    tempList.addAll(pendingDRSPackage);
                    tempList.addAll(commitPackage);
                    commonDRSListItemList.clear();
                    commonDRSListItemList.addAll(tempList);
                    saveDRSSequenceToTable(commonDRSListItemList);
                } else {
                    //New DRS are in the table with old DRS:-
                    List<CommonDRSListItem> newDRSItems = extractNewDRSFromCurrentList(commonDRSListItemList, drsSequencesList);
                    markDRSAsNewOrOldFlag(newDRSItems, true);
                    List<CommonDRSListItem> oldDRSItem = getSortedDRSAsPerSequence(drsSequencesList, commonDRSListItemList);
                    List<CommonDRSListItem> commitPackage = extractCommitPackage(oldDRSItem);
                    List<CommonDRSListItem> pendingDRSPackage = extractPendingPackage(oldDRSItem);
                    List<CommonDRSListItem> tempList = new ArrayList<>();
                    tempList.addAll(pendingDRSPackage);
                    tempList.addAll(newDRSItems);
                    tempList.addAll(commitPackage);
                    commonDRSListItemList.clear();
                    commonDRSListItemList.addAll(tempList);
                    saveDRSSequenceToTable(commonDRSListItemList);
                }
                getNavigator().setDrsData(commonDRSListItemList);
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void filterOnStatus() {
        List<CommonDRSListItem> commonDRSList = new ArrayList<>();
        try {
            Observable.just(commonDRSListItemList).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).flatMapIterable((Function<List<CommonDRSListItem>, List<CommonDRSListItem>>) v -> v).filter(commonDRSListItem -> isAddToList(commonDRSListItem, mItemSelectedDataStatus[0], mItemSelectedDataStatus[1], mItemSelectedDataStatus[2])).subscribe(new Observer<CommonDRSListItem>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(CommonDRSListItem commonDRSListItem) {
                    commonDRSList.add(commonDRSListItem);
                }

                @Override
                public void onError(Throwable e) {
                    Logger.e(TAG, String.valueOf(e));
                }

                @Override
                public void onComplete() {
                    commonDRSListItemList = commonDRSList;
                    for (boolean b : mItemSelectedDataRemark) {
                        if (b) {
                            filterDataRemark();
                            break;
                        }
                    }
                }
            });
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
        }
    }

    public void updateCountLayoutVisibility() {
        if (forwardTotalCount.get() == 0) {
            getNavigator().hideLayout(Constants.FWD);
        } else {
            getNavigator().showLayout(Constants.FWD);
        }
        if (rtsTotalCount.get() == 0) {
            getNavigator().hideLayout(Constants.RTS);
        } else {
            getNavigator().showLayout(Constants.RTS);
        }
        if (rvpTotalCount.get() == 0) {
            getNavigator().hideLayout(Constants.RVP);
        } else {
            getNavigator().showLayout(Constants.RVP);
        }
        if (edsTotalCount.get() == 0) {
            getNavigator().hideLayout(Constants.EDS);
        } else {
            getNavigator().showLayout(Constants.EDS);
        }
        getNavigator().ExpandableList();
    }

    public void setNoRemarkStatus(List<CommonDRSListItem> commonDRSListItemList) {
        for (CommonDRSListItem commonDRSListItem : commonDRSListItemList) {
            if (!commonDRSListItem.getType().equals(GlobalConstant.ShipmentTypeConstants.RTS)) {
                if (commonDRSListItem.getRemark() == null || commonDRSListItem.getRemark().remark == null || commonDRSListItem.getRemark().remark.isEmpty()) {
                    remark_none.set(true);
                    break;
                } else {
                    remark_none.set(false);
                }
            }
        }
    }

    private boolean ifIsNewDRSInTable(List<DRSSequence> previousDRSSequenceList, List<CommonDRSListItem> drsExtractedFromTable) {
        List<String> drsSeqAWBList = new ArrayList<>();
        List<String> drsListAWBList = new ArrayList<>();
        for (DRSSequence drsSequence : previousDRSSequenceList) {
            drsSeqAWBList.add(drsSequence.getCompositeKey());
        }
        for (CommonDRSListItem drsListItem : drsExtractedFromTable) {
            String drsType = drsListItem.getType();
            switch (drsType) {
                case GlobalConstant.ShipmentTypeConstants.FWD:
                    drsListAWBList.add(drsListItem.getDrsForwardTypeResponse().getCompositeKey());
                    break;
                case GlobalConstant.ShipmentTypeConstants.EDS:
                    drsListAWBList.add(drsListItem.getEdsResponse().getCompositeKey());
                    break;
                case GlobalConstant.ShipmentTypeConstants.RVP:
                    drsListAWBList.add(drsListItem.getDrsReverseQCTypeResponse().getCompositeKey());
                    break;
                case GlobalConstant.ShipmentTypeConstants.RVP_MPS:
                    drsListAWBList.add(String.valueOf(drsListItem.getDrsRvpQcMpsResponse().getCompositeKey()));
                    break;
                case GlobalConstant.ShipmentTypeConstants.RTS:
                    drsListAWBList.add(String.valueOf(drsListItem.getIRTSInterface().getDetails().getId()));
                    break;
                default:
                    break;
            }
        }
        for (int i = 0; i < drsListAWBList.size(); i++) {
            if (!drsSeqAWBList.contains(drsListAWBList.get(i))) {
                return true;
            }
        }
        return false;
    }

    public List<CommonDRSListItem> getSortedDRSAsPerSequence(List<DRSSequence> drsSequenceList, List<CommonDRSListItem> commonDRSListItemList) {
        List<CommonDRSListItem> tempList = new ArrayList<>();
        for (int i = 0; i < drsSequenceList.size(); i++) {
            DRSSequence drsSequence = drsSequenceList.get(i);
            for (CommonDRSListItem commonDRSListItem : commonDRSListItemList) {
                String type = commonDRSListItem.getType();
                switch (type) {
                    case GlobalConstant.ShipmentTypeConstants.EDS:
                        if (drsSequence.getCompositeKey().equals(commonDRSListItem.getEdsResponse().getCompositeKey())) {
                            tempList.add(commonDRSListItem);
                        }
                        break;
                    case GlobalConstant.ShipmentTypeConstants.FWD:
                        if (drsSequence.getCompositeKey().equals(commonDRSListItem.getDrsForwardTypeResponse().getCompositeKey())) {
                            tempList.add(commonDRSListItem);
                        }
                        break;
                    case GlobalConstant.ShipmentTypeConstants.RVP:
                        if (drsSequence.getCompositeKey().equals(commonDRSListItem.getDrsReverseQCTypeResponse().getCompositeKey())) {
                            tempList.add(commonDRSListItem);
                        }
                        break;
                    case GlobalConstant.ShipmentTypeConstants.RVP_MPS:
                        if (drsSequence.getCompositeKey().equals(String.valueOf(commonDRSListItem.getDrsRvpQcMpsResponse().getCompositeKey()))) {
                            tempList.add(commonDRSListItem);
                        }
                        break;
                    case GlobalConstant.ShipmentTypeConstants.RTS:
                        if (drsSequence.getCompositeKey().equals(String.valueOf(commonDRSListItem.getIRTSInterface().getDetails().getId()))) {
                            tempList.add(commonDRSListItem);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return tempList;
    }

    private List<CommonDRSListItem> extractCommitPackage(List<CommonDRSListItem> oldDRSItem) {
        List<CommonDRSListItem> tempCommitDRS = new ArrayList<>();
        for (CommonDRSListItem tempDrsItem : oldDRSItem) {
            switch (tempDrsItem.getType()) {
                case GlobalConstant.ShipmentTypeConstants.EDS:
                    if (tempDrsItem.getEdsResponse().getShipmentStatus() > 0) {
                        tempCommitDRS.add(tempDrsItem);
                    }
                    break;
                case GlobalConstant.ShipmentTypeConstants.FWD:
                    if (tempDrsItem.getDrsForwardTypeResponse().getShipmentStatus() > 0) {
                        tempCommitDRS.add(tempDrsItem);
                    }
                    break;
                case GlobalConstant.ShipmentTypeConstants.RVP:
                    if (tempDrsItem.getDrsReverseQCTypeResponse().getShipmentStatus() > 0) {
                        tempCommitDRS.add(tempDrsItem);
                    }
                    break;
                case GlobalConstant.ShipmentTypeConstants.RVP_MPS:
                    if (tempDrsItem.getDrsRvpQcMpsResponse().getShipmentStatus() > 0) {
                        tempCommitDRS.add(tempDrsItem);
                    }
                    break;
                case GlobalConstant.ShipmentTypeConstants.RTS:
                    if (tempDrsItem.getIRTSInterface().getDetails().getShipmentStatus() > 0) {
                        tempCommitDRS.add(tempDrsItem);
                    }
                    break;
                default:
                    break;
            }
        }
        return tempCommitDRS;
    }

    private List<CommonDRSListItem> extractPendingPackage(List<CommonDRSListItem> oldDRSItem) {
        List<CommonDRSListItem> tempCommitDRS = new ArrayList<>();
        for (CommonDRSListItem tempDrsItem : oldDRSItem) {
            switch (tempDrsItem.getType()) {
                case GlobalConstant.ShipmentTypeConstants.EDS:
                    if (tempDrsItem.getEdsResponse().getShipmentStatus() == 0) {
                        tempCommitDRS.add(tempDrsItem);
                    }
                    break;
                case GlobalConstant.ShipmentTypeConstants.FWD:
                    if (tempDrsItem.getDrsForwardTypeResponse().getShipmentStatus() == 0) {
                        tempCommitDRS.add(tempDrsItem);
                    }
                    break;
                case GlobalConstant.ShipmentTypeConstants.RVP:
                    if (tempDrsItem.getDrsReverseQCTypeResponse().getShipmentStatus() == 0) {
                        tempCommitDRS.add(tempDrsItem);
                    }
                    break;
                case GlobalConstant.ShipmentTypeConstants.RVP_MPS:
                    if (tempDrsItem.getDrsRvpQcMpsResponse().getShipmentStatus() == 0) {
                        tempCommitDRS.add(tempDrsItem);
                    }
                    break;
                case GlobalConstant.ShipmentTypeConstants.RTS:
                    if (tempDrsItem.getIRTSInterface().getDetails().getShipmentStatus() == 0) {
                        tempCommitDRS.add(tempDrsItem);
                    }
                    break;
                default:
                    break;
            }
        }
        return tempCommitDRS;
    }

    private List<CommonDRSListItem> extractNewDRSFromCurrentList(List<CommonDRSListItem> commonDRSListItemList, List<DRSSequence> drsSequencesList) {
        List<CommonDRSListItem> tempDRSList = new ArrayList<>();
        List<String> drsSeqCompositeList = new ArrayList<>();
        for (DRSSequence drsSequence : drsSequencesList) {
            drsSeqCompositeList.add(drsSequence.getCompositeKey());
        }
        for (CommonDRSListItem drsItem : commonDRSListItemList) {
            String drsType = drsItem.getType();
            switch (drsType) {
                case GlobalConstant.ShipmentTypeConstants.EDS:
                    if (!drsSeqCompositeList.contains(drsItem.getEdsResponse().getCompositeKey())) {
                        tempDRSList.add(drsItem);
                    }
                    break;
                case GlobalConstant.ShipmentTypeConstants.FWD:
                    if (!drsSeqCompositeList.contains(drsItem.getDrsForwardTypeResponse().getCompositeKey())) {
                        tempDRSList.add(drsItem);
                    }
                    break;
                case GlobalConstant.ShipmentTypeConstants.RVP:
                    if (!drsSeqCompositeList.contains(drsItem.getDrsReverseQCTypeResponse().getCompositeKey())) {
                        tempDRSList.add(drsItem);
                    }
                    break;
                case GlobalConstant.ShipmentTypeConstants.RVP_MPS:
                    if (!drsSeqCompositeList.contains(drsItem.getDrsRvpQcMpsResponse().getCompositeKey())) {
                        tempDRSList.add(drsItem);
                    }
                    break;
                case GlobalConstant.ShipmentTypeConstants.RTS:
                    if (!drsSeqCompositeList.contains(String.valueOf(drsItem.getIRTSInterface().getDetails().getId()))) {
                        tempDRSList.add(drsItem);
                    }
                    break;
                default:
                    break;
            }
        }
        return tempDRSList;
    }

    public void markDRSAsNewOrOldFlag(List<CommonDRSListItem> commonDRSListItems, Boolean isNew) {
        for (CommonDRSListItem drsItem : commonDRSListItems) {
            drsItem.setNewDRSAfterSync(isNew);
        }
    }

    private boolean isAddToList(CommonDRSListItem commonDRSListItem, boolean assigned, boolean delivered, boolean undelivered) {
        try {
            switch (commonDRSListItem.getType()) {
                case GlobalConstant.ShipmentTypeConstants.FWD:
                    DRSForwardTypeResponse drsForwardTypeResponse = commonDRSListItem.getDrsForwardTypeResponse();
                    int statusFWD = drsForwardTypeResponse.getShipmentStatus();
                    if (statusFWD == 0) {
                        filterStatusList.add(0, true);
                    }
                    if (statusFWD == 2) {
                        filterStatusList.add(1, true);
                    }
                    if (statusFWD == 3) {
                        filterStatusList.add(2, true);
                    }
                    return statusFilter(statusFWD, assigned, delivered, undelivered);
                case GlobalConstant.ShipmentTypeConstants.RTS:
                    IRTSBaseInterface irtsBaseInterface = commonDRSListItem.getIRTSInterface();
                    int statusRTS = irtsBaseInterface.getDetails().getShipmentStatus();
                    if (statusRTS == 0) {
                        filterStatusList.add(0, true);
                    }
                    if (statusRTS == 2) {
                        filterStatusList.add(1, true);
                    }
                    if (statusRTS == 3) {
                        filterStatusList.add(2, true);
                    }
                    return statusFilter(statusRTS, assigned, delivered, undelivered);
                case GlobalConstant.ShipmentTypeConstants.RVP:
                    DRSReverseQCTypeResponse drsReverseQCTypeResponse = commonDRSListItem.getDrsReverseQCTypeResponse();
                    int statusRVP = drsReverseQCTypeResponse.getShipmentStatus();
                    if (statusRVP == 0) {
                        filterStatusList.add(0, true);
                    }
                    if (statusRVP == 2) {
                        filterStatusList.add(1, true);
                    }
                    if (statusRVP == 3) {
                        filterStatusList.add(2, true);
                    }
                    return statusFilter(statusRVP, assigned, delivered, undelivered);
                case GlobalConstant.ShipmentTypeConstants.RVP_MPS:
                    DRSRvpQcMpsResponse drsRvpQcMpsResponse = commonDRSListItem.getDrsRvpQcMpsResponse();
                    int statusRvpMps = drsRvpQcMpsResponse.getShipmentStatus();
                    if (statusRvpMps == 0) {
                        filterStatusList.add(0, true);
                    }
                    if (statusRvpMps == 2) {
                        filterStatusList.add(1, true);
                    }
                    if (statusRvpMps == 3) {
                        filterStatusList.add(2, true);
                    }
                    return statusFilter(statusRvpMps, assigned, delivered, undelivered);
                case GlobalConstant.ShipmentTypeConstants.EDS:
                    EDSResponse edsResponse = commonDRSListItem.getEdsResponse();
                    int statusEDS = edsResponse.getShipmentStatus();
                    if (statusEDS == 0) {
                        filterStatusList.add(0, true);
                    }
                    if (statusEDS == 2) {
                        filterStatusList.add(1, true);
                    }
                    if (statusEDS == 3) {
                        filterStatusList.add(2, true);
                    }
                    return statusFilter(statusEDS, assigned, delivered, undelivered);
            }
            for (int i = 0; i < filterStatusList.size(); i++) {
                if (filterStatusList.get(i)) {
                    Constants.is_pending = true;
                }
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return true;
    }

    private void filterDataRemark() {
        List<CommonDRSListItem> commonDRSList = new ArrayList<>();
        try {
            Observable.just(commonDRSListItemList).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).flatMapIterable((Function<List<CommonDRSListItem>, List<CommonDRSListItem>>) v -> v).filter(commonDRSListItem -> filterBasisOnRemark(commonDRSListItem, mItemSelectedDataRemark[0], mItemSelectedDataRemark[1], mItemSelectedDataRemark[2], mItemSelectedDataRemark[3], mItemSelectedDataRemark[4])).subscribe(new Observer<CommonDRSListItem>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(CommonDRSListItem commonDRSListItem) {
                    commonDRSList.add(commonDRSListItem);
                }

                @Override
                public void onError(Throwable e) {
                    Logger.e(TAG, String.valueOf(e));
                }

                @Override
                public void onComplete() {
                    commonDRSListItemList.clear();
                    commonDRSListItemList.addAll(commonDRSList);
                    applySort();
                }
            });
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
        }
    }

    private boolean statusFilter(int status, boolean assigned, boolean delivered, boolean sync) {
        if (assigned && status == Constants.SHIPMENT_ASSIGNED_STATUS) {
            return true;
        } else {
            if (delivered && status == Constants.SHIPMENT_DELIVERED_STATUS) {
                return true;
            } else {
                return sync && status == Constants.SHIPMENT_UNDELIVERED_STATUS;
            }
        }
    }

    private boolean filterBasisOnRemark(CommonDRSListItem commonDRSListItem, boolean b1, boolean b2, boolean b3, boolean b4, boolean b5) {
        if (b5) {
            if (commonDRSListItem.getRemark() == null) {
                return true;
            }
        }
        if (commonDRSListItem.getRemark() != null && commonDRSListItem.getRemark().remark != null) {
            try {
                if (b1 && commonDRSListItem.getRemark().remark.contains(GlobalConstant.RemarksTypeConstants.CONSIGNEE_NOT_PICKING_CALL))
                    return true;
                if (b2 && commonDRSListItem.getRemark().remark.contains(GlobalConstant.RemarksTypeConstants.CONSIGNEE_REQUESTED_TO_CALL_LATER))
                    return true;
                if (b3 && commonDRSListItem.getRemark().remark.contains(GlobalConstant.RemarksTypeConstants.SAME_DAY_RESCHEDULE))
                    return true;
                if (b4 && commonDRSListItem.getRemark().remark.contains(GlobalConstant.RemarksTypeConstants.CONSIGNEE_CALLED))
                    return true;
                if (b5 && commonDRSListItem.getRemark().remark.contains(GlobalConstant.RemarksTypeConstants.I_Am_On_The_Way))
                    return true;
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
        }
        return false;
    }

    public void applySort() {
        if (ToDoListActivity.SORTING_TECHNIQUE == ToDoListActivity.SORTING_DRS_NO) {
            if (commonDRSListItemList != null && !commonDRSListItemList.isEmpty()) {
                commonDRSListItemList = DRSListSorter.sort(commonDRSListItemList, DRSListSorter.TECHNIQUE.DRS_NO);
            }
            getNavigator().applyLocationBasedSort(commonDRSListItemList);
        } else if (ToDoListActivity.SORTING_TECHNIQUE == ToDoListActivity.SORTING_LOCATION) {
            Location pointFE = getFELocation();
            for (CommonDRSListItem commonDRSListItem : commonDRSListItemList) {
                try {
                    switch (commonDRSListItem.getType()) {
                        case GlobalConstant.ShipmentTypeConstants.FWD: {
                            DRSForwardTypeResponse forwardTypeResponse = commonDRSListItem.getDrsForwardTypeResponse();
                            double lat = forwardTypeResponse.getConsigneeDetails().getAddress().getLocation().getLat();
                            double lng = forwardTypeResponse.getConsigneeDetails().getAddress().getLocation().getLng();
                            try {
                                Location pointAWB = new Location("LocationAWB");
                                pointAWB.setLatitude(lat);
                                pointAWB.setLongitude(lng);
                                double dKM = pointFE.distanceTo(pointAWB) / 1000;
                                DecimalFormat decimalFormat = new DecimalFormat("##.##");
                                dKM = Double.parseDouble(decimalFormat.format(dKM));
                                forwardTypeResponse.setDistance(dKM);
                                commonDRSListItem.setDistance(dKM);
                            } catch (Exception e) {
                                Logger.e(TAG, String.valueOf(e));
                            }
                        }
                        break;
                        case GlobalConstant.ShipmentTypeConstants.RTS: {
                            IRTSBaseInterface irtsBaseInterface = commonDRSListItem.getIRTSInterface();
                            double lat = irtsBaseInterface.getDetails().getLocation().getLat();
                            double lng = irtsBaseInterface.getDetails().getLocation().getLong();
                            try {
                                Location pointAWB = new Location("LocationAWB");
                                pointAWB.setLatitude(lat);
                                pointAWB.setLongitude(lng);
                                double dKM = pointFE.distanceTo(pointAWB) / 1000;
                                DecimalFormat decimalFormat = new DecimalFormat("##.##");
                                dKM = Double.parseDouble(decimalFormat.format(dKM));
                                irtsBaseInterface.setDistance(dKM);
                                commonDRSListItem.setDistance(dKM);
                            } catch (Exception e) {
                                Logger.e(TAG, String.valueOf(e));
                            }
                        }
                        break;
                        case GlobalConstant.ShipmentTypeConstants.RVP: {
                            DRSReverseQCTypeResponse drsReverseQCTypeResponse = commonDRSListItem.getDrsReverseQCTypeResponse();
                            double lat = drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getLocation().getLat();
                            double lng = drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getLocation().getLng();
                            try {
                                Location pointAWB = new Location("LocationAWB");
                                pointAWB.setLatitude(lat);
                                pointAWB.setLongitude(lng);
                                double dKM = pointFE.distanceTo(pointAWB) / 1000;
                                DecimalFormat decimalFormat = new DecimalFormat("##.##");
                                dKM = Double.parseDouble(decimalFormat.format(dKM));
                                drsReverseQCTypeResponse.setDistance(dKM);
                                commonDRSListItem.setDistance(dKM);
                            } catch (Exception e) {
                                Logger.e(TAG, String.valueOf(e));
                            }
                        }
                        break;
                        case GlobalConstant.ShipmentTypeConstants.RVP_MPS: {
                            DRSRvpQcMpsResponse drsRvpQcMpsResponse = commonDRSListItem.getDrsRvpQcMpsResponse();
                            double lat = drsRvpQcMpsResponse.getConsigneeDetails().getAddress().getLocation().getLat();
                            double lng = drsRvpQcMpsResponse.getConsigneeDetails().getAddress().getLocation().getLng();
                            try {
                                Location pointAWB = new Location("LocationAWB");
                                pointAWB.setLatitude(lat);
                                pointAWB.setLongitude(lng);
                                double dKM = pointFE.distanceTo(pointAWB) / 1000;
                                DecimalFormat decimalFormat = new DecimalFormat("##.##");
                                dKM = Double.parseDouble(decimalFormat.format(dKM));
                                drsRvpQcMpsResponse.setDistance(dKM);
                                commonDRSListItem.setDistance(dKM);
                            } catch (Exception e) {
                                Logger.e(TAG, String.valueOf(e));
                            }
                        }
                        break;
                        case GlobalConstant.ShipmentTypeConstants.EDS:
                            EDSResponse response = commonDRSListItem.getEdsResponse();
                            double lat = response.getConsigneeDetail().getAddress().getLocation().getLat();
                            double lng = response.getConsigneeDetail().getAddress().getLocation().getLng();
                            try {
                                Location pointAWB = new Location("LocationAWB");
                                pointAWB.setLatitude(lat);
                                pointAWB.setLongitude(lng);
                                double dKM = pointFE.distanceTo(pointAWB) / 1000;
                                DecimalFormat decimalFormat = new DecimalFormat("##.##");
                                dKM = Double.parseDouble(decimalFormat.format(dKM));
                                response.setDistance(dKM);
                                commonDRSListItem.setDistance(dKM);
                            } catch (Exception e) {
                                Logger.e(TAG, String.valueOf(e));
                            }
                            break;
                        default:
                    }
                } catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                }
            }
            List<CommonDRSListItem> list = DRSListSorter.sort(commonDRSListItemList, DRSListSorter.TECHNIQUE.DISTANCE);
            commonDRSListItemList.clear();
            commonDRSListItemList.addAll(list);
            getNavigator().applyLocationBasedSort(commonDRSListItemList);
        } else {
            getNavigator().setDataToDRSFragment(commonDRSListItemList);
        }
    }

    private Location getFELocation() {
        Location pointFE = new Location("LocationFE");
        pointFE.setLatitude(getDataManager().getCurrentLatitude());
        pointFE.setLongitude(getDataManager().getCurrentLongitude());
        return pointFE;
    }

    /**
     * DRSSequence operational methods
     */
    public void clearDRSSequenceFromTable() {
        try {
            getCompositeDisposable().add(getDataManager().deleteAllDataDRSSequence().subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
            }));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
        }
    }

    public void getEdsReschdData() {
        try {
            getCompositeDisposable().add(getDataManager().getEdsRescheduleFlag().subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(rescheduleEdsDS -> {
            }));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void getDRSRVP() {
        commonDRSListItemList.clear();
        try {
            getCompositeDisposable().add(Observable.zip(getDataManager().getDRSListRVP(), getDataManager().getAllRemarks(getDataManager().getCode(), TimeUtils.getDateYearMonthMillies()), (reverseQCTypeResponses, remarks) -> {
                HashMap<Long, Remark> remarkMap = new HashMap<>();
                for (Remark remark : remarks) {
                    remarkMap.put(remark.awbNo, remark);
                }
                rvpTotalCount.set((long) reverseQCTypeResponses.size());
                List<CommonDRSListItem> commonDRSListItems = new ArrayList<>();
                for (DRSReverseQCTypeResponse rvp : reverseQCTypeResponses) {
                    CommonDRSListItem item = new CommonDRSListItem(GlobalConstant.ShipmentTypeConstants.RVP, rvp);
                    item.setRemark(remarkMap.get(rvp.getAwbNo()));
                    commonDRSListItems.add(item);
                }
                return commonDRSListItems;
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(commonDRSListItems -> {
                commonDRSListItemList.addAll(commonDRSListItems);
                if (commonDRSListItemList.isEmpty()) {
                    getNavigator().hideLayout(Constants.RVP);
                }
                filterOnStatus();
            }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
        }
    }

    public void mapListtoogle() {
        getNavigator().mapListtoogle();
    }

    public void onResetSequenceClick() {
        getNavigator().onResetSequenceClick();
    }

    public void onCheckBoxClick() {
        getNavigator().OnCheckBoxClick();
    }

    public void initFilterObservers() {
        mItemSelectedDataRemark = new boolean[]{true, true, true, true, true};
        mItemSelectedDataStatus = new boolean[]{true, true, true};
    }

    public void setItemSelectedDataStatus(boolean[] itemSelectedDataStatus) {
        Logger.e(TAG, "setItemSelectedDataStatus()");
        this.mItemSelectedDataStatus = itemSelectedDataStatus;
        filterOnStatus();
    }

    public void setItemSelectedDataCategory(boolean[] itemSelectedDataCategory) {
        getDRSData(itemSelectedDataCategory);
    }

    public void applyFilterOnDRS(boolean[] itemShipmentType, boolean[] itemSelectedDataStatus, boolean[] itemSelectedDataRemark) {
        this.mItemSelectedDataStatus = itemSelectedDataStatus;
        this.mItemSelectedDataRemark = itemSelectedDataRemark;
        try {
            getCompositeDisposable().add(Observable.zip(getDataManager().getDRSListForward(), getDataManager().getDRSListNewRTS(), getDataManager().getDRSListRVP(), getDataManager().getDRSListRVPMPS(), getDataManager().getDrsListNewEds(), getDataManager().getAllRemarks(getDataManager().getCode(), TimeUtils.getDateYearMonthMillies()), (drsForwardTypeResponses, drsReturnToShipperTypeNewResponses, drsReverseQCTypeResponses, drsRvpQcMpsResponses, edsResponses, remarks) -> {
                HashMap<Long, Remark> remarkMap = new HashMap<>();
                for (Remark remark : remarks) {
                    remarkMap.put(remark.awbNo, remark);
                }
                List<CommonDRSListItem> commonDRSListItems = new ArrayList<>();
                if (itemShipmentType[0]) {
                    forwardTotalCount.set((long) drsForwardTypeResponses.size());
                    for (DRSForwardTypeResponse fwd : drsForwardTypeResponses) {
                        CommonDRSListItem item = new CommonDRSListItem(GlobalConstant.ShipmentTypeConstants.FWD, fwd);
                        item.setRemark(remarkMap.get(fwd.getAwbNo()));
                        commonDRSListItems.add(item);
                    }
                }
                if (itemShipmentType[1]) {
                    rtsTotalCount.set((long) drsReturnToShipperTypeNewResponses.getCombinedList().size());
                    for (IRTSBaseInterface rts : drsReturnToShipperTypeNewResponses.getCombinedList()) {
                        CommonDRSListItem item = new CommonDRSListItem(GlobalConstant.ShipmentTypeConstants.RTS, rts);
                        item.setRemark(remarkMap.get(rts.getDetails().getId()));
                        commonDRSListItems.add(item);
                    }
                }
                if (itemShipmentType[2]) {
                    rvpTotalCount.set((long) drsReverseQCTypeResponses.size());
                    for (DRSReverseQCTypeResponse rvp : drsReverseQCTypeResponses) {
                        CommonDRSListItem item = new CommonDRSListItem(GlobalConstant.ShipmentTypeConstants.RVP, rvp);
                        item.setRemark(remarkMap.get(rvp.getAwbNo()));
                        commonDRSListItems.add(item);
                    }
                }
                if (itemShipmentType[3]) {
                    edsTotalCount.set((long) edsResponses.size());
                    for (EDSResponse eds : edsResponses) {
                        CommonDRSListItem item = new CommonDRSListItem(GlobalConstant.ShipmentTypeConstants.EDS, eds);
                        item.setRemark(remarkMap.get(eds.getAwbNo()));
                        commonDRSListItems.add(item);
                    }
                }
                return commonDRSListItems;
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(commonDRSListItems -> {
                commonDRSListItemList.clear();
                commonDRSListItemList.addAll(commonDRSListItems);
                if (forwardTotalCount.get() == 0 || !itemShipmentType[0]) {
                    getNavigator().hideLayout(Constants.FWD);
                } else {
                    getNavigator().showLayout(Constants.FWD);
                }
                if (rtsTotalCount.get() == 0 || !itemShipmentType[1]) {
                    getNavigator().hideLayout(Constants.RTS);
                } else {
                    getNavigator().showLayout(Constants.RTS);
                }
                if (rvpTotalCount.get() == 0 || !itemShipmentType[2]) {
                    getNavigator().hideLayout(Constants.RVP);
                } else {
                    getNavigator().showLayout(Constants.RVP);
                }
                if (edsTotalCount.get() == 0 || !itemShipmentType[3]) {
                    getNavigator().hideLayout(Constants.EDS);
                } else {
                    getNavigator().showLayout(Constants.EDS);
                }
                filterOnStatus();
            }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
        }
    }

    public void getDRSData(boolean[] itemSelectedDataCategory) {
        commonDRSListItemList.clear();
        if (itemSelectedDataCategory[0]) {
            getForwardDRSList();
        }
        if (itemSelectedDataCategory[1]) {
            getRTSDRSList();
        }
        if (itemSelectedDataCategory[2]) {
            getDRSRVP();
        }
        if (itemSelectedDataCategory[3]) {
            getDRSEDS();
        }
    }

    public void getAllCategoryAssignedCount() {
        try {
            getCompositeDisposable().add(Observable.zip(getDataManager().getFWDStatusCount(0), getDataManager().getRTSStatusCount(0), getDataManager().getRVPStatusCount(0), getDataManager().getRVPMPSStatusCount(0),getDataManager().getEDSStatusCount(0), (forward, rts, rvp,rvp_mps, eds) -> {
                Long allAssignedCount = forward + rts + rvp +rvp_mps+ eds;
                totalAssignedCount.set(String.valueOf(allAssignedCount));
                is_pending.set(allAssignedCount > 0);
                return forward + rts + rvp + rvp_mps+eds;
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aLong -> {

            }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
        }
    }

    public void getAllCategoryDeliveredCount() {
        try {
            getCompositeDisposable().add(Observable.zip(getDataManager().getFWDStatusCount(2), getDataManager().getRTSStatusCount(2), getDataManager().getRVPStatusCount(2), getDataManager().getRVPMPSStatusCount(2), getDataManager().getEDSStatusCount(2), (forward, rts, rvp,rvp_mps, eds) -> {
                Long allDeliveredCount = forward + rts + rvp + rvp_mps+eds;
                totalDeliveredCount.set(String.valueOf(allDeliveredCount));
                is_successful.set(allDeliveredCount > 0);
                return forward + rts + rvp +rvp_mps+ eds;
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aLong -> getNavigator().setCount(), throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
        }
    }

    public void getAllCategoryUnDeliveredCount() {
        try {
            getCompositeDisposable().add(Observable.zip(getDataManager().getFWDStatusCount(3), getDataManager().getRTSStatusCount(3), getDataManager().getRVPStatusCount(3),getDataManager().getRVPMPSStatusCount(3), getDataManager().getEDSStatusCount(3), (forward, rts, rvp, rvp_mps,eds) -> {
                Long allUndeliveredCount = forward + rts + rvp + rvp_mps+eds;
                totalUndeliveredCount.set(String.valueOf(allUndeliveredCount));
                is_unsuccessful.set(allUndeliveredCount > 0);
                return forward + rts + rvp + rvp_mps+eds;
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aLong -> {
            }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
        }
    }

    public void getDRSEDS() {
        commonDRSListItemList.clear();
        try {
            getCompositeDisposable().add(Observable.zip(getDataManager().getDrsListNewEds(), getDataManager().getAllRemarks(getDataManager().getCode(), TimeUtils.getDateYearMonthMillies()), (edsResponses, remarks) -> {
                HashMap<Long, Remark> remarkMap = new HashMap<>();
                for (Remark remark : remarks) {
                    remarkMap.put(remark.awbNo, remark);
                }
                edsTotalCount.set((long) edsResponses.size());
                List<CommonDRSListItem> commonDRSListItems = new ArrayList<>();
                for (EDSResponse eds : edsResponses) {
                    CommonDRSListItem item = new CommonDRSListItem(GlobalConstant.ShipmentTypeConstants.EDS, eds);
                    item.setRemark(remarkMap.get(eds.getAwbNo()));
                    commonDRSListItems.add(item);
                }
                return commonDRSListItems;
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(commonDRSListItems -> {
                commonDRSListItemList.addAll(commonDRSListItems);
                if (commonDRSListItemList.isEmpty()) {
                    getNavigator().hideLayout(Constants.EDS);
                }
                filterOnStatus();
            }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
        }
    }

    public void onBackClick() {
        getNavigator().onBackClick();
    }

    public void onApplyClick() {
        getNavigator().onApplyClick();
    }

    public void onSendClick() {
        getNavigator().onSendClick();
    }

    public void onCancelClick() {
        getNavigator().onCancelClick();
    }

    public void onFilterClick() {
        getNavigator().onFilterClick();
    }

    public void checkAll() {
        try {
            if (!Objects.requireNonNull(listShipmentObserver.get()).isEmpty()) {
                getNavigator().handleDRSCheckboxes();
            } else {
                getNavigator().showError("No Shipment");
            }
        } catch (Exception e) {
            getNavigator().onErrorMsg(e.getMessage());
        }
    }

    public void onScanClick() {
        getNavigator().onScanClick();
    }

    public void onSwitchCallBridgeClick() {
        getNavigator().onSwitchCallBridgeClick();
    }

    public void onClearFilterClick() {
        getNavigator().onClearFilterClick();
    }

    public List<CommonDRSListItem> getSortedData(List<CommonDRSListItem> commonDRSListItems) {
        if (ToDoListActivity.SORTING_TECHNIQUE == ToDoListActivity.SORTING_DRS_NO) {
            if (commonDRSListItems != null && !commonDRSListItems.isEmpty()) {
                commonDRSListItems = DRSListSorter.sort(commonDRSListItems, DRSListSorter.TECHNIQUE.DRS_NO);
            }
            getNavigator().applyLocationBasedSort(commonDRSListItems);
        } else if (ToDoListActivity.SORTING_TECHNIQUE == ToDoListActivity.SORTING_LOCATION) {
            Location pointFE = getFELocation();
            for (CommonDRSListItem commonDRSListItem : commonDRSListItems) {
                try {
                    switch (commonDRSListItem.getType()) {
                        case GlobalConstant.ShipmentTypeConstants.FWD: {
                            DRSForwardTypeResponse forwardTypeResponse = commonDRSListItem.getDrsForwardTypeResponse();
                            double lat = forwardTypeResponse.getConsigneeDetails().getAddress().getLocation().getLat();
                            double lng = forwardTypeResponse.getConsigneeDetails().getAddress().getLocation().getLng();
                            try {
                                Location pointAWB = new Location("LocationAWB");
                                pointAWB.setLatitude(lat);
                                pointAWB.setLongitude(lng);
                                double dKM = pointFE.distanceTo(pointAWB) / 1000;
                                DecimalFormat decimalFormat = new DecimalFormat("##.##");
                                dKM = Double.parseDouble(decimalFormat.format(dKM));
                                forwardTypeResponse.setDistance(dKM);
                                commonDRSListItem.setDistance(dKM);
                            } catch (Exception e) {
                                Logger.e(TAG, String.valueOf(e));
                            }
                        }
                        break;
                        case GlobalConstant.ShipmentTypeConstants.RTS: {
                            IRTSBaseInterface irtsBaseInterface = commonDRSListItem.getIRTSInterface();
                            double lat = irtsBaseInterface.getDetails().getLocation().getLat();
                            double lng = irtsBaseInterface.getDetails().getLocation().getLong();
                            try {
                                Location pointAWB = new Location("LocationAWB");
                                pointAWB.setLatitude(lat);
                                pointAWB.setLongitude(lng);
                                double dKM = pointFE.distanceTo(pointAWB) / 1000;
                                DecimalFormat decimalFormat = new DecimalFormat("##.##");
                                dKM = Double.parseDouble(decimalFormat.format(dKM));
                                irtsBaseInterface.setDistance(dKM);
                                commonDRSListItem.setDistance(dKM);
                            } catch (Exception e) {
                                Logger.e(TAG, String.valueOf(e));
                            }
                        }
                        break;
                        case GlobalConstant.ShipmentTypeConstants.RVP: {
                            DRSReverseQCTypeResponse drsReverseQCTypeResponse = commonDRSListItem.getDrsReverseQCTypeResponse();
                            double lat = drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getLocation().getLat();
                            double lng = drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getLocation().getLng();
                            try {
                                Location pointAWB = new Location("LocationAWB");
                                pointAWB.setLatitude(lat);
                                pointAWB.setLongitude(lng);
                                double dKM = pointFE.distanceTo(pointAWB) / 1000;
                                DecimalFormat decimalFormat = new DecimalFormat("##.##");
                                dKM = Double.parseDouble(decimalFormat.format(dKM));
                                drsReverseQCTypeResponse.setDistance(dKM);
                                commonDRSListItem.setDistance(dKM);
                            } catch (Exception e) {
                                Logger.e(TAG, String.valueOf(e));
                            }
                        }
                        break;
                        case GlobalConstant.ShipmentTypeConstants.RVP_MPS: {
                            DRSRvpQcMpsResponse drsRvpQcMpsResponse = commonDRSListItem.getDrsRvpQcMpsResponse();
                            double lat = drsRvpQcMpsResponse.getConsigneeDetails().getAddress().getLocation().getLat();
                            double lng = drsRvpQcMpsResponse.getConsigneeDetails().getAddress().getLocation().getLng();
                            try {
                                Location pointAWB = new Location("LocationAWB");
                                pointAWB.setLatitude(lat);
                                pointAWB.setLongitude(lng);
                                double dKM = pointFE.distanceTo(pointAWB) / 1000;
                                DecimalFormat decimalFormat = new DecimalFormat("##.##");
                                dKM = Double.parseDouble(decimalFormat.format(dKM));
                                drsRvpQcMpsResponse.setDistance(dKM);
                                commonDRSListItem.setDistance(dKM);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                        case GlobalConstant.ShipmentTypeConstants.EDS:
                            EDSResponse response = commonDRSListItem.getEdsResponse();
                            double lat = response.getConsigneeDetail().getAddress().getLocation().getLat();
                            double lng = response.getConsigneeDetail().getAddress().getLocation().getLng();
                            try {
                                Location pointAWB = new Location("LocationAWB");
                                pointAWB.setLatitude(lat);
                                pointAWB.setLongitude(lng);
                                double dKM = pointFE.distanceTo(pointAWB) / 1000;
                                DecimalFormat decimalFormat = new DecimalFormat("##.##");
                                dKM = Double.parseDouble(decimalFormat.format(dKM));
                                response.setDistance(dKM);
                                commonDRSListItem.setDistance(dKM);
                            } catch (Exception e) {
                                Logger.e(TAG, String.valueOf(e));
                            }
                            break;
                        default:
                    }
                } catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                }
            }
            List<CommonDRSListItem> list = DRSListSorter.sort(commonDRSListItems, DRSListSorter.TECHNIQUE.DISTANCE);
            commonDRSListItemList.clear();
            commonDRSListItemList.addAll(list);
        }
        return commonDRSListItems;
    }

    public void addRemarks(Remark remark) {
        try {
            getCompositeDisposable().add(getDataManager().insertRemark(remark).observeOn(getSchedulerProvider().io()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> getRemarksCount(), throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
        }
    }

    public void getRemarksCount() {
        try {
            getCompositeDisposable().add(Observable.zip(getDataManager().getRemarksoneCount(GlobalConstant.RemarksTypeConstants.CONSIGNEE_NOT_PICKING_CALL), getDataManager().getRemarkstwoCount(GlobalConstant.RemarksTypeConstants.CONSIGNEE_REQUESTED_TO_CALL_LATER), getDataManager().getRemarksthreeCount("Day"), getDataManager().getRemarksfourCount(GlobalConstant.RemarksTypeConstants.CONSIGNEE_CALLED), getDataManager().getNoRemarksCount(), (one, two, three, four, five) -> {
                remark_one.set(one != 0);
                remark_two.set(two != 0);
                remark_three.set(three != 0);
                remark_four.set(four != 0);
                getNavigator().ExpandableList();
                return one + two + three + four;
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aLong -> {
            }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
        }
    }

    public String getEmployeeCode() {
        return getDataManager().getCode();
    }

    public void getcallConfig() {
        try {
            getCompositeDisposable().add(getDataManager().loadallcallbridge().subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(callbridgeConfiguration -> {
                if (callbridgeConfiguration != null) {
                    try {
                        myMasterDataReasonCodeResponse = callbridgeConfiguration;
                        getNavigator().callConfig(myMasterDataReasonCodeResponse);
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                }
            }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
        }
    }

    public void makeCallBridgeApiCall(String callBridgeKey, String awb, int drsid, String type) {
        CallApiRequest request = new CallApiRequest();
        request.setAwb(awb);
        request.setCb_api_key(callBridgeKey);
        request.setDrs_id(String.valueOf(drsid));
        final long timeStamp = System.currentTimeMillis();
        writeRestAPIRequst(timeStamp, request);
        try {
            getCompositeDisposable().add(getDataManager().doCallBridgeApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), request).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(callApiResponse -> {
                writeRestAPIResponse(timeStamp, callApiResponse);
                if (callApiResponse != null) {
                    try {
                        if (callApiResponse.isStatus()) {
                            if (type.equalsIgnoreCase(Constants.FWD)) {
                                updateForwardCallAttempted(awb);
                            }
                            if (type.equalsIgnoreCase(Constants.RVP)) {
                                updateRVPCallAttempted(awb);
                            }
                            if (type.equalsIgnoreCase(Constants.RVP_MPS)) {
                                updateRVPMPSCallAttempted(awb);
                            }
                            if (type.equalsIgnoreCase(Constants.RTS)) {
                                updateRTSCallAttempted(awb);
                            }
                            if (type.equalsIgnoreCase(Constants.EDS)) {
                                updateEDSCallAttempted(awb);
                            }
                        }
                        getNavigator().onHandleError(callApiResponse);
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                }
            }, throwable -> getNavigator().showErrorMessage(Objects.requireNonNull(throwable.getMessage()).contains("HTTP 500 "))));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
        }
    }

    public void updateForwardCallAttempted(String awb) {
        try {
            getCompositeDisposable().add(getDataManager().updateForwardCallAttempted(Long.valueOf(awb), Constants.callAttempted).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> getNavigator().notifyAdapter(), throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void updateRVPCallAttempted(String awb) {
        try {
            getCompositeDisposable().add(getDataManager().updateRVPCallAttempted(Long.valueOf(awb), Constants.callAttempted).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {
            }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void updateRVPMPSCallAttempted(String awb) {
        try {
            getCompositeDisposable().add(getDataManager().updateRVPMpsCallAttempted(Long.valueOf(awb), Constants.callAttempted).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) {
                }
            }, throwable -> {
            }));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void updateRTSCallAttempted(String awb) {
        try {
            getCompositeDisposable().add(getDataManager().updateRTSCallAttempted(Long.valueOf(awb), Constants.callAttempted).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> getNavigator().notifyAdapter(), throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void updateEDSCallAttempted(String awb) {
        try {
            getCompositeDisposable().add(getDataManager().updateEDSCallAttempted(Long.valueOf(awb), Constants.callAttempted).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {
            }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void getCodcount() {
        try {
            getCompositeDisposable().add(getDataManager().getCodCount().observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(value -> {
                DecimalFormat df2 = new DecimalFormat(".##");
                value = value + Double.parseDouble(PreferenceUtils.getSharedPreferences(getApplication().getApplicationContext()).getString("EDSCASHCOLLECTION", "0"));
                if (value == 0) {
                    codCashCount.set("No Cash collected");
                } else {
                    codCashCount.set("Cash : ₹" + df2.format(value));
                }
            }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void getEcodcount() {
        try {
            getCompositeDisposable().add(getDataManager().getEcodStatusCount().observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(value -> {
                DecimalFormat df2 = new DecimalFormat(".##");
                if (value != null)
                    if (value == 0) {
                        ecodCashCount.set("No eCOD collected");
                    } else {
                        ecodCashCount.set("eCOD : ₹" + df2.format(value));
                    }
            }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void updateRVPpinCallAttempted(String pin) {
        try {
            getCompositeDisposable().add(getDataManager().updateRVPpinCallAttempted(Long.valueOf(pin), Constants.callAttempted).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> getNavigator().notifyAdapter(), throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void updateForwardpinCallAttempted(String awb) {
        try {
            getCompositeDisposable().add(getDataManager().updateForwardpinCallAttempted(Long.valueOf(awb), Constants.callAttempted).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> getNavigator().notifyAdapter(), throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void updateEDSpinCallAttempted(String awb) {
        try {
            getCompositeDisposable().add(getDataManager().updateEDSpinCallAttempted(Long.valueOf(awb), Constants.callAttempted).observeOn(getSchedulerProvider().ui()).subscribeOn(getSchedulerProvider().io()).subscribe(aBoolean -> getNavigator().notifyAdapter(), throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public double getlat() {
        return getDataManager().getCurrentLatitude();
    }

    public double getlng() {
        return getDataManager().getCurrentLongitude();
    }

    public void callSmsApi(ToDoListActivity toDoListActivity) {
        ProgressDialog dialog = new ProgressDialog(toDoListActivity, android.R.style.Theme_Material_Light_Dialog);
        dialog.show();
        dialog.setCancelable(false);
        dialog.setMessage("Fetching Data...");
        dialog.setIndeterminate(true);
        try {
            smsRequest = new SmsRequest();
            addInfo = new Additional_info();
            smsRequest.setAwb(getRemarkAwb);
            smsRequest.setSms_type(Constants.UNREACHABLE);
            smsRequest.setLat(getDataManager().getCurrentLatitude());
            smsRequest.setLng(getDataManager().getCurrentLongitude());
            smsRequest.setDate(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            addInfo.setLocation("");
            addInfo.setRemarks("");
            addInfo.setDuration("");
            smsRequest.setAdditional_info(addInfo);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        try {
            getCompositeDisposable().add(getDataManager().doSMSApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), smsRequest).doOnSuccess(awbResponse -> Log.d("MyResponse1", awbResponse.toString())).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(response -> {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }, throwable -> {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                String error;
                try {
                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                    getNavigator().smsResponseThrowable(error.contains("HTTP 500 "));
                } catch (NullPointerException e) {
                    Logger.e(TAG, String.valueOf(e));
                }
            }));
        } catch (Exception e) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void getRemarkAwb(String awb) {
        getRemarkAwb.clear();
        getRemarkAwb.add(awb);
    }

    public void getAllCheckedShipments(HashMap<String, Boolean> hashMap) {
        for (Map.Entry<String, Boolean> next : hashMap.entrySet()) {
            if (next.getValue()) {
                awbhashset.add(next.getKey());
            } else {
                if (!next.getValue()) {
                    awbhashset.remove(next.getKey());
                }
            }
            myGetSelectedAwb = new ArrayList<>(awbhashset);
        }
    }

    public void getSearchCheckedShipmentlist(Boolean flag) {
        if (flag) {
            awbhashset.clear();
        }
    }

    public void getAllApiUrl() {
        try {
            getCompositeDisposable().add(getDataManager().getAllApiUrl().subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(apiUrlData -> {
                for (int i = 0; i < apiUrlData.size(); i++) {
                    SathiApplication.hashMapAppUrl.put(apiUrlData.get(i).getApiUrlKey(), apiUrlData.get(i).getApiUrl());
                }
            }));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void getEdsListTask(String composite_key) {
        try {
            getCompositeDisposable().add(getDataManager().getEdsWithActivityList(composite_key).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(edsWithActivityList -> {
                ToDoListViewModel.this.edsWithActivityList.set(edsWithActivityList);
                for (int i = 0; i < Objects.requireNonNull(ToDoListViewModel.this.edsWithActivityList.get()).getEdsActivityWizards().size(); i++) {
                    if (Objects.requireNonNull(ToDoListViewModel.this.edsWithActivityList.get()).getEdsActivityWizards().get(i).code.endsWith("CPV")) {
                        Constants.IS_CPV_ACTIVITY_EXITS = true;
                        break;
                    } else {
                        Constants.IS_CPV_ACTIVITY_EXITS = false;
                    }
                }
            }));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
        }
    }

    public void showAlertOfedsAssignment(Context context, CommonDRSListItem mCommonDRSListItem) {
        edsAcitivitesdialog = new Dialog(context);
        edsAcitivitesdialog.setContentView(R.layout.dialog_edstasklist_oncall);
        ExpandableListView expListView = edsAcitivitesdialog.findViewById(R.id.drawer);
        TextView tv_client_name = edsAcitivitesdialog.findViewById(R.id.tv_client_name);
        tv_client_name.setText(mCommonDRSListItem.getEdsResponse().getShipmentDetail().getCustomerName());
        getEdsListTaskCall(mCommonDRSListItem.getEdsResponse().getCompositeKey(), expListView);
        edsAcitivitesdialog.show();
    }

    public void getEdsListTaskCall(String composite_key, ExpandableListView expListView) {
        try {
            getCompositeDisposable().add(getDataManager().getEdsWithActivityList(composite_key).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(edsWithActivityList -> {
                ToDoListViewModel.this.edsWithActivityList.set(edsWithActivityList);
                getEdsActivityListMaster(expListView);
            }));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
        }
    }

    private void getEdsActivityListMaster(ExpandableListView expListView) {
        try {
            getCompositeDisposable().add(getDataManager().getEDSMasterDescriptions(Objects.requireNonNull(ToDoListViewModel.this.edsWithActivityList.get()).edsActivityWizards).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(masterActivityData -> {
                ToDoListViewModel.this.masterActivityDataList.set(new ArrayList<>(masterActivityData));
                getDetail(expListView);
            }));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
        }
    }

    private void getDetail(ExpandableListView expListView) {
        if (!Objects.requireNonNull(masterActivityDataList.get()).isEmpty()) {
            flagList = new ArrayList<>();
            try {
                for (MasterActivityData masterActivityData : Objects.requireNonNull(masterActivityDataList.get())) {
                    if (masterActivityData.getCode().startsWith("DC")) {
                        Constants.is_dc_enabled = true;
                        listHeaderDC.add(GlobalConstant.EdsTaskList.DOCUMENT_COLLECTION);
                        if (!header.contains(GlobalConstant.EdsTaskList.DOCUMENT_COLLECTION))
                            header.add(listHeaderDC.get(0));
                    } else if (masterActivityData.getCode().startsWith("DV")) {
                        listHeaderDV.add(GlobalConstant.EdsTaskList.DOCUMENT_VERIFICATION);
                        if (!header.contains(GlobalConstant.EdsTaskList.DOCUMENT_VERIFICATION))
                            header.add(listHeaderDV.get(0));
                    } else if (masterActivityData.getCode().startsWith("AC")) {
                        listHeaderActivity.add(GlobalConstant.EdsTaskList.ACTIVITIES);
                        if (!header.contains(GlobalConstant.EdsTaskList.ACTIVITIES))
                            header.add(listHeaderActivity.get(0));
                    }
                    for (EDSActivityWizard edsActivityWizard : Objects.requireNonNull(edsWithActivityList.get()).getEdsActivityWizards()) {
                        if (edsActivityWizard.getCode().startsWith("DC") && masterActivityData.getCode().startsWith("DC")) {
                            childListDC.add(masterActivityData.getActivityName());
                            option.add(edsActivityWizard.getWizardFlag().isMandate());
                        } else if (edsActivityWizard.getCode().startsWith("DV") && masterActivityData.getCode().startsWith("DV")) {
                            childListDV.add(masterActivityData.getActivityName());
                            option.add(edsActivityWizard.getWizardFlag().isMandate());
                        } else if (edsActivityWizard.getCode().startsWith("AC") && masterActivityData.getCode().startsWith("AC")) {
                            childListActivity.add(masterActivityData.getActivityName());
                            option.add(edsActivityWizard.getWizardFlag().isMandate());
                        }
                        if (edsActivityWizard.getCode().equalsIgnoreCase(masterActivityData.getCode())) {
                            if (edsActivityWizard.getCode().startsWith("AC")) {
                                flagList.add(edsActivityWizard.getCode().equalsIgnoreCase(Constants.CashCollection) || (edsActivityWizard.getCode().startsWith("AC_") && edsActivityWizard.getCode().endsWith("CPV")) || (edsActivityWizard.getCode().startsWith("AC") && edsActivityWizard.getCode().endsWith("MQF")) || (edsActivityWizard.getCode().startsWith("AC") && edsActivityWizard.getCode().endsWith("CVPV")) || (edsActivityWizard.getCode().startsWith("AC") && edsActivityWizard.getCode().endsWith("IMAGE")) || edsActivityWizard.getCode().startsWith("AC_LIST") || edsActivityWizard.getCode().startsWith("AC_AMAZON_EKYC") || edsActivityWizard.getCode().equalsIgnoreCase(Constants.Vodafone) || edsActivityWizard.getCode().contains("BKYC") || edsActivityWizard.getCode().equalsIgnoreCase(Constants.Icici) || edsActivityWizard.getCode().equalsIgnoreCase(Constants.EKYCXML) || edsActivityWizard.getCode().equalsIgnoreCase(Constants.EKYCRBL) || edsActivityWizard.getCode().equalsIgnoreCase(Constants.EKYCPAYTM));
                            } else {
                                flagList.add(true);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
            try {
                for (MasterActivityData masterActivityData : Objects.requireNonNull(masterActivityDataList.get())) {
                    code_name.put(masterActivityData.activityName, masterActivityData.code);
                }
                for (EDSActivityWizard edsActivityWizard : Objects.requireNonNull(edsWithActivityList.get()).getEdsActivityWizards()) {
                    for (MasterActivityData masterActivityData : Objects.requireNonNull(masterActivityDataList.get())) {
                        if (Objects.requireNonNull(code_name.get(masterActivityData.activityName)).equalsIgnoreCase(edsActivityWizard.code)) {
                            childList_optional_flag.put(masterActivityData.activityName, edsActivityWizard.getWizardFlag().isMandate());
                        }
                    }
                }
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
            try {
                Collections.sort(header);
                if (header.get(0).equalsIgnoreCase(GlobalConstant.EdsTaskList.ACTIVITIES)) {
                    header.remove(0);
                    header.add(GlobalConstant.EdsTaskList.ACTIVITIES);
                }
                if (!listHeaderDC.isEmpty()) {
                    Set<String> dc = new HashSet<>(childListDC);
                    childListDC.clear();
                    childListDC.addAll(dc);
                    childList.put(listHeaderDC.get(0), childListDC);
                }
                if (!listHeaderDV.isEmpty()) {
                    Set<String> dv = new HashSet<>(childListDV);
                    childListDV.clear();
                    childListDV.addAll(dv);
                    childList.put(listHeaderDV.get(0), childListDV);
                }
                if (!listHeaderActivity.isEmpty()) {
                    Set<String> act = new HashSet<>(childListActivity);
                    childListActivity.clear();
                    childListActivity.addAll(act);
                    childList.put(listHeaderActivity.get(0), childListActivity);
                }
            } catch (Exception e) {
                getNavigator().showError(e.getMessage());
            }
            flag = flagList.size() == Objects.requireNonNull(edsWithActivityList.get()).getEdsActivityWizards().size();
            if (flagList.contains(false)) {
                flag = false;
            }
            getNavigator().onExpandableData(expListView, flag, header, childList, option, childList_optional_flag);
        }
    }

    public Dialog getEdsAssignmentDialog() {
        try {
            if (edsAcitivitesdialog != null) {
                return edsAcitivitesdialog;
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return null;
    }

    public void updateLogs(List<LiveTrackingLogTable> remarkUpdateRequest) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager().dosendLiveTrackingLog(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), remarkUpdateRequest).doOnSuccess(liveTrackingResponse -> {
        }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(liveTrackingResponse -> {
        }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
    }

    public void saveCallStatus(long awb, int drs) {
        try {
            Call call = new Call();
            call.setDrs(drs);
            call.setAwb(awb);
            call.setStatus(true);
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(getDataManager().saveCallStatus(call).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
            }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void getRVPQCValues() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager().getQcValuesForAwb()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(qc_values -> {
                    for (int i = 0; i < qc_values.size(); i++) {
                        if (qc_values.get(i).getQcCode().equals("Check_Image")) {
                            qc_value_map.put(qc_values.get(i).getAwbNo(), qc_values.get(i).getQcValue());
                        }
                    }
                }));
    }

    public void checkRVPImageDownloadedORNot(Long awb) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager().getImageForAwb(String.valueOf(awb)).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(
                rvpQcImageTable -> {
                    try {
                        for (int i = 0; i < rvpQcImageTable.size(); i++) {
                            if (rvpQcImageTable.get(i).image_path.isEmpty()) {
                                String qcValue = qc_value_map.get(awb);
                                if (qcValue != null && !qcValue.isEmpty()) {
                                    String[] urls = qcValue.split(",#");
                                    for (int j = 0; j < urls.length; j++) {
                                        new DownloadImage(awb, awb + "_" + j + ".jpg").execute(urls[j]);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                }));
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadImage extends AsyncTask<String, Void, String> {

        String image_path_name;
        Long awb_number;

        public DownloadImage(Long awb, String image_path_name) {
            this.awb_number = awb;
            this.image_path_name = image_path_name;
        }

        // If bitmap received null then white bitmap will be return.
        private String downloadImageBitmap(String sUrl) {
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(sUrl).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                canvas.drawColor(Color.WHITE);
            }
            File fileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/" + Constants.EcomExpress);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            File cacheFile = new File(fileDir, image_path_name);
            if (!cacheFile.exists()) {
                try {
                    cacheFile.createNewFile();
                } catch (IOException e) {
                    Logger.e(TAG, String.valueOf(e));
                }
            }
            try {
                FileOutputStream out = new FileOutputStream(cacheFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
            return image_path_name;
        }

        @Override
        protected String doInBackground(String... params) {
            return downloadImageBitmap(params[0]);
        }

        protected void onPostExecute(String image_path_name) {
            updateImagePathToTable(awb_number, image_path_name);
        }
    }

    private void updateImagePathToTable(Long awb_number, String image_path_name) {
        RVPQCImageTable rvpqcImageTable = new RVPQCImageTable();
        rvpqcImageTable.awb_number = awb_number;
        rvpqcImageTable.image_path = image_path_name;
        getCompositeDisposable().add(getDataManager().insert(rvpqcImageTable).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
        }, throwable -> Logger.e(TAG, String.valueOf(throwable))));
    }

    public String getWhatsAppRemarkTemplate(String awb_no, String brand_name) {
        String FE_Name = getDataManager().getName();
        String FE_Number = getDataManager().getMobile();
        return "Hi, I am " + FE_Name + ", your Ecom Express agent.\n"
                + "I tried reaching you for your shipment with AWB " + awb_no + " from " + brand_name + " but unable to locate your address.\n"
                + "To assist in fulfilling your order,\n" + "1️⃣ Please share the 📍 location on the same chat\n"
                + "2️⃣ Please share alternate 📳 contact number for better coordination\n"
                + "In case of any issues, please contact me.\n"
                + FE_Name + "\n" + FE_Number + "\n"
                + "Looking forward to a great experience! 🤝🏻\n"
                + "Ecom Express";
    }

    public String getWhatsAppTechTemplate(String brand_name, String awb_no, String address) {
        String FE_Name = getDataManager().getName();
        String FE_Number = getDataManager().getMobile();
        return "Greetings from Ecom Express! 🙏🏻\n"
                + "Hi, I am " + FE_Name + ", your Ecom Express agent.\n"
                + "I am on my way to attempt your shipment with AWB " + awb_no + " from " + brand_name + " in the following address 📍:\n"
                + address + "\n" + "To assist in fulfilling your order 📦,\n"
                + "1️⃣ Please share the 📍 location on the same chat\n"
                + "2️⃣ Please share alternate 📳 contact number for better coordination\n"
                + "In case of any issues, please contact me.\n"
                + FE_Name + "\n" + FE_Number + "\n"
                + "Looking forward to a great experience! 🤝🏻\n"
                + "Ecom Express";
    }

    public void getRemarkForward(long awb, ItemForwardListViewBinding mBinding) {
        try {
            getCompositeDisposable().add(getDataManager().getRemarks(awb).observeOn(getSchedulerProvider().io()).subscribeOn(getSchedulerProvider().io()).subscribe(remark -> getNavigator().setForwardRemark(remark, mBinding), throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
        }
    }

    public void getRemarkRVP(long awb, ItemRvpListViewBinding mBinding) {
        try {
            getCompositeDisposable().add(getDataManager().getRemarks(awb).observeOn(getSchedulerProvider().io()).subscribeOn(getSchedulerProvider().io()).subscribe(remark -> getNavigator().setRVPRemark(remark, mBinding), throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
        }
    }

    public void getRemarkEDS(long awb, ItemEdsListViewBinding mBinding) {
        try {
            getCompositeDisposable().add(getDataManager().getRemarks(awb).observeOn(getSchedulerProvider().io()).subscribeOn(getSchedulerProvider().io()).subscribe(remark -> getNavigator().setEDSRemark(remark, mBinding), throwable -> Logger.e(TAG, String.valueOf(throwable))));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
        }
    }

    public void callFwdReassingApi(CommonDRSListItem commonDRSListItem) {
        setIsLoading(true);
        try {
            FWDReassignRequest fwdReassignRequest = new FWDReassignRequest();
            fwdReassignRequest.setAwb(commonDRSListItem.getDrsForwardTypeResponse().getAwbNo());
            fwdReassignRequest.setEmp_code(getDataManager().getEmp_code());
            fwdReassignRequest.setOld_drs_id(String.valueOf(commonDRSListItem.getDrsForwardTypeResponse().getDrsId()));
            getCompositeDisposable().add(getDataManager()
                    .doFwdReassingApiCall(getDataManager().getAuthToken(), fwdReassignRequest)
                    .doOnSuccess(fwdReassignReponse -> {
                    })
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(response -> {
                        if (response.status) {
                            convertPacketToAssign(commonDRSListItem, response.drsId);
                            updateAssignDataForward(response.awbNumber, String.valueOf(System.currentTimeMillis()));
                            getNavigator().showError(response.description);
                        } else {
                            if (response.response != null) {
                                getNavigator().showError(response.response.getDescription());
                            } else {
                                getNavigator().showError(response.description);
                            }
                        }
                    }, throwable -> {
                        setIsLoading(false);
                        String error;
                        try {
                            error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                            getNavigator().showErrorMessage(error.contains("HTTP 500 "));
                        } catch (NullPointerException e) {
                            Logger.e(TAG, String.valueOf(e));
                        }
                    }));
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
            setIsLoading(false);
        }
    }

    public void updateAssignDataForward(long awbNo, String assign_date) {
        try {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(getDataManager().updateAssignDataForward(awbNo, assign_date).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {
            }));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void convertPacketToAssign(CommonDRSListItem commonDRSListItem, int drsId) {
        String newCompositeKey = drsId + "" + commonDRSListItem.getDrsForwardTypeResponse().getAwbNo();
        getCompositeDisposable().add(getDataManager().updateForwardShipment(String.valueOf(commonDRSListItem.getDrsForwardTypeResponse().getAwbNo()), 0, 0, drsId, newCompositeKey).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().io()).subscribe(aBoolean -> {
            deleteFromPushApi(commonDRSListItem);
            deleteRemarkShipment(commonDRSListItem.getDrsForwardTypeResponse().getAwbNo());
            getDataManager().setTryReachingCount(commonDRSListItem.getDrsForwardTypeResponse().getAwbNo() + Constants.TRY_RECHING_COUNT, 0);
            getDataManager().setSendSmsCount(commonDRSListItem.getDrsForwardTypeResponse().getAwbNo() + Constants.TECH_PARK_COUNT, 0);
            getDataManager().setCallClicked(commonDRSListItem.getDrsForwardTypeResponse().getAwbNo() + "ForwardCall", true);
            getNavigator().getActivityReference().finish();
        }));
    }

    private void deleteFromPushApi(CommonDRSListItem commonDRSListItem) {
        getCompositeDisposable().add(getDataManager().deleteSyncedFWD(commonDRSListItem.getDrsForwardTypeResponse().getAwbNo()).subscribe(aBoolean -> getNavigator().UpdateDRSAdapter()));
    }

    private void deleteRemarkShipment(long awb) {
        getCompositeDisposable().add(getDataManager().deleteBasisAWB(awb).subscribe(aBoolean -> {
        }));
    }

    public void getRemarkRVPMPS(long awb, ItemRvpMpsListViewBinding mBinding) {
        try {
            getCompositeDisposable().add(getDataManager().getRemarks(awb).observeOn(getSchedulerProvider().io()).subscribeOn(getSchedulerProvider().io()).subscribe(remark -> getNavigator().setRVPMPSRemark(remark, mBinding), throwable -> {
                throwable.printStackTrace();
            }));
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
    }
}