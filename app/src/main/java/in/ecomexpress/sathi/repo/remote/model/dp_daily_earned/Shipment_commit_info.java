package in.ecomexpress.sathi.repo.remote.model.dp_daily_earned;

public class Shipment_commit_info
{
    private String allocated_shipment_count;

    private String potential_earning;

    private String actual_earning;

    private String delivered_shipment_count;

    private String mg_earning;

    public String getAllocated_shipment_count ()
    {
        return allocated_shipment_count;
    }

    public void setAllocated_shipment_count (String allocated_shipment_count)
    {
        this.allocated_shipment_count = allocated_shipment_count;
    }

    public String getPotential_earning ()
    {
        return potential_earning;
    }

    public void setPotential_earning (String potential_earning)
    {
        this.potential_earning = potential_earning;
    }

    public String getActual_earning ()
    {
        return actual_earning;
    }

    public void setActual_earning (String actual_earning)
    {
        this.actual_earning = actual_earning;
    }

    public String getDelivered_shipment_count ()
    {
        return delivered_shipment_count;
    }

    public void setDelivered_shipment_count (String delivered_shipment_count)
    {
        this.delivered_shipment_count = delivered_shipment_count;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [allocated_shipment_count = "+allocated_shipment_count+", potential_earning = "+potential_earning+", actual_earning = "+actual_earning+", delivered_shipment_count = "+delivered_shipment_count+"]";
    }

    public String getMg_earning() {
        return mg_earning;
    }

    public void setMg_earning(String mg_earning) {
        this.mg_earning = mg_earning;
    }
}