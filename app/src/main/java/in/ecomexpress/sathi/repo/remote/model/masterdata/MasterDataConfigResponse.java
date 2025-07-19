package in.ecomexpress.sathi.repo.remote.model.masterdata;

import androidx.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterDataConfigResponse {

    @JsonProperty("code")
    private int code;

    @JsonProperty("description")
    private String description;

    @JsonProperty("master_data_configurations")
    private MasterDataReasonCodeResponse master_data_configurations;

    @JsonProperty("code")
    public int getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(int code) {
        this.code = code;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("master_data_configurations")
    public MasterDataReasonCodeResponse getMaster_data_configurations() {
        return master_data_configurations;
    }

    @JsonProperty("master_data_configurations")
    public void setMaster_data_configurations(MasterDataReasonCodeResponse master_data_configurations) {
        this.master_data_configurations = master_data_configurations;
    }

    @NonNull
    @Override
    public String toString() {
        return "MasterDataConfigResponse [code = " + code + ", description = " + description + ", master_data_configurations = " + master_data_configurations + "]";
    }
}