package in.ecomexpress.sathi.repo.remote.model.trip;

import androidx.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DrsCheckListResponse {

    private String status;

    public String getStatus () {
        return status;
    }

    public void setStatus (String status) {
        this.status = status;
    }

    @JsonProperty("response")
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "DrsCheckListResponse{" +
                "status=" + status +
                ", response=" + response +
                '}';
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {

        @JsonProperty("status_code")
        private int status_code;

        private String description;

        private String code;

        @JsonProperty("is_dc_update_allowed_for_dept")
        private boolean is_dc_update_allowed_for_dept;

        public int getStatusCode() {
            return status_code;
        }

        public void setStatusCode(int statusCode) {
            this.status_code = statusCode;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public boolean isDcUpdateAllowedForDept() {
            return is_dc_update_allowed_for_dept;
        }

        public void setDcUpdateAllowedForDept(boolean is_dc_update_allowed_for_dept) {
            this.is_dc_update_allowed_for_dept = is_dc_update_allowed_for_dept;
        }

        @NonNull
        @Override
        public String toString() {
            return "Response{" +
                    "statusCode=" + status_code +
                    ", description='" + description + '\'' +
                    ", code='" + code + '\'' +
                    ", isDcUpdateAllowedForDept=" + is_dc_update_allowed_for_dept +
                    '}';
        }
    }
}