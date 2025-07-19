package in.ecomexpress.sathi.repo.remote.model.call;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


@Entity(tableName = "Call", indices = @Index(value = {"Awb"}, unique = true))
public class Call { // This table is to maintain calll records. Calls are already made or not.

    @PrimaryKey(autoGenerate = true)
    @JsonProperty("Id")
    private long id;

    @JsonProperty("Awb")
    @ColumnInfo(name = "Awb")
    private long Awb;

    @JsonIgnore
    @ColumnInfo(name = "Drs")
    private int Drs;

    @JsonIgnore
    @ColumnInfo(name = "Status")
    private boolean Status;

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public long getAwb(){
        return Awb;
    }

    public void setAwb(long awb){
        Awb = awb;
    }

    public int getDrs(){
        return Drs;
    }

    public void setDrs(int drs){
        Drs = drs;
    }

    public boolean isStatus(){
        return Status;
    }

    public void setStatus(boolean status){
        Status = status;
    }
}
