package in.ecomexpress.sathi.repo.remote.model.sos;

import com.fasterxml.jackson.annotation.JsonProperty;



/**
 * Created by parikshittomar on 28-02-2019.
 */

public class SOSResponse {

    @JsonProperty("response")
    private Response response;

    @JsonProperty("status")
    private boolean status;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SOSResponse{" +
                "response=" + response +
                ", status='" + status + '\'' +
                '}';
    }
}
