package in.ecomexpress.sathi.repo.remote.model.masterdata;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by parikshittomar on 02-03-2019.
 */


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WaterMark implements Parcelable {

    @JsonProperty("emp_code")
    public String emp_code;

    @JsonProperty("emp_name")
    public String emp_name;

    @JsonProperty("lat")
    public String lat;

    @JsonProperty("lng")
    public String lng;

    @JsonProperty("date")
    public String date;

    @JsonProperty("ecom_text")
    public String ecom_text;


    @JsonProperty("osv_text")
    public String osv_text;

    @JsonProperty("emp_code")
    public String getEmp_code() {
        return emp_code;
    }

    @JsonProperty("emp_code")
    public void setEmp_code(String emp_code) {
        this.emp_code = emp_code;
    }

    @JsonProperty("emp_name")
    public String getEmp_name() {
        return emp_name;
    }

    @JsonProperty("emp_name")
    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    @JsonProperty("lat")
    public String getLat() {
        return lat;
    }

    @JsonProperty("lat")
    public void setLat(String lat) {
        this.lat = lat;
    }

    @JsonProperty("lng")
    public String getLng() {
        return lng;
    }

    @JsonProperty("lng")
    public void setLng(String lng) {
        this.lng = lng;
    }

    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(String date) {
        this.date = date;
    }

    @JsonProperty("ecom_text")
    public String getEcom_text() {
        return ecom_text;
    }

    @JsonProperty("ecom_text")
    public void setEcom_text(String ecom_text) {
        this.ecom_text = ecom_text;
    }

    @JsonProperty("osv_text")
    public String getOsv_text() {
        return osv_text;
    }

    @JsonProperty("osv_text")
    public void setOsv_text(String osv_text) {
        this.osv_text = osv_text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.emp_code);
        dest.writeString(this.emp_name);
        dest.writeString(this.lat);
        dest.writeString(this.lng);
        dest.writeString(this.date);
        dest.writeString(this.ecom_text);
        dest.writeString(this.osv_text);
    }

    public WaterMark() {
    }

    protected WaterMark(Parcel in) {
        this.emp_code = in.readString();
        this.emp_name = in.readString();
        this.lat = in.readString();
        this.lng = in.readString();
        this.date = in.readString();
        this.ecom_text = in.readString();
        this.osv_text = in.readString();
    }

    public static final Creator<WaterMark> CREATOR = new Creator<WaterMark>() {
        @Override
        public WaterMark createFromParcel(Parcel source) {
            return new WaterMark(source);
        }

        @Override
        public WaterMark[] newArray(int size) {
            return new WaterMark[size];
        }
    };
}
