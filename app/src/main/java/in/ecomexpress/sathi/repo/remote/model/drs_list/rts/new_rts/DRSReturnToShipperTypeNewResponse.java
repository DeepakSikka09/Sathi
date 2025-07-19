package in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts;

import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.drs_list.common.CInterface;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DRSReturnToShipperTypeNewResponse implements CInterface{
    @PrimaryKey
    private int id;

    @JsonProperty("vendor_response")

    private List<VendorResponse> vendorResponse = null;

    @JsonProperty("warehouse_response")
    private List<WarehouseResponse> warehouseResponse = null;

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @JsonProperty("vendor_response")
    public List<VendorResponse> getVendorResponse() {
        return vendorResponse;
    }

    @JsonProperty("vendor_response")
    public void setVendorResponse(List<VendorResponse> vendorResponse) {
        this.vendorResponse = vendorResponse;
        list.addAll(vendorResponse);
    }

    @JsonProperty("warehouse_response")
    public List<WarehouseResponse> getWarehouseResponse() {
        return warehouseResponse;
    }

    @JsonProperty("warehouse_response")
    public void setWarehouseResponse(List<WarehouseResponse> warehouseResponse) {
        this.warehouseResponse = warehouseResponse;
        list.addAll(warehouseResponse);
    }

    List<IRTSBaseInterface> list = new ArrayList<>();

    public List<IRTSBaseInterface> getCombinedList(){
        return list;
    }

    public void addInCominedList(IRTSBaseInterface irtsBaseInterface){
        list.add(irtsBaseInterface);
    }
    public void addAllInCombinedList(List<IRTSBaseInterface> listIRTSBaseInterface){
        list.addAll(listIRTSBaseInterface);
    }

    private double distance;

    @Override
    public void setDistance(double distance) {
        this.distance=distance;
    }

    @Override
    public double getDistance() {
        return this.distance;
    }
}
