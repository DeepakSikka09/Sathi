package in.ecomexpress.sathi.repo.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RoomWarnings;
import androidx.room.Transaction;
import java.util.List;
import in.ecomexpress.sathi.repo.local.db.model.RVPMPSWithQC;
import in.ecomexpress.sathi.repo.remote.model.mps.DRSRvpQcMpsResponse;
import in.ecomexpress.sathi.repo.remote.model.mps.QcItem;

@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
@Dao
public interface DRSRvpMpsWithQCDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(DRSRvpQcMpsResponse drsRvpQcMpsResponse);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertQCMPS(List<QcItem> rvpQualitChecks);

    @Query("select * from QcItem")
    List<QcItem> getMPSQcValues();

    @Query("SELECT * FROM QcItem WHERE awbNo = :awbNo AND drs = :drs")
    List<QcItem> getQCDetailsForPickupActivity(long awbNo, int drs);

    @Query("DELETE FROM QcItem WHERE drs = :drs AND awbNo = :awbNo")
    void deleteMpsQcDataFromQcItemTable(int drs, long awbNo);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<DRSRvpQcMpsResponse> drsReverseQCTypeResponse);

    @Query("SELECT * FROM DRSRVPMPS WHERE shipmentStatus != 0 AND shipmentSyncStatus != 2")
    List<DRSRvpQcMpsResponse> getUnSyncRVPList();

    @Query("SELECT * FROM  DRSRVPMPS WHERE compositeKey = :composite_key")
    DRSRvpQcMpsResponse loadMpsShipmentDetailsFromDB(String composite_key);

    @Query("SELECT * FROM DRSRVPMPS ORDER BY drs, sequenceNo")
    List<DRSRvpQcMpsResponse> loadAllRVPMPSDRSList();

    @Query("SELECT COUNT(*) FROM DRSRVPMPS   WHERE  + shipmentStatus =:status")
    long getRVPMpsStatusCount(int status);

    @Query("DELETE FROM DRSRVPMPS")
    void nukeTable();

    @Query("DELETE FROM DRSRVPMPS WHERE shipmentStatus = 0")
    void nukeTableZeroSyncStatus();

    @Transaction
    @Query("SELECT * FROM DRSRVPMPS where compositeKey=:composite_key")
    RVPMPSWithQC getRvpMpsWithQc(String composite_key);

    @Query("UPDATE DRSRVPMPS SET shipmentStatus= :status WHERE compositeKey = :composite_key")
    void updateRvpMpsStatus(String composite_key, int status);

    @Query("UPDATE DRSRVPMPS SET isCallattempted= :isCallattempted WHERE awbNo = :awbNo")
    void updateRVPMpsCallAttempted(Long awbNo, int isCallattempted);

    @Query("SELECT isCallattempted FROM DRSRVPMPS WHERE + awbNo =:awbNo")
    long getIsMpsCallAttempted(Long awbNo);

    @Query("UPDATE DRSRVPMPS SET shipmentSyncStatus= :syncStatus WHERE compositeKey = :composite_key")
    void updateSyncStatus(String composite_key, int syncStatus);

    @Query("SELECT type FROM DRSRVPMPS WHERE awbNo = :awb")
    String getTypeOfShipment(String awb);

    @Query("DELETE FROM DRSRVPMPS")
    void deleteEntireMpsShipmentTable();

    @Query("DELETE FROM QCITEM")
    void deleteEntireQcItemTable();

    @Query("DELETE FROM RvpQualityCheck")
    void nukeTableQc();
}