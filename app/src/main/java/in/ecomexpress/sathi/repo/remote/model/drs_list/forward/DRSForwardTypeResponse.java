package in.ecomexpress.sathi.repo.remote.model.drs_list.forward;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
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
import in.ecomexpress.sathi.repo.remote.model.ErrorResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.common.CInterface;
import in.ecomexpress.sathi.repo.remote.model.drs_list.common.FrwRvpCommonConsigneeDetails;
import in.ecomexpress.sathi.utils.GlobalConstant;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "DRSForward")
public class DRSForwardTypeResponse extends ErrorResponse implements CInterface,Parcelable {
    @JsonProperty("awb_no")
    private Long awbNo;

    @JsonIgnore
    @PrimaryKey
    @NonNull
    public String compositeKey;

    @JsonProperty("amazon_encrypted_otp")
    private String amazonEncryptedOtp="";

    @JsonProperty("is_amazon_shipment")
    private String amazon="";

    @JsonProperty("secure_delivery_pin1")
    private String dlight_encrypted_otp1;

    @JsonProperty("secure_delivery_pin2")
    private String dlight_encrypted_otp2;

    @JsonProperty("is_dlight_secure_delivery")
    private boolean isDelightShipment;

    @JsonProperty("same_day_reassign_status")
    private boolean samedayreassignstatus=true;

    @JsonProperty("inscan_date")
    private long inscan_date;

    @JsonProperty("total_attempts")
    private int total_attempts;

    @JsonIgnore
    private int shipmentSyncStatus;

    @JsonProperty("cod_collected")
    public Float getCod_collected() {
        return cod_collected;
    }

    @JsonProperty("cod_collected")
    public void setCod_collected(Float cod_collected) {
        this.cod_collected = cod_collected;
    }

    @JsonProperty("ecod_collected")
    public Float getEcod_collected() {
        return ecod_collected;
    }

    @JsonProperty("ecod_collected")
    public void setEcod_collected(Float ecod_collected) {
        this.ecod_collected = ecod_collected;
    }

    @JsonProperty("cod_collected")
    private Float cod_collected;

    @JsonProperty("ecod_collected")
    private Float ecod_collected;

    @JsonProperty("address_status")
    private String address_status;

    @JsonProperty("address_quality_category")
    private String address_quality_category;

    @JsonProperty("address_quality_score")
    private String address_quality_score= "0";

    @JsonProperty("address_profiled")
    private String address_profiled= "N";

    @NonNull
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
    public int isCallattempted = 0;


    @JsonProperty("mps_shipment")
    public String mpsShipment;

    @JsonIgnore
    @JsonProperty("mps_shipment")
    public String getMpsShipment() {
        return mpsShipment;
    }

    @JsonProperty("mps_shipment")
    public void setMpsShipment(String mpsShipment) {
        this.mpsShipment = mpsShipment;
    }

    @JsonIgnore
    @ColumnInfo(name = "mpsAWBNos")
    public String mpsAWBs;

    @JsonProperty("mps_awb_nos")
    @Ignore
    public ArrayList<Long> mpsAWBNo;

    @JsonIgnore
    private int missedCalls;

    public int getMissedCalls() {
        return missedCalls;
    }

    public void setMissedCalls(int missedCalls) {
        this.missedCalls = missedCalls;
    }

    @JsonProperty("drs_id")
    private int drsId;

    @JsonProperty("consignee_details")
    @Embedded
    private FrwRvpCommonConsigneeDetails consigneeDetails;

    @Embedded
    @JsonProperty("shipment_details")
    private ShipmentDetails shipmentDetails;

    @TypeConverters(CallBridgeConverter.class)
    @JsonProperty("callbridge_details")
    public ArrayList<callbridge_details> callbridge_details ;

    @Ignore
    private double distance;

    @Embedded
    @JsonProperty("flags")
    private Flags flags;
    @JsonProperty("reason_group")
    private String reasonGroup;
    @JsonProperty("shipment_type")
    private String shipmentType = GlobalConstant.ShipmentTypeConstants.FWD;
    @JsonProperty("shipment_status")
    private int shipmentStatus;
    @JsonProperty("delivery_slot")
    private String deliverySlot;

