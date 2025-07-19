package in.ecomexpress.sathi.repo.remote.model.payphi.payment_link_sms;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import in.ecomexpress.sathi.repo.remote.model.payphi.awb_register.Response;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class SmsLinkResponse {


    private String description;

    private String status;



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

//    @Override
//    public String toString() {
//        return "ClassPojo [response = " + response + ", status = " + status + "]";
//    }

}




