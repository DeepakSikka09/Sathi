package in.ecomexpress.sathi.repo.local.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import in.ecomexpress.sathi.repo.remote.model.masterdata.CallbridgeConfiguration;

/**
 * Created by shivangis on 12/7/2018.
 */
@Dao
public interface CallBridgeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(CallbridgeConfiguration callbridgeConfiguration);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertall(CallbridgeConfiguration callbridgeConfiguration);


    @Query("SELECT * FROM callbridge_configuration")
    CallbridgeConfiguration loadAll();

    @Query("DELETE FROM callbridge_configuration")
    void nukeTable();

}
