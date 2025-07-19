package in.ecomexpress.sathi.repo.remote.model.login;

import com.fasterxml.jackson.annotation.JsonProperty;

import in.ecomexpress.sathi.repo.remote.model.base.ApiRequest;

public class CheckAttendanceRequest extends ApiRequest {
    @JsonProperty("emp_code")
    private String emp_code;


    public CheckAttendanceRequest(String emp_code) {

        this.emp_code = emp_code;
    }

    public String getEmp_code() {
        return emp_code;
    }

    public void setEmp_code(String emp_code) {
        this.emp_code = emp_code;
    }
}
