package in.ecomexpress.sathi.repo.remote.model.fuel;

import com.fasterxml.jackson.annotation.JsonProperty;


public class FuelReimbursementRequest  {

    @JsonProperty("employee_code")
    private String employee_code;

    @JsonProperty("to_date")
    private String to_date;

    @JsonProperty("from_date")
    private String from_date;

    public FuelReimbursementRequest(String employee_code, String to_date, String from_date) {
        this.employee_code = employee_code;
        this.to_date = to_date;
        this.from_date = from_date;
    }

    @JsonProperty("employee_code")
    public String getEmployee_code() {
        return employee_code;
    }

    @JsonProperty("employee_code")
    public void setEmployee_code(String employee_code) {
        this.employee_code = employee_code;
    }

    @JsonProperty("to_date")
    public String getTo_date() {
        return to_date;
    }

    @JsonProperty("to_date")
    public void setTo_date(String to_date) {
        this.to_date = to_date;
    }

    @JsonProperty("from_date")
    public String getFrom_date() {
        return from_date;
    }

    @JsonProperty("from_date")
    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    @Override
    public String toString() {
        return "FuelReimbursementRequest [employee_code = " + employee_code + ", to_date = " + to_date + ", from_date = " + from_date + "]";
    }


}
