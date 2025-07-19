package in.ecomexpress.sathi.repo.remote.model.adm;

public class ADMUpdateRequest {

    public String getEmployee_code(){
        return employee_code;
    }

    public void setEmployee_code(String employee_code){
        this.employee_code = employee_code;
    }

    public String getDay_of_week(){
        return day_of_week;
    }

    public void setDay_of_week(String day_of_week){
        this.day_of_week = day_of_week;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date){
        this.date = date;
    }

    public boolean isWeekly_off(){
        return weekly_off;
    }

    public void setWeekly_off(boolean weekly_off){
        this.weekly_off = weekly_off;
    }

    public String getPlanned_intime(){
        return planned_intime;
    }

    public void setPlanned_intime(String planned_intime){
        this.planned_intime = planned_intime;
    }

    public String getPlanned_slot_code(){
        return planned_slot_code;
    }

    public void setPlanned_slot_code(String planned_slot_code){
        this.planned_slot_code = planned_slot_code;
    }

    String employee_code;
    String day_of_week;
    String date;
    boolean weekly_off;
    String planned_intime;
    String planned_slot_code;
}
