package in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Location {
    @JsonProperty("lat")
    private double lat;

    @JsonProperty("long")
    private double _long;

    @JsonProperty("lat")
    public double getLat() {
        return lat;
    }

    @JsonProperty("lat")
    public void setLat(double lat) {
        this.lat = lat;
    }

    @JsonProperty("long")
    public double getLong() {
        return _long;
    }

    @JsonProperty("long")
    public void setLong(double _long) {
        this._long = _long;
    }

    @NonNull
    @Override
    public String toString() {
        return "Location{" +
                "lat=" + lat +
                ", _long=" + _long +
                '}';
    }
}
