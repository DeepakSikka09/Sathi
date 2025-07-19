package in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import in.ecomexpress.sathi.utils.Constants;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "rts_shipment_details")
public class ShipmentsDetail implements Comparable<ShipmentsDetail> , Serializable {
    @ColumnInfo(name = "detail_id")
    private long detail_id;

    public boolean isChecked() {
        return checked;
    }
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @ColumnInfo(name = "checked")
    private boolean checked = false;

    @JsonProperty("shipper_code")
    private String shipperCode;

    @JsonProperty("reason_id")
    private int reason_id;

    @JsonProperty("shipment_type")
    private String shipmentType;

    @JsonProperty("shipper_name")
    private String shipperName;

    @JsonProperty("parent_awb_no")
    private String parentAwbNo ;

    @JsonProperty("order_no")
    private String orderNo;

    @JsonProperty("is_flyer_scanned")
    private boolean is_flyer_scanned;

    @JsonProperty("is_flyer_image_captured")
    private boolean is_flyer_image_captured;

    @JsonProperty("flyer_code_manually_input")
    private String flyer_code_manually_input;

    @JsonProperty("return_package_barcode")
    private String return_package_barcode;

    @JsonProperty("flag_map")
    @Embedded
    private FlagsMap flagsMap;

    @JsonProperty("entered_return_package_barcode")
    private String entered_return_package_barcode;

    @JsonProperty("drs_no")
    private String drsNo;

    @JsonProperty("IMAGEM")
    private boolean IMAGEM;

    @JsonProperty("IS_IMAGE_CAPTURED")
    private boolean IS_IMAGE_CAPTURED;

    @JsonProperty("IS_FLYER_WRONG_CAPTURED")
    private boolean IS_FLYER_WRONG_CAPTURED;

    private String dataTime;

    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }

    @JsonProperty("awb_no")
    @PrimaryKey
    private Long awbNo;

    private String composite_key;

    private int reasonCode;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        this.Status = status;
    }

    private String Status = Constants.RTSASSIGNED;

    public void setAwbNo(Long awbNo) {
        this.awbNo = awbNo;
    }

    public Long getAwbNo() {
        return this.awbNo;
    }

    @JsonProperty("shipper_code")
    public String getShipperCode() {
        return shipperCode;
    }

    @JsonProperty("shipper_code")
    public void setShipperCode(String shipperCode) {
        this.shipperCode = shipperCode;
    }

    public String getShipmentType() {
        return shipmentType;
    }

    public void setShipmentType(String shipmentType) {
        this.shipmentType = shipmentType;
    }

    public String getShipperName() {
        return shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }

    public String getParentAwbNo() {
        return parentAwbNo;
    }

    public void setParentAwbNo(String parentAwbNo) {
        this.parentAwbNo = parentAwbNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getDrsNo() {
        return drsNo;
    }

    public void setDrsNo(String drsNo) {
        this.drsNo = drsNo;
    }

    public long getDetail_id() {
        return detail_id;
    }

    public void setDetail_id(long detail_id) {
        this.detail_id = detail_id;
    }

    public int getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(int reasonCode) {
        this.reasonCode = reasonCode;
    }


    @Override
    public int compareTo(@NonNull ShipmentsDetail shipmentsDetail) {
        return this.awbNo < shipmentsDetail.getAwbNo() ? 1 : 0;
    }

    @NonNull
    @Override
    public String toString() {
        return "ShipmentsDetail{" +
                "detail_id=" + detail_id +
                ", checked=" + checked +
                ", shipperCode='" + shipperCode + '\'' +
                ", shipmentType='" + shipmentType + '\'' +
                ", shipperName='" + shipperName + '\'' +
                ", parentAwbNo='" + parentAwbNo + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", drsNo='" + drsNo + '\'' +
                ", dataTime='" + dataTime + '\'' +
                ", awbNo=" + awbNo +
                ", reasonCode=" + reasonCode +
                ", Status='" + Status + '\'' +
                '}';
    }

    public int getReason_id(){
        return reason_id;
    }

    public void setReason_id(int reason_id){
        this.reason_id = reason_id;
    }

    public boolean getIMAGEM(){
        return IMAGEM;
    }

    public void setIMAGEM(boolean IMAGEM){
        this.IMAGEM = IMAGEM;
    }

    public boolean isIS_IMAGE_CAPTURED(){
        return IS_IMAGE_CAPTURED;
    }

    public void setIS_IMAGE_CAPTURED(boolean IS_IMAGE_CAPTURED){
        this.IS_IMAGE_CAPTURED = IS_IMAGE_CAPTURED;
    }

    public String getComposite_key(){
        return composite_key;
    }

    public void setComposite_key(String composite_key){
        this.composite_key = composite_key;
    }

    public boolean isIs_flyer_scanned() {
        return is_flyer_scanned;
    }

    public void setIs_flyer_scanned(boolean is_flyer_scanned) {
        this.is_flyer_scanned = is_flyer_scanned;
    }

    public String getReturn_package_barcode() {
        return return_package_barcode;
    }

    public void setReturn_package_barcode(String return_package_barcode) {
        this.return_package_barcode = return_package_barcode;
    }

    public boolean isIS_FLYER_WRONG_CAPTURED() {
        return IS_FLYER_WRONG_CAPTURED;
    }

    public void setIS_FLYER_WRONG_CAPTURED(boolean IS_FLYER_WRONG_CAPTURED) {
        this.IS_FLYER_WRONG_CAPTURED = IS_FLYER_WRONG_CAPTURED;
    }

    public String getEntered_return_package_barcode() {
        return entered_return_package_barcode;
    }

    public void setEntered_return_package_barcode(String entered_return_package_barcode) {
        this.entered_return_package_barcode = entered_return_package_barcode;
    }

    public String getFlyer_code_manually_input() {
        return flyer_code_manually_input;
    }

    public void setFlyer_code_manually_input(String flyer_code_manually_input) {
        this.flyer_code_manually_input = flyer_code_manually_input;
    }

    public boolean isIs_flyer_image_captured() {
        return is_flyer_image_captured;
    }

    public void setIs_flyer_image_captured(boolean is_flyer_image_captured) {
        this.is_flyer_image_captured = is_flyer_image_captured;
    }

    @JsonProperty("flag_map")
    public FlagsMap getFlagsMap() {
        return flagsMap;
    }

    @JsonProperty("flag_map")
    public void setFlagsMap(FlagsMap flagsMap) {
        this.flagsMap = flagsMap;
    }
}