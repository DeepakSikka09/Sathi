package in.ecomexpress.sathi.repo.local.db.dao;



import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;


/**
 * Created by dhananjayk on 27-10-2018.
 */
@Dao
public interface DRSEdsNewDao {
    @Query("SELECT COUNT(*) FROM  EDSResponse WHERE compositeKey = :composite")
    Long getEDSNewAWbExist(String composite);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(EDSResponse edsResponses);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<EDSResponse> edsResponses);

    @Query("Update EDSResponse set ofd_otp = :otp where awbNo = :awb")
    void updateOTPEDS(long awb, String otp);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertActivityWizard(List<EDSActivityWizard> activityWizards);

    @Query("SELECT * FROM EDSResponse ORDER BY drsNo, sequenceNo")
    List<EDSResponse> loadAllEDSList();

    @Query("SELECT * FROM EDSResponse WHERE shipmentStatus != 0 AND shipmentSyncStatus != 2")
    List<EDSResponse> getUnSyncEdsList();

    @Query("DELETE FROM EDSResponse")
    void nukeTableEDSResponse();

    @Query("DELETE FROM EDSResponse WHERE shipmentStatus = 0")
    void nukeTableEDSResponseZeroSyncStatus();

    @Transaction
    @Query("SELECT * FROM EDSResponse where compositeKey= :composite_key")
    EdsWithActivityList getEdswithActivityList(String composite_key);

    @Query("SELECT COUNT(*) FROM EDSResponse   WHERE  + shipmentStatus =:status")
    long getEDSStatusCount(int status);

    @Query("DELETE FROM EDSActivityWizard")
    void nukeTableEDSActivityWizard();


    @Query("UPDATE EDSResponse SET shipmentStatus= :status WHERE compositeKey = :composite")
    void updateEdsStatus(String composite, int status);

    @Query("SELECT * FROM EDSResponse   WHERE  compositeKey =:compositeKey")
    EDSResponse getEDS(String compositeKey);

    @Query("UPDATE EDSResponse SET isCallattempted= :isCallattempted WHERE awbNo = :awbNo")
    void updateEDSCallAttempted(Long awbNo, int isCallattempted);

    @Query("SELECT isCallattempted FROM EDSResponse   WHERE  + awbNo =:awbNo")
    long getisCallattempted(Long awbNo);


    @Query("UPDATE EDSResponse SET isCallattempted= :isCallattempted WHERE pin = :pin")
    void updateEDSpinCallAttempted(Long pin, int isCallattempted);

    @Query("UPDATE EDSResponse SET shipmentSyncStatus= :syncStatus WHERE CompositeKey = :compositeKey")
    void updateSyncStatus(String compositeKey, int syncStatus);
}
