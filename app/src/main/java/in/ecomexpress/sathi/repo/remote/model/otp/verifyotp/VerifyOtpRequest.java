package in.ecomexpress.sathi.repo.remote.model.otp.verifyotp;



import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VerifyOtpRequest {

    @PrimaryKey
    @JsonProperty("awb")
    private String awb;

    @JsonProperty("product_type")
    private String productType;

    @JsonProperty("OTP_TYPE")
    private String otpType;

    @JsonProperty("verify_type")
    public String getRequest_type() {
        return request_type;
    }

    @JsonProperty("verify_type")
    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    @JsonProperty("verify_type")
    private String request_type;

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getAwb() {
        return awb;
    }

    public void setAwb(String awb) {
        this.awb = awb;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    @JsonProperty("otp")
    private String otp;


    public VerifyOtpRequest(String awb, String otp, String productType, String request_type ,String otpType) {
        this.awb = awb;
        this.otp = otp;
        this.productType = productType;
        this.request_type = request_type;
        this.otpType = otpType;
    }

    public String getOtpType(){
        return otpType;
    }

    public void setOtpType(String otpType){
        this.otpType = otpType;
    }
}
