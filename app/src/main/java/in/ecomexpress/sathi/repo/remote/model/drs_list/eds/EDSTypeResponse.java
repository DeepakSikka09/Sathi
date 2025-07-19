
package in.ecomexpress.sathi.repo.remote.model.drs_list.eds;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import in.ecomexpress.sathi.utils.GlobalConstant;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "DRSEDSList")
public class EDSTypeResponse {
    @JsonProperty("shipment_type")
    private String shipmentType = GlobalConstant.ShipmentTypeConstants.EDS;
    @JsonProperty("shipment_status")
    private int shipmentStatus = 0;
    @JsonProperty("IsmobileValidationRequired")
    private String ismobileValidationRequired;

    @JsonProperty("cust_code")
    private String custCode;

    @JsonProperty("reason_code")
    private String reasonCode;

    @JsonProperty("consignee_longitude")
    private String consigneeLongitude;

    @JsonProperty("order_no")
    private String orderNo;

    @JsonProperty("item_description")
    private String itemDescription;

    @JsonProperty("assign_date")
    private Integer assignDate;
    @PrimaryKey
    @JsonProperty("awb_no")
    private Long awbNo;

    @JsonProperty("consignee_pincode")
    private Integer consigneePincode;

    @JsonProperty("cust_name")
    private String custName;

    @JsonProperty("drs_no")
    private Integer drsNo;

    @JsonProperty("consignee_address")
    private String consigneeAddress;

    @JsonProperty("consignee_lattitude")
    private String consigneeLattitude;

    @JsonProperty("appointment_slot")
    private String appointmentSlot;
    @Ignore
    @JsonProperty("activity_wizard")
    private List<ActivityWizard> activityWizard = null;

    @JsonProperty("consignee_contact_no")
    private Long consigneeContactNo;

    @JsonProperty("customerPSTN")
    private String customerPSTN;

    @JsonProperty("consignee_name")
    private String consigneeName;

    @JsonProperty("isDigitalPayment")
    private String isDigitalPayment;

    @JsonProperty("IsmobileValidationRequired")
    public String getIsmobileValidationRequired() {
        return ismobileValidationRequired;
    }

    @JsonProperty("IsmobileValidationRequired")
    public void setIsmobileValidationRequired(String ismobileValidationRequired) {
        this.ismobileValidationRequired = ismobileValidationRequired;
    }

    @JsonProperty("cust_code")
    public String getCustCode() {
        return custCode;
    }

    @JsonProperty("cust_code")
    public void setCustCode(String custCode) {
        this.custCode = custCode;
    }

    @JsonProperty("reason_code")
    public String getReasonCode() {
        return reasonCode;
    }

    @JsonProperty("reason_code")
    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    @JsonProperty("consignee_longitude")
    public String getConsigneeLongitude() {
        return consigneeLongitude;
    }

    @JsonProperty("consignee_longitude")
    public void setConsigneeLongitude(String consigneeLongitude) {
        this.consigneeLongitude = consigneeLongitude;
    }

    @JsonProperty("order_no")
    public String getOrderNo() {
        return orderNo;
    }

    @JsonProperty("order_no")
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    @JsonProperty("item_description")
    public String getItemDescription() {
        return itemDescription;
    }

    @JsonProperty("item_description")
    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    @JsonProperty("assign_date")
    public Integer getAssignDate() {
        return assignDate;
    }

    @JsonProperty("assign_date")
    public void setAssignDate(Integer assignDate) {
        this.assignDate = assignDate;
    }

    @JsonProperty("awb_no")
    public Long getAwbNo() {
        return awbNo;
    }

    @JsonProperty("awb_no")
    public void setAwbNo(Long awbNo) {
        this.awbNo = awbNo;
    }

    @JsonProperty("consignee_pincode")
    public Integer getConsigneePincode() {
        return consigneePincode;
    }

