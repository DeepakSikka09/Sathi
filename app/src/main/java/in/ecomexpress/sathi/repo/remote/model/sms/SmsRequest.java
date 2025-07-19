package in.ecomexpress.sathi.repo.remote.model.sms;



import androidx.room.Embedded;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by shivangis on 2/26/2019.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class SmsRequest {

    @Embedded
    @JsonProperty("additional_info")
    private Additional_info additional_info;

    @JsonProperty("sms_type")
    private String sms_type;

    @JsonProperty("additional_info")
    public Additional_info getAdditional_info() {
        return additional_info;
    }

    @JsonProperty("additional_info")
    public void setAdditional_info(Additional_info additional_info) {
        this.additional_info = additional_info;
    }

    @JsonProperty("sms_type")
    public String getSms_type() {
        return sms_type;
    }

    @JsonProperty("sms_type")
    public void setSms_type(String sms_type) {
        this.sms_type = sms_type;
    }


    @JsonProperty("awb")
    private ArrayList<String> awb;

    @JsonProperty("date")
    private String date;

    @JsonProperty("lng")
    private double lng;

    @JsonProperty("lat")
    private double lat;

  /*  public SmsRequest(ArrayList<String> awblist, double currentLatitude, double currentLongitude, String wDate) {
        this.awb = awblist;
        this.lat = currentLatitude;
        this.lng = currentLongitude;
        this.date = wDate;


    }*/


    @JsonProperty("lng")
    public double getLng() {
        return lng;
    }

    @JsonProperty("lng")
    public void setLng(double lng) {
        this.lng = lng;
    }

    @JsonProperty("awb")
    public ArrayList<String> getAwb() {
        return awb;
    }

    @JsonProperty("awb")
    public void setAwb(ArrayList<String> awb) {
        this.awb = awb;
    }

    @JsonProperty("lat")
    public double getLat() {
        return lat;
    }

    @JsonProperty("lat")
    public void setLat(double lat) {
        this.lat = lat;
    }


    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(String date) {
        this.date = date;
    }


    @Override
    public String toString() {
        return "SmsRequest [date = " + date + ", lng = " + lng + ", additional_info = " + additional_info + ", sms_type = " + sms_type + ", awb = " + awb + ", lat = " + lat + "]";

    }
}
