package in.ecomexpress.sathi.repo.remote.model.payphi.payment_link_sms;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by anshika on 11/4/20.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PaymentSmsLinkRequset {


    public PaymentSmsLinkRequset(String employee_code ,String awb, String alternate_mobile_number,String drs_id) {
        this.awb = awb;
        this.employee_code = employee_code;
        this.alternate_mobile_number = alternate_mobile_number;
        this.drs_id=drs_id;

    }

    @JsonProperty("awb_number")
    private String awb;

    @JsonProperty("alternate_mobile_number")
    private String alternate_mobile_number;

    @JsonProperty("drs_id")
    private String drs_id;

    @JsonProperty("employee_code")
    private String employee_code;

    public String getAwb() {
        return awb;
    }

    public void setAwb(String awb) {
        this.awb = awb;
    }

    public String getAlternate_mobile_number() {
        return alternate_mobile_number;
    }

    public void setAlternate_mobile_number(String alternate_mobile_number) {
        this.alternate_mobile_number = alternate_mobile_number;
    }

    public void setDrs_id(String drs_id) {
        this.drs_id = drs_id;
    }

    public String getDrs_id() {
        return drs_id;
    }

    public String getEmployee_code(){
        return employee_code;
    }

    public void setEmployee_code(String employee_code){
        this.employee_code = employee_code;
    }
    //    @Override
//    public String toString() {
//        return "AwbRequest [awb = " + awb + ", alternate_mobile_number = " + alternate_mobile_number + "]";
//    }


}

