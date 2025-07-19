package in.ecomexpress.sathi.repo.remote.model.consignee_profile;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shivangis on 5/17/2019.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileNotFound implements Parcelable {

    @JsonProperty("awb_numbers")
    public List<Long> getAwb_numbers() {
        return awb_numbers;
    }

    @JsonProperty("awb_numbers")
    public void setAwb_numbers(List<Long> awb_numbers) {
        this.awb_numbers = awb_numbers;
    }

    @JsonProperty("awb_numbers")
    private List<Long> awb_numbers;


    @Override
    public String toString() {
        return "ProfileNotFound [awb_numbers = " + awb_numbers + "]";
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.awb_numbers);
    }

    public ProfileNotFound() {
    }

    protected ProfileNotFound(Parcel in) {
        this.awb_numbers = new ArrayList<Long>();
        in.readList(this.awb_numbers, Long.class.getClassLoader());
    }

    public static final Creator<ProfileNotFound> CREATOR = new Creator<ProfileNotFound>() {
        @Override
        public ProfileNotFound createFromParcel(Parcel source) {
            return new ProfileNotFound(source);
        }

        @Override
        public ProfileNotFound[] newArray(int size) {
            return new ProfileNotFound[size];
        }
    };
}
