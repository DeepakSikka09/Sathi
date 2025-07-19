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
@Entity(tableName = "forward")
public class Forward {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    @JsonProperty("COD_UD_OTP")
    public ArrayList<String> cod_ud_otp;

    @JsonProperty("PPD_UD_OTP")
    public ArrayList<String> ppd_ud_otp;

    @JsonProperty("UD_OTP")
    public ArrayList<String> ud_otp;

    public ArrayList<String> getCod_ud_otp(){
        return cod_ud_otp;
    }

    public void setCod_ud_otp(ArrayList<String> cod_ud_otp){
        this.cod_ud_otp = cod_ud_otp;
    }

    public ArrayList<String> getPpd_ud_otp(){
        return ppd_ud_otp;
    }

    public void setPpd_ud_otp(ArrayList<String> ppd_ud_otp){
        this.ppd_ud_otp = ppd_ud_otp;
    }


    @JsonProperty("UD_OTP")
    public ArrayList<String> getUd_otp(){
        return ud_otp;
    }

    @JsonProperty("UD_OTP")
    public void setUd_otp(ArrayList<String> ud_otp){
        this.ud_otp = ud_otp;
    }

    @JsonProperty("SECURED")
    public ArrayList<String> secured;

    @JsonProperty("SECURED")
    public ArrayList<String> getSecured(){
        return secured;
    }

    @JsonProperty("SECURED")
    public void setSecured(ArrayList<String> ud_otp){
        this.secured = ud_otp;
    }

}

