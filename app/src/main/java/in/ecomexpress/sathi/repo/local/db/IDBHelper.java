package in.ecomexpress.sathi.repo.local.db;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import in.ecomexpress.sathi.repo.local.data.commit.PushApi;
import in.ecomexpress.sathi.repo.local.db.model.ApiUrlData;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.local.db.model.ImageModel;
import in.ecomexpress.sathi.repo.local.db.model.LiveTrackingLogTable;
import in.ecomexpress.sathi.repo.local.db.model.MsgLinkData;
import in.ecomexpress.sathi.repo.local.db.model.RVPMPSWithQC;
import in.ecomexpress.sathi.repo.local.db.model.RVPQCImageTable;
import in.ecomexpress.sathi.repo.local.db.model.Remark;
import in.ecomexpress.sathi.repo.local.db.model.RescheduleEdsD;
import in.ecomexpress.sathi.repo.local.db.model.RvpWithQC;
import in.ecomexpress.sathi.repo.remote.model.call.Call;
import in.ecomexpress.sathi.repo.remote.model.consignee_profile.ProfileFound;
import in.ecomexpress.sathi.repo.remote.model.drs_list.DRSSequence;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.DRSReturnToShipperTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.DRSReturnToShipperTypeNewResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.Details;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.ShipmentsDetail;
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
import in.ecomexpress.sathi.repo.remote.model.mps.DRSRvpQcMpsResponse;
import in.ecomexpress.sathi.repo.remote.model.mps.QcItem;
import in.ecomexpress.sathi.repo.remote.model.payphi.raise_dispute.PaymentDisputedAwb;
import io.reactivex.Observable;

public interface IDBHelper {

    Observable<Boolean> saveDRSForwardList(List<DRSForwardTypeResponse> drsForwardTypeResponse);

    Observable<Boolean> saveProfileFoundList(List<ProfileFound> profileFoundList);

    Observable<Boolean> saveToDRSSequenceTable(List<DRSSequence> drsSequenceList);

    Observable<Boolean> updateDRSSequenceTable(List<DRSSequence> drsSequenceList);

    Observable<List<DRSSequence>> getAllDRSSequenceData();

    Observable<Boolean> deleteAllDataDRSSequence();

    Observable<Boolean> saveDRSRTSList(List<DRSReturnToShipperTypeResponse> drsReturnToShipperTypeResponses);

    Observable<Boolean> saveDRSNewRTSList(DRSReturnToShipperTypeNewResponse drsReturnToShipperTypeResponses);

    Observable<Boolean> saveDRSRVP(DRSReverseQCTypeResponse drsReverseQCTypeResponses);

    Observable<Boolean> saveDRSRVPMPS(DRSRvpQcMpsResponse drsReverseQCTypeResponses);

    Observable<Boolean> saveDRSRVPMPSListQualityCheck(List<QcItem> rvpQualityChecksQcItems);

    Observable<Boolean> saveNewDrsEDS(EDSResponse edsResponse);

    Observable<Boolean> saveCallbridgeConfiguration(CallbridgeConfiguration callbridgeConfiguration);

    Observable<Boolean> saveDashboardBanner(List<DashboardBanner> dashboardBanner);

    Observable<List<DashboardBanner>> getAllDashboardBanner();

    Observable<Boolean> updatePushSyncStatusToZero(List<Long> awbNo);

    Observable<Boolean> updatePushSyncStatusToZeroOnClick(long awbNo);

    Observable<Boolean> updatePushShipmentStatus(long awbNo, int shipment_status);

    Observable<Boolean> saveDRSRVPList(List<DRSReverseQCTypeResponse> drsReverseQCTypeResponses);

    Observable<Boolean> getRescheduleFlag(String awbNo);

    Observable<Boolean> saveDRSRVPListQualityCheck(List<RvpQualityCheck> rvpQualityChecks);

    Observable<List<DRSForwardTypeResponse>> getAllForwardList();

    Observable<List<RvpQualityCheck>> getQcValuesForAwb();

    Observable<List<QcItem>> getMPSQcValues();

    Observable<List<DRSForwardTypeResponse>> getUnSyncForwardList();

