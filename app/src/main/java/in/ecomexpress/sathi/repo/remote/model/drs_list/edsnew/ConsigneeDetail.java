package in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Embedded;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import in.ecomexpress.sathi.repo.remote.model.drs_list.common.Address;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsigneeDetail implements Parcelable {

    @Embedded
    @JsonProperty("address")
    private Address address;

    @JsonProperty("name")
    private String name;

    @JsonProperty("contact_no")
    private String contactNo;

    @JsonProperty("mobile")
    private String mobile;

    @JsonProperty("alternate_mobile")
    private String alternate_mobile;

    @JsonProperty("customerPSTN")
    private String customerPSTN;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getMobile(){
        return mobile;
    }

    public void setMobile(String mobile){
        this.mobile = mobile;
    }

    public String getAlternate_mobile(){
        return alternate_mobile;
    }

    public void setAlternate_mobile(String alternate_mobile){
        this.alternate_mobile = alternate_mobile;
    }

    public String getCustomerPSTN() {
        return customerPSTN;
    }

    public void setCustomerPSTN(String customerPSTN) {
        this.customerPSTN = customerPSTN;
    }

    public ConsigneeDetail() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.address, flags);
        dest.writeString(this.name);
        dest.writeString(this.contactNo);
        dest.writeString(this.mobile);
        dest.writeString(this.alternate_mobile);
        dest.writeString(this.customerPSTN);
    }

    protected ConsigneeDetail(Parcel in) {
        this.address = in.readParcelable(Address.class.getClassLoader());
        this.name = in.readString();
        this.contactNo = in.readString();
        this.mobile = in.readString();
        this.alternate_mobile = in.readString();
        this.customerPSTN = in.readString();
    }

    public static final Creator<ConsigneeDetail> CREATOR = new Creator<ConsigneeDetail>() {
        @Override
        public ConsigneeDetail createFromParcel(Parcel source) {
            return new ConsigneeDetail(source);
        }

        @Override
        public ConsigneeDetail[] newArray(int size) {
            return new ConsigneeDetail[size];
        }
    };
}
