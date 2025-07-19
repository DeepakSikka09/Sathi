package in.ecomexpress.sathi.repo.local.db.dao;



import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;

/**
 * Created by dhananjayk on 29-10-2018.
 */
@Dao
public interface EDSMasterActivityDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<MasterActivityData> activityListMasters);

    @Query("SELECT * FROM EDSACTIVITYMASTERTABLE WHERE Code IN (:codes)")
    List<MasterActivityData> getEDSQCMasterDescription(List<String> codes);

    @Query("DELETE FROM EDSACTIVITYMASTERTABLE")
    void nukeTable();

    /*@Query("DELETE FROM MasterActivityData")
    void nukeMasterActivityDataTable();*/


}
