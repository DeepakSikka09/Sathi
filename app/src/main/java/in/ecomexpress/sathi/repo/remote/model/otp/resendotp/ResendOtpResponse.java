package in.ecomexpress.sathi.repo.remote.model.otp.resendotp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResendOtpResponse {

    @JsonProperty("response")
    private Response response;

    @JsonProperty("status")
    private String status;

    @JsonProperty("description")
    private String description;
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

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return "ResendOtpResponse [response = " + response + ", status = " + status + ", description = " + description + "]";
    }
}