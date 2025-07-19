package in.ecomexpress.sathi.repo.remote.model.masterdata;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dhananjayk on 31-07-2018.
 */

public class masterRequest {
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty("user_name")
    private String username;

}
