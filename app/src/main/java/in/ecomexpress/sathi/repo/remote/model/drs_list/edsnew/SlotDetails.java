package in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SlotDetails implements Parcelable {
    @JsonProperty("start_time")
    public long getStart_time() {
        return start_time;
    }

    @JsonProperty("start_time")
    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    @JsonProperty("end_time")
    public long getEnd_time() {
        return end_time;
    }

    @JsonProperty("end_time")
    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public boolean isReminder_status() {
        return reminder_status;
    }

    public void setReminder_status(boolean reminder_status) {
        this.reminder_status = reminder_status;
    }

    public boolean isComplition_status() {
        return complition_status;
    }

    public void setComplition_status(boolean complition_status) {
        this.complition_status = complition_status;
    }

    @JsonProperty("start_time")
    private long start_time;

    @JsonProperty("end_time")
    private long end_time;

    public boolean reminder_status = false;
    public boolean complition_status = false;

    public SlotDetails() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.start_time);
        dest.writeLong(this.end_time);
        dest.writeByte(this.reminder_status ? (byte) 1 : (byte) 0);
        dest.writeByte(this.complition_status ? (byte) 1 : (byte) 0);
    }

    protected SlotDetails(Parcel in) {
        this.start_time = in.readLong();
        this.end_time = in.readLong();
        this.reminder_status = in.readByte() != 0;
        this.complition_status = in.readByte() != 0;
    }

    public static final Creator<SlotDetails> CREATOR = new Creator<SlotDetails>() {
        @Override
        public SlotDetails createFromParcel(Parcel source) {
            return new SlotDetails(source);
        }

        @Override
        public SlotDetails[] newArray(int size) {
            return new SlotDetails[size];
        }
    };
}