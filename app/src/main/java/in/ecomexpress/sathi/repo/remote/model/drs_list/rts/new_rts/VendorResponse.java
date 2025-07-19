package in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts;

import androidx.annotation.NonNull;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import in.ecomexpress.sathi.repo.remote.model.drs_list.common.CInterface;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VendorResponse implements IRTSBaseInterface, CInterface {

    @JsonProperty("details")
    private Details details;

    @JsonProperty("shipments_details")
    private List<ShipmentsDetail> shipmentsDetails = null;

    private double distance;

    public VendorResponse() {
    }

    @JsonProperty("details")
    public Details getDetails() {
        return details;
    }

    @JsonProperty("details")
    public void setDetails(Details details) {
        this.details = details;
    }

    @JsonProperty("shipments_details")
    public List<ShipmentsDetail> getShipmentsDetails() {
        return shipmentsDetails;
    }

    @JsonProperty("shipments_details")
    public void setShipmentsDetails(List<ShipmentsDetail> shipmentsDetails) {
        this.shipmentsDetails = shipmentsDetails;
    }

    @Override
    public String filterValue() {
        return getDetails().getName()
                +", "+getDetails().getRtsAddress()
                +", "+(getDetails().isVendor()?"Vendor":"Warehouse");
    }

    @NonNull
    @Override
    public String toString() {
        return "RTS: "+details.toString();

    }

    @Override
    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public double getDistance() {
        return this.distance;
    }
}
