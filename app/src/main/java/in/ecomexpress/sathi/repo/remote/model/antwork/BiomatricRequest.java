package in.ecomexpress.sathi.repo.remote.model.antwork;

public class BiomatricRequest {

    private String order_number;
    private String FingerType;
    private String DeviceType;
    private String DeviceDataXml;


    public String getOrder_number(){
        return order_number;
    }

    public void setOrder_number(String order_number){
        this.order_number = order_number;
    }

    public String getFingerType(){
        return FingerType;
    }

    public void setFingerType(String fingerType){
        FingerType = fingerType;
    }

    public String getDeviceType(){
        return DeviceType;
    }

    public void setDeviceType(String deviceType){
        DeviceType = deviceType;
    }

    public String getDeviceDataXml(){
        return DeviceDataXml;
    }

    public void setDeviceDataXml(String DeviceDataXml){
        this.DeviceDataXml = DeviceDataXml;
    }
}
