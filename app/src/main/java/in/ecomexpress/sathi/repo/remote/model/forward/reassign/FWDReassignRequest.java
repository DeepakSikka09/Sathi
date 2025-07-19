package in.ecomexpress.sathi.repo.remote.model.forward.reassign;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FWDReassignRequest {

    @JsonProperty("employee_code")
    private String emp_code;

    @JsonProperty("airwaybill_number")
    private long awb;

    @JsonProperty("old_drs_id")
    private String old_drs_id;

    public String getEmp_code(){
        return emp_code;
    }

    public void setEmp_code(String emp_code){
        this.emp_code = emp_code;
    }

    public long getAwb(){
        return awb;
    }

    public void setAwb(long awb){
        this.awb = awb;
    }

    public String getOld_drs_id() {
        return old_drs_id;
    }

    public void setOld_drs_id(String old_drs_id) {
        this.old_drs_id = old_drs_id;
    }
}
