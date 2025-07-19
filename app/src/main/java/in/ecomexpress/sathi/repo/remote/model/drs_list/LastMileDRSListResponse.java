
package in.ecomexpress.sathi.repo.remote.model.drs_list;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.DRSReturnToShipperTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LastMileDRSListResponse {


    @JsonProperty("forward_drs_list")
    private List<DRSForwardTypeResponse> forwardDrsList = null;

    @JsonProperty("rev_drs_list")
    private List<DRSReverseQCTypeResponse> revDrsList = null;

    @JsonProperty("rts_drs_list")
    private List<DRSReturnToShipperTypeResponse> rtsDrsList = null;


    @JsonProperty("forward_drs_list")
    public List<DRSForwardTypeResponse> getForwardDrsList() {
        return forwardDrsList;
    }

    @JsonProperty("forward_drs_list")
    public void setForwardDrsList(List<DRSForwardTypeResponse> forwardDrsList) {
        this.forwardDrsList = forwardDrsList;
    }

    @JsonProperty("rev_drs_list")
    public List<DRSReverseQCTypeResponse> getRevDrsList() {
        return revDrsList;
    }

    @JsonProperty("rev_drs_list")
    public void setRevDrsList(List<DRSReverseQCTypeResponse> revDrsList) {
        this.revDrsList = revDrsList;
    }

    @JsonProperty("rts_drs_list")
    public List<DRSReturnToShipperTypeResponse> getRtsDrsList() {
        return rtsDrsList;
    }

    @JsonProperty("rts_drs_list")
    public void setRtsDrsList(List<DRSReturnToShipperTypeResponse> rtsDrsList) {
        this.rtsDrsList = rtsDrsList;
    }


}
