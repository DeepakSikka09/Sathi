package in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew;

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

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShipmentFlag implements Parcelable {

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

    @JsonProperty("is_dc_enabled")
    private boolean idDcEnabled;

    @JsonProperty("call_allowed")
    private boolean call_allowed;

    @JsonProperty("IsmobileValidationRequired")
    private boolean isMobileValidationRequired;

    @JsonProperty("reschedule_enable")
    private boolean reschedule_enable;

    @JsonProperty("is_kyc_active")
    private boolean is_kyc_active;

    @NonNull
    @JsonIgnore
    @Embedded
    @JsonProperty("flag_map")
    public FlagMap flagMap;

    @NonNull
    @JsonProperty("flag_map")
    public FlagMap getFlagMap() {
        return flagMap;
    }
    @JsonProperty("flag_map")
    public void setFlagMap(@NonNull FlagMap flagMap) {
        this.flagMap = flagMap;
    }

    public boolean isIs_kyc_active() {
        return is_kyc_active;
    }

    public void setIs_kyc_active(boolean is_kyc_active) {
        this.is_kyc_active = is_kyc_active;
    }

    public boolean isReschedule_enable() {
        return reschedule_enable;
    }

    public void setReschedule_enable(boolean reschedule_disable) {
        this.reschedule_enable = reschedule_disable;
    }

    public boolean isIdDcEnabled() {
        return idDcEnabled;
    }

    public void setIdDcEnabled(boolean idDcEnabled) {
        this.idDcEnabled = idDcEnabled;
    }

    public boolean isMobileValidationRequired() {
        return isMobileValidationRequired;
    }

    public void setMobileValidationRequired(boolean mobileValidationRequired) {
        isMobileValidationRequired = mobileValidationRequired;
    }


    public ShipmentFlag() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.secure_delivery, flags);
        dest.writeByte(this.idDcEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isMobileValidationRequired ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.flagMap, flags);
    }

    protected ShipmentFlag(Parcel in) {
        this.secure_delivery = in.readParcelable(SecureDelivery.class.getClassLoader());
        this.idDcEnabled = in.readByte() != 0;
        this.isMobileValidationRequired = in.readByte() != 0;
        this.flagMap = in.readParcelable(FlagMap.class.getClassLoader());
    }

    public static final Creator<ShipmentFlag> CREATOR = new Creator<ShipmentFlag>() {
        @Override
        public ShipmentFlag createFromParcel(Parcel source) {
            return new ShipmentFlag(source);
        }

        @Override
        public ShipmentFlag[] newArray(int size) {
            return new ShipmentFlag[size];
        }
    };

    public boolean isCall_allowed(){
        return call_allowed;
    }

    public void setCall_allowed(boolean call_allowed){
        this.call_allowed = call_allowed;
    }
}
