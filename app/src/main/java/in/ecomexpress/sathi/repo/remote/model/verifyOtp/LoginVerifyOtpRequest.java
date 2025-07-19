package in.ecomexpress.sathi.repo.remote.model.verifyOtp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dhananjayk on 29-01-2019.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginVerifyOtpRequest {

    @JsonProperty("employee_code")
    private String employeeCode;
    @JsonProperty("otp")
    private String otp;
    @JsonProperty("nOtp")
    private boolean nOtp;
    @JsonProperty("status")
    private String status;
    public LoginVerifyOtpRequest(String employeeCode, String otp,boolean nOtp, String status) {
        this.employeeCode = employeeCode;
        this.otp = otp;
        this.nOtp = nOtp;
        this.status = status;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public boolean getnOtp(){
        return nOtp;
    }

    public void setnOtp(boolean nOtp){
        this.nOtp = nOtp;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }
}
