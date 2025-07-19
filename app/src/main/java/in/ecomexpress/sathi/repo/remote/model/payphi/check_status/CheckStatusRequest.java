package in.ecomexpress.sathi.repo.remote.model.payphi.check_status;


import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CheckStatusRequest {


    public String getAwbNo() {
        return awbNo;
    }

    public void setAwbNo(String awbNo) {
        this.awbNo = awbNo;
    }

    @PrimaryKey
    @JsonProperty("AwbNo")
    private String awbNo;


    public CheckStatusRequest(String awb) {
        this.awbNo = awb;


    }
}
