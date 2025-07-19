package in.ecomexpress.sathi.repo.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;
import in.ecomexpress.sathi.repo.local.data.commit.PushApi;

@Dao
public interface PushApiDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PushApi pushApi);

    @Query("Select * from PushApi where shipmentStatus = 0 OR shipmentStatus = 3 LIMIT :limit OFFSET :offset ")
    List<PushApi> UnSyncList(int limit, int offset);

    @Query("Select * from PushApi where shipmentStatus = 0 OR shipmentStatus = 3 OR shipmentStatus = 1  OR shipmentStatus = 2 OR shipmentStatus = 4")
    List<PushApi> getSizeofPushApi();

    @Query("Select * from PushApi")
    List<PushApi> getAllPushApiData();

    @Query("Update PushApi set shipmentStatus = 0  where awbNo IN (:awb)")
    void updatePushSyncStatusToZero(List<Long> awb);

    @Query("Update PushApi set shipmentStatus = 0  where awbNo =:awb")
    void updatePushSyncStatusToZeroOnClick(long awb);

    @Query("Update PushApi set shipmentStatus = :shipment_status  where awbNo =:awb")
    void updatePushShipmentStatus(long awb , int shipment_status);

    @Query("Select * from PushApi where awbNo=:awbNo AND shipmentStatus = 3")
    PushApi UnSyncPushApiPacket(long awbNo);

    @Query("Select * from PushApi where   shipmentStatus = 4 LIMIT :limit OFFSET :offset ")
    List<PushApi> UnAllPendingSyncList(int limit, int offset);

    @Query("DELETE FROM PushApi")
    void nukeTable();

    @Query("Select * from PushApi where awbNo = :awbnumber")
    List<PushApi> insertedOrNot(long awbnumber);

    @Query("Select * from PushApi where awbNo = :awbnumber")
    PushApi getPushApiDetail(long awbnumber);

    @Query("delete from PushApi where awbNo = :awbNo")
    void deleteAwb(long awbNo);
}