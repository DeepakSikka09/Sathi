package in.ecomexpress.sathi.repo.remote.model.forward.verifyOTP;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import in.ecomexpress.sathi.repo.remote.model.otp.resendotp.Response;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyOTPResponse {

    @JsonProperty("response")
    private Response response;

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
    public String getStatus () {
        return status;
    }

    @JsonProperty("status")
    public void setStatus (String status)
    {
        this.status = status;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    @NonNull
    @Override
    public String toString() {
        return "VerifyOTPResponse [description = "+description+", status = "+status+"]";
    }
}