package in.ecomexpress.sathi.repo.remote.model.dp_daily_earned;

public class Daily_earning_list
{
    private String total_actual_earning;

    private String total_allocated_shipment_count;

    private String total_potential_earning;

    private String total_delivered_shipment_count;

    private String total_mg_earning;

    private String earning_date;

    private Shipment_delivery_info[] shipment_delivery_info;

    public String getTotal_actual_earning ()
    {
        return total_actual_earning;
    }

    public void setTotal_actual_earning (String total_actual_earning)
    {
        this.total_actual_earning = total_actual_earning;
    }

    public String getTotal_allocated_shipment_count ()
    {
        return total_allocated_shipment_count;
    }

    public void setTotal_allocated_shipment_count (String total_allocated_shipment_count)
    {
        this.total_allocated_shipment_count = total_allocated_shipment_count;
    }

    public String getTotal_potential_earning ()
    {
        return total_potential_earning;
    }

    public void setTotal_potential_earning (String total_potential_earning)
    {
        this.total_potential_earning = total_potential_earning;
    }

    public String getTotal_delivered_shipment_count ()
    {
        return total_delivered_shipment_count;
    }

    public void setTotal_delivered_shipment_count (String total_delivered_shipment_count)
    {
        this.total_delivered_shipment_count = total_delivered_shipment_count;
    }

    public String getEarning_date ()
    {
        return earning_date;
    }

    public void setEarning_date (String earning_date)
    {
        this.earning_date = earning_date;
    }

    public Shipment_delivery_info[] getShipment_delivery_info ()
    {
        return shipment_delivery_info;
    }

    public void setShipment_delivery_info (Shipment_delivery_info[] shipment_delivery_info)
    {
        this.shipment_delivery_info = shipment_delivery_info;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [total_actual_earning = "+total_actual_earning+", total_allocated_shipment_count = "+total_allocated_shipment_count+", total_potential_earning = "+total_potential_earning+", total_delivered_shipment_count = "+total_delivered_shipment_count+", earning_date = "+earning_date+", shipment_delivery_info = "+shipment_delivery_info+"]";
    }

    public String getTotal_mg_earning() {
        return total_mg_earning;
    }

    public void setTotal_mg_earning(String total_mg_earning) {
        this.total_mg_earning = total_mg_earning;
    }
}