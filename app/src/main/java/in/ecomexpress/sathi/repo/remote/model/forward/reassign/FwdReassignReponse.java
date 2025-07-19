package in.ecomexpress.sathi.repo.remote.model.forward.reassign;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import in.ecomexpress.sathi.repo.remote.model.sos.Response;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FwdReassignReponse {

    @JsonProperty("status")
    public boolean status;

    @JsonProperty("description")
    public String description;

    @JsonProperty("awbNumber")
    public long awbNumber;

    @JsonProperty("drsId")
    public int drsId;

    @JsonProperty("status")
    public boolean isStatus(){
        return status;
    }

    @JsonProperty("status")
    public void setStatus(boolean status){
        this.status = status;
    }

    @JsonProperty("response")
    public in.ecomexpress.sathi.repo.remote.model.sos.Response response;

    @JsonProperty("response")
    public in.ecomexpress.sathi.repo.remote.model.sos.Response getResponse(){
        return response;
    }
    @JsonProperty("response")
    public void setResponse(Response response){
        this.response = response;
    }
}
