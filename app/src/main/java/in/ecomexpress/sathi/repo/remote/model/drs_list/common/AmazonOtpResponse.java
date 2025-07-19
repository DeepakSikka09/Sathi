
package in.ecomexpress.sathi.repo.remote.model.drs_list.common;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AmazonOtpResponse implements Parcelable {

    @JsonProperty("otp")
    private String otp;
    @JsonProperty("status")
    private boolean status;
    @JsonProperty("description")
    private String description;


    public AmazonOtpResponse() {}


    protected AmazonOtpResponse(Parcel in) {
        otp = in.readString();
        status = in.readByte() != 0;
        description = in.readString();
    }

    public static final Creator<AmazonOtpResponse> CREATOR = new Creator<AmazonOtpResponse>() {
        @Override
        public AmazonOtpResponse createFromParcel(Parcel in) {
            return new AmazonOtpResponse(in);
        }

        @Override
        public AmazonOtpResponse[] newArray(int size) {
            return new AmazonOtpResponse[size];
        }
    };

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(otp);
        parcel.writeByte((byte) (status ? 1 : 0));
        parcel.writeString(description);
    }
}
