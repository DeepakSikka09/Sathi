package in.ecomexpress.sathi.repo.remote.model.drs_list.forward;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by o74884 on 20-09-2019.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForwardCallResponse {
    @JsonProperty("response")
    private String response;

    @JsonProperty("status")
    private String status;

    @JsonProperty("call_again_required")
    private boolean call_again_required;

    @JsonProperty("response")
    public String getResponse ()
    {
        return response;
    }

    @JsonProperty("response")
    public void setResponse (String response)
    {
        this.response = response;
    }

    @JsonProperty("status")
    public String getStatus ()
    {
        return status;
    }

    @JsonProperty("status")
    public void setStatus (String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "ForwardCallResponse [response = "+response+", status = "+status+"]";
    }

    public boolean isCall_again_required() {
        return call_again_required;
    }

    public void setCall_again_required(boolean call_again_required) {
        this.call_again_required = call_again_required;
    }
}
