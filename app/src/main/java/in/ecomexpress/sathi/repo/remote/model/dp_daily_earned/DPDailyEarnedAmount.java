package in.ecomexpress.sathi.repo.remote.model.dp_daily_earned;



import androidx.room.Embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sandeep on 29/6/20.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DPDailyEarnedAmount {

    @JsonProperty("emp_code")
    private String emp_code;

    @JsonProperty("code")
    private String code;

    @Embedded
    @JsonProperty("employee_performance")
    private Employee_performance employee_performance;

    @JsonProperty("description")
    private String description;

    @JsonProperty("status")
    private String status;

    @JsonProperty("emp_code")
    public String getEmp_code ()
    {
        return emp_code;
    }

    @JsonProperty("emp_code")
    public void setEmp_code (String emp_code)
    {
        this.emp_code = emp_code;
    }

    @JsonProperty("code")
    public String getCode ()
    {
        return code;
    }

    @JsonProperty("code")
    public void setCode (String code)
    {
        this.code = code;
    }

    @JsonProperty("employee_performance")
    public Employee_performance getEmployee_performance ()
    {
        return employee_performance;
    }

    @JsonProperty("employee_performance")
    public void setEmployee_performance (Employee_performance employee_performance)
    {
        this.employee_performance = employee_performance;
    }

    @JsonProperty("description")
    public String getDescription ()
    {
        return description;
    }

    @JsonProperty("description")
    public void setDescription (String description)
    {
        this.description = description;
    }

    @JsonProperty("status")
    public String getStatus ()
    {
        return status;
    }

    @JsonProperty("status")
    public void setStatus (String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "DPDailyEarnedAmount [emp_code = "+emp_code+", code = "+code+", employee_performance = "+employee_performance+", description = "+description+", status = "+status+"]";
    }

}
