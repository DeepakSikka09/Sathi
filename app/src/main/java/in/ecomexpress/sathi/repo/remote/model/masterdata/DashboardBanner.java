package in.ecomexpress.sathi.repo.remote.model.masterdata;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "banner")
public class DashboardBanner {

    @NonNull
    @PrimaryKey()
    @JsonProperty("short_url")
    private String short_url = "";

    @JsonProperty("long_url")
    private String long_url;

    @JsonProperty("title")
    private String title;

    @JsonProperty("type")
    private String type;

    @NonNull
    @Override
    public String toString() {
        return "DashboardBanner{" +
                ", short_url='" + short_url + '\'' +
                ", long_url='" + long_url + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

    @NonNull
    @JsonProperty("short_url")
    public String getShort_url() {
        return short_url;
    }

    @JsonProperty("short_url")
    public void setShort_url(@NonNull String short_url) {
        this.short_url = short_url;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("long_url")
    public String getLong_url() {
        return long_url;
    }

    @JsonProperty("long_url")
    public void setLong_url(String long_url) {
        this.long_url = long_url;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }
}