package in.ecomexpress.sathi.repo.remote.model.commonrequest;

import com.fasterxml.jackson.annotation.JsonProperty;

import in.ecomexpress.sathi.repo.remote.model.sos.Response;

/**
 * Created by parikshittomar on 16-05-2019.
 */

public class LogFileUploadResponse {

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
