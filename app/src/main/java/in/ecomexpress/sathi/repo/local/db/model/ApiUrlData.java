package in.ecomexpress.sathi.repo.local.db.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by 63318 on 03-06-2019.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "ApiUrlData")
public class ApiUrlData {

    @PrimaryKey
    @NonNull
    private String apiUrlKey;

    @JsonProperty("api_url")
    private String apiUrl;

    @JsonProperty("api_url_key")
    public String getApiUrlKey() {
        return apiUrlKey;
    }

    @JsonProperty("api_url_key")
    public void setApiUrlKey(String apiUrlKey) {
        this.apiUrlKey = apiUrlKey;
    }

    @JsonProperty("api_url")
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @JsonProperty("api_url")
    public String getApiUrl() {
        return apiUrl;
    }


}
