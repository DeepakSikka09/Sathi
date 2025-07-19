package in.ecomexpress.sathi.repo.local.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import in.ecomexpress.sathi.repo.local.db.model.ApiUrlData;
import in.ecomexpress.sathi.repo.local.db.model.User;

@Dao
public interface AppApiUrlDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<ApiUrlData> appApiUrls);

    @Query("SELECT * FROM ApiUrlData")
    List<ApiUrlData> getAllApiUrl();

    @Query("DELETE from ApiUrlData")
    void nukeTable();

}

