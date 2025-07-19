package in.ecomexpress.sathi.repo.remote.model.forward.verify_dispute_otp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by anshika on 24/7/19.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyDisputeRequest {

    @JsonProperty("awb_number")
    private String awb_number;

    @JsonProperty("drs_id")
    private String drs_id;

    @JsonProperty("otp_value")
    private String otp_value;

    @JsonProperty("payment_ref_number")
    private String payment_ref_number;

    @JsonProperty("sms_image_key")
    private String sms_image_key;


    public String getAwb_number() {
        return awb_number;
    }

    public void setAwb_number(String awb_number) {
        this.awb_number = awb_number;
    }

    public String getDrs_id() {
        return drs_id;
    }

    public void setDrs_id(String drs_id) {
        this.drs_id = drs_id;
    }

    public String getOtp_value() {
        return otp_value;
    }

    public void setOtp_value(String otp_value) {
        this.otp_value = otp_value;
    }

    public String getPayment_ref_number() {
        return payment_ref_number;
    }

    public void setPayment_ref_number(String payment_ref_number) {
        this.payment_ref_number = payment_ref_number;
    }

    public String getSms_image_key() {
        return sms_image_key;
    }

    public void setSms_image_key(String sms_image_key) {
        this.sms_image_key = sms_image_key;
    }
}

