package in.ecomexpress.sathi.repo.remote.model.masterdata;

import androidx.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterDataConfig {

    @JsonProperty("response")
    private MasterDataConfigResponse response;

    @JsonProperty("status")
    private Boolean status;

    @JsonProperty("response")
    public MasterDataConfigResponse getResponse() {
        return response;
    }

    @JsonProperty("response")
    public void setResponse(MasterDataConfigResponse response) {
        this.response = response;
    }

    @JsonProperty("status")
    public Boolean getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Boolean status) {
        this.status = status;
    }

    @NonNull
    @Override
    public String toString() {
        return "MasterDataConfig [response = " + response + ", status = " + status + "]";
    }
}