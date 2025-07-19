package in.ecomexpress.sathi.repo.remote.model.adm;

public class Available_slots
{
    private String planned_status_code;

    private String slot_hour;

    private String status;

    public String getPlanned_status_code ()
    {
        return planned_status_code;
    }

    public void setPlanned_status_code (String planned_status_code)
    {
        this.planned_status_code = planned_status_code;
    }

    public String getSlot_hour ()
    {
        return slot_hour;
    }

    public void setSlot_hour (String slot_hour)
    {
        this.slot_hour = slot_hour;
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
        return "ClassPojo [planned_status_code = "+planned_status_code+", slot_hour = "+slot_hour+", status = "+status+"]";
    }
}