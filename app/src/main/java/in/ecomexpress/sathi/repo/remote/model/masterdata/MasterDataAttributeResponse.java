package in.ecomexpress.sathi.repo.remote.model.masterdata;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterDataAttributeResponse implements Parcelable {
    private static final boolean DEFAULT = false;
    @JsonProperty("CALLM")
    public boolean cALLM = false;
    @JsonProperty("IMG")
    public boolean iMG = false;
    @JsonProperty("OTP")
    public boolean oTP = false;

    @JsonProperty("WHATSAPP_MAND")
    public boolean WHATSAPP_MAND = false;

    @JsonProperty("RCHD")
    public boolean rCHD = false;
    @JsonProperty("COD")
    public boolean cOD = false;
    @JsonProperty("PPD")
    public boolean pPD = false;
    @JsonProperty("UD_OTP")
    public boolean UD_OTP = false;

    @JsonProperty("RCHD_OTP")
    public boolean RCHD_OTP = false;

    @JsonProperty("IMAGEM")
    public boolean IMAGEM = false;

    @JsonProperty("RTS_SINGLE")
    public boolean RTS_SINGLE = false;

    @JsonProperty("DS_SL")
    public boolean DS_SL = false;

    @JsonProperty("PR_NA")
    public boolean PR_NA = false;

    @JsonProperty("IMAGEM")
    public boolean isIMAGEM(){
        return IMAGEM;
    }
    @JsonProperty("IMAGEM")
    public void setIMAGEM(boolean IMAGEM){
        this.IMAGEM = IMAGEM;
    }

    @JsonProperty("RTS_SINGLE")
    public boolean isRTS_SINGLE(){
        return RTS_SINGLE;
    }

    @JsonProperty("RTS_SINGLE")
    public void setRTS_SINGLE(boolean RTS_SINGLE){
        this.RTS_SINGLE = RTS_SINGLE;
    }

    @JsonProperty("WHATSAPP_MAND")
    public boolean isWHATSAPP_MAND(){
        return WHATSAPP_MAND;
    }

    @JsonProperty("WHATSAPP_MAND")
    public void setWHATSAPP_MAND(boolean WHATSAPP_MAND){
        this.WHATSAPP_MAND = WHATSAPP_MAND;
    }

    @JsonProperty("PR_NA")
    public boolean isPR_NA(){
        return PR_NA;
    }
    @JsonProperty("PR_NA")
    public void setPR_NA(boolean PR_NA){
        this.PR_NA = PR_NA;
    }

    @JsonProperty("SECURED")
    public boolean isSECURED() {
        return SECURED;
    }

    @JsonProperty("SECURED")
    public void setSECURED(boolean SECURED) {
        this.SECURED = SECURED;
    }

    @JsonProperty("SECURED")
    public boolean SECURED = false;

    @JsonProperty("MPS")
    public boolean isMPS() {
        return MPS;
    }

    @JsonProperty("MPS")
    public void setMPS(boolean MPS) {
        this.MPS = MPS;
    }

    @JsonProperty("MPS")
    public boolean MPS = false;

    @JsonProperty("CALLM")
    public boolean iscALLM() {
        return cALLM;
    }

    @JsonProperty("CALLM")
    public void setcALLM(boolean cALLM) {
        this.cALLM = cALLM;
    }

    @JsonProperty("IMG")
    public boolean isiMG() {
        return iMG;
    }

    @JsonProperty("IMG")
    public void setiMG(boolean iMG) {
        this.iMG = iMG;
    }

    @JsonProperty("OTP")
    public boolean isoTP() {
        return oTP;
    }

    @JsonProperty("OTP")
    public void setoTP(boolean oTP) {
        this.oTP = oTP;
    }

    @JsonProperty("UD_OTP")
    public boolean isUD_OTP() {
        return UD_OTP;
    }

    @JsonProperty("UD_OTP")
    public void setUD_OTP(boolean UD_OTP) {
        this.UD_OTP = UD_OTP;
    }

    @JsonProperty("RCHD_OTP")
    public boolean isRCHD_OTP(){
        return RCHD_OTP;
    }
    @JsonProperty("RCHD_OTP")
    public void setRCHD_OTP(boolean RCHD_OTP){
        this.RCHD_OTP = RCHD_OTP;
    }

    @JsonProperty("RCHD")
    public boolean isrCHD() {
        return rCHD;
    }

    @JsonProperty("RCHD")
    public void setrCHD(boolean rCHD) {
        this.rCHD = rCHD;
    }

    @JsonProperty("COD")
    public boolean iscOD() {
        return cOD;
    }

    @JsonProperty("COD")
    public void setcOD(boolean cOD) {
        this.cOD = cOD;
    }

    @JsonProperty("PPD")
    public boolean ispPD() {
        return pPD;
    }

    @JsonProperty("PPD")
    public void setpPD(boolean pPD) {
        this.pPD = pPD;
    }

    public MasterDataAttributeResponse() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.cALLM ? (byte) 1 : (byte) 0);
        dest.writeByte(this.RCHD_OTP ? (byte) 1 : (byte) 0);
        dest.writeByte(this.UD_OTP ? (byte) 1 : (byte) 0);
        dest.writeByte(this.WHATSAPP_MAND ? (byte) 1 : (byte) 0);
        dest.writeByte(this.iMG ? (byte) 1 : (byte) 0);
        dest.writeByte(this.oTP ? (byte) 1 : (byte) 0);
        dest.writeByte(this.rCHD ? (byte) 1 : (byte) 0);
        dest.writeByte(this.cOD ? (byte) 1 : (byte) 0);
        dest.writeByte(this.pPD ? (byte) 1 : (byte) 0);
        dest.writeByte(this.SECURED ? (byte) 1 : (byte) 0);
        dest.writeByte(this.MPS ? (byte) 1 : (byte) 0);
    }

    protected MasterDataAttributeResponse(Parcel in) {
        this.cALLM = in.readByte() != 0;
        this.RCHD_OTP = in.readByte() != 0;
        this.UD_OTP = in.readByte() != 0;
        this.WHATSAPP_MAND = in.readByte() != 0;
        this.iMG = in.readByte() != 0;
        this.oTP = in.readByte() != 0;
        this.rCHD = in.readByte() != 0;
        this.cOD = in.readByte() != 0;
        this.pPD = in.readByte() != 0;
        this.SECURED = in.readByte() != 0;
        this.MPS = in.readByte() != 0;
    }

    public static final Creator<MasterDataAttributeResponse> CREATOR = new Creator<MasterDataAttributeResponse>() {
        @Override
        public MasterDataAttributeResponse createFromParcel(Parcel source) {
            return new MasterDataAttributeResponse(source);
        }

        @Override
        public MasterDataAttributeResponse[] newArray(int size) {
            return new MasterDataAttributeResponse[size];
        }
    };
}
