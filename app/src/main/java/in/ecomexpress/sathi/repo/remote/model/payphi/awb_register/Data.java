package in.ecomexpress.sathi.repo.remote.model.payphi.awb_register;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Data {

    @JsonProperty("status")
    private String status;

    @JsonProperty("cust_sup_number")
    private String cust_sup_number;

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("cust_sup_number")
    public String getCust_sup_number() {
        return cust_sup_number;
    }

    @JsonProperty("cust_sup_number")
    public void setCust_sup_number(String cust_sup_number) {
        this.cust_sup_number = cust_sup_number;
    }

    @Override
    public String toString() {
        return "Data [status = " + status + ", cust_sup_number = " + cust_sup_number + "]";
    }
}