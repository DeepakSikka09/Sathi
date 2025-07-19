package in.ecomexpress.sathi.repo.remote.model.trip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by anshika on 24/7/19.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DrsCheckListRequest {

    @JsonProperty("employee_code")
    private String employee_code;

    @JsonProperty("employee_code")
    public String getEmployee_code ()
    {
        return employee_code;
    }

    @JsonProperty("employee_code")
    public void setEmployee_code (String employee_code)
    {
        this.employee_code = employee_code;
    }

    @Override
    public String toString()
    {
        return getEmployee_code();
    }
}

