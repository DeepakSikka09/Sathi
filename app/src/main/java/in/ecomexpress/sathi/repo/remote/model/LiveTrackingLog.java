package in.ecomexpress.sathi.repo.remote.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import in.ecomexpress.sathi.repo.local.db.model.LiveTrackingRequestDataDB;

public class LiveTrackingLog {

    @JsonProperty("emp_code")
    private String emp_code = "";

    @JsonProperty("logs")
    private String logs = "";

    @JsonProperty("LiveTrackingRequest")
    private LiveTrackingRequestData liveTrackingRequest;

    @JsonProperty("log_type")
    private String logtype;

    public LiveTrackingLog(String emp_code , String log_type , String logs , LiveTrackingRequestData liveTrackingRequest){
        this.emp_code = emp_code;
        this.logtype = log_type;
        this.logs = logs;
        this.liveTrackingRequest =liveTrackingRequest;
    }

    public String getEmp_code(){
        return emp_code;
    }

    public void setEmp_code(String emp_code){
        this.emp_code = emp_code;
    }

    public LiveTrackingRequestData getLiveTrackingRequest(){
        return liveTrackingRequest;
    }

    public void setLiveTrackingRequest(LiveTrackingRequestData liveTrackingRequest){
        this.liveTrackingRequest = liveTrackingRequest;
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

