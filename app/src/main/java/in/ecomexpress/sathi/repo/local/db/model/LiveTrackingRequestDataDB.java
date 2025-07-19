package in.ecomexpress.sathi.repo.local.db.model;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LiveTrackingRequestDataDB {

    @JsonProperty("ts")
    private String ts;

    @JsonProperty("lat")
    private String lat;

    @JsonProperty("lng")
    private String lng;

    @JsonProperty("v_l_e_o")
    private String v_l_e_o = "";

    @JsonProperty("i_l_e_o")
    private String i_l_e_o = "";

    @JsonProperty("gps")
    private String gps = "";

    @JsonProperty("pProbs")
    public String pProbs = "";

    @JsonProperty("L_S_R")
    private String L_S_R = "";

    @JsonProperty("i_n_c")
    private String I_N_C = "";

    @JsonProperty("b_t_l")
    private String b_t_l = "";

    @JsonProperty("is_recent")
    private String is_recent = "";

    @JsonProperty("login_permission")
    private String login_permission = "";

    @JsonProperty("is_reboot")
    private String is_reboot = "";

    @JsonProperty("file_name")
    private String file_name = "";

    @JsonProperty("file_created")
    private String file_created = "";

    @JsonProperty("file_send")
    private String file_send = "";

    @JsonProperty("speed")
    private String speed = "";

    public String getI_N_C(){
        return I_N_C;
    }

    public void setI_N_C(String i_N_C){
        I_N_C = i_N_C;
    }



    public LiveTrackingRequestDataDB(){

    }

    public String getpProbs(){
        return pProbs;
    }

    public void setpProbs(String pProbs){
        this.pProbs = pProbs;
    }

    public String getTs(){
        return ts;
    }

    public void setTs(String ts){
        this.ts = ts;
    }

    public String getLat(){
        return lat;
    }

    public void setLat(String lat){
        this.lat = lat;
    }

    public String getLng(){
        return lng;
    }

    public void setLng(String lng){
        this.lng = lng;
    }

    public String getV_l_e_o(){
        return v_l_e_o;
    }

    public void setV_l_e_o(String v_l_e_o){
        this.v_l_e_o = v_l_e_o;
    }

    public String getI_l_e_o(){
        return i_l_e_o;
    }

    public void setI_l_e_o(String i_l_e_o){
        this.i_l_e_o = i_l_e_o;
    }

    public String getGps(){
        return gps;
    }

    public void setGps(String gps){
        this.gps = gps;
    }


    public String getL_S_R(){
        return L_S_R;
    }

    public void setL_S_R(String l_S_R){
        L_S_R = l_S_R;
    }

    public String getB_t_l(){
        return b_t_l;
    }

    public void setB_t_l(String b_t_l){
        this.b_t_l = b_t_l;
    }

    public String getIs_recent(){
        return is_recent;
    }

    public void setIs_recent(String is_recent){
        this.is_recent = is_recent;
    }

    public String getLogin_permission(){
        return login_permission;
    }

    public void setLogin_permission(String login_permission){
        this.login_permission = login_permission;
    }

    public String getIs_reboot(){
        return is_reboot;
    }

    public void setIs_reboot(String is_reboot){
        this.is_reboot = is_reboot;
    }

    public String getFile_name(){
        return file_name;
    }

    public void setFile_name(String file_name){
        this.file_name = file_name;
    }

    public String getFile_created(){
        return file_created;
    }

    public void setFile_created(String file_created){
        this.file_created = file_created;
    }

    public String getFile_send(){
        return file_send;
    }

    public void setFile_send(String file_send){
        this.file_send = file_send;
    }

    public String getSpeed(){
        return speed;
    }

    public void setSpeed(String speed){
        this.speed = speed;
    }
}
