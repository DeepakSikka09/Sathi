package in.ecomexpress.sathi.repo.remote.model.forward.generateOTP;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sandeep on 6/4/20.
 */


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerateOTPResponse {

    @JsonProperty("description")
    private String description;

    @JsonProperty("status")
    private String status;

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
        return "GenerateOTPResponse [description = "+description+", status = "+status+"]";
    }

}
