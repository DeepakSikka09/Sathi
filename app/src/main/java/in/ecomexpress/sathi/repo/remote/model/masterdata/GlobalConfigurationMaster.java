package in.ecomexpress.sathi.repo.remote.model.masterdata;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dhananjayk on 31-07-2018.
 */


@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity(tableName = "GlobalConfigurationMasterTable")
public class GlobalConfigurationMaster {

    @PrimaryKey
    @NonNull
    @JsonProperty("config_group")
    private String configGroup = "";

    @JsonProperty("config_value")
    private String configValue = "";

    @JsonProperty("config_group")
    public String getConfigGroup() {
        return configGroup;
    }

    @JsonProperty("config_group")
    public void setConfigGroup(String configGroup) {
        this.configGroup = configGroup;
    }

    @JsonProperty("config_value")
    public String getConfigValue() {
        return configValue;
    }

    @JsonProperty("config_value")
    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }
}
