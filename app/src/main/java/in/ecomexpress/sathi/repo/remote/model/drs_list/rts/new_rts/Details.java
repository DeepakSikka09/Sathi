package in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.reactivex.annotations.NonNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "vw_detail")
public class Details {

    @NonNull
    @PrimaryKey
    @JsonProperty("id")
    private long id;

    @JsonIgnore
    private int shipmentSyncStatus;

    @JsonProperty("name")
    private String name;

    @JsonProperty("latching_required")
    private String latching_required;

    @JsonProperty("image_required")
    private String image_required;

    @JsonProperty("scan_deliver")
    private String scan_deliver;

    @Ignore
    private int delivered;
    
    @Ignore
    private int undelivered;

    @Ignore
    private int dispute_delivery;
    
    @Ignore
    private int mnnuallyDelivered;

    @Ignore
    private int totalShipmentCount;

    @JsonProperty("address")
    @Embedded
    public RTSAddress rtsAddress;

    private boolean isVendor;

    @JsonProperty("is_otp_required")
    private boolean is_otp_required;

    @JsonProperty("rts_skip_otp")
    private boolean rts_skip_otp = true;

    @JsonProperty("RCHD_enabled")
    private boolean RCHD_enabled;

    @JsonProperty("sub_shipper_mobile")
    private String sub_shipper_mobile;

    @JsonProperty("sub_shipper_email")
    private String sub_shipper_email;

    @JsonProperty("shipper_id")
    private String shipper_id;

    @JsonProperty("shipmentStatus")
    private int shipmentStatus;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("location")
    @Embedded
    private Location location;

    @Embedded
    @JsonProperty("flags")
    private Flags flags;

    @JsonProperty("assigned_date")
    private long assignedDate;

    @JsonProperty("status")
    private String status;

    @JsonIgnore
    private int missedCallCounter;

    public int getShipmentSyncStatus() {
        return shipmentSyncStatus;
    }

    public void setShipmentSyncStatus(int shipmentSyncStatus) {
        this.shipmentSyncStatus = shipmentSyncStatus;
    }

    @JsonProperty("id")
    public long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(long id) {
        this.id = id;
    }

    @JsonProperty("address")
    public RTSAddress getRtsAddress() {
        return rtsAddress;
    }

    @JsonProperty("address")
    public void setRtsAddress(RTSAddress rtsAddress) {
        this.rtsAddress = rtsAddress;
    }

    public int getMissedCallCounter() {
        return missedCallCounter;
    }

    public void setMissedCallCounter(int missedCallCounter) {
        this.missedCallCounter = missedCallCounter;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public String getCallbridgePstn() {
        return callbridgePstn;
    }

    public void setCallbridgePstn(String callbridgePstn) {
        this.callbridgePstn = callbridgePstn;
    }

    public String getCallbridge_Api() {
        return callbridge_Api;
    }

    public void setCallbridge_Api(String callbridge_Api) {
        this.callbridge_Api = callbridge_Api;
    }

    @JsonProperty("callbridge_pstn")
    private String callbridgePstn;

    @JsonProperty("callbridge_api")
    private String callbridge_Api;

    @JsonProperty("remarks")
    public String getRemarks() {
        return remarks;
    }

    @JsonProperty("remarks")
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @JsonProperty("flags")
    public Flags getFlags() {
        return flags;
    }

    @JsonProperty("flags")
    public void setFlags(Flags flags) {
        this.flags = flags;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @JsonProperty("assigned_date")
    public long getAssignedDate() {
        return assignedDate;
    }

    @JsonProperty("assigned_date")
    public void setAssignedDate(long assignedDate) {
        this.assignedDate = assignedDate;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("shipmentStatus")
    public int getShipmentStatus() {
        return shipmentStatus;
    }

    @JsonProperty("shipmentStatus")
    public void setShipmentStatus(int shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }

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

    public boolean isVendor() {
        return isVendor;
    }

    public void setVendor(boolean vendor) {
        isVendor = vendor;
    }

    public int getDelivered() {
        return delivered;
    }

    public void setDelivered(int delivered) {
        this.delivered = delivered;
    }

    public int getUndelivered() {
        return undelivered;
    }

    public void setUndelivered(int undelivered) {
        this.undelivered = undelivered;
    }

    public int getMnnuallyDelivered() {
        return mnnuallyDelivered;
    }

    public void setMnnuallyDelivered(int mnnuallyDelivered) {
        this.mnnuallyDelivered = mnnuallyDelivered;
    }

    public int getTotalShipmentCount() {
        return totalShipmentCount;
    }

    public void setTotalShipmentCount(int totalShipmentCount) {
        this.totalShipmentCount = totalShipmentCount;
    }

    @androidx.annotation.NonNull
    @Override
    public String toString() {
        return "Details{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", delivered=" + delivered +
                ", undelivered=" + undelivered +
                ", mnnuallyDelivered=" + mnnuallyDelivered +
                ", totalShipmentCount=" + totalShipmentCount +
                ", rtsAddress=" + rtsAddress +
                ", isVendor=" + (isVendor ? "Vendor" : "Warehouse") +
                ", shipmentStatus=" + shipmentStatus +
                ", remarks='" + remarks + '\'' +
                ", location=" + location +
                ", flags=" + flags +
                ", assignedDate=" + assignedDate +
                ", status='" + status + '\'' +
                ", missedCallCounter=" + missedCallCounter +
                ", callbridgePstn='" + callbridgePstn + '\'' +
                ", callbridge_Api='" + callbridge_Api + '\'' +
                ", isCallattempted=" + isCallattempted +
                '}';
    }

    public boolean isIs_otp_required(){
        return is_otp_required;
    }

    public void setIs_otp_required(boolean is_otp_required){
        this.is_otp_required = is_otp_required;
    }

    public String getSub_shipper_mobile(){
        return sub_shipper_mobile;
    }

    public void setSub_shipper_mobile(String sub_shipper_mobile){
        this.sub_shipper_mobile = sub_shipper_mobile;
    }

    public String getSub_shipper_email(){
        return sub_shipper_email;
    }

    public void setSub_shipper_email(String sub_shipper_email){
        this.sub_shipper_email = sub_shipper_email;
    }

    public String getShipper_id(){
        return shipper_id;
    }

    public void setShipper_id(String shipper_id){
        this.shipper_id = shipper_id;
    }

    public boolean isRCHD_enabled(){
        return RCHD_enabled;
    }

    public void setRCHD_enabled(boolean RCHD_enabled){
        this.RCHD_enabled = RCHD_enabled;
    }

    public int getDispute_delivery(){
        return dispute_delivery;
    }

    public void setDispute_delivery(int dispute_delivery){
        this.dispute_delivery = dispute_delivery;
    }

    public String getLatching_required() {
        return latching_required;
    }

    public void setLatching_required(String latching_required) {
        this.latching_required = latching_required;
    }

    public String getImage_required() {
        return image_required;
    }

    public void setImage_required(String image_required) {
        this.image_required = image_required;
    }

    public String getScan_deliver() {
        return scan_deliver;
    }

    public void setScan_deliver(String scan_deliver) {
        this.scan_deliver = scan_deliver;
    }

    public boolean isRts_skip_otp() {
        return rts_skip_otp;
    }

    public void setRts_skip_otp(boolean rts_skip_otp) {
        this.rts_skip_otp = rts_skip_otp;
    }
}