package in.ecomexpress.sathi.repo.remote.model.drs_list.forward;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PinbDetails implements Parcelable{

    @NonNull
    @Override
    public String toString() {
        return "PinbDetails{" +
                "pinb_value='" + pinb_value + '\'' +
                ", pinb_offline=" + pinb_offline +
                '}';
    }

    @JsonProperty("pinb_value")
    private String pinb_value;
    @JsonProperty("pinb_offline")
    private Boolean pinb_offline;

    @JsonProperty("pinb_value")
    public String getPinb_value() {
        return pinb_value;
    }

    @JsonProperty("pinb_value")
    public void setPinb_value(String pinb_value) {
        this.pinb_value = pinb_value;
    }

    @JsonProperty("pinb_offline")
    public Boolean getPinb_offline() {
        return pinb_offline;
    }

    @JsonProperty("pinb_offline")
    public void setPinb_offline(Boolean pinb_offline) {
        this.pinb_offline = pinb_offline;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.pinb_value);
        dest.writeValue(this.pinb_offline);
    }

    public PinbDetails() {
    }

    protected PinbDetails(Parcel in) {
        this.pinb_value = in.readString();
        this.pinb_offline = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<PinbDetails> CREATOR = new Creator<PinbDetails>() {
        @Override
        public PinbDetails createFromParcel(Parcel source) {
            return new PinbDetails(source);
        }

        @Override
        public PinbDetails[] newArray(int size) {
            return new PinbDetails[size];
        }
    };
}