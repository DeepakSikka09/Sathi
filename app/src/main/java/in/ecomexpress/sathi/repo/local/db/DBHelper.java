package in.ecomexpress.sathi.repo.local.db;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.repo.local.data.commit.PushApi;
import in.ecomexpress.sathi.repo.local.db.db_utils.SathiDatabase;
import in.ecomexpress.sathi.repo.local.db.model.ApiUrlData;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.local.db.model.ImageModel;
import in.ecomexpress.sathi.repo.local.db.model.LiveTrackingLogTable;
import in.ecomexpress.sathi.repo.local.db.model.MsgLinkData;
import in.ecomexpress.sathi.repo.local.db.model.RVPQCImageTable;
import in.ecomexpress.sathi.repo.local.db.model.Remark;
import in.ecomexpress.sathi.repo.local.db.model.RescheduleEdsD;
import in.ecomexpress.sathi.repo.local.db.model.RvpWithQC;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.call.Call;
import in.ecomexpress.sathi.repo.remote.model.consignee_profile.ProfileFound;
import in.ecomexpress.sathi.repo.remote.model.drs_list.DRSSequence;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.DRSReturnToShipperTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.DRSReturnToShipperTypeNewResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.Details;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.IRTSBaseInterface;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.ShipmentsDetail;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.VendorResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.WarehouseResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.RvpQualityCheck;
import in.ecomexpress.sathi.repo.remote.model.masterdata.CallbridgeConfiguration;
import in.ecomexpress.sathi.repo.remote.model.masterdata.CbPstnOptions;
import in.ecomexpress.sathi.repo.remote.model.masterdata.DashboardBanner;
import in.ecomexpress.sathi.repo.remote.model.masterdata.EDSReasonCodeMaster;
import in.ecomexpress.sathi.repo.remote.model.masterdata.Forward;
import in.ecomexpress.sathi.repo.remote.model.masterdata.ForwardReasonCodeMaster;
import in.ecomexpress.sathi.repo.remote.model.masterdata.GeneralQuestion;
import in.ecomexpress.sathi.repo.remote.model.masterdata.GlobalConfigurationMaster;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterDataConfig;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterDocumentList;
import in.ecomexpress.sathi.repo.remote.model.masterdata.RTSReasonCodeMaster;
import in.ecomexpress.sathi.repo.remote.model.masterdata.RVPReasonCodeMaster;
import in.ecomexpress.sathi.repo.remote.model.masterdata.Reverse;
import in.ecomexpress.sathi.repo.remote.model.masterdata.SampleQuestion;
import in.ecomexpress.sathi.repo.remote.model.payphi.raise_dispute.PaymentDisputedAwb;
import in.ecomexpress.sathi.utils.Constants;
import io.reactivex.Observable;

@Singleton
public class DBHelper implements IDBHelper {

    private final SathiDatabase mAppDatabase;

    @Inject
    public DBHelper(SathiDatabase mAppDatabase) {
        this.mAppDatabase = mAppDatabase;
    }

