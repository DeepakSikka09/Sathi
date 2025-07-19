package in.ecomexpress.sathi.ui.drs.rts.rts_main_list;

import static in.ecomexpress.sathi.SathiApplication.rtsCapturedImage1;
import static in.ecomexpress.sathi.SathiApplication.rtsCapturedImage2;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.widget.TextView;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.db.model.ImageModel;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.Details;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.ShipmentsDetail;
import in.ecomexpress.sathi.repo.remote.model.masterdata.RTSReasonCodeMaster;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.CryptoUtils;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@HiltViewModel
public class RTSListActivityViewModel extends BaseViewModel<IRTSListActivityNavigator> {

    public static String device = (Build.MANUFACTURER + ":" + Build.MODEL).toUpperCase(Locale.US);
    private final String TAG = RTSListActivityViewModel.class.getSimpleName();
    public final ObservableField<String> Mode = new ObservableField<>(Constants.RTSDeliveryMode);
    public final ObservableField<Boolean> OutPackDamage = new ObservableField<>(false);
    private final ObservableField<String> Assigned = new ObservableField<>("");
    private final ObservableField<String> Delivered = new ObservableField<>("");
    private final ObservableField<String> Pending = new ObservableField<>("");
    public HashMap<Integer, RTSReasonCodeMaster> mapRTSReasonCodeMaster = new HashMap<>();
    private final MutableLiveData<Boolean> imageUploadSuccessLiveData = new MutableLiveData<>();
    int deliveredCount = 0;
    int undeliveredCount = 0;
    int disputedCount = 0;
    int assignedCount = 0;
    Details details_data;
    boolean is_otp_required = false;
    List<ShipmentsDetail> shipmentsDetails_data;
    ObservableField<String> scanDeliverObservable = new ObservableField<>("");
    ObservableField<List<ShipmentsDetail>> listShipmentObserver = new ObservableField<>();
    private List<RTSReasonCodeMaster> listRTSReasonCodeMaster = new ArrayList<>();
    public Map<Long, Boolean> damageFlyerImageCaptured = new HashMap<>();
    public boolean isAllFWDChecked = false;
    public boolean isAllRVPChecked = false;
    private Dialog dialog;

    public LiveData<Boolean> getImageUploadSuccessLiveData() {
        return imageUploadSuccessLiveData;
    }

