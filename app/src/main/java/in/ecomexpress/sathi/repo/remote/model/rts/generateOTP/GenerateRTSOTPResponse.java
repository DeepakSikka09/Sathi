package in.ecomexpress.sathi.repo.remote.model.rts.generateOTP;

import androidx.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import in.ecomexpress.sathi.repo.remote.model.rts.Response;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerateRTSOTPResponse {

    @JsonProperty("description")
    private String description;

    @JsonProperty("status")
    private String status;

    @JsonProperty("response")
    public Response response = null;

    @JsonProperty("description")
    public String getDescription() {
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

    @JsonProperty("response")
    public Response getResponse(){
        return response;
    }

    @JsonProperty("response")
    public void setResponse(Response response){
        this.response = response;
    }

    @NonNull
    @Override
    public String toString() {
        return "GenerateOTPResponse [description = "+description+", status = "+status+"]";
    }
}