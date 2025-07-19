package in.ecomexpress.sathi.repo.remote.model.fuel.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Reimbursement_status {
    @JsonProperty("approved")
    private String approved;
    @JsonProperty("status")
    private String status;
    @JsonProperty("claimed")
    private String claimed;

    @JsonProperty("approved")
    public String getApproved() {
        return approved;
    }

    @JsonProperty("approved")
    public void setApproved(String approved) {
        this.approved = approved;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("claimed")

    public String getClaimed() {
        return claimed;
    }

    @JsonProperty("claimed")

    public void setClaimed(String claimed) {
        this.claimed = claimed;
    }

    @Override
    public String toString() {
        return "Reimbursement_status [approved = " + approved + ", status = " + status + ", claimed = " + claimed + "]";
    }
}
