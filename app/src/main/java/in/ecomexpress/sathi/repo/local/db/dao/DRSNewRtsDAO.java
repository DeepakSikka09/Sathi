package in.ecomexpress.sathi.repo.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RoomWarnings;
import androidx.room.Update;
import java.util.List;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.Details;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.ShipmentsDetail;

@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
@Dao
public interface DRSNewRtsDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertDetails(Details details);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertShipment(ShipmentsDetail shipmentDetail);

    @Update
    void updateShipment(ShipmentsDetail shipmentDetail);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllShipment(ShipmentsDetail... shipmentsDetail);

    @Query("SELECT * FROM vw_detail")
    List<Details> loadAllDetails();

    @Query("SELECT * FROM vw_detail WHERE id = :id")
    Details loadDetails(long id);

    @Query("SELECT * FROM rts_shipment_details WHERE detail_id = :vwId")
    List<ShipmentsDetail> getAllShipment(long vwId);

    @Query("SELECT * FROM rts_shipment_details WHERE awbNo = :awbNo or parentAwbNo = :awbNo")
    ShipmentsDetail getShipment(long awbNo);

    @Query("SELECT COUNT(*) FROM rts_shipment_details   WHERE  detail_id =:vwId and Status like :status")
    int getRTSUndeliveredCount(long vwId, String status);

    @Query("SELECT COUNT(*) FROM rts_shipment_details   WHERE  detail_id =:vwId and Status like :status")
    int getRTSDisputeDeliveryCount(long vwId, String status);

    @Query("SELECT COUNT(*) FROM rts_shipment_details   WHERE  detail_id =:vwId and Status=:status")
    int getRTSDeliveredCount(long vwId, String status);

    @Query("SELECT COUNT(*) FROM vw_detail   WHERE  + shipmentStatus =:status")
    long getRTSStatusCount(int status);


    @Query("SELECT COUNT(*) FROM rts_shipment_details   WHERE  detail_id =:vwId and Status=:status")
    int getRTSMannnuallyDeliveredCount(long vwId, String status);

    @Query("SELECT COUNT(*) FROM rts_shipment_details WHERE  detail_id =:vwId")
    int getRTSTotalCount(long vwId);

    @Query("DELETE FROM vw_detail")
    void nukeDetailTable();

    @Query("DELETE FROM rts_shipment_details")
    void nukeShipmentTable();

    @Query("UPDATE rts_shipment_details SET Status =:status AND checked=:checked WHERE detail_id =:vwID")
    void reassignAll(long vwID, String status, boolean checked);

    @Query("UPDATE vw_detail SET shipmentStatus= :status WHERE id = :id")
    void updateRtsStatus(Long id, int status);

    @Query("UPDATE rts_shipment_details SET IS_IMAGE_CAPTURED= :is_image_captured WHERE awbNo = :awb_no")
    void updateRtsImageCapturedStatus(Long awb_no, int is_image_captured);

    @Query("UPDATE vw_detail SET shipmentSyncStatus= :syncStatus WHERE id = :id")
    void updateSyncStatus(long id, int syncStatus);

    @Query("UPDATE vw_detail SET isCallattempted= :isCallattempted WHERE id = :id")
    void updateRTSCallAttempted(Long id, int isCallattempted);

    @Query("Select count(distinct reasonCode) from rts_shipment_details where detail_id = :detailId")
    int getDuplicateValueCount(String detailId);

    // Deleting RTS Shipment after successfully synced:-
    @Query("DELETE FROM rts_shipment_details WHERE detail_id = :detailId")
    void deleteShipmentData(int detailId);

    @Query("UPDATE vw_detail SET shipmentStatus = :shipmentStatus, status = :status WHERE id = :detailID")
    void updateShipmentDetailData(int shipmentStatus, String status, int detailID);
}