package in.ecomexpress.sathi.repo.remote.model.print_receipt;

/**
 * Created by anshika on 11/3/20.
 */

public class Bank_receipt_details {
    private String total_amount;

    private int codd_id;

    private String codd_code;

    private String submit_status;


    private double deposit_amount;

    private long codd_from_date;

    private long codd_to_date;

    private String slip_number;

    private String bank_code;

    private String reasonCode;

    private String remark;

    private long deposit_date;


    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public int getCodd_id() {
        return codd_id;
    }

    public void setCodd_id(int codd_id) {
        this.codd_id = codd_id;
    }

    public String getSubmit_status() {
        return submit_status;
    }

    public void setSubmit_status(String submit_status) {
        this.submit_status = submit_status;
    }

    @Override
    public String toString() {
        return "ClassPojo [total_amount = " + total_amount + ", codd_id = " + codd_id + ", submit_status = " + submit_status + "]";
    }

    public String getCodd_code() {
        return codd_code;
    }

    public void setCodd_code(String codd_code) {
        this.codd_code = codd_code;
    }

    public double getDeposit_amount() {
        return deposit_amount;
    }

    public void setDeposit_amount(double deposit_amount) {
        this.deposit_amount = deposit_amount;
    }

    public long getCodd_from_date() {
        return codd_from_date;
    }

    public void setCodd_from_date(long codd_from_date) {
        this.codd_from_date = codd_from_date;
    }

    public long getCodd_to_date() {
        return codd_to_date;
    }

    public void setCodd_to_date(long codd_to_date) {
        this.codd_to_date = codd_to_date;
    }

    public String getSlip_number() {
        return slip_number;
    }

    public void setSlip_number(String slip_number) {
        this.slip_number = slip_number;
    }

    public String getBank_code() {
        return bank_code;
    }

    public void setBank_code(String bank_code) {
        this.bank_code = bank_code;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getDeposit_date() {
        return deposit_date;
    }

    public void setDeposit_date(long deposit_date) {
        this.deposit_date = deposit_date;
    }
}
