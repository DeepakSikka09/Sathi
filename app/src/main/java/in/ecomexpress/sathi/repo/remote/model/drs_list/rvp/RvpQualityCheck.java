package in.ecomexpress.sathi.repo.remote.model.drs_list.rvp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(indices = {@Index(value = {"awbNo", "drs", "qcCode", "qcType", "qcValue"}, unique = true)})
public class RvpQualityCheck implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @JsonProperty("id")
    private long id;
    @JsonProperty("awb_no")
    private long awbNo;
    @JsonProperty("drs_id")
    private Integer drs;
    @JsonProperty("qc_code")
    private String qcCode;
    @JsonProperty("qc_type")
    private String qcType;
    @JsonProperty("qc_value")
    private String qcValue;
    @JsonProperty("qc_name")
    private String qcName;
    @JsonProperty("image_capture_settings")
    private String imageCaptureSettings = "O";
    @JsonProperty("instructions")
    private String instructions;
    @JsonProperty("is_optional")
    private String isOptional;

    public String getIsOptional() {
        return isOptional;
    }

    public void setIsOptional(String isOptional) {
        this.isOptional = isOptional;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getQcName() {
        return qcName;
    }

    public void setQcName(String qcName) {
        this.qcName = qcName;
    }


    public RvpQualityCheck() {
    }

    protected RvpQualityCheck(Parcel in) {
        id = in.readLong();
        awbNo = in.readLong();
        qcCode = in.readString();
        qcType = in.readString();
        qcValue = in.readString();
        drs = in.readInt();
        imageCaptureSettings = in.readString();
        qcName = in.readString();
        instructions = in.readString();
        isOptional = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(awbNo);
        dest.writeString(qcCode);
        dest.writeString(qcType);
        dest.writeString(qcValue);
        dest.writeInt(drs);
        dest.writeString(imageCaptureSettings);
        dest.writeString(qcName);
        dest.writeString(instructions);
        dest.writeString(isOptional);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RvpQualityCheck> CREATOR = new Creator<RvpQualityCheck>() {
        @Override
        public RvpQualityCheck createFromParcel(Parcel in) {
            return new RvpQualityCheck(in);
        }

        @Override
        public RvpQualityCheck[] newArray(int size) {
            return new RvpQualityCheck[size];
        }
    };

    @JsonProperty("qc_code")
    public String getQcCode() {
        return qcCode;
    }

    @JsonProperty("qc_code")
    public void setQcCode(String qcCode) {
        this.qcCode = qcCode;
    }

    @JsonProperty("qc_type")
    public String getQcType() {
        return qcType;
    }

    @JsonProperty("qc_type")
    public void setQcType(String qcType) {
        this.qcType = qcType;
    }

    @JsonProperty("id")
    public long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(long id) {
        this.id = id;
    }

    public long getAwbNo() {
        return awbNo;
    }

    public void setAwbNo(long awbNo) {
        this.awbNo = awbNo;
    }

    public Integer getDrs() {
        return drs;
    }

    public void setDrs(Integer drs) {
        this.drs = drs;
    }

    @NonNull
    @Override
    public String toString() {
        return "RvpQualityCheck{" +
                "id=" + id +
                ", awbNo=" + awbNo +
                ", drs=" + drs +
                ", qcCode='" + qcCode + '\'' +
                ", qcType='" + qcType + '\'' +
                ", qcValue='" + qcValue + '\'' +
                ", imageCaptureSettings='" + imageCaptureSettings + '\'' +
                ", qcName='" + qcName + '\'' +
                ", instructions='" + instructions + '\'' +
                ", isOptional='" + isOptional + '\'' +
                '}';
    }

    public void setQcValue(String qcValue) {
        this.qcValue = qcValue;
    }

    public String getQcValue() {
        return qcValue;
    }

    public String getImageCaptureSettings() {
        return imageCaptureSettings;
    }

    public void setImageCaptureSettings(String imageCaptureSettings) {
        this.imageCaptureSettings = imageCaptureSettings;
    }
}