package in.ecomexpress.sathi.repo.local.data.eds;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EDSResponseCommit {
    @JsonProperty("errors")
    private ArrayList<String> errors;

    @JsonProperty("code")
    private String code;

    @JsonProperty("awb_no")
    private String awb_no;

    @JsonProperty("drs_no")
    private String drs_no;

    @JsonProperty("shipment_status")
    private String shipment_status;
    @JsonProperty("drs_no")
    public String getDrs_no() {
        return drs_no;
    }
    @JsonProperty("drs_no")
    public void setDrs_no(String drs_no) {
        this.drs_no = drs_no;
    }
    @JsonProperty("shipment_status")
    public String getShipment_status() {
        return shipment_status;
    }
    @JsonProperty("shipment_status")
    public void setShipment_status(String shipment_status) {
        this.shipment_status = shipment_status;
    }

    @JsonProperty("description")
    private String description;

    @JsonProperty("errors")
    public ArrayList<String> getErrors() {
        return errors;
    }

    @JsonProperty("errors")
    public void setErrors(ArrayList<String> errors) {
        this.errors = errors;
    }


    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("awb_no")
    public String getAwb_no() {
        return awb_no;
    }

    @JsonProperty("awb_no")
    public void setAwb_no(String awb_no) {
        this.awb_no = awb_no;
    }

    @Override
    public String toString() {
        return "EDSResponseCommit{\" + description = " + description + ", code = " + code + ", awb_no = " + awb_no + ",errors = " + errors + '\'' +
                '}';
    }
}
