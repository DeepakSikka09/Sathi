package in.ecomexpress.sathi.repo.remote.model.payphi.raise_dispute;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import io.reactivex.annotations.NonNull;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "PaymentDisputedAwb")
public class PaymentDisputedAwb {

    @PrimaryKey
    @NonNull
    @JsonProperty("id")
    private Integer id;

    public String getDisputed_awb() {
        return disputed_awb;
    }

    public void setDisputed_awb(String disputed_awb) {
        this.disputed_awb = disputed_awb;
    }

    @JsonProperty("dispute_awb")
    private String disputed_awb;

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "RaiseDispute{" +
                "id=" + id +
                ", disputed_awb='" + disputed_awb + '\'' +
                '}';
    }
}
