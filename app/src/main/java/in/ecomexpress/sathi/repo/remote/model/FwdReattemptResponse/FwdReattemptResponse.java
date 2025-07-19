package in.ecomexpress.sathi.repo.remote.model.FwdReattemptResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import in.ecomexpress.sathi.repo.remote.model.sos.Response;



@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FwdReattemptResponse {

    @JsonProperty("status")
    public boolean status;

    @JsonProperty("status")
    public boolean isStatus(){
        return status;
    }

    @JsonProperty("status")
    public void setStatus(boolean status){
        this.status = status;
    }

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




}


