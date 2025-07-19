package in.ecomexpress.sathi.repo.remote.model.device_upload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by santosh on 28/8/19.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Biometric_response {
    @JsonProperty("code")
    int code;
    @JsonProperty("status")
    boolean status;
    @JsonProperty("description")
    String description;

//    public Biometric_response(int code, boolean status, String description) {
//        this.code = code;
//        this.status = status;
//        this.description = description;
//    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        Biometric_response that = (Biometric_response) o;
//
//        if (getCode() != that.getCode()) return false;
//        if (isStatus() != that.isStatus()) return false;
//        return getDescription() != null ? getDescription().equals(that.getDescription()) : that.getDescription() == null;
//    }
//
//    @Override
//    public int hashCode() {
//        int result = getCode();
//        result = 31 * result + (isStatus() ? 1 : 0);
//        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
//        return result;
//    }
}
