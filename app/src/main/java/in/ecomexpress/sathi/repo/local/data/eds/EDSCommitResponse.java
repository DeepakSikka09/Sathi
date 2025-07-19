package in.ecomexpress.sathi.repo.local.data.eds;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EDSCommitResponse {

    @JsonProperty("response")
    private EDSResponseCommit response;

    @JsonProperty("status")
    private boolean status;

    @JsonProperty("response")
    public EDSResponseCommit getResponse() {
        return response;
    }

    @JsonProperty("response")
    public void setResponse(EDSResponseCommit response) {
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
        return "EDSCommitResponse{" +
                "status=" + status +
                ", response='" + response + '\'' +
                '}';

    }
}
