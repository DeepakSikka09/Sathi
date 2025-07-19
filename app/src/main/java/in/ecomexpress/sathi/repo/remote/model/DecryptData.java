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
public class DecryptData {
    @Ignore
    @JsonProperty("description")
    private String description;


    @Ignore
    @JsonProperty("consignee_mobile_number")
    private long consignee_mobile_number;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getConsignee_mobile_number() {
        return consignee_mobile_number;
    }

    public void setConsignee_mobile_number(long consignee_mobile_number) {
        this.consignee_mobile_number = consignee_mobile_number;
    }
}
