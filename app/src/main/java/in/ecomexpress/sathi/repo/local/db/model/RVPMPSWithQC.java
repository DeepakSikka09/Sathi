package in.ecomexpress.sathi.repo.local.db.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.mps.DRSRvpQcMpsResponse;
import in.ecomexpress.sathi.repo.remote.model.mps.QcItem;
import in.ecomexpress.sathi.repo.remote.model.mps.RvpMpsQualityCheck;

public class RVPMPSWithQC implements Parcelable {
    @Embedded
    public DRSRvpQcMpsResponse drsRvpQcMpsResponse;

    @Relation(parentColumn = "awbNo", entityColumn = "awbNo", entity = QcItem.class)
    public List<QcItem> qcItemList;

    private List<RvpMpsQualityCheck> qualityChecks;

    public RVPMPSWithQC() {
    }

    public DRSRvpQcMpsResponse getDrsRvpQcMpsResponse() {
        return drsRvpQcMpsResponse;
    }

    public void setDrsRvpQcMpsResponse(DRSRvpQcMpsResponse drsRvpQcMpsResponse) {
        this.drsRvpQcMpsResponse = drsRvpQcMpsResponse;
    }

    public List<QcItem> getQcItemList() {
        return qcItemList;
    }

    public void setQcItemList(List<QcItem> qcItemList) {
        this.qcItemList = qcItemList;
    }

    public List<RvpMpsQualityCheck> getQualityChecks() {
        return qualityChecks;
    }

    public void setQualityChecks(List<RvpMpsQualityCheck> qualityChecks) {
        this.qualityChecks = qualityChecks;
    }

    public RVPMPSWithQC(Parcel in) {
        drsRvpQcMpsResponse = in.readParcelable(DRSRvpQcMpsResponse.class.getClassLoader());
        qcItemList = in.createTypedArrayList(QcItem.CREATOR);
        qualityChecks = in.createTypedArrayList(RvpMpsQualityCheck.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(drsRvpQcMpsResponse, flags);
        dest.writeTypedList(qcItemList);
        dest.writeTypedList(qualityChecks);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RVPMPSWithQC> CREATOR = new Creator<RVPMPSWithQC>() {
        @Override
        public RVPMPSWithQC createFromParcel(Parcel in) {
            return new RVPMPSWithQC(in);
        }

        @Override
        public RVPMPSWithQC[] newArray(int size) {
            return new RVPMPSWithQC[size];
        }
    };

    @Override
    public String toString() {
        return "RVPMPSWithQC{" +
                "drsRvpQcMpsResponse=" + drsRvpQcMpsResponse +
                ", qcItemList=" + qcItemList +
                ", qualityChecks=" + qualityChecks +
                '}';
    }
}