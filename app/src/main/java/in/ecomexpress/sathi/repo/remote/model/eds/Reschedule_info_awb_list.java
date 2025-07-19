package in.ecomexpress.sathi.repo.remote.model.eds;

public class Reschedule_info_awb_list
{
    private String awb_number;

    private String shipper_id;

    public String getAwb_number ()
    {
        return awb_number;
    }

    public void setAwb_number (String awb_number)
    {
        this.awb_number = awb_number;
    }

    public String getShipper_id ()
    {
        return shipper_id;
    }

    public void setShipper_id (String shipper_id)
    {
        this.shipper_id = shipper_id;
    }

    @Override
    public String toString()
    {
        return "awb_number = "+awb_number+", shipper_id = "+shipper_id+"";
    }
}