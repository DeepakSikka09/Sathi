
package in.ecomexpress.sathi.repo.remote.model.drs_list.common;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AmazonOtpRequest implements Parcelable {

    @JsonProperty("awb_number")
    private String awb_no;


    public AmazonOtpRequest(String awb_no) {
        this.awb_no = awb_no;
    }


    protected AmazonOtpRequest(Parcel in) {
        awb_no = in.readString();
    }

    public static final Creator<AmazonOtpRequest> CREATOR = new Creator<AmazonOtpRequest>() {
        @Override
        public AmazonOtpRequest createFromParcel(Parcel in) {
            return new AmazonOtpRequest(in);
        }

        @Override
        public AmazonOtpRequest[] newArray(int size) {
            return new AmazonOtpRequest[size];
        }
    };

    public String getAwb_no() {
        return awb_no;
    }

    public void setAwb_no(String awb_no) {
        this.awb_no = awb_no;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(awb_no);
    }
}
