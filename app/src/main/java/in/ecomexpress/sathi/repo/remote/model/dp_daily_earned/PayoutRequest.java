package in.ecomexpress.sathi.repo.remote.model.dp_daily_earned;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sandeep on 29/6/20.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayoutRequest {

    @JsonProperty("emp_code")
    private String emp_code;

    @JsonProperty("payout_date")
    private String payout_date;

    public void setEmp_code(String emp_code) {
        this.emp_code = emp_code;
    }

    public String getEmp_code()
    {
        return emp_code;
    }

    public String getPayout_date() {
        return payout_date;
    }

    public void setPayout_date(String payout_date) {
        this.payout_date = payout_date;
    }
}
