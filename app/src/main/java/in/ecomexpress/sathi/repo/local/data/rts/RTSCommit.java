
package in.ecomexpress.sathi.repo.local.data.rts;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "fe_emp_code",
        "drs_date",
        "rts_delivery_loc_id",
        "consignee_name",
        "consignee_pincode",
        "address_type",
        "location_lat",
        "location_long",
        "attempt_type",
        "shipment_type",
        "trip_id",
        "shipments",
        "is_otp_verified",
        "received_by_name",
        "receiver_phone_no"

})
@JsonIgnoreProperties(ignoreUnknown = true)
public class RTSCommit {

    @JsonProperty("fe_emp_code")
    private String fe_emp_code;

    @JsonProperty("drs_date")
    private String assignDate;

    @JsonProperty("address_type")
    private String addressType;

    @JsonProperty("trip_id")
    private String trip_id;

    @JsonProperty("vendor_name")
    private String vendor_name;

    @JsonProperty("receiver_phone_no")
    private String receiverPhoneNo;

    @JsonProperty("call_attempt_count")
    private int call_attempt_count;
    @JsonProperty("map_activity_count")
    private int map_activity_count;

    @JsonProperty("rts_delivery_loc_id")
    private String consigneeId;
    @JsonProperty("consignee_name")
    private String consigneeName;
    @JsonProperty("consignee_pincode")
    private String consigneePincode;
    @JsonProperty("attempt_type")
    private String deliveryType;
    @JsonProperty("location_lat")
    private String latitude="0.0";
    @JsonProperty("location_long")
    private String longitude="0.0";
    @JsonProperty("drs_type")
    private String drsType;
    @JsonProperty("shipment_type")
    private String shipmentType;
    @JsonProperty("shipments")
    private List<Shipment> shipments = null;
    @JsonProperty("received_by_name")
    private String receivedBy;

    @JsonProperty("image_response")
    private List<ImageData> imageData = new ArrayList<>();

    @JsonProperty("location_verified")
    private boolean location_verified;

    @JsonProperty("ud_otp_verify_status")
    private String ud_otp_verify_status;

    @JsonProperty("rts_flyer")
    private String rts_flyer;

    @JsonProperty("rchd_otp_verify_status")
    private String rd_otp_verify_status;

    @JsonProperty("fe_emp_code")
    public String getFe_emp_code() {
        return fe_emp_code;
    }

    @JsonProperty("fe_emp_code")
    public void setFe_emp_code(String fe_emp_code) {
        this.fe_emp_code = fe_emp_code;
    }

    @JsonProperty("address_type")
    public String getAddressType() {
        return addressType;
    }

    @JsonProperty("address_type")
    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    @JsonProperty("trip_id")
    public String getTrip_id() {
        return trip_id;
    }

    @JsonProperty("trip_id")
    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    @JsonProperty("drs_date")
    public String getAssignDate() {
        return assignDate;
    }

    @JsonProperty("drs_date")
    public void setAssignDate(String assignDate) {
        this.assignDate = assignDate;
    }

    @JsonProperty("rts_delivery_loc_id")
    public String getConsigneeId() {
        return consigneeId;
    }

    @JsonProperty("rts_delivery_loc_id")
    public void setConsigneeId(String consigneeId) {
        this.consigneeId = consigneeId;
    }

    @JsonProperty("consignee_name")
    public String getConsigneeName() {
        return consigneeName;
    }

    @JsonProperty("consignee_name")
    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    @JsonProperty("consignee_pincode")
    public String getConsigneePincode() {
        return consigneePincode;
    }

    @JsonProperty("consignee_pincode")
    public void setConsigneePincode(String consigneePincode) {
        this.consigneePincode = consigneePincode;
    }

    @JsonProperty("attempt_type")
    public String getDeliveryType() {
        return deliveryType;
    }

    @JsonProperty("attempt_type")
    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    @JsonProperty("location_lat")
    public String getLatitude() {
        return latitude;
    }

    @JsonProperty("location_lat")
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @JsonProperty("location_long")
    public String getLongitude() {
        return longitude;
    }

    @JsonProperty("location_long")
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @JsonProperty("drs_type")
    public String getDrsType() {
        return drsType;
    }

    @JsonProperty("drs_type")
    public void setDrsType(String drsType) {
        this.drsType = drsType;
    }

    @JsonProperty("shipment_type")
    public String getShipmentType() {
        return shipmentType;
    }

    @JsonProperty("shipment_type")
    public void setShipmentType(String shipmentType) {
        this.shipmentType = shipmentType;
    }

    @JsonProperty("shipments")
    public List<Shipment> getShipments() {
        return shipments;
    }

    @JsonProperty("shipments")
    public void setShipments(List<Shipment> shipments) {
        this.shipments = shipments;
    }

    @JsonProperty("received_by_name")
    public String getReceivedBy() {
        return receivedBy;
    }

    @JsonProperty("received_by_name")
    public void setReceivedBy(String receivedBy) {
        this.receivedBy = receivedBy;
    }


    @JsonProperty("receiver_phone_no")
    public String getReceiverPhoneNo() {
        return receiverPhoneNo;
    }

    @JsonProperty("receiver_phone_no")
    public void setReceiverPhoneNo(String receiverPhoneNo) {
        this.receiverPhoneNo = receiverPhoneNo;
    }

    public List<ImageData> getImageData() {
        return imageData;
    }

    public void setImageData(List<ImageData> imageData) {
        this.imageData = imageData;
    }

    @JsonProperty("location_verified")
    public void setLocation_verified(boolean location_verified) {
        this.location_verified = location_verified;
    }

    @JsonProperty("location_verified")
    public boolean isLocation_verified() {
        return location_verified;
    }


    public String getVendor_name(){
        return vendor_name;
    }

    public void setVendor_name(String vendor_name){
        this.vendor_name = vendor_name;
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

    public String getUd_otp_verify_status(){
        return ud_otp_verify_status;
    }

    public void setUd_otp_verify_status(String ud_otp_verify_status){
        this.ud_otp_verify_status = ud_otp_verify_status;
    }

    public String getRd_otp_verify_status(){
        return rd_otp_verify_status;
    }

    public void setRd_otp_verify_status(String rd_otp_verify_status){
        this.rd_otp_verify_status = rd_otp_verify_status;
    }

    public String getRts_flyer() {
        return rts_flyer;
    }

    public void setRts_flyer(String rts_flyer) {
        this.rts_flyer = rts_flyer;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "image_key",
            "image_uri"
    })
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageData {

        @JsonProperty("image_key")
        private String imageKey;
        @JsonProperty("image_id")
        private String imageId;

        @JsonProperty("image_type")
        private String imageType;

        public String getImageKey() {
            return imageKey;
        }

        public void setImageKey(String imageKey) {
            this.imageKey = imageKey;
        }

        public String getImageId() {
            return imageId;
        }

        public void setImageId(String imageId) {
            this.imageId = imageId;
        }

        public ImageData() {
        }

        public ImageData(String imageKey, String imageId) {
            this.imageKey = imageKey;
            this.imageId = imageId;
        }

        public String getImageType(){
            return imageType;
        }

        public void setImageType(String imageType){
            this.imageType = imageType;
        }
    }

}