    @JsonProperty("assigned_date")
    private long assignedDate;

    @JsonProperty("consignee_details")
    public FrwRvpCommonConsigneeDetails getConsigneeDetails() {
        return consigneeDetails;
    }


    public int getShipmentSyncStatus() {
        return shipmentSyncStatus;
    }

    public void setShipmentSyncStatus(int shipmentSyncStatus) {
        this.shipmentSyncStatus = shipmentSyncStatus;
    }

    @JsonProperty("consignee_details")
    public void setConsigneeDetails(FrwRvpCommonConsigneeDetails consigneeDetails) {
        this.consigneeDetails = consigneeDetails;
    }

    public int getShipmentStatus() {
        return shipmentStatus;
    }

    public void setShipmentStatus(int shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }

    @JsonProperty("shipment_details")
    public ShipmentDetails getShipmentDetails() {
        return shipmentDetails;
    }

    @JsonProperty("shipment_details")
    public void setShipmentDetails(ShipmentDetails shipmentDetails) {
        this.shipmentDetails = shipmentDetails;
    }

    @JsonProperty("callbridge_details")
    public ArrayList<callbridge_details> getCallbridge_details() {
        return callbridge_details;
    }

    @JsonProperty("callbridge_details")
    public void setCallbridge_details(ArrayList<callbridge_details> callbridge_details) {
        this.callbridge_details = callbridge_details;
    }

    public String getShipmentType() {
        return shipmentType;
    }

    public void setShipmentType(String shipmentType) {
        this.shipmentType = shipmentType;
    }

    @JsonProperty("flags")
    public Flags getFlags() {
        return flags;
    }


    @JsonProperty("flags")
    public void setFlags(Flags flags) {
        this.flags = flags;
    }

    @JsonProperty("reason_group")
    public String getReasonGroup() {
        return reasonGroup;
    }

    @JsonProperty("reason_group")
    public void setReasonGroup(String reasonGroup) {
        this.reasonGroup = reasonGroup;
    }

    @JsonProperty("delivery_slot")
    public String getDeliverySlot() {
        return deliverySlot;
    }

    @JsonProperty("delivery_slot")
    public void setDeliverySlot(String deliverySlot) {
        this.deliverySlot = deliverySlot;
    }

    @JsonProperty("assigned_date")
    public long getAssignedDate() {
        return assignedDate;
    }

    @JsonProperty("assigned_date")
    public void setAssignedDate(long assignedDate) {
        this.assignedDate = assignedDate;
    }

    public Long getAwbNo() {
        return awbNo;
    }

    public void setAwbNo(Long awbNo) {
        this.awbNo = awbNo;
    }

    @JsonProperty("drs_id")
    public Integer getDrsId() {
        return drsId;
    }

    @JsonProperty("drs_id")
    public void setDrsId(int drs) {
        this.drsId = drs;
    }

    @JsonProperty("sequence_no")
    private int sequenceNo;

    @JsonProperty("sequence_no")
    public int getSequenceNo() {
        return sequenceNo;
    }

