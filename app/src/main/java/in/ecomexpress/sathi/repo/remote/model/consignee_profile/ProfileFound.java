package in.ecomexpress.sathi.repo.remote.model.consignee_profile;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "ProfileFound")
public class ProfileFound implements Parcelable {

    @PrimaryKey
    @JsonProperty("awb_number")
    private long awb_number;

    @JsonProperty("last_attempted_fe_code")
    private String last_attempted_fe_code;

    @JsonProperty("last_attempted_fe_name")
    private String last_attempted_fe_name;

    @JsonProperty("red_alert")
    private boolean red_alert;

    @JsonProperty("total_attempt")
    private int total_attempt;

    @JsonProperty("delivery_latitude")
    private double delivery_latitude;

    @JsonProperty("delivery_longitude")
    private double delivery_longitude;

    @JsonProperty("time_slot")
    private String time_slot;

    @JsonProperty("awb_number")
    public long getAwb_number() {
        return awb_number;
    }

    @JsonProperty("awb_number")
    public void setAwb_number(long awb_number) {
        this.awb_number = awb_number;
    }

    @JsonProperty("red_alert")
    public boolean isRed_alert() {
        return red_alert;
    }

    @JsonProperty("red_alert")
    public void setRed_alert(boolean red_alert) {
        this.red_alert = red_alert;
    }

    @JsonProperty("total_attempt")
    public int getTotal_attempt() {
        return total_attempt;
    }

    @JsonProperty("total_attempt")
    public void setTotal_attempt(int total_attempt) {
        this.total_attempt = total_attempt;
    }

    @JsonProperty("delivery_latitude")
    public double getDelivery_latitude() {
        return delivery_latitude;
    }

    @JsonProperty("delivery_latitude")
    public void setDelivery_latitude(double delivery_latitude) {
        this.delivery_latitude = delivery_latitude;
    }

    @JsonProperty("delivery_longitude")
    public double getDelivery_longitude() {
        return delivery_longitude;
    }

    @JsonProperty("delivery_longitude")
    public void setDelivery_longitude(double delivery_longitude) {
        this.delivery_longitude = delivery_longitude;
    }

    @JsonProperty("last_attempted_fe_code")
    public String getLast_attempted_fe_code() {
        return last_attempted_fe_code;
    }

    @JsonProperty("last_attempted_fe_code")
    public void setLast_attempted_fe_code(String last_attempted_fe_code) {
        this.last_attempted_fe_code = last_attempted_fe_code;
    }

    @JsonProperty("last_attempted_fe_name")
    public String getLast_attempted_fe_name() {
        return last_attempted_fe_name;
    }

    @JsonProperty("last_attempted_fe_name")
    public void setLast_attempted_fe_name(String last_attempted_fe_name) {
        this.last_attempted_fe_name = last_attempted_fe_name;
    }

    @JsonProperty("time_slot")
    public String getTime_slot() {
        return time_slot;
    }

    @JsonProperty("time_slot")
    public void setTime_slot(String time_slot) {
        this.time_slot = time_slot;
    }

    @Override
    public String toString() {
        return "ProfileFound [awb_number = " + awb_number + ", last_attempted_fe_code = " + last_attempted_fe_code + ", last_attempted_fe_name = " + last_attempted_fe_name + ", red_alert = " + red_alert + ", total_attempt = " + total_attempt + ", delivery_latitude = " + delivery_latitude + ", delivery_longitude = " + delivery_longitude + ", time_slot = " + time_slot + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.awb_number);
        dest.writeString(this.last_attempted_fe_code);
        dest.writeString(this.last_attempted_fe_name);
        dest.writeByte(this.red_alert ? (byte) 1 : (byte) 0);
        dest.writeInt(this.total_attempt);
        dest.writeDouble(this.delivery_latitude);
        dest.writeDouble(this.delivery_longitude);
        dest.writeString(this.time_slot);
    }

    public ProfileFound() {
    }

    protected ProfileFound(Parcel in) {
        this.awb_number = in.readLong();
        this.last_attempted_fe_code = in.readString();
        this.last_attempted_fe_name = in.readString();
        this.red_alert = in.readByte() != 0;
        this.total_attempt = in.readInt();
        this.delivery_latitude = in.readDouble();
        this.delivery_longitude = in.readDouble();
        this.time_slot = in.readString();
    }

    public static final Creator<ProfileFound> CREATOR = new Creator<ProfileFound>() {
        @Override
        public ProfileFound createFromParcel(Parcel source) {
            return new ProfileFound(source);
        }

        @Override
        public ProfileFound[] newArray(int size) {
            return new ProfileFound[size];
        }
    };
}