package in.ecomexpress.sathi.repo.remote.model.masterdata;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "Reverse")
public class Reverse {
    public Reverse( ArrayList<String> rev_ud_otp, ArrayList<String> rqc_ud_otp, int id){

        this.rev_ud_otp = rev_ud_otp;
        this.rqc_ud_otp = rqc_ud_otp;
        this.id = id;
    }

    public Reverse(){
    }

    @JsonProperty("REV_UD_OTP")
    public ArrayList<String> rev_ud_otp;
    @JsonProperty("RQC_UD_OTP")
    public ArrayList<String> rqc_ud_otp;
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public ArrayList<String> getRev_ud_otp(){
        return rev_ud_otp;
    }

    public void setRev_ud_otp(ArrayList<String> rev_ud_otp){
        this.rev_ud_otp = rev_ud_otp;
    }

    public ArrayList<String> getRqc_ud_otp(){
        return rqc_ud_otp;
    }

    public void setRqc_ud_otp(ArrayList<String> rqc_ud_otp){
        this.rqc_ud_otp = rqc_ud_otp;
    }


}



