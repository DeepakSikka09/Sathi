package in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dhananjayk on 20-11-2018.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionFormFieldDetail implements Parcelable {

    @Embedded
    @JsonProperty("doc_list")
    private DocList docList = null;

    @JsonProperty("neo_app_name")
    private String neoAppName = null;
    @JsonProperty("neo_app_package_name")
    private String neoAppPackageName = null;
    @JsonProperty("neo_app_url")
    private String neoAppUrl = null;
    @JsonProperty("neo_app_ver")
    private String neoAppVer = null;
    @JsonProperty("icici_bkyc_header")
    private String iciciBkycHeader;
    @JsonProperty("icici_bkyc_url")
    private String iciciBkycUrl;

    @JsonProperty("icici_wadh")
    private String icici_wadh;

    @JsonProperty("wadh_value")
    private String wadh_value;

    @JsonProperty("ftype")
    private String ftype="0";

    @JsonProperty("transtype")
    private String transtype;

    @JsonProperty("MANIFEST_AGAIN")
    private String MANIFEST_AGAIN;

    @JsonProperty("isUrnRequired")
    private String isUrnRequired;

    @JsonProperty("channel")
    private String channel;

    @JsonProperty("Web_header_client_Pass")
    private String Web_header_client_Pass;

    @JsonProperty("ekyc_url")
    private String ekyc_url;

    @JsonProperty("IDFC_AUTH_scope")
    private String IDFC_AUTH_scope;

    @JsonProperty("IDFC_AUTH")
    private String IDFC_AUTH;

    @JsonProperty("IDFC_vndcode")
    private String IDFC_vndcode;


    @JsonProperty("authversiontouse")
    private String authversiontouse;

    @JsonProperty("general_questionaire")
    private String general_questionaire;

    @JsonProperty("demo")
    private String demo;

    @JsonProperty("Username")
    private String Username;

    @JsonProperty("url")
    private String url;

    @JsonProperty("Password")
    private String Password;

    @JsonProperty("Yes_wadh_Api_url")
    private String Yes_wadh_Api_url;

    public String getYes_wadh_Api_Username(){
        return Yes_wadh_Api_Username;
    }

    public void setYes_wadh_Api_Username(String yes_wadh_Api_Username){
        Yes_wadh_Api_Username = yes_wadh_Api_Username;
    }

    @JsonProperty("Yes_wadh_Api_Username")
    private String Yes_wadh_Api_Username;

    @JsonProperty("Yes_wadh_Api_Password")
    private String Yes_wadh_Api_Password;
    @JsonProperty("IDFC_AUTH_kid")
    private String IDFC_AUTH_kid;

    @JsonProperty("IDFC_AUTH_aud")
    private String IDFC_AUTH_aud;

    @JsonProperty("Yes_bkyc_url_Username")
    private String Yes_bkyc_url_Username;

    @JsonProperty("Yes_bkyc_url")
    private String Yes_bkyc_url;

    @JsonProperty("Yes_bkyc_url_Password")
    private String Yes_bkyc_url_Password;

    @JsonProperty("IsmobileValidationRequired")
    private String IsmobileValidationRequired;

    @JsonProperty("client_id")
    private String client_id;

    @JsonProperty("SEND_OTP_VERIFIED_SMS")
    private String SEND_OTP_VERIFIED_SMS;

//    @JsonProperty("id")
//    private String id;

    @JsonProperty("consenttext")
    private String consenttext;

    @JsonProperty("key")
    private String key;

    @JsonProperty("Web_header_client_ID")
    private String Web_header_client_ID;

    @JsonProperty("ekyctype")
    private String ekyctype;

    @JsonProperty("icici_bkyc_status_url")
    private String iciciBkycstatusUrl;

    @JsonProperty("IDFC_caMID")
    private String IDFC_caMID;

    @JsonProperty("referenceInd")
    private String referenceInd;

    @JsonProperty("productType")
    private String productType;

    @JsonProperty("IDFC_productType")
    private String IDFC_productType;


    @JsonProperty("IDFC_agentInfo")
    private String IDFC_agentInfo;

    @JsonProperty("IDFC_source")
    private String IDFC_source;

    @JsonProperty("IDFC_URL")
    private String IDFC_URL;

    @JsonProperty("IDFC_AUTH_client_id")
    private String IDFC_AUTH_client_id;

    @JsonProperty("IDFC_referenceInd")
    private String IDFC_referenceInd;


    @JsonProperty("IDFC_AUTH_grant_type")
    private String IDFC_AUTH_grant_type;

    @JsonProperty("IDFC_AUTH_client_assertion_type")
    private String IDFC_AUTH_client_assertion_type;



    public String getIciciBkycstatusUrl() {
        return iciciBkycstatusUrl;
    }

    public void setIciciBkycstatusUrl(String iciciBkycstatusUrl) {
        this.iciciBkycstatusUrl = iciciBkycstatusUrl;
    }

    public String getIciciBkycHeader() {
        return iciciBkycHeader;
    }

    public void setIciciBkycHeader(String iciciBkycHeader) {
        this.iciciBkycHeader = iciciBkycHeader;
    }

    public String getIciciBkycUrl() {
        return iciciBkycUrl;
    }

    public void setIciciBkycUrl(String iciciBkycUrl) {
        this.iciciBkycUrl = iciciBkycUrl;
    }

    public DocList getDocList() {
        return docList;
    }

    public void setDocList(DocList docList) {
        this.docList = docList;
    }

    public String getNeoAppName() {
        return neoAppName;
    }

    public void setNeoAppName(String neoAppName) {
        this.neoAppName = neoAppName;
    }

    public String getNeoAppPackageName() {
        return neoAppPackageName;
    }

    public void setNeoAppPackageName(String neoAppPackageName) {
        this.neoAppPackageName = neoAppPackageName;
    }

    public String getNeoAppUrl() {
        return neoAppUrl;
    }

    public void setNeoAppUrl(String neoAppUrl) {
        this.neoAppUrl = neoAppUrl;
    }

    public String getNeoAppVer() {
        return neoAppVer;
    }

    public void setNeoAppVer(String neoAppVer) {
        this.neoAppVer = neoAppVer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.docList, flags);
        dest.writeString(this.neoAppName);
        dest.writeString(this.neoAppPackageName);
        dest.writeString(this.neoAppUrl);
        dest.writeString(this.neoAppVer);
        dest.writeString(this.iciciBkycHeader);
        dest.writeString(this.iciciBkycUrl);
        dest.writeString(this.iciciBkycstatusUrl);
        dest.writeString(this.Username);
        dest.writeString(this.url);
        dest.writeString(this.Password);
        dest.writeString(this.ekyc_url);
        dest.writeString(this.Yes_wadh_Api_url);
        dest.writeString(this.Yes_wadh_Api_Username);
        dest.writeString(this.Yes_wadh_Api_Password);
        dest.writeString(this.Yes_bkyc_url_Username);
        dest.writeString(this.Yes_bkyc_url_Password);
        dest.writeString(this.Yes_bkyc_url);
        dest.writeString(this.wadh_value);
        dest.writeString(this.ftype);
        dest.writeString(this.IDFC_AUTH_kid);
        dest.writeString(this.IDFC_AUTH_client_id);
        dest.writeString(this.IDFC_AUTH_aud);
        dest.writeString(this.IDFC_AUTH);
        dest.writeString(this.IDFC_AUTH_grant_type);
        dest.writeString(this.IDFC_AUTH_client_assertion_type);
        dest.writeString(this.IDFC_AUTH_scope);
        dest.writeString(this.IDFC_URL);
        dest.writeString(this.IDFC_agentInfo);
        dest.writeString(this.IDFC_caMID);
        dest.writeString(this.IDFC_referenceInd);
        dest.writeString(this.IDFC_productType);
        dest.writeString(this.IDFC_source);

    }

    public QuestionFormFieldDetail() {
    }

    protected QuestionFormFieldDetail(Parcel in) {
        this.docList = in.readParcelable(DocList.class.getClassLoader());
        this.neoAppName = in.readString();
        this.neoAppPackageName = in.readString();
        this.neoAppUrl = in.readString();
        this.neoAppVer = in.readString();
        this.iciciBkycHeader=in.readString();
        this.iciciBkycUrl=in.readString();
        this.iciciBkycstatusUrl=in.readString();
        this.Username=in.readString();
        this.url= in.readString();
        this.Password= in.readString();
        this.ekyc_url= in.readString();
        this.Yes_wadh_Api_url = in.readString();
        this.Yes_wadh_Api_Username = in.readString();
        this.Yes_wadh_Api_Password = in.readString();
        this.Yes_bkyc_url_Username= in.readString();
        this.Yes_bkyc_url_Password= in.readString();
        this.Yes_bkyc_url = in.readString();
        this.wadh_value =in.readString();
        this.ftype =in.readString();
        this.IDFC_AUTH_kid= in.readString();
        this.IDFC_AUTH_client_id = in.readString();
        this.IDFC_AUTH_aud = in.readString();
        this.IDFC_AUTH = in.readString();
        this.IDFC_AUTH_grant_type = in.readString();
        this.IDFC_AUTH_client_assertion_type = in.readString();
        this.IDFC_AUTH_scope = in.readString();
        this.IDFC_URL = in.readString();
        this.IDFC_agentInfo = in.readString();
        this.IDFC_caMID = in.readString();
        this.IDFC_referenceInd = in.readString();
        this.IDFC_productType = in.readString();
        this.IDFC_source = in.readString();


    }

    public static final Creator<QuestionFormFieldDetail> CREATOR = new Creator<QuestionFormFieldDetail>() {
        @Override
        public QuestionFormFieldDetail createFromParcel(Parcel source) {
            return new QuestionFormFieldDetail(source);
        }

        @Override
        public QuestionFormFieldDetail[] newArray(int size) {
            return new QuestionFormFieldDetail[size];
        }
    };

    public String getIcici_wadh() {
        return icici_wadh;
    }

    public void setIcici_wadh(String icici_wadh) {
        this.icici_wadh = icici_wadh;
    }

    public String getTranstype() {
        return transtype;
    }

    public void setTranstype(String transtype) {
        this.transtype = transtype;
    }

    public String getMANIFEST_AGAIN() {
        return MANIFEST_AGAIN;
    }

    public void setMANIFEST_AGAIN(String MANIFEST_AGAIN) {
        this.MANIFEST_AGAIN = MANIFEST_AGAIN;
    }

    public String getIsUrnRequired() {
        return isUrnRequired;
    }

    public void setIsUrnRequired(String isUrnRequired) {
        this.isUrnRequired = isUrnRequired;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getWeb_header_client_Pass() {
        return Web_header_client_Pass;
    }

    public void setWeb_header_client_Pass(String web_header_client_Pass) {
        Web_header_client_Pass = web_header_client_Pass;
    }

    public String getEkyc_url() {
        return ekyc_url;
    }

    public void setEkyc_url(String ekyc_url) {
        this.ekyc_url = ekyc_url;
    }

    public String getAuthversiontouse() {
        return authversiontouse;
    }

    public void setAuthversiontouse(String authversiontouse) {
        this.authversiontouse = authversiontouse;
    }

    public String getGeneral_questionaire() {
        return general_questionaire;
    }

    public void setGeneral_questionaire(String general_questionaire) {
        this.general_questionaire = general_questionaire;
    }

    public String getDemo() {
        return demo;
    }

    public void setDemo(String demo) {
        this.demo = demo;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getSEND_OTP_VERIFIED_SMS() {
        return SEND_OTP_VERIFIED_SMS;
    }

    public void setSEND_OTP_VERIFIED_SMS(String SEND_OTP_VERIFIED_SMS) {
        this.SEND_OTP_VERIFIED_SMS = SEND_OTP_VERIFIED_SMS;
    }

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }

    public String getConsenttext() {
        return consenttext;
    }

    public void setConsenttext(String consenttext) {
        this.consenttext = consenttext;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getWeb_header_client_ID() {
        return Web_header_client_ID;
    }

    public void setWeb_header_client_ID(String web_header_client_ID) {
        Web_header_client_ID = web_header_client_ID;
    }

    public String getEkyctype() {
        return ekyctype;
    }

    public void setEkyctype(String ekyctype) {
        this.ekyctype = ekyctype;
    }

    public String getUsername(){
        return Username;
    }

    public void setUsername(String username){
        Username = username;
    }

    public String getPassword(){
        return Password;
    }

    public void setPassword(String password){
        Password = password;
    }

    public String getUrl(){
        return url;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public String getIsmobileValidationRequired(){
        return IsmobileValidationRequired;
    }

    public void setIsmobileValidationRequired(String ismobileValidationRequired){
        IsmobileValidationRequired = ismobileValidationRequired;
    }

    public String getIDFC_caMID(){
        return IDFC_caMID;
    }

    public void setIDFC_caMID(String IDFC_caMID){
        this.IDFC_caMID = IDFC_caMID;
    }

    public String getReferenceInd(){
        return referenceInd;
    }

    public void setReferenceInd(String referenceInd){
        this.referenceInd = referenceInd;
    }

    public String getProductType(){
        return productType;
    }

    public void setProductType(String productType){
        this.productType = productType;
    }

    public String getIDFC_agentInfo(){
        return IDFC_agentInfo;
    }

    public void setIDFC_agentInfo(String IDFC_agentInfo){
        this.IDFC_agentInfo = IDFC_agentInfo;
    }

    public String getIDFC_source(){
        return IDFC_source;
    }

    public void setIDFC_source(String IDFC_source){
        this.IDFC_source = IDFC_source;
    }

    public String getIDFC_URL(){
        return IDFC_URL;
    }

    public void setIDFC_URL(String IDFC_URL){
        this.IDFC_URL = IDFC_URL;
    }

    public String getYes_wadh_Api_url(){
        return Yes_wadh_Api_url;
    }

    public void setYes_wadh_Api_url(String yes_wadh_Api_url){
        Yes_wadh_Api_url = yes_wadh_Api_url;
    }

    public String getYes_wadh_Api_Password(){
        return Yes_wadh_Api_Password;
    }

    public void setYes_wadh_Api_Password(String yes_wadh_Api_Password){
        Yes_wadh_Api_Password = yes_wadh_Api_Password;
    }

    public String getYes_bkyc_url_Username(){
        return Yes_bkyc_url_Username;
    }

    public void setYes_bkyc_url_Username(String yes_bkyc_url_Username){
        Yes_bkyc_url_Username = yes_bkyc_url_Username;
    }

    public String getYes_bkyc_url(){
        return Yes_bkyc_url;
    }

    public void setYes_bkyc_url(String yes_bkyc_url){
        Yes_bkyc_url = yes_bkyc_url;
    }

    public String getYes_bkyc_url_Password(){
        return Yes_bkyc_url_Password;
    }

    public void setYes_bkyc_url_Password(String yes_bkyc_url_Password){
        Yes_bkyc_url_Password = yes_bkyc_url_Password;
    }

    public String getWadh_value(){
        return wadh_value;
    }

    public void setWadh_value(String wadh_value){
        this.wadh_value = wadh_value;
    }

    public String getFtype(){
        return ftype;
    }

    public void setFtype(String ftype){
        this.ftype = ftype;
    }

    public String getIDFC_AUTH_scope(){
        return IDFC_AUTH_scope;
    }

    public void setIDFC_AUTH_scope(String IDFC_AUTH_scope){
        this.IDFC_AUTH_scope = IDFC_AUTH_scope;
    }

    public String getIDFC_vndcode(){
        return IDFC_vndcode;
    }

    public void setIDFC_vndcode(String IDFC_vndcode){
        this.IDFC_vndcode = IDFC_vndcode;
    }

    public String getIDFC_AUTH_kid(){
        return IDFC_AUTH_kid;
    }

    public void setIDFC_AUTH_kid(String IDFC_AUTH_kid){
        this.IDFC_AUTH_kid = IDFC_AUTH_kid;
    }

    public String getIDFC_AUTH_client_id(){
        return IDFC_AUTH_client_id;
    }

    public void setIDFC_AUTH_client_id(String IDFC_AUTH_client_id){
        this.IDFC_AUTH_client_id = IDFC_AUTH_client_id;
    }

    public String getIDFC_AUTH_aud(){
        return IDFC_AUTH_aud;
    }

    public void setIDFC_AUTH_aud(String IDFC_AUTH_aud){
        this.IDFC_AUTH_aud = IDFC_AUTH_aud;
    }

    public String getIDFC_AUTH(){
        return IDFC_AUTH;
    }

    public void setIDFC_AUTH(String IDFC_AUTH){
        this.IDFC_AUTH = IDFC_AUTH;
    }

    public String getIDFC_AUTH_grant_type(){
        return IDFC_AUTH_grant_type;
    }

    public void setIDFC_AUTH_grant_type(String IDFC_AUTH_grant_type){
        this.IDFC_AUTH_grant_type = IDFC_AUTH_grant_type;
    }

    public String getIDFC_AUTH_client_assertion_type(){
        return IDFC_AUTH_client_assertion_type;
    }

    public void setIDFC_AUTH_client_assertion_type(String IDFC_AUTH_client_assertion_type){
        this.IDFC_AUTH_client_assertion_type = IDFC_AUTH_client_assertion_type;
    }

    public String getIDFC_referenceInd(){
        return IDFC_referenceInd;
    }

    public void setIDFC_referenceInd(String IDFC_referenceInd){
        this.IDFC_referenceInd = IDFC_referenceInd;
    }

    public String getIDFC_productType(){
        return IDFC_productType;
    }

    public void setIDFC_productType(String IDFC_productType){
        this.IDFC_productType = IDFC_productType;
    }
}
