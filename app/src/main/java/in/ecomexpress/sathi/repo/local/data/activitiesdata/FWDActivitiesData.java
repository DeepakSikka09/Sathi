package in.ecomexpress.sathi.repo.local.data.activitiesdata;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.SecureDelivery;

public class FWDActivitiesData implements Parcelable {
    private String amazon_encrypted_otp;
    private String amazon;
    private String ofd_otp;
    private String dlight_encrypted_otp1;
    private String dlight_encrypted_otp2;
    private String consignee_mobile;
    private String orderId;
    private String mpsShipment;
    private String mpsAWBs;
    private String return_package_barcode;
    private String consignee_alternate_number;
    private String show_fwd_undl_btn;
    private String compositeKey;
    private String drsPin;
    private String drsApiKey;
    private String drsPstnKey;
    private String shipment_type;
    private String scanValue;
    private String type;
    private String amount;
    private String change;
    private long awbNo;
    private int drsId;
    private boolean isCard;
    private boolean is_amazon_reschedule_enabled;
    private boolean callAllowed;
    private boolean resend_otp_enable;
    private boolean isDelightShipment;
    private boolean sign_image_required;
    private String fwd_del_image;
    private SecureDelivery secure_delivery;
    private String secure_undelivered;
    private String collected_value;
    private String mps_undelivered;
    private String decideNext;
    private String reason;
    private boolean is_obd;

    public FWDActivitiesData() {

    }

