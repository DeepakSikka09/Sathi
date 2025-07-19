package in.ecomexpress.sathi.repo.remote.model.reschedule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import in.ecomexpress.sathi.repo.remote.model.print_receipt.Bank_code_list;
import in.ecomexpress.sathi.repo.remote.model.print_receipt.Bank_receipt_details;
import in.ecomexpress.sathi.repo.remote.model.print_receipt.Reason_code_list;

/**
 * Created by anshika on 11/3/20.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReshceduleDetailsResponse {

    private long awb;
    private long inscan_date;
    private int total_attempts;

    public long getAwb() {
        return awb;
    }

    public void setAwb(long awb) {
        this.awb = awb;
    }

    public long getInscan_date() {
        return inscan_date;
    }

    public void setInscan_date(long inscan_date) {
        this.inscan_date = inscan_date;
    }

    public int getTotal_attempts() {
        return total_attempts;
    }

    public void setTotal_attempts(int total_attempts) {
        this.total_attempts = total_attempts;
    }
}
