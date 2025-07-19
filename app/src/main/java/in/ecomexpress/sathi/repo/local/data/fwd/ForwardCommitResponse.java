package in.ecomexpress.sathi.repo.local.data.fwd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForwardCommitResponse {
    @JsonProperty("response")
    private ForwardResponse response;
    @JsonProperty("status")
    private boolean status;

    @JsonProperty("response")
    public ForwardResponse getResponse() {
        return response;
    }

    @JsonProperty("response")
    public void setResponse(ForwardResponse response) {
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
        return "ForwardCommitResponse{" +
                "status=" + status +
                ", response='" + response + '\'' +
                '}';
    }
}
