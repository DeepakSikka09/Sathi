package in.ecomexpress.sathi.repo.remote.model.print_receipt;

/**
 * Created by anshika on 11/3/20.
 */

public class PrintReceiptRequest {
    private String emp_code;

    private String location_code;

    public String getEmp_code ()
    {
        return emp_code;
    }

    public void setEmp_code (String emp_code)
    {
        this.emp_code = emp_code;
    }

    public String getLocation_code ()
    {
        return location_code;
    }

    public void setLocation_code (String location_code)
    {
        this.location_code = location_code;
    }
}
