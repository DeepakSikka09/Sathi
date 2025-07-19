package in.ecomexpress.sathi.repo.remote.model.drs_list.forward;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import android.os.Parcel;
import android.os.Parcelable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class quality_checks implements Parcelable {

    @JsonProperty("qc_code")
    private String qcCode;

    @JsonProperty("qc_type")
    private String qcType;

    @JsonProperty("qc_value")
    private String qcValue;

    @JsonProperty("qc_name")
    private String qcName;

    @JsonProperty("image_capture_settings")
    private String imageCaptureSettings;

    @JsonProperty("id")
    private Integer  id;

    @JsonProperty("instructions")
    private String instructions;

    @JsonProperty("is_optional")
    private String is_optional;

    public quality_checks() {}

    protected quality_checks(Parcel in) {
        this.qcCode = ((String) in.readValue((String.class.getClassLoader())));
        this.qcType = ((String) in.readValue((String.class.getClassLoader())));
        this.qcValue = ((String) in.readValue((String.class.getClassLoader())));
        this.qcName = ((String) in.readValue((String.class.getClassLoader())));
        this.imageCaptureSettings = ((String) in.readValue((String.class.getClassLoader())));
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.instructions = ((String) in.readValue((String.class.getClassLoader())));
        this.is_optional = ((String) in.readValue((String.class.getClassLoader())));
    }

    public static final Creator<quality_checks> CREATOR = new Creator<quality_checks>() {
        @Override
        public quality_checks createFromParcel(Parcel in) {
            return new quality_checks(in);
        }

        @Override
        public quality_checks[] newArray(int size) {
            return new quality_checks[size];
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

    @JsonProperty("qc_value")
    public String getQcValue() {
        return qcValue;
    }

    @JsonProperty("qc_value")
    public void setQcValue(String qcValue) {
        this.qcValue = qcValue;
    }

    @JsonProperty("qc_name")
    public String getQcName() {
        return qcName;
    }

    @JsonProperty("qc_name")
    public void setQcName(String qcName) {
        this.qcName = qcName;
    }

    @JsonProperty("image_capture_settings")
    public String getImageCaptureSettings() {
        return imageCaptureSettings;
    }

    @JsonProperty("image_capture_settings")
    public void setImageCaptureSettings(String imageCaptureSettings) {
        this.imageCaptureSettings = imageCaptureSettings;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer  id) {
        this.id = id;
    }

    @JsonProperty("instructions")
    public String getInstructions() {
        return instructions;
    }

    @JsonProperty("instructions")
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeValue(qcCode);
        parcel.writeValue(qcType);
        parcel.writeValue(qcValue);
        parcel.writeValue(qcName);
        parcel.writeValue(imageCaptureSettings);
        parcel.writeValue(id);
        parcel.writeValue(instructions);
        parcel.writeValue(is_optional);
    }

    public String getIs_optional() {
        return is_optional;
    }

    public void setIs_optional(String is_optional) {
        this.is_optional = is_optional;
    }
}