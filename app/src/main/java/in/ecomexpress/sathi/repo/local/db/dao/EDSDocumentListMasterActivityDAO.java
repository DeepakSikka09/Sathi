package in.ecomexpress.sathi.repo.local.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterDocumentList;

/**
 * Created by dhananjayk on 19-11-2018.
 */
@Dao
public interface EDSDocumentListMasterActivityDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<MasterDocumentList> documentLists);

    @Query("select * from DocumentListTable where shipper = :shipperId AND listType = :listType")
    MasterDocumentList getEDSDocumentListMasterDescription(String shipperId, String listType);

    @Query("DELETE FROM DocumentListTable")
    void nukeTable();

  /*  @Query("DELETE FROM MasterDocumentList")
    void nukeMasterDocumentListTable();*/
}
