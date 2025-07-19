package in.ecomexpress.sathi.repo.remote.model.ekyc_xml_response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)

public class ekycXMLResponse {
    @JsonProperty("e_message")
    public String message;
    @JsonProperty("e_code")
    public String statusCode;
    @JsonProperty("txn")
    public String txn;
    @JsonProperty("order_number")
    public String order_number;
    @JsonProperty("awb")
    public String awb;
    @JsonProperty("success")
    public boolean success;

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public String getAwb() {
        return awb;
    }

    public void setAwb(String awb) {
        this.awb = awb;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
//    public String getOrder_number() {
//        return order_number;
//    }
//
//    public void setOrder_number(String order_number) {
//        this.order_number = order_number;
//    }
//
//    public String getAwb() {
//        return awb;
//    }
//
//    public void setAwb(String awb) {
//        this.awb = awb;
//    }

    public String getTxn() {
        return txn;
    }

    public void setTxn(String txn) {
        this.txn = txn;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}



