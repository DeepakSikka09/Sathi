
package in.ecomexpress.sathi.repo.remote.model.drs_list.gmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonIgnoreProperties(ignoreUnknown = true)
public class Distance {

    @JsonProperty("inMeters")
    private Integer inMeters;
    @JsonProperty("humanReadable")
    private String humanReadable;

    @JsonProperty("inMeters")
    public Integer getInMeters() {
        return inMeters;
    }

    @JsonProperty("inMeters")
    public void setInMeters(Integer inMeters) {
        this.inMeters = inMeters;
    }

    @JsonProperty("humanReadable")
    public String getHumanReadable() {
        return humanReadable;
    }

    @JsonProperty("humanReadable")
    public void setHumanReadable(String humanReadable) {
        this.humanReadable = humanReadable;
    }

}
