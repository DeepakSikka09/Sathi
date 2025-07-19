package in.ecomexpress.sathi.repo.local.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.masterdata.RTSReasonCodeMaster;

/**
 * Created by dhananjayk on 23-07-2018.
 */
@Dao
public interface DRSRTSMasterDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<RTSReasonCodeMaster> rtsReasonCodeMasters);

    @Query("SELECT * FROM   rtsmasterreasoncode")
    List<RTSReasonCodeMaster> getAllReasonCode();

    @Query("Select * from rtsmasterreasoncode where reasonCode= :reasoncode")
    RTSReasonCodeMaster isAttributeAvailable(String reasoncode);

    @Query("DELETE FROM RTSMasterReasonCode")
    void nukeTable();
}
