package in.ecomexpress.sathi.repo.remote.model.drs_list.rts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RtsConsigneeDetails {
    @JsonProperty("consignee_pincode")
    private Integer consigneePincode;
    
    @JsonProperty("customerPSTN")
    private String customerPSTN;

    @JsonProperty("consignee_pincode")
    public Integer getConsigneePincode() {
        return consigneePincode;
    }

    @JsonProperty("consignee_pincode")
    public void setConsigneePincode(Integer consigneePincode) {
        this.consigneePincode = consigneePincode;
    }

    @JsonProperty("customerPSTN")
    public String getCustomerPSTN() {
        return customerPSTN;
    }

    @JsonProperty("customerPSTN")
    public void setCustomerPSTN(String customerPSTN) {
        this.customerPSTN = customerPSTN;
    }
}
