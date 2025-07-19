package in.ecomexpress.sathi.repo.remote.model.login;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OTPVerifyWithPasswordRequest {
    @JsonProperty("username")
    private String userName;
    @JsonProperty("otp")
    private String otp;
    @JsonProperty("new_password")
    private String newPassword;

    public OTPVerifyWithPasswordRequest(String userName, String otp, String newPassword) {
        this.userName = userName;
        this.otp = otp;
        this.newPassword = newPassword;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}