package in.ecomexpress.sathi.repo.remote.model.shipment_weight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import in.ecomexpress.sathi.repo.remote.model.print_receipt.Bank_code_list;
import in.ecomexpress.sathi.repo.remote.model.print_receipt.Bank_receipt_details;
import in.ecomexpress.sathi.repo.remote.model.print_receipt.Reason_code_list;
import in.ecomexpress.sathi.repo.remote.model.sos.Response;

/**
 * Created by anshika on 11/3/20.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShipmentWeightResponse {

    @JsonProperty("response")
    public Response response;

    public Response getResponse(){
        return response;
    }

    public void setResponse(Response response){
        this.response = response;
    }

    boolean status;

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

    String description;


}
