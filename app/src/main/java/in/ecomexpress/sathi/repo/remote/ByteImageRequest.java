package in.ecomexpress.sathi.repo.remote;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ByteImageRequest implements Parcelable {

    public static final Creator<ByteImageRequest> CREATOR = new Creator<ByteImageRequest>() {
        @Override
        public ByteImageRequest createFromParcel(Parcel in) {
            return new ByteImageRequest(in);
        }

        @Override
        public ByteImageRequest[] newArray(int size) {
            return new ByteImageRequest[size];
        }
    };
    @JsonProperty("image")
    private String image;
    @JsonProperty("awb_no")
    private long awb_no;
    @JsonProperty("drs_no")
    private long drs_no;
    @JsonProperty("image_code")
    private String image_code;
    @JsonProperty("image_name")
    private String image_name;
    @JsonProperty("image_type")
    private String image_type;

    public ByteImageRequest() {
    }

    protected ByteImageRequest(Parcel in) {
        image = in.readString();
        awb_no = in.readLong();
        drs_no = in.readLong();
        image_code = in.readString();
        image_name = in.readString();
        image_type = in.readString();
    }

    public static Creator<ByteImageRequest> getCREATOR() {
        return CREATOR;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getAwb_no() {
        return awb_no;
    }

    public void setAwb_no(long awb_no) {
        this.awb_no = awb_no;
    }

    public long getDrs_no() {
        return drs_no;
    }

    public void setDrs_no(long drs_no) {
        this.drs_no = drs_no;
    }

    public String getImage_code() {
        return image_code;
    }

    public void setImage_code(String image_code) {
        this.image_code = image_code;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public String getImage_type() {
        return image_type;
    }

    public void setImage_type(String image_type) {
        this.image_type = image_type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(image);
        parcel.writeLong(awb_no);
        parcel.writeLong(drs_no);
        parcel.writeString(image_code);
        parcel.writeString(image_name);
        parcel.writeString(image_type);
    }
}
