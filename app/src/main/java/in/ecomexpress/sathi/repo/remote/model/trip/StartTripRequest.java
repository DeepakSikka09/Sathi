package in.ecomexpress.sathi.repo.remote.model.trip;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by dhananjayk on 27-06-2018.
 */

public class StartTripRequest implements Parcelable {

    @JsonProperty("employee_code")
    private String employeeCode;
    @JsonProperty("route_name")
    private String routeName;
    @JsonProperty("trip_start_time")
    private long tripStartTime;
    @JsonProperty("trip_start_km")
    private long tripStartKm;
    @JsonProperty("start_lattitude")
    private double startLattitude;
    @JsonProperty("start_longitude")
    private double startLongitude;

    @JsonProperty("type_of_vehicle")
    private String typeOfVehicle;

    @JsonProperty("imei_number")
    private String imei ="";
    @JsonProperty("location_code")
    private String location_code;

    @JsonProperty("vehicle_owner_type")
    private String vehicleOwnerType;
    @JsonProperty("vehicle_number")
    private String vehicelNumber;


    @Embedded
    @JsonProperty("trip_images")
    private List<ImageResponse> imageListResponse;

    @JsonProperty("is_dp_employee")
    private String is_dp_employee;


    @JsonProperty("device_name")
    private String device_name;

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public long getTripStartTime() {
        return tripStartTime;
    }

    public void setTripStartTime(long tripStartTime) {
        this.tripStartTime = tripStartTime;
    }

    public long getTripStartKm() {
        return tripStartKm;
    }

    public void setTripStartKm(long tripStartKm) {
        this.tripStartKm = tripStartKm;
    }

    public double getStartLattitude() {
        return startLattitude;
    }

    public void setStartLattitude(double startLattitude) {
        this.startLattitude = startLattitude;
    }

    public double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public String getTypeOfVehicle() {
        return typeOfVehicle;
    }

    public void setTypeOfVehicle(String typeOfVehicle) {
        this.typeOfVehicle = typeOfVehicle;
    }

    public String getVehicleOwnerType() {
        return vehicleOwnerType;
    }

    public void setVehicleOwnerType(String vehicleOwnerType) {
        this.vehicleOwnerType = vehicleOwnerType;
    }

    public String getVehicelNumber() {
        return vehicelNumber;
    }

    public void setVehicelNumber(String vehicelNumber) {
        this.vehicelNumber = vehicelNumber;
    }



    public List<ImageResponse> getImageListResponse() {
        return imageListResponse;
    }

    public void setImageListResponse(List<ImageResponse> imageListResponse) {
        this.imageListResponse = imageListResponse;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public StartTripRequest(String vehicelNumber, String typeOfVehicle,
                            String vehicleOwnerType, String employeeCode, String routeName,
                            long tripStartTime, long tripStartKm, double startLattitude,
                            double startLongitude, String imei, String location_code , String is_dp_employee, List<ImageResponse> imageListResponse,String device_name) {
        this.vehicelNumber = vehicelNumber;
        this.typeOfVehicle = typeOfVehicle;
        this.vehicleOwnerType = vehicleOwnerType;
        this.employeeCode = employeeCode;
        this.routeName = routeName;
        this.imei = imei;
        this.location_code =location_code;
        this.tripStartTime = tripStartTime;
        this.tripStartKm = tripStartKm;
        this.startLattitude = startLattitude;
        this.startLongitude = startLongitude;
        this.is_dp_employee = is_dp_employee;
        this.imageListResponse= imageListResponse;
        this.device_name= device_name;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.employeeCode);
        dest.writeString(this.routeName);
        dest.writeLong(this.tripStartTime);
        dest.writeLong(this.tripStartKm);
        dest.writeDouble(this.startLattitude);
        dest.writeDouble(this.startLongitude);
        dest.writeString(this.typeOfVehicle);
        dest.writeString(this.vehicleOwnerType);
        dest.writeString(this.vehicelNumber);
        dest.writeTypedList(this.imageListResponse, flags);
        dest.writeString(this.is_dp_employee);
    }

    protected StartTripRequest(Parcel in) {
        this.employeeCode = in.readString();
        this.routeName = in.readString();
        this.tripStartTime = in.readLong();
        this.tripStartKm = in.readLong();
        this.startLattitude = in.readDouble();
        this.startLongitude = in.readDouble();
        this.typeOfVehicle = in.readString();
        this.vehicleOwnerType = in.readString();
        this.vehicelNumber = in.readString();
        this.imageListResponse = in.createTypedArrayList(ImageResponse.CREATOR);
        this.is_dp_employee = in.readString();
    }

    public static final Creator<StartTripRequest> CREATOR = new Creator<StartTripRequest>() {
        @Override
        public StartTripRequest createFromParcel(Parcel source) {
            return new StartTripRequest(source);
        }

        @Override
        public StartTripRequest[] newArray(int size) {
            return new StartTripRequest[size];
        }
    };

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getLocation_code() {
        return location_code;
    }

    public void setLocation_code(String location_code) {
        this.location_code = location_code;
    }

    public String getIs_dp_employee() {
        return is_dp_employee;
    }

    public void setIs_dp_employee(String is_dp_employee) {
        this.is_dp_employee = is_dp_employee;
    }
}
