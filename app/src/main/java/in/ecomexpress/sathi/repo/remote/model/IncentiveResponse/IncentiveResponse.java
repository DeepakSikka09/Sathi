package in.ecomexpress.sathi.repo.remote.model.IncentiveResponse;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import in.ecomexpress.sathi.repo.remote.model.sos.Response;



@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class IncentiveResponse {

    @JsonProperty("status")
    public boolean status;

    @JsonProperty("response")
    public Response response;

    @JsonProperty("response")
    public Response getResponse(){
        return response;
    }
    @JsonProperty("response")
    public void setResponse(Response response){
        this.response = response;
    }

    @JsonProperty("incentive_response")
    private String performance_response;

    @JsonProperty("incentive_response")
    public String getPerformance_response() {
        return performance_response;
    }

    @JsonProperty("incentive_response")
    public void setPerformance_response(String performance_response) {
        this.performance_response = performance_response;
    }

    @Override
    public String toString() {
        return "PerformanceResponse [performance_response = " + performance_response + "]";
    }
}


