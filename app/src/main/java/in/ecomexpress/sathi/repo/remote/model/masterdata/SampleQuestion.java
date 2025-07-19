package in.ecomexpress.sathi.repo.remote.model.masterdata;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


import io.reactivex.annotations.NonNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "RVPQCMasterTable" , indices = {@Index(value = {"code"}, unique = true)})
public class SampleQuestion implements Parcelable {

    @JsonProperty("code")
    private String code;
    @JsonProperty("verification_mode")
    private String verificationMode;
    @JsonProperty("name")
    private String name;
    @JsonProperty("image_capture_settings")
    private String imageCaptureSettings="O";
    @PrimaryKey
    @NonNull
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("instructions")
    private String instructions;
    private Boolean isSelected;

    public SampleQuestion() {
    }

    protected SampleQuestion(Parcel in) {
        code = in.readString();
        verificationMode = in.readString();
        name = in.readString();
        imageCaptureSettings = in.readString();
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        instructions = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(verificationMode);
        dest.writeString(name);
        dest.writeString(imageCaptureSettings);
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(instructions);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SampleQuestion> CREATOR = new Creator<SampleQuestion>() {
        @Override
        public SampleQuestion createFromParcel(Parcel in) {
            return new SampleQuestion(in);
        }

        @Override
        public SampleQuestion[] newArray(int size) {
            return new SampleQuestion[size];
        }
    };

    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("verification_mode")
    public String getVerificationMode() {
        return verificationMode;
    }

    public void setVerificationMode(String verificationMode) {
        this.verificationMode = verificationMode;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("image_capture_settings")
    public String getImageCaptureSettings() {
        return imageCaptureSettings;
    }

    public void setImageCaptureSettings(String imageCaptureSettings) {
        this.imageCaptureSettings = imageCaptureSettings;
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("instructions")
    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }
    public Boolean getIsSelected() {
        return isSelected;
    }


}
