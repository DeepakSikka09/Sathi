package in.ecomexpress.sathi.repo.local.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.masterdata.CbPstnOptions;
@Dao
public interface CallBridgePSTNDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertall(List<CbPstnOptions> cbPstnOptions);

    @Query("SELECT * FROM callbridge_pstnoption")
    List<CbPstnOptions> getAllCbPstnoptions();

    @Query("DELETE FROM callbridge_pstnoption")
    void nukeTable();

}
