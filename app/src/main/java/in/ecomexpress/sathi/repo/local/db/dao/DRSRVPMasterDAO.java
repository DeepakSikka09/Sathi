package in.ecomexpress.sathi.repo.local.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
import java.util.Set;

import in.ecomexpress.sathi.repo.remote.model.masterdata.RVPReasonCodeMaster;

/**
 * Created by dhananjayk on 23-07-2018.
 */
@Dao
public interface DRSRVPMasterDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<RVPReasonCodeMaster> reasonCodeMasters);


    @Query("SELECT * FROM RVPMasterReasonCode WHERE SECURED = :value")
    List<RVPReasonCodeMaster> getAllRVPReasonCodeMasterList(boolean value);

    @Query("DELETE FROM RVPMasterReasonCode")
    void nukeTable();

    @Query("SELECT DISTINCT subGroup FROM RVPMasterReasonCode")
    List<String> getSubgroupFromRvpReasonCode();


    @Query("SELECT * FROM RVPMasterReasonCode WHERE subGroup = :selectedReason")
    List<RVPReasonCodeMaster> getSubReasonCodeFromSubGroup(String selectedReason);


}
