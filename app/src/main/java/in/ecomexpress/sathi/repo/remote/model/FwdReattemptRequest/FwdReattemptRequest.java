package in.ecomexpress.sathi.repo.remote.model.FwdReattemptRequest;

import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FwdReattemptRequest {


    @PrimaryKey
    @JsonProperty("awb")
    private String awb;

    @JsonProperty("emp_code")
    private String emp_code;




    public FwdReattemptRequest(String awb, String emp_code) {
        this.awb = awb;
        this.emp_code = emp_code;


    }

    public String getAwb(){
        return awb;
    }

    public void setAwb(String awb){
        this.awb = awb;
    }

    public String getEmp_code(){
        return emp_code;
    }

    public void setEmp_code(String emp_code){
        this.emp_code = emp_code;
    }
}
