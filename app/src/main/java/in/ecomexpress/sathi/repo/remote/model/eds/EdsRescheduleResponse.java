package in.ecomexpress.sathi.repo.remote.model.eds;

import java.util.ArrayList;

public class EdsRescheduleResponse
{
    private String code;

    private String description;

    private ArrayList<Awb_reschedule_info> awb_reschedule_info;

    private String status;

    public String getCode ()
    {
        return code;
    }

    public void setCode (String code)
    {
        this.code = code;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public ArrayList<Awb_reschedule_info> getAwb_reschedule_info ()
    {
        return awb_reschedule_info;
    }

    public void setAwb_reschedule_info (ArrayList<Awb_reschedule_info> awb_reschedule_info)
    {
        this.awb_reschedule_info = awb_reschedule_info;
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
        return "ClassPojo [code = "+code+", description = "+description+", awb_reschedule_info = "+awb_reschedule_info+", status = "+status+"]";
    }
}