package in.ecomexpress.sathi.repo.remote.model.masterdata;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;


/**
 * Created by shivangis on 11/28/2018.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "callbridge_configuration")
public class CallbridgeConfiguration {
/*
    @PrimaryKey(autoGenerate = true)
    public long id;*/

    @PrimaryKey
    @NonNull
    @JsonProperty("cb_calling_type")
    public String cb_calling_type;

    @JsonProperty("cb_calling_api")
    public String cb_calling_api;

    @Ignore
    @JsonProperty("cb_pstn_options")
    public List<CbPstnOptions> cb_pstn_options;

    @JsonProperty("cb_calling_type")
    public String getCb_calling_type() {
        return cb_calling_type;
    }

    @JsonProperty("cb_calling_type")
    public void setCb_calling_type(String cb_calling_type) {
        this.cb_calling_type = cb_calling_type;
    }

    @JsonProperty("cb_calling_api")
    public String getCb_calling_api() {
        return cb_calling_api;
    }

    @JsonProperty("cb_calling_api")
    public void setCb_calling_api(String cb_calling_api) {
        this.cb_calling_api = cb_calling_api;
    }

    @JsonProperty("cb_pstn_options")
    public List<CbPstnOptions> getCb_pstn_options() {
        return cb_pstn_options;
    }

    @JsonProperty("cb_pstn_options")
    public void setCb_pstn_options(List<CbPstnOptions> cb_pstn_options) {
        this.cb_pstn_options = cb_pstn_options;
    }

}
