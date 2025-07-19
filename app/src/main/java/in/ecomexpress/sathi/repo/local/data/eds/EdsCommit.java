package in.ecomexpress.sathi.repo.local.data.eds;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import in.ecomexpress.sathi.repo.local.data.fwd.FeRescheduleInfo;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EdsCommit implements Parcelable {
    @JsonProperty("fe_emp_code")
    private String feEmpCode;
    @JsonProperty("drs_id")
    private String drsId;
    @JsonProperty("location_lat")
    private String locationLat="0.0";
    @JsonProperty("location_long")
    private String locationLong="0.0";
    @JsonProperty("consignee_name")
    private String consigneeName;
    @JsonProperty("attempt_type")
    private String attemptType;
    @JsonProperty("awb")
    private String awb;
    @JsonProperty("fe_comments")
    private String feComments;
    @JsonProperty("status")
    private String status;
    @JsonProperty("assign_date")
    private long assignDate;
    @JsonProperty("attempt_reason_code")
    private String attemptReasonCode;
    @JsonProperty("order_no")
    private String orderNo;

    @JsonProperty("trying_reach")
    private String trying_reach;
    @JsonProperty("techpark")
    private String techpark;

    @JsonProperty("drs_commit_date_time")
    private String drsCommitDateTime;

    @JsonProperty("packaging_barcode")
    private String packageBarcode;
    @Embedded
    @JsonProperty("fe_reschedule_info")
    private FeRescheduleInfo feRescheduleInfo;
    @JsonProperty("trip_id")
    private String tripId;
    @JsonProperty("flag_of_warning")
    private String flag_of_warning;

    @JsonProperty("call_attempt_count")
    private int call_attempt_count;
    @JsonProperty("map_activity_count")
    private int map_activity_count;

    @JsonProperty("ofd_customer_otp")
    private String ofd_customer_otp;

    @JsonProperty("ofd_otp_verified")
    private String ofd_otp_verified;

    @JsonProperty("actual_value")
    private String actual_value;

    @JsonProperty("minimum_amount")
    private String minimum_amount;

    @JsonProperty("ud_otp_verify_status")
    private String ud_otp_verify_status;

    @JsonProperty("rchd_otp_verify_status")
    private String rd_otp_verify_status;
//    @JsonProperty("aadhar_front_image_name")
//    private String aadhar_front_image_name;
//    @JsonProperty("aadhar_rear_image_name")
//    private String aadhar_rear_image_name;
//    @JsonProperty("aadhar_front_image_Id")
//    private String aadhar_front_image_Id;
//    @JsonProperty("aadhar_rear_image_Id")
//    private String aadhar_rear_image_Id;


    @JsonProperty("activity_response")
    private List<EDSActivityResponseWizard> edsActivityResponseWizard = new ArrayList<>();
    @JsonProperty("image_response")
    private List<EDSActivityImageRequest> edsActivityImageRequests = new ArrayList<>();

    @JsonProperty("location_verified")
    private boolean location_verified;

    public long getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(long assignDate) {
        this.assignDate = assignDate;
    }

    public String getFeEmpCode() {
        return feEmpCode;
    }

    public void setFeEmpCode(String feEmpCode) {
        this.feEmpCode = feEmpCode;
    }

    public String getDrsId() {
        return drsId;
    }

    public void setDrsId(String drsId) {
        this.drsId = drsId;
    }

    public String getLocationLat() {
        return locationLat;
    }

    public void setFlag_of_warning(String flag_of_warning) {
        this.flag_of_warning = flag_of_warning;
    }

    public String getFlag_of_warning() {
        return flag_of_warning;
    }

    public void setLocationLat(String locationLat) {
        this.locationLat = locationLat;
    }

    public String getLocationLong() {
        return locationLong;
    }

    public void setLocationLong(String locationLong) {
        this.locationLong = locationLong;
    }

    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public String getAttemptType() {
        return attemptType;
    }

    public void setAttemptType(String attemptType) {
        this.attemptType = attemptType;
    }

    public String getAwb() {
        return awb;
    }

    public void setAwb(String awb) {
        this.awb = awb;
    }

    public String getFeComments() {
        return feComments;
    }

    public void setFeComments(String feComments) {
        this.feComments = feComments;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAttemptReasonCode() {
        return attemptReasonCode;
    }

    public void setAttemptReasonCode(String attemptReasonCode) {
        this.attemptReasonCode = attemptReasonCode;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getDrsCommitDateTime() {
        return drsCommitDateTime;
    }

    public void setDrsCommitDateTime(String drsCommitDateTime) {
        this.drsCommitDateTime = drsCommitDateTime;
    }

    public String getPackageBarcode() {
        return packageBarcode;
    }

    public void setPackageBarcode(String packageBarcode) {
        this.packageBarcode = packageBarcode;
    }

    public FeRescheduleInfo getFeRescheduleInfo() {
        return feRescheduleInfo;
    }

    public void setFeRescheduleInfo(FeRescheduleInfo feRescheduleInfo) {
        this.feRescheduleInfo = feRescheduleInfo;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public List<EDSActivityResponseWizard> getEdsActivityResponseWizard() {
        return edsActivityResponseWizard;
    }

    public void setEdsActivityResponseWizard(List<EDSActivityResponseWizard> edsActivityResponseWizard) {
        this.edsActivityResponseWizard = edsActivityResponseWizard;
    }

    public List<EDSActivityImageRequest> getEdsActivityImageRequests() {
        return edsActivityImageRequests;
    }

    public void setEdsActivityImageRequests(List<EDSActivityImageRequest> edsActivityImageRequests) {
        this.edsActivityImageRequests = edsActivityImageRequests;
    }


    public EdsCommit() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.feEmpCode);
        dest.writeString(this.drsId);
        dest.writeString(this.locationLat);
        dest.writeString(this.locationLong);
        dest.writeString(this.consigneeName);
        dest.writeString(this.attemptType);
        dest.writeString(this.awb);
        dest.writeString(this.feComments);
        dest.writeString(this.status);
        dest.writeLong(this.assignDate);
        dest.writeString(this.attemptReasonCode);
        dest.writeString(this.orderNo);
        dest.writeString(this.drsCommitDateTime);
        dest.writeString(this.packageBarcode);
        dest.writeParcelable(this.feRescheduleInfo, flags);
        dest.writeString(this.tripId);
        dest.writeString(this.flag_of_warning);
        dest.writeTypedList(this.edsActivityResponseWizard);
        dest.writeTypedList(this.edsActivityImageRequests);
        dest.writeString(this.ofd_customer_otp);
        dest.writeString(this.ofd_otp_verified);
        dest.writeString(this.minimum_amount);
        dest.writeString(this.actual_value);
        dest.writeInt(this.call_attempt_count);
        dest.writeInt(this.map_activity_count);
    }

    protected EdsCommit(Parcel in) {
        this.feEmpCode = in.readString();
        this.drsId = in.readString();
        this.locationLat = in.readString();
        this.locationLong = in.readString();
        this.consigneeName = in.readString();
        this.attemptType = in.readString();
        this.awb = in.readString();
        this.feComments = in.readString();
        this.status = in.readString();
        this.assignDate = in.readLong();
        this.attemptReasonCode = in.readString();
        this.orderNo = in.readString();
        this.drsCommitDateTime = in.readString();
        this.packageBarcode = in.readString();
//        this.aadhar_front_image_Id = in.readString();
//        this.aadhar_rear_image_Id = in.readString();
//        this.aadhar_front_image_name = in.readString();
//        this.aadhar_rear_image_name = in.readString();
        this.feRescheduleInfo = in.readParcelable(FeRescheduleInfo.class.getClassLoader());
        this.tripId = in.readString();
        this.edsActivityResponseWizard = in.createTypedArrayList(EDSActivityResponseWizard.CREATOR);
        this.edsActivityImageRequests = in.createTypedArrayList(EDSActivityImageRequest.CREATOR);
        this.flag_of_warning=in.readString();
        this.ofd_customer_otp= in.readString();
        this.ofd_otp_verified = in.readString();
        this.minimum_amount = in.readString();
        this.actual_value = in.readString();
        this.call_attempt_count = in.readInt();
        this.map_activity_count = in.readInt();
    }

    public static final Creator<EdsCommit> CREATOR = new Creator<EdsCommit>() {
        @Override
        public EdsCommit createFromParcel(Parcel source) {
            return new EdsCommit(source);
        }

        @Override
        public EdsCommit[] newArray(int size) {
            return new EdsCommit[size];
        }
    };
    @JsonProperty("location_verified")
    public void setLocation_verified(boolean location_verified) {
        this.location_verified = location_verified;
    }
    @JsonProperty("location_verified")
    public boolean isLocation_verified() {
        return location_verified;
    }

    public String getOfd_customer_otp(){
        return ofd_customer_otp;
    }

    public void setOfd_customer_otp(String ofd_customer_otp){
        this.ofd_customer_otp = ofd_customer_otp;
    }

    public String getOfd_otp_verified(){
        return ofd_otp_verified;
    }

    public void setOfd_otp_verified(String ofd_otp_verified){
        this.ofd_otp_verified = ofd_otp_verified;
    }

    public void setUd_otp(String ud_otp) {
        this.ud_otp_verify_status = ud_otp;
    }

    public String getUd_otp() {
        return ud_otp_verify_status;
    }

    public void setRd_otp(String rd_otp) {
        this.rd_otp_verify_status = rd_otp;
    }

    public String getRd_otp() {
        return rd_otp_verify_status;
    }
    public String getActual_value(){
        return actual_value;
    }

    public void setActual_value(String actual_value){
        this.actual_value = actual_value;
    }

    public String getMinimum_amount(){
        return minimum_amount;
    }

    public void setMinimum_amount(String minimum_amount){
        this.minimum_amount = minimum_amount;
    }

    public int getCall_attempt_count(){
        return call_attempt_count;
    }

    public void setCall_attempt_count(int call_attempt_count){
        this.call_attempt_count = call_attempt_count;
    }

    public int getMap_activity_count(){
        return map_activity_count;
    }

    public void setMap_activity_count(int map_activity_count){
        this.map_activity_count = map_activity_count;
    }

    public String getTrying_reach(){
        return trying_reach;
    }

    public void setTrying_reach(String trying_reach){
        this.trying_reach = trying_reach;
    }

    public String getTechpark(){
        return techpark;
    }

    public void setTechpark(String techpark){
        this.techpark = techpark;
    }

    //    public String getAadhar_front_image_name() {
//        return aadhar_front_image_name;
//    }
//
//    public void setAadhar_front_image_name(String aadhar_front_image_name) {
//        this.aadhar_front_image_name = aadhar_front_image_name;
//    }
//
//    public String getAadhar_rear_image_name() {
//        return aadhar_rear_image_name;
//    }
//
//    public void setAadhar_rear_image_name(String aadhar_rear_image_name) {
//        this.aadhar_rear_image_name = aadhar_rear_image_name;
//    }
//
//    public String getAadhar_front_image_Id() {
//        return aadhar_front_image_Id;
//    }
//
//    public void setAadhar_front_image_Id(String aadhar_front_image_Id) {
//        this.aadhar_front_image_Id = aadhar_front_image_Id;
//    }
//
//    public String getAadhar_rear_image_Id() {
//        return aadhar_rear_image_Id;
//    }
//
//    public void setAadhar_rear_image_Id(String aadhar_rear_image_Id) {
//        this.aadhar_rear_image_Id = aadhar_rear_image_Id;
//    }
}
