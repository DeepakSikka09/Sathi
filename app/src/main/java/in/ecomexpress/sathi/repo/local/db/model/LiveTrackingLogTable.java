package in.ecomexpress.sathi.repo.local.db.model;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "LiveTrackingLogTable")
public class LiveTrackingLogTable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Long id;

    @JsonProperty("emp_code")
    private String emp_code = "";

    @JsonProperty("logs")
    private String logs = "";

    @JsonProperty("log_type")
    private String logtype;

    public LiveTrackingRequestDataDB getLiveTrackingRequestDataDB(){
        return liveTrackingRequestDataDB;
    }

    public void setLiveTrackingRequestDataDB(LiveTrackingRequestDataDB liveTrackingRequestDataDB){
        this.liveTrackingRequestDataDB = liveTrackingRequestDataDB;
    }

    public LiveTrackingLogTable()
    {

    }

    @Embedded
    @JsonProperty("LiveTrackingRequest")
    private LiveTrackingRequestDataDB liveTrackingRequestDataDB;

    public LiveTrackingLogTable(String emp_code , String logtype , String logs ,LiveTrackingRequestDataDB liveTrackingRequest){
        this.emp_code = emp_code;
        this.logs = logs;
        this.logtype = logtype;
        this.liveTrackingRequestDataDB =liveTrackingRequest;
    }

    @NonNull
    public Long getId(){
        return id;
    }

    public void setId(@NonNull Long id){
        this.id = id;
    }

    public String getEmp_code(){
        return emp_code;
    }

    public void setEmp_code(String emp_code){
        this.emp_code = emp_code;
    }

    public String getLogs(){
        return logs;
    }

    public void setLogs(String logs){
        this.logs = logs;
    }

    public String getLogtype(){
        return logtype;
    }

    public void setLogtype(String logtype){
        this.logtype = logtype;
    }
}

