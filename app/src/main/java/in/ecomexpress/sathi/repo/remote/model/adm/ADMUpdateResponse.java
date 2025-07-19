package in.ecomexpress.sathi.repo.remote.model.adm;

public class ADMUpdateResponse {

    public boolean isStatus(){
        return status;
    }

    public void setStatus(boolean status){
        this.status = status;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    boolean status;
    String description;
}
