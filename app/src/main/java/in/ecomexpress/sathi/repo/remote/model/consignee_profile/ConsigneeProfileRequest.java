package in.ecomexpress.sathi.repo.remote.model.consignee_profile;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shivangis on 5/17/2019.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)

public class ConsigneeProfileRequest {

//    @JsonProperty("awbs")
//    public List<Consignee_profile> getAwb_numbers() {
//        return awb_numbers;
//    }
//    @JsonProperty("awbs")
////    public void setAwb_numbers(List<Long> awb_numbers) {
////        this.awb_numbers = awb_numbers;
////    }
//    public void setAwb_numbers(List<Consignee_profile> awb_numbers) {
//        this.awb_numbers = awb_numbers;
//    }
//
//    @JsonProperty("awbs")
//    private List<Consignee_profile> awb_numbers;
//
//    @Override
//    public String toString() {
//        return "ConsigneeProfileRequest [awb_numbers = " + awb_numbers + "]";
//    }
private ArrayList<Consignee_profile> consignee_profile;

    @JsonProperty("consignee_profile")
    public ArrayList<Consignee_profile> getConsignee_profile ()
    {
        return consignee_profile;
    }
    @JsonProperty("consignee_profile")
    public void setConsignee_profile (ArrayList<Consignee_profile> consignee_profile)
    {
        this.consignee_profile = consignee_profile;
    }


}
