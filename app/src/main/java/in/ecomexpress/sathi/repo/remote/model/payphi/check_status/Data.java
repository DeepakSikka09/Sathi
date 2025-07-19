package in.ecomexpress.sathi.repo.remote.model.payphi.check_status;

public class Data {
  /*  private String status;

    private String cust_sup_number;

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public String getCust_sup_number ()
    {
        return cust_sup_number;
    }

    public void setCust_sup_number (String cust_sup_number)
    {
        this.cust_sup_number = cust_sup_number;
    }

    @Override
    public String toString()
    {
        return "Data [status = "+status+", cust_sup_number = "+cust_sup_number+"]";
    }*/


    private String code;

    private String payment_id;

    private String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Data [code = " + code + ", payment_id = " + payment_id + ", description = " + description + "]";
    }
}
