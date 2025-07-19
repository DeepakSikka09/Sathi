package in.ecomexpress.sathi.repo.remote.model.otp.otherNo;



import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dhananjayk on 12-02-2019.
 */

public class OtherNoRequest {


    public String getAwb() {
        return awb;
    }

    public void setAwb(String awb) {
        this.awb = awb;
    }

    @PrimaryKey
    @JsonProperty("awb")
    private String awb;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @JsonProperty("phone")
    private String phone;


    public OtherNoRequest(String awb, String phone) {
        this.awb = awb;
        this.phone = phone;

    }

}
