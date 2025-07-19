package in.ecomexpress.sathi.repo.local.db.model;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.RvpQualityCheck;

/**
 * Created by Ashish Patel on 6/15/2018.
 */
public class RvpWithQC implements Parcelable {
    @Embedded
    public DRSReverseQCTypeResponse drsReverseQCTypeResponse;

    @Relation(parentColumn = "awbNo", entityColumn = "awbNo", entity = RvpQualityCheck.class)
    public List<RvpQualityCheck> rvpQualityCheckList;

    public RvpWithQC() {
    }


    public RvpWithQC(Parcel in) {
        drsReverseQCTypeResponse = in.readParcelable(DRSReverseQCTypeResponse.class.getClassLoader());
        rvpQualityCheckList = in.createTypedArrayList(RvpQualityCheck.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(drsReverseQCTypeResponse, flags);
        dest.writeTypedList(rvpQualityCheckList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RvpWithQC> CREATOR = new Creator<RvpWithQC>() {
        @Override
        public RvpWithQC createFromParcel(Parcel in) {
            return new RvpWithQC(in);
        }

        @Override
        public RvpWithQC[] newArray(int size) {
            return new RvpWithQC[size];
        }
    };

    @Override
    public String toString() {
        return "RvpWithQC{" +
                "drsReverseQCTypeResponse=" + drsReverseQCTypeResponse +
                ", rvpQualityCheckList=" + rvpQualityCheckList +
                '}';
    }
}
