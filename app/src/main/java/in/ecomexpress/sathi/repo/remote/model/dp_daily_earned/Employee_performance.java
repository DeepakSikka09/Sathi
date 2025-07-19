package in.ecomexpress.sathi.repo.remote.model.dp_daily_earned;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee_performance {

    private String total_actual_earning;

    private String total_allocated_shipment_count;

    private String total_mg_earning;

    private String arrears_days_count;

    private String total_potential_earning;

    private String arrears_penalty;

    private String total_delivered_shipment_count;

    private String absent_count;

    private String absent_penalty;

    private String incentive;
    private String bonus;
    private String tds;
    private String trxn_tds;
    private String trxn_other_deduction;
    private String lpc_penalty;

    private Shipment_delivery_info[] shipment_delivery_info;

    private String net_earning;

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

    public String getTotal_mg_earning ()
    {
        return total_mg_earning;
    }

    public void setTotal_mg_earning (String total_mg_earning)
    {
        this.total_mg_earning = total_mg_earning;
    }

    public String getArrears_days_count ()
    {
        return arrears_days_count;
    }

    public void setArrears_days_count (String arrears_days_count)
    {
        this.arrears_days_count = arrears_days_count;
    }

    public String getTotal_potential_earning ()
    {
        return total_potential_earning;
    }

    public void setTotal_potential_earning (String total_potential_earning)
    {
        this.total_potential_earning = total_potential_earning;
    }

    public String getArrears_penalty ()
    {
        return arrears_penalty;
    }

    public void setArrears_penalty (String arrears_penalty)
    {
        this.arrears_penalty = arrears_penalty;
    }

    public String getTotal_delivered_shipment_count ()
    {
        return total_delivered_shipment_count;
    }

    public void setTotal_delivered_shipment_count (String total_delivered_shipment_count)
    {
        this.total_delivered_shipment_count = total_delivered_shipment_count;
    }

    public String getAbsent_count ()
    {
        return absent_count;
    }

    public void setAbsent_count (String absent_count)
    {
        this.absent_count = absent_count;
    }

    public String getAbsent_penalty ()
    {
        return absent_penalty;
    }

    public void setAbsent_penalty (String absent_penalty)
    {
        this.absent_penalty = absent_penalty;
    }

    public Shipment_delivery_info[] getShipment_delivery_info ()
    {
        return shipment_delivery_info;
    }

    public void setShipment_delivery_info (Shipment_delivery_info[] shipment_delivery_info)
    {
        this.shipment_delivery_info = shipment_delivery_info;
    }

    public String getNet_earning ()
    {
        return net_earning;
    }

    public void setNet_earning (String net_earning)
    {
        this.net_earning = net_earning;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [total_actual_earning = "+total_actual_earning+", total_allocated_shipment_count = "+total_allocated_shipment_count+", total_mg_earning = "+total_mg_earning+", arrears_days_count = "+arrears_days_count+", total_potential_earning = "+total_potential_earning+", arrears_penalty = "+arrears_penalty+", total_delivered_shipment_count = "+total_delivered_shipment_count+", absent_count = "+absent_count+", absent_penalty = "+absent_penalty+", shipment_delivery_info = "+shipment_delivery_info+", net_earning = "+net_earning+"]";
    }

    public String getIncentive() {
        return incentive;
    }

    public void setIncentive(String incentive) {
        this.incentive = incentive;
    }

    public String getBonus() {
        return bonus;
    }

    public void setBonus(String bonus) {
        this.bonus = bonus;
    }

    public String getTds() {
        return tds;
    }

    public void setTds(String tds) {
        this.tds = tds;
    }

    public String getTrxn_tds() {
        return trxn_tds;
    }

    public void setTrxn_tds(String trxn_tds) {
        this.trxn_tds = trxn_tds;
    }

    public String getTrxn_other_deduction() {
        return trxn_other_deduction;
    }

    public void setTrxn_other_deduction(String trxn_other_deduction) {
        this.trxn_other_deduction = trxn_other_deduction;
    }

    public String getLpc_penalty() {
        return lpc_penalty;
    }
}
