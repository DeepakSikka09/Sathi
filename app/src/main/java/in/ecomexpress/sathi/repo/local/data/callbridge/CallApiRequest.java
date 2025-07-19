package in.ecomexpress.sathi.repo.local.data.callbridge;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CallApiRequest {

    @JsonProperty("awb_number")
    private String awb;

    @JsonProperty("api_key")
    private String cb_api_key;

    @JsonProperty("drs_id")
    private String drs_id;

    @JsonProperty("awb_number")
    public String getAwb ()
    {
        return awb;
    }

    @JsonProperty("awb_number")
    public void setAwb (String awb)
    {
        this.awb = awb;
    }

    @JsonProperty("api_key")
    public String getCb_api_key ()
    {
        return cb_api_key;
    }

    @JsonProperty("api_key")
    public void setCb_api_key (String cb_api_key)
    {
        this.cb_api_key = cb_api_key;
    }

    @JsonProperty("drs_id")
    public String getDrs_id ()
    {
        return drs_id;
    }

    @JsonProperty("drs_id")
    public void setDrs_id (String drs_id)
    {
        this.drs_id = drs_id;
    }

    @Override
    public String toString()
    {
        return "CallApiRequest [awb = "+awb+", cb_api_key = "+cb_api_key+", drs_id = "+drs_id+"]";
    }


}
