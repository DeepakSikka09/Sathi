package in.ecomexpress.sathi.repo.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RoomWarnings;
import java.util.List;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;

@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
@Dao
public interface DRSForwardDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(DRSForwardTypeResponse drsForwardTypeResponse);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<DRSForwardTypeResponse> drsForwardTypeResponses);

    @Query("SELECT * FROM DRSForward")
    List<DRSForwardTypeResponse> loadAllForwardList();

    @Query("Update DRSForward set ofd_otp = :otp where awbNo = :awb")
    void updateOTPForward(long awb, String otp);

    @Query("Update DRSForward set assignedDate = :assign_date where awbNo = :awb")
    void updateAssignDataForward(long awb, String assign_date);

    @Query("Update DRSForward set total_attempts = :attempts where awbNo = :awb")
    void updateTotalAttemptsForward(long awb, int attempts);

    @Query("SELECT * FROM DRSForward WHERE shipmentStatus != 0 AND shipmentSyncStatus != 2")
    List<DRSForwardTypeResponse> getUnSyncForwardList();

    //WHERE mpsShipment='P' OR mpsShipment IS NULL
    @Query("SELECT * FROM DRSForward WHERE mpsShipment='P' OR mpsShipment IS NULL ORDER BY drsId, sequenceNo")
    List<DRSForwardTypeResponse> loadAllForwardDRSList();

    @Query("SELECT * FROM DRSForward WHERE awbNo=:awb")
    DRSForwardTypeResponse loadForwardDRSList(Long awb);

    @Query("SELECT * FROM DRSForward WHERE awbNo=:awb")
    DRSForwardTypeResponse fetchObdQualityCheckData(Long awb);

    @Query("SELECT * FROM DRSForward WHERE compositeKey = :composite_key")
    DRSForwardTypeResponse loadAllForwardDRS(String composite_key);

    //New Query for FWD Count
    @Query("SELECT COUNT(*) FROM DRSForward   WHERE  shipmentStatus =:status AND mpsShipment='P' OR mpsShipment IS NULL AND shipmentStatus =:status")
    long getFWDStatusCount(int status);

    @Query("SELECT SUM(cod_collected) FROM DRSForward")
    double getCodCount();

    @Query("SELECT SUM(ecod_collected) FROM DRSForward")
    double getECodCount();

    @Query("SELECT isCallattempted FROM DRSForward   WHERE  + awbNo =:awbNo")
    long getisCallattempted(Long awbNo);

    @Query("UPDATE DRSForward SET shipmentStatus= :status WHERE compositeKey = :composite_key")
    void updateForwardStatus(String composite_key, int status);

    @Query("UPDATE DRSForward SET shipmentStatus= :status WHERE awbNo = :awbNo")
    void updateForwardMPSStatus(Long awbNo, int status);

    @Query("UPDATE DRSForward SET cod_collected= :amount WHERE awbNo = :awbNo")
    void updatecodCollected(Long awbNo, float amount);

    @Query("UPDATE DRSForward SET ecod_collected= :amount WHERE awbNo = :awbNo")
    void updateEcodCollected(Long awbNo, float amount);

    @Query("UPDATE DRSForward SET isCallattempted= :isCallattempted WHERE awbNo = :awbNo")
    void updateForwardCallAttempted(Long awbNo, int isCallattempted);

    @Query("DELETE FROM DRSForward")
    void nukeTable();

    @Query("DELETE FROM DRSForward WHERE shipmentStatus = 0")
    void nukeTableZeroSyncStatus();

    @Query("DELETE FROM DRSForward WHERE compositeKey = :compositkey")
    void deleteShipment(String compositkey);

    @Query("UPDATE DRSForward SET isCallattempted= :isCallattempted WHERE pin = :pin")
    void updateForwardpinCallAttempted(Long pin, int isCallattempted);

    @Query("UPDATE DRSForward SET shipmentSyncStatus= :syncStatus WHERE compositeKey = :composite_key")
    void updateSyncStatus(String composite_key, int syncStatus);

    @Query("UPDATE DRSForward SET samedayreassignstatus= :syncStatus WHERE compositeKey = :composite_key")
    void updateSameDayReassignSyncStatus(String composite_key, boolean syncStatus);

    @Query("UPDATE DRSForward SET shipmentSyncStatus= :syncStatus, shipmentStatus=:status, drsId= :drs_id, compositeKey= :composite_key_new WHERE awbNo = :awb_no")
    void updateForwardShipment(String awb_no, int syncStatus, int status, int drs_id, String composite_key_new);
}