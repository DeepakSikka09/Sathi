package in.ecomexpress.sathi.repo.remote.model.masterdata;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


/**
 * Created by shivangis on 11/28/2018.
 */


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "callbridge_pstnoption")
public class CbPstnOptions {
    @PrimaryKey
    @NonNull
    @JsonProperty("pstn_format")
    public String pstn_format;

    @JsonProperty("pstn_provider")
    public String pstn_provider;

    @JsonProperty("isLocal")
    public String getIsLocal() {
        return isLocal;
    }

    @JsonProperty("isLocal")
    public void setIsLocal(String isLocal) {
        this.isLocal = isLocal;
    }

    @JsonProperty("additional_text")
    public String getAdditional_text() {
        return additional_text;
    }

    @JsonProperty("additional_text")
    public void setAdditional_text(String additional_text) {
        this.additional_text = additional_text;
    }

    @JsonProperty("isLocal")
    public String isLocal;

    @JsonProperty("additional_text")
    public String additional_text;

    @JsonProperty("sp_name")
    public String sp_name;

    public String getSp_name() {
        return sp_name;
    }

    public void setSp_name(String sp_name) {
        this.sp_name = sp_name;
    }

    @JsonProperty("pstn_format")
    public String getPstn_format() {
        return pstn_format;
    }

    @JsonProperty("pstn_format")
    public void setPstn_format(String pstn_format) {
        this.pstn_format = pstn_format;
    }

    @JsonProperty("pstn_provider")
    public String getPstn_provider() {
        return pstn_provider;
    }

    @JsonProperty("pstn_provider")
    public void setPstn_provider(String pstn_provider) {
        this.pstn_provider = pstn_provider;
    }


}
