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
public class LiveTrackingResponse implements Parcelable
{
    @JsonProperty("status")
    private String status;
    @JsonProperty("code")
    private String code;
    @JsonProperty("liveTrackingId")
    private String live_tracking_id;

    public LiveTrackingResponse()
    {

    }

    public LiveTrackingResponse(String status , String live_tracking_id , String code)
    {
        this.status = status;
        this.code =code;
        this.live_tracking_id =live_tracking_id;
    }


    protected LiveTrackingResponse(Parcel in) {
        status = in.readString();
        code = in.readString();
        live_tracking_id = in.readString();
    }

    public static final Creator<LiveTrackingResponse> CREATOR = new Creator<LiveTrackingResponse>() {
        @Override
        public LiveTrackingResponse createFromParcel(Parcel in) {
            return new LiveTrackingResponse(in);
        }

        @Override
        public LiveTrackingResponse[] newArray(int size) {
            return new LiveTrackingResponse[size];
        }
    };

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLive_tracking_id() {
        return live_tracking_id;
    }

    public void setLive_tracking_id(String live_tracking_id) {
        this.live_tracking_id = live_tracking_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(status);
        parcel.writeString(code);
        parcel.writeString(live_tracking_id);
    }
}

