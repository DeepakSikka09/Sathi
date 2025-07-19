package in.ecomexpress.sathi.repo.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.drs_list.DRSSequence;

@Dao
public interface DRSSequenceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<DRSSequence> drsSequenceList);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateAll(List<DRSSequence> drsSequenceList);

    @Query("SELECT * FROM DRSSequence ORDER BY sequence_no ASC ")
    List<DRSSequence> getAllDRSSequenceList();

    @Query("DELETE FROM DRSSequence")
    void removeAllSequenceData();

    @Query("DELETE FROM DRSSequence")
    void nukeTable();
}
