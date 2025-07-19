package in.ecomexpress.sathi.repo.remote.model.drs_list;



import androidx.room.Ignore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by shivangis on 2/19/2019.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TodoResponse {
    @Ignore
    @JsonProperty("code")
    private int code;
    @Ignore
    @JsonProperty("status-code")
    private int statusCode;

    @Ignore
    @JsonProperty("description")
    private String description;

    @Ignore
    @JsonProperty("drs_list_response")
    private LastMileDRSListNewResponse drs_list_response;


    @JsonProperty("code")
    public int getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(int code) {
        this.code = code;
    }

    @JsonProperty("status-code")
    public int getStatusCode() {
        return statusCode;
    }

    @JsonProperty("status-code")
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("drs_list_response")
    public LastMileDRSListNewResponse getDrs_list_response() {
        return drs_list_response;
    }

    @JsonProperty("drs_list_response")
    public void setDrs_list_response(LastMileDRSListNewResponse drs_list_response) {
        this.drs_list_response = drs_list_response;
    }

    @Override
    public String toString() {
        return "TodoResponse [code = " + code + ", description = " + description + ", drs_list_response = " + drs_list_response + "]";
    }
}
