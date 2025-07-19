
package in.ecomexpress.sathi.repo.remote.model.drs_list.eds;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DRSKycListTypeResponse {

    @JsonProperty("kyc_data")
    private List<EDSTypeResponse> kycData = null;

    @JsonProperty("kyc_data")
    public List<EDSTypeResponse> getKycData() {
        return kycData;
    }

    @JsonProperty("kyc_data")
    public void setKycData(List<EDSTypeResponse> kycData) {
        this.kycData = kycData;
    }

    @Override
    public String toString() {
        return "DRSKycListTypeResponse{" +
                "kycData=" + kycData +
                '}';
    }
}
