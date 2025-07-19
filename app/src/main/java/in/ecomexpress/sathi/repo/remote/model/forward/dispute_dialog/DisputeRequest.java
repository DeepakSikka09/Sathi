package in.ecomexpress.sathi.repo.remote.model.forward.dispute_dialog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by anshika on 24/7/19.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DisputeRequest {

    @JsonProperty("alternate_mobile_number")
    private String alternate_mobile_number;

    @JsonProperty("awb_number")
    private String awb_number;

    @JsonProperty("drs_id")
    private String drs_id;

    public String getAlternate_mobile_number() {
        return alternate_mobile_number;
    }

    public void setAlternate_mobile_number(String alternate_mobile_number) {
        this.alternate_mobile_number = alternate_mobile_number;
    }

    public String getAwb_number() {
        return awb_number;
    }

    public void setAwb_number(String awb_number) {
        this.awb_number = awb_number;
    }

    public String getDrs_id() {
        return drs_id;
    }

    public void setDrs_id(String drs_id) {
        this.drs_id = drs_id;
    }
}

