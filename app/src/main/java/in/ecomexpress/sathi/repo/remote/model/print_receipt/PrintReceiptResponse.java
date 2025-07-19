package in.ecomexpress.sathi.repo.remote.model.print_receipt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by anshika on 11/3/20.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrintReceiptResponse {

    private Reason_code_list[] reason_code_list;

    private Bank_code_list[] bank_code_list;

    private String description;

    private String origin_id;

    private Bank_receipt_details[] bank_receipt_details;

    private boolean status;

    public Reason_code_list[] getReason_code_list() {
        return reason_code_list;
    }

    public void setReason_code_list(Reason_code_list[] reason_code_list) {
        this.reason_code_list = reason_code_list;
    }

    public Bank_code_list[] getBank_code_list() {
        return bank_code_list;
    }

    public void setBank_code_list(Bank_code_list[] bank_code_list) {
        this.bank_code_list = bank_code_list;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrigin_id() {
        return origin_id;
    }

    public void setOrigin_id(String origin_id) {
        this.origin_id = origin_id;
    }

    public Bank_receipt_details[] getBank_receipt_details() {
        return bank_receipt_details;
    }

    public void setBank_receipt_details(Bank_receipt_details[] bank_receipt_details) {
        this.bank_receipt_details = bank_receipt_details;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ClassPojo [reason_code_list = " + reason_code_list + ", bank_code_list = " + bank_code_list + ", description = " + description + ", origin_id = " + origin_id + ", bank_receipt_details = " + bank_receipt_details + ", status = " + status + "]";
    }


}
