package in.ecomexpress.sathi.repo.remote.model.IciciResponse;

/**
 * Created by santosh on 20/8/19.
 */

public class Request_data {
    private String lng;

    private PidData PidData;

    private String lat;

    public String getLng ()
    {
        return lng;
    }

    public void setLng (String lng)
    {
        this.lng = lng;
    }

    public PidData getPidData ()
    {
        return PidData;
    }

    public void setPidData (PidData PidData)
    {
        this.PidData = PidData;
    }

    public String getLat ()
    {
        return lat;
    }

    public void setLat (String lat)
    {
        this.lat = lat;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [lng = "+lng+", PidData = "+PidData+", lat = "+lat+"]";
    }
}
