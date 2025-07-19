package in.ecomexpress.sathi.repo.remote.model.drs_list.rts;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RtsShipmentDetails {
    @JsonProperty("shipper_code")
    private String shipperCode;

    @JsonProperty("pin")
    public Long getPin(){
        return pin;
    }

    @JsonProperty("pin")
    public void setPin(Long pin){
        this.pin = pin;
    }

    @JsonProperty("pin")
    private Long pin;
    @JsonProperty("parent_awb_no")
    private String parentAwbNo;
    @JsonProperty("status")
    private String status;
    @JsonProperty("order_no")
    private String orderNo;

    @JsonProperty("ofd_otp")
    private String ofd_otp;

    @JsonProperty("ofd_otp")
    public String getOfd_otp(){
        return ofd_otp;
    }

    @JsonProperty("ofd_otp")
    public void setOfd_otp(String ofd_otp){
        this.ofd_otp = ofd_otp;
    }

    @JsonProperty("shipper_code")
    public String getShipperCode(){
        return shipperCode;
    }

    @JsonProperty("shipper_code")
    public void setShipperCode(String shipperCode){
        this.shipperCode = shipperCode;
    }

    @JsonProperty("parent_awb_no")
    public String getParentAwbNo(){
        return parentAwbNo;
    }

    @JsonProperty("parent_awb_no")
    public void setParentAwbNo(String parentAwbNo){
        this.parentAwbNo = parentAwbNo;
    }

    @JsonProperty("status")
    public String getStatus(){
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status){
        this.status = status;
    }

    @JsonProperty("order_no")
    public String getOrderNo(){
        return orderNo;
    }

    @JsonProperty("order_no")
    public void setOrderNo(String orderNo){
        this.orderNo = orderNo;
    }

    @NonNull
    @Override
    public String toString(){
        return "ShipmentDetails [shipperCode=" + shipperCode + ", parentAwbNo=" + parentAwbNo + ", status=" + status + ", ofdotp=" + ofd_otp+ ", orderNo=" + orderNo + "]";
    }
}
