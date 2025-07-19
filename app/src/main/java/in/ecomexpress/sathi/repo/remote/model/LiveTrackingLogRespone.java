package in.ecomexpress.sathi.repo.remote.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Ignore;

public class LiveTrackingLogRespone  {


    @Ignore
    String status;

    @Ignore
    String description;

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }
}
