package in.ecomexpress.sathi.repo.local.db.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by parikshittomar on 10-12-2018.
 */
@Entity
public class Remark {

    @PrimaryKey
    public long awbNo;

    @ColumnInfo
    public String empCode;

    @ColumnInfo
    public long date;

    @ColumnInfo
    public int sync_status;

    @ColumnInfo
    public String remark;

    @ColumnInfo
    public int count;

    @Override
    public String toString() {
        return "awbNo: " + awbNo + "empCode: " + empCode + "date: " + date + "sync_status: " + "remark: " + remark+ "count: " + count;
    }
}
