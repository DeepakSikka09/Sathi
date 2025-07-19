package in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Embedded;
import androidx.room.Ignore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShipmentDetail implements Parcelable {
    @Embedded
    @JsonProperty("flag")
    private ShipmentFlag flag;

    @JsonProperty("cust_code")
    public String cust_code;

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

    @JsonProperty("reschedule_attempt_times")
    private int rescheduleAttemptTimes;

    @JsonProperty("reason_code")
    private String reasonCode;

    @JsonProperty("order_no")
    private String orderNo;

    @JsonProperty("item_description")
    private String itemDescription;

    @JsonProperty("cust_name")
    private String customerName;

    @JsonProperty("shipper_id")
    private int shipper_id;

    @JsonProperty("ofd_otp")
    private String ofd_otp;

    @Ignore
    @JsonProperty("activity_wizard")
    private List<EDSActivityWizard> edsActivityWizards;


    public List<EDSActivityWizard> getEdsActivityWizards() {
        return edsActivityWizards;
    }

    public int getRescheduleAttemptTimes() {
        return rescheduleAttemptTimes;
    }
    public void setRescheduleAttemptTimes(int rescheduleAttemptTimes) {
        this.rescheduleAttemptTimes = rescheduleAttemptTimes;
    }

    public void setEdsActivityWizards(List<EDSActivityWizard> edsActivityWizards) {
        this.edsActivityWizards = edsActivityWizards;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public ShipmentFlag getFlag() {
        return flag;
    }

    public void setFlag(ShipmentFlag flag) {
        this.flag = flag;
    }

    public void setCust_code(String cust_code) {
        this.cust_code = cust_code;
    }

    public String getCust_code() {
        return cust_code;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.flag, flags);
        dest.writeString(this.reasonCode);
        dest.writeString(this.orderNo);
        dest.writeString(this.itemDescription);
        dest.writeString(this.customerName);
        dest.writeInt(this.rescheduleAttemptTimes);
        dest.writeTypedList(this.edsActivityWizards);
        dest.writeString(this.cust_code);
        dest.writeInt(this.shipper_id);
        dest.writeString(this.ofd_otp);
    }

    public ShipmentDetail() {
    }

    protected ShipmentDetail(Parcel in) {
        this.flag = in.readParcelable(ShipmentFlag.class.getClassLoader());
        this.reasonCode = in.readString();
        this.orderNo = in.readString();
        this.itemDescription = in.readString();
        this.customerName = in.readString();
        this.rescheduleAttemptTimes=in.readInt();
        this.edsActivityWizards = in.createTypedArrayList(EDSActivityWizard.CREATOR);
        this.cust_code=in.readString();
        this.shipper_id = in.readInt();
        this.ofd_otp = in.readString();
    }

    public static final Creator<ShipmentDetail> CREATOR = new Creator<ShipmentDetail>() {
        @Override
        public ShipmentDetail createFromParcel(Parcel source) {
            return new ShipmentDetail(source);
        }

        @Override
        public ShipmentDetail[] newArray(int size) {
            return new ShipmentDetail[size];
        }
    };

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
}
