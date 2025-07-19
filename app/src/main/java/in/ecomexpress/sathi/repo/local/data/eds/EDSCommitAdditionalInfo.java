package in.ecomexpress.sathi.repo.local.data.eds;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EDSCommitAdditionalInfo implements Parcelable {
    @JsonProperty("ques_id")
    private String quesId;
    @JsonProperty("ques_value")
    private String quesValue;

    public String getQuesId() {
        return quesId;
    }

    public void setQuesId(String quesId) {
        this.quesId = quesId;
    }

    public String getQuesValue() {
        return quesValue;
    }

    public void setQuesValue(String quesValue) {
        this.quesValue = quesValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.quesId);
        dest.writeString(this.quesValue);
    }

    public EDSCommitAdditionalInfo() {
    }

    protected EDSCommitAdditionalInfo(Parcel in) {
        this.quesId = in.readString();
        this.quesValue = in.readString();
    }

    public static final Creator<EDSCommitAdditionalInfo> CREATOR = new Creator<EDSCommitAdditionalInfo>() {
        @Override
        public EDSCommitAdditionalInfo createFromParcel(Parcel source) {
            return new EDSCommitAdditionalInfo(source);
        }

        @Override
        public EDSCommitAdditionalInfo[] newArray(int size) {
            return new EDSCommitAdditionalInfo[size];
        }
    };
}
