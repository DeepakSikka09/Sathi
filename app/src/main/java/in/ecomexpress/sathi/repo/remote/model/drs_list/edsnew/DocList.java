package in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dhananjayk on 20-11-2018.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocList implements Parcelable {
    @JsonProperty("shipper")
    private String shipper = null;
    @JsonProperty("list_type")
    private String listType = null;

    public String getShipper() {
        return shipper;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
    }

    public String getListType() {
        return listType;
    }

    public void setListType(String listType) {
        this.listType = listType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.shipper);
        dest.writeString(this.listType);
    }

    public DocList() {
    }

    protected DocList(Parcel in) {
        this.shipper = in.readString();
        this.listType = in.readString();
    }

    public static final Creator<DocList> CREATOR = new Creator<DocList>() {
        @Override
        public DocList createFromParcel(Parcel source) {
            return new DocList(source);
        }

        @Override
        public DocList[] newArray(int size) {
            return new DocList[size];
        }
    };
}
