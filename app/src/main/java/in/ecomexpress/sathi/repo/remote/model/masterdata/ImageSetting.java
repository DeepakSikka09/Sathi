package in.ecomexpress.sathi.repo.remote.model.masterdata;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dhananjayk on 09-02-2019.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageSetting implements Parcelable {
    @JsonProperty("min")
    public int min;

    @JsonProperty("max")
    public int max;

    @JsonProperty("capture_scanned_image")
    public boolean capture_scanned_image;

    @JsonProperty("verify_image_on_server")
    public boolean verify_image_on_server;

    @Embedded
    @JsonProperty("watermark")
    public WaterMark waterMark = new WaterMark();


    protected ImageSetting(Parcel in) {
        min = in.readInt();
        max = in.readInt();
        capture_scanned_image = in.readByte() != 0;
        verify_image_on_server = in.readByte() != 0;
        waterMark = in.readParcelable(WaterMark.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(min);
        dest.writeInt(max);
        dest.writeByte((byte) (capture_scanned_image ? 1 : 0));
        dest.writeByte((byte) (verify_image_on_server ? 1 : 0));
        dest.writeParcelable(waterMark, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageSetting> CREATOR = new Creator<ImageSetting>() {
        @Override
        public ImageSetting createFromParcel(Parcel in) {
            return new ImageSetting(in);
        }

        @Override
        public ImageSetting[] newArray(int size) {
            return new ImageSetting[size];
        }
    };

    @JsonProperty("min")
    public int getMin() {
        return min;
    }

    @JsonProperty("min")
    public void setMin(int min) {
        this.min = min;
    }

    @JsonProperty("max")
    public int getMax() {
        return max;
    }

    @JsonProperty("max")
    public void setMax(int max) {
        this.max = max;
    }

    @JsonProperty("watermark")
    public WaterMark getWaterMark() {
        return waterMark;
    }

    @JsonProperty("watermark")
    public void setWaterMark(WaterMark waterMark) {
        this.waterMark = waterMark;
    }
    @JsonProperty("capture_scanned_image")
    public boolean iscapture_scanned_image() {
        return capture_scanned_image;
    }
    @JsonProperty("capture_scanned_image")
    public void setcapture_scanned_image(boolean capture_scanned_image) {
        this.capture_scanned_image = capture_scanned_image;
    }
    @JsonProperty("verify_image_on_server")
    public boolean isVerify_image_on_server() {
        return verify_image_on_server;
    }
    @JsonProperty("verify_image_on_server")
    public void setVerify_image_on_server(boolean verify_image_on_server) {
        this.verify_image_on_server = verify_image_on_server;
    }

    public ImageSetting() {
    }

}
