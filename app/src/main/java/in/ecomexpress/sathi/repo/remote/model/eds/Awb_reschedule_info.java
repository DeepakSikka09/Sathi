package in.ecomexpress.sathi.repo.remote.model.eds;

public class Awb_reschedule_info
{
    private String awb_number;

    private boolean reschedule_status;

    public String getAwb_number ()
    {
        return awb_number;
    }

    public void setAwb_number (String awb_number)
    {
        this.awb_number = awb_number;
    }

    public boolean getReschedule_status ()
    {
        return reschedule_status;
    }

    public void setReschedule_status (boolean reschedule_status)
    {
        this.reschedule_status = reschedule_status;
    }

    @Override
    public String toString()
    {
        return "awb_number = "+awb_number+", reschedule_status = "+reschedule_status+"";
    }
}