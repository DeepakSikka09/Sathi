package in.ecomexpress.sathi.ui.dummy.eds.eds_task_list;

import androidx.databinding.ObservableField;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.remote.model.device_upload.Biometric_requestdata;
import in.ecomexpress.sathi.repo.remote.model.device_upload.Biometric_response;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.functions.Consumer;


/**
 * Created by dhananjayk on 27-10-2018.
 */
@HiltViewModel
public class EdsTaskListActivityModel extends BaseViewModel<IEdsTaskListNavigator> {


    private static final String TAG = EdsTaskListActivityModel.class.getSimpleName();
    private final ObservableField<EdsWithActivityList> edsWithActivityList = new ObservableField<EdsWithActivityList>();

    private final ObservableField<ArrayList<MasterActivityData>> masterActivityDataList = new ObservableField<>();
    private final ObservableField<String> awbNo = new ObservableField<>("");
    private final ObservableField<String> consigneeName = new ObservableField<>("");
    private final ObservableField<String> itemName = new ObservableField<>("");
    private final ObservableField<String> address = new ObservableField<>("");
    private final List<String> listHeaderActivity = new ArrayList<>();
    private final List<String> listHeaderDV = new ArrayList<>();
    private final List<String> listHeaderDC = new ArrayList<>();
    private final Set<String> all_edsactivity_codes = new HashSet<>();
    private final LinkedHashMap<String, List<String>> childList = new LinkedHashMap<>();
    private final LinkedHashMap<String, Boolean> childList_optional_flag = new LinkedHashMap<>();
    private final LinkedHashMap<String, String> code_name = new LinkedHashMap<>();
    private final List<String> header = new ArrayList<>();
    private final List<String> childListDC = new ArrayList<>();
    private final List<String> childListDV = new ArrayList<>();
    private final List<Boolean> option = new ArrayList<>();
    private final List<String> childListActivity = new ArrayList<>();
    boolean flag = true;
    List<Boolean> flagList;


