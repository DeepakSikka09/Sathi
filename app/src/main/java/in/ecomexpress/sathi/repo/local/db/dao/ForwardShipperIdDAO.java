package in.ecomexpress.sathi.repo.local.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import in.ecomexpress.sathi.repo.local.db.model.ApiUrlData;
import in.ecomexpress.sathi.repo.remote.model.masterdata.Forward;



@Dao
public interface ForwardShipperIdDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Forward forward);

    @Query("SELECT * FROM forward")
    Forward getAllForward();

    @Query("DELETE from forward")
    void nukeTable();



}
