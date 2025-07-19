package in.ecomexpress.sathi.repo.remote.model.masterdata;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.reactivex.annotations.NonNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "EDSMasterReasonCode")
public class EDSReasonCodeMaster implements Parcelable {
    @PrimaryKey
    @NonNull
    @JsonProperty("reason_code")
    private Integer reasonCode;

    @JsonProperty("reason_id")
    private Integer reason_id;
    @JsonProperty("reason_msg")
    private String reasonMessage;

    @JsonProperty("sub_group")
    private String subGroup;

    @JsonProperty("attribute")
    @Embedded
    private EDSMasterDataAttributeResponse edsMasterDataAttributeResponse = new EDSMasterDataAttributeResponse();

    public Integer getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(Integer reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getReasonMessage() {
        return reasonMessage;
    }

    public void setReasonMessage(String reasonMessage) {
        this.reasonMessage = reasonMessage;
    }

    @JsonProperty("sub_group")
    public String getSubGroup() {
        return subGroup;
    }

    @JsonProperty("sub_group")
    public void setSubGroup(String subGroup) {
        this.subGroup = subGroup;
    }

    public EDSMasterDataAttributeResponse getEdsMasterDataAttributeResponse() {
        return edsMasterDataAttributeResponse;
    }

    public void setEdsMasterDataAttributeResponse(EDSMasterDataAttributeResponse edsMasterDataAttributeResponse) {
        this.edsMasterDataAttributeResponse = edsMasterDataAttributeResponse;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.reasonCode);
        dest.writeString(this.reasonMessage);
        dest.writeString(this.subGroup);
        dest.writeInt(this.reason_id);
        dest.writeParcelable(this.edsMasterDataAttributeResponse, flags);
    }

    public EDSReasonCodeMaster() {
    }

    protected EDSReasonCodeMaster(Parcel in) {
        this.reasonCode = (Integer) in.readValue(Integer.class.getClassLoader());
        this.reasonMessage = in.readString();
        this.subGroup = in.readString();
        this.reason_id = in.readInt();
        this.edsMasterDataAttributeResponse = in.readParcelable(EDSMasterDataAttributeResponse.class.getClassLoader());
    }

    public static final Creator<EDSReasonCodeMaster> CREATOR = new Creator<EDSReasonCodeMaster>() {
        @Override
        public EDSReasonCodeMaster createFromParcel(Parcel source) {
            return new EDSReasonCodeMaster(source);
        }

        @Override
        public EDSReasonCodeMaster[] newArray(int size) {
            return new EDSReasonCodeMaster[size];
        }
    };

    public Integer getReason_id(){
        return reason_id;
    }

    public void setReason_id(Integer reason_id){
        this.reason_id = reason_id;
    }
}
