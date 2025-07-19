package in.ecomexpress.sathi.repo.local.db.dao;

import android.database.Observable;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import in.ecomexpress.sathi.repo.remote.model.payphi.raise_dispute.PaymentDisputedAwb;


@Dao
public interface DisputedAwbDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PaymentDisputedAwb disputeAwb);

    @Query("SELECT disputed_awb FROM PaymentDisputedAwb   WHERE  disputed_awb =:awb")
    String getdisputedawb(String awb);

    @Query("DELETE FROM PaymentDisputedAwb")
    void nukeTable();

}
