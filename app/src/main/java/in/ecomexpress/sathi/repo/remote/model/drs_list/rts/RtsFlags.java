package in.ecomexpress.sathi.repo.remote.model.drs_list.rts;

import androidx.annotation.NonNull;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RtsFlags implements Serializable {
	private static final long serialVersionUID = -4091522097250184605L;

	@JsonProperty("is_warehouse")
    private Boolean isWarehouse;
    
    @JsonProperty("isOTPRequired")
    private Boolean isOTPRequired;

    @JsonProperty("is_warehouse")
    public Boolean getIsWarehouse() {
        return isWarehouse;
    }

    @JsonProperty("is_warehouse")
    public void setIsWarehouse(Boolean isWarehouse) {
        this.isWarehouse = isWarehouse;
    }

    @JsonProperty("isOTPRequired")
    public Boolean getIsOTPRequired() {
        return isOTPRequired;
    }

    @JsonProperty("isOTPRequired")
    public void setIsOTPRequired(Boolean isOTPRequired) {
        this.isOTPRequired = isOTPRequired;
    }

	@NonNull
    @Override
	public String toString() {
		return "Flags [isWarehouse=" + isWarehouse + ", isOTPRequired=" + isOTPRequired + "]";
	}
}
