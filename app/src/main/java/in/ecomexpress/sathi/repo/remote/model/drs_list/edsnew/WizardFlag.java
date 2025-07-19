package in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dhananjayk on 26-10-2018.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WizardFlag implements Parcelable{
    @JsonProperty("is_mandate")
    private boolean isMandate;
    @JsonProperty("manifest_again")
    private boolean manigestAgain;
    @JsonProperty("IsrescheduleRequired")
    private boolean isRescheduleRequired;
    @JsonProperty("is_adhar_masking_required")
    private boolean is_adhar_masking_required;

//    is_osv_required": true
    public boolean isMandate() {
        return isMandate;
    }

    public void setMandate(boolean mandate) {
        isMandate = mandate;
    }

    public boolean isManigestAgain() {
        return manigestAgain;
    }

    public void setManigestAgain(boolean manigestAgain) {
        this.manigestAgain = manigestAgain;
    }

    public boolean isRescheduleRequired() {
        return isRescheduleRequired;
    }

    public void setRescheduleRequired(boolean rescheduleRequired) {
        isRescheduleRequired = rescheduleRequired;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isMandate ? (byte) 1 : (byte) 0);
        dest.writeByte(this.manigestAgain ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isRescheduleRequired ? (byte) 1 : (byte) 0);
        dest.writeByte(this.is_adhar_masking_required ? (byte) 1 : (byte) 0);
    }

    public WizardFlag() {
    }

    protected WizardFlag(Parcel in) {
        this.isMandate = in.readByte() != 0;
        this.manigestAgain = in.readByte() != 0;
        this.isRescheduleRequired = in.readByte() != 0;
        this.is_adhar_masking_required = in.readByte() != 0;
    }

    public static final Creator<WizardFlag> CREATOR = new Creator<WizardFlag>() {
        @Override
        public WizardFlag createFromParcel(Parcel source) {
            return new WizardFlag(source);
        }

        @Override
        public WizardFlag[] newArray(int size) {
            return new WizardFlag[size];
        }
    };

    public boolean isIs_adhar_masking_required() {
        return is_adhar_masking_required;
    }

    public void setIs_adhar_masking_required(boolean is_adhar_masking_required) {
        this.is_adhar_masking_required = is_adhar_masking_required;
    }
}
