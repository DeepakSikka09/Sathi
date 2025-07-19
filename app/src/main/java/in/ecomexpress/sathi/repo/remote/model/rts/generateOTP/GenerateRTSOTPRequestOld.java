package in.ecomexpress.sathi.repo.remote.model.rts.generateOTP;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerateRTSOTPRequestOld {

    @JsonProperty("awb_numbers")
    private ArrayList<String> awb_numbers;

    @JsonProperty("disputed_awbs")
    private ArrayList<String> disputed_awbs;

    @JsonProperty("emp_code")
    private String emp_code;

    @JsonProperty("sub_shipper_id")
    private String sub_shipper_id;

    @JsonProperty("sub_shipper_mobile")
    private String sub_shipper_mobile;

    public ArrayList<String> getAwb_numbers(){
        return awb_numbers;
    }

    public void setAwb_numbers(ArrayList<String> awb_numbers){
        this.awb_numbers = awb_numbers;
    }

    public ArrayList<String> getDisputed_Awb_numbers(){
        return disputed_awbs;
    }

    public void setDisputed_Awb_numbers(ArrayList<String> disputed_awbs){
        this.disputed_awbs = disputed_awbs;
    }

    public String getEmp_code(){
        return emp_code;
    }

    public void setEmp_code(String emp_code){
        this.emp_code = emp_code;
    }

    public String getSub_shipper_id(){
        return sub_shipper_id;
    }

    public void setSub_shipper_id(String sub_shipper_id){
        this.sub_shipper_id = sub_shipper_id;
    }

    public String getSub_shipper_mobile(){
        return sub_shipper_mobile;
    }

    public void setSub_shipper_mobile(String sub_shipper_mobile){
        this.sub_shipper_mobile = sub_shipper_mobile;
    }
}
