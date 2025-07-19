package in.ecomexpress.sathi.repo.remote.model.drs_list.forward;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SecureDelivery implements Parcelable {

    @NonNull
    @Override
    public String toString() {
        return "SecureDelivery{" +
                "OTP=" + OTP +
                ", secure_pin=" + secure_pin +
                ", cancellation_enabled=" + cancellation_enabled +
                ", RCHD_enabled=" + RCHD_enabled +
                ", pinb=" + pinb +
                ", pinb_details=" + pinb_details +
                '}';
    }

    @JsonProperty("OTP")
    private Boolean OTP;


    @JsonProperty("secure_pin")
    private Boolean secure_pin;


    @JsonProperty("cancellation_enabled")
    private boolean cancellation_enabled;

    @JsonProperty("RCHD_enabled")
    private boolean RCHD_enabled;

    @JsonProperty("pinb")
    private Boolean pinb;

    @Embedded
    @JsonProperty("pinb_details")
    private PinbDetails pinb_details;

    @JsonProperty("is_resend_OTP_allow")
    private boolean resend_otp_enable;

    @JsonProperty("OTP")
    public Boolean getOTP() {
        return OTP;
    }

    @JsonProperty("OTP")
    public void setOTP(Boolean OTP) {
        this.OTP = OTP;
    }

    @JsonProperty("secure_pin")
    public Boolean getSecure_pin() {
        return secure_pin;
    }

    @JsonProperty("secure_pin")
    public void setSecure_pin(Boolean secure_pin) {
        this.secure_pin = secure_pin;
    }

    @JsonProperty("cancellation_enabled")
    public boolean getCancellation_enabled(){
        return cancellation_enabled;
    }
    @JsonProperty("cancellation_enabled")
    public void setCancellation_enabled(boolean cancellation_enabled){
        this.cancellation_enabled = cancellation_enabled;
    }

    @JsonProperty("RCHD_enabled")
    public boolean getRCHD_enabled(){
        return RCHD_enabled;
    }
    @JsonProperty("RCHD_enabled")
    public void setRCHD_enabled(boolean RCHD_enabled){
        this.RCHD_enabled = RCHD_enabled;
    }

    @JsonProperty("pinb")
    public Boolean getPinb() {
        return pinb;
    }

    @JsonProperty("pinb")
    public void setPinb(Boolean pinb) {
        this.pinb = pinb;
    }

    @JsonProperty("pinb_details")
    public PinbDetails getPinb_details() {
        return pinb_details;
    }

    @JsonProperty("pinb_details")
    public void setPinb_details(PinbDetails pinb_details) {
        this.pinb_details = pinb_details;
    }

    @JsonProperty("is_resend_OTP_allow")
    public void setResend_otp_enable(boolean resend_otp_enable) {
        this.resend_otp_enable = resend_otp_enable;
    }
    @JsonProperty("is_resend_OTP_allow")
    public boolean getResend_otp_enable() {
        return resend_otp_enable;
    }


    public SecureDelivery() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.OTP);
        dest.writeValue(this.secure_pin);
        dest.writeValue(this.cancellation_enabled);
        dest.writeValue(this.RCHD_enabled);
        dest.writeValue(this.pinb);
        dest.writeParcelable(this.pinb_details, flags);
    }

    protected SecureDelivery(Parcel in) {
        this.OTP = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.secure_pin = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.cancellation_enabled = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.RCHD_enabled = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.pinb = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.pinb_details = in.readParcelable(PinbDetails.class.getClassLoader());
    }

    public static final Creator<SecureDelivery> CREATOR = new Creator<SecureDelivery>() {
        @Override
        public SecureDelivery createFromParcel(Parcel source) {
            return new SecureDelivery(source);
        }

        @Override
        public SecureDelivery[] newArray(int size) {
            return new SecureDelivery[size];
        }
    };
}