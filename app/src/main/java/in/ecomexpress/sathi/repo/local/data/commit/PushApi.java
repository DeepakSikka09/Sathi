package in.ecomexpress.sathi.repo.local.data.commit;



import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class PushApi {

    @PrimaryKey
    private long awbNo;
    @JsonIgnore
    public String ShipmentDeliveryStatus = "";

    @JsonIgnore
    public String CompositeKey;

    @JsonIgnore
    public String vendor_name = "";

    private int shipmentStatus;
    private boolean rvp_mps;
    private String shipmentCaterogy;
    private String requestId;
    private String appId;
    private String apiVer;
    private String via;
    private String empId;
    private String authtoken;
    private String requestType;
    private String requestData;
    private String commitUrl;

    public long getAwbNo() {
        return awbNo;
    }

    public void setAwbNo(long awbNo) {
        this.awbNo = awbNo;
    }

    public int getShipmentStatus() {
        return shipmentStatus;
    }

    public void setShipmentStatus(int shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }

    public String getShipmentCaterogy() {
        return shipmentCaterogy;
    }

    public void setShipmentCaterogy(String shipmentCaterogy) {
        this.shipmentCaterogy = shipmentCaterogy;
    }

    public String getShipmentDeliveryStatus() {
        return ShipmentDeliveryStatus;
    }
    public void setShipmentDeliveryStatus(String shipmentDeliveryStatus) {
        ShipmentDeliveryStatus = shipmentDeliveryStatus;
    }
    public String getCompositeKey() {
        return CompositeKey;
    }

    public void setCompositeKey(String compositeKey) {
        CompositeKey = compositeKey;
    }


    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getApiVer() {
        return apiVer;
    }

    public void setApiVer(String apiVer) {
        this.apiVer = apiVer;
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestData() {
        return requestData;
    }

    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }

    public String getCommitUrl() {
        return commitUrl;
    }

    public void setCommitUrl(String commitUrl) {
        this.commitUrl = commitUrl;
    }


    @Override
    public String toString() {
        return "PushApi{" +
                "awbNo=" + awbNo +
                ", ShipmentDeliveryStatus='" + ShipmentDeliveryStatus + '\'' +
                ", CompositeKey=" + CompositeKey +
                ", shipmentStatus=" + shipmentStatus +
                ", shipmentCaterogy='" + shipmentCaterogy + '\'' +
                ", requestId='" + requestId + '\'' +
                ", appId='" + appId + '\'' +
                ", apiVer='" + apiVer + '\'' +
                ", via='" + via + '\'' +
                ", empId='" + empId + '\'' +
                ", authtoken='" + authtoken + '\'' +
                ", requestType='" + requestType + '\'' +
                ", requestData='" + requestData + '\'' +
                ", commitUrl='" + commitUrl + '\'' +
                '}';
    }

    public boolean isRvp_mps() {
        return rvp_mps;
    }

    public void setRvp_mps(boolean rvp_mps) {
        this.rvp_mps = rvp_mps;
    }
}
