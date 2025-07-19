package in.ecomexpress.sathi.repo.remote.model.rts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RTSVerifyOTPResponse {

    public boolean status;
    public String description;

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
}
