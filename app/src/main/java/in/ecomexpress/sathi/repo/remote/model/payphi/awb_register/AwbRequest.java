package in.ecomexpress.sathi.repo.remote.model.payphi.awb_register;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by shivangis on 1/8/2019.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AwbRequest {


    public AwbRequest(String awb, String fe_emp_code,String drs_id) {
        this.awb = awb;
        this.fe_emp_code = fe_emp_code;
        this.drs_id=drs_id;
    }

    @JsonProperty("awb")
    private String awb;

    @JsonProperty("fe_emp_code")
    private String fe_emp_code;

    @JsonProperty("drs_id")
    private String drs_id;

    public String getAwb() {
        return awb;
    }

    public void setAwb(String awb) {
        this.awb = awb;
    }

    public String getFe_emp_code() {
        return fe_emp_code;
    }

    public void setFe_emp_code(String fe_emp_code) {
        this.fe_emp_code = fe_emp_code;
    }

    public void setDrs_id(String drs_id) {
        this.drs_id = drs_id;
    }

    public String getDrs_id() {
        return drs_id;
    }

    @Override
    public String toString() {
        return "AwbRequest [awb = " + awb + ", fe_emp_code = " + fe_emp_code + "]";
    }


}
