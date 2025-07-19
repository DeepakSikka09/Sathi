package in.ecomexpress.sathi.repo.local.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.masterdata.GlobalConfigurationMaster;
@Dao
public interface GlobalConfigDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertall(List<GlobalConfigurationMaster> globalConfigurationMasters);


    @Query("SELECT * FROM GlobalConfigurationMasterTable")
    List<GlobalConfigurationMaster> getglobalConfigurationMasters();

    @Query("DELETE FROM GlobalConfigurationMasterTable")
    void nukeTable();
}
