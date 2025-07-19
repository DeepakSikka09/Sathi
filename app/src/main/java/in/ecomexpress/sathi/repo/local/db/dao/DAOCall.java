package in.ecomexpress.sathi.repo.local.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import in.ecomexpress.sathi.repo.remote.model.call.Call;

@Dao
public interface DAOCall {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Call call);

    @Query("Select Status from Call WHERE Awb = :awb AND Drs =:drs")
    boolean getCallStatus(long awb, int drs);

}

