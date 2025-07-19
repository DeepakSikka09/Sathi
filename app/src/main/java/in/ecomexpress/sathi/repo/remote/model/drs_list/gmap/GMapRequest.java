package in.ecomexpress.sathi.repo.remote.model.drs_list.gmap;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Created by parikshittomar on 10-01-2019.
 */


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "source_latitude",
        "source_longitude",
        "destination_latitude",
        "destination_longitude",
        "wayPoints"
})
public class GMapRequest {

    @JsonProperty("source_latitude")
    private Double sourceLatitude;
    @JsonProperty("source_longitude")
    private Double sourceLongitude;
    @JsonProperty("destination_latitude")
    private Double destinationLatitude;
    @JsonProperty("destination_longitude")
    private Double destinationLongitude;
    @JsonProperty("wayPoints")
    private List<WayPoint> wayPoints = null;

    @JsonProperty("source_latitude")
    public Double getSourceLatitude() {
        return sourceLatitude;
    }

    @JsonProperty("source_latitude")
    public void setSourceLatitude(Double sourceLatitude) {
        this.sourceLatitude = sourceLatitude;
    }

    @JsonProperty("source_longitude")
    public Double getSourceLongitude() {
        return sourceLongitude;
    }

    @JsonProperty("source_longitude")
    public void setSourceLongitude(Double sourceLongitude) {
        this.sourceLongitude = sourceLongitude;
    }

    @JsonProperty("destination_latitude")
    public Double getDestinationLatitude() {
        return destinationLatitude;
    }

    @JsonProperty("destination_latitude")
    public void setDestinationLatitude(Double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    @JsonProperty("destination_longitude")
    public Double getDestinationLongitude() {
        return destinationLongitude;
    }

    @JsonProperty("destination_longitude")
    public void setDestinationLongitude(Double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }

    @JsonProperty("wayPoints")
    public List<WayPoint> getWayPoints() {
        return wayPoints;
    }

    @JsonProperty("wayPoints")
    public void setWayPoints(List<WayPoint> wayPoints) {
        this.wayPoints = wayPoints;
    }


}