package in.ecomexpress.sathi.repo.remote.model.print_receipt;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by anshika on 13/3/20.
 */

public class Response {

    @JsonProperty("status-code")

    private String status_code;

    private String code;

    private String is_dc_update_allowed_for_dept;

    private String description;

    @JsonProperty("status-code")
    public String getStatus_code()
    {
        return status_code;
    }
    @JsonProperty("status-code")
    public void setStatus_code (String status_code)
    {
        this.status_code = status_code;
    }

    public String getCode ()
    {
        return code;
    }

    public void setCode (String code)
    {
        this.code = code;
    }

    public String getIs_dc_update_allowed_for_dept ()
    {
        return is_dc_update_allowed_for_dept;
    }

    public void setIs_dc_update_allowed_for_dept (String is_dc_update_allowed_for_dept)
    {
        this.is_dc_update_allowed_for_dept = is_dc_update_allowed_for_dept;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }
}
