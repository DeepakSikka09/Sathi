package in.ecomexpress.sathi.repo.remote.model.DCLocationUpdate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by o74884 on 21-08-2019.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DCLocationUpdate
{
    @JsonProperty("address")
    private String address;

    @JsonProperty("empCode")
    private String empCode;

    @JsonProperty("lat")
    private String lat;

    @JsonProperty("lng")
    private String lng;

    public DCLocationUpdate(String address, String code, String lat, String lng) {
        this.address=address;
        this.empCode=code;
        this.lat=lat;
        this.lng=lng;
    }

    @JsonProperty("address")
    public String getAddress ()
    {
        return address;
    }
    @JsonProperty("address")
    public void setAddress (String address)
    {
        this.address = address;
    }
    @JsonProperty("lng")
    public String getLng ()
    {
        return lng;
    }
    @JsonProperty("lng")
    public void setLng (String lng)
    {
        this.lng = lng;
    }
    @JsonProperty("empCode")
    public String getEmpCode ()
    {
        return empCode;
    }
    @JsonProperty("empCode")
    public void setEmpCode (String empCode)
    {
        this.empCode = empCode;
    }
    @JsonProperty("lat")
    public String getLat ()
    {
        return lat;
    }
    @JsonProperty("lat")
    public void setLat (String lat)
    {
        this.lat = lat;
    }

    @Override
    public String toString()
    {
        return "DCLocationUpdate [address = "+address+", lng = "+lng+", empCode = "+empCode+", lat = "+lat+"]";
    }
}

