package in.ecomexpress.sathi.repo.remote.model.sos;

import androidx.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {

    @JsonProperty("description")
    private String description;

    @JsonProperty("status-code")
    private int statusCode;

    @JsonProperty("code")
    private int code;

    @JsonProperty("status-code")
    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("code")
    public int getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(int code) {
        this.code = code;
    }

    @NonNull
    @JsonProperty("status-code")

    @Override
    public String toString() {
        return "Response [status-code=" + statusCode + ", description = " + description + "]";
    }
}