    @JsonProperty("sequence_no")
    public void setSequenceNo(int sequenceNo) {
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

    @NonNull
    @Override
    public String toString() {
        return "DRSForwardTypeResponse{" +
                "awbNo=" + awbNo +
                ", isCallattempted=" + isCallattempted +
                ", missedCalls=" + missedCalls +
                ", drsId=" + drsId +
                ", sequenceNo=" + sequenceNo +
                ", map_sequence_no=" + map_sequence_no +
                ", consigneeDetails=" + consigneeDetails +
                ", shipmentDetails=" + shipmentDetails +
                ", callbridge_details=" + callbridge_details +
                ", distance=" + distance +
                ", flags=" + flags +
                ", reasonGroup='" + reasonGroup + '\'' +
                ", shipmentType='" + shipmentType + '\'' +
                ", shipmentStatus=" + shipmentStatus +
                ", deliverySlot='" + deliverySlot + '\'' +
                ", assignedDate=" + assignedDate +
                '}';
    }


    @Override
    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public double getDistance() {
        return this.distance;
    }

    public String filterValue() {
        return getAwbNo()
                + ", " + getConsigneeDetails().getName()
                + ", " + getConsigneeDetails().getAddress().toString();
    }

    public DRSForwardTypeResponse() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected DRSForwardTypeResponse(Parcel in) {
        super(in);
        this.awbNo = (Long) in.readValue(Long.class.getClassLoader());
        this.compositeKey = (String) in.readValue(Long.class.getClassLoader());
        this.shipmentSyncStatus = in.readInt();
        this.cod_collected = (Float) in.readValue(Float.class.getClassLoader());
        this.ecod_collected = (Float) in.readValue(Float.class.getClassLoader());
        this.isCallattempted = in.readInt();
        this.mpsShipment = in.readString();
        this.mpsAWBs = in.readString();
        this.amazonEncryptedOtp = in.readString();
        this.mpsAWBNo = new ArrayList<Long>();
        in.readList(this.mpsAWBNo, Long.class.getClassLoader());
        this.missedCalls = in.readInt();
        this.drsId = in.readInt();
        this.consigneeDetails = in.readParcelable(FrwRvpCommonConsigneeDetails.class.getClassLoader());
        this.shipmentDetails = in.readParcelable(ShipmentDetails.class.getClassLoader());
        this.callbridge_details = new ArrayList<callbridge_details>();
        in.readList(this.callbridge_details,callbridge_details.class.getClassLoader());
        this.distance = in.readDouble();
        this.flags = in.readParcelable(Flags.class.getClassLoader());
        this.reasonGroup = in.readString();
        this.shipmentType = in.readString();
        this.shipmentStatus = in.readInt();
        this.deliverySlot = in.readString();
        this.amazon = in.readString();
        this.assignedDate = (Long) in.readValue(Long.class.getClassLoader());
        this.sequenceNo = in.readInt();
        this.map_sequence_no=in.readInt();
        this.status = in.readByte() != 0;
        this.eResponse = in.readParcelable(EResponse.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeValue(this.awbNo);
        dest.writeValue(this.compositeKey);
        dest.writeInt(this.shipmentSyncStatus);
        dest.writeValue(this.cod_collected);
        dest.writeValue(this.ecod_collected);
        dest.writeInt(this.isCallattempted);
        dest.writeString(this.mpsShipment);
        dest.writeString(this.mpsAWBs);
        dest.writeList(this.mpsAWBNo);
        dest.writeInt(this.missedCalls);
        dest.writeInt(this.drsId);
        dest.writeString(this.amazonEncryptedOtp);
        dest.writeParcelable(this.consigneeDetails, flags);
        dest.writeParcelable(this.shipmentDetails, flags);
        dest.writeList(callbridge_details);
        dest.writeDouble(this.distance);
        dest.writeParcelable(this.flags, flags);
        dest.writeString(this.reasonGroup);
        dest.writeString(this.shipmentType);
        dest.writeInt(this.shipmentStatus);
        dest.writeString(this.amazon);
        dest.writeString(this.deliverySlot);
        dest.writeValue(this.assignedDate);
        dest.writeInt(this.sequenceNo);
        dest.writeInt(this.map_sequence_no);
        dest.writeByte(this.status ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.eResponse, flags);
    }

    public static final Creator<DRSForwardTypeResponse> CREATOR = new Creator<DRSForwardTypeResponse>() {
        @Override
        public DRSForwardTypeResponse createFromParcel(Parcel source) {
            return new DRSForwardTypeResponse(source);
        }

        @Override
        public DRSForwardTypeResponse[] newArray(int size) {
            return new DRSForwardTypeResponse[size];
        }
    };

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

    public boolean isSamedayreassignstatus(){
        return samedayreassignstatus;
    }

    public void setSamedayreassignstatus(boolean samedayreassignstatus){
        this.samedayreassignstatus = samedayreassignstatus;
    }

    public long getInscan_date() {
        return inscan_date;
    }

    public void setInscan_date(long inscan_date) {
        this.inscan_date = inscan_date;
    }

    public int getTotal_attempts() {
        return total_attempts;
    }

    public void setTotal_attempts(int total_attempts) {
        this.total_attempts = total_attempts;
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