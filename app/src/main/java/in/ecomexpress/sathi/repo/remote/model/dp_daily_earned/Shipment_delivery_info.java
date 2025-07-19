package in.ecomexpress.sathi.repo.remote.model.dp_daily_earned;

public class Shipment_delivery_info
{
    private Shipment_commit_info shipment_commit_info;

    private String shipment_type;

    public Shipment_commit_info getShipment_commit_info ()
    {
        return shipment_commit_info;
    }

    public void setShipment_commit_info (Shipment_commit_info shipment_commit_info)
    {
        this.shipment_commit_info = shipment_commit_info;
    }

    public String getShipment_type ()
    {
        return shipment_type;
    }

    public void setShipment_type (String shipment_type)
    {
        this.shipment_type = shipment_type;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [shipment_commit_info = "+shipment_commit_info+", shipment_type = "+shipment_type+"]";
    }
}