package in.ecomexpress.sathi.repo.local.db.model;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by anshika on 20/12/19.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "RescheduleEdsD")
public class RescheduleEdsD {
    @PrimaryKey
    @NonNull
    @JsonProperty("awb_number")
    private String awb_number;

    @JsonProperty("reschedule_status")
    private boolean reschedule_status;

    @JsonProperty("awb_number")
    public String getAwb_number ()
    {
        return awb_number;
    }

    @JsonProperty("awb_number")
    public void setAwb_number (String awb_number)
    {
        this.awb_number = awb_number;
    }

    @JsonProperty("reschedule_status")
    public boolean getReschedule_status ()
    {
        return reschedule_status;
    }

    @JsonProperty("reschedule_status")

    public void setReschedule_status (boolean shipper_id)
    {
        this.reschedule_status = shipper_id;
    }



}

