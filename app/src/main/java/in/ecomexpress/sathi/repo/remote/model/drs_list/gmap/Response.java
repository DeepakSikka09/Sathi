package in.ecomexpress.sathi.repo.remote.model.drs_list.gmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by parikshittomar on 23-01-2019.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {

    @JsonProperty("code")
    private int code;

    @JsonProperty("status-code")
    private int stats_code;

    @JsonProperty("description")
    private String description;

    @JsonProperty("route")
    private Route route;

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

    @JsonProperty("route")
    public Route getRoute() {
        return route;
    }

    @JsonProperty("route")
    public void setRoute(Route route) {
        this.route = route;
    }

    @JsonProperty("status-code")
    public int getStats_code() {
        return stats_code;
    }

    @JsonProperty("status-code")
    public void setStats_code(int stats_code) {
        this.stats_code = stats_code;
    }

    @Override
    public String toString() {
        return "code: " + code + ", status-code: " + stats_code+", description: "+description;
    }
}

