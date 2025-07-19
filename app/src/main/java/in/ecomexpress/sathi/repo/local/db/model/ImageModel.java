package in.ecomexpress.sathi.repo.local.db.model;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

//for handle image
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "Image")
public class ImageModel implements Parcelable {

    //for unique a shipment and check data with respect to awb_no and relation contain with push table
    private String awbNo;
    private String draNo;
    //its combination of awb_no drs no and image code
    @PrimaryKey
    @NonNull
    private String imageName;
    //its unique code for every image with respect to awb
    private String imageCode;
    //its file path
    private String image;
    //shipment type name
    private String shipmentType;


    // image capture time
    private Long date;

    private String imageType;

    //image sync status
    private int status;

    //image id
    private int imageId;


    private long imageFutureSyncTime;

    private int imageCurrentSyncStatus;

    public ImageModel() {

    }


    protected ImageModel(Parcel in) {

        awbNo = in.readString();
        draNo = in.readString();
        imageName = in.readString();
        imageCode = in.readString();
        image = in.readString();
        imageCurrentSyncStatus=in.readInt();
        imageFutureSyncTime=in.readLong();

        shipmentType = in.readString();
        if (in.readByte() == 0) {
            date = null;
        } else {
            date = in.readLong();
        }
        imageType = in.readString();
        status = in.readInt();
        imageId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(awbNo);
        dest.writeString(draNo);
        dest.writeString(imageName);
        dest.writeString(imageCode);
        dest.writeString(image);
        dest.writeString(shipmentType);
        if (date == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(date);
        }
        dest.writeLong(imageFutureSyncTime);
        dest.writeInt(imageCurrentSyncStatus);
        dest.writeString(imageType);
        dest.writeInt(status);
        dest.writeInt(imageId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageModel> CREATOR = new Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel in) {
            return new ImageModel(in);
        }

        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };

    public String getAwbNo() {
        return awbNo;
    }

    public void setAwbNo(String awbNo) {
        this.awbNo = awbNo;
    }

    public String getDraNo() {
        return draNo;
    }

    public void setDraNo(String draNo) {
        this.draNo = draNo;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageCode() {
        return imageCode;
    }

    public void setImageCode(String imageCode) {
        this.imageCode = imageCode;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getShipmentType() {
        return shipmentType;
    }

    public void setShipmentType(String shipmentType) {
        this.shipmentType = shipmentType;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }



    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }


    public long getImageFutureSyncTime() {
        return imageFutureSyncTime;
    }

    public void setImageFutureSyncTime(long imageFutureSyncTime) {
        this.imageFutureSyncTime = imageFutureSyncTime;
    }

    public int getImageCurrentSyncStatus() {
        return imageCurrentSyncStatus;
    }

    public void setImageCurrentSyncStatus(int imageCurrentSyncStatus) {
        this.imageCurrentSyncStatus = imageCurrentSyncStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageModel that = (ImageModel) o;

        if (status != that.status) return false;
        if (!awbNo.equals(that.awbNo)) return false;
        if (!draNo.equals(that.draNo)) return false;
        if (!imageName.equals(that.imageName)) return false;
        if (!imageCode.equals(that.imageCode)) return false;
        if (!image.equals(that.image)) return false;
        if (!shipmentType.equals(that.shipmentType)) return false;
        return date.equals(that.date);
    }

    @Override
    public int hashCode() {
        int result = awbNo.hashCode();
        result = 31 * result + imageName.hashCode();

        return result;
    }

    @Override
    public String toString() {
        return "ImageModel{" +

                ", awbNo='" + awbNo + '\'' +
                ", draNo='" + draNo + '\'' +
                ", imageName='" + imageName + '\'' +
                ", imageCode='" + imageCode + '\'' +
                ", image='" + image + '\'' +
                ", shipmentType='" + shipmentType + '\'' +
                ", date=" + date +
                ", imageType='" + imageType + '\'' +
                ", status=" + status +
                '}';
    }
}
