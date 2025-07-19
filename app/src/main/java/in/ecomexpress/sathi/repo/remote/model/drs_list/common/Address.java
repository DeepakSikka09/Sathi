package in.ecomexpress.sathi.repo.remote.model.drs_list.common;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Embedded;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address implements Parcelable {

    @JsonProperty("line1")
    private String line1="";
    
    @JsonProperty("line2")
    private String line2="";
    
    @JsonProperty("line3")
    private String line3="";
    
    @JsonProperty("line4")
    private String line4="";
    
    @JsonProperty("city")
    private String city="";
    
    @JsonProperty("state")
    private String state="";
    
    @JsonProperty("pincode")
    private Integer pincode;

    @Embedded
    @JsonProperty("location")
    private Location location;
    public Address() {}

    public Address(Parcel in) {
        line1 = in.readString();
        line2 = in.readString();
        line3 = in.readString();
        line4 = in.readString();
        city = in.readString();
        state = in.readString();
        if (in.readByte() == 0) {
            pincode = null;
        } else {
            pincode = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(line1);
        dest.writeString(line2);
        dest.writeString(line3);
        dest.writeString(line4);
        dest.writeString(city);
        dest.writeString(state);
        if (pincode == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(pincode);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    @JsonProperty("line1")
    public String getLine1() {
        return line1;
    }

    @JsonProperty("line1")
    public void setLine1(String line1) {
        this.line1 = line1;
    }

    @JsonProperty("line2")
    public String getLine2() {
        return line2;
    }

    @JsonProperty("line2")
    public void setLine2(String line2) {
        this.line2 = line2;
    }

    @JsonProperty(value = "line3",defaultValue = " ")
    public String getLine3() {
        return line3;
    }

    @JsonProperty(value = "line3", defaultValue = " ")
    public void setLine3(String line3) {
        this.line3 = line3;
    }

    @JsonProperty(value = "line4" ,defaultValue = " ")
    public String getLine4() {
        return line4;
    }

    @JsonProperty(value = "line4",defaultValue = " ")
    public void setLine4(String line4) {
        this.line4 = line4;
    }

    @JsonProperty("city")
    public String getCity() {
        return city;
    }

    @JsonProperty("city")
    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty("state")
    public String getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }

    @JsonProperty("pincode")
    public Integer getPincode() {
        return pincode;
    }

    @JsonProperty("pincode")
    public void setPincode(Integer pincode) {
        this.pincode = pincode;
    }

    @JsonProperty("location")
    public Location getLocation() {
        return location;
    }

    @JsonProperty("location")
    public void setLocation(Location location) {
        this.location = location;
    }

	@Override
	public String toString() {
		return "Address [line1=" + line1 + ", line2=" + line2 + ", line3=" + line3 + ", line4=" + line4 + ", city="
				+ city + ", state=" + state + ", pincode=" + pincode + ", location=" + location + "]";
	}
    
}
