package in.ecomexpress.sathi.repo.remote.model.eds;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CashReceipt_Response implements Parcelable {
    @JsonProperty("code")
    public int code;
    @JsonProperty("receipt_status")
    public boolean receipt_status;


    public CashReceipt_Response(int code, boolean receipt_status)
    {
        this.code = code;
        this.receipt_status =receipt_status;
    }

    public CashReceipt_Response()
    {

    }

    protected CashReceipt_Response(Parcel in) {
        code = in.readInt();
        receipt_status = in.readByte() != 0;
    }

    public static final Creator<CashReceipt_Response> CREATOR = new Creator<CashReceipt_Response>() {
        @Override
        public CashReceipt_Response createFromParcel(Parcel in) {
            return new CashReceipt_Response(in);
        }

        @Override
        public CashReceipt_Response[] newArray(int size) {
            return new CashReceipt_Response[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(code);
        parcel.writeByte((byte) (receipt_status ? 1 : 0));
    }
}



