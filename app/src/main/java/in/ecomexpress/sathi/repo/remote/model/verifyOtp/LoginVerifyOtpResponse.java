package in.ecomexpress.sathi.repo.remote.model.verifyOtp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dhananjayk on 29-01-2019.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginVerifyOtpResponse {
    @JsonProperty("status")
    private boolean status;
    @JsonProperty("response")
    private SResponse sResponse;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public SResponse getsResponse() {
        return sResponse;
    }

    public void setsResponse(SResponse sResponse) {
        this.sResponse = sResponse;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class SResponse {

        @JsonProperty("code")
        private long Code;
        @JsonProperty("description")
        private String description;

        public long getCode() {
            return Code;
        }

        public void setCode(long code) {
            Code = code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
