package in.ecomexpress.sathi.repo.remote.model.adm;

public class ADMSpinnerData {
    public String getSlot_hour(){
        return slot_hour;
    }

    public void setSlot_hour(String slot_hour){
        this.slot_hour = slot_hour;
    }

    public String getPlanned_status_code(){
        return planned_status_code;
    }

    public void setPlanned_status_code(String planned_status_code){
        this.planned_status_code = planned_status_code;
    }

    String slot_hour;
    String planned_status_code;

    public String isStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    String status;

}
