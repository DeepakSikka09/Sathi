
package in.ecomexpress.sathi.repo.remote.model.drs_list.gmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Step {


    @JsonProperty("startLocation")
    private StartLocation startLocation;
    @JsonProperty("endLocation")
    private EndLocation endLocation;

    @JsonProperty("distance")
    private Distance distance;

    @JsonProperty("duration")
    private Duration duration;


    @JsonProperty("polyline")
    private Polyline polyline;


    @JsonProperty("startLocation")
    public StartLocation getStartLocation() {
        return startLocation;
    }

    @JsonProperty("startLocation")
    public void setStartLocation(StartLocation startLocation) {
        this.startLocation = startLocation;
    }

    @JsonProperty("endLocation")
    public EndLocation getEndLocation() {
        return endLocation;
    }

    @JsonProperty("endLocation")
    public void setEndLocation(EndLocation endLocation) {
        this.endLocation = endLocation;
    }

    @JsonProperty("polyline")
    public Polyline getPolyline() {
        return polyline;
    }

    @JsonProperty("polyline")
    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    @JsonProperty("distance")
    public Distance getDistance() {
        return distance;
    }

    @JsonProperty("distance")
    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    @JsonProperty("duration")
    public Duration getDuration() {
        return duration;
    }

    @JsonProperty("duration")
    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}
