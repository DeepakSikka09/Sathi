package in.ecomexpress.sathi.repo.remote.model.forward.verifyOTP;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sandeep on 6/4/20.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyOTPRequest {

    @JsonProperty("otp_value")
    private String otp;

    @JsonProperty("awb_number")
    private String awb;

    @JsonProperty("OTP_TYPE")
    private String otpType;

    @JsonProperty("drs_id")
    public String getDrs_id() {
        return drs_id;
    }

    @JsonProperty("drs_id")
    public void setDrs_id(String drs_id) {
        this.drs_id = drs_id;
    }

    @JsonProperty("drs_id")
    private String drs_id;

    @JsonProperty("otp_value")
    public String getOtp ()
    {
        return otp;
    }

    @JsonProperty("otp_value")
    public void setOtp (String otp)
    {
        this.otp = otp;
    }

    @JsonProperty("awb_number")
    public String getAwb ()
    {
        return awb;
    }

    @JsonProperty("awb_number")
    public void setAwb (String awb)
    {
        this.awb = awb;
    }

    @Override
    public String toString()
    {
        return "VerifyOTPRequest [otp = "+otp+", awb = "+awb+"]";
    }

    public String getOtpType(){
        return otpType;
    }

    public void setOtpType(String otpType){
        this.otpType = otpType;
    }
}