    @JsonProperty("consignee_pincode")
    public void setConsigneePincode(Integer consigneePincode) {
        this.consigneePincode = consigneePincode;
    }

    @JsonProperty("cust_name")
    public String getCustName() {
        return custName;
    }

    @JsonProperty("cust_name")
    public void setCustName(String custName) {
        this.custName = custName;
    }

    @JsonProperty("drs_no")
    public Integer getDrsNo() {
        return drsNo;
    }

    @JsonProperty("drs_no")
    public void setDrsNo(Integer drsNo) {
        this.drsNo = drsNo;
    }

    @JsonProperty("consignee_address")
    public String getConsigneeAddress() {
        return consigneeAddress;
    }

    @JsonProperty("consignee_address")
    public void setConsigneeAddress(String consigneeAddress) {
        this.consigneeAddress = consigneeAddress;
    }

    @JsonProperty("consignee_lattitude")
    public String getConsigneeLattitude() {
        return consigneeLattitude;
    }

    @JsonProperty("consignee_lattitude")
    public void setConsigneeLattitude(String consigneeLattitude) {
        this.consigneeLattitude = consigneeLattitude;
    }

    @JsonProperty("appointment_slot")
    public String getAppointmentSlot() {
        return appointmentSlot;
    }

    @JsonProperty("appointment_slot")
    public void setAppointmentSlot(String appointmentSlot) {
        this.appointmentSlot = appointmentSlot;
    }

    @JsonProperty("activity_wizard")
    public List<ActivityWizard> getActivityWizard() {
        return activityWizard;
    }

    @JsonProperty("activity_wizard")
    public void setActivityWizard(List<ActivityWizard> activityWizard) {
        this.activityWizard = activityWizard;
    }

    @JsonProperty("consignee_contact_no")
    public Long getConsigneeContactNo() {
        return consigneeContactNo;
    }

    @JsonProperty("consignee_contact_no")
    public void setConsigneeContactNo(Long consigneeContactNo) {
        this.consigneeContactNo = consigneeContactNo;
    }

    @JsonProperty("customerPSTN")
    public String getCustomerPSTN() {
        return customerPSTN;
    }

    @JsonProperty("customerPSTN")
    public void setCustomerPSTN(String customerPSTN) {
        this.customerPSTN = customerPSTN;
    }

    @JsonProperty("consignee_name")
    public String getConsigneeName() {
        return consigneeName;
    }

    @JsonProperty("consignee_name")
    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    @JsonProperty("isDigitalPayment")
    public String getIsDigitalPayment() {
        return isDigitalPayment;
    }

    @JsonProperty("isDigitalPayment")
    public void setIsDigitalPayment(String isDigitalPayment) {
        this.isDigitalPayment = isDigitalPayment;
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

    @Override
    public String toString() {
        return "EDSTypeResponse{" +
                "shipmentType='" + shipmentType + '\'' +
                ", shipmentStatus=" + shipmentStatus +
                ", ismobileValidationRequired='" + ismobileValidationRequired + '\'' +
                ", custCode='" + custCode + '\'' +
                ", reasonCode='" + reasonCode + '\'' +
                ", consigneeLongitude='" + consigneeLongitude + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", itemDescription='" + itemDescription + '\'' +
                ", assignDate=" + assignDate +
                ", awbNo=" + awbNo +
                ", consigneePincode=" + consigneePincode +
                ", custName='" + custName + '\'' +
                ", drsNo=" + drsNo +
                ", consigneeAddress='" + consigneeAddress + '\'' +
                ", consigneeLattitude='" + consigneeLattitude + '\'' +
                ", appointmentSlot='" + appointmentSlot + '\'' +
                ", activityWizard=" + activityWizard +
                ", consigneeContactNo=" + consigneeContactNo +
                ", customerPSTN='" + customerPSTN + '\'' +
                ", consigneeName='" + consigneeName + '\'' +
                ", isDigitalPayment='" + isDigitalPayment + '\'' +
                '}';
    }
}
