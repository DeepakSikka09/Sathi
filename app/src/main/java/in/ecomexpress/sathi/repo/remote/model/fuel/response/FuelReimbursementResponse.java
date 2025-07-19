package in.ecomexpress.sathi.repo.remote.model.fuel.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FuelReimbursementResponse {

    @JsonProperty("response")
    private Response response;

    @JsonProperty("status")
    private boolean status;

    @JsonProperty("response")
    public Response getResponse() {
        return response;
    }

    @JsonProperty("response")
    public void setResponse(Response response) {
        this.response = response;
    }

    @JsonProperty("status")
    public boolean getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "FuelReimbursementResponse [response = " + response + ", status = " + status + "]";
    }
}
