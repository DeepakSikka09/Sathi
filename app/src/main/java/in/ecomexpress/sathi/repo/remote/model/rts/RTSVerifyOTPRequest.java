package in.ecomexpress.sathi.repo.remote.model.rts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RTSVerifyOTPRequest {

    public ArrayList<String> drs_ids;
    public String otp;
    public String shipper_id;
    public String sub_shipper_id;
    public String sub_shipper_mobile;
    public String employee_code;

    public ArrayList<String> getDrs_ids(){
        return drs_ids;
    }

    public void setDrs_ids(ArrayList<String> drs_ids){
        this.drs_ids = drs_ids;
    }


    public String getShipper_id(){
        return shipper_id;
    }

    public void setShipper_id(String shipper_id){
        this.shipper_id = shipper_id;
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

    public String getOtp(){
        return otp;
    }

    public void setOtp(String otp){
        this.otp = otp;
    }

    public String getEmployee_code(){
        return employee_code;
    }

    public void setEmployee_code(String employee_code){
        this.employee_code = employee_code;
    }
}
