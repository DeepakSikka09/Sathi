package in.ecomexpress.sathi.repo.local.data.rvp;

import androidx.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RVPCommitResponse {

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

    @NonNull
    @Override
    public String toString() {
        return "RVPCommitResponse{" +
                "status=" + status +
                ", response='" + response + '\'' +
                '}';
    }
}