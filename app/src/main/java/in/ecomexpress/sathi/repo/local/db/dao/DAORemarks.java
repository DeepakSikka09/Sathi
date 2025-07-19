package in.ecomexpress.sathi.repo.local.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import in.ecomexpress.sathi.repo.local.db.model.Remark;

/**
 * Created by parikshittomar on 10-12-2018.
 */

@Dao
public interface DAORemarks {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Remark remarks);

    @Query("DELETE FROM Remark where awbNo=:awb")
    void deleteBasisAWB(long awb);

    @Query("DELETE FROM Remark where date <:currentDate")
    void deleteOldRemarks(long currentDate);

    @Query("DELETE FROM Remark where sync_status=:status")
    void deleteSyncedRemarks(int status);

    @Query("SELECT * FROM Remark where awbNo=:awb")
    Remark getRemarks(long awb);
   /* @Query("SELECT * FROM Remark where awbNo=:awb")
    Remark getRemarks(long awb);*/

    @Query("SELECT * FROM Remark where empCode=:code and date=:currentDate")
    List<Remark> getAllRemarks(String code, long currentDate);

    @Query("SELECT COUNT(*) FROM Remark   WHERE  + remark =:remark")
    long getRemarksoneCount(String remark);

    @Query("SELECT COUNT(*) FROM Remark   WHERE  + remark =:remark")
    long getRemarkstwoCount(String remark);

    @Query("SELECT COUNT(*) FROM Remark   WHERE remark LIKE :remark")
    long getRemarksthreeCount(String remark);

    @Query("SELECT COUNT(*) FROM Remark   WHERE  + remark =:remark")
    long getRemarksfourCount(String remark);

    @Query("SELECT COUNT(*) FROM Remark   WHERE  + remark =:remark")
    long getNoRemarksCount(String remark);
}