    Observable<List<QcItem>> getQCDetailsForPickupActivity(long awbNo, int drs);

    Observable<List<EDSResponse>> getUnSyncEdsList();

    Observable<List<DRSReverseQCTypeResponse>> getUnSyncRVPList();

    Observable<List<CbPstnOptions>> getCbPstnOptions();

    Observable<List<GlobalConfigurationMaster>> getglobalConfigurationMasters();

    Observable<Boolean> saveCallbridgecb_pstn_options(List<CbPstnOptions> cb_pstn_options);

    Observable<List<DRSForwardTypeResponse>> getDRSListForward();

    Observable<List<DRSReturnToShipperTypeResponse>> getDRSListRTS();

    Observable<DRSReturnToShipperTypeNewResponse> getDRSListNewRTS();

    Observable<Details> getVWDetails(long id);

    Observable<List<ShipmentsDetail>> getVWShipmentList(long vwID);

    Observable<List<DRSReverseQCTypeResponse>> getDRSListRVP();

    Observable<List<DRSRvpQcMpsResponse>> getDRSListRVPMPS();

    Observable<List<EDSResponse>> getDrsListNewEds();

    Observable<DRSForwardTypeResponse> getForwardDRS(String composite_key);

    Observable<DRSForwardTypeResponse> getForwardDRSCompositeKey(Long awb);

    Observable<DRSForwardTypeResponse> fetchObdQualityCheckData(Long awb);

    Observable<Integer> getDuplicateValueCount(String detailId);

    Observable<DRSReturnToShipperTypeResponse> getRTSDRS(long awbNo);

    Observable<DRSReverseQCTypeResponse> getRVPDRS(String composite_key);

    Observable<DRSRvpQcMpsResponse> loadMpsShipmentDetailsFromDB(String composite_key);

    Observable<Boolean> isRVPDRSExist(String composite_key);

    Observable<Long> insertOrUpdateForward(DRSForwardTypeResponse drsForwardTypeResponse);

    Observable<Long> getFWDStatusCount(int status);

    Observable<Long> getRTSStatusCount(int status);

    Observable<Long> getRVPStatusCount(int status);

    Observable<Long> getRVPMPSStatusCount(int status);

    Observable<String> getTypeOfShipment(String awb);

    Observable<String> getPhonePeShipmentType(String awb);

    Observable<Long> getEDSStatusCount(int status);

    Observable<Double> getCodCount();

    Observable<Double> getEcodStatusCount();

    Observable<Boolean> isEDSDRSNewExist(String awbNo);

    Observable<Boolean> saveEDSActivityNewWizardList(List<EDSActivityWizard> edsActivityWizards);

    Observable<Boolean> updateForwardStatus(String composite_key, int status);

    Observable<Boolean> insertcodCollected(Long awbNo, float amount);

    Observable<Boolean> insertEcodCollected(Long awbNo, float amount);

    Observable<Boolean> updateForwardCallAttempted(Long awbNo, int isCallAttempted);

    Observable<Boolean> updateRVPCallAttempted(Long awbNo, int isCallAttempted);

    Observable<RTSReasonCodeMaster> isAttributeAvailable(String reasoncode);

    Observable<Boolean> updateRTSCallAttempted(Long id, int isCallAttempted);

    Observable<Boolean> updateEDSCallAttempted(Long awbNo, int isCallAttempted);

    Observable<Boolean> updateRvpStatus(String composite_key, int status);

    Observable<Boolean> updateRvpMpsStatus(String composite_key, int status);

    Observable<Boolean> updateRtsStatus(Long id, int status);

    Observable<Boolean> updateEdsStatus(String awbNo, int status);

    Observable<Boolean> updateOTPForward(long awbNo, String otp);

    Observable<Boolean> updateAssignDataForward(long awbNo, String assign_date);

    Observable<Boolean> updateTotalAttemptsForward(long awbNo, int attempts);

    Observable<Boolean> updateRVPMpsCallAttempted(Long awbNo, int isCallAttempted);

    Observable<Boolean> deleteShipment(String compositkey);

    Observable<Boolean> updateForwardShipment(String awb_no, int syncStatus, int status, int drs_id, String composite_key_new);

