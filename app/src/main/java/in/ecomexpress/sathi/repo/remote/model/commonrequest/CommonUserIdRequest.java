package in.ecomexpress.sathi.repo.remote.model.commonrequest;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Ashish Patel on 6/20/2018.
 */
public class CommonUserIdRequest {
    @JsonProperty("user_name")
    private String userName;

    public CommonUserIdRequest(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
