package in.ecomexpress.sathi.repo.remote.model.consignee_profile;


import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by shivangis on 5/17/2019.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsigneeProfileResponse implements Parcelable {



    @JsonProperty("status")
    private boolean status;

    @JsonProperty("description")
    private String description;

    @JsonProperty("code")
    private String code;

    @JsonProperty("response")
    private ConsigneeResponse response;

    @JsonProperty("status")
    public boolean isStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(boolean status) {
        this.status = status;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(String code) {
        this.code = code;
    }


    @JsonProperty("response")
    public ConsigneeResponse getResponse() {
        return response;
    }

    @JsonProperty("response")
    public void setResponse(ConsigneeResponse response) {
        this.response = response;
    }


    @Override
    public String toString() {
        return "ConsigneeProfileResponse [code = " + code + ", response = " + response + ", description = " + description + ", status = " + status + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.status ? (byte) 1 : (byte) 0);
        dest.writeString(this.description);
        dest.writeString(this.code);
        dest.writeParcelable(this.response, flags);
    }

    public ConsigneeProfileResponse() {
    }

    protected ConsigneeProfileResponse(Parcel in) {
        this.status = in.readByte() != 0;
        this.description = in.readString();
        this.code = in.readString();
        this.response = in.readParcelable(ConsigneeResponse.class.getClassLoader());
    }

    public static final Creator<ConsigneeProfileResponse> CREATOR = new Creator<ConsigneeProfileResponse>() {
        @Override
        public ConsigneeProfileResponse createFromParcel(Parcel source) {
            return new ConsigneeProfileResponse(source);
        }

        @Override
        public ConsigneeProfileResponse[] newArray(int size) {
            return new ConsigneeProfileResponse[size];
        }
    };
}
