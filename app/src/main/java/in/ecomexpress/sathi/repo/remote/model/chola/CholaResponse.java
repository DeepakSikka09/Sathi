package in.ecomexpress.sathi.repo.remote.model.chola;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import in.ecomexpress.sathi.repo.remote.model.sos.Response;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CholaResponse {
    @JsonProperty("status")
    private boolean status;

    @JsonProperty("response")
    public Response response;

    @JsonProperty("description")
    private String  description;

    @JsonProperty("url")
    private String  url;

    public Response getResponse(){
        return response;
    }

    public void setResponse(Response response){
        this.response = response;
    }

    public boolean isStatus(){
        return status;
    }

    public void setStatus(boolean status){
        this.status = status;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getUrl(){
        return url;
    }

    public void setUrl(String url){
        this.url = url;
    }
}