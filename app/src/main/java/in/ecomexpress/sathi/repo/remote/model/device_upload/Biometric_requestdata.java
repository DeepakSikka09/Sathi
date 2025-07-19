package in.ecomexpress.sathi.repo.remote.model.device_upload;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by santosh on 23/8/19.
 */

public class Biometric_requestdata {
    @JsonProperty("device_serial_no")
    private String device_serial_no;

    @JsonProperty("model_no")
    private String model_no;
    @JsonProperty("manufacturer")
    private String manufacturer;

    @JsonProperty("time_stamp")
    private String time_stamp;

    @JsonProperty("employee_code")
    private String employee_code;

    public Biometric_requestdata(String device_serial_no, String model_no, String manufacturer, String time_stamp, String employee_code) {
        this.device_serial_no = device_serial_no;
        this.model_no = model_no;
        this.manufacturer = manufacturer;
        this.time_stamp = time_stamp;
        this.employee_code = employee_code;
    }

    public String getEmployee_code() {
        return employee_code;
    }

    public void setEmployee_code(String employee_code) {
        this.employee_code = employee_code;
    }

    public String getDevice_serial_no() {
        return device_serial_no;
    }

    public void setDevice_serial_no(String device_serial_no) {
        this.device_serial_no = device_serial_no;
    }

    public String getModel_no() {
        return model_no;
    }

    public void setModel_no(String model_no) {
        this.model_no = model_no;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Biometric_requestdata that = (Biometric_requestdata) o;

        if (!device_serial_no.equals(that.device_serial_no)) return false;
        if (!model_no.equals(that.model_no)) return false;
        if (!manufacturer.equals(that.manufacturer)) return false;
        if (!time_stamp.equals(that.time_stamp)) return false;
        return employee_code.equals(that.employee_code);
    }

    @Override
    public int hashCode() {
        int result = device_serial_no.hashCode();
        result = 31 * result + model_no.hashCode();
        result = 31 * result + manufacturer.hashCode();
        result = 31 * result + time_stamp.hashCode();
        result = 31 * result + employee_code.hashCode();
        return result;
    }

}
