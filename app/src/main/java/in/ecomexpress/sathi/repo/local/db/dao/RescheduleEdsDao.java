package in.ecomexpress.sathi.repo.local.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

import in.ecomexpress.sathi.repo.local.db.model.RescheduleEdsD;
import in.ecomexpress.sathi.repo.remote.model.eds.Awb_reschedule_info;

/**
 * Created by anshika on 20/12/19.
 */
@Dao
public interface RescheduleEdsDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insert(RescheduleEdsD rescheduleEds);
//
//    @Query("SELECT * FROM RescheduleEdsD")
//    List<RescheduleEdsD> getRescheduleEds();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ArrayList<RescheduleEdsD> appApiUrls);

    @Query("SELECT * FROM RescheduleEdsD")
    List<RescheduleEdsD> getEdsRescheduleData();

    @Query("DELETE FROM RescheduleEdsD")
    void nukeTable();
    @Query("SELECT reschedule_status FROM RescheduleEdsD   WHERE  + awb_number =:awb")
    boolean getEdsRescheduleDataFlag(String awb);
}