    public static final Creator<FWDActivitiesData> CREATOR = new Creator<FWDActivitiesData>() {
        @Override
        public FWDActivitiesData createFromParcel(Parcel in) {
            return new FWDActivitiesData(in);
        }

        @Override
        public FWDActivitiesData[] newArray(int size) {
            return new FWDActivitiesData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {

        dest.writeString(this.amazon_encrypted_otp);
        dest.writeString(this.amazon);
        dest.writeString(this.ofd_otp);
        dest.writeString(this.dlight_encrypted_otp1);
        dest.writeString(this.dlight_encrypted_otp2);
        dest.writeString(this.consignee_mobile);
        dest.writeString(this.orderId);
        dest.writeString(this.mpsShipment);
        dest.writeString(this.mpsAWBs);
        dest.writeString(this.return_package_barcode);
        dest.writeString(this.consignee_alternate_number);
        dest.writeString(this.show_fwd_undl_btn);
        dest.writeString(this.compositeKey);
        dest.writeString(this.drsPin);
        dest.writeString(this.drsApiKey);
        dest.writeString(this.drsPstnKey);
        dest.writeString(this.shipment_type);
        dest.writeString(this.scanValue);
        dest.writeString(this.type);
        dest.writeString(this.amount);
        dest.writeString(this.change);
        dest.writeLong(this.awbNo);
        dest.writeInt(this.drsId);
        dest.writeValue(this.isCard);
        dest.writeValue(this.is_amazon_reschedule_enabled);
        dest.writeValue(this.callAllowed);
        dest.writeValue(this.resend_otp_enable);
        dest.writeValue(this.isDelightShipment);
        dest.writeValue(this.sign_image_required);
        dest.writeString(this.fwd_del_image);
        dest.writeParcelable(this.secure_delivery, flags);
        dest.writeString(this.secure_undelivered);
        dest.writeString(this.collected_value);
        dest.writeString(this.mps_undelivered);
        dest.writeString(this.decideNext);
        dest.writeString(this.reason);
        dest.writeValue(this.is_obd);
    }

    protected FWDActivitiesData(Parcel in) {

        this.amazon_encrypted_otp = in.readString();
        this.amazon = in.readString();
        this.ofd_otp = in.readString();
        this.dlight_encrypted_otp1 = in.readString();
        this.dlight_encrypted_otp2 = in.readString();
        this.consignee_mobile = in.readString();
        this.orderId = in.readString();
        this.mpsShipment = in.readString();
        this.mpsAWBs = in.readString();
        this.return_package_barcode = in.readString();
        this.consignee_alternate_number = in.readString();
        this.show_fwd_undl_btn = in.readString();
        this.compositeKey = in.readString();
        this.drsPin = in.readString();
        this.drsApiKey = in.readString();
        this.drsPstnKey = in.readString();
        this.shipment_type = in.readString();
        this.scanValue = in.readString();
        this.type = in.readString();
        this.amount = in.readString();
        this.change = in.readString();
        this.awbNo = in.readLong();
        this.drsId = in.readInt();
        this.isCard = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.is_amazon_reschedule_enabled = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.callAllowed = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.resend_otp_enable = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isDelightShipment = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.sign_image_required = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.fwd_del_image = in.readString();
        this.secure_delivery = in.readParcelable(SecureDelivery.class.getClassLoader());
        this.secure_undelivered = in.readString();
        this.collected_value = in.readString();
        this.mps_undelivered = in.readString();
        this.decideNext = in.readString();
        this.reason = in.readString();
        this.is_obd = (Boolean) in.readValue(Boolean.class.getClassLoader());

    }

    public String getAmazon_encrypted_otp() {
        return amazon_encrypted_otp;
    }

    public void setAmazon_encrypted_otp(String amazon_encrypted_otp) {
        this.amazon_encrypted_otp = amazon_encrypted_otp;
    }

    public String getAmazon() {
        return amazon;
    }

    public void setAmazon(String amazon) {
        this.amazon = amazon;
    }

    public String getOfd_otp() {
        return ofd_otp;
    }

    public void setOfd_otp(String ofd_otp) {
        this.ofd_otp = ofd_otp;
    }

    public String getDlight_encrypted_otp1() {
        return dlight_encrypted_otp1;
    }

    public void setDlight_encrypted_otp1(String dlight_encrypted_otp1) {
        this.dlight_encrypted_otp1 = dlight_encrypted_otp1;
    }

    public String getDlight_encrypted_otp2() {
        return dlight_encrypted_otp2;
    }

    public void setDlight_encrypted_otp2(String dlight_encrypted_otp2) {
        this.dlight_encrypted_otp2 = dlight_encrypted_otp2;
    }

    public String getConsignee_mobile() {
        return consignee_mobile;
    }

    public void setConsignee_mobile(String consignee_mobile) {
        this.consignee_mobile = consignee_mobile;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMpsShipment() {
        return mpsShipment;
    }

    public void setMpsShipment(String mpsShipment) {
        this.mpsShipment = mpsShipment;
    }

    public String getMpsAWBs() {
        return mpsAWBs;
    }

    public void setMpsAWBs(String mpsAWBs) {
        this.mpsAWBs = mpsAWBs;
    }

    public String getReturn_package_barcode() {
        return return_package_barcode;
    }

    public void setReturn_package_barcode(String return_package_barcode) {
        this.return_package_barcode = return_package_barcode;
    }

    public String getConsignee_alternate_number() {
        return consignee_alternate_number;
    }

    public void setConsignee_alternate_number(String consignee_alternate_number) {
        this.consignee_alternate_number = consignee_alternate_number;
    }

    public String getShow_fwd_undl_btn() {
        return show_fwd_undl_btn;
    }

    public void setShow_fwd_undl_btn(String show_fwd_undl_btn) {
        this.show_fwd_undl_btn = show_fwd_undl_btn;
    }

    public String getCompositeKey() {
        return compositeKey;
    }

    public void setCompositeKey(String compositeKey) {
        this.compositeKey = compositeKey;
    }

    public String getDrsPin() {
        return drsPin;
    }

    public void setDrsPin(String drsPin) {
        this.drsPin = drsPin;
    }

    public String getDrsApiKey() {
        return drsApiKey;
    }

    public void setDrsApiKey(String drsApiKey) {
        this.drsApiKey = drsApiKey;
    }

    public String getDrsPstnKey() {
        return drsPstnKey;
    }

    public void setDrsPstnKey(String drsPstnKey) {
        this.drsPstnKey = drsPstnKey;
    }

    public String getShipment_type() {
        return shipment_type;
    }

    public void setShipment_type(String shipment_type) {
        this.shipment_type = shipment_type;
    }

    public String getScanValue() {
        return scanValue;
    }

    public void setScanValue(String scanValue) {
        this.scanValue = scanValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public long getAwbNo() {
        return awbNo;
    }

    public void setAwbNo(long awbNo) {
        this.awbNo = awbNo;
    }

    public int getDrsId() {
        return drsId;
    }

    public void setDrsId(int drsId) {
        this.drsId = drsId;
    }

    public boolean isCard() {
        return isCard;
    }

    public void setCard(boolean card) {
        isCard = card;
    }

    public boolean isIs_amazon_reschedule_enabled() {
        return is_amazon_reschedule_enabled;
    }

    public void setIs_amazon_reschedule_enabled(boolean is_amazon_reschedule_enabled) {
        this.is_amazon_reschedule_enabled = is_amazon_reschedule_enabled;
    }

    public boolean isCallAllowed() {
        return callAllowed;
    }

    public void setCallAllowed(boolean callAllowed) {
        this.callAllowed = callAllowed;
    }

    public boolean isResend_otp_enable() {
        return resend_otp_enable;
    }

    public void setResend_otp_enable(boolean resend_otp_enable) {
        this.resend_otp_enable = resend_otp_enable;
    }

    public boolean isDelightShipment() {
        return isDelightShipment;
    }

    public void setDelightShipment(boolean delightShipment) {
        isDelightShipment = delightShipment;
    }

    public boolean isSign_image_required() {
        return sign_image_required;
    }

    public void setSign_image_required(boolean sign_image_required) {
        this.sign_image_required = sign_image_required;
    }

    public String getFwd_del_image() {
        return fwd_del_image;
    }

    public void setFwd_del_image(String fwd_del_image) {
        this.fwd_del_image = fwd_del_image;
    }

    public SecureDelivery getSecure_delivery() {
        return secure_delivery;
    }

    public void setSecure_delivery(SecureDelivery secure_delivery) {
        this.secure_delivery = secure_delivery;
    }

    public String getSecure_undelivered() {
        return secure_undelivered;
    }

    public void setSecure_undelivered(String secure_undelivered) {
        this.secure_undelivered = secure_undelivered;
    }

    public String getCollected_value() {
        return collected_value;
    }

    public void setCollected_value(String collected_value) {
        this.collected_value = collected_value;
    }

    public String getMps_undelivered() {
        return mps_undelivered;
    }

    public void setMps_undelivered(String mps_undelivered) {
        this.mps_undelivered = mps_undelivered;
    }

    public String getDecideNext() {
        return decideNext;
    }

    public void setDecideNext(String decideNext) {
        this.decideNext = decideNext;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isIs_obd() {
        return is_obd;
    }

    public void setIs_obd(boolean is_obd) {
        this.is_obd = is_obd;
    }
}
