package in.ecomexpress.sathi.repo.remote.model.drs_list.eds;

import com.fasterxml.jackson.annotation.JsonProperty;

import in.ecomexpress.sathi.repo.remote.model.base.ApiRequest;

public class UploadImageRequest extends ApiRequest {

    @JsonProperty("awb")
    private long awb;

    @JsonProperty("drs_id")
    private int drsID;

    @JsonProperty("activity_code")
    private String ActivityCode;

    public UploadImageRequest(long awb, int drs_id, String activity_code) {

        this.awb = awb;
        this.drsID = drs_id;
        this.ActivityCode = activity_code;
    }

    public long getAwb() {
        return awb;
    }

    public void setAwb(long awb) {
        this.awb = awb;
    }

    public int getDrsID() {
        return drsID;
    }

    public void setDrsID(int drsID) {
        this.drsID = drsID;
    }

    public String getActivityCode() {
        return ActivityCode;
    }

    public void setActivityCode(String activityCode) {
        ActivityCode = activityCode;
    }
}