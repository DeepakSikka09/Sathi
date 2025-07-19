package in.ecomexpress.sathi.repo.remote.model.otp.resendotp;

import androidx.room.PrimaryKey;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ResendOtpRequest {

    public String getAwb() {
        return awb;
    }

    public void setAwb(String awb) {
        this.awb = awb;
    }

    @PrimaryKey
    @JsonProperty("awb")
    private String awb;

    @JsonProperty("message_type")
    private String message_type;

    @JsonProperty("alternate_number")
    private Boolean alternate_number;

    @JsonProperty("drs_id")
    private String drs_id;

    public ResendOtpRequest(String awb, String message_type,String drs_id, Boolean alternate_number) {
        this.awb = awb;
        this.message_type = message_type;
        this.drs_id = drs_id;
        this.alternate_number = alternate_number;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }
}