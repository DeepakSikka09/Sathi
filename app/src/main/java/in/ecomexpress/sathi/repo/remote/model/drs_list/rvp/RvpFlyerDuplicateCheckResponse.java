package in.ecomexpress.sathi.repo.remote.model.drs_list.rvp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RvpFlyerDuplicateCheckResponse {

    @JsonProperty("description")
    private String description;

    @JsonProperty("status")
    private String status;

    @JsonProperty("description")
    public String getDescription ()
    {
        return description;
    }

    @JsonProperty("description")
    public void setDescription (String description)
    {
        this.description = description;
    }

    @JsonProperty("status")
    public String getStatus () {
        return status;
    }

    @JsonProperty("status")
    public void setStatus (String status) {
        this.status = status;
    }
}