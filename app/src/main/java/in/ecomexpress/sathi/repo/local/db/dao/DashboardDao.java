package in.ecomexpress.sathi.repo.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;
import in.ecomexpress.sathi.repo.remote.model.masterdata.DashboardBanner;

@Dao
public interface DashboardDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(List<DashboardBanner> dashboardBanner);

    @Query("SELECT * FROM banner")
    List<DashboardBanner> getAllDashboardBanner();

    @Query("DELETE FROM banner")
    void nukeTable();
}