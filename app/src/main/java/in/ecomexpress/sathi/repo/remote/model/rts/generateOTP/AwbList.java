package in.ecomexpress.sathi.repo.remote.model.rts.generateOTP;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AwbList{
    private ArrayList<AllOtpAWB> undelivered_awb = new ArrayList<>();
    private ArrayList<AllOtpAWB> delivered_awb = new ArrayList<>();
    private ArrayList<AllOtpAWB> disputed_awb = new ArrayList<>();

    public ArrayList<AllOtpAWB> getUndelivered_awb(){
        return undelivered_awb;
    }

    public void setUndelivered_awb(ArrayList<AllOtpAWB> undelivered_awb){
        this.undelivered_awb = undelivered_awb;
    }

    public ArrayList<AllOtpAWB> getDelivered_awb(){
        return delivered_awb;
    }

    public void setDelivered_awb(ArrayList<AllOtpAWB> delivered_awb){
        this.delivered_awb = delivered_awb;
    }

    public ArrayList<AllOtpAWB> getDisputed_awb(){
        return disputed_awb;
    }

    public void setDisputed_awb(ArrayList<AllOtpAWB> disputed_awb){
        this.disputed_awb = disputed_awb;
    }
}