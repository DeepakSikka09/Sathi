package in.ecomexpress.sathi.repo.local.data.eds;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public class EDSActivityImageRequest implements Parcelable {
    @JsonProperty("image_key")
    private String imageKey=null;
    @JsonProperty("image_id")
    private String imageId=null;

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imageKey);
        dest.writeString(this.imageId);
    }

    public EDSActivityImageRequest() {
    }

    protected EDSActivityImageRequest(Parcel in) {
        this.imageKey = in.readString();
        this.imageId = in.readString();
    }

    public static final Creator<EDSActivityImageRequest> CREATOR = new Creator<EDSActivityImageRequest>() {
        @Override
        public EDSActivityImageRequest createFromParcel(Parcel source) {
            return new EDSActivityImageRequest(source);
        }

        @Override
        public EDSActivityImageRequest[] newArray(int size) {
            return new EDSActivityImageRequest[size];
        }
    };
}
