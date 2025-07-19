package in.ecomexpress.sathi.repo.remote.model.trip;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dhananjayk on 07-07-2018.
 */

public class StopTripRequest implements Parcelable {


    @JsonProperty("trip_id")
    private int tripId;
    @JsonProperty("trip_end_time")
    private long tripEndTime;
    @JsonProperty("trip_end_km")
    private long tripEndKm;
    @JsonProperty("end_lattitude")
    private double endLatitude;
    @JsonProperty("end_longitude")
    private double endLongitude;
    @JsonProperty("other_expenses")
    private float otherExpense;
    @JsonProperty("live_tracking_trip_id")
    private String live_tracking_trip_id;

    @JsonProperty("live_tracking_km")
    private double live_tracking_km;

    @Embedded
    @JsonProperty("image_response")
    private ImageResponse imageResponse;

    @JsonProperty("is_dp_employee")
    private String is_dp_employee;

    @JsonProperty("imei_number")
    private String imei_number;

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public long getTripEndTime() {
        return tripEndTime;
    }

    public void setTripEndTime(long tripEndTime) {
        this.tripEndTime = tripEndTime;
    }

    public long getTripEndKm() {
        return tripEndKm;
    }

    public void setTripEndKm(long tripEndKm) {
        this.tripEndKm = tripEndKm;
    }

    public double getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(double endLatitude) {
        this.endLatitude = endLatitude;
    }

    public double getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(double endLongitude) {
        this.endLongitude = endLongitude;
    }

    public float getOtherExpense() {
        return otherExpense;
    }

    public void setOtherExpense(float otherExpense) {
        this.otherExpense = otherExpense;
    }

    public ImageResponse getImageResponse() {
        return imageResponse;
    }

    public void setImageResponse(ImageResponse imageResponse) {
        this.imageResponse = imageResponse;
    }

    public StopTripRequest(int tripId, long tripEndTime, long tripEndKm, double endLatitude, double endLongitude, float otherExpense, ImageResponse imageResponse,
            String live_tracking_trip_id, double live_tracking_km , String is_dp_employee , String imei_number){
        this.tripId = tripId;
        this.tripEndTime = tripEndTime;
        this.tripEndKm = tripEndKm;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
        this.live_tracking_trip_id =live_tracking_trip_id;
        this.otherExpense = otherExpense;
        this.imageResponse = imageResponse;
        this.live_tracking_km = live_tracking_km;
        this.is_dp_employee = is_dp_employee;
        this.imei_number = imei_number;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.tripId);
        dest.writeLong(this.tripEndTime);
        dest.writeLong(this.tripEndKm);
        dest.writeDouble(this.endLatitude);
        dest.writeDouble(this.endLongitude);
        dest.writeFloat(this.otherExpense);
        dest.writeString(this.live_tracking_trip_id);
        dest.writeDouble(this.live_tracking_km);
        dest.writeParcelable(this.imageResponse, flags);
        dest.writeString(is_dp_employee);
        dest.writeString(imei_number);
    }

    protected StopTripRequest(Parcel in) {
        this.tripId = in.readInt();
        this.tripEndTime = in.readLong();
        this.tripEndKm = in.readLong();
        this.live_tracking_trip_id = in.readString();
        this.endLatitude = in.readDouble();
        this.endLongitude = in.readDouble();
        this.otherExpense = in.readFloat();
        this.live_tracking_km = in.readDouble();
        this.imageResponse = in.readParcelable(ImageResponse.class.getClassLoader());
        this.is_dp_employee = in.readString();
        this.imei_number = in.readString();
    }

    public static final Creator<StopTripRequest> CREATOR = new Creator<StopTripRequest>() {
        @Override
        public StopTripRequest createFromParcel(Parcel source) {
            return new StopTripRequest(source);
        }

        @Override
        public StopTripRequest[] newArray(int size) {
            return new StopTripRequest[size];
        }
    };

    public String getLive_tracking_trip_id() {
        return live_tracking_trip_id;
    }

    public void setLive_tracking_trip_id(String live_tracking_trip_id) {
        this.live_tracking_trip_id = live_tracking_trip_id;
    }

    public double getLive_tracking_km() {
        return live_tracking_km;
    }

    public void setLive_tracking_km(double live_tracking_km) {
        this.live_tracking_km = live_tracking_km;
    }

    public String getIs_dp_employee() {
        return is_dp_employee;
    }

    public void setIs_dp_employee(String is_dp_employee) {
        this.is_dp_employee = is_dp_employee;
    }

    public String getImei_number(){
        return imei_number;
    }

    public void setImei_number(String imei_number){
        this.imei_number = imei_number;
    }
}
