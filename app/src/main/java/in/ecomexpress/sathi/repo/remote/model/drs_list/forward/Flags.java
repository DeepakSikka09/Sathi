package in.ecomexpress.sathi.repo.remote.model.drs_list.forward;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Ignore;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Flags implements Parcelable {

    @Embedded
    @JsonProperty("secure_delivery")
    public SecureDelivery secure_delivery;

    @JsonProperty("otp_required")
    private Boolean otpRequired;

    @JsonProperty("sign_image_required")
    public boolean sign_image_required;

    @JsonProperty("return_package_barcode")
    private String return_package_barcode;

    @NonNull
    @JsonIgnore
    @Embedded
    @JsonProperty("flag_map")
    public FlagMap flagMap;

    @JsonProperty("oda_allowed")
    private Boolean odaAllowed;

    @JsonProperty("call_allowed")
    private Boolean callAllowed = true;

    @JsonProperty("is_digital_payment_allowed")
    private Boolean isDigitalPaymentAllowed;

    @JsonProperty("is_amazon_reschedule_enabled")
    private Boolean is_amazon_reschedule_enabled;

    @JsonProperty("otp_required")
    public Boolean getOtpRequired() {
        return otpRequired;
    }

    @JsonProperty("otp_required")
    public void setOtpRequired(Boolean otpRequired) {
        this.otpRequired = otpRequired;
    }
    @JsonProperty("return_package_barcode")
    public String getReturn_package_barcode() {
        return return_package_barcode;
    }
    @JsonProperty("return_package_barcode")
    public void setReturn_package_barcode(String return_package_barcode) {
        this.return_package_barcode = return_package_barcode;
    }

    @JsonProperty("flag_map")
    public FlagMap getFlagMap() {
        return flagMap;
    }

    @JsonProperty("flag_map")
    public void setFlagMap(FlagMap flagMap) {
        this.flagMap = flagMap;
    }

    @JsonProperty("oda_allowed")
    public Boolean getOdaAllowed() {
        return odaAllowed;
    }

    @JsonProperty("oda_allowed")
    public void setOdaAllowed(Boolean odaAllowed) {
        this.odaAllowed = odaAllowed;
    }

    @JsonProperty("call_allowed")
    public Boolean getCallAllowed() {
        return callAllowed;
    }

    @JsonProperty("call_allowed")
    public void setCallAllowed(Boolean callAllowed) {
        this.callAllowed = callAllowed;
    }

    @JsonProperty("is_digital_payment_allowed")
    public Boolean getIsDigitalPaymentAllowed() {
        return isDigitalPaymentAllowed;
    }



    @NonNull
    @Override
    public String toString() {
        return "Flags{" +
                "secure_delivery=" + secure_delivery +
                ", otpRequired=" + otpRequired +
                ", odaAllowed=" + odaAllowed +
                ", callAllowed=" + callAllowed +
                ", isDigitalPaymentAllowed=" + isDigitalPaymentAllowed +
                "return_package_barcode=" + return_package_barcode +
                "flagMap=" + flagMap +
                '}';
    }

    @JsonProperty("is_digital_payment_allowed")
    public void setIsDigitalPaymentAllowed(Boolean isDigitalPaymentAllowed) {
        this.isDigitalPaymentAllowed = isDigitalPaymentAllowed;
    }

    @JsonProperty("secure_delivery")
    public SecureDelivery getSecure_delivery() {
        return secure_delivery;
    }

    @JsonProperty("secure_delivery")
    public void setSecure_delivery(SecureDelivery secure_delivery) {
        this.secure_delivery = secure_delivery;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.secure_delivery, flags);
        dest.writeValue(this.otpRequired);
        dest.writeValue(this.odaAllowed);
        dest.writeValue(this.callAllowed);
        dest.writeValue(this.is_amazon_reschedule_enabled);
        dest.writeValue(this.isDigitalPaymentAllowed);
        dest.writeValue(this.sign_image_required);
        dest.writeString(this.return_package_barcode);
        dest.writeParcelable(this.flagMap, flags);
    }

    public Flags() {
    }

    protected Flags(Parcel in) {
        this.secure_delivery = in.readParcelable(SecureDelivery.class.getClassLoader());
        this.otpRequired = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.odaAllowed = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.callAllowed = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.is_amazon_reschedule_enabled = (Boolean)in.readValue(Boolean.class.getClassLoader());
        this.isDigitalPaymentAllowed = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.sign_image_required = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.return_package_barcode = in.readString();
        this.flagMap = in.readParcelable(FlagMap.class.getClassLoader());

    }

    public static final Creator<Flags> CREATOR = new Creator<Flags>() {
        @Override
        public Flags createFromParcel(Parcel source) {
            return new Flags(source);
        }

        @Override
        public Flags[] newArray(int size) {
            return new Flags[size];
        }
    };

    public Boolean getIs_amazon_reschedule_enabled() {
        return is_amazon_reschedule_enabled;
    }

    public void setIs_amazon_reschedule_enabled(Boolean is_amazon_reschedule_enabled) {
        this.is_amazon_reschedule_enabled = is_amazon_reschedule_enabled;
    }
}