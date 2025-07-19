package in.ecomexpress.sathi.repo.remote.model.IciciResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class IciciResponse_standard {
    @JsonProperty("message")
    public String message;
    @JsonProperty("response")
    public String response;
    @JsonProperty("status")
    public String status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}


