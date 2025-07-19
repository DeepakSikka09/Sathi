package in.ecomexpress.sathi.repo.local.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.masterdata.ForwardReasonCodeMaster;

/**
 * Created by dhananjayk on 23-07-2018.
 */
@Dao
public interface DRSForwardMasterDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ForwardReasonCodeMaster> forwardReasonCodeMasters);

    @Query("SELECT * FROM ForwardMasterReasonCode WHERE pPD = :value")
    List<ForwardReasonCodeMaster> getAllForwardPPDReasonCodeMasterList(boolean value);

    @Query("SELECT * FROM ForwardMasterReasonCode WHERE cOD = :value")
    List<ForwardReasonCodeMaster> getAllForwardCODReasonCodeMasterList(boolean value);

    @Query("SELECT * FROM ForwardMasterReasonCode")
    List<ForwardReasonCodeMaster> getAllForwardReasonCodeMasterList();

    @Query("DELETE FROM ForwardMasterReasonCode")
    void nukeTable();

    @Query("SELECT * FROM ForwardMasterReasonCode WHERE SECURED = :value")
    List<ForwardReasonCodeMaster> getAllForwardSECReasonCodeMasterList(boolean value);

    @Query("SELECT * FROM ForwardMasterReasonCode WHERE MPS = :value")
    List<ForwardReasonCodeMaster> getAllForwardMPSReasonCodeMasterList(boolean value);
}
