package in.ecomexpress.sathi.repo.remote.model.otp.verifyotp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyOtpResponse {

    @JsonProperty("response")
    private Response response;

    public Boolean getStatus() {
        return status;
    }

    @JsonProperty("status")
    private Boolean status;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "VerifyOtpResponse [response = " + response + ", status = " + status + "]";
    }
}