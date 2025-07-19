package in.ecomexpress.sathi.repo.local.data.rts;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.ecomexpress.sathi.repo.remote.model.rts.ImageResponse;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
        "drs_date",
        "drs_id",
        "awb",
        "order_no",
        "parent_awb_no",
        "status",
        "fe_comments",
        "attempt_reason_code",
        "drs_commit_date_time"
})
public class Shipment {

    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();
    @JsonProperty("drs_date")
    private String assignDate;
    @JsonProperty("drs_id")
    private String drsNo;
    @JsonProperty("awb")
    private String awbNo;
    @JsonProperty("order_no")
    private String orderNo;
    @JsonProperty("parent_awb_no")
    private String parentAwbNo;
    @JsonProperty("status")
    private String status;
    @JsonProperty("fe_comments")
    private String remarks;
    @JsonProperty("attempt_reason_code")
    private String reason;
    @JsonProperty("drs_commit_date_time")
    private String dateTime;
    @JsonProperty("dssl_flag")
    private String DS_SL = "false";
    @JsonProperty("return_package_barcode")
    private String return_package_barcode;
    @JsonProperty("reason_id")
    private int reason_id;
    @JsonProperty("rts_shipment_images")
    private ArrayList<ImageResponse> rts_shipment_images = new ArrayList<>();
    @JsonProperty("is_otp_verified")
    private String is_otp_verified;
    @JsonProperty("ofd_otp_verify_status")
    private String ofd_otp_verify_status;
    @JsonProperty("rchd_otp_verify_status")
    private String rchd_otp_verify_status;
    @JsonProperty("ud_otp_verify_status")
    private String ud_otp_verify_status;
    @JsonProperty("scanned_method")
    private String scanned_method;

    @JsonProperty("drs_date")
    public String getAssignDate() {
        return assignDate;
    }

    @JsonProperty("drs_date")
    public void setAssignDate(String assignDate) {
        this.assignDate = assignDate;
    }

    @JsonProperty("drs_id")
    public String getDrsNo() {
        return drsNo;
    }

    @JsonProperty("drs_id")
    public void setDrsNo(String drsNo) {
        this.drsNo = drsNo;
    }

    @JsonProperty("awb")
    public String getAwbNo() {
        return awbNo;
    }

    @JsonProperty("awb")
    public void setAwbNo(String awbNo) {
        this.awbNo = awbNo;
    }

    @JsonProperty("order_no")
    public String getOrderNo() {
        return orderNo;
    }

    @JsonProperty("order_no")
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    @JsonProperty("parent_awb_no")
    public String getParentAwbNo() {
        return parentAwbNo;
    }

    @JsonProperty("parent_awb")
    public void setParentAwbNo(String parentAwbNo) {
        this.parentAwbNo = parentAwbNo;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("fe_comments")
    public String getRemarks() {
        return remarks;
    }

    @JsonProperty("fe_comments")
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @JsonProperty("attempt_reason_code")
    public String getReason() {
        return reason;
    }

    @JsonProperty("attempt_reason_code")
    public void setReason(String reason) {
        this.reason = reason;
    }

    @JsonProperty("drs_commit_date_time")
    public String getDateTime() {
        return dateTime;
    }

    @JsonProperty("drs_commit_date_time")
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public String isIs_otp_verified() {
        return is_otp_verified;
    }

    public void setIs_otp_verified(String is_otp_verified) {
        this.is_otp_verified = is_otp_verified;
    }

    public String getOfd_otp_verify_status() {
        return ofd_otp_verify_status;
    }

    public void SetOfd_otp_verify_status(String ofd_otp_verify_status) {
        this.ofd_otp_verify_status = ofd_otp_verify_status;
    }

    public String getRchd_otp_verify_status() {
        return rchd_otp_verify_status;
    }

    public void setRchd_otp_verify_status(String rchd_otp_verify_status) {
        this.rchd_otp_verify_status = rchd_otp_verify_status;
    }

    public String getDS_SL() {
        return DS_SL;
    }

    public void setDS_SL(String DS_SL) {
        this.DS_SL = DS_SL;
    }

    public int getReason_id() {
        return reason_id;
    }

    public void setReason_id(int reason_id) {
        this.reason_id = reason_id;
    }

    public String getUd_otp_verify_status() {
        return ud_otp_verify_status;
    }

    public void setUd_otp_verify_status(String ud_otp_verify_status) {
        this.ud_otp_verify_status = ud_otp_verify_status;
    }

    public String getReturn_package_barcode() {
        return return_package_barcode;
    }

    public void setReturn_package_barcode(String return_package_barcode) {
        this.return_package_barcode = return_package_barcode;
    }

    public String getScanned_method() {
        return scanned_method;
    }

    public void setScanned_method(String scanned_method) {
        this.scanned_method = scanned_method;
    }

    public ArrayList<ImageResponse> getRts_shipment_images() {
        return rts_shipment_images;
    }

    public void setRts_shipment_images(ArrayList<ImageResponse> rts_shipment_images) {
        this.rts_shipment_images = rts_shipment_images;
    }
}
