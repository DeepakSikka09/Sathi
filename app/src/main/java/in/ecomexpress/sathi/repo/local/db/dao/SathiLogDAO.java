package in.ecomexpress.sathi.repo.local.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

import in.ecomexpress.sathi.repo.local.db.model.LiveTrackingLogTable;
import in.ecomexpress.sathi.repo.local.db.model.MsgLinkData;
import in.ecomexpress.sathi.repo.remote.model.LiveTrackingLog;

/**
 * Created by anshika on 7/4/20.
 */
@Dao
public interface SathiLogDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LiveTrackingLogTable liveTrackingLogTable);

    @Query("SELECT COUNT(id) FROM LiveTrackingLogTable")
    int getCount();

    @Query("SELECT * FROM LiveTrackingLogTable")
    List<LiveTrackingLogTable> getLiveTrackingLogTable();

    @Query("DELETE FROM LiveTrackingLogTable WHERE id = (SELECT MIN(id) FROM LiveTrackingLogTable)")
    int deleteFistRowTable();

    @Query("DELETE FROM LiveTrackingLogTable")
    void nukeTable();


}
