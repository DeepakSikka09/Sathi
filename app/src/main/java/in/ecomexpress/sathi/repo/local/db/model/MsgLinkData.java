package in.ecomexpress.sathi.repo.local.db.model;



import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by anshika on 7/4/20.
 */


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "MsgLinkData")
public class MsgLinkData {

    @PrimaryKey
    @NonNull
    @JsonProperty("awb_number")
    private String awb_number;

    @JsonProperty("was_clicked")
    private boolean was_clicked;

    @JsonProperty("mobile_number_list")
    private ArrayList<String> mobile_number_list;

    public void setMobile_number_list(ArrayList<String> mobile_number_list) {
        this.mobile_number_list = mobile_number_list;
    }

    public ArrayList<String> getMobile_number_list() {
        return mobile_number_list;
    }

    @JsonProperty("awb_number")
    public String getAwb_number() {
        return awb_number;
    }

    @JsonProperty("awb_number")
    public void setAwb_number(String awb_number) {
        this.awb_number = awb_number;
    }

    @JsonProperty("was_clicked")
    public void setWas_clicked(boolean was_clicked) {
        this.was_clicked = was_clicked;
    }

    @JsonProperty("was_clicked")
    public boolean getWas_clicked() {
        return was_clicked;
    }


}
