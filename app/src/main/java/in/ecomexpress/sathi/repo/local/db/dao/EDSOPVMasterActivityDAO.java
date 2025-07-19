package in.ecomexpress.sathi.repo.local.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.masterdata.GeneralQuestion;

/**
 * Created by dhananjayk on 28-11-2018.
 */
@Dao
public interface EDSOPVMasterActivityDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<GeneralQuestion> generalQuestions);

    @Query("SELECT * FROM GeneralQuestionMaster")
    List<GeneralQuestion> loadAllEDSOPVMasterCode();

    @Query("DELETE FROM GeneralQuestionMaster")
    void nukeTable();
}
