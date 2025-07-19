package in.ecomexpress.sathi.ui.dashboard.unattempted_shipments;

import java.util.List;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.ShipmentsDetail;

public class UnattemptedShipments {

    public UnattemptedShipments(String awbNo, String shipmentType, long assigned_date) {
        setAwbNo(awbNo);
        setShipmentType(shipmentType);
        setAssigned_date(assigned_date);

    }
    public UnattemptedShipments(String awbNo,String composite_key, String shipmentType,long assigned_date) {
        setAwbNo(awbNo);
        setShipmentType(shipmentType);
        setComposite_key(composite_key);
        setAssigned_date(assigned_date);
    }
    public UnattemptedShipments(String awbNo,String composite_key, String shipmentType, List<ShipmentsDetail> shipmentsDetails, long assigned_date) {
        setAwbNo(awbNo);
        setShipmentType(shipmentType);
        setComposite_key(composite_key);
        setShipmentsDetails(shipmentsDetails);
        setAssigned_date(assigned_date);
    }
    public String getComposite_key() {
        return composite_key;
    }

    public void setComposite_key(String composite_key) {
        this.composite_key = composite_key;
    }

    private String composite_key;
    private long assigned_date;
    List<ShipmentsDetail> shipmentsDetails;
    private String awbNo;
    private String shipmentType;

    public String getAwbNo() {
        return awbNo;
    }

    public void setAwbNo(String awbNo) {
        this.awbNo = awbNo;
    }

    public String getShipmentType() {
        return shipmentType;
    }

    public void setShipmentType(String shipmentType) {
        this.shipmentType = shipmentType;
    }

    public List<ShipmentsDetail> getShipmentsDetails(){
        return shipmentsDetails;
    }

    public void setShipmentsDetails(List<ShipmentsDetail> shipmentsDetails){
        this.shipmentsDetails = shipmentsDetails;
    }

    public long getAssigned_date(){
        return assigned_date;
    }

    public void setAssigned_date(long assigned_date){
        this.assigned_date = assigned_date;
    }
}