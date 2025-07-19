
package in.ecomexpress.sathi.repo.remote.model.drs_list.gmap;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Route {

    @JsonProperty("legs")
    private List<Leg> legs = null;
    @JsonProperty("overviewPolyline")
    private OverviewPolyline overviewPolyline;
    @JsonProperty("bounds")
    private Bounds bounds;
	
    @JsonProperty("legs")
    public List<Leg> getLegs() {
        return legs;
    }

    @JsonProperty("legs")
    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }

    @JsonProperty("overviewPolyline")
    public OverviewPolyline getOverviewPolyline() {
        return overviewPolyline;
    }

    @JsonProperty("overviewPolyline")
    public void setOverviewPolyline(OverviewPolyline overviewPolyline) {
        this.overviewPolyline = overviewPolyline;
    }

    @JsonProperty("bounds")
    public Bounds getBounds() {
        return bounds;
    }

    @JsonProperty("bounds")
    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

}
