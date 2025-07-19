package in.ecomexpress.sathi.repo.remote.model.login;

import androidx.annotation.NonNull;
import androidx.room.Ignore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponse {

    @JsonProperty("status")
    private boolean status;

    @JsonProperty("response")
    private SResponse sResponse;

    @JsonProperty("status")
    public boolean isStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(boolean status) {
        this.status = status;
    }

    @JsonProperty("response")
    public SResponse getSResponse() {
        return sResponse;
    }

    @JsonProperty("response")
    public void setResponse(SResponse response) {
        this.sResponse = response;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class SResponse {

        @JsonProperty("status-code")
        private long statusCode;

        @JsonProperty("description")
        private String description;

        @JsonProperty("auth_token")
        private String authToken;

        @JsonProperty("service_center")
        private String serviceCenter;

        @JsonProperty("aadhaar_consent")
        private String aadhaar_consent;

        @JsonProperty("is_covid_consent_captured")
        private boolean is_covid_consent_captured;
        @JsonProperty("covid_image_url")
        private String covid_image_url;

        @JsonProperty("ecom_dlv_region")
        private String ecom_dlv_region;

        @JsonProperty("name")
        private String name;

        @JsonProperty("designation")
        private String designation;

        @JsonProperty("mobile")
        private String mobile;

        @JsonProperty("location_type")
        private long locationType;

        @JsonProperty("location_code")
        private String locationCode;

        @JsonProperty("code")
        private String code;

        @JsonProperty("is_user_validated")
        private boolean isUserValidated;

        @JsonProperty("photo_url")
        private String photoUrl;

        public AdmPriceData getAdm_price_data() {
            return adm_price_data;
        }

        public void setAdm_price_data(AdmPriceData adm_price_data) {
            this.adm_price_data = adm_price_data;
        }

        @JsonProperty("adm_price_data")
        private AdmPriceData adm_price_data;

        @JsonProperty("is_adm_emp")
        public boolean getIs_adm_emp() {
            return is_adm_emp;
        }

        @JsonProperty("is_adm_emp")
        public void setIs_adm_emp(boolean is_adm_emp) {
            this.is_adm_emp = is_adm_emp;
        }

        @Ignore
        @JsonProperty("is_adm_emp")
        private boolean is_adm_emp;

        @JsonProperty("is_dc_update_allowed_for_dept")
        public boolean isIs_dc_update_allowed_for_dept() {
            return is_dc_update_allowed_for_dept;
        }

        @JsonProperty("is_dc_update_allowed_for_dept")
        public void setIs_dc_update_allowed_for_dept(boolean is_dc_update_allowed_for_dept) {
            this.is_dc_update_allowed_for_dept = is_dc_update_allowed_for_dept;
        }

        @JsonProperty("is_dc_update_allowed_for_dept")
        private boolean is_dc_update_allowed_for_dept;

        @JsonProperty("is_manual_otp_required")
        private boolean isManualOtpRequired;

        @JsonProperty("is_otp_required")
        private boolean isOtpRequired;

        @JsonProperty("is_blocked_user")
        private boolean isBlockedUser;

        @JsonProperty("is_password_reset_required")
        private boolean isPasswordResetRequired;

        @JsonProperty("server_timestamp")
        private String server_timestamp;

        @JsonProperty("apk_update_response")
        private APKUpdateResponse apkUpdateResponse = new APKUpdateResponse();

        @JsonProperty("start_trip")
        private StartTripBackup startTripBackup = new StartTripBackup();

        public StartTripBackup getStartTripBackup() {
            return startTripBackup;
        }

        public void setStartTripBackup(StartTripBackup startTripBackup) {
            this.startTripBackup = startTripBackup;
        }
        public boolean isManualOtpRequired() {
            return isManualOtpRequired;
        }

        public void setManualOtpRequired(boolean manualOtpRequired) {
            isManualOtpRequired = manualOtpRequired;
        }

        @JsonProperty("dc_location_address")
        private DcLocationAddress dcLocationAddress;

        @JsonProperty("api_urls")
        private APIUrls apiUrls;

        @JsonProperty("is_otp_required")
        public boolean isOtpRequired() {
            return isOtpRequired;
        }

        @JsonProperty("is_otp_required")
        public void setOtpRequired(boolean otpRequired) {
            isOtpRequired = otpRequired;
        }

        @JsonProperty("dc_location_address")
        public DcLocationAddress getDcLocationAddress() {
            return dcLocationAddress;
        }

        @JsonProperty("dc_location_address")
        public void setDcLocationAddress(DcLocationAddress dcLocationAddress) {
            this.dcLocationAddress = dcLocationAddress;
        }

        @JsonProperty("api_urls")
        public APIUrls getApiUrls() {
            return apiUrls;
        }

        @JsonProperty("api_urls")
        public void setApiUrls(APIUrls apiUrls) {
            this.apiUrls = apiUrls;
        }

        @JsonProperty("status-code")
        public long getStatusCode() {
            return statusCode;
        }

        @JsonProperty("status-code")
        public void setStatusCode(long statusCode) {
            this.statusCode = statusCode;
        }

        @JsonProperty("description")
        public String getDescription() {
            return description;
        }

        @JsonProperty("description")
        public void setDescription(String description) {
            this.description = description;
        }

        @JsonProperty("auth_token")
        public String getAuthToken() {
            return authToken;
        }

        @JsonProperty("auth_token")
        public void setAuthToken(String authToken) {
            this.authToken = authToken;
        }

        @JsonProperty("service_center")
        public String getServiceCenter() {
            return serviceCenter;
        }

        @JsonProperty("service_center")
        public void setServiceCenter(String serviceCenter) {
            this.serviceCenter = serviceCenter;
        }

        @JsonProperty("name")
        public String getName() {
            return name;
        }

        @JsonProperty("name")
        public void setName(String name) {
            this.name = name;
        }

        @JsonProperty("designation")
        public String getDesignation() {
            return designation;
        }

        @JsonProperty("designation")
        public void setDesignation(String designation) {
            this.designation = designation;
        }

        @JsonProperty("mobile")
        public String getMobile() {
            return mobile;
        }

        @JsonProperty("mobile")
        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        @JsonProperty("location_type")
        public long getLocationType() {
            return locationType;
        }

        @JsonProperty("location_type")
        public void setLocationType(long locationType) {
            this.locationType = locationType;
        }

        @JsonProperty("location_code")
        public String getLocationCode() {
            return locationCode;
        }

        @JsonProperty("location_code")
        public void setLocationCode(String locationCode) {
            this.locationCode = locationCode;
        }

        @JsonProperty("code")
        public String getCode() {
            return code;
        }

        @JsonProperty("code")
        public void setCode(String code) {
            this.code = code;
        }

        @JsonProperty("is_user_validated")
        public boolean isIsUserValidated() {
            return isUserValidated;
        }

        @JsonProperty("is_user_validated")
        public void setIsUserValidated(boolean isUserValidated) {
            this.isUserValidated = isUserValidated;
        }

        @JsonProperty("photo_url")
        public String getPhotoUrl() {
            return photoUrl;
        }

        @JsonProperty("photo_url")
        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }

        @JsonProperty("is_blocked_user")
        public boolean isIsBlockedUser() {
            return isBlockedUser;
        }

        @JsonProperty("is_blocked_user")
        public void setIsBlockedUser(boolean isBlockedUser) {
            this.isBlockedUser = isBlockedUser;
        }

        @JsonProperty("is_password_reset_required")
        public boolean isPasswordResetRequired() {
            return isPasswordResetRequired;
        }

        @JsonProperty("is_password_reset_required")
        public void setPasswordResetRequired(boolean isPasswordResetRequired) {
            this.isPasswordResetRequired = isPasswordResetRequired;
        }

        @JsonProperty("apk_update_response")
        public APKUpdateResponse getApkUpdateResponse() {
            return apkUpdateResponse;
        }

        @JsonProperty("apk_update_response")
        public void setApkUpdateResponse(APKUpdateResponse apkUpdateResponse) {
            this.apkUpdateResponse = apkUpdateResponse;
        }

        public String getAadhaar_consent() {
            return aadhaar_consent;
        }

        public void setAadhaar_consent(String aadhaar_consent) {
            this.aadhaar_consent = aadhaar_consent;
        }

        public String getCovid_image_url() {
            return covid_image_url;
        }

        public void setCovid_image_url(String covid_image_url) {
            this.covid_image_url = covid_image_url;
        }

        public String getEcom_dlv_region(){
            return ecom_dlv_region;
        }

        public void setEcom_dlv_region(String ecom_dlv_region){
            this.ecom_dlv_region = ecom_dlv_region;
        }

        public boolean isIs_covid_consent_captured() {
            return is_covid_consent_captured;
        }

        public void setIs_covid_consent_captured(boolean is_covid_consent_captured) {
            this.is_covid_consent_captured = is_covid_consent_captured;
        }

        public String getServer_timestamp() {
            return server_timestamp;
        }

        public void setServer_timestamp(String server_timestamp) {
            this.server_timestamp = server_timestamp;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "LoginResponse{" +
                "status=" + status +
                ", sResponse=" + sResponse +
                '}';
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class APKUpdateResponse {

        @JsonProperty("version_status")
        private int version_status;

        @JsonProperty("apk_version")
        private String apk_version;

        @JsonProperty("apk_url")
        private String apk_url;

        @JsonProperty("apk_version_message")
        private String apk_version_message;

        @JsonProperty("version_status")
        public int getVersion_status() {
            return version_status;
        }

        @JsonProperty("version_status")
        public void setVersion_status(int version_status) {
            this.version_status = version_status;
        }

        @JsonProperty("apk_version")
        public String getApk_version() {
            return apk_version;
        }

        @JsonProperty("apk_version")
        public void setApk_version(String apk_version) {
            this.apk_version = apk_version;
        }

        @JsonProperty("apk_url")
        public String getApk_url() {
            return apk_url;
        }

        @JsonProperty("apk_url")
        public void setApk_url(String apk_url) {
            this.apk_url = apk_url;
        }

        @JsonProperty("apk_version_message")
        public String getApk_version_message() {
            return apk_version_message;
        }

        @JsonProperty("apk_version_message")
        public void setApk_version_message(String apk_version_message) {
            this.apk_version_message = apk_version_message;
        }

        @NonNull
        @Override
        public String toString() {
            return "version_status: " + version_status + ", apk_version: " + apk_version + ", apk_url: " + apk_url + ", apk_version_message: " + apk_version_message;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class APIUrls {

        @JsonProperty("base_url")
        private String baseUrl;

        @JsonProperty("base_url")
        public String getBaseUrl() {
            return baseUrl;
        }

        @JsonProperty("base_url")
        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        @JsonProperty("api")
        public HashMap<String,String> live_api_url;

        public HashMap<String, String> getLive_api_url() {
            return live_api_url;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StartTripBackup {

        @Ignore
        @JsonProperty("vehicle_type")
        private String vehicleType = "";

        @Ignore
        @JsonProperty("type_of_vehicle")
        private String typeOfVehicle = "";

        @JsonProperty("trip_id")
        private int tripId;

        @JsonProperty("live_tracking_trip_id")
        private String live_tracking_trip_id;

        @JsonProperty("route_name")
        private String routeName;

        @JsonProperty("meter_reading")
        private long meterReading;

        @Ignore
        @JsonProperty("previous_trip_status")
        private Boolean previousTripStatus;

        @JsonProperty("end_trip_time")
        private long end_trip_time;

        @JsonProperty("end_trip_km")
        private long end_trip_km;

        @JsonProperty("current_trip_amount")
        private long current_trip_amount;

        public long getCurrent_trip_amount() {
            return current_trip_amount;
        }

        public void setCurrent_trip_amount(long current_trip_amount) {
            this.current_trip_amount = current_trip_amount;
        }

        public String getVehicleType() {
            return vehicleType;
        }

        public void setVehicleType(String vehicleType) {
            this.vehicleType = vehicleType;
        }

        public String getTypeOfVehicle() {
            return typeOfVehicle;
        }

        public void setTypeOfVehicle(String typeOfVehicle) {
            this.typeOfVehicle = typeOfVehicle;
        }

        public int getTripId() {
            return tripId;
        }

        public void setTripId(int tripId) {
            this.tripId = tripId;
        }

        public void setPreviousTripStatus(Boolean previousTripStatus){this.previousTripStatus=previousTripStatus;}

        public Boolean getPreviousTripStatus(){return previousTripStatus;}

        public String getRouteName() {
            return routeName;
        }

        public void setRouteName(String routeName) {
            this.routeName = routeName;
        }

        public long getMeterReading() {
            return meterReading;
        }

        public void setMeterReading(long meterReading) {
            this.meterReading = meterReading;
        }

        public long getEnd_trip_time() {
            return end_trip_time;
        }

        public void setEnd_trip_time(long end_trip_time) {
            this.end_trip_time = end_trip_time;
        }

        public long getEnd_trip_km() {
            return end_trip_km;
        }

        public void setEnd_trip_km(long end_trip_km) {
            this.end_trip_km = end_trip_km;
        }

        public String getLive_tracking_trip_id() {
            return live_tracking_trip_id;
        }

        public void setLive_tracking_trip_id(String live_tracking_trip_id) {
            this.live_tracking_trip_id = live_tracking_trip_id;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DcLocationAddress {

        @JsonProperty("line1")
        private String line1;

        @JsonProperty("line2")
        private String line2;

        @JsonProperty("line3")
        private String line3;

        @JsonProperty("line4")
        private String line4;

        @JsonProperty("pincode")
        private String pincode;

        @JsonProperty("city")
        private String city;

        @JsonProperty("state")
        private String state;

        @JsonProperty("location_lat")
        private double locationLat;

        @JsonProperty("location_long")
        private double locationLong;

        @JsonProperty("line1")
        public String getLine1() {
            return line1;
        }

        @JsonProperty("line1")
        public void setLine1(String line1) {
            this.line1 = line1;
        }

        @JsonProperty("line2")
        public String getLine2() {
            return line2;
        }

        @JsonProperty("line2")
        public void setLine2(String line2) {
            this.line2 = line2;
        }

        @JsonProperty("line3")
        public String getLine3() {
            return line3;
        }

        @JsonProperty("line3")
        public void setLine3(String line3) {
            this.line3 = line3;
        }

        @JsonProperty("line4")
        public String getLine4() {
            return line4;
        }

        @JsonProperty("line4")
        public void setLine4(String line4) {
            this.line4 = line4;
        }

        @JsonProperty("pincode")
        public String getPincode() {
            return pincode;
        }

        @JsonProperty("pincode")
        public void setPincode(String pincode) {
            this.pincode = pincode;
        }

        @JsonProperty("city")
        public String getCity() {
            return city;
        }

        @JsonProperty("city")
        public void setCity(String city) {
            this.city = city;
        }

        @JsonProperty("state")
        public String getState() {
            return state;
        }

        @JsonProperty("state")
        public void setState(String state) {
            this.state = state;
        }

        @JsonProperty("location_lat")
        public double
        getLocationLat() {
            return locationLat;
        }

        @JsonProperty("location_lat")
        public void setLocationLat(double locationLat) {
            this.locationLat = locationLat;
        }

        @JsonProperty("location_long")
        public double getLocationLong() {
            return locationLong;
        }

        @JsonProperty("location_long")
        public void setLocationLong(double locationLong) {
            this.locationLong = locationLong;
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AdmPriceData {

        @JsonProperty("RTS")
        private float RTS;

        @JsonProperty("PPD")
        private float PPD;

        @JsonProperty("RQC")
        private float RQC;

        @JsonProperty("EDS")
        private float EDS;

        @JsonProperty("COD")
        private float COD;

        @JsonProperty("RVP")
        private float RVP;

        @JsonProperty("RTS")
        public float getRTSPrice ()
        {
            return RTS;
        }

        @JsonProperty("RTS")
        public void setRTSPrice (float RTS)
        {
            this.RTS = RTS;
        }

        @JsonProperty("EDS")
        public float getEDSPrice ()
        {
            return EDS;
        }

        @JsonProperty("EDS")
        public void setEDSPrice (float EDS)
        {
            this.EDS = EDS;
        }

        @JsonProperty("RVP")
        public float getRVPPrice ()
        {
            return RVP;
        }

        @JsonProperty("RVP")
        public void setRVPPrice (float RVP)
        {
            this.RVP = RVP;
        }

        @JsonProperty("PPD")
        public float getPPDPrice() {
            return PPD;
        }

        @JsonProperty("PPD")
        public void setPPDPrice(float PPD) {
            this.PPD = PPD;
        }

        @JsonProperty("RQC")
        public float getRQCPrice() {
            return RQC;
        }

        @JsonProperty("RQC")
        public void setRQCPrice(float RQC) {
            this.RQC = RQC;
        }

        @JsonProperty("COD")
        public float getCODPrice() {
            return COD;
        }

        @JsonProperty("COD")
        public void setCODPrice(float COD) {
            this.COD = COD;
        }

        @NonNull
        @Override
        public String toString() {
            return "AdmPriceData [RTSPrice = "+RTS+", PPDPrice = "+PPD+", RQCPrice = "+RQC+", EDSPrice = "+EDS+", CODPrice = "+COD+", RVPPrice = "+RVP+"]";
        }
    }
}