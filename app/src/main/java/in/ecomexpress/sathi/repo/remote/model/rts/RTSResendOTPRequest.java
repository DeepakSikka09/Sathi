package in.ecomexpress.sathi.repo.remote.model.rts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RTSResendOTPRequest {

    public ArrayList<String> drs_ids;
    public String employee_code;
    public String shipper_id;
    public String sub_shipper_id;
    public String sub_shipper_mobile;
    public String phone;
    public boolean otp_resend_email;
    private String message_type;

    public ArrayList<String> getDrs_ids(){
        return drs_ids;
    }

    public void setDrs_ids(ArrayList<String> drs_ids){
        this.drs_ids = drs_ids;
    }

    public String getEmployee_code(){
        return employee_code;
    }

    public void setEmployee_code(String employee_code){
        this.employee_code = employee_code;
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

    public String getPhone(){
        return phone;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public boolean isOtp_resend_email(){
        return otp_resend_email;
    }

    public void setOtp_resend_email(boolean otp_resend_email){
        this.otp_resend_email = otp_resend_email;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }
}
