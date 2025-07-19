package in.ecomexpress.sathi.repo.remote.model.dp_daily_earned;

public class PayoutResponse
{
    private String emp_code;

    private String code;

    private String payout_date;

    private Employee_performance employee_performance;

    private String description;

    private boolean status;

    public String getEmp_code ()
    {
        return emp_code;
    }

    public void setEmp_code (String emp_code)
    {
        this.emp_code = emp_code;
    }

    public String getCode ()
    {
        return code;
    }

    public void setCode (String code)
    {
        this.code = code;
    }

    public String getPayout_date ()
    {
        return payout_date;
    }

    public void setPayout_date (String payout_date)
    {
        this.payout_date = payout_date;
    }

    public Employee_performance getEmployee_performance ()
    {
        return employee_performance;
    }

    public void setEmployee_performance (Employee_performance employee_performance)
    {
        this.employee_performance = employee_performance;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }



    @Override
    public String toString()
    {
        return "ClassPojo [emp_code = "+emp_code+", code = "+code+", payout_date = "+payout_date+", employee_performance = "+employee_performance+", description = "+description+", status = "+status+"]";
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean getStatus()
    {
        return status;
    }
}