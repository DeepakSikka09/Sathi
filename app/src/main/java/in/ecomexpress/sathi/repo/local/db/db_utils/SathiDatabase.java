package in.ecomexpress.sathi.repo.local.db.db_utils;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import in.ecomexpress.sathi.repo.local.data.commit.PushApi;
import in.ecomexpress.sathi.repo.local.db.dao.AppApiUrlDAO;
import in.ecomexpress.sathi.repo.local.db.dao.CallBridgeDao;
import in.ecomexpress.sathi.repo.local.db.dao.CallBridgePSTNDao;
import in.ecomexpress.sathi.repo.local.db.dao.DAOCall;
import in.ecomexpress.sathi.repo.local.db.dao.DAORemarks;
import in.ecomexpress.sathi.repo.local.db.dao.DRSEDSReasonCodeDao;
import in.ecomexpress.sathi.repo.local.db.dao.DRSEdsNewDao;
import in.ecomexpress.sathi.repo.local.db.dao.DRSForwardDAO;
import in.ecomexpress.sathi.repo.local.db.dao.DRSForwardMasterDAO;
import in.ecomexpress.sathi.repo.local.db.dao.DRSNewRtsDAO;
import in.ecomexpress.sathi.repo.local.db.dao.DRSRTSMasterDAO;
import in.ecomexpress.sathi.repo.local.db.dao.DRSRVPMasterDAO;
import in.ecomexpress.sathi.repo.local.db.dao.DRSRVPQCMasterDao;
import in.ecomexpress.sathi.repo.local.db.dao.DRSRtsDAO;
import in.ecomexpress.sathi.repo.local.db.dao.DRSRvpMpsWithQCDAO;
import in.ecomexpress.sathi.repo.local.db.dao.DRSRvpWithQCDAO;
import in.ecomexpress.sathi.repo.local.db.dao.DRSSequenceDao;
import in.ecomexpress.sathi.repo.local.db.dao.DashboardDao;
import in.ecomexpress.sathi.repo.local.db.dao.DisputedAwbDAO;
import in.ecomexpress.sathi.repo.local.db.dao.EDSDocumentListMasterActivityDAO;
import in.ecomexpress.sathi.repo.local.db.dao.EDSMasterActivityDAO;
import in.ecomexpress.sathi.repo.local.db.dao.EDSOPVMasterActivityDAO;
import in.ecomexpress.sathi.repo.local.db.dao.ForwardShipperIdDAO;
import in.ecomexpress.sathi.repo.local.db.dao.GlobalConfigDao;
import in.ecomexpress.sathi.repo.local.db.dao.ImageDAO;
import in.ecomexpress.sathi.repo.local.db.dao.MsgLinkDAO;
import in.ecomexpress.sathi.repo.local.db.dao.ProfileFoundDAO;
import in.ecomexpress.sathi.repo.local.db.dao.PushApiDAO;
import in.ecomexpress.sathi.repo.local.db.dao.RVPQCImageTableDao;
import in.ecomexpress.sathi.repo.local.db.dao.RVPShipperIdDao;
import in.ecomexpress.sathi.repo.local.db.dao.RescheduleEdsDao;
import in.ecomexpress.sathi.repo.local.db.dao.SathiLogDAO;
import in.ecomexpress.sathi.repo.local.db.model.ApiUrlData;
import in.ecomexpress.sathi.repo.local.db.model.ImageModel;
import in.ecomexpress.sathi.repo.local.db.model.LiveTrackingLogTable;
import in.ecomexpress.sathi.repo.local.db.model.MsgLinkData;
import in.ecomexpress.sathi.repo.local.db.model.RVPQCImageTable;
import in.ecomexpress.sathi.repo.local.db.model.Remark;
import in.ecomexpress.sathi.repo.local.db.model.RescheduleEdsD;
import in.ecomexpress.sathi.repo.local.db.model.User;
import in.ecomexpress.sathi.repo.remote.model.call.Call;
import in.ecomexpress.sathi.repo.remote.model.consignee_profile.ProfileFound;
import in.ecomexpress.sathi.repo.remote.model.drs_list.DRSSequence;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.DRSReturnToShipperTypeResponse;
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
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterDocumentList;
import in.ecomexpress.sathi.repo.remote.model.masterdata.RTSReasonCodeMaster;
import in.ecomexpress.sathi.repo.remote.model.masterdata.RVPReasonCodeMaster;
import in.ecomexpress.sathi.repo.remote.model.masterdata.Reverse;
import in.ecomexpress.sathi.repo.remote.model.masterdata.SampleQuestion;
import in.ecomexpress.sathi.repo.remote.model.mps.DRSRvpQcMpsResponse;
import in.ecomexpress.sathi.repo.remote.model.mps.QcItem;
import in.ecomexpress.sathi.repo.remote.model.payphi.raise_dispute.PaymentDisputedAwb;
import in.ecomexpress.sathi.utils.Constants;

