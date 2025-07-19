package in.ecomexpress.sathi.repo.remote.model.eds;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Get_Status_Masking implements Parcelable {
    @JsonProperty("image_1")
    public String image_1;
    @JsonProperty("image_2")
    public String image_2;


    public Get_Status_Masking(String image_1, String image_2)
    {
        this.image_1 = image_1;
        this.image_2 =image_2;
    }

    protected Get_Status_Masking(Parcel in) {
        image_1 = in.readString();
        image_2 = in.readString();
    }

    public static final Creator<Get_Status_Masking> CREATOR = new Creator<Get_Status_Masking>() {
        @Override
        public Get_Status_Masking createFromParcel(Parcel in) {
            return new Get_Status_Masking(in);
        }

        @Override
        public Get_Status_Masking[] newArray(int size) {
            return new Get_Status_Masking[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(image_1);
        parcel.writeString(image_2);
    }
}



