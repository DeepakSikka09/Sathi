package in.ecomexpress.sathi.repo.remote.model.drs_list.rvp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Ignore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.SlotDetails;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RvpShipmentDetails implements Parcelable {

    @JsonProperty("slot_details")
    public SlotDetails getSlot_details() {
        return slot_details;
    }

    @JsonProperty("slot_details")
    public void setSlot_details(SlotDetails slot_details) {
        this.slot_details = slot_details;
    }

    @Embedded
    @JsonProperty("slot_details")
    private SlotDetails slot_details =new SlotDetails();

    @JsonProperty("shipper")
    private String shipper;

    @JsonProperty("type")
    private String type;

    @JsonProperty("shipper_code")
    private String shipperCode;

    @JsonProperty("shipper_id")
    private int shipper_id;

    @JsonProperty("pin")
    public String getPin() {
        return pin;
    }

    @JsonProperty("pin")
    public void setPin(String pin) {
        this.pin = pin;
    }

    @JsonProperty("pin")
    private String pin;
    @JsonProperty("order")
    private String order;

    @JsonProperty("item")
    private String item;

    @JsonProperty("callbridge_pstn")
    private String callbridgePstn = null;

    @JsonProperty("ofd_otp")
    private String ofd_otp="";


    @JsonProperty("callbridge_api")
    private String callbridge_Api = null;

    public String getCallbridge_Api() {
        return callbridge_Api;
    }

    public void setCallbridge_Api(String callbridge_Api) {
        this.callbridge_Api = callbridge_Api;
    }

    @JsonProperty("reason")
    private Integer reason;

    @JsonProperty("status")
    private String status;

    @JsonProperty("remarks")
    private String remarks;

    @Ignore
    @JsonProperty("quality_checks")
    private List<RvpQualityCheck> qualityChecks = null;

    public RvpShipmentDetails() {

    }

    public RvpShipmentDetails(Parcel in) {
        shipper = in.readString();
        shipperCode = in.readString();
        order = in.readString();
        item = in.readString();
        callbridgePstn = in.readString();
        callbridge_Api = in.readString();
        if (in.readByte() == 0) {
            reason = null;
        } else {
            reason = in.readInt();
        }
        status = in.readString();
        remarks = in.readString();
        qualityChecks = in.createTypedArrayList(RvpQualityCheck.CREATOR);
        shipper_id = in.readInt();
        ofd_otp = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(shipper);
        dest.writeString(shipperCode);
        dest.writeString(order);
        dest.writeString(item);
        dest.writeString(callbridgePstn);
        dest.writeString(callbridge_Api);
        if (reason == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(reason);
        }
        dest.writeString(status);
        dest.writeString(remarks);
        dest.writeTypedList(qualityChecks);
        dest.writeInt(shipper_id);
        dest.writeString(ofd_otp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RvpShipmentDetails> CREATOR = new Creator<RvpShipmentDetails>() {
        @Override
        public RvpShipmentDetails createFromParcel(Parcel in) {
            return new RvpShipmentDetails(in);
        }

        @Override
        public RvpShipmentDetails[] newArray(int size) {
            return new RvpShipmentDetails[size];
        }
    };

    @JsonProperty("shipper")
    public String getShipper() {
        return shipper;
    }

    @JsonProperty("shipper")
    public void setShipper(String shipper) {
        this.shipper = shipper;
    }

    @JsonProperty("shipper_code")
    public String getShipperCode() {
        return shipperCode;
    }

    @JsonProperty("shipper_code")
    public void setShipperCode(String shipperCode) {
        this.shipperCode = shipperCode;
    }


    @JsonProperty("order")
    public String getOrder() {
        return order;
    }

    @JsonProperty("order")
    public void setOrder(String order) {
        this.order = order;
    }

    @JsonProperty("item")
    public String getItem() {
        return item;
    }

    @JsonProperty("item")
    public void setItem(String item) {
        this.item = item;
    }

    @JsonProperty("callbridge_pstn")
    public String getCallbridgePstn() {
        return callbridgePstn;
    }

    @JsonProperty("callbridge_pstn")
    public void setCallbridgePstn(String callbridgePstn) {
        this.callbridgePstn = callbridgePstn;
    }

    @JsonProperty("reason")
    public Integer getReason() {
        return reason;
    }

    @JsonProperty("reason")
    public void setReason(Integer reason) {
        this.reason = reason;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("remarks")
    public String getRemarks() {
        return remarks;
    }

    @JsonProperty("remarks")
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @JsonProperty("quality_checks")
    public List<RvpQualityCheck> getQualityChecks() {
        return qualityChecks;
    }

    @JsonProperty("quality_checks")
    public void setQualityChecks(List<RvpQualityCheck> qualityChecks) {
        this.qualityChecks = qualityChecks;
    }

    @NonNull
    @Override
    public String toString() {
        return "RvpShipmentDetails [shipper=" + shipper + ", shipperCode=" + shipperCode + ", order="
                + order + ", item=" + item + ", callbridgePstn=" + callbridgePstn + ", reason=" + reason + ", status="
                + status + ", remarks=" + remarks + ", qualityChecks=" + qualityChecks + "]";
    }

    public int getShipper_id() {
        return shipper_id;
    }

    public void setShipper_id(int shipper_id) {
        this.shipper_id = shipper_id;
    }

    public String getOfd_otp(){
        return ofd_otp;
    }

    public void setOfd_otp(String ofd_otp){
        this.ofd_otp = ofd_otp;
    }

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }
}