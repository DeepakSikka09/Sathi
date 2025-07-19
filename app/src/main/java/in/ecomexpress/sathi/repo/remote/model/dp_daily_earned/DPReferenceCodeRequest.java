package in.ecomexpress.sathi.repo.remote.model.dp_daily_earned;

import com.fasterxml.jackson.annotation.JsonProperty;

import in.ecomexpress.sathi.repo.remote.model.DeviceDetails;
import in.ecomexpress.sathi.repo.remote.model.base.ApiRequest;

public  class DPReferenceCodeRequest  {

    @JsonProperty("drs_id")
    private long drs_id;

    @JsonProperty("ref_code")
    private String ref_code;


    public long getDrs_id() {
        return drs_id;
    }

    public void setDrs_id(long drs_id) {
        this.drs_id = drs_id;
    }

    public String getRef_code() {
        return ref_code;
    }

    public void setRef_code(String ref_code) {
        this.ref_code = ref_code;
    }
}