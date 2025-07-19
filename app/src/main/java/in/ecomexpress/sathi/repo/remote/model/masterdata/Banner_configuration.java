package in.ecomexpress.sathi.repo.remote.model.masterdata;



import androidx.room.Ignore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by shivangis on 3/1/2019.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Banner_configuration {


    @JsonProperty("dashboard_banner")
    public List<DashboardBanner>  getDashboard_banner() {
        return dashboard_banner;
    }

    @JsonProperty("dashboard_banner")
    public void setDashboard_banner(List<DashboardBanner> dashboard_banner) {
        this.dashboard_banner = dashboard_banner;
    }

    @Ignore
    @JsonProperty("dashboard_banner")
    public List<DashboardBanner> dashboard_banner;

    @Override
    public String toString() {
        return "Banner_configuration [dashboard_banner = " + dashboard_banner + "]";
    }
}
