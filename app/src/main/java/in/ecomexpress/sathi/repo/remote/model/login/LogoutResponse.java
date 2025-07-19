package in.ecomexpress.sathi.repo.remote.model.login;

import androidx.annotation.NonNull;
import androidx.room.Ignore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogoutResponse {

    @JsonProperty("status")
    private boolean status;

    @JsonProperty("status")
    public boolean isStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(boolean status) {
        this.status = status;
    }

    @JsonProperty("response")
    public EResponse response;

    @JsonProperty("response")
    public EResponse getResponse() {
        return response;
    }

    @JsonProperty("response")
    public void setResponse(EResponse response) {
        this.response = response;
    }

    @NonNull
    @Override
    public String toString() {
        return "LoginResponse{" + "status=" + status + '}';
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public  static class EResponse{

        @Ignore
        @JsonProperty("description")
        private String description;

        @JsonProperty("description")
        public String getDescription() {
            return description;
        }

        @JsonProperty("description")
        public void setDescription(String description) {
            this.description = description;
        }
    }
}