package in.ecomexpress.sathi.repo.remote.model.eds;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CashReceipt_Request {
    @JsonProperty("awb_number")
    public String awb_number;
    @JsonProperty("emp_code")
    public String emp_code;


    public CashReceipt_Request(String awb_number, String emp_code)
    {
        this.awb_number = awb_number;
        this.emp_code =emp_code;
    }

}



