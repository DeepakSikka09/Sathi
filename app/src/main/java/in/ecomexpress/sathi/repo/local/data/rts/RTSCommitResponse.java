package in.ecomexpress.sathi.repo.local.data.rts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import in.ecomexpress.sathi.repo.local.data.rvp.RvpResponse;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RTSCommitResponse {

    @JsonProperty("response")
    private RvpResponse response;

    @JsonProperty("status")
    private boolean status;

    @JsonProperty("response")
    public RvpResponse getResponse() {
        return response;
    }

    @JsonProperty("response")
    public void setResponse(RvpResponse response) {
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
        return "RTSCommitResponse{" +
                "status=" + status +
                ", response='" + response + '\'' +
                '}';

    }
}
