package in.ecomexpress.sathi.repo.remote.model.drs_list.common;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FrwRvpCommonConsigneeDetails implements Parcelable {
    @JsonProperty("name")
    private String name;

    @JsonProperty("phone")
    private String phone;


    @JsonProperty("alternate_number")
    private String alternate_number;


    @JsonProperty("mobile")
    private String mobile;


    @JsonProperty("alternate_mobile")
    private String alternate_mobile;

    @Embedded
    @JsonProperty("address")
    private Address address;

    public FrwRvpCommonConsigneeDetails() {

    }

    public FrwRvpCommonConsigneeDetails(Parcel in) {
        name = in.readString();
        if (in.readByte() == 0) {
            phone = null;
        } else {
            phone = in.readString();
        }

        if (in.readByte() == 0) {
            alternate_number = null;
        } else {
            alternate_number = in.readString();
        }


        if (in.readByte() == 0) {
            mobile = null;
        } else {
            mobile = in.readString();
        }
        if (in.readByte() == 0) {
            alternate_mobile = null;
        } else {
            alternate_mobile = in.readString();
        }
        address = in.readParcelable(Address.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        if (phone == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeString(phone);
        }
        if (alternate_number == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeString(alternate_number);
        }

        if (mobile == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeString(mobile);
        }

        if (alternate_mobile == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeString(alternate_mobile);
        }
        dest.writeParcelable(address, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FrwRvpCommonConsigneeDetails> CREATOR = new Creator<FrwRvpCommonConsigneeDetails>() {
        @Override
        public FrwRvpCommonConsigneeDetails createFromParcel(Parcel in) {
            return new FrwRvpCommonConsigneeDetails(in);
        }

        @Override
        public FrwRvpCommonConsigneeDetails[] newArray(int size) {
            return new FrwRvpCommonConsigneeDetails[size];
        }
    };

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("phone")
    public String getPhone() {
        return phone;
    }

    @JsonProperty("phone")
    public void setPhone(String phone) {
        this.phone = phone;
    }
    @JsonProperty("alternate_number")
    public String getAlternate_number(){
        return alternate_number;
    }
    @JsonProperty("alternate_number")
    public void setAlternate_number(String alternate_number){
        this.alternate_number = alternate_number;
    }

    @JsonProperty("mobile")
    public String getMobile(){
        return mobile;
    }
    @JsonProperty("mobile")
    public void setMobile(String mobile){
        this.mobile = mobile;
    }

    @JsonProperty("alternate_mobile")
    public String getAlternate_mobile(){
        return alternate_mobile;
    }
    @JsonProperty("alternate_mobile")
    public void setAlternate_mobile(String alternate_mobile){
        this.alternate_mobile = alternate_mobile;
    }

    @JsonProperty("address")
    public Address getAddress() {
        return address;
    }

    @JsonProperty("address")
    public void setAddress(Address address) {
        this.address = address;
    }

    @NonNull
    @Override
    public String toString() {
        return "ConsigneeDetails [name=" + name + ", phone=" + phone + ", alternate_number=" + alternate_number +", mobile=" + mobile + ", address=" + address + " , alternate_mobile=" + alternate_mobile + "]";

    }
}