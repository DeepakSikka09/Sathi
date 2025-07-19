package in.ecomexpress.sathi.repo.remote.model.masterdata;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dhananjayk on 24-11-2018.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EDSMasterDataAttributeResponse implements Parcelable {
    @JsonProperty("CALLM")
    public boolean cALLM=false;
    @JsonProperty("IMG")
    public boolean iMG=false;
    @JsonProperty("OTP")
    public boolean oTP=false;
    @JsonProperty("RCHD")
    public boolean rCHD=false;
    @JsonProperty("EDS_ACTIVITY")
    public boolean eDSActivity=false;
    @JsonProperty("EDS_ACTIVITY_LIST")
    public boolean eDSActivityList=false;
    @JsonProperty("EDS_OTP")
    public boolean eDSOTP=false;

    @JsonProperty("WHATSAPP_MAND")
    public boolean WHATSAPP_MAND = false;

    @JsonProperty("PR_NA")
    public boolean PR_NA=false;

    // new keys
    @JsonProperty("UD_OTP")
    public boolean UD_OTP = false;

    @JsonProperty("RCHD_OTP")
    public boolean RCHD_OTP = false;


    @JsonProperty("SECURED")
    public boolean SECURED=false;
    @JsonProperty("EDS_CC")
    public boolean EDS_CC=false;
    @JsonProperty("EDS_CPV")
    public boolean EDS_CPV=false;
    @JsonProperty("EDS_EKYC")
    public boolean EDS_EKYC=false;
    @JsonProperty("EDS_UDAAN")
    public boolean EDS_UDAAN=false;
    @JsonProperty("EDS_IMAGE")
    public boolean EDS_IMAGE=false;
    @JsonProperty("EDS_PAYTM_IMAGE")
    public boolean EDS_PAYTM_IMAGE=false;
    @JsonProperty("EDS_DC")
    public boolean EDS_DC=false;
    @JsonProperty("EDS_DV")
    public boolean EDS_DV=false;

    @JsonProperty("EDS_EKYC_FAIL")
    public boolean EDS_EKYC_FAIL=false;

    protected EDSMasterDataAttributeResponse(Parcel in) {
        cALLM = in.readByte() != 0;
        iMG = in.readByte() != 0;
        oTP = in.readByte() != 0;
        rCHD = in.readByte() != 0;
        eDSActivity = in.readByte() != 0;
        eDSActivityList = in.readByte() != 0;
        eDSOTP = in.readByte() != 0;
        SECURED = in.readByte() != 0;
        EDS_CC = in.readByte() != 0;
        EDS_CPV = in.readByte() != 0;
        EDS_EKYC = in.readByte() != 0;
        EDS_UDAAN = in.readByte() != 0;
        EDS_IMAGE = in.readByte() != 0;
        EDS_PAYTM_IMAGE = in.readByte() != 0;
        EDS_DC = in.readByte() != 0;
        EDS_DV = in.readByte() != 0;
        EDS_EKYC_FAIL = in.readByte() != 0;
    }

    public static final Creator<EDSMasterDataAttributeResponse> CREATOR = new Creator<EDSMasterDataAttributeResponse>() {
        @Override
        public EDSMasterDataAttributeResponse createFromParcel(Parcel in) {
            return new EDSMasterDataAttributeResponse(in);
        }

        @Override
        public EDSMasterDataAttributeResponse[] newArray(int size) {
            return new EDSMasterDataAttributeResponse[size];
        }
    };
    @JsonProperty("PR_NA")
    public boolean isPR_NA(){
        return PR_NA;
    }
    @JsonProperty("PR_NA")
    public void setPR_NA(boolean PR_NA){
        this.PR_NA = PR_NA;
    }

    @JsonProperty("CALLM")
    public boolean iscALLM() {
        return cALLM;
    }
    @JsonProperty("CALLM")
    public void setcALLM(boolean cALLM) {
        this.cALLM = cALLM;
    }
    @JsonProperty("IMAGEM")
    public boolean isiMG() {
        return iMG;
    }

    @JsonProperty("IMAGEM")
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

    @JsonProperty("RCHD")
    public boolean isrCHD() {
        return rCHD;
    }

    @JsonProperty("RCHD")
    public void setrCHD(boolean rCHD) {
        this.rCHD = rCHD;
    }

    @JsonProperty("EDS_ACTIVITY")
    public boolean iseDSActivity() {
        return eDSActivity;
    }

    @JsonProperty("EDS_ACTIVITY")
    public void seteDSActivity(boolean eDSActivity) {
        this.eDSActivity = eDSActivity;
    }

    @JsonProperty("EDS_ACTIVITY_LIST")
    public boolean iseDSActivityList() {
        return eDSActivityList;
    }

    @JsonProperty("EDS_ACTIVITY_LIST")
    public void seteDSActivityList(boolean eDSActivityList) {
        this.eDSActivityList = eDSActivityList;
    }

    @JsonProperty("EDS_OTP")
    public boolean iseDSOTP() {
        return eDSOTP;
    }

    @JsonProperty("EDS_OTP")
    public void seteDSOTP(boolean eDSOTP) {
        this.eDSOTP = eDSOTP;
    }

    @JsonProperty("WHATSAPP_MAND")
    public boolean isWHATSAPP_MAND(){
        return WHATSAPP_MAND;
    }

    @JsonProperty("WHATSAPP_MAND")
    public void setWHATSAPP_MAND(boolean WHATSAPP_MAND){
        this.WHATSAPP_MAND = WHATSAPP_MAND;
    }

    public EDSMasterDataAttributeResponse() {
    }
    public boolean isSECURED() {
        return SECURED;
    }

    public void setSECURED(boolean SECURED) {
        this.SECURED = SECURED;
    }

    public boolean isEDS_CC() {
        return EDS_CC;
    }

    public void setEDS_CC(boolean EDS_CC) {
        this.EDS_CC = EDS_CC;
    }

    public boolean isEDS_CPV() {
        return EDS_CPV;
    }

    public void setEDS_CPV(boolean EDS_CPV) {
        this.EDS_CPV = EDS_CPV;
    }

    public boolean isEDS_EKYC() {
        return EDS_EKYC;
    }

    public void setEDS_EKYC(boolean EDS_EKYC) {
        this.EDS_EKYC = EDS_EKYC;
    }

    public boolean isEDS_UDAAN() {
        return EDS_UDAAN;
    }

    public void setEDS_UDAAN(boolean EDS_UDAAN) {
        this.EDS_UDAAN = EDS_UDAAN;
    }

    public boolean isEDS_IMAGE() {
        return EDS_IMAGE;
    }

    public void setEDS_IMAGE(boolean EDS_IMAGE) {
        this.EDS_IMAGE = EDS_IMAGE;
    }

    public boolean isEDS_PAYTM_IMAGE() {
        return EDS_PAYTM_IMAGE;
    }

    public void setEDS_PAYTM_IMAGE(boolean EDS_PAYTM_IMAGE) {
        this.EDS_PAYTM_IMAGE = EDS_PAYTM_IMAGE;
    }

    public boolean isEDS_DC() {
        return EDS_DC;
    }

    public void setEDS_DC(boolean EDS_DC) {
        this.EDS_DC = EDS_DC;
    }

    public boolean isEDS_DV() {
        return EDS_DV;
    }

    public void setEDS_DV(boolean EDS_DV) {
        this.EDS_DV = EDS_DV;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (cALLM ? 1 : 0));
        parcel.writeByte((byte) (iMG ? 1 : 0));
        parcel.writeByte((byte) (oTP ? 1 : 0));
        parcel.writeByte((byte) (rCHD ? 1 : 0));
        parcel.writeByte((byte) (eDSActivity ? 1 : 0));
        parcel.writeByte((byte) (eDSActivityList ? 1 : 0));
        parcel.writeByte((byte) (eDSOTP ? 1 : 0));
        parcel.writeByte((byte) (SECURED ? 1 : 0));
        parcel.writeByte((byte) (EDS_CC ? 1 : 0));
        parcel.writeByte((byte) (EDS_CPV ? 1 : 0));
        parcel.writeByte((byte) (EDS_EKYC ? 1 : 0));
        parcel.writeByte((byte) (EDS_UDAAN ? 1 : 0));
        parcel.writeByte((byte) (EDS_IMAGE ? 1 : 0));
        parcel.writeByte((byte) (EDS_PAYTM_IMAGE ? 1 : 0));
        parcel.writeByte((byte) (EDS_DC ? 1 : 0));
        parcel.writeByte((byte) (EDS_DV ? 1 : 0));
        parcel.writeByte((byte)(EDS_EKYC_FAIL ? 1 : 0));
    }
}