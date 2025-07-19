package in.ecomexpress.sathi.repo.remote.model.drs_list.forward;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlagMap implements Parcelable {
    public String getIs_address_updated() {
        return is_address_updated;
    }

    public void setIs_address_updated(String is_address_updated) {
        this.is_address_updated = is_address_updated;
    }

    public String getIs_location_updated() {
        return is_location_updated;
    }

    public void setIs_location_updated(String is_location_updated) {
        this.is_location_updated = is_location_updated;
    }

    public String getIs_mobile_updated() {
        return is_mobile_updated;
    }

    public void setIs_mobile_updated(String is_mobile_updated) {
        this.is_mobile_updated = is_mobile_updated;
    }

    protected FlagMap(Parcel in) {
        this.fwd_del_image = in.readString();
        this.is_callbridge_enabled = in.readString();
        this.is_rvp_phone_pe_flow = in.readString();
        this.is_address_updated = in.readString();
        this.is_mobile_updated = in.readString();
        this.is_location_updated = in.readString();
        this.is_mdc_rvp_qc_disabled = in.readString();
    }

    public String getIs_rvp_phone_pe_flow() {
        return is_rvp_phone_pe_flow;
    }

    public void setIs_rvp_phone_pe_flow(String is_rvp_phone_pe_flow) {
        this.is_rvp_phone_pe_flow = is_rvp_phone_pe_flow;
    }

    public FlagMap() {

    }

    public static final Creator<FlagMap> CREATOR = new Creator<FlagMap>() {
        @Override
        public FlagMap createFromParcel(Parcel in) {
            return new FlagMap(in);
        }

        @Override
        public FlagMap[] newArray(int size) {
            return new FlagMap[size];
        }
    };


    @JsonProperty("is_rvp_phone_pe_flow")
    private String is_rvp_phone_pe_flow = "false";

    public String getIs_mdc_rvp_qc_disabled() {
        return is_mdc_rvp_qc_disabled;
    }

    public void setIs_mdc_rvp_qc_disabled(String is_mdc_rvp_qc_disabled) {
        this.is_mdc_rvp_qc_disabled = is_mdc_rvp_qc_disabled;
    }

    public String getSmart_qc() {
        return smart_qc;
    }

    public void setSmart_qc(String smart_qc) {
        this.smart_qc = smart_qc;
    }

    @JsonProperty("is_mdc_rvp_qc_disabled")
    private String is_mdc_rvp_qc_disabled = "false";
    @JsonProperty("smart_qc")
    private String smart_qc = "false";

    @JsonProperty("is_address_updated")
    private String is_address_updated = "false";
    @JsonProperty("is_mobile_updated")
    private String is_mobile_updated = "false";
    @JsonProperty("is_location_updated")
    private String is_location_updated = "false";

    @JsonProperty("FWD_DEL_IMAGE")
    private String fwd_del_image;

    @JsonProperty("is_callbridge_enabled")
    private String is_callbridge_enabled = "false";

    @JsonProperty("FWD_DEL_IMAGE")
    public String getFwd_del_image() {
        return fwd_del_image;
    }

    @JsonProperty("FWD_DEL_IMAGE")
    public void setFwd_del_image(String fwd_del_image) {
        this.fwd_del_image = fwd_del_image;
    }

    @JsonProperty("is_callbridge_enabled")
    public String getIs_callbridge_enabled() {
        return is_callbridge_enabled;
    }

    @JsonProperty("is_callbridge_enabled")
    public void setIs_callbridge_enabled(String is_callbridge_enabled) {
        this.is_callbridge_enabled = is_callbridge_enabled;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @NonNull
    @Override
    public String toString() {
        return "FlagMap{" + "FWD_DEL_IMAGE=" + fwd_del_image + '}';
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(this.fwd_del_image);
        parcel.writeString(this.is_callbridge_enabled);
        parcel.writeString(this.is_rvp_phone_pe_flow);
        parcel.writeString(this.is_address_updated);
        parcel.writeString(this.is_mobile_updated);
        parcel.writeString(this.is_location_updated);
        parcel.writeString(this.is_mdc_rvp_qc_disabled);
        parcel.writeString(this.smart_qc);
    }
}
