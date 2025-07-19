package in.ecomexpress.sathi.repo.remote.model.drs_list;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.inject.Inject;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(indices = {@Index(value = {"drs_comp_key"}, unique = true)})
public class DRSSequence implements Parcelable {

    @JsonIgnore
    @ColumnInfo(name = "drs_comp_key")
    public String compositeKey;

    @JsonProperty("awb_no")
    @ColumnInfo(name = "awb_no")
    private Long awbNo;

    @JsonProperty("sequence_no")
    @PrimaryKey
    @ColumnInfo(name="sequence_no")
    private Long sequence_no;

    @JsonIgnore
    private int shipmentSyncStatus = 0;

    @JsonProperty("shipment_status")
    private int shipmentStatus = 0;

    public Long getSequence_no(){
        return sequence_no;
    }

    public void setSequence_no(Long sequence_no){
        this.sequence_no = sequence_no;
    }

    public String getCompositeKey(){
        return compositeKey;
    }

    public void setCompositeKey(String compositeKey){
        this.compositeKey = compositeKey;
    }

    public Long getAwbNo(){
        return awbNo;
    }

    public void setAwbNo(Long awbNo){
        this.awbNo = awbNo;
    }

    public int getShipmentSyncStatus(){
        return shipmentSyncStatus;
    }

    public void setShipmentSyncStatus(int shipmentSyncStatus){
        this.shipmentSyncStatus = shipmentSyncStatus;
    }

    public int getShipmentStatus(){
        return shipmentStatus;
    }

    public void setShipmentStatus(int shipmentStatus){
        this.shipmentStatus = shipmentStatus;
    }


    public DRSSequence(String compositeKey, Long awbNo, Long sequence_no, int shipmentSyncStatus, int shipmentStatus){
        this.compositeKey = compositeKey;
        this.awbNo = awbNo;
        this.sequence_no = sequence_no;
        this.shipmentSyncStatus = shipmentSyncStatus;
        this.shipmentStatus = shipmentStatus;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeValue(this.compositeKey);
        dest.writeValue(this.awbNo);
        dest.writeValue(this.sequence_no);
        dest.writeInt(this.shipmentSyncStatus);
        dest.writeInt(this.shipmentStatus);
    }

    public void readFromParcel(Parcel source){
        this.compositeKey = (String) source.readValue(Long.class.getClassLoader());
        this.awbNo = (Long) source.readValue(Long.class.getClassLoader());
        this.sequence_no = (Long) source.readValue(Long.class.getClassLoader());
        this.shipmentSyncStatus = source.readInt();
        this.shipmentStatus = source.readInt();
    }

    public DRSSequence(){
    }

    protected DRSSequence(Parcel in){
        this.compositeKey = (String) in.readValue(Long.class.getClassLoader());
        this.awbNo = (Long) in.readValue(Long.class.getClassLoader());
        this.sequence_no = (Long) in.readValue(Long.class.getClassLoader());
        this.shipmentSyncStatus = in.readInt();
        this.shipmentStatus = in.readInt();
    }

    public static final Creator<DRSSequence> CREATOR = new Creator<DRSSequence>() {
        @Override
        public DRSSequence createFromParcel(Parcel source){
            return new DRSSequence(source);
        }

        @Override
        public DRSSequence[] newArray(int size){
            return new DRSSequence[size];
        }
    };
}
