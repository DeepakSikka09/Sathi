package in.ecomexpress.sathi.repo.remote.model.trip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by anshika on 24/7/19.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdharRequest {

    @JsonProperty("aadhar_consent")
    private String aadhar_consent;

    @JsonProperty("emp_code")
    private String user_name;

    public String getAadhar_consent() {
        return aadhar_consent;
    }

    public void setAadhar_consent(String aadhar_consent) {
        this.aadhar_consent = aadhar_consent;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}

