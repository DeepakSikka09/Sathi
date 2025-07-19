package in.ecomexpress.sathi.repo.remote.model.login;

import com.fasterxml.jackson.annotation.JsonProperty;

import in.ecomexpress.sathi.repo.remote.model.DeviceDetails;
import in.ecomexpress.sathi.repo.remote.model.base.ApiRequest;

public  class LoginRequest extends ApiRequest {

    @JsonProperty("password")
    private String password;

    @JsonProperty("device_info")
    private DeviceDetails deviceDetails;

    @JsonProperty("user_name")
    private String username;

    public LoginRequest(String email, String password, DeviceDetails deviceDetails) {

        this.username=email;
        this.password=password;
        this.deviceDetails=deviceDetails;}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public DeviceDetails getDeviceDetails() {
        return deviceDetails;
    }

    public void setDeviceDetails(DeviceDetails deviceDetails) {
        this.deviceDetails = deviceDetails;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return
                "LoginRequest{" +
                        "password = '" + password + '\'' +
                        ",device_info = '" + deviceDetails.toString() + '\'' +
                        ",username = '" + username + '\'' +
                        "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoginRequest)) return false;
        LoginRequest that = (LoginRequest) o;
        if (!getPassword().equals(that.getPassword())) return false;
        if (!getDeviceDetails().equals(that.getDeviceDetails())) return false;
        return getUsername().equals(that.getUsername());
    }

    @Override
    public int hashCode() {
        int result = getPassword().hashCode();
        result = 31 * result + getDeviceDetails().hashCode();
        result = 31 * result + getUsername().hashCode();
        return result;
    }
}