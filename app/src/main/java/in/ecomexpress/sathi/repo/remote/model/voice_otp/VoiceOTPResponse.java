package in.ecomexpress.sathi.repo.remote.model.voice_otp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VoiceOTPResponse {
    public int code;
    public String description;
    public String employee_code;
}