    @Inject
    public RTSListActivityViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);
    }

    public ObservableField<String> getAssigned() {
        return Assigned;
    }

    public ObservableField<String> getDelivered() {
        return Delivered;
    }

    public ObservableField<String> getPending() {
        return Pending;
    }

    public ObservableField<String> getMode() {
        return Mode;
    }

    public void setMode(String add) {
        Mode.set(add);
    }

    /*
    * This method is used to fetch shipment details (Delivered, Undelivered and Disputed) from database.
    * */
    public void getVWDetails(long id) {
        try {
            getCompositeDisposable().add(getDataManager().getVWDetails(id).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(details -> {
                details_data = details;
                is_otp_required = details_data.isIs_otp_required();
                scanDeliverObservable.set(details.getScan_deliver());
            }));
        } catch (Exception e) {
            getNavigator().onErrorMessage(e.getMessage());
        }
    }

    /*
    * This method is used to extract all the reason code from database and fill into the HashMap mapRTSReasonCodeMaster.
    * */
    public void listRtsReasonCodeMaster() {
        try {
            getCompositeDisposable().add(getDataManager().getRTSReasonCodes().subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(rtsReasonCodeMasters -> {
                listRTSReasonCodeMaster = rtsReasonCodeMasters;
                for (RTSReasonCodeMaster rtsReasonCodeMaster : listRTSReasonCodeMaster) {
                    mapRTSReasonCodeMaster.put(rtsReasonCodeMaster.getReasonCode(), rtsReasonCodeMaster);
                }
            }));
        } catch (Exception e) {
            getNavigator().onErrorMessage(e.getMessage());
        }
    }

    /*
     * This method is used to at a time one shipment details from database with the help of vendorId.
     * */
    public void getShipments(long vwID) {
        try {
            getCompositeDisposable().add(getDataManager().getVWShipmentList(vwID).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(shipmentsDetails -> {
                shipmentsDetails_data = shipmentsDetails;
                listShipmentObserver.set(shipmentsDetails);
                getNavigator().OnSetAdapter(shipmentsDetails);
                updateStatistics1();
            }));
        } catch (Exception e) {
            getNavigator().onErrorMessage(e.getMessage());
        }
    }

    /*
     * This method is used to at a time one shipment details from database with the help of vendorId.
     * */
    public void getShipments1(long vwID) {
        try {
            getCompositeDisposable().add(getDataManager().getVWShipmentList(vwID).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(shipmentsDetails -> {
                listShipmentObserver.set(shipmentsDetails);
                getNavigator().OnSetAdapter(shipmentsDetails);
                updateStatistics1();
            }));
        } catch (Exception e) {
            getNavigator().onErrorMessage(e.getMessage());
        }
    }

    /*
     * This method is used check whether shipment details contains valid awb or not and return valid boolean.
     * */
    public Boolean getFilterOutput(String awbNo) {
        try {
            for (ShipmentsDetail shipmentsDetail : Objects.requireNonNull(listShipmentObserver.get())) {
                if (String.valueOf(shipmentsDetail.getAwbNo()).equalsIgnoreCase(awbNo)) {
                    return true;
                }
                if (shipmentsDetail.getParentAwbNo().equalsIgnoreCase(awbNo)) {
                    return true;
                }
            }
        } catch (Exception e) {
            getNavigator().onErrorMessage(e.getMessage());
        }
        return false;
    }

    /*
    * This method is used to check all shipments are in assigned mode or not and return boolean value accordingly.
    * */
    private boolean areAllShipmentsUnassigned() {
        try{
            if (listShipmentObserver == null || listShipmentObserver.get() == null) {
                return true;
            }
            for (ShipmentsDetail shipmentsDetail : Objects.requireNonNull(listShipmentObserver.get())) {
                if (shipmentsDetail.getStatus() != null && shipmentsDetail.getStatus().contains(Constants.RTSASSIGNED)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            getNavigator().onErrorMessage(e.getMessage());
        }
        return true;
    }

    /*
     * This method is used to check uncaptured images with respect to awb.
     * */
    private int unCapturedAWBsImagesCount() {
        int pendingImageCaptureCount = 0;
        try{
            for (ShipmentsDetail shipmentsDetail : Objects.requireNonNull(listShipmentObserver.get())) {
                if (shipmentsDetail.getStatus() != null && !shipmentsDetail.getStatus().contains(Constants.RTSASSIGNED)) {
                    if (shipmentsDetail.getIMAGEM()) {
                        if (!shipmentsDetail.isIS_IMAGE_CAPTURED()) {
                            pendingImageCaptureCount++;
                        }
                    }
                }
                if((shipmentsDetail.getStatus().equalsIgnoreCase(Constants.RTSMANUALLYDELIVERED) || shipmentsDetail.getStatus().equalsIgnoreCase(Constants.RTSDELIVERED)) && CommonUtils.getRtsDeliveredImagesValue(shipmentsDetail.getFlagsMap()).equalsIgnoreCase("true") && CommonUtils.capturedImageCount(shipmentsDetail.getAwbNo()) <= 1){
                    pendingImageCaptureCount++;
                }
            }
            return pendingImageCaptureCount;
        } catch (Exception e) {
            getNavigator().onErrorMessage(e.getMessage());
        }
        return pendingImageCaptureCount;
    }

    /*
     * This method is used move next activity RTSSignatureActivity after all shipments are in unassigned mode.
     * */
    public void onNextClick() {
        int count = unCapturedAWBsImagesCount();
        StringBuilder errorMessage = new StringBuilder();

        if (areAllShipmentsUnassigned()) {  
            errorMessage.append(getApplication().getString(R.string.all_pending_shipments_should_be_marked_to_move_further));
        } else if (count == 1) {
            errorMessage.append(getApplication().getString(R.string.upload_image_against)).append(count).append(getApplication().getString(R.string.waybill_to_move_further));
        } else if (count != 0) {
            errorMessage.append(getApplication().getString(R.string.upload_image_against)).append(count).append(getApplication().getString(R.string.waybill_to_move_further));
        }
        if (errorMessage.length() > 0) {
            getNavigator().onErrorMessage(errorMessage.toString());
            return;
        }
        getNavigator().onNextClick();
    }

    /*
    * This method is used to get check count in entire shipments.
    * */
    public int getCheckedCount() {
        int i = 0;
        for (ShipmentsDetail shipmentsDetail : Objects.requireNonNull(listShipmentObserver.get())) {
            if (shipmentsDetail.isChecked()) {
                i++;
            }
        }
        return i;
    }

    /*
     * This method is used to get FWD check count in entire shipments.
     * */
    public int getFWDCheckedCount() {
        int i = 0;
        for (ShipmentsDetail shipmentsDetail : Objects.requireNonNull(listShipmentObserver.get())) {
            if (shipmentsDetail.isChecked() && !shipmentsDetail.getParentAwbNo().equalsIgnoreCase("")) {
                i++;
            }
        }
        return i;
    }

    /*
     * This method is used to get RVP check count in entire shipments.
     * */
    public int getRVPCheckedCount() {
        int i = 0;
        for (ShipmentsDetail shipmentsDetail : Objects.requireNonNull(listShipmentObserver.get())) {
            if (shipmentsDetail.isChecked() && shipmentsDetail.getParentAwbNo().equalsIgnoreCase("")) {
                i++;
            }
        }
        return i;
    }

    /*
     * This method is used to get RVP type shipment count in entire shipments.
     * */
    public int getRVPPacketCount() {
        int i = 0;
        for (ShipmentsDetail shipmentsDetail : Objects.requireNonNull(listShipmentObserver.get())) {
            if (shipmentsDetail.getParentAwbNo().equalsIgnoreCase("")) {
                i++;
            }
        }
        return i;
    }

    /*
     * This method is used to get FWD type shipment count in entire shipments.
     * */
    public int getFWDPacketCount() {
        int i = 0;
        for (ShipmentsDetail shipmentsDetail : Objects.requireNonNull(listShipmentObserver.get())) {
            if (!shipmentsDetail.getParentAwbNo().equalsIgnoreCase("")) {
                i++;
            }
        }
        return i;
    }

    /*
     * This method is used to get count of how many shipments are checked in entire shipments.
     * */
    public List<ShipmentsDetail> getCheckedAwbList() {
        List<ShipmentsDetail> shipmentsDetails = new ArrayList<>();
        for (ShipmentsDetail shipmentsDetail : Objects.requireNonNull(listShipmentObserver.get())) {
            if (shipmentsDetail.isChecked()) {
                shipmentsDetails.add(shipmentsDetail);
            }
        }
        return shipmentsDetails;
    }

    /*
     * This method is used update shipment from assigned mode to delivered mode.
     * */
    public void updateManualMark(List<ShipmentsDetail> shipmentsDetailList) {
        try {
            for (ShipmentsDetail shipmentsDetail : shipmentsDetailList) {
                if (shipmentsDetail.isChecked()) {
                    if (Objects.requireNonNull(Mode.get()).equalsIgnoreCase(Constants.RTSDeliveryMode)) {
                        if (Boolean.TRUE.equals(OutPackDamage.get())) {
                            shipmentsDetail.setStatus(Constants.RTSMANUALLYDELIVEREDbutDAMAGED);
                            shipmentsDetail.setChecked(false);
                        } else {
                            shipmentsDetail.setStatus(Constants.RTSMANUALLYDELIVERED);
                            shipmentsDetail.setChecked(false);
                            rtsCapturedImage1.put(shipmentsDetail.getAwbNo(), false);
                            rtsCapturedImage2.put(shipmentsDetail.getAwbNo(), false);
                        }
                        shipmentsDetail.setReasonCode(999);
                    } else if (Objects.requireNonNull(Mode.get()).equalsIgnoreCase(Constants.RTSReAssignMode)) {
                        if (shipmentsDetail.getStatus().contains(Constants.RTSASSIGNED)) {
                            getNavigator().onErrorMessage(getApplication().getString(R.string.this_shipment_is_already_assigned));
                        } else {
                            shipmentsDetail.setStatus(Constants.RTSASSIGNED);
                            shipmentsDetail.setChecked(false);
                        }
                        shipmentsDetail.setReasonCode(0);
                    }
                    updateShipment(shipmentsDetail);
                }
            }
        } catch (Exception e) {
            getNavigator().onErrorMessage(e.getMessage());
        }
    }

    /*
     * This method efficiently retrieves the total count of shipments that have been checked in across the entirety of the shipment.
     * */
    public boolean checkIfAtLeastOneScan() {
        for (ShipmentsDetail shipmentsDetail : Objects.requireNonNull(listShipmentObserver.get())) {
            if (!shipmentsDetail.getStatus().contains(Constants.RTSASSIGNED)) {
                return true;
            }
        }
        return false;
    }

    /*
     * Make all shipment in assigned mode after clicking the button.
     * All shipment behave like fresh shipment.
     * */
    public void reassign() {
        List<ShipmentsDetail> shipmentsDetailList = listShipmentObserver.get();
        for (ShipmentsDetail shipmentsDetail : Objects.requireNonNull(shipmentsDetailList)) {
            shipmentsDetail.setChecked(false);
            shipmentsDetail.setReasonCode(0);
            shipmentsDetail.setIMAGEM(false);
            shipmentsDetail.setIS_IMAGE_CAPTURED(false);
            shipmentsDetail.setIs_flyer_scanned(false);
            shipmentsDetail.setStatus(Constants.RTSASSIGNED);
            shipmentsDetail.setIS_FLYER_WRONG_CAPTURED(false);
        }
        ShipmentsDetail[] shipmentsDetails = new ShipmentsDetail[shipmentsDetailList.size()];
        for (int i = 0; i < shipmentsDetailList.size(); i++) {
            shipmentsDetails[i] = shipmentsDetailList.get(i);
        }
        try {
            getCompositeDisposable().add(getDataManager().markUndelivered(shipmentsDetails).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> getShipments1(Constants.rtsVWDetailID)));
            getCompositeDisposable().add(getDataManager().deleteSyncedImage(String.valueOf(Constants.rtsVWDetailID)).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {}));
        } catch (Exception e) {
            getNavigator().onErrorMessage(e.getMessage());
        }
        getNavigator().notifyAdapter();
    }

    /*
     * This method is used to check all FWD type shipment in entire shipments.
     * */
    public void checkAllForwardShipment() {
        if (freezeFWDShipmentsCheckBox()) {
            getNavigator().setFWDCheckBoxStatus(true);
        } else {
            isAllFWDChecked = !isAllFWDChecked;
            for (ShipmentsDetail shipmentsDetail : Objects.requireNonNull(listShipmentObserver.get())) {
                if (shipmentsDetail.getStatus().contains(Constants.RTSASSIGNED) && !shipmentsDetail.getParentAwbNo().equalsIgnoreCase("")) {
                    shipmentsDetail.setChecked(isAllFWDChecked);
                } else if (shipmentsDetail.getStatus().contains(Constants.RTSASSIGNED) && !isAllFWDChecked && !shipmentsDetail.getParentAwbNo().equalsIgnoreCase("")) {
                    shipmentsDetail.setChecked(false);
                    shipmentsDetail.setStatus(Constants.RTSASSIGNED);
                }
            }
            getNavigator().notifyAdapter();
        }
    }

    /*
     * This method is used to check all RVP type shipment in entire shipments.
     * */
    public void checkAllRVPShipment() {
        if (freezeRVPShipmentsCheckBox()) {
            getNavigator().setRVPCheckBoxStatus(true);
        } else {
            isAllRVPChecked = !isAllRVPChecked;
            for (ShipmentsDetail shipmentsDetail : Objects.requireNonNull(listShipmentObserver.get())) {
                if (shipmentsDetail.getStatus().contains(Constants.RTSASSIGNED) && shipmentsDetail.getParentAwbNo().equalsIgnoreCase("")) {
                    shipmentsDetail.setChecked(isAllRVPChecked);
                } else if (shipmentsDetail.getStatus().contains(Constants.RTSASSIGNED) && !isAllRVPChecked && shipmentsDetail.getParentAwbNo().equalsIgnoreCase("")) {
                    shipmentsDetail.setChecked(false);
                    shipmentsDetail.setStatus(Constants.RTSASSIGNED);
                }
            }
            getNavigator().notifyAdapter();
        }
    }

    /*
     * This method is used show the popup and list of all reason code.
     * */
    public void showPopupWindow() {
        try {
            getCompositeDisposable().add(getDataManager().getRTSReasonCodes().subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(rtsReasonCodeMasters -> {
                List<RTSReasonCodeMaster> filteredRTSMasterReasonCodes;
                List<ShipmentsDetail> listShipment = listShipmentObserver.get();
                int counterSelected = 0;
                for (ShipmentsDetail shipmentsDetail : Objects.requireNonNull(listShipment)) {
                    if (shipmentsDetail.isChecked()) {
                        counterSelected++;
                    }
                }
                if (counterSelected != 0) {
                    try {
                        if (listShipment.size() == 1) {
                            filteredRTSMasterReasonCodes = new ArrayList<>();
                            for (RTSReasonCodeMaster rtsReasonCodeMaster : rtsReasonCodeMasters) {
                                if (rtsReasonCodeMaster.getMasterDataAttributeResponse().DS_SL) {
                                    filteredRTSMasterReasonCodes.add(rtsReasonCodeMaster);
                                } else {
                                    filteredRTSMasterReasonCodes.add(rtsReasonCodeMaster);
                                }
                            }
                            int count = 0;
                            for (int i = 0; i < filteredRTSMasterReasonCodes.size(); i++) {
                                if (filteredRTSMasterReasonCodes.get(i).getMasterDataAttributeResponse().DS_SL) {
                                    Collections.swap(filteredRTSMasterReasonCodes, count, i);
                                    count++;
                                }
                            }
                            Collections.reverse(filteredRTSMasterReasonCodes);
                            getNavigator().showPopupWindowUndelivered(filteredRTSMasterReasonCodes);
                            return;
                        }
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                    if (counterSelected != listShipment.size()) {
                        filteredRTSMasterReasonCodes = new ArrayList<>();
                        for (RTSReasonCodeMaster rtsReasonCodeMaster : rtsReasonCodeMasters) {
                            if (rtsReasonCodeMaster.getMasterDataAttributeResponse().isRTS_SINGLE()) {
                                if (rtsReasonCodeMaster.getMasterDataAttributeResponse().DS_SL) {
                                    filteredRTSMasterReasonCodes.add(rtsReasonCodeMaster);
                                } else {
                                    filteredRTSMasterReasonCodes.add(rtsReasonCodeMaster);
                                }
                            }
                        }
                        int count = 0;
                        for (int i = 0; i < filteredRTSMasterReasonCodes.size(); i++) {
                            if (filteredRTSMasterReasonCodes.get(i).getMasterDataAttributeResponse().DS_SL) {
                                Collections.swap(filteredRTSMasterReasonCodes, count, i);
                                count++;
                            }
                        }
                        Collections.reverse(filteredRTSMasterReasonCodes);
                        getNavigator().showPopupWindowUndelivered(filteredRTSMasterReasonCodes);
                    } else {
                        filteredRTSMasterReasonCodes = new ArrayList<>();
                        for (RTSReasonCodeMaster rtsReasonCodeMaster : rtsReasonCodeMasters) {
                            if (!rtsReasonCodeMaster.getMasterDataAttributeResponse().isRTS_SINGLE()) {
                                if (rtsReasonCodeMaster.getMasterDataAttributeResponse().DS_SL) {
                                    filteredRTSMasterReasonCodes.add(rtsReasonCodeMaster);
                                } else {
                                    filteredRTSMasterReasonCodes.add(rtsReasonCodeMaster);
                                }
                            }
                        }
                        int count = 0;
                        for (int i = 0; i < filteredRTSMasterReasonCodes.size(); i++) {
                            if (filteredRTSMasterReasonCodes.get(i).getMasterDataAttributeResponse().DS_SL) {
                                Collections.swap(filteredRTSMasterReasonCodes, count, i);
                                count++;
                            }
                        }
                        Collections.reverse(filteredRTSMasterReasonCodes);
                        getNavigator().showPopupWindowUndelivered(filteredRTSMasterReasonCodes);
                    }
                } else {
                    getNavigator().showMessage(getApplication().getString(R.string.select_awb_first));
                }

            }));
        } catch (Exception e) {
            getNavigator().onErrorMessage(e.getMessage());
        }
    }

    /*
     * @ShipmentsDetail :- It contains all the shipment with respect to the vendorID.
     * This method is used update all the shipment.
     * */
    public void updateShipment(ShipmentsDetail shipmentsDetail) {
        try {
            getCompositeDisposable().add(getDataManager().updateRTSShipmentDetail(shipmentsDetail).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
                getNavigator().notifyAdapter();
                getShipments1(Constants.rtsVWDetailID);
            }));
        } catch (Exception e) {
            getNavigator().onErrorMessage(e.getMessage());
        }
    }

    /*
     * @ShipmentsDetail:- It is the collection of shipments.
     * This method accept only one shipment at a time and check whether this shipment is in assign mode or not.
     * */
    public void updateScannedShipment(ShipmentsDetail shipmentsDetails) {
        try {
            shipmentsDetails.setChecked(false);
            if (shipmentsDetails.getStatus().contains(Constants.RTSASSIGNED)) {
                if (Objects.requireNonNull(Mode.get()).equalsIgnoreCase(Constants.RTSDeliveryMode)) {
                    if (Boolean.TRUE.equals(OutPackDamage.get())) {
                        shipmentsDetails.setStatus(Constants.RTSDELIVEREDBbutDAMAGED);
                    } else {
                        if (shipmentsDetails.isIs_flyer_scanned()) {
                            shipmentsDetails.setStatus(Constants.RTSDELIVERED);
                        }
                    }
                    shipmentsDetails.setReasonCode(999);
                } else if (Objects.requireNonNull(Mode.get()).equalsIgnoreCase(Constants.RTSReAssignMode)) {
                    if (shipmentsDetails.getStatus().contains(Constants.RTSASSIGNED)) {
                        getNavigator().onErrorMessage(getApplication().getString(R.string.this_shipment_is_already_assigned));
                    } else {
                        shipmentsDetails.setStatus(Constants.RTSASSIGNED);
                        shipmentsDetails.setChecked(false);
                    }
                    shipmentsDetails.setReasonCode(0);
                }
            } else if (shipmentsDetails.getStatus().contains(Constants.RTSDELIVERED)) {
                if (Objects.requireNonNull(Mode.get()).equalsIgnoreCase(Constants.RTSDeliveryMode)) {
                    getNavigator().onErrorMessage(getApplication().getString(R.string.shipment_already_marked_as_delivered));
                } else if (Objects.requireNonNull(Mode.get()).equalsIgnoreCase(Constants.RTSReAssignMode)) {
                    shipmentsDetails.setStatus(Constants.RTSASSIGNED);
                    shipmentsDetails.setChecked(false);
                }
            } else if (shipmentsDetails.getStatus().contains(Constants.RTSUNDELIVERED)) {
                if (Objects.requireNonNull(Mode.get()).equalsIgnoreCase(Constants.RTSDeliveryMode)) {
                    getNavigator().onErrorMessage(getApplication().getString(R.string.shipment_already_marked_as_undelivered));
                } else if (Objects.requireNonNull(Mode.get()).equalsIgnoreCase(Constants.RTSReAssignMode)) {
                    shipmentsDetails.setStatus(Constants.RTSASSIGNED);
                    shipmentsDetails.setChecked(false);
                }
            }
            updateScannedShipment1(shipmentsDetails);
        } catch (Exception e) {
            getNavigator().onErrorMessage(e.getMessage());
        }
    }

    public void updateScannedShipment1(ShipmentsDetail shipmentsDetail) {
        try {
            getCompositeDisposable().add(getDataManager().updateRTSShipmentDetail(shipmentsDetail).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {
                getNavigator().notifyAdapter();
                getShipments1(Constants.rtsVWDetailID);
            }));
        } catch (Exception e) {
            getNavigator().onErrorMessage(e.getMessage());
        }
    }

    public void updateStatistics1() {
        assignedCount = 0;
        deliveredCount = 0;
        undeliveredCount = 0;
        for (ShipmentsDetail shipmentsDetail : Objects.requireNonNull(listShipmentObserver.get())) {
            if (shipmentsDetail.getStatus().contains(Constants.RTSASSIGNED)) {
                assignedCount++;
            }
            if (shipmentsDetail.getStatus().contains(Constants.RTSDELIVERED) || shipmentsDetail.getStatus().contains(Constants.RTSMANUALLYDELIVERED) || shipmentsDetail.getStatus().contains(Constants.RTSMANUALLYDELIVEREDbutDAMAGED) || shipmentsDetail.getStatus().contains(Constants.RTSDELIVEREDBbutDAMAGED)) {
                deliveredCount++;
                ArrayList<String> checkListAWB = new ArrayList<>();
                checkListAWB.add(shipmentsDetail.getAwbNo().toString());
                getNavigator().setNewAwbList(checkListAWB);
            }
            if (shipmentsDetail.getStatus().contains(Constants.RTSUNDELIVERED)) {
                undeliveredCount++;
                ArrayList<String> checkListUndeliveredAWB = new ArrayList<>();
                checkListUndeliveredAWB.add(shipmentsDetail.getAwbNo().toString());
                getNavigator().setNewAwbList(checkListUndeliveredAWB);
            }
            if (shipmentsDetail.getStatus().contains(Constants.RTSDISPUTED)) {
                disputedCount++;
                ArrayList<String> checkListDisputedAWB = new ArrayList<>();
                checkListDisputedAWB.add(shipmentsDetail.getAwbNo().toString());
                getNavigator().setNewAwbList(checkListDisputedAWB);
            }
        }
        Assigned.set("Assigned : " + assignedCount);
        Delivered.set("Delivered : " + deliveredCount);
        Pending.set("Undelivered : " + undeliveredCount);
        setFinalCount(deliveredCount, undeliveredCount);
    }

    public void setFinalCount(int delivered, int undelivered) {
        getNavigator().getFinalCount(delivered, undelivered);
    }

    public void markUndelivered(int reasonCode, RTSReasonCodeMaster rtsReasonCodeMaster, Integer reason_id, boolean imageM, boolean is_image_captured) {
        List<ShipmentsDetail> list = new ArrayList<>();
        boolean isAnyChecked = false;
        for (ShipmentsDetail shipmentsDetail1 : Objects.requireNonNull(listShipmentObserver.get())) {
            if (shipmentsDetail1.isChecked()) {
                isAnyChecked = true;
                if (rtsReasonCodeMaster.getMasterDataAttributeResponse().DS_SL) {
                    shipmentsDetail1.setStatus(Constants.RTSDISPUTED);
                } else {
                    shipmentsDetail1.setStatus(Constants.RTSUNDELIVERED);
                }
                shipmentsDetail1.setReasonCode(reasonCode);
                shipmentsDetail1.setIMAGEM(imageM);
                shipmentsDetail1.setChecked(false);
                shipmentsDetail1.setIS_IMAGE_CAPTURED(is_image_captured);
                shipmentsDetail1.setReason_id(reason_id);
                shipmentsDetail1.setDataTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                list.add(shipmentsDetail1);
            }
        }
        if (!isAnyChecked) {
            getNavigator().showToast();
        }
        ShipmentsDetail[] shipmentsDetails = new ShipmentsDetail[list.size()];
        for (int i = 0; i < list.size(); i++) {
            shipmentsDetails[i] = list.get(i);
        }
        try {
            getCompositeDisposable().add(getDataManager().markUndelivered(shipmentsDetails).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> getShipments1(Constants.rtsVWDetailID)));
        } catch (Exception e) {
            getNavigator().onErrorMessage(e.getMessage());
        }
    }

    public String getAwb() {
        ShipmentsDetail shipmentsDetail = new ShipmentsDetail();
        return String.valueOf(shipmentsDetail.getAwbNo());
    }

    public void getScannedShipment(long ScannedAwbNo) {
        ShipmentsDetail[] shipmentsDetails = {new ShipmentsDetail()};
        try {
            getCompositeDisposable().add(getDataManager().getShipmentData(ScannedAwbNo).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(shipmentsDetail -> {
                shipmentsDetails[0] = shipmentsDetail;
                getNavigator().getShipmentDetail(shipmentsDetails[0]);
            }));
        } catch (Exception e) {
            getNavigator().onErrorMessage(e.getMessage());
        }
    }

    public void isAttributeAvailable(int reasonCode, RTSReasonCodeMaster rtsReasonCodeMaster, Integer reason_id, boolean is_image_captured) {
        try {
            getCompositeDisposable().add(getDataManager().isAttributeAvailable(String.valueOf(reasonCode)).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(reason_code_enabled -> markUndelivered(reasonCode, rtsReasonCodeMaster, reason_id, reason_code_enabled.getMasterDataAttributeResponse().IMAGEM, is_image_captured)));
        } catch (Exception e) {
            getNavigator().onErrorMessage(e.getMessage());
        }
    }

    public void uploadImageOnServer(String imageName, String imageUri, String imageCode, long awbNo, int drsNo, long vendorId, String ud_rts_image, Context context) {
        showProgressDialog(context, context.getString(R.string.image_uploading));
        try {
            File image_file = new File(imageUri);
            byte[] bytes = CryptoUtils.decryptFile1(image_file.toString(), Constants.ENC_DEC_KEY);
            RequestBody mFile = RequestBody.create(MediaType.parse("application/octet-stream"), Objects.requireNonNull(bytes));
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", image_file.getName(), mFile);
            RequestBody awb_no = RequestBody.create(MultipartBody.FORM, String.valueOf(awbNo));
            RequestBody drs_no = RequestBody.create(MultipartBody.FORM, String.valueOf(drsNo));
            RequestBody image_code = RequestBody.create(MultipartBody.FORM, String.valueOf(imageCode));
            RequestBody image_name = RequestBody.create(MultipartBody.FORM, String.valueOf(imageName));
            RequestBody image_type = RequestBody.create(MultipartBody.FORM, GlobalConstant.ImageTypeConstants.OTHERS);
            Map<String, RequestBody> map = new HashMap<>();
            map.put("image", mFile);
            map.put("awb_no", awb_no);
            map.put("drs_no", drs_no);
            map.put("image_code", image_code);
            map.put("image_name", image_name);
            map.put("image_type", image_type);
            Map<String, String> headers = new HashMap<>();
            headers.put("token", getDataManager().getAuthToken());
            headers.put("Accept", "application/json");
            try {
                getCompositeDisposable().add(getDataManager().doImageUploadApiCall(getDataManager().getAuthToken(), getDataManager().getEcomRegion(), GlobalConstant.ImageTypeConstants.OTHERS, headers, map, fileToUpload).doOnSuccess(imageQualityResponse -> {}).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(imageUploadResponse -> {
                    dismissProgressDialog();
                    String errorDescription;
                    try {
                        if (imageUploadResponse == null) {
                            getNavigator().onErrorMessage(getApplication().getString(R.string.api_response_null));
                            return;
                        }
                        errorDescription = (imageUploadResponse.getDescription() == null) ? getApplication().getString(R.string.image_upload_api_response_false) : imageUploadResponse.getDescription();
                        String status = imageUploadResponse.getStatus();
                        if (status == null || (!status.equalsIgnoreCase("true") && !status.equalsIgnoreCase("Success"))) {
                            getNavigator().onErrorMessage(errorDescription);
                        } else {
                            saveImageDB(imageUri, imageCode, imageName, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_COMPLETE, vendorId, awbNo, imageUploadResponse.getImageId(), ud_rts_image, 2);
                        }
                    } catch (Exception e) {
                        getNavigator().onErrorMessage(String.valueOf(e));
                    }
                }, throwable -> {
                    dismissProgressDialog();
                    getNavigator().onErrorMessage(String.valueOf(throwable));
                }));
            } catch (Exception e) {
                dismissProgressDialog();
                getNavigator().onErrorMessage(String.valueOf(e));
            }
        } catch (Exception e) {
            dismissProgressDialog();
            getNavigator().onErrorMessage(String.valueOf(e));
        }
    }

    public void saveImageDB(String imageUri, String imageCode, String name, int image_sync_status, long rtsVWDetailID, long shipment_awb_no, int image_id, String ud_rts_image, int status) {
        try {
            imageUploadSuccessLiveData.setValue(true);
            ImageModel imageModel = new ImageModel();
            imageModel.setDraNo(String.valueOf(shipment_awb_no));
            imageModel.setAwbNo(String.valueOf(rtsVWDetailID));
            imageModel.setImageName(name);
            imageModel.setImage(imageUri);
            imageModel.setImageCode(imageCode);
            imageModel.setStatus(status);
            imageModel.setImageCurrentSyncStatus(image_sync_status);
            imageModel.setImageFutureSyncTime(Calendar.getInstance().getTimeInMillis());
            imageModel.setImageId(image_id);
            imageModel.setDate(Calendar.getInstance().getTimeInMillis());
            imageModel.setShipmentType(GlobalConstant.ShipmentTypeConstants.RTS);
            imageModel.setImageType(ud_rts_image);
            getCompositeDisposable().add(getDataManager().saveImage(imageModel).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> {}));
        } catch (Exception e) {
            getNavigator().onErrorMessage(e.getMessage());
        }
    }

    public void updateImageCapturedFlag(long shipment_awb_no, int is_image_captured) {
        try {
            getCompositeDisposable().add(getDataManager().updateRtsImageCapturedStatus(shipment_awb_no, is_image_captured).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(aBoolean -> getShipments1(Constants.rtsVWDetailID)));
        } catch (Exception e) {
            getNavigator().onErrorMessage(e.getMessage());
        }
    }

    public void showManualMandateDialog(String message, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        Dialog dialog = builder.create();
        dialog.show();
    }

    public boolean freezeFWDShipmentsCheckBox() {
        boolean atLeastOneFWD = false;
        for (ShipmentsDetail shipmentsDetail : Objects.requireNonNull(listShipmentObserver.get())) {
            if (!shipmentsDetail.getParentAwbNo().equalsIgnoreCase("")) {
                if (shipmentsDetail.getStatus().contains(Constants.RTSASSIGNED)) {
                    return false;
                }
                atLeastOneFWD = true;
            }
        }
        return atLeastOneFWD;
    }

    public boolean freezeRVPShipmentsCheckBox() {
        boolean atLeastOneRVP = false;
        for (ShipmentsDetail shipmentsDetail : Objects.requireNonNull(listShipmentObserver.get())) {
            if (shipmentsDetail.getParentAwbNo().equalsIgnoreCase("")) {
                if (shipmentsDetail.getStatus().contains(Constants.RTSASSIGNED)) {
                    return false;
                }
                atLeastOneRVP = true;
            }
        }
        return atLeastOneRVP;
    }

    private void showProgressDialog(Context context, String loadingMessage) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_progress_dialog);
        dialog.setCancelable(false);
        TextView loadingText = dialog.findViewById(R.id.dialog_loading_text);
        loadingText.setText(loadingMessage);
        dialog.show();
    }

    private void dismissProgressDialog(){
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }
}