    Observable<Boolean> updateOTPEDS(long awbNo, String otp);

    Observable<List<ImageModel>> getUDImage(String imagetype);

    Observable<Boolean> updateRTSShipmentDetail(ShipmentsDetail shipmentsDetail);

    Observable<Boolean> updateRtsImageCapturedStatus(Long awb_no, int is_image_captured);

    Observable<Boolean> deleteAllTables();

    Observable<Boolean> deleteAllTablesOnStopTrip();

    Observable<Boolean> nukeTable();

    Observable<Boolean> deleteDRSTables();

    Observable<Boolean> saveMasterReason(MasterDataConfig masterDataReasonCodeResponse);

    Observable<RvpWithQC> getRvpWithQc(String composite_key);

    Observable<RVPMPSWithQC> getRvpMpsWithQc(String composite_key);

    Observable<CallbridgeConfiguration> loadallcallbridge();

    Observable<EdsWithActivityList> getEdsWithActivityList(String composite_key);

    //Undelivered Reason Code List
    Observable<List<ForwardReasonCodeMaster>> getAllForwardUndeliveredReasonCode(String shipmentType, String isSecure, String isMPS);

    Observable<List<RVPReasonCodeMaster>> getAllRVPUndeliveredReasonCode(String isSecure);

    Observable<List<String>> getSubgroupFromRvpReasonCode();


    Observable<List<RVPReasonCodeMaster>> getSubReasonCodeFromSubGroup(String selectedReason);

    Observable<List<SampleQuestion>> getRvpMasterDescriptions(List<RvpQualityCheck> rvpQualityCheckList);

    Observable<List<SampleQuestion>> getRvpMpsMasterDescriptions(List<QcItem> rvpQualityCheckList);

    Observable<List<MasterActivityData>> getEDSMasterDescriptions(List<EDSActivityWizard> edsActivityWizards);

    Observable<List<ImageModel>> getImages(String awbNo);

    Observable<Boolean> saveImage(ImageModel imageModel);

    Observable<Boolean> insertSathiLog(LiveTrackingLogTable liveTrackingLogTable);

    Observable<Boolean> deleteLogs();

    Observable<List<LiveTrackingLogTable>> getDataFromLiveTrackingLog();

    Observable<Integer> getCountFromLiveTrackingLog();

    Observable<Integer> deleteFistRowTable();

    Observable<Boolean> updateImageStatus(String imageName, int status);

    Observable<Boolean> updateImageID(String imageName, int imageId);

    Observable<Long> getisForwardCallattempted(long awb);

    Observable<Long> getisRVPCallattempted(long awb);

    Observable<Long> getIsMpsCallAttempted(long awb);

    Observable<Long> getisEDSCallattempted(Long awb);

    Observable<Boolean> reassignAll(long vwID);

    Observable<Boolean> markUndelivered(ShipmentsDetail... shipmentDetails);

    Observable<List<RTSReasonCodeMaster>> getRTSReasonCodes();

    Observable<Boolean> saveCommitPacket(PushApi pushApi);

    Observable<List<PushApi>> getPushApiUnSyncList();

    Observable<List<PushApi>> getSizeOFPushApi();

    Observable<List<PushApi>> getAllPushApiData();

    Observable<List<PushApi>> getPushApiAllPendingUnSyncList();

    Observable<Boolean> getImageStatus(long awb);

    Observable<List<ImageModel>> getShipmentImageStatus(long awb);

    Observable<List<ImageModel>> getUnSyncImagesV2();

    Observable<MasterDocumentList> doDocumentListMasterCall(String s, String abc);

    Observable<List<GeneralQuestion>> doOpvMasterCall();

    Observable<List<EDSReasonCodeMaster>> doEDSReasonMasterCode(String fliter);

    Observable<List<EDSReasonCodeMaster>> doEDSReasonMasterCodeALL(HashSet<String> fliter);

    Observable<List<ImageModel>> getUnsynced(long awbNo);

    Observable<Boolean> deleteSyncedImage(String Composite_key);

    Observable<Boolean> deleteSyncedFWD(long awbNo);

    Observable<Boolean> insertRemark(Remark remark);

