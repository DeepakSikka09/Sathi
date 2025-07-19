package in.ecomexpress.sathi.repo.remote.model.consignee_profile;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by anshika on 2/3/20.
 */


public class Consignee_profile {
    @JsonProperty("shipper_id")
    private String shipper_id;

    @JsonProperty("awb")
    private String awb;

    @JsonProperty("shipper_id")
    public String getShipper_id() {
        return shipper_id;
    }

    @JsonProperty("shipper_id")
    public void setShipper_id(String shipper_id) {
        this.shipper_id = shipper_id;
    }

    @JsonProperty("awb")
    public String getAwb() {
        return awb;
    }

    @JsonProperty("awb")
    public void setAwb(String awb) {
        this.awb = awb;
    }


}
