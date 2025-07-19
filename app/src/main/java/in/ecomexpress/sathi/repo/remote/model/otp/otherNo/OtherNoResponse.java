package in.ecomexpress.sathi.repo.remote.model.otp.otherNo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import in.ecomexpress.sathi.repo.remote.model.otp.resendotp.Response;

/**
 * Created by dhananjayk on 12-02-2019.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OtherNoResponse {
    @JsonProperty("response")
    private Response response;

    @JsonProperty("status")
    private String status;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "OtherNoResponse [response = " + response + ", status = " + status + "]";
    }

}