@Database(version = Constants.DB_VERSION, entities = {User.class, Details.class, DashboardBanner.class, GlobalConfigurationMaster.class, CbPstnOptions.class, CallbridgeConfiguration.class, ShipmentsDetail.class, Remark.class, MasterDocumentList.class, MasterActivityData.class, Call.class, EDSResponse.class, EDSActivityWizard.class, DRSForwardTypeResponse.class, DRSReturnToShipperTypeResponse.class, DRSReverseQCTypeResponse.class, RvpQualityCheck.class, SampleQuestion.class, PushApi.class, ForwardReasonCodeMaster.class, EDSReasonCodeMaster.class, GeneralQuestion.class, RTSReasonCodeMaster.class, RVPReasonCodeMaster.class, ProfileFound.class, ImageModel.class, ApiUrlData.class, RescheduleEdsD.class, MsgLinkData.class,
        LiveTrackingLogTable.class,
        Forward.class, Reverse.class, PaymentDisputedAwb.class, DRSSequence.class, RVPQCImageTable.class, QcItem.class, DRSRvpQcMpsResponse.class}, exportSchema = false)
@TypeConverters({Converters.class})

public abstract class SathiDatabase extends RoomDatabase {

    public abstract DRSSequenceDao drsSequenceDao();

    public abstract RVPQCImageTableDao rvpqcImageTableDao();

    public abstract DRSForwardDAO drsForwardDAO();

    public abstract DAOCall daoCall();

    public abstract ProfileFoundDAO profileFoundDAO();

    public abstract DRSRtsDAO drsRtsDAO();

    public abstract DRSNewRtsDAO drsRtsNewDAO();

    public abstract DRSRvpWithQCDAO drsRvpWithQCDAO();

    public abstract DRSEdsNewDao drsEdsNewDao();

    public abstract PushApiDAO pushApiDAO();

    public abstract DRSForwardMasterDAO drsForwardMasterDAO();

    public abstract DRSRTSMasterDAO drsRTSMasterDAO();

    public abstract DRSRVPMasterDAO drsRVPMasterDAO();

    public abstract DRSRVPQCMasterDao drsRVPQCMasterDao();

    public abstract ImageDAO imageDAO();

    public abstract EDSMasterActivityDAO edsMasterActivityDAO();

    public abstract DRSEDSReasonCodeDao drsedsReasonCodeDao();

    public abstract EDSDocumentListMasterActivityDAO edsDocumentListMasterActivityDAO();

    public abstract EDSOPVMasterActivityDAO edsopvMasterActivityDAO();

//    public abstract QuizDao quizDao();

    public abstract DAORemarks daoRemarks();

    public abstract CallBridgeDao callbridgedao();

    public abstract DashboardDao dashboardDao();

    public abstract CallBridgePSTNDao callBridgePSTNDao();

    public abstract GlobalConfigDao globalConfigDao();

    public abstract SathiLogDAO sathiLogDAO();

    public abstract AppApiUrlDAO appApiUrlDAO();

    public abstract ForwardShipperIdDAO forwardShipperIdDAO();

    public abstract RescheduleEdsDao rescheduleEdsDao();

    public abstract MsgLinkDAO msgLinkDAO();

    public abstract DisputedAwbDAO DisputedAwbDAO();

    public abstract RVPShipperIdDao rvpShipperIdDAO();

    public abstract DRSRvpMpsWithQCDAO drsRvpMpsWithQCDAO();
}