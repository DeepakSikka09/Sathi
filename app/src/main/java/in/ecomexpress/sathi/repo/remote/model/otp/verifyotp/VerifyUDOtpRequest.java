package in.ecomexpress.sathi.repo.remote.model.otp.verifyotp;



import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VerifyUDOtpRequest {

    @PrimaryKey
    @JsonProperty("awb_number")
    private String awb_number;

    @JsonProperty("drs_id")
    private String drs_id;

    @JsonProperty("message_type")
    private String message_type;

    @JsonProperty("otp_value")
    private String otp_value;


    public VerifyUDOtpRequest(String awb_number, String drs_id, String message_type, String otp_value) {
        this.awb_number = awb_number;
        this.drs_id = drs_id;
        this.message_type = message_type;
        this.otp_value = otp_value;

    }


}
