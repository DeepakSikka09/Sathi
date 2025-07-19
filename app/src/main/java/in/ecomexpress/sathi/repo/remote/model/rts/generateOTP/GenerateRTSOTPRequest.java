package in.ecomexpress.sathi.repo.remote.model.rts.generateOTP;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerateRTSOTPRequest {

    private ArrayList<String> awb_numbers = new ArrayList<>();
    private  AwbList awb_lists = new AwbList();
    private String emp_code;
    private String sub_shipper_id;
    private String sub_shipper_mobile;
    private String message_type;


    public ArrayList<String> getAwb_numbers(){
        return awb_numbers;
    }

    public void setAwb_numbers(ArrayList<String> awb_numbers){
        this.awb_numbers = awb_numbers;
    }

    public AwbList getAwb_lists(){
        return awb_lists;
    }

    public void setAwb_lists(AwbList awb_lists){
        this.awb_lists = awb_lists;
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

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }
}

