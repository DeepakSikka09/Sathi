package in.ecomexpress.sathi.repo.local.data.eds;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EDSActivityResponseWizard implements Parcelable {

    @JsonProperty("activity_id")
    private String activityId = null;
    @JsonProperty("code")
    private String code = null;
    @JsonProperty("input_value")
    private String input_value = null;
    @JsonProperty("is_done")
    private String isDone = null;
    @JsonProperty("input_remark")
    private String inputRemark = null;
    @JsonProperty("additional_info")
    private List<EDSCommitAdditionalInfo> additionalInfos;

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInput_value() {
        return input_value;
    }

    public void setInput_value(String input_value) {
        this.input_value = input_value;
    }

    public String getIsDone() {
        return isDone;
    }

    public void setIsDone(String isDone) {
        this.isDone = isDone;
    }

    public String getInputRemark() {
        return inputRemark;
    }

    public void setInputRemark(String inputRemark) {
        this.inputRemark = inputRemark;
    }

    public List<EDSCommitAdditionalInfo> getAdditionalInfos() {
        return additionalInfos;
    }

    public void setAdditionalInfos(List<EDSCommitAdditionalInfo> additionalInfos) {
        this.additionalInfos = additionalInfos;
    }



    public EDSActivityResponseWizard() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.activityId);
        dest.writeString(this.code);
        dest.writeString(this.input_value);
        dest.writeString(this.isDone);
        dest.writeString(this.inputRemark);
        dest.writeTypedList(this.additionalInfos);
    }

    protected EDSActivityResponseWizard(Parcel in) {
        this.activityId = in.readString();
        this.code = in.readString();
        this.input_value = in.readString();
        this.isDone = in.readString();
        this.inputRemark = in.readString();
        this.additionalInfos = in.createTypedArrayList(EDSCommitAdditionalInfo.CREATOR);
    }

    public static final Creator<EDSActivityResponseWizard> CREATOR = new Creator<EDSActivityResponseWizard>() {
        @Override
        public EDSActivityResponseWizard createFromParcel(Parcel source) {
            return new EDSActivityResponseWizard(source);
        }

        @Override
        public EDSActivityResponseWizard[] newArray(int size) {
            return new EDSActivityResponseWizard[size];
        }
    };
}
