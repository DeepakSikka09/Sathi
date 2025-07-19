package in.ecomexpress.sathi.repo.remote.model.login;

import com.fasterxml.jackson.annotation.JsonProperty;

import in.ecomexpress.sathi.repo.remote.model.DeviceDetails;
import in.ecomexpress.sathi.repo.remote.model.base.ApiRequest;

public class MarkAttendanceRequest extends ApiRequest {

    @JsonProperty("emp_code")
    private String emp_code;

    @JsonProperty("imei_number")
    private String imei_number;

    @JsonProperty("location_code")
    private String location_code;



    public MarkAttendanceRequest(String emp_code, String imei_number, String location_code) {

        this.emp_code = emp_code;
        this.imei_number = imei_number;
        this.location_code = location_code;}

    public String getEmp_code() {
        return emp_code;
    }

    public void setEmp_code(String emp_code) {
        this.emp_code = emp_code;
    }

    public String getImei_number() {
        return imei_number;
    }
    public void setImei_number(String imei_number) {
        this.imei_number = imei_number;
    }

    public String getLocation_code() {
        return location_code;
    }

    public void setLocation_code(String location_code) {
        this.location_code = location_code;
    }
}
