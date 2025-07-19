package in.ecomexpress.sathi.repo.remote.model.drs_list.AmazonReschedule;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.DeviceDetails;
import in.ecomexpress.sathi.repo.remote.model.base.ApiRequest;

public  class AmazonScheduleRequest extends ApiRequest {

    @JsonProperty("awb_number")
    private String awb_number_list;


    public AmazonScheduleRequest(String awb) {
        this.awb_number_list = awb;
    }


    public String getAwb_number_list() {
        return awb_number_list;
    }

    public void setAwb_number_list(String awb_number_list) {
        this.awb_number_list = awb_number_list;
    }
}