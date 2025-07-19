package in.ecomexpress.sathi.repo.remote.model.drs_list.AmazonReschedule;

public class Amazon_reschedule_list
{
    private Reschedule_info reschedule_info;

    private String code;

    private String awb_number;

    private String description;

    private String status;

    public Reschedule_info getReschedule_info ()
    {
        return reschedule_info;
    }

    public void setReschedule_info (Reschedule_info reschedule_info)
    {
        this.reschedule_info = reschedule_info;
    }

    public String getCode ()
    {
        return code;
    }

    public void setCode (String code)
    {
        this.code = code;
    }

    public String getAwb_number ()
    {
        return awb_number;
    }

    public void setAwb_number (String awb_number)
    {
        this.awb_number = awb_number;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [reschedule_info = "+reschedule_info+", code = "+code+", awb_number = "+awb_number+", description = "+description+", status = "+status+"]";
    }
}