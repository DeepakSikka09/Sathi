package in.ecomexpress.sathi.repo.local.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import in.ecomexpress.sathi.repo.local.db.model.RVPQCImageTable;

@Dao
public interface RVPQCImageTableDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(RVPQCImageTable rvpqcImageTable);

    @Query("Select * from RVPQCImageTable where awb_number = :awb")
    List<RVPQCImageTable> getImageForAwb(String awb);

    @Query("DELETE FROM RVPQCImageTable")
    int deleteRVPQCImageTable() ;



}
