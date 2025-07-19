package in.ecomexpress.sathi.repo.remote.model.otp.verifyotp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
    @JsonProperty("description")
    private String description;


    @JsonProperty("code")
    private String code ;

    @JsonProperty("status-code")
    private String statusCode;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Response{" +
                "description='" + description + '\'' +
                ", code='" + code + '\'' +
                '}';
    }

    @JsonProperty("status-code")
    public String getStatusCode() {
        return statusCode;
    }

    @JsonProperty("status-code")
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
