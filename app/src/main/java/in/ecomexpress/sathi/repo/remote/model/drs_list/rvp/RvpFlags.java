package in.ecomexpress.sathi.repo.remote.model.drs_list.rvp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Embedded;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.FlagMap;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.SecureDelivery;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RvpFlags implements Parcelable {

    @Embedded
    @JsonProperty("secure_delivery")
    private SecureDelivery secure_delivery;

    @JsonProperty("secure_delivery")
    public SecureDelivery getSecure_delivery() {
        return secure_delivery;
    }
    @JsonProperty("secure_delivery")
    public void setSecure_delivery(SecureDelivery secure_delivery) {
        this.secure_delivery = secure_delivery;
    }
    @JsonProperty("sign_image_required")
    private boolean sign_image_required;

    @NonNull
    @JsonIgnore
    @Embedded
    @JsonProperty("flag_map")
    public FlagMap flagMap;

    @JsonProperty("otp_required_for_delivery")
    private String otp_required_for_delivery;

    @JsonProperty("sign_image_required")
    public boolean isSign_image_required(){
        return sign_image_required;
    }
    @JsonProperty("sign_image_required")
    public void setSign_image_required(boolean sign_image_required){
        this.sign_image_required = sign_image_required;
    }

    @NonNull
    @JsonProperty("flag_map")
    public FlagMap getFlagMap() {
        return flagMap;
    }
    @JsonProperty("flag_map")
    public void setFlagMap(@NonNull FlagMap flagMap) {
        this.flagMap = flagMap;
    }

    @JsonProperty("otp_required")
    private Boolean otpRequired;
    @JsonProperty("opa_allowed")
    private Boolean opaAllowed;
    @JsonProperty("call_allowed")
    private Boolean callAllowed;

    @JsonProperty("otp_required")
    public Boolean getOtpRequired() {
        return otpRequired;
    }

    @JsonProperty("otp_required")
    public void setOtpRequired(Boolean otpRequired) {
        this.otpRequired = otpRequired;
    }

    @JsonProperty("opa_allowed")
    public Boolean getOpaAllowed() {
        return opaAllowed;
    }

    @JsonProperty("opa_allowed")
    public void setOpaAllowed(Boolean opaAllowed) {
        this.opaAllowed = opaAllowed;
    }

    @JsonProperty("call_allowed")
    public Boolean getCallAllowed() {
        return callAllowed;
    }

    @JsonProperty("call_allowed")
    public void setCallAllowed(Boolean callAllowed) {
        this.callAllowed = callAllowed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.secure_delivery, flags);
        dest.writeValue(this.otpRequired);
        dest.writeValue(this.opaAllowed);
        dest.writeValue(this.callAllowed);
        dest.writeParcelable(this.flagMap, flags);
    }

    public RvpFlags() {
    }

    protected RvpFlags(Parcel in) {
        this.secure_delivery = in.readParcelable(SecureDelivery.class.getClassLoader());
        this.otpRequired = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.opaAllowed = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.callAllowed = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.flagMap = in.readParcelable(FlagMap.class.getClassLoader());
    }

    public static final Creator<RvpFlags> CREATOR = new Creator<RvpFlags>() {
        @Override
        public RvpFlags createFromParcel(Parcel source) {
            return new RvpFlags(source);
        }

        @Override
        public RvpFlags[] newArray(int size) {
            return new RvpFlags[size];
        }
    };

    public String getOtp_required_for_delivery() {
        return otp_required_for_delivery;
    }

    public void setOtp_required_for_delivery(String otp_required_for_delivery) {
        this.otp_required_for_delivery = otp_required_for_delivery;
    }

}
