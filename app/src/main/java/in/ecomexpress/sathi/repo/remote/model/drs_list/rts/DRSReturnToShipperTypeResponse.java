package in.ecomexpress.sathi.repo.remote.model.drs_list.rts;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import in.ecomexpress.sathi.repo.remote.model.drs_list.common.CInterface;
import in.ecomexpress.sathi.repo.remote.model.drs_list.common.Location;
import in.ecomexpress.sathi.utils.GlobalConstant;
import io.reactivex.annotations.NonNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "DRSRTSList")
public class DRSReturnToShipperTypeResponse implements CInterface{
    @PrimaryKey
    @NonNull
    @JsonProperty("awb_no")
    private long awbNo;

    @JsonProperty("isCallattempted")
    public int getIsCallattempted() {
        return isCallattempted;
    }

    @JsonProperty("isCallattempted")
    public void setIsCallattempted(int isCallattempted) {
        this.isCallattempted = isCallattempted;
    }

    @JsonProperty("isCallattempted")
    private int isCallattempted = 0;

    @JsonProperty("shipment_type")
    private String shipmentType = GlobalConstant.ShipmentTypeConstants.RTS;

    @JsonProperty("shipment_status")
    private int shipmentStatus = 0;

    @Embedded
    @JsonProperty("consignee_details")
    private RtsConsigneeDetails consigneeDetails;

    @Embedded
    @JsonProperty("vendor_details")
    private RtsVendorDetails vendorDetails;

    @Embedded
    @JsonProperty("shipment_details")
    private RtsShipmentDetails shipmentDetails;

    @Embedded
    @JsonProperty("flags")
    private RtsFlags flags;

    @Embedded
    @JsonProperty("location")
    private Location location;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("drs_no")
    private String drsNo;

    @Ignore
    double distance;

    @JsonProperty("assigned_date")
    private Long assignedDate;

    @JsonProperty("delivery_slot")
    private String deliverySlot;

    public String getDeliverySlot() {
        return deliverySlot;
    }

    public void setDeliverySlot(String deliverySlot) {
        this.deliverySlot = deliverySlot;
    }

    @JsonProperty("consignee_details")
    public RtsConsigneeDetails getConsigneeDetails() {
        return consigneeDetails;
    }

    @JsonProperty("consignee_details")
    public void setConsigneeDetails(RtsConsigneeDetails consigneeDetails) {
        this.consigneeDetails = consigneeDetails;
    }

    @JsonProperty("vendor_details")
    public RtsVendorDetails getVendorDetails() {
        return vendorDetails;
    }

    @JsonProperty("vendor_details")
    public void setVendorDetails(RtsVendorDetails vendorDetails) {
        this.vendorDetails = vendorDetails;
    }

    @JsonProperty("shipment_details")
    public RtsShipmentDetails getShipmentDetails() {
        return shipmentDetails;
    }

    @JsonProperty("shipment_details")
    public void setShipmentDetails(RtsShipmentDetails shipmentDetails) {
        this.shipmentDetails = shipmentDetails;
    }

    @JsonProperty("flags")
    public RtsFlags getFlags() {
        return flags;
    }

    @JsonProperty("flags")
    public void setFlags(RtsFlags flags) {
        this.flags = flags;
    }

    @JsonProperty("location")
    public Location getLocation() {
        return location;
    }

    @JsonProperty("location")
    public void setLocation(Location location) {
        this.location = location;
    }

    @JsonProperty("remarks")
    public String getRemarks() {
        return remarks;
    }

    @JsonProperty("remarks")
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @JsonProperty("drs_no")
    public String getDrsNo() {
        return drsNo;
    }

    @JsonProperty("drs_no")
    public void setDrsNo(String drsNo) {
        this.drsNo = drsNo;
    }

    @JsonProperty("assigned_date")
    public Long getAssignedDate() {
        return assignedDate;
    }

    @JsonProperty("assigned_date")
    public void setAssignedDate(Long assignedDate) {
        this.assignedDate = assignedDate;
    }

    public String getShipmentType() {
        return shipmentType;
    }

    public void setShipmentType(String shipmentType) {
        this.shipmentType = shipmentType;
    }

    public int getShipmentStatus() {
        return shipmentStatus;
    }

    public void setShipmentStatus(int shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }

    @JsonProperty("awb_no")
    public Long getAwbNo() {
        return awbNo;
    }

    @JsonProperty("awb_no")
    public void setAwbNo(long awbNo) {
        this.awbNo = awbNo;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @androidx.annotation.NonNull
    @Override
    public String toString(){
        return "DRSReturnToShipperTypeResponse{" + "awbNo=" + awbNo + ", shipmentType='" + shipmentType + '\'' + ", shipmentStatus='" + shipmentStatus + '\'' + ", consigneeDetails=" + consigneeDetails + ", vendorDetails=" + vendorDetails + ", shipmentDetails=" + shipmentDetails + ", flags=" + flags + ", location=" + location + ", remarks='" + remarks + '\'' + ", drsNo='" + drsNo + '\'' + ", assignedDate=" + assignedDate + ", deliverySlot='" + deliverySlot + '\'' + '}';
    }
}
