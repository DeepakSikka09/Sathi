package in.ecomexpress.sathi.repo.remote.model.mps;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(indices = {@Index(value = {"awbNo", "drs", "id"}, unique = true)})
public class QcItem implements Parcelable {

    public QcItem() {}

    @NonNull
    @Override
    public String toString() {
        return "qc_item [id=" + id + ", item=" + item + ", qualityChecks=" + qualityChecks +
                ", awbNo=" + awbNo +
                ", drs=" + drs + "]";
    }

    @JsonProperty("quality_checks")
    private List<RvpMpsQualityCheck> qualityChecks;
    @JsonProperty("item")
    private String item;
    @JsonProperty("awb_no")
    private long awbNo;
    @JsonProperty("drs_id")
    private Integer drs;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(long unique_id) {
        this.unique_id = unique_id;
    }

    @JsonProperty("id")
    private long id;
    @PrimaryKey(autoGenerate = true)
    private long unique_id;

    protected QcItem(Parcel in) {
        qualityChecks = in.createTypedArrayList(RvpMpsQualityCheck.CREATOR);
        item = in.readString();
        awbNo = in.readLong();
        drs = in.readInt();
        id = in.readLong();
        unique_id = in.readLong();
    }

    public static final Creator<QcItem> CREATOR = new Creator<QcItem>() {
        @Override
        public QcItem createFromParcel(Parcel in) {
            return new QcItem(in);
        }

        @Override
        public QcItem[] newArray(int size) {
            return new QcItem[size];
        }
    };

    @JsonProperty("quality_checks")
    public List<RvpMpsQualityCheck> getQualityChecks() {
        return qualityChecks;
    }

    @JsonProperty("quality_checks")
    public void setQualityChecks(List<RvpMpsQualityCheck> value) {
        this.qualityChecks = value;
    }

    @JsonProperty("item")
    public String getItem() {
        return item;
    }

    @JsonProperty("item")
    public void setItem(String value) {
        this.item = value;
    }

    @JsonProperty("id")
    public long getid() {
        return id;
    }

    @JsonProperty("id")
    public void setid(long value) {
        this.id = value;
    }

    public long getAwbNo() {
        return awbNo;
    }

    public void setAwbNo(long awbNo) {
        this.awbNo = awbNo;
    }

    public Integer getDrs() {
        return drs;
    }

    public void setDrs(Integer drs) {
        this.drs = drs;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeTypedList(qualityChecks);
        parcel.writeString(item);
        parcel.writeLong(id);
        parcel.writeLong(awbNo);
        parcel.writeInt(drs);
        parcel.writeLong(unique_id);
    }
}
