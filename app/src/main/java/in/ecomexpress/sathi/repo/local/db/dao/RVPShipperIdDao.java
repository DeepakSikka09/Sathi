package in.ecomexpress.sathi.repo.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import in.ecomexpress.sathi.repo.remote.model.masterdata.Reverse;

@Dao
public interface RVPShipperIdDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Reverse reverse);

    @Query("Select * FROM Reverse")
    Reverse getAllRVP();

    @Query("Delete FROM Reverse")
    void nukeTable();
}
