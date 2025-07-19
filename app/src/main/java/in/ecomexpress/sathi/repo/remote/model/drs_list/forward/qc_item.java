package in.ecomexpress.sathi.repo.remote.model.drs_list.forward;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.room.TypeConverters;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import in.ecomexpress.sathi.repo.local.db.db_utils.QualityChecksConverter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class qc_item implements Parcelable {

    public qc_item(){}

    @JsonProperty("id")
    private Integer  id;

    @JsonProperty("item")
    private String item;

    @JsonProperty("quality_checks")
    @TypeConverters(QualityChecksConverter.class)
    public ArrayList<quality_checks> quality_checks;

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("item")
    public String getItem() {
        return item;
    }

    @JsonProperty("item")
    public void setItem(String item) {
        this.item = item;
    }

    public ArrayList<quality_checks> getQuality_checks() {
        return quality_checks;
    }

    public void setQuality_checks(ArrayList<quality_checks> quality_checks) {
        this.quality_checks = quality_checks;
    }

    protected qc_item(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.item = ((String) in.readValue((String.class.getClassLoader())));
        this.quality_checks = new ArrayList<quality_checks>();
        in.readList(this.quality_checks, quality_checks.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeValue(id);
        parcel.writeValue(item);
       parcel.writeList(quality_checks);
    }

    public static final Creator<qc_item> CREATOR = new Creator<qc_item>() {
        @Override
        public qc_item createFromParcel(Parcel in) {
            return new qc_item(in);
        }

        @Override
        public qc_item[] newArray(int size) {
            return new qc_item[size];
        }
    };
}