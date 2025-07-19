package in.ecomexpress.sathi.repo.local.db.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;


/**
 * Created by dhananjayk on 29-10-2018.
 */

public class EdsWithActivityList implements Parcelable {
    @Embedded
    public EDSResponse edsResponse;

    @Relation(parentColumn = "awbNo", entityColumn = "awbNo", entity = EDSActivityWizard.class)
    public List<EDSActivityWizard> edsActivityWizards;


    public EdsWithActivityList() {

    }

    public EdsWithActivityList(Parcel in) {
        edsResponse = in.readParcelable(EDSResponse.class.getClassLoader());
        edsActivityWizards = in.createTypedArrayList(EDSActivityWizard.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(edsResponse, flags);
        dest.writeTypedList(edsActivityWizards);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EdsWithActivityList> CREATOR = new Creator<EdsWithActivityList>() {
        @Override
        public EdsWithActivityList createFromParcel(Parcel in) {
            return new EdsWithActivityList(in);
        }

        @Override
        public EdsWithActivityList[] newArray(int size) {
            return new EdsWithActivityList[size];
        }
    };


    public EDSResponse getEdsResponse() {
        return edsResponse;
    }

    public void setEdsResponse(EDSResponse edsResponse) {
        this.edsResponse = edsResponse;
    }

    public List<EDSActivityWizard> getEdsActivityWizards() {
        return edsActivityWizards;
    }

    public void setEdsActivityWizards(List<EDSActivityWizard> edsActivityWizards) {
        this.edsActivityWizards = edsActivityWizards;
    }

    @Override
    public String toString() {
        return "EdsWithActivityList{" +
                "edsResponse=" + edsResponse +
                ", edsActivityWizards=" + edsActivityWizards +
                '}';
    }


}
