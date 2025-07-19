package in.ecomexpress.sathi.repo.local.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.masterdata.SampleQuestion;

/**
 * Created by dhananjayk on 27-07-2018.
 */
@Dao
public interface DRSRVPQCMasterDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<SampleQuestion> rvpQcCheckMaster);

    @Query("SELECT * FROM RVPQCMasterTable WHERE Code IN (:codes)")
    List<SampleQuestion> getRVPQCMasterDescription(List<String> codes);

    @Query("DELETE FROM RVPQCMasterTable")
    void nukeTable();
}
