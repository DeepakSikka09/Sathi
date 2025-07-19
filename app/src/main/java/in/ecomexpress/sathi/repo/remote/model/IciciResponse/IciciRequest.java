package in.ecomexpress.sathi.repo.remote.model.IciciResponse;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by santosh on 19/8/19.
 */

public class IciciRequest


//        @JsonProperty("request_data")
//        private String requestdata;
//
//        public IciciRequest(String requestdata) {
//            this.requestdata = requestdata;
//        }
//
//    public String getRequestdata() {
//        return requestdata;
//    }
//
//    public void setRequestdata(String requestdata) {
//        this.requestdata = requestdata;
//    }
{
    private String requesttype;

    private String order_number;

    private Request_data request_data;

    private String awb;

    public String getRequesttype ()
    {
        return requesttype;
    }

    public void setRequesttype (String requesttype)
    {
        this.requesttype = requesttype;
    }

    public String getOrder_number ()
    {
        return order_number;
    }

    public void setOrder_number (String order_number)
    {
        this.order_number = order_number;
    }

    public Request_data getRequest_data ()
    {
        return request_data;
    }

    public void setRequest_data (Request_data request_data)
    {
        this.request_data = request_data;
    }

    public String getAwb ()
    {
        return awb;
    }

    public void setAwb (String awb)
    {
        this.awb = awb;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [requesttype = "+requesttype+", order_number = "+order_number+", request_data = "+request_data+", awb = "+awb+"]";
    }
}




