package in.ecomexpress.sathi.repo.remote.model.masterdata;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "EdsActivityMasterTable")
public class MasterActivityData implements Parcelable, Comparable<MasterActivityData> {
    @PrimaryKey
    @NonNull
    @JsonProperty("code")
    public String code;
    @JsonProperty("verification_mode")
    public String verificationMode;
    @JsonProperty("instruction")
    public String instructions="";
    @Embedded
    @JsonProperty("image_settings")
    public ImageSetting imageSettings = new ImageSetting();
    @JsonProperty("activity_question")
    public String activityQuestion;
    @JsonProperty("activity_name")
    public String activityName;

    @NonNull
    public String getCode() {
        return code;
    }

    public void setCode(@NonNull String code) {
        this.code = code;
    }

    public String getVerificationMode() {
        return verificationMode;
    }

    public void setVerificationMode(String verificationMode) {
        this.verificationMode = verificationMode;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    @JsonProperty("image_settings")
    public ImageSetting getImageSettings() {
        return imageSettings;
    }
    @JsonProperty("image_settings")
    public void setImageSettings(ImageSetting imageSettings) {
        this.imageSettings = imageSettings;
    }

    public String getActivityQuestion() {
        return activityQuestion;
    }

    public void setActivityQuestion(String activityQuestion) {
        this.activityQuestion = activityQuestion;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public MasterActivityData() {
    }


    @Override
    public int compareTo(@NonNull MasterActivityData masterActivityData) {
        if (getCode() == null || masterActivityData.getCode() == null) {
            return 0;
        }
        return getCode().compareTo(masterActivityData.getCode());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.code);
        dest.writeString(this.verificationMode);
        dest.writeString(this.instructions);
        dest.writeParcelable(this.imageSettings, flags);
        dest.writeString(this.activityQuestion);
        dest.writeString(this.activityName);
    }

    protected MasterActivityData(Parcel in) {
        this.code = in.readString();
        this.verificationMode = in.readString();
        this.instructions = in.readString();
        this.imageSettings = in.readParcelable(ImageSetting.class.getClassLoader());
        this.activityQuestion = in.readString();
        this.activityName = in.readString();
    }

    public static final Creator<MasterActivityData> CREATOR = new Creator<MasterActivityData>() {
        @Override
        public MasterActivityData createFromParcel(Parcel source) {
            return new MasterActivityData(source);
        }

        @Override
        public MasterActivityData[] newArray(int size) {
            return new MasterActivityData[size];
        }
    };
}
