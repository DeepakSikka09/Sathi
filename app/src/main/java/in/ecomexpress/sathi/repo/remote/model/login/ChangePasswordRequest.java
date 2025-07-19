package in.ecomexpress.sathi.repo.remote.model.login;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChangePasswordRequest {
    @JsonProperty("username")
    private String userName;
    @JsonProperty("old_password")
    private String oldPassword;
    @JsonProperty("new_password")
    private String newPassword;

    public ChangePasswordRequest(String usename, String oldPassword, String newPassword){
        this.userName = usename;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getUserName(){
        return userName;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getOldPassword(){
        return oldPassword;
    }

    public void setOldPassword(String oldPassword){
        this.oldPassword = oldPassword;
    }

    public String getNewPassword(){
        return newPassword;
    }

    public void setNewPassword(String newPassword){
        this.newPassword = newPassword;
    }
}