    @Inject
    public EdsTaskListActivityModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }

    public void onBackClick() {
        getNavigator().onBack();
    }

    public void getEdsListTask(String composite_key) {
        try {

            getCompositeDisposable().add(getDataManager().
                    getEdsWithActivityList(composite_key).
                    subscribeOn(getSchedulerProvider().io()).
                    observeOn(getSchedulerProvider().ui()).
                    subscribe(new Consumer<EdsWithActivityList>() {
                        @Override
                        public void accept(EdsWithActivityList edsWithActivityList) throws Exception {
                            EdsTaskListActivityModel.this.edsWithActivityList.set(edsWithActivityList);
                            all_edsactivity_codes.clear();
                            for (int i = 0; i < edsWithActivityList.edsActivityWizards.size(); i++) {
                                all_edsactivity_codes.add(edsWithActivityList.edsActivityWizards.get(i).code);
                            }
                            getDataManager().setEdsActivityCodes(all_edsactivity_codes);
                            getEdsActivityListMaster();
                        }

                    }));

        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
    }

    public void onCancelClick() {
        getNavigator().onCancel();
    }

    public void onProceedClick() {
        getNavigator().onProceed();
    }

    private void getEdsActivityListMaster() {
        try {
            getCompositeDisposable().add(getDataManager().getEDSMasterDescriptions(EdsTaskListActivityModel.this.edsWithActivityList.get().edsActivityWizards).
                    subscribeOn(getSchedulerProvider().io()).
                    observeOn(getSchedulerProvider().ui()).
                    subscribe(new Consumer<List<MasterActivityData>>() {
                                  @Override
                                  public void accept(List<MasterActivityData> masterActivityData) throws Exception {
                                      Log.d(TAG, "accept: " + masterActivityData);
                                      EdsTaskListActivityModel.this.masterActivityDataList.set(new ArrayList<>(masterActivityData));
                                      refreshData();
                                  }
                              }
                    ));

        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
    }


    public void refreshData() {
        try {
            awbNo.set(String.valueOf(edsWithActivityList.get().edsResponse.getAwbNo()));
            consigneeName.set(edsWithActivityList.get().edsResponse.getConsigneeDetail().getName());
            itemName.set(edsWithActivityList.get().edsResponse.getShipmentDetail().getItemDescription());
            //address.set(edsWithActivityList.get().edsResponse.getConsigneeDetail().getAddress());
            address.set(CommonUtils.nullToEmpty(edsWithActivityList.get().edsResponse.getConsigneeDetail().getAddress().getLine1()) + " "
                    + CommonUtils.nullToEmpty(edsWithActivityList.get().edsResponse.getConsigneeDetail().getAddress().getLine2()) + " "
                    + CommonUtils.nullToEmpty(edsWithActivityList.get().edsResponse.getConsigneeDetail().getAddress().getLine3()) + " "
                    + CommonUtils.nullToEmpty(edsWithActivityList.get().edsResponse.getConsigneeDetail().getAddress().getLine4()) + " "
                    + CommonUtils.nullToEmpty(edsWithActivityList.get().edsResponse.getConsigneeDetail().getAddress().getCity()) + " "
                    + edsWithActivityList.get().edsResponse.getConsigneeDetail().getAddress().getPincode());
            getDetail();

        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }

    }

    private void getDetail() {
        if (masterActivityDataList.get().size() != 0) {
            flagList = new ArrayList<>();
            try {
                for (MasterActivityData masterActivityData : masterActivityDataList.get()) {
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
                    for (EDSActivityWizard edsActivityWizard : edsWithActivityList.get().getEdsActivityWizards()) {
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
                                flagList.add(edsActivityWizard.getCode().equalsIgnoreCase(Constants.CashCollection) || (edsActivityWizard.getCode().startsWith("AC_") && edsActivityWizard.getCode().endsWith("CPV")) || (edsActivityWizard.getCode().startsWith("AC") && edsActivityWizard.getCode().endsWith("MQF")) || (edsActivityWizard.getCode().startsWith("AC") && edsActivityWizard.getCode().endsWith("CVPV")) || (edsActivityWizard.getCode().startsWith("AC") && edsActivityWizard.getCode().endsWith("IMAGE")) || edsActivityWizard.getCode().startsWith("AC_LIST") || edsActivityWizard.getCode().startsWith("AC_AMAZON_EKYC") || edsActivityWizard.getCode().equalsIgnoreCase(Constants.Vodafone) || edsActivityWizard.getCode().contains("BKYC") || edsActivityWizard.getCode().contains("EKYC") || edsActivityWizard.getCode().equalsIgnoreCase(Constants.Icici) || edsActivityWizard.getCode().equalsIgnoreCase(Constants.EKYCXML) || edsActivityWizard.getCode().equalsIgnoreCase(Constants.EKYCRBL)||edsActivityWizard.getCode().equalsIgnoreCase(Constants.EKYCPAYTM) ||edsActivityWizard.getCode().equalsIgnoreCase(Constants.EKYCPAYTMM));
                            } else {
                                flagList.add(true);
                            }

                        }
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
                getNavigator().showError(e.getMessage());
            }
            try {
                for (MasterActivityData masterActivityData : masterActivityDataList.get()) {
                    code_name.put(masterActivityData.activityName, masterActivityData.code);
                }
                for (EDSActivityWizard edsActivityWizard : edsWithActivityList.get().getEdsActivityWizards()) {
                    for (MasterActivityData masterActivityData : masterActivityDataList.get()) {
                        if (code_name.get(masterActivityData.activityName).equalsIgnoreCase(edsActivityWizard.code)) {
                            childList_optional_flag.put(masterActivityData.activityName, edsActivityWizard.getWizardFlag().isMandate());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Collections.sort(header);
                if (header.get(0).equalsIgnoreCase(GlobalConstant.EdsTaskList.ACTIVITIES)) {
                    header.remove(0);
                    header.add(GlobalConstant.EdsTaskList.ACTIVITIES);
                }
                if (!listHeaderDC.isEmpty()) {
                    Set<String> dc = new HashSet<>();
                    dc.addAll(childListDC);
                    childListDC.clear();
                    childListDC.addAll(dc);
                    childList.put(listHeaderDC.get(0), childListDC);
                }
                if (!listHeaderDV.isEmpty()) {
                    Set<String> dv = new HashSet<>();
                    dv.addAll(childListDV);
                    childListDV.clear();
                    childListDV.addAll(dv);
                    childList.put(listHeaderDV.get(0), childListDV);
                }
                if (!listHeaderActivity.isEmpty()) {
                    Set<String> act = new HashSet<>();
                    act.addAll(childListActivity);
                    childListActivity.clear();
                    childListActivity.addAll(act);
                    childList.put(listHeaderActivity.get(0), childListActivity);
                }
            } catch (Exception e) {
                e.printStackTrace();
                getNavigator().showError(e.getMessage());
            }
            flag = flagList.size() == edsWithActivityList.get().getEdsActivityWizards().size();
            if (flagList.contains(false)) {
                flag = false;
            }
            getNavigator().onExpandableData(flag, header, childList, option, childList_optional_flag);


        } else {
            getNavigator().showMsg();
        }

    }

    public void verifyDevice(String device_serial_no, String model_no, String manufacturer, String time_stamp, String empcode) {

        setIsLoading(true);
        try {

            final long timeStamp = Calendar.getInstance().getTimeInMillis();
            Biometric_requestdata biometric_requestdata = new Biometric_requestdata(device_serial_no, model_no, manufacturer, time_stamp, empcode);
            // writeRestAPIRequst(timeStamp, packet);
            getCompositeDisposable().add(getDataManager()
                    .dobiometricApiCall(getDataManager().getAuthToken(),getDataManager().getEcomRegion(), biometric_requestdata)
                    .doOnSuccess(new Consumer<Biometric_response>() {
                        @Override
                        public void accept(Biometric_response biometric_response) {
                            writeRestAPIResponse(timeStamp, biometric_response);
                            //  Log.d("ICICI",biometric_response.getDescription());
                            setIsLoading(false);
                            getNavigator().sendBiometricResponse(biometric_response);

//                            Log.d("ICICI",biometric_response.getCode());
//                            Log.d("ICICI",biometric_response.getDescription());
                            //   Log.d("ICICI",iciciResponse.toString());
                            // getNavigator().sendICICIResponse(iciciResponse);
                        }
                    })
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<Biometric_response>() {
                        @Override
                        public void accept(Biometric_response biometric_response) {
                            setIsLoading(false);
                            getNavigator().sendBiometricResponse(biometric_response);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            EdsTaskListActivityModel.this.setIsLoading(false);
                            String error, myerror;

                        }
                    }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*  //  public ObservableField<String> getAwbNo() {
        return awbNo;
    }*/

    public ObservableField<String> getConsigneeName() {
        return consigneeName;
    }

    public ObservableField<String> getItemName() {
        return itemName;
    }

    public ObservableField<String> getAddress() {
        return address;
    }

    public EdsWithActivityList edsWithActivityList() {
        return edsWithActivityList.get();
    }

    public ArrayList<MasterActivityData> getEdsMasterData() {
        return masterActivityDataList.get();
    }

}
