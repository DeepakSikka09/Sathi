
package in.ecomexpress.sathi.repo.remote.model.drs_list;


import androidx.room.Ignore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.ErrorResponse;

import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.DRSReturnToShipperTypeNewResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.mps.DRSRvpQcMpsResponse;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LastMileDRSListNewResponse extends ErrorResponse {

    @Ignore
    @JsonProperty("forward_drs_list")
    private List<DRSForwardTypeResponse> forwardDrsList = null;

    @Ignore
    @JsonProperty("rev_drs_list")
    private List<DRSReverseQCTypeResponse> revDrsList = null;

    @Ignore
    @JsonProperty("rts_drs_list")
    private DRSReturnToShipperTypeNewResponse drsReturnToShipperTypeNewResponse = null;

    @Ignore
    @JsonProperty("eds_drs_list")
    private List<EDSResponse> edsList = null;

    @Ignore
    @JsonProperty("rev_mps_drs_list")
    private List<DRSRvpQcMpsResponse> revMpsDrsList = null;

    @JsonProperty("rev_mps_drs_list")
    public List<DRSRvpQcMpsResponse> getRevMpsDrsList() {
        return revMpsDrsList;
    }

    @JsonProperty("rev_mps_drs_list")
    public void setRevMpsDrsList(List<DRSRvpQcMpsResponse> revMpsDrsList) {
        this.revMpsDrsList = revMpsDrsList;
    }

    public void setRtsDrsList(DRSReturnToShipperTypeNewResponse drsReturnToShipperTypeNewResponse ) {
        this.drsReturnToShipperTypeNewResponse = drsReturnToShipperTypeNewResponse;
    }
   /* @Ignore
    @JsonProperty("eds_drs_list")
    private List<EDSTypeResponse> edsDrsList = null;
*/
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

    public DRSReturnToShipperTypeNewResponse getDrsReturnToShipperTypeNewResponse() {
        return drsReturnToShipperTypeNewResponse;
    }

    public void setDrsReturnToShipperTypeNewResponse(DRSReturnToShipperTypeNewResponse drsReturnToShipperTypeNewResponse) {
        this.drsReturnToShipperTypeNewResponse = drsReturnToShipperTypeNewResponse;
    }


    public List<EDSResponse> getEdsList() {
        return edsList;
    }

    public void setEdsList(List<EDSResponse> edsList) {
        this.edsList = edsList;
    }
}
