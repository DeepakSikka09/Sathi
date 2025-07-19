package in.ecomexpress.sathi.repo.remote.model.sos;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by parikshittomar on 28-02-2019.
 */


public class SOSRequest {


    public SOSRequest(String empCode, double latitude, double longitude, long timeStamp) {
        setEmpCode(empCode);
        setLatitude(latitude);
        setLongitude(longitude);
        setTimeStamp(timeStamp);
    }

    @JsonProperty("employee_code")
    private String empCode;

    @JsonProperty("lat")
    private double latitude;

    @JsonProperty("lng")
    private double longitude;


    @JsonProperty("date")
    private long timeStamp;

    @JsonProperty("employee_code")
    public String getEmpCode() {
        return empCode;
    }

    @JsonProperty("employee_code")
    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    @JsonProperty("lat")
    public double getLatitude() {
        return latitude;
    }

    @JsonProperty("lat")
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @JsonProperty("lng")
    public double getLongitude() {
        return longitude;
    }

    @JsonProperty("lng")
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @JsonProperty("date")
    public long getTimeStamp() {
        return timeStamp;
    }

    @JsonProperty("date")
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
