package in.ecomexpress.sathi.repo.local.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RoomWarnings;

import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.consignee_profile.ProfileFound;


/**
 * Created by shivangis on 5/18/2019.
 */
@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)

@Dao
public interface ProfileFoundDAO {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
     void insertAll(List<ProfileFound> profileFoundList);

    @Query("SELECT * FROM ProfileFound")
    List<ProfileFound> getAllProfile();

    @Query("select * from ProfileFound where awb_number = :awbno")
    ProfileFound getProfileLatLng(long awbno);


}
