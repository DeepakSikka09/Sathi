package in.ecomexpress.sathi.repo.remote.model.consignee_profile;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by shivangis on 5/17/2019.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsigneeResponse implements Parcelable {

    @JsonProperty("consignee_profiles")
    private ConsigneeProfiles consignee_profiles;

    @JsonProperty("consignee_profiles")
    public ConsigneeProfiles getConsignee_profiles() {
        return consignee_profiles;
    }

    @JsonProperty("consignee_profiles")
    public void setConsignee_profiles(ConsigneeProfiles consignee_profiles) {
        this.consignee_profiles = consignee_profiles;
    }

    @Override
    public String toString() {
        return "ConsigneeResponse [consignee_profiles = " + consignee_profiles + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.consignee_profiles, flags);
    }

    public ConsigneeResponse() {
    }

    protected ConsigneeResponse(Parcel in) {
        this.consignee_profiles = in.readParcelable(ConsigneeProfiles.class.getClassLoader());
    }

    public static final Creator<ConsigneeResponse> CREATOR = new Creator<ConsigneeResponse>() {
        @Override
        public ConsigneeResponse createFromParcel(Parcel source) {
            return new ConsigneeResponse(source);
        }

        @Override
        public ConsigneeResponse[] newArray(int size) {
            return new ConsigneeResponse[size];
        }
    };
}
