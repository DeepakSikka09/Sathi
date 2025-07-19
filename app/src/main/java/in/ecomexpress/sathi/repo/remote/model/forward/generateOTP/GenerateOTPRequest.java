package in.ecomexpress.sathi.repo.remote.model.forward.generateOTP;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sandeep on 6/4/20.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerateOTPRequest {



    @JsonProperty("awb_number")
    private String awb_number;

    @JsonProperty("drs_id")
    public String getDrs_id() {
        return drs_id;
    }

    @JsonProperty("drs_id")
    public void setDrs_id(String drs_id) {
        this.drs_id = drs_id;
    }

    @JsonProperty("drs_id")
    private String drs_id;


    public String getAwb_number() {
        return awb_number;
    }

    public void setAwb_number(String awb_number) {
        this.awb_number = awb_number;
    }

    @Override
    public String toString()
    {
        return "GenerateOTPRequest [awb_number = "+awb_number+"]";
    }

}