    @Override
    public Observable<Boolean> saveDRSForwardList(List<DRSForwardTypeResponse> drsForwardTypeResponse) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsForwardDAO().insertAll(drsForwardTypeResponse);
            return true;
        });
    }

    @Override
    public Observable<Boolean> saveProfileFoundList(List<ProfileFound> profileFoundList) {
        return Observable.fromCallable(() -> {
            mAppDatabase.profileFoundDAO().insertAll(profileFoundList);
            return true;
        });
    }

    @Override
    public Observable<Boolean> saveToDRSSequenceTable(List<DRSSequence> drsSequenceList) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsSequenceDao().insertAll(drsSequenceList);
            return true;
        });
    }

    @Override
    public Observable<Boolean> updateDRSSequenceTable(List<DRSSequence> drsSequenceList) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsSequenceDao().updateAll(drsSequenceList);
            return true;
        });
    }

    @Override
    public Observable<List<DRSSequence>> getAllDRSSequenceData() {
        return Observable.fromCallable(() -> mAppDatabase.drsSequenceDao().getAllDRSSequenceList());
    }

    @Override
    public Observable<Boolean> deleteAllDataDRSSequence() {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsSequenceDao().removeAllSequenceData();
            return true;
        });
    }

    @Override
    public Observable<Boolean> saveDRSRTSList(List<DRSReturnToShipperTypeResponse> drsReturnToShipperTypeResponses) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsRtsDAO().insertAll(drsReturnToShipperTypeResponses);
            return true;
        });
    }

    @Override
    public Observable<Boolean> saveDRSNewRTSList(DRSReturnToShipperTypeNewResponse drsReturnToShipperTypeResponses) {
        return Observable.fromCallable(() -> {
            for (IRTSBaseInterface irtsBaseInterface : drsReturnToShipperTypeResponses.getCombinedList()) {
                Details details = irtsBaseInterface.getDetails();
                details.setVendor(irtsBaseInterface instanceof VendorResponse);
                mAppDatabase.drsRtsNewDAO().insertDetails(details);
                for (ShipmentsDetail shipmentsDetail : irtsBaseInterface.getShipmentsDetails()) {
                    ShipmentsDetail sDetail1 = mAppDatabase.drsRtsNewDAO().getShipment(shipmentsDetail.getAwbNo());
                    if (sDetail1 == null) {
                        // Status and Shipment status will assigned and 0;
                        mAppDatabase.drsRtsNewDAO().updateShipmentDetailData(0, "Assigned", Math.toIntExact(details.getId()));
                        // For hide green tick:-
                        mAppDatabase.drsRtsNewDAO().updateSyncStatus(details.getId(), 0);
                        // If new packets comes for same vendor then deleting the data with respect of vendor id:-
                        mAppDatabase.pushApiDAO().deleteAwb(details.getId());
                        shipmentsDetail.setDetail_id(details.getId());
                        mAppDatabase.drsRtsNewDAO().insertShipment(shipmentsDetail);
                        continue;
                    }
                    if (sDetail1.getStatus().equalsIgnoreCase(Constants.ASSIGNED)) {
                        shipmentsDetail.setDetail_id(details.getId());
                        mAppDatabase.drsRtsNewDAO().insertShipment(shipmentsDetail);
                    }
                }
            }
            return true;
        });
    }

    @Override
    public Observable<Boolean> saveDRSRVP(DRSReverseQCTypeResponse drsReverseQCTypeResponses) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsRvpWithQCDAO().insert(drsReverseQCTypeResponses);
            return true;
        });
    }

    @Override
    public Observable<Boolean> saveNewDrsEDS(EDSResponse edsResponse) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsEdsNewDao().insert(edsResponse);
            return true;
        });
    }

    @Override
    public Observable<Boolean> saveCallbridgeConfiguration(CallbridgeConfiguration callbridgeConfiguration) {
        return Observable.fromCallable(() -> {
            mAppDatabase.callbridgedao().insert(callbridgeConfiguration);
            return true;
        });
    }

    @Override
    public Observable<Boolean> saveDashboardBanner(List<DashboardBanner> dashboardBanner) {
        return Observable.fromCallable(() -> {
            mAppDatabase.dashboardDao().insert(dashboardBanner);
            return true;
        });
    }

    @Override
    public Observable<List<DashboardBanner>> getAllDashboardBanner() {
        return Observable.fromCallable(() -> mAppDatabase.dashboardDao().getAllDashboardBanner());
    }

    @Override
    public Observable<Boolean> updatePushSyncStatusToZero(List<Long> awbNo) {
        return Observable.fromCallable(() -> {
            try {
                mAppDatabase.pushApiDAO().updatePushSyncStatusToZero(awbNo);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    @Override
    public Observable<Boolean> updatePushSyncStatusToZeroOnClick(long awbNo) {
        return Observable.fromCallable(() -> {
            try {
                mAppDatabase.pushApiDAO().updatePushSyncStatusToZeroOnClick(awbNo);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    @Override
    public Observable<Boolean> updatePushShipmentStatus(long awbNo, int shipment_status) {
        return Observable.fromCallable(() -> {
            try {
                mAppDatabase.pushApiDAO().updatePushShipmentStatus(awbNo, shipment_status);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    @Override
    public Observable<Boolean> saveDRSRVPList(List<DRSReverseQCTypeResponse> drsReverseQCTypeResponses) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsRvpWithQCDAO().insertAll(drsReverseQCTypeResponses);
            return true;
        });
    }

    @Override
    public Observable<Boolean> getRescheduleFlag(String awb) {
        return Observable.fromCallable(() -> mAppDatabase.rescheduleEdsDao().getEdsRescheduleDataFlag(awb));
    }

    @Override
    public Observable<Boolean> saveDRSRVPListQualityCheck(List<RvpQualityCheck> rvpQualityChecks) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsRvpWithQCDAO().insertQC(rvpQualityChecks);
            return true;
        });
    }

    @Override
    public Observable<List<DRSForwardTypeResponse>> getAllForwardList() {
        return Observable.fromCallable(() -> mAppDatabase.drsForwardDAO().loadAllForwardList());
    }

    @Override
    public Observable<List<DRSForwardTypeResponse>> getUnSyncForwardList() {
        return Observable.fromCallable(() -> mAppDatabase.drsForwardDAO().getUnSyncForwardList());
    }

    @Override
    public Observable<List<EDSResponse>> getUnSyncEdsList() {
        return Observable.fromCallable(() -> mAppDatabase.drsEdsNewDao().getUnSyncEdsList());
    }

    @Override
    public Observable<List<DRSReverseQCTypeResponse>> getUnSyncRVPList() {
        return Observable.fromCallable(() -> mAppDatabase.drsRvpWithQCDAO().getUnSyncRVPList());
    }

    @Override
    public Observable<List<CbPstnOptions>> getCbPstnOptions() {
        return Observable.fromCallable(() -> mAppDatabase.callBridgePSTNDao().getAllCbPstnoptions());
    }

    @Override
    public Observable<List<GlobalConfigurationMaster>> getglobalConfigurationMasters() {
        return Observable.fromCallable(() -> mAppDatabase.globalConfigDao().getglobalConfigurationMasters());
    }

    @Override
    public Observable<Boolean> saveCallbridgecb_pstn_options(List<CbPstnOptions> cb_pstn_options) {
        return Observable.fromCallable(() -> {
            mAppDatabase.callBridgePSTNDao().insertall(cb_pstn_options);
            return true;
        });
    }

    @Override
    public Observable<List<DRSForwardTypeResponse>> getDRSListForward() {
        return Observable.fromCallable(() -> mAppDatabase.drsForwardDAO().loadAllForwardDRSList());
    }

    @Override
    public Observable<List<DRSReturnToShipperTypeResponse>> getDRSListRTS() {
        return Observable.fromCallable(() -> mAppDatabase.drsRtsDAO().loadAllRTSDRSList());
    }

    @Override
    public Observable<DRSReturnToShipperTypeNewResponse> getDRSListNewRTS() {
        return Observable.fromCallable(() -> {
            List<IRTSBaseInterface> list = new ArrayList<>();
            List<Details> listDetails = mAppDatabase.drsRtsNewDAO().loadAllDetails();
            for (Details details : listDetails) {
                IRTSBaseInterface irtsBaseInterface;
                if (details.isVendor()) {
                    irtsBaseInterface = new VendorResponse();
                } else {
                    irtsBaseInterface = new WarehouseResponse();
                }
                int delivered = mAppDatabase.drsRtsNewDAO().getRTSDeliveredCount(details.getId(), Constants.RTSDELIVERED) + mAppDatabase.drsRtsNewDAO().getRTSDeliveredCount(details.getId(), Constants.RTSDELIVEREDBbutDAMAGED);
                details.setDelivered(delivered);
                int unDelivered = mAppDatabase.drsRtsNewDAO().getRTSUndeliveredCount(details.getId(), "%" + Constants.RTSUNDELIVERED + "%");
                details.setUndelivered(unDelivered);
                int mannuallyDelivered = mAppDatabase.drsRtsNewDAO().getRTSMannnuallyDeliveredCount(details.getId(), Constants.RTSMANUALLYDELIVERED) + mAppDatabase.drsRtsNewDAO().getRTSMannnuallyDeliveredCount(details.getId(), Constants.RTSMANUALLYDELIVEREDbutDAMAGED);
                details.setMnnuallyDelivered(mannuallyDelivered);
                int totalShipment = mAppDatabase.drsRtsNewDAO().getRTSTotalCount(details.getId());
                details.setTotalShipmentCount(totalShipment);
                irtsBaseInterface.setDetails(details);
                irtsBaseInterface.setShipmentsDetails(mAppDatabase.drsRtsNewDAO().getAllShipment(details.getId()));
                list.add(irtsBaseInterface);
            }
            DRSReturnToShipperTypeNewResponse drsReturnToShipperTypeNewResponse = new DRSReturnToShipperTypeNewResponse();
            drsReturnToShipperTypeNewResponse.addAllInCombinedList(list);
            return drsReturnToShipperTypeNewResponse;
        });
    }

    @Override
    public Observable<Details> getVWDetails(long id) {
        return Observable.fromCallable(() -> {
            Details details = mAppDatabase.drsRtsNewDAO().loadDetails(id);
            int delivered = mAppDatabase.drsRtsNewDAO().getRTSDeliveredCount(details.getId(), Constants.RTSDELIVERED) + mAppDatabase.drsRtsNewDAO().getRTSDeliveredCount(details.getId(), Constants.RTSDELIVEREDBbutDAMAGED);
            details.setDelivered(delivered);
            int unDelivered = mAppDatabase.drsRtsNewDAO().getRTSUndeliveredCount(details.getId(), "%" + Constants.RTSUNDELIVERED + "%");
            details.setUndelivered(unDelivered);
            int manuallyDelivered = mAppDatabase.drsRtsNewDAO().getRTSMannnuallyDeliveredCount(details.getId(), Constants.RTSMANUALLYDELIVERED) + mAppDatabase.drsRtsNewDAO().getRTSMannnuallyDeliveredCount(details.getId(), Constants.RTSMANUALLYDELIVEREDbutDAMAGED);
            details.setMnnuallyDelivered(manuallyDelivered);
            int disputeDeliveryCount = mAppDatabase.drsRtsNewDAO().getRTSDisputeDeliveryCount(details.getId(), "%" + Constants.RTSDISPUTED + "%");
            details.setDispute_delivery(disputeDeliveryCount);
            int totalShipment = mAppDatabase.drsRtsNewDAO().getRTSTotalCount(details.getId());
            details.setTotalShipmentCount(totalShipment);
            return details;
        });
    }

    @Override
    public Observable<List<ShipmentsDetail>> getVWShipmentList(long vwID) {
        return Observable.fromCallable(() -> mAppDatabase.drsRtsNewDAO().getAllShipment(vwID));
    }

    @Override
    public Observable<List<DRSReverseQCTypeResponse>> getDRSListRVP() {
        return Observable.fromCallable(() -> {
            List<DRSReverseQCTypeResponse> response = mAppDatabase.drsRvpWithQCDAO().loadAllRVPDRSList();
            for (DRSReverseQCTypeResponse drsReverseQCTypeResponse : response) {
                RvpWithQC rvpWithQC = mAppDatabase.drsRvpWithQCDAO().getRvpWithQc(drsReverseQCTypeResponse.getCompositeKey());
                drsReverseQCTypeResponse.getShipmentDetails().setQualityChecks(rvpWithQC.rvpQualityCheckList);
            }
            return response;
        });
    }

    @Override
    public Observable<List<EDSResponse>> getDrsListNewEds() {
        return Observable.fromCallable(() -> mAppDatabase.drsEdsNewDao().loadAllEDSList());
    }

    @Override
    public Observable<DRSForwardTypeResponse> getForwardDRS(String composite_key) {
        return Observable.fromCallable(() -> mAppDatabase.drsForwardDAO().loadAllForwardDRS(composite_key));
    }

    @Override
    public Observable<DRSForwardTypeResponse> getForwardDRSCompositeKey(Long awb) {
        return Observable.fromCallable(() -> mAppDatabase.drsForwardDAO().loadForwardDRSList(awb));
    }

    @Override
    public Observable<DRSForwardTypeResponse> fetchObdQualityCheckData(Long awb) {
        return Observable.fromCallable(() -> mAppDatabase.drsForwardDAO().fetchObdQualityCheckData(awb));
    }

    @Override
    public Observable<Integer> getDuplicateValueCount(String detailId) {
        return Observable.fromCallable(() -> mAppDatabase.drsRtsNewDAO().getDuplicateValueCount(detailId));
    }

    @Override
    public Observable<DRSReturnToShipperTypeResponse> getRTSDRS(long awbNo) {
        return Observable.fromCallable(() -> mAppDatabase.drsRtsDAO().loadAllRTSDRS(awbNo));
    }

    @Override
    public Observable<DRSReverseQCTypeResponse> getRVPDRS(String composite_key) {
        return Observable.fromCallable(() -> mAppDatabase.drsRvpWithQCDAO().loadRVPDRS(composite_key));
    }

    @Override
    public Observable<List<RvpQualityCheck>> getQcValuesForAwb() {
        return Observable.fromCallable(() -> mAppDatabase.drsRvpWithQCDAO().getQcValuesForAwb());
    }

    @Override
    public Observable<Boolean> isRVPDRSExist(String composite_key) {
        return Observable.fromCallable(() -> {
            long count = mAppDatabase.drsRvpWithQCDAO().getRVPAWbExist(composite_key);
            return count > 0;
        });
    }

    @Override
    public Observable<Long> insertOrUpdateForward(DRSForwardTypeResponse drsForwardTypeResponse) {
        return Observable.fromCallable(() -> mAppDatabase.drsForwardDAO().insert(drsForwardTypeResponse));
    }

    @Override
    public Observable<Long> getFWDStatusCount(int status) {
        return Observable.fromCallable(() -> mAppDatabase.drsForwardDAO().getFWDStatusCount(status));
    }

    @Override
    public Observable<Long> getRTSStatusCount(int status) {
        return Observable.fromCallable(() -> mAppDatabase.drsRtsNewDAO().getRTSStatusCount(status));
    }

    @Override
    public Observable<Long> getRVPStatusCount(int status) {
        return Observable.fromCallable(() -> mAppDatabase.drsRvpWithQCDAO().getRVPStatusCount(status));
    }

    @Override
    public Observable<Boolean> deleteQCData(int drs, long awbNo) {
        return Observable.fromCallable(() -> {
            try {
                mAppDatabase.drsRvpWithQCDAO().deleteQCData(drs, awbNo);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });

    }

    @Override
    public Observable<String> getTypeOfShipment(String awb) {
        return Observable.fromCallable(() -> mAppDatabase.drsRvpWithQCDAO().getTypeOfShipment(awb));
    }

    @Override
    public Observable<String> getPhonePeShipmentType(String awb) {
        return Observable.fromCallable(() -> mAppDatabase.drsRvpWithQCDAO().getPhonePeShipmentType(awb));
    }

    @Override
    public Observable<Long> getEDSStatusCount(int status) {
        return Observable.fromCallable(() -> mAppDatabase.drsEdsNewDao().getEDSStatusCount(status));
    }

    @Override
    public Observable<Double> getCodCount() {
        return Observable.fromCallable(() -> mAppDatabase.drsForwardDAO().getCodCount());
    }

    @Override
    public Observable<Double> getEcodStatusCount() {
        return Observable.fromCallable(() -> mAppDatabase.drsForwardDAO().getECodCount());
    }

    @Override
    public Observable<Boolean> isEDSDRSNewExist(String compositeKey) {
        return Observable.fromCallable(() -> {
            long count = mAppDatabase.drsEdsNewDao().getEDSNewAWbExist(compositeKey);
            return count > 0;
        });
    }

    @Override
    public Observable<Boolean> saveEDSActivityNewWizardList(List<EDSActivityWizard> edsActivityWizards) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsEdsNewDao().insertActivityWizard(edsActivityWizards);
            return true;
        });
    }

    @Override
    public Observable<Boolean> updateForwardStatus(String composite_key, int status) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsForwardDAO().updateForwardStatus(composite_key, status);
            return true;
        });
    }

    @Override
    public Observable<Boolean> insertcodCollected(Long awbNo, float amount) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsForwardDAO().updatecodCollected(awbNo, amount);
            return true;
        });
    }

    @Override
    public Observable<Boolean> insertEcodCollected(Long awbNo, float amount) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsForwardDAO().updateEcodCollected(awbNo, amount);
            return true;
        });
    }

    @Override
    public Observable<Boolean> updateForwardCallAttempted(Long awbNo, int isCallattempted) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsForwardDAO().updateForwardCallAttempted(awbNo, isCallattempted);
            return true;
        });
    }

    @Override
    public Observable<Boolean> updateRVPCallAttempted(Long awbNo, int isCallAttempted) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsRvpWithQCDAO().updateRVPCallAttempted(awbNo, isCallAttempted);
            return true;
        });
    }

    @Override
    public Observable<RTSReasonCodeMaster> isAttributeAvailable(String reasonCode) {
        return Observable.fromCallable(() -> mAppDatabase.drsRTSMasterDAO().isAttributeAvailable(reasonCode));
    }

    @Override
    public Observable<Boolean> updateRTSCallAttempted(Long id, int isCallAttempted) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsRtsNewDAO().updateRTSCallAttempted(id, isCallAttempted);
            return true;
        });
    }

    @Override
    public Observable<Boolean> updateEDSCallAttempted(Long awbNo, int isCallAttempted) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsEdsNewDao().updateEDSCallAttempted(awbNo, isCallAttempted);
            return true;
        });
    }

    @Override
    public Observable<Boolean> updateRvpStatus(String composite_key, int status) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsRvpWithQCDAO().updateRvpStatus(composite_key, status);
            return true;
        });
    }

    @Override
    public Observable<Boolean> updateRtsStatus(Long id, int status) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsRtsNewDAO().updateRtsStatus(id, status);
            return true;
        });
    }

    @Override
    public Observable<Boolean> updateEdsStatus(String compositeKey, int status) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsEdsNewDao().updateEdsStatus(compositeKey, status);
            return true;
        });
    }

    @Override
    public Observable<Boolean> updateOTPForward(long awbNo, String otp) {
        return Observable.fromCallable(() -> {
            try {
                mAppDatabase.drsForwardDAO().updateOTPForward(awbNo, otp);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    @Override
    public Observable<Boolean> updateAssignDataForward(long awbNo, String assign_date) {
        return Observable.fromCallable(() -> {
            try {
                mAppDatabase.drsForwardDAO().updateAssignDataForward(awbNo, assign_date);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    @Override
    public Observable<Boolean> updateTotalAttemptsForward(long awbNo, int attempts) {
        return Observable.fromCallable(() -> {
            try {
                mAppDatabase.drsForwardDAO().updateTotalAttemptsForward(awbNo, attempts);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    @Override
    public Observable<Boolean> deleteShipment(String compositkey) {
        return Observable.fromCallable(() -> {
            try {
                mAppDatabase.drsForwardDAO().deleteShipment(compositkey);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    @Override
    public Observable<Boolean> updateForwardShipment(String awb_no, int syncStatus, int status, int drs_id, String composite_key_new) {
        return Observable.fromCallable(() -> {
            try {
                mAppDatabase.drsForwardDAO().updateForwardShipment(awb_no, syncStatus, status, drs_id, composite_key_new);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    @Override
    public Observable<Boolean> updateOTPEDS(long awbNo, String otp) {
        return Observable.fromCallable(() -> {
            try {
                mAppDatabase.drsEdsNewDao().updateOTPEDS(awbNo, otp);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    @Override
    public Observable<List<ImageModel>> getUDImage(String imagetype) {
        return Observable.fromCallable(() -> mAppDatabase.imageDAO().getUDImage(imagetype));
    }

    @Override
    public Observable<Boolean> updateRTSShipmentDetail(ShipmentsDetail shipmentsDetail) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsRtsNewDAO().updateShipment(shipmentsDetail);
            return true;
        });
    }

    @Override
    public Observable<Boolean> updateRtsImageCapturedStatus(Long awb_no, int is_image_captured) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsRtsNewDAO().updateRtsImageCapturedStatus(awb_no, is_image_captured);
            return true;
        });
    }

    @Override
    public Observable<Boolean> deleteAllTablesOnStopTrip() {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsEdsNewDao().nukeTableEDSActivityWizard();
            mAppDatabase.drsForwardDAO().nukeTable();
            mAppDatabase.drsSequenceDao().nukeTable();
//            mAppDatabase.drsForwardMasterDAO().nukeTable();
            mAppDatabase.drsEdsNewDao().nukeTableEDSResponse();
//            mAppDatabase.drsedsReasonCodeDao().nukeTable();
            mAppDatabase.drsRtsNewDAO().nukeDetailTable();
            mAppDatabase.drsRtsNewDAO().nukeShipmentTable();
            mAppDatabase.drsRvpWithQCDAO().nukeTable();
            mAppDatabase.edsDocumentListMasterActivityDAO().nukeTable();
//            mAppDatabase.edsMasterActivityDAO().nukeTable();
//            mAppDatabase.drsedsReasonCodeDao().nukeTable();
//            mAppDatabase.edsopvMasterActivityDAO().nukeTable();
//            mAppDatabase.drsRVPQCMasterDao().nukeTable();
            mAppDatabase.drsRtsDAO().nukeTable();
            mAppDatabase.DisputedAwbDAO().nukeTable();
            return true;
        });
    }

    @Override
    public Observable<Boolean> deleteAllTables() {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsEdsNewDao().nukeTableEDSActivityWizard();
            mAppDatabase.drsForwardDAO().nukeTable();
            mAppDatabase.drsSequenceDao().nukeTable();
            mAppDatabase.drsForwardMasterDAO().nukeTable();
            mAppDatabase.drsEdsNewDao().nukeTableEDSResponse();
            mAppDatabase.drsedsReasonCodeDao().nukeTable();
            mAppDatabase.drsRtsNewDAO().nukeDetailTable();
            mAppDatabase.drsRtsNewDAO().nukeShipmentTable();
            mAppDatabase.drsRvpWithQCDAO().nukeTable();
            mAppDatabase.edsDocumentListMasterActivityDAO().nukeTable();
            mAppDatabase.edsMasterActivityDAO().nukeTable();
            mAppDatabase.drsedsReasonCodeDao().nukeTable();
            mAppDatabase.edsopvMasterActivityDAO().nukeTable();
            mAppDatabase.drsRVPQCMasterDao().nukeTable();
            SathiApplication.hashMapAppUrl.clear();
            mAppDatabase.drsRtsDAO().nukeTable();
            mAppDatabase.DisputedAwbDAO().nukeTable();
            mAppDatabase.drsRvpWithQCDAO().nukeTableQc();
            return true;
        });
    }

    @Override
    public Observable<Boolean> nukeTable() {
        return Observable.fromCallable(() -> {
            mAppDatabase.callBridgePSTNDao().nukeTable();
            return true;
        });
    }

    @Override
    public Observable<Boolean> deleteDRSTables() {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsForwardDAO().nukeTableZeroSyncStatus();
            mAppDatabase.drsEdsNewDao().nukeTableEDSResponseZeroSyncStatus();
            mAppDatabase.drsEdsNewDao().nukeTableEDSActivityWizard();
            mAppDatabase.drsRtsDAO().nukeTableZeroSyncStatus();
            mAppDatabase.drsRvpWithQCDAO().nukeTableZeroSyncStatus();
            return true;
        });
    }

    @Override
    public Observable<Boolean> saveMasterReason(MasterDataConfig masterDataReasonCodeResponse) {
        return Observable.fromCallable(() -> {
            try {
                if (masterDataReasonCodeResponse != null)
                    if (masterDataReasonCodeResponse.getStatus()) {
                        if (masterDataReasonCodeResponse.getResponse().getMaster_data_configurations() != null) {
                            mAppDatabase.callbridgedao().nukeTable();
                            mAppDatabase.callBridgePSTNDao().nukeTable();
                            mAppDatabase.globalConfigDao().nukeTable();
                            mAppDatabase.dashboardDao().nukeTable();
                            if (masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getReasonCodeMaster() != null) {
                                if (masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getReasonCodeMaster().getForwardReasonCodeMasters() != null)
                                    mAppDatabase.drsForwardMasterDAO().insertAll(masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getReasonCodeMaster().getForwardReasonCodeMasters());
                                if (masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getReasonCodeMaster().getRtsReasonCodeMasters() != null)
                                    mAppDatabase.drsRTSMasterDAO().insertAll(masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getReasonCodeMaster().getRtsReasonCodeMasters());
                                if (masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getReasonCodeMaster().getEdsReasonCodeMasters() != null)
                                    mAppDatabase.drsedsReasonCodeDao().insertAll(masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getReasonCodeMaster().getEdsReasonCodeMasters());
                                if (masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getReasonCodeMaster().getReasonCodeMasters() != null)
                                    mAppDatabase.drsRVPMasterDAO().insertAll(masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getReasonCodeMaster().getReasonCodeMasters());
                                if (masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getMasterManagementDataResponse().getMasterSampleQuestion().getSampleQuestions() != null)
                                    mAppDatabase.drsRVPQCMasterDao().insertAll(masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getMasterManagementDataResponse().getMasterSampleQuestion().getSampleQuestions());
                            }
                            if (masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getMasterManagementDataResponse() != null) {
                                if (masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getMasterManagementDataResponse().getActivityDatas() != null) {
                                    mAppDatabase.edsMasterActivityDAO().insertAll(masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getMasterManagementDataResponse().getActivityDatas());
                                }
                                if (masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getMasterManagementDataResponse().getDocumentLists() != null) {
                                    mAppDatabase.edsDocumentListMasterActivityDAO().insertAll(masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getMasterManagementDataResponse().getDocumentLists());
                                }
                            }
                            if (masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getMasterManagementDataResponse().getOpvMaster() != null) {
                                mAppDatabase.edsopvMasterActivityDAO().insertAll(masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getMasterManagementDataResponse().getOpvMaster().generalQuestions);
                            }
                            if (masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCallbridgeConfiguration() != null) {
                                mAppDatabase.callbridgedao().insertall(masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCallbridgeConfiguration());
                                if (masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCallbridgeConfiguration().getCb_pstn_options() != null)
                                    mAppDatabase.callBridgePSTNDao().insertall(masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getCallbridgeConfiguration().getCb_pstn_options());
                            }
                            if (masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getGlobalConfigurationResponse() != null) {
                                mAppDatabase.globalConfigDao().insertall(masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getGlobalConfigurationResponse());
                            }
                            if (masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getBanner_configuration() != null) {
                                if (masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getBanner_configuration().getDashboard_banner() != null) {
                                    mAppDatabase.dashboardDao().insert(masterDataReasonCodeResponse.getResponse().getMaster_data_configurations().getBanner_configuration().getDashboard_banner());
                                }
                            }
                            return true;
                        }
                    }
            } catch (Exception e) {
                e.printStackTrace();
                RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                restApiErrorHandler.writeErrorLogs(0, e.getMessage());
            }
            return false;
        });
    }

    @Override
    public Observable<RvpWithQC> getRvpWithQc(String composite_key) {
        return Observable.fromCallable(() -> mAppDatabase.drsRvpWithQCDAO().getRvpWithQc(composite_key));
    }

    @Override
    public Observable<CallbridgeConfiguration> loadallcallbridge() {
        return Observable.fromCallable(() -> mAppDatabase.callbridgedao().loadAll());
    }

    @Override
    public Observable<EdsWithActivityList> getEdsWithActivityList(String composite_key) {
        return Observable.fromCallable(() -> mAppDatabase.drsEdsNewDao().getEdswithActivityList(composite_key));
    }

    @Override
    public Observable<List<ForwardReasonCodeMaster>> getAllForwardUndeliveredReasonCode(String shipmentType, String isSecure, String isMps) {
        return Observable.fromCallable(() -> {
            if (isSecure.equalsIgnoreCase("true")) {
                return mAppDatabase.drsForwardMasterDAO().getAllForwardSECReasonCodeMasterList(true);
            }
            if (isMps.equalsIgnoreCase("true")) {
                return mAppDatabase.drsForwardMasterDAO().getAllForwardMPSReasonCodeMasterList(true);
            }
            if (shipmentType.equalsIgnoreCase("PPD")) {
                return mAppDatabase.drsForwardMasterDAO().getAllForwardPPDReasonCodeMasterList(true);
            }
            if (shipmentType.equalsIgnoreCase("COD")) {
                return mAppDatabase.drsForwardMasterDAO().getAllForwardCODReasonCodeMasterList(true);
            }
            return mAppDatabase.drsForwardMasterDAO().getAllForwardCODReasonCodeMasterList(true);
        });
    }

    @Override
    public Observable<List<RVPReasonCodeMaster>> getAllRVPUndeliveredReasonCode(String isSecure) {
        return Observable.fromCallable(() -> {
            if (isSecure.equalsIgnoreCase("true")) {
                return mAppDatabase.drsRVPMasterDAO().getAllRVPReasonCodeMasterList(true);
            }
            return mAppDatabase.drsRVPMasterDAO().getAllRVPReasonCodeMasterList(false);
        });
    }

    @Override
    public Observable<List<String>> getSubgroupFromRvpReasonCode() {
        return Observable.fromCallable(() -> mAppDatabase.drsRVPMasterDAO().getSubgroupFromRvpReasonCode());
    }

    @Override
    public Observable<List<RVPReasonCodeMaster>> getSubReasonCodeFromSubGroup(String selectedReason) {
        return Observable.fromCallable(() -> mAppDatabase.drsRVPMasterDAO().getSubReasonCodeFromSubGroup(selectedReason));
    }

    @Override
    public Observable<List<SampleQuestion>> getRvpMasterDescriptions(List<RvpQualityCheck> rvpQualityCheckList) {
        return Observable.fromCallable(() -> {
            ArrayList qcCode = new ArrayList<String>();
            for (RvpQualityCheck rvpQualityCheck : rvpQualityCheckList) {
                qcCode.add(rvpQualityCheck.getQcCode());
            }
            return mAppDatabase.drsRVPQCMasterDao().getRVPQCMasterDescription(qcCode);
        });
    }

    @Override
    public Observable<List<MasterActivityData>> getEDSMasterDescriptions(List<EDSActivityWizard> edsActivityWizards) {
        return Observable.fromCallable(() -> {
            ArrayList qcCode = new ArrayList<String>();
            for (EDSActivityWizard edsActivityWizard : edsActivityWizards) {
                qcCode.add(edsActivityWizard.getCode());
            }
            return mAppDatabase.edsMasterActivityDAO().getEDSQCMasterDescription(qcCode);
        });
    }

    @Override
    public Observable<List<ImageModel>> getImages(String awbNo) {
        return Observable.fromCallable(() -> mAppDatabase.imageDAO().getImages(awbNo));
    }

    @Override
    public Observable<Boolean> saveImage(ImageModel imageModel) {
        return Observable.fromCallable(() -> {
            mAppDatabase.imageDAO().insert(imageModel);
            return true;
        });
    }

    @Override
    public Observable<Boolean> insertSathiLog(LiveTrackingLogTable liveTrackingLogTable) {
        return Observable.fromCallable(() -> {
            mAppDatabase.sathiLogDAO().insert(liveTrackingLogTable);
            return true;
        });
    }

    @Override
    public Observable<Boolean> deleteLogs() {
        return Observable.fromCallable(() -> {
            mAppDatabase.sathiLogDAO().nukeTable();
            return true;
        });
    }

    @Override
    public Observable<List<LiveTrackingLogTable>> getDataFromLiveTrackingLog() {
        return Observable.fromCallable(() -> mAppDatabase.sathiLogDAO().getLiveTrackingLogTable());
    }

    @Override
    public Observable<Integer> getCountFromLiveTrackingLog() {
        return Observable.fromCallable(() -> mAppDatabase.sathiLogDAO().getCount());
    }

    @Override
    public Observable<Integer> deleteFistRowTable() {
        return Observable.fromCallable(() -> mAppDatabase.sathiLogDAO().deleteFistRowTable());
    }

    @Override
    public Observable<Boolean> updateImageStatus(String imageName, int status) {
        return Observable.fromCallable(() -> {
            mAppDatabase.imageDAO().updateImageStatus(imageName, status);
            return true;
        });
    }

    @Override
    public Observable<Boolean> updateImageID(String imageName, int imageId) {
        return Observable.fromCallable(() -> {
            mAppDatabase.imageDAO().updateImageID(imageName, imageId);
            return true;
        });
    }

    @Override
    public Observable<Long> getisForwardCallattempted(long awb) {
        return Observable.fromCallable(() -> mAppDatabase.drsForwardDAO().getisCallattempted(awb));
    }

    @Override
    public Observable<Long> getisRVPCallattempted(long awb) {
        return Observable.fromCallable(() -> mAppDatabase.drsRvpWithQCDAO().getisCallattempted(awb));
    }

    @Override
    public Observable<Long> getisEDSCallattempted(Long awb) {
        return Observable.fromCallable(() -> mAppDatabase.drsEdsNewDao().getisCallattempted(awb));
    }

    @Override
    public Observable<Boolean> reassignAll(long vwID) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsRtsNewDAO().reassignAll(vwID, Constants.ASSIGNED, true);
            return true;
        });
    }

    @Override
    public Observable<Boolean> markUndelivered(ShipmentsDetail... shipmentDetails) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsRtsNewDAO().insertAllShipment(shipmentDetails);
            return true;
        });
    }

    @Override
    public Observable<List<RTSReasonCodeMaster>> getRTSReasonCodes() {
        return Observable.fromCallable(() -> mAppDatabase.drsRTSMasterDAO().getAllReasonCode());
    }

    @Override
    public Observable<Boolean> saveCommitPacket(PushApi pushApi) {
        return Observable.fromCallable(() -> {
            mAppDatabase.pushApiDAO().insert(pushApi);
            return true;
        });
    }

    @Override
    public Observable<List<PushApi>> getPushApiUnSyncList() {
        return Observable.fromCallable(() -> mAppDatabase.pushApiDAO().UnSyncList(100, 0));
    }

    @Override
    public Observable<List<PushApi>> getSizeOFPushApi() {
        return Observable.fromCallable(() -> mAppDatabase.pushApiDAO().getSizeofPushApi());
    }

    @Override
    public Observable<List<PushApi>> getAllPushApiData() {
        return Observable.fromCallable(() -> mAppDatabase.pushApiDAO().getAllPushApiData());
    }

    @Override
    public Observable<List<PushApi>> getPushApiAllPendingUnSyncList() {
        return Observable.fromCallable(() -> mAppDatabase.pushApiDAO().UnAllPendingSyncList(100, 0));
    }

    @Override
    public Observable<Boolean> getImageStatus(long awb) {
        return Observable.fromCallable(() -> {
            int unsync = 0;
            List<ImageModel> imageModel = mAppDatabase.imageDAO().getImageStatus(String.valueOf(awb));
            for (ImageModel imageModel1 : imageModel) {
                if (imageModel1.getStatus() != 2) {
                    unsync = 1;
                    break;
                }
            }
            return unsync != 1;
        });
    }

    @Override
    public Observable<List<ImageModel>> getShipmentImageStatus(long awb) {
        return Observable.fromCallable(() -> mAppDatabase.imageDAO().getShipmentImageStatus(String.valueOf(awb)));
    }

    @Override
    public Observable<List<ImageModel>> getUnSyncImagesV2() {
        return Observable.fromCallable(() -> mAppDatabase.imageDAO().getUnSyncImageV2());
    }

    @Override
    public Observable<MasterDocumentList> doDocumentListMasterCall(String s, String abc) {
        return Observable.fromCallable(() -> mAppDatabase.edsDocumentListMasterActivityDAO().getEDSDocumentListMasterDescription(s, abc));
    }

    @Override
    public Observable<List<GeneralQuestion>> doOpvMasterCall() {
        return Observable.fromCallable(() -> mAppDatabase.edsopvMasterActivityDAO().loadAllEDSOPVMasterCode());
    }

    @Override
    public Observable<List<EDSReasonCodeMaster>> doEDSReasonMasterCode(String filter) {
        return Observable.fromCallable(() -> {
            if (filter.equalsIgnoreCase("activity"))
                return mAppDatabase.drsedsReasonCodeDao().loadAllEDSActivityReasonCode(true);
            else if (filter.equalsIgnoreCase("act_list")) {
                return mAppDatabase.drsedsReasonCodeDao().loadAllEDSActListReasonCode(true);
            } else if (filter.equalsIgnoreCase("otp"))
                return mAppDatabase.drsedsReasonCodeDao().loadAllEDSOtpReasonCode(true);
            return mAppDatabase.drsedsReasonCodeDao().loadAllEDSReasonCode();
        });
    }

    @Override
    public Observable<List<EDSReasonCodeMaster>> doEDSReasonMasterCodeALL(HashSet<String> fliter) {
        return Observable.fromCallable(() -> {
            List<EDSReasonCodeMaster> edsReasonCodeMastersOTP = new ArrayList<>();
            List<EDSReasonCodeMaster> edsReasonCodeMastersSecured = new ArrayList<>();
            List<EDSReasonCodeMaster> edsReasonCodeMastersCC = new ArrayList<>();
            List<EDSReasonCodeMaster> edsReasonCodeMastersCPV = new ArrayList<>();
            List<EDSReasonCodeMaster> edsReasonCodeMastersEKYC = new ArrayList<>();
            List<EDSReasonCodeMaster> edsReasonCodeMastersUDAAN = new ArrayList<>();
            List<EDSReasonCodeMaster> edsReasonCodeMastersImage = new ArrayList<>();
            List<EDSReasonCodeMaster> edsReasonCodeMastersPaytm = new ArrayList<>();
            List<EDSReasonCodeMaster> edsReasonCodeMastersDC = new ArrayList<>();
            List<EDSReasonCodeMaster> edsReasonCodeMastersDV = new ArrayList<>();
            for (String edsattr : fliter) {
                if (edsattr.equalsIgnoreCase("EDS_OTP")) {
                    edsReasonCodeMastersOTP = mAppDatabase.drsedsReasonCodeDao().loadAllEDSOtpReasonCodeEDS_OTP(true);
                } else if (edsattr.equalsIgnoreCase("SECURED")) {
                    edsReasonCodeMastersSecured = mAppDatabase.drsedsReasonCodeDao().loadAllEDSOtpReasonCodeSecured(true);
                } else if (edsattr.equalsIgnoreCase("EDS_CC")) {
                    edsReasonCodeMastersCC = mAppDatabase.drsedsReasonCodeDao().loadAllEDSOtpReasonCodeEDSCC(true);
                } else if (edsattr.equalsIgnoreCase("EDS_CPV")) {
                    edsReasonCodeMastersCPV = mAppDatabase.drsedsReasonCodeDao().loadAllEDSOtpReasonCodeEDSCP(true);
                } else if (edsattr.equalsIgnoreCase("EDS_EKYC")) {
                    edsReasonCodeMastersEKYC = mAppDatabase.drsedsReasonCodeDao().loadAllEDSOtpReasonCodeEDS_EKYC(true);
                } else if (edsattr.equalsIgnoreCase("EDS_UDAAN")) {
                    edsReasonCodeMastersUDAAN = mAppDatabase.drsedsReasonCodeDao().loadAllEDSOtpReasonCodeEDS_UDAAN(true);
                } else if (edsattr.equalsIgnoreCase("EDS_IMAGE")) {
                    edsReasonCodeMastersImage = mAppDatabase.drsedsReasonCodeDao().loadAllEDSOtpReasonCodeEDS_IMAGE(true);
                } else if (edsattr.equalsIgnoreCase("EDS_PAYTM_IMAGE")) {
                    edsReasonCodeMastersPaytm = mAppDatabase.drsedsReasonCodeDao().loadAllEDSOtpReasonCodeEDS_PAYTM_IMAGE(true);
                } else if (edsattr.equalsIgnoreCase("EDS_DC")) {
                    edsReasonCodeMastersDC = mAppDatabase.drsedsReasonCodeDao().loadAllEDSOtpReasonCodeEDS_DC(true);
                } else if (edsattr.equalsIgnoreCase("EDS_DV")) {
                    edsReasonCodeMastersDV = mAppDatabase.drsedsReasonCodeDao().loadAllEDSOtpReasonCodeEDS_DV(true);
                } else if (edsattr.equalsIgnoreCase("EDS_EKYC_FAIL")) {
                    edsReasonCodeMastersDV = mAppDatabase.drsedsReasonCodeDao().loadAllEDSOtpReasonCodeEDS_EKYC_FAIL(true);
                }
            }
            List<EDSReasonCodeMaster> mainHashSet = new ArrayList<>();
            mainHashSet.addAll(edsReasonCodeMastersOTP);
            mainHashSet.addAll(edsReasonCodeMastersSecured);
            mainHashSet.addAll(edsReasonCodeMastersCC);
            mainHashSet.addAll(edsReasonCodeMastersCPV);
            mainHashSet.addAll(edsReasonCodeMastersEKYC);
            mainHashSet.addAll(edsReasonCodeMastersUDAAN);
            mainHashSet.addAll(edsReasonCodeMastersImage);
            mainHashSet.addAll(edsReasonCodeMastersPaytm);
            mainHashSet.addAll(edsReasonCodeMastersDC);
            mainHashSet.addAll(edsReasonCodeMastersDV);
            return mainHashSet;
        });
    }

    @Override
    public Observable<List<ImageModel>> getUnsynced(long awbNo) {
        return Observable.fromCallable(() -> mAppDatabase.imageDAO().getImages(String.valueOf(awbNo)));
    }

    @Override
    public Observable<Boolean> deleteSyncedImage(String composite_key) {
        return Observable.fromCallable(() -> {
            try {
                mAppDatabase.imageDAO().deleteImage(composite_key);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    @Override
    public Observable<Boolean> deleteSyncedFWD(long awbNo) {
        return Observable.fromCallable(() -> {
            try {
                mAppDatabase.pushApiDAO().deleteAwb(awbNo);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    @Override
    public Observable<Boolean> insertRemark(Remark remark) {
        return Observable.fromCallable(() -> {
            mAppDatabase.daoRemarks().insert(remark);
            return true;
        });
    }

    @Override
    public Observable<Long> getRemarksoneCount(String remark) {
        return Observable.fromCallable(() -> mAppDatabase.daoRemarks().getRemarksoneCount(remark));
    }

    @Override
    public Observable<Long> getRemarkstwoCount(String remark) {
        return Observable.fromCallable(() -> mAppDatabase.daoRemarks().getRemarkstwoCount(remark));
    }

    @Override
    public Observable<Long> getRemarksthreeCount(String remark) {
        return Observable.fromCallable(() -> mAppDatabase.daoRemarks().getRemarksthreeCount('%' + remark + '%'));
    }

    @Override
    public Observable<Long> getRemarksfourCount(String remark) {
        return Observable.fromCallable(() -> mAppDatabase.daoRemarks().getRemarksfourCount(remark));
    }

    @Override
    public Observable<Long> getNoRemarksCount() {
        return Observable.fromCallable(() -> mAppDatabase.daoRemarks().getNoRemarksCount(""));
    }

    @Override
    public Observable<Remark> getRemarks(long awb) {
        return Observable.fromCallable(() -> mAppDatabase.daoRemarks().getRemarks(awb));
    }

    @Override
    public Observable<Boolean> deleteOldRemarks(long currentDate) {
        return Observable.fromCallable(() -> {
            mAppDatabase.daoRemarks().deleteOldRemarks(currentDate);
            return true;
        });
    }

    @Override
    public Observable<Boolean> deleteBasisAWB(long awb) {
        return Observable.fromCallable(() -> {
            mAppDatabase.daoRemarks().deleteBasisAWB(awb);
            return true;
        });
    }

    @Override
    public Observable<Boolean> deleteSyncedRemarks(int status) {
        return Observable.fromCallable(() -> {
            mAppDatabase.daoRemarks().deleteSyncedRemarks(status);
            return true;
        });
    }

    @Override
    public Observable<List<Remark>> getAllRemarks(String code, long currentDate) {
        return Observable.fromCallable(() -> mAppDatabase.daoRemarks().getAllRemarks(code, currentDate));
    }

    @Override
    public Observable<List<ProfileFound>> getProfileFound() {
        return Observable.fromCallable(() -> mAppDatabase.profileFoundDAO().getAllProfile());
    }

    @Override
    public Observable<EDSResponse> getEDS(String compositeKey) {
        return Observable.fromCallable(() -> mAppDatabase.drsEdsNewDao().getEDS(compositeKey));
    }

    @Override
    public Observable<Boolean> saveRTSConsigneeDetails(Details details) {
        return Observable.fromCallable(() -> {
            try {
                mAppDatabase.drsRtsNewDAO().insertDetails(details);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        });
    }

    @Override
    public Observable<Boolean> updateEDSpinCallAttempted(Long pin, int isCallAttempted) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsEdsNewDao().updateEDSpinCallAttempted(pin, isCallAttempted);
            return true;
        });
    }

    @Override
    public Observable<Boolean> updateRVPpinCallAttempted(Long pin, int isCallAttempted) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsRvpWithQCDAO().updateRVPpinCallAttempted(pin, isCallAttempted);
            return true;
        });
    }

    @Override
    public Observable<Boolean> updateForwardpinCallAttempted(Long pin, int isCallAttempted) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsForwardDAO().updateForwardpinCallAttempted(pin, isCallAttempted);
            return true;
        });
    }

    @Override
    public Observable<Boolean> updateSyncStatusFWD(String composite_key, int syncStatus) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsForwardDAO().updateSyncStatus(composite_key, syncStatus);
            return true;
        });
    }

    @Override
    public Observable<Boolean> updateSameDayReassignSyncStatusFWD(String composite_key, boolean syncStatus) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsForwardDAO().updateSameDayReassignSyncStatus(composite_key, syncStatus);
            return true;
        });
    }

    @Override
    public Observable<Boolean> updateSyncStatusEDS(String composite_key, int syncStatus) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsEdsNewDao().updateSyncStatus(composite_key, syncStatus);
            return true;
        });
    }

    @Override
    public Observable<Boolean> updateSyncStatusRVP(String composite_key, int syncStatus) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsRvpWithQCDAO().updateSyncStatus(composite_key, syncStatus);
            return true;
        });
    }

    @Override
    public Observable<Boolean> updateSyncStatusRTS(long ID, int syncStatus) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsRtsNewDAO().updateSyncStatus(ID, syncStatus);
            return true;
        });
    }

    @Override
    public Observable<Boolean> updateForwardMPSShipmentStatus(String[] awbArr, int shipmentUndeliveredStatus) {
        return Observable.fromCallable(() -> {
            for (String awbNo : awbArr) {
                mAppDatabase.drsForwardDAO().updateForwardMPSStatus(Long.parseLong(awbNo.trim()), shipmentUndeliveredStatus);
            }
            return true;
        });
    }

    @Override
    public Observable<Boolean> insertAllApiUrls(List<ApiUrlData> apiUrlList) {
        return Observable.fromCallable(() -> {
            try {
                mAppDatabase.appApiUrlDAO().nukeTable();
                mAppDatabase.appApiUrlDAO().insert(apiUrlList);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    @Override
    public Observable<List<ApiUrlData>> getAllApiUrl() {
        return Observable.fromCallable(() -> mAppDatabase.appApiUrlDAO().getAllApiUrl());
    }

    @Override
    public Observable<Boolean> insertFwdShipperId(Forward forward) {
        return Observable.fromCallable(() -> {
            try {
                mAppDatabase.forwardShipperIdDAO().nukeTable();
                mAppDatabase.forwardShipperIdDAO().insert(forward);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    @Override
    public Observable<Forward> getFwdShipperId() {
        return Observable.fromCallable(() -> mAppDatabase.forwardShipperIdDAO().getAllForward());
    }

    @Override
    public Observable<Boolean> insertSmsLink(MsgLinkData msgLinkData) {
        return Observable.fromCallable(() -> {
            try {
                mAppDatabase.msgLinkDAO().insert(msgLinkData);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    @Override
    public Observable<Boolean> updateMobileList(ArrayList<String> mobile_number, String awbNo) {
        return Observable.fromCallable(() -> {
            try {
                mAppDatabase.msgLinkDAO().updateMobileList(mobile_number, awbNo);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    @Override
    public Observable<MsgLinkData> getSmsLink(String awb) {
        return Observable.fromCallable(() -> mAppDatabase.msgLinkDAO().getMsgLinkData(awb));
    }

    @Override
    public boolean getImageStatusSingleThread(long awb) {
        int unsync = 0;
        List<ImageModel> imageModel = mAppDatabase.imageDAO().getImageStatus(String.valueOf(awb));
        for (ImageModel imageModel1 : imageModel) {
            if (imageModel1.getStatus() != 2) {
                unsync = 1;
                break;
            }
        }
        return unsync != 1;
    }

    @Override
    public boolean saveImageSingleThread(ImageModel imageModel) {
        mAppDatabase.imageDAO().insert(imageModel);
        return true;
    }

    @Override
    public Observable<PushApi> getUnSyncPushAPIPacket(long awbNo) {
        return Observable.fromCallable(() -> mAppDatabase.pushApiDAO().UnSyncPushApiPacket(awbNo));
    }

    @Override
    public Observable<ProfileFound> getProfileLat(long awbno) {
        return Observable.fromCallable(() -> mAppDatabase.profileFoundDAO().getProfileLatLng(awbno));
    }

    @Override
    public Observable<ShipmentsDetail> getShipmentData(long scannedAwbNo) {
        return Observable.fromCallable(() -> mAppDatabase.drsRtsNewDAO().getShipment(scannedAwbNo));
    }

    @Override
    public Observable<List<RescheduleEdsD>> getEdsRescheduleFlag() {
        return Observable.fromCallable(() -> mAppDatabase.rescheduleEdsDao().getEdsRescheduleData());
    }

    @Override
    public Observable<Boolean> insertEdsRescheduleFlag(ArrayList<RescheduleEdsD> edsRescheduleResponse) {
        return Observable.fromCallable(() -> {
            try {
                mAppDatabase.rescheduleEdsDao().insert(edsRescheduleResponse);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    @Override
    public Observable<Boolean> deleteEdsRescheduleFlag() {
        return Observable.fromCallable(() -> {
            try {
                mAppDatabase.rescheduleEdsDao().nukeTable();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    @Override
    public Observable<List<PushApi>> insetedOrNotinTable(long awbno) {
        return Observable.fromCallable(() -> mAppDatabase.pushApiDAO().insertedOrNot(awbno));
    }

    @Override
    public Observable<PushApi> getPushApiDetail(long awbno) {
        return Observable.fromCallable(() -> mAppDatabase.pushApiDAO().getPushApiDetail(awbno));
    }

    @Override
    public Observable<String> getdisputed(String awb) {
        return Observable.fromCallable(() -> {
            try {
                if (mAppDatabase.DisputedAwbDAO().getdisputedawb(awb) == null) {
                    return "";
                } else {
                    return mAppDatabase.DisputedAwbDAO().getdisputedawb(awb);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "false";
            }
        });
    }

    @Override
    public Observable<Boolean> saveDisputedAwb(PaymentDisputedAwb disputedAwb) {
        return Observable.fromCallable(() -> {
            mAppDatabase.DisputedAwbDAO().insert(disputedAwb);
            return true;
        });
    }

    @Override
    public Observable<List<ForwardReasonCodeMaster>> getAllForwardReasonCodeMasterList() {
        return Observable.fromCallable(() -> mAppDatabase.drsForwardMasterDAO().getAllForwardReasonCodeMasterList());
    }

    @Override
    public Observable<Boolean> saveCallStatus(Call call) {
        return Observable.fromCallable(() -> {
            mAppDatabase.daoCall().insert(call);
            return true;
        });
    }

    @Override
    public Observable<Boolean> getCallStatus(long awb, int drs) {
        return Observable.fromCallable(() -> mAppDatabase.daoCall().getCallStatus(awb, drs));
    }

    @Override
    public Observable<Boolean> insertRvpShipperId(Reverse reverse) {
        return Observable.fromCallable(() -> {
            try {
                mAppDatabase.rvpShipperIdDAO().nukeTable();
                mAppDatabase.rvpShipperIdDAO().insert(reverse);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    @Override
    public Observable<Reverse> getRvpShipperId() {
        return Observable.fromCallable(() -> mAppDatabase.rvpShipperIdDAO().getAllRVP());
    }

    @Override
    public Observable<Long> insert(RVPQCImageTable rvpqcImageTable) {
        return Observable.fromCallable(() -> mAppDatabase.rvpqcImageTableDao().insert(rvpqcImageTable));
    }

    @Override
    public Observable<List<RVPQCImageTable>> getImageForAwb(String awb) {
        return Observable.fromCallable(() -> mAppDatabase.rvpqcImageTableDao().getImageForAwb(awb));
    }

    @Override
    public Observable<Integer> deleteRVPQCImageTable() {
        return Observable.fromCallable(() -> mAppDatabase.rvpqcImageTableDao().deleteRVPQCImageTable());
    }

    @Override
    public Observable<Boolean> deleteShipmentData(int detailId) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsRtsNewDAO().deleteShipmentData(detailId);
            return true;
        });
    }

    @Override
    public Observable<Boolean> updateShipmentDetailData(int shipmentStatus, String status, int detailID) {
        return Observable.fromCallable(() -> {
            mAppDatabase.drsRtsNewDAO().updateShipmentDetailData(shipmentStatus, status, detailID);
            return true;
        });
    }
}