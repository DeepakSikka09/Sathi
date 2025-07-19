package in.ecomexpress.sathi.repo.remote.model.fuel.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Shipment_details {
    @JsonProperty("unattempted")
    private String unattempted;
    @JsonProperty("undelivered")

    private String undelivered;
    @JsonProperty("success")
    private String success;

    @JsonProperty("unattempted")
    public String getUnattempted() {
        return unattempted;
    }

    @JsonProperty("unattempted")

    public void setUnattempted(String unattempted) {
        this.unattempted = unattempted;
    }

    @JsonProperty("undelivered")
    public String getUndelivered() {
        return undelivered;
    }

    @JsonProperty("undelivered")
    public void setUndelivered(String undelivered) {
        this.undelivered = undelivered;
    }

    @JsonProperty("success")
    public String getSuccess() {
        return success;
    }

    @JsonProperty("success")
    public void setSuccess(String success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "Shipment_details [unattempted = " + unattempted + ", undelivered = " + undelivered + ", success = " + success + "]";
    }
}

