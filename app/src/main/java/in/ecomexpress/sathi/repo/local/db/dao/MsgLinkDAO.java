package in.ecomexpress.sathi.repo.local.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;
import in.ecomexpress.sathi.repo.local.db.model.MsgLinkData;
@Dao
public interface MsgLinkDAO {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MsgLinkData msgLinkData);

    @Query("SELECT * FROM MsgLinkData  WHERE awb_number = :value")
    MsgLinkData getMsgLinkData(String value);

    @Query("UPDATE MsgLinkData SET mobile_number_list= :mobile_number WHERE awb_number = :awbNo")
    void updateMobileList(ArrayList<String> mobile_number, String awbNo);


    @Query("DELETE from MsgLinkData")
    void nukeTable();
}
