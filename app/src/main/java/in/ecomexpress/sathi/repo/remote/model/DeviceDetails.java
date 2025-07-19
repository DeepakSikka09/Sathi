package in.ecomexpress.sathi.repo.remote.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;

public class DeviceDetails {

    @JsonProperty("sdk_version_code")
    private int sdkVersionCode;

    @JsonProperty("device_id")
    private String deviceId;

    @JsonProperty("latitude")
    private double latitude;

    @JsonProperty("is_otg_enabled")
    private boolean isOtgEnabled;

    @JsonProperty("sdk_version")
    private String sdkVersion;

    @JsonProperty("device_time")
    private long deviceTime;

    @JsonProperty("ip_address")
    private String ipAddress;

    @JsonProperty("model_number")
    private String modelNumber;

    @JsonProperty("longitude")
    private double longitude;

    @JsonProperty("manufacturer")
    private String manufacturer;

    @JsonIgnore
    private String kernalVersion;

    @JsonIgnore
    private String osVersion;

    @JsonIgnore
    private String manufacturerOSVersion;

    @JsonIgnore
    private String deviceName;

    public void setSdkVersionCode(int sdkVersionCode) {
        this.sdkVersionCode = sdkVersionCode;
    }

    public int getSdkVersionCode() {
        return sdkVersionCode;
    }

    public void setDeviceId(String deviceId) {
       // this.deviceId = Long.parseLong(deviceId);
        this.deviceId = deviceId;
      /*  String value = String.valueOf(new BigInteger(deviceId, 16).longValue());
        this.deviceId = Long.parseLong(value.substring(1));
*/
    }

    public String getDeviceId() {
        return deviceId;
    }

    public long getDeviceTime() {
        return deviceTime;
    }

    public void setDeviceTime(long deviceTime) {
        this.deviceTime = deviceTime;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setIsOtgEnabled(boolean isOtgEnabled) {
        this.isOtgEnabled = isOtgEnabled;
    }

    public boolean isIsOtgEnabled() {
        return isOtgEnabled;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    @Override
    public String toString() {
        return
                "DeviceDetails{" +
                        "sdk_version_code = '" + sdkVersionCode + '\'' +
                        ",device_id = '" + deviceId + '\'' +
                        ",latitude = '" + latitude + '\'' +
                        ",is_otg_enabled = '" + isOtgEnabled + '\'' +
                        ",sdk_version = '" + sdkVersion + '\'' +
                        ",ip_address = '" + ipAddress + '\'' +
                        ",model_number = '" + modelNumber + '\'' +
                        ",longitude = '" + longitude + '\'' +
                        ",manufacturer = '" + manufacturer + '\'' +
                        "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeviceDetails)) return false;
        DeviceDetails that = (DeviceDetails) o;
        if (getSdkVersionCode() != that.getSdkVersionCode()) return false;
        if (getDeviceId() != that.getDeviceId()) return false;
        if (Double.compare(that.getLatitude(), getLatitude()) != 0) return false;
        if (isOtgEnabled != that.isOtgEnabled) return false;
        if (Double.compare(that.getLongitude(), getLongitude()) != 0) return false;
        if (!getSdkVersion().equals(that.getSdkVersion())) return false;
        if (!getIpAddress().equals(that.getIpAddress())) return false;
        if (!getModelNumber().equals(that.getModelNumber())) return false;
        return getManufacturer().equals(that.getManufacturer());
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getSdkVersionCode();
        result = 31 * result /*+ (int) (getDeviceId() ^ (getDeviceId() >>> 32))*/;
        temp = Double.doubleToLongBits(getLatitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (isOtgEnabled ? 1 : 0);
        result = 31 * result + getSdkVersion().hashCode();
        result = 31 * result + getIpAddress().hashCode();
        result = 31 * result + getModelNumber().hashCode();
        temp = Double.doubleToLongBits(getLongitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + getManufacturer().hashCode();
        return result;
    }

    public String getKernalVersion() {
        return kernalVersion;
    }

    public String getOSVersion() {
        return osVersion;
    }

    public String getManufacturerOSVersion() {
        return manufacturerOSVersion;
    }

    public String getDeviceName() {
        return deviceName;
    }
}