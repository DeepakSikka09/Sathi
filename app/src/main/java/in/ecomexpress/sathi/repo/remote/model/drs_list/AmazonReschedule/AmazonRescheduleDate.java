package in.ecomexpress.sathi.repo.remote.model.drs_list.AmazonReschedule;

public class AmazonRescheduleDate
{
    private String code;

    private String description;

    private Amazon_reschedule_list[] amazon_reschedule_list;

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

    public Amazon_reschedule_list[] getAmazon_reschedule_list ()
    {
        return amazon_reschedule_list;
    }

    public void setAmazon_reschedule_list (Amazon_reschedule_list[] amazon_reschedule_list)
    {
        this.amazon_reschedule_list = amazon_reschedule_list;
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
        return "ClassPojo [code = "+code+", description = "+description+", amazon_reschedule_list = "+amazon_reschedule_list+", status = "+status+"]";
    }
}