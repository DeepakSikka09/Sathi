package in.ecomexpress.sathi.repo.local.data.fwd;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeRescheduleInfo implements Parcelable{
    @JsonProperty("reschedule_date")
    private String rescheduleDate = null;
    @JsonProperty("reschedule_slot")
    private String rescheduleSlot = null;
    @JsonProperty("reschedule_fe_comments")
    private String rescheduleFeComments = null;

    public String getRescheduleDate() {
        return rescheduleDate;
    }

    public void setRescheduleDate(String rescheduleDate) {
        this.rescheduleDate = rescheduleDate;
    }

    public String getRescheduleSlot() {
        return rescheduleSlot;
    }

    public void setRescheduleSlot(String rescheduleSlot) {
        this.rescheduleSlot = rescheduleSlot;
    }

    public String getRescheduleFeComments() {
        return rescheduleFeComments;
    }

    public void setRescheduleFeComments(String rescheduleFeComments) {
        this.rescheduleFeComments = rescheduleFeComments;
    }

    public FeRescheduleInfo() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.rescheduleDate);
        dest.writeString(this.rescheduleSlot);
        dest.writeString(this.rescheduleFeComments);
    }

    protected FeRescheduleInfo(Parcel in) {
        this.rescheduleDate = in.readString();
        this.rescheduleSlot = in.readString();
        this.rescheduleFeComments = in.readString();
    }

    public static final Creator<FeRescheduleInfo> CREATOR = new Creator<FeRescheduleInfo>() {
        @Override
        public FeRescheduleInfo createFromParcel(Parcel source) {
            return new FeRescheduleInfo(source);
        }

        @Override
        public FeRescheduleInfo[] newArray(int size) {
            return new FeRescheduleInfo[size];
        }
    };
}
