package in.ecomexpress.sathi.repo.remote.model;



import androidx.room.Ignore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by santosh on 31/1/20.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EncryptContactResponse {
    @Ignore
    @JsonProperty("status")
    private boolean status;


    @Ignore
    @JsonProperty("response")
    private DecryptData response;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public DecryptData getResponse() {
        return response;
    }

    public void setResponse(DecryptData response) {
        this.response = response;
    }
}
