package in.ecomexpress.sathi.repo.remote.model.login;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ForgetPasswordUserRequest {

    @JsonProperty("username")
    private String userName;

    public ForgetPasswordUserRequest(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}