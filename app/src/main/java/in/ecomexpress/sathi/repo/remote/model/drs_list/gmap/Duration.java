
package in.ecomexpress.sathi.repo.remote.model.drs_list.gmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Duration {

    @JsonProperty("inSeconds")
    private Integer inSeconds;
    @JsonProperty("humanReadable")
    private String humanReadable;

    @JsonProperty("inSeconds")
    public Integer getInSeconds() {
        return inSeconds;
    }

    @JsonProperty("inSeconds")
    public void setInSeconds(Integer inSeconds) {
        this.inSeconds = inSeconds;
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
