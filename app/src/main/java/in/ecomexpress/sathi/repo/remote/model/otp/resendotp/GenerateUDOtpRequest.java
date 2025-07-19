package in.ecomexpress.sathi.repo.remote.model.otp.resendotp;



import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenerateUDOtpRequest {


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

    @JsonProperty("product_type")
    private String product_type = null;

    @JsonProperty("alternate_number")
    private Boolean alternate_number;

    @JsonProperty("drs_id")
    private String drs_id;

    @JsonProperty("employee_code")
    private String employee_code;

    @JsonProperty("generate_otp")
    private Boolean generate_otp;

    public GenerateUDOtpRequest(String awb, String message_type,String drs_id, Boolean alternate_number,String employee_code,Boolean generate_otp, String product_type) {
        this.awb = awb;
        this.message_type = message_type;
        this.drs_id = drs_id;
        this.alternate_number = alternate_number;
        this.employee_code = employee_code;
        this.generate_otp = generate_otp;
        this.product_type = product_type;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public String getProduct_type(){
        return product_type;
    }

    public void setProduct_type(String product_type){
        this.product_type = product_type;
    }
}
