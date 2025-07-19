package in.ecomexpress.sathi.repo.remote.model.drs_list.forward;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.RoomWarnings;
import androidx.room.TypeConverters;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import in.ecomexpress.sathi.repo.local.db.db_utils.QcItemConverter;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.SlotDetails;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
public class ShipmentDetails implements Parcelable{

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
    @JsonProperty("type")
    private String type;

    @JsonProperty("declared_value")
    private Double declaredValue;

    @JsonProperty("collectable_value")
    private Double collectableValue;

    @JsonProperty("volumetric_weight")
    private double volumetric_weight;

    @JsonProperty("shipper")
    private String shipper;

    @JsonProperty("shipper_id")
    private int shipper_id;

    @JsonProperty("vendorName")
    private String vendorName;

    @JsonProperty("order")
    private String order;

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

    @JsonProperty("item")
    private String item;

    @JsonProperty("ofd_otp")
    private String ofd_otp;

    @JsonProperty("callbridge_pstn")
    private String callbridgePstn;

    @JsonProperty("callbridge_api")
    public String getCallbridgeApi() {
        return callbridgeApi;
    }

    @JsonProperty("callbridge_api")
    public void setCallbridgeApi(String callbridgeApi) {
        this.callbridgeApi = callbridgeApi;
    }

    @JsonProperty("callbridge_api")
    private String callbridgeApi;

    @JsonProperty("reason")
    private Integer reason;

    @JsonProperty("status")
    private String status;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("declared_value")
    public Double getDeclaredValue() {
        return declaredValue;
    }

    @JsonProperty("declared_value")
    public void setDeclaredValue(Double declaredValue) {
        this.declaredValue = declaredValue;
    }

    @JsonProperty("collectable_value")
    public Double getCollectableValue() {
        return collectableValue;
    }

    @JsonProperty("collectable_value")
    public void setCollectableValue(Double collectableValue) {
        this.collectableValue = collectableValue;
    }

    @JsonProperty("shipper")
    public String getShipper() {
        return shipper;
    }

    @JsonProperty("shipper")
    public void setShipper(String shipper) {
        this.shipper = shipper;
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

    @JsonProperty("is_obd")
    private boolean is_obd;

    @JsonProperty("qc_item")
    @TypeConverters(QcItemConverter.class)
    public ArrayList<qc_item> qc_item;

    @NonNull
    @Override
    public String toString() {
        return "ShipmentDetails [type=" + type + ", declaredValue=" + declaredValue + ", collectableValue="
                + collectableValue + ", shipper=" + shipper + ", order=" + order + ", item=" + item
                + ", callbridgePstn=" + callbridgePstn + ", reason=" + reason + ", status=" + status
                + ", remarks=" + remarks
                + ", qcItem=" + qc_item +"]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeValue(this.declaredValue);
        dest.writeValue(this.collectableValue);
        dest.writeValue(this.volumetric_weight);
        dest.writeString(this.shipper);
        dest.writeString(this.order);
        dest.writeString(this.pin);
        dest.writeString(this.item);
        dest.writeString(this.ofd_otp);
        dest.writeString(this.callbridgePstn);
        dest.writeString(this.callbridgeApi);
        dest.writeValue(this.reason);
        dest.writeString(this.status);
        dest.writeString(this.remarks);
        dest.writeInt(this.shipper_id);
        dest.writeList(qc_item);
    }

    public ShipmentDetails() {}

    protected ShipmentDetails(Parcel in) {
        this.type = in.readString();
        this.declaredValue = (Double) in.readValue(Double.class.getClassLoader());
        this.collectableValue = (Double) in.readValue(Double.class.getClassLoader());
        this.volumetric_weight = in.readDouble();
        this.shipper = in.readString();
        this.order = in.readString();
        this.pin = in.readString();
        this.item = in.readString();
        this.ofd_otp = in.readString();
        this.callbridgePstn = in.readString();
        this.callbridgeApi = in.readString();
        this.reason = (Integer) in.readValue(Integer.class.getClassLoader());
        this.status = in.readString();
        this.remarks = in.readString();
        this.shipper_id = in.readInt();
        this.qc_item = new ArrayList<>();
        in.readList(this.qc_item,qc_item.class.getClassLoader());
    }

    public static final Creator<ShipmentDetails> CREATOR = new Creator<>() {
        @Override
        public ShipmentDetails createFromParcel(Parcel source) {
            return new ShipmentDetails(source);
        }

        @Override
        public ShipmentDetails[] newArray(int size) {
            return new ShipmentDetails[size];
        }
    };

    public int getShipper_id() {
        return shipper_id;
    }

    public void setShipper_id(int shipper_id) {
        this.shipper_id = shipper_id;
    }

    @JsonProperty("ofd_otp")
    public String getOfd_otp(){
        return ofd_otp;
    }

    @JsonProperty("ofd_otp")
    public void setOfd_otp(String ofd_otp){
        this.ofd_otp = ofd_otp;
    }

    public boolean isIs_obd() {
        return is_obd;
    }

    public void setIs_obd(boolean is_obd) {
        this.is_obd = is_obd;
    }

    public ArrayList<qc_item> getQc_item() {
        return qc_item;
    }

    public void setQc_item(ArrayList<qc_item> qc_item) {
        this.qc_item = qc_item;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public double getVolumetric_weight() {
        return volumetric_weight;
    }

    public void setVolumetric_weight(double volumetric_weight) {
        this.volumetric_weight = volumetric_weight;
    }
}