    Observable<Long> getRemarksoneCount(String remark);

    Observable<Long> getRemarkstwoCount(String remark);

    Observable<Long> getRemarksthreeCount(String remark);

    Observable<Long> getRemarksfourCount(String remark);

    Observable<Long> getNoRemarksCount();

    Observable<Remark> getRemarks(long awb);

    Observable<Boolean> deleteOldRemarks(long currentDate);

    Observable<Boolean> deleteBasisAWB(long awb);

    Observable<Boolean> deleteSyncedRemarks(int status);

    Observable<List<Remark>> getAllRemarks(String empCode, long currentDate);

    Observable<List<ProfileFound>> getProfileFound();

    Observable<EDSResponse> getEDS(String compositeKey);

    Observable<Boolean> saveRTSConsigneeDetails(Details details);

    Observable<Boolean> updateEDSpinCallAttempted(Long pin, int isCallAttempted);

    Observable<Boolean> updateRVPpinCallAttempted(Long pin, int isCallAttempted);

    Observable<Boolean> updateForwardpinCallAttempted(Long pin, int isCallAttempted);

    Observable<Boolean> updateSyncStatusFWD(String composite_key, int syncStatus);

    Observable<Boolean> updateSameDayReassignSyncStatusFWD(String composite_key, boolean syncStatus);

    Observable<Boolean> updateSyncStatusEDS(String composite_key, int syncStatus);

    Observable<Boolean> updateSyncStatusRVP(String composite_key, int syncStatus);

    Observable<Boolean> updateSyncStatusMps(String composite_key, int syncStatus);

    Observable<Boolean> updateSyncStatusRTS(long vendorID, int syncStatus);

    Observable<Boolean> updateForwardMPSShipmentStatus(String[] awbArr, int shipmentUndeliveredStatus);

    Observable<Boolean> insertAllApiUrls(List<ApiUrlData> apiUrlKey);

    Observable<List<ApiUrlData>> getAllApiUrl();

    Observable<Boolean> insertFwdShipperId(Forward forward);

    Observable<Forward> getFwdShipperId();

    Observable<Boolean> insertSmsLink(MsgLinkData msgLinkData);

    Observable<Boolean> updateMobileList(ArrayList<String> mobile_number, String awbNo);

    Observable<MsgLinkData> getSmsLink(String awb);

    boolean getImageStatusSingleThread(long awbNo);

    boolean saveImageSingleThread(ImageModel imageModel);

    Observable<PushApi> getUnSyncPushAPIPacket(long awbNo);

    Observable<ProfileFound> getProfileLat(long awbno);

    Observable<ShipmentsDetail> getShipmentData(long scannedAwbNo);

    Observable<List<RescheduleEdsD>> getEdsRescheduleFlag();

    Observable<Boolean> insertEdsRescheduleFlag(ArrayList<RescheduleEdsD> edsRescheduleResponse);

    Observable<Boolean> deleteEdsRescheduleFlag();

    Observable<List<PushApi>> insetedOrNotinTable(long awbno);

    Observable<PushApi> getPushApiDetail(long awbno);

    Observable<String> getdisputed(String awb);

    Observable<Boolean> saveDisputedAwb(PaymentDisputedAwb awb);

    Observable<List<ForwardReasonCodeMaster>> getAllForwardReasonCodeMasterList();

    Observable<Boolean> saveCallStatus(Call call);

    Observable<Boolean> deleteQCData(int drs, long awbNo);

    Observable<Boolean> deleteMpsQcDataFromQcItemTable(int drs, long awbNo);

    Observable<Boolean> getCallStatus(long awb, int drs);

    Observable<Boolean> insertRvpShipperId(Reverse reverse);

    Observable<Reverse> getRvpShipperId();

    Observable<Long> insert(RVPQCImageTable rvpqcImageTable);

    Observable<List<RVPQCImageTable>> getImageForAwb(String awb);

    Observable<Integer> deleteRVPQCImageTable();

    // Deleting RTS Shipment after successfully synced:-
    Observable<Boolean> deleteShipmentData(int detailId);

    Observable<Boolean> updateShipmentDetailData(int shipmentStatus, String status, int detailID);
}