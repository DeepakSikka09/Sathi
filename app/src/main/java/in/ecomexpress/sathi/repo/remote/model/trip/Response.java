package in.ecomexpress.sathi.repo.remote.model.trip;



import androidx.room.Ignore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by parikshittomar on 18-01-2019.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
/*@JsonPropertyOrder({
        "code",
        "description",
        "trip_id"
})*/
public class Response {
    @JsonProperty("status-code")
    public long getStatusCode() {
        return statusCode;
    }

    @JsonProperty("status-code")
    public void setStatusCode(long statusCode) {
        this.statusCode = statusCode;
    }

    @JsonProperty("status-code")
    @Ignore
    private long statusCode;

    @JsonProperty("live_tracking_trip_id")
    @Ignore
    private String live_tracking_trip_id;

/*
    @JsonProperty("additional_info")
    public String getAdditional_info() {
        return additional_info;
    }

    @JsonProperty("additional_info")
    public void setAdditional_info(String additional_info) {
        this.additional_info = additional_info;
    }

    @JsonProperty("additional_info")
    @Ignore
    private String additional_info;

*/

    @JsonProperty("code")
    private int code;

    @JsonProperty("description")
    private String description;

    @JsonProperty("trip_id")
    private Integer tripId;

    @JsonProperty("code")
    public int getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(int code) {
        this.code = code;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("trip_id")
    public Integer getTripId() {
        return tripId;
    }

    @JsonProperty("trip_id")
    public void setTripId(Integer tripId) {
        this.tripId = tripId;
    }

    public String getLive_tracking_trip_id() {
        return live_tracking_trip_id;
    }

    public void setLive_tracking_trip_id(String live_tracking_trip_id) {
        this.live_tracking_trip_id = live_tracking_trip_id;
    }
}
