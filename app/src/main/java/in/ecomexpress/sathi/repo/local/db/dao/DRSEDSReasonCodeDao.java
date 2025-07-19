package in.ecomexpress.sathi.repo.local.db.dao;



import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.HashSet;
import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.masterdata.EDSReasonCodeMaster;

/**
 * Created by dhananjayk on 23-11-2018.
 */
@Dao
public interface DRSEDSReasonCodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<EDSReasonCodeMaster> edsReasonCodeMasters);

    @Query("SELECT * FROM EDSMasterReasonCode")
    List<EDSReasonCodeMaster> loadAllEDSReasonCode();

    @Query("SELECT * FROM EDSMasterReasonCode WHERE eDSActivity = :value")
    List<EDSReasonCodeMaster> loadAllEDSActivityReasonCode(boolean value);


    @Query("SELECT * FROM EDSMasterReasonCode WHERE eDSActivityList = :value")
    List<EDSReasonCodeMaster> loadAllEDSActListReasonCode(boolean value);

    @Query("SELECT * FROM EDSMasterReasonCode WHERE eDSOTP = :value")
    List<EDSReasonCodeMaster> loadAllEDSOtpReasonCode(boolean value);


    // new query based new reason code

    @Query("SELECT * FROM EDSMasterReasonCode WHERE CALLM = :value")
    List<EDSReasonCodeMaster> loadAllEDSOtpReasonCodeCALLM(boolean value);
    @Query("SELECT * FROM EDSMasterReasonCode WHERE RCHD = :value")
    List<EDSReasonCodeMaster> loadAllEDSOtpReasonCodeRCHD(boolean value);
    @Query("SELECT * FROM EDSMasterReasonCode WHERE SECURED = :value")
    List<EDSReasonCodeMaster> loadAllEDSOtpReasonCodeSecured(boolean value);
    @Query("SELECT * FROM EDSMasterReasonCode WHERE EDS_CC = :value")
    List<EDSReasonCodeMaster> loadAllEDSOtpReasonCodeEDSCC(boolean value);
    @Query("SELECT * FROM EDSMasterReasonCode WHERE EDS_CPV = :value")
    List<EDSReasonCodeMaster> loadAllEDSOtpReasonCodeEDSCP(boolean value);
    @Query("SELECT * FROM EDSMasterReasonCode WHERE EDS_EKYC = :value")
    List<EDSReasonCodeMaster> loadAllEDSOtpReasonCodeEDS_EKYC(boolean value);
    @Query("SELECT * FROM EDSMasterReasonCode WHERE EDS_UDAAN = :value")
    List<EDSReasonCodeMaster> loadAllEDSOtpReasonCodeEDS_UDAAN(boolean value);
    @Query("SELECT * FROM EDSMasterReasonCode WHERE EDS_IMAGE = :value")
    List<EDSReasonCodeMaster> loadAllEDSOtpReasonCodeEDS_IMAGE(boolean value);
    @Query("SELECT * FROM EDSMasterReasonCode WHERE EDS_PAYTM_IMAGE = :value")
    List<EDSReasonCodeMaster> loadAllEDSOtpReasonCodeEDS_PAYTM_IMAGE(boolean value);
    @Query("SELECT * FROM EDSMasterReasonCode WHERE EDS_DC = :value")
    List<EDSReasonCodeMaster> loadAllEDSOtpReasonCodeEDS_DC(boolean value);
    @Query("SELECT * FROM EDSMasterReasonCode WHERE EDS_DV = :value")
    List<EDSReasonCodeMaster> loadAllEDSOtpReasonCodeEDS_DV(boolean value);
    @Query("SELECT * FROM EDSMasterReasonCode WHERE eDSOTP = :value")
    List<EDSReasonCodeMaster> loadAllEDSOtpReasonCodeEDS_OTP(boolean value);

    @Query("SELECT * FROM EDSMasterReasonCode WHERE EDS_EKYC_FAIL = :value")
    List<EDSReasonCodeMaster> loadAllEDSOtpReasonCodeEDS_EKYC_FAIL(boolean value);


    @Query("DELETE FROM EDSMasterReasonCode")
    void nukeTable();
}
