package in.ecomexpress.sathi.repo.remote.model.trip;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageResponse implements Parcelable{
    @JsonProperty("image_key")
    private String imageKey;
    @JsonProperty("image_id")
    private int imageId;

    public ImageResponse(String imageKey, int imageId) {
        this.imageKey = imageKey;
        this.imageId = imageId;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imageKey);
        dest.writeInt(this.imageId);
    }

    public ImageResponse() {
    }

    protected ImageResponse(Parcel in) {
        this.imageKey = in.readString();
        this.imageId = in.readInt();
    }

    public static final Creator<ImageResponse> CREATOR = new Creator<ImageResponse>() {
        @Override
        public ImageResponse createFromParcel(Parcel source) {
            return new ImageResponse(source);
        }

        @Override
        public ImageResponse[] newArray(int size) {
            return new ImageResponse[size];
        }
    };
}
