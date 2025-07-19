package in.ecomexpress.sathi.repo.remote.model.performance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import in.ecomexpress.sathi.repo.remote.model.sos.Response;

/**
 * Created by sandeep on 01-04-2019.
 */
/*@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PerformanceResponse
{
    @JsonProperty("performance_response")
    private String performance_response;

    public PerformanceResponse(String performance_response) {

        this.performance_response = performance_response;

    }

    @JsonProperty("performance_response")
    public String getPerformance ()
    {
        return performance_response;
    }

    @JsonProperty("performance_response")
    public void setPerformance (String performance)
    {
        this.performance_response = performance;
    }

    @Override
    public String toString()
    {
        return "PerformanceResponse [performance = "+performance_response+"]";
    }
}*/

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PerformanceResponse {

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

    @JsonProperty("performance_response")
    private String performance_response;

    @JsonProperty("performance_response")
    public String getPerformance_response() {
        return performance_response;
    }

    @JsonProperty("performance_response")
    public void setPerformance_response(String performance_response) {
        this.performance_response = performance_response;
    }

    @Override
    public String toString() {
        return "PerformanceResponse [performance_response = " + performance_response + "]";
    }
}

