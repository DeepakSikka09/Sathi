package in.ecomexpress.sathi.repo.remote.model.attendance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sandeep on 17-12-2018.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttendanceRequest {

    @JsonProperty("emp_id")
    private String emp_id;
    @JsonProperty("month")
    private String month;
    @JsonProperty("year")
    private String year;

    public AttendanceRequest(String emp_id, String month, String year) {

        this.emp_id = emp_id;
        this.month = month;
        this.year = year;
    }

    @JsonProperty("emp_id")
    public String getEmp_id() {
        return emp_id;
    }

    @JsonProperty("emp_id")
    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    @JsonProperty("month")
    public String getMonth() {
        return month;
    }

    @JsonProperty("month")
    public void setMonth(String month) {
        this.month = month;
    }

    @JsonProperty("year")
    public String getYear() {
        return year;
    }

    @JsonProperty("year")
    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "AttendanceRequest [emp_id = " + emp_id + ", month = " + month + ", year = " + year + "]";
    }
}
