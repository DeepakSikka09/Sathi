package in.ecomexpress.sathi.repo.remote.model.drs_list.gmap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Created by parikshittomar on 10-01-2019.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "waysPoints"
})
public class WayPoint {


    @JsonProperty("waysPoints")
    private String waysPoints;

    @JsonProperty("waysPoints")
    public String getWaysPoints() {
        return waysPoints;
    }

    @JsonProperty("waysPoints")
    public void setWaysPoints(String waysPoints) {
        this.waysPoints = waysPoints;
    }

}