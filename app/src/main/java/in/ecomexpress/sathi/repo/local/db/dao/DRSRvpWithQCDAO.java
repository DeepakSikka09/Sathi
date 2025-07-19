package in.ecomexpress.sathi.repo.local.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RoomWarnings;
import androidx.room.Transaction;

import java.util.List;

import in.ecomexpress.sathi.repo.local.db.model.RvpWithQC;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.RvpQualityCheck;

/**
 * Created by dhananjayk on 11-06-2018.
 */
@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
@Dao
public interface DRSRvpWithQCDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(DRSReverseQCTypeResponse drsReverseQCTypeResponse);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertQC(List<RvpQualityCheck> rvpQualitChecks);

    @Query("select * from RvpQualityCheck")
    List<RvpQualityCheck> getQcValuesForAwb();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<DRSReverseQCTypeResponse> drsReverseQCTypeResponse);

    @Query("SELECT * FROM DRSRVP WHERE compositeKey = :composite_key")
    DRSReverseQCTypeResponse loadRVPDRS(String composite_key);

    @Query("SELECT * FROM DRSRVP WHERE shipmentStatus != 0 AND shipmentSyncStatus != 2")
    List<DRSReverseQCTypeResponse> getUnSyncRVPList();

    @Query("SELECT COUNT (*) FROM  DRSRVP WHERE compositeKey = :composite_key")
    Long getRVPAWbExist(String composite_key);

    @Query("SELECT * FROM DRSRVP ORDER BY drs, sequenceNo")
    List<DRSReverseQCTypeResponse> loadAllRVPDRSList();

    @Query("SELECT COUNT(*) FROM DRSRVP   WHERE  + shipmentStatus =:status")
    long getRVPStatusCount(int status);

    @Query("DELETE FROM DRSRVP")
    void nukeTable();

    @Query("DELETE FROM DRSRVP WHERE shipmentStatus = 0")
    void nukeTableZeroSyncStatus();

    @Transaction
    @Query("SELECT * FROM DRSRVP where compositeKey=:composite_key")
    RvpWithQC getRvpWithQc(String composite_key);

    @Query("UPDATE DRSRVP SET shipmentStatus= :status WHERE compositeKey = :composite_key")
    void updateRvpStatus(String composite_key, int status);

    @Query("UPDATE DRSRVP SET isCallattempted= :isCallattempted WHERE awbNo = :awbNo")
    void updateRVPCallAttempted(Long awbNo, int isCallattempted);

    @Query("SELECT isCallattempted FROM DRSRVP   WHERE  + awbNo =:awbNo")
    long getisCallattempted(Long awbNo);

    @Query("UPDATE DRSRVP SET isCallattempted= :isCallattempted WHERE pin = :pin")
    void updateRVPpinCallAttempted(Long pin, int isCallattempted);

    @Query("UPDATE DRSRVP SET shipmentSyncStatus= :syncStatus WHERE compositeKey = :composite_key")
    void updateSyncStatus(String composite_key, int syncStatus);

    @Query("SELECT type FROM DRSRVP WHERE awbNo = :awb")
    String getTypeOfShipment(String awb);

    @Query("SELECT is_rvp_phone_pe_flow FROM DRSRVP WHERE awbNo = :awb")
    String getPhonePeShipmentType(String awb);

    @Query("DELETE FROM RvpQualityCheck WHERE drs = :drs AND awbNo=:awb")
    void deleteQCData(int drs, long awb);

    @Query("DELETE FROM RvpQualityCheck")
    void nukeTableQc();
}
