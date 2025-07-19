package in.ecomexpress.sathi.repo.remote.model.covid;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by anshika on 24/7/19.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CovidRequest {

    @JsonProperty("emp_code")
    private String emp_code;

    @JsonProperty("emp_covid_consent")
    private String emp_covid_consent;


    public String getEmp_code() {
        return emp_code;
    }

    public void setEmp_code(String emp_code) {
        this.emp_code = emp_code;
    }

    public String getEmp_covid_consent() {
        return emp_covid_consent;
    }

    public void setEmp_covid_consent(String emp_covid_consent) {
        this.emp_covid_consent = emp_covid_consent;
    }
}

