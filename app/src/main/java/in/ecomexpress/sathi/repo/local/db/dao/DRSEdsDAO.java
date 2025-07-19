package in.ecomexpress.sathi.repo.local.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.drs_list.eds.ActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.drs_list.eds.EDSTypeResponse;

/**
 * Created by Ashish Patel on 6/29/2018.
 */
@Dao
public interface DRSEdsDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(EDSTypeResponse edsTypeResponse);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<EDSTypeResponse> edsTypeResponses);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertActivityWizardList(List<ActivityWizard> activityWizards);

    @Query("SELECT * FROM DRSEDSList")
    List<EDSTypeResponse> loadAllEDSList();

    @Query("SELECT COUNT(*) FROM  DRSEDSList WHERE awbNo = :awbNo")
    Long getEDSAWbExist(long awbNo);

    @Query("SELECT COUNT(*) FROM DRSEDSList   WHERE  + shipmentStatus =:status")
    long getEDSStatusCount(int status);
   /* @Query("DELETE FROM DRSEDSList")
    void nukeTable();*/
}
