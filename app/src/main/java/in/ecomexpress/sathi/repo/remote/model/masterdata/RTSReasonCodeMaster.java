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
@Entity(tableName = "RTSMasterReasonCode")
public class RTSReasonCodeMaster implements Parcelable {
    @PrimaryKey
    @NonNull
    @JsonProperty("reason_code")
    private Integer reasonCode;

    @JsonProperty("reason_id")
    private Integer reason_id;
    @JsonProperty("reason_msg")
    private String reasonMessage;

    @JsonProperty("group_code")
    private String groupCode;

    @JsonProperty("attribute")
    @Embedded
    private MasterDataAttributeResponse masterDataAttributeResponse = new MasterDataAttributeResponse();

    @JsonProperty("reason_code")
    public Integer getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(Integer reasonCode) {
        this.reasonCode = reasonCode;
    }

    @JsonProperty("reason_msg")
    public String getReasonMessage() {
        return reasonMessage;
    }

    public void setReasonMessage(String reasonMessage) {
        this.reasonMessage = reasonMessage;
    }
    @JsonProperty("group_code")
    public String getGroupCode() {
        return groupCode;
    }

    @JsonProperty("group_code")
    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    @JsonProperty("attribute")
    public MasterDataAttributeResponse getMasterDataAttributeResponse() {
        return masterDataAttributeResponse;
    }

    public void setMasterDataAttributeResponse(MasterDataAttributeResponse masterDataAttributeResponse) {
        this.masterDataAttributeResponse = masterDataAttributeResponse;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.reasonCode);
        dest.writeString(this.reasonMessage);
        dest.writeString(this.groupCode);
        dest.writeInt(this.reason_id);
        dest.writeParcelable(this.masterDataAttributeResponse, flags);
    }

    public RTSReasonCodeMaster() {
    }

    protected RTSReasonCodeMaster(Parcel in) {
        this.reasonCode = (Integer) in.readValue(Integer.class.getClassLoader());
        this.reasonMessage = in.readString();
        this.groupCode = in.readString();
        this.reason_id = in.readInt();
        this.masterDataAttributeResponse = in.readParcelable(MasterDataAttributeResponse.class.getClassLoader());
    }

    public static final Creator<RTSReasonCodeMaster> CREATOR = new Creator<RTSReasonCodeMaster>() {
        @Override
        public RTSReasonCodeMaster createFromParcel(Parcel source) {
            return new RTSReasonCodeMaster(source);
        }

        @Override
        public RTSReasonCodeMaster[] newArray(int size) {
            return new RTSReasonCodeMaster[size];
        }
    };

    public Integer getReason_id(){
        return reason_id;
    }

    public void setReason_id(Integer reason_id){
        this.reason_id = reason_id;
    }
}
