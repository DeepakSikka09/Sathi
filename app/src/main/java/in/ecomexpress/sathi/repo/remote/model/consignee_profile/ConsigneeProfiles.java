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
public class ConsigneeProfiles implements Parcelable {

    @JsonProperty("profile_found")
    public List<ProfileFound> getProfile_found() {
        return profile_found;
    }

    @JsonProperty("profile_found")
    public void setProfile_found(List<ProfileFound> profile_found) {
        this.profile_found = profile_found;
    }

    @JsonProperty("profile_found")
    private List<ProfileFound> profile_found = null;

    @JsonProperty("profile_not_found")
    public ProfileNotFound getProfile_not_found() {
        return profile_not_found;
    }

    @JsonProperty("profile_not_found")
    public void setProfile_not_found(ProfileNotFound profile_not_found) {
        this.profile_not_found = profile_not_found;
    }

    @JsonProperty("profile_not_found")
    private ProfileNotFound profile_not_found;

    @Override
    public String toString() {
        return "ConsigneeProfiles [profile_found = " + profile_found + ", profile_not_found = " + profile_not_found + "]";
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.profile_found);
        dest.writeParcelable(this.profile_not_found, flags);
    }

    public ConsigneeProfiles() {
    }

    protected ConsigneeProfiles(Parcel in) {
        this.profile_found = in.createTypedArrayList(ProfileFound.CREATOR);
        this.profile_not_found = in.readParcelable(ProfileNotFound.class.getClassLoader());
    }

    public static final Creator<ConsigneeProfiles> CREATOR = new Creator<ConsigneeProfiles>() {
        @Override
        public ConsigneeProfiles createFromParcel(Parcel source) {
            return new ConsigneeProfiles(source);
        }

        @Override
        public ConsigneeProfiles[] newArray(int size) {
            return new ConsigneeProfiles[size];
        }
    };
}
