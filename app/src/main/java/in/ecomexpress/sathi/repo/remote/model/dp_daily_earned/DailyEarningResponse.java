package in.ecomexpress.sathi.repo.remote.model.dp_daily_earned;

public class DailyEarningResponse
{
    private String emp_code;

    private String code;

    private String month;

    private String[] payout_date_list;

    private String description;

    private Daily_earning_list[] daily_earning_list;

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

    public String getMonth ()
    {
        return month;
    }

    public void setMonth (String month)
    {
        this.month = month;
    }

    public String[] getPayout_date_list ()
    {
        return payout_date_list;
    }

    public void setPayout_date_list (String[] payout_date_list)
    {
        this.payout_date_list = payout_date_list;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public Daily_earning_list[] getDaily_earning_list ()
    {
        return daily_earning_list;
    }

    public void setDaily_earning_list (Daily_earning_list[] daily_earning_list)
    {
        this.daily_earning_list = daily_earning_list;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [emp_code = "+emp_code+", code = "+code+", month = "+month+", payout_date_list = "+payout_date_list+", description = "+description+", daily_earning_list = "+daily_earning_list+", status = "+status+"]";
    }

    public boolean getStatus()
    {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}