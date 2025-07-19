package in.ecomexpress.sathi.repo.remote.model.remark;

public class RemarkUpdateRequest {

    private String awb_number;
    private String drs_id;
    private String fe_code;
    private String fe_remark;
    private String reschedule_time;
    private String date;
    private String lat;
    private String lng;

    public String getAwb_number(){
        return awb_number;
    }

    public void setAwb_number(String awb_number){
        this.awb_number = awb_number;
    }

    public String getDrs_id(){
        return drs_id;
    }

    public void setDrs_id(String drs_id){
        this.drs_id = drs_id;
    }

    public String getFe_code(){
        return fe_code;
    }

    public void setFe_code(String fe_code){
        this.fe_code = fe_code;
    }

    public String getFe_remark(){
        return fe_remark;
    }

    public void setFe_remark(String fe_remark){
        this.fe_remark = fe_remark;
    }

    public String getReschedule_time(){
        return reschedule_time;
    }

    public void setReschedule_time(String reschedule_time){
        this.reschedule_time = reschedule_time;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date){
        this.date = date;
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
}
