package in.ecomexpress.sathi.repo.remote.model.mps;

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
import in.ecomexpress.sathi.repo.remote.model.drs_list.common.CInterface;
import in.ecomexpress.sathi.repo.remote.model.drs_list.common.FrwRvpCommonConsigneeDetails;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.callbridge_details;
import in.ecomexpress.sathi.utils.GlobalConstant;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "DRSRVPMPS")
public class DRSRvpQcMpsResponse implements Parcelable, CInterface {

    @JsonProperty("awb_no")
    private Long awbNo;

    @JsonProperty("amazon_encrypted_otp")
    private String amazonEncryptedOtp;

    @JsonProperty("is_amazon_shipment")
    private String amazon;

    @JsonProperty("secure_delivery_pin1")
    private String dlight_encrypted_otp1;
    @JsonProperty("secure_delivery_pin2")
    private String dlight_encrypted_otp2;
    @JsonProperty("is_dlight_secure_delivery")
    private boolean isDelightShipment;
    @NonNull
    @PrimaryKey
    @JsonIgnore
    private String compositeKey= "";

    @JsonIgnore
    private int shipmentSyncStatus;


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
    private String shipmentType = GlobalConstant.ShipmentTypeConstants.RVP_MPS;
    @JsonProperty("shipment_status")
    private int shipmentStatus = 0;
    @Embedded
    @JsonProperty("consignee_details")
    private FrwRvpCommonConsigneeDetails consigneeDetails;
    @Embedded
    @JsonProperty("shipment_details")
    private RvpMpsShipmentDetails shipmentDetails;
    @Embedded
    @JsonProperty("flags")
    private RvpMpsFlags flags;

    @JsonProperty("drs_id")
    private Integer drs;

    @JsonProperty("address_status")
    private String address_status;
    @JsonProperty("address_quality_category")
    private String address_quality_category;
    @JsonProperty("address_quality_score")
    private String address_quality_score= "0";

    @JsonProperty("address_profiled")
    private String address_profiled= "N";

    @JsonIgnore
    public String getCompositeKey() {
        return compositeKey;
    }

    @JsonIgnore
    public void setCompositeKey(String compositeKey) {
        this.compositeKey = compositeKey;
    }

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
    @JsonProperty("map_sequence_no")
    public int getMap_sequence_no() {
        return map_sequence_no;
    }
    @JsonProperty("map_sequence_no")
    public void setMap_sequence_no(int map_sequence_no) {
        this.map_sequence_no = map_sequence_no;
    }

    @JsonProperty("reason_group")
    private String reasonGroup;

    @JsonProperty("delivery_slot")
    private String deliverySlot;

    @JsonProperty("assigned_date")
    private Long assignedDate;

    public int getShipmentSyncStatus() {
        return shipmentSyncStatus;
    }

    public void setShipmentSyncStatus(int shipmentSyncStatus) {
        this.shipmentSyncStatus = shipmentSyncStatus;
    }

    @JsonIgnore
    private int missedCalls;

    public DRSRvpQcMpsResponse() {
    }


    protected DRSRvpQcMpsResponse(Parcel in) {
        if (in.readByte() == 0) {
            awbNo = null;
        } else {
            awbNo = in.readLong();
        }
        shipmentType = in.readString();
        shipmentStatus = in.readInt();
        consigneeDetails = in.readParcelable(FrwRvpCommonConsigneeDetails.class.getClassLoader());
        shipmentDetails = in.readParcelable(RvpMpsShipmentDetails.class.getClassLoader());
        if (in.readByte() == 0) {
            drs = null;
        } else {
            drs = in.readInt();
        }
        reasonGroup = in.readString();
        deliverySlot = in.readString();
        if (in.readByte() == 0) {
            assignedDate = null;
        } else {
            assignedDate = in.readLong();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (awbNo == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(awbNo);
        }
        dest.writeString(shipmentType);
        dest.writeInt(shipmentStatus);
        dest.writeParcelable(consigneeDetails, flags);
        dest.writeParcelable(shipmentDetails, flags);
        if (drs == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(drs);
        }
        dest.writeString(reasonGroup);
        dest.writeString(deliverySlot);
        if (assignedDate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(assignedDate);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DRSRvpQcMpsResponse> CREATOR = new Creator<DRSRvpQcMpsResponse>() {
        @Override
        public DRSRvpQcMpsResponse createFromParcel(Parcel in) {
            return new DRSRvpQcMpsResponse(in);
        }

        @Override
        public DRSRvpQcMpsResponse[] newArray(int size) {
            return new DRSRvpQcMpsResponse[size];
        }
    };

    @JsonProperty("consignee_details")
    public FrwRvpCommonConsigneeDetails getConsigneeDetails() {
        return consigneeDetails;
    }

    @JsonProperty("consignee_details")
    public void setConsigneeDetails(FrwRvpCommonConsigneeDetails consigneeDetails) {
        this.consigneeDetails = consigneeDetails;
    }

    @JsonProperty("shipment_details")
    public RvpMpsShipmentDetails getShipmentDetails() {
        return shipmentDetails;
    }

    @JsonProperty("shipment_details")
    public void setShipmentDetails(RvpMpsShipmentDetails shipmentDetails) {
        this.shipmentDetails = shipmentDetails;
    }

    public Long getAwbNo() {
        return awbNo;
    }

    public void setAwbNo(Long awbNo) {
        this.awbNo = awbNo;
    }

    @JsonProperty("flags")
    public RvpMpsFlags getFlags() {
        return flags;
    }

    @JsonProperty("flags")
    public void setFlags(RvpMpsFlags flags) {
        this.flags = flags;
    }

    @JsonProperty("drs_id")
    public Integer getDrs() {
        return drs;
    }

    @JsonProperty("drs_id")
    public void setDrs(Integer drs) {
        this.drs = drs;
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

    @Override
    public String toString() {
        return "DRSReverseQCTypeResponse{" +
                "awbNo=" + awbNo +
                ", shipmentType='" + shipmentType + '\'' +
                ", shipmentStatus='" + shipmentStatus + '\'' +
                ", consigneeDetails=" + consigneeDetails +
                ", shipmentDetails=" + shipmentDetails +
                ", flags=" + flags +
                ", drs=" + drs +
                ", reasonGroup='" + reasonGroup + '\'' +
                ", deliverySlot='" + deliverySlot + '\'' +
                ", assignedDate=" + assignedDate +
                '}';
    }

    @TypeConverters(CallBridgeConverter.class)
    @JsonProperty("callbridge_details")
    public ArrayList<callbridge_details> callbridge_details ;

    @JsonProperty("callbridge_details")
    public ArrayList<callbridge_details> getCallbridge_details() {
        return callbridge_details;
    }

    @JsonProperty("callbridge_details")
    public void setCallbridge_details(ArrayList<callbridge_details> callbridge_details) {
        this.callbridge_details = callbridge_details;
    }

    @Ignore
    private double distance;

    @Override
    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public double getDistance() {
        return this.distance;
    }

    public int getMissedCalls() {
        return missedCalls;
    }

    public void setMissedCalls(int missedCalls) {
        this.missedCalls = missedCalls;
    }

    public String filterValue() {
        return getAwbNo() + ", " + getConsigneeDetails().getName()
                + ", " + getConsigneeDetails().getAddress().toString()
                ;
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