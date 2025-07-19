package in.ecomexpress.sathi.repo.remote.model.print_receipt;

/**
 * Created by anshika on 11/3/20.
 */

public class Reason_code_list {

    private String reason_code;

    private String reason_id;

    public String getReason_code() {
        return reason_code;
    }

    public void setReason_code(String reason_code) {
        this.reason_code = reason_code;
    }

    public String getReason_id() {
        return reason_id;
    }

    public void setReason_id(String reason_id) {
        this.reason_id = reason_id;
    }

    @Override
    public String toString() {
        return "ClassPojo [reason_code = " + reason_code + ", reason_id = " + reason_id + "]";
    }

}
