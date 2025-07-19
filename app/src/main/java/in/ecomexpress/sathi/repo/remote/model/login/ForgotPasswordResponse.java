package in.ecomexpress.sathi.repo.remote.model.login;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForgotPasswordResponse  {

    @JsonProperty("status")
    private boolean status;
    @JsonProperty("response")
    private SResponse sResponse;

    @JsonProperty("status")
    public boolean isStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(boolean status) {
        this.status = status;
    }

    @JsonProperty("response")
    public SResponse getResponse() {
        return sResponse;
    }

    @JsonProperty("response")
    public void setResponse(SResponse response) {
        this.sResponse = response;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class SResponse {

        @JsonProperty("status-code")
        private long statusCode;
        @JsonProperty("description")
        private String description;

        @JsonProperty("status-code")
        public long getStatusCode() {
            return statusCode;
        }

        @JsonProperty("status-code")
        public void setStatusCode(long statusCode) {
            this.statusCode = statusCode;
        }

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