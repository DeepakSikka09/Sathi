package in.ecomexpress.sathi.repo.local.data.activitiesdata;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.Objects;

import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.ShipmentsDetail;

public class RTSActivitiesData implements Parcelable {
    private String address;
    private String awb;
    private String consigneeMobile;
    private String consigneeName;
    private String decideNext;
    private Long rtsVWDetailID;
    private boolean allPacketUndelivered;
    private ArrayList<String> awbArray;
    private ArrayList<ShipmentsDetail> shipmentsDetailsData;

    public RTSActivitiesData() {
    }

    public static final Creator<RTSActivitiesData> CREATOR = new Creator<RTSActivitiesData>() {
        @Override
        public RTSActivitiesData createFromParcel(Parcel in) {
            return new RTSActivitiesData(in);
        }

        @Override
        public RTSActivitiesData[] newArray(int size) {
            return new RTSActivitiesData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(address != null ? 1 : 0);
        if (address != null) {
            dest.writeString(address);
        }

        dest.writeInt(awb != null ? 1 : 0);
        if (awb != null) {
            dest.writeString(awb);
        }

        dest.writeInt(consigneeMobile != null ? 1 : 0);
        if (consigneeMobile != null) {
            dest.writeString(consigneeMobile);
        }

        dest.writeInt(consigneeName != null ? 1 : 0);
        if (consigneeName != null) {
            dest.writeString(consigneeName);
        }

        dest.writeInt(decideNext != null ? 1 : 0);
        if (decideNext != null) {
            dest.writeString(decideNext);
        }

        dest.writeInt(rtsVWDetailID != null ? 1 : 0);
        if (rtsVWDetailID != null) {
            dest.writeLong(rtsVWDetailID);
        }

        dest.writeInt(allPacketUndelivered ? 1 : 0);

        dest.writeInt(awbArray != null ? 1 : 0);
        if (awbArray != null) {
            dest.writeStringList(awbArray);
        }

        dest.writeInt(shipmentsDetailsData != null ? 1 : 0);
        if (shipmentsDetailsData != null) {
            dest.writeList(shipmentsDetailsData);
        }
    }

    protected RTSActivitiesData(Parcel in) {
        int addressIndicator = in.readInt();
        if (addressIndicator == 1) {
            address = in.readString();
        }

        int awbIndicator = in.readInt();
        if (awbIndicator == 1) {
            awb = in.readString();
        }

        int consigneeMobileIndicator = in.readInt();
        if (consigneeMobileIndicator == 1) {
            consigneeMobile = in.readString();
        }

        int consigneeNameIndicator = in.readInt();
        if (consigneeNameIndicator == 1) {
            consigneeName = in.readString();
        }

        int decideNextIndicator = in.readInt();
        if (decideNextIndicator == 1) {
            decideNext = in.readString();
        }

        int rtsVWDetailIDIndicator = in.readInt();
        if (rtsVWDetailIDIndicator == 1) {
            rtsVWDetailID = in.readLong();
        }

        allPacketUndelivered = in.readInt() == 1;

        int flowerArrayIndicator = in.readInt();
        if (flowerArrayIndicator == 1) {
            awbArray = new ArrayList<>(Objects.requireNonNull(in.createStringArrayList()));
        }

        int shipmentsDetailsDataIndicator = in.readInt();
        if (shipmentsDetailsDataIndicator == 1) {
            shipmentsDetailsData = new ArrayList<>();
            in.readList(shipmentsDetailsData, ShipmentsDetail.class.getClassLoader());
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAwb() {
        return awb;
    }

    public void setAwb(String awb) {
        this.awb = awb;
    }

    public String getConsigneeMobile() {
        return consigneeMobile;
    }

    public void setConsigneeMobile(String consigneeMobile) {
        this.consigneeMobile = consigneeMobile;
    }

    public Long getRtsVWDetailID() {
        return rtsVWDetailID;
    }

    public void setRtsVWDetailID(Long rtsVWDetailID) {
        this.rtsVWDetailID = rtsVWDetailID;
    }

    public ArrayList<String> getAwbArray() {
        return awbArray;
    }

    public void setAwbArray(ArrayList<String> awbArray) {
        this.awbArray = awbArray;
    }

    public ArrayList<ShipmentsDetail> getShipmentsDetailsData() {
        return shipmentsDetailsData;
    }

    public void setShipmentsDetailsData(ArrayList<ShipmentsDetail> shipmentsDetailsData) {
        this.shipmentsDetailsData = shipmentsDetailsData;
    }

    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public String getDecideNext() {
        return decideNext;
    }

    public void setDecideNext(String decideNext) {
        this.decideNext = decideNext;
    }

    public boolean isAllPacketUndelivered() {
        return allPacketUndelivered;
    }

    public void setAllPacketUndelivered(boolean allPacketUndelivered) {
        this.allPacketUndelivered = allPacketUndelivered;
    }
}
