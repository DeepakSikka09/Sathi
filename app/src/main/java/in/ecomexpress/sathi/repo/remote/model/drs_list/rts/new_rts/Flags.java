package in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Flags {
    @JsonProperty("isOTPRequired")
    private Boolean isOTPRequired;

    @JsonProperty("isOTPRequired")
    public Boolean getIsOTPRequired() {
        return isOTPRequired;
    }

    @JsonProperty("isOTPRequired")
    public void setIsOTPRequired(Boolean isOTPRequired) {
        this.isOTPRequired = isOTPRequired;
    }
}
