package in.ecomexpress.sathi.repo.remote.model.mps;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RvpMpsQualityCheck implements Parcelable {

    private long idNumber;
    private String item;

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    @JsonProperty("id")
    private long id;

    public long getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(long idNumber) {
        this.idNumber = idNumber;
    }

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

    @JsonProperty("image_capture_settings")
    private String imageCaptureSettings = "O";
    @JsonProperty("instructions")
    private String instructions;
    @JsonProperty("is_optional")
    private String isOptional;


    public RvpMpsQualityCheck() {
    }

    protected RvpMpsQualityCheck(Parcel in) {
        idNumber = in.readLong();
        id = in.readLong();
        awbNo = in.readLong();
        qcCode = in.readString();
        qcType = in.readString();
        qcValue = in.readString();
        drs = in.readInt();
        imageCaptureSettings = in.readString();
        instructions = in.readString();
        isOptional = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(idNumber);
        dest.writeLong(id);
        dest.writeString(qcCode);
        dest.writeString(qcType);
        dest.writeString(qcValue);
        dest.writeLong(awbNo);
        dest.writeInt(drs);
        dest.writeString(imageCaptureSettings);
        dest.writeString(instructions);
        dest.writeString(isOptional);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RvpMpsQualityCheck> CREATOR = new Creator<RvpMpsQualityCheck>() {
        @Override
        public RvpMpsQualityCheck createFromParcel(Parcel in) {
            return new RvpMpsQualityCheck(in);
        }

        @Override
        public RvpMpsQualityCheck[] newArray(int size) {
            return new RvpMpsQualityCheck[size];
        }
    };

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getIsOptional() {
        return isOptional;
    }

    public void setIsOptional(String isOptional) {
        this.isOptional = isOptional;
    }

    public String getImageCaptureSettings() {
        return imageCaptureSettings;
    }

    public void setImageCaptureSettings(String imageCaptureSettings) {
        this.imageCaptureSettings = imageCaptureSettings;
    }

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

    @Override
    public String toString() {
        return "RvpMpsQualityCheck{" +
                "idNumber=" + idNumber +
                ", id=" + id +
                ", awbNo=" + awbNo +
                ", drs=" + drs +
                ", qcCode='" + qcCode + '\'' +
                ", qcType='" + qcType + '\'' +
                ", qcValue='" + qcValue + '\'' +
                '}';
    }

    public void setQcValue(String qcValue) {
        this.qcValue = qcValue;
    }

    public String getQcValue() {
        return qcValue;
    }

}
