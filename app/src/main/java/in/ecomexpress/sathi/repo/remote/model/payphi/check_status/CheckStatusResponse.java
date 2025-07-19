package in.ecomexpress.sathi.repo.remote.model.payphi.check_status;

public class CheckStatusResponse {

    private Data response;

    private String status;

    public Data getResponse ()
    {
        return response;
    }

    public void setResponse (Data response)
    {
        this.response = response;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "CheckStatusResponse [response = "+response+", status = "+status+"]";
    }
   /* @JsonProperty("emsg")
    private String emsg;


    @JsonProperty("data")
    private Data data;

    @JsonProperty("success")
    private String success;

    public String getEmsg() {
        return emsg;
    }

    public void setEmsg(String emsg) {
        this.emsg = emsg;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "CheckStatusResponse [emsg = " + emsg + ", data = " + data + ", success = " + success + "]";
    }*/
}


