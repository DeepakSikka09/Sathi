package in.ecomexpress.sathi.repo.local.db.dao;



import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RoomWarnings;

import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.DRSReturnToShipperTypeResponse;

/**
 * Created by dhananjayk on 06-06-2018.
 */
@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)

@Dao
public interface DRSRtsDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(DRSReturnToShipperTypeResponse drsReturnToShipperTypeResponse);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<DRSReturnToShipperTypeResponse> drsReturnToShipperTypeResponse);

    @Query("SELECT * FROM DRSRTSList")
    List<DRSReturnToShipperTypeResponse> loadAllRTSDRSList();

    @Query("SELECT * FROM DRSRTSList WHERE awbNo = :awbNo")
    DRSReturnToShipperTypeResponse loadAllRTSDRS(long awbNo);

    @Query("SELECT COUNT(*) FROM DRSRTSList   WHERE  + shipmentStatus =:status")
    long getRTSStatusCount(int status);

    @Query("DELETE FROM DRSRTSList")
    void nukeTable();

    @Query("DELETE FROM DRSRTSList WHERE shipmentStatus = 0")
    void nukeTableZeroSyncStatus();


    @Query("UPDATE DRSRTSList SET shipmentStatus= :status WHERE awbNo = :awbNo")
    void updateRtsStatus(Long awbNo, int status);


}
