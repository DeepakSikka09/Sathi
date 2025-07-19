package in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

import in.ecomexpress.sathi.repo.local.db.db_utils.CallBridgeConverter;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.callbridge_details;
import in.ecomexpress.sathi.utils.GlobalConstant;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class EDSResponse implements Parcelable {
    @JsonProperty("awb_no")
    public Long awbNo;
    @JsonProperty("amazon_encrypted_otp")
    private String amazonEncryptedOtp="";

    @JsonProperty("is_amazon_shipment")
    private String amazon;

    @JsonProperty("secure_delivery_pin1")
    private String dlight_encrypted_otp1;
    @JsonProperty("secure_delivery_pin2")
    private String dlight_encrypted_otp2;
    @JsonProperty("is_dlight_secure_delivery")
    private boolean isDelightShipment;


    @JsonProperty("is_cash_collection")
    private boolean is_cash_collection;

    @JsonProperty("address_status")
    private String address_status;
    @JsonProperty("address_quality_category")
    private String address_quality_category;
    @JsonProperty("address_quality_score")
    private String address_quality_score ="0";

    @JsonProperty("address_profiled")
    private String address_profiled= "N";

    @PrimaryKey
    @JsonIgnore
    @NonNull
    public String compositeKey;

    @JsonIgnore
    private int shipmentSyncStatus = 0;

    @JsonProperty("shipment_type")
    public String shipmentType = GlobalConstant.ShipmentTypeConstants.EDS;
    @JsonProperty("shipment_status")
    public int shipmentStatus = 0;


    @JsonIgnore
    private int missedCallCounter;


    public int getShipmentSyncStatus() {
        return shipmentSyncStatus;
    }

    public void setShipmentSyncStatus(int shipmentSyncStatus) {
        this.shipmentSyncStatus = shipmentSyncStatus;
    }

    public int getMissedCallCounter() {
        return missedCallCounter;
    }

    public void setMissedCallCounter(int missedCallCounter) {
        this.missedCallCounter = missedCallCounter;
    }


    @JsonIgnore
    public String getCompositeKey() {
        return compositeKey;
    }

    @JsonIgnore
    public void setCompositeKey(String compositeKey) {
        this.compositeKey = compositeKey;
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

    @JsonProperty("callbridge_pstn")
    public String getCallbridgePstn() {
        return callbridgePstn;
    }

    @JsonProperty("callbridge_pstn")
    public void setCallbridgePstn(String callbridgePstn) {
        this.callbridgePstn = callbridgePstn;
    }

    @JsonProperty("callbridge_api")
    public String getCallbridge_Api() {
        return callbridge_Api;
    }

    @JsonProperty("callbridge_api")
    public void setCallbridge_Api(String callbridge_Api) {
        this.callbridge_Api = callbridge_Api;
    }

    @JsonProperty("callbridge_pstn")
    private String callbridgePstn;


    @JsonProperty("callbridge_api")
    private String callbridge_Api;


    @JsonProperty("drs_no")
    private int drsNo;

    @JsonProperty("sequence_no")
    private Integer sequenceNo;

    @JsonProperty("sequence_no")
    public Integer getSequenceNo() {
        return sequenceNo;
    }

    @JsonProperty("sequence_no")
    public void setSequenceNo(Integer sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    @JsonProperty("map_sequence_no")
    private int map_sequence_no;

    public int getMap_sequence_no() {
        return map_sequence_no;
    }

    public void setMap_sequence_no(int map_sequence_no) {
        this.map_sequence_no = map_sequence_no;
    }

    @JsonProperty("appointment_slot")
    private String appointmentSlot;

    @JsonProperty("assign_date")
    private long assignDate;
    @Embedded
    @JsonProperty("consignee_details")
    private ConsigneeDetail consigneeDetail;


    @Embedded
    @JsonProperty("shipment_details")
    private ShipmentDetail shipmentDetail;

    @TypeConverters(CallBridgeConverter.class)
    @JsonProperty("callbridge_details")
    public ArrayList<callbridge_details> callbridge_details ;


    public Long getAwbNo() {
        return awbNo;
    }

    public void setAwbNo(Long awbNo) {
        this.awbNo = awbNo;
    }



    public int getDrsNo() {
        return drsNo;
    }

    public void setDrsNo(int drsNo) {
        this.drsNo = drsNo;
    }

    public String getAppointmentSlot() {
        return appointmentSlot;
    }

    public void setAppointmentSlot(String appointmentSlot) {
        this.appointmentSlot = appointmentSlot;
    }

    public long getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(long assignDate) {
        this.assignDate = assignDate;
    }

    public ConsigneeDetail getConsigneeDetail() {
        return consigneeDetail;
    }

    public void setConsigneeDetail(ConsigneeDetail consigneeDetail) {
        this.consigneeDetail = consigneeDetail;
    }

    public ShipmentDetail getShipmentDetail() {
        return shipmentDetail;
    }

    public void setShipmentDetail(ShipmentDetail shipmentDetail) {
        this.shipmentDetail = shipmentDetail;
    }

    public ArrayList<callbridge_details> getCallbridge_details() {
        return callbridge_details;
    }

    public void setCallbridge_details(ArrayList<callbridge_details> callbridge_details) {
        this.callbridge_details = callbridge_details;
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.shipmentType);
        dest.writeInt(this.shipmentStatus);
        dest.writeLong(this.awbNo);
        dest.writeString(this.compositeKey);
        dest.writeInt(this.drsNo);
        dest.writeString(compositeKey);
        dest.writeString(this.appointmentSlot);
        dest.writeLong(this.assignDate);
        dest.writeParcelable(this.consigneeDetail, flags);
        dest.writeParcelable(this.shipmentDetail, flags);
        dest.writeList(this.callbridge_details);
        dest.writeDouble(this.distance);

    }

    public EDSResponse() {
    }

    protected EDSResponse(Parcel in) {

        this.shipmentType = in.readString();
        this.shipmentStatus = in.readInt();
        this.awbNo = in.readLong();
        this.compositeKey=in.readString();
        this.drsNo = in.readInt();
        this.compositeKey=in.readString();
        this.appointmentSlot = in.readString();
        this.assignDate = in.readLong();
        this.consigneeDetail = in.readParcelable(ConsigneeDetail.class.getClassLoader());
        this.shipmentDetail = in.readParcelable(ShipmentDetail.class.getClassLoader());
        this.callbridge_details = new ArrayList<callbridge_details>();
        in.readList(this.callbridge_details,callbridge_details.class.getClassLoader());
        this.distance = in.readDouble();

    }

    public static final Creator<EDSResponse> CREATOR = new Creator<EDSResponse>() {
        @Override
        public EDSResponse createFromParcel(Parcel source) {
            return new EDSResponse(source);
        }

        @Override
        public EDSResponse[] newArray(int size) {
            return new EDSResponse[size];
        }
    };

    @Ignore
    private double distance;

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return this.distance;
    }

    @Override
    public String toString() {
        return "awbNo: " + awbNo + ", callbridge_details=" + callbridge_details + ", consignee details: " + consigneeDetail.toString();
    }

    public String filterValue() {
        return getAwbNo()
                + ", " + getConsigneeDetail().getName()
                + ", " + getConsigneeDetail().getAddress().getLine1()
                + ", " + getConsigneeDetail().getAddress().getPincode();
}

    public String getAmazonEncryptedOtp() {
        return amazonEncryptedOtp;
    }

    public void setAmazonEncryptedOtp(String amazonEncryptedOtp) {
        this.amazonEncryptedOtp = amazonEncryptedOtp;
    }

    public String getAmazon() {
        return amazon;
    }

    public void setAmazon(String amazon) {
        this.amazon = amazon;
    }

    public boolean isIs_cash_collection() {
        return is_cash_collection;
    }

    public void setIs_cash_collection(boolean is_cash_collection) {
        this.is_cash_collection = is_cash_collection;
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

    public boolean isDelightShipment() {
        return isDelightShipment;
    }

    public void setDelightShipment(boolean delightShipment) {
        isDelightShipment = delightShipment;
    }

    public String getAddress_status() {
        return address_status;
    }

    public void setAddress_status(String address_status) {
        this.address_status = address_status;
    }

    public String getAddress_quality_category() {
        return address_quality_category;
    }

    public void setAddress_quality_category(String address_quality_category) {
        this.address_quality_category = address_quality_category;
    }

    public String getAddress_quality_score() {
        return address_quality_score;
    }

    public void setAddress_quality_score(String address_quality_score) {
        this.address_quality_score = address_quality_score;
    }

    public String getAddress_profiled() {
        return address_profiled;
    }

    public void setAddress_profiled(String address_profiled) {
        this.address_profiled = address_profiled;
    }
}
