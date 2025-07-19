package in.ecomexpress.sathi.repo.remote.model.rts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    @JsonProperty("description")
    public String description;
    @JsonProperty("status-code")
    public String status_code;

    @JsonProperty("code")
    public String code;
}