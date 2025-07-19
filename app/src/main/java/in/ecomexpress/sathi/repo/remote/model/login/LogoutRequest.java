package in.ecomexpress.sathi.repo.remote.model.login;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogoutRequest {

    @JsonProperty("username")
    private String username;
    @JsonProperty("logout_lat")
    private double logoutLat;
    @JsonProperty("logout_lng")
    private double logoutLng;

    public double getLogoutLat() {
        return logoutLat;
    }

    public void setLogoutLat(double logoutLat) {
        this.logoutLat = logoutLat;
    }

    public double getLogoutLng() {
        return logoutLng;
    }

    public void setLogoutLng(double logoutLng) {
        this.logoutLng = logoutLng;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}