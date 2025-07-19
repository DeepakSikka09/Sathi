package in.ecomexpress.sathi.repo.remote.model.rts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RTSResendOTPResponse {

    @JsonProperty("status")
    public boolean status;

    @JsonProperty("response")
    public Response response;

    @JsonProperty("description")
    public String description;
    @JsonProperty("status_code")
    public String status_code;
}