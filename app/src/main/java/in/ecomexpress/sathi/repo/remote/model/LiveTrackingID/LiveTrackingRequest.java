package in.ecomexpress.sathi.repo.remote.model.LiveTrackingID;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by o74884 on 21-08-2019.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveTrackingRequest implements Parcelable
{
    @JsonProperty("employee_code")
    private final String emp_code;
    @JsonProperty("trip_id")
    private int trip_id;

    public LiveTrackingRequest(String emp_code , int trip_id) {
        this.emp_code=emp_code;
        this.trip_id = trip_id;

    }

    protected LiveTrackingRequest(Parcel in) {
        emp_code = in.readString();
        trip_id= in.readInt();
    }

    public static final Creator<LiveTrackingRequest> CREATOR = new Creator<LiveTrackingRequest>() {
        @Override
        public LiveTrackingRequest createFromParcel(Parcel in) {
            return new LiveTrackingRequest(in);
        }

        @Override
        public LiveTrackingRequest[] newArray(int size) {
            return new LiveTrackingRequest[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(emp_code);
        parcel.writeInt(trip_id);
    }

    public int getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(int trip_id) {
        this.trip_id = trip_id;
    }
}

