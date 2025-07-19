package in.ecomexpress.sathi.repo.remote.model.drs_list.rts;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RtsVendorDetails  {
	@JsonProperty("vendor_pincode")
    private String vendorPincode;
    
    @JsonProperty("vendor_name")
    private String vendorName;
    
    @JsonProperty("vendor_contact_no")
    private String vendorContactNo;
    
    @JsonProperty("vendor_id")
    private String vendorId;
    
    @JsonProperty("vendor_address")
    private String vendorAddress;

    @JsonProperty("vendor_pincode")
    public String getVendorPincode() {
        return vendorPincode;
    }

    @JsonProperty("vendor_pincode")
    public void setVendorPincode(String vendorPincode) {
        this.vendorPincode = vendorPincode;
    }

    @JsonProperty("vendor_name")
    public String getVendorName() {
        return vendorName;
    }

    @JsonProperty("vendor_name")
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    @JsonProperty("vendor_contact_no")
    public String getVendorContactNo() {
        return vendorContactNo;
    }

    @JsonProperty("vendor_contact_no")
    public void setVendorContactNo(String vendorContactNo) {
        this.vendorContactNo = vendorContactNo;
    }

    @JsonProperty("vendor_id")
    public String getVendorId() {
        return vendorId;
    }

    @JsonProperty("vendor_id")
    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    @JsonProperty("vendor_address")
    public String getVendorAddress() {
        return vendorAddress;
    }

    @JsonProperty("vendor_address")
    public void setVendorAddress(String vendorAddress) {
        this.vendorAddress = vendorAddress;
    }

	@NonNull
    @Override
	public String toString() {
		return "VendorDetails [vendorPincode=" + vendorPincode + ", vendorName=" + vendorName + ", vendorContactNo="
				+ vendorContactNo + ", vendorId=" + vendorId + ", vendorAddress=" + vendorAddress + "]";
	}
}
