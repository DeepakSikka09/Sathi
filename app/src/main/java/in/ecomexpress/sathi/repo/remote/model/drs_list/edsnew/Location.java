package in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dhananjayk on 26-10-2018.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Location implements Parcelable{

    @JsonProperty("consignee_lattitude")
    private double consigneeLatitude;
    @JsonProperty("consignee_longitude")
    private double consigneeLongitude;

    public double getConsigneeLatitude() {
        return consigneeLatitude;
    }

    public void setConsigneeLatitude(double consigneeLatitude) {
        this.consigneeLatitude = consigneeLatitude;
    }

    public double getConsigneeLongitude() {
        return consigneeLongitude;
    }

    public void setConsigneeLongitude(double consigneeLongitude) {
        this.consigneeLongitude = consigneeLongitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(String.valueOf(this.consigneeLatitude));
        dest.writeString(String.valueOf(this.consigneeLongitude));
    }

    public Location() {
    }

    protected Location(Parcel in) {
        this.consigneeLatitude = Double.parseDouble(in.readString());
        this.consigneeLongitude = Double.parseDouble(in.readString());
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel source) {
            return new Location(source);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
}
