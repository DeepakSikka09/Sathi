package in.ecomexpress.sathi.repo.remote.model.drs_list.forward;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class callbridge_details implements Parcelable {

    @JsonProperty("callbridge_number")
    private String callbridge_number;

    @JsonProperty("pin")
   // @ColumnInfo(name = "pin_column")
    private int pin;


    @JsonProperty("vendor_name")
    private String vendor_name;

    public callbridge_details() {

    }
    public callbridge_details(Parcel in) {
        this.callbridge_number = in.readString();
        this.pin = in.readInt();
        this.vendor_name = in.readString();


    }
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<callbridge_details> CREATOR = new Creator<callbridge_details>() {
        @Override
        public callbridge_details createFromParcel(Parcel in) {
            return new callbridge_details(in);
        }

        @Override
        public callbridge_details[] newArray(int size) {
            return new callbridge_details[size];
        }
    };


    @NonNull
    @Override
    public String toString() {
        return "callbridge_details [callbridge_number=" + callbridge_number + ", pin=" + pin + ", vendor_name=" + vendor_name + "]";

    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.callbridge_number);
        dest.writeInt(this.pin);
        dest.writeString(this.vendor_name);
    }
    @JsonProperty("callbridge_number")
    public String getCallbridge_number() {
        return callbridge_number;
    }
    @JsonProperty("callbridge_number")
    public void setCallbridge_number(String callbridge_number) {
        this.callbridge_number = callbridge_number;
    }
    @JsonProperty("pin")
    public int getPin() {
        return pin;
    }
    @JsonProperty("pin")
    public void setPin(int pin) {
        this.pin = pin;
    }
    @JsonProperty("vendor_name")
    public String getVendor_name() {
        return vendor_name;
    }
    @JsonProperty("vendor_name")
    public void setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
    }
}
